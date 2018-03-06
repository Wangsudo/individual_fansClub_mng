package com.soccer.util;

import com.alibaba.fastjson.JSONObject;
import mediautil.image.jpeg.AbstractImageInfo;
import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.LLJTran;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PicUtil {

	private static Integer[] smallDimens = {64,64};
	private static Integer[] medianDimens = {640,640};
	private static Integer[] largeDimens = {1280,1280};
	
	public static int[] getPicDem(File picture){
		  BufferedImage sourceImg;
		  int width = 0;
		  int heigth= 0;
		try {
			sourceImg = ImageIO.read(picture);
			 width = sourceImg.getWidth();
			 heigth = sourceImg.getHeight();
		} catch (Exception e) {
			  e.printStackTrace();
		}
		 return new int[]{width,heigth};
	}
	
	//存放三张图片的路径,将其中的指向的临时文件转向正式文件夹
		public static String transfer3size( File f,String appPath,String toFoler) throws Exception{
			JSONObject pathJson = new JSONObject();
			Image image  = turnImage(f);
			String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			pathJson.put("1", zipImageFile(image,smallDimens,nowTime,appPath,toFoler));
			pathJson.put("2",zipImageFile(image,medianDimens,nowTime,appPath,toFoler));
			pathJson.put("3",zipImageFile(image,largeDimens,nowTime,appPath,toFoler));
			return pathJson.toJSONString();
		}
		
		public static String zipImage( File f,String appPath,String toFoler) throws Exception{
			Image image  = turnImage(f);
			String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String url = zipImageFile(image,largeDimens,nowTime,appPath,toFoler);
			return url;
		}
	
	 public static String transferOriginal(File f,String appPath,String toFoler) throws Exception {
		   String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		   Image image  = turnImage(f);
		   String filepath = convertImageFile(image, nowTime, appPath, toFoler);
		   return filepath;
	}
	
	public static String transferToLoc(String jsonStr,String basePath,String toFolder) {
		JSONObject json = null;
		try {
			json = JSONObject.parseObject(jsonStr);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(json == null ){
			return jsonStr;
		}
		for (String key : new String []{"1","2","3"}){
			String path = json.getString(key);
			if(!StringUtil.isEmpty(path) && !path.startsWith(toFolder)){
				String newUrl = "";
				try {
					newUrl = transfer(basePath,path,toFolder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				json.put(key, newUrl);
			}
		}
		return json.toJSONString();
	}
	
	/*
	 * 
	 */
	public static String transfer3Gray(String basePath,String jsonStr) {
		JSONObject json = null;
		try {
			json = JSONObject.parseObject(jsonStr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(json == null ){
			return jsonStr;
		}
		for (String key : new String []{"1","2","3"}){
			String path = json.getString(key);
				String newUrl = "";
				try {
					newUrl = transferGray(basePath,path);
				} catch (Exception e) {
					e.printStackTrace();
				}
				json.put(key, newUrl);
		}
		return json.toJSONString();
	}
	
	private static String transferGray(String basePath, String path) throws IOException {
		File oriFile = new File(basePath + path);
		if (!oriFile.exists()) {
			return "";
		}
		String newPath = path.replace(".jpg","_gray.jpg");
		File newFile = new File(basePath + newPath);
		 BufferedImage src = ImageIO.read(oriFile);  
		int imageWidth = src.getWidth();  
	    int imageHeight = src.getHeight();  
        BufferedImage newPic = new BufferedImage(imageWidth, imageHeight,    BufferedImage.TYPE_3BYTE_BGR);  
        ColorConvertOp cco = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);  
        cco.filter(src, newPic);  
	        
    	FileOutputStream out = new FileOutputStream(newFile);
		ImageIO.write(newPic,"JPEG",out);
		out.close();   
		return newPath;
	}

	public static String transfer(String basePath,String oriPath,String toFolder) throws Exception{
		
		File oriFile = new File(basePath + oriPath);
		if (!oriFile.exists()) {
			return "";
		}
		 File toFolderFull = new File(basePath + toFolder);
	 	if (!toFolderFull.exists()) {
	 		toFolderFull.mkdirs();
		}
	 	
	 	 String fileName = oriFile.getName();
	 	String toPath = toFolderFull + "/" + fileName;
	     //获取待复制文件的文件名
	     File toFile = new File(toPath);
	     if(!toFile.exists()){
	    	 copy(oriFile,toFile);
		 }
		String newUrl = toFolder+"/" +fileName;
		return newUrl;
	}
	
	private static void copy(File srcFile, File destFile) throws Exception {
		// 复制文件
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			bos = new BufferedOutputStream(new FileOutputStream(destFile));
			byte[] buf = new byte[8192];
			for (int count = -1; (count = bis.read(buf)) != -1;) {
				bos.write(buf, 0, count);
			}
			bos.flush();
		} catch (IOException ie) {
			throw new Exception("copy失败");
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}
	
	private boolean delFile(String url){
		if(StringUtil.isEmpty(url)){
			return false;
		}
		// 应用根目录
		File destFile = new File(url);
		if(destFile.exists()){
			return destFile.delete();
		}
		return true;
	}
	
	/**
	 * 直接指定压缩后的宽高：
	 * (先保存原文件，再压缩、上传)
	 * 壹拍项目中用于二维码压缩
	 * @param oldFile 要进行压缩的文件全路径
	 * @param width 压缩后的宽度
	 * @param height 压缩后的高度
	 * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
	 * @return 返回压缩后的文件的全路径
	 */
	public static String zipImageFile(Image image, Integer[] dimens,
			 String fileNamePrefix,String appPath,String relaFolder) {
		int w = dimens[0];
		int h = dimens[1];
		String fileName = null;
		String newImage = null;
		try {
			/**对服务器上的临时文件进行处理 */
			int srcWidth = image.getWidth(null);
			int srcHeight = image.getHeight(null);
			  if (srcWidth*1.0 / srcHeight >= w*1.0 / h && srcWidth > w){
				  h = (int) (srcHeight * w / srcWidth);  
			  }else if(srcWidth*1.0 / srcHeight < w*1.0 / h && srcHeight > h){
				  w = (int) ( srcWidth * h / srcHeight);  
			  }else{
				  h = srcHeight;
				  w = srcWidth;
			  }
				//图片 用于标实唯一性的key， 对于静态图片，key为img文件夹中图名称，而对于需经常更新的banner图，key为生成的固定的格式的文件名
			 File basePath = new File(appPath + relaFolder);
			 if (!basePath.exists()) {
					basePath.mkdirs();
			 }
				/** 压缩后的文件名 */
			 fileName =fileNamePrefix+"-" + w+ "x" + h ;
			newImage = basePath+"/"+ fileName+ ".jpg";
			if(new File(newImage).exists()){
				return relaFolder +"/"+ fileName+ ".jpg";
			}
			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(image, 0, 0, w, h,Color.WHITE, null);
		
			/** 压缩之后临时存放位置 */
			FileOutputStream out = new FileOutputStream(newImage);
			ImageIO.write(tag,"JPEG",out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relaFolder +"/"+ fileName+ ".jpg";
	}

	public static Image turnImage(File file) throws IOException{
		   BufferedImage src = ImageIO.read(file);  
		   Integer turn=0;  
		   InputStream fip = new FileInputStream(file);
		   LLJTran llj = new LLJTran(fip);  
	       try {
			llj.read(LLJTran.READ_INFO, true);
			} catch (Exception e) {
				return src;
			}  
	       AbstractImageInfo<?> imageInfo = llj.getImageInfo();
	       if (imageInfo instanceof Exif){
	    	   Exif exif = (Exif) llj.getImageInfo();
	           int orientation = 1;
	           Entry orientationTag = exif.getTagValue(Exif.ORIENTATION, true);
	           if (orientationTag != null){
	        	   orientation = (Integer) orientationTag.getValue(0);
	           }
	    	      if(orientation==0||orientation==1)  
	    	      {  
	    	          turn=0;  
	    	      }  
	    	      else if(orientation==3)  
	    	      {  
	    	          turn=180;  
	    	      }  
	    	      else if(orientation==6)  
	    	      {  
	    	          turn=90;  
	    	      }  
	    	      else if(orientation==8)  
	    	      {  
	    	          turn=270;  
	    	      }  
	       }
		    
		      BufferedImage ret = src;
		      if(turn >0){
		    	  ret = RotateImage.Rotate(src, turn);
		      }
		      return ret;
		}
	
	/**
	 * 使用原文件的宽高压缩
	 * (先保存原文件，再压缩、上传)
	 * @param oldFile 要进行压缩的文件全路径
	 * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
	 * @return 返回压缩后的文件的全路径
	 */
	public static String convertImageFile(Image image,String fileNamePrefix,String appPath,String relaFolder) {
		String fileName = null;
		String newImage = null;
		try {
			/**对服务器上的临时文件进行处理 */
			int w = image.getWidth(null);
			int h = image.getHeight(null);
				//图片 用于标实唯一性的key， 对于静态图片，key为img文件夹中图名称，而对于需经常更新的banner图，key为生成的固定的格式的文件名
			 File basePath = new File(appPath + relaFolder);
			 if (!basePath.exists()) {
					basePath.mkdirs();
			 }
				/** 压缩后的文件名 */
			 fileName =fileNamePrefix+"-" + w+ "x" + h ;
			newImage = basePath+"/"+ fileName+ ".jpg";
			if(new File(newImage).exists()){
				return relaFolder +"/"+ fileName+ ".jpg";
			}
			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(image, 0, 0, w, h,Color.WHITE, null);
		
			/** 压缩之后临时存放位置 */
			FileOutputStream out = new FileOutputStream(newImage);
			ImageIO.write(tag,"JPEG",out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relaFolder +"/"+ fileName+ ".jpg";
	}
	
	public static String cropImage(CropImage info,String basePath,String toFolder) throws IOException{
		String filePath = basePath +"/"+ info.getPath();
		String fileName = null;
		String newImage = null;
			BufferedImage image = ImageIO.read(new File(filePath));
			/**对服务器上的临时文件进行处理 */
				//图片 用于标实唯一性的key， 对于静态图片，key为img文件夹中图名称，而对于需经常更新的banner图，key为生成的固定的格式的文件名
			 File toPath = new File(basePath + toFolder);
			 if (!toPath.exists()) {
				 toPath.mkdirs();
			 }
				/** 压缩后的文件名 */
			 fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +"-" + info.getX()+ "x" + info.getY() ;
			newImage = toPath+"/"+ fileName+ ".jpg";
			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(info.getW(),info.getH(), BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(image,  0, 0, info.getW(), info.getH(), info.getX(), info.getY(), info.getX2(),info.getY2(),Color.WHITE, null);
		
			/** 压缩之后临时存放位置 */
			FileOutputStream out = new FileOutputStream(newImage);
			ImageIO.write(tag,"JPEG",out);
			out.close();
		return toFolder +"/"+ fileName+ ".jpg";
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		zipImageFile("Z:/360wallpaper_dt.bmp",smallDimens,0.8f,"SS");
//		zipImageFile("Z:/360wallpaper_dt.bmp",medianDimens,0.8f,"MM");
//	   File file = new File("Z:/360wallpaper_dt.bmp");
	   
	   transferGray("z:/","球员详情-队长.png");
	}

}
