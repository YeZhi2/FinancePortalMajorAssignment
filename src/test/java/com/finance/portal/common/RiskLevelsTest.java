package com.finance.portal.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 风险等级映射规则测试。
 */
class RiskLevelsTest {

    @Test
    @DisplayName("测评得分按区间映射为 C1~C5")
    void scoreMapsToCustomerLevel() {
        assertEquals("C1", RiskLevels.levelForScore(0));
        assertEquals("C1", RiskLevels.levelForScore(8));
        assertEquals("C2", RiskLevels.levelForScore(9));
        assertEquals("C2", RiskLevels.levelForScore(16));
        assertEquals("C3", RiskLevels.levelForScore(17));
        assertEquals("C3", RiskLevels.levelForScore(24));
        assertEquals("C4", RiskLevels.levelForScore(25));
        assertEquals("C4", RiskLevels.levelForScore(32));
        assertEquals("C5", RiskLevels.levelForScore(33));
        assertEquals("C5", RiskLevels.levelForScore(40));
    }

    @Test
    @DisplayName("产品风险与客户等级都能解析出序号")
    void indexOfParsesPrefix() {
        assertEquals(1, RiskLevels.indexOf("R1"));
        assertEquals(5, RiskLevels.indexOf("R5"));
        assertEquals(3, RiskLevels.indexOf("C3"));
        assertEquals(0, RiskLevels.indexOf(null));
        assertEquals(0, RiskLevels.indexOf(""));
        assertEquals(0, RiskLevels.indexOf("X9"));
    }

    @Test
    @DisplayName("未测评时给出兜底文案")
    void labelsFallbackWhenUnrated() {
        assertEquals("未评级", RiskLevels.riskLabel(null));
        assertEquals("未测评", RiskLevels.customerLabel(null));
        assertEquals("中低风险", RiskLevels.riskLabel("R2"));
        assertEquals("进取型", RiskLevels.customerLabel("C5"));
    }
}
