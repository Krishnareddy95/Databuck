package com.databuck.restcontroller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.constants.DatabuckConstants;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.AppGroupMapping;
import com.databuck.bean.DATA_QUALITY_INDEX;
import com.databuck.bean.DomainProject;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListSchedule;
import com.databuck.bean.ListTrigger;
import com.databuck.bean.Project;
import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.DataConnectionService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IProjectService;
import com.databuck.service.ITaskService;
import com.databuck.service.LoginService;
import com.databuck.service.PrimaryKeyMatchingResultService;
import com.databuck.service.TaskService;
import com.databuck.util.TokenValidator;

@CrossOrigin(origins = "*")
@RestController
public class TaskRestController {

	@Autowired
	PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	public IResultsDAO iResultsDAO;

	@Autowired
	public ITaskService iTaskService;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	private SchemaDAOI schemaDao;

	@Autowired
	private DataConnectionService dataConnectionService;

	@Autowired
	private TokenValidator tokenValidator;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private TaskService taskService;

	@Autowired
	public IProjectService projService;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private IUserDAO IUserdAO;

	@Autowired
	private Properties appDbConnectionProperties;

	
	private static final Logger LOG = Logger.getLogger(TaskRestController.class);
	@RequestMapping(value = "/dbconsole/getRolePermissionsByRoleId", method = RequestMethod.POST)
	public ResponseEntity<Object> getRolePermissionsByRoleId(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> idRole) {	
		LOG.info("/dbconsole/getRolePermissionsByRoleId - START");
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
				LOG.debug("token   " + token );
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  "+e.getMessage());
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("idRole "+idRole);

					try {

						JSONObject roleModuleObj = taskService.getRolePermissionsByRoleId((idRole.get("idRole")));
						if (roleModuleObj != null) {
							String moduleStatus = roleModuleObj.getString("status");
							if (moduleStatus.equalsIgnoreCase("success")) {
								json.put("result", roleModuleObj.getJSONArray("result"));
								message = "success";
								status = "success";
							} else {
								message = roleModuleObj.getString("message");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
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
			LOG.error("Exception  "+e.getMessage());
			
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("/dbconsole/getRolePermissionsByRoleId - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/checkIfRuleCatalogApprovalRequired", method = RequestMethod.POST)
	public ResponseEntity<Object> checkIfRuleCatalogApprovalRequired(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> idApp) {
		LOG.info("/dbconsole/checkIfRuleCatalogApprovalRequired - START");
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
			// Validate Token
			if (token.trim().isEmpty()) {
				message = "Please provide token.";
				LOG.error(message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			} else {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("idApp "+idApp);
					JSONObject aprStatus = taskService.checkIfRuleCatalogApprovalRequired(idApp.get("idApp"));
					message = aprStatus.getString("message");
					status = aprStatus.getString("status");
					approvalRequired = aprStatus.getString("approvalRequired");
					httpStatus = HttpStatus.OK;
				} else {
					message = "Token expired.";
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			httpStatus = HttpStatus.OK;
		}

		response.put("idApp", idApp.get("idApp"));
		response.put("message", message);
		response.put("approvalRequired", approvalRequired);
		response.put("status", status);
		LOG.info("/dbconsole/checkIfRuleCatalogApprovalRequired - END");
		return new ResponseEntity<Object>(response.toString(), httpStatus);
	}

	@RequestMapping(value = "/dbconsole/runTaskResult", method = RequestMethod.POST)
	public ResponseEntity<Object> runTaskResult(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/runTaskResult - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("appIds") || !requestBody.containsKey("validationRunType")) {
				throw new Exception("Required parameters not found.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {

				// Get deployMode
				String deployMode = clusterProperties.getProperty("deploymode");

				if (deployMode.trim().equalsIgnoreCase("2")) {
					deployMode = "local";
				} else {
					deployMode = "cluster";
				}

				List<String> arr = Arrays
						.asList(requestBody.get("appIds").replaceAll("\\[", "").replaceAll("]", "").split(","));
				List<Map<String, String>> listOfAppIdNUniqueId = new ArrayList<Map<String, String>>();

				for (int i = 0; i < arr.size(); i++) {

					Map<String, String> appIdNUniqueIdMap = new HashMap<>();
					// Get User email from token
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

					String uniqueId = iTaskDAO.insertRunScheduledTask((long) Integer.parseInt(arr.get(i)), "queued",
							deployMode, userToken.getEmail(), requestBody.get("validationRunType"));

					appIdNUniqueIdMap.put("appId", arr.get(i));
					appIdNUniqueIdMap.put("uniqueId", uniqueId);
					listOfAppIdNUniqueId.add(appIdNUniqueIdMap);
				}

				response.put("result", listOfAppIdNUniqueId);
				response.put("message", "Validation runned successfully.");
				response.put("status", "success");
				LOG.info("Validation runned successfully.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed...");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", "Failed to run validation.");
			response.put("status", "failed");
			LOG.error("Exception  "+e.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("/dbconsole/runTaskResult - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/statusPoll", method = RequestMethod.POST)
	public ResponseEntity<Object> statusPoll(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
//		LOG.info("/dbconsole/statusPoll - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus httpStatus = null;
		String status = null;
		try {
//			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error( "Please provide token.");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
//			LOG.debug("requestBody "+requestBody);

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("idApp") || !requestBody.containsKey("task_name")
					|| !requestBody.containsKey("uniqueId")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {

				String arr[] = requestBody.get("idApp").split(",");
				String uniqueIds[] = requestBody.get("uniqueId").split(",");
				int percentage = 5;
				for (int i = 0; i < arr.length; i++) {

					long idApp = (long) Integer.parseInt(arr[i]);
					String validationJobStatus = iTaskService.getTaskStatusFromRunScheduledTasks(idApp, uniqueIds[i]);
					status = "Task " + validationJobStatus;

					SqlRowSet appTypeFromListApplications = iTaskService.getAppTypeFromListApplications(idApp);
					String appType = "", appName = "";
					while (appTypeFromListApplications.next()) {
						appType = appTypeFromListApplications.getString(1);
						appName = appTypeFromListApplications.getString(2);
					}
					// for showing task % status
					if (appType.equalsIgnoreCase("Data Forensics")) {
						percentage = iTaskService
								.getStatusOfDfReadFromTaskProgressStatus((long) Integer.parseInt(arr[i]));
						if (percentage >= 30) {
							double count = iTaskService.getColumnCountYesInListApplicationsAndListDFTranrule(
									(long) Integer.parseInt(arr[i]));
							double passedStatus = iTaskService.getTaskStatusForPassed((long) Integer.parseInt(arr[i]));
							percentage = percentage + (int) ((passedStatus / count) * 70);

							if (percentage > 100) {
								percentage = 100;
							}
						}
					} else if (appType.equalsIgnoreCase("Data Matching") || appType.equalsIgnoreCase("Data Matching")
							|| appType.equalsIgnoreCase("Data Matching Group")
							|| appType.equalsIgnoreCase("Statistical Matching")
							|| appType.equalsIgnoreCase("Rolling DataMatching")) {
						percentage = iTaskService.getStatusForMatching((long) Integer.parseInt(arr[i]));
						// System.out.println("Matching");
					} else if (appType.equalsIgnoreCase("Schema Matching")) {
						percentage = iTaskService.getStatusForSchemaMatching((long) Integer.parseInt(arr[i]));
						if (percentage > 100) {
							percentage = 100;
						}
					}
					if (status.equalsIgnoreCase("Task completed1")) {
						ListApplications listApplicationsData = validationcheckdao
								.getdatafromlistapplications((long) Integer.parseInt(arr[i]));
						Long idData = listApplicationsData.getIdData();
						String recordCountAnomaly = listApplicationsData.getRecordCountAnomaly();
						DATA_QUALITY_INDEX DQI = new DATA_QUALITY_INDEX();
						// Record Anomaly Status on Dashboard
						// String recordAnomalyNewStatus =
						// iResultsDAO.getStatusFromRecordAnomalyTable(idApp);
						// DQI.setRecord_anomaly_status(recordAnomalyNewStatus);
						// RCA Dashboard
						String rcaStatus = iResultsDAO.getDashboardStatusForRCA((long) Integer.parseInt(arr[i]),
								recordCountAnomaly);
						DQI.setRecord_count_status(rcaStatus);
						// nullcountStatus Dashboard
						String nullcountStatus = iResultsDAO
								.getDashboardStatusForNullCount((long) Integer.parseInt(arr[i]));
						DQI.setNull_count_status(nullcountStatus);
						// numericalFieldStatsStatus Dashboard
						String numericalFieldStatus = iResultsDAO
								.getDashboardStatusFornumericalFieldStatsStatus((long) Integer.parseInt(arr[i]));
						DQI.setNumerical_field_status(numericalFieldStatus);
						// stringFieldStatsStatus Dashboard
						String stringFieldStatus = iResultsDAO
								.getDashboardStatusForstringFieldStatsStatus((long) Integer.parseInt(arr[i]));
						DQI.setString_field_status(stringFieldStatus);
						// recordAnomalyStatus Dashboard
						String recordAnomalystatus = iResultsDAO
								.getDashboardStatusForRecordAnomalyStatus((long) Integer.parseInt(arr[i]));
						DQI.setRecord_anomaly_status(recordAnomalystatus);
						// dataDriftStatus Dashboard
						// String dataDriftStatus =
						// iResultsDAO.getDashboardStatusForDataDriftStatus(idApp);
						// modelAndView.addObject("dataDriftStatus", dataDriftStatus);
						// System.out.println("dataDriftStatus="+dataDriftStatus);
						// allFieldsStatus Dashboard
						String allFieldsStatus = iResultsDAO
								.getDashboardStatusForAllFieldsStatus((long) Integer.parseInt(arr[i]));
						DQI.setAll_fields_status(allFieldsStatus);
						// identityfieldsStatus Dashboard
						String identityfieldsStatus = iResultsDAO
								.getDashboardStatusForIdentityfieldsStatus((long) Integer.parseInt(arr[i]));
						DQI.setUser_selected_fields_status(identityfieldsStatus);
						// Score
						boolean calculateScore = iResultsDAO
								.checkRunIntheResultTableForScore((long) Integer.parseInt(arr[i]), recordCountAnomaly);
						// calculateScore
						if (calculateScore) {
							DecimalFormat df = new DecimalFormat("#.00");
							// RecordAnomaly
							String recordAnomalyScore = iResultsDAO.CalculateScoreForRecordAnomaly(idData,
									(long) Integer.parseInt(arr[i]), recordCountAnomaly, rcaStatus,
									listApplicationsData);
							if (recordAnomalyScore.contains("∞")) {
								recordAnomalyScore = "0";
							}
							DQI.setRecord_count_score(recordAnomalyScore);
							// NullCountScore
							String nullCountScore = iResultsDAO.CalculateScoreForNullCount(idData,
									(long) Integer.parseInt(arr[i]), recordCountAnomaly, nullcountStatus,
									listApplicationsData);
							if (nullCountScore.contains("∞")) {
								nullCountScore = "0";
							}
							DQI.setNull_count_score(nullCountScore);
							// All FieldsScore
							String allFieldsScore = iResultsDAO.CalculateScoreForallFields(idData,
									(long) Integer.parseInt(arr[i]), allFieldsStatus, listApplicationsData);
							if (allFieldsScore.contains("∞")) {
								allFieldsScore = "0";
							}
							DQI.setAll_fields_score(allFieldsScore);
							// identityFieldsScore
							SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(
									(long) Integer.parseInt(arr[i]), "DQ_Uniqueness -Seleted Fields",
									listApplicationsData);
							String identityFieldsScore = "";
							while (dashboardDetails.next()) {
								identityFieldsScore = dashboardDetails.getString(4);
							}
							if (identityFieldsScore.contains("∞")) {
								identityFieldsScore = "0";
							}
							DQI.setUser_selected_fields_score(identityFieldsScore);
							// Numerical Field
							String numericalFieldScore = iResultsDAO.CalculateScoreForNumericalField(idData,
									(long) Integer.parseInt(arr[i]), numericalFieldStatus, listApplicationsData);
							if (numericalFieldScore.contains("∞")) {
								numericalFieldScore = "0";
							}
							DQI.setNumerical_field_score(numericalFieldScore);
							// string Field
							String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData,
									(long) Integer.parseInt(arr[i]), stringFieldStatus, listApplicationsData);
							if (stringFieldScore.contains("∞")) {
								stringFieldScore = "0";
							}
							DQI.setString_field_score(stringFieldScore);
							// Record Fingerprint
							String recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(idData,
									(long) Integer.parseInt(arr[i]), recordAnomalystatus, listApplicationsData);
							if (recordFieldScore.contains("∞")) {
								recordFieldScore = "0";
							}
							DQI.setRecord_anomaly_score(recordFieldScore);
							DQI.setValidation_check_id((long) Integer.parseInt(arr[i]));
							DQI.setValidation_check_name(listApplicationsData.getName());
							iTaskService.insertDataIntoDATA_QUALITY_INDEX(DQI, recordCountAnomaly);

						} else {

						}

					}

					// Check if job is completed, killed or failed and log it
					if (validationJobStatus != null && (validationJobStatus.equalsIgnoreCase("completed")
							|| validationJobStatus.equalsIgnoreCase("failed")
							|| validationJobStatus.equalsIgnoreCase("killed"))) {
						LOG.debug("\n====>Validation with Id [" + idApp + "] and uniqueId ["
								+ requestBody.get("uniqueId") + "] is " + validationJobStatus + "!!");
					}
				}
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("percentage", percentage);
				responseMap.put("idApp", requestBody.get("idApp"));
				responseMap.put("status", status);
				response.put("result", responseMap);
				response.put("message", "Status fetched successfully.");
				response.put("status", "success");
//				LOG.info("Status fetched successfully.");
				httpStatus = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed...");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", "Failed to fetch status validation.");
			LOG.error("Failed to fetch status validation.");
			response.put("status", "failed");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
//		LOG.info("/dbconsole/statusPoll - END");
		return new ResponseEntity<Object>(response, httpStatus);
	}

	@RequestMapping(value = "/dbconsole/getSchemaNames", method = RequestMethod.POST)
	public ResponseEntity<Object> getSchemaNames(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) throws IOException, URISyntaxException {
		LOG.info("/dbconsole/getSchemaNames - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/getSchemaNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			LOG.info("/dbconsole/getSchemaNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			if (!params.containsKey("projectId")) {
				LOG.error("Project id is missing in parameters.");
				throw new Exception("Project id is missing in parameters.");
			}
			Long projectId = params.get("projectId");
			response.put("status", "success");
			response.put("message", "Successfully fetched connection names.");
			LOG.info("Successfully fetched connection names.");
			response.put("result", schemaDao.getSchemaNames(projectId));
			LOG.info("/dbconsole/getSchemaNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to get connection names.");
			LOG.error("Exception  "+e.getMessage());
			response.put("stackTrace", e.getMessage());
			LOG.info("/dbconsole/getSchemaNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/triggerDataSchema", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerDataSchema(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/triggerDataSchema - START");
		String token = null;
		JSONObject response = new JSONObject();
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			LOG.error("Token is missing in headers.");
			response.put("message", "Token is missing in headers.");
			LOG.info("/dbconsole/triggerDataSchema - END");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			LOG.info("/dbconsole/triggerDataSchema - END");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		try {
			if (!params.containsKey("idDataSchema")) {
				LOG.error("idDataSchema is missing in parameters.");
				throw new Exception("idDataSchema is missing in parameters.");
			}
			response = dataConnectionService.runSchemaJob(params.get("idDataSchema"), null);
			LOG.info("/dbconsole/triggerDataSchema - END");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to get connection names.");
			response.put("stackTrace", e.getMessage());
			LOG.error("Exception  "+e.getMessage());
			LOG.info("/dbconsole/triggerDataSchema - END");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getAppGroups", method = RequestMethod.GET)
	public ResponseEntity<Object> getAppGroups(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/getAppGroups - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/getAppGroups - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			LOG.info("/dbconsole/getAppGroups - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

			// Get ProjectList
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
			if (projList != null && projList.size() > 0)
				projectId = projList.get(0).getIdProject();

			if (taskService.checkUserPermission(userToken, "R", "Tasks")) {
				List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId, projList);
				response.put("status", "success");
				response.put("message", "Successfully fetched app groups.");
				response.put("result", listAppGroupData);
				LOG.info("Successfully fetched app groups.");
				LOG.info("/dbconsole/getAppGroups - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Permission denied.");
				LOG.error("Permission denied.");
				LOG.info("/dbconsole/getAppGroups - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to get app groups.");
			LOG.error("Exception  "+e.getMessage());
			response.put("stackTrace", e.getMessage());
			LOG.info("/dbconsole/getAppGroups - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getAppGroupNames", method = RequestMethod.GET)
	public ResponseEntity<Object> getAppGroupNames(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/getAppGroupNames - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/getAppGroupNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			LOG.info("/dbconsole/getAppGroupNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

			// Get ProjectList
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
			if (projList != null && projList.size() > 0)
				projectId = projList.get(0).getIdProject();

			List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId, projList);

			List<String> appGroupList = new ArrayList<String>();
			if (listAppGroupData != null && listAppGroupData.size() > 0) {
				for (ListAppGroup listAppGroup : listAppGroupData) {
					String data = listAppGroup.getIdAppGroup() + "-" + listAppGroup.getName();
					appGroupList.add(data);
				}
			}
			response.put("status", "success");
			response.put("message", "Successfully fetched app group names.");
			response.put("result", appGroupList);
			LOG.info("Successfully fetched app group names.");
			LOG.info("/dbconsole/getAppGroupNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to get app group names.");
			response.put("stackTrace", e.getMessage());
			LOG.info("/dbconsole/getAppGroupNames - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/triggerAppGroup", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerAppGroup(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/triggerAppGroup - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				LOG.info("/dbconsole/triggerAppGroup - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("appGroupId")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}
			LOG.debug("requestBody "+requestBody);

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				Long appGroupId = Long.parseLong(requestBody.get("appGroupId"));
				String resultMessage = null;
				// Check if AppGroup is already in queued or started or in progress
				if (iTaskDAO.checkIfAppGroupJobInQueue(appGroupId)) {
					resultMessage = "AppGroup is already in Queue";
				} else if (iTaskDAO.checkIfAppGroupJobInProgress(appGroupId)) {

					resultMessage = "AppGroup is already in started / in progress";
					LOG.info(resultMessage);
				} else {
					String uniqueId = iTaskDAO.addAppGroupJobToQueue(appGroupId);
					if (uniqueId != null) {
						resultMessage = "AppGroup placed in queue successfully";
						LOG.info(resultMessage);
						HashMap<String, String> responseMap = new HashMap<String, String>();
						responseMap.put("uniqueId", uniqueId);
						response.put("result", responseMap);
					} else {
						resultMessage = "There was a problem, failed to place in queue";
						LOG.error(resultMessage);
					}
				}
				response.put("message", resultMessage);
				response.put("status", "success");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token is failed.");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to run trigger app group.");
			response.put("status", "failed");
			LOG.info("/dbconsole/triggerAppGroup - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("/dbconsole/triggerAppGroup - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/getApprovedValidationsForProject", method = RequestMethod.GET)
	public ResponseEntity<Object> getApprovedValidationsForProject(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/getApprovedValidationsForProject - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			LOG.error("Token is missing in headers.");
			response.put("message", "Token is missing in headers.");
			LOG.info("/dbconsole/getApprovedValidationsForProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			LOG.error("Token is expired.");
			response.put("message", "Token is expired.");
			LOG.info("/dbconsole/getApprovedValidationsForProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

			// Get ProjectList
			List<Project> projList = null;
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

				String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
				ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

				projList = loginService.getProjectListOfUser(userLdapGroups);
			} else {
				projList = projService.getAllProjectsOfAUser(userToken.getEmail());
			}

			List<DomainProject> dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);

			// Get validation list
			List<String> validationList = new ArrayList<String>();
			if (projList != null && projList.size() > 0) {
				for (Project proj : projList) {
					long projectId = proj.getIdProject();
					List<String> prj_validationList = iTaskDAO
							.getApprovedValidationNamesListForProject(Long.valueOf(dplist.get(0).getIdProject()));
					validationList.addAll(prj_validationList);
				}
			}

			List<String> outputList = new ArrayList<>();
			for (String list : validationList) {
				String str = list.toString().replace("[", "").replace("]", "");
				outputList.add(str);
			}
			response.put("status", "success");
			response.put("message", "Successfully fetched approved validation names.");
			response.put("result", outputList);
			LOG.info("Successfully fetched approved validation names.");
			LOG.info("/dbconsole/getApprovedValidationsForProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to fetch approved validation names.");
			response.put("stackTrace", e.getMessage());
			LOG.info("/dbconsole/getApprovedValidationsForProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@Transactional
	@RequestMapping(value = "/dbconsole/createAppGroup", method = RequestMethod.POST)
	public ResponseEntity<Object> createAppGroup(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/createAppGroup - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				LOG.info("dbconsole/createAppGroup - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("appIdList") || !requestBody.containsKey("appName")
					|| !requestBody.containsKey("description") || !requestBody.containsKey("isSchedulerEnabled")
					|| !requestBody.containsKey("projectId")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

			// Get ProjectList
			// List<Project> projList = null;
//			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
//			
//			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
//				
//				String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
//				ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
//				
//				projList = loginService.getProjectListOfUser(userLdapGroups);
//			} else {
//				projList = projService.getAllProjectsOfAUser(userToken.getEmail());
//			}
//			
			long projectId = Long.parseLong(requestBody.get("projectId"));
//			if(projList != null && projList.size() > 0)
//				projectId = projList.get(0).getIdProject();

			boolean isDuplicateName = iTaskDAO.isAppGroupNameDuplicated(requestBody.get("appName"));

			if (!isDuplicateName) {
				if (requestBody.get("appIdList") != null && !requestBody.get("appIdList").trim().isEmpty()) {
					Long idScheduler = null;
					String schedulerEnabled = requestBody.get("isSchedulerEnabled");
					if (schedulerEnabled != null && schedulerEnabled.equalsIgnoreCase("Y")) {
						schedulerEnabled = "Y";
						if (!requestBody.containsKey("idScheduler")) {
							LOG.error("Required parameters not found.");
							throw new Exception("Required parameters not found.");
						} else {
							idScheduler = Long.parseLong(requestBody.get("idScheduler"));
						}
					} else {
						schedulerEnabled = "N";
					}
					Long insertIntolistAppGroup = iTaskDAO.insertIntolistAppGroup(requestBody.get("appName"),
							requestBody.get("description"), schedulerEnabled, idScheduler, projectId,
							requestBody.get("appIdList"));
					if (insertIntolistAppGroup > 0) {
						// changes regarding Audit trail
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_APP_GROUP, formatter.format(new Date()), insertIntolistAppGroup,
								DatabuckConstants.ACTIVITY_TYPE_CREATED,requestBody.get("appName"));
						response.put("message", "AppGroup successfully saved");
						response.put("status", "success");
						status = HttpStatus.OK;
						LOG.info("AppGroup successfully saved");
					} else {
						response.put("message", "There was a problem");
						response.put("status", "failed");
						LOG.error("There was a problem");
						status = HttpStatus.OK;
					}
				} else {
					response.put("message", "No validation selected,AppGroup failed");
					response.put("status", "failed");
					LOG.error("No validation selected,AppGroup failed");
					status = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				LOG.info("AppGroup name already exists");
				response.put("message", "AppGroup name already exists");
				response.put("status", "failed");
				status = HttpStatus.OK;
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to create app group.");
			response.put("status", "failed");
			LOG.info("dbconsole/createAppGroup - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/createAppGroup - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/deleteAppGroup", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteAppGroup(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/deleteAppGroup - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.info("/dbconsole/deleteAppGroup - END");
				LOG.error("Please provide token.");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("appGroupId")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				ListAppGroup appGroup = iTaskDAO.getListAppGroupById(Long.parseLong(requestBody.get("appGroupId")));
				iTaskDAO.deleteAppGroupById(Long.parseLong(requestBody.get("appGroupId")));
				// changes regarding Audit trail
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_APP_GROUP, formatter.format(new Date()), Long.valueOf(requestBody.get("appGroupId")),
						DatabuckConstants.ACTIVITY_TYPE_DELETED,appGroup.getName());
				response.put("message", "App group deleted successfully.");
				LOG.info("App group deleted successfully.");
				response.put("status", "success");
				status = HttpStatus.OK;
			} else {
				LOG.error("Token failed...");
				response.put("message", "Token failed...");
				response.put("status", "failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to delete app group.");
			response.put("status", "failed");
			LOG.info("/dbconsole/deleteAppGroup - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("/dbconsole/deleteAppGroup - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/deleteAppGroupMapping", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteAppGroupMapping(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/deleteAppGroupMapping - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.info("/dbconsole/deleteAppGroupMapping - END");				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("idAppGroupMapping")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				iTaskDAO.deleteAppGroupMapping(Long.parseLong(requestBody.get("idAppGroupMapping")));
				response.put("message", "AppGroupMapping deleted successfully.");
				LOG.info("AppGroupMapping deleted successfully.");
				response.put("status", "success");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed...");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to delete AppGroupMapping.");
			response.put("status", "failed");
			LOG.info("/dbconsole/deleteAppGroupMapping - END");		
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("/dbconsole/deleteAppGroupMapping - END");		
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/editAppGroup", method = RequestMethod.POST)
	public ResponseEntity<Object> editAppGroup(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/editAppGroup - START");		
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.info("/dbconsole/editAppGroup - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("idAppGroup") || !requestBody.containsKey("name")
					|| !requestBody.containsKey("description") || !requestBody.containsKey("idAppList")
					|| !requestBody.containsKey("idScheduler") || !requestBody.containsKey("schedulerEnabled")
					|| !requestBody.containsKey("projectId")) {
				throw new Exception("Required parameters not found.");
			}

			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

			// Get ProjectList
//			List<Project> projList = null;
//			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
//			
//			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
//				
//				String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
//				ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
//				
//				projList = loginService.getProjectListOfUser(userLdapGroups);
//			} else {
//				projList = projService.getAllProjectsOfAUser(userToken.getEmail());
//			}
//			
			LOG.debug("requestBody "+requestBody);
			long projectId = Long.parseLong(requestBody.get("projectId"));
//			if(projList != null && projList.size() > 0)
//				projectId = projList.get(0).getIdProject();

			String schedulerEnabled = null;
			if (requestBody.get("schedulerEnabled") != null
					&& requestBody.get("schedulerEnabled").equalsIgnoreCase("Y")) {
				schedulerEnabled = "Y";
			} else {
				schedulerEnabled = "N";
			}
			String idAppList = requestBody.get("idAppList");
			if (idAppList != null && !idAppList.trim().isEmpty()) {
				Long idAppGroup = Long.parseLong(requestBody.get("idAppGroup"));
				Long idScheduler = Long.parseLong(requestBody.get("idScheduler"));
				ListAppGroup appGroup= iTaskDAO.getListAppGroupById(idAppGroup);
				boolean updateIntolistAppGroup = iTaskDAO.updateIntolistAppGroup(idAppGroup, requestBody.get("name"),
						requestBody.get("description"), schedulerEnabled, idScheduler, projectId, idAppList);
				if (updateIntolistAppGroup) {
					// changes regarding Audit trail
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_APP_GROUP, formatter.format(new Date()), idAppGroup,
							DatabuckConstants.ACTIVITY_TYPE_EDITED,appGroup.getName());
					response.put("message", "AppGroup edited successfully.");
					LOG.info("AppGroup edited successfully.");
					response.put("status", "success");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					LOG.error("There was a problem.");
					response.put("message", "There was a problem.");
					response.put("status", "failed");
					LOG.info("/dbconsole/editAppGroup - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			} else {
				LOG.error("No validation selected,AppGroup failed.");
				response.put("message", "No validation selected,AppGroup failed.");
				response.put("status", "failed");
				LOG.info("/dbconsole/editAppGroup - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to update app group.");
			response.put("status", "failed");
			LOG.info("/dbconsole/editAppGroup - END");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/dbconsole/getSchedulerList", method = RequestMethod.GET)
	public ResponseEntity<Object> getSchedulerList(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/getSchedulerList - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			LOG.error("Token is missing in headers.");
			response.put("message", "Token is missing in headers.");
			LOG.info("/dbconsole/getSchedulerList - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			LOG.error("Token is expired.");
			response.put("message", "Token is expired.");
			LOG.info("/dbconsole/getSchedulerList - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			// Get ProjectList
			List<Project> projList = null;
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

			if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

				String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
				ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

				projList = loginService.getProjectListOfUser(userLdapGroups);
			} else {
				projList = projService.getAllProjectsOfAUser(userToken.getEmail());
			}

			response.put("status", "success");
			response.put("message", "Successfully fetched approved validation names.");
			LOG.info("Successfully fetched approved validation names.");
			response.put("result", iTaskDAO.getSchedulers(projList.get(0).getIdProject(), projList));
			LOG.info("/dbconsole/getSchedulerList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			response.put("status", "failed");
			response.put("message", "Failed to fetch approved validation names.");
			response.put("stackTrace", e.getMessage());
			LOG.info("/dbconsole/getSchedulerList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	// -------------APIs for Scheduler added on 23rd Aug 22 by Amar ------------

	@RequestMapping(value = "/dbconsole/viewSchedules", method = RequestMethod.POST)
	public ResponseEntity<Object> viewSchedules(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/viewSchedules - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();
		String token = null;
		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.info("/dbconsole/viewSchedules - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		LOG.debug("params "+params);
		if (!params.containsKey("projectId") || !params.containsKey("domainId")) {
			responseMap.put("message", "Required parameters are missing in request parameters.");
			LOG.info("/dbconsole/viewSchedules - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		try {
			if (tokenValidator.isValid(token)) {
				long projectId = params.get("projectId");
				long domainId = params.get("domainId");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}

				List<ListSchedule> ListScheduleData = iTaskDAO.getSchedulersForProjectId(projectId, domainId);
				responseMap.put("status", "success");
				responseMap.put("result", ListScheduleData);
				responseMap.put("message", "Successfully fetched data.");
				LOG.info("Successfully fetched data.");
				httpStatus = HttpStatus.OK;
			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				LOG.error(message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception E) {
			E.printStackTrace();
			LOG.error("Exception  "+E.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/viewSchedules - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	@RequestMapping(value = "/dbconsole/editSchedule", method = RequestMethod.POST)
	public ResponseEntity<Object> editSchedule(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/editSchedule - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		Object user = session.getAttribute("user");

		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		String token = null;
		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			LOG.error("Exception  "+ex.getMessage());
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/editSchedule - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!params.containsKey("idSchedule")) {
			LOG.error("idSchedule is missing in request parameters.");
			responseMap.put("message", "idSchedule is missing in request parameters.");
			LOG.info("/dbconsole/editSchedule - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

		try {
			if (tokenValidator.isValid(token)) {
				long idSchedule = params.get("idSchedule");
				ListSchedule listScheduleData = iTaskDAO.getSchedulerById(idSchedule);

				responseMap.put("status", "success");
				responseMap.put("result", listScheduleData);
				LOG.info("Successfully fetched data.");
				responseMap.put("message", "Successfully fetched data.");
				httpStatus = HttpStatus.OK;
			} else {
				message = "Token expired.";
				LOG.error(message);
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.OK;
		}
		LOG.info("/dbconsole/editSchedule - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	@RequestMapping(value = "/dbconsole/editScheduleTask", method = RequestMethod.POST)
	public ResponseEntity<Object> editScheduleTask(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody String inputsJsonStr) {
		LOG.info("/dbconsole/editScheduleTask - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		JSONObject inputJson = new JSONObject(inputsJsonStr);
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/editScheduleTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		LOG.debug("inputsJsonStr "+inputsJsonStr);
		if (!inputsJsonStr.contains("idSchedule") || !inputsJsonStr.contains("name")
				|| !inputsJsonStr.contains("description") || !inputsJsonStr.contains("frequency")
				|| !inputsJsonStr.contains("scheduledDay") || !inputsJsonStr.contains("day")
				|| !inputsJsonStr.contains("ScheduleTimer") || !inputsJsonStr.contains("projectId")) {
			LOG.error("Parameters are missing in request parameters.");
			responseMap.put("message", "Parameters are missing in request parameters.");
			LOG.info("/dbconsole/editScheduleTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

		try {
			if (tokenValidator.isValid(token)) {

				long idSchedule = inputJson.getLong("idSchedule");
				String name = inputJson.getString("name");
				String description = inputJson.getString("description");
				String frequency = inputJson.getString("frequency");
				String scheduledDay = inputJson.getString("scheduledDay");
				String day = inputJson.getString("day");
				String ScheduleTimer = inputJson.getString("ScheduleTimer");
				long projectId = inputJson.getLong("projectId");

				int insertIntolistSchedule = iTaskDAO.updateListSchedule(idSchedule, name, description, frequency,
						scheduledDay, day, ScheduleTimer);
				LOG.debug("insertIntolistSchedule=" + insertIntolistSchedule);

				if (insertIntolistSchedule > 0) {
					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_SCHEDULER, formatter.format(new Date()), (long) idSchedule,
							DatabuckConstants.ACTIVITY_TYPE_EDITED,name);
					responseMap.put("status", "success");
					responseMap.put("message", "Runtask Schedule edited successfully.");
					LOG.info("Runtask Schedule edited successfully.");
					httpStatus = HttpStatus.OK;
				} else {
					responseMap.put("status", "failed");
					responseMap.put("message", "There is a problem");
					LOG.error("There is a problem");
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				LOG.error(message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to add data.");
			httpStatus = HttpStatus.OK;
		}
		LOG.info("/dbconsole/editScheduleTask - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	@RequestMapping(value = "/dbconsole/deleteSchedule", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteSchedule(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/deleteSchedule - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		Object user = session.getAttribute("user");

		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			LOG.error("Token is missing in headers.");
			responseMap.put("message", "Token is missing in headers");
			LOG.info("/dbconsole/deleteSchedule - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!params.containsKey("idSchedule")) {
			LOG.error("idSchedule is missing in request parameters.");
			responseMap.put("message", "idSchedule is missing in request parameters.");
			LOG.info("/dbconsole/deleteSchedule - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		if (tokenValidator.isValid(token)) {

			try {
				long idSchedule = params.get("idSchedule");
				boolean enableScheduleStatus = iTaskService.enableScheduleCheck(idSchedule);
				ListSchedule scheduler = iTaskDAO.getSchedulerById(idSchedule);
				if (enableScheduleStatus) {
					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_SCHEDULER, formatter.format(new Date()), (long) idSchedule,
							DatabuckConstants.ACTIVITY_TYPE_DELETED,scheduler.getName());
					iTaskDAO.deleteFromListSchedule(idSchedule);
					responseMap.put("status", "success");
					responseMap.put("message", "Successfully deleted the Schedule.");
					LOG.info("Successfully deleted the Schedule.");
					httpStatus = HttpStatus.OK;
				} else {
					responseMap.put("status", "failed");
					responseMap.put("message", "The schedule is already enabled in Trigger or App group");
					LOG.info("The schedule is already enabled in Trigger or App group");
					httpStatus = HttpStatus.OK;
				}
			} catch (Exception E) {
				E.printStackTrace();
				LOG.error("Exception  "+E.getMessage());
				responseMap.put("status", "failed");
				responseMap.put("message", "Failed to delete the Schedule.");
				httpStatus = HttpStatus.OK;
			}

		} else {
			message = "Token expired.";
			responseMap.put("status", "failed");
			responseMap.put("message", message);
			LOG.error(message);
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/deleteSchedule - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	@RequestMapping(value = "/dbconsole/scheduleTask", method = RequestMethod.POST)
	public ResponseEntity<Object> scheduleTask(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody String inputsJsonStr) {
		LOG.info("/dbconsole/scheduleTask - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		JSONObject inputJson = new JSONObject(inputsJsonStr);
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		responseMap.put("status", "failed");
		try {
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.info("/dbconsole/scheduleTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!inputsJsonStr.contains("name") || !inputsJsonStr.contains("description")
				|| !inputsJsonStr.contains("frequency") || !inputsJsonStr.contains("scheduledDay")
				|| !inputsJsonStr.contains("day") || !inputsJsonStr.contains("ScheduleTimer")
				|| !inputsJsonStr.contains("projectId") || !inputsJsonStr.contains("domainId")) {
			responseMap.put("message", "Parameters are missing in request parameters.");
			LOG.info("/dbconsole/scheduleTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		LOG.debug("inputJson "+inputJson);
		try {
			if (tokenValidator.isValid(token)) {

				String name = inputJson.getString("name");
				String description = inputJson.getString("description");
				String frequency = inputJson.getString("frequency");
				String scheduledDay = inputJson.getString("scheduledDay");
				String day = inputJson.getString("day");
				String ScheduleTimer = inputJson.getString("ScheduleTimer");
				long projectId = inputJson.getLong("projectId");
				long domainId = inputJson.getLong("domainId");

				int idSchedule = iTaskDAO.insertIntolistSchedule(name, description, frequency, scheduledDay,
						day, ScheduleTimer, projectId, domainId);
				LOG.debug("idSchedule=" + idSchedule);

				if (idSchedule > 0) {
					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_SCHEDULER, formatter.format(new Date()), (long) idSchedule,
							DatabuckConstants.ACTIVITY_TYPE_CREATED,name);
					responseMap.put("status", "success");
					responseMap.put("message", "Data Inserted Successfully.");
					LOG.info("Data Inserted Successfully.");
					httpStatus = HttpStatus.OK;
				} else {
					responseMap.put("status", "failed");
					responseMap.put("message", "There is a problem");
					LOG.error("There is a problem");
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token expired.";
				LOG.error("Token is expired.");
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to add data.");
			httpStatus = HttpStatus.OK;
		}
		LOG.info("/dbconsole/scheduleTask - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	// ----------- APIs for Trigger added on 23rd Aug 22 by Amar
	// ---------------------
	
	@RequestMapping(value = "/dbconsole/viewTriggers", method = RequestMethod.POST)
	public ResponseEntity<Object> viewTriggers(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/viewTriggers - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = null;
		Long domainId = 1l;

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
		}
		if (token == null || token.isEmpty()) {
			LOG.error("Token is missing in headers.");
			responseMap.put("message", "Token is missing in headers");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		LOG.debug("params "+params);
		if (!params.containsKey("projectId")) {
			LOG.error("projectId is missing in request parameters.");
			responseMap.put("message", "projectId is missing in request parameters.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		if (params.containsKey("domainId")) {
			domainId = params.get("domainId");
		}
		try {
			if (tokenValidator.isValid(token)) {
				long projectId = params.get("projectId");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}

				List<ListTrigger> validationTriggerData = iTaskDAO.getValidationTriggersListProjectName(projectId,
						projList);
				List<ListTrigger> schemaTriggerData = iTaskDAO.getSchemaTriggersListWithDomain(projectId, domainId);
				model.put("validationTriggerData", validationTriggerData);
				model.put("schemaTriggerData", schemaTriggerData);
				responseMap.put("status", "success");
				responseMap.put("result", model);
				responseMap.put("message", "Successfully fetched data.");
				LOG.info("Successfully fetched data.");
				httpStatus = HttpStatus.OK;
			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				LOG.error(message);
				responseMap.put("message", message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception E) {
			E.printStackTrace();
			LOG.error("Exception  "+E.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.OK;
		}
		LOG.info("/dbconsole/viewTriggers - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}

	
	@RequestMapping(value = "/dbconsole/deleteTrigger", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteTrigger(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/deleteTrigger - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		Object user = session.getAttribute("user");

		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/deleteTrigger - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		LOG.debug("params "+params);
		if (!params.containsKey("idTrigger")) {
			LOG.error("idTrigger is missing in request parameters.");
			responseMap.put("message", "idTrigger is missing in request parameters.");
			LOG.info("/dbconsole/deleteTrigger - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}

		if (tokenValidator.isValid(token)) {

			try {
				long idTrigger = params.get("idTrigger");
				iTaskDAO.deleteFromScheduledTasks(idTrigger);
				responseMap.put("status", "success");
				responseMap.put("message", "Successfully deleted the Trigger.");
				LOG.info("Successfully deleted the Trigger.");
				httpStatus = HttpStatus.OK;
			} catch (Exception E) {
				E.printStackTrace();
				response.put("status", "failed");
				response.put("message", "Failed to delete the Trigger.");
				LOG.error("Exception  "+E.getMessage());
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} else {
			message = "Token expired.";
			responseMap.put("status", "failed");
			responseMap.put("message", message);
			LOG.error(message);
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/deleteTrigger - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}
	

	@RequestMapping(value = "/dbconsole/triggerTask", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerTask(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/triggerTask - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();
		String token = null;

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
		}
		if (token == null || token.isEmpty()) {
			LOG.error("Token is missing in headers.");
			responseMap.put("message", "Token is missing in headers");
			LOG.info("/dbconsole/triggerTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!params.containsKey("projectId")) {
			LOG.error( "projectId is missing in request parameters.");
			responseMap.put("message", "projectId is missing in request parameters.");
			LOG.info("/dbconsole/triggerTask - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		LOG.debug("params "+params);
		try {
			if (tokenValidator.isValid(token)) {
				long projectId = params.get("projectId");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}

				Map<Long, String> listScheduleData = iTaskDAO.getListScheduleData(projectId, projList);
				List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId, projList);
				List<ListDataSchema> listSchemaData = listdatasourcedao.getListDataSchema(projectId, projList, "", "");

				// model.put("listScheduleData", listScheduleData);
				model.put("listApplicationsdata", listApplicationsdata);
				model.put("listSchemaData", listSchemaData);
				responseMap.put("status", "success");
				responseMap.put("result", model);
				responseMap.put("message", "Successfully fetched data.");
				LOG.info("Successfully fetched data.");
				httpStatus = HttpStatus.OK;
			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				LOG.error(message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception E) {
			E.printStackTrace();
			LOG.error("Exception  "+E.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/triggerTask - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}
	

	@RequestMapping(value = "/dbconsole/triggerTaskSchedule", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerTaskSchedule(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody String inputsJsonStr) {
		LOG.info("/dbconsole/triggerTaskSchedule - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();

		JSONObject inputJson = new JSONObject(inputsJsonStr);
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String token = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.info("/dbconsole/triggerTaskSchedule - END");
			LOG.error("Token is missing in headers.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!inputsJsonStr.contains("idApp") || !inputsJsonStr.contains("idDataSchema")
				|| !inputsJsonStr.contains("idScheduler") || !inputsJsonStr.contains("projectId")) {
			responseMap.put("message", "Parameters are missing in request parameters.");
			LOG.error("Parameters are missing in request parameters.");
			LOG.info("/dbconsole/triggerTaskSchedule - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		LOG.debug("inputJson "+inputJson);
		try {
			if (tokenValidator.isValid(token)) {

				String idApp = inputJson.getString("idApp");
				String idDataSchema = inputJson.getString("idDataSchema");
				long idScheduler = inputJson.getLong("idScheduler");
				long projectId = inputJson.getLong("projectId");

				int jobsCount = 0;
				int insertintoscheduledtasks = 0;
				int duplicatescheduledtasks = 0;

				if (idApp != null && !idApp.trim().isEmpty()) {
					String[] idAppList = idApp.trim().split(",");

					if (idAppList != null && idAppList.length > 0) {
						jobsCount = idAppList.length;

						for (String s_idApp : idAppList) {
							long l_idApp = Long.parseLong(s_idApp);
							int insertCount = iTaskDAO.insertintoscheduledtasks(l_idApp, null, idScheduler, projectId);
							insertintoscheduledtasks += insertCount;
						}
					}
				} else if (idDataSchema != null && !idDataSchema.trim().isEmpty()) {
					String[] idDataSchemaList = idDataSchema.trim().split(",");

					if (idDataSchemaList != null && idDataSchemaList.length > 0) {
						jobsCount = idDataSchemaList.length;

						for (String s_idDataSchema : idDataSchemaList) {
							long l_idDataSchema = Long.parseLong(s_idDataSchema);
							int count = iTaskDAO.getCountscheduledtasks(l_idDataSchema, projectId);
							if (count <= 0) {
								int insertCount = iTaskDAO.insertintoscheduledtasks(null, l_idDataSchema, idScheduler,
										projectId);
								insertintoscheduledtasks += insertCount;
							} else {
								duplicatescheduledtasks++;
							}

						}
					}
				}

				if (duplicatescheduledtasks == jobsCount) {
					responseMap.put("status", "failed");
					responseMap.put("message", "Task already scheduled.");
					LOG.info("Task already scheduled.");
					httpStatus = HttpStatus.OK;

				} else if ((insertintoscheduledtasks + duplicatescheduledtasks) == jobsCount) {
					responseMap.put("status", "success");
					responseMap.put("message", "Data Inserted Successfully.");
					LOG.info("Data Inserted Successfully.");
					httpStatus = HttpStatus.OK;
				} else {
					responseMap.put("status", "failed");
					responseMap.put("message", "There is a problem");
					LOG.error("There is a problem");
					httpStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				LOG.error(message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to add data.");
			httpStatus = HttpStatus.OK;
		}
		LOG.info("/dbconsole/triggerTaskSchedule - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}
	

	// ------------------Csv Controller--------------------------------------

	@RequestMapping(value = "/dbconsole/getScheduleCSV", method = RequestMethod.POST)
	public void getScheduleCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> params,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getScheduleCSV - START");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// "success".equalsIgnoreCase(csvService.validateUserToken(token))
				LOG.debug("params "+params);
				if (true) {
					if (params.containsKey("projectId") && params.containsKey("domainId")) {
						List<Project> projList = new ArrayList<>();
						long projectId = params.get("projectId");
						long domainId = params.get("domainId");
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

						// Get ProjectList
						String activeDirectoryFlag = appDbConnectionProperties
								.getProperty("isActiveDirectoryAuthentication");

						if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

							String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
							ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

							projList = loginService.getProjectListOfUser(userLdapGroups);
						} else {
							projList = projService.getAllProjectsOfAUser(userToken.getEmail());
						}
						List<ListSchedule> ListScheduleData = iTaskDAO.getSchedulersForProjectId(projectId, domainId);
						if (ListScheduleData != null) {

							httpResponse.setContentType("text/csv");
							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = { "name", "description", "frequency", "scheduleDay", "time",
									"projectName" };
							String[] header = { "Scheduler Name", "Description", "Frequency", "Schedule Day", "Time",
									"Project Name" };
							csvWriter.writeHeader(header);
							for (ListSchedule scheduler : ListScheduleData) {
								csvWriter.write(scheduler, fields);
							}
							csvWriter.close();
						} else {
							LOG.error("Records not found.");
							throw new Exception("Records not found.");
						}

					} else {
						LOG.error("required parameters is missing in request parameters");
						throw new Exception("required parameters is missing in request parameters");
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
			LOG.error("Exception  "+e.getMessage());
		}
		LOG.info("/dbconsole/getScheduleCSV - END");
	}
	

	@RequestMapping(value = "/dbconsole/getValidationTriggerDataCSV", method = RequestMethod.POST)
	public void validationTriggerDataCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> params,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getValidationTriggerDataCSV - START");

		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// "success".equalsIgnoreCase(csvService.validateUserToken(token))
				LOG.debug("params "+params);
				if (true) {
					if (params.containsKey("projectId")) {
						// UserToken userToken =
						// dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						// List<Project> projList =
						// projService.getAllProjectsOfAUser(userToken.getEmail());
						List<Project> projList = null;
						long projectId = params.get("projectId");
						List<ListTrigger> validationTriggerData = iTaskDAO.getValidationTriggersList(projectId,
								projList);
						if (validationTriggerData != null) {

							httpResponse.setContentType("text/csv");
							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = { "validationCheck", "scheduleName", "projectName" };
							String[] header = { "Validation Name", "Scheduler Name", "Project Name" };
							csvWriter.writeHeader(header);
							for (ListTrigger validationTrigger : validationTriggerData) {
								csvWriter.write(validationTrigger, fields);
							}
							csvWriter.close();
						} else {
							LOG.error("Records not found.");
							throw new Exception("Records not found.");
						}

					} else {
						LOG.error("projectId is missing in request parameters");
						throw new Exception("projectId is missing in request parameters");
						
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
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("/dbconsole/getValidationTriggerDataCSV - END");
	}
	

	@RequestMapping(value = "/dbconsole/getSchemaTriggersDataCSV", method = RequestMethod.POST)
	public void SchemaTriggersDataCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Long> params,
			HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/getSchemaTriggersDataCSV - START");

		try {
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				// "success".equalsIgnoreCase(csvService.validateUserToken(token))
				LOG.debug("params "+params);
				if (true) {
					if (params.containsKey("projectId")) {
						// UserToken userToken =
						// dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						// List<Project> projList =
						// projService.getAllProjectsOfAUser(userToken.getEmail());
						List<Project> projList = null;
						long projectId = params.get("projectId");
						List<ListTrigger> schemaTriggerData = iTaskDAO.getSchemaTriggersList(projectId, projList);
						if (schemaTriggerData != null) {

							httpResponse.setContentType("text/csv");
							String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
							String headerKey = "Content-Disposition";
							String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
							httpResponse.setHeader(headerKey, headerValue);
							ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
									CsvPreference.STANDARD_PREFERENCE);
							String[] fields = { "schemaName", "scheduleName", "projectName" };
							String[] header = { "Schema Name", "Scheduler Name", "Project Name" };
							csvWriter.writeHeader(header);
							for (ListTrigger schemaTrigger : schemaTriggerData) {
								csvWriter.write(schemaTrigger, fields);
							}
							csvWriter.close();
						} else {
							LOG.error("Records not found.");
							throw new Exception("Records not found.");
						}

					} else {
						LOG.error("projectId is missing in request parameters");
						throw new Exception("projectId is missing in request parameters");
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
			LOG.error("Exception  "+e.getMessage());
		}
		LOG.info("/dbconsole/getSchemaTriggersDataCSV - END");
	}
	
	// --------------------------------------------------
	// --------------------------------------------------------

	@RequestMapping(value = "/dbconsole/getAppGroupById", method = RequestMethod.POST)
	public ResponseEntity<Object> getAppGroupById(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("/dbconsole/getAppGroupById - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.info("/dbconsole/getAppGroupById - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			
			LOG.debug("requestBody "+requestBody);

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("appGroupId")) {
				LOG.error("Required parameters not found.");
				throw new Exception("Required parameters not found.");
			}
			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
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
				if (projList != null && projList.size() > 0)
					projectId = projList.get(0).getIdProject();

				if (taskService.checkUserPermission(userToken, "C", "Tasks")) {
					List<AppGroupMapping> appGroupMappings = iTaskDAO
							.getApplicationMappingForGroup(Long.parseLong(requestBody.get("appGroupId")));
					List<ListSchedule> scheduleList = iTaskDAO.getSchedulers(projectId, projList);
					String selectedAppIds = "";
					for (AppGroupMapping appGroupMapping : appGroupMappings) {
						selectedAppIds = selectedAppIds + appGroupMapping.getAppId() + ",";
					}
					if (selectedAppIds != null && selectedAppIds.length() > 0) {
						selectedAppIds = selectedAppIds.substring(0, selectedAppIds.length() - 1);
					}

					ListAppGroup listAppGroup = iTaskDAO
							.getListAppGroupById(Long.parseLong(requestBody.get("appGroupId")));
					Map<String, Object> responseMap = new HashMap<>();
					responseMap.put("listAppGroup", listAppGroup);
					responseMap.put("appGroupMappings", appGroupMappings);
					responseMap.put("selectedAppIds", selectedAppIds);
					responseMap.put("scheduleList", scheduleList);
					response.put("status", "success");
					response.put("message", "Successfully fetched app group info.");
					response.put("result", responseMap);
					LOG.info("Successfully fetched app group info.");
					LOG.info("/dbconsole/getAppGroupById - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					LOG.error("Permission failed...");
					response.put("message", "Permission failed...");
					response.put("status", "failed");
					status = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				LOG.error("Token failed...");
				response.put("message", "Token failed...");
				response.put("status", "failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			response.put("message", "Failed to fetch app group info.");
			response.put("status", "failed");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("/dbconsole/getAppGroupById - END");
		return new ResponseEntity<Object>(response, status);
	}
	

	
	@RequestMapping(value = "/dbconsole/accessLog", method = RequestMethod.GET)
	public ResponseEntity<Object> viewAccessLog(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/accessLog - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				LOG.info("/dbconsole/accessLog - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			SqlRowSet loggingActivityData = IUserdAO.getlogging_activity();
			response.put("result", retrieveRecord(loggingActivityData));
			response.put("status", "success");
			response.put("message", "Access logs fetch successfully !!");
			LOG.info("Access logs fetch successfully !!");
			LOG.info("/dbconsole/accessLog - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "Error while fetching access logs");
			LOG.error("Exception  "+e.getMessage());
			LOG.info("/dbconsole/accessLog - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	

	
	public ArrayList<Map<String, Object>> retrieveRecord(SqlRowSet rset) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int colCount = rset.getMetaData().getColumnCount();
		while (rset.next()) {
			Map<String, Object> columnValueMap = new HashMap<String, Object>();
			for (int i = 1; i <= colCount; i++) {
				columnValueMap.put(rset.getMetaData().getColumnLabel(i), rset.getObject(i));
			}
			list.add(columnValueMap);
		}
		return list;
	}
	

	
	@RequestMapping(value = "/dbconsole/clearAccesslog", method = RequestMethod.GET)
	public ResponseEntity<Object> clearAllclearAccesslog(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/clearAccesslog - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			IUserdAO.clearAccessLog();
			response.put("status", "success");
			response.put("message", "Access logs cleared successfully  !!");
			LOG.info("Access logs cleared successfully  !!");
			LOG.info("/dbconsole/clearAccesslog - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "Error while clearing access logs");
			LOG.error("Exception  "+e.getMessage());
			LOG.info("/dbconsole/clearAccesslog - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

	}
	

	
	@RequestMapping(value = "/dbconsole/scheduledSchemasCSV", method = RequestMethod.POST)
	public ResponseEntity<Object> scheduledSchemasCSV(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/scheduledSchemasCSV - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		JSONObject response = new JSONObject();
		String token = null;

		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/scheduledSchemasCSV - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!params.containsKey("projectId")) {
			responseMap.put("message", "projectId is missing in request parameters.");
			LOG.error("projectId is missing in request parameters.");
			LOG.info("/dbconsole/scheduledSchemasCSV - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		
		LOG.debug("params "+params);
		try {
			if (tokenValidator.isValid(token)) {
				long projectId = params.get("projectId");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}

				// List<ListTrigger> validationTriggerData =
				// iTaskDAO.getValidationTriggersList(projectId,projList);
				List<ListTrigger> schemaTriggerData = iTaskDAO.getSchemaTriggersList(projectId, projList);
				if (!schemaTriggerData.isEmpty()) {
					httpResponse.setContentType("text/csv");
					String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
					httpResponse.setHeader(headerKey, headerValue);
					ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
							CsvPreference.STANDARD_PREFERENCE);
					String[] fields = { "idTrigger", "schemaName", "scheduleName", "projectName" };
					String[] header = { "Trigger Id", "Schema Name", "Schedule", "Project Name" };
					csvWriter.writeHeader(header);
					for (ListTrigger listTrigger : schemaTriggerData) {
						csvWriter.write(listTrigger, fields);
					}
					csvWriter.close();
					responseMap.put("status", "success");
					responseMap.put("message", "File sent");
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			} else {
				message = "Token expired.";
				LOG.error("Token is expired.");
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception E) {
			E.printStackTrace();
			responseMap.put("status", "failed");
			LOG.error("Exception  "+E.getMessage());
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/scheduledSchemasCSV - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}
	
	

	
	@RequestMapping(value = "/dbconsole/triggerTaskSchema", method = RequestMethod.POST)
	public ResponseEntity<Object> triggerTaskSchema(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("/dbconsole/triggerTaskSchema - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<>();
		HttpStatus httpStatus = HttpStatus.OK;
		String message = "";
		String status = "failed";
		String approvalRequired = "";
		JSONObject response = new JSONObject();
		String token = null;

		responseMap.put("status", "failed");
		try {
			LOG.debug("token   " + headers.get("token").get(0) );
			token = headers.get("token").get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/triggerTaskSchema - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}

		if (!params.containsKey("projectId") || !params.containsKey("domainId")) {
			responseMap.put("message", "Required parameters are missing in request parameters.");
			LOG.error("Required parameters are missing in request parameters.");
			LOG.info("/dbconsole/triggerTaskSchema - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		LOG.debug("params "+params);
		try {
			if (tokenValidator.isValid(token)) {
				long projectId = params.get("projectId");
				long domainId = params.get("domainId");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));

				// Get ProjectList
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}

				Map<Long, String> listScheduleData = iTaskDAO.getListScheduleDatawithDomain(projectId, projList,
						domainId);

				model.put("listScheduleData", listScheduleData);
				responseMap.put("status", "success");
				responseMap.put("result", model);
				responseMap.put("message", "Successfully fetched data.");
				LOG.info("Successfully fetched data.");
				httpStatus = HttpStatus.OK;
			} else {
				message = "Token expired.";
				responseMap.put("status", "failed");
				responseMap.put("message", message);
				LOG.error("Token is expired.");
				httpStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception E) {
			E.printStackTrace();
			LOG.error("Exception  "+E.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			httpStatus = HttpStatus.EXPECTATION_FAILED;
		}
		LOG.info("/dbconsole/triggerTaskSchema - END");
		return new ResponseEntity<Object>(responseMap, httpStatus);
	}
}
