package com.soccer.action;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soccer.model.AdminExecLog;
import com.soccer.service.AdminExecLogService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/adminExecLog")
public class AdminExecLogAction {
	private static Logger log = LogManager.getLogger(AdminExecLogAction.class);

	@Resource(name = "adminExecLogService")
	private AdminExecLogService adminExecLogService;

	
	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<AdminExecLog> list(@RequestBody Search search) {
		PageResult<AdminExecLog> ret = null;
		try {
			ret = adminExecLogService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return ret;
		
	}
}
