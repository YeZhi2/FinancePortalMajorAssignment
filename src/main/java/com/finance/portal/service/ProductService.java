package com.finance.portal.service;

import com.finance.portal.common.BizException;
import com.finance.portal.common.Constants;
import com.finance.portal.common.PageResult;
import com.finance.portal.dto.ProductForm;
import com.finance.portal.dto.ProductQuery;
import com.finance.portal.entity.Product;
import com.finance.portal.mapper.OrderMapper;
import com.finance.portal.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;

/**
 * 产品业务：门户端查询与管理端维护。
 */
@Service
public class ProductService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public ProductService(ProductMapper productMapper, OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * 门户端分页查询：只返回上架产品。
     */
    public PageResult<Product> page(ProductQuery query) {
        query.setAdmin(Boolean.FALSE);
        long total = productMapper.count(query);
        List<Product> list = productMapper.findPage(query);
        return PageResult.of(total, query.getPage(), query.getLimit(), list);
    }

    /**
     * 管理端分页查询：包含下架产品。
     */
    public PageResult<Product> adminPage(ProductQuery query) {
        query.setAdmin(Boolean.TRUE);
        long total = productMapper.count(query);
        List<Product> list = productMapper.findPage(query);
        return PageResult.of(total, query.getPage(), query.getLimit(), list);
    }

    public Product detail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BizException(404, "产品不存在");
        }
        return product;
    }

    public List<Product> hot(int limit) {
        return productMapper.findHot(Math.min(Math.max(limit, 1), 12));
    }

    @Transactional
    public Product create(ProductForm form) {
        Product product = new Product();
        product.setCode(resolveCode(form.getCode(), null));
        applyForm(product, form);
        product.setSoldAmount(BigDecimal.ZERO);
        productMapper.insert(product);
        return product;
    }

    @Transactional
    public Product update(Long id, ProductForm form) {
        Product product = detail(id);
        product.setCode(StringUtils.hasText(form.getCode())
                ? resolveCode(form.getCode(), id)
                : product.getCode());
        applyForm(product, form);
        productMapper.update(product);
        return product;
    }

    public void delete(Long id) {
        detail(id);
        if (orderMapper.countByProduct(id) > 0) {
            throw new BizException("该产品已有交易记录，无法删除，请改为下架");
        }
        productMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != Constants.PRODUCT_ON && status != Constants.PRODUCT_OFF)) {
            throw new BizException("产品状态取值不合法");
        }
        detail(id);
        productMapper.updateStatus(id, status);
    }

    private void applyForm(Product product, ProductForm form) {
        product.setName(form.getName());
        product.setCategory(form.getCategory());
        product.setRiskLevel(form.getRiskLevel());
        product.setExpectedReturn(form.getExpectedReturn().setScale(2, RoundingMode.HALF_UP));
        product.setTermDays(form.getTermDays());
        product.setMinAmount(form.getMinAmount().setScale(2, RoundingMode.HALF_UP));
        product.setTotalAmount(form.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        product.setManager(form.getManager());
        product.setDescription(form.getDescription());
        product.setStatus(form.getStatus() == null ? Constants.PRODUCT_ON : form.getStatus());
    }

    /**
     * 解析产品代码：为空时自动生成，非空时校验唯一性（编辑时排除自身）。
     */
    private String resolveCode(String code, Long excludeId) {
        if (StringUtils.hasText(code)) {
            Product existing = productMapper.findByCode(code);
            if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
                throw new BizException("产品代码已存在");
            }
            return code;
        }
        for (int i = 0; i < 5; i++) {
            String candidate = "FP" + java.time.LocalDate.now().getYear() + String.format("%04d", RANDOM.nextInt(10000));
            if (productMapper.findByCode(candidate) == null) {
                return candidate;
            }
        }
        throw new BizException("产品代码生成失败，请手动指定");
    }
}
