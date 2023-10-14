package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class RunValidationResponse {

	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Reason")
	private String reason;
	@ApiModelProperty(notes = "Unique ID")
	private String uniqueId;
	@ApiModelProperty(notes = "App Run Status")
	private String appRunStatus;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public String getAppRunStatus() {
		return appRunStatus;
	}
	public void setAppRunStatus(String appRunStatus) {
		this.appRunStatus = appRunStatus;
	}
	
	
}
