package com.finance.portal.common;

/**
 * 风险等级工具：产品风险 R1~R5 与客户风险承受能力 C1~C5 的映射。
 */
public final class RiskLevels {

    private static final String[] RISK_LABELS = {"低风险", "中低风险", "中风险", "中高风险", "高风险"};
    private static final String[] CUSTOMER_LABELS = {"保守型", "稳健型", "平衡型", "成长型", "进取型"};
    private static final String[] DESCRIPTIONS = {
            "不愿承担本金损失，追求资金安全与流动性。",
            "可接受较小波动，以稳健增值为主要目标。",
            "可接受一定波动，追求收益与风险的平衡。",
            "可承受较大波动，愿意用风险换取更高收益。",
            "追求长期高收益，能承受本金较大幅度的波动。"
    };

    private RiskLevels() {
    }

    /**
     * 解析等级序号：R1/R2... 或 C1/C2...，无法识别时返回 0。
     */
    public static int indexOf(String level) {
        if (level == null || level.length() < 2) {
            return 0;
        }
        int value = level.charAt(1) - '0';
        return value >= 1 && value <= 5 ? value : 0;
    }

    /**
     * 测评得分换算为客户风险等级：0~8 分 C1，9~16 分 C2，以此类推。
     */
    public static String levelForScore(int score) {
        if (score <= 8) {
            return "C1";
        }
        if (score <= 16) {
            return "C2";
        }
        if (score <= 24) {
            return "C3";
        }
        if (score <= 32) {
            return "C4";
        }
        return "C5";
    }

    public static String riskLabel(String level) {
        int index = indexOf(level);
        return index == 0 ? "未评级" : RISK_LABELS[index - 1];
    }

    public static String customerLabel(String level) {
        int index = indexOf(level);
        return index == 0 ? "未测评" : CUSTOMER_LABELS[index - 1];
    }

    public static String description(String level) {
        int index = indexOf(level);
        return index == 0 ? "尚未完成风险测评。" : DESCRIPTIONS[index - 1];
    }
}
