package com.finance.portal.util;

import com.finance.portal.common.BizException;
import com.finance.portal.common.Constants;
import com.finance.portal.common.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 从 HttpSession 中读取当前登录用户。
 */
public final class SessionHelper {

    private SessionHelper() {
    }

    public static LoginUser current(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(Constants.SESSION_USER);
        if (attribute instanceof LoginUser user) {
            return user;
        }
        throw new BizException(401, "请先登录");
    }

    public static LoginUser currentOrNull(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(Constants.SESSION_USER);
        return attribute instanceof LoginUser user ? user : null;
    }
}
