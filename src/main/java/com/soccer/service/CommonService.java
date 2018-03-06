package com.soccer.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.soccer.dao.AdminUserDao;
import com.soccer.dao.CommonDao;
import com.soccer.dao.RoleDao;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.AdminUser;
import com.soccer.model.Player;
import com.soccer.util.MyConstants;
import com.soccer.util.PicUtil;

/**
 * @author 002195
 * 
 */
@Service("commonService")
public class CommonService {

	@Resource(name = "roleDao")
	private RoleDao roleDao;
	
	@Resource(name = "commonDao")
	private CommonDao commonDao;
	
	@Resource(name = "adminUserDao")
	private AdminUserDao adminUserDao;
	
	//获取角色字典
//	@Cacheable(value="myCache",key="'getRoleDict'")
	@Transactional(readOnly=true)
	public List getRoleDict() throws Exception{
		return roleDao.findAll();
	}
	
	/**
	 * 找出所有管理员，形成选择控件。
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<AdminUser> findAllAdminUser(){
		return adminUserDao.findAllAdminUser();
	}
	
	public JSONObject saveImage(MultipartFile file,Boolean original){
		JSONObject ret = new JSONObject();
		if(file.getSize()> MyConstants.IMG_MAXSIZE){
			ret.put("error", "Uploading image size exceeds 1M！");
			ret.put("success",false);
			return ret;
		}
        
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
			if(original!=null && original == true){
				ret.put("picPath",tempPath);
			}else{
				ret.put("picPath",PicUtil.transfer3size(f,InitServlet.APPLICATION_URL,MyConstants.TEMP_UPLOAD_PATH ));
			}
	        ret.put("success",true);
		} catch (Exception e) {
			ret.put("success",false);
			ret.put("error", e.getStackTrace());
		} 
		return ret;
	}

	@Transactional(readOnly=true)
	public Integer getUnreadCount(Integer noticeType, Player player, Long lastReadTime) {
		 return  commonDao.getUnreadCount( noticeType,  player,  lastReadTime);
	}

}
