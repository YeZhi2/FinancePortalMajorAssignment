package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.Constants;
import com.finance.portal.common.LoginUser;
import com.finance.portal.dto.LoginRequest;
import com.finance.portal.dto.RegisterRequest;
import com.finance.portal.service.UserService;
import com.finance.portal.util.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：注册、登录、登出、当前登录用户。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<LoginUser> register(@Valid @RequestBody RegisterRequest request,
                                           HttpServletRequest servletRequest) {
        LoginUser user = userService.register(request);
        establishSession(servletRequest, user);
        return ApiResponse.ok(user);
    }

    @PostMapping("/login")
    public ApiResponse<LoginUser> login(@Valid @RequestBody LoginRequest request,
                                        HttpServletRequest servletRequest) {
        LoginUser user = userService.login(request);
        establishSession(servletRequest, user);
        return ApiResponse.ok(user);
    }

    /**
     * 写入登录态：先更换会话 ID，避免会话固定攻击。
     */
    private void establishSession(HttpServletRequest request, LoginUser user) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(Constants.SESSION_USER, user);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<LoginUser> me(HttpServletRequest request) {
        return ApiResponse.ok(SessionHelper.currentOrNull(request));
    }
}
