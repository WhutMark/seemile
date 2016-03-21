package com.seemile.launcher.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by whuthm on 2016/1/8.
 */
public class ImageUtils {

    private void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h) {
        renderDrawableToBitmap(d, bitmap, x, y, w, h, 1f);
    }

    private void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h,
                                        float scale) {
        if (bitmap != null) {
            Canvas c = new Canvas(bitmap);
            c.scale(scale, scale);
            Rect oldBounds = d.copyBounds();
            d.setBounds(x, y, x + w, y + h);
            d.draw(c);
            d.setBounds(oldBounds);
            c.setBitmap(null);
        }
    }

    public static Bitmap createDrawableToBitmap(Context context, Drawable d) {
        if (d instanceof BitmapDrawable) {
            // Ensure the bitmap has a density.
            BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
            }
        }
        int sourceWidth = d.getIntrinsicWidth();
        int sourceHeight = d.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(sourceWidth, sourceHeight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        Rect oldBounds = new Rect();

        oldBounds.set(d.getBounds());
        d.setBounds(0, 0, sourceWidth, sourceHeight);
        d.draw(canvas);
        d.setBounds(oldBounds);
        canvas.setBitmap(null);
        return bitmap;
    }

}
