package com.seemile.rc.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RemoteControllerClient.getInstance().connect();

        findViewById(R.id.btn_enter_key).setOnClickListener(this);
        findViewById(R.id.btn_left_key).setOnClickListener(this);
        findViewById(R.id.btn_up_key).setOnClickListener(this);
        findViewById(R.id.btn_right_key).setOnClickListener(this);
        findViewById(R.id.btn_down_key).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int keyCode;
        switch (id) {
            case R.id.btn_enter_key:
                keyCode = 0;
                break;
            case R.id.btn_left_key:
                keyCode = 1;
                break;
            case R.id.btn_up_key:
                keyCode = 2;
                break;
            case R.id.btn_right_key:
                keyCode = 3;
                break;
            case R.id.btn_down_key:
                keyCode = 4;
                break;
            default:
                return;
        }
        RemoteControllerClient.getInstance().post(keyCode);
    }
}
