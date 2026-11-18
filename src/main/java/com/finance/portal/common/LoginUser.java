package com.finance.portal.common;

/**
 * 登录态用户信息，存入 HttpSession，不包含密码等敏感字段。
 */
public record LoginUser(Long id, String username, String realName, String role) {
}
