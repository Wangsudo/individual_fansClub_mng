/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.News;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("newsDao")
public class NewsDao extends BaseDao<News> {
	public PageResult<News> findByPage(Search search) throws Exception {
		PageResult<News> ret = new PageResult<News>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from News u where 1=1 ";
			if (!StringUtil.isEmpty(search.getTitle())) {
				hql += " and u.title like ? escape '/'";
				params.add("%" + StringUtil.escapeSQLLike(search.getTitle().trim()) + "%");
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
	
}
