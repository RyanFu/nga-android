package com.coldmn3.nga.api;

import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class NgaUtils {

	public static String parseAvatarUrl(String in) {
		if (StringUtils.isEmpty(in)) {
			return "";
		}

		String result = "";

		int start = in.indexOf("http");
		if (start == 0) {
			result = in;
		} else if (start > 0) {
			int end = in.indexOf("\"", start);
			if (end == -1) {
				end = in.length();
			}
			result = in.substring(start, end);
		}
		if (result.contains("?")) {
			result = result.substring(0, result.indexOf("?"));
		}
		return result;
	}

	public static String parseContent(String in) {
		String result = "";

		return in;
	}

}
