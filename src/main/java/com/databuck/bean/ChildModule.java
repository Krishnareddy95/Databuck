package com.databuck.bean;

public class ChildModule {

	String displayName;
	String value;
	Boolean checked;
	
	public ChildModule(){}
	
	public ChildModule(String displayName, String value, Boolean checked) {
		super();
		this.displayName = displayName;
		this.value = value;
		this.checked = checked;
	}
	
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	
	
}
