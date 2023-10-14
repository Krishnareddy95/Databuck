package com.databuck.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.ColumnAggregateRequest;
import com.databuck.bean.DATA_QUALITY_Column_Summary;
import com.databuck.bean.DQSummaryBean;
import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ProcessData;
import com.databuck.bean.Project;
import com.databuck.bean.SqlRule;
import com.databuck.dto.DateVsDTSGraph;

public interface IResultsDAO {
	public Map<Long, String> getQualityApplicatinNamesAndId();
	public List readColumn_SummaryTable(String tableName,List<ListDataDefinition> displayNamesForNullTest);
	public List readTransactionset_sum_A1Table(long appId);
	public SqlRowSet readTransaction_DetailTable(String tableName, Long idApp);
	public List readTransaction_SummaryTable(String tableName,long idApp);
	//public String[] columnNames(long appId);
	public String dynamiccolumnNameforTransactionset(long appId);
	public Map<Long, String> getResultMasterTableAppNamesAndAppId();
	public SqlRowSet readTransaction_Detail_IdentityTable(String tableName, long idApp);

	//Dashboard Methods
	public long getIdappFromListApplictions(long idData);
	public long getIdDataFromListApplictions(long idApp);
	public SqlRowSet getTableNamesFromResultMasterTable(long idApp);
	public int getTableSize(String tableName);
	public SqlRowSet getTableMaxDateAndRun(long idApp);
	public List getTransactionSetTable(String tableName, Long idData);
	public SqlRowSet data_Quality__record_anomaly(String tableName, long idApp);
	public Long getRecordCount( ListApplications listApplicationsData, long idApp);
	public Long getAverageRecordCount( ListApplications listApplicationsData, long idApp);
	public Long getnonNullColumns(Long idData);
	public Long getnonNullColumnsFailed(String tableName, ListApplications listApplicationsData);
	public Long getAllFields(String tableName);
	public Long getIdentityFields(String tableName);
	public Long getAllFields(String tableName, ListApplications listApplicationsData);
	public Long getIdentityFields(String tableName, ListApplications listApplicationsData);
	public Long getRecordAnomalyTotal(String tableName, Long idApp);
	public Long getNumberOfRecordsFailed(String tableName, Long idApp);
	public SqlRowSet getIdDataAndAppNameFromListApplications(Long idApp);
	public Long getNumberofNumericalColumnsYes(Long idData);
	public Long getNumberofStringColumnsYes(Long idData);
	public Long getnumberofStringColumnsFailed(String tableName, ListApplications listApplicationsData);
	public Long getnumberofNumericalColumnsFailed(String tableName, ListApplications listApplicationsData);
	//graphs
	public List getRecordCountAnomalyGraphValues(String tableName);
	public List getNullCountGraph(String tableName);
	public List getAllfieldsGraph(String tableName, long idApp);
	public List getidentityfieldsGraph(String tableName, long idApp);
	public List getStringFieldStatsGraph(String tableName);
	public List getRecordAnomalyGraph(String tableName, long idApp);
	public List getNumericalFieldStatsGraph(String tableName);
	public List getfileNameandcolumnOrderValidationStatus(String tableName, long idApp);
	public List getAllfieldsandIdentityfields(String tableName, long idApp);
	public List getDisplayNamesForNullTest(Long idApp, Long idData);
	public List<DATA_QUALITY_Column_Summary> readColumn_SummaryTable(String tableName);
	public List getDisplayNamesForStringTest(Long idApp, Long idData);
	public List getDisplayNamesForNumericalTest(Long idApp, Long idData);
	public Map<String,String> getdatafromlistdftranrule(long idApp);
	public SqlRowSet getRulesTableData(String tableName , long idApp);
	public SqlRowSet getFrequencyUpdateDateTableData(String tableName, long idApp);
	public SqlRowSet getDataDriftTable(String tableName, long idApp);
	public String getRecordCountStatus(String tableName, long idApp);
	public String getDashBoardStatusForGroupEquality(long idApp, String tableName);
	public boolean getmeasurementFromListDataDefinition( Long idData);
	public String getMissingDatesFromTransactionset_sum_A1(long idApp);
	public String getStatusFromRecordAnomalyTable(Long idApp);
	public String getDashboardStatusForRCA(Long idApp, String recordCountAnomaly);
	public String getDashboardStatusForNullCount(Long idApp);
	public String getDashboardStatusFornumericalFieldStatsStatus(Long idApp);
	public String getDashboardStatusForstringFieldStatsStatus(Long idApp);
	public String getDashboardStatusForRecordAnomalyStatus(Long idApp);
	public String getDashboardStatusForAllFieldsStatus(Long idApp);
	public String getDashboardStatusForIdentityfieldsStatus(Long idApp);
	public String getDateForSummaryOfLastRun(long idApp);
	public boolean checkRunIntheResultTableForScore(long idApp, String recordCountAnomaly);
	public String CalculateScoreForRecordAnomaly(Long idData, long idApp, String recordCountAnomaly, String rcaStatus, ListApplications listApplicationsData);
	public String CalculateScoreForNullCount(Long idData, long idApp, String recordCountAnomaly, String rcaStatus,
			ListApplications listApplicationsData);
	public String CalculateScoreForallFields(Long idData, long idApp, String allFieldsStatus,
			ListApplications listApplicationsData);
	public SqlRowSet CalculateDetailsForDashboard(long idApp, String tabName,
			ListApplications listApplicationsData);
	public String CalculateScoreForNumericalField(Long idData, long idApp, String numericalFieldStatus,
			ListApplications listApplicationsData);
	public String CalculateScoreForStringField(Long idData, long idApp, String stringFieldStatus,
			ListApplications listApplicationsData);
	public String CalculateScoreForrecordFieldScore(Long idData, long idApp, String recordAnomalyNewStatus,
			ListApplications listApplicationsData);
	public List<DataQualityMasterDashboard> getMasterDashboardForDataQuality(Map<Long, String> resultmasterdata, List<ListApplications> listapplicationsData);
	public List<String> getallColumns(String tableName);
	public Map<String, String> checkRulesTable(ListApplications listApplicationsData);
	public String getScoreForDataDrift(ListApplications listApplicationsData);
	public List<DataQualityMasterDashboard> getDataFromDataQualityDashboard(Long projectId,List<Project> projlst);
	void createQualityCsv(Long projectId,List<Project> projlst);
	public Map<String, String> getDataFromListDFTranRuleForMap(long idApp);
	public Map<String, String> getDateAndRunForSummaryOfLastRun(Long idApp, String recordCountAnomaly);
	public Long calculateRecordCountForAggregateRecordCount(ListApplications listApplicationsData);
	public String getDashboardStatusForTimelinessCheck(Long idApp);
	public String CalculateScoreForTimelinessCheck(long idApp, ListApplications listApplicationsData);
	public Long getFailedRecordCountForTimelinessCheck(Long idApp);
	//Sumeet_11_09_2018
	public String updaterejectIndDrift(String tableName, String userName, String run, String uniqueValues, String colName, String dGroupVal,String dGroupCol);
	public String updaterejectIndValidity(String tableName, String dGroupCol, String run, String dGroupVal, String Date, String userName, String idApp);
	public String updaterejectIndGBRCA(String tableName, String dGroupCol, String run, String dGroupVal, String Date, String userName, String idApp);
	public String getMaxQuery(String tableName);
	public String updaterejectAllDrift(String tableName, String userName, int maxValue, String dateUpdate, String idApp,String columnname);
	public String updaterejectAllGBRCA(String tableName, String userName, int maxValue, String dateUpdate, String idApp);
	public String updaterejectAllValidity(String tableName, String userName, int maxValue, String dateUpdate, String idApp);

