package com.soccer.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


/**
 * Team entity. @author MyEclipse Persistence Tools
 * 球队
 */
@Entity
@Table(name = "team")
public class Team implements java.io.Serializable {

	// Fields
    
	private Long id;
	private String teamTitle;	//队名
	private String introduce;	//介绍
	private String iconUrl;	   //队徽
	private String coverUrl;	//球队的合影
	private String region;	  //地区
	private Integer kind;		//类型 teamKindList
	private Integer acRate;		//活动频率 1周一次之类的 acRateList , 建队时要选择
	private Integer teamType;	//赛制 teamTypeList
	private String locks;	    //权限控制
	private Integer value;		//球队身价
	private Integer total;		//一共踢的赛次
	private Integer win;		//赢的次数
	private Integer lost;		//输的次数
	private Integer even;		//平的次数
	private Integer wrongTimes;		//上报比分错误的次数
    private Integer chanllegeTimes;	//约战次数
    private Integer acceptedTimes;	//应战次数
	private Integer newsTimes;		//发布站内信次数
	private Integer point;			//综合评分
	private Long register;
	private Timestamp registTime;	//注册时间
	private Long creater;
	private Timestamp createTime;
	private Long modifier;
	private Timestamp modifyTime;
	private Integer dismissed;		//是否解散  yesnoList
	private Long dismisser;			//解散球队的队长
	private Timestamp dismissTime;	 //解散时间
	private Integer official;		//是否官方球员	yesnoList
	private Set<TeamHonor> honors;	//球队荣誉
	private Integer likeNum;		//被点赞次数
	private Integer likeNumLastWeek;	//上周点赞次数
	private Integer likeNumWeekBefLast;	//前周点赞次数
	private Integer pointMinus;//扣分
	private Integer playerNum;//球员数目
	// Constructors

	/** default constructor */
	public Team() {
	}
    public Team(Long id, String teamTitle,Integer playerNum) {
		super();
		this.id = id;
		this.teamTitle = teamTitle;
		this.playerNum = playerNum;
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

	@Column(name = "team_title", length = 100)
	public String getTeamTitle() {
		return this.teamTitle;
	}

	public void setTeamTitle(String teamTitle) {
		this.teamTitle = teamTitle;
	}

	@Column(name = "introduce")
	public String getIntroduce() {
		return this.introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	@Column(name = "icon_url", length = 200)
	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	@Column(name = "cover_url", length = 200)
	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	@Column(name = "region", length = 40)
	public String getRegion() {
		return region;
	}



	public void setRegion(String region) {
		this.region = region;
	}

	@Column(name = "kind")
	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}
	
	@Column(name = "ac_rate")
	public Integer getAcRate() {
		return acRate;
	}
	public void setAcRate(Integer acRate) {
		this.acRate = acRate;
	}
	
	@Column(name = "team_type")
	public Integer getTeamType() {
		return this.teamType;
	}

	public void setTeamType(Integer teamType) {
		this.teamType = teamType;
	}
	
	@Column(name="locks")
    public String getLocks() {
        return this.locks;
    }
    
    public void setLocks(String locks) {
        this.locks = locks;
    }

	@Column(name = "value")
	public Integer getValue() {
		return this.value;
	}

	public void setValue(Integer value) {
		this.value = value;
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

	@Column(name="wrong_times")
    public Integer getWrongTimes() {
		return wrongTimes;
	}
	public void setWrongTimes(Integer wrongTimes) {
		this.wrongTimes = wrongTimes;
	}
	
	@Column(name="chanllege_times")
    public Integer getChanllegeTimes() {
        return this.chanllegeTimes;
    }
    
    public void setChanllegeTimes(Integer chanllegeTimes) {
        this.chanllegeTimes = chanllegeTimes;
    }
    
    @Column(name="accepted_times")

    public Integer getAcceptedTimes() {
        return this.acceptedTimes;
    }
    
    public void setAcceptedTimes(Integer acceptedTimes) {
        this.acceptedTimes = acceptedTimes;
    }

	@Column(name = "news_times")
	public Integer getNewsTimes() {
		return this.newsTimes;
	}

	public void setNewsTimes(Integer newsTimes) {
		this.newsTimes = newsTimes;
	}
	
	@Column(name = "point")
	public Integer getPoint() {
		return this.point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}
	@Column(name = "creater")
	public Long getCreater() {
		return this.creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}

	@Column(name = "register")
	public Long getRegister() {
		return register;
	}
	public void setRegister(Long register) {
		this.register = register;
	}
	@Column(name = "regist_time", length = 19)
	public Timestamp getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Timestamp registTime) {
		this.registTime = registTime;
	}

	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modifier")
	public Long getModifier() {
		return this.modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}
	
	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

    @Column(name="dismissed")
	public Integer getDismissed() {
		return dismissed;
	}

	public void setDismissed(Integer dismissed) {
		this.dismissed = dismissed;
	}


	@Column(name = "dismiss_time", length = 19)
	public Timestamp getDismissTime() {
		return dismissTime;
	}

	public void setDismissTime(Timestamp dismissTime) {
		this.dismissTime = dismissTime;
	}
	
	
	@Column(name = "dismisser")
	public Long getDismisser() {
		return dismisser;
	}

	public void setDismisser(Long dismisser) {
		this.dismisser = dismisser;
	}
	
	@Column(name = "official")
	public Integer getOfficial() {
		return official;
	}
	public void setOfficial(Integer official) {
		this.official = official;
	}
	
	@Column(name = "like_num")
	public Integer getLikeNum() {
		return this.likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
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
	
	@Column(name = "player_num")
	public Integer getPlayerNum() {
		return playerNum;
	}
	public void setPlayerNum(Integer playerNum) {
		this.playerNum = playerNum;
	}
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="team_id")
	public Set<TeamHonor> getHonors() {
		return honors;
	}
	public void setHonors(Set<TeamHonor> honors) {
		this.honors = honors;
	}

}