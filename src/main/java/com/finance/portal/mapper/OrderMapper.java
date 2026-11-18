package com.finance.portal.mapper;

import com.finance.portal.entity.InvestOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单表数据访问。
 */
public interface OrderMapper {

    int insert(InvestOrder order);

    InvestOrder findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    List<InvestOrder> findByUser(@Param("userId") Long userId,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    long countByUser(@Param("userId") Long userId);

    List<InvestOrder> findPage(@Param("offset") int offset,
                               @Param("size") int size,
                               @Param("status") String status,
                               @Param("keyword") String keyword);

    long count(@Param("status") String status, @Param("keyword") String keyword);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    long countAll();

    BigDecimal sumAmountByStatus(@Param("status") String status);

    long countByProduct(@Param("productId") Long productId);
}
