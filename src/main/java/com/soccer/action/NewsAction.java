package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.News;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.NewsService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/news")
public class NewsAction {
	private static Logger log = LogManager.getLogger(NewsAction.class);

	@Resource(name = "newsService")
	private NewsService newsService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<News> list(@RequestBody Search search) {
		PageResult<News> ret = null;
		try {
			ret = newsService.findByPage(search);
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
	            News news = newsService.findById(News.class,id);
	            ret.put("data", news);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody News news,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = news.getId();
            newsService.saveOrUpdate(news);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[公告列表]添加了记录:"+news.getTitle(),News.class.getName(), news.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[公告列表]修改了记录"+news.getTitle(),News.class.getName(), news.getId());
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
        	News one = newsService.findById(News.class, id);
        	newsService.del(News.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[公告列表]删除了记录:" + one.getTitle(),News.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

	// 通过id置顶
	@RequestMapping(value = "/setTop/{id}", method = RequestMethod.GET)
	public JSONObject setTop(@PathVariable("id") final Long id,
						  HttpSession session) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			newsService.setTop(id);
			success = true;
		} catch (Exception e) {
			log.error(e);
		}
		ret.put("success", success);
		return ret;
	}
}
