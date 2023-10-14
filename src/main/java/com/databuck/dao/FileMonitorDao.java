package com.databuck.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMHistory;
import com.databuck.bean.FileMonitorMasterDashboard;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;


public interface FileMonitorDao {
	public List<FileMonitorRules> getCurrentTimeMonitorRules(String timeStr, int currentDay);

	public List<FileTrackingSummary> getActiveRulesSummary(String dailyRulesIds, String trackingDate, String timeStr,
			int currentDay);

	public void addFileTrackingHistoryRequest(FileTrackingHistory fileTrackingHistory);

	public FileTrackingHistory getFileTrackingHistoryRequest(long id);

	public List<FileMonitorRules> findFileMonitorRules(String bucketName, String folderPath);

	public void addFileTrackingSummary(FileTrackingSummary fileTrackingSummary);

	public List<FileTrackingSummary> getFileTrackingSummaryForRule(long fMonitorRuleId, String trackingDate,
			Integer dayOfWeek, String hourOfDay);

	// Adding File Monitor Rules
	public boolean addMonitorRule(FileMonitorRules fileMonitorRules);

	public List<FileTrackingSummary> getFileTrackingRulesSummaryDetails(Long idApp);

	public List<FileTrackingHistory> getFileTrackingHistoryDetails(Long idApp);

	public List<FileMonitorRules> getAllFileMonitorRuleDetailsByIdApp(long idApp);

	public int updateFileMonitorRulesById(long id, FileMonitorRules fm);

	public boolean deleteFileMonitorRuleById(long id);
	
	public FileMonitorRules getFileMonitorRulesById(long id);
	
	// Get list  of Applications with File Monitoring type
	public List<FileMonitorMasterDashboard> getFileMonitoringAppsList();
	
	// Get list  of Applications with File Monitoring type(Snowflake, S3, Azure) - new code from file monitoring engine(backend)
	public List<FileMonitorMasterDashboard> getDbkFileMonitoringAppsList();
	
	public List<FileMonitorRules> getFileMonitorRulesByAppId(long idApp);

	public void updateFMRuleLastProcessingTime(long idApp, long fmRuleId, Date lastProcessedTime);

	public Date getMaxDateForRule(long idApp, long fmruleId);

	public long getAvgFileSizeOfRule(long idApp, long fmRuleId);

	public List<ListApplications> getContinuousFileMonitoringAppsList();
	
	public List<ListApplications> getContinuousFileMonitoringAppsListByType(String fileMonitoringType);
	
	public FileTrackingSummary getLatestFileTrackingSummaryForRule(long idApp, long id, String trackingDate);

	public ListDataSource getDataSourceForFilePathAndPattern(String folderPath, String filePattern);

	public void updateFolderNameOfTemplate(long idData, String folderName);
	
	public Long getDQApplicationsForIdData(Long idData);
	
	public String getOverallCountStatusOfFM(long idApp);
	
	public String getOverallFileSizeStatusOfFM(long idApp);

	public ListDataSource getLatestDataSourceForIdDataSchema(long idDataSchema);

	public List<FileMonitorRules> getFileMonitorRulesForSchema(long idDataSchema);

	public List<FileTrackingHistory> getValidUnProcessedFilesFromHistory();

	public void updateFileExecutionStatusAndMsg(long fileTrackingHistoryId, String fileExecutionStatus, String fileExecutionStatusMsg);

	public Long getS3IAMConnectionForFilePathAndPattern(String bucketName, String folderPath,
			String filePattern, String partitionedFolders);

	public Long getTemplateIdByPattern(long idDataSchema, String subFolderName, String fileNamePattern);
	
	public void saveTemplateMultiPatternInfo(long idDataSchema, long idData, String subFolderName,
			String fileNamePattern);

	public boolean isListDataSchemaActive(Long idDataSchema);
	
	public boolean isTemplateActive(Long idData);

	public void updateTemplateIdInMultiPatternInfo(Long idDataSchema, String subFolderName, String fileNamePattern,
			Long idData);

	public void insertToDbkFileMonitorRules(Object[] objarr);
	public String getFileMonitorTypeByIdApp(long idApp);
	public List<DBKFileMonitoringRules> getDBKFileMonitorDetailsByIdApp(long idApp);
	public void updateToDbkFileMonitorRules(DBKFileMonitoringRules fileMonitoringRules, long idApp);
	public boolean isRowIdExitForDbkFileMonitorRules(long rowId);
	public boolean deleteDBKFileMonitorRuleById(long id, long idApp);
	
	public Map<String, Object> getAzureConnectionByAccountKeyAndContainer(String account_name,String container_name,String folderPath);
	
	public int saveDbkFMLoadHistory(DbkFMHistory fmlh);

	public List<DBKFileMonitoringRules> getRulesListForFile(DBKFileMonitoringRules dbk_file_monitor_rules);

	public int getArrivalCountOfFile(DBKFileMonitoringRules dbk_file_monitor_rules, String load_date, int loaded_hour);

	public long saveDbkFMFileArrivalDetails(DbkFMFileArrivalDetails fmad);

	public Long getTemplateIdOfConnectionByTableName(long idDataSchema, String tableName);

	public Long getIncrementalDQApplicationsForIdData(Long idData);

	public ListDataSource getDataSourceForRowAdd(Long idData);

	public void updateIncrementalColStatus(long idData, String displayName);

	public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
			Double matchingThreshold, String incrementalMatching, String dateFormat, String leftSliceEnd,
			String fileMonitoringType, String continuousFileMonitoring, Long project_id, String createdByUser,
			Long domainId);

	public void updateIncrementalMatchingOfApplications(long idApp, String incrementalMatching);

	public Map<String, Object> getListDataSchemaDeatilsForidData(Long idData);

	public Map<String, Object> getS3BucketConnectionDetailsByName(String bucketName,String schemaType,String folderPath);

	int deleteFMRule(long id);

	public long getFMValidationByIdDataSchema(long idDataSchema);

	public boolean isDuplicateFMRuleForHourlyFiles(DBKFileMonitoringRules dbkFileMonitoringRules);

}
