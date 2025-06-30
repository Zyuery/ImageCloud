package com.zyuer.imagecloud.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    //错误码
    private final int code;

    //全参（message自定义）
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    //全参（meassage对应Enum错误码信息）
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    //全参（code对应Enum的错误码）
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}

