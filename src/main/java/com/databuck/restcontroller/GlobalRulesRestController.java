package com.databuck.restcontroller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.databuck.bean.*;
import com.databuck.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ajax.JSON;
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
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.constants.DatabuckConstants;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.GlobalRuleService;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.CSVGenerator;
import com.databuck.util.DateUtility;
import com.databuck.util.ToCamelCase;
import com.google.common.collect.Multimap;

import static com.databuck.util.DatabuckUtility.getDatabuckHome;

@CrossOrigin(origins = "*")
@RestController
public class GlobalRulesRestController {

	@Autowired
	private GlobalRuleService globalRuleService;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	ITaskDAO iTaskDAO;

	@Autowired
	IListDataSourceDAO oListDataSourceDAO;

	@Autowired
	private ChecksCSVService csvService;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	GlobalRuleDAO globalRuleDAO;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private CSVGenerator csvGenerator;

	public static final Logger LOG = Logger.getLogger(GlobalRulesRestController.class);

	@RequestMapping(value = "/dbconsole/getEligibleGlobalRulesForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getEligibleGlobalRulesForTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getEligibleGlobalRulesForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getEligibleGlobalRulesForTemplate(params.get("idData"));
						String linkedGlobalRules = oListDataSourceDAO
								.getAlreadyLinkedGlobalRulesToDataTemplate(params.get("idData"));
						List<String> columnsList = validationcheckdao
								.getDisplayNamesFromListDataDefinition(params.get("idData"));
						JSONObject oJsonResponse = new JSONObject();
						oJsonResponse.put("Rules", eligibleGlobalRulesJson.get("DataSet"));
						oJsonResponse.put("AlreadyLinkedGlobalRulesToDataTemplate", linkedGlobalRules);
						oJsonResponse.put("ColumnsList", String.join(",", columnsList));
						response.put("result", oJsonResponse.toMap());
						response.put("status", "success");
						response.put("message", "Custom rules fetched successfully.");
						LOG.info("Custom rules fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getEligibleGlobalRulesForTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getEligibleGlobalRulesCSV", method = RequestMethod.POST)
	public void getEligibleGlobalRulesCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getEligibleGlobalRulesCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						httpResponse.setContentType("text/csv");
						String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getEligibleGlobalRulesForTemplate(params.get("idData"));
						JSONArray headerValues = new JSONArray();
						JSONArray columns = new JSONArray();

						headerValues.put("Domain").put("Rule Name").put("Rule Type").put("Columns")
								.put("Filter Condition").put("Right Template Filter Condition").put("Matching Rules")
								.put("Expression").put("Threshold").put("Dimension").put("Defect Code")
								.put("Rule Description").put("Review Comments");
						columns.put("DomainName").put("Name").put("RuleType").put("AnchorColumns")
								.put("FilterCondition").put("Right Template Filter Condition").put("MatchingRules")
								.put("EffectiveExpression").put("Threshold").put("Dimension").put("DefectCode")
								.put("RuleDescription").put("ReviewComments");
						String csvString = csvGenerator.getCSVString(eligibleGlobalRulesJson.getJSONArray("DataSet"),
								headerValues, columns);
						httpResponse.getWriter().append(csvString);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getEligibleGlobalRulesCSV - END");
	}

