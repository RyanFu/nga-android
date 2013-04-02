package com.yulingtech.lycommon.util;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

/**
 * 
 * @author session
 * @date 2013-4-2
 * @version v1.0
 */
public class BitmapManager {

	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static Map<ImageView, String> imageViews;
	private static ExecutorService pool;

	private Bitmap defaultBmp;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5); // 固定线程池
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

	public BitmapManager() {
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片，可以指定显示图片的高度和宽度
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp, int width, int height) {
		imageViews.put(imageView, url);
		
		Bitmap bitmap = getBitmapFromCache(url);
		
		if (bitmap != null) {
			// 显示缓存图片
			imageView.setImageBitmap(bitmap);
		} else {
			// 加载SD卡中的图片缓存
			String fileName = StringUtils.getUrlFileName(url);
			
			String filePath = "";
			if (AndroidUtils.isSDCardMounted()) {
				String imageCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/_Nga/image_cache/";
				File file = new File(imageCachePath);
				if (!file.exists()) {
					file.mkdirs();
				} else {
					filePath = imageCachePath + fileName;
				}
			}
			File imageFile = new File(filePath);
			
			if (imageFile.exists()) {
				
			} else {
				// 线程加载网络图片
				imageView.setImageBitmap(defaultBmp);
				
			}
		}
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}
}
