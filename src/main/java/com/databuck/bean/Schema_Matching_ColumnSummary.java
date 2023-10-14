package com.databuck.bean;

public class Schema_Matching_ColumnSummary {

	private Long id;
	private String date;
	private String tableName;
	private String columnsinsleftsourcetable;
	private String missingcolumnsinrightsourcetable;
	private String extracolumnsinrightsourcetable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnsinsleftsourcetable() {
		return columnsinsleftsourcetable;
	}

	public void setColumnsinsleftsourcetable(String columnsinsleftsourcetable) {
		this.columnsinsleftsourcetable = columnsinsleftsourcetable;
	}

	public String getMissingcolumnsinrightsourcetable() {
		return missingcolumnsinrightsourcetable;
	}

	public void setMissingcolumnsinrightsourcetable(String missingcolumnsinrightsourcetable) {
		this.missingcolumnsinrightsourcetable = missingcolumnsinrightsourcetable;
	}

	public String getExtracolumnsinrightsourcetable() {
		return extracolumnsinrightsourcetable;
	}

	public void setExtracolumnsinrightsourcetable(String extracolumnsinrightsourcetable) {
		this.extracolumnsinrightsourcetable = extracolumnsinrightsourcetable;
	}

}
