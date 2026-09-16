package com.micro.cloud.common.core.exception;

import com.micro.cloud.common.core.domain.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class ServiceException extends RuntimeException {

    private final int code;

    public ServiceException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }
}
