package com.databuck.dto;

import java.util.List;

public class AppGroupNamesRequest {

	private String message;
	private String status;
	private List<String> result;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getResult() {
		return result;
	}
	public void setResult(List<String> appGroupList) {
		this.result = appGroupList;
	}
	
	
}
