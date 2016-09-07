package com.seemile.launcher.domain.home;

import android.content.Context;
import com.google.gson.stream.JsonReader;
import com.seemile.launcher.data.config.Theme;
import com.seemile.launcher.util.AssetsUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whuthm on 2016/2/4.
 */
class HomeLocalStore {

    HomeLocalStore() {
    }

    public List<HomePageInfo> getHomePageList(Context context) throws IOException {
        JsonReader reader = new JsonReader(AssetsUtils.readAsStreamReader(context, Theme.getHomePagesPath()));
        reader.beginObject();
        List<HomePageInfo> pageList = new ArrayList<HomePageInfo>();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("pages".equals(name)) {
                reader.beginArray();
                while (reader.hasNext()) {
                    HomePageInfo homePageInfo = new HomePageInfo();
                    //array节点
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if ("id".equals(name)) {
                            homePageInfo.id = reader.nextLong();
                        } else if ("title".equals(name)) {
                            homePageInfo.title = reader.nextString();
                        } else if ("items".equals(name)) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                reader.beginObject();
                                //array节点
                                HomeInfo itemInfo = new HomeInfo();
                                while (reader.hasNext()) {
                                    name = reader.nextName();
                                    if ("id".equals(name)) {
                                        itemInfo.id = reader.nextLong();
                                    } else if ("title".equals(name)) {
                                        itemInfo.title = reader.nextString();
                                    } else if ("itemType".equals(name)) {
                                        itemInfo.itemType = reader.nextInt();
                                    } else if ("screen".equals(name)) {
                                        itemInfo.screen = reader.nextInt();
                                    } else if ("container".equals(name)) {
                                        itemInfo.container = reader.nextLong();
                                    } else if ("cellX".equals(name)) {
                                        itemInfo.cellX = reader.nextInt();
                                    } else if ("cellY".equals(name)) {
                                        itemInfo.cellY = reader.nextInt();
                                    } else if ("spanX".equals(name)) {
                                        itemInfo.spanX = reader.nextInt();
                                    } else if ("spanY".equals(name)) {
                                        itemInfo.spanY = reader.nextInt();
                                    } else if ("iconUrl".equals(name)) {
                                        itemInfo.iconUrl = reader.nextString();
                                    } else if ("bgUrl".equals(name)) {
                                        itemInfo.bgUrl = reader.nextString();
                                    } else if ("url".equals(name)) {
                                        itemInfo.url = reader.nextString();
                                    } else if ("appType".equals(name)) {
                                        itemInfo.appType = reader.nextInt();
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                homePageInfo.itemList.add(itemInfo);
                                reader.endObject();
                            }
                            reader.endArray();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    pageList.add(homePageInfo);
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return pageList;
    }

}
