package com.finance.portal.service;

import com.finance.portal.common.BizException;
import com.finance.portal.common.RiskLevels;
import com.finance.portal.entity.RiskAssessment;
import com.finance.portal.mapper.RiskAssessmentMapper;
import com.finance.portal.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 风险测评业务：计算得分、写入结论并回写用户风险等级。
 */
@Service
public class RiskService {

    private static final int QUESTION_COUNT = 10;
    private static final int OPTION_MAX = 4;

    private final RiskAssessmentMapper riskAssessmentMapper;
    private final UserMapper userMapper;

    public RiskService(RiskAssessmentMapper riskAssessmentMapper, UserMapper userMapper) {
        this.riskAssessmentMapper = riskAssessmentMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public Map<String, Object> submit(Long userId, List<Integer> answers) {
        if (answers == null || answers.size() != QUESTION_COUNT) {
            throw new BizException("风险测评共 " + QUESTION_COUNT + " 道题，请完整作答");
        }
        for (Integer answer : answers) {
            if (answer == null || answer < 0 || answer > OPTION_MAX) {
                throw new BizException("测评选项取值不合法");
            }
        }
        int score = answers.stream().mapToInt(Integer::intValue).sum();
        String level = RiskLevels.levelForScore(score);

        RiskAssessment assessment = new RiskAssessment();
        assessment.setUserId(userId);
        assessment.setScore(score);
        assessment.setRiskLevel(level);
        assessment.setAnswers(answers.stream().map(String::valueOf).collect(Collectors.joining(",")));
        riskAssessmentMapper.insert(assessment);
        userMapper.updateRiskLevel(userId, level);

        return describe(score, level);
    }

    public Map<String, Object> latest(Long userId) {
        RiskAssessment assessment = riskAssessmentMapper.findLatestByUser(userId);
        if (assessment == null) {
            return Map.of("assessed", false);
        }
        Map<String, Object> result = new LinkedHashMap<>(describe(assessment.getScore(), assessment.getRiskLevel()));
        result.put("assessed", true);
        result.put("createdAt", assessment.getCreatedAt());
        return result;
    }

    private Map<String, Object> describe(int score, String level) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("score", score);
        result.put("riskLevel", level);
        result.put("customerLabel", RiskLevels.customerLabel(level));
        result.put("description", RiskLevels.description(level));
        return result;
    }
}
