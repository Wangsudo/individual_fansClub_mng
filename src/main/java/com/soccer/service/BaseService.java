package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.BaseDao;

public class BaseService<T> {
	
	 @Resource(name = "baseDao")
	 private BaseDao<T> baseDao;

	/**
	 * @param user
	 * @throws Exception
	 */
	@Transactional
	public void saveOrUpdate(T one) throws Exception{
		baseDao.saveOrUpdate(one);
	}
	@Transactional
	public void save(T one) throws Exception{
		baseDao.save(one);
	}
	
	@Transactional
	public void save(List<T> many) throws Exception{
		baseDao.save(many);
	}
	
	@Transactional
	public void saveOrUpdate(List<T> many) throws Exception{
		baseDao.saveOrUpdate(many);
	}
	
	@Transactional
	public void delete(T one) throws Exception{
		baseDao.delete(one);
	}
	
	@Transactional
	public void del(Class clazz,Long id) throws Exception{
		baseDao.del(clazz,id);
	}
	
	@Transactional
	public void del(Class clazz,List<Long> ids) throws Exception{
		baseDao.del(clazz,ids);
	}
	
	@Transactional(readOnly = true)
	public T findById(Class clazz,Long id)throws Exception{
		return baseDao.findById(clazz,id);
	}
	
}
