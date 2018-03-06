package com.soccer.action;

import com.alibaba.fastjson.JSONObject;
import com.soccer.interceptor.InitServlet;
import com.soccer.model.*;
import com.soccer.service.*;
import com.soccer.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping(value = "/app")
public class InterfaceAction {
    private static Logger log = LogManager.getLogger(InterfaceAction.class);
    
    @Resource(name = "playerService")
	private PlayerService playerService;
    
    @Resource(name = "teamService")
	private TeamService teamService;
    
    @Resource(name = "adsService")
   	private AdsService adsService;
    
	@Resource(name = "fieldService")
	private FieldService fieldService;
	
	@Resource(name = "gameService")
	private GameService gameService;
	
	@Resource(name = "newsService")
	private NewsService newsService;

	@Resource(name = "messageService")
	private MessageService messageService;
	
	@Resource(name = "transferService")
	private TransferService transferService;
	
	@Resource(name = "commentService")
	private CommentService commentService;
    
	@Resource(name = "videoService")
	private VideoService videoService;
	
	@Resource(name = "photoService")
	private PhotoService photoService;
	
	@Resource(name = "recruitService")
	private RecruitService recruitService;
	
	@Resource(name = "applyService")
	private ApplyService applyService;
	
	@Resource(name = "cupService")
	private CupService cupService;
	
	@Resource(name = "logoService")
	private LogoService logoService;
	
	@Resource(name = "sysconfigService")
	private SysconfigService sysconfigService;
	
	@Resource(name = "commonService")
	private CommonService commonService;
	
	/**
	 * 用户登陆
	 * @param  {account: "a0002", password: "111111"}
	 * @return {success:true/false,"user":{account:"a0002",id:76,team:{}},error:"用户名或者密码错误" }
	 */
    @RequestMapping(value = "/login")
    public JSONObject login(@RequestBody Player player, HttpSession session) {
    	JSONObject result = new JSONObject();
    	String account = player.getAccount();
    	String password = player.getPassword();
			Player user = playerService.findPlayerByAccount(account,null);
			if(user == null){
				user = playerService.findPlayerByMobile(account,null);
			}
			boolean success = false;
			if (user != null && password.equals(user.getPassword())) {
				String userLocks = user.getLocks();
				String teamLocks  = null;
				if(user.getTeam()!=null){
					teamLocks = user.getTeam().getLocks();
				}
				if(canLogin(userLocks) && canLogin(teamLocks)){
					session.setAttribute("playerId", user.getId());
					result.put("user", user);
					result.put("access_token",videoService.getYoukuAccessToken());
					success = true;
				}else{
					result.put("error", "您被禁止登陆!");
				}
			} else {
				result.put("error", "用户名或者密码错误");
		    }
			result.put("success", success);
		return result;
	}
    
    private boolean canLogin(String locks){
    	if(locks == null){
    		return true;
    	}
    	try {
			JSONObject accessObj = JSONObject.parseObject(locks);
			JSONObject loginObj = accessObj.getJSONObject("login");
			if(loginObj == null){
				return true;
			}
			Integer v = loginObj.getInteger("v");
			if(v!=2){
				return true;
			}
		    String toDate = loginObj.getString("toDate");
		    if(toDate != null ){
		    	Date date = DateUtils.string2Date(toDate, DateUtils.DATE_PATTERN);
		    	if(date.before(new Date())){
		    		return true;
		    	}
		    }
		    return false;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			return true;
		}
    }
    
    @RequestMapping(value = "/canRegTeam")
    public JSONObject canRegTeam(@RequestParam("playerId") Long playerId) {
    	JSONObject ret = new JSONObject();
    	Boolean success = false;
		Integer no = teamService.getRegNo(playerId);
		Sysconfig config = this.sysconfigService.findByCode("create_team_max");
		String create_team_max  = config.getValue();
		if( Integer.valueOf(create_team_max)> no){
			success = true;
		}else{
			ret.put("error", "您今天已注册"+no+"个球队！达到了系统上限："+create_team_max+"。请明天再试！");
		}
		ret.put("success", success);
		return ret;
	}
    
    /**
     * 用户登陆后,再次获取登陆的用户
     * @return {user:{account:"a0002",id:76,team:{}},success:true/false}
     * @throws Exception 
     */
    @RequestMapping(value = "/getUserbySession")
	public JSONObject getUserbySession(HttpSession session) {
    	JSONObject ret = new JSONObject();
    	Boolean success = false;
		Long playerId =  (Long) session.getAttribute("playerId");
		Player user = null;
		try {
			if(playerId != null){
				user = playerService.findById(Player.class, playerId);
				ret.put("user", user);
				ret.put("access_token",videoService.getYoukuAccessToken());
				success = true;
			}
		} catch (Exception e) {
			ret.put("error",e.getMessage());
			log.error(e);
		}
		ret.put("success", success);
		return ret;
	}
    
    /**
     * 注销登陆 
     * @return {success:true}
     */
    @RequestMapping(value = "/logout")
	public JSONObject logout(HttpSession session) {
    	JSONObject ret = new JSONObject();
		session.invalidate();
		ret.put("success", true);
		return ret;
	}
    
