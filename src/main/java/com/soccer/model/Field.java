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
 * Field entity. @author MyEclipse Persistence Tools
 * 场地
 */
@Entity
@Table(name="field")

public class Field  implements java.io.Serializable {


    // Fields    

	private Long id;
	private String name;
	private String address; //地址
	private String url;	//图片url
	private String contact;	//联系人
	private String mobile;	//号码
	private String content;	//内容
	private Timestamp opTime;
	private Long op;
	private Long adminId;  // 管理此场地的管理员

    // Constructors

    /** default constructor */
    public Field() {
    }

	/** minimal constructor */
    public Field(Long id) {
        this.id = id;
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
    
    @Column(name="name", length=100)

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="address", length=200)

    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Column(name="url", length=500)

    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Column(name="contact", length=100)

    public String getContact() {
        return this.contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    @Column(name="mobile", length=20)

    public String getMobile() {
        return this.mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    @Column(name="content", length=2000)

    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Column(name="op_time", length=19)
    public Timestamp getOpTime() {
		return opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}

	@Column(name = "op")
	public Long getOp() {
		return op;
	}

	public void setOp(Long op) {
		this.op = op;
	}

	@Column(name = "admin_id")
	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
}