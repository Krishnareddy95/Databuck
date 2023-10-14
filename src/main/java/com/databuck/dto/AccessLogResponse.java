package com.databuck.dto;

import java.util.ArrayList;
import java.util.Map;

public class AccessLogResponse {

	private String message;
	private String status;
	private ArrayList<Map<String, Object>> result;
	
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
	public ArrayList<Map<String, Object>> getResult() {
		return result;
	}
	public void setResult(ArrayList<Map<String, Object>> arrayList) {
		this.result = arrayList;
	}
	
	
}
