package com.databuck.bean;

public class ValidationCreateReq {

	private String name;
	private String description;
	private String apptype;
	private String fileMonitoringType;
	private String matchapptype;
	private String selectedTables;
	private Double threshold = 0.0;
	private Integer domainId;
	private Long schemaid1;
	private Long schemaid2;
	private String schemaThresholdtype;
	private String schemaRc = "0.0";
	private String schematypename;
	private String prefix1 = "";
	private String prefix2 = "";
	private String dateformatid = "";
	private Integer windowTime;
	private String startTime;
	private String endTime;
	private Long idUser;
	private String createdBy;
	private Long projectId;
	private Integer dataDomainId;
	private Long sourceId;
	private String incrementalMatching = "N";
	private String leftsliceend;
	private String enableContinuousMonitoring;
	private String fmConnectionId;
	private Long idApp;

	private String reprofiling;
	private String validationJobSize;

	public String getValidationJobSize() {
		return validationJobSize;
	}

	public void setValidationJobSize(String validationJobSize) {
		this.validationJobSize = validationJobSize;
	}

	public String getReprofiling() {
		return reprofiling;
	}

	public void setReprofiling(String reprofiling) {
		this.reprofiling = reprofiling;
	}

	public Long getIdApp() {
		return idApp;
	}

	public void setIdApp(Long idApp) {
		this.idApp = idApp;
	}

	public String getFmConnectionId() {
		return fmConnectionId;
	}

	public void setFmConnectionId(String fmConnectionId) {
		this.fmConnectionId = fmConnectionId;
	}

	public String getEnableContinuousMonitoring() {
		return enableContinuousMonitoring;
	}

	public void setEnableContinuousMonitoring(String enableContinuousMonitoring) {
		this.enableContinuousMonitoring = enableContinuousMonitoring;
	}

	public String getLeftsliceend() {
		return leftsliceend;
	}

	public void setLeftsliceend(String leftsliceend) {
		this.leftsliceend = leftsliceend;
	}

	public String getIncrementalMatching() {
		return incrementalMatching;
	}

	public void setIncrementalMatching(String incrementalMatching) {
		this.incrementalMatching = incrementalMatching;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getDataDomainId() {
		return dataDomainId;
	}

	public void setDataDomainId(Integer dataDomainId) {
		this.dataDomainId = dataDomainId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}

	public String getFileMonitoringType() {
		return fileMonitoringType;
	}

	public void setFileMonitoringType(String fileMonitoringType) {
		this.fileMonitoringType = fileMonitoringType;
	}

	public String getMatchapptype() {
		return matchapptype;
	}

	public void setMatchapptype(String matchapptype) {
		this.matchapptype = matchapptype;
	}

	public String getSelectedTables() {
		return selectedTables;
	}

	public void setSelectedTables(String selectedTables) {
		this.selectedTables = selectedTables;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold_id) {
		this.threshold = threshold_id;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Long getSchemaid1() {
		return schemaid1;
	}

	public void setSchemaid1(Long schemaid1) {
		this.schemaid1 = schemaid1;
	}

	public Long getSchemaid2() {
		return schemaid2;
	}

	public void setSchemaid2(Long schemaid2) {
		this.schemaid2 = schemaid2;
	}

	public String getSchemaThresholdtype() {
		return schemaThresholdtype;
	}

	public void setSchemaThresholdtype(String schemaThresholdtype) {
		this.schemaThresholdtype = schemaThresholdtype;
	}

	public String getSchemaRc() {
		return schemaRc;
	}

	public void setSchemaRc(String schemaRc) {
		this.schemaRc = schemaRc;
	}

	public String getSchematypename() {
		return schematypename;
	}

	public void setSchematypename(String schematypename) {
		this.schematypename = schematypename;
	}

	public String getPrefix1() {
		return prefix1;
	}

	public void setPrefix1(String prefix1) {
		this.prefix1 = prefix1;
	}

	public String getPrefix2() {
		return prefix2;
	}

	public void setPrefix2(String prefix2) {
		this.prefix2 = prefix2;
	}

	public String getDateformatid() {
		return dateformatid;
	}

	public void setDateformatid(String dateformatid) {
		this.dateformatid = dateformatid;
	}

	public Integer getWindowTime() {
		return windowTime;
	}

	public void setWindowTime(Integer windowTime) {
		this.windowTime = windowTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
