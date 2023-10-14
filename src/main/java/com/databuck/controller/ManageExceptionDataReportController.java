package com.databuck.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.JwfSpaInfra.CustomizeDataTableColumn;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ManageExceptionDataReportController {

	@Autowired
	private RBACController rbacController;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private Properties appDbConnectionProperties;

	@RequestMapping(value = "/ManageExceptionDataReport")
	public ModelAndView ManageExceptionDataReportView(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("Validation Check", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("ManageExceptionDataReport");

			oModelAndView.addObject("currentSection", "Validation Check");
			oModelAndView.addObject("currentLink", "Exception Data Report");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/loadExceptionReportViewList", method = RequestMethod.POST, produces = "application/json")
	public void loadExceptionReportViewList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			oJsonResponse = loadExceptionReportDataList(oSession);

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject loadExceptionReportDataList(HttpSession oSession) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] {
				"ReportRowId", "ReportId", "ReportName", "ReportDescription", "DataPeriodWord", "DataPeriodInt", "ProjectId", "AppCount", "AppIds", "AppNames", "ReportRowId:edit", "ReportRowId:delete"
		};

		String sProjectId = oSession.getAttribute("projectId").toString();
		String sNewExceptionReport = "{ 'ProjectId': 'SessionProjectId', 'ReportName':'','DataPeriodWord':'Daily', 'ReportId':'-1','ReportDescription':'','DataPeriodInt':'1','AppIds':'','ReportRowId':'-1' }";

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		try {
			/* Put main data entity list onto return JSON */
			// Query compatibility changes for both POSTGRES and MYSQL
			sDataSql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = sDataSql + "select core_qry.*, sub_qry.* \n";
				sDataSql = sDataSql + "from \n";
				sDataSql = sDataSql + "( \n";
				sDataSql = sDataSql + "select row_id as ReportRowId,  report_id as ReportId, name as ReportName, description as ReportDescription, \n";
				sDataSql = sDataSql + "case when data_frequency = 1 then 'Daily' when data_frequency = 2 then 'Weekly' when data_frequency = 3 then 'Monthly' else 'Error' end as DataPeriodWord, \n";
				sDataSql = sDataSql + "data_frequency as DataPeriodInt, project_id as ProjectId \n";
				sDataSql = sDataSql + "from exception_data_report \n";
				sDataSql = sDataSql + ") core_qry \n";
				sDataSql = sDataSql + "left outer join ( \n";
				sDataSql = sDataSql + "select ReportAppList.report_row_id as ReportRowId, count(*) as AppCount, string_agg(AppMaster.idApp::text,',') as AppIds, string_agg(AppMaster.name,',') as AppNames \n";
				sDataSql = sDataSql + "from exception_data_report_apps ReportAppList, listApplications AppMaster \n";
				sDataSql = sDataSql + "where ReportAppList.app_row_id = AppMaster.idApp \n";
				sDataSql = sDataSql + "group by ReportAppList.report_row_id \n";
				sDataSql = sDataSql + ") as sub_qry on core_qry.ReportRowId = sub_qry.ReportRowId;";
			} else {
				sDataSql = sDataSql + "select core_qry.*, sub_qry.* \n";
				sDataSql = sDataSql + "from \n";
				sDataSql = sDataSql + "( \n";
				sDataSql = sDataSql + "select row_id as ReportRowId,  report_id as ReportId, name as ReportName, description as ReportDescription, \n";
				sDataSql = sDataSql + "case when data_frequency = 1 then 'Daily' when data_frequency = 2 then 'Weekly' when data_frequency = 3 then 'Monthly' else 'Error' end as DataPeriodWord, \n";
				sDataSql = sDataSql + "data_frequency as DataPeriodInt, project_id as ProjectId \n";
				sDataSql = sDataSql + "from exception_data_report \n";
				sDataSql = sDataSql + ") core_qry \n";
				sDataSql = sDataSql + "left outer join ( \n";
				sDataSql = sDataSql + "select ReportAppList.report_row_id as ReportRowId, count(*) as AppCount, group_concat(AppMaster.idApp) as AppIds, group_concat(AppMaster.name) as AppNames \n";
				sDataSql = sDataSql + "from exception_data_report_apps ReportAppList, listApplications AppMaster \n";
				sDataSql = sDataSql + "where ReportAppList.app_row_id = AppMaster.idApp \n";
				sDataSql = sDataSql + "group by ReportAppList.report_row_id \n";
				sDataSql = sDataSql + ") as sub_qry on core_qry.ReportRowId = sub_qry.ReportRowId;";	
			}
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("ExceptionReportViewList", aJsonDataList);

			/* Put all validation applications organized by project onto return JSON */
			sDataSql = "";
			sDataSql = sDataSql + "select a.idProject as ProjectId, b.idApp as AppId, b.name as AppName \n";
			sDataSql = sDataSql + "from project a, listApplications b \n";
			sDataSql = sDataSql + "where b.project_id = a.idProject and b.appType = 'Data Forensics' \n";
			sDataSql = sDataSql + "order by a.idProject, b.idApp;";
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);

			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("ValidationMasterList", aJsonDataList);

			/* Put all projects master data onto return JSON */
			sDataSql = "select idProject as ProjectId, concat(projectName, ' (', trim(lpad(idProject,4,' ')), ')')  as ProjectName from project;";
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("ProjectMasterList", aJsonDataList);

			sNewExceptionReport = sNewExceptionReport.replaceAll("'", "\"").replaceFirst("SessionProjectId", sProjectId);
			DateUtility.DebugLog("loadExceptionReportDataList",	String.format("New Exception Report JSON = \n%1$s\n", new JSONObject(sNewExceptionReport)));

			oJsonRetValue = oJsonRetValue.put("NewExceptionReport", new JSONObject(sNewExceptionReport));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	@RequestMapping(value = "/mainExceptionReportHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainExceptionReportHandler(HttpSession oSession,  @RequestParam String ExceptionReportData, HttpServletResponse oResponse) throws IOException {
		JSONObject oWrapperData = new JSONObject(ExceptionReportData);
		JSONObject oExceptionReportData = null;
		JSONObject oJsonResponse = new JSONObject();
		String sContext;
		String sMsg = "Sample server call";
		boolean lStatus = true;

		boolean lNewReport = false;

		try {
			sContext = oWrapperData.getString("Context");
			oExceptionReportData = oWrapperData.getJSONObject("Data");
			oExceptionReportData.put("idUser", oSession.getAttribute("idUser")) ;
			DateUtility.DebugLog("Exception Report data 01",	String.format("Begin controller Context = %1$s, Data = %2$s", sContext, oExceptionReportData));

			switch (sContext) {

			case "saveExceptionReport":

				DateUtility.DebugLog("Exception Report data 02", oExceptionReportData.getString("ReportRowId"));
				lNewReport = (Integer.parseInt(oExceptionReportData.getString("ReportRowId")) < 0) ? true : false;
				DateUtility.DebugLog("Exception Report data 03", String.format("%1$s" , lNewReport));

				if (lNewReport) {
					sMsg = createNewExceptionDataReport(oExceptionReportData);
				} else {
					sMsg = updateExistingExceptionDataReport(oExceptionReportData);
				}
				break;

			case "deleteExceptionReport":
				deleteExceptionReportData(oExceptionReportData);
				sMsg =  "Successfully deleted Exception Report data";
				break;

				default:
			}

			oJsonResponse.put("Status", lStatus);
			oJsonResponse.put("Msg", sMsg);

			DateUtility.DebugLog("Exception Report data 02", "End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	private String createNewExceptionDataReport(JSONObject oExceptionReportData) {
		String sInsertSql = "", sSelectSql = "";
		int nNewReportId = 0;
		int nNewReportRowId = 0;
		SqlRowSet oSqlRowSet = null;
		String sRetMsg = "";

		String[] aAppIds = (oExceptionReportData.getString("AppIds").trim().length() > 0) ? oExceptionReportData.getString("AppIds").split(",") : new String[]{};

		try {

			sSelectSql = "select case when (select count(*) from exception_data_report) < 1 then 1 else (select max(report_id) + 1 exception_data_report) end as NewReportId from exception_data_report;";
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
			nNewReportId = oSqlRowSet.next() ? oSqlRowSet.getInt("NewReportId") : -1;

			sInsertSql = sInsertSql + "insert into exception_data_report \n";
			sInsertSql = sInsertSql + "  (report_id, name, description, data_frequency, project_id, created_by, created_date, modified_by, modified_date) \n";
			sInsertSql = sInsertSql + "values \n";
			sInsertSql = sInsertSql + String.format(
					"(%1$s, '%2$s', '%3$s', %4$s, %5$s, %6$s, sysdate(), %7$s, sysdate());", nNewReportId,
					oExceptionReportData.getString("ReportName"), oExceptionReportData.getString("ReportDescription"),
					oExceptionReportData.getInt("DataPeriodInt"), oExceptionReportData.getInt("ProjectId"), oExceptionReportData.getInt("idUser"), oExceptionReportData.getInt("idUser"));

			jdbcTemplate.batchUpdate(sInsertSql);

			/* Get row id of newly inserted row (project_id + name) is unique */
			sSelectSql = String.format(
					"(select row_id as NewReportRowId from exception_data_report where project_id = %1$s and name = '%2$s' order by row_id desc limit 1);",
					oExceptionReportData.getInt("ProjectId"), oExceptionReportData.getString("ReportName")
				);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
			nNewReportRowId = oSqlRowSet.next() ? oSqlRowSet.getInt("NewReportRowId") : -1;

			/* Insert each validation app id as selected by user as new row each */
			for (String sAppId : aAppIds) {
				sInsertSql = String.format("insert into exception_data_report_apps (report_row_id, app_row_id) values (%1$s, %2$s);", nNewReportRowId, sAppId);
				jdbcTemplate.update(sInsertSql);
			}
			sRetMsg = "Successfully saved new Exception Data Report";
		} catch (Exception oException) {
			String sErrMsg = oException.getMessage();

			sRetMsg = (sErrMsg.toLowerCase().indexOf("duplicate") > -1) ? "Duplicate Report name within same project not allowed" : sErrMsg;
			oException.printStackTrace();
		}

		return sRetMsg;
	}

	private String updateExistingExceptionDataReport(JSONObject oExceptionReportData) {
		String insertSql = "";
		String updateSql = "";
		String deleteSql = "";
		String sRetMsg = "";

		try {
			/* Delete old mapping */
			deleteSql= deleteSql + "delete from exception_data_report_apps where report_row_id = %1$s";
			jdbcTemplate.update(String.format(deleteSql, oExceptionReportData.getInt("ReportRowId")));

			/* Update Recent data */
			updateSql = updateSql + "update exception_data_report set description = '%1$s', data_frequency =%2$s, modified_by = %3$s, modified_date = sysdate() where row_id = %4$s";
			jdbcTemplate.update(
						String.format(
								updateSql, oExceptionReportData.getString("ReportDescription"), oExceptionReportData.getInt("DataPeriodInt"),
								oExceptionReportData.getInt("idUser"), oExceptionReportData.getInt("ReportRowId")
							)
						);

			/* Insert each validation app id as selected by user as new row each */
			String sAppIdArray[] = oExceptionReportData.getString("AppIds").split(",");
			for(int i=0; i<sAppIdArray.length;i++) {
				insertSql = "insert into exception_data_report_apps (report_row_id, app_row_id) values (%1$s, %2$s)";
				jdbcTemplate.update(String.format(insertSql, oExceptionReportData.getInt("ReportRowId"), sAppIdArray[i]));

			}

			sRetMsg = "Successfully saved Exception Data Report";
		} catch (Exception oException) {
			sRetMsg = oException.getMessage();
			oException.printStackTrace();
		}

		return sRetMsg;
	}

	private void deleteExceptionReportData(JSONObject oExceptionReportData) {
		String deleteDataReportSql = "";
		String deleteDataReportAppsSql = "";

		try {
			/* Delete from exception_data_report table*/
			deleteDataReportSql = deleteDataReportSql + "delete from exception_data_report where row_id = %1$s";
			jdbcTemplate.update(String.format(deleteDataReportSql, oExceptionReportData.getInt("ReportRowId")));

			/* Delete from exception_data_report_apps table*/
			deleteDataReportAppsSql = deleteDataReportAppsSql + "delete from exception_data_report_apps where report_row_id = %1$s";
			jdbcTemplate.update(String.format(deleteDataReportAppsSql, oExceptionReportData.getInt("ReportRowId")));

		} catch (Exception oException) {
			oException.printStackTrace();
		}


	}

}
