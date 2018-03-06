package com.soccer.action;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.soccer.util.StringUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Video;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.VideoService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/video")
public class VideoAction {
	private static Logger log = LogManager.getLogger(VideoAction.class);

	@Resource(name = "videoService")
	private VideoService videoService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Video> list(@RequestBody Search search) {
		PageResult<Video> ret = null;
		try {
			ret = videoService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		} return ret;
	}
	
	 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public JSONObject findById(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Video video = videoService.findById(Video.class,id);
	            if(StringUtil.isEmpty(video.getScreenshot())){
					videoService.saveVideo(video);
				}
	            ret.put("data", video);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Video video,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = video.getId();
            videoService.saveVideo(video);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[视频列表]添加了记录:"+video.getTitle(),Video.class.getName(), video.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[视频列表]修改了记录"+video.getTitle(),Video.class.getName(), video.getId());
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

	/**
	 * @throws IOException 
	 */
    @RequestMapping(value = "/toggleLock", method = RequestMethod.GET)
	public JSONObject toggleEnable(@RequestParam("id") Long id,HttpSession session) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = true;
		videoService.toggleEnable(id);
		try {
            adminExecLogService.saveExecLog(session, "在[视频列表]修改了场地前端可见度",Video.class.getName(), id);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
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
        	Video one = videoService.findById(Video.class, id);
        	videoService.del(Video.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[视频列表]删除了记录:" + one.getTitle(),Video.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
