package com.coldmn3.nga.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.ListViewTopicAdapter;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.Topic;
import com.coldmn3.nga.bean.TopicList;
import com.coldmn3.nga.common.Constants;
import com.yulingtech.lycommon.ui.PullToRefreshListView;
import com.yulingtech.lycommon.ui.PullToRefreshListView.OnRefreshListener;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class Main extends BaseActivity {

	private static final String LOG_TAG = "Main";

	private AppContext appContext;

	private PullToRefreshListView lvTopics;

	private ListViewTopicAdapter lvTopicAdapter;

	private Handler lvTopicHandler;

	private List<Topic> lvTopicData = new ArrayList<Topic>();

	private View lvTopicFooter;

	private TextView lvTopicFooterMore;

	private ProgressBar lvTopicFooterProgress;

	private int lvTopicSumData;

	private long exitTime;

	private int currentPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		appContext = (AppContext) getApplication();

		appContext.initSettings();
		// 初始化登陆
		appContext.initLogin();

		if (!appContext.isLogin()) {
			ULog.i(LOG_TAG, "not login redirect to Login...");
			Intent intent = new Intent(Main.this, Login.class);
			intent.putExtra("action", "main");
			startActivity(intent);
			this.finish();
		} else {
			initTopicListView();
			initTopicListData();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				AndroidUtils.Toast(this, R.string.exit_hint);
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initTopicListView() {
		lvTopicAdapter = new ListViewTopicAdapter(this, lvTopicData);
		lvTopicFooter = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
		lvTopicFooterMore = (TextView) lvTopicFooter.findViewById(R.id.listview_foot_more);
		lvTopicFooterProgress = (ProgressBar) lvTopicFooter.findViewById(R.id.listview_foot_progress);
		lvTopics = (PullToRefreshListView) findViewById(R.id.topic_listview);
		lvTopics.addFooterView(lvTopicFooter);
		lvTopics.setAdapter(lvTopicAdapter);

		// 跳转帖子内容
		lvTopics.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lvTopicFooter)
					return;

				Topic topic = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					topic = (Topic) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.topic_title);
					topic = (Topic) tv.getTag();
				}
				if (topic == null)
					return;

				if (StringUtils.isEmpty(topic.getTid()) || StringUtils.isEmpty(topic.getAuthor())) {
					return;
				}
				// 跳转帖子内容
				Intent intent = new Intent();
				intent.setClass(Main.this, TopicDetail.class);
				String tid = topic.getTid();
				int pages = (Integer.valueOf(topic.getReplies()) / 20) + 1;
				if (!StringUtils.isEmpty(tid)) {
					intent.putExtra("tid", tid);
					intent.putExtra("page_count", pages);
					intent.putExtra("title", topic.getSubject());
					startActivity(intent);
				}
			}

		});

		lvTopics.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvTopics.onScrollStateChanged(view, scrollState);

				if (lvTopicData.isEmpty()) {
					return;
				}
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvTopicFooter) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvTopics.getTag());
				if (scrollEnd && lvDataState == Constants.LISTVIEW_DATA_MORE) {

					if (!appContext.isNetworkConnected()) {
						AndroidUtils.Toast(Main.this, R.string.network_not_connected);
						return;
					}
					lvTopics.setTag(Constants.LISTVIEW_DATA_LOADING);
					lvTopicFooterMore.setText(R.string.load_ing);
					lvTopicFooterProgress.setVisibility(View.VISIBLE);

					currentPage++;
					loadDefaultTopics(String.valueOf(currentPage), lvTopicHandler, Constants.LISTVIEW_ACTION_SCROLL);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lvTopics.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});

		lvTopics.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (!appContext.isNetworkConnected()) {
					AndroidUtils.Toast(Main.this, R.string.network_not_connected);
					lvTopicFooterProgress.setVisibility(ProgressBar.GONE);
					lvTopics.onRefreshComplete();
					lvTopics.setSelection(0);
					return;
				}
				loadDefaultTopics("1", lvTopicHandler, Constants.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	private void initTopicListData() {
		// 初始化handler
		lvTopicHandler = getHandler(lvTopics, lvTopicAdapter, lvTopicFooterMore, lvTopicFooterProgress);
		//
		if (lvTopicData.isEmpty()) {
			loadDefaultTopics("1", lvTopicHandler, Constants.LISTVIEW_ACTION_INIT);
		}
	}

    /**
     * 加载回复
     * @param page
     * @param handler
     * @param action
     */
	private void loadDefaultTopics(final String page, final Handler handler, final int action) {

		new Thread() {

			@Override
			public void run() {

				Message msg = Message.obtain();
				boolean isRefresh = false;
				if (action == Constants.LISTVIEW_ACTION_REFRESH) {
					isRefresh = true;
				}

				try {
					TopicList topicList = appContext.getDefaultTopicList(page, isRefresh);
					if (topicList != null) {
						msg.what = 1;
						msg.obj = topicList;
					} else {
						msg.what = 0;
					}

				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = Constants.LISTVIEW_DATATYPE_TOPICS;
				handler.sendMessage(msg);
			}

		}.start();
	}

	private Handler getHandler(final PullToRefreshListView lv, final BaseAdapter adapter, final TextView more, final ProgressBar progress) {
		return new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what > 0) {

					handleData(msg.what, msg.obj, msg.arg2, msg.arg1);

					// if (msg.what < 1000) {
					// lv.setTag(Constants.LISTVIEW_DATA_FULL);
					// adapter.notifyDataSetChanged();
					// more.setText(R.string.load_full);
					// } else if (msg.what >= 1000) {
					lv.setTag(Constants.LISTVIEW_DATA_MORE);
					adapter.notifyDataSetChanged();
					more.setText(R.string.load_more);
					// }

				} else if (msg.what == -1) {
					// 异常，显示加载出错，弹出错误消息
					lv.setTag(Constants.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(Main.this);
				} else if (msg.what == 0) {
					lv.setTag(Constants.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					AndroidUtils.Toast(Main.this, "加载帖子列表失败，请尝试重新登陆");
				}

				if (adapter.getCount() == 0) {
					lv.setTag(Constants.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == Constants.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + StringUtils.updateTime());
					lv.setSelection(0);
				} else if (msg.arg1 == Constants.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}

		};
	}

	private void handleData(int what, Object obj, int objtype, int actiontype) {
		switch (actiontype) {
		case Constants.LISTVIEW_ACTION_INIT:
		case Constants.LISTVIEW_ACTION_REFRESH:
			int newdata = 0;// 新加载数据
			switch (objtype) {
			case Constants.LISTVIEW_DATATYPE_TOPICS:
				TopicList topicList = (TopicList) obj;
				lvTopicSumData = what;
				if (actiontype == Constants.LISTVIEW_ACTION_REFRESH) {
					if (lvTopicData.size() > 0) {
						for (Topic topic : topicList.getTopicList()) {
							boolean b = false;
							for (Topic e_topic : lvTopicData) {
								if (topic.getSubject() == e_topic.getSubject()) {
									b = true;
									break;
								}
							}
							if (!b)
								newdata++;
						}

					} else {
						newdata = what;
					}
				}
				lvTopicData.clear();
				lvTopicData.addAll(topicList.getTopicList());

				// 提示新加载数据
				// if (newdata > 0) {
				// NewDataToast.makeText(this,
				// getString(R.string.new_data_toast_message, newdata),
				// false).show();
				// } else {
				// NewDataToast.makeText(this,
				// getString(R.string.new_data_toast_none), false).show();
				// }
				if (actiontype == Constants.LISTVIEW_ACTION_INIT) {
					lvTopics.clickRefresh();
				}
				break;
			}

		case Constants.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {

			case Constants.LISTVIEW_DATATYPE_TOPICS:
				TopicList topicList = (TopicList) obj;
				lvTopicSumData += what;
				if (lvTopicData.size() > 0) {
					for (Topic topic : topicList.getTopicList()) {
						boolean b = false;
						for (Topic e_topic : lvTopicData) {
							if (topic.getSubject() == e_topic.getSubject()) {
								b = true;
								break;
							}
						}
						if (!b) {
							lvTopicData.add(topic);
						}
					}
				} else {
					lvTopicData.addAll(topicList.getTopicList());
				}
				break;
			}
			break;
		}
	}

	public void setting(View view) {
		Intent intent = new Intent(Main.this, Setting.class);
		startActivity(intent);
	}

	public void search(View view) {

	}

	public void refresh(View view) {
		lvTopics.clickRefresh();
	}
}
