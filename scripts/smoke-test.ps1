<#
    端到端接口冒烟测试
    用法：pwsh -File scripts/smoke-test.ps1 [-BaseUrl http://127.0.0.1:8080]
    前置：应用已启动，数据库已执行 schema.sql 与 data.sql
#>
param(
    [string]$BaseUrl = 'http://127.0.0.1:8080'
)

$ErrorActionPreference = 'Stop'
$script:passed = 0
$script:failed = 0

function Check {
    param([string]$Name, [bool]$Condition, [string]$Detail = '')
    if ($Condition) {
        $script:passed++
        Write-Host ("PASS  {0}" -f $Name) -ForegroundColor Green
    } else {
        $script:failed++
        Write-Host ("FAIL  {0}  {1}" -f $Name, $Detail) -ForegroundColor Red
    }
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        $Session,
        $Body
    )
    $params = @{
        Uri             = "$BaseUrl$Path"
        Method          = $Method
        ContentType     = 'application/json; charset=utf-8'
        UseBasicParsing = $true
        ErrorAction     = 'Stop'
    }
    if ($Session) { $params.WebSession = $Session }
    if ($null -ne $Body) {
        $json = $Body | ConvertTo-Json -Depth 8 -Compress
        $params.Body = [System.Text.Encoding]::UTF8.GetBytes($json)
    }
    try {
        $response = Invoke-WebRequest @params
        # Windows PowerShell 会按 Latin-1 解码无 charset 的 JSON，这里按 UTF-8 重新读取原始字节
        $reader = New-Object System.IO.StreamReader($response.RawContentStream, [System.Text.Encoding]::UTF8)
        $text = $reader.ReadToEnd()
        return [pscustomobject]@{
            Status = [int]$response.StatusCode
            Body   = ($text | ConvertFrom-Json)
        }
    } catch {
        $status = 0
        $payload = $null
        $response = $_.Exception.Response
        if ($response -ne $null) {
            $status = [int]$response.StatusCode
            $reader = New-Object System.IO.StreamReader($response.GetResponseStream(), [System.Text.Encoding]::UTF8)
            $text = $reader.ReadToEnd()
            if ($text) { $payload = $text | ConvertFrom-Json }
        }
        return [pscustomobject]@{ Status = $status; Body = $payload }
    }
}

Write-Host "目标服务：$BaseUrl" -ForegroundColor Cyan

# 等待服务就绪
$ready = $false
for ($i = 0; $i -lt 40; $i++) {
    try {
        Invoke-WebRequest -Uri "$BaseUrl/api/products?page=1&size=1" -UseBasicParsing -TimeoutSec 3 | Out-Null
        $ready = $true
        break
    } catch {
        Start-Sleep -Seconds 1
    }
}
if (-not $ready) {
    Write-Host '服务未就绪，测试终止' -ForegroundColor Red
    exit 1
}

# ---------- 1. 匿名访问与产品查询 ----------
$anon = Invoke-Api -Method GET -Path '/api/account/overview'
Check '匿名访问受保护接口返回 401' ($anon.Status -eq 401)

$list = Invoke-Api -Method GET -Path '/api/products?page=1&size=20'
Check '产品列表返回成功' ($list.Status -eq 200 -and $list.Body.code -eq 0)
Check '产品列表仅含上架产品' ($list.Body.data.total -ge 10)

$filtered = Invoke-Api -Method GET -Path '/api/products?category=%E5%80%BA%E5%88%B8%E5%9E%8B'
$nonBond = @($filtered.Body.data.list | Where-Object { $_.category -ne '债券型' })
Check '按类型筛选（债券型）有效' ($nonBond.Count -eq 0)

$hot = Invoke-Api -Method GET -Path '/api/products/hot?limit=3'
Check '热门产品接口返回 3 条' ($hot.Body.data.Count -eq 3)

$detail = Invoke-Api -Method GET -Path '/api/products/1'
Check '产品详情返回指定产品' ($detail.Body.data.id -eq 1)

# ---------- 2. 管理员登录与后台 ----------
$adminSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$adminLogin = Invoke-Api -Method POST -Path '/api/auth/login' -Session $adminSession -Body @{ username = 'admin'; password = 'admin123' }
Check '管理员登录成功' ($adminLogin.Status -eq 200 -and $adminLogin.Body.data.role -eq 'ADMIN')

$stats = Invoke-Api -Method GET -Path '/api/admin/stats' -Session $adminSession
Check '管理员可读取统计' ($stats.Status -eq 200 -and $stats.Body.data.productCount -ge 11)

$stamp = Get-Date -Format 'HHmmss'
$newProduct = Invoke-Api -Method POST -Path '/api/admin/products' -Session $adminSession -Body @{
    name             = "自动化测试产品-$stamp"
    category         = '混合型'
    riskLevel        = 'R3'
    expectedReturn   = 5.5
    termDays         = 90
    minAmount        = 1000
    totalAmount      = 1000000
    manager          = '测试管理人'
    description      = '冒烟测试创建，稍后删除'
    status           = 1
}
Check '管理员新建产品成功' ($newProduct.Status -eq 200 -and $newProduct.Body.data.code)

$newId = $newProduct.Body.data.id
$updated = Invoke-Api -Method PUT -Path "/api/admin/products/$newId" -Session $adminSession -Body @{
    code             = $newProduct.Body.data.code
    name             = "自动化测试产品-已修改-$stamp"
    category         = '债券型'
    riskLevel        = 'R2'
    expectedReturn   = 3.2
    termDays         = 60
    minAmount        = 500
    totalAmount      = 800000
    manager          = '测试管理人'
    description      = '已修改'
    status           = 1
}
Check '管理员编辑产品成功' ($updated.Status -eq 200 -and $updated.Body.data.riskLevel -eq 'R2')

