package com.seemile.launcher.domain.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.seemile.launcher.R;
import com.seemile.launcher.domain.ItemInfo;
import com.seemile.launcher.util.AssetsUtils;

import java.io.IOException;

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
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(url) && url.startsWith("app://")) {
            String name = url.substring("app://".length(), url.length());
            String[] components = name.split("/");
            if (components != null) {
                if (components.length > 1) {
                    intent.setClassName(components[0], components[1]);
                } else if (components.length > 0) {
                    intent.setPackage(components[0]);
                }
            }
        }
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
