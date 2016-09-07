package com.seemile.launcher.exception;

/**
 * Created by whuthm on 2016/3/15.
 */
public class ConverterException extends Exception {

    public ConverterException(String detailMessage) {
        super(detailMessage);
    }

    public ConverterException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
