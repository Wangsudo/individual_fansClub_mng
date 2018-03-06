package com.soccer.service;

import com.soccer.dao.PlayerDao;
import com.soccer.dao.RecruitDao;
import com.soccer.dao.TransferDao;
import com.soccer.model.Player;
import com.soccer.model.Recruit;
import com.soccer.model.Team;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("recruitService")
public class RecruitService extends BaseService<Recruit>{

	@Resource(name = "playerDao")
	private PlayerDao playerDao;
	
	@Resource(name = "recruitDao")
	private RecruitDao recruitDao;
	
	@Resource(name = "transferDao")
	private TransferDao transferDao;
	
	@Transactional(readOnly=true)
	public PageResult<Recruit> findByPage(Search search) throws Exception {
		return recruitDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List<Recruit> findOpenRecruit(Long teamId,Long playerId) {
		return recruitDao.findOpenRecruit(teamId,playerId);
	}
	
	@Transactional
	public int toggleEnable(Long id){
		return recruitDao.toggleEnable(id);
	}
	
	/**
	 * 球员对入队邀请进行的操作:
	 * 1.confirmStatus 为1时, 加入某球队,如果有其它球队也招此球员,刚自动设置confirmStatus为2 . 同时更新球员所属球队, 更新转会记录
	 * 2.confirmStatus 为2时, 放弃加入球队
	 * @param recruitId 针对某球员的入队邀请id
	 * @param confirmStatus 是否确认入队.
	 */
	@Transactional
	public void confirm(Long recruitId,Integer confirmStatus){
		Recruit recruit = recruitDao.findById(Recruit.class, recruitId);
		Player player = recruit.getPlayer();
		if(confirmStatus == 1 && player !=null){
			String sql ="update recruit u set u.confirm_status = 2,u.confirm_time = now()  "
					+ "where u.player_id = ? and u.confirm_status is null and u.is_enabled =1";
			recruitDao.excuteSqlUpdate(sql,player.getId());
			Team ori = player.getTeam();
			player.setTeam(recruit.getTeam());
			transferDao.saveTransferlog(player,ori,recruit.getTeam());
		}
		recruitDao.confirm(recruitId, confirmStatus);
	}
}
