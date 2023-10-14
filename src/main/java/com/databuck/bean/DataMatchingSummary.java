package com.databuck.bean;

public class DataMatchingSummary {
	private int id;
	private String date;
	private Long run;
	private Long totalRecordsInSource1;
	private Long totalRecordsInSource2;
	private Long unmatchedRecords;
	private Long source1OnlyRecords;
	private Double  soure1OnlyPercenage;
	private  Long source2OnlyRecods;
	private Double soure2OnlyPercenage;
	private String status;
	private String source1OnlyStatus;
	private String source2OnlyStatus;
	private Long rcDifference;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getRun() {
		return run;
	}
	public void setRun(Long run) {
		this.run = run;
	}
	public Long getTotalRecordsInSource1() {
		return totalRecordsInSource1;
	}
	public void setTotalRecordsInSource1(Long totalRecordsInSource1) {
		this.totalRecordsInSource1 = totalRecordsInSource1;
	}
	public Long getTotalRecordsInSource2() {
		return totalRecordsInSource2;
	}
	public void setTotalRecordsInSource2(Long totalRecordsInSource2) {
		this.totalRecordsInSource2 = totalRecordsInSource2;
	}
	public Long getUnmatchedRecords() {
		return unmatchedRecords;
	}
	public void setUnmatchedRecords(Long unmatchedRecords) {
		this.unmatchedRecords = unmatchedRecords;
	}
	public Long getSource1OnlyRecords() {
		return source1OnlyRecords;
	}
	public void setSource1OnlyRecords(Long source1OnlyRecords) {
		this.source1OnlyRecords = source1OnlyRecords;
	}
	public Double getSoure1OnlyPercenage() {
		return soure1OnlyPercenage;
	}
	public void setSoure1OnlyPercenage(Double soure1OnlyPercenage) {
		this.soure1OnlyPercenage = soure1OnlyPercenage;
	}
	public Long getSource2OnlyRecods() {
		return source2OnlyRecods;
	}
	public void setSource2OnlyRecods(Long source2OnlyRecods) {
		this.source2OnlyRecods = source2OnlyRecods;
	}
	public Double getSoure2OnlyPercenage() {
		return soure2OnlyPercenage;
	}
	public void setSoure2OnlyPercenage(Double soure2OnlyPercenage) {
		this.soure2OnlyPercenage = soure2OnlyPercenage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSource1OnlyStatus() {
		return source1OnlyStatus;
	}
	public void setSource1OnlyStatus(String source1OnlyStatus) {
		this.source1OnlyStatus = source1OnlyStatus;
	}
	public String getSource2OnlyStatus() {
		return source2OnlyStatus;
	}
	public void setSource2OnlyStatus(String source2OnlyStatus) {
		this.source2OnlyStatus = source2OnlyStatus;
	}
	public Long getRcDifference() {
		return rcDifference;
	}
	public void setRcDifference(Long rcDifference) {
		this.rcDifference = rcDifference;
	}
	
}