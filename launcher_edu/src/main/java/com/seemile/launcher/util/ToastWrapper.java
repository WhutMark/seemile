package com.seemile.launcher.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by whuthm on 2016/1/8.
 */
public class ToastWrapper extends AbsUtils {

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void show(String text) {
        show(text, LENGTH_SHORT);
    }

    public static void show(int resId) {
        if (sContext != null) {
            show(sContext.getString(resId));
        }
    }

    public static void show(String text, int length) {
        if (sContext == null) {
            Logger.e("ToastWrapper", "sContext = null");
            return;
        }
        Toast.makeText(sContext, text, length > 0 ? LENGTH_LONG : LENGTH_SHORT).show();
    }

}
