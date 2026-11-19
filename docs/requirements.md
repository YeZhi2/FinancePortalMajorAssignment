# 金融理财产品门户 · 需求文档

> 版本：v1.1　更新日期：2026-11-19
> 本文档的每条功能（F-xx）与接口（API-xx）都可在代码中逐一对照，对照关系见第 9 节。

## 1. 项目概述

本项目是一个面向个人投资者的**金融理财产品门户**，提供理财产品展示、筛选与对比、风险承受能力测评、产品购买与持仓管理、赎回兑付，以及面向运营人员的后台管理能力。

项目定位为**单组独立完成的教学演示系统**：不接入真实支付与真实行情，账户资金为模拟资金（注册赠送 100,000 元），收益按"本金 × 年化收益率 × 期限 / 365"静态估算，用于完整演练"需求 → 设计 → 编码 → 测试"的工程流程。

技术栈按要求采用 **Spring Boot 3 + MyBatis + MySQL 8**，前端为同源静态页面（HTML/CSS/原生 JS，由 Spring Boot 直接托管），无需额外构建。

## 2. 用户角色与权限

| 角色 | 说明 | 权限范围 |
| --- | --- | --- |
| 游客（未登录） | 未注册或未登录的访问者 | 浏览首页、产品列表、产品详情、市场资讯；对比在售产品；注册、登录 |
| 客户 CUSTOMER | 注册投资者 | 游客全部权限 + 风险测评、购买产品、查看持仓与订单、赎回 |
| 管理员 ADMIN | 平台运营人员 | 游客全部权限 + 数据概览、产品增删改查与上下架、订单查询、用户启用/禁用 |

权限实现方式：登录成功后把用户信息写入 `HttpSession`（键 `LOGIN_USER`，只含 id/username/realName/role）；`AuthInterceptor` 对 `/api/account/**`、`/api/orders/**`、`/api/risk/**`、`/api/admin/**` 做登录校验，其中 `/api/admin/**` 额外校验 `role=ADMIN`。未登录返回 401，角色不足返回 403。

## 3. 功能清单

### 3.1 门户与产品

| 编号 | 功能 | 需求说明 |
| --- | --- | --- |
| F-01 | 门户首页 | 展示平台统计（在售产品数、资讯数量）、热门产品（按预期收益率取前 6）、最新资讯 |
| F-02 | 产品列表 | 支持关键字（名称/代码）、产品类型、风险等级筛选，支持收益率降序/期限升序/起投金额升序排序，分页展示；只展示上架产品 |
| F-03 | 产品详情 | 展示收益率、期限、起投金额、募集总额、剩余额度、风险等级、管理人、产品说明；提供购买入口 |
| F-04 | 市场资讯 | 按发布日期倒序展示市场解读、投教、公告类资讯 |
| F-05 | 热门产品接口 | 供首页使用，按预期收益率降序返回指定条数 |
| F-21 | 产品对比 | 产品中心选取 2–3 款不同产品，按列查看产品类型、风险等级、预期年化收益率、期限、起投金额与管理人，并可打开各产品详情 |

### 3.2 账户与风控

| 编号 | 功能 | 需求说明 |
| --- | --- | --- |
| F-06 | 注册 | 用户名 3~20 位字母/数字/下划线且唯一；密码 6~32 位并以 BCrypt 存储；赠送 100,000 元模拟资金；手机号、邮箱选填并做格式校验 |
| F-07 | 登录 / 登出 / 当前用户 | 登录校验密码与账号状态，写入会话；登出销毁会话；`/me` 返回当前登录用户（未登录返回空） |
| F-08 | 风险测评 | 10 道单选题，每题 0~4 分，合计 0~40 分；按区间映射 C1~C5；结果写入测评记录表并回写用户风险等级；可重复测评，以最新一次为准 |
| F-09 | 账户总览 | 返回余额、持仓本金合计、持仓预期收益合计、订单笔数、当前风险等级与中文释义 |

### 3.3 交易

| 编号 | 功能 | 需求说明 |
| --- | --- | --- |
| F-10 | 购买产品 | 校验顺序：登录 → 产品存在且上架 → 已完成测评 → 产品风险等级 ≤ 客户风险等级 → 金额 ≥ 起投金额 → 余额充足 → 产品剩余额度充足；通过后扣减余额、占用额度、生成订单（PAID）与持仓 |
| F-11 | 我的订单 | 分页查询本人订单，含产品名称、金额、预期收益、状态、下单时间 |
| F-12 | 我的持仓 | 按"用户 + 产品"聚合展示持仓本金、份额、预期收益、买入日、到期日、状态 |
| F-13 | 赎回 | 仅本人持有中订单可赎回；按"本金 + 预期收益"模拟兑付回账户余额；订单置为 REDEEMED；持仓扣减至 0 时删除记录；重复赎回被拦截 |

