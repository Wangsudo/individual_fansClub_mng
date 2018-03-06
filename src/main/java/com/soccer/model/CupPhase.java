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
 * CupPhase entity. @author MyEclipse Persistence Tools
 * 比赛阶段 比如 预选赛, 决赛
 */
@Entity
@Table(name = "cup_phase")
public class CupPhase implements java.io.Serializable {

	// Fields

	private Long id;
	private Long cupId;
	private String name;  //名称
	private Set<CupGroup> groups;

	// Constructors

	/** default constructor */
	public CupPhase() {
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
	
	@Column(name = "cup_id")
	public Long getCupId() {
		return this.cupId;
	}

	public void setCupId(Long cupId) {
		this.cupId = cupId;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="phase_id")
	@OrderBy(value = "id ASC")
	public Set<CupGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<CupGroup> groups) {
		this.groups = groups;
	}

}