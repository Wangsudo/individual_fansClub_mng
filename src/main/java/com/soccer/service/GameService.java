package com.soccer.service;

import com.soccer.dao.GameDao;
import com.soccer.dao.GameLogDao;
import com.soccer.dao.PlayerDao;
import com.soccer.model.Game;
import com.soccer.model.GameLog;
import com.soccer.model.Player;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 002195
 * 
 */
@Service("gameService")
public class GameService extends BaseService<Game>{

	@Resource(name = "gameDao")
	private GameDao gameDao;
	
	@Resource(name = "gameLogDao")
	private GameLogDao gameLogDao;
	
	@Resource(name = "playerDao")
	private PlayerDao playerDao;

	/**
	 * @param search
	 * @return
	 */
	@Transactional(readOnly=true)
	public PageResult<Game> findByPage(Search search) throws Exception{
		return gameDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public PageResult<Game> findScoreByPage(Search search) throws Exception{
		return gameDao.findScoreByPage(search);
	}
	
	@Transactional(readOnly=true)
	public PageResult<GameLog> findGameLogs(Search search) throws Exception{
		return gameLogDao.findByPage(search);
	}
	@Transactional(readOnly=true)
	public List<Game> toSocreGames(Long teamId){
		return gameDao.toSocreGames(teamId);
	}
	
	
	/**
	 * 
	 * @param game
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public void saveOrUpdate(Game game) throws Exception{
		gameDao.saveOrUpdate(game);
		if( game.getAuditStatus()!=null &&  game.getAuditStatus()==1){
			List <GameLog> list = gameLogDao.findByGameid(game.getId());
			//若球赛通过审核,生成gameLog,记录每个球员的比赛记录
			if(list.size() == 0){
				List<Player> teamAplayers = playerDao.findPlayersByTeam(game.getTeamA().getId(),null);
				List<Player> teamBplayers = playerDao.findPlayersByTeam(game.getTeamB().getId(),null);
				for( Player p : teamAplayers){
					GameLog rec = new GameLog();
					rec.setGame(game);
					rec.setPlayer(p);
					rec.setTeam(game.getTeamA());
					list.add(rec);
				}
				for( Player p : teamBplayers){
					GameLog rec = new GameLog();
					rec.setGame(game);
					rec.setPlayer(p);
					rec.setTeam(game.getTeamB());
					list.add(rec);
				}
				gameLogDao.saveOrUpdate(list);
			}
			//计算此次比赛造成的胜负平
			gameDao.calcuScore(game.getId());
		}
		
	}

	@Transactional
	public void delScore(Long teamId,Long[] gameIds){
		for(Long gameId:gameIds){
			Game game = this.gameDao.findById(Game.class,gameId);
			if(game.getScoreA1()!=null && game.getScoreA2()!=null){
				continue;
			}
			game.setAuditor(null);
			game.setAuditTime(null);
			game.setAuditStatus(null);
			game.setScoreA(null);
			game.setScoreB(null);
			if(game.getTeamA()!=null && game.getTeamA().getId().equals(teamId)){
				game.setScoreA1(null);
				game.setScoreB1(null);
			}else{
				game.setScoreA2(null);
				game.setScoreB2(null);
			}
			gameDao.saveOrUpdate(game);
		}
	}

	@Transactional
	public void toggleEnable(Long id){
		gameDao.toggleEnable(id);
	}
	
	/**
	 * 获取球队当天约战次数
	 * @param teamId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Integer getTodayDareNum(Long teamId){
		return gameDao.getTodayDareNum(teamId);
	}
	
}
