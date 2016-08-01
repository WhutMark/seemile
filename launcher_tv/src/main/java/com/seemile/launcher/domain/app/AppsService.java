package com.seemile.launcher.domain.app;

import android.content.ComponentName;
import android.content.Context;

import com.seemile.launcher.Constants;
import com.seemile.launcher.data.cache.IconCache;
import com.seemile.launcher.data.config.Theme;
import com.seemile.launcher.domain.interactor.AppInteractor;
import com.seemile.launcher.exception.AssetsFileReadException;
import com.seemile.launcher.util.ExecutorUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by whuthm on 2016/2/4.
 * Domain Service
 */
public class AppsService implements AppInteractor {


    private final Context mContext;
    private final AppsLocalStore mLocalStore;
    private final IconCache mIconCache;

    private Map<ComponentName, AppInfo> mAllApps = new ConcurrentHashMap<>();

    private static volatile AppsService sInstance;

    private AppsService(Context context) {
        mContext = context.getApplicationContext();
        mLocalStore = new AppsLocalStore(this);
        mIconCache = new IconCache(mContext);
        ExecutorUtils.newThread().execute(new Runnable() {
            @Override
            public void run() {
                List<AppInfo> appInfoList = mLocalStore.getAllApps(mContext, mIconCache);
                for (AppInfo appInfo : appInfoList) {
                    mAllApps.put(appInfo.componentName, appInfo);
                }
            }
        });
    }


    public static AppsService getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppsService.class) {
                if (sInstance == null) {
                    sInstance = new AppsService(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public Observable<List<AppItemInfo>> getGameAppList() {
        Observable<List<AppItemInfo>> observable = Observable.create(
                new Observable.OnSubscribe<List<AppItemInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<AppItemInfo>> sub) {
                        try {
                            List<AppItemInfo> appItemInfoList = mLocalStore.getAppItemList(mContext, Theme.getGamePagePath());
                            sub.onNext(appItemInfoList);
                            sub.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                            sub.onError(new AssetsFileReadException());
                        }
                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    public Observable<List<AppItemInfo>> getMoviesAppList() {
        Observable<List<AppItemInfo>> observable = Observable.create(
                new Observable.OnSubscribe<List<AppItemInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<AppItemInfo>> sub) {
                        try {
                            List<AppItemInfo> appItemInfoList = mLocalStore.getAppItemList(mContext, Theme.getMoviesPagePath());
                            sub.onNext(appItemInfoList);
                            sub.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                            sub.onError(new AssetsFileReadException());
                        }
                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    public Observable<List<AppItemInfo>> getAllAppList() {
        Observable<List<AppItemInfo>> observable = Observable.create(
                new Observable.OnSubscribe<List<AppItemInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<AppItemInfo>> sub) {

                    }
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    public Observable<List<AppItemInfo>> getAppListBy(int appType) {
        switch (appType) {
            case Constants.APP_TYPE_GAME:
                return getGameAppList();
            case Constants.APP_TYPE_MOVIES:
                return getMoviesAppList();
            default:
                return getAllAppList();
        }
    }

    public AppInfo getAppInfo(AppItemInfo itemInfo) {
        return mAllApps.get(new ComponentName(itemInfo.packageName, itemInfo.className));
    }

    public AppInfo getAppInfo(String packageName, String className) {
        return mAllApps.get(new ComponentName(packageName, className));
    }

    public AppInfo getAppInfo(ComponentName componentName) {
        return mAllApps.get(componentName);
    }

}
