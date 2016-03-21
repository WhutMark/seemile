package com.seemile.launcher.util;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.seemile.launcher.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by whuthm on 2016/1/7.
 */
public class AppUtils {

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

}
