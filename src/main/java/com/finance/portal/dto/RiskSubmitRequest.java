package com.finance.portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 风险测评提交：10 道题，每题选项 0~4 分。
 */
@Data
public class RiskSubmitRequest {

    @NotEmpty(message = "请完成全部测评题目")
    @Size(min = 10, max = 10, message = "风险测评共 10 道题，请完整作答")
    private List<Integer> answers;
}
