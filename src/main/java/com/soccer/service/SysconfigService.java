package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.SysconfigDao;
import com.soccer.model.Sysconfig;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("sysconfigService")
public class SysconfigService extends BaseService<Sysconfig>{

	@Resource(name = "sysconfigDao")
	private SysconfigDao sysconfigDao;
	
	@Transactional(readOnly=true)
	public PageResult<Sysconfig> findByPage(Search search) throws Exception {
		return sysconfigDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public Sysconfig findByCode(String code) {
		return sysconfigDao.findByCode(code);
	}

	@Transactional(readOnly=true)
	public List<Sysconfig> findAll(List<String> codes) {
		return sysconfigDao.findAll(codes);
	}
	
}
