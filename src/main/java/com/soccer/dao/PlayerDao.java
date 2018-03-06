/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Player;
import com.soccer.model.PlayerLike;
import com.soccer.model.TeamLike;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * @author Feng Jianli
 */
@Repository("playerDao")
public class PlayerDao extends BaseDao<Player> {
	public PageResult<Player> findByPage(Search search) throws Exception {
		PageResult<Player> ret = new PageResult<Player>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		String hql = "from Player u where 1=1 ";

		List<Object> params = new ArrayList<Object>();
			 //是否队长
				if(search.getIsCaptain()!=null && search.getIsCaptain() == 1 ){
					hql += " and u.isCaptain = 1";
				}else if(search.getIsCaptain()!=null && search.getIsCaptain()== 2){
					hql += " and (u.isCaptain is null or u.isCaptain =2)";
				}
				
			//注册审核状态
				if(search.getStatus()!=null  ){
					switch(search.getStatus()){
					case 0:hql +=" and (u.auditStatus =0 or u.auditStatus is null) " ;break;
					default:hql +=" and u.auditStatus = ? "; params.add(search.getStatus());break;
					}
				}
				
			//所属球队
			if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
				hql += " and u.team.teamTitle like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
	     	}
			
			//所属球队
			if (search.getTeamId()!=null ) {
				hql += " and u.team.id =? ";
				params.add(search.getTeamId());
	     	}
			//用于接受hql片断
			if(search.getCondition()!=null){
				hql +=search.getCondition();
			}
			
			// 球队类型
			if(search.getTeamType()!=null && search.getTeamType() !=0 ){
				hql += " and u.team.teamType = ?";
				params.add(search.getTeamType());
			}	
			
			//姓名
			if (!StringUtil.isEmpty(search.getName()) ) {
				hql += " and u.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
	     	}

			//姓名
			if (!StringUtil.isEmpty(search.getAccount())) {
				hql += " and u.mobile like ? escape '/' ";
				params.add("%" + search.getAccount() + "%");
			}

			//模拟查询
			if (!StringUtil.isEmpty(search.getSnippet())) {
				hql += " and u.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			}
			
			//注册开始时间
			if (search.getFromDate() != null ) {
				hql += " and u.createTime >= ?";
				params.add(search.getFromDate());
			}
			
			//注册结束时间
			if (search.getToDate() != null) {
				hql += " and u.createTime <= ?";
				Calendar toDate = Calendar.getInstance();
			 	toDate.setTime(search.getToDate() );
			 	toDate.add(Calendar.DATE, 1);
				params.add(toDate.getTime());
			}
			
			if(search.getOrderby()!=null){
				hql+=" order by u."+search.getOrderby();
			}else{
				hql+=" order by u.createTime desc";
			}
		 System.out.println("findPlayerByPage HQL = " + hql);
		
		this.findPageList(hql, params,ret);
		
	    return ret;
	}      
	
	/**
	 * 通过登录账号查找用户
	 * 
	 * @param account
	 * @return
	 */
	public Player findPlayerByAccount(String account,Long id) {
		Player user = null;
		String hql = " from Player where account = ?";
		if(id!=null){
			hql += " and id!="+id;
		}
		Query query = this.getSession().createQuery(hql).setParameter(0, account);
		List<Player> userList = query.list();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		return user;
	}
	/**
	 * 通过手机号查找用户
	 * @param mobile
	 * @return
	 */
	public Player findPlayerByMobile(String mobile,Long id) {
		Player user = null;
		String hql = " from Player where mobile = ?";
		if(id!=null){
			hql += " and id!="+id;
		}
		Query query = this.getSession().createQuery(hql).setParameter(0, mobile);
		List<Player> userList = query.list();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		return user;
	}
	
	/**
	 * 通过email查找用户
	 * @return
	 */
	public Player findPlayerByEmail(String email,Long id) {
		Player user = null;
		String hql = " from Player where email = ?";
		if(id!=null){
			hql += " and id!="+id;
		}
		Query query = this.getSession().createQuery(hql).setParameter(0, email);
		List<Player> userList = query.list();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		return user;
	}
	
	/**
	 * 通过身份证号查找用户
	 * @return
	 */
	public Player findPlayerByIdno(String idno,Long id) {
		Player user = null;
		String hql = " from Player where idno = ?";
		if(id!=null){
			hql += " and id!="+id;
		}
		Query query = this.getSession().createQuery(hql).setParameter(0, idno);
		List<Player> userList = query.list();
		if (!userList.isEmpty()) {
			user = userList.get(0);
		}
		return user;
	}
	
	/**
	 * 由球队找对员
	 * @param id
	 * @param isCaptain
	 * @return
	 */
	public List<Player> findPlayersByTeam(Long id,Boolean isCaptain) {
		String hql = " from Player where team.id = ?";
		if(isCaptain != null && isCaptain){
			hql += " and isCaptain = 1";
		}else if(isCaptain != null && !isCaptain){
			hql += " and isCaptain = 2";
		}
		hql +=" order by isCaptain";
		List<Player> userList = this.find(hql, id);
		return userList;
	}
	
