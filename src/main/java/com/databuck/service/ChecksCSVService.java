package com.databuck.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.bean.Project;
import com.databuck.bean.SqlRule;
import com.databuck.bean.UserToken;
import com.databuck.csvmodel.AdvDataDriftCheck;
import com.databuck.csvmodel.AdvDataDriftCountSummary;
import com.databuck.csvmodel.CustomRule;
import com.databuck.csvmodel.CustomUniqueness;
import com.databuck.csvmodel.CustomeSummary;
import com.databuck.csvmodel.DataDrift;
import com.databuck.csvmodel.DataTypeCheck;
import com.databuck.csvmodel.DateAnomalyCheck;
import com.databuck.csvmodel.DateConsSummary;
import com.databuck.csvmodel.DefaultPatternCheck;
import com.databuck.csvmodel.DefaultValueCheck;
import com.databuck.csvmodel.DistributionCheck;
import com.databuck.csvmodel.DuplicateSummary;
import com.databuck.csvmodel.GlobalRule;
import com.databuck.csvmodel.LengthCheck;
import com.databuck.csvmodel.MicroDateRuleCheck;
import com.databuck.csvmodel.MicroNullCheck;
import com.databuck.csvmodel.NullCheck;
import com.databuck.csvmodel.PatternCheck;
import com.databuck.csvmodel.PrimaryKeyDashboardTable;
import com.databuck.csvmodel.ProcessData;
import com.databuck.csvmodel.RecordAnomaly;
import com.databuck.csvmodel.RecordAnomalyHistory;
import com.databuck.csvmodel.RecordCountReasonability;
import com.databuck.csvmodel.RecordCountResTransaction;
import com.databuck.csvmodel.RecordCountSumGroup;
import com.databuck.csvmodel.SequenceCheck;
import com.databuck.csvmodel.StringDuplicate;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IEssentialChecksRepo;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.MatchingResultDao;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class ChecksCSVService {
	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private IEssentialChecksRepo essentialCheckRepo;

	@Autowired
	private DashboardSummaryService dashboardService;

	@Autowired
	private PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	public IMatchingResultService iMatchingResultService;

	@Autowired
	MatchingResultDao matchingresultdao;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private TaskService taskService;

	@Autowired
	public IProjectService projService;

	@Autowired
	private ITaskDAO iTaskDAO;
	
	private static final Logger LOG = Logger.getLogger(ChecksCSVService.class);

	public String validateUserToken(String token) {
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
		if (userToken != null) {
			String tokenStatus = "";
			if (userToken != null) {
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			}
			if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE"))
				return "success";
			else
				return "failed";

		} else {
			return "failed";
		}
	}

	public List<NullCheck> getNullChecks(String requestObj) {
		JSONObject inputJson = new JSONObject(requestObj);
		try {
			Long idApp = inputJson.getLong("idApp");
			String fromDate = inputJson.getString("fromDate");
			String toDate = inputJson.getString("toDate");
			String checkName = inputJson.getString("checkName");
			String tableName = inputJson.getString("tableName");
			return this.essentialCheckRepo.getNullChecks(idApp, fromDate, toDate, checkName, tableName);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<MicroNullCheck> getMicroNullChecks(String inputsJsonStr)
			throws JsonMappingException, JsonProcessingException {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<MicroNullCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), MicroNullCheck.class));
		}
		return results;
	}

	public List<LengthCheck> getLengthChecks(String inputsJsonStr)
			throws JsonMappingException, JsonProcessingException {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<LengthCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), LengthCheck.class));
		}
		return results;
	}

	public List<AdvDataDriftCheck> getAdvDataDriftChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<AdvDataDriftCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), AdvDataDriftCheck.class));
		}
		return results;
	}

	public List<AdvDataDriftCountSummary> getAdvDataDriftCountSummaryChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<AdvDataDriftCountSummary> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), AdvDataDriftCountSummary.class));
		}
		return results;
	}

	public List<DataTypeCheck> getDataTypeChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DataTypeCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DataTypeCheck.class));
		}
		return results;
	}

	public List<DefaultValueCheck> getDefaultValueChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DefaultValueCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DefaultValueCheck.class));
		}
		return results;
	}

	public List<PatternCheck> getPatternChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<PatternCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), PatternCheck.class));
		}
		return results;
	}

	public List<RecordAnomaly> getRecordAnomalyChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<RecordAnomaly> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), RecordAnomaly.class));
		}
		return results;
	}

	public List<RecordAnomalyHistory> getRecordAnomalyHistory(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<RecordAnomalyHistory> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), RecordAnomalyHistory.class));
		}
		return results;
	}

	public List<RecordCountReasonability> getRecordCountReasonabilityChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<RecordCountReasonability> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), RecordCountReasonability.class));
		}
		return results;
	}

	public List<CustomeSummary> getCustomeSummaryChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<CustomeSummary> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), CustomeSummary.class));
		}
		return results;
	}

	public List<CustomUniqueness> getCustomeUniqueChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<CustomUniqueness> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), CustomUniqueness.class));
		}
		return results;
	}

	public List<DataDrift> getDataDirftChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DataDrift> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DataDrift.class));
		}
		return results;
	}

	public List<DistributionCheck> getDistributionChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DistributionCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DistributionCheck.class));
		}
		return results;
	}

	public List<SequenceCheck> getSequenceChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<SequenceCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), SequenceCheck.class));
		}
		return results;
	}

	public List<StringDuplicate> getStringDuplicateChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<StringDuplicate> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), StringDuplicate.class));
		}
		return results;
	}

	public List<MicroDateRuleCheck> getMicroDateRuleChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<MicroDateRuleCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), MicroDateRuleCheck.class));
		}
		return results;
	}

	public List<DateAnomalyCheck> getDateAnomalyChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DateAnomalyCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DateAnomalyCheck.class));
		}
		return results;
	}

	public List<SqlRule> getSqlRules(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<SqlRule> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), SqlRule.class));
		}
		return results;
	}

	public List<GlobalRule> getGlobalRules(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<GlobalRule> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), GlobalRule.class));
		}
		return results;
	}

	public List<CustomRule> getCustomRules(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<CustomRule> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), CustomRule.class));
		}
		return results;
	}

	public List<ProcessData> getProcessData(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<ProcessData> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), ProcessData.class));
		}
		return results;
	}

	public List<DateConsSummary> getDateConsSummary(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DateConsSummary> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DateConsSummary.class));
		}
		return results;
	}

	public List<RecordCountResTransaction> getRecordCountTranChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<RecordCountResTransaction> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), RecordCountResTransaction.class));
		}
		return results;
	}

	public List<DefaultPatternCheck> getDefaultPatternCheck(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DefaultPatternCheck> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DefaultPatternCheck.class));
		}
		return results;
	}

	public List<DuplicateSummary> getDuplicateSummary(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<DuplicateSummary> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), DuplicateSummary.class));
		}
		return results;
	}

	public List<RecordCountSumGroup> getRecordCountDGroupChecks(String inputsJsonStr) {
		Gson gson = new Gson();
		JSONArray arr = getResults(inputsJsonStr);
		List<RecordCountSumGroup> results = new ArrayList<>();
		for (Object json : arr) {
			results.add(gson.fromJson(json.toString(), RecordCountSumGroup.class));
		}
		return results;
	}

	private JSONArray getResults(String inputsJsonStr) {
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		JSONObject data = dashboardService.getStatsForValidationCheckByFilter(inputJson);
		return data.getJSONArray(inputJson.getString("tableName"));
	}

	public List<HashMap<String, String>> getPrimaryKeyMatchingRecordsDetails(String appId) {
		String sDataSql = "select * from DATA_MATCHING_" + appId + "_MATCHED";
		return JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate1, sDataSql, new String[] {}, "", null);
	}

	public List<PrimaryMatchingSummary> getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(String appId) {
		return iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate("DATA_MATCHING_" + appId + "_SUMMARY");
	}

	public List<PrimaryKeyDashboardTable> getPrimaryKeyMatchingDashboardData(String appId, String source1,
			String source2) {
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(Long.parseLong(appId));
		Double threshold = null;
		while (laData.next()) {
			threshold = laData.getDouble("matchingThreshold");
		}
		Map<String, String> primaryKeyMatchingMap = iMatchingResultService
				.getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate("DATA_MATCHING_" + appId + "_SUMMARY");
		List<PrimaryKeyDashboardTable> dashboardRecords = new ArrayList<PrimaryKeyDashboardTable>();
		for (int i = 0; i < 5; i++) {
			PrimaryKeyDashboardTable primaryKeyDashboardTable = new PrimaryKeyDashboardTable();
			if (i == 0) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Records on " + source1);
				primaryKeyDashboardTable.setMeasurement(primaryKeyMatchingMap.get("leftTotalCount"));
				primaryKeyDashboardTable.setStatusNRecordCount(primaryKeyMatchingMap.get("leftTotalCount"));
			} else if (i == 1) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Records on " + source2);
				primaryKeyDashboardTable.setMeasurement(primaryKeyMatchingMap.get("rightTotalCount"));
				primaryKeyDashboardTable.setStatusNRecordCount(primaryKeyMatchingMap.get("leftTotalCount"));
			} else if (i == 2) {
				primaryKeyDashboardTable.setKeyMetrics("Records in " + source1 + " not in " + source2);
				primaryKeyDashboardTable.setMeasurement(primaryKeyMatchingMap.get("leftOnlyCount"));
				primaryKeyDashboardTable.setStatusNRecordCount(primaryKeyMatchingMap.get("leftOnlyStatus"));
				primaryKeyDashboardTable.setPercentage(primaryKeyMatchingMap.get("leftOnlyPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			} else if (i == 3) {
				primaryKeyDashboardTable.setKeyMetrics("Records in " + source2 + " not in " + source1);
				primaryKeyDashboardTable.setMeasurement(primaryKeyMatchingMap.get("rightOnlyCount"));
				primaryKeyDashboardTable.setStatusNRecordCount(primaryKeyMatchingMap.get("rightOnlyStatus"));
				primaryKeyDashboardTable.setPercentage(primaryKeyMatchingMap.get("rightOnlyPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			} else if (i == 4) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Unmatched Item");
				primaryKeyDashboardTable.setMeasurement(primaryKeyMatchingMap.get("unMatchedCount"));
				primaryKeyDashboardTable.setStatusNRecordCount(primaryKeyMatchingMap.get("unMatchedStatus"));
				primaryKeyDashboardTable.setPercentage(primaryKeyMatchingMap.get("unMatchedPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			}
			dashboardRecords.add(primaryKeyDashboardTable);
		}
		return dashboardRecords;
	}

	public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingResultsDetails(String domainId, String projectId,
			String fromDate, String toDate) {
		return primaryKeyMatchingResultService.getPrimaryKeyMatchingDashboard(domainId, projectId, fromDate, toDate);
	}

	public List<KeyMeasurementMatchingDashboard> getKeyMeasurementMatchingDashboardByProjectNDateFilter(String domainId,
			String projectId, String fromDate, String toDate) {
		return matchingresultdao.getKeyMeasurementMatchingDashboardByProjectNDateFilter(domainId, projectId, fromDate,
				toDate);
	}

	public List<Map<String, Object>> getTranTablesDetails(String tableName) {
		return primaryKeyMatchingResultService.getTranDetailAllDetails(tableName,
				primaryKeyMatchingResultService.getColumnNames(tableName), null);
	}

	public List<PrimaryKeyDashboardTable> getKeyMeasurementMatchDashboardData(String appId, String source1,
			String source2) {
		SqlRowSet laData = iMatchingResultService.getThresholdFromListApplication(Long.parseLong(appId));
		Double threshold = null;
		while (laData.next()) {
			threshold = laData.getDouble("matchingThreshold");
		}
		Map<String, String> keyMeasurementMatchingMap = iMatchingResultService
				.getDataFromDataMatchingSummary("DATA_MATCHING_" + appId + "_SUMMARY");
		List<PrimaryKeyDashboardTable> dashboardRecords = new ArrayList<PrimaryKeyDashboardTable>();
		Object[] getZeroStatusAbdCount = iMatchingResultService
				.getZeroStatusAbdCount("DATA_MATCHING_" + appId + "_SUMMARY", Long.parseLong(appId));
		for (int i = 0; i < 5; i++) {
			PrimaryKeyDashboardTable primaryKeyDashboardTable = new PrimaryKeyDashboardTable();
			if (i == 0) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Records on " + source1);
				primaryKeyDashboardTable.setMeasurement(keyMeasurementMatchingMap.get("totalRecordsInSource1"));
				primaryKeyDashboardTable.setStatusNRecordCount(keyMeasurementMatchingMap.get("totalRecordsInSource1"));
			} else if (i == 1) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Records on " + source2);
				primaryKeyDashboardTable.setMeasurement(keyMeasurementMatchingMap.get("totalRecordsInSource2"));
				primaryKeyDashboardTable.setStatusNRecordCount(keyMeasurementMatchingMap.get("totalRecordsInSource2"));
			} else if (i == 2) {
				primaryKeyDashboardTable.setKeyMetrics("Records in " + source1 + " not in " + source2);
				primaryKeyDashboardTable.setMeasurement(keyMeasurementMatchingMap.get("source1OnlyRecords"));
				primaryKeyDashboardTable.setStatusNRecordCount((String) getZeroStatusAbdCount[0]);
				primaryKeyDashboardTable.setPercentage(keyMeasurementMatchingMap.get("source1OnlyPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			} else if (i == 3) {
				primaryKeyDashboardTable.setKeyMetrics("Records in " + source2 + " not in " + source1);
				primaryKeyDashboardTable.setMeasurement(keyMeasurementMatchingMap.get("source2OnlyRecords"));
				primaryKeyDashboardTable.setStatusNRecordCount((String) getZeroStatusAbdCount[1]);
				primaryKeyDashboardTable.setPercentage(keyMeasurementMatchingMap.get("source2OnlyPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			} else if (i == 4) {
				primaryKeyDashboardTable.setKeyMetrics("Number of Unmatched Item");
				primaryKeyDashboardTable.setMeasurement(keyMeasurementMatchingMap.get("unmatchedRecords"));
				primaryKeyDashboardTable.setStatusNRecordCount(keyMeasurementMatchingMap.get("unmatchedStatus"));
				primaryKeyDashboardTable.setPercentage(keyMeasurementMatchingMap.get("unmatchedPercentage"));
				primaryKeyDashboardTable.setThreshold(threshold);
			}
			dashboardRecords.add(primaryKeyDashboardTable);
		}
		return dashboardRecords;
	}

	public List<HashMap<String, String>> getPaginatedDataTemplates(HashMap<String, String> oPaginationParms) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(listdatasourcedao.getPaginatedDataTemplateJsonData(oPaginationParms).toString(),
					new TypeReference<List<HashMap<String, String>>>() {
					});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	  @Autowired
	    private Properties appDbConnectionProperties;

	  @Autowired
		private LoginService loginService;
	  
	public List<ListAppGroup> getAppGroupsForProject(String token) {
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
		List<Project> projList = null;
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

		if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
			
			String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
			ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
			
			projList = loginService.getProjectListOfUser(userLdapGroups);
		} else {
			projList = projService.getAllProjectsOfAUser(userToken.getEmail());
		}
		
		long projectId = 0l;
		if(projList != null && projList.size() > 0)
			projectId = projList.get(0).getIdProject();
		
		if (taskService.checkUserPermission(userToken, "R", "Tasks")) {
			 List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId, projList);
			 return listAppGroupData;
		}else {
			return null;
		}
	}

}
