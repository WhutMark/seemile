package com.seemile.launcher.domain.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by whuthm on 2016/1/7.
 */
public class AppInfo {

    private static final String TAG = "AppInfo";

    public static final int DOWNLOADED_FLAG = 1;
    public static final int UPDATED_SYSTEM_APP_FLAG = 2;

    /**
     * 实体的Unique Id
     */
    public ComponentName componentName;

    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * A bitmap version of the application icon.
     */
    public Bitmap iconBitmap;

    public int flags;

    public String title;

    /**
     * Must not hold the Context.
     */
    public AppInfo(PackageManager pm, ResolveInfo info) {
        final String packageName = info.activityInfo.applicationInfo.packageName;

        this.componentName = new ComponentName(packageName,
                info.activityInfo.name);
        this.setActivity(componentName, Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            int appFlags = pm.getApplicationInfo(packageName, 0).flags;
            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
                flags |= DOWNLOADED_FLAG;

                if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    flags |= UPDATED_SYSTEM_APP_FLAG;
                }
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "PackageManager.getApplicationInfo failed for "
                    + packageName);
        }
    }

    public AppInfo(AppInfo info) {
        iconBitmap = info.iconBitmap;
        componentName = info.componentName;
        title = info.title;
        intent = new Intent(info.intent);
        flags = info.flags;
    }

    /**
     * Returns the package name that the shortcut's intent will resolve to, or
     * an empty string if none exists.
     */
    public String getPackageName() {
        if (intent != null) {
            String packageName = null;
            if (intent.getComponent() != null) {
                packageName = intent.getComponent().getPackageName();
            }
            if (packageName != null) {
                return packageName;
            }
        }
        return "";
    }

    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }

    @Override
    public String toString() {
        return "AppInfo(title=" + title.toString() + ")";
    }

    public ComponentName getComponentName() {
        return new ComponentName(componentName.getPackageName(),
                componentName.getClassName());
    }

    public Intent getIntent() {
        return new Intent(intent);
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public int getFlags() {
        return flags;
    }

    public String getTitle() {
        return title;
    }

}
