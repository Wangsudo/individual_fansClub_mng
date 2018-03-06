package com.soccer.action;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Apply;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.ApplyService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/apply")
public class ApplyAction {
	private static Logger log = LogManager.getLogger(ApplyAction.class);

	@Resource(name = "applyService")
	private ApplyService applyService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Apply> list(@RequestBody Search search) {
		PageResult<Apply> ret = null;
		try {
			ret = applyService.findByPage(search);
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
	            Apply apply = applyService.findById(Apply.class,id);
	            ret.put("data", apply);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Apply apply,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = apply.getId();
            applyService.saveOrUpdate(apply);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[站内信列表]添加了记录:"+apply.getTitle(),Apply.class.getName(), apply.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[站内信列表]修改了记录"+apply.getTitle(),Apply.class.getName(), apply.getId());
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    // 通过id删除
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    public JSONObject del(@PathVariable("id") final Long id,
            HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Apply one = applyService.findById(Apply.class, id);
        	applyService.del(Apply.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[站内信列表]删除了记录:" + one.getTitle(),Apply.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
	 * 加解锁球员
	 * @throws IOException 
	 */
    @RequestMapping(value = "/toggleLock", method = RequestMethod.GET)
	public JSONObject toggleEnable(@RequestParam("id") Long id,HttpSession session) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = true;
		applyService.toggleEnable(id);
		try {
            adminExecLogService.saveExecLog(session, "在[站内信列表]修改了赛士前端可见度",Apply.class.getName(), id);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		ret.put("success", success);
		return ret;
	}
    
}
