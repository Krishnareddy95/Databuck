package com.databuck.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.bean.RollDataMatchingDashboard;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.IMatchingResultService;
import com.databuck.service.RBACController;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MatchingResultController {

	@Autowired
	public IMatchingResultService iMatchingResultService;
	@Autowired
	MatchingResultDao matchingresultdao;
	@Autowired
	private RBACController rbacController;
	
	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	public JdbcTemplate jdbcTemplate1;

	@Autowired
	IValidationCheckDAO validationcheckdao;
	
	@Autowired
	private Properties appDbConnectionProperties;

	@RequestMapping(value = "/getDataMatchingResults", method = RequestMethod.GET)
	public ModelAndView getMatchingTablesResultTables(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			Map<Long, String> matchingResultTableData = iMatchingResultService.getMatchingResultTable();
			//List<KeyMeasurementMatchingDashboard> dashboardTable = matchingresultdao.getDashboardStatusForKeyMeasurementMatching(matchingResultTableData);
			//System.out.println(dashboardTable);
			List<KeyMeasurementMatchingDashboard> dashboardTable = matchingresultdao.getmatchingDashboard();
			ModelAndView modelAndView = new ModelAndView("dataMatchResultView");
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("dashboardTable", dashboardTable);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/getRollDataMatchingResults", method = RequestMethod.GET)
	public ModelAndView getRollMatchingTablesResultTables(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			Map<Long, String> matchingResultTableData = iMatchingResultService.getRollDataMatchingResultTable();
			List<RollDataMatchingDashboard> dashboardTable = matchingresultdao.getRollmatchingDashboard();
			ModelAndView modelAndView = new ModelAndView("rollDataMatchResultView");
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("dashboardTable", dashboardTable);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getSourceTableDetails", method = RequestMethod.GET)
	public void getDataMatchingResults(HttpServletResponse response, HttpSession session,
			@RequestParam Long appId) {
		JSONObject json = new JSONObject();
		try {
			RollDataMatchingDashboard dashboardTable = matchingresultdao.getRollmatchingDashboardById(appId);
			if (dashboardTable != null) {
				json.put("success", "success");
				json.put("source1", dashboardTable.getSource1());
				json.put("source2", dashboardTable.getSource2());
			}

		} catch (Exception e) {
			e.printStackTrace();
			json.put("failure", "failure");
		}

		try {
			response.getWriter().println(json);
			System.out.println("\n====>response json: " + json);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/getMatchTablesData", method = RequestMethod.GET)
	public ModelAndView getDataMatchingResults(HttpServletRequest request, HttpSession session,
			@RequestParam Long appId ,@RequestParam String source1 , @RequestParam String source2) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			System.out.println("getDataMatchingResults");
			
			System.out.println("=============getDataMatchingResults=== Source1 =============="+source1);
	
			ModelAndView modelAndView = new ModelAndView("showMatchingResults");
			ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching");
			String appName = listApplicationsData.getName();
			System.out.println("appName:" + appName);
			modelAndView.addObject("appName", appName);
			// Long appId = Long.parseLong(request.getParameter("appId"));
			Map<Long, String> matchingResultTableData = iMatchingResultService.getMatchingResultTable();
			SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
			while (laData.next()) {
				modelAndView.addObject("threshold", laData.getDouble("matchingThreshold"));
			}
			SqlRowSet sqlRowSet = iMatchingResultService.getDataMatchingResultsTableNames(appId);

			modelAndView.addObject("source1", source1);
			modelAndView.addObject("source2", source2);
			
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("Left_Only_Count", 0);
			modelAndView.addObject("Right_Only_Count", 0);
			modelAndView.addObject("Unmatched_result_Count", 0);
			modelAndView.addObject("measurement", false);
			
			Object[] getZeroStatusAbdCount = iMatchingResultService
					.getZeroStatusAbdCount("DATA_MATCHING_"+appId+"_SUMMARY", appId);

					modelAndView.addObject("leftStatus", getZeroStatusAbdCount[0]);
					modelAndView.addObject("rightStatus", getZeroStatusAbdCount[1]);
					modelAndView.addObject("leftTotalRecord", getZeroStatusAbdCount[2]);
					modelAndView.addObject("rightTotalRecord", getZeroStatusAbdCount[3]);
			try {
				while (sqlRowSet.next()) {
					if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
						modelAndView.addObject("PrimaryKeyMatching", true);
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")) {
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							modelAndView.addObject("Primary_Left_Only", dataForPrimary);

							dataForPrimary.last();
							modelAndView.addObject("PrimaryLeftOnlyCount", dataForPrimary.getRow());

							modelAndView.addObject("PrimaryLeftOnlyTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("PrimaryRightOnlyCount", dataForPrimary.getRow());
							modelAndView.addObject("Primary_Right_Only", dataForPrimary);
							modelAndView.addObject("PrimaryRightOnlyTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
							System.out.println("PRIMARY_UNMATCHED");
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("Primary_Unmatched_Count", dataForPrimary.getRow());

							modelAndView.addObject("Primary_Unmatched_result", dataForPrimary);
							modelAndView.addObject("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
							System.out.println("PRIMARY_UNMATCHED");
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("Primary_Unmatched_Count", dataForPrimary.getRow());
							modelAndView.addObject("Primary_Unmatched_result", dataForPrimary);
							modelAndView.addObject("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
							modelAndView.addObject("idApp", appId);
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED_ANAMOLY")) {
							modelAndView.addObject("primaryUnMatchedAnamoly", sqlRowSet.getString("Table_Name"));
							Object[] dataAndCount = iMatchingResultService
									.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
							modelAndView.addObject("primary_Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
							modelAndView.addObject("primary_Unmatched_Anamoly_result", dataAndCount[0]);
							modelAndView.addObject("primary_Unmatched_Anamoly_result_Count", dataAndCount[1]);
							modelAndView.addObject("idApp", appId);
						}
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Left_Only")) {
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Left_Only", dataAndCount[0]);
						modelAndView.addObject("Left_Only_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("leftOnlyTableName", sqlRowSet.getString("Table_Name"));
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Right_Only")) {
						modelAndView.addObject("rightOnlyTableName", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Right_Only", dataAndCount[0]);
						modelAndView.addObject("Right_Only_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						System.out.println(" dataAndCount[1]:" + dataAndCount[1]);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
						modelAndView.addObject("unMatchedTableName", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Unmatched_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("Unmatched_result", dataAndCount[0]);
						modelAndView.addObject("Unmatched_result_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("measurement", true);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_ANAMOLY")) {
						modelAndView.addObject("unMatchedAnamoly", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("Unmatched_Anamoly_result", dataAndCount[0]);
						modelAndView.addObject("Unmatched_Anamoly_result_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("measurement", true);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
						List<DataMatchingSummary> dmSummaryData = iMatchingResultService
								.getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
						Map<String, String> dataFromDataMatchingSummary = iMatchingResultService
								.getDataFromDataMatchingSummary(sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("dataMatchingMap", dataFromDataMatchingSummary);
						modelAndView.addObject("dmSummaryData", dmSummaryData);
						String tableName = sqlRowSet.getString("Table_Name");
						
						List<DataMatchingSummary> unmatchedGraph = matchingresultdao.getUnmatchedGraph(tableName);
						List<DataMatchingSummary> leftGraph = matchingresultdao.getLeftGraph(tableName);
						List<DataMatchingSummary> rightGraph = matchingresultdao.getRightGraph(tableName);
						modelAndView.addObject("unmatchedGraph", unmatchedGraph);
						System.out.println("${unmatchedGraph}" + unmatchedGraph);
						modelAndView.addObject("leftGraph", leftGraph);
						modelAndView.addObject("rightGraph", rightGraph);
						modelAndView.addObject("idApp", appId);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_GROUPBY")) {
						System.out.println("UNMATCHED_GROUPBY");
						modelAndView.addObject("UnmatchedGroupBy_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("unMatchedGroupByTableName", sqlRowSet.getString("Table_Name"));
						SqlRowSet unmatchedGroupbyTableData = iMatchingResultService
								.getUnmatchedGroupbyTableData(sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("unmatchedGroupbyTableData", unmatchedGroupbyTableData);
						unmatchedGroupbyTableData.last();
						modelAndView.addObject("unmatchedGroupbyRecordCount", unmatchedGroupbyTableData.getRow());
						unmatchedGroupbyTableData.beforeFirst();
						modelAndView.addObject("idApp", appId);
					}
				}
				if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
					modelAndView.addObject("RecordCountMatching", true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/getRollDataMatchTablesData", method = RequestMethod.GET)
	public ModelAndView getRollDataMatchingResults(HttpServletRequest request, HttpSession session,
			@RequestParam Long appId, @RequestParam String source1, @RequestParam String source2) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("=============getRollDataMatchingResults=== Source1 ==============" + source1);

			ModelAndView modelAndView = new ModelAndView("showRollMatchingResults");
			ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching");
			modelAndView.addObject("appId", appId);
			
			long rollTargetSchemaId = listApplicationsData.getRollTargetSchemaId();
			modelAndView.addObject("rollTargetSchemaId", rollTargetSchemaId);
			
			/*
			 * Abbive requirement:
			 * Adding this property to connect to AverUI tool
			 */
			String averReportUILink = appDbConnectionProperties.getProperty("aver.report.link");
			modelAndView.addObject("averReportUILink", averReportUILink);
			
			String appName = listApplicationsData.getName();
			System.out.println("appName:" + appName);
			modelAndView.addObject("appName", appName);

			Map<Long, String> matchingResultTableData = iMatchingResultService.getRollDataMatchingResultTable();
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("measurement", true);
			modelAndView.addObject("source1", source1);
			modelAndView.addObject("source2", source2);
			modelAndView.addObject("Left_Only_Count", 0);
			modelAndView.addObject("Right_Only_Count", 0);
			modelAndView.addObject("Unmatched_result_Count", 0);
			SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
			while (laData.next()) {
				modelAndView.addObject("threshold", laData.getDouble("matchingThreshold"));
			}

			SqlRowSet sqlRowSet = iMatchingResultService.getRollDataMatchingResultsTableNames(appId);

			Object[] getZeroStatusAbdCount = iMatchingResultService
					.getZeroStatusAbdCount("DATA_MATCHING_" + appId + "_SUMMARY", appId);

			modelAndView.addObject("leftStatus", getZeroStatusAbdCount[0]);
			modelAndView.addObject("rightStatus", getZeroStatusAbdCount[1]);
			modelAndView.addObject("leftTotalRecord", getZeroStatusAbdCount[2]);
			modelAndView.addObject("rightTotalRecord", getZeroStatusAbdCount[3]);
			try {
				while (sqlRowSet.next()) {
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
						// Get the summary results from Target 
						
						String tableName = sqlRowSet.getString("Table_Name");
						
						SqlRowSet summryData = iMatchingResultService.getRollDataSummary(tableName, rollTargetSchemaId);
						
						long summryCount = iMatchingResultService.getRollDataSummaryCount(tableName, rollTargetSchemaId);
						
						modelAndView.addObject("rollSummaryTableName", tableName);
						modelAndView.addObject("rollSummary_Table_Name", tableName);
						modelAndView.addObject("rollSummary_result", summryData);
						modelAndView.addObject("rollSummary_result_Count", summryCount);
						modelAndView.addObject("idApp", appId);
					}
					
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
						List<DataMatchingSummary> dmSummaryData = iMatchingResultService
								.getDataFromDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
						Map<String, String> dataFromDataMatchingSummary = iMatchingResultService
								.getDataFromDataMatchingSummary(sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("dataMatchingMap", dataFromDataMatchingSummary);
						modelAndView.addObject("dmSummaryData", dmSummaryData);
						String tableName = sqlRowSet.getString("Table_Name");
						List unmatchedGraph = matchingresultdao.getUnmatchedGraph(tableName);
						List<DataMatchingSummary> leftGraph = matchingresultdao.getLeftGraph(tableName);
						List<DataMatchingSummary> rightGraph = matchingresultdao.getLeftGraph(tableName);
						modelAndView.addObject("unmatchedGraph", unmatchedGraph);
						System.out.println("${unmatchedGraph}" + unmatchedGraph);
						modelAndView.addObject("leftGraph", leftGraph);
						modelAndView.addObject("rightGraph", rightGraph);
						modelAndView.addObject("idApp", appId);
					}
					
				}
				if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
					modelAndView.addObject("RecordCountMatching", true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");

	}
	
	@RequestMapping(value = "/getRollDMSummaryData")
	public void getRollDMSummaryData(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		System.out.println("\n=============getRollDMSummaryData ==============");

		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String tableName = request.getParameter("tableName");
			String appIdStr = request.getParameter("appId");
			long appId = Long.parseLong(appIdStr);
			System.out.println("tableName=" + tableName);
			System.out.println("appId=" + appId);
			
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
		}
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		out.print(result);
	}
	
	@RequestMapping(value = "/downloadRollDataCsv")
	public void downloadRollDataCsv(HttpServletRequest request, HttpServletResponse response) {
		String tableName = request.getParameter("tableName");
		String rollTargetSchemaId = request.getParameter("rollTargetSchemaId");

		try {
			long r_trgtSchemaId = Long.parseLong(rollTargetSchemaId);
			SqlRowSet result = iMatchingResultService.downloadRollDataSummary(tableName, r_trgtSchemaId);

			if (result != null) {
				int ncols = result.getMetaData().getColumnCount();

				OutputStream outStream = response.getOutputStream();

				// set content attributes for the response
				String mimeType = "application/octet-stream";
				response.setContentType(mimeType);

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", tableName + ".csv");
				response.setHeader(headerKey, headerValue);

				StringBuilder headerLine = new StringBuilder();
				for (int j = 1; j < (ncols + 1); j++) {
					headerLine.append(result.getMetaData().getColumnName(j));
					if (j < ncols)
						headerLine.append(",");
				}
				outStream.write(headerLine.toString().getBytes());
				outStream.write("\n".getBytes());

				while (result.next()) {
					StringBuilder line = new StringBuilder();

					for (int k = 1; k < (ncols + 1); k++) {

						line.append(result.getString(k));

						if (k < ncols)
							line.append(",");
					}
					outStream.write(line.toString().getBytes());
					outStream.write("\n".getBytes());
				}

				outStream.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// for primary key matching getPrimaryKeyMatchingResultTable
	
	@RequestMapping(value = "/getPrimaryKeyMatchingResultsDetails", method = RequestMethod.GET)
	public ModelAndView getPrimaryKeyMatchingResultTableDetails(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		System.out.println("@@@@@@@@@@@@@@@@@@@In getPrimaryKeyMatchingResultsDetails ");
		if (rbac) {
			Map<Long, String> matchingResultTableData = iMatchingResultService.getPrimaryKeyMatchingResultTable();
			//List<KeyMeasurementMatchingDashboard> dashboardTable = matchingresultdao.getDashboardStatusForKeyMeasurementMatching(matchingResultTableData);
			//System.out.println(dashboardTable);
			List<KeyMeasurementMatchingDashboard> dashboardTable = matchingresultdao.getPrimaryKeyMatchingDashboard();
			ModelAndView modelAndView = new ModelAndView("primaryKeyMatchResultView");
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("dashboardTable", dashboardTable);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Primary Key Matching");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/loadPrimaryKeyMatchingResultsTable", method = RequestMethod.POST, produces = "application/json")
	public void loadPrimaryKeyMatchingResultsTable(HttpSession oSession, HttpServletResponse oResponse, @RequestParam String MatchingResultsData) {
		JSONObject oJsonResponse = new JSONObject();
		JSONObject oWrapperData = new JSONObject(MatchingResultsData);

		try {
			oJsonResponse = loadPrimaryKeyMatchingResultsTableData(oSession, oWrapperData);

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}
	
	private JSONObject loadPrimaryKeyMatchingResultsTableData(HttpSession oSession, JSONObject oWrapperData) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;

		String sDataSql, sDataViewList;
		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		try {
			/* Put main data entity list onto return JSON */
			sDataSql = "";
			sDataSql = String.format("select * from DATA_MATCHING_%1$s_MATCHED;", oWrapperData.getInt("IdApp"));

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate1, sDataSql,  new String[] {}, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	@RequestMapping(value = "/getPrimaryKeyMatchTablesData", method = RequestMethod.GET)
	public ModelAndView getPrimaryKeyMatchingResults(HttpServletRequest request, HttpSession session,
			@RequestParam Long appId ,@RequestParam String source1 , @RequestParam String source2) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			System.out.println("getPrimaryKeyMatchingResults");
			
			System.out.println("=============getPrimaryKeyMatchingResults=== Source1 =============="+source1);
	
			ModelAndView modelAndView = new ModelAndView("showPrimaryKeyMatchingResults");
			ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(appId);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Primary Key Matching");
			String appName = listApplicationsData.getName();
			System.out.println("appName:" + appName);
			modelAndView.addObject("appName", appName);
			modelAndView.addObject("appId",appId);
			String columnsData = matchingresultdao.getPrimaryKeyMatchingResultTableColumns(appId);
			System.out.println("\ncolumnsData: " + columnsData);
			modelAndView.addObject("columnsData", columnsData);
			// Long appId = Long.parseLong(request.getParameter("appId"));
			Map<Long, String> matchingResultTableData = iMatchingResultService.getPrimaryKeyMatchingResultTable();
			SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(appId);
			while (laData.next()) {
				modelAndView.addObject("threshold", laData.getDouble("matchingThreshold"));
				if (laData.getString("fileNameValidation").equalsIgnoreCase("Y")) {
					System.out.println("measurement is true");
					modelAndView.addObject("measurement", true);
				}
			}
			SqlRowSet sqlRowSet = iMatchingResultService.getPrimaryKeyMatchingResultsTableNames(appId);

			modelAndView.addObject("source1", source1);
			modelAndView.addObject("source2", source2);
			
			modelAndView.addObject("matchingResultTableData", matchingResultTableData);
			modelAndView.addObject("Left_Only_Count", 0);
			modelAndView.addObject("Right_Only_Count", 0);
			modelAndView.addObject("Unmatched_result_Count", 0);
			Object[] getZeroStatusAbdCountForPrimaryKeyMatchingCount = iMatchingResultService
					.getZeroStatusAbdCountForPrimaryKeyMatching("DATA_MATCHING_"+appId+"_SUMMARY", appId);

					modelAndView.addObject("leftStatus", getZeroStatusAbdCountForPrimaryKeyMatchingCount[0]);
					modelAndView.addObject("rightStatus", getZeroStatusAbdCountForPrimaryKeyMatchingCount[1]);
					modelAndView.addObject("leftTotalRecord", getZeroStatusAbdCountForPrimaryKeyMatchingCount[2]);
					modelAndView.addObject("rightTotalRecord", getZeroStatusAbdCountForPrimaryKeyMatchingCount[3]);
			try {
				while (sqlRowSet.next()) {
					if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
						modelAndView.addObject("PrimaryKeyMatching", true);
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_LEFT")) {
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							modelAndView.addObject("Primary_Left_Only", dataForPrimary);

							dataForPrimary.last();
							modelAndView.addObject("PrimaryLeftOnlyCount", dataForPrimary.getRow());

							modelAndView.addObject("PrimaryLeftOnlyTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_RIGHT")) {
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("PrimaryRightOnlyCount", dataForPrimary.getRow());
							modelAndView.addObject("Primary_Right_Only", dataForPrimary);
							modelAndView.addObject("PrimaryRightOnlyTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
							System.out.println("PRIMARY_UNMATCHED");
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("Primary_Unmatched_Count", dataForPrimary.getRow());

							modelAndView.addObject("Primary_Unmatched_result", dataForPrimary);
							modelAndView.addObject("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED")) {
							System.out.println("PRIMARY_UNMATCHED");
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("Primary_Unmatched_Count", dataForPrimary.getRow());
							modelAndView.addObject("Primary_Unmatched_result", dataForPrimary);
							modelAndView.addObject("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
							modelAndView.addObject("idApp", appId);
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_KEY_MATCHED")) {
							System.out.println("PRIMARY_KEY_MATCHED");
							SqlRowSet dataForPrimary = iMatchingResultService
									.getDataForPrimary(sqlRowSet.getString("Table_Name"), appId);
							dataForPrimary.last();
							modelAndView.addObject("Primary_Unmatched_Count", dataForPrimary.getRow());
							modelAndView.addObject("Primary_Unmatched_result", dataForPrimary);
							modelAndView.addObject("PrimaryUnMatchedTableName", sqlRowSet.getString("Table_Name"));
							modelAndView.addObject("idApp", appId);
						}
						if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("PRIMARY_UNMATCHED_ANAMOLY")) {
							modelAndView.addObject("primaryUnMatchedAnamoly", sqlRowSet.getString("Table_Name"));
							Object[] dataAndCount = iMatchingResultService
									.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
							modelAndView.addObject("primary_Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
							modelAndView.addObject("primary_Unmatched_Anamoly_result", dataAndCount[0]);
							modelAndView.addObject("primary_Unmatched_Anamoly_result_Count", dataAndCount[1]);
							modelAndView.addObject("idApp", appId);
						}
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Left_Only")) {
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Left_Only", dataAndCount[0]);
						modelAndView.addObject("Left_Only_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("leftOnlyTableName", sqlRowSet.getString("Table_Name"));
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Right_Only")) {
						modelAndView.addObject("rightOnlyTableName", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Right_Only", dataAndCount[0]);
						modelAndView.addObject("Right_Only_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
						System.out.println(" dataAndCount[1]:" + dataAndCount[1]);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("Unmatched_result")) {
						modelAndView.addObject("unMatchedTableName", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Unmatched_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("Unmatched_result", dataAndCount[0]);
						modelAndView.addObject("Unmatched_result_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_ANAMOLY")) {
						modelAndView.addObject("unMatchedAnamoly", sqlRowSet.getString("Table_Name"));
						Object[] dataAndCount = iMatchingResultService
								.getDataFromResultTable(sqlRowSet.getString("Table_Name"), appId);
						modelAndView.addObject("Unmatched_Anamoly_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("Unmatched_Anamoly_result", dataAndCount[0]);
						modelAndView.addObject("Unmatched_Anamoly_result_Count", dataAndCount[1]);
						modelAndView.addObject("idApp", appId);
					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("summary")) {
						List<PrimaryMatchingSummary> dmPrimarySummaryData = iMatchingResultService
								.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(sqlRowSet.getString("Table_Name"));
						System.out.println("@@@@@@@@@@@dmPrimarySummaryData ==>"+dmPrimarySummaryData.toString().indexOf(4));
						
						Map<String, String> dataFromPrimaryKeyMatchingSummary = iMatchingResultService
								.getDataFromPrimaryKeyDataMatchingSummary(sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("primaryKeyMatchingMap", dataFromPrimaryKeyMatchingSummary);
						modelAndView.addObject("dmPrimarySummaryData", dmPrimarySummaryData);
						String tableName = sqlRowSet.getString("Table_Name");
						List unmatchedGraph = matchingresultdao.getUnmatchedGraphForPrimaryKeyMatching(tableName);
						List<PrimaryMatchingSummary> leftGraph = matchingresultdao.getLeftGraphForPrimaryKeyMatching(tableName);
						List<PrimaryMatchingSummary> rightGraph = matchingresultdao.getLeftGraphForPrimaryKeyMatching(tableName);
						modelAndView.addObject("unmatchedGraph", unmatchedGraph);
						System.out.println("${unmatchedGraph}" + unmatchedGraph);
						modelAndView.addObject("leftGraph", leftGraph);
						modelAndView.addObject("rightGraph", rightGraph);
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("Unmatched_result_Count", dataFromPrimaryKeyMatchingSummary.get("unMatchedCount"));


						modelAndView.addObject("PrimaryLeftOnlyCount", dataFromPrimaryKeyMatchingSummary.get("leftOnlyCount"));
						String leftOnlyTableName="leftOnly";
						modelAndView.addObject("PrimaryLeftOnlyTableName", leftOnlyTableName);

						String rightOnlyTableName="rightOnly";
						modelAndView.addObject("PrimaryRightOnlyCount", dataFromPrimaryKeyMatchingSummary.get("rightOnlyCount"));
						modelAndView.addObject("PrimaryRightOnlyTableName", rightOnlyTableName);


					}
					if (sqlRowSet.getString("Result_Category2").equalsIgnoreCase("UNMATCHED_GROUPBY")) {
						System.out.println("UNMATCHED_GROUPBY");
						modelAndView.addObject("UnmatchedGroupBy_Table_Name", sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("unMatchedGroupByTableName", sqlRowSet.getString("Table_Name"));
						SqlRowSet unmatchedGroupbyTableData = iMatchingResultService
								.getUnmatchedGroupbyTableData(sqlRowSet.getString("Table_Name"));
						modelAndView.addObject("unmatchedGroupbyTableData", unmatchedGroupbyTableData);
						unmatchedGroupbyTableData.last();
						modelAndView.addObject("unmatchedGroupbyRecordCount", unmatchedGroupbyTableData.getRow());
						unmatchedGroupbyTableData.beforeFirst();
						modelAndView.addObject("idApp", appId);
					}
				}
				if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
					modelAndView.addObject("RecordCountMatching", true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	
	
}