package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.Field;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.FieldService;
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/field")
public class FieldAction {
	private static Logger log = LogManager.getLogger(FieldAction.class);

	@Resource(name = "fieldService")
	private FieldService fieldService;
	
	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Field> list(@RequestBody Search search) {
		PageResult<Field> ret = null;
		try {
			ret = fieldService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getStackTrace());
		} return ret;
	}
	
	 @RequestMapping(value = "/{id}", method = RequestMethod.GET)
	    public JSONObject findById(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Field field = fieldService.findById(Field.class,id);
	            ret.put("data", field);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getStackTrace());
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	// 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Field field,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = field.getId();
            field.setUrl(PicUtil.transferToLoc( field.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_FIELD));
            field.setOpTime(new Timestamp(new Date().getTime()));
            fieldService.saveOrUpdate(field);
            
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[场地列表]添加了记录:"+field.getName(),Field.class.getName(), field.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[场地列表]修改了记录"+field.getName(),Field.class.getName(), field.getId());
            }
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchField(@RequestParam(value="name",required=false) String name,@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return fieldService.searchField(name, pageSize);
  	}
    
    // 通过id删除
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    public JSONObject del(@PathVariable("id") final Long id,
            HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Field one = fieldService.findById(Field.class, id);
        	fieldService.del(Field.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[场地列表]删除了记录:" + one.getName(),Field.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
}
