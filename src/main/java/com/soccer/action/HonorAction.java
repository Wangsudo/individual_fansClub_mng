package com.soccer.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
import com.soccer.interceptor.InitServlet;
import com.soccer.model.Honor;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.HonorService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/honor")
public class HonorAction {
	private static Logger log = LogManager.getLogger(HonorAction.class);

	@Resource(name = "honorService")
	private HonorService honorService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Honor> list(@RequestBody Search search) {
		PageResult<Honor> ret = null;
		try {
			ret = honorService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getStackTrace());
		} return ret;
	}
	
	 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public JSONObject findById(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Honor honor = honorService.findById(Honor.class,id);
	            ret.put("data", honor);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Honor honor,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = honor.getId();
            honor.setUrl(PicUtil.transferToLoc( honor.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_HONOR));
            honor.setOpTime(new Timestamp(new Date().getTime()));
            honorService.saveOrUpdate(honor);
            
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[荣誉列表]添加了记录:"+honor.getName(),Honor.class.getName(), honor.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[荣誉列表]修改了记录"+honor.getName(),Honor.class.getName(), honor.getId());
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchHonor(@RequestParam(value="name",required=false) String name,@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return honorService.searchHonor(name, pageSize);
  	}
    
    @RequestMapping(value = "/getOthers", method = RequestMethod.GET)
  	public List getOthers(@RequestParam(value="teamId",required=false) Long teamId,
  			@RequestParam(value="playerId",required=false) Long playerId,
  			@RequestParam(value="type",required=true) Integer type) {
  		return honorService.getOthers(teamId,playerId,type);
  	}
    
    // 通过id删除
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    public JSONObject del(@PathVariable("id") final Long id,
            HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Honor one = honorService.findById(Honor.class, id);
        	honorService.del(Honor.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[荣誉列表]删除了记录:" + one.getName(),Honor.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
