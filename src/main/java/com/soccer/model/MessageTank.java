package com.soccer.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * MessageTank entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "message_tank")
public class MessageTank implements java.io.Serializable {

	// Fields

	private Long id;
	private Player player;  //球员
	private Long messageId;	//父表id
	private Integer readStatus;	//阅读状态 (字段未用)
	private Integer confirmStatus;	//确认 yesnoList (用于签到信)
	private Timestamp readTime;	//阅读时间 (字段未用)
	private Timestamp confirmTime;  //确认时间 (用于签到信)
	private Timestamp auditTime;	//队长审核 
	private Integer auditStatus;	//队长审核 状态   auditStatusList


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

	 // 上传相片球员
	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToOne
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Column(name = "message_id")
	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	@Column(name = "read_status")
	public Integer getReadStatus() {
		return this.readStatus;
	}

	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}

	@Column(name = "confirm_status")
	public Integer getConfirmStatus() {
		return this.confirmStatus;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	@Column(name = "read_time")
	public Timestamp getReadTime() {
		return readTime;
	}

	public void setReadTime(Timestamp readTime) {
		this.readTime = readTime;
	}

	@Column(name = "confirm_time")
	public Timestamp getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Timestamp confirmTime) {
		this.confirmTime = confirmTime;
	}
    
    @Column(name="audit_time", length=19)
    public Timestamp getAuditTime() {
        return this.auditTime;
    }
    
    public void setAuditTime(Timestamp auditTime) {
        this.auditTime = auditTime;
    }
    
	@Column(name = "audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	
}