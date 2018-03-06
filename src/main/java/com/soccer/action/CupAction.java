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
import com.soccer.model.Cup;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.CupService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/cup")
public class CupAction {
	private static Logger log = LogManager.getLogger(CupAction.class);

	@Resource(name = "cupService")
	private CupService cupService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Cup> list(@RequestBody Search search) {
		PageResult<Cup> ret = null;
		try {
			ret = cupService.findByPage(search);
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
	            Cup cup = cupService.findById(Cup.class,id);
	            ret.put("data", cup);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Cup item,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = item.getId();
            item.setIconUrl(PicUtil.transferToLoc( item.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_CUP));
            item.setOpTime(new Timestamp(new Date().getTime()));
            item.setOp((Long) session.getAttribute("admin"));
            cupService.saveOrUpdate(item);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[杯赛列表]添加了记录",Cup.class.getName(), item.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[杯赛列表]修改了记录",Cup.class.getName(), item.getId());
            }
            success = true;
        } catch (Exception e) {
        	e.printStackTrace();
        	ret.put("error", e.getMessage());
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
        	cupService.del(Cup.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[杯赛列表]删除了记录",Cup.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchPlayer(@RequestParam(value="name",required = false) String name,
  			@RequestParam(value="isPublic",required = false) Integer isPublic,
  			@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return cupService.searchCup(name, isPublic, pageSize);
  	}
    
    @RequestMapping(value = "/getPhases", method = RequestMethod.GET)
  	public List getPhases(@RequestParam(value="cupId",required = true) Long cupId)  {
  		return cupService.getPhases(cupId);
  	}
    
    
}
