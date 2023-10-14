package com.databuck.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.databuck.dao.IPaginationDAO;
import com.databuck.dto.ValidationListRequest;
import com.databuck.dto.ValidationResultRequest;
import com.databuck.util.DatabuckUtility;

@Service
public class PaginationServiceRest {

	@Autowired
	private IPaginationDAO paginationDAO;

	@Autowired
	private DatabuckUtility databuckUtility;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	private static final Logger LOG = Logger.getLogger(PaginationServiceRest.class);

	// Using this service layer method to get all dashboad validations
	public Map<String, Object> getAllValidations(ValidationListRequest validationRequest) {
		LOG.info("Into the getAllValidations method.");
		Map<String, Object> result = new HashMap<>();
		try {
			result = paginationDAO.getAllValidations(validationRequest, paginationDAO
					.getAppIdsListForDomainProject(validationRequest.getDomainId(), validationRequest.getProjectId()));
		} catch (Exception ex) {
			LOG.error("Got the error while getting the validations : " + ex.getMessage());
			ex.printStackTrace();
		}
		LOG.info("End of the getAllValidations method.");
		return result;
	}

	public Map<String, Object> getDataDriftResult(ValidationResultRequest resultRequest) {
		LOG.info("Into the getAllValidations method.");
		Map<String, Object> result = new HashMap<>();
		try {
			result = paginationDAO.getDataDriftResult(resultRequest);
		} catch (Exception ex) {
			LOG.error("Got the error while getting the validations : " + ex.getMessage());
			ex.printStackTrace();
		}
		LOG.info("End of the getAllValidations method.");
		return result;
	}

	public Map<String, Object> getPaginatedValidationsJsonData(ValidationListRequest validationRequest) {
		LOG.info("Into the getPaginatedValidationsJsonData method.");
		Map<String, Object> result = new HashMap<>();
		try {
			result = paginationDAO.getPaginatedValidationsJsonData(validationRequest);
		} catch (Exception ex) {
			LOG.error("Got the error while getting the validations : " + ex.getMessage());
			ex.printStackTrace();
		}
		LOG.info("End of the getPaginatedValidationsJsonData method.");
		return result;
	}

	public Map<String, Object> getDistributionResult(ValidationResultRequest resultRequest) {
		LOG.info("Into the getAllValidations method.");
		Map<String, Object> result = new HashMap<>();
		try {
			result = paginationDAO.getDistributionResult(resultRequest);
		} catch (Exception ex) {
			LOG.error("Got the error while getting the validations : " + ex.getMessage());
			ex.printStackTrace();
		}
		LOG.info("End of the getAllValidations method.");
		return result;
	}

	// Adding New code below (23-Feb-23)

