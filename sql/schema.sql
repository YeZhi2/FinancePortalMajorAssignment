-- =============================================================
-- 金融理财产品门户 - 数据库结构脚本
-- 说明：本脚本会删除并重建所有业务表，请在空的 finance_portal 库上执行。
-- 执行方式：mysql -u root -p < sql/schema.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS finance_portal
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE finance_portal;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS t_risk_assessment;
DROP TABLE IF EXISTS t_holding;
DROP TABLE IF EXISTS t_invest_order;
DROP TABLE IF EXISTS t_article;
DROP TABLE IF EXISTS t_product;
DROP TABLE IF EXISTS t_user;
SET FOREIGN_KEY_CHECKS = 1;

-- 用户表：客户与管理员共用，通过 role 区分
CREATE TABLE t_user (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    username   VARCHAR(50)     NOT NULL COMMENT '登录名',
    password   VARCHAR(100)    NOT NULL COMMENT '密码（BCrypt 摘要）',
    real_name  VARCHAR(50)     NOT NULL COMMENT '真实姓名',
    phone      VARCHAR(20)     DEFAULT NULL COMMENT '手机号',
    email      VARCHAR(100)    DEFAULT NULL COMMENT '邮箱',
    role       VARCHAR(20)     NOT NULL DEFAULT 'CUSTOMER' COMMENT '角色：CUSTOMER/ADMIN',
    balance    DECIMAL(18, 2)  NOT NULL DEFAULT 0.00 COMMENT '账户余额（模拟资金）',
    risk_level VARCHAR(10)     DEFAULT NULL COMMENT '客户风险等级 C1~C5',
    status     TINYINT         NOT NULL DEFAULT 1 COMMENT '1 正常，0 禁用',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户';

-- 理财产品表
CREATE TABLE t_product (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    code            VARCHAR(32)     NOT NULL COMMENT '产品代码',
    name            VARCHAR(100)    NOT NULL COMMENT '产品名称',
    category        VARCHAR(32)     NOT NULL COMMENT '产品类型：货币型/债券型/混合型/股票型/指数型',
    risk_level      VARCHAR(10)     NOT NULL COMMENT '风险等级 R1~R5',
    expected_return DECIMAL(6, 2)   NOT NULL COMMENT '预期年化收益率（%）',
    term_days       INT             NOT NULL DEFAULT 0 COMMENT '投资期限（天），0 表示活期',
    min_amount      DECIMAL(18, 2)  NOT NULL COMMENT '起投金额',
    total_amount    DECIMAL(18, 2)  NOT NULL COMMENT '募集总额度',
    sold_amount     DECIMAL(18, 2)  NOT NULL DEFAULT 0.00 COMMENT '已售金额',
    manager         VARCHAR(50)     DEFAULT NULL COMMENT '产品管理人',
    description     VARCHAR(1000)   DEFAULT NULL COMMENT '产品说明',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '1 上架，0 下架',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (code),
    KEY idx_product_category (category),
    KEY idx_product_risk (risk_level),
    KEY idx_product_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='理财产品';

-- 风险测评记录表
CREATE TABLE t_risk_assessment (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
    score      INT             NOT NULL COMMENT '测评得分（0~40）',
    risk_level VARCHAR(10)     NOT NULL COMMENT '结论等级 C1~C5',
    answers    VARCHAR(200)    NOT NULL COMMENT '各题选项，逗号分隔',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_risk_user (user_id),
    CONSTRAINT fk_risk_user FOREIGN KEY (user_id) REFERENCES t_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='风险测评记录';

-- 投资订单表
CREATE TABLE t_invest_order (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no        VARCHAR(32)     NOT NULL COMMENT '订单号',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
    product_id      BIGINT UNSIGNED NOT NULL COMMENT '产品 ID',
    amount          DECIMAL(18, 2)  NOT NULL COMMENT '投资金额',
    shares          DECIMAL(18, 4)  NOT NULL COMMENT '份额',
    net_value       DECIMAL(10, 4)  NOT NULL DEFAULT 1.0000 COMMENT '成交净值',
    expected_income DECIMAL(18, 2)  NOT NULL DEFAULT 0.00 COMMENT '预期收益',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PAID' COMMENT 'PAID 持有中 / REDEEMED 已赎回',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user (user_id),
    KEY idx_order_product (product_id),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES t_user (id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES t_product (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='投资订单';

-- 持仓表：按用户 + 产品聚合
CREATE TABLE t_holding (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
    product_id      BIGINT UNSIGNED NOT NULL COMMENT '产品 ID',
    shares          DECIMAL(18, 4)  NOT NULL COMMENT '持有份额',
    amount          DECIMAL(18, 2)  NOT NULL COMMENT '持仓本金',
    expected_income DECIMAL(18, 2)  NOT NULL DEFAULT 0.00 COMMENT '预期收益',
    purchase_date   DATE            NOT NULL COMMENT '首次买入日期',
    maturity_date   DATE            DEFAULT NULL COMMENT '到期日，活期为 NULL',
    status          VARCHAR(20)     NOT NULL DEFAULT 'HOLDING' COMMENT 'HOLDING 持有中 / REDEEMED 已赎回',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_holding_user_product (user_id, product_id),
    CONSTRAINT fk_holding_user FOREIGN KEY (user_id) REFERENCES t_user (id),
    CONSTRAINT fk_holding_product FOREIGN KEY (product_id) REFERENCES t_product (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='客户持仓';

-- 门户资讯表
CREATE TABLE t_article (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    title        VARCHAR(200)    NOT NULL COMMENT '标题',
    category     VARCHAR(32)     NOT NULL COMMENT '分类：市场解读/投教/公告',
    summary      VARCHAR(500)    DEFAULT NULL COMMENT '摘要',
    content      TEXT            COMMENT '正文',
    publish_date DATE            NOT NULL COMMENT '发布日期',
    status       TINYINT         NOT NULL DEFAULT 1 COMMENT '1 发布，0 隐藏',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_article_publish (publish_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='门户资讯';
