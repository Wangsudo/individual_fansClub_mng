/**
 * 
 */
package com.soccer.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.soccer.model.TeamLike;

/**
 * @author 002195
 * 
 */
@Repository("teamLikeDao")
public class TeamLikeDao extends BaseDao<TeamLike> {
	public void calcuLikeNum(Long teamId){
		Session session =this.getSession(); 
		String updateSql ="update team t set like_num = (select count(l.id) from team_like l where l.team_id = t.id and l.status = 1) where t.id = ? ";
		SQLQuery query = session.createSQLQuery(updateSql);
		query.setLong(0, teamId);
		query.executeUpdate();
	}
}
