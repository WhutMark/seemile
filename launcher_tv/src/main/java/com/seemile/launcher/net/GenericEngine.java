package com.seemile.launcher.net;

import com.seemile.launcher.data.converter.DataConverter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by whuthm on 2016/3/9.
 */
public class GenericEngine<T> extends OkHttpEngine<T> {

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient sHttpClient;

    private static final String BMOB_APPLICATION_ID = "X-Bmob-Application-Id";

    private static final String VALUE_BMOB_APPLICATION_ID = "068e2845657604675dd4e513ca2ab7d4";

    private static final String BMOB_REST_API_KEY = "X-Bmob-REST-API-Key";

    private static final String VALUE_BMOB_REST_API_KEY = "0f734a7c938b2f6dfd6402dc2642d77c";

    private static final String CONTENT_TYPE = "Content-type";

    private static final String VALUE_CONTENT_TYPE = "application/json";

    static {
        sHttpClient = new OkHttpClient();

        sHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        sHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        sHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
    }

    public GenericEngine(RequestProtocol protocol, DataConverter dataConverter) {
        super(protocol, dataConverter);
    }

    @Override
    protected Request convertFromRequestProtocol() {
        Request.Builder okBuilder = new Request.Builder();
        Map<String, String> headers = protocol.headers();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            okBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        StringBuilder url = new StringBuilder(protocol.url());
        if (protocol.method() == RequestProtocol.Method.GET) {
            Map<String, String> parameters = protocol.parameters();
            if (parameters.size() > 0) {
                url.append("?");
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                url.delete(url.length() - 1, url.length());
            }
        }
        okBuilder.url(url.toString())
                .method(getOkMethod(), getOkRequestBody());
        return okBuilder.build();
    }

    @Override
    protected RequestBody getOkRequestBody() {
        if (protocol.method() == RequestProtocol.Method.GET) {
            return null;
        } else {
            Map<String, String> parameters = protocol.parameters();
            if (parameters.size() > 0) {
                JSONObject jsonBody = new JSONObject();
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    try {
                        jsonBody.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return RequestBody.create(JSON_TYPE, jsonBody.toString());
            }
            return super.getOkRequestBody();
        }
    }

    @Override
    protected OkHttpClient getOkHttpClient() {
        return sHttpClient;
    }
}
