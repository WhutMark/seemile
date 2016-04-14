package com.seemile.rc.client;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by whuthm on 2016/4/14.
 */
public class KeyView extends Button {

    private int mKeyCode;

    private KeyListener mKeyListener;

    public KeyView(Context context) {
        this(context, null);
    }

    public KeyView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public KeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyView);
        mKeyCode = a.getInt(R.styleable.KeyView_keyCode, KeyEvent.KEYCODE_UNKNOWN);
        a.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        int action = event.getAction();
        int keyAction;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                keyAction = KeyEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                keyAction = KeyEvent.ACTION_DOWN;
                return result;
            case MotionEvent.ACTION_UP:
                keyAction = KeyEvent.ACTION_UP;
                break;
            default:
                return result;
        }

        if(mKeyCode != KeyEvent.KEYCODE_UNKNOWN && mKeyListener != null) {
            mKeyListener.onKey(this, new KeyEvent(keyAction, mKeyCode));
        }
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mKeyListener = null;
    }

    public void setKeyListener(KeyListener l) {
        mKeyListener = l;
    }

    public interface KeyListener {
        void onKey(View v, KeyEvent event);
    }
}
