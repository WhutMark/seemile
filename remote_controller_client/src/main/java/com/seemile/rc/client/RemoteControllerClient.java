package com.seemile.rc.client;

import android.util.Log;

import com.seemile.rc.RemoteController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by whuthm on 2016/4/11.
 */
public class RemoteControllerClient extends RemoteController {

    private static final String TAG = "RemoteControllerClient";

    private volatile static RemoteControllerClient sInstance;

    private BlockingQueue<Integer> mKeyCodeQueue = new ArrayBlockingQueue<>(100);

    private boolean mRepeatConnect;

    private Socket mSocket;

    private RemoteControllerClient() {
        super("RemoteControllerClient");
    }

    public static RemoteControllerClient getInstance() {
        if (sInstance == null) {
            synchronized (RemoteControllerClient.class) {
                if (sInstance == null) {
                    sInstance = new RemoteControllerClient();
                }
            }
        }
        return sInstance;
    }

    public void setRepeatConnect(boolean repeatConnect) {
        mRepeatConnect = repeatConnect;
    }

    private class ConnectionRunnable implements Runnable {

        @Override
        public void run() {
            try {
                mSocket = new Socket("127.0.0.1", PORT);
                mSocket.setKeepAlive(true);//开启保持活动状态的套接字
                //mSocket.setSoTimeout(5000);//设置超时时间
                //mSocket.sendUrgentData();
                mIsConnected = true;
                Log.i(TAG, "Connected");
                while (true) {
                    mSocket.sendUrgentData(15);
                    final int keyCode = mKeyCodeQueue.take();
                    Log.i(TAG, "post key : " + keyCode);
                    OutputStream  os = mSocket.getOutputStream();
                    //ps.close();
                }
            } catch (UnknownHostException e) {
                Log.w(TAG, e);
            } catch (IOException e) {
                Log.w(TAG, e);
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            } finally {
                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "disconnected");
                mIsConnected = false;
                runOnWorkThread(new ConnectionRunnable());
            }
        }
    }

    @Override
    protected void post(int keyCode) {
        mKeyCodeQueue.add(keyCode);
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            runOnWorkThread(new ConnectionRunnable());
        } else {
            Log.e(TAG, "RemoteControllerServer is connected");
        }
    }

    @Override
    public boolean isConnected() {
        return mIsConnected && mSocket != null && mSocket.isConnected();
    }

    @Override
    public void disconnect() {

    }
}
