package com.seemile.launcher.net;

import android.text.TextUtils;
import android.util.Log;

import com.seemile.launcher.domain.Download;
import com.seemile.launcher.exception.NetworkError;
import com.seemile.launcher.util.DigestUtils;
import com.seemile.launcher.util.FileUtils;
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

    private static final String TAG = "DownloadEngine";

    private static final int SC_PARTIAL_CONTENT = 206;

    private static final String MD5_SUFFIX = ".md5";

    private static OkHttpClient sHttpClient;

    static {
        sHttpClient = new OkHttpClient();

        sHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        sHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        sHttpClient.setWriteTimeout(60, TimeUnit.SECONDS);
    }

    private final String filePath;
    private final String md5;
    private final long fileSize;

    public DownloadEngine(String url, String filePath, long fileSize, String md5) {
        super(new RequestProtocol.Builder(url).build());
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.md5 = md5;
    }

    private boolean needVerifyMd5() {
        return !TextUtils.isEmpty(md5);
    }

    private String getMd5FilePath() {
        return filePath + MD5_SUFFIX;
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
        Log.i(TAG, "convertFromRequestProtocol : " + new File(filePath).length());

        okBuilder.url(protocol.url())
                .addHeader("Range", "bytes=" + new File(filePath).length() + "-")
                .method(getOkMethod(), getOkRequestBody());
        return okBuilder.build();
    }

    @Override
    public Observable<Download> start() {
        Observable<Download> observable = Observable.create(new Observable.OnSubscribe<Download>() {
            @Override
            public void call(Subscriber<? super Download> subscriber) {
                Logger.i(TAG, "start download");
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
                        Logger.i(TAG, "downloading : responseSuccessful");
                        ResponseBody responseBody = response.body();
                        //根据具体的后台来判断，有的后台只返回剩余的大小
                        final long realFileSize = responseBody.contentLength();
                        Logger.i(TAG, "FilePath = " + filePath);
                        Logger.i(TAG, "开始判断是否断点续传：rspCode = " + rspCode +
                                ", fileSize = " + fileSize +
                                ", realFileSize = " + realFileSize +
                                ", completedFileSize = " + completedFileSize);
                        final File md5File = new File(getMd5FilePath());
                        final String localMd5 = FileUtils.readFileAsString(md5File);
                        final String remoteMd5 = md5;

                        final boolean needVerifyMd5 = needVerifyMd5();
                        Log.i(TAG, "needVerifyMd5 = " + needVerifyMd5);

                        if (needVerifyMd5 && remoteMd5.equals(localMd5) && realFileSize == fileSize && rspCode == SC_PARTIAL_CONTENT) {
                            append = true;
                        } else {
                            if (needVerifyMd5) {
                                FileUtils.writeFile(md5File, remoteMd5);
                            }
                            completedFileSize = 0;
                            file.createNewFile();
                        }
                        Logger.i(TAG, "断点续传 : " + append);
                        is = responseBody.byteStream();
                        fos = new FileOutputStream(file, append);
                        byte[] buffer = new byte[4 * 1024];
                        int length;
                        int progress = calculateProgress(completedFileSize, realFileSize);
                        Logger.i(TAG, "curProgress progress = " + progress + "  " + completedFileSize + "  " + realFileSize);
                        subscriber.onNext(Download.RUNNING(progress));
                        while ((length = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                            completedFileSize += length;
                            int curProgress = calculateProgress(completedFileSize, realFileSize);
                            if (curProgress != progress) {
                                Logger.i(TAG, "curProgress = " + curProgress + "  " + completedFileSize + "  " + realFileSize);
                                progress = curProgress;
                                subscriber.onNext(Download.RUNNING(progress));
                            }
                        }
                        if (needVerifyMd5) {
                            md5File.delete();
                            final String fileMd5 = DigestUtils.getMD5(file);
                            if (remoteMd5.equals(fileMd5)) {
                                subscriber.onNext(Download.SUCCESSFUL());
                                subscriber.onCompleted();
                            } else {
                                file.delete();
                                Logger.e(TAG, "md5 verify failed : fileMd5 = " + fileMd5 + ", remoteMd5 = " + remoteMd5 + ", localMd5 = " + localMd5);
                                subscriber.onNext(Download.FAILED());
                                subscriber.onError(new NetworkError(rspCode, response.toString()));
                            }
                        } else {
                            subscriber.onNext(Download.SUCCESSFUL());
                            subscriber.onCompleted();
                        }
                    } else {
                        Logger.e(TAG, "downloading : responseFailed");
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
