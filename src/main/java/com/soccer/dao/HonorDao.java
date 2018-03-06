/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.soccer.model.Honor;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("honorDao")
public class HonorDao extends BaseDao<Honor> {
	public PageResult<Honor> findByPage(Search search) throws Exception {
		PageResult<Honor> ret = new PageResult<Honor>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Honor u where 1=1 ";
			//名称
			if (!StringUtil.isEmpty(search.getName()) ) {
				hql += " and u.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
	     	}
			// 评论类型
			if(search.getType()!=null){
				hql += " and u.type = ? ";
				params.add(search.getType());
			}
			//管理员
			if (search.getAdminId()!=null ) {
				hql += " and u.op =?";
				params.add(search.getAdminId());
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
			}else{
				hql+=" order by opTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
	
	public List searchHonor(String name,Integer pageSize){
	    String hql = "select u.name from Honor u";
		List<Object> params = new ArrayList<Object>();
		if(!StringUtil.isEmpty(name)){
			hql += " where u.name like ?";
			params.add("%" +name.trim()+"%");
		}
		Query query = getSession().createQuery(hql);
		for (int i=0;i<params.size();i++) {
			query.setParameter(i, params.get(i));
		}
		if(pageSize!=null){
			query.setMaxResults(pageSize);
		}
		return query.list();
	}

	public List getOthers(Long teamId,Long playerId,Integer type) {
		String hql;
		Long entityId;
		if(type == 2){
			hql = "select u from Honor u where not exists (select 1 From TeamHonor t where t.honor.id=u.id and t.teamId =?) and type = ?";
			entityId = teamId;
		}else{
			hql = "select u from Honor u where not exists (select 1 From PlayerHonor t where t.honor.id=u.id and t.playerId =?) and type = ?";
			entityId = playerId;
		}
		return this.find(hql, entityId,type);
	}
	
}
