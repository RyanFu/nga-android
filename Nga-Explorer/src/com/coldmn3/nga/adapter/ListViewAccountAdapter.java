package com.coldmn3.nga.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.app.AppContext;

public class ListViewAccountAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<String> listItems;
	private AppContext appContext;
	private Context mContext;

	static class ListItemView {
		public TextView account;
		public ImageView current;
		public Button del;
	}

	public ListViewAccountAdapter(Context context, List<String> data) {
		this.mContext = context;
		this.listItems = data;
		this.inflater = LayoutInflater.from(context);
		appContext = (AppContext) ((Activity) context).getApplication();
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.account_item, null);

			listItemView = new ListItemView();
			listItemView.account = (TextView) convertView.findViewById(R.id.account);
			listItemView.current = (ImageView) convertView.findViewById(R.id.active);
			listItemView.del = (Button) convertView.findViewById(R.id.delete);

			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		final String user = listItems.get(position);
		listItemView.account.setText(user);
		if (appContext.getCurrentUserName().equals(user)) {
			listItemView.current.setVisibility(View.VISIBLE);
		} else {
			listItemView.current.setVisibility(View.INVISIBLE);
		}
		listItemView.del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Builder builder = new Builder(v.getContext());
				builder.setMessage("是否删除此账号？");
				builder.setTitle("删除");
				builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						listItems.remove(user);
						appContext.cleanUserInfo(user);
						notifyDataSetChanged();

						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();

			}
		});

		return convertView;
	}

}
