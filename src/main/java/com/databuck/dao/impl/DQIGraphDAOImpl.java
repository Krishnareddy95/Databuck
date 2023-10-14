package com.databuck.dao.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.Idapp;
import com.databuck.bean.ListApplications;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DQIGraphDAOI;
import com.databuck.dao.IResultsDAO;
import org.apache.log4j.Logger;

@Repository
public class DQIGraphDAOImpl implements DQIGraphDAOI {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	public IResultsDAO iResultsDAO;
	
	private static final Logger LOG = Logger.getLogger(DQIGraphDAOImpl.class);

	@Override
	public MultiValueMap getRecordCountGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
			String sql = "";
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				sql = "select sum(DQICountMlt)/sum(RecordCount) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun,"
						+ "Date from ((SELECT (RecordCount * dgDqi) AS DQICountMlt, RecordCount, DATE, Run"
						+ " FROM (SELECT DATE, Run, RecordCount, (CASE WHEN (RC_Deviation <=" + threshold + ") "
						+ "THEN 100 ELSE (CASE WHEN (RC_Deviation >=6) THEN 0 ELSE ( 100 - ( (" + "ABS( RC_Deviation -"
						+ threshold + " ) *100 ) / ( 6 -" + threshold + " ) )) END) END) AS dgDqi "
						+ "FROM DATA_QUALITY_Transactionset_sum_A1 where idApp="+idApp+") AS alias) AS alias1 )"
						+ "group by Date,Run order by Date desc,Run desc limit 50 ";

			} else {
				sql = "select sum(DQICountMlt)/sum(RecordCount) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as "
						+ "DateRun,Date from ((SELECT (RecordCount * dgDqi) AS DQICountMlt, RecordCount, DATE, Run"
						+ " FROM (SELECT DATE, Run, RecordCount, (CASE WHEN (dGroupDeviation <=" + threshold + ") "
						+ "THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6) THEN 0 ELSE ( 100 - ( ("
						+ "ABS( dGroupDeviation -" + threshold + " ) *100 ) / ( 6 -" + threshold
						+ " ) )) END) END) AS dgDqi " + "FROM DATA_QUALITY_Transactionset_sum_dgroup where idApp="+idApp+") AS alias) AS alias1 )"
						+ "group by Date,Run order by Date desc,Run desc limit 50 ";
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("finalDQI") != null)
					map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	public MultiValueMap getNullCountGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "SELECT (100 - (sum(Null_Value)*100/sum(Record_Count))) AS finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun FROM  "
					+ "DATA_QUALITY_Column_Summary "
					+ "WHERE idApp="+idApp+" and STATUS IS NOT NULL GROUP BY DATE, Run ORDER BY DATE DESC , Run DESC LIMIT 50 ";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	public MultiValueMap GetGraphForDashboard(Long idApp, ListApplications listApplicationsData, String tabName) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "SELECT DQI AS finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun,Status FROM  "
					+ "DashBoard_Summary "
					+ "WHERE Test = '" + tabName + "' and AppId =" + idApp;

			if(tabName.equalsIgnoreCase("DQ_LengthCheck")){
				String testNames="('DQ_LengthCheck','DQ_MaxLengthCheck')";

				sql = "SELECT AVG(DQI) AS finalDQI,CONCAT( max(DATE),  ' (', max(Run),  ')' )  as DateRun,Status FROM  "
						+ "DashBoard_Summary "
						+ "WHERE Test in " + testNames + " and AppId =" + idApp+" group by DATE,Run";
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
				
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	
	public MultiValueMap GetGraphForDashboardWithStatus(Long idApp, ListApplications listApplicationsData, String tabName) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "SELECT DQI AS finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun,Status FROM  "
					+ "DashBoard_Summary "
					+ "WHERE Test = '" + tabName + "' and AppId =" + idApp;

			if(tabName.equalsIgnoreCase("DQ_LengthCheck")){
				String testNames="('DQ_LengthCheck','DQ_MaxLengthCheck')";

				sql = "SELECT AVG(DQI) AS finalDQI,CONCAT( max(DATE),  ' (', max(Run),  ')' )  as DateRun,Status FROM  "
						+ "DashBoard_Summary "
						+ "WHERE Test in " + testNames + " and AppId =" + idApp+" group by DATE,Run";
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {	
				if(tabName.equals("DQ_Data Drift")) {
					map.put(queryForRowSet.getString("DateRun"),queryForRowSet.getString("Status")!=null?queryForRowSet.getDouble("finalDQI"):0);
				}else {
					map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
				}
				
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getString("Status")!=null?queryForRowSet.getString("Status"):"");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	
	@Override
	public MultiValueMap getPrimaryKeyGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "SELECT  (100 - (sum(Duplicate)*100/sum(TotalCount))) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun FROM  "
					+ " DATA_QUALITY_Transaction_Summary WHERE idApp="+idApp+" and TYPE =  'identity'"
					+ " GROUP BY DATE, Run ORDER BY DATE DESC , Run DESC LIMIT 50";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	public MultiValueMap getUserSelectedKeyGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "SELECT  (100 - (sum(Duplicate)*100/sum(TotalCount))) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun FROM  "
					+ " DATA_QUALITY_Transaction_Summary WHERE idApp="+idApp+" and TYPE =  'all'"
					+ " GROUP BY DATE, Run ORDER BY DATE DESC , Run DESC LIMIT 50";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public MultiValueMap getNumericalFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "";
			sql = "select sum(DQICountMlt)/sum(Count) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun, "
					+ "Date from ((SELECT (Count * dgDqi) AS DQICountMlt, Count, DATE, Run"
					+ " FROM (SELECT DATE, Run, Count, (CASE WHEN (NumMeanDeviation <= NumMeanThreshold) THEN 100 ELSE "
					+ "(CASE WHEN (NumMeanDeviation >=6) "
					+ "THEN 0 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS dgDqi "
					+ "FROM DATA_QUALITY_Column_Summary where idApp="+idApp+" and NumMeanThreshold IS NOT NULL) AS alias) AS alias1 )"
					+ "group by Date,Run order by Date desc,Run desc limit 50 ";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	public MultiValueMap getStringFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String sql = "select sum(DQICountMlt)/sum(Count) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun, "
					+ "Date from ((SELECT (Count * dgDqi) AS DQICountMlt, Count, DATE, Run"
					+ " FROM (SELECT DATE, Run, Count, (CASE WHEN (StrCardinalityDeviation <= String_Threshold) THEN 100 ELSE "
					+ "(CASE WHEN (StrCardinalityDeviation >=6) THEN 0 ELSE (100 - ( (ABS(StrCardinalityDeviation "
					+ "- String_Threshold) *100) / ( 6 - String_Threshold ) )) END) END)  AS dgDqi "
					+ "FROM DATA_QUALITY_Column_Summary where idApp="+idApp+" and String_Threshold IS NOT NULL) AS alias) AS alias1 )"
					+ "group by Date,Run order by Date desc,Run desc limit 50 ";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	public MultiValueMap getRAFieldFingerprintGraph(Long idApp, ListApplications listApplicationsData) {
		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		try {
			String dateForDashboard = iResultsDAO.getDateForSummaryOfLastRun(idApp);
			String recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(0l, idApp, "",
					listApplicationsData);
			if (recordFieldScore.contains("∞")) {
				recordFieldScore = "0";
			}
			map.put(dateForDashboard + "(1)", Double.valueOf(recordFieldScore));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		try {
			String sql = "	SELECT (100-RA_Dqi) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun from "
					+ "DATA_QUALITY_Record_Anomaly where idApp="+idApp
					+ " group by Date,Run order by Date desc,Run desc limit 50 ";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString("DateRun"), queryForRowSet.getDouble("finalDQI"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public MultiValueMap getAggregateDQISummaryGraph(Long idApp, ListApplications listApplicationsData,
			Map<String, String> tranRuleMap) {
		MultiValueMap finalMap = new MultiValueMap();
		SqlRowSet RCAqueryForRowSet = null;
		int totalCount = 0;
		Double totalDQI = 0.0;
		/*try {
			try {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String sql = "";
				if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
					sql = "select sum(DQICountMlt)/sum(RecordCount) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as DateRun,"
							+ "Date from ((SELECT (RecordCount * dgDqi) AS DQICountMlt, RecordCount, DATE, Run"
							+ " FROM (SELECT DATE, Run, RecordCount, (CASE WHEN (RC_Deviation <=" + threshold + ") "
							+ "THEN 100 ELSE (CASE WHEN (RC_Deviation >=6) THEN 0 ELSE ( 100 - ( ("
							+ "ABS( RC_Deviation -" + threshold + " ) *100 ) / ( 6 -" + threshold
							+ " ) )) END) END) AS dgDqi " + "FROM DATA_QUALITY_" + idApp
							+ "_Transactionset_sum_A1) AS alias) AS alias1 )"
							+ "group by Date,Run order by Date desc,Run desc limit 50 ";

				} else {
					sql = "select sum(DQICountMlt)/sum(RecordCount) as finalDQI,CONCAT( DATE,  ' (', Run,  ')' )  as "
							+ "DateRun,Date from ((SELECT (RecordCount * dgDqi) AS DQICountMlt, RecordCount, DATE, Run"
							+ " FROM (SELECT DATE, Run, RecordCount, (CASE WHEN (dGroupDeviation <=" + threshold + ") "
							+ "THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6) THEN 0 ELSE ( 100 - ( ("
							+ "ABS( dGroupDeviation -" + threshold + " ) *100 ) / ( 6 -" + threshold
							+ " ) )) END) END) AS dgDqi " + "FROM DATA_QUALITY_" + idApp
							+ "_Transactionset_sum_dgroup) AS alias) AS alias1 )"
							+ "group by Date,Run order by Date desc,Run desc limit 50 ";
				}
				LOG.debug(sql);
				RCAqueryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			while (RCAqueryForRowSet.next()) {
				totalDQI = 0.0;
				totalCount = 0;
				String Date = RCAqueryForRowSet.getString("DateRun");
				totalDQI = RCAqueryForRowSet.getDouble("finalDQI");
				totalCount++;
				if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
					try {
						MultiValueMap numericalFieldFingerprintGraph = getNumericalFieldFingerprintGraph(idApp,
								listApplicationsData);
						ArrayList<Double> list = (ArrayList<Double>) numericalFieldFingerprintGraph.get(Date);
						LOG.debug("list=" + list + "Date=" + Date);
						totalDQI = totalDQI + list.get(0);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
					try {
						MultiValueMap stringGraph = getStringFieldFingerprintGraph(idApp, listApplicationsData);
						ArrayList<Double> list = (ArrayList<Double>) stringGraph.get(Date);
						totalDQI = totalDQI + list.get(0);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
					try {
						MultiValueMap nullGraph = getNullCountGraph(idApp, listApplicationsData);
						ArrayList<Double> list = (ArrayList<Double>) nullGraph.get(Date);
						totalDQI = totalDQI + list.get(0);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (tranRuleMap.get("all").equalsIgnoreCase("Y")) {
					try {
						MultiValueMap userSelectedKeyGraph = getUserSelectedKeyGraph(idApp, listApplicationsData);
						ArrayList<Double> list = (ArrayList<Double>) userSelectedKeyGraph.get(Date);
						totalDQI = totalDQI + list.get(0);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) {
					try {
						MultiValueMap primaryKeyGraph = getPrimaryKeyGraph(idApp, listApplicationsData);
						ArrayList<Double> list = (ArrayList<Double>) primaryKeyGraph.get(Date);
						totalDQI = totalDQI + list.get(0);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
					try {
						String recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(0l, idApp, "",
								listApplicationsData);
						totalDQI = totalDQI + Double.valueOf(recordFieldScore);
						totalCount++;
					} catch (Exception e) {
					}
				}
				if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y")) {
					try {
						Map<String, String> rulesMap = iResultsDAO.checkRulesTable(listApplicationsData);
						if (rulesMap.size() >= 2) {
							totalDQI = totalDQI + Double.valueOf(rulesMap.get("ruleScoreDF"));
							totalCount++;
						}
					} catch (Exception e) {
					}
				}
				if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) {
					try {
						String dataDriftScore = iResultsDAO.getScoreForDataDrift(listApplicationsData);
						totalDQI = totalDQI + Double.valueOf(dataDriftScore);
						totalCount++;
					} catch (Exception e) {
					}
				}*/

				//-------****


				DecimalFormat df = new DecimalFormat("#.00");
				// RecordAnomaly

				/*
				 * String recordAnomalyScore =
				 * iResultsDAO.CalculateScoreForRecordAnomaly(idData, idApp,
				 * recordCountAnomaly, rcaStatus, listApplicationsData); if
				 * (recordAnomalyScore.contains("∞")) { recordAnomalyScore =
				 * "0.0"; } modelAndView.addObject("recordAnomalyScore",
				 * recordAnomalyScore); totalDQI = totalDQI +
				 * Double.valueOf(recordAnomalyScore); totalCount++;
				 * LOG.debug("recordAnomalyScore=" +
				 * recordAnomalyScore);
				 */
				/*
				 * modelAndView.addObject("RCAStatus", "passed");
				 * modelAndView.addObject("recordAnomalyScore", "30.0");
				 * modelAndView.addObject("RCAKey_Matric_1", "10");
				 * modelAndView.addObject("RCAKey_Matric_2", "20");
				 */
				try {

					if (true) {

						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Record Count Fingerprint", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty",0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}



						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}


					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
					E.getMessage();
				}

				// BadData
				try {

					if (listApplicationsData.getBadData().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Bad_Data",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}
					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
					E.getMessage();
				}


				// Date Rule changes 8jan2019 priyanka
				try {

					if (listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_DateRuleCheck", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}



						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}


				// Numerical Field
				try {

					if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Numerical Field Fingerprint", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}



						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// string Field

				if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {

					Long idData = 0l;
					SqlRowSet idDataAndAppName = iResultsDAO.getIdDataAndAppNameFromListApplications(idApp);
					while (idDataAndAppName.next()) {
						idData = idDataAndAppName.getLong("idData");
					String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData, idApp, "",
							listApplicationsData);
					if (stringFieldScore.contains("∞")) {
						stringFieldScore = "0";
					}


					String stringFieldStatus = iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);

					if(stringFieldStatus != null){
						totalDQI = totalDQI + Double.valueOf(stringFieldScore);
						totalCount++;
					}


					}
				}
				// NullCountScore
				try {

					if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Completeness",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// All FieldsScore
				try {

					if (tranRuleMap.get("all").equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Uniqueness -Seleted Fields", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// identityFieldsScore
				try {

					if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Uniqueness -Primary Keys", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// Record Fingerprint
				try {

					if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Record Anomaly", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}


				// Length Check
				try {

						if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {
							SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
									"DQ_LengthCheck", listApplicationsData);

							String DQI = "";
							String status = "";
							String Key_Matric_1 = "";
							String Key_Matric_2 = "";
							while (dashboardDetails.next()) {
								DQI = dashboardDetails.getString(4);
								status = dashboardDetails.getString(5);
								Key_Matric_1 = dashboardDetails.getString(6);
								Key_Matric_2 = dashboardDetails.getString(7);

							}
							String PercentageDF = "0";
							if (DQI == null) {
								// modelAndView.addObject("showDQIEmpty", 0);

							} else {
								// modelAndView.addObject("showDQIEmpty",null);
								double Percentage = Double.parseDouble(DQI);
								DecimalFormat df1 = new DecimalFormat("#0.0");
								PercentageDF = df1.format(Percentage);

							}



							if(status != null){
								totalDQI = totalDQI + Double.valueOf(PercentageDF);
								totalCount++;
							}

						}
					} catch (Exception E) {
						LOG.error(E.getMessage());
					}
				
				//Max Length Check
				try {

						if (listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
							SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
									"DQ_maxLengthCheck", listApplicationsData);

							String DQI = "";
							String status = "";
							String Key_Matric_1 = "";
							String Key_Matric_2 = "";
							while (dashboardDetails.next()) {
								DQI = dashboardDetails.getString(4);
								status = dashboardDetails.getString(5);
								Key_Matric_1 = dashboardDetails.getString(6);
								Key_Matric_2 = dashboardDetails.getString(7);

							}
							String PercentageDF = "0";
							if (DQI == null) {
								// modelAndView.addObject("showDQIEmpty", 0);

							} else {
								// modelAndView.addObject("showDQIEmpty",null);
								double Percentage = Double.parseDouble(DQI);
								DecimalFormat df1 = new DecimalFormat("#0.0");
								PercentageDF = df1.format(Percentage);

							}



							if(status != null){
								totalDQI = totalDQI + Double.valueOf(PercentageDF);
								totalCount++;
							}

						}
					} catch (Exception E) {
						LOG.error(E.getMessage());
					}

				// RegexCheck
			try {

					if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Rules", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// datadrift
				try {

					if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Data Drift",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}
				} catch (Exception E) {
					LOG.error(E.getMessage());
				}
				// Timeliness
				try {

					if (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Timeliness",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);

						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);


						}


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}

					}

				} catch (Exception E) {
					LOG.error(E.getMessage());
				}

				// Default Check
				try {
					
					if (listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "'DQ_Completeness'",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
						
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							

						}
						
						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}
					}

				} catch (Exception E) {
					LOG.error(E.getMessage());
				}

				//--------****

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				LOG.debug(dateFormat.format(date)); //2016/11/16 12:08:43


				double avgDQI = totalDQI / totalCount;

				LOG.debug("==============avgDQI=="+avgDQI);

				finalMap.put(dateFormat.format(date), avgDQI);
				LOG.debug("Score:-->"+avgDQI);

		return finalMap;
	}

	/* Pradeep 04-Feb-2020:  Replaced all function body with corrections */
	@Override
	public MultiValueMap getGraphForDQI_ExecutiveSumm(String project_id) {
		MultiValueMap oMap = new MultiValueMap();
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;
		
		List<Idapp> IdApp =  new ArrayList<>();
		try
		{
			
			String sql = "select idApp from listApplications where project_id in ("+ project_id+") and active = 'Yes' order by idApp";
		
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}
		}catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		//LOG.debug("text-->"+text);
		
		

		try {
			sSqlQry = "select sum(DQI)/count(date) as aggDQI, Date from  DashBoard_Summary\n";
			sSqlQry = sSqlQry + "where Date >= date_sub(curdate(), interval 24 month) and AppId in ("+text+")\n";
			sSqlQry = sSqlQry + "group by Date order by date desc";

			LOG.debug("getGraphForDQI_ExecutiveSumm()" + sSqlQry);
			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

			while (oSqlRowSet.next()) {
				oMap.put(oSqlRowSet.getString("Date"), oSqlRowSet.getDouble("aggDQI"));
			}
			LOG.debug("getGraphForDQI_ExecutiveSumm()" + oMap);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return oMap;
	}

	//for historical avg for all runs ...by priyanka [25 jan 2020]
	@Override
	public MultiValueMap getHistoricAvgForAllRunsAggDQI_graph(Long idApp) {

		
		MultiValueMap map = new MultiValueMap();
		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				//sql = "select avg(DQI) as aggDQI, CONCAT( Date,'(',Run, ')' ) as DateRun from DashBoard_Summary where appId = " + idApp + " and length(trim(COALESCE(Status,''))) > 0 group by Date, Run order by Date,Run";
				 sql = "SELECT aggregateDQI as aggDQI, CONCAT( Date,'(',Run, ')' ) as DateRun FROM data_quality_dashboard  WHERE idApp  in ("+ idApp + ")";
				
			else 
				sql = "SELECT aggregateDQI as aggDQI, CONCAT( Date,'(',Run, ')' ) as DateRun FROM data_quality_dashboard  WHERE idApp  in ("+ idApp + ")";
				//sql = "select avg(DQI) as aggDQI, CONCAT( Date,'(',Run, ')' ) as DateRun from DashBoard_Summary where appId = " + idApp + " and length(trim(ifnull(Status,''))) > 0 group by Date, Run order by Date,Run";

			LOG.debug("====getHistoricAvgForAllRunsAggDQI_graph====sql=="+sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				//map.put(queryForRowSet.getString("aggDQI"), queryForRowSet.getString("DateRun"));
				//	finalMap.put(dateFormat.format(date), avgDQI);
				map.put(queryForRowSet.getString("DateRun"),queryForRowSet.getDouble("aggDQI"));
			}
			LOG.debug("====getHistoricAvgForAllRunsAggDQI_graph===map==="+map);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;


	}



	}


