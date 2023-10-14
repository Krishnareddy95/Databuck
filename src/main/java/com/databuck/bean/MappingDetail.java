package com.databuck.bean;

public class MappingDetail {

	private long dbkRowId;
	private String tableName;
	private String leftColumn;
	private String rightColumn;
	private String defaultValue;
	private long position;

	public long getDbkRowId() {
		return dbkRowId;
	}

	public void setDbkRowId(long dbkRowId) {
		this.dbkRowId = dbkRowId;
	}

	public String getTableName() { return tableName; }

	public void setTableName(String tableName) { this.tableName = tableName; }

	public String getLeftColumn() {
		return leftColumn;
	}

	public void setLeftColumn(String leftColumn) {
		this.leftColumn = leftColumn;
	}

	public String getRightColumn() {
		return rightColumn;
	}

	public void setRightColumn(String rightColumn) {
		this.rightColumn = rightColumn;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "MappingDetail{" + "dbkRowId=" + dbkRowId + ", tableName='" + tableName + '\'' + ", leftColumn='" + leftColumn + '\'' + ", rightColumn='"
				+ rightColumn + '\'' + ", defaultValue='" + defaultValue + '\'' + ", position=" + position + '}';
	}
}
