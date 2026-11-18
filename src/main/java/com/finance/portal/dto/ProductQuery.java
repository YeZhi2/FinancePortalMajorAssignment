package com.finance.portal.dto;

import lombok.Data;

/**
 * 产品查询条件：关键字、分类、风险等级、排序与分页。
 */
@Data
public class ProductQuery {

    private String keyword;
    private String category;
    private String riskLevel;

    /** return_desc / term_asc / min_asc，其它值按默认排序。 */
    private String sort;

    private Integer page = 1;
    private Integer size = 9;

    /** true 时包含下架产品，仅管理端使用。 */
    private Boolean admin = Boolean.FALSE;

    public int getOffset() {
        return (normalizedPage() - 1) * normalizedSize();
    }

    public int getLimit() {
        return normalizedSize();
    }

    private int normalizedPage() {
        return page == null || page < 1 ? 1 : page;
    }

    private int normalizedSize() {
        if (size == null || size < 1) {
            return 9;
        }
        return Math.min(size, 50);
    }
}
