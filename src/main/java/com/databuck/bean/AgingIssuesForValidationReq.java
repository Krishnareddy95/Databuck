package com.databuck.bean;

import io.swagger.annotations.ApiModelProperty;

public class AgingIssuesForValidationReq {

	@ApiModelProperty(notes = "Hive Server Name")
	private String hiveserverName;
	@ApiModelProperty(notes = "Hive Server Port")
	private long hiveserverport;
	@ApiModelProperty(notes = "Validation Id")
	private long validationId;
	@ApiModelProperty(notes = "DQR Id")
	private String dqrId;

	public String getHiveserverName() {
		return hiveserverName;
	}

	public void setHiveserverName(String hiveserverName) {
		this.hiveserverName = hiveserverName;
	}

	public long getHiveserverport() {
		return hiveserverport;
	}

	public void setHiveserverport(long hiveserverport) {
		this.hiveserverport = hiveserverport;
	}

	public long getValidationId() {
		return validationId;
	}

	public void setValidationId(long validationId) {
		this.validationId = validationId;
	}

	public String getDqrId() {
		return dqrId;
	}

	public void setDqrId(String dqrId) {
		this.dqrId = dqrId;
	}

}
