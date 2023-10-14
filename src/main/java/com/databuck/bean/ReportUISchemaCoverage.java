package com.databuck.bean;

import java.util.List;

public class ReportUISchemaCoverage {
	private long connectionId;
	private String displayName;
	private int displayOrder;
	private List<ReportUITableCoverage> schemaTableList;

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
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

	public List<ReportUITableCoverage> getSchemaTableList() {
		return schemaTableList;
	}

	public void setSchemaTableList(List<ReportUITableCoverage> schemaTableList) {
		this.schemaTableList = schemaTableList;
	}

}
