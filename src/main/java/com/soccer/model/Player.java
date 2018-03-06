package com.soccer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Player entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "football_player")
public class Player implements java.io.Serializable {

	// Fields
	/**
	 * 
	 */
	private static final long serialVersionUID = -312903743425775795L;
	private Long id;	
	private String address;		//地址
	private Date birth;	//生日
	private String iconUrl;	//头像url
	private String email;	//email
	private Integer playerStatus;	//球员状态 playerStatusList
	private Integer kind;	// 类型  playerKindList
	private Float weight;	//体重
	private Float height;	//身高
	private Integer uniformNumber;	//队服号码
	private String introduce;	//介绍
	private String qq;	//qq
	private String wechat;//微信
	private String personalPage;//个人主页
	private Integer position;//  球场上踢的位置 playerPositionList
	private Integer x;// app使用的位置:x
	private Integer y;// app使用的位置:y

	private Integer gender;// 性别  genderList
    private String locks;  //权限
    private String idno;	//身份证号
    private String nickname;// 尼称
	private Date loginDate; // 登录日期
	private Integer loginFailureCount;// 登陆失败次数
	private String loginIp;//	登录ip
	private String mobile;//	手机号
	private String name;//	姓名
	private String account;//	帐号
	private String password;//	密码
	private Integer auditStatus;//	审核状态 auditStatusList
	private Long auditor;//	审核者
	private Date auditTime;//审核 时间
	private Integer isCaptain;//	是否队长  yesnoList
	private Team team;//	 所属球队
	private Long creater;//
	private Date createTime;//
	private Long modifier;//
	private Date modifyTime;//
	private Integer total;// 全部赛次
	private Integer win;// 赢的次数
	private Integer lost;//输的次数
	private Integer even;//平的次数
	private Integer attendTimes;// 参加活动的次数
	private Integer cheatTimes;// 签到却没有参加活动，被队长否决的次数
	private Integer allActivites;//应当参加的活动总数
	private Float point;//综合评分
	private Integer value;//	身价
	private Integer acRate;		//活动频率 1周一次之类的 acRateList
	private Map map;//	统计map
	private String teamTitle;//	球队名称
	private String verifyCode;//  验证码
	private Date verifyTime;// 验证时间
    private Integer likeNum; //点赞次数
    private Integer likeNumLastWeek; //上周点赞次数
    private Integer likeNumWeekBefLast; //前周点赞次数
    private Integer pointMinus;//扣分
    private Set<PlayerHonor> honors;//荣誉

