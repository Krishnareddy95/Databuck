package com.databuck.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.databuck.bean.RuleCatalog;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.DateUtility;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.databuck.bean.DashboardTableCount;
import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ProcessData;
import com.databuck.bean.listDataAccess;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.csvmodel.ValidationSummary;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dto.DashboardTableCountSummary;
import com.databuck.dto.DateVsDTSGraph;
import com.databuck.dto.TableListforSchema;
import java.text.DecimalFormat;

@Service
public class DashboardSummaryService {

	@Autowired
	private IResultsDAO resultsDAO;

	@Autowired
	private IValidationDAO validationDAO;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	String dateFilter = "";
	int runFilter = 0;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private DataConnectionService dataConnectionService;

	@Autowired
	private ITemplateViewDAO iTemplateViewDAO;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	private IDashboardConsoleDao iDashboardConsoleDao;

	private static final Logger LOG = Logger.getLogger(DashboardSummaryService.class);

	// returns a list of validation run details from data_quality_dashboard based on
	// date range and project details
	public List<DataQualityMasterDashboard> getValidationListSummary(JSONObject inputJson, int projectId, int domainId,
			String fromDate, String toDate) {

		String applicationIds = "";
		applicationIds = resultsDAO.getAppIdsListForDomainProject(domainId, projectId, fromDate, toDate);
		List<DataQualityMasterDashboard> masterDashboard = resultsDAO.getAppIdsListSummary(applicationIds, fromDate,
				toDate);
		// DateUtility.DebugLog("getValidationListSummary 01", String.format("%1$s,
		// %2$s", inputJson, masterDashboard));
		return masterDashboard;
	}

	public List<DataQualityMasterDashboard> getSingleValidation(String idApp, String fromDate, String toDate) {

		List<DataQualityMasterDashboard> masterDashboard = resultsDAO.getAppIdsListSummary(idApp, fromDate, toDate);

		return masterDashboard;
	}

	// returns a filtered list of validation run details from data_quality_dashboard
	// based on date range
	// and project details along with input parameters matching
	public List<DataQualityMasterDashboard> getValidationListSummaryByFilter(JSONObject inputJson, int projectId,
			int domainId, String fromDate, String toDate, String filterCondition, long idData) {
		String applicationIds = "";
		applicationIds = resultsDAO.getFilteredAppIdsListForDomainProject(domainId, projectId, idData, fromDate,
				toDate);
		List<DataQualityMasterDashboard> masterDashboard = resultsDAO.getAppIdsListSummaryByFilter(applicationIds,
				filterCondition, fromDate, toDate);
		// DateUtility.DebugLog("getValidationListSummary 01", String.format("%1$s,
		// %2$s", inputJson, masterDashboard));

		return masterDashboard;
	}

	public List<DataQualityMasterDashboard> getValidationListSummaryByFilter(JSONObject inputJson, int projectId,
			int domainId, String fromDate, String toDate, String filterCondition, long idData, String connectionName,
			String fileName) {
		String applicationIds = "";
		applicationIds = resultsDAO.getFilteredAppIdsListForDomainProject(domainId, projectId, idData, fromDate,
				toDate);
		List<DataQualityMasterDashboard> masterDashboard = resultsDAO.getValidationDetailsByFilter(applicationIds,
				filterCondition, fromDate, toDate, connectionName, fileName);
		// DateUtility.DebugLog("getValidationListSummary 01", String.format("%1$s,
		// %2$s", inputJson, masterDashboard));

		return masterDashboard;
	}

