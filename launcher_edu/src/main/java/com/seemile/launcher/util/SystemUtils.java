package com.seemile.launcher.util;

import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by whuthm on 2016/4/11.
 */
public class SystemUtils {

    private static final String TAG = "SystemUtils";

    private static final String UNKNOWN = "unknown";

    private static final String CHANNEL = "edu";

    public static String getVersion() {
        //return "1.4";
        return getPropertyValue("ro.vendor.sw.version", UNKNOWN);
    }

    public static String getChannel() {
        return CHANNEL;
    }


    public static final String getPropertyValue(String propertyName, String defaultValue) {
        String value;
        try {
            Method method = Class.forName("android.os.Build").getDeclaredMethod(
                    "getString", new Class[]{Class.forName("java.lang.String")});
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            value = (String) method.invoke(new Build(), propertyName);
        } catch (ClassNotFoundException e) {
            Logger.w(TAG, "getPropertyValue propertyName = " + propertyName, e);
            return defaultValue;
        } catch (NoSuchMethodException e) {
            Logger.w(TAG, "getPropertyValue propertyName = " + propertyName, e);
            return defaultValue;
        } catch (InvocationTargetException e) {
            Logger.w(TAG, "getPropertyValue propertyName = " + propertyName, e);
            return defaultValue;
        } catch (IllegalAccessException e) {
            Logger.w(TAG, "getPropertyValue propertyName = " + propertyName, e);
            return defaultValue;
        } catch (ClassCastException e) {
            Logger.w(TAG, "getPropertyValue propertyName = " + propertyName, e);
            return defaultValue;
        }
        return value;
    }

}
