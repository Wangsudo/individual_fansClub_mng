package com.soccer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soccer.dao.TransferDao;
import com.soccer.model.TransferLog;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@Service("transferService")
public class TransferService  extends BaseService<TransferLog>{


	@Resource(name = "transferDao")
	private TransferDao transferDao;
	/**
	 * @param Player
	 * @param pageResult
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public PageResult<TransferLog> findByPage(Search search) throws Exception {
		return transferDao.findByPage(search);
	}
	
	/**
	 * 获取某个球员的转会历史
	 */
	@Transactional(readOnly = true)
	public List<TransferLog>  findHistory(Long playerId){
		return transferDao.findHistory(playerId);
	}
	
}
