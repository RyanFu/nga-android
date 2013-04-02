package com.coldmn3.nga.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.app.AppContext;
import com.yulingtech.lycommon.util.AndroidUtils;

public class SettingBrowse extends BaseActivity {

	private AppContext appContext;

	private TextView avatarOnOff;

	private RadioButton fontLarge;
	private RadioButton fontMedium;
	private RadioButton fontSmall;

	private RadioButton imageOp0;;
	private RadioButton imageOp1;
	private RadioButton imageOp2;

	private RadioGroup groupImage;
	private RadioGroup groupFont;
	
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_browse);
		appContext = (AppContext) getApplication();
		
		title = (TextView) findViewById(R.id.title_text);
		title.setText("正文浏览设置");
		
		avatarOnOff = (TextView) findViewById(R.id.avatar_on);

		fontLarge = (RadioButton) findViewById(R.id.font_large);
		fontMedium = (RadioButton) findViewById(R.id.font_medium);
		fontSmall = (RadioButton) findViewById(R.id.font_small);

		imageOp0 = (RadioButton) findViewById(R.id.image_op0);
		imageOp1 = (RadioButton) findViewById(R.id.image_op1);
		imageOp2 = (RadioButton) findViewById(R.id.image_op2);

		groupImage = (RadioGroup) findViewById(R.id.radio_group_image);
		groupFont = (RadioGroup) findViewById(R.id.radio_group_text);

		groupImage.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.image_op0) {
					appContext.setFloorPictureOption(0);
				} else if (checkedId == R.id.image_op1) {
					appContext.setFloorPictureOption(1);
				} else if (checkedId == R.id.image_op2) {
					appContext.setFloorPictureOption(2);
				}
			}
		});

		groupFont.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.font_large) {
					appContext.setFloorFontOption(0);
				} else if (checkedId == R.id.font_medium) {
					appContext.setFloorFontOption(1);
				} else if (checkedId == R.id.font_small) {
					appContext.setFloorFontOption(2);
				}
			}
		});

		setup();
	}

	private void setup() {
		if (appContext.isShowAvatar()) {
			avatarOnOff.setText("开");
		} else {
			avatarOnOff.setText("关");
		}

		switch (appContext.getFloorFontOption()) {
		case 0:
			fontLarge.setChecked(true);
			fontMedium.setChecked(false);
			fontSmall.setChecked(false);
			break;
		case 1:
			fontLarge.setChecked(false);
			fontMedium.setChecked(true);
			fontSmall.setChecked(false);
			break;
		case 2:
			fontLarge.setChecked(false);
			fontMedium.setChecked(false);
			fontSmall.setChecked(true);
			break;
		}

		switch (appContext.getFloorPictureOption()) {
		case 0:
			imageOp0.setChecked(true);
			imageOp1.setChecked(false);
			imageOp2.setChecked(false);
			break;
		case 1:
			imageOp0.setChecked(false);
			imageOp1.setChecked(true);
			imageOp2.setChecked(false);
			break;
		case 2:
			imageOp0.setChecked(false);
			imageOp1.setChecked(false);
			imageOp2.setChecked(true);
			break;
		}
	}

	public void avatar_show(View view) {
		AndroidUtils.showOptionDialig(this, "正文显示头像", R.array.switch_onoff, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					if (appContext.isShowAvatar()) {
						return;
					} else {
						appContext.setShowAvatar(true);
						avatarOnOff.setText("开");
					}
				} else if (which == 1) {
					if (appContext.isShowAvatar()) {
						appContext.setShowAvatar(false);
						avatarOnOff.setText("关");
					} else {
						return;
					}
				}
			}
		});
	}

	public void back(View view) {
		this.finish();
	}

}
