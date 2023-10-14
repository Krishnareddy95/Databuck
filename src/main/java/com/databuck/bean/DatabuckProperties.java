package com.databuck.bean;

import java.sql.Timestamp;

public class DatabuckProperties {
	private int propertyId;
	private int propertyCategoryId;
	private String propertyName;
	private String propertyValue;
	private String description;
	private boolean mandatoryField;
	private boolean passwordField;
	private boolean valueEncrypted;
	private String propertyDefaultvalue;
	private String propertyDataType;
	private String propRequiresRestart;
	private Timestamp lastUpdatedAt;

	private boolean warning;

	public int getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

	public int getPropertyCategoryId() {
		return propertyCategoryId;
	}

	public void setPropertyCategoryId(int propertyCategoryId) {
		this.propertyCategoryId = propertyCategoryId;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isMandatoryField() {
		return mandatoryField;
	}

	public void setMandatoryField(boolean mandatoryField) {
		this.mandatoryField = mandatoryField;
	}

	public boolean isPasswordField() {
		return passwordField;
	}

	public void setPasswordField(boolean passwordField) {
		this.passwordField = passwordField;
	}

	public boolean isValueEncrypted() {
		return valueEncrypted;
	}

	public void setValueEncrypted(boolean valueEncrypted) {
		this.valueEncrypted = valueEncrypted;
	}

	public String getPropertyDefaultvalue() {
		return propertyDefaultvalue;
	}

	public void setPropertyDefaultvalue(String propertyDefaultvalue) {
		this.propertyDefaultvalue = propertyDefaultvalue;
	}

	public String getPropertyDataType() {
		return propertyDataType;
	}

	public void setPropertyDataType(String propertyDataType) {
		this.propertyDataType = propertyDataType;
	}

	public String getPropRequiresRestart() {
		return propRequiresRestart;
	}

	public void setPropRequiresRestart(String propRequiresRestart) {
		this.propRequiresRestart = propRequiresRestart;
	}

	public Timestamp getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

}
