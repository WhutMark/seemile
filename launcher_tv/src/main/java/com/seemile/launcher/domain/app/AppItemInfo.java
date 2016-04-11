package com.seemile.launcher.domain.app;

import com.seemile.launcher.domain.ItemInfo;

import java.lang.ref.WeakReference;

/**
 * Created by whuthm on 2016/4/11.
 */
public class AppItemInfo extends ItemInfo {

    public String packageName;

    public String className;

    WeakReference<AppInfo> appInfoReference;

    public AppInfo getAppInfo() {
        return appInfoReference != null ? appInfoReference.get() : null;
    }

    public void setAppInfo(AppInfo appInfo) {
        appInfoReference = new WeakReference<AppInfo>(appInfo);
    }
}
