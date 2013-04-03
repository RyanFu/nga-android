package com.yulingtech.lycommon.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * 图片操作Util
 * 
 * @author session
 * @date 2013-4-2
 * @version v1.0
 */
public class ImageUtils {

	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;

		try {
			fis = new FileInputStream(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e2) {
			}
		}

		return bitmap;
	}

	public static void saveBitmapToSDCard(String absPath, Context context, String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;

		if (AndroidUtils.isSDCardMounted()) {
			FileOutputStream fos = new FileOutputStream(absPath);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, stream);
			byte[] bytes = stream.toByteArray();
			fos.write(bytes);
			fos.close();
		}

	}
}
