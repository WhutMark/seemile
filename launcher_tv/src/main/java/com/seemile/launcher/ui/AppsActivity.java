package com.seemile.launcher.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.seemile.launcher.Constants;
import com.seemile.launcher.R;
import com.seemile.launcher.domain.app.AppInfo;
import com.seemile.launcher.domain.app.AppItemInfo;
import com.seemile.launcher.domain.app.AppsService;
import com.seemile.launcher.util.AppUtils;
import com.seemile.launcher.util.Logger;
import com.seemile.launcher.view.CellLayout;
import com.seemile.launcher.view.ItemView;

import java.util.List;

import butterknife.Bind;
import rx.Subscriber;

/**
 * Created by whuthm on 2016/4/11.
 */
public class AppsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AppsActivity";

    @Bind(R.id.app_cell_layout)
    CellLayout mAppCellLayout;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(this);

        int appType = getIntent() != null ? getIntent().getIntExtra(Constants.APP_TYPE, 0) : 0;

        Subscriber<List<AppItemInfo>> subscriber = new Subscriber<List<AppItemInfo>>() {
            @Override
            public void onNext(List<AppItemInfo> itemInfoList) {
                if (itemInfoList != null) {
                    for (AppItemInfo info : itemInfoList) {
                        addAppItemToCellLayout(info);
                    }
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
        AppsService.getInstance(this).getAppListBy(appType).subscribe(subscriber);
    }

    void addAppItemToCellLayout(AppItemInfo itemInfo) {
        ItemView itemView = (ItemView) mInflater.inflate(R.layout.item_app, null);
        AppInfo appInfo = itemInfo.getAppInfo();
        if (appInfo != null) {
            itemView.setTitle(appInfo.title);
            itemView.setIcon(appInfo.iconBitmap);
            itemView.setBackgroundDrawable(AppUtils.getBackground(appInfo));
            itemView.setTag(itemInfo);
            itemView.setOnClickListener(this);
            CellLayout.LayoutParams lp = new CellLayout.LayoutParams(itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
            mAppCellLayout.addViewToCellLayout(itemView, lp);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apps;
    }

    @Override
    public void onClick(View v) {
        Logger.i(TAG, "onClick");
        if (v.getTag() instanceof AppItemInfo) {
            AppItemInfo itemInfo = (AppItemInfo) v.getTag();
            if (itemInfo.getAppInfo() != null) {
                AppUtils.startActivitySafely(this, itemInfo.getAppInfo().getIntent());
            }
        }
    }
}
