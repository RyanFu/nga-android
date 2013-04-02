package com.coldmn3.nga.api;

import com.yulingtech.lycommon.util.StringUtils;

public class NgaUtils {

	public static String parseAvatarUrl(String in) {
		if (StringUtils.isEmpty(in)) {
			return "";
		}

		String result = "";

		int start = in.indexOf("http");
		if (start > 0) {
			int end = in.indexOf("\"", start);
			if (end > 0) {
				result = in.substring(start, end);
			}
		}

		return result;
	}

	public static String parseContent(String in) {
		String result = "";

		return result;
	}

}
