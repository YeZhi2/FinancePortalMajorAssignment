package com.finance.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.Constants;
import com.finance.portal.common.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 登录与角色拦截器：受保护接口要求已登录，/api/admin/** 额外要求管理员角色。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        HttpSession session = request.getSession(false);
        Object attribute = session == null ? null : session.getAttribute(Constants.SESSION_USER);
        if (!(attribute instanceof LoginUser user)) {
            reject(response, 401, "请先登录");
            return false;
        }
        if (request.getRequestURI().startsWith("/api/admin")
                && !Constants.ROLE_ADMIN.equals(user.role())) {
            reject(response, 403, "无管理员权限");
            return false;
        }
        return true;
    }

    private void reject(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, message));
    }
}
