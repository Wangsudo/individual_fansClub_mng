/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.GameLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Feng Jianli
 */
@SuppressWarnings("unchecked")
@Repository("gameLogDao")
public class GameLogDao extends BaseDao<GameLog> {
	public PageResult<GameLog> findByPage(Search search) throws Exception {
		PageResult<GameLog> ret = new PageResult<GameLog>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from GameLog u where 1=1 ";
		//所属球员
		if (search.getPlayerId()!=null ) {
			hql += " and  u.player.id =? ";
			params.add(search.getPlayerId());
	 	}
		
		if(search.getOrderby()!=null){
			hql+=" order by u."+search.getOrderby();
		}else{
			hql+=" order by u.game.beginTime desc";
		}
		
		this.findPageList(hql, params,ret);
		
	    return ret;
	}    
	
	
	public List<GameLog> findByGameid(Long gameid){
		String hql = "from GameLog u where u.game.id = ?";
		return this.find(hql, gameid);
	}
	
}
