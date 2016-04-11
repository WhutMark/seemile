package com.seemile.launcher.util;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.seemile.launcher.R;
import com.seemile.launcher.domain.app.AppInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by whuthm on 2016/1/7.
 */
public class AppUtils extends AbsUtils {

    private volatile static PackageManager sPM;

    public static boolean isMainProcess(Context context) {
        boolean result = true;
        String processName = getProcessName(context);
        if (processName != null) {
            result = processName.equals(context.getPackageName());
        }
        return result;
    }

    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public static void installApk(Context context, String apkFilePath) {
        String command = "chmod 777 " + apkFilePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivitySafely(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastWrapper.show(R.string.activity_not_found);
        } catch (SecurityException e) {
            ToastWrapper.show(R.string.activity_not_found);
        }
    }

    public static List<ResolveInfo> getAppLauncherInfo(String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        return getResolveInfoList(intent);
    }

    public static List<ResolveInfo> getAllAppLauncherInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getResolveInfoList(intent);
    }

    public static Drawable getBackground(AppInfo info) {
        int color;
        int mod = info != null && info.title != null ? info.title.length() / 4 : 0;
        switch (mod) {
            case 1:
                color = R.color.pink;
                break;
            case 2:
                color = R.color.blue;
                break;
            case 3:
                color = R.color.green;
                break;
            default:
                color = R.color.purple;
                break;
        }
        return new ColorDrawable(sContext.getResources().getColor(color));
    }


    static List<ResolveInfo> getResolveInfoList(Intent intent) {
        ensurePackageManager();
        return sPM.queryIntentActivities(intent, 0);
    }


    private static void ensurePackageManager() {
        if (sPM == null) {
            synchronized (PackageManager.class) {
                if (sPM == null) {
                    sPM = sContext.getPackageManager();
                }
            }
        }
    }

}
