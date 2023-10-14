package com.databuck.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.MultiValueMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.dao.DQIGraphDAOI;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.IDashBoardService;
import com.databuck.service.IProjectService;
import com.databuck.service.IValidationService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DashBoardController {
	
	private final String CURRENT_MODULE_NAME = "Dashboard";

	@Autowired
	public IValidationService validationService;

	@Autowired
	public IDashBoardService dashBoardService;

	@Autowired
	public IResultsDAO iResultsDAO;
	
	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	MatchingResultDao matchingresultdao;

	@Autowired
	DQIGraphDAOI dqiGraphDAOI;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	public IValidationDAO ivalidationdao;
	@Autowired
	private RBACController rbacController;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private IProjectService iProjectService;
	
	@Autowired
	public LoginService loginService;

	@RequestMapping(value = "/dashboard_View")
	public ModelAndView dashboardView(HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		ModelAndView oModelViewPage = null;
		boolean oRbac = rbacController.rbac("Results", "R", oSession);

		if ( (oUser == null) || (!oUser.equals("validUser")) || (!oRbac) ) {
			oModelViewPage = new ModelAndView("loginPage");

		} else {
			oModelViewPage = new ModelAndView("resultDashboard");

			Long nProjectId= (Long)oSession.getAttribute("projectId");
			List<Project> aProjList = (List<Project>)oSession.getAttribute("userProjectList");
			List<DataQualityMasterDashboard> aDashboardTable = iResultsDAO.getDataFromDataQualityDashboard(nProjectId, aProjList);
			Project selectedProject = iProjectDAO.getSelectedProject(nProjectId);
			oModelViewPage.addObject("dashboardTable", aDashboardTable);
			oModelViewPage.addObject("projectList", aProjList);
			oModelViewPage.addObject("selectedProject", selectedProject);
			oModelViewPage.addObject("SelectedProjectId", (Long)oSession.getAttribute("projectId"));
			oModelViewPage.addObject("currentSection", "Dashboard");
			oModelViewPage.addObject("currentLink", "View");
		}
		return oModelViewPage;
	}

	@RequestMapping(value = "/getPaginatedResultsList", method = RequestMethod.POST, produces = "application/json")
	public void getPaginatedResultsList(HttpSession oSession, HttpServletRequest oRequest, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		ObjectMapper oObjectMapper = new ObjectMapper();

		Long nProjectId = 0l;
		Project oSelectedProject = null;
		List<Project> aProjectList = null;

		try {
			DateUtility.DebugLog("serverSideDataTableTest 01","Start of controller");

			nProjectId = (Long)oSession.getAttribute("projectId");
			oSelectedProject = iProjectDAO.getSelectedProject(nProjectId);
			//aProjectList = (List<Project>)oSession.getAttribute("userProjectList");
			aProjectList = loginService.getAllDistinctProjectListForUser(oSession);
			
			oJsonResponse.put("SelectedProjectId", nProjectId);
			oJsonResponse.put("AllProjectList", new JSONArray(oObjectMapper.writeValueAsString(aProjectList)));
			oJsonResponse.put("SecurityFlags", getSecurityFlags(CURRENT_MODULE_NAME, oSession));
			

			for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds", "SearchText" }) {
				oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
			}

			oJsonResponse.put("ViewPageDataList", iResultsDAO.getPaginatedResultsJsonData(oPaginationParms));
			DateUtility.DebugLog("serverSideDataTableTest 02",String.format("End of controller, got data sending to client \n%1$s\n", oJsonResponse.get("SecurityFlags")));

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject getSecurityFlags(String sModuleName, HttpSession oSession) {
		String sSecurityAccessFlags = "{ 'Create': %1$s, 'Update': %2$s, 'Delete': %3$s }";

		sSecurityAccessFlags = String.format(sSecurityAccessFlags,
				rbacController.rbac(sModuleName, "C", oSession),
				rbacController.rbac(sModuleName, "U", oSession),
				rbacController.rbac(sModuleName, "D", oSession)
			);
		return new JSONObject(sSecurityAccessFlags);
	}

	@RequestMapping(value = "/dqUniverse")
	public ModelAndView dqUniverse(HttpSession session,HttpServletRequest request, HttpServletResponse response) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

						System.out.println("DQ Universe controller");
			ModelAndView modelAndView = new ModelAndView("dqUniverse");

		/*	SqlRowSet getdatafromresultmaster = ivalidationdao.getdatafromresultmaster();
			Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();

			// Map resultmasterdata = new HashMap<>();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));

				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}


			List<DataQualityMasterDashboard> dashboardTable = iResultsDAO.getDataFromDataQualityDashboard();

			modelAndView.addObject("dashboardTable", dashboardTable);
			modelAndView.addObject("resultmasterdata", resultmasterdata);*/

			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "View");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}



	@RequestMapping(value = "/dashboardMaps")
	public ModelAndView dashboardMaps(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			// System.out.println("schema"+request.getParameter("schema"));
			long idDataSchema = Long.parseLong(request.getParameter("schema"));
			System.out.println("dashboard controller");
			ModelAndView modelAndView = new ModelAndView("dashboardSchemaView");
			System.out.println("iDashBoardService" + dashBoardService);
			List<ListDataSource> listOfDataSources = dashBoardService.getDataFromDataSources(idDataSchema);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("listOfDataSources", listOfDataSources);
			System.out.println("listOfDataSources.size():" + listOfDataSources.size());
			modelAndView.addObject("currentLink", "View");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/refDataColumn", method = RequestMethod.POST)
	public void refDataColumn(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idData,@RequestParam String tablename) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<String> listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinitionForReftables(idData);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(listDataDefinitionColumnNames);
			//System.out.println("jsonInString=" + jsonInString);

			JSONObject displayName = new JSONObject();
			displayName.put("success", jsonInString);
			//System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	@RequestMapping(value = "/updateRefData", method = RequestMethod.POST)
	public void updateRefData(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long row_id ,@RequestParam String col_name,@RequestParam String update_val,@RequestParam String table_name) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String updateVal = iResultsDAO.updateRefTableData(row_id,col_name,update_val,table_name);


		try {
			//String jsonInString = mapper.writeValueAsString(listDataDefinitionColumnNames);
			System.out.println("jsonInString=" + updateVal);

			JSONObject updateV = new JSONObject();
			updateV.put("success", updateVal);
			//System.out.println("displayName=" + displayName);

			response.getWriter().println(updateV);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	@RequestMapping(value = "/insertNewValueToRefTable", method = RequestMethod.POST)
	public void insertNewValueToRefTable(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String columnName,@RequestParam String columnValue,@RequestParam String table_name) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String insertNewRow = iResultsDAO.insertNewValueToRefTable(columnName,columnValue,table_name);


		try {
			//String jsonInString = mapper.writeValueAsString(listDataDefinitionColumnNames);
			System.out.println("jsonInString=" + insertNewRow);

			JSONObject updateV = new JSONObject();
			updateV.put("success", insertNewRow);
			//System.out.println("displayName=" + displayName);

			response.getWriter().println(updateV);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	@RequestMapping(value = "/refDataValue", method = RequestMethod.POST)
	public void refDataValue(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idData,@RequestParam String tablename,@RequestParam String columname) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//long idData = validationcheckdao.getIdDataTFromListApplication(idApp);
		//List<String> listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinitionForReftables(idData);


		List<String> listMicroSegValData = iResultsDAO.getRefTableColValue(idData,columname,tablename);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(listMicroSegValData);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject dGroupVal = new JSONObject();
			dGroupVal.put("success", jsonInString);
			System.out.println("displayName=" + dGroupVal);

			response.getWriter().println(dGroupVal);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}

	/*
	 * @RequestMapping(value = "/dashboard_table") public ModelAndView
	 * dashboardTableView(HttpServletRequest req, HttpSession session) {
	 *
	 * DecimalFormat decimalFormat = new DecimalFormat();
	 * decimalFormat.setRoundingMode(RoundingMode.DOWN);
	 * decimalFormat.setGroupingUsed(true); decimalFormat.setGroupingSize(3);
	 *
	 * Object user = session.getAttribute("user"); System.out.println("user:" +
	 * user); if ((user == null) || (!user.equals("validUser"))) { return new
	 * ModelAndView("loginPage"); } boolean rbac = rbacController.rbac("Results",
	 * "R", session); if (rbac) {
	 *
	 * ModelAndView modelAndView = new ModelAndView("dashboardTableView");
	 * modelAndView.addObject("recordCount", 0);
	 * modelAndView.addObject("averageRecordCount", 0);
	 * modelAndView.addObject("nonNullColumns", 0);
	 * modelAndView.addObject("nonNullColumnsFailed", 0);
	 * modelAndView.addObject("allFields", 0);
	 * modelAndView.addObject("identityFields", 0);
	 * modelAndView.addObject("numberOfRecordsFailed", 0);
	 * modelAndView.addObject("numberofStringColumnsFailed", 0);
	 * modelAndView.addObject("numberofNumericalColumnsFailed", 0); //
	 * modelAndView.addObject("numberOfRecordsFailed", 0);
	 * modelAndView.addObject("recordCountAnomalyGraphValues", "");
	 * modelAndView.addObject("TransactionSetTable", "");
	 *
	 * modelAndView.addObject("toDate", (String)session.getAttribute("toDate"));
	 * modelAndView.addObject("fromDate", (String)session.getAttribute("fromDate"));
	 * if((int)session.getAttribute("RunFilter") == 0){
	 * modelAndView.addObject("Run", ""); } else{ modelAndView.addObject("Run",
	 * (int)session.getAttribute("RunFilter")); }
	 *
	 *
	 * List<DATA_QUALITY_Transactionset_sum_A1> transactionSetTable = null; long
	 * idApp = Long.parseLong(req.getParameter("idApp"));
	 * System.out.println("idApp======" + idApp); SqlRowSet
	 * tableNamesFromResultMasterTable =
	 * iResultsDAO.getTableNamesFromResultMasterTable(idApp); Long
	 * nonNullColumnsFailed = 0l; Long numberofNumericalColumnsFailed = 0l; Long
	 * numberofStringColumnsFailed = 0l; Long recordAnomalyTotal = 0l; Long
	 * numberOfRecordsFailed = 0l; ListApplications listApplicationsData =
	 * validationcheckdao.getdatafromlistapplications(idApp); Long idData = 0l;
	 * SqlRowSet idDataAndAppName =
	 * iResultsDAO.getIdDataAndAppNameFromListApplications(idApp); while
	 * (idDataAndAppName.next()) { idData = idDataAndAppName.getLong("idData");
	 * modelAndView.addObject("applicationName",
	 * idDataAndAppName.getString("name")); }
	 *
	 *
	 * int maxRun = 1;
	 * if(listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")
	 * ){ maxRun = jdbcTemplate1.queryForObject(
	 * "select max(Run) from DATA_QUALITY_" + listApplicationsData.getIdApp() +
	 * "_Transactionset_sum_dgroup where "+
	 * "Date=(select max(Date) from DATA_QUALITY_" + listApplicationsData.getIdApp()
	 * + "_Transactionset_sum_dgroup)", Integer.class); }else{ maxRun =
	 * jdbcTemplate1.queryForObject( "select max(Run) from DATA_QUALITY_" +
	 * listApplicationsData.getIdApp() + "_Transactionset_sum_A1 where Date="+
	 * "(select max(Date) from DATA_QUALITY_" + listApplicationsData.getIdApp() +
	 * "_Transactionset_sum_A1)", Integer.class); }
	 *
	 * Date maxDate = null;
	 * if(listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")
	 * ){ maxDate = jdbcTemplate1.queryForObject(
	 * "select max(Date) from DATA_QUALITY_" + listApplicationsData.getIdApp() +
	 * "_Transactionset_sum_dgroup", Date.class); }else{ maxDate =
	 * jdbcTemplate1.queryForObject("select max(Date) from DATA_QUALITY_" +
	 * listApplicationsData.getIdApp() + "_Transactionset_sum_A1", Date.class); }
	 *
	 * SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd"); String strMaxDate =
	 * dt.format(maxDate);
	 *
	 * System.out.println("idData=" + idData); while
	 * (tableNamesFromResultMasterTable.next()) {
	 * modelAndView.addObject("DataQualityRulesResultsTableName",
	 * "//csvFiles//"+idApp+"//"+strMaxDate+"//"+ maxRun+"//rules-output"); if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").toLowerCase().
	 * endsWith("_rules")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name");
	 * System.out.println("rules_results=" + tableName); SqlRowSet
	 * DATA_QUALITY_RULES = iResultsDAO.getRulesTableData(tableName); if
	 * (DATA_QUALITY_RULES != null) { modelAndView.addObject("DataQualityRules",
	 * DATA_QUALITY_RULES); modelAndView.addObject("DataQualityRulesTrue",
	 * "DataQualityRulesTrue"); }
	 * modelAndView.addObject("DataQualityRulesTableName", tableName); } if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").toLowerCase().
	 * contains("sum_dgroup")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name"); SqlRowSet
	 * frequencyUpdateDateTableData =
	 * iResultsDAO.getFrequencyUpdateDateTableData(tableName);
	 * System.out.println("frequencyUpdateDateTableData=" +
	 * frequencyUpdateDateTableData); if (frequencyUpdateDateTableData != null) {
	 * modelAndView.addObject("frequencyUpdateDateTableData",
	 * frequencyUpdateDateTableData);
	 * modelAndView.addObject("frequencyUpdateDateTableDataTrue",
	 * "frequencyUpdateDateTableDataTrue");
	 *
	 * } // Group Equality String groupEqualityStatus =
	 * iResultsDAO.getDashBoardStatusForGroupEquality(idApp, tableName);
	 * modelAndView.addObject("groupEqualityStatus", groupEqualityStatus);
	 * System.out.println("groupEqualityStatus=" + groupEqualityStatus);
	 * modelAndView.addObject("sum_dgroup_tableName", tableName); } if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").contains(
	 * "_Transactionset_sum_A1")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name");
	 * modelAndView.addObject("Transactionset_sum_A1_TableName", tableName); //
	 * graph List recordCountAnomalyGraphValues =
	 * iResultsDAO.getRecordCountAnomalyGraphValues(tableName);
	 * modelAndView.addObject("recordCountAnomalyGraphValues",
	 * recordCountAnomalyGraphValues); } if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").toLowerCase()
	 * .contains("_transactionset_sum_a1")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name"); Long recordCount =
	 * iResultsDAO.getRecordCount(listApplicationsData, idApp);
	 * System.out.println("recordCount=" + recordCount); Long averageRecordCount =
	 * iResultsDAO.getAverageRecordCount(listApplicationsData, idApp);
	 * System.out.println("averageRecordCount=" + averageRecordCount);
	 * recordAnomalyTotal = iResultsDAO.getRecordAnomalyTotal(tableName, idApp);
	 * System.out.println("recordAnomalyTotal=" + recordAnomalyTotal);
	 * modelAndView.addObject("recordAnomalyTotal",
	 * decimalFormat.format(recordAnomalyTotal));
	 * modelAndView.addObject("recordCount", decimalFormat.format(recordCount));
	 * modelAndView.addObject("averageRecordCount",
	 * decimalFormat.format(averageRecordCount)); // passfail
	 * List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderStatus =
	 * iResultsDAO .getfileNameandcolumnOrderValidationStatus(tableName); //
	 * System.out.println("fileNameandcolumnOrderStatussize="+
	 * fileNameandcolumnOrderStatus.size()); //
	 * System.out.println(fileNameandcolumnOrderStatus.get(0)); //
	 * System.out.println(fileNameandcolumnOrderStatus.get(1));
	 * System.out.println("fileNameandcolumnOrderStatus=" +
	 * fileNameandcolumnOrderStatus);
	 * modelAndView.addObject("fileNameandcolumnOrderStatus",
	 * fileNameandcolumnOrderStatus); } if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").toLowerCase()
	 * .contains("_transaction_summary")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name"); Long allFields =
	 * iResultsDAO.getAllFields(tableName); System.out.println("allFields=" +
	 * allFields);
	 *
	 * Long identityFields = iResultsDAO.getIdentityFields(tableName);
	 * System.out.println("identityFields=" + identityFields);
	 * modelAndView.addObject("allFields", decimalFormat.format(allFields));
	 * modelAndView.addObject("identityFields",
	 * decimalFormat.format(identityFields)); } if
	 * (tableNamesFromResultMasterTable.getString("Table_Name").toLowerCase().
	 * contains("_column_summary")) { String tableName =
	 * tableNamesFromResultMasterTable.getString("Table_Name");
	 * numberofStringColumnsFailed =
	 * iResultsDAO.getnumberofStringColumnsFailed(tableName,listApplicationsData);
	 * System.out.println("numberofStringColumnsFailed=" +
	 * numberofStringColumnsFailed); numberofNumericalColumnsFailed =
	 * iResultsDAO.getnumberofNumericalColumnsFailed(tableName,listApplicationsData)
	 * ; System.out.println("numberofNumericalColumnsFailed=" +
	 * numberofNumericalColumnsFailed);
	 * modelAndView.addObject("numberofStringColumnsFailed",
	 * decimalFormat.format(numberofStringColumnsFailed));
	 * modelAndView.addObject("numberofNumericalColumnsFailed",
	 * decimalFormat.format(numberofNumericalColumnsFailed)); } }
	 *
	 * // passfail List<DATA_QUALITY_Transactionset_sum_A1>
	 * fileNameandcolumnOrderStatus = iResultsDAO
	 * .getfileNameandcolumnOrderValidationStatus("DATA_QUALITY_" + idApp +
	 * "_Transactionset_sum_A1");
	 * modelAndView.addObject("fileNameandcolumnOrderStatus",
	 * fileNameandcolumnOrderStatus); modelAndView.addObject("idApp", idApp);
	 * modelAndView.addObject("currentSection", "Dashboard");
	 * modelAndView.addObject("currentLink", "View"); // dashboard missing dates if
	 * (listApplicationsData.getIncrementalMatching() != null &&
	 * listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
	 * modelAndView.addObject("incrementalMatching",
	 * listApplicationsData.getIncrementalMatching()); String missingDates =
	 * iResultsDAO.getMissingDatesFromTransactionset_sum_A1(idApp);
	 * modelAndView.addObject("missingDates", missingDates); }
	 * modelAndView.addObject("listApplicationsData", listApplicationsData); Map
	 * listdftranruleData = iResultsDAO.getdatafromlistdftranrule(idApp);
	 * modelAndView.addObject("listdftranruleData", listdftranruleData); String
	 * recordCountAnomaly = listApplicationsData.getRecordCountAnomaly(); // Record
	 * Anomaly Status on Dashboard // Record Count Fingerprint // RCA Dashboard
	 * String rcaStatus = iResultsDAO.getDashboardStatusForRCA(idApp,
	 * recordCountAnomaly); modelAndView.addObject("recordCountStatus", rcaStatus);
	 *
	 * // nullcountStatus Dashboard String nullcountStatus = "";
	 * modelAndView.addObject("nullCountScore", 0.0); if
	 * (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
	 * nullcountStatus = iResultsDAO.getDashboardStatusForNullCount(idApp);
	 * modelAndView.addObject("nullCountStatus", nullcountStatus); Long
	 * nonNullColumns = iResultsDAO.getnonNullColumns(idData);
	 * modelAndView.addObject("nonNullColumns",
	 * decimalFormat.format(nonNullColumns)); nonNullColumnsFailed =
	 * iResultsDAO.getnonNullColumnsFailed("DATA_QUALITY_" + idApp +
	 * "_Column_Summary", listApplicationsData);
	 * System.out.println("nonNullColumnsFailed=" + nonNullColumnsFailed);
	 * modelAndView.addObject("nonNullColumnsFailed",
	 * decimalFormat.format(nonNullColumnsFailed)); } // if
	 * (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) { Long
	 * numberofNumericalColumnsYes =
	 * iResultsDAO.getNumberofNumericalColumnsYes(idData);
	 * System.out.println("numberofNumericalColumnsYes=" +
	 * numberofNumericalColumnsYes);
	 * modelAndView.addObject("numberofNumericalColumnsYes",
	 * decimalFormat.format(numberofNumericalColumnsYes)); //
	 * numericalFieldStatsStatus Dashboard String numericalFieldStatus =
	 * iResultsDAO.getDashboardStatusFornumericalFieldStatsStatus(idApp);
	 * modelAndView.addObject("numericalFieldStatsStatus", numericalFieldStatus); }
	 * // stringFieldStatsStatus Dashboard if
	 * (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) { String
	 * stringFieldStatus =
	 * iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);
	 * modelAndView.addObject("stringFieldStatsStatus", stringFieldStatus); Long
	 * numberofStringColumnsYes = iResultsDAO.getNumberofStringColumnsYes(idData);
	 * System.out.println("numberofStringColumnsYes=" + numberofStringColumnsYes);
	 * modelAndView.addObject("numberofStringColumnsYes",
	 * decimalFormat.format(numberofStringColumnsYes)); } // recordAnomalyStatus
	 * Dashboard if
	 * (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) { String
	 * recordAnomalystatus =
	 * iResultsDAO.getDashboardStatusForRecordAnomalyStatus(idApp);
	 * System.out.println("recordAnomalystatus=" + recordAnomalystatus);
	 * modelAndView.addObject("recordAnomalyStatus", recordAnomalystatus); //
	 * numberOfRecordsFailed numberOfRecordsFailed = iResultsDAO
	 * .getNumberOfRecordsFailed("DATA_QUALITY_" + idApp + "_Transactionset_sum_A1",
	 * idApp); System.out.println("numberOfRecordsFailed=" + numberOfRecordsFailed);
	 * modelAndView.addObject("numberOfRecordsFailed",
	 * decimalFormat.format(numberOfRecordsFailed)); String recordAnomalyNewStatus =
	 * iResultsDAO.getStatusFromRecordAnomalyTable(idApp);
	 * modelAndView.addObject("recordAnomalyStatus", recordAnomalyNewStatus); } //
	 * dataDriftStatus Dashboard if
	 * (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) { String
	 * dataDriftStatus = iResultsDAO.getDashboardStatusForDataDriftStatus(idApp);
	 * modelAndView.addObject("dataDriftStatus", dataDriftStatus);
	 * System.out.println("dataDriftStatus=" + dataDriftStatus); } if
	 * (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) { String
	 * timelinessCheckStatus =
	 * iResultsDAO.getDashboardStatusForTimelinessCheck(idApp);
	 * modelAndView.addObject("TimelinessCheckStatus", timelinessCheckStatus); Long
	 * timelinessFailedRecordCount =
	 * iResultsDAO.getFailedRecordCountForTimelinessCheck(idApp);
	 * modelAndView.addObject("TimelinessFailedRecordCount",
	 * decimalFormat.format(timelinessFailedRecordCount)); Long
	 * timelinessTotalRecordCount = iResultsDAO.getRecordCount(listApplicationsData,
	 * idApp); modelAndView.addObject("TimelinessTotalRecordCount",
	 * decimalFormat.format(timelinessTotalRecordCount));
	 * modelAndView.addObject("TimelinessCheckStatus", timelinessCheckStatus);
	 * System.out.println("TimelinessCheckStatus=" + timelinessCheckStatus); }
	 * Map<String, String> tranRuleMap =
	 * iResultsDAO.getDataFromListDFTranRuleForMap(idApp); // allFieldsStatus
	 * Dashboard System.out.println("testing--->"+tranRuleMap.get("all"));
	 * modelAndView.addObject("allFieldsScore", 0.0); if
	 * (tranRuleMap.get("all").equalsIgnoreCase("Y")) { String allFieldsStatus =
	 * iResultsDAO.getDashboardStatusForAllFieldsStatus(idApp);
	 * modelAndView.addObject("allFieldsStatus", allFieldsStatus);
	 * System.out.println("allFieldsStatus=" + allFieldsStatus); } //
	 * identityfieldsStatus Dashboard modelAndView.addObject("identityFieldsScore",
	 * 0.0); if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) { String
	 * identityfieldsStatus =
	 * iResultsDAO.getDashboardStatusForIdentityfieldsStatus(idApp);
	 * modelAndView.addObject("identityfieldsStatus", identityfieldsStatus); } //
	 * Summary of Last Run (Date) for Validation Check String dateForDashboard =
	 * iResultsDAO.getDateForSummaryOfLastRun(idApp);
	 * modelAndView.addObject("dateForDashboard", dateForDashboard); // Score
	 *
	 * boolean calculateScore = iResultsDAO.checkRunIntheResultTableForScore(idApp,
	 * recordCountAnomaly); System.out.println("calculateScore=" + calculateScore);
	 *
	 * // calculateScore int totalCount = 0; Double totalDQI = 0.0; if
	 * (calculateScore) { DecimalFormat df = new DecimalFormat("#.00"); //
	 * RecordAnomaly String recordAnomalyScore =
	 * iResultsDAO.CalculateScoreForRecordAnomaly(idData, idApp, recordCountAnomaly,
	 * rcaStatus, listApplicationsData); if (recordAnomalyScore.contains("∞")) {
	 * recordAnomalyScore = "0.0"; } modelAndView.addObject("recordAnomalyScore",
	 * recordAnomalyScore); totalDQI = totalDQI +
	 * Double.valueOf(recordAnomalyScore); totalCount++;
	 * System.out.println("recordAnomalyScore=" + recordAnomalyScore); // Numerical
	 * Field modelAndView.addObject("numericalFieldScore", 0.0); if
	 * (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) { String
	 * numericalFieldScore = iResultsDAO.CalculateScoreForNumericalField(idData,
	 * idApp, "", listApplicationsData); if (numericalFieldScore.contains("∞")) {
	 * numericalFieldScore = "0"; } modelAndView.addObject("numericalFieldScore",
	 * numericalFieldScore); totalDQI = totalDQI +
	 * Double.valueOf(numericalFieldScore); totalCount++; } // string Field
	 * modelAndView.addObject("stringFieldScore", 0.0); if
	 * (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) { String
	 * stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData, idApp,
	 * "", listApplicationsData); if (stringFieldScore.contains("∞")) {
	 * stringFieldScore = "0"; } modelAndView.addObject("stringFieldScore",
	 * stringFieldScore); totalDQI = totalDQI + Double.valueOf(stringFieldScore);
	 * totalCount++; } // NullCountScore modelAndView.addObject("nullCountScore",
	 * 0.0); if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
	 * String nullCountScore = iResultsDAO.CalculateScoreForNullCount(idData, idApp,
	 * recordCountAnomaly, nullcountStatus, listApplicationsData); if
	 * (nullCountScore.contains("∞")) { nullCountScore = "0"; }
	 * modelAndView.addObject("nullCountScore", nullCountScore); totalDQI = totalDQI
	 * + Double.valueOf(nullCountScore); totalCount++; } // All FieldsScore
	 * modelAndView.addObject("allFieldsScore", 0.0); if
	 * (tranRuleMap.get("all").equalsIgnoreCase("Y")) { String allFieldsScore =
	 * iResultsDAO.CalculateScoreForallFields(idData, idApp, "",
	 * listApplicationsData); if (allFieldsScore.contains("∞")) { allFieldsScore =
	 * "0"; } modelAndView.addObject("allFieldsScore", allFieldsScore); totalDQI =
	 * totalDQI + Double.valueOf(allFieldsScore); totalCount++; } //
	 * identityFieldsScore modelAndView.addObject("identityFieldsScore", 0.0); if
	 * (tranRuleMap.get("identity").equalsIgnoreCase("Y")) { String
	 * identityFieldsScore = iResultsDAO.CalculateScoreForidentityFields(idData,
	 * idApp, "", listApplicationsData); if (identityFieldsScore.contains("∞")) {
	 * identityFieldsScore = "0"; } modelAndView.addObject("identityFieldsScore",
	 * identityFieldsScore); totalDQI = totalDQI +
	 * Double.valueOf(identityFieldsScore); totalCount++; } // Record Fingerprint
	 * modelAndView.addObject("recordFieldScore", 0.0); if
	 * (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) { String
	 * recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(idData,
	 * idApp, "", listApplicationsData); if (recordFieldScore.contains("∞")) {
	 * recordFieldScore = "0"; } modelAndView.addObject("recordFieldScore",
	 * recordFieldScore); totalDQI = totalDQI + Double.valueOf(recordFieldScore);
	 * totalCount++; } // RegexCheck modelAndView.addObject("ruleScoreDF", 0); if
	 * (listApplicationsData.getApplyRules().equalsIgnoreCase("Y")) { Map<String,
	 * String> rulesMap = iResultsDAO.checkRulesTable(listApplicationsData);
	 * System.out.println("rulesMap=" + rulesMap); if (rulesMap.size() >= 2) {
	 * modelAndView.addObject("rulesDashboard", true);
	 * modelAndView.addObject("ruleType", rulesMap.get("ruleType"));
	 * modelAndView.addObject("ruleScoreDF", rulesMap.get("ruleScoreDF")); totalDQI
	 * = totalDQI + Double.valueOf(rulesMap.get("ruleScoreDF")); totalCount++; } }
	 * modelAndView.addObject("dataDriftScore", 0); // datadrift if
	 * (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) { String
	 * dataDriftScore = iResultsDAO.getScoreForDataDrift(listApplicationsData);
	 * System.out.println("dataDriftScore=" + dataDriftScore);
	 * modelAndView.addObject("dataDriftScore", dataDriftScore); totalDQI = totalDQI
	 * + Double.valueOf(dataDriftScore); totalCount++; }
	 * modelAndView.addObject("TimelinessCheckScore", 0); if
	 * (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) { String
	 * timelinessScore = iResultsDAO.CalculateScoreForTimelinessCheck(idApp,
	 * listApplicationsData); System.out.println("TimelinessCheckScore=" +
	 * timelinessScore); modelAndView.addObject("TimelinessCheckScore",
	 * timelinessScore); totalDQI = totalDQI + Double.valueOf(timelinessScore);
	 * totalCount++; } modelAndView.addObject("showDQIEmpty", 0); } else {
	 * System.out.println("showDQIEmpty=null"); //
	 * modelAndView.addObject("showDQIEmpty",null);
	 * modelAndView.addObject("recordAnomalyScore", 0.0);
	 * modelAndView.addObject("numericalFieldScore", 0.0);
	 * modelAndView.addObject("stringFieldScore", 0.0);
	 * modelAndView.addObject("recordFieldScore", 0.0); // Record Fingerprint if
	 * (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) { String
	 * recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(idData,
	 * idApp, "", listApplicationsData); if (recordFieldScore.contains("∞")) {
	 * recordFieldScore = "0"; } modelAndView.addObject("recordFieldScore",
	 * recordFieldScore); totalDQI = totalDQI + Double.valueOf(recordFieldScore);
	 * totalCount++; System.out.println("recordFieldScore=" + recordFieldScore); }
	 * // RegexCheck modelAndView.addObject("ruleScoreDF", 0); if
	 * (listApplicationsData.getApplyRules().equalsIgnoreCase("Y")) { Map<String,
	 * String> rulesMap = iResultsDAO.checkRulesTable(listApplicationsData);
	 * System.out.println("rulesMap=" + rulesMap); if (rulesMap.size() >= 2) {
	 * modelAndView.addObject("rulesDashboard", true);
	 * modelAndView.addObject("ruleType", rulesMap.get("ruleType"));
	 * modelAndView.addObject("ruleScoreDF", rulesMap.get("ruleScoreDF")); totalDQI
	 * = totalDQI + Double.valueOf(rulesMap.get("ruleScoreDF")); totalCount++; } }
	 * modelAndView.addObject("dataDriftScore", 0); if
	 * (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) { String
	 * dataDriftScore = iResultsDAO.getScoreForDataDrift(listApplicationsData);
	 * System.out.println("dataDriftScore=" + dataDriftScore);
	 * modelAndView.addObject("dataDriftScore", dataDriftScore); totalDQI = totalDQI
	 * + Double.valueOf(dataDriftScore); totalCount++; } // NullCountScore
	 * modelAndView.addObject("nullCountScore", 0.0); if
	 * (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) { String
	 * nullCountScore = iResultsDAO.CalculateScoreForNullCount(idData, idApp,
	 * recordCountAnomaly, nullcountStatus, listApplicationsData); if
	 * (nullCountScore.contains("∞")) { nullCountScore = "0"; }
	 * modelAndView.addObject("nullCountScore", nullCountScore); totalDQI = totalDQI
	 * + Double.valueOf(nullCountScore); totalCount++; } // All FieldsScore
	 * modelAndView.addObject("allFieldsScore", 0.0); if
	 * (tranRuleMap.get("all").equalsIgnoreCase("Y")) { String allFieldsScore =
	 * iResultsDAO.CalculateScoreForallFields(idData, idApp, "",
	 * listApplicationsData); if (allFieldsScore.contains("∞")) { allFieldsScore =
	 * "0"; } modelAndView.addObject("allFieldsScore", allFieldsScore); totalDQI =
	 * totalDQI + Double.valueOf(allFieldsScore); totalCount++; } //
	 * identityFieldsScore modelAndView.addObject("identityFieldsScore", 0.0); if
	 * (tranRuleMap.get("identity").equalsIgnoreCase("Y")) { String
	 * identityFieldsScore = iResultsDAO.CalculateScoreForidentityFields(idData,
	 * idApp, "", listApplicationsData); if (identityFieldsScore.contains("∞")) {
	 * identityFieldsScore = "0"; } modelAndView.addObject("identityFieldsScore",
	 * identityFieldsScore); totalDQI = totalDQI +
	 * Double.valueOf(identityFieldsScore); totalCount++; }
	 * modelAndView.addObject("TimelinessCheckScore", 0); if
	 * (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) { String
	 * timelinessScore = iResultsDAO.CalculateScoreForTimelinessCheck(idApp,
	 * listApplicationsData); System.out.println("TimelinessCheckScore=" +
	 * timelinessScore); modelAndView.addObject("TimelinessCheckScore",
	 * timelinessScore); totalDQI = totalDQI + Double.valueOf(timelinessScore);
	 * totalCount++; } } DecimalFormat df = new DecimalFormat("#0.0");
	 * System.out.println("totalDQI=  " + totalDQI + "/" + totalCount); double
	 * avgDQI = totalDQI / totalCount; modelAndView.addObject("totalDQI",
	 * df.format(avgDQI)); System.out.println("avgDQI=" + avgDQI); if (totalDQI <=
	 * 0.0) { modelAndView.addObject("totalDQI", 0); } else if (avgDQI >= 100) {
	 * modelAndView.addObject("totalDQI", 100); } return modelAndView; } else return
	 * new ModelAndView("loginPage"); }
	 */

	@RequestMapping(value = "/getDQIScoresMap", method = RequestMethod.POST)
	public @ResponseBody MultiValueMap populateTableAnalysisData(ModelAndView model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response, @RequestParam String validationCheck,
			@RequestParam Long idApp) {
		System.out.println("validationCheck:::"+validationCheck + "" + idApp);

		// Map<String, Double> map = new LinkedHashMap<String, Double>();
		MultiValueMap map = new MultiValueMap();
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		System.out.println("listApplicationsData:::"+listApplicationsData );

		if (validationCheck.equalsIgnoreCase("Record Count Anomaly")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Record Count Fingerprint");
		} else if (validationCheck.equalsIgnoreCase("Null Count")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Completeness");
		} else if (validationCheck.equalsIgnoreCase("Primary Key Duplicate")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Uniqueness -Primary Keys");
		} else if (validationCheck.equalsIgnoreCase("User Selected Fields Duplicate")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Uniqueness -Seleted Fields");
		} else if (validationCheck.equalsIgnoreCase("Numerical Field Fingerprint")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Numerical Field Fingerprint");
		} else if (validationCheck.equalsIgnoreCase("String Field Fingerprint")) {
			map = dqiGraphDAOI.getStringFieldFingerprintGraph(idApp, listApplicationsData);
		} else if (validationCheck.equalsIgnoreCase("Record Anomaly Fingerprint")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Record Anomaly");
		} else if (validationCheck.equalsIgnoreCase("Aggregate DQI Summary")) {
			Map<String, String> tranRuleMap = iResultsDAO.getDataFromListDFTranRuleForMap(idApp);
			System.out.println("tranRuleMap-----> : "+tranRuleMap);
			//map = dqiGraphDAOI.getAggregateDQISummaryGraph(idApp, listApplicationsData, tranRuleMap);
			map = dqiGraphDAOI.getHistoricAvgForAllRunsAggDQI_graph(idApp);
			System.out.println("map----getHistoricAvgForAllRunsAggDQI_graph---> : "+map.toString());
		}else if(validationCheck.equalsIgnoreCase("Data Drift")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Data Drift");
		}else if(validationCheck.equalsIgnoreCase("Length Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_LengthCheck");
		}else if(validationCheck.equalsIgnoreCase("Max Length Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_MaxLengthCheck");	
		}else if(validationCheck.equalsIgnoreCase("Bad Data")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Bad_Data");
		}else if(validationCheck.equalsIgnoreCase("Date Rule Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_DateRuleCheck");
		}else if(validationCheck.equalsIgnoreCase("Timeliness Fingerprint")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Timeliness");
		}
//////////////////AKSHAY--4/3/2019/////////////////////////////
		else if(validationCheck.equalsIgnoreCase("Timeliness Fingerprint")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Timeliness");
		}
////////////////////////////////////////////////////////////

		else if(validationCheck.equalsIgnoreCase("Regex Pattern Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Pattern_Data");
		}
		else if(validationCheck.equalsIgnoreCase("Rules")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Rules");
		} else if (validationCheck.equalsIgnoreCase("SqlRules")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Sql_Rule");
		} else if (validationCheck.equalsIgnoreCase("Global Rules")) {
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_GlobalRules");
		}
		else if(validationCheck.equalsIgnoreCase("Default Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_DefaultCheck");
		}
		else if(validationCheck.equalsIgnoreCase("Default Pattern Check")){
			map = dqiGraphDAOI.GetGraphForDashboard(idApp, listApplicationsData, "DQ_Default_Pattern_Data");
		}

		/*
		 * long start = System.currentTimeMillis(); try { Thread.sleep(5000); }
		 * catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * System.out.println("Sleep time in ms = "+(System.currentTimeMillis()-
		 * start));
		 */
		return map;
	}

	@RequestMapping(value = "/microDataColumn", method = RequestMethod.POST)
	public void microDataColumn(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idApp) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("idApp..............=" + idApp);
		List<String> listDataDefinitionColumnNames = new ArrayList<String>();
		// check if keyGroupRecordCount is enabled
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		if(listApplicationsData.getKeyGroupRecordCountAnomaly()!=null
				&& listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y")) {
			long idData = validationcheckdao.getIdDataTFromListApplication(idApp);
			listDataDefinitionColumnNames = validationcheckdao
					.getDisplayNamesFromListDataDefinitionFordGroup(idData);
		}
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(listDataDefinitionColumnNames);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject displayName = new JSONObject();
			displayName.put("success", jsonInString);
			System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/microDataValue", method = RequestMethod.POST)
	public void microDataValue(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String tableName, @RequestParam String idApp) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("microDataValue : tableName..............=" + tableName);
		// long idData = validationcheckdao.getIdDataTFromListApplication(idApp);
		List<String> listMicroSegValData = iResultsDAO.getMicrosegmentColValue(tableName, idApp);
		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writeValueAsString(listMicroSegValData);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject dGroupVal = new JSONObject();
			dGroupVal.put("success", jsonInString);
			System.out.println("displayName=" + dGroupVal);

			response.getWriter().println(dGroupVal);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/** Last Update : 18thApril2020
	 * Code By : Anant S. Mahale
	 * @param session : to validate user
	 * @param req : get project
	 * @param res
	 * @return : Location validation mapping
	 */
	@RequestMapping(value = "/locationInfo")
	public ModelAndView locationInfo(HttpSession session, HttpServletRequest req, HttpServletResponse res) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		String strProject = req.getParameter("project");
		Object objSessionprojectId = session.getAttribute("projectId");
		ModelAndView modelAndView = new ModelAndView("locationInfo");
		if(strProject != null && !strProject.isEmpty()) {
			System.out.println("DashBoardController : locationInfo : get call : strProject :: "+strProject);
			int intProjectId = iProjectService.getProjectIdByProjectName(strProject);
			List<String> listLocationNames = new ArrayList<String>();
			listLocationNames = iResultsDAO.getListOfLocationsbyProject(intProjectId);
			Map<String, List<String>> mapTableData = iResultsDAO.getLocatoinInfoTableData(listLocationNames);
			List<List<String>> listOfListRows = iResultsDAO.formatedDataForTable(mapTableData);
			System.out.println("DashBoardController : locationInfo : listOfListRows :: "+listOfListRows);
				modelAndView.addObject("tabledata", listOfListRows);
		}else {
			System.out.println("DashBoardController : locationInfo : session project id : objSessionprojectId :: "+objSessionprojectId);
			int intSessionprojectId = Integer.parseInt(objSessionprojectId.toString());
			List<String> listLocationNames = new ArrayList<String>();
			listLocationNames = iResultsDAO.getListOfLocationsbyProject(intSessionprojectId);
			Map<String, List<String>> mapTableData = iResultsDAO.getLocatoinInfoTableData(listLocationNames);
			List<List<String>> listOfListRows = iResultsDAO.formatedDataForTable(mapTableData);
			System.out.println("DashBoardController : locationInfo : listOfListRows :: "+listOfListRows);
				modelAndView.addObject("tabledata", listOfListRows);
		}
		modelAndView.addObject("currentSection", "Dashboard");
		modelAndView.addObject("currentLink", "View");
		//	List<String> listLocationNames = iResultsDAO.getListOfLocationsbyProject(15);
			return modelAndView;
	}

	@RequestMapping(value = "/forecedDirectedGraph", method = RequestMethod.GET)
	public void forecedDirectedGraph(HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String jsonString = "{" +
				"    \"nodes\": [" +
				"      {\"id\": \"Myriel\", \"group\": 1}," +
				"      {\"id\": \"Napoleon\", \"group\": 1}," +
				"      {\"id\": \"Mlle.Baptistine\", \"group\": 1}," +
				"      {\"id\": \"Mme.Magloire\", \"group\": 1}," +
				"      {\"id\": \"CountessdeLo\", \"group\": 1}," +
				"      {\"id\": \"Geborand\", \"group\": 1}," +
				"      {\"id\": \"Champtercier\", \"group\": 1}," +
				"      {\"id\": \"Cravatte\", \"group\": 1}," +
				"      {\"id\": \"Count\", \"group\": 1}," +
				"      {\"id\": \"OldMan\", \"group\": 1}," +
				"      {\"id\": \"Labarre\", \"group\": 2}," +
				"      {\"id\": \"Valjean\", \"group\": 2}," +
				"      {\"id\": \"Marguerite\", \"group\": 3}," +
				"      {\"id\": \"Mme.deR\", \"group\": 2}," +
				"      {\"id\": \"Isabeau\", \"group\": 2}," +
				"      {\"id\": \"Gervais\", \"group\": 2}," +
				"      {\"id\": \"Tholomyes\", \"group\": 3}," +
				"      {\"id\": \"Listolier\", \"group\": 3}," +
				"      {\"id\": \"Fameuil\", \"group\": 3}," +
				"      {\"id\": \"Blacheville\", \"group\": 3}," +
				"      {\"id\": \"Favourite\", \"group\": 3}," +
				"      {\"id\": \"Dahlia\", \"group\": 3}," +
				"      {\"id\": \"Zephine\", \"group\": 3}," +
				"      {\"id\": \"Fantine\", \"group\": 3}," +
				"      {\"id\": \"Mme.Thenardier\", \"group\": 4}," +
				"      {\"id\": \"Thenardier\", \"group\": 4}," +
				"      {\"id\": \"Cosette\", \"group\": 5}," +
				"      {\"id\": \"Javert\", \"group\": 4}," +
				"      {\"id\": \"Fauchelevent\", \"group\": 0}," +
				"      {\"id\": \"Bamatabois\", \"group\": 2}," +
				"      {\"id\": \"Perpetue\", \"group\": 3}," +
				"      {\"id\": \"Simplice\", \"group\": 2}," +
				"      {\"id\": \"Scaufflaire\", \"group\": 2}," +
				"      {\"id\": \"Woman1\", \"group\": 2}," +
				"      {\"id\": \"Judge\", \"group\": 2}," +
				"      {\"id\": \"Champmathieu\", \"group\": 2}," +
				"      {\"id\": \"Brevet\", \"group\": 2}," +
				"      {\"id\": \"Chenildieu\", \"group\": 2}," +
				"      {\"id\": \"Cochepaille\", \"group\": 2}," +
				"      {\"id\": \"Pontmercy\", \"group\": 4}," +
				"      {\"id\": \"Boulatruelle\", \"group\": 6}," +
				"      {\"id\": \"Eponine\", \"group\": 4}," +
				"      {\"id\": \"Anzelma\", \"group\": 4}," +
				"      {\"id\": \"Woman2\", \"group\": 5}," +
				"      {\"id\": \"MotherInnocent\", \"group\": 0}," +
				"      {\"id\": \"Gribier\", \"group\": 0}," +
				"      {\"id\": \"Jondrette\", \"group\": 7}," +
				"      {\"id\": \"Mme.Burgon\", \"group\": 7}," +
				"      {\"id\": \"Gavroche\", \"group\": 8}," +
				"      {\"id\": \"Gillenormand\", \"group\": 5}," +
				"      {\"id\": \"Magnon\", \"group\": 5}," +
				"      {\"id\": \"Mlle.Gillenormand\", \"group\": 5}," +
				"      {\"id\": \"Mme.Pontmercy\", \"group\": 5}," +
				"      {\"id\": \"Mlle.Vaubois\", \"group\": 5}," +
				"      {\"id\": \"Lt.Gillenormand\", \"group\": 5}," +
				"      {\"id\": \"Marius\", \"group\": 8}," +
				"      {\"id\": \"BaronessT\", \"group\": 5}," +
				"      {\"id\": \"Mabeuf\", \"group\": 8}," +
				"      {\"id\": \"Enjolras\", \"group\": 8}," +
				"      {\"id\": \"Combeferre\", \"group\": 8}," +
				"      {\"id\": \"Prouvaire\", \"group\": 8}," +
				"      {\"id\": \"Feuilly\", \"group\": 8}," +
				"      {\"id\": \"Courfeyrac\", \"group\": 8}," +
				"      {\"id\": \"Bahorel\", \"group\": 8}," +
				"      {\"id\": \"Bossuet\", \"group\": 8}," +
				"      {\"id\": \"Joly\", \"group\": 8}," +
				"      {\"id\": \"Grantaire\", \"group\": 8}," +
				"      {\"id\": \"MotherPlutarch\", \"group\": 9}," +
				"      {\"id\": \"Gueulemer\", \"group\": 4}," +
				"      {\"id\": \"Babet\", \"group\": 4}," +
				"      {\"id\": \"Claquesous\", \"group\": 4}," +
				"      {\"id\": \"Montparnasse\", \"group\": 4}," +
				"      {\"id\": \"Toussaint\", \"group\": 5}," +
				"      {\"id\": \"Child1\", \"group\": 10}," +
				"      {\"id\": \"Child2\", \"group\": 10}," +
				"      {\"id\": \"Brujon\", \"group\": 4}," +
				"      {\"id\": \"Mme.Hucheloup\", \"group\": 8}" +
				"    ]," +
				"    \"links\": [" +
				"      {\"source\": \"Napoleon\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Mlle.Baptistine\", \"target\": \"Myriel\", \"value\": 8}," +
				"      {\"source\": \"Mme.Magloire\", \"target\": \"Myriel\", \"value\": 10}," +
				"      {\"source\": \"Mme.Magloire\", \"target\": \"Mlle.Baptistine\", \"value\": 6}," +
				"      {\"source\": \"CountessdeLo\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Geborand\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Champtercier\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Cravatte\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Count\", \"target\": \"Myriel\", \"value\": 2}," +
				"      {\"source\": \"OldMan\", \"target\": \"Myriel\", \"value\": 1}," +
				"      {\"source\": \"Valjean\", \"target\": \"Labarre\", \"value\": 1}," +
				"      {\"source\": \"Valjean\", \"target\": \"Mme.Magloire\", \"value\": 3}," +
				"      {\"source\": \"Valjean\", \"target\": \"Mlle.Baptistine\", \"value\": 3}," +
				"      {\"source\": \"Valjean\", \"target\": \"Myriel\", \"value\": 5}," +
				"      {\"source\": \"Marguerite\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Mme.deR\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Isabeau\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Gervais\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Listolier\", \"target\": \"Tholomyes\", \"value\": 4}," +
				"      {\"source\": \"Fameuil\", \"target\": \"Tholomyes\", \"value\": 4}," +
				"      {\"source\": \"Fameuil\", \"target\": \"Listolier\", \"value\": 4}," +
				"      {\"source\": \"Blacheville\", \"target\": \"Tholomyes\", \"value\": 4}," +
				"      {\"source\": \"Blacheville\", \"target\": \"Listolier\", \"value\": 4}," +
				"      {\"source\": \"Blacheville\", \"target\": \"Fameuil\", \"value\": 4}," +
				"      {\"source\": \"Favourite\", \"target\": \"Tholomyes\", \"value\": 3}," +
				"      {\"source\": \"Favourite\", \"target\": \"Listolier\", \"value\": 3}," +
				"      {\"source\": \"Favourite\", \"target\": \"Fameuil\", \"value\": 3}," +
				"      {\"source\": \"Favourite\", \"target\": \"Blacheville\", \"value\": 4}," +
				"      {\"source\": \"Dahlia\", \"target\": \"Tholomyes\", \"value\": 3}," +
				"      {\"source\": \"Dahlia\", \"target\": \"Listolier\", \"value\": 3}," +
				"      {\"source\": \"Dahlia\", \"target\": \"Fameuil\", \"value\": 3}," +
				"      {\"source\": \"Dahlia\", \"target\": \"Blacheville\", \"value\": 3}," +
				"      {\"source\": \"Dahlia\", \"target\": \"Favourite\", \"value\": 5}," +
				"      {\"source\": \"Zephine\", \"target\": \"Tholomyes\", \"value\": 3}," +
				"      {\"source\": \"Zephine\", \"target\": \"Listolier\", \"value\": 3}," +
				"      {\"source\": \"Zephine\", \"target\": \"Fameuil\", \"value\": 3}," +
				"      {\"source\": \"Zephine\", \"target\": \"Blacheville\", \"value\": 3}," +
				"      {\"source\": \"Zephine\", \"target\": \"Favourite\", \"value\": 4}," +
				"      {\"source\": \"Zephine\", \"target\": \"Dahlia\", \"value\": 4}," +
				"      {\"source\": \"Fantine\", \"target\": \"Tholomyes\", \"value\": 3}," +
				"      {\"source\": \"Fantine\", \"target\": \"Listolier\", \"value\": 3}," +
				"      {\"source\": \"Fantine\", \"target\": \"Fameuil\", \"value\": 3}," +
				"      {\"source\": \"Fantine\", \"target\": \"Blacheville\", \"value\": 3}," +
				"      {\"source\": \"Fantine\", \"target\": \"Favourite\", \"value\": 4}," +
				"      {\"source\": \"Fantine\", \"target\": \"Dahlia\", \"value\": 4}," +
				"      {\"source\": \"Fantine\", \"target\": \"Zephine\", \"value\": 4}," +
				"      {\"source\": \"Fantine\", \"target\": \"Marguerite\", \"value\": 2}," +
				"      {\"source\": \"Fantine\", \"target\": \"Valjean\", \"value\": 9}," +
				"      {\"source\": \"Mme.Thenardier\", \"target\": \"Fantine\", \"value\": 2}," +
				"      {\"source\": \"Mme.Thenardier\", \"target\": \"Valjean\", \"value\": 7}," +
				"      {\"source\": \"Thenardier\", \"target\": \"Mme.Thenardier\", \"value\": 13}," +
				"      {\"source\": \"Thenardier\", \"target\": \"Fantine\", \"value\": 1}," +
				"      {\"source\": \"Thenardier\", \"target\": \"Valjean\", \"value\": 12}," +
				"      {\"source\": \"Cosette\", \"target\": \"Mme.Thenardier\", \"value\": 4}," +
				"      {\"source\": \"Cosette\", \"target\": \"Valjean\", \"value\": 31}," +
				"      {\"source\": \"Cosette\", \"target\": \"Tholomyes\", \"value\": 1}," +
				"      {\"source\": \"Cosette\", \"target\": \"Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Javert\", \"target\": \"Valjean\", \"value\": 17}," +
				"      {\"source\": \"Javert\", \"target\": \"Fantine\", \"value\": 5}," +
				"      {\"source\": \"Javert\", \"target\": \"Thenardier\", \"value\": 5}," +
				"      {\"source\": \"Javert\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Javert\", \"target\": \"Cosette\", \"value\": 1}," +
				"      {\"source\": \"Fauchelevent\", \"target\": \"Valjean\", \"value\": 8}," +
				"      {\"source\": \"Fauchelevent\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Bamatabois\", \"target\": \"Fantine\", \"value\": 1}," +
				"      {\"source\": \"Bamatabois\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Bamatabois\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Perpetue\", \"target\": \"Fantine\", \"value\": 1}," +
				"      {\"source\": \"Simplice\", \"target\": \"Perpetue\", \"value\": 2}," +
				"      {\"source\": \"Simplice\", \"target\": \"Valjean\", \"value\": 3}," +
				"      {\"source\": \"Simplice\", \"target\": \"Fantine\", \"value\": 2}," +
				"      {\"source\": \"Simplice\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Scaufflaire\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Woman1\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Woman1\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Judge\", \"target\": \"Valjean\", \"value\": 3}," +
				"      {\"source\": \"Judge\", \"target\": \"Bamatabois\", \"value\": 2}," +
				"      {\"source\": \"Champmathieu\", \"target\": \"Valjean\", \"value\": 3}," +
				"      {\"source\": \"Champmathieu\", \"target\": \"Judge\", \"value\": 3}," +
				"      {\"source\": \"Champmathieu\", \"target\": \"Bamatabois\", \"value\": 2}," +
				"      {\"source\": \"Brevet\", \"target\": \"Judge\", \"value\": 2}," +
				"      {\"source\": \"Brevet\", \"target\": \"Champmathieu\", \"value\": 2}," +
				"      {\"source\": \"Brevet\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Brevet\", \"target\": \"Bamatabois\", \"value\": 1}," +
				"      {\"source\": \"Chenildieu\", \"target\": \"Judge\", \"value\": 2}," +
				"      {\"source\": \"Chenildieu\", \"target\": \"Champmathieu\", \"value\": 2}," +
				"      {\"source\": \"Chenildieu\", \"target\": \"Brevet\", \"value\": 2}," +
				"      {\"source\": \"Chenildieu\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Chenildieu\", \"target\": \"Bamatabois\", \"value\": 1}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Judge\", \"value\": 2}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Champmathieu\", \"value\": 2}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Brevet\", \"value\": 2}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Chenildieu\", \"value\": 2}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Cochepaille\", \"target\": \"Bamatabois\", \"value\": 1}," +
				"      {\"source\": \"Pontmercy\", \"target\": \"Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Boulatruelle\", \"target\": \"Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Eponine\", \"target\": \"Mme.Thenardier\", \"value\": 2}," +
				"      {\"source\": \"Eponine\", \"target\": \"Thenardier\", \"value\": 3}," +
				"      {\"source\": \"Anzelma\", \"target\": \"Eponine\", \"value\": 2}," +
				"      {\"source\": \"Anzelma\", \"target\": \"Thenardier\", \"value\": 2}," +
				"      {\"source\": \"Anzelma\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Woman2\", \"target\": \"Valjean\", \"value\": 3}," +
				"      {\"source\": \"Woman2\", \"target\": \"Cosette\", \"value\": 1}," +
				"      {\"source\": \"Woman2\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"MotherInnocent\", \"target\": \"Fauchelevent\", \"value\": 3}," +
				"      {\"source\": \"MotherInnocent\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Gribier\", \"target\": \"Fauchelevent\", \"value\": 2}," +
				"      {\"source\": \"Mme.Burgon\", \"target\": \"Jondrette\", \"value\": 1}," +
				"      {\"source\": \"Gavroche\", \"target\": \"Mme.Burgon\", \"value\": 2}," +
				"      {\"source\": \"Gavroche\", \"target\": \"Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Gavroche\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Gavroche\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Gillenormand\", \"target\": \"Cosette\", \"value\": 3}," +
				"      {\"source\": \"Gillenormand\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Magnon\", \"target\": \"Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"Magnon\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Mlle.Gillenormand\", \"target\": \"Gillenormand\", \"value\": 9}," +
				"      {\"source\": \"Mlle.Gillenormand\", \"target\": \"Cosette\", \"value\": 2}," +
				"      {\"source\": \"Mlle.Gillenormand\", \"target\": \"Valjean\", \"value\": 2}," +
				"      {\"source\": \"Mme.Pontmercy\", \"target\": \"Mlle.Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"Mme.Pontmercy\", \"target\": \"Pontmercy\", \"value\": 1}," +
				"      {\"source\": \"Mlle.Vaubois\", \"target\": \"Mlle.Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"Lt.Gillenormand\", \"target\": \"Mlle.Gillenormand\", \"value\": 2}," +
				"      {\"source\": \"Lt.Gillenormand\", \"target\": \"Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"Lt.Gillenormand\", \"target\": \"Cosette\", \"value\": 1}," +
				"      {\"source\": \"Marius\", \"target\": \"Mlle.Gillenormand\", \"value\": 6}," +
				"      {\"source\": \"Marius\", \"target\": \"Gillenormand\", \"value\": 12}," +
				"      {\"source\": \"Marius\", \"target\": \"Pontmercy\", \"value\": 1}," +
				"      {\"source\": \"Marius\", \"target\": \"Lt.Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"Marius\", \"target\": \"Cosette\", \"value\": 21}," +
				"      {\"source\": \"Marius\", \"target\": \"Valjean\", \"value\": 19}," +
				"      {\"source\": \"Marius\", \"target\": \"Tholomyes\", \"value\": 1}," +
				"      {\"source\": \"Marius\", \"target\": \"Thenardier\", \"value\": 2}," +
				"      {\"source\": \"Marius\", \"target\": \"Eponine\", \"value\": 5}," +
				"      {\"source\": \"Marius\", \"target\": \"Gavroche\", \"value\": 4}," +
				"      {\"source\": \"BaronessT\", \"target\": \"Gillenormand\", \"value\": 1}," +
				"      {\"source\": \"BaronessT\", \"target\": \"Marius\", \"value\": 1}," +
				"      {\"source\": \"Mabeuf\", \"target\": \"Marius\", \"value\": 1}," +
				"      {\"source\": \"Mabeuf\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Mabeuf\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Enjolras\", \"target\": \"Marius\", \"value\": 7}," +
				"      {\"source\": \"Enjolras\", \"target\": \"Gavroche\", \"value\": 7}," +
				"      {\"source\": \"Enjolras\", \"target\": \"Javert\", \"value\": 6}," +
				"      {\"source\": \"Enjolras\", \"target\": \"Mabeuf\", \"value\": 1}," +
				"      {\"source\": \"Enjolras\", \"target\": \"Valjean\", \"value\": 4}," +
				"      {\"source\": \"Combeferre\", \"target\": \"Enjolras\", \"value\": 15}," +
				"      {\"source\": \"Combeferre\", \"target\": \"Marius\", \"value\": 5}," +
				"      {\"source\": \"Combeferre\", \"target\": \"Gavroche\", \"value\": 6}," +
				"      {\"source\": \"Combeferre\", \"target\": \"Mabeuf\", \"value\": 2}," +
				"      {\"source\": \"Prouvaire\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Prouvaire\", \"target\": \"Enjolras\", \"value\": 4}," +
				"      {\"source\": \"Prouvaire\", \"target\": \"Combeferre\", \"value\": 2}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Gavroche\", \"value\": 2}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Enjolras\", \"value\": 6}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Prouvaire\", \"value\": 2}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Combeferre\", \"value\": 5}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Mabeuf\", \"value\": 1}," +
				"      {\"source\": \"Feuilly\", \"target\": \"Marius\", \"value\": 1}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Marius\", \"value\": 9}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Enjolras\", \"value\": 17}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Combeferre\", \"value\": 13}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Gavroche\", \"value\": 7}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Mabeuf\", \"value\": 2}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Feuilly\", \"value\": 6}," +
				"      {\"source\": \"Courfeyrac\", \"target\": \"Prouvaire\", \"value\": 3}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Combeferre\", \"value\": 5}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Gavroche\", \"value\": 5}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Courfeyrac\", \"value\": 6}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Mabeuf\", \"value\": 2}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Enjolras\", \"value\": 4}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Feuilly\", \"value\": 3}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Prouvaire\", \"value\": 2}," +
				"      {\"source\": \"Bahorel\", \"target\": \"Marius\", \"value\": 1}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Marius\", \"value\": 5}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Courfeyrac\", \"value\": 12}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Gavroche\", \"value\": 5}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Bahorel\", \"value\": 4}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Enjolras\", \"value\": 10}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Feuilly\", \"value\": 6}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Prouvaire\", \"value\": 2}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Combeferre\", \"value\": 9}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Mabeuf\", \"value\": 1}," +
				"      {\"source\": \"Bossuet\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Joly\", \"target\": \"Bahorel\", \"value\": 5}," +
				"      {\"source\": \"Joly\", \"target\": \"Bossuet\", \"value\": 7}," +
				"      {\"source\": \"Joly\", \"target\": \"Gavroche\", \"value\": 3}," +
				"      {\"source\": \"Joly\", \"target\": \"Courfeyrac\", \"value\": 5}," +
				"      {\"source\": \"Joly\", \"target\": \"Enjolras\", \"value\": 5}," +
				"      {\"source\": \"Joly\", \"target\": \"Feuilly\", \"value\": 5}," +
				"      {\"source\": \"Joly\", \"target\": \"Prouvaire\", \"value\": 2}," +
				"      {\"source\": \"Joly\", \"target\": \"Combeferre\", \"value\": 5}," +
				"      {\"source\": \"Joly\", \"target\": \"Mabeuf\", \"value\": 1}," +
				"      {\"source\": \"Joly\", \"target\": \"Marius\", \"value\": 2}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Bossuet\", \"value\": 3}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Enjolras\", \"value\": 3}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Combeferre\", \"value\": 1}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Courfeyrac\", \"value\": 2}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Joly\", \"value\": 2}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Bahorel\", \"value\": 1}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Feuilly\", \"value\": 1}," +
				"      {\"source\": \"Grantaire\", \"target\": \"Prouvaire\", \"value\": 1}," +
				"      {\"source\": \"MotherPlutarch\", \"target\": \"Mabeuf\", \"value\": 3}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Thenardier\", \"value\": 5}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Gueulemer\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Babet\", \"target\": \"Thenardier\", \"value\": 6}," +
				"      {\"source\": \"Babet\", \"target\": \"Gueulemer\", \"value\": 6}," +
				"      {\"source\": \"Babet\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Babet\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Babet\", \"target\": \"Javert\", \"value\": 2}," +
				"      {\"source\": \"Babet\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Babet\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Thenardier\", \"value\": 4}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Babet\", \"value\": 4}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Gueulemer\", \"value\": 4}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Mme.Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Claquesous\", \"target\": \"Enjolras\", \"value\": 1}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Babet\", \"value\": 2}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Gueulemer\", \"value\": 2}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Claquesous\", \"value\": 2}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Montparnasse\", \"target\": \"Thenardier\", \"value\": 1}," +
				"      {\"source\": \"Toussaint\", \"target\": \"Cosette\", \"value\": 2}," +
				"      {\"source\": \"Toussaint\", \"target\": \"Javert\", \"value\": 1}," +
				"      {\"source\": \"Toussaint\", \"target\": \"Valjean\", \"value\": 1}," +
				"      {\"source\": \"Child1\", \"target\": \"Gavroche\", \"value\": 2}," +
				"      {\"source\": \"Child2\", \"target\": \"Gavroche\", \"value\": 2}," +
				"      {\"source\": \"Child2\", \"target\": \"Child1\", \"value\": 3}," +
				"      {\"source\": \"Brujon\", \"target\": \"Babet\", \"value\": 3}," +
				"      {\"source\": \"Brujon\", \"target\": \"Gueulemer\", \"value\": 3}," +
				"      {\"source\": \"Brujon\", \"target\": \"Thenardier\", \"value\": 3}," +
				"      {\"source\": \"Brujon\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Brujon\", \"target\": \"Eponine\", \"value\": 1}," +
				"      {\"source\": \"Brujon\", \"target\": \"Claquesous\", \"value\": 1}," +
				"      {\"source\": \"Brujon\", \"target\": \"Montparnasse\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Bossuet\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Joly\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Grantaire\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Bahorel\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Courfeyrac\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Gavroche\", \"value\": 1}," +
				"      {\"source\": \"Mme.Hucheloup\", \"target\": \"Enjolras\", \"value\": 1}" +
				"    ]" +
				"  }";

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("json", jsonString.toString());
		response.getWriter().println(jsonObject);

	}

}