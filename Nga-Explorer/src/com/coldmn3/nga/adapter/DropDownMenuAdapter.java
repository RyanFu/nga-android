package com.coldmn3.nga.adapter;

import java.util.List;

import com.coldmn3.nga.R;
import com.coldmn3.nga.bean.MenuData;
import com.yulingtech.lycommon.util.ULog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DropDownMenuAdapter extends BaseAdapter {

	private Context mcContext;
	private List<MenuData> listData;

	public DropDownMenuAdapter(Context context, List<MenuData> data) {
		mcContext = context;
		this.listData = data;
	}

	@Override
	public int getCount() {
		return listData.size();
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

		ViewHolder holder = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(mcContext).inflate(R.layout.dropdown_list_item, null);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.menu_name);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MenuData data = listData.get(position);
		ULog.e("data: ", " " + data.getName());

		holder.name.setText(data.getName());

		holder.name.setCompoundDrawablesWithIntrinsicBounds(data.getIcon(), 0, 0, 0);
		
		return convertView;
	}

	static class ViewHolder {
		TextView name;
	}

}
