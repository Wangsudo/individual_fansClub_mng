package com.soccer.action;

import java.io.IOException;
import java.util.ArrayList;
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
import com.soccer.model.AdminUser;
import com.soccer.model.Ads;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.AdsService;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/ads")
public class AdsAction {
	private static Logger log = LogManager.getLogger(AdsAction.class);
	public static final String UPLOAD_PATH_ADS = "/formalPic/ads";
	@Resource(name = "adsService")
	private AdsService adsService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Ads> list(@RequestBody Search search) {
		PageResult<Ads> ret = null;
		try {
			ret = adsService.findByPage(search);
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
	            Ads ads = adsService.findById(Ads.class,id);
	            ret.put("data", ads);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Ads ads,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = ads.getId();
            ads.setUrl(PicUtil.transferToLoc( ads.getUrl(),InitServlet.APPLICATION_URL, UPLOAD_PATH_ADS));
            adsService.saveOrUpdate(ads);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[广告列表]添加了记录:"+ads.getTitle(),Ads.class.getName(), ads.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[广告列表]修改了记录"+ads.getTitle(),Ads.class.getName(), ads.getId());
            }
            success = true;
        } catch (Exception e) {
        	e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

    @RequestMapping(value = "/release", method = RequestMethod.POST)
    public JSONObject release(@RequestBody List<JSONObject> items) {
        List<Ads> many = new ArrayList<Ads>();
        for (JSONObject item : items) {
        	Ads one = JSONObject.toJavaObject(item, Ads.class);
            many.add(one);
        }
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            adsService.saveOrUpdate(many);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
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
        	Ads one = adsService.findById(Ads.class, id);
        	adsService.del(Ads.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[广告列表]删除了记录:" + one.getTitle(),Ads.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