	public JSONObject validateEssentialCheckParams(JSONObject inputJson) {
		String status = "failed";
		String message = "";

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		try {
			long idApp = inputJson.getLong("idApp");
			if (idApp <= 0l) {
				message = "Invalid idApp";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "missing idApp in the arguments";
			e.printStackTrace();
			LOG.error(message + " " + e.getStackTrace());
		}

		try {
			String fromDate = inputJson.getString("fromDate");
			if (fromDate != null && !fromDate.trim().isEmpty()) {
				Date f_date = simpleDateFormat.parse(fromDate.trim());
				if (f_date == null) {
					message = "Invalid fromDate format";
					LOG.error(message);
				}
			} else {
				message = "missing fromDate in the arguments";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "please check the syntax of argument 'fromDate'";
			e.printStackTrace();
			LOG.error(message + " " + e.getStackTrace());
		}

		try {
			String toDate = inputJson.getString("toDate");
			if (toDate != null && !toDate.trim().isEmpty()) {
				Date t_date = simpleDateFormat.parse(toDate.trim());
				if (t_date == null) {
					message = "Invalid toDate format";
					LOG.error(message);
				}
			} else {
				message = "missing toDate in the arguments";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "please check the syntax of argument 'toDate'";
			e.printStackTrace();
			LOG.error(message + " " + e.getStackTrace());
		}

		try {
			String checkName = inputJson.getString("checkName");
			if (checkName != null && !checkName.trim().isEmpty()) {
				if (checkName.contains(">") || checkName.contains("<")) {
					message = "Invalid checkName";
					LOG.error(message);
				}
			} else {
				message = "missing checkName in the arguments";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "please check the syntax of argument 'checkName'";
			e.printStackTrace();
			LOG.error(message + " " + e.getStackTrace());
		}

		try {
			String colName = inputJson.getString("colName");
			if (colName != null) {
				if (colName.contains(">") || colName.contains("<")) {
					message = "Invalid colName";
					LOG.error(message);
				}
			} else {
				message = "missing checkName in the arguments";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "please check the syntax of argument 'colName'";
			e.printStackTrace();
			LOG.error(message + " " + e.getStackTrace());
		}
		if (message.trim().isEmpty())
			status = "success";

		JSONObject validateObj = new JSONObject();
		validateObj.put("status", status);
		validateObj.put("message", message);
		return validateObj;
	}

	// New method to get null check results
	public JSONObject getNullCheckResults(JSONObject requestObj) {

		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "ColName", "Record_Count", "Null_Value",
						"Null_Percentage", "Null_Threshold", "Historic_Null_Mean", "Historic_Null_stddev", "Status",
						"forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_NullCheck_Summary where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_NullCheck_Summary where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQNullCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_NullCheck_Summary", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	// New method to get microsegment null check results
	public JSONObject getMicrosegmentNullCheckResults(JSONObject requestObj) {

		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			globalSearchColsList = Arrays.asList("Date", "Run", "ColName", "Null_Value", "Record_Count",
					"Null_Percentage", "Null_Threshold", "dGroupVal", "dgroupCol", "Status", "forgot_run_enabled");
			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";
				// Excluding dgroupVal clause during historical result fetch, since we were not
				// able to see all the old microsegment results
				// if (dGroupVal != null && !dGroupVal.trim().isEmpty())
				// colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";
			String nullCheckResultCols = String.join(",", globalSearchColsList);

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select " + nullCheckResultCols
						+ " ,id,idApp from DATA_QUALITY_Column_Summary where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND Null_Threshold is not null AND NOT(Date ='" + maxDate
						+ "' AND Run =" + maxRun + ") " + colNameQuery + globleSearch;

			} else {
				sql = "Select " + nullCheckResultCols + " ,id,idApp from DATA_QUALITY_Column_Summary where Date ='"
						+ maxDate + "' AND Run= '" + maxRun + "' AND idApp=" + idApp + " AND Null_Threshold is not null"
						+ globleSearch;
			}

			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQMicroNullCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Column_Summary", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	public JSONObject getLengthCheckResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "ColName", "forgot_run_enabled",
						"FailedRecords_Percentage", "Length_Threshold", "RecordCount", "Length", "Run", "idApp",
						"TotalFailedRecords", "max_length_check_enabled", "Id", "Status");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Length_Check where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Length_Check where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQLengthCheckResults((sql));

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Length_Check", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	public JSONObject getAutoDiscoverdPatternResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "Col_Name", "Total_Records", "Total_Failed_Records",
						"Total_Matched_Records", "Patterns_List", "New_Pattern", "FailedRecords_Percentage",
						"Pattern_Threshold", "Status", "forgot_run_enabled", "Csv_File_Path");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND Col_Name='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Unmatched_Default_Pattern_Data where Date between '" + fromDate
						+ "' AND '" + toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run ="
						+ maxRun + ") " + colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Unmatched_Default_Pattern_Data where Date ='" + maxDate
						+ "' AND Run= '" + maxRun + "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getAutoDiscoverdPatternResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Unmatched_Default_Pattern_Data", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getUserDefinedPatternResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "Col_Name", "Total_Records", "Total_Failed_Records",
						"Pattern_Threshold", "FailedRecords_Percentage", "Status", "forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND Col_Name='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Unmatched_Pattern_Data where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Unmatched_Pattern_Data where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getUserDefinedPatternResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Unmatched_Pattern_Data", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQBadDataCheckResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "ColName", "TotalRecord", "TotalBadRecord",
						"badDataPercentage", "badDataThreshold", "forgot_run_enabled", "Run", "idApp", "Id", "Status");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_badData where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_badData where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQBadDataCheckResults((sql));

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_badData", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDefaultValueCheckResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "ColName", "Default_Value", "Default_Percentage",
						"Default_Count", "forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_default_value where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_default_value where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDefaultValueCheckResults((sql));

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_default_value", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDataDriftResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "colName", "uniqueValuesCount", "missingValueCount",
						"newValueCount", "forgot_run_enabled", "dGroupCol", "dGroupVal");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";

				if (dGroupVal != null && !dGroupVal.trim().isEmpty() && !"null".equalsIgnoreCase(dGroupVal)) {
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";
				} else {
					colNameQuery = colNameQuery + " AND dGroupVal IS NULL ";
				}
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND NOT(Date ='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + globleSearch;
			}

			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDataDriftCountSummaryResults(sql , idApp);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;

	}

	public JSONObject getDQDataDriftResultsSummary(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");

			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "colName", "status", "operation", "uniqueValues",
						"forgot_run_enabled", "dGroupCol", "dGroupVal");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String sql = "";

			sql = "select * from DATA_QUALITY_DATA_DRIFT_SUMMARY where idApp = " + idApp + " and colName ='" + colName
					+ "' and Date between '" + fromDate + "' and '" + toDate + "' " + globleSearch;
			// }

			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Run DESC limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDataDriftSummaryResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_DATA_DRIFT_SUMMARY", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;

	}

	public JSONObject getDuplicateCheckSummaryResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "Type", "duplicateCheckFields",
						"Duplicate", "dGroupVal", "dGroupCol", "Status", "Percentage", "TotalCount", "Threshold");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND duplicateCheckFields ='" + colName + "' ";

				// Excluding dgroupVal clause during historical result fetch, since we were not
				// able to see all the old microsegment results
//				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
//					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Duplicate_Check_Summary where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Duplicate_Check_Summary where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDuplicateCheckSummaryResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Duplicate_Check_Summary", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDuplicateCheckIndividualResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "duplicateCheckFields",
						"duplicateCheckValues", "dupcount", "forgot_run_enabled", "dGroupVal", "dGroupCol");

				for (String col : globalSearchColsList)
					globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";

			if (colName != null && !colName.trim().isEmpty())
				colNameQuery = " AND duplicateCheckFields='" + colName + "' ";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");

				if (dGroupVal != null && !dGroupVal.trim().isEmpty() && !"null".equalsIgnoreCase(dGroupVal)) {
					dGroupVal = dGroupVal.replace("'", "\\'");
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

				} else {
					colNameQuery = colNameQuery + " AND dGroupVal IS NULL ";
				}

			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Transaction_Detail_All where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Transaction_Detail_All where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + colNameQuery + " " + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDuplicateCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Transaction_Detail_All", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDuplicateCheckCompositeResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "duplicateCheckFields",
						"duplicateCheckValues", "dupcount", "forgot_run_enabled", "dGroupVal", "dGroupCol");

				for (String col : globalSearchColsList)
					globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (colName != null && !colName.trim().isEmpty())
				colNameQuery = " AND duplicateCheckFields ='" + colName + "' ";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");

				if (dGroupVal != null && !dGroupVal.trim().isEmpty() && !"null".equalsIgnoreCase(dGroupVal)) {
					dGroupVal = dGroupVal.replace("'", "\\'");
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

				} else {
					colNameQuery = colNameQuery + " AND dGroupVal IS NULL ";
				}

			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Transaction_Detail_Identity where Date between '" + fromDate
						+ "' AND '" + toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run ="
						+ maxRun + ") " + colNameQuery + " " + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Transaction_Detail_Identity where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + colNameQuery + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDuplicateCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Transaction_Detail_Identity", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getGlobalRuleResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "ruleName", "dGroupCol", "dGroupVal",
						"totalRecords", "totalFailed", "rulePercentage", "ruleThreshold", "status",
						"forgot_run_enabled", "dimension_name");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else{
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ruleName ='" + colName + "' ";
				// Excluding dgroupVal clause during historical result fetch, since we were not
				// able to see all the old microsegment results
//				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
//					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_GlobalRules where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * from DATA_QUALITY_GlobalRules where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getGlobalRuleResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_GlobalRules", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getTimeSequenceCheckResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");

			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "TimelinessKey", "No_Of_Days", "Status");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String sql = "";

			sql = "select * from DATA_QUALITY_timeliness_check where idApp = " + idApp + " and  Date between '"
					+ fromDate + "' and '" + toDate + "' " + globleSearch;
			// }

			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Run DESC limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getTimeSequenceCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_timeliness_check", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;

	}

	public JSONObject getDQRecordAnomalyResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "ColName", "ColVal", "mean",
						"stddev", "dGroupVal", "dGroupCol", "ra_Deviation", "Status", "RA_Dqi", "forgot_run_enabled",
						"threshold");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName='" + colName + "' ";

				if (dGroupVal != null && !dGroupVal.trim().isEmpty() && !"null".equalsIgnoreCase(dGroupVal)) {
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";
				} else {
					colNameQuery = colNameQuery + " AND dGroupVal IS NULL ";
				}
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Record_Anomaly where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND NOT(Date ='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + globleSearch;

			} else {
				sql = "Select * from DATA_QUALITY_Record_Anomaly where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + globleSearch;
			}

			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQRecordAnomalyResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			
			for (int i = 0; i < valCheckResultArr.length(); i++) {
				JSONObject itemArr = (JSONObject) valCheckResultArr.get(i);					
				String str=itemArr.get("colVal").toString();				
				System.out.println("str  "+str);
				if(str!=null) {						
			        DecimalFormat df = new DecimalFormat("#.###");
			        df.setRoundingMode(RoundingMode.CEILING);
			        itemArr.put("colVal",df.format(Double.parseDouble(str.toString())));
				}
			}
			
			
			essentialChkResultsObj.put("DATA_QUALITY_Record_Anomaly", valCheckResultArr);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDistributionCheckResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			globalSearchColsList = Arrays.asList("Date", "ColName", "Record_Count", "Run", "dGroupVal", "Status",
					"NumSDDeviation", "Min", "Std_Dev", "NumMeanAvg", "NumMeanDeviation", "NumSDAvg", "NumSumStdDev",
					"NumMeanStatus", "Max", "NumMeanStdDev", "NumMeanThreshold", "Mean", "Count", "NumSDStatus",
					"NumSDStdDev", "NumSumAvg", "NumSDThreshold", "NumSumThreshold", "sumOfNumStat", "dGroupCol");

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND ColName ='" + colName + "' ";

				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
					colNameQuery = colNameQuery + " AND dGroupVal = '" + dGroupVal + "'";

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}
			String distributionColumns = String.join(",", globalSearchColsList);
			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = " Select " + distributionColumns
						+ ", idApp, CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ " ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from DATA_QUALITY_Column_Summary where Date between '"
						+ fromDate + "' AND '" + toDate + "' AND Null_Threshold is null AND idApp=" + idApp
						+ " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") " + colNameQuery + " "
						+ globleSearch;

			} else {
				sql = " Select " + distributionColumns
						+ ", idApp, CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ " ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from DATA_QUALITY_Column_Summary where  Null_Threshold is null AND idApp="
						+ idApp + " AND  Date ='" + maxDate + "' AND Run= '" + maxRun + "'" + globleSearch;

			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDistributionCheckResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Column_Summary", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDateConsistencyResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Date", "Run", "DateField", "TotalFailedRecords",
						"TotalNumberOfRecords", "forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty())
					colNameQuery = " AND DateField ='" + colName + "' ";
			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_DateRule_Summary where Date between '" + fromDate + "' AND '" + toDate
						+ "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * from DATA_QUALITY_DateRule_Summary where Date ='" + maxDate + "' AND Run= '" + maxRun
						+ "' AND idApp=" + idApp + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDateConsistencyResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_DateRule_Summary", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQDateConsistencyFailedResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Id", "idApp", "Run", "DateFieldCols", "DateFieldValues",
						"dGroupVal", "dGroupCol", "FailureReason", "forgot_run_enabled", "Date");

				for (String col : globalSearchColsList)
					globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}
			if (colName != null && !colName.trim().isEmpty())
				colNameQuery = " AND DateFieldCols ='" + colName + "' ";

			String sql = "";

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_DateRule_FailedRecords where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ") "
						+ colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * from DATA_QUALITY_DateRule_FailedRecords where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + colNameQuery + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQDateConsistencyFailedResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_DateRule_FailedRecords", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQRecordCountAnomalyResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = "AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek",
						"hourOfDay", "RecordCount", "fileNameValidationStatus", "columnOrderValidationStatus",
						"DuplicateDataSet", "RC_Std_Dev", "RC_Mean", "RC_Deviation", "RC_Std_Dev_Status",
						"RC_Mean_Moving_Avg", "M_Mean_Moving_Avg_Status", "recordAnomalyCount", "dGroupVal",
						"dGroupCol", "missingDates", "forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * from DATA_QUALITY_Transactionset_sum_A1 where Date between '" + fromDate + "' AND '"
						+ toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate + "' AND Run =" + maxRun + ")"
						+ " " + globleSearch;
			} else {
				sql = "Select * from DATA_QUALITY_Transactionset_sum_A1 where Date ='" + maxDate + "' AND Run= '"
						+ maxRun + "' AND idApp=" + idApp + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQRecordCountAnomalyResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}  

				for (int i = 0; i < valCheckResultArr.length(); i++) {
					JSONObject itemArr = (JSONObject) valCheckResultArr.get(i);					
					String str=itemArr.get("dGroupCol").toString();
					String dGroupCol="";
					if(str.lastIndexOf("?")>-1)
						 dGroupCol=str.substring(0,str.lastIndexOf("?")+1)+makeTitleCaseString(str.substring(str.lastIndexOf("?")+1,str.length()));		
					else
						dGroupCol=makeTitleCaseString(str);					
					
					itemArr.put("dGroupCol",dGroupCol );
					

				}
			
			essentialChkResultsObj.put("DATA_QUALITY_Transactionset_sum_A1", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}
	
	
	
	public  String makeTitleCaseString(String str){
		String str1 = str.replaceAll("([a-z])([A-Z])","$1 $2").trim().toLowerCase();
		Matcher matcher = Pattern.compile("(?:^| )[^a-z]*[a-z]").matcher(str1);        //
		StringBuffer result = new StringBuffer();
		while (matcher.find())
			matcher.appendReplacement(result, matcher.group().toUpperCase());

     return matcher.appendTail(result).toString();
	}
	
	
	

	public JSONObject getDQRecordCountAnomalyDgroupResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek",
						"hourOfDay", "RecordCount", "fileNameValidationStatus", "columnOrderValidationStatus",
						"RC_Std_Dev", "RC_Mean", "dGroupDeviation", "dGroupRcStatus", "measurementCol", "SumOf_M_Col",
						"M_Std_Dev", "M_Mean", "M_Deviation", "M_Std_Dev_Status", "recordAnomalyCount", "dGroupVal",
						"dGroupCol", "missingDates", "DuplicateDataSet", "Action", "UserName", "Time", "Validity",
						"forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * ,(CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi from DATA_QUALITY_Transactionset_sum_dgroup where Date between '"
						+ fromDate + "' AND '" + toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate
						+ "' AND Run =" + maxRun + ") AND Validity = 'False' " + colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * ,(CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi from DATA_QUALITY_Transactionset_sum_dgroup where Date ='" + maxDate
						+ "' AND Run= '" + maxRun + "' AND idApp=" + idApp + " AND Validity = 'False' " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQRecordCountAnomalyDgroupResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Transactionset_sum_dgroup", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQMicrosegmentRCAResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek",
						"hourOfDay", "RecordCount", "fileNameValidationStatus", "columnOrderValidationStatus",
						"RC_Std_Dev", "RC_Mean", "dGroupDeviation", "dGroupRcStatus", "measurementCol", "SumOf_M_Col",
						"M_Std_Dev", "M_Mean", "M_Deviation", "M_Std_Dev_Status", "recordAnomalyCount", "dGroupVal",
						"dGroupCol", "missingDates", "DuplicateDataSet", "Action", "UserName", "Time", "Validity",
						"forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * ,(CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi from DATA_QUALITY_Transactionset_sum_dgroup where Date between '"
						+ fromDate + "' AND '" + toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate
						+ "' AND Run =" + maxRun + ") AND Validity = 'True' " + colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * ,(CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi from DATA_QUALITY_Transactionset_sum_dgroup where Date ='" + maxDate
						+ "' AND Run= '" + maxRun + "' AND idApp=" + idApp + " AND Validity = 'True' " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQRecordCountAnomalyDgroupResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Transactionset_sum_dgroup", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

	public JSONObject getDQCustomDistributionResults(JSONObject requestObj) {
		JSONObject essentialChkResultsObj = new JSONObject();

		try {
			long idApp = requestObj.getLong("idApp");
			String fromDate = requestObj.getString("fromDate");
			String toDate = requestObj.getString("toDate");
			String colName = requestObj.getString("colName");
			String dGroupVal = requestObj.getString("dGroupVal");
			String historicFetch = "N";
			try {
				historicFetch = requestObj.getString("historicFetch");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.error("Syntax issue in retrieving historicFetch");
			}

			// should be master search term
			String GLOBAL_SEARCH_TERM = requestObj.getString("globalSearchTerm");
			String maxDate = "";
			String maxRun = "";
			List<String> globalSearchColsList = null;
			String globleSearch = "";

			if (GLOBAL_SEARCH_TERM != null && !GLOBAL_SEARCH_TERM.trim().isEmpty()) {
				globleSearch = " AND (";
				globalSearchColsList = Arrays.asList("Date", "Run", "dayOfYear", "month", "dayOfMonth", "dayOfWeek",
						"hourOfDay", "ColName", "Count", "Min", "Max", "Cardinality", "Std_Dev", "Mean", "Null_Value",
						"Status", "StringCardinalityAvg", "StringCardinalityStdDev", "StrCardinalityDeviation",
						"String_Threshold", "String_Status", "NumMeanAvg", "NumMeanStdDev", "NumMeanDeviation",
						"NumMeanThreshold", "NumMeanStatus", "NumSDAvg", "NumSDStdDev", "NumSDDeviation",
						"NumSDThreshold", "NumSDStatus", "outOfNormStatStatus", "sumOfNumStat", "NumSumAvg",
						"NumSumStdDev", "NumSumThreshold", "dGroupVal", "dGroupCol", "dataDriftCount",
						"dataDriftStatus", "Default_Value", "Default_Count", "Record_Count", "Null_Percentage",
						"Default_Percentage", "Null_Threshold", "Default_Threshold", "forgot_run_enabled");

				for (String col : globalSearchColsList)
					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						globleSearch = globleSearch + col + "::text like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}else {
						globleSearch = globleSearch + col + " like '%" + GLOBAL_SEARCH_TERM + "%' OR ";
					}

				if (!globleSearch.trim().isEmpty()) {
					globleSearch = globleSearch.substring(0, globleSearch.lastIndexOf("OR"));
					globleSearch = globleSearch + ")";
				}
			}

			String colNameQuery = "";
			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {

				maxDate = requestObj.getString("maxDate");
				maxRun = requestObj.getString("maxRun");

				if (colName != null && !colName.trim().isEmpty()) {
					colNameQuery = colNameQuery + " AND ColName='" + colName + "' ";
				}
				if (dGroupVal != null && !dGroupVal.trim().isEmpty())
					colNameQuery = colNameQuery + " AND dGroupVal='" + dGroupVal + "' ";

			} else {
				Map<String, String> dateRunMap = paginationDAO.getMaxRunAndDateByQuery(fromDate, toDate, idApp);

				maxDate = dateRunMap.get("maxDate");
				maxRun = dateRunMap.get("maxRun");
			}

			String sql = "";
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);

			if (historicFetch != null && historicFetch.trim().equalsIgnoreCase("Y")) {
				sql = "Select * , CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numSumStatus, (CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from DATA_QUALITY_Custom_Column_Summary where Date between '"
						+ fromDate + "' AND '" + toDate + "' AND idApp=" + idApp + " AND  NOT(Date='" + maxDate
						+ "' AND Run =" + maxRun + ") " + colNameQuery + " " + globleSearch;
			} else {
				sql = "Select * , CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numSumStatus, (CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from DATA_QUALITY_Custom_Column_Summary where Date ='"
						+ maxDate + "' AND Run= '" + maxRun + "' AND idApp=" + idApp + " " + globleSearch;
			}
			long fetchLimit = databuckUtility.getResultsFetchLimit();
			sql = sql + " order by Id desc limit " + fetchLimit;

			JSONArray valCheckResultArr = paginationDAO.getDQCustomDistributionResults(sql);

			if (valCheckResultArr == null || valCheckResultArr.length() <= 0) {
				valCheckResultArr = new JSONArray();
			}
			essentialChkResultsObj.put("DATA_QUALITY_Custom_Column_Summary", valCheckResultArr);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		return essentialChkResultsObj;
	}

}
