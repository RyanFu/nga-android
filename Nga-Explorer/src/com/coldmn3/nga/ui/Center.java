package com.coldmn3.nga.ui;

import com.coldmn3.nga.R;

import android.os.Bundle;
import android.view.View;

public class Center extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.center);
	}

	public void back(View view) {
		this.finish();
	}
}
