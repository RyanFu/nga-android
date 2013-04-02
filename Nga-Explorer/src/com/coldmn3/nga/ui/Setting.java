package com.coldmn3.nga.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coldmn3.nga.R;

public class Setting extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
	}
    
	public void back(View view) {
		this.finish();
	}

	public void account_manage(View view) {
		startActivity(new Intent(Setting.this, Account.class));
	}
	
	public void setting_browse(View view) {
		startActivity(new Intent(Setting.this, SettingBrowse.class));
	}
}
