package com.databuck.restcontroller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.databuck.dao.impl.ValidationDAOImpl;
import com.databuck.service.ExecutiveSummaryService;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.constants.ResultTableConstants;
import com.databuck.dto.ValidationListRequest;
import com.databuck.dto.ValidationResultRequest;
import com.databuck.service.PaginationServiceRest;
import com.databuck.service.PrimaryKeyMatchingResultService;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.TokenValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "*")
@RestController
public class PaginationRestController {

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    private ValidationDAOImpl validationDAO;

    @Autowired
    private PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

    @Autowired
    private PaginationServiceRest paginationService;

    @Autowired
    RuleCatalogService ruleCatalogService;

    @Autowired
    private TokenValidator tokenValidator;

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;

    private static final Logger LOG = Logger.getLogger(PaginationRestController.class);

    String dateFilter = "";
    int runFilter = 0;

    @RequestMapping(value = "/dbconsole/TranDetailAllTable", method = RequestMethod.POST)
    public ResponseEntity<Object> TranDetailAllTable(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	LOG.info("dbconsole/TranDetailAllTable - START");
	Map<String, Object> response = new HashMap<String, Object>();
	HttpStatus status = null;
	// runFilter = (int) session.getAttribute("RunFilter");
	try {
	    if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
		response.put("message", "Please provide token.");
		response.put("status", "failed");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    // Check whether all required parameters are available.
	    if (!requestBody.containsKey("idApp") || !requestBody.containsKey("fromDate")
		    || !requestBody.containsKey("toDate")) {
		response.put("message", "Please provide require parameters.");
		response.put("status", "failed");
		return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
	    }
	    if (tokenValidator.isValid(headers.get("token").get(0))) {
		LOG.debug("token " + headers.get("token").get(0).toString());
		LOG.debug("Getting request parameters  " + requestBody);
		String idApp = requestBody.get("idApp");
		String fromDate = requestBody.get("fromDate");
		String toDate = requestBody.get("toDate");
		response.put("result",
			primaryKeyMatchingResultService.getTableDataByDateFilter(idApp, fromDate, toDate));
		response.put("status", "success");
		response.put("message", "Records fetched successfully.");
		LOG.info("Records fetched successfully.");
		status = HttpStatus.OK;
	    } else {
		response.put("message", "Invalid token.");
		response.put("status", "failed");
		LOG.error("Invalid token.");
		status = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    response.put("status", "failed");
	    response.put("message", "Failed to fetch records.");
	    LOG.error(e.getMessage());
	    LOG.info("dbconsole/TranDetailAllTable - END");
	    return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	LOG.info("dbconsole/TranDetailAllTable - END");
	return new ResponseEntity<Object>(response, status);
    }

    public int getTotalRecordCount(String tableName, String idApp, String dateFilter, boolean checkFailedRecords)
	    throws SQLException {

	int totalRecords = -1;

	String sql = "SELECT " + "COUNT(*) as count from " + tableName + " where (" + dateFilter + ")";

	if (checkFailedRecords) {
	    sql += " and (NumMeanStatus  = 'failed' OR  NumSDStatus = 'failed' )";
	}

	SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(sql);

	if (resultSet.next()) {
	    totalRecords = resultSet.getInt("count");
	}

	return totalRecords;
    }

    // This api is used to get dashboard validations
    @RequestMapping(value = "/dbconsole/dashboardValidations", method = RequestMethod.POST)
    public ResponseEntity<Object> getAllValidations(@RequestHeader HttpHeaders headers,
	    @RequestBody ValidationListRequest validationRequest) {

	LOG.info("Get dashboard validation list api started.");
	Map<String, Object> response = new HashMap<String, Object>();

	HttpStatus httpStatus = HttpStatus.OK;
	String status = "failed";
	String message = "";

	try {
	    if (headers.containsKey("token") && !headers.get("token").isEmpty()) {

			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (tokenValidator.isValid(headers.get("token").get(0))) {
	
			    LOG.debug("Getting validation details for request parameters, " + validationRequest);
			    Map<String, Object> result = paginationService.getAllValidations(validationRequest);
	
			    if (!result.containsKey("validations") || result.get("validations") == null) {
			    	message = "Records not found for requested parameters.";
			    } else {
					message = "Successfully got the records.";
					response.put("result", result);
					status = "success";
			    }
			    LOG.debug(message + " " + validationRequest);
			} else {
			    message = "Token is invalid.";
			    LOG.error(message);
			    httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
	    } else {	
		message = "Token is missing in request.";
		LOG.error(message);
		httpStatus = HttpStatus.EXPECTATION_FAILED;
	    }
	} catch (Exception e) {
	    message = "Failed to fetch records.";
	    LOG.error(message + " " + e.getStackTrace());
	}
	response.put("status", status);
	response.put("message", message);
	LOG.info("Get dashboard validation list api end.");
	return new ResponseEntity<Object>(response, httpStatus);
    }

    @RequestMapping(value = "/dbconsole/dashboardValCSV", method = RequestMethod.POST)
    public void dashboardValCSV(@RequestHeader HttpHeaders headers,
	    @RequestBody ValidationListRequest validationRequest, HttpServletResponse httpResponse) {
	LOG.info("dbconsole/getValidationCSVReport - START");
	String status = "failed";
	JSONObject apiResponse = new JSONObject();
	List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();

	String token = "";
	String msg = "DashBoard Validation List successfully build";

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token " + token.toString());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (token != null && !token.isEmpty()) {
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");
		if (tokenStatus.equalsIgnoreCase("success")) {
		    try {

			Object list = paginationService.getAllValidations(validationRequest).get("validations");
			if (list != null) {
			    ObjectMapper mapper = new ObjectMapper();
			    masterDashboard = mapper.convertValue(list,
				    new TypeReference<List<DataQualityMasterDashboard>>() {
				    });
			}
			if (masterDashboard != null && masterDashboard.size() > 0) {
			    httpResponse.setContentType("text/csv");
			    String csvFileName = "ValidationReport.csv";
			    String headerKey = "Content-Disposition";
			    String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
			    httpResponse.setHeader(headerKey, headerValue);
			    ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
				    CsvPreference.STANDARD_PREFERENCE);
			    String[] fields = { "IdApp", "Date", "Run", "ConnectionName", "ValidationCheckName",
				    "Source1", "AggreagteDQI", "RecordCount", "PrimaryKeyStatus", "DataDriftStatus",
				    "FileName", "NullCountStatus", "RecordAnomalyStatus", "NumericalFieldStatus",
				    "RecordCountStatus" };
			    String[] header = { "Validation ID", "Execution Date", "Run", "Connection Name",
				    "Validation Results", "Validation Template", "DTS", "Record Count",
				    "Custom Uniqueness", "Data Drift", "File Name", "Null Count", "Value Anomaly",
				    "Data Distribution", "Record Count Status" };

			    csvWriter.writeHeader(header);
			    for (DataQualityMasterDashboard validation : masterDashboard) {
				csvWriter.write(validation, fields);
			    }
			    csvWriter.close();
			    LOG.info("Response merged successfully.");
			} else {
			    msg = "Records not available";
			    LOG.error(msg);
			}
		    } catch (JSONException | IOException e) {
			msg = "Please Check Input Data";
			LOG.error(e.getMessage());
		    }

		} else {
		    msg = tokenStatusObj.getString("msg");
		    LOG.error(msg);
		}

	    } else {
		msg = "Token is missing in headers";
		LOG.error(msg);
		httpResponse.sendError(403, "Token is missing in headers");
	    }
	} catch (JSONException | IOException e) {
	    msg = "Request failed....";
	    LOG.error(e.getMessage());
	}
	apiResponse.put("status", status);
	apiResponse.put("message", msg);
    }

