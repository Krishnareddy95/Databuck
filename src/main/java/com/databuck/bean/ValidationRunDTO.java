package com.databuck.bean;

public class ValidationRunDTO {

	private long idApp;
	private String uniqueId;
	private String execDate;
	private long run;
	private String testRun;
	private String status;

	public long getIdApp() {
		return idApp;
	}

	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getExecDate() {
		return execDate;
	}

	public void setExecDate(String execDate) {
		this.execDate = execDate;
	}

	public long getRun() {
		return run;
	}

	public void setRun(long run) {
		this.run = run;
	}

	public String getTestRun() {
		return testRun;
	}

	public void setTestRun(String testRun) {
		this.testRun = testRun;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