### 3.4 管理端

| 编号 | 功能 | 需求说明 |
| --- | --- | --- |
| F-14 | 数据概览 | 产品总数、注册用户数、订单总数、持有中订单金额、持仓笔数 |
| F-15 | 产品管理 | 分页查询（含下架产品）、新增、编辑、删除、上架/下架；产品代码留空自动生成并保证唯一；已有交易记录的产品禁止删除，只能下架 |
| F-16 | 订单管理 | 分页查询全平台订单，支持按状态与关键字（订单号/客户名/产品名）过滤 |
| F-17 | 用户管理 | 分页查询用户，启用/禁用账号；禁用后该账号无法登录 |

### 3.5 通用能力

| 编号 | 功能 | 需求说明 |
| --- | --- | --- |
| F-18 | 统一响应 | 所有接口返回 `{code, message, data}`，`code=0` 表示成功 |
| F-19 | 统一异常处理 | 业务异常按 400/401/403/404 返回并带可读提示；参数校验失败返回首条校验信息；未知异常返回 500 且不泄露堆栈 |
| F-20 | 参数校验 | 请求体使用 Jakarta Validation 注解校验，错误信息为中文 |

## 4. 核心业务流程

### 4.1 客户购买流程

```
登录 → 风险测评（未测评则被拦截）
     → 浏览/筛选产品 → 按需对比 → 查看详情
     → 输入投资金额 → 提交购买
         ├─ 产品下架/不存在        → 400 产品不存在或已下架
         ├─ 未完成风险测评          → 400 请先完成风险测评
         ├─ 产品风险 > 客户承受能力 → 400 风险等级不匹配
         ├─ 金额 < 起投金额         → 400 金额不足起投
         ├─ 余额不足                → 400 余额不足
         ├─ 剩余额度不足            → 400 额度不足（同时回滚已扣款）
         └─ 校验通过 → 扣余额 + 占额度 + 生成订单 + 累加持仓
```

说明：扣款与占用额度均使用带条件的 `UPDATE`（`balance >= amount`、`sold_amount + amount <= total_amount`），返回受影响行数为 0 即视为不满足条件，并在同一事务中抛出异常回滚，避免并发场景下超卖或负余额。

### 4.2 风险测评流程

1. 客户作答 10 题，前端收集 10 个 0~4 的选项索引；
2. 服务端校验题数与取值范围，计算总分；
3. 总分映射：`0-8 → C1`、`9-16 → C2`、`17-24 → C3`、`25-32 → C4`、`33-40 → C5`；
4. 写入 `t_risk_assessment`，并把 `t_user.risk_level` 更新为本次结论；
5. 返回得分、等级、中文释义，前端展示可购买范围。

### 4.3 赎回流程

1. 校验订单归属与状态（仅 PAID 可赎回）；
2. 按"本金 + 预期收益"增加账户余额；
3. 订单状态置为 REDEEMED；
4. 持仓金额/份额/预期收益同步扣减，扣减后本金小于 0.01 元时删除持仓记录；
5. 整个过程在同一事务内完成。

### 4.4 管理端产品维护流程

1. 新建：必填项校验 → 产品代码唯一性校验（留空则自动生成）→ 落库，`sold_amount` 初始化为 0；
2. 编辑：允许修改名称、类型、风险等级、收益率、期限、起投金额、募集总额、管理人、说明与状态；产品代码可与自身相同；
3. 下架：`status=0`，门户端立即不可见（仍可按 ID 查详情）；
4. 删除：若存在订单记录则拒绝删除，提示改为下架。

### 4.5 产品对比流程

1. 在产品中心点击卡片上的「对比」。可跨筛选、排序和分页选取，最多同时选 3 款；重复点击取消选择，也可在对比栏移除单款或清空。
2. 至少选 2 款才可打开对比表；窄屏下可横向滚动查看全部产品列。表格提供详情入口，并标明预期收益不等于实际收益。
3. 对比使用产品列表已加载的数据，不要求登录，不新增接口或数据表；选中项仅保存在当前页面内存，刷新后清空。购买时仍按原流程重新校验。

## 5. 页面清单

