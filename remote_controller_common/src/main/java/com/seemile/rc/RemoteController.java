package com.seemile.rc;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by whuthm on 2016/4/11.
 */
public abstract class RemoteController<T> {

    protected static final int PORT = 7000;

    protected static final int UDP_SERVER_PORT = 7001;

    protected static final int UDP_CLIENT_PORT = 7002;

    protected static String SIGNAL_BROADCAST = "Broadcast";

    protected static String SIGNAL_ACK = "ACK";

    private Handler mMainHandler;

    protected boolean mConnected;

    protected Object mConnLock = new Object();

    protected WeakReference<RemoteControllerConnection> mRCConnectionRef;

    protected RemoteController() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public abstract void connect();

    public boolean isConnected() {
        synchronized (mConnLock) {
            return mConnected;
        }
    }

    protected void setConnected(boolean isConnected) {
        boolean notify;
        synchronized (mConnLock) {
            notify = mConnected != isConnected;
            mConnected = isConnected;
        }
        if (notify) {
            notifyConnectionChanged(isConnected);
        }
    }

    public abstract void disconnect();

    //生产事件：用于客户端
    protected void post(T t) {
    }

    //分发事件:用于服务端
    protected void delivery(T t) {
    }

    protected final void runOnMainThread(Runnable r) {
        mMainHandler.post(r);
    }

    public void setRemoteControllerConnection(RemoteControllerConnection connection) {
        mRCConnectionRef = new WeakReference<RemoteControllerConnection>(connection);
    }

    protected void notifyConnectionChanged(final boolean isConnected) {
        final RemoteControllerConnection connection = mRCConnectionRef != null ? mRCConnectionRef.get() : null;
        if (connection == null) {
            return;
        }
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    connection.onConnected();
                } else {
                    connection.onDisconnected();
                }
            }
        });
    }

}
