package com.soccer.action;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.model.TransferLog;
import com.soccer.service.TransferService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/transferLog")
public class TransferAction {
	private static Logger log = LogManager.getLogger(TransferAction.class);

	@Resource(name = "transferService")
	private TransferService transferService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<TransferLog> list(@RequestBody Search search) {
		PageResult<TransferLog> ret = null;
		try {
			ret = transferService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return ret;
	}
}