    /**
     * 获取首页的banner广告. position字段非必须.如果不指定, 就是获取所有广告.
     * @param position 广告位置 [[1,"幻灯片"],[2,"横条"],[3,"左广告"],[4,"右广告"]]
     * @return {"data":[{"content":"超级颜论","id":2,"modifier":1,"modifyTime":1453947675000,"position":2,"seq":0,"startTime":1473870012000,"status":2,"title":"超级颜论","url":"{\"3\":\"/temp/20160915002006006-980x70.jpg\",\"2\":\"/temp/20160915002006006-640x45.jpg\",\"1\":\"/temp/20160915002006006-64x4.jpg\"}"},{"content":"右广告备注","id":3,"modifier":1,"modifyTime":1461835229000,"position":4,"seq":0,"startTime":1473869913000,"status":2,"title":"右广告","url":"{\"3\":\"/temp/20160915001832473-362x517.jpg\",\"2\":\"/temp/20160915001832473-362x517.jpg\",\"1\":\"/temp/20160915001832473-44x64.jpg\"}"},{"content":"左广告备注","id":4,"modifier":1,"modifyTime":1450462608000,"position":3,"seq":0,"startTime":1473869864000,"status":2,"title":"左广告","url":"{\"3\":\"/temp/20160915001723085-362x517.jpg\",\"2\":\"/temp/20160915001723085-362x517.jpg\",\"1\":\"/temp/20160915001723085-44x64.jpg\"}"}],"success":true}
     */
    @RequestMapping(value = "/findAds")
    public JSONObject findAds(@RequestParam(value="position",required = false) Integer position){
    	JSONObject ret = new JSONObject();
    	boolean success = false;
    	try {
			ret.put("data", adsService.findAds(position));
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
    	ret.put("success", success);
    	return ret;
	}
    
    /**
     * 获取杯赛,要么此杯赛为公共的, 或者teamA 和teamB都有参赛资格的.
     * 此方法已弃用
     * @param teamA teamA的id
     * @param teamB teamB的id
     * @return 
     */
    @RequestMapping(value = "/getCups")
    @Deprecated
    public JSONObject getCups(@RequestParam(value="teamA",required = true) Long teamA,@RequestParam(value="teamB",required = false) Long teamB){
    	JSONObject ret = new JSONObject();
    	boolean success = false;
    	try {
			ret.put("data", cupService.getCups(teamA, teamB));
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
    	ret.put("success", success);
    	return ret;
	}
    
    /**
     * 获取公共杯赛,如草根杯
     * @return {"data":[{"id":1,"name":"草根杯"}],"success":true}
     */
    @RequestMapping(value = "/getPublicCups")
    public JSONObject getPublicCups(){
    	JSONObject ret = new JSONObject();
    	boolean success = false;
    	try {
			ret.put("data", cupService.getPublicCups());
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
    	ret.put("success", success);
    	return ret;
	}
    
    /**
     * 前台没有球队的球员创建球队时,获取系统提供的队徽
     * @return {"data":[{"id":5,"name":"伯纳乌","op":1,"opTime":1477238323000,"url":"{\"1\":\"/formalPic/logo/20161023235843011-64x64.jpg\",\"2\":\"/formalPic/logo/20161023235843011-268x268.jpg\",\"3\":\"/formalPic/logo/20161023235843011-268x268.jpg\"}"},{"id":9,"name":"贾克斯","op":1,"opTime":1477238317000,"url":"{\"1\":\"/formalPic/logo/20161023235836558-64x64.jpg\",\"2\":\"/formalPic/logo/20161023235836558-160x160.jpg\",\"3\":\"/formalPic/logo/20161023235836558-160x160.jpg\"}"}],"success":true}
     */
    @RequestMapping(value = "/getLogos")
    public JSONObject getLogos(){
    	JSONObject ret = new JSONObject();
    	boolean success = false;
    	try {
			ret.put("data", logoService.getAll());
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
    	ret.put("success", success);
    	return ret;
	}
    
    /**
     * 获取球员当前的 有效的入队申请,同一时间, 球员只有一个有效申请,tanks里面有针对多个球队的申请
     * @param playerId
     * @return 
			    {"data": {
			        "applyTime": 1483375797000, 
			        "id": 19, 
			        "isEnabled": 1, 
			        "isOpen": 1, 
			        "isPublic": 2, 
			        "player": {
			            "account": "hehef", 
			           ...
			        }, 
			        "tanks": [
			            {
			                "applyId": 19, 
			                "id": 14, 
			                "team": {
			                    "id": 2, 
			                    "teamTitle": "巴萨", 
			                    "teamType": 3, 
			                    ....
			                }
			            }
			        ], 
			        "title": "从球队详情页面申请入队"
			    }, 
			    "success": true
			}
     */
    @RequestMapping(value = "/activeApply", method = RequestMethod.GET)
	public JSONObject activeApply(@RequestParam("playerId") Long playerId) {
    	JSONObject ret = new JSONObject();
    	boolean success = false;
		try {
			List<Apply> applys  = applyService.findOpenApply(playerId);
			if(applys.size()>0){
				ret.put("data", applys.get(0));
			}
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} 
		ret.put("success", success);
		return ret;
	}
    
    /**
     * 获取 teamId 对应的球队是否对 playerId 对应的球员 有没有 "邀请入队".用于在球员的详情页面显示当前的邀请状态,避免得重复邀请
     * @param playerId 
     * @param teamId
     * @return 
	 *     {
			    "data": {
			        "id": 10, 
			        "isEnabled": 1, 
			        "isPublic": 2, 
			        "opTime": 1483845167000, 
			        "player": {
			            "account": "feng1", 
			            "auditStatus": 1, 
			           ...
			        }, 
			        "team": {
			            "id": 2, 
			            "teamTitle": "巴萨", 
			            "teamType": 3, 
				    ...
			        }, 
			        "title": "从球员详情页面邀请入队"
			    }, 
			    "success": true
			}
     */
    @RequestMapping(value = "/activeRecruit", method = RequestMethod.GET)
	public JSONObject activeRecruit(@RequestParam("playerId") Long playerId,@RequestParam("teamId") Long teamId) {
    	JSONObject ret = new JSONObject();
    	boolean success = false;
		try {
			List<Recruit> items  = recruitService.findOpenRecruit(teamId, playerId);
			if(items.size()>0){
				ret.put("data", items.get(0));
			}
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} 
		ret.put("success", success);
		return ret;
	}
    
    /**
     * 获取球员的转会历史
     * @param playerId 
     * @return {"data":[{"fromTeam":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1482159790000,"dismissed":1,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477242694000,"url":"{\"3\":\"/formalPic/honor/20161024011133552-220x220.jpg\",\"2\":\"/formalPic/honor/20161024011133552-220x220.jpg\",\"1\":\"/formalPic/honor/20161024011133552-64x64.jpg\"}"},"id":13,"teamId":1},{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477242702000,"url":"{\"3\":\"/formalPic/honor/20161024011141594-268x554.jpg\",\"2\":\"/formalPic/honor/20161024011141594-268x554.jpg\",\"1\":\"/formalPic/honor/20161024011141594-30x64.jpg\"}"},"id":14,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463469540000,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":1},"fromTime":1482159790000,"id":99,"modifyTime":1483551626000,"player":{"account":"song003","address":"法国","auditStatus":1,"auditTime":1463541264000,"birth":231696000000,"createTime":1463465880000,"creater":1,"email":"sdy888@163.COM","even":1,"gender":1,"height":182,"iconUrl":"/picture/playerImg/324x324-20160517141732.jpg","id":3,"idno":"310110197705060410","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585918778","modifyTime":1463541264000,"name":"本泽马","nickname":"本泽马","password":"111111","position":2,"team":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"total":2,"uniformNumber":9,"weight":86,"win":1},"toTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"toTime":1483551626000},{"fromTime":1475124475000,"id":93,"player":{"account":"song003","address":"法国","auditStatus":1,"auditTime":1463541264000,"birth":231696000000,"createTime":1463465880000,"creater":1,"email":"sdy888@163.COM","even":1,"gender":1,"height":182,"iconUrl":"/picture/playerImg/324x324-20160517141732.jpg","id":3,"idno":"310110197705060410","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585918778","modifyTime":1463541264000,"name":"本泽马","nickname":"本泽马","password":"111111","position":2,"team":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"total":2,"uniformNumber":9,"weight":86,"win":1}}],"success":true}
     */
    @RequestMapping(value = "/transfer/history")
    public JSONObject transferHistory(@RequestParam("playerId") Long playerId){
    	JSONObject ret = new JSONObject();
    	boolean success = false;
    	try {
			ret.put("data", transferService.findHistory(playerId));
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
    	ret.put("success", success);
    	return ret;
	}
    
    /**
     * 解散球队
     * @param captainId 队长id
     * @param teamId	球队id
     * @return {success:true,error:"reason"}
     */
    @RequestMapping(value = "/team/dismissTeam", method = RequestMethod.GET)
    public JSONObject dismissTeam(@RequestParam("captainId") Long captainId,@RequestParam("teamId") Long teamId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	teamService.dismissTeam(teamId,captainId);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 现在业务上不允许恢复球队了。
     * 恢复球队  球员必须是解散球队的队长
     * @param captainId 队长id
     * @param teamId	球队id
     * @return {success:true,error:"reason"}
     */
    @Deprecated
    @RequestMapping(value = "/team/restoreTeam", method = RequestMethod.GET)
    public JSONObject restoreTeam(@RequestParam("captainId") Long captainId,@RequestParam("teamId") Long teamId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	teamService.restoreTeam(captainId,teamId);
            ret.put("data", playerService.findById(Player.class, captainId));
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 将球员踢出球队
     * @param playerId 被踢出球队的球员id 
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/dismissPlayer", method = RequestMethod.GET)
    public JSONObject dismissPlayer(@RequestParam("playerId") Long playerId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	playerService.dismissPlayer(playerId);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 退出球队
     * @param playerId 
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/quitTeam", method = RequestMethod.GET)
    public JSONObject quitTeam(@RequestParam("playerId") Long playerId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	playerService.dismissPlayer(playerId);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取重置密码的短信
     * @param mobile 手机号
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/player/sendSms")
    public JSONObject sendSms(@RequestParam("mobile") String mobile) {
    	JSONObject ret = new JSONObject();
		boolean success = true;
		
		Player user = playerService.findPlayerByMobile(mobile, null);
		if(user == null){
			user = new Player();
			user.setMobile(mobile);
		}
		//1分钟内,防止多次发送短信
		Date forgetPasswordTime = user.getVerifyTime();
		if(forgetPasswordTime != null ){
			Calendar toDate = Calendar.getInstance();
            toDate.setTime(forgetPasswordTime);
            toDate.add(Calendar.MINUTE, 1);
            if(toDate.after(new Date())){
            	ret.put("success", false);
            	ret.put("error", "1分钟内已发送过验证码，请稍候！");
            	return ret;
            }
		}
		
		String verifyCode = (Math.random()+"").substring(2,8);
		String text = MyConstants.SMS_TEXT.replace("#code#", verifyCode);
		
		String param ="apikey="+MyConstants.SMS_API_KEY+"&mobile="+mobile+"&text="+text;
		//{"code":0,"msg":"OK","result":{"count":1,"fee":1,"sid":2788101223}}
		String feedBack = HttpRequest.sendPost(MyConstants.URI_SEND_SMS, param);
		//String feedBack = "{\"code\":0,\"msg\":\"OK\",\"result\":{\"count\":1,\"fee\":1,\"sid\":2788101223}}";
		JSONObject jsonObj = JSONObject.parseObject(feedBack, JSONObject.class);
		if(jsonObj.getInteger("code")==0){
			//保存用户验证码
			user.setVerifyCode(verifyCode);
			user.setVerifyTime(new Timestamp(new Date().getTime()) );
			try {
				playerService.saveOrUpdate(user);
				success = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				success = false;
				log.error(e.getMessage(),e.getCause());
			}
		}else{
			success = false;
			ret.put("error", "短信接口调用失败:"+jsonObj.get("msg"));
		}
		  ret.put("success", success);
		return ret;
	}

    
    /**
     * 验证 验证码是否正确
     * @param mobile 手机号
     * @param verifyCode 验证码
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/player/checkVerifyCode")
    public JSONObject checkVerifyCode(@RequestParam("mobile") String mobile,@RequestParam("verifyCode") String verifyCode) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        String error = null;
        try {
        	Player player = playerService.findPlayerByMobile(mobile, null);
        	if(player == null){
        		error ="手机号码未注册过！";
        	}else{
        		
        		Date forgetPasswordTime = player.getVerifyTime();
        		Calendar toDate = Calendar.getInstance();
                toDate.setTime(forgetPasswordTime);
                toDate.add(Calendar.MINUTE, 10);
                if(toDate.before(new Date())){
                	error ="已超过10分钟，验证码失效！";
                }else if(!verifyCode.equals(player.getVerifyCode())){
                	error ="验证码错误！";
                }else{
                	success = true;
                }
        	}
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", error);
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 修改密码
     * @param mobile
     * @param password
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/player/changePassword")
    public JSONObject changePassword(@RequestParam("mobile") String mobile,@RequestParam("password") String password) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Player player = playerService.findPlayerByMobile(mobile, null);
        	player.setPassword(password);
        	playerService.saveOrUpdate(player);
            success = true;
        } catch (Exception e) {
        	ret.put("error", e.getMessage());
            log.error(e.getMessage(),e.getCause());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 将球员置为 队长
     * @param playerId 球员id
     * @param teamId   球队id
     * @return   {success:true,error:"reason"}
     */
    @RequestMapping(value = "/player/setCaptain")
    public JSONObject changePassword(@RequestParam("playerId") Long playerId,@RequestParam("teamId") Long teamId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	playerService.setCaptain(playerId, teamId);
            ret.put("data",playerService.findById(Player.class, playerId));
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error",e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取某一系统参数, 如 每天给球员点赞次数上限
     * @param codes 代码数组 : code:like_player_max code:like_team_max code:dare_max
     * @return {"data":[{"code":"like_player_max","comment":"每天给球员点赞次数上限","defaultValue":"2","id":1,"value":"5"},{"code":"like_team_max","comment":"每天给球员点赞次数上限","defaultValue":"2","id":2,"value":"5"},{"code":"dare_max","comment":"球队每天约战的次数上限","defaultValue":"2","id":3,"value":"10"}],"success":true}
     */
    @RequestMapping(value = "/sysconfig")
    public JSONObject sysconfg(@RequestParam("code") List<String> codes) {
    	JSONObject ret = new JSONObject();
    	boolean success = false;
        try {
        	List<Sysconfig> item = sysconfigService.findAll(codes);
            ret.put("data",item);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error",e.getMessage());
        }
        ret.put("success", success);
        return ret;
	}
    
    /**
     * 
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/getLikeMax")
    public Map getLikeMax() {
		Properties pros = null;
		try {
			pros = PropertyUtil.getProperties(InitServlet.APPLICATION_URL +"/downstat.properties");
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		}
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("like_player_max", Integer.parseInt(pros.getProperty("like_player_max")));
		map.put("like_team_max", Integer.parseInt(pros.getProperty("like_team_max")));
		return map;
	}
    
    /**
     * 获取球员当天给哪些球员或球队点赞了
     * @param playerId 
     * @return {playerLikes: [{player:{},...}], teamLikes: [{team:{},...}]}
     */
    @RequestMapping(value = "/findLikes")
    public Map findLikes(@RequestParam(value="playerId",required = false)  Long playerId) {
    	return playerService.findLikes(playerId);
	}
    
    /**
     * 获取球队当天约战次数
     * @param teamId
     * @return 球队当天约战次数
     */
    @RequestMapping(value = "/getTodayDareNum")
    public Integer getTodayDareNum(@RequestParam("teamId")  Long teamId) {
    	return gameService.getTodayDareNum(teamId);
	}
    
    /**
     * 给球员点赞
     * @param giverId  登陆用户
     * @param playerId 被点赞的球员
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/likePlayer")
    public JSONObject likePlayer(@RequestParam("giverId") Long giverId,@RequestParam("playerId") Long playerId) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	  Player giver = playerService.findById(Player.class, giverId);
        	  Player player  = playerService.findById(Player.class, playerId);
        	 PlayerLike like = new PlayerLike();
        	  like.setGiver(giver);
        	  like.setPlayer(player);
        	  like.setCreateTime(new Date());
        	  like.setStatus(1);
              playerService.savePlayerLike(like);
             success = true;
         } catch (Exception e) {
             log.error(e.getMessage(),e.getCause());
             
         }
         ret.put("success", success);
         return ret;
	}
    
    /**
     * 给球队点赞
     * @param giverId 登陆用户
     * @param teamId 被点赞的球队
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/likeTeam")
    public JSONObject likeTeam(@RequestParam("giverId") Long giverId,@RequestParam("teamId") Long teamId) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 
        	  Player giver = playerService.findById(Player.class, giverId);
        	  Team team  = teamService.findById(Team.class, teamId);
        	  TeamLike like = new TeamLike();
        	  like.setGiver(giver);
        	  like.setTeam(team);
        	  like.setCreateTime(new Date());
        	  like.setStatus(1);
        	  playerService.saveTeamLike(like);
             success = true;
         } catch (Exception e) {
             log.error(e.getMessage(),e.getCause());
             
         }
         ret.put("success", success);
         return ret;
	}
    
    /**
     * 球员给 照片点赞 
     * @param playerId
     * @param photoGroupId  照片组id
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/likePhoto")
    public JSONObject likePhoto(@RequestParam("playerId") Long playerId,@RequestParam("photoGroupId") Long photoGroupId) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 PhotoGroup pg = photoService.findById(PhotoGroup.class, photoGroupId);
        	 
        	 String likes = pg.getLikes();
			if(StringUtil.isEmpty(likes)){
        		 pg.setLikes(playerId+"");
        	 }else {
        		 String[] list = likes.split(",");
        		if( !Arrays.asList(list).contains(playerId+"")){
        			 pg.setLikes(likes+","+playerId);
        		};  
        	 }
	     	 photoService.saveOrUpdate(pg);
	     	 ret.put("data",pg);
             success = true;
         } catch (Exception e) {
             log.error(e.getMessage(),e.getCause());
             
         }
         ret.put("success", success);
         return ret;
	}
    
    /**
     * 给视频点赞
     * @param playerId
     * @param videoId 视频id
     * @return  {success:true,error:"reason"}
     */
    @RequestMapping(value = "/likeVideo")
    public JSONObject likeVideo(@RequestParam("playerId") Long playerId,@RequestParam("videoId") Long videoId) {
    	 JSONObject ret = new JSONObject();
         boolean success = false;
         try {
        	 Video video = videoService.findById(Video.class, videoId);
        	 String likes = video.getLikes();
			if(StringUtil.isEmpty(likes)){
        		 video.setLikes(playerId+"");
        	 }else{
        		 String[] list = likes.split(",");
         		if( !Arrays.asList(list).contains(playerId+"")){
         			video.setLikes(likes+","+playerId);
         		};  
        	 }
        	 videoService.saveOrUpdate(	video);
        	 ret.put("data",video);
             success = true;
         } catch (Exception e) {
             log.error(e.getMessage(),e.getCause());
             
         }
         ret.put("success", success);
         return ret;
	}
    
    /**
     * 由id获取照片组详情
     * @param id 照片组id
     * @return {"data":{"auditStatus":1,"comment":"上传了两张图片","createTime":1460311964000,"id":12,"isEnabled":1,"likes":"41,1","pics":[{"groupId":12,"id":17,"name":"bg_team_member_picture.jpg","status":1,"url":"/picture/playerImg\\260x110-20160411021205.jpg"},{"groupId":12,"id":18,"name":"icon_personhead_04_24.png","status":1,"url":"/picture/playerImg\\24x24-20160411021209.png"}],"player":{"account":"song040","address":"巴西","attendTimes":0,"auditStatus":1,"auditTime":1463893118000,"birth":661622400000,"createTime":1463886480000,"creater":1,"email":"sdy888@163.com","even":0,"gender":2,"height":174,"iconUrl":"/picture/playerImg/268x335-20160522110803.jpg","id":41,"idno":"610125199012204324","isCaptain":2,"likeNum":0,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585789674","modifyTime":1463893118000,"name":"沃尔姆","nickname":"沃尔姆","password":"111111","playerStatus":1,"point":13,"pointMinus":0,"position":3,"team":{"coverUrl":"/picture/teamImg/220x176-20160522100138.jpg","createTime":1463882400000,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x554-20160522100053.jpg","id":7,"introduce":"托特纳姆热刺的前身“热刺足球俱乐部”（Hotspur Football Club）是在1882年由一群来自“圣约翰长老派教徒学\n托特纳姆热刺足球俱乐部全家福\n托特纳姆热刺足球俱乐部全家福\n校”（St. John's Presbyterian School）及“托特纳姆文法学校”（Tottenham Grammar School）旧生的板球员所成立的。由于在冬季时没有板球比赛，他们便商议成立一支足球队以维持体能状态","kind":1,"likeNum":3,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463882512000,"point":2,"region":"英格兰","registTime":1463882460000,"teamTitle":"托特纳姆热刺","teamType":11,"total":1,"win":1},"total":1,"uniformNumber":8,"weight":77,"win":1},"viewTimes":11,"viewType":1},"success":true}
     */
    @RequestMapping(value = "/photo/{id}", method = RequestMethod.GET)
    public JSONObject photo(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            PhotoGroup one = photoService.findById(PhotoGroup.class, id);
			if(one.getViewTimes()==null){
				one.setViewTimes(1);
			}else{
				one.setViewTimes(one.getViewTimes()+1);
			}
			photoService.saveOrUpdate(one);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 由 id 获取 入队申请详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/apply/{id}", method = RequestMethod.GET)
    public JSONObject apply(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Apply one = applyService.findById(Apply.class, id);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 由id获取视频详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public JSONObject video(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Video one = videoService.findById(Video.class, id);

			if(one.getViewTimes()==null){
				one.setViewTimes(1);
			}else{
				one.setViewTimes(one.getViewTimes()+1);
			}
			if(StringUtil.isEmpty(one.getScreenshot())){
				videoService.saveVideo(one);
			}
			videoService.saveOrUpdate(one);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取球员详情
     * @param id 球员id
     * @return
     */
    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public JSONObject player(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Player one = playerService.findPlayerById(id);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取球队详情
     * @param id 球队id
     * @return
     */
    @RequestMapping(value = "/team/{id}", method = RequestMethod.GET)
    public JSONObject team(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Team one = teamService.findTeamById(id);
            ret.put("data", one);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取赛场详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/field/{id}", method = RequestMethod.GET)
    public JSONObject field(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Field field = fieldService.findById(Field.class,id);
            ret.put("data", field);
            success = true;
        } catch (Exception e) {
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取比赛详情
     * @param id 比赛id
     * @return
     */
    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET)
    public JSONObject game(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Game game = gameService.findById(Game.class,id);
            ret.put("data", game);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        	ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
  /**
   * 被挑战球队回应挑战
   * @param gameId 挑战id.
   * @param operation 被挑战球队回应 . 1为接受挑战 2为拒绝挑战
   * @param teamBId 被挑战的球队(如果是广场挑战,则此字段不为空)
   * @return  {success:true,error:"reason"}
   */
    @RequestMapping(value = "/respondDare", method = RequestMethod.GET)
    public JSONObject respondDare(@RequestParam("gameId") Long gameId,@RequestParam("operation") Integer operation
    		,@RequestParam(value="teamBId",required = false) Long teamBId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Game game = gameService.findById(Game.class,gameId);
            game.setTeamBOperation(operation);
            if(teamBId!=null){
            	Team teamB = teamService.findById(Team.class, teamBId);
            	if(teamB==null){
            		throw new Exception(teamBId+"对应球队不存在！");
				}else if(!teamB.getTeamType().equals(game.getTeamA().getTeamType())){
					throw new Exception("赛制不一致,请选择其它约战！");
				}else if(teamB.getPlayerNum()<game.getTeamType()){
					throw new Exception("当前球队人数不够"+game.getTeamType()+"人,请先招募球员！");
				}
            	game.setTeamB(teamB);
            }
            gameService.saveOrUpdate(game);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        	ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 录入球队比分 
     * @param gameId 球赛id
     * @param teamId 待上传比分球队id
     * @param scoreA 挑战方球队比分	
     * @param scoreB 应战方球队比分	
     * @return {success:true/false,error:""}
     */
    @RequestMapping(value = "/game/inputScore", method = RequestMethod.GET)
    public JSONObject inputScore(@RequestParam("gameId") Long gameId,
    		@RequestParam("teamId") Long teamId,
    		@RequestParam("scoreA") Integer scoreA,
    		@RequestParam("scoreB") Integer scoreB) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            Game game = gameService.findById(Game.class,gameId);
            if(teamId.equals(game.getTeamA().getId())){
        	   game.setScoreA1(scoreA);
               game.setScoreB1(scoreB);
            }else{
            	game.setScoreA2(scoreA);
                game.setScoreB2(scoreB);
            }
            Integer scoreA1 = game.getScoreA1();
            Integer scoreB1 = game.getScoreB1();
            
            if(scoreA1 !=null && scoreA1.equals(game.getScoreA2()) && scoreB1!=null && scoreB1.equals(game.getScoreB2()) ){
         	   game.setScoreA(scoreA);
         	   game.setScoreB(scoreB);
         	   game.setAuditStatus(1);
            }else{
         	   game.setScoreA(null);
         	   game.setScoreB(null);
         	   game.setAuditStatus(null);
            }
            game.setModifyTime(new Timestamp(new Date().getTime()));
            gameService.saveOrUpdate(game);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        	ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 获取teamId指定球队未上传比分的比赛
     * @param teamId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/game/toSocreGames", method = RequestMethod.GET)
   	public JSONObject toSocreGames(@RequestParam("teamId") Long teamId) throws IOException {
    	JSONObject ret = new JSONObject();
   		boolean success = false;
   		try {
   			ret.put("data",gameService.toSocreGames(teamId));
   			success = true;
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   		}
   		ret.put("success", success);
   		return ret;
   	}
    
    @RequestMapping(value = "/delPhoto", method = RequestMethod.GET)
   	public JSONObject delPhoto(@RequestParam("ids") Long[] ids) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = true;
   		try {
   			photoService.del(PhotoGroup.class,Arrays.asList(ids));
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   			success = false;
   		}
   		ret.put("success", success);
   		return ret;
   	}
    
    @RequestMapping(value = "/delVideo", method = RequestMethod.GET)
   	public JSONObject delVideo(@RequestParam("ids") Long[] ids) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = true;
   		try {
   			videoService.del(Video.class, Arrays.asList(ids));
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   			success = false;
   		}
   		ret.put("success", success);
   		return ret;
   	}
    
    @RequestMapping(value = "/delApply", method = RequestMethod.GET)
   	public JSONObject delApply(@RequestParam("ids") Long[] ids) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = true;
   		try {
   			applyService.del(Apply.class,Arrays.asList(ids));
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   			success = false;
   		}
   		ret.put("success", success);
   		return ret;
   	}
    @RequestMapping(value = "/delRecruit", method = RequestMethod.GET)
   	public JSONObject delRecruit(@RequestParam("ids") Long[] ids) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = true;
   		try {
   			recruitService.del(Recruit.class,Arrays.asList(ids));
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   			success = false;
   		}
   		ret.put("success", success);
   		return ret;
   	}

	@RequestMapping(value = "/delScore", method = RequestMethod.GET)
	public JSONObject delScore(@RequestParam("teamId") Long teamId,@RequestParam("ids") Long[] ids) throws IOException {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			gameService.delScore(teamId, ids);
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("error", e.getMessage());
		}
		ret.put("success", success);
		return ret;
	}

    @RequestMapping(value = "/delDare", method = RequestMethod.GET)
   	public JSONObject
	delDare(@RequestParam("id") Long id) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = true;
   		try {
   			gameService.del(Game.class, id);
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());
   			success = false;
   		}
   		ret.put("success", success);
   		return ret;
   	}
    
    /**
     * 删除控制站内信/签到信
     * @param ids
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/delMessage", method = RequestMethod.GET)
   	public JSONObject delMessage(@RequestParam("ids") Long[] ids) throws IOException {
   		JSONObject ret = new JSONObject();
   		boolean success = false;
   		try {
   			messageService.del(Message.class,Arrays.asList(ids));
			success = true;
   		} catch (Exception e) {
   			log.error(e.getMessage(),e.getCause());
   			ret.put("error", e.getMessage());

   		}
   		ret.put("success", success);
   		return ret;
   	}
    
    
    /**
     * 显示球员列表 
     * @param search {orderby: "createTime desc", teamType: 0, snippet: "", currentPage: 1, pageSize: 20}
     * 按人气排序: orderby:"likeNumLastWeek desc"
     * 按身价: orderby:"value desc"
     * 按等级: orderby:"point desc"
     * @return {currentPage:1
				list:[{account: "feng1", attendTimes: 0, auditStatus: 1, createTime: 1484497547000, creater: 1,…},…]
				pageSize:20
				totalPage:4
				totalRecord:70}
     */
    @RequestMapping(value = "/listPlayer", method = RequestMethod.POST)
    public PageResult<Player> listPlayer(@RequestBody Search search) {
        PageResult<Player> ret = null;
        try {
            ret = playerService.findByPage(search);
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        return ret;
    }
    
    /**
     * 获取球队的成员 
     * @param teamId 
     * @param isCaptain 是否队长 1是 2否 ,非必填
     * @return {success:true,data:[{},{}]}
     */
    @RequestMapping(value = "/findPlayersByTeam", method = RequestMethod.GET)
    public JSONObject findPlayersByTeam(@RequestParam("teamId") Long teamId,@RequestParam(value="isCaptain",required = false) Boolean isCaptain) {
        JSONObject ret = new JSONObject();
    	boolean success = false;
        try {
            ret.put("data",playerService.findPlayersByTeam(teamId,isCaptain));
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    
    /**
     * 显示球队列表 
     * @param search {orderby: "createTime desc", teamType: 0, snippet: "", currentPage: 1, pageSize: 20,condition:" and ..."}
     * 按人气排序: orderby:"likeNumLastWeek desc"
     * 按身价: orderby:"value desc"
     * 按等级: orderby:"point desc"
     * 需要加上查询条件: condition:" and (u.dismissed is null or u.dismissed !=1)" , 会过滤掉已解散的球队.
     * @return {currentPage:1
				list:[]
				pageSize:20
				totalPage:4
				totalRecord:70}
     */
    @RequestMapping(value = "/listTeam", method = RequestMethod.POST)
    public PageResult<Team> listTeam(@RequestBody Search search) {
        PageResult<Team> ret = null;
        try {
            ret = teamService.findByPage(search);
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        return ret;
    }
    
    /**
     * 获取评论列表,有分页
     * @param search {type: 1,id:1,currentPage:1}  type取值为 1: 图片 2:视频 3.公告, 4 约战 5找队 6 招人
     * id: 被评论主体的id
     * @return
     */
    @RequestMapping(value = "/listComment", method = RequestMethod.POST)
	public PageResult<Comment> listComment(@RequestBody Search search) {
		PageResult<Comment> ret = null;
		try {
			ret = commentService.findByPage(search);
		} catch (Exception e) {
			
		} return ret;
	}
    
    /**
     * 获取球场列表(有分页)
     * @param search {orderby: " opTime desc", snippet: "", currentPage: 1}
     * @return {"currentPage":1,"list":[{"address":"上海市嘉定区伯纳乌1号","adminId":4,"contact":"C罗","content":"<p>欢迎光临上海市嘉定区伯纳乌球场。</p>","id":5,"mobile":"18616661888","name":"伯纳乌球场","op":1,"opTime":1476595142000,"url":"{\"1\":\"/formalPic/field/20161016115410387-64x36.jpg\",\"2\":\"/formalPic/field/20161016115410387-533x300.jpg\",\"3\":\"/formalPic/field/20161016115410387-533x300.jpg\"}"},{"address":"上海市黄浦区子洲路1888弄38号","contact":"梅西","content":"欢迎和梅西一起踢球。","id":9,"mobile":"021-69577777","name":"诺坎普球场","op":1,"opTime":1463651310000,"url":"/picture/fieldImg/1919x1080-20160519174743.jpg"},{"address":"上海市虹口区老特拉福德路88号188","contact":"弗格森","content":"欢迎光临上海市虹口区老特拉福德路88号188老特拉福德球场。","id":8,"mobile":"13717179977","name":"老特拉福德球场","op":1,"opTime":1463651259000,"url":"/picture/fieldImg/902x605-20160519174651.jpg"},{"address":"上海市闵行区海布里18号","contact":"温格","content":"欢迎光临上海市闵行区海布里18号。","id":7,"mobile":"13414143456","name":"海布里球场","op":1,"opTime":1463651208000,"url":"/picture/fieldImg/600x400-20160519174608.jpg"},{"address":"上海市杨浦区光明路1号","contact":"德科","content":"欢迎光临上海市杨浦区光明路1号光明球场。","id":6,"mobile":"13818181199","name":"光明球场","op":1,"opTime":1463651164000,"url":"/picture/fieldImg/533x300-20160519174518.jpg"}],"pageSize":10,"totalPage":1,"totalRecord":5}
     */
	@RequestMapping(value = "/listField", method = RequestMethod.POST)
	public PageResult<Field> listField(@RequestBody Search search) {
		PageResult<Field> ret = null;
		try {
			ret = fieldService.findByPage(search);
		} catch (Exception e) {
			
		} return ret;
	}
	
	/**
     * 获取比赛列表(有分页)
     * 参数: 
     * teamId:比赛中有 teamId 对应球队参加的.
     * type: 比赛的分类(网页上的tab标签分有四种)"gameTypeList":[[0,"全部"],[1,"约战中"],[2,"未开赛"],[3,"已结束"]],
     * 注意: condition 字段 teamBOperation =1 是指 已约战成功的(包含未开赛 与已结束的比赛)
     * @param search {isEnabled: 1, orderby: "beginTime desc", teamType: 3, condition: " and u.teamBOperation =1", snippet: "",teamId:'',type:'', currentPage: 1}
     * @return {"currentPage":1,"list":[{"address":"夺命岛","beginTime":1488348720000,"content":"fefe","createTime":1488175976000,"cup":{"content":"<ol class=\" list-paddingleft-2\" style=\"list-style-type: decimal;\"><li><p>不准打架</p></li><li><p>不准假球</p></li><li><p><img src=\"http://localhost:8050/formalPic/ueditor/20170223171233390-200x200.jpg\"/></p></li></ol>","iconUrl":"{\"1\":\"/formalPic/cup/20161018225610953-64x64.jpg\",\"2\":\"/formalPic/cup/20161018225610953-200x200.jpg\",\"3\":\"/formalPic/cup/20161018225610953-200x200.jpg\"}","id":1,"isPublic":1,"name":"草根杯","op":1,"opTime":1487841162000,"phases":[],"teams":[{"cupId":1,"id":15,"teamId":2,"teamTitle":"巴萨"},{"cupId":1,"id":17,"teamId":4,"teamTitle":"切尔西"},{"cupId":1,"id":19,"teamId":6,"teamTitle":"拜仁"},{"cupId":1,"id":20,"teamId":7,"teamTitle":"托特纳姆热刺"},{"cupId":1,"id":18,"teamId":5,"teamTitle":"国际米兰"},{"cupId":1,"id":16,"teamId":3,"teamTitle":"曼联"},{"cupId":1,"id":21,"teamId":8,"teamTitle":"利物浦"}]},"id":38,"isEnabled":1,"isPublic":2,"teamA":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"teamB":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"teamType":3,"title":"友谊赛"}],"pageSize":10,"totalPage":1,"totalRecord":1}
     */
	@RequestMapping(value = "/listGame", method = RequestMethod.POST)
	public PageResult<Game> listGame(@RequestBody Search search) {
		PageResult<Game> ret = null;
		try {
			ret = gameService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取某球队上传的比分
	 * @param search {teamId: 球队id}
	 * @return
	 */
	@RequestMapping(value = "/listScore", method = RequestMethod.POST)
	public PageResult<Game> listScore(@RequestBody Search search) {
		PageResult<Game> ret = null;
		try {
			ret = gameService.findScoreByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取比赛日志(有分页)
	 * @param search {playerId:1} id为球员id
	 * @return 
	 */
	@RequestMapping(value = "/listGamelog", method = RequestMethod.POST)
	public PageResult<GameLog> listGamelog(@RequestBody Search search) {
		PageResult<GameLog> ret = null;
		try {
			ret = gameService.findGameLogs(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取广场照片列表
	 *  status:"auditStatusList":[[1,"审核通过"],[2,"审核不通过"],[0,"待审核"]],
	 *  viewType :[[1,"广场"],[2,"队内"]]
	 * @param search {isEnabled: 1, orderby: "createTime desc", teamType: 0, status: 1, viewType: 1, currentPage: 1, snippet: ""}
	 * @return {"currentPage":1,"list":[{"auditStatus":1,"comment":"qwqe","createTime":1476379680000,"id":16,"isEnabled":1,"likes":"1","pics":[{"groupId":16,"id":29,"seq":0,"status":1,"url":"{\"1\":\"/temp/20161014012753282-64x48.jpg\",\"2\":\"/temp/20161014012753282-640x480.jpg\",\"3\":\"/temp/20161014012753282-1024x768.jpg\"}"}],"player":{"account":"song001","address":"葡萄牙","attendTimes":0,"auditStatus":1,"auditTime":1463465467000,"auditor":1,"birth":231696000000,"createTime":1463460300000,"email":"sdy888@163.com","even":1,"gender":1,"height":180,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1487237312000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"playerId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"playerId":1}],"iconUrl":"/picture/playerImg/961x1440-20160517124455.jpg","id":1,"idno":"310110197705060410","introduce":"完美主义者","isCaptain":1,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1}}","lost":0,"mobile":"13585918777","modifier":4,"modifyTime":1482159893000,"name":"c罗","nickname":"c罗","password":"111111","playerStatus":1,"point":23,"pointMinus":0,"position":1,"qq":"32797402","team":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"total":2,"uniformNumber":10,"value":2500,"verifyCode":"548596","verifyTime":1481260983000,"wechat":"S13585948777","weight":75,"win":1},"viewType":1},{"auditStatus":1,"comment":"上传了两张图片","createTime":1460311964000,"id":12,"isEnabled":1,"likes":"41,1","pics":[{"groupId":12,"id":18,"name":"icon_personhead_04_24.png","status":1,"url":"/picture/playerImg\\24x24-20160411021209.png"},{"groupId":12,"id":17,"name":"bg_team_member_picture.jpg","status":1,"url":"/picture/playerImg\\260x110-20160411021205.jpg"}],"player":{"account":"song040","address":"巴西","attendTimes":0,"auditStatus":1,"auditTime":1463893118000,"birth":661622400000,"createTime":1463886480000,"creater":1,"email":"sdy888@163.com","even":0,"gender":2,"height":174,"honors":[],"iconUrl":"/picture/playerImg/268x335-20160522110803.jpg","id":41,"idno":"610125199012204324","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585789674","modifyTime":1463893118000,"name":"沃尔姆","nickname":"沃尔姆","password":"111111","playerStatus":1,"point":13,"pointMinus":0,"position":3,"team":{"coverUrl":"/picture/teamImg/220x176-20160522100138.jpg","createTime":1463882400000,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x554-20160522100053.jpg","id":7,"introduce":"托特纳姆热刺的前身“热刺足球俱乐部”（Hotspur Football Club）是在1882年由一群来自“圣约翰长老派教徒学\n托特纳姆热刺足球俱乐部全家福\n托特纳姆热刺足球俱乐部全家福\n校”（St. John's Presbyterian School）及“托特纳姆文法学校”（Tottenham Grammar School）旧生的板球员所成立的。由于在冬季时没有板球比赛，他们便商议成立一支足球队以维持体能状态","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463882512000,"point":16,"region":"英格兰","registTime":1463882460000,"teamTitle":"托特纳姆热刺","teamType":11,"total":1,"value":19800,"win":1},"total":1,"uniformNumber":8,"value":1800,"weight":77,"win":1},"viewTimes":11,"viewType":1},{"auditStatus":1,"comment":"发布到广场发布到广场发布到广场","createTime":1457874612000,"id":10,"isEnabled":1,"likes":"30,30,30,30,30,30,41","pics":[{"groupId":10,"id":14,"name":"0.gif","status":1,"url":"/picture/playerImg\\400x247-20160313211005.gif"}],"player":{"account":"song033","address":"法国","attendTimes":0,"auditStatus":1,"auditTime":1463883054000,"birth":753465600000,"createTime":1463882940000,"creater":1,"email":"sdy888@163.com","even":0,"gender":1,"height":187,"honors":[],"iconUrl":"/picture/playerImg/268x335-20160522100849.jpg","id":34,"idno":"610121199311172857","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13580906787","modifyTime":1463883054000,"name":"维默尔","nickname":"维默尔","password":"111111","playerStatus":1,"point":13,"pointMinus":0,"position":1,"qq":"425879879","team":{"coverUrl":"/picture/teamImg/220x176-20160522100138.jpg","createTime":1463882400000,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x554-20160522100053.jpg","id":7,"introduce":"托特纳姆热刺的前身“热刺足球俱乐部”（Hotspur Football Club）是在1882年由一群来自“圣约翰长老派教徒学\n托特纳姆热刺足球俱乐部全家福\n托特纳姆热刺足球俱乐部全家福\n校”（St. John's Presbyterian School）及“托特纳姆文法学校”（Tottenham Grammar School）旧生的板球员所成立的。由于在冬季时没有板球比赛，他们便商议成立一支足球队以维持体能状态","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463882512000,"point":16,"region":"英格兰","registTime":1463882460000,"teamTitle":"托特纳姆热刺","teamType":11,"total":1,"value":19800,"win":1},"total":1,"uniformNumber":22,"value":1800,"wechat":"uyehd0989","weight":88,"win":1},"viewTimes":27,"viewType":1},{"auditStatus":1,"auditTime":1457874078000,"auditor":1,"comment":"逗比","createTime":1457840160000,"id":9,"isEnabled":1,"likes":"30","modifier":1,"modifyTime":1457874078000,"pics":[{"groupId":9,"id":13,"name":"0.gif","status":1,"url":"/picture/playerImg\\400x247-20160313113546.gif"}],"player":{"account":"song029","address":"西班牙","attendTimes":0,"auditStatus":1,"auditTime":1463845135000,"birth":368899200000,"createTime":1463845020000,"creater":1,"email":"sdy888@163.com","even":0,"gender":1,"height":183,"honors":[],"iconUrl":"/picture/playerImg/268x402-20160521233630.jpg","id":30,"idno":"431381198109106573","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13580989068","modifyTime":1463845135000,"name":"阿隆索","nickname":"阿隆索","password":"111111","playerStatus":1,"point":13,"pointMinus":0,"position":2,"team":{"coverUrl":"/picture/teamImg/220x131-20160521224718.jpg","createTime":1463841900000,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x268-20160521224527.jpg","id":6,"introduce":"德甲豪门拜仁慕尼黑官网上宣布，前巴萨主帅瓜迪奥拉将从下赛季正式担任球队主帅，双方将签订一份为期3年的合同，金牌教练最终花落德甲巨人。","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463842909000,"point":15,"region":"德国","registTime":1463841900000,"teamTitle":"拜仁","teamType":7,"total":1,"value":12600,"win":1},"total":1,"uniformNumber":22,"value":1800,"weight":78,"win":1},"type":1,"viewTimes":4,"viewType":1},{"auditStatus":1,"comment":"sadf","id":18,"isEnabled":1,"pics":[{"groupId":18,"id":31,"status":1,"url":"{\"1\":\"/formalPic/photo/20170219182001559-64x64.jpg\",\"2\":\"/formalPic/photo/20170219182001559-640x640.jpg\",\"3\":\"/formalPic/photo/20170219182001559-800x800.jpg\"}"}],"player":{"account":"song001","address":"葡萄牙","attendTimes":0,"auditStatus":1,"auditTime":1463465467000,"auditor":1,"birth":231696000000,"createTime":1463460300000,"email":"sdy888@163.com","even":1,"gender":1,"height":180,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1487237312000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"playerId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"playerId":1}],"iconUrl":"/picture/playerImg/961x1440-20160517124455.jpg","id":1,"idno":"310110197705060410","introduce":"完美主义者","isCaptain":1,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1}}","lost":0,"mobile":"13585918777","modifier":4,"modifyTime":1482159893000,"name":"c罗","nickname":"c罗","password":"111111","playerStatus":1,"point":23,"pointMinus":0,"position":1,"qq":"32797402","team":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"total":2,"uniformNumber":10,"value":2500,"verifyCode":"548596","verifyTime":1481260983000,"wechat":"S13585948777","weight":75,"win":1},"viewType":1},{"auditStatus":1,"comment":"sadf","id":19,"isEnabled":1,"pics":[{"groupId":19,"id":33,"status":1,"url":"{\"1\":\"/formalPic/photo/20170219182134196-64x64.jpg\",\"2\":\"/formalPic/photo/20170219182134196-640x640.jpg\",\"3\":\"/formalPic/photo/20170219182134196-800x800.jpg\"}"},{"groupId":19,"id":32,"status":1,"url":"{\"1\":\"/formalPic/photo/20170219182131827-64x64.jpg\",\"2\":\"/formalPic/photo/20170219182131827-200x200.jpg\",\"3\":\"/formalPic/photo/20170219182131827-200x200.jpg\"}"}],"player":{"account":"song001","address":"葡萄牙","attendTimes":0,"auditStatus":1,"auditTime":1463465467000,"auditor":1,"birth":231696000000,"createTime":1463460300000,"email":"sdy888@163.com","even":1,"gender":1,"height":180,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1487237312000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"playerId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"playerId":1}],"iconUrl":"/picture/playerImg/961x1440-20160517124455.jpg","id":1,"idno":"310110197705060410","introduce":"完美主义者","isCaptain":1,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1}}","lost":0,"mobile":"13585918777","modifier":4,"modifyTime":1482159893000,"name":"c罗","nickname":"c罗","password":"111111","playerStatus":1,"point":23,"pointMinus":0,"position":1,"qq":"32797402","team":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"total":2,"uniformNumber":10,"value":2500,"verifyCode":"548596","verifyTime":1481260983000,"wechat":"S13585948777","weight":75,"win":1},"viewType":1}],"pageSize":10,"totalPage":1,"totalRecord":6}
	 */
	@RequestMapping(value = "/listPhoto", method = RequestMethod.POST)
	public PageResult<PhotoGroup> listPhoto(@RequestBody Search search) {
		PageResult<PhotoGroup> ret = null;
		try {
			ret = photoService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}

	/**
	 * 获取广场视频列表
	 * 	 *  status:"adsStatusList":[[1,"草稿"],[2,"上线"],[3,"待上线"],[4,"过期"]],
	 *  viewType :[[1,"广场"],[2,"队内"]]
	 * @param search {isEnabled: 1, orderby: "createTime desc", teamType: 0, status: 1, viewType: 1, currentPage: 1, snippet: ""}
	 * @return {"currentPage":1,"list":[{"auditStatus":1,"comment":"123123","commentCount":2,"id":16,"isEnabled":1,"likes":"1","player":{"account":"song001","address":"葡萄牙","attendTimes":0,"auditStatus":1,"auditTime":1463465467000,"auditor":1,"birth":231696000000,"createTime":1463460300000,"email":"sdy888@163.com","even":1,"gender":1,"height":180,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1487237312000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"playerId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"playerId":1}],"iconUrl":"/picture/playerImg/961x1440-20160517124455.jpg","id":1,"idno":"310110197705060410","introduce":"完美主义者","isCaptain":1,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1}}","lost":0,"mobile":"13585918777","modifier":4,"modifyTime":1482159893000,"name":"c罗","nickname":"c罗","password":"111111","playerStatus":1,"point":23,"pointMinus":0,"position":1,"qq":"32797402","team":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"total":2,"uniformNumber":10,"value":2500,"verifyCode":"548596","verifyTime":1481260983000,"wechat":"S13585948777","weight":75,"win":1},"screenshot":"http://r4.ykimg.com/05410608567CAF2C6A0A472C264E1DBA","title":"好看的","videoDiv":"http://player.youku.com/player.php/Type/Folder/Fid/25974427/Ob/1/sid/XMTQyMzM2MDA4NA==/v.swf"},{"auditStatus":1,"comment":"mieiei","commentCount":0,"id":1,"isEnabled":1,"player":{"account":"song001","address":"葡萄牙","attendTimes":0,"auditStatus":1,"auditTime":1463465467000,"auditor":1,"birth":231696000000,"createTime":1463460300000,"email":"sdy888@163.com","even":1,"gender":1,"height":180,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1487237312000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"playerId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"type":1,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"playerId":1}],"iconUrl":"/picture/playerImg/961x1440-20160517124455.jpg","id":1,"idno":"310110197705060410","introduce":"完美主义者","isCaptain":1,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1}}","lost":0,"mobile":"13585918777","modifier":4,"modifyTime":1482159893000,"name":"c罗","nickname":"c罗","password":"111111","playerStatus":1,"point":23,"pointMinus":0,"position":1,"qq":"32797402","team":{"acceptedTimes":3,"chanllegeTimes":3,"createTime":1482159931000,"dismissed":2,"even":1,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":1,"modifyTime":1486975119000,"newsTimes":4,"point":46,"registTime":1482159931000,"teamTitle":"皇马","teamType":3,"total":6,"value":10000,"win":2},"total":2,"uniformNumber":10,"value":2500,"verifyCode":"548596","verifyTime":1481260983000,"wechat":"S13585948777","weight":75,"win":1},"screenshot":"http://r2.ykimg.com/054101015673B7AE6A0A4247368F6FA5","title":"2015最后一场","videoDiv":"<embed src=\"http://player.youku.com/player.php/sid/XMTQxNjg1ODgyNA==/v.swf\" allowFullScreen=\"true\" quality=\"high\" width=\"480\" height=\"400\" align=\"middle\" allowScriptAccess=\"always\" type=\"application/x-shockwave-flash\"></embed>"}],"pageSize":10,"totalPage":1,"totalRecord":2}
	 */
	@RequestMapping(value = "/listVideo", method = RequestMethod.POST)
	public PageResult<Video> listVideo(@RequestBody Search search) {
		PageResult<Video> ret = null;
		try {
			ret = videoService.findByPage(search);
		} catch (Exception e) {
			
		} return ret;
	}
	
	/**
	 * 获取球招人列表/邀我入队.  
	 *  teamId 不为空时，表示 获取球队招人。 playerId不为空时， 获取“邀我入队”
	 * @param search {teamId：,playerId:,isEnabled: 1, orderby: "opTime desc", teamType: 7, snippet: "", isPublic: 1, currentPage: 1}
	 * @return {"currentPage":1,"list":[{"confirmStatus":1,"confirmTime":1484496992000,"id":10,"isEnabled":1,"isPublic":2,"opTime":1483845167000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"从球员详情页面邀请入队"},{"id":9,"isEnabled":1,"isPublic":2,"opTime":1483374399000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"从球员详情页面邀请入队"},{"content":"456789","id":8,"isEnabled":1,"isPublic":1,"opTime":1483339858000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"巴萨球队招人"},{"confirmStatus":1,"confirmTime":1481731819000,"content":"要你来, 你来不?","id":7,"isEnabled":1,"isPublic":2,"player":{"account":"song100","attendTimes":2,"auditStatus":1,"auditTime":1469501549000,"birth":231724800000,"createTime":1469250060000,"email":"1832533317@qq.com","gender":1,"height":177,"honors":[],"iconUrl":"{\"3\":\"/formalPic/player/20170111225123437-379x379.jpg\",\"2\":\"/formalPic/player/20170111225123437-379x379.jpg\",\"1\":\"/formalPic/player/20170111225123437-64x64.jpg\"}","id":68,"idno":"310110197705060437","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"mobile":"13586235987","modifier":1,"modifyTime":1482597093000,"name":"宋老师","nickname":"魔力鸟","password":"111111","playerStatus":1,"point":11,"pointMinus":0,"position":3,"team":{"createTime":1484144481000,"dismissed":2,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20170111222122400-621x621.jpg\",\"2\":\"/formalPic/team/20170111222122400-621x621.jpg\",\"1\":\"/formalPic/team/20170111222122400-64x64.jpg\"}","id":25,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"modifier":1,"modifyTime":1484723618000,"point":0,"registTime":1484144481000,"teamTitle":"加斯科因","teamType":3,"value":2600},"uniformNumber":18,"value":1000,"weight":67},"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"招人呀"}],"pageSize":10,"totalPage":1,"totalRecord":4}
	 */
	@RequestMapping(value = "/listRecruit", method = RequestMethod.POST)
	public PageResult<Recruit> listRecruit(@RequestBody Search search) {
		PageResult<Recruit> ret = null;
		try {
			ret = recruitService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		} return ret;
	}
	
	
	/**
	 * 获取广场申请入队列表
	 * @param search {isEnabled: 1, orderby: "applyTime desc", currentPage: 1, teamType: 0, snippet: "", isPublic: 1,isOpen:"1"} isOpen表示仍有效的(还在申请中)
	 * @return 
	 */
	@RequestMapping(value = "/listApply", method = RequestMethod.POST)
	public PageResult<Apply> listApply(@RequestBody Search search) {
		PageResult<Apply> ret = null;
		try {
			ret = applyService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		} return ret;
	}
	
	/**
	 * 获取球员申请入队的 申请记录 isEnabled 为1 指没被管理员禁掉的.
	 *  teamId 不为空时，表示 获取"入队申请"。 playerId不为空时， 获取“我的申请”
	 * @param search {isEnabled: 1, currentPage: 1, pageSize: 10, playerId: 3, teamId：}
	 * @return  {"currentPage":1,"list":[{"apply":{"applyTime":1483375797000,"id":19,"isEnabled":1,"isOpen":1,"isPublic":2,"title":"从球队详情页面申请入队"},"id":14,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4}}],"pageSize":10,"totalPage":1,"totalRecord":1}
	 */
	@RequestMapping(value = "/listApplyTank", method = RequestMethod.POST)
	public PageResult<ApplyTankB> listApplyTank(@RequestBody Search search) {
		PageResult<ApplyTankB> ret = null;
		try {
			ret = applyService.findTanksByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取发布的站内信 和签到信
	 * teamId:发布的球队
	 * type:"messageTypeList":[[1,"普通信"],[2,"签到信"]],
	 * {isEnabled: 1, currentPage: 1, pageSize: 10, teamId: 2, type: 2, orderby: "opTime desc"}
	 * @param search
	 * @return {"currentPage":1,"list":[{"address":"详细地址","beginTime":1475675460000,"content":"sdfsdf","id":7,"isEnabled":1,"messageType":2,"opTime":1475589086000,"tanks":[{"id":5,"messageId":7,"player":{"account":"song004","address":"阿根廷","attendTimes":1,"auditStatus":1,"auditTime":1463474838000,"auditor":1,"birth":-220348800000,"createTime":1463470800000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":176,"iconUrl":"/picture/playerImg/1600x1148-20160517153947.jpg","id":4,"idno":"310106196301081611","introduce":"我的地盘我做主","isCaptain":1,"likeNum":0,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585874562","modifier":1,"modifyTime":1463474838000,"name":"梅西","nickname":"梅西","password":"111111","playerStatus":1,"point":3.3333,"position":1,"qq":"1459637","team":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"total":2,"uniformNumber":10,"wechat":"4586329","weight":68,"win":0}}],"title":"标题"},{"content":"asdfasdf","id":6,"isEnabled":1,"messageType":4,"opTime":1475584708000,"tanks":[],"title":"fe球队招人"},{"content":"123456","id":5,"isEnabled":1,"messageType":4,"opTime":1475584564000,"tanks":[],"title":"fe球队招人"},{"content":"sadfasfasdfasd","id":4,"isEnabled":1,"messageType":4,"opTime":1475584220000,"tanks":[],"title":"fe球队招人"},{"content":"我想找几个队员","id":3,"isEnabled":1,"messageType":4,"opTime":1475579804000,"tanks":[],"title":"fe球队招人"},{"beginTime":1480490692000,"content":"ab","id":2,"isEnabled":1,"messageType":1,"opTime":1475249380000,"tanks":[{"confirmTime":1475593436000,"id":4,"messageId":2,"player":{"account":"song004","address":"阿根廷","attendTimes":1,"auditStatus":1,"auditTime":1463474838000,"auditor":1,"birth":-220348800000,"createTime":1463470800000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":176,"iconUrl":"/picture/playerImg/1600x1148-20160517153947.jpg","id":4,"idno":"310106196301081611","introduce":"我的地盘我做主","isCaptain":1,"likeNum":0,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585874562","modifier":1,"modifyTime":1463474838000,"name":"梅西","nickname":"梅西","password":"111111","playerStatus":1,"point":3.3333,"position":1,"qq":"1459637","team":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"total":2,"uniformNumber":10,"wechat":"4586329","weight":68,"win":0}},{"confirmStatus":2,"confirmTime":1480258039000,"id":3,"messageId":2,"player":{"account":"song003","address":"法国","attendTimes":0,"auditStatus":1,"auditTime":1463541264000,"birth":231696000000,"createTime":1463465880000,"creater":1,"email":"sdy888@163.COM","even":1,"gender":1,"height":182,"iconUrl":"/picture/playerImg/324x324-20160517141732.jpg","id":3,"idno":"310110197705060410","isCaptain":2,"likeNum":9,"likeNumLastWeek":5,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585918778","modifyTime":1463541264000,"name":"本泽马","nickname":"本泽马","password":"111111","point":3.5,"position":2,"team":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1476605483000,"dismissed":2,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477238292000,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"teamId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":2},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1},\"dare\":{\"v\":1},\"accept\":{\"v\":1},\"message\":{\"v\":1},\"recruit\":{\"v\":2}}","lost":0,"modifyTime":1477727848000,"point":4,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":85},"total":2,"uniformNumber":9,"weight":86,"win":1}}],"team":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1476605483000,"dismissed":2,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477238292000,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"teamId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":2},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1},\"dare\":{\"v\":1},\"accept\":{\"v\":1},\"message\":{\"v\":1},\"recruit\":{\"v\":2}}","lost":0,"modifyTime":1477727848000,"point":4,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":85},"title":"a"},{"beginTime":1480577096000,"content":"普通站内信详细信息","id":1,"isEnabled":1,"messageType":1,"tanks":[{"confirmTime":1475593457000,"id":2,"messageId":1,"player":{"account":"song004","address":"阿根廷","attendTimes":1,"auditStatus":1,"auditTime":1463474838000,"auditor":1,"birth":-220348800000,"createTime":1463470800000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":176,"iconUrl":"/picture/playerImg/1600x1148-20160517153947.jpg","id":4,"idno":"310106196301081611","introduce":"我的地盘我做主","isCaptain":1,"likeNum":0,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585874562","modifier":1,"modifyTime":1463474838000,"name":"梅西","nickname":"梅西","password":"111111","playerStatus":1,"point":3.3333,"position":1,"qq":"1459637","team":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"total":2,"uniformNumber":10,"wechat":"4586329","weight":68,"win":0}},{"confirmTime":1480225866000,"id":1,"messageId":1,"player":{"account":"song003","address":"法国","attendTimes":0,"auditStatus":1,"auditTime":1463541264000,"birth":231696000000,"createTime":1463465880000,"creater":1,"email":"sdy888@163.COM","even":1,"gender":1,"height":182,"iconUrl":"/picture/playerImg/324x324-20160517141732.jpg","id":3,"idno":"310110197705060410","isCaptain":2,"likeNum":9,"likeNumLastWeek":5,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585918778","modifyTime":1463541264000,"name":"本泽马","nickname":"本泽马","password":"111111","point":3.5,"position":2,"team":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1476605483000,"dismissed":2,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477238292000,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"teamId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":2},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1},\"dare\":{\"v\":1},\"accept\":{\"v\":1},\"message\":{\"v\":1},\"recruit\":{\"v\":2}}","lost":0,"modifyTime":1477727848000,"point":4,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":85},"total":2,"uniformNumber":9,"weight":86,"win":1}}],"team":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1476605483000,"dismissed":2,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477238292000,"url":"{\"1\":\"/formalPic/honor/20161023235811452-64x64.jpg\",\"2\":\"/formalPic/honor/20161023235811452-220x220.jpg\",\"3\":\"/formalPic/honor/20161023235811452-220x220.jpg\"}"},"id":14,"teamId":1},{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477238305000,"url":"{\"1\":\"/formalPic/honor/20161023235823643-64x48.jpg\",\"2\":\"/formalPic/honor/20161023235823643-289x220.jpg\",\"3\":\"/formalPic/honor/20161023235823643-289x220.jpg\"}"},"id":13,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":2},\"video\":{\"v\":1},\"apply\":{\"v\":1},\"comment\":{\"v\":1},\"dare\":{\"v\":1},\"accept\":{\"v\":1},\"message\":{\"v\":1},\"recruit\":{\"v\":2}}","lost":0,"modifyTime":1477727848000,"point":4,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":85},"title":"普通站内信"}],"pageSize":10,"totalPage":1,"totalRecord":7}
	 */
	@RequestMapping(value = "/listMessage", method = RequestMethod.POST)
	public PageResult<Message> listMessage(@RequestBody Search search) {
		PageResult<Message> ret = null;
		try {
			ret = messageService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取所有发给我的签到信 
	 * @param search {isEnabled: 1, currentPage: 1, pageSize: 10, playerId: 3}
	 * @return {"currentPage":1,"list":[{"auditStatus":1,"auditTime":1486727455000,"confirmStatus":1,"confirmTime":1486206754000,"id":13,"message":{"address":"随机地址","beginTime":1486205580000,"content":"到武汉光谷球场参加活动","id":10,"isEnabled":1,"messageType":2,"opTime":1486104846000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"签到测试0203"},"player":{"account":"song006","address":"德国","attendTimes":1,"auditStatus":1,"auditTime":1463475297000,"birth":-220320000000,"createTime":1463475120000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":178,"honors":[],"iconUrl":"{\"3\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"2\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"1\":\"/formalPic/player/20170204203019038-64x60.jpg\"}","id":6,"idno":"310106196301081611","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585918463","modifier":1,"modifyTime":1486211310000,"name":"苏亚雷斯","nickname":"苏亚雷斯","password":"111111","playerStatus":1,"point":25,"pointMinus":0,"position":4,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"total":2,"uniformNumber":6,"value":2300,"weight":78,"win":0}},{"confirmStatus":5,"id":10,"message":{"address":"上当受骗","beginTime":1486179420000,"content":"上当受骗","id":9,"isEnabled":1,"messageType":2,"opTime":1483328229000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"上当受骗"},"player":{"account":"song006","address":"德国","attendTimes":1,"auditStatus":1,"auditTime":1463475297000,"birth":-220320000000,"createTime":1463475120000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":178,"honors":[],"iconUrl":"{\"3\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"2\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"1\":\"/formalPic/player/20170204203019038-64x60.jpg\"}","id":6,"idno":"310106196301081611","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585918463","modifier":1,"modifyTime":1486211310000,"name":"苏亚雷斯","nickname":"苏亚雷斯","password":"111111","playerStatus":1,"point":25,"pointMinus":0,"position":4,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"total":2,"uniformNumber":6,"value":2300,"weight":78,"win":0}},{"id":6,"message":{"content":"站内信","id":8,"isEnabled":1,"messageType":1,"opTime":1483326969000,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"title":"站内信"},"player":{"account":"song006","address":"德国","attendTimes":1,"auditStatus":1,"auditTime":1463475297000,"birth":-220320000000,"createTime":1463475120000,"creater":1,"email":"sdy888@163.com","even":1,"gender":1,"height":178,"honors":[],"iconUrl":"{\"3\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"2\":\"/formalPic/player/20170204203019038-336x319.jpg\",\"1\":\"/formalPic/player/20170204203019038-64x60.jpg\"}","id":6,"idno":"310106196301081611","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13585918463","modifier":1,"modifyTime":1486211310000,"name":"苏亚雷斯","nickname":"苏亚雷斯","password":"111111","playerStatus":1,"point":25,"pointMinus":0,"position":4,"team":{"acceptedTimes":2,"chanllegeTimes":2,"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":2,"modifier":1,"modifyTime":1486975295000,"newsTimes":4,"official":1,"point":95,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":7,"value":8700,"win":4},"total":2,"uniformNumber":6,"value":2300,"weight":78,"win":0}}],"pageSize":10,"totalPage":1,"totalRecord":3}
	 */
	@RequestMapping(value = "/listMessageTank", method = RequestMethod.POST)
	public PageResult<MessageTankB> listMessageTank(@RequestBody Search search) {
		PageResult<MessageTankB> ret = null;
		try {
			ret = messageService.findTanksByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 签到与否
	 * @param tankId 签到信 tank 的id
	 * @param confirmStatus  1:签到 2,不到
	 * @return
	 */
	@RequestMapping(value = "/signIn", method = RequestMethod.GET)
	public JSONObject signIn(@RequestParam("tankId") Long tankId,@RequestParam("confirmStatus") Integer confirmStatus	) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			MessageTankB tank = messageService.findMessageTankB(tankId);
			tank.setConfirmStatus(confirmStatus);
			tank.setConfirmTime(new Timestamp(new Date().getTime()));
			messageService.updateTank(tank);
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} 
		ret.put("success", success);
		return ret;
	}
	
	/**
	 * 队长审核球员   签到与否
	 * @param tankId 签到信 tank 的id
	 * @param auditStatus  1:通过  2,不通过
	 * @return
	 */
	@RequestMapping(value = "/signInAudit", method = RequestMethod.GET)
	public JSONObject signInAudit(@RequestParam("tankId") Long tankId,@RequestParam("auditStatus") Integer auditStatus	) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			MessageTankB tank = messageService.findMessageTankB(tankId);
			tank.setAuditStatus(auditStatus);
			tank.setAuditTime(new Timestamp(new Date().getTime()));
			messageService.updateTank(tank);
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		} 
		ret.put("success", success);
		return ret;
	}
	
	/**
	 * 确认加入或者拒绝某个申请的球队
	 * 
	 * @param tankId 一次有效的入队申请记录包含若干个子记录(tank),子记录是对不同球队的申请,tankId为子记录的id.
	 * @param confirmStatus 为1时,加入球队, 2 放弃加入.
	 * @return
	 */
	@RequestMapping(value = "/confirmApply", method = RequestMethod.GET)
	public JSONObject confirmApply(@RequestParam("tankId") Long tankId,@RequestParam("confirmStatus") Integer confirmStatus	) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			applyService.confirm(tankId,confirmStatus);
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} 
		ret.put("success", success);
		return ret;
	}
	
	 /**
	  * 
	  * 确认加入或者拒绝某个邀请我入队的球队
	  * @param recruitId  入队邀请信id
	  * @param confirmStatus  confirmStatus 为1时,加入球队, 2 放弃加入.
	  * @return
	  */
		@RequestMapping(value = "/confirmRecruit", method = RequestMethod.GET)
		public JSONObject confirmRecruit(@RequestParam("recruitId") Long recruitId,@RequestParam("confirmStatus") Integer confirmStatus	) {
			JSONObject ret = new JSONObject();
			boolean success = false;
			try {
				recruitService.confirm(recruitId,confirmStatus);
				success = true;
			} catch (Exception e) {
				log.error(e.getMessage(),e.getCause());
			} 
			ret.put("success", success);
			return ret;
		}
	
	
		
	
	/**
	 * 球队确认某球员加入或者拒绝某个申请	
	 * @param tankId  申请tank记录
	 * @param auditStatus 审核  1:通过 2 拒绝
	 * @param captainId 队长id
	 * @return
	 */
	@RequestMapping(value = "/audit", method = RequestMethod.GET)
	public JSONObject audit(@RequestParam("tankId") Long tankId,@RequestParam("auditStatus") Integer auditStatus,@RequestParam("captainId") Long captainId	) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			ret.put("message", applyService.audit(tankId,auditStatus,captainId));
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} 
		ret.put("success", success);
		return ret;
	}
	
	/** 
	 * 儿获系统公告列表
	 * status:"adsStatusList":[[1,"草稿"],[2,"上线"],[3,"待上线"],[4,"过期"]],
	 * {status: 2, orderby: "startTime desc", snippet: "", currentPage: 1}
	 * @param search
	 * @return {"currentPage":1,"list":[{"content":"3","createTime":1454668005000,"creater":1,"id":22,"startTime":1455877560000,"status":2,"title":"3"},{"content":"<p>想找一支11人制的球队，等级为5级，经常在上海闸北区一带活动，有诚意者，请联系我，谢谢，我收到信息一定会回复！</p>","createTime":1450774740000,"creater":1,"id":12,"modifier":1,"modifyTime":1456155201000,"startTime":1450256340000,"status":2,"title":"寻找一只靠谱的球队"},{"content":"asdfasdsfdafsda","createTime":1451268263000,"creater":1,"id":14,"startTime":1450058650000,"status":2,"title":"1111"},{"content":"寻找一名球员，踢3人制的球队，请联系我，谢谢！","createTime":1450034400000,"creater":1,"id":10,"modifier":1,"modifyTime":1456154935000,"startTime":1449084000000,"status":2,"title":"寻找一名球员，踢3人制"}],"pageSize":10,"totalPage":1,"totalRecord":4}
	 */
	@RequestMapping(value = "/listNews", method = RequestMethod.POST)
	public PageResult<News> listNews(@RequestBody Search search) {
		PageResult<News> ret = null;
		try {
			ret = newsService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取转会的列表 
	 * @param search  {orderby: "toTime desc", pageSize: 5, teamType: 0, snippet: "", currentPage: 1}
	 * snippet 指球员或球队的名称模糊搜索. teamType为0时指全部
	 * @return {"currentPage":1,"list":[{"createTime":1484496959000,"id":2,"toTeam":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNum":3,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"toTime":1484496959000},{"createTime":1481731819000,"fromTeam":{"coverUrl":"/picture/teamImg/352x220-20160518131035.jpg","createTime":1463542380000,"honors":[],"iconUrl":"/picture/teamImg/1010x1024-20160518113351.png","id":3,"kind":1,"likeNum":2,"likeNumLastWeek":8,"likeNumWeekBefLast":9,"modifier":1,"modifyTime":1463814793000,"point":1,"region":"英国","registTime":1463542380000,"teamTitle":"曼联","teamType":5},"fromTime":1481731819000,"id":1,"player":{"account":"song100","attendTimes":0,"auditStatus":1,"auditTime":1469501549000,"birth":231696000000,"createTime":1469250060000,"email":"1832533317@qq.com","gender":1,"height":177,"iconUrl":"/picture/playerImg/570x341-20160723130102.jpg","id":68,"idno":"310110197705060437","isCaptain":2,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"locks":"{\"login\":{\"v\":1},\"photo\":{\"v\":1},\"video\":{\"v\":1},\"apply\":{\"v\":2,\"fromDate\":\"2016-09-28\",\"toDate\":\"2016-11-05\",\"reason\":\"5\"},\"comment\":{\"v\":1}}","mobile":"13586235987","modifier":4,"modifyTime":1477659283000,"name":"宋老师","nickname":"魔力鸟","password":"111111","playerStatus":1,"point":1,"pointMinus":0,"position":3,"team":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNum":3,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"uniformNumber":18,"weight":67},"toTeam":{"coverUrl":"/picture/teamImg/600x375-20160517160324.jpg","createTime":1463471220000,"dismissed":2,"even":1,"honors":[],"iconUrl":"/picture/teamImg/260x220-20160517154642.jpg","id":2,"introduce":"<p>世界上最好的球队。</p>","kind":2,"likeNum":3,"likeNumLastWeek":9,"likeNumWeekBefLast":7,"lost":1,"modifier":4,"modifyTime":1476614255000,"official":1,"point":3,"region":"上海","registTime":1463471220000,"teamTitle":"巴萨","teamType":3,"total":2,"win":0},"toTime":1481731819000}],"pageSize":5,"totalPage":1,"totalRecord":2}
	 */
	@RequestMapping(value = "/listTransfer", method = RequestMethod.POST)
	public PageResult<TransferLog> listTransfer(@RequestBody Search search) {
		PageResult<TransferLog> ret = null;
		try {
			ret = transferService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			
		} return ret;
	}
	
	/**
	 * 获取某只球队  的球员加入与退出 情况
	 * @param search {currentPage: 1, pageSize: 10, teamId: 19}
	 * @return {"currentPage":1,"list":[{"createTime":1482573250000,"fromTeam":{"coverUrl":"/picture/teamImg/220x165-20160606123254.jpg","createTime":1465186680000,"dismissed":1,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x289-20160606121835.jpg","id":9,"introduce":"<p>上海上港集团足球俱乐部（Shanghai SIPG Football Club）是一家位于中国上海的职业足球俱乐部，现参加中国足球超级联赛，主场设在上海体育场。球队曾获得一次中超联赛亚军，一次中甲联赛冠军，一次中乙联赛冠军，两次全运会冠军（代表上海足球队），五次沪港球会杯冠军等荣誉。</p>","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":4,"modifyTime":1476613113000,"official":1,"region":"上海","registTime":1465186680000,"teamTitle":"上港集团","teamType":11,"total":1,"win":0},"fromTime":1482573250000,"id":103,"player":{"account":"song064","address":"大连","auditStatus":1,"auditTime":1465190846000,"birth":13017600000,"createTime":1465190580000,"creater":1,"email":"sdy888@163.com","even":0,"gender":1,"height":180,"iconUrl":"/picture/playerImg/240x341-20160606132243.jpg","id":65,"idno":"610121197006012858","isCaptain":2,"likeNum":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"mobile":"13122323696","modifyTime":1465190846000,"name":"金周荣","nickname":"小周","password":"111111","playerStatus":1,"position":3,"team":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"total":1,"uniformNumber":14,"weight":75,"win":0},"toTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"toTime":1482573250000},{"createTime":1482572281000,"fromTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"fromTime":1482572281000,"id":102,"player":{"account":"a0002","auditStatus":1,"birth":-1235174400000,"createTime":1481383762000,"email":"sdy8881234@163.com","gender":1,"iconUrl":"{\"3\":\"/formalPic/player/20161210232827415-324x324.jpg\",\"2\":\"/formalPic/player/20161210232827415-324x324.jpg\",\"1\":\"/formalPic/player/20161210232827415-64x64.jpg\"}","id":76,"idno":"31011119301111083X","isCaptain":2,"likeNum":2,"likeNumLastWeek":2,"likeNumWeekBefLast":0,"mobile":"13122323456","modifier":1,"modifyTime":1481384120000,"name":"周铁民","nickname":"周铁民","password":"111111","team":{"coverUrl":"/picture/teamImg/220x165-20160606123254.jpg","createTime":1465186680000,"dismissed":1,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x289-20160606121835.jpg","id":9,"introduce":"<p>上海上港集团足球俱乐部（Shanghai SIPG Football Club）是一家位于中国上海的职业足球俱乐部，现参加中国足球超级联赛，主场设在上海体育场。球队曾获得一次中超联赛亚军，一次中甲联赛冠军，一次中乙联赛冠军，两次全运会冠军（代表上海足球队），五次沪港球会杯冠军等荣誉。</p>","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":4,"modifyTime":1476613113000,"official":1,"region":"上海","registTime":1465186680000,"teamTitle":"上港集团","teamType":11,"total":1,"win":0}},"toTeam":{"coverUrl":"{\"3\":\"/temp/20161218230319302-420x627.jpg\",\"2\":\"/temp/20161218230319302-420x627.jpg\",\"1\":\"/temp/20161218230319302-42x64.jpg\"}","createTime":1482073332000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011113807-160x160.jpg\",\"2\":\"/formalPic/team/20161024011113807-160x160.jpg\",\"1\":\"/formalPic/team/20161024011113807-64x64.jpg\"}","id":18,"introduce":"来踢球","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"region":"泰国","registTime":1482073332000,"teamTitle":"3人球队","teamType":3},"toTime":1482572281000},{"createTime":1482160008000,"id":101,"player":{"account":"a0002","auditStatus":1,"birth":-1235174400000,"createTime":1481383762000,"email":"sdy8881234@163.com","gender":1,"iconUrl":"{\"3\":\"/formalPic/player/20161210232827415-324x324.jpg\",\"2\":\"/formalPic/player/20161210232827415-324x324.jpg\",\"1\":\"/formalPic/player/20161210232827415-64x64.jpg\"}","id":76,"idno":"31011119301111083X","isCaptain":2,"likeNum":2,"likeNumLastWeek":2,"likeNumWeekBefLast":0,"mobile":"13122323456","modifier":1,"modifyTime":1481384120000,"name":"周铁民","nickname":"周铁民","password":"111111","team":{"coverUrl":"/picture/teamImg/220x165-20160606123254.jpg","createTime":1465186680000,"dismissed":1,"even":0,"honors":[],"iconUrl":"/picture/teamImg/268x289-20160606121835.jpg","id":9,"introduce":"<p>上海上港集团足球俱乐部（Shanghai SIPG Football Club）是一家位于中国上海的职业足球俱乐部，现参加中国足球超级联赛，主场设在上海体育场。球队曾获得一次中超联赛亚军，一次中甲联赛冠军，一次中乙联赛冠军，两次全运会冠军（代表上海足球队），五次沪港球会杯冠军等荣誉。</p>","kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":1,"modifier":4,"modifyTime":1476613113000,"official":1,"region":"上海","registTime":1465186680000,"teamTitle":"上港集团","teamType":11,"total":1,"win":0}},"toTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"toTime":1482160008000},{"createTime":1482159992000,"id":100,"player":{"account":"a0003","auditStatus":1,"birth":-1294444800000,"createTime":1481383870000,"email":"sdy888987@163.com","gender":1,"iconUrl":"{\"3\":\"/formalPic/player/20161210233003093-423x423.jpg\",\"2\":\"/formalPic/player/20161210233003093-423x423.jpg\",\"1\":\"/formalPic/player/20161210233003093-64x64.jpg\"}","id":77,"idno":"310111192812250811","isCaptain":2,"likeNum":2,"likeNumLastWeek":2,"likeNumWeekBefLast":0,"mobile":"13122323987","modifier":1,"modifyTime":1481384126000,"name":"张绍启","nickname":"张绍启","password":"111111","team":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3}},"toTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"toTime":1482159992000},{"fromTeam":{"coverUrl":"/picture/teamImg/600x338-20160517151047.jpg","createTime":1463466420000,"dismissTime":1482159790000,"dismissed":1,"dismisser":1,"even":1,"honors":[{"honor":{"content":"<p>最牛球队 没有之一</p>","id":5,"name":"最牛球队","op":1,"opTime":1477242694000,"url":"{\"3\":\"/formalPic/honor/20161024011133552-220x220.jpg\",\"2\":\"/formalPic/honor/20161024011133552-220x220.jpg\",\"1\":\"/formalPic/honor/20161024011133552-64x64.jpg\"}"},"id":13,"teamId":1},{"honor":{"content":"<p>草根杯第一名</p>","id":10,"name":"草根杯第一名","opTime":1477242702000,"url":"{\"3\":\"/formalPic/honor/20161024011141594-268x554.jpg\",\"2\":\"/formalPic/honor/20161024011141594-268x554.jpg\",\"1\":\"/formalPic/honor/20161024011141594-30x64.jpg\"}"},"id":14,"teamId":1}],"iconUrl":"/picture/teamImg/191x220-20160517142717.jpg","id":1,"kind":1,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"modifyTime":1463469540000,"region":"北京","registTime":1463466420000,"teamTitle":"皇马","teamType":3,"total":2,"win":1},"fromTime":1482159790000,"id":99,"modifyTime":1483551626000,"player":{"account":"song003","address":"法国","auditStatus":1,"auditTime":1463541264000,"birth":231696000000,"createTime":1463465880000,"creater":1,"email":"sdy888@163.COM","even":1,"gender":1,"height":182,"iconUrl":"/picture/playerImg/324x324-20160517141732.jpg","id":3,"idno":"310110197705060410","isCaptain":2,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"lost":0,"mobile":"13585918778","modifyTime":1463541264000,"name":"本泽马","nickname":"本泽马","password":"111111","position":2,"team":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"total":2,"uniformNumber":9,"weight":86,"win":1},"toTeam":{"createTime":1482159931000,"honors":[],"iconUrl":"{\"3\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"2\":\"/formalPic/team/20161024011122245-304x350.jpg\",\"1\":\"/formalPic/team/20161024011122245-55x64.jpg\"}","id":19,"likeNumLastWeek":0,"likeNumWeekBefLast":0,"registTime":1482159931000,"teamTitle":"皇马","teamType":3},"toTime":1483551626000}],"pageSize":10,"totalPage":1,"totalRecord":5}
	 */
	@RequestMapping(value = "/listInOut", method = RequestMethod.POST)
	public PageResult<TransferLog> listInOut(@RequestBody Search search) {
		PageResult<TransferLog> ret = null;
		try {
			ret = transferService.findByPage(search);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} return ret;
	}
	
	
	@RequestMapping(value = "/news/{id}", method = RequestMethod.GET)
    public JSONObject news(@PathVariable("id") Long id) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            News news = newsService.findById(News.class,id);
            ret.put("data", news);
            success = true;
        } catch (Exception e) {
            
        }
        ret.put("success", success);
        return ret;
    }
	
	/**
	 * 签到信或者站内信详情
	 * @param id
	 * @return
	 */
	 @RequestMapping(value = "/message/{id}", method = RequestMethod.GET)
	    public JSONObject message(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Message message = messageService.findById(Message.class,id);
	            ret.put("data", message);
	            success = true;
	        } catch (Exception e) {
	            
	        }
	        ret.put("success", success);
	        return ret;
	    }
	 
	 /**
	  * 获取找队信息详情
	  * @param id
	  * @return
	  */
	 @RequestMapping(value = "/recruit/{id}", method = RequestMethod.GET)
	    public JSONObject recruit(@PathVariable("id") Long id) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            Recruit recruit = recruitService.findById(Recruit.class,id);
	            ret.put("data", recruit);
	            success = true;
	        } catch (Exception e) {
	            
	        }
	        ret.put("success", success);
	        return ret;
	    }
    
	 	/**
	     * web 和 app端注册
	     * @param user  {mobile:"",verifyCode:"",password:""}
	     * @return
	     */
		@RequestMapping(value = "/player/register", method = RequestMethod.POST)
		public JSONObject register(@RequestBody Player user) {
			JSONObject ret = new JSONObject();
			boolean success = false;
			String error = "";
			String mobile = user.getMobile(); 
			Player existUser = playerService.findPlayerByMobile(mobile, null);
			if("6666".equals(user.getVerifyCode())){
				if(existUser == null){
					existUser = new Player();
					existUser.setMobile(mobile);
					existUser.setVerifyCode("6666");
					existUser.setVerifyTime(new Date());
				}
			}else if(existUser == null){
				ret.put("success", false);
				ret.put("error", "此手机号未发无验证码,非法注册！");
				return ret;
			}else if(!StringUtils.isEmpty(existUser.getPassword())){
				ret.put("success", false);
				ret.put("error", "此手机号码已注册，请登陆！");
				return ret;
			}
			Date forgetPasswordTime = existUser.getVerifyTime();
			Calendar toDate = Calendar.getInstance();
	        toDate.setTime(forgetPasswordTime);
	        toDate.add(Calendar.MINUTE, 30);
	        if(toDate.before(Calendar.getInstance())){
	        	error ="已超过30分钟，验证码失效！";
	        }else if(existUser.getVerifyCode() ==null || user.getVerifyCode()== null || !existUser.getVerifyCode().equals(user.getVerifyCode())){
	        	error ="验证码错误！";
	        }else{
	        	existUser.setPassword(user.getPassword());
	        	
	        	existUser.setName(user.getName());
	        	existUser.setNickname(user.getNickname());
	        	existUser.setIconUrl(PicUtil.transferToLoc( user.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PLAYER));
	        	String idCard = user.getIdno();
	        	if(idCard != null){
	        		existUser.setIdno(idCard);
	        		existUser.setGender(IdCardUtil.maleOrFemalByIdCard(idCard));
	        		existUser.setBirth(IdCardUtil.getBirthDayByIdCard(idCard));
	        	}
	        	existUser.setPlayerStatus(1);
	        	existUser.setIsCaptain(2);
	        	existUser.setCreateTime(new Date());
	        	try {
					playerService.saveOrUpdate(existUser);
					success = true;
				} catch (Exception e) {
					error = e.getMessage();
					log.error(e.getMessage(),e.getCause());
				}
	        }
			ret.put("success", success);
			ret.put("error", error);
			return ret;
		}
		
		   /**
	     * 保存或更新球队
	     * @param team
	     * @return
	     */
	    @RequestMapping(value = "/team/register", method = RequestMethod.POST)
	    public JSONObject register(@RequestBody Team team) {
	        JSONObject ret = new JSONObject();
	        boolean success = false;
	        try {
	            team.setIconUrl(PicUtil.transferToLoc( team.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_TEAM));
	            Timestamp now = new Timestamp(new Date().getTime());
	            team.setRegistTime(now);
	            team.setCreateTime(now);
	            teamService.saveOrUpdate(team);
	            playerService.setCaptain(team.getRegister(), team.getId());
	            ret.put("data", team);
	            success = true;
	        } catch (Exception e) {
	            log.error(e.getMessage(),e.getCause());
	            ret.put("error", e.getMessage());
	        }
	        ret.put("success", success);
	        return ret;
	    }

	/**
	 *  app 用于更新球员信息
	 * @param playerId 球员id
	 * @param nickname 昵称
	 * @param weight 体重
	 * @param height 身高
	 * @param uniformNumber 场 上号码
	 * @param qq	qq
	 * @param wechat	 wechat
	 * @return
	 */
	@RequestMapping(value = "/player/updateInfo")
	public JSONObject updatePlayerInfo(@RequestParam(value="playerId") Long playerId,
									   @RequestParam(value="nickname",required = false) String nickname,
									   @RequestParam(value="weight",required = false) Float weight,
									   @RequestParam(value="height",required = false) Float height,
									   @RequestParam(value="uniformNumber",required = false) Integer uniformNumber,
									   @RequestParam(value="qq",required = false) String qq,
									   @RequestParam(value="wechat",required = false) String wechat) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			Player dbPlayer = playerService.findPlayerById(playerId);
			dbPlayer.setNickname(nickname);//
			dbPlayer.setHeight(height);
			dbPlayer.setWeight(weight);
			dbPlayer.setUniformNumber(uniformNumber);
			dbPlayer.setQq(qq);
			dbPlayer.setWechat(wechat);

			playerService.saveOrUpdate(dbPlayer);
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			ret.put("error",e.getMessage());
		}
		ret.put("success", success);
		return ret;
	}

	 /**
	  * 保存或者更新球员
	  * @param player
	  * @return
	  */
    @RequestMapping(value = "/player/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdatePlayer(@RequestBody Player player) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	
        	
            player.setIconUrl(PicUtil.transferToLoc( player.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PLAYER));
            playerService.saveOrUpdate(player);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }

	/**
	 * 保存或者更新球员
	 * @param players
	 * @return
	 */
	@RequestMapping(value = "/player/updateCoordinate", method = RequestMethod.POST)
	public JSONObject updateCoordinate(@RequestBody List<JSONObject> players) {
		JSONObject ret = new JSONObject();
		boolean success = false;
		try {
			for(JSONObject jsonObject:players){
				Player player = JSONObject.toJavaObject(jsonObject, Player.class);
				if(player.getId()!=null){
					playerService.updateCoordinate(player);
				}
			}
			success = true;
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
		}
		ret.put("success", success);
		return ret;
	}
    /**
     * 保存或更新球队
     * @param team
     * @return
     */
    @RequestMapping(value = "/team/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateTeam(@RequestBody Team team) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            team.setIconUrl(PicUtil.transferToLoc( team.getIconUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_TEAM));
            teamService.saveOrUpdate(team);
            ret.put("data", team);
            success = true;
        } catch (Exception e) {
            log.error(e.getMessage(),e.getCause());
            ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 
     * 输入: 站内信: {"messageType":1,"checkAll":true,"title":"标题","content":"详细信息","team":{"id":2},"isEnabled":1,"tanks":[{"player":{"id":2}},{"player":{"id":6}},{"player":{"id":19}},{"player":{"id":4}}]}
     *      签到信:{"messageType":2,"checkAll":true,"title":"标题","address":"详细地址","beginTime":1489649940000,"content":"详细信息","team":{"id":2},"isEnabled":1,"tanks":[{"player":{"id":2}},{"player":{"id":6}},{"player":{"id":19}},{"player":{"id":4}}]}
     * 保存或更新站内信/签到信
     * @param message
     * @return {success: true}
     */
    @RequestMapping(value = "/message/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateMessage(@RequestBody Message message) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	message.setOpTime(new Timestamp(new Date().getTime()));
            messageService.saveOrUpdate(message);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 保存或更新入队申请
     * @param apply
     * @return
     */
    @RequestMapping(value = "/apply/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateApply(@RequestBody Apply apply) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
			if(apply.getIsOpen()==null){
				apply.setIsOpen(1);
			}
			if(apply.getIsEnabled()==null){
				apply.setIsEnabled(1);
			}
        	applyService.saveOrUpdate(apply);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 保存或更新招人信息
     * @param recruit
     * @return
     */
    @RequestMapping(value = "/recruit/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateRecruit(@RequestBody Recruit recruit) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	recruit.setOpTime(new Timestamp(new Date().getTime()));
        	recruitService.saveOrUpdate(recruit);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 上传照片
     * @param pg
     * @return
     */
    @RequestMapping(value = "/photo/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdatePhoto(@RequestBody PhotoGroup pg) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
	    	for(Photo pic:pg.getPics()){
	          	pic.setUrl(PicUtil.transferToLoc( pic.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PHOTO));
	        }
        	photoService.saveOrUpdate(pg);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 
     * @param files 上传的文件列表
     * @param comment 评论
     * @param playerId 上传球员id
     * @param viewType 查看类型: 1 公开  2 队内, 默认公开
     * @return
     */
	@RequestMapping(value = "/photo/releasePhotos", method = RequestMethod.POST)
	public JSONObject releasePhotos(@RequestParam("file") MultipartFile[] files
			,@RequestParam(value="comment",required = false) String comment
			,@RequestParam(value="playerId",required = false) Long playerId
			,@RequestParam(value="viewType",required = false,defaultValue="1") Integer viewType) {
		 JSONObject ret = new JSONObject();
		 String error = "";
        boolean success = false;
		
		PhotoGroup pg = new PhotoGroup();
		try {
			Player player = playerService.findById(Player.class, playerId);
			pg.setPlayer(player);
		} catch (Exception e) {
			log.error(e.getMessage(),e.getCause());
			ret.put("success", false);
			ret.put("error", "球员ID:"+playerId+"无效!");
			return ret;
		}
		
		pg.setComment(comment);
		pg.setAuditStatus(1);
		pg.setIsEnabled(1);
		pg.setViewType(viewType);
		pg.setCreateTime(new Timestamp(new Date().getTime()));
		pg.setPics(new HashSet<Photo>());
			 //判断file数组不能为空并且长度大于0  
        if(files!=null&&files.length>0){  
            //循环获取file数组中得文件  
            for(int i = 0;i<files.length;i++){  
            	Photo photo = new Photo();
                MultipartFile file = files[i];  
                //保存文件  
                JSONObject jo = commonService.saveImage(file,false);
                if(jo.getBooleanValue("success") == true){
                	photo.setUrl(jo.getString("picPath"));
                	photo.setStatus(1);//默认是可见的
                	pg.getPics().add(photo);
                }else{
                	error +="第"+i+"张图片上传失败:"+jo.getString("error")+";";
                }
            }
            if(pg.getPics().size() ==0){
            	ret.put("success", false);
    			ret.put("error", error+"无图片上传成功!");
    			return ret;
            }
        }  
	        try {
	            for(Photo pic:pg.getPics()){
	            	pic.setUrl(PicUtil.transferToLoc( pic.getUrl(),InitServlet.APPLICATION_URL, MyConstants.UPLOAD_PATH_PHOTO));
	            }
				photoService.saveOrUpdate(pg);
				success = true;
			} catch (Exception e) {
				error +=e.getMessage();
				log.error(e.getMessage(),e.getCause());
			}
	        ret.put("success", success);
	        ret.put("error", error);
	        return ret;
	}
	
    /**
     * 上传视频
     * @param video
     * @return
     */
    @RequestMapping(value = "/video/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateVideo(@RequestBody Video video) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
			if(video.getId()!=null){
				video.setModifyTime(new Timestamp(new Date().getTime()));
			}else{
				video.setCreateTime(new Timestamp(new Date().getTime()));
			}
        	videoService.saveVideo(video);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 球赛保存或者更新
     * @param game
     * @return
     */
    @RequestMapping(value = "/game/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateGame(@RequestBody Game game) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	if(game.getId()==null){
        		game.setCreateTime(new Timestamp(new Date().getTime()));
        	}
        	gameService.saveOrUpdate(game);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        	ret.put("error", e.getMessage());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 在球队详情页, 点击申请入队
     * @param teamId 
     * @param playerId 
     * @return
     */
    @RequestMapping(value = "/applyTeam", method = RequestMethod.GET)
    public JSONObject applyTeam(@RequestParam("teamId") Long teamId,@RequestParam("playerId") Long playerId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Apply apply;
        	Team team = teamService.findById(Team.class, teamId);
        	Player player = playerService.findById(Player.class, playerId);
			ApplyTank tank = new ApplyTank();
			tank.setTeam(team);
			tank.setCreateTime(new Date());
            List<Apply> applys = applyService.findOpenApply(playerId);
            if(applys.size()>0){
            	apply = applys.get(0);
            }else{
            	apply = new Apply();
            	apply.setTitle("从球队详情页面申请入队");
            	apply.setApplyTime(new Timestamp(new Date().getTime()));
            	apply.setPlayer(player);
            	apply.setIsOpen(1);
            	apply.setIsPublic(2);
            	apply.setIsEnabled(1);
            	Set<ApplyTank> tanks = new HashSet<ApplyTank>();
				apply.setTanks(tanks);
            }
			Set<ApplyTank> tanks = apply.getTanks();
			tanks.add(tank);
            applyService.saveOrUpdate(apply);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 从球员详情页面邀请入队
     * @param teamId
     * @param playerId
     * @return
     */
    @RequestMapping(value = "/recruitPlayer", method = RequestMethod.GET)
    public JSONObject recruitPlayer(@RequestParam("teamId") Long teamId,@RequestParam("playerId") Long playerId) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	Recruit recruit;
        	Team team = teamService.findById(Team.class, teamId);
        	Player player = playerService.findById(Player.class, playerId);
        	recruit = new Recruit();
        	recruit.setTitle("从球员详情页面邀请入队");
        	recruit.setOpTime(new Timestamp(new Date().getTime()));
        	recruit.setTeam(team);
        	recruit.setIsEnabled(1);
        	recruit.setIsPublic(2);
        	recruit.setPlayer(player);
            recruitService.saveOrUpdate(recruit);
            
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 队长审核 球员的签到信息
     * @param items
     * @return
     */
    @RequestMapping(value = "/message/updateTanks", method = RequestMethod.POST)
    public JSONObject updateTanks(@RequestBody List<JSONObject> items) {
            List<MessageTank> tanks = new ArrayList<MessageTank>();
            for (JSONObject item : items) {
            	MessageTank one = JSONObject.toJavaObject(item, MessageTank.class);
            	tanks.add(one);
            }
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
            messageService.confirm(tanks);
            success = true;
        } catch (Exception e) {
        	log.error(e.getMessage(),e.getCause());
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 添加评论
     * @param comment
     * @return
     */
    @RequestMapping(value = "/comment/saveOrUpdate", method = RequestMethod.POST)
    public JSONObject saveOrUpdateComment(@RequestBody Comment comment) {
        JSONObject ret = new JSONObject();
        boolean success = false;
        try {
        	comment.setOpTime(new Timestamp(new Date().getTime()));
        	commentService.saveOrUpdate(comment);
            success = true;
        } catch (Exception e) {
            
        }
        ret.put("success", success);
        return ret;
    }
    
    /**
     * 判断球员字段的唯一性
     * @param id (当前球员的id,球员修改球员信息时需加上此字段)
     * @param idno  (身份证号)
     * @param mobile 手机号
     * 手机号与身份证号二选一, 哪个存在时验证哪个的唯一性
     * @return
     */
    @RequestMapping(value = "/player/checkUnique", method = RequestMethod.POST)
  	public JSONObject checkUniquePlayer(@RequestParam(value="id",required = false) Long id,
    		@RequestParam(value="idno",required = false) String idno,
    		@RequestParam(value="mobile",required = false) String mobile){
    	JSONObject ret = new JSONObject();
    	Player player = null ;
    	if(mobile!=null){
  			player  = playerService.findPlayerByMobile(mobile,id);
  		}else if(idno!=null&& !"110110190101010106".equals(idno)){
  			player  = playerService.findPlayerByIdno(idno,id);
  		}
    	if(player == null ||StringUtils.isEmpty(player.getPassword())){
    		ret.put("isUnique", true);
		}else{
			ret.put("isUnique", false);
  			ret.put("auditStatus", player.getAuditStatus());
		}
  		return ret;
  	}
    
    /**
     * 看当前球队的队名有没有其它球队使用
     * @param id 球队id
     * @param name 球队名称
     * @return
     */
    @RequestMapping(value = "/team/checkUnique", method = RequestMethod.POST)
  	public JSONObject checkUniqueTeam(@RequestParam(value="id",required = false) Long id,
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
    
    /**
     * 模糊搜索 球场 
     * @param name  球场名称
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/field/search", method = RequestMethod.GET)
  	public List searchField(@RequestParam(value="name",required=false) String name,@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return fieldService.searchField(name, pageSize);
  	}
    
    /**
     * 模糊搜索满足条件的球队
     * @param name
     * @param snippet
     * @param teamType
     * @param exId
     * @param cupId
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/team/search", method = RequestMethod.GET)
  	public List searchTeam(@RequestParam(value="name",required = false) String name,
  			@RequestParam(value="snippet",required = false) String snippet,
  			@RequestParam(value="teamType",required = false) Integer teamType,
  			@RequestParam(value="exId",required = false) Long exId,
  			@RequestParam(value="cupId",required = false) Long cupId,
  			@RequestParam(value="pageSize",required = false) Integer pageSize) throws IOException {
  		return teamService.searchTeam(name, snippet, teamType,exId,cupId, pageSize);
  	}
    
    /**
     * 模糊搜索满足条件的球员
     * @param name 
     * @param isCaptain
     * @param pageSize
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/player/search", method = RequestMethod.GET)
  	public List searchPlayer(@RequestParam(value="name",required = false) String name,
  			@RequestParam(value="isCaptain",required = false) Boolean isCaptain,
  			@RequestParam("pageSize") Integer pageSize) throws IOException {
  		return playerService.searchPlayer(name, isCaptain, pageSize);
  	}
    
  /**
   * "noticeTypeList":[[1,"球队招人"],[2,"入队申请"],[3,"约战"],[4,"签到"],[5,"站内信"],[6,"我的申请"],[7,"邀我入队"],[8,"球员变动"]],		
   * @param playerId : 球员的id. 如果是队长, 则读取球队的通目. 如果是普通球员,则读取球员的通知数目
   * @param lastReadTime: 球队招人的上次阅读时间(此时间之后作了变动的记录,即为新消息)
   * @return
 * @throws Exception 
   */
    @RequestMapping(value = "/getUnreadCnt", method = RequestMethod.GET)
  	public JSONObject getUnreadCnt(@RequestParam(value="playerId",required = true) Long playerId,
  			@RequestParam(value="lastReadTime",required = false) Long lastReadTime) throws Exception {
		JSONObject ret = new JSONObject();
		Player player = playerService.findById(Player.class, playerId);
		if(player.getIsCaptain() != null && player.getIsCaptain() == 1){
			//当前球员是队长, 获取队长的
			ret.put("unReadCnt1", commonService.getUnreadCount(1,player,lastReadTime)) ;
			ret.put("unReadCnt2", commonService.getUnreadCount(2,player,lastReadTime)) ;
			ret.put("unReadCnt3", commonService.getUnreadCount(3,player,lastReadTime)) ;
			ret.put("unReadCnt8", commonService.getUnreadCount(8,player,lastReadTime)) ;
		}else{
			ret.put("unReadCnt3", commonService.getUnreadCount(3,player,lastReadTime)) ;
			ret.put("unReadCnt4", commonService.getUnreadCount(4,player,lastReadTime)) ;
			ret.put("unReadCnt5", commonService.getUnreadCount(5,player,lastReadTime)) ;
			ret.put("unReadCnt6", commonService.getUnreadCount(6,player,lastReadTime)) ;
			ret.put("unReadCnt7", commonService.getUnreadCount(7,player,lastReadTime)) ;
			ret.put("unReadCnt8", commonService.getUnreadCount(8,player,lastReadTime)) ;
		}
        return ret;
  	}
    
}
