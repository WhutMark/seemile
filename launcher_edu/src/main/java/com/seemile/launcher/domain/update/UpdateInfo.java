package com.seemile.launcher.domain.update;

import android.text.TextUtils;

/**
 * 实体
 * AggregateRoot
 */
public class UpdateInfo {

    private final String uniqueId;

    private final String localVersion;
    private Version version = Version.NONE;
    private boolean isDownloaded;

    private String downloadFileName;
    private String md5FileName;

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public String getMd5FileName() {
        return md5FileName;
    }

    public UpdateInfo(String uniqueId, String localVersion) {
        this.localVersion = localVersion;
        if (TextUtils.isEmpty(uniqueId)) {
            throw new IllegalArgumentException("uniqueId is empty");
        }
        this.uniqueId = uniqueId;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Version getVersion() {
        return version;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    void setVersion(Version version) {
        this.version = version;
    }

    void setIsDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    void setMd5FileName(String md5FileName) {
        this.md5FileName = md5FileName;
    }

    void startDownload(String downloadFileName, String md5FileName) {
        this.downloadFileName = downloadFileName;
        this.md5FileName = md5FileName;
        this.isDownloaded = false;
    }

    void endDownload(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    @Override
    public String toString() {
        return "uniqueId " + uniqueId + " : localVersion = " + localVersion + ", serviceVersion = " + version.getServiceVersion() + ", downloaded = " + isDownloaded;
    }
}
