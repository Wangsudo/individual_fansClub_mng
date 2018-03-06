package com.soccer.util;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class Search {

	private String name;
	private String orderby;
	private String title;
	private String password;
	private String phone;
	private String email;
	private Integer type;
	private String scores;
	
	
	private Long dictId;
	private Long id;
	private String content;
	private Timestamp optime;
	private String typename;
	private Long dismisser;
	private Long teamId;
	private Long playerId;
	private Long adminId;
	
	private Timestamp loginTime;
	private String ip;
	
	private String account;
	
	private Integer pageSize;
	private Integer currentPage;
	private Integer status;
	private Integer gameStatus;
	private Integer viewType;
	
	private String teamTitle;
	private Integer teamType;
	private Integer isPublic ;
	private Integer isCaptain ;
	private Integer isOpen ;
	//已解散
	private Integer isDismissed;
	//是否官方
	private Integer official;
	
	/**
	 * 是否启用
	 */
	private Integer isEnabled;
	/**
	 * 图片位置 
	 */
	private Integer position;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
	private Date fromDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
	private Date toDate;
	
	/**
	 * hql 片断
	 * @return
	 */
	private String condition;
	/**
	 * 模糊字段查询
	 */
	private String snippet;

	public Long getDictId() {
		return dictId;
	}

	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	public String getContent() {
		return content;
	}

	public Long getDismisser() {
		return dismisser;
	}

	public void setDismisser(Long dismisser) {
		this.dismisser = dismisser;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getOptime() {
		return optime;
	}

	public void setOptime(Timestamp optime) {
		this.optime = optime;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getCurrentPage() {
		if(currentPage!=null){
			return currentPage;
		}
		return 1;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getPhone() {
		return phone;
	}

	public String getScores() {
		return scores;
	}

	public void setScores(String scores) {
		this.scores = scores;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(Integer gameStatus) {
		this.gameStatus = gameStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Integer isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Integer getPageSize() {
		if(pageSize!=null){
			return pageSize;
		}
		return 10;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTeamTitle() {
		return teamTitle;
	}

	public void setTeamTitle(String teamTitle) {
		this.teamTitle = teamTitle;
	}


	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getTeamType() {
		return teamType;
	}

	public void setTeamType(Integer teamType) {
		this.teamType = teamType;
	}

	public Integer getViewType() {
		return viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	public Integer getIsCaptain() {
		return isCaptain;
	}

	public void setIsCaptain(Integer isCaptain) {
		this.isCaptain = isCaptain;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	public Integer getIsDismissed() {
		return isDismissed;
	}

	public void setIsDismissed(Integer isDismissed) {
		this.isDismissed = isDismissed;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Integer getOfficial() {
		return official;
	}

	public void setOfficial(Integer official) {
		this.official = official;
	}
	
}
