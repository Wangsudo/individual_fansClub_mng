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
 * TabletAdminUser entity. @author MyEclipse Persistence Tools
 * 管理员表
 */
@Entity
@Table(name = "admin_user")
public class AdminUser implements java.io.Serializable {

	// Fields

	private Long id;
	private String account;  //帐号
	private String password; //密码
	private String email;	 //邮箱
	private String teleNum;	 //电话
	private String name;	 //姓名
	private String headpic;	 //头像路径
	private Long creater;	 
	private Timestamp createTime;
	private Long modifier;
	private Timestamp modifyTime;
	private Timestamp forgetPasswordTime; //忘记密码时间
	private Integer forbidden;	//禁止登陆
	private Role role;			//角色
	
	public AdminUser() {
		super();
	}

	public AdminUser(Long id, String account) {
		super();
		this.id = id;
		this.account = account;
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

	@Column(name = "account", nullable = false, length = 30)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "password", nullable = false, length = 20)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "email", length = 120)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "tele_num", length = 30)
	public String getTeleNum() {
		return this.teleNum;
	}

	public void setTeleNum(String teleNum) {
		this.teleNum = teleNum;
	}


	@Column(name = "name", length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "creater", length = 20)
	public Long getCreater() {
		return this.creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}

	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modifier", length = 20)
	public Long getModifier() {
		return this.modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}

	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	@Column(name = "forget_password_time", length = 19)
	public Timestamp getForgetPasswordTime() {
		return forgetPasswordTime;
	}

	public void setForgetPasswordTime(Timestamp forgetPasswordTime) {
		this.forgetPasswordTime = forgetPasswordTime;
	}

	@JoinColumn(name = "roleid", referencedColumnName = "id")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne    
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Column(name = "headpic")
	public String getHeadpic() {
		return headpic;
	}
	public void setHeadpic(String headpic) {
		this.headpic = headpic;
	}
	@Column(name = "forbidden")
	public Integer getForbidden() {
		return forbidden;
	}

	public void setForbidden(Integer forbidden) {
		this.forbidden = forbidden;
	}

}