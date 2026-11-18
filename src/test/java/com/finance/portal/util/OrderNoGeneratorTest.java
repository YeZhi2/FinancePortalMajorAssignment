package com.finance.portal.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 订单号生成器测试。
 */
class OrderNoGeneratorTest {

    @Test
    @DisplayName("订单号带 FP 前缀且长度固定")
    void orderNoHasPrefixAndLength() {
        String orderNo = OrderNoGenerator.next();
        assertTrue(orderNo.startsWith("FP"));
        assertEquals(22, orderNo.length());
        assertTrue(orderNo.matches("^FP\\d{20}$"));
    }

    @Test
    @DisplayName("连续生成 1000 个订单号不重复")
    void orderNosAreUnique() {
        Set<String> orderNos = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            orderNos.add(OrderNoGenerator.next());
        }
        assertEquals(1000, orderNos.size());
    }
}
