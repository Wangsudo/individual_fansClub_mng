package com.soccer.interceptor;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.soccer.service.PlayerService;
import com.soccer.service.TeamService;
import com.soccer.util.PropertyUtil;

@Component("pointTask")
public class PointTask {
	
	@Resource(name = "playerService")
	private PlayerService playerService;
	
	@Resource(name = "teamService")
	private TeamService teamService;
	
	@Scheduled(cron="30 10 1 * * ?") //每天1点执行一次
	public void calucPoint() {
	System.out.println("spring task 注解使用。。。计算球员与球队积分");
		String configDir = this.getClass().getClassLoader().getResource("").getPath();
		try {
			Properties pros = PropertyUtil.getProperties(configDir +"/honor.properties");
			playerService.callProc(pros);
			teamService.callProc(0l, pros);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
