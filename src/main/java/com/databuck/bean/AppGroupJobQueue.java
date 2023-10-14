package com.databuck.bean;

public class AppGroupJobQueue {
	private Long queueId;
	private Long idAppGroup;
	private String uniqueId;
	private String status;

	public Long getQueueId() {
		return queueId;
	}

	public void setQueueId(Long queueId) {
		this.queueId = queueId;
	}

	public Long getIdAppGroup() {
		return idAppGroup;
	}

	public void setIdAppGroup(Long idAppGroup) {
		this.idAppGroup = idAppGroup;
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
