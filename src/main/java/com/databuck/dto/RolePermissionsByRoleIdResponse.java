package com.databuck.dto;

import org.json.JSONObject;

public class RolePermissionsByRoleIdResponse {

	private String message;
	private String status;
	private JSONObject result;
	
	
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
	public JSONObject getResult() {
		return result;
	}
	public void setResult(JSONObject result) {
		this.result = result;
	}
	
	
}
