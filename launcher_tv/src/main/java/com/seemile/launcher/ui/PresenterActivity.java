package com.seemile.launcher.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.seemile.launcher.presenter.Presenter;

/**
 * Created by whuthm on 2016/3/9.
 */
public abstract class PresenterActivity<V extends Presenter.View, P extends Presenter<V>> extends BaseActivity {

    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getPresenter();
        mPresenter.onViewAttached(getPresenterView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDetached();
    }

    @NonNull
    protected abstract P getPresenter();

    @NonNull
    protected abstract V getPresenterView();

}
