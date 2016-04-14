package com.seemile.rc.server;

import android.util.Log;
import android.view.KeyEvent;

import com.seemile.rc.RemoteController;
import com.seemile.rc.util.ToastWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by whuthm on 2016/4/11.
 */
public class RemoteControllerServer extends RemoteController<KeyEvent> {

    private static final String TAG = "RemoteControllerServer";

    private volatile static RemoteControllerServer sInstance;

    private RemoteControllerServer() {
        new ConnectionThread().start();
        new UDPReceiver().start();
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

    class ConnectionThread extends Thread {

        private int mRestartCount;

        @Override
        public void run() {
            while (true) {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(PORT);
                    Log.i(TAG, "RemoteControllerServer : start");
                    while (true) {
                        Socket client = serverSocket.accept();
                        Log.i(TAG, "RemoteControllerServer : accept");
                        new WorkThread(client).start();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "RemoteControllerServer : disconnected!");
                } finally {
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                        }
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "RemoteControllerServer : restart! count = " + mRestartCount);
                    mRestartCount++;
                }
            }
        }
    }

    private class WorkThread extends Thread {

        private Socket socket;

        WorkThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    Log.i(TAG, "Read : " + tmp);
                    String[] parameters = tmp.split(",");
                    if (parameters != null && parameters.length >= 2) {
                        try {
                            delivery(new KeyEvent(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1])));
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "WorkThread", e);
                e.printStackTrace();
            } finally {
                Log.w(TAG, "WorkThread stop");
            }

        }
    }

    private class DeliveryRunnable implements Runnable {

        private final KeyEvent keyEvent;

        DeliveryRunnable(KeyEvent keyEvent) {
            this.keyEvent = keyEvent;
        }

        @Override
        public void run() {
            ToastWrapper.show("Delivery KeyAction : " + keyEvent.getAction() + ", KeyCode : " + keyEvent.getKeyCode());
        }
    }

    private class UDPReceiver extends Thread {

        @Override
        public void run() {
            while (true) {
                DatagramSocket datagramSocket = null;
                try {
                    byte[] buf = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    datagramSocket = new DatagramSocket(UDP_SERVER_PORT);
                    Log.i(TAG, "UDPReceiver : start");
                    while (true) {
                        datagramSocket.receive(receivePacket);

                        String broadcastSignal = new String(receivePacket.getData(), 0, receivePacket.getLength());

                        Log.i(TAG, "UDPReceiver : " +
                                broadcastSignal + "  " +
                                receivePacket.getAddress().getHostAddress() + "  " +
                                receivePacket.getAddress().getHostName());
                        if(SIGNAL_BROADCAST.equals(broadcastSignal)) {
                            final String feedback = SIGNAL_ACK;
                            byte[] feedbackBytes = feedback.getBytes();
                            DatagramPacket feedbackPacket = new DatagramPacket(feedbackBytes, 0,
                                    feedbackBytes.length, receivePacket.getSocketAddress());
                            datagramSocket.send(feedbackPacket);
                            Log.i(TAG, "UDPReceiver : feedback");
                        }
                    }
                } catch (SocketException e) {
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void delivery(KeyEvent keyEvent) {
        runOnMainThread(new DeliveryRunnable(keyEvent));
    }

    @Override
    public void connect() {
        //Nothing
    }

    @Override
    public synchronized void disconnect() {
        //Nothing
    }
}