	/**
	 * 获取球员当天点赞记录(给球队点赞)
	 * @param id
	 * @return
	 */
	public List<TeamLike> findTeamLikes(Long id) {
		String hql = " from TeamLike where  date(createTime) = date(now()) and  giver.id = ? ";
		List<TeamLike> list = this.find(hql, id);
		return list;
	}
	
	/**
	 * 获取球员当天点赞记录(给球员点赞)
	 * @param id
	 * @return
	 */
	public List<PlayerLike> findPlayerLikes(Long id) {
		String hql = " from PlayerLike where  date(createTime) = date(now()) and  giver.id = ? ";
		List<PlayerLike> list = this.find(hql,id);
		return list;
	}
	
	
	
   public Object getStatics(Long id){
		
		final String sql ="select "+
		"(select count(l.id) from football_player_like l where l.player_id = t.id and l.status = 1) as likeNum ,"+
		"(select count(l.id) from football_player_like l where l.player_id = t.id and l.status = 1 and YEARWEEK(l.create_time) = YEARWEEK(now())-1 ) as lastWeek ,"+	
		"(select count(l.id) from football_player_like l where l.player_id = t.id and l.status = 1 and YEARWEEK(l.create_time) = YEARWEEK(now())-2 ) as twoWeekBef "+
		 "from football_player t where t.id =? ";
			return this.uniqueResultSql(sql, id);
		}
   
   /**
    * 获取球员活跃指数
    * 活跃指数：球员实际参与的总签到次数除以他所在球队（从他网站注册以来所有所在球队）总的发起的签到次数
    * @param id
    * @return
    */
   public Object getActive(Long id){
	   final String sql ="select sum(case when confirm_status = 1 then 1 else 0 end) as attendTimes,count(id) totalEvents from message_tank where player_id = ?";
	   return this.uniqueResultSql(sql, id);
   }

	public List searchPlayer(String name, Boolean isCaptain,Integer pageSize){
		String hql = "select new com.soccer.model.Player(u.id,u.name,k.teamTitle) from Player u left join u.team k where 1=1 ";
		List<Object> params = new ArrayList<Object>();
		if(!StringUtil.isEmpty(name)){
			 hql+=" and u.name like ? ";
			 params.add("%" +name.trim()+"%");
		 }
		
		if(isCaptain != null && isCaptain){
			hql += " and u.isCaptain = 1";
		}else if(isCaptain != null && !isCaptain){
			hql += " and (u.isCaptain is null or u.isCaptain!=1)";
		}
		
		final Query query = this.getSession().createQuery(hql);
		for (int i=0;i<params.size();i++) {
			query.setParameter(i, params.get(i));
		}
		query.setMaxResults(pageSize);
		return query.list();
	}
	/**
	 * 设置某一球员为队长
	 */
	public void setCaptain(final Long newCaptainId, final Long teamId){
		
		//原队长成为队员
		String disPlayersSql = "update Player u set u.isCaptain = 2,u.modifyTime = now() where u.team.id = ? and u.isCaptain = 1";
		this.excuteUpdate(disPlayersSql,teamId);
		//升级某一队员为队长
		String hql="update Player u set u.isCaptain = 1 ,u.team.id = ?, u.modifyTime = now() where u.id=?";
		 this.excuteUpdate(hql,teamId,newCaptainId);
	}
	
	public int changePassword(Long playerId,String password){
		String hql = "update Player u set u.password = ?,u.modifyTime = now() where u.id = ?";
		return this.excuteUpdate(hql,password,playerId);
	}
	
	public void callProc(Properties data){
		Session session =this.getSession(); 
		String procName="{Call pr_calcu_player(?,?,?,?,?,?,?,?,?) }";
		SQLQuery query = session.createSQLQuery(procName);
		query.setFloat(0, Float.parseFloat(data.getProperty("player_total_game")));
		query.setFloat(1, Float.parseFloat(data.getProperty("player_active")));
		query.setFloat(2, Float.parseFloat(data.getProperty("player_win_rate")));
		query.setFloat(3, Float.parseFloat(data.getProperty("player_like")));
		query.setFloat(4, Float.parseFloat(data.getProperty("beginer")));
		query.setFloat(5, Float.parseFloat(data.getProperty("total")));
		query.setFloat(6, Float.parseFloat(data.getProperty("win")));
		query.setFloat(7, Float.parseFloat(data.getProperty("even")));
		query.setFloat(8, Float.parseFloat(data.getProperty("lost")));
		List list =query.list();
	}
	
	
	//获取未审核的球员数目
	public Integer getUnAuditNo() {
		 final String sql ="select count(id) from football_player where audit_status is null or audit_status = 0";
		 BigInteger cnt = (BigInteger) this.uniqueResultSql(sql);
		 return cnt.intValue();
	}
	
}
