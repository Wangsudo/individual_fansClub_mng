package com.soccer.service;

import com.soccer.dao.PlayerDao;
import com.soccer.dao.PlayerLikeDao;
import com.soccer.dao.TeamLikeDao;
import com.soccer.dao.TransferDao;
import com.soccer.model.Player;
import com.soccer.model.PlayerLike;
import com.soccer.model.Team;
import com.soccer.model.TeamLike;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Service("playerService")
public class PlayerService  extends BaseService<Player>{

	private static Logger log = LogManager.getLogger(PlayerService.class);

	@Resource(name = "playerDao")
	private PlayerDao playerDao;
	
	@Resource(name = "transferDao")
	private TransferDao transferDao;
	
	@Resource(name = "teamLikeDao")
	private TeamLikeDao teamLikeDao;
	
	@Resource(name = "playerLikeDao")
	private PlayerLikeDao playerLikeDao;

	/**
	 * 
	 * @param search
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly=true)
	public PageResult<Player> findByPage(Search search) throws Exception {
		PageResult<Player> pageResult =  playerDao.findByPage(search);
		return pageResult;
	}
	
	@Transactional
	public void saveTeamLike(TeamLike entity){
		entity.setCreateTime(new Timestamp(new Date().getTime()));
		teamLikeDao.save(entity);
		teamLikeDao.calcuLikeNum(entity.getTeam().getId());
	}
	
	@Transactional
	public void savePlayerLike(PlayerLike entity){
		entity.setCreateTime(new Timestamp(new Date().getTime()));
		playerLikeDao.save(entity);
		playerLikeDao.calcuLikeNum(entity.getPlayer().getId());
	}
	
	@Transactional(readOnly=true)
	public Player findPlayerById(Long id) {
		Player one =  playerDao.findById(Player.class, id);
		   return one;
	}
	
	/**
	 * 获取活跃指数
	 * 活跃指数：球员实际参与的总签到次数除以他所在球队（从他网站注册以来所有所在球队）总的发起的签到次数
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true)
	public Map<String, Object> getActive(Long id) {
		   Object[] statics = (Object[])playerDao.getActive(id);
	    	Map<String, Object> map = new HashMap<String, Object>();
		   map.put("attendTimes",statics[0]);
		   map.put("totalEvents",statics[1]);
		   return map;
	}


	@Transactional
	public void resetPassword(Long id) {
		playerDao.excuteSqlUpdate("update football_player set password = 111111 where id = ?",id);
	}


	@Transactional(readOnly = true)
	public List<Player> findPlayersByTeam(Long teamId,Boolean isCaptain){
		return playerDao.findPlayersByTeam(teamId, isCaptain);
	}
	
	@Transactional(readOnly=true)
	public Map<String, Object> findLikes(Long playerId){
		Map<String, Object> map = new HashMap();
		List<TeamLike> teamLikes = playerDao.findTeamLikes(playerId);
		List<PlayerLike> playerLikes = playerDao.findPlayerLikes(playerId);
		map.put("teamLikes",teamLikes);
		map.put("playerLikes",playerLikes);
		return map;
	}
	
	/**
	 * 
	 * @param player
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public void updatePlayer(Player player) throws Exception{
		if(player.getId() == null){
			playerDao.saveOrUpdate(player);
			return;
		}
		Player ori = playerDao.findById(Player.class, player.getId());
		playerDao.getSession().evict(ori);
		transferDao.saveTransferlog(player,ori.getTeam(),player.getTeam());
		//如果修改前后都是属于同一支球队,但是由球员变为队长
		if(ori.getTeam()!=null && player.getTeam()!=null && ori.getTeam().equals(player.getTeam()) && (ori.getIsCaptain()==null || ori.getIsCaptain()!=1)  &&   player.getIsCaptain()!=null && player.getIsCaptain() ==1 ){
			playerDao.setCaptain(player.getId(), player.getTeam().getId());
		}
		//如果修改前属另一球队修改后变为队长
		else if(ori.getTeam()!=null && !ori.getTeam().equals(player.getTeam()) && player.getIsCaptain()!=null && player.getIsCaptain() ==1 ){
			playerDao.setCaptain(player.getId(), player.getTeam().getId());
		}
		//如果修改前无球队,修改后变为队长
		else if(ori.getTeam()==null && player.getIsCaptain()!=null && player.getIsCaptain() ==1){
			playerDao.setCaptain(player.getId(), player.getTeam().getId());
		}
	}
	
	/**
	 * 
	 * @param player
	 * @throws Exception
	 */
	@Transactional
	public void savePlayer(Player player) throws Exception{
		transferDao.saveTransferlog(player,null,player.getTeam());
	}
	
	@Transactional
	public void setCaptain(Long newCaptainId, Long teamId){
	     playerDao.setCaptain(newCaptainId,teamId);
	}
	
	@Transactional
	public void dismissPlayer(Long playerId){
		Player player = playerDao.findById(Player.class, playerId);
		Team oriTeam = player.getTeam();
		player.setTeam(null);
		transferDao.saveTransferlog(player,oriTeam,null);
	}
	@Transactional
	public int changePassword(Long playerId,String password ){
		return playerDao.changePassword(playerId, password);
	}
	
	@Transactional(readOnly=true)
	public Player findPlayerByAccount(String account,Long id) {
		return playerDao.findPlayerByAccount(account,id);
	}
	@Transactional(readOnly=true)
	public Player findPlayerByMobile(String mobile,Long id) {
		return playerDao.findPlayerByMobile(mobile,id);
	}
	
	@Transactional(readOnly=true)
	public Player findPlayerByEmail(String email,Long id) {
		return playerDao.findPlayerByEmail(email,id);
	}
	
	@Transactional(readOnly=true)
	public Player findPlayerByIdno(String idno,Long id) {
		return playerDao.findPlayerByIdno(idno,id);
	}
	
	@Transactional(readOnly=true)
	public List searchPlayer(String name, Boolean isCaptain,Integer pageSize){
		return playerDao.searchPlayer(name, isCaptain,pageSize);
	}
	
	@Transactional
	public void callProc( Properties data) {
		playerDao.callProc(data);
	}
	
	@Transactional(readOnly=true)
	public Integer getUnAuditNo() {
		return playerDao.getUnAuditNo();
	}

	@Transactional
	public void updateCoordinate(Player player) {
		playerDao.excuteSqlUpdate("update football_player set x = ?,y=? where id = ?",player.getX(),player.getY(),player.getId());
	}
}
