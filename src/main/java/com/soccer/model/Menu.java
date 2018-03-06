package com.soccer.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Resource entity. @author MyEclipse Persistence Tools
 * 菜单表
 */
@Entity
@Table(name = "menu")
public class Menu implements java.io.Serializable {

	// Fields

	private Long id;
	private String code; //菜单代码
	private String name;	//名称
	private String page;	//指向的页面
	private String pcode;	//父节点的code
	private Integer power;	//包括的所有操作项
	private Set<Menu> items;	//子菜单

	// Constructors

	/** default constructor */
	public Menu() {
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

	@Column(name = "code", length = 10)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name", length = 30)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "page", length = 30)
	public String getPage() {
		return this.page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	@Column(name = "pcode", length = 10)
	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	@Column(name = "power")
	public Integer getPower() {
		return this.power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="pcode",referencedColumnName ="code")
	@OrderBy(value = "code ASC")
	public Set<Menu> getItems() {
		return items;
	}

	public void setItems(Set<Menu> items) {
		this.items = items;
	}
}