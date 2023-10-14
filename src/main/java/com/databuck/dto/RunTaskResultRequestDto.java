package com.databuck.dto;

import java.util.ArrayList;


public class RunTaskResultRequestDto {

	private ArrayList<String> appIds;
	private String validationRunType;
	
	public ArrayList<String> getAppIds() {
		return appIds;
	}
	public void setAppIds(ArrayList<String> appIds) {
		this.appIds = appIds;
	}
	public String getValidationRunType() {
		return validationRunType;
	}
	public void setValidationRunType(String validationRunType) {
		this.validationRunType = validationRunType;
	}
	
	
	
}
