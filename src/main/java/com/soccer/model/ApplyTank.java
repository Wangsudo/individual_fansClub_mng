package com.soccer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * ApplyTank entity. @author MyEclipse Persistence Tools
 * 一次入队申请的 针对某一球队的申请, 是Apply的子表
 */
@Entity
@Table(name = "apply_tank")
public class ApplyTank implements java.io.Serializable {

	// Fields

	private Long id;
	private Team team;  //申请的球队
	private Long applyId;	//母表的id
	private Player captain;	//team 球队的队长 , 与auditStatus\auditTime 配合. 
	private Integer confirmStatus;	//审核通过后, 球员是否确认入队(只有在球队申请了多个球队时,才会要求球员手动地确认)
	private Date confirmTime; //审核通过后, 球员的确认时间
	private Integer auditStatus;  //代表队长captain 对该入队申请的审核 auditStatusList
	private Date auditTime;  //审核 时间
	private Date createTime;  //入队申请时间
	// Constructors

	/** default constructor */
	public ApplyTank() {
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

	@JoinColumn(name = "team_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne  
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Column(name = "apply_id")
	public Long getApplyId() {
		return applyId;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	

	@JoinColumn(name = "captain_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	public Player getCaptain() {
		return captain;
	}

	public void setCaptain(Player captain) {
		this.captain = captain;
	}

	@Column(name = "confirm_status")
	public Integer getConfirmStatus() {
		return this.confirmStatus;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	@Column(name = "confirm_time", length = 19)
	public Date getConfirmTime() {
		return this.confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
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

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
}