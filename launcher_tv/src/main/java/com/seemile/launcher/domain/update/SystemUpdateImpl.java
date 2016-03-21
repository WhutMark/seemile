package com.seemile.launcher.domain.update;

import com.seemile.launcher.domain.Download;
import com.seemile.launcher.domain.interactor.SystemUpdateInteractor;
import com.seemile.launcher.net.DownloadEngine;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by whuthm on 2016/3/15.
 */
class SystemUpdateImpl implements SystemUpdateInteractor {

    @Override
    public Observable<Version> checkNewVersion(String uniqueId, String localVersion, String channel) {
        Observable<Version> observable = Observable.create(new Observable.OnSubscribe<Version>() {
            @Override
            public void call(Subscriber<? super Version> subscriber) {
                Version version = Version.valueOf(
                        "1.1",
                        "UC浏览器",
                        "http://shouji.360tpcdn.com/160315/908f2f78ab4ce4b44ecaca2968b09886/com.wiwigo.app_42.apk");
                subscriber.onNext(version);
                subscriber.onCompleted();
            }
        });
        return observable;
    }

    @Override
    public Observable<Download> download(String url, String filePath, long fileSize) {
        DownloadEngine engine = new DownloadEngine(
                url,
                filePath,
                fileSize);
        return engine.start();
    }
}
