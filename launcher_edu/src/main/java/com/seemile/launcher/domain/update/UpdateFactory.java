package com.seemile.launcher.domain.update;

/**
 * Created by whuthm on 2016/2/17.
 */
public class UpdateFactory {

    private static final String SYSTEM_UNIQUE_ID = "system";

    public static UpdateInfo createSystemUpdateInfo(String localVersion) {
        return new UpdateInfo(SYSTEM_UNIQUE_ID, localVersion);
    }

    public static UpdateInfo createAppUpdateInfo(String localVersion) {
        return new UpdateInfo(SYSTEM_UNIQUE_ID, localVersion);
    }

}
