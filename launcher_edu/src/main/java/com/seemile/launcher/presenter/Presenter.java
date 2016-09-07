package com.seemile.launcher.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.seemile.launcher.App;

/**
 * Created by whuthm on 2016/3/8.
 */
public class Presenter<V extends Presenter.View> {

    protected V view;

    public void onViewAttached(@NonNull V view) {
        if (this.view != null) {
            throw new IllegalStateException("View " + this.view + " is already attached. Cannot attach " + view);
        }
        this.view = view;
    }

    public void onViewDetached() {
        if (view == null) {
            throw new IllegalStateException("View is already detached.");
        }
        this.view = null;
    }

    public interface View {
        @NonNull
        public Context getContext();
    }

    public static Presenter<View> nullPresenter() {
        return new Presenter<View>();
    }

    public static View nullView() {
        return new View() {

            @NonNull
            @Override
            public Context getContext() {
                return App.getContext();
            }
        };
    }
}
