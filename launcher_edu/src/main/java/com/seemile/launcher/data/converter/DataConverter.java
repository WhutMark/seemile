package com.seemile.launcher.data.converter;


import com.seemile.launcher.exception.ConverterException;

/**
 * Created by whuthm on 2016/3/8.
 */
public interface DataConverter<T, R> {

    public T from(R source) throws ConverterException;

    public static DataConverter EMPTY = new DataConverter() {
        @Override
        public Object from(Object source) throws ConverterException {
            return source;
        }
    };
}
