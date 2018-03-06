package com.soccer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;


/**
 * FootballMessages entity. @author MyEclipse Persistence Tools
 * 签到信与站内信表
 */
@Entity
@Table(name = "message")
public class Message implements java.io.Serializable {

	// Fields

	private Long id;
	private String title; //标题
	private String content;	//内容
	private String comment;	//评论
	private String address;	//地址(签到信才有)
	private Team team;	    //发布的球队
	private Integer messageType;	//消息类型  messageTypeList
	private Timestamp beginTime;  	//活动开始时间(签到信才有)
	private Long op;
	private Timestamp opTime;
	private Integer isEnabled;	//是否前端显示
	private Set<MessageTank> tanks;	//哪些球员将收到消息
	// Constructors

	/** default constructor */
	public Message() {
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
	
	@Column(name = "comment", length = 200)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "address", length = 100)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "message_type")
	public Integer getMessageType() {
		return this.messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}


	@Column(name = "begin_time", length = 19)
	public Timestamp getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
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

	@Column(name = "op")
	public Long getOp() {
		return op;
	}

	public void setOp(Long op) {
		this.op = op;
	}

	@Column(name = "op_time", length = 19)
	public Timestamp getOpTime() {
		return opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}

	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	//签到信息
	 @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
     @JoinColumn(name="message_id")
	 @OrderBy(value = "confirmTime ASC")
	public Set<MessageTank> getTanks() {
		return tanks;
	}

	public void setTanks(Set<MessageTank> tanks) {
		this.tanks = tanks;
	}

}