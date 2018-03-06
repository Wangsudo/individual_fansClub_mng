package com.soccer.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import com.soccer.bean.TeamWeight;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.Team;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.TeamService;
import com.soccer.util.MyConstants;
import com.soccer.util.PageResult;
import com.soccer.util.PicUtil;
import com.soccer.util.PropertyUtil;
import com.soccer.util.Search;

@RestController
@RequestMapping(value = "/team")
public class TeamAction {
	private static Logger log = LogManager.getLogger(Team.class);

	@Resource(name = "teamService")
	private TeamService teamService;
	
    @Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;
    
/*	@Resource(name = "transferService")
	private TransferService transferService;*/


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageResult<Team> list(@RequestBody Search search) {
        PageResult<Team> ret = null;
        try {
            ret = teamService.findByPage(search);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ret;
    }
    
  //判断球员字段的唯一性
    @RequestMapping(value = "/checkUnique", method = RequestMethod.POST)
  	public JSONObject checkUnique(@RequestParam(value="id",required = false) Long id,
  			@RequestParam(value="name",required = false) String name){
    	JSONObject ret = new JSONObject();
    	Team team = null ;
    	if(name!=null){
    		team  = teamService.findTeamByName(name,id);
    	}
  		if(team!=null){
  			ret.put("isUnique", false);
  		}else{
  			ret.put("isUnique", true);
  		}
  		return ret;
  	}

    // 通过Id查账管理员
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject findById(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Team one = teamService.findTeamById(id);
            ret.put("data", one);
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
    public JSONObject saveOrUpdate(@RequestBody Team team,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = team.getId();
            team.setIconUrl(PicUtil.transferToLoc( team.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_TEAM));
            team.setCoverUrl(PicUtil.transferToLoc( team.getCoverUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_TEAM));
            teamService.saveOrUpdate(team);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[球队列表]添加了记录",Team.class.getName(), team.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[球员列表]修改了记录",Team.class.getName(), team.getId());
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

	/**
	 * 加解锁球员
	 * @throws IOException 
	 */
    @RequestMapping(value = "/toggleLock", method = RequestMethod.POST)
	public JSONObject toggleLock(@RequestBody Team Team,HttpSession session) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = true;
		Team mem = teamService.findTeamById(Team.getId());
		mem.setLocks(Team.getLocks());
		mem.setModifier((Long) session.getAttribute("admin"));
		mem.setModifyTime(new Timestamp(new Date().getTime()));
		try {
            adminExecLogService.saveExecLog(session, "在[球员列表]修改了权限",Team.class.getName(), Team.getId());
			teamService.saveOrUpdate(mem);
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
        	Team one = teamService.findById(Team.class, id);
        	teamService.del(Team.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[球队列表]删除了记录(team:" + one.getTeamTitle() + ")",Team.class.getName(), id );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchTeam(@RequestParam(value="name",required = false) String name,
  			@RequestParam(value="snippet",required = false) String snippet,
  			@RequestParam(value="teamType",required = false) Integer teamType,
  			@RequestParam(value="cupId",required = false) Long cupId,
  			@RequestParam(value="pageSize",required = false) Integer pageSize) throws IOException {
  		return teamService.searchTeam(name, snippet, teamType,null,cupId, pageSize);
  	}

    @RequestMapping(value = "/getAllTeam", method = RequestMethod.GET)
  	public List getAllTeam() throws IOException {
  		return teamService.getAllTeam();
  	}
    
    @RequestMapping(value = "/eleWeight", method = RequestMethod.GET)
    public Properties findEleWeight() {
		//荣誉公式中各因素的 权重
		Properties pros = null;
		try {
			String configDir = this.getClass().getClassLoader().getResource("").getPath();
			pros = PropertyUtil.getProperties(configDir +"/honor.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pros;
	}
    
    
    @RequestMapping(value = "/saveEleWeight", method = RequestMethod.POST)
    public JSONObject saveEleWeight(@RequestBody TeamWeight data) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 
        	 String configDir = this.getClass().getClassLoader().getResource("").getPath();
        	 Properties pros = PropertyUtil.getProperties(configDir +"/honor.properties");
        	 pros.setProperty("team_total_game", data.getTeam_total_game()+"");
        	 pros.setProperty("team_active", data.getTeam_active()+"");
        	 pros.setProperty("team_win_rate", data.getTeam_win_rate()+"");
        	 pros.setProperty("team_like", data.getTeam_like()+"");
        	 PropertyUtil.saveProperites(configDir+"/honor.properties", pros);
        	 teamService.callProc(0l, pros);
			 success = true;
         } catch (Exception e) {
        	 ret.put("message", e.getMessage());
             log.error(e.getMessage(),e);
         }
         ret.put("success", success);
         return ret;
	}
    
}
