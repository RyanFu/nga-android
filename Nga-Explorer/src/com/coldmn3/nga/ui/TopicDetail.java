package com.coldmn3.nga.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.ListViewTopicDetailAdapter;
import com.coldmn3.nga.adapter.TopicDetailPagerAdapter;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class TopicDetail extends FragmentActivity {

	private AppContext appContext;

	TopicDetailPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		setContentView(R.layout.topic_detail);
		appContext = (AppContext) getApplication();

		String title = getIntent().getExtras().getString("title");
		TextView titleView = (TextView) findViewById(R.id.topic_detail_title);
		titleView.setText(title);
		int count = getIntent().getExtras().getInt("page_count");
		mAdapter = new TopicDetailPagerAdapter(getSupportFragmentManager(), count);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

//		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
//		mIndicator = indicator;
//		mIndicator.setViewPager(mPager);
//		final float density = getResources().getDisplayMetrics().density;
//		indicator.setFooterIndicatorHeight(1 * density);
//		indicator.setBackgroundColor(0xFF313131);
//		indicator.setFooterColor(0xFF313131);
//		indicator.setSelectedColor(this.getResources().getColor(R.color.holo_blue_light));
//		indicator.setTextSize(14 * density);

	}

	public void back(View view) {
		this.finish();
	}
}