| 页面 | 文件 | 主要功能 | 使用接口 |
| --- | --- | --- | --- |
| 首页 | `index.html` | 平台统计、热门产品、市场资讯 | API-01/02/04 |
| 产品中心 | `products.html` | 筛选、排序、分页浏览产品，选取 2–3 款进行对比 | API-01 |
| 产品详情 | `detail.html?id=` | 产品信息、购买表单、收益试算 | API-03、API-08 |
| 登录 | `login.html` | 账号登录、按角色跳转 | API-06 |
| 注册 | `register.html` | 注册并自动登录 | API-05 |
| 风险测评 | `risk.html` | 10 题问卷、结果展示、历史结论 | API-10/11 |
| 我的账户 | `portfolio.html` | 账户总览、持仓、订单、赎回 | API-13/14/12/15 |
| 管理后台 | `admin.html` | 概览、产品 CRUD、订单、用户管理 | API-16 ~ API-25 |

前端为原生 JS + `fetch`，通过 `js/api.js` 统一处理请求与错误（401 自动跳转登录页），`js/layout.js` 统一渲染带登录态的导航与页脚。

全站配色参考 `demo1115`：深松绿用于导航和主按钮，亮黄绿用于强调操作，浅绿灰用于页面与次级表面，白色用于卡片；风险徽章保留等级区分，错误继续使用红色。共享色板定义在 `css/style.css`。

## 6. 接口清单

统一前缀 `/api`，除标注"公开"外均需登录；`/api/admin/**` 需管理员。

| 编号 | 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- | --- |
| API-01 | GET | `/api/products` | 产品分页查询，参数 `keyword/category/riskLevel/sort/page/size` | 公开 |
| API-02 | GET | `/api/products/hot` | 热门产品，参数 `limit`（默认 6） | 公开 |
| API-03 | GET | `/api/products/{id}` | 产品详情 | 公开 |
| API-04 | GET | `/api/articles` | 资讯列表，参数 `limit`（可选） | 公开 |
| API-05 | POST | `/api/auth/register` | 注册并建立会话 | 公开 |
| API-06 | POST | `/api/auth/login` | 登录 | 公开 |
| API-07 | POST | `/api/auth/logout` | 登出 | 公开 |
| API-08 | POST | `/api/orders` | 购买产品，body `{productId, amount}` | 客户 |
| API-09 | GET | `/api/auth/me` | 当前登录用户 | 公开 |
| API-10 | POST | `/api/risk/assessments` | 提交测评，body `{answers:[10 个 0~4]}` | 客户 |
| API-11 | GET | `/api/risk/latest` | 最近一次测评结论 | 客户 |
| API-12 | GET | `/api/orders` | 我的订单分页，参数 `page/size` | 客户 |
| API-13 | GET | `/api/account/overview` | 账户总览 | 客户 |
| API-14 | GET | `/api/account/holdings` | 我的持仓列表 | 客户 |
| API-15 | POST | `/api/orders/{id}/redeem` | 赎回订单 | 客户 |
| API-16 | GET | `/api/admin/stats` | 平台统计 | 管理员 |
| API-17 | GET | `/api/admin/products` | 产品分页（含下架），参数 `keyword/category/page/size` | 管理员 |
| API-18 | POST | `/api/admin/products` | 新增产品 | 管理员 |
| API-19 | PUT | `/api/admin/products/{id}` | 编辑产品 | 管理员 |
| API-20 | DELETE | `/api/admin/products/{id}` | 删除产品（有订单则拒绝） | 管理员 |
| API-21 | PATCH | `/api/admin/products/{id}/status` | 上架/下架，参数 `status` | 管理员 |
| API-22 | GET | `/api/admin/orders` | 全平台订单，参数 `page/size/status/keyword` | 管理员 |
| API-23 | GET | `/api/admin/users` | 用户分页 | 管理员 |
| API-24 | PATCH | `/api/admin/users/{id}/status` | 启用/禁用用户 | 管理员 |
| API-25 | GET | `/api/orders/{id}` | 订单详情（仅本人） | 客户 |

响应示例（成功）：

```json
{ "code": 0, "message": "success", "data": { "total": 10, "page": 1, "size": 9, "pages": 2, "list": [] } }
```

响应示例（业务失败）：

```json
{ "code": 400, "message": "该产品风险等级为 R5（高风险），高于您的风险承受能力 C1（保守型），无法购买", "data": null }
```

## 7. 数据模型

