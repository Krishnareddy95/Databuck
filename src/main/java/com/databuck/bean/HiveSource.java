package com.databuck.bean;

public class HiveSource {
	private long idHiveSource;
	private String name;
	private String description;
	private long idDataSchema;
	private String tableName;
	public long getIdHiveSource() {
		return idHiveSource;
	}
	public void setIdHiveSource(long idHiveSource) {
		this.idHiveSource = idHiveSource;
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
	public long getIdDataSchema() {
		return idDataSchema;
	}
	public void setIdDataSchema(long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
