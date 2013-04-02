package com.coldmn3.nga.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.ListViewTopicDetailAdapter;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.TopicFloor;
import com.coldmn3.nga.bean.TopicFloorList;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class TopicDetailFragment extends ListFragment {
	private ListViewTopicDetailAdapter adapter;
	private List<TopicFloor> topicListData = new ArrayList<TopicFloor>();
	private String tid;
	private AppContext appContext;
	private String page;
	// private ProgressBar progressBar;
	private TextView loadingText;
	private ListView listView;

	public static TopicDetailFragment newInstance(String page) {
		TopicDetailFragment fragment = new TopicDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString("page", page);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
		loadingText = (TextView) view.findViewById(R.id.loading_text);
		listView = getListView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.floor_listfragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		// 网络连接判断
		if (!appContext.isNetworkConnected()) {
			AndroidUtils.Toast(getActivity(), R.string.network_not_connected);
		}

		adapter = new ListViewTopicDetailAdapter(getActivity(), topicListData);
		setListAdapter(adapter);

		Bundle b = getArguments();
		page = b.getString("page");
		if (page == null) {
			page = "";
		}
		tid = getActivity().getIntent().getStringExtra("tid");
		if (tid == null) {
			tid = "";
		}

		if (topicListData.isEmpty()) {
			loadTopicDetailListDate(page, tid);
		}
	}

	/**
	 * 加载话题数据
	 * 
	 * @param page
	 * @param action
	 */
	private void loadTopicDetailListDate(final String page, final String tid) {

		loadingText.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		final Handler handler = new TopicDetailHandler(TopicDetailFragment.this);
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();

				try {
					TopicFloorList list = appContext.getTopicDetailList(page, tid);
					if (list != null) {
						if (!StringUtils.isEmpty(list.getErrorMsg())) {
							AndroidUtils.Toast(TopicDetailFragment.this.getActivity(), list.getErrorMsg());
						}
						msg.what = 1;
						msg.obj = list;
					} else {
						// 未登陆
						msg.what = 0;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}

		}.start();
	}

	static class TopicDetailHandler extends Handler {
		private final WeakReference<TopicDetailFragment> mTopicDetail;

		public TopicDetailHandler(TopicDetailFragment topicDetail) {
			mTopicDetail = new WeakReference<TopicDetailFragment>(topicDetail);
		}

		@Override
		public void handleMessage(Message msg) {
			TopicDetailFragment topicDetail = mTopicDetail.get();

			if (topicDetail != null) {

				topicDetail.loadingText.setVisibility(View.GONE);
				topicDetail.listView.setVisibility(View.VISIBLE);
				if (msg.what == 1) {
					TopicFloorList tList = (TopicFloorList) msg.obj;
					topicDetail.topicListData.clear();
					topicDetail.topicListData.addAll(tList.getTopicDetailList());
					topicDetail.adapter.notifyDataSetChanged();
				} else if (msg.what == 0) {
					AndroidUtils.Toast(topicDetail.getActivity(), "加载帖子列表失败，请尝试重新登陆");
				} else if (msg.what == -1) {
					// eception
					((AppException) msg.obj).makeToast(topicDetail.getActivity());
				}

			}

		}

	}
}
