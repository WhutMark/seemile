package com.seemile.launcher.data.converter;

import com.seemile.launcher.exception.ConverterException;

import org.json.JSONObject;

/**
 * Created by whuthm on 2016/3/8.
 */
public interface JsonConverter<T> extends DataConverter<T, JSONObject> {

    @Override
    public T from(JSONObject source) throws ConverterException;
}
