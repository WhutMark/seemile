package com.seemile.launcher.util;

import java.util.concurrent.Executor;

/**
 * Created by whuthm on 2016/3/15.
 */
public class ExecutorUtils {

    private ExecutorUtils() {

    }

    public static Executor newThread() {
        return new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        };
    }



}
