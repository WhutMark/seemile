package com.seemile.launcher.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seemile.launcher.R;
import com.seemile.launcher.util.Logger;

/**
 * Created by whuthm on 2016/2/4.
 */
public class ItemView extends RelativeLayout {

    protected TextView titleView;
    protected ImageView iconView;

    public ItemView(Context context) {
        super(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView = (TextView) findViewById(R.id.tv_title);
        iconView = (ImageView) findViewById(R.id.img_icon);
    }

    public void setTitle(String title) {
        if (titleView != null && !TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
    }

    public void setIcon(Drawable icon) {
        if (iconView != null && icon != null) {
            iconView.setImageDrawable(icon);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        Logger.i("ItemView", "onTouchEvent : " + event.getAction() + "  " + result);
        return result;
    }
}
