package com.coldmn3.nga.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.coldmn3.nga.app.AppException;
import com.yulingtech.lycommon.util.ULog;

public class TopicList extends Entity {

	private String error_msg;

	private int topicCount;

	private List<Topic> topicList = new ArrayList<Topic>();

	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	public int getTopicCount() {
		return topicCount;
	}

	public void setTopicCount(int topicCount) {
		this.topicCount = topicCount;
	}

	public List<Topic> getTopicList() {
		return topicList;
	}

	public void setTopicList(List<Topic> topicList) {
		this.topicList = topicList;
	}

	public static TopicList parse(String jsonString) throws AppException {

//		jsonString = jsonString.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
//		jsonString = jsonString.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

		TopicList topicList = new TopicList();

		Topic topic = null;
		List<Topic> list = new ArrayList<Topic>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject dataObject = jsonObject.getJSONObject("data");
			if (dataObject == null) {
				ULog.e("TopicList Parse:", "JSON格式已变化？");
				topicList.setError_msg("解析数据异常请尝试重新登陆");
				return topicList;
			}
			JSONObject topicsObject = dataObject.getJSONObject("__T");
			if (topicsObject == null) {
				String error = dataObject.getString("__MESSAGE");
				if (error.startsWith("(ERROR:16)")) {
					topicList.setError_msg("账号权限不足");
				} else {
					topicList.setError_msg(Html.fromHtml(error).toString());
				}
				return topicList;
			}
			int rows = dataObject.getInt("__T__ROWS");
			for (int i = 0; i < rows; i++) {
                
				if (topicsObject.has(String.valueOf(i))) {
					JSONObject topicObject = (JSONObject) topicsObject.get(String.valueOf(i));
					topic = new Topic();
					topic.setAuthor(topicObject.getString("author"));
					topic.setAuthorid(topicObject.getString("authorid"));
					topic.setIfmark(topicObject.getString("ifmark"));
					topic.setLocked(topicObject.getString("locked"));
					topic.setPostdate(topicObject.getString("postdate"));
					topic.setReplies(topicObject.getString("replies"));
					topic.setSubject(topicObject.getString("subject"));
					topic.setTid(topicObject.getString("tid"));
					list.add(topic);

//					ULog.d("TopicList Parse: ", "topic:" + i + " " + topic.toString());
				} else {
					ULog.e("TopicList", "topic:" + i + " null!");
					topic = new Topic();
					topic.setAuthor("请用已激活的ID登陆");
					topic.setAuthorid("1");
					topic.setIfmark("1");
					topic.setLocked("1");
					topic.setPostdate("0");
					topic.setReplies("0");
					topic.setSubject("NULL!NULL!NULL!请用已激活的ID登陆，否则可能会出现这种异常帖子");
					topic.setTid("");
					list.add(topic);
				}
                
			}
			topicList.setTopicList(list);
			return topicList;
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
	}
}
