package com.coldmn3.nga.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.onekeyshare.ShareAllGird;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.DropDownMenuAdapter;
import com.coldmn3.nga.adapter.TopicDetailPagerAdapter;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.app.AppManager;
import com.coldmn3.nga.bean.MenuData;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.StringUtils;

public class TopicDetail extends FragmentActivity implements Callback{

	private AppContext appContext;

	TopicDetailPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;

	private String tid;
    private String title;
    private String url;

	private Button dropDownMenu;
	private PopupWindow popupWindow;
	private List<MenuData> data = new ArrayList<MenuData>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		setContentView(R.layout.topic_detail);
		appContext = (AppContext) getApplication();

		dropDownMenu = (Button) findViewById(R.id.detail_more);

		title = getIntent().getExtras().getString("title");
		tid = getIntent().getExtras().getString("tid");
		TextView titleView = (TextView) findViewById(R.id.topic_detail_title);
		titleView.setText(title);
		int count = getIntent().getExtras().getInt("page_count");
		mAdapter = new TopicDetailPagerAdapter(getSupportFragmentManager(), count);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator = indicator;
		mIndicator.setViewPager(mPager);
		final float density = getResources().getDisplayMetrics().density;
		indicator.setFooterIndicatorHeight(1 * density);
		// indicator.setBackgroundColor(0xFF313131);
		indicator.setBackgroundResource(R.drawable.bottom_bar);
		indicator.setFooterColor(0xFF313131);
		indicator.setSelectedColor(this.getResources().getColor(R.color.holo_blue_light));
		indicator.setTextSize(14 * density);

		dropDownMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow == null) {

					View view = getLayoutInflater().inflate(R.layout.dropdown_list, null);

					ListView listView = (ListView) view.findViewById(R.id.lvGroup);

					MenuData d = null;
					d = new MenuData();
					d.setName("收藏");
					d.setIcon(R.drawable.ic_collect);
					data.add(d);
					d = new MenuData();
					d.setName("分享");
					d.setIcon(R.drawable.ic_share);
					data.add(d);

					DropDownMenuAdapter adapter = new DropDownMenuAdapter(TopicDetail.this, data);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (position == 0) { // 收藏

								new Thread() {

									@Override
									public void run() {
										Message msg = Message.obtain();
										try {
											String result = appContext.addTopicToFav(tid);

											if (StringUtils.isEmpty(result)) {
												msg.what = 0;
											} else {
												msg.what = 1;
												msg.obj = result;
											}
										} catch (AppException e) {
											e.printStackTrace();
											msg.what = -1;
											msg.obj = e;
										}
									}

								}.start();

							} else if (position == 1) {
								newLaunchThread(false).start();
							}
							popupWindow.dismiss();
						}
					});

					popupWindow = new PopupWindow(view, 200, 180);

				}
				popupWindow.setFocusable(true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
				int xPos = windowManager.getDefaultDisplay().getWidth() - popupWindow.getWidth() - AndroidUtils.dip2px(v.getContext(), 10);

				popupWindow.showAsDropDown(v, xPos, 0);
			}
		});

	}

	public void back(View view) {
		this.finish();
	}

	Handler handler = new Handler(this);
	
	// 使用快捷分享完成图文分享
	private Thread newLaunchThread(final boolean silent) {
		return new Thread(){
			public void run() {
				final String image = getImagePath();
				handler.post(new Runnable() {
					public void run() {
						Intent i = new Intent(TopicDetail.this, ShareAllGird.class);
						i.putExtra("notif_icon", R.drawable.ic_launcher);
						i.putExtra("notif_title", TopicDetail.this.getString(R.string.app_name));
						
						i.putExtra("address", "13800123456");
						i.putExtra("title", title);
						i.putExtra("text", TopicDetail.this.getString(R.string.share_content));
						i.putExtra("image", image);
						i.putExtra("image_url", "http://sharesdk.cn/Public/Frontend/images/logo.png");
						i.putExtra("site", TopicDetail.this.getString(R.string.app_name));
						i.putExtra("siteUrl", "http://sharesdk.cn");
						
						i.putExtra("silent", silent);
						TopicDetail.this.startActivity(i);
					}
				});
			}
		};
	}
	
	private String getImagePath() {
		try {
			Activity act = (Activity) this;
			String path;
			if (Environment.getExternalStorageDirectory().exists()) {
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic.jpg";
			}
			else {
				path = act.getApplication().getFilesDir().getAbsolutePath() + "/pic.jpg";
			}
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(act.getResources(), R.drawable.pic);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
			return path;
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
    
	/** 处理操作结果 */
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		String text = AbstractWeibo.actionToString(msg.arg2);
		switch (msg.arg1) {
			case 1: { // 成功
				text = weibo.getName() + " completed at " + text;
			}
			break;
			case 2: { // 失败
				text = weibo.getName() + " caught error at " + text;
			}
			break;
			case 3: { // 取消
				text = weibo.getName() + " canceled at " + text;
			}
			break;
		}
		
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		return false;
	}

	static class BookmarkHandler extends Handler {
		private final WeakReference<TopicDetail> mTopicDetail;

		public BookmarkHandler(TopicDetail topicDetail) {
			mTopicDetail = new WeakReference<TopicDetail>(topicDetail);
		}

		@Override
		public void handleMessage(Message msg) {
			TopicDetail detail = mTopicDetail.get();

			if (detail != null) {
				if (msg.what == 1) {
					// 判断成功或者是重复了!
				} else if (msg.what == 0) {
					AndroidUtils.Toast(detail, "收藏帖子失败");
				} else if (msg.what == -1) {
					((AppException) msg.obj).makeToast(detail);
				}
			}

		}
	}
}
