package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class RunSchemaResponse {
	
	@ApiModelProperty(notes = "Schema Id")
	private long idDataSchema;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Message")
	private String message;
	@ApiModelProperty(notes = "Unique Id")
	private String uniqueId;
	
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
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
