package com.coldmn3.nga.ui;

import com.coldmn3.nga.R;
import com.coldmn3.nga.bean.TopicFloor_;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Center extends BaseActivity {

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.center);

		title = (TextView) findViewById(R.id.title_text);
		title.setText("用户信息");

	}

	public void back(View view) {
		this.finish();
	}
}
