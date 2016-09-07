package com.seemile.launcher.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.seemile.launcher.R;
import com.seemile.launcher.data.config.Theme;
import com.seemile.launcher.domain.app.AppsService;
import com.seemile.launcher.domain.home.HomeInfo;
import com.seemile.launcher.domain.home.HomePageInfo;
import com.seemile.launcher.domain.home.HomeService;
import com.seemile.launcher.presenter.SystemUpdatePresenter;
import com.seemile.launcher.util.AppUtils;
import com.seemile.launcher.util.Logger;
import com.seemile.launcher.view.CellLayout;
import com.seemile.launcher.view.ItemView;

import java.util.List;

import butterknife.Bind;
import rx.Subscriber;

public class LauncherActivity extends PresenterActivity<SystemUpdatePresenter.View, SystemUpdatePresenter>
        implements View.OnClickListener, SystemUpdatePresenter.View {

    private static final String TAG = "Launcher";

    @Bind(R.id.home_cell_layout)
    CellLayout mHomeCellLayout;

    private LayoutInflater mInflater;

    private Dialog mLocalUpdateDialog;

    private Dialog mOnlineUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppsService.getInstance(this);

        mInflater = LayoutInflater.from(this);

        mHomeCellLayout.setGridSize(Theme.getHomeCountX(), Theme.getHomeCountY());

        Subscriber<List<HomePageInfo>> subscriber = new Subscriber<List<HomePageInfo>>() {
            @Override
            public void onNext(List<HomePageInfo> pageList) {
                if (pageList != null && pageList.size() > 0) {
                    HomePageInfo homePageInfo = pageList.get(0);
                    addHomePageToCellLayout(homePageInfo);
                }
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
        HomeService.getInstance(this).getHomePageList().subscribe(subscriber);
    }

    private void addHomePageToCellLayout(HomePageInfo homePageInfo) {
        for (HomeInfo homeInfo : homePageInfo.itemList) {
            addHomeInfoToCellLayout(homeInfo);
        }
    }

    private void addHomeInfoToCellLayout(HomeInfo homeInfo) {
        ItemView itemView = (ItemView) mInflater.inflate(R.layout.item_home, null);
        itemView.setTitle(homeInfo.title);
        itemView.setIcon(homeInfo.getIcon(this));
        itemView.setTag(homeInfo);
        itemView.setOnClickListener(this);
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(homeInfo.cellX, homeInfo.cellY, homeInfo.spanX, homeInfo.spanY);
        mHomeCellLayout.addViewToCellLayout(itemView, lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.showOnlineUpdateIfNeeded();
    }

    @NonNull
    @Override
    protected SystemUpdatePresenter getPresenter() {
        return new SystemUpdatePresenter();
    }

    @NonNull
    @Override
    protected SystemUpdatePresenter.View getPresenterView() {
        return this;
    }

    @Override
    public void onClick(View v) {
        Logger.i(TAG, "onClick");
        if (v.getTag() instanceof HomeInfo) {
            AppUtils.startActivitySafely(this, ((HomeInfo) v.getTag()).getIntent());
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void showOnlineUpdate(String serviceVersion, String localVersion, String description) {
        hideDialog();
        CompatDialog.Builder builder = new CompatDialog.Builder(this, R.style.Theme_Dialog_Compat_Alert)
                .setTitle(getString(R.string.system_online_update_dlg_title, localVersion))
                .setIcon(R.drawable.ic_update)
                .setMessage(getString(R.string.system_online_update_dlg_msg, serviceVersion, description))
                .setPositiveButton(R.string.update_now_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.startOnlineUpdate();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.update_later_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        mOnlineUpdateDialog = builder.create();
        mOnlineUpdateDialog.show();
    }

    @Override
    public void hideOnlineUpdate() {
        if (mOnlineUpdateDialog != null) {
            mOnlineUpdateDialog.dismiss();
        }
    }

    @Override
    public void showLocalUpdate(final String filePath) {
        hideDialog();
        CompatDialog.Builder builder = new CompatDialog.Builder(this, R.style.Theme_Dialog_Compat_Alert)
                .setTitle(getString(R.string.system_local_update_dlg_title))
                .setIcon(R.drawable.ic_update)
                .setMessage(getString(R.string.system_local_update_dlg_msg))
                .setPositiveButton(R.string.update_now_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.startLocalUpdate(filePath);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.update_later_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        mLocalUpdateDialog = builder.create();
        mLocalUpdateDialog.show();
    }

    @Override
    public void hideLocalUpdate() {
        if (mLocalUpdateDialog != null) {
            mLocalUpdateDialog.dismiss();
        }
    }

    private void hideDialog() {
        hideOnlineUpdate();
        hideLocalUpdate();
    }
}
