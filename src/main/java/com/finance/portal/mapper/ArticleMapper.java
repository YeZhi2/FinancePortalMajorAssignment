package com.finance.portal.mapper;

import com.finance.portal.entity.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资讯数据访问。
 */
public interface ArticleMapper {

    List<Article> findLatest(@Param("limit") int limit);

    List<Article> findAll();
}
