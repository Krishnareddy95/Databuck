package com.databuck.restcontroller;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.*;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.RuleCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@CrossOrigin(origins = "*")
@RestController
public class RuleCatalogRestController {
	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	private IUserDAO userDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private IValidationCheckDAO validationcheckdao;
	
	private static final Logger LOG = Logger.getLogger(RuleCatalogRestController.class);
	
	@RequestMapping(value = "/dbconsole/rc/saveRuleCatalogStatus", method = RequestMethod.POST)
	public ResponseEntity<Object> SaveRuleCatalogStatus(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("\n*****====> SaveRuleCatalogStatus for validation - START <====*****");
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
					int approve_status = inputJson.getInt("approvalStatusCode");
					String approve_comments = inputJson.getString("approvalComments");

					// Validate idApp
					ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
					if (listApplications != null) {

						// Validate approval status code
						String val_new_approve_status = ruleCatalogDao.getApproveStatusNameById(approve_status);

						if (val_new_approve_status != null && !val_new_approve_status.trim().isEmpty()) {

							// Get user details by token
							UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

							Long reviewedByUser = userToken.getIdUser();
							String approverName = "";

							// Check ActiveDirectory flag
							String activeDirectoryFlag = appDbConnectionProperties
									.getProperty("isActiveDirectoryAuthentication");

							if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
								reviewedByUser = null;
								approverName = userToken.getEmail();
							} else
								approverName = userDAO.getUserNameByUserId(reviewedByUser);

							LOG.debug("\n====> approverName: " + approverName);
							LOG.debug("\n====> approve_status: " + approve_status);
							LOG.debug("\n====> approve_comments: " + approve_comments);
							LOG.debug("\n====> reviewedByUser: " + reviewedByUser);

							boolean updateStatus = ruleCatalogService.updateRuleCatalogStatus(idApp, approve_status,
									approve_comments, reviewedByUser, approverName);
							if (updateStatus) {

								status = "success";
								message = "Rule Catalog Approval Status is Updated Successfully";
								LOG.info(message);
							} else {
								message = "Failed to update Rule Catalog Approval Status";
								LOG.error(message);
							}
						} else {
							message = "Invalid approvalStatusCode";
							LOG.error(message);
						}
					} else {
						message = "Invalid idApp";
						LOG.error(message);
					}
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
			LOG.error(e.getMessage());
			message = "Failed to update the Rule Catalog status, please retry or contact admin.";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/rc/saveRuleCatalogStatus - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/rc/getValidationApprovalStatus", method = RequestMethod.POST)
	public ResponseEntity<Object> getValidationApprovalStatus(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {

		LOG.info("\n======> getValidationApprovalStatus for Rule catalog- START <======");
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
				LOG.debug(token.toString());
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
					ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
					if (listApplications != null) {
						// getValidationApprovalStatus in rule catalog
						// Get the current approval status of validation
						Map<String, String> valAppStatus = validationCheckDAO.getRuleCatalogApprovalStatus(idApp);
						result.put("validationStatus", valAppStatus);

						// Get the staging approval status of validation
						Map<String, String> stg_rcStatus = validationCheckDAO
								.getStagingRuleCatalogApprovalStatus(idApp);
						result.put("stagingCatalogStatus", stg_rcStatus);

						// Get the RuleCatalog Approval status list
						List<Map<String, Object>> valStatusList = ruleCatalogDao.getRCApprovalOptionsList();
						result.put("ruleCatalogStatusLists", valStatusList);

						message = "success";
						status = "success";
						LOG.info(message);
						responseStatus = HttpStatus.OK;
					} else {
						message = "Invald idApp";
						LOG.error(message);
					}
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/rc/getValidationApprovalStatus - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/rc/getDimensionDefectCodeList", method = RequestMethod.GET)
	public ResponseEntity<Object> getDimensionDefectCodeList(@RequestHeader HttpHeaders headers) {

		LOG.info("\n======> getDimensionDefectCodeList for Rule catalog- START <======");

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
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					// getDimensionDefectCodeList in rule catalog
					List<Map<String, Object>> defectDimensionList = ruleCatalogDao.getDimensionDefectCodeList();

					if (defectDimensionList != null && !defectDimensionList.isEmpty()) {
						status = "success";
						result.put("defectDimensionList", defectDimensionList);
					} else {
						message = "defectDimensionList is empty";
						LOG.error(message);
					}
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/rc/getDimensionDefectCodeList - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/rc/getRuleCatalogForValidation", method = RequestMethod.POST)
	public ResponseEntity<Object> getRuleCatalogForValidation(@RequestHeader HttpHeaders headers,
			HttpServletRequest request, @RequestBody String inputsJsonStr) {
		LOG.debug("\n======> getRuleCatalogForValidation - START <======");

		JSONObject json = new JSONObject();
		JSONObject resultData = new JSONObject();

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
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
					String userLDAPGroups = userToken.getUserLDAPGroups();
					Long idRole = userToken.getIdRole();
					if (inputsJsonStr != null && !inputsJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputsJsonStr);
						JSONObject inputJson = new JSONObject(inputsJsonStr);
						long idApp = inputJson.getLong("idApp");
						
						// Get validation details
						ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
						if (listApplications != null) {
							// Get template details
							ListDataSource listDataSource = listDataSourceDao
									.getDataFromListDataSourcesOfIdData(listApplications.getIdData());

							// Get the RuleCatalog Approval status of validation
							Map<String, String> valAppStatus = validationCheckDAO.getRuleCatalogApprovalStatus(idApp);

							String approvalStatusCode = valAppStatus.get("approvalStatusCode");
							String approvalStatusName = ruleCatalogDao
									.getApproveStatusNameById(Integer.parseInt(approvalStatusCode));

							// Check if validation is approved for Production
							boolean isValidationProdApproved = (approvalStatusName != null && approvalStatusName
									.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) ? true : false;

							// Check if validation is active or not
							boolean isValidationActive = (listApplications != null
									&& listApplications.getActive() != null
									&& listApplications.getActive().equalsIgnoreCase("yes")) ? true : false;

							if (!isValidationActive) {
								LOG.error(
										"\n====>Validation is deactivated, Rule catalog Approve Validation button will be disabled !!\n");
							}

							resultData.put("isValidationActive", isValidationActive);
							resultData.put("isValidationProdApproved", isValidationProdApproved);
							resultData.put("approvalStatusCode", approvalStatusCode);
							resultData.put("approvalStatus", valAppStatus.get("approvalStatus"));
							resultData.put("approvalComments", valAppStatus.get("approvalComments"));
							resultData.put("isApprover",
									ruleCatalogService.isLoggedInUserApprover(userLDAPGroups, idRole));
							resultData.put("idData", listDataSource.getIdData());
							resultData.put("templateName", listDataSource.getName());
							resultData.put("templateDataLocation", listDataSource.getDataLocation());
							resultData.put("idApp", idApp);
							resultData.put("validationName", listApplications.getName());

							// Get the Rule catalog data for the validation
							JSONObject ruleCatalogData = ruleCatalogService.getRuleCatalogRecordList(idApp);
							JSONArray dataset = ruleCatalogData.getJSONArray("DataSet");
							resultData.put("ruleCatalogData", dataset);
							status = "success";

						} else {
							message = "Invalid validation Id";
							LOG.error(message);
						}
					} else {
						message = "Invalid Request";
						LOG.error(message);
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
		json.put("result", resultData);
		LOG.info("dbconsole/rc/getRuleCatalogForValidation - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/rc/saveRuleCatalogForEdit", method = RequestMethod.POST)
	public ResponseEntity<Object> saveRuleCatalogForEdit(@RequestHeader HttpHeaders headers, HttpServletRequest request,
			@RequestBody String inputJsonStr) {
		LOG.info("\n======> saveRuleCatalogForEdit - START <======");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		boolean result = false;

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

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						ObjectMapper objectMapper = new ObjectMapper();
						RuleCatalog ruleCatalog = objectMapper.readValue(inputJsonStr, RuleCatalog.class);

						if (ruleCatalog != null) {

							if (ruleCatalog.getDimensionId() <= 0l)
								ruleCatalog.setDimensionId(-1);

							if (ruleCatalog.getDefectCode() == null
									|| ruleCatalog.getDefectCode().equalsIgnoreCase("-1"))
								ruleCatalog.setDefectCode("");

							if (ruleCatalog.getBusinessAttributeId() == null || ruleCatalog.getBusinessAttributeId().equalsIgnoreCase("-1"))
								ruleCatalog.setBusinessAttributeId("");
							// check updated status
							boolean updateStatus = ruleCatalogService
									.updateAssociatedRuleCatalogsForCatalogRuleEdit(ruleCatalog);

							if (updateStatus) {
								result = true;
								message = "Rule Catalog edit is Successful";
								status = "success";
								LOG.info(message);
							} else {
								message = "Rule Catalog edit is failed";
								LOG.error(message);
							}
						} else {
							message = "Invalid Request";
							LOG.error("Invalid Request");
						}
					} else {
						message = "Failed to get object from Body";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token is missing in headers";
				LOG.error("Token is missing in headers.");
			}
		} catch (Exception e) {
			message = "Failed to save Rule catalog edit changes";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/rc/saveRuleCatalogForEdit - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/rc/downloadRuleCatalog", method = RequestMethod.POST)
	public void downloadRuleCatalog(@RequestHeader HttpHeaders headers, HttpServletRequest request,
			HttpServletResponse response, @RequestBody String inputJsonStr) {
		LOG.debug("\n======> downloadRuleCatalog - START <======");

		String token = "";

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
					LOG.debug("Getting request parameters  " + inputJsonStr);
					JSONObject inputJson = new JSONObject(inputJsonStr);

					if (inputJsonStr != null) {
						long idApp = inputJson.getLong("idApp");

						// Get the Rule catalog data for the validation
						JSONObject oJsonResponse = ruleCatalogService.getRuleCatalogRecordList(idApp);
						if (oJsonResponse != null) {

							JSONArray jsonArray = (JSONArray) oJsonResponse.get("DataSet");

							// String csvFileName = "RuleCatalogReport" + LocalDateTime.now() + ".csv";
							if (jsonArray != null && jsonArray.length() > 0) {
								// set headers for the response
								String headerKey = "Content-Disposition";
								String headerValue = String.format("attachment; filename=\"%s\"",
										"rulecatalog_" + idApp + ".csv");
								response.setHeader(headerKey, headerValue);
								ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
										CsvPreference.STANDARD_PREFERENCE);
								String[] headerLine = { "Rule Staging Status", "Rule Reference", "Rule Category",
										"Rule Type", "Rule Name", "Column Name", "Custom/Global Rule Type",
										"Filter Condition", "Right Template Filter Condition", "Matching Rules", "Rule Expression", "Threshold",
										"Dimension", "Defect Code", "Rule Code", "Rule Description", "Tags", "Review Comments",
										"Reviewed By", "Reviewed Date", "Aging Enabled" };

								String[] fields = { "deltaType", "ruleReference", "ruleCategory", "ruleType",
										"ruleName", "columnName", "customOrGlobalRuleType", "filterCondition", "rightTemplateFilterCondition",
										"matchingRules", "ruleExpression", "threshold", "dimensionName", "defectCode",
										"ruleCode", "ruleDescription","ruleTags", "reviewComments", "reviewBy", "reviewDate",
										"agingCheckEnabled" };

								ObjectMapper objectMapper = new ObjectMapper();
								csvWriter.writeHeader(headerLine);

								// Iterating JSON array
								for (int i = 0; i < jsonArray.length(); i++) {
									RuleCatalog rc = objectMapper.readValue(jsonArray.get(i).toString(),
											RuleCatalog.class);

									String ruleExpression = rc.getRuleExpression();
									String ruleType = rc.getRuleType();
									if (ruleType != null && ruleExpression != null) {

										if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK))
											rc.setRuleExpression(ruleExpression.replaceAll("val:", "")
													.replaceAll("per:", "").replaceAll("\"", "\\\""));
										else
											rc.setRuleExpression(ruleExpression.replaceAll("\"", "\\\""));
									}

									String matchingRules = rc.getMatchingRules();
									if (matchingRules != null)
										rc.setMatchingRules(matchingRules.replaceAll("\"", "\\\""));

									String filterCondition = rc.getFilterCondition();
									if (filterCondition != null)
										rc.setFilterCondition(filterCondition.replaceAll("\"", "\\\""));
									
									String rightTemplateFilterCondition = rc.getRightTemplateFilterCondition();
									if (rightTemplateFilterCondition != null)
										rc.setRightTemplateFilterCondition(rightTemplateFilterCondition.replaceAll("\"", "\\\""));

									csvWriter.write(rc, fields);
								}
								csvWriter.close();
							}
						} else {
							LOG.error("Invalid Validation Id");
							throw new Exception("Invalid Validation Id");
						}
					} else {
						LOG.error("Failed request");
						throw new Exception("Failed request");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token expired");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in headers");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			try {
				response.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("dbconsole/rc/downloadRuleCatalog - END");
	}
	@RequestMapping(value = "/dbconsole/rc/getAssociatedGlobalRuleTagsForValidation", method = RequestMethod.POST)
	public ResponseEntity<Object> getAssociatedGlobalRuleTagsForValidation(@RequestHeader HttpHeaders headers, HttpServletRequest request,
																		   HttpServletResponse response, @RequestBody String inputJsonStr) {
		LOG.info("\n======> getAssociatedGlobalRuleTagsForValidation - START <======");
		JSONObject json = new JSONObject();
		JSONArray resultArray = new JSONArray();
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
					LOG.debug("Getting request parameters  " + inputJsonStr);
					JSONObject inputJson = new JSONObject(inputJsonStr);

					if (inputJsonStr != null) {
						long idApp = inputJson.getLong("idApp");
						// Get validation details
						ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);
						if (listApplications != null) {
							// Get the Rule catalog data for the validation
							JSONObject ruleCatalogData = ruleCatalogService.getRuleCatalogRecordList(idApp);
							JSONArray dataset = ruleCatalogData.getJSONArray("DataSet");

							for (int i=0; i < dataset.length(); i++) {
								JSONObject jsonObject = dataset.getJSONObject(i);
								String ruleTags = jsonObject.optString("ruleTags","");
								int ruleReferenceId = jsonObject.getInt("ruleReference");
								int customOrGlobalRuleId = jsonObject.getInt("customOrGlobalRuleId");
								String ruleType = jsonObject.getString("ruleType");
								if(ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)){
									JSONObject jObj = new JSONObject();
									jObj.put("ruleReferenceId", ruleReferenceId);
									jObj.put("globalRuleId", customOrGlobalRuleId);
									jObj.put("associatedTags", ruleTags);
									resultArray.put(jObj);
								}
								message="successfully fetched associated tags for globalRules";
								status="success";
								LOG.info(message);
							}
						} else {
							message = "Invalid validation id";
							LOG.error(message);
						}
					} else {
						message = "Invalid request";
						LOG.error(message);
					}
				} else {
					message = "Token expired";
					LOG.error("Token is expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error("Token is missing in headers.");
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", resultArray);
		LOG.info("dbconsole/rc/getAssociatedGlobalRuleTagsForValidation - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
}