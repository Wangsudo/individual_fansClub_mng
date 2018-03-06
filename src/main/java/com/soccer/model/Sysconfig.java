package com.soccer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Sysconfig entity. @author MyEclipse Persistence Tools
 * 系统参数表
 */
@Entity
@Table(name = "sysconfig")
public class Sysconfig implements java.io.Serializable {

	// Fields

	private Long id;
	private String code;	//代码
	private String value;	//值
	private String comment;	//描述
	private String defaultValue;	//默认值 

	// Constructors

	/** default constructor */
	public Sysconfig() {
	}

	/** full constructor */
	public Sysconfig(String code, String value, String comment, String defaultValue) {
		this.code = code;
		this.value = value;
		this.comment = comment;
		this.defaultValue = defaultValue;
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

	@Column(name = "code", length = 20)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "value", length = 50)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "comment", length = 50)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "default_value", length = 50)
	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}