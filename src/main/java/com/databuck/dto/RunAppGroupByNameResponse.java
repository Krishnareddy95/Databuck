package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class RunAppGroupByNameResponse {
	
	@ApiModelProperty(notes = "AppGroup Id")
	private Long idAppGroup;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;
	@ApiModelProperty(notes = "Unique Id")
	private String uniqueId;
	
	public Long getIdAppGroup() {
		return idAppGroup;
	}
	public void setIdAppGroup(Long idAppGroup) {
		this.idAppGroup = idAppGroup;
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
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	
}
