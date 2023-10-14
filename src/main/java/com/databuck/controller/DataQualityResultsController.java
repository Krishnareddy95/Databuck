package com.databuck.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DATA_QUALITY_Transaction_Summary;
import com.databuck.bean.DATA_QUALITY_Transactionset_sum_A1;
import com.databuck.bean.DQSummaryBean;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.service.DistributionCheckResultsService;
import com.databuck.service.IDashBoardService;
import com.databuck.service.IDataQualityResultsService;
import com.databuck.service.IMatchingResultService;
import com.databuck.service.IValidationService;
import com.databuck.service.RBACController;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.JwfSpaInfra.CustomizeDataTableColumn;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class DataQualityResultsController {

	@Autowired
	public IValidationService validationService;

	@Autowired
	public IDashBoardService dashBoardService;

	@Autowired
	public IResultsDAO iResultsDAO;

	@Autowired
	MatchingResultDao matchingresultdao;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	public IValidationDAO ivalidationdao;

	@Autowired
	private RBACController rbacController;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	public IMatchingResultService iMatchingResultService;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IProjectDAO projectDao;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private DistributionCheckResultsService distributionCheckResultsService;
	
	@Autowired
	private IDataQualityResultsService dataQualityResultsService;

	@RequestMapping(value = "/dashboard_table")
	public ModelAndView dashboard_table(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardTableView");
			modelAndView.addObject("fromMapping", "dashboardTableView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Transactionset_sum_A1");
			myListTable.add("processData");
			myListTable.add("DATA_QUALITY_Transactionset_sum_dgroup");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/forgotRun",method = RequestMethod.POST)
	public void forgotRun(HttpSession session,HttpServletResponse oResponse,@RequestParam long idApp,
						  @RequestParam String maxExecDate,@RequestParam long maxExecRun) {
		System.out.println("\n=======> forgotRun API - START <=======");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message ="";

		try {
			boolean rbac = RBACController.rbac("Results", "C", session);
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				oResponse.sendRedirect("loginPage.jsp");
			}
			if (rbac) {
				if(idApp>0l) {
					boolean forgotRunStatus = dataQualityResultsService.forgotSelectedRunOfValidation(idApp,maxExecDate,maxExecRun,"Y");
					if (forgotRunStatus) {
						status = "success";
						message = "Forgot Run executed successfully";
					} else
						message = "Failed to perform forgot Run action";
				}else
					message = "Invalid Validation Id, forgot Run action failed";
			}else
				message = "Insuffient permissions to perform forgot Run action";
		} catch (Exception e) {
			message="Error occurred while performing forgot Run action";
			e.printStackTrace();
		}

		// Publishing the response
		try {
			json.put("status", status);
			json.put("message", message);
			oResponse.getWriter().println(json);
			oResponse.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/unforgotRun", method = RequestMethod.POST)
	public void unforgotRun(HttpSession session,HttpServletResponse oResponse,@RequestParam long idApp,
							@RequestParam String maxExecDate,@RequestParam long maxExecRun){
		System.out.println("\n=======> unforgotRun API - START <=======");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message ="";
		try {
			boolean rbac = RBACController.rbac("Results", "C", session);
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				oResponse.sendRedirect("loginPage.jsp");
			}
			if (rbac) {
				if(idApp>0l) {
					boolean forgotRunStatus = dataQualityResultsService.forgotSelectedRunOfValidation(idApp,maxExecDate,maxExecRun,"N");
					if (forgotRunStatus) {
						status = "success";
						message = "Unforgot Run executed successfully";
					} else
						message = "Failed to perform unforgot Run action";
				}else
					message = "Invalid Validation Id, unforgot Run action failed";
			}else
				message = "Insuffient permissions to perform unforgot Run action";
		} catch (Exception e) {
			message="Error occurred while performing unforgot Run action";
			e.printStackTrace();
		}

		// Publishing the response
		try {
			json.put("status", status);
			json.put("message", message);
			oResponse.getWriter().println(json);
			oResponse.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/downloadDQSummary", method = RequestMethod.POST )
	public void downloadDQSummary(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("**** downloadDQSummary *****");
		String sr_idApp = request.getParameter("sr_idApp");

		try {

			String summaryDate = request.getParameter("summaryDate");
			String sr_run = request.getParameter("summaryRun");
			String dr_fileSelected = request.getParameter("sr_summarySelected");

			System.out.println("idApp:" + sr_idApp);
			System.out.println("summaryDate:" + summaryDate);
			System.out.println("summaryRun:" + sr_run);
			System.out.println("sr_fileSelected:" + dr_fileSelected);

			long idApp = Long.valueOf(sr_idApp);
			long summaryRun = 1l;

			if (dr_fileSelected == null || dr_fileSelected.equalsIgnoreCase("LATEST_FILE")) {

				System.out.println("\n====>Fetching MaxDate and Run of table to download latest summary !!");
				SqlRowSet tableDateAndRun = iResultsDAO.getTableMaxDateAndRun(idApp);

				if (tableDateAndRun.next()) {
					summaryDate = tableDateAndRun.getString("Date");
					summaryRun = tableDateAndRun.getLong("Run");
					System.out.println("\n===>Max Date:" + summaryDate);
					System.out.println("\n===>Max Run:" + summaryRun);
				}
			} else {
				summaryRun = Long.valueOf(sr_run);
			}

			// Get the summary Data
			List<DQSummaryBean> summaryList = iResultsDAO.getDQSummaryDataForidApp(idApp, summaryDate, summaryRun);

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", "DQ_Summary_" + idApp + ".csv");
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			String fileHeader = "Date,Run,ColumnName Or RuleName,Checks Type,Total Records,Failed Records";
			outStream.write(fileHeader.getBytes());
			outStream.write("\n".getBytes());

			for (DQSummaryBean dqSmry : summaryList) {
				outStream.write(dqSmry.toString().getBytes());
				outStream.write("\n".getBytes());
			}

			outStream.close();

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred by downloading the summary report!!");
			e.printStackTrace();

			try {
				response.sendRedirect("dashboard_table?idApp=" + sr_idApp);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}	

	@RequestMapping(value = "/dupstats")
	public ModelAndView dashboardDupStatView(HttpServletRequest req, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardDupStatView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Transaction_Detail_All");
			myListTable.add("DATA_QUALITY_Transaction_Detail_Identity");
			myListTable.add("DATA_QUALITY_Transaction_Summary");
			myListTable.add("DATA_QUALITY_DATA_DRIFT");
			myListTable.add("DATA_QUALITY_Duplicate_Check_Summary");
			modelAndView = common(req, session, modelAndView,myListTable);
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	@RequestMapping(value = "/validationIdApp", method = RequestMethod.POST)
	public void validationIdApp(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String projectid, @RequestParam String fromDate, @RequestParam String toDate) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("projectid..............=" + projectid);
		String idAppList = validationcheckdao.getValidationId(projectid, fromDate, toDate);


		try {
			String jsonInString = idAppList;
			//System.out.println("jsonInString=" + jsonInString);

			JSONObject idApp = new JSONObject();
			idApp.put("success", jsonInString);
			//System.out.println("displayName=" + idApp);

			response.getWriter().println(idApp);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/nullstats")
	public ModelAndView dashboardNullStatView(HttpServletRequest req, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardNullStatView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_NullCheck_Summary");
			myListTable.add("DATA_QUALITY_Column_Summary");
			myListTable.add("DATA_QUALITY_default_value");
			modelAndView = common(req, session, modelAndView, myListTable);
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/badData")
	public ModelAndView dashboardBadData(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardBadData");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Length_Check");
			myListTable.add("DATA_QUALITY_badData");
			myListTable.add("DATA_QUALITY_Unmatched_Pattern_Data");
			myListTable.add("DATA_QUALITY_Unmatched_Default_Pattern_Data");
			modelAndView = common(req, session, modelAndView, myListTable );

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/validity")
	public ModelAndView dashboardValidity(HttpServletRequest req, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardValidity");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Transactionset_sum_dgroup");
			modelAndView = common(req, session, modelAndView,myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");

	}

	@RequestMapping(value = "/stringstats")
	public ModelAndView dashboardStringStatView(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardStringStatView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY");
			myListTable.add("DATA_QUALITY_DATA_DRIFT_SUMMARY");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/numericstats")
	public ModelAndView dashboardNumericStatView(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardNumericStatView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Column_Summary");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/recordAnomaly")
	public ModelAndView recordAnomaly(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardRecordAnomaly");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Record_Anomaly");
			myListTable.add("DATA_QUALITY_History_Anomaly");
			myListTable.add("DATA_QUALITY_DateRule_Summary");
			myListTable.add("DATA_QUALITY_DateRule_FailedRecords");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/timelinessCheck")
	public ModelAndView timelinessCheck(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardTimelinessCheckView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_timeliness_check");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	/*@RequestMapping(value = "/processingData")
	public ModelAndView processingData(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardProcessingData");
			modelAndView = common(req, session, modelAndView);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}*/

	@RequestMapping(value = "/sqlRules")
	public ModelAndView sqlRulesResultData(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			System.out.println("Redirecting or loading dashboardSqlRulesResult.jsp with idApp req paramater ");
			ModelAndView modelAndView = new ModelAndView("dashboardSqlRulesResult");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("processData");
			myListTable.add("DATA_QUALITY_Rules");
			myListTable.add("DATA_QUALITY_GlobalRules");
			modelAndView = common(req, session, modelAndView, myListTable);
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/exceptions")
	public ModelAndView dashboardExceptions(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardExceptionsView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Transactionset_sum_A1");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/rootCauseAnalysis")
	public ModelAndView dashboardRootCauseAnalysis(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("dashboardRootCauseAnalysisView");
			List<String> myListTable = new ArrayList<String>();
			myListTable.add("DATA_QUALITY_Transactionset_sum_A1");
			modelAndView = common(req, session, modelAndView, myListTable);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	public ModelAndView common(HttpServletRequest req, HttpSession session, ModelAndView modelAndView, List<String> myListTable) {

		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		decimalFormat.setGroupingUsed(true);
		decimalFormat.setGroupingSize(3);
		String flagGrca="";
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {

			modelAndView.addObject("recordCount", 0);
			modelAndView.addObject("averageRecordCount", 0);
			modelAndView.addObject("nonNullColumns", 0);
			modelAndView.addObject("nonNullColumnsFailed", 0);
			modelAndView.addObject("allFields", 0);
			modelAndView.addObject("identityFields", 0);
			modelAndView.addObject("numberOfRecordsFailed", 0);
			modelAndView.addObject("numberofStringColumnsFailed", " ");
			modelAndView.addObject("numberofNumericalColumnsFailed", 0);


			if ((int) session.getAttribute("RunFilter") == 0) {
				modelAndView.addObject("Run", "");
			} else {
				modelAndView.addObject("Run", (int) session.getAttribute("RunFilter"));
			}

			List<DATA_QUALITY_Transaction_Summary> readTransaction_SummaryTable = null;
			SqlRowSet readTransaction_DetailTable;
			SqlRowSet readTransaction_Detail_IdentityTable;
			Long idApp = Long.parseLong(req.getParameter("idApp"));


			/*Avishkar: 22-04-2020 [Approval Process Status flag changes]*/
			String approvalStatusFlag = getApplicationApprovalProcessStatus(idApp);
			modelAndView.addObject("approvalStatusFlag",approvalStatusFlag);

			String sIsDownloadCsvDateRangeEnabled = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "isDateRange_DownloadCsv_Enhancements", "N");
			modelAndView.addObject("IsDownloadCsvDateRangeEnabled", sIsDownloadCsvDateRangeEnabled);

			// long idApp = iResultsDAO.getIdappFromListApplictions(idData);
			System.out.println("idApp=" + idApp);


			 long idData1 = iResultsDAO.getIdDataFromListApplictions(idApp);

			System.out.println("idData1=" + idData1);
//			long idData1 = Long.parseLong(req.getParameter("idData"));

			// Fetching template id and name and setting it to model object
			ListDataSource listDataSource = validationcheckdao.getTemplateDetailsForAppId(idApp);
			int advancedRulesListcount = listdatasourcedao.getAdvancedRulesCount(idData1);
			modelAndView.addObject("advancedRulesListcount", advancedRulesListcount);
//			System.out.println("advancedRulesListcount "+advancedRulesListcount);
			long templateId = 0l;
			String templateName = "";

			if (listDataSource != null) {
				templateId = listDataSource.getIdData();
				templateName = listDataSource.getName();
			}

			modelAndView.addObject("idData", templateId);
			modelAndView.addObject("templateName", templateName);

			/*SqlRowSet tableNamesFromResultMasterTable = iResultsDAO.getTableNamesFromResultMasterTable(idApp);*/

			Long nonNullColumnsFailed = 0l;
			Long numberofNumericalColumnsFailed = 0l;
			Long numberofStringColumnsFailed = 0l;
			Long recordAnomalyTotal = 0l;
			Long numberOfRecordsFailed = 0l;

			int maxRun = 1;
			String rcaTableName = "DATA_QUALITY_Transactionset_sum_A1";

			String maxRunSql = "select max(Run) from " + rcaTableName + " where Date=(select max(Date) from "
					+ rcaTableName + " where idApp=?) and idApp=?";
			maxRun = jdbcTemplate1.queryForObject(maxRunSql, Integer.class, idApp, idApp);

			Date maxDate = jdbcTemplate1.queryForObject(
					"select max(Date) from " + rcaTableName + " where idApp=?", Date.class, idApp);

			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
			String strMaxDate = dt.format(maxDate);



			// If RuleCatalog is enabled and staging is activated and the run is test run
			// then fetch the details from staging_listApplications table else from
			// listApplications table
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
			System.out.println("\n====> Rule catalog enabled: " + isRuleCatalogEnabled);

			boolean validationStagingEnabled =  ruleCatalogService.isValidationStagingActivated(idApp);
			System.out.println("\n====> Is Validation Staging enabled: " + validationStagingEnabled);
			System.out.println("\n====> Execution Date: " + strMaxDate);
			System.out.println("\n====> Execution Run : " + maxRun);

			boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);
			System.out.println("\n====> Is Test Run: " + isTestRun);

			ListApplications listApplicationsData = null;
			Map<String, String>  tranRuleMap = null;
			Map<String, String> listdftranruleData = new HashMap<>();

			if (isRuleCatalogEnabled && validationStagingEnabled && isTestRun) {
				System.out.println(
						"\n====> Validation is Approved and it is a Test Run, so fetching the details from staging tables");
				listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
				tranRuleMap = ruleCatalogDao.getDataFromStagingListDFTranRuleForMap(idApp);
				listdftranruleData.put(tranRuleMap.get("all"), tranRuleMap.get("identity"));

			} else {
				System.out.println("\n====> Fetching the details from actual tables");
				listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
				listdftranruleData = iResultsDAO.getdatafromlistdftranrule(idApp);
				tranRuleMap = iResultsDAO.getDataFromListDFTranRuleForMap(idApp);
			}

			modelAndView.addObject("listApplicationsData", listApplicationsData);
			modelAndView.addObject("listdftranruleData", listdftranruleData);

			flagGrca = listApplicationsData.getKeyGroupRecordCountAnomaly();
			modelAndView.addObject("flagGrca", flagGrca);
			System.out.println("flagGrca" +flagGrca);
			int i = 0;
			//while (tableNamesFromResultMasterTable.next()) {
			while (i < myListTable.size()) {

				/*String tableName = tableNamesFromResultMasterTable.getString("Table_Name");*/
				String tableName = myListTable.get(i);

				modelAndView.addObject("DataQualityRulesResultsTableName",
						"//csvFiles//" + idApp + "//" + strMaxDate + "//" + maxRun + "//rules-output");

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Rules")) {
					System.out.println("rules_results=" + tableName);
					SqlRowSet DATA_QUALITY_RULES = iResultsDAO.getRulesTableData(tableName,idApp);
					if (DATA_QUALITY_RULES != null) {

						modelAndView.addObject("DataQualityRules", DATA_QUALITY_RULES);
						modelAndView.addObject("DataQualityRulesTrue", "DataQualityRulesTrue");
					}
					modelAndView.addObject("DataQualityRulesTableName", tableName);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules")) {
					System.out.println("rules_results=" + tableName);
					SqlRowSet DATA_QUALITY_GLOBAL_RULES = iResultsDAO.getRulesTableData(tableName,idApp);
					if (DATA_QUALITY_GLOBAL_RULES != null) {
						String keyGroupRecordAnomaly="";
						if(listApplicationsData.getKeyGroupRecordCountAnomaly().equalsIgnoreCase("Y"))
							keyGroupRecordAnomaly="Y";

						modelAndView.addObject("DataQualityGlobalRules", DATA_QUALITY_GLOBAL_RULES);
						modelAndView.addObject("DataQualityGlobalRulesTrue", "DataQualityGlobalRulesTrue");
						modelAndView.addObject("keyGroupRecordAnomaly", keyGroupRecordAnomaly);

					}
					modelAndView.addObject("DataQualityGlobalRulesTableName", tableName);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
					SqlRowSet frequencyUpdateDateTableData = iResultsDAO.getFrequencyUpdateDateTableData(tableName, idApp);
					System.out.println("frequencyUpdateDateTableData=" + frequencyUpdateDateTableData);
					if (frequencyUpdateDateTableData != null) {
						modelAndView.addObject("frequencyUpdateDateTableData", frequencyUpdateDateTableData);
						modelAndView.addObject("frequencyUpdateDateTableDataTrue", "frequencyUpdateDateTableDataTrue");

					}
					// Group Equality
					String groupEqualityStatus = iResultsDAO.getDashBoardStatusForGroupEquality(idApp, tableName);
					modelAndView.addObject("groupEqualityStatus", groupEqualityStatus);
					System.out.println("groupEqualityStatus=" + groupEqualityStatus);

					modelAndView.addObject("ColSumm_TableName_Validity", tableName);
					modelAndView.addObject("sum_dgroup_tableName", tableName);
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")) {
					modelAndView.addObject("Transactionset_sum_A1_TableName", tableName);
					// graph
					List recordCountAnomalyGraphValues = iResultsDAO.getRecordCountAnomalyGraphValues(tableName);
					modelAndView.addObject("recordCountAnomalyGraphValues", recordCountAnomalyGraphValues);

					// RootCause Analysis
					modelAndView.addObject("row_summary_TableName", "DATA_QUALITY_Transactionset_sum_A1");
					modelAndView.addObject("row_summary_TableNameCsv",
							"//csvFiles//" + idApp + "//" + strMaxDate + "//" + maxRun + "//RowSummary");
					modelAndView.addObject("root_cause_AnalysisCsv",
							"//csvFiles//" + idApp + "//" + strMaxDate + "//" + maxRun + "//RootCauseAnalysisFinal");

					Long recordCount = iResultsDAO.getRecordCount(listApplicationsData, idApp);
					System.out.println("recordCount=" + recordCount);

					Long averageRecordCount = iResultsDAO.getAverageRecordCount(listApplicationsData, idApp);
					System.out.println("averageRecordCount=" + averageRecordCount);

					recordAnomalyTotal = iResultsDAO.getRecordAnomalyTotal(tableName, idApp);
					System.out.println("recordAnomalyTotal=" + recordAnomalyTotal);

					modelAndView.addObject("recordAnomalyTotal", decimalFormat.format(recordAnomalyTotal));

					// recordCountStatus
					String recordCountStatus = iResultsDAO.getRecordCountStatus(tableName,idApp);
					modelAndView.addObject("recordCountStatus", recordCountStatus);

					numberOfRecordsFailed = iResultsDAO.getNumberOfRecordsFailed(tableName, idApp);
					System.out.println("numberOfRecordsFailed=" + numberOfRecordsFailed);
					modelAndView.addObject("numberOfRecordsFailed", decimalFormat.format(numberOfRecordsFailed));

					List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderStatus = iResultsDAO
							.getfileNameandcolumnOrderValidationStatus(tableName, idApp);
					System.out.println("fileNameandcolumnOrderStatus=" + fileNameandcolumnOrderStatus);
					modelAndView.addObject("fileNameandcolumnOrderStatus", fileNameandcolumnOrderStatus);
				}

				/*if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
					numberofStringColumnsFailed = iResultsDAO.getnumberofStringColumnsFailed(tableName,
							listApplicationsData);
					System.out.println("numberofStringColumnsFailed=" + numberofStringColumnsFailed);
					numberofNumericalColumnsFailed = iResultsDAO.getnumberofNumericalColumnsFailed(tableName,
							listApplicationsData);

					modelAndView.addObject("numberofStringColumnsFailed",
							decimalFormat.format(numberofStringColumnsFailed));

					modelAndView.addObject("numberofNumericalColumnsFailed",
							decimalFormat.format(numberofNumericalColumnsFailed));
				}*/

				/*if (tableNamesFromResultMasterTable.getString("Table_Name").equals("processData")) {*/
				if (tableName.equalsIgnoreCase("processData")) {
					// recordCountStatus
					// String recordCountStatus =
					// iResultsDAO.getRecordCountStatus(tableName);
					modelAndView.addObject("processing_Data_TableName", tableName);
					modelAndView.addObject("idApp", idApp);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_All")) {
					modelAndView.addObject("Transaction_DetailAll_TableName", tableName);
					int tableSize = iResultsDAO.getTableSize(tableName);
					modelAndView.addObject("Transaction_DetailAll_TableSize", tableSize);
					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);
					System.out.println("readTransaction_DetailTable=" + readTransaction_DetailTable);
					if (readTransaction_DetailTable != null) {
						modelAndView.addObject("readTransaction_DetailTable", readTransaction_DetailTable);
						modelAndView.addObject("readTransaction_DetailTableTrue", "readTransaction_DetailTableTrue");
					}
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Detail_Identity")) {
					modelAndView.addObject("Transaction_DetailIdentity_TableName", tableName);
					readTransaction_Detail_IdentityTable = iResultsDAO.readTransaction_Detail_IdentityTable(tableName,idApp);
					System.out.println("readTransaction_Detail_IdentityTable=" + readTransaction_Detail_IdentityTable);
					if (readTransaction_Detail_IdentityTable != null) {
						modelAndView.addObject("readTransaction_Detail_IdentityTable",
								readTransaction_Detail_IdentityTable);
						modelAndView.addObject("readTransaction_Detail_IdentityTableTrue",
								"readTransaction_Detail_IdentityTableTrue");
					}
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_default_value")) {
					System.out.println("Default Value...");
					System.out.println(tableName);
					modelAndView.addObject("Default_value_TableName", tableName);

				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_badData")) {

					modelAndView.addObject("Bad_Data_TableName", tableName);
					modelAndView.addObject("date_rule_bad_data", tableName);
					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);
					System.out.println("readTransaction_DetailTable=" + readTransaction_DetailTable);
					if (readTransaction_DetailTable != null) {
						modelAndView.addObject("headerBadData", readTransaction_DetailTable);
						modelAndView.addObject("readTransaction_DetailTableTrue", "readTransaction_DetailTableTrue");
					}
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_SUMMARY")) {
					modelAndView.addObject("DataDrift_Summary_TableName", tableName);
					SqlRowSet dataDriftTable = iResultsDAO.getDataDriftTable(tableName, idApp);
					if (dataDriftTable != null) {
						modelAndView.addObject("dataDriftSummaryTable", dataDriftTable);
						modelAndView.addObject("dataDriftSummaryTableTrue", "dataDriftSummaryTableTrue");
					}
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_History_Anomaly")) {
					System.out.println(tableName);
					modelAndView.addObject("HistoryAnomaly_TableName", tableName);

					SqlRowSet data_Quality__History_anomaly = iResultsDAO.data_Quality__record_anomaly(tableName, idApp);
					// System.out.println("data_Quality__history_anomaly=" +
					// data_Quality__History_anomaly.first());
					if (data_Quality__History_anomaly != null) {
						modelAndView.addObject("data_Quality__history_anomaly", data_Quality__History_anomaly);
						modelAndView.addObject("data_Quality__history_anomalyTrue",
								"data_Quality__history_anomalyTrue");
					}
					modelAndView.addObject("HistoryAnomalyTableName", tableName);
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_timeliness_check")) {
					// System.out.println(tableName);
					modelAndView.addObject("timeliness_summary_TableName", tableName);
					modelAndView.addObject("timeliness_summary_TableNameCsv",
							"//csvFiles//" + idApp + "//" + strMaxDate + "//" + maxRun + "//TimelinessData");
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT")) {
					modelAndView.addObject("DataDrift_TableName", tableName);
					SqlRowSet dataDriftTable = iResultsDAO.getDataDriftTable(tableName, idApp);
					if (dataDriftTable != null) {
						modelAndView.addObject("dataDriftTable", dataDriftTable);
						modelAndView.addObject("dataDriftTableTrue", "dataDriftTableTrue");
					}
					modelAndView.addObject("dataDriftTable_DB", tableName);
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY")) {
					modelAndView.addObject("DataDrift_Count_Summary_TableName", tableName);
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_DateRule_Summary")) {
					System.out.println("DATE RULE...");
					System.out.println(tableName);
					// String tableName = "Date_Rule_check_validation_387";
					modelAndView.addObject("Date_Rule_TableName", tableName);
					System.out.println("Date_Rule_TableName" + tableName);
					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);
					System.out.println("DateRule_readTransaction_DetailTable=" + readTransaction_DetailTable);
					if (readTransaction_DetailTable != null) {
						modelAndView.addObject("Header_DateRule", readTransaction_DetailTable);
						modelAndView.addObject("DateRule_readTransaction_DetailTableTrue",
								"readTransaction_DetailTableTrue");
					}
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_DateRule_FailedRecords")) {
					System.out.println("DATE RULE123...");
					System.out.println(tableName);
					// String tableName = "FailedRecords_1679";
					modelAndView.addObject("Failed_Date_Rule_TableName", tableName);
					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);
					System.out.println("failedDateRule_readTransaction_DetailTable=" + readTransaction_DetailTable);
					if (readTransaction_DetailTable != null) {
						modelAndView.addObject("headerFailedDateRule", readTransaction_DetailTable);
						modelAndView.addObject("failedDateRule_readTransaction_DetailTableTrue",
								"readTransaction_DetailTableTrue");
					}
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Unmatched_Pattern_Data")) {
					System.out.println("Pattern...");
					System.out.println(tableName);
					modelAndView.addObject("Pattern_Bad_Data_TableName", tableName);
				}
				
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Unmatched_Default_Pattern_Data")) {
					modelAndView.addObject("Default_Pattern_TableName", tableName);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
					System.out.println("DataQualityResultController : common : tableName :: "+tableName);
					modelAndView.addObject("tableNameOrphan", tableName);
					modelAndView.addObject("ColSumm_TableName", tableName);
					modelAndView.addObject("Custom_microseg_ColSumm_TableName", "DATA_QUALITY_Custom_Column_Summary");
					nonNullColumnsFailed = iResultsDAO.getnonNullColumnsFailed(tableName, listApplicationsData);
					System.out.println("nonNullColumnsFailed=" + nonNullColumnsFailed);
					modelAndView.addObject("nonNullColumnsFailed", decimalFormat.format(nonNullColumnsFailed));

					numberofStringColumnsFailed = iResultsDAO.getnumberofStringColumnsFailed(tableName,
							listApplicationsData);
					System.out.println("numberofStringColumnsFailed=" + numberofStringColumnsFailed);
					numberofNumericalColumnsFailed = iResultsDAO.getnumberofNumericalColumnsFailed(tableName,
							listApplicationsData);
					System.out.println("numberofNumericalColumnsFailed=" + numberofNumericalColumnsFailed);
					/*
					 * if(numberofStringColumnsFailed == 0) { numberofStringColumnsFailed = null;
					 * modelAndView.addObject("numberofStringColumnsFailed",
					 * decimalFormat.format(numberofStringColumnsFailed)); }
					 */
					modelAndView.addObject("numberofStringColumnsFailed",
							decimalFormat.format(numberofStringColumnsFailed));
//					System.out.println(" numberofStringColumnsFailed :: "+numberofStringColumnsFailed);
					modelAndView.addObject("numberofNumericalColumnsFailed",
							decimalFormat.format(numberofNumericalColumnsFailed));

					//Object[] dataAndCount = iMatchingResultService.getDataFromResultTable(tableName, idApp);

					//modelAndView.addObject("columnNamesForColumnSumm",dataAndCount[0]);

					/*
					 * // graphs List nullCountGraphValues =
					 * iResultsDAO.getNullCountGraph(tableName);
					 * modelAndView.addObject("nullCountGraphValues",
					 * nullCountGraphValues);
					 *
					 * List stringFieldStatsGraph =
					 * iResultsDAO.getStringFieldStatsGraph(tableName);
					 * modelAndView.addObject("stringFieldStatsGraph",
					 * stringFieldStatsGraph);
					 *
					 * List numericalFieldStatsGraph =
					 * iResultsDAO.getNumericalFieldStatsGraph(tableName);
					 * modelAndView.addObject("numericalFieldStatsGraph", numericalFieldStatsGraph);
					 */
				}
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Summary")) {
					modelAndView.addObject("Transaction_summary_TableName", tableName);

					Long allFields = iResultsDAO.getAllFields(tableName);
					System.out.println("allFields=" + allFields);

					Long identityFields = iResultsDAO.getIdentityFields(tableName);
					System.out.println("identityFields=" + identityFields);

					modelAndView.addObject("allFields", allFields);
					modelAndView.addObject("identityFields", decimalFormat.format(identityFields));

					readTransaction_SummaryTable = iResultsDAO.readTransaction_SummaryTable(tableName,idApp);
					System.out.println("readTransaction_SummaryTable=" + readTransaction_SummaryTable);

					modelAndView.addObject("readTransaction_SummaryTable", readTransaction_SummaryTable);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
					modelAndView.addObject("Transaction_dupCheckSum_selected_fields_TableName", tableName);

					Long allFields = iResultsDAO.getAllFields(tableName);
					System.out.println("allFields=" + allFields);

					modelAndView.addObject("allFields", allFields);

					readTransaction_SummaryTable = iResultsDAO.readTransaction_SummaryTable(tableName,idApp);
					System.out.println("readTransaction_SummaryTable=" + readTransaction_SummaryTable);

					modelAndView.addObject("readTransaction_SummaryTable", readTransaction_SummaryTable);
				}

				if (tableName.equalsIgnoreCase("DATA_QUALITY_Record_Anomaly")) {
					System.out.println("Record Anomaly..");
					System.out.println(tableName);
					// String tableName = "Date_Rule_check_validation_387";
					modelAndView.addObject("RecordAnomaly_TableName", tableName);

					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);
					System.out.println("RecordAnomaly_readTransaction_DetailTable=" + readTransaction_DetailTable);
					modelAndView.addObject("headerRecordAnomaly",listApplicationsData.getKeyGroupRecordCountAnomaly());
				}

				//---------- [priyanka 25-12-2018] --
				if(tableName.equalsIgnoreCase("DATA_QUALITY_Length_Check")) {
					//String tableName = "data_quality_2540_Length_Check";
					System.out.println("DataQualityResultsController > _Length_Check > "+tableName );
					modelAndView.addObject("LengthCheck_TableName",tableName);
					readTransaction_DetailTable = iResultsDAO.readTransaction_DetailTable(tableName, idApp);

					System.out.println("DataQualityResultsController > LengthCheck_TableName ="+readTransaction_DetailTable);


					if(readTransaction_DetailTable !=null) {
						//modelAndView.addObject("headerLengthCheck",listApplicationsData.get)
						modelAndView.addObject("headerLengthCheck", readTransaction_DetailTable);
						modelAndView.addObject("Length_Check_TableTrue",
								"Length_Check_TableTrue");
					}
				}
				//-------------


				//---------- [Sreelakshmi 12-03-2019] --
				if(tableName.equalsIgnoreCase("DATA_QUALITY_NullCheck_Summary")) {
					// String tableName = "data_quality_2540_Length_Check";
					System.out.println("DataQualityResultsController >_NullCheck_Summary > " + tableName);
					modelAndView.addObject("Null_Summary_TableName", tableName);
				}
				// -------------
			//}
			 i++;	
			}

			Long idData = 0l;
			SqlRowSet idDataAndAppName = iResultsDAO.getIdDataAndAppNameFromListApplications(idApp);
			while (idDataAndAppName.next()) {
				idData = idDataAndAppName.getLong("idData");
				modelAndView.addObject("applicationName", idDataAndAppName.getString("name"));
			}

			System.out.println("idData=" + idData);
			System.out.println("in dashboardDupStatView");
			// List<ListDataSchema> listDataSchema =
			// validationService.getData();
			//Long projectId= (Long)session.getAttribute("projectId");
			Long projectId = projectDao.getProjectIdfromListAppTable(idApp);
			System.out.println("selected projectId : "+ projectId);
			String projectName = projectDao.getProjectNameByProjectid(projectId);
			System.out.println("selected projectName : "+ projectName);
			modelAndView.addObject("projectName", projectName);
			// passfail
			List<DATA_QUALITY_Transactionset_sum_A1> fileNameandcolumnOrderStatus = iResultsDAO
					.getfileNameandcolumnOrderValidationStatus("DATA_QUALITY_Transactionset_sum_A1", idApp);
			modelAndView.addObject("fileNameandcolumnOrderStatus", fileNameandcolumnOrderStatus);
			modelAndView.addObject("idApp", idApp);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "View");
			// dashboard missing dates
			if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
				modelAndView.addObject("incrementalMatching", listApplicationsData.getIncrementalMatching());
				// String missingDates =
				// iResultsDAO.getMissingDatesFromTransactionset_sum_A1(idApp);
				// modelAndView.addObject("missingDates", missingDates);
			}

			/*String recordCountAnomaly = listApplicationsData.getRecordCountAnomaly();*/
			// Record Anomaly Status on Dashboard
			// Record Count Fingerprint
			// RCA Dashboard
			/*String rcaStatus = iResultsDAO.getDashboardStatusForRCA(idApp, recordCountAnomaly);
			modelAndView.addObject("recordCountStatus", rcaStatus);*/

			// nullcountStatus Dashboard
			// String nullcountStatus = "";
			// modelAndView.addObject("nullCountScore", 0.0);
			/*
			 * if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
			 * nullcountStatus = iResultsDAO.getDashboardStatusForNullCount(idApp);
			 * modelAndView.addObject("nullCountStatus", nullcountStatus); Long
			 * nonNullColumns = iResultsDAO.getnonNullColumns(idData);
			 * modelAndView.addObject("nonNullColumns",
			 * decimalFormat.format(nonNullColumns)); nonNullColumnsFailed =
			 * iResultsDAO.getnonNullColumnsFailed("DATA_QUALITY_" + idApp +
			 * "_Column_Summary", listApplicationsData);
			 * System.out.println("nonNullColumnsFailed=" + nonNullColumnsFailed);
			 * modelAndView.addObject("nonNullColumnsFailed",
			 * decimalFormat.format(nonNullColumnsFailed)); }
			 */
			//
			/*
			 * if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase( "Y")) {
			 * Long numberofNumericalColumnsYes =
			 * iResultsDAO.getNumberofNumericalColumnsYes(idData);
			 * System.out.println("numberofNumericalColumnsYes=" +
			 * numberofNumericalColumnsYes);
			 * modelAndView.addObject("numberofNumericalColumnsYes",
			 * decimalFormat.format(numberofNumericalColumnsYes)); //
			 * numericalFieldStatsStatus Dashboard String numericalFieldStatus =
			 * iResultsDAO.getDashboardStatusFornumericalFieldStatsStatus(idApp) ;
			 * modelAndView.addObject("numericalFieldStatsStatus", numericalFieldStatus); }
			 */
			// stringFieldStatsStatus Dashboard
			
			
			if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
				String stringFieldStatus = iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);
				modelAndView.addObject("stringFieldStatsStatus", stringFieldStatus);
				Long numberofStringColumnsYes = iResultsDAO.getNumberofStringColumnsYes(idData);
				System.out.println("numberofStringColumnsYes=" + numberofStringColumnsYes);
				modelAndView.addObject("numberofStringColumnsYes", decimalFormat.format(numberofStringColumnsYes));
			}
			
			
			
			// recordAnomalyStatus Dashboard
			/*
			 * if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase( "Y")) {
			 * String recordAnomalystatus =
			 * iResultsDAO.getDashboardStatusForRecordAnomalyStatus(idApp);
			 * System.out.println("recordAnomalystatus=" + recordAnomalystatus);
			 * modelAndView.addObject("recordAnomalyStatus", recordAnomalystatus); //
			 * numberOfRecordsFailed numberOfRecordsFailed = iResultsDAO
			 * .getNumberOfRecordsFailed("DATA_QUALITY_" + idApp + "_Transactionset_sum_A1",
			 * idApp); System.out.println("numberOfRecordsFailed=" + numberOfRecordsFailed);
			 * modelAndView.addObject("numberOfRecordsFailed",
			 * decimalFormat.format(numberOfRecordsFailed)); String recordAnomalyNewStatus =
			 * iResultsDAO.getStatusFromRecordAnomalyTable(idApp);
			 * modelAndView.addObject("recordAnomalyStatus", recordAnomalyNewStatus); }
			 */
			// dataDriftStatus Dashboard
			/*
			 * if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) { String
			 * dataDriftStatus = iResultsDAO.getDashboardStatusForDataDriftStatus(idApp);
			 * modelAndView.addObject("dataDriftStatus", dataDriftStatus);
			 * System.out.println("dataDriftStatus=" + dataDriftStatus); } if
			 * (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y") ) { String
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
			 */
			// allFieldsStatus Dashboard
			/*
			 * modelAndView.addObject("allFieldsScore", 0.0); if
			 * (tranRuleMap.get("all").equalsIgnoreCase("Y")) { String allFieldsStatus =
			 * iResultsDAO.getDashboardStatusForAllFieldsStatus(idApp);
			 * modelAndView.addObject("allFieldsStatus", allFieldsStatus);
			 * System.out.println("allFieldsStatus=" + allFieldsStatus); } //
			 * identityfieldsStatus Dashboard modelAndView.addObject("identityFieldsScore",
			 * 0.0); if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) { String
			 * identityfieldsStatus =
			 * iResultsDAO.getDashboardStatusForIdentityfieldsStatus(idApp);
			 * modelAndView.addObject("identityfieldsStatus", identityfieldsStatus); }
			 */
			// Summary of Last Run (Date) for Validation Check
			String dateForDashboard = iResultsDAO.getDateForSummaryOfLastRun(idApp);
			modelAndView.addObject("dateForDashboard", dateForDashboard);
			// Score

			/*boolean calculateScore = iResultsDAO.checkRunIntheResultTableForScore(idApp, recordCountAnomaly);*/
			/*System.out.println("calculateScore=" + calculateScore);*/

			// calculateScore
			int totalCount = 0;
			Double totalDQI = 0.0;
			double avgDQI = 0.0;
			if (true) {

				DecimalFormat df = new DecimalFormat("#.00");
				// RecordAnomaly

				/*
				 * String recordAnomalyScore =
				 * iResultsDAO.CalculateScoreForRecordAnomaly(idData, idApp, recordCountAnomaly,
				 * rcaStatus, listApplicationsData); if (recordAnomalyScore.contains("∞")) {
				 * recordAnomalyScore = "0.0"; } modelAndView.addObject("recordAnomalyScore",
				 * recordAnomalyScore); totalDQI = totalDQI +
				 * Double.valueOf(recordAnomalyScore); totalCount++;
				 * System.out.println("recordAnomalyScore=" + recordAnomalyScore);
				 */
				/*
				 * modelAndView.addObject("RCAStatus", "passed");
				 * modelAndView.addObject("recordAnomalyScore", "30.0");
				 * modelAndView.addObject("RCAKey_Matric_1", "10");
				 * modelAndView.addObject("RCAKey_Matric_2", "20");
				 */
				try {
					modelAndView.addObject("recordAnomalyScore", "0.0");
					if (true) {

						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Record Count Fingerprint", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						String Status = "0.0";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty",0);
							modelAndView.addObject("recordAnomalyScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("recordAnomalyScore", PercentageDF);

						}

						modelAndView.addObject("RCAStatus", status);


						//FORMAT(RecordCount, 0) as RecordCount1
					/*	DecimalFormat dMeasurement = new DecimalFormat(",###");
						double val = Double.parseDouble(Key_Matric_1);

						System.out.println("val =>"+val);

						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Key_Matric_1 => "+dMeasurement.format(val));

						String mVal = dMeasurement.format(val);
						*/

						modelAndView.addObject("RCAKey_Matric_1", Key_Matric_1);
						if(status.equals("passed")) {
							/*System.out.println("  status ::::: "+status);
							System.out.println(" Key_Matric_2 ::::: "+Key_Matric_2);*/
							 Key_Matric_2 = Status;

						}
						modelAndView.addObject("RCAKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("RCAKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						//RCA condition

						System.out.println("===================maxRun==="+maxRun);
						if(maxRun > 2) {
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
						/*if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}*/


						System.out.println("-------------RCA Total count===="+totalCount);

					}
				} catch (Exception E) {
					E.getMessage();
				}

				// BadData
				try {
					modelAndView.addObject("badDataScore", 0.0);
					if (listApplicationsData.getBadData().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Bad_Data",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("badDataScore", 0.0);
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("badDataScore", PercentageDF);

						}
						modelAndView.addObject("badDataStatus", status);
						modelAndView.addObject("badDataKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("badDataKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("badDataKey_Matric_3", Key_Matric_3.replace(",", "<br>"));


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}

						System.out.println("-------------badData Total count===="+totalCount);
					}
				} catch (Exception E) {
					E.getMessage();
				}
				//pattern unmatch  11-July-2019  by pravin

				try {
					modelAndView.addObject("patternDataScore", 0.0);
					if (listApplicationsData.getPatternCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Pattern_Data",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("patternDataScore", 0.0);
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							System.out.println("percentage :: "+PercentageDF);
							modelAndView.addObject("patternDataScore", PercentageDF);

						}
						modelAndView.addObject("patternDataStatus", status);

						DecimalFormat df12 = new DecimalFormat(",###");
						String key_mat1 = df12.format(Double.parseDouble(Key_Matric_1));
						String key_mat2 = df12.format(Double.parseDouble(Key_Matric_2));


						System.out.println("------- In controller patternDataKey_Matric_1 =>"+Key_Matric_1);
						System.out.println("------- In controller patternDataKey_Matric_2 =>"+Key_Matric_2);

						modelAndView.addObject("patternDataKey_Matric_1", key_mat1);
						modelAndView.addObject("patternDataKey_Matric_2", key_mat2);
						modelAndView.addObject("patternDataKey_Matric_3", Key_Matric_3.replace(",", "<br>"));




						/*
						modelAndView.addObject("patternDataKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("patternDataKey_Matric_2", Key_Matric_2);*/

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}

						System.out.println("-------------patternData Total count===="+totalCount);
					}
				} catch (Exception E) {
					E.getMessage();
				}
				
				try {
					modelAndView.addObject("defaultPatternDataScore", 0.0);
					if (listApplicationsData.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Default_Pattern_Data",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("defaultPatternDataScore", 0.0);
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							System.out.println("percentage :: "+PercentageDF);
							modelAndView.addObject("defaultPatternDataScore", PercentageDF);

						}
						modelAndView.addObject("defaultPatternDataStatus", status);

						DecimalFormat df12 = new DecimalFormat(",###");
						String key_mat1 = df12.format(Double.parseDouble(Key_Matric_1));
						String key_mat2 = df12.format(Double.parseDouble(Key_Matric_2));


						System.out.println("------- In controller patternDataKey_Matric_1 =>"+Key_Matric_1);
						System.out.println("------- In controller patternDataKey_Matric_2 =>"+Key_Matric_2);

						modelAndView.addObject("defaultPatternDataKey_Matric_1", key_mat1);
						modelAndView.addObject("defaultPatternDataKey_Matric_2", key_mat2);
						modelAndView.addObject("defaultPatternDataKey_Matric_3", Key_Matric_3.replace(",", "<br>"));


						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}

						System.out.println("-------------defaultpatternData Total count===="+totalCount);
					}
				} catch (Exception E) {
					E.getMessage();
				}


				// Date Rule changes 8jan2019 priyanka
				try {
					modelAndView.addObject("dateRuleCheckScore", 0.0);
					if (listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y") ||
							listApplicationsData.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_DateRuleCheck", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("dateRuleCheckScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("dateRuleCheckScore", PercentageDF);

						}

						modelAndView.addObject("dateRuleChkStatus", status);
						modelAndView.addObject("dateRuleKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("dateRuleKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("dateRuleKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if (status != null) {
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);

							totalCount++;
						}
					}
				} catch (Exception E) {
					E.getMessage();
				}

				// Numerical Field
				try {
					modelAndView.addObject("numericalFieldScore", "0.0");
					if (listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Numerical Field Fingerprint", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("numericalFieldScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("numericalFieldScore", PercentageDF);

						}

						modelAndView.addObject("numericalFieldStatsStatus", status);

						modelAndView.addObject("numericalFieldKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("numericalFieldKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("numericalFieldKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
					/*	if(status != null){

							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}*/

						//For NumStat
						if(maxRun > 2) {
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}

						System.out.println("-------------numericalFieldStats Total count===="+totalCount);
					}
				} catch (Exception E) {
					E.getMessage();
				}
				// string Field
				modelAndView.addObject("stringFieldScore", 0.0);
				if (listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
					String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData, idApp, "",
							listApplicationsData);
					if (stringFieldScore.contains("∞")) {
						stringFieldScore = "0";
					}
					modelAndView.addObject("stringFieldScore", stringFieldScore);

					String stringFieldStatus = iResultsDAO.getDashboardStatusForstringFieldStatsStatus(idApp);

					if(stringFieldStatus != null){
						totalDQI = totalDQI + Double.valueOf(stringFieldScore);
						totalDQI= Math.floor(totalDQI);
						totalCount++;
					}
					System.out.println("-------------stringField Total count===="+totalCount);
				}
				// NullCountScore
				try {
					modelAndView.addObject("nullCountScore", 0.0);
					if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Completeness",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("nullCountScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("nullCountScore", PercentageDF);

						}
						modelAndView.addObject("nullCountStatus", status);
						modelAndView.addObject("nullCountKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("nullCountKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("nullCountKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}

					}
				} catch (Exception E) {
					E.getMessage();
				}
				// All FieldsScore
				try {
					modelAndView.addObject("allFieldsScore", "0.0");
					if (tranRuleMap.get("all").equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Uniqueness -Seleted Fields", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("allFieldsScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("allFieldsScore", PercentageDF);

						}
						modelAndView.addObject("allFieldsStatus", status);
						modelAndView.addObject("allFieldsKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("allFieldsKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("allFieldsKey_Matric_3", Key_Matric_3.replace(",", "<br>"));

						if(status != null){

							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}
				} catch (Exception E) {
					E.getMessage();
				}
				// identityFieldsScore
				try {
					modelAndView.addObject("identityFieldsScore", 0.0);
					if (tranRuleMap.get("identity").equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Uniqueness -Primary Keys", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("identityFieldsScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("identityFieldsScore", PercentageDF);

						}
						modelAndView.addObject("identityfieldsStatus", status);
						modelAndView.addObject("identityfieldsKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("identityfieldsKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("identityfieldsKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}
				} catch (Exception E) {
					E.getMessage();
				}
				// Record Fingerprint
				try {
					modelAndView.addObject("recordFieldScore", 0.0);
					if (listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Record Anomaly", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("recordFieldScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("recordFieldScore", PercentageDF);

						}
						modelAndView.addObject("recordAnomalyStatus", status);
						modelAndView.addObject("recordAnomalyKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("recordAnomalyKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("recordAnomalyKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}
				} catch (Exception E) {
					E.getMessage();
				}
				// Length Check
				try {
					modelAndView.addObject("lengthCheckScore", 0.0);
                    if (!listApplicationsData.getlengthCheck().equalsIgnoreCase("Y") &&  listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {

                        String DQI = "";
                        String status = "";
                        String Key_Matric_1 = "";
                        String Key_Matric_2 = "";
                        String Key_Matric_3 = "";
                        double dqi_val=0.0,metric1_val=0.0,metric2_val=0.0;

                        SqlRowSet dashboardDetails = null;

                        if(listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")){
                            dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
                                    "DQ_MaxLengthCheck", listApplicationsData);
                            if(dashboardDetails!=null){
                                while (dashboardDetails.next()) {
                                    dqi_val = dashboardDetails.getDouble(4);
                                    status = dashboardDetails.getString(5);
                                    metric1_val = dashboardDetails.getDouble(6);
                                    metric2_val = dashboardDetails.getDouble(7);
                                    Key_Matric_3 = dashboardDetails.getString(8);
                                }
                            }

                        }

                        DQI = ""+dqi_val;

                        Key_Matric_1= ""+metric1_val;
                        Key_Matric_2= ""+metric2_val;

                        String PercentageDF = "0";
                        if (DQI == null || DQI.trim().isEmpty()) {
                            modelAndView.addObject("lengthCheckScore", "0.0");
                        } else {

                            double Percentage = Double.parseDouble(DQI);
                            Percentage = Math.floor(Percentage);
                            DecimalFormat df1 = new DecimalFormat("#0.0");
                            PercentageDF = df1.format(Percentage);
                            modelAndView.addObject("lengthCheckScore", PercentageDF);

                        }
                        System.out.println("MaxLengthStatus="+status);
                        System.out.println("maxLengthCheckScore="+PercentageDF);

                        modelAndView.addObject("MaxLengthStatus", status);
                        modelAndView.addObject("MaxLengthKey_Matric_1", Key_Matric_1);
                        modelAndView.addObject("MaxLengthKey_Matric_2", Key_Matric_2);
                        modelAndView.addObject("MaxLengthKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
                        if(status != null){
                            totalDQI = totalDQI + Double.valueOf(PercentageDF);
                            totalDQI= Math.floor(totalDQI);
                            totalCount++;
                        }
                        System.out.println("-------------Max length Total count===="+totalCount);
                        System.out.println("-------------Total DQI===="+totalDQI);
                    }else if(listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")){
					//if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y") || listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {

							String DQI = "";
							String status = "";
							String Key_Matric_1 = "";
							String Key_Matric_2 = "";
							String Key_Matric_3 = "";
							double dqi_val=0.0,metric1_val=0.0,metric2_val=0.0;

							SqlRowSet dashboardDetails = null;
							int avgFactor=0;

							if(listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")){
								dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
										"DQ_LengthCheck", listApplicationsData);
								if(dashboardDetails!=null){
									while (dashboardDetails.next()) {
										dqi_val = dashboardDetails.getDouble(4);
										status = dashboardDetails.getString(5);
										metric1_val = dashboardDetails.getDouble(6);
										metric2_val = dashboardDetails.getDouble(7);
										Key_Matric_3 = dashboardDetails.getString(8);
									}
									avgFactor++;
								}

							}

							if(listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")){
								dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
										"DQ_MaxLengthCheck", listApplicationsData);

								if(dashboardDetails!=null){
									while (dashboardDetails.next()) {
										dqi_val = dqi_val + dashboardDetails.getDouble(4);
										if(status==null || status.trim().isEmpty() || status.equalsIgnoreCase("passed"))
											status = dashboardDetails.getString(5);
										metric1_val = metric1_val + dashboardDetails.getDouble(6);
										metric2_val = metric2_val + dashboardDetails.getDouble(7);
										Key_Matric_3 = (Key_Matric_3 != null && !Key_Matric_3.trim().isEmpty())
												? Key_Matric_3 + "," + dashboardDetails.getString(8)
												: dashboardDetails.getString(8);
									}
									avgFactor++;
								}

							}

							DQI = ""+(dqi_val/avgFactor);

							Key_Matric_1= ""+metric1_val;
							Key_Matric_2= ""+metric2_val;

							String PercentageDF = "0";
							if (DQI == null || DQI.trim().isEmpty()) {
								// modelAndView.addObject("showDQIEmpty", 0);
								modelAndView.addObject("lengthCheckScore", "0.0");
							} else {
								// modelAndView.addObject("showDQIEmpty",null);
								double Percentage = Double.parseDouble(DQI);
								Percentage = Math.floor(Percentage);
								DecimalFormat df1 = new DecimalFormat("#0.0");
								PercentageDF = df1.format(Percentage);
								modelAndView.addObject("lengthCheckScore", PercentageDF);

							}

							modelAndView.addObject("lengthStatus", status);
							modelAndView.addObject("lengthKey_Matric_1", Key_Matric_1);
							modelAndView.addObject("lengthKey_Matric_2", Key_Matric_2);
							modelAndView.addObject("lengthKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
							if(status != null){
								totalDQI = totalDQI + Double.valueOf(PercentageDF);
								totalDQI= Math.floor(totalDQI);
								totalCount++;
							}
							System.out.println("-------------length Total count===="+totalCount);
							System.out.println("lengthCheckScore="+PercentageDF);
                        	System.out.println("lengthStatus="+status);
						}


				} catch (Exception E) {
					E.getMessage();
				}
   Integer custrule_count=0;
   Integer globalrule_count=0;

				// RegexCheck
   try {
				SqlRowSet rowSet=iResultsDAO.custom_rules_configured_count(idData);

			if (!rowSet.next()) {
				System.out.println("no results");
				custrule_count = -1;
			} else {
				custrule_count = rowSet.getInt(1);
			}
			modelAndView.addObject("custrule_count", custrule_count);
			System.out.println("custom rule configured ::" + rowSet.getInt(1));
		} catch (Exception e) {
			e.printStackTrace();
		}



   SqlRowSet rowSet1=iResultsDAO.global_rules_configured_count(idData);
   try {
   if (!rowSet1.next()) {
		System.out.println("no results");
		globalrule_count = -1;
	} else {
		globalrule_count = rowSet1.getInt(1);
	}
   modelAndView.addObject("globalrule_count", globalrule_count);
	System.out.println("global rule configured ::" + rowSet1.getInt(1));
} catch (Exception e) {
	e.printStackTrace();
}


				try {
					modelAndView.addObject("ruleScoreDF", 0.0);
					if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && custrule_count > 0) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Rules", listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = null;
						String Key_Matric_2 = null;
						String Key_Matric_3 = "";
						String DqRules ="DQ_Rules";
						//session.setAttribute("DQ_Rules", DqRules);
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("ruleScoreDF", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("ruleScoreDF", PercentageDF);

						}
						modelAndView.addObject("ruleStatus", status);
						modelAndView.addObject("ruleKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("ruleKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("ruleKey_Matric_3", Key_Matric_3.replace(",", "<br>"));

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}
				} catch (Exception E) {
					E.getMessage();
				}
				/*}

				//for global rule 7-06-19
				else if(globalrule_count>=1)
				{*/
				try {
					modelAndView.addObject("GlobalruleScoreDF", 0.0);
					if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && globalrule_count > 0) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_GlobalRules",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = null;
						String Key_Matric_2 = null;

						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("GlobalruleScoreDF", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("GlobalruleScoreDF", PercentageDF);

						}
						modelAndView.addObject("GlobalruleStatus", status);
						modelAndView.addObject("ruleKey_Matric_3", Key_Matric_1);
						modelAndView.addObject("ruleKey_Matric_4", Key_Matric_2);

						if (status != null) {
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
						System.out.println("-------------rule Total count===="+totalCount);

					}
				} catch (Exception E) {
					E.getMessage();
				}


				// datadrift
				try {
					modelAndView.addObject("dataDriftScore", 0);
					if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y") || listApplicationsData.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Data Drift",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						String Status = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("dataDriftScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							modelAndView.addObject("dataDriftScore", "100.0");
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
								System.out.println("======= In Else ===== DataDrift DQI ==PercentageDF =>"+PercentageDF);
							modelAndView.addObject("dataDriftScore", PercentageDF);

						}
						/*
						 * if(status.equals("")) { Key_Matric_2=Status; }
						 */
						modelAndView.addObject("dataDriftStatus", status);
						modelAndView.addObject("dataDriftKey_Matric_1", Key_Matric_1);
						try {
						if (status==null || status.equals("")) {
							Key_Matric_2 = "0";
						}
						}catch(Exception e) {
							e.printStackTrace();
						}
						modelAndView.addObject("dataDriftKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("dataDriftKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if (status != null || status == null) {
							System.out.println("======= In Else ===== DataDrift DQI ==PercentageDF =>"+PercentageDF);

							modelAndView.addObject("dataDriftScore", PercentageDF);

						}
						/*if(status.equals("")) {
							Key_Matric_2=Status;
						}*/
						modelAndView.addObject("dataDriftStatus", status);
						modelAndView.addObject("dataDriftKey_Matric_1", Key_Matric_1);
						try {
						if(status == null || status.equals("")) {
							Key_Matric_2="0";
						}
						}catch(Exception e) {
							e.printStackTrace();
						}
						modelAndView.addObject("dataDriftKey_Matric_2", Key_Matric_2);

						if(status != null || status == null) {
						totalDQI = totalDQI + Double.valueOf(PercentageDF);
						totalDQI= Math.floor(totalDQI);
						totalCount++;
						}

						/*if(status != null || status == null){
							if(maxRun == 1) {avgDQI = 100.0;
							totalCount++;}
							else if (maxRun >=2) {
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}*/
						/*if(DQI != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}*/

						System.out.println("-------------dataDrift Total count===="+totalCount);

					}
				} catch (Exception E) {
					E.getMessage();
				}
				// Timeliness
				try {
					modelAndView.addObject("TimelinessCheckScore", 0);
					if (listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_Timeliness",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("TimelinessCheckScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("TimelinessCheckScore", PercentageDF);

						}
						modelAndView.addObject("TimelinessCheckStatus", status);
						modelAndView.addObject("TimelinessCheckKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("TimelinessCheckKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("TimelinessCheckKey_Matric_3", Key_Matric_3.replace(",", "<br>"));
						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}

				} catch (Exception E) {
					E.getMessage();
				}


				//for SQL-Rules
				//listApplicationsData.getApplyRules().
				//DQ_Sql_Rule

				Integer sqlrule_count=0;

					// RegexCheck

					String strIdApp = idApp.toString();

				   rowSet1=iResultsDAO.sql_rules_configured_count(strIdApp);
				   try {
				   if (!rowSet1.next()) {
						System.out.println("no results");
						sqlrule_count = -1;
					} else {
						sqlrule_count = rowSet1.getInt(1);
					}
				   modelAndView.addObject("sqlrule_count", sqlrule_count);
					System.out.println("=============sql rule configured ::" + rowSet1.getInt(1));
				} catch (Exception e) {
					e.printStackTrace();
				}



				try {
					modelAndView.addObject("sqlRuleScoreDF", 0.0);
					if (listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && sqlrule_count > 0) {
						/*SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp,
								"DQ_Sql_Rule", listApplicationsData);*/

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						LinkedHashMap<String, String> oSqlRulesSummary = new LinkedHashMap<String, String>();

						oSqlRulesSummary = iResultsDAO.getSqlRulesDashboardSummary(req.getParameter("idApp"));


						String PercentageDF = "0";

						DateUtility.DebugLog("SQL Row line", String.format("%1$s,%2$s,%3$s", PercentageDF,oSqlRulesSummary.get("SqlRuleCount"),oSqlRulesSummary.get("SqlRuleFailed")));

						PercentageDF = oSqlRulesSummary.get("SqlRuleDqi");
						status =  oSqlRulesSummary.get("SqlRuleFailed");

						modelAndView.addObject("sqlRuleScoreDF", PercentageDF);
						modelAndView.addObject("sqlRuleStatus", oSqlRulesSummary.get("SqlRuleStatus"));
						modelAndView.addObject("ruleKey_Matric_5", oSqlRulesSummary.get("SqlRuleCount"));
						modelAndView.addObject("ruleKey_Matric_6", oSqlRulesSummary.get("SqlRuleFailed"));


					/*	while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);

						}
						System.out.println("");

						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("sqlRuleScoreDF", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("sqlRuleScoreDF", PercentageDF);

						}
						modelAndView.addObject("sqlRuleStatus", status);
						modelAndView.addObject("ruleKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("ruleKey_Matric_2", Key_Matric_2);*/

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalCount++;
						}
						System.out.println("-------------sqlRule Total count===="+totalCount);

					}
				} catch (Exception E) {
					E.getMessage();
				}

				// Default Check
				try {
					modelAndView.addObject("DefaultCheckScore", 0);
					if (listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(idApp, "DQ_DefaultCheck",
								listApplicationsData);

						String DQI = "";
						String status = "";
						String Key_Matric_1 = "";
						String Key_Matric_2 = "";
						String Key_Matric_3 = "";
						while (dashboardDetails.next()) {
							DQI = dashboardDetails.getString(4);
							status = dashboardDetails.getString(5);
							Key_Matric_1 = dashboardDetails.getString(6);
							Key_Matric_2 = dashboardDetails.getString(7);
							Key_Matric_3 = dashboardDetails.getString(8);
						}
						String PercentageDF = "0";
						if (DQI == null) {
							// modelAndView.addObject("showDQIEmpty", 0);
							modelAndView.addObject("DefaultCheckScore", "0.0");
						} else {
							// modelAndView.addObject("showDQIEmpty",null);
							double Percentage = Double.parseDouble(DQI);
							Percentage = Math.floor(Percentage);
							DecimalFormat df1 = new DecimalFormat("#0.0");
							PercentageDF = df1.format(Percentage);
							modelAndView.addObject("DefaultCheckScore", PercentageDF);

						}
						modelAndView.addObject("DefaultCheckStatus", status);
						modelAndView.addObject("DefaultCheckKey_Matric_1", Key_Matric_1);
						modelAndView.addObject("DefaultCheckKey_Matric_2", Key_Matric_2);
						modelAndView.addObject("DefaultCheckKey_Matric_3", Key_Matric_3.replace(",", "<br>"));

						if(status != null){
							totalDQI = totalDQI + Double.valueOf(PercentageDF);
							totalDQI= Math.floor(totalDQI);
							totalCount++;
						}
					}

				} catch (Exception E) {
					E.getMessage();
				}


				modelAndView.addObject("showDQIEmpty", 0);
			} else {
				
				System.out.println("false");
			}
			DecimalFormat df = new DecimalFormat("#0.0");
			totalDQI= Math.floor(totalDQI);
			System.out.println("totalDQI=  " + totalDQI + "/" + totalCount);
			avgDQI = totalDQI / totalCount;
			avgDQI= Math.floor(avgDQI);
		//	modelAndView.addObject("totalDQI", df.format(avgDQI));
			System.out.println("avgDQI=" + avgDQI);
				if (totalDQI <= 0.0) {
				modelAndView.addObject("totalDQI", 0);
			} else if (avgDQI >= 100) {
				modelAndView.addObject("totalDQI", 100);
			}
			//get DQI from DB

			String avgDQIByQuery = iResultsDAO.getAggregateDQIForDataQualityDashboard(idApp);
			modelAndView.addObject("totalDQI", avgDQIByQuery);

			boolean isRuleCatalogDiscovery = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N").equalsIgnoreCase("Y") ? true : false;
			modelAndView.addObject("isRuleCatalogDiscovery", isRuleCatalogDiscovery);

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	/* Avishkar:[17-Apr-2020] review process changes*/
	@RequestMapping(value = "/reviewProcessController")
	public ModelAndView reviewProcessControllerM(HttpServletRequest req, HttpSession session, @RequestParam long idApp ) {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Results", "R", session);
		if (rbac) {
			System.out.println("Redirecting to reviewProcess.jsp ");
			ModelAndView modelAndView = new ModelAndView("reviewProcess");
			modelAndView.addObject("idApp",idApp);
			modelAndView.addObject("currentSection", "Dashboard");
			modelAndView.addObject("currentLink", "View");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/loadActionRecordList", method = RequestMethod.POST, produces = "application/json")
	public void loadReviewRecordList(@RequestParam int LoadContext, @RequestParam long idApp, HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadReviewRecordList 01",String.format("Begin controller processing with get data context/idApp as '%1$s'/'%2$s'", LoadContext, idApp));

			oJsonResponse = getReviewPageData(LoadContext, idApp);

			DateUtility.DebugLog("loadReviewRecordList 02","Got data sending to client");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject getReviewPageData(int nDataContext, long nIdApp) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataListSql, sMaxRowIdFlagSql, sDataViewList, sJsonData;
		int nMaxRowIdDueForApproval = -1;
		String[] aColumnSpec = new String[] {
				"RowId", "IdApp", "RunDate", "RunNo", "ActionType", "ActionStateCode", "ActionStateName", "ActionComments", "ActionDate" , "UserCode", "UserName", "IsRowIdDueForApproval",  "RowId:view", "IsDisplayApproveIcon:lambda"
		};

		JSONObject oJsonRetValue = new JSONObject();
		JSONObject oJsonData = new JSONObject();
		JSONArray aJsonDataList = new JSONArray();
		HashMap<String, ArrayList<HashMap<String,String>>> oDroDownLists = null;
		ObjectMapper oMapper = new ObjectMapper();

    	CustomizeDataTableColumn oIsDisplayApproveIcon = (oDataRow) -> {
			String sApproveAnkerTmpl = "<a class='datatable-anker' id='approve-%1$s'><img style='width: 45px; height: 45px;' src='./assets/img/approval_icon_02.jpg'></a>";
			String sRetValue = oDataRow.get("IsRowIdDueForApproval").equalsIgnoreCase("Y") ? String.format(sApproveAnkerTmpl, oDataRow.get("RowId")) : "";

			return sRetValue;
    	};

    	HashMap<String, CustomizeDataTableColumn> oCustomizeDataTable = new HashMap<String, CustomizeDataTableColumn>() {{
    		put("IsDisplayApproveIcon", oIsDisplayApproveIcon);
    	}};

		DateUtility.DebugLog("loadReviewRecordList 01","Begin controller processing");
		try {
			nMaxRowIdDueForApproval = getMaxRowIdDueForApproval(nIdApp);
			sMaxRowIdFlagSql = String.format("case when a.row_id = %1$s then 'Y' else 'N' end as IsRowIdDueForApproval\n", nMaxRowIdDueForApproval);

			String app_db_name = appDbConnectionProperties.getProperty("db.schema.name").trim();
			/* Get review records record list */
			
			// Query compatibility changes for both POSTGRES and MYSQL
			sDataListSql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataListSql = sDataListSql + "select a.row_id as RowId, a.idApp as IdApp, to_date(a.date::text, 'YYYY-MM-DD') as RunDate, a.run as RunNo, case when a.action_type = 'R' then 'Review' else 'Approve' end as ActionType,\n";
				sDataListSql = sDataListSql + "a.action_state as ActionStateCode, b.element_text as ActionStateName, a.action_comments as ActionComments, to_date(a.action_date::text, 'YYYY-MM-DD') as ActionDate,\n";
				sDataListSql = sDataListSql + "a.action_by as UserCode, c.UserName, \n";
				sDataListSql = sDataListSql + sMaxRowIdFlagSql;
				sDataListSql = sDataListSql + "from data_quality_approval_log a, app_option_list_elements b,\n";
				sDataListSql = sDataListSql + "( select idUser as UserId, concat(firstName, lastName) as UserName, email as EmailId\n";
				sDataListSql = sDataListSql + "from "+app_db_name+".User where active > 0 ) c\n";
				sDataListSql = sDataListSql + "where a.action_state = b.row_id\n";
				sDataListSql = sDataListSql + "and a.action_by = c.UserId\n";
				sDataListSql = sDataListSql + String.format("and a.idApp = %1$s\n",nIdApp);
				sDataListSql = sDataListSql + "order by a.action_date desc;";
			} else {
				sDataListSql = sDataListSql + "select a.row_id as RowId, a.idApp as IdApp, date_format(a.date, '%Y-%m-%d') as RunDate, a.run as RunNo, case when a.action_type = 'R' then 'Review' else 'Approve' end as ActionType,\n";
				sDataListSql = sDataListSql + "a.action_state as ActionStateCode, b.element_text as ActionStateName, a.action_comments as ActionComments, date_format(a.action_date, '%Y-%m-%d %T') as ActionDate,\n";
				sDataListSql = sDataListSql + "a.action_by as UserCode, c.UserName, \n";
				sDataListSql = sDataListSql + sMaxRowIdFlagSql;
				sDataListSql = sDataListSql + "from data_quality_approval_log a, app_option_list_elements b,\n";
				sDataListSql = sDataListSql + "( select idUser as UserId, concat(firstName, lastName) as UserName, email as EmailId\n";
				sDataListSql = sDataListSql + "from "+app_db_name+".User where active > 0 ) c\n";
				sDataListSql = sDataListSql + "where a.action_state = b.row_id\n";
				sDataListSql = sDataListSql + "and a.action_by = c.UserId\n";
				sDataListSql = sDataListSql + String.format("and a.idApp = %1$s\n",nIdApp);
				sDataListSql = sDataListSql + "order by a.action_date desc;";
			}
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate1, sDataListSql, aColumnSpec, "sampleTable", oCustomizeDataTable);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			sJsonData = oMapper.writeValueAsString( getApproveProcessLogicalFlags(nIdApp) );
			oJsonData = new JSONObject(sJsonData);
			oJsonRetValue = oJsonRetValue.put("ApproveProcessLogicalFlags", oJsonData);

			/* Get json of all dropdown needed on UI page for page load only (nDataContext = 0) ignore for (nDataContext = 1) */
			if (nDataContext < 1) {
				oDroDownLists = JwfSpaInfra.getAppOptionsListsMap(jdbcTemplate1, "DQ_REVIEW_STATUS, DQ_APPROVAL_PROCESS_STATUS, DQ_APPROVE_STATUS");
				oJsonData = new JSONObject(oMapper.writeValueAsString(oDroDownLists));
			} else {
				oJsonData = new JSONObject();
			}
			oJsonRetValue = oJsonRetValue.put("PageOptionLists", oJsonData);

			DateUtility.DebugLog("loadReviewRecordList 03","End controller processing");
		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	public HashMap<String, String> getApproveProcessLogicalFlags(long nApplicationId) {
		HashMap<String, String> oRetValue = new HashMap<String, String>() {{}};

		oRetValue.put("MaxRowIdDueForApproval", String.valueOf(getMaxRowIdDueForApproval(nApplicationId)));
		oRetValue.put("IsReviewCommentsAllowed", String.valueOf(getIsReviewCommentsAllowed(nApplicationId)));
		oRetValue.put("ApplicationApprovalProcessStatus", getApplicationApprovalProcessStatus(nApplicationId));

		return oRetValue;
	}

	private String getApplicationApprovalProcessStatus(long nApplicationId) {
		String[] aStatusRefText = { "NOT_STARTED", "REVIEWED", "APPROVED" };
		int nStatusIndex = -1;

		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;
		boolean lRetValue = false;
		HashMap<String, String> oReferenceRecord = new HashMap<String, String>() {{}};
		ObjectMapper oMapper = new ObjectMapper();
		String sRetValue = "";

		/* No review log records for current run means 'not started' */
		sSqlQry = sSqlQry + "select count(*) as Count from data_quality_approval_log a\n";
		sSqlQry = sSqlQry + String.format("where a.idApp = %1$s and a.run = (select run from data_quality_dashboard where IdApp = %2$s);",nApplicationId, nApplicationId);
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
		oSqlRowSet.next();

		DateUtility.DebugLog("getApplicationApprovalProcessStatus 01",String.format("%1$s,%2$s", nApplicationId, oSqlRowSet.getInt("Count")));

		if (oSqlRowSet.getInt("Count") < 1) {
			nStatusIndex = 0;
		} else {
			nStatusIndex = getIsReviewCommentsAllowed(nApplicationId) ? 1 : 2;
		}

		DateUtility.DebugLog("getApplicationApprovalProcessStatus 02",String.format("%1$s,%2$s", nApplicationId, nStatusIndex));

		sSqlQry = "";
		sSqlQry = sSqlQry + "select b.row_id, b.element_reference, b.element_text\n";
		sSqlQry = sSqlQry + "from app_option_list a, app_option_list_elements b\n";
		sSqlQry = sSqlQry + "where b.elements2app_list = a.row_id\n";
		sSqlQry = sSqlQry + "and   b.active > 0\n";
		sSqlQry = sSqlQry + "and   a.list_reference = 'DQ_APPROVAL_PROCESS_STATUS'\n";
		sSqlQry = sSqlQry + String.format("and   b.element_reference = '%1$s' limit 1;", aStatusRefText[nStatusIndex]);
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

		DateUtility.DebugLog("getApplicationApprovalProcessStatus 03",sSqlQry);

		if (oSqlRowSet.next()) {
			oReferenceRecord.put("row_id", oSqlRowSet.getString("row_id"));
			oReferenceRecord.put("element_reference", oSqlRowSet.getString("element_reference"));
			oReferenceRecord.put("element_text", oSqlRowSet.getString("element_text"));

			try {
				sRetValue = oMapper.writeValueAsString(oReferenceRecord);
			} catch (Exception oException) {
				oException.printStackTrace();
			}
		} else {
			sRetValue = "{ `row_id`: `-1`, `element_reference`: `xxx`, `element_text`: `xxx` }".replace('`','\"');
		}

		DateUtility.DebugLog("getApplicationApprovalProcessStatus 04",String.format("%1$s,%2$s",aStatusRefText[nStatusIndex],sRetValue));

		return sRetValue;
	}

	private boolean getIsReviewCommentsAllowed(long nApplicationId) {
		String sSqlQry = "";;
		SqlRowSet oSqlRowSet = null;
		boolean lRetValue = false;

		/* Is current run in approved state then no review commnets allowed else in all rest cases they are allowed */
		sSqlQry = sSqlQry + "select a.row_id\n";
		sSqlQry = sSqlQry + "from data_quality_approval_log a, app_option_list b, app_option_list_elements c\n";
		sSqlQry = sSqlQry + "where c.elements2app_list = b.row_id\n";
		sSqlQry = sSqlQry + "and   a.action_state = c.row_id\n";
		sSqlQry = sSqlQry + "and   b.list_reference = 'DQ_APPROVE_STATUS'\n";
		sSqlQry = sSqlQry + "and   c.element_reference = 'APPROVED'\n";
		sSqlQry = sSqlQry + "and   c.active > 0\n";
		sSqlQry = sSqlQry + String.format("and a.idApp = %1$s and a.run = (select run from data_quality_dashboard where IdApp = %2$s)\n",nApplicationId,nApplicationId);
		sSqlQry = sSqlQry + "limit 1;";
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

		lRetValue = (oSqlRowSet.next()) ? false : true;

		return lRetValue;
	}

	/* Get row id on which approve icon to be rendered in data table last column */
	private int getMaxRowIdDueForApproval(long nApplicationId) {
		int nRetValue = -1;													// row id = -1 means no approve button will be rendered in data table
		String sSqlQry = "";;
		SqlRowSet oSqlRowSet = null;
		boolean lIsCurrentRunApproved = false;

		/* Is current run in approved state then no approve button to be rendered on last column of data table */
		sSqlQry = sSqlQry + "select a.row_id\n";
		sSqlQry = sSqlQry + "from data_quality_approval_log a, app_option_list b, app_option_list_elements c\n";
		sSqlQry = sSqlQry + "where c.elements2app_list = b.row_id\n";
		sSqlQry = sSqlQry + "and   a.action_state = c.row_id\n";
		sSqlQry = sSqlQry + "and   b.list_reference = 'DQ_APPROVE_STATUS'\n";
		sSqlQry = sSqlQry + "and   c.element_reference = 'APPROVED'\n";
		sSqlQry = sSqlQry + "and   c.active > 0\n";
		sSqlQry = sSqlQry + String.format("and a.idApp = %1$s and a.run = (select run from data_quality_dashboard where IdApp = %1$s)\n",nApplicationId,nApplicationId);
		sSqlQry = sSqlQry + "limit 1;";
		oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

		lIsCurrentRunApproved = (oSqlRowSet.next()) ? true : false;

		/* Is current run is not approved state then no approve button to be rendered on last column of data table */
		if (!lIsCurrentRunApproved) {
			sSqlQry = "";
			sSqlQry = sSqlQry + "select a.row_id\n";
			sSqlQry = sSqlQry + "from data_quality_approval_log a, app_option_list b, app_option_list_elements c\n";
			sSqlQry = sSqlQry + "where a.action_state = c.row_id\n";
			sSqlQry = sSqlQry + "and   c.elements2app_list = b.row_id\n";
			sSqlQry = sSqlQry + "and   b.list_reference in ('DQ_REVIEW_STATUS','DQ_APPROVE_STATUS')\n";
			sSqlQry = sSqlQry + "and   c.active > 0\n";
			sSqlQry = sSqlQry + String.format("and a.idApp = %1$s and a.run = (select run from data_quality_dashboard where IdApp = %1$s)\n",nApplicationId,nApplicationId);
			sSqlQry = sSqlQry + "order by a.action_date desc limit 1;";

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			if (oSqlRowSet.next()) { nRetValue = oSqlRowSet.getInt("row_id"); }
		}

		DateUtility.DebugLog("getMaxRowIdDueForApproval 99","nRetValue = " + String.valueOf(nRetValue));
		return nRetValue;
	}

	@RequestMapping(value = "/SaveActionRecord", method = RequestMethod.POST, produces = "application/json")
	public void SaveActionRecord(@RequestParam String ActionRecordToSave, HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oActionRecordToSave = new JSONObject();
		JSONObject oJsonResponse = new JSONObject();

		Long nIdUser = (Long) oSession.getAttribute("idUser");
		String sInsertSql = "";
		String sActionType, sActionComments;
		int nActionStateCode = 0;

		DateUtility.DebugLog("SaveActionRecord 01","Begin save process");

		try {
			oActionRecordToSave = new JSONObject(ActionRecordToSave);
			DateUtility.DebugLog("SaveActionRecord 02",String.format("Read input form values from UI as \n%1$s\n", oActionRecordToSave.toString()));

			sActionType = oActionRecordToSave.getString("ActionType").substring(0, 1);
			sActionComments = oActionRecordToSave.getString("ActionComments");
			nActionStateCode = oActionRecordToSave.getInt("ActionStateCode");

			sInsertSql = sInsertSql + "insert into data_quality_approval_log\n";
			sInsertSql = sInsertSql + "(idApp, date, run, action_type, action_state, action_comments, action_date, action_by, entry_date)\n";
			sInsertSql = sInsertSql + String.format("select IdApp, date, run, '%1$s', %2$s, '%3$s', now(), %4$s, now()\n", sActionType, nActionStateCode, sActionComments, nIdUser);
			sInsertSql = sInsertSql + String.format("from data_quality_dashboard where IdApp = %1$s limit 1;", oActionRecordToSave.getString("IdApp"));

			DateUtility.DebugLog("SaveReviewRecordList 02", sInsertSql);
			jdbcTemplate1.update(sInsertSql);

			oJsonResponse.put("Result", true);
			oJsonResponse.put("Msg", "Action Record Successfully Saved");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();

			DateUtility.DebugLog("SaveReviewRecordList 03",String.format("Pushed response to UI as \n%1$s\n", oJsonResponse.toString()));
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}
	@RequestMapping(value = "/avgTrendChart", method = RequestMethod.POST)
	public void avgTrendChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<List<Object>> listOfListAvgTrend = new ArrayList<List<Object>>();
			List<String> listDGroupVal = new ArrayList<String>();
			System.out.println("DataQualityResultsController : avgTrendChart ");
			String tableName = request.getParameter("tableName");
			String DGroupVal = request.getParameter("DGroupVal");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			if((tableName != null && !tableName.isEmpty()) && (DGroupVal != null && !DGroupVal.isEmpty()) && (colName != null && !colName.isEmpty())) {
				listOfListAvgTrend = distributionCheckResultsService.getAvgTrendChartData(tableName, DGroupVal, colName, idApp);
				JSONArray jsonArrayChartList = new JSONArray(listOfListAvgTrend);
				jsonObject.put("children", jsonArrayChartList);
				System.out.println("DataQualityResultsController : avgTrendChart : jsonObject :: "+jsonObject);
			}else {
				System.err.println("DataQualityResultsController : avgTrendChart : Table name is null or empty ");
				listOfListAvgTrend.add(null);
				jsonObject.put("children", listOfListAvgTrend);
			}
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : avgTrendChart : Exception :: "+e);
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/stdDevavgTrendChart", method = RequestMethod.POST)
	public void stdDevavgTrendChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<List<Object>> listOfListAvgTrend = new ArrayList<List<Object>>();
			System.out.println("DataQualityResultsController : avgTrendChart ");
			String tableName = request.getParameter("tableName");
			String DGroupVal = request.getParameter("DGroupVal");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			if((tableName != null && !tableName.isEmpty()) && (colName != null && !colName.isEmpty())) {
				System.out.println("DataQualityResultsController : avgTrendChart tableName");
				listOfListAvgTrend = distributionCheckResultsService.getStdDevAvgTrendChartData(tableName, DGroupVal, colName, idApp);
				JSONArray jsonArrayChartList = new JSONArray(listOfListAvgTrend);
				jsonObject.put("children", jsonArrayChartList);
				System.out.println("DataQualityResultsController : avgTrendChart : jsonObject :: "+jsonObject);
			}else {
				System.err.println("DataQualityResultsController : avgTrendChart : Table name is null or empty ");
				listOfListAvgTrend.add(null);
				jsonObject.put("children", listOfListAvgTrend);
			}
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : avgTrendChart : Exception :: "+e);
		}

	}

	@RequestMapping(value = "/listOfDGroupValByTableName", method = RequestMethod.POST)
	private void listOfDGroupValByTableName(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		try {
			System.out.println("DataQualityResultsController : listOfDGroupValByTableName ");
			List<String> listDGroupVal = new ArrayList<String>();
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");

			if (tableName != null && !tableName.isEmpty()) {
				StringBuffer strBufQuery = new StringBuffer();

				strBufQuery.append(" SELECT DISTINCT dGroupVal FROM ");
				strBufQuery.append(tableName + " where idApp="+idApp);
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
					strBufQuery.append(" and NumMeanThreshold IS NOT NULL");
					strBufQuery.append(" and Null_Threshold is null ");
				}

				SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

				while (sqlRowSet_strDateSqlQuery.next()) {
					listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
				}
				System.out.println("DataQualityResultsController : listOfDGroupValByTableName : listDGroupVal "
						+ listDGroupVal);
				JSONArray jsonArray = new JSONArray(listDGroupVal);

				List<String> listColName = new ArrayList<String>();
				StringBuffer strBufColNameQuery = new StringBuffer();

				strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
				strBufColNameQuery.append(tableName + " where idApp="+idApp);
				if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
					strBufColNameQuery.append(" and NumMeanThreshold IS NOT NULL");
					strBufColNameQuery.append(" and Null_Threshold is null ");
				}

				SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strBufColNameQuery.toString());

				while (sqlRowSet_strColNameSqlQuery.next()) {
					listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
				}
				System.out.println(
						"DataQualityResultsController : listOfDGroupValByTableName : listColName " + listColName);
				JSONArray jsonArrayColName = new JSONArray(listColName);

				JSONObject jsonObj = new JSONObject();
				jsonObj.put("dGroupVal", jsonArray);
				jsonObj.put("ColName", jsonArrayColName);
				response.getWriter().println(jsonObj);
			}else {
				System.err.println("DataQualityResultsController : listOfDGroupValByTableName : table name is empty ");
			}


		} catch (Exception e) {
			System.err.println("DataQualityResultsController : listOfDGroupValByTableName : Exception :: "+e);
		}

	}
	
	
	@RequestMapping(value = "/listOfDGroupValForColumnByTableName", method = RequestMethod.POST)
	private void listOfDGroupValForColumnByTableName(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		try {
			System.out.println("DataQualityResultsController : listOfDGroupValForColumnByTableName ");
			List<String> listDGroupVal = new ArrayList<String>();
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");
			String columnName = request.getParameter("columnName");

			if (tableName != null && !tableName.isEmpty()) {
				StringBuffer strBufQuery = new StringBuffer();

				strBufQuery.append(" SELECT DISTINCT dGroupVal FROM ");
				strBufQuery.append(tableName + " where idApp=" + idApp + " and ColName='"+columnName+"'");

				System.out.println("DataQualityResultsController : listOfDGroupValForColumnByTableName : sql "
						+ strBufQuery.toString());
				
				SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

				while (sqlRowSet_strDateSqlQuery.next()) {
					listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
				}
				System.out.println("DataQualityResultsController : listOfDGroupValForColumnByTableName : listDGroupVal "
						+ listDGroupVal);
				JSONArray jsonArray = new JSONArray(listDGroupVal);

				response.getWriter().println(jsonArray);
			} else {
				System.err.println(
						"DataQualityResultsController : listOfDGroupValForColumnByTableName : table name is empty ");
			}

		} catch (Exception e) {
			System.err
					.println("DataQualityResultsController : listOfDGroupValForColumnByTableName : Exception :: " + e);
		}

	}
	
	@RequestMapping(value = "/listOfDistributionCheckColumnsByTableName", method = RequestMethod.POST)
	private void listOfDistributionCheckColumnsByTableName(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		try {
			System.out.println("DataQualityResultsController : listOfDistributionCheckColumnsByTableName ");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");

			if (tableName != null && !tableName.isEmpty()) {

				List<String> listColName = new ArrayList<String>();
				StringBuffer strBufColNameQuery = new StringBuffer();

				strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
				strBufColNameQuery.append(tableName + " where idApp=" + idApp);

				SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strBufColNameQuery.toString());

				while (sqlRowSet_strColNameSqlQuery.next()) {
					listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
				}
				System.out.println(
						"DataQualityResultsController : listOfDGroupValByTableName : listColName " + listColName);
				JSONArray jsonArrayColName = new JSONArray(listColName);
				response.getWriter().println(jsonArrayColName);
			} else {
				System.err.println(
						"DataQualityResultsController : listOfDistributionCheckColumnsByTableName : table name is empty ");
			}

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : listOfDistributionCheckColumnsByTableName : Exception :: "+e);
		}

	}

	@RequestMapping(value = "/listOfDGroupValByTableNameForDistribution", method = RequestMethod.POST)
	private void listOfDGroupValByTableNameForDistribution(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		try {
			System.out.println("DataQualityResultsController : listOfDGroupValByTableNameForDistribution ");
			List<String> listDGroupVal = new ArrayList<String>();
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");

			if (tableName != null && !tableName.isEmpty()) {
				StringBuffer strBufQuery = new StringBuffer();

				strBufQuery.append(" SELECT DISTINCT dGroupVal FROM ");
				strBufQuery.append(tableName + " where idApp="+idApp);

				SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

				while (sqlRowSet_strDateSqlQuery.next()) {
					listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
				}
				System.out.println("DataQualityResultsController : listOfDGroupValByTableNameForDistribution : listDGroupVal "
						+ listDGroupVal);
				JSONArray jsonArray = new JSONArray(listDGroupVal);

				List<String> listColName = new ArrayList<String>();
				StringBuffer strBufColNameQuery = new StringBuffer();

				strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
				strBufColNameQuery.append(tableName + " where idApp="+idApp);

				SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strBufColNameQuery.toString());

				while (sqlRowSet_strColNameSqlQuery.next()) {
					listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
				}
				System.out.println(
						"DataQualityResultsController : listOfDGroupValByTableNameForDistribution : listColName " + listColName);
				JSONArray jsonArrayColName = new JSONArray(listColName);

				JSONObject jsonObj = new JSONObject();
				jsonObj.put("dGroupVal", jsonArray);
				jsonObj.put("ColName", jsonArrayColName);
				response.getWriter().println(jsonObj);
			} else {
				System.err
						.println("DataQualityResultsController : listOfDGroupValByTableNameForDistribution : table name is empty ");
			}


		} catch (Exception e) {
			System.err.println("DataQualityResultsController : listOfDGroupValByTableNameForDistribution : Exception :: "+e);
		}

	}

	@RequestMapping(value = "/getListOfColName", method = RequestMethod.POST)
	private void getListOfColName(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		try {
			System.out.println("DataQualityResultsController : getListOfColName ");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");

			if (tableName != null && !tableName.isEmpty()) {
				List<String> listColName = new ArrayList<String>();
				StringBuffer strBufColNameQuery = new StringBuffer();

				strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
				strBufColNameQuery.append(tableName + " where idApp="+idApp);

				SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strBufColNameQuery.toString());

				while (sqlRowSet_strColNameSqlQuery.next()) {
					listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
				}
				System.out.println("DataQualityResultsController : getListOfColName : listColName " + listColName);
				JSONArray jsonArrayColName = new JSONArray(listColName);

				response.getWriter().println(jsonArrayColName);
			} else {
				System.err
						.println("DataQualityResultsController : getListOfColName : table name is empty ");
			}


		} catch (Exception e) {
			System.err.println("DataQualityResultsController : getListOfColName : Exception :: "+e);
		}

	}

	@RequestMapping(value = "/distrubutionCheckTrendChart", method = RequestMethod.POST)
	public void distrubutionCheckTrendChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : distrubutionCheckTrendChart : ...");
			String tableName = request.getParameter("tableName");
			String colName = request.getParameter("colName");
			String strListOfDGroupVal = request.getParameter("dGroupValList");
			String idApp = request.getParameter("idApp");

			List<List<Object>> list = new ArrayList<List<Object>>();

			if ((tableName != null && !tableName.isEmpty()) && (colName != null && !colName.isEmpty()) && (strListOfDGroupVal != null && !strListOfDGroupVal.isEmpty())) {
				String strCommaSeperatedListOfDGroupVal = removeLastCharacterOfString(strListOfDGroupVal);
				List<String> listOfDGroupVal = Arrays.asList(strCommaSeperatedListOfDGroupVal.split("\\s*,\\s*"));
				jsonObject = distributionCheckResultsService.getdistrubutionCheckTrendChartDetails(tableName, colName, listOfDGroupVal,idApp);
				// JSONObject jsonResult = new JSONObject(jsonArray);
				System.out.println(
						"DataQualityResultsController : distrubutionCheckTrendChart : json result :: " + jsonObject);
				response.getWriter().println(jsonObject);
			} else {
				System.err
						.println("DataQualityResultsController : distrubutionCheckTrendChart : table name is empty ");
			}

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : distrubutionCheckTrendChart : Exception :: "+e);
		}

	}

	private String removeLastCharacterOfString(String str) {
	    if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
	        str = str.substring(0, str.length() - 1);
	    }
	    return str;
	}

	

	@RequestMapping(value = "/distrubutionCheckTrendDGroupValChart", method = RequestMethod.POST)
	public void distrubutionCheckTrendDGroupValChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : distrubutionCheckTrendDGroupValChart : ...");
			String tableName = request.getParameter("tableName");
			String strdgroupval = request.getParameter("dgroupval");
			String idApp = request.getParameter("idApp");

			if ((tableName != null && !tableName.isEmpty()) && (strdgroupval != null && !strdgroupval.isEmpty())) {
				List<List<Object>> list = new ArrayList<List<Object>>();
				jsonObject = getdistrubutionDGroupValCheckTrendChartDetails(tableName, strdgroupval, idApp);
				// JSONObject jsonResult = new JSONObject(jsonArray);
				System.out
						.println("DataQualityResultsController : distrubutionCheckTrendDGroupValChart : json result :: "
								+ jsonObject);
				response.getWriter().println(jsonObject);
			} else {
				System.err
						.println("DataQualityResultsController : distrubutionCheckTrendDGroupValChart : table name is empty ");
			}


		} catch (Exception e) {
			System.err
			.println("DataQualityResultsController : distrubutionCheckTrendDGroupValChart : Exception :: "+e);
			e.printStackTrace();
		}

	}

	public JSONObject getdistrubutionDGroupValCheckTrendChartDetails(String tableName, String paramStrDGroupVal, String idApp) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : getdistrubutionDGroupValCheckTrendChartDetails : tableName :: " + tableName);
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();

			String strDateSqlQuery = "SELECT date FROM " + tableName + " where idApp="+idApp+" and dGroupVal = '"+paramStrDGroupVal+"'  GROUP BY date ORDER BY date";
			System.out.println("DataQualityResultsController : getdistrubutionCheckTrendChartDetails : strDateSqlQuery :: " + strDateSqlQuery);
			SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strDateSqlQuery);
			List<Date> listTableDates = new ArrayList<Date>();
			while (sqlRowSet_strDateSqlQuery.next()) {
				listTableDates.add(sqlRowSet_strDateSqlQuery.getDate("date"));
			}

			List<String> listTable_dGroupVal = new ArrayList<String>();
			listTable_dGroupVal.add(paramStrDGroupVal);
			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);

			for (Date localdate : listTableDates) {
				List<Object> listOfChartElements = new ArrayList<Object>();
				listOfChartElements.add(localdate);
				for (String localstring : listTable_dGroupVal) {

					String strSqlQueryGetChartList = "SELECT Mean FROM " + tableName + " WHERE idApp="+idApp+" and date = '"
							+ localdate + "' AND dGroupVal = '" + localstring
							+ "' AND RUN IN (SELECT MAX(RUN) AS RUN FROM " + tableName + " WHERE idApp="+idApp+" and DATE = '" + localdate
							+ "' AND dGroupVal = '" + localstring + "')";
					SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(strSqlQueryGetChartList);
					while (sqlRowSet_strSqlQueryGetChartList.next()) {
						listOfChartElements.add(sqlRowSet_strSqlQueryGetChartList.getDouble("Mean"));
					}
				}
				listRowChartData.add(listOfChartElements);
			}
			JSONArray jsonArrayChartList = new JSONArray(listRowChartData);

			jsonObject.put("header", jsonArrayDGroupVal);
			jsonObject.put("chart", jsonArrayChartList);

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : getdistrubutionCheckTrendChartDetails : Exception :: " + e);
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**Last Updated : 15thMay,2020
	 * Code By : Anant S. Mahale
	 * @param request : table name (tableName) from dashboardNumericStatView.jsp
	 * @param response : JSON Object to bind data in Rollup analysis dropdownds ( RollUp Variable & Column Name)
	 * @param session
	 */
	@RequestMapping(value = "/rollupAnalysisDropdownDataLoad", method = RequestMethod.POST)
	public void rollupAnalysisDropdownDataLoad(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : rollupAnalysisDropdownDataLoad : ...");
			String tableName = request.getParameter("tableName");
			String idApp = request.getParameter("idApp");

			if (tableName != null && !tableName.isEmpty()) {
				jsonObject = distributionCheckResultsService.rollupAnalysisDropdownDataLoadJsonObj(tableName, idApp);
				response.getWriter().println(jsonObject);
			} else {
				System.err.println("DataQualityResultsController : rollupAnalysisDropdownDataLoad : table name is empty ");
			}
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : rollupAnalysisDropdownDataLoad : Exception :: "+e);
			e.printStackTrace();
		}

	}

	/**Last Updated : 28thApril,2020
	 * Code By: Anant S. Mahale
	 * @param request : tableName ( table name), rollupVariable, colName (ColumnName) from dashboardNumericStatView.jsp
	 * @param response : JSON Object for datatable in RollUp Analysis
	 * @param session
	 */
	@RequestMapping(value = "/rollupdatatable")
	public void rollupdatatable(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject result = new JSONObject();
		try {
			JSONArray JsonTableArray = null;
			int intCounter = 0;
			JSONArray dataArray = new JSONArray();
			PrintWriter out = response.getWriter();
			String tableName = request.getParameter("tableName");
			String rollupVariable = request.getParameter("rollupVariable");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			List<List<Object>> dataTable = new ArrayList<List<Object>>();
			int recordCounter = 0;
			if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
					&& (colName != null && !colName.isEmpty())) {

				StringBuffer strLocalrollupVariable = new StringBuffer();
				int intIndexOfRollUpVariable = distributionCheckResultsService.getLocationOfDGroupCal(rollupVariable, tableName, idApp);

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
					}
				} else {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
								+ intIndexOfRollUpVariable + "), '?::?', -1)");
					}
				}
				String condition = "";
				if(tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")){
					condition = " AND dqcs.Null_Threshold is NULL ";
				}

				String sql = "";

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

					sql = "SELECT dqcs.Date as date, dqcs.Run  as run, dqcs.ColName as colName, "
							+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat)::numeric,2) end))) THEN 'Failed' ELSE 'Passed' END AS status , "
							+ "SUM(dqcs.Record_Count) AS recordCount, ROUND(MIN(dqcs.Min)::numeric,2) AS min, ROUND(MAX(dqcs.Max)::numeric,2) AS max,  "
							+ "ROUND(AVG(dqcs.NumMeanThreshold)::numeric,2) AS numMeanThreshold, ROUND(SUM(dqcs.sumOfNumStat)::numeric,2) AS sumOfNumStat , ROUND(AVG(sumOfNumStat)::Numeric,2) AS  histAvg,"
							+ "CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat)::numeric,2) end AS histStdDev,"
							+ strLocalrollupVariable
							+ " AS rollUpColumn, ROUND(AVG(dqcs.Mean::numeric),2) AS mean , ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as upperLimit , "
							+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as lowerLimit "
							+ "FROM " + tableName + " AS dqcs WHERE dqcs.idApp=" + idApp + " and ColName = '" + colName
							+ "' "+condition+" group by " + strLocalrollupVariable + ",ColName,dqcs.Date,dqcs.Run ORDER BY "
							+ strLocalrollupVariable + ", ColName,dqcs.Date, dqcs.Run;";

				} else {

					sql = "SELECT dqcs.Date as date, dqcs.Run as run, dqcs.ColName as colName, "
							+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end))) THEN 'Failed' ELSE 'Passed' END AS status , "
							+ "SUM(dqcs.Record_Count) AS recordCount,ROUND(MIN(dqcs.Min),2) AS min, ROUND(MAX(dqcs.Max),2) AS max, "
							+ "ROUND(AVG(dqcs.NumMeanThreshold),2) AS numMeanThreshold, ROUND(SUM(dqcs.sumOfNumStat),2) AS sumOfNumStat ,  ROUND(AVG(sumOfNumStat)) AS  histAvg,"
							+ "CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end AS histStdDev, "
							+ strLocalrollupVariable
							+ " AS rollUpColumn, ROUND(AVG(dqcs.Mean),2) AS mean , ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as upperLimit , "
							+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as lowerLimit "
							+ "FROM " + tableName + " AS dqcs WHERE dqcs.idApp=" + idApp + " and ColName = '" + colName
							+ "' "+condition+" group by " + strLocalrollupVariable + ",ColName,dqcs.Date,dqcs.Run ORDER BY "
							+ strLocalrollupVariable + ", ColName,dqcs.Date, dqcs.Run;";
				}
				System.out.println("DataQualityResultsController : rollupdatatable : strBufQuery :: " + sql);

				SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(sql);
				String[] columnNames = { "date", "run", "colName", "status", "recordCount", "min", "max",
						"numMeanThreshold", "sumOfNumStat", "histAvg", "histStdDev", "rollUpColumn", "mean",
						"upperLimit", "lowerLimit" };

				DecimalFormat numberFormat = new DecimalFormat("#0.00");

				while (sqlRowSet_strSqlQuery.next()) {
					int colIndex = 1;
					JSONObject rowArray = new JSONObject();

					// Read Row Data
					for (String columnName : columnNames) {
						String columnDataType = sqlRowSet_strSqlQuery.getMetaData().getColumnTypeName(colIndex);

						// Read column value
						String columnValue = "";
						if (columnDataType.equalsIgnoreCase("double") || columnDataType.equalsIgnoreCase("decimal")
								|| columnDataType.equalsIgnoreCase("float")) {

							columnValue = numberFormat.format(sqlRowSet_strSqlQuery.getDouble(columnName));
						} else {
							columnValue = sqlRowSet_strSqlQuery.getString(columnName);
						}

						if (columnValue == null)
							columnValue = "";

						rowArray.put(columnName, columnValue);
						++colIndex;
					}
					dataArray.put(rowArray);
					++recordCounter;
				}
				JsonTableArray = new JSONArray(dataTable);

			} else {
				System.err.println("DataQualityResultsController : rollupdatatable : table name is empty ");
			}
			result.put("iTotalRecords", recordCounter);
			result.put("iTotalDisplayRecords", recordCounter);
			result.put("aaData", dataArray);
			System.out.println(result);
			out.print(result);

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : rollupdatatable : Exception :: " + e);
			e.printStackTrace();
		}

	}



	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : tableName ( table name), rollupVariable, colName (ColumnName) from dashboardNumericStatView.jsp
	 * @param response : JSON Object, JSON Object having JSON Array to bind data to dropdown. The dropdown is placed in TrendBreakChart
	 * @param session
	 */
	@RequestMapping(value = "/rollupAnalysisTrendBreakChartDropdownDataLoad", method = RequestMethod.POST)
	public void rollupAnalysisTrendBreakChartDropdownDataLoad(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		try {
			JSONArray JsonTableArray = null;
			PrintWriter out = response.getWriter();
			System.out.println("DataQualityResultsController : rollupAnalysisTrendBreakChartDropdownDataLoad : ...");
			String tableName = request.getParameter("tableName");
			String rollupVariable = request.getParameter("rollupVariable");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
					&& (colName != null && !colName.isEmpty())) {
				StringBuffer strLocalrollupVariable = new StringBuffer();

				int intIndexOfRollUpVariable = distributionCheckResultsService.getLocationOfDGroupCal(rollupVariable, tableName, idApp);

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
					}
				} else {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
								+ intIndexOfRollUpVariable + "), '?::?', -1)");
					}
				}

				// Query compatibility changes for both POSTGRES and MYSQL
				String sql = "SELECT   distinct " + strLocalrollupVariable + " AS " + rollupVariable + " FROM " + tableName
						+ " WHERE idApp=" + idApp + " and ColName = '" + colName + "'  order by "
						+ strLocalrollupVariable;

				System.out.println(
						"DataQualityResultsController : rollupAnalysisTrendBreakChartDropdownDataLoad : strBufQuery :: "
								+ sql);
				SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(sql);
				List<Object> dataTableRow = new ArrayList<Object>();
				while (sqlRowSet_strSqlQuery.next()) {
					dataTableRow.add(sqlRowSet_strSqlQuery.getString(rollupVariable));
				}
				JsonTableArray = new JSONArray(dataTableRow);
			} else {
				System.err.println(
						"DataQualityResultsController : rollupAnalysisTrendBreakChartDropdownDataLoad : table name is empty ");
			}
			out.print(JsonTableArray);
		} catch (Exception e) {
			System.err.println(
					"DataQualityResultsController : rollupAnalysisTrendBreakChartDropdownDataLoad : Exception :: " + e);
			e.printStackTrace();
		}

	}

	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : tableName ( table name), rollupVariable, colName (ColumnName), element (its like data under RollUp Variable column)  from dashboardNumericStatView.jsp
	 * @param response : JSON Object, JSON Object contains List of List having header and chart data
	 * @param session
	 */
	@RequestMapping(value = "/rollupAnalysisTrendBreakChartData", method = RequestMethod.POST)
	public void rollupAnalysisTrendBreakChartData(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		JSONObject result = new JSONObject();
		try {
			JSONArray JsonTableArray = null;
			PrintWriter out = response.getWriter();
			System.out.println("DataQualityResultsController : rollupAnalysisTrendBreakChartData : ...");
			String tableName = request.getParameter("tableName");
			String rollupVariable = request.getParameter("rollupVariable");
			String colName = request.getParameter("colName");
			String element = request.getParameter("element");
			String idApp = request.getParameter("idApp");

			if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
					&& (colName != null && !colName.isEmpty()) && (element != null && !element.isEmpty())) {

				StringBuffer strLocalrollupVariable = new StringBuffer();
				int intIndexOfRollUpVariable = distributionCheckResultsService.getLocationOfDGroupCal(rollupVariable, tableName, idApp);

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
					}

				} else {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
								+ intIndexOfRollUpVariable + "), '?::?', -1)");
					}
				}

				List<Integer> listRun = new ArrayList<Integer>();
				List<List<Object>> listOfListTrendBreakChart = new ArrayList<List<Object>>();

				List<Date> listDate = new ArrayList<Date>();
				listDate = distributionCheckResultsService.getListOfDateFromColumnSummarywithTableName(tableName, idApp);

				listRun = distributionCheckResultsService.getListOfRunFromColumnSummaryWithTableName(tableName, idApp);

				for (Date date : listDate) {

					for (Integer integer : listRun) {
						if (distributionCheckResultsService.checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {

							String sql = "";
							// Query compatibility changes for both POSTGRES and MYSQL
							if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
								sql = "SELECT dqcs.Date, dqcs.Run AS Run,dqcs.ColName, SUM(dqcs.Record_Count) AS Record_Count,  SUM(dqcs.Count) AS Count,"
										+ " ROUND(MIN(dqcs.Min)::numeric,2) AS Min, ROUND(MAX(dqcs.Max)::numeric,2) AS MAX,  ROUND(AVG(dqcs.NumMeanThreshold)::numeric,2) AS NumMeanThreshold,  "
										+ " ROUND(AVG(dqcs.NumSDThreshold)::numeric,2) AS NumSDThreshold,   ROUND(SUM(dqcs.sumOfNumStat)::numeric,2) AS sumOfNumStat ,  "
										+ strLocalrollupVariable + " AS " + rollupVariable + ", "
										+ " ROUND(AVG(dqcs.Mean)::numeric,2) AS Mean, ROUND(STDDEV(sumOfNumStat)::numeric,2) AS STD_SUM_DEV, ROUND(AVG(sumOfNumStat)::numeric,2) AS  avg_sum1,"
										+ "ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as upper_limit , "
										+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as lower_limit, "
										+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat)::Numeric,2) end))) THEN 'Failed' ELSE 'Passed' END AS STATUS "
										+ "FROM " + tableName + " AS dqcs WHERE idApp=" + idApp + " and ColName = '"
										+ colName + "' AND " + strLocalrollupVariable + " = '" + element
										+ "' AND  Date='" + date + "'::date AND dqcs.run = " + integer + " group by "
										+ strLocalrollupVariable + ",ColName,DATE,Run ORDER BY "
										+ strLocalrollupVariable + ",ColName,DATE,Run asc LIMIT 1";

							} else {

								sql = "SELECT dqcs.Date, dqcs.Run AS Run,dqcs.ColName, SUM(dqcs.Record_Count) AS Record_Count,  SUM(dqcs.Count) AS Count,"
										+ " ROUND(MIN(dqcs.Min),2) AS Min, ROUND(MAX(dqcs.Max),2) AS MAX,  ROUND(AVG(dqcs.NumMeanThreshold),2) AS NumMeanThreshold,  "
										+ " ROUND(AVG(dqcs.NumSDThreshold),2) AS NumSDThreshold,   ROUND(SUM(dqcs.sumOfNumStat),2) AS sumOfNumStat ,  "
										+ strLocalrollupVariable + " AS " + rollupVariable + ", "
										+ " ROUND(AVG(dqcs.Mean),2) AS Mean, ROUND(STDDEV(sumOfNumStat),2) AS STD_SUM_DEV, ROUND(AVG(sumOfNumStat),2) AS  avg_sum1,"
										+ "ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as upper_limit , "
										+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as lower_limit, "
										+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end))) THEN 'Failed' ELSE 'Passed' END AS STATUS "
										+ "FROM " + tableName + " AS dqcs WHERE idApp=" + idApp + " and ColName = '"
										+ colName + "' AND " + strLocalrollupVariable + " = '" + element
										+ "' AND  Date='" + date + "' AND dqcs.run = " + integer + " group by "
										+ strLocalrollupVariable + ",ColName,DATE,Run ORDER BY "
										+ strLocalrollupVariable + ",ColName,DATE,Run asc LIMIT 1";
							}
							System.out.println(
									"DataQualityResultsController : rollupAnalysisTrendBreakChartData : strBufQuery :: "
											+ sql);
							SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(sql);
							while (sqlRowSet_strSqlQuery.next()) {
								List<Object> dataTableRow = new ArrayList<Object>();
								java.sql.Date dbSqlDate = sqlRowSet_strSqlQuery.getDate("Date");
								if (integer < 10) {
									dataTableRow.add(dbSqlDate + "(0" + integer + ")");
								} else {
									dataTableRow.add(dbSqlDate + "(" + integer + ")");
								}
								dataTableRow.add(sqlRowSet_strSqlQuery.getDouble("UPPER_LIMIT"));
								dataTableRow.add(sqlRowSet_strSqlQuery.getDouble("sumOfNumStat"));
								dataTableRow.add(sqlRowSet_strSqlQuery.getDouble("LOWER_LIMIT"));
								listOfListTrendBreakChart.add(dataTableRow);
							}
						}
					}
				}

				JsonTableArray = new JSONArray(listOfListTrendBreakChart);
				result.put("children", JsonTableArray);
			} else {
				System.err.println(
						"DataQualityResultsController : rollupAnalysisTrendBreakChartData : table name or other param is empty ");
			}
			out.print(result);

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : rollupAnalysisTrendBreakChartData : Exception :: " + e);
			e.printStackTrace();
		}

	}


	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : tableName ( table name), rollupVariable, colName (ColumnName)  from dashboardNumericStatView.jsp
	 * @param response : JSON Object having two JSON arrays 1) Headear and 2) chartdata
	 * @param session
	 */
	@RequestMapping(value = "/callRollUpComparisonChart", method = RequestMethod.POST)
	public void callRollUpComparisonChart(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		JSONObject result = new JSONObject();
		try {
			List<List<Object>> listRowChartData = new ArrayList<List<Object>>();
			PrintWriter out = response.getWriter();
			System.out.println("DataQualityResultsController : callRollUpComparisonChart : ...");
			String tableName = request.getParameter("tableName");
			String rollupVariable = request.getParameter("rollupVariable");
			String colName = request.getParameter("colName");
			String strrolluplist = request.getParameter("rolluplist"); // add here list of dgroup vals
			String idApp = request.getParameter("idApp");

			if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
					&& (colName != null && !colName.isEmpty())) {
				String strCommaSeperatedListOfRollUpVar = removeLastCharacterOfString(strrolluplist);
				List<String> listRollUpVariable = Arrays.asList(strCommaSeperatedListOfRollUpVar.split("\\s*,\\s*"));
				int intIndexOfRollUpVariable = distributionCheckResultsService.getLocationOfDGroupCal(rollupVariable, tableName, idApp);

				StringBuffer strLocalrollupVariable = new StringBuffer();

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
					}

				} else {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
								+ intIndexOfRollUpVariable + "), '?::?', -1)");
					}
				}
				List<Integer> listRun = new ArrayList<Integer>();
				listRun = distributionCheckResultsService.getListOfRunFromColumnSummaryWithTableName(tableName, idApp);
				List<Date> listDate = new ArrayList<Date>();
				listDate = distributionCheckResultsService.getListOfDateFromColumnSummarywithTableName(tableName, idApp);

				JSONArray jsonArrayRollUpVariable = new JSONArray(listRollUpVariable);

				for (Date localdate : listDate) {

					for (Integer integer : listRun) {
						if (distributionCheckResultsService.checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, localdate, integer, idApp)) {
							List<Object> listOfChartElements = new ArrayList<Object>();

							if (integer < 10) {
								listOfChartElements.add(localdate + "(0" + integer + ")");
							} else {
								listOfChartElements.add(localdate + "(" + integer + ")");
							}

							for (String localstring : listRollUpVariable) {
								StringBuffer whereClause = new StringBuffer();
								whereClause.append(strLocalrollupVariable + " = '");
								whereClause.append(localstring + "'");
								if (distributionCheckResultsService.checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndRollUpElement(tableName,
										localdate, integer, whereClause.toString())) {

									// Query compatibility changes for both POSTGRES and MYSQL
									String sql = "";
									if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

										sql = "SELECT dqcs.Date, dqcs.Run AS Run,dqcs.ColName, SUM(dqcs.Record_Count) AS Record_Count,  SUM(dqcs.Count) AS Count,"
												+ " ROUND(MIN(dqcs.Min)::numeric,2) AS Min, ROUND(MAX(dqcs.Max)::numeric,2) AS MAX,  ROUND(AVG(dqcs.NumMeanThreshold)::numeric,2) AS NumMeanThreshold,  "
												+ " ROUND(AVG(dqcs.NumSDThreshold)::numeric,2) AS NumSDThreshold,   ROUND(SUM(dqcs.sumOfNumStat)::numeric,2) AS sumOfNumStat ,  "
												+ strLocalrollupVariable + " AS " + rollupVariable + ", "
												+ " ROUND(AVG(dqcs.Mean)::numeric,2) AS Mean, ROUND(STDDEV(sumOfNumStat)::numeric,2) AS STD_SUM_DEV, ROUND(AVG(sumOfNumStat)::numeric,2) AS  avg_sum1,"
												+ "ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as upper_limit , "
												+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END)))::numeric,2) as lower_limit, "
												+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat)::Numeric,2) end))) THEN 'Failed' ELSE 'Passed' END AS STATUS "
												+ "FROM " + tableName + " AS dqcs WHERE idApp=" + idApp
												+ " and ColName = '" + colName + "' AND " + strLocalrollupVariable
												+ " = '" + localstring + "' AND  Date='" + localdate
												+ "'::date AND dqcs.run = " + integer + " group by " + strLocalrollupVariable
												+ ",ColName,DATE,Run ORDER BY " + strLocalrollupVariable
												+ ",ColName,DATE,Run asc LIMIT 1";

									} else {

										sql = "SELECT dqcs.Date, dqcs.Run AS Run,dqcs.ColName, SUM(dqcs.Record_Count) AS Record_Count,  SUM(dqcs.Count) AS Count,"
												+ " ROUND(MIN(dqcs.Min),2) AS Min, ROUND(MAX(dqcs.Max),2) AS MAX,  ROUND(AVG(dqcs.NumMeanThreshold),2) AS NumMeanThreshold,  "
												+ " ROUND(AVG(dqcs.NumSDThreshold),2) AS NumSDThreshold,   ROUND(SUM(dqcs.sumOfNumStat),2) AS sumOfNumStat ,  "
												+ strLocalrollupVariable + " AS " + rollupVariable + ", "
												+ " ROUND(AVG(dqcs.Mean),2) AS Mean, ROUND(STDDEV(sumOfNumStat),2) AS STD_SUM_DEV, ROUND(AVG(sumOfNumStat),2) AS  avg_sum1,"
												+ "ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) = NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as upper_limit , "
												+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) = NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as lower_limit, "
												+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) = NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end))) THEN 'Failed' ELSE 'Passed' END AS STATUS "
												+ "FROM " + tableName + " AS dqcs WHERE idApp=" + idApp
												+ " and ColName = '" + colName + "' AND " + strLocalrollupVariable
												+ " = '" + localstring + "' AND  Date='" + localdate
												+ "' AND dqcs.run = " + integer + " group by " + strLocalrollupVariable
												+ ",ColName,DATE,Run ORDER BY " + strLocalrollupVariable
												+ ",ColName,DATE,Run asc LIMIT 1";
									}
									System.out.println("sql: " + sql);
									SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(sql);
									while (sqlRowSet_strSqlQueryGetChartList.next()) {
										listOfChartElements
												.add(sqlRowSet_strSqlQueryGetChartList.getDouble("sumOfNumStat"));
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

				result.put("header", jsonArrayRollUpVariable);
				result.put("chart", jsonArrayChartList);

			} else {
				System.err.println("DataQualityResultsController : callRollUpComparisonChart : table name is empty ");
			}
			out.print(result);

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : callRollUpComparisonChart : Exception :: " + e);
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/listOfRollUpVariableByTableName", method = RequestMethod.POST)
	public void listOfRollUpVariableByTableName(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject result = new JSONObject();
		try {
			PrintWriter out =  response.getWriter();
			System.out.println("DataQualityResultsController : listOfRollUpVariableByTableName : ...");
			String tableName = request.getParameter("tableName");
			String rollupVariable = request.getParameter("rollupVariable");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty()) && (colName != null && !colName.isEmpty())) {

				int intIndexOfRollUpVariable = distributionCheckResultsService.getLocationOfDGroupCal(rollupVariable, tableName, idApp);

				StringBuffer strLocalrollupVariable = new StringBuffer();

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
					}

				} else {
					if (intIndexOfRollUpVariable == 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
					} else if (intIndexOfRollUpVariable > 1) {
						strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
								+ intIndexOfRollUpVariable + "), '?::?', -1)");
					}
				}

				StringBuffer strBufQuery = new StringBuffer("SELECT ");
				strBufQuery.append(" DISTINCT "+strLocalrollupVariable+" AS "+rollupVariable);
				strBufQuery.append(" FROM ");
				strBufQuery.append(tableName);
				strBufQuery.append(" AS dqcs WHERE idApp="+idApp+" and ColName = '");
				strBufQuery.append(colName);
				strBufQuery.append("'  group by "+strLocalrollupVariable+",ColName ORDER BY "+strLocalrollupVariable);
				System.out.println("DataQualityResultsController : callRollUpComparisonChart : strBufQuery :: "+strBufQuery.toString());
				SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());
				List<String> listRollUpVariable = new ArrayList<String>();
				while (sqlRowSet_strSqlQuery.next()) {
					listRollUpVariable.add(sqlRowSet_strSqlQuery.getString(rollupVariable));
				}

				JSONArray jsonArrayRollUpVariable = new JSONArray(listRollUpVariable);

				result.put("rollupVarlist", jsonArrayRollUpVariable);

			} else {
				System.err.println("DataQualityResultsController : listOfRollUpVariableByTableName : table name is empty ");
			}
			out.print(result);

		} catch (Exception e) {
			System.err.println("DataQualityResultsController : listOfRollUpVariableByTableName : Exception :: "+e);
			e.printStackTrace();
		}

	}

	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : tableName to get dgroup values
	 * @param response : JSON Object of dgroup values
	 * @param session
	 */
	@RequestMapping(value = "/sumOfNumStats", method = RequestMethod.POST)
	public void sumOfNumStats(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<List<Object>> listOfListAvgTrend = new ArrayList<List<Object>>();
			System.out.println("DataQualityResultsController : dataDrift ");
			String tableName = request.getParameter("tableName");
			if((tableName != null && !tableName.isEmpty())) {
				jsonObject = distributionCheckResultsService.getsumOfNumStatsObj(tableName);
			}else {
				System.err.println("DataQualityResultsController : sumOfNumStats : Table name is null or empty ");
				listOfListAvgTrend.add(null);
				jsonObject.put("colName", listOfListAvgTrend);
			}
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : sumOfNumStats : Exception :: "+e);
		}

	}


	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : table name, dgroup val
	 * @param response : JSON Object for sum of num stats chart
	 * @param session
	 */
	@RequestMapping(value = "/sumOfNumStatsChart", method = RequestMethod.POST)
	public void sumOfNumStatsChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : sumOfNumStatsChart ");
			String tableName = request.getParameter("tableName");
			String dGroupVal = request.getParameter("dGroupVal");
			String colName = request.getParameter("colName");
			String run = request.getParameter("run");
			String idApp = request.getParameter("idApp");

			if((tableName != null && !tableName.isEmpty()) && (dGroupVal != null && !dGroupVal.isEmpty())  && (colName != null && !colName.isEmpty()) && (run != null && !run.isEmpty())) {
				jsonObject = distributionCheckResultsService.getsumOfNumStatsChartData(tableName, dGroupVal, colName, Integer.parseInt(run),idApp);
			}else {
				System.err.println("DataQualityResultsController : sumOfNumStatsChart : Table name is null or empty ");
			}
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : sumOfNumStatsChart : Exception :: "+e);
		}

	}

	/**Last Updated : 15thMay,2020
	 * Code By: Anant S. Mahale
	 * @param request : table name, column Name
	 * @param response : call for data drift  (Drift and Orphan Trend Chart)
	 * @param session
	 */
	@RequestMapping(value = "/datadriftChart", method = RequestMethod.POST)
	public void datadriftChart(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("DataQualityResultsController : datadriftChart ");
			String tableName = request.getParameter("tableName");
			String colName = request.getParameter("colName");
			String idApp = request.getParameter("idApp");

			if((tableName != null && !tableName.isEmpty())  && (colName != null && !colName.isEmpty())) {
				System.out.println("DataQualityResultsController : datadriftChart tableName");
				jsonObject = distributionCheckResultsService.getdatadriftChartData(tableName, colName, idApp);
			}else {
				System.err.println("DataQualityResultsController : datadriftChart : Table name OR Column Name is null or empty ");
			}
			response.getWriter().println(jsonObject);
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : datadriftChart : Exception :: "+e);
		}

	}

}
