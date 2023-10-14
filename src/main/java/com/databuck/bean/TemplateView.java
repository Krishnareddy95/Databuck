package com.databuck.bean;

import java.io.Serializable;
import java.util.Date;

public class TemplateView implements Serializable {

	private int idDataBlend;
	private int idData;
	private String name;
	private String lbdescription;
	private String lsdescription;
	private Date createdAt;
	private int projectId;
	private String projectName;

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private String createdByUser;

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLbdescription() {
		return lbdescription;
	}

	public void setLbdescription(String lbdescription) {
		this.lbdescription = lbdescription;
	}

	public String getLsdescription() {
		return lsdescription;
	}

	public void setLsdescription(String lsdescription) {
		this.lsdescription = lsdescription;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getIdDataBlend() {
		return idDataBlend;
	}

	public void setIdDataBlend(int idDataBlend) {
		this.idDataBlend = idDataBlend;
	}

	public int getIdData() {
		return idData;
	}

	public void setIdData(int idData) {
		this.idData = idData;
	}

	public TemplateView() {
	
		this.name = name;
		this.lbdescription = lbdescription;
		this.lsdescription = lsdescription;
		this.createdAt = createdAt;
		this.idDataBlend = idDataBlend;
		this.idData = idData;
	}

	
	
	
}