package com.finance.portal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投资订单。
 */
@Data
public class InvestOrder {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;

    /** 关联查询字段，非数据库列。 */
    private String productName;

    /** 关联查询字段，管理端订单列表展示客户名。 */
    private String username;

    /** 投资金额。 */
    private BigDecimal amount;

    /** 份额（本系统按净值 1.0000 简化处理，份额等于金额）。 */
    private BigDecimal shares;

    /** 成交净值。 */
    private BigDecimal netValue;

    /** 预期收益。 */
    private BigDecimal expectedIncome;

    /** PAID 持有中 / REDEEMED 已赎回。 */
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
