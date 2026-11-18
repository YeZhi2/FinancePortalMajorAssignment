package com.finance.portal.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品新增 / 编辑表单。
 */
@Data
public class ProductForm {

    /** 新增时留空表示自动生成。 */
    private String code;

    @NotBlank(message = "请输入产品名称")
    @Size(max = 100, message = "产品名称不能超过 100 个字符")
    private String name;

    @NotBlank(message = "请选择产品类型")
    private String category;

    @NotBlank(message = "请选择风险等级")
    @Pattern(regexp = "^R[1-5]$", message = "风险等级只能是 R1~R5")
    private String riskLevel;

    @NotNull(message = "请输入预期年化收益率")
    @DecimalMin(value = "0", message = "收益率不能为负数")
    @DecimalMax(value = "100", message = "收益率不能超过 100%")
    private BigDecimal expectedReturn;

    @NotNull(message = "请输入投资期限")
    @Min(value = 0, message = "期限不能为负数")
    private Integer termDays;

    @NotNull(message = "请输入起投金额")
    @DecimalMin(value = "0.01", message = "起投金额必须大于 0")
    private BigDecimal minAmount;

    @NotNull(message = "请输入募集总额")
    @DecimalMin(value = "1", message = "募集总额必须大于 0")
    private BigDecimal totalAmount;

    @Size(max = 50, message = "管理人名称不能超过 50 个字符")
    private String manager;

    @Size(max = 1000, message = "产品说明不能超过 1000 个字符")
    private String description;

    /** 1 上架，0 下架。 */
    private Integer status = 1;
}
