package com.finance.portal.common;

import lombok.Getter;

/**
 * 业务异常：由 Service 层抛出，由全局异常处理器转换为统一的响应体。
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(400, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
