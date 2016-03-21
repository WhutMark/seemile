package com.seemile.launcher.exception;

/**
 * Created by whuthm on 2016/3/9.
 */
public class NetworkError extends Exception {

    private int mRspCode = -1;

    public NetworkError(String detailMessage) {
        super(detailMessage);
    }

    public NetworkError(int rspCode, String detailMessage) {
        super(detailMessage);
        mRspCode = rspCode;
    }

    public NetworkError(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public int getRspCode() {
        return mRspCode;
    }

    public static NetworkError getDefault() {
        return new NetworkError("");
    }
}
