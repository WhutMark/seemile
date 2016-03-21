package com.seemile.launcher.net;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by whuthm on 2016/3/10.
 */
public class RequestProtocol {

    private final String url;
    private final int method;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final RetryPolicy retryPolicy;

    private RequestProtocol(Builder builder) {
        url = builder.url;
        method = builder.method;
        headers = builder.headers;
        parameters = builder.parameters;
        retryPolicy = builder.retryPolicy;
    }

    public int method() {
        return method;
    }

    public String url() {
        return url;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Map<String, String> parameters() {
        return parameters;
    }

    public RetryPolicy retryPolicy() {
        return retryPolicy;
    }

    public static class Builder {
        private String url;
        private int method = Method.GET;
        private Map<String, String> headers = new LinkedHashMap<String, String>();
        private Map<String, String> parameters = new LinkedHashMap<String, String>();
        private RetryPolicy retryPolicy = defaultRetryPolicy();

        public Builder() {
        }

        public Builder(String url) {
            this.url = url;
        }

        public Builder method(int method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addHead(String head, String value) {
            headers.put(head, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addParameter(String parameter, String value) {
            parameters.put(parameter, value);
            return this;
        }

        public Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public RequestProtocol build() {
            return new RequestProtocol(this);
        }

    }

    public static RetryPolicy defaultRetryPolicy() {
        return new DefaultRetryPolicy(0);
    }

    public static class Method {
        public static final int GET = 0;
        public static final int POST = 1;
        public static final int PUT = 2;
        public static final int DELETE = 3;
        public static final int HEAD = 4;
        public static final int PATCH = 7;
    }

    public interface RetryPolicy {
        public int getRetryCount();

        public boolean canRetry();

        public void retry();

    }

    private static class DefaultRetryPolicy implements RetryPolicy {

        private final int retryCount;
        private int curRetryCount;

        private DefaultRetryPolicy(int retryCount) {
            this.retryCount = retryCount;
            this.curRetryCount = retryCount;
        }

        @Override
        public int getRetryCount() {
            return retryCount;
        }

        @Override
        public boolean canRetry() {
            return curRetryCount > 0;
        }

        @Override
        public void retry() {
            curRetryCount--;
        }

    }

}
