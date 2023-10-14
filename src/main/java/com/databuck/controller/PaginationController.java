package com.databuck.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.DateRuleMap;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.util.DateUtility;

@Controller
public class PaginationController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	@Autowired
	private JdbcTemplate jdbcTemplate2;

	@Autowired
	public Properties appDbConnectionProperties;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private Properties resultDBConnectionProperties;
	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	public IResultsDAO iResultsDAO;

	@Autowired
	public DownloadCsv downloadCsv;

	private String GLOBAL_SEARCH_TERM;
	private String COLUMN_NAME, STRINGTAB_COLUMN_NAME;
	private String DIRECTION;
	private int INITIAL;
	private int RECORD_SIZE;
	private String ID_SEARCH_TERM, DATE_SEARCH_TERM, RUN_SEARCH_TERM, DAYOFYEAR_SEARCH_TERM, MONTH_SEARCH_TERM,
			DayOfMonth_SEARCH_TERM = "";
	private String DayOfWeek_SEARCH_TERM, HourOfDay_SEARCH_TERM, FileNameValidationStatus_SEARCH_TERM,
			ColumnOrderValidationStatus_SEARCH_TERM, RC_Std_Dev_Status_SEARCH_TERM,
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = "";
	private String RecordCount_SEARCH_TERM, DuplicateDataSet_SEARCH_TERM, RC_Std_Dev_SEARCH_TERM, RC_Mean_SEARCH_TERM,
			RC_Deviation_SEARCH_TERM, RC_Mean_Moving_Avg_SEARCH_TERM = "";
	private String DGroupVal_SEARCH_TERM, DGroupCol_SEARCH_TERM = "";
	private String COL_NAME_SEARCH_TERM, UNIQUE_VALUES_SEARCH_TERM, OPERATION_SEARCH_TERM = "";
	private String UNIQUE_VALUES_COUNT_SEARCH_TERM, MISSING_VALUE_COUNT_SEARCH_TERM, NEW_VALUE_COUNT_SEARCH_TERM = "";

	String dateFilter = "";
	int runFilter = 0;

	public JSONObject getTransactionSetTableDetails(int totalRecords, HttpServletRequest request, String tableName,
			String idApp, String dateFilter, int runFilter) throws SQLException, ClassNotFoundException {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";

		if (GLOBAL_SEARCH_TERM.contains(",")) {
			GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			System.out.println("amar" + GLOBAL_SEARCH_TERM);
		}
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		String globeSearch = "";
		String idSearch = "";
		String nameSearch = "";
		String placeSearch = "";
		String citySearch = "";
		String stateSearch = "";
		String phoneSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "SELECT Id,Date,round(Run::numeric, 0) as Run1,forgot_run_enabled,dayOfYear,month,dayOfMonth,dayOfWeek,hourOfDay,RC_Std_Dev_Status,"
					+ "	RC_Mean_Moving_Avg_Status,round(RecordCount::numeric, 0) as RecordCount1,round(RC_Std_Dev::numeric, 2) as RC_Std_Dev1,round(RC_Mean::numeric, 2) as RC_Mean1,Round(RC_Deviation::numeric, 2) as RC_Deviation,RC_Mean_Moving_Avg,"
					+ "dGroupVal,DuplicateDataSet,fileNameValidationStatus,columnOrderValidationStatus FROM "
					+ tableName;
			
			globeSearch = "and (Id::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or fileNameValidationStatus like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or columnOrderValidationStatus like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Std_Dev_Status::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean_Moving_Avg_Status::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or RecordCount::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Std_Dev::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Mean::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Deviation::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean_Moving_Avg::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or DuplicateDataSet like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%"
					+ GLOBAL_SEARCH_TERM + "%')";
			
			idSearch = "Id::text like " + ID_SEARCH_TERM + "";
			nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status::text like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status::text like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			
		} else {
			sql = "SELECT Id,Date,FORMAT(Run, 0) as Run1,forgot_run_enabled,dayOfYear,month,dayOfMonth,dayOfWeek,hourOfDay,RC_Std_Dev_Status,"
					+ "	RC_Mean_Moving_Avg_Status,FORMAT(RecordCount, 0) as RecordCount1,FORMAT(RC_Std_Dev, 2) as RC_Std_Dev1,FORMAT(RC_Mean, 2) as RC_Mean1,Round(RC_Deviation, 2) as RC_Deviation,RC_Mean_Moving_Avg,"
					+ "dGroupVal,DuplicateDataSet,fileNameValidationStatus,columnOrderValidationStatus FROM "
					+ tableName;
			
			globeSearch = "(and Id like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or fileNameValidationStatus like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or columnOrderValidationStatus like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Std_Dev_Status like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean_Moving_Avg_Status like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or RecordCount like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Std_Dev like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Mean like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Deviation like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean_Moving_Avg like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or DuplicateDataSet like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%"
					+ GLOBAL_SEARCH_TERM + "%')";
			
			idSearch = "Id like " + ID_SEARCH_TERM + "";
			nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}

		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (ID_SEARCH_TERM != "") {
			searchSQL = idSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = nameSearch;
		} else if (RUN_SEARCH_TERM != "") {
			searchSQL = placeSearch;
		} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
			searchSQL = citySearch;
		} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
			searchSQL = stateSearch;
		} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			searchSQL = phoneSearch;
			// System.out.println(searchSQL);
		}

		sql += " where idApp= " + idApp + " and (" + dateFilter + ")";
		if (runFilter != 0) {
			sql += " and Run =" + runFilter;
		}

		if (searchSQL != null && !searchSQL.trim().isEmpty()) {
			sql += searchSQL;
		}
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		String[] columnNames = { "Id", "Date", "Run1", "forgot_run_enabled", "dayOfYear", "month", "dayOfMonth",
				"dayOfWeek", "hourOfDay", "RC_Std_Dev_Status", "RC_Mean_Moving_Avg_Status", "RecordCount1",
				"RC_Std_Dev1", "RC_Mean1", "RC_Deviation", "RC_Mean_Moving_Avg", "dGroupVal", "DuplicateDataSet",
				"fileNameValidationStatus", "columnOrderValidationStatus", };
		
		
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		DecimalFormat numberFormat = new DecimalFormat("#0.00");

		JSONArray dataArray = new JSONArray();

		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}

		String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where idApp="+idApp+" and (" + dateFilter + ")";
		// + " WHERE ";
		if (runFilter != 0) {
			query += " and Run = " + runFilter;
		}
		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
				|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
				|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	// -- AM -- //
	public int getTotalRecordCountOfReference(String tableName) throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count from " + tableName;

		/*
		 * if (maxRun != null && maxRun.equals("true")) {
		 * 
		 * sql = sql + " and Run = (SELECT MAX(Run) from " + tableName + ")";
		 * 
		 * }
		 */

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCount(String tableName, String idApp, String maxRun, String dateFilter, int runFilter,
			String filterSearch, boolean checkFailedRecords) throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count from " + tableName + " where (" + dateFilter + ")";

		if (idApp != null && !idApp.trim().isEmpty()) {
			sql = sql + " and idApp=" + idApp;
		}

		/*
		 * if (maxRun != null && maxRun.equals("true")) {
		 * 
		 * sql = sql + " and Run = (SELECT MAX(Run) from " + tableName + ")";
		 * 
		 * }
		 */
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		if (filterSearch != null && !filterSearch.isEmpty()) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		if (checkFailedRecords) {
			sql += " and (NumMeanStatus  = 'failed' OR  NumSDStatus = 'failed' )";
		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountPattern(String tableName, String maxRun, String idApp, String dateFilter)
			throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT COUNT(*) as count from " + tableName + " where idApp=" + idApp + " and (" + dateFilter
				+ ")";

		if (maxRun != null && maxRun.equals("true")) {

			sql = sql + " and Run = (SELECT MAX(Run) from " + tableName + " where idApp=" + idApp + " and " + dateFilter
					+ ")";

		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountDateRule(String tableName, String maxRun, String idApp) throws SQLException {

		int totalRecords = -1;
		String sql = "";

		if (idApp != null && !idApp.trim().isEmpty()) {
			sql = "SELECT COUNT(*) as count from " + tableName + " where idApp=" + idApp
					+ " and Date = (select MAX(date) from " + tableName + " where idApp = " + idApp
					+ ") and Run = (select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + "))";
		} else {
			sql = "SELECT " + "COUNT(*) as count from " + tableName + " where Run = (select MAX(Run) from " + tableName
					+ " where Date = (select MAX(date) from " + tableName + ")) and Date=(select MAX(date) from "
					+ tableName + ")";
		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountProcessData(String idApp, String dateFilter, int runFilter) {
		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count from processData where " + "idApp = " + idApp + " and ("
				+ dateFilter + ")";

		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountDrift(String tableName, String idApp, String maxRun, String dateFilterDrift,
			int runFilter) throws SQLException {

		int totalRecords = -1;
		System.out.println("\n=====> getTotalRecordCountDrift");
		System.out.println("maxRun:" + maxRun);
		String sql = "SELECT " + "COUNT(*) as count from " + tableName + " where idApp=" + idApp + " and ("
				+ dateFilterDrift + ")";

		if (maxRun != null && maxRun.equals("true")) {

			sql = "SELECT COUNT(*) as count from " + tableName + " where idApp=" + idApp
					+ " and (Date=(SELECT MAX(Date) from " + tableName + " where idApp=" + idApp + ")) and"
					+ "(Run = (SELECT MAX(Run) from " + tableName + " where idApp=" + idApp
					+ " and (Date=(SELECT MAX(Date) from " + tableName + " where idApp=" + idApp + ")))) and ("
					+ dateFilterDrift + ")";

		}
		System.out.println("sql:" + sql);
		/*
		 * if (runFilter != 0) { sql += " and Run = " + runFilter; }
		 */

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountNullSummary(String tableName, String idApp, String maxRun, String dateFilter,
			int runFilter) throws SQLException {

		int totalRecords = -1;

		/*
		 * String sql =
		 * "select count(*) from (SELECT Date,Run,(CASE when (sum(Null_Value)/sum(Record_Count)*100) > Null_Threshold then 'Failed' else 'Passed' END)  as 'Status',colName, sum(Null_Value) as Null_Value,sum(Record_Count) as Record_Count,sum(Null_Value)/sum(Record_Count)*100 as Null_Percentage,Null_Threshold from "
		 * + tableName +
		 * 
		 * " where idApp="
		 * +idApp+" and (colName in (Select Distinct(colName) AS colName from " +
		 * tableName + " where idApp="+idApp+")) and (" + dateFilter + ")";
		 */
		String sql = "SELECT " + "COUNT(*) as count from " + tableName + " where (" + dateFilter + ")";

		if (idApp != null && !idApp.trim().isEmpty()) {
			sql = sql + " and idApp=" + idApp;
		}

		/*
		 * if (maxRun != null && maxRun.equals("true")) {
		 *
		 * sql = sql + " where Run = (SELECT MAX(Run) from " + tableName + ")";
		 *
		 * }
		 */
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		/*
		 * sql += " group by colName, Date, Run, Null_Threshold order by Run) AS count";
		 */
		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;

	}

	public int getTotalRecordCountValidity(String tableName, String idApp, String maxRun, String dateFilter,
			int runFilter) throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " WHERE idApp=" + idApp + " and ("
				+ dateFilter + ") and Validity = 'True'";

		if (maxRun != null && maxRun.equals("true")) {

			sql = sql + " and Run = (SELECT MAX(Run) from " + tableName + ")";

		}

		if (runFilter != 0) {
			sql += " and Run =" + runFilter;
		}
		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	private int getTotalRecordCountColumnRulesData(int idData) {
		int totalRecords = -1;

		String sql = "SELECT COUNT(*) as count from listColRules where activeFlag='Y' and idData = " + idData;

		SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;

	}

	public int getTotalRecordCountForTable(String tableName, String idApp, String maxRun, String dateFilter,
			int runFilter, String filterSearch) throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count from " + tableName + " where idApp=" + idApp + " and (" + dateFilter
				+ ")";

		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		if (filterSearch != null && !filterSearch.isEmpty()) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	public int getTotalRecordCountDGroup(String tableName, String idApp, String maxRun, String dateFilter,
			int runFilter, String filterSearch) throws SQLException {

		int totalRecords = -1;

		String sql = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " WHERE idApp=" + idApp + " and ("
				+ dateFilter + ") and Validity = 'False'";

		if (maxRun != null && maxRun.equals("true")) {

			sql = sql + " and Run = (SELECT MAX(Run) from " + tableName + ")";

		}
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		if (filterSearch != null && !filterSearch.isEmpty()) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

		if (resultSet.next()) {
			totalRecords = resultSet.getInt("count");
		}

		return totalRecords;
	}

	@RequestMapping(value = "/googleChart", method = RequestMethod.POST)
	public void googleChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : googleChart : ...");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			System.out.println("PaginationController : googleChart : idApp ::" + idApp);
			List<List<Object>> list = new ArrayList<List<Object>>();
			jsonObject = getLineChartDetails(tableName, idApp);
			// JSONObject jsonResult = new JSONObject(jsonArray);
			System.out.println("PaginationController : googleChart : json result :: " + jsonObject);
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param tableName
	 *            : this table will come from View side
	 * @return JSON Object which contains google chart's header and chart array
	 * @throws SQLException
	 *             : to handle query related errors
	 */
	public JSONObject getLineChartDetails(String tableName, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : getLineChartDetails : tableName :: " + tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummary(tableName, idApp);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummary(tableName, idApp);

			String str_dGroupVal_SqlQuery = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp;
			System.out.println(
					"PaginationController : getLineChartDetails : str_dGroupVal_SqlQuery :: " + str_dGroupVal_SqlQuery);
			SqlRowSet sqlRowSet_str_dGroupVal_SqlQuery = jdbcTemplate1.queryForRowSet(str_dGroupVal_SqlQuery);
			List<String> listTable_dGroupVal = new ArrayList<String>();
			while (sqlRowSet_str_dGroupVal_SqlQuery.next()) {
				listTable_dGroupVal.add(sqlRowSet_str_dGroupVal_SqlQuery.getString("dGroupVal"));
			}
			Set<String> set = new HashSet<>(listTable_dGroupVal);
			listTable_dGroupVal.clear();
			listTable_dGroupVal.addAll(set);
			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);
			System.out.println(
					"PaginationController : getLineChartDetails : jsonArrayDGroupVal :: " + jsonArrayDGroupVal);
			for (Date localdate : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, idApp, localdate, integer)) {
						List<Object> listOfChartElements = new ArrayList<Object>();

						if (integer < 10) {
							listOfChartElements.add(localdate + "(0" + integer + ")");
						} else {
							listOfChartElements.add(localdate + "(" + integer + ")");
						}
						for (String localstring : listTable_dGroupVal) {

							String strSqlQueryGetChartList = "SELECT RecordCount FROM " + tableName + " WHERE date = '"
									+ localdate + "' AND dGroupVal = '" + localstring + "' AND RUN = " + integer
									+ " and idApp=" + idApp;
							SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1
									.queryForRowSet(strSqlQueryGetChartList);
							boolean flag = false;
							while (sqlRowSet_strSqlQueryGetChartList.next()) {
								listOfChartElements.add(sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
								flag = true;
							}

							if (!flag) {
								listOfChartElements.add(null);
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
			System.err.println("PaginationController : getLineChartDetails : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	/*
	 * @RequestMapping(value="/paginationreq") public ModelAndView
	 * paginationreq(HttpServletRequest request,HttpServletResponse response){
	 * ModelAndView model=new ModelAndView("viewDatatable"); return model; }
	 */

	@RequestMapping(value = "/pagination")
	public void pagination(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("pagination");
		String[] columnNames = { "id", "Date", "Run", "forgot_run_enabled", "dayOfYear", "month", "dayOfMonth",
				"dayOfWeek", "hourOfDay", "fileNameValidationStatus", "columnOrderValidationStatus",
				"RC_Std_Dev_Status", "RC_Mean_Moving_Avg_Status", "RecordCount", "DuplicateDataSet", "RC_Std_Dev",
				"RC_Mean", "RC_Deviation", "RC_Mean_Moving_Avg", "DGroupVal" };

		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");

		JSONObject jsonResult = new JSONObject();
		int listDisplayAmount = 10;
		int start = 0;
		int column = 0;
		boolean booleanCheckFailedRecords = false;
		String dir = "asc";
		String pageNo = request.getParameter("iDisplayStart");
		String pageSize = request.getParameter("iDisplayLength");
		String colIndex = request.getParameter("iSortCol_0");
		String sortDirection = request.getParameter("sSortDir_0");
		String tableName = request.getParameter("tableName");
		String maxRun = request.getParameter("maxRun");
		String searchValue = request.getParameter("searchValue");
		String idApp = request.getParameter("idApp");
		System.out.println("tableName=" + tableName);
		if (pageNo != null) {
			start = Integer.parseInt(pageNo);
			if (start < 0) {
				start = 0;
			}
		}
		if (pageSize != null) {
			listDisplayAmount = Integer.parseInt(pageSize);
			if (listDisplayAmount < 10 || listDisplayAmount > 50) {
				listDisplayAmount = 10;
			}
		}
		if (colIndex != null) {
			column = Integer.parseInt(colIndex);
			if (column < 0 || column > 20)
				column = 0;
		}
		if (sortDirection != null) {
			if (!sortDirection.equals("asc"))
				dir = "desc";
		}

		String colName = columnNames[column];
		int totalRecords = -1;
		try {
			totalRecords = getTotalRecordCountForTable(tableName, idApp, maxRun, dateFilter, runFilter, searchValue);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		RECORD_SIZE = listDisplayAmount;
		GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
		ID_SEARCH_TERM = request.getParameter("sSearch_0");
		DATE_SEARCH_TERM = request.getParameter("sSearch_1");
		RUN_SEARCH_TERM = request.getParameter("sSearch_2");
		RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
		RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
		RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
		COLUMN_NAME = colName;
		DIRECTION = dir;
		INITIAL = start;

		if (GLOBAL_SEARCH_TERM.contains(",")) {
			GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			System.out.println("amar" + GLOBAL_SEARCH_TERM);
		}

		try {
			jsonResult = getTransactionSetTableDetails(totalRecords, request, tableName, idApp, dateFilter, runFilter);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(jsonResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ModelAndView model = new ModelAndView("viewDatatable");
		// return model;

	}

	/*
	 * @RequestMapping(value="/paginationreq") public ModelAndView
	 * paginationreq(HttpServletRequest request,HttpServletResponse response){
	 * ModelAndView model=new ModelAndView("viewDatatable"); return model; }
	 */

	@RequestMapping(value = "/DGroupTable")
	public void DGroupTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("DGroupTable");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);

		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");

		String[] columnNames = { "id", "Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek", "hourOfDay",
				"RecordCount", "RC_Std_Dev", "RC_Mean", "dGroupDeviation", "dgDqi", "dGroupVal", "dgroupCol",
				"dGroupRcStatus", "Action", "UserName", "Time" };

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);
			String idApp = request.getParameter("idApp");
			System.out.println("idApp=" + idApp);
			String searchValue = request.getParameter("searchValue");
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountDGroup(tableName, idApp, maxRun, dateFilter, runFilter, searchValue);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
				System.out.println("amar" + GLOBAL_SEARCH_TERM);
			}

			try {
				jsonResult = getDGroupTableDetails(totalRecords, request, tableName, idApp, dateFilter, runFilter,
						searchValue);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("PaginationController : /DGroupVal : jsonResult :: " + jsonResult);
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	public JSONObject getDGroupTableDetails(int totalRecords, HttpServletRequest request, String tableName,
			String idApp, String dateFilter, int runFilter, String filterSearch)
			throws SQLException, ClassNotFoundException {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";
		String[] columnNames = { "Id", "Date", "Run1", "dayOfYear", "month", "dayOfMonth", "dayOfWeek", "hourOfDay",
				"RecordCount1", "RC_Std_Dev1", "RC_Mean1", "dGroupDeviation1", "dgDqi", "dGroupVal", "dgroupCol",
				"dGroupRcStatus", "Action", "UserName", "Time" };
		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
		Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		String idSearch = "";
		String nameSearch = "";
		String placeSearch = "";
		String citySearch = "";
		String stateSearch = "";
		String phoneSearch = "";
		String globeSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "SELECT Id,Date,round(Run::numeric, 0) as Run1,dayOfYear,month,dayOfMonth,dayOfWeek, hourOfDay,"
					+ "round(RecordCount::numeric, 0) as RecordCount1," + "(CASE WHEN (dGroupDeviation <= " + threshold
					+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 " + "ELSE (100 - ( (ABS(dGroupDeviation - "
					+ threshold + ") *100) / ( 6 - " + threshold + " ) )) END) END) AS dgDqi, " + "dGroupVal,dgroupCol,"
					+ "	round(RC_Std_Dev::numeric, 2) as RC_Std_Dev1,round(RC_Mean::numeric, 2) as RC_Mean1,"
					+ "round(dGroupDeviation::numeric, 2) as dGroupDeviation1, dGroupRcStatus,Action,UserName,Time FROM "
					+ tableName + " WHERE idApp=" + idApp + " and (" + dateFilter + ") and Validity = 'False'";
			
			globeSearch = " AND (Id::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RecordCount::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupVal like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dgroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Std_Dev::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean::text like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupDeviation::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupRcStatus like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id::text like " + ID_SEARCH_TERM + "";
			nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			
		} else {
			sql = "SELECT Id,Date,FORMAT(Run, 0) as Run1,dayOfYear,month,dayOfMonth,dayOfWeek, hourOfDay,"
					+ "FORMAT(RecordCount, 0) as RecordCount1," + "(CASE WHEN (dGroupDeviation <= " + threshold
					+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 " + "ELSE (100 - ( (ABS(dGroupDeviation - "
					+ threshold + ") *100) / ( 6 - " + threshold + " ) )) END) END) AS dgDqi, " + "dGroupVal,dgroupCol,"
					+ "	FORMAT(RC_Std_Dev, 2) as RC_Std_Dev1,FORMAT(RC_Mean, 2) as RC_Mean1,"
					+ "FORMAT(dGroupDeviation, 2) as dGroupDeviation1, dGroupRcStatus,Action,UserName,Time FROM "
					+ tableName + " WHERE idApp=" + idApp + " and (" + dateFilter + ") and Validity = 'False'";
			
			globeSearch = " AND (Id like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RecordCount like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupVal like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dgroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Std_Dev like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupDeviation like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupRcStatus like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id like " + ID_SEARCH_TERM + "";
			nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}

		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (ID_SEARCH_TERM != "") {
			searchSQL = idSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = nameSearch;
		} else if (RUN_SEARCH_TERM != "") {
			searchSQL = placeSearch;
		} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
			searchSQL = citySearch;
		} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
			searchSQL = stateSearch;
		} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			searchSQL = phoneSearch;
		}

		sql += searchSQL;
		// sql += " and (" + dateFilter + ")";
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		System.out.println("PaginationController : getDGroupTableDetails : before sql" + sql);
		if (filterSearch != null && !filterSearch.isEmpty()) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		System.out.println("PaginationController : getDGroupTableDetails : after sql" + sql);
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);

		DecimalFormat numberFormat = new DecimalFormat("#0.00");

		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}
		System.out.println("JSONArray=" + dataArray);

		String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where idApp=" + idApp + " and ("
				+ dateFilter + ") and Validity = 'False'";
		// + " WHERE ";
		if (filterSearch != null && !filterSearch.isEmpty()) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}
		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
				|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
				|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/TranSummTable")
	public void TranSummTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("TranSummTable");

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
			String tableName = request.getParameter("tableName");
			String runNum = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			System.out.println("idApp:" + idApp);
			System.out.println("tableName=" + tableName);
			System.out.println("runNum=" + runNum);

			String[] columnNames = null;
			// Following sql will fetch records along with dGroup columns
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
				columnNames = new String[] {"Date","Run","Type","dGroupCol","dGroupVal","duplicateCheckFields","Duplicate","TotalCount","Percentage","Threshold","Status"};
			} else {
				columnNames = new String[] {"Date","Run","Duplicate","Type","TotalCount","Percentage","Threshold","Status"};
			}
			
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String searchValue = request.getParameter("searchValue");

			String maxRun = request.getParameter("maxRun");

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			/*
			 * if (GLOBAL_SEARCH_TERM.contains(",")) { GLOBAL_SEARCH_TERM =
			 * GLOBAL_SEARCH_TERM.replaceAll(",", ""); }
			 */

			try {
				jsonResult = getTranDetailSummDetails(totalRecords, request, tableName, idApp, columnNames, maxRun,
						dateFilter, runFilter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				// jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}
	}

	private JSONObject getTranDetailSummDetails(int totalRecords, HttpServletRequest request, String tableName,
			String idApp, String[] columnNames, String maxRun, String dateFilter, int runFilter) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		try {
			int totalAfterSearch = totalRecords;

			String searchSQL = "";

			String sql = "SELECT Date,Run,Duplicate,Type,TotalCount,Percentage,Threshold,Status FROM " + tableName
					+ " where idApp=" + idApp + " and (" + dateFilter + ")";

			// Following sql will fetch summary along with dGroup columns
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
				sql = "SELECT Date,Run,Type,dGroupCol,dGroupVal,duplicateCheckFields,Duplicate,TotalCount,Percentage,Threshold,Status FROM "
						+ tableName + " where idApp=" + idApp + " and (" + dateFilter + ")";
			}
			/*
			 * String matchingPage = request.getParameter("matchingPage"); if (matchingPage
			 * != null && matchingPage.equals("true")) { sql +=
			 * " and (Date=(select max(date) from " + tableName + " ) " +
			 * "And Run=(select max(run) from " + tableName + " where date=" +
			 * "(select max(date) from " + tableName + "))) "; }
			 */

			String globeSearch = " ";
			/*
			 * if (matchingPage != null && matchingPage.equals("true")) { globeSearch =
			 * " And "; } else { globeSearch = " where "; }
			 */
			for (String col : columnNames) {
				
				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					globeSearch = globeSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
				else 
					globeSearch = globeSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			}
			globeSearch = globeSearch.substring(0, globeSearch.length() - 3);

			String idSearch = "Id like " + ID_SEARCH_TERM + "";
			String nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			String placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			String citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			// String stateSearch = " RC_Mean_Moving_Avg_Status like '%" +
			// RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			// String phoneSearch="";
			// String phoneSearch = " RC_Mean_Moving_Avg like '%" +
			// RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			// System.out.println(phoneSearch);
			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = " and (" + globeSearch + ")";
			} else if (ID_SEARCH_TERM != "") {
				// searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				// searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				// searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				// searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				// searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				// searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}

			sql += searchSQL;

			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println(sql);
			// for searching

			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			// SqlRowSetMetaData metaData = rs.getMetaData();
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			System.out.println("JSONArray=" + dataArray);

			String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where (" + dateFilter + ")";

			if (maxRun != null && maxRun.equals("true")) {

				query = query + " and (Run = (SELECT MAX(Run) from " + tableName + "))";

			}
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			// + " WHERE ";

			// for pagination
			if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
					|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
					|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				query += searchSQL;

				System.out.println(query);
				SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

				if (results.next()) {
					totalAfterSearch = results.getInt("count");
				}

			}
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {
			try {
				// System.out.println("exception");
				result.put("iTotalRecords", 0);
				result.put("iTotalDisplayRecords", 0);
				// array.put(false);
				result.put("aaData", array);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return result;
		}

		return result;
	}

	@RequestMapping(value = "/TranDetailAllTable")
	public void TranDetailAllTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("TranDetailAllTable");

		String tableName = request.getParameter("tableName");
		String runNum = request.getParameter("maxRun");
		String idApp = request.getParameter("idApp");
		System.out.println("idApp=" + idApp);
		System.out.println("tableName=" + tableName);
		System.out.println("runNum=" + runNum);

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			System.out.println("Io Exception message:" + e2.getMessage());
		}
		try {
			// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
			String[] columnNames = null;
			if(tableName.trim().equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_All")) {
				columnNames = new String[] {"Date","Run","duplicateCheckFields","duplicateCheckValues","dupcount","dGroupCol","dGroupVal"};
			} else {
				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet("select * from " + tableName);
				columnNames = queryForRowSet.getMetaData().getColumnNames();
			}

			columnNames = ArrayUtils.removeElement(columnNames, "Id");
			columnNames = ArrayUtils.removeElement(columnNames, "idApp");
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String searchValue = request.getParameter("searchValue");
			String maxRun = request.getParameter("maxRun");

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			/*
			 * if (GLOBAL_SEARCH_TERM.contains(",")) { GLOBAL_SEARCH_TERM =
			 * GLOBAL_SEARCH_TERM.replaceAll(",", ""); }
			 */

			try {
				jsonResult = getTranDetailAllDetails(totalRecords, request, tableName, idApp, columnNames, maxRun,
						dateFilter, runFilter);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error getting table:" + tableName + ", message:" + e.getMessage());
			}
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				// e1.printStackTrace();
				System.out.println("Json Exception message:" + e.getMessage());
			}

			out.print(jsonResult);
			// e.printStackTrace();
			System.out.println("Error getting table:" + tableName + ", message:" + e.getMessage());
		}
	}

	private JSONObject getTranDetailAllDetails(int totalRecords, HttpServletRequest request, String tableName,
			String idApp, String[] columnNames, String maxRun, String dateFilter, int runFilter) {
		JSONObject result = new JSONObject();
		JSONArray dmResultarray = new JSONArray();
		JSONArray dataArray = new JSONArray();
		try {
			int totalAfterSearch = totalRecords;

			String searchSQL = "";

			String columns_Str = "";
			int pos = 0;
			for (String col : columnNames) {
				if (col != null && !col.trim().isEmpty()) {
					columns_Str = (pos == 0) ? col : columns_Str + "," + col;
					++pos;
				}
			}
			String sql = "SELECT " + columns_Str + " FROM " + tableName + " where ";
			if (idApp != null && !idApp.trim().isEmpty()) {
				sql = sql + " idApp=" + idApp + " and ";
			}
			sql = sql + " (" + dateFilter + ")";

			/*
			 * String matchingPage = request.getParameter("matchingPage"); if (matchingPage
			 * != null && matchingPage.equals("true")) { sql +=
			 * " and (Date=(select max(date) from " + tableName + " ) " +
			 * "And Run=(select max(run) from " + tableName + " where date=" +
			 * "(select max(date) from " + tableName + "))) "; }
			 */

			// String sql = "SELECT * FROM " + tableName;
			String globeSearch = " ";
			/*
			 * if (matchingPage != null && matchingPage.equals("true")) { globeSearch =
			 * " And "; } else { globeSearch = " where "; }
			 */
			for (String col : columnNames) {
				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					globeSearch = globeSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
				else
					globeSearch = globeSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			}
			globeSearch = globeSearch.substring(0, globeSearch.length() - 3);

			String idSearch = "Id like " + ID_SEARCH_TERM + "";
			String nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			String placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			String citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			// String stateSearch = " RC_Mean_Moving_Avg_Status like '%" +
			// RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			// String phoneSearch="";
			// String phoneSearch = " RC_Mean_Moving_Avg like '%" +
			// RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			// System.out.println(phoneSearch);
			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = " AND (" + globeSearch + ")";
			} else if (ID_SEARCH_TERM != "") {
				// searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				// searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				// searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				// searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				// searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				// searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}

			sql += searchSQL;

			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			}else {
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;
			}
			System.out.println(sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			// SqlRowSetMetaData metaData = rs.getMetaData();
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();
				JSONArray dmRowArray = new JSONArray();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));

						// Mamta 20/7/2021
						double data = Double.parseDouble(columnValue);
						int value = (int) data;
						columnValue = Integer.toString(value);
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					dmRowArray.put(columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);
				dmResultarray.put(dmRowArray);
			}

			String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where ";
			if (idApp != null && !idApp.trim().isEmpty()) {
				query = query + " idApp=" + idApp + " and ";
			}
			query = query + " (" + dateFilter + ")";

			/*
			 * if (maxRun != null && maxRun.equals("true")) {
			 * 
			 * query = query + " and (Run = (SELECT MAX(Run) from " + tableName + "))";
			 * 
			 * }
			 */
			if (runFilter != 0) {
				query += " and Run = " + runFilter;
			}
			// + " WHERE ";

			// for pagination
			if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
					|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
					|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				query += searchSQL;

				System.out.println(query);
				SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

				if (results.next()) {
					totalAfterSearch = results.getInt("count");
				}

			}
			String matchingPage = request.getParameter("matchingPage");

			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			if (matchingPage != null && matchingPage.trim().equalsIgnoreCase("true")) {
				result.put("aaData", dmResultarray);
			} else {
				result.put("aaData", dataArray);
			}
		} catch (Exception e) {
			try {
				// System.out.println("exception");
				result.put("iTotalRecords", 0);
				result.put("iTotalDisplayRecords", 0);
				// array.put(false);
				result.put("aaData", new JSONArray());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return result;
		}

		return result;
	}

	@RequestMapping(value = "/TranDetailIdentityTable")
	public void TranDetailIdentityTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("TranDetailIdentityTable");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String tableName = request.getParameter("tableName");
		System.out.println("tableName=" + tableName);
		String idApp = request.getParameter("idApp");
		System.out.println("idApp=" + idApp);
		PrintWriter out = null;
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		JSONObject jsonResult = new JSONObject();
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			// e2.printStackTrace();
			System.out.println("IO Exception message:" + e2.getMessage());
		}
		try {
			boolean booleanCheckFailedRecords = false;
			String[] columnNames = null;
			
			if (tableName.equalsIgnoreCase("DATA_QUALITY_History_Anomaly")) {
				String[] anotherArray = { "Date", "Run", "ColName", "ColVal", "mean",
						"stddev", "dGroupVal","dGroupCol", "status", "RA_Dqi"};
				columnNames = anotherArray;

			}

			else if (tableName.equalsIgnoreCase("DATA_QUALITY_timeliness_check")) {
				String[] anotherArray = { "Date", "Run", "SDate", "EDate", "TimelinessKey",
					"No_Of_Days", "Status" };
				columnNames = anotherArray;

			}
			
			// Mamta 6-July-2022
			else if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_Identity")) {
				String[] anotherArray = { "Date", "Run", "duplicateCheckFields", "duplicateCheckValues", "dupcount",
						"dGroupCol", "dGroupVal" };
				columnNames = anotherArray;

			}

			else if (tableName.equalsIgnoreCase("DATA_QUALITY_Rules")) {
				String[] anotherArray = { "Id","Date", "Run", "ruleName", "totalRecords", "totalFailed",
						"rulePercentage", "ruleThreshold", "status"};
				columnNames = anotherArray;
			}
			
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;

			totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
					booleanCheckFailedRecords);

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}

			try {
				jsonResult = getTranDetailAllDetails(totalRecords, request, tableName, idApp, columnNames, maxRun,
						dateFilter, runFilter);
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error getting table:" + tableName + ", message:" + e.getMessage());
			}
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			// System.out.println("Exception Came");
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
				// out.print(jsonResult);
				// System.out.println("return empty json");
			} catch (JSONException e1) {
				// e1.printStackTrace();
				System.out.println("Json exception message:" + e.getMessage());
			}

			out.print(jsonResult);
			System.out.println("Error getting table:" + tableName + ", message:" + e.getMessage());
		}

	}

	@RequestMapping(value = "/ColSummTableForNullTab")
	public void ColSummTableForNullTab(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("ColSummTableForNullTab");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "Status", "colName", "Null_Value", "Record_Count", "Null_Percentage",
				"Null_Threshold", "DGroupVal", "DGroupCol" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String idApp = request.getParameter("idApp");
			// Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("tableName=" + tableName);
			// System.out.println("idApp=" + idApp);
			// System.out.println("idData=" + idData);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForNullTabDetails(totalRecords, request, tableName, idApp, dateFilter,
					runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForNullTabDetails(int totalRecords, HttpServletRequest request, String tableName,
			String idApp, String dateFilter, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String idSearch = "";
			String nameSearch = "";
			String placeSearch = "";
			String citySearch = "";
			String stateSearch = "";
			String phoneSearch = "";
			String globeSearch = "";
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				sql = "SELECT Date,ROUND(Run, 0) as Run1,Status,colName,ROUND(Null_Value, 0) as Null_Value1,"
						+ "ROUND(Record_Count, 0) as Record_Count1,Null_Percentage,Null_Threshold,DGroupVal,DGroupCol FROM "
						+ tableName + " WHERE idApp=" + idApp + " and Null_Threshold IS NOT NULL";
				
				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Null_Value::text like  '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Record_Count::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Null_Percentage::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Null_Threshold::text like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or DGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'" + "or DGroupCol like '%" + GLOBAL_SEARCH_TERM
						+ "%')";

				idSearch = "Id::text like " + ID_SEARCH_TERM + "";
				nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
				phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
				
			} else {
				// for (ListDataDefinition ldd : displayNamesForNullTest) {

				sql = "SELECT Date,FORMAT(Run, 0) as Run1,Status,colName,FORMAT(Null_Value, 0) as Null_Value1,"
						+ "FORMAT(Record_Count, 0) as Record_Count1,Null_Percentage,Null_Threshold,DGroupVal,DGroupCol FROM "
						+ tableName + " WHERE idApp=" + idApp + " and Null_Threshold IS NOT NULL";
				
				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Null_Value like  '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Record_Count like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Null_Percentage like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Null_Threshold like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or DGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'" + "or DGroupCol like '%" + GLOBAL_SEARCH_TERM
						+ "%')";

				idSearch = "Id like " + ID_SEARCH_TERM + "";
				nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
				phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			}

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (ID_SEARCH_TERM != "") {
				searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}

			sql += searchSQL;
			sql += " and (" + dateFilter + ")";
			if (runFilter != 0) {
				sql += " and Run =" + runFilter;
			}
			// sql += " WHERE colName='"+ldd.getDisplayName()+"'";
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			String[] columnNames = { "Date", "Run1", "Status", "colName", "Null_Value1", "Record_Count1",
					"Null_Percentage", "Null_Threshold", "DGroupVal", "DGroupCol" };

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}

			String query = "SELECT COUNT(*) as count FROM " + tableName + " WHERE idApp=" + idApp
					+ " and Null_Threshold IS NOT NULL and (" + dateFilter + ")";

			// + " WHERE ";

			// for pagination
			/*
			 * if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM !=
			 * "" || RUN_SEARCH_TERM != "" || RC_Std_Dev_Status_SEARCH_TERM != "" ||
			 * RC_Mean_Moving_Avg_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_SEARCH_TERM
			 * != "") {
			 */
			if (runFilter != 0) {
				query += " and Run =" + runFilter;
			}
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
			/* } */
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);
		/*
		 * while (rs.next()) { JSONArray ja = new JSONArray();
		 * ja.put(rs.getString("Date")); ja.put(rs.getString("Run"));
		 * ja.put(rs.getString("Status")); ja.put(rs.getString("colName"));
		 * ja.put(rs.getString("Null_Value")); ja.put(rs.getString("Record_Count"));
		 * ja.put(rs.getString("Null_Percentage"));
		 * ja.put(rs.getString("Null_Threshold")); ja.put(rs.getString("DGroupVal"));
		 * ja.put(rs.getString("DGroupCol")); array.put(ja); }
		 */
		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColNullSummTableForNullTab")
	public void ColNullSummTableForNullTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForNullTab");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "Status", "colName", "Null_Value", "Record_Count", "Null_Percentage",
				"Null_Threshold", "Historic_Null_Mean", "Historic_Null_stddev", "Historic_Null_Status" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String searchValue = request.getParameter("searchValue");
			String maxRun = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			// Long idApp = Long.parseLong(request.getParameter("idApp"));
			// Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + idApp);
			// System.out.println("idData=" + idData);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountNullSummary(tableName, idApp, maxRun, dateFilter, runFilter);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getNullColSummTableForNullTabDetails(totalRecords, idApp, request, tableName, dateFilter,
					runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			// jsonResult.put("data",jsonResult);
			// response.getWriter().flush();
			out.print(jsonResult);
		} catch (Exception e) {
			// JSONArray array = new JSONArray();
			// try {
			// jsonResult.put("iTotalRecords", 0);
			// jsonResult.put("iTotalDisplayRecords", 0);
			// jsonResult.put("aaData", array);
			// } catch (JSONException e1) {
			// e1.printStackTrace();
			// }

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private HashMap<String, String> getIsRejectOptionToBeShown(String sNullSummaryTable, String sAppId) {
		String sLogicalFlagsQry = "";
		HashMap<String, String> oRetValue = new HashMap<String, String>();
		int nThresholdLinkedCount = 0;

		SqlRowSet oSqlRowSet = null;

		try {
			DateUtility.DebugLog("getIsRejectOptionToBeShown 01",
					String.format("sNullSummaryTable = '%1$s'", sNullSummaryTable));
			// Query compatibility changes for both POSTGRES and MYSQL

			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				/*
				 * Get latest run information for application summary results table per each
				 * column
				 */
				sLogicalFlagsQry = sLogicalFlagsQry + "select core_qry.colName, max(core_qry.DateRun) as MaxDataRun\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "from\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "(\n";
				sLogicalFlagsQry = sLogicalFlagsQry
						+ "	select colName,cast(concat(to_char(Date,'YYYYMMDD'), ROUND(Run,0)) as bigint) as DateRun\n";
				sLogicalFlagsQry = sLogicalFlagsQry + String.format("from %1$s\n", sNullSummaryTable);
				sLogicalFlagsQry = sLogicalFlagsQry + " where idApp=" + sAppId + ") core_qry\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "group by core_qry.colName;";
			} else {
				/*
				 * Get latest run information for application summary results table per each
				 * column
				 */
				sLogicalFlagsQry = sLogicalFlagsQry + "select core_qry.colName, max(core_qry.DateRun) as MaxDataRun\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "from\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "(\n";
				sLogicalFlagsQry = sLogicalFlagsQry
						+ "	select colName, cast(concat(date_format(Date, '%Y%m%d'), format(Run,0)) as unsigned) as DateRun\n";
				sLogicalFlagsQry = sLogicalFlagsQry + String.format("from %1$s\n", sNullSummaryTable);
				sLogicalFlagsQry = sLogicalFlagsQry + " where idApp=" + sAppId + ") core_qry\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "group by core_qry.colName;";
			}

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sLogicalFlagsQry);
			while (oSqlRowSet.next()) {
				oRetValue.put(String.format("%1$s_MaxDateAndRun", oSqlRowSet.getString("colName")),
						oSqlRowSet.getString("MaxDataRun"));
			}

			/*
			 * Get is there threshold linked to application and does user opted for reject
			 * value from dashboard? if yes get row id of threshold record
			 */
			sLogicalFlagsQry = "";
			sLogicalFlagsQry = sLogicalFlagsQry + "select count(*) as Count\n";
			sLogicalFlagsQry = sLogicalFlagsQry
					+ "from listApplications a, app_option_list b, app_option_list_elements c\n";
			sLogicalFlagsQry = sLogicalFlagsQry + "where c.elements2app_list = b.row_id\n";
			sLogicalFlagsQry = sLogicalFlagsQry + "and   a.thresholdsApplyOption = c.row_id\n";
			sLogicalFlagsQry = sLogicalFlagsQry + "and   b.list_reference = 'GLOBAL_THRESHOLDS_OPTION'\n";
			sLogicalFlagsQry = sLogicalFlagsQry + "and   c.element_reference = 'USE_USER_REJECTED_VALUE'\n";
			sLogicalFlagsQry = sLogicalFlagsQry + "and   b.active > 0\n";
			sLogicalFlagsQry = sLogicalFlagsQry + String.format("and   a.idApp = %1$s;", sAppId);

			oSqlRowSet = jdbcTemplate.queryForRowSet(sLogicalFlagsQry);
			while (oSqlRowSet.next()) {
				nThresholdLinkedCount = oSqlRowSet.getInt("Count");
			}

			/*
			 * If validation application has threshold option = apply from dashboard
			 * rejected value? If yes per column get row id of each threshold selected vs
			 * data template
			 */
			if (nThresholdLinkedCount > 0) {
				sLogicalFlagsQry = "";
				sLogicalFlagsQry = sLogicalFlagsQry
						+ "select c.displayName as colName,  a.idGlobalThresholdSelected as ThresholdRowId\n";
				sLogicalFlagsQry = sLogicalFlagsQry
						+ "from  listGlobalThresholdsSelected a, listApplications b, listDataDefinition c\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "where a.idData = b.idData\n";
				sLogicalFlagsQry = sLogicalFlagsQry + "and   a.idColumn = c.idColumn\n";
				sLogicalFlagsQry = sLogicalFlagsQry + String.format("and   b.idApp = %1$s\n", sAppId);

				oSqlRowSet = jdbcTemplate.queryForRowSet(sLogicalFlagsQry);
				while (oSqlRowSet.next()) {
					oRetValue.put(String.format("%1$s_ThresholdRowId", oSqlRowSet.getString("colName")),
							oSqlRowSet.getString("ThresholdRowId"));
				}
			}
			DateUtility.DebugLog("getIsRejectOptionToBeShown 02", String.format("Return Map =\n%1$s\n", oRetValue));
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		return oRetValue;
	}

	private JSONObject getNullColSummTableForNullTabDetails(int totalRecords, String idApp, HttpServletRequest request,
			String tableName, String dateFilter, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		HashMap<String, String> oIsRejectOptionToBeShown = new HashMap<String, String>();
		String sRowDateAndRun = "";
		String sThresholdRowId = "-1";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		try {

			/*
			 * Get logical flags to determine reject from dashboard to be rendered on data
			 * table column or not
			 */
			oIsRejectOptionToBeShown = getIsRejectOptionToBeShown(tableName, idApp);
			DateUtility.DebugLog("getNullColSummTableForNullTabDetails 01",
					String.format("%1$s", oIsRejectOptionToBeShown));

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String globeSearch = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT Date,round(Run, 0) as Run1, Status,colName, round(Null_Value, 0) as Null_Value1,"
						+ "Record_Count,Null_Percentage,Null_Threshold, Historic_Null_Mean, Historic_Null_stddev, Historic_Null_Status,"
						+ "cast(concat(to_char(Date, 'YYYYMMDD'),round(Run,0)) as int) as DateRun " + " from "
						+ tableName + " WHERE idApp=" + idApp + " and  Null_Threshold IS NOT NULL";

				// + " WHERE ";
				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status::text like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or colName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Null_Value::text like  '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Record_Count::text like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Null_Percentage::text like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Null_Threshold::text like '%" + GLOBAL_SEARCH_TERM + "%')";
			} else {
				sql = "SELECT Date,FORMAT(Run, 0) as Run1, Status,colName, FORMAT(Null_Value, 0) as Null_Value1,"
						+ "Record_Count,Null_Percentage,Null_Threshold, Historic_Null_Mean, Historic_Null_stddev, Historic_Null_Status,"
						+ "cast(concat(date_format(Date, '%Y%m%d'),format(Run,0)) as unsigned) as DateRun " + " from "
						+ tableName + " WHERE idApp=" + idApp + " and  Null_Threshold IS NOT NULL";

				// + " WHERE ";

				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Runlike '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or colName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Null_Value like  '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Record_Count like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Null_Percentage like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Null_Threshold like '%" + GLOBAL_SEARCH_TERM + "%')";

			}

			// String idSearch = "Id like " + ID_SEARCH_TERM + "";
			// String nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			// String placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			// String citySearch = " RC_Std_Dev_Status like '%" +
			// RC_Std_Dev_Status_SEARCH_TERM + "%'";
			// String stateSearch = " RC_Mean_Moving_Avg_Status like '%" +
			// RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			// String phoneSearch="";
			// String phoneSearch = " RC_Mean_Moving_Avg like '%" +
			// RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (ID_SEARCH_TERM != "") {
				// searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				// searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				// searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				// searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				// searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				// searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}

			sql += searchSQL;
			sql += " and (" + dateFilter + ")";
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			// sql += searchSQL;
			// sql += " WHERE colName='"+ldd.getDisplayName()+"'";
			// sql += "group by Date, colName, Run, Null_Threshold ";
			if (COLUMN_NAME.equals("Date")) {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION + ", Run " + DIRECTION;
			} else {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			String[] columnNames = { "Date", "Run1", "Status", "colName", "Null_Value1", "Record_Count",
					"Null_Percentage", "Null_Threshold", "Historic_Null_Mean", "Historic_Null_stddev",
					"Historic_Null_Status", "DateRun" };

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					System.out.println("columnname:" + columnName + " columnvalue:" + columnValue);
					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}

			/*
			 * String query =
			 * "select count(*) from (SELECT Date,Run,(CASE when (sum(Null_Value)/sum(Record_Count)*100) > Null_Threshold then 'Failed' else 'Passed' END)  as 'Status',colName, sum(Null_Value) as Null_Value,sum(Record_Count) as Record_Count,sum(Null_Value)/sum(Record_Count)*100 as Null_Percentage,Null_Threshold from "
			 * + tableName +
			 *
			 * " where colName in (Select Distinct(colName) AS colName from " + tableName +
			 * ") group by colName, Date, Run, Null_Threshold order by Run) AS count" ;
			 */
			/*
			 * if (GLOBAL_SEARCH_TERM.equals("")) { query = "select count(*) from " +
			 * tableName + " where idApp="+idApp+" and Null_Threshold IS NOT NULL and (" +
			 * dateFilter + ")";
			 * 
			 * if (runFilter != 0) { query += " and Run = " + runFilter; } //query +=
			 * " group by colName, Date, Run, Null_Threshold order by Run) AS count"; } else
			 * { query = "select count(*) from ( " + sql + " ) as count"; }
			 */
			String query = "SELECT COUNT(*) as count FROM " + tableName + " WHERE idApp=" + idApp
					+ " and Null_Threshold IS NOT NULL and (" + dateFilter + ")";

			// String query = "select count(*) from ( "+ sql + " ) as count";

			// + " WHERE ";

			// for pagination
			/*
			 * if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM !=
			 * "" || RUN_SEARCH_TERM != "" || RC_Std_Dev_Status_SEARCH_TERM != "" ||
			 * RC_Mean_Moving_Avg_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_SEARCH_TERM
			 * != "") {
			 */
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
			/* } */

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);
		/*
		 * while (rs.next()) { JSONArray ja = new JSONArray();
		 * ja.put(rs.getString("Date")); ja.put(rs.getString("Run"));
		 * ja.put(rs.getString("Status")); ja.put(rs.getString("colName"));
		 * ja.put(rs.getString("Null_Value")); ja.put(rs.getString("Record_Count"));
		 * ja.put(rs.getString("Null_Percentage"));
		 * ja.put(rs.getString("Null_Threshold")); ja.put(rs.getString("DGroupVal"));
		 * ja.put(rs.getString("DGroupCol")); array.put(ja); }
		 */

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForDefaultValTab")
	public void ColSummTableForDefaultValTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForNullTab");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "colName", "Default_Value", "Default_Count", "Default_Percentage" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;

		dateFilter = (String) session.getAttribute("dateFilter");
		int runFilter = 0;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String idApp = request.getParameter("idApp");
			// Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + idApp);
			// System.out.println("idData=" + idData);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, null, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForDefaultValueDetails(totalRecords, request, tableName, idApp, dateFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForDefaultValueDetails(int totalRecords, HttpServletRequest request,
			String tableName, String idApp, String dateFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String idSearch = "";
			String nameSearch = "";
			String placeSearch = "";
			String citySearch = "";
			String stateSearch = "";
			String phoneSearch = "";
			String globeSearch = "";
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT Date,ROUND(Run, 0) as Run1,colName,Default_Value as Default_Value1,"
						+ "ROUND(CAST(Default_Count as numeric), 0) as Default_Count1,ROUND(CAST(Default_Percentage as numeric),2) as Default_Percentage1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and Default_Count IS NOT NULL";
				
				// + " WHERE ";
				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or colName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Default_Value like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or Default_Count::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Default_Percentage::text like '%" + GLOBAL_SEARCH_TERM + "%')";

				idSearch = "Id::text like " + ID_SEARCH_TERM + "";
				nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
				phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
				
			} else {
				sql = "SELECT Date,FORMAT(Run, 0) as Run1,colName,Default_Value as Default_Value1,"
						+ "FORMAT(Default_Count, 0) as Default_Count1,FORMAT(Default_Percentage,2) as Default_Percentage1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and Default_Count IS NOT NULL";
				
				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or colName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Default_Value like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or Default_Count like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Default_Percentage like '%" + GLOBAL_SEARCH_TERM + "%')";

				idSearch = "Id like " + ID_SEARCH_TERM + "";
				nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
				phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			}

			
			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (ID_SEARCH_TERM != "") {
				searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}

			sql += searchSQL;
			sql += " and (" + dateFilter + ")";
			// sql += " WHERE colName='"+ldd.getDisplayName()+"'";
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("Hello");
			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			String[] columnNames = { "Date", "Run1", "colName", "Default_Value1", "Default_Count1",
					"Default_Percentage1" };

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}

			String query = "SELECT COUNT(*) as count FROM " + tableName + "  WHERE idApp=" + idApp
					+ " and Default_Count IS NOT NULL and (" + dateFilter + ")";

			// + " WHERE ";

			// for pagination
			/*
			 * if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM !=
			 * "" || RUN_SEARCH_TERM != "" || RC_Std_Dev_Status_SEARCH_TERM != "" ||
			 * RC_Mean_Moving_Avg_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_SEARCH_TERM
			 * != "") {
			 */
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
			/* } */
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);
		/*
		 * while (rs.next()) { JSONArray ja = new JSONArray();
		 * ja.put(rs.getString("Date")); ja.put(rs.getString("Run"));
		 * ja.put(rs.getString("Status")); ja.put(rs.getString("colName"));
		 * ja.put(rs.getString("Null_Value")); ja.put(rs.getString("Record_Count"));
		 * ja.put(rs.getString("Null_Percentage"));
		 * ja.put(rs.getString("Null_Threshold")); ja.put(rs.getString("DGroupVal"));
		 * ja.put(rs.getString("DGroupCol")); array.put(ja); }
		 */
		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	/*
	 * @RequestMapping(value="/dynamicDataColumnCsv") Read the header from csv file
	 */
	@RequestMapping(value = "/dynamicDataColumnCsv")
	public void dynamicDataColumnCsv(HttpServletResponse response, HttpServletRequest request, HttpSession session) {

		String tableNameCsv = request.getParameter("tableNameCsv");
		System.out.println("tableName=" + tableNameCsv);
		boolean isHeader = true;
		try {

			JSONObject displayName = new JSONObject();
			displayName = readDataFromCSV(response, request, session, tableNameCsv, isHeader, null, 0);
			System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/*
	 * @RequestMapping(value="/dynamicDataSetCsv") Read 20 records from csv file
	 * with pagination
	 */
	@RequestMapping(value = "/dynamicDataSetCsv")
	public void dynamicDataSetCsv(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		String tableNameCsv = request.getParameter("tableNameCsv");
		String displayData = request.getParameter("vdata");
		System.out.println("tableName=" + tableNameCsv);
		System.out.println("displayData=" + displayData);
		String displayCount = request.getParameter("dispCount");
		System.out.println("displayCount=" + displayCount);
		int displayLength = 0;
		if (displayCount != null && displayCount.length() > 0) {
			displayLength = Integer.parseInt(displayCount);
		}
		PrintWriter out = null;
		boolean isHeader = false;

		JSONObject jsonResult = new JSONObject();

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.out.println("IO Exception message:" + e2.getMessage());
		}

		if (displayData.equalsIgnoreCase("Page") || displayData.equalsIgnoreCase("Page2")) {
			int listDisplayAmount = 10;
			int start = 0;
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			System.out.println("iDisplayLength=" + pageSize);
			System.out.println("iDisplayStart=" + pageNo);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}

			RECORD_SIZE = listDisplayAmount;

			INITIAL = start;

		}

		try {
			jsonResult = readDataFromCSV(response, request, session, tableNameCsv, isHeader, displayData,
					displayLength);

			System.out.println("jsonResult:" + jsonResult);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);

		} catch (Exception e) {
			// System.out.println("Exception Came");
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("recordsTotal", 0);
				jsonResult.put("recordsFiltered", 0);
				jsonResult.put("aaData", array);
				out.print(jsonResult);
				System.out.println("return empty json");
			} catch (JSONException e1) {
				// e1.printStackTrace();
				System.out.println("Json exception message:" + e.getMessage());
			}

			out.print(jsonResult);
			System.out.println("Error getting table:" + tableNameCsv + ", message:" + e.getMessage());
		}

	}
	/*
	 * @return JSONObject
	 * 
	 * @param response, request, session, csv file name, header or data
	 */

	private JSONObject readDataFromCSV(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			String tableNameCsv, boolean isHeader, String displayData, int displayCount) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();

		boolean fileNotFound = true;
		BufferedReader reader = null;

		String folderName = tableNameCsv.replaceAll("//", "/");

		try {

			if (clusterProperties.getProperty("deploymentMode").equals("s3")) {
				System.out.println("\n******* Read from S3 *******");

				String s3CsvPath = appDbConnectionProperties.getProperty("s3CsvPath");
				AWSCredentials credentials = new BasicAWSCredentials(
						appDbConnectionProperties.getProperty("s3.aws.accessKey"),
						appDbConnectionProperties.getProperty("s3.aws.secretKey"));

				AmazonS3 s3client = new AmazonS3Client(credentials);
				System.out.println("folderName:" + folderName);
				System.out.println("s3CsvPath:" + s3CsvPath);
				folderName = folderName.substring(1);
				// Get files list in the folder
				List<String> getObjectslistFromFolder = downloadCsv.getObjectslistFromFolder(s3CsvPath.split("//")[1],
						folderName);
				Iterator<String> iterator = getObjectslistFromFolder.iterator();

				while (iterator.hasNext()) {
					String list = iterator.next();

					if (list.endsWith(".csv")) {
						folderName = list;

						System.out.println("File full path:" + folderName);
						S3Object s3object = s3client
								.getObject(new GetObjectRequest(s3CsvPath.split("//")[1], folderName));

						System.out.println("downloadCsvS3 s3object getContentType()......"
								+ s3object.getObjectMetadata().getContentType());

						reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));

						fileNotFound = false;
					}
				}
			} else if (clusterProperties.getProperty("deploymentMode").equals("hdfs")) {
				System.out.println("\n***** Read from HDFS *****");

				String hdfsCvsPath = clusterProperties.getProperty("hdfs_result_directory");

				FileSystem hdfsClient = downloadCsv.getHDFSClient();
				System.out.println("folderName:" + folderName);
				System.out.println("hdfsCvsPath:" + hdfsCvsPath);

				String fullFolderPath = hdfsCvsPath + folderName;
				System.out.println("fullFolderPath:" + fullFolderPath);

				// Get files list in the folder
				RemoteIterator<LocatedFileStatus> fileStatusListIterator = hdfsClient
						.listFiles(new Path(fullFolderPath), false);

				Path fileHdfsPath = null;
				while (fileStatusListIterator.hasNext()) {
					LocatedFileStatus fileStatus = fileStatusListIterator.next();
					if (fileStatus.getPath().toString().endsWith(".csv")) {
						fileHdfsPath = fileStatus.getPath();
					}
				}

				FSDataInputStream inputStream = hdfsClient.open(fileHdfsPath);

				reader = new BufferedReader(new InputStreamReader(inputStream));

				fileNotFound = false;

			} else {
				File[] fList = null;
				String folderNameFull = System.getenv("DATABUCK_HOME") + folderName;
				File directory = new File(folderNameFull);
				fList = directory.listFiles();

				System.out.println("downloadCsvLocal ........ full =>" + folderNameFull);

				if (fList != null) {

					for (File file : fList) {
						if (file.isFile()) {
							System.out.println(file.getName());
							String fileName = file.getName();

							if (fileName.endsWith(".csv")) {

								String fileFullPath = folderNameFull + "/" + fileName;
								System.out.println("downloadCsvLocal ........ table for csv+" + fileName);

								reader = new BufferedReader(new FileReader(fileFullPath));
								fileNotFound = false;

							}

						}
					}

				}

			}
			String line = "";

			String[] headerData = null;
			int count = 0;
			if (!fileNotFound) {
				while ((line = reader.readLine()) != null) {
					JSONArray ja = new JSONArray();
					line = line.replaceAll("\"", "\\\"");
					if (count == 0 && isHeader) {
						headerData = line.split(",");
						for (int dataCount = 0; dataCount < headerData.length; dataCount++) {

							array.put(headerData[dataCount]);
						}
						// array.put(ja);
					} else if (count < 21 && count > INITIAL && count <= (RECORD_SIZE + INITIAL) && !isHeader
							&& displayData.equalsIgnoreCase("Page"))

					{
						String[] cellData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

						for (int dataCount = 0; dataCount < cellData.length; dataCount++) {
							if (dataCount > (cellData.length - 10)) {
								if (cellData[dataCount].equalsIgnoreCase("Y")) {
									ja.put("<span class=\"label label-danger label-sm\">failed</span>");
								} else {

									ja.put("");
								}
							} else {

								ja.put(cellData[dataCount].replaceAll("\\\"", "\""));
							}
						}
						array.put(ja);
					}

					else if (count > 0 && !isHeader && displayData.equalsIgnoreCase("All")) {
						String[] cellDataAll = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

						for (int dataCountAll = 0; dataCountAll < cellDataAll.length; dataCountAll++) {
							if (dataCountAll > (cellDataAll.length - 10)) {
								if (cellDataAll[dataCountAll].equalsIgnoreCase("Y")) {
									ja.put("<span class=\"label label-danger label-sm\">failed</span>");
								} else {

									ja.put(cellDataAll[dataCountAll]);
								}
							} else {
								ja.put(cellDataAll[dataCountAll].replaceAll("\\\"", "\""));
							}
						}
						array.put(ja);

					} else if (count > 0 && !isHeader && displayData.equalsIgnoreCase("Page2")
							&& count < displayCount + 1 && count > INITIAL && count <= (RECORD_SIZE + INITIAL)) {

						String[] cellDataCount = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

						for (int dataCountNew = 0; dataCountNew < cellDataCount.length; dataCountNew++) {

							ja.put(cellDataCount[dataCountNew].replaceAll("\\\"", "\""));

						}
						array.put(ja);

					}

					count++;
				}

				if (isHeader) {

					System.out.println("jsonInString=" + array);
					result.put("success", array);
				} else if ((!isHeader && displayData.equalsIgnoreCase("All"))) {

					int totalDataRecords = count - 1;
					result.put("iTotalRecords", totalDataRecords);

					result.put("iTotalDisplayRecords", totalDataRecords);

					result.put("aaData", array);
				} else if (!isHeader && displayData.equalsIgnoreCase("Page")) {
					int totalDataRecords = count - 1;
					result.put("iTotalRecords", totalDataRecords);
					if (totalDataRecords < 20) {
						result.put("iTotalDisplayRecords", totalDataRecords);
					} else {
						result.put("iTotalDisplayRecords", 20);
					}
					result.put("aaData", array);
				} else if (!isHeader && displayData.equalsIgnoreCase("Page2")) {
					int totalDataRecords = count - 1;
					result.put("iTotalRecords", totalDataRecords);
					if (totalDataRecords < displayCount) {
						result.put("iTotalDisplayRecords", totalDataRecords);
					} else {
						result.put("iTotalDisplayRecords", displayCount);
					}
					result.put("aaData", array);
				}

			}

			else {
				System.out.println("In fileNotFound============" + fileNotFound);
				String message = "'" + tableNameCsv + "' Data file is not available for Current Run !!";
				session.setAttribute("errormsg", message);
				result.put("fail", message);
			}
		} catch (Exception e) {
			result.put("fail", e.getMessage());
		}

		return result;

	}

	@RequestMapping(value = "/lengthCheckTableName")
	public void lengthCheckTableName(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("lengthCheckTableName");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "Status", "ColName", "max_length_check_enabled", "Length",
				"RecordCount", "TotalFailedRecords", "FailedRecords_Percentage", "Length_Threshold" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;

		dateFilter = (String) session.getAttribute("dateFilter");
		int runFilter = 0;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String idApp = request.getParameter("idApp");
			// Long idData = Long.parseLong(request.getParameter("idData"));
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + idApp);
			// System.out.println("idData=" + idData);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				throw e1;
				// e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSLengthTableDetails(totalRecords, request, tableName, dateFilter, idApp, runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				// e1.printStackTrace();
			}

			out.print(jsonResult);
			// e.printStackTrace();
		}

	}

	private JSONObject getColSLengthTableDetails(int totalRecords, HttpServletRequest request, String tableName,
			String dateFilter, String idApp, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String globeSearch ="";
			String idSearch ="";
			String nameSearch ="";
			String placeSearch =""; 
			String citySearch ="";
			String stateSearch ="";
			String phoneSearch ="";
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "Select Date, Run, Status, ColName, max_length_check_enabled, Length,TotalFailedRecords , Length_Threshold, ROUND(RecordCount,0) as RecordCount, FailedRecords_Percentage  from "
						+ tableName + " where" + " idApp = " + idApp + " and (" + dateFilter + ")";
				
				// + " WHERE ";
				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Length::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or TotalFailedRecords::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Length_Threshold::text like  '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or RecordCount::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or FailedRecords_Percentage::text like  '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status like  '%" + GLOBAL_SEARCH_TERM + "%')";

				idSearch = " AND Id::text like " + ID_SEARCH_TERM + "";
				nameSearch = " AND Date::text like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " AND  Run::text like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " AND RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " AND RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM
						+ "%'";
				phoneSearch = " AND RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			} else {
				sql = "Select Date, Run, Status, ColName, max_length_check_enabled, Length,TotalFailedRecords , Length_Threshold, FORMAT(RecordCount,0) as RecordCount, FailedRecords_Percentage   from "
						+ tableName + " where" + " idApp = " + idApp + " and (" + dateFilter + ")";
				
				// + " WHERE ";
				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or Length like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or TotalFailedRecords like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Length_Threshold like  '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or RecordCount like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or FailedRecords_Percentage like  '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or Status like  '%" + GLOBAL_SEARCH_TERM + "%')";

				idSearch = " AND Id like " + ID_SEARCH_TERM + "";
				nameSearch = " AND Date like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " AND  Run like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " AND RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " AND RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM
						+ "%'";
				phoneSearch = " AND RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			}

			System.out.println("DATE_SEARCH_TERM: " + DATE_SEARCH_TERM);
			if (GLOBAL_SEARCH_TERM != "") {
				System.out.println("");
				searchSQL = globeSearch;
			} else if (ID_SEARCH_TERM != "") {
				searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				searchSQL = citySearch;

				// System.out.println(searchSQL);
			}

			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			// sql += " and (" + dateFilter + ")";
			// sql += " WHERE colName='"+ldd.getDisplayName()+"'";
			// sql += " order by " + COLUMN_NAME + " " + DIRECTION;
			if (COLUMN_NAME.equals("Date")) {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION + ", Run " + DIRECTION;
			} else {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			String[] columnNames = { "Date", "Run", "Status", "ColName", "max_length_check_enabled", "Length",
					"TotalFailedRecords", "Length_Threshold", "RecordCount", "FailedRecords_Percentage" };

			long idData = iResultsDAO.getIdDataFromListApplictions(Long.valueOf(idApp));

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);
					
					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {
						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}
					// Mamta 5-May-2022 to convert Total Failed Records to integer.
					if (columnName.equals("TotalFailedRecords")) {
						double data = Double.parseDouble(columnValue);
						int value = (int) data;
						columnValue = Integer.toString(value);

					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count FROM " + tableName + "  WHERE  idApp = " + idApp + " and ("
					+ dateFilter + ")";
			if (runFilter != 0) {
				query += " and Run = " + runFilter;
			}

			// + " WHERE ";

			// for pagination
			/*
			 * if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM !=
			 * "" || RUN_SEARCH_TERM != "" || RC_Std_Dev_Status_SEARCH_TERM != "" ||
			 * RC_Mean_Moving_Avg_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_SEARCH_TERM
			 * != "") {
			 */
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
			/* } */
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);
		/*
		 * while (rs.next()) { JSONArray ja = new JSONArray();
		 * ja.put(rs.getString("Date")); ja.put(rs.getString("Run"));
		 * ja.put(rs.getString("Status")); ja.put(rs.getString("colName"));
		 * ja.put(rs.getString("Null_Value")); ja.put(rs.getString("Record_Count"));
		 * ja.put(rs.getString("Null_Percentage"));
		 * ja.put(rs.getString("Null_Threshold")); ja.put(rs.getString("DGroupVal"));
		 * ja.put(rs.getString("DGroupCol")); array.put(ja); }
		 */
		System.out.println("JSONArray=" + dataArray);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForBadDataTableTab")
	public void ColSummTableForBadDataTableTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForBadDataTableTab");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");

		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + idApp);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "Date", "Run", "status", "ColName", "TotalRecord", "TotalBadRecord",
					"badDataPercentage", "badDataThreshold" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				throw e1;
				// e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForBadDataTableTabDetails(totalRecords, request, tableName, idApp, columnNames,
					dateFilter, runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				// e1.printStackTrace();
			}

			out.print(jsonResult);
			// e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForBadDataTableTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String idApp, String[] columnNames, String dateFilter, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		String finalSearchQry = "";
		String searchQry = "";
		for (int i = 0; i < columnNames.length; i++) {
			// Query compatibility changes for both POSTGRES and MYSQLs
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				searchQry = columnNames[i] + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			else
				searchQry = columnNames[i] + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			
			finalSearchQry = finalSearchQry + searchQry;
		}
		finalSearchQry = finalSearchQry.trim();
		StringBuilder builder = new StringBuilder(finalSearchQry);
		builder = new StringBuilder(finalSearchQry);
		// int x = finalSearchQry.length() - 1;
		StringBuilder finalString = builder.deleteCharAt(finalSearchQry.length() - 1);
		StringBuilder builder1 = new StringBuilder(finalString);
		finalString = builder1.deleteCharAt(finalString.length() - 1);

		try {
			String sql = "SELECT Date,Run,status,ColName,TotalRecord,TotalBadRecord,badDataPercentage,badDataThreshold from "
					+ tableName;
			String globeSearch = " and (" + finalString.toString() + ")";
			String idSearch = "";
			String nameSearch = "";
			String placeSearch ="";
			String citySearch = "";
			String stateSearch = "";
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				idSearch = "Id::text like " + ID_SEARCH_TERM + "";
				nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			} else {
				idSearch = "Id like " + ID_SEARCH_TERM + "";
				nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
				placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
				citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
				stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			}
			// String phoneSearch="";
			// String phoneSearch = " RC_Mean_Moving_Avg like '%" +
			// RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (ID_SEARCH_TERM != "") {
				searchSQL = idSearch;
			} else if (DATE_SEARCH_TERM != "") {
				searchSQL = nameSearch;
			} else if (RUN_SEARCH_TERM != "") {
				searchSQL = placeSearch;
			} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
				searchSQL = citySearch;
			} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
				searchSQL = stateSearch;
			} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
				// searchSQL = phoneSearch;
				// System.out.println(searchSQL);
			}
			sql += " where idApp=" + idApp + " and (" + dateFilter + ")";
			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY " + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			String[] columNames = { "Date", "Run", "status", "ColName", "TotalRecord", "TotalBadRecord",
					"badDataPercentage", "badDataThreshold" };

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float")|| columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count FROM " + tableName;

			query += " where idApp=" + idApp + " and (" + dateFilter + ")";
			query += searchSQL;
			if (runFilter != 0) {
				query += " and Run = " + runFilter;
			}

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForPatternTableTab")
	public void ColSummTableForPatternTableTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("============ColSummTableForPatternTableTab===================");

		dateFilter = (String) session.getAttribute("dateFilter");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int runFilter = 0;
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + tableName);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "Date", "Run", "Status", "Col_Name", "Total_Records", "Total_Failed_Records",
					"FailedRecords_Percentage", "Pattern_Threshold" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountPattern(tableName, maxRun, idApp, dateFilter);
			} catch (SQLException e1) {
				// e1.printStackTrace();
				throw e1;
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForPatternTableTabDetails(totalRecords, request, tableName, idApp, columnNames,
					dateFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				// e1.printStackTrace();
			}

			out.print(jsonResult);
			// e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForPatternTableTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String idApp, String[] columnNames, String dateFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		String finalSearchQry = "";
		String searchQry = "";
		for (int i = 0; i < columnNames.length; i++) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				searchQry = columnNames[i] + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			else
				searchQry = columnNames[i] + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			
			finalSearchQry = finalSearchQry + searchQry;
		}
		finalSearchQry = finalSearchQry.trim();
		StringBuilder builder = new StringBuilder(finalSearchQry);
		builder = new StringBuilder(finalSearchQry);
		// int x = finalSearchQry.length() - 1;
		StringBuilder finalString = builder.deleteCharAt(finalSearchQry.length() - 1);
		StringBuilder builder1 = new StringBuilder(finalString);
		finalString = builder1.deleteCharAt(finalString.length() - 1);

		try {
			String sql = "SELECT Date,Run,Status,Col_Name,Total_Records,Total_Failed_Records,FailedRecords_Percentage,Pattern_Threshold from "
					+ tableName + " where idApp=" + idApp + " and (" + dateFilter + ")";
			// + " WHERE ";
			String globeSearch = " and (" + finalString.toString() + ")";

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (searchSQL != null && !searchSQL.trim().isEmpty()) {
				searchSQL = " and (" + searchSQL + ")";
			}

			sql += searchSQL;
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count FROM " + tableName + " where idApp=" + idApp + " and ("
					+ dateFilter + ")";

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForDefaultPatternTab")
	public void ColSummTableForDefaultPatternTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("============ColSummTableForDefaultPatternTab===================");

		dateFilter = (String) session.getAttribute("dateFilter");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int runFilter = 0;
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "Date", "Run", "Status", "Col_Name", "Total_Records", "Total_Failed_Records",
					"Total_Matched_Records", "Patterns_List", "New_Pattern", "FailedRecords_Percentage",
					"Pattern_Threshold", "Csv_File_Path" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountPattern(tableName, maxRun, idApp, dateFilter);
			} catch (SQLException e1) {
				// e1.printStackTrace();
				throw e1;
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForDefaultPatternTabDetails(totalRecords, request, tableName, idApp,
					columnNames, dateFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				// e1.printStackTrace();
			}

			out.print(jsonResult);
			// e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForDefaultPatternTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String idApp, String[] columnNames, String dateFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		String finalSearchQry = "";
		String searchQry = "";
		for (int i = 0; i < columnNames.length; i++) {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				searchQry = columnNames[i] + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			else
				searchQry = columnNames[i] + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			finalSearchQry = finalSearchQry + searchQry;
		}
		finalSearchQry = finalSearchQry.trim();
		StringBuilder builder = new StringBuilder(finalSearchQry);
		builder = new StringBuilder(finalSearchQry);
		// int x = finalSearchQry.length() - 1;
		StringBuilder finalString = builder.deleteCharAt(finalSearchQry.length() - 1);
		StringBuilder builder1 = new StringBuilder(finalString);
		finalString = builder1.deleteCharAt(finalString.length() - 1);

		try {
			String sql = "SELECT Date,Run,Status,Col_Name,Total_Records,Total_Failed_Records,Total_Matched_Records,Patterns_List,New_Pattern,FailedRecords_Percentage,Pattern_Threshold,Csv_File_Path from "
					+ tableName + " where idApp=" + idApp + " and (" + dateFilter + ")";
			// + " WHERE ";
			String globeSearch = " and (" + finalString.toString() + ")";

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (searchSQL != null && !searchSQL.trim().isEmpty()) {
				searchSQL = " and (" + searchSQL + ")";
			}

			sql += searchSQL;
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count FROM " + tableName + " where idApp=" + idApp + " and ("
					+ dateFilter + ")";

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("totalAfterSearch=" + totalAfterSearch);

		// System.out.println("JSONArray=" + dataArray);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/colRules")
	public void colRules(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// System.out.println("ProcessDataTable");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// dateFilter = (String) session.getAttribute("dateFilter");
		// runFilter = (int) session.getAttribute("RunFilter");
		// String idDataValue = (String) session.getAttribute("datasourceIdData");
		// int idData = Integer.parseInt(idDataValue);

		/*
		 * if(idData == -1) { idData = 0; }
		 */
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			// String tableName = request.getParameter("tableName");
			String idDataValue = request.getParameter("idData");
			String dimensionIdValue = request.getParameter("dimensionId");
			// String maxRun = request.getParameter("maxRun");
			// System.out.println("tableName=" + tableName);
			int dimensionId = Integer.parseInt(dimensionIdValue);
			int idData = Integer.parseInt(idDataValue);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "createdAt", "ruleName", "description", "ruleType", "expression", "Dimension",
					"matchingRules", "ruleThreshold", "createdByUser" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountColumnRulesData(idData);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColRulesDataTableDetails(totalRecords, idData, columnNames, dimensionId);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColRulesDataTableDetails(int totalRecords, int idData, String[] columnNames,
			int dimensionId) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		String searchSQL = "";
		try {

			/*
			 * String sql1 =
			 * "SELECT createdAt,ruleName,description,ruleType,expression,ds.dimensionName as Dimension,matchingRules,ruleThreshold,createdByUser from listColRules la,dimension ds where la.domensionId ="
			 * +dimensionId+" and idData = " + idData; String sql =
			 * "SELECT createdAt,ruleName,description,ruleType,expression,ds.dimensionName as Dimension,matchingRules,ruleThreshold,createdByUser from listColRules la,dimension ds where la.domensionId = ds.idDimension and idData = "
			 * + idData;
			 */
			// + " WHERE ";
			String sql = "SELECT createdAt,ruleName,description,ruleType,expression,ds.dimensionName as Dimension,matchingRules,ruleThreshold,createdByUser from listColRules la,dimension ds";
			if (dimensionId == -1) {
				sql = sql + " where la.domensionId = ds.idDimension and la.activeFlag='Y' and idData = " + idData;
			} else {
				sql = sql + " where la.domensionId =" + dimensionId
						+ " and la.domensionId = ds.idDimension and la.activeFlag='Y' and idData = " + idData;
			}
			String globeSearch = " and (createdAt like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ruleName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or ruleType like  '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or expression like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ds.dimensionName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or matchingRules like  '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or ruleThreshold like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or createdByUser like  '%"
					+ GLOBAL_SEARCH_TERM + "%')";

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			// Mamta 12-4-2022 to apply sorting to columns
			if (COLUMN_NAME.equals("Date")) {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
			} else {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {
				int colIndex = 0;
				JSONArray ja = new JSONArray();
				for (String colName : columnNames) {

					if (columnNames.length < colIndex) {

						if (rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("double")
								|| rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("decimal")
								|| rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("float")
								|| rs.getMetaData().getColumnName(colIndex + 1).equalsIgnoreCase("RC_Deviation")) {

							ja.put(numberFormat.format(rs.getDouble(columnNames[colIndex])));

							colIndex++;
						} else {

							ja.put("<td><span class='label label-success label-sm'>"
									+ rs.getString(columnNames[colIndex]) + "</span></td>");
							colIndex++;
						}

					} else {
						ja.put(rs.getString(columnNames[colIndex]));
						colIndex++;
					}

				}
				array.put(ja);
			}
			String query = "SELECT COUNT(*) as count from listColRules where activeFlag='Y' and idData = " + idData;

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", array);
		} catch (Exception e) {

		}

		return result;
	}

	// ---------End----------------//

	// ------------Process Data--------------
	@RequestMapping(value = "/ProcessDataTable")
	public void ProcessDataTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("ProcessDataTable");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "idApp", "Run", "Date", "folderName" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountProcessData(idApp, dateFilter, runFilter);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getProcessDataTableDetails(totalRecords, request, idApp, columnNames, dateFilter, runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getProcessDataTableDetails(int totalRecords, HttpServletRequest request, String idApp,
			String[] columnNames, String dateFilter, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		try {
			String sql = "SELECT * from processData where " + "idApp = " + idApp + " and (" + dateFilter + ")";
			// + " WHERE ";
			// Query compatibility changes for both POSTGRES and MYSQL
			String globeSearch="";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			         globeSearch = " and (idApp::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Date::text like  '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or folderName like '%" + GLOBAL_SEARCH_TERM + "%')";
			}else {
					 globeSearch = " and (idApp like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%"
										+ GLOBAL_SEARCH_TERM + "%'" + "or Date like  '%" + GLOBAL_SEARCH_TERM + "%'"
										+ "or folderName like '%" + GLOBAL_SEARCH_TERM + "%')";
			}

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count from processData where" + " idApp = " + idApp + " and ("
					+ dateFilter + ")";

			query += searchSQL;

			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + dataArray);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}
	// ------------Process Data--------------

	@RequestMapping(value = "/ColSummTableForDateRuleTableTab")
	public void ColSummTableForDateRuleTableTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForDateRuleTableTab");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = {"Date", "Run", "DateField", "TotalNumberOfRecords" , "TotalFailedRecords"};
			
			// remove the extra columns id and idApp
			columnNames = ArrayUtils.removeElement(columnNames, "Id");
			columnNames = ArrayUtils.removeElement(columnNames, "idApp");

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountDateRule(tableName, maxRun, idApp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForDateRuleTableTabDetails(totalRecords, request, tableName, columnNames,
					dateFilter, runFilter, idApp);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForDateRuleTableTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String[] columnNames, String dateFilter, int runFilter, String idApp) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();

		String searchSQL = "";

		String finalSearchQry = "";
		String searchQry = "";
		for (int i = 0; i < columnNames.length; i++) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				searchQry = columnNames[i] + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			else 
				searchQry = columnNames[i] + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			
			finalSearchQry = finalSearchQry + searchQry;
		}
		finalSearchQry = finalSearchQry.trim();
		StringBuilder builder = new StringBuilder(finalSearchQry);
		builder = new StringBuilder(finalSearchQry);
		// int x = finalSearchQry.length() - 1;
		StringBuilder finalString = builder.deleteCharAt(finalSearchQry.length() - 1);
		StringBuilder builder1 = new StringBuilder(finalString);
		finalString = builder1.deleteCharAt(finalString.length() - 1);

		String colNamesQuery = "";

		int pos = 0;
		for (String col : columnNames) {
			if (col != null && !col.trim().isEmpty()) {
				colNamesQuery = (pos == 0) ? col : colNamesQuery + "," + col;
				++pos;
			}
		}

		try {
			String sql = "SELECT " + colNamesQuery + " from " + tableName + " where idApp= " + idApp
					+ " and Date = (select MAX(Date) from " + tableName + "  where idApp= " + idApp
					+ ") and Run=(Select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select MAX(Date) from " + tableName + "  where idApp= " + idApp + "))";
			// + " WHERE ";
			String globeSearch = " and (" + finalString.toString() + ")";

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			System.out.println(sql);

			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}

			String query = "SELECT count(*) as count from " + tableName + " where idApp= " + idApp
					+ " and Date = (select MAX(Date) from " + tableName + "  where idApp= " + idApp
					+ ") and Run=(Select max(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date=(select MAX(Date) from " + tableName + "  where idApp= " + idApp + "))";

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForFailedDateRuleTableTab")
	public void ColSummTableForFailedDateRuleTableTab(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("ColSummTableForDateRuleTableTab");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int runFilter = 0;
		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);
			System.out.println("idApp=" + idApp);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = {"Date", "Run","DateFieldCols","DateFieldValues", "dGroupVal", "dGroupCol", "FailureReason"};

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountDateRule(tableName, maxRun, idApp);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForFailedDateRuleTableTabDetails(totalRecords, request, tableName, columnNames,
					idApp);
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForFailedDateRuleTableTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String[] columnNames, String idApp) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		String finalSearchQry = "";
		String searchQry = "";
		for (int i = 0; i < columnNames.length; i++) {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				searchQry = columnNames[i] + "::text like '%" + GLOBAL_SEARCH_TERM + "%' or ";
			else
				searchQry = columnNames[i] + " like '%" + GLOBAL_SEARCH_TERM + "%' or ";

			finalSearchQry = finalSearchQry + searchQry;
		}
		finalSearchQry = finalSearchQry.trim();
		StringBuilder builder = new StringBuilder(finalSearchQry);
		builder = new StringBuilder(finalSearchQry);
		// int x = finalSearchQry.length() - 1;
		StringBuilder finalString = builder.deleteCharAt(finalSearchQry.length() - 1);
		StringBuilder builder1 = new StringBuilder(finalString);
		finalString = builder1.deleteCharAt(finalString.length() - 1);

		String colNamesQuery = "";

		int pos = 0;
		for (String col : columnNames) {
			if (col != null && !col.trim().isEmpty()) {
				colNamesQuery = (pos == 0) ? col : colNamesQuery + "," + col;
				++pos;
			}
		}

		try {
			String sql = "SELECT " + colNamesQuery + " from " + tableName + " where idApp=" + idApp
					+ " and Run = (select MAX(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date = (select MAX(date) from " + tableName + " where idApp=" + idApp
					+ ")) and Date=(select MAX(date) from " + tableName + " where idApp=" + idApp + ")";
			// + " WHERE ";
			String globeSearch = " AND " + finalString.toString();

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			// for searching
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count from " + tableName + " where idApp=" + idApp
					+ " and Run = (select MAX(Run) from " + tableName + " where idApp=" + idApp
					+ " and Date = (select MAX(date) from " + tableName + " where idApp=" + idApp
					+ ")) and Date=(select MAX(date) from " + tableName + " where idApp=" + idApp + ")";

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("totalAfterSearch=" + totalAfterSearch);

		System.out.println("JSONArray=" + array);

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/ColSummTableForValidityTableTab")
	public void ColSummTableForValidityTableTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("DGroupTable");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		String[] columnNames = { "id", "Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek", "hourOfDay",
				"RecordCount", "dGroupVal", "dgroupCol", "dGroupRcStatus", "Action", "UserName", "Time" };
		// "RC_Std_Dev", "RC_Mean", "dGroupDeviation", "dgDqi",

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);
			String idApp = request.getParameter("idApp");
			System.out.println("idApp=" + idApp);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecordSValidity = -1;
			try {
				totalRecordSValidity = getTotalRecordCountValidity(tableName, idApp, maxRun, dateFilter, runFilter);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			try {
				jsonResult = getColSummTableForValidityTabDetails(totalRecordSValidity, request, tableName, idApp,
						dateFilter, runFilter);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForValidityTabDetails(int totalRecordSValidity, HttpServletRequest request,
			String tableName, String idApp, String dateFilter, int runFilter)
			throws SQLException, ClassNotFoundException {

		int totalAfterSearchValidity = totalRecordSValidity;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		String[] columnNames = { "id", "Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek", "hourOfDay",
				"RecordCount", "dGroupVal", "dgroupCol", "dGroupRcStatus", "Action", "UserName", "Time" };
		// "RC_Std_Dev", "RC_Mean", "dGroupDeviation", "dgDqi",

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
		Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		String idSearch = "";
		String nameSearch = "";
		String placeSearch = "";
		String citySearch = "";
		String stateSearch = "";
		String phoneSearch = "";
		String globeSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "SELECT Id,Date,Run,dayOfYear,month,dayOfMonth,dayOfWeek, hourOfDay, "
				+ "ROUND(RecordCount, 0) as RecordCount,dGroupVal,dgroupCol,"
				+ "	dGroupRcStatus,Action,UserName,Time, RC_Std_Dev,RC_Mean,dGroupDeviation,"
				+ "(CASE WHEN (dGroupDeviation <= " + threshold
				+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 " + "ELSE (100 - ( (ABS(dGroupDeviation - "
				+ threshold + ") *100) / ( 6 - " + threshold + " ) )) END) END) AS dgDqi FROM " + tableName
				+ " WHERE idApp=" + idApp + " and (" + dateFilter + ") and Validity = 'True'";
			
			// + " WHERE ";
			globeSearch = " AND (Id::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RecordCount::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupVal like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dgroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Std_Dev::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean::text like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupDeviation::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupRcStatus like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id::text like " + ID_SEARCH_TERM + "";
			nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			
		} else {
			sql = "SELECT Id,Date,Run,dayOfYear,month,dayOfMonth,dayOfWeek, hourOfDay, "
					+ "FORMAT(RecordCount, 0) as RecordCount, " + "dGroupVal,dgroupCol,"
					+ "	dGroupRcStatus,Action,UserName,Time, RC_Std_Dev,RC_Mean,dGroupDeviation,"
					+ "(CASE WHEN (dGroupDeviation <= " + threshold
					+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 " + "ELSE (100 - ( (ABS(dGroupDeviation - "
					+ threshold + ") *100) / ( 6 - " + threshold + " ) )) END) END) AS dgDqi FROM " + tableName
					+ " WHERE idApp=" + idApp + " and (" + dateFilter + ") and Validity = 'True'";
			
			globeSearch = " AND (Id like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Date like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfYear like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or month like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dayOfMonth like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dayOfWeek like '%" + GLOBAL_SEARCH_TERM + "%'" + "or hourOfDay like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or RecordCount like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupVal like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or dgroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or RC_Std_Dev like '%" + GLOBAL_SEARCH_TERM + "%'" + "or RC_Mean like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupDeviation like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or dGroupRcStatus like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id like " + ID_SEARCH_TERM + "";
			nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}
		
		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (ID_SEARCH_TERM != "") {
			searchSQL = idSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = nameSearch;
		} else if (RUN_SEARCH_TERM != "") {
			searchSQL = placeSearch;
		} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
			searchSQL = citySearch;
		} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
			searchSQL = stateSearch;
		} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			searchSQL = phoneSearch;
		}
		// HttpSession session = request.getSession();

		sql += searchSQL;
		// sql += " and (" + dateFilter + ")";
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;
		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);

		DecimalFormat numberFormat = new DecimalFormat("#0.00");
		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}

		/*
		 * ja.put(rs.getString("Id")); ja.put(rs.getString("Date"));
		 * ja.put(rs.getString("Run")); ja.put(rs.getString("dayOfYear"));
		 * ja.put(rs.getString("month")); ja.put(rs.getString("dayOfMonth"));
		 * ja.put(rs.getString("dayOfWeek")); ja.put(rs.getString("hourOfDay"));
		 * ja.put(rs.getString("RecordCount")); ja.put(rs.getString("dGroupVal"));
		 * ja.put(rs.getString("dgroupCol")); ja.put(rs.getString("RC_Std_Dev"));
		 * ja.put(rs.getString("RC_Mean")); ja.put(rs.getString("dGroupDeviation"));
		 * ja.put(rs.getString("dGroupRcStatus"));
		 */

		System.out.println("JSONArray=" + array);

		String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " WHERE idApp="+idApp+" and (" + dateFilter
				+ ") and Validity = 'True'";
		// + " WHERE ";

		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
				|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
				|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearchValidity = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecordSValidity);
			result.put("iTotalDisplayRecords", totalAfterSearchValidity);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/recordAnomalyTable")
	public void recordAnomalyTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("DGroupTable");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String idApp = request.getParameter("idApp");
		Long l_idApp = Long.parseLong(idApp);
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(l_idApp);
		String[] columnNames = new String[] { "Date", "Run", "ColName", "ColVal", "mean", "stddev", "dGroupVal",
				"dGroupCol", "status", "ra_Deviation", "threshold" };
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			System.out.println("tableName=" + tableName);
			// String idApp = request.getParameter("idApp");
			System.out.println("idApp=" + idApp);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}
			String colName = columnNames[column];
			int totalRecordCount = -1;
			try {
				totalRecordCount = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;
			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			try {
				jsonResult = recordAnomalyTableDetail(listApplicationsData, totalRecordCount, request, tableName,
						dateFilter, runFilter, idApp);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject recordAnomalyTableDetail(ListApplications listApplicationsData, int totalRecordCount,
			HttpServletRequest request, String tableName, String dateFilter, int runFilter, String idApp)
			throws SQLException, ClassNotFoundException {

		int totalCountAfterSearch = totalRecordCount;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";
		String[] columnNames = new String[] { "Date", "Run", "ColName", "ColVal", "mean", "stddev", "dGroupVal",
				"dGroupCol", "status", "ra_Deviation", "threshold" };
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		String idSearch = "";
		String nameSearch = "";
		String placeSearch = "";
		String citySearch = "";
		String stateSearch = "";
		String phoneSearch = "";
		String globeSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

			sql = "SELECT Date,ROUND(Run, 0) as Run,ColName," + "ROUND(CAST(ColVal as numeric), 2) as ColVal,"
					+ "ROUND(CAST(mean as numeric), 2) as mean, ROUND(CAST(stddev as numeric), 2) as stddev,dGroupVal,dGroupCol,"
					+ " status,ROUND(CAST(ra_Deviation as numeric), 2) as ra_Deviation, threshold FROM " + tableName + " Where idApp="
					+ idApp + " and (Run > 0) and (" + dateFilter + ")";
			
			if (listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")) {

				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or mean::text like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or stddev::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColVal::text like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or status::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or RA_Dqi::text like '%" + GLOBAL_SEARCH_TERM + "%')";
			} else {

				globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or mean::text like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or stddev::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColVal like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or status::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ra_Deviation::text like '%"
						+ GLOBAL_SEARCH_TERM + "%')";
			}
			
			idSearch = "Id::text like " + ID_SEARCH_TERM + "";
			nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
			
		} else {
			sql = "SELECT Date,FORMAT(Run, 0) as Run,ColName," + "FORMAT(ColVal, 2) as ColVal,"
					+ "FORMAT(mean, 2) as mean, FORMAT(stddev, 2) as stddev,dGroupVal,dGroupCol,"
					+ " status,FORMAT(ra_Deviation, 2) as ra_Deviation, threshold FROM " + tableName + " Where idApp="
					+ idApp + " and (Run > 0) and (" + dateFilter + ")";
			
			if (listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")) {

				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or mean like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or stddev like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColVal like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or status like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or RA_Dqi like '%" + GLOBAL_SEARCH_TERM + "%')";
			} else {

				globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or ColName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or mean like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or stddev like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColVal like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or status like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ra_Deviation like '%"
						+ GLOBAL_SEARCH_TERM + "%')";
			}
			
			idSearch = "Id like " + ID_SEARCH_TERM + "";
			nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}
		
		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (ID_SEARCH_TERM != "") {
			searchSQL = idSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = nameSearch;
		} else if (RUN_SEARCH_TERM != "") {
			searchSQL = placeSearch;
		} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
			searchSQL = citySearch;
		} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
			searchSQL = stateSearch;
		} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			searchSQL = phoneSearch;
		}

		sql += searchSQL;
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);

		DecimalFormat numberFormat = new DecimalFormat("#0.00");
		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}

		/*
		 * ja.put(rs.getString("Id")); ja.put(rs.getString("Date"));
		 * ja.put(rs.getString("Run")); ja.put(rs.getString("dayOfYear"));
		 * ja.put(rs.getString("month")); ja.put(rs.getString("dayOfMonth"));
		 * ja.put(rs.getString("dayOfWeek")); ja.put(rs.getString("hourOfDay"));
		 * ja.put(rs.getString("RecordCount")); ja.put(rs.getString("dGroupVal"));
		 * ja.put(rs.getString("dgroupCol")); ja.put(rs.getString("RC_Std_Dev"));
		 * ja.put(rs.getString("RC_Mean")); ja.put(rs.getString("dGroupDeviation"));
		 * ja.put(rs.getString("dGroupRcStatus"));
		 */

		System.out.println("JSONArray=" + array);

		String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " WHERE idApp=" + idApp
				+ " and (Run > 0) and (" + dateFilter + ")";
		// + " WHERE ";
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || ID_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || RUN_SEARCH_TERM != ""
				|| RC_Std_Dev_Status_SEARCH_TERM != "" || RC_Mean_Moving_Avg_Status_SEARCH_TERM != ""
				|| RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalCountAfterSearch = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecordCount);
			result.put("iTotalDisplayRecords", totalCountAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}
		return result;
	}

	@RequestMapping(value = "/ColSummTableForStringTab")
	public void ColSummTableForStringTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForStringTab");
		String[] columnNames = { "Date", "Run1", "ColName", "dGroupCol", "dGroupVal", "uniqueValuesCount",
				"missingValueCount", "newValueCount" };
		String dateFilterDrift = "Date >= '" + (String) session.getAttribute("toDate") + "' and Date" + " <= '"
				+ (String) session.getAttribute("fromDate") + "'";
		runFilter = (int) session.getAttribute("RunFilter");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			boolean booleanCheckFailedRecords = false;
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String idApp = request.getParameter("idApp");
			System.out.println("tableName=" + tableName);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilterDrift, runFilter, searchValue,
						booleanCheckFailedRecords);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			DATE_SEARCH_TERM = request.getParameter("sSearch_0");
			RUN_SEARCH_TERM = request.getParameter("sSearch_1");
			COL_NAME_SEARCH_TERM = request.getParameter("sSearch_2");
			UNIQUE_VALUES_COUNT_SEARCH_TERM = request.getParameter("sSearch_3");
			MISSING_VALUE_COUNT_SEARCH_TERM = request.getParameter("sSearch_4");
			NEW_VALUE_COUNT_SEARCH_TERM = request.getParameter("sSearch_5");

			STRINGTAB_COLUMN_NAME = colName;
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getColSummTableForStringTabDetails(totalRecords, request, tableName, idApp, dateFilterDrift,
					runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForStringTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, String idApp, String dateFilterDrift, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String globeSearch = "";
			String dateSearch = "";
			String colNameSearch = "";
			String uniqueValuesCountSearch = "";
			String missingValueCountSearch = "";
			String newValueCountSearch = "";
			
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT Date,ROUND(Run, 0) as Run1,ColName,dGroupCol,dGroupVal,uniqueValuesCount,missingValueCount,newValueCount from "
					+ tableName + " where idApp=" + idApp + " and (" + dateFilterDrift + ") ";
				
				globeSearch = " and (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or colName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or uniqueValuesCount::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or missingValueCount::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or newValueCount::text like '%" + GLOBAL_SEARCH_TERM + "%' "
						+ "or dGroupVal like '%" + GLOBAL_SEARCH_TERM + "%' " + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM
						+ "%' )";

				dateSearch = " Date::text like " + DATE_SEARCH_TERM + "";
				colNameSearch = " colName like " + COL_NAME_SEARCH_TERM + "";
				uniqueValuesCountSearch = "uniqueValuesCount::text like " + UNIQUE_VALUES_COUNT_SEARCH_TERM + "";
				missingValueCountSearch = "missingValueCount::text like " + MISSING_VALUE_COUNT_SEARCH_TERM + "";
				newValueCountSearch = "newValueCount::text like " + NEW_VALUE_COUNT_SEARCH_TERM + "";
				
			} else {
				sql = "SELECT Date,FORMAT(Run, 0) as Run1,ColName,dGroupCol,dGroupVal,uniqueValuesCount,missingValueCount,newValueCount from "
						+ tableName + " where idApp=" + idApp + " and (" + dateFilterDrift + ") ";
				
				globeSearch = " and (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or colName like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or uniqueValuesCount like '%" + GLOBAL_SEARCH_TERM + "%'" + "or missingValueCount like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or newValueCount like '%" + GLOBAL_SEARCH_TERM + "%' "
						+ "or dGroupVal like '%" + GLOBAL_SEARCH_TERM + "%' " + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM
						+ "%' )";

				dateSearch = " Date like " + DATE_SEARCH_TERM + "";
				colNameSearch = " colName like " + COL_NAME_SEARCH_TERM + "";
				uniqueValuesCountSearch = "uniqueValuesCount like " + UNIQUE_VALUES_COUNT_SEARCH_TERM + "";
				missingValueCountSearch = "missingValueCount like " + MISSING_VALUE_COUNT_SEARCH_TERM + "";
				newValueCountSearch = "newValueCount like " + NEW_VALUE_COUNT_SEARCH_TERM + "";
			}
			// ******************************************************************************

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			} else if (DATE_SEARCH_TERM != "") {
				// searchSQL = dateSearch;
			} else if (COL_NAME_SEARCH_TERM != "") {
				searchSQL = colNameSearch;
			} else if (UNIQUE_VALUES_COUNT_SEARCH_TERM != "") {
				searchSQL = uniqueValuesCountSearch;
			} else if (MISSING_VALUE_COUNT_SEARCH_TERM != "") {
				searchSQL = missingValueCountSearch;
			} else if (NEW_VALUE_COUNT_SEARCH_TERM != "") {
				searchSQL = newValueCountSearch;
			}

			if (STRINGTAB_COLUMN_NAME.equals("Date")) {
				STRINGTAB_COLUMN_NAME = "Date, Run ";
			}
			// ******************************************************************************
			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			// sql += " order by Date desc, Run desc ";
			if (COLUMN_NAME.equals("Date")) {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION + ", Run " + DIRECTION;
			} else {
				sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("SQLQUERY" + sql);
			String[] columnNames = { "Date", "Run1", "ColName", "dGroupCol", "dGroupVal", "uniqueValuesCount",
					"missingValueCount", "newValueCount" };
			// for searching
			SqlRowSet newRs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			while (newRs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = newRs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(newRs.getDouble(columnName));
					} else {
						columnValue = newRs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			System.out.println("JSONArray=" + array);

			String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where idApp=" + idApp + " and ("
					+ dateFilterDrift + ")";

			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}
			/*
			 * query += searchSQL;
			 * 
			 * System.out.println(query); SqlRowSet results =
			 * jdbcTemplate1.queryForRowSet(query);
			 * 
			 * if (results.next()) { totalAfterSearch = results.getInt("count"); }
			 */

			/* } */

			// for pagination

			/*
			 * if (GLOBAL_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" ||
			 * COL_NAME_SEARCH_TERM != "" || UNIQUE_VALUES_COUNT_SEARCH_TERM != "" ||
			 * MISSING_VALUE_COUNT_SEARCH_TERM != "" || NEW_VALUE_COUNT_SEARCH_TERM != "") {
			 */

			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}

			/* } */
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("totalAfterSearch=" + totalAfterSearch);
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	@RequestMapping(value = "/DataDriftTable")
	public void DataDriftTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		System.out.println("DataDriftTable");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "colName", "uniqueValues", "PotentialDuplicates", "dGroupVal",
				"dGroupCol" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String dateFilterDrift = "Date >= '" + (String) session.getAttribute("toDate") + "' and Date" + " <= '"
				+ (String) session.getAttribute("fromDate") + "'";
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			String maxRun = "true";
			System.out.println("tableName=" + tableName);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountDrift(tableName, idApp, maxRun, dateFilterDrift, runFilter);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");

			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			DATE_SEARCH_TERM = request.getParameter("sSearch_0");
			COL_NAME_SEARCH_TERM = request.getParameter("sSearch_1");
			UNIQUE_VALUES_SEARCH_TERM = request.getParameter("sSearch_2");

			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getDataDriftDetails(totalRecords, request, tableName, dateFilterDrift, runFilter, maxRun,
					idApp);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getDataDriftDetails(int totalRecords, HttpServletRequest request, String tableName,
			String dateFilterDrift, int runFilter, String maxRun, String idApp) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		System.out.println("maxRun: " + maxRun);
		String sql = "SELECT * from " + tableName + " where idApp=" + idApp + " and (" + dateFilterDrift + ")";

		if (maxRun != null && maxRun.equals("true")) {
			sql = "SELECT * from " + tableName + " where idApp=" + idApp + " and (Date=(SELECT MAX(Date) from "
					+ tableName + " where idApp=" + idApp + ")) and" + "(Run = (SELECT MAX(Run) from " + tableName
					+ " where idApp=" + idApp + " and (Date=(SELECT MAX(Date) from " + tableName + " where idApp="
					+ idApp + ")))) and (" + dateFilterDrift + ")";
		}

		// Query compatibility changes for both POSTGRES and MYSQL
		String globeSearch = "";
		String dateSearch = "";
		
		if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			globeSearch = " and (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or uniqueValues like '%" + GLOBAL_SEARCH_TERM + "%')";
			dateSearch = "Date::text like " + DATE_SEARCH_TERM + "";
		} else {
			globeSearch = " and (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or uniqueValues like '%" + GLOBAL_SEARCH_TERM + "%')";
			dateSearch = "Date like " + DATE_SEARCH_TERM + "";
		}
		String colSearch = "colName like " + COL_NAME_SEARCH_TERM + "";
		String uniqueValuesSearch = "uniqueValues like '%" + UNIQUE_VALUES_SEARCH_TERM + "%'";

		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = dateSearch;
		} else if (COL_NAME_SEARCH_TERM != "") {
			searchSQL = colSearch;
		} else if (UNIQUE_VALUES_SEARCH_TERM != "") {
			searchSQL = uniqueValuesSearch;
		}

		sql += searchSQL;
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		// sql += " order by " + COLUMN_NAME + ", dGroupVal " + DIRECTION;
		// Mamta 11-4-2022
		if (COLUMN_NAME.equals("Date")) {
			sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION + ", Run " + DIRECTION;
		} else {
			sql += " ORDER by " + COLUMN_NAME + " " + DIRECTION;
		}

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		String[] columnNames = { "Date", "Run", "colName", "uniqueValues", "PotentialDuplicates", "dGroupVal",
				"dGroupCol" };
		DecimalFormat numberFormat = new DecimalFormat("#0.00");
		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}

		System.out.println("JSONArray=" + array);

		String query = "SELECT COUNT(*) as count from " + tableName + " where idApp=" + idApp + "  and ("
				+ dateFilterDrift + ")";

		if (maxRun != null && maxRun.equals("true")) {
			query = "SELECT COUNT(*) as count  from " + tableName + " where idApp=" + idApp
					+ " and (Date=(SELECT MAX(Date) from " + tableName + " where idApp=" + idApp + ")) and"
					+ "(Run = (SELECT MAX(Run) from " + tableName + " where idApp=" + idApp
					+ " and (Date=(SELECT MAX(Date) from " + tableName + " where idApp=" + idApp + ")))) and ("
					+ dateFilterDrift + ")";
		}

		// + " WHERE ";

		/*
		 * if (runFilter != 0) { sql += " and Run = " + runFilter; }
		 */

		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || COL_NAME_SEARCH_TERM != ""
				|| UNIQUE_VALUES_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	@RequestMapping(value = "/DataDriftSummaryTable")
	public void DataDriftSummaryTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		System.out.println("DataDriftsummaryTable");
		String searchItem = GLOBAL_SEARCH_TERM;
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String[] columnNames = { "Date", "Run", "colName", "uniqueValues", "Operation", "dGroupVal", "dGroupCol" };
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String dateFilterDrift = "Date >= '" + (String) session.getAttribute("toDate") + "' and Date" + " <= '"
				+ (String) session.getAttribute("fromDate") + "'";
		runFilter = (int) session.getAttribute("RunFilter");
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String idApp = request.getParameter("idApp");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.contains("asc"))
					dir = "desc";
			}
			String colName = "Date";
			try {
				colName = columnNames[column];
			} catch (Exception E) {
				E.printStackTrace();
			}

			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountDrift(tableName, idApp, maxRun, dateFilterDrift, runFilter);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;

			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");

			DATE_SEARCH_TERM = request.getParameter("sSearch_0");
			COL_NAME_SEARCH_TERM = request.getParameter("sSearch_1");
			UNIQUE_VALUES_SEARCH_TERM = request.getParameter("sSearch_2");
			OPERATION_SEARCH_TERM = request.getParameter("sSearch_3");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getDataDriftSummaryDetails(totalRecords, request, tableName, session, idApp, dateFilterDrift,
					runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				// jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getDataDriftSummaryDetails(int totalRecords, HttpServletRequest request, String tableName,
			HttpSession session, String idApp, String dateFilterDrift, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";

		// String sql = "SELECT " + "id, name, place, city, state, "
		// + "phone " + "FROM " + "person " + "WHERE ";
		String sql = "";

		sql = "select Date,Run, colName, uniqueValues,Operation, dGroupVal, dGroupCol, status, userName, Time "
				+ "from " + tableName + " where idApp=" + idApp + " and (" + dateFilterDrift + ")";

		// Query compatibility changes for both POSTGRES and MYSQL
		String dateSearch = "";
		String globeSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			globeSearch = " and (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
				+ GLOBAL_SEARCH_TERM + "%'" + "or uniqueValues like '%" + GLOBAL_SEARCH_TERM + "%'"
				+ "or Operation like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%" + GLOBAL_SEARCH_TERM
				+ "%'" + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM + "%'" + "or status::text like '%" + GLOBAL_SEARCH_TERM
				+ "%'" + "or userName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Time::text like '%" + GLOBAL_SEARCH_TERM
				+ "%')";

			dateSearch = " Date::text like " + DATE_SEARCH_TERM + "";
		} else {
			globeSearch = " and (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or colName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or uniqueValues like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or Operation like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or dGroupCol like '%" + GLOBAL_SEARCH_TERM + "%'" + "or status like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or userName like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Time like '%" + GLOBAL_SEARCH_TERM
					+ "%')";
			
			dateSearch = " Date like " + DATE_SEARCH_TERM + "";
		}

		String colSearch = "colName like " + COL_NAME_SEARCH_TERM + "";
		String uniqueValuesSearch = "uniqueValues like '%" + UNIQUE_VALUES_SEARCH_TERM + "%'";
		String operationSearch = " Operation like '%" + OPERATION_SEARCH_TERM + "%'";
		
		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (DATE_SEARCH_TERM != "") {
			// searchSQL = dateSearch;
		} else if (COL_NAME_SEARCH_TERM != "") {
			searchSQL = colSearch;
		} else if (UNIQUE_VALUES_SEARCH_TERM != "") {
			searchSQL = uniqueValuesSearch;
		} else if (OPERATION_SEARCH_TERM != "") {
			searchSQL = operationSearch;
		}

		if (COLUMN_NAME.equals("Date")) {
			COLUMN_NAME = "Date, Run, dGroupVal";
		}

		sql += searchSQL;
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println("Amarxxxx" + sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		String[] columnNames = { "Date", "Run", "colName", "uniqueValues", "Operation", "dGroupVal", "dGroupCol",
				"status", "userName", "Time" };
		DecimalFormat numberFormat = new DecimalFormat("#0.00");

		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}
		System.out.println("JSONArray=" + array);

		String query = "SELECT " + "COUNT(*) as count " + "FROM " + tableName + " where idApp=" + idApp + " and " + "("
				+ dateFilterDrift + ")";
		// + " WHERE ";
		if (runFilter != 0) {
			sql += " and Run = " + runFilter;
		}
		// for pagination
		if (GLOBAL_SEARCH_TERM != "" || DATE_SEARCH_TERM != "" || COL_NAME_SEARCH_TERM != ""
				|| UNIQUE_VALUES_SEARCH_TERM != "" || OPERATION_SEARCH_TERM != "") {
			query += searchSQL;

			System.out.println(query);
			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}

		}
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	// --- AM ----//
	@RequestMapping(value = "/referenceTable")
	public void referenceTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		System.out.println("ColSummTableForNumericTab");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		// Long idData = Long.parseLong(request.getParameter("idData"));
		String ColumnName = request.getParameter("columnName");

		String[] columnNames = ColumnName.split("-");
		JSONObject jsonResult = new JSONObject();
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 30)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountOfReference(tableName);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}

			jsonResult = getColSummTableforReference(totalRecords, request, ColumnName, tableName, dateFilter,
					runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/getCustomMicrosegmentSummaryDetails")
	public void getCustomMicrosegmentSummaryDetails(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("getCustomMicrosegmentSummaryDetails");
		String idApp = request.getParameter("idApp");
		System.out.println("idApp=" + idApp);
		Long l_idApp = Long.parseLong(idApp);

		String[] columnNames = { "Date", "Run", "NumMeanStatus", "NumDqi", "ColName", "Count1", "Min", "Max", "Std_Dev",
				"Mean", "NumMeanAvg", "NumMeanStdDev", "NumMeanDeviation", "NumMeanThreshold", "NumSDAvg",
				"NumSDStdDev", "NumSDDeviation", "NumSDThreshold", "DGroupVal", "DGroupCol", "SumOfNumStat",
				"NumSumAvg", "NumSumStdDev", "NumSumThreshold", "NumSDStatus", "numsumstatus1" };
		JSONObject jsonResult = new JSONObject();
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		PrintWriter out = null;
		boolean booleanCheckFailedRecords = false;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 30)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}

			jsonResult = getColSummTableForNumericTabDetails(totalRecords, request, tableName, l_idApp, dateFilter,
					runFilter, searchValue, false);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/ColSummTableForNumericTab")
	public void ColSummTableForNumericTab(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		System.out.println("ColSummTableForNumericTab");
		// System.out.println("PHONE_SEARCH_TERM="+PHONE_SEARCH_TERM);
		String idApp = request.getParameter("idApp");
		System.out.println("idApp=" + idApp);
		Long l_idApp = Long.parseLong(idApp);
		/*
		 * String[] columnNames = { "Date", "Run", "DayOfYear", "Month", "DayOfMonth",
		 * "DayOfWeek", "HourOfDay", "NumMeanStatus", "NumSDStatus", "NumDqi",
		 * "OutOfNormStatStatus", "ColName", "Count", "Min", "Max", "Std_Dev", "Mean",
		 * "NumMeanAvg", "NumMeanStdDev", "NumMeanDeviation", "NumMeanThreshold",
		 * "NumSDAvg", "NumSDStdDev", "NumSDDeviation", "NumSDThreshold", "DGroupVal",
		 * "DGroupCol", "SumOfNumStat","NumSumAvg","NumSumStdDev","NumSumThreshold" };
		 */
		String[] columnNames = { "Date", "Run", /* "DayOfYear", "Month", "DayOfMonth", "DayOfWeek", "HourOfDay", */
				"NumMeanStatus", /* "NumSDStatus", */ "NumDqi", /* "OutOfNormStatStatus", */ "ColName", "Count1", "Min",
				"Max", "Std_Dev", "Mean", "NumMeanAvg", "NumMeanStdDev", "NumMeanDeviation", "NumMeanThreshold",
				"NumSDAvg", "NumSDStdDev", "NumSDDeviation", "NumSDThreshold", "DGroupVal", "DGroupCol", "SumOfNumStat",
				"NumSumAvg", "NumSumStdDev", "NumSumThreshold", "NumSDStatus", "numsumstatus1" };
		JSONObject jsonResult = new JSONObject();
		dateFilter = (String) session.getAttribute("dateFilter");
		runFilter = (int) session.getAttribute("RunFilter");
		PrintWriter out = null;
		boolean booleanCheckFailedRecords = false;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String maxRun = request.getParameter("maxRun");
			String searchValue = request.getParameter("searchValue");
			String checkFailedRecords = request.getParameter("checkFailedRecords");
			booleanCheckFailedRecords = Boolean.parseBoolean(checkFailedRecords);
			System.out.println("PaginationController : ColSummTableForNumericTab : booleanCheckFailedRecords :: "
					+ booleanCheckFailedRecords);
			System.out.println("tableName=" + tableName);
			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}
			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 30)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCount(tableName, idApp, maxRun, dateFilter, runFilter, searchValue,
						booleanCheckFailedRecords);

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}

			jsonResult = getColSummTableForNumericTabDetails(totalRecords, request, tableName, l_idApp, dateFilter,
					runFilter, searchValue, booleanCheckFailedRecords);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	private JSONObject getColSummTableForNumericTabDetails(int totalRecords, HttpServletRequest request,
			String tableName, Long idApp, String dateFilter, int runFilter, String filterSearch,
			boolean checkFailedRecords) {

		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		String globeSearch = "";
		String idSearch = "";
		String nameSearch = "";
		String placeSearch = "";
		String citySearch = "";
		String stateSearch = "";
		String phoneSearch = "";
		
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				sql = "SELECT Date," + "ROUND(Run, 0) as Run1,NumMeanStatus,NULL AS NumDqi,"
						+ "ColName,ROUND(Count, 0) as Count1,ROUND(CAST(Min AS numeric), 0) as Min1,ROUND(CAST(Max AS numeric), 0) as Max1,ROUND(CAST(Std_Dev AS numeric), 2) as Std_Dev1,ROUND(CAST(Mean AS numeric), 2) as Mean1,ROUND(CAST(NumMeanAvg AS numeric), 2) as NumMeanAvg1,ROUND(CAST(NumMeanStdDev AS numeric), 2) as NumMeanStdDev1,ROUND(CAST(NumMeanDeviation AS numeric), 2) as NumMeanDeviation1,NumMeanThreshold,ROUND(CAST(NumSDAvg AS numeric), 2) as NumSDAvg1,"
						+ "ROUND(CAST(NumSDStdDev AS numeric), 2) as NumSDStdDev1,ROUND(CAST(NumSDDeviation AS numeric), 2) as NumSDDeviation1,NumSDThreshold,DGroupVal,DGroupCol,COALESCE(ROUND(CAST(SumOfNumStat AS numeric), 2),0) as SumOfNumStat1, COALESCE(ROUND(CAST(NumSumAvg AS numeric), 2),0)  AS NumSumAvg1,ROUND(CAST(NumSumStdDev AS numeric), 2) AS NumSumStdDev1,NumSumThreshold,NumSDStatus, CASE WHEN (RUN > 2 or NumSDStatus IS NOT null) THEN CASE WHEN((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numsumstatus1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and NumMeanThreshold IS NOT NULL and (" + dateFilter
						+ ")";
			} else {
				sql = "SELECT Date, ROUND(Run, 0) as Run1, NumMeanStatus,("
						+ "CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi, "
						+ " ColName, ROUND(Count, 0) as Count1, ROUND(CAST(Min AS numeric), 0) as Min1, ROUND(CAST(Max AS numeric), 0) as Max1, ROUND(CAST(Std_Dev AS numeric), 2) as Std_Dev1, ROUND(CAST(Mean AS numeric), 2) as Mean1, ROUND(CAST(NumMeanAvg AS numeric), 2) as NumMeanAvg1, ROUND(CAST(NumMeanStdDev AS numeric), 2) as NumMeanStdDev1, ROUND(CAST(NumMeanDeviation AS numeric), 2) as NumMeanDeviation1, NumMeanThreshold, "
						+ "ROUND(CAST(NumSDAvg AS numeric), 2) as NumSDAvg1, ROUND(CAST(NumSDStdDev AS numeric), 2) as NumSDStdDev1, ROUND(CAST(NumSDDeviation AS numeric), 2) as NumSDDeviation1, NumSDThreshold, DGroupVal, DGroupCol, ROUND(CAST(SumOfNumStat AS numeric), 2) as SumOfNumStat1,ROUND(CAST(NumSumAvg AS numeric), 2) AS NumSumAvg1,ROUND(CAST(NumSumStdDev AS numeric), 2) AS NumSumStdDev1,NumSumThreshold, NumSDStatus, CASE WHEN (RUN > 2 or NumSDStatus IS NOT null) THEN CASE WHEN((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev)  <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numsumstatus1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and NumMeanThreshold IS NOT NULL and (" + dateFilter
						+ ")";
			}
			
			globeSearch = " AND (Date::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or DayOfYear::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Month::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or DayOfMonth::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or DayOfWeek::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or HourOfDay::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumMeanStatus like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumSDStatus like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or OutOfNormStatStatus like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Count::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Min::text like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Max::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Std_Dev::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Mean::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumMeanAvg::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumMeanStdDev::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumMeanDeviation::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumMeanThreshold::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumSDAvg::text like  '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumSDStdDev::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumSDDeviation::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumSDThreshold::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or DGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'" + "or DGroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or SumOfNumStat::text like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id::text like " + ID_SEARCH_TERM + "";
			nameSearch = "Date::text like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run::text like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg::text like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}
		else {
			if (listApplicationsData.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
				sql = "SELECT Date," + "FORMAT(Run, 0) as Run1,NumMeanStatus,NULL AS NumDqi,"
						+ "ColName,FORMAT(Count, 0) as Count1,FORMAT(Min, 0) as Min1,FORMAT(Max, 0) as Max1,FORMAT(Std_Dev, 2) as Std_Dev1,FORMAT(Mean, 2) as Mean1,FORMAT(NumMeanAvg, 2) as NumMeanAvg1,FORMAT(NumMeanStdDev, 2) as NumMeanStdDev1,FORMAT(NumMeanDeviation, 2) as NumMeanDeviation1,NumMeanThreshold,FORMAT(NumSDAvg, 2) as NumSDAvg1,"
						+ "FORMAT(NumSDStdDev, 2) as NumSDStdDev1,FORMAT(NumSDDeviation, 2) as NumSDDeviation1,NumSDThreshold,DGroupVal,DGroupCol,IFNULL(FORMAT(SumOfNumStat, 2),0) as SumOfNumStat1, IFNULL(FORMAT(NumSumAvg, 2),0)  AS NumSumAvg1,FORMAT(NumSumStdDev, 2) AS NumSumStdDev1,NumSumThreshold,NumSDStatus, CASE WHEN (RUN > 2 or !(NumSDStatus IS null)) THEN IF((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold,'passed','failed') ELSE '' END AS numsumstatus1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and NumMeanThreshold IS NOT NULL and (" + dateFilter
						+ ")";
			} else {
				sql = "SELECT Date, FORMAT(Run, 0) as Run1, NumMeanStatus,("
						+ "CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi, "
						+ " ColName, FORMAT(Count, 0) as Count1, FORMAT(Min, 0) as Min1, FORMAT(Max, 0) as Max1, FORMAT(Std_Dev, 2) as Std_Dev1, FORMAT(Mean, 2) as Mean1, FORMAT(NumMeanAvg, 2) as NumMeanAvg1, FORMAT(NumMeanStdDev, 2) as NumMeanStdDev1, FORMAT(NumMeanDeviation, 2) as NumMeanDeviation1, NumMeanThreshold, "
						+ "FORMAT(NumSDAvg, 2) as NumSDAvg1, FORMAT(NumSDStdDev, 2) as NumSDStdDev1, FORMAT(NumSDDeviation, 2) as NumSDDeviation1, NumSDThreshold, DGroupVal, DGroupCol, FORMAT(SumOfNumStat, 2) as SumOfNumStat1,FORMAT(NumSumAvg, 2) AS NumSumAvg1,FORMAT(NumSumStdDev, 2) AS NumSumStdDev1,NumSumThreshold, NumSDStatus, CASE WHEN (RUN > 2 or !(NumSDStatus IS null)) THEN IF((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev)  <= NumSumThreshold,'passed','failed') ELSE '' END AS numsumstatus1 FROM "
						+ tableName + " WHERE idApp=" + idApp + " and NumMeanThreshold IS NOT NULL and (" + dateFilter
						+ ")";
			}
			
			globeSearch = " AND (Date like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Run like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or DayOfYear like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Month like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or DayOfMonth like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or DayOfWeek like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or HourOfDay like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumMeanStatus like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumSDStatus like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or OutOfNormStatStatus like '%" + GLOBAL_SEARCH_TERM + "%'" + "or ColName like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Count like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Min like  '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Max like '%" + GLOBAL_SEARCH_TERM + "%'" + "or Std_Dev like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or Mean like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumMeanAvg like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumMeanStdDev like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumMeanDeviation like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumMeanThreshold like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumSDAvg like  '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or NumSDStdDev like '%" + GLOBAL_SEARCH_TERM + "%'" + "or NumSDDeviation like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or NumSDThreshold like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or DGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'" + "or DGroupCol like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or SumOfNumStat like '%" + GLOBAL_SEARCH_TERM + "%')";

			idSearch = "Id like " + ID_SEARCH_TERM + "";
			nameSearch = "Date like '%" + DATE_SEARCH_TERM + "%'";
			placeSearch = " Run like '%" + RUN_SEARCH_TERM + "%'";
			citySearch = " RC_Std_Dev_Status like '%" + RC_Std_Dev_Status_SEARCH_TERM + "%'";
			stateSearch = " RC_Mean_Moving_Avg_Status like '%" + RC_Mean_Moving_Avg_Status_SEARCH_TERM + "%'";
			phoneSearch = " RC_Mean_Moving_Avg like '%" + RC_Mean_Moving_Avg_SEARCH_TERM + "%'";
		}
		
		if (filterSearch != null && !filterSearch.isEmpty() && !filterSearch.equals("pageload")
				&& (filterSearch.matches(".*[a-zA-Z]+.*") || filterSearch.matches(".*\\d.*"))) {
			filterSearch = filterSearch.replace("_", "%");
			sql += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		if (checkFailedRecords) {
			sql += " AND ( NumMeanStatus = 'failed' OR NumSDStatus = 'failed' OR (ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) > NumSumThreshold )"; // (SumOfNumStat-NumSumAvg)
																																					// >
		}

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		JSONObject jsonObjectFunctionParams = new JSONObject();
		String searchSQL = "";

		if (GLOBAL_SEARCH_TERM != "") {
			searchSQL = globeSearch;
		} else if (ID_SEARCH_TERM != "") {
			searchSQL = idSearch;
		} else if (DATE_SEARCH_TERM != "") {
			searchSQL = nameSearch;
		} else if (RUN_SEARCH_TERM != "") {
			searchSQL = placeSearch;
		} else if (RC_Std_Dev_Status_SEARCH_TERM != "") {
			searchSQL = citySearch;
		} else if (RC_Mean_Moving_Avg_Status_SEARCH_TERM != "") {
			searchSQL = stateSearch;
		} else if (RC_Mean_Moving_Avg_SEARCH_TERM != "") {
			searchSQL = phoneSearch;
			// System.out.println(searchSQL);
		}

		sql += searchSQL;
		if (runFilter != 0) {
			sql += " and Run=" + runFilter;
		}
		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		DecimalFormat numberFormat = new DecimalFormat("#0.00");
		String[] columnNames = { "Date", "Run1", /* "DayOfYear", "Month", "DayOfMonth", "DayOfWeek", "HourOfDay", */
				"NumMeanStatus", /* "NumSDStatus", */ "NumDqi", /* "OutOfNormStatStatus", */ "ColName", "Count1",
				"Min1", "Max1", "Std_Dev1", "Mean1", "NumMeanAvg1", "NumMeanStdDev1", "NumMeanDeviation1",
				"NumMeanThreshold", "NumSDAvg1", "NumSDStdDev1", "NumSDDeviation1", "NumSDThreshold", "DGroupVal",
				"DGroupCol", "SumOfNumStat1", "NumSumAvg1", "NumSumStdDev1", "NumSumThreshold", "NumSDStatus",
				"numsumstatus1" };
		while (rs.next()) {

			int colIndex = 1;
			JSONObject rowArray = new JSONObject();

			// Read Row Data
			for (String columnName : columnNames) {
				String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

				// Read column value
				String columnValue = "";
				if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
						|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

					columnValue = numberFormat.format(rs.getDouble(columnName));
				} else {
					columnValue = rs.getString(columnName);
				}

				if (columnValue == null)
					columnValue = "";

				rowArray.put(columnName, columnValue);
				++colIndex;
			}
			dataArray.put(rowArray);

		}

		System.out.println("JSONArray=" + array);

		String query = "SELECT COUNT(*) as count FROM " + tableName + " WHERE idApp=" + idApp
				+ " and NumMeanThreshold IS NOT NULL and (" + dateFilter + ")";
		// + " WHERE ";
		if (runFilter != 0) {
			query += " and Run=" + runFilter;
		}

		if (filterSearch != null && !filterSearch.isEmpty() && !filterSearch.equals("pageload")
				&& (filterSearch.matches(".*[a-zA-Z]+.*") || filterSearch.matches(".*\\d.*"))) {
			filterSearch = filterSearch.replace("_", "%");
			query += " and dGroupVal LIKE  '" + filterSearch + "'";
		}

		if (checkFailedRecords) {
			query += " AND ( NumMeanStatus = 'failed' OR NumSDStatus = 'failed' OR  (ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) > NumSumThreshold )"; // (SumOfNumStat-NumSumAvg)
																																						// >
																																						// (NumSumThreshold*NumSumStdDev)
																																						// ..
																																						// NumSumStatus
																																						// =
																																						// 'failed'
		}

		query += searchSQL;

		System.out.println(query);
		SqlRowSet results = jdbcTemplate1.queryForRowSet(query);

		if (results.next()) {
			totalAfterSearch = results.getInt("count");
		}

		/* } */
		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	// -- Pradeep 29-11-2019 SqlRules ---

	@RequestMapping(value = "/SqlResultsDataTable")
	public void SqlResultsDataTable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String dateFilter = (String) session.getAttribute("dateFilter");
		dateFilter = dateFilter.toLowerCase();
		runFilter = (int) session.getAttribute("RunFilter");

		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);

			DateUtility.DebugLog("SqlResults Tab Debug 01", String.format("%1$s,%2$s", dateFilter, idApp));

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "rulename", "date", "run", "status", "total_failed_records", "ruleThreshold",
					"totalRecords" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountSqlRules(idApp, dateFilter, runFilter);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getSqlRulesTableDetails(totalRecords, request, idApp, columnNames, dateFilter, runFilter);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	public int getTotalRecordCountSqlRules(String nIdApp, String sDateFilter, int nRunFilter) {
		int nTotalRecords = -1;
		String sCountQry = "";
		SqlRowSet resultSet = null;

		sCountQry = String.format("select count(*) as count from data_quality_sql_rules where idapp = %1$s and (%2$s)",
				nIdApp, sDateFilter);
		sCountQry = sCountQry + ((nRunFilter != 0) ? String.format(" and Run = %1$s", nRunFilter) : "");

		resultSet = jdbcTemplate1.queryForRowSet(sCountQry);
		nTotalRecords = (resultSet.next()) ? resultSet.getInt("count") : nTotalRecords;

		DateUtility.DebugLog("SqlResults Tab Debug 02",
				String.format("No of times SQL rules exceuted for date range is %1$s", nTotalRecords));

		return nTotalRecords;
	}

	private JSONObject getSqlRulesTableDetails(int totalRecords, HttpServletRequest request, String idApp,
			String[] columnNames, String dateFilter, int runFilter) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";
		String[] aStatus = new String[] { "<span class=\"label label-danger label-sm\">failed</span>",
				"<span class=\"label label-success label-sm\">passed</span>" };

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			String globeSearch = "";
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select rulename,to_date(date::TEXT, 'YYYY-MM-DD') as date, run, status, total_failed_records,ruleThreshold,totalRecords"
						+ " from data_quality_sql_rules" + " where idapp = '" + idApp + "'" + " and ( " + dateFilter
						+ " ) ";
				
				globeSearch = " and (rulename like '%" + GLOBAL_SEARCH_TERM + "%'" + "or date::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or run::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or status::text like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + " or total_failed_records::text like '%" + GLOBAL_SEARCH_TERM + "%')";
				
			} else {
				sql = "select rulename, date_format(date, '%Y-%m-%d') as date, run, status, total_failed_records,ruleThreshold,totalRecords"
						+ " from data_quality_sql_rules" + " where idapp = '" + idApp + "'" + " and ( " + dateFilter
						+ " ) ";
				
				globeSearch = " and (rulename like '%" + GLOBAL_SEARCH_TERM + "%'" + "or date like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or run like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or status like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + " or total_failed_records like '%" + GLOBAL_SEARCH_TERM + "%')";
			}

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			DateUtility.DebugLog("SqlResults Tab Debug 03", sql);

			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}
			String query = "SELECT COUNT(*) as count from data_quality_sql_rules where" + " idapp = " + idApp + " and ("
					+ dateFilter + ")";

			query += searchSQL;

			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			DateUtility.DebugLog("SqlResults Tab Debug 04", query);

			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		DateUtility.DebugLog("SqlResults Tab Debug 05",
				String.format("totalAfterSearch=%1$s \nJSONArray=%2$s", totalAfterSearch, array));

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	public static String getDataBuckHome() {
		String sRetValue = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null) {
			sRetValue = System.getenv("DATABUCK_HOME");
		} else if (System.getProperty("DATABUCK_HOME") != null) {
			sRetValue = System.getProperty("DATABUCK_HOME");
		}

		sRetValue = sRetValue.replace('\\', '/');
		return sRetValue;
	}
	// -- Pradeep 29-11-2019 SqlRules ---

	@RequestMapping(value = "/GlobalRuleResultsDataTable")
	public void GlobalRuleResultsDataTable(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String dateFilter = (String) session.getAttribute("dateFilter");
		dateFilter = dateFilter.toLowerCase();
		runFilter = (int) session.getAttribute("RunFilter");

		try {

			int listDisplayAmount = 10;
			int start = 0;
			int column = 0;
			String dir = "asc";
			String pageNo = request.getParameter("iDisplayStart");
			String pageSize = request.getParameter("iDisplayLength");
			String colIndex = request.getParameter("iSortCol_0");
			String sortDirection = request.getParameter("sSortDir_0");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			String maxRun = request.getParameter("maxRun");
			System.out.println("tableName=" + tableName);

			if (pageNo != null) {
				start = Integer.parseInt(pageNo);
				if (start < 0) {
					start = 0;
				}
			}

			String[] columnNames = { "Id", "Date", "Run", "ruleName", "totalRecords", "totalFailed", "rulePercentage",
					"ruleThreshold", "dGroupCol", "dGroupVal", "dimension_name", "status", "forgot_run_enabled" };

			if (pageSize != null) {
				listDisplayAmount = Integer.parseInt(pageSize);
				if (listDisplayAmount < 10 || listDisplayAmount > 50) {
					listDisplayAmount = 10;
				}
			}
			if (colIndex != null) {
				column = Integer.parseInt(colIndex);
				if (column < 0 || column > 20)
					column = 0;
			}
			if (sortDirection != null) {
				if (!sortDirection.equals("asc"))
					dir = "desc";
			}

			String colName = columnNames[column];
			int totalRecords = -1;
			try {
				totalRecords = getTotalRecordCountGlobalRules(idApp, dateFilter, runFilter, tableName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			RECORD_SIZE = listDisplayAmount;
			GLOBAL_SEARCH_TERM = request.getParameter("sSearch");
			ID_SEARCH_TERM = request.getParameter("sSearch_0");
			DATE_SEARCH_TERM = request.getParameter("sSearch_1");
			RUN_SEARCH_TERM = request.getParameter("sSearch_2");
			RC_Std_Dev_Status_SEARCH_TERM = request.getParameter("sSearch_3");
			RC_Mean_Moving_Avg_Status_SEARCH_TERM = request.getParameter("sSearch_4");
			RC_Mean_Moving_Avg_SEARCH_TERM = request.getParameter("sSearch_5");
			COLUMN_NAME = colName;
			DIRECTION = dir;
			INITIAL = start;

			if (GLOBAL_SEARCH_TERM.contains(",")) {
				GLOBAL_SEARCH_TERM = GLOBAL_SEARCH_TERM.replaceAll(",", "");
			}
			jsonResult = getGlobalRulesTableDetails(totalRecords, request, idApp, columnNames, dateFilter, runFilter,
					tableName);

			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(jsonResult);
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			try {
				jsonResult.put("iTotalRecords", 0);
				jsonResult.put("iTotalDisplayRecords", 0);
				jsonResult.put("aaData", array);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			out.print(jsonResult);
			e.printStackTrace();
		}

	}

	public int getTotalRecordCountGlobalRules(String nIdApp, String sDateFilter, int nRunFilter, String nTableName) {
		int nTotalRecords = -1;
		String sCountQry = "";
		SqlRowSet resultSet = null;
		String sqlqry = "select count(*) as count from " + nTableName + " where idApp = " + nIdApp + " and ("
				+ sDateFilter + ")";
		sCountQry = String.format(sqlqry, nIdApp);
		sCountQry = sCountQry + ((nRunFilter != 0) ? String.format(" and Run = %1$s", nRunFilter) : "");

		resultSet = jdbcTemplate1.queryForRowSet(sCountQry);
		nTotalRecords = (resultSet.next()) ? resultSet.getInt("count") : nTotalRecords;

		return nTotalRecords;
	}

	private JSONObject getGlobalRulesTableDetails(int totalRecords, HttpServletRequest request, String idApp,
			String[] columnNames, String dateFilter, int runFilter, String tableName) {

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		JSONArray dataArray = new JSONArray();
		String searchSQL = "";
		String colNamesQuery = "";

		int pos = 0;
		for (String col : columnNames) {
			if (col != null && !col.trim().isEmpty()) {
				colNamesQuery = (pos == 0) ? col : colNamesQuery + "," + col;
				++pos;
			}
		}

		try {
			String sql = "select " + colNamesQuery + " from " + tableName + " where idApp = " + idApp + " and ("
					+ dateFilter + ")";

			// Query compatibility changes for both POSTGRES and MYSQL
			String globeSearch = "";
			
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				globeSearch = " and (id::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or date::text like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or run::text like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ruleName like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + " or totalRecords::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or totalFailed::text like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or rulePercentage::text like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ "or ruleThreshold::text like '%" + GLOBAL_SEARCH_TERM + "%'" + "or status like '%" + GLOBAL_SEARCH_TERM
					+ "%'" + "or forgot_run_enabled like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupCol like '%"
					+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'"
					+ " or dimension_name like '%" + GLOBAL_SEARCH_TERM + "%')";
				
			} else {
				globeSearch = " and (id like '%" + GLOBAL_SEARCH_TERM + "%'" + "or date like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or run like  '%" + GLOBAL_SEARCH_TERM + "%'" + "or ruleName like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + " or totalRecords like '%" + GLOBAL_SEARCH_TERM + "%'" + "or totalFailed like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or rulePercentage like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ "or ruleThreshold like '%" + GLOBAL_SEARCH_TERM + "%'" + "or status like '%" + GLOBAL_SEARCH_TERM
						+ "%'" + "or forgot_run_enabled like '%" + GLOBAL_SEARCH_TERM + "%'" + "or dGroupCol like '%"
						+ GLOBAL_SEARCH_TERM + "%'" + "or dGroupVal like '%" + GLOBAL_SEARCH_TERM + "%'"
						+ " or dimension_name like '%" + GLOBAL_SEARCH_TERM + "%')";
			}

			if (GLOBAL_SEARCH_TERM != "") {
				searchSQL = globeSearch;
			}

			sql += searchSQL;
			if (runFilter != 0) {
				sql += " and Run = " + runFilter;
			}

			sql += " order by " + COLUMN_NAME + " " + DIRECTION;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
			else
				sql += " limit " + INITIAL + ", " + RECORD_SIZE;

			System.out.println("sql: " + sql);
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			while (rs.next()) {

				int colIndex = 1;
				JSONObject rowArray = new JSONObject();

				// Read Row Data
				for (String columnName : columnNames) {
					String columnDataType = rs.getMetaData().getColumnTypeName(colIndex);

					// Read column value
					String columnValue = "";
					if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
							|| columnDataType.equalsIgnoreCase("float") || columnDataType.equalsIgnoreCase("float8")) {

						columnValue = numberFormat.format(rs.getDouble(columnName));
					} else {
						columnValue = rs.getString(columnName);
					}

					if (columnValue == null)
						columnValue = "";

					rowArray.put(columnName, columnValue);
					++colIndex;
				}
				dataArray.put(rowArray);

			}

			String query = "SELECT COUNT(*) as count from " + tableName + "  where idApp = " + idApp + " and ("
					+ dateFilter + ")";

			query += searchSQL;

			if (runFilter != 0) {
				query += " and Run = " + runFilter;
			}

			SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
			if (results.next()) {
				totalAfterSearch = results.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			result.put("iTotalRecords", totalAfterSearch);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", dataArray);
		} catch (Exception e) {

		}

		return result;
	}

	private JSONObject getColSummTableforReference(int totalRecords, HttpServletRequest request, String ColumnName,
			String tableName, String dateFilter, int runFilter) {

		// ListApplications listApplicationsData =
		// validationcheckdao.getdatafromlistapplications(idApp);
		String[] columnNames = ColumnName.split("-");

		String searchSql = "";
		for (String columnName : columnNames) {
			searchSql = searchSql + columnName + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
		}

		if (searchSql.length() > 0)
			searchSql = " WHERE " + searchSql.substring(0, searchSql.length() - 3);

		String sql = "SELECT * FROM " + tableName + searchSql;

		sql += " order by " + COLUMN_NAME + " " + DIRECTION;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
			sql += " OFFSET " + INITIAL + " LIMIT " + RECORD_SIZE;
		else
			sql += " limit " + INITIAL + ", " + RECORD_SIZE;

		int totalAfterSearch = totalRecords;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		String searchSQL = "";

		System.out.println(sql);
		// for searching
		SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
		// DecimalFormat numberFormat = new DecimalFormat("#.00");
		while (rs.next()) {
			int colIndex = 0;
			JSONArray ja = new JSONArray();
			for (String colName : columnNames) {

				// Group_DQI

				try {
					ja.put(rs.getString(columnNames[colIndex]));
					colIndex++;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			array.put(ja);
		}

		String query = "SELECT COUNT(*) as count from " + tableName + searchSql;

		SqlRowSet results = jdbcTemplate1.queryForRowSet(query);
		if (results.next()) {
			totalAfterSearch = results.getInt("count");
		}

		try {
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", totalAfterSearch);
			result.put("aaData", array);
		} catch (Exception e) {

		}

		return result;
	}

	@RequestMapping(value = "/dGroupValGoogleChart", method = RequestMethod.POST)
	public void dGroupValGoogleChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : dGroupValGoogleChart : ...");
			String tableName = request.getParameter("tableName");
			String strdGroupVal = request.getParameter("dGroupVal");
			List<List<Object>> list = new ArrayList<List<Object>>();
			if ((tableName != null && !tableName.isEmpty()) && (strdGroupVal != null && !strdGroupVal.isEmpty())) {
				jsonObject = getDgroupValLineChartDetails(tableName, strdGroupVal);
			} else {
				System.err.println(
						"PaginationController : dGroupValGoogleChart : table name or dGroupVal is null or empty");
			}
			// JSONObject jsonResult = new JSONObject(jsonArray);
			System.out.println("PaginationController : dGroupValGoogleChart : json result :: " + jsonObject);
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param tableName
	 *            : this table will come from View side
	 * @return JSON Object which contains google chart's header and chart array
	 * @throws SQLException
	 *             : to handle query related errors
	 */
	public JSONObject getDgroupValLineChartDetails(String tableName, String paramStrdGroupVal) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : getDgroupValLineChartDetails : tableName :: " + tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			String strDateSqlQuery = "SELECT date FROM " + tableName + " where dGroupVal = '" + paramStrdGroupVal
					+ "' GROUP BY date ORDER BY date";
			System.out.println("PaginationController : getLineChartDetails : strDateSqlQuery :: " + strDateSqlQuery);
			SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strDateSqlQuery);
			List<Date> listTableDates = new ArrayList<Date>();
			while (sqlRowSet_strDateSqlQuery.next()) {
				listTableDates.add(sqlRowSet_strDateSqlQuery.getDate("date"));
			}

			List<String> listTable_dGroupVal = new ArrayList<String>();

			listTable_dGroupVal.add(paramStrdGroupVal);
			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);

			for (Date localdate : listTableDates) {
				List<Object> listOfChartElements = new ArrayList<Object>();
				listOfChartElements.add(localdate);
				for (String localstring : listTable_dGroupVal) {

					String strSqlQueryGetChartList = "SELECT RecordCount FROM " + tableName + " WHERE date = '"
							+ localdate + "' AND dGroupVal = '" + localstring
							+ "' AND RUN IN (SELECT MAX(RUN) AS RUN FROM " + tableName + " WHERE DATE = '" + localdate
							+ "' AND dGroupVal = '" + localstring + "')";
					SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(strSqlQueryGetChartList);
					while (sqlRowSet_strSqlQueryGetChartList.next()) {
						listOfChartElements.add(sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
					}
				}
				listRowChartData.add(listOfChartElements);
			}
			JSONArray jsonArrayChartList = new JSONArray(listRowChartData);
			jsonObject.put("header", jsonArrayDGroupVal);
			jsonObject.put("chart", jsonArrayChartList);

		} catch (Exception e) {
			System.err.println("PaginationController : getLineChartDetails : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * Last Updated : 06thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName
	 *            : table name ( column summary table)
	 * @return : list of distinct run from table
	 */
	private List<Integer> getListOfRunFromColumnSummary(String paramStrTableName, String idApp) {
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
			System.out.println("Pagination Controller : getListOfRunFromColumnSummary : listRun :: " + listRun);
			return listRun;
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : getListOfRunFromColumnSummary : Exception :: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 06thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName
	 *            : table name ( column summary)
	 * @return : list of distinct date from given table
	 */
	private List<Date> getListOfDateFromColumnSummary(String paramStrTableName, String idApp) {

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
			System.err.println("DataQualityResultsController : getListOfDateFromColumnSummary : Exception :: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Last Updated : 06thMay,2020 Code By: Anant S. Mahale
	 * 
	 * @param paramStrTableName
	 *            : table name
	 * @param paramDate
	 *            : date
	 * @param paramIntRun
	 *            : Run
	 * @return : check record exists or not with given date, run and table
	 */
	private boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDate(String paramStrTableName, String idApp,
			Date paramDate, int paramIntRun) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE DATE = '" + paramDate
					+ "' AND RUN = " + paramIntRun;

			if (paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strQuery = strQuery + " and idApp=" + idApp;
			}

			System.out.println(
					"DataQualityResultsController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : strQuery "
							+ strQuery);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			System.out
					.println("PaginationController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : intRowCount "
							+ intRowCount);
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			System.err.println(
					"PaginationController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : Exception :: " + e);
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/transactionsetSumTrendChart", method = RequestMethod.POST)
	public void transactionsetSumTrendChart(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : transactionsetSumTrendChart : ...");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			if ((tableName != null && !tableName.isEmpty())) {
				jsonObject = getLineChartDetails1(tableName, idApp);
			} else {
				System.err.println("PaginationController : transactionsetSumTrendChart : table name is null or empty");
			}
			// JSONObject jsonResult = new JSONObject(jsonArray);
			System.out.println("PaginationController : transactionsetSumTrendChart : json result :: " + jsonObject);
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/countReasionabilitySumDgroupCluster", method = RequestMethod.POST)
	public void countReasionabilitySumDgroupCluster(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : transactionsetSumTrendChart : ...");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			if ((tableName != null && !tableName.isEmpty())) {
				jsonObject = getcountReasionabilitySumDgroupClusterMapJsonObject(tableName, idApp);
			} else {
				System.err.println("PaginationController : transactionsetSumTrendChart : table name is null or empty");
			}
			// JSONObject jsonResult = new JSONObject(jsonArray);
			System.out.println("PaginationController : transactionsetSumTrendChart : json result :: " + jsonObject);
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param tableName
	 *            : this table will come from View side
	 * @return JSON Object which contains google chart's header and chart array
	 * @throws SQLException
	 *             : to handle query related errors
	 */
	public JSONObject getcountReasionabilitySumDgroupClusterMapJsonObject(String tableName, String idApp)
			throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : getLineChartDetails : tableName :: " + tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummary(tableName, idApp);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummary(tableName, idApp);

			String str_dGroupVal_SqlQuery = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp;
			System.out.println(
					"PaginationController : getLineChartDetails : str_dGroupVal_SqlQuery :: " + str_dGroupVal_SqlQuery);
			SqlRowSet sqlRowSet_str_dGroupVal_SqlQuery = jdbcTemplate1.queryForRowSet(str_dGroupVal_SqlQuery);
			List<String> listTable_dGroupVal = new ArrayList<String>();
			while (sqlRowSet_str_dGroupVal_SqlQuery.next()) {
				listTable_dGroupVal.add(sqlRowSet_str_dGroupVal_SqlQuery.getString("dGroupVal"));
			}
			Set<String> set = new HashSet<>(listTable_dGroupVal);
			listTable_dGroupVal.clear();
			listTable_dGroupVal.addAll(set);
			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);
			System.out.println(
					"PaginationController : getLineChartDetails : jsonArrayDGroupVal :: " + jsonArrayDGroupVal);
			List<Object> listOfChartElements = new ArrayList<Object>();

			for (String localstring : listTable_dGroupVal) {
				String strSqlQueryGetChartList = "SELECT MAX(RecordCount) RecordCount FROM " + tableName
						+ " WHERE idApp=" + idApp + " and dGroupVal = '" + localstring
						+ "' group by dGroupVal order by dGroupVal ";
				SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(strSqlQueryGetChartList);
				while (sqlRowSet_strSqlQueryGetChartList.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("Name", localstring);
					// map.put("title", localstring+" "+localdate+" "+integer);
					// map.put("group", localstring);
					map.put("Count", sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
					listOfChartElements.add(map);
				}
			}

			JSONArray jsonArrayChartList = new JSONArray(listOfChartElements);
			System.out.println("listRowChartData : jsonArrayChartList :: " + jsonArrayChartList);

			jsonObject.put("children", jsonArrayChartList);

		} catch (Exception e) {
			System.err.println("PaginationController : getLineChartDetails : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	public JSONObject getLineChartDetails1(String tableName, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("PaginationController : getLineChartDetails1 : tableName :: " + tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummary(tableName, idApp);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummary(tableName, idApp);

			String str_dGroupVal_SqlQuery = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp;
			System.out.println("PaginationController : getLineChartDetails1 : str_dGroupVal_SqlQuery :: "
					+ str_dGroupVal_SqlQuery);
			SqlRowSet sqlRowSet_str_dGroupVal_SqlQuery = jdbcTemplate1.queryForRowSet(str_dGroupVal_SqlQuery);
			List<String> listTable_dGroupVal = new ArrayList<String>();
			while (sqlRowSet_str_dGroupVal_SqlQuery.next()) {
				listTable_dGroupVal.add(sqlRowSet_str_dGroupVal_SqlQuery.getString("dGroupVal"));
			}
			Set<String> set = new HashSet<>(listTable_dGroupVal);
			listTable_dGroupVal.clear();
			listTable_dGroupVal.addAll(set);
			JSONArray jsonArrayDGroupVal = new JSONArray(listRun);
			System.out.println(
					"PaginationController : getLineChartDetails1 : jsonArrayDGroupVal :: " + jsonArrayDGroupVal);
			for (Date localdate : listDate) {

				for (Integer integer : listRun) {
					List<Object> listOfChartElements = new ArrayList<Object>();
					if (integer < 10) {
						listOfChartElements.add(localdate + "(0" + integer + ")");
					} else {
						listOfChartElements.add(localdate + "(" + integer + ")");
					}
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, idApp, localdate, integer)) {

						String strSqlQueryGetChartList = "SELECT RecordCount FROM " + tableName + " WHERE idApp="
								+ idApp + " and date = '" + localdate + "' AND RUN = " + integer;
						SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1
								.queryForRowSet(strSqlQueryGetChartList);
						while (sqlRowSet_strSqlQueryGetChartList.next()) {
							listOfChartElements.add(sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
						}
						listRowChartData.add(listOfChartElements);
					} else {
						listOfChartElements.add(0);
					}
				}

			}
			JSONArray jsonArrayChartList = new JSONArray(listRowChartData);
			jsonObject.put("header", jsonArrayDGroupVal);
			jsonObject.put("chart", jsonArrayChartList);

		} catch (Exception e) {
			System.err.println("PaginationController : getLineChartDetails1 : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

}
