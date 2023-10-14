package com.databuck.bean;

public class Demo {

	private String tempName;
	private String description;
	private String location;
	private String dataformet;

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDataformet() {
		return dataformet;
	}

	public void setDataformet(String dataformet) {
		this.dataformet = dataformet;
	}

	@Override
	public String toString() {
		return "Demo [tempName=" + tempName + ", description=" + description + ", location=" + location
				+ ", dataformet=" + dataformet + "]";
	}

}
