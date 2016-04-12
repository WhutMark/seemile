package com.seemile.rc.server;

import android.util.Log;

import com.seemile.rc.RemoteController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by whuthm on 2016/4/11.
 */
public class RemoteControllerServer extends RemoteController {

    private static final String TAG = "RemoteControllerServer";

    private volatile static RemoteControllerServer sInstance;

    private ServerSocket mServerSocket;

    private RemoteControllerServer() {
        super("RemoteControllerServer");
    }

    public static RemoteControllerServer getInstance() {
        if (sInstance == null) {
            synchronized (RemoteControllerServer.class) {
                if (sInstance == null) {
                    sInstance = new RemoteControllerServer();
                    sInstance.connect();
                }
            }
        }
        return sInstance;
    }

    class ConnectionRunnable implements Runnable {

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(PORT);
                mIsConnected = true;
                Log.i(TAG, "RemoteControllerServer : connected!");
                while (true) {
                    Socket client = mServerSocket.accept();
                    InputStream ins = client.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ins));
                    final String tmp = br.readLine();
                    try {
                        delivery(Integer.parseInt(tmp));
                    } catch (NumberFormatException e) {
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "RemoteControllerServer : disconnected!");
            } finally {
                if (mServerSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e) {
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mIsConnected = false;
                runOnWorkThread(new ConnectionRunnable());
            }
        }
    }

    class DeliveryRunnable implements Runnable {

        private final int keyCode;

        DeliveryRunnable(int keyCode) {
            this.keyCode = keyCode;
        }

        @Override
        public void run() {
            Log.i(TAG, "Delivery key : " + keyCode);
        }
    }

    @Override
    protected void delivery(int keyCode) {
        runOnMainThread(new DeliveryRunnable(keyCode));
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
    public synchronized void disconnect() {
        //Nothing
    }
}
