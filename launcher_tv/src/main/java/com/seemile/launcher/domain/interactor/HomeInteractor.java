package com.seemile.launcher.domain.interactor;

import com.seemile.launcher.domain.home.HomePageInfo;
import java.util.List;
import rx.Observable;

/**
 * Created by whuthm on 2016/3/15.
 */
public interface HomeInteractor {

    Observable<List<HomePageInfo>> getHomePageList();
}
