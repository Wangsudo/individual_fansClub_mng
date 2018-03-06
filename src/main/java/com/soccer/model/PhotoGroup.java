package com.soccer.model;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * PhotoGroup entity. @author MyEclipse Persistence Tools
 * 照片表(父表)
 */
@Entity
@Table(name = "football_photo_group")
public class PhotoGroup implements java.io.Serializable {

	// Fields

	private Long id;
	private Player player;	//上传者
	private String comment;	//内容
	private Integer type;	//类型   uploadTypeList
	private Integer isEnabled;   //是否被禁止
	private Integer auditStatus;	//后台审核状态
	private Integer viewType;		//可见范围 (队内或广场 ) viewTypeList
	private Set<Photo> pics;		//包含的照 片
	private Integer viewTimes;	 //被查看次数
	private String likes;		 //喜欢此图片的 球员,以","分隔
	private Timestamp createTime;	
	private Timestamp modifyTime;
	private Timestamp auditTime;
	private Long auditor;  //审核 者
	private Long creater;
	private Long modifier;
	// Constructors

	/** default constructor */
	public PhotoGroup() {
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
	
	
    // 上传相片球员
	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@ManyToOne    
	@NotFound(action=NotFoundAction.IGNORE)
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Column(name = "comment", length = 200)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Column(name = "create_time", length = 19)
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Column(name = "modify_time", length = 19)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Column(name = "audit_time", length = 19)
	public Timestamp getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Timestamp auditTime) {
		this.auditTime = auditTime;
	}

	@Column(name = "audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Column(name = "view_type")
	public Integer getViewType() {
		return viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
    @JoinColumn(name="group_id")
	public Set<Photo> getPics() {
		return pics;
	}
	public void setPics(Set<Photo> pics) {
		this.pics = pics;
	}
	
	@Column(name = "view_times")
	public Integer getViewTimes() {
		return viewTimes;
	}



	public void setViewTimes(Integer viewTimes) {
		this.viewTimes = viewTimes;
	}

	@Column(name = "likes")
	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}
	
	@Column(name = "auditor")
	public Long getAuditor() {
		return this.auditor;
	}

	public void setAuditor(Long auditor) {
		this.auditor = auditor;
	}
		@Column(name = "modifier")
	public Long getModifier() {
		return this.modifier;
	}

	public void setModifier(Long modifier) {
		this.modifier = modifier;
	}
	
	@Column(name = "creater")
	public Long getCreater() {
		return this.creater;
	}

	public void setCreater(Long creater) {
		this.creater = creater;
	}
	
	

}