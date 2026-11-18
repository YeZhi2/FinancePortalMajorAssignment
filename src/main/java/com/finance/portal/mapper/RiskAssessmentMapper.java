package com.finance.portal.mapper;

import com.finance.portal.entity.RiskAssessment;
import org.apache.ibatis.annotations.Param;

/**
 * 风险测评记录数据访问。
 */
public interface RiskAssessmentMapper {

    int insert(RiskAssessment assessment);

    RiskAssessment findLatestByUser(@Param("userId") Long userId);
}
