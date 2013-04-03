package com.yulingtech.lycommon.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yulingtech.lycommon.R;

public class AndroidUtils {

	private static long lastClickTime;

	/**
	 * 按钮3000ms内防风怒
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (timeD > 0 && timeD < 3000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static LayoutInflater getLayoutInflater(Context context) {
		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflate;
	}

	/**
	 * 使用自定义外观的Toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void Toast(Context context, String text) {
		Toast result = new Toast(context);
		View v = getLayoutInflater(context).inflate(R.layout.custom_toast, null);
		TextView tv = (TextView) v.findViewById(R.id.toast_text);
		tv.setText(text);
		result.setDuration(Toast.LENGTH_SHORT);
		result.setView(v);
		result.show();
	}

	public static void Toast(Context context, int id) {
		Toast(context, context.getString(id));
	}

	public static void showOptionDialig(Context context, String title, int array_id, OnClickListener listener) {
		Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setIcon(null).setItems(context.getResources().getStringArray(array_id), listener)
				.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}

	/**
	 * 检测SD卡是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardMounted() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * dip px 互换
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
