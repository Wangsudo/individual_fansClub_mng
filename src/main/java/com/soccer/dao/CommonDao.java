/**
 * 
 */
package com.soccer.dao;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.stereotype.Repository;

import com.soccer.model.Player;
import com.soccer.model.Team;


/**
 * @author 002195
 * 
 */
@Repository("commonDao")
@SuppressWarnings("unchecked")
public class CommonDao extends BaseDao {

	/**
	 * [[1,"球队招人"],[2,"入队申请"],[3,"约战"],[4,"签到"],[5,"站内信"],[6,"我的申请"],[7,"邀我入队"],[8,"球员变动"]],	
	 * @param noticeType
	 * @param playerId
	 * @param lastReadTime
	 * @return
	 */
	public Integer getUnreadCount(Integer noticeType, Player player,Long lastReadTime) {
		String sql = null;
		Long playerId= player.getId();
		Team team = player.getTeam();
		BigInteger cnt = BigInteger.ZERO;
		Date date = new Date(lastReadTime);
		switch(noticeType){
			case 1:
			sql = "select ifnull(count(*),0) from recruit where is_enabled = 1 and team_id = ? and (op_time > ? or confirm_time >?)";
			cnt = (BigInteger) this.uniqueResultSql(sql, team.getId(),date,date);break;
			case 2:
			sql = "SELECT ifnull(count(t.id),0) FROM apply_tank t left join apply a on t.apply_id = a.id where t.team_id =? and a.is_enabled = 1 and (a.apply_time > ? or t.audit_status >  ? or t.confirm_time > ?);";
			cnt = (BigInteger) this.uniqueResultSql(sql, team.getId(),date,date,date);break;
			case 3:if(team!=null){
				sql ="select ifnull(count(*),0) from games where is_enabled = 1 and (team_a = ? or team_b = ?) and (create_time > ? or audit_time >?)";
				cnt = (BigInteger) this.uniqueResultSql(sql, team.getId(),team.getId(),date,date);
			}break;
			case 4:
				sql = "SELECT count(*) FROM message_tank t left join message m on t.message_id = m.id where m.is_enabled = 1 and m.message_type = 2  and t.player_id = ? and (m.op_time >? or t.audit_time >? or t.confirm_time >? )";
				cnt = (BigInteger) this.uniqueResultSql(sql, playerId,date,date,date);break;
			case 5:
				sql = "SELECT count(*) FROM message_tank t left join message m on t.message_id = m.id where m.is_enabled = 1 and m.message_type = 1  and t.player_id = ? and m.op_time >? ";
				cnt = (BigInteger) this.uniqueResultSql(sql, playerId,date);break;
			case 6:
				sql = "SELECT ifnull(count(t.id),0) FROM apply_tank t left join apply a on t.apply_id = a.id where a.player_id =? and a.is_enabled = 1 and (a.apply_time > ? or t.audit_status >  ? or t.confirm_time > ?);";
				cnt = (BigInteger) this.uniqueResultSql(sql, playerId,date,date,date);break;
			case 7:
				sql = "select ifnull(count(*),0) from recruit where is_enabled = 1 and player_id = ? and (op_time > ? or confirm_time >?)";
				cnt = (BigInteger) this.uniqueResultSql(sql, playerId,date,date);break;
			case 8:
				if(team!=null){
					sql = "SELECT ifnull(count(*),0) FROM football_transfer_log where (from_team = ? or  to_team = ?) and (from_time >? or to_time >?)";
					cnt = (BigInteger) this.uniqueResultSql(sql, team.getId(),team.getId(),date,date);
				}break;
		}
		return cnt.intValue();
	}
	
}
