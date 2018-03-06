package com.soccer.util;

import java.net.URLEncoder;

import org.springframework.util.StringUtils;

public class StringUtil {
	
	public static boolean isEmpty(String str){
		if(null == str){
			return true;
		} else {
			return str.trim().equals("");
		}
	}
	 public static String escapeSQLLike(String likeStr) {
        String str = StringUtils.replace(likeStr, "_", "/_");
        str = StringUtils.replace(str, "%", "/%");
        return str;
    }
	 
	public static String filterParam(String param) throws Exception {
		return URLEncoder.encode(param, "ISO-8859-1");
	}
	
}
