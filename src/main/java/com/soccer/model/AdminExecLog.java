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
 * AdminExecLog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "admin_exec_log")
public class AdminExecLog implements java.io.Serializable {

	// Fields

	private Long id;
	private AdminUser admin;
	private String opdesc;//operation desc
	private String beanName;
	private Long beanId;
	private Timestamp optime;

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

	@JoinColumn(name = "admin_id", referencedColumnName = "id")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne  
	public AdminUser getAdmin() {
		return admin;
	}

	public void setAdmin(AdminUser admin) {
		this.admin = admin;
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