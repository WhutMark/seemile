package com.seemile.rc.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 使用时需要添加网络权限
 */
public class NetworkUtils extends AbsUtils {

    private NetworkUtils() {
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }

    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return info != null && info.isConnected();
        }
        return false;
    }

    public static boolean isMobileConnected() {
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return info != null && info.isConnected();
        }
        return false;
    }

    public static boolean isEthernetConnected() {
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            return info != null && info.isConnected();
        }
        return false;
    }


    public static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) sContext
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

}
