package com.soccer.model;
// default package

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;


/**
 * Youku entity. @author MyEclipse Persistence Tools
 * 优酷视频表
 */
@Entity
@Table(name="football_video")

public class Video  implements java.io.Serializable {


    // Fields    

     private Long id;
 	 private Player player; //上传的球员
     private String screenshot; //截图
     private String videoDiv;	//视频链接
     private String title;	//标题
     private String comment;	//评论
     private Integer type;	 //类型  暂时未用.
     private Integer viewType;  // 可见范围  viewTypeList
     private Integer isEnabled;  //是否前端显示
     private Timestamp createTime;	
     private Timestamp modifyTime;
     private Timestamp auditTime;
     private Long auditor;  
     private Long creater;
     private Long modifier;
     private Integer auditStatus; //审核状态
     private Integer viewTimes;   //观看次数
 	 private String likes;		//点赞的球员, 以,分隔
 	 private Long commentCount;	//评论条数

    // Constructors

    /** default constructor */
    public Video() {
    }

    public Video(Long id, Player player, String screenshot, String videoDiv,
			String title, String comment,Integer auditStatus,Integer isEnabled,Date createTime, String likes,Integer viewTimes,  Long commentCount) {
		super();
		this.id = id;
		this.player = player;
		this.screenshot = screenshot;
		this.videoDiv = videoDiv;
		this.title = title;
		this.comment = comment;
		this.auditStatus = auditStatus;
		this.isEnabled = isEnabled;
		this.likes = likes;
		this.viewTimes = viewTimes;
		this.createTime = new Timestamp(createTime.getTime());
		this.commentCount = commentCount;
	}

	// Property accessors
    @Id 
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    // 上传视频球员（队长）
   	@JoinColumn(name = "player_id", referencedColumnName = "ID")
   	@ManyToOne    
   	@NotFound(action=NotFoundAction.IGNORE)
   	public Player getPlayer() {
   		return player;
   	}

   	public void setPlayer(Player player) {
   		this.player = player;
   	}
    
    @Column(name="screenshot", length=200)

    public String getScreenshot() {
        return this.screenshot;
    }
    
    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }
    
    @Column(name="video_div", length=200)

    public String getVideoDiv() {
        return this.videoDiv;
    }
    
    public void setVideoDiv(String videoDiv) {
        this.videoDiv = videoDiv;
    }
    
	@Column(name = "title", length = 100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
    @Column(name="comment", length=200)
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Column(name="type")

    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
	@Column(name = "is_enabled")
	public Integer getIsEnabled() {
		return this.isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}
	
    @Column(name="create_time", length=19)
    public Timestamp getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    @Column(name="modify_time", length=19)
    public Timestamp getModifyTime() {
        return this.modifyTime;
    }
    
    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    @Column(name="audit_time", length=19)

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

	@Transient
	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
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