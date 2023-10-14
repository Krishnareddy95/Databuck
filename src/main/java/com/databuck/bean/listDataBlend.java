package com.databuck.bean;

public class listDataBlend {
private long idDataBlend;
private long idData;
private long idColumn;
private String name;
private String description;
private String expression;
private String columnName;
private String derivedColType;
private String columnValue;
private String columnValueType;
private String filterName;
private long idUser;
private String rowAddExpression;

private String createdByUser;

public String getCreatedByUser() {
	return createdByUser;
}
public void setCreatedByUser(String createdByUser) {
	this.createdByUser=createdByUser;
}

public long getIdDataBlend() {
	return idDataBlend;
}
public void setIdDataBlend(long idDataBlend) {
	this.idDataBlend = idDataBlend;
}
public long getIdData() {
	return idData;
}
public void setIdData(long idData) {
	this.idData = idData;
}
public long getIdColumn() {
	return idColumn;
}
public void setIdColumn(long idColumn) {
	this.idColumn = idColumn;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getExpression() {
	return expression;
}
public void setExpression(String expression) {
	this.expression = expression;
}
public String getColumnName() {
	return columnName;
}
public void setColumnName(String columnName) {
	this.columnName = columnName;
}
public String getDerivedColType() {
	return derivedColType;
}
public void setDerivedColType(String derivedColType) {
	this.derivedColType = derivedColType;
}
public String getColumnValue() {
	return columnValue;
}
public void setColumnValue(String columnValue) {
	this.columnValue = columnValue;
}
public String getColumnValueType() {
	return columnValueType;
}
public void setColumnValueType(String columnValueType) {
	this.columnValueType = columnValueType;
}
public String getFilterName() {
	return filterName;
}
public void setFilterName(String filterName) {
	this.filterName = filterName;
}
public long getIdUser() {
	return idUser;
}
public void setIdUser(long idUser) {
	this.idUser = idUser;
}
public String getRowAddExpression() {
	return rowAddExpression;
}
public void setRowAddExpression(String rowAddExpression) {
	this.rowAddExpression = rowAddExpression;
}

}
