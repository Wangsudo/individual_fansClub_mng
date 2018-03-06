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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Cup entity. @author MyEclipse Persistence Tools
 * 杯赛表 ,由多个比赛阶段构成, 如小组赛, 1/4决赛
 */
@Entity
@Table(name = "cup")
public class Cup implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;   //名称
	private String content;	//内容	
	private String iconUrl;	 //杯赛图标
	private Integer isPublic;	//是否公开(公开的才在网页上供队长选择)
	private Timestamp opTime;	
	private Long op;	
	private Set<CupTeam> teams;  //可参加此杯赛的球队
	private Set<CupPhase> phases;	//杯赛的阶段, 比如小组赛

	// Constructors

	/** default constructor */
	public Cup() {
	}

	/** minimal constructor */
	public Cup(Long id) {
		this.id = id;
	}
	
	public Cup(Long id,String name) {
		this.id = id;
		this.name = name;
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

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "content", length = 500)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	   
	@Column(name = "icon_url", length = 200)
	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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

	@Column(name = "op")
	public Long getOp() {
		return this.op;
	}

	public void setOp(Long op) {
		this.op = op;
	}

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="cup_id")
	public Set<CupTeam> getTeams() {
		return teams;
	}

	public void setTeams(Set<CupTeam> teams) {
		this.teams = teams;
	}

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="cup_id")
	@OrderBy(value = "id ASC")
	public Set<CupPhase> getPhases() {
		return phases;
	}

	public void setPhases(Set<CupPhase> phases) {
		this.phases = phases;
	}
	
	

}