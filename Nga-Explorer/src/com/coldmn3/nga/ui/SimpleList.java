package com.coldmn3.nga.ui;

import com.coldmn3.nga.R;
import com.yulingtech.lycommon.util.StringUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SimpleList extends BaseActivity {

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);

		title = (TextView) findViewById(R.id.title_text);

		String titleText = (String) getIntent().getExtras().get("title");
		if (!StringUtils.isEmpty(titleText)) {
			title.setText(titleText);
		}

	}

	public void back(View view) {
		this.finish();
	}

}
