/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Game;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 002195
 * 
 */
@Repository("gameDao")
@SuppressWarnings("unchecked")
public class GameDao extends BaseDao<Game> {

	/**
	 * @param user
	 * @param pageResult
	 * @return
	 */
	public PageResult<Game> findByPage(Search search)  throws Exception {
		PageResult<Game> ret = new PageResult<Game>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("select u from Game u left join u.teamB b where 1=1 ");
		//所属球队,球队以“，”分隔
		if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
			//球队以中文"，"或者英文","分隔
			String title = search.getTeamTitle();
			String teamA = null;
			String teamB = null;
			String[] teams = title.split(",|，");
			for(String team:teams){
				if(!StringUtil.isEmpty(team) && teamA == null){
					teamA = team.trim();
				}else if(!StringUtil.isEmpty(team) && teamA != null && teamB == null){
					teamB = team.trim();
				}
			}
			if(teamA!=null){
				hql.append(" and (u.teamA.teamTitle like ? escape '/'  or u.teamB.teamTitle like ? escape '/') ");
				params.add("%" + StringUtil.escapeSQLLike(teamA) + "%");
				params.add("%" + StringUtil.escapeSQLLike(teamA) + "%");
				if(teamB!=null){
					hql.append( " and (u.teamA.teamTitle like ? escape '/'  or u.teamB.teamTitle like ? escape '/') ");
					params.add("%" + StringUtil.escapeSQLLike(teamB) + "%");
					params.add("%" + StringUtil.escapeSQLLike(teamB) + "%");
				}
			}
     	}
		
		//模拟查询
		if (!StringUtil.isEmpty(search.getSnippet())) {
			hql.append(" and (u.teamA.teamTitle like ? escape '/'  or b.teamTitle like ? escape '/'   or u.title like ? escape '/') ");
			params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
		}
		
		
		//所属球队(通过id)
		if (search.getTeamId()!=null ) {
			hql.append(" and ( u.teamA.id =? or u.teamB.id=?) ");
			params.add(search.getTeamId());
			params.add(search.getTeamId());
     	}
		
		if(!StringUtil.isEmpty(search.getScores())){
			//比分 ， 以“：|:”分隔
			String scoreStrs = search.getScores();
			Integer scoreA = null;
			Integer scoreB = null;
			String[] scores = scoreStrs.split("：|:");
			for(String score:scores){
				if(!StringUtil.isEmpty(score) && scoreA == null){
					scoreA = Integer.parseInt(score.trim());
				}else if(!StringUtil.isEmpty(score) && scoreA != null && scoreB == null){
					scoreB =Integer.parseInt(score.trim());
				}
			}
			if(scoreA!=null){
				hql.append(" and (u.scoreA =?  or u.scoreB = ? )");
				params.add(scoreA);
				params.add(scoreA);
				if(scoreB!=null){
					hql.append( "  and (u.scoreA = ?  or u.scoreB = ? ) ");
					params.add(scoreB);
					params.add(scoreB);
				}
			}
		}
		
		//比分审核状态
		if(search.getStatus()!=null  ){
			switch(search.getStatus()){
			case 0:
				hql.append(" and (u.auditStatus =0 or u.auditStatus is null) ") ;break;
			default:
				hql.append(" and u.auditStatus = ?"); params.add(search.getStatus());break;
			}
		}
		//1: 约战中   2: 未开赛, 3: 已结束, 
		if(search.getType()!=null){
			Integer type = search.getType();
			if(type == 1){
				hql.append(" and u.teamBOperation is null") ;
			}else if(type == 2){
				hql.append(" and u.teamBOperation = 1 and u.beginTime > ?") ;params.add(new Date());	
			}else if(type == 3){
				hql.append(" and u.teamBOperation = 1 and u.beginTime < ?") ;params.add(new Date());	
			}
		}
		
		
		//isPublic == true 时, 一对一的挑战 .isPublic ==false 时,广场上的挑战 
		if(search.getIsPublic()!=null && search.getIsPublic() == 1 ){
			hql.append(" and u.isPublic =1");
		}else if(search.getIsPublic()!=null && search.getIsPublic()== 2){
		//将要比赛的预告
			hql.append(" and u.isPublic =2");
		}
		
		
		
