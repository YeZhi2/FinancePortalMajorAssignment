package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.LoginUser;
import com.finance.portal.common.PageResult;
import com.finance.portal.dto.OrderRequest;
import com.finance.portal.entity.InvestOrder;
import com.finance.portal.service.OrderService;
import com.finance.portal.util.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 交易接口：购买、赎回、我的订单（需登录）。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<InvestOrder> purchase(@Valid @RequestBody OrderRequest request,
                                             HttpServletRequest servletRequest) {
        LoginUser user = SessionHelper.current(servletRequest);
        return ApiResponse.ok(orderService.purchase(user.id(), request));
    }

    @GetMapping
    public ApiResponse<PageResult<InvestOrder>> list(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     HttpServletRequest servletRequest) {
        LoginUser user = SessionHelper.current(servletRequest);
        return ApiResponse.ok(orderService.myOrders(user.id(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<InvestOrder> detail(@PathVariable Long id, HttpServletRequest servletRequest) {
        LoginUser user = SessionHelper.current(servletRequest);
        return ApiResponse.ok(orderService.detail(user.id(), id));
    }

    @PostMapping("/{id}/redeem")
    public ApiResponse<Map<String, Object>> redeem(@PathVariable Long id, HttpServletRequest servletRequest) {
        LoginUser user = SessionHelper.current(servletRequest);
        return ApiResponse.ok(orderService.redeem(user.id(), id));
    }
}
