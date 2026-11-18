package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.PageResult;
import com.finance.portal.dto.ProductQuery;
import com.finance.portal.entity.Product;
import com.finance.portal.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门户端产品接口（无需登录）。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<PageResult<Product>> list(ProductQuery query) {
        return ApiResponse.ok(productService.page(query));
    }

    @GetMapping("/hot")
    public ApiResponse<List<Product>> hot(@RequestParam(defaultValue = "6") int limit) {
        return ApiResponse.ok(productService.hot(limit));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.detail(id));
    }
}
