package com.coldmn3.nga.adapter;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.coldmn3.nga.ui.GentleManHelper;

public class EmoticonAdapter extends BaseAdapter {
	private Context mContext = null;
	private Bitmap bitmap;

	int catory;

	// private ArrayList<Integer> mFaceList = ;

	public EmoticonAdapter(Context context, String catory) {
		mContext = context;
		this.catory = Integer.valueOf(catory);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (catory > GentleManHelper.res.length) {
			return 0;
		}
		return GentleManHelper.res[catory].length;
	}

	@Override
	public Object getItem(int position) {

		InputStream is = null;
		String imgURl = GentleManHelper.res[catory][position];
		String fileName = GentleManHelper.dirs[catory] + "/" + imgURl.substring(imgURl.lastIndexOf("/") + 1, imgURl.length());
		try {
			is = mContext.getAssets().open(fileName);
			Bitmap bm = BitmapFactory.decodeStream(is);
			return bm;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
		// return b;
		// return "[img]" + GentleManHelper.res[catory][position] +"[/img]\n";
	}

	public String getFaceURL(int position) {
		return "[img]" + GentleManHelper.res[catory][position] + "[/img]";
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(90, 90));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// imageView.setPadding(3, 3, 3, 3);

		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setFocusable(false);
		InputStream is = null;
		String imgURl = GentleManHelper.res[catory][position];
		String fileName = GentleManHelper.dirs[catory] + "/" + imgURl.substring(imgURl.lastIndexOf("/") + 1, imgURl.length());
		try {
			is = mContext.getAssets().open(fileName);
			Bitmap bm = BitmapFactory.decodeStream(is);
			imageView.setImageBitmap(bm);
		} catch (Exception e) {
			// TODO: handle exception
		}

		// imageView.setImageResource(emoticon_list.get(position));
		return imageView;
	}

}
