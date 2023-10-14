package com.databuck.restcontroller;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.*;
import com.databuck.dao.impl.MatchingResultDaoImpl;
import com.databuck.econstants.DeltaType;
import com.databuck.econstants.RuleActionTypes;
import com.databuck.econstants.TemplateRunTypes;
import com.databuck.service.*;
import com.databuck.util.ToCamelCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class DataTemplateProfilingRestController {

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;

    @Autowired
    private IProjectService projService;

    @Autowired
    private IDashboardConsoleDao dashboardConsoleDao;

    @Autowired
    private DataProfilingDetailsService dataProfilingDetailsService;

    @Autowired
    private IListDataSourceDAO listdatasourcedao;

    @Autowired
    private IExtendTemplateRuleDAO extendTemplateDao;

    @Autowired
    private RuleCatalogService ruleCatalogService;

    @Autowired
    private ITemplateViewDAO templateViewDAO;

    @Autowired
    public IResultsDAO iResultsDAO;

    @Autowired
    private IProjectDAO iProjectDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    DataProfilingTemplateService dataProilingTemplateService;

    @Autowired
    private RBACController rbacController;

    @Autowired
    public ITaskService iTaskService;

    @Autowired
    IUserDAO userDAO;
    @Autowired
    MatchingResultDaoImpl matchingResultDao;
    @Autowired
    private Properties appDbConnectionProperties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Properties resultDBConnectionProperties;

    @Autowired
    private IImportExportUtilityDAO importExportDao;

    @Autowired
    private ITaskDAO iTaskDAO;

    @Autowired
    IValidationCheckDAO validationcheckdao;

    @Autowired
    ITemplateViewDAO templateviewdao;

    @Autowired
    SchemaDAOI schemaDao;

    @Autowired
    IDataTemplateAddNewDAO dataTemplateAddNewDAO;

    @Autowired
    private ChecksCSVService csvService;

    @Autowired
    private LoginService loginService;

	@Autowired
	DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

    private static final Logger LOG = Logger.getLogger(DataTemplateProfilingRestController.class);

    @RequestMapping(value = "/dbconsole/refDataColumn", method = RequestMethod.POST)
    public ResponseEntity<String> refDataColumn(@RequestHeader HttpHeaders headers, @RequestBody String jsonStr) {
	LOG.info("dbconsole/refDataColumn - START");

	JSONObject json = new JSONObject();
	JSONArray result = new JSONArray();
	String status = "failed";
	String message = "";
	String token = "";

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
			responseStatus = HttpStatus.EXPECTATION_FAILED;
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equals("success")) {
		    // get idData and table name from input json
			LOG.debug("Getting request parameters  " + jsonStr);
		    JSONObject inputObj = new JSONObject(jsonStr);
		    long idData = inputObj.getLong("idData");
		    String templateName = validationcheckdao.getNameFromListDataSources(idData);
		    if (templateName != null && !templateName.isEmpty()) {
			List<String> tableColumnNames = validationcheckdao
				.getDisplayNamesFromListDataDefinitionForReftables(idData);

			if (tableColumnNames != null && tableColumnNames.size() > 0) {
			    json.put("result", tableColumnNames);
			    status = "success";
			    message = "Table Column Names Fetched Successfully";
			    LOG.info(message);
			} else {
			    message = "Failed to fetch Table Names";
				LOG.error(message);
			}
		    } else {
			message = "Invalid idData";
		    LOG.error(message);
		    }
		} else {
		    message = "Token expired.";
			LOG.error(message);
			responseStatus = HttpStatus.EXPECTATION_FAILED;
		}
	    } else {
		message = "Token is missing in headers";
	    LOG.error(message);
			responseStatus = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/refDataColumn - END");
	return new ResponseEntity<String>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/referenceTable", method = RequestMethod.POST)
    public ResponseEntity<String> referenceTable(@RequestHeader HttpHeaders headers, @RequestBody String jsonStr) {
	LOG.info("dbconsole/referenceTable - START");

	JSONObject json = new JSONObject();
	JSONArray result = new JSONArray();
	String status = "failed";
	String message = "";
	String token = "";

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equals("success")) {
		    // get idData and table name from input json
			LOG.debug("Getting request parameters  " + jsonStr);
		    JSONObject inputObj = new JSONObject(jsonStr);
		    long idData = inputObj.getLong("idData");
		    String tableName = inputObj.getString("tableName");
		    List<String> columnNames = matchingResultDao.getColumnNamesByMetadata(tableName);

		    String templateName = validationcheckdao.getNameFromListDataSources(idData);
		    if (templateName != null && !templateName.isEmpty()) {

			if (tableName != null && !tableName.trim().isEmpty() && columnNames != null
				&& columnNames.size() > 0) {
			    // fetch template list
			    JSONArray tableData = validationcheckdao.getAllDataAsKeyValueByTableName(tableName,
				    new JSONArray(columnNames));
			    List<String> columnHeaders = matchingResultDao.getColumnNamesByMetadataWithType(tableName);
			    if (tableData != null && tableData.length() > 0) {
				json.put("headers", columnHeaders);
				json.put("data", tableData);
				status = "success";
				message = "Table Data Fetched Successfully";
				LOG.info(message);
			    } else {
				message = "Failed to fetch Table Data";
			    LOG.error(message);
			    }
			} else {
			    message = "Incorrect table name ";
				LOG.error(message);
			}
		    } else {
			message = "Invalid idData";
		    LOG.error(message);
		    }
		} else {
		    message = "Token expired.";
		    LOG.error(message);
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		}

	    } else {
		message = "Token is missing in headers";
		LOG.error(message);
		responseStatus = HttpStatus.EXPECTATION_FAILED;

	    }
	} catch (Exception e) {
	    message = "Failed to fetch Table Data";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/referenceTable - END");
	return new ResponseEntity<String>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/updateRefData", method = RequestMethod.POST)
    public ResponseEntity<Object> updateRefData(HttpServletRequest request, HttpServletResponse response,
	    @RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
    	LOG.info("dbconsole/updateRefData - START");
    JSONObject json = new JSONObject();
	String status = "failed";
	String message = "";
	String token = "";

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equals("success")) {
			LOG.debug("Getting request parameters  " + inputsJsonStr);
		    JSONObject inputJson = new JSONObject(inputsJsonStr);
		    Long row_id = inputJson.getLong("row_id");
		    String col_name = inputJson.getString("col_name");
		    String update_val = inputJson.getString("update_val");
		    String table_name = inputJson.getString("table_name");
		    Long idData = inputJson.getLong("idData");

		    String updateVal = iResultsDAO.updateRefTableData(row_id, col_name, update_val, table_name);
		    if (updateVal != null && updateVal.trim().equalsIgnoreCase("True")) {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			Long idUser = userToken.getIdUser();

			// changes regarding Audit trail
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
				DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCESVIEW, formatter.format(new Date()), idData,
				DatabuckConstants.ACTIVITY_TYPE_EDITED, table_name);

			status = "success";
			//message = "updated successfully";
			message = "Record updated successfully";
			LOG.info(message);
		    } else {
			//message = "Failed to update";
		    message = "Enter valid records";
			
		    LOG.error(message);
		    }
		} else {
		    message = "Token expired.";
		    LOG.error(message);
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		}

	    } else {
		message = "Token is missing in headers";
	    LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/updateRefData - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getTemplateProfilingSummary", method = RequestMethod.GET)
    public ResponseEntity<String> getTemplateProfilingSummary(@RequestHeader HttpHeaders headers,
	    @RequestParam long idData) {
    	LOG.info("dbconsole/getTemplateProfilingSummary - START");
	String msg = "Results data successfully build";

	String status = "failed";
	JSONObject profileResultData = new JSONObject();
	JSONObject apiResponse = new JSONObject();
	String templateName = "";

	String token = "";
	HttpStatus responseStatus = HttpStatus.NOT_FOUND;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null || !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
		    try {

			// Get Template Name

			List<ListDataSource> listApplicationsData = listdatasourcedao.getListDataSourceTableId(idData);

			for (ListDataSource ld : listApplicationsData) {
			    templateName = ld.getName();
			}

			// for RowProfile_DP
			List<RowProfile_DP> rowProfileList = listdatasourcedao.readRowProfileForTemplate(idData);

			// for NumericalProfile_DP
			List<NumericalProfile_DP> numericProfileList = listdatasourcedao
				.readNumericProfileForTemplate(idData);

			// for ColumnProfileDetails_DP
			List<ColumnProfileDetails_DP> columnProfileDetailsList = listdatasourcedao
				.readColumnProfileDetailsForTemplate(idData);

			// for ColumnProfile
			List<ColumnProfile_DP> precolumnProfileList = listdatasourcedao
				.readColumnProfileForTemplate(idData);
			List<ColumnProfile_DP> newColumnProfileList = dataProfilingDetailsService
				.getNewColumnsDelta(idData, precolumnProfileList);
			List<ColumnProfile_DP> missingColumnProfileList = dataProfilingDetailsService
				.getMissingColumnsDelta(idData, precolumnProfileList);
			List columnProfileList = precolumnProfileList;

			if (precolumnProfileList != null && precolumnProfileList.size() > 0) {
			    List<ColumnProfileDelta_DP> deltaList = dataProfilingDetailsService
				    .getColumnProfileDeltaProcess(idData, precolumnProfileList);
			    if (deltaList != null && deltaList.size() > 0) {
				columnProfileList = deltaList;
			    }
			}

			// for Column Combination Profile Headers Reading
			List<ColumnCombinationProfile_DP> columnCombinationProfileList = listdatasourcedao
				.readColumnCombinationProfileForTemplate(idData);

			profileResultData.put("idData", idData);
			profileResultData.put("templateName", templateName);

			JSONArray rowProfileResult = new JSONArray(rowProfileList);
			profileResultData.put("rowProfileList", rowProfileResult);

			JSONArray numericProfileResult = new JSONArray(numericProfileList);
			profileResultData.put("numericProfileList", numericProfileResult);

			JSONArray columnProfileDetailsResult = new JSONArray(columnProfileDetailsList);
			profileResultData.put("columnProfileDetailsList", columnProfileDetailsResult);

			JSONArray newColumnProfileResult = new JSONArray(newColumnProfileList);
			profileResultData.put("newColumnProfileList", newColumnProfileResult);

			JSONArray missingColumnProfileResult = new JSONArray(missingColumnProfileList);
			profileResultData.put("missingColumnProfileList", missingColumnProfileResult);

			JSONArray columnProfileListResult = new JSONArray(columnProfileList);
			profileResultData.put("columnProfileList", columnProfileListResult);

			JSONArray columnCombinationProfileResult = new JSONArray(columnCombinationProfileList);
			profileResultData.put("columnCombinationProfileList", columnCombinationProfileResult);

			if (profileResultData != null && profileResultData.length() > 0) {
			    responseStatus = HttpStatus.OK;
			    status = "success";
			    LOG.info("success");
			} else {
			    msg = "Invalid idData/template id";
				LOG.error(msg);
			}
		    } catch (Exception e) {
			e.printStackTrace();
			msg = "Invalid idData/template id";
			LOG.error(msg);
		    }

		} else {
		    msg = tokenStatusObj.getString("msg");
		    LOG.error(msg);
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		}
	    } else {
		msg = "Token is missing in headers";
	    LOG.error(msg);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	    msg = "Request failed";
	}

	apiResponse.put("result", profileResultData);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);
	
	return new ResponseEntity<>(apiResponse.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getTemplateProfilingSummary", method = RequestMethod.POST)
    public ResponseEntity<Object> getTemplateProfilingSummaryByTable(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestParam) {
    	LOG.info("dbconsole/getTemplateProfilingSummary - START");
	Map<String, Object> response = new HashedMap<String, Object>();
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null && !token.isEmpty()) {
		    // validate received token
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			String templateName = "";
			List<ListDataSource> listApplicationsData = listdatasourcedao.getListDataSourceTableId(idData);
			for (ListDataSource ld : listApplicationsData) {
			    templateName = ld.getName();
			}
			response.put("idData", idData);
			response.put("templateName", templateName);
			if (!requestParam.containsKey("filterAttribute")) {
			    List<ColumnProfile_DP> precolumnProfileList = new ArrayList<ColumnProfile_DP>();
			    if ("newColumnProfileList".equals(profileName)
				    || "missingColumnProfileList".equals(profileName)
				    || "columnProfileList".equals(profileName)) {
				precolumnProfileList = listdatasourcedao.readColumnProfileForTemplate(idData);
			    }
			    switch (profileName) {
			    case "columnCombinationProfileList":
				response.put("columnCombinationProfileList", getUpdatedList(
					listdatasourcedao.readColumnCombinationProfileForTemplate(idData)));
				break;
			    case "numericProfileList":
				response.put("numericProfileList",
					getUpdatedList(listdatasourcedao.readNumericProfileForTemplate(idData)));
				break;
			    case "columnProfileDetailsList":
				response.put("columnProfileDetailsList",
					getUpdatedList(listdatasourcedao.readColumnProfileDetailsForTemplate(idData)));
				break;
			    case "rowProfileList":
				response.put("rowProfileList",
					getUpdatedList(listdatasourcedao.readRowProfileForTemplate(idData)));
				break;
			    case "newColumnProfileList":
				response.put("newColumnProfileList", getUpdatedList(
					dataProfilingDetailsService.getNewColumnsDelta(idData, precolumnProfileList)));
				break;
			    case "missingColumnProfileList":
				response.put("missingColumnProfileList", getUpdatedList(dataProfilingDetailsService
					.getMissingColumnsDelta(idData, precolumnProfileList)));
				break;
			    case "columnProfileList":
				List columnProfileList = new ArrayList<>();
				List<ColumnProfile_DP> newColumnsDelta = dataProfilingDetailsService.getNewColumnsDelta(idData, precolumnProfileList);
				List<ColumnProfile_DP> missingColumnsDelta = dataProfilingDetailsService.getMissingColumnsDelta(idData, precolumnProfileList);
				List newColumnProfileList = updateList(newColumnsDelta);
				List missingColumnProfileList = updateList(missingColumnsDelta);
				if (precolumnProfileList != null && precolumnProfileList.size() > 0) {
				    List<ColumnProfileDelta_DP> deltaList = dataProfilingDetailsService
					    .getColumnProfileDeltaProcessRest(idData, precolumnProfileList);
				    if (deltaList != null && deltaList.size() > 0) {
						columnProfileList = deltaList;
				    } else {
						columnProfileList = updateList(dataProfilingDetailsService.getColumnProfileFirstRunResult(idData, precolumnProfileList));
				    }
					response.put("newColumnProfileList", getUpdatedList(newColumnProfileList));
					response.put("missingColumnProfileList",getUpdatedList(missingColumnProfileList));
				}
				response.put("columnProfileList", getUpdatedList(columnProfileList));
				break;
			    }
			} else {
			    String profileTable = requestParam.get("profileTable");
			    response.put(profileName, dataProfilingDetailsService.findProfilingByFilter(idData,
				    requestParam.get("filterAttribute"), profileName));
			}
		    } else {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
		    }
		} else {
		    response.put("message", "Auth token is required");
		    LOG.error("Auth token is required");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
		}
	    } else {
		response.put("message", "Please provide valid app id and profile name");
		LOG.error("Please provide valid app id and profile name");
		response.put("status", "failed");
		response.put("result", new HashMap<String, Object>());
		
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	    }
	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("result", response);
	    result.put("status", "success");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	} catch (Exception e) {
	    response.put("message", "Please provide valid app id and profile name");
	    response.put("status", "failed");
	    response.put("result", new HashMap<String, Object>());
	    LOG.info("dbconsole/getTemplateProfilingSummary - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/getAdvanceRules", method = RequestMethod.POST)
    public ResponseEntity<Object> getAdvanceRules(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
    	LOG.info("dbconsole/getAdvanceRules - START");
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	Map<String, Object> response = new HashedMap<String, Object>();
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
	    	LOG.debug("Getting request parameters  " + inputsJsonStr);
		Long idData = inputJson.getLong("idData");
		if (idData != null && idData != 0) {
		    response.put("advanceRules", listdatasourcedao.getAdvancedRulesForId(idData));
		} else {
			LOG.error("Provide projectIds.");
		    throw new Exception("Provide projectIds.");
		}
	    } else {
		response.put("message", "Token is expired.");
		LOG.error("Token is expired.");
		
		return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
	    }

	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("result", response);
	    result.put("status", "success");
	    LOG.info("success to get advanced rules");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", "Please provide valid id data");
	    LOG.error("Please provide valid id data");
	    response.put("status", "failed");
	    response.put("result", new HashMap<String, Object>());
	    LOG.info("dbconsole/getAdvanceRules - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/advanceRulesForChecks", method = RequestMethod.POST)
    public ResponseEntity<Object> getAdvanceRulesForChecks(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
    	LOG.info("dbconsole/advanceRulesForChecks - START");
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	Map<String, Object> response = new HashedMap<String, Object>();
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
	    	LOG.debug("Getting request parameters  " + inputsJsonStr);
		Long idData = inputJson.getLong("idData");
		String checkType = inputJson.getString("checkType");
		JSONObject filterAtrr = inputJson.getJSONObject("filterAttribute");
		if (idData != null && idData != 0) {
		    response.put("advanceRules",
			    listdatasourcedao.getAdvancedRulesForChecks(idData, checkType, filterAtrr));
		} else {
			LOG.error("Provide projectIds.");
		    throw new Exception("Provide projectIds.");
		}
	    } else {
		response.put("message", "Token is expired.");
		LOG.error("Token is expired.");
		
		return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
	    }

	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("result", response);
	    result.put("status", "success");
	    LOG.info("success to get advanced rules for checks");
	    
	    return new ResponseEntity<Object>(result, HttpStatus.OK);
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", "Please provide valid id data");
	    LOG.error("Please provide valid id data");
	    response.put("status", "failed");
	    response.put("result", new HashMap<String, Object>());
	    LOG.info("dbconsole/getAlertEventById - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/activateAdvancedRuleById", method = RequestMethod.POST)
    public ResponseEntity<Object> activateAdvancedRuleById(@RequestHeader HttpHeaders headers,
	    @RequestBody List<ActivateRuleReq> rules) {
    	LOG.info("dbconsole/activateAdvancedRuleById - START");
	Map<String, Object> response = new HashMap<>();
	// JSONObject inputJson = new JSONObject(inputsJsonStr);
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
//					long idData = inputJson.getLong("idData");
//					long ruleId = inputJson.getLong("ruleId");
//					String ruleSql = inputJson.getString("ruleSql");
//					String columnName = inputJson.getString("columnName");
//					String ruleType = inputJson.getString("ruleType");
//					String createdByUser = inputJson.getString("createdByUser");

		    // Get project id & domain of left DataTemplate from listDataSources
		    if (rules != null && !rules.isEmpty()) {
		    	LOG.debug("Getting request parameters  " + rules);
			for (ActivateRuleReq ruleReq : rules) {
			    ListDataSource lds = listdatasourcedao
				    .getDataFromListDataSourcesOfIdData(ruleReq.getIdData());
			    Integer domainId = lds.getDomain();
			    Long projectId = (long) lds.getProjectId();

			    // Create a custom rule
			    ListColRules listColRules = new ListColRules();

			    String ruleName = ruleReq.getRuleId() + "_" + ruleReq.getRuleType() + "_"
				    + ruleReq.getColumnName();

			    LOG.debug("Creating new custom rule :" + ruleName);

			    LOG.debug("ruleSql: " + ruleReq.getRuleSql());

			    listColRules.setIdData(ruleReq.getIdData());
			    listColRules.setIdRightData(ruleReq.getIdData());
			    listColRules.setRuleName(ruleName);
			    listColRules.setExpression(ruleReq.getRuleSql());
			    listColRules.setMatchingRules("");
			    listColRules.setRuleType("Referential");
			    listColRules.setExternal("N");
			    listColRules.setCreatedByUser(ruleReq.getCreatedByUser());
			    listColRules.setProjectId(projectId);
			    listColRules.setDomainId(domainId);
			    listColRules.setIdDimension(0l);
			    listColRules.setAnchorColumns(ruleReq.getColumnName());
			    extendTemplateDao.insertintolistColRules(listColRules);

			    long idListColrules = extendTemplateDao.getCustomRuleByName(ruleName);

			    if (idListColrules > 0l) {
				// Update the status to Y and set the custom rule id to Advanced rule
				listdatasourcedao.updateAdvancedRulesActiveStatus(ruleReq.getRuleId(), "Y",
					idListColrules);

				// When RuleCatalog is enabled, We need to add this new rule in all the
				// associated rule catalog
				boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

				if (isRuleCatalogEnabled) {
					LOG.info(
					    "Adding the new custom rule in all the Associated validations RuleCatalogs");
				    CompletableFuture.runAsync(() -> {
					ruleCatalogService.updateAssociatedRuleCatalogsForCustomRuleChange(
						idListColrules, RuleActionTypes.CREATE);
				    });
				}

				// json.put("success", "Extended Template Rule for AdvancedRule is created
				// successfully");
				response.put("message",
					"Auto Discovered Rule is Activated successfully and Rule is created in Extend Template and Rule");
				response.put("status", "success");
				LOG.info("Auto Discovered Rule is Activated successfully and Rule is created in Extend Template and Rule");

			    } else {
			    	LOG.debug("Failed to create custom rule for advancedRule with Id:"
					+ ruleReq.getRuleId());

				response.put("message", "Extended Template Rule creation for AdvancedRule is failed");
				response.put("status", "failed");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			    }
			}
		    }
		} else {
		    response.put("message", "Token expired");
		    response.put("status", "failed");
		    LOG.error("Token expired");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    }
	} catch (Exception e) {
	    LOG.error("Exception occurred while activating advancedRule");
	    e.printStackTrace();

	    try {
		response.put("message", "Extended Template Rule creation for AdvancedRule is failed");
		response.put("status", "failed");
		LOG.error("Extended Template Rule creation for AdvancedRule is failed");
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	    } catch (Exception e1) {
	    LOG.error(e.getMessage());
		e1.printStackTrace();
	    }

	}
	LOG.info("dbconsole/activateAdvancedRuleById - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/deactivateAdvancedRuleById", method = RequestMethod.POST)
    public ResponseEntity<Object> deactivateAdvancedRuleById(@RequestHeader HttpHeaders headers,
	    @RequestBody List<Map<String, Long>> requestParam) {
    	LOG.info("dbconsole/deactivateAdvancedRuleById - START");
	Map<String, Object> response = new HashMap<String, Object>();
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
		    if (requestParam != null && !requestParam.isEmpty()) {
		    	LOG.debug("Getting request parameters  " + requestParam);
			for (Map<String, Long> reqMap : requestParam) {
			    long ruleId = reqMap.get("ruleId");
			    long idListColrules = reqMap.get("idListColrules");
			    if (idListColrules > 0l && ruleId > 0l) {
				/*
				 * When rule catalog is enabled, deactivate the rule and delete it from all the
				 * associated rule catalogs
				 *
				 * When rule catalog is disabled, delete it from table
				 */
				boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
				if (isRuleCatalogEnabled) {
				    CompletableFuture.runAsync(() -> {
					ruleCatalogService.updateAssociatedRuleCatalogsForCustomRuleChange(
						idListColrules, RuleActionTypes.DELETE);
				    });
				} else {
				    LOG.info("Deleting the rule from the table");
				    // Delete the custom rule
				    templateViewDAO.deleteIdListColRulesData(idListColrules);
				}
				// Update the status to Y and set the custom rule id to Advanced rule
				listdatasourcedao.updateAdvancedRulesActiveStatus(ruleId, "N", null);
				// json.put("success", "AdvancedRule is deactivated successfully");
				response.put("message", "Auto Discovered Rule Activated Successfully");
				response.put("status", "success");
				LOG.info("Auto Discovered Rule Activated Successfully");
			    }

			}
		    }
		} else {
		    response.put("message", "Token expired");
		    response.put("status", "failed");
		    LOG.error("Token expired");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("message", "Please provide the token");
		response.put("status", "failed");
		LOG.error("Please provide the token");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    LOG.error("Exception occurred while deactivating advancedRule");
	    LOG.error(e.getMessage());
	    e.printStackTrace();

	    try {
		response.put("message", "Deactivation of AdvancedRule is failed");
		response.put("status", "failed");
		LOG.error("Deactivation of AdvancedRule is failed");
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	    } catch (Exception e1) {
	    LOG.error(e.getMessage());
		e1.printStackTrace();
	    }

	}
	LOG.info("dbconsole/deactivateAdvancedRuleById - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/rejectInd", method = RequestMethod.POST)
    public ResponseEntity<Object> rejectInd(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> request) {
    	LOG.info("dbconsole/rejectInd - START");
	Map<String, Object> response = new HashMap<String, Object>();
	String colName = request.get("colName");
	String tableName = request.get("tableName");
	String uniqueValues = request.get("uniqueValues");
	String dGroupVal = request.get("dGroupVal");
	String dGroupCol = request.get("dGroupCol");
	String Run = request.get("run");
	String idApp = request.get("idApp");
	String userName = request.get("userName");
	String tab = request.get("tab");
	String date = request.get("date");
	try {
	    String token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	    if (token != null && !token.isEmpty()) {
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
			LOG.debug("Getting request parameters  " + request);
		    String isRejected = "false";
		    String msg = "Rejected status updated successfully.";
		    if (tab.equals("drift")) {
			isRejected = iResultsDAO.updaterejectIndDrift(tableName, userName, Run, uniqueValues, colName,
				dGroupVal, dGroupCol);
		    }
		    if (tab.equals("validity")) {
			isRejected = iResultsDAO.updaterejectIndValidity(tableName, dGroupCol, Run, dGroupVal, date,
				userName, idApp);
			if (request.containsKey("checkName")
				&& request.get("checkName").equalsIgnoreCase("interColumn")) {
			    msg = "The record has been accepted successfully.";
			}
		    }
		    if (tab.equals("GBRCA")) {
			isRejected = iResultsDAO.updaterejectIndGBRCA(tableName, dGroupCol, Run, dGroupVal, date,
				userName, idApp);
		    }
		    if (!isRejected.isEmpty() && isRejected.equalsIgnoreCase("True")) {
			response.put("message", msg);
			response.put("status", "success");
			LOG.info(msg);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		    } else {
			response.put("message", "Failed to update status");
			response.put("status", "failed");
			LOG.error("Failed to update status");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		    }
		} else {
		    response.put("message", "Failed to update status");
		    response.put("status", "failed");
		    LOG.error("Failed to update status");
			
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	    } else {
		response.put("message", "Token expired");
		response.put("status", "failed");
		LOG.error("Token expired");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", e.getMessage());
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/rejectInd - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/undoRejectInd", method = RequestMethod.POST)
    public ResponseEntity<Object> undoRejectInd(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> request) {
    	LOG.info("dbconsole/undoRejectInd - START");
	Map<String, Object> response = new HashMap<String, Object>();
	try {
	    String colName = request.get("colName");
	    String tableName = request.get("tableName");
	    String uniqueValues = request.get("uniqueValues");
	    String dGroupVal = request.get("dGroupVal");
	    String dGroupCol = request.get("dGroupCol");
	    String Run = request.get("run");
	    String idApp = request.get("idApp");
	    String userName = request.get("userName");
	    String tab = request.get("tab");
	    String date = request.get("date");
	    String token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	    if (token != null && !token.isEmpty()) {
	    
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
			LOG.debug("Getting request parameters  " + request);
		    iResultsDAO.undoDataDriftRejectInd(tableName, userName, Run, uniqueValues, colName, dGroupVal,
			    dGroupCol);
		    response.put("message", "Un-rejected status updated successfully.");
		    response.put("status", "success");
		    LOG.info("Un-rejected status updated successfully.");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
		    response.put("message", "Token expired");
		    response.put("status", "failed");
		    LOG.error("Token expired");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("message", "Token missing");
		response.put("status", "failed");
		LOG.error("Token missing");
	    
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", e.getMessage());
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/undoRejectInd - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

    }

    @RequestMapping(value = "/dbconsole/rejectAll", method = RequestMethod.POST)
    public ResponseEntity<Object> rejectAll(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> request) {
    	LOG.info("dbconsole/rejectAll - START");
	Map<String, Object> response = new HashMap<String, Object>();
	String rejectAll = request.get("rejectAll");
	String tableName = request.get("tableName");
	String idApp = request.get("idApp");
	String userName = request.get("userName");
	String columnname = request.get("columnname");
	String tabName = request.get("tabName");
	try {
	    String token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	    if (token != null && !token.isEmpty()) {
	    	LOG.debug("Getting request parameters  " + request);
		int maxValue = 0;
		String dateUpdate = "";
		String isRejected = "";
		String maxQuery = "";

		if (tabName.equals("Drift")) {
		    maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="
			    + idApp + " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + ")";
		    LOG.debug("maxQuery   " + maxQuery);
		    SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
		    if (max.next()) {
			maxValue = max.getInt("maxRun");
			dateUpdate = max.getString("Date");
		    }
		    isRejected = iResultsDAO.updaterejectAllDrift(tableName, userName, maxValue, dateUpdate, idApp,
			    columnname);
		}

		if (tabName.equals("GBRCA")) {
		    maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="
			    + idApp + " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + ")";
		    SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
		    if (max.next()) {
			maxValue = max.getInt("maxRun");
			dateUpdate = max.getString("Date");
		    }
		    isRejected = iResultsDAO.updaterejectAllGBRCA(tableName, userName, maxValue, dateUpdate, idApp);
		}

		if (tabName.equals("Validity")) {
		    maxQuery = "Select MAX(Run) as maxRun, MAX(Date) as Date from " + tableName + " where idApp="
			    + idApp + " and Date=(select max(Date) from " + tableName + " where idApp=" + idApp + ")";
		    SqlRowSet max = jdbcTemplate1.queryForRowSet(maxQuery);
		    if (max.next()) {
			maxValue = max.getInt("maxRun");
			dateUpdate = max.getString("Date");
		    }
		    isRejected = iResultsDAO.updaterejectAllValidity(tableName, userName, maxValue, dateUpdate, idApp);
		}

		if (!isRejected.isEmpty() && isRejected.equalsIgnoreCase("True")) {
		    String msg = "Rejected status updated to all successfully.";
		    if (request.containsKey("checkName") && request.get("checkName").equalsIgnoreCase("interColumn")) {
			msg = "All the records have been accepted successfully.";
		    }
		    response.put("message", msg);
		    response.put("status", "success");
		    LOG.info(msg);
		    
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
		    response.put("message", "Failed to update status");
		    response.put("status", "failed");
		    LOG.error("Failed to update status");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	    } else {
		response.put("message", "Token expired");
		response.put("status", "failed");
		LOG.error("Token expired");
	    
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	    }
	} catch (Exception e) {
	    response.put("message", e.getMessage());
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/rejectAll - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    private Object getUpdatedList(List<?> profiles) {
	ObjectMapper oMapper = new ObjectMapper();
	if (profiles != null && profiles.size() > 0) {
	    List<Map<String, Object>> profileMaps = new ArrayList<Map<String, Object>>();
	    Map<String, String> convertedColMap = new HashMap<>();
	    for (Object obj : profiles) {
		Map<String, Object> map = oMapper.convertValue(obj, Map.class);
		Map<String, Object> data = new HashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    if (convertedColMap.containsKey(key)) {
			data.put(convertedColMap.get(key), value);
		    } else {
			String camCase = ToCamelCase.toCamelCase(key);
			convertedColMap.put(key, camCase);
			data.put(camCase, value);
		    }
		}
		profileMaps.add(data);
	    }
	    return profileMaps;
	}
	return new ArrayList<>();
    }

    @RequestMapping(value = "/dbconsole/getColumnProfileDetailsListCSV", method = RequestMethod.POST)
    public void getColumnProfileDetailsListCSV(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestParam, HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/getColumnProfileDetailsListCSV - START");
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null || !token.isEmpty()) {
		    // validate received token
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    	LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			if ("columnProfileDetailsList".equals(profileName)) {
			    List<ColumnProfileDetails_DP> profiles = listdatasourcedao
				    .readColumnProfileDetailsForTemplate(idData);
			    if (profiles != null && profiles.size() > 0) {
				httpResponse.setContentType("text/csv");
				String csvFileName = "ValidationReport.csv";
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				httpResponse.setHeader(headerKey, headerValue);
				ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					CsvPreference.STANDARD_PREFERENCE);
				String[] fields = { "ExecDate", "Run", "ColumnName", "ColumnValue", "Count",
					"Percentage" };
				String[] header = { "Execution Date", "Run", "Column Name", "Column Value", "Count",
					"% of Total" };
				csvWriter.writeHeader(header);
				for (ColumnProfileDetails_DP profile : profiles) {
				    csvWriter.write(profile, fields);
				}
				csvWriter.close();
				LOG.info("CSV file written successfully");
			    } else {
			    LOG.error("Records not found.");
				throw new Exception("Records not found.");
			    }
			} else {
				LOG.error("Profile name is not matching.");
			    throw new Exception("Profile name is not matching.");
			}

		    } else {
		    LOG.error("Token is missing.");
			throw new Exception("Token is missing.");
		    }
		} else {
			LOG.error("Auth token is required");
		    throw new Exception("Auth token is required");
		}
	    } else {
	    	LOG.error("Please provide valid app id and profile name");
		throw new Exception("Please provide valid app id and profile name");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getColumnProfileDetailsListCSV - END");
    }

    @RequestMapping(value = "/dbconsole/getRowProfileListCSV", method = RequestMethod.POST)
    public void getRowProfileListCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> requestParam,
	    HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/getRowProfileListCSV - START");
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null || !token.isEmpty()) {
		    // validate received token
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			if ("rowProfileList".equals(profileName)) {
			    List<RowProfile_DP> profiles = listdatasourcedao.readRowProfileForTemplate(idData);
			    if (profiles != null && profiles.size() > 0) {
				httpResponse.setContentType("text/csv");
				String csvFileName = "ValidationReport.csv";
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				httpResponse.setHeader(headerKey, headerValue);
				ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					CsvPreference.STANDARD_PREFERENCE);
				String[] fields = { "ExecDate", "Run", "Number_of_Columns_with_NULL",
					"Number_of_Records", "PercentageMissing" };
				String[] header = { "Execution Date", "Run", "# Of Column w/Nulls", "Number Of Records",
					"% Missing" };
				csvWriter.writeHeader(header);
				for (RowProfile_DP profile : profiles) {
				    csvWriter.write(profile, fields);
				}
				csvWriter.close();
				LOG.info("CSV file written successfully");
			    } else {
			    LOG.error("Records not found.");
				throw new Exception("Records not found.");
			    }
			} else {
				LOG.error("Profile name is not matching.");
			    throw new Exception("Profile name is not matching.");
			}

		    } else {
		    	LOG.error("Token is missing.");
			throw new Exception("Token is missing.");
		    }
		} else {
			LOG.error("Auth token is required");
		    throw new Exception("Auth token is required");
		}
	    } else {
	    LOG.error("Please provide valid app id and profile name");
		throw new Exception("Please provide valid app id and profile name");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getRowProfileListCSV - END");
    }

    @RequestMapping(value = "/dbconsole/getColumnProfileCSV", method = RequestMethod.POST)
    public void getColumnProfileCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> requestParam,
	    HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/getColumnProfileCSV - START");
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null || !"".equals(token)) {
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    	LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			if ("columnProfileList".equals(profileName)) {
			    List<ColumnProfile_DP> precolumnProfileList = listdatasourcedao
				    .readColumnProfileForTemplate(idData);
			    List<ColumnProfileDelta_DP> profiles = new ArrayList<ColumnProfileDelta_DP>();
			    if (precolumnProfileList != null && precolumnProfileList.size() > 0) {
				List<ColumnProfileDelta_DP> deltaList = dataProfilingDetailsService
					.getColumnProfileDeltaProcessRest(idData, precolumnProfileList);
				if (deltaList != null && deltaList.size() > 0) {
				    profiles = deltaList;
				} else {
				    profiles = updateListColumns(precolumnProfileList);
				}
			    }
			    if (profiles != null && profiles.size() > 0) {
				httpResponse.setContentType("text/csv");
				String csvFileName = "ColumnProfileReport.csv";
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				httpResponse.setHeader(headerKey, headerValue);
				ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					CsvPreference.STANDARD_PREFERENCE);
				String[] fields = { "ExecDate", "Run", "ColumnName", "DataType", "MissingValue",
					"PercentageMissing", "UniqueCount", "minLength", "maxLength", "mean", "stdDev",
					"min", "max", "percentile_99", "percentile_75", "percentile_25", "percentile_1",
					"DefaultPatterns" };
				String[] header = { "Execution Date", "Run", "Column Name", "Data Type",
					"Missing Values", "% Missing", "Unique Count", "Min Length", "Max Length",
					"Mean", "StdDev", "Min", "Max", "Percentile 99", "Percentile 75",
					"Percentile 25", "Percentile 1", "DefaultPatterns" };
				csvWriter.writeHeader(header);
				for (ColumnProfileDelta_DP profile : profiles) {
				    csvWriter.write(profile, fields);
				}
				csvWriter.close();
				LOG.info("CSV file written successfully");
			    } else {
			    	LOG.error("Records not found.");
				throw new Exception("Records not found.");
			    }
			} else {
				LOG.error("Profile name is not matching.");
			    throw new Exception("Profile name is not matching.");
			}

		    } else {
		    	LOG.error("Token is missing.");
			throw new Exception("Token is missing.");
		    }
		} else {
			LOG.error("Auth token is required");
		    throw new Exception("Auth token is required");
		}
	    } else {
	    	LOG.error("Please provide valid app id and profile name");
		throw new Exception("Please provide valid app id and profile name");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getColumnProfileCSV - END");
    }

    private List<ColumnProfileDelta_DP> updateListColumns(List<ColumnProfile_DP> columnProfileList) {
    	List<ColumnProfileDelta_DP> updatedColProfiles = new ArrayList<ColumnProfileDelta_DP>();
	if (columnProfileList.size() > 0) {
	    for (ColumnProfile_DP colProf : columnProfileList) {
		ColumnProfileDelta_DP colProfileDelta = new ColumnProfileDelta_DP();
		colProfileDelta.setExecDate(colProf.getExecDate());
		colProfileDelta.setRun(colProf.getRun());
		colProfileDelta.setColumnName(colProf.getColumnName());
		colProfileDelta.setDataType(colProf.getDataType());
		colProfileDelta.setTotalRecordCount("" + colProf.getTotalRecordCount());
		colProfileDelta.setMissingValue("" + colProf.getMissingValue());
		colProfileDelta.setPercentageMissing("" + colProf.getPercentageMissing());
		colProfileDelta.setUniqueCount(
			"" + (Double.valueOf(colProf.getUniqueCount()) / Double.valueOf(colProf.getTotalRecordCount()))
				* 100);
		colProfileDelta.setMinLength("" + colProf.getMinLength());
		colProfileDelta.setMaxLength("" + colProf.getMaxLength());
		colProfileDelta.setMean("" + colProf.getMean());
		colProfileDelta.setStdDev("" + colProf.getStdDev());
		colProfileDelta.setMin("" + colProf.getMin());
		colProfileDelta.setMax("" + colProf.getMax());
		colProfileDelta.setPercentile_99("" + colProf.getPercentile_99());
		colProfileDelta.setPercentile_75("" + colProf.getPercentile_75());
		colProfileDelta.setPercentile_25("" + colProf.getPercentile_25());
		colProfileDelta.setPercentile_1("" + colProf.getPercentile_1());
		colProfileDelta.setDefaultPatterns(colProf.getDefaultPatterns());
		updatedColProfiles.add(colProfileDelta);
	    }
	}
	return updatedColProfiles;
    }

    @RequestMapping(value = "/dbconsole/getColumnCombinationProfileListCSV", method = RequestMethod.POST)
    public void getTemplateProfilingSummaryCSV(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestParam, HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/getColumnCombinationProfileListCSV - START");
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null || !token.isEmpty()) {
		    // validate received token
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    	LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			if ("columnCombinationProfileList".equals(profileName)) {
			    List<ColumnCombinationProfile_DP> profiles = listdatasourcedao
				    .readColumnCombinationProfileForTemplate(idData);
			    if (profiles != null && profiles.size() > 0) {
				httpResponse.setContentType("text/csv");
				String csvFileName = "ValidationReport.csv";
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				httpResponse.setHeader(headerKey, headerValue);
				ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					CsvPreference.STANDARD_PREFERENCE);
				String[] fields = { "ExecDate", "Run", "Column_Group_Value", "Count", "Percentage" };
				String[] header = { "Execution Date", "Run", "Microsegment Value", "Count",
					"% of Total" };
				csvWriter.writeHeader(header);
				for (ColumnCombinationProfile_DP profile : profiles) {
					if(profile.getColumn_Group_Value()!=null && !profile.getColumn_Group_Value().trim().isEmpty())
						profile.setColumn_Group_Value(profile.getColumn_Group_Value().trim().replaceAll("\\?::\\?", ","));
					csvWriter.write(profile, fields);
				}
				csvWriter.close();
				LOG.info("CSV file written successfully");
			    } else {
			    	LOG.error("Records not found.");
				throw new Exception("Records not found.");
			    }
			} else {
				LOG.error("Profile name is not matching.");
			    throw new Exception("Profile name is not matching.");
			}

		    } else {
		    	LOG.error("Token is missing.");
			throw new Exception("Token is missing.");
		    }
		} else {
			LOG.error("Auth token is required");
		    throw new Exception("Auth token is required");
		}
	    } else {
	    	LOG.error("Please provide valid app id and profile name");
		throw new Exception("Please provide valid app id and profile name");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getColumnCombinationProfileListCSV - END");
    }

    @RequestMapping(value = "/dbconsole/createReferencesTemplate", method = RequestMethod.POST)
    public ResponseEntity<Object> createReferencesTemplate(@RequestHeader HttpHeaders headers,
	    @RequestParam("configFile") MultipartFile file, @RequestParam("idUser") Long idUser,
	    @RequestParam("projectId") Long projectId, @RequestParam("domainId") Integer nDomainId,
	    @RequestParam("referencename") String newTemplateName, HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/createReferencesTemplate - START");
	JSONObject oJsonResponse = new JSONObject();
	String status = "failed";
	String fileDownloadStatus = "failed";
	String fileDownloadMessage = "";
	String uniqueId = "";
	Long templateId = 0l;
	String newdataTemplateName = "";

	Map<String, Object> response = new HashMap<String, Object>();

	try {
	    List<String> token = headers.get("token");
	    LOG.debug("token "+token.toString());
	    if (token != null && !token.isEmpty()) {
		if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
		    LOG.info("referencesfile");
		    LOG.debug("referencesfile " + newTemplateName);

		    UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

		    String filename = file.getOriginalFilename();

		    String hostUri = System.getenv("DATABUCK_HOME") + "/referencesfile";

		    String path = hostUri + "/" + filename;

		    newdataTemplateName = "ref_" + newTemplateName;
		    String dataTemplateName = filename.split("\\.")[0];
		    if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

			oJsonResponse.put("msg", "successfully");

			// Check if the path exists
			File downloaded_file_dir = new File(hostUri);
			if (!downloaded_file_dir.exists()) {
			    downloaded_file_dir.mkdir();
			}

			// Download file to DATABUCK_HOME directory
			try (BufferedOutputStream stream = new BufferedOutputStream(
				new FileOutputStream(new File(path)));) {
			    byte[] bytes = file.getBytes();
			    stream.write(bytes);
			    fileDownloadStatus = "success";
			    oJsonResponse.put("msg", "successfully");

			} catch (Exception e) {
			    fileDownloadMessage = "\n====>Exception occurred while saving file to DATABUCK_HOME !!";
			    LOG.error("Exception occurred while saving file to DATABUCK_HOME");
			    e.printStackTrace();
			}

			// When file download is success create template
			if (fileDownloadStatus.equalsIgnoreCase("success")) {
			    oJsonResponse.put("msg", "successfully");

			    String activeDirectoryFlag = appDbConnectionProperties
				    .getProperty("isActiveDirectoryAuthentication");
			    String createdByUser = userDAO.getUserNameByUserId(idUser);

			    LOG.debug("Created by in createTemplateForQuickStart " + createdByUser);
			    // -------------------------file read --------------------------------//
			    Map<String, String> metadata = new LinkedHashMap<String, String>();
			    List<String> columnNamesList = new ArrayList<String>();
			    List<String> datatypes = new ArrayList<String>();
			    datatypes.add("int");
			    datatypes.add("char");
			    datatypes.add("long");
			    datatypes.add("float");
			    datatypes.add("double");
			    datatypes.add("varchar");
			    datatypes.add("text");
			    datatypes.add("string");
			    datatypes.add("date");
			    datatypes.add("number");

			    String numberFormat = appDbConnectionProperties.getProperty("number.Format");
			    String numberRegex = "";
			    if (numberFormat.equalsIgnoreCase("US")) {
				numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				oJsonResponse.put("msg", "successfully");
			    } else {
				numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
			    }
			    String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate")
				    .split(",");
			    String createColumns = "";
			    String insertColumns = "";
			    String insertValues = "";
			    ArrayList<String> dateRegexFormate = new ArrayList<String>();
			    for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
				dateRegexFormate.add(dateRegex[regIdx].trim());
			    }

			    try {
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				String line = br.readLine();
				String dataLine = br.readLine();
				oJsonResponse.put("msg", "successfully");
				if (line != null) {
				    boolean isJSON = false;
				    String[] dataValues = {};
				    String[] headerValues = {};
				    Map<String, String> jsonHM = new HashMap<String, String>();

				    String splitBy = "\\,";

				    if (!isJSON) {
					oJsonResponse.put("msg", "successfully");
					// This needs to be sorted
					if (line != null && dataLine != null) {
					    dataValues = dataLine.split(splitBy, -1);
					    headerValues = line.split(splitBy);
					}

					// if dataline is not provided, initiallize to string
					if (line != null && dataLine == null) {
					    headerValues = line.split(splitBy);
					    dataValues = new String[headerValues.length];
					    for (int i = 0; i < headerValues.length; i++) {
						dataValues[i] = "";
					    }
					}
				    } else {
					headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
					dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
				    }

				    for (int i = 0; i < headerValues.length; i++) {
					String word = headerValues[i];
					String columnName = headerValues[i];
					String dataValue = dataValues[i];
					String columnType = "String";

					int first = word.lastIndexOf("(");
					int last = word.lastIndexOf(")");

					boolean isColumnTypePresent = false;
					if (first != -1 && last != -1) {
					    isColumnTypePresent = true;
					    if (datatypes
						    .contains(word.substring(first + 1, last).trim().toLowerCase())) {
						columnType = word.substring(first + 1, last).trim();
						columnName = word.substring(0, first);
					    } else {
						columnName = word.substring(0, last);
					    }
					} else {
					    columnName = word;
					}

					// Determine column type based on first row. This will
					// be done only if
					// column type is not already specified in csv.
					if (!isColumnTypePresent) {
					    boolean isColumnTypeIdentified = false;
					    if (dataValue.matches(numberRegex)) {
						isColumnTypeIdentified = true;
						columnType = "number";
					    } else {
						for (int j = 0; j < dateRegexFormate.size(); j++) {
						    String regex = dateRegexFormate.get(j).trim();
						    if (dataValue.matches(regex) || (dataValue.length() >= 10
							    && dataValue.substring(0, 10).trim().matches(regex))) {
							columnType = "Date";
							isColumnTypeIdentified = true;
						    }
						}
					    }

					    if (!isColumnTypeIdentified) {
						columnType = "varchar";
					    }
					}

					columnName = columnName.trim();
					String modifiedColumn = "";
					String[] charArray = columnName.split("(?!^)");
					for (int j = 0; j < charArray.length; j++) {
					    if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

						modifiedColumn = modifiedColumn + charArray[j];
					    }
					}

					modifiedColumn = modifiedColumn.replace("-", "_");
					modifiedColumn = modifiedColumn.replace(".", "_");
					modifiedColumn = modifiedColumn.replace(" ", "_");

					LOG.debug("columnName=" + modifiedColumn);
					LOG.debug("columnType=" + columnType);
					metadata.put(modifiedColumn, columnType);
					columnNamesList.add(modifiedColumn);
				    }

				    for (Map.Entry m : metadata.entrySet()) {
					String Type;
					if (m.getValue() == "varchar") {
					    Type = "varchar(500)";
					} else if (m.getValue() == "number") {
					    // Query compatibility changes for both POSTGRES and MYSQL
					    Type = (DatabuckEnv.DB_TYPE
						    .equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "bigint"
							    : "bigint(20)";
					} else {
					    Type = "Date";
					}
					String createColumnslocal = m.getKey() + " " + Type + " NULL ,";
					createColumns = createColumns + createColumnslocal;
					String insertColumnslocal = "" + m.getKey() + ",";
					insertColumns = insertColumns + insertColumnslocal;
					String insertValueslocal = "?,";
					insertValues = insertValues + insertValueslocal;
				    }
				}

				createColumns = createColumns.substring(0, createColumns.length() - 1);
				insertColumns = insertColumns.substring(0, insertColumns.length() - 1);
				insertValues = insertValues.substring(0, insertValues.length() - 1);
				// ---------------------------End-----------------------------------//
				LOG.debug(createColumns);

				String createQuery = "";
				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				    createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
					    + "(dbk_row_Id serial PRIMARY KEY ," + createColumns + ")";

				else
				    createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
					    + "(dbk_row_Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT," + createColumns
					    + ")";

				jdbcTemplate1.execute(createQuery);
				InputStream inputStreams = file.getInputStream();
				BufferedReader bra = new BufferedReader(new InputStreamReader(inputStreams));
				String dataLine_1 = "";

				CSVReader csvReader = new CSVReaderBuilder(bra).withSkipLines(1).build();
				List<String[]> allData = csvReader.readAll();
				for (String[] data : allData) {
				    String insertvalues = "";
				    for (int i = 0; i <= data.length - 1; i++) {
					String colName = columnNamesList.get(i);
					String colDataType = metadata.get(colName);
					String colValue = data[i];

					if (colDataType.equalsIgnoreCase("date")
						|| colDataType.toLowerCase().startsWith("varchar")) {
					    colValue = colValue.replace("'", "''");
					    String val = "'" + colValue + "',";
					    insertvalues = insertvalues + val;

					} else {
					    if (colValue == null || colValue.trim().isEmpty())
						colValue = null;
					    insertvalues = insertvalues + colValue + ",";
					}

				    }
				    insertvalues = insertvalues.substring(0, insertvalues.length() - 1);

				    String sql = "INSERT INTO " + dataTemplateName + "(" + insertColumns + ") VALUES ("
					    + insertvalues + ")";

				    jdbcTemplate1.execute(sql);
				}

				List<GloabalRule> selected_list = null;
				String schema = resultDBConnectionProperties.getProperty("db1.schema.name");
				String pg_databaseSchemaName = resultDBConnectionProperties
					.getProperty("db1.postgres.databaseschema.name");
				String HostUri = resultDBConnectionProperties.getProperty("db1.url");
				String OrgUri = resultDBConnectionProperties.getProperty("db1.url");

				int index = HostUri.indexOf("/");
				String ResultHost = HostUri.substring(index + 2);
				String resultHostUri = ResultHost.substring(0, ResultHost.indexOf(":"));
				HostUri = resultHostUri;

				int index1 = ResultHost.indexOf(":");
				String ResultPort = ResultHost.substring(index1 + 1);
				String resultPort = ResultPort.substring(0, ResultPort.indexOf("/"));
				String port = resultPort;

				String userlogin = resultDBConnectionProperties.getProperty("db1.user");
				String password = resultDBConnectionProperties.getProperty("db1.pwd");

				String connectionName = dataTemplateName + "_con";

				// Get the connection available for the connection details else create new
				// connection
				String connectionType = "";
				String dataLocation = "";
				// Query compatibility changes for both POSTGRES and MYSQL
				connectionType = (DatabuckEnv.DB_TYPE
					.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "Postgres" : "MYSQL";
				dataLocation = (DatabuckEnv.DB_TYPE
					.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "Postgres" : "MYSQL";
				schema = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? schema + "," + pg_databaseSchemaName
					: schema;

				Long idDataSchema = schemaDao.getConnectionIdByDetails(connectionType, HostUri, port,
					schema, userlogin, password);
				String sslEnabled = "N";
				LOG.debug("OrgURI: " + OrgUri);

				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_MYSQL)
					&& OrgUri.toLowerCase().contains("usessl=true")) {
				    sslEnabled = "Y";
				} else if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
						&& OrgUri.toLowerCase().contains("ssl=true")) {
					sslEnabled = "Y";
				}

				LOG.debug("sslEnabled: " + sslEnabled);

				if (idDataSchema == null || idDataSchema <= 0l) {
				    // Create schema
				    idDataSchema = schemaDao.saveDataIntoListDataSchema(HostUri, schema, userlogin,
					    password, port, connectionName, dataLocation, "", "", "", "N", "", "", "",
					    "", sslEnabled, "", "", "", "", "", "", "", "", "", "", "", "", projectId,
					    createdByUser, "", "", "", Long.toString(projectId), "", "", "", "", "", "",
					    "", "", "", "", "N", "N", "N", 0, 0, 2, "N", nDomainId, "N", "", "", "", "",
					    "", "", "Y", "N", "N", "N", "local", "N", "N","","","");
				} else if (sslEnabled.equalsIgnoreCase("Y")) {
				    // update connection to ssl if not enabled
				    List<ListDataSchema> listdataschema = listdatasourcedao
					    .getListDataSchemaForIdDataSchema(idDataSchema);

				    if (!listdataschema.get(0).getSslEnb().equalsIgnoreCase("Y")) {
					boolean sslEnableStatus = listdatasourcedao
						.enableSSLForConnectionById(idDataSchema);

					if (!sslEnableStatus)
						LOG.debug("Could not enable SSL for connection Id["
						    + idDataSchema + "]");
					else
						LOG.debug(
						    "SSL is enabled for connection Id[" + idDataSchema + "]");
				    }
				}

				if (idDataSchema != null && idDataSchema > 0l) {
				    // update clusterpropertyCategory to local
				    schemaDao.updateClusterPropertyCategoryByIdDataSchema(idDataSchema, "local");

				    // Create a template

				    CompletableFuture<Long> result = dataProilingTemplateService
					    .createDataTemplatewithoutSession(nDomainId, idDataSchema, dataLocation,
						    dataTemplateName, newdataTemplateName, "", schema, "", "", "", "N",
						    "", "", "", "", "", "", idUser, HostUri, schema, "", "", "", schema,
						    file, "", "", "N", selected_list, projectId, "N", createdByUser,
						    "N", null, "", null, null);

				    templateId = result.get();

				    LOG.debug("TemplateId: " + templateId);

				    if (templateId != null && templateId > 0l) {
				    	LOG.info("Placing template in queue");

					iTaskService.insertTaskListForTemplate(templateId, "N", "N");

					uniqueId = iTaskDAO.placeTemplateJobInQueue(templateId,
						TemplateRunTypes.newtemplate.toString());
					// changes regarding Audit trail
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCES,
						formatter.format(new Date()), templateId,
						DatabuckConstants.ACTIVITY_TYPE_CREATED, newdataTemplateName);

					status = "success";
					fileDownloadMessage = "Reference Template Creation is in progress!!";
					String message = "Validation Template creation is in progress, status will be notified to registered email !!";
					response.put("currentSection", "Global Rule");
					response.put("currentLink", "References View");
					response.put("templateId", templateId);
					response.put("uniqueId", uniqueId);
					response.put("templateName", newdataTemplateName);
					response.put("message", message);
					response.put("status", "success");
					LOG.info(message);
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				    } else {
					LOG.error(
						"Failed to create connection, Reference Template Creation failed");
					fileDownloadMessage = "Reference Template Creation failed!!";
					response.put("status", "failed");
					response.put("message", fileDownloadMessage);
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);

				    }

				} else {
				    fileDownloadMessage = "Reference Template Creation failed!!";
				    response.put("status", "failed");
				    response.put("message", fileDownloadMessage);
				    LOG.error(fileDownloadMessage);
				    
				    return new ResponseEntity<Object>(response, HttpStatus.OK);

				}

			    } catch (Exception e) {
				fileDownloadMessage = "Reference Template Creation failed!!";
				e.printStackTrace();
				response.put("status", "failed");
				response.put("message", fileDownloadMessage);
				LOG.error(fileDownloadMessage);
			    
				return new ResponseEntity<Object>(response, HttpStatus.OK);

			    }

			} else {
			    LOG.error("Failed to download the file to DATABUCK_HOME");
			    fileDownloadMessage = "Exception occurred while saving file to DATABUCK_HOME !!";
			    response.put("status", "failed");
			    response.put("message", fileDownloadMessage);
			    
			    return new ResponseEntity<Object>(response, HttpStatus.OK);

			}
		    } else {
			LOG.error(
				"Unable to process this file!! Currently this feature supports only csv files");
			fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
			response.put("status", "failed");
			response.put("message", fileDownloadMessage);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		    }
		} else {
		    response.put("status", "failed");
		    response.put("message", "Token is expired.");
		    LOG.error("Token is expired.");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("status", "failed");
		response.put("message", "Token is missing in the headers.");
		LOG.error("Token is missing in the headers.");
	    
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", e.getMessage());
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/createReferencesTemplate - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/createReferencesTemplateByPath", method = RequestMethod.POST)
    public ResponseEntity<Object> createReferencesTemplateByPath(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputJsonStr) {
    	LOG.info("dbconsole/createReferencesTemplateByPath - START");
	JSONObject oJsonResponse = new JSONObject();
	String fileDownloadStatus = "failed";
	String fileDownloadMessage = "";
	String uniqueId = "";
	Long templateId = 0l;
	String newdataTemplateName = "";
	String message = "";

	Map<String, Object> response = new HashMap<String, Object>();

	JSONObject json = new JSONObject();
	String token = "";

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	    if (token != null && !token.isEmpty()) {
		if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			LOG.debug("Getting request parameters  " + inputJsonStr);
		    UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
		    JSONObject inputJson = new JSONObject(inputJsonStr);
		    String filePath = inputJson.getString("filePath");
		    String newTemplateName = inputJson.getString("referencename");
		    Long projectId = inputJson.getLong("projectId");
		    Long idUser = inputJson.getLong("idUser");
		    Integer domainId = inputJson.getInt("domainId");

		    File fileObj = null;
		    try {
			fileObj = new File(filePath);
		    } catch (Exception e) {
			e.printStackTrace();
			message = "Path does not exists";
			fileObj = null;

		    }
		    if (fileObj == null) {
			response.put("status", "failed");
			response.put("message", message);
			LOG.error(message);
			
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		    }

		    Project project = iProjectDAO.getSelectedProject(projectId);
		    if (project != null) {

			LOG.debug("referencesfile = " + newTemplateName);

			String filename = fileObj.getName();

			String hostUri = System.getenv("DATABUCK_HOME") + "/referencesfile";

			String path = hostUri + "/" + filename;

			newdataTemplateName = "ref_" + newTemplateName;
			String dataTemplateName = filename.split("\\.")[0];
			if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

			    oJsonResponse.put("msg", "successfully");

			    DiskFileItem fileItem = new DiskFileItem("file", "text/csv", false, fileObj.getName(),
				    (int) fileObj.length(), fileObj.getParentFile());
			    fileItem.getOutputStream();
			    try (OutputStream out = fileItem.getOutputStream();
				    InputStream in = Files.newInputStream(fileObj.toPath())) {
				IOUtils.copy(in, out);
			    }
			    fileDownloadStatus = "success";
			    MultipartFile file = new CommonsMultipartFile(fileItem);

			    // Check if the path exists
			    File downloaded_file_dir = new File(hostUri);
			    if (!downloaded_file_dir.exists()) {
				downloaded_file_dir.mkdir();
			    }

			    // Download file to DATABUCK_HOME directory
			    try (BufferedOutputStream stream = new BufferedOutputStream(
				    new FileOutputStream(new File(path)));) {
				byte[] bytes = file.getBytes();
				stream.write(bytes);
				fileDownloadStatus = "success";
				oJsonResponse.put("msg", "successfully");

			    } catch (Exception e) {
				fileDownloadMessage = "\n====>Exception occurred while saving file to DATABUCK_HOME !!";
				LOG.error("Exception occurred while saving file to DATABUCK_HOME");
				e.printStackTrace();
			    }

			    // When file download is success create template
			    if (fileDownloadStatus.equalsIgnoreCase("success")) {
				oJsonResponse.put("msg", "successfully");

				String activeDirectoryFlag = appDbConnectionProperties
					.getProperty("isActiveDirectoryAuthentication");
				String createdByUser = userDAO.getUserNameByUserId(idUser);

				LOG.debug("Created by in createTemplateForQuickStart = " + createdByUser);
				// -------------------------file read --------------------------------//
				Map<String, String> metadata = new LinkedHashMap<String, String>();
				List<String> columnNamesList = new ArrayList<String>();
				List<String> datatypes = new ArrayList<String>();
				datatypes.add("int");
				datatypes.add("char");
				datatypes.add("long");
				datatypes.add("float");
				datatypes.add("double");
				datatypes.add("varchar");
				datatypes.add("text");
				datatypes.add("string");
				datatypes.add("date");
				datatypes.add("number");

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
				    numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				    oJsonResponse.put("msg", "successfully");
				} else {
				    numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate")
					.split(",");
				String createColumns = "";
				String insertColumns = "";
				String insertValues = "";
				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
				    dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
				    InputStream inputStream = file.getInputStream();
				    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				    String line = br.readLine();
				    String dataLine = br.readLine();
				    oJsonResponse.put("msg", "successfully");
				    if (line != null) {
					boolean isJSON = false;
					String[] dataValues = {};
					String[] headerValues = {};
					Map<String, String> jsonHM = new HashMap<String, String>();

					String splitBy = "\\,";

					if (!isJSON) {
					    oJsonResponse.put("msg", "successfully");
					    // This needs to be sorted
					    if (line != null && dataLine != null) {
						dataValues = dataLine.split(splitBy, -1);
						headerValues = line.split(splitBy);
					    }

					    // if dataline is not provided, initiallize to string
					    if (line != null && dataLine == null) {
						headerValues = line.split(splitBy);
						dataValues = new String[headerValues.length];
						for (int i = 0; i < headerValues.length; i++) {
						    dataValues[i] = "";
						}
					    }
					} else {
					    headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
					    dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
					}

					for (int i = 0; i < headerValues.length; i++) {
					    String word = headerValues[i];
					    String columnName = headerValues[i];
					    String dataValue = dataValues[i];
					    String columnType = "String";

					    int first = word.lastIndexOf("(");
					    int last = word.lastIndexOf(")");

					    boolean isColumnTypePresent = false;
					    if (first != -1 && last != -1) {
						isColumnTypePresent = true;
						if (datatypes.contains(
							word.substring(first + 1, last).trim().toLowerCase())) {
						    columnType = word.substring(first + 1, last).trim();
						    columnName = word.substring(0, first);
						} else {
						    columnName = word.substring(0, last);
						}
					    } else {
						columnName = word;
					    }

					    // Determine column type based on first row. This will
					    // be done only if
					    // column type is not already specified in csv.
					    if (!isColumnTypePresent) {
						boolean isColumnTypeIdentified = false;
						if (dataValue.matches(numberRegex)) {
						    isColumnTypeIdentified = true;
						    columnType = "number";
						} else {
						    for (int j = 0; j < dateRegexFormate.size(); j++) {
							String regex = dateRegexFormate.get(j).trim();
							if (dataValue.matches(regex) || (dataValue.length() >= 10
								&& dataValue.substring(0, 10).trim().matches(regex))) {
							    columnType = "Date";
							    isColumnTypeIdentified = true;
							}
						    }
						}

						if (!isColumnTypeIdentified) {
						    columnType = "varchar";
						}
					    }

					    columnName = columnName.trim();
					    String modifiedColumn = "";
					    String[] charArray = columnName.split("(?!^)");
					    for (int j = 0; j < charArray.length; j++) {
						if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

						    modifiedColumn = modifiedColumn + charArray[j];
						}
					    }

					    modifiedColumn = modifiedColumn.replace("-", "_");
					    modifiedColumn = modifiedColumn.replace(".", "_");
					    modifiedColumn = modifiedColumn.replace(" ", "_");

					    LOG.debug("columnName=" + modifiedColumn);
					    LOG.debug("columnType=" + columnType);
					    metadata.put(modifiedColumn, columnType);
					    columnNamesList.add(modifiedColumn);
					}

					for (Map.Entry m : metadata.entrySet()) {
					    String Type;
					    if (m.getValue() == "varchar") {
						Type = "varchar(500)";
					    } else if (m.getValue() == "number") {
						// Query compatibility changes for both POSTGRES and MYSQL
						Type = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(
							DatabuckConstants.DB_TYPE_POSTGRES)) ? "bigint" : "bigint(20)";
					    } else {
						Type = "Date";
					    }
					    String createColumnslocal = m.getKey() + " " + Type + " NULL ,";
					    createColumns = createColumns + createColumnslocal;
					    String insertColumnslocal = "" + m.getKey() + ",";
					    insertColumns = insertColumns + insertColumnslocal;
					    String insertValueslocal = "?,";
					    insertValues = insertValues + insertValueslocal;
					}
				    }

				    createColumns = createColumns.substring(0, createColumns.length() - 1);
				    insertColumns = insertColumns.substring(0, insertColumns.length() - 1);
				    insertValues = insertValues.substring(0, insertValues.length() - 1);
				    // ---------------------------End-----------------------------------//
				    LOG.debug(createColumns);

				    String createQuery = "";
				    // Query compatibility changes for both POSTGRES and MYSQL
				    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
						+ "(dbk_row_Id serial PRIMARY KEY ," + createColumns + ")";

				    else
					createQuery = "CREATE TABLE IF NOT EXISTS " + dataTemplateName
						+ "(dbk_row_Id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,"
						+ createColumns + ")";

				    jdbcTemplate1.execute(createQuery);
				    InputStream inputStreams = file.getInputStream();
				    BufferedReader bra = new BufferedReader(new InputStreamReader(inputStreams));
				    String dataLine_1 = "";

				    CSVReader csvReader = new CSVReaderBuilder(bra).withSkipLines(1).build();
				    List<String[]> allData = csvReader.readAll();
				    for (String[] data : allData) {
					String insertvalues = "";
					for (int i = 0; i <= data.length - 1; i++) {
					    String colName = columnNamesList.get(i);
					    String colDataType = metadata.get(colName);
					    String colValue = data[i];

					    if (colDataType.equalsIgnoreCase("date")
						    || colDataType.toLowerCase().startsWith("varchar")) {
						colValue = colValue.replace("'", "''");
						String val = "'" + colValue + "',";
						insertvalues = insertvalues + val;

					    } else {
						if (colValue == null || colValue.trim().isEmpty())
						    colValue = null;
						insertvalues = insertvalues + colValue + ",";
					    }

					}
					insertvalues = insertvalues.substring(0, insertvalues.length() - 1);

					String sql = "INSERT INTO " + dataTemplateName + "(" + insertColumns
						+ ") VALUES (" + insertvalues + ")";

					jdbcTemplate1.execute(sql);
				    }

				    List<GloabalRule> selected_list = null;
				    String schema = resultDBConnectionProperties.getProperty("db1.schema.name");
				    String pg_databaseSchemaName = resultDBConnectionProperties
					    .getProperty("db1.postgres.databaseschema.name");
				    String HostUri = resultDBConnectionProperties.getProperty("db1.url");
				    String OrgUri = resultDBConnectionProperties.getProperty("db1.url");

				    int index = HostUri.indexOf("/");
				    String ResultHost = HostUri.substring(index + 2);
				    String resultHostUri = ResultHost.substring(0, ResultHost.indexOf(":"));
				    HostUri = resultHostUri;

				    int index1 = ResultHost.indexOf(":");
				    String ResultPort = ResultHost.substring(index1 + 1);
				    String resultPort = ResultPort.substring(0, ResultPort.indexOf("/"));
				    String port = resultPort;

				    String userlogin = resultDBConnectionProperties.getProperty("db1.user");
				    String password = resultDBConnectionProperties.getProperty("db1.pwd");

				    String connectionName = dataTemplateName + "_con";

				    // Get the connection available for the connection details else create new
				    // connection
				    String connectionType = "";
				    String dataLocation = "";
				    // Query compatibility changes for both POSTGRES and MYSQL
				    connectionType = (DatabuckEnv.DB_TYPE
					    .equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "Postgres"
						    : "MYSQL";
				    dataLocation = (DatabuckEnv.DB_TYPE
					    .equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "Postgres"
						    : "MYSQL";
				    schema = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					    ? schema + "," + pg_databaseSchemaName
					    : schema;

				    Long idDataSchema = schemaDao.getConnectionIdByDetails(connectionType, HostUri,
					    port, schema, userlogin, password);

				    String sslEnabled = "N";
				    LOG.debug("OrgURI: " + OrgUri);

				    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_MYSQL)
					    && OrgUri.toLowerCase().contains("usessl=true")) {
						sslEnabled = "Y";
				    } else if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
							&& OrgUri.toLowerCase().contains("ssl=true")) {
						sslEnabled = "Y";
					}

				    LOG.debug("sslEnabled: " + sslEnabled);

				    if (idDataSchema == null || idDataSchema <= 0l) {

					// Create schema
					idDataSchema = schemaDao.saveDataIntoListDataSchema(HostUri, schema, userlogin,
						password, port, connectionName, dataLocation, "", "", "", "N", "", "",
						"", "", sslEnabled, "", "", "", "", "", "", "", "", "", "", "", "",
						projectId, createdByUser, "", "", "", Long.toString(projectId), "", "",
						"", "", "", "", "", "", "", "", "N", "N", "N", 0, 0, 2, "N", domainId,
						"N", "", "", "", "", "", "", "Y", "N", "N", "N", "local", "N", "N","","","");
				    } else if (sslEnabled.equalsIgnoreCase("Y")) {
					// update connection to ssl if not enabled
					List<ListDataSchema> listdataschema = listdatasourcedao
						.getListDataSchemaForIdDataSchema(idDataSchema);

					if (!listdataschema.get(0).getSslEnb().equalsIgnoreCase("Y")) {
					    boolean sslEnableStatus = listdatasourcedao
						    .enableSSLForConnectionById(idDataSchema);

					    if (!sslEnableStatus)
					    	LOG.debug("Could not enable SSL for connection Id["
							+ idDataSchema + "]");
					    else
					    	LOG.debug("SSL is enabled for connection Id["
							+ idDataSchema + "]");
					}
				    }

				    if (idDataSchema != null && idDataSchema > 0l) {

					// update clusterpropertyCategory to local
					schemaDao.updateClusterPropertyCategoryByIdDataSchema(idDataSchema, "local");

					// Create a template
					CompletableFuture<Long> result = dataProilingTemplateService
						.createDataTemplatewithoutSession(domainId, idDataSchema, dataLocation,
							dataTemplateName, newdataTemplateName, "", schema, "", "", "",
							"N", "", "", "", "", "", "", idUser, HostUri, schema, "", "",
							"", schema, file, "", "", "N", selected_list, projectId, "N",
							createdByUser, "N", null, "", null, null);

					templateId = result.get();

					LOG.debug("TemplateId: " + templateId);

					if (templateId != null && templateId > 0l) {
					   LOG.info("Placing template in queue");

					    iTaskService.insertTaskListForTemplate(templateId, "N", "N");

					    uniqueId = iTaskDAO.placeTemplateJobInQueue(templateId,
						    TemplateRunTypes.newtemplate.toString());

					    // changes regarding Audit trail
					    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						    DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCES,
						    formatter.format(new Date()), templateId,
						    DatabuckConstants.ACTIVITY_TYPE_CREATED, newdataTemplateName);

					    fileDownloadMessage = "Reference Template Creation is in progress!!";
					    message = "Validation Template creation is in progress, status will be notified to registered email !!";
					    response.put("currentSection", "Global Rule");
					    response.put("currentLink", "References View");
					    response.put("templateId", templateId);
					    response.put("uniqueId", uniqueId);
					    response.put("templateName", newdataTemplateName);
					    response.put("message", message);
					    response.put("status", "success");
					    LOG.info(message);
					    
					    return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
					    LOG.error(
						    "Failed to create connection, Reference Template Creation failed");
					    fileDownloadMessage = "Reference Template Creation failed!!";
					    response.put("status", "failed");
					    response.put("message", fileDownloadMessage);
					    
					    return new ResponseEntity<Object>(response, HttpStatus.OK);

					}

				    } else {
					fileDownloadMessage = "Reference Template Creation failed!!";
					response.put("status", "failed");
					response.put("message", fileDownloadMessage);
					LOG.error(fileDownloadMessage);
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);

				    }

				} catch (Exception e) {
				    fileDownloadMessage = "Reference Template Creation failed!!";
				    e.printStackTrace();
				    response.put("status", "failed");
				    response.put("message", fileDownloadMessage);
				    LOG.error(fileDownloadMessage);
					
				    return new ResponseEntity<Object>(response, HttpStatus.OK);

				}

			    } else {
				LOG.error("Failed to download the file to DATABUCK_HOME");
				fileDownloadMessage = "Exception occurred while saving file to DATABUCK_HOME !!";
				response.put("status", "failed");
				response.put("message", fileDownloadMessage);
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);

			    }
			} else {
			    LOG.error(
				    "Unable to process this file!! Currently this feature supports only csv files");
			    fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
			    response.put("status", "failed");
			    response.put("message", fileDownloadMessage);
				
			    return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		    } else {
			response.put("status", "failed");
			response.put("message", "Invalid Project Id");
			LOG.error("Invalid Project Id");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		    }
		} else {
		    response.put("status", "failed");
		    response.put("message", "Token is expired.");
		    LOG.error("Token is expired.");
			
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("status", "failed");
		response.put("message", "Token is missing in the headers.");
		 LOG.error("Token is missing in the headers.");
		
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("message", "Error while creating data template from the given path");
	    response.put("status", "failed");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/createReferencesTemplateByPath - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/getNumericProfileListCSV", method = RequestMethod.POST)
    public void getNumericProfileListCSV(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestParam, HttpServletResponse httpResponse) {
    	LOG.info("dbconsole/getNumericProfileListCSV - START");
	try {
	    if (requestParam.containsKey("idData") && requestParam.containsKey("profileName")) {
		String token = "";
		try {
		    token = headers.get("token").get(0);
		    LOG.debug("token "+token.toString());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		if (token != null || !token.isEmpty()) {
		    // validate received token
		    JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		    String tokenStatus = tokenStatusObj.getString("status");

		    if (tokenStatus.equalsIgnoreCase("success")) {
		    	LOG.debug("Getting request parameters  " + requestParam);
			Long idData = Long.valueOf(requestParam.get("idData"));
			String profileName = requestParam.get("profileName");
			if ("numericProfileList".equals(profileName)) {
			    List<NumericalProfile_DP> profiles = listdatasourcedao
				    .readNumericProfileForTemplate(idData);
			    if (profiles != null && profiles.size() > 0) {
				httpResponse.setContentType("text/csv");
				String csvFileName = "ValidationReport.csv";
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				httpResponse.setHeader(headerKey, headerValue);
				ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					CsvPreference.STANDARD_PREFERENCE);
				String[] fields = { "ExecDate", "Run", "ColumnName", "ColumnName1", "Correlation" };
				String[] header = { "Execution Date", "Run", "Column Name 1", "Column Name 2",
					"Correlation(>0.25)" };
				csvWriter.writeHeader(header);
				for (NumericalProfile_DP profile : profiles) {
				    csvWriter.write(profile, fields);
				}
				csvWriter.close();
				LOG.info("CSV file written successfully");
			    } else {
			    	LOG.error("Records not found.");
				throw new Exception("Records not found.");
			    }
			} else {
				LOG.error("Profile name is not matching.");
			    throw new Exception("Profile name is not matching.");
			}

		    } else {
		    	LOG.error("Token is missing.");
			throw new Exception("Token is missing.");
		    }
		} else {
			LOG.error("Auth token is required");
		    throw new Exception("Auth token is required");
		}
	    } else {
	    	LOG.error("Please provide valid app id and profile name");
		throw new Exception("Please provide valid app id and profile name");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getNumericProfileListCSV - END");
    }

    @RequestMapping(value = "/dbconsole/getReferenceTemplateDetails", method = RequestMethod.POST)
    public ResponseEntity<Object> getReferenceTemplateDetails(@RequestHeader HttpHeaders headers,@RequestBody Map<String, String> requestParam) {
	LOG.info("getReferenceTemplateDetails - START");

	JSONObject json = new JSONObject();
	JSONArray result = new JSONArray();
	String status = "failed";
	String message = "";
	String token = "";
	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;
	try {
			token = headers.get("token").get(0);
		    // Validate token
		    if (token != null && !token.isEmpty()) {
				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {
					if (requestParam.containsKey("projectId") && requestParam.containsKey("domainId")) {			
							// fetch template list
					    	String projectId = requestParam.get("projectId");
					    	String domainId = requestParam.get("domainId");
							List<ListDataSource> listDataSource = listdatasourcedao.getListDataSourceTableForRef(projectId,domainId);
				
							//if (listDataSource != null && listDataSource.size() > 0) {
							    result = new JSONArray(listDataSource);
							    status = "success";
							    message = "Fetch Reference Template Details Successfully";
							    LOG.info(message);
							    responseStatus = HttpStatus.OK;
//							} else {
//							    message = "Failed to fetch template Details";
//							LOG.error(message);
//							}
						}else {
							message = "Please provide valid project id and domain id";
						}
				} else {
				    message = "Token expired.";
				    LOG.error(message);
				    responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
	
		    } else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
		    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	json.put("status", status);
	json.put("message", message);
	json.put("result", result);
	LOG.info("dbconsole/getNumericProfileListCSV - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getReferenceTemplateDetailsCSV", method = RequestMethod.POST)
    public void getReferenceTemplateDetailsCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse,@RequestBody Map<String, String> requestParam) {
    	LOG.info("dbconsole/getReferenceTemplateDetailsCSV - START");
	String token = "";
	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug(token.toString());
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equals("success")) { // get email from userToken

		    if (requestParam.containsKey("projectId") && requestParam.containsKey("domainId")) {
		    	String projectId = requestParam.get("projectId");
		    	String domainId = requestParam.get("domainId");
				List<ListDataSource> listDataSource = listdatasourcedao.getListDataSourceTableForRef(projectId,domainId);
				if (listDataSource != null && listDataSource.size() > 0) {
				    httpResponse.setContentType("text/csv");
				    String csvFileName = "ReferenceTemplateDetails.csv";
				    String headerKey = "Reference-Template";
				    String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
				    httpResponse.setHeader(headerKey, headerValue);
				    ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
					    CsvPreference.STANDARD_PREFERENCE);
				    String[] fields = { "idData", "name", "dataLocation", "tableName", "projectName",
					    "createdAt", "createdByUser" };
				    String[] header = { "Template Id", "Name", "Location", "Table Name", "Project Name",
					    "Created At", "Created By" };
				    csvWriter.writeHeader(header);
				    for (ListDataSource dataSource : listDataSource) {
				    	csvWriter.write(dataSource, fields);
				    }
				    csvWriter.close();
				    LOG.info("CSV file written successfully");
				} else {
					LOG.error("Records not found.");
				    throw new Exception("Records not found.");
				}
		    } else {
		    	LOG.error("Please provide valid project id");
			throw new Exception("Please provide valid project id and domain id");
		    }
		} else {
			LOG.error("Token expired.");
		    throw new Exception("Token expired.");
		}

	    } else {
	    	LOG.error("Token is missing in headers.");
		throw new Exception("Token is missing in headers.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	    try {
		httpResponse.sendError(0, e.getMessage());
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	LOG.info("dbconsole/getReferenceTemplateDetailsCSV - END");
    }

    private List<ColumnProfileDelta_DP> updateList(List<ColumnProfile_DP> columnProfileList) {
	List<ColumnProfileDelta_DP> updatedColProfiles = new ArrayList<ColumnProfileDelta_DP>();
	if (columnProfileList.size() > 0) {
	    for (ColumnProfile_DP colProf : columnProfileList) {
		ColumnProfileDelta_DP colProfileDelta = new ColumnProfileDelta_DP();
		colProfileDelta.setExecDate(colProf.getExecDate());
		colProfileDelta.setRun(colProf.getRun());
		colProfileDelta.setColumnName(colProf.getColumnName());
		colProfileDelta.setDataType(colProf.getDataType());
		colProfileDelta.setTotalRecordCount("primary," + colProf.getTotalRecordCount());
		colProfileDelta.setMissingValue("primary," + colProf.getMissingValue());
		colProfileDelta.setPercentageMissing("primary," + colProf.getPercentageMissing());
		colProfileDelta.setUniqueCount("primary,"
			+ (Double.valueOf(colProf.getUniqueCount()) / Double.valueOf(colProf.getTotalRecordCount()))
				* 100);
		colProfileDelta.setMinLength("primary," + colProf.getMinLength());
		colProfileDelta.setMaxLength("primary," + colProf.getMaxLength());
		colProfileDelta.setMean("primary," + colProf.getMean());
		colProfileDelta.setStdDev("primary," + colProf.getStdDev());
		colProfileDelta.setMin("primary," + colProf.getMin());
		colProfileDelta.setMax("primary," + colProf.getMax());
		colProfileDelta.setPercentile_99("primary," + colProf.getPercentile_99());
		colProfileDelta.setPercentile_75("primary," + colProf.getPercentile_75());
		colProfileDelta.setPercentile_25("primary," + colProf.getPercentile_25());
		colProfileDelta.setPercentile_1("primary," + colProf.getPercentile_1());
		colProfileDelta.setDefaultPatterns(colProf.getDefaultPatterns());
		updatedColProfiles.add(colProfileDelta);
	    }
	}
	return updatedColProfiles;
    }

    @RequestMapping(value = "/dbconsole/getNonDerivedtemplatename", method = RequestMethod.POST)
    public ResponseEntity<Object> getNonDerivedtemplatename(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestParam) throws IOException, URISyntaxException {
    	LOG.info("dbconsole/getNonDerivedtemplatename - START");
    	Map<String, Object> response = new HashedMap<String, Object>();
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {
			LOG.debug("Getting request parameters  " + requestParam);
		    Long projectId = Long.parseLong(requestParam.get("projectId"));

		    List<String> templateListFrom = validationcheckdao.getNonDerivedTemplateList(projectId);
		    List<String> templateNameList = new ArrayList<>();
		    if (templateListFrom != null && templateListFrom.size() > 0) {
			for (String list : templateListFrom) {
			    String str = list.toString().replace("[", "").replace("]", "");
			    templateNameList.add(str);
			}
			response.put("result", templateNameList);
		    }
		    response.put("status", "success");
		    response.put("message", "success");
		    LOG.info("success");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
		    response.put("status", "fail");
		    response.put("message", "Token expaired!!");
		    LOG.error("Token expaired!!");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("status", "fail");
		response.put("message", "Please provide token");
		LOG.error("Please provide token");
	    
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    response.put("status", "failed");
	    response.put("message", e.getMessage());
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/getNonDerivedtemplatename - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/createDerivedDataTemplate", method = RequestMethod.POST)
    public ResponseEntity<Object> derivedDataTemplated(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> request, HttpSession session) throws IOException, URISyntaxException {
    	LOG.info("dbconsole/createDerivedDataTemplate - START");
    	Map<String, Object> response = new HashedMap<String, Object>();
	try {
	    String token = "";
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    long idUser = Long.parseLong(request.get("idUser").toString());
		    LOG.debug("idUser=" + idUser);
		    String activeDirectoryFlag = appDbConnectionProperties
			    .getProperty("isActiveDirectoryAuthentication");
		    LOG.debug("activeDirectoryFlag = " + activeDirectoryFlag);
		    String createdByUser = "";
		    if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
			createdByUser = request.get("createdByUser");
			LOG.debug("createdByUser = " + createdByUser);
		    } else {
			// getting createdBy username from createdBy userId
		    	LOG.debug("idUser = " + idUser);
			createdByUser = userDAO.getUserNameByUserId(idUser);
			LOG.debug("createdByUser = " + createdByUser);
		    }
		    Long projectId = Long.parseLong(request.get("projectId").toString());
		    Integer domainId = Integer.parseInt(request.get("domainId").toString());
		    String datalocation = "Derived";

		    LOG.info("hello create form");
		    String DataTemplateName = request.get("dataset");
		    LOG.debug("DataTemplateName=" + DataTemplateName);
		    String description = request.get("description");
		    LOG.debug("description=" + description);
		    String template1Value = request.get("template1id");
		    LOG.debug("template1Value=" + template1Value);
		    String template2Value = request.get("template2id");
		    LOG.debug("template2Value=" + template2Value);
		    String template1Name = request.get("template1name");
		    LOG.debug("template1=" + template1Name);
		    String template2Name = request.get("template2name");
		    LOG.debug("template2=" + template2Name);
		    String aliasNameTemplate1 = request.get("aliasname1");
		    LOG.debug("aliasNameTemplate1=" + aliasNameTemplate1);
		    String aliasNameTemplate2 = request.get("aliasname2");
		    LOG.debug("aliasNameTemplate1=" + aliasNameTemplate2);
		    String queryText = request.get("querytext");
		    LOG.debug("queryText=" + queryText);

		    String message = "Validation Template creation is in progress, status will be notified to registered email !!";
		    CompletableFuture<Long> result = dataProilingTemplateService.createDerivedDataTemplate(session,
			    datalocation, DataTemplateName, description, idUser, projectId, template1Value,
			    template2Value, template1Name, template2Name, aliasNameTemplate1, aliasNameTemplate2,
			    queryText, createdByUser, domainId);

		    long idData = 0l;
		    Map<String, Object> results = new HashedMap<>();
		    try {

			idData = result.get();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    if (idData > 0 && idData != 0l) {
			// changes regarding Audit trail
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			iTaskDAO.addAuditTrailDetail(idUser, userDAO.getUserNameByUserId(Long.valueOf(idUser)),
				DatabuckConstants.DBK_FEATURE_DERIVED_TEMPLATE, formatter.format(new Date()), idData,
				DatabuckConstants.ACTIVITY_TYPE_CREATED, DataTemplateName);

			String uniqueId = "";
			try {
			    uniqueId = dataProilingTemplateService.triggerDataTemplate(idData, datalocation, "N", "N")
				    .get();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			LOG.debug("uniqueId: " + uniqueId);

			results.put("templateId", String.valueOf(idData));
			results.put("uniqueId", uniqueId);
		    } else {
			message = "Validation Template not Created Successfully";
		    }
		    results.put("dataTemplateName", DataTemplateName);
		    response.put("results", results);
		    response.put("status", "success");
		    response.put("message", message);
		    LOG.info(message);
		    
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
		    response.put("status", "fail");
		    response.put("message", "Token expaired!!");
		    LOG.error("Token expaired!!");
		    
		    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
	    } else {
		response.put("status", "fail");
		response.put("message", "Please provide token");
		LOG.error("Please provide token");
	    
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	} catch (Exception e) {
	    response.put("status", "fail");
	    response.put("message", e.getMessage());
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/createDerivedDataTemplate - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/dbconsole/insertNewValueToRefTable", method = RequestMethod.POST)
    public ResponseEntity<Object> insertNewValueToRefTable(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputJsonStr) {
	LOG.info("dbconsole/insertNewValueToRefTable - START");
	JSONObject json = new JSONObject();
	String status = "failed";
	String message = "";
	String token = "";

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
			responseStatus = HttpStatus.EXPECTATION_FAILED;
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equals("success")) {
			LOG.debug("Getting request parameters  " + inputJsonStr);
		    if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
			JSONObject alertData = new JSONObject(inputJsonStr);
			JSONArray columnNames = alertData.getJSONArray("columnNames");
			JSONArray columnValues = alertData.getJSONArray("columnValues");
			String table_name = alertData.getString("tableName");
			Long idData = alertData.getLong("idData");
			ListDataSource listDataSource= listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			// convert jsonArray to Comma separated string
//			String columnName = CDL.rowToString(columnNames);
//			String columnValue = CDL.rowToString(columnValues);
//			columnName = columnName.trim().replaceAll(",", "\\$\\$");
//			columnValue = columnValue.trim().replaceAll(",", "\\$\\$");

			String columnName = columnNames.join("$$").replaceAll("\"", "");
			String columnValue = columnValues.join("$$").replace("\\\"", "??")
					.replace("\"", "").replace("??", "\"");

			// insert new Value
			String insertNewRow = iResultsDAO.insertNewValueToRefTable(columnName, columnValue, table_name);
			if (insertNewRow != null && !insertNewRow.isEmpty()) {
			    UserToken userToken = dashboardConsoleDao
				    .getUserDetailsOfToken(headers.get("token").get(0));
			    Long idUser = userToken.getIdUser();

			    // changes regarding Audit trail
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
				    DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCESVIEW, formatter.format(new Date()),
						idData, DatabuckConstants.ACTIVITY_TYPE_EDITED, listDataSource.getName());

			    message = "Successfully inserted new row";
			    status = "success";
			    LOG.info(message);

			} else {
			    message = "Failed to insert data row. Please enter appropriate data type.";
			    LOG.error(message);
			}

		    } else {
			message = "Invalid request";
			LOG.error(message);
		    }
		} else {
		    message = "Token expired.";
			LOG.error(message);
			responseStatus = HttpStatus.EXPECTATION_FAILED;
		}
	    } else {
			message = "Token is missing in headers";
	    	LOG.error(message);
			responseStatus = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	}
	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/insertNewValueToRefTable - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

	@RequestMapping(value = "/dbconsole/addReferenceDataByFile", method = RequestMethod.POST)
	public ResponseEntity<Object> addReferenceDataByFile(@RequestHeader HttpHeaders headers, @RequestParam("datafile") MultipartFile file,
													   @RequestParam("idData") Long idData) {
		LOG.info("addReferenceDataByFile - START");
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if(file.getOriginalFilename().endsWith(".csv")){
						if (idData != null) {
							String tableName = "";
							String refTemplateName = "";
							ListDataSource listDataSource= listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
							if(listDataSource != null){
								refTemplateName = listDataSource.getName();
								listDataAccess listDataAccess = listdatasourcedao.getListDataAccess((long) listDataSource.getIdData());
								if (listDataAccess != null && listDataAccess.getFolderName()!=null && !listDataAccess.getFolderName().isEmpty()) {
									tableName = listDataAccess.getFolderName();
								}
							}
							LOG.debug("Tablename :: " + tableName);
//							Check table name is non empty
							if(!tableName.equals("")){
								InputStream inputStreams = file.getInputStream();
								BufferedReader bra = new BufferedReader(new InputStreamReader(inputStreams));
								CSVReader csvReader = new CSVReaderBuilder(bra).build();
								List<String[]> allData = csvReader.readAll();
//								Check if file contains data
								if(allData.size() >= 2) {
									JSONObject jsonObject = dataTemplateDeltaCheckService.bulkUploadToRefTable(idData, tableName, allData);
									if(jsonObject!=null){

										// changes regarding Audit trail
										UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
										SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
												DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCESVIEW, formatter.format(new Date()),
												idData, DatabuckConstants.ACTIVITY_TYPE_EDITED, refTemplateName);

										result = jsonObject.getJSONObject("result");
										message = jsonObject.getString("message");
										status = jsonObject.getString("status");
									}
									responseStatus = HttpStatus.OK;
								} else if (allData.size() == 1) {
									if(String.join("", allData.get(0)).trim().equals("")){
										message = "Unable to process blank file!!";
									} else {
										message = "Unable to process file, does not contains data!!";
									}
									LOG.error(message);
								} else {
									message = "Unable to process blank file!!";
									LOG.error(message);
								}
							} else {
								message = "Reference Data Table not found";
								LOG.error(message);
							}

						} else {
							message = "Invalid request";
							LOG.error(message);
						}
					} else {
						message = "This feature only supports CSV file.";
						LOG.error(message);
					}

				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("addReferenceDataByFile - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/addReferenceDataByPath", method = RequestMethod.POST)
	public ResponseEntity<Object> addReferenceDataByPath(@RequestHeader HttpHeaders headers, @RequestBody String inputJsonStr) {
		LOG.info("addReferenceDataByPath - START");
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject alertData = new JSONObject(inputJsonStr);

						File file;
						long idData = alertData.getLong("idData");
						String filePath = alertData.getString("filePath");

						String tableName = "";
						String refTemplateName = "";
                        ListDataSource listDataSource= listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
						if(listDataSource != null){
							refTemplateName = listDataSource.getName();
							listDataAccess listDataAccess = listdatasourcedao.getListDataAccess((long) listDataSource.getIdData());
							if (listDataAccess != null && listDataAccess.getFolderName()!=null && !listDataAccess.getFolderName().isEmpty()) {
								tableName = listDataAccess.getFolderName();
							}
						}
						LOG.debug("Tablename :: " + tableName);
//						Check table name is non empty
						if(!tableName.equalsIgnoreCase("")){
							try{
								file = new File(filePath);
								if(file.getName().endsWith(".csv")){
									InputStream inputStreams = new FileInputStream(file);
									BufferedReader bra = new BufferedReader(new InputStreamReader(inputStreams));
									CSVReader csvReader = new CSVReaderBuilder(bra).build();
									List<String[]> allData = csvReader.readAll();
//									Check if file contains data
									if(allData.size() >= 2){
										JSONObject jsonObject = dataTemplateDeltaCheckService.bulkUploadToRefTable(idData, tableName, allData);
										if(jsonObject.has("result")){
											// changes regarding Audit trail
											UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
											SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
													DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCESVIEW, formatter.format(new Date()),
													idData, DatabuckConstants.ACTIVITY_TYPE_EDITED, refTemplateName);
											result = jsonObject.getJSONObject("result");
											message = jsonObject.getString("message");
											status = jsonObject.getString("status");
										}
										responseStatus = HttpStatus.OK;
									} else if (allData.size() == 1) {
										message = "Unable to process file, it only contains headers!!";
										LOG.error(message);
									} else {
										message = "Unable to process blank file!!";
										LOG.error(message);
									}
								} else{
									message = "This feature only supports CSV file.";
									LOG.error(message);
								}
							} catch (Exception e){
								message = "Invalid Filepath.";
								LOG.error(e.getMessage());
							}
						} else {
							message = "Table not found";
							LOG.error(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("addReferenceDataByPath - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/deleteReferenceData", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteInternalRefRecords(@RequestHeader HttpHeaders headers,
													 @RequestBody String inputJsonStr) {

		LOG.info("dbconsole/deleteReferenceData - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug(token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.getLong("idData");
						JSONArray dbkRowIdArray = inputJson.getJSONArray("recordsToDelete");
						String tableName = "";
						String refTemplateName = "";
						ListDataSource listDataSource= listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
						if(listDataSource != null){
							refTemplateName = listDataSource.getName();
							listDataAccess listDataAccess = listdatasourcedao.getListDataAccess((long) listDataSource.getIdData());
							if (listDataAccess != null && listDataAccess.getFolderName()!=null && !listDataAccess.getFolderName().isEmpty()) {
								tableName = listDataAccess.getFolderName();
								LOG.debug("Tablename :: " + tableName);
							}
							if (tableName != null && !tableName.trim().isEmpty()) {
								String dbkRowIds = dbkRowIdArray.join(",");
								int deletedCount = iResultsDAO.deleteDataFromRefTable(tableName, dbkRowIds);
								if(deletedCount>0){
									// changes regarding Audit trail
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
											DatabuckConstants.DBK_FEATURE_INTERNAL_REFERENCESVIEW, formatter.format(new Date()),
											idData, DatabuckConstants.ACTIVITY_TYPE_DELETED, refTemplateName);
									message = "Records deleted successfully";
									status="success";
								}else
									message="Failed to delete the record.";

							}else {
								message = "Incorrect table name ";
								LOG.error(message);
							}
						}
					}else
						message="Invalid Request";
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			message = "Invalid Request";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/deleteReferenceData - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
}
