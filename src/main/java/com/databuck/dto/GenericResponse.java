package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GenericResponse<T> {

	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;
	@ApiModelProperty(notes = "Result")
	private T result;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	
	
	
}
