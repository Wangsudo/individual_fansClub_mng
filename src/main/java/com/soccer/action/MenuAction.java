package com.soccer.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Menu;
import com.soccer.service.MenuService;

@RestController
@RequestMapping(value = "/menu")
public class MenuAction {
	private static Logger log = LogManager.getLogger(MenuAction.class);
	
	@Resource(name = "menuService")
	private MenuService menuService;

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public JSONObject all() {
		JSONObject ret = new JSONObject();
		boolean success = false;
		 try {
			List<Menu> list =  menuService.findAll();
			ret.put("list",list);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		 ret.put("success", success);
		 return ret;
	}

}
