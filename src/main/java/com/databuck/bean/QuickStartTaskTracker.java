package com.databuck.bean;

import java.io.Serializable;

public class QuickStartTaskTracker implements Serializable {

	private Long idDataSchema;
	private String connectionName;
	private String schemaJobId;
	private String status;

	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getSchemaJobId() {
		return schemaJobId;
	}

	public void setSchemaJobId(String schemaJobId) {
		this.schemaJobId = schemaJobId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "{\\\"idDataSchema\\\":" + idDataSchema + ",\\\"connectionName\\\":\\\"" + connectionName
				+ "\\\",\\\"schemaJobId\\\":" + schemaJobId + ",\\\"status\\\":\\\"" + status + "\\\"}";
	}

}