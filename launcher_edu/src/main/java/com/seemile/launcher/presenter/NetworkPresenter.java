package com.seemile.launcher.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.seemile.launcher.R;
import com.seemile.launcher.util.Logger;
import com.seemile.launcher.util.NetworkUtils;

/**
 * Created by whuthm on 2016/2/17.
 */
public class NetworkPresenter extends Presenter<NetworkPresenter.View> {

    private static final String TAG = "NetworkPresenter";

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(TAG, intent.getAction());
            showNetworkIcon();
        }
    };

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        view.getContext().registerReceiver(networkReceiver, filter);
        showNetworkIcon();
    }

    @Override
    public void onViewDetached() {
        view.getContext().unregisterReceiver(networkReceiver);
        super.onViewDetached();
    }

    private void showNetworkIcon() {
        NetworkInfo networkInfo = NetworkUtils.getActiveNetworkInfo();
        int resId;
        if (networkInfo != null) {
            final boolean isConnected = networkInfo.isConnected();
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                    resId = isConnected ? R.drawable.ic_ethernet_connected : R.drawable.ic_ethernet_unconnected;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                default:
                    resId = isConnected ? R.drawable.ic_wifi_connected : R.drawable.ic_wifi_unconnected;
                    break;
            }
        } else {
            resId = R.drawable.ic_wifi_unconnected;
        }
        if (view != null) {
            view.showNetworkIcon(resId);
        }
    }

    public interface View extends Presenter.View {
        void showNetworkIcon(int resId);
    }
}
