package com.soccer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.soccer.dao.AdminUserDao;
import com.soccer.model.AdminUser;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * @author 002195
 * 
 */
@Service("adminUserService")
public class AdminUserService extends BaseService<AdminUser>{

	@Resource(name = "adminUserDao")
	private AdminUserDao adminUserDao;

	/**
	 * 
	 * @param search
	 * @return
	 */
	@Transactional(readOnly=true)
	public PageResult<AdminUser> findByPage(Search search) throws Exception{
		return adminUserDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public AdminUser findUserByAccount(String account)  throws Exception{
		return adminUserDao.findUserByAccount(account);
	}
	
	@Transactional(readOnly=true)
	public JSONObject ckeck(AdminUser adminUser) {
		return adminUserDao.check(adminUser);
	}
	@Transactional
	public void changePassword(Long adminId,String password) {
		adminUserDao.changePassword(adminId, password);
	}
	
}
