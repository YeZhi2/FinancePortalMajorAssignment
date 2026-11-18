package com.finance.portal.mapper;

import com.finance.portal.entity.User;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户表数据访问。
 */
public interface UserMapper {

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    int insert(User user);

    int updateRiskLevel(@Param("id") Long id, @Param("riskLevel") String riskLevel);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 扣减余额，带余额充足条件，返回受影响行数（0 表示余额不足）。
     */
    int deductBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 增加余额（赎回、模拟入金）。
     */
    int addBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    long countAll();

    List<User> findPage(@Param("offset") int offset, @Param("size") int size);
}