	// The following method creates a filter condition to get dqi result based on
	// input received
	public String getDQIFilterCondition(JSONObject filterAttribute) {
		String filterCondition = "";
		Set<String> keySet = filterAttribute.keySet();
		for (String filterColumn : keySet) {
			try {
				String filterValue = "" + filterAttribute.get(filterColumn);
				if (filterColumn.equals("recordCount")) {
					if (!filterValue.isEmpty())
						filterCondition = filterCondition + " LOWER(t3." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and";
				} else if (filterColumn.equals("testRun")) {
					if (!filterValue.isEmpty())
						filterCondition = filterCondition + " LOWER(t2." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and";
				} else if (filterColumn.equals("recordCountStatus") && filterValue.contains("N")) {
					if (!filterValue.isEmpty())
						filterCondition = filterCondition + getFieldMappings(filterColumn) + " is null and";
				} else {
					if (!filterValue.isEmpty())
						filterCondition = filterCondition + " LOWER(t1." + getFieldMappings(filterColumn) + ") LIKE '%"
								+ filterValue + "%' and";
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (filterCondition.length() > 0) {
			filterCondition = " and " + filterCondition.substring(0, filterCondition.lastIndexOf("and"));
		}
		LOG.debug("filter condition=" + filterCondition);
		return filterCondition;
	}

	private String getFieldMappings(String field) {
		switch (field) {
		case "source1":
			return "sourceName";
		case "testRun":
			return "test_run";
		case "aggreagteDQI":
			return "aggregateDQI";
		default:
			return field;
		}
	}

	// The following method makes validationDQISummary
	public JSONArray getValidationDQISummary(long idApp) {

		JSONArray validationDQISummaryArr = new JSONArray();
		Map<String, String> validationCheckTestNames = getListOfPossibleTestValues();

		Set<Map.Entry<String, String>> keySet = validationCheckTestNames.entrySet();

		Map<String, String> keyMetric1LableMap = getKeyMetric1LabelCheckNameMap();
		Map<String, String> keyMetric2LabelMap = getKeyMetric2LabelCheckNameMap();
		ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
		for (Map.Entry<String, String> entry : keySet) {
			double DQI = 0.0;
			String status = "failed";
			double Key_Matric_1 = 0l;
			double Key_Matric_2 = 0l;
			String Key_Matric_3 = "";
			JSONObject validationCheckObj = new JSONObject();
			String testName = entry.getKey();
			String checkName = entry.getValue();
			SqlRowSet dashboardDetails = null;
			if (testName != null && testName.equals("DQ_LengthCheck")) {
				if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")
						|| listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
					double dqi_val = 0.0, metric1_val = 0.0, metric2_val = 0.0;
					int avgFactor = 0;
					if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {
						dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, "DQ_LengthCheck",
								listApplicationsData);
						if (dashboardDetails != null) {
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
					if (listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
						dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, "DQ_MaxLengthCheck",
								listApplicationsData);
						String Key_Matric_3_max = "";
						if (dashboardDetails != null) {
							while (dashboardDetails.next()) {
								dqi_val = dqi_val + dashboardDetails.getDouble(4);
								if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("passed"))
									status = dashboardDetails.getString(5);
								metric1_val = metric1_val + dashboardDetails.getDouble(6);
								metric2_val = metric2_val + dashboardDetails.getDouble(7);
								Key_Matric_3_max = Key_Matric_3_max + dashboardDetails.getString(8);
							}
							avgFactor++;
						}
						if (!Key_Matric_3_max.equals(""))
							Key_Matric_3 += "," + Key_Matric_3_max;
					}
					DQI = dqi_val / avgFactor;
					Key_Matric_1 = metric1_val;
					Key_Matric_2 = metric2_val;
					validationCheckObj.put("DQI", DQI);
					validationCheckObj.put("Status", status);
					validationCheckObj.put("Key_Metric_1", Key_Matric_1);
					validationCheckObj.put("Key_Metric_2", Key_Matric_2);
					validationCheckObj.put("Key_Metric_3", Key_Matric_3);
					validationCheckObj.put("test", "Length Check (Conformity)");
					validationCheckObj.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));
					String keyMetric2Label = "";
					try {
						keyMetric2Label = keyMetric2LabelMap.get(checkName);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
						validationCheckObj.put("Key_Metric_2_Label", keyMetric2Label);
					else
						validationCheckObj.put("Key_Metric_2_Label", "");
				}

			}
//			else if (testName != null && testName.equals("DQ_DefaultCheck")) {
//				if (listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {
//					dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, testName, null);
//					while (dashboardDetails.next()) {
//						DQI = dashboardDetails.getDouble("DQI");
//						status = dashboardDetails.getString("Status");
//						if (status != null && !status.equals("")) {
//							status = status.toLowerCase();
//						} else {
//							status = "failed";
//						}
//						Key_Matric_1 = dashboardDetails.getDouble("Key_Metric_1");
//						Key_Matric_2 = dashboardDetails.getDouble("Key_Metric_2");
//						Key_Matric_3 = dashboardDetails.getString("Key_Metric_3");
//						if (Key_Matric_3 == null) {
//							Key_Matric_3 = "";
//						}
//						validationCheckObj.put("DQI", DQI);
//						validationCheckObj.put("Status", status);
//						validationCheckObj.put("Key_Metric_1", Key_Matric_1);
//						validationCheckObj.put("Key_Metric_2", Key_Matric_2);
//						validationCheckObj.put("Key_Metric_3", Key_Matric_3);
//						validationCheckObj.put("test", checkName);
//						validationCheckObj.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));
//						String keyMetric2Label = "";
//						try {
//							keyMetric2Label = keyMetric2LabelMap.get(checkName);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
//							validationCheckObj.put("Key_Metric_2_Label", keyMetric2Label);
//						else
//							validationCheckObj.put("Key_Metric_2_Label", "");
//					}
//				}
//			} else if (testName != null && testName.equals("DQ_Data Drift")) {
//				if (listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")) {
//					dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, testName, null);
//					while (dashboardDetails.next()) {
//						int run = dashboardDetails.getInt("Run");
//						DQI = dashboardDetails.getDouble("DQI");
//						status = dashboardDetails.getString("Status");
//						if (status != null && !status.equals("")) {
//							status = status.toLowerCase();
//						} else {
//							status = "failed";
//						}
//						if (run < 2) {
//							DQI = 0.0;
//							status = "";
//						}
//						Key_Matric_1 = dashboardDetails.getDouble("Key_Metric_1");
//						Key_Matric_2 = dashboardDetails.getDouble("Key_Metric_2");
//						Key_Matric_3 = dashboardDetails.getString("Key_Metric_3");
//						if (Key_Matric_3 == null) {
//							Key_Matric_3 = "";
//						}
//						validationCheckObj.put("DQI", DQI);
//						validationCheckObj.put("Status", status);
//						validationCheckObj.put("Key_Metric_1", Key_Matric_1);
//						validationCheckObj.put("Key_Metric_2", Key_Matric_2);
//						validationCheckObj.put("Key_Metric_3", Key_Matric_3);
//						validationCheckObj.put("test", checkName);
//						validationCheckObj.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));
//						String keyMetric2Label = "";
//						try {
//							keyMetric2Label = keyMetric2LabelMap.get(checkName);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
//							validationCheckObj.put("Key_Metric_2_Label", keyMetric2Label);
//						else
//							validationCheckObj.put("Key_Metric_2_Label", "");
//					}
//				}
//			}
			else {
				dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, testName, null);
				while (dashboardDetails.next()) {
					int run = dashboardDetails.getInt("Run");
					DQI = dashboardDetails.getDouble("DQI");
					status = dashboardDetails.getString("Status");
					if (status != null && !status.equals("")) {
						status = status.toLowerCase();
					} else {
						status = "failed";
					}
					if (testName.equalsIgnoreCase("DQ_Numerical Field Fingerprint")
							&& listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y") && run < 2) {
						DQI = 0.0;
						status = "";
					}
					if (testName.equalsIgnoreCase("DQ_Data Drift") && run < 2) {
						DQI = 0.0;
						status = "";
					}
					Key_Matric_1 = dashboardDetails.getDouble("Key_Metric_1");
					Key_Matric_2 = dashboardDetails.getDouble("Key_Metric_2");
					Key_Matric_3 = dashboardDetails.getString("Key_Metric_3");
					if (Key_Matric_3 == null) {
						Key_Matric_3 = "";
					}
					validationCheckObj.put("DQI", DQI);
					validationCheckObj.put("Status", status);
					validationCheckObj.put("Key_Metric_1", Key_Matric_1);
					validationCheckObj.put("Key_Metric_2", Key_Matric_2);
					validationCheckObj.put("Key_Metric_3", Key_Matric_3);
					validationCheckObj.put("test", checkName);
					validationCheckObj.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));

					String keyMetric2Label = "";
					try {
						keyMetric2Label = keyMetric2LabelMap.get(checkName);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
						validationCheckObj.put("Key_Metric_2_Label", keyMetric2Label);
					else
						validationCheckObj.put("Key_Metric_2_Label", "");

					if (testName.equals("DQ_Sql_Rule")) {
						LinkedHashMap<String, String> oSqlRulesSummary = resultsDAO
								.getSqlRulesDashboardSummary(String.valueOf(idApp));
						validationCheckObj.put("Key_Metric_1", oSqlRulesSummary.get("SqlRuleCount"));
						validationCheckObj.put("Key_Metric_2", oSqlRulesSummary.get("SqlRuleFailed"));
						validationCheckObj.put("DQI", oSqlRulesSummary.get("SqlRuleDqi"));
					}
				}
			}
			if (validationCheckObj.length() > 0)
				validationDQISummaryArr.put(validationCheckObj);
		}

		return validationDQISummaryArr;
	}

	public List<ValidationSummary> getValidationDQISummaryCSV(long idApp) {

		List<ValidationSummary> validationDQISummarys = new ArrayList<>();
		Map<String, String> validationCheckTestNames = getListOfPossibleTestValues();

		Set<Map.Entry<String, String>> keySet = validationCheckTestNames.entrySet();

		Map<String, String> keyMetric1LableMap = getKeyMetric1LabelCheckNameMap();
		Map<String, String> keyMetric2LabelMap = getKeyMetric2LabelCheckNameMap();
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		for (Map.Entry<String, String> entry : keySet) {
			double DQI = 0.0;
			String status = "failed";
			double Key_Matric_1 = 0l;
			double Key_Matric_2 = 0l;
			String Key_Matric_3 = "";
			ValidationSummary validationCheckObj = new ValidationSummary();
			String testName = entry.getKey();
			String checkName = entry.getValue();
			SqlRowSet dashboardDetails = null;
			if (testName != null && testName.equals("DQ_LengthCheck")) {
				ListApplications listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
				if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")
						|| listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
					double dqi_val = 0.0, metric1_val = 0.0, metric2_val = 0.0;
					int avgFactor = 0;
					if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {
						dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, "DQ_LengthCheck",
								listApplicationsData);
						if (dashboardDetails != null) {
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
					if (listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
						dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, "DQ_MaxLengthCheck",
								listApplicationsData);
						String Key_Matric_3_max = "";
						if (dashboardDetails != null) {
							while (dashboardDetails.next()) {
								dqi_val = dqi_val + dashboardDetails.getDouble(4);
								if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("passed"))
									status = dashboardDetails.getString(5);
								metric1_val = metric1_val + dashboardDetails.getDouble(6);
								metric2_val = metric2_val + dashboardDetails.getDouble(7);
								Key_Matric_3_max = Key_Matric_3_max + dashboardDetails.getString(8);
							}
							avgFactor++;
						}
						if (!Key_Matric_3_max.equals(""))
							Key_Matric_3 += "," + Key_Matric_3_max;
					}
					DQI = dqi_val / avgFactor;
					Key_Matric_1 = metric1_val;
					Key_Matric_2 = metric2_val;
					validationCheckObj.setDqi(String.valueOf(DQI));
					validationCheckObj.setStatus(status);
					validationCheckObj.setKeyMetric1(String.valueOf(Key_Matric_1));
					validationCheckObj.setKeyMetric2(String.valueOf(Key_Matric_2));
					validationCheckObj.setKeyMetric3(Key_Matric_3);
					validationCheckObj.setTest(checkName);
					validationCheckObj.setKeyMetric1Label(keyMetric1LableMap.get(checkName));
					String keyMetric2Label = "";
					try {
						keyMetric2Label = keyMetric2LabelMap.get(checkName);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
						validationCheckObj.setKeyMetric2Label(keyMetric2Label);
					else
						validationCheckObj.setKeyMetric2Label("");
				}

			} else {
				dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, testName, null);
				while (dashboardDetails.next()) {
					int run = dashboardDetails.getInt("Run");
					DQI = dashboardDetails.getDouble("DQI");
					status = dashboardDetails.getString("Status");
					if (status != null && !status.equals("")) {
						status = status.toLowerCase();
					} else {
						status = "failed";
					}
					if (testName.equalsIgnoreCase("DQ_Data Drift") && run < 2) {
						DQI = 0.0;
						status = "";
					} else if (testName.equalsIgnoreCase("DQ_Numerical Field Fingerprint")) {
						int maxRunCount = getMaxRun("DATA_QUALITY_Column_Summary", idApp);
						if (maxRunCount < 3) {
							DQI = 0.0;
							status = "";
						}
					}
					Key_Matric_1 = dashboardDetails.getDouble("Key_Metric_1");
					Key_Matric_2 = dashboardDetails.getDouble("Key_Metric_2");
					Key_Matric_3 = dashboardDetails.getString("Key_Metric_3");
					if (Key_Matric_3 == null) {
						Key_Matric_3 = "";
					}
					validationCheckObj.setDqi(String.valueOf(DQI));
					validationCheckObj.setStatus(status);
					validationCheckObj.setKeyMetric1(String.valueOf(Key_Matric_1));
					validationCheckObj.setKeyMetric2(String.valueOf(Key_Matric_2));
					validationCheckObj.setKeyMetric3(Key_Matric_3);
					validationCheckObj.setTest(checkName);
					validationCheckObj.setKeyMetric1Label(keyMetric1LableMap.get(checkName));
					String keyMetric2Label = "";
					try {
						keyMetric2Label = keyMetric2LabelMap.get(checkName);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
					if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
						validationCheckObj.setKeyMetric2Label(keyMetric2Label);
					else
						validationCheckObj.setKeyMetric2Label("");
					if (testName.equals("DQ_Sql_Rule")) {
						LinkedHashMap<String, String> oSqlRulesSummary = resultsDAO
								.getSqlRulesDashboardSummary(String.valueOf(idApp));
						validationCheckObj.setKeyMetric1(oSqlRulesSummary.get("SqlRuleCount"));
						validationCheckObj.setKeyMetric2(oSqlRulesSummary.get("SqlRuleFailed"));
						validationCheckObj.setDqi("" + oSqlRulesSummary.get("SqlRuleDqi"));
					}
				}
			}
			if (validationCheckObj != null && validationCheckObj.getTest() != null
					&& !validationCheckObj.getTest().isEmpty()) {
				validationCheckObj.setDqi(decimalFormat.format(Double.valueOf(validationCheckObj.getDqi())));
				validationDQISummarys.add(validationCheckObj);
			}
		}

		return validationDQISummarys;
	}

	private int getMaxRun(String tableName, long idApp) {
		String sql = "select count(Run) as runCount from " + tableName + " where idApp=" + idApp;
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		while (queryForRowSet.next()) {
			int runCount = queryForRowSet.getInt("runCount");
			return runCount;
		}
		return 0;
	}

	public Map<String, String> getListOfPossibleTestValues() {
		Map<String, String> validationCheckTestNames = new LinkedHashMap<>();
		// validationCheckTestNames.put(null, "");
		validationCheckTestNames.put("DQ_Record Count Fingerprint", "Record Count");
		validationCheckTestNames.put("DQ_Completeness", "Null (Completeness)");
		validationCheckTestNames.put("DQ_LengthCheck", "Length Check (Conformity)");
		validationCheckTestNames.put("DQ_Pattern_Data", "Regex Pattern (Conformity)");
		validationCheckTestNames.put("DQ_Default_Pattern_Data", "Default Pattern Check (Conformity)");
		validationCheckTestNames.put("DQ_Uniqueness -Primary Keys", "Primary Uniqueness");
		validationCheckTestNames.put("DQ_Uniqueness -Seleted Fields", "Custom Uniqueness");
		validationCheckTestNames.put("DQ_Bad_Data", "Data Type (Conformity)");
		validationCheckTestNames.put("DQ_DefaultCheck", "Default Check");
		validationCheckTestNames.put("DQ_DateRuleCheck", "Date Consistency");
		validationCheckTestNames.put("DQ_Data Drift", "Data Drift");
		validationCheckTestNames.put("DQ_Record Anomaly", "Value Anomaly");
		validationCheckTestNames.put("DQ_Numerical Field Fingerprint", "Distribution Check");
		validationCheckTestNames.put("DQ_Timeliness", "Time Sequence");
		validationCheckTestNames.put("DQ_Sql_Rule", "SQL Rules");
		validationCheckTestNames.put("DQ_Rules", "Custom Rules");
		validationCheckTestNames.put("DQ_GlobalRules", "Custom Rules");
		return validationCheckTestNames;
	}

	public Map<String, String> getKeyMetric2LabelCheckNameMap() {
		Map<String, String> keyMetric2LabelMap = new HashMap<>();
		keyMetric2LabelMap.put(null, "");
		keyMetric2LabelMap.put("", "");
		keyMetric2LabelMap.put("Record Count", "");
		keyMetric2LabelMap.put("Length Check (Conformity)", "Number of Records Failed");
		keyMetric2LabelMap.put("Null (Completeness)", "Number of Nulls Identified");
		keyMetric2LabelMap.put("Primary Uniqueness", "Total Number of Duplicate Records");
		keyMetric2LabelMap.put("Custom Uniqueness", "Total Number of Duplicate Records");
		keyMetric2LabelMap.put("Distribution Check", "Number of Columns Failed");
		keyMetric2LabelMap.put("Value Anomaly", "Number of Records Failed");
		keyMetric2LabelMap.put("Data Type (Conformity)", "Number of Bad Data");
		keyMetric2LabelMap.put("Regex Pattern (Conformity)", "Number of Records Failed");
		keyMetric2LabelMap.put("Date Consistency", "Number of Records Failed");
		keyMetric2LabelMap.put("Time Sequence", "Number of Records Failed");
		keyMetric2LabelMap.put("Custom Rules", "Total No. of Rules Failed");
		keyMetric2LabelMap.put("Global Rules", "Total No. of Rules Failed");
		keyMetric2LabelMap.put("SQL Rules", "Total No. of Records Failed");
		keyMetric2LabelMap.put("Data Drift", "Number of Unique Value Changed");
		keyMetric2LabelMap.put("Default Check", "Number of Columns Failed");
		keyMetric2LabelMap.put("Default Pattern Check (Conformity)", "No of failed Record");
		return keyMetric2LabelMap;
	}

	public Map<String, String> getKeyMetric1LabelCheckNameMap() {
		Map<String, String> keyMetric2LabelMap = new HashMap<>();
		keyMetric2LabelMap.put(null, "");
		keyMetric2LabelMap.put("", "");
		keyMetric2LabelMap.put("Record Count", "Record Count");
		keyMetric2LabelMap.put("Length Check (Conformity)", "Number of Columns Tested");
		keyMetric2LabelMap.put("Null (Completeness)", "Number of Columns Tested");
		keyMetric2LabelMap.put("Primary Uniqueness", "Total Number of Primary Keys");
		keyMetric2LabelMap.put("Custom Uniqueness", "Total Number of Primary Keys");
		keyMetric2LabelMap.put("Distribution Check", "Number of Columns Tested");
		keyMetric2LabelMap.put("Value Anomaly", "Number of Columns Tested");
		keyMetric2LabelMap.put("Data Type (Conformity)", "Number of Columns Tested");
		keyMetric2LabelMap.put("Regex Pattern (Conformity)", "Number of Columns Tested");
		keyMetric2LabelMap.put("Date Consistency", "Number of Columns Tested");
		keyMetric2LabelMap.put("Time Sequence", "Number of Columns Tested");
		keyMetric2LabelMap.put("Custom Rules", "Total No. of Rules Processed");
		keyMetric2LabelMap.put("Global Rules", "Total No. of Rules Processed");
		keyMetric2LabelMap.put("SQL Rules", "Total No. of Records Processed");
		keyMetric2LabelMap.put("Data Drift", "No of changed Values");
		keyMetric2LabelMap.put("Default Check", "Number of Columns Tested");
		keyMetric2LabelMap.put("Default Pattern Check (Conformity)", "Number of Columns Tested");
		return keyMetric2LabelMap;
	}

	// The following method return results for essential checks with or without
	// filter parameter
	public JSONObject getStatsForValidationCheck(JSONObject requestObj) {
		long idApp = 0;
		String fromDate = "";
		String toDate = "";
		String checkName = "";
		String colName = "NoColumnName";
		JSONObject columnValues = null;
		JSONObject filterAttributeJson = null;
		try {
			idApp = requestObj.getLong("idApp");
			fromDate = requestObj.getString("fromDate");
			toDate = requestObj.getString("toDate");
			checkName = requestObj.getString("checkName");
			try {
				colName = requestObj.getString("colName");
			} catch (JSONException je) {
				LOG.error(je.getMessage());

			}
			filterAttributeJson = requestObj.getJSONObject("filterAttribute");

			columnValues = requestObj.getJSONObject("columnValues");

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject essentialChkResultsObj = new JSONObject();

		JSONObject checkTablesList = getTableNamesByCheck(checkName);
		JSONArray myListTable = new JSONArray();

		if (checkTablesList.length() > 0)
			myListTable = (JSONArray) checkTablesList.get(checkName);

		for (int i = 0; i < myListTable.length(); i++) {
			JSONArray tableResultArr = null;
			String tableName = (String) myListTable.get(i);
			String column = getColumnNameByTable(tableName);
			if (tableName != null && !tableName.isEmpty()) {

				String dGroupCondition = "";
//				if (checkName != null && !checkName.isEmpty()) {
//					if (checkName.contains("micro"))
//						dGroupCondition = " and dGroupCol IS NOT NULL and dGroupCol!='' ";
//				}

				if (checkName != null && !checkName.isEmpty()) {
					if (checkName.equals("micro_rca_check"))
						dGroupCondition = " and Validity = 'True' ";
				}

				LOG.debug("\n====>processing for table:" + tableName);

				if (tableName.equalsIgnoreCase(".")) {
					if (filterAttributeJson != null && filterAttributeJson.length() > 0) {

						LOG.info("\n====>Processing with filterAttribute values");

						tableResultArr = validationDAO.getFilteredValidationResultsByCheck(idApp, fromDate, toDate,
								tableName, filterAttributeJson, dGroupCondition);
						essentialChkResultsObj.put(tableName, tableResultArr);
					} else {
						LOG.info("\n====>Processing without filterAttribute values");
						List<ProcessData> processDataList = resultsDAO.getProcessDataTableDetails(idApp, fromDate,
								toDate);
						essentialChkResultsObj.put("processData", processDataList);
					}
					continue;
				}
				if (filterAttributeJson != null && filterAttributeJson.length() > 0) {

					LOG.info("\n====>Processing with filterAttribute values");

					if ("NoColumnName".equalsIgnoreCase(column))
						tableResultArr = validationDAO.getFilteredValidationResultsByCheck(idApp, fromDate, toDate,
								tableName, filterAttributeJson, dGroupCondition);
				} else {

					LOG.info("\n====>Processing without filterAttribute values");

					if ("NoColumnName".equalsIgnoreCase(column))
						tableResultArr = validationDAO.getValidationResultsByCheck(idApp, fromDate, toDate, tableName,
								dGroupCondition);
					else {
						tableResultArr = validationDAO.getValidationResultsByCheckWithColName(idApp, fromDate, toDate,
								tableName, dGroupCondition, colName, column, checkName, columnValues);
					}
				}
			}

			if (tableResultArr == null || tableResultArr.length() <= 0) {
				tableResultArr = new JSONArray();
			}
			essentialChkResultsObj.put(tableName, tableResultArr);
			if (checkName.equalsIgnoreCase("data_drift_check")) {
				String isFirstRun = "N";
				int firstRunCount = validationDAO.getFirstRunCount(idApp);
				if (firstRunCount == 1) {
					isFirstRun = "Y";
				}
				essentialChkResultsObj.put("isFirstRun", isFirstRun);
			}
		}

		return essentialChkResultsObj;
	}

	public JSONObject getStatsForValidationCheckNew(JSONObject requestObj) {
		long idApp = 0;
		String fromDate = "";
		String toDate = "";
		String checkName = "";
		String colName = "NoColumnName";
		JSONObject columnValues = null;
		JSONObject filterAttributeJson = null;
		boolean isInitialCall = true;
		boolean isCountRequired = false;
		int offset = 0;
		int records = 50;
		int count = 0;
		try {
			idApp = requestObj.getLong("idApp");
			fromDate = requestObj.getString("fromDate");
			toDate = requestObj.getString("toDate");
			checkName = requestObj.getString("checkName");
			offset = requestObj.getInt("pageNo");
			records = requestObj.getInt("noOfRecords");
			isInitialCall = requestObj.getBoolean("isInitialCall");
			try {
				colName = requestObj.getString("colName");
			} catch (JSONException je) {
				LOG.error(je.getMessage());
			}
			if (requestObj.has("isCountRequired")) {
				isCountRequired = requestObj.getBoolean("isCountRequired");
			}
			filterAttributeJson = requestObj.getJSONObject("filterAttribute");

			columnValues = requestObj.getJSONObject("columnValues");

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject essentialChkResultsObj = new JSONObject();

		JSONObject checkTablesList = getTableNamesByCheck(checkName);
		JSONArray myListTable = new JSONArray();

		if (checkTablesList.length() > 0)
			myListTable = (JSONArray) checkTablesList.get(checkName);

		for (int i = 0; i < myListTable.length(); i++) {
			JSONArray tableResultArr = null;
			String tableName = (String) myListTable.get(i);
			String column = getColumnNameByTable(tableName);
			if (tableName != null && !tableName.isEmpty()) {
				String dGroupCondition = "";
				if (checkName != null && !checkName.isEmpty()) {
					if (checkName.equals("micro_rca_check"))
						dGroupCondition = " and Validity = 'True' ";
				}
				LOG.debug("\n====>processing for table:" + tableName);
				if (tableName.equalsIgnoreCase("processData")) {
					if (filterAttributeJson != null && filterAttributeJson.length() > 0) {
						LOG.info("\n====>Processing with filterAttribute values");
						tableResultArr = validationDAO.getFilteredValidationResultsByCheck(idApp, fromDate, toDate,
								tableName, filterAttributeJson, dGroupCondition);
						essentialChkResultsObj.put(tableName, tableResultArr);
					} else {
						LOG.info("\n====>Processing without filterAttribute values");
						List<ProcessData> processDataList = resultsDAO.getProcessDataTableDetails(idApp, fromDate,
								toDate);
						essentialChkResultsObj.put("processData", processDataList);
					}
					continue;
				}
				if (isInitialCall) {
					tableResultArr = validationDAO.getValidationResultsByCheckInitialRecords(idApp, fromDate, toDate,
							tableName);
				} else {
					if (isCountRequired) {
						count = validationDAO.getResultCount(idApp, fromDate, toDate, tableName, dGroupCondition,
								checkName);
					}
					tableResultArr = validationDAO.getValidationResultsByCheckWithColNameNew(idApp, fromDate, toDate,
							tableName, dGroupCondition, colName, column, checkName, columnValues, offset, records);
				}
			}

			if (tableResultArr == null || tableResultArr.length() <= 0) {
				tableResultArr = new JSONArray();
			}
			if (count > 0) {
				essentialChkResultsObj.put("count", count);
			}
			essentialChkResultsObj.put(tableName, tableResultArr);
			if (checkName.equalsIgnoreCase("data_drift_check")) {
				String isFirstRun = "N";
				int firstRunCount = validationDAO.getFirstRunCount(idApp);
				if (firstRunCount == 1) {
					isFirstRun = "Y";
				}
				essentialChkResultsObj.put("isFirstRun", isFirstRun);
			}
		}
		return essentialChkResultsObj;
	}

	public JSONObject getStatsForDataDrift(JSONObject requestObj) {
		long idApp = 0;
		String fromDate = "";
		String toDate = "";
		String colName = "";
		JSONObject columnValues = null;
		try {
			idApp = requestObj.getLong("idApp");
			fromDate = requestObj.getString("fromDate");
			toDate = requestObj.getString("toDate");
			try {
				colName = requestObj.getString("colName");
			} catch (JSONException je) {
				LOG.error(je.getMessage());
			}
			columnValues = requestObj.getJSONObject("columnValues");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject essentialChkResultsObj = new JSONObject();
		JSONArray tableResultArr = null;
		tableResultArr = validationDAO.getValidationResultsForDataDrift(idApp, fromDate, toDate, colName, columnValues);
		if (tableResultArr == null || tableResultArr.length() <= 0) {
			tableResultArr = new JSONArray();
		}
		essentialChkResultsObj.put("DATA_QUALITY_DATA_DRIFT_SUMMARY", tableResultArr);
		return essentialChkResultsObj;

	}

	private String getColumnNameByTable(String tableName) {
		switch (tableName) {
		case "DATA_QUALITY_NullCheck_Summary":
			return "ColName";
		case "DATA_QUALITY_Column_Summary":
			return "ColName";
		case "DATA_QUALITY_Custom_Column_Summary":
			return "ColName";
		case "DATA_QUALITY_Length_Check":
			return "ColName";
		case "DATA_QUALITY_Unmatched_Pattern_Data":
			return "Col_Name";
		case "DATA_QUALITY_badData":
			return "ColName";
		case "DATA_QUALITY_default_value":
			return "ColName";
		case "data_quality_sql_rules":
			return "ruleName";
		case "DATA_QUALITY_Rules":
			return "ruleName";
		case "DATA_QUALITY_GlobalRules":
			return "ruleName";
		case "DATA_QUALITY_DATA_DRIFT":
			return "colName";
		case "DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY":
			return "colName";
		case "DATA_QUALITY_Record_Anomaly":
			return "colName";
//		case "DATA_QUALITY_DATA_DRIFT_SUMMARY":
//			return "colName";
		case "DATA_QUALITY_Unmatched_Default_Pattern_Data":
			return "Col_Name";
		case "DATA_QUALITY_Transactionset_sum_A1":
			return "RecordCount";
		case "DATA_QUALITY_Transactionset_sum_dgroup":
			return "RecordCount";
		case "DATA_QUALITY_Duplicate_Check_Summary":
			return "duplicateCheckFields";
		case "DATA_QUALITY_DateRule_Summary":
			return "DateField";
		default:
			LOG.info("\n====>:No Table entry for result column so continuing with NoColumnName");
			return "NoColumnName";
		}
	}

	public JSONObject getStatsForValidationCheckByFilter(JSONObject requestObj) {
		long idApp = 0;
		String fromDate = "";
		String toDate = "";
		String checkName = "";
		JSONObject filterAttributeJson = null;
		String table = "";
		try {
			idApp = requestObj.getLong("idApp");
			fromDate = requestObj.getString("fromDate");
			toDate = requestObj.getString("toDate");
			checkName = requestObj.getString("checkName");
			table = requestObj.getString("tableName");
			filterAttributeJson = requestObj.getJSONObject("filterAttribute");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject essentialChkResultsObj = new JSONObject();
		JSONArray tableResultArr = null;
		String dGroupCondition = "";
		if (checkName != null && !checkName.isEmpty()) {
			if (checkName.contains("micro_null_check"))
				dGroupCondition = " and Status in ('passed','failed','PASSED','FAILED','Passed','Failed') ";
		}
		tableResultArr = validationDAO.getFilteredValidationResultsByCheck(idApp, fromDate, toDate, table,
				filterAttributeJson, dGroupCondition);
		essentialChkResultsObj.put(table, tableResultArr);
		return essentialChkResultsObj;
	}

	public JSONObject getTableNamesByCheck(String checkName) {
		JSONObject checkTablesObj = new JSONObject();
		JSONArray myListTable = new JSONArray();

		if (checkName.equalsIgnoreCase("duplicate_check")) {
			// myListTable.put("DATA_QUALITY_Transaction_Summary");
			myListTable.put("DATA_QUALITY_Transaction_Detail_All");
			myListTable.put("DATA_QUALITY_Transaction_Detail_Identity");
			myListTable.put("DATA_QUALITY_Duplicate_Check_Summary");
		} else if (checkName.equalsIgnoreCase("stringduplicate_check")) {
			myListTable.put("DATA_QUALITY_DATA_DRIFT");
		} else if (checkName.equalsIgnoreCase("default_pattern_check")) {
			myListTable.put("DATA_QUALITY_Unmatched_Default_Pattern_Data");
		} else if (checkName.equalsIgnoreCase("null_check")) {
			myListTable.put("DATA_QUALITY_NullCheck_Summary");

		} else if (checkName.equalsIgnoreCase("bad_data_check")) {
			myListTable.put("DATA_QUALITY_badData");

		} else if (checkName.equalsIgnoreCase("default_check")) {
			myListTable.put("DATA_QUALITY_default_value");

		} else if (checkName.equalsIgnoreCase("length_check")) {

			myListTable.put("DATA_QUALITY_Length_Check");

		} else if (checkName.equalsIgnoreCase("pattern_check")) {
			myListTable.put("DATA_QUALITY_Unmatched_Pattern_Data");

		} else if (checkName.equalsIgnoreCase("custom_rule_check")) {
			myListTable.put("DATA_QUALITY_Rules");

		} else if (checkName.equalsIgnoreCase("micro_rca_check")) {
			// myListTable.put("DATA_QUALITY_Transactionset_sum_A1");
			myListTable.put("DATA_QUALITY_Transactionset_sum_dgroup");

		} else if (checkName.equalsIgnoreCase("micro_null_check")) {
			// myListTable.put("DATA_QUALITY_NullCheck_Summary");
			myListTable.put("DATA_QUALITY_Column_Summary");

		} else if (checkName.equalsIgnoreCase("micro_date_rule_check")) {
			// myListTable.put("DATA_QUALITY_DateRule_Summary");
			myListTable.put("DATA_QUALITY_DateRule_FailedRecords");
		}

		// Adding advance checks also
		else if (checkName.equalsIgnoreCase("rca_check")) {
			myListTable.put("DATA_QUALITY_Transactionset_sum_A1");
			myListTable.put("processData");
			myListTable.put("DATA_QUALITY_Transactionset_sum_dgroup");
		} else if (checkName.equalsIgnoreCase("distribution_check")) {
			myListTable.put("DATA_QUALITY_Column_Summary");
		} else if (checkName.equalsIgnoreCase("record_anomaly_check")) {
			myListTable.put("DATA_QUALITY_Record_Anomaly");
			myListTable.put("DATA_QUALITY_History_Anomaly");
		} else if (checkName.equalsIgnoreCase("date_anomaly_check")) {
			myListTable.put("DATA_QUALITY_DateRule_Summary");
			// myListTable.put("DATA_QUALITY_DateRule_FailedRecords");
		} else if (checkName.equalsIgnoreCase("sequence_check")) {
			myListTable.put("DATA_QUALITY_timeliness_check");
		} else if (checkName.equalsIgnoreCase("data_drift_check")) {
			myListTable.put("DATA_QUALITY_DATA_DRIFT_SUMMARY");
			myListTable.put("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY");
		} else if (checkName.equalsIgnoreCase("count_reasonability")) {
			myListTable.put("DATA_QUALITY_Transactionset_sum_A1");
			myListTable.put("DATA_QUALITY_Transactionset_sum_dgroup");
			myListTable.put("processData");
		} else if (checkName.equalsIgnoreCase("sql_rules")) {
			myListTable.put("data_quality_sql_rules");
		} else if (checkName.equalsIgnoreCase("custom_rules")) {
			myListTable.put("DATA_QUALITY_Rules");
		} else if (checkName.equalsIgnoreCase("global_rules")) {
			myListTable.put("DATA_QUALITY_GlobalRules");
		} else if (checkName.equalsIgnoreCase("custom_distribution_check")) {
			myListTable.put("DATA_QUALITY_Custom_Column_Summary");
		} else {
			LOG.debug("\n====>No Table entry for the check:" + checkName);
		}

		if (myListTable.length() > 0) {
			checkTablesObj.put(checkName, myListTable);
		}

		return checkTablesObj;
	}

	public Map<String, Object> getcountReasionabilitySumDgroupClusterMapJsonObject(String tableName, int idApp)
			throws SQLException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String str_dGroupVal_SqlQuery = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp;
			SqlRowSet sqlRowSet_str_dGroupVal_SqlQuery = jdbcTemplate1.queryForRowSet(str_dGroupVal_SqlQuery);
			List<String> listTable_dGroupVal = new ArrayList<String>();
			while (sqlRowSet_str_dGroupVal_SqlQuery.next()) {
				listTable_dGroupVal
						.add("'" + sqlRowSet_str_dGroupVal_SqlQuery.getString("dGroupVal").replace("'", "\\\'") + "'");
			}
			Set<String> set = new HashSet<>(listTable_dGroupVal);
			listTable_dGroupVal.clear();
			listTable_dGroupVal.addAll(set);
			List<Map<String, Object>> listOfChartElements = new ArrayList<Map<String, Object>>();
			// for (String localstring : listTable_dGroupVal) {
			String strSqlQueryGetChartList = "SELECT MAX(RecordCount) RecordCount, dGroupVal FROM " + tableName
					+ " WHERE idApp=" + idApp + " and dGroupVal in ("
					+ listTable_dGroupVal.toString().replace("[", "").replace("]", "")
					+ ") group by dGroupVal order by dGroupVal ";
			SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(strSqlQueryGetChartList);
			while (sqlRowSet_strSqlQueryGetChartList.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", sqlRowSet_strSqlQueryGetChartList.getString("dGroupVal"));
				map.put("count", sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
				listOfChartElements.add(map);
			}
			// }
			result.put("children", listOfChartElements);
		} catch (Exception e) {
			LOG.error("getLineChartDetails : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> getAvgTrendChartData(String tableName, String DGroupVal, String colName, int idApp,
			String toDate) {
		try {
			LOG.debug("DataQualityResultsController : getAvgTrendChartData tableName :: " + tableName
					+ " | DGroupVal :: " + DGroupVal + " | colName :: " + colName);
			List<Map<String, Object>> listOfListAvgTrend = new ArrayList<Map<String, Object>>();
			List<Integer> listRun = new ArrayList<Integer>();
			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp, toDate);
			LOG.debug("DataQualityResultsController : getAvgTrendChartData listDate :: " + listDate);
			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);
			LOG.debug("DataQualityResultsController : getAvgTrendChartData listRun :: " + listRun);

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {
				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						LOG.debug(
								"Dashboard service : getAvgTrendChartData checkColumnSummaryRecordsExistsOrNotWithRunAndDate : tableName :: "
										+ tableName + " | date :: " + date + " | integer :: " + integer);
						StringBuffer strBufQuery = new StringBuffer();
						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumMeanAvg, 0) AS NumMeanAvg , "
								+ ifnull_function + "(dqcs.NumMeanThreshold , 0) AS NumMeanThreshold   , "
								+ ifnull_function + " (dqcs.NumMeanStdDev, 0) AS NumMeanStdDev , " + ifnull_function
								+ "(dqcs.Mean, 0) AS Mean , MAX(dqcs.Run) AS run, dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
						strBufQuery.append(" dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);
						LOG.debug(
								"DataQualityResultsController : getAvgTrendChartData : strBufQuery :: " + strBufQuery);
						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");

							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								double doubleNumMeanavg = sqlRowSet_strDateSqlQuery.getDouble("NumMeanAvg");
								double doubleNumMeanThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumMeanThreshold");
								double doubleNumMeanStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumMeanStdDev");
								double doubleMean = sqlRowSet_strDateSqlQuery.getDouble("Mean");
								Date dbSqlDateConverted = new Date(dbSqlDate.getTime());

								double doubleUpperLimit = doubleNumMeanavg
										+ (doubleNumMeanThreshold * doubleNumMeanStdDev);
								double doubleLowerLimit = doubleNumMeanavg
										- (doubleNumMeanThreshold * doubleNumMeanStdDev);

								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);

								if (integer < 10) {
									strDate += "(0" + integer + ")";
								} else {
									strDate += "(" + integer + ")";
								}
								Map<String, Object> data = new HashMap<String, Object>();
								data.put("Date", strDate);
								data.put("UpperLimit", doubleUpperLimit);
								data.put("Mean", doubleMean);
								data.put("LowerLimit", doubleLowerLimit);
								listOfListAvgTrend.add(data);
							}
						}
					}
				}
			}

			LOG.debug("Dashboard service : getAvgTrendChartData : listOfListAvgTrend " + listOfListAvgTrend);
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error("Dashboard service  : getAvgTrendChartData : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	private List<Date> getListOfDateFromColumnSummarywithTableNameAndDGroupVal(String paramStrTableName,
			String paramStrDGroupVal, int idApp, String toDate) {

		try {
			List<Date> listDate = new ArrayList<Date>();
			// Query compatibility changes for both POSTGRES and MYSQL
			String strDistinctRunQuery = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where idApp=" + idApp
						+ " and DGroupVal = '" + paramStrDGroupVal + "' and date between ('" + toDate
						+ "'::Date - INTERVAL '30 DAY') and '" + toDate + "'::Date";
			else

				strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where idApp=" + idApp
						+ " and DGroupVal = '" + paramStrDGroupVal + "' and date between ('" + toDate
						+ "' - INTERVAL 30 DAY) and '" + toDate + "'";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			return listDate;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfDateFromColumnSummary : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private List<Integer> getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(String paramStrTableName,
			String paramStrDGroupVal, int idApp) {
		try {
			List<Integer> listRun = new ArrayList<Integer>();
			String strDistinctRunQuery = "SELECT distinct run FROM " + paramStrTableName + " where idApp=" + idApp
					+ " and DGroupVal = '" + paramStrDGroupVal + "'";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listRun.add(sqlRowSet_strDistinctRunQuery.getInt("run"));
			}
			return listRun;
		} catch (Exception e) {
			LOG.error(
					"DataQualityResultsController : getListOfRunFromColumnSummaryWithTableNameAndDGroupVal : Exception :: "
							+ e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDate(String paramStrTableName, Date paramDate,
			int paramIntRun, int idApp) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE idApp=" + idApp
					+ " and DATE = '" + paramDate + "' AND RUN = " + paramIntRun;
			LOG.debug("DataQualityResultsController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : strQuery "
					+ strQuery);
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(
					"DataQualityResultsController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : Exception :: "
							+ e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, Object> rollupAnalysisDropdownDataLoadJsonObj(String tableName, int idApp) throws SQLException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String strColNameSqlQuery = "SELECT distinct ColName FROM " + tableName + " where idApp=" + idApp;
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
				strColNameSqlQuery += " and NumMeanThreshold IS NOT NULL";
				strColNameSqlQuery += " and Null_Threshold IS NULL";
			}
			SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1.queryForRowSet(strColNameSqlQuery);
			List<String> listColName = new ArrayList<String>();
			while (sqlRowSet_strColNameSqlQuery.next()) {
				listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
			}
			JSONArray jsonArrayColName = new JSONArray(listColName);

			String strdGroupColSqlQuery = "SELECT distinct dGroupCol FROM " + tableName + " where idApp=" + idApp;
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
				strdGroupColSqlQuery += " and NumMeanThreshold IS NOT NULL";
				strdGroupColSqlQuery += " and Null_Threshold IS NULL";
			}
			SqlRowSet sqlRowSet_strDGroupColSqlQuery = jdbcTemplate1.queryForRowSet(strdGroupColSqlQuery);
			List<String> listdGroupCol = new ArrayList<String>();
			while (sqlRowSet_strDGroupColSqlQuery.next()) {
				String strDGroupCal = sqlRowSet_strDGroupColSqlQuery.getString("dGroupCol");
				String[] arrdGroupColParts = strDGroupCal.split("\\?::\\?");
				listdGroupCol = Arrays.asList(arrdGroupColParts);
			}

			JSONArray jsonArrayDGroupCol = new JSONArray(listdGroupCol);
			result.put("dGroupCalSplit", jsonArrayDGroupCol);
			result.put("colName", jsonArrayColName);

		} catch (Exception e) {
			LOG.error("DataQualityResultsController : rollupAnalysisDropdownDataLoadJsonObj : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> getStdDevAvgTrendChartData(String tableName, String DGroupVal, String colName,
			int idApp, String toDate) {
		try {
			LOG.info("DataQualityResultsController : getStdDevAvgTrendChartData ");
			List<Map<String, Object>> listOfListAvgTrend = new ArrayList<Map<String, Object>>();
			List<Object> listHeader = new ArrayList<Object>();
			List<Integer> listRun = new ArrayList<Integer>();
			listHeader.add("Date");
			listHeader.add("UpperLimit");
			listHeader.add("StandardDeviation");
			listHeader.add("LowerLimit");
			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, DGroupVal, idApp, toDate);
			listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, DGroupVal, idApp);

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {
				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufQuery = new StringBuffer();
						strBufQuery.append("SELECT " + ifnull_function + "(dqcs.NumSDAvg, 0) AS NumSDAvg , "
								+ ifnull_function + "(dqcs.NumSDThreshold , 0) AS NumSDThreshold   , " + ifnull_function
								+ " (dqcs.NumSDStdDev, 0) AS NumSDStdDev , " + ifnull_function
								+ "(dqcs.Std_Dev, 0) AS Std_Dev , MAX(dqcs.Run) AS run, dqcs.Date  FROM ");
						strBufQuery.append(tableName);
						strBufQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp);
						strBufQuery.append(" and dqcs.DATE = '");
						strBufQuery.append(date);
						strBufQuery.append("' AND dqcs.DGroupVal = '");
						strBufQuery.append(DGroupVal + "'");
						strBufQuery.append(" AND dqcs.ColName = '");
						strBufQuery.append(colName + "' AND dqcs.run = " + integer);
						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());
						while (sqlRowSet_strDateSqlQuery.next()) {
							java.sql.Date dbSqlDate = sqlRowSet_strDateSqlQuery.getDate("Date");
							if (dbSqlDate != null && dbSqlDate.getTime() != 0l) {
								List<Object> listAvgColumn = new ArrayList<Object>();
								double doubleNumSDAvg = sqlRowSet_strDateSqlQuery.getDouble("NumSDAvg");
								double doubleNumSDThreshold = sqlRowSet_strDateSqlQuery.getDouble("NumSDThreshold");
								double doubleNumSDStdDev = sqlRowSet_strDateSqlQuery.getDouble("NumSDStdDev");
								double doubleStd_Dev = sqlRowSet_strDateSqlQuery.getDouble("Std_Dev");
								Date dbSqlDateConverted = new Date(dbSqlDate.getTime());
								double doubleUpperLimit = doubleNumSDAvg + (doubleNumSDThreshold * doubleNumSDStdDev);
								double doubleLowerLimit = doubleNumSDAvg - (doubleNumSDThreshold * doubleNumSDStdDev);
								if (doubleLowerLimit < 0) {
									doubleLowerLimit = 0.0;
								}
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
								String strDate = formatter.format(dbSqlDateConverted);
								if (integer < 10) {
									strDate += " (0" + integer + ") ";
								} else {
									strDate += " (" + integer + ") ";
								}
								Map<String, Object> data = new HashMap<String, Object>();
								data.put("Date", strDate);
								data.put("UpperLimit", doubleUpperLimit);
								data.put("StandardDeviation", doubleStd_Dev);
								data.put("LowerLimit", doubleLowerLimit);
								listOfListAvgTrend.add(data);
							}
						}
					}
				}
			}
			return listOfListAvgTrend;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, List<Map<String, Object>>> getsumOfNumStatsChartData(String tableName, String dGroupVal,
			String colName, int paramIntRun, int idApp, String toDate) throws SQLException {
		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		try {
			LOG.info("DataQualityResultsController : getsumOfNumStatsChartData ");
			List<Map<String, Object>> listOfListDataDriftChart = new ArrayList<Map<String, Object>>();
			List<Integer> listRun = new ArrayList<Integer>();
			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableNameAndDGroupVal(tableName, dGroupVal, idApp, toDate);
			if (paramIntRun == 0) {
				listRun = getListOfRunFromColumnSummaryWithTableNameAndDGroupVal(tableName, dGroupVal, idApp);
			} else {
				listRun.add(paramIntRun);
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date date : listDate) {
				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, date, integer, idApp)) {
						StringBuffer strBufDirftChartQuery = new StringBuffer();
						// Query compatibility changes for both POSTGRES and MYSQL
						if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND((dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold))::Numeric,2),0) AS Upper_Limit, ");
							strBufDirftChartQuery.append(" " + ifnull_function
									+ "( ROUND(dqcs.sumOfNumStat::Numeric,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(ifnull_function
									+ " (ROUND((dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold))::Numeric,2),0) AS Lower_Limit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("' AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						} else {
							strBufDirftChartQuery.append(" SELECT dqcs.Date, " + ifnull_function
									+ "(ROUND(dqcs.NumSumAvg+(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS Upper_Limit, ");
							strBufDirftChartQuery.append(
									" " + ifnull_function + "( ROUND(dqcs.sumOfNumStat,2),0) AS SumOfNumStats, ");
							strBufDirftChartQuery.append(ifnull_function
									+ " (ROUND(dqcs.NumSumAvg-(dqcs.NumSumStdDev*dqcs.numSumThreshold),2),0) AS Lower_Limit FROM ");
							strBufDirftChartQuery.append(tableName);
							strBufDirftChartQuery.append(" AS dqcs WHERE dqcs.idApp=" + idApp + " and ");
							strBufDirftChartQuery.append(" dqcs.DATE = '");
							strBufDirftChartQuery.append(date);
							strBufDirftChartQuery.append("' AND dqcs.DGroupVal = '");
							strBufDirftChartQuery.append(dGroupVal + "'");
							strBufDirftChartQuery.append(" AND dqcs.ColName = '");
							strBufDirftChartQuery.append(colName + "' AND dqcs.run = " + integer);
						}
						LOG.debug("DataQualityResultsController : getsumOfNumStatsChartData : strBufDirftChartQuery :: "
								+ strBufDirftChartQuery);
						SqlRowSet sqkRowSetDirftChartQuery = jdbcTemplate1
								.queryForRowSet(strBufDirftChartQuery.toString());
						while (sqkRowSetDirftChartQuery.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							if (integer < 10) {
								data.put("Date", sqkRowSetDirftChartQuery.getDate("Date") + "(0" + integer + ")");
							} else {
								data.put("Date", sqkRowSetDirftChartQuery.getDate("Date") + "(" + integer + ")");
							}
							data.put("UpperLimit", sqkRowSetDirftChartQuery.getDouble("Upper_Limit"));
							data.put("SumOfNumStats", sqkRowSetDirftChartQuery.getDouble("SumOfNumStats"));
							data.put("LowerLimit", sqkRowSetDirftChartQuery.getDouble("Lower_Limit"));
							listOfListDataDriftChart.add(data);
						}

					}
				}
			}
			result.put("sumOfNumStatsChart", listOfListDataDriftChart);
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getsumOfNumStatsChartData : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Object> getdistrubutionCheckTrendChartDetails(String tableName, String colName,
			List<String> listTable_dGroupVal, int idApp, String toDate) throws SQLException {
		Map<String, Object> result = new HashMap<>();
		try {
			List<Map<String, Object>> listRowChartData = new ArrayList<Map<String, Object>>();
			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummarywithTableName(tableName, idApp, toDate);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummaryWithTableName(tableName, idApp);

			JSONArray jsonArrayDGroupVal = new JSONArray(listTable_dGroupVal);

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";

			for (Date localdate : listDate) {

				for (Integer integer : listRun) {
					if (checkColumnSummaryRecordsExistsOrNotWithRunAndDate(tableName, localdate, integer, idApp)) {
						Map<String, Object> data = new HashMap<>();
						if (integer < 10) {
							data.put("date", localdate + "(0" + integer + ")");
						} else {
							data.put("date", localdate + "(0" + integer + ")");
						}

						for (String localstring : listTable_dGroupVal) {
							if (checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndDgroupVal(tableName, localdate,
									integer, localstring)) {
								String strSqlQueryGetChartList = "SELECT " + ifnull_function + "(Mean,0) as Mean FROM "
										+ tableName + " WHERE idApp=" + idApp + " and date = '" + localdate
										+ "' AND dGroupVal = '" + localstring + "' AND ColName='" + colName
										+ "' AND RUN = " + integer;
								SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1
										.queryForRowSet(strSqlQueryGetChartList);
								boolean flag = false;
								while (sqlRowSet_strSqlQueryGetChartList.next()) {
									data.put("mean_" + localstring,
											sqlRowSet_strSqlQueryGetChartList.getDouble("Mean"));
									flag = true;
								}

								if (!flag) {
									data.put("mean_" + localstring, 0);
								}
							} else {
								data.put("mean_" + localstring, 0);
							}
						}
						listRowChartData.add(data);
					}
				}
			}
			result.put("header", jsonArrayDGroupVal);
			result.put("chart", listRowChartData);
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getdistrubutionCheckTrendChartDetails : Exception :: " + e);
			LOG.error(e.getMessage());
		}
		return result;
	}

	private List<Date> getListOfDateFromColumnSummarywithTableName(String paramStrTableName, int idApp, String toDate) {

		try {
			List<Date> listDate = new ArrayList<Date>();
			String strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where idApp=" + idApp
					+ " and date between ('" + toDate + "' - INTERVAL 30 DAY) and '" + toDate + "' limit 200";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			return listDate;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfDateFromColumnSummarywithTableName : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private List<Integer> getListOfRunFromColumnSummaryWithTableName(String paramStrTableName, int idApp) {
		try {
			List<Integer> listRun = new ArrayList<Integer>();
			String strDistinctRunQuery = "SELECT distinct run FROM " + paramStrTableName + " where idApp=" + idApp
					+ " limit 200";
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listRun.add(sqlRowSet_strDistinctRunQuery.getInt("run"));
			}
			// Collections.sort(listRun);
			return listRun;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfRunFromColumnSummaryWithTableName : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDateAndDgroupVal(String paramStrTableName,
			Date paramDate, int paramIntRun, String paramStrDGroupVal) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE DATE = '" + paramDate
					+ "' AND RUN = " + paramIntRun + " AND DGroupVal = '" + paramStrDGroupVal + "'";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public int getLocationOfDGroupCal(String rollupVariable, String tableName, int idApp) {
		try {
			String strdGroupColSqlQuery = "SELECT distinct dGroupCol FROM " + tableName + " where idApp=" + idApp;
			LOG.debug("DataQualityResultsController : rollupAnalysisDropdownDataLoadJsonObj : strDateSqlQuery :: "
					+ strdGroupColSqlQuery);
			SqlRowSet sqlRowSet_strDGroupColSqlQuery = jdbcTemplate1.queryForRowSet(strdGroupColSqlQuery);
			List<String> listdGroupCol = new ArrayList<String>();
			while (sqlRowSet_strDGroupColSqlQuery.next()) {
				String strDGroupCal = sqlRowSet_strDGroupColSqlQuery.getString("dGroupCol");
				String[] arrdGroupColParts = strDGroupCal.split("-");
				listdGroupCol = Arrays.asList(arrdGroupColParts);
			}
			int intIndex = listdGroupCol.indexOf(rollupVariable);
			int returnVal = intIndex + 1;
			return returnVal;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	public Map<String, Object> getLineChartDetails(String tableName, int idApp, String toDate) throws SQLException {
		Map<String, Object> result = new HashMap<>();
		try {
			List<Date> listDate = new ArrayList<Date>();
			listDate = getListOfDateFromColumnSummary(tableName, idApp, toDate);

			List<Integer> listRun = new ArrayList<Integer>();
			listRun = getListOfRunFromColumnSummary(tableName, idApp);

			String str_dGroupVal_SqlQuery = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp;
			SqlRowSet sqlRowSet_str_dGroupVal_SqlQuery = jdbcTemplate1.queryForRowSet(str_dGroupVal_SqlQuery);
			List<String> listTable_dGroupVal = new ArrayList<String>();
			while (sqlRowSet_str_dGroupVal_SqlQuery.next()) {
				listTable_dGroupVal
						.add("'" + sqlRowSet_str_dGroupVal_SqlQuery.getString("dGroupVal").replace("'", "\\\'") + "'");

			}
			Set<String> set = new HashSet<>(listTable_dGroupVal);
			listTable_dGroupVal.clear();
			listTable_dGroupVal.addAll(set);
			List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
			List<String> listDateList = new ArrayList<String>();
			for (Date localdate : listDate) {
				listDateList.add("'" + localdate + "'");
			}
			String strSqlQueryGetChartList = "SELECT RecordCount, dGroupVal, date, RUN FROM " + tableName
					+ " WHERE date in (" + listDateList.toString().replace("[", "").replace("]", "")
					+ ") AND dGroupVal in (" + listTable_dGroupVal.toString().replace("[", "").replace("]", "")
					+ ") AND RUN in (" + listRun.toString().replace("[", "").replace("]", "") + ") AND idApp=" + idApp;
			LOG.debug("Chart Query : " + strSqlQueryGetChartList);
			SqlRowSet sqlRowSet_strSqlQueryGetChartList = jdbcTemplate1.queryForRowSet(strSqlQueryGetChartList);

			while (sqlRowSet_strSqlQueryGetChartList.next()) {
				Map<String, Object> data = new HashMap<>();
				if (sqlRowSet_strSqlQueryGetChartList.getInt("RUN") < 10) {
					data.put("date", sqlRowSet_strSqlQueryGetChartList.getString("date") + "(0"
							+ sqlRowSet_strSqlQueryGetChartList.getInt("RUN") + ")");
				} else {
					data.put("date", sqlRowSet_strSqlQueryGetChartList.getString("date") + "("
							+ sqlRowSet_strSqlQueryGetChartList.getInt("RUN") + ")");
				}
				data.put("dGroupValue", sqlRowSet_strSqlQueryGetChartList.getString("dGroupVal"));
				data.put("count", sqlRowSet_strSqlQueryGetChartList.getInt("RecordCount"));
				response.add(data);
			}

			List<String> headers = new ArrayList<String>();
			for (String val : listTable_dGroupVal)
				headers.add(val.replace("'", ""));
			result.put("header", headers);
			result.put("chart", response);

		} catch (Exception e) {
			LOG.error("PaginationController : getLineChartDetails : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	private Map<String, Integer> getColumnSummaryRecordsExistsOrNotWithRunAndDateMap(List<Date> listDate,
			List<Integer> listRun, String tableName, int idApp) {
		try {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String strDistinctRunQuery = "SELECT COUNT(id) AS id, date, run FROM " + tableName + " where date in ("
					+ listDate + ") AND RUN in (" + listRun + ") ";
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| tableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strDistinctRunQuery = strDistinctRunQuery + " and idApp=" + idApp;
			}
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
				map.put(sqlRowSet_strDistinctRunQuery.getDate("date") + "_"
						+ sqlRowSet_strDistinctRunQuery.getInt("run"), sqlRowSet_strDistinctRunQuery.getInt("id"));
			}
			return map;
		} catch (Exception e) {
			LOG.error("Dashboard service : getListOfDateFromColumnSummary : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return new HashMap<String, Integer>();
		}
	}

	private List<Date> getListOfDateFromColumnSummary(String paramStrTableName, int idApp, String toDate) {

		try {
			List<Date> listDate = new ArrayList<Date>();
			// Query compatibility changes for both POSTGRES and MYSQL
			String strDistinctRunQuery = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where (date between ('"
						+ toDate + "'::Date - INTERVAL '30 DAY') and '" + toDate + "')";
			else
				strDistinctRunQuery = "SELECT distinct Date FROM " + paramStrTableName + " where (date between ('"
						+ toDate + "' - INTERVAL 30 DAY) and '" + toDate + "')";

			if (paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strDistinctRunQuery = strDistinctRunQuery + " and idApp=" + idApp;
			}
			LOG.debug("Date query : " + strDistinctRunQuery);
			SqlRowSet sqlRowSet_strDistinctRunQuery = jdbcTemplate1.queryForRowSet(strDistinctRunQuery);
			while (sqlRowSet_strDistinctRunQuery.next()) {
				listDate.add(sqlRowSet_strDistinctRunQuery.getDate("Date"));
			}
			LOG.debug("Dates : " + listDate);
			return listDate;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfDateFromColumnSummary : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private List<Integer> getListOfRunFromColumnSummary(String paramStrTableName, int idApp) {
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
			LOG.debug("Pagination Controller : getListOfRunFromColumnSummary : listRun :: " + listRun);
			return listRun;
		} catch (Exception e) {
			LOG.error("DataQualityResultsController : getListOfRunFromColumnSummary : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private boolean checkColumnSummaryRecordsExistsOrNotWithRunAndDate(String paramStrTableName, int idApp,
			Date paramDate, int paramIntRun) {
		try {
			int intRowCount = 0;
			String strQuery = "SELECT COUNT(id) AS id FROM " + paramStrTableName + " WHERE DATE = '" + paramDate
					+ "' AND RUN = " + paramIntRun;

			if (paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_A1")
					|| paramStrTableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
				strQuery = strQuery + " and idApp=" + idApp;
			}

			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(strQuery);
			while (sqlRowSet.next()) {
				intRowCount = sqlRowSet.getInt("id");
			}
			if (intRowCount > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error("PaginationController : checkColumnSummaryRecordsExistsOrNotWithRunAndDate : Exception :: " + e);
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public List<Map<String, Object>> getValidationDQISummaryDetails(long idApp) {
		boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
		boolean validationStagingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);
		int maxRun = 1;
		String rcaTableName = "DATA_QUALITY_Transactionset_sum_A1";
		String maxRunSql = "select max(Run) from " + rcaTableName + " where Date=(select max(Date) from " + rcaTableName
				+ " where idApp=?) and idApp=?";
		maxRun = jdbcTemplate1.queryForObject(maxRunSql, Integer.class, idApp, idApp);
		Date maxDate = jdbcTemplate1.queryForObject("select max(Date) from " + rcaTableName + " where idApp=?",
				Date.class, idApp);
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		String strMaxDate = dt.format(maxDate);
		boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);
		ListApplications listApplicationsData = null;
		Map<String, String> tranRuleMap = null;
		Map<String, String> listdftranruleData = new HashMap<>();

		if (isRuleCatalogEnabled && validationStagingEnabled && isTestRun) {
			LOG.debug(
					"\n====> Validation is Approved and it is a Test Run, so fetching the details from staging tables");
			listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
			tranRuleMap = ruleCatalogDao.getDataFromStagingListDFTranRuleForMap(idApp);
			listdftranruleData.put(tranRuleMap.get("all"), tranRuleMap.get("identity"));

		} else {
			LOG.info("\n====> Fetching the details from actual tables");
			listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
			listdftranruleData = resultsDAO.getdatafromlistdftranrule(idApp);
			tranRuleMap = resultsDAO.getDataFromListDFTranRuleForMap(idApp);
		}

		Map<String, String> validationCheckTestNames = getListOfPossibleTestValues();

		Set<Map.Entry<String, String>> keySet = validationCheckTestNames.entrySet();

		Map<String, String> keyMetric1LableMap = getKeyMetric1LabelCheckNameMap();
		Map<String, String> keyMetric2LabelMap = getKeyMetric2LabelCheckNameMap();

		List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
		details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Record Count Fingerprint", listApplicationsData,
				validationCheckTestNames.get("DQ_Record Count Fingerprint"), keyMetric2LabelMap, keyMetric1LableMap,
				maxRun, strMaxDate));

		if (listApplicationsData != null && tranRuleMap != null) {

			if (listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Completeness", listApplicationsData,
						validationCheckTestNames.get("DQ_Completeness"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			if ((listApplicationsData.getlengthCheck() != null
					&& listApplicationsData.getlengthCheck().equalsIgnoreCase("Y"))
					|| (listApplicationsData.getMaxLengthCheck() != null
							&& listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y"))) {
				Map<String, Object> dashboardSummary = new HashMap<String, Object>();
				double DQI = 0.0;
				String status = "";
				double Key_Matric_1 = 0l;
				String trend = "NEUTRAL";
				double Key_Matric_2 = 0l;
				String Key_Matric_3 = "";
				double dqi_val = 0.0, metric1_val = 0.0, metric2_val = 0.0;
				int avgFactor = 0;
				if (listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {

					Map<String, Object> dqLengthCheckMap = getDashboardSummaryMapByRunAndDate(idApp, "DQ_LengthCheck",
							listApplicationsData, validationCheckTestNames.get("DQ_LengthCheck"), keyMetric2LabelMap,
							keyMetric1LableMap, maxRun, strMaxDate);
					Long keyMetric1 = Long.parseLong(dqLengthCheckMap.get("Key_Metric_1").toString());
					if (keyMetric1 > 0) {
						dqi_val = Double.parseDouble(dqLengthCheckMap.get("DQI").toString());
						status = (String) dqLengthCheckMap.get("status");
						metric1_val = Double.parseDouble(dqLengthCheckMap.get("Key_Metric_1").toString());
						metric2_val = Double.parseDouble(dqLengthCheckMap.get("Key_Metric_2").toString());
						Key_Matric_3 = (String) dqLengthCheckMap.get("Key_Metric_3");
						trend = dqLengthCheckMap.get("trend").toString();
						avgFactor++;
					}

				}
				if (listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {

					String maxLengthKey_Matric_3 = "";

					Map<String, Object> dqMaxLengthCheckMap = getDashboardSummaryMapByRunAndDate(idApp,
							"DQ_MaxLengthCheck", listApplicationsData,
							validationCheckTestNames.get("DQ_MaxLengthCheck"), keyMetric2LabelMap, keyMetric1LableMap,
							maxRun, strMaxDate);
					Long keyMetric1 = Long.parseLong(dqMaxLengthCheckMap.get("Key_Metric_1").toString());
					if (keyMetric1 > 0) {
						dqi_val = dqi_val + Double.parseDouble(dqMaxLengthCheckMap.get("DQI").toString());
						if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("passed")) {
							status = (String) dqMaxLengthCheckMap.get("status");

						}
						metric1_val = metric1_val
								+ Double.parseDouble(dqMaxLengthCheckMap.get("Key_Metric_1").toString());
						metric2_val = metric2_val
								+ Double.parseDouble(dqMaxLengthCheckMap.get("Key_Metric_2").toString());
						maxLengthKey_Matric_3 = (String) dqMaxLengthCheckMap.get("Key_Metric_3");
						trend = dqMaxLengthCheckMap.get("trend").toString();
						avgFactor++;
					}

					if (!Key_Matric_3.equals(""))
						Key_Matric_3 += "," + maxLengthKey_Matric_3;
					else
						Key_Matric_3 = maxLengthKey_Matric_3;
				}
				if (avgFactor != 0) {
					DQI = dqi_val / avgFactor;
				}

				Key_Matric_1 = metric1_val;
				Key_Matric_2 = metric2_val;
				DecimalFormat df12 = new DecimalFormat(",###");
				status = status == null ? "" : status;
				dashboardSummary.put("DQI", DQI);
				dashboardSummary.put("status", status);
				dashboardSummary.put("Key_Metric_1", df12.format((Key_Matric_1)) + "");
				dashboardSummary.put("Key_Metric_2", df12.format(Key_Matric_2) + "");
				dashboardSummary.put("Key_Metric_3", Key_Matric_3);
				dashboardSummary.put("test", "Length Check (Conformity)");
				dashboardSummary.put("Key_Metric_1_Label", "Number of Columns Tested");
				dashboardSummary.put("Key_Metric_2_Label", "Number of Records Failed");
				dashboardSummary.put("trend", trend);
				details.add(dashboardSummary);
			}
			if (listApplicationsData.getPatternCheck() != null
					&& listApplicationsData.getPatternCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Pattern_Data", listApplicationsData,
						validationCheckTestNames.get("DQ_Pattern_Data"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			if (listApplicationsData.getDefaultPatternCheck() != null
					&& listApplicationsData.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Default_Pattern_Data", listApplicationsData,
						validationCheckTestNames.get("DQ_Default_Pattern_Data"), keyMetric2LabelMap, keyMetric1LableMap,
						maxRun, strMaxDate));
			}
			if (tranRuleMap.get("identity") != null && tranRuleMap.get("identity").equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Uniqueness -Primary Keys",
						listApplicationsData, validationCheckTestNames.get("DQ_Uniqueness -Primary Keys"),
						keyMetric2LabelMap, keyMetric1LableMap, maxRun, strMaxDate));
			}
			if (tranRuleMap.get("all") != null && tranRuleMap.get("all").equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Uniqueness -Seleted Fields",
						listApplicationsData, validationCheckTestNames.get("DQ_Uniqueness -Seleted Fields"),
						keyMetric2LabelMap, keyMetric1LableMap, maxRun, strMaxDate));
			}
			if (listApplicationsData.getBadData() != null && listApplicationsData.getBadData().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Bad_Data", listApplicationsData,
						validationCheckTestNames.get("DQ_Bad_Data"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			if (listApplicationsData.getDefaultCheck() != null
					&& listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_DefaultCheck", listApplicationsData,
						validationCheckTestNames.get("DQ_DefaultCheck"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			if (listApplicationsData.getDateRuleChk() != null
					&& listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_DateRuleCheck", listApplicationsData,
						validationCheckTestNames.get("DQ_DateRuleCheck"), keyMetric2LabelMap, keyMetric1LableMap,
						maxRun, strMaxDate));
			}
			if (listApplicationsData.getDataDriftCheck() != null
					&& listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")
					|| listApplicationsData.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Data Drift", listApplicationsData,
						validationCheckTestNames.get("DQ_Data Drift"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			if (listApplicationsData.getRecordAnomalyCheck() != null
					&& listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Record Anomaly", listApplicationsData,
						validationCheckTestNames.get("DQ_Record Anomaly"), keyMetric2LabelMap, keyMetric1LableMap,
						maxRun, strMaxDate));
			}
			if (listApplicationsData.getNumericalStatCheck() != null
					&& listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Numerical Field Fingerprint",
						listApplicationsData, validationCheckTestNames.get("DQ_Numerical Field Fingerprint"),
						keyMetric2LabelMap, keyMetric1LableMap, maxRun, strMaxDate));
			}

			if (listApplicationsData.getTimelinessKeyChk() != null
					&& listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Timeliness", listApplicationsData,
						validationCheckTestNames.get("DQ_Timeliness"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}
			Integer sqlrule_count = 0;
			String strIdApp = idApp + "";
			SqlRowSet rowSet2 = resultsDAO.sql_rules_configured_count(strIdApp);
			try {
				if (!rowSet2.next()) {
					LOG.info("no results");
					sqlrule_count = -1;
				} else {
					sqlrule_count = rowSet2.getInt(1);
				}
				LOG.debug("=============sql rule configured ::" + rowSet2.getInt(1));
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			if (listApplicationsData.getApplyRules() != null
					&& listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && sqlrule_count > 0) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Sql_Rule", listApplicationsData,
						validationCheckTestNames.get("DQ_Sql_Rule"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}

			Long idData = 0l;
			SqlRowSet idDataAndAppName = resultsDAO.getIdDataAndAppNameFromListApplications(idApp);
			while (idDataAndAppName.next()) {
				idData = idDataAndAppName.getLong("idData");
			}
			Integer custrule_count = 0;
			Integer globalrule_count = 0;
			try {
				SqlRowSet rowSet = resultsDAO.custom_rules_configured_count(idData);
				if (!rowSet.next()) {
					LOG.info("no results");
					custrule_count = -1;
				} else {
					custrule_count = rowSet.getInt(1);
				}
				LOG.debug("custom rule configured ::" + rowSet.getInt(1));
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			SqlRowSet rowSet1 = resultsDAO.global_rules_configured_count(idData);
			try {
				if (!rowSet1.next()) {
					LOG.info("no results");
					globalrule_count = -1;
				} else {
					globalrule_count = rowSet1.getInt(1);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			if (listApplicationsData.getApplyRules() != null
					&& listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && custrule_count > 0) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_Rules", listApplicationsData,
						validationCheckTestNames.get("DQ_Rules"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}

			if (listApplicationsData.getApplyRules() != null
					&& listApplicationsData.getApplyRules().equalsIgnoreCase("Y") && globalrule_count > 0) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "DQ_GlobalRules", listApplicationsData,
						validationCheckTestNames.get("DQ_GlobalRules"), keyMetric2LabelMap, keyMetric1LableMap, maxRun,
						strMaxDate));
			}

			if (listApplicationsData.getStringStatCheck() != null
					&& listApplicationsData.getStringStatCheck().equalsIgnoreCase("Y")) {
				details.add(getDashboardSummaryMapByRunAndDate(idApp, "", listApplicationsData,
						validationCheckTestNames.get(""), keyMetric2LabelMap, keyMetric1LableMap, maxRun, strMaxDate));
			}
		}

		return details;

	}

	private Map<String, Object> getDashboardSummaryMapByRunAndDate(long idApp, String testName,
			ListApplications listApplicationsData, String checkName, Map<String, String> keyMetric2LabelMap,
			Map<String, String> keyMetric1LableMap, int maxRun, String maxDate) {
		Map<String, Object> dashboardSummary = new HashMap<String, Object>();
		Double DQI = 0.0;
		String status = "";
		Double Key_Matric_1 = null;
		Double Key_Matric_2 = null;
		String Key_Matric_3 = "";
		String trend = "";
		SqlRowSet dashboardDetails = resultsDAO.getDashboardSummaryByCheck(idApp, testName, maxRun, maxDate);
		if (dashboardDetails != null) {
			while (dashboardDetails.next()) {
				DQI = dashboardDetails.getDouble(4);
				status = dashboardDetails.getString(5);
				Key_Matric_1 = dashboardDetails.getDouble(6);
				Key_Matric_2 = dashboardDetails.getDouble(7);
				Key_Matric_3 = dashboardDetails.getString(8);
				maxRun = dashboardDetails.getInt("Run");
			}
			DecimalFormat df1 = new DecimalFormat("#0.00");
			LOG.debug("percentage :: " + df1.format(DQI));
			dashboardSummary.put("DQI", Double.valueOf(df1.format(DQI)));
			status = status == null ? "" : status;
			dashboardSummary.put("status", status);
			DecimalFormat df12 = new DecimalFormat(",###");
			String key_mat1 = "0";
			if (Key_Matric_1 != null)
				key_mat1 = DatabuckUtility.getIntegerValue(Key_Matric_1);

			String key_mat2 = "0";
			if (Key_Matric_2 != null)
				key_mat2 = DatabuckUtility.getIntegerValue(Key_Matric_2);

			if (Key_Matric_3.contains("BusinessKey")) {
				Key_Matric_3 = Key_Matric_3.replace("BusinessKey", "SequenceCheck");
			}

			List<Long> avgDQIList = resultsDAO.getAvgDQIofLastTwoRuns(idApp, testName);
			if (avgDQIList != null && avgDQIList.size() > 0) {
				if (avgDQIList.size() > 1) {
					if (avgDQIList.get(0) >= avgDQIList.get(1)) {
						trend = "UP";
					} else {
						trend = "DOWN";
					}
				}else {
					trend = "NEUTRAL";
				}
			}

			LOG.debug("------- In controller patternDataKey_Matric_1 =>" + Key_Matric_1);
			LOG.debug("------- In controller patternDataKey_Matric_2 =>" + Key_Matric_2);
			dashboardSummary.put("Key_Metric_1", key_mat1);
			dashboardSummary.put("Key_Metric_2", key_mat2);
			dashboardSummary.put("Key_Metric_3", Key_Matric_3);
			dashboardSummary.put("test", checkName);
			dashboardSummary.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));
			dashboardSummary.put("run", maxRun);
			dashboardSummary.put("trend", trend);
			String keyMetric2Label = "";
			try {
				keyMetric2Label = keyMetric2LabelMap.get(checkName);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
				dashboardSummary.put("Key_Metric_2_Label", keyMetric2Label);
			else
				dashboardSummary.put("Key_Metric_2_Label", "");

			if (testName.equals("DQ_Sql_Rule")) {
				LinkedHashMap<String, String> oSqlRulesSummary = resultsDAO
						.getSqlRulesDashboardSummary(String.valueOf(idApp));
				dashboardSummary.put("Key_Metric_1", oSqlRulesSummary.get("SqlRuleCount"));
				dashboardSummary.put("Key_Metric_2", oSqlRulesSummary.get("SqlRuleFailed"));
				dashboardSummary.put("DQI", oSqlRulesSummary.get("SqlRuleDqi"));
			}
		}
		return dashboardSummary;
	}

	private Map<String, Object> getSummayMap(long idApp, String testName, ListApplications listApplicationsData,
			String checkName, Map<String, String> keyMetric2LabelMap, Map<String, String> keyMetric1LableMap) {
		Map<String, Object> dashboardSummary = new HashMap<String, Object>();
		Double DQI = 0.0;
		String status = "";
		Double Key_Matric_1 = null;
		Double Key_Matric_2 = null;
		String Key_Matric_3 = "";
		int run = 0;
		SqlRowSet dashboardDetails = resultsDAO.CalculateDetailsForDashboard(idApp, testName, listApplicationsData);
		while (dashboardDetails.next()) {
			DQI = dashboardDetails.getDouble(4);
			status = dashboardDetails.getString(5);
			Key_Matric_1 = dashboardDetails.getDouble(6);
			Key_Matric_2 = dashboardDetails.getDouble(7);
			Key_Matric_3 = dashboardDetails.getString(8);
			run = dashboardDetails.getInt("Run");
		}
		DecimalFormat df1 = new DecimalFormat("#0.0");
		LOG.debug("percentage :: " + df1.format(DQI));
		dashboardSummary.put("DQI", Double.valueOf(df1.format(DQI)));
		status = status == null ? "" : status;
		dashboardSummary.put("status", status);
		DecimalFormat df12 = new DecimalFormat(",###");
		String key_mat1 = "0";
		if (Key_Matric_1 != null)
			key_mat1 = df12.format((Key_Matric_1));

		String key_mat2 = "0";
		if (Key_Matric_2 != null)
			key_mat2 = df12.format(Key_Matric_2);

		LOG.debug("------- In controller patternDataKey_Matric_1 =>" + Key_Matric_1);
		LOG.debug("------- In controller patternDataKey_Matric_2 =>" + Key_Matric_2);
		dashboardSummary.put("Key_Metric_1", key_mat1);
		dashboardSummary.put("Key_Metric_2", key_mat2);
		dashboardSummary.put("Key_Metric_3", Key_Matric_3);
		dashboardSummary.put("test", checkName);
		dashboardSummary.put("Key_Metric_1_Label", keyMetric1LableMap.get(checkName));
		dashboardSummary.put("run", run);
		String keyMetric2Label = "";
		try {
			keyMetric2Label = keyMetric2LabelMap.get(checkName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		if (keyMetric2Label != null && !keyMetric2Label.isEmpty())
			dashboardSummary.put("Key_Metric_2_Label", keyMetric2Label);
		else
			dashboardSummary.put("Key_Metric_2_Label", "");

		if (testName.equals("DQ_Sql_Rule")) {
			LinkedHashMap<String, String> oSqlRulesSummary = resultsDAO
					.getSqlRulesDashboardSummary(String.valueOf(idApp));
			dashboardSummary.put("Key_Metric_1", oSqlRulesSummary.get("SqlRuleCount"));
			dashboardSummary.put("Key_Metric_2", oSqlRulesSummary.get("SqlRuleFailed"));
			dashboardSummary.put("DQI", oSqlRulesSummary.get("SqlRuleDqi"));
		}
		return dashboardSummary;
	}

	public JSONObject getvalidationExecutiveSummary(long idApp) {
		List<String> messagesList = new ArrayList<String>();
		List<String> newMessagesList = new ArrayList<String>();
		JSONObject resultJson = new JSONObject();
		JSONObject messages = new JSONObject();
		try {
			int totalRunCount = resultsDAO.getTotalRunCount(idApp);
			if (totalRunCount == 0) {
				messagesList.add("Validation is not processed");
			} else if (totalRunCount == 1) {
				messagesList.add("No history available");
			} else if (totalRunCount == 2) {
				Map<String, DataQualityMasterDashboard> mapPrevious = resultsDAO
						.getPreviousRunDetailsForValidation(idApp);
				Map<String, DataQualityMasterDashboard> mapLatest = resultsDAO.getLatestRunDetailsForValidation(idApp);

				for (Map.Entry entry : mapLatest.entrySet()) {

					if (mapPrevious.containsKey(entry.getKey())) {
						DataQualityMasterDashboard masterDashboardPrevious = mapPrevious.get(entry.getKey());
						DataQualityMasterDashboard masterDashboardLatest = (DataQualityMasterDashboard) entry
								.getValue();
						Double dts = Double.parseDouble(
								new DecimalFormat("##.##").format(masterDashboardLatest.getAggreagteDQI()));
						Double previousDTS = Double.parseDouble(
								new DecimalFormat("##.##").format(masterDashboardPrevious.getAggreagteDQI()));
						if (masterDashboardPrevious.getAggreagteDQI() < masterDashboardLatest.getAggreagteDQI()) {
							messagesList.add(entry.getKey() + " Current DTS [" + dts
									+ "] has gone up from previous run [" + previousDTS + "]");
						} else if (masterDashboardPrevious.getAggreagteDQI() > masterDashboardLatest
								.getAggreagteDQI()) {
							messagesList.add(entry.getKey() + " Current DTS [" + dts
									+ "] has gone down from previous run [" + previousDTS + "]");
						}
					}
				}
				if (messagesList.isEmpty()) {
					messagesList.add("Current DTS is normal as per previous run.");
				}
			} else if (totalRunCount > 2) {
				Map<String, DataQualityMasterDashboard> mapPrevious = resultsDAO
						.getHistoricalAggregateDetailsForValidation(idApp);
				Map<String, DataQualityMasterDashboard> mapLatest = resultsDAO.getLatestRunDetailsForValidation(idApp);
				for (Map.Entry entry : mapLatest.entrySet()) {

					if (mapPrevious.containsKey(entry.getKey())) {
						DataQualityMasterDashboard masterDashboardPrevious = mapPrevious.get(entry.getKey());
						DataQualityMasterDashboard masterDashboardLatest = (DataQualityMasterDashboard) entry
								.getValue();
						Double dts = Double.parseDouble(
								new DecimalFormat("##.##").format(masterDashboardLatest.getAggreagteDQI()));
						Double previousDTS = Double.parseDouble(
								new DecimalFormat("##.##").format(masterDashboardPrevious.getAggreagteDQI()));
						if (masterDashboardPrevious.getUpperLimit() < masterDashboardLatest.getAggreagteDQI()) {
							messagesList.add(entry.getKey() + " Current DTS [" + dts + "] has gone up from history ["
									+ previousDTS + "].");
						} else if (masterDashboardPrevious.getLowerLimit() > masterDashboardLatest.getAggreagteDQI()) {
							messagesList.add(entry.getKey() + " Current DTS [" + dts + "] has gone down from history ["
									+ previousDTS + "]");
						}
					}
				}
				if (messagesList.isEmpty()) {
					messagesList.add("Current DTS is normal as per history.");
				}
			}
			if (totalRunCount > 1) {
				Map<String, DataQualityMasterDashboard> mapPreviousDts = resultsDAO
						.getHistoricalOverallAggregateDetailsForValidation(idApp);
				for (Map.Entry entry : mapPreviousDts.entrySet()) {
					if (mapPreviousDts.containsKey(entry.getKey())) {
						DataQualityMasterDashboard masterDashboardPrevious = mapPreviousDts.get(entry.getKey());
						double dts = resultsDAO.getLatestOverAllDtsDetailsForValidation(idApp);
						dts = Double.parseDouble(new DecimalFormat("##.##").format(dts));
						Double previousDTS = Double.parseDouble(
								new DecimalFormat("##.##").format(masterDashboardPrevious.getAggreagteDQI()));

						if (masterDashboardPrevious.getUpperLimit() < dts) {
							messagesList
									.add("Overall DTS [" + dts + "] has gone up from history [" + previousDTS + "]");
						} else if (masterDashboardPrevious.getLowerLimit() > dts) {
							messagesList
									.add("Overall DTS [" + dts + "] has gone down from history [" + previousDTS + "]");
						}
					}
				}

			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}

		// for DC-2970 - replace DQ_GlobalRules to DQ_CustomRules and DC-3031.
		for (String a : messagesList) {
			if (a.contains("Seleted Fields")) {
				newMessagesList.add(a.replace("Seleted Fields", "Selected Fields"));
			} else if (a.contains("DQ_GlobalRules")) {
				newMessagesList.add(a.replace("DQ_GlobalRules", "DQ_CustomRules"));
			} else {
				newMessagesList.add(a);
			}
		}

		messages.put("executiveSummary", newMessagesList.toArray());
		resultJson.put("message", "success");
		resultJson.put("status", "success");
		resultJson.put("result", messages);

		return resultJson;
	}

	public boolean getCheckIsEnabledOrNot(String checkName, long idApp) {
		boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
		boolean validationStagingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);
		int maxRun = 1;
		String rcaTableName = "DATA_QUALITY_Transactionset_sum_A1";
		String maxRunSql = "select max(Run) from " + rcaTableName + " where Date=(select max(Date) from " + rcaTableName
				+ " where idApp=?) and idApp=?";
		maxRun = jdbcTemplate1.queryForObject(maxRunSql, Integer.class, idApp, idApp);
		Date maxDate = jdbcTemplate1.queryForObject("select max(Date) from " + rcaTableName + " where idApp=?",
				Date.class, idApp);
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		String strMaxDate = dt.format(maxDate);
		boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, strMaxDate, maxRun);
		ListApplications listApplicationsData = null;
		List<RuleCatalog> ruleCatalog = null;
		Map<String, String> tranRuleMap = null;
		Map<String, String> listdftranruleData = new HashMap<>();

		if (isRuleCatalogEnabled && validationStagingEnabled && isTestRun) {
			LOG.info(
					"\n====> Validation is Approved and it is a Test Run, so fetching the details from staging tables");
			listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
			ruleCatalog = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
			tranRuleMap = ruleCatalogDao.getDataFromStagingListDFTranRuleForMap(idApp);
			listdftranruleData.put(tranRuleMap.get("all"), tranRuleMap.get("identity"));

		} else {
			LOG.info("\n====> Fetching the details from actual tables");
			listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
			ruleCatalog = ruleCatalogDao.getRulesFromRuleCatalog(idApp);
			listdftranruleData = resultsDAO.getdatafromlistdftranrule(idApp);
			tranRuleMap = resultsDAO.getDataFromListDFTranRuleForMap(idApp);
		}
		if (checkName.equals("DQ_Completeness") && listApplicationsData.getNonNullCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_LengthCheck") && listApplicationsData.getlengthCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_MaxLengthCheck") && listApplicationsData.getMaxLengthCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Pattern_Data") && listApplicationsData.getPatternCheck() != null
				&& listApplicationsData.getPatternCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Default_Pattern_Data") && listApplicationsData.getDefaultPatternCheck() != null
				&& listApplicationsData.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (tranRuleMap.get("identity") != null && tranRuleMap.get("identity").equalsIgnoreCase("Y")) {
			return true;
		}
		if (tranRuleMap.get("all") != null && tranRuleMap.get("all").equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Bad_Data") && listApplicationsData.getBadData() != null
				&& listApplicationsData.getBadData().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_DefaultCheck") && listApplicationsData.getDefaultCheck() != null
				&& listApplicationsData.getDefaultCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_DateRuleCheck") && listApplicationsData.getDateRuleChk() != null
				&& listApplicationsData.getDateRuleChk().equalsIgnoreCase("Y")
				|| listApplicationsData.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Data Drift") && listApplicationsData.getDataDriftCheck() != null
				&& listApplicationsData.getDataDriftCheck().equalsIgnoreCase("Y")
				|| listApplicationsData.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Record Anomaly") && listApplicationsData.getRecordAnomalyCheck() != null
				&& listApplicationsData.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_Numerical Field Fingerprint") && listApplicationsData.getNumericalStatCheck() != null
				&& listApplicationsData.getNumericalStatCheck().equalsIgnoreCase("Y")) {
			return true;
		}

		if (checkName.equals("DQ_Timeliness") && listApplicationsData.getTimelinessKeyChk() != null
				&& listApplicationsData.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
			return true;
		}
		if (checkName.equals("DQ_GlobalRules") && ruleCatalog != null) {
			for (RuleCatalog rc : ruleCatalog) {
				if (!rc.getRuleType().isEmpty() && rc.getRuleType().equalsIgnoreCase("Global Rule"))
					return true;
			}
		}
		return false;
	}

	// Run Table Summary & Save it - Vinav 20-07-2023
	@Scheduled(cron = DatabuckConstants.EVERYDAY_AT_ELEVEN_CRON)
	public void runTableDetailsCountSummary() throws Exception {
		
		// get all table list from application
		Long getAllTabelSize = 0L;
		List<ListDataSchema> listDataSchemaArrList = dataTemplateAddNewDAO.getuniqueUrlfromAllListDataSchema();
		for (ListDataSchema lds : listDataSchemaArrList) {
			TableListforSchema getAllTableList = dataConnectionService
					.getTableListForSchema(lds.getIdDataSchema() + "");
			getAllTabelSize = getAllTabelSize + getAllTableList.getTableNameList().size();
		}
		if (getAllTabelSize > 0) {

			Long noOfHighTrustTable = 0L;
			Long noOfLowTrustTable = 0L;
			List<Long> highTrustTableList = new ArrayList<Long>();
			List<Long> lowTrustTableList = new ArrayList<Long>();

			
			String totalHoursSaved = "NA";
			Date yesterday = DateUtility.getYesterday();
			Date previousYear = DateUtility.getPreviousYear();
			Date currentDate = new Date();
			String yesterdayStr = DateUtility.getDateinString(yesterday, "yyyy-MM-dd");
			String previousYearStr = DateUtility.getDateinString(previousYear, "yyyy-MM-dd");
			String currentDateStr = DateUtility.getDateinString(currentDate, "yyyy-MM-dd");
			
			List<Long> ruleExecutedList = new ArrayList<Long>();
			List<Long> issueDetectedList = new ArrayList<Long>();
			List<Double> hourSavedList = new ArrayList<Double>();
			
			// Monitored table 
			List<listDataAccess> uniqueListDataAccess = iTemplateViewDAO.getUniqueListDataAccess();
			for (listDataAccess uniqueTable : uniqueListDataAccess) {
				// get all unique table list from application which are added in validations
				List<listDataAccess> allIdDataForAUniquleTableList = iTemplateViewDAO
						.getMonitoredTableNamesfromListDataAccess(uniqueTable);

				List<Double> avgDQIofATableList = new ArrayList<Double>();

				for (listDataAccess lda : allIdDataForAUniquleTableList) {
					List<ListApplications> appIdList = iTemplateViewDAO.getidAppListFromListApplication(lda.getIdData());
					List<Long> idAppList = appIdList.stream().map(e -> e.getIdApp()).collect(Collectors.toList());
					List<Double> dQIAllIdAppsOfAIdDataList = new ArrayList<Double>();
					
				
					
					// get average value of the data quality for all application
					for (Long e : idAppList) {
						List<Integer> dqiList = iResultsDAO.getAllResultsforIdApp(e);
						Long ruleExecuted = 0l;
						Long issuesDetected = 0l;
						Long recordCount = 0l;
						String hoursSaved = "NA";
						Double avgIdAppDQI = dqiList.stream().mapToInt(val -> val).average().orElse(0);
						if (avgIdAppDQI != 0) {
							dQIAllIdAppsOfAIdDataList.add(avgIdAppDQI);
							// check rules executed
							ruleExecuted = ruleExecuted
									+ iResultsDAO.getTotalExecutedRulesCount(e, currentDateStr, yesterdayStr);
							issuesDetected = issuesDetected
									+ iResultsDAO.getTotalFailedRecordCountforAppId(e, currentDateStr, previousYearStr);
							recordCount = recordCount
									+ iResultsDAO.getTotalRecordCountforAppId(e, currentDateStr, previousYearStr);
							
							DecimalFormat df = new DecimalFormat("#.##");
							if (recordCount < 10000) {
								hoursSaved = df.format(issuesDetected / 60.0);
							} else if (recordCount >= 10000 && recordCount < 100000) {
								hoursSaved = df.format((issuesDetected * 2) / 60.0);
							} else if (recordCount >= 100000 && recordCount < 1000000) {
								hoursSaved = df.format((issuesDetected * 3) / 60.0);
							} else if (recordCount >= 1000000) {
								hoursSaved = df.format((issuesDetected * 4) / 60.0);
							}
							
							ruleExecutedList.add(ruleExecuted);
							issueDetectedList.add(issuesDetected);
							hourSavedList.add(Double.parseDouble(hoursSaved));
							
						
						}
					}
					;

					// check the table of high and low trust tables

					Double avgAllIdAppDQI = dQIAllIdAppsOfAIdDataList.stream().mapToDouble(val -> val).average()
							.orElse(0);
					avgDQIofATableList.add(avgAllIdAppDQI);

				}

				Double avgDQITable = avgDQIofATableList.stream().mapToDouble(val -> val).average().orElse(0);
				if (avgDQITable != 0) {
					if (avgDQITable >= 90) {
						noOfHighTrustTable++;
					} else {
						noOfLowTrustTable++;
					}
				}

			}
			Long monitoredTableSize = noOfHighTrustTable + noOfLowTrustTable;
			Long unValidatedTableCount = getAllTabelSize - monitoredTableSize;
			Long totalRulesExecuted = ruleExecutedList.stream().mapToLong(val -> val).sum();
			Long totalIssueDetected = issueDetectedList.stream().mapToLong(val -> val).sum();
			Double totalHourSaved = hourSavedList.stream().mapToDouble(val -> val).sum();

		
			// add the data into table
			iDashboardConsoleDao.insertIntoDashboardTableCount(1, 1, getAllTabelSize, monitoredTableSize,
					totalIssueDetected, unValidatedTableCount, totalRulesExecuted, noOfHighTrustTable, noOfLowTrustTable,
					Double.toString(totalHourSaved), "");

		}
	}

	// Get Table Summary & Show it on Dashboard Screen - Vinav 20-07-2023
	public DashboardTableCountSummary getAllTableDetailsCountSummary() throws Exception {
		DashboardTableCountSummary dashboardTableCountSummary = new DashboardTableCountSummary();
		dashboardTableCountSummary = iDashboardConsoleDao.getSumOfDashboardTableCount();
		dashboardTableCountSummary.setUnValidatedTablesCount(dashboardTableCountSummary.getTotalTablesCount()
				- dashboardTableCountSummary.getMonitoredTablesCount());
		Double hrsSaved = Double.parseDouble(dashboardTableCountSummary.getEffortsSavedHrs());
		if(hrsSaved.compareTo(1d)>0) {
			dashboardTableCountSummary.setEffortsSavedHrs(hrsSaved.intValue()+"");
		}else if (hrsSaved.compareTo(0d)==0){
			dashboardTableCountSummary.setEffortsSavedHrs(hrsSaved.intValue()+"");
		}else {
			dashboardTableCountSummary.setEffortsSavedHrs(hrsSaved.toString());
		}
		return dashboardTableCountSummary;
	}

	// List of Tables processed
	public List<Object> getProcessedTableList(TableListforSchema tableListFrom, String selSchemaId) {

		List<Object> tableProcessedList = new ArrayList<>();
		;
		LOG.info("\n====> Started validating List of tables processed in DataBuck ");
		try {
			for (String table : tableListFrom.getTableNameList()) {
				Map<String, String> tablelist = new HashMap<String, String>();
				String Status="NotExist";

				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)){
					int count= 0;

					String sql="select count(*) from data_quality_dashboard dq join DashBoard_Summary ds on dq.IdApp=ds.AppId where dq.folderName=? and dq.idDataSchema=?";
					count = jdbcTemplate1.queryForObject(sql,Integer.class, table, Long.parseLong(selSchemaId));

					if(count > 0)
						Status="Exist";
				}else {
					Status = jdbcTemplate1.queryForObject(
							"select  case when count(*) > 0 then 'Exist' else 'NotExist' end from data_quality_dashboard dq join DashBoard_Summary ds on dq.IdApp=ds.AppId "
									+ "where dq.folderName=? and dq.idDataSchema=?",
							String.class, table, Long.parseLong(selSchemaId));
				}
				if (Status.equalsIgnoreCase("Exist")) {
					tablelist.put("tableName", table);
					tablelist.put("monitoredTable", "Y");
				} else {
					tablelist.put("tableName", table);
					tablelist.put("monitoredTable", "N");
				}
				tableProcessedList.add(tablelist);

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return tableProcessedList;

	}

	// Get Date vs Dts Graph Data - Vinav 27-07-2023
	public List<DateVsDTSGraph> getDateVsDtsGraphData(Long idApp, String fromDate, String toDate) throws Exception {
		List<DateVsDTSGraph> listDateVsDTSGraph = new ArrayList<DateVsDTSGraph>();
		listDateVsDTSGraph = iResultsDAO.getDtsVsDateGraphData(idApp, fromDate, toDate);
		return listDateVsDTSGraph;
	}

}
