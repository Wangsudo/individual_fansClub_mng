package com.soccer.model;
// default package

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Photo entity. @author MyEclipse Persistence Tools
 * 照片表(子表)
 */
@Entity
@Table(name="football_photo")

public class Photo  implements java.io.Serializable {

    // Fields    
     private Long id;
     private Long groupId;
     private String name;  //名称
     private String url;	//图片url
     private Integer seq;	//图片序号
     private Integer status; //状态 ,是否通过  ,可单独让某张图片不通过审核

    // Constructors

    /** default constructor */
    public Photo() {
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
    
    @Column(name="group_id")

    public Long getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    @Column(name="name", length=100)

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="url", length=200)

    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
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

}