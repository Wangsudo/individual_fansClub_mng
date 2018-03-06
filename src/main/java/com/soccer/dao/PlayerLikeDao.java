/**
 * 
 */
package com.soccer.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.soccer.model.PlayerLike;

/**
 * @author 002195
 * 
 */
@Repository("playerLikeDao")
public class PlayerLikeDao extends BaseDao<PlayerLike> {
	public void calcuLikeNum(Long playerId){
		Session session =this.getSession(); 
		String updateSql ="update football_player t set like_num = (select count(l.id) from football_player_like l where l.player_id = t.id and l.status = 1) where t.id = ? ";
		SQLQuery query = session.createSQLQuery(updateSql);
		query.setLong(0, playerId);
		query.executeUpdate();
	}
}
