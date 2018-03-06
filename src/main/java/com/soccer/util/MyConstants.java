package com.soccer.util;

public class MyConstants {
	
	/** 图片上传大小限制1M */
	public static final long IMG_MAXSIZE = 10240000;
	/**临时图片存放路径*/
	public static final String TEMP_UPLOAD_PATH ="/temp"; 
	/** 获取当前操作系统的路径分隔符 System.getProperties().getProperty("file.separator"); */
	public static final String FILE_SEPARATOR = "/";
	
	public static final String UPLOAD_PATH_ADMIN = "/formalPic/admin";
	public static final String UPLOAD_PATH_PHOTO = "/formalPic/photo";
	public static final String UPLOAD_PATH_CUP = "/formalPic/cup";
	public static final String UPLOAD_PATH_FIELD = "/formalPic/field";
	public static final String UPLOAD_PATH_HONOR = "/formalPic/honor";
	public static final String UPLOAD_PATH_LOGO = "/formalPic/logo";
	public static final String UPLOAD_PATH_PLAYER ="/formalPic/player";
	public static final String UPLOAD_PATH_TEAM ="/formalPic/team";
	public static final String UPLOAD_PATH_UEDITOR ="/formalPic/ueditor"; 
	
	
	/** 云片网   用于发短信的APIkey*/
	public static final String SMS_API_KEY = "c5d82f1d17182efa5d6c8e1769d7fe7a";
	/**通用发送接口的http地址*/
	public static String URI_SEND_SMS = "http://yunpian.com/v1/sms/send.json";
	/**用户注册经纪人时的短信模板*/
	public static String SMS_TEXT = "【草根足球】验证码 #code#，草根足球密码找回。";
	
}
