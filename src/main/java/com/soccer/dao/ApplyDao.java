/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Apply;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * @author Feng Jianli
 */
@Repository("applyDao")
public class ApplyDao extends BaseDao<Apply> {
	public PageResult<Apply> findByPage(Search search) throws Exception {
		PageResult<Apply> ret = new PageResult<Apply>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Apply u where 1=1 ";
			//标题
			if (!StringUtil.isEmpty(search.getTitle())) {
				hql += " and u.title like ? escape '/'";
				params.add("%" + StringUtil.escapeSQLLike(search.getTitle().trim()) + "%");
			}
			
			if(search.getPlayerId()!=null){
				hql += " and u.player.id = ? ";
				params.add(search.getPlayerId());
			}
			// 赛制
			if(search.getTeamType()!=null  &&  search.getTeamType()!=0){
				hql += " and u.dreamType = ? ";
				params.add(search.getTeamType());
			}
			
			//模拟查询
			if (!StringUtil.isEmpty(search.getSnippet())) {
				hql += " and (u.player.name like ? escape '/')";
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			}
			
			//球员姓名
			if (!StringUtil.isEmpty(search.getName()) ) {
				hql += " and u.player.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
	     	}
			//是否公开
			 if (search.getIsPublic()!= null) {
		        	hql += " and u.isPublic =?";
		        	params.add(search.getIsPublic());
		     }
			 
			 if (search.getIsOpen()!= null) {
		        	hql += " and u.isOpen =?";
		        	params.add(search.getIsOpen());
		     }
			 if (search.getIsEnabled()!= null) {
		        	hql += " and u.isEnabled =?";
		        	params.add(search.getIsEnabled());
		     }

	        // 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
	        
			// 发布时间
	        if (search.getFromDate() != null) {
	            hql += " and u.applyTime >= ?";
	            params.add(search.getFromDate());
	        }
	        
	      //发布结束时间
			if (search.getToDate() != null) {
				hql += " and u.applyTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.applyTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
	public List<Apply> findOpenApply(Long playerId) {
		String hql = "from Apply u where u.isOpen = 1 and u.isEnabled = 1 and u.player.id =?";
		return this.find(hql, playerId);
	}
	
	public int toggleEnable(Long id){
		String sql="update apply u set u.is_enabled = ifnull(u.is_enabled^3,2) where u.id=?";
		return this.excuteSqlUpdate(sql.toString(),id);
	}
}