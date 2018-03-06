/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.soccer.model.Logo;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("logoDao")
public class LogoDao extends BaseDao<Logo> {
	public PageResult<Logo> findByPage(Search search) throws Exception {
		PageResult<Logo> ret = new PageResult<Logo>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Logo u where 1=1 ";
			//名称
			if (!StringUtil.isEmpty(search.getName()) ) {
				hql += " and u.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
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
	
	public List searchLogo(String name,Integer pageSize){
	    String hql = "select u.name from Logo u";
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

	public List getAll() {
		return this.findByHql("from Logo", null);
	}
	
}
