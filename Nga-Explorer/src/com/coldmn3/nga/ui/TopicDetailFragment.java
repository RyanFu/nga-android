package com.coldmn3.nga.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.ListViewTopicDetailAdapter;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.TopicFloorList;
import com.coldmn3.nga.bean.TopicFloor_;
import com.yulingtech.lycommon.animation.ComposerButtonAnimation;
import com.yulingtech.lycommon.animation.ComposerButtonGrowAnimationIn;
import com.yulingtech.lycommon.animation.ComposerButtonGrowAnimationOut;
import com.yulingtech.lycommon.animation.ComposerButtonShrinkAnimationOut;
import com.yulingtech.lycommon.animation.InOutAnimation;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.widget.InOutImageButton;

public class TopicDetailFragment extends ListFragment {
	private ListViewTopicDetailAdapter adapter;
	private List<TopicFloor_> topicListData = new ArrayList<TopicFloor_>();
	private String tid;
	private AppContext appContext;
	private String page;
	// private ProgressBar progressBar;
	private TextView loadingText;
	private ListView listView;
	private ImageView img_loading;
	private RotateAnimation rotateAnimation;

	// path ui
	private boolean areButtonsShowing;
	private ViewGroup composerButtonsWrapper;
	private View composerButtonsShowHideButtonIcon;
	private View composerButtonsShowHideButton;
	private Animation rotateStoryAddButtonIn;
	private Animation rotateStoryAddButtonOut;

	private Activity activity;

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
		img_loading = (ImageView) view.findViewById(R.id.img_load);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.refresh); // 加载XML文件中定义的动画

		composerButtonsWrapper = (ViewGroup) view.findViewById(R.id.composer_buttons_wrapper);
		composerButtonsShowHideButton = view.findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = view.findViewById(R.id.composer_buttons_show_hide_button_icon);
		rotateStoryAddButtonIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_story_add_button_in);
		rotateStoryAddButtonOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_story_add_button_out);
		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleComposerButtons();
			}
		});
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			composerButtonsWrapper.getChildAt(i).setOnClickListener(new ComposerLauncher(null, new Runnable() {

				@Override
				public void run() {
					new Thread(new Runnable() {

						@Override
						public void run() {
							startActivity(new Intent(TopicDetailFragment.this.getActivity(), Post.class));
//							reshowComposer();
						}
					}).start();
				}
			}));
		}
		composerButtonsShowHideButton.startAnimation(new ComposerButtonGrowAnimationIn(200));

		listView = getListView();
	}

	private class ComposerLauncher implements View.OnClickListener {

		public final Runnable DEFAULT_RUN = new Runnable() {

			@Override
			public void run() {
				startActivityForResult(new Intent(activity, ComposerLauncher.this.cls), 1);
			}
		};
		private final Class<? extends Activity> cls;
		private final Runnable runnable;

		private ComposerLauncher(Class<? extends Activity> c, Runnable runnable) {
			this.cls = c;
			this.runnable = runnable;
		}

		@Override
		public void onClick(View paramView) {
			startComposerButtonClickedAnimations(paramView, runnable);
		}
	}

	private void startComposerButtonClickedAnimations(View view, final Runnable runnable) {
		this.areButtonsShowing = false;
		Animation shrinkOut1 = new ComposerButtonShrinkAnimationOut(300);
		Animation shrinkOut2 = new ComposerButtonShrinkAnimationOut(300);
		Animation growOut = new ComposerButtonGrowAnimationOut(300);
		shrinkOut1.setInterpolator(new AnticipateInterpolator(2.0F));
		shrinkOut1.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				composerButtonsShowHideButtonIcon.clearAnimation();
				if (runnable != null) {
					runnable.run();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		this.composerButtonsShowHideButton.startAnimation(shrinkOut1);
		for (int i = 0; i < this.composerButtonsWrapper.getChildCount(); i++) {
			final View button = this.composerButtonsWrapper.getChildAt(i);
			if (!(button instanceof InOutImageButton))
				continue;
			if (button.getId() != view.getId())
				// 其他按钮缩小消失
				button.setAnimation(shrinkOut2);
			else {
				// 被点击按钮放大消失
				button.startAnimation(growOut);
			}
		}
	}

	private void reshowComposer() {
		Animation growIn = new ComposerButtonGrowAnimationIn(300);
		growIn.setInterpolator(new OvershootInterpolator(2.0F));
		this.composerButtonsShowHideButton.startAnimation(growIn);
	}

	private void toggleComposerButtons() {
		if (!areButtonsShowing) {
			ComposerButtonAnimation.startAnimations(this.composerButtonsWrapper, InOutAnimation.Direction.IN);
			this.composerButtonsShowHideButtonIcon.startAnimation(this.rotateStoryAddButtonIn);
		} else {
			ComposerButtonAnimation.startAnimations(this.composerButtonsWrapper, InOutAnimation.Direction.OUT);
			this.composerButtonsShowHideButtonIcon.startAnimation(this.rotateStoryAddButtonOut);
		}
		areButtonsShowing = !areButtonsShowing;
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	/**
	 * 加载话题数据
	 * 
	 * @param page
	 * @param action
	 */
	private void loadTopicDetailListDate(final String page, final String tid) {

		loadingText.setText("加载中...");
		loadingText.setVisibility(View.VISIBLE);
		img_loading.setVisibility(View.VISIBLE);
		img_loading.startAnimation(rotateAnimation);// 开始动画

		listView.setVisibility(View.GONE);
		final Handler handler = new TopicDetailHandler(TopicDetailFragment.this);
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();

				try {
					TopicFloorList list = appContext.getTopicDetailList(page, tid);
					if (list != null) {
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
				topicDetail.img_loading.clearAnimation();
				topicDetail.img_loading.setVisibility(View.INVISIBLE);
				topicDetail.listView.setVisibility(View.VISIBLE);

				if (msg.what == 1) {
					TopicFloorList tList = (TopicFloorList) msg.obj;
					if (!StringUtils.isEmpty(tList.getErrorMsg())) {
						AndroidUtils.Toast(topicDetail.getActivity(), tList.getErrorMsg());
					}
					topicDetail.topicListData.clear();
					topicDetail.topicListData.addAll(tList.getTopicDetailList());
					topicDetail.adapter.notifyDataSetChanged();
				} else if (msg.what == 0) {
					AndroidUtils.Toast(topicDetail.getActivity(), "加载帖子列表失败，请尝试重新登陆");
				} else if (msg.what == -1) {
					// exception
					((AppException) msg.obj).makeToast(topicDetail.getActivity());
				}

			}

		}

	}
}
