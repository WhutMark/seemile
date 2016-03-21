package com.seemile.launcher.domain.update;

import android.text.TextUtils;

/**
 * VO
 */
public class Version {

    public final static Version NONE = new Version("", "", "", "", 0);

    private final String serviceVersion;
    private final String description;

    private final String downloadUrl;
    private final String md5;
    private final long size;

    private Version(String serviceVersion,
                    String description, String downloadUrl, String md5, long size) {
        this.serviceVersion = serviceVersion;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.md5 = md5;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getMd5() {
        return md5;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getDescription() {
        return description;
    }

    public static Version valueOf(
            String serviceVersion, String description, String downloadUrl) {
        return valueOf(serviceVersion, description, downloadUrl,
                null, 0);
    }

    public static Version valueOf(String serviceVersion, String description, String downloadUrl,
                                  String md5, long size) {
        if (TextUtils.isEmpty(serviceVersion)) {
            return NONE;
        }
        return new Version(serviceVersion, description,
                downloadUrl, md5, size);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Version) {
            Version other = (Version) o;
            return (serviceVersion == other.serviceVersion || (serviceVersion != null && serviceVersion.equals(other.serviceVersion)))
                    && (description == other.description || (description != null && description.equals(other.description)))
                    && (downloadUrl == other.downloadUrl || (downloadUrl != null && downloadUrl.equals(other.downloadUrl)))
                    && (md5 == other.md5 || (md5 != null && md5.equals(other.md5)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (serviceVersion != null) {
            result = 37 * result + serviceVersion.hashCode();
        }
        if (description != null) {
            result = 37 * result + description.hashCode();
        }
        if (downloadUrl != null) {
            result = 37 * result + downloadUrl.hashCode();
        }
        if (md5 != null) {
            result = 37 * result + md5.hashCode();
        }
        return result;
    }
}
