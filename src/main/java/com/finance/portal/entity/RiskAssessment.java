package com.finance.portal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险测评记录。
 */
@Data
public class RiskAssessment {

    private Long id;
    private Long userId;

    /** 0~40 分。 */
    private Integer score;

    /** 测评结论 C1~C5。 */
    private String riskLevel;

    /** 各题选项，逗号分隔，便于回溯。 */
    private String answers;

    private LocalDateTime createdAt;
}
