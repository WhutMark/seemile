package com.seemile.rc.client;

import android.util.Log;
import android.view.KeyEvent;

import com.seemile.rc.RemoteController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by whuthm on 2016/4/11.
 */
public class RemoteControllerClient extends RemoteController<KeyEvent> {

    private static final String TAG = "RemoteControllerClient";

    private volatile static RemoteControllerClient sInstance;

    private BlockingQueue<KeyEvent> mKeyEventQueue = new LinkedBlockingDeque<>();

    private Socket mSocket;

    private boolean mTryStopConn = true;

    private RemoteControllerClient() {
        //Empty
        new ConnectionThread().start();
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

    /**
     * 心跳检测
     */
    private class ConnectionThread extends Thread {

        @Override
        public void run() {
            while (true) {
                boolean mNeedStop = false;
                synchronized (mConnLock) {
                    if (mTryStopConn) {
                        Log.i(TAG, "Connection mTryStopConn = " + mTryStopConn);
                        mNeedStop = true;
                    }
                }
                if (mNeedStop) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                OutputStream os = null;
                InputStream is = null;
                Thread readThread = null;
                Thread writeThread = null;
                try {
                    //mSocket = new Socket("127.0.0.1", PORT);
                    mSocket = new Socket("172.22.19.13", PORT);
                    mSocket.setSoTimeout(0);
                    mSocket.setKeepAlive(true);//开启保持活动状态的套接字
                    //mSocket.setSoTimeout(5000);//设置超时时间
                    Log.i(TAG, "Connection Started");
                    os = mSocket.getOutputStream();
                    is = mSocket.getInputStream();
                    readThread = new ReadThread(is);
                    writeThread = new WriteThread(os);
                    readThread.start();
                    writeThread.start();
                    while (true) {
                        synchronized (mConnLock) {
                            if (mTryStopConn) {
                                break;
                            }
                        }
                        mSocket.sendUrgentData(0xFF);
                        setConnected(true);
                        Thread.sleep(4000);
                    }
                } catch (UnknownHostException e) {
                    Log.w(TAG, e);
                } catch (IOException e) {
                    Log.w(TAG, e);
                } catch (InterruptedException e) {
                    Log.w(TAG, e);
                } finally {
                    setConnected(false);
                    Log.i(TAG, "disconnected");
                    //----------------------------------------------------------------------------
                    //先关闭流再关闭套接字
                    //----------------------------------------------------------------------------
                    if (readThread != null) {
                        readThread.interrupt();
                    }
                    if (writeThread != null) {
                        writeThread.interrupt();
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "restart");
                }
            }
        }
    }

    private class ReadThread extends Thread {
        private InputStream is;

        ReadThread(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            //Empty
            try {

            } finally {
                is = null;
            }
        }
    }

    private class WriteThread extends Thread {

        private OutputStream os;

        WriteThread(OutputStream os) {
            this.os = os;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Log.i(TAG, "start WriteThread");
                    final KeyEvent keyEvent = mKeyEventQueue.take();
                    final int action = keyEvent.getAction();
                    final int keyCode = keyEvent.getKeyCode();
                    PrintWriter pw = new PrintWriter(os);
                    String content = new StringBuilder().append(action).append(",").append(keyCode).toString();
                    Log.i(TAG, "post : " + content);
                    pw.println(content);
                    pw.flush();//必须要写这一句
                }
            } catch (Exception e) {
                Log.w(TAG, "WriteThread", e);
            } finally {
                os = null;
            }
        }
    }


    @Override
    protected void post(KeyEvent t) {
        mKeyEventQueue.offer(t);
    }

    @Override
    public void connect() {
        synchronized (mConnLock) {
            mTryStopConn = false;
        }
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {
        synchronized (mConnLock) {
            mTryStopConn = true;
        }
    }
}
