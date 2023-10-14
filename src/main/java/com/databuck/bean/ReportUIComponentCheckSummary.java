package com.databuck.bean;

public class ReportUIComponentCheckSummary {

	private String source;
	private String checkName;
	private Long appId;
	private String exectionDate;
	private Double dqi;

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getExectionDate() {
		return exectionDate;
	}

	public void setExectionDate(String exectionDate) {
		this.exectionDate = exectionDate;
	}

	public Double getDqi() {
		return dqi;
	}

	public void setDqi(Double dqi) {
		this.dqi = dqi;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
