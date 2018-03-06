package com.soccer.action;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.model.PlayerExecLog;
import com.soccer.service.PlayerExecLogService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/playerExecLog")
public class PlayerExecLogAction {

	@Resource(name = "playerExecLogService")
	private PlayerExecLogService playerExecLogService;
	private static Logger log = LogManager.getLogger(PlayerExecLogAction.class);

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<PlayerExecLog> list(@RequestBody Search search) {
		PageResult<PlayerExecLog> ret = null;
		try {
			ret = playerExecLogService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return ret;
		
	}
}
