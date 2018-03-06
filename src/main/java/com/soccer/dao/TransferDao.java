/**
 * 
 */
package com.soccer.dao;

import com.soccer.model.Player;
import com.soccer.model.Team;
import com.soccer.model.TransferLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import com.soccer.util.StringUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 002195
 * 
 */
@Repository("transferDao")
@SuppressWarnings("unchecked")
public class TransferDao extends BaseDao<TransferLog> {

	@Resource(name = "playerDao")
	private PlayerDao playerDao;
	public PageResult<TransferLog> findByPage(Search search) throws Exception {
		PageResult<TransferLog> ret = new PageResult<TransferLog>();
		ret.setCurrentPage(search.getCurrentPage());
		ret.setPageSize(search.getPageSize());
		
		String hql = "select u from TransferLog u left join u.fromTeam j left join u.toTeam k where 1=1 ";
	//	String hql = "select u from TransferLog u join u.toTeam k where u.fromTeam is not null ";
		List<Object> params = new ArrayList<Object>();
				
			//所属球队
			if (!StringUtil.isEmpty(search.getTeamTitle()) ) {
				hql += " and ( j.teamTitle like ? escape '/' or  k.teamTitle like ? escape '/' ) ";
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
				params.add("%" + StringUtil.escapeSQLLike(search.getTeamTitle().trim()) + "%");
	     	}
			
			//所属球队
			if (search.getTeamId()!=null) {
				hql += " and ( j.id = ? or  k.id = ? ) ";
				params.add(search.getTeamId());
				params.add(search.getTeamId());
	     	}
			
			//模拟查询
			if (!StringUtil.isEmpty(search.getSnippet())) {
				hql += " and ( j.teamTitle like ? escape '/' or  k.teamTitle like ? escape '/'  or u.player.name like ? escape '/'  ) ";
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
				params.add("%" + StringUtil.escapeSQLLike(search.getSnippet().trim()) + "%");
			}
			
			// 球队类型
			if(search.getTeamType()!=null &&  search.getTeamType()!=0 ){
			//	hql += " and ( j.teamType = ? or k.teamType = ?)";
				hql += " and ( k.teamType = ?)";
		//		params.add(search.getTeamType());
				params.add(search.getTeamType());
			}	
			
			//姓名
			if (!StringUtil.isEmpty(search.getName()) ) {
				hql += " and u.player.name like ? escape '/' ";
				params.add("%" + StringUtil.escapeSQLLike(search.getName().trim()) + "%");
	     	}
			
			//注册开始时间
			if (search.getFromDate() != null ) {
				hql += " and u.toTime >= ?";
				params.add(search.getFromDate());
			}
			
			//注册结束时间
			if (search.getToDate() != null) {
				hql += " and u.toTime <= ?";
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
	
	public List<TransferLog> findHistory(Long playerId){
		String hql = "from TransferLog u where u.player.id = ? order by id desc";
		List<TransferLog> list = this.find(hql, playerId);
		return list;
	}
	
	/**
	 * 添加或者修改球员信息时, 保存其中的转会记录
	 * 
	 5种情况.1.新建球队 (原球队为空,现球队不为空,且无转出球队记录)
		 	2.新注册球员加入球队,原球队为空(新加入球队)
		 	3.转出
		 	4.转出转入同时
		 	5.转入
		只记录 3, 4 ,5 三种情况 	
	 * @param ori
	 * @param now
	 */
	public void saveTransferlog(Player player,Team ori, Team now){
		/*Session session = this.getSession();
		Transaction tx = session.beginTransaction();  */
		this.playerDao.saveOrUpdate(player);
		/*session.flush();    
		tx.commit(); */
		this.refreshPlayerNum(ori);
		this.refreshPlayerNum(now);
	
		TransferLog bean ;
		Timestamp curTime = new Timestamp(new Date().getTime());
		TransferLog lastTransOut = getLastTransOutLog(player.getId());
		// 转出球队
		if(ori!=null && now==null){
			bean = new TransferLog();
			bean.setCreateTime(curTime);
			bean.setFromTeam(ori);
			bean.setFromTime(curTime);
		}
		//转出同时转入
		else if(ori!=null && now!=null && !ori.getId().equals(now.getId())){
			bean = new TransferLog();
			bean.setCreateTime(curTime);
			bean.setFromTeam(ori);
			bean.setFromTime(curTime);
			bean.setToTeam(now);
			bean.setToTime(curTime);
		}
		//以前转出过, 现转入
		else if(lastTransOut != null && ori==null && now!=null){
			bean = lastTransOut;
			bean.setModifyTime(curTime);
			bean.setToTeam(now);
			bean.setToTime(curTime);
		}
		//新注册球员加入球队
		else if(lastTransOut == null && ori==null  && now!=null){
			bean = new TransferLog();
			bean.setCreateTime(curTime);
			bean.setToTeam(now);
			bean.setToTime(curTime);
		}else{
			return;
		}
		bean.setPlayer(player);
		this.saveOrUpdate(bean);
	}
	
	/**
	 * 获取球员上次转出的球队
	 * @param playerId
	 * @return
	 */
	public TransferLog getLastTransOutLog(Long playerId){
		String hql = "from TransferLog u where u.toTeam is null and u.player.id = ? order by u.id desc";
		List<Object> params = new ArrayList<Object>(); 
		params.add(playerId);
		List<TransferLog> list = this.findByHql(hql, params);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public void refreshPlayerNum(Team team){
		if(team!=null && team.getId()!=null){
			String sql = "update Team t set t.playerNum = (select count(*) from Player p where p.team.id = t.id) where t.id = ?";
			this.excuteUpdate(sql, team.getId());
		}
	}
	
}