	public Player() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Player(Long id, String name,String teamTitle) {
		super();
		this.id = id;
		this.name = name;
		this.teamTitle = teamTitle;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "nickname", length = 50)
	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "idno", length = 22)
	public String getIdno() {
		return this.idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}
	    
	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name = "icon_url", length = 200)
	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@Column(name = "birth", length = 19)
	public Date getBirth() {
		return this.birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	@Column(name = "email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "gender")
	public Integer getGender() {
		return this.gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

    @Column(name="locks")
    public String getLocks() {
        return this.locks;
    }
    
    public void setLocks(String locks) {
        this.locks = locks;
    }
    
	 @Column(name="position")
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Column(name="x")
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	@Column(name="y")
	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}


	@Column(name = "login_date", length = 19)
	public Date getLoginDate() {
		return this.loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Column(name = "login_failure_count")
	public Integer getLoginFailureCount() {
		return this.loginFailureCount;
	}

	public void setLoginFailureCount(Integer loginFailureCount) {
		this.loginFailureCount = loginFailureCount;
	}

	@Column(name = "login_ip")
	public String getLoginIp() {
		return this.loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@Column(name = "mobile")
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "account")
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "password")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "audit_status")
	public Integer getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}


	@Column(name = "audit_time", length = 19)
	public Date getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	@Column(name = "is_captain")
	public Integer getIsCaptain() {
		return this.isCaptain;
	}

	public void setIsCaptain(Integer isCaptain) {
		this.isCaptain = isCaptain;
	}

	@JoinColumn(name = "teamId", referencedColumnName = "ID")
	@ManyToOne(cascade = {})   
	@NotFound(action=NotFoundAction.IGNORE)
	public Team getTeam() {
		return this.team;
	}
		
	public void setTeam(Team team) {
		this.team = team;
	}

	@Column(name = "creater")
	public Long getCreater() {
		return this.creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modifier")
	public Long getModifier() {
		return this.modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}

	@Column(name = "auditor")
	public Long getAuditor() {
		return this.auditor;
	}

	public void setAuditor(Long auditor) {
		this.auditor = auditor;
	}

	@Column(name = "modify_time", length = 19)
	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "total")
	public Integer getTotal() {
		return this.total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@Column(name = "win")
	public Integer getWin() {
		return this.win;
	}

	public void setWin(Integer win) {
		this.win = win;
	}

	@Column(name = "lost")
	public Integer getLost() {
		return this.lost;
	}

	public void setLost(Integer lost) {
		this.lost = lost;
	}

	@Column(name = "even")
	public Integer getEven() {
		return this.even;
	}

	public void setEven(Integer even) {
		this.even = even;
	}

	@Column(name = "attend_times")
	public Integer getAttendTimes() {
		return this.attendTimes;
	}

	public void setAttendTimes(Integer attendTimes) {
		this.attendTimes = attendTimes;
	}

	@Column(name = "cheat_times")
	public Integer getCheatTimes() {
		return cheatTimes;
	}

	public void setCheatTimes(Integer cheatTimes) {
		this.cheatTimes = cheatTimes;
	}
	
	@Column(name = "all_activities")
	public Integer getAllActivites() {
		return allActivites;
	}

	public void setAllActivites(Integer allActivites) {
		this.allActivites = allActivites;
	}

	@Column(name = "point", precision = 12, scale = 0)
	public Float getPoint() {
		return this.point;
	}

	public void setPoint(Float point) {
		this.point = point;
	}

	@Column(name = "value")
	public Integer getValue() {
		return this.value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Column(name = "player_status")
	public Integer getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(Integer playerStatus) {
		this.playerStatus = playerStatus;
	}

	@Column(name = "kind")
	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}

	public Float getWeight() {
		return weight;
	}

	@Column(name = "weight", precision = 12, scale = 2)
	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Float getHeight() {
		return height;
	}

	@Column(name = "height", precision = 12, scale = 2)
	public void setHeight(Float height) {
		this.height = height;
	}

	@Column(name = "uniform_number")
	public Integer getUniformNumber() {
		return uniformNumber;
	}

	public void setUniformNumber(Integer uniformNumber) {
		this.uniformNumber = uniformNumber;
	}

	@Column(name = "introduce")
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	@Column(name = "qq")
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	@Column(name = "wechat")
	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	@Column(name = "personal_page")
	public String getPersonalPage() {
		return personalPage;
	}

	public void setPersonalPage(String personalPage) {
		this.personalPage = personalPage;
	}
	@Transient
	public String getTeamTitle() {
		return teamTitle;
	}

	public void setTeamTitle(String teamTitle) {
		this.teamTitle = teamTitle;
	}
	
	
	@Column(name = "verify_code")
	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	@Column(name = "verify_time",length = 19)
	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}
	
	@Column(name = "like_num")
	public Integer getLikeNum() {
		return this.likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}
	
	@Column(name = "ac_rate")
	public Integer getAcRate() {
		return acRate;
	}
	public void setAcRate(Integer acRate) {
		this.acRate = acRate;
	}

	@Column(name = "like_num_last_week")
	public Integer getLikeNumLastWeek() {
		return this.likeNumLastWeek;
	}

	public void setLikeNumLastWeek(Integer likeNumLastWeek) {
		this.likeNumLastWeek = likeNumLastWeek;
	}

	@Column(name = "like_num_week_bef_last")
	public Integer getLikeNumWeekBefLast() {
		return this.likeNumWeekBefLast;
	}

	public void setLikeNumWeekBefLast(Integer likeNumWeekBefLast) {
		this.likeNumWeekBefLast = likeNumWeekBefLast;
	}

	@Column(name = "point_minus")
	public Integer getPointMinus() {
		return pointMinus;
	}

	public void setPointMinus(Integer pointMinus) {
		this.pointMinus = pointMinus;
	}

	@Transient
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="player_id")
	public Set<PlayerHonor> getHonors() {
		return honors;
	}
	public void setHonors(Set<PlayerHonor> honors) {
		this.honors = honors;
	}
	
}