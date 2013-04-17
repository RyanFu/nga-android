package com.coldmn3.nga.ui;

import com.coldmn3.nga.R;
import com.coldmn3.nga.api.NgaApi;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.Poster;
import com.coldmn3.nga.bean.TopicFloor_;
import com.yulingtech.lycommon.util.AsyncImageDownload;
import com.yulingtech.lycommon.util.BitmapManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfo extends BaseActivity {

	// private TextView userName;
	// private TextView userID;
	private ImageView userPhoto;

	private TextView title;

	private BitmapManager bitmapManager;

	private TextView userId;
	private TextView userName;
	private TextView userGroup;
	private TextView userYz;// 用户状态
	private TextView userPostNum;
	private TextView userMoney;
	private TextView userRegDate;
	private TextView userLastVisit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);

		title = (TextView) findViewById(R.id.title_text);
		title.setText("用户信息");

		bitmapManager = new BitmapManager(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_avatar));

		// userName = (TextView) findViewById(R.id.user_name);
		userId = (TextView) findViewById(R.id.u_user_id);
		userPhoto = (ImageView) findViewById(R.id.user_avatar);
		userName = (TextView) findViewById(R.id.u_user_name);
		userGroup = (TextView) findViewById(R.id.u_user_group);
		userYz = (TextView) findViewById(R.id.u_user_yz);
		userPostNum = (TextView) findViewById(R.id.u_user_postnum);
		userMoney = (TextView) findViewById(R.id.u_user_money);
		userRegDate = (TextView) findViewById(R.id.u_user_regdate);
		userLastVisit = (TextView) findViewById(R.id.u_user_lastvisit);

		Bundle bundle = getIntent().getExtras();
		TopicFloor_ floor_ = (TopicFloor_) bundle.getSerializable("floor");
		// String user_name = floor_.getPoster().getUsername();

		bitmapManager.loadBitmap(true, "/_Nga/avatar/", floor_.getPoster().getAvatar(), userPhoto, new AsyncImageDownload() {

			@Override
			public Bitmap onDownload(String url) {

				try {
					return NgaApi.getNetBitmap(url);
				} catch (AppException e) {
					e.printStackTrace();
				}
				return null;
			}
		});

		Poster poster = floor_.getPoster();

		String user_id = "用户ID :" + floor_.getPoster().getUid();
		SpannableString user_id_span = new SpannableString(user_id);
		user_id_span.setSpan(new ForegroundColorSpan(R.color.nga_textblue), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		user_id_span.setSpan(new ForegroundColorSpan(R.color.shit5), 10, user_id.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// userName.setText(user_name);
		userId.setText(user_id_span);
		userName.setText("用户名 :" + poster.getUsername());
		userGroup.setText("用户组 :" + poster.getGroupid());
		userYz.setText("用户状态 :" + poster.getYz());
		userPostNum.setText("发帖数 :" + poster.getPostnum());
		userMoney.setText("金钱 :" + poster.getMoney());
		userRegDate.setText("注册日期 :" + poster.getRegdate());
		userLastVisit.setTag("最后访问 :" + poster.getThisvisit());
		
	}

	public void back(View view) {
		this.finish();
	}
}
