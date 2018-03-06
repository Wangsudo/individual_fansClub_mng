/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.AdminUser;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;

/**
 * @author 002195
 * 
 */
@Repository("adminUserDao")
@SuppressWarnings("unchecked")
public class AdminUserDao extends BaseDao<AdminUser> {

	/**
	 * @param user
	 * @param pageResult
	 * @return
	 */
	public PageResult<AdminUser> findByPage(Search search)  throws Exception {
		PageResult<AdminUser> ret = new PageResult<AdminUser>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "from AdminUser u where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		//姓名
		if (!StringUtil.isEmpty(search.getName()) ) {
			hql += " and u.name like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
     	}
		//帐号
		if (!StringUtil.isEmpty(search.getAccount()) ) {
			hql += " and u.account like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getAccount().trim()) + "%");
     	}
		
		//角色
		if (search.getDictId()!=null) {
			hql += " and u.role.id = ?";
			params.add(search.getDictId());
     	}
		
		if(search.getOrderby()!=null){
			hql+=" order by u."+search.getOrderby();
		}else{
			hql+=" order by case u.account when 'superadmin' then 1 else 0 end desc, u.modifyTime desc";
		}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}
	
	
	/**
	 * 通过登录账号查找用户
	 * 
	 * @param deId
	 * @return
	 */
	public AdminUser findUserByAccount(String account) throws Exception{
		AdminUser user = null;
		boolean inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		String hql = " from AdminUser a where a.account = ?";
		Query query = this.getSession().createQuery(hql).setParameter(0, account);
		inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		List<AdminUser> userList = query.list();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		return user;
	}
	
	
	/**
	 * 通过RoleId删除管理员
	 * 
	 * @param roleId
	 */
	public int delUserByRoleId(Long roleId) throws Exception {
		List<Object> params = new ArrayList<Object>();
		String hqlUpdate = "delete AdminUser r where r.userRole.id = ?" ;
    	params.add(roleId);
    	Integer num =  excuteUpdate(hqlUpdate, params);	
    	return num;
	}
	
	public void changePassword(Long adminId, String password){
		AdminUser adminUser = super.findById(AdminUser.class, adminId);
		adminUser.setForgetPasswordTime(null);
		adminUser.setPassword(password);
		this.saveOrUpdate(adminUser);
	}
	
	
	/**
	 * 找出所有管理员，形成选择控件。
	 * @return
	 */
	public List<AdminUser> findAllAdminUser(){
		String hql = "select new com.soccer.model.AdminUser(id,account) from AdminUser";
		List<AdminUser > ret = this.findByHql(hql, null);
		return ret;
	}
      
	public JSONObject check(AdminUser adminUser) {
		JSONObject ret = new JSONObject();
		String queryString = null;
		if (adminUser.getId()!=null) {
			queryString = "from AdminUser a where a.id <> "+adminUser.getId();
		}else {
			queryString = "from AdminUser a where 1=1 ";
		}
		
		String queryString1 = queryString+"and a.account = "+"'"+adminUser.getAccount()+"'";
		if (!super.findByHql(queryString1,Collections.EMPTY_LIST).isEmpty()) {
			ret.put("account", false);
		}else {
			ret.put("account", true);
		};
		
		String queryString2 = queryString+"and a.email = "+"'"+adminUser.getEmail()+"'";
		if (!super.findByHql(queryString2,Collections.EMPTY_LIST).isEmpty()) {
			ret.put("email", false);
		}else {
			ret.put("email", true);
		};
		
		String queryString3 = queryString+"and a.teleNum = "+"'"+adminUser.getTeleNum()+"'";
		if (!super.findByHql(queryString3,Collections.EMPTY_LIST).isEmpty()) {
			ret.put("teleNum", false);
		}else {
			ret.put("teleNum", true);
		};
		return ret;
	}
}
