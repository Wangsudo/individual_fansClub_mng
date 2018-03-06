package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.RoleDao;
import com.soccer.model.AdminUser;
import com.soccer.model.Role;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * @author 002195
 * 
 */
@Service("roleService")
public class RoleService extends BaseService<Role>{

	@Resource(name = "roleDao")
	private RoleDao roleDao;

	
	@Transactional(readOnly=true)
	public PageResult<Role> findByPage(Search search) throws Exception{
		PageResult<Role> ret = roleDao.findByPage(search);
		 for(Role role : ret.getList()){
			 role.setCount(roleDao.getCount(role.getId()));
		 }
		return ret;
	}
	
	@CacheEvict(value="myCache", key="'getRoleDict'")  
	@Transactional
	public void saveOrUpdate(Role one) throws Exception{
		roleDao.saveOrUpdate(one);
	}
	@CacheEvict(value="myCache", key="'getRoleDict'")  
	@Transactional
	public void delete(Role one) {
		roleDao.delete(one);
	}
	@CacheEvict(value="myCache", key="'getRoleDict'")  
	@Transactional
	public boolean delByClass(Long id) throws Exception {
		return roleDao.delById(id);
	}
	@CacheEvict(value="myCache", key="'getRoleDict'")  
	@Transactional
	public void del(Class clazz,List<Long> ids) {
		roleDao.del(clazz,ids);
	}
	
}
