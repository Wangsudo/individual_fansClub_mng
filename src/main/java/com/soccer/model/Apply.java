package com.soccer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Apply entity. @author MyEclipse Persistence Tools
 * 球员入队申请, 一个球员可一次申请多个球队, ApplyTank 为其子表
 */
@Entity
@Table(name = "apply")
public class Apply implements java.io.Serializable {

	// Fields

	private Long id;
	private String title;   //标题
	private String content;	//内容
	private Player player;	//球员
	private Integer dreamType;	//理想的球队类型 teamTypeList
	private Integer isPublic;   //是否在广场显示 yesnoList
	private Timestamp applyTime;  //申请时间
	private Integer isEnabled;	  //是否启用yesnoList , 默认是启用的. 但是管理员可置为否,表示禁止显示
	private Integer isOpen;		//是否在有效期内 yesnoList , 默是开的, 表示球队可以接纳该球员.  同一时间一个球员最多只有一个开放中的 入队申请
	private Set<ApplyTank> tanks; // 对各个球队的 申请.
	// Constructors

	/** default constructor */
	public Apply() {
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

	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
     @JoinColumn(name="apply_id")
	 @OrderBy(value = "confirmTime ASC")
	public Set<ApplyTank> getTanks() {
		return tanks;
	}

	public void setTanks(Set<ApplyTank> tanks) {
		this.tanks = tanks;
	}

}