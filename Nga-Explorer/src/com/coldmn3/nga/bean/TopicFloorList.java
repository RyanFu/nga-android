package com.coldmn3.nga.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.coldmn3.nga.api.NgaUtils;
import com.coldmn3.nga.app.AppException;
import com.yulingtech.lycommon.util.ULog;

public class TopicFloorList {

	private String errorMsg;

	private List<TopicFloor_> topicDetailList = new ArrayList<TopicFloor_>();

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<TopicFloor_> getTopicDetailList() {
		return topicDetailList;
	}

	public void setTopicDetailList(List<TopicFloor_> topicDetailList) {
		this.topicDetailList = topicDetailList;
	}

	public static TopicFloorList parse(String jsonString) throws AppException {

		jsonString = jsonString.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		jsonString = jsonString.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

		jsonString = jsonString.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
		jsonString = jsonString.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
		jsonString = jsonString.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");

		// final String start = "\"__P\":{\"aid\":";
		// final String end = "\"this_visit_rows\":";
		// if (jsonString.indexOf(start) != -1 && jsonString.indexOf(end) != -1) {
		// String validJs = jsonString.substring(0, jsonString.indexOf(start));
		// validJs += jsonString.substring(jsonString.indexOf(end));
		// jsonString = validJs;
		// }
		TopicFloorList topicDetailList = new TopicFloorList();
		TopicFloor_ topicFloor = null;
		List<TopicFloor_> list = new ArrayList<TopicFloor_>();
		int j = 0;
		try {

			JSONObject jsonObject = new JSONObject(jsonString);

			JSONObject dataObject = jsonObject.getJSONObject("data");
			if (dataObject == null) {
				ULog.e("TopicList Parse:", "JSON格式已变化？");
				topicDetailList.setErrorMsg("解析数据异常请尝试重新登陆");
				return topicDetailList;
			}

			JSONObject rowObject = null;
			if (!dataObject.has("__R")) {
				String error = dataObject.getString("__MESSAGE");
				if (error.startsWith("(ERROR:16)")) {
					topicDetailList.setErrorMsg("账号权限不足");
				} else if (error.startsWith("(ERROR:5)")) {
					topicDetailList.setErrorMsg("主题发布时间超过限制");
				} else {
					topicDetailList.setErrorMsg(Html.fromHtml(error).toString());
				}
				return topicDetailList;
			} else {
				rowObject = dataObject.getJSONObject("__R");
			}

			JSONObject posterObject = null;
			if (dataObject.has("__U")) {
				posterObject = dataObject.getJSONObject("__U");
			}

			int rows = dataObject.getInt("__R__ROWS");

			if (rowObject != null && posterObject != null) {
				for (int i = 0; i < rows; i++) {
					j = i;
					topicFloor = new TopicFloor_();
					if (rowObject.has(String.valueOf(i))) {

						JSONObject topicObject = (JSONObject) rowObject.get(String.valueOf(i));

						// row...
						FloorRow floorRow = new FloorRow();
						if (topicObject.has("comment_to_id")) {
							floorRow.setSubject(topicObject.getString("subject"));
							floorRow.setLou("[" + topicObject.getString("lou") + "]楼");
							floorRow.setPostdate(topicObject.getString("postdate"));
							floorRow.setAuthorid(topicObject.getString("authorid"));
						} else {
							floorRow.setAuthorid(topicObject.getString("authorid"));
							floorRow.setPostdate(topicObject.getString("postdate"));
							floorRow.setLou("[" + topicObject.getString("lou") + "]楼");
							floorRow.setSubject(topicObject.getString("subject"));
							floorRow.setContent(NgaUtils.parseContent(topicObject.getString("content")));
							floorRow.setAlterinfo(topicObject.getString("alterinfo"));
						}

						// author...
						Poster poster = new Poster();
						JSONObject userObject = posterObject.getJSONObject(floorRow.getAuthorid());
						if (userObject != null) {
							poster.setUid(userObject.getString("uid"));
							poster.setUsername(userObject.getString("username"));
							poster.setCredit(userObject.getString("credit"));
							poster.setAvatar(NgaUtils.parseAvatarUrl(userObject.getString("avatar")));
							poster.setYz(userObject.getString("yz"));
							float rvrc = userObject.getInt("rvrc");
							rvrc = rvrc / 10.0f;
							poster.setRvrc(String.valueOf(rvrc));// 威望
							poster.setPostnum(userObject.getString("postnum"));
						}

						topicFloor.setPoster(poster);
						topicFloor.setRow(floorRow);

						list.add(topicFloor);

						// ULog.d("TopicList Parse: ", "topic:" + i + " " + topic.toString());
					} else {
						ULog.e("TopicList", "topic:" + i + " null!");
						list.add(topicFloor);
					}

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
