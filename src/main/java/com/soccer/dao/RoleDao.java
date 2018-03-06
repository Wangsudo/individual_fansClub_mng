/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.soccer.model.AdminUser;
import com.soccer.model.Role;
import com.soccer.util.PageResult;
import com.soccer.util.Search;


/**
 * @author 002195
 * 
 */
@Repository("roleDao")
public class RoleDao extends BaseDao<Role> {
	@Resource
	private AdminUserDao adminUserDao;
	/**
	 * 通过id删除用户
	 * @param id
	 */
	public boolean delById(Long id) throws Exception {
		Role one = this.findById(Role.class, id);
		String querysString="from AdminUser where role.id ="+id;
		List<AdminUser> adminUsers = getSession().createQuery(querysString).list();
		if (adminUsers.isEmpty()) {
			this.delete(one);
			return true;
		}
		return false;
	}
	
	public List findAll() throws Exception{
		return this.findByHql("select u.id as key,u.roleName as value from Role u",Collections.EMPTY_LIST);
	}
	
	public Long getCount(Long roleId)  throws Exception{
		Query query = this.getSession().createQuery("select count(1) from AdminUser u where u.role.id =? ");
		query.setParameter(0, roleId);
		Object count = query.uniqueResult();
		return Long.valueOf(count.toString());
	}
	
	public PageResult<Role> findByPage(Search search)  throws Exception {
		PageResult<Role> ret = new PageResult<Role>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "from Role u where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		//姓名
		if (search.getId()!=null) {
			hql += " and u.id like ? escape '/' ";
			params.add(search.getId());
     	}
		
		if(search.getOrderby()!=null){
			hql+=" order by u."+search.getOrderby();
		}else{
			hql+=" order by u.id asc";
		}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}
      
}
