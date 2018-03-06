package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.model.Game;
import com.soccer.service.AdminExecLogService;
import com.soccer.service.GameService;
import com.soccer.util.PageResult;
import com.soccer.util.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping(value = "/game")
public class GameAction {
	private static Logger log = LogManager.getLogger(GameAction.class);

	@Resource(name = "gameService")
	private GameService gameService;

	@Resource(name = "adminExecLogService")
    private AdminExecLogService adminExecLogService;
	/**
	 * 加载当前用户工作空间.
	 * @param search
	 * @return 当前用户工作空间项目
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PageResult<Game> list(@RequestBody Search search) {
		PageResult<Game> ret = null;
		try {
			ret = gameService.findByPage(search);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} return ret;
	}
	
	// 通过Id查账管理员
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject findById(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Game one = gameService.findById(Game.class, id);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }

    // 保存或更新
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdate(@RequestBody Game game,HttpSession session) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Long id = game.getId();
            gameService.saveOrUpdate(game);
            if (id == null) {
            	adminExecLogService.saveExecLog(session, "在[比赛列表]添加了记录",Game.class.getName(), game.getId());
            } else {
            	adminExecLogService.saveExecLog(session, "在[比赛列表]修改了记录",Game.class.getName(), game.getId());
            }
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
    @RequestMapping(value = "/toggleLock", method = RequestMethod.GET)
	public JSONObject toggleEnable(@RequestParam("id") Long id,HttpSession session) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = true;
		try {
            gameService.toggleEnable(id);
            adminExecLogService.saveExecLog(session, "在[比赛列表]修改了赛士前端可见度",Game.class.getName(), id);
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
        	Game one = gameService.findById(Game.class, id);
        	gameService.del(Game.class, id);
            success = true;
            adminExecLogService.saveExecLog(session, "在[比赛列表]删除了记录(account:" + one.getTitle() + ")",Game.class.getName(), id );
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        ret.put("success", success);
        return ret;
    }
}
