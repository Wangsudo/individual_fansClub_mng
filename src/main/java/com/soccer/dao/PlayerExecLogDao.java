/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.PlayerExecLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("playerExecLogDao")
public class PlayerExecLogDao extends BaseDao<PlayerExecLog> {
	public PageResult<PlayerExecLog> findByPage(Search search) throws Exception {
		PageResult<PlayerExecLog> ret = new PageResult<PlayerExecLog>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "from PlayerExecLog u where u.player is not null ";
		List<Object> params = new ArrayList<Object>();
		//帐号
		if (!StringUtil.isEmpty(search.getAccount()) ) {
			hql += " and u.player.account like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getAccount().trim()) + "%");
		}
		//姓名
		if (!StringUtil.isEmpty(search.getName()) ) {
			hql += " and u.player.name like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
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
			hql+=" order by "+search.getOrderby();
		}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
}
