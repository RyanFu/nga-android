package com.coldmn3.nga.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.coldmn3.nga.api.NgaUtils;
import com.coldmn3.nga.app.AppException;
import com.yulingtech.lycommon.util.AndroidUtils;
import com.yulingtech.lycommon.util.ULog;

public class TopicFloorList {
	private int floorCount;

	private String errorMsg;

	private List<TopicFloor> topicDetailList = new ArrayList<TopicFloor>();

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getFloorCount() {
		return floorCount;
	}

	public void setFloorCount(int floor_count) {
		this.floorCount = floor_count;
	}

	public List<TopicFloor> getTopicDetailList() {
		return topicDetailList;
	}

	public void setTopicDetailList(List<TopicFloor> topicDetailList) {
		this.topicDetailList = topicDetailList;
	}

	public static TopicFloorList parse(String jsonString) throws AppException {

		jsonString = jsonString.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		jsonString = jsonString.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

		jsonString = jsonString.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
		jsonString = jsonString.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
		jsonString = jsonString.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");

		final String start = "\"__P\":{\"aid\":";
		final String end = "\"this_visit_rows\":";
		if (jsonString.indexOf(start) != -1 && jsonString.indexOf(end) != -1) {
			String validJs = jsonString.substring(0, jsonString.indexOf(start));
			validJs += jsonString.substring(jsonString.indexOf(end));
			jsonString = validJs;
		}
		TopicFloorList topicDetailList = new TopicFloorList();
		TopicFloor topicFloor = null;
		List<TopicFloor> list = new ArrayList<TopicFloor>();
		int j = 0;
		try {

			JSONObject jsonObject = new JSONObject(jsonString);

			JSONObject dataObject = jsonObject.getJSONObject("data");
			if (dataObject == null) {
				ULog.e("TopicList Parse:", "JSON格式已变化？");
				topicDetailList.setErrorMsg("解析数据异常请尝试重新登陆");
				return topicDetailList;
			}

			JSONObject topicsObject = dataObject.getJSONObject("__R");
			if (topicsObject == null) {
				String error = dataObject.getString("__MESSAGE");
				if (error.startsWith("(ERROR:16)")) {
					topicDetailList.setErrorMsg("账号权限不足");
				} else if (error.startsWith("(ERROR:5)")) {
					topicDetailList.setErrorMsg("主题发布时间超过限制");
				} else {
					topicDetailList.setErrorMsg(Html.fromHtml(error).toString());
				}
				return topicDetailList;
			}

			int rows = dataObject.getInt("__R__ROWS");
			for (int i = 0; i < rows; i++) {
				j = i;
				if (topicsObject.has(String.valueOf(i))) {

					JSONObject topicObject = (JSONObject) topicsObject.get(String.valueOf(i));
					topicFloor = new TopicFloor();
					if (topicObject.has("comment_to_id")) {
						topicFloor.setAuthor(topicObject.getString("author"));
						topicFloor.setLou(topicObject.getString("lou"));
						topicFloor.setPostdate(topicObject.getString("postdate"));
					} else {
						topicFloor.setAuthor(topicObject.getString("author"));
						topicFloor.setContent(NgaUtils.parseContent(topicObject.getString("content")));
						topicFloor.setPostdate(topicObject.getString("postdate"));
						topicFloor.setPostnum(topicObject.getString("postnum"));
						topicFloor.setAurvrc(topicObject.getString("aurvrc"));
						topicFloor.setLou(topicObject.getString("lou"));
						topicFloor.setJs_escap_avatar(NgaUtils.parseAvatarUrl(topicObject.getString("js_escap_avatar")));
						ULog.i("avatar url:", topicFloor.getJs_escap_avatar());
					}

					list.add(topicFloor);

					// ULog.d("TopicList Parse: ", "topic:" + i + " " + topic.toString());
				} else {
					ULog.e("TopicList", "topic:" + i + " null!");
					topicFloor.setAuthor("");
					topicFloor.setContent("<i>楼层解析错误</i>");
					topicFloor.setPostdate("");
					topicFloor.setPostnum("");
					topicFloor.setAurvrc("");
					topicFloor.setLou("");
					topicFloor.setJs_escap_avatar("");
					list.add(topicFloor);
				}

			}
			topicDetailList.setTopicDetailList(list);
			return topicDetailList;

		} catch (JSONException e) {
			e.printStackTrace();
			ULog.e("topic json error:", " " + j);
			throw AppException.json(e);
		}

	}

}
