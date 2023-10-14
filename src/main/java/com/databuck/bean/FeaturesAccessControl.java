package com.databuck.bean;

import java.io.Serializable;

public class FeaturesAccessControl implements Serializable{
	
	private String moduleName;
	private String feature;
	private String roles;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		return "FeaturesAccessControl [moduleName=" + moduleName + ", feature=" + feature + ", roles=" + roles + "]";
	}
	
	public FeaturesAccessControl(String moduleName, String feature, String roles) {
		super();
		this.moduleName = moduleName;
		this.feature = feature;
		this.roles = roles;
	}
	
	
}
