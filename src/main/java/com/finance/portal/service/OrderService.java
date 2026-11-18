package com.finance.portal.service;

import com.finance.portal.common.BizException;
import com.finance.portal.common.Constants;
import com.finance.portal.common.PageResult;
import com.finance.portal.common.RiskLevels;
import com.finance.portal.dto.OrderRequest;
import com.finance.portal.entity.Holding;
import com.finance.portal.entity.InvestOrder;
import com.finance.portal.entity.Product;
import com.finance.portal.entity.User;
import com.finance.portal.mapper.HoldingMapper;
import com.finance.portal.mapper.OrderMapper;
import com.finance.portal.mapper.ProductMapper;
import com.finance.portal.mapper.UserMapper;
import com.finance.portal.util.OrderNoGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易业务：购买、赎回、订单查询与后台统计。
 */
@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final HoldingMapper holdingMapper;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper,
                        UserMapper userMapper, HoldingMapper holdingMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.holdingMapper = holdingMapper;
    }

    /**
     * 购买产品：校验风险匹配度与额度，扣减余额、占用额度、生成订单与持仓。
     */
    @Transactional
    public InvestOrder purchase(Long userId, OrderRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        Product product = productMapper.findById(request.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != Constants.PRODUCT_ON) {
            throw new BizException("产品不存在或已下架");
        }
        checkRiskMatch(user, product);

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(product.getMinAmount()) < 0) {
            throw new BizException("投资金额不能低于起投金额 " + product.getMinAmount().toPlainString() + " 元");
        }
        if (userMapper.deductBalance(userId, amount) == 0) {
            throw new BizException("账户余额不足，请先赎回或调整金额");
        }
        // 额度校验失败时抛出异常，事务会回滚上面的扣款
        if (productMapper.increaseSold(product.getId(), amount) == 0) {
            throw new BizException("该产品剩余额度不足");
        }

        int days = product.getTermDays() == null || product.getTermDays() <= 0 ? 365 : product.getTermDays();
        BigDecimal income = amount.multiply(product.getExpectedReturn())
                .multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(365 * 100), 2, RoundingMode.HALF_UP);

        InvestOrder order = new InvestOrder();
        order.setOrderNo(OrderNoGenerator.next());
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setAmount(amount);
        order.setShares(amount);
        order.setNetValue(new BigDecimal("1.0000"));
        order.setExpectedIncome(income);
        order.setStatus(Constants.ORDER_PAID);
        orderMapper.insert(order);

        saveHolding(userId, product, amount, income, days);
        order.setProductName(product.getName());
        return order;
    }

    /**
     * 赎回：按本金 + 预期收益模拟兑付，更新订单与持仓。
     */
    @Transactional
    public Map<String, Object> redeem(Long userId, Long orderId) {
        InvestOrder order = orderMapper.findByIdAndUser(orderId, userId);
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        if (!Constants.ORDER_PAID.equals(order.getStatus())) {
            throw new BizException("该订单已赎回，请勿重复操作");
        }
        BigDecimal principal = order.getAmount();
        BigDecimal income = order.getExpectedIncome() == null ? BigDecimal.ZERO : order.getExpectedIncome();
        BigDecimal credited = principal.add(income);

        userMapper.addBalance(userId, credited);
        orderMapper.updateStatus(orderId, Constants.ORDER_REDEEMED);

        Holding holding = holdingMapper.findByUserAndProduct(userId, order.getProductId());
        if (holding != null) {
            BigDecimal remainAmount = holding.getAmount().subtract(principal);
            if (remainAmount.compareTo(new BigDecimal("0.01")) < 0) {
                holdingMapper.deleteById(holding.getId());
            } else {
                holdingMapper.updateAmounts(holding.getId(), remainAmount,
                        holding.getShares().subtract(order.getShares()),
                        holding.getExpectedIncome().subtract(income));
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("principal", principal);
        result.put("income", income);
        result.put("credited", credited);
        return result;
    }

    public PageResult<InvestOrder> myOrders(Long userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        long total = orderMapper.countByUser(userId);
        List<InvestOrder> list = orderMapper.findByUser(userId, (safePage - 1) * safeSize, safeSize);
        return PageResult.of(total, safePage, safeSize, list);
    }

    public InvestOrder detail(Long userId, Long orderId) {
        InvestOrder order = orderMapper.findByIdAndUser(orderId, userId);
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        return order;
    }

    public PageResult<InvestOrder> adminOrders(int page, int size, String status, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String safeStatus = status == null || status.isBlank() ? null : status;
        String safeKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        long total = orderMapper.count(safeStatus, safeKeyword);
        List<InvestOrder> list = orderMapper.findPage((safePage - 1) * safeSize, safeSize, safeStatus, safeKeyword);
        return PageResult.of(total, safePage, safeSize, list);
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("productCount", productMapper.countAll());
        stats.put("userCount", userMapper.countAll());
        stats.put("orderCount", orderMapper.countAll());
        stats.put("tradeAmount", orderMapper.sumAmountByStatus(Constants.ORDER_PAID));
        stats.put("holdingCount", holdingMapper.countAll());
        return stats;
    }

    private void checkRiskMatch(User user, Product product) {
        if (user.getRiskLevel() == null || user.getRiskLevel().isBlank()) {
            throw new BizException("请先完成风险测评，再购买理财产品");
        }
        int customerLevel = RiskLevels.indexOf(user.getRiskLevel());
        int productLevel = RiskLevels.indexOf(product.getRiskLevel());
        if (customerLevel < productLevel) {
            throw new BizException(String.format("该产品风险等级为 %s（%s），高于您的风险承受能力 %s（%s），无法购买",
                    product.getRiskLevel(), RiskLevels.riskLabel(product.getRiskLevel()),
                    user.getRiskLevel(), RiskLevels.customerLabel(user.getRiskLevel())));
        }
    }

    private void saveHolding(Long userId, Product product, BigDecimal amount, BigDecimal income, int days) {
        LocalDate today = LocalDate.now();
        Holding existing = holdingMapper.findByUserAndProduct(userId, product.getId());
        if (existing != null && Constants.HOLDING_HOLDING.equals(existing.getStatus())) {
            holdingMapper.updateAmounts(existing.getId(),
                    existing.getAmount().add(amount),
                    existing.getShares().add(amount),
                    existing.getExpectedIncome().add(income));
            return;
        }
        if (existing != null) {
            holdingMapper.deleteById(existing.getId());
        }
        Holding holding = new Holding();
        holding.setUserId(userId);
        holding.setProductId(product.getId());
        holding.setShares(amount);
        holding.setAmount(amount);
        holding.setExpectedIncome(income);
        holding.setPurchaseDate(today);
        holding.setMaturityDate(product.getTermDays() != null && product.getTermDays() > 0
                ? today.plusDays(days) : null);
        holding.setStatus(Constants.HOLDING_HOLDING);
        holdingMapper.insert(holding);
    }
}
