package com.coldmn3.nga.api;

import com.yulingtech.lycommon.util.StringUtils;

public class NgaUtils {

	private static final String cTag = "(?i)";

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

		// [l][/l]
		in = in.replaceAll(cTag + "\\[l\\]", "<div style='float:left' >");
		in = in.replaceAll(cTag + "\\[/l\\]", "</div>");

		// [r][/r]
		in = in.replaceAll(cTag + "\\[r\\]", "<div style='float:right' >");
		in = in.replaceAll(cTag + "\\[/r\\]", "</div>");

		// align
		in = in.replaceAll(cTag + "\\[align=right\\]", "<div style='text-align:right' >");
		in = in.replaceAll(cTag + "\\[align=left\\]", "<div style='text-align:left' >");
		in = in.replaceAll(cTag + "\\[align=center\\]", "<div style='text-align:center' >");
		in = in.replaceAll(cTag + "\\[/align\\]", "</div>");

		// [quote]
		in = in.replaceAll(cTag + "\\[quote\\]", "<div style='background:#E8E8E8;border:1px solid #888' >");
		in = in.replaceAll(cTag + "\\[/quote\\]", "</div>");

		// reply
		in = in.replaceAll(cTag + "\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");

		// topic
		in = in.replaceAll(cTag + "\\[tid=\\d+\\]Topic\\[/pid\\]", "Topic");

		// [b]
		in = in.replaceAll(cTag + "\\[b\\]", "<b>");
		in = in.replaceAll(cTag + "\\[/b\\]", "</b>");

		// [item]
		in = in.replaceAll(cTag + "\\[item\\]", "<b>");
		in = in.replaceAll(cTag + "\\[/item\\]", "</b>");

		// [u]
		in = in.replaceAll(cTag + "\\[u\\]", "<u>");
		in = in.replaceAll(cTag + "\\[/u\\]", "</u>");

		// in = in.replaceAll(cTag + "\\[s:(\\d+)\\]", "<img src='file:///android_asset/a$1.gif'>");
		in = in.replace(cTag + "<br/><br/>", "<br/>");

		// [url]
		in = in.replaceAll(cTag + "\\[url\\](http[^\\[|\\]]+)\\[/url\\]", "<a href=\"$1\">$1</a>");
		in = in.replaceAll(cTag + "\\[url=(http[^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]", "<a href=\"$1\">$2</a>");

		// [flash]
		in = in.replaceAll(cTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
				"<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");

		// [color]
		in = in.replaceAll(cTag + "\\[color=([^\\[|\\]]+)\\]", "<span style='color:$1' >");
		in = in.replaceAll(cTag + "\\[/color\\]", "</span>");

		// lessernuke
		in = in.replaceAll("\\[lessernuke\\]",
				"<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>");
		in = in.replaceAll("\\[/lessernuke\\]", "</div?");

		// [table]
		in = in.replaceAll("\\[table\\]", "<table border='1px' cellspacing='0px' style='border-collapse:collapse;color:blue'><tbody>");
		in = in.replaceAll("\\[/table\\]", "</tbody></table>");
		in = in.replaceAll("\\[tr\\]", "<tr>");
		in = in.replaceAll("\\[/tr\\]", "<tr>");
		in = in.replaceAll("\\[td\\]", "<td>");
		in = in.replaceAll("\\[/td\\]", "<td>");

		// [i]
		in = in.replaceAll(cTag + "\\[i\\]", "<i style=\"font-style:italic\">");
		in = in.replaceAll(cTag + "\\[/i\\]", "</i>");

		// [del]
		in = in.replaceAll(cTag + "\\[del\\]", "<del class=\"gray\">");
		in = in.replaceAll(cTag + "\\[/del\\]", "</del>");

		//
		in = in.replaceAll(cTag + "\\[font=([^\\[|\\]]+)\\]", "<span style=\"font-family:$1\">");
		in = in.replaceAll(cTag + "\\[/font\\]", "</span>");

		// collapse
		in = in.replaceAll(cTag + "\\[collapse([^\\[|\\]])*\\](([\\d|\\D])+?)\\[/collapse\\]", "<div style='border:1px solid #888' >" + "$2" + "</div>");

		// size
		in = in.replaceAll(cTag + "\\[size=(\\d+)%\\]", "<span style=\"font-size:$1%;line-height:$1%\">");
		in = in.replaceAll(cTag + "\\[/size\\]", "</span>");

		// [img]./ddd.jpg[/img]
		in = in.replaceAll(cTag + "\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]",
				"<a href='http://img6.ngacn.cc/attachments$1'><img src='http://img6.ngacn.cc/attachments$1' style= 'max-width:100%' ></a>");
		in = in.replaceAll(cTag + "\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]", "<a href='$1'><img src='$1' style= 'max-width:100%' ></a>");

		return in;
	}

}
