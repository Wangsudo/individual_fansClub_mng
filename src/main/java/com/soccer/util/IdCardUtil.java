package com.soccer.util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdCardUtil {
	
	public static Integer maleOrFemalByIdCard(String idCard){
			Integer ret = null;
		    Integer gender  = Integer.parseInt(idCard.substring(14,17));
		    if(gender !=null){
		    	if(gender%2==0){  
		    		ret = 2;
		    	}else{
		    		ret = 1;
		    	}
		    }
		    return ret;
	}
	public static Date getBirthDayByIdCard(String idCard){
	    String birth =  idCard.substring(6,14); 
	    try {
			return  DateUtils.string2Date(birth,"yyyyMMdd");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	
	
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("<img src=\"(.*)\"");
		String sql = "123<img src=\"/formalPic/ueditor/20180113193931130-200x200.jpg\"123123";
		Matcher matcher = pattern.matcher(sql);
		String thumbnail =null ;
		if(matcher.find() ){
			thumbnail = matcher.group(1);
		}

		String str="this is \"Tom\" and \"Eric\"， this is \"Bruce lee\", he is a chinese, name is \"李小龙\"。";
		Pattern p=Pattern.compile("\"(.*?)\"");
		Matcher m=p.matcher(str);
		while(m.find()){
			System.out.println(m.group());
		}
	}
}
