package com.seemile.launcher.domain.interactor;

import com.seemile.launcher.domain.Download;
import com.seemile.launcher.domain.update.Version;

import rx.Observable;

/**
 * Created by wthuhm on 2016/3/15.
 */
public interface SystemUpdateInteractor extends Interactor {

    public Observable<Version> checkNewVersion(String uniqueId, String localVersion, String channel);

    public Observable<Download> download(String url, String filePath, long fileSize, String md5);

}
