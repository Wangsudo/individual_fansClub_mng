package com.soccer.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * FootballGames entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "games")
public class Game implements java.io.Serializable {

	// Fields

	private Long id;
	private String address;  //比赛地点
	private Timestamp beginTime;	//比赛开始时间
	private Integer playingTime;	//比寒耗时
	private Integer overtime;	    //超时
	private String title;		//标题
	private String content;		//内容
	private String comment;		//评论
	private Integer teamType;	 //赛制  teamTypeList
	private Integer auditStatus;	//审核状态 auditStatusList
	private Long auditor;	    // 审核 者(后台管理员 在 上传比分不一致时,判定比赛比分)
	private Timestamp auditTime;	//审核时间
	private Team teamA;		    // 队伍A
	private Integer teamAOperation;	 //队伍A的操作 (1:挑战, 2,撤消)
	private Team teamB;			 // 队伍B
	private Integer teamBOperation;  //队伍B的操作 (1:应战, 2,放弃)
	private Integer scoreA1;		//A队上传A队比分
	private Integer scoreB1;		//A队上传B队比分
	private Integer scoreA2;		//B队上传A队比分
	private Integer scoreB2;		//B队上传B队比分
	private Integer scoreA;			//最终上传A队比分
	private Integer scoreB;			//最终上传B队比分
	private Integer isEnabled;		//赛事是否前端显示
	private Integer isPublic;		//赛事是否公开(是否广场可见) yesnoList
	private Timestamp createTime;	
	private Long creater;
	private Timestamp modifyTime;
	private Long modifier;
	private Cup cup;				//杯赛
	private CupGroup group;			//杯赛的哪个小组.

	// Constructors

	/** default constructor */
	public Game() {
	}

	/** minimal constructor */
	public Game(Long id) {
		this.id = id;
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

	@Column(name = "address", length = 100)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "begin_time", length = 19)
	public Timestamp getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "playing_time")
	public Integer getPlayingTime() {
		return playingTime;
	}

	public void setPlayingTime(Integer playingTime) {
		this.playingTime = playingTime;
	}

	@Column(name = "overtime")
	public Integer getOvertime() {
		return overtime;
	}

	public void setOvertime(Integer overtime) {
		this.overtime = overtime;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content", length = 500)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "comment", length = 200)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@JoinColumn(name = "team_a", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne    
	public Team getTeamA() {
		return this.teamA;
	}

	public void setTeamA(Team teamA) {
		this.teamA = teamA;
	}

	@JoinColumn(name = "team_b", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne    
	public Team getTeamB() {
		return this.teamB;
	}

	public void setTeamB(Team teamB) {
		this.teamB = teamB;
	}
	
	@Column(name = "score_a1")
	public Integer getScoreA1() {
		return this.scoreA1;
	}

	public void setScoreA1(Integer scoreA1) {
		this.scoreA1 = scoreA1;
	}

	@Column(name = "score_b1")
	public Integer getScoreB1() {
		return this.scoreB1;
	}

	public void setScoreB1(Integer scoreB1) {
		this.scoreB1 = scoreB1;
	}

	@Column(name = "score_a2")
	public Integer getScoreA2() {
		return this.scoreA2;
	}

	public void setScoreA2(Integer scoreA2) {
		this.scoreA2 = scoreA2;
	}

	@Column(name = "score_b2")
	public Integer getScoreB2() {
		return this.scoreB2;
	}

	public void setScoreB2(Integer scoreB2) {
		this.scoreB2 = scoreB2;
	}

	@Column(name = "score_a")
	public Integer getScoreA() {
		return this.scoreA;
	}

	public void setScoreA(Integer scoreA) {
		this.scoreA = scoreA;
	}

	@Column(name = "score_b")
	public Integer getScoreB() {
		return this.scoreB;
	}

	public void setScoreB(Integer scoreB) {
		this.scoreB = scoreB;
	}

	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}
		
	@Column(name = "is_public")
	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}


	@Column(name = "team_type")
	public Integer getTeamType() {
		return teamType;
	}

	public void setTeamType(Integer teamType) {
		this.teamType = teamType;
	}

	@Column(name = "team_a_operation")
	public Integer getTeamAOperation() {
		return teamAOperation;
	}

	public void setTeamAOperation(Integer teamAOperation) {
		this.teamAOperation = teamAOperation;
	}
	
	@Column(name = "team_b_operation")
	public Integer getTeamBOperation() {
		return teamBOperation;
	}

	public void setTeamBOperation(Integer teamBOperation) {
		this.teamBOperation = teamBOperation;
	}

	@Column(name = "audit_status")
	public Integer getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "auditor")
	public Long getAuditor() {
		return this.auditor;
	}

	public void setAuditor(Long auditor) {
		this.auditor = auditor;
	}

	@Column(name = "audit_time", length = 19)
	public Timestamp getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Timestamp auditTime) {
		this.auditTime = auditTime;
	}
	
	@Column(name = "modifier")
	public Long getModifier() {
		return this.modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}
	
	@Column(name = "creater")
	public Long getCreater() {
		return this.creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}

	
	@JoinColumn(name = "cup_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne   
	public Cup getCup() {
		return cup;
	}

	public void setCup(Cup cup) {
		this.cup = cup;
	}

	@JoinColumn(name = "group_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne 
	public CupGroup getGroup() {
		return group;
	}

	public void setGroup(CupGroup group) {
		this.group = group;
	}

}