package com.finance.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户（客户 / 管理员）。
 */
@Data
public class User {

    private Long id;
    private String username;

    /** BCrypt 摘要，不对外输出。 */
    @JsonIgnore
    private String password;

    private String realName;
    private String phone;
    private String email;

    /** CUSTOMER 或 ADMIN。 */
    private String role;

    /** 账户可用余额（模拟资金）。 */
    private BigDecimal balance;

    /** 风险测评结论：C1~C5，未测评时为空。 */
    private String riskLevel;

    /** 1 正常，0 禁用。 */
    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
