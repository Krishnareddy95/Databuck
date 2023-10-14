package com.databuck.bean;

import java.time.OffsetDateTime;

public class DbkFMHistory {
	private Long connection_id;
	private Long validation_id;
	private String connection_type;
	private String schema_name;
	private String table_or_subfolder_name;
	private Integer record_count;
	private String last_load_time;
	private String last_altered;
	private String file_name;
	private String account_name;
	private String container_name;
	private OffsetDateTime currentLoadTime;
	private String bucketName;
	private String folderPath;
	private long file_arrival_id;

	public long getFile_arrival_id() {
		return file_arrival_id;
	}

	public void setFile_arrival_id(long file_arrival_id) {
		this.file_arrival_id = file_arrival_id;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAccount_name() {
		return account_name;
	}
	public OffsetDateTime getCurrentLoadTime() {
		return currentLoadTime;
	}
	public void setCurrentLoadTime(OffsetDateTime currentLoadTime) {
		this.currentLoadTime = currentLoadTime;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	public String getContainer_name() {
		return container_name;
	}
	public void setContainer_name(String container_name) {
		this.container_name = container_name;
	}
	public Long getConnection_id() {
		return connection_id;
	}
	public void setConnection_id(Long connection_id) {
		this.connection_id = connection_id;
	}
	public Long getValidation_id() {
		return validation_id;
	}
	public void setValidation_id(Long validation_id) {
		this.validation_id = validation_id;
	}
	public String getConnection_type() {
		return connection_type;
	}
	public void setConnection_type(String connection_type) {
		this.connection_type = connection_type;
	}
	public String getSchema_name() {
		return schema_name;
	}
	public void setSchema_name(String schema_name) {
		this.schema_name = schema_name;
	}
	public String getTable_or_subfolder_name() {
		return table_or_subfolder_name;
	}
	public void setTable_or_subfolder_name(String table_or_subfolder_name) {
		this.table_or_subfolder_name = table_or_subfolder_name;
	}
	public Integer getRecord_count() {
		return record_count;
	}
	public void setRecord_count(Integer record_count) {
		this.record_count = record_count;
	}
	public String getLast_load_time() {
		return last_load_time;
	}
	public void setLast_load_time(String last_load_time) {
		this.last_load_time = last_load_time;
	}
	public String getLast_altered() {
		return last_altered;
	}
	public void setLast_altered(String last_altered) {
		this.last_altered = last_altered;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

}

