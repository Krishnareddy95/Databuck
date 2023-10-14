package com.databuck.bean;

public class data_source {
	
	private int id;
	private String name;
	private String type;
	private String FORMAT;
	private String url;
	private String username;
	private String password;
	private String filter;
	private long app_id;
	private int seq;
	private String checkSource;
	private String RowAddSource;
	private String query;
	private String incrementalType;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getIncrementalType() {
		return incrementalType;
	}
	public void setIncrementalType(String incrementalType) {
		this.incrementalType = incrementalType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFORMAT() {
		return FORMAT;
	}
	public void setFORMAT(String fORMAT) {
		FORMAT = fORMAT;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getCheckSource() {
		return checkSource;
	}
	public void setCheckSource(String checkSource) {
		this.checkSource = checkSource;
	}
	public String getRowAddSource() {
		return RowAddSource;
	}
	public void setRowAddSource(String rowAddSource) {
		RowAddSource = rowAddSource;
	}
	public long getApp_id() {
		return app_id;
	}
	public void setApp_id(long app_id) {
		this.app_id = app_id;
	}
	
}
