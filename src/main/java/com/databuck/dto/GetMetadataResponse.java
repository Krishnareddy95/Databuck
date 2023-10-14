package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class GetMetadataResponse {

	@ApiModelProperty(notes = "Metadata")
	private ArrayList<GetMetadataResponseDto> metadata;
	@ApiModelProperty(notes = "Template Info")
	private TemplateInfoDto templateInfo;
	@ApiModelProperty(notes = "Connection Info")
	private ConnectionInfoDto connectionInfo;
	@ApiModelProperty(notes = "Status")
	private String fail;

	public ArrayList<GetMetadataResponseDto> getMetadata() {
		return metadata;
	}

	public void setMetadata(ArrayList<GetMetadataResponseDto> metadata) {
		this.metadata = metadata;
	}

	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}

	public TemplateInfoDto getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfoDto templateInfo) {
		this.templateInfo = templateInfo;
	}

	public ConnectionInfoDto getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfoDto connectionInfo) {
		this.connectionInfo = connectionInfo;
	}
	

}
