package com.seemile.launcher.domain.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.seemile.launcher.R;
import com.seemile.launcher.domain.ItemInfo;
import com.seemile.launcher.util.AppUtils;
import com.seemile.launcher.util.AssetsUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by whuthm on 2016/2/4.
 */
public class HomeInfo extends ItemInfo {

    public String bgUrl;
    public String iconUrl;
    public String url;

    private Entry entry = new Entry();


    public Drawable getIcon(Context context) {
        boolean isIconUrlEmpty = TextUtils.isEmpty(iconUrl);
        if (!TextUtils.equals(entry.key, iconUrl) && !isIconUrlEmpty) {
            entry.key = iconUrl;
            final Resources res = context.getResources();
            if (iconUrl.startsWith("assets://")) {
                String name = iconUrl.substring("assets://".length(), iconUrl.length());
                try {
                    entry.icon = AssetsUtils.readAsDrawable(context, name);
                } catch (IOException e) {
                    entry.icon = null;
                    e.printStackTrace();
                }

            } else if (iconUrl.startsWith("drawable://")) {
                String name = iconUrl.substring("drawable://".length(), iconUrl.length());
                int resId = res.getIdentifier(name, "drawable", context.getPackageName());
                try {
                    entry.icon = res.getDrawable(resId);
                } catch (Resources.NotFoundException e) {
                    entry.icon = null;
                    e.printStackTrace();
                }
            }
        } else if (isIconUrlEmpty) {
            entry.icon = getDefaultIcon(context);
        }
        if (entry.icon == null) {
            entry.icon = getDefaultIcon(context);
        }
        return entry.icon;
    }

    public Intent getIntent() {
        if (!TextUtils.isEmpty(url) && url.startsWith("app://")) {
            String name = url.substring("app://".length(), url.length());
            String[] components = name.split("/");
            if (components != null) {
                if (components.length > 1) {
                    return getActivity(new ComponentName(components[0], components[1]));
                } else if (components.length > 0) {
                    String packageName = components[0];
                    List<ResolveInfo> resolveInfoList = AppUtils.getAppLauncherInfo(packageName);
                    if (resolveInfoList != null && resolveInfoList.size() > 0) {
                        ResolveInfo info = resolveInfoList.get(0);
                        return getMainActivity(new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
                    }
                }
            }
        }
        return new Intent();
    }

    final Intent getMainActivity(ComponentName componentName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return intent;
    }

    final Intent getActivity(ComponentName componentName) {
        Intent intent = new Intent();
        intent.setComponent(componentName);
        //启动同一个应用内的Activity不要使用FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static Drawable getDefaultIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_default_app);
    }

    private static class Entry {
        public Drawable icon;
        public String key;
    }
}
