package com.seemile.launcher.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.seemile.launcher.presenter.Presenter;


/**
 * Created by whuthm on 2016/3/9.
 */
public abstract class PresenterFragment<V extends Presenter.View, P extends Presenter<V>> extends BaseFragment {

    protected P mPresenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = getPresenter();
        mPresenter.onViewAttached(getPresenterView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onViewDetached();
    }

    @NonNull
    protected abstract P getPresenter();

    @NonNull
    protected abstract V getPresenterView();
}
