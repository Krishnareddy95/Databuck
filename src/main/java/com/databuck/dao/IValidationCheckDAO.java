package com.databuck.dao;

import com.databuck.bean.*;
import com.google.common.collect.Multimap;
import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface IValidationCheckDAO {

	public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
			Double matchingThreshold, String incrementalMatching, String dateFormat, String leftsliceend,String fileMonitoringType,String continuousFileMonitoring, Long project_id, String createdByUser,Integer domainId,Integer data_Domain_id,String stringStatCheck,String validationJobSize);

	
	public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
			Double matchingThreshold, String incrementalMatching, String dateFormat, String leftsliceend,String fileMonitoringType,String continuousFileMonitoring, Long project_id, String createdByUser,Integer domainId);

	public String getAppTypeFromListApplication(Long idApp);

	List<ListApplicationsandListDataSources> getdatafromlistappsandlistdatasources(Long project_id,
			List<Project> projlst, String toDate, String fromDate);

	List<ListDataSource> getdatafromlistdatasource(Long idData);
	public int updateFMConnectionDetails(long idApp,long schemaId );
	public int checkFMConnectionDetailsForConnection(long schemaId );
	int deletefromlistapplications(Long idApp, String mode);

	int updateintolistdatadefinitions(Long idData, String recordCount, Double nullCountTextNull,
			String incrementalCheck, String numericalStats, Double numericalStatsTextNull, String stringStat,
			Double stringStatTextNull, String recordAnomaly, Double recordAnomalyThresholdnull,
			Double dataDriftCheckTextnull, Double outofNormThresholdnull);

	public int updateintolistapplication(String outofNorm, String columnOrderVal, String fileNameVal, Double rows,
			String nameofEntityColumn, Long idApp, String nullCount, String recordAnomaly, String numericalStats,
			String stringStat, String dataDriftCheck, String updateFrequency, Double frequencyDaysnull,
			String recordCount, Double dFSetComparisonnull, String timeSeries, String recordCountAnomalyType,
			String applyRules, String applyDerivedColumns, String csvDirectory, String groupEquality,
			Double groupEqualityThreshold, String dGroupNullCheck, String dGroupDateRuleCheck, ListApplications listApplication);

	public int insertintolistdftranrule(Long idApp, String duplicateCount, Double duplicateCountTextNull,
			String duplicateCountAll, Double duplicateCountAllTextNull);
	public long getlatestListDataSourceIdData() ;
	public Map getdatafromlistdftranrule(Long idApp);

	public int updatedataintolistdftranrule(Long idApp, Long thresholdAll, Long thresholdIdentity);

	public int insertintolistdfsetruleandtranrule(Long idApp, Double dFSetComparisonnull, String duplicateCheck);

	public List getDataFromListDataDefinition(Long idData);

	public ListApplications getdatafromlistapplications(Long idApp);

	public int updateintolistapplications(Long idApp, String numericalStats, String recordAnomaly, String nullCount,
			String stringStat);

	public String getNameFromListDataSources(Long idData);
	public void deleteEntryFromListDMRulesWithIdApp(long idApp);
	public int insertFMConnectionDetails(long idApp,long idDataSchema);
	public Long insertIntoListDMRules(Long idApp, String matchtype, String matchCategory);

	public List getDataFromlistRefFunctions();

	public List<String> getDisplayNamesFromListDataDefinition(Long idData);
	
	public List<String> getDisplayNamesForMeasurementKeys(Long idData);

	public int insertDataIntoListDMCriteria(String expression, Long idDM, Long idApp);

	public void updateDataIntoListApplications(Long idApp, Long rightSourceId, double absoluteThresholdId,
			String groupbyid, String measurementid, String dateFormat, String rightSliceEnd, String expression,
			String matchingRuleAutomatic, String recordCount, String primaryKey, double unMatchedAnomalyThreshold);

	public String getDataLocationInListDataSources(Long idData);

	public List getMatchingRules(Long idApp);

	public int updateMatchingRuleAutomaticIntoListDMCriteria(List<String> rightSourceColumnNames,
			List<String> leftSourceColumnNames, Long idDM, Long idApp);

	public List<String> validationCheckprerequisite(Long idApp, Long idData, String duplicateCountAll, String nullCount,
			String duplicateCount, String recordAnomaly, String stringStat, String numericalStats);

	public long insertIntoListSchedule(long idApp, String time);

	public int insertintoscheduledtasks(Long idApp, Long idScheduler);

	public String duplicateValidationCheckName(String validationCheckName);
	public String duplicateValidationCheckNameNew(String validationCheckName);

	public int insertIntoListFmRules(Long idApp, String dupCheck);

	public Multimap<String, Double> getDataFromListDfTranRule(Long idApp);

	public String getTimeSeriesForIdApp(Long idApp);

	public int updateintolistdftranrule(Long idApp, String duplicateCount, Double duplicateCountTextNull,
			String duplicateCountAll, Double duplicateCountAllTextNull);

	public String getDataLocationFromListDataSources(Long idData);

	// date rule changes 8jan2019
	public int updateintolistapplicationforCustomize(String outofNorm, String columnOrderVal, String fileNameVal,
			double d, String nameofEntityColumn, Long idApp, String nullCount, String recordAnomaly,
			String numericalStats, String stringStat, String dataDriftCheck, String updateFrequency,
			int frequencyDaysnull, String recordCount, Double dFSetComparisonnull, String timeSeries,
			String recordCountAnomalyType, String applyRules, String applyDerivedColumns,String badData,String lengthCheck,String maxLengthCheck,String dateRuleChk,
			ListApplications listApplication);

	public int insertintolistStatisticalMatchingConfig(listStatisticalMatchingConfig listStatisticalMatchingConfig);

	public long getNamefromlistDataSources(Long idApp);

	public listStatisticalMatchingConfig getDataFromlistStatisticalMatchingConfig(Long idApp);

	public int updateintolistStatisticalMatchingConfig(listStatisticalMatchingConfig listStatisticalMatchingConfig);

	public int updateApplicationNameWithIdApp(String name, long idApp);

	public int insertintolistModelGovernance(Long idApp, String modelGovernanceType, String modelIdCol,
			String decileCol, double expectedPercentage, double thresholdPercentage);

	public listModelGovernance getDataFromListModelGovernance(Long idApp);

	public void updateIntoListApplicationForModelGovernance(ListApplications la);

	public int insertintolistModelGovernanceForScoreConsistency(Long idApp, String modelGovernanceType,
			String leftSourceSliceStart, String leftSourceSliceEnd, String rightSourceSliceStart,
			String rightSourceSliceEnd, String matchingExpression, String measurementExpression, double scThreshold,
			String sourceDateFormat, String modelIdCol, String decileCol);

	public int updateintolistModelGovernanceForScoreConsistency(Long idApp, String modelGovernanceType,
			String leftSourceSliceStart, String leftSourceSliceEnd, String rightSourceSliceStart,
			String rightSourceSliceEnd, String matchingExpression, String measurementExpression, double scThreshold,
			String sourceDateFormat, String modelIdCol, String decileCol);

	public int updateintolistModelGovernance(Long idApp, String modelGovernanceType, String modelIdCol,
			String decileCol, double expectedPercentage, double thresholdPercentage);

	public Map<Long, String> getModelGovernanceAppTypeFromListApplications(String appType);

	public int updateModelGovernanceDashboardIntoListApplications(String modelGovernanceDashboard, Long idApp);

	public int saveDataIntolistApplicationForDataMatchingGroup(Long idApp, Long secondSourceId,
			double absoluteThresholdId);

	public int updateintolistapplicationForAjaxRequest(ListApplications listApplications);

	public boolean checkTheConfigurationForBuildHistoric(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForPatternCheckTab(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);
	
	public boolean checkTheConfigurationForDefaultPatternCheckTab(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForDupRowIdentity(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForDupRowAll(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForDataDrift(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForNumField(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForstringField(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForRecordAnomaly(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForNonNullField(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public boolean checkConfigurationForDGroupNullCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public String getDuplicateCheckFromListDFSetRule(Long idApp);

	public void updateintolistdfsetruleandtranrule(Long idApp, Double recordCountAnomalyThreshold,
			String duplicateCheck);

	public boolean checkTheConfigurationFordGroup(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForapplyRules(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationForapplyDerivedColumns(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public boolean checkDataTemplateForDGroup(List<ListDataDefinition> listdatadefinition);

	public void updateDataIntoListApplication(long idApp, long rightSourceId);

	public void insertDataIntoListDMCriteriaForMeasurementMatch(Long measurementIdDM, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols);
	public boolean checkMeasurementColumnForLeftTemplate(Long leftSourceId);

	public boolean checkMeasurementColumnForRightTemplate(Long rightSourceId);

	public void deleteFileManagementFromListFMRules(Long idApp);

	public boolean checkDgroupColumnForLeftTemplate(Long leftSourceId);

	public boolean checkDgroupColumnForRightTemplate(Long rightSourceId);

	public void deleteEntryFromListDMRules(Long idApp, String type);

	public boolean checkWhetherTheSameDgroupAreSelectedInDataTemplate(String expression, Long leftSourceId);

	public boolean checkWhetherIncrementalForLeftTemplate(Long leftSourceId);

	public boolean checkForIncrementalInListDataAccess(Long idData);
	public boolean checkFordateRuleCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public String getMatchingExpressionFromListDMRules(Long idApp);

	public String getSchemaTypeFromListDataSchema(String idDataSchema);

	public Integer getCountFromResult_masterById(Long idApp);

	public int deleteEntryFromListDMRulesWithIdDm1(long idDm);
	
	public void deleteEntryFromListDMRulesWithIdDm(long idDm);

	public Long getIddmFromListDMRules(Long idApp, String matchType2);

	public Long updateListDMRulesForPrimaryKeyMatching(Long idDM, Long idApp, Long rightSourceId, Long leftSourceId);

	public String getAutomaticDateFormat(ListApplications listApplications);


	public List getdatafromlistappsandlistdatasourcesId(long idApp);

	public Long getListApplicationsFromIdData(Long idData);

	public boolean checkTheConfigurationForTimelinessKeyField(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public boolean checkTheConfigurationForDefaultCheckField(List<ListDataDefinition> listdatadefinition,
			ListApplications la);

	public boolean checkTheConfigurationDefaultCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);

	public boolean checkTheConfigurationPatternCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);
	
	public boolean checkTheConfigurationDefaultPatternCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications listApplications);

	public boolean checkTheConfigurationDateRuleCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);

	public boolean checkConfigurationDgroupDateRuleCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);

	//sumeet
	public boolean checkTheBadData(List<ListDataDefinition> listdatadefinition, ListApplications listApplications);

	public boolean defaultCheckFlag(List<ListDataDefinition> listdatadefinition, ListApplications listApplications);

	public boolean checkIfTheFileIsProcessed(String filePath);
	public void insertIntoInProcessFiles(String filePath);

	// Changes for length Check
	public boolean checkForLengthCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);

	// Changes for Max length Check
		public boolean checkForMaxLengthCheck(List<ListDataDefinition> listdatadefinition, ListApplications la);

	
	//changes for export
	public List getdatafromlistappsandlistdatasourcesForExport();

	public String getDataLocationForIdApp(Long idApp);
	public List<String> getTemplateListDataSource(Long projectId);
	public long getListDataSourceIdData(String templatename);

	//method for getting validation names in search box
	public List<String> getValidationNames(Long projectId);

	public ListDataSource getTemplateDetailsForAppId(long idApp);

	public int updateintolistapplicationForFoundationChecksAjaxRequest(ListApplications listApplications);
	public int updateintolistapplicationForEssentialChecksAjaxRequest(ListApplications listApplications);
	public int updateintolistDFSetComparisonRule(Long idApp, String duplicateCheck);
	public int updateintolistapplicationForAdvancedChecksAjaxRequest(ListApplications listApplications);

	public int updateintolistdatadefinitionsAdCheckTab(Long idData, String numericalStats, Double numericalStatsTextNull, String recordAnomaly,
			Double recordAnomalyThresholdnull);

	public boolean checkTheConfigurationForMicrosegment(List<ListDataDefinition> listdatadefinition, ListApplications la);

	public List<String> getMatchValueColumns(Long idData);

	public void insertDataIntoListDMCriteriaForRollMatch(Long measurementIdDM, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols);

	public void updateRollTargetSchemaIdForIdApp(Long idApp, Long targetSchemaId) ;

	public List<String> getDisplayNamesFromListDataDefinitionForReftables(Long idData);

	List<String> getPrimaryColumns(Long idData);
	public List<String> getDisplayNamesFromListDataDefinitionFordGroup(long idData);
	public long getIdDataTFromListApplication(Long idApp);

	public void updateRollTypeForIdApp(Long idApp, String rollType);

	public Map<String, String> getRuleCatalogApprovalStatus(Long idApp);

	public boolean isRuleCatalogExists(long  nIdApp);

	public List<String> getDisplayName(Long leftSourceId);
	
	public List<DataDomain> getAllDataDomainNames();

	public Long copyValidation(Long idApp, String newValidationName, String createdByUser);

	public List<String> getNonDerivedTemplateList(Long projectId);

	public List<ListApplicationsandListDataSources> getListApplicationsByIdData(long idData);

	public Map<String, Object> getDateRunForUniqueId(String uniqueId);

	public Date getMaxDateForTable(String tableName, long idApp);

	public int getMaxRunForDate(String strMaxDate, String tableName, long idApp);

	public String getValidationId(String projectid, String fromDate, String toDate);

	public String getApproveStatusById(int statusCode);

	public String getTestRunByUniqueId(String uniqueId, long idApp);

	public boolean updateTemplateIdOfValidation(Long newIdApp, Long newIdData);

	public boolean linkDefectCodeToRule(long idApp, long ruleId, String defectCode);

	public int getRuleCatalogCreateStatus();

	public boolean isValidationInRunnableStatus(long nIdApp);

	public String getRunnableStatusRowIds();

	public List<Long> getValidationIdListForSchema(long idDataSchema);
	
	public int getCurrentDataDomainForIdApp(long idApp);

	public HashMap<Integer, String> getAllValidationDataDomains(boolean lOnlyActive);
	
	public Map<String, String> getStagingRuleCatalogApprovalStatus(Long nIdApp);

	public boolean checkTestRunByExcecutionDateAndRun(long idApp, String executionDate, long run);


	public  JSONArray getPaginatedValidationsJsonData(HashMap<String, String> oPaginationParms) throws Exception;

	public void updateFoundationChecksDetailsToListApplications(ListApplications listApplications);

	public boolean updateForgotRunStatusOfValidationLatestRun(long idApp,String maxExecDate,long maxExecRun,String checkValue,String tableName);

	public void deleteStagingDbkFileMonitorRules(Long idApp);
	
	public void deleteDBkFileMonitoringRules(long idApp);

	public void copyDbkFileMonitorRulesFromStaging(long idApp);

	public Long getDbkFileMonitorRulesCountInStaging(Long idApp);

	public List<DBKFileMonitoringRules> getStagingDbkFileMonitorRules(Long idApp);

	public List<String>  getPrimaryKeyListDataDefinition(long idData);

	public Integer getGlobalRulesCountByTemplateId(long idData);

	public boolean enableApplyRules(long idApp);
	
	public Long getMaxValidationByIdData(Long idData);

	public JSONArray getAllDataAsKeyValueByTableName(String tableName,JSONArray tableColumnNames) ;

	public Long getMaxMicrosegmentValidationByIdData(Long idData);

	public List<String> getMicrosegmentTemplateListDataSource(Long projectId);


	public int insertDataIntoListDMCriteria(String trim, Long idDM, Long idApp, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols);

	public void insertIntolistdfsetruleandtranrule(long idApp);


	public ListDmCriteria getlistDMCriteriaDetailsByID(long idDMCriteria);


	public Map<String, Object> getListDMRulesByIdDM(Long idDM);

	public boolean isDuplicateCheckEnabled(long idApp);

	public ListApplications getListApplicationForRCA(Long idApp, long projectId, long domainId , String validationCheckName);

	public boolean updateValidationJobsize(int idApp,String validationJobSize);

	}