package com.seemile.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.seemile.launcher.presenter.TimePresenter;
import com.seemile.launcher.util.FormatUtils;

import java.util.Date;

/**
 * Created by whuthm on 2016/2/4.
 */
public class TimeView extends TextView implements TimePresenter.View {

    TimePresenter presenter;

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        presenter = new TimePresenter();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.onViewAttached(this);
    }

    @Override
    public void setTime(long time) {
        setText(FormatUtils.DATE_TIME_FORMAT.format(new Date(time)));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.onViewDetached();
    }
}
