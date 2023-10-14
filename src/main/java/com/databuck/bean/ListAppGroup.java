package com.databuck.bean;

public class ListAppGroup {
	private long idAppGroup;
	private String name;
	private String description;
	private String enableScheduling;
	private long idSchedule;
	private String frequency;
	private String scheduleDay;
	private String time;
	private long projectId;
	private String projectName;
	
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public long getIdAppGroup() {
		return idAppGroup;
	}
	public void setIdAppGroup(long idAppGroup) {
		this.idAppGroup = idAppGroup;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEnableScheduling() {
		return enableScheduling;
	}
	public void setEnableScheduling(String enableScheduling) {
		this.enableScheduling = enableScheduling;
	}
	public long getIdSchedule() {
		return idSchedule;
	}
	public void setIdSchedule(long idSchedule) {
		this.idSchedule = idSchedule;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getScheduleDay() {
		return scheduleDay;
	}
	public void setScheduleDay(String scheduleDay) {
		this.scheduleDay = scheduleDay;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

}
