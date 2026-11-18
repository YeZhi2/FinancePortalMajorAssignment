-- =============================================================
-- 金融理财产品门户 - 初始化数据脚本（演示数据）
-- 前置条件：已执行 sql/schema.sql
-- 执行方式：mysql -u root -p < sql/data.sql
-- 说明：用户与产品按唯一键幂等写入，资讯每次执行会重建。
-- =============================================================

USE finance_portal;

-- 演示账号（组内测试用）：
--   管理员      admin   / admin123
--   客户        customer / 123456
--   测试客户     tester1 ~ tester5 / test123456
INSERT INTO t_user (username, password, real_name, phone, email, role, balance, risk_level, status)
VALUES ('admin', '$2a$10$hu1IFMY9Kx2hwUm6nJFgz.tDxyFlxPxF5pIg7.HV04akFb.PczycC',
        '系统管理员', '13800000000', 'admin@finance.local', 'ADMIN', 0.00, NULL, 1),
       ('customer', '$2a$10$6Ew/hcmKE7khiNYFYZBNhuqpAYwla.bbVNm94u/LCouLGSIukrzhu',
        '张小明', '13900000000', 'customer@finance.local', 'CUSTOMER', 100000.00, NULL, 1),
       ('tester1', '$2a$10$7DnYzji4ErlGDfhdBSTiMuf2t6aglYxdxdJZhn9SOOOfntruoHfWO',
        '测试员一', '13700000001', 'tester1@finance.local', 'CUSTOMER', 100000.00, NULL, 1),
       ('tester2', '$2a$10$7DnYzji4ErlGDfhdBSTiMuf2t6aglYxdxdJZhn9SOOOfntruoHfWO',
        '测试员二', '13700000002', 'tester2@finance.local', 'CUSTOMER', 100000.00, NULL, 1),
       ('tester3', '$2a$10$7DnYzji4ErlGDfhdBSTiMuf2t6aglYxdxdJZhn9SOOOfntruoHfWO',
        '测试员三', '13700000003', 'tester3@finance.local', 'CUSTOMER', 100000.00, NULL, 1),
       ('tester4', '$2a$10$7DnYzji4ErlGDfhdBSTiMuf2t6aglYxdxdJZhn9SOOOfntruoHfWO',
        '测试员四', '13700000004', 'tester4@finance.local', 'CUSTOMER', 100000.00, NULL, 1),
       ('tester5', '$2a$10$7DnYzji4ErlGDfhdBSTiMuf2t6aglYxdxdJZhn9SOOOfntruoHfWO',
        '测试员五', '13700000005', 'tester5@finance.local', 'CUSTOMER', 100000.00, NULL, 1)
ON DUPLICATE KEY UPDATE password   = VALUES(password),
                        real_name  = VALUES(real_name),
                        role       = VALUES(role),
                        balance    = VALUES(balance),
                        status     = VALUES(status);

INSERT INTO t_product (code, name, category, risk_level, expected_return, term_days, min_amount, total_amount,
                       sold_amount, manager, description, status)
