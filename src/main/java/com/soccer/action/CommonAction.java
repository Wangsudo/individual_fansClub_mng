package com.soccer.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.service.CommonService;
import com.soccer.util.CropImage;
import com.soccer.util.MyConstants;
import com.soccer.util.PicUtil;

@RestController
@RequestMapping(value = "/common")
public class CommonAction{
	private static Logger log = LogManager.getLogger(CommonAction.class);
	
	@Resource(name = "commonService")
	private CommonService commonService;
	
	@RequestMapping(value = "/getDicts", method = RequestMethod.GET)
	public JSONObject getDicts() {
		JSONObject ret = new JSONObject();
		try {
			ret.put("roleList", commonService.getRoleDict());
			ret.put("adminList", commonService.findAllAdminUser());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		return ret;
	}
	
	@RequestMapping(value = "/ueditor")
	public JSONObject ueditor(@RequestParam(value="action",required = false) String action,HttpServletRequest req) {
		JSONObject ret = new JSONObject();
		try {
			if(action == null){
				ret.put("state", "command error!");
			}else if(action.equals("config")){
				config(ret);
			}else if(action.equals("uploadimage")){
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
				MultipartFile file = multipartRequest.getFile("file");
				ret = ueditorUploadImage(file);
			}else{
				ret.put("state", "command doesn't exist!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		return ret;
	}
	
	@RequestMapping(value = "/imageUp", method = RequestMethod.POST)
	public JSONObject imageUp(@RequestParam("upfile") MultipartFile file,@RequestParam(value="name",required = false) String name
			,@RequestParam(value="filename",required = false) String filename) {
		JSONObject ret  =   ueditorUploadImage(file);
		return ret;
	}
	
	private JSONObject ueditorUploadImage(MultipartFile file){
		JSONObject ret = new JSONObject();
		long size =file.getSize();
		if(size> MyConstants.IMG_MAXSIZE){
			ret.put("state", "Uploading image size exceeds 1M！");
			return ret;
		}else{
			ret.put("size",size);
		}
		//设置图片信息
		try {
			File tempDir = new File(InitServlet.APPLICATION_URL + MyConstants.TEMP_UPLOAD_PATH);
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
			File basePath = new File(InitServlet.APPLICATION_URL + MyConstants.UPLOAD_PATH_UEDITOR);
			 if (!basePath.exists()) {
					basePath.mkdirs();
			 }
			 ret.put("original",file.getOriginalFilename());
			 ret.put("type",".jpg");
			String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String tempPath = MyConstants.TEMP_UPLOAD_PATH+"/"+nowTime+file.getOriginalFilename();
			
			File f = new File(InitServlet.APPLICATION_URL+tempPath);
			file.transferTo(f);
			ret.put("url",PicUtil.zipImage(f,InitServlet.APPLICATION_URL,MyConstants.UPLOAD_PATH_UEDITOR ));
	        ret.put("state","SUCCESS");
		} catch (Exception e) {
			ret.put("state", e.getMessage());
			e.printStackTrace();
		} 
		return ret;
	}
	
	private void config(JSONObject data){
		data.put("imageActionName", "uploadimage"); /* 执行上传图片的action名称 */
		data.put("imageFieldName", "file");/* 提交的图片表单名称 */
		data.put("imageMaxSize", MyConstants.IMG_MAXSIZE);/* 上传大小限制，单位B */
		data.put("imageAllowFiles", new String[]{".png", ".jpg", ".jpeg", ".gif", ".bmp"});/* 上传图片格式显示 */
		data.put("imageCompressEnable", true);/* 是否压缩图片,默认是true */
		data.put("imageCompressBorder", 1600);/* 图片压缩最长边限制 */
		data.put("imageInsertAlign", "none");/* 插入的图片浮动方式 */
		data.put("imageUrlPrefix", ""); /* 图片访问路径前缀 */
		data.put("imagePathFormat", MyConstants.UPLOAD_PATH_UEDITOR+"/size*{yyyy}{mm}{dd}{time}{rand:6}");/* 上传保存路径,可以自定义保存路径和文件名格式 */
	}
	

	@RequestMapping(value = "/uploadImgs", method = RequestMethod.POST)
	public JSONArray uploadImgs(@RequestParam("file") MultipartFile[] files,@RequestParam(value="original",required = false) Boolean original) {
		JSONArray array = new JSONArray();
			 //判断file数组不能为空并且长度大于0  
	        if(files!=null&&files.length>0){  
	            //循环获取file数组中得文件  
	            for(int i = 0;i<files.length;i++){  
	                MultipartFile file = files[i];  
	                //保存文件  
	               array.add(commonService.saveImage(file,original));
	            }  
	        }  
		return array;
	}
	/**
	 * 添加图片信息
	 * @return 如果图片存在大于1M的返回添加页面
	 * @throws IOException 
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
	public JSONObject uploadImg(@RequestParam("file") MultipartFile file,
			@RequestParam(value="original",required = false) Boolean original) {
		 return commonService.saveImage(file,original);
	}
	
	@RequestMapping(value = "/cropImage", method = RequestMethod.POST)
	public JSONObject cropImage(@RequestBody CropImage info) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			String relaPath =  PicUtil.cropImage(info, InitServlet.APPLICATION_URL, MyConstants.TEMP_UPLOAD_PATH);
			File file = new File(InitServlet.APPLICATION_URL + relaPath);
			ret.put("picPath",PicUtil.transfer3size(file,InitServlet.APPLICATION_URL,MyConstants.TEMP_UPLOAD_PATH ));
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			ret.put("error", e.getMessage());
		}
	     ret.put("success", success);
		return ret;
	}
	
	
	
	/**
	 * 添加图片信息
	 * @return 如果图片存在大于1M的返回添加页面
	 * @throws IOException 
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
	public JSONObject uploadVideo(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
			JSONObject ret = new JSONObject();
			//设置图片信息
			try {
				File basePath = new File(InitServlet.APPLICATION_URL + MyConstants.TEMP_UPLOAD_PATH);
				 if (!basePath.exists()) {
						basePath.mkdirs();
				 }
				String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				String tempPath = MyConstants.TEMP_UPLOAD_PATH+"/"+nowTime+file.getOriginalFilename();
				File f = new File(InitServlet.APPLICATION_URL+tempPath);
				file.transferTo(f);
				
				ret.put("size",file.getSize() / 1024);
				ret.put("name", file.getOriginalFilename());
				ret.put("url",tempPath);
		        ret.put("success",true);
			} catch (Exception e) {
				ret.put("success",false);
				ret.put("message", e.getStackTrace());
				log.error(e.getStackTrace());
			}
		return ret;
	}
	
	/**
	 * 添加apk信息
	 * @return 如果图片存在大于1M的返回添加页面
	 * @throws IOException 
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/uploadApk", method = RequestMethod.POST)
	public JSONObject uploadApk(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
	    	JSONObject ret = new JSONObject();
			//设置图片信息
			String nowTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			try {
				
				//图片 用于标实唯一性的key， 对于静态图片，key为img文件夹中图名称，而对于需经常更新的banner图，key为生成的固定的格式的文件名
				String key = nowTime + "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
				ret.put("url", MyConstants.TEMP_UPLOAD_PATH+MyConstants.FILE_SEPARATOR+key);
				ret.put("name", file.getOriginalFilename());
				
				File basePath = new File(request.getSession().getServletContext().getRealPath(MyConstants.TEMP_UPLOAD_PATH) );
				if (!basePath.exists()) {
					basePath.mkdirs();
				}
				 File destFile = new File(basePath.getCanonicalPath()+ MyConstants.FILE_SEPARATOR + key);
		          file.transferTo(destFile);
		          ret.put("success",true);
			} catch (IOException e) {
				ret.put("success",false);
				log.error(e.getStackTrace());
				e.printStackTrace();
			}
		return ret;
	}
	
	@RequestMapping(value = "/error_fileupload")
	public JSONObject error_fileupload(Exception ex) {
		JSONObject ret = new JSONObject();
		ret.put("message", "Uploading file size exceeds 20M！");
		ret.put("success",false);
		return ret;
	}
	
}
 
