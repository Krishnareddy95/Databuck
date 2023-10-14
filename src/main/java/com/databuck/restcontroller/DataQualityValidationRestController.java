package com.databuck.restcontroller;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.DataQualityMasterDashboard;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ValidationDQISummary;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.csvmodel.ValidationSummary;
import com.databuck.dao.DQIGraphDAOI;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dto.DashboardTableCountSummary;
import com.databuck.dto.DateVsDTSGraph;
import com.databuck.service.DashboardSummaryService;
import com.databuck.service.DataTemplateDeltaCheckService;
import com.databuck.service.DistributionCheckResultsService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IDataQualityResultsService;


@CrossOrigin(origins = "*")
@RestController
public class DataQualityValidationRestController {

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	public IProjectDAO projectDAO;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private DashboardSummaryService dashboardSummaryService;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	DQIGraphDAOI dqiGraphDAOI;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private IDataQualityResultsService dataQualityResultsService;

	@Autowired
	DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	@Autowired
	DistributionCheckResultsService distributionCheckResultsService;

	private static final Logger LOG = Logger.getLogger(DataQualityValidationRestController.class);

	@RequestMapping(value = "/dbconsole/getNonEnabledColumnsForCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> getNonEnabledColumnsForCheck(@RequestHeader HttpHeaders headers,
			HttpServletRequest request, HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getNonEnabledColumnsForCheck - START");
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
				LOG.debug("token " + token.toString());
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
					long idApp = inputJson.getLong("idApp");
					String checkName = inputJson.getString("checkName");

					// Get All NonEnabled Columns
					json = dataTemplateDeltaCheckService.getNonEnabledColumnsForCheck(idApp, checkName);
					status = json.getString("status");
					message = json.getString("message");

				} else {
					message = "Token expired.";
					LOG.error("Token is expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token is missing in headers";
				LOG.error("Token is missing in headers.");
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/getNonEnabledColumnsForCheck - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/activateColumnCheckInTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> activateColumnCheckInTemplate(@RequestHeader HttpHeaders headers,
			HttpServletRequest request, HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/activateColumnCheckInTemplate - START");
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
				LOG.debug("token " + token.toString());
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
					long idApp = inputJson.getLong("idApp");
					String checkName = inputJson.getString("checkName");
					JSONArray columnNames = inputJson.getJSONArray("columnNames");

					for (int i = 0; i < columnNames.length(); ++i) {
						JSONObject rec = columnNames.getJSONObject(i);
						
						if(rec.has("threshold")) {
							Pattern pattern = Pattern.compile("[0-9]");
							Matcher matcher = pattern.matcher(rec.getString("threshold"));
							if(matcher.find()) {
								double value = Double.parseDouble(rec.getString("threshold"));
								LOG.debug("Threshold Value =>" + value);
								
							    if(value<0)
							    {
							       LOG.error(value + " Threshold is negative");
							        json.put("status", "failed");
									json.put("message", "Please enter positive value for Threshold");
									LOG.info("dbconsole/activateColumnCheckInTemplate - END");
									return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
							    }
							    else {
								       LOG.debug(value + "Threshold is positive");
								     }
							}else {
								json.put("status", "failed");
								json.put("message", "Please enter valid Threshold");
								LOG.info("dbconsole/activateColumnCheckInTemplate - END");
								return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
							}
							
						}
						
						if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DATERULECHECK)) {
							if (rec.has("dateformat")) {
								String dateformat = rec.getString("dateformat");
								if (dateformat == null || dateformat.trim().equals("")) {
									message = " Please enter correct date format";
									json.put("status", "failed");
									json.put("message", message);
									LOG.error(message);
									LOG.info("dbconsole/activateColumnCheckInTemplate - END");
									return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
								}
							} else {
								message = "DateFormat is missing";
								json.put("status", "failed");
								json.put("message", message);
								LOG.error(message);
								LOG.info("dbconsole/activateColumnCheckInTemplate - END");
								return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
							}
						}
					}

					// Activate column check
					json = dataTemplateDeltaCheckService.activateColumnCheck(idApp, checkName, columnNames);

					status = json.getString("status");
					message = json.getString("message");

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
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/activateColumnCheckInTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/deactivateColumnCheckInTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> deactivateColumnCheckInTemplate(@RequestHeader HttpHeaders headers,
			HttpServletRequest request, HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/deactivateColumnCheckInTemplate - START");
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
				LOG.debug("token " + token.toString());
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
					long idApp = inputJson.getLong("idApp");
					String checkName = inputJson.getString("checkName");
					String columnNames = inputJson.getString("columnNames");
					// Deactivate column check
					json = dataTemplateDeltaCheckService.deactivateColumnCheck(idApp, checkName, columnNames);

					status = json.getString("status");
					message = json.getString("message");

				} else {
					message = "Token expired.";
					LOG.error("Token is expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token is missing in headers";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/deactivateColumnCheckInTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getDashboardValidationDataByFilter", method = RequestMethod.POST)
	public ResponseEntity<String> getDashboardValidationDataByFilter(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getDashboardValidationDataByFilter - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		JSONObject apiResponse = new JSONObject();
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();
		String token = "";
		int domainId = 0;
		int projectId = 0;
		String fromDate = "";
		String toDate = "";

		String msg = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

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
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						domainId = inputJson.getInt("domainId");
						projectId = inputJson.getInt("projectId");
						fromDate = inputJson.getString("fromDate");
						toDate = inputJson.getString("toDate");
						JSONObject filterAttribute = inputJson.getJSONObject("filterAttribute");
						String connectionName = "";
						String fileName = "";
						if (filterAttribute.has("connectionName")) {
							connectionName = filterAttribute.getString("connectionName");
							filterAttribute.remove("connectionName");
						}
						if (filterAttribute.has("fileName")) {
							fileName = filterAttribute.getString("fileName");
							filterAttribute.remove("fileName");
						}
						String filterCondition = dashboardSummaryService.getDQIFilterCondition(filterAttribute);

						// check if domain-project combination is valid or not
						if (projectDAO.isDomainProjectValid(domainId, projectId)) {
							Set<String> attributeKeys = filterAttribute.keySet();
							long templateId = 0l;
							if (attributeKeys.contains("templateId")) {
								templateId = filterAttribute.getLong("templateId");
							}

							if (templateId > 0l)
								masterDashboard = dashboardSummaryService.getValidationListSummaryByFilter(inputJson,
										projectId, domainId, fromDate, toDate, filterCondition, templateId,
										connectionName, fileName);
							else {
								if ((filterCondition == null || filterCondition.isEmpty()) && connectionName.equals("")
										&& fileName.equals(""))
									masterDashboard = dashboardSummaryService.getValidationListSummary(inputJson,
											projectId, domainId, fromDate, toDate);
								else
									masterDashboard = dashboardSummaryService.getValidationListSummaryByFilter(
											inputJson, projectId, domainId, fromDate, toDate, filterCondition,
											templateId, connectionName, fileName);

							}

							if (masterDashboard != null && masterDashboard.size() > 0) {
								JSONArray jsResult = new JSONArray(masterDashboard);
								apiResponse.put("result", jsResult);
								status = "success";
								responseStatus = HttpStatus.OK;
								msg = "DashBoard Validation List successfully build";
								LOG.info("Response merged successfully.");
							} else {
								msg = "Records not available";
								LOG.error(msg);
							}
						} else {
							msg = "Provided Domain-Project Is Invalid";
							LOG.error(msg);
						}
					} catch (JSONException e) {
						LOG.error(e.getMessage());
						msg = "Please Check Input Data";
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
		} catch (JSONException e) {
			LOG.error(e.getMessage());
			msg = "Request failed....";
		}
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getDashboardValidationDataByFilter - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getDashboardDataValidationList", method = RequestMethod.POST)
	public ResponseEntity<String> getDashboardDataValidationList(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getDashboardDataValidationList - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		JSONObject apiResponse = new JSONObject();
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();

		int domainId = 0;
		int projectId = 0;
		String token = "";
		String fromDate = "";
		String toDate = "";

		HttpStatus responseStatus = HttpStatus.NOT_FOUND;
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
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						domainId = inputJson.getInt("domainId");
						projectId = inputJson.getInt("projectId");
						fromDate = inputJson.getString("fromDate");
						toDate = inputJson.getString("toDate");
						if (projectDAO.isDomainProjectValid(domainId, projectId)) {
							masterDashboard = dashboardSummaryService.getValidationListSummary(inputJson, projectId,
									domainId, fromDate, toDate);
							if (masterDashboard != null && masterDashboard.size() > 0) {
								JSONArray jsResult = new JSONArray(masterDashboard);
								apiResponse.put("result", jsResult);
								status = "success";
								responseStatus = HttpStatus.OK;
								LOG.debug("Response merged successfully.");
							} else {
								msg = "Records not available";
								LOG.error("Records not available.");
							}

						} else {
							msg = "Provided Domain-Project Is Invalid";
							LOG.error("Provided Domain-Project is invalid.");
						}
					} catch (JSONException e) {
						msg = "Please Check Input Data";
						LOG.error(msg);
					}

				} else {
					msg = tokenStatusObj.getString("msg");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					LOG.error("Something went wrong.");
				}

			} else {
				msg = "Token is missing in headers";
				LOG.error("Token is missing in headers.");
			}
		} catch (JSONException e) {
			msg = "Request failed....";
			LOG.error(e.getMessage());
		}
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getDashboardDataValidationList - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getValidationExecutiveSummary", method = RequestMethod.POST)
	public ResponseEntity<Object> getValidationExecutiveSummary(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getValidationExecutiveSummary - START");

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
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {

						boolean isRequestValid = false;
						long idApp = 0l;

						// Validate request
						try {
							LOG.debug("Getting request parameters  " + inputJsonStr);
							JSONObject inputJson = new JSONObject(inputJsonStr);
							idApp = inputJson.getLong("idApp");
							isRequestValid = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (isRequestValid) {
							// Validate validation id
							ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);

							if (listApplications != null) {
								// Get Executive summary
								JSONObject obj = dashboardSummaryService.getvalidationExecutiveSummary(idApp);
								if (obj != null && obj.length() > 0) {
									message = obj.getString("message");
									status = obj.getString("status");
									result = obj.getJSONObject("result");
								} else {
									message = "Failed result";
									LOG.error(message);
								}
							} else {
								message = "Invalid Validation Id";
								LOG.error(message);
							}
						} else {
							message = "Invalid request";
							LOG.error(message);
						}
					} else {
						message = "Missing request body";
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
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/getValidationExecutiveSummary - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getValidationForIdApp", method = RequestMethod.POST)
	public ResponseEntity<String> getValidationForIdApp(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getValidationForIdApp - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		JSONObject apiResponse = new JSONObject();
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();

		int inputIdApp = 0;
		String idApp = "";
		String token = "";
		String fromDate = "";
		String toDate = "";
		String trend = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;
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
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						inputIdApp = inputJson.getInt("idApp");
						idApp = Integer.toString(inputIdApp);
						fromDate = inputJson.getString("fromDate");
						toDate = inputJson.getString("toDate");

						masterDashboard = dashboardSummaryService.getSingleValidation(idApp, fromDate, toDate);
						if (masterDashboard != null && masterDashboard.size() > 0) {
							JSONArray jsResult = new JSONArray(masterDashboard);

							try {
								if(idApp!=null) {
									List<Long> avgAggrDQIList = iResultsDAO.getAvgDQIAggregateofLastTwo(Long.parseLong(idApp));
									if(avgAggrDQIList!=null && avgAggrDQIList.size()>0) {
										if(avgAggrDQIList.size()>1) {
											if (avgAggrDQIList.get(0)>=avgAggrDQIList.get(1)) {
												trend ="UP";
											}else  {
												trend ="DOWN";
											}
										}else {
											trend ="NEUTRAL";
										}
									}else {
										trend ="NEUTRAL";
									}
								}
								JSONObject obj = new JSONObject(jsResult.get(0).toString());
								Map<String, Object> result = new HashMap<String, Object>();
								result.put("idApp", obj.getInt("idApp"));
								result.put("validationCheckName", obj.getString("validationCheckName"));
								result.put("date", obj.get("date"));
								result.put("run", obj.getInt("run"));
								result.put("aggreagteDQI", obj.get("aggreagteDQI"));
								result.put("trend", trend);
								apiResponse.put("result", result);

								status = "success";
								responseStatus = HttpStatus.OK;
								LOG.info("Response merged successfully.");

							} catch (Exception e) {
								msg = "Failed to fetch records";
								LOG.error("Failed to fetch records");
							}
						} else {
							msg = "Records not available";
							LOG.error("Records not available.");
						}

					} catch (JSONException e) {
						msg = "Please Check Input Data";
						LOG.error(msg);
					}

				} else {
					msg = tokenStatusObj.getString("msg");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					LOG.error(msg);
				}

			} else {
				msg = "Token is missing in headers";
				LOG.error(msg);
			}
		} catch (JSONException e) {
			msg = "Request failed....";
			LOG.error(e.getMessage());
		}
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getValidationForIdApp - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getValidationCSVReport", method = RequestMethod.POST)
	public void getValidationCSVReport(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getValidationCSVReport - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		JSONObject apiResponse = new JSONObject();
		List<DataQualityMasterDashboard> masterDashboard = new ArrayList<>();

		int domainId = 0;
		int projectId = 0;
		String token = "";
		String fromDate = "";
		String toDate = "";

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
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						domainId = inputJson.getInt("domainId");
						projectId = inputJson.getInt("projectId");
						fromDate = inputJson.getString("fromDate");
						toDate = inputJson.getString("toDate");
						if (projectDAO.isDomainProjectValid(domainId, projectId)) {
							masterDashboard = dashboardSummaryService.getValidationListSummary(inputJson, projectId,
									domainId, fromDate, toDate);
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

//								String[] header = { "Validation ID", "Execution Date", "Run", "Validation Name",
//										"Template Name", "Record Count", "DQI", "Data Drift", "Null Count",
//										"Data Distribution", "Record Anomaly", "Record Count Status" };

								String[] header = { "Validation ID", "Execution Date", "Run", "Connectio Name",
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

						} else {
							msg = "Provided Domain-Project Is Invalid";
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

	@RequestMapping(value = "dbconsole/getValidationDQISummary", method = RequestMethod.POST)
	public ResponseEntity<String> getValidationDQISummary(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getValidationDQISummary - START");
		String msg = "Results data successfully build";
		long idApp = params.get("idApp");
		String status = "failed";
		JSONArray dqResultData = new JSONArray();
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
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
						LOG.debug("Getting request parameters  " + params);
						dqResultData = dashboardSummaryService.getValidationDQISummary(idApp);
						if (dqResultData != null && dqResultData.length() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
						} else {
							msg = "Invalid IdApp";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error(e.getMessage());
						msg = "Invalid IdApp";
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
			msg = "Request failed";
			LOG.error(e.getMessage());
		}

		apiResponse.put("result", dqResultData);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getValidationDQISummary - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/dashboardSummary", method = RequestMethod.POST)
	public ResponseEntity<String> getValidationDQISummary11(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/dashboardSummary - START");
		String msg = "Results data successfully build";
		long idApp = params.get("idApp");
		String status = "failed";
		List<Map<String, Object>> dqResultData = new ArrayList<>();
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					try {
						LOG.debug("Getting request parameters  " + params);
						dqResultData = dashboardSummaryService.getValidationDQISummaryDetails(idApp);
						if (dqResultData != null && dqResultData.size() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
						} else
							msg = "Invalid IdApp";

					} catch (Exception e) {
						e.printStackTrace();
						msg = "Invalid IdApp";
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
			msg = "Request failed";
			LOG.error(e.getMessage());
		}

		apiResponse.put("result", dqResultData);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/dashboardSummary - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	/*
	 * 15-03-2023 Abhijeet Add ValidationDQISummary to show validation name and Ag
	 * DTS in CSV file
	 */
	@RequestMapping(value = "dbconsole/getValidationDQISummaryCSV", method = RequestMethod.POST)
	public void getValidationDQISummaryCSV(@RequestHeader HttpHeaders headers, @RequestBody ValidationDQISummary params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getValidationDQISummaryCSV - START");
		long idApp = params.getIdApp(); // params.get("idApp");
		LOG.debug(params.getIdApp());
		LOG.debug(params.getValidationName());
		LOG.debug(params.getAggregateDTS());

		String token = "";
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
						List<ValidationSummary> validationSummaryList = dashboardSummaryService
								.getValidationDQISummaryCSV(idApp);
						if (validationSummaryList != null && !validationSummaryList.isEmpty()) {
							httpResponse.setContentType("text/csv");
							String csvFileName = "NullCheckReport" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = { "Test", "Dqi", "KeyMetric1Label", "KeyMetric1", "KeyMetric2Label",
									"KeyMetric2", "Status", "KeyMetric3" };
							String[] header = { "Test", "DTS", "Measurement", "Count", "Measurement", "Defects", "Status",
									"More Column Names" };
							String[] validationHeader = { "Validation Name :", params.getValidationName() };
							csvWriter.writeHeader(validationHeader);
							String[] TableHeader = { "Table Name :", params.getTableName() + "" };
							csvWriter.writeHeader(TableHeader);
							String[] DTSHeader = { "Aggregate DTS :", params.getAggregateDTS() + "" };
							csvWriter.writeHeader(DTSHeader);
							csvWriter.writeHeader(header);
							for (ValidationSummary check : validationSummaryList) {
								csvWriter.write(check, fields);
							}
							csvWriter.close();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getValidationDQISummaryCSV - END");
	}

	@RequestMapping(value = "/dbconsole/getEssentialCheckResults", method = RequestMethod.POST)
	public ResponseEntity<String> getEssentialCheckResults(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {

		LOG.info("dbconsole/getEssentialCheckResults - START");
		String msg = "Results data successfully build";
		JSONObject inputJson = new JSONObject(inputsJsonStr);

		String status = "failed";
		JSONObject validationCheckResultsObj = new JSONObject();
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					boolean isRequestParamsValid = false;
					String requestFailureMsg = "";
					boolean flag = true;
					String pattern = "yyyy-MM-dd";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

					if (flag) {
						try {
							long idApp = inputJson.getLong("idApp");
							if (idApp <= 0l)
								flag = false;
						} catch (Exception e) {
							flag = false;
							e.printStackTrace();
						}
						if (!flag)
							requestFailureMsg = "Invalid data in field - idApp";
					}
					if (flag) {
						try {
							String fromDate = inputJson.getString("fromDate");
							if (fromDate != null && !fromDate.trim().isEmpty()) {
								Date f_date = simpleDateFormat.parse(fromDate.trim());
								if (f_date == null)
									flag = false;
							} else
								flag = false;
						} catch (Exception e) {
							flag = false;
							e.printStackTrace();
						}
						if (!flag)
							requestFailureMsg = "Invalid data in field - fromDate";
					}
					if (flag) {
						try {
							String toDate = inputJson.getString("toDate");
							if (toDate != null && !toDate.trim().isEmpty()) {
								Date t_date = simpleDateFormat.parse(toDate.trim());
								if (t_date == null)
									flag = false;
							} else
								flag = false;
						} catch (Exception e) {
							flag = false;
							e.printStackTrace();
						}
						if (!flag)
							requestFailureMsg = "Invalid data in field - toDate";
					}
					if (flag) {
						try {
							String checkName = inputJson.getString("checkName");
							if (checkName != null && !checkName.trim().isEmpty()) {
								if (checkName.contains(">") || checkName.contains("<"))
									flag = false;
							} else
								flag = false;
						} catch (Exception e) {
							flag = false;
							e.printStackTrace();
						}
						if (!flag)
							requestFailureMsg = "Invalid data in field - checkName";
					}
					if (flag) {
						try {
							String colName = inputJson.getString("colName");
							if (colName != null) {
								if (colName.contains(">") || colName.contains("<"))
									flag = false;
							} else
								flag = false;
						} catch (Exception e) {
							flag = false;
							e.printStackTrace();
						}
						if (!flag)
							requestFailureMsg = "Invalid data in field - colName";
					}

					if (flag)
						isRequestParamsValid = true;

					if (isRequestParamsValid) {
						try {
							validationCheckResultsObj = dashboardSummaryService.getStatsForValidationCheck(inputJson);
							if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
								responseStatus = HttpStatus.OK;
								status = "success";
							} else {
								msg = "Records are not found.";
								LOG.error(msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage());
							msg = "Records are not found.";
						}
					} else {
						msg = requestFailureMsg;
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

		apiResponse.put("result", validationCheckResultsObj);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getEssentialCheckResults - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/nullResults", method = RequestMethod.POST)
	public ResponseEntity<String> nullResults(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {

		LOG.info("dbconsole/nullResults - START");
		String msg = "Results data successfully build";
		JSONObject inputJson = new JSONObject(inputsJsonStr);

		String status = "failed";
		JSONObject validationCheckResultsObj = new JSONObject();
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					boolean isRequestParamsValid = false;
					String requestFailureMsg = "";
					boolean flag = true;
					String pattern = "yyyy-MM-dd";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
					isRequestParamsValid = true;

					if (isRequestParamsValid) {
						try {
							validationCheckResultsObj = dashboardSummaryService
									.getStatsForValidationCheckNew(inputJson);
							if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
								responseStatus = HttpStatus.OK;
								status = "success";
							} else {
								msg = "Records are not found.";
								LOG.error(msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage());
							msg = "Records are not found.";
						}
					} else {
						msg = requestFailureMsg;
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

		apiResponse.put("result", validationCheckResultsObj);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/nullResults - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/dataDriftResults", method = RequestMethod.POST)
	public ResponseEntity<String> getDataDrift(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/dataDriftResults - START");
		String msg = "Results data successfully build";
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		JSONObject validationCheckResultsObj = new JSONObject();
		JSONObject apiResponse = new JSONObject();
		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equalsIgnoreCase("success")) {
					try {
						validationCheckResultsObj = dashboardSummaryService.getStatsForDataDrift(inputJson);
						if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
						} else {
							msg = "Records are not found.";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error(e.getMessage());
						msg = "Records are not found.";
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
			msg = "Records are not found.";
		}

		apiResponse.put("result", validationCheckResultsObj);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/dataDriftResults - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getEssentialCheckResultsByFilter", method = RequestMethod.POST)
	public ResponseEntity<String> getEssentialCheckResultsByFilter(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getEssentialCheckResultsByFilter - START");
		String msg = "Results data successfully build";
		JSONObject inputJson = new JSONObject(inputsJsonStr);

		String status = "failed";
		JSONObject validationCheckResultsObj = new JSONObject();
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					try {
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						validationCheckResultsObj = dashboardSummaryService
								.getStatsForValidationCheckByFilter(inputJson);
						if (validationCheckResultsObj != null && validationCheckResultsObj.length() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
						} else {
							msg = "Invalid IdApp";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error(e.getMessage());
						msg = "Invalid IdApp";
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

		apiResponse.put("result", validationCheckResultsObj);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getEssentialCheckResultsByFilter - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/forgotRun", method = RequestMethod.POST)
	public ResponseEntity<Object> forgotRun(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/forgotRun - START");
		Map<String, String> json = new HashMap<String, String>();
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String status = "failed";
		String message = "";

		try {
			long idApp = inputJson.getLong("idApp");
			long maxExecRun = inputJson.getLong("maxExecRun");
			String maxExecDate = inputJson.getString("maxExecDate");
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					if (idApp > 0l) {
						boolean forgotRunStatus = dataQualityResultsService.forgotSelectedRunOfValidation(idApp,
								maxExecDate, maxExecRun, "Y");
						if (forgotRunStatus) {
							status = "success";
							message = "Forget Run executed successfully";
							LOG.info(message);
						} else {
							message = "Failed to perform forget Run action";
							LOG.error(message);
						}
					} else {
						message = "Invalid Validation Id, forget Run action failed";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					json.put("status", status);
					json.put("message", message);
					LOG.error(message);
					return new ResponseEntity<Object>(json, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				message = "Insuffient permissions to perform forget Run action";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "Error occurred while performing forget Run action";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/forgotRun - END");
		return new ResponseEntity<Object>(json, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/unforgotRun", method = RequestMethod.POST)
	public ResponseEntity<Object> unForgotRun(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/unforgotRun - START");
		Map<String, String> json = new HashMap<String, String>();
		String status = "failed";
		String message = "";
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		try {
			long idApp = inputJson.getLong("idApp");
			long maxExecRun = inputJson.getLong("maxExecRun");
			String maxExecDate = inputJson.getString("maxExecDate");
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					if (idApp > 0l) {
						boolean forgotRunStatus = dataQualityResultsService.forgotSelectedRunOfValidation(idApp,
								maxExecDate, maxExecRun, "N");
						if (forgotRunStatus) {
							status = "success";
							message = "Forgotten Run reversed successfully";
							LOG.info(message);
						} else {
							message = "Failed to perform unforgot Run action";
							LOG.error(message);
						}
					} else {
						message = "Invalid Validation Id, unforgot Run action failed";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					json.put("status", status);
					json.put("message", message);
					LOG.error(message);
					return new ResponseEntity<Object>(json, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				message = "Insuffient permissions to perform unforgot Run action";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "Error occurred while performing unforgot Run action";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/unforgotRun - END");
		return new ResponseEntity<Object>(json, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/updateColumnCheckThreshold", method = RequestMethod.POST)
	public ResponseEntity<Object> updateColumnCheckThreshold(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/updateColumnCheckThreshold - START");
		String status = "failed";
		String message = "";
		JSONObject json = new JSONObject();
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		try {
			long idApp = inputJson.getLong("idApp");
			String checkName = inputJson.getString("checkName");
			String column_or_rule_name = inputJson.getString("columnOrRuleName");
			double failed_Threshold = inputJson.getDouble("failedThreshold");
			String defaultPattern = inputJson.getString("defaultPattern");
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					json = dataTemplateDeltaCheckService.updateColumnCheckThreshold(idApp, checkName,
							column_or_rule_name, failed_Threshold, defaultPattern);
					status = json.getString("status");
					message = json.getString("message");
				} else {
					message = "Token expired.";
					json.put("status", status);
					json.put("message", message);
					LOG.error("Token is expired.");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				message = "Insuffient permissions to perform unforgot Run action";
				json.put("status", status);
				json.put("message", message);
				LOG.error(message);
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "Error Occurred, failed to update Threshold";
			json.put("status", status);
			json.put("message", message);
			LOG.error(e.getMessage());
			return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/updateColumnCheckThreshold - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/updateRecordCountAnomalyThreshold", method = RequestMethod.POST)
	public ResponseEntity<Object> updateRecordCountAnomalyThreshold(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/updateRecordCountAnomalyThreshold - START");
		String status = "failed";
		String message = "";
		JSONObject json = new JSONObject();
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			if (token != null && !token.isEmpty()) {
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				if (tokenStatus.equals("success")) {

					if (inputsJsonStr != null && !inputsJsonStr.isEmpty()) {
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						JSONObject inputJson = new JSONObject(inputsJsonStr);
						long idApp = inputJson.getLong("idApp");
						String checkName = inputJson.getString("checkName");
						double failed_Threshold = inputJson.getDouble("failedThreshold");
						json = dataTemplateDeltaCheckService.updateRecordCountAnomalyThreshold(idApp, checkName,
								failed_Threshold);
						status = json.getString("status");
						message = json.getString("message");
						responseStatus = HttpStatus.OK;
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
			e.printStackTrace();
			message = "Error Occurred, failed to update Threshold";
			LOG.error(e.getMessage());
			responseStatus = HttpStatus.OK;
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/updateRecordCountAnomalyThreshold - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getDQIScoresMap", method = RequestMethod.POST)
	public ResponseEntity<String> populateTableAnalysisData(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getDQIScoresMap - START");
		long idApp = params.get("idApp");
		String msg = "";

		String status = "failed";
		JSONObject apiResponse = new JSONObject();

		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				token = "";
			}

			// check if token is empty or not
			if (token != null || !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					try {
						JSONArray allChecksMaps = new JSONArray();
						MultiValueMap map = new MultiValueMap();
						Map<String, String> checkNameMap = getListOfChecks();
						ListApplications listApplicationsData = validationCheckDAO.getdatafromlistapplications(idApp);
						LOG.debug("listApplicationsData:::" + listApplicationsData);

						List<String> qualityCheckNames = iResultsDAO.getDashboardTestNamesByIdapp(idApp);

						for (String checkName : qualityCheckNames) {
							if (dashboardSummaryService.getCheckIsEnabledOrNot(checkName, idApp)) {
								map = dqiGraphDAOI.GetGraphForDashboardWithStatus(idApp, listApplicationsData,
										checkName);
								if (map != null && map.size() > 0) {
									JSONObject checkObj = getCheckObjByMapWithStatus(
											checkNameMap.containsKey(checkName) ? checkNameMap.get(checkName)
													: checkName,
											map);
									if (checkObj.length() > 0)
										allChecksMaps.put(checkObj);
								}
							}
						}
						map = dqiGraphDAOI.getStringFieldFingerprintGraph(idApp, listApplicationsData);
						if (map != null && map.size() > 0) {
							JSONObject checkObj = getCheckObjByMap("String Field Fingerprint", map);
							if (checkObj.length() > 0)
								allChecksMaps.put(checkObj);
						}

						Map<String, String> tranRuleMap = iResultsDAO.getDataFromListDFTranRuleForMap(idApp);
						LOG.debug("tranRuleMap-----> : " + tranRuleMap);
						// map = dqiGraphDAOI.getAggregateDQISummaryGraph(idApp, listApplicationsData,
						// tranRuleMap);
						map = dqiGraphDAOI.getHistoricAvgForAllRunsAggDQI_graph(idApp);
						LOG.debug("map----getHistoricAvgForAllRunsAggDQI_graph---> : " + map.toString());

						if (map != null && map.size() > 0) {
							JSONObject checkObj = getCheckObjByMap("Aggregate DTS Summary", map);
							if (checkObj.length() > 0)
								allChecksMaps.put(checkObj);
						}

						if (allChecksMaps != null && allChecksMaps.length() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
							apiResponse.put("result", allChecksMaps);
							msg = "Results data successfully build";
							LOG.info(msg);
						} else {
							msg = "Invalid idApp";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error(e.getMessage());
						msg = "Invalid idApp";
					}

				} else {
					msg = tokenStatusObj.getString("msg");
					LOG.error(msg);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				msg = "Token is missing in headers";
				LOG.error(msg);
				status = "Failed";
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			msg = "Request failed";
		}

		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getDQIScoresMap - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getEncryptedToken", method = RequestMethod.POST)
	public ResponseEntity<String> getEncryptedToken(@RequestBody String tokenObj) {
		LOG.info("dbconsole/getEncryptedToken - START");
		String msg = "";

		String status = "failed";
		String encryptedToken = "";
		JSONObject apiResponse = new JSONObject();
		JSONObject inputJson = new JSONObject(tokenObj);
		String token;

		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			token = (String) inputJson.get("token");
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
				encryptedToken = encryptor.encrypt(token);
				if (encryptedToken != null && !encryptedToken.isEmpty()) {
					responseStatus = HttpStatus.OK;
					status = "success";
				} else {
					msg = "Invalid Token";
					LOG.error(msg);
				}
			} else {
				msg = "Invalid Token";
				LOG.error(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			msg = "Invalid Token";
		}

		apiResponse.put("result", encryptedToken);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getEncryptedToken - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getDecryptedToken", method = RequestMethod.POST)
	public ResponseEntity<String> getDecryptedToken(@RequestBody String tokenObj) {
		LOG.info("dbconsole/getDecryptedToken - START");

		String msg = "";

		String status = "failed";
		String decryptedToken = "";
		JSONObject apiResponse = new JSONObject();
		JSONObject inputJson = new JSONObject(tokenObj);
		String token;

		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			token = (String) inputJson.get("token");
			token = URLDecoder.decode(token, "UTF-8");
			LOG.debug("token after FE: " + token);
			if (token != null && !token.isEmpty()) {
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();

				decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
				decryptedToken = decryptor.decrypt(token);
				if (decryptedToken != null && !decryptedToken.isEmpty()) {
					responseStatus = HttpStatus.OK;
					status = "success";
				} else {
					msg = "Invalid Token";
					LOG.error(msg);
				}
			} else {
				msg = "Invalid Token";
				LOG.error(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			msg = "Invalid Token";
		}

		apiResponse.put("result", decryptedToken);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getDecryptedToken - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/listOfDGroupValByTableName", method = RequestMethod.POST)
	public ResponseEntity<Object> listOfDGroupValByTableName(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/listOfDGroupValByTableName - START");
		String msg = "";
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				JSONObject inputJson = new JSONObject(request);
				LOG.info("DataQualityResultsController : listOfDGroupValByTableName ");
				List<String> listDGroupVal = new ArrayList<String>();
				String tableName = inputJson.getString("tableName");
				int idApp = inputJson.getInt("idApp");

				if (tableName != null && !tableName.isEmpty()) {
					StringBuffer strBufQuery = new StringBuffer();

					strBufQuery.append(" SELECT DISTINCT dGroupVal FROM ");
					strBufQuery.append(
							tableName + " where idApp=" + idApp + " and dGroupVal is not null and dGroupVal != ''");
					if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
						strBufQuery.append(" and NumMeanThreshold IS NOT NULL");
						strBufQuery.append(" and Null_Threshold is null ");
					}
					SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

					while (sqlRowSet_strDateSqlQuery.next()) {
						listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
					}
					LOG.debug("DataQualityResultsController : listOfDGroupValByTableName : listDGroupVal "
							+ listDGroupVal);
					JSONArray jsonArray = new JSONArray(listDGroupVal);

					List<String> listColName = new ArrayList<String>();
					StringBuffer strBufColNameQuery = new StringBuffer();

					strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
					strBufColNameQuery.append(tableName + " where idApp=" + idApp);
					if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
						strBufColNameQuery.append(" and NumMeanThreshold IS NOT NULL");
						strBufColNameQuery.append(" and Null_Threshold is null ");
					}
					SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1
							.queryForRowSet(strBufColNameQuery.toString());

					while (sqlRowSet_strColNameSqlQuery.next()) {
						listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
					}
					LOG.debug("DataQualityResultsController : listOfDGroupValByTableName : listColName " + listColName);
					JSONArray jsonArrayColName = new JSONArray(listColName);

					JSONObject jsonObj = new JSONObject();
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("dGroupVal", jsonArray);
					result.put("ColName", jsonArrayColName);
					Map<String, Object> response = new HashMap<String, Object>();
					response.put("result", result);
					response.put("status", "success");
					response.put("message", "Successfully got the results.");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					LOG.error("DataQualityResultsController : listOfDGroupValByTableName : table name is empty ");
					return new ResponseEntity<Object>("Table Name is empty.", HttpStatus.BAD_REQUEST);
				}
			} else {
				msg = "Token is missing in headers";
				LOG.error(msg);
				return new ResponseEntity<Object>(msg, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.info("dbconsole/listOfDGroupValByTableName - END");
			return new ResponseEntity<Object>("Error occurred while fetching the Details", HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/listOfDGroupValForColumnByTableName", method = RequestMethod.POST)
	public ResponseEntity<Object> listOfDGroupValForColumnByTableName(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/listOfDGroupValForColumnByTableName - START");
		JSONObject json = new JSONObject();
		List<String> listDGroupVal = new ArrayList<String>();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting request parameters  " + request);
					JSONObject inputJson = new JSONObject(request);
					String tableName = inputJson.getString("tableName");
					String columnName = inputJson.getString("columnName");
					int idApp = inputJson.getInt("idApp");

					boolean isRequestValid = true;
					if (tableName == null || tableName.trim().isEmpty()) {
						message = "Table Name is missing";
						isRequestValid = false;
					} else if (columnName == null || columnName.trim().isEmpty()) {
						message = "Column Name is missing";
						isRequestValid = false;
					} else if (idApp <= 0) {
						message = "idApp is missing";
						isRequestValid = false;
					}

					if (isRequestValid) {

						String sql = "SELECT DISTINCT dGroupVal FROM " + tableName + " where idApp=" + idApp
								+ " and ColName='" + columnName + "'";
						SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(sql);

						while (sqlRowSet_strDateSqlQuery.next()) {
							listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
						}
						status = "success";

					} else {
						responseStatus = HttpStatus.BAD_REQUEST;
					}
				} else {
					message = "Token expired.";
					LOG.error("Token is expired.");
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
		json.put("result", new JSONArray(listDGroupVal));
		LOG.info("dbconsole/listOfDGroupValForColumnByTableName - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);

	}

	@RequestMapping(value = "/dbconsole/listOfDistributionCheckColumnsByTableName", method = RequestMethod.POST)
	public ResponseEntity<Object> listOfDistributionCheckColumnsByTableName(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/listOfDistributionCheckColumnsByTableName - START");
		JSONObject json = new JSONObject();
		List<String> listColName = new ArrayList<String>();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting request parameters  " + request);
					JSONObject inputJson = new JSONObject(request);
					String tableName = inputJson.getString("tableName");
					int idApp = inputJson.getInt("idApp");

					boolean isRequestValid = true;
					if (tableName == null || tableName.trim().isEmpty()) {
						message = "Table Name is missing";
						isRequestValid = false;
					} else if (idApp <= 0) {
						message = "idApp is missing";
						isRequestValid = false;
					}

					if (isRequestValid) {
						String sql = "SELECT DISTINCT ColName FROM " + tableName + " where idApp=" + idApp;
						SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);

						while (rs.next()) {
							listColName.add(rs.getString("ColName"));
						}
						status = "success";
					} else {
						responseStatus = HttpStatus.BAD_REQUEST;
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
		json.put("result", new JSONArray(listColName));
		LOG.info("dbconsole/listOfDistributionCheckColumnsByTableName - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/countReasionabilitySumDgroupCluster", method = RequestMethod.POST)
	public ResponseEntity<Object> countReasionabilitySumDgroupCluster(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/countReasionabilitySumDgroupCluster - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				int idApp = inputJson.getInt("idApp");
				if ((tableName != null && !tableName.isEmpty())) {
					responseMap.put("result", dashboardSummaryService
							.getcountReasionabilitySumDgroupClusterMapJsonObject(tableName, idApp));
					responseMap.put("message", "Successfully got the response.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("message", "Tablename or IDApp is missing.");
					responseMap.put("status", "Failed");
					LOG.error("Tablename or IDApp is missing.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			responseMap.put("message", "Error occurred while fetching the details");
			responseMap.put("status", "Failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getAlertEventById - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/avgTrendChart", method = RequestMethod.POST)
	public ResponseEntity<Object> avgTrendChart(@RequestBody String request, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/avgTrendChart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String DGroupVal = inputJson.getString("dGroupVal");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				String toDate = inputJson.getString("toDate");
				if ((tableName != null && !tableName.isEmpty()) && (DGroupVal != null && !DGroupVal.isEmpty())
						&& (colName != null && !colName.isEmpty())) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("children", distributionCheckResultsService.getAvgTrendChartDataForAngular(tableName,
							DGroupVal, colName, idApp + ""));
					responseMap.put("result", result);
					responseMap.put("message", "Successfully got trend chart data.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			System.err.println("DataQualityResultsController : avgTrendChart : Exception :: " + e);
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/avgTrendChart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/listOfDGroupValByTableNameForDistribution", method = RequestMethod.POST)
	private ResponseEntity<Object> listOfDGroupValByTableNameForDistribution(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/listOfDGroupValByTableNameForDistribution - START");
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				List<String> listDGroupVal = new ArrayList<String>();
				String tableName = inputJson.getString("tableName");
				int idApp = inputJson.getInt("idApp");
				if (tableName != null && !tableName.isEmpty()) {
					StringBuffer strBufQuery = new StringBuffer();

					strBufQuery.append(" SELECT DISTINCT dGroupVal FROM ");
					strBufQuery.append(tableName + " where idApp=" + idApp);

					SqlRowSet sqlRowSet_strDateSqlQuery = jdbcTemplate1.queryForRowSet(strBufQuery.toString());

					while (sqlRowSet_strDateSqlQuery.next()) {
						listDGroupVal.add(sqlRowSet_strDateSqlQuery.getString("dGroupVal"));
					}
					JSONArray jsonArray = new JSONArray(listDGroupVal);

					List<String> listColName = new ArrayList<String>();
					StringBuffer strBufColNameQuery = new StringBuffer();

					strBufColNameQuery.append(" SELECT DISTINCT ColName FROM ");
					strBufColNameQuery.append(tableName + " where idApp=" + idApp);

					SqlRowSet sqlRowSet_strColNameSqlQuery = jdbcTemplate1
							.queryForRowSet(strBufColNameQuery.toString());

					while (sqlRowSet_strColNameSqlQuery.next()) {
						listColName.add(sqlRowSet_strColNameSqlQuery.getString("ColName"));
					}
					JSONArray jsonArrayColName = new JSONArray(listColName);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("dGroupVal", jsonArray);
					result.put("ColName", jsonArrayColName);
					Map<String, Object> response = new HashMap<String, Object>();
					response.put("result", result);
					response.put("status", "success");
					response.put("message", "Successfully got the results.");
					LOG.info("dbconsole/listOfDGroupValByTableNameForDistribution - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					LOG.error("Table Name is empty.");
					return new ResponseEntity<Object>("Table Name is empty.", HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.error("Token is missing in headers.");
				return new ResponseEntity<Object>("Token is missing in headers", HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.info("dbconsole/listOfDGroupValByTableNameForDistribution - END");
			return new ResponseEntity<Object>("Error occurred while fetching the details", HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/rollupAnalysisDropdownDataLoad", method = RequestMethod.POST)
	public ResponseEntity<Object> rollupAnalysisDropdownDataLoad(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/rollupAnalysisDropdownDataLoad - START");
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				int idApp = inputJson.getInt("idApp");
				if (tableName != null && !tableName.isEmpty()) {
					Map<String, Object> response = new HashMap<String, Object>();
					response.put("result",
							dashboardSummaryService.rollupAnalysisDropdownDataLoadJsonObj(tableName, idApp));
					response.put("status", "success");
					response.put("message", "Successfully got the results.");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					LOG.error("Table Name is empty.");
					
					return new ResponseEntity<Object>("Table Name is empty.", HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>("Token is missing in headers", HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.info("dbconsole/rollupAnalysisDropdownDataLoad - END");
			return new ResponseEntity<Object>("Error occurred while fetching the details", HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/stdDevavgTrendChart", method = RequestMethod.POST)
	public ResponseEntity<Object> stdDevavgTrendChart(@RequestBody String request, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/stdDevavgTrendChart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String DGroupVal = inputJson.getString("dGroupVal");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				String toDate = inputJson.getString("toDate");
				if ((tableName != null && !tableName.isEmpty()) && (colName != null && !colName.isEmpty())) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("children", distributionCheckResultsService
							.getStdDevAvgTrendChartDataForAngular(tableName, DGroupVal, colName, idApp + ""));
					responseMap.put("result", result);
					responseMap.put("message", "Successfully got trend chart data.");
					responseMap.put("status", "success");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/stdDevavgTrendChart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/sumOfNumStatsChart", method = RequestMethod.POST)
	public ResponseEntity<Object> sumOfNumStatsChart(@RequestBody String request, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/sumOfNumStatsChart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String dGroupVal = inputJson.getString("dGroupVal");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				int run = inputJson.getInt("run");
				String toDate = inputJson.getString("toDate");
				if ((tableName != null && !tableName.isEmpty()) && (dGroupVal != null && !dGroupVal.isEmpty())
						&& (colName != null && !colName.isEmpty())) {
					responseMap.put("result", distributionCheckResultsService
							.getsumOfNumStatsChartDataForAngular(tableName, dGroupVal, colName, run, idApp + ""));
					responseMap.put("message", "Successfully got total volume chart data.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/sumOfNumStatsChart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/cmpTrendChart", method = RequestMethod.POST)
	public ResponseEntity<Object> distrubutionCheckTrendChart(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/cmpTrendChart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String strListOfDGroupVal = inputJson.getString("dGroupValList");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				String toDate = inputJson.getString("toDate");
				if ((tableName != null && !tableName.isEmpty()) && (colName != null && !colName.isEmpty())
						&& (strListOfDGroupVal != null && !strListOfDGroupVal.isEmpty())) {
					String strCommaSeperatedListOfDGroupVal = removeLastCharacterOfString(strListOfDGroupVal);
					List<String> listOfDGroupVal = Arrays.asList(strCommaSeperatedListOfDGroupVal.split("\\s*,\\s*"));
					responseMap.put("result",
							distributionCheckResultsService.getdistrubutionCheckTrendChartDetailsForAngular(tableName,
									colName, listOfDGroupVal, idApp + ""));
					responseMap.put("message", "Successfully got total volume chart data.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/cmpTrendChart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/rollUpTrendChart", method = RequestMethod.POST)
	public ResponseEntity<Object> listOfRollUpVariableByTableName(@RequestBody String request,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/rollUpTrendChart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String rollupVariable = inputJson.getString("rollupVariable");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
						&& (colName != null && !colName.isEmpty())) {
					int intIndexOfRollUpVariable = dashboardSummaryService.getLocationOfDGroupCal(rollupVariable,
							tableName, idApp);
					StringBuffer strLocalrollupVariable = new StringBuffer();

					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						if (intIndexOfRollUpVariable == 1) {
							strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
						} else if (intIndexOfRollUpVariable > 1) {
							strLocalrollupVariable
									.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
						}

					} else {
						if (intIndexOfRollUpVariable == 1) {
							strLocalrollupVariable.append("SUBSTRING_INDEX(dGroupVal, '?::?', 1)");
						} else if (intIndexOfRollUpVariable > 1) {
							strLocalrollupVariable.append("SUBSTRING_INDEX(SUBSTRING_INDEX(dGroupVal, '?::?', "
									+ intIndexOfRollUpVariable + "), '?::?', -1)");
						}
					}

					String sql = "SELECT   distinct " + strLocalrollupVariable + " AS " + rollupVariable + " FROM "
							+ tableName + " WHERE idApp=" + idApp + " and ColName = '" + colName + "'  order by "
							+ strLocalrollupVariable;

					LOG.debug("DataQualityResultsController : callRollUpComparisonChart : strBufQuery :: " + sql);

					SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(sql);
					List<String> listRollUpVariable = new ArrayList<String>();
					while (sqlRowSet_strSqlQuery.next()) {
						listRollUpVariable.add(sqlRowSet_strSqlQuery.getString(rollupVariable));
					}
					responseMap.put("result", listRollUpVariable);
					responseMap.put("message", "Chart data got successfully.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);

				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/rollUpTrendChart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/microTrendchart", method = RequestMethod.POST)
	public ResponseEntity<Object> googleChart(@RequestBody String request, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/microTrendchart - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String toDate = inputJson.getString("toDate");
				int idApp = inputJson.getInt("idApp");
				if ((tableName != null && !tableName.isEmpty()) && idApp != 0) {
					responseMap.put("result", dashboardSummaryService.getLineChartDetails(tableName, idApp, toDate));
					responseMap.put("message", "Chart data got successfully.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/microTrendchart - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/rollupdatatable", method = RequestMethod.POST)
	public ResponseEntity<Object> rollupdatatable(@RequestBody String request, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/rollupdatatable - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			String token = "";
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (token != null && !token.isEmpty()) {
				LOG.debug("Getting request parameters  " + request);
				JSONObject inputJson = new JSONObject(request);
				String tableName = inputJson.getString("tableName");
				String rollupVariable = inputJson.getString("rollupVariable");
				String colName = inputJson.getString("colName");
				int idApp = inputJson.getInt("idApp");
				List<Map<String, Object>> dataArray = new ArrayList<>();
				int recordCounter = 0;
				if ((tableName != null && !tableName.isEmpty()) && (rollupVariable != null && !rollupVariable.isEmpty())
						&& (colName != null && !colName.isEmpty())) {

					StringBuffer strLocalrollupVariable = new StringBuffer();
					int intIndexOfRollUpVariable = getLocationOfDGroupCal(rollupVariable, tableName, idApp);

					// Query compatibility changes for both POSTGRES and MYSQL
					if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
						if (intIndexOfRollUpVariable == 1) {
							strLocalrollupVariable.append("SPLIT_PART(dGroupVal, '?::?', 1)");
						} else if (intIndexOfRollUpVariable > 1) {
							strLocalrollupVariable
									.append("SPLIT_PART(dGroupVal, '?::?', " + intIndexOfRollUpVariable + ")");
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
					if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")) {
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
								+ "FROM " + tableName + " AS dqcs WHERE dqcs.idApp=" + idApp + " and ColName = '"
								+ colName + "' " + condition + " group by " + strLocalrollupVariable
								+ ",ColName,dqcs.Date,dqcs.Run ORDER BY " + strLocalrollupVariable
								+ ", ColName,dqcs.Date, dqcs.Run;";

					} else {

						sql = "SELECT dqcs.Date as date, dqcs.Run as run, dqcs.ColName as colName, "
								+ "CASE WHEN (ABS(SUM(dqcs.sumOfNumStat)-AVG(sumOfNumStat))>(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end))) THEN 'Failed' ELSE 'Passed' END AS status , "
								+ "SUM(dqcs.Record_Count) AS recordCount,ROUND(MIN(dqcs.Min),2) AS min, ROUND(MAX(dqcs.Max),2) AS max, "
								+ "ROUND(AVG(dqcs.NumMeanThreshold),2) AS numMeanThreshold, ROUND(SUM(dqcs.sumOfNumStat),2) AS sumOfNumStat ,  ROUND(AVG(sumOfNumStat)) AS  histAvg,"
								+ "CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE ROUND(STDDEV(sumOfNumStat),2) end AS histStdDev, "
								+ strLocalrollupVariable
								+ " AS rollUpColumn, ROUND(AVG(dqcs.Mean),2) AS mean , ROUND((AVG(sumOfNumStat)+(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as upperLimit , "
								+ "ROUND((AVG(sumOfNumStat)-(AVG(NumMeanThreshold)*(CASE WHEN (STDDEV(sumOfNumStat) = 0 OR STDDEV(sumOfNumStat) IS NULL) THEN 0.5 ELSE STDDEV(sumOfNumStat) END))),2) as lowerLimit "
								+ "FROM " + tableName + " AS dqcs WHERE dqcs.idApp=" + idApp + " and ColName = '"
								+ colName + "' " + condition + " group by " + strLocalrollupVariable
								+ ",ColName,dqcs.Date,dqcs.Run ORDER BY " + strLocalrollupVariable
								+ ", ColName,dqcs.Date, dqcs.Run;";
					}

					LOG.debug("DataQualityResultsController : rollupdatatable : strBufQuery :: " + sql);

					SqlRowSet sqlRowSet_strSqlQuery = jdbcTemplate1.queryForRowSet(sql);

					String[] columnNames = { "date", "run", "colName", "status", "recordCount", "min", "max",
							"numMeanThreshold", "sumOfNumStat", "histAvg", "histStdDev", "rollUpColumn", "mean",
							"upperLimit", "lowerLimit" };

					DecimalFormat numberFormat = new DecimalFormat("#0.00");

					while (sqlRowSet_strSqlQuery.next()) {
						int colIndex = 1;
						Map<String, Object> data = new HashMap<String, Object>();

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

							data.put(columnName, columnValue);
							++colIndex;
						}
						dataArray.add(data);
						++recordCounter;
					}

					Map<String, Object> result = new HashMap<String, Object>();
					result.put("iTotalRecords", recordCounter);
					result.put("iTotalDisplayRecords", recordCounter);
					result.put("aaData", dataArray);
					responseMap.put("result", result);
					responseMap.put("message", "Record got successfully.");
					responseMap.put("status", "success");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);

				} else {
					responseMap.put("result", new ArrayList<>());
					responseMap.put("message", "Unable to fetch chart data.");
					responseMap.put("status", "failed");
					LOG.error("Unable to fetch chart data.");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.BAD_REQUEST);
				}
			} else {
				responseMap.put("result", new ArrayList<>());
				responseMap.put("message", "Token is missing in headers");
				responseMap.put("status", "Failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			responseMap.put("result", new ArrayList<>());
			responseMap.put("message", "Unable to fetch chart data.");
			responseMap.put("status", "failed");
			LOG.info("dbconsole/rollupdatatable - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

	}

	private int getLocationOfDGroupCal(String paramStrRollUpVariable, String tableName, int idApp) {
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
			int intIndex = listdGroupCol.indexOf(paramStrRollUpVariable);
			int returnVal = intIndex + 1;
			return returnVal;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private String removeLastCharacterOfString(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	private JSONObject getCheckObjByMap(String checkName, MultiValueMap map) {
		Set<String> dates = map.keySet();
		JSONObject checkObj = new JSONObject();
		JSONArray graphData = new JSONArray();
		DecimalFormat df = new DecimalFormat("#0.00");
		for (String dateVal : dates) {
			JSONObject dateDqiObj = new JSONObject();
			dateDqiObj.put("date", dateVal);
			try {
				List<Double> dqiArr = (List<Double>) map.get(dateVal);
				dateDqiObj.put("dqi", Double.parseDouble(df.format(dqiArr.get(0))));
				graphData.put(dateDqiObj);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (graphData.length() > 0) {
			checkObj.put("dqiRecordName", checkName);
			checkObj.put("graphData", graphData);
		}
		return checkObj;
	}

	private JSONObject getCheckObjByMapWithStatus(String checkName, MultiValueMap map) {
		Set<String> dates = map.keySet();
		JSONObject checkObj = new JSONObject();
		JSONArray graphData = new JSONArray();
		DecimalFormat df = new DecimalFormat("#0.00");
		for (String dateVal : dates) {
			JSONObject dateDqiObj = new JSONObject();
			dateDqiObj.put("date", dateVal);
			try {
				List<String> dqiArr = (List<String>) map.get(dateVal);
				dateDqiObj.put("dqi", Double.parseDouble(df.format(dqiArr.get(0))));
				dateDqiObj.put("status", dqiArr.get(1));
				graphData.put(dateDqiObj);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (graphData.length() > 0) {
			checkObj.put("dqiRecordName", checkName);
			checkObj.put("graphData", graphData);
		}
		return checkObj;
	}

	private Map<String, String> getListOfChecks() {
		Map<String, String> validationCheckTestNames = new HashMap<>();
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
		validationCheckTestNames.put("DQ_Timeliness", "Time sequence");
		validationCheckTestNames.put("DQ_Sql_Rule", "SQL Rules");
		validationCheckTestNames.put("DQ_Rules", "Custom Rules");
		validationCheckTestNames.put("DQ_GlobalRules", "Custom Rules");
		validationCheckTestNames.put("DQ_MaxLengthCheck", "Max Length check");
		return validationCheckTestNames;
	}

	@RequestMapping(value = "/dbconsole/getRCATrendChart", method = RequestMethod.POST)
	public ResponseEntity<Object> getRCATrendChart(@RequestHeader HttpHeaders headers, HttpServletRequest request,
			HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getRCATrendChart - START");
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
				LOG.debug("token " + token.toString());
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
					long idApp = inputJson.getLong("idApp");
					String fromDate = inputJson.getString("fromDate");
					String toDate = inputJson.getString("toDate");
					// getRCATrendChart
					JSONArray chartDetails = iResultsDAO.getRCATrendChart(idApp, fromDate, toDate);
					result.put("chartDetails", chartDetails);
					status = "success";
					message = "success";
					LOG.info(message);

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
		json.put("result", result);
		LOG.info("dbconsole/getRCATrendChart - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}


	@RequestMapping(value = "/dbconsole/getGraphDataForNullStatistics", method = RequestMethod.POST)
	public ResponseEntity<Object> getGraphDataForNullStatistics(@RequestHeader HttpHeaders headers, HttpServletRequest request,
												   HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getGraphDataForNullStatistics - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		JSONArray graphDataNullArr = new JSONArray();

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
										   
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					JSONObject params = new JSONObject(inputsJsonStr);

					long idApp = params.getLong("idApp");
					String colName = params.getString("colName");

					// getGraphDataForNullCheck
					graphDataNullArr = iResultsDAO.getGraphDataForNullStatistics(idApp, colName);
					if(graphDataNullArr!=null && graphDataNullArr.length() > 0){
						status = "success";
						message = "success";
					}else{
						message= "Failed to obtain results for given input parameters. Please check the input";
						graphDataNullArr=  new JSONArray();
					}

					LOG.info(message);

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
		json.put("result", graphDataNullArr);
		LOG.info("dbconsole/getGraphDataForNullStatistics - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}


	@RequestMapping(value = "/dbconsole/getGraphDataForLengthStatistics", method = RequestMethod.POST)
	public ResponseEntity<Object> getGraphDataForLengthStatistics(@RequestHeader HttpHeaders headers, HttpServletRequest request,
																HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getGraphDataForLengthStatistics - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		JSONArray graphDataLengthArr = new JSONArray();

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					JSONObject params = new JSONObject(inputsJsonStr);

					long idApp = params.getLong("idApp");
					String colName = params.getString("colName");

					// getGraphDataForLength
					graphDataLengthArr = iResultsDAO.getGraphDataForLengthStatistics(idApp, colName);
					if(graphDataLengthArr!=null && graphDataLengthArr.length() > 0){
						status = "success";
						message = "success";
					}else{
						message= "Failed to obtain results for given input parameters. Please check the input";
						graphDataLengthArr=  new JSONArray();
					}

					LOG.info(message);

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
		json.put("result", graphDataLengthArr);
		LOG.info("dbconsole/getGraphDataForLengthStatistics - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
	
	@RequestMapping(value = "/dbconsole/getGraphDataForBadDataStatistics", method = RequestMethod.POST)
	public ResponseEntity<Object> getGraphDataForBadDataStatistics(@RequestHeader HttpHeaders headers, HttpServletRequest request,
																HttpServletResponse response, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getGraphDataForBadDataStatistics - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;
		JSONArray graphDataBadDataArr = new JSONArray();

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					JSONObject params = new JSONObject(inputsJsonStr);

					long idApp = params.getLong("idApp");
					String colName = params.getString("colName");

					// getGraphDataForBadData
					graphDataBadDataArr = iResultsDAO.getGraphDataForBadDataStatistics(idApp, colName);
					if(graphDataBadDataArr!=null && graphDataBadDataArr.length() > 0){
						status = "success";
						message = "success";
					}else{
						message= "Failed to obtain results for given input parameters. Please check the input";
						graphDataBadDataArr=  new JSONArray();
					}

					LOG.info(message);

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
		json.put("result", graphDataBadDataArr);
		LOG.info("dbconsole/getGraphDataForBadDataStatistics - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

    @RequestMapping(value = "/dbconsole/getGraphDataForRegexPatternStatistics", method = RequestMethod.POST)
    public ResponseEntity<Object> getGraphDataForRegexPatternStatistics(@RequestHeader HttpHeaders headers, HttpServletRequest request,
                                                                  HttpServletResponse response, @RequestBody String inputsJsonStr) {
        LOG.info("dbconsole/getGraphDataForRegexPatternStatistics - START");
        JSONObject json = new JSONObject();
        String status = "failed";
        String message = "";
        String token = "";

        // Default response status
        HttpStatus responseStatus = HttpStatus.OK;
        JSONArray graphDataRegexArr = new JSONArray();

        try {
            // Get token from request header
            try {
                token = headers.get("token").get(0);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Validate token
            if (token != null && !token.isEmpty()) {

                // Check if token is expired or not
                JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
                String tokenStatus = tokenStatusObj.getString("status");

                if (tokenStatus.equalsIgnoreCase("success")) {
                    LOG.debug("Getting request parameters  " + inputsJsonStr);
                    JSONObject params = new JSONObject(inputsJsonStr);

                    long idApp = params.getLong("idApp");
                    String colName = params.getString("colName");

                    // getGraphDataForRegexPattern
                    graphDataRegexArr = iResultsDAO.getGraphDataForRegexPatternStatistics(idApp, colName);
                    if(graphDataRegexArr!=null && graphDataRegexArr.length() > 0){
                        status = "success";
                        message = "success";
                    }else{
                        message= "Failed to obtain results for given input parameters. Please check the input";
                        graphDataRegexArr=  new JSONArray();
                    }

                    LOG.info(message);

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
        json.put("result", graphDataRegexArr);
        LOG.info("dbconsole/getGraphDataForRegexPatternStatistics - END");
        return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/getGraphDataForDuplicateStatistics", method = RequestMethod.POST)
    public ResponseEntity<Object> getGraphDataForDuplicateStatistics(@RequestHeader HttpHeaders headers, HttpServletRequest request,
                                                                        HttpServletResponse response, @RequestBody String inputsJsonStr) {
        LOG.info("dbconsole/getGraphDataForDuplicateStatistics - START");
        JSONObject json = new JSONObject();
        String status = "failed";
        String message = "";
        String token = "";

        // Default response status
        HttpStatus responseStatus = HttpStatus.OK;
        JSONArray graphDataDuplicateArr = new JSONArray();

        try {
            // Get token from request header
            try {
                token = headers.get("token").get(0);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Validate token
            if (token != null && !token.isEmpty()) {

                // Check if token is expired or not
                JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
                String tokenStatus = tokenStatusObj.getString("status");

                if (tokenStatus.equalsIgnoreCase("success")) {
                    LOG.debug("Getting request parameters  " + inputsJsonStr);
                    JSONObject params = new JSONObject(inputsJsonStr);

                    long idApp = params.getLong("idApp");
                    String colName = params.getString("colName");

                    // getGraphDataForDuplicate
                    graphDataDuplicateArr = iResultsDAO.getGraphDataForDuplicateStatistics(idApp, colName);
                    if(graphDataDuplicateArr!=null && graphDataDuplicateArr.length() > 0){
                        status = "success";
                        message = "success";
                    }else{
                        message= "Failed to obtain results for given input parameters. Please check the input";
                        graphDataDuplicateArr=  new JSONArray();
                    }

                    LOG.info(message);

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
        json.put("result", graphDataDuplicateArr);
        LOG.info("dbconsole/getGraphDataForDuplicateStatistics - END");
        return new ResponseEntity<Object>(json.toString(), responseStatus);
    }
 
	//Get Table Summary & Show it on Dashboard Screen - Vinav 20-07-2023
	@RequestMapping(value = "/dbconsole/getAllTableDetailsCountSummary", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> getAllTableDetailsCountSummary(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAllTableDetailsCountSummary - START");
		DashboardTableCountSummary dashboardTableCountSummary = null;
		Map<String, Object> response = new HashMap<String, Object>();  		  
		String token = "";
		HttpStatus responseStatus=HttpStatus.OK;
											   
		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					Map<String, Object> result = new HashMap<String, Object>();
					dashboardTableCountSummary = dashboardSummaryService.getAllTableDetailsCountSummary();
					result.put("dashboardTableCountSummary", dashboardTableCountSummary);
					response.put("result", result);
					response.put("status", "success");
					response.put("message", "Successfully fetched records.");
					responseStatus = HttpStatus.OK;
					LOG.info("success");

				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				responseStatus = HttpStatus.EXPECTATION_FAILED;
				response.put("status", "failed");
				response.put("message", "Token is expired.");
				LOG.error("Token is missing in headers");
					   
			}
		} catch (Exception e) {
			responseStatus = HttpStatus.OK;
			LOG.error(e.getMessage());
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
		}					   
		LOG.info("dbconsole/getAllTableDetailsCountSummary - END");
		return new ResponseEntity<Object>(response, responseStatus);
	}
	
	//Get Table Summary & Show it on Dashboard Screen - Vinav 20-07-2023
		@RequestMapping(value = "/dbconsole/runTableDetailsCountSummary", method = RequestMethod.GET)
		public @ResponseBody ResponseEntity<String> runTableDetailsCountSummary(@RequestHeader HttpHeaders headers, HttpServletRequest request,
				HttpServletResponse response) {
			LOG.info("dbconsole/runTableDetailsCountSummary - START");
			String genericResponse = "";
			String token = "";
			HttpStatus responseStatus=HttpStatus.OK;
			try {
				// Get token from request header
				try {
					token = headers.get("token").get(0);
					LOG.debug("token " + token.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Validate token
				if (token != null && !token.isEmpty()) {

					// Check if token is expired or not
					JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
					String tokenStatus = tokenStatusObj.getString("status");

					if (tokenStatus.equals("success")) {
						dashboardSummaryService.runTableDetailsCountSummary();
						LOG.info("success");
						genericResponse = "Success";
					} else {
						responseStatus = HttpStatus.EXPECTATION_FAILED;
					}
				} else {
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					LOG.error("Token is missing in headers");
				}
			} catch (Exception e) {
				responseStatus = HttpStatus.OK;
				LOG.error(e.getMessage());
				e.printStackTrace();
				genericResponse = "Error Occurred";
			}
			LOG.info("dbconsole/runTableDetailsCountSummary - END");
			return new ResponseEntity<String>(genericResponse, responseStatus);
		}
		
		
		//Get Date Vs Dts Graph Data - Vinav 20-07-2023
		@RequestMapping(value = "/dbconsole/getDateVsDtsGraphData", method = RequestMethod.POST)
		public @ResponseBody ResponseEntity<Object> getDateVsDtsGraphData(@RequestBody String inputsJsonStr, @RequestHeader HttpHeaders headers, HttpServletRequest request) {
			LOG.info("dbconsole/runTableDetailsCountSummary - START");
			String token = "";
			HttpStatus responseStatus=HttpStatus.OK;
			Map<String, Object> response = new HashMap<String, Object>();  
			List<DateVsDTSGraph> listDateVsDTSGraph = new ArrayList<DateVsDTSGraph>();
			try {
				// Get token from request header
				try {
					token = headers.get("token").get(0);
					LOG.debug("token " + token.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Validate token
				if (token != null && !token.isEmpty()) {

					// Check if token is expired or not
					JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
					String tokenStatus = tokenStatusObj.getString("status");

					if (tokenStatus.equals("success")) {
						JSONObject params = new JSONObject(inputsJsonStr);
	                    long idApp = params.getLong("idApp");
	                    String fromDate = params.getString("fromDate");
	                    String toDate = params.getString("toDate");
	                    
	                    Map<String, Object> result = new HashMap<String, Object>();
	                    listDateVsDTSGraph = dashboardSummaryService.getDateVsDtsGraphData(idApp,fromDate,toDate);
						result.put("listDateVsDTSGraph", listDateVsDTSGraph);
						response.put("result", result);
						response.put("status", "success");
						response.put("message", "Successfully fetched records.");
					} else {
						responseStatus = HttpStatus.EXPECTATION_FAILED;
						response.put("status", "failed");
						response.put("message", "Token is expired.");
						LOG.error("Token is missing in headers");
					}
				} else {
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is missing in headers");
				}
			} catch (Exception e) {
				responseStatus = HttpStatus.OK;
				LOG.error(e.getMessage());
				e.printStackTrace();
				response.put("message", e.getMessage());
				response.put("status", "failed");
			}
			LOG.info("dbconsole/runTableDetailsCountSummary - END");
			return new ResponseEntity<Object>(response, responseStatus);
		}
}