VALUES ('FP2024001', '稳盈货币宝A', '货币型', 'R1', 2.10, 0, 100.00, 50000000.00, 12800000.00, '华夏基金',
        '主要投资于银行存款、同业存单等高流动性资产，支持随时赎回，适合短期闲置资金打理。', 1),
       ('FP2024002', '安心短债30天', '债券型', 'R2', 3.15, 30, 1000.00, 20000000.00, 8600000.00, '易方达基金',
        '以短期高等级信用债为主要投资标的，30 天封闭运作，波动小、流动性较好。', 1),
       ('FP2024003', '国债精选180天', '债券型', 'R2', 3.85, 180, 5000.00, 30000000.00, 15400000.00, '招商基金',
        '重点配置国债与政策性金融债，久期适中，追求稳健的票息收益。', 1),
       ('FP2024004', '稳健增利一年定开', '混合型', 'R3', 4.60, 365, 10000.00, 50000000.00, 27600000.00, '汇添富基金',
        '债券打底、少量权益增强，一年定期开放，力争在中低波动下获取超额收益。', 1),
       ('FP2024005', '沪深300指数增强', '指数型', 'R4', 6.80, 365, 10000.00, 80000000.00, 39100000.00, '天弘基金',
        '以沪深300指数为基准，通过量化模型进行指数增强，适合具备一定风险承受能力的投资者。', 1),
       ('FP2024006', '科技创新主题股票', '股票型', 'R4', 8.50, 730, 20000.00, 100000000.00, 48600000.00, '广发基金',
        '聚焦半导体、人工智能、高端制造等科技创新方向，波动较大，建议长期持有。', 1),
       ('FP2024007', '消费升级优选混合', '混合型', 'R3', 5.20, 180, 5000.00, 40000000.00, 18200000.00, '中欧基金',
        '围绕食品饮料、家电、医美等消费升级主线，兼顾成长与估值。', 1),
       ('FP2024008', '港股通价值精选', '股票型', 'R5', 9.60, 365, 30000.00, 60000000.00, 23400000.00, '南方基金',
        '投资港股通标的中的低估值蓝筹，受汇率与国际市场影响较大，仅适合进取型投资者。', 1),
       ('FP2024009', '绿色能源指数', '指数型', 'R4', 7.40, 365, 10000.00, 50000000.00, 19800000.00, '富国基金',
        '跟踪绿色能源主题指数，覆盖光伏、储能、新能源车产业链。', 1),
       ('FP2024010', '现金管理日日盈', '货币型', 'R1', 1.95, 0, 100.00, 100000000.00, 61200000.00, '工银瑞信',
        'T+0 快速赎回的现金管理产品，适合作为账户闲钱的默认去处。', 1),
       ('FP2024011', '红利低波策略', '指数型', 'R3', 4.90, 365, 5000.00, 30000000.00, 12000000.00, '华泰柏瑞',
        '以高股息、低波动个股为样本，兼顾分红收益与回撤控制。（已下架，用于管理端演示）', 0)
ON DUPLICATE KEY UPDATE name           = VALUES(name),
                        category       = VALUES(category),
                        risk_level     = VALUES(risk_level),
                        expected_return= VALUES(expected_return),
                        term_days      = VALUES(term_days),
                        min_amount     = VALUES(min_amount),
                        total_amount   = VALUES(total_amount),
                        manager        = VALUES(manager),
                        description    = VALUES(description),
                        status         = VALUES(status);

DELETE FROM t_article;

INSERT INTO t_article (title, category, summary, content, publish_date, status)
VALUES ('央行公开市场操作持续加力，短端利率保持平稳', '市场解读',
        '本周央行通过逆回购与买断式回购投放流动性，资金面整体宽松，短端收益率小幅下行。',
        '本周央行公开市场操作净投放规模扩大，跨月资金面保持平稳。受此影响，1 年期国债收益率下行约 3BP，货币型与短债型产品收益相对稳定。对于以流动性管理为目标的投资者，可优先关注货币型与 30 天短债产品。',
        '2026-11-17', 1),
       ('如何看懂产品的“风险等级”？', '投教',
        'R1 到 R5 并不是收益排序，而是本金损失可能性的分级，购买前请先完成风险测评。',
        '产品的风险等级反映的是净值波动与本金损失的可能性，不等同于收益高低。R1、R2 主要投资于存款与高等级债券；R3 起会配置权益资产；R4、R5 的净值波动明显放大。投资者应先完成风险测评获得 C1~C5 的承受能力等级，再选择不高于自身等级的产品。',
        '2026-11-15', 1),
       ('平台上线风险测评与持仓功能说明', '公告',
        '门户已支持在线风险测评、产品购买、持仓与赎回全流程，欢迎体验。',
        '本次更新上线了风险测评问卷（10 题，得分 0~40 分对应 C1~C5）、产品购买与持仓查询、订单赎回等功能。测评结论会同步至账户，购买高于自身风险等级的产品将被系统拦截。如遇问题请通过页面底部联系方式反馈。',
        '2026-11-12', 1),
       ('消费与科技双主线下的配置思路', '市场解读',
        '在盈利预期改善的背景下，均衡配置消费与科技板块成为不少组合的选择。',
        '近期消费数据边际改善，科技板块估值有所回落。对于风险承受能力在 C3 及以上的投资者，可考虑以混合型产品作为底仓，搭配一定比例的指数型产品；C1、C2 投资者仍建议以债券型产品为主，避免追高。',
        '2026-11-08', 1);
