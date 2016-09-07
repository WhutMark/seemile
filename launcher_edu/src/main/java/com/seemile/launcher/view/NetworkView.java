package com.seemile.launcher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.seemile.launcher.presenter.NetworkPresenter;

/**
 * Created by whuthm on 2016/2/17.
 */
public class NetworkView extends ImageView implements NetworkPresenter.View {

    NetworkPresenter presenter;

    public NetworkView(Context context) {
        this(context, null);
    }

    public NetworkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.presenter = new NetworkPresenter();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.onViewAttached(this);
    }

    @Override
    public void showNetworkIcon(int resId) {
        setImageResource(resId);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.onViewDetached();
    }

}
