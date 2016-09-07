package com.seemile.launcher.domain.interactor;

import com.seemile.launcher.domain.app.AppItemInfo;

import java.util.List;

import rx.Observable;

/**
 * Created by whuthm on 2016/4/11.
 */
public interface AppInteractor {

    Observable<List<AppItemInfo>> getGameAppList();

    Observable<List<AppItemInfo>> getMoviesAppList();

    Observable<List<AppItemInfo>> getAllAppList();

    Observable<List<AppItemInfo>> getAppListBy(int appType);
}
