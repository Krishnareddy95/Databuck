package com.databuck.restcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMConnectionSummary;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMSummaryDetails;
import com.databuck.bean.FMUpdateReq;
import com.databuck.bean.FileMonitorRules;
import com.databuck.dao.DBKFileMonitoringDao;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.DBKFileMonitoringService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.util.TokenValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class FileMonitoringRestController {

    @Autowired
    private FileMonitorDao fileMonitorDao;

    @Autowired
    private DBKFileMonitoringDao dbkFileMonitoringDao;

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;

    @Autowired
    private TokenValidator tokenValidator;

    @Autowired
    private DBKFileMonitoringService dbkFileMonitoringService;

    @Autowired
    private IValidationCheckDAO validationcheckdao;
    
    private static final Logger LOG = Logger.getLogger(FileMonitoringRestController.class);

    @RequestMapping(value = "/dbconsole/getFileMonitoringValidationsList", method = RequestMethod.POST)
    public @ResponseBody String getFileMonitoringValidationsList(HttpServletRequest request,
	    @RequestBody Map<String, Long> params) {

    LOG.info("dbconsole/getFileMonitoringValidationsList - START");
	long projectId = params.get("projectId");
	long domainId = params.get("domainId");
	String status = "failure";
	String reason = "";
	JSONArray responseList = new JSONArray();

	try {
		LOG.debug("Getting request parameters  " + params);
		LOG.debug("Getting request parameters projectId " + projectId);
		LOG.debug("Getting request parameters domainId  " + domainId);

	    List<DbkFMConnectionSummary> fmSummaryList = dbkFileMonitoringDao.getFileMonitoringValidationsList(domainId,
		    projectId);

	    if (fmSummaryList != null && fmSummaryList.size() > 0) {
		for (DbkFMConnectionSummary fcs : fmSummaryList) {
		    JSONObject obj = new JSONObject();
		    obj.put("validationId", fcs.getValidationId());
		    obj.put("validationName", fcs.getValidationName());
		    obj.put("connectionId", fcs.getConnectionId());
		    obj.put("connectionName", fcs.getConnectionName());
		    obj.put("connectionType", fcs.getConnectionType());
		    obj.put("executionDate", fcs.getExecutionDate());
		    obj.put("expectedFileCount", fcs.getExpectedFileCount());
		    obj.put("arrivedFileCount", fcs.getArrivedFileCount());
//					obj.put("duplicateFileCount", fcs.getDuplicateFileCount());
		    obj.put("newFileCount", fcs.getNewFileCount());
		    obj.put("missingFileCount", fcs.getMissingFileCount());
		    if (fcs.getArrivedFileCount() > 0 && fcs.getExpectedFileCount() == 0)
			obj.put("ingestionStatus", "additional");
		    else
			obj.put("ingestionStatus", fcs.getIngestionStatus());
		    responseList.put(obj);

		}
	    }

	    status = "success";
	  
	} catch (Exception e) {
	    LOG.error("\n=====>Exception occurred in getFileMonitoringValidationsList API !!");
	    reason = "Unexpected error Occurred";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	JSONObject jobj = new JSONObject();
	jobj.put("status", status);
	jobj.put("message", reason);
	jobj.put("result", responseList);
	String output = jobj.toString();

	LOG.info("dbconsole/getFileMonitoringValidationsList - END");
	return output;
    }

    @RequestMapping(value = "/dbconsole/getFMSummaryForConnection", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String getFMSummaryForConnection(HttpServletRequest request,
	    @RequestBody Map<String, String> requestMap) {

    LOG.info("dbconsole/getFMSummaryForConnection - START");
	String status = "failure";
	String reason = "";
	JSONArray responseList = new JSONArray();

	try {
	    Long connectionId = null;
	    Long validationId = null;
	    String fromDate = null;
	    String toDate = null;
	    boolean isRequestValid = true;

	    if (requestMap != null && requestMap.size() > 0) {
	    LOG.debug("Getting request parameters  " + requestMap);
		connectionId = Long.parseLong(requestMap.get("connectionId"));
		LOG.debug("Getting request parameters connectionId " + connectionId);
		if (connectionId == null || connectionId <= 0l)
		    reason = "Invalid connectionId";

		validationId = Long.parseLong(requestMap.get("validationId"));
		LOG.debug("Getting request parameters validationId " + validationId);
		
		if (validationId == null || validationId <= 0l)
		    reason = "Invalid validationId";

		fromDate = requestMap.get("fromDate");
		LOG.debug("fromDate: " + fromDate);
		toDate = requestMap.get("toDate");
		LOG.debug("toDate: " + toDate);
		if ((fromDate == null && toDate == null) || (fromDate.trim().isEmpty() && toDate.trim().isEmpty()))
		    reason = "Invalid loadDate";
	    }

	    if (reason != null && !reason.trim().isEmpty())
		isRequestValid = false;

	    if (isRequestValid) {

		List<DbkFMSummaryDetails> fmSummaryDetailsList = dbkFileMonitoringDao
			.getDBKFMSummaryDetailsForConnection(validationId, connectionId, fromDate, toDate);

		if (fmSummaryDetailsList != null && fmSummaryDetailsList.size() > 0) {

		    for (DbkFMSummaryDetails fms : fmSummaryDetailsList) {
			JSONObject obj = new JSONObject();
			obj.put("executionDate", fms.getLoad_date());
			obj.put("connectionId", fms.getConnection_id());
			obj.put("validationId", fms.getValidation_id());
			obj.put("connectionName", fms.getConnectionName());
			obj.put("connectionType", fms.getConnectionType());
			obj.put("tableOrFileName", fms.getTable_or_subfolder_name());
			obj.put("fileIndicator", fms.getFile_indicator());
			obj.put("dayOfWeek", fms.getDayOfWeek());

			String expectedTime = "";
			if (fms.getStatus() != null && !fms.getStatus().equalsIgnoreCase("new file")) {
			    if (fms.getLoaded_hour() != null) {
				expectedTime = StringUtils.leftPad(String.valueOf(fms.getLoaded_hour()), 2, "0") + ":"
					+ StringUtils.leftPad(String.valueOf(fms.getExpected_minute()), 2, "0");
			    }
			}
			obj.put("expectedTime", expectedTime);
			obj.put("actualFileCount", fms.getActual_file_count());
			obj.put("expectedFileCount", fms.getExpected_file_count());
			obj.put("status", fms.getStatus());
			responseList.put(obj);
		    }
		}

		status = "success";
	    }
	} catch (Exception e) {
	    LOG.error("\n=====>Exception occurred in getFMSummaryForConnection API !!");
	    reason = "Unexpected error Occurred";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	JSONObject jobj = new JSONObject();
	jobj.put("status", status);
	jobj.put("message", reason);
	jobj.put("result", responseList);
	String output = jobj.toString();
	LOG.info("dbconsole/getFMSummaryForConnection - END");
	return output;
    }

    @RequestMapping(value = "/dbconsole/getFileArrivalDetailsForTable", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String getFileArrivalDetailsForTable(HttpServletRequest request,
	    @RequestBody Map<String, String> requestMap) {

    LOG.info("dbconsole/getFileArrivalDetailsForTable - START");
	String status = "failure";
	String reason = "";
	JSONArray responseList = new JSONArray();

	try {
	    Long connectionId = null;
	    Long validationId = null;
	    String tableOrFileName = null;
	    String fromDate = null;
	    String toDate = null;
	    boolean isRequestValid = true;

	    if (requestMap != null && requestMap.size() > 0) {
	    LOG.debug("Getting request parameters  " + requestMap);
		connectionId = Long.parseLong(requestMap.get("connectionId"));
		LOG.debug("ConnectionId: " + connectionId);
		if (connectionId == null || connectionId <= 0l)
		    reason = "Invalid connectionId";

		validationId = Long.parseLong(requestMap.get("validationId"));
		LOG.debug("ValidationId: " + validationId);
		if (validationId == null || validationId <= 0l)
		    reason = "Invalid validationId";

		tableOrFileName = requestMap.get("tableOrFileName");
		LOG.info("\n=====> tableOrFileName: " + tableOrFileName);
		if (tableOrFileName == null || tableOrFileName.trim().isEmpty())
		    reason = "Invalid tableOrFileName";

		fromDate = requestMap.get("fromDate");
		LOG.debug("fromDate: " + fromDate);
		toDate = requestMap.get("toDate");
		LOG.debug("toDate: " + toDate);
		if ((fromDate == null && toDate == null) || (fromDate.trim().isEmpty() && toDate.trim().isEmpty()))
		    reason = "Invalid loadDate";

	    }

	    if (reason != null && !reason.trim().isEmpty())
		isRequestValid = false;

	    if (isRequestValid) {

		List<DbkFMFileArrivalDetails> dbkFMFileArrivalList = dbkFileMonitoringDao
			.getDBKFMFileArrivalDetailsForTable(validationId, connectionId, fromDate, toDate,
				tableOrFileName);

		if (dbkFMFileArrivalList != null && dbkFMFileArrivalList.size() > 0) {
		    for (DbkFMFileArrivalDetails fad : dbkFMFileArrivalList) {
			JSONObject obj = new JSONObject();
			obj.put("executionDate", fad.getLoad_date());
			obj.put("connectionId", fad.getConnection_id());
			obj.put("validationId", fad.getValidation_id());
			obj.put("connectionName", fad.getConnectionName());
			obj.put("connectionType", fad.getConnectionType());
			obj.put("tableOrFileName", fad.getTable_or_subfolder_name());
			obj.put("fileIndicator", fad.getFile_indicator());
			obj.put("dayOfWeek", fad.getDayOfWeek());
			String loadedTime = "";
			if (fad.getLoaded_hour() != null) {
			    loadedTime = StringUtils.leftPad(String.valueOf(fad.getLoaded_hour()), 2, "0") + ":"
				    + StringUtils.leftPad(String.valueOf(fad.getLoaded_time()), 2, "0");
			}
			obj.put("loadedTime", loadedTime);
			String expectedTime = "";
			if (fad.getFile_arrival_status() != null
				&& !fad.getFile_arrival_status().equalsIgnoreCase("new file")) {
			    if (fad.getExpected_hour() != null) {
				expectedTime = StringUtils.leftPad(String.valueOf(fad.getExpected_hour()), 2, "0") + ":"
					+ StringUtils.leftPad(String.valueOf(fad.getExpected_time()), 2, "0");
			    }
			}
			obj.put("expectedTime", expectedTime);
			obj.put("volumne", fad.getRecord_count());
			obj.put("volumeCheck", fad.getRecord_count_check());
			obj.put("arrivalStatus", fad.getFile_arrival_status());
			obj.put("fileStatus", fad.getFile_validity_status());
			obj.put("schemaCheck", fad.getColumn_metadata_check());
			responseList.put(obj);
		    }
		}

		status = "success";
	    }

	} catch (Exception e) {
	    LOG.error("Exception occurred in getFileArrivalDetailsForTable API !!");
	    reason = "Unexpected error Occurred";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	JSONObject jobj = new JSONObject();
	jobj.put("status", status);
	jobj.put("message", reason);
	jobj.put("result", responseList);
	String output = jobj.toString();

	LOG.info("dbconsole/getFileArrivalDetailsForTable - END");
	return output;
    }

    @RequestMapping(value = "/dbconsole/getFmResultsExecutiveSummary", method = RequestMethod.POST)
    public ResponseEntity<Object> getFmResutsExecutiveSummary(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputJsonStr) {
    LOG.info("dbconsole/getFmResultsExecutiveSummary - START");
	JSONObject arrivalDetails = new JSONObject();
	JSONObject json = new JSONObject();
	JSONArray missingDelayedFileCount = new JSONArray();
	JSONArray additionalFileCount = new JSONArray();
	JSONArray failedFileCount = new JSONArray();
	String status = "failed";
	String message = "";
	String token = "";
	Long domainId = null;
	Long projectId = null;
	String fromDate = null;
	String toDate = null;
	String validationList = null;

	// Default response status
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    // Get token from request header
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token "+token.toString());
	    } catch (Exception e) {
	    LOG.error(e.getMessage());
		e.printStackTrace();
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");
		if (tokenStatus.equals("success")) {

		    // validate Request
		    if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
		    LOG.debug("Getting request parameters  " + inputJsonStr);

			JSONObject inputJson = new JSONObject(inputJsonStr);
			domainId = inputJson.getLong("domainId");
			projectId = inputJson.getLong("projectId");
			fromDate = inputJson.getString("fromDate");
			toDate = inputJson.getString("toDate");

			if (domainId > 0 && projectId > 0 && !fromDate.trim().isEmpty() && !toDate.trim().isEmpty()) {

			    validationList = dbkFileMonitoringDao.getIdAppsForFMconnectiondetails(domainId, projectId);

			    // Check result is empty or not
			    if (validationList != null && !validationList.isEmpty()) {

				additionalFileCount = dbkFileMonitoringDao.getAdditionalFileCount(fromDate, toDate,
					validationList);

				missingDelayedFileCount = dbkFileMonitoringDao.getMissingDelayedFileCount(fromDate,
					toDate, validationList);

				failedFileCount = dbkFileMonitoringDao.getfailedFileCount(fromDate, toDate,
					validationList);

				status = "success";

			    } else {
				message = "Failed to get results";
				LOG.error(message);
				}
			} else {
			    message = "Wrong Input";
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
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	arrivalDetails.put("additionalFileCount", additionalFileCount);
	arrivalDetails.put("missedDelayedFileCount", missingDelayedFileCount);
	arrivalDetails.put("failedFileCount", failedFileCount);

	json.put("status", status);
	json.put("message", message);
	json.put("result", arrivalDetails);
	LOG.info("dbconsole/getFmResultsExecutiveSummary - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/editDBKFileMonitoringRule", method = RequestMethod.POST)
    public ResponseEntity<Object> editDBKFileMonitoringRule(@RequestHeader HttpHeaders headers,
	    @RequestBody FMUpdateReq fmUpdateReq) {
	// List<DBKFileMonitoringRules> dataList, @PathVariable("data") int idApp
    LOG.info("dbconsole/editDBKFileMonitoringRule - START");
	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");
	String token = "", message = "";
	int idApp;
	List<DBKFileMonitoringRules> dataList = null;

	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    if (fmUpdateReq != null) {
		idApp = fmUpdateReq.getIdApp();
		dataList = fmUpdateReq.getDataList();

		LOG.debug("idApp=" + idApp);
		for (DBKFileMonitoringRules fMRules : dataList) {
		    boolean rowStatus = fileMonitorDao.isRowIdExitForDbkFileMonitorRules(fMRules.getId());
		    if (rowStatus) {
			fileMonitorDao.updateToDbkFileMonitorRules(fMRules, idApp);
		    } else {
		    	boolean isDuplicateRule= fileMonitorDao.isDuplicateFMRuleForHourlyFiles(fMRules);
		    	if(isDuplicateRule){
					System.out.println("\n====>Duplicate rule discarded");
				}else {
					fileMonitorDao.insertToDbkFileMonitorRules(new Object[]{fMRules.getConnectionId(), idApp,
							fMRules.getSchemaName(), fMRules.getTableName(), fMRules.getFileIndicator(),
							fMRules.getDayOfWeek(), fMRules.getHourOfDay(), fMRules.getExpectedTime(),
							fMRules.getExpectedFileCount(), fMRules.getStartHour(), fMRules.getEndHour(),
							fMRules.getFrequency()});
				}
			}
		}
		Long stagingDataCount = validationcheckdao.getDbkFileMonitorRulesCountInStaging((long) idApp);
		if (stagingDataCount > 0l) {
		    List<DBKFileMonitoringRules> stagingFMRulesList = validationcheckdao
			    .getStagingDbkFileMonitorRules((long) idApp);
		    response.put("result", stagingFMRulesList);
		} else {
		    List<DBKFileMonitoringRules> fileMonitoringRulesList = fileMonitorDao
			    .getDBKFileMonitorDetailsByIdApp(idApp);
		    response.put("result", fileMonitoringRulesList);
		}
		response.put("message", "Successfully updated file monitoring rules.");
		response.put("status", "success");
	    } else {
		message = "Invalid request";
		LOG.error("Invalid request");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	    response.put("message", "Failed to update file monitoring rules");
	}
	LOG.info("dbconsole/editDBKFileMonitoringRule - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/saveDBKFileMonitoringRule", method = RequestMethod.POST)
    public ResponseEntity<Object> saveDBKFileMonitoringRule(@RequestHeader HttpHeaders headers,
	    @RequestBody FMUpdateReq fmUpdateReq) {
    LOG.info("dbconsole/saveDBKFileMonitoringRule - START");
	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");
	String token = "", message = "";
	int idApp;
	List<DBKFileMonitoringRules> dataList = null;
	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    if (fmUpdateReq != null) {
		idApp = fmUpdateReq.getIdApp();
		dataList = fmUpdateReq.getDataList();

		LOG.debug("idApp=" + idApp);
		for (DBKFileMonitoringRules fMRules : dataList) {
		    fileMonitorDao.insertToDbkFileMonitorRules(
			    new Object[] { fMRules.getConnectionId(), idApp, fMRules.getSchemaName(),
				    fMRules.getTableName(), fMRules.getFileIndicator(), fMRules.getDayOfWeek(),
				    fMRules.getHourOfDay(), fMRules.getExpectedTime(), fMRules.getExpectedFileCount(),
				    fMRules.getStartHour(), fMRules.getEndHour(), fMRules.getFrequency() });
		}
		Long stagingDataCount = validationcheckdao.getDbkFileMonitorRulesCountInStaging((long) idApp);
		if (stagingDataCount > 0l) {
		    List<DBKFileMonitoringRules> stagingFMRulesList = validationcheckdao
			    .getStagingDbkFileMonitorRules((long) idApp);
		    response.put("result", stagingFMRulesList);
		} else {
		    List<DBKFileMonitoringRules> fileMonitoringRulesList = fileMonitorDao
			    .getDBKFileMonitorDetailsByIdApp(idApp);
		    response.put("result", fileMonitoringRulesList);
		}
		response.put("message", "Successfully updated file monitoring rules.");
		response.put("status", "success");
	    } else {
		message = "Invalid request";
		LOG.error("Invalid request");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    LOG.error(e.getMessage());
	    response.put("message", "Failed to update file monitoring rules");
	}
	LOG.info("dbconsole/saveDBKFileMonitoringRule - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/deleteFileMonitorRuleById", method = RequestMethod.POST)
    public ResponseEntity<Object> deleteFileMonitorRuleById(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, Integer> params) {
    LOG.info("dbconsole/deleteFileMonitorRuleById - START");
	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");
	response.put("message", "Failed to delete the file monitoring rule.");
	String token = "";
	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	try {
	    LOG.debug("-- deleteFileMonitorRuleById ----" + params.get("id"));
	    int count = fileMonitorDao.deleteFMRule(params.get("id"));
	    if (count > 0) {
		response.put("status", "success");
		response.put("message", "Successfully deleted the file monitoring rule.");
	    }
	    LOG.info("result Delete----->" + count);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	LOG.info("dbconsole/deleteFileMonitorRuleById - END");
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/submitFileMonitoringCSV", method = RequestMethod.POST)
    public ResponseEntity<Object> submitFileMonitoringCSV(@RequestHeader HttpHeaders headers,
	    @RequestParam("monitoringtype") String fileMonitoringType, @RequestParam("data") int idApp,
	    @RequestParam("dataupload") MultipartFile file) {
    LOG.info("dbconsole/submitFileMonitoringCSV - START");

	// String fileMonitoringType = null;
	Map<String, Object> resMap = new HashMap<>();

	LOG.debug(fileMonitoringType + "---" + idApp);

	Map<String, Object> response = new HashMap<>();
	response.put("status", "failed");

	String token = "";
	try {
	    token = headers.get("token").get(0);
	    LOG.debug("token "+token.toString());
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	if (token == null || token.isEmpty()) {
	    response.put("message", "Token is missing in headers.");
	    LOG.error("Token is missing in headers.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	}
	if (!tokenValidator.isValid(token)) {
	    response.put("message", "Token is expired.");
	    LOG.error("Token is expired.");
	    return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	} else {
	    // System.out.println("inputJsonStr" + inputJsonStr);

	    if (fileMonitoringType != null && !fileMonitoringType.trim().isEmpty()) {

		// JSONObject inputJson = new JSONObject(inputJsonStr);
		// fileMonitoringType = inputJson.getString("fileMonitoringType");
		// idApp = inputJson.getInt("idApp");
//				fromDate = inputJson.getString("fromDate");
//				toDate = inputJson.getString("toDate");

		String viewName = "viewFileMonitoringCSV";

		if (fileMonitoringType != null && !fileMonitoringType.trim().isEmpty()
			&& (fileMonitoringType.trim().equalsIgnoreCase("snowflake")
				|| fileMonitoringType.trim().equalsIgnoreCase("azuredatalakestoragegen2batch")
				|| fileMonitoringType.trim().equalsIgnoreCase("aws s3")))
		    viewName = "dbkFileMonitoringView";

		// File file1 = convert(file);

		if (fileMonitoringType.equalsIgnoreCase("snowflake")
			|| fileMonitoringType.equalsIgnoreCase("azuredatalakestoragegen2batch")
			|| fileMonitoringType.equalsIgnoreCase("aws s3")) {

		    List<DBKFileMonitoringRules> dbkFileMonitoringRules = dbkFileMonitoringService
			    .submitFileMonitoringCSVForSnowFlake(file, file.getOriginalFilename(), idApp);
//					System.out.println("output from method "+dbkFileMonitoringRules);
//					for (DBKFileMonitoringRules elem_ : dbkFileMonitoringRules) {
//					       System.out.println(elem_.getId()+"");
//					}
		    response.put("result", dbkFileMonitoringRules);
		    response.put("status", "success");
		    response.put("message", "Successfully Submmited the file monitoring rule.");
		   // LOG.info("dbconsole/submitFileMonitoringCSV - END");
		    return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {

		    try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
			LOG.info("File data =>" + br.readLine());
			// Delimiters used in the CSV file
			final String COMMA_DELIMITER = ",";
			String line = "";

			// Create List for holding FileMonitorRules objects
			List<FileMonitorRules> arrListFileMonitorRule = new ArrayList<FileMonitorRules>();

			while ((line = br.readLine()) != null) {

			    String[] fileDetails = line.split(COMMA_DELIMITER);

			    if (fileDetails.length > 0) {

				// save the filedetails in FileMonitorRules object

				// String fId = fileDetails[0];

				// long id = 101L;

				// System.out.println();

				String bucketName = fileDetails[0];
				String strDayOfchk = fileDetails[1];

				Integer dayOfCheck = 0;

				if (strDayOfchk.equalsIgnoreCase("Sun") || strDayOfchk.equalsIgnoreCase("Sunday")) {
				    dayOfCheck = 1;
				} else if (strDayOfchk.equalsIgnoreCase("Mon")
					|| strDayOfchk.equalsIgnoreCase("Monday")) {
				    dayOfCheck = 2;
				} else if (strDayOfchk.equalsIgnoreCase("Tue")
					|| strDayOfchk.equalsIgnoreCase("Tuesday")) {
				    dayOfCheck = 3;

				} else if (strDayOfchk.equalsIgnoreCase("wed")
					|| strDayOfchk.equalsIgnoreCase("Wednesday")) {
				    dayOfCheck = 4;

				} else if (strDayOfchk.equalsIgnoreCase("thur")
					|| strDayOfchk.equalsIgnoreCase("Thursday")) {
				    dayOfCheck = 5;

				} else if (strDayOfchk.equalsIgnoreCase("fri")
					|| strDayOfchk.equalsIgnoreCase("Friday")) {
				    dayOfCheck = 6;

				} else if (strDayOfchk.equalsIgnoreCase("sat")
					|| strDayOfchk.equalsIgnoreCase("Saturday")) {
				    dayOfCheck = 7;

				}

				Integer fileCount = Integer.parseInt(fileDetails[2]);
				String filePattern = fileDetails[3];

				String folderPath = fileDetails[4];

				String frequency = fileDetails[5];

				String timeOfCheck = fileDetails[7];

				LOG.info("timeOfCheck:" + timeOfCheck);

				Integer fileSizeThreshold = Integer.parseInt(fileDetails[8]);

				String partitionedFolders = "N";

				int maxFolderDepth = 2;

				Date lastProcessedDate = null;

				FileMonitorRules fm = new FileMonitorRules(bucketName, folderPath, filePattern,
					frequency, dayOfCheck, timeOfCheck, fileCount, lastProcessedDate, idApp,
					fileSizeThreshold, partitionedFolders, maxFolderDepth);

				LOG.info("FM =>" + fm);

				arrListFileMonitorRule.add(fm);

			    }

			}
			LOG.info("arrListFileMonitorRule =>" + arrListFileMonitorRule);
			// Lets print the Employee List

			br.close();

			response.put("result", arrListFileMonitorRule);
			response.put("status", "success");
			response.put("message", "Successfully Submmited the file monitoring rule.");
			//LOG.info("dbconsole/submitFileMonitoringCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			//LOG.info("dbconsole/submitFileMonitoringCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		    }
		}

	    } else {
		response.put("status", "failed");
		response.put("message", "Invalid Input Fields.");
		LOG.error("Invalid Input Fields.");
		LOG.info("dbconsole/submitFileMonitoringCSV - END");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }

	}
    }

    public File convert(MultipartFile file) {
	LOG.info("Path of Files   " + file.getOriginalFilename());
	File convFile = new File(file.getOriginalFilename());

	try {
	    convFile.createNewFile();
	    FileOutputStream fos;
	    fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return convFile;
    }

}
