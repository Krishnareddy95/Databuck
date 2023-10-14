package com.databuck.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

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

import com.databuck.bean.Project;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DQIGraphDAOI;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.databuck.util.ExportUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ExecutiveSummaryController {

	@Autowired
	public ExportUtility oExportUtility;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	public IResultsDAO iResultsDAO;

	@Autowired
	public IValidationCheckDAO oValidationCheckDAO;

	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	private RBACController rbacController;

	@Autowired
	DQIGraphDAOI dqiGraphDAOI;

	@RequestMapping(value = "/executiveSummary")
	public ModelAndView executiveSummary(HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("Results", "R", oSession);
		ModelAndView oModelAndView = null;
		Long nProjectId = 0l;
		Project oSelectedProject = null;
		List<Project> aProjectList = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("executiveSummary");

			nProjectId= (Long)oSession.getAttribute("projectId");
			oSelectedProject = iProjectDAO.getSelectedProject(nProjectId);
			aProjectList = getProjectsForLoggedInUser(oSession);

			oModelAndView.addObject("projectList", aProjectList);
			oModelAndView.addObject("selectedProject", oSelectedProject);
			oModelAndView.addObject("currentSection", "Dashboard");
			oModelAndView.addObject("currentLink", "View");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	private List<Project> getProjectsForLoggedInUser(HttpSession oSession){
		List<Project> lstProjectUser = (List<Project>)oSession.getAttribute("userProjectList");
		return lstProjectUser;
	}

	@RequestMapping(value = "/saveSvgAsPngToServer", method = RequestMethod.POST, produces = "application/json")
	public void saveSvgAsPngToServer(HttpSession oSession,  @RequestParam String PngData, HttpServletResponse oResponse) throws IOException {
		JSONObject oPngData = new JSONObject(PngData);
		JSONObject oJsonResponse = new JSONObject();

		String sMsg = "Png file successfully received and saved";
		boolean lStatus = true;

		String sDataBuckPdfPath, sImageString, sPngFullFileName = "";
		byte[] aPngContent = null;
		File oFile = null;
		boolean lFileExists,lFolderCreated = false;
		OutputStream oOutputStream = null;

		try {
			DateUtility.DebugLog("saveSvgAsPngToServer 01", String.format("Got input parameters from UI as '%1$s'", oPngData.getString("PngFile")));

			/* For first time it will create folder to work as standard PDF files folder under data buck home */
			sDataBuckPdfPath = System.getenv("DATABUCK_HOME") + "/pdfFiles/";
			oFile = new File(sDataBuckPdfPath);
			if ( !oFile.exists() ) { oFile.mkdir(); }

			sPngFullFileName = String.format("%1$s%2$s",sDataBuckPdfPath, oPngData.getString("PngFile"));
			oFile = new File(sPngFullFileName);

			sImageString = oPngData.getString("PngContent").split(",")[1];
			aPngContent = DatatypeConverter.parseBase64Binary(sImageString);

	        oOutputStream = new BufferedOutputStream(new FileOutputStream(oFile));
	        oOutputStream.write(aPngContent);

			oJsonResponse.put("Status", true);
			oJsonResponse.put("Msg", sMsg);

		} catch (Exception oException) {
			oException.printStackTrace();

			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}

		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	@RequestMapping(value = "/mainDqResultsHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainDqResultsHandler(HttpSession oSession,  @RequestParam String sSelectedInputs, HttpServletResponse oResponse) throws IOException {
		JSONObject oSelectedInputs = new JSONObject(sSelectedInputs);

		JSONObject oJsonResponse = new JSONObject();
		JSONObject oDqResultData = new JSONObject();
		String sMsg = "Results data successfully build";
		boolean lStatus = true;

		try {
			DateUtility.DebugLog("mainDqResultsHandler 01", String.format("Got input parameters from UI as '%1$s'", oSelectedInputs));

			oDqResultData = getDqResultData(oSelectedInputs);

			oJsonResponse.put("DqResultData", oDqResultData);
			oJsonResponse.put("Status", true);
			oJsonResponse.put("Msg", sMsg);

		} catch (Exception oException) {
			oException.printStackTrace();

			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}

		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	private JSONObject getDqResultData(JSONObject oSelectedInputs) throws Exception {
		JSONObject oRetValue = null;
		JSONObject oGraphDataNode = null;
		JSONArray aGraphDataNodes = new JSONArray();
		ObjectMapper oMapperObj = new ObjectMapper();

		String  sHashMapData = "";
		HashMap<String, Double> oHashMapData = null;
		int nDataDomainIndex = 0;
		String sApplicationIds = "";

		String sAllGraphData = "{ `dataDomainGraphsData`: [] }";
		String sGraphDataNode = "{ 'dataDomain': null, `currentPeriodGraphData`: {},  `previousPeriodGraphData`: {}, `aggregateDqIndex`: `` }";

		String sSelectedProjectIds = oSelectedInputs.getString("SelectedProjects");
		int nSelectedDataRange = oSelectedInputs.getInt("SelectedDateRange");

		HashMap<Integer, String> oValidationDataDomains = oValidationCheckDAO.getAllValidationDataDomains(true);
		HashMap<String, String> oDateRange = getDataRangeForDQSummary(nSelectedDataRange);

		oValidationDataDomains.put(0, "0");				// 0 = Consolidated results i.e. dummy data domain, 0 PK will never be in DB
		oRetValue = new JSONObject(sAllGraphData.replace("`", "\""));

		/* Get consolidated graph data to be send to client as data domain proper string */
		for (int nIndex : oValidationDataDomains.keySet()) {
			DateUtility.DebugLog("getDqResultData 01", String.format("Data Domain Index / Index / Name '%1$s' / '%2$s' / '%2$s'", nDataDomainIndex, nIndex, oValidationDataDomains.get(nIndex)));

			oGraphDataNode = new JSONObject(sGraphDataNode.replace("`", "\""));
			sApplicationIds = iResultsDAO.getApplicationsForSelectedProjectsByDataDomain(sSelectedProjectIds, ((nIndex < 1) ? -1 : nIndex));

			oGraphDataNode = oGraphDataNode.put("dataDomain", String.format("%1$s-%2$s", nDataDomainIndex, oValidationDataDomains.get(nIndex)));

			oHashMapData = getDqGraphData(oDateRange, "C", sApplicationIds);
			oGraphDataNode = oGraphDataNode.put("currentPeriodGraphData", (new JSONObject(oMapperObj.writeValueAsString(oHashMapData))));

			oHashMapData = getDqGraphData(oDateRange, "P", sApplicationIds);
			oGraphDataNode = oGraphDataNode.put("previousPeriodGraphData", (new JSONObject(oMapperObj.writeValueAsString(oHashMapData))));

			oGraphDataNode = oGraphDataNode.put("aggregateDqIndex", getLatestDqIndex(sApplicationIds));
			aGraphDataNodes.put(nDataDomainIndex, oGraphDataNode);

			nDataDomainIndex++;
		}

		oRetValue = oRetValue.put("dataDomainGraphsData", aGraphDataNodes);

		return oRetValue;
	}

	private HashMap<String, Double> getDqGraphData(HashMap<String, String> oDateRange, String sWhichPeriod, String sApplicationIds) {
		HashMap<String, Double> oRetValue = new HashMap<String, Double>();
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;

		String sPeriodStartDate = "";
		String sPeriodToDate = "";

		try {
			sPeriodStartDate = sWhichPeriod.equalsIgnoreCase("C") ? oDateRange.get("CurrentPeriodStartDate") : oDateRange.get("PreviousPeriodStartDate");
			sPeriodToDate = sWhichPeriod.equalsIgnoreCase("C") ? oDateRange.get("CurrentPeriodToDate") : oDateRange.get("PreviousPeriodToDate");

			sSqlQry = sSqlQry + "select sum(DQI)/count(date) as aggDqIndex, Date \n";
			sSqlQry = sSqlQry + "from DashBoard_Summary \n";
			sSqlQry = sSqlQry + String.format("where date between '%1$s' and '%2$s' \n",sPeriodStartDate, sPeriodToDate);
			sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", sApplicationIds);
			sSqlQry = sSqlQry + "group by Date order by date desc";

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);

			while (oSqlRowSet.next()) {
				oRetValue.put(oSqlRowSet.getString("Date"), oSqlRowSet.getDouble("aggDqIndex"));
			}

		} catch (Exception oException) {
			oRetValue = new HashMap<String, Double>();
			oException.printStackTrace();
		}
		return oRetValue;
	}

	private String getLatestDqIndex(String sApplicationIds) {
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
				sSqlQry = sSqlQry + "	to_date(Date::text, 'YYYY-MM-DD') as Date \n";
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
				sRetValue = String.format("%.2f,%2$s",dAvgDqIndex, oSqlRowSet.getString("Date"));
			}
		} catch (Exception oException) {
			sRetValue = "0.0,0000-00-00";
			oException.printStackTrace();
		}
		return sRetValue;
	}

	private double getAverageDqIndexByProjectsAndPeriodByDataDomain(String sPeriodStartDate, String sPeriodToDate, String sApplicationIds) {
		double dAvgDqIndex = 0.0;
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;

		try {
			sSqlQry = sSqlQry + "select AVG(DQI) as AggregateDqIndex \n";
			sSqlQry = sSqlQry + "from DashBoard_Summary \n";
			sSqlQry = sSqlQry + String.format("where date between '%1$s' and '%2$s' \n",sPeriodStartDate, sPeriodToDate);
			sSqlQry = sSqlQry + String.format("and AppId in (%1$s) \n", sApplicationIds);
			sSqlQry = sSqlQry + "group by Date order by date desc limit 1";

			System.out.println("!!!!!getAverageDqIndexByProjectsAndPeriodByDataDomain ==>"+sSqlQry);

			oSqlRowSet = jdbcTemplate1.queryForRowSet(sSqlQry);
			dAvgDqIndex = (oSqlRowSet.next()) ? oSqlRowSet.getDouble("AggregateDqIndex") : dAvgDqIndex;

		} catch (Exception oException) {
			oException.printStackTrace();
		}
		return dAvgDqIndex;
	}

	@RequestMapping(value = "/DownloadDqReportAsCsv")
	public void doDownload(HttpServletRequest oRequest, HttpServletResponse oResponse) {
		HttpSession session = oRequest.getSession();
		String sProjectIds = oRequest.getParameter("sProjectIds");
		String sSelectedDateRange = oRequest.getParameter("sSelectedDateRange");
		String sSelectedFormat = oRequest.getParameter("sSelectedFormat");
		String sPngRandomToken = oRequest.getParameter("sPngRandomToken");

		int nSelectedDateRange = Integer.parseInt(sSelectedDateRange);

		String sDataFileFolder = null;
		String sDownloadFileName = null;
		String sDownloadFileFullName = null;

		DateUtility.DebugLog("DownloadDqReportAsCsv 01", String.format("sProjectIds = %1$s, %2$s, %3$s, %4$s", sProjectIds, sSelectedDateRange, oRequest.getParameter("sSelectedFormat"),oRequest.getParameter("sPngRandomToken")));

		try {
			if (sSelectedFormat.toUpperCase().indexOf("PDF") > -1) {
				sDataFileFolder = System.getenv("DATABUCK_HOME") + "/pdfFiles/";
				sDownloadFileName = DataQualityIndexByProjectPdfFile(sDataFileFolder, sPngRandomToken);
			} else {
				sDataFileFolder = System.getenv("DATABUCK_HOME") + "/csvFiles/";
				sDownloadFileName = DataQualityIndexByProjectCsvFile(sDataFileFolder, sProjectIds, nSelectedDateRange);
			}
			sDownloadFileFullName = String.format("%1$s%2$s", sDataFileFolder,sDownloadFileName);

			ServletContext oContext = oRequest.getSession().getServletContext();

			File oDownloadFile = new File(sDownloadFileFullName);
			FileInputStream inputStream = new FileInputStream(oDownloadFile);

			String mimeType = oContext.getMimeType(sDownloadFileFullName);
			if (mimeType == null) {	mimeType = "application/octet-stream";	}
			System.out.println("MIME type: " + mimeType);

			oResponse.setContentType(mimeType);
			oResponse.setContentLength((int) oDownloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", sDownloadFileName);
			oResponse.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = oResponse.getOutputStream();

			byte[] buffer = new byte[1024 * 1000];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();

			System.out.println("Download completed");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Pradeep 24-06-2021 not called as for PDF browser print save as used */
	private String DataQualityIndexByProjectPdfFile(String sDataFolder, String sPngRandomToken) {
		DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 01", String.format("sDataFolder = '%1$s', sPngRandomToken = '%2$s'",sDataFolder, sPngRandomToken));
		return "DataBuck.pdf";
	}

	private String DataQualityIndexByProjectCsvFile(String sDataFolder, String sSelectedProjectIds, int nDateRange) {
		String sCsvReturnFileName = String.format("DataQualityIndexByProject_%1$s.csv", DateUtility.getSysDateTimeStamp(false));
		String sCsvFullFileName = String.format("%1$s%2$s", sDataFolder, sCsvReturnFileName);

		HashMap<Integer, String> oPeriodTitle = new HashMap<Integer, String>() {{
		    put(-1, "Quarterly");
		    put(1, "Daily");
		    put(2, "Weekly");
		    put(3, "Bi-Monthly");
		    put(4, "Monthly");
		    put(5, "Quarterly");
		    put(6, "Yearly");
		}};

		/* This array will build CSV lines in memory and write at end in one write call */
		List<String[]> aDataList = new ArrayList<String[]>();

		String[] aReportTitle1 = "Total Aggregated DQI".split(",");
		String[] aColumnsHeader1 = "Date Selection,Date Period,Country Name,Current DQI,Previous DQI".split(",");

		String[] aEmptyLine = " ".split(",");

		String[] aReportTitle2 = "Aggregated DQI per Data Domain".split(",");
		String[] aColumnsHeader2 = "Date Selection,Date Period,Country Name,Current DQI,Previous DQI,Domain".split(",");

		String sReportDataTmpl1 = "%1$s,%2$s,%3$s,%4$s,%5$s";
		String sReportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s";

		String[] aReportDataRow1 = null;
		String[] aReportDataRow2 = null;

		HashMap<String, String> oDateRangeForSelectedDate = getDataRangeForDQSummary(nDateRange);

		String sSelectedPeriod = oPeriodTitle.get(nDateRange);
		String sCurrentPeriod = "%1$s - %2$s";
		String sPreviousPeriod = "%1$s - %2$s";

		String[] aSelectedProjectIds = sSelectedProjectIds.split(",");
		HashMap<Integer, String> oValidationDataDomains = oValidationCheckDAO.getAllValidationDataDomains(true);

		String sApplicationIds = "";
		String sDataRow = "";

		String sDqIndexAndDate = "";
		double dDqIndex = 56.67;
		double dCurrentDqIndex = 56.67;
		double dPreviousDqIndex = 56.67;

		DateUtility.DebugLog("DataQualityIndexByProjectCsvFile 01", String.format("sProjectIds = '%1$s', Period = '%2$s'",sSelectedProjectIds, sSelectedPeriod));

		try {
			sCurrentPeriod = String.format(sCurrentPeriod,
					oDateRangeForSelectedDate.get("CurrentPeriodStartDate").replace('-', '/'),
					oDateRangeForSelectedDate.get("CurrentPeriodToDate").replace('-', '/')
				);
			sPreviousPeriod = String.format(sPreviousPeriod,
					oDateRangeForSelectedDate.get("PreviousPeriodStartDate").replace('-', '/'),
					oDateRangeForSelectedDate.get("PreviousPeriodToDate").replace('-', '/')
				);

			/* Consolidated/total DQI for selected for all projects and date period. One line for each project */
			aDataList.add(aReportTitle1);
			aDataList.add(aColumnsHeader1);

			for (String sProjectId : aSelectedProjectIds ) {
				long nProjectId = Long.parseLong(sProjectId);

				dCurrentDqIndex = iResultsDAO.getAverageDqIndexByProjectsAndPeriod(sProjectId, oDateRangeForSelectedDate.get("CurrentPeriodStartDate"), oDateRangeForSelectedDate.get("CurrentPeriodToDate"));
				dPreviousDqIndex = iResultsDAO.getAverageDqIndexByProjectsAndPeriod(sProjectId, oDateRangeForSelectedDate.get("PreviousPeriodStartDate"), oDateRangeForSelectedDate.get("PreviousPeriodToDate"));

				sDataRow = String.format(sReportDataTmpl1, sSelectedPeriod, sCurrentPeriod,
						iProjectDAO.getProjectNameByProjectid(nProjectId),
						String.format("%.2f", dCurrentDqIndex), String.format("%.2f", dPreviousDqIndex));
				aReportDataRow1 = sDataRow.split(",");
				aDataList.add(aReportDataRow1);

				DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 02", String.format("Data Domain Result Data '%1$s'", sDataRow));
			}

			/* Empty lines and then next report title */
			aDataList.add(aEmptyLine);
			aDataList.add(aEmptyLine);
			aDataList.add(aReportTitle2);
			aDataList.add(aColumnsHeader2);

			/* Get Project and within it data domain wise data rows */
			for (String sProjectId : aSelectedProjectIds ) {

				for (int nIndex : oValidationDataDomains.keySet()) {
					sApplicationIds = iResultsDAO.getApplicationsForSelectedProjectsByDataDomain(sProjectId, nIndex);

					// String sReportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s";

					dCurrentDqIndex = getAverageDqIndexByProjectsAndPeriodByDataDomain(
							oDateRangeForSelectedDate.get("CurrentPeriodStartDate"), oDateRangeForSelectedDate.get("CurrentPeriodToDate"),
							sApplicationIds);
					dPreviousDqIndex = getAverageDqIndexByProjectsAndPeriodByDataDomain(
							oDateRangeForSelectedDate.get("PreviousPeriodStartDate"), oDateRangeForSelectedDate.get("PreviousPeriodToDate"),
							sApplicationIds);

					sDataRow = String.format(sReportDataTmpl2, sSelectedPeriod, sCurrentPeriod,
							iProjectDAO.getProjectNameByProjectid(Long.parseLong(sProjectId)),
							String.format("%.2f", dCurrentDqIndex), String.format("%.2f", dPreviousDqIndex),
							oValidationDataDomains.get(nIndex)
							);
					aReportDataRow2 = sDataRow.split(",");
					aDataList.add(aReportDataRow2);

					DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 03", String.format("Data Domain Result Data '%1$s'", sDataRow));
				}
				aDataList.add(aEmptyLine);
			}
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		DateUtility.DebugLog("DataQualityIndexByProjectCsvFile 04", String.format("Writting '%1$s' rows to CSV file '%2$s'", aDataList.size(), sCsvFullFileName));

		oExportUtility.writeToCsv(aDataList, sCsvFullFileName, ',');
	    return sCsvReturnFileName;
	}

	private HashMap<String, String> getDataRangeForDQSummary(int nDateRange) {
		HashMap<String, String> oRetValue = new HashMap<String, String>();

		try {
			switch (nDateRange) {
				case -1: case 5:         // Quarterly
					oRetValue = DateUtility.getQuanterlyDateRange(LocalDate.now());
					break;

				case 1:            		// Daily
					oRetValue = DateUtility.getDailyDateRange(LocalDate.now());
					break;

				case 2:            		// Weekly
					oRetValue = DateUtility.getWeeklyDateRange(LocalDate.now());
					break;

				case 3:            		// Bi-Monthly
					oRetValue = DateUtility.getBiMonthlyDateRange(LocalDate.now());
					break;

				case 4:            		// Monthly
					oRetValue = DateUtility.getMonthlyDateRange(LocalDate.now());
					break;

				case 6:            		// Yearly
					oRetValue = DateUtility.getYearlyDateRange(LocalDate.now());
					break;

				default:
					break;
			}

		} catch (Exception oException) {
			oException.getStackTrace();
			oRetValue = new HashMap<String, String>();
		}
		DateUtility.DebugLog("getDataRangeForDQSummary",
				String.format("\n%1$s \t\t Current Period => %2$s / %3$s \t\t Previous Period => %4$s / %5$s", 	nDateRange,
				oRetValue.get("CurrentPeriodStartDate"), oRetValue.get("CurrentPeriodToDate"),
				oRetValue.get("PreviousPeriodStartDate"), oRetValue.get("PreviousPeriodToDate")
			)
		);
		return oRetValue;
	}

	/* Not used for newly modified page - Begin */
	private String getNoOfSources(String sSelectedProjects, int nDateRange) {

		String totalNoOfSources = "";
		HashMap<String, String> dateRangeForSelectedDate = getDataRangeForDQSummary(nDateRange);

		String CurrentPeriodStartDate = dateRangeForSelectedDate.get("CurrentPeriodStartDate");
		String CurrentPeriodToDate = dateRangeForSelectedDate.get("CurrentPeriodToDate");

		//totalNoOfSources = iResultsDAO.getTotalNoOfSourcesForCurrentProjectByProjectIdAsPerDate(sSelectedProjects, CurrentPeriodStartDate, CurrentPeriodToDate);
		totalNoOfSources = iResultsDAO.getTotalNoOfSourcesForCurrentProjectByProjectId(sSelectedProjects);
		return totalNoOfSources ;
	}

	private int getNoOfValidations(String sSelectedProjects, int nDateRange) {
		int totalNoOfValidations = 0;
		int totalNoOfSources = 0;
		HashMap<String, String> dateRangeForSelectedDate = getDataRangeForDQSummary(nDateRange);

		String CurrentPeriodStartDate = dateRangeForSelectedDate.get("CurrentPeriodStartDate");
		String CurrentPeriodToDate = dateRangeForSelectedDate.get("CurrentPeriodToDate");

		totalNoOfValidations = iResultsDAO.getTotalNoOfValidationsForCurrentProjectByProjectIdAsPerDate(sSelectedProjects, CurrentPeriodStartDate, CurrentPeriodToDate);
		return totalNoOfValidations ;
	}

	private int getNoOfFailedValidations(String sSelectedProjects, int nDateRange) {
		int noOfFailedValidationsForLast30Days = 0;
		HashMap<String, String> dateRangeForSelectedDate = getDataRangeForDQSummary(nDateRange);

		String CurrentPeriodStartDate = dateRangeForSelectedDate.get("CurrentPeriodStartDate");
		String CurrentPeriodToDate = dateRangeForSelectedDate.get("CurrentPeriodToDate");

		noOfFailedValidationsForLast30Days = iResultsDAO.getNoOfFailedValidationsForLast30DaysAsPerDate(sSelectedProjects, CurrentPeriodStartDate, CurrentPeriodToDate);
		return noOfFailedValidationsForLast30Days;
	}
	/* Not used for newly modified page - End */

}
