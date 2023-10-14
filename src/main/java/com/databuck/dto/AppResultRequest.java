package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class AppResultRequest {
	
	@ApiModelProperty(notes = "App Id")
	private String idApp;
	@ApiModelProperty(notes = "Execution Date")
	private String executionDate;
	@ApiModelProperty(notes = "Run")
	private String run;
	
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	public String getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	}

	
}
