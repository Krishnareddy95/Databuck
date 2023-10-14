package com.databuck.bean;

import java.io.Serializable;

public class ViewRule implements Serializable {

	private static final long serialVersionUID = 1L;
	private long idruleMap;
	private String viewName;
	private String description;
	private String idListColrules;
	private String idData;
	private String idApp;

	public long getIdruleMap() {
		return idruleMap;
	}

	public void setIdruleMap(long idruleMap) {
		this.idruleMap = idruleMap;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getIdListColrules() {
		return idListColrules;
	}

	public void setIdListColrules(String idListColrules) {
		this.idListColrules = idListColrules;
	}

	public String getIdData() {
		return idData;
	}

	public void setIdData(String idData) {
		this.idData = idData;
	}

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

}
