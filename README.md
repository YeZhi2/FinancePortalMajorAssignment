# 金融理财产品门户（FinancePortalMajorAssignment）

基于 **Spring Boot 3 + MyBatis + MySQL 8** 的金融理财产品门户课程大作业：覆盖产品展示与筛选、风险承受能力测评、产品购买、持仓与赎回，以及面向运营人员的管理后台。

前端为同源静态页面（原生 HTML/CSS/JS），由 Spring Boot 直接托管，无需额外构建步骤；后端所有接口统一返回 `{code, message, data}`，并通过 Session + 拦截器完成登录与角色控制。

## 功能概览

| 模块 | 功能 |
| --- | --- |
| 门户 | 首页（统计、热门产品、资讯）、产品中心（关键字/类型/风险筛选 + 排序 + 分页）、产品详情、市场资讯 |
| 账户 | 注册（BCrypt 存储密码，赠 100,000 元模拟资金）、登录 / 登出、账户总览 |
| 风控 | 10 题风险测评，0~40 分映射 C1~C5，结论回写账户 |
| 交易 | 购买（风险匹配、起投金额、余额、剩余额度四重校验）、我的订单、我的持仓、赎回（本金 + 预期收益模拟兑付） |
| 管理 | 数据概览、产品增删改查与上下架、全平台订单查询、用户启用/禁用 |

完整的角色、业务流程、页面清单与 25 个接口定义见 [docs/requirements.md](docs/requirements.md)（第 9 节为"需求 → 代码 → 接口"对照表）。

## 技术栈

- 后端：Spring Boot 3.3.5、MyBatis 3.0.3、Spring MVC、Jakarta Validation
- 数据库：MySQL 8.0（InnoDB、utf8mb4）
- 鉴权：HttpSession + `HandlerInterceptor`，密码使用 `BCryptPasswordEncoder`
- 前端：原生 HTML5 / CSS3 / ES6（`fetch`），无第三方依赖
- 构建：Maven 3.9+、JDK 21

## 目录结构

```text
FinancePortalMajorAssignment/
├── pom.xml                       Maven 工程描述
├── README.md                     本文件（启动说明）
├── docs/
│   ├── requirements.md           需求文档（功能/角色/流程/页面/接口/数据模型）
│   ├── ai-usage.md               AI 使用记录（提示词、产出、人工验证与修改）
│   └── 金融理财产品门户大作业.docx   原始作业要求
├── sql/
│   ├── schema.sql                建库建表脚本（可重复执行，会重建表）
│   └── data.sql                  演示数据（2 个账号、11 个产品、4 条资讯）
├── scripts/
│   └── smoke-test.ps1            31 项接口冒烟测试
└── src/
    ├── main/java/com/finance/portal/
    │   ├── FinancePortalApplication.java
    │   ├── common/               统一响应、异常、常量、风险等级
    │   ├── config/               拦截器与 MVC / Bean 配置
    │   ├── controller/           7 个 REST 控制器
    │   ├── service/              业务层（用户、产品、订单、测评、资讯）
    │   ├── mapper/               MyBatis 接口
    │   ├── entity/ dto/          实体与请求对象
    │   └── util/                 会话工具、订单号生成器
    ├── main/resources/
    │   ├── application.yml       数据源与框架配置
    │   ├── mapper/*.xml          6 个 MyBatis 映射文件
    │   └── static/               前端页面（8 个 HTML + css + js）
    └── test/java/                单元测试
```

## 快速开始

### 1. 环境要求

| 组件 | 版本 |
| --- | --- |
| JDK | 21（Spring Boot 3 要求 17+） |
| Maven | 3.9+ |
| MySQL | 8.0+ |

### 2. 初始化数据库

```bash
# 1) 建库建表
mysql -u root -p --default-character-set=utf8mb4 < sql/schema.sql

# 2) 导入演示数据
mysql -u root -p --default-character-set=utf8mb4 < sql/data.sql
```

