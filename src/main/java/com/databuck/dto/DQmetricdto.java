package com.databuck.dto;

import io.swagger.annotations.ApiModelProperty;

public class DQmetricdto {

	@ApiModelProperty(notes = "Record Anomaly Score")
	private String recordAnomalyScore;
	@ApiModelProperty(notes = "RCA Status")
	private String RCAStatus;
	@ApiModelProperty(notes = "RCA Key Matrics 1")
	private String RCAKey_Matric_1;
	@ApiModelProperty(notes = "RCA Key Matrics 2")
	private String RCAKey_Matric_2;
	@ApiModelProperty(notes = "RCA Key Matrics 3")
	private String RCAKey_Matric_3;
	@ApiModelProperty(notes = "Bad Data Score")
	private double badDataScore;
	@ApiModelProperty(notes = "Bad Data Status")
	private String badDataStatus;
	@ApiModelProperty(notes = "Bad Data Key Matrics 1")
	private String badDataKey_Matric_1;
	@ApiModelProperty(notes = "Bad Data Key Matrics 2")
	private String badDataKey_Matric_2;
	@ApiModelProperty(notes = "Bad Data Key Matrics 3")
	private String badDataKey_Matric_3;
	@ApiModelProperty(notes = "Pattern Data Score")
	private String patternDataScore;
	@ApiModelProperty(notes = "Pattern Data Status")
	private String patternDataStatus;
	@ApiModelProperty(notes = "Pattern Data Key Matrics 1")
	private String patternDataKey_Matric_1;
	@ApiModelProperty(notes = "Pattern Data Key Matrics 2")
	private String patternDataKey_Matric_2;
	@ApiModelProperty(notes = "Pattern Data Key Matrics 3")
	private String patternDataKey_Matric_3;
	@ApiModelProperty(notes = "Date Rule Check Score")
	private String dateRuleCheckScore;
	@ApiModelProperty(notes = "Date Rule Check Status")
	private String dateRuleChkStatus;
	@ApiModelProperty(notes = "Date Rule Key Matrics 1")
	private String dateRuleKey_Matric_1;
	@ApiModelProperty(notes = "Date Rule Key Matrics 2")
	private String dateRuleKey_Matric_2;
	@ApiModelProperty(notes = "Date Rule Key Matrics 3")
	private String dateRuleKey_Matric_3;
	@ApiModelProperty(notes = "Numerical Field Score")
	private String numericalFieldScore;
	@ApiModelProperty(notes = "Numerical Field Stats Status")
	private String numericalFieldStatsStatus;
	@ApiModelProperty(notes = "Numerical Field Key Matrics 1")
	private String numericalFieldKey_Matric_1;
	@ApiModelProperty(notes = "Numerical Field Key Matrics 2")
	private String numericalFieldKey_Matric_2;
	@ApiModelProperty(notes = "Numerical Field Key Matrics 3")
	private String numericalFieldKey_Matric_3;
	@ApiModelProperty(notes = "String Field Score")
	private String stringFieldScore;
	@ApiModelProperty(notes = "String Field Status")
	private String stringFieldStatus;
	@ApiModelProperty(notes = "Null Count Score")
	private String nullCountScore;
	@ApiModelProperty(notes = "Null Count Status")
	private String nullCountStatus;
	@ApiModelProperty(notes = "Null Count Key Matrics 1")
	private String nullCountKey_Matric_1;
	@ApiModelProperty(notes = "Null Count Key Matrics 2")
	private String nullCountKey_Matric_2;
	@ApiModelProperty(notes = "Null Count Key Matrics 3")
	private String nullCountKey_Matric_3;
	@ApiModelProperty(notes = "All Field Score")
	private String allFieldsScore;
	@ApiModelProperty(notes = "All Field Status")
	private String allFieldsStatus;
	@ApiModelProperty(notes = "All Field Key Matrics 1")
	private String allFieldsKey_Matric_1;
	@ApiModelProperty(notes = "All Field Key Matrics 2")
	private String allFieldsKey_Matric_2;
	@ApiModelProperty(notes = "All Field Key Matrics 3")
	private String allFieldsKey_Matric_3;
	@ApiModelProperty(notes = "Identity Field Score")
	private String identityFieldsScore;
	@ApiModelProperty(notes = "Identity Field Status")
	private String identityfieldsStatus;
	@ApiModelProperty(notes = "Identity Field Key Matrics 1")
	private String identityfieldsKey_Matric_1;
	@ApiModelProperty(notes = "Identity Field Key Matrics 2")
	private String identityfieldsKey_Matric_2;
	@ApiModelProperty(notes = "Identity Field Key Matrics 3")
	private String identityfieldsKey_Matric_3;
	@ApiModelProperty(notes = "Record Field Score")
	private String recordFieldScore;
	@ApiModelProperty(notes = "Record Anomaly Status")
	private String recordAnomalyStatus;
	@ApiModelProperty(notes = "Record Anomaly Key Matrics 1")
	private String recordAnomalyKey_Matric_1;
	@ApiModelProperty(notes = "Record Anomaly Key Matrics 2")
	private String recordAnomalyKey_Matric_2;
	@ApiModelProperty(notes = "Record Anomaly Key Matrics 3")
	private String recordAnomalyKey_Matric_3;
	@ApiModelProperty(notes = "Length Check Score")
	private String lengthCheckScore;
	@ApiModelProperty(notes = "Length Status")
	private String lengthStatus;
	@ApiModelProperty(notes = "Length Key Matrics 1")
	private String lengthKey_Matric_1;
	@ApiModelProperty(notes = "Length Key Matrics 2")
	private String lengthKey_Matric_2;
	@ApiModelProperty(notes = "Length Key Matrics 3")
	private String lengthKey_Matric_3;
	@ApiModelProperty(notes = "Max Length Check Score")
	private String maxLengthCheckScore;
	@ApiModelProperty(notes = "Max Length Status")
	private String maxLengthStatus;
	@ApiModelProperty(notes = "Max Length Key Matrics 1")
	private String maxLengthKey_Matric_1;
	@ApiModelProperty(notes = "Max Length Key Matrics 2")
	private String maxLengthKey_Matric_2;
	@ApiModelProperty(notes = "Max Length Key Matrics 3")
	private String maxLengthKey_Matric_3;
	@ApiModelProperty(notes = "Rule Score DF")
	private String ruleScoreDF;
	@ApiModelProperty(notes = "Rule Status")
	private String ruleStatus;
	@ApiModelProperty(notes = "Rule Key Matrics 1")
	private String ruleKey_Matric_1;
	@ApiModelProperty(notes = "Rule Key Matrics 2")
	private String ruleKey_Matric_2;
	@ApiModelProperty(notes = "Rule Key Matrics 3")
	private String ruleKey_Matric_3;
	@ApiModelProperty(notes = "Custom Rule Count")
	private Integer custrule_count;
	@ApiModelProperty(notes = "Global Rule Score DF")
	private String GlobalruleScoreDF;
	@ApiModelProperty(notes = "Global Rule Status")
	private String GlobalruleStatus;
	@ApiModelProperty(notes = "Rule Key Matrics 4")
	private String ruleKey_Matric_4;
	@ApiModelProperty(notes = "Global Rule Count")
	private Integer globalrule_count;
	@ApiModelProperty(notes = "Data Drift Score")
	private String dataDriftScore;
	@ApiModelProperty(notes = "Data Drift Status")
	private String dataDriftStatus;
	@ApiModelProperty(notes = "Data Drift Key Matrics 1")
	private String dataDriftKey_Matric_1;
	@ApiModelProperty(notes = "Data Drift Key Matrics 2")
	private String dataDriftKey_Matric_2;
	@ApiModelProperty(notes = "Data Drift Key Matrics 3")
	private String dataDriftKey_Matric_3;
	@ApiModelProperty(notes = "Timeliness Check Score")
	private String TimelinessCheckScore;
	@ApiModelProperty(notes = "Timeliness Check Status")
	private String TimelinessCheckStatus;
	@ApiModelProperty(notes = "Timeliness Check Key Matrics 1")
	private String TimelinessCheckKey_Matric_1;
	@ApiModelProperty(notes = "Timeliness Check Key Matrics 2")
	private String TimelinessCheckKey_Matric_2;
	@ApiModelProperty(notes = "Timeliness Check Key Matrics 3")
	private String TimelinessCheckKey_Matric_3;
	@ApiModelProperty(notes = "Default Check Score")
	private String DefaultCheckScore;
	@ApiModelProperty(notes = "Default Check Status")
	private String DefaultCheckStatus;
	@ApiModelProperty(notes = "Default Check Key Matrics 1")
	private String DefaultCheckKey_Matric_1;
	@ApiModelProperty(notes = "Default Check Key Matrics 2")
	private String DefaultCheckKey_Matric_2;
	@ApiModelProperty(notes = "Default Check Key Matrics 3")
	private String DefaultCheckKey_Matric_3;
	
	
	
