package com.soccer.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Honor entity. @author MyEclipse Persistence Tools
 * 荣誉表, 包括球员与球队的
 */
@Entity
@Table(name = "honor")
public class Honor implements java.io.Serializable {

	// Fields

	private Long id;
	private String name;  //荣誉名称
	private Integer type;	//类型:honorTypeList
	private String url;		//图标url
	private String content;	//内容
	private Timestamp opTime;
	private Long op;

	// Constructors

	/** default constructor */
	public Honor() {
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

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	@Column(name = "content", length = 500)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
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