	public LinkedHashMap<String, String> getSqlRulesDashboardSummary(String sIdApp);
	public String getAggregateDQIForDataQualityDashboard(Long idApp);

	public SqlRowSet custom_rules_configured_count(long idData);
	public SqlRowSet global_rules_configured_count(long idData);
	public SqlRowSet sql_rules_configured_count(String idapp);

	/*-- Changes for Executive Summary ------ priyanka 15 jan 2020*/

	public String getAvgDQIForTodaysDtAllValidations(Long projectId);

	public String getNoOfFailedValidationsForLast30Days(Long projectId);

	public String getTotalNoOfSourcesForCurrentProjectByProjectId(String sSelectedProjects);

	public String getTotalNoOfValidationsForCurrentProjectByProjectId(Long projectId);
	public String updateRefTableData(Long row_id, String col_name, String update_val, String table_name);
	public String insertNewValueToRefTable(String columnName, String columnValue, String table_name);
	public List<String> getRefTableColValue(Long idData, String columname, String tablename);
	List<String> getMicrosegmentColValue(String tableName, String idApp);

	/**code by : Anant S. Mahale
	 * Date : 28thMarch2020
	 * @param projectId : from request
	 * @return : list of location names from locations table
	 */
	public List<String> getListOfLocationsbyProject(int projectId);

