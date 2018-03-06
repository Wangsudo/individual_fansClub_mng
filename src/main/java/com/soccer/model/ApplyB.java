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
 * Apply entity. @author MyEclipse Persistence Tools
 * 参考 Apply 类, 对应的是同一张表. 只是母子表的区别
 */
@Entity
@Table(name = "apply")
public class ApplyB implements java.io.Serializable {

	// Fields

	private Long id;
	private String title;
	private String content;
	private Player player;
	private Integer dreamType;
	private Integer isPublic;
	private Timestamp applyTime;
	private Integer isEnabled;
	private Integer isOpen;
	// Constructors

	/** default constructor */
	public ApplyB() {
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

	@Column(name = "title", length = 100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content", length = 1000)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@JoinColumn(name = "player_id", referencedColumnName = "ID")
	@NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne    
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Column(name = "dream_type")
	public Integer getDreamType() {
		return this.dreamType;
	}

	public void setDreamType(Integer dreamType) {
		this.dreamType = dreamType;
	}

	@Column(name = "is_public")
	public Integer getIsPublic() {
		return this.isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	@Column(name = "apply_time", length = 19)
	public Timestamp getApplyTime() {
		return this.applyTime;
	}

	public void setApplyTime(Timestamp applyTime) {
		this.applyTime = applyTime;
	}

	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	@Column(name = "is_open")
	 public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}
	
}