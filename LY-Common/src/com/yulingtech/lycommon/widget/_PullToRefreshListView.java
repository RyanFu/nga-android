package com.yulingtech.lycommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yulingtech.lycommon.R;

public class _PullToRefreshListView extends ListView implements OnScrollListener {

	private static final String LOG_TAG = "PullToRefreshListView";

	// 下拉刷新标志
	private final static int PULL_TO_REFRESH = 0;
	// 松开刷新标志
	private final static int RELEASE_TO_REFRESH = 1;
	// 正在刷新标志
	private final static int REFRESHING = 2;
	// 刷新完成标志
	private final static int DONE = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	// 设置箭头图标动画
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int firstItemIndex;
	private int currentScrollState;

	private int headContentHeight;
	private int headContentOriginalTopPadding;

	private int state;

	private boolean isBack;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecorded;

	public OnRefreshListener refreshListener;

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public _PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public _PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public _PullToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		currentScrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisibleItem;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstItemIndex == 0 && !isRecorded) {
				startY = (int) ev.getY();
				isRecorded = true;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (state != REFRESHING) {

				if (state == DONE) {

				} else if (state == PULL_TO_REFRESH) {
					state = DONE;
					changeHeaderViewByState();
				} else if (state == RELEASE_TO_REFRESH) {
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
				}

			}

			isRecorded = false;
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) ev.getY();

			if (firstItemIndex == 0 && !isRecorded) {
				isRecorded = true;
				startY = tempY;
			}
			if (state != REFRESHING && isRecorded) {
				// 可以松开刷新了
				if (state == RELEASE_TO_REFRESH) {

					if ((tempY - startY) < (headContentHeight + 20) && (tempY - startY) > 0) {
						state = PULL_TO_REFRESH;
						changeHeaderViewByState();
					} else if ((tempY - startY) <= 0) {
						state = DONE;
						changeHeaderViewByState();
					} else {

					}

				} else if (state == PULL_TO_REFRESH) {
					// 下拉长度可以进入RELEASE_TO_REFRESH
					if ((tempY - startY) >= headContentHeight + 20 && currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
						state = RELEASE_TO_REFRESH;
						isBack = true;
						changeHeaderViewByState();
					} else if ((tempY - startY) <= 0) {
						// 下拉然后上移
						state = DONE;
						changeHeaderViewByState();
					}

				} else if (state == DONE) {
					// done -> pull_to_refresh
					if ((tempY - startY) > 0) {
						state = PULL_TO_REFRESH;
						changeHeaderViewByState();
					}

				}

				if (state == PULL_TO_REFRESH) {
					int topPadding = (int) (-1 * headContentHeight + (tempY - startY));
					headView.setPadding(headView.getPaddingLeft(), topPadding, headView.getPaddingRight(), headView.getPaddingBottom());
					headView.invalidate();
				}

				if (state == RELEASE_TO_REFRESH) {
					int topPadding = (int) ((tempY - startY - headContentHeight));
					headView.setPadding(headView.getPaddingLeft(), topPadding, headView.getPaddingRight(), headView.getPaddingBottom());
					headView.invalidate();
				}

			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void onRefreshComplete(String update) {
		lastUpdatedTextView.setText(update);
		onRefreshComplete();
	}

	public void onRefreshComplete() {
		state = DONE;
		changeHeaderViewByState();
	}

	private void init(Context context) {

		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.pull_to_refresh_head, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

		arrowImageView.setMinimumWidth(50);
		arrowImageView.setMinimumHeight(50);

		measureView(headView);
		headContentOriginalTopPadding = headView.getPaddingTop();
		headContentHeight = headView.getMeasuredHeight();

		// 隐藏headview
		headView.setPadding(headView.getPaddingLeft(), -1 * headView.getPaddingTop(), headView.getPaddingRight(), headView.getPaddingBottom());
		headView.invalidate();

		addHeaderView(headView);
		setOnScrollListener(this);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_TO_REFRESH:

			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText(R.string.pull_to_refresh_release_label);

			// Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_TO_REFRESH:

			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
			}
			tipsTextview.setText(R.string.pull_to_refresh_pull_label);

			// Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(headView.getPaddingLeft(), headContentOriginalTopPadding, headView.getPaddingRight(), headView.getPaddingBottom());
			headView.invalidate();

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.INVISIBLE);
			tipsTextview.setText(R.string.pull_to_refresh_refreshing_label);
			lastUpdatedTextView.setVisibility(View.GONE);

			// Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(headView.getPaddingLeft(), -1 * headContentHeight, headView.getPaddingRight(), headView.getPaddingBottom());
			headView.invalidate();

			progressBar.setVisibility(View.INVISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow_down);

			tipsTextview.setText(R.string.pull_to_refresh_pull_label);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			// Log.v(TAG, "当前状态，done");
			break;
		}
	}

	// 计算headView的width及height值
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

}
