package com.databuck.service;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;

@Service
public class DistributionCheckResultsService {
	
	private static final Logger LOG = Logger.getLogger(DistributionCheckResultsService.class);

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	public List<List<Object>> getAvgTrendChartData(String tableName, String DGroupVal, String colName, String idApp) {
		try {
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData tableName :: " + tableName
					+ " | DGroupVal :: " + DGroupVal + " | colName :: " + colName);
			List<List<Object>> listOfListAvgTrend = new ArrayList<List<Object>>();
			List<Integer> listRun = new ArrayList<Integer>();
			List<Object> listHeader = new ArrayList<Object>();
			listHeader.add("Date");
			listHeader.add("UpperLimit");
			listHeader.add("Mean");
			listHeader.add("LowerLimit");
			listOfListAvgTrend.add(listHeader);

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData listDate :: " + listDate);
			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData listRun :: " + listRun);

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {

						LOG.debug(
								"DistributionCheckResultsService : getAvgTrendChartData checkColumnSummaryRecordsExistsOrNotWithRunAndDate : tableName :: "
										+ tableName + " | date :: " + date + " | integer :: " + integer);
						StringBuffer strBufQuery = new StringBuffer();

						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumMeanAvg, 0) AS NumMeanAvg , "
								+ ifnull_function + "(dqcs.NumMeanThreshold , 0) AS NumMeanThreshold   , "
								+ ifnull_function + " (dqcs.NumMeanStdDev, 0) AS NumMeanStdDev , " + ifnull_function
								+ "(dqcs.Mean, 0) AS Mean ,  dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
						strBufQuery.append(" dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);
						LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : strBufQuery :: "
								+ strBufQuery);
						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");

							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								List<Object> listAvgColumn = new ArrayList<Object>();
								double doubleNumMeanavg = sqlRowSet_strDateSqlQuery.getDouble("NumMeanAvg");
								double doubleNumMeanThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumMeanThreshold");
								double doubleNumMeanStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumMeanStdDev");
								double doubleMean = sqlRowSet_strDateSqlQuery.getDouble("Mean");
								java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());

