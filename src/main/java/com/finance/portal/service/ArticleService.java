package com.finance.portal.service;

import com.finance.portal.entity.Article;
import com.finance.portal.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门户资讯业务。
 */
@Service
public class ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    public List<Article> latest(int limit) {
        return articleMapper.findLatest(Math.min(Math.max(limit, 1), 20));
    }

    public List<Article> all() {
        return articleMapper.findAll();
    }
}
