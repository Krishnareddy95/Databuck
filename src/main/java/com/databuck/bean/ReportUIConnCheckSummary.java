package com.databuck.bean;

import java.util.List;

public class ReportUIConnCheckSummary {

	private long connectionId;
	private String connectionName;
	private String displayName;
	private int displayOrder;
	private List<ReportUIComponentCheckSummary> checkList;

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<ReportUIComponentCheckSummary> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<ReportUIComponentCheckSummary> checkList) {
		this.checkList = checkList;
	}

}
