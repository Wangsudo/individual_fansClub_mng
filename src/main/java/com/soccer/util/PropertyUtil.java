package com.soccer.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.annotations.Synchronize;

public class PropertyUtil {

	synchronized public static String getUploadPath(String filePath, String key) throws Exception {
		String result = null;
		
		System.out.println("getUploadPath, filepath=" + filePath + ", key=" + key);
		
		InputStream path = new FileInputStream(filePath);
		Properties pros = new Properties();
		try {
			pros.load(path);
			result = pros.getProperty(key);
			
			System.out.println("getUploadPath, filepath=" + filePath + ", key=" + key + ", value=" + result);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("file is not exist");
		} finally {
			if(path!=null) {
				path.close();
			}
		}
		return result;
	}
	synchronized public static Properties getProperties(String filePath) throws Exception {
		//InputStream path = PropertyUtil.class.getClassLoader().getResourceAsStream(filePath);
		InputStream path = new FileInputStream(filePath);
		Properties pros = new Properties();
		try {
			pros.load(path);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("file is not exist");
		} finally {
			if(path!=null) {
				path.close();
			}
		}
		return pros;
	}
	
	synchronized public static void saveProperites(String filePath, Properties pros) throws Exception {
		FileOutputStream fos = null;
		try {
			// 文件输出流      
			//fos = new FileOutputStream(PropertyUtil.class.getResource("/").getPath() + filePath);
			fos = new FileOutputStream(filePath);
			pros.store(fos, "update");
			if(fos!=null) {
				fos.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("file is not exist");
		} finally {
			if(fos!=null) {
				fos.close();
			}
		}
	}	
	
	
	synchronized public static void setValue(String filePath, String key, String value) throws Exception {
		String result = null;
		
		System.out.println("setValue, filepath=" + filePath + ", key=" + key + ", value=" + value);
		
		InputStream path = PropertyUtil.class.getClassLoader().getResourceAsStream(filePath);
		FileOutputStream fos = null;
		Properties pros = new Properties();
		try {
			pros.load(path);
			pros.setProperty(key, value);
			if(path!=null) {
				path.close();
			}

			// 文件输出流      
			fos = new FileOutputStream("/work/apache-tomcat-7.0.53/webapps/footballServer/WEB-INF/classes" + filePath);
			pros.store(fos, "update");
			if(fos!=null) {
				fos.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("file is not exist");
		} finally {
			if(fos!=null) {
				fos.close();
			}
			if(path!=null) {
				path.close();
			}
		}
	}	
}
