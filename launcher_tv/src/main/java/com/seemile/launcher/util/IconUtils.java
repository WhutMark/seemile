package com.seemile.launcher.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by whuthm on 2016/1/8.
 */
public class IconUtils {

    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
    }


    public static Bitmap createDrawableToBitmap(Context context, Drawable icon) {
        synchronized (sCanvas) { // we share the statics :-(
            if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();

            final Bitmap bitmap = Bitmap.createBitmap(sourceWidth, sourceHeight,
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);

            sOldBounds.set(icon.getBounds());
            icon.setBounds(0, 0, sourceWidth, sourceHeight);
            icon.draw(canvas);
            icon.setBounds(sOldBounds);
            canvas.setBitmap(null);
            return bitmap;
        }
    }

}
