package com.finance.portal.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户账户总览。
 */
@Data
public class AccountOverview {

    private String username;
    private String realName;
    private String riskLevel;
    private String riskLabel;

    /** 可用余额。 */
    private BigDecimal balance;

    /** 持仓本金合计。 */
    private BigDecimal holdingAmount;

    /** 持仓预期收益合计。 */
    private BigDecimal expectedIncome;

    /** 订单数量。 */
    private long orderCount;
}
