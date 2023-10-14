package com.databuck.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.databuck.aver.bean.ABCCheckFailedReport;
import com.databuck.aver.bean.ABCCheckPerformanceSummary;
import com.databuck.aver.bean.ABCCheckRuleSummaryReport;
import com.databuck.aver.bean.ABCCheckSummaryReport;
import com.databuck.aver.bean.AVERDashboardSummary;
import com.databuck.aver.bean.AVERPerformanceSummary;
import com.databuck.aver.bean.FMFilePerformanceSummary;
import com.databuck.aver.bean.FileCheckReport;
import com.databuck.aver.bean.TrendCheckDataDriftReport;
import com.databuck.aver.bean.TrendCheckFailedReport;
import com.databuck.aver.bean.TrendCheckMicrosegmentReport;
import com.databuck.aver.bean.TrendCheckPerformanceSummary;
import com.databuck.aver.bean.TrendCheckSummaryReport;
import com.databuck.aver.bean.TrendCheckTrendGraphReport;
import com.databuck.bean.FileTrackingHistory;

public interface AverSummaryDao {

	public AVERDashboardSummary getFileMonitoringSummaryForDateRange(String startDate, String endDate);

	public AVERDashboardSummary getABCCheckSummaryForDateRange(String startDate, String endDate);

	public AVERDashboardSummary getTrendCheckSummaryForDateRange(String startDate, String endDate);

	public List<FileCheckReport> getfmFailedFilesReportForDateRange(String startDate, String endDate);

	public FileCheckReport getFMSummaryDetailsOfFile(String processedDate, String sourceType, String source,
			String fileName);

	public List<String> getUniqueTypeListFromLocMap();

	public Map<Integer, String> getUniqueLocationsList();

	public List<Long> getValdiationsForSourceFileAndLocation(String sourceType, String source, String fileName,
			Integer locationId);

	public Date getMaxDateForFMValidation(Long idApp, String startDate, String endDate);

	public List<FileTrackingHistory> getFMResultsForDate(Long idApp, Date maxDate);

	public List<String> getSourceListForSourceType(String sourceType);

	public Integer getLocationIdByName(String locationName);

	public Map<String, Long> getFileCountSummary(List<Long> validationList, String startDate, String endDate);

	public Double getTotalDQIOfIdAPPForDate(Long idApp, String abc_maxDate);

	public Double getTrendCheckDQIOfIdAPPForDate(Long idApp, String trend_Date);

	public Date getMaxDateForValidation(Long idApp, String startDate, String endDate);

	public List<Long> getValdiationsForLocation(Integer locationId);

	public Map<String, Map<String, Long>> getFMPerformanceSummary(List<Long> validationList, String startDate,
			String endDate);

	public Map<String, Map<String, Double>> getFMPassTrendSummary(List<Long> validationList, String startDate,
			String endDate);

	public List<Long> getValdiationsForSourceLocation(String sourceType, Integer locationId);

	public List<String> getFileNameListForSource(String sourceType, String source);

	public List<FMFilePerformanceSummary> getFMDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate);

	public List<TrendCheckFailedReport> getTrendCheckFailedReportForDateRange(String startDate, String endDate,
			List<Long> validationList);

	public List<TrendCheckSummaryReport> getTrendCheckSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId);

	public List<TrendCheckPerformanceSummary> getTrendCheckDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate, Integer locationId);

	public List<AVERPerformanceSummary> getABCCheckPerformanceSummary(List<Long> validationList, String startDate,
			String endDate);

	public List<AVERPerformanceSummary> getTrendCheckPerformanceSummary(List<Long> validationList, String startDate,
			String endDate);

	public Map<String, Map<String, Double>> getABCCheckPassTrendSummary(List<Long> validationList, String startDate,
			String endDate);

	public Map<String, Map<String, Double>> getTrendCheckPassTrendSummary(List<Long> validationList, String startDate,
			String endDate);

	public List<ABCCheckFailedReport> getABCCheckFailedReportForDateRange(String startDate, String endDate,
			List<Long> validationList);

	public List<ABCCheckPerformanceSummary> getABCCheckDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate, Integer locationId);

	public List<ABCCheckSummaryReport> getABCCheckSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId);

	public List<ABCCheckRuleSummaryReport> getABCCheckRuleSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String ruleName, String columnName);

	// TODO : delete this
	public List<TrendCheckMicrosegmentReport> getTrendCheckMicrosegmentSummary(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName);

	public AVERDashboardSummary getTrendCheckSummaryForSourceTypeDateRange(String startDate, String endDate,
			String sourceType);

	public AVERDashboardSummary getABCCheckSummaryForSourceTypeDateRange(String startDate, String endDate,
			String sourceType);

	public TrendCheckDataDriftReport getTrendCheckDataDriftSummary(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName);

	public List<TrendCheckTrendGraphReport> getTrendCheckAverageTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName);

	public List<TrendCheckTrendGraphReport> getTrendCheckDistributionTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName);

	public List<TrendCheckTrendGraphReport> getTrendCheckSumTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName);
}
