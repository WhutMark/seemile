package com.seemile.launcher;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.seemile.launcher.domain.update.SystemUpdateService;
import com.seemile.launcher.util.AbsUtils;

/**
 * Created by whuthm on 2016/2/4.
 */
public class App extends Application {

    static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        AbsUtils.init(this);
        SystemUpdateService.getInstance(this);
    }

    public static App with(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SystemUpdateService.getInstance(this).destroy();
    }
}
