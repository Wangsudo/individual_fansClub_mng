
package com.soccer.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.bean.TeamWeight;
import com.soccer.dao.PlayerDao;
import com.soccer.dao.TeamDao;
import com.soccer.dao.TransferDao;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.Player;
import com.soccer.model.Team;
import com.soccer.model.TransferLog;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.PropertyUtil;
import com.soccer.util.Search;

@Service("teamService")
public class TeamService  extends BaseService<Team>{

	@Resource(name = "teamDao")
	private TeamDao teamDao;
	
	@Resource(name = "playerDao")
	private PlayerDao playerDao;

	@Resource(name = "transferDao")
	private TransferDao transferDao;
	/**
	 * 
	 * @param Team
	 * @param pageResult
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public PageResult<Team> findByPage(Search search) throws Exception  {
		PageResult<Team> pageResult =  teamDao.findByPage(search);
		
	/*	for(Team one:pageResult.getList()){
		    Object[] statics =   (Object[])teamDao.getStatics(one.getId());
	    	Map<String, Object> map = new HashMap();
		   map.put("playerNum", statics[0]);
		   map.put("captainId", statics[1]);
		   map.put("captainName", statics[2]);
		   map.put("challengeNum",statics[3]);
		   map.put("acceptNum",statics[4]);
		   one.setMap(map);
		}*/
		return pageResult;
	}
	@Transactional(readOnly = true)
	public List searchTeam(String name,String snippet,Integer teamType,Long exId,Long cupId, Integer pageSize){
		return teamDao.searchTeam(name, snippet, teamType, exId,cupId,pageSize);
	}
	
	@Transactional(readOnly = true)
	public List getAllTeam(){
		return teamDao.getAllTeam();
	}
	
	/**
	 * 
	 * @param deId
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public Team findTeamById(Long id) {
	  Team one =  teamDao.findById(Team.class, id);
/*	  Object[] statics =   (Object[])teamDao.getStatics(one.getId());
    	Map<String, Object> map = new HashMap();
	   map.put("captainId", statics[0]);
	   map.put("captainName", statics[0]);
	   map.put("playerNum", statics[0]);
	   map.put("challengeNum",statics[3]);
	   map.put("acceptNum",statics[4]);
	   one.setMap(map);*/
	   return one;
	}
	@Transactional(readOnly = true)
	public Team findTeamByName(String name,Long id){
		return teamDao.findTeamByName(name,id);
	}

	/**
	 *
	 * @param teamId
	 * @param dismisser
	 * @throws Exception
	 */
	@Transactional
	public void dismissTeam(Long teamId,Long dismisser) throws Exception{
		teamDao.dismissTeam(teamId);
		Timestamp dismissTime = new Timestamp(new Date().getTime());
		Team team = this.findById(Team.class, teamId);
		team.setDismissed(1);
		team.setDismisser(dismisser);
		team.setDismissTime(dismissTime);
		//设置球员数为0
		team.setPlayerNum(0);
		//解散球队后，将原来的图片设置为灰阶的
		String grayIconUrl = PicUtil.transfer3Gray(InitServlet.APPLICATION_URL, team.getIconUrl());
		team.setIconUrl(grayIconUrl);
		this.saveOrUpdate(team);
	}
	
	@Transactional
	public void restoreTeam(Long captainId,Long teamId){
		Team team = teamDao.findById(Team.class, teamId);
    	team.setDismissed(2);
    	team.setDismisser(null);
    	teamDao.saveOrUpdate(team);
    	Player cap = playerDao.findById(Player.class, captainId);
    	Team oriTeam = cap.getTeam();
    	cap.setTeam(team);
    	cap.setIsCaptain(1);
    	transferDao.saveTransferlog(cap, oriTeam, team);
	}
	
	@Transactional
	public void saveOrUpdate(Team one) throws Exception{
		teamDao.saveOrUpdate(one);
		this.playerDao.getSession().flush();
		String configDir = this.getClass().getClassLoader().getResource("").getPath();
		Properties pros = PropertyUtil.getProperties(configDir +"/honor.properties");
		teamDao.callProc(one.getId(), pros);
	}
	
	@Transactional
	public void callProc(long id, Properties data) {
		teamDao.callProc(id,data);
	}
	
	@Transactional(readOnly=true)
	public Integer getRegNo(Long playerId) {
		return teamDao.getRegNo(playerId);
	}
}
