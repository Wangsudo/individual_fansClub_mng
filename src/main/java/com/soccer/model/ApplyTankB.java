package com.soccer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * ApplyTank entity. @author MyEclipse Persistence Tools
 * 参考 ApplyTank 类, 对应的是同一张表. 只是母子表的区别
 */
@Entity
@Table(name = "apply_tank")
public class ApplyTankB implements java.io.Serializable {

	// Fields

	private Long id;
	private Team team;
	private ApplyB apply;
	private Player captain;
	private Integer confirmStatus;
	private Timestamp confirmTime;
	private Integer auditStatus;
	private Timestamp auditTime;
	private Date createTime;  //入队申请时间

	// Constructors

	/** default constructor */
	public ApplyTankB() {
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

	@JoinColumn(name = "apply_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne( cascade=CascadeType.ALL)  
	public ApplyB getApply() {
		return apply;
	}

	public void setApply(ApplyB apply) {
		this.apply = apply;
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
	public Timestamp getConfirmTime() {
		return this.confirmTime;
	}

	public void setConfirmTime(Timestamp confirmTime) {
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
	public Timestamp getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Timestamp auditTime) {
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