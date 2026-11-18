package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.LoginUser;
import com.finance.portal.dto.AccountOverview;
import com.finance.portal.entity.Holding;
import com.finance.portal.service.UserService;
import com.finance.portal.util.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户账户接口：总览与持仓（需登录）。
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/overview")
    public ApiResponse<AccountOverview> overview(HttpServletRequest request) {
        LoginUser user = SessionHelper.current(request);
        return ApiResponse.ok(userService.overview(user.id()));
    }

    @GetMapping("/holdings")
    public ApiResponse<List<Holding>> holdings(HttpServletRequest request) {
        LoginUser user = SessionHelper.current(request);
        return ApiResponse.ok(userService.holdings(user.id()));
    }
}
