package com.soccer.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Logo entity. @author MyEclipse Persistence Tools
 * 新建球队时给队长选择的默认的 logo
 */
@Entity
@Table(name = "logo")
public class Logo implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;	//logo名称
	private String url;		//logo 的url
	private Timestamp opTime;	
	private Long op;

	// Constructors

	/** default constructor */
	public Logo() {
	}

	/** full constructor */
	public Logo(String name, String url, Timestamp opTime, Long op) {
		this.name = name;
		this.url = url;
		this.opTime = opTime;
		this.op = op;
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

	@Column(name = "url", length = 200)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
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