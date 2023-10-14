package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class GetAgingIssuesForValidationDto {

	@ApiModelProperty(notes = "Validation ID")
	private String Validation_ID;
	@ApiModelProperty(notes = "Open Date")
	private String Open_Date;
	@ApiModelProperty(notes = "Run")
	private String Run;
	@ApiModelProperty(notes = "DQR ID")
	private String DQR_ID;
	@ApiModelProperty(notes = "Key Value")
	private String Key_Value;
	@ApiModelProperty(notes = "Key Column")
	private String Key_Column;
	@ApiModelProperty(notes = "Exposure Amount")
	private String Exposure_Amount;
	@ApiModelProperty(notes = "Date Closed")
	private String Date_Closed;
	
	
	public String getValidation_ID() {
		return Validation_ID;
	}
	public void setValidation_ID(String validation_ID) {
		Validation_ID = validation_ID;
	}
	public String getOpen_Date() {
		return Open_Date;
	}
	public void setOpen_Date(String open_Date) {
		Open_Date = open_Date;
	}
	public String getRun() {
		return Run;
	}
	public void setRun(String run) {
		Run = run;
	}
	public String getDQR_ID() {
		return DQR_ID;
	}
	public void setDQR_ID(String dQR_ID) {
		DQR_ID = dQR_ID;
	}
	public String getKey_Value() {
		return Key_Value;
	}
	public void setKey_Value(String key_Value) {
		Key_Value = key_Value;
	}
	public String getKey_Column() {
		return Key_Column;
	}
	public void setKey_Column(String key_Column) {
		Key_Column = key_Column;
	}
	public String getExposure_Amount() {
		return Exposure_Amount;
	}
	public void setExposure_Amount(String exposure_Amount) {
		Exposure_Amount = exposure_Amount;
	}
	public String getDate_Closed() {
		return Date_Closed;
	}
	public void setDate_Closed(String date_Closed) {
		Date_Closed = date_Closed;
	}
	
	
}
