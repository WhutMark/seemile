package com.seemile.launcher.data.cache;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.seemile.launcher.R;
import com.seemile.launcher.domain.app.AppInfo;
import com.seemile.launcher.util.IconUtils;
import com.seemile.launcher.util.Logger;

import java.util.HashMap;

/**
 * Created by whuthm on 2016/1/8.
 */
public class IconCache {

	private static final String TAG = "IconCache";

	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

	private static class CacheEntry {
		public Bitmap icon;
		public String title;
	}

	private final HashMap<ComponentName, CacheEntry> mCache = new HashMap<ComponentName, CacheEntry>(
			INITIAL_ICON_CACHE_CAPACITY);

	private final Context mContext;
	private final PackageManager mPackageManager;

	private final Bitmap mDefaultIcon;

	public IconCache(Context context) {
		context = context.getApplicationContext();

		mContext = context;
		mPackageManager = context.getPackageManager();

		mDefaultIcon = makeDefaultIcon();
	}

	private Bitmap makeDefaultIcon() {
		Drawable d = getFullResDefaultIcon();
		return IconUtils.createDrawableToBitmap(mContext, d);
	}

	public void getTitleAndIcon(AppInfo application, ResolveInfo info) {
		synchronized (mCache) {
			Logger.d(TAG, "getTitleAndIcon: componentName = "
					+ application.componentName);
			CacheEntry entry;
			entry = cacheLocked(application.componentName, info);
			application.title = entry.title;
			application.iconBitmap = entry.icon;
		}
	}

	public Bitmap getIcon(Intent intent) {
		synchronized (mCache) {
			final ResolveInfo resolveInfo = mPackageManager.resolveActivity(
					intent, 0);
			ComponentName component = intent.getComponent();

			if (resolveInfo == null || component == null) {
				return mDefaultIcon;
			}

			CacheEntry entry = cacheLocked(component, resolveInfo);
			return entry.icon;
		}
	}

	private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info) {
		CacheEntry entry = mCache.get(componentName);
		if (entry == null) {
			entry = new CacheEntry();

			mCache.put(componentName, entry);
			entry.title = info.loadLabel(mPackageManager).toString();
			if (entry.title == null) {
				entry.title = info.activityInfo.name;
				Logger.d(TAG,
						"CacheLocked get title from activity information: entry.title = "
								+ entry.title);
			}

			if (entry.icon == null) {
				Drawable icon = getFullResIcon(info);
				if (icon == null) {
					icon = getFullResDefaultIcon();
				}
				entry.icon = IconUtils.createDrawableToBitmap(mContext, icon);
			}

		}
		return null;
	}

	public Drawable getFullResDefaultIcon() {
		return mContext.getResources().getDrawable(R.drawable.ic_default_app);
	}

	public Drawable getFullResIcon(ResolveInfo info) {
		return getFullResIcon(info.activityInfo);
	}

	public Drawable getFullResIcon(ActivityInfo info) {
		return info.loadIcon(mPackageManager);
	}

	public HashMap<ComponentName, Bitmap> getAllIcons() {
		synchronized (mCache) {
			HashMap<ComponentName, Bitmap> set = new HashMap<ComponentName, Bitmap>();
			for (ComponentName cn : mCache.keySet()) {
				final CacheEntry e = mCache.get(cn);
				set.put(cn, e.icon);
			}
			return set;
		}
	}

	/**
	 * Remove any records for the supplied ComponentName.
	 */
	public void remove(ComponentName componentName) {
		synchronized (mCache) {
			mCache.remove(componentName);
		}
	}

	/**
	 * Empty out the cache.
	 */
	public void flush() {
		synchronized (mCache) {
			for (ComponentName cn : mCache.keySet()) {
				CacheEntry e = mCache.get(cn);
				e.icon = null;
				e.title = null;
				e = null;
			}
			mCache.clear();
		}
		Logger.d(TAG, "Flush icon cache here.");
	}

}
