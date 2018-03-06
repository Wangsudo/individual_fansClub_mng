package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.bean.PlayerWeight;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.AdminUser;
import com.soccer.model.Player;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.PlayerService;
import com.soccer.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(value = "/player")
public class PlayerAction {
	private static Logger log = LogManager.getLogger(PlayerAction.class);

	@Resource(name = "playerService")
	private PlayerService playerService;
	
    @Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;
    
/*	@Resource(name = "transferService")
	private TransferService transferService;*/


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageResult<Player> list(@RequestBody Search search) {
        PageResult<Player> ret = null;
        try {
            ret = playerService.findByPage(search);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ret;
    }
    
  //判断球员字段的唯一性
    @RequestMapping(value = "/checkUnique", method = RequestMethod.POST)
  	public JSONObject checkUnique(@RequestParam(value="id",required = false) Long id,
  			@RequestParam(value="account",required = false) String account,
    		@RequestParam(value="idno",required = false) String idno,
    		@RequestParam(value="email",required = false) String email,
    		@RequestParam(value="mobile",required = false) String mobile){
    	JSONObject ret = new JSONObject();
    	Player player = null ;
    	if(account!=null){
    		player  = playerService.findPlayerByAccount(account, id);
    	}else if(idno!=null){
  			player  = playerService.findPlayerByIdno(idno,id);
  		}else if(email!=null){
  			player  = playerService.findPlayerByEmail(email,id);
  		}else if(mobile!=null){
  			player  = playerService.findPlayerByMobile(mobile,id);
  		}
  		if(player!=null){
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
            Player one = playerService.findPlayerById(id);
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
    public JSONObject saveOrUpdate(@RequestBody Player player,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = player.getId();
            player.setIconUrl(PicUtil.transferToLoc( player.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PLAYER));
            
            playerService.updatePlayer(player);
           
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[球员列表]添加了记录",Player.class.getName(), player.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[球员列表]修改了记录",Player.class.getName(), player.getId());
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
	public JSONObject toggleLock(@RequestBody Player player,HttpSession session) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = true;
		Player mem = playerService.findPlayerById(player.getId());
		mem.setLocks(player.getLocks());
		mem.setModifier((Long) session.getAttribute("admin"));
		mem.setModifyTime(new Timestamp(new Date().getTime()));
		try {
            adminExecLogService.saveExecLog(session, "在[球员列表]修改了权限",Player.class.getName(), player.getId());
			playerService.saveOrUpdate(mem);
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
        	Player one = playerService.findById(AdminUser.class, id);
        	playerService.del(Player.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[球员列表]删除了记录(account:" + one.getName() + ")",Player.class.getName(), id );
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

	// 通过id删除
	@RequestMapping(value = "/resetPassword/{id}", method = RequestMethod.GET)
	public JSONObject resetPassword(@PathVariable("id") final Long id,
						  HttpSession session) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			Player one = playerService.findById(Player.class, id);
			playerService.resetPassword(id);
			success = true;
			adminExecLogService.saveExecLog(session, "在[球员列表]重置了球员登录蜜码,记录(account:" + one.getName() + ")",Player.class.getName(), id );
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		ret.put("success", success);
		return ret;
	}


    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
  	public List searchPlayer(@RequestParam(value="name",required = false) String name,
  			@RequestParam(value="isCaptain",required = false) Boolean isCaptain,
  			@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return playerService.searchPlayer(name, isCaptain, pageSize);
  	}
    
    @RequestMapping(value = "/getActive", method = RequestMethod.GET)
  	public JSONObject getActive(@RequestParam("playerId") Long playerId) throws IOException {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 ret.put("data", playerService.getActive(playerId));
        	 success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
  		 ret.put("success", success);
         return ret;
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
    public JSONObject saveEleWeight(@RequestBody PlayerWeight data) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 
        	 String configDir = this.getClass().getClassLoader().getResource("").getPath();
        	 Properties pros = PropertyUtil.getProperties(configDir +"/honor.properties");
        	 pros.setProperty("player_total_game", data.getPlayer_total_game()+"");
        	 pros.setProperty("player_active", data.getPlayer_active()+"");
        	 pros.setProperty("player_win_rate", data.getPlayer_win_rate()+"");
        	 pros.setProperty("player_like", data.getPlayer_like()+"");
        	 pros.setProperty("beginer", data.getBeginer()+"");
        	 pros.setProperty("total", data.getTotal()+"");
        	 pros.setProperty("win", data.getWin()+"");
        	 pros.setProperty("even", data.getEven()+"");
        	 pros.setProperty("lost", data.getLost()+"");
        	 PropertyUtil.saveProperites(configDir+"/honor.properties", pros);
        	 
        	 playerService.callProc(pros);
			success = true;
         } catch (Exception e) {
        	 ret.put("message", e.getMessage());
             log.error(e.getMessage(),e);
         }
         ret.put("success", success);
         return ret;
	}
    
    
    @RequestMapping(value = "/statics", method = RequestMethod.GET)
    public JSONObject statics() {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	Integer unAuditNo =  playerService.getUnAuditNo();
        	ret.put("unAuditNo", unAuditNo);
			success = true;
         } catch (Exception e) {
        	 ret.put("message", e.getMessage());
             log.error(e.getMessage(),e);
         }
         ret.put("success", success);
         return ret;
	}
}
