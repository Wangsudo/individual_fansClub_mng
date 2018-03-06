package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.HonorDao;
import com.soccer.model.Honor;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("honorService")
public class HonorService extends BaseService<Honor>{

	@Resource(name = "honorDao")
	private HonorDao honorDao;
	
	@Transactional(readOnly=true)
	public PageResult<Honor> findByPage(Search search) throws Exception {
		return honorDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List searchHonor(String name,Integer pageSize){
		return honorDao.searchHonor(name,pageSize);
	}

	/**
	 * 
	 * @param teamId
	 * @param playerId
	 * @param type 为1时 球员荣誉 , 为2时 球队荣誉
	 * @return
	 */
	@Transactional(readOnly=true)
	public List getOthers(Long teamId,Long playerId,Integer type) {
		return honorDao.getOthers(teamId,playerId,type);
	}
    
}
