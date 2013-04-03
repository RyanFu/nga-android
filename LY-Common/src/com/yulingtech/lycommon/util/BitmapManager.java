package com.yulingtech.lycommon.util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

	public void loadBitmap(boolean net, String catalog, String url, ImageView imageView, AsyncImageDownload downloader) {
		loadBitmap(net, catalog, url, imageView, this.defaultBmp, 0, 0, downloader);
	}

	public void loadBitmap(boolean net, String catalog, String url, ImageView imageView, Bitmap defaultBmp, AsyncImageDownload downloader) {
		loadBitmap(net, catalog, url, imageView, defaultBmp, 0, 0, downloader);
	}

	/**
	 * 加载图片，可以指定显示图片的高度和宽度
	 * 
	 * @param catalog
	 *            SD卡下的目录，如/_Nga/image_cache/
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 * @param width
	 * @param height
	 */
	public void loadBitmap(boolean net, String catalog, String url, ImageView imageView, Bitmap defaultBmp, int width, int height, AsyncImageDownload downloader) {
		ULog.d("loadBitmap, url:", " " + url);
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
				String imageCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + catalog;
				File file = new File(imageCachePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				filePath = imageCachePath + fileName;
			}
			File imageFile = new File(filePath);

			if (imageFile.exists()) {
				Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(), filePath);
				imageView.setImageBitmap(bmp);
			} else {
				// 线程加载网络图片
				imageView.setImageBitmap(defaultBmp);
				if (net) {
					queueJob(filePath, url, imageView, width, height, downloader);
				}
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

	/**
	 * 从网络中加载图片
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String absPath, final String url, final ImageView imageView, final int width, final int height,
			final AsyncImageDownload downloader) {

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
						// 向SD卡中写入图片缓存
						try {
							ImageUtils.saveBitmapToSDCard(absPath, imageView.getContext(), StringUtils.getUrlFileName(url), (Bitmap) msg.obj);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height, downloader);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String url, int width, int height, AsyncImageDownload downloader) {
		Bitmap bitmap = null;
		try {
			// http加载图片
			bitmap = downloader.onDownload(url);
			if (width > 0 && height > 0) {
				// 指定显示图片的高宽
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			}
			// 放入缓存
			cache.put(url, new SoftReference<Bitmap>(bitmap));
			// 存入SD卡
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
