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
 * 球队荣誉表
 */
@Entity
@Table(name = "team_honor")
public class TeamHonor implements java.io.Serializable {

	// Fields

	private Long id;
	private Long teamId; //球队id
	private Honor honor; 
	private Timestamp opTime;
	private Long op;

	// Constructors

	/** default constructor */
	public TeamHonor() {
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

	@Column(name = "team_id")
	public Long getTeamId() {
		return this.teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
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