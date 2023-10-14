package com.databuck.restcontroller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDmCriteria;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.bean.RollDataMatchingDashboard;
import com.databuck.dao.IFileManagementDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IMatchingResultService;
import com.databuck.service.PrimaryKeyMatchingResultService;
import com.databuck.util.ToCamelCase;
import com.databuck.util.TokenValidator;

@CrossOrigin(origins = "*")
@RestController
public class MatchingResultRestController {
    @Autowired
    public IMatchingResultService iMatchingResultService;

    @Autowired
    MatchingResultDao matchingresultdao;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplate jdbcTemplate1;

    @Autowired
    IValidationCheckDAO validationcheckdao;

    @Autowired
    IValidationDAO validationDao;

    @Autowired
    private Properties appDbConnectionProperties;

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;

    @Autowired
    PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

    @Autowired
    public IFileManagementDAO IFileManagementDAOObject;

    @Autowired
    private TokenValidator tokenValidator;

    @Autowired
    private IListDataSourceDAO listdatasourcedao;

    private static final Logger LOG = Logger.getLogger(MatchingResultRestController.class);

    @RequestMapping(value = "/dbconsole/getKeyMeasurementMatchingResults", method = RequestMethod.POST)
    public ResponseEntity<Object> getMatchingTablesResultTables(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/getKeyMeasurementMatchingResults - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    // Check whether all required parameters are available.
	    if (!requestBody.containsKey("domainId") || !requestBody.containsKey("projectId")
		    || !requestBody.containsKey("fromDate") || !requestBody.containsKey("toDate")) {
		throw new Exception("Required parameters not found.");
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		LOG.debug("Getting request parameters  " + requestBody);
		List<KeyMeasurementMatchingDashboard> dashboardTable = matchingresultdao
			.getKeyMeasurementMatchingDashboardByProjectNDateFilter(requestBody.get("domainId"),
				requestBody.get("projectId"), requestBody.get("fromDate"), requestBody.get("toDate"));
		// response.put("matchingResultTableData", matchingResultTableData);
		response.put("result", dashboardTable);
		response.put("message", "Data matching results fetched successfully.");
		response.put("status", "success");
		LOG.info("Data matching results fetched successfully.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Token failed...");
		response.put("status", "failed");
		LOG.error("Token is expired.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    response.put("message", "Failed to fetch data matching results.");
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/getKeyMeasurementMatchingResults - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/getRollDataMatchingResults", method = RequestMethod.GET)
    public ResponseEntity<Object> getRollMatchingTablesResultTables(@RequestHeader HttpHeaders headers) {
	LOG.info("dbconsole/getRollDataMatchingResults - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		Map<Long, String> matchingResultTableData = iMatchingResultService.getRollDataMatchingResultTable();
		List<RollDataMatchingDashboard> dashboardTable = matchingresultdao.getRollmatchingDashboard();
		response.put("matchingResultTableData", matchingResultTableData);
		response.put("result", dashboardTable);
		response.put("message", "Data matching results fetched successfully.");
		response.put("status", "success");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Token failed...");
		response.put("status", "failed");
		LOG.error("Token failed...");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    response.put("message", "Failed to fetch data matching results.");
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/getRollDataMatchingResults - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/getSourceTableDetails", method = RequestMethod.POST)
    public ResponseEntity<Object> getDataMatchingResultsForSourceTable(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, Long> requestBody) {
	LOG.info("dbconsole/getSourceTableDetails - START");
	Map<String, Object> response = new HashMap<String, Object>();
	JSONObject json = new JSONObject();
	try {
	    LOG.debug("token " + headers.get("token").get(0).toString());
	    LOG.debug("Getting request parameters  " + requestBody);
	    long appId = requestBody.get("appId");
	    RollDataMatchingDashboard dashboardTable = matchingresultdao.getRollmatchingDashboardById(appId);
	    if (dashboardTable != null) {
		json.put("source1", dashboardTable.getSource1());
		json.put("source2", dashboardTable.getSource2());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	    response.put("status", "failed");
	    response.put("message", "Unable to fetched source table details.");
	  
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	response.put("status", "success");
	response.put("message", "Successfully fetched source table details.");
	response.put("result", json.toString());
	LOG.info("dbconsole/getSourceTableDetails - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/getMatchTablesData", method = RequestMethod.POST)
    public ResponseEntity<Object> getDataMatchingResults(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/getMatchTablesData - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	String source1 = (String) requestBody.get("source1");
	String source2 = (String) requestBody.get("source2");
	long appId = Long.parseLong(requestBody.get("appId"));
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		LOG.info("dbconsole/getMatchTablesData - END");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
		String appName = listApplicationsData.getName();
		LOG.debug("appName:" + appName);
		response.put("appName", appName);
		// Long appId = Long.parseLong(request.getParameter("appId"));
		Map<Long, String> matchingResultTableData = iMatchingResultService.getMatchingResultTable();
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
		while (laData.next()) {
		    response.put("threshold", laData.getDouble("matchingThreshold"));
		    response.put("measurement", false);
		}
		SqlRowSet sqlRowSet = iMatchingResultService.getDataMatchingResultsTableNames(appId);

		response.put("source1", source1);
		response.put("source2", source2);

		response.put("matchingResultTableData", matchingResultTableData);
		response.put("Left_Only_Count", 0);
		response.put("Right_Only_Count", 0);
		response.put("Unmatched_result_Count", 0);
		// Mamta 29/Aug/2022
		response.put("measurement", false);
		Object[] getZeroStatusAbdCount = iMatchingResultService
			.getZeroStatusAbdCount("DATA_MATCHING_" + appId + "_SUMMARY", appId);
		if (getZeroStatusAbdCount != null) {
		    response.put("leftStatus", getZeroStatusAbdCount[0]);
		    response.put("rightStatus", getZeroStatusAbdCount[1]);
		    response.put("leftTotalRecord", getZeroStatusAbdCount[2]);
		    response.put("rightTotalRecord", getZeroStatusAbdCount[3]);
		}
		while (sqlRowSet.next()) {
		    if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
			response.put("PrimaryKeyMatching", true);
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")) {
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    response.put("Primary_Left_Only", dataForPrimary);

			    dataForPrimary.last();
			    response.put("PrimaryLeftOnlyCount", dataForPrimary.getRow());

			    response.put("PrimaryLeftOnlyTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("PrimaryRightOnlyCount", dataForPrimary.getRow());
			    response.put("Primary_Right_Only", dataForPrimary);
			    response.put("PrimaryRightOnlyTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
			    LOG.info("PRIMARY_UNMATCHED");
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("Primary_Unmatched_Count", dataForPrimary.getRow());

			    response.put("Primary_Unmatched_result", dataForPrimary);
			    response.put("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
			    LOG.info("PRIMARY_UNMATCHED");
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("Primary_Unmatched_Count", dataForPrimary.getRow());
			    response.put("Primary_Unmatched_result", dataForPrimary);
			    response.put("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
			    response.put("idApp", appId);
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED_ANAMOLY")) {
			    response.put("primaryUnMatchedAnamoly", sqlRowSet.getString("Table_Name"));
			    Object[] dataAndCount = iMatchingResultService
				    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			    response.put("primary_Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
			    response.put("primary_Unmatched_Anamoly_result", dataAndCount[0]);
			    response.put("primary_Unmatched_Anamoly_result_Count", dataAndCount[1]);
			    response.put("idApp", appId);
			}
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Left_Only")) {
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Left_Only", dataAndCount[0]);
			response.put("Left_Only_Count", dataAndCount[1]);
			response.put("idApp", appId);
			response.put("leftOnlyTableName", sqlRowSet.getString("Table_Name"));
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Right_Only")) {
			response.put("rightOnlyTableName", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Right_Only", dataAndCount[0]);
			response.put("Right_Only_Count", dataAndCount[1]);
			response.put("idApp", appId);
			LOG.debug(" dataAndCount[1]:" + dataAndCount[1]);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
			response.put("unMatchedTableName", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Unmatched_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("Unmatched_result", dataAndCount[0]);
			response.put("Unmatched_result_Count", dataAndCount[1]);
			response.put("idApp", appId);
			response.put("measurement", true);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_ANAMOLY")) {
			response.put("unMatchedAnamoly", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("Unmatched_Anamoly_result", dataAndCount[0]);
			response.put("Unmatched_Anamoly_result_Count", dataAndCount[1]);
			response.put("idApp", appId);
			response.put("measurement", true);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
			List<DataMatchingSummary> dmSummaryData = iMatchingResultService
				.getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
			Map<String, String> dataFromDataMatchingSummary = iMatchingResultService
				.getDataFromDataMatchingSummary(sqlRowSet.getString("Table_Name"));
			response.put("dataMatchingMap", dataFromDataMatchingSummary);
			response.put("dmSummaryData", dmSummaryData);
			String tableName = sqlRowSet.getString("Table_Name");
			List unmatchedGraph = matchingresultdao.getUnmatchedGraph(tableName);
			List<DataMatchingSummary> leftGraph = matchingresultdao.getLeftGraph(tableName);
			List<DataMatchingSummary> rightGraph = matchingresultdao.getLeftGraph(tableName);
			response.put("unmatchedGraph", unmatchedGraph);
			LOG.debug("${unmatchedGraph}" + unmatchedGraph);
			response.put("leftGraph", leftGraph);
			response.put("rightGraph", rightGraph);
			response.put("idApp", appId);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_GROUPBY")) {
			LOG.info("UNMATCHED_GROUPBY");
			response.put("UnmatchedGroupBy_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("unMatchedGroupByTableName", sqlRowSet.getString("Table_Name"));
			SqlRowSet unmatchedGroupbyTableData = iMatchingResultService
				.getUnmatchedGroupbyTableData(sqlRowSet.getString("Table_Name"));
			response.put("unmatchedGroupbyTableData", unmatchedGroupbyTableData);
			unmatchedGroupbyTableData.last();
			response.put("unmatchedGroupbyRecordCount", unmatchedGroupbyTableData.getRow());
			unmatchedGroupbyTableData.beforeFirst();
			response.put("idApp", appId);
		    }
		}
		if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
		    response.put("RecordCountMatching", true);
		}
		response.put("status", "success");
		response.put("message", "Successfully fetched data.");
		LOG.info("Successfully fetched data.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    Map<String, Object> failedResponse = new HashMap<String, Object>();
	    failedResponse.put("message", "Unable to fetch data.");
	    failedResponse.put("status", "failed");
	    LOG.error(e.getMessage());
	    return new ResponseEntity<Object>(failedResponse, HttpStatus.OK);
	}
	LOG.info("dbconsole/getMatchTablesData - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/getRollDataMatchTablesData", method = RequestMethod.POST)
    public ResponseEntity<Object> getRollDataMatchingResults(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/getRollDataMatchTablesData - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	String source1 = (String) requestBody.get("source1");
	String source2 = (String) requestBody.get("source2");
	long appId = Long.parseLong(requestBody.get("appId"));
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {

		LOG.debug("=============getRollDataMatchingResults=== Source1 ==============" + source1);

		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
		response.put("appId", appId);

		long rollTargetSchemaId = listApplicationsData.getRollTargetSchemaId();
		response.put("rollTargetSchemaId", rollTargetSchemaId);

		/*
		 * Abbive requirement: Adding this property to connect to AverUI tool
		 */
		String averReportUILink = appDbConnectionProperties.getProperty("aver.report.link");
		response.put("averReportUILink", averReportUILink);

		String appName = listApplicationsData.getName();
		LOG.debug("appName:" + appName);
		response.put("appName", appName);

		Map<Long, String> matchingResultTableData = iMatchingResultService.getRollDataMatchingResultTable();
		response.put("matchingResultTableData", matchingResultTableData);
		response.put("measurement", true);
		response.put("source1", source1);
		response.put("source2", source2);
		response.put("Left_Only_Count", 0);
		response.put("Right_Only_Count", 0);
		response.put("Unmatched_result_Count", 0);
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
		while (laData.next()) {
		    response.put("threshold", laData.getDouble("matchingThreshold"));
		}

		SqlRowSet sqlRowSet = iMatchingResultService.getRollDataMatchingResultsTableNames(appId);

		Object[] getZeroStatusAbdCount = iMatchingResultService
			.getZeroStatusAbdCount("DATA_MATCHING_" + appId + "_SUMMARY", appId);
		if (getZeroStatusAbdCount != null) {
		    response.put("leftStatus", getZeroStatusAbdCount[0]);
		    response.put("rightStatus", getZeroStatusAbdCount[1]);
		    response.put("leftTotalRecord", getZeroStatusAbdCount[2]);
		    response.put("rightTotalRecord", getZeroStatusAbdCount[3]);
		}
		while (sqlRowSet.next()) {
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
			// Get the summary results from Target

			String tableName = sqlRowSet.getString("Table_Name");

			SqlRowSet summryData = iMatchingResultService.getRollDataSummary(tableName, rollTargetSchemaId);

			long summryCount = iMatchingResultService.getRollDataSummaryCount(tableName,
				rollTargetSchemaId);

			response.put("rollSummaryTableName", tableName);
			response.put("rollSummary_Table_Name", tableName);
			response.put("rollSummary_result", summryData);
			response.put("rollSummary_result_Count", summryCount);
			response.put("idApp", appId);
		    }

		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
			List<DataMatchingSummary> dmSummaryData = iMatchingResultService
				.getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
			Map<String, String> dataFromDataMatchingSummary = iMatchingResultService
				.getDataFromDataMatchingSummary(sqlRowSet.getString("Table_Name"));
			response.put("dataMatchingMap", dataFromDataMatchingSummary);
			response.put("dmSummaryData", dmSummaryData);
			String tableName = sqlRowSet.getString("Table_Name");
			List unmatchedGraph = matchingresultdao.getUnmatchedGraph(tableName);
			List<DataMatchingSummary> leftGraph = matchingresultdao.getLeftGraph(tableName);
			List<DataMatchingSummary> rightGraph = matchingresultdao.getLeftGraph(tableName);
			response.put("unmatchedGraph", unmatchedGraph);
			LOG.debug("${unmatchedGraph}" + unmatchedGraph);
			response.put("leftGraph", leftGraph);
			response.put("rightGraph", rightGraph);
			response.put("idApp", appId);
		    }

		}
		if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
		    response.put("RecordCountMatching", true);
		}
		response.put("message", "Successfully fetched roll data matching details");
		response.put("status", "success");
		LOG.info("Successfully fetched roll data matching details");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    Map<String, Object> failedResponse = new HashMap<String, Object>();
	    failedResponse.put("message", "Failed to fetch roll data matching details");
	    failedResponse.put("status", "failed");
	    LOG.error(e.getMessage());
	    
	    return new ResponseEntity<Object>(failedResponse, HttpStatus.OK);
	}
	LOG.info("dbconsole/getRollDataMatchTablesData - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/getRollDMSummaryData", method = RequestMethod.POST)
    public ResponseEntity<Object> getRollDMSummaryData(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> request) {
	LOG.info("dbconsole/getRollDMSummaryData - START");
	JSONObject result = new JSONObject();
	JSONArray array = new JSONArray();
	Map<String, Object> response = new HashMap<String, Object>();
	try {
	    LOG.debug("token " + headers.get("token").get(0).toString());
	    String tableName = request.get("tableName");
	    String appIdStr = request.get("appId");
	    long appId = Long.parseLong(appIdStr);
	    LOG.debug("tableName=" + tableName);
	    LOG.debug("appId=" + appId);

	    ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);

	    long rollTargetSchemaId = listApplicationsData.getRollTargetSchemaId();

	    long totalRecords = iMatchingResultService.getRollDataSummaryCount(tableName, rollTargetSchemaId);

	    SqlRowSet rs = iMatchingResultService.getRollDataSummary(tableName, rollTargetSchemaId);
	    String[] columnNames = rs.getMetaData().getColumnNames();

	    DecimalFormat numberFormat = new DecimalFormat("#.00");

	    while (rs.next()) {
		JSONArray ja = new JSONArray();
		int colIndex = 0;
		for (String colName : columnNames) {
		    try {
			if (rs.getString(columnNames[colIndex]) != null) {
			    if (rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("double")
				    || rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("decimal")
				    || rs.getMetaData().getColumnTypeName(colIndex + 1).equalsIgnoreCase("float")) {
				ja.put(numberFormat.format(rs.getDouble(columnNames[colIndex])));
				colIndex++;
			    } else {

				if (rs.getString(columnNames[colIndex]).equalsIgnoreCase("passed")) {
				    ja.put("<td><span class='label label-success label-sm'>"
					    + rs.getString(columnNames[colIndex]) + "</span></td>");
				    colIndex++;
				} else if (rs.getString(columnNames[colIndex]).equalsIgnoreCase("failed")) {
				    ja.put("<td><span class='label label-danger label-sm'>"
					    + rs.getString(columnNames[colIndex]) + "</span></td>");
				    colIndex++;
				} else {
				    ja.put(rs.getString(columnNames[colIndex]));
				    colIndex++;
				}

			    }
			} else {
			    ja.put(rs.getString(columnNames[colIndex]));
			    colIndex++;
			}

		    } catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		    }
		}
		array.put(ja);
		result.put("iTotalRecords", totalRecords);
		result.put("iTotalDisplayRecords", totalRecords);
		result.put("aaData", array);
	    }
	} catch (Exception e) {
	    try {
		result.put("iTotalRecords", 0);
		result.put("iTotalDisplayRecords", 0);
		result.put("aaData", array);
	    } catch (JSONException e1) {
		e1.printStackTrace();
	    }
	    response.put("message", "Failed to fetch roll DMS summary details.");
	    response.put("status", "failed");
	    LOG.error("Token is expired.");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	// response.setContentType("application/json");
	// response.setHeader("Cache-Control", "no-store");
	response.put("message", "Successfully fetched roll DMS summary details.");
	response.put("status", "success");
	response.put("result", result.toString());
	LOG.info("Successfully fetched roll DMS summary details.");
	LOG.info("dbconsole/getRollDMSummaryData - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    // for primary key matching getPrimaryKeyMatchingResultTable

    @RequestMapping(value = "/dbconsole/getPrimaryKeyMatchingResultsDetails", method = RequestMethod.POST)
    public ResponseEntity<Object> getPrimaryKeyMatchingResultTableDetails(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/getPrimaryKeyMatchingResultsDetails - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    // Check whether all required parameters are available.
	    if (!requestBody.containsKey("domainId") || !requestBody.containsKey("projectId")
		    || !requestBody.containsKey("fromDate") || !requestBody.containsKey("toDate")) {
		throw new Exception("Required parameters not found.");
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		LOG.debug("Getting request parameters  " + requestBody);
		// Map<Long, String> matchingResultTableData =
		// iMatchingResultService.getPrimaryKeyMatchingResultTable();
		List<KeyMeasurementMatchingDashboard> dashboardTable = primaryKeyMatchingResultService
			.getPrimaryKeyMatchingDashboard(requestBody.get("domainId"), requestBody.get("projectId"),
				requestBody.get("fromDate"), requestBody.get("toDate"));
		response.put("result", dashboardTable);
		response.put("message", "Primary key matching results fetched successfully.");
		response.put("status", "success");
		LOG.info("Primary key matching results fetched successfully.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Token failed...");
		response.put("status", "failed");
		LOG.error("Token failed...");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    response.put("message", "Failed to fetch primary key matching results.");
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/getPrimaryKeyMatchingResultsDetails - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/loadPrimaryKeyMatchingResultsTable", method = RequestMethod.POST)
    public ResponseEntity<Object> loadPrimaryKeyMatchingResultsTable(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/loadPrimaryKeyMatchingResultsTable - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;

	try {
	    // Check whether token is present in header or not
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    // Check whether all required parameters are available.
	    if (!requestBody.containsKey("appId")) {
		response.put("message", "Please provide require parameters.");
		response.put("status", "failed");
		LOG.error("Please provide require parameters.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		LOG.debug("Getting request parameters  " + requestBody);
		long appId = Long.parseLong(requestBody.get("appId"));
		String primary_matched_tableName = "DATA_MATCHING_" + appId + "_MATCHED";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("recordsTable",
			matchingresultdao.getPrimaryKeyMatchingTableResult(primary_matched_tableName, appId));
		// Get table name
		SqlRowSet sqlRowSet = jdbcTemplate1
			.queryForRowSet("select * from result_master_table where appID=? and AppType='PKM'", appId);
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
		Double threshold = null;
		while (laData.next()) {
		    threshold = laData.getDouble("matchingThreshold");
		}
		Map<String, String> columnMapping = getPrimaryKeyMatchingColumnRelationshipMap(appId);
		while (sqlRowSet.next()) {
		    // Retrieve table name
		    String tableName = sqlRowSet.getString("Table_Name");
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Summary")) {
			// Get primarykey data matching summary
			Map<String, String> primaryKeyMatchingMap = iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummary(tableName);
			primaryKeyMatchingMap.put("threshold", threshold.toString());
			resultMap.put("primaryKeyMatchingGap", primaryKeyMatchingMap);
			// Get primary key data matching summary group by data
			List<PrimaryMatchingSummary> dmPrimarySummaryData = iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(tableName);
			resultMap.put("dmPrimarySummaryData", dmPrimarySummaryData);
			if (!dmPrimarySummaryData.isEmpty()) {
			    resultMap.put("firstSourceTotalCount", dmPrimarySummaryData.get(0).getLeftOnlyCount());
			    resultMap.put("secondSourceTotalCount", dmPrimarySummaryData.get(0).getRightOnlyCount());
			    resultMap.put("unmatchedTotalCount", dmPrimarySummaryData.get(0).getUnMatchedCount());
			}
		    } else if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")
			    || sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
			SqlRowSet dataForPrimary = iMatchingResultService.getDataForPrimary(tableName, appId);
			String subString = sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")
				? "Left"
				: "Right";
			Map<String, Object> tableDataMap = new HashMap<String, Object>();
			tableDataMap.put("primary" + subString + "Only",
				convertSqlResultSetToList(dataForPrimary, columnMapping));
			dataForPrimary.last();
			tableDataMap.put("Primary" + subString + "OnlyCount", dataForPrimary.getRow());
			tableDataMap.put("Primary" + subString + "OnlyTableName", sqlRowSet.getString("Table_Name"));
			resultMap.put(subString + "TableData", tableDataMap);
		    }
		}
			// Response changes for both POSTGRES and MYSQL
			if ((DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))) {
				String updatedKey="";
				String updatedValue="";
				Map<String, String> updatedMap = new HashMap<>();
				for (Map.Entry<String, String> entry : columnMapping.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					// Compare and replace key and value as needed
					updatedKey = key.replace("leftM","leftm").replace("leftV","leftv").replace("rightM","rightm").replace("rightV","rightv");
					updatedValue = value.replace("leftM","leftm").replace("leftV","leftv").replace("rightM","rightm").replace("rightV","rightv");
					updatedMap.put(updatedKey, updatedValue);
				}
				resultMap.put("columnsMapping", updatedMap);
			}else{
				resultMap.put("columnsMapping", columnMapping);
			}
		response.put("result", resultMap);
		response.put("message", "Successfully fetched primary key matching result details.");
		response.put("status", "success");
		LOG.info("Successfully fetched primary key matching result details.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception oException) {
	    response.put("message", "Failed to fetch primary key matching result details.");
	    response.put("status", "failed");
	    LOG.error(oException.getMessage());
	    
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/loadPrimaryKeyMatchingResultsTable - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/loadPrimaryKeyMatchingResultsTableByIgnoreColumn", method = RequestMethod.POST)
    public ResponseEntity<Object> loadPrimaryKeyMatchingResultsTable(@RequestHeader HttpHeaders headers,
	    @RequestBody String jsonStr) {
	LOG.info("dbconsole/loadPrimaryKeyMatchingResultsTableByIgnoreColumn - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;

	try {
	    JSONObject requestBody = new JSONObject(jsonStr);
	    // Check whether token is present in header or not
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    // Check whether all required parameters are available.
	    if (!requestBody.has("appId")) {
		response.put("message", "Please provide require parameters.");
		response.put("status", "failed");
		LOG.error("Please provide require parameters.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		LOG.debug("Getting request parameters  " + requestBody);
		long appId = requestBody.getLong("appId");
		JSONArray ignoreColumnArr = requestBody.getJSONArray("ignoreColumns");
		status = HttpStatus.OK;

		if (ignoreColumnArr.length() > 0) {
		    String primary_matched_tableName = "DATA_MATCHING_" + appId + "_MATCHED";
		    Map<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("recordsTable", matchingresultdao
			    .getPrimaryKeyMatchingTableResult(primary_matched_tableName, ignoreColumnArr));

		    Map<String, String> columnMapping = getPrimaryKeyMatchingColumnRelationshipMap(appId);
		    resultMap.put("columnsMapping", columnMapping);

		    response.put("result", resultMap);
		    response.put("message", "Successfully fetched primary key matching result details.");
		    response.put("status", "success");
		    LOG.info("Successfully fetched primary key matching result details.");
		} else {
		    response.put("result", new HashMap<String, Object>());
		    response.put("message", "Ignore columns are missing in the Input.");
		    response.put("status", "failed");
		    LOG.error("Ignore columns are missing in the Input.");
		}
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception oException) {
	    oException.printStackTrace();
	    response.put("message", "Failed to fetch primary key matching result details.");
	    response.put("status", "failed");
	    LOG.error(oException.getMessage());
	    
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/loadPrimaryKeyMatchingResultsTableByIgnoreColumn - END");
	return new ResponseEntity<Object>(response, status);
    }

    private Map<String, String> getPrimaryKeyMatchingColumnRelationshipMap(long idApp) {

	Map<String, String> relationShipCols = new HashMap<String, String>();

	try {
	    List<ListDmCriteria> matchingConditions = validationDao.getDataFromListDMCriteria(idApp);

	    if (matchingConditions != null) {
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)){
				for (ListDmCriteria listDmCriteria : matchingConditions) {
					String matchType = listDmCriteria.getMatchType();

					String leftColumnName = listDmCriteria.getLeftSideColumn().trim().toLowerCase();
					String rightColumnName = listDmCriteria.getRightSideColumn().trim().toLowerCase();

					if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")) {
						relationShipCols.put("leftm_" + leftColumnName, "rightm_" + rightColumnName);

					} else if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")) {
						relationShipCols.put("leftv_" + leftColumnName, "rightv_" + rightColumnName);
					}

				}
			}else{
				for (ListDmCriteria listDmCriteria : matchingConditions) {
					String matchType = listDmCriteria.getMatchType();
//		    String leftColumnName = listDmCriteria.getLeftSideColumn().trim().toLowerCase();
//		    String rightColumnName = listDmCriteria.getRightSideColumn().trim().toLowerCase();

					String leftColumnName = listDmCriteria.getLeftSideColumn().trim();
					String rightColumnName = listDmCriteria.getRightSideColumn().trim();

					if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")) {
						relationShipCols.put("leftM_" + leftColumnName, "rightM_" + rightColumnName);

					} else if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")) {
						relationShipCols.put("leftV_" + leftColumnName, "rightV_" + rightColumnName);
					}

				}
			}

	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return relationShipCols;
    }

    private List<Map<String, Object>> convertSqlResultSetToList(SqlRowSet rs, Map<String, String> columnMapping) {
	SqlRowSetMetaData md = rs.getMetaData();
	int columns = md.getColumnCount();
	Set<String> columnList = new HashSet<>();
	for (String s : columnMapping.keySet()) {
	    String value = columnMapping.get(s);

	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			value = value.replaceAll("rightm_", "").replaceAll("rightv_", "").replaceAll("leftm_", "")
					.replaceAll("leftv_", "");
			s = s.replaceAll("rightm_", "").replaceAll("rightm_", "").replaceAll("leftm_", "").replaceAll("leftv_", "");
			columnList.add(s.trim().toLowerCase());
			columnList.add(value.trim().toLowerCase());
		}else {
			value = value.replaceAll("rightM_", "").replaceAll("rightV_", "").replaceAll("leftM_", "")
					.replaceAll("leftV_", "");
			s = s.replaceAll("rightM_", "").replaceAll("rightM_", "").replaceAll("leftM_", "").replaceAll("leftV_", "");
			columnList.add(s);
			columnList.add(value);
		}
	}

//		System.out.println("columnList:"+columnList);
	List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>(columns);
				for (int i = 1; i <= columns; ++i) {

					String colName= md.getColumnName(i).trim().toLowerCase();
					System.out.println("colName="+colName);

					if (columnList.contains(colName)) {
						row.put(colName, rs.getObject(i));
					}else if ("date".equalsIgnoreCase(colName)) {
						row.put("Date", rs.getString(i));
					}else if ("run".equalsIgnoreCase(colName)) {
						row.put("Run", rs.getObject(i));
					}
				}
				rows.add(row);
			}
		}else {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>(columns);
				for (int i = 1; i <= columns; ++i) {
					if (columnList.contains(md.getColumnName(i)) || "run".equalsIgnoreCase(md.getColumnName(i))) {
						row.put(md.getColumnName(i), rs.getObject(i));
					} else if ("date".equalsIgnoreCase(md.getColumnName(i))) {
						row.put(md.getColumnName(i), rs.getString(i));
					}
				}
				rows.add(row);
			}
		}

	return rows;
    }

    @RequestMapping(value = "/dbconsole/getPrimaryKeyMatchTablesData", method = RequestMethod.POST)
    public ResponseEntity<Object> getPrimaryKeyMatchingResults(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/getPrimaryKeyMatchTablesData - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	String source1 = (String) requestBody.get("source1");
	String source2 = (String) requestBody.get("source2");
	long appId = Long.parseLong(requestBody.get("appId"));
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		LOG.error("Token is missing in headers.");
		LOG.info("dbconsole/getPrimaryKeyMatchTablesData - END");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (validateToken(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());

		LOG.debug("=============getPrimaryKeyMatchingResults=== Source1 ==============" + source1);

		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
		String appName = listApplicationsData.getName();
		LOG.debug("appName:" + appName);
		response.put("appName", appName);
		response.put("appId", appId);
		String columnsData = matchingresultdao.getPrimaryKeyMatchingResultTableColumns(appId);
		LOG.debug("\ncolumnsData: " + columnsData);
		response.put("columnsData", columnsData);
		// Long appId = Long.parseLong(request.getParameter("appId"));
		Map<Long, String> matchingResultTableData = iMatchingResultService.getPrimaryKeyMatchingResultTable();
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
		while (laData.next()) {
		    response.put("threshold", laData.getDouble("matchingThreshold"));
		    if (laData.getString("fileNameValidation").equalsIgnoreCase("Y")) {
			LOG.info("measurement is true");
			response.put("measurement", true);
		    }
		}
		SqlRowSet sqlRowSet = iMatchingResultService.getPrimaryKeyMatchingResultsTableNames(appId);

		response.put("source1", source1);
		response.put("source2", source2);

		response.put("matchingResultTableData", matchingResultTableData);
		response.put("Left_Only_Count", 0);
		response.put("Right_Only_Count", 0);
		response.put("Unmatched_result_Count", 0);
		Object[] getZeroStatusAbdCountForPrimaryKeyMatchingCount = iMatchingResultService
			.getZeroStatusAbdCountForPrimaryKeyMatching("DATA_MATCHING_" + appId + "_SUMMARY", appId);

		response.put("leftStatus", getZeroStatusAbdCountForPrimaryKeyMatchingCount[0]);
		response.put("rightStatus", getZeroStatusAbdCountForPrimaryKeyMatchingCount[1]);
		response.put("leftTotalRecord", getZeroStatusAbdCountForPrimaryKeyMatchingCount[2]);
		response.put("rightTotalRecord", getZeroStatusAbdCountForPrimaryKeyMatchingCount[3]);
		while (sqlRowSet.next()) {
		    if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
			response.put("PrimaryKeyMatching", true);
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")) {
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    response.put("Primary_Left_Only", dataForPrimary);

			    dataForPrimary.last();
			    response.put("PrimaryLeftOnlyCount", dataForPrimary.getRow());

			    response.put("PrimaryLeftOnlyTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("PrimaryRightOnlyCount", dataForPrimary.getRow());
			    response.put("Primary_Right_Only", dataForPrimary);
			    response.put("PrimaryRightOnlyTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
			    LOG.info("PRIMARY_UNMATCHED");
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("Primary_Unmatched_Count", dataForPrimary.getRow());

			    response.put("Primary_Unmatched_result", dataForPrimary);
			    response.put("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
			    LOG.info("PRIMARY_UNMATCHED");
			    SqlRowSet dataForPrimary = iMatchingResultService
				    .getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			    dataForPrimary.last();
			    response.put("Primary_Unmatched_Count", dataForPrimary.getRow());
			    response.put("Primary_Unmatched_result", dataForPrimary);
			    response.put("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
			    response.put("idApp", appId);
			}
			if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED_ANAMOLY")) {
			    response.put("primaryUnMatchedAnamoly", sqlRowSet.getString("Table_Name"));
			    Object[] dataAndCount = iMatchingResultService
				    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			    response.put("primary_Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
			    response.put("primary_Unmatched_Anamoly_result", dataAndCount[0]);
			    response.put("primary_Unmatched_Anamoly_result_Count", dataAndCount[1]);
			    response.put("idApp", appId);
			}
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Left_Only")) {
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Left_Only", dataAndCount[0]);
			response.put("Left_Only_Count", dataAndCount[1]);
			response.put("idApp", appId);
			response.put("leftOnlyTableName", sqlRowSet.getString("Table_Name"));
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Right_Only")) {
			response.put("rightOnlyTableName", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Right_Only", dataAndCount[0]);
			response.put("Right_Only_Count", dataAndCount[1]);
			response.put("idApp", appId);
			LOG.debug(" dataAndCount[1]:" + dataAndCount[1]);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
			response.put("unMatchedTableName", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Unmatched_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("Unmatched_result", dataAndCount[0]);
			response.put("Unmatched_result_Count", dataAndCount[1]);
			response.put("idApp", appId);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_ANAMOLY")) {
			response.put("unMatchedAnamoly", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("Unmatched_Anamoly_result", dataAndCount[0]);
			response.put("Unmatched_Anamoly_result_Count", dataAndCount[1]);
			response.put("idApp", appId);
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
			List<PrimaryMatchingSummary> dmPrimarySummaryData = iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
			LOG.debug("@@@@@@@@@@@dmPrimarySummaryData ==>" + dmPrimarySummaryData.toString().indexOf(4));

			Map<String, String> dataFromPrimaryKeyMatchingSummary = iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummary(sqlRowSet.getString("Table_Name"));
			response.put("primaryKeyMatchingMap", dataFromPrimaryKeyMatchingSummary);
			response.put("dmPrimarySummaryData", dmPrimarySummaryData);
			String tableName = sqlRowSet.getString("Table_Name");
			List unmatchedGraph = matchingresultdao.getUnmatchedGraphForPrimaryKeyMatching(tableName);
			List<PrimaryMatchingSummary> leftGraph = matchingresultdao
				.getLeftGraphForPrimaryKeyMatching(tableName);
			List<PrimaryMatchingSummary> rightGraph = matchingresultdao
				.getLeftGraphForPrimaryKeyMatching(tableName);
			response.put("unmatchedGraph", unmatchedGraph);
			LOG.debug("${unmatchedGraph}" + unmatchedGraph);
			response.put("leftGraph", leftGraph);
			response.put("rightGraph", rightGraph);
			response.put("idApp", appId);
			response.put("Unmatched_result_Count", dataFromPrimaryKeyMatchingSummary.get("unMatchedCount"));
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_GROUPBY")) {
			LOG.info("UNMATCHED_GROUPBY");
			response.put("UnmatchedGroupBy_Table_Name", sqlRowSet.getString("Table_Name"));
			response.put("unMatchedGroupByTableName", sqlRowSet.getString("Table_Name"));
			SqlRowSet unmatchedGroupbyTableData = iMatchingResultService
				.getUnmatchedGroupbyTableData(sqlRowSet.getString("Table_Name"));
			response.put("unmatchedGroupbyTableData", unmatchedGroupbyTableData);
			unmatchedGroupbyTableData.last();
			response.put("unmatchedGroupbyRecordCount", unmatchedGroupbyTableData.getRow());
			unmatchedGroupbyTableData.beforeFirst();
			response.put("idApp", appId);
		    }
		}
		if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
		    response.put("RecordCountMatching", true);
		}
		response.put("message", "Successfully fetched primary key matching details.");
		response.put("status", "success");
		LOG.info("Successfully fetched primary key matching details.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    Map<String, Object> failedResponse = new HashMap<String, Object>();
	    failedResponse.put("message", "Failed to fetch primary key mathcing details.");
	    failedResponse.put("status", "failed");
	    LOG.error(e.getMessage());
	    return new ResponseEntity<Object>(failedResponse, HttpStatus.OK);
	}
	LOG.info("dbconsole/getPrimaryKeyMatchTablesData - END");
	return new ResponseEntity<Object>(response, status);
    }

    @RequestMapping(value = "/dbconsole/matchTablesData", method = RequestMethod.POST)
    public ResponseEntity<Object> getDataMatchingResulsts(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/matchTablesData - START");
	String source1 = (String) requestBody.get("source1");
	String source2 = (String) requestBody.get("source2");
	long appId = Long.parseLong(requestBody.get("appId"));
	Map<String, Object> result = new HashMap<String, Object>();
	if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
	    result.put("message", "Please provide token.");
	    result.put("status", "failed");
	    LOG.error("Token is missing in headers.");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.EXPECTATION_FAILED);
	}
	if (!validateToken(headers.get("token").get(0))) {
	    result.put("message", "Token expired.");
	    result.put("status", "failed");
	    LOG.error("Token is expired.");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    LOG.debug("=============getDataMatchingResults=== Source1 ==============" + source1);
	    Map<String, Object> response = new HashMap<>();
	    ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
	    // response.put("currentSection", "Dashboard");
	    // response.put("currentLink", "Data Matching");
	    String appName = listApplicationsData.getName();
	    LOG.debug("appName:" + appName);
	    // response.put("appName", appName);
	    // Long appId = Long.parseLong(request.getParameter("appId"));
	    // Map<Long, String> matchingResultTableData =
	    // iMatchingResultService.getMatchingResultTable();
	    Map<String, Object> dataFromDataMatchingSummary = new HashMap<>();
	    SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
	    while (laData.next()) {
		dataFromDataMatchingSummary.put("threshold", laData.getDouble("matchingThreshold"));
		LOG.info("measurement is true");
	    }
	    SqlRowSet sqlRowSet = iMatchingResultService.getDataMatchingResultsTableNames(appId);
	    // response.put("source1", source1);
	    // response.put("source2", source2);

	    // response.put("matchingResultTableData", matchingResultTableData);
	    // response.put("Left_Only_Count", 0);
	    // response.put("Right_Only_Count", 0);
	    // response.put("Unmatched_result_Count", 0);

	    Object[] getZeroStatusAbdCount = iMatchingResultService
		    .getZeroStatusAbdCount("DATA_MATCHING_" + appId + "_SUMMARY", appId);

	    dataFromDataMatchingSummary.put("leftStatus", getZeroStatusAbdCount[0]);
	    dataFromDataMatchingSummary.put("rightStatus", getZeroStatusAbdCount[1]);
	    dataFromDataMatchingSummary.put("measurement", false);
	    response.put("primaryLeftTable", new ArrayList<>());
	    response.put("primaryRightTable", new ArrayList<>());
	    response.put("primaryUnmatchedTable", new ArrayList<>());
	    response.put("leftTable", new ArrayList<>());
	    response.put("rightTable", new ArrayList<>());
	    response.put("unmatchedTable", new ArrayList<>());
	    response.put("unmatchedAnamolyTable", new ArrayList<>());

	    while (sqlRowSet.next()) {
		if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
		    response.put("PrimaryKeyMatching", true);
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")) {
			SqlRowSet dataForPrimary = iMatchingResultService
				.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			response.put("primaryLeftTable", getResults(dataForPrimary));
			dataForPrimary.last();
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
			SqlRowSet dataForPrimary = iMatchingResultService
				.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			dataForPrimary.last();
			response.put("primaryRightTable", getResults(dataForPrimary));
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
			LOG.info("PRIMARY_UNMATCHED");
			SqlRowSet dataForPrimary = iMatchingResultService
				.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
			dataForPrimary.last();
			response.put("primaryUnmatchedTable", getResults(dataForPrimary));
		    }
		    if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED_ANAMOLY")) {
			response.put("primaryUnMatchedAnamoly", sqlRowSet.getString("Table_Name"));
			Object[] dataAndCount = iMatchingResultService
				.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
			response.put("primaryUnmatchedAnomalyTable", getResults((SqlRowSet) dataAndCount[0]));
		    }
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Left_Only")) {
		    Object[] dataAndCount = iMatchingResultService
			    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
		    response.put("leftTable", getResults((SqlRowSet) dataAndCount[0]));
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Right_Only")) {
		    Object[] dataAndCount = iMatchingResultService
			    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
		    response.put("rightTable", getResults((SqlRowSet) dataAndCount[0]));
		    LOG.debug(" dataAndCount[1]:" + dataAndCount[1]);
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
		    Object[] dataAndCount = iMatchingResultService
			    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
		    response.put("unmatchedTable", getResults((SqlRowSet) dataAndCount[0]));
		    dataFromDataMatchingSummary.put("measurement", true);
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_ANAMOLY")) {
		    Object[] dataAndCount = iMatchingResultService
			    .getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
		    response.put("unmatchedAnamolyTable", getResults((SqlRowSet) dataAndCount[0]));
		    dataFromDataMatchingSummary.put("idApp", appId);
		    dataFromDataMatchingSummary.put("measurement", true);
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
		    List<DataMatchingSummary> dmSummaryData = iMatchingResultService
			    .getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
		    Map<String, String> MatchingSummary = iMatchingResultService
			    .getDataFromDataMatchingSummary(sqlRowSet.getString("Table_Name"));
		    dataFromDataMatchingSummary.putAll(MatchingSummary);
		    SqlRowSet DataThreshold = iMatchingResultService.getThresholdFromListApplication(appId);
		    Double threshold = null;
		    Double absoluteThreshold = 0.0;
		    while (DataThreshold.next()) {
			threshold = DataThreshold.getDouble("matchingThreshold");
			absoluteThreshold = DataThreshold.getDouble("recordCountAnomalyThreshold");
		    }
		    dataFromDataMatchingSummary.put("absoluteThreshold", absoluteThreshold);
		    dataFromDataMatchingSummary.put("threshold", threshold.toString());
		    response.put("dataMatchingMap", dataFromDataMatchingSummary);
		    response.put("dmSummaryData", dmSummaryData);
		    if (!dmSummaryData.isEmpty()) {
			response.put("firstSourceTotalCount", dmSummaryData.get(0).getSource1OnlyRecords());
			response.put("secondSourceTotalCount", dmSummaryData.get(0).getSource2OnlyRecods());
			response.put("unmatchedTotalCount", dmSummaryData.get(0).getUnmatchedRecords());
		    }
		    String tableName = sqlRowSet.getString("Table_Name");
		    List unmatchedGraph = matchingresultdao.getUnmatchedGraph(tableName);
		    // List<DataMatchingSummary> leftGraph =
		    // matchingresultdao.getLeftGraph(tableName);
		    // List<DataMatchingSummary> rightGraph =
		    // matchingresultdao.getLeftGraph(tableName);
		    LOG.debug("${unmatchedGraph}" + unmatchedGraph);
		}
		if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_GROUPBY")) {
		    LOG.info("UNMATCHED_GROUPBY");
		    SqlRowSet unmatchedGroupbyTableData = iMatchingResultService
			    .getUnmatchedGroupbyTableData(sqlRowSet.getString("Table_Name"));
		    response.put("unmatchedGroupbyTableData", getResults(unmatchedGroupbyTableData));
		    unmatchedGroupbyTableData.last();
		    unmatchedGroupbyTableData.beforeFirst();
		}
	    }
	    if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
		// response.put("RecordCountMatching", true);
	    }
	    result.put("result", response);
	    result.put("status", "success");
	    result.put("message", "Matching data fetched successfully.");
	    LOG.info("Matching data fetched successfully.");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	} catch (Exception e) {
	    e.printStackTrace();
	    result.put("message", "Failed to fetch Key Measurement Matching details.");
	    result.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/matchTablesData - END");
	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

    }

    private List<Object> getResults(SqlRowSet queryForRowSet) {
	JSONArray resultArr = new JSONArray();
	try {
	    SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
	    while (queryForRowSet.next()) {
		JSONObject resultObj = new JSONObject();
		int count = metaData.getColumnCount();
		for (int i = 1; i <= count; i++) {
		    String column_name = metaData.getColumnName(i);
		    String columnDatatype = metaData.getColumnTypeName(i);

		    Object object = queryForRowSet.getObject(column_name);
		    if (columnDatatype.equalsIgnoreCase("date")) {
			object = queryForRowSet.getString(column_name);
		    }
		    String camCaseCol = ToCamelCase.toCamelCase(column_name);
		    if (object == null) {
			resultObj.put(camCaseCol, "");
		    } else {
			resultObj.put(camCaseCol, object);
		    }
		}
		if (resultObj != null)
		    resultArr.put(resultObj);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return resultArr.toList();
    }

    public Boolean validateToken(String token) {
	return executiveSummaryService.validateToken(token).getString("status") == "success" ? true : false;
    }

    @RequestMapping(value = "/dbconsole/schemaMatchingResultDropDown", method = RequestMethod.POST)
    public ResponseEntity<Object> getSchemaMatchingResults(@RequestHeader HttpHeaders headers) {
	LOG.info("dbconsole/schemaMatchingResultDropDown - START");
	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");
	String token = null;
	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token " + token.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    SqlRowSet rowset = IFileManagementDAOObject.getDataFromResultMasterForSchemaMatching();
	    Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
	    while (rowset.next()) {
		resultmasterdata.put(rowset.getLong(1), rowset.getString(2));
		LOG.debug(rowset.getLong(1) + "" + rowset.getString(2));
	    }
	    response.put("result", resultmasterdata);
	    response.put("status", "success");
	    response.put("message", "Successfully got the data.");
	    LOG.info("Successfully got the data.");
	} catch (Exception ex) {
	    ex.printStackTrace();
	    response.put("message", "Failed to get data.");
	    LOG.error(ex.getMessage());
	}
	LOG.info("dbconsole/schemaMatchingResultDropDown - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/showSchemaMatchingData", method = RequestMethod.POST)
    public ResponseEntity<Object> showSchemaMatchingData(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, Long> param) {
	LOG.info("dbconsole/showSchemaMatchingData - START");
	Map<String, Object> result = new HashMap<>();
	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");
	String token = null;
	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token " + token.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    Long appId = param.get("idApp");
	    ListApplications listApplications = validationcheckdao.getdatafromlistapplications(appId);
	    ListDataSchema listdataschema_left = listdatasourcedao
		    .getListDataSchemaForIdDataSchema(listApplications.getIdLeftData()).get(0);
	    ListDataSchema listdataschema_right = listdatasourcedao
		    .getListDataSchemaForIdDataSchema(listApplications.getIdRightData()).get(0);
	    result.put("leftSchemaName", listdataschema_left.getSchemaName());
	    result.put("rightSchemaName", listdataschema_right.getSchemaName());
	    listdataschema_right.getSchemaName();
	    LOG.debug("appId=" + appId);
	    String resultType = "";
	    if (listApplications.getEntityColumn().equalsIgnoreCase("MetaData")) {
		resultType = "MetaData";
	    } else if (listApplications.getEntityColumn().equalsIgnoreCase("Record Count")) {
		resultType = "RecordCount";
	    }

	    SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
		    .getTableNamesForAppIDinResultMasterForSchemaMatching(appId, resultType);

	    String table_Name = null;
	    while (tableNamesForAppIDinResultMaster.next()) {
		LOG.debug("tableName:" + tableNamesForAppIDinResultMaster.getString("Table_Name"));
		if (tableNamesForAppIDinResultMaster.getString("Table_Name")
			.equalsIgnoreCase("schema_matching_" + appId + "_tablesummary")) {
		    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
		    LOG.debug("Schema MATCHING Table_Name=" + table_Name);

		    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
		    // result.put("tablesummaryrowset", getResult(sqlRowset));
		    List<Map<String, Object>> list = getResult(sqlRowset);

		    try {
			for (int i = 0; i < list.size(); i++) {
			    list.get(i).replace("date", list.get(i).get("date").toString());
			}

		    } catch (Exception e) {

			LOG.error("date column is not present !!!");
		    }

		    result.put("tablesummaryrowset", list);

		    result.put("tableSummaryExist", "Y");
		    String csvLookupTableName = "Schema_Matching_" + appId + "_Transaction_Summary";
		    result.put("idApp", appId);
		    result.put("schemaMatchingTransactionTable", csvLookupTableName);
		}

		if (tableNamesForAppIDinResultMaster.getString("Table_Name")
			.equalsIgnoreCase("schema_matching_" + appId + "_columnsummary")) {
		    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
		    LOG.debug("Schema MATCHING Table_Name=" + table_Name);

		    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
		    result.put("columnSummaryrowset", getResult(sqlRowset));
		    result.put("columnSummaryExist", "Y");
		    String csvLookupTableName = "Schema_Matching_" + appId + "_Transaction_Summary";
		    result.put("idApp", appId);
		    result.put("schemaMatchingTransactionTable", csvLookupTableName);
		}
		if (tableNamesForAppIDinResultMaster.getString("Table_Name")
			.equalsIgnoreCase("schema_matching_" + appId + "_recordcount")) {
		    LOG.info("record count matched");
		    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
		    LOG.debug("Schema MATCHING Table_Name=" + table_Name);
		    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
		    result.put("recordSummaryrowset", getResult(sqlRowset));
		    String csvLookupTableName = "Schema_Matching_" + appId + "_Transaction_Summary";
		    result.put("idApp", appId);
		    result.put("schemaMatchingTransactionTable", csvLookupTableName);

		    result.put("recordSummaryExist", "Y");
		}
		if (tableNamesForAppIDinResultMaster.getString("table_Name")
			.equalsIgnoreCase("schema_matching_" + appId + "_recordcountuncommon")) {
		    LOG.info("record countuncommon ");
		    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
		    LOG.debug("Schema MATCHING Table_Name=" + table_Name);

		    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
		    result.put("recordSummaryrowsetuncommon", getResult(sqlRowset));
		    result.put("recordSummaryExistuncommon", "Y");
		    String csvLookupTableName = "Schema_Matching_" + appId + "_Transaction_Summary";
		    result.put("idApp", appId);
		    result.put("schemaMatchingTransactionTable", csvLookupTableName);
		    SqlRowSetMetaData metaData = sqlRowset.getMetaData();
		    for (int i = 1; i <= metaData.getColumnCount(); i++) {
			LOG.debug(metaData.getColumnName(i));

		    }
		}
		String appName = tableNamesForAppIDinResultMaster.getString("AppName");
		result.put("appName", appName);
	    }
	    response.put("status", "success");
	    response.put("result", result);
	    response.put("message", "Successully got the details.");
	    LOG.info("Successully got the details.");
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", "Failed to get details.");
	    LOG.error(e.getMessage());
	}
	LOG.info("dbconsole/showSchemaMatchingData - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    private List<Map<String, Object>> getResult(SqlRowSet queryForRowSet) {
	List<Map<String, Object>> resultArr = new ArrayList<>();
	while (queryForRowSet.next()) {
	    Map<String, Object> resultObj = new HashMap<>();
	    SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
	    int count = metaData.getColumnCount();
	    for (int i = 1; i <= count; i++) {
		String column_name = metaData.getColumnName(i);
		Object object = queryForRowSet.getObject(column_name);
		String camCaseCol = ToCamelCase.toCamelCase(column_name);
		if (object == null) {
		    resultObj.put(camCaseCol, "");
		} else {
		    resultObj.put(camCaseCol, object);
		}
	    }
	    if (resultObj != null)
		resultArr.add(resultObj);
	}
	return resultArr;
    }

    private String toCSVByHeaders1(List<Map<String, Object>> resultArr, List<String> headers, List<String> keyList) {
	final StringBuffer sb = new StringBuffer();
	for (int i = 0; i < headers.size(); i++) {
	    sb.append(headers.get(i));
	    sb.append(i == headers.size() - 1 ? "\n" : ",");
	}
	for (Map<String, Object> map : resultArr) {
	    for (int i = 0; i < keyList.size(); i++) {
		sb.append(map.get(keyList.get(i)));
		sb.append(i == keyList.size() - 1 ? "\n" : ",");
	    }
	}
	return sb.toString();
    }

    @PostMapping("/dbconsole/tableMetaDataCSV")
    public void tableMetaDataCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> param,
	    HttpServletResponse httpResponse) {
	LOG.info("dbconsole/tableMetaDataCSV - START");
	try {
	    String token = headers.get("token").get(0);
	    LOG.debug("token " + token.toString());
	    if (token != null && !token.isEmpty()) {
		if (tokenValidator.isValid(token)) {

		    List<Map<String, Object>> resultArr = new ArrayList<>();

		    Long appId = param.get("idApp");
		    ListApplications listApplications = validationcheckdao.getdatafromlistapplications(appId);
		    ListDataSchema listdataschema_left = listdatasourcedao
			    .getListDataSchemaForIdDataSchema(listApplications.getIdLeftData()).get(0);
		    ListDataSchema listdataschema_right = listdatasourcedao
			    .getListDataSchemaForIdDataSchema(listApplications.getIdRightData()).get(0);
		    listdataschema_right.getSchemaName();
		    LOG.debug("appId=" + appId);
		    String resultType = "";
		    if (listApplications.getEntityColumn().equalsIgnoreCase("MetaData")) {
			resultType = "MetaData";
		    } else if (listApplications.getEntityColumn().equalsIgnoreCase("Record Count")) {
			resultType = "RecordCount";
		    }

		    SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
			    .getTableNamesForAppIDinResultMasterForSchemaMatching(appId, resultType);

		    String table_Name = null;
		    while (tableNamesForAppIDinResultMaster.next()) {
			LOG.debug("tableName:" + tableNamesForAppIDinResultMaster.getString("Table_Name"));
			if (tableNamesForAppIDinResultMaster.getString("Table_Name")
				.equalsIgnoreCase("schema_matching_" + appId + "_tablesummary")) {
			    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
			    LOG.debug("Schema MATCHING Table_Name=" + table_Name);

			    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
			    resultArr = getResult(sqlRowset);
			}
		    }
		    if (resultArr != null && resultArr.size() > 0) {
			httpResponse.setContentType("text/csv");
			String csvFileName = "PrimaryKeyMatchRecords" + LocalDateTime.now() + ".csv";
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
			httpResponse.setHeader(headerKey, headerValue);
			List<String> keyList = Arrays.asList("id", "run", "date", "commonTables", "leftOnlyTables",
				"rightOnlyTables");
			List<String> headerList = Arrays.asList("Id", "Run", "date", "Common Tables",
				"Left Only Tables", "Right Only Tables");
			httpResponse.getWriter().print(toCSVByHeaders1(resultArr, headerList, keyList));
		    } else {
			LOG.error("Records not found.");
			throw new Exception("Records not found.");
		    }
		} else {
		    LOG.error("Token is expired.");
		    throw new Exception("Token is expired.");
		}
	    } else {
		LOG.error("Token is missing in headers.");
		throw new Exception("Please provide token.");
	    }
	} catch (Exception e) {
	    httpResponse.setContentType("text/csv");
	    LOG.error(e.getMessage());
	    httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    try {
		httpResponse.getWriter().print(e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/tableMetaDataCSV - END");
    }

    @PostMapping("/dbconsole/columnMetaDataCSV")
    public void columnMetaDataCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> param,
	    HttpServletResponse httpResponse) {
	LOG.info("dbconsole/columnMetaDataCSV - START");
	try {
	    String token = headers.get("token").get(0);
	    LOG.debug("token " + token.toString());
	    if (token != null && !token.isEmpty()) {
		if (tokenValidator.isValid(token)) {

		    List<Map<String, Object>> resultArr = new ArrayList<>();

		    Long appId = param.get("idApp");
		    ListApplications listApplications = validationcheckdao.getdatafromlistapplications(appId);
		    ListDataSchema listdataschema_left = listdatasourcedao
			    .getListDataSchemaForIdDataSchema(listApplications.getIdLeftData()).get(0);
		    ListDataSchema listdataschema_right = listdatasourcedao
			    .getListDataSchemaForIdDataSchema(listApplications.getIdRightData()).get(0);
		    listdataschema_right.getSchemaName();
		    LOG.debug("appId=" + appId);
		    String resultType = "";
		    if (listApplications.getEntityColumn().equalsIgnoreCase("MetaData")) {
			resultType = "MetaData";
		    } else if (listApplications.getEntityColumn().equalsIgnoreCase("Record Count")) {
			resultType = "RecordCount";
		    }

		    SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
			    .getTableNamesForAppIDinResultMasterForSchemaMatching(appId, resultType);

		    String table_Name = null;
		    while (tableNamesForAppIDinResultMaster.next()) {
			LOG.debug("tableName:" + tableNamesForAppIDinResultMaster.getString("Table_Name"));
			if (tableNamesForAppIDinResultMaster.getString("Table_Name")
				.equalsIgnoreCase("schema_matching_" + appId + "_columnsummary")) {
			    table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
			    LOG.debug("Schema MATCHING Table_Name=" + table_Name);

			    SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
			    resultArr = getResult(sqlRowset);
			}
		    }
		    if (resultArr != null && resultArr.size() > 0) {
			httpResponse.setContentType("text/csv");
			String csvFileName = "PrimaryKeyMatchRecords" + LocalDateTime.now() + ".csv";
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
			httpResponse.setHeader(headerKey, headerValue);
			List<String> keyList = Arrays.asList("id", "run", "date", "commonTables", "leftOnlyTables",
				"rightOnlyTables");
			List<String> headerList = Arrays.asList("Id", "Run", "date", "Common Tables",
				"Left Only Tables", "Right Only Tables");
			httpResponse.getWriter().print(toCSVByHeaders1(resultArr, headerList, keyList));
		    } else {
			LOG.error("Records not found.");
			throw new Exception("Records not found.");
		    }
		} else {
		    LOG.error("Token is expired.");
		    throw new Exception("Token is expired.");
		}
	    } else {
		LOG.error("Token is missing in headers.");
		throw new Exception("Please provide token.");
	    }
	} catch (Exception e) {
	    httpResponse.setContentType("text/csv");
	    LOG.error(e.getMessage());
	    httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    try {
		httpResponse.getWriter().print(e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/columnMetaDataCSV - END");
    }
}
