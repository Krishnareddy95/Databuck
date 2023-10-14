package com.databuck.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserToken {

	private Long idUser;
	private String userName;
	private Long idRole;
	private String rolename;
	private String email;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date loginTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expiryTime;
	private String token;
	private String refreshtoken;
	public String getRefreshtoken() {
		return refreshtoken;
	}

	public UserToken(Long idUser, String userName, Long idRole, String rolename, String email, Date loginTime,
			Date expiryTime, String token, String refreshtoken, String tokenStatus, String activeDirectoryUser,
			String userLDAPGroups) {
		super();
		this.idUser = idUser;
		this.userName = userName;
		this.idRole = idRole;
		this.rolename = rolename;
		this.email = email;
		this.loginTime = loginTime;
		this.expiryTime = expiryTime;
		this.token = token;
		this.refreshtoken = refreshtoken;
		this.tokenStatus = tokenStatus;
		this.activeDirectoryUser = activeDirectoryUser;
		this.userLDAPGroups = userLDAPGroups;
	}

	public void setRefreshtoken(String refreshtoken) {
		this.refreshtoken = refreshtoken;
	}

	private String tokenStatus;
	private String activeDirectoryUser;
	private String userLDAPGroups;

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getIdRole() {
		return idRole;
	}

	public void setIdRole(Long idRole) {
		this.idRole = idRole;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(String tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public String getActiveDirectoryUser() {
		return activeDirectoryUser;
	}

	public void setActiveDirectoryUser(String activeDirectoryUser) {
		this.activeDirectoryUser = activeDirectoryUser;
	}

	public String getUserLDAPGroups() {
		return userLDAPGroups;
	}

	public void setUserLDAPGroups(String userLDAPGroups) {
		this.userLDAPGroups = userLDAPGroups;
	}

	public UserToken() {

	}

	public UserToken(Long idUser, String userName, Long idRole, String rolename, String email, Date loginTime,
			Date expiryTime, String token, String tokenStatus, String activeDirectoryUser, String userLDAPGroups) {
		super();
		this.idUser = idUser;
		this.userName = userName;
		this.idRole = idRole;
		this.rolename = rolename;
		this.email = email;
		this.loginTime = loginTime;
		this.expiryTime = expiryTime;
		this.token = token;
		this.tokenStatus = tokenStatus;
		this.activeDirectoryUser = activeDirectoryUser;
		this.userLDAPGroups = userLDAPGroups;
	}

}
