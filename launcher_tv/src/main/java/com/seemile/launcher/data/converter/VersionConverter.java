package com.seemile.launcher.data.converter;

import com.seemile.launcher.domain.update.Version;
import com.seemile.launcher.exception.ConverterException;

import org.json.JSONObject;

/**
 * Created by whuthm on 2016/3/15.
 */
public class VersionConverter implements JsonConverter<Version> {

    @Override
    public Version from(JSONObject source) throws ConverterException {
        JSONObject dataJson = source.optJSONObject("data");
        String description = dataJson.optString("description");
        String serviceVersion = dataJson.optString("romVersion");
        String downloadUrl = dataJson.optString("downUrl");
        long fileSize = dataJson.optLong("fileSize");
        return Version.valueOf(serviceVersion, description, downloadUrl, "", fileSize);
    }
}
