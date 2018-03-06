package com.soccer.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Sysconfig;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.SysconfigService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/sysconfig")
public class SysconfigAction {
	private static Logger log = LogManager.getLogger(SysconfigAction.class);

	@Resource(name = "sysconfigService")
	private SysconfigService sysconfigService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Sysconfig> list(@RequestBody Search search) {
		PageResult<Sysconfig> ret = null;
		try {
			ret = sysconfigService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		} return ret;
	}
	
	 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public JSONObject findById(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Sysconfig sysconfig = sysconfigService.findById(Sysconfig.class,id);
	            ret.put("data", sysconfig);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Sysconfig sysconfig,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            sysconfigService.saveOrUpdate(sysconfig);
        	adminExecLogService.saveExecLog(session, "在[公告列表]修改了记录"+sysconfig.getCode(),Sysconfig.class.getName(), sysconfig.getId());
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

}
