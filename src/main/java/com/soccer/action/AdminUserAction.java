package com.soccer.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.AdminUser;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.AdminUserService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/adminUser")
public class AdminUserAction {
	private static Logger log = LogManager.getLogger(AdminUserAction.class);

    @Resource(name = "adminUserService")
    private AdminUserService adminUserService;

    @Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageResult<AdminUser> list(@RequestBody Search search) {
        PageResult<AdminUser> ret = null;
        try {
            ret = adminUserService.findByPage(search);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ret;
    }

    // 通过Id查账管理员
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject findById(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            AdminUser adminUser = adminUserService.findById(AdminUser.class,id);
            ret.put("data", adminUser);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

    // 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody AdminUser adminUser,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = adminUser.getId();
            adminUser.setHeadpic(PicUtil.transferToLoc( adminUser.getHeadpic(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_ADMIN));
            adminUserService.saveOrUpdate(adminUser);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[管理者列表]添加了记录",AdminUser.class.getName(), adminUser.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[管理者列表]修改了记录",AdminUser.class.getName(), adminUser.getId());
            }
  //          adminUser.setHeadpic(PicUtil.transfer( adminUser.getHeadpic(), UPLOAD_PATH_ADMIN));
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

    // 判断重复
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public JSONObject check(@RequestBody AdminUser adminUser) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            ret = adminUserService.ckeck(adminUser);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    // 通过id删除
    @RequestMapping(value = "/del/{adminId}", method = RequestMethod.GET)
    public JSONObject delByClass(@PathVariable("adminId") final Long adminId,
            HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	AdminUser adminUser = adminUserService.findById(AdminUser.class, adminId);
            adminUserService.del(AdminUser.class, adminId);
            success = true;
            adminExecLogService.saveExecLog(session, "在[管理者列表]删除了记录(account:" + adminUser.getAccount() + ")",AdminUser.class.getName(), adminId );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

}