    // This api is used to get data drift results in advance result tab
    @RequestMapping(value = "/dbconsole/dataDriftResultList", method = RequestMethod.POST)
    public ResponseEntity<Object> getDataDriftResult(@RequestHeader HttpHeaders headers,
	    @RequestBody ValidationResultRequest resultRequest) {

	LOG.info("Get data drift result list api started.");
	Map<String, Object> response = new HashMap<String, Object>();

	HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
	String status = "failed";
	String message = "";

	try {
	    if (headers.containsKey("token") && !headers.get("token").isEmpty()) {

		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
		if (tokenValidator.isValid(headers.get("token").get(0))) {

		    LOG.debug("Getting data drift result for request parameters, " + resultRequest);
		    Map<String, Object> result = paginationService.getDataDriftResult(resultRequest);

		    if (!result.containsKey(ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE)
			    || result.get(ResultTableConstants.DATA_DRIFT_COUNT_SUMMERY_TABLE) == null) {
			message = "Records not found for requested parameters.";
		    } else {
			message = "Successfully got the records.";
			response.put("result", result);
			httpStatus = HttpStatus.OK;
			status = "success";
		    }
		    LOG.debug(message + " " + resultRequest);
		} else {
		    message = "Token is invalid.";
		    LOG.error(message);
		}
	    } else {
		message = "Token is missing in request.";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Failed to fetch records.";
	    LOG.error(message + " " + e.getStackTrace());
	    httpStatus = HttpStatus.OK;
	}
	response.put("status", status);
	response.put("message", message);
	LOG.info("Get data drift result list api end.");
	return new ResponseEntity<Object>(response, httpStatus);
    }

    // This api is used to get Distribution results in advance result tab
    @RequestMapping(value = "/dbconsole/distributionResult", method = RequestMethod.POST)
    public ResponseEntity<Object> getDistributionResult(@RequestHeader HttpHeaders headers,
	    @RequestBody ValidationResultRequest resultRequest) {

	LOG.info("Get Distribution result list api started.");
	Map<String, Object> response = new HashMap<String, Object>();

	HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
	String status = "failed";
	String message = "";

	try {
	    if (headers.containsKey("token") && !headers.get("token").isEmpty()) {

		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
		if (tokenValidator.isValid(headers.get("token").get(0))) {

		    LOG.debug("Getting Distribution result for request parameters, " + resultRequest);
		    Map<String, Object> result = paginationService.getDistributionResult(resultRequest);

		    if (!result.containsKey(ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY)
			    || result.get(ResultTableConstants.DISTRIBUTION_RESULT_SUMMRY) == null) {
			message = "Records not found for requested parameters.";
		    } else {
			message = "Successfully got the records.";
			response.put("result", result);
			httpStatus = HttpStatus.OK;
			status = "success";
		    }
		    LOG.debug(message + " " + resultRequest);
		} else {
		    message = "Token is invalid.";
		    LOG.error(message);
		}
	    } else {
		message = "Token is missing in request.";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Failed to fetch records.";
	    LOG.error(message + " " + e.getStackTrace());
	    httpStatus = HttpStatus.OK;
	}
	response.put("status", status);
	response.put("message", message);
	LOG.info("Get Distribution result list api end.");
	return new ResponseEntity<Object>(response, httpStatus);
    }

    @RequestMapping(value = "/dbconsole/validationslists", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> getPaginatedCustomRulesList(@RequestHeader HttpHeaders headers,
	    @RequestBody ValidationListRequest validationRequest) {
	LOG.info("Get rule catlog validations list api started.");
	Map<String, Object> response = new HashMap<String, Object>();

	HttpStatus httpStatus = HttpStatus.OK;
	String status = "failed";
	String message = "";

	try {
	    if (headers.containsKey("token") && !headers.get("token").isEmpty()) {

		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
		if (tokenValidator.isValid(headers.get("token").get(0))) {

		    LOG.debug("Getting rule catlog validation list for request parameters, " + validationRequest);
		    Map<String, Object> result = paginationService.getPaginatedValidationsJsonData(validationRequest);
		    LOG.debug(result);
		    boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

		    if (!result.containsKey("validations") || result.get("validations") == null) {
			message = "Records not found for requested parameters.";
		    } else {
			message = "Successfully got the records.";
			result.put("isRuleCatalogEnabled", isRuleCatalogEnabled);
			response.put("result", result);
			httpStatus = HttpStatus.OK;
			status = "success";
		    }
		    LOG.debug(message + " " + validationRequest);
		} else {
		    message = "Token is invalid.";
		    LOG.error(message);
		}
	    } else {
		message = "Token is missing in request.";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Failed to fetch records.";
	    LOG.error(message + " " + e.getStackTrace());
	    httpStatus = HttpStatus.OK;
	}
	response.put("status", status);
	response.put("message", message);
	LOG.info("Get rule catelog validation list api end.");
	return new ResponseEntity<Object>(response, httpStatus);
    }

    // Adding New code below (23-Feb-23)

    @RequestMapping(value = "/dbconsole/getDQNullCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getNullCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {

	LOG.info("Get DQNullCheck Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());

	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("null_check"))
				validationCheckResultsObj = paginationService.getNullCheckResults(inputJson);

			    else if (checkName.equalsIgnoreCase("micro_null_check"))
				validationCheckResultsObj = paginationService
					.getMicrosegmentNullCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg + " " + e.getStackTrace());
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQNullCheck Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getDQLengthCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getLengthCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQLengthCheck Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("length_check"))
				validationCheckResultsObj = paginationService.getLengthCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQLengthCheck Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQAutoDiscoverRegexPResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQAutoDiscoverRegexPResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQ Regex Pattern Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());

	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		if (tokenValidator.isValid(token)) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("default_pattern_check"))
				validationCheckResultsObj = paginationService.getAutoDiscoverdPatternResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg + " " + e.getStackTrace());
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = "Token is expired";
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQNullCheck Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getDQUserDefinedRegexPResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQUserDefinedRegexPResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQ Regex Pattern Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());

	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		if (tokenValidator.isValid(token)) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("pattern_check"))
				validationCheckResultsObj = paginationService.getUserDefinedPatternResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg + " " + e.getStackTrace());
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = "Token is expired";
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQNullCheck Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getDQBadDataCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQBadDataCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQBadDataCheckResults  api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("bad_data_check"))
				validationCheckResultsObj = paginationService.getDQBadDataCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQBadDataCheckResults Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDefaultValueCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDefaultValueCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQDefaultValueCheckResults  api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("default_check"))
				validationCheckResultsObj = paginationService.getDQDefaultValueCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDefaultValueCheckResults Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDataDriftResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDataDriftResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQDataDriftResults  api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		if (tokenValidator.isValid(token)) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("data_drift_check")) {
				validationCheckResultsObj = paginationService.getDQDataDriftResults(inputJson);
				String isFirstRun = "N";
				int firstRunCount = validationDAO.getFirstRunCount(inputJson.getLong("idApp"));
				if (firstRunCount == 1) {
				    isFirstRun = "Y";
				}
				validationCheckResultsObj.put("isFirstRun", isFirstRun);
			    }

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = "Token is expired";
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDataDriftResults Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDataDriftResultsSummary", method = RequestMethod.POST)
    public ResponseEntity<String> DataDriftResultsSummary(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info(" DataDriftResultsSummary  api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		if (tokenValidator.isValid(token)) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("data_drift_check"))
				validationCheckResultsObj = paginationService.getDQDataDriftResultsSummary(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = "Token is expired";
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDataDriftResults Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDuplicateCheckSummaryResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDuplicateCheckSummaryResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQDuplicateCheckSummary Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("duplicate_check"))
				validationCheckResultsObj = paginationService
					.getDuplicateCheckSummaryResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQDuplicateCheckSummary Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDuplicateCheckCompositeResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDuplicateCheckCompositeResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQDuplicateCheckCompositeResults api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("duplicate_check"))
				validationCheckResultsObj = paginationService
					.getDQDuplicateCheckCompositeResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQDuplicateCheckCompositeResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDuplicateCheckIndividualResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDuplicateCheckIndividualResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get DQDuplicateCheckIndividual Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("duplicate_check"))
				validationCheckResultsObj = paginationService
					.getDQDuplicateCheckIndividualResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get DQDuplicateCheckIndividual Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQGlobalRuleResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQGlobalRuleResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQGlobalRule Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("global_rules"))
				validationCheckResultsObj = paginationService.getGlobalRuleResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQGlobalRuleResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQTimeSequenceCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQTimeSequenceCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQTimeSequenceCheckResults Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("sequence_check"))
				validationCheckResultsObj = paginationService.getTimeSequenceCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQTimeSequenceCheckResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQRecordAnomalyResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQRecordAnomalyResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info(" getDQRecordAnomalyResults  api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		if (tokenValidator.isValid(token)) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("record_anomaly_check"))
				validationCheckResultsObj = paginationService.getDQRecordAnomalyResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = "Token is expired";
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQRecordAnomalyResults Results api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDistributionCheckResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDistributionCheckResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQDistributionCheck Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("distribution_check"))
				validationCheckResultsObj = paginationService.getDQDistributionCheckResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDistributionCheckResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDateConsistencyResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDateConsistencyResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQDateConsistency Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("date_anomaly_check"))
				validationCheckResultsObj = paginationService.getDQDateConsistencyResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDateConsistencyResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQDateConsistencyFailedResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQDateConsistencyFailedResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQDateConsistencyFailed Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("micro_date_rule_check"))
				validationCheckResultsObj = paginationService
					.getDQDateConsistencyFailedResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQDateConsistencyFailedResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQRecordCountAnomalyResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQRecordCountAnomalyResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQRecordCountAnomaly Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("count_reasonability"))
				validationCheckResultsObj = paginationService.getDQRecordCountAnomalyResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	System.out.println("\n===> RestAPI : getDQRecordCountAnomalyResults End<===");
	LOG.info("Get getDQRecordCountAnomalyResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQRCAdgroupResults", method = RequestMethod.POST)
    public ResponseEntity<String> getDQRecordCountAnomalyDgroupResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQRecordCountAnomalyDgroupResults Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("count_reasonability"))
				validationCheckResultsObj = paginationService
					.getDQRecordCountAnomalyDgroupResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQRecordCountAnomalyDgroupResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQMicrosegmentRCAResult", method = RequestMethod.POST)
    public ResponseEntity<String> getDQMicrosegmentRCAResult(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQMicrosegmentRCAResult Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("micro_rca_check"))
				validationCheckResultsObj = paginationService.getDQMicrosegmentRCAResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQMicrosegmentRCAResult api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

    @RequestMapping(value = "/dbconsole/getDQCustomDistributionResult", method = RequestMethod.POST)
    public ResponseEntity<String> getDQCustomDistributionResults(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputsJsonStr) {
	LOG.info("Get getDQCustomDistributionResults Results api started");
	String msg = "Results data fetched successfully";
	JSONObject inputJson = new JSONObject(inputsJsonStr);
	LOG.debug("Input paramters : " + inputJson.toString());
	String status = "failed";
	JSONObject validationCheckResultsObj = new JSONObject();
	JSONObject apiResponse = new JSONObject();

	String token = "";
	HttpStatus responseStatus = HttpStatus.OK;

	try {
	    try {
		token = headers.get("token").get(0);
		LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    // check if token is empty or not
	    if (token != null && !token.isEmpty()) {
		// validate received token
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");

		if (tokenStatus.equalsIgnoreCase("success")) {

		    // validate input params
		    JSONObject validateObj = paginationService.validateEssentialCheckParams(inputJson);

		    if (validateObj.getString("status").equalsIgnoreCase("success")) {
			try {
			    String checkName = inputJson.getString("checkName");

			    if (checkName.equalsIgnoreCase("custom_distribution_check"))
				validationCheckResultsObj = paginationService.getDQCustomDistributionResults(inputJson);

			    if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
				status = "success";
				LOG.debug(msg + " " + inputJson.toString());
			    } else {
				msg = "Records are not found.";
				LOG.error(msg);
			    }

			} catch (Exception e) {
			    e.printStackTrace();
			    msg = "Records are not found.";
			    LOG.error(msg);
			}
		    } else {
			msg = validateObj.getString("message");
			LOG.error(msg);
		    }
		} else {
		    msg = tokenStatusObj.getString("msg");
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		    LOG.error(msg);
		}
	    } else {
		msg = "Token is missing in headers";
		responseStatus = HttpStatus.EXPECTATION_FAILED;
		LOG.error(msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    msg = "Request failed";
	    LOG.error(msg + " " + e.getStackTrace());
	}

	apiResponse.put("result", validationCheckResultsObj);
	apiResponse.put("status", status);
	apiResponse.put("message", msg);

	LOG.info("Get getDQCustomDistributionResults api end");

	return new ResponseEntity<>(apiResponse.toString(), responseStatus);

    }

}
