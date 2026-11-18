package com.finance.portal.common;

/**
 * 全局常量，避免魔法值散落在各层。
 */
public final class Constants {

    /** Session 中保存登录用户的键。 */
    public static final String SESSION_USER = "LOGIN_USER";

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_ADMIN = "ADMIN";

    /** 订单状态：已支付（持有中）/ 已赎回。 */
    public static final String ORDER_PAID = "PAID";
    public static final String ORDER_REDEEMED = "REDEEMED";

    /** 持仓状态：持有中 / 已赎回。 */
    public static final String HOLDING_HOLDING = "HOLDING";
    public static final String HOLDING_REDEEMED = "REDEEMED";

    /** 产品上架状态。 */
    public static final int PRODUCT_ON = 1;
    public static final int PRODUCT_OFF = 0;

    /** 用户状态：正常 / 禁用。 */
    public static final int USER_ENABLED = 1;
    public static final int USER_DISABLED = 0;

    private Constants() {
    }
}