		//比赛状态 
		if(search.getGameStatus()!=null  ){
			Integer status = search.getGameStatus();
			switch (status) {
				// 约战中
				case 1:	hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation is null ");break;
				//已过期
				case 2:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation is null and u.beginTime < now()");break;
				// 已关闭
				case 3:hql.append(" and ifnull(u.teamAOperation,0) = 2");break;
				//已拒绝
				case 4:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 2");break;
				//未开赛	
				case 5:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime > now()");break;
				//比赛中
				case 6:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime < now() and timestampdiff(MINUTE,u.beginTime, now()) <u.playingTime");break;
				//待上报
				case 7:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime < now() and (u.scoreA1 is null or u.scoreA2 is null)");break;
				//待审核 
				case 8:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime < now() and u.auditStatus is null and (u.scoreA1 != u.scoreA2 or u.scoreB1 != u.scoreB2)");break;
				//审核不通过
				case 9:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime < now() and u.auditStatus = 2 ");break;
				//已结束
				case 10:hql.append(" and ifnull(u.teamAOperation,0) != 2 and u.teamBOperation = 1 and u.beginTime < now() and u.auditStatus = 1 ");break;
			}
		}
	
		// 赛制
		if(search.getTeamType()!=null  &&  search.getTeamType()!=0){
			hql.append(" and u.teamType = ? ");
			params.add(search.getTeamType());
		}
		//首页是否显示
		if(search.getIsEnabled()!=null  ){
			switch (search.getIsEnabled()) {
				case 1:
					hql.append(" and (u.auditStatus !=2 or u.auditStatus is null ) and (u.isEnabled is null or u.isEnabled = 1) ") ;break;
				case 2:
					hql.append(" and (u.auditStatus =2 or u.isEnabled = 2)") ;break;
				default:
					break;
			}
		}
		//用于接受hql片断
		if(search.getCondition()!=null){
			hql.append(search.getCondition());
		}
		//比赛开始时间
		if (search.getFromDate() != null ) {
			hql.append(" and u.beginTime >= ?");
			params.add(search.getFromDate());
		}
		
		//比赛结束时间
		if (search.getToDate() != null) {
			hql.append(" and u.beginTime <= ?");
			Calendar toDate = Calendar.getInstance();
		 	toDate.setTime(search.getToDate() );
		 	toDate.add(Calendar.DATE, 1);
			params.add(toDate.getTime());
		}
		
		if(search.getOrderby()!=null){
			hql.append(" order by u."+search.getOrderby());
		}
		
		this.findPageList(hql.toString(), params,ret);
	    return ret;
	}
	
	
	/**
	 * 由player 的 teamid 获取 上传的 比分 信息 
	 * @param search
	 * @throws Exception
	 */
	public PageResult<Game> findScoreByPage(Search search) throws Exception {

		PageResult<Game> ret = new PageResult<Game>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		List<Object> params = new ArrayList<Object>();
		
		String hql = "from Game u where u.teamBOperation = 1 and u.beginTime < now() and ((u.teamA.id =? and u.scoreA1 is not null) or (u.teamB.id =? and u.scoreA2 is not null))  order by u.modifyTime desc";
		//所属球队(通过id)
		params.add(search.getTeamId());
		params.add(search.getTeamId());
		
		this.findPageList(hql, params,ret);
		return ret;
	}
	
	public int toggleEnable(Long gameId){
		String sql="update games u set u.is_enabled = ifnull(u.is_enabled^3,2) where u.id=?";
		return this.excuteSqlUpdate(sql.toString(),gameId);
	}

	public Game findGameByName(String GameTitle) {
		Game one = null;
		String hql = " from Game where GameTitle = ?";
		List<Game> list = this.find(hql, GameTitle);
		if (!list.isEmpty()) {
			one = list.get(0);
		}
		return one;
	}
	
	public List<Game> toSocreGames(Long teamId){
		String hql = " from Game u where u.teamBOperation = 1 and u.beginTime < now()  and (u.auditStatus =0 or u.auditStatus is null)"
				+ " and ((u.teamA.id =? and u.scoreA1 is null) or (u.teamB.id =? and u.scoreA2 is null)) order by u.beginTime desc";
		return this.find(hql, teamId,teamId);
	}
	
	/**
	 * 获取球队当天约战次数
	 * @param id
	 * @return
	 */
	public Integer getTodayDareNum(Long id) {
		String sql = "select count(1) from games where date(create_time) = date(now()) and  team_a = ? ";
		Object obj = this.uniqueResultSql(sql, id);
		return Integer.valueOf(obj.toString());
	}
	
	public void calcuScore(final Long gameId){
		SQLQuery sqlQuery = getSession().createSQLQuery("{call pr_calcu_wins(?)}");
    	sqlQuery.setLong(0, gameId);
    	sqlQuery.executeUpdate();
	}
	
}
