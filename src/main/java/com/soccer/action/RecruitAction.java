package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Recruit;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.RecruitService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping(value = "/recruit")
public class RecruitAction {
	private static Logger log = LogManager.getLogger(RecruitAction.class);

	@Resource(name = "recruitService")
	private RecruitService recruitService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Recruit> list(@RequestBody Search search) {
		PageResult<Recruit> ret = null;
		try {
			ret = recruitService.findByPage(search);
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
	            Recruit recruit = recruitService.findById(Recruit.class,id);
	            ret.put("data", recruit);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Recruit recruit,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = recruit.getId();
            recruit.setOpTime(new Timestamp(new Date().getTime()));
            recruitService.saveOrUpdate(recruit);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[站内信列表]添加了记录:"+recruit.getTitle(),Recruit.class.getName(), recruit.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[站内信列表]修改了记录"+recruit.getTitle(),Recruit.class.getName(), recruit.getId());
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
        	Recruit one = recruitService.findById(Recruit.class, id);
        	recruitService.del(Recruit.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[站内信列表]删除了记录:" + one.getTitle(),Recruit.class.getName(), id );
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
		recruitService.toggleEnable(id);
		try {
            adminExecLogService.saveExecLog(session, "在[站内信列表]修改了赛士前端可见度",Recruit.class.getName(), id);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		ret.put("success", success);
		return ret;
	}
    
}
