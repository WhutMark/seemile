package com.seemile.launcher.domain.home;

import android.content.Context;

import com.seemile.launcher.domain.interactor.HomeInteractor;
import com.seemile.launcher.exception.AssetsFileReadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by whuthm on 2016/2/4.
 */
public class HomeService implements HomeInteractor {

    private static HomeService sInstance;

    private Context context;
    private HomeLocalStore localStore;

    private List<HomePageInfo> pageList = new ArrayList<HomePageInfo>(0);

    private HomeService(Context context) {
        this.context = context.getApplicationContext();
        localStore = new HomeLocalStore();
    }

    public static HomeService getInstance(Context context) {
        if (sInstance == null) {
            synchronized (HomeService.class) {
                if (sInstance == null) {
                    sInstance = new HomeService(context);
                }
            }
        }
        return sInstance;
    }

    public Observable<List<HomePageInfo>> getHomePageList() {
        return getHomeInfoListFromLocal();
    }

    private Observable<List<HomePageInfo>> getHomeInfoListFromLocal() {
        Observable<List<HomePageInfo>> observable = Observable.create(
                new Observable.OnSubscribe<List<HomePageInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<HomePageInfo>> sub) {
                        try {
                            pageList.clear();
                            pageList = localStore.getHomePageList(context);
                            sub.onNext(pageList);
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

}
