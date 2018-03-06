package com.soccer.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Recruit entity. @author MyEclipse Persistence Tools
 * 球队招人表  
 */
@Entity
@Table(name = "recruit")
public class Recruit implements java.io.Serializable {

	// Fields

	private Long id;
	private String title;  //标题
	private String content;	//内容
	private Team team;	//球队
	private Integer isPublic;	//是否广场显示
	private Timestamp opTime;	
	private Integer isEnabled;	//是否被管理员禁用
	private Player player;	   //被邀请入队的球员  (可以为空)
	private Integer confirmStatus;	//球员 是的回应 ,是否加入球队  yesnoList
	private Timestamp confirmTime;	//
	// Constructors

	/** default constructor */
	public Recruit() {
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

	@Column(name = "title", length = 100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content", length = 1000)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
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

	@Column(name = "is_public")
	public Integer getIsPublic() {
		return this.isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	@Column(name = "op_time", length = 19)
	public Timestamp getOpTime() {
		return this.opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}

	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
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

}