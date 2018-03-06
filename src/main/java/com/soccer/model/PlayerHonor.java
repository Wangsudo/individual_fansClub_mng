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
 * TeamHonor entity. @author MyEclipse Persistence Tools
 * 球员 与 荣誉的关联表
 */
@Entity
@Table(name = "player_honor")
public class PlayerHonor implements java.io.Serializable {

	// Fields

	private Long id;
	private Long playerId; //球员id
	private Honor honor;	//荣誉
	private Timestamp opTime;	
	private Long op;

	// Constructors

	/** default constructor */
	public PlayerHonor() {
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

	@Column(name = "player_id")
	public Long getPlayerId() {
		return playerId;
	}


	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}


	@JoinColumn(name = "honor_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne   
	public Honor getHonor() {
		return honor;
	}

	public void setHonor(Honor honor) {
		this.honor = honor;
	}

	@Column(name = "op_time", length = 19)
	public Timestamp getOpTime() {
		return this.opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}

	@Column(name = "op")
	public Long getOp() {
		return this.op;
	}

	public void setOp(Long op) {
		this.op = op;
	}

}