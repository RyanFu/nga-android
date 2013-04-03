package com.coldmn3.nga.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.api.NgaApi;
import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.TopicFloor;
import com.yulingtech.lycommon.util.AsyncImageDownload;
import com.yulingtech.lycommon.util.BitmapManager;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class ListViewTopicDetailAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<TopicFloor> listData;
	private Context mContext = null;
	private AppContext appContext;

	boolean touchUp = false;
	View popupContainer;
	float touchX;
	float touchY;
	final float density;

	private BitmapManager bitmapManager;

	private static SparseArray<SoftReference<View>> cache;

	public static final class ViewHolder {
		public TextView author;
		public WebView content;
		public ImageView avatar;
		public TextView floor;
		public TextView postdate;
		public TextView aurvrc;
		public TextView postnum;
		int position = -1;
	}

	public ListViewTopicDetailAdapter(Context context, List<TopicFloor> data) {
		mContext = context;
		inflater = LayoutInflater.from(context);
		density = mContext.getResources().getDisplayMetrics().density;
		this.listData = data;

		appContext = (AppContext) ((Activity) context).getApplication();
		cache = new SparseArray<SoftReference<View>>();
		bitmapManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		TopicFloor detail = listData.get(position);
//		ViewHolder viewHolder = null;
//		if (convertView == null) {
//			viewHolder = new ViewHolder();
//			convertView = inflater.inflate(R.layout.relative_aritclelist, parent, false);
//
//			viewHolder.author = (TextView) convertView.findViewById(R.id.topic_floor_author);
//			viewHolder.content = (WebView) convertView.findViewById(R.id.content);
//			viewHolder.floor = (TextView) convertView.findViewById(R.id.floor);
//			viewHolder.postdate = (TextView) convertView.findViewById(R.id.postdate);
//			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.topic_floor_avatar);
//			viewHolder.postnum = (TextView) convertView.findViewById(R.id.topic_floor_postnum);
//			viewHolder.aurvrc = (TextView) convertView.findViewById(R.id.topic_floor_aurvrc);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
//		handleAvatar(viewHolder, detail.getJs_escap_avatar(), true);
//		viewHolder.author.setText(detail.getAuthor());
//		viewHolder.floor.setText("[" + detail.getLou() + "楼]");
//		viewHolder.postdate.setText(detail.getPostdate());
//		viewHolder.aurvrc.setText("威望:" + detail.getAurvrc());
//		viewHolder.postnum.setText("发帖:" + detail.getPostnum());
//
//		viewHolder.content.loadDataWithBaseURL(" fake", detail.getContent(), "text/html", "utf-8", null);
		
		ViewHolder viewHolder = null;

		SoftReference<View> ref = cache.get(position);
		View cachedView = null;
		if (ref != null) {
			cachedView = ref.get();
		}

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.topic_flooritem_1, parent, false);

			viewHolder.author = (TextView) convertView.findViewById(R.id.topic_floor_author);
			viewHolder.content = (WebView) convertView.findViewById(R.id.content);
			viewHolder.floor = (TextView) convertView.findViewById(R.id.floor);
			viewHolder.postdate = (TextView) convertView.findViewById(R.id.postdate);
			viewHolder.aurvrc = (TextView) convertView.findViewById(R.id.topic_floor_aurvrc);
			viewHolder.postnum = (TextView) convertView.findViewById(R.id.topic_floor_postnum);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.topic_floor_avatar);

			convertView.setTag(viewHolder);
			cache.put(position, new SoftReference<View>(convertView));
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		TopicFloor detail = listData.get(position);

		viewHolder.content.setFocusableInTouchMode(false);
		viewHolder.content.setFocusable(false);
		viewHolder.content.setLongClickable(false);
		viewHolder.content.setTag(detail.getLou());

		WebSettings setting = viewHolder.content.getSettings();
		// setting.setBlockNetworkImage(!showImage);
		// setting.setDefaultFontSize(PhoneConfiguration.getInstance().getWebSize());
		setting.setJavaScriptEnabled(false);
		setting.setBuiltInZoomControls(true);
		setting.setJavaScriptEnabled(false);
		setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		setting.setRenderPriority(RenderPriority.HIGH);

		int picOp = appContext.getFloorPictureOption();
		boolean net = false;
		if (picOp == 2 || (picOp == 1 && appContext.getNetworkType() == AppContext.NETTYPE_WIFI)) {
			setting.setBlockNetworkImage(false);
			net = true;
		} else {
			setting.setBlockNetworkImage(true);
			net = false;
		}
		viewHolder.position = position;

		String url = detail.getJs_escap_avatar();
		handleAvatar(viewHolder, url, net); // 头像

		String content = detail.getContent();
		String subject = detail.getSubject();
		if (content == null) {
			content = "";
		}
		if (subject == null) {
			subject = "";
		}
		content = "<strong>" + subject + "</strong>" + " <br/>" + content;
		viewHolder.content.loadDataWithBaseURL(" fake", "loading", "text/html", "utf-8", null);

		// viewHolder.content.refreshDrawableState();
		// viewHolder.content.clearView();
		viewHolder.content.loadDataWithBaseURL(" fake", detail.getContent(), "text/html", "utf-8", null);
		// handleFloorContent(viewHolder.content, content, false);
		viewHolder.author.setText(detail.getAuthor());
		viewHolder.floor.setText("[" + detail.getLou() + "楼]");
		viewHolder.postdate.setText(detail.getPostdate());
		viewHolder.aurvrc.setText("威望:" + detail.getAurvrc());
		viewHolder.postnum.setText("发帖:" + detail.getPostnum());

		View left = convertView.findViewById(R.id.floor_left);
		left.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {

					touchUp = true;
					touchY = event.getRawY();
					ULog.d("touchY", "" + touchY);
					popupContainer = inflater.inflate(R.layout.topic_detail_popout, null);
					PopupWindow popupWindow = new PopupWindow(popupContainer, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					popupWindow.setFocusable(true);
					popupWindow.setOutsideTouchable(true);
					// popupWindow.setAnimationStyle(R.style.popupFade);
					popupWindow.update();
					popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));
					ULog.d("popupwindow height", "" + popupWindow.getHeight());

					popupWindow.showAtLocation(v, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, (int) (touchY - 56 * density));
				}
				return false;
			}
		});

		if (position % 2 == 0) {
			convertView.findViewById(R.id.floor_left).setBackgroundColor(mContext.getResources().getColor(R.color.shit3));
			convertView.findViewById(R.id.floor_right).setBackgroundColor(mContext.getResources().getColor(R.color.shit1));
			viewHolder.content.setBackgroundColor(mContext.getResources().getColor(R.color.shit1));
		} else {
			convertView.findViewById(R.id.floor_left).setBackgroundColor(mContext.getResources().getColor(R.color.shit4));
			convertView.findViewById(R.id.floor_right).setBackgroundColor(mContext.getResources().getColor(R.color.shit2));
			viewHolder.content.setBackgroundColor(mContext.getResources().getColor(R.color.shit2));
		}
		return convertView;
	}

	private void handleAvatar(ViewHolder viewHolder, String url, boolean net) {
		if (appContext.isShowAvatar()) {
			viewHolder.avatar.setVisibility(View.VISIBLE);
			if (!StringUtils.isEmpty(url)) {
				bitmapManager.loadBitmap(net, "/_Nga/avatar/", url, viewHolder.avatar, new AsyncImageDownload() {

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
			} else {
				viewHolder.avatar.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_avatar));
//				viewHolder.avatar.setVisibility(View.GONE);
			}
		} else {
			viewHolder.avatar.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_trans));
		}
	}

	private void handleFloorContent(final WebView contentView, final String content, final boolean b) {
		WebSettings webSettings = contentView.getSettings();
		webSettings.setDefaultFontSize(14);
		webSettings.setJavaScriptEnabled(false);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setRenderPriority(RenderPriority.HIGH);

		contentView.setFocusableInTouchMode(false);
		contentView.setFocusable(false);
		contentView.loadDataWithBaseURL("fake", "loading", "text/html", "utf-8", null);
		webSettings.setBlockNetworkImage(b);
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String result = (String) msg.obj;
				contentView.loadDataWithBaseURL("fake", result, "text/html", "utf-8", null);
				contentView.refreshDrawableState();
				// contentView.loadData(result, "text/html; charset=gbk", null);
			}

		};

		new Thread() {

			@Override
			public void run() {
				// String result = NgaTagHandler.formatTags(content, b);
				Message message = Message.obtain();
				// message.obj = result;
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// TODO: handle exception
				}

				message.obj = content;
				handler.sendMessage(message);
			}

		}.start();

	}

}
