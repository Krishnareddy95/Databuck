package com.databuck.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.io.File;
import com.databuck.bean.FingerprintMatchingDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.dao.IFileManagementDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.impl.ValidationCheckDAOImpl;
import com.databuck.service.RBACController;
import java.util.Scanner;

@Controller
public class FileManagementResultController {

	@Autowired
	public IFileManagementDAO IFileManagementDAOObject;
	@Autowired
	private RBACController rbacController;
	@Autowired
	private ValidationCheckDAOImpl validationcheckdao;
	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@RequestMapping(value = "/FileManagementResultView")
	public ModelAndView FileManagementResultView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("FileManagementResultController");
			ModelAndView modelAndView = new ModelAndView("FileManagementResultView");

			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject.getdatafromresultmaster();
			Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "File Management");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/showFileManagementData", method = RequestMethod.GET)
	public ModelAndView showFileManagementData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("showFileManagementData");
			ModelAndView modelAndView = new ModelAndView("showFileManagementData");
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "File Management");
			Long appId = Long.parseLong(request.getParameter("idApp"));
			System.out.println("appId=" + appId);

			SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
					.getTableNamesForAppIDinResultMaster(appId);

			while (tableNamesForAppIDinResultMaster.next()) {
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("FILE_MANAGEMENT")) {
					String table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("FILE_MANAGEMENT_Table_Name=" + table_Name);
					SqlRowSet fileManagementTable = IFileManagementDAOObject.getTableFromResultsDB(table_Name, appId);
					modelAndView.addObject("fileManagementTable", fileManagementTable);
					modelAndView.addObject("fileManagementTableTrue", "fileManagementTableTrue");
					modelAndView.addObject("table_Name", table_Name);
				}
				String appName = tableNamesForAppIDinResultMaster.getString("AppName");
				modelAndView.addObject("appName", appName);
			}

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/StatisticalMatchingResultView")
	public ModelAndView StatisticalMatchingResultView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("StatisticalMatchingResultView");
			ModelAndView modelAndView = new ModelAndView("StatisticalMatchingResultView");

			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject
					.getDataFromResultMasterForStatisticalMatching();
			Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			List<FingerprintMatchingDashboard> dashboardTable = IFileManagementDAOObject
					.getDashBoardDataForFingerprintMatching(resultmasterdata);
			modelAndView.addObject("dashboardTable", dashboardTable);
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Statistical Matching");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/SchemaMatchingResultView", method = RequestMethod.GET)
	public ModelAndView getSchemaMatchingResults(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			System.out.println("schemaMatchingView");
			ModelAndView modelAndView = new ModelAndView("schemaMatchingView");
			SqlRowSet rowset = IFileManagementDAOObject.getDataFromResultMasterForSchemaMatching();
			Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
			while (rowset.next()) {
				resultmasterdata.put(rowset.getLong(1), rowset.getString(2));
				System.out.println(rowset.getLong(1) + "" + rowset.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Schema Matching");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/showSchemaMatchingData", method = RequestMethod.GET)
	public ModelAndView showSchemaMatchingData(HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("showSchemaMatchingData");
		try {
			
			
			
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage");
			}
			boolean rbac = rbacController.rbac("Results", "R", session);
			if (rbac) {

				System.out.println("showSchemaMatchingData");

				modelAndView.addObject("currentSection", "Dashboard");
				modelAndView.addObject("currentLink", "Statistical Matching");
				Long appId = Long.parseLong(request.getParameter("idApp"));
				ListApplications listApplications = validationcheckdao.getdatafromlistapplications(appId);
				ListDataSchema listdataschema_left = listdatasourcedao
						.getListDataSchemaForIdDataSchema(listApplications.getIdLeftData()).get(0);
				ListDataSchema listdataschema_right = listdatasourcedao
						.getListDataSchemaForIdDataSchema(listApplications.getIdRightData()).get(0);

				modelAndView.addObject("leftSchemaName", listdataschema_left.getSchemaName());
				modelAndView.addObject("rightSchemaName", listdataschema_right.getSchemaName());
				listdataschema_right.getSchemaName();
				System.out.println("appId=" + appId);

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
					System.out.println("tableName:" + tableNamesForAppIDinResultMaster.getString("Table_Name"));
					if (tableNamesForAppIDinResultMaster.getString("Table_Name")
							.equalsIgnoreCase("schema_matching_" + appId + "_tablesummary")) {
						table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
						System.out.println("Schema MATCHING Table_Name=" + table_Name);

						SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
						modelAndView.addObject("tablesummaryrowset", sqlRowset);
						modelAndView.addObject("tableSummaryExist", "Y");
						String csvLookupTableName ="Schema_Matching_"+appId+"_Transaction_Summary";
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("schemaMatchingTransactionTable", csvLookupTableName);
					}

					if (tableNamesForAppIDinResultMaster.getString("Table_Name")
							.equalsIgnoreCase("schema_matching_" + appId + "_columnsummary")) {
						table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
						System.out.println("Schema MATCHING Table_Name=" + table_Name);

						SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
						modelAndView.addObject("columnSummaryrowset", sqlRowset);
						modelAndView.addObject("columnSummaryExist", "Y");
						String csvLookupTableName ="Schema_Matching_"+appId+"_Transaction_Summary";
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("schemaMatchingTransactionTable", csvLookupTableName);
					}
					if (tableNamesForAppIDinResultMaster.getString("Table_Name")
							.equalsIgnoreCase("schema_matching_" + appId + "_recordcount")) {
						System.out.println("record count matched");
						table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
						System.out.println("Schema MATCHING Table_Name=" + table_Name);
						SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
						modelAndView.addObject("recordSummaryrowset", sqlRowset);
						String csvLookupTableName ="Schema_Matching_"+appId+"_Transaction_Summary";
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("schemaMatchingTransactionTable", csvLookupTableName);

						modelAndView.addObject("recordSummaryExist", "Y");
					}if(tableNamesForAppIDinResultMaster.getString("table_Name")
							.equalsIgnoreCase("schema_matching_" + appId + "_recordcountuncommon")){
						System.out.println("record countuncommon ");
						table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
						System.out.println("Schema MATCHING Table_Name=" + table_Name);

						SqlRowSet sqlRowset = IFileManagementDAOObject.getTableDataForSchemMatching(table_Name);
						modelAndView.addObject("recordSummaryrowsetuncommon", sqlRowset);
						modelAndView.addObject("recordSummaryExistuncommon", "Y");
						String csvLookupTableName ="Schema_Matching_"+appId+"_Transaction_Summary";
						modelAndView.addObject("idApp", appId);
						modelAndView.addObject("schemaMatchingTransactionTable", csvLookupTableName);
						SqlRowSetMetaData metaData = sqlRowset.getMetaData();
						for (int i = 1; i <= metaData.getColumnCount(); i++) {
							System.out.println(metaData.getColumnName(i));
						
					}
					}

					String appName = tableNamesForAppIDinResultMaster.getString("AppName");

					modelAndView.addObject("appName", appName);
				}
				return modelAndView;
			} else
				return new ModelAndView("loginPage");
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/showStatisticalMatchingData", method = RequestMethod.GET)
	public ModelAndView showStatisticalMatchingData(HttpServletRequest request, HttpSession session, @RequestParam Long idApp ) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("showStatisticalMatchingData");
			ModelAndView modelAndView = new ModelAndView("showStatisticalMatchingData");
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Statistical Matching");
			Long appId = Long.parseLong(request.getParameter("idApp"));
			System.out.println("appId=" + appId);

			SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
					.getTableNamesForAppIDinResultMaster(appId);
			String table_Name = null;
			while (tableNamesForAppIDinResultMaster.next()) {
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("SUMMARY")) {
					table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("STATISTICAL_MATCHING Table_Name=" + table_Name);
					SqlRowSet fileManagementTable = IFileManagementDAOObject
							.getTableFromResultsDBForStatisticalMatching(table_Name, appId, "summary");
					modelAndView.addObject("tableName", table_Name);
					modelAndView.addObject("fileManagementTable", fileManagementTable);
					modelAndView.addObject("fileManagementTableTrue", "fileManagementTableTrue");
					modelAndView.addObject("Statistical_Matching_Table_Name", table_Name);
				}
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("DGROUP")) {
					table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("STATISTICAL_MATCHING Table_Name=" + table_Name);
					SqlRowSet fileManagementTable = IFileManagementDAOObject
							.getTableFromResultsDBForStatisticalMatching(table_Name, appId, "dgroup");
					modelAndView.addObject("tableName", table_Name);
					modelAndView.addObject("fileManagementTable", fileManagementTable);
					modelAndView.addObject("fileManagementTableTrue", "fileManagementTableTrue");
					modelAndView.addObject("Statistical_Matching_Table_Name", table_Name);
				}
				SqlRowSet maxRecordForDashBoard = IFileManagementDAOObject.getMaxRecordForDashBoard(table_Name, appId);
				modelAndView.addObject("maxRecordForDashBoard", maxRecordForDashBoard);
				String appName = tableNamesForAppIDinResultMaster.getString("AppName");
				modelAndView.addObject("appName", appName);
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/ModelGovernanceResultView")
	public ModelAndView ModelGovernanceResultView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("ModelGovernanceResultView");
			ModelAndView modelAndView = new ModelAndView("ModelGovernanceResultView");

			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject.getDataFromResultMasterForModelGovernance();
			Map<Long, String> resultmasterdata = new LinkedHashMap<Long, String>();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Model Governance");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/showModelGovernanceData", method = RequestMethod.GET)
	public ModelAndView showModelGovernanceData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("showModelGovernanceData");
			ModelAndView modelAndView = new ModelAndView("showModelGovernanceData");
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Model Governance");
			Long appId = Long.parseLong(request.getParameter("idApp"));
			System.out.println("appId=" + appId);

			SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
					.getTableNamesForAppIDinResultMaster(appId);

			while (tableNamesForAppIDinResultMaster.next()) {
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("DecileConsistency")) {
					String table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("Model Governance Table_Name=" + table_Name);
					SqlRowSet decileConsistencyTable = IFileManagementDAOObject
							.getTableFromResultsDBForModelGovernance(table_Name, appId, "DecileConsistency");
					modelAndView.addObject("tableName", table_Name);
					modelAndView.addObject("decileConsistencyTable", decileConsistencyTable);
					modelAndView.addObject("decileConsistencyTableTrue", "decileConsistencyTableTrue");
					modelAndView.addObject("DecileConsistency", true);
					modelAndView.addObject("DECILECONSISTENCY", "DECILE CONSISTENCY");
				}
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("DecileEquality")) {
					String table_Name1 = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("Model Governance Table_Name=" + table_Name1);
					SqlRowSet decileEqualityTable = IFileManagementDAOObject
							.getTableFromResultsDBForModelGovernance(table_Name1, appId, "DecileEquality");
					modelAndView.addObject("tableName1", table_Name1);
					modelAndView.addObject("decileEqualityTable", decileEqualityTable);
					modelAndView.addObject("decileEqualityTableTrue", "decileEqualityTableTrue");
					modelAndView.addObject("DecileEquality", true);
					modelAndView.addObject("DECILEEQUALITY", "DECILE EQUALITY");
				}
				// for Score Consistency reusing code
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("ScoreConsistency")) {
					String table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("Model Governance Table_Name=" + table_Name);
					SqlRowSet decileConsistencyTable = IFileManagementDAOObject
							.getTableFromResultsDBForModelGovernance(table_Name, appId, "ScoreConsistency");
					modelAndView.addObject("tableName", table_Name);
					modelAndView.addObject("decileConsistencyTable", decileConsistencyTable);
					modelAndView.addObject("decileConsistencyTableTrue", "decileConsistencyTableTrue");
					modelAndView.addObject("DecileConsistency", true);
					modelAndView.addObject("DECILECONSISTENCY", "SCORE CONSISTENCY");
				}
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("ScoreStatus")) {
					String table_Name1 = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("Model Governance Table_Name=" + table_Name1);
					SqlRowSet decileEqualityTable = IFileManagementDAOObject
							.getTableFromResultsDBForModelGovernance(table_Name1, appId, "ScoreStatus");
					modelAndView.addObject("tableName1", table_Name1);
					modelAndView.addObject("decileEqualityTable", decileEqualityTable);
					modelAndView.addObject("decileEqualityTableTrue", "decileEqualityTableTrue");
					modelAndView.addObject("DecileEquality", true);
					modelAndView.addObject("DECILEEQUALITY", "SCORE STATUS");
				}
				String appName = tableNamesForAppIDinResultMaster.getString("AppName");
				modelAndView.addObject("appName", appName);
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/ModelGovernanceDashboardResultView")
	public ModelAndView ModelGovernanceDashboardResultView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("ModelGovernanceDashboardResultView");
			ModelAndView modelAndView = new ModelAndView("ModelGovernanceDashboardResultView");

			// IFileManagementDAOObject.getModelGovernanceDashboardFromListApplications();
			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject
					.getModelGovernanceDashboardFromListApplications();
			Map resultmasterdata = new HashMap();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Model Governance Dashboard");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	

	

	
	@RequestMapping(value = "/downloadLogFiles")
	public ModelAndView downloadLogFiles(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("downloadLogFiles");
			ModelAndView modelAndView = new ModelAndView("downloadLogFiles");

			// IFileManagementDAOObject.getModelGovernanceDashboardFromListApplications();
			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject
					.getModelGovernanceDashboardFromListApplications();
			Map resultmasterdata = new HashMap();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Log Files");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	
	@RequestMapping(value = "/showModelGovernanceDashboardData", method = RequestMethod.GET)
	public ModelAndView showModelGovernanceDashboardData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("showModelGovernanceDashboardData");
			ModelAndView modelAndView = new ModelAndView("showModelGovernanceDashboardData");
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Model Governance Dashboard");
			Long idApp = Long.parseLong(request.getParameter("idApp"));
			System.out.println("idApp=" + idApp);

			String date = request.getParameter("dateid");
			System.out.println("date=" + date);
			modelAndView.addObject("date", date);
			SqlRowSet listApplicationsData = IFileManagementDAOObject.getcsvDirectoryFromListApplications(idApp);
			listApplicationsData.next();

			try {
				System.out.println("csvDir=" + listApplicationsData.getString("csvDir"));
				String[] split = listApplicationsData.getString("csvDir").split("-");
				String decileEqualityTableName = "MODEL_GOVERNANCE_" + split[0] + "_DecileEquality";
				String decileConsistencyTableName = "MODEL_GOVERNANCE_" + split[1] + "_DecileConsistency";
				String scoreConsistencyTableName = "MODEL_GOVERNANCE_" + split[2] + "_ScoreStatus";

				// 16
				List<String> allModelsOfDecileEquality = IFileManagementDAOObject
						.getAllModelsFromModelGovernanceResultsTable(decileEqualityTableName, date);
				List<String> failedOfDecileEquality = IFileManagementDAOObject
						.getfailedModelsFromModelGovernanceResultsTable(decileEqualityTableName, date,
								"decileEqualityStatus");
				String decileEqualityAppName = IFileManagementDAOObject.getApplicationNameFromApplicationName(split[0]);
				modelAndView.addObject("decileEqualityAppName", decileEqualityAppName);
				allModelsOfDecileEquality.removeAll(failedOfDecileEquality);
				System.out.println("failedOfDecileEquality" + failedOfDecileEquality);
				System.out.println("allModelsOfDecileEquality" + allModelsOfDecileEquality);
				modelAndView.addObject("failedOfDecileEquality", failedOfDecileEquality);
				modelAndView.addObject("allModelsOfDecileEquality", allModelsOfDecileEquality);
				// 17
				List<String> allModelsOfDecileConsistency = IFileManagementDAOObject
						.getAllModelsFromModelGovernanceResultsTable(decileConsistencyTableName, date);
				List<String> failedOfDecileConsistency = IFileManagementDAOObject
						.getfailedModelsFromModelGovernanceResultsTable(decileConsistencyTableName, date,
								"decileConsistencyStatus");
				String decileConsistencyAppName = IFileManagementDAOObject
						.getApplicationNameFromApplicationName(split[1]);
				modelAndView.addObject("decileConsistencyAppName", decileConsistencyAppName);
				allModelsOfDecileConsistency.removeAll(failedOfDecileConsistency);
				System.out.println("failedOfDecileConsistency" + failedOfDecileConsistency);
				System.out.println("allModelsOfDecileConsistency" + allModelsOfDecileConsistency);
				modelAndView.addObject("failedOfDecileConsistency", failedOfDecileConsistency);
				modelAndView.addObject("allModelsOfDecileConsistency", allModelsOfDecileConsistency);
				// 18 to do
				List<String> allModelsOfScoreConsistency = IFileManagementDAOObject
						.getAllModelsFromModelGovernanceResultsTable(scoreConsistencyTableName, date);
				List<String> failedOfScoreConsistency = IFileManagementDAOObject
						.getfailedModelsFromModelGovernanceResultsTable(scoreConsistencyTableName, date, "status");
				String scoreConsistencyAppName = IFileManagementDAOObject
						.getApplicationNameFromApplicationName(split[2]);
				modelAndView.addObject("scoreConsistencyAppName", scoreConsistencyAppName);
				allModelsOfScoreConsistency.removeAll(failedOfScoreConsistency);
				System.out.println("failedOfScoreConsistency" + failedOfScoreConsistency);
				System.out.println("allModelsOfScoreConsistency" + allModelsOfScoreConsistency);
				modelAndView.addObject("failedOfScoreConsistency", failedOfScoreConsistency);
				modelAndView.addObject("allModelsOfScoreConsistency", allModelsOfScoreConsistency);

				modelAndView.addObject("appName", listApplicationsData.getString("name"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// SELECT DISTINCT `MODEL_ID` FROM
			// MODEL_GOVERNANCE_27_DecileEquality WHERE DATE = '2017-04-21'
			// SELECT DISTINCT `MODEL_ID` FROM
			// MODEL_GOVERNANCE_27_DecileEquality WHERE DATE = '2017-04-21' AND
			// decileEqualityStatus = 'failed'
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/DataMatchingGroupResultView")
	public ModelAndView DataMatchingGroupResultView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("DataMatchingGroupResultView");
			ModelAndView modelAndView = new ModelAndView("DataMatchingGroupResultView");

			SqlRowSet getdatafromresultmaster = IFileManagementDAOObject.getDataFromResultMasterForDataMatchingGroup();
			Map resultmasterdata = new HashMap();
			while (getdatafromresultmaster.next()) {
				resultmasterdata.put(getdatafromresultmaster.getLong(1), getdatafromresultmaster.getString(2));
				System.out.println(getdatafromresultmaster.getLong(1) + "" + getdatafromresultmaster.getString(2));
			}
			modelAndView.addObject("resultmasterdata", resultmasterdata);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching Group");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/showDataMatchingGroupData", method = RequestMethod.GET)
	public ModelAndView showDataMatchingGroupData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			System.out.println("showDataMatchingGroupData");
			ModelAndView modelAndView = new ModelAndView("showDataMatchingGroupData");
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "Data Matching Group");
			Long appId = Long.parseLong(request.getParameter("idApp"));
			System.out.println("appId=" + appId);

			SqlRowSet tableNamesForAppIDinResultMaster = IFileManagementDAOObject
					.getTableNamesForAppIDinResultMaster(appId);

			while (tableNamesForAppIDinResultMaster.next()) {
				if (tableNamesForAppIDinResultMaster.getString("Table_Name").contains("DATA_MATCHING_GROUP_")) {
					String table_Name = tableNamesForAppIDinResultMaster.getString("Table_Name");
					System.out.println("STATISTICAL_MATCHING Table_Name=" + table_Name);
					SqlRowSet fileManagementTable = IFileManagementDAOObject
							.getTableFromResultsDBForModelGovernance(table_Name, appId, "summary");
					modelAndView.addObject("tableName", table_Name);
					modelAndView.addObject("fileManagementTable", fileManagementTable);
					modelAndView.addObject("fileManagementTableTrue", "fileManagementTableTrue");
				}
				/*
				 * if(tableNamesForAppIDinResultMaster.getString("Table_Name").
				 * contains("DGROUP")){ String
				 * table_Name1=tableNamesForAppIDinResultMaster.getString(
				 * "Table_Name"); System.out.println(
				 * "STATISTICAL_MATCHING Table_Name="+table_Name1); SqlRowSet
				 * fileManagementTable = IFileManagementDAOObject.
				 * getTableFromResultsDBForStatisticalMatching(table_Name1,
				 * appId,"dgroup");
				 * modelAndView.addObject("tableName",table_Name1);
				 * modelAndView.addObject("fileManagementTable",
				 * fileManagementTable);
				 * modelAndView.addObject("fileManagementTableTrue",
				 * "fileManagementTableTrue"); }
				 */
				String appName = tableNamesForAppIDinResultMaster.getString("AppName");
				modelAndView.addObject("appName", appName);
			}
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
}
