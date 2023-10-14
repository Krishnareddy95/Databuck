package com.databuck.bean;

import java.util.ArrayList;

public class Module {

	public Module(Long idModule, String moduleName, String displayName) {
		super();
		this.idModule = idModule;
		this.moduleName = moduleName;
		this.displayName = displayName;
	}
	Long idModule;
	String moduleName;
	Boolean checked;
	String displayName;
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	ArrayList<ChildModule> childModule;
	
	public Module() {}
	
	public Module(Long idModule, String moduleName, Boolean checked, ArrayList<ChildModule> childModule) {
		super();
		this.idModule = idModule;
		this.moduleName = moduleName;
		this.checked = checked;
		this.childModule = childModule;
	}
	public Long getIdModule() {
		return idModule;
	}
	public void setIdModule(Long idModule) {
		this.idModule = idModule;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public ArrayList<ChildModule> getChildModule() {
		return childModule;
	}
	public void setChildModule(ArrayList<ChildModule> childModule) {
		this.childModule = childModule;
	}
	
	
	
}
