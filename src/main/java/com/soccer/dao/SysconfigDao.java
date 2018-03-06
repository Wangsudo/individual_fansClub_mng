/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.soccer.model.Menu;
import com.soccer.model.Sysconfig;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;

/**
 * @author Feng Jianli
 */
@Repository("sysconfigDao")
public class SysconfigDao extends BaseDao<Sysconfig> {
	public PageResult<Sysconfig> findByPage(Search search) throws Exception {
		PageResult<Sysconfig> ret = new PageResult<Sysconfig>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Sysconfig u where 1=1 ";
		
		if (!StringUtil.isEmpty(search.getTitle()) ) {
			hql += " and u.comment like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getTitle()) + "%");
     	}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}

	public Sysconfig findByCode(String code) {
		Sysconfig one = null;
		String hql = " from Sysconfig where code = ?";
		Query query = this.getSession().createQuery(hql).setParameter(0, code);
		List<Sysconfig> list = query.list();
		if (!list.isEmpty()) {
			one = list.get(0);
		}
		return one;
	}

	public List<Sysconfig> findAll(List<String> codes) {
		String hql = "from Sysconfig where code in (:codes)";
		Query query = getSession().createQuery(hql).setParameterList("codes", codes);
		return query.list();
	}
}
