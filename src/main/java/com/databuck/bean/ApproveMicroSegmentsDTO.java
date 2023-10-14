package com.databuck.bean;

public class ApproveMicroSegmentsDTO {
	private Long templateId;
	private String templateName;
	private Long validationId;
	private String validationName;
	private boolean templateCreationStatus;
	private boolean validationCreationStatus;
	private boolean templateDeactivateStatus;

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getValidationId() {
		return validationId;
	}

	public void setValidationId(Long validationId) {
		this.validationId = validationId;
	}

	public String getValidationName() {
		return validationName;
	}

	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}

	public boolean isTemplateCreationStatus() {
		return templateCreationStatus;
	}

	public void setTemplateCreationStatus(boolean templateCreationStatus) {
		this.templateCreationStatus = templateCreationStatus;
	}

	public boolean isValidationCreationStatus() {
		return validationCreationStatus;
	}

	public void setValidationCreationStatus(boolean validationCreationStatus) {
		this.validationCreationStatus = validationCreationStatus;
	}

	public boolean isTemplateDeactivateStatus() {
		return templateDeactivateStatus;
	}

	public void setTemplateDeactivateStatus(boolean templateDeactivateStatus) {
		this.templateDeactivateStatus = templateDeactivateStatus;
	}

	@Override
	public String toString() {
		return "{\"templateId\":\"" + templateId + "\", \"templateName\":\"" + templateName + "\", \"validationId\":\""
				+ validationId + "\", \"validationName\":\"" + validationName + "\", \"templateCreationStatus\":\""
				+ templateCreationStatus + "\", \"validationCreationStatus\":\"" + validationCreationStatus
				+ "\", \"templateDeactivateStatus\":\"" + templateDeactivateStatus + "\"}";
	}

}
