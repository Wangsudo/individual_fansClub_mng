package com.soccer.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TabletRole entity. @author MyEclipse Persistence Tools
 * 后台管理员角色表
 */
@Entity
@Table(name = "role")
public class Role implements java.io.Serializable {

	// Fields

	private Long id;
	private String roleName; //角色名称
	private String roleDesc;	//描述
	private Set<RoleMenu> menus; //菜单
	private Long count;

	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Role(Long id,String roleName,  Set<RoleMenu> menus,Long count) {
		super();
		this.id = id;
		this.roleName = roleName;
		this.menus = menus;
		this.count = count;
	}



	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ROLE_NAME", nullable = false, length = 30)
	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Column(name = "ROLE_DESC", length = 500)
	public String getRoleDesc() {
		return this.roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	
	@Transient
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="role_id")
	public Set<RoleMenu> getMenus() {
		return menus;
	}

	public void setMenus(Set<RoleMenu> menus) {
		this.menus = menus;
	}

}