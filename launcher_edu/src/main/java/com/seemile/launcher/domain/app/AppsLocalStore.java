package com.seemile.launcher.domain.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.google.gson.stream.JsonReader;
import com.seemile.launcher.data.cache.IconCache;
import com.seemile.launcher.util.AppUtils;
import com.seemile.launcher.util.AssetsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whuthm on 2016/4/11.
 */
class AppsLocalStore {

    AppsService mService;

    AppsLocalStore(AppsService service) {
        mService = service;
    }

    public List<AppItemInfo> getAppItemList(Context context, String assetsPath) throws IOException {
        JsonReader reader = new JsonReader(AssetsUtils.readAsStreamReader(context, assetsPath));
        reader.beginObject();
        List<AppItemInfo> appItemInfoList = new ArrayList<AppItemInfo>();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("items".equals(name)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    //array节点
                    AppItemInfo itemInfo = new AppItemInfo();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if ("id".equals(name)) {
                            itemInfo.id = reader.nextLong();
                        } else if ("title".equals(name)) {
                            itemInfo.title = reader.nextString();
                        } else if ("itemType".equals(name)) {
                            itemInfo.itemType = reader.nextInt();
                        } else if ("screen".equals(name)) {
                            itemInfo.screen = reader.nextInt();
                        } else if ("container".equals(name)) {
                            itemInfo.container = reader.nextLong();
                        } else if ("cellX".equals(name)) {
                            itemInfo.cellX = reader.nextInt();
                        } else if ("cellY".equals(name)) {
                            itemInfo.cellY = reader.nextInt();
                        } else if ("spanX".equals(name)) {
                            itemInfo.spanX = reader.nextInt();
                        } else if ("spanY".equals(name)) {
                            itemInfo.spanY = reader.nextInt();
                        } else if ("packageName".equals(name)) {
                            itemInfo.packageName = reader.nextString();
                        } else if ("className".equals(name)) {
                            itemInfo.className = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    itemInfo.setAppInfo(mService.getAppInfo(itemInfo));
                    appItemInfoList.add(itemInfo);
                    reader.endObject();
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return appItemInfoList;
    }

    public List<AppInfo> getAllApps(Context context, IconCache iconCache) {
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = AppUtils.getAllAppLauncherInfo();
        if(resolveInfoList != null) {
            for(ResolveInfo resolveInfo : resolveInfoList) {
                AppInfo appInfo = new AppInfo(pm, resolveInfo);
                iconCache.getTitleAndIcon(appInfo, resolveInfo);
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

}
