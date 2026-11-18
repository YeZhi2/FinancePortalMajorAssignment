package com.finance.portal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户持仓，按“用户 + 产品”聚合。
 */
@Data
public class Holding {

    private Long id;
    private Long userId;
    private Long productId;

    /** 关联查询字段，非数据库列。 */
    private String productName;
    private String category;
    private String riskLevel;
    private BigDecimal expectedReturn;
    private Integer termDays;

    private BigDecimal shares;

    /** 持仓本金。 */
    private BigDecimal amount;

    /** 预期收益。 */
    private BigDecimal expectedIncome;

    private LocalDate purchaseDate;
    private LocalDate maturityDate;

    /** HOLDING 持有中 / REDEEMED 已赎回。 */
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
