package com.soccer.action;

import java.sql.ResultSet;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.AdminUser;
import com.soccer.model.Role;
import com.soccer.service.RoleService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/role")
public class RoleAction {

	@Resource(name = "roleService")
	private RoleService roleService;
	private static Logger log = LogManager.getLogger(RoleAction.class);

	/**
	 * 加载当前用户工作空间.
	 * @param userId
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Role> list(@RequestBody Search search) {
		PageResult<Role> ret = null;
		try {
			ret =roleService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return ret;
	}
	//通过id查询
	@RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
	public Role findById(@PathVariable("roleId") final Long roleId) {
		Role role =null;
		try {
			role=roleService.findById(Role.class,roleId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return role;
	}
	//保存或更新
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public JSONObject saveOrUpdate(@RequestBody Role role) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			roleService.saveOrUpdate(role);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		ret.put("success", success);
		return ret;
	}
	//删除
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public JSONObject delete(@RequestBody Role role) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			roleService.delete(role);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		ret.put("success", success);
		return ret;
	}
	//通过id删除
	@RequestMapping(value = "/del/{roleId}", method = RequestMethod.GET)
	public JSONObject delByClass(@PathVariable("roleId") final Long roleId) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			success = roleService.delByClass(roleId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		ret.put("success", success);
		return ret;
	}

}
