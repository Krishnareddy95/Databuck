package com.databuck.bean;

public class DashboardColorGrade {

	private long gradeId;
	private long domainId;
	private long projectId;
	private String color;
	private String logic;
	private double colorPercentage;

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getGradeId() {
		return gradeId;
	}

	public void setGradeId(long gradeId) {
		this.gradeId = gradeId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public double getColorPercentage() {
		return colorPercentage;
	}

	public void setColorPercentage(double colorPercentage) {
		this.colorPercentage = colorPercentage;
	}
}
