/**
 * 
 */
package com.soccer.dao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.soccer.bean.PlayerWeight;
import com.soccer.bean.TeamWeight;
import com.soccer.model.AdminExecLog;
import com.soccer.model.Player;
import com.soccer.model.Team;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;


/**
 * @author Feng Jianli
 */
@Repository("teamDao")
public class TeamDao extends BaseDao<Team> {
	public PageResult<Team> findByPage(Search search) throws Exception {
		PageResult<Team> ret = new PageResult<Team>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "from Team u where 1=1 ";

		List<Object> params = new ArrayList<Object>();
		//所属球队
		if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
			hql += " and u.teamTitle like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
     	}
		
		//模拟查询
		if (!StringUtil.isEmpty(search.getSnippet())) {
			hql += " and u.teamTitle like ? escape '/' ";
			params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
		}
		
		
		// 球队类型
		if(search.getTeamType()!=null && search.getTeamType() !=0 ){
			hql += " and u.teamType = ?";
			params.add(search.getTeamType());
		}	
		
		// 由谁解散的
		if(search.getDismisser()!=null){
			hql += " and u.dismisser = ?";
			params.add(search.getDismisser());
		}	
		
		//是否解散
		if (search.getIsDismissed() != null ) {
			hql += " and u.dismissed = ?";
			params.add(search.getIsDismissed());
		}
		
		//是否官方
		if (search.getOfficial() != null ) {
			hql += " and u.official = ?";
			params.add(search.getOfficial());
		}
				
		
		//建队开始时间
		if (search.getFromDate() != null ) {
			hql += " and u.createTime >= ?";
			params.add(search.getFromDate());
		}
		
		//建队结束时间
		if (search.getToDate() != null) {
			hql += " and u.createTime <= ?";
			Calendar toDate = Calendar.getInstance();
		 	toDate.setTime(search.getToDate() );
		 	toDate.add(Calendar.DATE, 1);
			params.add(toDate.getTime());
		}
		
		//用于接受hql片断
		if(search.getCondition()!=null){
			hql +=search.getCondition();
		}
		
		if(search.getOrderby()!=null){
			hql+=" order by "+search.getOrderby();
		}else{
			hql+=" order by u.createTime desc";
		}
		
		System.out.println("findTeamByPage HQL = " + hql);
		
		this.findPageList(hql, params,ret);
		
	    return ret;
	}      
	
public Object getStatics(Long id){
		final String sql =
		"select (select count(p.id) from football_player p where p.teamid = t.id) as playerNum, "+
		"p.id as captainId,p.name as captainName,"+
		"(select count(g.id) from games g where g.team_a = t.id) as challengeNum , "+
		"(select count(g.id) from games g where g.team_a = t.id and g.team_b is not null) as acceptNum "+
		 "from team t left join football_player p on p.teamid = t.id and p.is_captain = 1 where   t.id =? ";
	   return  this.uniqueResultSql(sql, id);
	}
	
	public List getAllTeam(){
		 String hql = "select new com.soccer.model.Team(u.id as id,u.teamTitle  as name) from Team u where  (u.dismissed is null or u.dismissed =2)";
		 Query query = this.getSession().createQuery(hql).setFirstResult(0);
		 return query.list();
	}
	public List searchTeam(String name,String snippet,Integer teamType,Long exId,Long cupId, Integer pageSize){
		 List<Object> params = new ArrayList<Object>();
		 String hql = "select new com.soccer.model.Team(u.id as id,u.teamTitle  as name,u.playerNum as num) from Team u where  (u.dismissed is null or u.dismissed =2)";
		 if(!StringUtil.isEmpty(name)){
			 hql+=" and u.teamTitle like ? ";
			 params.add("%" +name.trim()+"%");
		 }
		 if(snippet!=null){
			 hql +=snippet;
		 }
		 if(teamType!=null && teamType !=0 ){
				hql += " and u.teamType = ? and u.playerNum >= ?";
				params.add(teamType);
			 	params.add(teamType);
		}	
		 if(exId!=null ){
				hql += " and u.id != ?";
				params.add(exId);
		}	
	/*	 if(cupId!=null ){
				hql += " and exists (select 1 from Cup c where c.id= ? and (c.isPublic = 1 or exists (select 1 From CupTeam t where t.teamId= u.id and t.cupId=c.id)))";
				params.add(cupId); 
		 }	*/
		 Query query = this.getSession().createQuery(hql);
		for (int i=0;i<params.size();i++) {
			query.setParameter(i, params.get(i));
		}
		query.setFirstResult(0).setMaxResults(pageSize);
		return query.list();
	}
	
	public Team findTeamByName(String teamTitle,Long id) {
		Team team = null;
		String hql = " from Team where teamTitle = ? and (dismissed is null or dismissed!=1)";
		if(id!=null){
			hql += " and id!="+id;
		}
		Query query = this.getSession().createQuery(hql).setParameter(0, teamTitle);
		List<Team> teamList = query.list();
		if (!teamList.isEmpty()) {
			team = teamList.get(0);
		}
		return team;
	}
	
	/**
	 * 解散一个球队
	 * @param teamId
	 * @return
	 */
	public void dismissTeam(Long teamId){
		//将未审批的入队申请修改为审核 不通过
		String recruitHql ="update apply_tank t inner join apply a on t.apply_id = a.id "+
				"set t.audit_time = now(),t.audit_status = 2 "+
				"WHERE a.is_open =1 and t.audit_status is null and t.team_id =?";
		   this.excuteSqlUpdate(recruitHql, teamId);
		//删除未到活动时间的签到信
		String signInSqlSon = "delete t from message_tank t where t.message_id in (select id from message m where m.begin_time > now() and m.message_type = 2 and m.team_id = ?)";
		String signInSql = "delete m from message m where m.begin_time > now() and m.message_type = 2 and m.team_id = ?";
		this.excuteSqlUpdate(signInSqlSon, teamId);
		this.excuteSqlUpdate(signInSql, teamId);
		//保存转队记录
		String transferlog = " insert  into football_transfer_log(player_id,from_team,from_time) select  id,teamid,now() from football_player where teamid = ? ";
		this.excuteSqlUpdate(transferlog, teamId);
		//解散球队的队员
		String disPlayersSql = "update Player u set u.isCaptain = 2 ,u.team.id = null where u.team.id = ?";
		this.excuteUpdate(disPlayersSql, teamId);
	}
	
	public void callProc(Long id,Properties data){
		Session session =this.getSession(); 
		String procName="{Call pr_calcu_team(?,?,?,?,?) }"; 
		SQLQuery query = session.createSQLQuery(procName);
		query.setFloat(0, Float.parseFloat(data.getProperty("team_total_game")));
		query.setFloat(1, Float.parseFloat(data.getProperty("team_active")));
		query.setFloat(2, Float.parseFloat(data.getProperty("team_win_rate")));
		query.setFloat(3, Float.parseFloat(data.getProperty("team_like")));
		query.setLong(4, id);
		List list =query.list();
	}
	
		/**
		 * 获取当天注册的球队数
		 * @param playerId
		 * @return
		 */
	public Integer getRegNo(Long playerId) {
		String sql = "select count(*) from team t where register = ? and date(regist_time) = date(now())";
		BigInteger cnt = (BigInteger) this.uniqueResultSql(sql, playerId);
		return cnt.intValue();
	}
}
