package com.soccer.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CupTeam entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "cup_team")
public class CupTeam implements java.io.Serializable {

	// Fields

	private Long id;
	private Long cupId;
	private Long teamId;
	private String teamTitle;

	// Constructors

	/** default constructor */
	public CupTeam() {
	}

	/** minimal constructor */
	public CupTeam(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CupTeam(Long id, Long cupId, Long teamId, String teamTitle) {
		this.id = id;
		this.cupId = cupId;
		this.teamId = teamId;
		this.teamTitle = teamTitle;
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

	@Column(name = "team_id")
	public Long getTeamId() {
		return this.teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@Column(name = "team_title", length = 50)
	public String getTeamTitle() {
		return this.teamTitle;
	}

	public void setTeamTitle(String teamTitle) {
		this.teamTitle = teamTitle;
	}

}