	@RequestMapping(value = "/dbconsole/getNonEligibleGlobalRulesForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getEligibleOtherGlobalRulesForTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("getNonEligibleGlobalRulesForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + params);
					if (params.containsKey("idData")) {
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getNonEligibleGlobalRulesForTemplate(params.get("idData"));
						JSONObject oJsonResponse = new JSONObject();
						oJsonResponse.put("synonymNames",
								validationcheckdao.getDisplayNamesFromListDataDefinition(params.get("idData")));
						oJsonResponse.put("Rules", eligibleGlobalRulesJson.get("DataSet"));
						response.put("result", oJsonResponse.toMap());
						response.put("status", "success");
						response.put("message", "Custom rules fetched successfully.");
						LOG.info("Custom rules fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("getNonEligibleGlobalRulesForTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getNonEligibleGlobalRulesCSV", method = RequestMethod.POST)
	public void getEligibleOtherGlobalRulesCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getNonEligibleGlobalRulesCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getNonEligibleGlobalRulesForTemplate(params.get("idData"));
						httpResponse.setContentType("text/csv");
						String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						JSONArray headerValues = new JSONArray();
						JSONArray columns = new JSONArray();
						headerValues.put("Domain").put("Rule Name").put("Rule Type").put("Columns")
								.put("Filter Condition").put("Right Template Filter Condition").put("Matching Rules")
								.put("Expression").put("Unmatched Synonyms");
						columns.put("DomainName").put("Name").put("RuleType").put("AnchorColumns")
								.put("FilterCondition").put("RightTemplateFilterCondition").put("MatchingRules")
								.put("EffectiveExpression").put("UnmatchedSynonyms");
						httpResponse.setHeader(headerKey, headerValue);
						String csvString = csvGenerator.getCSVString(eligibleGlobalRulesJson.getJSONArray("DataSet"),
								headerValues, columns);
						httpResponse.getWriter().append(csvString);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getNonEligibleGlobalRulesCSV - END");
	}

	@RequestMapping(value = "/dbconsole/getEligibleReferenceRulesForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getEligibleReferenceRulesForTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getEligibleReferenceRulesForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getEligibleReferenceRulesForTemplate(params.get("idData"));
						JSONObject oJsonResponse = new JSONObject();
						oJsonResponse.put("Rules", eligibleGlobalRulesJson.get("DataSet"));
						response.put("result", oJsonResponse.toMap());
						response.put("status", "success");
						response.put("message", "Custom rules fetched successfully.");
						LOG.info("Custom rules fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getEligibleReferenceRulesForTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getEligibleReferenceRulesCSV", method = RequestMethod.POST)
	public void getEligibleReferenceRulesCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getEligibleReferenceRulesCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleGlobalRulesJson = globalRuleService
								.getEligibleReferenceRulesForTemplate(params.get("idData"));
						httpResponse.setContentType("text/csv");
						String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						JSONArray headerValues = new JSONArray();
						JSONArray columns = new JSONArray();
						headerValues.put("Template Column").put("Connection").put("Table Name").put("Template Name")
								.put("Reference Column").put("Confidence");
						columns.put("NewColumnName").put("MasterConnectionName").put("MasterTableName")
								.put("MasterTemplateName").put("MasterColumnName").put("ConfidenceLevel");
						httpResponse.setHeader(headerKey, headerValue);
						String csvString = csvGenerator.getCSVString(eligibleGlobalRulesJson.getJSONArray("DataSet"),
								headerValues, columns);
						httpResponse.getWriter().append(csvString);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getEligibleReferenceRulesCSV - END");
	}

	@RequestMapping(value = "/dbconsole/getEligibleGlobalThresholdsForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getEligibleGlobalThresholdsForTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getEligibleGlobalThresholdsForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleThresholdsJson = globalRuleService
								.getEligibleGlobalThresholdsForTemplate(params.get("idData"));
						JSONObject oJsonResponse = new JSONObject();
						oJsonResponse.put("Rules", eligibleThresholdsJson.get("DataSet"));
						response.put("result", oJsonResponse.toMap());
						response.put("status", "success");
						response.put("message", "Custom rules fetched successfully.");
						LOG.info("Custom rules fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getEligibleGlobalThresholdsForTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getEligibleGlobalThresholdsCSV", method = RequestMethod.POST)
	public void getEligibleGlobalThresholdsCVS(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getEligibleGlobalThresholdsCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject eligibleThresholdsJson = globalRuleService
								.getEligibleGlobalThresholdsForTemplate(params.get("idData"));
						httpResponse.setContentType("text/csv");
						String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						JSONArray headerValues = new JSONArray();
						JSONArray columns = new JSONArray();
						headerValues.put("Column Name").put("Null").put("Distribution Check").put("Length")
								.put("Data Drift").put("Value Anomaly");
						columns.put("globalColumnName").put("nullCountThreshold").put("numericalThreshold")
								.put("lengthCheckThreshold").put("dataDriftThreshold").put("recordAnomalyThreshold");
						String csvString = csvGenerator.getCSVString(eligibleThresholdsJson.getJSONArray("DataSet"),
								headerValues, columns);
						httpResponse.getWriter().append(csvString);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getEligibleGlobalThresholdsCSV - END");
	}

	@RequestMapping(value = "/dbconsole/synonymsCSV", method = RequestMethod.POST, produces = "application/json")
	public void loadSynonymsCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Integer> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/synonymsCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("loadContext")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject oJsonResponse = new JSONObject();
						oJsonResponse = globalRuleService.getSynonymsList(params.get("loadContext"));
						httpResponse.setContentType("text/csv");
						String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						JSONArray headerValues = new JSONArray();
						JSONArray columns = new JSONArray();
						headerValues.put("Domain Name").put("Synonym Name").put("User Fields");
						columns.put("domainName").put("tableColumn").put("possiblenames");
						String csvString = csvGenerator.getCSVString(oJsonResponse.getJSONArray("PageOptionLists"),
								headerValues, columns);
						httpResponse.getWriter().append(csvString);
					} else {
						LOG.error("Load context missing in request parameters");
						throw new Exception("Load context missing in request parameters");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/synonymsCSV - END");
	}

	@RequestMapping(value = "/dbconsole/loadSynonymsViewList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadSynonymsViewList(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Integer> params) {
		LOG.info("dbconsole/loadSynonymsViewList - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("loadContext")) {
						LOG.debug("Getting request parameters  " + params);
						JSONObject oJsonResponse = new JSONObject();
						DateUtility.DebugLog("loadSynonymsViewList 01",
								String.format("Begin controller processing with get data context as '%1$s'",
										params.get("loadContext")));
						oJsonResponse = globalRuleService.getSynonymsList(params.get("loadContext"));
						DateUtility.DebugLog("loadSynonymsViewList 02", "Got data sending to client");
						response.put("result", oJsonResponse.toMap());
						response.put("status", "success");
						response.put("message", "Synonym names fetched successfully.");
						LOG.info("Synonym names fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Load Context is missing in request parameters");
						throw new Exception("Load Context is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/loadSynonymsViewList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/linkSelectedGlobalRulesToTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> linkSelectedGlobalRulesToTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/linkSelectedGlobalRulesToTemplate - START");
		JSONObject params = new JSONObject(inputsJsonStr);
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("selectedGlobalRules")) {
				LOG.error("idData or selectedGlobalRules is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			long idData = params.getLong("idData");
			//long idApp = params.getLong("idApp");
			LOG.debug("\n====>idData: " + idData);
			LOG.debug("\n====>selectedGlobalRules: " + params.get("selectedGlobalRules"));
			JSONObject selectedRulesJson = params.getJSONObject("selectedGlobalRules");

			JSONArray selectedRules = null;
			if (selectedRulesJson != null && selectedRulesJson.has("Rules"))
				selectedRules = selectedRulesJson.getJSONArray("Rules");

			JSONArray delinkedRules = null;
			if (selectedRulesJson != null && selectedRulesJson.has("DelinkedRules"))
				delinkedRules = selectedRulesJson.getJSONArray("DelinkedRules");

			resJson = globalRuleService.linkSelectedGlobalRulesToTemplate(selectedRules, delinkedRules, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalRules to Template");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/linkSelectedGlobalRulesToTemplate - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dbconsole/linkCustomRules", method = RequestMethod.POST)
	public ResponseEntity<Object> linkCustomRules(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/linkSelectedGlobalRulesToTemplate - START");
		JSONObject params = new JSONObject(inputsJsonStr);
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("selectedGlobalRules")) {
				LOG.error("idData or selectedGlobalRules is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			long idData = params.getLong("idData");
			long idApp = params.getLong("idApp");
			LOG.debug("\n====>idData: " + idData);
			LOG.debug("\n====>selectedGlobalRules: " + params.get("selectedGlobalRules"));
			JSONObject selectedRulesJson = params.getJSONObject("selectedGlobalRules");

			List<ListDataDefinition> listdatadefinition = new ArrayList<>();
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
			boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);

			ListApplications listApplicationsData = null;
			if (isRuleCatalogEnabled && validationStatingEnabled) {
				listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
			} else {
				listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
			}

			if (listApplicationsData.getApplyRules().equalsIgnoreCase("N")) {
				boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
						listApplicationsData);
				LOG.debug("applyRulesFlag=" + applyRulesFlag);
				if (!applyRulesFlag) {
					// flag = false;
					resJson.put("message", "The rules are configured incorrectly");
					return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
				}
				listApplicationsData.setApplyRules("Y");
				if (isRuleCatalogEnabled && validationStatingEnabled) {
					ruleCatalogDao.updateIntoStagingListapplication(listApplicationsData);
				} else {
					validationcheckdao.updateintolistapplicationForAjaxRequest(listApplicationsData);
				}
				LOG.info("Apply rule flag is updated successfully");
			}

			JSONArray selectedRules = null;
			if (selectedRulesJson != null && selectedRulesJson.has("Rules"))
				selectedRules = selectedRulesJson.getJSONArray("Rules");

			JSONArray delinkedRules = null;
			if (selectedRulesJson != null && selectedRulesJson.has("DelinkedRules"))
				delinkedRules = selectedRulesJson.getJSONArray("DelinkedRules");

			resJson = globalRuleService.linkSelectedGlobalRulesToTemplate(selectedRules, delinkedRules, idData);

		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalRules to Template");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/linkSelectedGlobalRulesToTemplate - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/linkSelectedOtherGlobalRulesToTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> linkSelectedOtherGlobalRulesToTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		JSONObject params = new JSONObject(inputsJsonStr);
		LOG.info("dbconsole/linkSelectedOtherGlobalRulesToTemplate - START");
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("selectedGlobalRules")) {
				LOG.error("idData or selectedGlobalRules is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			LOG.debug("Getting request parameters  " + inputsJsonStr);
			resJson = globalRuleService.linkSelectedOtherGlobalRulesToTemplate(
					params.getJSONArray("selectedGlobalRules"), params.getLong("idData"));
		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalRules to Template");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/linkSelectedOtherGlobalRulesToTemplate - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/linkSelectedReferenceRulesToTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> linkSelectedReferenceRulesToTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/linkSelectedReferenceRulesToTemplate - START");
		JSONObject params = new JSONObject(inputsJsonStr);
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("selectedReferenceRules")) {
				LOG.error("idData or selectedReferenceRules is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			LOG.debug("Getting request parameters  " + params);
			resJson = globalRuleService.linkSelectedReferenceRulesToTemplate(
					params.getJSONArray("selectedReferenceRules"), params.getLong("idData"));
		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking ReferenceRules to Template");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/linkSelectedReferenceRulesToTemplate - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/linkSelectedGlobalThresholdsToTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> linkSelectedGlobalThresholdsToTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		JSONObject params = new JSONObject(inputsJsonStr);
		LOG.info("dbconsole/linkSelectedGlobalThresholdsToTemplate - START");
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("selectedThresholds")) {
				LOG.error("idData or selectedThresholds is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			LOG.debug("Getting request parameters  " + inputsJsonStr);
			resJson = globalRuleService.linkGlobalThresholdToDataTemplate(params.getJSONArray("selectedThresholds"),
					params.getLong("idData"));
		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while linking GlobalThresholds to Template");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/linkSelectedGlobalThresholdsToTemplate - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/saveSynonymMappings", method = RequestMethod.POST)
	public ResponseEntity<Object> saveSynonymMappings(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		JSONObject params = new JSONObject(inputsJsonStr);
		LOG.info("dbconsole/saveSynonymMappings - START");
		JSONObject resJson = new JSONObject();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.has("idData") || !params.has("synonymMappings")) {
				LOG.error("idData or synonym mapping is missing.");
				throw new Exception("Please provide require parameters in request.");
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			long idData = params.getLong("idData");
			LOG.debug("\n====>idData: " + idData);
			LOG.debug("\n====>synonymMappings: " + params.get("synonymMappings"));
			resJson = globalRuleService.saveSynonymMappings(idData, params.getJSONObject("synonymMappings"));
		} catch (Exception e) {
			resJson.put("status", "failed");
			resJson.put("message", "Error occurred while saving synonym mappings");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		LOG.info("dbconsole/saveSynonymMappings - END");
		return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/saveSynonyms", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveSynonyms(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/saveSynonyms - START");
		JSONObject oSynonymsRecordToSave = new JSONObject(inputsJsonStr);
		JSONObject oJsonResponse = new JSONObject();
		JSONObject resJson = new JSONObject();
		DateUtility.DebugLog("SaveSynonymsFromViewList 01", "Begin save process");
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				resJson.put("message", "Please provide token.");
				resJson.put("status", "failed");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!validateToken(headers.get("token").get(0))) {
				resJson.put("message", "Token expired.");
				resJson.put("status", "failed");
				LOG.error("Token is expired.");

				return new ResponseEntity<Object>(resJson.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			LOG.debug("token " + headers.get("token").get(0).toString());
			LOG.debug("Getting request parameters  " + inputsJsonStr);
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String synonymId = oSynonymsRecordToSave.getString("SynonymsId");
			String SynonymsName = oSynonymsRecordToSave.getString("SynonymsName");

			/* Get all data entry form entered by user (add or edit) */
			DateUtility.DebugLog("SaveSynonymsFromViewList 02",
					String.format("Read input form values from UI as \n%1$s\n", oSynonymsRecordToSave.toString()));
			oJsonResponse = globalRuleService.saveSynonymsFormFromViewList(oSynonymsRecordToSave);
			DateUtility.DebugLog("SaveSynonymsFromViewList 03",
					String.format("Pushed response to UI as \n%1$s\n", oJsonResponse.toString()));
			boolean isNewSynonyms = (oSynonymsRecordToSave.getString("SynonymsId").equalsIgnoreCase("-1")) ? true
					: false;
			resJson.put("status", "success");
			if (isNewSynonyms) {
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_SYNONYM, formatter.format(new Date()),
						(long) oJsonResponse.getInt("synonymId"), DatabuckConstants.ACTIVITY_TYPE_CREATED,
						SynonymsName);
				resJson.put("message", oJsonResponse.getString("Msg"));
			} else {
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_SYNONYM, formatter.format(new Date()), Long.parseLong(synonymId),
						DatabuckConstants.ACTIVITY_TYPE_EDITED, SynonymsName);
				resJson.put("message", "Synonyms updated successfully.");
			}
			if (!oJsonResponse.getBoolean("Result")) {
				resJson.put("status", "failed");
				resJson.put("message", oJsonResponse.getString("Msg"));
			}
			resJson.put("result", resJson.toMap());

			return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
		} catch (Exception oException) {
			oException.printStackTrace();
			resJson.put("status", "failed");
			LOG.error(oException.getMessage());
			if (!oJsonResponse.getBoolean("Result")) {
				resJson.put("message", oJsonResponse.getString("Msg"));
			} else {
				resJson.put("message", "Error occurred while saving synonym mappings");
			}
			LOG.info("dbconsole/saveSynonyms - END");
			return new ResponseEntity<Object>(resJson.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getGlobalRuleCreateList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getRuleCategory(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getGlobalRuleCreateList - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<String> ruleCategory = new ArrayList<>();
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.REFERENTIAL_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.ORPHAN_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CROSSREFERENTIAL_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_ORPHAN_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE));
					ruleCategory.add((ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE))
							.replace("Sql", "SQL"));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.DIRECT_QUERY_RULE));
					ruleCategory.add(ToCamelCase.convertString(DatabuckConstants.SQL_INTERNAL_RULE));
					List<Domain> domainList = globalRuleDAO.getDomainList();
					List<Dimension> dimensionList = templateviewdao.getlistdimensionname();

					JSONObject oJsonResponse = new JSONObject();
					oJsonResponse.put("ruleCategory", ruleCategory);
					oJsonResponse.put("domainList", domainList);
					oJsonResponse.put("dimensionList", dimensionList);
					response.put("result", oJsonResponse.toMap());
					response.put("status", "success");
					response.put("message", "Global Filters List fetched successfully.");
					LOG.info("Global Filters List fetched successfully.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getGlobalRuleCreateList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getDomainList", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getDomainList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getDomainList - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<Domain> domainList;
					try {
						domainList = globalRuleDAO.getDomainList();
						response.put("result", domainList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					response.put("status", "success");
					response.put("message", "Domain List fetched successfully.");
					LOG.info("Domain List fetched successfully.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getDomainList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getListDataSourcesName", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getDimensionList(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getListDataSourcesName - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("projectId")) {
						LOG.debug("Getting request parameters  " + params);
						List<Map<String, Object>> getlistdatasourcesname = templateviewdao
								.getListSecondDatasources(params.get("projectId"));
						response.put("result", getlistdatasourcesname);
						response.put("status", "success");
						response.put("message", "Datasources List fetched successfully.");

						LOG.info("Datasources List fetched successfully.");
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						throw new Exception("Project Id is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getListDataSourcesName - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getListSourceDataColumn", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getListSourceDataColumn(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getListSourceDataColumn - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idData")) {
						LOG.debug("Getting request parameters  " + params);
						List<String> listDataDefinitionColumnNames = validationcheckdao
								.getDisplayNamesFromListDataDefinition(params.get("idData"));
						response.put("result", listDataDefinitionColumnNames);
						response.put("status", "success");
						response.put("message", "Data Source Column List fetched successfully.");
						LOG.info("Data Source Column List fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Id data is missing in request parameters");
						throw new Exception("Id data is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getListSourceDataColumn - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getPopulateFilters", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getPopulateFilters(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Integer> params) {
		LOG.info("dbconsole/getPopulateFilters - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("domainId")) {
						LOG.debug("Getting request parameters  " + params);
						List<GlobalFilters> globalFiltersList = globalRuleDAO
								.getAllGlobalFiltersByDomain(params.get("domainId"));
						response.put("result", globalFiltersList);
						response.put("status", "success");
						response.put("message", "Global Filters List fetched successfully.");
						LOG.info("Global Filters List fetched successfully.");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Domain Id is missing in request parameters");
						throw new Exception("Domain Id is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getPopulateFilters - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getGlobalRulesList", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getGlobalRulesList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getGlobalRulesList - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					try {
						List<GlobalRuleView> listColRulesData = globalRuleDAO.getListColRulesForViewRules();
						response.put("result", listColRulesData);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response.put("status", "fail");
						response.put("message", "Exception while fetching Global Rules");
						LOG.error(e.getMessage());

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}

					response.put("status", "success");
					response.put("message", "Global Rules List fetched successfully.");
					LOG.info("Global Rules List fetched successfully.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getGlobalRulesList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getGlobalRulesCSV", method = RequestMethod.GET, produces = "application/json")
	public void getGlobalRulesListCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/getGlobalRulesCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					httpResponse.setContentType("text/csv");
					String csvFileName = "RunningJob" + LocalDateTime.now() + ".csv";
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
					httpResponse.setHeader(headerKey, headerValue);
					ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
							CsvPreference.STANDARD_PREFERENCE);
					List<GlobalRuleView> listColRulesData = globalRuleDAO.getListColRulesForViewRules();
					String[] csvHeaders = new String[] { "Rule Id", "Rule Name", "Domain", "Rule Type",
							"Filter Condition", "Filter Condition for Reference Template", "Matching Rules", "Expression",
							"Dimension", "Created On", "Created By" };
					String[] fields = new String[] { "idListColrules", "ruleName", "domain", "ruleType",
							"filterCondition", "RightTemplateFilterCondition", "matchingRules", "expression",
							"dimensionName", "createdAt", "createdByUser" };
					csvWriter.writeHeader(csvHeaders);
					for (GlobalRuleView globalRule : listColRulesData) {
						csvWriter.write(globalRule, fields);
					}
					csvWriter.close();
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Please provide token.");
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
		LOG.info("dbconsole/getGlobalRulesCSV - END");
	}

	@RequestMapping(value = "/dbconsole/deleteGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> deleteGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/deleteGlobalRule - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.containsKey("idListColrules")) {
						Long globalRuleId = params.get("idListColrules");
						LOG.debug("idListColrules=" + globalRuleId);
						ListColGlobalRules globalRule = globalRuleDAO.getGlobalRuleById(globalRuleId);

						globalRuleDAO.deleteGlobalRulesData(globalRuleId);
						// changes regarding Audit trail
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_GLOBAL_RULE, formatter.format(new Date()),
								params.get("idListColrules"), DatabuckConstants.ACTIVITY_TYPE_DELETED,
								globalRule.getRuleName());

						response.put("status", "success");
						response.put("message", "Custom Rule deleted successfully");
						LOG.info("Custom Rule deleted successfully");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Custom Rule Id is missing in request parameters");
						throw new Exception("Custom Rule Id is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteGlobalRule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/copyGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> copyGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody copyGlobalRulesRequest params) {
		LOG.info("dbconsole/copyGlobalRule - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (params.newRuleName != null && params.idListColrules != null) {
						LOG.debug("globalNewRuleName=" + params.newRuleName);
						LOG.debug("globalRuleId=" + params.idListColrules);
						long resultIdListColrules = globalRuleDAO.copyGlobalRulesData(params.newRuleName,
								params.idListColrules);
						response.put("status", "success");
						response.put("message", "Global Rule is copied successfully.");
						LOG.info("Global Rule is copied successfully.");
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_GLOBAL_RULE, formatter.format(new Date()),
								resultIdListColrules, DatabuckConstants.ACTIVITY_TYPE_CREATED, params.newRuleName);

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						LOG.error("Global Rule Id or New rule name is missing in request parameters");
						throw new Exception("Global Rule Id or New rule name is missing in request parameters");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/copyGlobalRule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/createGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody GlobalRuleBean globalRule) {
		LOG.info("dbconsole/createGlobalRule - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String activeDirectoryFlag = appDbConnectionProperties
							.getProperty("isActiveDirectoryAuthentication");
					LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
					String createdByUser = "";

					if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
						createdByUser = globalRule.getCreatedBy();
						LOG.debug("======= createdByUser in GlobalRuleController ===>" + createdByUser);
					} else {
						// getting createdBy username from createdBy userId
						LOG.debug("======= idUser ===>" + globalRule.getIdUser());

						createdByUser = userDAO.getUserNameByUserId(globalRule.getIdUser());

						LOG.debug("======= createdByUser in GlobalRuleController ===>" + createdByUser);
					}

					String matchingRules = globalRule.getMatchingRules();
					String expression = globalRule.getRuleExpression();

					if (globalRule.getMatchingRules() == null)
						matchingRules = "";

					if (globalRule.getRuleExpression() == null)
						expression = "";

					LOG.debug("Global Rule Add =" + globalRule.toString());

					int domainId = 0;
					ListColGlobalRules lcr = new ListColGlobalRules();
					lcr.setRuleName(globalRule.getRuleName());
					LOG.debug("rightTemplateFilterId=" + globalRule.getRightTemplateFilterId());
					lcr.setRightTemplateFilterId(globalRule.getRightTemplateFilterId());

					if (globalRule.getDescription() != null) {
						lcr.setDescription(globalRule.getDescription());
					}

					lcr.setExpression(expression);

					if (globalRule.getRightIdData() != null) {
						lcr.setIdRightData(globalRule.getRightIdData());
					}

					lcr.setMatchingRules(matchingRules);
					lcr.setCreatedByUser(createdByUser);

					if (globalRule.getRuleThreshold() != null) {
						lcr.setRuleThreshold(globalRule.getRuleThreshold());
					}

					if (globalRule.getFilterId() != null) {
						lcr.setFilterId(globalRule.getFilterId());
					}

					if (globalRule.getDomainId() != null) {
						lcr.setDomain_id(globalRule.getDomainId());
						domainId = globalRule.getDomainId();
					}

					lcr.setProjectId(globalRule.getProjectId());
					lcr.setDimensionId(globalRule.getDimensionId());
					lcr.setAggregateResultsEnabled(globalRule.getAggregateResultsEnabled());
					lcr.setRightTemplateFilterId(0);

					String ruleCategory = globalRule.getRuleCategory();

					if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.REFERENTIAL_RULE);
						lcr.setMatchingRules("");
						lcr.setFilterId(0);

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
						lcr.setRuleType(DatabuckConstants.ORPHAN_RULE);
						lcr.setExpression("");
						lcr.setExternalDatasetName(globalRule.getExternalDatasetName());
						lcr.setFilterId(0);

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.CROSSREFERENTIAL_RULE);
						lcr.setExternalDatasetName(globalRule.getExternalDatasetName());
						lcr.setFilterId(0);

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE);
						lcr.setExternalDatasetName(globalRule.getExternalDatasetName());
						lcr.setExpression(globalRule.getRuleExpression());
						lcr.setRightTemplateFilterId(globalRule.getRightTemplateFilterId());

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_ORPHAN_RULE);
						lcr.setExternalDatasetName(globalRule.getExternalDatasetName());
						lcr.setExpression("");
						lcr.setRightTemplateFilterId(globalRule.getRightTemplateFilterId());

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE);
						lcr.setMatchingRules("");

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE);
						lcr.setMatchingRules("");

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)) {
						lcr.setRuleType(DatabuckConstants.SQL_INTERNAL_RULE);
						lcr.setFilterId(0);
						lcr.setMatchingRules("");

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK);
						lcr.setMatchingRules("");

					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
						lcr.setRuleType(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK);
						lcr.setMatchingRules("");
					} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)) {
						lcr.setRuleType(DatabuckConstants.DIRECT_QUERY_RULE);
						lcr.setMatchingRules("");
						lcr.setFilterId(0);
						lcr.setFilterCondition("");
						lcr.setRightTemplateFilterId(0);
						lcr.setRightTemplateFilterCondition("");
					}

