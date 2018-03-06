package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.MenuDao;
import com.soccer.model.Menu;

@Service("menuService")
public class MenuService {

	@Resource(name = "menuDao")
	private MenuDao menuDao;

	//@Cacheable(value="myCache")
	@Transactional(readOnly=true)
	public List<Menu> findAll() throws Exception{
			return  menuDao.findAll();
	}

	@Transactional
	public void save(Menu one) throws Exception{
		menuDao.save(one);
	}
	
	@Transactional
	public void saveOrUpdate(List<Menu> many) throws Exception{
		menuDao.saveOrUpdate(many);
	}
	
	
}
