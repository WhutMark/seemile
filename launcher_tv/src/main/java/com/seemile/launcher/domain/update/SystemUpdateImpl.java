package com.seemile.launcher.domain.update;

import com.seemile.launcher.data.converter.VersionConverter;
import com.seemile.launcher.domain.Download;
import com.seemile.launcher.domain.interactor.SystemUpdateInteractor;
import com.seemile.launcher.net.DownloadEngine;
import com.seemile.launcher.net.GenericEngine;
import com.seemile.launcher.net.RequestProtocol;
import com.seemile.launcher.util.SystemUtils;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by whuthm on 2016/3/15.
 */
class SystemUpdateImpl implements SystemUpdateInteractor {

    //http://120.27.157.5/os/rom/getNewestRom.do?category=edu
    // {"createDate":"2016/03/17 15:12:05","createMan":0,"description":"大文件测试","filePath":"http://120.27.157.5:80/os/files/oth/201603/20160317151200392.img","fileSize":785151189,"id":66,"romName":"大文件测试","romType":"edu","romVersion":1,"updateDate":"2016/03/17 15:12:05","updateMan":0}

    @Override
    public Observable<Version> checkNewVersion(String uniqueId, String localVersion, String channel) {

        RequestProtocol requestProtocol = new RequestProtocol.Builder(ENDPOINT + "/os/rom/getNewestRom.do")
                .addParameter("category", channel)
                .addParameter("version", localVersion)
                .build();
        return new GenericEngine<Version>(requestProtocol, new VersionConverter()).start();
    }

    @Override
    public Observable<Download> download(String url, String filePath, long fileSize) {
        DownloadEngine engine = new DownloadEngine(
                url,
                filePath,
                fileSize);
        return engine.start();
    }
}
