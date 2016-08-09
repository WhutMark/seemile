package com.screen.resolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView) findViewById(R.id.tv_test);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.e("MainActivity", dm.density + "  " + dm.densityDpi);
        Log.e("MainActivity", dm.widthPixels + "  " + dm.heightPixels);
        
        textView.setText(dm.density + "  " + dm.densityDpi + "  " + dm.widthPixels + "  "
                + dm.heightPixels);
    }
}
