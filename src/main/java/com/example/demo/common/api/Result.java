package com.example.demo.common.api;

import com.example.demo.common.constant.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 统一响应结果封装
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 响应时间戳（毫秒）
     */
    private Long timestamp;

    public Result() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(ErrorCode.SUCCESS.getCode());
        r.setMessage(ErrorCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> failure(ErrorCode errorCode) {
        Result<T> r = new Result<>();
        r.setCode(errorCode.getCode());
        r.setMessage(errorCode.getMessage());
        return r;
    }

    public static <T> Result<T> failure(ErrorCode errorCode, String message) {
        Result<T> r = new Result<>();
        r.setCode(errorCode.getCode());
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> failure(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}

