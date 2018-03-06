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
 * TransferLog entity. @author MyEclipse Persistence Tools
 * 转队日志
 */
@Entity
@Table(name = "football_transfer_log")
public class TransferLog implements java.io.Serializable {

	// Fields

	private Long id;
	private Team fromTeam; //转出球队
	private Team toTeam;	//转入球队
	private Player player;	//球员
	private Timestamp fromTime;	//转出时间
	private Timestamp toTime;	//转入时间
	private Timestamp createTime;
	private String creater;
	private Timestamp modifyTime;
	private String modifier;

	// Constructors

	/** default constructor */
	public TransferLog() {
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

	
	@JoinColumn(name = "from_team", referencedColumnName = "ID",nullable=true)
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Team getFromTeam() {
		return fromTeam;
	}


	public void setFromTeam(Team fromTeam) {
		this.fromTeam = fromTeam;
	}

	@JoinColumn(name = "to_team", referencedColumnName = "ID",nullable=true)
	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	public Team getToTeam() {
		return toTeam;
	}


	public void setToTeam(Team toTeam) {
		this.toTeam = toTeam;
	}

	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setToTime(Timestamp toTime) {
		this.toTime = toTime;
	}

	@Column(name = "from_time", length = 19)
	public Timestamp getFromTime() {
		return fromTime;
	}

	public void setFromTime(Timestamp fromTime) {
		this.fromTime = fromTime;
	}

	@Column(name = "to_time", length = 19)
	public Timestamp getToTime() {
		return toTime;
	}

	




	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Column(name = "creater", length = 20)
	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "modifier", length = 20)
	public String getModifier() {
		return this.modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

}