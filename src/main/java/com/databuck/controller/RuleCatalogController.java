package com.databuck.controller;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.User;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DatabuckTagsDao;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IViewRuleDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dto.AppResultRequest;
import com.databuck.dto.GenericResponse;
import com.databuck.dto.UpdateRuleCatalogApprovalStatusRequest;
import com.databuck.service.AuthorizationService;
import com.databuck.service.RBACController;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.TokenValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

//@Api(tags="Catalog")
@Controller
public class RuleCatalogController {

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private IUserDAO userDAO;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private IViewRuleDAO viewRuleDao;

	@Autowired
	private DatabuckTagsDao databuckTagsDao;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private TokenValidator tokenValidator;

	@RequestMapping(value = "/getRuleCatalog", method = RequestMethod.GET)
	public ModelAndView getRuleCatalog(HttpServletRequest req, HttpSession session, @RequestParam long idApp,
			@RequestParam String fromMapping) {

		ModelAndView modelAndView = new ModelAndView("loginPage");
		try {

			boolean rbac = RBACController.rbac("Results", "R", session);

			if (rbac) {

				// Get validation details
				ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

				// Get template details
				ListDataSource listDataSource = listDataSourceDao
						.getDataFromListDataSourcesOfIdData(listApplications.getIdData());

				// Get the RuleCatalog Approval status of validation
				Map<String, String> valAppStatus = validationcheckdao.getRuleCatalogApprovalStatus(idApp);

				// get All tags
				JSONArray databuckTag = databuckTagsDao.getAllDatabuckTags();

				modelAndView = new ModelAndView("ruleCatalog");

				String approvalStatusCode = valAppStatus.get("approvalStatusCode");
				String approvalStatusName = ruleCatalogDao
						.getApproveStatusNameById(Integer.parseInt(approvalStatusCode));

				// Check if validation is approved for Production
				boolean isValidationProdApproved = (approvalStatusName != null
						&& approvalStatusName.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) ? true
								: false;

				// Check if validation is active or not
				boolean isValidationActive = (listApplications != null && listApplications.getActive() != null
						&& listApplications.getActive().equalsIgnoreCase("yes")) ? true : false;

				if (!isValidationActive) {
					System.out.println(
							"\n====>Validation is deactivated, Rule catalog Approve Validation button will be disabled !!\n");
				}

				modelAndView.addObject("isValidationActive", isValidationActive);
				modelAndView.addObject("isValidationProdApproved", isValidationProdApproved);
				modelAndView.addObject("approvalStatusCode", approvalStatusCode);
				modelAndView.addObject("approvalStatus", valAppStatus.get("approvalStatus"));
				modelAndView.addObject("approvalComments", valAppStatus.get("approvalComments"));

				String userLDAPGroups = null;
				Long idRole = null;
				if (session.getAttribute("UserLDAPGroups") != null) {
					userLDAPGroups = session.getAttribute("UserLDAPGroups").toString();
				}
				if (session.getAttribute("idRole") != null) {
					idRole = (long) session.getAttribute("idRole");
				}
				modelAndView.addObject("isApprover", ruleCatalogService.isLoggedInUserApprover(userLDAPGroups, idRole));

				modelAndView.addObject("idData", listDataSource.getIdData());
				modelAndView.addObject("templateName", listDataSource.getName());
				modelAndView.addObject("templateDataLocation", listDataSource.getDataLocation());
				modelAndView.addObject("tags", databuckTag.toList());

				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("validationName", listApplications.getName());
				modelAndView.addObject("fromMapping", fromMapping);
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/getValidationApprovalStatus", method = RequestMethod.GET)
	public void getValidationApprovalStatus(HttpServletResponse response, HttpSession session,
			@RequestParam long idApp) {

		boolean rbac = RBACController.rbac("Results", "R", session);

		try {
			if (rbac) {
				ObjectMapper oMapper = new ObjectMapper();
				JSONObject jsonObj = new JSONObject();

				// Get the current approval status of validation
				Map<String, String> valAppStatus = validationcheckdao.getRuleCatalogApprovalStatus(idApp);
				jsonObj.put("validationStatus", new JSONObject(oMapper.writeValueAsString(valAppStatus)));

				// Get the staging approval status of validation
				Map<String, String> stg_rcStatus = validationcheckdao.getStagingRuleCatalogApprovalStatus(idApp);
				jsonObj.put("stagingCatalogStatus", new JSONObject(oMapper.writeValueAsString(stg_rcStatus)));

				// Get the RuleCatalog Approval status list
				List<Map<String, Object>> valStatusList = ruleCatalogDao.getRCApprovalOptionsList();
				jsonObj.put("ruleCatalogStatusLists", new JSONArray(oMapper.writeValueAsString(valStatusList)));

				response.getWriter().println(jsonObj);
				response.getWriter().flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getDimensionDefectCodeList", method = RequestMethod.GET)
	public void getDimensionDefectCodeList(HttpServletResponse response, HttpSession session) {

		boolean rbac = RBACController.rbac("Results", "R", session);
		try {
			if (rbac) {
				ObjectMapper oMapper = new ObjectMapper();

				List<Map<String, Object>> defectDimensionList = ruleCatalogDao.getDimensionDefectCodeList();
				JSONObject jsonObj = new JSONObject();
				JSONArray jsonArray = new JSONArray(oMapper.writeValueAsString(defectDimensionList));
				jsonObj.put("DefectDimensionList", jsonArray);

				response.getWriter().println(jsonObj);
				response.getWriter().flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getRuleTypeList", method = RequestMethod.GET)
	public void getRuleTypeList(HttpServletResponse response, HttpSession session) {

		boolean rbac = RBACController.rbac("Results", "R", session);
		try {
			if (rbac) {
				ObjectMapper oMapper = new ObjectMapper();

				List<String> ruleTypeList = ruleCatalogService.getRuleTypeList();
				JSONObject jsonObj = new JSONObject();
				JSONArray jsonArray = new JSONArray(oMapper.writeValueAsString(ruleTypeList));
				jsonObj.put("ruleTypeList", jsonArray);

				response.getWriter().println(jsonObj);
				response.getWriter().flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/downloadRuleCatalog", method = RequestMethod.GET)
	public void downloadRuleCatalog(HttpServletResponse response, HttpSession session, long idApp) {

		boolean rbac = RBACController.rbac("Results", "R", session);

		String message = "";
		boolean isFileDownloadSuccess = true;
		try {
			if (rbac) {
				// Get the Rule catalog data for the validation
				JSONObject oJsonResponse = ruleCatalogService.getRuleCatalogRecordList(idApp);

				JSONArray jsonArray = (JSONArray) oJsonResponse.get("DataSet");

				// Prepare file header
				String headerLine = "Rule Staging Status,Rule Reference,Rule Category,Rule Type,Rule Name,Column Name,Custom/Global Rule Type,Filter Condition,Right Template Filter Condition, Matching Rules,Rule Expression,Threshold,Dimension,Defect Code,Rule Code,Rule Description,Review Comments,Reviewed By,Reviewed Date,Aging Enabled";

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", "rulecatalog_" + idApp + ".csv");
				response.setHeader(headerKey, headerValue);

				OutputStream outStream = response.getOutputStream();

				// Write header line
				outStream.write(headerLine.getBytes());
				outStream.write("\n".getBytes());

				if (jsonArray != null && jsonArray.length() > 0) {

					ObjectMapper objectMapper = new ObjectMapper();

					// Iterating JSON array
					for (int i = 0; i < jsonArray.length(); i++) {

						RuleCatalog rc = objectMapper.readValue(jsonArray.get(i).toString(), RuleCatalog.class);

						String ruleExpression = rc.getRuleExpression();
						String ruleType = rc.getRuleType();
						if (ruleType != null && ruleExpression != null) {

							if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK))
								ruleExpression = ruleExpression.replaceAll("val:", "").replaceAll("per:", "")
										.replaceAll("\"", "\\\"");
							else
								ruleExpression = ruleExpression.replaceAll("\"", "\\\"");
						}

						String matchingRules = rc.getMatchingRules();
						if (matchingRules != null)
							matchingRules = matchingRules.replaceAll("\"", "\\\"");

						String filterCondition = rc.getFilterCondition();
						if (filterCondition != null)
							filterCondition = filterCondition.replaceAll("\"", "\\\"");

						String rightTemplateFilterCondition = rc.getRightTemplateFilterCondition();
						if (rightTemplateFilterCondition != null)
							rightTemplateFilterCondition = rightTemplateFilterCondition.replaceAll("\"", "\\\"");

						String line = rc.getDeltaType() + "," + rc.getRuleReference() + "," + rc.getRuleCategory() + ","
								+ rc.getRuleType() + "," + rc.getRuleName() + "," + rc.getColumnName() + ","
								+ rc.getCustomOrGlobalRuleType() + "," + "\"" + filterCondition + "\",\""
								+ rightTemplateFilterCondition + "\",\"" + matchingRules + "\",\"" + ruleExpression
								+ "\"," + rc.getThreshold() + "," + rc.getDimensionName() + "," + rc.getDefectCode()
								+ "," + rc.getRuleCode() + "," + rc.getRuleDescription() + "," + rc.getReviewComments()
								+ "," + rc.getReviewBy() + "," + rc.getReviewDate() + "," + rc.getAgingCheckEnabled();

						outStream.write(line.getBytes());
						outStream.write("\n".getBytes());
					}

				}
				outStream.flush();
				outStream.close();

			}

		} catch (Exception e) {
			isFileDownloadSuccess = false;
			message = "Failed to download rule catalog file";
			e.printStackTrace();
		}

		if (!isFileDownloadSuccess) {
			try {
				session.setAttribute("errormsg", message);
				response.getWriter().println(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/loadRuleCatalog", method = RequestMethod.POST, produces = "application/json")
	public void loadRuleCatalogRecordList(@RequestParam long idApp, HttpSession oSession,
			HttpServletResponse oResponse) {

		try {
			// Get the Rule catalog data for the validation
			JSONObject oJsonResponse = ruleCatalogService.getRuleCatalogRecordList(idApp);
			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/SaveRuleCatalogFromEdit", method = RequestMethod.POST, produces = "application/json")
	public void SaveRuleCatalogFromEdit(@RequestParam String RuleCatalogEditRecordToSave, @RequestParam long idApp,
			HttpSession oSession, HttpServletResponse httpResponse) throws Exception {

		System.out.println("\n*****====> SaveRuleCatalogFromEdit - START <====*****");

		JSONObject jsonResponse = new JSONObject();
		boolean result = false;
		String message = "";

		try {
			long idUser = (Long) oSession.getAttribute("idUser");
			String username = userDAO.getfirstNameByUserId(idUser);

			ObjectMapper objectMapper = new ObjectMapper();
			RuleCatalog ruleCatalog = objectMapper.readValue(RuleCatalogEditRecordToSave, RuleCatalog.class);

			if (ruleCatalog.getDimensionId() <= 0l)
				ruleCatalog.setDimensionId(-1);

			if (ruleCatalog.getDefectCode() == null || ruleCatalog.getDefectCode().equalsIgnoreCase("-1"))
				ruleCatalog.setDefectCode("");

			ruleCatalog.setReviewBy(username);

			boolean updateStatus = ruleCatalogService.updateAssociatedRuleCatalogsForCatalogRuleEdit(ruleCatalog);

			if (updateStatus) {
				result = true;
				message = "Rule Catalog edit is Successful";

			} else
				message = "Rule Catalog edit is failed";

		} catch (Exception e) {
			e.printStackTrace();
			message = "Failed to save changes !!";

		}

		try {
			jsonResponse.put("Result", result);
			jsonResponse.put("Msg", message);
			httpResponse.getWriter().println(jsonResponse);
			httpResponse.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/SaveRuleCatalogStatus", method = RequestMethod.POST, produces = "application/json")
	public void SaveRuleCatalogStatus(@RequestParam long idApp, @RequestParam String DataToSave, HttpSession oSession,
			HttpServletResponse httpResponse) throws Exception {

		System.out.println("\n*****====> SaveRuleCatalogStatus for validation [" + idApp + "] - START <====*****");

		Map<String, String> oValidationAppStatus = new HashMap<String, String>();
		JSONObject jsonResponse = new JSONObject();
		String message = "";
		boolean status = false;

		try {
			ObjectMapper oMapper = new ObjectMapper();
			JSONObject oDataToSave = new JSONObject(DataToSave);

			String idUser = oSession.getAttribute("idUser").toString();
			Long reviewedByUser = Long.parseLong(idUser);
			String approverName = "";

			// Check activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
				reviewedByUser = null;
				approverName = (String) oSession.getAttribute("email");
			} else {
				approverName = userDAO.getUserNameByUserId(reviewedByUser);
			}

			int approve_status = oDataToSave.getInt("StatusCode");
			String approve_comments = oDataToSave.getString("Comments");

			System.out.println("\n====> approverName: " + approverName);
			System.out.println("\n====> approve_status: " + approve_status);
			System.out.println("\n====> approve_comments: " + approve_comments);
			System.out.println("\n====> reviewedByUser: " + reviewedByUser);

			boolean updateStatus = ruleCatalogService.updateRuleCatalogStatus(idApp, approve_status, approve_comments,
					reviewedByUser, approverName);

			if (updateStatus) {

				status = true;
				message = "Rule Catalog Approval Status is Updated Successfully";

				// Invoke external API to update the defect code to rules in catalog
				ruleCatalogService.fetchAndUpdateDefectCodeForRule(idApp, oDataToSave.getInt("StatusCode"));

				// Get validation approval status
				oValidationAppStatus = validationcheckdao.getRuleCatalogApprovalStatus(idApp);
				jsonResponse.put("ValidationStatus", new JSONObject(oMapper.writeValueAsString(oValidationAppStatus)));

				// Invoke JbpmWorkFlow
				CompletableFuture.runAsync(() -> {
					ruleCatalogService.triggerJbpmWorkFlow(idApp, oDataToSave.getInt("StatusCode"));
				});
			}

		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while updating rule catalog status ..");
			message = "Failed to update the Rule Catalog status, please retry or contact admin.";
			e.printStackTrace();
		}

		try {
			jsonResponse.put("Status", status);
			jsonResponse.put("Msg", message);
			httpResponse.getWriter().println(jsonResponse);
			httpResponse.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/enableOrDisableAgingCheck", method = RequestMethod.POST, produces = "application/json")
	public void enableOrDisableAgingCheck(HttpSession session, HttpServletResponse response,
			@RequestParam int ruleCatalogRowId, @RequestParam long idApp) throws Exception {
		Object user = session.getAttribute("user");

		JSONObject jsonResponse = new JSONObject();
		boolean status = false;
		String msg = "";

		try {
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean updateStatus = false;

			if (ruleCatalogService.isRuleCatalogEnabled() && ruleCatalogService.isValidationStagingActivated(idApp)) {

				String stg_agingCheckEnabled = iResultsDAO.getAgingCheckValueForRuleFromStaging(ruleCatalogRowId);

				if (stg_agingCheckEnabled != null && stg_agingCheckEnabled.equalsIgnoreCase("Y")) {
					// Disable it
					updateStatus = iResultsDAO.updateAgingCheckForRuleInStagingRuleCatalog(ruleCatalogRowId, "N");
				} else {
					// Enable it
					updateStatus = iResultsDAO.updateAgingCheckForRuleInStagingRuleCatalog(ruleCatalogRowId, "Y");
				}

			} else {
				String agingCheckEnabled = iResultsDAO.getAgingCheckValueForRule(ruleCatalogRowId);

				if (agingCheckEnabled != null && agingCheckEnabled.equalsIgnoreCase("Y")) {
					// Disable it
					updateStatus = iResultsDAO.updateAgingCheckForRuleInRuleCatalog(ruleCatalogRowId, "N");
				} else {
					// Enable it
					updateStatus = iResultsDAO.updateAgingCheckForRuleInRuleCatalog(ruleCatalogRowId, "Y");
				}

			}
			if (updateStatus) {
				status = true;
				msg = "Aging check updated successfully";
			} else
				msg = "Failed to update AgingCheck for Rule";

		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}

		try {
			jsonResponse.put("Result", status);
			jsonResponse.put("Msg", msg);
			response.getWriter().println(jsonResponse);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteRuleFromRuleCatalog", method = RequestMethod.POST, produces = "application/json")
	public void deleteRuleFromRuleCatalog(HttpSession session, HttpServletResponse response,
			@RequestParam String ruleCatalogRecordToDelete, @RequestParam long idApp) throws Exception {

		Object user = session.getAttribute("user");

		JSONObject jsonResponse = new JSONObject();
		boolean status = false;
		String msg = "";

		try {
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			ObjectMapper objectMapper = new ObjectMapper();
			RuleCatalog ruleCatalog = objectMapper.readValue(ruleCatalogRecordToDelete, RuleCatalog.class);

			boolean deleteStatus = ruleCatalogService.deleteRuleFromRuleCatalog(idApp, ruleCatalog);

			if (deleteStatus) {
				status = true;
				msg = "Rule deleted Successfully";
			} else
				msg = "Failed to delete Rule";

		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}

		try {
			jsonResponse.put("Result", status);
			jsonResponse.put("Msg", msg);
			response.getWriter().println(jsonResponse);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// API code start
	@ApiOperation(value = "Get list of validation by schema id", notes = "This API has been changed from POST to GET url which accepts "
			+ "only connection Id as input parameter.", tags = "Catalog")
	@ResponseBody
	@GetMapping(value = "/catalog/appList", produces = "application/json")
	public GenericResponse<List<Map<String, Object>>> loadListOfValidation(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam @ApiParam(name = "idDataSchema", value = "Schema Id", example = "1") long idDataSchema)
			throws Exception {
		List<Map<String, Object>> validationList = new ArrayList<Map<String, Object>>();

		GenericResponse<List<Map<String, Object>>> json = new GenericResponse<List<Map<String, Object>>>();
		String status = "failed";
		String reason = "";

		try {
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			if (isUserValid) {

				validationList = ruleCatalogDao.getValidationsListForSchema(idDataSchema);
				status = "success";
				json.setResult(validationList);
			} else {
				reason = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			reason = "Request failed";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.setStatus(status);
		json.setMessage(reason);
		return json;
	}

	// API -2
	@ApiOperation(value = "Get app result by app id", notes = "This API always serves the results for the latest run of a given"
			+ "validation check.", tags = "Catalog")
	@ResponseBody
	@RequestMapping(value = "/restapi/catalog/appResult", method = RequestMethod.GET)
	public ResponseEntity<Object> loadRuleCatalogValidationResultData(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam @ApiParam(name = "idApp", value = "idApp", example = "1") long idApp) {

		System.out.println("\n*****====> appResult - START <====*****");

		// GenericResponse<JSONArray> json = new GenericResponse<JSONArray>();
		JSONObject json = new JSONObject();		
		HttpStatus responseStatus = HttpStatus.OK;
		String status = "failed";
		String reason = "";
		try {
			JSONArray apiResult = new JSONArray();

			// Authorize the request
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			// Check if user is valid
			if (isUserValid) {
				ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplications != null) {

					// Get Max date, run and total records of validation
					Map<String, String> validationRunDetail = iResultsDAO.getValidationRunDetail(idApp);

					// Check if this run is valid
					boolean isRunValid = (validationRunDetail.size() == 3) ? true : false;

					if (isRunValid) {
						String executionDate = validationRunDetail.get("ResultRunDate");
						long run = Long.parseLong(validationRunDetail.get("ResultRunNo"));
						long totalRecords = Long.parseLong(validationRunDetail.get("TotalRecords"));

						// Check it is test run by execution date and run
						boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, executionDate,
								run);

						// Check if the validation staging is enabled
						boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);

						// Fetching listApplications to add project, domain and other details
						if (isTestRun && validationStatingEnabled)
							listApplications = ruleCatalogDao.getDataFromStagingListapplications(idApp);

						if (listApplications != null) {

							apiResult = ruleCatalogService.getRuleCatalogResultData(listApplications, idApp,
									executionDate, run, totalRecords, isTestRun);
							if (apiResult != null && apiResult.length() > 0) {
								status = "success";
								json.put("Result", apiResult);
								responseStatus = HttpStatus.OK;

							} else
								reason = "No rules found in Rule Catalog for the validation";

						} else
							reason = "Invalid idApp";

					} else
						reason = "No run information available for validation";

				} else
					reason = "Invalid idApp";

			} else {
				reason = "Invalid Authorization";
				responseStatus = HttpStatus.UNAUTHORIZED;
				// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			reason = "Request failed";
			responseStatus = HttpStatus.FORBIDDEN;

		}
		json.put("status", status);
		json.put("message", reason);

		return new ResponseEntity<Object>(json.toString(), responseStatus);

	}

	// API -3
	@ApiOperation(value = "Get app result for run", notes = "The API returns the results of a specific Date and Run given by the "
			+ "User", tags = "Catalog")
	@RequestMapping(value = "/restapi/catalog/getAppResultForRun", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String getAppResultForRun(HttpServletRequest request,
			HttpServletResponse response, @RequestBody AppResultRequest requestMap) {

		System.out.println("\n*****====> getAppResultForRun - START <====*****");

		GenericResponse<String> json = new GenericResponse<String>();
		String status = "failed";
		String reason = "";

		try {
			JSONArray apiResult = new JSONArray();

			// Authorize the request
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
			// Check if user is valid
			if (isUserValid) {
				long idApp = Long.parseLong(requestMap.getIdApp());
				System.out.println("\n====>idApp: " + idApp);

				String executionDate = requestMap.getExecutionDate();
				System.out.println("\n====>executionDate: " + executionDate);

				long run = Long.parseLong(requestMap.getRun());
				System.out.println("\n====>run: " + run);

				ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

				if (listApplications != null) {

					// Check it is test run by execution date and run
					boolean isTestRun = validationcheckdao.checkTestRunByExcecutionDateAndRun(idApp, executionDate,
							run);

					// Check if the validation staging is enabled
					boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);

					// Fetching listApplications to add project, domain and other details
					if (isTestRun && validationStatingEnabled)
						listApplications = ruleCatalogDao.getDataFromStagingListapplications(idApp);

					if (listApplications != null) {

						// Check if the validation Date and Run combination is valid or not
						boolean isRunValid = viewRuleDao.isDateRunValid(idApp, executionDate, run);

						if (isRunValid) {

							// Get total records for this date and run of validation
							long totalRecords = viewRuleDao.getValidationTotalRecordsByDateRun(idApp, executionDate,
									run);
 
							apiResult = ruleCatalogService.getRuleCatalogResultData(listApplications, idApp,
									executionDate, run, totalRecords, isTestRun);
							if (apiResult != null && apiResult.length() > 0) {
								status = "success";
								json.setResult(apiResult.toString().replace("\"", "'"));
							} else
								reason = "No rules found in Rule Catalog for the validation";

						} else
							reason = "No validation run details for the given date and run combination";

					} else
						reason = "Invalid idApp";

				} else
					reason = "Invalid idApp";

			} else {
				reason = "Invalid Authorization";
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			reason = "Request failed";
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		json.setStatus(status);
		json.setMessage(reason);
		ObjectMapper objectMapper = new ObjectMapper();
		String strjson="error occurred";
		try {
			strjson = objectMapper.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strjson;
	}

	// API -4
	@ApiOperation(value = "Update rule catalog approval status", notes = "This API is used to update status of the rules catalog for a particular"
			+ "Validation", tags = "Catalog")
	@ResponseBody
	@PostMapping(value = "/restapi/catalog/updateRuleCatalogApprovalStatus", produces = "application/json")
	public GenericResponse<String> updateRuleCatalogApprovalStatus(HttpServletRequest req, HttpServletResponse response,
			HttpServletRequest request, @RequestBody UpdateRuleCatalogApprovalStatusRequest requestMap)
			throws Exception {

		System.out.println("\n*****====> updateRuleCatalogApprovalStatus - START <====*****");

		GenericResponse<String> returnResult = new GenericResponse<String>();
		String status = "failed";
		String result="";
		String message = "";

		try {
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");

			if (isUserValid) {
				// Get idApp
				long idApp = Long.parseLong(requestMap.getIdApp());
				String req_approvalStatus = requestMap.getStatus();

				if (idApp > 0l) {

					// Get validation details
					ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);
					if (listApplications != null) {

						// Check if validation is Active
						if (listApplications.getActive() != null && listApplications.getActive().trim().equals("yes")) {

							if (req_approvalStatus != null && !req_approvalStatus.trim().isEmpty()) {
								String approve_status = "";

								if (req_approvalStatus.trim()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1)) {
									approve_status = DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1;
								} else if (req_approvalStatus.trim()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {
									approve_status = DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2;
								}

								if (!approve_status.isEmpty()) {

									String approve_comments = "Approved by API";
									Long approverId = null;

									// Get the approval status code
									int approve_status_code = ruleCatalogDao
											.getApprovalStatusCodeByStatusName(approve_status);

									// Fetch Approver Name from authorization header
									User approver = userDAO.getUserDataByName(requestMap.getApproverUserName());
									if (approver != null) {
										String approverName = approver.getFirstName();
										approverId = approver.getIdUser();
										System.out.println("\n====> approverName: " + approverName);
										System.out.println("\n====> approve_status: " + approve_status);
										System.out.println("\n====> approve_status_code: " + approve_status_code);
										System.out.println("\n====> approve_comments: " + approve_comments);
										System.out.println("\n====> approverId: " + approverId);

										// Approve validation
										ruleCatalogService.updateRuleCatalogStatus(idApp, approve_status_code,
												approve_comments, approverId, approverName);

										result = "passed";
										status="Success";
										message = "Validation is '" + approve_status + "'";
									}

								} else {
									message = "Invalid status in request,'"
											+ DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1 + "','"
											+ DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2
											+ "' are the valid status";
								    status="Fail";
								}
							} else {
								message = "Empty status in request,'" + DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1
										+ "','" + DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2
										+ "' are the valid status";
							status="Success";
							}
						} else {
							message = "Failed to change status, validation is Inactive";
							status="Fail";
						}

					} else {
						message = "Invalid Validation Id";
						status="Fail";
					}

				} else {
					message = "Invalid Validation Id";
					status="Fail";
				}

			} else {
				returnResult.setResult("Invalid Authorization");
				status="Fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResult.setResult("request failed");
		}

		returnResult.setResult(result);
		returnResult.setMessage(message);
		returnResult.setStatus(status);
		
		return returnResult;
	}

// API Code - ENDS

}