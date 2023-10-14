package com.databuck.dto;

import java.util.List;

public class TableListforSchema {

	private List<String> tableNameList;
	private List<Long> schemaIdList;
	
	public List<String> getTableNameList() {
		return tableNameList;
	}
	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}
	public List<Long> getSchemaIdList() {
		return schemaIdList;
	}
	public void setSchemaIdList(List<Long> schemaIdList) {
		this.schemaIdList = schemaIdList;
	}
	
	public TableListforSchema(List<String> tableNameList, List<Long> schemaIdList) {
		super();
		this.tableNameList = tableNameList;
		this.schemaIdList = schemaIdList;
	}
	
	@Override
	public String toString() {
		return "TableListforSchema [tableNameList=" + tableNameList + ", schemaIdList=" + schemaIdList + "]";
	}
	
	
}
