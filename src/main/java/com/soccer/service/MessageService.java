package com.soccer.service;

import com.soccer.dao.MessageDao;
import com.soccer.dao.MessageTankBDao;
import com.soccer.model.Message;
import com.soccer.model.MessageTank;
import com.soccer.model.MessageTankB;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * 
 * @author Feng Jianli
 *
 */
@Service("messageService")
public class MessageService extends BaseService<Message>{

	@Resource(name = "messageDao")
	private MessageDao messageDao;
	
	@Resource(name = "messageTankBDao")
	private MessageTankBDao messageTankBDao;
	
	@Transactional(readOnly=true)
	public PageResult<Message> findByPage(Search search) throws Exception {
		return messageDao.findByPage(search);
	}
	
	@Transactional(readOnly=true)
	public MessageTankB findMessageTankB(Long id) throws Exception {
		return messageTankBDao.findById(MessageTankB.class, id);
	}
	
	@Transactional(readOnly=true)
	public PageResult<MessageTankB> findTanksByPage(Search search) throws Exception {
		return messageTankBDao.findByPage(search);
	}
	
	@Transactional
	public int toggleEnable(Long id){
		return messageDao.toggleEnable(id);
	}
	
	@Transactional
	public void updateTank(MessageTankB tank){
		messageTankBDao.update(tank);
	}
	
	@Transactional
	public void confirm(Collection<MessageTank> tanks){
		 messageDao.confirm(tanks);
	}

/*	@Transactional(readOnly=true)
	public Integer getTotalActivity(Long playerId) {
		return messageDao.getTotalActivity(playerId);
	}
	@Transactional(readOnly=true)
	public Integer getPartipateNum(Long playerId) {
		return messageDao.getPartipateNum(playerId);
	}
	@Transactional(readOnly=true)
	public Integer getCheatNum(Long playerId) {
		return messageDao.getCheatNum(playerId);
	}*/

}
