package com.seemile.controller.client;

import android.util.Log;

import com.seemile.controller.RemoteController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by whuthm on 2016/4/11.
 */
public class RemoteControllerClient extends RemoteController {

    private static final String TAG = "RemoteControllerClient";

    private volatile static RemoteControllerClient sInstance;

    private BlockingQueue<Integer> mKeyCodeQueue = new LinkedBlockingDeque<>();

    private boolean mNeedConnect;

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

    private class ConnectionRunnable implements Runnable {

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket("192.168.5.101", PORT);
                mIsConnected = true;
                while (true) {
                    final int keyCode = mKeyCodeQueue.take();
                    OutputStream os = socket.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    ps.println(keyCode);
                    ps.flush();
                    ps.close();
                }
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            } catch (InterruptedException e) {
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
        if(!isConnected()) {
            runOnWorkThread(new ConnectionRunnable());
        } else {
            Log.e(TAG, "RemoteControllerServer is connected");
        }
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }
}
