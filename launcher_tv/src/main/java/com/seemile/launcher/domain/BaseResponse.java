package com.seemile.launcher.domain;

/**
 * Created by whuthm on 2016/4/11.
 */
public class BaseResponse {

    public final int errorCode;
    public final String message;

    public BaseResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseResponse : (errorCode=" + errorCode + "; message=" + message + ")";
    }
}
