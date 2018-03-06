package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.AdminUser;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.AdminUserService;
import com.soccer.util.EmailTool;
import com.soccer.util.MD5;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/adminLogin")
public class AdminLoginAction {

	private static Logger log = LogManager.getLogger(AdminLoginAction.class);
	@Resource(name = "adminUserService")
	private AdminUserService adminUserService;
	
	@Resource(name = "adminExecLogService")
	private AdminExecLogService adminExecLogService;


	// 修改密码
    @RequestMapping(value = "/changePassword/{adminId}", method = RequestMethod.GET)
    public JSONObject changePassword(
            @PathVariable("adminId")  Long adminId,
            @RequestParam("password")  String password) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            adminUserService.changePassword(adminId, password);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONObject adminLogin(@RequestBody AdminUser admin,HttpSession session) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		AdminUser user = null;
		try {
			user = adminUserService.findUserByAccount(admin.getAccount());
		} catch (Exception e) {
			log.error("此管理员帐号查询错误！");
		}
		if(user == null){
			ret.put("error","帐号不存在");
		}else if(!user.getPassword().equals(admin.getPassword())){
			ret.put("error","密码错误");
		}else{
			session.setAttribute("admin",user.getId());
			adminExecLogService.saveExecLog(session, "登陆", null,null);
			
			String sessionId = session.getId();
			ServletContext applicationCtx = session.getServletContext();
			applicationCtx.setAttribute(user.getId().toString(), sessionId);
			
			ret.put("admin", user);
			success = true;
		}
		ret.put("success", success);
		return ret;
	}
	
	@RequestMapping(value = "/keepSession", method = RequestMethod.GET)
	public Map<String, String> keepSession(HttpSession session) {
		final Map<String, String> sessionContainer = new HashMap<String, String>(1);
		sessionContainer.put("JSSESSIONID", session.getId());
		return sessionContainer;
	}
	
	@RequestMapping(value = "/getUserbySession")
	public Object getUserbySession(HttpSession session) {
		Long id =  (Long) session.getAttribute("admin");
		String sessionId = session.getId();
		ServletContext applicationCtx = session.getServletContext();
		AdminUser ret = null;
		try {
			if(id != null && sessionId.equals(applicationCtx.getAttribute(id.toString())) ){
				ret = adminUserService.findById(AdminUser.class,id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 后台用户注销登陆
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/clearSession")
	public Object clearSession(HttpSession session) {
		JSONObject ret = new JSONObject();
		Long adminId = (Long) session.getAttribute("admin");
		if(adminId !=null){
			ServletContext applicationCtx = session.getServletContext();
			applicationCtx.removeAttribute(adminId.toString());
			session.removeAttribute("admin");
		}
		ret.put("success", true);
		return ret;
	}
	
	@RequestMapping(value = "/forgetPassword/{id}", method = RequestMethod.GET)
	public JSONObject forgetPassword(@PathVariable("id") Long id,@RequestParam("origin") String origin) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			Timestamp forgetPasswordTime = new Timestamp(new Date().getTime());
			
			AdminUser user = adminUserService.findById(AdminUser.class,id);
			Timestamp lastSendTime = user.getForgetPasswordTime();
			if(lastSendTime!=null){
				 Calendar deadlineTime = Calendar.getInstance();
				 deadlineTime.setTime(lastSendTime);
				 deadlineTime.add(Calendar.DATE, 1);
				 Calendar now = Calendar.getInstance();
				 now.setTime(lastSendTime);
				 if(deadlineTime.after(now)){
					 ret.put("message", "密码重置邮件已发送至您的邮箱,24 小时内有效");
					 ret.put("success", success);
					 return ret;
				 }
			}
			String adminId = user.getId().toString();
			
			forgetPasswordTime.setNanos(0);
			//更新发送忘记密码时间
			user.setForgetPasswordTime(forgetPasswordTime);
			adminUserService.saveOrUpdate(user);
			
			String token = MD5.encodeByMd5AndSalt(forgetPasswordTime.toString());
			
            String email = user.getEmail();
            String title = "reset password";// 邮件标题
            String templetPath = InitServlet.APPLICATION_URL +"lenovo/i18n/findPass.txt";
            String []args1 = new String[]{origin, adminId,token };// 邮件模板的参数设置
            System.out.println(EmailTool.getBean().sendEmail(email, title, templetPath, args1, null));// 发送邮件
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
		ret.put("success", success);
		return ret;
	}
	
	@RequestMapping(value = "/resetPassword/{id}", method = RequestMethod.GET)
	public ModelAndView getUserbySession(@PathVariable("id") Long id,@RequestParam("token") String data) {
		ModelAndView modelAndView = new ModelAndView(); 
		try {
			AdminUser user = adminUserService.findById(AdminUser.class,id);
		    if(user != null){
		    	Timestamp forgetPasswordTime = user.getForgetPasswordTime();
		    	if(forgetPasswordTime == null){
		    		modelAndView.setViewName("/lenovo/resetPasswordOverdue.html");  
		    		return modelAndView;  
		    	}
			    Calendar toDate = Calendar.getInstance();
	            toDate.setTime(forgetPasswordTime);
	            toDate.add(Calendar.DATE, 1);
	            if(toDate.before(new Date())){
	            	modelAndView.setViewName("/lenovo/resetPasswordOverdue.html");  
	            }else if(!MD5.encodeByMd5AndSalt(forgetPasswordTime.toString()).equals(data)){
	            	modelAndView.setViewName("/lenovo/resetPasswordOverdue.html");  
	            }else{
	            	modelAndView.setViewName("/lenovo/resetPassword.html");  
	            }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		  return modelAndView;  
	}
	
	
}
