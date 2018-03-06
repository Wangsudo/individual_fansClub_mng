package com.soccer.service;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.PlayerDao;
import com.soccer.dao.PlayerExecLogDao;
import com.soccer.model.Player;
import com.soccer.model.PlayerExecLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("playerExecLogService")
public class PlayerExecLogService extends BaseService<PlayerExecLog>{

	@Resource(name = "playerExecLogDao")
	private PlayerExecLogDao playerExecLogDao;

	@Resource(name = "playerDao")
	private PlayerDao playerDao;
	/**
	 * @param user
	 * @param pageResult
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly=true)
	public PageResult<PlayerExecLog> findByPage(Search search) throws Exception {
		return playerExecLogDao.findByPage(search);
	}
	
	/**
	 * @param player
	 * @param opdesc
	 * @param beanName
	 */
	@Transactional
	public void saveExecLog(HttpSession session, Integer optype,String opdesc, String beanName,Long beanId) {
		Long playerId = (Long) session.getAttribute("player");
        PlayerExecLog log = new PlayerExecLog();
        log.setPlayer(playerDao.findById(Player.class, playerId));
        log.setOptype(optype);
        log.setOpdesc(opdesc);
        log.setBeanName(beanName);
        log.setBeanId(beanId);
        log.setOptime(new Timestamp(new Date().getTime()));
        playerExecLogDao.save(log);
    }
    
}
