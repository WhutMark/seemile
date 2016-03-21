package com.seemile.launcher.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.seemile.launcher.util.ActivityStack;

import butterknife.ButterKnife;

/**
 * Created by whuthm on 2015/12/14.
 * BaseActivity : 必须使用主题BaseTheme
 */
public abstract class BaseActivity extends Activity {

    private boolean mAttached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttached = true;
        ActivityStack.push(this);
        setContentView(getLayoutId());
        //必须在setContentView之后
        ButterKnife.bind(this);
    }


    //操作UI时先判断是否Attached
    public boolean isAttached() {
        return mAttached;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.pop(this);
        ButterKnife.unbind(this);
        mAttached = false;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @NonNull
    public Context getContext() {
        return this;
    }
}
