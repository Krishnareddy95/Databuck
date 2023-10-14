package com.databuck.csvmodel;

public class DuplicateSummary {
	private String id;
	private String idApp;
	private String date;
	private String run;
	private String type;
	private String dGroupCol;
	private String dGroupVal;
	private String duplicateCheckFields;
	private String duplicate;
	private String totalCount;
	private String percentage;
	private String threshold;
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMicroCol() {
		return dGroupCol;
	}

	public void setdGroupCol(String dGroupCol) {
		this.dGroupCol = dGroupCol;
	}

	public String getMicroVal() {
		return dGroupVal;
	}

	public void setdGroupVal(String dGroupVal) {
		this.dGroupVal = dGroupVal;
	}

	public String getDuplicateCheckFields() {
		return duplicateCheckFields;
	}

	public void setDuplicateCheckFields(String duplicateCheckFields) {
		this.duplicateCheckFields = duplicateCheckFields;
	}

	public String getDuplicate() {
		return duplicate;
	}

	public void setDuplicate(String duplicate) {
		this.duplicate = duplicate;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
