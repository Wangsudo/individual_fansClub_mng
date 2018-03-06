package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.Photo;
import com.soccer.model.PhotoGroup;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.PhotoService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping(value = "/photo")
public class PhotoAction {
	private static Logger log = LogManager.getLogger(PhotoAction.class);

	@Resource(name = "photoService")
	private PhotoService photoService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param search
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<PhotoGroup> list(@RequestBody Search search) {
		PageResult<PhotoGroup> ret = null;
		try {
			ret = photoService.findByPage(search);
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
	            PhotoGroup photoGroup = photoService.findById(PhotoGroup.class,id);
	            ret.put("data", photoGroup);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody PhotoGroup item,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = item.getId();
            for(Photo pic:item.getPics()){
            	pic.setUrl(PicUtil.transferToLoc( pic.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PHOTO));
            }
            photoService.saveOrUpdate(item);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[图片列表]添加了记录",PhotoGroup.class.getName(), item.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[图片列表]修改了记录",PhotoGroup.class.getName(), item.getId());
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
		photoService.toggleEnable(id);
		try {
            adminExecLogService.saveExecLog(session, "在[图片列表]修改了图片前端可见度",PhotoGroup.class.getName(), id);
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
        	photoService.del(PhotoGroup.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[图片列表]删除了记录",PhotoGroup.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
