package com.finance.portal.service;

import com.finance.portal.common.BizException;
import com.finance.portal.common.Constants;
import com.finance.portal.common.LoginUser;
import com.finance.portal.common.PageResult;
import com.finance.portal.common.RiskLevels;
import com.finance.portal.dto.AccountOverview;
import com.finance.portal.dto.LoginRequest;
import com.finance.portal.dto.RegisterRequest;
import com.finance.portal.entity.Holding;
import com.finance.portal.entity.User;
import com.finance.portal.mapper.HoldingMapper;
import com.finance.portal.mapper.OrderMapper;
import com.finance.portal.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户相关业务：注册、登录、账户总览与管理端用户管理。
 */
@Service
public class UserService {

    /** 新注册客户的模拟初始资金。 */
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("100000.00");

    private final UserMapper userMapper;
    private final HoldingMapper holdingMapper;
    private final OrderMapper orderMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, HoldingMapper holdingMapper, OrderMapper orderMapper,
                       PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.holdingMapper = holdingMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginUser register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BizException("用户名已被占用，请更换");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(Constants.ROLE_CUSTOMER);
        user.setBalance(INITIAL_BALANCE);
        user.setStatus(Constants.USER_ENABLED);
        userMapper.insert(user);
        return toLoginUser(user);
    }

    public LoginUser login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == Constants.USER_DISABLED) {
            throw new BizException(403, "该账号已被禁用，请联系管理员");
        }
        return toLoginUser(user);
    }

    public User requireUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        return user;
    }

    public AccountOverview overview(Long userId) {
        User user = requireUser(userId);
        AccountOverview overview = new AccountOverview();
        overview.setUsername(user.getUsername());
        overview.setRealName(user.getRealName());
        overview.setRiskLevel(user.getRiskLevel());
        overview.setRiskLabel(RiskLevels.customerLabel(user.getRiskLevel()));
        overview.setBalance(user.getBalance());
        overview.setHoldingAmount(holdingMapper.sumAmountByUser(userId));
        long orderCount = orderMapper.countByUser(userId);
        overview.setOrderCount(orderCount);
        List<Holding> holdings = holdingMapper.findByUser(userId);
        BigDecimal expected = holdings.stream()
                .filter(h -> Constants.HOLDING_HOLDING.equals(h.getStatus()))
                .map(Holding::getExpectedIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        overview.setExpectedIncome(expected);
        return overview;
    }

    public List<Holding> holdings(Long userId) {
        requireUser(userId);
        return holdingMapper.findByUser(userId);
    }

    public PageResult<User> listUsers(int page, int size) {        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        long total = userMapper.countAll();
        List<User> list = userMapper.findPage((safePage - 1) * safeSize, safeSize);
        return PageResult.of(total, safePage, safeSize, list);
    }

    public void updateStatus(Long userId, Integer status) {
        if (status == null || (status != Constants.USER_ENABLED && status != Constants.USER_DISABLED)) {
            throw new BizException("用户状态取值不合法");
        }
        if (userMapper.updateStatus(userId, status) == 0) {
            throw new BizException(404, "用户不存在");
        }
    }

    public LoginUser toLoginUser(User user) {
        return new LoginUser(user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }
}
