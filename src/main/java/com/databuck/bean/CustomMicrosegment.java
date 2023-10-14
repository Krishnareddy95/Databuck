package com.databuck.bean;

public class CustomMicrosegment {
	private long id;
	private long templateId;
	private String checkName;
	private String microsegmentColumns;
	private String checkEnabledColumns;

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMicrosegmentColumns() {
		return microsegmentColumns;
	}

	public void setMicrosegmentColumns(String microsegmentColumns) {
		this.microsegmentColumns = microsegmentColumns;
	}

	public String getCheckEnabledColumns() {
		return checkEnabledColumns;
	}

	public void setCheckEnabledColumns(String checkEnabledColumns) {
		this.checkEnabledColumns = checkEnabledColumns;
	}

}
