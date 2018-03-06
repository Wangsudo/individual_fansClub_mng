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
 * FootballPlayerLike entity. @author MyEclipse Persistence Tools
 * 球员接收到的点赞
 */
@Entity
@Table(name = "football_player_like")
public class PlayerLike implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1507449790251105823L;
	// Fields

	private Long id;
	private Player player; //被点赞的球员
	private Player giver;	//点赞者
	private Integer status;	//  状态   默认是1 ,暂时无取消功能
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

	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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