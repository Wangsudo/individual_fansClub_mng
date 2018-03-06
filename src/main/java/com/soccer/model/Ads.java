package com.soccer.model;

// default package

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Ads entity. @author MyEclipse Persistence Tools
 * 广告表: 包括banner位置 , 下面横条,两边的侧边栏广告
 */
@Entity
@Table(name = "football_ads")
public class Ads implements java.io.Serializable {
	 // Fields    
    private Long id;
    private Integer position; //位置 具体显字典表
    private Integer seq;	  //同一位置的图片位置 positionList
    private Integer status;	  //状态 详见 adsStatusList
    private String content;   //内容
    private String title;		//标题
    private String url;		  //图片路径
    private Timestamp createTime;	
    private Long creater;
    private Timestamp modifyTime;
    private Long modifier;
    private Integer pointType;	//指向类型  pointTypeList
    private Long pointId;		//指向 bean的id
    private Timestamp startTime;	//启用时间
    private Timestamp stopTime;		//结束时间

   // Constructors

   /** default constructor */
   public Ads() {
   }

  
   // Property accessors
   @Id @GeneratedValue(strategy=IDENTITY)
   
   @Column(name="id", unique=true, nullable=false)

   public Long getId() {
       return this.id;
   }
   
   public void setId(Long id) {
       this.id = id;
   }
   
   @Column(name="position")

   public Integer getPosition() {
       return this.position;
   }
   
   public void setPosition(Integer position) {
       this.position = position;
   }
   
   @Column(name="seq")

   public Integer getSeq() {
       return this.seq;
   }
   
   public void setSeq(Integer seq) {
       this.seq = seq;
   }
   
   @Column(name="status")
   public Integer getStatus() {
	return status;
   }
	public void setStatus(Integer status) {
		this.status = status;
	}


@Column(name="content", length=2000)

   public String getContent() {
       return this.content;
   }
   
   public void setContent(String content) {
       this.content = content;
   }
   
   @Column(name="title", length=100)

   public String getTitle() {
       return this.title;
   }
   
   public void setTitle(String title) {
       this.title = title;
   }
   
   @Column(name="url", length=200)

   public String getUrl() {
       return this.url;
   }
   
   public void setUrl(String url) {
       this.url = url;
   }
   
   @Column(name="create_time", length=19)

   public Timestamp getCreateTime() {
       return this.createTime;
   }
   
   public void setCreateTime(Timestamp createTime) {
       this.createTime = createTime;
   }
   
   @Column(name="creater")

   public Long getCreater() {
       return this.creater;
   }
   
   public void setCreater(Long creater) {
       this.creater = creater;
   }
   
   @Column(name="modify_time", length=19)

   public Timestamp getModifyTime() {
       return this.modifyTime;
   }
   
   public void setModifyTime(Timestamp modifyTime) {
       this.modifyTime = modifyTime;
   }
   
   @Column(name="modifier")

   public Long getModifier() {
       return this.modifier;
   }
   
   public void setModifier(Long modifier) {
       this.modifier = modifier;
   }
   
   @Column(name="point_type")

   public Integer getPointType() {
       return this.pointType;
   }
   
   public void setPointType(Integer pointType) {
       this.pointType = pointType;
   }
   
   @Column(name="point_id")

   public Long getPointId() {
       return this.pointId;
   }
   
   public void setPointId(Long pointId) {
       this.pointId = pointId;
   }
   
   @Column(name="start_time", length=19)

   public Timestamp getStartTime() {
       return this.startTime;
   }
   
   public void setStartTime(Timestamp startTime) {
       this.startTime = startTime;
   }
   
   @Column(name="stop_time", length=19)

   public Timestamp getStopTime() {
       return this.stopTime;
   }
   
   public void setStopTime(Timestamp stopTime) {
       this.stopTime = stopTime;
   }
}