/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.soccer.model.Cup;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;

/**
 * @author Feng Jianli
 */
@Repository("cupDao")
public class CupDao extends BaseDao<Cup> {
	public PageResult<Cup> findByPage(Search search) throws Exception {
		PageResult<Cup> ret = new PageResult<Cup>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "select u from Cup u where 1=1 ";
		//杯赛名称
		if (!StringUtil.isEmpty(search.getName()) ) {
			hql += " and u.name like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
     	}
		if(search.getTeamId()!=null){
			hql += " and exists (select 1 From CupTeam t where t.teamId=? and t.cupId=u.id)";
			params.add(search.getTeamId());
		}
		//是否公开
		 if (search.getIsPublic()!= null) {
	        	hql += " and u.isPublic =?";
	        	params.add(search.getIsPublic());
	     }
				
			//编缉开始时间
			if (search.getFromDate() != null ) {
				hql += " and u.opTime >= ?";
				params.add(search.getFromDate());
			}
			
			//编缉结束时间
			if (search.getToDate() != null) {
				hql += " and u.opTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}		
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
	public List<Cup> getCups(Long teamA,Long teamB){
		List<Object> params = new ArrayList<Object>();
		String hql = "select new com.soccer.model.Cup(u.id,u.name) from Cup u where u.isPublic = 1 or "
				+ "( exists (select 1 From CupTeam t where t.teamId=? and t.cupId=u.id)";
		params.add(teamA);
		if(teamB!=null){
			hql += " and exists (select 1 From CupTeam t where t.teamId=? and t.cupId=u.id)";
			params.add(teamB);
		}
		hql +=")";
		return this.findByHql(hql,params);
	}
	
	public List searchCup(String name, Integer isPublic,Integer pageSize){
		String hql = "select new com.soccer.model.Cup(u.id,u.name) from Cup u where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if(!StringUtil.isEmpty(name)){
			 hql+=" and u.name like ? ";
			 params.add("%" +name.trim()+"%");
		 }
		if(isPublic != null){
			hql += " and u.isPublic = ?";
			params.add(isPublic);
		}
		
		final Query query = this.getSession().createQuery(hql);
		for (int i=0;i<params.size();i++) {
			query.setParameter(i, params.get(i));
		}
		query.setMaxResults(pageSize);
		return query.list();
	}

	public List getPhases(Long cupId) {
		// TODO Auto-generated method stub
		String hql = "from CupPhase u where u.cupId =?";
		return this.find(hql,cupId);
	}

	@SuppressWarnings("unchecked")
	public List<Cup> getPublicCups() {
		String hql = "select new com.soccer.model.Cup(u.id,u.name) from Cup u where u.isPublic = 1";
		return this.findByHql(hql,null);
	}
	
}
