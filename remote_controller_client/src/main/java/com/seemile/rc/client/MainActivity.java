package com.seemile.rc.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements KeyView.KeyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ((KeyView) findViewById(R.id.btn_enter_key)).setKeyListener(this);
        ((KeyView) findViewById(R.id.btn_left_key)).setKeyListener(this);
        ((KeyView) findViewById(R.id.btn_up_key)).setKeyListener(this);
        ((KeyView) findViewById(R.id.btn_right_key)).setKeyListener(this);
        ((KeyView) findViewById(R.id.btn_down_key)).setKeyListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RemoteControllerClient.getInstance().connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        RemoteControllerClient.getInstance().disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onKey(View v, KeyEvent event) {
        RemoteControllerClient.getInstance().post(event);
    }
}
