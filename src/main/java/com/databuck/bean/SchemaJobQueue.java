package com.databuck.bean;

public class SchemaJobQueue {
	private Long queueId;
	private Long idDataSchema;
	private String uniqueId;
	private String status;
	private String healthCheck;

	public String getHealthCheck() {
		return healthCheck;
	}

	public void setHealthCheck(String healthCheck) {
		this.healthCheck = healthCheck;
	}

	public Long getQueueId() {
		return queueId;
	}

	public void setQueueId(Long queueId) {
		this.queueId = queueId;
	}

	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
