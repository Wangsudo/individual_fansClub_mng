package com.soccer.service;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.AdminExecLogDao;
import com.soccer.dao.AdminUserDao;
import com.soccer.model.AdminExecLog;
import com.soccer.model.AdminUser;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("adminExecLogService")
public class AdminExecLogService extends BaseService<AdminExecLog>{

	@Resource(name = "adminExecLogDao")
	private AdminExecLogDao adminExecLogDao;

	@Resource(name = "adminUserDao")
	private AdminUserDao adminUserDao;
	/**
	 * @param user
	 * @param pageResult
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly=true)
	public PageResult<AdminExecLog> findByPage(Search search) throws Exception {
		return adminExecLogDao.findByPage(search);
	}
	
	/**
	 * @param admin
	 * @param opdesc
	 * @param beanName
	 */
	@Transactional
	public void saveExecLog(HttpSession session, String opdesc, String beanName,Long beanId) {
		Long adminId = (Long) session.getAttribute("admin");
        AdminExecLog log = new AdminExecLog();
        if(adminId!=null){
        	log.setAdmin(adminUserDao.findById(AdminUser.class, adminId));
        }
        log.setOpdesc(opdesc);
        log.setBeanName(beanName);
        log.setBeanId(beanId);
        log.setOptime(new Timestamp(new Date().getTime()));
        adminExecLogDao.save(log);
    }
    
}
