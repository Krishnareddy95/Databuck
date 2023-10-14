package com.databuck.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.aver.bean.ABCCheckFailedReport;
import com.databuck.aver.bean.ABCCheckPerformanceSummary;
import com.databuck.aver.bean.ABCCheckRuleSummaryReport;
import com.databuck.aver.bean.ABCCheckSummaryReport;
import com.databuck.aver.bean.AVERDashboardSummary;
import com.databuck.aver.bean.AVERPassTrendSummary;
import com.databuck.aver.bean.AVERPerformanceSummary;
import com.databuck.aver.bean.AVERSourceTypeSummary;
import com.databuck.aver.bean.AVERTableSummary;
import com.databuck.aver.bean.FMFilePerformanceSummary;
import com.databuck.aver.bean.FileCheckReport;
import com.databuck.aver.bean.TrendCheckDataDriftReport;
import com.databuck.aver.bean.TrendCheckFailedReport;
import com.databuck.aver.bean.TrendCheckMicrosegmentReport;
import com.databuck.aver.bean.TrendCheckPerformanceSummary;
import com.databuck.aver.bean.TrendCheckSummaryReport;
import com.databuck.aver.bean.TrendCheckTrendGraphReport;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.dao.AverSummaryDao;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AVERDashboardController {

	@Autowired
	private AverSummaryDao averSummaryDao;

	/**
	 * This method is to get the count summary details of various checks
	 * 
	 * Slide -1 and -2 upper section
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/averDashboardSummary")
	public String averDashboardSummary(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate) {
		System.out.println("\n=====> averDashboardSummary - START <=====");
		String result = "";
		try {
			List<AVERDashboardSummary> summaryList = new ArrayList<AVERDashboardSummary>();

			// Get file Monitoring result
			AVERDashboardSummary fileMonitorSumry = averSummaryDao.getFileMonitoringSummaryForDateRange(startDate,
					endDate);
			summaryList.add(fileMonitorSumry);

			// Get the ABC Check result
			AVERDashboardSummary abcCheckSumry = averSummaryDao.getABCCheckSummaryForDateRange(startDate, endDate);
			summaryList.add(abcCheckSumry);

			// Get the trend check result
			AVERDashboardSummary trendSumry = averSummaryDao.getTrendCheckSummaryForDateRange(startDate, endDate);
			summaryList.add(trendSumry);

			// Get the Oracle result
			AVERDashboardSummary oracleSumry = new AVERDashboardSummary();
			oracleSumry.setSummaryName("ORACLE");
			summaryList.add(oracleSumry);

			// Get the Teradata result
			AVERDashboardSummary teradataSumry = new AVERDashboardSummary();
			teradataSumry.setSummaryName("TERADATA");
			summaryList.add(teradataSumry);

			// Get the Datalake result
			AVERDashboardSummary datalakeSumry = new AVERDashboardSummary();
			datalakeSumry.setSummaryName("DATA LAKE");
			summaryList.add(datalakeSumry);

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(summaryList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in averDashboardSummary API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> averDashboardSummary - END  <=====");
		return result;
	}

	/**
	 * This method is for Table summary for all sourceType level All source type, No
	 * of files processed, passed and failed
	 * 
	 * Slide -1 or -2 lower part without drill down
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/averDashboardTableSummary")
	public String averDashboardTableSummary(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> averDashboardTableSummary - START <=====");
		String result = "";
		try {
			List<AVERTableSummary> summaryList = new ArrayList<AVERTableSummary>();

			// Get unique sourceType List
			List<String> sourceTypeList = averSummaryDao.getUniqueTypeListFromLocMap();
			if (sourceTypeList != null) {
				for (String sourceType : sourceTypeList) {
					System.out.println("\n====>SourceType: " + sourceType);

					AVERTableSummary sourceSumry = new AVERTableSummary();
					sourceSumry.setSourceType(sourceType);

					// Get unique Locations list
					Integer locationId = averSummaryDao.getLocationIdByName("FileMonitoring");

					if (locationId != null && locationId != 0) {
						// For SourceType and 'FileMonitoring' Location combination get the validation
						// list
						List<Long> validationList = averSummaryDao.getValdiationsForSourceLocation(sourceType,
								locationId);

						if (validationList != null && validationList.size() > 0) {
							System.out.println("validationList: " + validationList);
							Map<String, Long> countSumry = averSummaryDao.getFileCountSummary(validationList, startDate,
									endDate);
							if (countSumry != null && countSumry.size() == 4) {
								long processedFilesCount = countSumry.get("processedFilesCount");
								long passedFilesCount = countSumry.get("passedFilesCount");
								long failedFilesCount = countSumry.get("failedFilesCount");
								sourceSumry.setTotalFilesCount(countSumry.get("totalFilesCount"));
								sourceSumry.setProcessedFilesCount(processedFilesCount);
								sourceSumry.setPassedFilesCount(passedFilesCount);
								sourceSumry.setFailedFilesCount(failedFilesCount);
							}
						}

						// Get the ABC Check result
						AVERDashboardSummary abcCheckSumry = averSummaryDao
								.getABCCheckSummaryForSourceTypeDateRange(startDate, endDate, sourceType);

						// Get the trend check result
						AVERDashboardSummary trendSumry = averSummaryDao
								.getTrendCheckSummaryForSourceTypeDateRange(startDate, endDate, sourceType);

						if (abcCheckSumry.getTotalCount() > 0 || trendSumry.getTotalCount() > 0
								|| sourceSumry.getProcessedFilesCount() > 0) {
							summaryList.add(sourceSumry);
						}
					}
				}
			}
			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(summaryList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in averDashboardTableSummary API !!");
			e.printStackTrace();
		}

		System.out.println("\n=====> averDashboardTableSummary - END  <=====");
		return result;
	}

	/**
	 * This method is for Table summary for one specific sourceType For one
	 * SourceType,each source FileName, (Date,FileMonitoring %),
	 * (Date,ABC&DomainCheck %), (Date,TrendCheck %), (Date,Oracle %),
	 * (Date,Teradata %), (Date,DataLake %)
	 * 
	 * Slide -1 or -2 lower part for drill down of one sourceType
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @param sourceType
	 * @return
	 */
	@RequestMapping(value = "/getSummaryForSourceType")
	public String getSummaryForSourceType(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate, @RequestParam String sourceType) {

		System.out.println("\n=====> getSummaryForSourceType - START <=====");

		String result = "";

		try {
			List<AVERSourceTypeSummary> sourceSumryList = new ArrayList<AVERSourceTypeSummary>();

			// Get unique Source list for sourceType
			List<String> sourceList = averSummaryDao.getSourceListForSourceType(sourceType);

			for (String source : sourceList) {
				System.out.println("Source: " + source);

				// Get the unique FileName for the Source
				List<String> fileNameList = averSummaryDao.getFileNameListForSource(sourceType, source);

				for (String fileName : fileNameList) {
					AVERSourceTypeSummary sourceSumry = new AVERSourceTypeSummary();
					sourceSumry.setSourceType(sourceType);
					sourceSumry.setSource(source);
					sourceSumry.setFileName(fileName);

					// Get unique Locations list
					Map<Integer, String> locationsList = averSummaryDao.getUniqueLocationsList();

					if (locationsList != null) {
						for (Integer locationId : locationsList.keySet()) {
							if (locationId != null && locationId != 0l) {
								System.out.println("LocationId: " + locationId);

								String locationName = locationsList.get(locationId);
								System.out.println("LocationName: " + locationName);

								// For each SourceType, Source and Location combination get the validation list
								List<Long> validationList = averSummaryDao.getValdiationsForSourceFileAndLocation(
										sourceType, source, fileName, locationId);

								if (validationList != null) {
									for (Long idApp : validationList) {

										// Get the summaryResult for FileMonitoring
										if (locationName.equalsIgnoreCase("FileMonitoring")) {
											// Get the latest run results
											Date f_maxDate = averSummaryDao.getMaxDateForFMValidation(idApp, startDate,
													endDate);
											List<FileTrackingHistory> historyList = averSummaryDao
													.getFMResultsForDate(idApp, f_maxDate);

											if (historyList != null) {
												int totalChecks = 0;
												int totalChecksPassed = 0;
												for (FileTrackingHistory fmHistory : historyList) {
													if (fmHistory.getFileFormat() != null
															&& fmHistory.getFileFormat().equalsIgnoreCase("FLAT")) {

														totalChecks = 4;

														if (fmHistory.getRecordLengthCheck() != null && fmHistory
																.getRecordLengthCheck().equalsIgnoreCase("passed")) {
															++totalChecksPassed;
														}
														if (fmHistory.getRecordMaxLengthCheck() != null && fmHistory
																.getRecordMaxLengthCheck().equalsIgnoreCase("passed")) {
															++totalChecksPassed;
														}
													} else {
														totalChecks = 3;
													}

													if (fmHistory.getZeroSizeFileCheck() != null && fmHistory
															.getZeroSizeFileCheck().equalsIgnoreCase("passed")) {
														++totalChecksPassed;
													}

													if (fmHistory.getColumnCountCheck() != null && fmHistory
															.getColumnCountCheck().equalsIgnoreCase("passed")) {
														++totalChecksPassed;
													}

													if (fmHistory.getColumnSequenceCheck() != null && fmHistory
															.getColumnSequenceCheck().equalsIgnoreCase("passed")) {
														++totalChecksPassed;
													}
												}

												System.out.println("totalChecks: " + totalChecks);
												System.out.println("totalChecksPassed: " + totalChecksPassed);

												double f_percentage = ((double) totalChecksPassed / totalChecks) * 100;
												String f_Date = new SimpleDateFormat("yyyy-MM-dd").format(f_maxDate);
												sourceSumry.setFileMonitoringDate(f_Date);
												sourceSumry.setFileMonitoringPerc(f_percentage);
												System.out.println(
														"FM maxDate: " + f_maxDate + " percentage: " + f_percentage);
											}
										}
										// For ABC check
										else if (locationName.equalsIgnoreCase("ABCCheck")) {
											Date abc_maxDate = averSummaryDao.getMaxDateForValidation(idApp, startDate,
													endDate);
											String abc_Date = null;
											Double totalDQI = null;

											if (abc_maxDate != null) {
												abc_Date = new SimpleDateFormat("yyyy-MM-dd").format(abc_maxDate);
												totalDQI = averSummaryDao.getTotalDQIOfIdAPPForDate(idApp, abc_Date);
												if (totalDQI != null) {
													sourceSumry.setAbcCheckDate(abc_Date);
													sourceSumry.setAbcCheckPerc(totalDQI);
												}
											}
											System.out.println(
													"ABCCheck maxDate: " + abc_Date + " percentage: " + totalDQI);
										}
										// For Trend Check
										else if (locationName.equalsIgnoreCase("TrendCheck")) {

											Date trend_maxDate = averSummaryDao.getMaxDateForValidation(idApp,
													startDate, endDate);
											String trend_Date = null;
											Double totalDQI = null;

											if (trend_maxDate != null) {
												trend_Date = new SimpleDateFormat("yyyy-MM-dd").format(trend_maxDate);
												totalDQI = averSummaryDao.getTrendCheckDQIOfIdAPPForDate(idApp,
														trend_Date);
												if (totalDQI != null) {
													sourceSumry.setTrendCheckDate(trend_Date);
													sourceSumry.setTrendCheckPerc(totalDQI);
												}
											}

											System.out.println(
													"TrendCheck maxDate: " + trend_Date + " percentage: " + totalDQI);

										}
									}
								}

							}
						}
					}
					if (sourceSumry.getFileMonitoringDate() != null || sourceSumry.getAbcCheckDate() != null
							|| sourceSumry.getTrendCheckDate() != null) {
						sourceSumryList.add(sourceSumry);
					}
				}
			}
			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(sourceSumryList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getSummaryForSourceType API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getSummaryForSourceType - END  <=====");
		return result;
	}

	/*
	 * (File Monitoring) File Monitoring: Daily Performance Trend:
	 * 
	 * For 10 Dates Total Processed, Passed, Failed, %Failed (slide -4 and -5)
	 */
	@RequestMapping(value = "/fmDailyPerformanceTrend")
	public String fmDailyPerformanceTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> fmDailyPerformanceTrend - START <=====");
		String result = "";

		try {
			List<AVERPerformanceSummary> summryList = new ArrayList<AVERPerformanceSummary>();

			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("FileMonitoring");

			if (locationId != null && locationId != 0) {
				// For 'FileMonitoring' Location get the validation list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {
					Map<String, Map<String, Long>> resultMap = averSummaryDao.getFMPerformanceSummary(validationList,
							startDate, endDate);

					if (resultMap != null) {
						System.out.println("resultMap: " + resultMap);

						for (String f_date : resultMap.keySet()) {

							AVERPerformanceSummary summry = new AVERPerformanceSummary();

							Map<String, Long> countSumry = resultMap.get(f_date);
							if (countSumry != null && countSumry.size() > 0) {
								long processedFilesCount = 0;
								if (countSumry.get("processedFilesCount") != null) {
									processedFilesCount = (Long) countSumry.get("processedFilesCount");
								}
								long failedFilesCount = 0;
								if (countSumry.get("failedFilesCount") != null) {
									failedFilesCount = (Long) countSumry.get("failedFilesCount");
								}

								long passFilesCount = 0;
								if (countSumry.get("passedFilesCount") != null) {
									passFilesCount = (Long) countSumry.get("passedFilesCount");
								}
								double failPercentage = ((double) failedFilesCount / processedFilesCount) * 100;

								summry.setDate(f_date);
								summry.setProcessedFilesCount(processedFilesCount);
								summry.setFailedFilesCount(failedFilesCount);
								summry.setPassedFilesCount(passFilesCount);
								summry.setFailPercentage(failPercentage);
								summryList.add(summry);
							}
						}
					}
				}

				if (summryList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(summryList);
				}
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in fmDailyPerformanceTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> fmDailyPerformanceTrend - END  <=====");
		return result;
	}

	/*
	 * [ABC Check] ABC Check: Daily Performance Trend:
	 * 
	 * For 10 Dates Total Processed, Passed, Failed, %Failed (slide -6 and -7)
	 */
	@RequestMapping(value = "/abcCheckDailyPerformanceTrend")
	public String abcCheckDailyPerformanceTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> abcCheckDailyPerformanceTrend - START <=====");
		String result = "";

		try {
			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {
				// For 'ABCCheck' Location get the validation list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {
					List<AVERPerformanceSummary> summryList = averSummaryDao
							.getABCCheckPerformanceSummary(validationList, startDate, endDate);

					if (summryList != null) {
						ObjectMapper objMapper = new ObjectMapper();
						result = objMapper.writeValueAsString(summryList);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in abcCheckDailyPerformanceTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> abcCheckDailyPerformanceTrend - END  <=====");
		return result;
	}

	/*
	 * [Trend Check] Trend Check: Daily Performance Trend:
	 * 
	 * For 10 Dates Total Processed, Passed, Failed, %Failed (slide -8 and -9)
	 */
	@RequestMapping(value = "/trendCheckDailyPerformanceTrend")
	public String trendCheckDailyPerformanceTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> trendCheckDailyPerformanceTrend - START <=====");
		String result = "";

		try {
			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				if (locationId != null && locationId != 0) {
					// For 'TrendCheck' Location get the validation
					// list
					List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

					if (validationList != null && validationList.size() > 0) {
						List<AVERPerformanceSummary> summryList = averSummaryDao
								.getTrendCheckPerformanceSummary(validationList, startDate, endDate);

						if (summryList != null) {
							ObjectMapper objMapper = new ObjectMapper();
							result = objMapper.writeValueAsString(summryList);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in trendCheckDailyPerformanceTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> trendCheckDailyPerformanceTrend - END  <=====");
		return result;
	}

	/*
	 * File Monitoring: Daily Pass Trend
	 * 
	 * slide -10 and -11
	 * 
	 * For each source Type pass percentage for Date Range Eg: 10 Dates
	 */
	@RequestMapping(value = "/fmDailyPassTrend")
	public String fmDailyPassTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> fmDailyPassTrend - START <=====");
		String result = "";

		try {
			List<AVERPassTrendSummary> summryList = new ArrayList<AVERPassTrendSummary>();

			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("FileMonitoring");

			if (locationId != null && locationId != 0) {
				// For SourceType and 'FileMonitoring' Location combination get the validation
				// list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {
					Map<String, Map<String, Double>> resultMap = averSummaryDao.getFMPassTrendSummary(validationList,
							startDate, endDate);

					if (resultMap != null) {
						for (String f_date : resultMap.keySet()) {
							AVERPassTrendSummary summry = new AVERPassTrendSummary();

							Map<String, Double> sourcePercMap = resultMap.get(f_date);
							summry.setDate(f_date);
							summry.setSourcePercentage(sourcePercMap);
							summryList.add(summry);
						}
					}
				}

				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(summryList);
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in fmDailyPassTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> fmDailyPassTrend - END  <=====");
		return result;
	}

	/*
	 * ABC check: Daily Pass Trend
	 * 
	 * slide -12 and -13
	 * 
	 * For each source Type pass percentage for Date Range Eg: 10 Dates
	 */
	@RequestMapping(value = "/abcCheckDailyPassTrend")
	public String abcCheckDailyPassTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> abcCheckDailyPassTrend - START <=====");
		String result = "";

		try {
			List<AVERPassTrendSummary> summryList = new ArrayList<AVERPassTrendSummary>();

			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {
				// For SourceType and 'ABCCheck' Location combination get the validation
				// list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {
					Map<String, Map<String, Double>> resultMap = averSummaryDao
							.getABCCheckPassTrendSummary(validationList, startDate, endDate);

					if (resultMap != null) {
						for (String f_date : resultMap.keySet()) {
							AVERPassTrendSummary summry = new AVERPassTrendSummary();

							Map<String, Double> sourcePercMap = resultMap.get(f_date);
							summry.setDate(f_date);
							summry.setSourcePercentage(sourcePercMap);
							summryList.add(summry);
						}
					}
				}

				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(summryList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in abcCheckDailyPassTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> abcCheckDailyPassTrend - END  <=====");
		return result;
	}

	/*
	 * Trend check: Daily Pass Trend
	 * 
	 * slide -14 and -15
	 * 
	 * For each source Type pass percentage for Date Range Eg: 10 Dates
	 */
	@RequestMapping(value = "/trendCheckDailyPassTrend")
	public String trendCheckDailyPassTrend(HttpServletRequest request, String startDate, String endDate) {

		System.out.println("\n=====> trendCheckDailyPassTrend - START <=====");
		String result = "";

		try {
			List<AVERPassTrendSummary> summryList = new ArrayList<AVERPassTrendSummary>();

			// Get unique Locations list
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				// For SourceType and 'TrendCheck' Location combination get the validation
				// list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {
					Map<String, Map<String, Double>> resultMap = averSummaryDao
							.getTrendCheckPassTrendSummary(validationList, startDate, endDate);

					if (resultMap != null) {
						for (String f_date : resultMap.keySet()) {
							AVERPassTrendSummary summry = new AVERPassTrendSummary();

							Map<String, Double> sourcePercMap = resultMap.get(f_date);
							summry.setDate(f_date);
							summry.setSourcePercentage(sourcePercMap);
							summryList.add(summry);
						}
					}
				}

				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(summryList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in trendCheckDailyPassTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> trendCheckDailyPassTrend - END  <=====");
		return result;
	}

	/*
	 * FileMonitoring : Daily Failed files
	 * 
	 * Slide -16 and -17
	 * 
	 * Get details of File checks status Zerosize, Record length, column count,
	 * column sequence, calculate pass percentage for the checks Rule columnLevel -
	 * File Level Record count Threshold - 0 status - passed/failed processedDate
	 */
	@RequestMapping(value = "/fmFailedFilesDetails")
	public String fmFailedFilesDetails(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate) {
		System.out.println("\n=====> fmFailedFilesDetails - START <=====");
		String result = "";
		try {
			List<FileCheckReport> fileCheckReportList = averSummaryDao.getfmFailedFilesReportForDateRange(startDate,
					endDate);

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(fileCheckReportList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in fmFailedFilesDetails API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> fmFailedFilesDetails - END  <=====");
		return result;
	}

	/*
	 * ABCCheck : Daily Failed Files
	 * 
	 * Slide -18 and -19
	 * 
	 * FileName, OverallSTatus,
	 * NullCheck,LengthCheck,DomainCheck,RecordReasonablility status
	 */
	@RequestMapping(value = "/abcCheckDailyFailedFiles")
	public String abcCheckDailyFailedFiles(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> abcCheckDailyFailedFiles - START <=====");
		String result = "";
		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {

				// For 'ABCCheck' Location get the validation list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {

					List<ABCCheckFailedReport> abcCheckFailedReportList = averSummaryDao
							.getABCCheckFailedReportForDateRange(startDate, endDate, validationList);

					if (abcCheckFailedReportList != null) {
						ObjectMapper objMapper = new ObjectMapper();
						result = objMapper.writeValueAsString(abcCheckFailedReportList);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in abcCheckDailyFailedFiles API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> abcCheckDailyFailedFiles - END  <=====");
		return result;
	}

	/*
	 * TrendCheck : Daily Failed Files
	 * 
	 * Slide -20 and -21
	 * 
	 * FileName, OverallSTatus, NoOfTrends, #NoOfChecksFailed, Columns Involved
	 */
	@RequestMapping(value = "/trendCheckDailyFailedFiles")
	public String trendCheckDailyFailedFiles(HttpServletRequest request, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> trendCheckDailyFailedFiles - START <=====");
		String result = "";
		try {

			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {

				// For 'TrendCheck' Location get the validation list
				List<Long> validationList = averSummaryDao.getValdiationsForLocation(locationId);

				if (validationList != null && validationList.size() > 0) {

					List<TrendCheckFailedReport> trendCheckFailedReportList = averSummaryDao
							.getTrendCheckFailedReportForDateRange(startDate, endDate, validationList);

					if (trendCheckFailedReportList != null) {
						ObjectMapper objMapper = new ObjectMapper();
						result = objMapper.writeValueAsString(trendCheckFailedReportList);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in trendCheckDailyFailedFiles API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> trendCheckDailyFailedFiles - END  <=====");
		return result;
	}

	/**
	 * File Monitoring: ACARIA HUMIRA
	 * 
	 * This Method is API for Summary Details for a SourceType, Source , FileName
	 * for a Specific Date
	 * 
	 * Slide -22 or -23
	 * 
	 * @param request
	 * @param processedDate
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/getFMSummaryDetailsOfFile")
	public String getFMSummaryDetailsOfFile(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName) {

		System.out.println("\n=====> getFMSummaryDetailsOfFile - START <=====");
		String result = "";
		try {
			FileCheckReport fileCheckReport = averSummaryDao.getFMSummaryDetailsOfFile(processedDate, sourceType,
					source, fileName);

			if (fileCheckReport != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(fileCheckReport);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getFMSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getFMSummaryDetailsOfFile - END  <=====");
		return result;
	}

	/*
	 * Slide -24 and -25
	 * 
	 * For one sourceType, source and filename , for 10 dates OverallScore, Record
	 * count, Zero size check, record length check, column length check, column
	 * sequence check
	 */
	@RequestMapping(value = "/getFMDailyPerformanceForFile")
	public String getFMDailyPerformanceForFile(HttpServletRequest request, @RequestParam String sourceType,
			@RequestParam String source, @RequestParam String fileName, String startDate, String endDate) {

		System.out.println("\n=====> getFMDailyPerformanceForFile - START <=====");
		String result = "";

		try {
			List<FMFilePerformanceSummary> fmFilePerformanceSmryList = averSummaryDao
					.getFMDailyPerformanceForFile(sourceType, source, fileName, startDate, endDate);

			if (fmFilePerformanceSmryList != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(fmFilePerformanceSmryList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getFMDailyPerformanceForFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getFMDailyPerformanceForFile - END  <=====");
		return result;
	}

	/**
	 * ABC Check: ACARIA HUMIRA
	 * 
	 * This Method is API for Summary Details for a SourceType, Source , FileName
	 * for a Specific Date
	 * 
	 * Slide -26 or -27 (table -1)
	 * 
	 * @param request
	 * @param processedDate
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/getABCCheckSummaryDetailsOfFile")
	public String getABCCheckSummaryDetailsOfFile(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName) {

		System.out.println("\n=====> getABCCheckSummaryDetailsOfFile - START <=====");
		String result = "";
		try {
			// Get LocationId for ABCCheck
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {
				List<ABCCheckSummaryReport> abcCheckSummaryReportList = averSummaryDao
						.getABCCheckSummaryDetailsOfFile(processedDate, sourceType, source, fileName, locationId);

				if (abcCheckSummaryReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(abcCheckSummaryReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getABCCheckSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getABCCheckSummaryDetailsOfFile - END  <=====");
		return result;
	}

	/**
	 * ABC Check: ACARIA HUMIRA
	 * 
	 * This Method is API for Summary Details of a Rule on click on Rule link
	 * 
	 * Slide -26 or -27 (table -2)
	 * 
	 * @param request
	 * @param processedDate
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/getABCCheckRuleSummaryDetailsOfFile")
	public String getABCCheckSummaryDetailsOfFileRule(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String ruleName, @RequestParam String columnName) {

		System.out.println("\n=====> getABCCheckRuleSummaryDetailsOfFile - START <=====");
		String result = "";
		try {
			// Get LocationId for ABCCheck
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {
				List<ABCCheckRuleSummaryReport> abcCheckSummaryReportList = averSummaryDao
						.getABCCheckRuleSummaryDetailsOfFile(processedDate, sourceType, source, fileName, locationId,
								ruleName, columnName);

				if (abcCheckSummaryReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(abcCheckSummaryReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getABCCheckRuleSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getABCCheckRuleSummaryDetailsOfFile - END  <=====");
		return result;
	}

	/*
	 * Slide -28 and -29
	 * 
	 * For one sourceType, source and filename , for 10 dates OverallScore, Record
	 * count, Zero size check, record length check, column length check, column
	 * sequence check
	 */
	@RequestMapping(value = "/getABCCheckDailyPerformanceForFile")
	public String getABCCheckDailyPerformanceForFile(HttpServletRequest request, @RequestParam String sourceType,
			@RequestParam String source, @RequestParam String fileName, String startDate, String endDate) {

		System.out.println("\n=====> getABCCheckDailyPerformanceForFile - START <=====");
		String result = "";

		try {
			// Get LocationId for ABCCheck
			Integer locationId = averSummaryDao.getLocationIdByName("ABCCheck");

			if (locationId != null && locationId != 0) {
				List<ABCCheckPerformanceSummary> abcCheckPerfSumryList = averSummaryDao
						.getABCCheckDailyPerformanceForFile(sourceType, source, fileName, startDate, endDate,
								locationId);

				if (abcCheckPerfSumryList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(abcCheckPerfSumryList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getABCCheckDailyPerformanceForFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getABCCheckDailyPerformanceForFile - END  <=====");
		return result;
	}

	/*
	 * Slide -30 and -31
	 * 
	 * 
	 * Get details of Trendcheck of one sourceType, source and filename, for one
	 * date
	 * 
	 * Microsegment, Microsegment value, Column name, Count, Min, Max, Std Dev,
	 * Mean, Status
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckSummaryDetailsOfFile")
	public String getTrendCheckSummaryDetailsOfFile(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName) {

		System.out.println("\n=====> getTrendCheckSummaryDetailsOfFile - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckSummaryReport> trendCheckSummaryReportList = averSummaryDao
						.getTrendCheckSummaryDetailsOfFile(processedDate, sourceType, source, fileName, locationId);

				if (trendCheckSummaryReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckSummaryReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckSummaryDetailsOfFile - END  <=====");
		return result;
	}

	// TODO : delete this
	/*
	 * Slide -30 and -31(graph)
	 * 
	 * 
	 * Get details of Trendcheck of one sourceType, source and filename, for one one
	 * microsegment, microsegment value and column name for latest 10 dates
	 * 
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckMicrosegmentSummary")
	public String getTrendCheckMicrosegmentSummary(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String microsegment, @RequestParam String microsegmentValue,
			@RequestParam String columnName) {

		System.out.println("\n=====> getTrendCheckMicrosegmentSummary - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckMicrosegmentReport> trendCheckMicrosegSmryList = averSummaryDao
						.getTrendCheckMicrosegmentSummary(processedDate, sourceType, source, fileName, locationId,
								microsegment, microsegmentValue, columnName);

				if (trendCheckMicrosegSmryList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckMicrosegSmryList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckMicrosegmentSummary API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckMicrosegmentSummary - END  <=====");
		return result;
	}

	/*
	 * Slide -30 and -31(Avg Trend graph)
	 * 
	 * 
	 * Get Average Trend details of Trendcheck of one sourceType, source and
	 * filename, for one one microsegment, microsegment value and column name for
	 * latest 10 dates
	 * 
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckAverageTrend")
	public String getTrendCheckAverageTrend(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String microsegment, @RequestParam String microsegmentValue,
			@RequestParam String columnName) {

		System.out.println("\n=====> getTrendCheckAverageTrend - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckTrendGraphReport> trendCheckTrendGraphReportList = averSummaryDao
						.getTrendCheckAverageTrend(processedDate, sourceType, source, fileName, locationId,
								microsegment, microsegmentValue, columnName);

				if (trendCheckTrendGraphReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckTrendGraphReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckAverageTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckAverageTrend - END  <=====");
		return result;
	}

	/*
	 * Slide -30 and -31(Distribution Trend)
	 * 
	 * 
	 * Get Distribution Trend details of Trendcheck of one sourceType, source and
	 * filename, for one one microsegment, microsegment value and column name for
	 * latest 10 dates
	 * 
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckDistributionTrend")
	public String getTrendCheckDistributionTrend(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String microsegment, @RequestParam String microsegmentValue,
			@RequestParam String columnName) {

		System.out.println("\n=====> getTrendCheckDistributionTrend - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckTrendGraphReport> trendCheckTrendGraphReportList = averSummaryDao
						.getTrendCheckDistributionTrend(processedDate, sourceType, source, fileName, locationId,
								microsegment, microsegmentValue, columnName);

				if (trendCheckTrendGraphReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckTrendGraphReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckDistributionTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckDistributionTrend - END  <=====");
		return result;
	}

	/*
	 * Slide -30 and -31(Sum Trend graph)
	 * 
	 * 
	 * Get Sum Trend details of Trendcheck of one sourceType, source and filename,
	 * for one one microsegment, microsegment value and column name for latest 10
	 * dates
	 * 
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckSumTrend")
	public String getTrendCheckSumTrend(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String microsegment, @RequestParam String microsegmentValue,
			@RequestParam String columnName) {

		System.out.println("\n=====> getTrendCheckSumTrend - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckTrendGraphReport> trendCheckTrendGraphReportList = averSummaryDao.getTrendCheckSumTrend(
						processedDate, sourceType, source, fileName, locationId, microsegment, microsegmentValue,
						columnName);

				if (trendCheckTrendGraphReportList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckTrendGraphReportList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckSumTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckSumTrend - END  <=====");
		return result;
	}

	/*
	 * Slide -30 and -31(Data Drift table)
	 * 
	 * 
	 * Get DataDrift details of Trendcheck of one sourceType, source and filename,
	 * for one one microsegment, microsegment value and column name latest Date info
	 * 
	 * 
	 */
	@RequestMapping(value = "/getTrendCheckDataDriftSummary")
	public String getTrendCheckDataDriftSummary(HttpServletRequest request, @RequestParam String processedDate,
			@RequestParam String sourceType, @RequestParam String source, @RequestParam String fileName,
			@RequestParam String microsegment, @RequestParam String microsegmentValue,
			@RequestParam String columnName) {

		System.out.println("\n=====> getTrendCheckDataDriftSummary - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				TrendCheckDataDriftReport trendCheckDataDriftReport = averSummaryDao.getTrendCheckDataDriftSummary(
						processedDate, sourceType, source, fileName, locationId, microsegment, microsegmentValue,
						columnName);

				if (trendCheckDataDriftReport != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckDataDriftReport);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckDataDriftSummary API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckDataDriftSummary - END  <=====");
		return result;
	}

	/*
	 * Slide -32 and -33
	 * 
	 * For one sourceType, source and filename for 10 dates
	 * 
	 * OverallScore, Record count, No of Trends, No of Checks failed
	 */
	@RequestMapping(value = "/getTrendCheckDailyPerformanceForFile")
	public String getTrendCheckDailyPerformanceForFile(HttpServletRequest request, @RequestParam String sourceType,
			@RequestParam String source, @RequestParam String fileName, String startDate, String endDate) {

		System.out.println("\n=====> getTrendCheckDailyPerformanceForFile - START <=====");

		String result = "";

		try {
			// Get LocationId for TrendCheck
			Integer locationId = averSummaryDao.getLocationIdByName("TrendCheck");

			if (locationId != null && locationId != 0) {
				List<TrendCheckPerformanceSummary> trendCheckPerfSumryList = averSummaryDao
						.getTrendCheckDailyPerformanceForFile(sourceType, source, fileName, startDate, endDate,
								locationId);

				if (trendCheckPerfSumryList != null) {
					ObjectMapper objMapper = new ObjectMapper();
					result = objMapper.writeValueAsString(trendCheckPerfSumryList);
				}
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTrendCheckDailyPerformanceForFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTrendCheckDailyPerformanceForFile - END  <=====");
		return result;
	}

}
