package com.seemile.launcher.net;

import com.seemile.launcher.domain.Download;
import com.seemile.launcher.exception.NetworkError;
import com.seemile.launcher.util.Logger;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by whuthm on 2016/3/10.
 */
public class DownloadEngine extends OkHttpEngine<Download> {

    private static final String TAG = "GenericEngine";

    private static final int SC_PARTIAL_CONTENT = 206;

    private static OkHttpClient sHttpClient;

    static {
        sHttpClient = new OkHttpClient();

        sHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        sHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        sHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
    }

    private final String filePath;
    private final String url;
    private final long fileSize;

    public DownloadEngine(String url, String filePath, long fileSize) {
        super(new RequestProtocol.Builder(url).build());
        this.filePath = filePath;
        this.url = url;
        this.fileSize = fileSize;
    }

    @Override
    protected OkHttpClient getOkHttpClient() {
        return sHttpClient;
    }

    protected Request convertFromRequestProtocol() {
        Request.Builder okBuilder = new Request.Builder();
        Map<String, String> headers = protocol.headers();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            okBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        okBuilder.url(protocol.url())
                //.addHeader("Range", "bytes=" + new File(filePath).length() + "-")
                .method(getOkMethod(), getOkRequestBody());
        return okBuilder.build();
    }

    @Override
    public Observable<Download> start() {
        Observable<Download> observable = Observable.create(new Observable.OnSubscribe<Download>() {
            @Override
            public void call(Subscriber<? super Download> subscriber) {
                subscriber.onNext(Download.PENDING());
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    File file = new File(filePath);
                    long completedFileSize = file.length();
                    okCall = sHttpClient.newCall(convertFromRequestProtocol());
                    Response response = okCall.execute();
                    final int rspCode = response.code();
                    boolean append = false;
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        //根据具体的后台来判断，有的后台只返回剩余的大小
                        final long realFileSize = responseBody.contentLength();
                        if (realFileSize == fileSize && rspCode == SC_PARTIAL_CONTENT) {
                            append = true;
                        } else {
                            completedFileSize = 0;
                            file.createNewFile();
                        }
                        is = responseBody.byteStream();
                        fos = new FileOutputStream(file, append);
                        byte[] buffer = new byte[4 * 1024];
                        int length;
                        int progress = calculateProgress(completedFileSize, realFileSize);
                        subscriber.onNext(Download.RUNNING(progress));
                        while ((length = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                            completedFileSize += length;
                            int curProgress = calculateProgress(completedFileSize, realFileSize);
                            Logger.e(TAG, "curProgress = " + curProgress + "  " + completedFileSize + "  " + realFileSize);
                            if (curProgress != progress) {
                                progress = curProgress;
                                subscriber.onNext(Download.RUNNING(progress));
                            }
                        }
                        subscriber.onNext(Download.SUCCESSFUL());
                        subscriber.onCompleted();
                    } else {
                        subscriber.onNext(Download.FAILED());
                        subscriber.onError(new NetworkError(rspCode, response.toString()));
                    }

                } catch (FileNotFoundException e) {
                    subscriber.onNext(Download.FAILED());
                    subscriber.onError(e);
                } catch (IOException e) {
                    subscriber.onNext(Download.FAILED());
                    subscriber.onError(e);
                } catch (NullPointerException e) {
                    subscriber.onNext(Download.FAILED());
                    subscriber.onError(e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    static int calculateProgress(long completedFileSize, long fileSize) {
        if (completedFileSize < 0 || fileSize <= 0) {
            return 0;
        }
        int result = (int) (completedFileSize * 100 / fileSize);
        if (result < 0) {
            return 0;
        } else if (result > 100) {
            return 100;
        }
        return result;
    }

}