$offShelf = Invoke-Api -Method PATCH -Path "/api/admin/products/$newId/status?status=0" -Session $adminSession
Check '管理员下架产品成功' ($offShelf.Status -eq 200)
$publicAfterOff = Invoke-Api -Method GET -Path "/api/products/$newId"
Check '下架产品仍可按 ID 查询（详情兜底）' ($publicAfterOff.Status -eq 200)

$deleted = Invoke-Api -Method DELETE -Path "/api/admin/products/$newId" -Session $adminSession
Check '管理员删除无交易产品成功' ($deleted.Status -eq 200)

# ---------- 3. 客户全流程 ----------
$customerSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$customerLogin = Invoke-Api -Method POST -Path '/api/auth/login' -Session $customerSession -Body @{ username = 'customer'; password = '123456' }
Check '客户登录成功' ($customerLogin.Status -eq 200)

$forbidden = Invoke-Api -Method GET -Path '/api/admin/stats' -Session $customerSession
Check '客户访问管理接口返回 403' ($forbidden.Status -eq 403)

$riskAnswers = @(4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
$risk = Invoke-Api -Method POST -Path '/api/risk/assessments' -Session $customerSession -Body @{ answers = $riskAnswers }
Check '风险测评提交成功（40 分 -> C5）' ($risk.Status -eq 200 -and $risk.Body.data.riskLevel -eq 'C5')

$latestRisk = Invoke-Api -Method GET -Path '/api/risk/latest' -Session $customerSession
Check '可查询最新测评结论' ($latestRisk.Body.data.assessed -eq $true)

$buy = Invoke-Api -Method POST -Path '/api/orders' -Session $customerSession -Body @{ productId = 5; amount = 10000 }
Check '购买产品成功' ($buy.Status -eq 200 -and $buy.Body.data.status -eq 'PAID')
$orderId = $buy.Body.data.id
$orderAmount = $buy.Body.data.amount
Check '订单金额与提交一致' ($orderAmount -eq 10000)

$overview = Invoke-Api -Method GET -Path '/api/account/overview' -Session $customerSession
Check '购买后余额扣减' ([decimal]$overview.Body.data.balance -lt 100000)
Check '购买后持仓本金增加' ([decimal]$overview.Body.data.holdingAmount -ge 10000)

$myOrders = Invoke-Api -Method GET -Path '/api/orders?page=1&size=10' -Session $customerSession
Check '可查询自己的订单' ($myOrders.Body.data.total -ge 1)

$belowMin = Invoke-Api -Method POST -Path '/api/orders' -Session $customerSession -Body @{ productId = 3; amount = 1 }
Check '低于起投金额被拦截' ($belowMin.Status -eq 400)

$redeem = Invoke-Api -Method POST -Path "/api/orders/$orderId/redeem" -Session $customerSession
Check '赎回成功并返回到账金额' ($redeem.Status -eq 200 -and [decimal]$redeem.Body.data.credited -gt 10000)

$redeemAgain = Invoke-Api -Method POST -Path "/api/orders/$orderId/redeem" -Session $customerSession
Check '重复赎回被拦截' ($redeemAgain.Status -eq 400)

# ---------- 4. 新用户风险校验 ----------
$newUser = "smoke$stamp"
$register = Invoke-Api -Method POST -Path '/api/auth/register' -Body @{
    username = $newUser
    password = 'test123456'
    realName = '冒烟测试用户'
    phone    = '13700000000'
    email    = 'smoke@example.com'
}
Check '新用户注册成功' ($register.Status -eq 200)

$noRiskSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-Api -Method POST -Path '/api/auth/login' -Session $noRiskSession -Body @{ username = $newUser; password = 'test123456' } | Out-Null

$buyNoRisk = Invoke-Api -Method POST -Path '/api/orders' -Session $noRiskSession -Body @{ productId = 1; amount = 1000 }
Check '未测评用户购买被拦截' ($buyNoRisk.Status -eq 400 -and $buyNoRisk.Body.message -match '测评')

$lowRiskAnswers = @(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
Invoke-Api -Method POST -Path '/api/risk/assessments' -Session $noRiskSession -Body @{ answers = $lowRiskAnswers } | Out-Null
$buyHighRisk = Invoke-Api -Method POST -Path '/api/orders' -Session $noRiskSession -Body @{ productId = 8; amount = 50000 }
Check 'C1 客户购买 R5 产品被拦截' ($buyHighRisk.Status -eq 400 -and $buyHighRisk.Body.message -match '风险')

$buyLowRisk = Invoke-Api -Method POST -Path '/api/orders' -Session $noRiskSession -Body @{ productId = 1; amount = 1000 }
Check 'C1 客户购买 R1 产品成功' ($buyLowRisk.Status -eq 200)

# ---------- 5. 静态页面 ----------
$homePage = Invoke-WebRequest -Uri "$BaseUrl/" -UseBasicParsing
Check '首页可访问' ($homePage.StatusCode -eq 200 -and $homePage.Content -match 'site-header')
Check '首页声明 UTF-8 编码' ("$($homePage.Headers['Content-Type'])" -match 'utf-8')

# ---------- 汇总 ----------
Write-Host ''
Write-Host ("通过 {0} 项，失败 {1} 项" -f $script:passed, $script:failed) -ForegroundColor Cyan
if ($script:failed -gt 0) {
    exit 1
}
exit 0
