package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.PageResult;
import com.finance.portal.dto.ProductForm;
import com.finance.portal.dto.ProductQuery;
import com.finance.portal.entity.InvestOrder;
import com.finance.portal.entity.Product;
import com.finance.portal.entity.User;
import com.finance.portal.service.OrderService;
import com.finance.portal.service.ProductService;
import com.finance.portal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理端接口：统计、产品维护、订单与用户管理（需管理员角色）。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.ok(orderService.stats());
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<Product>> products(ProductQuery query) {
        return ApiResponse.ok(productService.adminPage(query));
    }

    @PostMapping("/products")
    public ApiResponse<Product> createProduct(@Valid @RequestBody ProductForm form) {
        return ApiResponse.ok(productService.create(form));
    }

    @PutMapping("/products/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductForm form) {
        return ApiResponse.ok(productService.update(id, form));
    }

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok();
    }

    @PatchMapping("/products/{id}/status")
    public ApiResponse<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return ApiResponse.ok();
    }

    @GetMapping("/orders")
    public ApiResponse<PageResult<InvestOrder>> orders(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(orderService.adminOrders(page, size, status, keyword));
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<User>> users(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(userService.listUsers(page, size));
    }

    @PatchMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return ApiResponse.ok();
    }
}
