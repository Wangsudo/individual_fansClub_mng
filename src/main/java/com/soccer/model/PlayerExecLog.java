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
 * UserExecLog entity. @author MyEclipse Persistence Tools
 * 球员操作记录表  (此表其实未完美使用)
 */
@Entity
@Table(name = "player_exec_log")
public class PlayerExecLog implements java.io.Serializable {

	// Fields

	private Long id;
	private Player player;	//球员
	private Integer optype;	//操作类型
	private String opdesc;	//操作描述  比如用户登陆
	private String beanName;	//操作bean名称
	private Long beanId;		//beanId
	private Timestamp optime;

	// Constructors

	/** default constructor */
	public PlayerExecLog() {
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

	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Column(name = "optype")
	public Integer getOptype() {
		return this.optype;
	}

	public void setOptype(Integer optype) {
		this.optype = optype;
	}

	@Column(name = "opdesc", length = 100)
	public String getOpdesc() {
		return this.opdesc;
	}

	public void setOpdesc(String opdesc) {
		this.opdesc = opdesc;
	}

	@Column(name = "bean_name", length = 50)
	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Column(name = "bean_id")
	public Long getBeanId() {
		return this.beanId;
	}

	public void setBeanId(Long beanId) {
		this.beanId = beanId;
	}

	@Column(name = "optime", length = 19)
	public Timestamp getOptime() {
		return this.optime;
	}

	public void setOptime(Timestamp optime) {
		this.optime = optime;
	}

}