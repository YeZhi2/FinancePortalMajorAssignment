package com.finance.portal.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门户资讯 / 投教文章。
 */
@Data
public class Article {

    private Long id;
    private String title;

    /** 市场解读 / 投教 / 公告。 */
    private String category;

    private String summary;
    private String content;
    private LocalDate publishDate;

    /** 1 发布，0 隐藏。 */
    private Integer status;

    private LocalDateTime createdAt;
}
