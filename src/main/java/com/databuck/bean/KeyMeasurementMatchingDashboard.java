package com.databuck.bean;

public class KeyMeasurementMatchingDashboard {
private Long idApp;
private String date;
private Long run;
private String validationCheckName;
private String source1;
private String source2;
private Long source1Count;
private Long source2Count;
private Long source1Records;
private Long source2Records;
private String unmatchedStatus;
private String source1OnlyStatus;
private String source2OnlyStatus;
private String unmatchedRecords;
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
public Long getIdApp() {
	return idApp;
}
public void setIdApp(Long idApp) {
	this.idApp = idApp;
}
public String getValidationCheckName() {
	return validationCheckName;
}
public void setValidationCheckName(String validationCheckName) {
	this.validationCheckName = validationCheckName;
}
public String getSource1() {
	return source1;
}
public void setSource1(String source1) {
	this.source1 = source1;
}
public String getSource2() {
	return source2;
}
public void setSource2(String source2) {
	this.source2 = source2;
}
public Long getSource1Count() {
	return source1Count;
}
public void setSource1Count(Long source1Count) {
	this.source1Count = source1Count;
}
public Long getSource2Count() {
	return source2Count;
}
public void setSource2Count(Long source2Count) {
	this.source2Count = source2Count;
}
public Long getSource1Records() {
	return source1Records;
}
public void setSource1Records(Long source1Records) {
	this.source1Records = source1Records;
}
public Long getSource2Records() {
	return source2Records;
}
public void setSource2Records(Long source2Records) {
	this.source2Records = source2Records;
}
public String getUnmatchedStatus() {
	return unmatchedStatus;
}
public void setUnmatchedStatus(String unmatchedStatus) {
	this.unmatchedStatus = unmatchedStatus;
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
public String getUnmatchedRecords() {
	return unmatchedRecords;
}
public void setUnmatchedRecords(String unmatchedRecords) {
	this.unmatchedRecords = unmatchedRecords;
}
}
