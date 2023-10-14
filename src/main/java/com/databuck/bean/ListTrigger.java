package com.databuck.bean;

public class ListTrigger {

	private long idTrigger;
	private String schemaName;
	private String validationCheck;
	private String scheduleName;
	private String triggerType;
	private String projectName;
	
	
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getValidationCheck() {
		return validationCheck;
	}
	public void setValidationCheck(String validationCheck) {
		this.validationCheck = validationCheck;
	}
	public long getIdTrigger() {
		return idTrigger;
	}
	public void setIdTrigger(long idTrigger) {
		this.idTrigger = idTrigger;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
