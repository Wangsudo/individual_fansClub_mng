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
 * 签到信与站内信表的子表 参考 MessageTank类, 只是父子不一样
 */
@Entity
@Table(name = "message_tank")
public class MessageTankB implements java.io.Serializable {

	// Fields

	private Long id;
	private Player player;
	private MessageB message;
	private Integer readStatus;
	private Integer confirmStatus;
	private Timestamp readTime;
	private Timestamp confirmTime;
	private Timestamp auditTime;
	private Integer auditStatus;


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
	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)  
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@JoinColumn(name = "message_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne 
	public MessageB getMessage() {
		return message;
	}

	public void setMessage(MessageB message) {
		this.message = message;
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