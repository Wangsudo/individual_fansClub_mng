package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.CupDao;
import com.soccer.model.Cup;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("cupService")
public class CupService extends BaseService<Cup>{

	@Resource(name = "cupDao")
	private CupDao cupDao;
	
	@Transactional(readOnly=true)
	public PageResult<Cup> findByPage(Search search) throws Exception {
		return cupDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List searchCup(String name, Integer isPublic,Integer pageSize){
		return cupDao.searchCup(name, isPublic,pageSize);
	}
	
	@Transactional(readOnly=true)
	public List<Cup> getCups(Long teamA,Long teamB){
		return cupDao.getCups(teamA, teamB);
	}

	@Transactional(readOnly=true)
	public List getPhases(Long cupId) {
		return cupDao.getPhases(cupId);
	}

	@Transactional(readOnly=true)
	public List<Cup> getPublicCups() {
		return cupDao.getPublicCups();
	}
	
}
