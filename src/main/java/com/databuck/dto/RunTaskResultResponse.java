package com.databuck.dto;

import java.util.List;
import java.util.Map;

public class RunTaskResultResponse {

	private String message;
	private String status;
	List<Map<String, String>> listOfAppIdNUniqueId;
	
	
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
	public List<Map<String, String>> getListOfAppIdNUniqueId() {
		return listOfAppIdNUniqueId;
	}
	public void setListOfAppIdNUniqueId(List<Map<String, String>> listOfAppIdNUniqueId) {
		this.listOfAppIdNUniqueId = listOfAppIdNUniqueId;
	}
	
	
	
}
