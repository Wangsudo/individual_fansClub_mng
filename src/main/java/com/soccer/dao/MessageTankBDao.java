/**
 * 
 */
package com.soccer.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.soccer.model.MessageTankB;
import com.soccer.util.PageResult;
import com.soccer.util.Search;


/**
 * @author Feng Jianli
 */
@Repository("messageTankBDao")
public class MessageTankBDao extends BaseDao<MessageTankB> {
	public PageResult<MessageTankB> findByPage(Search search) throws Exception {
		PageResult<MessageTankB> ret = new PageResult<MessageTankB>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from MessageTankB u where 1=1 ";
			
			//球员找队
			if(search.getPlayerId()!=null){
				hql += " and u.player.id =?";
				params.add(search.getPlayerId());
			}
			if(search.getType() !=null){
				hql += " and u.message.messageType =?";
				params.add(search.getType());
			}
			if(search.getOrderby()!=null){
				hql+=" order by "+search.getOrderby();
			}else{
				hql+=" order by u.message.opTime desc";
			}
		
		this.findPageList(hql, params,ret);
	    return ret;
	}      
}
