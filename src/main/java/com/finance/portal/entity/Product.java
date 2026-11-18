package com.finance.portal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 理财产品。
 */
@Data
public class Product {

    private Long id;

    /** 产品代码，如 FP2024001。 */
    private String code;

    private String name;

    /** 货币型 / 债券型 / 混合型 / 股票型 / 指数型。 */
    private String category;

    /** 风险等级 R1~R5。 */
    private String riskLevel;

    /** 预期年化收益率（百分比，如 3.65 表示 3.65%）。 */
    private BigDecimal expectedReturn;

    /** 投资期限（天），0 表示活期。 */
    private Integer termDays;

    /** 起投金额。 */
    private BigDecimal minAmount;

    /** 募集总额度。 */
    private BigDecimal totalAmount;

    /** 已售金额。 */
    private BigDecimal soldAmount;

    /** 产品管理人。 */
    private String manager;

    private String description;

    /** 1 上架，0 下架。 */
    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
