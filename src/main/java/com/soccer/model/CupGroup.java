package com.soccer.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

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
 * CupGroup entity. @author MyEclipse Persistence Tools
 * 小组赛 ,如A组, B组.
 */
@Entity
@Table(name = "cup_group")
public class CupGroup implements java.io.Serializable {

	// Fields

	private Long id;
	private Long phaseId;
	private String name;  //小组赛名称

	// Constructors

	/** default constructor */
	public CupGroup() {
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

	@Column(name = "phase_id")
	public Long getPhaseId() {
		return this.phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}
	

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}