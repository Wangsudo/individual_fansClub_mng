package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.LogoDao;
import com.soccer.model.Logo;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("logoService")
public class LogoService extends BaseService<Logo>{

	@Resource(name = "logoDao")
	private LogoDao logoDao;
	
	@Transactional(readOnly=true)
	public PageResult<Logo> findByPage(Search search) throws Exception {
		return logoDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List searchLogo(String name,Integer pageSize){
		return logoDao.searchLogo(name,pageSize);
	}

	@Transactional(readOnly=true)
	public List getAll() {
		return logoDao.getAll();
	}
    
}
