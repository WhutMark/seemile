package com.seemile.launcher.domain.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.RecoverySystem;
import android.text.TextUtils;

import com.seemile.launcher.domain.Download;
import com.seemile.launcher.domain.interactor.SystemUpdateInteractor;
import com.seemile.launcher.exception.NetworkError;
import com.seemile.launcher.util.ExecutorUtils;
import com.seemile.launcher.util.Logger;
import com.seemile.launcher.util.NetworkUtils;
import com.seemile.launcher.util.SystemUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

/**
 * Created by whuthm on 2016/2/17.
 */
public class SystemUpdateService {

    private static final String TAG = "SystemUpdateService";

    public static final String ROM_FILE_NAME = "rom.zip";

    private Context context;

    private static SystemUpdateService sInstance;

    private SystemUpdateLocalStore localStore;

    private UpdateInfo updateInfo;

    private final String localVersion;

    private boolean isChecking;

    private boolean isDownloading;

    private SystemUpdateInteractor interactor;

    private SystemUpdateService(Context context) {
        this.context = context.getApplicationContext();

        interactor = new SystemUpdateImpl();
        localStore = new SystemUpdateLocalStore(this.context);
        localVersion = SystemUtils.getVersion();

        updateInfo = localStore.getUpdateInfo();
        if (updateInfo != null) {
            Logger.i(TAG, updateInfo.toString());
        }
        if (updateInfo == null || !updateInfo.getLocalVersion().equals(localVersion)) {
            flushFileCache();
            updateInfo = UpdateFactory.createSystemUpdateInfo(localVersion);
            localStore.saveUpdateInfo(updateInfo);
        }

        Logger.i(TAG, "UpdateInfo init : " + updateInfo.toString());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.context.registerReceiver(networkReceiver, filter);


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startCheck();
            }
        };

        Timer timer = new Timer();

        timer.schedule(timerTask, 3 * 60 * 1000, 24 * 60 * 60 * 1000);
    }

    public static SystemUpdateService getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SystemUpdateService.class) {
                if (sInstance == null) {
                    sInstance = new SystemUpdateService(context);
                }
            }
        }
        return sInstance;
    }

    public void startCheck() {
        Logger.i(TAG, "startCheckVersion : " + isChecking);
        if (canCheck()) {
            isChecking = true;
            interactor.checkNewVersion(updateInfo.getUniqueId(), updateInfo.getLocalVersion(), SystemUtils.getChannel())
                    .subscribe(new Subscriber<Version>() {
                        @Override
                        public void onCompleted() {
                            isChecking = false;
                            Logger.i(TAG, "Check Completed");
                            startDownload();
                        }

                        @Override
                        public void onError(Throwable e) {
                            isChecking = false;
                            if (e instanceof NetworkError) {
                                NetworkError error = (NetworkError) e;
                                Logger.e(TAG, "Check Error : Code = " + error.getRspCode() + ", Message = " + error.getMessage());
                            } else {
                                Logger.e(TAG, "Check Error", e);
                            }
                        }

                        @Override
                        public void onNext(Version version) {
                            Logger.i(TAG, "Check Success Version = " + version.toString());
                            updateInfo.setVersion(version);
                            localStore.saveUpdateInfo(updateInfo);
                        }
                    });
        }
    }

    String getDownloadFilePath() {
        File fileDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            fileDir = new File(Environment.getExternalStorageDirectory(), "/seemile");
        } else {
            fileDir = new File("/data/seemile/");
        }
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return new File(fileDir, ROM_FILE_NAME).getPath();
    }

    void startDownload() {
        Logger.i(TAG, "startDownload : " + isDownloading);
        if (canDownload()) {
            isDownloading = true;
            final String downloadFileName = getDownloadFilePath();
            final String url = updateInfo.getVersion().getDownloadUrl();
            final long fileSize = updateInfo.getVersion().getSize();
            final String md5 = updateInfo.getVersion().getMd5();
            Logger.i(TAG, "startDownload : " + updateInfo.getVersion().toString());
            updateInfo.startDownload(downloadFileName, null);
            localStore.saveUpdateInfo(updateInfo);
            interactor.download(url, downloadFileName, fileSize, md5).subscribe(new Subscriber<Download>() {
                @Override
                public void onCompleted() {
                    isDownloading = false;
                    updateInfo.endDownload(true);
                    localStore.saveUpdateInfo(updateInfo);
                    Logger.i(TAG, "onDownloadProgress: onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    isDownloading = false;
                    Logger.e(TAG, "onDownloadProgress: error", e);
                }

                @Override
                public void onNext(Download download) {
                    Logger.i(TAG, "onDownloadProgress: progress=" + download.getProgress());
                }
            });
        }
    }

    public void startOnlineUpdate() {
        ExecutorUtils.newThread().execute(new Runnable() {
            @Override
            public void run() {
                if (checkUpdateInfo()) {
                    //不清除数据:下载的文件以及md5文件
                    final String romPath = updateInfo.getDownloadFileName();
                    updateInfo = UpdateFactory.createSystemUpdateInfo(localVersion);
                    localStore.saveUpdateInfo(updateInfo);
                    update(romPath);
                } else {
                    flushFileCache();
                    updateInfo = UpdateFactory.createSystemUpdateInfo(localVersion);
                    localStore.saveUpdateInfo(updateInfo);
                    startCheck();
                }
            }
        });
    }

    private boolean checkUpdateInfo() {
        return updateInfo.isDownloaded()
                && localVersion.equals(updateInfo.getLocalVersion())
                && new File(updateInfo.getDownloadFileName()).exists();
    }

    private void update(String romFilePath) {
        try {
            Logger.e(TAG, "update : " + romFilePath);
            RecoverySystem.installPackage(context, new File(romFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLocalUpdate(String romFilePath) {
        update(romFilePath);
    }

    private boolean canCheck() {
        return !isChecking && !updateInfo.isDownloaded();
    }

    private boolean canDownload() {
        final UpdateInfo info = updateInfo;
        Version version = info.getVersion();
        return !isDownloading
                && !info.isDownloaded()
                && version != Version.NONE
                && !info.getLocalVersion().equals(version.getServiceVersion());
    }

    private File getDownloadFile() {
        return new File(updateInfo != null && !TextUtils.isEmpty(updateInfo.getDownloadFileName()) ? updateInfo.getDownloadFileName() : "");
    }

    private void flushFileCache() {
        ExecutorUtils.newThread().execute(new Runnable() {
            @Override
            public void run() {
                getDownloadFile().delete();
            }
        });
    }

    public UpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i(TAG, intent.getAction());
            if (NetworkUtils.isNetworkConnected()) {
            }
        }
    };

    public void destroy() {
        context.unregisterReceiver(networkReceiver);
        context = null;
        sInstance = null;
    }

}
