package com.databuck.bean;

public class DashboardCheckComponent {

	private long componentId;
	private String checkName;
	private String description;
	private String component;
	private String entityName;
	private String technicalName;
	private String technicalCheckValue;
	private String technicalResultName;

	public long getComponentId() {
		return componentId;
	}

	public void setComponentId(long componentId) {
		this.componentId = componentId;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getTechnicalCheckValue() {
		return technicalCheckValue;
	}

	public void setTechnicalCheckValue(String technicalCheckValue) {
		this.technicalCheckValue = technicalCheckValue;
	}

	public String getTechnicalResultName() {
		return technicalResultName;
	}

	public void setTechnicalResultName(String technicalResultName) {
		this.technicalResultName = technicalResultName;
	}

}
