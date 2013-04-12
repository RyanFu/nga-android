package com.coldmn3.nga.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.params.ClientPNames;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.coldmn3.nga.app.AppContext;
import com.coldmn3.nga.app.AppException;
import com.coldmn3.nga.bean.TopicFloorList;
import com.coldmn3.nga.bean.TopicList;
import com.coldmn3.nga.bean.User;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class NgaApi {

	private static final String LOG_TAG = "NgaApi";

	private final static String USER_AGENT = "AndroidNga";
	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "GBK";

	private final static int TIMEOUT_CONNECTION = 10000;
	private final static int TIMEOUT_SOCKET = 10000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;

	public static void cleanCookie() {
		appCookie = "";
	}

	private static String getCookie(AppContext appContext) {
		// if (appCookie == null || "".equals(appCookie)) {
		appCookie = "";
		String currentUserName = appContext.getCurrentUserName();
		if (!StringUtils.isEmpty(currentUserName)) {
			appCookie = appContext.getUserList().get(currentUserName).getCookie();
		}
		// appCookie = appContext.getProperty(userName + ".cookie");
		// }
		return appCookie;
	}

	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(GBK);
		// 禁制自动从定向
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		return httpClient;
	}

	private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		// httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);

		return httpPost;
	}

	/**
	 * 登陆，处理cookie
	 * 
	 * @param appContext
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String account, String pwd) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", account);
		params.put("password", pwd);

		try {
			User user = new User();
			user.setAccount(account);
			user.setPassword(pwd);
			String result = post(appContext, URLs.LOGIN_VALIDATE, params, null, user);

			return user;
		} catch (Exception e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}
	}

	public static TopicFloorList getTopicDetailList(AppContext appContext, final String page, final String tid) throws AppException {
		String url = _MakeURL(URLs.READ_PHP, new HashMap<String, Object>() {
			{
				put("page", page);
				put("tid", tid);
			}
		});
		ULog.i(LOG_TAG + " getTopicDetailList", url);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Accept-Encoding", "gzip,deflate");
		params.put("Accept-Charset", "GBK");

		try {
			long start_time = System.currentTimeMillis();
			final TopicFloorList topicFloorList = TopicFloorList.parse(post(appContext, url, params, null, null));
			long end_time = System.currentTimeMillis();
			long parse_time = end_time - start_time;
			ULog.e("TopicFloorList Parse Time:", " " + parse_time/1000000000);
			final String quote = topicFloorList.getQuote_from();
			if (!StringUtils.isEmpty(quote)) {
				url = _MakeURL(URLs.READ_PHP, new HashMap<String, Object>() {
					{
						put("page", page);
						put("tid", quote);
					}
				});
				ULog.i(LOG_TAG + " getTopicDetailList", "quote from:" + quote + " redirect to :" + url);
				return TopicFloorList.parse(post(appContext, url, params, null, null));
			}
			return topicFloorList;
		} catch (AppException e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}

	}

	/**
	 * 获取大漩涡帖子列表
	 */
	public static TopicList getDefaultTopicList(AppContext appContext, final String page) throws AppException {
		String url = _MakeURL(URLs.THREAD_PHP, new HashMap<String, Object>() {
			{
				put("page", page);
				put("fid", "-7");
			}
		});

		ULog.i(LOG_TAG + " getDefaultTopicList", url);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Accept-Encoding", "gzip,deflate");
		params.put("Accept-Charset", "GBK");

		try {
			return TopicList.parse(post(appContext, url, params, null, null));
		} catch (Exception e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}
	}

	public static TopicList getTopicList(AppContext appContext, final String page, final String fid, final String searchpost, final String favor,
			final String authorid, final String key) throws AppException {
		String url = _MakeURL(URLs.THREAD_PHP, new HashMap<String, Object>() {
			{
				put("page", page);
				put("fid", fid);
				put("searchpost", searchpost);
				put("favor", favor);
				put("authorid", authorid);
				put("key", key);
			}
		});

		ULog.i(LOG_TAG + " getTopicList", url);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Accept-Encoding", "gzip,deflate");
		params.put("Accept-Charset", "GBK");

		try {
			return TopicList.parse(post(appContext, url, params, null, null));
		} catch (Exception e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}
	}

	public static String addTopicToFav(AppContext appContext, String tid) throws AppException {
		return post(appContext, URLs.BOOKMARK + tid, null, null, null);
	}

	// TODO 分离登陆和加载数据的逻辑
	private static String post(AppContext appContext, String url, Map<String, Object> params, Map<String, File> files, User user) throws AppException {
		// String userName = String.valueOf(params.get("email"));
		String cookie = getCookie(appContext);
		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		String responseBody = "";
		int time = 0;

		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, USER_AGENT);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts, httpPost.getParams()));

				if (user == null) {
					httpPost.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
					httpPost.addRequestHeader("Connection", "close");
				}

				int statusCode = httpClient.executeMethod(httpPost);
				responseBody = httpPost.getResponseBodyAsString();

				if (ULog.DEBUG) {
					Header[] headers = httpPost.getResponseHeaders();
					for (Header header : headers) {
						ULog.d(LOG_TAG + " response headers:", header.toString());
					}
					Cookie[] cookies = httpClient.getState().getCookies();
					for (Cookie c : cookies) {
						ULog.d(LOG_TAG + " cookies:", c.toString());
					}
					ULog.v(LOG_TAG, "response body:" + responseBody);
				}

				if (user != null) {
					Header Location = httpPost.getResponseHeader("Location");
					if (Location == null || (Location.toString().indexOf("login_success&error=0") == -1)) {
						return null;
					}

					Cookie[] cookies = httpClient.getState().getCookies();
					String cookieString = "";
					String cid = "";
					String uid = "";
					for (Cookie ck : cookies) {
						cookieString = ck.toString();
						if (cookieString.indexOf("_sid=") == 0) {
							cid = cookieString.substring(5);
						}
						if (cookieString.indexOf("_178c=") == 0) {
							uid = cookieString.substring(6, cookieString.indexOf('%'));
						}
					}

					// 保存cookie
					String userCookie = "ngaPassportUid=" + uid + "; ngaPassportCid=" + cid;

					if (appContext != null && userCookie != "") {
						appCookie = userCookie;
						ULog.d(LOG_TAG, "Login cookie saved!:" + userCookie);
					}

					if (cid != "" && uid != "") {
						user.setUid(uid);
						user.setCid(cid);
						user.setCookie(userCookie);
					}
				}

				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		return responseBody;
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		// System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					ULog.e("error url", " " + url);
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));

			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}
		url.append(URLs.JSON_NOPREFIX);
		return url.toString().replace("?&", "?");
	}

}
