package com.seemile.launcher.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by DELL on 2016/2/4.
 */
public class AssetsUtils {

    public static final String UTF_8 = "UTF-8";

    private AssetsUtils() {
    }

    public static String readAsString(Context context, String fileName) throws IOException {
        InputStreamReader inputReader = readAsStreamReader(context, fileName);
        BufferedReader bufReader = new BufferedReader(inputReader);
        StringBuilder result = new StringBuilder("");
        String line;
        while ((line = bufReader.readLine()) != null)
            result.append(line);
        return result.toString();
    }

    public static InputStreamReader readAsStreamReader(Context context, String fileName) throws IOException {
        return new InputStreamReader(context.getResources().getAssets().open(fileName), UTF_8);
    }

    public static Drawable readAsDrawable(Context context, String fileName) throws IOException {
//        int targetDensity = context.getResources().getDisplayMetrics().densityDpi;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inDensity = DisplayMetrics.DENSITY_HIGH;
        InputStream is = context.getResources().getAssets().open(fileName);
        return new BitmapDrawable(BitmapFactory.decodeStream(is));
    }
}