	public String getRecordAnomalyScore() {
		return recordAnomalyScore;
	}
	public void setRecordAnomalyScore(String recordAnomalyScore) {
		this.recordAnomalyScore = recordAnomalyScore;
	}
	public String getRCAStatus() {
		return RCAStatus;
	}
	public void setRCAStatus(String rCAStatus) {
		RCAStatus = rCAStatus;
	}
	public String getRCAKey_Matric_1() {
		return RCAKey_Matric_1;
	}
	public void setRCAKey_Matric_1(String rCAKey_Matric_1) {
		RCAKey_Matric_1 = rCAKey_Matric_1;
	}
	public String getRCAKey_Matric_2() {
		return RCAKey_Matric_2;
	}
	public void setRCAKey_Matric_2(String rCAKey_Matric_2) {
		RCAKey_Matric_2 = rCAKey_Matric_2;
	}
	public String getRCAKey_Matric_3() {
		return RCAKey_Matric_3;
	}
	public void setRCAKey_Matric_3(String rCAKey_Matric_3) {
		RCAKey_Matric_3 = rCAKey_Matric_3;
	}
	public double getBadDataScore() {
		return badDataScore;
	}
	public void setBadDataScore(double d) {
		this.badDataScore = d;
	}
	public String getBadDataStatus() {
		return badDataStatus;
	}
	public void setBadDataStatus(String badDataStatus) {
		this.badDataStatus = badDataStatus;
	}
	public String getBadDataKey_Matric_1() {
		return badDataKey_Matric_1;
	}
	public void setBadDataKey_Matric_1(String badDataKey_Matric_1) {
		this.badDataKey_Matric_1 = badDataKey_Matric_1;
	}
	public String getBadDataKey_Matric_2() {
		return badDataKey_Matric_2;
	}
	public void setBadDataKey_Matric_2(String badDataKey_Matric_2) {
		this.badDataKey_Matric_2 = badDataKey_Matric_2;
	}
	public String getBadDataKey_Matric_3() {
		return badDataKey_Matric_3;
	}
	public void setBadDataKey_Matric_3(String badDataKey_Matric_3) {
		this.badDataKey_Matric_3 = badDataKey_Matric_3;
	}
	public String getPatternDataScore() {
		return patternDataScore;
	}
	public void setPatternDataScore(String patternDataScore) {
		this.patternDataScore = patternDataScore;
	}
	public String getPatternDataStatus() {
		return patternDataStatus;
	}
	public void setPatternDataStatus(String patternDataStatus) {
		this.patternDataStatus = patternDataStatus;
	}
	public String getPatternDataKey_Matric_1() {
		return patternDataKey_Matric_1;
	}
	public void setPatternDataKey_Matric_1(String patternDataKey_Matric_1) {
		this.patternDataKey_Matric_1 = patternDataKey_Matric_1;
	}
	public String getPatternDataKey_Matric_2() {
		return patternDataKey_Matric_2;
	}
	public void setPatternDataKey_Matric_2(String patternDataKey_Matric_2) {
		this.patternDataKey_Matric_2 = patternDataKey_Matric_2;
	}
	public String getPatternDataKey_Matric_3() {
		return patternDataKey_Matric_3;
	}
	public void setPatternDataKey_Matric_3(String patternDataKey_Matric_3) {
		this.patternDataKey_Matric_3 = patternDataKey_Matric_3;
	}
	public String getDateRuleCheckScore() {
		return dateRuleCheckScore;
	}
	public void setDateRuleCheckScore(String dateRuleCheckScore) {
		this.dateRuleCheckScore = dateRuleCheckScore;
	}
	public String getDateRuleChkStatus() {
		return dateRuleChkStatus;
	}
	public void setDateRuleChkStatus(String dateRuleChkStatus) {
		this.dateRuleChkStatus = dateRuleChkStatus;
	}
	public String getDateRuleKey_Matric_1() {
		return dateRuleKey_Matric_1;
	}
	public void setDateRuleKey_Matric_1(String dateRuleKey_Matric_1) {
		this.dateRuleKey_Matric_1 = dateRuleKey_Matric_1;
	}
	public String getDateRuleKey_Matric_2() {
		return dateRuleKey_Matric_2;
	}
	public void setDateRuleKey_Matric_2(String dateRuleKey_Matric_2) {
		this.dateRuleKey_Matric_2 = dateRuleKey_Matric_2;
	}
	public String getDateRuleKey_Matric_3() {
		return dateRuleKey_Matric_3;
	}
	public void setDateRuleKey_Matric_3(String dateRuleKey_Matric_3) {
		this.dateRuleKey_Matric_3 = dateRuleKey_Matric_3;
	}
	public String getNumericalFieldScore() {
		return numericalFieldScore;
	}
	public void setNumericalFieldScore(String numericalFieldScore) {
		this.numericalFieldScore = numericalFieldScore;
	}
	public String getNumericalFieldStatsStatus() {
		return numericalFieldStatsStatus;
	}
	public void setNumericalFieldStatsStatus(String numericalFieldStatsStatus) {
		this.numericalFieldStatsStatus = numericalFieldStatsStatus;
	}
	public String getNumericalFieldKey_Matric_1() {
		return numericalFieldKey_Matric_1;
	}
	public void setNumericalFieldKey_Matric_1(String numericalFieldKey_Matric_1) {
		this.numericalFieldKey_Matric_1 = numericalFieldKey_Matric_1;
	}
	public String getNumericalFieldKey_Matric_2() {
		return numericalFieldKey_Matric_2;
	}
	public void setNumericalFieldKey_Matric_2(String numericalFieldKey_Matric_2) {
		this.numericalFieldKey_Matric_2 = numericalFieldKey_Matric_2;
	}
	public String getNumericalFieldKey_Matric_3() {
		return numericalFieldKey_Matric_3;
	}
	public void setNumericalFieldKey_Matric_3(String numericalFieldKey_Matric_3) {
		this.numericalFieldKey_Matric_3 = numericalFieldKey_Matric_3;
	}
	public String getStringFieldScore() {
		return stringFieldScore;
	}
	public void setStringFieldScore(String stringFieldScore) {
		this.stringFieldScore = stringFieldScore;
	}
	public String getStringFieldStatus() {
		return stringFieldStatus;
	}
	public void setStringFieldStatus(String stringFieldStatus) {
		this.stringFieldStatus = stringFieldStatus;
	}
	public String getNullCountScore() {
		return nullCountScore;
	}
	public void setNullCountScore(String nullCountScore) {
		this.nullCountScore = nullCountScore;
	}
	public String getNullCountStatus() {
		return nullCountStatus;
	}
	public void setNullCountStatus(String nullCountStatus) {
		this.nullCountStatus = nullCountStatus;
	}
	public String getNullCountKey_Matric_1() {
		return nullCountKey_Matric_1;
	}
	public void setNullCountKey_Matric_1(String nullCountKey_Matric_1) {
		this.nullCountKey_Matric_1 = nullCountKey_Matric_1;
	}
	public String getNullCountKey_Matric_2() {
		return nullCountKey_Matric_2;
	}
	public void setNullCountKey_Matric_2(String nullCountKey_Matric_2) {
		this.nullCountKey_Matric_2 = nullCountKey_Matric_2;
	}
	public String getNullCountKey_Matric_3() {
		return nullCountKey_Matric_3;
	}
	public void setNullCountKey_Matric_3(String nullCountKey_Matric_3) {
		this.nullCountKey_Matric_3 = nullCountKey_Matric_3;
	}
	public String getAllFieldsScore() {
		return allFieldsScore;
	}
	public void setAllFieldsScore(String allFieldsScore) {
		this.allFieldsScore = allFieldsScore;
	}
	public String getAllFieldsStatus() {
		return allFieldsStatus;
	}
	public void setAllFieldsStatus(String allFieldsStatus) {
		this.allFieldsStatus = allFieldsStatus;
	}
	public String getAllFieldsKey_Matric_1() {
		return allFieldsKey_Matric_1;
	}
	public void setAllFieldsKey_Matric_1(String allFieldsKey_Matric_1) {
		this.allFieldsKey_Matric_1 = allFieldsKey_Matric_1;
	}
	public String getAllFieldsKey_Matric_2() {
		return allFieldsKey_Matric_2;
	}
	public void setAllFieldsKey_Matric_2(String allFieldsKey_Matric_2) {
		this.allFieldsKey_Matric_2 = allFieldsKey_Matric_2;
	}
	public String getAllFieldsKey_Matric_3() {
		return allFieldsKey_Matric_3;
	}
	public void setAllFieldsKey_Matric_3(String allFieldsKey_Matric_3) {
		this.allFieldsKey_Matric_3 = allFieldsKey_Matric_3;
	}
	public String getIdentityFieldsScore() {
		return identityFieldsScore;
	}
	public void setIdentityFieldsScore(String identityFieldsScore) {
		this.identityFieldsScore = identityFieldsScore;
	}
	public String getIdentityfieldsStatus() {
		return identityfieldsStatus;
	}
	public void setIdentityfieldsStatus(String identityfieldsStatus) {
		this.identityfieldsStatus = identityfieldsStatus;
	}
	public String getIdentityfieldsKey_Matric_1() {
		return identityfieldsKey_Matric_1;
	}
	public void setIdentityfieldsKey_Matric_1(String identityfieldsKey_Matric_1) {
		this.identityfieldsKey_Matric_1 = identityfieldsKey_Matric_1;
	}
	public String getIdentityfieldsKey_Matric_2() {
		return identityfieldsKey_Matric_2;
	}
	public void setIdentityfieldsKey_Matric_2(String identityfieldsKey_Matric_2) {
		this.identityfieldsKey_Matric_2 = identityfieldsKey_Matric_2;
	}
	public String getIdentityfieldsKey_Matric_3() {
		return identityfieldsKey_Matric_3;
	}
	public void setIdentityfieldsKey_Matric_3(String identityfieldsKey_Matric_3) {
		this.identityfieldsKey_Matric_3 = identityfieldsKey_Matric_3;
	}
	public String getRecordFieldScore() {
		return recordFieldScore;
	}
	public void setRecordFieldScore(String recordFieldScore) {
		this.recordFieldScore = recordFieldScore;
	}
	public String getRecordAnomalyStatus() {
		return recordAnomalyStatus;
	}
	public void setRecordAnomalyStatus(String recordAnomalyStatus) {
		this.recordAnomalyStatus = recordAnomalyStatus;
	}
	public String getRecordAnomalyKey_Matric_1() {
		return recordAnomalyKey_Matric_1;
	}
	public void setRecordAnomalyKey_Matric_1(String recordAnomalyKey_Matric_1) {
		this.recordAnomalyKey_Matric_1 = recordAnomalyKey_Matric_1;
	}
	public String getRecordAnomalyKey_Matric_2() {
		return recordAnomalyKey_Matric_2;
	}
	public void setRecordAnomalyKey_Matric_2(String recordAnomalyKey_Matric_2) {
		this.recordAnomalyKey_Matric_2 = recordAnomalyKey_Matric_2;
	}
	public String getRecordAnomalyKey_Matric_3() {
		return recordAnomalyKey_Matric_3;
	}
	public void setRecordAnomalyKey_Matric_3(String recordAnomalyKey_Matric_3) {
		this.recordAnomalyKey_Matric_3 = recordAnomalyKey_Matric_3;
	}
	public String getLengthCheckScore() {
		return lengthCheckScore;
	}
	public void setLengthCheckScore(String lengthCheckScore) {
		this.lengthCheckScore = lengthCheckScore;
	}
	public String getLengthStatus() {
		return lengthStatus;
	}
	public void setLengthStatus(String lengthStatus) {
		this.lengthStatus = lengthStatus;
	}
	public String getLengthKey_Matric_1() {
		return lengthKey_Matric_1;
	}
	public void setLengthKey_Matric_1(String lengthKey_Matric_1) {
		this.lengthKey_Matric_1 = lengthKey_Matric_1;
	}
	public String getLengthKey_Matric_2() {
		return lengthKey_Matric_2;
	}
	public void setLengthKey_Matric_2(String lengthKey_Matric_2) {
		this.lengthKey_Matric_2 = lengthKey_Matric_2;
	}
	public String getLengthKey_Matric_3() {
		return lengthKey_Matric_3;
	}
	public void setLengthKey_Matric_3(String lengthKey_Matric_3) {
		this.lengthKey_Matric_3 = lengthKey_Matric_3;
	}
	public String getMaxLengthCheckScore() {
		return maxLengthCheckScore;
	}
	public void setMaxLengthCheckScore(String maxLengthCheckScore) {
		this.maxLengthCheckScore = maxLengthCheckScore;
	}
	public String getMaxLengthStatus() {
		return maxLengthStatus;
	}
	public void setMaxLengthStatus(String maxLengthStatus) {
		this.maxLengthStatus = maxLengthStatus;
	}
	public String getMaxLengthKey_Matric_1() {
		return maxLengthKey_Matric_1;
	}
	public void setMaxLengthKey_Matric_1(String maxLengthKey_Matric_1) {
		this.maxLengthKey_Matric_1 = maxLengthKey_Matric_1;
	}
	public String getMaxLengthKey_Matric_2() {
		return maxLengthKey_Matric_2;
	}
	public void setMaxLengthKey_Matric_2(String maxLengthKey_Matric_2) {
		this.maxLengthKey_Matric_2 = maxLengthKey_Matric_2;
	}
	public String getMaxLengthKey_Matric_3() {
		return maxLengthKey_Matric_3;
	}
	public void setMaxLengthKey_Matric_3(String maxLengthKey_Matric_3) {
		this.maxLengthKey_Matric_3 = maxLengthKey_Matric_3;
	}
	public String getRuleScoreDF() {
		return ruleScoreDF;
	}
	public void setRuleScoreDF(String ruleScoreDF) {
		this.ruleScoreDF = ruleScoreDF;
	}
	public String getRuleStatus() {
		return ruleStatus;
	}
	public void setRuleStatus(String ruleStatus) {
		this.ruleStatus = ruleStatus;
	}
	public String getRuleKey_Matric_1() {
		return ruleKey_Matric_1;
	}
	public void setRuleKey_Matric_1(String ruleKey_Matric_1) {
		this.ruleKey_Matric_1 = ruleKey_Matric_1;
	}
	public String getRuleKey_Matric_2() {
		return ruleKey_Matric_2;
	}
	public void setRuleKey_Matric_2(String ruleKey_Matric_2) {
		this.ruleKey_Matric_2 = ruleKey_Matric_2;
	}
	public String getRuleKey_Matric_3() {
		return ruleKey_Matric_3;
	}
	public void setRuleKey_Matric_3(String ruleKey_Matric_3) {
		this.ruleKey_Matric_3 = ruleKey_Matric_3;
	}
	public Integer getCustrule_count() {
		return custrule_count;
	}
	public void setCustrule_count(Integer custrule_count) {
		this.custrule_count = custrule_count;
	}
	public String getGlobalruleScoreDF() {
		return GlobalruleScoreDF;
	}
	public void setGlobalruleScoreDF(String globalruleScoreDF) {
		GlobalruleScoreDF = globalruleScoreDF;
	}
	public String getGlobalruleStatus() {
		return GlobalruleStatus;
	}
	public void setGlobalruleStatus(String globalruleStatus) {
		GlobalruleStatus = globalruleStatus;
	}
	public String getRuleKey_Matric_4() {
		return ruleKey_Matric_4;
	}
	public void setRuleKey_Matric_4(String ruleKey_Matric_4) {
		this.ruleKey_Matric_4 = ruleKey_Matric_4;
	}
	public Integer getGlobalrule_count() {
		return globalrule_count;
	}
	public void setGlobalrule_count(Integer globalrule_count) {
		this.globalrule_count = globalrule_count;
	}
	public String getDataDriftScore() {
		return dataDriftScore;
	}
	public void setDataDriftScore(String dataDriftScore) {
		this.dataDriftScore = dataDriftScore;
	}
	public String getDataDriftStatus() {
		return dataDriftStatus;
	}
	public void setDataDriftStatus(String dataDriftStatus) {
		this.dataDriftStatus = dataDriftStatus;
	}
	public String getDataDriftKey_Matric_1() {
		return dataDriftKey_Matric_1;
	}
	public void setDataDriftKey_Matric_1(String dataDriftKey_Matric_1) {
		this.dataDriftKey_Matric_1 = dataDriftKey_Matric_1;
	}
	public String getDataDriftKey_Matric_2() {
		return dataDriftKey_Matric_2;
	}
	public void setDataDriftKey_Matric_2(String dataDriftKey_Matric_2) {
		this.dataDriftKey_Matric_2 = dataDriftKey_Matric_2;
	}
	public String getDataDriftKey_Matric_3() {
		return dataDriftKey_Matric_3;
	}
	public void setDataDriftKey_Matric_3(String dataDriftKey_Matric_3) {
		this.dataDriftKey_Matric_3 = dataDriftKey_Matric_3;
	}
	public String getTimelinessCheckStatus() {
		return TimelinessCheckStatus;
	}
	public void setTimelinessCheckStatus(String timelinessCheckStatus) {
		TimelinessCheckStatus = timelinessCheckStatus;
	}
	public String getTimelinessCheckKey_Matric_1() {
		return TimelinessCheckKey_Matric_1;
	}
	public void setTimelinessCheckKey_Matric_1(String timelinessCheckKey_Matric_1) {
		TimelinessCheckKey_Matric_1 = timelinessCheckKey_Matric_1;
	}
	public String getTimelinessCheckKey_Matric_2() {
		return TimelinessCheckKey_Matric_2;
	}
	public void setTimelinessCheckKey_Matric_2(String timelinessCheckKey_Matric_2) {
		TimelinessCheckKey_Matric_2 = timelinessCheckKey_Matric_2;
	}
	public String getTimelinessCheckKey_Matric_3() {
		return TimelinessCheckKey_Matric_3;
	}
	public void setTimelinessCheckKey_Matric_3(String timelinessCheckKey_Matric_3) {
		TimelinessCheckKey_Matric_3 = timelinessCheckKey_Matric_3;
	}
	public String getDefaultCheckScore() {
		return DefaultCheckScore;
	}
	public void setDefaultCheckScore(String defaultCheckScore) {
		DefaultCheckScore = defaultCheckScore;
	}
	public String getDefaultCheckStatus() {
		return DefaultCheckStatus;
	}
	public void setDefaultCheckStatus(String defaultCheckStatus) {
		DefaultCheckStatus = defaultCheckStatus;
	}
	public String getDefaultCheckKey_Matric_1() {
		return DefaultCheckKey_Matric_1;
	}
	public void setDefaultCheckKey_Matric_1(String defaultCheckKey_Matric_1) {
		DefaultCheckKey_Matric_1 = defaultCheckKey_Matric_1;
	}
	public String getDefaultCheckKey_Matric_2() {
		return DefaultCheckKey_Matric_2;
	}
	public void setDefaultCheckKey_Matric_2(String defaultCheckKey_Matric_2) {
		DefaultCheckKey_Matric_2 = defaultCheckKey_Matric_2;
	}
	public String getDefaultCheckKey_Matric_3() {
		return DefaultCheckKey_Matric_3;
	}
	public void setDefaultCheckKey_Matric_3(String defaultCheckKey_Matric_3) {
		DefaultCheckKey_Matric_3 = defaultCheckKey_Matric_3;
	}
	public String getTimelinessCheckScore() {
		return TimelinessCheckScore;
	}
	public void setTimelinessCheckScore(String timelinessCheckScore) {
		TimelinessCheckScore = timelinessCheckScore;
	}
	
	
}
