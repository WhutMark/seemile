package com.seemile.launcher.net;

import com.seemile.launcher.data.converter.DataConverter;
import com.seemile.launcher.exception.NetworkError;
import com.seemile.launcher.util.Logger;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by whuthm on 2016/3/10.
 */
public abstract class OkHttpEngine<T> extends AbstractEngine<T, JSONObject> {

    private static final String TAG = "OkHttpEngine";

    protected Call okCall;

    protected OkHttpEngine(RequestProtocol protocol, DataConverter dataConverter) {
        super(protocol, dataConverter);
    }

    protected OkHttpEngine(RequestProtocol protocol) {
        super(protocol);
    }

    protected abstract Request convertFromRequestProtocol();

    protected final String getOkMethod() {
        switch (protocol.method()) {
            case RequestProtocol.Method.GET:
                return "GET";
            case RequestProtocol.Method.POST:
                return "POST";
            case RequestProtocol.Method.PUT:
                return "PUT";
            case RequestProtocol.Method.DELETE:
                return "DELETE";
            case RequestProtocol.Method.HEAD:
                return "HEAD";
            case RequestProtocol.Method.PATCH:
                return "PATCH";
            default:
                return "GET";
        }
    }

    protected RequestBody getOkRequestBody() {
        return null;
    }

    protected abstract OkHttpClient getOkHttpClient();

    @Override
    public Observable<T> start() {
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                final Request okRequest = convertFromRequestProtocol();
                okCall = getOkHttpClient().newCall(okRequest);
                okCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Logger.e(TAG, request.urlString());
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        final String url = okRequest.urlString();
                        int rspCode = response.code();
                        String rspBody = new String(response.body().bytes());
                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(rspBody);
                                String rspSelfMsg = json.optString("msg");
                                int rspSelfCode = Integer.parseInt(json.optString("ret"));
                                Logger.i(TAG, "parse response : rspSelfCode = " + rspSelfCode + ", rspSelfMsg = " + rspSelfMsg);
                                if (rspSelfCode == 1) {
                                    subscriber.onNext(parseRspContent(json));
                                    subscriber.onCompleted();
                                } else {
                                    subscriber.onError(new NetworkError(rspSelfCode, rspSelfMsg));
                                }
                                Logger.i(TAG, url + "(response success) : " + rspBody);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Logger.e(TAG, url + "(parse json error) : " + rspBody);
                                subscriber.onError(e);
                            }
                        } else {
                            Logger.e(TAG, url + "(response error) : " + rspBody);
                            subscriber.onError(new NetworkError(rspCode, rspBody));
                            //subscriber.onError(RspErrorConverter.parseByHttpRsp(rspCode, rspBody));
                        }
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    public void stop() {
        if (okCall != null) {
            okCall.cancel();
        }
    }
}
