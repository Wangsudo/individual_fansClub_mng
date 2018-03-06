package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.AdsDao;
import com.soccer.model.Ads;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("adsService")
public class AdsService extends BaseService<Ads>{

	@Resource(name = "adsDao")
	private AdsDao adsDao;
	
	@Transactional(readOnly=true)
	public PageResult<Ads> findByPage(Search search) throws Exception {
		return adsDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List<Ads> findAds(Integer position){
		return adsDao.findAds(position);
	}
}