					// String domain_Name = GlobalRuleDAO.getdomainName(domain);
					// System.out.println("for check duplicate domain_name :: " + domain_Name);
					/*
					 * if (globalRule.getDomain() != null) { try { domainid =
					 * globalRuleDAO.getDomainId(globalRule.getDomain());
					 * lcr.setDomain_id(domainid); System.out.println("get domain_Id  from db ::   "
					 * + domainid); } catch (Exception e) {
					 * System.out.println("probem occured while getting domain_Id  from db "); } }
					 */

					boolean duplicateName = globalRuleDAO.checkIfDuplicateGlobalRuleName(lcr);

					if (!duplicateName) {

						String validityStatus = "fail";
						String message = "";
						JSONObject match_expr_status = null;
						JSONObject rule_expr_status = null;
						if (matchingRules.trim() != null && !matchingRules.trim().isEmpty()) {
							if (!globalRuleService.validateSynonymExpression(matchingRules)) {
								response.put("status", "failed");
								response.put("message", "Invalid synonym names.");
								LOG.error("Invalid Synonym Name :" + matchingRules);
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}
						}

						// Validate Matching condition
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
							match_expr_status = globalRuleService.validateSynonymExpression(matchingRules, domainId);
						}

						if (expression.trim() != null && !expression.trim().isEmpty()) {
							if (!ruleCategory.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)
									&& !globalRuleService.validateSynonymExpression(expression)) {
								response.put("status", "failed");
								response.put("message", "Invalid synonym names.");
								LOG.error("Invalid Synonym Name :" + expression);
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}
						}
						// Validate Rule Expression
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
							rule_expr_status = globalRuleService.validateSynonymExpression(expression, domainId);
						}

						if (match_expr_status != null
								&& match_expr_status.getString("status").equalsIgnoreCase("failed")) {
							message = match_expr_status.getString("message");

						} else if (rule_expr_status != null
								&& rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
							message = rule_expr_status.getString("message");

						} else
							validityStatus = "success";
						LOG.debug("validity" + message + ":" + validityStatus);

						if (validityStatus.equalsIgnoreCase("success")) {

							int checkIfDuplicateSqlRowSet = globalRuleDAO.checkIfDuplicateGlobalRule(lcr);
							LOG.debug("checkIfDuplicateSqlRowSet::::" + checkIfDuplicateSqlRowSet);
							try {
								if (checkIfDuplicateSqlRowSet >= 1) {
									try {
										json.put("fail", "The Rule Expression already exists");
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Long globalRuleId = globalRuleDAO.insertintolistColRules(lcr);

									// changes regarding Audit trail
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									iTaskDAO.addAuditTrailDetail(globalRule.getIdUser(), globalRule.getCreatedBy(),
											DatabuckConstants.DBK_FEATURE_GLOBAL_RULE, formatter.format(new Date()),
											globalRuleId, DatabuckConstants.ACTIVITY_TYPE_CREATED,
											globalRule.getRuleName());

									try {
										json.put("ruleId", globalRuleId);
										json.put("success", "Custom Rule created successfully");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								globalRuleDAO.insertintolistColRules(lcr);
								try {
									json.put("success", "Custom Rule created successfully");
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}

						} else {
							try {

								json.put(validityStatus, message);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						LOG.error("\nThe Rule Name already exists!!");
						try {
							json.put("failed", "The Rule Name already exists ");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					response.put("result", json.toMap());
					if (json.has("failed")) {
						response.put("status", "failed");
						response.put("message", json.get("failed"));
					} else {
						response.put("status", "success");
						response.put("message", json.get("success"));
					}
					// response.put("message", "Custom rule applied to DB successfully.");
					//LOG.info("Custom rule applied to DB successfully.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/createGlobalRule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> updateGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody GlobalRuleBean globalRule) {
		LOG.info("dbconsole/updateGlobalRule - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String matchingRules = globalRule.getMatchingRules();
					String expression = globalRule.getRuleExpression();

					// changes regarding Audit trail
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(globalRule.getIdUser(), globalRule.getCreatedBy(),
							DatabuckConstants.DBK_FEATURE_GLOBAL_RULE, formatter.format(new Date()),
							globalRule.getIdListColRules(), DatabuckConstants.ACTIVITY_TYPE_EDITED,
							globalRule.getRuleName());

					if (globalRule.getMatchingRules() == null)
						matchingRules = "";

					if (globalRule.getRuleExpression() == null)
						expression = "";

					LOG.debug("Global Rule Add =" + globalRule.toString());

					int domainId = 0;
					ListColGlobalRules lcr = new ListColGlobalRules();
					lcr.setIdListColrules(globalRule.getIdListColRules());
					lcr.setRuleName(globalRule.getRuleName());
					lcr.setRightTemplateFilterId(globalRule.getRightTemplateFilterId());
					LOG.debug("rightTemplateFilterId=" + globalRule.getRightTemplateFilterId());

					if (globalRule.getDescription() != null) {
						lcr.setDescription(globalRule.getDescription());
					}

					lcr.setExpression(expression);

					if (globalRule.getRightIdData() != null) {
						lcr.setIdRightData(globalRule.getRightIdData());
					}

					lcr.setMatchingRules(matchingRules);

					if (globalRule.getRuleThreshold() != null) {
						lcr.setRuleThreshold(globalRule.getRuleThreshold());
					}

					if (globalRule.getFilterId() != null) {
						lcr.setFilterId(globalRule.getFilterId());
					}

					if (globalRule.getDomainId() != null) {
						lcr.setDomain_id(globalRule.getDomainId());
						domainId = globalRule.getDomainId();
					}
					lcr.setDimensionId(globalRule.getDimensionId());
					lcr.setAggregateResultsEnabled(globalRule.getAggregateResultsEnabled());
					lcr.setRuleType(globalRule.getRuleCategory());

					String ruleCategory = globalRule.getRuleCategory();
					Integer rightTemplateFilterId = lcr.getRightTemplateFilterId();

					if ((ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE))
							|| (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
									&& rightTemplateFilterId != null && rightTemplateFilterId > 0)
						lcr.setRightTemplateFilterId(rightTemplateFilterId);
					else
						lcr.setRightTemplateFilterId(0);

					// String domain_Name = GlobalRuleDAO.getdomainName(domain);
					// System.out.println("for check duplicate domain_name :: " + domain_Name);
					/*
					 * if (globalRule.getDomain() != null) { try { domainid =
					 * globalRuleDAO.getDomainId(globalRule.getDomain());
					 * lcr.setDomain_id(domainid); System.out.println("get domain_Id  from db ::   "
					 * + domainid); } catch (Exception e) {
					 * System.out.println("probem occured while getting domain_Id  from db "); } }
					 */

					String finalStatus = "failed";
					String message = "Failed to update Custom rule";
					JSONArray mappingErrors = null;

					JSONObject match_expr_status = null;
					JSONObject rule_expr_status = null;

					if (matchingRules.trim() != null && !matchingRules.trim().isEmpty()) {
						if (!globalRuleService.validateSynonymExpression(matchingRules)) {
							response.put("status", "failed");
							response.put("message", "Invalid synonym names.");
							LOG.error("Invalid Synonym Name :" + matchingRules);
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					}
					// Validate Matching condition
					if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
						match_expr_status = globalRuleService.validateSynonymExpression(matchingRules, domainId);
					}

					if (expression.trim() != null && !expression.trim().isEmpty()) {
						if (!ruleCategory.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)
								&& !globalRuleService.validateSynonymExpression(expression)) {
							response.put("status", "failed");
							response.put("message", "Invalid synonym names.");
							LOG.error("Invalid Synonym Name :" + expression);
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					}

					// Validate Rule Expression
					if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
							|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
						rule_expr_status = globalRuleService.validateSynonymExpression(expression, domainId);
					}

					if (match_expr_status != null && match_expr_status.getString("status").equalsIgnoreCase("failed")) {
						message = match_expr_status.getString("message");

					} else if (rule_expr_status != null
							&& rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
						message = rule_expr_status.getString("message");

					} else {
						try {
							int checkIfDuplicateSqlRowSet = globalRuleDAO.checkIfDuplicateGlobalRule(lcr);
							LOG.debug("checkIfDuplicateSqlRowSet=" + checkIfDuplicateSqlRowSet);
							if (checkIfDuplicateSqlRowSet >= 1)
								message = "Rule already exists";
							else {
								// Save the rule
								globalRuleDAO.updateintolistColRules(lcr);
								finalStatus = "success";
								message = "Custom Rule updated successfully";

								// Updating the rule Mapping in all associated templates
								LOG.info("\n====> Updating rule Mapping in all associated templates");
								JSONObject templateMappingStatusObj = globalRuleService
										.updateGlobalRuleMappingInAssociatedTemplates(lcr);

								finalStatus = templateMappingStatusObj.getString("status");

								if (finalStatus.equalsIgnoreCase("failed")) {
									message = templateMappingStatusObj.getString("message");
									mappingErrors = templateMappingStatusObj.getJSONArray("mappingErrors");
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					response.put("result", json.toMap());
					response.put("status", finalStatus);
					response.put("message", message);
					response.put("mappingErrors", mappingErrors);
					LOG.info("Custom rule updates applied to DB successfully.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateGlobalRule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	public Boolean validateToken(String token) {
		return executiveSummaryService.validateToken(token).getString("status") == "success" ? true : false;
	}

	public static class copyGlobalRulesRequest {
		public String newRuleName;
		public Long idListColrules;
	}

	public static class editGlobalRulesRequest {
		public Long idListColrules;
		public Long projectId;
		public Integer domainId;
	}

//	@RequestMapping(value = "/loadSynonymsViewList", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> loadSynonymsViewList(@RequestHeader HttpHeaders headers ,@RequestParam int LoadContext){
//		Map<String, Object> response = new HashMap<String, Object>();
//		try {
//			String token = headers.get("token").get(0);
//			if (token != null && !token.isEmpty()) {
//				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
//					DateUtility.DebugLog("loadSynonymsViewList 01",
//							String.format("Begin controller processing with get data context as '%1$s'", LoadContext));
//
//					JSONObject oJsonResponse = globalRuleService.getSynonymsPageData(LoadContext);
//					response.put("result", oJsonResponse.toMap());
//					response.put("status", "Success");
//					response.put("message", "Success");
//					return new ResponseEntity<Object>(response, HttpStatus.OK);
//				} else {
//					response.put("status", "failed");
//					response.put("message", "Token is expired.");
//					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//				}
//			} else {
//				response.put("status", "failed");
//				response.put("message", "Token is missing in headers.");
//				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("status", "failed");
//			response.put("message", e.getMessage());
//			return new ResponseEntity<Object>(response, HttpStatus.OK);
//		}		
//	}

	@RequestMapping(value = "/dbconsole/createGlobalFilter", method = RequestMethod.POST)
	public ResponseEntity<Object> createGlobalFilter(@RequestHeader HttpHeaders headers,
			@RequestBody GlobalFilters globalFilter) {
		LOG.info("dbconsole/createGlobalFilter - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("domain:" + globalFilter.getDomain());
					int domainId = globalRuleDAO.getDomainId(globalFilter.getDomain());
					globalFilter.setDomainId(domainId);
					String message = "";
					String status = "failed";

					if (globalRuleService.validateSynonymExpression(globalFilter.getFilterCondition())) {
						// Check if the filter condition has valid synonym names
						JSONObject synonymStatusObj = globalRuleService
								.validateSynonymExpression(globalFilter.getFilterCondition(), domainId);
						String synonymStatus = synonymStatusObj.getString("status");

						if (!synonymStatus.equalsIgnoreCase("failed")) {

							if(!globalRuleDAO.isDuplicateGlobalFilter(globalFilter.getDomainId(),globalFilter.getFilterName().trim(), globalFilter.getFilterCondition())){

								long filterId = globalRuleDAO.insertIntoGlobalFilters(globalFilter);
								LOG.debug("filterId=" + filterId);

								if (filterId > 0) {
									// changes regarding Audit trail
									UserToken userToken = dashboardConsoleDao
											.getUserDetailsOfToken(headers.get("token").get(0));
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

									iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
											DatabuckConstants.DBK_FEATURE_GLOBAL_FILTER, formatter.format(new Date()),
											filterId, DatabuckConstants.ACTIVITY_TYPE_CREATED,
											globalFilter.getFilterName());
									message = "Global Filter created successfully";
									status = "success";
								} else {
									message = "Failed to create global filter.";
								}
							}else
								message ="Global Filter Already Exists";

						} else {
							message = synonymStatusObj.getString("message");
						}
					} else
						message = "Invalid global filter condition";

					response.put("status", status);
					response.put("message", message);
				}

				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);

			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/createGlobalFilter - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateGlobalFilter", method = RequestMethod.POST)
	public ResponseEntity<Object> updateGlobalFilter(@RequestHeader HttpHeaders headers,
			@RequestBody GlobalFilters globalFilter) {
		LOG.info("dbconsole/updateGlobalFilter - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String status = "failed";
					LOG.debug("domain:" + globalFilter.getDomain());
					int domain_id = globalRuleDAO.getDomainId(globalFilter.getDomain());
					globalFilter.setDomainId(domain_id);

					String message = "";
					JSONArray mappingErrors = null;
					if (globalRuleService.validateSynonymExpression(globalFilter.getFilterCondition())) {
						JSONObject synonymStatusObj = globalRuleService
								.validateSynonymExpression(globalFilter.getFilterCondition(), domain_id);
						String synonymStatus = synonymStatusObj.getString("status");
						if (!synonymStatus.equalsIgnoreCase("failed")) {
							message = "Failed to update Global Filter";
							int updateStatus = globalRuleDAO.updateIntoGlobalFilters(globalFilter);
							GlobalFilters globalFilterById = globalRuleDAO
									.getGlobalFilterById(globalFilter.getFilterId());
							if (updateStatus > 0) {
								// changes regarding Audit trail
								UserToken userToken = dashboardConsoleDao
										.getUserDetailsOfToken(headers.get("token").get(0));
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
										DatabuckConstants.DBK_FEATURE_GLOBAL_FILTER, formatter.format(new Date()),
										globalFilter.getFilterId(), DatabuckConstants.ACTIVITY_TYPE_EDITED,
										globalFilterById.getFilterName());
								message = "Global Filter updated successfully";
								LOG.debug("\n====>" + message);
								status = "success";
								LOG.info(
										"\n====> Updating the filter conditiona rule Mapping in all associated templates");
								JSONObject templateMappingStatusObj = globalRuleService
										.updateGlobalFilterInAssociatedTemplates(globalFilter);
								status = templateMappingStatusObj.getString("status");
								if (status.equalsIgnoreCase("failed")) {
									message = templateMappingStatusObj.getString("message");
									mappingErrors = templateMappingStatusObj.getJSONArray("mappingErrors");
								}
							}
						} else
							message = synonymStatusObj.getString("message");
					} else
						message = "Invalid global filter condition";

					response.put("status", status);
					response.put("message", message);
					response.put("mappingErrors", mappingErrors);

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateGlobalFilter - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/viewGlobalFilters", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> viewGlobalFilters(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/viewGlobalFilters - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					List<GlobalFilters> globalFiltersData = globalRuleDAO.getListGlobalFilters();

					response.put("result", globalFiltersData);
					response.put("status", "success");
					response.put("message", "View Global Filter");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/viewGlobalFilters - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getGlobalFiltersCSV", method = RequestMethod.GET, produces = "application/json")
	public void getGlobalFiltersCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		try {
			LOG.info("dbconsole/getGlobalFiltersCSV - START");
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					httpResponse.setContentType("text/csv");
					String csvFileName = "RunningJob" + LocalDateTime.now() + ".csv";
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
					httpResponse.setHeader(headerKey, headerValue);
					ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
							CsvPreference.STANDARD_PREFERENCE);
					List<GlobalFilters> globalFiltersData = globalRuleDAO.getListGlobalFilters();
					String[] csvHeaders = new String[] { "Filter Name", "Domain", "Filter Condition", "Description", };
					String[] fields = new String[] { "filterName", "domain", "filterCondition", "description", };
					csvWriter.writeHeader(csvHeaders);
					for (GlobalFilters globalFilter : globalFiltersData) {
						csvWriter.write(globalFilter, fields);
					}
					csvWriter.close();
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token is expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Please provide token.");
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
	}

	/*
	 * API to get synonyms mapping details for global rule.
	 */
	@RequestMapping(value = "/dbconsole/getSynonymMappingForGlobalRuleVerification", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getSynonymMappingForGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		HttpStatus httpStatus = HttpStatus.OK;
		LOG.info("dbconsole/getSynonymMappingForGlobalRuleVerification - START");
		try {
			DateUtility.DebugLog("getSynonymMappingForGlobalRuleVerification ",
					String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));

			JSONObject inputJson = new JSONObject(inputsJsonStr);

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String ruleCategory = inputJson.getString("ruleCategory");
					if (!ruleCategory.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)) {
						int domainId = 0;
						ListColGlobalRules lcr = new ListColGlobalRules();

						String matchingRules = inputJson.getString("matchingRules");
						String expression = inputJson.getString("ruleExpression");

						if (matchingRules == null)
							matchingRules = "";

						if (expression == null)
							expression = "";

						lcr.setRuleName(inputJson.getString("ruleName"));
						lcr.setExpression(expression);
						lcr.setMatchingRules(matchingRules);

						if (inputJson.getString("description") != null) {
							lcr.setDescription(inputJson.getString("description"));
						}
						if (inputJson.get("rightIdData") != null && !inputJson.get("rightIdData").equals("")) {
							lcr.setIdRightData(inputJson.getLong("rightIdData"));
						}
						if (inputJson.get("ruleThreshold") != null && !inputJson.get("ruleThreshold").equals("")) {
							lcr.setRuleThreshold(inputJson.getDouble("ruleThreshold"));
						}
						if (inputJson.get("filterId") != null && !inputJson.get("filterId").equals("")) {
							lcr.setFilterId(inputJson.getInt("filterId"));
							GlobalFilters globalFilter = globalRuleDAO
									.getGlobalFilterById(inputJson.getInt("filterId"));
							if (globalFilter != null)
								lcr.setFilterCondition(globalFilter.getFilterCondition());
						}
						if (inputJson.get("rightTemplateFilterId") != null
								&& !inputJson.get("rightTemplateFilterId").equals("")) {
							lcr.setRightTemplateFilterId(inputJson.getInt("rightTemplateFilterId"));
							GlobalFilters globalFilter = globalRuleDAO
									.getGlobalFilterById(inputJson.getInt("rightTemplateFilterId"));
							if (globalFilter != null)
								lcr.setRightTemplateFilterCondition(globalFilter.getFilterCondition());
						}
						if (inputJson.get("domainId") != null && !inputJson.get("domainId").equals("")) {
							lcr.setDomain_id(inputJson.getInt("domainId"));
							domainId = inputJson.getInt("domainId");
						}
						lcr.setProjectId(inputJson.getLong("projectId"));
						lcr.setDimensionId(inputJson.getLong("dimensionId"));

						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.REFERENTIAL_RULE);
							lcr.setMatchingRules("");
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
							lcr.setRuleType(DatabuckConstants.ORPHAN_RULE);
							lcr.setExpression("");
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CROSSREFERENTIAL_RULE);
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_ORPHAN_RULE);
							lcr.setExpression("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.SQL_INTERNAL_RULE);
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE);
						}

						String validityStatus = "fail";
						String validateMessage = "";
						JSONObject match_expr_status = null;
						JSONObject rule_expr_status = null;

						// Validate Matching condition
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
							match_expr_status = globalRuleService.validateSynonymExpression(matchingRules, domainId);
						}
						// Validate Rule Expression
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
							rule_expr_status = globalRuleService.validateSynonymExpression(expression, domainId);
						}

						if (match_expr_status != null
								&& match_expr_status.getString("status").equalsIgnoreCase("failed")) {
							validateMessage = match_expr_status.getString("message");
						} else if (rule_expr_status != null
								&& rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
							validateMessage = rule_expr_status.getString("message");
						} else
							validityStatus = "success";
						LOG.debug("validity" + validateMessage + ":" + validityStatus);

						if (validityStatus.equalsIgnoreCase("success")) {
							long idData = inputJson.getLong("idData");
							json = globalRuleService.getGlobalRuleDetails(idData, lcr);
						} else {
							json.put(validityStatus, validateMessage);
						}

						if (json != null) {
							response.put("result", json.toMap());
							status = "success";
							message = "Details fetched successfully.";
						} else {
							status = "failed";
							message = "Invalid synonym expression found.";
						}
					} else {
						status = "failed";
						message = "Validate global rule does not support Direct Query Rule.";
					}

				} else {
					LOG.error("Token is expired.");
					status = "failed";
					message = "Token is expired.";
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				status = "failed";
				message = "Token is missing in headers.";
				LOG.error("Token is missing in headers.");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failed";
			message = e.getMessage();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getSynonymMappingForGlobalRuleVerification - END");
		response.put("status", status);
		response.put("message", message);
		return new ResponseEntity<Object>(response, httpStatus);
	}

	/*
	 * API to add unmatched synonyms mapping details for global rule and validate
	 * global rule synonyms details.
	 */
	@RequestMapping(value = "/dbconsole/validateSynonymMappingForGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateSynonymMappingForGlobalRule(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		HttpStatus httpStatus = HttpStatus.OK;
		LOG.info("dbconsole/validateSynonymMappingForGlobalRule - START");
		try {
			DateUtility.DebugLog("validateSynonymMappingForGlobalRule",
					String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));

			JSONObject requestJson = new JSONObject(inputsJsonStr);
			JSONObject inputJson = requestJson.getJSONObject("globalRuleDetails");
			ListColGlobalRules lcr = new ListColGlobalRules();

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String ruleCategory = inputJson.getString("ruleCategory");
					if (!ruleCategory.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)) {
						int domainId = 0;
						if (inputJson.get("domainId") != null && !inputJson.get("domainId").equals("")) {
							lcr.setDomain_id(inputJson.getInt("domainId"));
							domainId = inputJson.getInt("domainId");
						}

						JSONArray leftUnmatchedSynonyms = requestJson.getJSONArray("leftUnmatchedSynonyms");
						JSONArray rightUnmatchedSynonyms = requestJson.getJSONArray("rightUnmatchedSynonyms");
						// Add leftUnmatchedSynonyms
						if (leftUnmatchedSynonyms != null && leftUnmatchedSynonyms.length() > 0) {
							globalRuleService.addPossibleNamesToSynonyms(domainId, leftUnmatchedSynonyms);
						}
						// Add rightUnmatchedSynonyms
						if (rightUnmatchedSynonyms != null && rightUnmatchedSynonyms.length() > 0) {
							globalRuleService.addPossibleNamesToSynonyms(domainId, rightUnmatchedSynonyms);
						}

						String matchingRules = inputJson.getString("matchingRules");
						String expression = inputJson.getString("ruleExpression");

						if (matchingRules == null)
							matchingRules = "";

						if (expression == null)
							expression = "";

						lcr.setRuleName(inputJson.getString("ruleName"));
						lcr.setExpression(expression);
						lcr.setMatchingRules(matchingRules);

						if (inputJson.getString("description") != null) {
							lcr.setDescription(inputJson.getString("description"));
						}
						if (inputJson.get("rightIdData") != null && !inputJson.get("rightIdData").equals("")) {
							lcr.setIdRightData(inputJson.getLong("rightIdData"));
						}
						if (inputJson.get("ruleThreshold") != null && !inputJson.get("ruleThreshold").equals("")) {
							lcr.setRuleThreshold(inputJson.getDouble("ruleThreshold"));
						}
						if (inputJson.get("filterId") != null && !inputJson.get("filterId").equals("")) {
							lcr.setFilterId(inputJson.getInt("filterId"));
							GlobalFilters globalFilter = globalRuleDAO
									.getGlobalFilterById(inputJson.getInt("filterId"));
							if (globalFilter != null)
								lcr.setFilterCondition(globalFilter.getFilterCondition());
						}
						if (inputJson.get("rightTemplateFilterId") != null
								&& !inputJson.get("rightTemplateFilterId").equals("")) {
							lcr.setRightTemplateFilterId(inputJson.getInt("rightTemplateFilterId"));
							GlobalFilters globalFilter = globalRuleDAO
									.getGlobalFilterById(inputJson.getInt("rightTemplateFilterId"));
							if (globalFilter != null)
								lcr.setRightTemplateFilterCondition(globalFilter.getFilterCondition());
						}

						lcr.setProjectId(inputJson.getLong("projectId"));
						lcr.setDimensionId(inputJson.getLong("dimensionId"));

						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.REFERENTIAL_RULE);
							lcr.setMatchingRules("");
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
							lcr.setRuleType(DatabuckConstants.ORPHAN_RULE);
							lcr.setExpression("");
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CROSSREFERENTIAL_RULE);
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_ORPHAN_RULE);
							lcr.setExpression("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK);
							lcr.setMatchingRules("");
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE);
						} else if (ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)) {
							lcr.setRuleType(DatabuckConstants.SQL_INTERNAL_RULE);
							lcr.setFilterId(0);
							lcr.setFilterCondition("");
							lcr.setMatchingRules("");
						}

						String validityStatus = "fail";
						String validityMessage = "";
						JSONObject match_expr_status = null;
						JSONObject rule_expr_status = null;

						// Validate Matching condition
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
							match_expr_status = globalRuleService.validateSynonymExpression(matchingRules, domainId);
						}
						// Validate Rule Expression
						if (ruleCategory.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
								|| ruleCategory.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
							rule_expr_status = globalRuleService.validateSynonymExpression(expression, domainId);
						}

						if (match_expr_status != null
								&& match_expr_status.getString("status").equalsIgnoreCase("failed")) {
							validityMessage = match_expr_status.getString("message");
						} else if (rule_expr_status != null
								&& rule_expr_status.getString("status").equalsIgnoreCase("failed")) {
							validityMessage = rule_expr_status.getString("message");
						} else
							validityStatus = "success";
						LOG.debug("validity" + validityMessage + ":" + validityStatus);

						if (validityStatus.equalsIgnoreCase("success")) {
							long idData = inputJson.getLong("idData");
							json = globalRuleService.getGlobalRuleDetails(idData, lcr);
						} else {
							json.put(validityStatus, validityMessage);
						}

						if (json != null) {
							response.put("result", json.toMap());
							status = "success";
							message = "Details fetched successfully.";
						} else {
							status = "failed";
							message = "Invalid synonym expression found.";
						}
					} else {
						status = "failed";
						message = "Validate global rule does not support Direct Query Rule.";
					}
				} else {
					status = "failed";
					message = "Token is expired";
					LOG.error("Token is expired");
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				status = "failed";
				message = "Token is missing in headers.";
				LOG.error("Token is missing in headers.");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failed";
			message = e.getMessage();
			LOG.error(e.getMessage());
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		response.put("status", status);
		response.put("message", message);
		LOG.info("dbconsole/validateSynonymMappingForGlobalRule - END");
		return new ResponseEntity<Object>(response, httpStatus);
	}

	/*
	 * API to add details and call validate global rule script.
	 */
	@RequestMapping(value = "/dbconsole/validateGlobalRule", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateGlobalRule(@RequestHeader HttpHeaders headers,
			HttpServletResponse httpResponse, @RequestBody String jsonStr) {
		String status = "failed";
		String message = "";
		Map<String, Object> response = new HashMap<String, Object>();
		LOG.info("dbconsole/validateGlobalRule - START");
		try {

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("jsonStr:" + jsonStr);
					JSONObject inputObj = new JSONObject(jsonStr);
					JSONObject json = new JSONObject();

					String uniqueId = iTaskDAO.insertRunGlobalRuleValidateTask("local", "", -1, inputObj);

					String databuckHome = getDatabuckHome();
					String scriptPath = databuckHome + "/scripts/globalRuleValidate.sh";
					long pid = -1;

					List<String> list = new ArrayList<>();
					list.add(scriptPath);
					list.add(uniqueId);
					// Execute
					ProcessBuilder builder = new ProcessBuilder().command(list);

					// execute script
					Process process = builder.start();
					LOG.info("\n====>Global Rule Validation is in progress !! Please wait ...");

					try {
						if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
							Field f = process.getClass().getDeclaredField("pid");
							f.setAccessible(true);
							pid = f.getLong(process);
							f.setAccessible(false);
							LOG.debug("process id=" + pid);
							status = "success";
							iTaskDAO.updateRunGlobalRuleUpdateTask(uniqueId, pid);
							json.put("uniqueId", uniqueId);
							response.put("result", json.toMap());
							response.put("status", "success");
							response.put("message", "Validate Global Rule initiated successfully.");
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					} catch (Exception e) {
						LOG.error("\n====>Exception occurred failed to get the process Id of current job !!");
						pid = -1;
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			message = "Failed to validate global rule.";
		}
		response.put("status", status);
		response.put("message", message);
		LOG.info("dbconsole/validateGlobalRule - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/getValidateGlobalRuleStatus", method = RequestMethod.POST)
	public ResponseEntity<Object> getValidateGlobalRuleStatus(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getValidateGlobalRuleStatus - START");

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
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String uniqueId = inputJson.getString("uniqueId");
						JSONObject globalRuleByUniqueId = globalRuleDAO.getValidateGlobalRuleByUniqueId(uniqueId);
						if (globalRuleByUniqueId != null && globalRuleByUniqueId.length() > 0) {
							String databuckHome = getDatabuckHome();
							String logFilePath = databuckHome + "/logs/validatecustomrule/" + uniqueId + ".txt";
							globalRuleByUniqueId.put("logFilePath", logFilePath);
							result.put("globalRuleData", globalRuleByUniqueId);
							status = "success";
							message = "success";
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					LOG.error(message);
				}
			} else {
				message = "Token is missing in header";
				responseStatus = HttpStatus.EXPECTATION_FAILED;
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
		LOG.info("dbconsole/getValidateGlobalRuleStatus - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	/*
	 * API to get synonyms mapping details for global filter.
	 */
	@RequestMapping(value = "/dbconsole/getSynonymMappingForGlobalFilterVerification", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getSynonymMappingForGlobalFilterVerification(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		HttpStatus httpStatus = HttpStatus.OK;
		LOG.info("dbconsole/getSynonymMappingForGlobalFilterVerification - START");
		try {
			DateUtility.DebugLog("getSynonymMappingForGlobalFilterVerification ",
					String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));

			JSONObject inputJson = new JSONObject(inputsJsonStr);

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					GlobalFilters globalFilters = new GlobalFilters();
					if (inputJson.has("filterCondition")) {
						globalFilters.setFilterCondition(inputJson.getString("filterCondition"));
					}
					if (inputJson.has("filterName")) {
						globalFilters.setFilterName(inputJson.getString("filterName"));
					}
					if (inputJson.has("description")) {
						globalFilters.setDescription(inputJson.getString("description"));
					}
					if (inputJson.has("domain")) {
						globalFilters.setDomain(inputJson.getString("domain"));
					}

					int domainId = globalRuleDAO.getDomainId(globalFilters.getDomain());
					globalFilters.setDomainId(domainId);

					if (globalFilters.getFilterCondition() != null && !globalFilters.getFilterCondition().isEmpty()) {

						String validityStatus = "fail";
						String validateMessage = "";
						JSONObject filterConditionExpressionStatus = globalRuleService
								.validateSynonymExpression(globalFilters.getFilterCondition(), domainId);

						if (filterConditionExpressionStatus != null
								&& filterConditionExpressionStatus.getString("status").equalsIgnoreCase("failed")) {
							validateMessage = filterConditionExpressionStatus.getString("message");
						} else
							validityStatus = "success";
						LOG.debug("validity" + validateMessage + ":" + validityStatus);

						if (validityStatus.equalsIgnoreCase("success")) {
							long idData = inputJson.getLong("idData");
							json = globalRuleService.getGlobalFilterDetails(idData, globalFilters);
						} else {
							json.put(validityStatus, validateMessage);
						}

						if (json != null) {
							response.put("result", json.toMap());
							status = "success";
							message = "Details fetched successfully.";
						} else {
							status = "failed";
							message = "Invalid synonym expression found.";
						}
					} else {
						status = "failed";
						message = "Please provide filter condition";
					}
				} else {
					LOG.error("Token is expired.");
					status = "failed";
					message = "Token is expired.";
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				status = "failed";
				message = "Token is missing in headers.";
				LOG.error("Token is missing in headers.");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failed";
			message = e.getMessage();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/getSynonymMappingForGlobalFilterVerification - END");
		response.put("status", status);
		response.put("message", message);
		return new ResponseEntity<Object>(response, httpStatus);
	}

	/*
	 * API to add unmatched synonyms mapping details for global filter and validate
	 * global filter synonyms details.
	 */
	@RequestMapping(value = "/dbconsole/validateSynonymMappingForGlobalFilter", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateSynonymMappingForGlobalFilter(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		HttpStatus httpStatus = HttpStatus.OK;
		LOG.info("dbconsole/validateSynonymMappingForGlobalFilter - START");
		try {
			DateUtility.DebugLog("validateSynonymMappingForGlobalFilter ",
					String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));

			JSONObject requestJson = new JSONObject(inputsJsonStr);
			JSONObject inputJson = requestJson.getJSONObject("globalFilterDetails");

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					GlobalFilters globalFilters = new GlobalFilters();
					if (inputJson.has("filterCondition")) {
						globalFilters.setFilterCondition(inputJson.getString("filterCondition"));
					}
					if (inputJson.has("filterName")) {
						globalFilters.setFilterName(inputJson.getString("filterName"));
					}
					if (inputJson.has("description")) {
						globalFilters.setDescription(inputJson.getString("description"));
					}
					if (inputJson.has("domain")) {
						globalFilters.setDomain(inputJson.getString("domain"));
					}

					int domainId = globalRuleDAO.getDomainId(globalFilters.getDomain());
					globalFilters.setDomainId(domainId);

					JSONArray unmatchedSynonyms = requestJson.getJSONArray("unmatchedSynonyms");
					// Add leftUnmatchedSynonyms
					if (unmatchedSynonyms != null && unmatchedSynonyms.length() > 0) {
						globalRuleService.addPossibleNamesToSynonyms(domainId, unmatchedSynonyms);
					}

					if (globalFilters.getFilterCondition() != null && !globalFilters.getFilterCondition().isEmpty()) {
						String validityStatus = "fail";
						String validateMessage = "";
						JSONObject filterConditionExpressionStatus = globalRuleService
								.validateSynonymExpression(globalFilters.getFilterCondition(), domainId);

						if (filterConditionExpressionStatus != null
								&& filterConditionExpressionStatus.getString("status").equalsIgnoreCase("failed")) {
							validateMessage = filterConditionExpressionStatus.getString("message");
						} else
							validityStatus = "success";
						LOG.debug("validity" + validateMessage + ":" + validityStatus);

						if (validityStatus.equalsIgnoreCase("success")) {
							long idData = inputJson.getLong("idData");
							json = globalRuleService.getGlobalFilterDetails(idData, globalFilters);
						} else {
							json.put(validityStatus, validateMessage);
						}

						if (json != null) {
							response.put("result", json.toMap());
							status = "success";
							message = "Details fetched successfully.";
						} else {
							status = "failed";
							message = "Invalid synonym expression found.";
						}
					} else {
						status = "failed";
						message = "Please provide filter condition";
					}
				} else {
					LOG.error("Token is expired.");
					status = "failed";
					message = "Token is expired.";
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				status = "failed";
				message = "Token is missing in headers.";
				LOG.error("Token is missing in headers.");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = "failed";
			message = e.getMessage();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/validateSynonymMappingForGlobalFilter - END");
		response.put("status", status);
		response.put("message", message);
		return new ResponseEntity<Object>(response, httpStatus);
	}

	/*
	 * API to Initiate Validate Global Filter Spark task.
	 */
	@RequestMapping(value = "/dbconsole/validateGlobalFilter", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> validateGlobalFilter(@RequestHeader HttpHeaders headers,
			HttpServletResponse httpResponse, @RequestBody String jsonStr) {
		String status = "failed";
		String message = "";
		Map<String, Object> response = new HashMap<String, Object>();
		LOG.info("dbconsole/validateGlobalFilter - START");
		try {

			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("jsonStr:" + jsonStr);
					JSONObject inputObj = new JSONObject(jsonStr);
					JSONObject json = new JSONObject();

					String uniqueId = iTaskDAO.insertRunGlobalFilterValidateTask("local", "", -1, inputObj);

					String databuckHome = getDatabuckHome();
					String scriptPath = databuckHome + "/scripts/globalFilterValidate.sh";
					LOG.info("\n=====> Script Path :: " + scriptPath);
					long pid = -1;

					List<String> list = new ArrayList<>();
					list.add(scriptPath);
					list.add(uniqueId);
					// Execute
					ProcessBuilder builder = new ProcessBuilder().command(list);

					// execute script
					Process process = builder.start();
					LOG.info("\n====>Global Filter Validation is in progress !! Please wait ...");

					try {
						if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
							Field f = process.getClass().getDeclaredField("pid");
							f.setAccessible(true);
							pid = f.getLong(process);
							f.setAccessible(false);
							LOG.debug("process id=" + pid);
							status = "success";
							iTaskDAO.updateRunGlobalFilterUpdateTask(uniqueId, pid);
							json.put("uniqueId", uniqueId);
							response.put("result", json.toMap());
							response.put("status", "success");
							response.put("message", "Validate Global Filter initiated successfully.");
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					} catch (Exception e) {
						LOG.error("\n====>Exception occurred failed to get the process Id of current job !!");
						pid = -1;
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");

					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");

				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			message = "Failed to validate global rule.";
		}
		response.put("status", status);
		response.put("message", message);
		LOG.info("dbconsole/validateGlobalFilter - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/getValidateGlobalFilterStatus", method = RequestMethod.POST)
	public ResponseEntity<Object> getValidateGlobalFilterStatus(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getValidateGlobalFilterStatus - START");

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
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String uniqueId = inputJson.getString("uniqueId");
						JSONObject globalFilterByUniqueId = globalRuleDAO.getValidateGlobalFilterByUniqueId(uniqueId);
						if (globalFilterByUniqueId != null && globalFilterByUniqueId.length() > 0) {
							String databuckHome = getDatabuckHome();
							String logFilePath = databuckHome + "/logs/GlobalFilters/" + uniqueId + ".txt";
							globalFilterByUniqueId.put("logFilePath", logFilePath);
							result.put("globalFilterData", globalFilterByUniqueId);
							status = "success";
							message = "success";
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
					LOG.error(message);
				}
			} else {
				message = "Token is missing in header";
				responseStatus = HttpStatus.EXPECTATION_FAILED;
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
		LOG.info("dbconsole/getValidateGlobalFilterStatus - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/updateNullFilterColumns", method = RequestMethod.POST)
	public ResponseEntity<Object> updatenullFilterColumns(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/updateNullFilterColumns - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if (validateToken(token.toString())) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						String message = "";
						String status = "failed";
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.optLong("idData", 0);
						long idApp = inputJson.optLong("idApp", 0);
						long ruleId = inputJson.optLong("ruleId", 0);
						String nullFilterColumns = inputJson.optString("nullFilterColumns", "");

						if (idData != 0 && idApp != 0 && ruleId != 0) {
							JSONObject result = globalRuleService.updateNullFilterColumn(idApp, idData, ruleId,
									nullFilterColumns);
							if (!result.getString("status").equalsIgnoreCase("failed")) {
								message = "Null Filter columns updated successfully";
								status = "success";
							} else {
								message = "Failed to update null filter columns.";
							}
						} else {
							message = "Please provide proper parameters.";
						}
						response.put("status", status);
						response.put("message", message);
					} else {
						response.put("status", "failed");
						response.put("message", "Invalid request");
					}

				} else {
					response.put("status", "failed");
					response.put("message", "Token expired.");
					LOG.error("Token expired.");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateNullFilterColumns - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/updateNullFilterColumns - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/runValidationByCustomRuleId", method = RequestMethod.POST)
	public ResponseEntity<Object> runValidationByCustomRuleId(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/runValidationByCustomRuleId - START");
		JSONObject response = new JSONObject();
		String message = "";
		String status = "failed";
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if (validateToken(token.toString())) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String tableName = inputJson.optString("tableName", "");
						long ruleId = inputJson.optLong("ruleId", 0);

						if (!tableName.trim().isEmpty() && ruleId != 0) {
							JSONObject executionResult = globalRuleService.runValidationByCustomRuleId(tableName,
									ruleId);
							if (!executionResult.getString("status").equalsIgnoreCase("failed")) {
								response.put("result", executionResult.getJSONObject("result"));
								message = "Rule executed successfully";
								status = "success";
							} else
								message = executionResult.getString("message");
						} else
							message = "Please provide proper parameters.";
					} else {
						message = "Invalid request";
						LOG.error("Invalid request");
					}
				} else {
					message = "Token expired.";
					LOG.error("Token expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers.";
				LOG.error("Token is missing in headers.");
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			LOG.error(e.getMessage());
			LOG.info("dbconsole/runValidationByCustomRuleId - END");
		}
		LOG.info("dbconsole/runValidationByCustomRuleId - END");
		response.put("status", status);
		response.put("message", message);
		return new ResponseEntity<Object>(response.toString(), responseStatus);
	}

}