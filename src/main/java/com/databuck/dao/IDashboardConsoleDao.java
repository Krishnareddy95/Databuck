package com.databuck.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.databuck.bean.DashboardCheckComponent;
import com.databuck.bean.DashboardColorGrade;
import com.databuck.bean.DashboardConnection;
import com.databuck.bean.DashboardConnectionValidion;
import com.databuck.bean.DashboardTableCount;
import com.databuck.bean.EssentialCheckRuleSummaryReport;
import com.databuck.bean.EssentialCheckSummaryReport;
import com.databuck.bean.ReportUIDQIIndexHistory;
import com.databuck.bean.ReportUIDashboardSummary;
import com.databuck.bean.ReportUIFailedAsset;
import com.databuck.bean.ReportUIFailedFilesSummary;
import com.databuck.bean.ReportUIOverallDQIIndex;
import com.databuck.bean.ReportUIPerformanceSummary;
import com.databuck.bean.ReportUIProjectCoverage;
import com.databuck.bean.ReportUITableSummary;
import com.databuck.bean.UserToken;
import com.databuck.dto.DashboardTableCountSummary;

public interface IDashboardConsoleDao {

	public List<DashboardConnection> getConnectionsForDashboard(long domainId, long projectId);

	public String updateConnectionsForDashboard(List<DashboardConnection> dashboardConnectionList, long domainId,
                                                long projectId);

	public List<DashboardColorGrade> getColorGrading(long domainId, long projectId);

	public void addColorGrading(long domainId, long projectId);

	public String updateColorGrading(List<DashboardColorGrade> dashboardColorGradeList);

	public List<DashboardCheckComponent> getCheckComponentList();

	public List<DashboardConnectionValidion> getConnectionValidtionMap(long domainId, long projectId,
                                                                       long connectionId);

	public String updateConnectionValidtionMap(List<DashboardConnectionValidion> dashboardConnectionValidionList);

	public void insertUserToken(UserToken userToken);

	public UserToken getUserDetailsOfToken(String token);

	public String getUserTokenStatus(String token);

	public void updateUserTokenStatus(String token, String status);

	public String checkForExistingUserToken(Long idUser, String email, String activeDirectoryUser);
	public UserToken checkForExistingUserTokenAndRefreshToken(Long idUser, String email, String activeDirectoryUser);

	public List<DashboardConnection> getEnabledConnectionsForDashboard(long domainId, long projectId);

	public ReportUIDashboardSummary getDashboardSummaryForConnection(long domainId,long projectId,
             DashboardConnection dashboardConnection, String startDate, String endDate);

	public List<String> getUniqueDatasourcesFromConnValidationMap(long domainId, long projectId);

	public ReportUITableSummary getSummaryForSourceTypeDateRange(long domainId,long projectId, String sourceType, String startDate,
                                                                 String endDate);

	public List<ReportUIPerformanceSummary> getDailyPerformanceTrend(long domainId, long projectId, long connectionId,
                                                                     String startDate, String endDate);

	public Map<String, Map<String, Double>> getDailyPassTrend(long domainId, long projectId, long connectionId, String startDate,
                                                              String endDate);

	public List<String> getSourceListForDatasource(long domainId, long projectId, String datasource);

	public List<String> getFileNameListForSource(long domainId, long projectId, String datasource, String source);

	public List<Long> getValdiationsForSourceFile(long domainId, long projectId, long connectionId, String datasource, String source,
                                                  String fileName);

	public Date getMaxDateForValidation(Long idApp, String startDate, String endDate);

	public boolean isDQComponentCheckEnabledForApp(Long idApp, String componentType);

	public List<DashboardCheckComponent> getChecksByComponentType(String component);

	public List<ReportUIFailedFilesSummary> getDailyFailedFiles(long domainId, long projectId, long connectionId,
                                                                String startDate, String endDate);

	public Double getTotalDQIOfIdAppForComponentType(Long idApp, String appMaxDate, String componentType);

	public List<EssentialCheckSummaryReport> getEssentialCheckSummaryDetailsOfFile(String processedDate, Long idApp);

	public List<EssentialCheckRuleSummaryReport> getEssentalCheckRuleSummaryDetailsOfFile(String processedDate,
                                                                                          Long idApp, long checkComponentId, String technicalName, String ruleName, String columnName);

	public String isDuplicateCheckIdentityEnabled(long idApp);

	public String isDuplicateCheckAllEnabled(long idApp);

	public ReportUIProjectCoverage getProjectCoverage(ReportUIProjectCoverage reportUIProjectCoverage,long domainId, long projectId);

	public List<ReportUIFailedAsset> getTopFailedAssets(long domainId, long projectId);

	public List<DashboardConnection> getDashboardEnabledConnectionsForProject(long domainId,long projectId);

	public ReportUIOverallDQIIndex getOverallDQIIndexForProject(long domainId, long projectId);

	public ReportUIDQIIndexHistory getIndexTrendHistory(long domainId, long projectId, String indexName, String startDate,
                                                        String endDate);
	public UserToken extendTokenValidity(String refreshtoken,String ExpiryTime);
	
	//public Integer insertIntoDashboardTableCount(DashboardTableCount dashboardTableCount);

	public Integer insertIntoDashboardTableCount(long id, long schemaId, long totalNoTable, long tableMonitored,
			long issuesDetected,long unValidatedTableCount, long totalRulesExecuted, long highTrustTable,long lowTrustTable,
			String hoursSaved, String date);
	public DashboardTableCountSummary getSumOfDashboardTableCount(); 
}
