package com.databuck.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.util.DateUtility;
import com.databuck.util.ExportUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExecutiveSummaryService {

	@Autowired
	public IValidationCheckDAO validationCheckDAO;

	@Autowired
	public IResultsDAO resultsDAO;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	private ExportUtility exportUtility;

	@Autowired
	private DashboardSummaryService dashboardSummaryService;
	
	private static final Logger LOG = Logger.getLogger(ExecutiveSummaryService.class);

	private JSONArray getFormattedDQIByDate(HashMap<String, Double> hashMapData) {
		JSONArray resultDQIs = new JSONArray();
		for (Map.Entry<String, Double> entry : hashMapData.entrySet()) {
			JSONObject dqIObj = new JSONObject();
			dqIObj.put("date", entry.getKey());
			dqIObj.put("dqi", entry.getValue());
			resultDQIs.put(dqIObj);
		}
		return resultDQIs;
	}

	/*
	 * The method 'getDqResultData' is the actual service method which fetching of
	 * dqData when Selected Domain-Project,Selected Date Range are provided.
	 */
	public JSONObject getDqResultData(JSONObject inputJson, int domainId, int projectId, int selectedDataRange)
			throws Exception {

		JSONObject dqJson = null;
		JSONObject graphDataNode = null;
		JSONArray graphDataNodes = new JSONArray();
		ObjectMapper mapperObj = new ObjectMapper();
		HashMap<String, Double> hashMapData = null;
		int dataDomaiindex = 0;
		String applicationIds = "";

		String allGraphData = "{ `dataDomainGraphsData`: [] }";
		String graphDataNodeJson = "{ 'dataDomain': null, `currentPeriodGraphData`: {},`aggregateDqIndex`: `` }";

		HashMap<Integer, String> validationDataDomains = validationCheckDAO.getAllValidationDataDomains(true);
		HashMap<String, String> dateRange = getDataRangeForDQSummary(selectedDataRange);

		validationDataDomains.put(0, "0"); // 0 = Consolidated results i.e. dummy data domain, 0 PK will never be in DB
		dqJson = new JSONObject(allGraphData.replace("`", "\""));

		/*
		 * Get consolidated graph data to be send to client as data domain proper string
		 */
		for (int index : validationDataDomains.keySet()) {
			DateUtility.DebugLog("getDqResultData 01",
					String.format("Data Domain Index / Index / Name '%1$s' / '%2$s' / '%2$s'", dataDomaiindex, index,
							validationDataDomains.get(index)));

			graphDataNode = new JSONObject(graphDataNodeJson.replace("`", "\""));

			// getting validation Ids based on domain-project combination
			applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, ((index < 1) ? -1 : index));
			graphDataNode = graphDataNode.put("dataDomain",
					String.format("%1$s-%2$s", dataDomaiindex, validationDataDomains.get(index)));

			hashMapData = resultsDAO.getDqGraphData(dateRange, "C", applicationIds);
			graphDataNode = graphDataNode.put("currentPeriodGraphData", getFormattedDQIByDate(hashMapData));

//            hashMapData = resultsDAO.getDqGraphData(dateRange, "P", applicationIds);
//            graphDataNode = graphDataNode.put("previousPeriodGraphData", (new JSONObject(mapperObj.writeValueAsString(hashMapData))));

			graphDataNode = graphDataNode.put("aggregateDqIndex", resultsDAO.getLatestDqIndex(applicationIds));
			graphDataNodes.put(dataDomaiindex, graphDataNode);

			dataDomaiindex++;
		}

		dqJson = dqJson.put("dataDomainGraphsData", graphDataNodes);

		// JSONArray selectedValidations = getLeastValidations(domainId,projectId);
		dqJson.put("leastDQIValidations", getLeastValidations(domainId, projectId));
		return dqJson;
	}

	public JSONObject getDqResultData(JSONObject inputJson, int domainId, int projectId, String fromDate, String toDate)
			throws Exception {

		JSONObject dqJson = null;
		JSONObject graphDataNode = null;
		JSONArray graphDataNodes = new JSONArray();
		ObjectMapper mapperObj = new ObjectMapper();
		HashMap<String, Double> hashMapData = null;
		int dataDomaiindex = 0;
		String applicationIds = "";

		String allGraphData = "{ `dataDomainGraphsData`: [] }";
		String graphDataNodeJson = "{ 'dataDomain': null, `currentPeriodGraphData`: {},`aggregateDqIndex`: `` }";

		HashMap<Integer, String> validationDataDomains = validationCheckDAO.getAllValidationDataDomains(true);

		validationDataDomains.put(0, "0"); // 0 = Consolidated results i.e. dummy data domain, 0 PK will never be in DB
		dqJson = new JSONObject(allGraphData.replace("`", "\""));

		/*
		 * Get consolidated graph data to be send to client as data domain proper string
		 */
		for (int index : validationDataDomains.keySet()) {
			DateUtility.DebugLog("getDqResultData 01",
					String.format("Data Domain Index / Index / Name '%1$s' / '%2$s' / '%2$s'", dataDomaiindex, index,
							validationDataDomains.get(index)));

			graphDataNode = new JSONObject(graphDataNodeJson.replace("`", "\""));

			// getting validation Ids based on domain-project combination
			applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, ((index < 1) ? -1 : index));
			graphDataNode = graphDataNode.put("dataDomain",
					String.format("%1$s-%2$s", dataDomaiindex, validationDataDomains.get(index)));

			hashMapData = resultsDAO.getDqGraphData(fromDate, toDate, "C", applicationIds);
			graphDataNode = graphDataNode.put("currentPeriodGraphData", getFormattedDQIByDate(hashMapData));

//            hashMapData = resultsDAO.getDqGraphData(dateRange, "P", applicationIds);
//            graphDataNode = graphDataNode.put("previousPeriodGraphData", (new JSONObject(mapperObj.writeValueAsString(hashMapData))));

			graphDataNode = graphDataNode.put("aggregateDqIndex", resultsDAO.getLatestDqIndex(applicationIds));
			graphDataNodes.put(dataDomaiindex, graphDataNode);

			dataDomaiindex++;
		}

		dqJson = dqJson.put("dataDomainGraphsData", graphDataNodes);

		// JSONArray selectedValidations = getLeastValidations(domainId,projectId);
		//dqJson.put("leastDQIValidations", getLeastValidations(domainId, projectId));
		dqJson.put("leastDQIValidations", getLeastValidations(domainId, projectId, fromDate, toDate));
		dqJson.put("totalTableMonitored", resultsDAO.getTotalTableMonitored(fromDate, toDate));
		return dqJson;
	}

	private List<Object> getLeastValidations(int domainId, int projectId) {

		// JSONArray selectedValidations = new JSONArray();
		List<Object> validations = new LinkedList<Object>();

		Map<Long, String> validationMap = resultsDAO.getValidationMapByDomainAndProject(domainId, projectId);
		List<Long> validationList = validationMap.keySet().stream().collect(Collectors.toList());

		String validationIds = validationList.stream().map(i -> i.toString()).collect(Collectors.joining(","));
		Map<Long, Double> validationAggrDQIMap = resultsDAO.getLeastDQIsByValidations(validationIds);

		for (Map.Entry<Long, Double> entry : validationAggrDQIMap.entrySet()) {
			JSONObject dqIObj = new JSONObject();
			dqIObj.put("validationName", validationMap.get(entry.getKey()));
			dqIObj.put("dqi", entry.getValue());
			// selectedValidations.put(dqIObj);
			validations.add(dqIObj);
		}
		return validations;
	}
	
	private List<Map<String, Object>> getLeastValidations(int domainId, int projectId, String fromDate, String toDate) {

		Map<Long, String> validationMap = resultsDAO.getValidationMapByDomainAndProject(domainId, projectId);
		List<Long> validationList = validationMap.keySet().stream().collect(Collectors.toList());

		String validationIds = validationList.stream().map(i -> i.toString()).collect(Collectors.joining(","));
		return resultsDAO.getLeastDQIsByValidations(validationIds, fromDate, toDate);
	}

	private HashMap<String, String> getDataRangeForDQSummary(int dateRange) {
		HashMap<String, String> dqJson = new HashMap<String, String>();

		try {
			switch (dateRange) {
			case -1:
			case 5: // Quarterly
				dqJson = DateUtility.getQuanterlyDateRange(LocalDate.now());
				break;

			case 1: // Daily
				dqJson = DateUtility.getDailyDateRange(LocalDate.now());
				break;

			case 2: // Weekly
				dqJson = DateUtility.getWeeklyDateRange(LocalDate.now());
				break;

			case 3: // Bi-Monthly
				dqJson = DateUtility.getBiMonthlyDateRange(LocalDate.now());
				break;

			case 4: // Monthly
				dqJson = DateUtility.getMonthlyDateRange(LocalDate.now());
				break;

			case 6: // Yearly
				dqJson = DateUtility.getYearlyDateRange(LocalDate.now());
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.getStackTrace();
			dqJson = new HashMap<String, String>();
		}
		DateUtility.DebugLog("getDataRangeForDQSummary",
				String.format("\n%1$s \t\t Current Period => %2$s / %3$s \t\t Previous Period => %4$s / %5$s",
						dateRange, dqJson.get("CurrentPeriodStartDate"), dqJson.get("CurrentPeriodToDate"),
						dqJson.get("PreviousPeriodStartDate"), dqJson.get("PreviousPeriodToDate")));
		return dqJson;
	}

	private String getStatusOfUserToken(String token) {
		String tokenStatus = "EXPIRED";

		try {
			// Fetch token details
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

			if (userToken != null) {

				// Check if token is already EXPIRED, else check if it is still active
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {

					// Check if token is expired
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";

					// Update the token status in database
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			} else {
				LOG.error("\n====> Failed to get Token details!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tokenStatus;
	}

	public JSONObject validateToken(String token) {
		// Fetch token details
		JSONObject tokenStatusObj = new JSONObject();
		String status = "failed";
		String msg = "";
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
		if (userToken != null) {
			// Check if token is still active
			String tokenStatus = getStatusOfUserToken(token);

			// If the token is ACTIVE, Fetch the project list
			if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE"))
				status = "success";
			else
				msg = "Token expired";

		} else
			msg = "Failed to get Token details";
		tokenStatusObj.put("status", status);
		tokenStatusObj.put("msg", msg);
		return tokenStatusObj;
	}

	public String prepareExecutiveSummaryCSVFile(int domainId, int projectId, int dateRange) {
		// It is a location very newly generated csv will be saved
		String dataFolder = System.getenv("DATABUCK_HOME") + "/csvFiles/";

		String executiveSummayFileName = String.format("DataQualityIndexByProject_%1$s.csv",
				DateUtility.getSysDateTimeStamp(false));
		String executiveSummayFullFileName = String.format("%1$s%2$s", dataFolder, executiveSummayFileName);

		HashMap<Integer, String> periodTitle = new HashMap<Integer, String>() {
			{
				put(-1, "Quarterly");
				put(1, "Daily");
				put(2, "Weekly");
				put(3, "Bi-Monthly");
				put(4, "Monthly");
				put(5, "Quarterly");
				put(6, "Yearly");
			}
		};

		/*
		 * This array will build CSV lines in memory and write at end in one write call
		 */
		List<String[]> dataList = new ArrayList<String[]>();

		String[] reportTitles = "Total Aggregated DQI".split(",");
		String[] columnsHeaders1 = "Date Selection,Date Period,Country Name,Project DQI".split(",");

		String[] emptyLine = " ".split(",");

		String[] reportTitles2 = "Aggregated DQI per Data Domain".split(",");
		String[] columnsHeaders2 = "Date Selection,Date Period,Country Name,Project DQI,Domain".split(",");

		String reportDataTmpl1 = "%1$s,%2$s,%3$s,%4$s";
		String reportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s";

		String[] reportDataRows1 = null;
		String[] reportDataRows2 = null;

		HashMap<String, String> dateRangeForSelectedDate = getDataRangeForDQSummary(dateRange);

		String selectedPeriod = periodTitle.get(dateRange);
		String currentPeriod = "%1$s - %2$s";

		HashMap<Integer, String> validationDataDomainMap = validationCheckDAO.getAllValidationDataDomains(true);

		String applicationIds = "";
		String dataRows = "";

		double currentDqIndex = 56.67;

		try {
			currentPeriod = String.format(currentPeriod,
					dateRangeForSelectedDate.get("CurrentPeriodStartDate").replace('-', '/'),
					dateRangeForSelectedDate.get("CurrentPeriodToDate").replace('-', '/'));

			/*
			 * Consolidated/total DQI for selected for all projects and date period. One
			 * line for each project
			 */
			dataList.add(reportTitles);
			dataList.add(columnsHeaders1);

			applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, -1);

			currentDqIndex = resultsDAO.getAverageDqIndexByDomainProject(applicationIds,
					dateRangeForSelectedDate.get("CurrentPeriodStartDate"),
					dateRangeForSelectedDate.get("CurrentPeriodToDate"));

			dataRows = String.format(reportDataTmpl1, selectedPeriod, currentPeriod,
					projectDAO.getProjectNameByProjectid(Long.valueOf(projectId)),
					String.format("%.2f", currentDqIndex));
			reportDataRows1 = dataRows.split(",");
			dataList.add(reportDataRows1);

			DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 02",
					String.format("Data Domain Result Data '%1$s'", dataRows));

			/* Empty lines and then next report title */
			dataList.add(emptyLine);
			dataList.add(emptyLine);
			dataList.add(reportTitles2);
			dataList.add(columnsHeaders2);

			/* Get Project and within it data domain wise data rows */
			for (int index : validationDataDomainMap.keySet()) {
				applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, index);

				// String reportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s";

				currentDqIndex = resultsDAO.getAverageDqIndexByDomainProjectsForDataDomain(
						dateRangeForSelectedDate.get("CurrentPeriodStartDate"),
						dateRangeForSelectedDate.get("CurrentPeriodToDate"), applicationIds);

				dataRows = String.format(reportDataTmpl2, selectedPeriod, currentPeriod,
						projectDAO.getProjectNameByProjectid(Long.valueOf(projectId)),
						String.format("%.2f", currentDqIndex), validationDataDomainMap.get(index));
				reportDataRows2 = dataRows.split(",");
				dataList.add(reportDataRows2);

				DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 03",
						String.format("Data Domain Result Data '%1$s'", dataRows));
			}
			dataList.add(emptyLine);
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		DateUtility.DebugLog("DataQualityIndexByProjectCsvFile 04",
				String.format("Writting '%1$s' rows to CSV file '%2$s'", dataList.size(), executiveSummayFullFileName));

		exportUtility.writeToCsv(dataList, executiveSummayFullFileName, ',');
		executiveSummayFileName = String.format("%1$s%2$s", dataFolder, executiveSummayFileName);
		return executiveSummayFileName;
	}

	public String prepareExecutiveSummaryCSVFile(int domainId, int projectId, String fromDate, String toDate) {
		// It is a location very newly generated csv will be saved
		String dataFolder = System.getenv("DATABUCK_HOME") + "/csvFiles/";

		String executiveSummayFileName = String.format("DataQualityIndexByProject_%1$s.csv",
				DateUtility.getSysDateTimeStamp(false));
		String executiveSummayFullFileName = String.format("%1$s%2$s", dataFolder, executiveSummayFileName);

		HashMap<Integer, String> periodTitle = new HashMap<Integer, String>() {
			{
				put(-1, "Quarterly");
				put(1, "Daily");
				put(2, "Weekly");
				put(3, "Bi-Monthly");
				put(4, "Monthly");
				put(5, "Quarterly");
				put(6, "Yearly");
			}
		};

		/*
		 * This array will build CSV lines in memory and write at end in one write call
		 */
		List<String[]> dataList = new ArrayList<String[]>();

		String[] reportTitles = "Total Aggregated DTS".split(",");
		String[] columnsHeaders1 = "Date Selection,Date Period,Project Name,Total Aggregated DTS".split(",");

		String[] emptyLine = " ".split(",");

		String[] reportTitles2 = "Aggregated DTS per Data Domain".split(",");
		String[] columnsHeaders2 = "Date Selection,Date Period,Project Name,Project DTS,Domain".split(",");

		String reportDataTmpl1 = "%1$s,%2$s,%3$s,%4$s";
		String reportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s";

		String[] reportDataRows1 = null;
		String[] reportDataRows2 = null;

		String selectedPeriod = "On Date Basis";
		String currentPeriod = "%1$s - %2$s";

		HashMap<Integer, String> validationDataDomainMap = validationCheckDAO.getAllValidationDataDomains(true);

		String applicationIds = "";
		String dataRows = "";

		double currentDqIndex = 56.67;

		try {
			currentPeriod = String.format(currentPeriod, fromDate.replace('-', '/'), toDate.replace('-', '/'));

			/*
			 * Consolidated/total DQI for selected for all projects and date period. One
			 * line for each project
			 */
			LOG.debug("reportTitles-->"+ Arrays.toString(reportTitles));
			dataList.add(reportTitles);
			dataList.add(columnsHeaders1);

			applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, -1);

			currentDqIndex = resultsDAO.getAverageDqIndexByDomainProject(applicationIds, fromDate, toDate);

			dataRows = String.format(reportDataTmpl1, selectedPeriod, currentPeriod,
					projectDAO.getProjectNameByProjectid(Long.valueOf(projectId)),
					String.format("%.2f", currentDqIndex));
			reportDataRows1 = dataRows.split(",");
			dataList.add(reportDataRows1);

			DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 02",
					String.format("Data Domain Result Data '%1$s'", dataRows));

			/* Empty lines and then next report title */
			dataList.add(emptyLine);
			dataList.add(emptyLine);
			dataList.add(reportTitles2);
			dataList.add(columnsHeaders2);

			/* Get Project and within it data domain wise data rows */
			for (int index : validationDataDomainMap.keySet()) {
				applicationIds = resultsDAO.getAppIdsForDomainProject(domainId, projectId, index);

				// String reportDataTmpl2 = "%1$s,%2$s,%3$s,%4$s,%5$s,%6$s";

				currentDqIndex = resultsDAO.getAverageDqIndexByDomainProjectsForDataDomain(fromDate, toDate,
						applicationIds);
				LOG.debug("currentDqIndex----->"+currentDqIndex);

				dataRows = String.format(reportDataTmpl2, selectedPeriod, currentPeriod,
						projectDAO.getProjectNameByProjectid(Long.valueOf(projectId)),
						String.format("%.2f", currentDqIndex), validationDataDomainMap.get(index));
				reportDataRows2 = dataRows.split(",");
				dataList.add(reportDataRows2);

				DateUtility.DebugLog("DataQualityIndexByProjectPdfFile 03",
						String.format("Data Domain Result Data '%1$s'", dataRows));
			}
			dataList.add(emptyLine);
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		LOG.debug("DataQualityIndexByProjectCsvFile 04"+
				String.format("Writting '%1$s' rows to CSV file '%2$s'", dataList.size(), executiveSummayFullFileName));

		exportUtility.writeToCsv(dataList, executiveSummayFullFileName, ',');
		executiveSummayFileName = String.format("%1$s%2$s", dataFolder, executiveSummayFileName);
		return executiveSummayFileName;
	}


	public JSONObject getValidationDeviationStatusByCheck(long idApp){
		// Fetch token details
		JSONObject dqStatusObj = new JSONObject();
		String status = "failed";
		String msg = "";
		JSONArray dtsMessages = new JSONArray();
		try {
			Map<String, String> validationCheckTestNames = dashboardSummaryService.getListOfPossibleTestValues();

			List<String> testNames = resultsDAO.getDashboardSummaryTestNamesByIdApp(idApp);

			int failedCount = 0;

			if (testNames != null && !testNames.isEmpty()){

				for (String testName : testNames) {

					try {
						if (testName != null && !testName.trim().isEmpty()) {

							JSONObject deviationStatObj = resultsDAO.getDeviationStatisticsByTestName(idApp, testName);

							if (deviationStatObj != null) {

								Double meanVal = deviationStatObj.getDouble("meanVal");
								Double stdDevVal = deviationStatObj.getDouble("stdDevVal");
								Double currentVal = deviationStatObj.getDouble("currentVal");

								String checkName = validationCheckTestNames.get(testName);

								if (meanVal != null && stdDevVal != null && currentVal != null) {

									double lowerLimit = meanVal - (3 * stdDevVal);
									double upperLimit = meanVal + (3 * stdDevVal);

									if (currentVal >= lowerLimit) {

										if (currentVal > upperLimit) {
											msg = "DTS gone Up for " + checkName + " from the previous Run";
											failedCount++;
										}
									} else {
										msg = "DTS gone down for " + checkName + " from the previous Run";
										failedCount++;
									}
									if (msg != null && !msg.trim().isEmpty()) {
										dtsMessages.put(msg);
										msg = "";
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			if (failedCount <= 0) {
				msg = "DTS did not change from previous run";
				dtsMessages.put(msg);
				msg = "";
				status = "success";
			}
		}else
			msg="Invalid IdApp";

		}catch (Exception e){
			e.printStackTrace();
		}

		dqStatusObj.put("status", status);
		dqStatusObj.put("message", msg);
		dqStatusObj.put("result", dtsMessages);
		return dqStatusObj;
	}

	public JSONObject getDqResultForEnterpriseData(String fromDate, String toDate)
			throws Exception {
		JSONObject graphDataNode = new JSONObject();
		HashMap<String, Double> hashMapData = null;
		hashMapData = resultsDAO.getDqGraphEnterpriseData(fromDate, toDate);
		graphDataNode.put("currentPeriodGraphData", getFormattedDQIByDate(hashMapData));
		graphDataNode.put("aggregateDqIndex", resultsDAO.getLatestDqI(fromDate, toDate));
		return graphDataNode;
	}
}
