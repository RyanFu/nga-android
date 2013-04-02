package com.coldmn3.nga.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.bean.Topic;
import com.yulingtech.lycommon.util.StringUtils;

public class ListViewTopicAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Topic> listItems;

	static class ListItemView {
		public TextView title;
		public TextView post_time;
		public TextView author;
		public TextView comment_count;
	}

	public ListViewTopicAdapter(Context context, List<Topic> data) {
		inflater = LayoutInflater.from(context);
		this.listItems = data;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.topic_listitem, null);

			listItemView = new ListItemView();
			listItemView.title = (TextView) convertView.findViewById(R.id.topic_title);
			listItemView.author = (TextView) convertView.findViewById(R.id.topic_author);
			listItemView.post_time = (TextView) convertView.findViewById(R.id.topic_post_time);
			listItemView.comment_count = (TextView) convertView.findViewById(R.id.topic_comment_count);

			convertView.setTag(listItemView);

		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		Topic topic = listItems.get(position);
		String date = topic.getPostdate();
		date = StringUtils.friendly_time(String.valueOf(Integer.valueOf(topic.getPostdate()) * 1000L));
		String replies = topic.getReplies();
		if (replies == null) {
			replies = "";
		}
		String author = topic.getAuthor();
		if (author == null) {
			author = "";
		}

		listItemView.title.setText(Html.fromHtml(topic.getSubject()));
		listItemView.title.setTag(topic);
		listItemView.author.setText(author);
		listItemView.post_time.setText(date);
		listItemView.comment_count.setText(replies);

		// 设置深浅间隔的shit
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.topic_item_bg_shit1);
		} else {
			convertView.setBackgroundResource(R.drawable.topic_item_bg_shit2);
		}
		return convertView;
	}

}
