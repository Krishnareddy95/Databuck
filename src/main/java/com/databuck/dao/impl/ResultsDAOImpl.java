package com.databuck.dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

//import com.amazonaws.services.dynamodbv2.xspec.S;
import com.databuck.bean.ColumnAggregateRequest;
import com.databuck.bean.DATA_QUALITY_Column_Summary;
import com.databuck.bean.DATA_QUALITY_Record_Anomaly;
import com.databuck.bean.DATA_QUALITY_Transaction_Summary;
import com.databuck.bean.DATA_QUALITY_Transactionset_sum_A1;
import com.databuck.bean.DQSummaryBean;
import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.bean.Idapp;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.ProcessData;
import com.databuck.bean.Project;
import com.databuck.bean.SqlRule;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dto.AverageDQI;
import com.databuck.dto.DateVsDTSGraph;
import com.databuck.service.IProjectService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ResultsDAOImpl implements IResultsDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private JdbcTemplate jdbcTemplate2;

	@Autowired
	private IProjectService IProjectservice;

	@Autowired
	private IListDataSourceDAO listDataSourceDAO;

	@Autowired
	IValidationCheckDAO validationcheckdao;
	
	private static final Logger LOG = Logger.getLogger(ResultsDAOImpl.class);

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1) {
		this.jdbcTemplate1 = jdbcTemplate1;
		this.jdbcTemplate = jdbcTemplate;

	}

	public Map<String, String> getdatafromlistdftranrule(long idApp) {
		Map<String, String> map = new HashMap<>();
		try {
			String sql = "select dupRow from listDFTranRule where type='all' and idApp=" + idApp;
			String dupRowall = jdbcTemplate.queryForObject(sql, String.class);
			// LOG.debug("dupRowall=" + dupRowall);
			String sql1 = "select dupRow from listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRowidentity = jdbcTemplate.queryForObject(sql1, String.class);
			// LOG.debug("dupRowidentity=" + dupRowidentity);
			map.put(dupRowall, dupRowidentity);
			LOG.debug("getdatafromlistdftranrule" + map);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return map;
	}

	public Map<String, String> getDataFromListDFTranRuleForMap(long idApp) {
		Map<String, String> map = new HashMap<>();
		try {
			String sql = "select dupRow from listDFTranRule where type='all' and idApp=" + idApp;
			String dupRowall = jdbcTemplate.queryForObject(sql, String.class);
			LOG.debug("dupRowall=" + dupRowall);
			String sql1 = "select dupRow from listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRowidentity = jdbcTemplate.queryForObject(sql1, String.class);
			LOG.debug("dupRowidentity=" + dupRowidentity);
			map.put("all", dupRowall);
			map.put("identity", dupRowidentity);
			LOG.debug("getdatafromlistdftranrule" + map);
		} catch (Exception e) {
			 LOG.error(e.getMessage());
			 e.printStackTrace();
		}
		return map;
	}

	public Long getnumberofNumericalColumnsFailed(String tableName, ListApplications listApplicationsData) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "select count(NumMeanStatus) from " + tableName + " where idApp=" + idApp
					+ " and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ ")) and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ ") and  NumMeanStatus='failed' limit 200";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select count(NumMeanStatus) from " + tableName + " where  idApp=" + idApp
						+ " and NumMeanStatus='failed'";
			}
			Long numberofNumericalColumnsYes = jdbcTemplate1.queryForObject(sql, Long.class);

			if (numberofNumericalColumnsYes == 0) {
				sql = "select count(NumSDStatus) from " + tableName + " where idApp=" + idApp
						+ " and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
						+ " and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
						+ ")) and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
						+ ") and  NumSDStatus='failed'";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "select count(NumSDStatus) from " + tableName + " where idApp=" + idApp
							+ " and NumSDStatus='failed'";
				}
				numberofNumericalColumnsYes = jdbcTemplate1.queryForObject(sql, Long.class);
			}

			return numberofNumericalColumnsYes;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getnumberofStringColumnsFailed(String tableName, ListApplications listApplicationsData) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "select count(String_Status) from " + tableName + " where  idApp=" + idApp
					+ " and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(SELECT max(date) from " + tableName + ")) and Date=(SELECT max(date) from "
					+ tableName + " where idApp=" + idApp + ") and  String_Status='failed'";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select count(String_Status) from " + tableName + " where  idApp=" + idApp
						+ " and String_Status='failed'";
			}
			Long numberofStringColumnsFailed = jdbcTemplate1.queryForObject(sql, Long.class);
			return numberofStringColumnsFailed;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public String getRecordAnomalyForWhichTableInListApplication(Long idApp) {
		try {
			String sql = "select recordCountAnomaly from listApplications where idApp=" + idApp;
			String recordCountAnomaly = jdbcTemplate.queryForObject(sql, String.class);
			return recordCountAnomaly;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "";
	}

	public Long getRecordAnomalyTotal(String tableName, Long idApp) {
		String sql = "";
		try {
			String recordCountAnomaly = getRecordAnomalyForWhichTableInListApplication(idApp);
			if (!recordCountAnomaly.equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			sql = "select RecordCount from " + tableName + " where idApp=" + idApp + " and Date=(SELECT max(Date) FROM "
					+ tableName + " where idApp=" + idApp + ") and run=(select max(run) from " + tableName
					+ " where idApp=" + idApp + " and date=(SELECT max(Date) FROM " + tableName + " where idApp="
					+ idApp + "))  LIMIT 1";

			LOG.debug("getRecordAnomalyTotal=" + sql);
			Long recordAnomalyTotal = jdbcTemplate1.queryForObject(sql, Long.class);
			return recordAnomalyTotal;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public SqlRowSet getIdDataAndAppNameFromListApplications(Long idApp) {
		try {

			String sql = "select idData,name from listApplications where idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			return queryForRowSet;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public Long getNumberofNumericalColumnsYes(Long idData) {
		try {
			String sql = "select count(*) from listDataDefinition where numericalStat='Y' and idData=" + idData;
			Long numberofNumericalColumnsYes = jdbcTemplate.queryForObject(sql, Long.class);
			return numberofNumericalColumnsYes;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getNumberofStringColumnsYes(Long idData) {
		try {
			String sql = "select count(*) from listDataDefinition where stringStat='Y' and idData=" + idData;
			Long numberofStringColumnsYes = jdbcTemplate.queryForObject(sql, Long.class);
			return numberofStringColumnsYes;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getAverageRecordCount(ListApplications listApplications, long idApp) {
		try {
			String tableName = "";
			if (listApplications.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			String sql = "SELECT SUM(RecordCount)/COUNT(distinct Run) FROM " + tableName + " where idApp=?";
			Long averageRecordCount = jdbcTemplate1.queryForObject(sql, Long.class, idApp);
			if (averageRecordCount == null)
				averageRecordCount = 0l;
			return averageRecordCount;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getnonNullColumns(Long idData) {
		try {
			String sql = "select count(*) from listDataDefinition where nonNull='Y' and idData=" + idData;
			Long nonNullColumns = jdbcTemplate.queryForObject(sql, Long.class);
			return nonNullColumns;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getnonNullColumnsFailed(String tableName, ListApplications listApplicationsData) {
		String recordCountAnomaly = listApplicationsData.getRecordCountAnomaly();
		try {
			String sql = "";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				sql = "select count(Null_Value) from " + tableName
						+ " where status='failed' and date=(SELECT max(date) from " + tableName + ")"
						+ " and Run=(SELECT max(Run) from " + tableName + " where Date=(SELECT max(date) from "
						+ tableName + "))";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "select count(*) from (select count(Null_Value) from " + tableName
							+ " where status='failed' group By Status,ColName) As Alias limit 1";
				}
			} else {
				String RCATableName = "DATA_QUALITY_Column_Summary";
				long idApp = listApplicationsData.getIdApp();
				sql = "Select Count(*) from (SELECT ColName FROM " + RCATableName + " " + " where idApp=" + idApp
						+ " and Date=(SELECT max(date) from " + RCATableName + " where idApp=" + idApp
						+ ") AND  Run=(select max(Run) from " + RCATableName + " where idApp=" + idApp
						+ " and Date=(SELECT max(date) from " + RCATableName + " where idApp=" + idApp
						+ ")) AND Status='failed' group By Status,ColName) AS Alias";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "Select Count(*) from (SELECT ColName FROM " + RCATableName + " "
							+ " where Status='failed' group By Status,ColName) AS Alias";
				}
				LOG.debug("sql:" + sql);
			}

			Long nonNullColumnsFailed = jdbcTemplate1.queryForObject(sql, Long.class);
			return nonNullColumnsFailed;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getAllFields(String tableName) {
		try {
			String sql = "select  Duplicate from " + tableName + " where Type='all' and Date=(SELECT max(date) from "
					+ tableName + ")" + "and Run=(SELECT max(Run) from " + tableName
					+ " where Date=(SELECT max(date) from " + tableName + ")) limit 1";
			Long allFields = 0l;
			try {
				allFields = jdbcTemplate1.queryForObject(sql, Long.class);
			} catch (org.springframework.dao.EmptyResultDataAccessException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("allFields=" + allFields);
			return allFields;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getIdentityFields(String tableName) {
		try {
			String sql = "select  Duplicate from " + tableName
					+ " where Type='identity' and Date=(SELECT max(date) from " + tableName + ")"
					+ "and Run=(SELECT max(Run) from " + tableName + " where Date=(SELECT max(date) from " + tableName
					+ ")) limit 1";

			Long identityFields = 0l;
			try {
				identityFields = jdbcTemplate1.queryForObject(sql, Long.class);
			} catch (EmptyResultDataAccessException e) {
				LOG.error(e.getMessage());

			}
			return identityFields;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getAllFields(String tableName, ListApplications listApplicationsData) {
		try {
			String sql = "";
			long idApp = listApplicationsData.getIdApp();
			sql = "select  Duplicate from " + tableName + " where idApp=" + idApp
					+ " and Type='all' and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='all')" + "and Run=(SELECT max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Type='all' and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='all')) limit 1";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select  sum(Duplicate) from " + tableName + " where Type='all'";
			}
			Long allFields = 0l;
			try {
				allFields = jdbcTemplate1.queryForObject(sql, Long.class);
			} catch (EmptyResultDataAccessException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("allFields=" + allFields);
			return allFields;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getIdentityFields(String tableName, ListApplications listApplicationsData) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "select  Duplicate from " + tableName + " where idApp=" + idApp
					+ " and Type='identity' and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity')" + "and Run=(SELECT max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity' and Date=(SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity')) limit 1";

			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select  sum(Duplicate) from " + tableName + " where idApp=" + idApp + " and Type='identity'";
			}
			Long identityFields = 0l;
			try {
				identityFields = jdbcTemplate1.queryForObject(sql, Long.class);
			} catch (EmptyResultDataAccessException e) {
				LOG.error(e.getMessage());

			}
			return identityFields;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public long getIdappFromListApplictions(long idData) {
		String sql = "select idApp from listApplications where idData=" + idData;
		Long idApp = 0l;
		try {
			idApp = jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return idApp;

	}

	public long getIdDataFromListApplictions(long idApp) {
		String sql = "select idData from listApplications where idApp=" + idApp;
		Long idData1 = 0l;
		try {
			idData1 = jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return idData1;

	}

	public SqlRowSet getTableNamesFromResultMasterTable(long idApp) {
		try {
			String Query = "select Table_Name from result_master_table where  AppType='DF' and appID=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(Query);
			/*
			 * while(queryForRowSet.next()){
			 * LOG.debug("getTableNamesFromResultMasterTable");
			 * LOG.debug(queryForRowSet.getString(1)); }
			 */
			return queryForRowSet;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public int getTableSize(String tableName) {
		int tableSize = 0;
		try {

			String Query = "select count(column_name) as count from information_schema.columns where" + " table_name='"
					+ tableName + "'";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(Query);

			if (queryForRowSet.next()) {
				tableSize = queryForRowSet.getInt("count");
			}
			return tableSize;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 1;
	}

	public SqlRowSet getTableMaxDateAndRun(long idApp) {
		int tableSize = 0;

		String Query = "";

		String apptype = "select appType as appType from listApplications where idApp = " + idApp;
		SqlRowSet apptypeRS = jdbcTemplate.queryForRowSet(apptype);
		String appType = "";
		if (apptypeRS.next()) {
			appType = apptypeRS.getString("appType");
		}

		if (appType.equals("Schema Matching")) {

			Query = "select MAX(Date) as Date, MAX(Run) as Run from Schema_Matching_" + idApp
					+ "_Transaction_Summary where" + " Date = (select MAX(Date) from Schema_Matching_" + idApp
					+ "_Transaction_Summary)";

		} else if (appType.equals("Data Matching")) {

			Query = "select MAX(Date) as Date, MAX(Run) as Run from DATA_MATCHING_" + idApp + "_SUMMARY where"
					+ " Date = (select MAX(Date) from DATA_MATCHING_" + idApp + "_SUMMARY)";

		} else if (appType.equals("Data Forensics")) {
			Query = "select MAX(Date) as Date, MAX(Run) as Run from DATA_QUALITY_Transactionset_sum_A1 where "
					+ " idApp=" + idApp
					+ " and Date = (select MAX(Date) from DATA_QUALITY_Transactionset_sum_A1 where idApp=" + idApp
					+ ")";
		} else if (appType.equals("Primary Key Matching")) {
			Query = "select MAX(Date) as Date, MAX(Run) as Run from DATA_MATCHING_" + idApp + "_SUMMARY where"
					+ " Date = (select MAX(Date) from DATA_MATCHING_" + idApp + "_SUMMARY)";
		}

		LOG.debug(Query);

		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(Query);
		return queryForRowSet;
	}

	public boolean getmeasurementFromListDataDefinition(Long idData) {
		try {
			String sql = "SELECT count(*) FROM listDataDefinition WHERE idData=? and measurement='Y'";
			Integer measurementCount = jdbcTemplate.queryForObject(sql, Integer.class, idData);
			LOG.debug("measurementCount=" + measurementCount + "" + idData);
			return measurementCount >= 1;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return false;
	}

	public List<DATA_QUALITY_Transactionset_sum_A1> getTransactionSetTable(String tableName, Long idData) {
		try {
			DecimalFormat numberFormat = new DecimalFormat("#.00");
			String Query = "select * from " + tableName;
			RowMapper<DATA_QUALITY_Transactionset_sum_A1> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transactionset_sum_A1 dqts = new DATA_QUALITY_Transactionset_sum_A1();
				dqts.setId(rs.getLong("Id"));
				dqts.setDate(rs.getString("Date"));
				dqts.setRun(rs.getInt("Run"));
				dqts.setRecordCount(rs.getInt("RecordCount"));
				dqts.setDuplicateDataSet(rs.getString("DuplicateDataSet"));
				// LOG.debug("rs.getDouble(RC_Std_Dev):" +
				// rs.getDouble("RC_Std_Dev"));
				// LOG.debug(rs.wasNull() ? "null value" :
				// rs.getDouble("RC_Std_Dev"));
				// To show null as empty in UI
				if (rs.getString("RC_Std_Dev") != null) {
					dqts.setRc_Std_Dev(Double.parseDouble(numberFormat.format(rs.getDouble("RC_Std_Dev"))));
				} else {
					dqts.setRc_Std_Dev(null);
				}

				if (rs.getString("RC_Mean") != null) {
					dqts.setRc_Mean(Double.parseDouble(numberFormat.format(rs.getDouble("RC_Mean"))));
				} else {
					dqts.setRc_Mean(null);
				}

				if (rs.getString("RC_Deviation") != null) {
					dqts.setRc_Deviation(Double.parseDouble(numberFormat.format(rs.getDouble("RC_Deviation"))));
				} else {
					dqts.setRc_Deviation(null);
				}

				dqts.setRc_Std_Dev_Status(rs.getString("RC_Std_Dev_Status"));
				dqts.setRc_Mean_Moving_Avg(rs.getString("RC_Mean_Moving_Avg"));
				dqts.setRc_Mean_Moving_Avg_Status(rs.getString("RC_Mean_Moving_Avg_Status"));
				dqts.setFileNameValidationStatus(rs.getString("fileNameValidationStatus"));
				dqts.setColumnOrderValidationStatus(rs.getString("columnOrderValidationStatus"));
				dqts.setDayOfYear(rs.getLong("dayOfYear"));
				dqts.setMonth(rs.getString("month"));
				// LOG.debug("month=" + rs.getString("month"));
				dqts.setDayOfMonth(rs.getLong("dayOfMonth"));
				dqts.setDayOfWeek(rs.getString("dayOfWeek"));
				dqts.setHourOfDay(rs.getLong("hourOfDay"));
				// dqts.setdGroupCol(rs.getString("dGroupCol"));
				dqts.setdGroupVal(rs.getString("dGroupVal"));
				boolean measurementBoolean = getmeasurementFromListDataDefinition(idData);
				if (measurementBoolean) {

					if (rs.getString("M_Mean") != null) {
						dqts.setM_Mean(Double.parseDouble(numberFormat.format(rs.getDouble("M_Mean"))));
					} else {
						dqts.setM_Mean(null);
					}

					if (rs.getString("M_Std_Dev") != null) {
						dqts.setM_Std_Dev(Double.parseDouble(numberFormat.format(rs.getDouble("M_Std_Dev"))));
					} else {
						dqts.setM_Std_Dev(null);
					}

					if (rs.getString("M_Deviation") != null) {
						dqts.setM_Deviation(Double.parseDouble(numberFormat.format(rs.getDouble("M_Deviation"))));
					} else {
						dqts.setM_Deviation(null);
					}

					dqts.setM_Std_Dev_Status(rs.getString("M_Std_Dev_Status"));
				}
				return dqts;
			};

			List<DATA_QUALITY_Transactionset_sum_A1> colunSummaryData = jdbcTemplate1.query(Query, rowMapper);
			return colunSummaryData;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public Map<Long, String> getResultMasterTableAppNamesAndAppId() {
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1
					.queryForRowSet("select appID,AppName from result_master_table where  AppType='DF' ");

			Map<Long, String> map = new HashMap<Long, String>();
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public Map<Long, String> getQualityApplicatinNamesAndId() {
		try {
			SqlRowSet queryForRowSet = jdbcTemplate
					.queryForRowSet("select idApp,name from listApplications where  appType='DATA_QUALITY' ");

			Map<Long, String> map = new HashMap<Long, String>();
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List getDisplayNamesForNullTest(Long idApp, Long idData) {
		String query = "select nonNullCheck from listApplications where idApp=" + idApp;
		String nonNullCheck = jdbcTemplate.queryForObject(query, String.class);
		LOG.debug("nonNullCheck=" + nonNullCheck);
		try {
			if (nonNullCheck.equalsIgnoreCase("Y")) {
				String q = "select displayName from listDataDefinition where nonNull='Y' and idData=" + idData;
				RowMapper<ListDataDefinition> rowMapper = (rs, i) -> {
					ListDataDefinition ldd = new ListDataDefinition();
					ldd.setDisplayName(rs.getString("displayName"));
					LOG.debug("rs.getString(displayName)=" + rs.getString("displayName"));
					return ldd;
				};
				List<ListDataDefinition> displayNames = jdbcTemplate.query(q, rowMapper);
				return displayNames;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List getDisplayNamesForStringTest(Long idApp, Long idData) {
		String query = "select stringStatCheck from listApplications where idApp=" + idApp;
		String stringStatCheck = jdbcTemplate.queryForObject(query, String.class);
		LOG.debug("stringStatCheck=" + stringStatCheck);
		try {
			if (stringStatCheck.equalsIgnoreCase("Y")) {
				String q = "select displayName from listDataDefinition where stringStat='Y' and idData=" + idData;
				RowMapper<ListDataDefinition> rowMapper = (rs, i) -> {
					ListDataDefinition ldd = new ListDataDefinition();
					ldd.setDisplayName(rs.getString("displayName"));
					LOG.debug("rs.getString(displayName)=" + rs.getString("displayName"));
					return ldd;
				};
				List<ListDataDefinition> displayNames = jdbcTemplate.query(q, rowMapper);
				return displayNames;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List getDisplayNamesForNumericalTest(Long idApp, Long idData) {
		try {
			String query = "select numericalStatCheck from listApplications where idApp=" + idApp;
			String numericalStatCheck = jdbcTemplate.queryForObject(query, String.class);
			LOG.debug("numericalStatCheck=" + numericalStatCheck);

			if (numericalStatCheck.equalsIgnoreCase("Y")) {
				String q = "select displayName from listDataDefinition where numericalStat='Y' and idData=" + idData;
				RowMapper<ListDataDefinition> rowMapper = (rs, i) -> {
					ListDataDefinition ldd = new ListDataDefinition();
					ldd.setDisplayName(rs.getString("displayName"));
					LOG.debug("rs.getString(displayName)=" + rs.getString("displayName"));
					return ldd;
				};
				List<ListDataDefinition> displayNames = jdbcTemplate.query(q, rowMapper);
				return displayNames;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List readColumn_SummaryTable(String tableName, List<ListDataDefinition> displayNamesForNullTest) {
		try {
			DecimalFormat numberFormat = new DecimalFormat("#.00");
			RowMapper<DATA_QUALITY_Column_Summary> rowMapper = (rs, i) -> {

				DATA_QUALITY_Column_Summary data_Quality_Column_Summary = new DATA_QUALITY_Column_Summary();
				data_Quality_Column_Summary.setDate(rs.getString("Date"));
				data_Quality_Column_Summary.setRun(rs.getInt("Run"));
				data_Quality_Column_Summary.setColName(rs.getString("ColName"));
				data_Quality_Column_Summary.setCount(rs.getInt("Count"));
				data_Quality_Column_Summary.setMin(Double.parseDouble(numberFormat.format(rs.getDouble("Min"))));
				data_Quality_Column_Summary.setMax(Double.parseDouble(numberFormat.format(rs.getDouble("Max"))));
				data_Quality_Column_Summary.setCardinality(rs.getInt("Cardinality"));
				data_Quality_Column_Summary
						.setStd_Dev(Double.parseDouble(numberFormat.format(rs.getDouble("Std_Dev"))));
				data_Quality_Column_Summary.setMean(Double.parseDouble(numberFormat.format(rs.getDouble("Mean"))));
				data_Quality_Column_Summary.setNull_Value(rs.getInt("Null_Value"));
				data_Quality_Column_Summary.setRecord_Count(rs.getInt("Record_Count"));

				if (rs.getString("Null_Percentage") != null) {
					data_Quality_Column_Summary.setNull_Percentage(
							Double.parseDouble(numberFormat.format(rs.getDouble("Null_Percentage"))));
				} else {
					data_Quality_Column_Summary.setNull_Percentage(null);
				}

				data_Quality_Column_Summary.setNull_Threshold(rs.getDouble("Null_Threshold"));
				data_Quality_Column_Summary.setStatus(rs.getString("Status"));

				if (rs.getString("StringCardinalityAvg") != null) {
					data_Quality_Column_Summary.setStringCardinalityAvg(
							Double.parseDouble(numberFormat.format(rs.getDouble("StringCardinalityAvg"))));
				} else {
					data_Quality_Column_Summary.setStringCardinalityAvg(null);
				}
				if (rs.getString("StringCardinalityStdDev") != null) {
					data_Quality_Column_Summary.setStringCardinalityStdDev(
							Double.parseDouble(numberFormat.format(rs.getDouble("StringCardinalityStdDev"))));
				} else {
					data_Quality_Column_Summary.setStringCardinalityStdDev(null);
				}
				if (rs.getString("StrCardinalityDeviation") != null) {
					data_Quality_Column_Summary.setStrCardinalityDeviation(
							Double.parseDouble(numberFormat.format(rs.getDouble("StrCardinalityDeviation"))));
				} else {
					data_Quality_Column_Summary.setStrCardinalityDeviation(null);
				}
				if (rs.getString("String_Threshold") != null) {
					data_Quality_Column_Summary.setString_Threshold(
							Double.parseDouble(numberFormat.format(rs.getDouble("String_Threshold"))));
				} else {
					data_Quality_Column_Summary.setString_Threshold(null);
				}
				data_Quality_Column_Summary.setString_Status(rs.getString("String_Status"));

				if (rs.getString("NumMeanAvg") != null) {
					data_Quality_Column_Summary
							.setNumMeanAvg(Double.parseDouble(numberFormat.format(rs.getDouble("NumMeanAvg"))));
				} else {
					data_Quality_Column_Summary.setNumMeanAvg(null);
				}
				if (rs.getString("NumMeanStdDev") != null) {
					data_Quality_Column_Summary
							.setNumMeanStdDev(Double.parseDouble(numberFormat.format(rs.getDouble("NumMeanStdDev"))));
				} else {
					data_Quality_Column_Summary.setNumMeanStdDev(null);
				}
				if (rs.getString("NumMeanDeviation") != null) {
					data_Quality_Column_Summary.setNumMeanDeviation(
							Double.parseDouble(numberFormat.format(rs.getDouble("NumMeanDeviation"))));
				} else {
					data_Quality_Column_Summary.setNumMeanDeviation(null);
				}
				if (rs.getString("NumMeanThreshold") != null) {
					data_Quality_Column_Summary.setNumMeanThreshold(
							Double.parseDouble(numberFormat.format(rs.getDouble("NumMeanThreshold"))));
				} else {
					data_Quality_Column_Summary.setNumMeanThreshold(null);
				}
				data_Quality_Column_Summary.setNumMeanStatus(rs.getString("NumMeanStatus"));

				if (rs.getString("NumSDAvg") != null) {
					data_Quality_Column_Summary
							.setNumSDAvg(Double.parseDouble(numberFormat.format(rs.getDouble("NumSDAvg"))));
				} else {
					data_Quality_Column_Summary.setNumSDAvg(null);
				}

				if (rs.getString("NumSDStdDev") != null) {
					data_Quality_Column_Summary
							.setNumSDStdDev(Double.parseDouble(numberFormat.format(rs.getDouble("NumSDStdDev"))));
				} else {
					data_Quality_Column_Summary.setNumSDStdDev(null);
				}

				if (rs.getString("NumSDDeviation") != null) {
					data_Quality_Column_Summary
							.setNumSDDeviation(Double.parseDouble(numberFormat.format(rs.getDouble("NumSDDeviation"))));
				} else {
					data_Quality_Column_Summary.setNumSDDeviation(null);
				}
				if (rs.getString("NumSDThreshold") != null) {
					data_Quality_Column_Summary
							.setNumSDThreshold(Double.parseDouble(numberFormat.format(rs.getDouble("NumSDThreshold"))));
				} else {
					data_Quality_Column_Summary.setNumSDThreshold(null);
				}
				data_Quality_Column_Summary.setNumSDStatus(rs.getString("NumSDStatus"));
				data_Quality_Column_Summary.setDayOfYear(rs.getLong("dayOfYear"));
				data_Quality_Column_Summary.setMonth(rs.getString("month"));
				data_Quality_Column_Summary.setDayOfMonth(rs.getLong("dayOfMonth"));
				data_Quality_Column_Summary.setDayOfWeek(rs.getString("dayOfWeek"));
				data_Quality_Column_Summary.setHourOfDay(rs.getLong("hourOfDay"));
				data_Quality_Column_Summary.setDataDriftStatus(rs.getString("dataDriftStatus"));
				data_Quality_Column_Summary.setOutOfNormStatStatus(rs.getString("outOfNormStatStatus"));

				data_Quality_Column_Summary.setdGroupVal(rs.getString("dGroupVal"));
				data_Quality_Column_Summary.setdGroupCol(rs.getString("dGroupCol"));

				if (rs.getString("sumOfNumStat") != null) {
					data_Quality_Column_Summary.setSumOfNumStat(numberFormat.format(rs.getDouble("sumOfNumStat")));
				} else {
					data_Quality_Column_Summary.setSumOfNumStat(null);
				}
				return data_Quality_Column_Summary;
			};
			List totaldataList = new ArrayList();

			for (ListDataDefinition ldd : displayNamesForNullTest) {
				String columnSummaryQuery = "select * from " + tableName + " where ColName = ?";
				List<DATA_QUALITY_Column_Summary> colunSummaryData = jdbcTemplate1.query(columnSummaryQuery, rowMapper,
						ldd.getDisplayName());
				totaldataList.add(colunSummaryData);
			}
			return totaldataList;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Column_Summary> readColumn_SummaryTable(String tableName) {
		try {
			String columnSummaryQuery = "select * from " + tableName;
			RowMapper<DATA_QUALITY_Column_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Column_Summary data_Quality_Column_Summary = new DATA_QUALITY_Column_Summary();
				data_Quality_Column_Summary.setDate(rs.getString("Date"));
				data_Quality_Column_Summary.setRun(rs.getInt("Run"));
				data_Quality_Column_Summary.setColName(rs.getString("ColName"));
				data_Quality_Column_Summary.setCount(rs.getInt("Count"));
				data_Quality_Column_Summary.setMin(rs.getDouble("Min"));
				data_Quality_Column_Summary.setMax(rs.getDouble("Max"));
				data_Quality_Column_Summary.setCardinality(rs.getInt("Cardinality"));
				data_Quality_Column_Summary.setStd_Dev(rs.getDouble("Std_Dev"));
				data_Quality_Column_Summary.setMean(rs.getDouble("Mean"));
				data_Quality_Column_Summary.setNull_Value(rs.getInt("Null_Value"));
				data_Quality_Column_Summary.setRecord_Count(rs.getInt("Record_Count"));
				data_Quality_Column_Summary.setNull_Percentage(rs.getDouble("Null_Percentage"));
				data_Quality_Column_Summary.setNull_Threshold(rs.getDouble("Null_Threshold"));
				data_Quality_Column_Summary.setStatus(rs.getString("Status"));
				data_Quality_Column_Summary.setStringCardinalityAvg(rs.getDouble("StringCardinalityAvg"));
				data_Quality_Column_Summary.setStringCardinalityStdDev(rs.getDouble("StringCardinalityStdDev"));
				data_Quality_Column_Summary.setStrCardinalityDeviation(rs.getDouble("StrCardinalityDeviation"));
				data_Quality_Column_Summary.setString_Threshold(rs.getDouble("String_Threshold"));
				data_Quality_Column_Summary.setString_Status(rs.getString("String_Status"));
				data_Quality_Column_Summary.setNumMeanAvg(rs.getDouble("NumMeanAvg"));
				data_Quality_Column_Summary.setNumMeanStdDev(rs.getDouble("NumMeanStdDev"));
				data_Quality_Column_Summary.setNumMeanDeviation(rs.getDouble("NumMeanDeviation"));
				data_Quality_Column_Summary.setNumMeanThreshold(rs.getDouble("NumMeanThreshold"));
				data_Quality_Column_Summary.setNumMeanStatus(rs.getString("NumMeanStatus"));
				data_Quality_Column_Summary.setNumSDAvg(rs.getDouble("NumSDAvg"));
				data_Quality_Column_Summary.setNumSDStdDev(rs.getDouble("NumSDStdDev"));
				data_Quality_Column_Summary.setNumSDDeviation(rs.getDouble("NumSDDeviation"));
				data_Quality_Column_Summary.setNumSDThreshold(rs.getDouble("NumSDThreshold"));
				data_Quality_Column_Summary.setNumSDStatus(rs.getString("NumSDStatus"));
				return data_Quality_Column_Summary;

			};

			List<DATA_QUALITY_Column_Summary> colunSummaryData = jdbcTemplate1.query(columnSummaryQuery, rowMapper);
			return colunSummaryData;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public String dynamiccolumnNameforTransactionset(long appId) {
		try {
			String sql = "select * from DATA_QUALITY_Transactionset_sum_A1 where idApp=?";
			SqlRowSetMetaData metaData = jdbcTemplate1.queryForRowSet(sql, appId).getMetaData();
			String columnName = metaData.getColumnName(5);
			return columnName;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "";
	}

	public List readTransactionset_sum_A1Table(long appId) {
		try {
			String sql = "select * from DATA_QUALITY_Transactionset_sum_A1 where idApp=?";
			SqlRowSetMetaData metaData = jdbcTemplate1.queryForRowSet(sql, appId).getMetaData();
			String columnName = metaData.getColumnName(4);

			String columnSummaryQuery = "select * from DATA_QUALITY_Transactionset_sum_A1 where idApp=?";
			RowMapper<DATA_QUALITY_Transactionset_sum_A1> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transactionset_sum_A1 dqts = new DATA_QUALITY_Transactionset_sum_A1();

				dqts.setId(rs.getLong("Id"));
				dqts.setDate(rs.getString("Date"));
				dqts.setRun(rs.getInt("Run"));
				dqts.setRecordCount(rs.getInt("RecordCount"));
				dqts.setSumOf_sal(rs.getDouble(columnName));
				dqts.setDuplicateDataSet(rs.getString("DuplicateDataSet"));
				dqts.setRc_Std_Dev(rs.getDouble("RC_Std_Dev"));
				dqts.setRc_Mean(rs.getDouble("RC_Mean"));
				dqts.setRc_Deviation(rs.getDouble("RC_Deviation"));
				dqts.setRc_Std_Dev_Status(rs.getString("RC_Std_Dev_Status"));
				dqts.setRc_Mean_Moving_Avg(rs.getString("RC_Mean_Moving_Avg"));
				dqts.setRc_Mean_Moving_Avg_Status(rs.getString("RC_Mean_Moving_Avg_Status"));
				dqts.setM_Std_Dev(rs.getDouble("M_Std_Dev"));
				dqts.setM_Mean(rs.getDouble("M_Mean"));
				dqts.setM_Deviation(rs.getDouble("M_Deviation"));
				dqts.setM_Std_Dev_Status(rs.getString("M_Std_Dev_Status"));
				dqts.setM_Mean_Moving_Avg(rs.getString("M_Mean_Moving_Avg"));
				dqts.setM_Mean_Moving_Avg_Status(rs.getString("M_Mean_Moving_Avg_Status"));
				return dqts;

			};

			List<DATA_QUALITY_Transactionset_sum_A1> colunSummaryData = jdbcTemplate1.query(columnSummaryQuery,
					rowMapper, appId);
			return colunSummaryData;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	/*
	 * public String[] columnNames(long appId){ String sql=
	 * "select * from DATA_QUALITY_"+appId+"_Transaction_Detail"; String[]
	 * columnNames =
	 * jdbcTemplate1.queryForRowSet(sql).getMetaData().getColumnNames(); for(String
	 * column:columnNames) { LOG.debug("column:"+column); } return
	 * columnNames;
	 *
	 *
	 * }
	 */

	public SqlRowSet readTransaction_Detail_IdentityTable(String tableName, long idApp) {
		try {
			String columnSummaryQuery = "select * from " + tableName + " where idApp=" + idApp + " limit 200";
			return jdbcTemplate1.queryForRowSet(columnSummaryQuery);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;

	}

	public SqlRowSet data_Quality__record_anomaly(String tableName, long idApp) {
		try {
			String columnSummaryQuery = "select * from " + tableName + " where idApp=" + idApp + " limit 200";
			LOG.debug(jdbcTemplate1.queryForRowSet(columnSummaryQuery));
			return jdbcTemplate1.queryForRowSet(columnSummaryQuery);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getRulesTableData(String tableName, long idApp) {
		try {
			String getRulesTableData = "select * from " + tableName + " where idApp=" + idApp + " limit 200";
			LOG.info("Rules");
			return jdbcTemplate1.queryForRowSet(getRulesTableData);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getFrequencyUpdateDateTableData(String tableName, long idApp) {
		try {
			String frequencyUpdateDateTableData = "select Id,Date,Run,dayOfYear,month,dayOfMonth,dayOfWeek,hourOfDay,RecordCount,"
					+ "dGroupVal,dgroupCol,ROUND(RC_Std_Dev,2) AS RC_Std_Dev,ROUND(RC_Mean,2) AS RC_Mean,ROUND(dGroupDeviation,2) AS dGroupDeviation,dGroupRcStatus from "
					+ tableName + " where idApp=" + idApp + " and Date=(select Max(Date) from " + tableName
					+ " where idApp=" + idApp + ") and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select Max(Date) from " + tableName + " where idApp=" + idApp + ")) limit 200";
			return jdbcTemplate1.queryForRowSet(frequencyUpdateDateTableData);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getDataDriftTable(String tableName, long idApp) {
		try {
			String sql = "select * from " + tableName + " where idApp=" + idApp + " order by Date limit 200";
			return jdbcTemplate1.queryForRowSet(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet readTransaction_DetailTable(String tableName, Long idApp) {
		try {
			String columnSummaryQuery = "select * from " + tableName;
			if (idApp != null) {
				columnSummaryQuery = columnSummaryQuery + " where idApp=" + idApp;
			}
			columnSummaryQuery = columnSummaryQuery + " limit 200";

			return jdbcTemplate1.queryForRowSet(columnSummaryQuery);
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error("Error reading table:" + tableName + ", error message:" + e.getMessage());
		}
		return null;
	}

	public List readTransaction_SummaryTable(String tableName, long idApp) {
		try {
			DecimalFormat numberFormat = new DecimalFormat("#.00");
			String columnSummaryQuery = "select * from " + tableName + " where idApp=" + idApp + " limit 200";
			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {

				DATA_QUALITY_Transaction_Summary dqt = new DATA_QUALITY_Transaction_Summary();
				dqt.setDate(rs.getString("Date"));
				dqt.setRun(rs.getInt("Run"));
				dqt.setDuplicate(rs.getInt("Duplicate"));
				dqt.setType(rs.getString("Type"));

				dqt.setTotalCount(rs.getInt("TotalCount"));

				if (rs.getString("Percentage") != null) {
					dqt.setPercentage(Double.parseDouble(numberFormat.format(rs.getDouble("Percentage"))));
				} else {
					dqt.setPercentage(null);
				}

				// dqt.setThreshold(rs.getDouble("Threshold"));
				dqt.setStatus(rs.getString("Status"));
				return dqt;

			};
			List<DATA_QUALITY_Transaction_Summary> colunSummaryData = jdbcTemplate1.query(columnSummaryQuery,
					rowMapper);
			return colunSummaryData;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error("Error reading table:" + tableName + ", error message:" + e.getMessage());
		}
		return null;
	}

	public List getRecordCountAnomalyGraphValues(String tableName) {
		List<DATA_QUALITY_Transactionset_sum_A1> recordCountAnomalyGraphValues = new ArrayList<>();
		try {
			// String Query = "Select Date, max(Run) as Run ,RecordCount from "
			// + tableName + " group by Date order by Date asc";
			String Query = "SELECT Date, MAX( Run ) AS Run, RecordCount FROM " + tableName
					+ " GROUP BY Date ORDER BY DATE ASC ";
			RowMapper<DATA_QUALITY_Transactionset_sum_A1> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transactionset_sum_A1 dqts = new DATA_QUALITY_Transactionset_sum_A1();

				dqts.setDate(rs.getString("Date"));
				dqts.setRecordCount(rs.getInt("RecordCount"));
				return dqts;
			};
			recordCountAnomalyGraphValues = jdbcTemplate1.query(Query, rowMapper);
			if (recordCountAnomalyGraphValues.size() == 0) {
				DATA_QUALITY_Transactionset_sum_A1 dq = new DATA_QUALITY_Transactionset_sum_A1();
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String strDate = formatter.format(date);
				LOG.debug(strDate);
				dq.setDate(strDate);
				dq.setRecordCount(0);
				recordCountAnomalyGraphValues.add(dq);
			}
			return recordCountAnomalyGraphValues;
		} catch (Exception e) {
			DATA_QUALITY_Transactionset_sum_A1 dq = new DATA_QUALITY_Transactionset_sum_A1();
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String strDate = formatter.format(date);
			LOG.debug(strDate);
			dq.setDate(strDate);
			dq.setRecordCount(0);
			recordCountAnomalyGraphValues.add(dq);
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return recordCountAnomalyGraphValues;
	}

	public List<DATA_QUALITY_Transaction_Summary> getNullCountGraph(String tableName) {
		try {
			String Query = "select Date, max(Run) as Run from " + tableName + " group by Date ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transaction_Summary dqts = new DATA_QUALITY_Transaction_Summary();
				// LOG.debug("Date="+rs.getString("Date"));
				// LOG.debug("count="+rs.getInt("Run"));
				dqts.setDate(rs.getString("Date"));
				dqts.setTotalCount(rs.getInt("Run"));
				return dqts;
			};
			List<DATA_QUALITY_Transaction_Summary> nullCountGraphValues = jdbcTemplate1.query(Query, rowMapper);
			List<DATA_QUALITY_Transaction_Summary> fullNullCountGraph = getFullNullCountGraph(nullCountGraphValues,
					tableName);
			return nullCountGraphValues;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Transaction_Summary> getFullNullCountGraph(
			List<DATA_QUALITY_Transaction_Summary> nullCountGraphValues, String tableName) {
		try {
			List<DATA_QUALITY_Transaction_Summary> al = new ArrayList<DATA_QUALITY_Transaction_Summary>();

			for (DATA_QUALITY_Transaction_Summary dqts : nullCountGraphValues) {
				// LOG.debug("Date dqts="+dqts.getDate());
				// LOG.debug("count dqts="+dqts.getTotalCount());
				SqlRowSet rs = jdbcTemplate1.queryForRowSet(
						"select Date,sum(Null_Value) as Run  from  " + tableName + " where Date=? and Run=?",
						dqts.getDate(), dqts.getTotalCount());
				while (rs.next()) {
					// LOG.debug("Date="+rs.getString("Date"));
					// LOG.debug("count="+rs.getInt("Run"));
					dqts.setDate(rs.getString("Date"));
					dqts.setTotalCount(rs.getInt("Run"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Transaction_Summary> getAllfieldsGraph(String tableName, long idApp) {
		try {
			String Query = "select Date, max(Run) as Duplicate from " + tableName + " where idApp=" + idApp
					+ " group by Date ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transaction_Summary dqts = new DATA_QUALITY_Transaction_Summary();

				dqts.setDate(rs.getString("Date"));
				dqts.setDuplicate(rs.getInt("Duplicate"));
				return dqts;
			};
			List<DATA_QUALITY_Transaction_Summary> allfieldsGraph = jdbcTemplate1.query(Query, rowMapper);
			List<DATA_QUALITY_Transaction_Summary> fullAllfieldsGraph = getFullAllfieldsGraph(allfieldsGraph, tableName,
					idApp);
			return fullAllfieldsGraph;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Transaction_Summary> getFullAllfieldsGraph(
			List<DATA_QUALITY_Transaction_Summary> allfieldsGraph, String tableName, long idApp) {
		try {
			List<DATA_QUALITY_Transaction_Summary> al = new ArrayList<DATA_QUALITY_Transaction_Summary>();

			for (DATA_QUALITY_Transaction_Summary dqts : allfieldsGraph) {
				SqlRowSet rs = jdbcTemplate1.queryForRowSet("select Date,Duplicate from  " + tableName + " where idApp="
						+ idApp + " and Type='all' and Date=? and Run=?", dqts.getDate(), dqts.getDuplicate());
				while (rs.next()) {
					dqts.setDate(rs.getString("Date"));
					dqts.setDuplicate(rs.getInt("Duplicate"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List getidentityfieldsGraph(String tableName, long idApp) {
		try {
			String Query = "select Date, max(Run) as Duplicate from " + tableName + " where idApp=" + idApp
					+ " group by Date ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transaction_Summary dqts = new DATA_QUALITY_Transaction_Summary();

				dqts.setDate(rs.getString("Date"));
				dqts.setDuplicate(rs.getInt("Duplicate"));
				return dqts;
			};
			List<DATA_QUALITY_Transaction_Summary> identityfieldsGraph = jdbcTemplate1.query(Query, rowMapper);
			List<DATA_QUALITY_Transaction_Summary> fullIdentityfieldsGraph = getFullIdentityfieldsGraph(
					identityfieldsGraph, tableName, idApp);
			return fullIdentityfieldsGraph;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Transaction_Summary> getFullIdentityfieldsGraph(
			List<DATA_QUALITY_Transaction_Summary> allfieldsGraph, String tableName, long idApp) {
		try {
			List<DATA_QUALITY_Transaction_Summary> al = new ArrayList<DATA_QUALITY_Transaction_Summary>();

			for (DATA_QUALITY_Transaction_Summary dqts : allfieldsGraph) {
				SqlRowSet rs = jdbcTemplate1.queryForRowSet("select Date,Duplicate from  " + tableName + " where idApp="
						+ idApp + " and Type='identity' and Date=? and Run=?", dqts.getDate(), dqts.getDuplicate());
				while (rs.next()) {
					// LOG.debug("Date=" + rs.getString("Date"));
					// LOG.debug("Duplicate=" +
					// rs.getInt("Duplicate"));
					dqts.setDate(rs.getString("Date"));
					dqts.setDuplicate(rs.getInt("Duplicate"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List getStringFieldStatsGraph(String tableName) {
		try {
			String Query = "select Date, max(Run) as count from " + tableName + "  group by Date  ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Column_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Column_Summary dqts = new DATA_QUALITY_Column_Summary();

				dqts.setDate(rs.getString("Date"));
				dqts.setCount(rs.getInt("count"));
				return dqts;
			};
			List<DATA_QUALITY_Column_Summary> stringFieldStatsGraph = jdbcTemplate1.query(Query, rowMapper);
			List<DATA_QUALITY_Column_Summary> fullStringFieldStatsGraph = getFullStringFieldStatsGraph(
					stringFieldStatsGraph, tableName);
			return fullStringFieldStatsGraph;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Column_Summary> getFullStringFieldStatsGraph(
			List<DATA_QUALITY_Column_Summary> stringFieldStatsGraph, String tableName) {
		try {
			List<DATA_QUALITY_Column_Summary> al = new ArrayList<DATA_QUALITY_Column_Summary>();

			for (DATA_QUALITY_Column_Summary dqts : stringFieldStatsGraph) {
				// LOG.debug("Date dqts="+dqts.getDate());
				// LOG.debug("count dqts="+dqts.getTotalCount());
				SqlRowSet rs = jdbcTemplate1.queryForRowSet(
						"select  Date,count(*) as count from " + tableName
								+ " where Date=? and Run=? and String_Status  is not null ",
						dqts.getDate(), dqts.getCount());
				while (rs.next()) {
					// LOG.debug("Date="+rs.getString("Date"));
					// LOG.debug("count="+rs.getInt("count"));
					dqts.setDate(rs.getString("Date"));
					dqts.setCount(rs.getInt("count"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Record_Anomaly> getRecordAnomalyGraph(String tableName, long idApp) {
		List<DATA_QUALITY_Record_Anomaly> fullRecordAnomalyGraph = new ArrayList<>();
		try {
			String Query = "select Date, max(Run) as count from  " + tableName + " where idApp=" + idApp
					+ " group by Date ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Record_Anomaly> rowMapper = (rs, i) -> {
				DATA_QUALITY_Record_Anomaly dqts = new DATA_QUALITY_Record_Anomaly();

				dqts.setDate(rs.getString("Date"));
				dqts.setCount(rs.getLong("count"));
				return dqts;
			};
			List<DATA_QUALITY_Record_Anomaly> recordAnomalyGraph = jdbcTemplate1.query(Query, rowMapper);
			fullRecordAnomalyGraph = getFullRecordAnomalyGraph(recordAnomalyGraph, tableName, idApp);
			return fullRecordAnomalyGraph;
		} catch (Exception e) {
			/*
			 * DATA_QUALITY_Record_Anomaly dq=new DATA_QUALITY_Record_Anomaly();
			 * dq.setDate("02/04/17"); dq.setCount(0l); fullRecordAnomalyGraph.add(dq);
			 */
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return fullRecordAnomalyGraph;
	}

	public List<DATA_QUALITY_Record_Anomaly> getFullRecordAnomalyGraph(
			List<DATA_QUALITY_Record_Anomaly> recordAnomalyGraph, String tableName, long idApp) {
		try {
			List<DATA_QUALITY_Record_Anomaly> al = new ArrayList<DATA_QUALITY_Record_Anomaly>();

			for (DATA_QUALITY_Record_Anomaly dqts : recordAnomalyGraph) {
				SqlRowSet rs = jdbcTemplate1.queryForRowSet(
						"select Date, count(*) as count from " + tableName + " where idApp=? and Date=? and Run=?",
						idApp, dqts.getDate(), dqts.getCount());
				while (rs.next()) {
					dqts.setDate(rs.getString("Date"));
					dqts.setCount(rs.getLong("count"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	public List<DATA_QUALITY_Column_Summary> getNumericalFieldStatsGraph(String tableName) {
		try {
			String Query = "select Date, max(Run) as count from " + tableName + "  group by Date ORDER BY DATE ASC";

			RowMapper<DATA_QUALITY_Column_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Column_Summary dqts = new DATA_QUALITY_Column_Summary();
				// LOG.debug("Date=" + rs.getString("Date"));
				dqts.setDate(rs.getString("Date"));
				dqts.setCount(rs.getInt("count"));
				return dqts;
			};
			List<DATA_QUALITY_Column_Summary> numericalFieldStatsGraph = jdbcTemplate1.query(Query, rowMapper);
			List<DATA_QUALITY_Column_Summary> fullNumericalFieldStatsGraph = getFullNumericalFieldStatsGraph(
					numericalFieldStatsGraph, tableName);
			return fullNumericalFieldStatsGraph;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public List<DATA_QUALITY_Column_Summary> getFullNumericalFieldStatsGraph(
			List<DATA_QUALITY_Column_Summary> numericalFieldStatsGraph, String tableName) {
		try {
			List<DATA_QUALITY_Column_Summary> al = new ArrayList<DATA_QUALITY_Column_Summary>();

			for (DATA_QUALITY_Column_Summary dqts : numericalFieldStatsGraph) {
				// LOG.debug("Date dqts="+dqts.getDate());
				// LOG.debug("count dqts="+dqts.getTotalCount());
				SqlRowSet rs = jdbcTemplate1.queryForRowSet(
						"select  Date,count(*) as count from " + tableName
								+ " where Date=? and Run=? and NumMeanStatus  is not null",
						dqts.getDate(), dqts.getCount());
				while (rs.next()) {
					// LOG.debug("Date=" + rs.getString("Date"));
					// LOG.debug("count=" + rs.getInt("count"));
					dqts.setDate(rs.getString("Date"));
					dqts.setCount(rs.getInt("count"));
				}
				al.add(dqts);
			}
			return al;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	public List<DATA_QUALITY_Transactionset_sum_A1> getfileNameandcolumnOrderValidationStatus(String tableName,
			long idApp) {
		List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderValidationStatus = null;
		try {
			String query = "select fileNameValidationStatus,columnOrderValidationStatus from " + tableName
					+ " where Date=(select max(date) from " + tableName + " where idApp=" + idApp
					+ ") and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select max(date) from " + tableName + " where idApp=" + idApp + ")) and idApp="
					+ idApp + " limit 1";
			RowMapper<DATA_QUALITY_Transactionset_sum_A1> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transactionset_sum_A1 dqts = new DATA_QUALITY_Transactionset_sum_A1();
				dqts.setFileNameValidationStatus(rs.getString("fileNameValidationStatus"));
				dqts.setColumnOrderValidationStatus(rs.getString("columnOrderValidationStatus"));
				return dqts;
			};
			fileNameandcolumnOrderValidationStatus = jdbcTemplate1.query(query, rowMapper);
			LOG.debug(
					"fileNameandcolumnOrderValidationStatus.size()=" + fileNameandcolumnOrderValidationStatus.size());
			if (fileNameandcolumnOrderValidationStatus.size() == 0) {
				DATA_QUALITY_Transactionset_sum_A1 dq = new DATA_QUALITY_Transactionset_sum_A1();
				dq.setColumnOrderValidationStatus("");
				dq.setFileNameValidationStatus("");
				fileNameandcolumnOrderValidationStatus.add(dq);
			}
			return fileNameandcolumnOrderValidationStatus;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return fileNameandcolumnOrderValidationStatus;
	}

	public List<DATA_QUALITY_Transaction_Summary> getAllfieldsandIdentityfields(String tableName, long idApp) {
		List<DATA_QUALITY_Transaction_Summary> al = new ArrayList<DATA_QUALITY_Transaction_Summary>();

		try {
			String Query = "Select Status from  " + tableName + " where idApp=" + idApp
					+ " and Date= (SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='all') and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Type='all' and Date= (select max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='all')) and Type='all'";

			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transaction_Summary dqts = new DATA_QUALITY_Transaction_Summary();
				dqts.setStatus(rs.getString("Status"));
				return dqts;
			};
			List<DATA_QUALITY_Transaction_Summary> allfieldsandIdentityfields = jdbcTemplate1.query(Query, rowMapper);
			if (allfieldsandIdentityfields.size() != 0) {
				al.add(allfieldsandIdentityfields.get(0));
			} else {
				DATA_QUALITY_Transaction_Summary dts = new DATA_QUALITY_Transaction_Summary();
				dts.setStatus("not computed");
				al.add(dts);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		try {
			String Query = "Select Status from  " + tableName + " where idApp=" + idApp
					+ " and Date= (SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity') and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity' and Date= (select max(date) from " + tableName + " where idApp=" + idApp
					+ " and Type='identity')) and Type='identity'";

			RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
				DATA_QUALITY_Transaction_Summary dqts = new DATA_QUALITY_Transaction_Summary();
				dqts.setStatus(rs.getString("Status"));
				return dqts;
			};
			List<DATA_QUALITY_Transaction_Summary> identityfieldsandIdentityfields = jdbcTemplate1.query(Query,
					rowMapper);

			if (identityfieldsandIdentityfields.size() != 0) {
				al.add(identityfieldsandIdentityfields.get(0));
			} else {
				DATA_QUALITY_Transaction_Summary dts = new DATA_QUALITY_Transaction_Summary();
				dts.setStatus("not computed");
				al.add(dts);
			}

			return al;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;
	}

	@Override
	public String getRecordCountStatus(String tableName, long idApp) {
		try {
			// String sql = "SELECT RC_Std_Dev_Status FROM " + tableName + "
			// ORDER BY ID DESC LIMIT 1";
			String sql = "SELECT RC_Std_Dev_Status FROM " + tableName + " WHERE idApp=" + idApp
					+ " and Date= (SELECT max(date) from " + tableName + " where idApp=" + idApp
					+ ") and Run=(select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date= (SELECT max(date) from " + tableName + " where idApp=" + idApp + "))";
			String recordCountStatus = jdbcTemplate1.queryForObject(sql, String.class);
			LOG.debug("recordCountStatus=" + recordCountStatus);
			return recordCountStatus;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return "";
	}

	public String checkGroupEqualityInListApplicationsTable(long idApp) {
		try {
			String sql = "select groupEquality from listApplications where idApp=" + idApp;
			String groupEquality = jdbcTemplate.queryForObject(sql, String.class);
			return groupEquality;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
			return "";
		}
	}

	public String getDashBoardStatusForGroupEquality(long idApp, String tableName) {
		try {
			String groupEquality = checkGroupEqualityInListApplicationsTable(idApp);
			if (groupEquality.equalsIgnoreCase("Y")) {
				String sql = "select groupEqualityStatus from " + tableName + " where run=(select max(run) from "
						+ tableName + ")" + "and date=(SELECT max(Date) FROM  " + tableName + ") LIMIT 1";
				String groupEqualityStatus = jdbcTemplate1.queryForObject(sql, String.class);
				return groupEqualityStatus;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return null;

	}

	public String getMissingDatesFromTransactionset_sum_A1(long idApp) {
		try {
			String tableName = "DATA_QUALITY_Transactionset_sum_A1";
			String sql = "select missingDates from " + tableName + " where id=(select max(id) from " + tableName
					+ " where idApp=?) and idApp=? LIMIT 1";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, idApp);
			if (queryForRowSet.next()) {
				LOG.debug("queryForRowSet.next()");
				return queryForRowSet.getString(1).replaceAll(",", ", ");
			} else {
				String tableName1 = "DATA_QUALITY_Transactionset_sum_dgroup";
				String sql1 = "select missingDates from " + tableName1 + " where Date=(SELECT max(Date) FROM  "
						+ tableName1 + " where idApp=?) and run=(select max(run) from " + tableName1
						+ " where idApp=? and date=(SELECT max(Date) FROM  " + tableName1
						+ " where idApp=?)) and idApp=? LIMIT 1";
				SqlRowSet queryForRowSet1 = jdbcTemplate1.queryForRowSet(sql1, idApp, idApp, idApp, idApp);
				if (queryForRowSet1.next()) {
					return queryForRowSet1.getString(1).replaceAll(",", ", ");
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());

		}
		return "No Missing Dates";
	}

	public String getStatusFromRecordAnomalyTable(Long idApp) {
		try {
			String tableName = "DATA_QUALITY_Record_Anomaly";
			String sql = "select status FROM " + tableName + " where idApp=" + idApp
					+ " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + ")"
					+ " and Run=(SELECT max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + ")) LIMIT 1";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				return queryForRowSet.getString(1);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return "passed";
	}

	public String getDashboardStatusForRCA(Long idApp, String recordCountAnomaly) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			SqlRowSet queryForRowSet;
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				String RCATableName = "DATA_QUALITY_Transactionset_sum_A1";
				String sql = "SELECT RC_Std_Dev_Status, DATE FROM " + RCATableName
						+ " WHERE idApp=? and RC_Deviation IS NOT NULL ORDER BY DATE desc limit 3";
				LOG.debug("sql:" + sql);
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);
			} else {
				String RCATableName = "DATA_QUALITY_Transactionset_sum_dgroup";
				String sql = "SELECT dGroupRcStatus, DATE FROM " + RCATableName
						+ " WHERE idApp=? and dGroupDeviation IS NOT NULL ORDER BY DATE desc limit 3";
				LOG.debug("sql:" + sql);
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);
			}
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString(1) != null) {
					if (queryForRowSet.getString(1).equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusForNullCount(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Column_Summary";
			String sql = "SELECT STATUS , DATE FROM  " + colSumm + " WHERE idApp=" + idApp
					+ " and STATUS IS NOT NULL ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("STATUS") != null) {
					if (queryForRowSet.getString("STATUS").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusFornumericalFieldStatsStatus(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Column_Summary";
			String sql = "SELECT NumMeanStatus , DATE FROM  " + colSumm + " WHERE idApp=" + idApp
					+ " and NumMeanStatus IS NOT NULL ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("NumMeanStatus") != null) {
					if (queryForRowSet.getString("NumMeanStatus").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusForstringFieldStatsStatus(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Column_Summary";
			String sql = "SELECT String_Status , DATE FROM  " + colSumm + " WHERE idApp=" + idApp
					+ " and String_Status IS NOT NULL ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("String_Status") != null) {
					if (queryForRowSet.getString("String_Status").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public Long getFailedRecordCountForTimelinessCheck(Long idApp) {
		Long recordCount = 0L;
		try {
			String status = null;
			String timelinessCheckTable = "DATA_QUALITY_timeliness_check";
			String sql = "SELECT TotalFailedCount from " + timelinessCheckTable + " where idApp=" + idApp;
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				recordCount = queryForRowSet.getLong(1);
			}
			return recordCount;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return recordCount;
		}
	}

	public String getDashboardStatusForTimelinessCheck(Long idApp) {
		try {
			String status = null;
			String timelinessCheckTable = "DATA_QUALITY_timeliness_check";
			String sql = "SELECT count(*) from " + timelinessCheckTable + " where idApp=" + idApp;
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Long recordCount = queryForRowSet.getLong(1);
				if (recordCount > 0) {
					status = "failed";
				} else {
					status = "passed";
				}
			}
			return status;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusForRecordAnomalyStatus(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Record_Anomaly";
			String sql = "SELECT status , DATE FROM  " + colSumm + " where idApp=" + idApp
					+ " ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("status") != null) {
					if (queryForRowSet.getString("status").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusForAllFieldsStatus(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Transaction_Summary";
			String sql = "SELECT STATUS , DATE FROM " + colSumm + " WHERE idApp=" + idApp
					+ " and TYPE =  'all' ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("STATUS") != null) {
					if (queryForRowSet.getString("STATUS").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public String getDashboardStatusForIdentityfieldsStatus(Long idApp) {
		try {
			List<String> dbStatusList = new ArrayList<String>(3);
			String colSumm = "DATA_QUALITY_Transaction_Summary";
			String sql = "SELECT STATUS , DATE FROM " + colSumm + " WHERE idApp=" + idApp
					+ " and TYPE =  'identity' ORDER BY DATE desc limit 3";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				if (queryForRowSet.getString("STATUS") != null) {
					if (queryForRowSet.getString("STATUS").equalsIgnoreCase("failed")) {
						return "failed";
					} else {
						// passed
						dbStatusList.add("passed");
					}
				} else {
					// return null
					dbStatusList.add(null);
				}
			}
			if (dbStatusList.size() == 0) {
				return null;
			}
			if (dbStatusList.contains("passed")) {
				return "passed";
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public String getDateForSummaryOfLastRun(long idApp) {
		try {
			String sql = "select recordCountAnomaly from listApplications where idApp=" + idApp;
			String queryForObject = jdbcTemplate.queryForObject(sql, String.class);
			String tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			if (queryForObject.equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			}
			String sql1 = "select max(Date) from " + tableName + " where idApp=?";
			LOG.debug("query" + sql1);
			return jdbcTemplate1.queryForObject(sql1, String.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getDateAndRunForSummaryOfLastRun(Long idApp, String recordCountAnomaly) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			}
			String sql = "select Date,Run from " + tableName + " where idApp=? and id=(select max(id) from " + tableName
					+ " where idApp=?)";
			LOG.debug("query" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, idApp);
			while (queryForRowSet.next()) {
				map.put("Date", queryForRowSet.getString(1));
				map.put("Run", queryForRowSet.getString(2));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return map;
	}

	public boolean checkRunIntheResultTableForScore(long idApp, String recordCountAnomaly) {
		try {
			String tableName = "";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}
			String sql = "SELECT COUNT( * ) AS count FROM (SELECT Date, Run FROM " + tableName
					+ " where idApp=? GROUP BY DATE, Run) AS countTable";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);
			while (queryForRowSet.next()) {
				// long count = queryForRowSet.getLong("count");
				if (queryForRowSet.getLong("count") >= 3)
					return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return false;
	}

	public String CalculateScoreForRecordAnomaly(Long idData, long idApp, String recordCountAnomaly, String rcaStatus,
			ListApplications listApplicationsData) {
		try {
			Double RC_Deviation = 0.0;
			String sql = "";
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
						&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
					sql = "SELECT  RC_Deviation FROM  DATA_QUALITY_Transactionset_sum_A1 " + " WHERE idApp=" + idApp
							+ " and DATE = ( SELECT MAX( DATE ) FROM  DATA_QUALITY_Transactionset_sum_A1 where idApp="
							+ idApp + ") and "
							+ "run=(SELECT MAX( run ) FROM  DATA_QUALITY_Transactionset_sum_A1 where idApp=" + idApp
							+ " and DATE = ( SELECT MAX( DATE ) "
							+ "FROM  DATA_QUALITY_Transactionset_sum_A1 where idApp=" + idApp + ")) " + " LIMIT 1";
				} else {
					sql = "SELECT  avg(RC_Deviation) FROM  DATA_QUALITY_Transactionset_sum_A1 where idApp=" + idApp
							+ " and RC_Deviation is not null" + " limit 1";
				}
				LOG.debug(sql);
				RC_Deviation = jdbcTemplate1.queryForObject(sql, Double.class);
			} else {
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
						&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
					sql = "select sum(DQICountMlt)/sum(RecordCount) as finalDQI from "
							+ "(SELECT (RecordCount*dgDqi) as DQICountMlt,RecordCount from "
							+ "(SELECT RecordCount,(CASE WHEN (dGroupDeviation <= " + threshold + ")THEN 100 ELSE "
							+ "(CASE WHEN (dGroupDeviation >=6)THEN 0 ELSE (100 - ( (ABS(dGroupDeviation - " + threshold
							+ ") *100) / ( 6 - " + threshold + " ) )) END) END)"
							+ " AS dgDqi FROM DATA_QUALITY_Transactionset_sum_dgroup where " + " idApp=" + idApp
							+ " and (DATE = ( SELECT MAX( DATE ) FROM DATA_QUALITY_Transactionset_sum_dgroup where idApp="
							+ idApp + ")) "
							+ "and (run=(SELECT MAX( run ) FROM  DATA_QUALITY_Transactionset_sum_dgroup where idApp="
							+ idApp
							+ " and DATE = ( SELECT MAX( DATE ) FROM DATA_QUALITY_Transactionset_sum_dgroup where idApp="
							+ idApp + ")))) as alias) as alias1";
					LOG.debug(sql);
					SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
					while (queryForRowSet.next()) {
						RC_Deviation = queryForRowSet.getDouble(1);
					}
					DecimalFormat df = new DecimalFormat("#0.0");
					if (RC_Deviation > 100) {
						RC_Deviation = 100.0;
					}
					String score = df.format(RC_Deviation);
					return score;
				} else {
					sql = "SELECT  avg(dGroupDeviation) FROM  DATA_QUALITY_Transactionset_sum_dgroup where idApp="
							+ idApp + " and dGroupDeviation is not null" + " limit 1";
					LOG.debug(sql);
				}
				RC_Deviation = jdbcTemplate1.queryForObject(sql, Double.class);
			}
			DecimalFormat df = new DecimalFormat("#0.0");
			double val = 0;
			if (RC_Deviation <= threshold) {
				return "100.0";
			} else if (RC_Deviation >= 6) {
				return "0.0";
			} else {
				val = 100 - ((RC_Deviation - threshold) * 100) / (6 - threshold);
			}
			if (val > 100) {
				val = 100;
			}
			String score = df.format(val);
			return score;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
			// TODO: handle exception
		}
		return "0.0";
	}

	@Override
	public String CalculateScoreForNullCount(Long idData, long idApp, String recordCountAnomaly, String nullcountStatus,
			ListApplications listApplicationsData) {
		String sql = "";
		try {
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
					&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
				sql = "SELECT avg(Null_Percentage) FROM DATA_QUALITY_Column_Summary WHERE " + " idApp=" + idApp
						+ " and Date=(select max(Date) from DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ " and STATUS IS NOT NULL) and "
						+ "Run=(select max(Run) from DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ " and Date=(select max(Date) from DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ ") and STATUS IS NOT NULL) and STATUS IS NOT NULL GROUP BY DATE, Run limit 1;";
			} else {
				sql = "SELECT avg(Null_Percentage) FROM DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ " and STATUS IS NOT NULL and Null_Percentage IS NOT NULL";
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Double Percentage = null;
			while (queryForRowSet.next()) {
				Percentage = queryForRowSet.getDouble(1);
			}
			Percentage = 100 - Percentage;
			DecimalFormat df = new DecimalFormat("#0.0");
			String PercentageDF = df.format(Percentage);
			return PercentageDF;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "0.0";
	}

	public String CalculateScoreForallFields(Long idData, long idApp, String allFieldsStatus,
			ListApplications listApplicationsData) {
		try {
			String sql = "SELECT Percentage FROM DATA_QUALITY_Transaction_Summary WHERE " + "idApp=" + idApp
					+ " and Date=(select max(Date) from DATA_QUALITY_Transaction_Summary where idApp=" + idApp
					+ " and Type='all') and "
					+ "Run=(select max(Run) from DATA_QUALITY_Transaction_Summary where idApp=" + idApp
					+ " and Date=(select max(Date) " + "from DATA_QUALITY_Transaction_Summary where idApp=" + idApp
					+ " and Type='all') and Type='all') and Type='all' limit 1";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Double Percentage = null;
			while (queryForRowSet.next()) {
				Percentage = queryForRowSet.getDouble(1);
			}
			Percentage = 100 - Percentage;
			LOG.debug("Percentage=" + Percentage);
			DecimalFormat df = new DecimalFormat("#0.0");
			String PercentageDF = df.format(Percentage);
			return PercentageDF;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "0.0";
	}

	public SqlRowSet CalculateDetailsForDashboard(long idApp, String tabName, ListApplications listApplicationsData) {

		String sql = "select * from DashBoard_Summary where AppId = " + idApp + " and Test = '" + tabName + "' and "
				+ "Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp + " "
				+ "and Date = (select MAX(Date) from DashBoard_Summary where AppId = " + idApp + " and Test = '"
				+ tabName + "') and Test" + " = '" + tabName + "') "
				+ "and Date = (select MAX(Date) from DashBoard_Summary where AppId = " + idApp + " and Test = '"
				+ tabName + "')";
		LOG.debug(sql);
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		return queryForRowSet;
	}

	@Override
	public SqlRowSet getDashboardSummaryByCheck(long idApp, String testName, int maxRun,String maxDate) {

		try{
				String	sql = "select * from DashBoard_Summary where AppId = " + idApp + " and Test = '" + testName + "' and "
						+ "Run ="+ maxRun +" and Date ='" + maxDate + "'";

			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet global_rules_configured_count(long idData) {
		String sql = "SELECT count(1) from rule_Template_Mapping where templateid =" + idData + "";
		return jdbcTemplate.queryForRowSet(sql);
	}

	public String CalculateScoreForTimelinessCheck(long idApp, ListApplications listApplicationsData) {
		try {
			String sql = "SELECT count(*) from DATA_QUALITY_timeliness_check where idApp=" + idApp;
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Long noOfFailedRecords = null;
			while (queryForRowSet.next()) {
				noOfFailedRecords = queryForRowSet.getLong(1);
			}
			Long totalRecordCount = getRecordCount(listApplicationsData, idApp);
			double percentage = ((noOfFailedRecords * 1.0) / totalRecordCount) * 100;
			percentage = 100 - percentage;
			DecimalFormat df = new DecimalFormat("#0.0");
			String PercentageDF = df.format(percentage);
			return PercentageDF;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "0.0";
	}

	public String CalculateScoreForNumericalField(Long idData, long idApp, String numericalFieldStatus,
			ListApplications listApplicationsData) {
		try {
			double RC_Deviation = 0;
			double threshold = 0;
			String sql = "";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
					&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
				sql = "select sum(DQICountMlt)/sum(Count) as finalDQI from (SELECT (Count*NumDqi) as "
						+ "DQICountMlt,Count from (SELECT Count,(CASE WHEN (NumMeanDeviation <= "
						+ "NumMeanThreshold) THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6) THEN "
						+ "0 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - "
						+ "NumMeanThreshold ) )) END) END) AS NumDqi FROM DATA_QUALITY_Column_Summary " + "WHERE idApp="
						+ idApp + " and NumMeanThreshold IS NOT NULL and  (DATE = ( SELECT MAX( DATE ) FROM "
						+ "DATA_QUALITY_Column_Summary  where idApp=" + idApp
						+ " and NumMeanThreshold IS NOT NULL)) and "
						+ "(run=(SELECT MAX( run ) FROM  DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ " and DATE = " + "( SELECT MAX( DATE ) FROM DATA_QUALITY_Column_Summary where idApp=" + idApp
						+ ") and " + "NumMeanThreshold IS NOT NULL))) as alias) as alias1";
			} else {
				sql = "SELECT avg(NumMeanDeviation),avg(NumMeanThreshold) FROM DATA_QUALITY_Column_Summary where idApp="
						+ idApp + " and NumMeanThreshold IS NOT NULL and NumMeanDeviation IS NOT NULL";
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					RC_Deviation = queryForRowSet.getDouble(1);
					threshold = queryForRowSet.getDouble(2);
				}
				DecimalFormat df = new DecimalFormat("#0.0");
				double val = 0;
				if (RC_Deviation <= threshold) {
					return "100.0";
				} else if (RC_Deviation >= 6) {
					return "0";
				} else {
					val = 100 - ((RC_Deviation - threshold) * 100) / (6 - threshold);
				}
				if (val > 100) {
					val = 100;
				}
				if (val < 0) {
					val = 0;
				}
				String score = df.format(val);
				return score;
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Double Percentage = null;
			while (queryForRowSet.next()) {
				Percentage = queryForRowSet.getDouble(1);
			}
			if (Percentage > 100) {
				Percentage = 100.0;
			}
			LOG.debug("Percentage=" + Percentage);
			DecimalFormat df = new DecimalFormat("#0.0");
			String PercentageDF = df.format(Percentage);
			return PercentageDF;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return "0.0";
	}

	public String CalculateScoreForStringField(Long idData, long idApp, String stringFieldStatus,
			ListApplications listApplicationsData) {
		try {
			String sql = "";
			double RC_Deviation = 0;
			double threshold = 0;
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
					&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
				sql = "select sum(DQICountMlt)/sum(Count) as finalDQI from (SELECT (Count*StringDQI) as "
						+ "DQICountMlt,Count from ( SELECT Count,(CASE WHEN (StrCardinalityDeviation "
						+ "<= String_Threshold) THEN 100 ELSE (CASE WHEN (StrCardinalityDeviation >=6) "
						+ "THEN 0 ELSE (100 - ( (ABS(StrCardinalityDeviation - String_Threshold) *100) "
						+ "/ ( 6 - String_Threshold ) )) END) END) AS StringDQI FROM "
						+ "DATA_QUALITY_Column_Summary WHERE idApp=" + idApp + " and String_Threshold IS NOT NULL and  "
						+ "(DATE = ( SELECT MAX( DATE ) FROM DATA_QUALITY_Column_Summary  where idApp=" + idApp
						+ " and String_Threshold IS NOT NULL)) and (run=(SELECT MAX( run ) FROM  "
						+ "DATA_QUALITY_Column_Summary where idApp=" + idApp + " and DATE = ( SELECT MAX( DATE ) FROM "
						+ "DATA_QUALITY_Column_Summary  where idApp=" + idApp
						+ " and String_Threshold IS NOT NULL)))) as " + "alias) as alias1";
			} else {
				sql = "SELECT avg(StrCardinalityDeviation),avg(String_Threshold) FROM DATA_QUALITY__Column_Summary where idApp="
						+ idApp + " and StrCardinalityDeviation IS NOT NULL and String_Threshold IS NOT NULL";
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					RC_Deviation = queryForRowSet.getDouble(1);
					threshold = queryForRowSet.getDouble(2);
				}
				DecimalFormat df = new DecimalFormat("#0.0");
				double val = 0;
				if (RC_Deviation <= threshold) {
					return "100.0";
				} else if (RC_Deviation >= 6) {
					return "0";
				} else {
					val = 100 - ((RC_Deviation - threshold) * 100) / (6 - threshold);
				}
				if (val > 100) {
					val = 100;
				}
				if (val < 0) {
					val = 0;
				}
				String score = df.format(val);
				return score;
			}
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Double Percentage = null;
			while (queryForRowSet.next()) {
				Percentage = queryForRowSet.getDouble(1);
			}
			LOG.debug("Percentage=" + Percentage);
			DecimalFormat df = new DecimalFormat("#0.0");
			String PercentageDF = df.format(Percentage);
			return PercentageDF;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
			// e.printStackTrace();
		}
		return "0.0";
	}

	public String CalculateScoreForrecordFieldScore(Long idData, long idApp, String recordAnomalyNewStatus,
			ListApplications listApplicationsData) {
		try {
			String sql = "";
			Double failedRecordCount = 0.0;
			Double recordAnomalyCount = 0.0;
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				failedRecordCount = Double
						.parseDouble(getNumberOfRecordsFailed("DATA_QUALITY_Transactionset_sum_A1", idApp).toString());
				recordAnomalyCount = Double.parseDouble(getRecordCount(listApplicationsData, idApp).toString());
			} else {
				failedRecordCount = Double.parseDouble(
						getNumberOfRecordsFailed("DATA_QUALITY_Transactionset_sum_dgroup", idApp).toString());
				recordAnomalyCount = Double.parseDouble(getRecordCount(listApplicationsData, idApp).toString());
			}
			double val = (failedRecordCount * 100 / recordAnomalyCount);
			val = 100 - val;
			if (val > 100) {
				val = 100.0;
			}
			if (val < 0) {
				val = 0.0;
			}
			DecimalFormat df = new DecimalFormat("#0.0");
			String recordAnomoly = df.format(val);
			return recordAnomoly;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "0.0";
	}

	public Long getNumberOfRecordsFailed(String tableName, Long idApp) {
		try {
			String sql = "select recordAnomalyCount FROM " + tableName + " where id=(select max(id) from " + tableName
					+ " where idApp=?) and idApp=?" + " LIMIT 1";
			LOG.debug("getNumberOfRecordsFailed=" + sql);

			Long numberOfRecordsFailed = jdbcTemplate1.queryForObject(sql, Long.class, idApp, idApp);
			if (numberOfRecordsFailed == null)
				numberOfRecordsFailed = 0l;
			LOG.debug("numberOfRecordsFailed here=" + numberOfRecordsFailed);
			return numberOfRecordsFailed;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public Long getRecordCount(ListApplications listApplicationsData, long idApp) {
		try {
			String tableName = "";
			String sql = "";
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
						&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
					sql = "select RecordCount from " + tableName + " where idApp=? and Run=(select max(Run) from "
							+ tableName + " where idApp=" + idApp + " and Date=(SELECT max(date) from " + tableName
							+ " where idApp=" + idApp + ")) and Date=(SELECT max(date) from " + tableName
							+ " where idApp=" + idApp + ") limit 1";
				} else {
					sql = "select sum(RecordCount) from " + tableName + " where idApp=" + idApp
							+ " and RecordCount IS NOT NULL limit 1";
				}
				Long recordCount = jdbcTemplate1.queryForObject(sql, Long.class);
				return recordCount;
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
						&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
					sql = "select RecordCount from " + tableName + " where Run=(select max(Run) from " + tableName
							+ " where idApp=" + idApp + " and Date=(SELECT max(date) from " + tableName
							+ " where idApp=" + idApp + ")) and Date=(SELECT max(date) from " + tableName
							+ " where idApp=" + idApp + ")";
				} else {
					sql = "select sum(RecordCount) from " + tableName + " where idApp=" + idApp
							+ " and RecordCount IS NOT NULL limit 1";
				}
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				Long recordCount = 0l;
				while (queryForRowSet.next()) {
					recordCount = recordCount + queryForRowSet.getLong(1);
				}
				return recordCount;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	public String getScoreForDataDrift(ListApplications listApplicationsData) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String tabName = "DQ_Data Drift";
			String sql = "select * from DashBoard_Summary where AppId = " + idApp + " and Test = '" + tabName + "' and "
					+ "Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp + " "
					+ "and Date = (select MAX(Date) from DashBoard_Summary where AppId = " + idApp + ") and Test"
					+ " = '" + tabName + "') " + "and Date = (select MAX(Date) from DashBoard_Summary where AppId = "
					+ idApp + ")";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			String DQI = "";
			while (queryForRowSet.next()) {
				DQI = queryForRowSet.getString(4);
			}
			String dataDriftDQI = "0";
			if (DQI == null) {
				dataDriftDQI = "0.0";
			} else {
				double Percentage = Double.parseDouble(DQI);
				Percentage = Math.floor(Percentage);
				DecimalFormat df1 = new DecimalFormat("#0.0");
				dataDriftDQI = df1.format(Percentage);
			}

			return dataDriftDQI;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "0.0";
	}

	// Sumeet_30_07_2018---modified by pallavi 01_08_2018
	public Map<String, String> checkRulesTable(ListApplications listApplicationsData) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Long idApp = listApplicationsData.getIdApp();
			String sql = "select ruleType from listColRules where idData=" + listApplicationsData.getIdData()
					+ " limit 1";
			String ruleType = jdbcTemplate.queryForObject(sql, String.class);
			//// totalPassed changed to totalFailed
			String sql2 = "SELECT totalFailed,totalRecords FROM DATA_QUALITY_" + idApp + "_RULES WHERE "
					+ "Date=(select max(Date) from DATA_QUALITY_" + idApp + "_RULES) and "
					+ "run=(select max(run) from DATA_QUALITY_" + idApp + "_RULES where "
					+ "date=(select max(Date) from DATA_QUALITY_" + idApp + "_RULES))";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql2);
			Long totalFailed = 0l, totalRecords = 0l;
			while (queryForRowSet.next()) {
				totalFailed = totalFailed + queryForRowSet.getLong(1);
				totalRecords = totalRecords + queryForRowSet.getLong(2);
			}
			// double ruleScore = (totalPassed * 100 / totalRecords);
			double ruleScore = ((totalFailed * 1.0) / totalRecords) * 100;
			ruleScore = 100 - ruleScore;
			LOG.debug("ruleScore=" + ruleScore);
			DecimalFormat df = new DecimalFormat("#0.0");
			String ruleScoreDF = df.format(ruleScore);
			map.put("ruleType", ruleType);
			map.put("ruleScoreDF", ruleScoreDF);
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return map;
	}

	String getSourceNameFromListDataSource(Long idData) {
		try {
			return jdbcTemplate.queryForObject("SELECT name FROM listDataSources WHERE idData=" + idData, String.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "";
	}

	public List<DataQualityMasterDashboard> getDataFromDataQualityDashboard(Long projectId, List<Project> projlst) {

		/*
		 * select * from databuck_results_db_development.data_quality_dashboard where
		 * idApp in (select idApp from databuck_app_db_development.listApplications
		 * where project_id = 63) order by idApp;
		 */
		/* select * from data_quality_dashboard order by idapp */
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
		List<Idapp> IdApp = new ArrayList<>();
		try {

			String sql = "select idApp from listApplications where project_id in ( " + projIds + " ) order by idApp";
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		// LOG.debug("text-->"+text);
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		if (!text.isEmpty()) {
			try {
				// String sql = "select t1.*, t2.test_run from data_quality_dashboard t1 left
				// join app_uniqueId_master_table t2 on t1.idApp = t2.idapp and t1.Date =
				// t2.execution_date and t1.Run = t2.run where t1.idApp in ("+text+") order by
				// t1.idapp";
				String sql = "select t1.*, t2.test_run,t3.RecordCount from data_quality_dashboard t1 left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date and t1.Run = t2.run "
						+ " left join (select idApp, Date, Run, Sum(RecordCount) AS RecordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run "
						+ " where t1.idApp in (" + text + ") order by t1.idapp";
				// LOG.debug("\nResultsSq;: " + sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
					Long idApp = queryForRowSet.getLong("idApp");
					dashboard.setIdApp(idApp);
					dashboard.setDate(queryForRowSet.getString("date"));
					dashboard.setRun(queryForRowSet.getLong("run"));
					dashboard.setTestRun(queryForRowSet.getString("test_run"));
					dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
					try {
						String templateSql = "select la.idData, ls.schemaName as connectionName from listApplications la join listDataSources lds on la.idData=lds.idData "
								+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp="
								+ idApp;
						Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
						dashboard.setTemplateId(Long.parseLong(la_data.get("idData").toString()));
						dashboard.setConnectionName((String) la_data.get("connectionName"));
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					dashboard.setSource1(queryForRowSet.getString("sourceName"));
					dashboard.setRecordCount(queryForRowSet.getLong("RecordCount"));
					dashboard.setRecordCountStatus(queryForRowSet.getString("recordCountStatus"));
					dashboard.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
					dashboard.setStringFieldStatus(queryForRowSet.getString("stringFieldStatus"));
					dashboard.setNumericalFieldStatus(queryForRowSet.getString("numericalFieldStatus"));
					dashboard.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
					dashboard.setUserSelectedFieldsStatus(queryForRowSet.getString("userSelectedFieldStatus"));
					dashboard.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
					dashboard.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
					dashboard.setProjectName(getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
					dashboard.setAggreagteDQI(Math.floor(queryForRowSet.getDouble("aggregateDQI"))); // added
					// for
					// showing
					// aggDqI
					// on
					// Dashboard.[priyanka]

					masterDashboard.add(dashboard);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		// createMatchingCsv(masterDashboard);
		return masterDashboard;
	}

	public JSONArray getPaginatedResultsJsonData(HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] { "IdApp", "date", "run", "test_run", "validationCheckName", "sourceName",
				"aggregateDQI", "RecordCount", "recordCountStatus", "nullCountStatus", "primaryKeyStatus",
				"userSelectedFieldStatus", "numericalFieldStatus", "recordAnomalyStatus", "dataDriftStatus" };

		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		DateUtility.DebugLog("getPaginatedValidationsJsonData 01",
				String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			List<Idapp> IdApp = new ArrayList<>();
			try {

				String sql = "select idApp from listApplications where project_id in ( "
						+ oPaginationParms.get("ProjectIds") + " ) order by idApp";
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					Idapp idapp = new Idapp();
					idapp.setIdApp(queryForRowSet.getLong("idApp"));
					IdApp.add(idapp);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			if (IdApp != null && IdApp.size() > 0) {
				String sProjectFilteredIdApps = IdApp.toString().replace("[", "").replace("]", "");

				sOption1Sql = String.format("and (t1.date between '%1$s' and '%2$s') \n",
						oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

				sOption2Sql = "and 1 = case when validationCheckName like 'LIKE-TEXT' then 1 when sourceName like 'LIKE-TEXT' then 1 else 0 end \n"
						.replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
				sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

				sDataSql = "" + "select t1.*, t2.test_run,t3.RecordCount from data_quality_dashboard t1 "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run "
						+ " left join (select idApp, Date, Run, Sum(RecordCount) AS RecordCount "
						+ "from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run " + " where t1.idApp in ("
						+ sProjectFilteredIdApps + ") ";

				if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("1")) {
					// sDataSql = sDataSql + sOption1Sql + " order by t1.idapp limit 1000;";
					sDataSql = sDataSql + sOption1Sql + " order by t1.date desc limit 1000;";
				} else if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("2")) {
					// sDataSql = sDataSql + sOption2Sql + " order by t1.idapp limit 1000;";
					sDataSql = sDataSql + sOption2Sql + " order by t1.date desc limit 1000;";
				} else {
					// sDataSql = sDataSql + sOption1Sql + sOption2Sql + "limit 1000;";
					sDataSql = sDataSql + sOption1Sql + sOption2Sql + " order by t1.date desc limit 1000;";
				}

				DateUtility.DebugLog("getPaginatedValidationsJsonData 02", String.format(
						"Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql));

				aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate1, sDataSql, aColumnSpec, "sampleTable",
						null);

				for (HashMap<String, String> oDataViewListRecord : aDataViewList) {
					String templateSql = "select la.idData, ls.schemaName as connectionName from listApplications la join listDataSources lds on la.idData=lds.idData "
							+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp="
							+ oDataViewListRecord.get("IdApp");
					Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
					// LOG.debug("\nData:\t" + oDataViewListRecord.get("IdApp") + "\t" +
					// la_data.get("idData").toString() + "\t" +(String)
					// la_data.get("connectionName") );
					oDataViewListRecord.put("idData", la_data.get("idData").toString());
					oDataViewListRecord.put("connectionName", (String) la_data.get("connectionName"));
					oDataViewListRecord.put("projectName",
							getProjectNameOfIdapp(Long.parseLong(oDataViewListRecord.get("IdApp"))));
				}

				sDataViewList = oMapper.writeValueAsString(aDataViewList);
				aRetValue = new JSONArray(sDataViewList);

				DateUtility.DebugLog("getPaginatedValidationsJsonData 03",
						String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));
			}

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			throw oException;
		}
		return aRetValue;
	}

	public String getProjectNameOfIdapp(Long idApp) {

		/*
		 * select a.projectName from databuck_app_db.project a ,
		 * databuck_app_db.listApplications b where b.project_id = a.idProject and
		 * b.idApp = 96 ;
		 */

		String sql = "select a.projectName  from project a , listApplications b"
				+ " where b.project_id = a.idProject and b.idApp = ?";
		String projname = ""+ jdbcTemplate.queryForObject(sql, new Object[] { idApp }, String.class);
		/*
		 * SqlRowSet results = jdbcTemplate.queryForRowSet(sql); projectName =
		 * results.getString("ProjectName");
		 */

		return projname;
	}

	public List<DataQualityMasterDashboard> getMasterDashboardForDataQuality(Map<Long, String> resultmasterdata,
			List<ListApplications> listapplicationsData) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		try {
			for (ListApplications la : listapplicationsData) {
				LOG.debug("la.getIdApp()" + la.getIdApp());
				DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
				dashboard.setIdApp(la.getIdApp());
				Object[] dateAndRun = getDateAndRun(la.getIdApp(), la.getRecordCountAnomaly());
				dashboard.setDate(dateAndRun[0].toString());
				dashboard.setRun((Long) dateAndRun[1]);
				dashboard.setValidationCheckName(la.getName());
				dashboard.setSource1(getSourceNameFromListDataSource(la.getIdData()));

				dashboard.setRecordCountStatus(getDashboardStatusForRCA(la.getIdApp(), la.getRecordCountAnomaly()));
				if (la.getNonNullCheck().equalsIgnoreCase("Y")) {
					dashboard.setNullCountStatus(getDashboardStatusForNullCount(la.getIdApp()));
				} else {
					dashboard.setNullCountStatus("NA");
				}
				if (la.getStringStatCheck().equalsIgnoreCase("Y")) {
					dashboard.setStringFieldStatus(getDashboardStatusForstringFieldStatsStatus(la.getIdApp()));
				} else {
					dashboard.setStringFieldStatus("NA");
				}
				if (la.getNumericalStatCheck().equalsIgnoreCase("Y")) {
					dashboard.setNumericalFieldStatus(getDashboardStatusFornumericalFieldStatsStatus(la.getIdApp()));
				} else {
					dashboard.setNumericalFieldStatus("NA");
				}
				if (la.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
					dashboard.setRecordAnomalyStatus(getDashboardStatusForRecordAnomalyStatus(la.getIdApp()));
				} else {
					dashboard.setRecordAnomalyStatus("NA");
				}
				Map<String, String> listDFTranRuleMap = getdatafromlistdftranrule(la.getIdApp());
				dashboard.setUserSelectedFieldsStatus("NA");
				dashboard.setAllFieldsStatus("NA");
				for (Entry<String, String> map : listDFTranRuleMap.entrySet()) {

					if (map.getKey().equalsIgnoreCase("Y")) {
						dashboard.setAllFieldsStatus(getDashboardStatusForAllFieldsStatus(la.getIdApp()));
					} else {
						dashboard.setAllFieldsStatus("NA");
					}
					if (map.getValue().equalsIgnoreCase("Y")) {
						dashboard.setUserSelectedFieldsStatus(getDashboardStatusForIdentityfieldsStatus(la.getIdApp()));
					} else {
						dashboard.setUserSelectedFieldsStatus("NA");
					}

				}

				masterDashboard.add(dashboard);

			}

		} catch (Exception e) {
			// e.printStackTrace();
			LOG.error(e.getMessage());
		}
		// createMatchingCsv(masterDashboard);
		return masterDashboard;

	}

	public void createQualityCsv(Long projectId, List<Project> projlst) {
		List<DataQualityMasterDashboard> masterDashboard = getDataFromDataQualityDashboard(projectId, projlst);
		try {
			// Collections.reverse(masterDashboard);
			PrintWriter pw = new PrintWriter(
					new FileWriter(new File(System.getenv("DATABUCK_HOME") + "/csvFiles/dq_dashboard.csv")));
			String csvHeader = "idApp,Date,Run,Test Run,Validation Check Name,Source Name,Record Count Status,Null Count Status,All Field Status,"
					+ "User Selected Field Status,Numerical Field Status,String Field Status,Record Anomaly Status, Data Drift Status";
			pw.println(csvHeader);
			for (DataQualityMasterDashboard dqData : masterDashboard) {
				StringJoiner csvData = new StringJoiner(",");
				csvData.add(dqData.getIdApp().toString());
				csvData.add(dqData.getDate());
				csvData.add(dqData.getRun().toString());
				csvData.add(dqData.getTestRun());
				csvData.add(dqData.getValidationCheckName());
				csvData.add(dqData.getSource1());
				csvData.add(dqData.getRecordCountStatus());
				csvData.add(dqData.getNullCountStatus());
				csvData.add(dqData.getAllFieldsStatus());
				csvData.add(dqData.getUserSelectedFieldsStatus());
				csvData.add(dqData.getNumericalFieldStatus());
				csvData.add(dqData.getStringFieldStatus());
				csvData.add(dqData.getRecordAnomalyStatus());
				csvData.add(dqData.getDataDriftStatus());
				pw.println(csvData);
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
	}

	private Object[] getDateAndRun(long idApp, String recordCountAnomaly) {
		String date = "";
		Long run = 1l;
		try {
			SqlRowSet queryForRowSet;
			String RCATableName = "";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				RCATableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				RCATableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			String sql = "SELECT run, DATE FROM  " + RCATableName + " WHERE idApp=? and DATE = ( SELECT MAX( DATE ) "
					+ "FROM  " + RCATableName + " where idApp=?) ORDER BY run DESC LIMIT 1";
			LOG.debug("sql:" + sql);
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, idApp);

			while (queryForRowSet.next()) {
				date = queryForRowSet.getString(2);
				run = queryForRowSet.getLong(1);
				LOG.debug(date + "date&Run" + run);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return new Object[] { date, run };
	}

	public List<String> getallColumns(String tableName) {
		ArrayList<String> al = new ArrayList<String>();
		SqlRowSetMetaData metaData = jdbcTemplate1.queryForRowSet("SELECT * FROM " + tableName + " LIMIT 1")
				.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			al.add(metaData.getColumnName(i));
		}
		return al;
	}

	public Long calculateRecordCountForAggregateRecordCount(ListApplications listApplicationsData) {
		try {
			Long recordCount = getRecordCount(listApplicationsData, listApplicationsData.getIdApp());
			Long idApp = listApplicationsData.getIdApp();
			String sql = "select sum(RecordCount*(RecordCount/" + recordCount
					+ ")) as aggregateRecordCount from DATA_QUALITY_Transactionset_sum_dgroup " + "where idApp=" + idApp
					+ " and Run=(select max(Run) from DATA_QUALITY_Transactionset_sum_dgroup where idApp=" + idApp
					+ " and Date=(SELECT max(date) from DATA_QUALITY_Transactionset_sum_dgroup where idApp=" + idApp
					+ ")) and " + "Date=(SELECT max(date) from  DATA_QUALITY_Transactionset_sum_dgroup where idApp="
					+ idApp + ")";
			Long aggregateRecordCount = jdbcTemplate1.queryForObject(sql, Long.class);
			LOG.debug("aggregateRecordCount=" + aggregateRecordCount);
			return aggregateRecordCount;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	// Sumeet_11_09_2018_changesforRejectDataDriftInd
	public String updaterejectIndDrift(String tableName, String userName, String Run, String uniqueValues,
			String colName, String dGroupVal, String dGroupCol) {

		String dGroupVal2 = "";
		String dGroupCol2 = "";

		if (dGroupVal == null || dGroupVal.equalsIgnoreCase("null") || ((dGroupVal.split("'")[0].trim().isEmpty()))) {
			dGroupVal2 = " dGroupVal IS NULL ";
		} else {
			dGroupVal2 = " dGroupVal = " + "'" + dGroupVal + "' ";
		}

		if (dGroupCol == null || dGroupCol.equalsIgnoreCase("null") || ((dGroupCol.split("'")[0].trim().isEmpty()))) {
			dGroupCol2 = " dGroupCol IS NULL ";
		} else {
			dGroupCol2 = " dGroupCol = " + "'" + dGroupCol + "' ";
		}

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String SQL = "update " + tableName + " set status = 'Rejected', userName = '" + userName + "', Time = '"
					+ dtf.format(now) + "' where Run = " + Run + " and status = 'True' and uniqueValues = '"
					+ uniqueValues + "' and colName = '" + colName + "'" + " and " + dGroupVal2 + " and " + dGroupCol2;

			LOG.debug(" ---------- Update SQL = " + SQL);
			jdbcTemplate1.execute(SQL);
			
		} catch (Exception E) {
			LOG.error(E.getMessage());

		}
		return "true";
	}

	public String updaterejectIndValidity(String tableName, String dGroupCol, String run, String dGroupVal, String Date,
			String userName, String idApp) {

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String SQL = "update " + tableName + " set Action = 'Accepted', userName = '" + userName + "', Time = '"
					+ dtf.format(now) + "', Validity = 'False' where " + "Run = " + run
					+ " and Action = 'Reject' and dGroupCol = '" + dGroupCol + "' and dGroupVal = '" + dGroupVal
					+ "' and Date = '" + Date + "' and idApp=" + idApp;
			jdbcTemplate1.execute(SQL);

			/*
			 * String isThresholdNullSql = "select RC_Std_Dev from " + tableName +
			 * " where Run = " + run + " and Action = 'Rejected' and dGroupCol = '" +
			 * dGroupCol + "' and dGroupVal = '" + dGroupVal + "' and Date = '" + Date +
			 * "'"; SqlRowSet results1 = jdbcTemplate1.queryForRowSet(isThresholdNullSql);
			 * String isThresholdNull = ""; if (results1.next()) { isThresholdNull =
			 * results1.getString("RC_Std_Dev"); }
			 *
			 * if(isThresholdNull != null){ String newThresholdSql =
			 * "select (ABS(RecordCount - RC_Mean)/RC_Std_Dev) as thval from " + tableName +
			 * " where Run = " + run + " and Action = 'Rejected' and dGroupCol = '" +
			 * dGroupCol + "' and dGroupVal = '" + dGroupVal + "' and Date = '" + Date +
			 * "'"; SqlRowSet results = jdbcTemplate1.queryForRowSet(newThresholdSql);
			 * double newThreshold = 0.0; if (results.next()) { newThreshold =
			 * results.getDouble("thval"); }
			 *
			 * LOG.debug("new"+ newThreshold); String thresholdQuery =
			 * "select recordCountAnomalyThreshold from listApplications where idApp=" +
			 * idApp; Double threshold = jdbcTemplate.queryForObject(thresholdQuery,
			 * Double.class); LOG.debug("old"+ threshold); if(newThreshold >
			 * threshold){ String updateSQL =
			 * "update listApplications set recordCountAnomalyThreshold = '" + newThreshold
			 * + "'  where idApp=" + idApp; jdbcTemplate.execute(updateSQL); }
			 * if(newThreshold < threshold){ String updateSQL = ""; if(newThreshold < 0){
			 * updateSQL = "update listApplications set recordCountAnomalyThreshold = '" +
			 * (newThreshold+0.1) + "'  where idApp=" + idApp; } else{ updateSQL =
			 * "update listApplications set recordCountAnomalyThreshold = '" +
			 * (newThreshold-0.1) + "'  where idApp=" + idApp; }
			 * jdbcTemplate.execute(updateSQL); }
			 *
			 * }
			 */
		} catch (Exception E) {
			LOG.error(E.getMessage());
			E.printStackTrace();
		}
		return "true";
	}

	public String updaterejectIndGBRCA(String tableName, String dGroupCol, String run, String dGroupVal, String Date,
			String userName, String idApp) {

		try {
			/*
			 * DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy/MM/dd HH:mm:ss");
			 * LocalDateTime now = LocalDateTime.now(); String SQL = "update " + tableName +
			 * " set Action = 'Rejected', userName = '" + userName + "', Time = '" +
			 * dtf.format(now) + "', Validity = 'False' where " + "Run = " + run +
			 * " and Action = 'Reject' and dGroupCol = '" + dGroupCol +
			 * "' and dGroupVal = '" + dGroupVal + "' and Date = '" + Date + "'";
			 * jdbcTemplate1.execute(SQL);
			 */

			String isThresholdNullSql = "select RC_Std_Dev from " + tableName + " where Run = " + run
					+ " and Action = 'Reject' and dGroupCol = '" + dGroupCol + "' and dGroupVal = '" + dGroupVal
					+ "' and Date = '" + Date + "' and idApp=" + idApp;
			SqlRowSet results1 = jdbcTemplate1.queryForRowSet(isThresholdNullSql);
			String isThresholdNull = "";
			if (results1.next()) {
				isThresholdNull = results1.getString("RC_Std_Dev");
			}

			if (isThresholdNull != null) {
				String newThresholdSql = "select (ABS(RecordCount - RC_Mean)/RC_Std_Dev) as thval from " + tableName
						+ " where Run = " + run + " and Action = 'Reject' and dGroupCol = '" + dGroupCol
						+ "' and dGroupVal = '" + dGroupVal + "' and Date = '" + Date + "' and idApp=" + idApp;
				SqlRowSet results = jdbcTemplate1.queryForRowSet(newThresholdSql);
				double newThreshold = 0.0;
				if (results.next()) {
					newThreshold = results.getDouble("thval");
				}

				LOG.debug("new" + newThreshold);
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				LOG.debug("old" + threshold);
				if (newThreshold > threshold) {
					String updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + newThreshold
							+ "'  where idApp=" + idApp;
					jdbcTemplate.execute(updateSQL);
				}
				if (newThreshold < threshold) {
					String updateSQL = "";
					if (newThreshold < 0) {
						updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + (newThreshold + 0.1)
								+ "'  where idApp=" + idApp;
					} else {
						updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + (newThreshold - 0.1)
								+ "'  where idApp=" + idApp;
					}
					jdbcTemplate.execute(updateSQL);
				}

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String SQL = "update " + tableName + " set Action = 'Rejected', userName = '" + userName + "', Time = '"
						+ dtf.format(now) + "', Validity = 'True' where " + "Run = " + run
						+ " and Action = 'Reject' and dGroupCol = '" + dGroupCol + "' and dGroupVal = '" + dGroupVal
						+ "' and Date = '" + Date + "' and idApp=" + idApp;
				jdbcTemplate1.execute(SQL);

			} else {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				String SQL = "update " + tableName + " set Action = 'Rejected', userName = '" + userName + "', Time = '"
						+ dtf.format(now) + "', Validity = 'True' where " + "Run = " + run
						+ " and Action = 'Reject' and dGroupCol = '" + dGroupCol + "' and dGroupVal = '" + dGroupVal
						+ "' and Date = '" + Date + "' and idApp=" + idApp;
				jdbcTemplate1.execute(SQL);
			}
		} catch (Exception E) {
			LOG.error(E.getMessage());
			E.printStackTrace();
		}
		return "true";
	}

	public String getMaxQuery(String tableName) {
		String maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName;
		return maxQuery;
	}

	public String updaterejectAllDrift(String tableName, String userName, int maxValue, String dateUpdate,
			String idApp,String columnname) {
		String colstr="";
		
		if (columnname == null || columnname.equalsIgnoreCase("null") || ((columnname.isEmpty()))) {
			colstr = "";
		} else {
			colstr = " and colName = " + "'" + columnname + "' ";
		}
		

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String SQL = "update " + tableName + " set status = 'Rejected', userName = '" + userName + "', Time = '"
				+ dtf.format(now) + "' where Operation != 'Missing' and Run = " + maxValue + " and Date = '"
				+ dateUpdate + "' and idApp=" + idApp+" "+colstr;
		LOG.debug("updaterejectAllDrift  "+SQL);
		jdbcTemplate1.update(SQL);
		return "True";

	}

	public String updaterejectAllGBRCA(String tableName, String userName, int maxValue, String dateUpdate,
			String idApp) {

		// -----------

		String isThresholdNullSql = "select max(RC_Std_Dev) as RC_Std_Dev from " + tableName + " where Run = "
				+ maxValue + " and Action = 'Reject' and Date = '" + dateUpdate + "' and idApp=" + idApp;
		SqlRowSet results1 = jdbcTemplate1.queryForRowSet(isThresholdNullSql);
		String isThresholdNull = "";
		if (results1.next()) {
			isThresholdNull = results1.getString("RC_Std_Dev");
		}

		if (isThresholdNull != null) {
			String newThresholdSql = "select MAX((ABS(RecordCount - RC_Mean)/RC_Std_Dev)) as thval from " + tableName
					+ " where Run = " + maxValue + " and Action = 'Reject' and Date = '" + dateUpdate + "' and idApp="
					+ idApp;
			SqlRowSet results = jdbcTemplate1.queryForRowSet(newThresholdSql);
			double newThreshold = 0.0;
			if (results.next()) {
				newThreshold = results.getDouble("thval");
			}

			LOG.debug("new" + newThreshold);
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
			LOG.debug("old" + threshold);
			if (newThreshold > threshold) {
				String updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + newThreshold
						+ "'  where idApp=" + idApp;
				jdbcTemplate.execute(updateSQL);
			}
			if (newThreshold < threshold) {
				String updateSQL = "";
				if (newThreshold < 0) {
					updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + (newThreshold + 0.1)
							+ "'  where idApp=" + idApp;
				} else {
					updateSQL = "update listApplications set recordCountAnomalyThreshold = '" + (newThreshold - 0.1)
							+ "'  where idApp=" + idApp;
				}
				jdbcTemplate.execute(updateSQL);
			}

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String SQL = "update " + tableName + " set Action = 'Rejected', userName = '" + userName + "', Time = '"
					+ dtf.format(now) + "', Validity = 'True' where Run = " + maxValue + " and Date = '" + dateUpdate
					+ "' and Validity = 'False' and Action = 'Reject' and idApp=" + idApp;
			jdbcTemplate1.execute(SQL);
			LOG.debug("xxx" + SQL);
			return "True";

		} else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String SQL = "update " + tableName + " set Action = 'Rejected', userName = '" + userName + "', Time = '"
					+ dtf.format(now) + "', Validity = 'True' where Run = " + maxValue + " and Date = '" + dateUpdate
					+ "' and Validity = 'False' and Action = 'Reject' and idApp=" + idApp;
			jdbcTemplate1.execute(SQL);
			LOG.debug("xxx" + SQL);
			return "True";
		}

		// -----------

	}

	public String updaterejectAllValidity(String tableName, String userName, int maxValue, String dateUpdate,
			String idApp) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String SQL = "update " + tableName + " set Action = 'Accepted', userName = '" + userName + "', Time = '"
				+ dtf.format(now) + "', Validity = 'False' where Run = " + maxValue + " and Date = '" + dateUpdate
				+ "' and Validity = 'True' and Action = 'Reject' and idApp=" + idApp;
		jdbcTemplate1.execute(SQL);
		LOG.debug("SQL" + SQL);
		return "True";

	}

	// changes done for Ui [priyanka]
	public String getAggregateDQIForDataQualityDashboard(Long idApp) {

		String aggDqi = null;

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "select core_qry.date_avg from(select Date, avg(DQI) as date_avg, Run from DashBoard_Summary where appId ="
					+ idApp
					+ " and length(trim(COALESCE(Status,''))) > 0 group by Date, Run order by Date Desc, Run desc ) core_qry limit 1";
		} else {
			sql = "select core_qry.date_avg from(select Date, avg(DQI) as date_avg, Run from DashBoard_Summary where appId ="
					+ idApp
					+ " and length(trim(ifnull(Status,''))) > 0 group by Date, Run order by Date Desc, Run desc ) core_qry limit 1";
		}

		/*
		 * String sql3 = "select Avg(DQI) from DashBoard_Summary where AppId =" + idApp
		 * + " and Date='" + maxDt + "' and Run=" + maxRun + " and Status is not null";
		 */
		LOG.debug("---------- Sql-----------" + sql);
		// aggDqi = jdbcTemplate1.queryForObject(sql, String.class);

		aggDqi = jdbcTemplate1.query(sql, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				return rs.next() ? rs.getString("date_avg") : null;
			}
		});

		LOG.debug("======= aggDqi =====" + aggDqi);

		// if (aggDqi.equalsIgnoreCase("NULL") || aggDqi.isEmpty() || aggDqi==null) {
		if (aggDqi == null) {
			aggDqi = "0.0";
		}

		String PercentageDF = "0";
		double Percentage = Double.parseDouble(aggDqi);
		DecimalFormat df1 = new DecimalFormat("#0.0");
		PercentageDF = df1.format(Percentage);

		LOG.debug("getAggregateDQIForDataQualityDashboard sql =>" + PercentageDF);

		return PercentageDF;
	}

	public LinkedHashMap<String, String> getSqlRulesDashboardSummary(String sIdApp) {
		LinkedHashMap<String, String> oRetValue = new LinkedHashMap<String, String>();

		SqlRowSet oSqlResult = null;
		String sSqlQuery, sMaxDate, sMaxRun;
		int nSqlRuleCount, nSqlRuleFailed;
		double dSqlRuleDqi;

		oRetValue.put("SqlRuleCount", "0");
		oRetValue.put("SqlRuleStatus", "failed");
		oRetValue.put("SqlRuleFailed", "0");
		oRetValue.put("SqlRuleDqi", "0.0");

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQuery = "select to_date(max(date)::text, 'YYYY-MM-DD') as MaxDate from data_quality_sql_rules where idapp = "
						+ sIdApp;
			} else {
				sSqlQuery = "select date_format(max(date), '%Y-%m-%d') as MaxDate from data_quality_sql_rules where idapp = "
						+ sIdApp;
			}
			oSqlResult = jdbcTemplate1.queryForRowSet(sSqlQuery);

			if (oSqlResult.next()) {
				sMaxDate = oSqlResult.getString("MaxDate");

				sSqlQuery = "select max(run) as MaxRun\n";
				sSqlQuery = sSqlQuery + "from data_quality_sql_rules\n";
				sSqlQuery = sSqlQuery + "where idapp = %1$s\n";
				sSqlQuery = sSqlQuery + "and date = '%2$s'";

				sSqlQuery = String.format(sSqlQuery, sIdApp, sMaxDate);
				oSqlResult = jdbcTemplate1.queryForRowSet(sSqlQuery);

				if (oSqlResult.next()) {
					sMaxRun = oSqlResult.getString("MaxRun");

					sSqlQuery = "select count(*) as SqlRuleCount\n";
					sSqlQuery = sSqlQuery + "from data_quality_sql_rules\n";
					sSqlQuery = sSqlQuery + "where idapp = %1$s\n";
					sSqlQuery = sSqlQuery + "and date = '%2$s'\n";
					sSqlQuery = sSqlQuery + "and run = %3$s";

					sSqlQuery = String.format(sSqlQuery, sIdApp, sMaxDate, sMaxRun);
					oSqlResult = jdbcTemplate1.queryForRowSet(sSqlQuery);

					if (oSqlResult.next()) {
						nSqlRuleCount = oSqlResult.getInt("SqlRuleCount");

						sSqlQuery = "select count(*) as SqlRuleFailed\n";
						sSqlQuery = sSqlQuery + "from data_quality_sql_rules\n";
						sSqlQuery = sSqlQuery + "where idapp = %1$s\n";
						sSqlQuery = sSqlQuery + "and date = '%2$s'\n";
						sSqlQuery = sSqlQuery + "and run = %3$s\n";
						sSqlQuery = sSqlQuery + "and status = 0;\n\n";

						sSqlQuery = String.format(sSqlQuery, sIdApp, sMaxDate, sMaxRun);
						oSqlResult = jdbcTemplate1.queryForRowSet(sSqlQuery);

						if (oSqlResult.next()) {
							nSqlRuleFailed = oSqlResult.getInt("SqlRuleFailed");

							dSqlRuleDqi = (((double) (nSqlRuleCount - nSqlRuleFailed)) / nSqlRuleCount) * 100;
							DateUtility.DebugLog("getSqlRulesDashboardSummary() final results", String.format(
									"sMaxDate, sMaxRun, nSqlRuleCount, nSqlRuleFailed, dSqlRuleDqi %1$s,%2$s,%3$s,%4$s,%5$s",
									sMaxDate, sMaxRun, nSqlRuleCount, nSqlRuleFailed, dSqlRuleDqi));

							oRetValue.replace("SqlRuleCount", String.valueOf(nSqlRuleCount));
							oRetValue.replace("SqlRuleStatus", ((int) dSqlRuleDqi == 100) ? "passed" : "failed");
							oRetValue.replace("SqlRuleFailed", String.valueOf(nSqlRuleFailed));
							oRetValue.replace("SqlRuleDqi", Double.toString(dSqlRuleDqi));
						}
					}
				}
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return oRetValue;
	}

	public SqlRowSet custom_rules_configured_count(long idData) {
		String sql = "SELECT count(1) from listColRules where idData =" + idData
				+ " and ruleType not in ('SQL Internal Rule', 'SQL Rule') and activeFlag='Y' ";
		LOG.debug("========custom_rules_configured_count sql==>" + sql);
		return jdbcTemplate.queryForRowSet(sql);
	}

	public SqlRowSet sql_rules_configured_count(String idapp) {
		String sql = "SELECT count(1) from data_quality_sql_rules where idapp ='" + idapp + "'";
		return jdbcTemplate1.queryForRowSet(sql);
	}
	/*
	 * //------------ [priyanka 25-12-2018] DATA_QUALITY_LENGTH_CHECK
	 *
	 * public List<DATA_QUALITY_Transaction_Summary> getNullCountGraph(String
	 * tableName) { try { String Query = "select Date, max(Run) as Run from " +
	 * tableName + " group by Date ORDER BY DATE ASC";
	 *
	 * RowMapper<DATA_QUALITY_Transaction_Summary> rowMapper = (rs, i) -> {
	 * DATA_QUALITY_Transaction_Summary dqts = new
	 * DATA_QUALITY_Transaction_Summary(); //
	 * LOG.debug("Date="+rs.getString("Date")); //
	 * LOG.debug("count="+rs.getInt("Run"));
	 * dqts.setDate(rs.getString("Date")); dqts.setTotalCount(rs.getInt("Run"));
	 * return dqts; }; List<DATA_QUALITY_Transaction_Summary> nullCountGraphValues =
	 * jdbcTemplate1.query(Query, rowMapper); List<DATA_QUALITY_Transaction_Summary>
	 * fullNullCountGraph = getFullNullCountGraph(nullCountGraphValues, tableName);
	 * return nullCountGraphValues; } catch (Exception e) { // e.printStackTrace();
	 * } return null; }
	 *
	 * //--------------------------------
	 */

	@Override
	public String getAvgDQIForTodaysDtAllValidations(Long projectId) {

		String avgDqi = null;

		// select Date,AVG(DQI) from databuck_results_db_development.dashboard_summary
		// where Date=current_date();
		// select date,AVG(aggregateDQI) from
		// databuck_results_db_development.data_quality_dashboard where
		// date=current_date();
		/*
		 * SELECT idApp FROM databuck_app_db_development.listapplications where
		 * project_id=61 and active='Yes';
		 * 
		 * select AVG(aggregateDQI) from
		 * databuck_app_db_development.data_quality_dashboard where date=CURDATE() and
		 * idApp in (id_apps);
		 */

		List<Idapp> IdApp = new ArrayList<>();
		try {

			String sql = "select idApp from listApplications where project_id = " + projectId
					+ " and active = 'Yes' order by idApp";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		// LOG.debug("text-->"+text);
		// select AVG(aggregateDQI) from
		// databuck_app_db_development.data_quality_dashboard where date=CURDATE() and
		// idApp in (id_apps);
		String sql = "select AVG(aggregateDQI) from data_quality_dashboard where date=CURDATE() and idApp in (" + text
				+ ") order by idApp";

		LOG.debug("---getAvgDQIForTodaysDtAllValidations------ Sql -----------" + sql);
		avgDqi = jdbcTemplate1.queryForObject(sql, String.class);

		if (avgDqi == null) {
			LOG.debug("In if.......");
			avgDqi = "0.0";
		}

		return avgDqi;
	}

	@Override
	public String getAvgDQIForTodaysDtAllValidationsAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate,
			String CurrentPeriodToDate) {

		String avgDqi = null;

		// select Date,AVG(DQI) from databuck_results_db_development.dashboard_summary
		// where Date=current_date();
		// select date,AVG(aggregateDQI) from
		// databuck_results_db_development.data_quality_dashboard where
		// date=current_date();
		/*
		 * SELECT idApp FROM databuck_app_db_development.listapplications where
		 * project_id=61 and active='Yes';
		 * 
		 * select AVG(aggregateDQI) from
		 * databuck_app_db_development.data_quality_dashboard where date=CURDATE() and
		 * idApp in (id_apps);
		 */

		List<Idapp> IdApp = new ArrayList<>();
		try {

			String sql = "select idApp from listApplications where project_id in (" + sSelectedProjects
					+ ") and active = 'Yes' order by idApp";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		LOG.debug("text-->" + text);

		// String sql = "select AVG(aggregateDQI) from data_quality_dashboard where
		// date=CURDATE() and idApp in ("+text+") order by idApp";

		/*
		 * mysql> select AVG(aggregateDQI) from
		 * databuck_results_db.data_quality_dashboard where date = (select max(date)
		 * from databuck_results_db.data_quality_dashboard order by date Desc) and IdApp
		 * in (3826,3832) order by IdApp;
		 */
		String sql = "select AVG(aggregateDQI) from  databuck_results_db.data_quality_dashboard where date ="
				+ "(select max(date) from databuck_results_db.data_quality_dashboard order by date Desc) and IdApp in ("
				+ sSelectedProjects + ") order by IdApp";

		// String sql = "select AVG(aggregateDQI) from data_quality_dashboard where date
		// between " +CurrentPeriodStartDate+" and "+CurrentPeriodToDate+" and IdApp in
		// (" +sSelectedProjects + ") order by IdApp";

		LOG.debug("---getAvgDQIForTodaysDtAllValidations------ Sql -----------" + sql);
		avgDqi = jdbcTemplate1.queryForObject(sql, String.class);

		if (avgDqi == null) {
			LOG.debug("In if.......");
			avgDqi = "0.0";
		}

		return avgDqi;
	}

	@Override
	public String getNoOfFailedValidationsForLast30Days(Long projectId) {

		String failedValCount = null;

		List<Idapp> IdApp = new ArrayList<>();
		try {

			String sql = "select idApp from listApplications where project_id = " + projectId
					+ " and active = 'Yes' order by idApp";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		// LOG.debug("text-->"+text);

		// select count(*) from databuck_results_db_development.dashboard_summary where
		// DQI <> 100 and Date between (CURDATE() - INTERVAL 1 MONTH ) and CURDATE();
		String sql = "select count(*) from data_quality_dashboard where aggregateDQI <> 100 and date between (CURDATE() - INTERVAL 1 MONTH ) and CURDATE() and idApp in ("
				+ text + ")";

		LOG.debug("---getNoOfFailedValidationsForLast30Days------ Sql -----------" + sql);
		failedValCount = jdbcTemplate1.queryForObject(sql, String.class);

		return failedValCount;
	}

	@Override
	public int getNoOfFailedValidationsForLast30DaysAsPerDate(String sSelectedProjects, String CurrentPeriodStartDate,
			String CurrentPeriodToDate) {

		/*
		 * getAverageDqIndexByProjectsAndPeriod(String sSelectedProjects, String
		 * sPeriodStartDate , String sPeriodToDate) select AVG(aggregateDQI) as
		 * AggregateDqIndex from data_quality_dashboard where date between '%1$s' and
		 * '%2$s' and idApp in (%3$s) order by idApp;
		 */

		int failedValCount = 0;

		List<Idapp> IdApp = new ArrayList<>();
		try {
			String sql = "select idApp from listApplications where project_id in (" + sSelectedProjects
					+ ") and active = 'Yes' order by idApp";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Idapp idapp = new Idapp();
				idapp.setIdApp(queryForRowSet.getLong("idApp"));
				IdApp.add(idapp);
			}

			String text = IdApp.toString().replace("[", "").replace("]", "");
			// LOG.debug("text-->"+text);

			// select count(*) from databuck_results_db_development.dashboard_summary where
			// DQI <> 100 and Date between (CURDATE() - INTERVAL 1 MONTH ) and CURDATE();
			// String sql = "select count(*) from data_quality_dashboard where aggregateDQI
			// <> 100 and date between (CURDATE() - INTERVAL 1 MONTH ) and CURDATE() and
			// idApp in ("+text+")";

			String sql1 = "select count(*) as Count from data_quality_dashboard where aggregateDQI <> 100 and date between '"
					+ CurrentPeriodStartDate + "' and '" + CurrentPeriodToDate + "' and idApp in (" + sSelectedProjects
					+ ")";
			LOG.debug("---getNoOfFailedValidationsForLast30Days------ Sql -----------" + sql1);
			String strFailedValCount = jdbcTemplate1.queryForObject(sql1, String.class);

			if (strFailedValCount == null) {
				strFailedValCount = "0";
			}

			failedValCount = Integer.parseInt(strFailedValCount);

			if (failedValCount == 0) {
				failedValCount = 0;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return failedValCount;
	}

	@Override
	public String getTotalNoOfSourcesForCurrentProjectByProjectId(String sSelectedProjects) {

		String noOfSources = null;

		// select count(*) from databuck_app_db_development.listdatasources where
		// project_id=61;

		String sql = "select count(*) from listDataSources where project_id in (" + sSelectedProjects
				+ ") and active = 'Yes'";

		LOG.debug("---getTotalNoOfSourcesForCurrentProjectByProjectId------ Sql -----------" + sql);
		noOfSources = jdbcTemplate.queryForObject(sql, String.class);

		return noOfSources;
	}

	@Override
	public int getTotalNoOfSourcesForCurrentProjectByProjectIdAsPerDate(String sSelectedProjects,
			String CurrentPeriodStartDate, String CurrentPeriodToDate) {

		int noOfSources = 0;

		// select count(*) from databuck_app_db_development.listdatasources where
		// project_id=61;

		String sql = "select count(*) from listDataSources where project_id in (" + sSelectedProjects
				+ ") and createdAt between '" + CurrentPeriodStartDate + "' and '" + CurrentPeriodToDate
				+ "' and active = 'Yes'";

		LOG.debug("---getTotalNoOfSourcesForCurrentProjectByProjectId------ Sql -----------" + sql);
		String strNoOfSources = jdbcTemplate.queryForObject(sql, String.class);
		noOfSources = Integer.parseInt(strNoOfSources);

		return noOfSources;
	}

	@Override
	public String getTotalNoOfValidationsForCurrentProjectByProjectId(Long projectId) {

		String noOfvalidations = null;

		// select count(*) from databuck_app_db_development.listapplications where
		// project_id=61;

		String sql = "select count(*) from listApplications where project_id =" + projectId + " and active = 'Yes'";

		LOG.debug("---getTotalNoOfValidationsForCurrentProjectByProjectId------ Sql -----------" + sql);
		noOfvalidations = jdbcTemplate.queryForObject(sql, String.class);

		return noOfvalidations;

	}

	@Override
	public int getTotalNoOfValidationsForCurrentProjectByProjectIdAsPerDate(String sSelectedProjects,
			String CurrentPeriodStartDate, String CurrentPeriodToDate) {

		int noOfvalidations = 0;

		// select count(*) from databuck_app_db_development.listapplications where
		// project_id=61;

		/// String sql = "select count(*) from listDataSources where project_id in (" +
		/// sSelectedProjects + ") and createdAt between " + CurrentPeriodStartDate +
		/// "and "+CurrentPeriodToDate+" active = 'Yes'";

		String sql = "select count(*) from listApplications where project_id in (" + sSelectedProjects
				+ ") and createdAt between '" + CurrentPeriodStartDate + "' and '" + CurrentPeriodToDate
				+ "' and active = 'Yes'";

		LOG.debug("---getTotalNoOfValidationsForCurrentProjectByProjectId------ Sql -----------" + sql);
		String strNoOfvalidations = jdbcTemplate.queryForObject(sql, String.class);

		noOfvalidations = Integer.parseInt(strNoOfvalidations);
		return noOfvalidations;

	}

	@Override
	public List<String> getRefTableColValue(Long idData, String columnname, String tablename) {
		try {
			String sql = "SELECT " + columnname + " FROM " + tablename;

			List<String> listMicrosegval = jdbcTemplate2.query(sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(columnname);
				}
			});
			return listMicrosegval;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String updateRefTableData(Long row_id, String col_name, String update_val, String table_name) {
		try {
			String sql_type = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table_name
					+ "' AND COLUMN_NAME='" + col_name + "'";
			SqlRowSet oSqlResult = jdbcTemplate1.queryForRowSet(sql_type);
			String Type = "";
			if (oSqlResult.next()) {
				Type = oSqlResult.getString("DATA_TYPE");
			}
			LOG.debug("Type.." + Type);
			if (Type.toLowerCase().contains("char") || Type.toLowerCase().contains("date") || Type.toLowerCase().contains("time") || Type.toLowerCase().contains("text")) {
				update_val = "'" + update_val + "'";
			} else if (update_val == null || update_val.trim().isEmpty()) {
				update_val = null;
			}

			String sql = " update " + table_name + " set " + col_name + "=" + update_val + " where dbk_row_Id="
					+ row_id;
			LOG.debug("\n===>update sql: " + sql);

			jdbcTemplate1.execute(sql);
			return "True";
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public String insertNewValueToRefTable(String columnName, String columnValue, String table_name) {
		String insertvalues = "";
		String[] data = columnValue.split("\\$\\$", -1);
		String insertColumns = columnName.replaceAll("\\$\\$", ",");

		// Get metadata
		Map<String, String> metadata = new HashMap<String, String>();
		try {
			String sql = "Select " + insertColumns + " from " + table_name + " limit 1";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData sqlRowSetMetaData = sqlRowSet.getMetaData();
			for (int i = 1; i <= sqlRowSetMetaData.getColumnCount(); i++) {
				metadata.put(sqlRowSetMetaData.getColumnName(i), sqlRowSetMetaData.getColumnTypeName(i));
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n===> metadata: " + metadata);

		// Get columnName
		String[] columnNames = insertColumns.split(",");

		int empty_values_count = 0;
		for (int i = 0; i <= data.length - 1; i++) {
			String colValue = data[i];
			if(colValue!=null && (colValue.contains("'") || colValue.contains("\""))){
				colValue = colValue.replace("'", "''");
				colValue = colValue.replace("\"", "\"");
			}
			String colName = columnNames[i];
			String colDataType = metadata.get(colName);

			if (colValue == null || colValue.trim().isEmpty())
				++empty_values_count;

			if (colDataType.equalsIgnoreCase("date") || colDataType.toLowerCase().startsWith("varchar")) {
				String val = "'" + colValue + "',";
				insertvalues = insertvalues + val;

			} else {
				if (colValue == null || colValue.trim().isEmpty())
					colValue = null;
				insertvalues = insertvalues + colValue + ",";
			}

		}
		LOG.debug("values--->" + insertvalues);

		if (empty_values_count < columnNames.length && insertvalues.length() > 0) {

			insertvalues = insertvalues.substring(0, insertvalues.length() - 1);
			LOG.debug("values--->" + insertvalues);

			try {
				String sql = "INSERT INTO " + table_name + "(" + insertColumns + ") VALUES (" + insertvalues + ")";
				LOG.debug("\n===> InsertQuery: " + sql);
				jdbcTemplate1.execute(sql);
				return "True";
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public List<String> getMicrosegmentColValue(String tableName, String idApp) {
		try {
			if (tableName.trim().length() > 0 && !tableName.equals("")) {
				List<String> listdGroupVal = new ArrayList<String>();

				LOG.info("\n====>ResultsDAOImpl : getMicrosegmentColValue ....");
				String sql = "SELECT dGroupVal FROM " + tableName + " where idApp = " + idApp
						+ " GROUP BY dGroupVal ORDER BY dGroupVal";
				LOG.debug("\n===>Sql: " + sql);

				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

				while (queryForRowSet.next()) {
					listdGroupVal.add(queryForRowSet.getString("dGroupVal"));
				}
				LOG.debug(
						"ResultsDAOImpl : getMicrosegmentColValue : listdGroupVal :: " + listdGroupVal.toString());
				return listdGroupVal;
			} else {
				LOG.debug(
						"\n====>ResultsDAOImpl : getMicrosegmentColValue === tablename is null, returning null");
				return null;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getListOfLocationsbyProject(int projectId) {
		LOG.info(" UserServiceImpl : getListOfLocationsbyProject  ");
		try {
			if (projectId > 0) {
				LOG.debug(" UserServiceImpl : getListOfLocationsbyProject : projectId :: " + projectId);
				List<String> listLocationName = new ArrayList<String>();

				String sql = "SELECT l.locationName FROM locations as l "
						+ "				INNER JOIN locationMapping AS lm "
						+ "					ON l.id = lm.locationId " + "				WHERE projectid = " + projectId
						+ " GROUP BY l.locationName ORDER BY l.locationName";
				LOG.debug(" UserServiceImpl : getListOfLocationsbyProject : Query :: " + sql);
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

				while (queryForRowSet.next()) {
					listLocationName.add(queryForRowSet.getString("locationName"));
				}
				LOG.debug(
						" UserServiceImpl : getListOfLocationsbyProject : listLocationName :: " + listLocationName);
				return listLocationName;

			} else {
				LOG.error(
						" UserServiceImpl : getListOfLocationsbyProject  : Custome Error :: Project ID is not valid");
				return null;
			}
		} catch (Exception e) {
			LOG.error(" UserServiceImpl : getListOfLocationsbyProject  : Exception :: " + e.getMessage());
			return null;
		}
	}

	private String getDateAndDQI(String paramIntIDApp) {
		try {
			LOG.debug("ResultsDAOImpl : getLocatoinInfoTableData : paramIntIDApp :: " + paramIntIDApp);
			StringBuilder strBuildCellData = new StringBuilder();

			// Query compatibility changes for both POSTGRES and MYSQL
			String strQuery = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				strQuery = "SELECT ds.date AS dsdate, COALESCE(ds.DQI, 0) AS dqi  FROM DashBoard_Summary AS ds WHERE ds.AppId = "
						+ paramIntIDApp + " ORDER BY ds.Run DESC LIMIT 1";
			else
				strQuery = "SELECT ds.date AS dsdate, IFNULL(ds.DQI, 0) AS dqi  FROM DashBoard_Summary AS ds WHERE ds.AppId = "
						+ paramIntIDApp + " ORDER BY ds.Run DESC LIMIT 1";

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (queryForRowSet.next()) {
				String[] randomStrings = new String[44];
				Random random = new Random();
				for (int i = 0; i < 4; i++) {
					char[] word = new char[random.nextInt(8) + 3]; // words of length 3 through 10. (1 and 2 letter
																	// words are boring.)
					for (int j = 0; j < word.length; j++) {
						word[j] = (char) ('a' + random.nextInt(26));
					}
					randomStrings[i] = new String(word);
				}

				String strRandom = String.join("", randomStrings);

				String strHTMLTableCode = getValidationDetailInformation(Integer.parseInt(paramIntIDApp));
				LOG.debug(
						"ResultsDAOImpl : getLocatoinInfoTableData : Internal Table Code :: " + strHTMLTableCode);
				String strDate = queryForRowSet.getString("dsdate");
				Double dblDQI = queryForRowSet.getDouble("dqi");
				LOG.debug("ResultsDAOImpl : getLocatoinInfoTableData : paramIntIDApp :: " + paramIntIDApp
						+ " | Date :: " + strDate + " | DQI :: " + dblDQI);
				double roundOff = Math.round(dblDQI * 100.0) / 100.0;
				if (dblDQI <= 50) {
					strBuildCellData.append("<td class='alert alert-danger' id='table-cell-" + paramIntIDApp + "-"
							+ strRandom + "' onclick='callFuncation1(" + paramIntIDApp + ")'>  " + strDate + " <strong>"
							+ roundOff + "% </strong> <div id='parent-div-data-" + paramIntIDApp + "-" + strRandom
							+ "' class='paren-div-class' style='float:right; cursor: pointer; ' onclick='callFuncation(this)'><i class='fa fa-info-circle' aria-hidden='true'></i>"
							+ strHTMLTableCode + "</div></td>");
				} else if (dblDQI <= 90 && dblDQI > 50) {
					strBuildCellData.append("<td class='alert alert-warning' id='table-cell-" + paramIntIDApp + "-"
							+ strRandom + "' onclick='callFuncation1(" + paramIntIDApp + ")'> " + strDate + " <strong>"
							+ roundOff + "% </strong> <div id='parent-div-data-" + paramIntIDApp + "-" + strRandom
							+ "' class='paren-div-class' style='float:right; cursor: pointer; ' onclick='callFuncation(this)'><i class='fa fa-info-circle' aria-hidden='true'></i>"
							+ strHTMLTableCode + "</div></td>");
				} else if (dblDQI > 90) {
					strBuildCellData.append("<td class='alert alert-success' id='table-cell-" + paramIntIDApp + "-"
							+ strRandom + "' onclick='callFuncation1(" + paramIntIDApp + ")'> " + strDate + " <strong>"
							+ roundOff + "% </strong> <div id='parent-div-data-" + paramIntIDApp + "-" + strRandom
							+ "' class='paren-div-class' style='float:right; cursor: pointer; ' onclick='callFuncation(this)'><i class='fa fa-info-circle' aria-hidden='true'></i>"
							+ strHTMLTableCode + "</div></td>");
				} else {
					strBuildCellData.append("<td class='alert alert-danger' id='table-cell-" + paramIntIDApp + "-"
							+ strRandom + "' onclick='callFuncation1(" + paramIntIDApp
							+ ")'> No Data Found <div id='parent-div-data-" + paramIntIDApp + "-" + strRandom
							+ "' class='paren-div-class' style='float:right; cursor: pointer; margin-left: 5%;' onclick='callFuncation(this)'><i class='fa fa-info-circle' aria-hidden='true'></i>"
							+ strHTMLTableCode + "</div> </td>");
				}
				LOG.debug("ResultsDAOImpl : getLocatoinInfoTableData : paramIntIDApp :: " + paramIntIDApp
						+ " | Cell Data ::" + strBuildCellData.toString());
				return strBuildCellData.toString();
			}
		} catch (Exception e) {
			LOG.error("ResultsDAOImpl : getLocatoinInfoTableData : Exception :: " + e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, List<String>> getLocatoinInfoTableData(List<String> listLocationName) {
		try {
			LOG.info(" ResultsDAOImpl : getLocatoinInfoTableData");

			Map<String, List<String>> maplocationInfoTableData = new LinkedHashMap<String, List<String>>();
			List<String> listColumns = new ArrayList<String>();
			int intLocationCounter = 1;
			for (String strLocalItr : listLocationName) {
				List<String> listValidationColumnData = new ArrayList<String>();
				List<String> listValidation = new ArrayList<String>();
				String sql = "	SELECT lm.idApp as lidApp FROM locationMapping AS lm "
						+ "		INNER JOIN listApplications la " + "			ON lm.idApp = la.idApp "
						+ "		INNER JOIN locations ls " + "			ON lm.locationId = ls.id "
						+ "			WHERE ls.locationName = '" + strLocalItr + "'";

				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
				int intWhileItr = 1;
				while (queryForRowSet.next()) {
					String strValidationName = queryForRowSet.getInt("lidApp") + "";
					LOG.debug(
							"ResultsDAOImpl : getLocatoinInfoTableData : strValidationName load into variable :: "
									+ strValidationName);
					if (maplocationInfoTableData.isEmpty() && listValidationColumnData.isEmpty()) { // insert first cell
																									// for first column
						// String strCellData = getDateAndDQI(strValidationName);
						listValidationColumnData.add(strValidationName);
					} else {
						int intBlankPosCount = 0;
						List<String> listValidations = new ArrayList<String>();
						if (maplocationInfoTableData.isEmpty() && !listValidationColumnData.isEmpty()) { // to insert
																											// first
																											// element
							LOG.debug(" ResultsDAOImpl : getLocatoinInfoTableData listValidationColumnData :: "
									+ listValidationColumnData);
							String strLastValidationName = listValidationColumnData
									.get(listValidationColumnData.size() - 1); // get last validation
							LOG.debug(" ResultsDAOImpl : getLocatoinInfoTableData  : strLastValidationName :: "
									+ strLastValidationName);
							List<String> listCopyOflistValidationColumnData = new ArrayList<String>();
							listValidations.add(strLastValidationName);
							intBlankPosCount = getBlankPositionExtension(0, listValidations);
							LOG.debug(" ResultsDAOImpl : getLocatoinInfoTableData  : intBlankPosCount :: "
									+ intBlankPosCount);
							listCopyOflistValidationColumnData = insertNullDataIntoValidationList(
									listValidationColumnData, intBlankPosCount, strValidationName);
							LOG.debug(
									" ResultsDAOImpl : getLocatoinInfoTableData  : listCopyOflistValidationColumnData :: "
											+ listCopyOflistValidationColumnData);
							listValidationColumnData.clear();
							listValidationColumnData.addAll(listCopyOflistValidationColumnData);
						} else { // insert data into second column onwords
							// if(listValidationColumnData.isEmpty()) { //1. insert first cell data from
							// second column onwords
							LOG.debug(
									"ResultsDAOImpl : getLocatoinInfoTableData : inserting data for second column onwards");
							boolean booleanCheckMappingStatus = false;
							List<String> listCloneValidationDataWithBlankPosition = new ArrayList<String>();
							int intCountMapIteration = 0;
							for (Entry<String, List<String>> entry : maplocationInfoTableData.entrySet()) {
								LOG.debug(
										"ResultsDAOImpl : getLocatoinInfoTableData : iterate map to compare with previous columns");
								List<String> listTableColumnsValidations = entry.getValue();
								for (int i = 0; i < listTableColumnsValidations.size(); i++) {
									List<String> listCopyOflistValidationColumnData = new ArrayList<String>();
									if (checkValidationMappingExistsOrNot(listTableColumnsValidations.get(i),
											strValidationName)) {
										booleanCheckMappingStatus = true;
										LOG.debug(
												"ResultsDAOImpl : getLocatoinInfoTableData : mapped condition true with previous columns :: "
														+ strValidationName);
										listCopyOflistValidationColumnData = insertNullDataIntoValidationList(
												listValidationColumnData, i, strValidationName);
										listValidationColumnData.clear();
										listValidationColumnData.addAll(listCopyOflistValidationColumnData);
										break;
									} else if (i == (listTableColumnsValidations.size() - 1)
											&& booleanCheckMappingStatus == false
											&& (maplocationInfoTableData.size() - 1) == intCountMapIteration) {
										LOG.debug(
												"ResultsDAOImpl : getLocatoinInfoTableData : mapped condition false with previous columns :: "
														+ strValidationName);
										String strlistValidationColumnDataLastElement = "";
										if (listValidationColumnData.size() > 0) {
											strlistValidationColumnDataLastElement = listValidationColumnData
													.get(listValidationColumnData.size() - 1);
											LOG.debug(
													"last element ::" + strlistValidationColumnDataLastElement);
										}
										if (!strlistValidationColumnDataLastElement.equals("nulldata")
												&& !strlistValidationColumnDataLastElement.isEmpty()) {
											LOG.debug("one after another");
											listValidationColumnData.add(strValidationName);
											break;
										} else {
											LOG.debug("no one after another");
											listCopyOflistValidationColumnData = insertNullDataIntoValidationList(
													listValidationColumnData, i + 1, strValidationName);
											LOG.debug("listValidationColumnData :: " + listValidationColumnData);
											LOG.debug("listCopyOflistValidationColumnData :: "
													+ listCopyOflistValidationColumnData.size());
											listValidationColumnData.clear();
											LOG.debug(
													"ResultsDAOImpl : getLocatoinInfoTableData : listCopyOflistValidationColumnData :: "
															+ listCopyOflistValidationColumnData);
											listValidationColumnData.addAll(listCopyOflistValidationColumnData);
										}

									}
									LOG.debug(
											"ResultsDAOImpl : getLocatoinInfoTableData : listCloneValidationDataWithBlankPosition :: "
													+ listCloneValidationDataWithBlankPosition);
									if (listCloneValidationDataWithBlankPosition.isEmpty()) {
										LOG.debug(
												"ResultsDAOImpl : getLocatoinInfoTableData : listValidationColumnData cloned ");
										listCloneValidationDataWithBlankPosition.addAll(listValidationColumnData);
									}
									for (int k = 0; k < listValidationColumnData.size(); k++) {
										for (int j = k + 1; j < listCloneValidationDataWithBlankPosition.size(); j++) {
											if (!listValidationColumnData.get(k).equals("nulldata")
													&& listCloneValidationDataWithBlankPosition.get(j)
															.equals("nulldata")) { // compare list.get(i) and
												// list.get(j)
												listCloneValidationDataWithBlankPosition.set(j,
														listValidationColumnData.get(k));
												listValidationColumnData.clear();
												listValidationColumnData
														.addAll(listCloneValidationDataWithBlankPosition);
											} else if (listValidationColumnData.get(k)
													.equals(listCloneValidationDataWithBlankPosition.get(j))) {
												listCloneValidationDataWithBlankPosition.set(j,
														listValidationColumnData.get(k));
												listValidationColumnData.clear();
												listValidationColumnData
														.addAll(listCloneValidationDataWithBlankPosition);
											}

										}
									}
									LOG.debug(
											"ResultsDAOImpl : getLocatoinInfoTableData : after clone listValidationColumnData :: "
													+ listValidationColumnData);
								}
								intCountMapIteration++;
							}

							// }/*else {
							/*
							 * System.out.
							 * println("ResultsDAOImpl : getLocatoinInfoTableData : columnhave more than 1 validations"
							 * ); System.out.
							 * println("ResultsDAOImpl : getLocatoinInfoTableData : listValidations :: "
							 * +listValidations); String strLastValidationName =
							 * listValidationColumnData.get(listValidationColumnData.size()-1); // get last
							 * validation System.out.
							 * println("ResultsDAOImpl : getLocatoinInfoTableData : strLastValidationName :: "
							 * +strLastValidationName); List<String> listCopyOflistValidationColumnData =
							 * new ArrayList<String>(); listValidations.add(strLastValidationName);
							 * intBlankPosCount = getBlankPositionExtension(0,listValidations);
							 * listCopyOflistValidationColumnData =
							 * insertNullDataIntoValidationList(listValidationColumnData, intBlankPosCount,
							 * strLastValidationName); listValidationColumnData.clear();
							 * listValidationColumnData.addAll(listCopyOflistValidationColumnData); }
							 */
						}
					}
					intWhileItr++;
				}
				maplocationInfoTableData.put(strLocalItr, listValidationColumnData);
				LOG.debug(" ResultsDAOImpl : getLocatoinInfoTableData  : maplocationInfoTableData :: "
						+ maplocationInfoTableData);
				intLocationCounter++;
			}
			return maplocationInfoTableData;
		} catch (Exception e) {
			LOG.error(" ResultsDAOImpl : getLocatoinInfoTableData  : Exception :: " + e.getMessage());
			return null;
		}
	}

	private int getBlankPositionExtension(int paramIntBlankPositionExtension, List<String> paramListValidationNames) {
		try {
			int intIncrementInBlanckPositionExtension = 0;
			LOG.debug(" ResultDAOImpl : getBlankPositionExtension : paramIntBlankPositionExtension ::"
					+ paramIntBlankPositionExtension + " | paramListValidationNames :: " + paramListValidationNames);
			List<String> listMappedValidationNames = new ArrayList<String>();
			if (paramListValidationNames.isEmpty()) {
				LOG.debug("ResultDAOImpl : getBlankPositionExtension : returning total Blank Position count :: "
								+ paramIntBlankPositionExtension);
				return paramIntBlankPositionExtension;
			} else {
				intIncrementInBlanckPositionExtension = paramIntBlankPositionExtension;
				for (String strLocalForLoopValidationName : paramListValidationNames) {
					String sql = "SELECT COUNT(vm.relationIdApp) FROM validationMapping AS vm "
							+ "			INNER JOIN listApplications AS la " + "			ON vm.idApp = la.idApp "
							+ "			WHERE la.idApp = '" + strLocalForLoopValidationName + "'";
					SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
					int intCountRows = 0;
					while (queryForRowSet.next()) {
						intCountRows = queryForRowSet.getInt(1);
					}

					if (intCountRows > 0) {
						LOG.debug(
								"ResultDAOImpl : getBlankPositionExtension : if row count > 0 : intCountRows :: "
										+ intCountRows);
						String strSqlQeryToGetValidationNames = " SELECT la.idApp vname FROM validationMapping AS vm "
								+ "				INNER JOIN listApplications AS la "
								+ "				ON vm.relationIdApp = la.idApp " + "				WHERE "
								+ "				vm.idApp = "
								+ "( SELECT lac.idApp FROM listApplications lac WHERE lac.idApp = '"
								+ strLocalForLoopValidationName + "')";
						SqlRowSet queryForRowSetToGetValidationNames = jdbcTemplate
								.queryForRowSet(strSqlQeryToGetValidationNames);
						while (queryForRowSetToGetValidationNames.next()) {
							listMappedValidationNames.add(queryForRowSetToGetValidationNames.getInt("vname") + "");
						}
						intIncrementInBlanckPositionExtension = intIncrementInBlanckPositionExtension
								+ (intCountRows - 1);
					} else {
						intIncrementInBlanckPositionExtension = intIncrementInBlanckPositionExtension + (intCountRows);
					}

				}

				return getBlankPositionExtension(intIncrementInBlanckPositionExtension, listMappedValidationNames);
			}

		} catch (Exception e) {
			LOG.error(" ResultDAOImpl : getListOfLocationsbyProject  : Exception :: " + e.getMessage());
			return 0;
		}

	}

	private List<String> insertNullDataIntoValidationList(List<String> paramListValidations,
			int paramIntBlanckPositions, String paramStrValidationName) {
		List<String> listValidations = new ArrayList<String>();
		try {
			listValidations.addAll(paramListValidations);
			LOG.debug(" ResultDAOImpl : getBlankPositionExtension : insertNullDataIntoValidationList "
					+ paramListValidations);
			for (int i = 0; i < paramIntBlanckPositions; i++) {
				listValidations.add("nulldata");
			}
			listValidations.add(paramStrValidationName);
			LOG.debug("ResultDAOImpl : getBlankPositionExtension : paramListValidations :: " + listValidations);
			return listValidations;
		} catch (Exception e) {
			LOG.error(" ResultDAOImpl : getListOfLocationsbyProject  : Exception :: " + e.getMessage());
			return listValidations;
		}
	}

	private boolean checkValidationMappingExistsOrNot(String paramStrPreviousColumnValidation,
			String paramStrValidation) {
		try {
			LOG.debug("ResultDAOImpl : checkValidationMappingExistsOrNot : paramStrPreviousColumnValidation :: "
							+ paramStrPreviousColumnValidation + " | paramStrValidation :: " + paramStrValidation);
			String sql = "SELECT count(vm.relationIdApp) FROM validationMapping vm "
					+ "INNER JOIN listApplications AS la " + "ON vm.relationIdApp = la.idApp AND la.idApp = '"
					+ paramStrValidation + "' " + // validation 2
					"WHERE vm.idApp= (SELECT l.idApp FROM listApplications AS l WHERE l.idApp='"
					+ paramStrPreviousColumnValidation + "')"; // validation 3
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			int intCountRows = 0;
			while (queryForRowSet.next()) {
				intCountRows = queryForRowSet.getInt(1);
			}
			return intCountRows > 0;
		} catch (Exception e) {
			LOG.error(" ResultDAOImpl : checkValidationMappingExistsOrNot  : Exception :: " + e.getMessage());
			return false;
		}
	}

	public List<List<String>> formatedDataForTable(Map<String, List<String>> mapTableData) {
		try {
			LOG.debug("ResultsDAOImpl : formatedDataForTable ... Entered");
			List<String> listColumnHeader = new ArrayList<String>();
			List<List<String>> listFormatedDataForTable = new ArrayList<List<String>>();
			for (Entry<String, List<String>> iterable_element : mapTableData.entrySet()) {
				listColumnHeader.add(iterable_element.getKey());
			}
			LOG.debug("ResultsDAOImpl : formatedDataForTable : listColumnHeader :: " + listColumnHeader);
			listFormatedDataForTable.add(listColumnHeader);
			List<String> listFirstColumn = mapTableData.get(listColumnHeader.get(0));
			int intGetMaxCellCountOfColumn = listFirstColumn.size();
			LOG.debug(
					"ResultsDAOImpl : formatedDataForTable : sizeof first element : intGetMaxCellCountOfColumn :: "
							+ intGetMaxCellCountOfColumn);
			for (Entry<String, List<String>> iterable_element : mapTableData.entrySet()) {
				int intLocalListSize = iterable_element.getValue().size();
				if (intLocalListSize > intGetMaxCellCountOfColumn) {
					intGetMaxCellCountOfColumn = intLocalListSize;
				}
			}
			LOG.debug("ResultsDAOImpl : formatedDataForTable : maxsize : intGetMaxCellCountOfColumn :: "
					+ intGetMaxCellCountOfColumn);
			for (int i = 0; i < intGetMaxCellCountOfColumn; i++) {
				List<String> listRowData = new ArrayList<String>();
				for (int j = 0; j < listColumnHeader.size(); j++) {
					if (i < mapTableData.get(listColumnHeader.get(j)).size()) {
						List<String> listColumn = mapTableData.get(listColumnHeader.get(j));
						LOG.debug(" column data :: " + listColumn);
						LOG.debug("ResultsDAOImpl : formatedDataForTable : Row " + (i + 1) + " | Column :: "
								+ (j + 1) + " | Cell Data " + listColumn.get(i));
						listRowData.add(listColumn.get(i));
					} else {
						LOG.debug("ResultsDAOImpl : formatedDataForTable : Row " + (i + 1) + " | Column :: "
								+ (j + 1) + " | Cell Data :: nulldata");
						listRowData.add("nulldata");

					}
				}
				listFormatedDataForTable.add(listRowData);
			}
			List<String> listFormatedHeader = new ArrayList<String>();
			int intHeaderCount = 0;
			for (String strhead : listColumnHeader) {
				if (intHeaderCount == 0)
					listFormatedHeader.add(
							"<th class='text-info text-center' style='padding-top:5%; padding-bottom:5%; background: ghostwhite;'> </th>");
				listFormatedHeader.add(
						"<th class='text-info text-center' style='padding-top:5%; padding-bottom:5%; background: ghostwhite;'>"
								+ strhead + "</th>");
				intHeaderCount++;
			}
			List<List<String>> listOfListRows = new ArrayList<List<String>>();
			int intListCounter = 0;
			for (List<String> listOfString : listFormatedDataForTable) {
				List<String> listRows = new ArrayList<String>();
				if (intListCounter > 0) {
					int intColumnCounter = 0;
					for (String strIdApp : listOfString) {
						String strCellData = null;
						if (listRows.isEmpty()) {
							if (!strIdApp.equals("nulldata")) {
								if (intColumnCounter == 0 && intListCounter == 1)
									listRows.add("<td style='color: #5b9bd1;'> <h5>Universe</h5></td>");
								else if (intColumnCounter == 0 && intListCounter >= 2)
									listRows.add("<td style='color: #5b9bd1;'><h5> Prescription File</h5></td>");
								else
									listRows.add("<td> </td>");
								intColumnCounter++;
							} else {
								listRows.add("<td> </td>");
							}

						}
						if (!strIdApp.equals("nulldata")) {
							strCellData = getDateAndDQI(strIdApp);
						} else {
							strCellData = "<td> </td>";
						}
						listRows.add(strCellData);
					}
					listOfListRows.add(listRows);
				} else if (intListCounter == 0) {
					listOfListRows.add(listFormatedHeader);
				}

				intListCounter++;
			}
			return listOfListRows;
		} catch (Exception e) {
			LOG.error("ResultsDAOImpl : formatedDataForTable :: Exception " + e.getMessage());
			return null;
		}

	}

	public String getValidationDetailInformation(int paramIntIdApp) {
		try {
			LOG.debug("ResultsDAOImpl : getValidationDetailInformation : paramIntIdApp :: " + paramIntIdApp);
			String sql = "select * from data_quality_dashboard where idApp =" + paramIntIdApp + " order by idapp";
			LOG.debug("select * from data_quality_dashboard where idApp =" + paramIntIdApp + " order by idapp");
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			StringBuffer strBufTableTag = new StringBuffer();
			strBufTableTag.append("<Table class='table-bordered'>");
			strBufTableTag.append("<tr>");
			strBufTableTag.append(
					"<th>IdApp</th><th>Date</th><th>Run</th><th>Validation Check Name</th><th>Source Name</th>");
			strBufTableTag.append(
					"<th>Aggregate DQI</th><th>Record Count Status</th><th>Null Count Status</th><th>Primary Key Status</th>");
			strBufTableTag.append(
					"<th>User Selected Field Status</th> <th>Numerical Field Status</th><th>Record Anomally Status</th><th>Data Drift Status</th>");
			strBufTableTag.append("</tr>");

			while (queryForRowSet.next()) {
				LOG.debug(
						"ResultsDAOImpl : getValidationDetailInformation : idApp :: " + queryForRowSet.getLong("idApp")
								+ " | validationCheckName :: " + queryForRowSet.getString("validationCheckName"));
				strBufTableTag.append("<tr>");
				strBufTableTag.append("<td>" + queryForRowSet.getLong("idApp") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("date") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getLong("run") + "</td>");
				strBufTableTag.append("<td><a href='dashboard_table?idApp=" + queryForRowSet.getLong("idApp") + "'> "
						+ queryForRowSet.getString("validationCheckName") + "</a></td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("sourceName") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getDouble("aggregateDQI") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("recordCountStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("nullCountStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("primaryKeyStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("userSelectedFieldStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("numericalFieldStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("recordAnomalyStatus") + "</td>");
				strBufTableTag.append("<td>" + queryForRowSet.getString("dataDriftStatus") + "</td>");
				strBufTableTag.append("</tr>");

			}

			strBufTableTag.append("</table>");

			StringBuffer strUpperCoverData = new StringBuffer();
			strUpperCoverData.append("<div class='hidden' id='table-cell-data'> <div id='table-cell-data2'>");
			strUpperCoverData.append(strBufTableTag);
			strUpperCoverData.append("</div></div>");
			
			return strUpperCoverData.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	public String getApprovalStatusFlag(Long idApp) {

		int rowId = 0, count = 0;
		String reviewStatusSql, approveStatusSql, approvalStatusFlag = "";

		/* Query to check the review records present in the log table */
		reviewStatusSql = "";
		reviewStatusSql = reviewStatusSql + "select a.row_id\n";
		reviewStatusSql = reviewStatusSql
				+ "from data_quality_approval_log a, app_option_list b, app_option_list_elements c\n";
		reviewStatusSql = reviewStatusSql + "where a.action_state = c.row_id\n";
		reviewStatusSql = reviewStatusSql + "and b.list_reference = 'DQ_REVIEW_STATUS'\n";
		reviewStatusSql = reviewStatusSql + "and c.active > 0\n";
		reviewStatusSql = reviewStatusSql + "and a.idApp = " + idApp + "\n";
		reviewStatusSql = reviewStatusSql + "order by a.action_date desc\n";
		reviewStatusSql = reviewStatusSql + "limit 1;";

		/* Query to check the approve records present in the log table */
		approveStatusSql = "";
		approveStatusSql = approveStatusSql + "select count(*) as Count\n";
		approveStatusSql = approveStatusSql
				+ "from data_quality_approval_log a, app_option_list b, app_option_list_elements c\n";
		approveStatusSql = approveStatusSql + "where a.action_state = c.row_id\n";
		approveStatusSql = approveStatusSql + "and b.list_reference = 'DQ_APPROVE_STATUS'\n";
		approveStatusSql = approveStatusSql + "and c.element_reference = 'APPROVED'\n";
		approveStatusSql = approveStatusSql + "and c.active > 0\n";
		approveStatusSql = approveStatusSql + "and a.idApp = " + idApp + "\n";
		approveStatusSql = approveStatusSql + "and a.run = (select run from data_quality_dashboard where IdApp = "
				+ idApp + ");";

		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(reviewStatusSql);
		SqlRowSet queryForRowSet2 = jdbcTemplate1.queryForRowSet(approveStatusSql);

		if (queryForRowSet.next()) {
			queryForRowSet.first();
			rowId = queryForRowSet.getInt("row_id");
		}

		if (queryForRowSet2.next()) {
			queryForRowSet2.first();
			count = queryForRowSet2.getInt("Count");
		}

		/* Decision making what to passed as approvalStatusFlag hardCoded for demo */
		if (count > 0) {
			approvalStatusFlag = "Approved";
			return approvalStatusFlag;
		} else {
			if (rowId >= 1) {
				approvalStatusFlag = "Reviewed";
				return approvalStatusFlag;
			} else {
				approvalStatusFlag = "Not Started";
				return approvalStatusFlag;
			}
		}
	}

	// Added to get Validation Run details - For Rule Catalog
	public Map<String, String> getValidationRunDetail(long nIdApp) {
		Map<String, String> retMap = new HashMap<String, String>();
		String Sql = "SELECT  RecordCount as TotalRecords , Date as ResultRunDate , Run as ResultRunNo from DATA_QUALITY_Transactionset_sum_A1  where idApp="
				+ nIdApp + " ORDER by Date Desc, Run Desc limit 1\n";
		Sql = String.format(Sql, nIdApp);
		LOG.debug("sql=" + Sql);
		SqlRowSet oSqlRowSet = jdbcTemplate1.queryForRowSet(Sql);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		while (oSqlRowSet.next()) {
			retMap.put("TotalRecords", oSqlRowSet.getString("TotalRecords"));
			retMap.put("ResultRunDate", formatter.format(oSqlRowSet.getDate("ResultRunDate")));
			retMap.put("ResultRunNo", oSqlRowSet.getString("ResultRunNo"));
		}
		return retMap;
	}

	@Override
	public SqlRowSet getRulesTableDataForExecDateRun(String tableName, long idApp, String execDate, long run) {
		try {
			String getRulesTableData = "select * from " + tableName + " where idApp=" + idApp + " and date='" + execDate
					+ "' and run=" + run;
			return jdbcTemplate1.queryForRowSet(getRulesTableData);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SqlRowSet getFrequencyUpdateDateTableDataForExecDateRun(String tableName, long idApp, String execDate,
			long run) {
		try {
			String frequencyUpdateDateTableData = "select Id,Date,Run,dayOfYear,month,dayOfMonth,dayOfWeek,hourOfDay,RecordCount,"
					+ "dGroupVal,dgroupCol,ROUND(RC_Std_Dev,2) AS RC_Std_Dev,ROUND(RC_Mean,2) AS RC_Mean,ROUND(dGroupDeviation,2) AS dGroupDeviation,dGroupRcStatus from "
					+ tableName + " where idApp=? and Date=? and Run=?";
			return jdbcTemplate1.queryForRowSet(frequencyUpdateDateTableData, idApp, execDate, run);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDashBoardStatusForGroupEqualityForExecDateRun(long idApp, String tableName, String execDate,
			long run) {
		try {
			String groupEquality = checkGroupEqualityInListApplicationsTable(idApp);
			if (groupEquality.equalsIgnoreCase("Y")) {
				String sql = "select groupEqualityStatus from " + tableName
						+ " where idApp=? and date=? and run=? LIMIT 1";
				String groupEqualityStatus = jdbcTemplate1.queryForObject(sql, String.class, idApp, execDate, run);
				return groupEqualityStatus;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Long getRecordCountForExecDateRun(ListApplications listApplicationsData, long idApp, String execDate,
			long run) {
		try {
			String tableName = "";
			String sql = "";
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("N")
					&& listApplicationsData.getBuildHistoricFingerPrint().equalsIgnoreCase("N")) {
				sql = "select RecordCount from " + tableName + " where idApp=? and Run=? and Date=? limit 1";
			} else {
				sql = "select sum(RecordCount) from " + tableName
						+ " where RecordCount IS NOT NULL and idApp=? and Run=? and Date=? limit 1";
			}
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, run, execDate);
			Long recordCount = 0l;
			while (queryForRowSet.next()) {
				recordCount = recordCount + queryForRowSet.getLong(1);
			}
			return recordCount;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public Long getAverageRecordCountForExecDateRun(ListApplications listApplications, long idApp, String execDate,
			long run) {
		try {
			String tableName = "";
			if (listApplications.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			String sql = "SELECT SUM(RecordCount)/COUNT(distinct Date, Run) FROM " + tableName + " where idApp=?";
			Long averageRecordCount = jdbcTemplate1.queryForObject(sql, Long.class, idApp);
			if (averageRecordCount == null)
				averageRecordCount = 0l;
			return averageRecordCount;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public long getRecordAnomalyTotalForExecDateRun(ListApplications listApplications, String tableName, long idApp,
			String execDate, long run) {
		String sql = "";
		try {
			if (listApplications.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				tableName = "DATA_QUALITY_Transactionset_sum_A1";
			} else {
				tableName = "DATA_QUALITY_Transactionset_sum_dgroup";
			}

			sql = "select RecordCount from " + tableName + " where idApp=? and run=? and date=? LIMIT 1";

			Long recordAnomalyTotal = jdbcTemplate1.queryForObject(sql, Long.class, idApp, run, execDate);
			return recordAnomalyTotal;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public Long getNumberOfRecordsFailedForExecDateRun(String tableName, long idApp, String execDate, long run) {
		String sql = "";
		try {
			sql = "select recordAnomalyCount FROM " + tableName + " where idApp=? and id=(select max(id) from "
					+ tableName + " where idApp=? and Date='" + execDate + "' and run=" + run + ")" + " LIMIT 1";
			Long numberOfRecordsFailed = jdbcTemplate1.queryForObject(sql, Long.class, idApp, idApp);
			if (numberOfRecordsFailed == null)
				numberOfRecordsFailed = 0l;
			return numberOfRecordsFailed;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public String getRecordCountStatusForExecDateRun(String tableName, long idApp, String execDate, long run) {
		try {
			String sql = "SELECT RC_Std_Dev_Status FROM " + tableName + " WHERE idApp=? and Date= ? and Run=?";
			String recordCountStatus = jdbcTemplate1.queryForObject(sql, String.class, idApp, execDate, run);
			return recordCountStatus;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return "";
	}

	@Override
	public long getnumberofStringColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "select count(String_Status) from " + tableName + " where idApp=" + idApp + " and Run=" + run
					+ "and Date='" + execDate + "' String_Status='failed'";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select count(String_Status) from " + tableName + " where  idApp=" + idApp
						+ " and String_Status='failed'";
			}
			Long numberofStringColumnsFailed = jdbcTemplate1.queryForObject(sql, Long.class);
			return numberofStringColumnsFailed;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public long getnonNullColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run) {

		String recordCountAnomaly = listApplicationsData.getRecordCountAnomaly();
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "";
			if (recordCountAnomaly.equalsIgnoreCase("Y")) {
				sql = "select count(Null_Value) from " + tableName + " where idApp=" + idApp
						+ " and status='failed' and date='" + execDate + "' and Run=" + run;
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "select count(*) from (select count(Null_Value) from " + tableName + " where idApp=" + idApp
							+ " and status='failed' group By Status,ColName) As Alias limit 1";
				}
			} else {
				String RCATableName = "DATA_QUALITY_Column_Summary";
				sql = "Select Count(*) from (SELECT ColName FROM " + RCATableName + " " + " where idApp=" + idApp
						+ " and Date='" + execDate + "' AND  Run=" + run
						+ " AND Status='failed' group By Status,ColName) AS Alias";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "Select Count(*) from (SELECT ColName FROM " + RCATableName + " " + " where idApp=" + idApp
							+ " and Status='failed' group By Status,ColName) AS Alias";
				}
				LOG.debug("sql:" + sql);
			}

			Long nonNullColumnsFailed = jdbcTemplate1.queryForObject(sql, Long.class);
			return nonNullColumnsFailed;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public long getnumberofNumericalColumnsFailedForExecDateRun(String tableName, ListApplications listApplicationsData,
			String execDate, long run) {
		try {
			long idApp = listApplicationsData.getIdApp();
			String sql = "select count(NumMeanStatus) from " + tableName + " where idApp=" + idApp + " and Run=" + run
					+ "and Date='" + execDate + "' and  NumMeanStatus='failed'";
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				sql = "select count(NumMeanStatus) from " + tableName + " where idApp=" + idApp
						+ " and NumMeanStatus='failed'";
			}
			Long numberofNumericalColumnsYes = jdbcTemplate1.queryForObject(sql, Long.class);

			if (numberofNumericalColumnsYes == 0) {
				sql = "select count(NumSDStatus) from " + tableName + " where  idApp=" + idApp + " and Run=" + run
						+ "and Date='" + execDate + "' and  NumSDStatus='failed'";
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					sql = "select count(NumSDStatus) from " + tableName + " where idApp=" + idApp
							+ " and NumSDStatus='failed'";
				}
				numberofNumericalColumnsYes = jdbcTemplate1.queryForObject(sql, Long.class);
			}

			return numberofNumericalColumnsYes;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public SqlRowSet calculateDashboardDetailsForExecDateRun(long idApp, String tabName, String execDate, long run) {
		try {
			String sql = "select * from DashBoard_Summary where AppId = " + idApp + " and Test = '" + tabName + "' and "
					+ "Run = " + run + " " + "and Date = '" + execDate + "'";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
 	
	@Override
	public List<Integer> getAllResultsforIdApp(long idApp) {
		List<Integer> listdataschema = new ArrayList<Integer>();
		try {
			String sql = "select DQI from DashBoard_Summary where AppId = " + idApp + " and Status != ''";
			listdataschema = jdbcTemplate1.query(sql, new RowMapper<Integer>() {

				@Override
				public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("DQI");
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return listdataschema;
	}
	
	@Override
	public Integer getTotalExecutedRulesCount(long idApp,String currentDateStr,String yesterdayStr) {
		Integer rulesExecuted = null;
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql = "select count(*) as rules_executed from DashBoard_Summary where (AppId,date,run) in"
					+ " (SELECT AppId,date, MAX(run) AS max_run FROM "
					+ "DashBoard_Summary where date = '"+currentDateStr+"' GROUP BY AppId,date)";
		else
			sql = "select count(*) as rules_executed from DashBoard_Summary where (AppId,date,run) in"
					+ " (SELECT AppId,date, MAX(run) AS max_run FROM "
					+ "DashBoard_Summary where date = '"+currentDateStr+"' GROUP BY AppId,date)";
		rulesExecuted = jdbcTemplate1.query(sql, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				return rs.next() ? rs.getInt("rules_executed") : null;
			}
		});

		if (rulesExecuted == null) {
			rulesExecuted = 0;
		}
		return rulesExecuted;
	}
	
	@Override
	public Integer getTotalFailedRecordCountforAppId(long idApp,String currentDateStr,String previousYearStr) {
		Integer rulesExecuted = null;
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql = "select sum(columnFailed) as totalColumnFailed from ((select count(*) as columnFailed from DATA_QUALITY_NullCheck_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed') "
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,duplicateCheckFields from DATA_QUALITY_Duplicate_Check_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status= 'failed' group by idApp,Date,Run,duplicateCheckFields) as df)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_badData where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_Unmatched_Default_Pattern_Data where Status = 'failed' and idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_Length_Check where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,sum(TotalFailedRecords) as totalFailed from DATA_QUALITY_DateRule_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run) as tt where totalFailed > 0)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from ( select idApp,date,run,ColName from DATA_QUALITY_Record_Anomaly where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed' group by idApp,date,run,ColName) as sd)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,date,run,colName from DATA_QUALITY_Column_Summary where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and (NumMeanStatus = 'failed' or NumSDStatus='failed' or (ABS(sumOfNumStat-NumSumAvg)/NumSumStdDev) > NumSumThreshold) group by idApp,date,run,colName) as hgh)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,sum(TotalFailedCount) as totalFailed from DATA_QUALITY_timeliness_check where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run) as tt where totalFailed > 0)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,colName from DATA_QUALITY_DATA_DRIFT_SUMMARY where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run,colName) as df)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DashBoard_Summary where AppID = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Test = 'DQ_Record Count Fingerprint' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select sum(Key_Metric_2) as columnFailed from DashBoard_Summary where Test in ('DQ_DefaultCheck','DQ_Rules','DQ_Sql_Rule') and AppId = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"')) as totalCount";
		else
			sql = "select sum(columnFailed) as totalColumnFailed from ((select count(*) as columnFailed from DATA_QUALITY_NullCheck_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed') "
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,duplicateCheckFields from DATA_QUALITY_Duplicate_Check_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status= 'failed' group by idApp,Date,Run,duplicateCheckFields) as df)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_badData where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_Unmatched_Default_Pattern_Data where Status = 'failed' and idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DATA_QUALITY_Length_Check where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,sum(TotalFailedRecords) as totalFailed from DATA_QUALITY_DateRule_Summary where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run) as tt where totalFailed > 0)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from ( select idApp,date,run,ColName from DATA_QUALITY_Record_Anomaly where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Status = 'failed' group by idApp,date,run,ColName) as sd)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,date,run,colName from DATA_QUALITY_Column_Summary where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and (NumMeanStatus = 'failed' or NumSDStatus='failed' or (ABS(sumOfNumStat-NumSumAvg)/NumSumStdDev) > NumSumThreshold) group by idApp,date,run,colName) as hgh)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,sum(TotalFailedCount) as totalFailed from DATA_QUALITY_timeliness_check where idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run) as tt where totalFailed > 0)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from (select idApp,Date,Run,colName from DATA_QUALITY_DATA_DRIFT_SUMMARY where  idApp = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' group by idApp,Date,Run,colName) as df)"
					+ "UNION ALL"
					+ "(select count(*) as columnFailed from DashBoard_Summary where AppID = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"' and Test = 'DQ_Record Count Fingerprint' and Status = 'failed')"
					+ "UNION ALL"
					+ "(select sum(Key_Metric_2) as columnFailed from DashBoard_Summary where Test in ('DQ_DefaultCheck','DQ_Rules','DQ_Sql_Rule') and AppId = "+idApp+" and date between '"+previousYearStr+"' and '"+currentDateStr+"')) as totalCount";
		rulesExecuted = jdbcTemplate1.query(sql, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				return rs.next() ? rs.getInt("totalColumnFailed") : null;
			}
		});

		if (rulesExecuted == null) {
			rulesExecuted = 0;
		}
		return rulesExecuted;
	}
	
	@Override
	public Integer getTotalRecordCountforAppId(long idApp,String currentDateStr,String previousYearStr) {
		Integer rulesExecuted = null;
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql = "SELECT sum(Key_Metric_2) as total_records from DashBoard_Summary WHERE Test ='DQ_Record Count Fingerprint' AND AppId = "+ idApp+" AND Date between '"+previousYearStr+"' AND '"+currentDateStr+"'";
		else
			sql = "SELECT SUM(Key_Metric_2) AS total_records FROM DashBoard_Summary WHERE Test ='DQ_Record Count Fingerprint' AND AppId = "+ idApp+" AND Date between '"+previousYearStr+"' AND '"+currentDateStr+"'";

		rulesExecuted = jdbcTemplate1.query(sql, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				return rs.next() ? rs.getInt("total_records") : null;
			}
		});

		if (rulesExecuted == null) {
			rulesExecuted = 0;
		}
		return rulesExecuted;
	}
	
	@Override
	public String getAggregateDQIForDataQualityDashboardForExecDateRun(long idApp, String execDate, long run) {

		String aggDqi = null;

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql = "select Date, avg(DQI) as date_avg, Run from DashBoard_Summary where appId =" + idApp
					+ " and length(trim(COALESCE(Status,''))) > 0 and Date=? and Run=?  limit 1";
		else
			sql = "select Date, avg(DQI) as date_avg, Run from DashBoard_Summary where appId =" + idApp
					+ " and length(trim(ifnull(Status,''))) > 0 and Date=? and Run=?  limit 1";

		aggDqi = jdbcTemplate1.query(sql, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				return rs.next() ? rs.getString("date_avg") : null;
			}
		}, execDate, run);

		if (aggDqi == null) {
			aggDqi = "0.0";
		}

		String PercentageDF = "0";
		double Percentage = Double.parseDouble(aggDqi);
		DecimalFormat df1 = new DecimalFormat("#0.0");
		PercentageDF = df1.format(Percentage);

		return PercentageDF;
	}

	@Override
	public boolean updateAgingCheckForRuleInRuleCatalog(int ruleCatalogRowId, String status) {
		boolean result = false;
		try {
			String sql = "update listApplicationsRulesCatalog set agingCheckEnabled=? where row_id=?";
			int updateCount = jdbcTemplate.update(sql, status, ruleCatalogRowId);
			if (updateCount > 0) {
				result = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getAgingCheckValueForRule(int ruleCatalogRowId) {
		String agingCheckEnabled = "";
		try {
			String sql = "Select agingCheckEnabled from listApplicationsRulesCatalog where row_id=?";
			agingCheckEnabled = jdbcTemplate.queryForObject(sql, String.class, ruleCatalogRowId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return agingCheckEnabled;
	}

	@Override
	public SqlRowSet getValidationCheckDetails(long idApp, String resultTableName, String strMaxDate, int maxRun) {
		SqlRowSet queryForRowSet = null;
		try {
			String sql = "select * from " + resultTableName + " where idApp=? and Date=? and Run=?";
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, strMaxDate, maxRun);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return queryForRowSet;
	}

	@Override
	public SqlRowSet getDefaultValueValidationCheckDetails(long idApp, String strMaxDate, int maxRun) {
		SqlRowSet queryForRowSet = null;
		try {
			String sql = "select ColName, sum(Default_Count) as Default_Count, avg(Default_Percentage) as Default_Percentage from DATA_QUALITY_default_value where idApp =? and Date =? and Run =? group by ColName";
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, strMaxDate, maxRun);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return queryForRowSet;
	}

	@Override
	public long getDomainIdByIdData(long idData) {
		long domainId = 0l;

		try {
			String sql = "select domain_id from listDataSources where idData=?";
			domainId = jdbcTemplate.queryForObject(sql, Long.class, idData);
		} catch (Exception e) {
			LOG.error("\n=====> Exception occured while retrieving dimensionId from listColRules !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return domainId;
	}

	@Override
	public long getDimensionIdByIdData(long idData) {
		List<Long> dimensionIdList = null;

		try {
			String sql = "select DISTINCT domensionId from listColRules where idData=? and activeFlag='Y' ";
			dimensionIdList = jdbcTemplate.queryForList(sql, Long.class, idData);

			if (dimensionIdList != null && dimensionIdList.size() > 0) {
				return dimensionIdList.get(0);
			}
		} catch (Exception e) {
			LOG.error("\n=====> Exception occured while retrieving dimensionId from listColRules !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public String getDimensionName(long dimensionId) {
		String dimensionName = "";
		try {
			String sql = "select dimensionName from dimension where idDimension=?";
			dimensionName = jdbcTemplate.queryForObject(sql, String.class, dimensionId);
		} catch (Exception e) {
			LOG.error("\n=====> Exception occured while retrieving dimensionName from dimension !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dimensionName;
	}

	@Override
	public String getRuleDescriptionByCustomRuleId(long idListColrules) {
		String description = "";
		try {
			String sql = "select description from listColRules where idListColrules=?";
			description = jdbcTemplate.queryForObject(sql, String.class, idListColrules);

		} catch (EmptyResultDataAccessException e) {
			LOG.error("\n=====> Exception occured while retrieving description from Rule !!");
			LOG.error(e.getMessage());
		} catch (Exception e) {
			LOG.error("\n=====> Exception occured while retrieving description from Rule !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return description;
	}

	@Override
	public String getApplicationsForSelectedProjects(String sSelectedProjects) {
		String sRetValue = "";
		String sDefaultValue = "-10";
		List<String> aAppIds = new ArrayList<String>();

		String sSqlQry = String.format("select idApp from listApplications where project_id in (%1$s);",
				sSelectedProjects);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);

		while (oSqlRowSet.next()) {
			aAppIds.add(String.valueOf(oSqlRowSet.getLong("idApp")));
		}
		sRetValue = String.join(",", aAppIds);
		sRetValue = (sRetValue.length() < 1) ? sDefaultValue : sRetValue;

		return sRetValue;
	}

	@Override
	public String getApplicationsForSelectedProjectsByDataDomain(String sSelectedProjects, int nDataDomain) {
		String sRetValue = "";
		String sDefaultValue = "-10";
		List<String> aAppIds = new ArrayList<String>();

		String sSqlQry = (nDataDomain < 0)
				? String.format("select idApp from listApplications where project_id in (%1$s);", sSelectedProjects)
				: String.format(
						"select idApp from listApplications where project_id in (%1$s) and data_domain_id = %2$s;",
						sSelectedProjects, nDataDomain);

		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);

		while (oSqlRowSet.next()) {
			aAppIds.add(String.valueOf(oSqlRowSet.getLong("idApp")));
		}
		sRetValue = String.join(",", aAppIds);
		sRetValue = (sRetValue.length() < 1) ? sDefaultValue : sRetValue;

		return sRetValue;
	}

	@Override
	public String getAppIdsForDomainProject(int domainId, int projectId, int nDataDomain) {
		String sRetValue = "";
		String sDefaultValue = "-10";
		List<String> aAppIds = new ArrayList<String>();

		String query1 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
				+ " where domain_id =" + domainId + ") and project_id =" + projectId;

		String query2 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
				+ " where domain_id =" + domainId + ") and project_id =" + projectId + " and data_domain_id ="
				+ nDataDomain;

		String sSqlQry = (nDataDomain < 0) ? query1 : query2;

		try {
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);

			while (oSqlRowSet.next()) {
				aAppIds.add(String.valueOf(oSqlRowSet.getLong("idApp")));
			}
			sRetValue = String.join(",", aAppIds);
			sRetValue = (sRetValue.length() < 1) ? sDefaultValue : sRetValue;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return sRetValue;
	}

	@Override
	public String getAppIdsListForDomainProject(int domainId, int projectId, String fromDate, String toDate) {
		String sRetValue = "";
		String sDefaultValue = "-10";
		List<String> aAppIds = new ArrayList<String>();

		try {
			Date date1 = new SimpleDateFormat("yyyy-mm-dd").parse(toDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			cal.add(Calendar.DATE, 1);
			Date dateWith1Days = cal.getTime();
			toDate = new SimpleDateFormat("yyyy-mm-dd").format(dateWith1Days);

			String query1 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
					+ " where domain_id =" + domainId + ") and project_id =" + projectId;

			LOG.debug("listofvalidation " + query1);

			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query1);

			while (oSqlRowSet.next()) {
				aAppIds.add(String.valueOf(oSqlRowSet.getLong("idApp")));
			}
			sRetValue = String.join(",", aAppIds);
			sRetValue = (sRetValue.length() < 1) ? sDefaultValue : sRetValue;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return sRetValue;
	}

	@Override
	public String getFilteredAppIdsListForDomainProject(int domainId, int projectId, long idData, String fromDate,
			String toDate) {
		String sRetValue = "";
		String sDefaultValue = "-10";
		List<String> aAppIds = new ArrayList<String>();
		String query1 = "";
		if (idData > 0) {
			query1 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
					+ " where domain_id =" + domainId + ") and project_id =" + projectId + " and idData =" + idData;

		} else {
			query1 = "select idApp from listApplications where project_id in (select project_id from domain_to_project"
					+ " where domain_id =" + domainId + ") and project_id =" + projectId;
		}

		LOG.debug("listofvalidation " + query1);
		try {
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query1);

			while (oSqlRowSet.next()) {
				aAppIds.add(String.valueOf(oSqlRowSet.getLong("idApp")));
			}
			sRetValue = String.join(",", aAppIds);
			sRetValue = (sRetValue.length() < 1) ? sDefaultValue : sRetValue;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return sRetValue;
	}

	@Override
	public double getAverageDqIndexByProjectsAndPeriod(String sSelectedProjects, String sPeriodStartDate,
			String sPeriodToDate) {
		double dAvgDqIndex = 0.0;
		SqlRowSet oSqlRowSet = null;
		String sApplicableAppIds = getApplicationsForSelectedProjects(sSelectedProjects);

		String sSql = String.format(
				"select AVG(DQI) as AggregateDqIndex from DashBoard_Summary where Date between '%1$s' and '%2$s' and AppId in (%3$s);",
				sPeriodStartDate, sPeriodToDate, sApplicableAppIds);

		/*
		 * String sSql = String.
		 * format("select AVG(aggregateDQI) as AggregateDqIndex from data_quality_dashboard where date between '%1$s' and '%2$s' and idApp in (%3$s) order by idApp;"
		 * , sPeriodStartDate, sPeriodToDate, sApplicableAppIds );
		 */

		LOG.debug("getAverageDqIndexByProjectsAndPeriod() 01 " + sSql);
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSql);

		if (oSqlRowSet.next()) {
			dAvgDqIndex = oSqlRowSet.getDouble("AggregateDqIndex");
		}

		return dAvgDqIndex;
	}

	@Override
	public String getLatestAverageDqIndexAndDate(String sSelectedProjects) {
		double dAvgDqIndex = 0.0;
		String sRetValue = "0.0,2000-01-01";
		SqlRowSet oSqlRowSet = null;
		String sApplicableAppIds = getApplicationsForSelectedProjects(sSelectedProjects);

		// Query compatibility changes for both POSTGRES and MYSQL
		String sSql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) { 
			sSql = "select sum(DQI)/count(date) as AggregateDqIndex, to_date(Date::text, 'YYYY-MM-DD') as Date from DashBoard_Summary where AppId in (#1) group by Date order by date desc limit 1;";
		} else {
			sSql = "select sum(DQI)/count(date) as AggregateDqIndex, date_format(Date, '%Y-%m-%d') as Date from DashBoard_Summary where AppId in (#1) group by Date order by date desc limit 1;";
		}

		sSql = sSql.replaceAll("#1", sApplicableAppIds);

		LOG.debug("getAverageDqIndexByProjectsAndPeriod() 01 " + sSql);

		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSql);

		if (oSqlRowSet.next()) {
			dAvgDqIndex = oSqlRowSet.getDouble("AggregateDqIndex");
			sRetValue = String.format("%1$s,%2$s", dAvgDqIndex, oSqlRowSet.getString("Date"));
		}
		return sRetValue;
	}

	@Override
	public SqlRowSet calculateAvgMetricsForCheck(String validationIdsList, String checkName) {
		SqlRowSet sqlRowSet = null;
		try {
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select Avg(ds.DQI) as avgDQI,sum(ds.Key_Metric_1) As Key_Metric_1, sum(ds.Key_Metric_2) As Key_Metric_2, case when (sum(case when (ds.Status = 'failed') then 1 else 0 end)) > 0 then 'failed' else 'passed' end as Status from DashBoard_Summary ds "
						+ "join (select t1.AppId, t1.Date, t2.Run from (select AppId, TO_DATE(max(Date), 'YYYY-MM-DD') AS Date from DashBoard_Summary group by AppId) t1 join (select AppId, TO_DATE(Date, 'YYYY-MM-DD') AS Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date) dr "
						+ "on ds.AppId=dr.AppId and TO_DATE(ds.Date, 'YYYY-MM-DD')=dr.Date and ds.Run=dr.Run and ds.Test=? and ds.AppId in ("
						+ validationIdsList + ")";
			}else{
				sql = "select Avg(ds.DQI) as avgDQI,sum(ds.Key_Metric_1) As Key_Metric_1, sum(ds.Key_Metric_2) As Key_Metric_2, case when sum(ds.Status = 'failed') > 0 then 'failed' else 'passed' end as Status from DashBoard_Summary ds "
						+ "join (select t1.AppId, t1.Date, t2.Run from (select AppId, max(Date) AS Date from DashBoard_Summary group by AppId) t1 join (select AppId, Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date) dr "
						+ "on ds.AppId=dr.AppId and ds.Date=dr.Date and ds.Run=dr.Run and ds.Test=? and ds.AppId in ("
						+ validationIdsList + ")";
			}
			sqlRowSet = jdbcTemplate1.queryForRowSet(sql, checkName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return sqlRowSet;
	}

	@Override
	public Integer getTotalCustomRulesExecutedForSchema(String validationIdsList) {
		Integer customRuleCount = 0;
		try {
			String sql = "select case when sum(Key_Metric_1) IS NULL then 0 else sum(Key_Metric_1) end AS customRuleCount from DashBoard_Summary ds join (select t1.AppId, t1.Date, t2.Run from (select AppId, max(Date) AS Date from DashBoard_Summary group by AppId) t1 join (select AppId, Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date) dr on ds.AppId=dr.AppId and ds.Date=dr.Date and ds.Run=dr.Run and ds.AppId in ("
					+ validationIdsList + ") and Test='DQ_Rules'";
			Integer count = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (count != null && count > 0)
				customRuleCount = count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return customRuleCount;
	}

	@Override
	public Integer getTotalGlobalRulesExecutedForSchema(String validationIdsList) {
		Integer globalRuleCount = 0;
		try {
			String sql = "select case when sum(Key_Metric_1) IS NULL then 0 else sum(Key_Metric_1) end AS globalRuleCount from DashBoard_Summary ds join (select t1.AppId, t1.Date, t2.Run from (select AppId, max(Date) AS Date from DashBoard_Summary group by AppId) t1 join (select AppId, Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date) dr on ds.AppId=dr.AppId and ds.Date=dr.Date and ds.Run=dr.Run and ds.AppId in ("
					+ validationIdsList + ") and Test='DQ_GlobalRules'";
			Integer count = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (count != null && count > 0)
				globalRuleCount = count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return globalRuleCount;
	}

	@Override
	public Double getAggregateDQIForSchema(String validationIdsList) {
		double avgDQI = 0.0;
		try {
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select case when Avg(ds.DQI) IS NULL then 0 else Avg(ds.DQI) end AS avgDQI from DashBoard_Summary ds "
						+ "join (select t1.AppId, t1.Date, t2.Run from (select AppId, max(Date) AS Date from DashBoard_Summary group by AppId) t1 "
						+ "join (select AppId, Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date"
						+ ") dr on ds.AppId=dr.AppId and to_date(ds.Date,'yyyy-MM-dd')=to_date(dr.Date,'yyyy-MM-dd') and ds.Run=dr.Run and ds.AppId in ("
						+ validationIdsList + ") and length(trim(COALESCE(ds.Status,''))) > 0";
			} else {
				sql = "select case when Avg(ds.DQI) IS NULL then 0 else Avg(ds.DQI) end AS avgDQI from DashBoard_Summary ds "
						+ "join (select t1.AppId, t1.Date, t2.Run from (select AppId, max(Date) AS Date from DashBoard_Summary group by AppId) t1 "
						+ "join (select AppId, Date, max(Run) As Run from DashBoard_Summary group by AppId,Date) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date"
						+ ") dr on ds.AppId=dr.AppId and ds.Date=dr.Date and ds.Run=dr.Run and ds.AppId in ("
						+ validationIdsList + ") and length(trim(COALESCE(ds.Status,''))) > 0";
			}
			Double dqi = jdbcTemplate1.queryForObject(sql, Double.class);
			if (dqi != null)
				avgDQI = dqi;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return avgDQI;
	}

	@Override
	public Integer getTotalExecutedValidationCountForSchema(String validationIdsList) {
		Integer executedValidationsCount = 0;
		try {
			String sql = " select count(distinct AppId) as executedValidationsCount from DashBoard_Summary where AppId in ("
					+ validationIdsList + ")";
			LOG.debug("Execution validation count sql: " + sql);

			Integer count = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (count != null && count > 0)
				executedValidationsCount = count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return executedValidationsCount;
	}

	/*
	 * The following method returns details about failed external notifications
	 */

	@Override
	public List<ExternalAPIAlertPOJO> getFailedExternalNotificationDetails(String externalApiType, String startDate,
			String endDate) {

		List<ExternalAPIAlertPOJO> externalAPIAlertPOJOList = null;
		String sql = "select * from external_api_alert_msg_queue where alert_msg_deliver_status='failed' and external_api_type='"
				+ externalApiType.trim() + "' and alter_timeStamp>='" + startDate.trim() + "' and alter_timeStamp<='"
				+ endDate.trim() + "'";

		LOG.debug("\n====>Failed alert notification retrieval sql=" + sql);
		try {
			RowMapper<ExternalAPIAlertPOJO> rowMapper = (rs, i) -> {
				ExternalAPIAlertPOJO externalAPIAlertPOJO = new ExternalAPIAlertPOJO();
				externalAPIAlertPOJO.setExternalAPIType(rs.getString("external_api_type"));
				externalAPIAlertPOJO.setTaskType(rs.getString("taskType"));
				externalAPIAlertPOJO.setTaskId(rs.getLong("taskId"));
				externalAPIAlertPOJO.setUniqueId(rs.getString("uniqueId"));
				externalAPIAlertPOJO.setExecDate(rs.getDate("execution_date").toString());
				externalAPIAlertPOJO.setRun(rs.getLong("run"));
				externalAPIAlertPOJO.setTestRun(rs.getString("test_run"));
				externalAPIAlertPOJO.setAlertTimeStamp(rs.getTimestamp("alter_timeStamp").toString());
				externalAPIAlertPOJO.setAlertMsg(rs.getString("alert_msg"));
				externalAPIAlertPOJO.setAlertMsgCode(rs.getString("alert_msg_code"));
				externalAPIAlertPOJO.setAlertLabel(rs.getString("alert_label"));
				externalAPIAlertPOJO.setAlertJson(rs.getString("alert_json"));
				externalAPIAlertPOJO.setAlertMsgSentStatus(rs.getString("alert_msg_deliver_status"));

				return externalAPIAlertPOJO;
			};
			externalAPIAlertPOJOList = jdbcTemplate1.query(sql, rowMapper);
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while retrieving details from external_api_alert_msg_queue");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return externalAPIAlertPOJOList;
	}

	@Override
	public HashMap<String, Double> getDqGraphData(HashMap<String, String> oDateRange, String sWhichPeriod,
			String sApplicationIds) {
		SqlRowSet oSqlRowSet = null;
		HashMap<String, Double> oRetValue = new HashMap<String, Double>();
		try {
			String sPeriodStartDate = sWhichPeriod.equalsIgnoreCase("C") ? oDateRange.get("CurrentPeriodStartDate")
					: oDateRange.get("PreviousPeriodStartDate");
			String sPeriodToDate = sWhichPeriod.equalsIgnoreCase("C") ? oDateRange.get("CurrentPeriodToDate")
					: oDateRange.get("PreviousPeriodToDate");

			String sSqlQry = "select sum(DQI)/count(date) as aggDqIndex, Date \n";
			sSqlQry = sSqlQry + "from DashBoard_Summary \n";
			sSqlQry = sSqlQry
					+ String.format("where date between '%1$s' and '%2$s' \n", sPeriodStartDate, sPeriodToDate);
			sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", sApplicationIds);
			sSqlQry = sSqlQry + "group by Date order by date desc";

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

			while (oSqlRowSet.next()) {
				oRetValue.put(oSqlRowSet.getString("Date"), oSqlRowSet.getDouble("aggDqIndex"));
			}
		} catch (Exception oException) {
			oRetValue = new HashMap<String, Double>();
			oException.printStackTrace();
			LOG.error("\n=====>Could not retrieve data from DashBoard_Summary");
			LOG.error(oException.getMessage());
		}
		return oRetValue;
	}

	@Override
	public HashMap<String, Double> getDqGraphData(String fromDate, String toDate, String sWhichPeriod,
			String sApplicationIds) {
		SqlRowSet oSqlRowSet = null;
		HashMap<String, Double> oRetValue = new HashMap<String, Double>();
		try {
			String sSqlQry = "";
			if(DatabuckEnv.DB_TYPE.equals(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQry = "select sum(DQI)/count(date) as aggDqIndex, to_date(Date::text, 'YYYY-MM-DD') as Date \n";
				sSqlQry = sSqlQry + "from DashBoard_Summary \n";
				sSqlQry = sSqlQry + String.format("where date between '%1$s' and '%2$s' \n", fromDate, toDate);
				sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", sApplicationIds);
				sSqlQry = sSqlQry + "group by Date order by date desc";
			}else {
				sSqlQry = "select sum(DQI)/count(date) as aggDqIndex, Date \n";
				sSqlQry = sSqlQry + "from DashBoard_Summary \n";
				sSqlQry = sSqlQry + String.format("where date between '%1$s' and '%2$s' \n", fromDate, toDate);
				sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", sApplicationIds);
				sSqlQry = sSqlQry + "group by Date order by date desc";
			}
			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			while (oSqlRowSet.next()) {
				oRetValue.put(oSqlRowSet.getString("Date"), oSqlRowSet.getDouble("aggDqIndex"));
			}
		} catch (Exception oException) {
			oRetValue = new HashMap<String, Double>();
			oException.printStackTrace();
			LOG.error("\n=====>Could not retrieve data from DashBoard_Summary");
			LOG.error(oException.getMessage());
		}
		return oRetValue;
	}

	@Override
	public String getLatestDqIndex(String sApplicationIds) {
		String sSqlQry = "";
		String sSelectedApplications = "";
		SqlRowSet oSqlRowSet = null;
		String sRetValue = "0.0,0000-00-00";
		double dAvgDqIndex = 0.0;

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) { 
				sSqlQry = sSqlQry + "select \n";
				sSqlQry = sSqlQry + "   sum(DQI)/count(date) as aggregateDqIndex, \n";
				sSqlQry = sSqlQry + "	to_date(Date::TEXT, 'YYYY-MM-DD') as Date \n";
				sSqlQry = sSqlQry + "from DashBoard_Summary \n";
				sSqlQry = sSqlQry + String.format("where AppId in (%1$s) \n", sApplicationIds);
				sSqlQry = sSqlQry + "group by Date order by date desc limit 1;";
			} else {
				sSqlQry = sSqlQry + "select \n";
				sSqlQry = sSqlQry + "   sum(DQI)/count(date) as aggregateDqIndex, \n";
				sSqlQry = sSqlQry + "	date_format(Date, '%Y-%m-%d') as Date \n";
				sSqlQry = sSqlQry + "from DashBoard_Summary \n";
				sSqlQry = sSqlQry + String.format("where AppId in (%1$s) \n", sApplicationIds);
				sSqlQry = sSqlQry + "group by Date order by date desc limit 1;";
			}
			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

			if (oSqlRowSet.next()) {
				dAvgDqIndex = oSqlRowSet.getDouble("aggregateDqIndex");
				sRetValue = String.format("%.2f,%2$s", dAvgDqIndex, oSqlRowSet.getString("Date"));
			}
		} catch (Exception oException) {
			sRetValue = "0.0,0000-00-00";
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return sRetValue;
	}

	@Override
	public Map<Long, String> getValidationMapByDomainAndProject(int domainId, int projectId) {
		Map<Long, String> validationMap = new HashMap<>();
		String sql = "select idApp,name as validationName from listApplications where project_id in (select project_id from domain_to_project "
				+ "where domain_id =" + domainId + ") and project_id =" + projectId
				+ " and active = 'yes' order by idApp";

		try {
			List<Map<String, Object>> validationMapList = jdbcTemplate.queryForList(sql);
			if (validationMapList != null) {
				for (Map<String, Object> valMap : validationMapList) {
					Object idAppObj = valMap.get("idApp");
					Object validationNameObj = valMap.get("validationName");

					if (idAppObj instanceof Long) {
						Long idApp = (Long) idAppObj;
						validationMap.put(idApp, (String) validationNameObj);
					} else if (idAppObj instanceof Integer) {
						Integer idAppInt = (Integer) idAppObj;
						Long idAppLong = idAppInt.longValue();
						validationMap.put(idAppLong, (String) validationNameObj);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occured while fetching validation details from listApplications");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return validationMap;
	}

	@Override
	public Map<Long, Double> getLeastDQIsByValidations(String validationIds) {

		Map<Long, Double> filteredValidation = new LinkedHashMap<>();
		String sql = "select IdApp as idApp,aggregateDQI from data_quality_dashboard where IdApp in " + "("
				+ validationIds + ") order by aggregateDQI ASC limit 10";
		try {
			List<Map<String, Object>> dqiMapList = jdbcTemplate1.queryForList(sql);
			if (dqiMapList != null) {
				for (Map<String, Object> dMap : dqiMapList) {
					filteredValidation.put((Long) dMap.get("idApp"), (Double) dMap.get("aggregateDQI"));
				}
			}
		} catch (Exception e) {
			LOG.error("Exception occured while retrieving details from data_quality_dashboard");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return filteredValidation;
	}

	@Override
	public List<Map<String, Object>> getLeastDQIsByValidations(String validationIds, String fromDate, String toDate) {

		List<Map<String, Object>> validations = new ArrayList<Map<String, Object>>();

		try {
			if (validationIds != null && !validationIds.trim().isEmpty()) {
				String sql = "select IdApp as idApp,aggregateDQI, date, validationCheckName from data_quality_dashboard where IdApp in "
						+ "(" + validationIds + ") and Date between '" + fromDate + "' and '" + toDate
						+ "' order by aggregateDQI ASC limit 10";

				SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
				while (rs.next()) {
					Map<String, Object> data = new HashMap<>();
					data.put("dqi", rs.getString("aggregateDQI"));
					data.put("date", rs.getString("date"));
					data.put("validationName", rs.getString("validationCheckName"));
					validations.add(data);
				}
			}
		} catch (Exception e) {
			LOG.error("Exception occured while retrieving details from data_quality_dashboard");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return validations;
	}

	public double getAverageDqIndexByDomainProjectsForDataDomain(String periodStartDate, String periodToDate,
			String applicationIds) {
		double dAvgDqIndex = 0.0;
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;

		try {
			sSqlQry = sSqlQry + "select AVG(DQI) as AggregateDqIndex \n";
			sSqlQry = sSqlQry + "from DashBoard_Summary \n";
			sSqlQry = sSqlQry + String.format("where date between '%1$s' and '%2$s' \n", periodStartDate, periodToDate);
			sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", applicationIds);
			sSqlQry = sSqlQry + "group by Date order by date desc limit 1";

			LOG.debug("getAverageDqIndexByProjectsAndPeriodByDataDomain ==>" + sSqlQry);

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			dAvgDqIndex = (oSqlRowSet.next()) ? oSqlRowSet.getDouble("AggregateDqIndex") : dAvgDqIndex;

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return dAvgDqIndex;
	}

	@Override
	public double getAverageDqIndexByDomainProject(String applicationIds, String periodStartDate, String periodToDate) {
		double dAvgDqIndex = 0.0;
		SqlRowSet oSqlRowSet = null;

		String sSql = String.format(
				"select AVG(DQI) as AggregateDqIndex from DashBoard_Summary where Date between '%1$s' and '%2$s' and AppId in (%3$s);",
				periodStartDate, periodToDate, applicationIds);

		LOG.debug("getAverageDqIndexByProjectsAndPeriod() 01 " + sSql);
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSql);

		if (oSqlRowSet.next()) {
			dAvgDqIndex = oSqlRowSet.getDouble("AggregateDqIndex");
		}

		return dAvgDqIndex;
	}

	private DataQualityMasterDashboard getDetailsFromRowSet(SqlRowSet queryForRowSet) {
		DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
		Long idApp = queryForRowSet.getLong("idApp");
		dashboard.setIdApp(idApp);
		dashboard.setDate(queryForRowSet.getString("date"));
		dashboard.setRun(queryForRowSet.getLong("run"));
		dashboard.setTestRun(queryForRowSet.getString("test_run"));
		dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
		try {
			String templateSql = "select la.idData, ls.schemaName as connectionName, ls.fileNamePattern as fileName, applyRules from listApplications la join listDataSources lds on la.idData=lds.idData "
					+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp=" + idApp;
			Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
			dashboard.setTemplateId(Long.valueOf(la_data.get("idData").toString()));
			dashboard.setConnectionName(String.valueOf(la_data.get("connectionName")));
			dashboard.setApplyRules(String.valueOf(la_data.get("applyRules")));
			String fileSql = "select folderName as fileName from processData where idApp=" + idApp + " limit 1";
			Map<String, Object> fileData = jdbcTemplate1.queryForMap(fileSql);
			dashboard.setFileName(String.valueOf(fileData.get("fileName")));
			ListDerivedDataSource listDerivedDataSource = listDataSourceDAO.getDataFromListDerivedDataSourcesOfIdData(Long.valueOf(la_data.get("idData").toString()));
			String isDerivedTemplate = listDerivedDataSource != null? "Y":"N";
			dashboard.setIsDerivedTemplate(isDerivedTemplate);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		dashboard.setSource1(queryForRowSet.getString("sourceName"));
		dashboard.setRecordCountStatus(queryForRowSet.getString("recordCountStatus"));
		dashboard.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
		dashboard.setStringFieldStatus(queryForRowSet.getString("stringFieldStatus"));
		dashboard.setNumericalFieldStatus(queryForRowSet.getString("numericalFieldStatus"));
		dashboard.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
		dashboard.setUserSelectedFieldsStatus(queryForRowSet.getString("userSelectedFieldStatus"));
		dashboard.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
		dashboard.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
		dashboard.setProjectName(getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
		dashboard.setAggreagteDQI(queryForRowSet.getDouble("aggregateDQI")); // added
		dashboard.setRecordCount(queryForRowSet.getLong("recordCount"));
		// Commented temporarily for performance, Will be enabled when server-side pagination is configured
		/*
		try {
			String avgSql = "select ag5.appId, count(*) as runcount, (Avg(ag5.avgDQI) - 3*stddev(ag5.avgDQI)) AS  lowerlimit, (Avg(ag5.avgDQI) + 3*stddev(ag5.avgDQI)) AS upperlimit from (select ag3.appId, ag3.Date, ag3.Run, Avg(ag3.DQI) AS avgDQI from DashBoard_Summary ag3 left outer join (select ag1.appId, ag1.Date, max(ag1.Run) as Run from DashBoard_Summary ag1 join (select appId, max(Date) as maxDate from DashBoard_Summary where appId="
					+ idApp
					+ " group by appId) ag2 on ag1.appId=ag2.appId and ag1.Date=ag2.maxDate group by ag1.appId,ag1.Date) ag4 on ag3.appId = ag4.appId and ag3.Date=ag4.Date and ag3.Run = ag4.Run where ag4.Date is null and ag4.Run is null and ag3.appId = "
					+ idApp + " group by ag3.appId, ag3.Date, ag3.Run) ag5 group by ag5.appId";
			SqlRowSet avg_data = jdbcTemplate1.queryForRowSet(avgSql);

			Double upperlimit = queryForRowSet.getDouble("aggregateDQI");
			Double lowerlimit = queryForRowSet.getDouble("aggregateDQI");

			if (avg_data != null) {
				while (avg_data.next()) {
					Integer runCount = avg_data.getInt("runcount");
					if (runCount > 1) {
						upperlimit = avg_data.getDouble("upperlimit");
						if (upperlimit != null && upperlimit > 100) {
							upperlimit = new Double(100);
						}
						lowerlimit = avg_data.getDouble("lowerlimit");
						if (lowerlimit != null && lowerlimit < 0) {
							lowerlimit = new Double(0);
						}
					}
				}
			}

			dashboard.setUpperLimit(upperlimit);
			dashboard.setLowerLimit(lowerlimit);

		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return dashboard;
	}

	@Override
	public List<DataQualityMasterDashboard> getAppIdsListSummary(String applicationIds, String fromDate,
			String toDate) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		if (!applicationIds.isEmpty()) {
			try {
				String sql = "select t1.*, t2.test_run,t3.recordCount from data_quality_dashboard t1  "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run where t1.idApp in ("
						+ applicationIds + ") and (execution_date between '" + fromDate + "' and '" + toDate
						+ "') order by t1.date desc ,t1.idapp limit 1000";
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					//String status = "";
					DataQualityMasterDashboard dashboard = getDetailsFromRowSet(queryForRowSet);
					String listAppsql = "Select active from listApplications where idApp="+dashboard.getIdApp();
					SqlRowSet listAppData = jdbcTemplate.queryForRowSet(listAppsql);
					while (listAppData.next()) {
						dashboard.setStatus(String.valueOf(listAppData.getString("active")));
					}
					masterDashboard.add(dashboard);
				}
			} catch (Exception e) {
				LOG.debug("\n====>Exception occured while fetching details from data_quality_dashboard");
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return masterDashboard;
	}

	@Override
	public List<DataQualityMasterDashboard> getAppIdsListSummaryByFilter(String applicationIds, String filterCondition,
			String fromDate, String toDate) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		if (!filterCondition.isEmpty()) {
			try {
				String sql = "select t1.*, t2.test_run,t3.recordCount from data_quality_dashboard t1  "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run where t1.idApp in ("
						+ applicationIds + ") and (execution_date between '" + fromDate + "' and '" + toDate + "') and "
						+ filterCondition + " order by t1.date desc , t1.idapp limit 1000";
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					//String status = "";
					DataQualityMasterDashboard dashboard = getDetailsFromRowSet(queryForRowSet);
					String listAppsql = "Select active from listApplications where idApp="+dashboard.getIdApp();
					SqlRowSet listAppData = jdbcTemplate.queryForRowSet(listAppsql);
					while (listAppData.next()) {
						dashboard.setStatus(String.valueOf(listAppData.getString("active")));
					}
					masterDashboard.add(dashboard);
				}
			} catch (Exception e) {
				LOG.error("\n====>Exception occured while fetching details from data_quality_dashboard");
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		// createMatchingCsv(masterDashboard);
		return masterDashboard;
	}

	@Override
	public List<DataQualityMasterDashboard> getValidationDetailsByFilter(String applicationIds, String fromDate,
			String toDate, String connectioName, String fileName) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		if (!applicationIds.isEmpty()) {
			try {
				String sql = "select t1.*, t2.test_run,t3.recordCount from data_quality_dashboard t1  "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run where t1.idApp in ("
						+ applicationIds + ") and (execution_date between '" + fromDate + "' and '" + toDate
						+ "') order by t1.date desc ,t1.idapp limit 1000";
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
					Long idApp = queryForRowSet.getLong("idApp");
					try {
						String templateSql = "select la.idData, ls.schemaName as connectionName, ls.fileNamePattern as fileName from listApplications la join listDataSources lds on la.idData=lds.idData "
								+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp="
								+ idApp;
						Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
						dashboard.setTemplateId(Long.valueOf(la_data.get("idData").toString()));
						dashboard.setConnectionName(String.valueOf(la_data.get("connectionName")));
						String fileSql = "select folderName as fileName from processData where idApp=" + idApp
								+ " limit 1";
						Map<String, Object> fileData = jdbcTemplate1.queryForMap(fileSql);
						dashboard.setFileName(String.valueOf(fileData.get("fileName")));
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					dashboard.setIdApp(idApp);
					dashboard.setDate(queryForRowSet.getString("date"));
					dashboard.setRun(queryForRowSet.getLong("run"));
					dashboard.setTestRun(queryForRowSet.getString("test_run"));
					dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
					dashboard.setSource1(queryForRowSet.getString("sourceName"));
					dashboard.setRecordCountStatus(queryForRowSet.getString("recordCountStatus"));
					dashboard.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
					dashboard.setStringFieldStatus(queryForRowSet.getString("stringFieldStatus"));
					dashboard.setNumericalFieldStatus(queryForRowSet.getString("numericalFieldStatus"));
					dashboard.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
					dashboard.setUserSelectedFieldsStatus(queryForRowSet.getString("userSelectedFieldStatus"));
					dashboard.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
					dashboard.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
					dashboard.setProjectName(getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
					dashboard.setAggreagteDQI(queryForRowSet.getDouble("aggregateDQI")); // added
					dashboard.setRecordCount(queryForRowSet.getLong("recordCount"));
					if (!connectioName.equals("")) {
						if (dashboard.getConnectionName().contains(connectioName)) {
							masterDashboard.add(dashboard);
							continue;
						} else {
							continue;
						}
					}
					if (!fileName.equals("")) {
						if (dashboard.getFileName().contains(fileName)) {
							masterDashboard.add(dashboard);
							continue;
						} else {
							continue;
						}
					}
					masterDashboard.add(dashboard);
				}
			} catch (Exception e) {
				LOG.error("\n====>Exception occured while fetching details from data_quality_dashboard");
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return masterDashboard;
	}

	@Override
	public List<DQSummaryBean> getDQSummaryDataForidApp(long idApp, String execDate, long run) {
		List<DQSummaryBean> summaryList = new ArrayList<DQSummaryBean>();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "(Select Date,Run,ColName AS columnNameOrRuleName, 'Null Check' AS typeOfCheck, Record_Count as totalRecords, Null_Value as failedRecords from DATA_QUALITY_NullCheck_Summary where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + "  union  "
						+ "(select Date,Run,ColName AS columnNameOrRuleName, 'Length Check' AS typeOfCheck, RecordCount as totalRecords ,TotalFailedRecords as failedRecords from DATA_QUALITY_Length_Check where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Bad Data Check' AS typeOfCheck,TotalRecord as  totalRecords,TotalBadRecord as failedRecords from DATA_QUALITY_badData where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(select Date,Run,Col_Name AS columnNameOrRuleName, 'Pattern Check' AS typeOfCheck,Total_Records as totalRecords, Total_Failed_Records as failedRecords from DATA_QUALITY_Unmatched_Pattern_Data where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Record Anomaly Check' AS typeOfCheck,(SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, count(*) AS failedRecords from DATA_QUALITY_Record_Anomaly where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by Date,Run,ColName)"
						+ " union "
						+ "(select Date,Run,ColName as columnNameOrRuleName,'Default Check' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, ABS((SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1)-sum(Default_Count::int)) as failedRecords from DATA_QUALITY_default_value where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by idApp,Date,Run,ColName)"
						+ " union "
						+ "(select Date,Run,DateField AS columnNameOrRuleName, 'Date Rule Check' AS typeOfCheck,TotalNumberOfRecords as totalRecords, TotalFailedRecords as failedRecords from DATA_QUALITY_DateRule_Summary where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Data Drift Check' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (missingValueCount + newValueCount) as failedRecords from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,duplicateCheckFields AS columnNameOrRuleName, 'Duplicate Check PrimaryFields' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (select Duplicate from DATA_QUALITY_Transaction_Summary where Type='identity' and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as failedRecords from DATA_QUALITY_Transaction_Detail_Identity where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " group by idApp,Date,Run,duplicateCheckFields)" + " union "
						+ "(Select Date,Run,duplicateCheckFields AS columnNameOrRuleName, 'Duplicate Check SelectedFields' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (select Duplicate from DATA_QUALITY_Transaction_Summary where Type='all' and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as failedRecords from DATA_QUALITY_Transaction_Detail_All where idApp=" + idApp
						+ " and Date='" + execDate + "' and Run=" + run
						+ " group by idApp,Date,Run,duplicateCheckFields)" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Numerical Statistics Check' AS typeOfCheck,(SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, count(*) as failedRecords from DATA_QUALITY_Column_Summary where (NumSDStatus='failed' or NumMeanStatus='failed') and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by Date,Run,ColName)"
						+ " union "
						+ "(Select Date,Run,ruleName AS columnNameOrRuleName, 'Global Rule' AS typeOfCheck, totalRecords, totalFailed as failedRecords from DATA_QUALITY_GlobalRules where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ruleName AS columnNameOrRuleName, 'Custom Rule' AS typeOfCheck,  totalRecords, totalFailed as failedRecords from DATA_QUALITY_Rules where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,Col_Name AS columnNameOrRuleName, 'Default Pattern Check' AS typeOfCheck,  Total_Records, Total_Failed_Records as failedRecords from DATA_QUALITY_Unmatched_Default_Pattern_Data where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")";

			} else {
				sql = "(Select Date,Run,ColName AS columnNameOrRuleName, 'Null Check' AS typeOfCheck, Record_Count as totalRecords, Null_Value as failedRecords from DATA_QUALITY_NullCheck_Summary where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + "  union  "
						+ "(select Date,Run,ColName AS columnNameOrRuleName, 'Length Check' AS typeOfCheck, RecordCount as totalRecords ,TotalFailedRecords as failedRecords from DATA_QUALITY_Length_Check where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Bad Data Check' AS typeOfCheck,TotalRecord as  totalRecords,TotalBadRecord as failedRecords from DATA_QUALITY_badData where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(select Date,Run,Col_Name AS columnNameOrRuleName, 'Pattern Check' AS typeOfCheck,Total_Records as totalRecords, Total_Failed_Records as failedRecords from DATA_QUALITY_Unmatched_Pattern_Data where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Record Anomaly Check' AS typeOfCheck,(SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, count(*) AS failedRecords from DATA_QUALITY_Record_Anomaly where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by Date,Run,ColName)"
						+ " union "
						+ "(select Date,Run,ColName as columnNameOrRuleName,'Default Check' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, ABS((SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1)-sum(Default_Count)) as failedRecords from DATA_QUALITY_default_value where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by idApp,Date,Run,ColName)"
						+ " union "
						+ "(select Date,Run,DateField AS columnNameOrRuleName, 'Date Rule Check' AS typeOfCheck,TotalNumberOfRecords as totalRecords, TotalFailedRecords as failedRecords from DATA_QUALITY_DateRule_Summary where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Data Drift Check' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (missingValueCount + newValueCount) as failedRecords from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,duplicateCheckFields AS columnNameOrRuleName, 'Duplicate Check PrimaryFields' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (select Duplicate from DATA_QUALITY_Transaction_Summary where Type='identity' and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as failedRecords from DATA_QUALITY_Transaction_Detail_Identity where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " group by idApp,Date,Run,duplicateCheckFields)" + " union "
						+ "(Select Date,Run,duplicateCheckFields AS columnNameOrRuleName, 'Duplicate Check SelectedFields' AS typeOfCheck, (SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, (select Duplicate from DATA_QUALITY_Transaction_Summary where Type='all' and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as failedRecords from DATA_QUALITY_Transaction_Detail_All where idApp=" + idApp
						+ " and Date='" + execDate + "' and Run=" + run
						+ " group by idApp,Date,Run,duplicateCheckFields)" + " union "
						+ "(Select Date,Run,ColName AS columnNameOrRuleName, 'Numerical Statistics Check' AS typeOfCheck,(SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run
						+ " limit 1) as totalRecords, count(*) as failedRecords from DATA_QUALITY_Column_Summary where (NumSDStatus='failed' or NumMeanStatus='failed') and idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + " group by Date,Run,ColName)"
						+ " union "
						+ "(Select Date,Run,ruleName AS columnNameOrRuleName, 'Global Rule' AS typeOfCheck, totalRecords, totalFailed as failedRecords from DATA_QUALITY_GlobalRules where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,ruleName AS columnNameOrRuleName, 'Custom Rule' AS typeOfCheck,  totalRecords, totalFailed as failedRecords from DATA_QUALITY_Rules where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")" + " union "
						+ "(Select Date,Run,Col_Name AS columnNameOrRuleName, 'Default Pattern Check' AS typeOfCheck,  Total_Records, Total_Failed_Records as failedRecords from DATA_QUALITY_Unmatched_Default_Pattern_Data where idApp="
						+ idApp + " and Date='" + execDate + "' and Run=" + run + ")";
			}

			summaryList = jdbcTemplate1.query(sql, new RowMapper<DQSummaryBean>() {

				@Override
				public DQSummaryBean mapRow(ResultSet rs, int rowNum) throws SQLException {
					DQSummaryBean dqSmry = new DQSummaryBean();
					Date execDate = rs.getDate("Date");
					dqSmry.setExecDate(new SimpleDateFormat("yyyy-MM-dd").format(execDate));
					dqSmry.setRun(rs.getLong("Run"));
					dqSmry.setColumnNameOrRuleName(rs.getString("columnNameOrRuleName"));
					dqSmry.setTypeOfCheck(rs.getString("typeOfCheck"));
					dqSmry.setTotalRecords(rs.getLong("totalRecords"));
					dqSmry.setFailedRecords(rs.getLong("failedRecords"));
					return dqSmry;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return summaryList;
	}

	public List<String> getDashboardTestNamesByIdapp(long idApp) {
		List<String> dashBoardTestNames = new ArrayList<>();
		try {

			String sql = "select distinct(Test) from DashBoard_Summary where AppId=?";
			LOG.debug("sql:" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp);

			while (queryForRowSet.next()) {
				String testName = queryForRowSet.getString(1);
				if (!testName.isEmpty())
					dashBoardTestNames.add(testName);
			}
		} catch (Exception e) {
			LOG.debug("\n====> Exception occurred while retrieving Test Names from DashBoard_Summary");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dashBoardTestNames;
	}

	@Override
	public String getAgingCheckValueForRuleFromStaging(int ruleCatalogRowId) {
		String agingCheckEnabled = "";
		try {
			String sql = "Select agingCheckEnabled from staging_listApplicationsRulesCatalog where row_id=?";
			agingCheckEnabled = jdbcTemplate.queryForObject(sql, String.class, ruleCatalogRowId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return agingCheckEnabled;
	}

	@Override
	public boolean updateAgingCheckForRuleInStagingRuleCatalog(int ruleCatalogRowId, String status) {
		boolean result = false;
		try {
			String sql = "update staging_listApplicationsRulesCatalog set agingCheckEnabled=? where row_id=?";
			int updateCount = jdbcTemplate.update(sql, status, ruleCatalogRowId);
			if (updateCount > 0) {
				result = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean undoDataDriftRejectInd(String tableName, String userName, String run, String uniqueValues,
			String colName, String dGroupVal, String dGroupCol) {
		boolean status = false;

		try {
			String dGroupVal2 = "";
			String dGroupCol2 = "";

			if (dGroupVal == null || dGroupVal.equalsIgnoreCase("null")
					|| ((dGroupVal.split("'")[0].trim().isEmpty()))) {
				dGroupVal2 = " dGroupVal IS NULL ";
			} else {
				dGroupVal2 = " dGroupVal = " + "'" + dGroupVal + "' ";
			}

			if (dGroupCol == null || dGroupCol.equalsIgnoreCase("null")
					|| ((dGroupCol.split("'")[0].trim().isEmpty()))) {
				dGroupCol2 = " dGroupCol IS NULL ";
			} else {
				dGroupCol2 = " dGroupCol = " + "'" + dGroupCol + "' ";
			}

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String SQL = "update " + tableName + " set status = 'True', userName = '" + userName + "', Time = '"
					+ dtf.format(now) + "' where Run = " + run + " and status = 'Rejected' and uniqueValues = '"
					+ uniqueValues + "' and colName = '" + colName + "'" + " and " + dGroupVal2 + " and " + dGroupCol2;
			LOG.debug("undoDataDriftRejectInd   "+SQL);

			jdbcTemplate1.execute(SQL);
			status = true;
		} catch (Exception E) {
			LOG.error(E.getMessage());

		}
		return status;
	}

	@Override
	public List<ProcessData> getProcessDataTableDetails(long idApp , String fromDate, String toDate) {
		List<ProcessData> processDataList = new ArrayList<>();

		String sql = "SELECT idApp,Run,Date,folderName from processData where idApp = " + idApp + " and Date between '" + fromDate + "' and '"  +toDate+ "' ";
		LOG.debug("SQLQUERY" + sql);
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		while (rs.next()) {
			ProcessData processData = new ProcessData();
			processData.setIdApp(rs.getLong("idApp"));
			processData.setRun(rs.getInt("Run"));
			processData.setDate(rs.getDate("Date"));
			processData.setFolderName(rs.getString("folderName"));
			processDataList.add(processData);
		}
		return processDataList;
	}

	@Override
	public List<SqlRule> getSqlRules(long idApp, String fromDate, String toDate) {
		List<SqlRule> sqlRules = new ArrayList<>();

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "select rulename, to_date(date::text, 'YYYY-MM-DD') as date, run, status, total_failed_records, '' as download_csv_link "
					+ " from data_quality_sql_rules" + " where idapp = '" + idApp + "'" + " and Date between '"
					+ fromDate + "' and '" + toDate + "' order by date desc";
		} else {
			sql = "select rulename, date_format(date, '%Y-%m-%d') as date, run, status, total_failed_records, '' as download_csv_link "
					+ " from data_quality_sql_rules" + " where idapp = '" + idApp + "'" + " and Date between '"
					+ fromDate + "' and '" + toDate + "' order by date desc";
		}
		LOG.debug("SQLQUERY" + sql);
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		while (rs.next()) {
			SqlRule sqlRule = new SqlRule();
			sqlRule.setRuleName(rs.getString("rulename"));
			sqlRule.setDate(rs.getString("date"));
			sqlRule.setRun(rs.getInt("run"));
			String status = rs.getString("status");
			if (status.equalsIgnoreCase("true")) {
				sqlRule.setStatus("passed");
			} else {
				sqlRule.setStatus("failed");
			}
			sqlRule.setTotalFailedRecords(rs.getString("total_failed_records"));
			sqlRules.add(sqlRule);
		}
		return sqlRules;
	}

	// For Max Length

	@Override
	public String getMaxLengthResultTypeFromResultMasterTbl(Long idApp) {
		String resultType = "";
		try {
			// select Result_Type from result_master_table where Appid=4140 and
			// Result_Type='MaxLength';

			String sql = "select Result_Type from result_master_table where Appid=" + idApp
					+ " and Result_Type='MaxLength'";
			resultType = jdbcTemplate1.queryForObject(sql, String.class);
			return resultType;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultType;
	}

	@Override
	public List<Date> getListOfDateFromColumnSummary(String paramStrTableName, String idApp) {

		try {
			List<Date> listDate = new ArrayList<Date>();

			String strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName;
			if (paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strDistinctRunQuery = strDistinctRunQuery + " where idApp=" + idApp;
			}
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			return listDate;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfDateFromColumnSummary : Exception :: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getListOfRunFromColumnSummary(String paramStrTableName, String idApp) {
		try {
			List<Integer> listRun = new ArrayList<Integer>();
			String strDistinctRunQuery = "SELECT distinct run FROM " + paramStrTableName;

			if (paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strDistinctRunQuery = strDistinctRunQuery + " where idApp=" + idApp;
			}

			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listRun.add(sqlRowSet_strDistinctRunQuery.getInt("run"));
			}
			Collections.sort(listRun);
			LOG.debug("Pagination Controller : getListOfRunFromColumnSummary : listRun :: " + listRun);
			return listRun;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfRunFromColumnSummary : Exception :: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<DataQualityMasterDashboard> getValidationDetailsByFilter(String applicationIds, String filterCondition,
			String fromDate, String toDate, String connectionName, String fileName) {
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		if (!applicationIds.isEmpty()) {
			try {
				String sql = "select t1.*, t2.test_run,t3.recordCount from data_quality_dashboard t1  "
						+ "left join (select idapp,execution_date,run,test_run, max(uniqueId) from app_uniqueId_master_table "
						+ "group by idapp,execution_date,run,test_run) t2 on t1.idApp = t2.idapp and t1.Date = t2.execution_date "
						+ "and t1.Run = t2.run left join (select idApp, Date, Run, Sum(RecordCount) AS recordCount from DATA_QUALITY_Transactionset_sum_A1 group by idApp, Date, Run) t3 "
						+ "on t1.IdApp=t3.idApp and t1.date=t3.Date and t1.run=t3.Run where t1.idApp in ("
						+ applicationIds + ") and (execution_date between '" + fromDate + "' and '" + toDate + "') "
						+ filterCondition + " order by t1.date desc ,t1.idapp limit 1000";
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					DataQualityMasterDashboard dashboard = new DataQualityMasterDashboard();
					Long idApp = queryForRowSet.getLong("idApp");
					try {
						String templateSql = "select la.idData, ls.schemaName as connectionName, ls.fileNamePattern as fileName from listApplications la join listDataSources lds on la.idData=lds.idData "
								+ " left join listDataSchema ls on lds.idDataSchema=ls.idDataSchema where la.idApp="
								+ idApp;
						Map<String, Object> la_data = jdbcTemplate.queryForMap(templateSql);
						dashboard.setTemplateId(Long.valueOf(la_data.get("idData").toString()));
						dashboard.setConnectionName(String.valueOf(la_data.get("connectionName")));
						String fileSql = "select folderName as fileName from processData where idApp=" + idApp
								+ " limit 1";
						Map<String, Object> fileData = jdbcTemplate1.queryForMap(fileSql);
						dashboard.setFileName(String.valueOf(fileData.get("fileName")));
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					dashboard.setIdApp(idApp);
					dashboard.setDate(queryForRowSet.getString("date"));
					dashboard.setRun(queryForRowSet.getLong("run"));
					dashboard.setTestRun(queryForRowSet.getString("test_run"));
					dashboard.setValidationCheckName(queryForRowSet.getString("validationCheckName"));
					dashboard.setSource1(queryForRowSet.getString("sourceName"));
					dashboard.setRecordCountStatus(queryForRowSet.getString("recordCountStatus"));
					dashboard.setNullCountStatus(queryForRowSet.getString("nullCountStatus"));
					dashboard.setStringFieldStatus(queryForRowSet.getString("stringFieldStatus"));
					dashboard.setNumericalFieldStatus(queryForRowSet.getString("numericalFieldStatus"));
					dashboard.setRecordAnomalyStatus(queryForRowSet.getString("recordAnomalyStatus"));
					dashboard.setUserSelectedFieldsStatus(queryForRowSet.getString("userSelectedFieldStatus"));
					dashboard.setPrimaryKeyStatus(queryForRowSet.getString("primaryKeyStatus"));
					dashboard.setDataDriftStatus(queryForRowSet.getString("dataDriftStatus"));
					dashboard.setProjectName(getProjectNameOfIdapp(queryForRowSet.getLong("idApp")));
					dashboard.setAggreagteDQI(queryForRowSet.getDouble("aggregateDQI")); // added
					dashboard.setRecordCount(queryForRowSet.getLong("recordCount"));
					if (!connectionName.equals("") && !fileName.equals("")) {
						if ((dashboard.getConnectionName().toLowerCase()).contains(connectionName.toLowerCase())
								&& (dashboard.getFileName().toLowerCase()).contains(fileName.toLowerCase())) {
							masterDashboard.add(dashboard);
							continue;
						} else {
							continue;
						}
					}
					if (!connectionName.equals("")) {
						if ((dashboard.getConnectionName().toLowerCase()).contains(connectionName.toLowerCase())) {
							masterDashboard.add(dashboard);
							continue;
						} else {
							continue;
						}
					}
					if (!fileName.equals("")) {
						if ((dashboard.getFileName().toLowerCase()).contains(fileName.toLowerCase())) {
							masterDashboard.add(dashboard);
							continue;
						} else {
							continue;
						}
					}
					masterDashboard.add(dashboard);
				}
			} catch (Exception e) {
				LOG.error("\n====>Exception occured while fetching details from data_quality_dashboard");
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return masterDashboard;
	}

	@Override
	public List<Map<String, Object>> getTotalTableMonitored(String fromDate, String toDate) {
		List<Map<String, Object>> dqiMapList = new ArrayList<Map<String, Object>>();

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT endTime::date as date, count(id) as tableProcessed FROM runScheduledTasks where status='completed' "
						+ " and endTime::date between '" + fromDate + "'::date and '" + toDate
						+ "'::date group by endTime::date order by endTime::date;";
			} else {
				sql = "SELECT CAST(endTime as DATE)as date, count(id) as tableProcessed FROM runScheduledTasks where status='completed' "
						+ " and endTime between CAST('" + fromDate + "' as datetime) and CAST('" + toDate
						+ "' as datetime) group by CAST(endTime as DATE) order by CAST(endTime as DATE);";
			}
			
			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> data = new HashMap<>();
					data.put("date", rs.getString("date"));
					data.put("tableProcessed", rs.getInt("tableProcessed"));
					dqiMapList.add(data);
				}
			}
			if (dqiMapList != null && !dqiMapList.isEmpty()) {
				return dqiMapList;
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public SqlRowSet getColumnAggregateDetailsForMicrosegmentNullCheck(long idApp, String tableName, String strMaxDate,
			int maxRun, ColumnAggregateRequest columnAggregateRequest, List<String> all_microseg_cols,
			List<String> groupby_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String type = columnAggregateRequest.getType().trim();
			String filterValues = columnAggregateRequest.getFilterValues().trim();
			String filterColumn = columnAggregateRequest.getFilterColumn().trim();

			String filterCondition = "";
			if (filterValues != null && !filterValues.isEmpty()) {
				filterCondition = " where " + filterColumn + " in ('" + filterValues.replaceAll(",", "','") + "') ";
			}
			LOG.debug("\n====> filterCondition: " + filterCondition);

			String microSegGroupByStr = "," + String.join(",", groupby_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as '" + microSegColName+"'";
				} else {
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as '"
							+ microSegColName+"'";
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				if (type.equalsIgnoreCase("individual")) {
					sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
							+ ", sum(Record_count) as totalRecords, sum(Null_value) as failedCount , (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage, "
							+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
							+ microSegSelectStr
							+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
							+ " and Date='" + strMaxDate + "'::date and Run=" + maxRun + ") A " + filterCondition
							+ " group by idApp,Date,Run,ColName" + microSegGroupByStr;

				} else if (type.equalsIgnoreCase("aggregate")) {

					if (filterValues != null && !filterValues.isEmpty()) {
						sql = "select idApp,Date,Run,ColName, sum(Record_count) as totalRecords, sum(Null_value) as failedCount, (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage,"
								+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
								+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value  "
								+ microSegSelectStr
								+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp="
								+ idApp + " and Date='" + strMaxDate + "'::date and Run=" + maxRun + ") A "
								+ filterCondition + " group by idApp,Date,Run,ColName";
					} else {
						sql = "select idApp,Date,Run,ColName, Max(Record_count) as totalRecords, sum(Null_value) as failedCount, (sum(Null_value)/Max(Record_count))*100.0 AS failPercentage,"
								+ " ((Max(Record_count) - sum(Null_value))/Max(Record_count))*100.0 AS passPercentage from "
								+ "(select idApp,Date,Run,ColName "+microSegSelectStr+",dGroupVal,dGroupCol, (select sum(RecordCount) from (select RecordCount"+microSegSelectStr+" from DATA_QUALITY_Transactionset_sum_dgroup where idApp="
								+ idApp + " and Date='" + strMaxDate + "'::date and Run='" + maxRun
								+ "') A where project_name='default') AS Record_Count, Null_Value from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp="
								+ idApp + " and Date='" + strMaxDate + "'::date and Run=" + maxRun
								+ ") A where project_name ='default' group by idApp,Date,Run,ColName";
					}
				}
			} else {
				if (type.equalsIgnoreCase("individual")) {
					sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
							+ ", sum(Record_count) as totalRecords, sum(Null_value) as failedCount , (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage, "
							+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
							+ microSegSelectStr
							+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
							+ " and Date='" + strMaxDate + "' and Run=" + maxRun + ") A " + filterCondition
							+ " group by idApp,Date,Run,ColName" + microSegGroupByStr;

				} else if (type.equalsIgnoreCase("aggregate")) {

					if (filterValues != null && !filterValues.isEmpty()) {
						sql = "select idApp,Date,Run,ColName, sum(Record_count) as totalRecords, sum(Null_value) as failedCount, (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage,"
								+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
								+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value  "
								+ microSegSelectStr
								+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp="
								+ idApp + " and Date='" + strMaxDate + "' and Run=" + maxRun + ") A " + filterCondition
								+ " group by idApp,Date,Run,ColName";
					} else {
						sql = "select idApp,Date,Run,ColName, Max(Record_count) as totalRecords, sum(Null_value) as failedCount, (sum(Null_value)/Max(Record_count))*100.0 AS failPercentage,"
								+ " ((Max(Record_count) - sum(Null_value))/Max(Record_count))*100.0 AS passPercentage from "
								+ "(select idApp,Date,Run,ColName"+microSegSelectStr+",dGroupVal,dGroupCol, (select sum(RecordCount) from (select RecordCount "+microSegSelectStr+" from DATA_QUALITY_Transactionset_sum_dgroup where idApp="
								+ idApp + " and Date='" + strMaxDate + "' and Run='" + maxRun
								+ "') A where project_name='default') AS Record_Count, Null_Value from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp="
								+ idApp + " and Date='" + strMaxDate + "' and Run=" + maxRun
								+ ") A where project_name ='default' group by idApp,Date,Run,ColName";
					}
				}

			}

			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentNullCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) 
				sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, sum(Null_value) as failedCount , (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage, "
						+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "'::date and Run=" + maxRun
						+ ") A group by idApp,Date,Run,ColName" + microSegGroupByStr;
			 else 
				sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, sum(Null_value) as failedCount , (sum(Null_value)/sum(Record_count))*100.0 AS failPercentage, "
						+ " ((sum(Record_count) - sum(Null_value))/sum(Record_count))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "' and Run=" + maxRun + ") A group by idApp,Date,Run,ColName"
						+ microSegGroupByStr;
			
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public List<String> getMicrosegmentsForNullCheck(long idApp, String strMaxDate, int maxRun) {
		List<String> all_microseg_cols = new ArrayList<>();
		try {

			// Read all microsegments from table
			String m_sql = "select dGroupCol from DATA_QUALITY_Column_Summary where Null_Threshold is not null and idApp="
					+ idApp + " and Date='" + strMaxDate + "' and Run=" + maxRun
					+ " and dGroupCol is not null and dGroupVal is not null limit 1";
			String dGroupCol = "";
			try {
				dGroupCol = jdbcTemplate1.queryForObject(m_sql, String.class);

			} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
				LOG.error(e.getMessage());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			LOG.debug("\n====> dGroupCol: " + dGroupCol);

			if (dGroupCol != null && !dGroupCol.isEmpty()) {
				String[] dupChkFieldsTokens = dGroupCol.split("\\?::\\?");
				for (int i = 0; i < dupChkFieldsTokens.length; ++i) {
					String microSegColName = dupChkFieldsTokens[i];
					all_microseg_cols.add(microSegColName);
				}
			}

			LOG.debug("\n====> all_microseg_cols: " + all_microseg_cols);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return all_microseg_cols;
	}

	@Override
	public List<String> getMicrosegmentsForCheck(long idApp, String tableName, String strMaxDate, int maxRun) {
		List<String> all_microseg_cols = new ArrayList<>();
		try {

			// Read all microsegments from table
			String m_sql = "select dGroupCol from " + tableName + " where idApp=" + idApp + " and Date='" + strMaxDate
					+ "' and Run=" + maxRun + " and dGroupCol is not null and dGroupVal is not null limit 1";
			String dGroupCol = "";
			try {
				dGroupCol = jdbcTemplate1.queryForObject(m_sql, String.class);

			} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
				LOG.error(e.getMessage());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			LOG.debug("\n====> dGroupCol: " + dGroupCol);
			if (dGroupCol != null && !dGroupCol.isEmpty()) {
				String[] dupChkFieldsTokens = dGroupCol.split("\\?::\\?");
				for (int i = 0; i < dupChkFieldsTokens.length; ++i) {
					String microSegColName = dupChkFieldsTokens[i];
					all_microseg_cols.add(microSegColName);
				}
			}

			LOG.debug("\n====> all_microseg_cols: " + all_microseg_cols);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return all_microseg_cols;
	}

	@Override
	public List<String> getMicrosegmentsForDuplicateCheck(long idApp, String tableName, String strMaxDate, int maxRun,
			String type) {
		List<String> all_microseg_cols = new ArrayList<>();
		try {

			// Read all microsegments from table
			String m_sql = "select dGroupCol from " + tableName + " where idApp=" + idApp + " and Date='" + strMaxDate
					+ "' and Run=" + maxRun + " and Type = '" + type
					+ "' and dGroupCol is not null and dGroupVal is not null limit 1";
			String dGroupCol = "";
			try {
				dGroupCol = jdbcTemplate1.queryForObject(m_sql, String.class);

			} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
				LOG.error(e.getMessage());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			LOG.debug("\n====> dGroupCol: " + dGroupCol);

			if (dGroupCol != null && !dGroupCol.isEmpty()) {
				String[] dupChkFieldsTokens = dGroupCol.split("\\?::\\?");
				for (int i = 0; i < dupChkFieldsTokens.length; ++i) {
					String microSegColName = dupChkFieldsTokens[i];
					all_microseg_cols.add(microSegColName);
				}
			}

			LOG.debug("\n====> all_microseg_cols: " + all_microseg_cols);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return all_microseg_cols;
	}

	@Override
	public SqlRowSet getColumnAggregateDetailsForMicrosegmentGlobalRules(long idApp, String tableName,
			String strMaxDate, int maxRun, ColumnAggregateRequest columnAggregateRequest,
			List<String> all_microseg_cols, List<String> groupby_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String type = columnAggregateRequest.getType().trim();
			String filterValues = columnAggregateRequest.getFilterValues().trim();
			String filterColumn = columnAggregateRequest.getFilterColumn().trim();

			String filterCondition = "";
			if (filterValues != null && !filterValues.isEmpty()) {
				filterCondition = " where " + filterColumn + " in ('" + filterValues.replaceAll(",", "','") + "') ";
			}
			LOG.debug("\n====> filterCondition: " + filterCondition);
			String microSegGroupByStr = "," + String.join(",", groupby_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) 
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				 else
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				if (type.equalsIgnoreCase("individual")) {
					sql = "select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
							+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
							+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
							+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
							+ strMaxDate + "'::date and Run=" + maxRun
							+ " and dGroupCol is not null and dGroupVal is not null) A " + filterCondition
							+ " group by idApp,Date,Run,ruleName,dimension_name" + microSegGroupByStr;

				} else if (type.equalsIgnoreCase("aggregate")) {
					sql ="select idApp,Date,Run,ruleName,dimension_name, sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount,  (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
							+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ruleName,dimension_name, totalRecords, totalFailed from (select  * "+microSegSelectStr+" from DATA_QUALITY_GlobalRules "
							+ "where idApp="+idApp+" and  Date='"+strMaxDate+"'::Date and Run ="+maxRun+" and dGroupCol is not null and dGroupVal is not null) A where project_name='default') B "
							+ " group by idApp,Date,Run,ruleName,dimension_name";
				}
			} else {
				if (type.equalsIgnoreCase("individual")) {
					sql = "select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
							+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
							+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
							+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
							+ strMaxDate + "' and Run=" + maxRun
							+ " and dGroupCol is not null and dGroupVal is not null) A " + filterCondition
							+ " group by idApp,Date,Run,ruleName,dimension_name" + microSegGroupByStr;

				} else if (type.equalsIgnoreCase("aggregate")) {
					sql ="select idApp,Date,Run,ruleName,dimension_name, sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount,  (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
							+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
							+ "(select idApp,Date,Run,ruleName,dimension_name, totalRecords, totalFailed from (select  * "+microSegSelectStr+" from DATA_QUALITY_GlobalRules "
						    + "where idApp="+idApp+" and  Date='"+strMaxDate+"' and Run ="+maxRun+" and dGroupCol is not null and dGroupVal is not null) A where project_name='default') B "
						    + " group by idApp,Date,Run,ruleName,dimension_name";
				}
			}

			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
			LOG.error(e.getMessage());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentGlobalRules(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all Microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;

				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
						+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr;
			}else {
				sql = "select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
						+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr;
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
			LOG.error(e.getMessage());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getColumnNonAggregateDetailsForMicrosegmentDuplicateCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols, String type) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all Microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)){
				sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
						+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr;
			}else{
				sql = "select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as failedCount, (sum(totalFailed)/sum(totalRecords))*100.0 AS failPercentage,"
						+ " ((sum(totalRecords) - sum(totalFailed))/sum(totalRecords))*100.0 AS passPercentage from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr;
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
			LOG.error("Dao - getColumnNonAggregateDetailsForMicrosegmentDuplicateCheck: " + e.getMessage());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public Map<String, String> getColumnDimensionsForCheck(String check_name, long idApp) {
		Map<String, String> result_map = new HashMap<String, String>();
		try {
			String sql = "select t1.column_name as column_name, t2.dimensionName as dimension_name from listApplicationsRulesCatalog t1 "
					+ " left outer join dimension t2 on  t1.dimension_id=t2.idDimension where t1.idApp=? and t1.rule_type=?;";

			List<Map<String, Object>> dmns_list = jdbcTemplate.queryForList(sql, idApp, check_name);

			if (dmns_list != null && dmns_list.size() > 0) {
				for (Map<String, Object> col_dmns : dmns_list) {
					Object column = col_dmns.get("column_name");
					Object dimension_name = col_dmns.get("dimension_name");

					if (column != null && dimension_name != null)
						result_map.put(column.toString(), dimension_name.toString());
				}
			}

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result_map;
	}

	@Override
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentGlobalRules(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all Microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "(select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as count, 'bad' as quality from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr + " ) union (select idApp,Date,Run,ruleName,dimension_name "
						+ microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, (sum(totalRecords) - sum(totalFailed)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr + " )";
			} else {
				sql = "(select idApp,Date,Run,ruleName,dimension_name " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as count, 'bad' as quality from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr + " ) union (select idApp,Date,Run,ruleName,dimension_name "
						+ microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, (sum(totalRecords) - sum(totalFailed)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,ruleName,dimension_name,dGroupVal,dGroupCol, totalRecords, totalFailed "
						+ microSegSelectStr + " from DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ruleName,dimension_name"
						+ microSegGroupByStr + " )";
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
			LOG.error(e.getMessage());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentDuplicateCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols, String type) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all Microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {
				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)){
				sql = "(select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as count, 'bad' as quality from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " ) union (select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, (sum(totalRecords) - sum(totalFailed)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "'::date and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " )";
			}else{
				sql = "(select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, sum(totalFailed) as count, 'bad' as quality from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " ) union (select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(totalRecords) as totalRecords, (sum(totalRecords) - sum(totalFailed)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,duplicateCheckFields as ColName ,dGroupVal,dGroupCol,TotalCount  as totalRecords, Duplicate as totalFailed "
						+ microSegSelectStr + " from " + tableName + "  where idApp=" + idApp + " and Date='"
						+ strMaxDate + "' and Run=" + maxRun + " and Type='" + type + "' "
						+ " and dGroupCol is not null and dGroupVal is not null) A  group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " )";
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
			LOG.error("Dao - getColumnNonAggregateSummaryForMicrosegmentDuplicateCheck: " + e.getMessage());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getColumnNonAggregateSummaryForMicrosegmentNullCheck(long idApp, String tableName,
			String strMaxDate, int maxRun, List<String> all_microseg_cols) {

		SqlRowSet sqlRowSet = null;
		try {
			String microSegGroupByStr = "," + String.join(",", all_microseg_cols);
			LOG.debug("\n====> microSegGroupByStr: " + microSegGroupByStr);

			// Read all microsegments from table
			String microSegSelectStr = "";

			int i = 1;
			for (String microSegColName : all_microseg_cols) {

				// Query compatibility changes for both POSTGRES and MYSQL
				String microSegValue = "";
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					microSegValue = ", SPLIT_PART(dGroupVal, '?::?', " + i + ") as " + microSegColName;
				} else {
					//microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '-', " + i + "),'-',-1) as "
					//		+ microSegColName;
					microSegValue = ", SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', " + i + "),'?::?',-1) as "
							+ microSegColName;
				}
				microSegSelectStr = microSegSelectStr + microSegValue;
				++i;
			}
			LOG.debug("\n====> microSegSelectStr: " + microSegSelectStr);

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "(select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, sum(Null_value) as count, 'bad' as quality  from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "'::date and Run=" + maxRun
						+ ") A group by idApp,Date,Run,ColName" + microSegGroupByStr
						+ " ) union (select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, (sum(Record_count) - sum(Null_value)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "'::date and Run=" + maxRun
						+ ") A group by idApp,Date,Run,ColName" + microSegGroupByStr + " )";
			} else {
				sql = "(select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, sum(Null_value) as count, 'bad' as quality  from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "' and Run=" + maxRun + ") A group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " ) union (select idApp,Date,Run,ColName " + microSegGroupByStr
						+ ", sum(Record_count) as totalRecords, (sum(Record_count) - sum(Null_value)) as count, 'good' as quality from "
						+ "(select idApp,Date,Run,ColName,dGroupVal,dGroupCol, Record_Count, Null_Value "
						+ microSegSelectStr
						+ " from DATA_QUALITY_Column_Summary  where Null_Threshold is not null and idApp=" + idApp
						+ " and Date='" + strMaxDate + "' and Run=" + maxRun + ") A group by idApp,Date,Run,ColName"
						+ microSegGroupByStr + " )";
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public JSONArray getRCATrendChart(Long idApp, String fromDate, String toDate) {

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select Date,Run, RecordCount from DATA_QUALITY_Transactionset_sum_A1 where Date > '" + fromDate
						+ "'::date AND Date <'" + toDate + "'::date and idApp =" + idApp;

			} else {
				sql = "select Date,Run, RecordCount from DATA_QUALITY_Transactionset_sum_A1 where Date > '" + fromDate
						+ "' AND Date <'" + toDate + "' and idApp =" + idApp;
			}
			SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);
			JSONArray jsonArray = new JSONArray();

			while (resultSet.next()) {

				JSONObject obj = new JSONObject();

				long recordCount = resultSet.getLong("RecordCount");
				String date = "" + resultSet.getObject("Date");
				String run = "" + resultSet.getObject("Run");
				int runCount = Integer.parseInt(run);
				if (runCount < 10)
					run = "0" + run;
				String dateRun = date + "(" + run + ")";
				obj.put("dateRun", dateRun);
				obj.put("recordCount", recordCount);
				jsonArray.put(obj);
			}
			return jsonArray;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public JSONObject getDeviationStatisticsByTestName(long idApp, String testName){
		String sql="";
		SqlRowSet sqlRowSet = null;
		JSONObject dqiStatObj= new JSONObject();
		try{
			sql="select count(*) from DashBoard_Summary where AppId="+idApp+" and Test='"+testName+"'";
			int numOfRuns= jdbcTemplate1.queryForObject(sql, Integer.class);

			if(numOfRuns >=3){

				// select latest 30 records excluding current run
				if(numOfRuns > 30)
					numOfRuns=30;

				sql= "select AVG(Key_Metric_2) as meanVal,STDDEV(Key_Metric_2) as stdDevVal from (select Key_Metric_2 from DashBoard_Summary " +
						"where AppId="+idApp+" and Test='"+testName+"' order by Date desc,Run desc limit "+numOfRuns+" offset 1) alias";

				sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

				if(sqlRowSet!=null && sqlRowSet.next()){
					dqiStatObj.put("meanVal",sqlRowSet.getObject("meanVal"));
					dqiStatObj.put("stdDevVal",sqlRowSet.getObject("stdDevVal"));

					sql="select Key_Metric_2 as currentVal from DashBoard_Summary where AppId="+idApp+" and Test='"+testName+"' and Date=(select MAX(Date) from DashBoard_Summary " +
							"where AppId="+idApp+" and Test='"+testName+"') order by Run desc limit 1";

					LOG.debug(sql);
					Long currentVal= jdbcTemplate1.queryForObject(sql, Long.class);
					if(currentVal!=null)
						dqiStatObj.put("currentVal",currentVal);
				}
			}

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dqiStatObj;
	}

	public List<String> getDashboardSummaryTestNamesByIdApp(long idApp){
		List<String> testNames = new ArrayList<>();
		String sql="";
		try{
			sql="select distinct(Test) from DashBoard_Summary where AppId="+idApp+" and Test not in ('DQ_Record Count Fingerprint')";
			testNames = jdbcTemplate1.queryForList(sql,String.class);
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return testNames;
	}

	@Override
	public HashMap<String, Double> getDqGraphEnterpriseData(String fromDate, String toDate) {
		SqlRowSet oSqlRowSet = null;
		HashMap<String, Double> oRetValue = new HashMap<String, Double>();
		try {
			String sSqlQry = "";
			 if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				  sSqlQry = "select avg(DQI) as dqi, REPLACE (date, '+00', '') as Date, count(date) from DashBoard_Summary ";
					sSqlQry = sSqlQry + String.format("where Date between '%1$s' and '%2$s' \n", fromDate, toDate);
					sSqlQry = sSqlQry + "group by Date order by Date asc";
					oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			 }else {
				 sSqlQry = "select avg(DQI) as dqi, Date, count(date) from DashBoard_Summary ";
			sSqlQry = sSqlQry + String.format("where Date between '%1$s' and '%2$s' \n", fromDate, toDate);
			sSqlQry = sSqlQry + "group by Date order by Date asc";
			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			 }
			while (oSqlRowSet.next()) {
				Double dqi = Double.parseDouble(String.format("%.2f", oSqlRowSet.getDouble("dqi")));
				oRetValue.put(oSqlRowSet.getString("Date"), dqi);
			}
		} catch (Exception oException) {
			oRetValue = new HashMap<String, Double>();
			oException.printStackTrace();
			LOG.error("\n=====>Could not retrieve data from DashBoard_Summary");
			LOG.error(oException.getMessage());
		}
		return oRetValue;
	}

	@Override
	public String getLatestDqI(String fromDate, String toDate) {
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;
		String sRetValue = "0.0,0000-00-00";
		double dAvgDqIndex = 0.0;
		try {
			 if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sSqlQry = "select avg(dqi) as aggregateDqIndex,REPLACE (date, '+00', '') as Date from DashBoard_Summary where Date between '" + fromDate + "' and '" + toDate + "' group by DATE \n" +
					"ORDER BY Date DESC LIMIT 1;";
			 }
			 else {
				 sSqlQry = "select avg(dqi) as aggregateDqIndex, Date from DashBoard_Summary where Date between '" + fromDate + "' and '" + toDate + "' group by DATE \n" +
							"ORDER BY Date DESC LIMIT 1;";
			 }
			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

			if (oSqlRowSet.next()) {
				dAvgDqIndex = oSqlRowSet.getDouble("aggregateDqIndex");
				sRetValue = String.format("%.2f,%2$s", dAvgDqIndex, oSqlRowSet.getString("Date"));
			}
		} catch (Exception oException) {
			sRetValue = "0.0,0000-00-00";
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return sRetValue;
	}

	@Override
	public int getTotalRunCount(long idApp) {
		int totalCount = 0;

		String Query = "";
		try {
			String apptype = "select appType as appType from listApplications where idApp = " + idApp;
			SqlRowSet apptypeRS = jdbcTemplate.queryForRowSet(apptype);
			String appType = "";
			if (apptypeRS.next()) {
				appType = apptypeRS.getString("appType");
			}

			if (appType.equals("Schema Matching")) {
				Query = "select count(*) as Run from Schema_Matching_" + idApp
						+ "_Transaction_Summary where idApp=" + idApp;
			} else if (appType.equals("Data Matching")) {
				Query = "select count(*) as Run from DATA_MATCHING_" + idApp + "_SUMMARY where idApp=" + idApp;
			} else if (appType.equals("Data Forensics")) {
				Query = "select count(*) as Run from DATA_QUALITY_Transactionset_sum_A1  where idApp=" + idApp;
			} else if (appType.equals("Primary Key Matching")) {
				Query = "select count(*) as Run from DATA_MATCHING_" + idApp + "_SUMMARY where idApp=" + idApp;
			}

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(Query);
			if (queryForRowSet.next()) {
				totalCount = queryForRowSet.getInt("Run");
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return totalCount;
	}

	@Override
	public Map<String, DataQualityMasterDashboard> getPreviousRunDetailsForValidation(long idApp) {
		Map<String, DataQualityMasterDashboard> dataQualityMasterDashboardMap = new HashMap<>();
		try {
			String sql = "SELECT ag2.appId, ag2.date,ag2.dqi,ag2.run,ag2.test as checkName FROM DashBoard_Summary ag2 JOIN (SELECT * FROM (SELECT DATE,run, AppId FROM DashBoard_Summary WHERE appId=" + idApp + " GROUP BY DATE, run " +
					"ORDER BY DATE DESC, Run DESC LIMIT 2) t1 ORDER BY DATE ASC, Run ASC LIMIT 1) ag1 ON ag1.appId=ag2.appId AND ag1.date=ag2.date AND ag1.Run=ag2.run";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (sqlRowSet.next()) {
				DataQualityMasterDashboard dqmd = new DataQualityMasterDashboard();
				dqmd.setRun(sqlRowSet.getLong("run"));
				dqmd.setValidationCheckName(sqlRowSet.getString("checkName"));
				dqmd.setAggreagteDQI(sqlRowSet.getDouble("dqi"));
				dataQualityMasterDashboardMap.put(sqlRowSet.getString("checkName"), dqmd);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return dataQualityMasterDashboardMap;
	}

	@Override
	public Map<String, DataQualityMasterDashboard> getLatestRunDetailsForValidation(long idApp) {
		Map<String, DataQualityMasterDashboard> dataQualityMasterDashboardMap = new HashMap<>();
		try {
			String sql = "SELECT run,test as checkName, dqi FROM DashBoard_Summary WHERE AppId = " + idApp + " AND Date = (SELECT max(Date) \n" +
					"FROM DashBoard_Summary WHERE AppId = " + idApp + ") AND Run = (SELECT max(Run) FROM DashBoard_Summary WHERE AppId = " + idApp + ") AND Date = (SELECT max(Date) FROM DashBoard_Summary WHERE AppId = " + idApp + ")";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (sqlRowSet.next()) {
				DataQualityMasterDashboard dqmd = new DataQualityMasterDashboard();
				dqmd.setRun(sqlRowSet.getLong("run"));
				dqmd.setValidationCheckName(sqlRowSet.getString("checkName"));
				dqmd.setAggreagteDQI(sqlRowSet.getDouble("dqi"));
				dataQualityMasterDashboardMap.put(sqlRowSet.getString("checkName"), dqmd);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return dataQualityMasterDashboardMap;
	}

	@Override
	public Map<String, DataQualityMasterDashboard> getHistoricalAggregateDetailsForValidation(long idApp) {
		Map<String, DataQualityMasterDashboard> dataQualityMasterDashboardMap = new HashMap<>();
		try {
			String sql = "SELECT ag5.appId, count(*) as runcount, (Avg(ag5.avgDQI) - 3*stddev(ag5.avgDQI)) AS  lowerlimit, (Avg(ag5.avgDQI) + 3*stddev(ag5.avgDQI)) AS upperlimit, ag5.checkName,Avg(ag5.avgDQI) as dqi " +
					" FROM ( SELECT ag3.appId, ag3.Date, ag3.Run, AVG(ag3.DQI) AS avgDQI, ag3.test as checkName FROM DashBoard_Summary ag3 " +
					" LEFT OUTER JOIN ( SELECT ag1.appId, ag1.Date, MAX(ag1.Run) as Run from DashBoard_Summary ag1 " +
					" JOIN (SELECT appId, MAX(DATE) as maxDate FROM DashBoard_Summary WHERE appId=" + idApp + " GROUP BY appId) ag2 ON ag1.appId=ag2.appId AND ag1.Date=ag2.maxDate " +
					" GROUP BY ag1.appId,ag1.Date ) ag4 ON ag3.appId = ag4.appId AND ag3.Date=ag4.Date AND ag3.Run = ag4.Run " +
					" WHERE ag4.Date is null AND ag4.Run is null AND ag3.appId = " + idApp +
					" GROUP BY ag3.appId, ag3.Date, ag3.Run, ag3.test " +
					" ) ag5 GROUP BY ag5.appId, ag5.checkName";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (sqlRowSet.next()) {
				DataQualityMasterDashboard dqmd = new DataQualityMasterDashboard();
				dqmd.setRun(sqlRowSet.getLong("runcount"));
				dqmd.setValidationCheckName(sqlRowSet.getString("checkName"));
				dqmd.setAggreagteDQI(sqlRowSet.getDouble("dqi"));

				Double upperlimit = sqlRowSet.getDouble("upperlimit");
				if (upperlimit != null && upperlimit > 100) {
					upperlimit = new Double(100);
				}
				Double lowerlimit = sqlRowSet.getDouble("lowerlimit");
				if (lowerlimit != null && lowerlimit < 0) {
					lowerlimit = new Double(0);
				}
				dqmd.setUpperLimit(upperlimit);
				dqmd.setLowerLimit(lowerlimit);

				dataQualityMasterDashboardMap.put(sqlRowSet.getString("checkName"), dqmd);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return dataQualityMasterDashboardMap;
	}
	@Override
	public Map<String, DataQualityMasterDashboard> getHistoricalOverallAggregateDetailsForValidation(long idApp) {
		Map<String, DataQualityMasterDashboard> dataQualityMasterDashboardMap = new HashMap<>();
		try {
			String sql = "SELECT ag5.appId,COUNT(*) AS runcount, (AVG(ag5.avgDQI) - 3 * STDDEV(ag5.avgDQI)) AS lowerlimit, (AVG(ag5.avgDQI) + 3 * STDDEV(ag5.avgDQI)) AS upperlimit,AVG(ag5.avgDQI) as dqi FROM (SELECT ag3.appId, ag3.Date, ag3.Run, AVG(ag3.DQI) AS avgDQI FROM DashBoard_Summary ag3 LEFT OUTER JOIN (SELECT ag1.appId, ag1.Date, MAX(ag1.Run) AS Run FROM DashBoard_Summary ag1 JOIN (SELECT appId, MAX(DATE) AS maxDate FROM DashBoard_Summary WHERE appId = "
					+ idApp
					+ " GROUP BY appId) ag2 ON ag1.appId = ag2.appId AND ag1.Date = ag2.maxDate GROUP BY ag1.appId , ag1.Date) ag4 ON ag3.appId = ag4.appId AND ag3.Date = ag4.Date AND ag3.Run = ag4.Run WHERE ag4.Date IS NULL AND ag4.Run IS NULL AND ag3.appId = "
					+ idApp + " GROUP BY ag3.appId , ag3.Date , ag3.Run) ag5 GROUP BY ag5.appId";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (sqlRowSet.next()) {
				DataQualityMasterDashboard dqmd = new DataQualityMasterDashboard();
				dqmd.setRun(sqlRowSet.getLong("runcount"));
				dqmd.setAggreagteDQI(sqlRowSet.getDouble("dqi"));

				Double upperlimit = sqlRowSet.getDouble("upperlimit");
				if (upperlimit != null && upperlimit > 100) {
					upperlimit = new Double(100);
				}
				Double lowerlimit = sqlRowSet.getDouble("lowerlimit");
				if (lowerlimit != null && lowerlimit < 0) {
					lowerlimit = new Double(0);
				}
				dqmd.setUpperLimit(upperlimit);
				dqmd.setLowerLimit(lowerlimit);
				dataQualityMasterDashboardMap.put("result",dqmd);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return dataQualityMasterDashboardMap;
	}
	@Override
	public double getLatestOverAllDtsDetailsForValidation(long idApp) {
		double dts = 0.0d;
		try {
//			String sql = "SELECT  avg(dqi) as dts FROM DashBoard_Summary WHERE AppId = " + idApp
//					+ " AND Date = (SELECT max(Date) FROM DashBoard_Summary WHERE AppId = " + idApp
//					+ ") AND Run = (SELECT max(Run) FROM DashBoard_Summary WHERE AppId =" + idApp
//					+" AND Date = (SELECT max(Date) FROM DashBoard_Summary WHERE AppId = " + idApp+ "))";
			
			String sql = "SELECT aggregateDQI FROM data_quality_dashboard  WHERE idApp  in ("+ idApp + ")";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while(sqlRowSet.next()) {
				dts = sqlRowSet.getDouble("aggregateDQI");
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return dts;
	}

	@Override
	public SqlRowSet getColumnAggregateDetailsForNullCheck(long idApp, String tableName, String strMaxDate, int maxRun, ColumnAggregateRequest columnAggregateRequest) {
		SqlRowSet sqlRowSet = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

					sql = "select idApp,Date,Run,ColName, Record_count as totalRecords, Null_value as failedCount, (Null_value/Record_count)*100.0 AS failPercentage,"
							+ " ((Record_count - Null_value)/Record_count)*100.0 AS passPercentage from "
							+ " DATA_QUALITY_NullCheck_Summary where Null_Threshold is not null and idApp="
							+ idApp + " and Date='" + strMaxDate + "'::date and Run=" + maxRun;
			} else {
					sql = "select idApp,Date,Run,ColName, Record_count as totalRecords, Null_value as failedCount, (Null_value/Record_count)*100.0 AS failPercentage,"
							+ " ((Record_count - Null_value)/Record_count)*100.0 AS passPercentage from "
							+ " DATA_QUALITY_NullCheck_Summary  where Null_Threshold is not null and idApp="
							+ idApp + " and Date='" + strMaxDate + "' and Run=" + maxRun;
			}
			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}
	@Override
	public SqlRowSet getColumnAggregateDetailsForGlobalRule(long idApp, String tableName, String strMaxDate, int maxRun, ColumnAggregateRequest columnAggregateRequest) {
		SqlRowSet sqlRowSet = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

					sql = "select idApp,Date,Run,ruleName,dimension_name , totalRecords, totalFailed as failedCount, ((totalFailed)/(totalRecords))*100.0 AS failPercentage,"
							+ " ((totalRecords) - (totalFailed))/ (totalRecords)*100.0 AS passPercentage from "
							+ "DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
							+ strMaxDate + "'::date and Run=" + maxRun;
			} else {

					sql = "select idApp,Date,Run,ruleName,dimension_name , totalRecords, totalFailed as failedCount, ((totalFailed)/(totalRecords))*100.0 AS failPercentage,"
							+ " ((totalRecords) - (totalFailed))/ (totalRecords)*100.0 AS passPercentage from "
							+ "DATA_QUALITY_GlobalRules  where idApp=" + idApp + " and Date='"
							+ strMaxDate + "'and Run=" + maxRun;
			}

			LOG.debug("\n====> sql: " + sql);

			sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public Boolean checkRecordInRefTable(String columnName, String columnValue, String table_name, Map<String, String> metadata) {

		String[] data = columnValue.split("\\$\\$", -1);
		String insertColumns = columnName.replaceAll("\\$\\$", ",");
		// Get columnName
		String[] columnNames = insertColumns.split(",");

		int empty_values_count = 0;
		String condition = "";
		for (int i = 0; i <= data.length - 1; i++) {
			String colValue = data[i];
			String colName = columnNames[i];
			String colDataType = metadata.get(colName);
			if (colDataType.equalsIgnoreCase("date") || colDataType.toLowerCase().startsWith("varchar")) {
				condition += condition.equals("")? colName + "= '" + colValue + "' " : " AND "+colName+"="+"'" + colValue + "' ";
			} else {
				if (colValue == null || colValue.trim().isEmpty()){
					colValue = null;
					condition += condition.equals("")? colName+" is " + colValue + " " : " AND "+colName+" is " + colValue + " ";
				} else {
					condition += condition.equals("")? colName+"= '" + colValue + "' " : " AND "+colName+"= '" + colValue + "' ";
				}
			}
		}
		LOG.debug("Duplicate condition--->" + condition);

		if (condition.length() > 0) {
			try {
				String sql = "SELECT * FROM " + table_name + " WHERE " + condition;
				LOG.debug("===> Duplicate Query: " + sql);
				SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
				if(sqlRowSet.next()){
					return true;
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Map<String, String> getRefTableColumns(String tableName){
		Map<String, String> metadata = new HashMap<String, String>();
		try {
			String sql = "select * from " + tableName + " limit 1";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData sqlRowSetMetaData = sqlRowSet.getMetaData();
			for (int i = 1; i <= sqlRowSetMetaData.getColumnCount(); i++) {
				metadata.put(sqlRowSetMetaData.getColumnName(i), sqlRowSetMetaData.getColumnTypeName(i));
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	@Override
	public int deleteDataFromRefTable(String tableName, String dbkRowIds) {
		int deleteCount = 0;
		try {
			String sql = "delete from " + tableName + " where dbk_row_Id in ("+dbkRowIds+")";
			deleteCount = jdbcTemplate1.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return deleteCount;
	}

	@Override
	public Map<String, Object> getAvgDtsByIdApp(long idApp) {
		Map<String, Object> result = new HashMap<>();
		try {
			String sql = "SELECT * FROM data_quality_dashboard where idApp =" + idApp;
			LOG.debug("---getAvgDtsByIdApp------ Sql -----------" + sql);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);
			while(sqlRowSet.next()){
				result.put("aggregateDQI",sqlRowSet.getDouble("aggregateDQI"));
				result.put("validationName",sqlRowSet.getString("validationCheckName"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int updateApplicationPropertyByNameAndCategory(String propertyCategoryName, String propertyName, String propertyValue) {
		int updateCount=0;
		try{
			String sql="update databuck_property_details set property_value=? where property_category_id=" +
					"(select property_category_id from databuck_properties_master where lower(property_category_name)=?) and property_name=?";
			updateCount= jdbcTemplate.update(sql,propertyValue,propertyCategoryName,propertyName);
			System.out.println("\n====>Application Property:"+propertyName+" for category["+propertyCategoryName+"] is Auto updated with value["+propertyValue+"]");
		}catch (Exception e){
			e.printStackTrace();
		}
		return updateCount;
	}

	@Override
	public JSONArray getGraphDataForNullStatistics(long idApp, String colName) {
		JSONArray graphDataArray = new JSONArray();
		try{
			String sql="select Date,Run,Null_Percentage,Null_Threshold from DATA_QUALITY_NullCheck_Summary where idApp=? and ColName=?";

			System.out.println("sql:"+"select Date,Run,Null_Percentage,Null_Threshold from DATA_QUALITY_NullCheck_Summary where idApp="+idApp+" and ColName='"+colName+"'");
			SqlRowSet sqlRowSet= jdbcTemplate1.queryForRowSet(sql,idApp,colName);

			while (sqlRowSet.next()){
				String date= sqlRowSet.getString("Date");
				String run= ""+ sqlRowSet.getLong("Run");
				Double nullPercentage= sqlRowSet.getDouble("Null_Percentage");
				Double nullThreshold= sqlRowSet.getDouble("Null_Threshold");

				if(run.length()==1)
					run= "0"+run;

				JSONObject statDateObj= new JSONObject();
				statDateObj.put("date",date+" ("+run+")");
				statDateObj.put("nullPercentage",String.format("%.2f",nullPercentage));
				statDateObj.put("nullThreshold",String.format("%.2f",nullThreshold));

				graphDataArray.put(statDateObj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return graphDataArray;
	}

	@Override
	public JSONArray getGraphDataForLengthStatistics(long idApp, String colName) {
		JSONArray graphDataArray = new JSONArray();
		try{
			String sql="select Date,Run,FailedRecords_Percentage,Length_Threshold from DATA_QUALITY_Length_Check where idApp=? and ColName=?";

			System.out.println("sql:"+"select Date,Run,FailedRecords_Percentage,Length_Threshold from DATA_QUALITY_Length_Check where idApp="+idApp+" and ColName='"+colName+"'");
			SqlRowSet sqlRowSet= jdbcTemplate1.queryForRowSet(sql,idApp,colName);

			while (sqlRowSet.next()){
				String date= sqlRowSet.getString("Date");
				String run= ""+ sqlRowSet.getLong("Run");
				Double lengthPercentage= sqlRowSet.getDouble("FailedRecords_Percentage");
				Double lengthThreshold= sqlRowSet.getDouble("Length_Threshold");

				if(run.length()==1)
					run= "0"+run;

				JSONObject statDateObj= new JSONObject();
				statDateObj.put("date",date+" ("+run+")");
				statDateObj.put("failedRecordsPercentage",String.format("%.2f",lengthPercentage));
				statDateObj.put("lengthThreshold",String.format("%.2f",lengthThreshold));

				graphDataArray.put(statDateObj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return graphDataArray;
	}
	
	@Override
	public JSONArray getGraphDataForBadDataStatistics(long idApp, String colName) {
		JSONArray graphDataArray = new JSONArray();
		try{
			String sql="select Date,Run,badDataPercentage,badDataThreshold from DATA_QUALITY_badData where idApp=? and ColName=?";

			System.out.println("sql:"+"select Date,Run,badDataPercentage,badDataThreshold from DATA_QUALITY_badData where idApp=? and ColName=? where idApp="+idApp+" and ColName='"+colName+"'");
			SqlRowSet sqlRowSet= jdbcTemplate1.queryForRowSet(sql,idApp,colName);

			while (sqlRowSet.next()){
				String date= sqlRowSet.getString("Date");
				String run= ""+ sqlRowSet.getLong("Run");
				Double badDataPercentage= sqlRowSet.getDouble("badDataPercentage");
				Double badDataThreshold= sqlRowSet.getDouble("badDataThreshold");

				if(run.length()==1)
					run= "0"+run;

				JSONObject statDateObj= new JSONObject();
				statDateObj.put("date",date+" ("+run+")");
				statDateObj.put("badDataPercentage",String.format("%.2f",badDataPercentage));
				statDateObj.put("badDataThreshold",String.format("%.2f",badDataThreshold));

				graphDataArray.put(statDateObj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return graphDataArray;
	}

	@Override
	public JSONArray getGraphDataForRegexPatternStatistics(long idApp, String colName) {
		JSONArray graphDataArray = new JSONArray();
		try{
			String sql="select Date,Run,FailedRecords_Percentage,Pattern_Threshold from DATA_QUALITY_Unmatched_Pattern_Data where idApp=? and Col_Name=?";

			System.out.println("sql:"+"select Date,Run,FailedRecords_Percentage,Pattern_Threshold from DATA_QUALITY_Unmatched_Pattern_Data where idApp="+idApp+" and Col_Name='"+colName+"'");
			SqlRowSet sqlRowSet= jdbcTemplate1.queryForRowSet(sql,idApp,colName);

			while (sqlRowSet.next()){
				String date= sqlRowSet.getString("Date");
				String run= ""+ sqlRowSet.getLong("Run");
				Double patternPercentage= sqlRowSet.getDouble("FailedRecords_Percentage");
				Double patternThreshold= sqlRowSet.getDouble("Pattern_Threshold");

				if(run.length()==1)
					run= "0"+run;

				JSONObject statDateObj= new JSONObject();
				statDateObj.put("date",date+" ("+run+")");
				statDateObj.put("failedRecordsPercentage",String.format("%.2f",patternPercentage));
				statDateObj.put("patternThreshold",String.format("%.2f",patternThreshold));

				graphDataArray.put(statDateObj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return graphDataArray;
	}


	@Override
	public JSONArray getGraphDataForDuplicateStatistics(long idApp, String colName) {
		JSONArray graphDataArray = new JSONArray();
		try{
			String sql="select Date,Run,Percentage,Threshold from DATA_QUALITY_Duplicate_Check_Summary where idApp=? and duplicateCheckFields=?";

			System.out.println("sql:"+"select Date,Run,Percentage,Threshold from DATA_QUALITY_Duplicate_Check_Summary where idApp="+idApp+" and duplicateCheckFields='"+colName+"'");
			SqlRowSet sqlRowSet= jdbcTemplate1.queryForRowSet(sql,idApp,colName);

			while (sqlRowSet.next()){
				String date= sqlRowSet.getString("Date");
				String run= ""+ sqlRowSet.getLong("Run");
				Double duplicatePercentage= sqlRowSet.getDouble("Percentage");
				Double duplicateThreshold= sqlRowSet.getDouble("Threshold");

				if(run.length()==1)
					run= "0"+run;

				JSONObject statDateObj= new JSONObject();
				statDateObj.put("date",date+" ("+run+")");
				statDateObj.put("percentage",String.format("%.2f",duplicatePercentage));
				statDateObj.put("threshold",String.format("%.2f",duplicateThreshold));

				graphDataArray.put(statDateObj);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return graphDataArray;
	}
	
	public List<DateVsDTSGraph> getDtsVsDateGraphData(Long appId,String fromDate,String toDate) {
		List<DateVsDTSGraph> listDateVsDTSGraph = new ArrayList<DateVsDTSGraph>();
		String sql ="";
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "SELECT TO_CHAR(TO_DATE(Date,'YYYY-MM-DD'), 'YYYY-MM-DD') || ' (' || Run || ')' AS date, ROUND(CAST(AVG(DQI) AS numeric),2) AS avgDqi "
					+ "FROM DashBoard_Summary "
					+ "WHERE AppId = "+appId
					+ " AND DQI IS NOT NULL AND DQI >0 AND Date between '"+fromDate+"'"
					+ " AND '"+toDate+"'"
					+ " GROUP BY Date,Run;";
		}else {
			sql = "SELECT concat(DATE_FORMAT(Date,'%Y-%m-%d'),concat(' (',Run,')')) AS date, ROUND(AVG(DQI),2) AS avgDqi "
					+ "FROM DashBoard_Summary "
					+ "WHERE AppId = "+appId
					+ " AND DQI IS NOT NULL AND DQI >0 AND Date between '"+fromDate+"'"
					+ " AND '"+toDate+"'"
					+ " GROUP BY Date,Run;";
		}
		listDateVsDTSGraph = jdbcTemplate1.query(sql, new RowMapper<DateVsDTSGraph>() {
			@Override
			public DateVsDTSGraph mapRow(ResultSet rs, int rowNum) throws SQLException {
				DateVsDTSGraph dateVsDTSGraph = new DateVsDTSGraph();
				dateVsDTSGraph.setDate(rs.getString("date"));
				dateVsDTSGraph.setDts(rs.getString("avgDqi"));
				return dateVsDTSGraph;
			}
		});
			return listDateVsDTSGraph;
	}


	public List<Long> getAvgDQIofLastTwoRuns(Long appId,String test) {
		List<Long> avgDQIList = new ArrayList<Long>();
		List<AverageDQI> avgDQIObjList = new ArrayList<AverageDQI>();
		String sql = "SELECT avgDQI,runs,Dates FROM (SELECT avg(DQI) as avgDQI,Date as Dates,Run as runs,Test as Tests "
				+ "FROM DashBoard_Summary where AppId = "+appId+" AND Test = '"+test+"' "
				+ "group by Date,Run,Test order by Dates desc ,Run desc limit 30) as b limit 2";
		avgDQIObjList = jdbcTemplate1.query(sql, new RowMapper<AverageDQI>() {
			@Override
			public AverageDQI mapRow(ResultSet rs, int rowNum) throws SQLException {
				AverageDQI avgDQI = new AverageDQI();
				avgDQI.setAvgDQI(rs.getLong("avgDQI"));
				avgDQI.setDate(rs.getString("Dates"));
				avgDQI.setRun(rs.getInt("runs"));
				return avgDQI;
			}
		});
		
		if(avgDQIObjList!=null && avgDQIObjList.size()>1) {
			if(avgDQIObjList.get(0).getDate().equals(avgDQIObjList.get(1).getDate())){
				if(avgDQIObjList.get(0).getRun()>=avgDQIObjList.get(1).getRun()) {
					avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
					avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
				}else {
					avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
					avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
				}
			}else {
				avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
				avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
			}
		}else if(avgDQIObjList!=null && avgDQIObjList.size()==1){
			avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
		}
		return avgDQIList;
	}
	
	public List<Long> getAvgDQIAggregateofLastTwo(Long appId) {
		List<Long> avgDQIList = new ArrayList<Long>();
		List<AverageDQI> avgDQIObjList = new ArrayList<AverageDQI>();
		String sql = "SELECT avgDQI,runs,Dates FROM (SELECT avg(DQI) as avgDQI,Date as Dates,Run as runs "
				+ "FROM DashBoard_Summary where AppId = "+appId+" "
				+ " AND DQI!=0 group by Date,Run order by Dates desc ,Run desc limit 30) as b limit 2";
		avgDQIObjList = jdbcTemplate1.query(sql, new RowMapper<AverageDQI>() {
			@Override
			public AverageDQI mapRow(ResultSet rs, int rowNum) throws SQLException {
				AverageDQI avgDQI = new AverageDQI();
				avgDQI.setAvgDQI(rs.getLong("avgDQI"));
				avgDQI.setDate(rs.getString("Dates"));
				avgDQI.setRun(rs.getInt("runs"));
				return avgDQI;
			}
		});		
		if(avgDQIObjList!=null && avgDQIObjList.size()>1) {
			if(avgDQIObjList.get(0).getDate().equals(avgDQIObjList.get(1).getDate())){
				if(avgDQIObjList.get(0).getRun()>=avgDQIObjList.get(1).getRun()) {
					avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
					avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
				}else {
					avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
					avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
				}
			}else {
				avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
				avgDQIList.add(avgDQIObjList.get(1).getAvgDQI());
			}
		}else if(avgDQIObjList!=null && avgDQIObjList.size()==1){
			avgDQIList.add(avgDQIObjList.get(0).getAvgDQI());
		}
		return avgDQIList;
	}
	
}
