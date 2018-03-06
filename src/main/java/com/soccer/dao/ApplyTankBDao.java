/**
 * 
 */
package com.soccer.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.ApplyTankB;
import com.soccer.model.MessageTankB;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("applyTankBDao")
public class ApplyTankBDao extends BaseDao<ApplyTankB> {
	public PageResult<ApplyTankB> findByPage(Search search) throws Exception {
		PageResult<ApplyTankB> ret = new PageResult<ApplyTankB>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from ApplyTankB u where (u.team.dismissed is null or u.team.dismissed =2) ";
			
			//应聘至球队
			if(search.getTeamId()!=null){
				hql += " and u.team.id =?";
				params.add(search.getTeamId());
			}
			//球员找队
			if(search.getPlayerId()!=null){
				hql += " and u.apply.player.id =?";
				params.add(search.getPlayerId());
			}
			//是否启用， 1启用， 2不启用。
			if(search.getIsEnabled()!=null){
				hql += " and u.apply.isEnabled =?";
				params.add(search.getIsEnabled());
			}
			//是否开放， 1开放， 2关闭。
			if(search.getIsOpen()!=null){
				hql += " and u.apply.isOpen =?";
				params.add(search.getIsOpen());
			}
			
			// 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.apply.applyTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}
	
	public void confirm(Long tankId,Integer confirmStatus){
		
		String hql = "update ApplyTankB u set u.confirmStatus = ?,u.confirmTime = now() where u.id = ?";
		this.excuteUpdate(hql,confirmStatus,tankId);
		
	}
	

}
