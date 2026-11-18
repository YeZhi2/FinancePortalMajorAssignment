package com.finance.portal.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果。
 */
@Data
public class PageResult<T> {

    private long total;
    private int page;
    private int size;
    private int pages;
    private List<T> list;

    public static <T> PageResult<T> of(long total, int page, int size, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setList(list);
        result.setPages(size <= 0 ? 0 : (int) ((total + size - 1) / size));
        return result;
    }
}
