package com.databuck.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class UpdateRuleCatalogApprovalStatusRequest {

	@ApiModelProperty(notes = "App ID", required = true)
	@NotNull
	private String idApp;
	
	@ApiModelProperty(notes = "Status", required = true)
	@NotNull
	private String status;
	
	@ApiModelProperty(notes = "Approver UserName", required = true)
	@NotNull
	private String approverUserName;
	
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getApproverUserName() {
		return approverUserName;
	}
	public void setApproverUserName(String approverUserName) {
		this.approverUserName = approverUserName;
	}
	
	
	
}
