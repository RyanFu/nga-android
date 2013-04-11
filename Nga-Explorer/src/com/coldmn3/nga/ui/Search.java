package com.coldmn3.nga.ui;

import com.coldmn3.nga.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Search extends BaseActivity {
	
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		title = (TextView) findViewById(R.id.title_text);
		title.setText("搜索");
	}

	public void back(View view) {
		this.finish();
	}
}
