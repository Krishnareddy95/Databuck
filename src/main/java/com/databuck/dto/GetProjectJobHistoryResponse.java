package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetProjectJobHistoryResponse {

	@ApiModelProperty(notes = "History")
	private ArrayList<ProjectJobHistoryDto> history;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;
	
	public ArrayList<ProjectJobHistoryDto> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<ProjectJobHistoryDto> history) {
		this.history = history;
	}
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
	
	
}
