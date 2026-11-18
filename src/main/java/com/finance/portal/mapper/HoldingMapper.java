package com.finance.portal.mapper;

import com.finance.portal.entity.Holding;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 持仓表数据访问。
 */
public interface HoldingMapper {

    Holding findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    List<Holding> findByUser(@Param("userId") Long userId);

    int insert(Holding holding);

    int updateAmounts(@Param("id") Long id,
                      @Param("amount") BigDecimal amount,
                      @Param("shares") BigDecimal shares,
                      @Param("expectedIncome") BigDecimal expectedIncome);

    int deleteById(@Param("id") Long id);

    BigDecimal sumAmountByUser(@Param("userId") Long userId);

    long countAll();
}
