package com.coldmn3.nga.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.FaceFragmentAdapter;
import com.coldmn3.nga.app.AppManager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class Post extends FragmentActivity {

	private TextView title;

	private View emo_view;

	private RadioButton emo_recent;
	private RadioButton emo_default;
	private RadioButton emo_keyboard;

	private RadioGroup emoGroup;

	FaceFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		setContentView(R.layout.post);

		initUI();

	}

	public void back(View view) {
		this.finish();
	}

	public void showEmoView(View view) {
		if (emo_view.getVisibility() == View.VISIBLE) {
			emo_view.setVisibility(View.GONE);
		} else {
			emo_view.setVisibility(View.VISIBLE);
		}
	}

	private void initUI() {
		title = (TextView) findViewById(R.id.title_text);
		title.setText("发帖");

		emo_view = findViewById(R.id.emo_view);
		emo_keyboard = (RadioButton) findViewById(R.id.emo_keyboard);
		emo_recent = (RadioButton) findViewById(R.id.emo_recent);
		emo_default = (RadioButton) findViewById(R.id.emo_default);
        
		mPager = (ViewPager) findViewById(R.id.face_pager);
		emoGroup = (RadioGroup) findViewById(R.id.emo_group);

		emo_keyboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (emo_view.getVisibility() == View.VISIBLE) {
					emo_view.setVisibility(View.GONE);
					emo_keyboard.setChecked(false);
					emo_default.setChecked(true);
				}
			}
		});
        
		emoGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.emo_recent) {
					emo_keyboard.setChecked(false);
					emo_default.setChecked(false);
				} else if (checkedId == R.id.emo_default) {
					emo_keyboard.setChecked(false);
					emo_recent.setChecked(false);
				} else if (checkedId == R.id.emo_keyboard) {
					emo_recent.setChecked(false);
					emo_default.setChecked(false);
				}
			}
		});

		mAdapter = new FaceFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.face_indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setSnap(true);
		indicator.setFillColor(0xff121C46);
		indicator.setStrokeColor(0xFF000000);
		
	}
}
