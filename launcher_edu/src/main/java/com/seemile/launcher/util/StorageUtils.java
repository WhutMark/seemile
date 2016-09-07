package com.seemile.launcher.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whuthm on 2016/2/25.
 */
public class StorageUtils {

    private static final String TAG = "USBStorageUtils";

    private StorageUtils() {
    }

    static Object methodReflexInvoke(String method, Object receiver,
                                     Object... args) {
        Class<?>[] types = new Class<?>[args.length];
        try {
            for (int i = 0; i < args.length; ++i) {
                types[i] = args[i].getClass();
            }
            Method m = receiver.getClass().getMethod(method, types);
            return m.invoke(receiver, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAllMountedPaths(Context context) {
        List<String> mountedPaths = new ArrayList<String>();
        try {

            StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Object obVolume[] = (Object[]) methodReflexInvoke("getVolumeList", manager);
            if (obVolume != null) {
                for (Object o : obVolume) {

                    // 如果 不是mounted状态，忽略
                    String state = (String) methodReflexInvoke("getState", o);
                    if (!Environment.MEDIA_MOUNTED.equals(state))
                        continue;

                    // 如果是 primary 忽略 sdcard是primary
//                    boolean isPrimary = (Boolean) methodReflexInvoke(
//                            "isPrimary", o);
//                    if (isPrimary) {
//                        continue;
//                    }
                    String path = (String) methodReflexInvoke("getPath", o);
                    Logger.i(TAG, "getAllMountedPaths path: " + path);
                    if (!TextUtils.isEmpty(path)) {
                        mountedPaths.add(path);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "getAllMountedPaths failed: " + e.getMessage());
        }
        return mountedPaths;
    }

    public static List<String> getAllMountedPathsWithoutSDCard(Context context) {
        List<String> mountedPaths = getAllMountedPaths(context);

        File sdCard = Environment.getExternalStorageDirectory();
        if (sdCard != null) {
            mountedPaths.remove(sdCard.getPath());
        }

        mountedPaths.remove("internal_sd");

        List<String> backupMountedPaths = new ArrayList<>(mountedPaths.size());
        backupMountedPaths.addAll(mountedPaths);

        for (String filePath : backupMountedPaths) {
            if (isAllDirectory(filePath)) {
                String[] allFilenames = new File(filePath).list();
                if(allFilenames != null && allFilenames.length > 0) {
                    for(String filename : allFilenames) {
                        mountedPaths.add(filePath + File.separator + filename);
                    }
                }
            }
        }

        int size = mountedPaths.size();
        int i = 0;
        while (i < size) {
            String path = mountedPaths.get(i);
            String[] filenames = new File(path).list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return new File(dir, filename).isDirectory();
                }
            });
            if (filenames != null) {
                for (String filename : filenames) {
                    Logger.i(TAG, filename);
                    mountedPaths.add(path + File.separator + filename);
                }
            }
            i++;
        }
        return mountedPaths;
    }

    static boolean isAllDirectory(String filePath) {
        File file = new File(filePath);
        String[] filenames = new File(filePath).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return new File(dir, filename).isDirectory();
            }
        });
        String[] allFilenames = file.list();
        return filenames != null && allFilenames != null && filenames.length == allFilenames.length && allFilenames.length > 0;
    }

}
