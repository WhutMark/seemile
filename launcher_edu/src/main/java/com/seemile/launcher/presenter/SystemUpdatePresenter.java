package com.seemile.launcher.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.seemile.launcher.domain.update.SystemUpdateService;
import com.seemile.launcher.domain.update.UpdateInfo;
import com.seemile.launcher.util.Logger;
import com.seemile.launcher.util.StorageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by whuthm on 2016/3/15.
 */
public class SystemUpdatePresenter extends Presenter<SystemUpdatePresenter.View> {

    private SystemUpdateService mSystemUpdateService;

    private static final String TAG = "SystemLocalUpdatePresenter";

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(TAG, intent.getAction());
            showLocalUpdateIfNeeded();
        }
    };

    private void showLocalUpdateIfNeeded() {
        List<String> mountedPaths = StorageUtils.getAllMountedPathsWithoutSDCard(view.getContext());
        for (String path : mountedPaths) {
            File romFile = new File(path, SystemUpdateService.ROM_FILE_NAME);
            Logger.i(TAG, "showLocalUpdateIfNeeded : " + romFile.getPath() + " " + romFile.exists());
            if (romFile.exists()) {
                if (view != null) {
                    view.showLocalUpdate(romFile.getPath());
                }
                return;
            }
        }
        if (view != null) {
            view.hideLocalUpdate();
        }
    }

    public void showOnlineUpdateIfNeeded() {
        UpdateInfo updateInfo = mSystemUpdateService.getUpdateInfo();
        if (updateInfo.isDownloaded()) {
            view.showOnlineUpdate(
                    updateInfo.getVersion().getServiceVersion(),
                    updateInfo.getLocalVersion(),
                    updateInfo.getVersion().getDescription());
        }
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);
        Logger.i(TAG, "onViewAttached registerReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        view.getContext().registerReceiver(usbReceiver, filter);

        showLocalUpdateIfNeeded();

        mSystemUpdateService = SystemUpdateService.getInstance(view.getContext());
    }

    @Override
    public void onViewDetached() {
        view.getContext().unregisterReceiver(usbReceiver);
        super.onViewDetached();
    }

    public void startLocalUpdate(String filePath) {
        mSystemUpdateService.startLocalUpdate(filePath);
    }

    public void startOnlineUpdate() {
        mSystemUpdateService.startOnlineUpdate();
    }


    public interface View extends Presenter.View {

        public void showOnlineUpdate(String serviceVersion, String localVersion, String description);

        public void hideOnlineUpdate();

        public void showLocalUpdate(String filePath);

        public void hideLocalUpdate();

    }

}
