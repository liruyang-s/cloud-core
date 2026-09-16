package com.micro.cloud.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 */
@Data
public class Result<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(String msg) {
        return fail(ResultCode.FAIL.getCode(), msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMsg());
    }
}
