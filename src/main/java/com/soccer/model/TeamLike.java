package com.soccer.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

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
 * FootballTeamLike entity. @author MyEclipse Persistence Tools
 * 球队点赞表
 */
@Entity
@Table(name = "team_like")
public class TeamLike implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1621996724924491260L;
	// Fields

	private Long id;
	private Team team; //被赞球队
	private Player giver;	//点赞者
	private Integer status;	// 默认1 目前没有取消点赞方法
	private Date createTime;

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
	@ManyToOne(cascade = {})   
	@NotFound(action=NotFoundAction.IGNORE)
	public Team getTeam() {
		return this.team;
	}
		
	public void setTeam(Team team) {
		this.team = team;
	}

	@JoinColumn(name = "giver", referencedColumnName = "ID")
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Player getGiver() {
		return this.giver;
	}

	public void setGiver(Player giver) {
		this.giver = giver;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}