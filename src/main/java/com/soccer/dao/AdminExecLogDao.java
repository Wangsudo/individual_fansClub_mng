/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.AdminExecLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("adminExecLogDao")
public class AdminExecLogDao extends BaseDao<AdminExecLog> {
	public PageResult<AdminExecLog> findByPage(Search search) throws Exception {
		PageResult<AdminExecLog> ret = new PageResult<AdminExecLog>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "from AdminExecLog u where u.admin is not null ";
		List<Object> params = new ArrayList<Object>();
		//帐号
		if (!StringUtil.isEmpty(search.getAccount()) ) {
			hql += " and u.admin.account like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getAccount().trim()) + "%");
		}
		// 开始时间
		if (search.getFromDate() != null) {
			hql += " and u.optime >= ?";
			params.add(search.getFromDate());
		}

		// 结束时间
		if (search.getToDate() != null) {
			hql += " and u.optime <= ?";
			Calendar toDate = Calendar.getInstance();
			toDate.setTime(search.getToDate());
			toDate.add(Calendar.DATE, 1);
			params.add(toDate.getTime());
		}
		
		if(search.getOrderby()!=null){
			hql+=" order by u."+search.getOrderby();
		}else{
			hql+=" order by u.optime desc";
		}
		
		this.findPageList(hql, params,ret);
		
	    return ret;
	}      
}
