package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class ProjectJobHistoryDto {

	@ApiModelProperty(notes = "Queue Id")
	private Long queueId;
	@ApiModelProperty(notes = "Project Id")
	private Long projectId;
	@ApiModelProperty(notes = "Project Name")
	private String projectName;
	@ApiModelProperty(notes = "Status")
	private String status;
	@ApiModelProperty(notes = "Created At")
	private String createdAt;
	@ApiModelProperty(notes = "Project Unique Id")
	private String project_uniqueId;
	@ApiModelProperty(notes = "Spark Application Id")
	private String sparkApplicationId;
	@ApiModelProperty(notes = "Deploy Mode")
	private String deployMode;
	@ApiModelProperty(notes = "Start Time")
	private String startTime;
	@ApiModelProperty(notes = "End Time")
	private String endTime;
	@ApiModelProperty(notes = "Process Id")
	private Long processId;
	
	public Long getQueueId() {
		return queueId;
	}
	public void setQueueId(Long queueId) {
		this.queueId = queueId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getProject_uniqueId() {
		return project_uniqueId;
	}
	public void setProject_uniqueId(String project_uniqueId) {
		this.project_uniqueId = project_uniqueId;
	}
	public String getSparkApplicationId() {
		return sparkApplicationId;
	}
	public void setSparkApplicationId(String sparkApplicationId) {
		this.sparkApplicationId = sparkApplicationId;
	}
	public String getDeployMode() {
		return deployMode;
	}
	public void setDeployMode(String deployMode) {
		this.deployMode = deployMode;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	
	
	
}
