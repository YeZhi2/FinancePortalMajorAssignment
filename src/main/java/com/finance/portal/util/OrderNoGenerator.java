package com.finance.portal.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单号生成器：FP + 秒级时间戳 + 6 位自增序号。
 * 单进程内同一秒最多可生成 100 万个不重复订单号；序号起点随机，降低多实例部署时的碰撞概率。
 */
public final class OrderNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int SEQUENCE_MOD = 1_000_000;
    private static final AtomicInteger SEQUENCE = new AtomicInteger(new SecureRandom().nextInt(SEQUENCE_MOD));

    private OrderNoGenerator() {
    }

    public static String next() {
        int sequence = Math.floorMod(SEQUENCE.getAndIncrement(), SEQUENCE_MOD);
        return "FP" + LocalDateTime.now().format(FORMATTER) + String.format("%06d", sequence);
    }
}
