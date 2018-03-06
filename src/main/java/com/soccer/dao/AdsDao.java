/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.Ads;
import com.soccer.util.PageResult;
import com.soccer.util.Search;


/**
 * @author Feng Jianli
 */
@Repository("adsDao")
public class AdsDao extends BaseDao<Ads> {
	public PageResult<Ads> findByPage(Search search) throws Exception {
		PageResult<Ads> ret = new PageResult<Ads>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Ads u where 1=1 ";
			//广告位置信息
			if (search.getPosition()!=null) {
				hql += " and u.position =?";
				params.add(search.getPosition());
			}
			 // 状态
	        if (search.getStatus() != null) {
	        	switch(search.getStatus()){
	        	//草稿 
	        	case 1:  hql += " and u.status = 1 ";break;
	        	//上线
	        	case 2:  hql += " and u.status = 2 and u.startTime <= now() and (u.stopTime is null or u.stopTime > now())";break;
	        	//待上线
	        	case 3:  hql += " and u.status = 2 and u.startTime > now()";break;  
	        	//过期
	        	case 4:  hql += " and u.status = 2 and u.stopTime <= now()";break;  
	        	}
	        }

	        // 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
			
			// 上档时间
	        if (search.getFromDate() != null) {
	            hql += " and u.startTime <= ?";
	            params.add(search.getFromDate());
	            hql += " and (u.stopTime is null or u.stopTime > ?)";
	            params.add(search.getFromDate());
	        }
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
	@SuppressWarnings("unchecked")
	public List<Ads> findAds(Integer position){
		String hql = "from Ads u where u.status = 2 and (u.startTime is null or u.startTime <= now()) and (u.stopTime is null or u.stopTime > now())";
		List<Object> params = new ArrayList<Object>();
		if (position!=null) {
			hql += " and u.position =?";
			params.add(position);
		}
		return this.findByHql(hql,params);
	}
	
}
