package com.finance.portal.mapper;

import com.finance.portal.dto.ProductQuery;
import com.finance.portal.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品表数据访问。
 */
public interface ProductMapper {

    Product findById(@Param("id") Long id);

    Product findByCode(@Param("code") String code);

    List<Product> findPage(ProductQuery query);

    long count(ProductQuery query);

    List<Product> findHot(@Param("limit") int limit);

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 增加已售金额，带额度上限条件，返回受影响行数（0 表示额度不足）。
     */
    int increaseSold(@Param("id") Long id, @Param("amount") BigDecimal amount);

    long countAll();
}
