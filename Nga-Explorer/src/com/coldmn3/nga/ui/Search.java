package com.coldmn3.nga.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;

public class Search extends BaseActivity {
	
	private TextView title;
	
	private EditText search;
	
	private Button search_del;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		title = (TextView) findViewById(R.id.title_text);
		title.setText("搜索");
        
		search = (EditText) findViewById(R.id.at_search_edit);
		search_del = (Button) findViewById(R.id.at_search_del);
		
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (!StringUtils.isEmpty(search.getText().toString())) {
					search_del.setVisibility(View.VISIBLE);
				} else {
					search_del.setVisibility(View.GONE);
				}
			}
		});
        
		search.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
					String search_string = search.getText().toString();
					if (StringUtils.isEmpty(search_string)) {
						AndroidUtils.Toast(Search.this, "搜索条件不能为空！");
						return false;
					}
					
					Intent intent = new Intent();
					intent.putExtra("search_string", search_string);
					setResult(RESULT_OK, intent);
					finish();
				}
				return false;
			}
		});
        
		search_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search.setText("");
			}
		});
	}

	public void back(View view) {
		this.finish();
	}
}
