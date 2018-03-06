/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Recruit;
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
@Repository("recruitDao")
public class RecruitDao extends BaseDao<Recruit> {
	public PageResult<Recruit> findByPage(Search search) throws Exception {
		PageResult<Recruit> ret = new PageResult<Recruit>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Recruit u where (u.team.dismissed is null or u.team.dismissed =2) ";
			//标题
			if (!StringUtil.isEmpty(search.getTitle())) {
				hql += " and u.title like ? escape '/'";
				params.add("%" + StringUtil.escapeSQLLike(search.getTitle().trim()) + "%");
			}
			
			//球员姓名
			if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
				hql += " and u.team.teamTitle like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
	     	}

			// 球队类型
			if(search.getTeamType()!=null && search.getTeamType() !=0 ){
				hql += " and u.team.teamType = ?";
				params.add(search.getTeamType());
			}	
			
			if(search.getTeamId()!=null){
				hql += " and u.team.id = ? ";
				params.add(search.getTeamId());
			}
			
			if(search.getPlayerId()!=null){
				hql += " and u.player.id = ? ";
				params.add(search.getPlayerId());
			}
			
			 // 查询条件
	        if (search.getIsPublic()!= null) {
	        	hql += " and u.isPublic =?";
	        	params.add(search.getIsPublic());
	        }
	        
	        // 查询条件
	        if (search.getCondition() != null) {
	            hql += search.getCondition();
	        }
	        
			//是否启用， 1启用， 0不启用。
			if(search.getIsEnabled()!=null){
				hql += " and u.isEnabled =?";
				params.add(search.getIsEnabled());
			}
			
			// 发布时间
	        if (search.getFromDate() != null) {
	            hql += " and u.opTime >= ?";
	            params.add(search.getFromDate());
	        }
	        
	      //发布结束时间
			if (search.getToDate() != null) {
				hql += " and u.opTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}
	        
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.opTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}

	public int toggleEnable(Long id){
		String sql="update recruit u set u.is_enabled = ifnull(u.is_enabled^3,2) where u.id=?";
		return this.excuteSqlUpdate(sql.toString(), id);
	}
	
	public List<Recruit> findOpenRecruit(Long teamId,Long playerId) {
		String hql = "from Recruit u where u.isPublic = 2 and u.isEnabled = 1 and u.confirmStatus is null and u.team.id =? and u.player.id =?";
		return this.find(hql, teamId,playerId);
	}
	
	public void confirm(Long id,Integer confirmStatus){
		String hql = "update Recruit u set u.confirmStatus = ?,u.confirmTime = now() where u.id = ?";
		this.excuteUpdate(hql,confirmStatus,id);
	}
}
