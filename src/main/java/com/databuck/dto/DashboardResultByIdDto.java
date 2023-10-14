package com.databuck.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModelProperty;

public class DashboardResultByIdDto {

	@ApiModelProperty(notes = "App ID")
	private long idApp;
	@ApiModelProperty(notes = "Name")
	private String name;
	@ApiModelProperty(notes = "Data Id")
	private long idData;
	@ApiModelProperty(notes = "Template Name")
	private String templateName;
	@ApiModelProperty(notes = "GRCA Flag")
	private String flagGrca;
	@ApiModelProperty(notes = "Max Run")
	private int maxRun;
	@ApiModelProperty(notes = "Max Date")
	private String maxDate;
	@ApiModelProperty(notes = "RCA DQ Metrics")
	private DQmetricdto rcaDQmetricdto;
	@ApiModelProperty(notes = "Total DQI")
	private String totalDQI;
	@ApiModelProperty(notes = "DQ Metrics")
	private ArrayList<DQmetricdto> DQMetrics;
	
	public long getIdApp() {
		return idApp;
	}
	public void setIdApp(long idApp) {
		this.idApp = idApp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getIdData() {
		return idData;
	}
	public void setIdData(long idData) {
		this.idData = idData;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getFlagGrca() {
		return flagGrca;
	}
	public void setFlagGrca(String flagGrca) {
		this.flagGrca = flagGrca;
	}
	public int getMaxRun() {
		return maxRun;
	}
	public void setMaxRun(int maxRun) {
		this.maxRun = maxRun;
	}
	public String getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}
	public DQmetricdto getRcaDQmetricdto() {
		return rcaDQmetricdto;
	}
	public void setRcaDQmetricdto(DQmetricdto rcaDQmetricdto) {
		this.rcaDQmetricdto = rcaDQmetricdto;
	}
	public String getTotalDQI() {
		return totalDQI;
	}
	public void setTotalDQI(String totalDQI) {
		this.totalDQI = totalDQI;
	}
	public ArrayList<DQmetricdto> getDQMetrics() {
		return DQMetrics;
	}
	public void setDQMetrics(ArrayList<DQmetricdto> dQMetrics) {
		DQMetrics = dQMetrics;
	}
	
	
}
