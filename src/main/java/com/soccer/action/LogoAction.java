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
import com.soccer.model.Logo;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.LogoService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/logo")
public class LogoAction {
	private static Logger log = LogManager.getLogger(LogoAction.class);

	@Resource(name = "logoService")
	private LogoService logoService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Logo> list(@RequestBody Search search) {
		PageResult<Logo> ret = null;
		try {
			ret = logoService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getStackTrace());
		} return ret;
	}
	
	 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public JSONObject findById(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Logo logo = logoService.findById(Logo.class,id);
	            ret.put("data", logo);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Logo logo,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = logo.getId();
            logo.setUrl(PicUtil.transferToLoc( logo.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_LOGO));
            logo.setOpTime(new Timestamp(new Date().getTime()));
            logoService.saveOrUpdate(logo);
            
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[队徽列表]添加了记录:"+logo.getName(),Logo.class.getName(), logo.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[队徽列表]修改了记录"+logo.getName(),Logo.class.getName(), logo.getId());
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchLogo(@RequestParam(value="name",required=false) String name,@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return logoService.searchLogo(name, pageSize);
  	}
    
    // 通过id删除
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    public JSONObject del(@PathVariable("id") final Long id,
            HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Logo one = logoService.findById(Logo.class, id);
        	logoService.del(Logo.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[队徽列表]删除了记录:" + one.getName(),Logo.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
