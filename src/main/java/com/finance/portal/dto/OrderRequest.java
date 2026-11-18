package com.finance.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购买请求。
 */
@Data
public class OrderRequest {

    @NotNull(message = "请选择产品")
    private Long productId;

    @NotNull(message = "请输入投资金额")
    @DecimalMin(value = "0.01", message = "投资金额必须大于 0")
    private BigDecimal amount;
}
