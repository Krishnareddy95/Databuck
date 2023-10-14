package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GenericRestResponse<T> {

	@ApiModelProperty(notes = "Result")
	private T result;
	@ApiModelProperty(notes = "Status")
	private String fail;
	
	
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	
	
	
}