	public Map<String, List<String>> getLocatoinInfoTableData(List<String> listLocationName);
	public List<List<String>> formatedDataForTable(Map<String, List<String>> mapTableData);

	/* Approval Process*/
	public String getApprovalStatusFlag(Long idApp);
	/*Rule Catalog */
	public  Map<String, String> getValidationRunDetail(long  nIdApp);
	public SqlRowSet getRulesTableDataForExecDateRun(String tableName, long idApp, String execDate, long run);
	public SqlRowSet getFrequencyUpdateDateTableDataForExecDateRun(String tableName, long idApp, String execDate, long run);
	public String getDashBoardStatusForGroupEqualityForExecDateRun(long idApp, String tableName, String execDate,
			long run);
	public Long getRecordCountForExecDateRun(ListApplications listApplicationsData, long idApp, String execDate,
			long run);
	public Long getAverageRecordCountForExecDateRun(ListApplications listApplicationsData, long idApp, String execDate,
			long run);
	public long getRecordAnomalyTotalForExecDateRun(ListApplications listApplicationsData, String tableName, long idApp,
			String execDate, long run);
	public Long getNumberOfRecordsFailedForExecDateRun(String tableName, long idApp, String execDate, long run);
	public String getRecordCountStatusForExecDateRun(String tableName, long idApp, String execDate, long run);
	public long getnumberofStringColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run);
	public long getnonNullColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run);
	public long getnumberofNumericalColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run);
	public SqlRowSet calculateDashboardDetailsForExecDateRun(long idApp, String tableName, String execDate, long run);
	public String getAggregateDQIForDataQualityDashboardForExecDateRun(long idApp, String execDate, long run);
	public boolean updateAgingCheckForRuleInRuleCatalog(int ruleCatalogRowId, String status);
	public String getAgingCheckValueForRule(int ruleCatalogRowId);
	public SqlRowSet getValidationCheckDetails(long idApp, String resultTableName, String strMaxDate, int maxRun);
	public SqlRowSet getDefaultValueValidationCheckDetails(long idApp, String strMaxDate, int maxRun);
	public long getDomainIdByIdData(long idData);
	public long getDimensionIdByIdData(long idData);
	public String getDimensionName(long dimensionId);
	public String getRuleDescriptionByCustomRuleId(long idListColrules);

	//as per Date
	public String getAvgDQIForTodaysDtAllValidationsAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate , String CurrentPeriodToDate);
	public int getNoOfFailedValidationsForLast30DaysAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate , String CurrentPeriodToDate);
	public int getTotalNoOfSourcesForCurrentProjectByProjectIdAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate , String CurrentPeriodToDate);
	public int getTotalNoOfValidationsForCurrentProjectByProjectIdAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate , String CurrentPeriodToDate);

	public String getApplicationsForSelectedProjects(String sSelectedProjects);
	public String getApplicationsForSelectedProjectsByDataDomain(String sSelectedProjects, int nDataDomain);

	public double getAverageDqIndexByProjectsAndPeriod(String sSelectedProjects, String sPeriodStartDate , String sPeriodToDate);
	public String getLatestAverageDqIndexAndDate(String sSelectedProjects);

	public SqlRowSet calculateAvgMetricsForCheck(String validationIdsList, String checkName);
	public Integer getTotalCustomRulesExecutedForSchema(String validationIdsList);
	public Integer getTotalGlobalRulesExecutedForSchema(String validationIdsList);
	public Double getAggregateDQIForSchema(String validationIdsList);
	public Integer getTotalExecutedValidationCountForSchema(String validationIdsList);
	public List<ExternalAPIAlertPOJO> getFailedExternalNotificationDetails(String externalApiType, String startDate, String endDate);
	public HashMap<String, Double> getDqGraphData(HashMap<String, String> oDateRange, String sWhichPeriod, String sApplicationIds);
	public String getLatestDqIndex(String sApplicationIds);
    public String getAppIdsForDomainProject(int domainId, int projectId, int nDataDomain);
    public Map<Long,String> getValidationMapByDomainAndProject(int domainId, int projectId);
    public Map<Long,Double> getLeastDQIsByValidations(String validationIds);
	public double getAverageDqIndexByDomainProjectsForDataDomain(String periodStartDate, String periodToDate, String applicationIds);
	public double getAverageDqIndexByDomainProject(String applicationIds, String periodStartDate , String periodToDate);
	public String getAppIdsListForDomainProject(int domainId, int projectId, String fromDate, String toDate);
	public List<DataQualityMasterDashboard> getAppIdsListSummary(String applicationIds, String fromDate, String toDate);
	public List<DataQualityMasterDashboard> getAppIdsListSummaryByFilter(String applicationIds,String filterCondition, String fromDate, String toDate);
    public String getFilteredAppIdsListForDomainProject(int domainId, int projectId,long idData, String fromDate, String toDate);
    
 //For max Length Check
    public String getMaxLengthResultTypeFromResultMasterTbl(Long idApp);
    
    public List<DQSummaryBean> getDQSummaryDataForidApp(long idApp, String execDate, long run);
	
	public List<String> getDashboardTestNamesByIdapp(long idApp);
	
    public String getAgingCheckValueForRuleFromStaging(int ruleCatalogRowId);
	public boolean updateAgingCheckForRuleInStagingRuleCatalog(int ruleCatalogRowId, String status);
	public boolean undoDataDriftRejectInd(String tableName, String userName, String run, String uniqueValues,
			String colName, String dGroupVal, String dGroupCol);
	
	public JSONArray getPaginatedResultsJsonData(HashMap<String, String> oPaginationParms) throws Exception;
	public List<ProcessData> getProcessDataTableDetails(long idApp, String fromDate, String toDate);
	public List<SqlRule> getSqlRules(long idApp, String fromDate, String toDate);
	public List<Date> getListOfDateFromColumnSummary(String paramStrTableName, String idApp);
	public List<Integer> getListOfRunFromColumnSummary(String tableName, String idApp);
	public HashMap<String, Double> getDqGraphData(String fromDate, String toDate, String string, String applicationIds);
	public List<Map<String, Object>> getLeastDQIsByValidations(String validationIds, String fromDate, String toDate);
	List<DataQualityMasterDashboard> getValidationDetailsByFilter(String applicationIds, String fromDate, String toDate,
			String connectioName, String fileName);
	public List<DataQualityMasterDashboard> getValidationDetailsByFilter(String applicationIds, String filterCondition,
			String fromDate, String toDate, String connectionName, String fileName);
	public List<Map<String, Object>> getTotalTableMonitored(String fromDate, String toDate);
	
	public SqlRowSet getColumnAggregateDetailsForMicrosegmentNullCheck(long idApp, String tableName, String strMaxDate,
			int maxRun, ColumnAggregateRequest columnAggregateRequest, List<String> all_microseg_cols,
			List<String> groupby_microseg_cols);

	public SqlRowSet getColumnAggregateDetailsForGlobalRule(long idApp, String tableName, String strMaxDate, int maxRun, ColumnAggregateRequest columnAggregateRequest);

	public List<String> getMicrosegmentsForNullCheck(long idApp, String strMaxDate, int maxRun);

	public List<String> getMicrosegmentsForCheck(long idApp, String tableName, String strMaxDate, int maxRun);
	public List<String> getMicrosegmentsForDuplicateCheck(long idApp, String tableName, String strMaxDate,
			int maxRun, String type);
	
	public SqlRowSet getColumnAggregateDetailsForMicrosegmentGlobalRules(long idApp, String tableName,
			String strMaxDate, int maxRun, ColumnAggregateRequest columnAggregateRequest,
			List<String> all_microseg_cols, List<String> groupby_microseg_cols);
	public Map<String, String> getColumnDimensionsForCheck(String check_name, long idApp);
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentGlobalRules(long idApp, String tableName, String strMaxDate,
			int maxRun, List<String> all_microseg_cols);
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentNullCheck(long idApp, String tableName, String strMaxDate,
			int maxRun, List<String> all_microseg_cols);
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentDuplicateCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols, String type);
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentGlobalRules(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols);
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentDuplicateCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols, String type);
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentNullCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols);
	public JSONArray getRCATrendChart(Long idApp,String fromDate,String toDate);
	public JSONObject getDeviationStatisticsByTestName(long idApp, String testName);
	public List<String> getDashboardSummaryTestNamesByIdApp(long idApp);
	public SqlRowSet getDashboardSummaryByCheck(long idApp, String testName, int run,String date);
	public HashMap<String, Double> getDqGraphEnterpriseData(String fromDate, String toDate);
	public String getLatestDqI(String fromDate, String toDate);
	public int getTotalRunCount(long idApp);
	public Map<String,DataQualityMasterDashboard> getPreviousRunDetailsForValidation(long idApp);
	public Map<String,DataQualityMasterDashboard> getLatestRunDetailsForValidation(long idApp);
	public Map<String,DataQualityMasterDashboard> getHistoricalAggregateDetailsForValidation(long idApp);
	public Map<String, DataQualityMasterDashboard> getHistoricalOverallAggregateDetailsForValidation(long idApp);
	public double getLatestOverAllDtsDetailsForValidation(long idApp);

	public SqlRowSet getColumnAggregateDetailsForNullCheck(long idApp, String tableName, String strMaxDate,
																	   int maxRun, ColumnAggregateRequest columnAggregateRequest);
	public Boolean checkRecordInRefTable(String columnName, String columnValue, String table_name, Map<String, String> metadata);
	public Map<String, String> getRefTableColumns(String tableName);
	public int deleteDataFromRefTable(String tableName, String dbkRowIds);
	public Map<String, Object> getAvgDtsByIdApp(long idApp);
	public int updateApplicationPropertyByNameAndCategory(String propertyCategoryName, String propertyName, String propertyValue);
	public JSONArray getGraphDataForNullStatistics(long idApp, String colName);
	public List<Integer> getAllResultsforIdApp(long idApp);
	public Integer getTotalExecutedRulesCount(long idApp,String currentDateStr,String yesterdayStr);
	public Integer getTotalFailedRecordCountforAppId(long idApp,String currentDateStr,String previousYearStr);

	public JSONArray getGraphDataForLengthStatistics(long idApp, String colName);
	public JSONArray getGraphDataForRegexPatternStatistics(long idApp, String colName);
	public JSONArray getGraphDataForDuplicateStatistics(long idApp, String colName);
	public List<DateVsDTSGraph> getDtsVsDateGraphData(Long appId,String fromDate,String toDate);
	public JSONArray getGraphDataForBadDataStatistics(long idApp, String colName);
	public Integer getTotalRecordCountforAppId(long idApp,String currentDateStr,String previousYearStr);
	public List<Long> getAvgDQIofLastTwoRuns(Long appId,String test);
	public List<Long> getAvgDQIAggregateofLastTwo(Long appId);
}
