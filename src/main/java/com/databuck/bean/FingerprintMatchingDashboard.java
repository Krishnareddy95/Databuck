package com.databuck.bean;

public class FingerprintMatchingDashboard {
	private Long idApp;
	private String date;
	private Long run;
	private String validationCheckName;
	private String source1;
	private String source2;
	private String rcStatus;
	private String sumStatus;
	private String meanStatus;
	private String stdDevStatus;
	public Long getIdApp() {
		return idApp;
	}
	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getRun() {
		return run;
	}
	public void setRun(Long run) {
		this.run = run;
	}
	public String getValidationCheckName() {
		return validationCheckName;
	}
	public void setValidationCheckName(String validationCheckName) {
		this.validationCheckName = validationCheckName;
	}
	public String getSource1() {
		return source1;
	}
	public void setSource1(String source1) {
		this.source1 = source1;
	}
	public String getSource2() {
		return source2;
	}
	public void setSource2(String source2) {
		this.source2 = source2;
	}
	public String getRcStatus() {
		return rcStatus;
	}
	public void setRcStatus(String rcStatus) {
		this.rcStatus = rcStatus;
	}
	public String getSumStatus() {
		return sumStatus;
	}
	public void setSumStatus(String sumStatus) {
		this.sumStatus = sumStatus;
	}
	public String getMeanStatus() {
		return meanStatus;
	}
	public void setMeanStatus(String meanStatus) {
		this.meanStatus = meanStatus;
	}
	public String getStdDevStatus() {
		return stdDevStatus;
	}
	public void setStdDevStatus(String stdDevStatus) {
		this.stdDevStatus = stdDevStatus;
	}
}
