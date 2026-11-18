package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.entity.Article;
import com.finance.portal.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门户资讯接口。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ApiResponse<List<Article>> list(@RequestParam(required = false) Integer limit) {
        return ApiResponse.ok(limit == null ? articleService.all() : articleService.latest(limit));
    }
}
