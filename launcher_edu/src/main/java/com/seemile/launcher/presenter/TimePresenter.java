package com.seemile.launcher.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.seemile.launcher.util.Logger;

/**
 * Created by whuthm on 2016/2/4.
 */
public class TimePresenter extends Presenter<TimePresenter.View> {

    private static final String TAG = "TimePresenter";

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            view.setTime(System.currentTimeMillis());
            Logger.i(TAG, intent.getAction());
        }
    };

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        view.setTime(System.currentTimeMillis());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        view.getContext().registerReceiver(timeReceiver, filter);
    }

    @Override
    public void onViewDetached() {
        view.getContext().unregisterReceiver(timeReceiver);
        super.onViewDetached();
    }

    public interface View extends Presenter.View {

        void setTime(long time);

    }
}
