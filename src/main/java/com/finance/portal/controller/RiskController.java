package com.finance.portal.controller;

import com.finance.portal.common.ApiResponse;
import com.finance.portal.common.LoginUser;
import com.finance.portal.dto.RiskSubmitRequest;
import com.finance.portal.service.RiskService;
import com.finance.portal.util.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 风险测评接口（需登录）。
 */
@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/assessments")
    public ApiResponse<Map<String, Object>> submit(@Valid @RequestBody RiskSubmitRequest request,
                                                   HttpServletRequest servletRequest) {
        LoginUser user = SessionHelper.current(servletRequest);
        return ApiResponse.ok(riskService.submit(user.id(), request.getAnswers()));
    }

    @GetMapping("/latest")
    public ApiResponse<Map<String, Object>> latest(HttpServletRequest request) {
        LoginUser user = SessionHelper.current(request);
        return ApiResponse.ok(riskService.latest(user.id()));
    }
}
