package com.databuck.dto;

import java.util.List;

import com.databuck.bean.ListAppGroup;

public class AppGroupsRequest {

	private String message;
	private String status;
	private List<ListAppGroup> result;
	
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
	public List<ListAppGroup> getResult() {
		return result;
	}
	public void setResult(List<ListAppGroup> listAppGroupData) {
		this.result = listAppGroupData;
	}
	
	
}