| 表 | 说明 | 关键字段 |
| --- | --- | --- |
| `t_user` | 用户（客户 + 管理员） | `username` 唯一、`password`(BCrypt)、`role`、`balance`、`risk_level`(C1~C5)、`status` |
| `t_product` | 理财产品 | `code` 唯一、`category`、`risk_level`(R1~R5)、`expected_return`、`term_days`、`min_amount`、`total_amount`、`sold_amount`、`status` |
| `t_risk_assessment` | 风险测评记录 | `user_id`、`score`、`risk_level`、`answers` |
| `t_invest_order` | 投资订单 | `order_no` 唯一、`user_id`、`product_id`、`amount`、`shares`、`expected_income`、`status`(PAID/REDEEMED) |
| `t_holding` | 客户持仓 | `user_id + product_id` 唯一、`amount`、`shares`、`expected_income`、`purchase_date`、`maturity_date`、`status` |
| `t_article` | 门户资讯 | `title`、`category`、`summary`、`content`、`publish_date`、`status` |

约束：订单与持仓通过外键关联用户与产品；持仓对 `(user_id, product_id)` 建唯一索引，保证同一产品只保留一条聚合记录。

## 8. 非功能需求与约束

1. **可运行性**：本地执行两份 SQL 脚本、配置数据库连接后，`mvn spring-boot:run` 即可启动，浏览器访问 `http://localhost:8080`。
2. **安全性**：密码只存 BCrypt 摘要，任何接口不返回密码字段；会话 Cookie 设置 `HttpOnly`；所有写接口做服务端归属校验（订单只能操作自己的）。
3. **一致性**：购买与赎回使用数据库事务；扣款与额度占用采用条件更新，避免并发超卖。
4. **可读性**：按 `controller / service / mapper / entity / dto / common / config / util` 分层，接口返回统一结构，异常集中处理。
5. **演示友好**：初始化脚本提供 7 个演示账号（1 个管理员、1 个示例客户、5 个组内测试客户）与 11 个产品（含 1 个下架产品），4 条资讯。

## 9. 需求与实现对照

| 需求 | 后端实现 | 前端页面 | 接口 |
| --- | --- | --- | --- |
| F-01 | `ProductController#hot`、`ArticleController#list` | `index.html` | API-02、API-04 |
| F-02 | `ProductController#list`、`ProductService#page` | `products.html` | API-01 |
| F-03 | `ProductController#detail` | `detail.html` | API-03 |
| F-04 | `ArticleController#list`、`ArticleService#latest` | `index.html` | API-04 |
| F-05 | `ProductController#hot` | `index.html` | API-02 |
| F-06 | `AuthController#register`、`UserService#register` | `register.html` | API-05 |
| F-07 | `AuthController#login/logout/me` | `login.html`、`js/layout.js` | API-06/07/09 |
| F-08 | `RiskController`、`RiskService`、`RiskLevels` | `risk.html` | API-10/11 |
| F-09 | `AccountController#overview`、`UserService#overview` | `portfolio.html` | API-13 |
| F-10 | `OrderController#purchase`、`OrderService#purchase` | `detail.html` | API-08 |
| F-11 | `OrderController#list`、`OrderService#myOrders` | `portfolio.html` | API-12 |
| F-12 | `AccountController#holdings` | `portfolio.html` | API-14 |
| F-13 | `OrderController#redeem`、`OrderService#redeem` | `portfolio.html` | API-15 |
| F-14 | `AdminController#stats`、`OrderService#stats` | `admin.html` | API-16 |
| F-15 | `AdminController` 产品段、`ProductService` | `admin.html` | API-17~21 |
| F-16 | `AdminController#orders`、`OrderService#adminOrders` | `admin.html` | API-22 |
| F-17 | `AdminController#users`、`UserService#listUsers/updateStatus` | `admin.html` | API-23/24 |
| F-18 | `common/ApiResponse` | 全部页面 | — |
| F-19 | `common/GlobalExceptionHandler`、`common/BizException` | — | — |
| F-20 | `dto/*` 校验注解、`GlobalExceptionHandler#handleValidation` | 表单提示 | — |
| F-21 | 复用 `ProductController#list`，无需新增接口 | `products.html`、`js/product-comparison.js` | API-01 |

## 10. 需求范围说明

- 不做真实支付、实名认证、短信验证码与真实行情对接（作业约束为本地可运行的演示系统）；
- 不做多级审批、资金托管与真实收益结算，赎回按"本金 + 预期收益"一次性模拟兑付；
- 门户定位与功能范围由本组自主定义，覆盖"产品展示 → 风控测评 → 交易 → 持仓 → 后台运营"的最小闭环。
