package com.soccer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * RoleMenu entity. @author MyEclipse Persistence Tools
 * 角色与菜单关联表
 */
@Entity
@Table(name = "role_menu")
public class RoleMenu implements java.io.Serializable {

	// Fields

	private Long id;
	private Long roleId;
	private Integer access;  //角色 对菜单的操作权限
	private Long menuId;	

	// Constructors

	/** default constructor */
	public RoleMenu() {
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

	@Column(name = "role_id")
	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	@Column(name = "access")
	public Integer getAccess() {
		return this.access;
	}

	public void setAccess(Integer access) {
		this.access = access;
	}
	
	@Column(name = "menu_id")
	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

}