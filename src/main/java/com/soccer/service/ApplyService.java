package com.soccer.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.ApplyDao;
import com.soccer.dao.ApplyTankBDao;
import com.soccer.dao.PlayerDao;
import com.soccer.dao.TransferDao;
import com.soccer.model.Apply;
import com.soccer.model.ApplyTank;
import com.soccer.model.ApplyTankB;
import com.soccer.model.Player;
import com.soccer.model.Team;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("applyService")
public class ApplyService extends BaseService<Apply>{

	@Resource(name = "applyDao")
	private ApplyDao applyDao;
	
	@Resource(name = "applyTankBDao")
	private ApplyTankBDao applyTankBDao;
	
	@Resource(name = "playerDao")
	private PlayerDao playerDao;
	
	@Resource(name = "transferDao")
	private TransferDao transferDao;
	
	@Transactional(readOnly=true)
	public PageResult<Apply> findByPage(Search search) throws Exception {
		return applyDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public ApplyTankB findApplyTankB(Long id) {
		return applyTankBDao.findById(ApplyTankB.class, id);
	}
	
	@Transactional(readOnly=true)
	public PageResult<ApplyTankB> findTanksByPage(Search search) throws Exception {
		return applyTankBDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public List<Apply> findOpenApply(Long playerId) {
		return applyDao.findOpenApply(playerId);
	}
	
	/**
	 * 球员对入队申请进行的操作:
	 * 1. 当球队还未进行入队审核, 球员可撤消入队申请 confirmStatus 为2
	 * 2.confirmStatus 为1时, 加入某球队. 同时更新球员所属球队, 更新转会记录
	 * @param tankId 针对某球队的入队申请id
	 * @param confirmStatus 是否确认入队.
	 */
	@Transactional
	public void confirm(Long tankId,Integer confirmStatus){
		ApplyTankB tank = applyTankBDao.findById(ApplyTankB.class, tankId);
		Player player = tank.getApply().getPlayer();
		if(confirmStatus == 1 && player !=null){
			String sql ="update apply_tank u,apply a set u.confirm_status = 2,u.confirm_time = now(),a.is_open = 2 where u.apply_id = a.id"
					+ " and a.player_id = ? and u.id != ? and u.confirm_status is null and ifnull(u.audit_status,0) != 2 and a.is_enabled =1 and a.is_open =1";
			applyTankBDao.excuteSqlUpdate(sql, player.getId(), tankId);
			Team ori = player.getTeam();
			player.setTeam(tank.getTeam());
			transferDao.saveTransferlog(player,ori,tank.getTeam());
		}
		applyTankBDao.confirm(tankId, confirmStatus);
	}
	
	/**
	 * 球队对球员的入队申请进行操作:
	 * 1. 当球队还未进行入队审核, 球员可撤消入队申请 confirmStatus 为2
	 * 2.confirmStatus 为1时, 加入某球队. 同时更新球员所属球队, 更新转会记录
	 * @param tankId 针对某球队的入队申请id
	 * @param confirmStatus 是否确认入队.
	 */
	@Transactional
	public String audit(Long tankId,Integer auditStatus,Long captainId){
		String message = "";
		ApplyTankB tank = applyTankBDao.findById(ApplyTankB.class, tankId);
		Player captain = playerDao.findById(Player.class, captainId);
		tank.setAuditStatus(auditStatus);
		tank.setAuditTime(new Timestamp(new Date().getTime()));
		tank.setCaptain(captain);
		
		if(auditStatus == 1 && tank.getApply()!=null){
			Long applyId = tank.getApply().getId();
			Apply apply = applyDao.findById(Apply.class, applyId);
			Set<ApplyTank> tanks = apply.getTanks();
			boolean hasOtherApply = false;
			for(ApplyTank one:tanks){
				if(!one.getId().equals(tankId) && one.getConfirmStatus() ==null && !new Integer(2).equals(one.getAuditStatus())){
					hasOtherApply = true;
				}
			}
			if(!hasOtherApply){
				tank.setConfirmStatus(1);
				tank.getApply().setIsOpen(2);
				Player player = apply.getPlayer();
				Team ori = player.getTeam();
				player.setTeam(tank.getTeam());
				transferDao.saveTransferlog(player,ori,tank.getTeam());
				message = player.getName()+"成功加入球队!"; 
			}
		}
		applyTankBDao.saveOrUpdate(tank);
		return message;
	}
	
	
	@Transactional
	public void saveTanks(Collection<ApplyTankB> tanks){
		applyTankBDao.saveOrUpdate(tanks);
	}
	
	@Transactional
	public int toggleEnable(Long id){
		return applyDao.toggleEnable(id);
	}
}
