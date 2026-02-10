package com.example.demo.common.constant;

/**
 * 通用错误码定义
 */
public enum ErrorCode {

    SUCCESS(0, "OK"),
    BUSINESS_ERROR(1000, "业务异常"),
    VALIDATION_ERROR(1001, "参数校验失败"),
    NOT_FOUND(1004, "资源不存在"),
    UNAUTHORIZED(1005, "未授权"),
    FORBIDDEN(1006, "无访问权限"),
    SYSTEM_ERROR(2000, "系统异常，请稍后重试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

