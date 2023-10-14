package com.databuck.dto;

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;

public class LoginResponse {

	@ApiModelProperty(notes = "Message")
	private String message;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Result")
	private Map<String, Object> result;
	@ApiModelProperty(notes = "Token which is use for autherization")
	private String token;
	
		public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
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
	public Map<String, Object> getResult() {
		return result;
	}
	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
}
