package com.seemile.launcher.net;


import com.seemile.launcher.data.converter.DataConverter;
import com.seemile.launcher.exception.ConverterException;

/**
 * Created by whuthm on 2016/3/14.
 */
public abstract class AbstractEngine<T, R> implements Engine {

    protected final RequestProtocol protocol;

    protected final DataConverter<T, R> dataConverter;

    protected AbstractEngine(RequestProtocol protocol, DataConverter<T, R> dataConverter) {
        this.protocol = protocol;
        this.dataConverter = dataConverter;
    }

    protected AbstractEngine(RequestProtocol protocol) {
        this(protocol, DataConverter.EMPTY);
    }

    protected T parseRspContent(R rspContent) throws ConverterException {
        return dataConverter.from(rspContent);
    }

}
