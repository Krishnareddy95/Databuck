package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class TemplateDto {

	@ApiModelProperty(notes = "Template Id")
	private long templateId;
	@ApiModelProperty(notes = "Template Unique Id")
	private String template_uniqueId;
	@ApiModelProperty(notes = "Template Status")
	private String templateStatus;
	
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public String getTemplate_uniqueId() {
		return template_uniqueId;
	}
	public void setTemplate_uniqueId(String template_uniqueId) {
		this.template_uniqueId = template_uniqueId;
	}
	public String getTemplateStatus() {
		return templateStatus;
	}
	public void setTemplateStatus(String templateStatus) {
		this.templateStatus = templateStatus;
	}
	
	
}