`schema.sql` 会创建 `finance_portal` 库并重建全部业务表（重复执行等于重置数据）；`data.sql` 中的用户与产品按唯一键幂等写入。

### 3. 配置数据库连接

`src/main/resources/application.yml` 默认连接 `127.0.0.1:3306/finance_portal`，用户名 `root`、密码为空，可用环境变量覆盖：

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_HOST` | `127.0.0.1` | 数据库地址 |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `finance_portal` | 数据库名 |
| `DB_USER` | `root` | 用户名 |
| `DB_PASSWORD` | 空 | 密码 |
| `SERVER_PORT` | `8080` | 应用端口 |

例如密码为 `123456`、端口为 `3307`：

```bash
# Windows PowerShell
$env:DB_PASSWORD="123456"; $env:DB_PORT="3307"; mvn spring-boot:run

# macOS / Linux
DB_PASSWORD=123456 DB_PORT=3307 mvn spring-boot:run
```

### 4. 启动应用

```bash
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package -DskipTests
java -jar target/finance-portal-1.0.0.jar
```

启动后访问 <http://localhost:8080>。首次请求会初始化连接池，日志中看到 `Started FinancePortalApplication` 即启动成功。

### 5. 演示账号

| 角色 | 用户名 | 密码 | 说明 |
| --- | --- | --- | --- |
| 管理员 | `admin` | `admin123` | 登录后导航栏出现"管理后台" |
| 客户 | `customer` | `123456` | 初始余额 100,000 元，尚未测评 |

推荐体验路径：注册新账号 → 风险测评 → 产品中心筛选 → 产品详情购买 → 我的持仓查看 → 赎回 → 用 `admin` 登录查看后台统计与订单。

## 测试

```bash
# 单元测试：风险等级映射、订单号唯一性
mvn test

# 接口冒烟测试（需应用已启动）
pwsh -File scripts/smoke-test.ps1
# 或 Windows PowerShell：
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/smoke-test.ps1
```

冒烟测试覆盖 31 项断言：匿名 401、产品查询与筛选、管理员登录与产品 CRUD、客户测评与购买赎回、越权 403、风控拦截、静态页面可访问。

## 实现要点

1. **防超卖与负余额**：扣款使用 `UPDATE ... WHERE balance >= #{amount}`，占用额度使用 `UPDATE ... WHERE sold_amount + #{amount} <= total_amount`，受影响行数为 0 时抛业务异常并在同一事务中回滚。
2. **风险匹配**：产品风险 `R1~R5` 与客户承受能力 `C1~C5` 按序号比较，客户等级低于产品等级时拒绝购买，提示中给出双方等级与中文释义。
3. **权限控制**：`AuthInterceptor` 统一处理 401/403；订单与持仓的所有查询、赎回都带 `userId` 条件，避免越权访问他人数据。
4. **统一异常**：`GlobalExceptionHandler` 将业务异常、参数校验异常、404、未知异常分别转换为规范的 HTTP 状态与响应体。
5. **前端复用**：`js/api.js` 统一请求封装与 401 跳转，`js/layout.js` 按登录态渲染导航与页脚，页面只关注自身渲染逻辑。

## 交付物对照

| 作业要求 | 本项目对应内容 |
| --- | --- |
| 需求文档（功能清单、角色、流程、页面/接口清单） | [docs/requirements.md](docs/requirements.md) |
| 代码仓库（完整源码 + 数据库脚本 + 启动说明） | 本仓库 + [sql/schema.sql](sql/schema.sql) + [sql/data.sql](sql/data.sql) + 本文档 |
| AI 使用记录（提示词、产出摘要、人工验证与修改） | [docs/ai-usage.md](docs/ai-usage.md) |

## 声明

本项目为课程作业演示系统，不接入真实支付与真实行情，账户资金与收益均为模拟数据，不构成任何投资建议。