								double doubleUpperLimit = doubleNumMeanavg
										+ (doubleNumMeanThreshold * doubleNumMeanStdDev);
								double doubleLowerLimit = doubleNumMeanavg
										- (doubleNumMeanThreshold * doubleNumMeanStdDev);

								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);

								if (integer < 10) {
									listAvgColumn.add(strDate + "(0" + integer + ")");
								} else {
									listAvgColumn.add(strDate + "(" + integer + ")");
								}
								listAvgColumn.add(doubleUpperLimit);
								listAvgColumn.add(doubleMean);
								listAvgColumn.add(doubleLowerLimit);

								listOfListAvgTrend.add(listAvgColumn);
							}
						}
					}
				}
			}

			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : listOfListAvgTrend "
					+ listOfListAvgTrend);
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getAvgTrendChartData : Exception :: " + e);
			e.printStackTrace();
			return null;
		}

	}

	public List<Map<String, Object>> getAvgTrendChartDataForAngular(String tableName, String DGroupVal, String colName,
			String idApp) {
		try {
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData tableName :: " + tableName
					+ " | DGroupVal :: " + DGroupVal + " | colName :: " + colName);
			List<Map<String, Object>> listOfListAvgTrend = new ArrayList<Map<String, Object>>();
			List<Integer> listRun = new ArrayList<Integer>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData listDate :: " + listDate);

			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData listRun :: " + listRun);

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {

						LOG.debug(
								"DistributionCheckResultsService : getAvgTrendChartData checkColumnSummaryRecordsExistsOrNotWithRunAndDate : tableName :: "
										+ tableName + " | date :: " + date + " | integer :: " + integer);

						StringBuffer strBufQuery = new StringBuffer();

						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumMeanAvg, 0) AS NumMeanAvg , "
								+ ifnull_function + "(dqcs.NumMeanThreshold , 0) AS NumMeanThreshold   , "
								+ ifnull_function + " (dqcs.NumMeanStdDev, 0) AS NumMeanStdDev , " + ifnull_function
								+ "(dqcs.Mean, 0) AS Mean ,  dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
						strBufQuery.append(" dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);
						strBufQuery.append(" AND Null_Threshold is null ");
						LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : strBufQuery :: "
								+ strBufQuery);
						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");

							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								Map<String, Object> listAvgColumn = new HashMap<>();
								double doubleNumMeanavg = sqlRowSet_strDateSqlQuery.getDouble("NumMeanAvg");
								double doubleNumMeanThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumMeanThreshold");
								double doubleNumMeanStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumMeanStdDev");
								double doubleMean = sqlRowSet_strDateSqlQuery.getDouble("Mean");
								java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());

								double doubleUpperLimit = doubleNumMeanavg
										+ (doubleNumMeanThreshold * doubleNumMeanStdDev);
								double doubleLowerLimit = doubleNumMeanavg
										- (doubleNumMeanThreshold * doubleNumMeanStdDev);

								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);
								String dateRunStr = "";
								if (integer < 10) {
									dateRunStr = strDate + "(0" + integer + ")";
								} else {
									dateRunStr = strDate + "(" + integer + ")";
								}

								listAvgColumn.put("Date", dateRunStr);
								listAvgColumn.put("UpperLimit", doubleUpperLimit);
								listAvgColumn.put("Mean", doubleMean);
								listAvgColumn.put("LowerLimit", doubleLowerLimit);
								listOfListAvgTrend.add(listAvgColumn);
							}
						}
					}
				}
			}

			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : listOfListAvgTrend "
					+ listOfListAvgTrend);
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getAvgTrendChartData : Exception :: " + e);
			e.printStackTrace();
			return null;
		}

	}

	public List<List<Object>> getStdDevAvgTrendChartData(String tableName, String DGroupVal, String colName,
			String idApp) {
		try {
			LOG.info("DistributionCheckResultsService : getStdDevAvgTrendChartData ");
			List<List<Object>> listOfListAvgTrend = new ArrayList<List<Object>>();
			List<Object> listHeader = new ArrayList<Object>();
			List<Integer> listRun = new ArrayList<Integer>();
			listHeader.add("Date");
			listHeader.add("UpperLimit");
			listHeader.add("Standard Deviation");
			listHeader.add("LowerLimit");
			// listOfListAvgTrend.add(listHeader);

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp);

			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufQuery = new StringBuffer();

						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumSDAvg, 0) AS NumSDAvg , "
								+ ifnull_function + "(dqcs.NumSDThreshold , 0) AS NumSDThreshold   , " + ifnull_function
								+ " (dqcs.NumSDStdDev, 0) AS NumSDStdDev , " + ifnull_function
								+ "(dqcs.Std_Dev, 0) AS Std_Dev , dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp);
						strBufQuery.append(" and dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);

						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");

							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								List<Object> listAvgColumn = new ArrayList<Object>();
								double doubleNumSDAvg = sqlRowSet_strDateSqlQuery.getDouble("NumSDAvg");
								double doubleNumSDThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumSDThreshold");
								double doubleNumSDStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumSDStdDev");
								double doubleStd_Dev = sqlRowSet_strDateSqlQuery.getDouble("Std_Dev");

								java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());

								double doubleUpperLimit = doubleNumSDAvg + (doubleNumSDThreshold * doubleNumSDStdDev);
								double doubleLowerLimit = doubleNumSDAvg - (doubleNumSDThreshold * doubleNumSDStdDev);
								// LOG.debug("doubleLowerLimit"+doubleLowerLimit);
								if (doubleLowerLimit < 0) {
									// if doubleLowerLimit is negative then make it zero
									doubleLowerLimit = 0.0;
								}
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);

								if (integer < 10) {
									listAvgColumn.add(strDate + " (0" + integer + ") ");
								} else {
									listAvgColumn.add(strDate + " (" + integer + ") ");
								}
								listAvgColumn.add(doubleUpperLimit);
								listAvgColumn.add(doubleStd_Dev);
								listAvgColumn.add(doubleLowerLimit);

								listOfListAvgTrend.add(listAvgColumn);
							}
						}
					}
				}
			}

			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : listOfListAvgTrend "
					+ listOfListAvgTrend);
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getAvgTrendChartData : Exception :: " + e);
			e.printStackTrace();
			return null;
		}

	}

	public List<Map<String, Object>> getStdDevAvgTrendChartDataForAngular(String tableName, String DGroupVal,
			String colName, String idApp) {
		try {
			LOG.info("DistributionCheckResultsService : getStdDevAvgTrendChartData ");
			List<Map<String, Object>> listOfListAvgTrend = new ArrayList<Map<String, Object>>();
			List<Object> listHeader = new ArrayList<Object>();
			List<Integer> listRun = new ArrayList<Integer>();
			listHeader.add("Date");
			listHeader.add("UpperLimit");
			listHeader.add("Standard Deviation");
			listHeader.add("LowerLimit");
			// listOfListAvgTrend.add(listHeader);

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp);

			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufQuery = new StringBuffer();

						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumSDAvg, 0) AS NumSDAvg , "
								+ ifnull_function + "(dqcs.NumSDThreshold , 0) AS NumSDThreshold   , " + ifnull_function
								+ " (dqcs.NumSDStdDev, 0) AS NumSDStdDev , " + ifnull_function
								+ "(dqcs.Std_Dev, 0) AS Std_Dev , dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp);
						strBufQuery.append(" and dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);
						strBufQuery.append(" AND Null_Threshold is null ");

						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");

							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								Map<String, Object> listAvgColumn = new HashMap<String, Object>();
								double doubleNumSDAvg = sqlRowSet_strDateSqlQuery.getDouble("NumSDAvg");
								double doubleNumSDThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumSDThreshold");
								double doubleNumSDStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumSDStdDev");
								double doubleStd_Dev = sqlRowSet_strDateSqlQuery.getDouble("Std_Dev");

								java.util.Date dbSqlDateConverted = new java.util.Date(dbSqlDate.getTime());

								double doubleUpperLimit = doubleNumSDAvg + (doubleNumSDThreshold * doubleNumSDStdDev);
								double doubleLowerLimit = doubleNumSDAvg - (doubleNumSDThreshold * doubleNumSDStdDev);
								// LOG.debug("doubleLowerLimit"+doubleLowerLimit);
								if (doubleLowerLimit < 0) {
									// if doubleLowerLimit is negative then make it zero
									doubleLowerLimit = 0.0;
								}
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);

								String dateRunStr = "";
								if (integer < 10) {
									dateRunStr = strDate + "(0" + integer + ")";
								} else {
									dateRunStr = strDate + "(" + integer + ")";
								}

								listAvgColumn.put("Date", dateRunStr);
								listAvgColumn.put("UpperLimit", doubleUpperLimit);
								listAvgColumn.put("StandardDeviation", doubleStd_Dev);
								listAvgColumn.put("LowerLimit", doubleLowerLimit);
								listOfListAvgTrend.add(listAvgColumn);
							}
						}
					}
				}
			}

			LOG.debug("DistributionCheckResultsService : getAvgTrendChartData : listOfListAvgTrend "
					+ listOfListAvgTrend);
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getAvgTrendChartData : Exception :: " + e);
			e.printStackTrace();
			return null;
		}

	}

	public JSONObject getdistrubutionCheckTrendChartDetails(String tableName, String colName,
			List<String> listTable_dGroupVal, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			LOG.debug("DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : tableName :: "
					+ tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			LOG.debug(
					"DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : listTable_dGroupVal :: "
							+ listTable_dGroupVal);

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableName(tableName, idApp);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummaryWithTableName(tableName, idApp);

			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);
			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date localdate : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, localdate, integer, idApp)) {
						List<Object> listOfChartElements = new ArrayList<Object>();
						if (integer < 10) {
							listOfChartElements.add(localdate + "(0" + integer + ")");
						} else {
							listOfChartElements.add(localdate + "(" + integer + ")");
						}

						for (String localstring : listTable_dGroupVal) {
							if (checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndDgroupVal(tableName, localdate,
									integer, localstring)) {
								String strSqlQueryGetChartList = "SELECT " + ifnull_function + "(Mean,0) as Mean FROM "
										+ tableName + " WHERE idApp=" + idApp + " and date = '" + localdate
										+ "' AND dGroupVal = '" + localstring + "' AND ColName='" + colName
										+ "' AND RUN = " + integer;
								SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1
										.queryForRowSet(strSqlQueryGetChartList);

								boolean flag = false;
								while (sqlRowSet_strSqlQueryGetChartList.next()) {
									listOfChartElements.add(sqlRowSet_strSqlQueryGetChartList.getDouble("Mean"));
									flag = true;
								}

								if (!flag) {
									listOfChartElements.add(0);
								}
							} else {
								listOfChartElements.add(0);
							}

						}
						listRowChartData.add(listOfChartElements);
					}
				}
			}
			JSONArray jsonArrayChartList = new JSONArray(listRowChartData);

			jsonObject.put("header", jsonArrayDGroupVal);
			jsonObject.put("chart", jsonArrayChartList);

		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : Exception :: " + e);
		}
		return jsonObject;
	}
	
	public Map<String,Object> getdistrubutionCheckTrendChartDetailsForAngular(String tableName, String colName,
			List<String> listTable_dGroupVal, String idApp) throws SQLException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		try {
			LOG.debug("DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : tableName :: "
					+ tableName);
			List<Map<String,Object>> listRowChartData = new ArrayList<>();

			LOG.debug(
					"DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : listTable_dGroupVal :: "
							+ listTable_dGroupVal);

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableName(tableName, idApp);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummaryWithTableName(tableName, idApp);

			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);
			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date localdate : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, localdate, integer, idApp)) {
						Map<String,Object> listOfChartElements = new HashMap<String,Object>();
						
						String dateRunStr = "";
						if (integer < 10) {
							dateRunStr = localdate + "(0" + integer + ")";
						} else {
							dateRunStr = localdate + "(" + integer + ")";
						}

						for (String localstring : listTable_dGroupVal) {
							double dgroup_mean = 0;
							
							if (checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndDgroupVal(tableName, localdate,
									integer, localstring)) {
								String strSqlQueryGetChartList = "SELECT " + ifnull_function + "(Mean,0) as Mean FROM "
										+ tableName + " WHERE idApp=" + idApp + " and date = '" + localdate
										+ "' AND dGroupVal = '" + localstring + "' AND ColName='" + colName
										+ "' AND RUN = " + integer;
								if(tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")){
									strSqlQueryGetChartList = strSqlQueryGetChartList + " AND  Null_Threshold IS NULL ";
								}
								SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1
										.queryForRowSet(strSqlQueryGetChartList);

								while (sqlRowSet_strSqlQueryGetChartList.next()) {
									dgroup_mean = sqlRowSet_strSqlQueryGetChartList.getDouble("Mean");
									break;
								}
							} 
							
							listOfChartElements.put("date", dateRunStr);
							listOfChartElements.put("mean_"+localstring, dgroup_mean);

						}

						listRowChartData.add(listOfChartElements);
					}
				}
			}

			responseMap.put("header", listTable_dGroupVal);
			responseMap.put("chart", listRowChartData);

		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : getdistrubutionCheckTrendChartDetails : Exception :: " + e);
		}
		return responseMap;
	}

	/**
	 * Last Updated : 28thApril,2020 Code By: Anant S. Mahale
	 * 
	 * @param tableName : from rollupAnalysisDropdownDataLoad() to generate dropdown
	 *                  data
	 * @return : JSON Object which have to JSON arrays
	 * @throws SQLException
	 */
	public JSONObject rollupAnalysisDropdownDataLoadJsonObj(String tableName, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			String strColNameSqlQuery = "SELECT distinct ColName FROM " + tableName + " where idApp=" + idApp;
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
				strColNameSqlQuery += " and NumMeanThreshold IS NOT NULL";
				strColNameSqlQuery += " and Null_Threshold IS NULL";
			}
			SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strColNameSqlQuery);
			List<String> listColName = new ArrayList<String>();
			while (sqlRowSet_strColNameSqlQuery.next()) {
				listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
			}
			JSONArray jsonArrayColName = new JSONArray(listColName);

			String strdGroupColSqlQuery = "SELECT distinct dGroupCol FROM " + tableName + " where idApp=" + idApp;
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
				strdGroupColSqlQuery += " and NumMeanThreshold IS NOT NULL";
				strdGroupColSqlQuery += " and Null_Threshold IS NULL";
			}
			SqlRowSet sqlRowSet_strDGroupColSqlQuery = jdbcTemplate1.queryForRowSet(strdGroupColSqlQuery);
			List<String> listdGroupCol = new ArrayList<String>();
			while (sqlRowSet_strDGroupColSqlQuery.next()) {
				String strDGroupCal = sqlRowSet_strDGroupColSqlQuery.getString("dGroupCol");
				String[] arrdGroupColParts = strDGroupCal.split("-");
				listdGroupCol = Arrays.asList(arrdGroupColParts);
			}

			JSONArray jsonArrayDGroupCol = new JSONArray(listdGroupCol);

			jsonObject.put("dGroupCalSplit", jsonArrayDGroupCol);
			jsonObject.put("colName", jsonArrayColName);

		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : rollupAnalysisDropdownDataLoadJsonObj : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Last Updated : 28thApril,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrRollUpVariable : its word from dgroupVal which is inbetween
	 *                               two - ( we have to find its location/ index)
	 * @param tableName              : respective table name
	 * @return : index of RollUp variable in DGroupVal's splited array
	 */
	public int getLocationOfDGroupCal(String paramStrRollUpVariable, String tableName, String idApp) {
		try {
			String strdGroupColSqlQuery = "SELECT distinct dGroupCol FROM " + tableName + " where idApp=" + idApp;
			LOG.debug(
					"DistributionCheckResultsService : rollupAnalysisDropdownDataLoadJsonObj : strDateSqlQuery :: "
							+ strdGroupColSqlQuery);
			SqlRowSet sqlRowSet_strDGroupColSqlQuery = jdbcTemplate1.queryForRowSet(strdGroupColSqlQuery);
			List<String> listdGroupCol = new ArrayList<String>();
			while (sqlRowSet_strDGroupColSqlQuery.next()) {
				String strDGroupCal = sqlRowSet_strDGroupColSqlQuery.getString("dGroupCol");
				String[] arrdGroupColParts = strDGroupCal.split("-");
				listdGroupCol = Arrays.asList(arrdGroupColParts);
			}
			int intIndex = listdGroupCol.indexOf(paramStrRollUpVariable);
			int returnVal = intIndex + 1;
			return returnVal;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Last Updated : 29thApril,2020 Code By: Anant S. Mahale
	 * 
	 * @param tableName : tableName from sumOfNumStats()
	 * @return : JSON Object of dgroup values
	 * @throws SQLException
	 */
	public JSONObject getsumOfNumStatsObj(String tableName) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			String strColNameSqlQuery = "SELECT distinct DGroupVal FROM " + tableName;
			SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strColNameSqlQuery);
			List<String> listColName = new ArrayList<String>();
			while (sqlRowSet_strColNameSqlQuery.next()) {
				listColName.add(sqlRowSet_strColNameSqlQuery.getString("DGroupVal"));
			}
			JSONArray jsonArrayColName = new JSONArray(listColName);

			jsonObject.put("colName", jsonArrayColName);

		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getsumOfNumStatsObj : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param tableName
	 * @param dGroupVal
	 * @param colName     : Column Name
	 * @param paramIntRun : Run
	 * @return JSON object having date, upper limit, sume of num stats and lowe
	 *         limit
	 * @throws SQLException
	 */
	public JSONObject getsumOfNumStatsChartData(String tableName, String dGroupVal, String colName, int paramIntRun,
			String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			LOG.debug("DistributionCheckResultsService : getsumOfNumStatsChartData ");
			List<List<Object>> listOfListDataDriftChart = new ArrayList<List<Object>>();
			List<Integer> listRun = new ArrayList<Integer>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, dGroupVal, idApp);

			if (paramIntRun == 0) {
				listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, dGroupVal, idApp);
			} else {
				listRun.add(paramIntRun);
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";
			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufDirftChartQuery = new StringBuffer();

						// Query compatibility changes for both POSTGRES and MYSQL
						if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND((dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold))::numeric,2),0) AS UpperLimit, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "( ROUND(dqcs.sumOfNumStat::numeric,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "(ROUND((dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold))::numeric,2),0) AS LowerLimit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("'::date AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						} else {
							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND(dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS UpperLimit, ");
							strBufDirftChartQuery.append(
									" " + ifnull_function + "( ROUND(dqcs.sumOfNumStat,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "(ROUND(dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS LowerLimit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("' AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						}
//						strBufDirftChartQuery.append("order by dqcs.run asc ");
						LOG.debug(
								"DistributionCheckResultsService : getsumOfNumStatsChartData : strBufDirftChartQuery :: "
										+ strBufDirftChartQuery);
						SqlRowSet sqkRowSetDirftChartQuery = jdbcTemplate1
								.queryForRowSet(strBufDirftChartQuery.toString());
						while (sqkRowSetDirftChartQuery.next()) {
							List<Object> listChartRows = new ArrayList<Object>();
							if (integer < 10) {
								listChartRows.add(sqkRowSetDirftChartQuery.getDate("Date") + "(0" + integer + ")");
							} else {
								listChartRows.add(sqkRowSetDirftChartQuery.getDate("Date") + "(" + integer + ")");
							}

							listChartRows.add(sqkRowSetDirftChartQuery.getDouble("UpperLimit"));
							listChartRows.add(sqkRowSetDirftChartQuery.getDouble("SumOfNumStats"));
							listChartRows.add(sqkRowSetDirftChartQuery.getDouble("LowerLimit"));
							listOfListDataDriftChart.add(listChartRows);
						}
					}
				}
			}
			JSONArray jsonArray = new JSONArray(listOfListDataDriftChart);
			jsonObject.put("sumOfNumStatsChart", jsonArray);
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getsumOfNumStatsChartData : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	public List<Map<String, Object>> getsumOfNumStatsChartDataForAngular(String tableName, String dGroupVal,
			String colName, int paramIntRun, String idApp) throws SQLException {
		List<Map<String, Object>> listOfListDataDriftChart = new ArrayList<Map<String, Object>>();
		try {
			LOG.info("DistributionCheckResultsService : getsumOfNumStatsChartData ");
			List<Integer> listRun = new ArrayList<Integer>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, dGroupVal, idApp);

			if (paramIntRun == 0) {
				listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, dGroupVal, idApp);
			} else {
				listRun.add(paramIntRun);
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";
			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufDirftChartQuery = new StringBuffer();

						// Query compatibility changes for both POSTGRES and MYSQL
						if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND((dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold))::numeric,2),0) AS UpperLimit, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "( ROUND(dqcs.sumOfNumStat::numeric,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "(ROUND((dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold))::numeric,2),0) AS LowerLimit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("'::date AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						} else {
							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND(dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS UpperLimit, ");
							strBufDirftChartQuery.append(
									" " + ifnull_function + "( ROUND(dqcs.sumOfNumStat,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "(ROUND(dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS LowerLimit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("' AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						}
						strBufDirftChartQuery.append(" AND Null_Threshold is null ");
						LOG.debug(
								"DistributionCheckResultsService : getsumOfNumStatsChartData : strBufDirftChartQuery :: "
										+ strBufDirftChartQuery);
						SqlRowSet sqkRowSetDirftChartQuery = jdbcTemplate1
								.queryForRowSet(strBufDirftChartQuery.toString());
						while (sqkRowSetDirftChartQuery.next()) {
							Map<String, Object> listChartRow = new HashMap<String, Object>();

							SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
							String strDate = formatter.format(sqkRowSetDirftChartQuery.getDate("Date"));
							String dateRunStr = "";
							if (integer < 10) {
								dateRunStr = strDate + "(0" + integer + ")";
							} else {
								dateRunStr = strDate + "(" + integer + ")";
							}

							listChartRow.put("Date", dateRunStr);
							listChartRow.put("UpperLimit", sqkRowSetDirftChartQuery.getDouble("UpperLimit"));
							listChartRow.put("SumOfNumStats", sqkRowSetDirftChartQuery.getDouble("SumOfNumStats"));
							listChartRow.put("LowerLimit", sqkRowSetDirftChartQuery.getDouble("LowerLimit"));
							listOfListDataDriftChart.add(listChartRow);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getsumOfNumStatsChartData : Exception :: " + e);
			e.printStackTrace();
		}
		return listOfListDataDriftChart;
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name ( column summary table)
	 * @return : list of distinct run from table
	 */
	public List<Integer> getListOfRunFromColumnSummaryWithTableName(String paramStrTableName, String idApp) {
		try {
			List<Integer> listRun = new ArrayList<Integer>();
			String strDistinctRunQuery = "SELECT distinct run FROM " + paramStrTableName + " where idApp=" + idApp
					+ " limit 200";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listRun.add(sqlRowSet_strDistinctRunQuery.getInt("run"));
			}
			// Collections.sort(listRun);
			return listRun;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : getListOfRunFromColumnSummaryWithTableName : Exception :: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name ( column summary table)
	 * @return : list of distinct run from table
	 */
	public List<Integer> getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(String paramStrTableName,
			String paramStrDGroupVal, String idApp) {
		try {
			List<Integer> listRun = new ArrayList<Integer>();
			String strDistinctRunQuery = "SELECT distinct run FROM " + paramStrTableName + " where idApp=" + idApp
					+ " and DGroupVal = '" + paramStrDGroupVal + "'";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listRun.add(sqlRowSet_strDistinctRunQuery.getInt("run"));
			}
			return listRun;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : getListOfRunFromColumnSummaryWithTableNameAndDGroupVal : Exception :: "
							+ e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name ( column summary)
	 * @return : list of distinct date from given table
	 */
	public List<Date> getListOfDateFromColumnSummarywithTableNameAndDGroupVal(String paramStrTableName,
			String paramStrDGroupVal, String idApp) {

		try {
			List<Date> listDate = new ArrayList<Date>();
			String strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where idApp=" + idApp
					+ " and DGroupVal = '" + paramStrDGroupVal + "'";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			return listDate;
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getListOfDateFromColumnSummary : Exception :: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name ( column summary)
	 * @return : list of distinct date from given table
	 */
	public List<Date> getListOfDateFromColumnSummarywithTableName(String paramStrTableName, String idApp) {

		try {
			List<Date> listDate = new ArrayList<Date>();
			String strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where idApp=" + idApp
					+ " limit 200";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			return listDate;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : getListOfDateFromColumnSummarywithTableName : Exception :: "
							+ e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name
	 * @param paramDate         : date
	 * @param paramIntRun       : Run
	 * @return : check record exists or not with given date, run and table
	 */
	public boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDate(String paramStrTableName, Date paramDate,
			int paramIntRun, String idApp) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE idApp=" + idApp
					+ " and DATE = '" + paramDate + "' AND RUN = " + paramIntRun;
			LOG.debug(
					"DistributionCheckResultsService : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : strQuery "
							+ strQuery);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : Exception :: "
							+ e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Last Updated : 06thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name
	 * @param paramDate         : date
	 * @param paramIntRun       : Run
	 * @param paramStrDGroupVal : DGroupVal
	 * @return : check record exists or not with given date, run and table
	 */
	public boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndDgroupVal(String paramStrTableName,
			Date paramDate, int paramIntRun, String paramStrDGroupVal) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE DATE = '" + paramDate
					+ "' AND RUN = " + paramIntRun + " AND DGroupVal = '" + paramStrDGroupVal + "'";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name
	 * @param paramDate         : date
	 * @param paramIntRun       : Run
	 * @param whereClause       : RollUpVariable with rollup element
	 * @return : check record exists or not with given date, run and table
	 */
	public boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndRollUpElement(String paramStrTableName,
			Date paramDate, int paramIntRun, String whereClause) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE DATE = '" + paramDate
					+ "' AND RUN = " + paramIntRun + " AND " + whereClause;
			LOG.debug(
					"DistributionCheckResultsService : checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndRollUpElement : strQuery "
							+ strQuery);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndRollUpElement : Exception :: "
							+ e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName : table name
	 * @param paramDate         : date
	 * @param paramIntRun       : Run
	 * @return : check record exists or not with given date, run and table
	 */
	public boolean checkDataDriftCountSummarysExistsOrNotWithRunAndDate(String paramStrTableName, Date paramDate,
			int paramIntRun, String idApp) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(run) AS runcount FROM " + paramStrTableName + " WHERE idApp=" + idApp
					+ " and Date = '" + paramDate + "' AND RUN = " + paramIntRun;
			LOG.debug(
					"DistributionCheckResultsService : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : strQuery "
							+ strQuery);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("runcount");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(
					"DistributionCheckResultsService : checkDataDriftCountSummarysExistsOrNotWithRunAndDate : Exception :: "
							+ e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Last Updated : 15thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param tableName
	 * @param colName
	 * @return JSON object having date, uniqueValuesCount, missingValueCount,
	 *         newValueCount
	 * @throws SQLException
	 */
	public JSONObject getdatadriftChartData(String tableName, String colName, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			LOG.info("DistributionCheckResultsService : getdatadriftChartData ");
			List<List<Object>> listOfListDataDriftChart = new ArrayList<List<Object>>();
			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummaryWithTableName(tableName, idApp);

			List<Date> listDate = new ArrayList<Date>();
			String strDistinctRunQuery = "SELECT DISTINCT Date FROM " + tableName + " where idApp=" + idApp;
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}

			for (Date date : listDate) {

				for (Integer integer : listRun) {
					if (checkDataDriftCountSummarysExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufDirftChartQuery = new StringBuffer();
						strBufDirftChartQuery
								.append(" SELECT ddcs.uniqueValuesCount, ddcs.missingValueCount, ddcs.newValueCount ");
						strBufDirftChartQuery.append(" FROM ");
						strBufDirftChartQuery.append(tableName);
						strBufDirftChartQuery.append(" AS ddcs WHERE ddcs.idApp=" + idApp + " and ");
						strBufDirftChartQuery.append(" ddcs.Date = '");
						strBufDirftChartQuery.append(date);
						strBufDirftChartQuery.append("' AND ddcs.colName = '");
						strBufDirftChartQuery.append(colName + "' AND ddcs.Run = " + integer);
//						strBufDirftChartQuery.append("order by dqcs.run asc ");
						LOG.debug(
								"DistributionCheckResultsService : getdatadriftChartData : strBufDirftChartQuery :: "
										+ strBufDirftChartQuery);
						SqlRowSet sqkRowSetDirftChartQuery = jdbcTemplate1
								.queryForRowSet(strBufDirftChartQuery.toString());
						while (sqkRowSetDirftChartQuery.next()) {
							List<Object> listChartRows = new ArrayList<Object>();
							if (integer < 10) {
								listChartRows.add(date + "(0" + integer + ")");
							} else {
								listChartRows.add(date + "(" + integer + ")");
							}

							listChartRows.add(sqkRowSetDirftChartQuery.getInt("uniqueValuesCount"));
							listChartRows.add(sqkRowSetDirftChartQuery.getInt("missingValueCount"));
							listChartRows.add(sqkRowSetDirftChartQuery.getInt("newValueCount"));
							listOfListDataDriftChart.add(listChartRows);
						}
					}
				}
			}
			JSONArray jsonArray = new JSONArray(listOfListDataDriftChart);
			jsonObject.put("dataDrift", jsonArray);
		} catch (Exception e) {
			LOG.error("DistributionCheckResultsService : getdatadriftChartData : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}
}
