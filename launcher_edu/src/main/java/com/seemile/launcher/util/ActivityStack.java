package com.seemile.launcher.util;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * Created by whuthm on 2015/12/14.
 * Activity stack
 */
public final class ActivityStack {

    private static Stack<WeakReference<Activity>> activityStack = new Stack<WeakReference<Activity>>();

    /**
     * 返回栈大小
     */
    public static int size() {
        return activityStack.size();
    }


    /**
     * 指定项出栈
     */
    public static void pop(Activity currentActivity) {
        if (currentActivity == null) {
            return;
        }
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            WeakReference<Activity> reference = activityStack.get(i);
            Activity activity = reference.get();
            if (activity == null) {
                activityStack.remove(reference);
                size--;
            } else {
                if (activity == currentActivity) {
                    activityStack.remove(reference);
                    break;
                }
            }
        }
    }

    /**
     * 所有项出栈
     */
    public static void popAll() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (activityStack.get(i) != null) {
                Activity activity = activityStack.get(i).get();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }

    /**
     * 新项入栈
     */
    public static void push(Activity activity) {
        activityStack.add(new WeakReference<Activity>(activity));
    }

}
