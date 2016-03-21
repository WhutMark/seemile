package com.seemile.launcher.domain.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by whuthm on 2016/2/17.
 */
class SystemUpdateLocalStore {

    private static final String SP_NAME = "system_update";
    private static final String KEY_LOCAL_VERSION = "local_version";
    private static final String KEY_SERVICE_VERSION = "service_version";
    private static final String KEY_DOWNLOAD_URL = "download_url";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_MD5 = "md5";
    private static final String KEY_SIZE = "size";
    private static final String KEY_DOWNLOADED = "downloaded";
    private static final String KEY_DOWNLOAD_FILE = "download_file";
    private static final String KEY_MD5_FILE = "md5_file";


    Context context;

    SystemUpdateLocalStore(Context context) {
        this.context = context;
    }

    public UpdateInfo getUpdateInfo() {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String localVersion = sp.getString(KEY_LOCAL_VERSION, "");
        if (TextUtils.isEmpty(localVersion)) {
            return null;
        }
        UpdateInfo updateInfo = UpdateFactory.createSystemUpdateInfo(localVersion);
        Version version = Version.valueOf(
                sp.getString(KEY_SERVICE_VERSION, ""),
                sp.getString(KEY_DESCRIPTION, ""),
                sp.getString(KEY_DOWNLOAD_URL, ""),
                sp.getString(KEY_MD5, ""),
                sp.getLong(KEY_SIZE, 0));
        updateInfo.setVersion(version);
        updateInfo.setDownloadFileName(sp.getString(KEY_DOWNLOAD_FILE, ""));
        updateInfo.setMd5FileName(sp.getString(KEY_MD5_FILE, ""));
        updateInfo.setIsDownloaded(sp.getBoolean(KEY_DOWNLOADED, false));
        return updateInfo;
    }

    public void clearUpdateInfo() {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public void saveUpdateInfo(UpdateInfo updateInfo) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        final Version version = updateInfo.getVersion();
        editor.putString(KEY_LOCAL_VERSION, updateInfo.getLocalVersion());
        editor.putString(KEY_SERVICE_VERSION, version.getServiceVersion());
        editor.putString(KEY_DESCRIPTION, version.getDescription());
        editor.putString(KEY_DOWNLOAD_URL, version.getDownloadUrl());
        editor.putString(KEY_MD5, version.getMd5());
        editor.putLong(KEY_SIZE, version.getSize());
        editor.putBoolean(KEY_DOWNLOADED, updateInfo.isDownloaded());
        editor.putString(KEY_DOWNLOAD_FILE, updateInfo.getDownloadFileName());
        editor.putString(KEY_MD5_FILE, updateInfo.getMd5FileName());
        editor.commit();
    }

}
