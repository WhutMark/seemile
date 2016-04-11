package com.seemile.controller;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by whuthm on 2016/4/11.
 */
public abstract class RemoteController {

    public static final int PORT = 7000;

    private Handler mWorkHandler;
    private Handler mMainHandler;

    protected boolean mIsConnected;

    protected RemoteController(String name) {
        mMainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        mWorkHandler = new Handler(handlerThread.getLooper());
    }

    public abstract void connect();

    public boolean isConnected() {
        return mIsConnected;
    }

    public abstract void disconnect();

    //生产事件：用于客户端
    protected void post(int keyCode) {
    }

    //分发事件:用于服务端
    protected void delivery(int keyCode) {
    }

    protected final void runOnMainThread(Runnable r) {
        mMainHandler.post(r);
    }

    protected final void runOnWorkThread(Runnable r) {
        mWorkHandler.post(r);
    }
}
