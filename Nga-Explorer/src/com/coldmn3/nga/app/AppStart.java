package com.coldmn3.nga.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.coldmn3.nga.R;
import com.coldmn3.nga.ui.Main;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_start);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(AppStart.this, Main.class);
				startActivity(intent);
				AppStart.this.finish();
			}
		}, 1000);
	}

}
