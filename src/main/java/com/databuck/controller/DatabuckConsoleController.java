package com.databuck.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
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
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DashboardCheckComponent;
import com.databuck.bean.DashboardColorGrade;
import com.databuck.bean.DashboardConnection;
import com.databuck.bean.DashboardConnectionValidion;
import com.databuck.bean.DbkFMConnectionSummary;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMSummaryDetails;
import com.databuck.bean.DomainProject;
import com.databuck.bean.EssentialCheckRuleSummaryReport;
import com.databuck.bean.EssentialCheckSummaryReport;
import com.databuck.bean.Project;
import com.databuck.bean.ReportUIComponentCheckSummary;
import com.databuck.bean.ReportUIConnCheckSummary;
import com.databuck.bean.ReportUIDQIIndexHistory;
import com.databuck.bean.ReportUIDashboardSummary;
import com.databuck.bean.ReportUIDatasourceSummary;
import com.databuck.bean.ReportUIFailedAsset;
import com.databuck.bean.ReportUIFailedFilesSummary;
import com.databuck.bean.ReportUIOverallDQIIndex;
import com.databuck.bean.ReportUIPassTrendSummary;
import com.databuck.bean.ReportUIPerformanceSummary;
import com.databuck.bean.ReportUIProjectCoverage;
import com.databuck.bean.ReportUITableSummary;
import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IProjectService;
import com.databuck.service.RBACController;
import com.databuck.util.UserLDAPGroupHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.databuck.dao.DBKFileMonitoringDao;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckConsoleController {

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private IValidationCheckDAO validationCheckDao;

	@Autowired
	private IProjectDAO iProjectDAO;

	@Autowired
	private Properties appDbConnectionProperties;


	@Autowired
	private ChecksCSVService csvService;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@RequestMapping(value = "/dbconsole/getApplicationModuleName", method = RequestMethod.GET)
	public ResponseEntity<Object> getApplicationModuleName(@RequestHeader HttpHeaders headers,
			HttpServletRequest request, HttpServletResponse response) {

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
				System.out.println(token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					// Get DB Connection Properties Details
					String propertyModule = appDbConnectionProperties.getProperty("databuck.module");
					if (propertyModule == null || propertyModule.trim().isEmpty())
						propertyModule = "all";
					status = "success";
					message = "";
					json.put("propertyModule", propertyModule);
				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getAllDomainProjectsForUser", method = RequestMethod.GET)
	public @ResponseBody List<DomainProject> getAllDomainProjectsForUser(HttpSession session,
			HttpServletRequest request) {
		System.out.println("\n====> API: getAllDomainProjectsForUser Start <====");

		List<DomainProject> projectList = null;
		try {
			String email = (String) session.getAttribute("email");
			System.out.println("\n====> Session User : " + email);
			projectList = iProjectDAO.getAllDomainProjectsForUser(email);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> API: getAllDomainProjectsForUser End  <====");

		return projectList;
	}

	@RequestMapping(value = "/dbconsole/getConnectionsForDashboard", method = RequestMethod.GET)
	public @ResponseBody List<DashboardConnection> getConnectionsForDashboard(HttpServletRequest request,
			@RequestParam long domainId, @RequestParam long projectId) {
		System.out.println("\n====> API: getConnectionsForDashboard Start <====");

		List<DashboardConnection> dashboardConnectionList = null;
		try {
			dashboardConnectionList = dashboardConsoleDao.getConnectionsForDashboard(domainId, projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> API: getConnectionsForDashboard End  <====");

		return dashboardConnectionList;
	}

	@RequestMapping(value = "/dbconsole/updateConnectionsForDashboard", method = RequestMethod.POST)
	public @ResponseBody String updateConnectionsForDashboard(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestBody List<DashboardConnection> dashboardConnectionList) {
		System.out.println("\n====> API: updateConnectionsForDashboard Start <====");
		String status = "failed";
		try {
			status = dashboardConsoleDao.updateConnectionsForDashboard(dashboardConnectionList, domainId, projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", status);

		System.out.println("\n====> API: updateConnectionsForDashboard End <====");

		return jsonObject.toString();
	}

	@CrossOrigin(origins = { "http://45.14.113.40:4200", "http://45.14.113.40:8090", "http://localhost:4200" })
	@RequestMapping(value = "/dbconsole/getColorGrading", method = RequestMethod.GET)
	public @ResponseBody List<DashboardColorGrade> getColorGrading(HttpServletRequest request,
			@RequestParam long domainId, @RequestParam long projectId) {
		System.out.println("\n====> API: getColorGrading Start <====");

		List<DashboardColorGrade> dashboardColorGradeList = null;
		try {
			dashboardColorGradeList = dashboardConsoleDao.getColorGrading(domainId, projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> API: getColorGrading End  <====");

		return dashboardColorGradeList;
	}

	@RequestMapping(value = "/dbconsole/updateColorGrading", method = RequestMethod.POST)
	public @ResponseBody String updateColorGrading(HttpServletRequest request,
			@RequestBody List<DashboardColorGrade> dashboardColorGradeList) {
		System.out.println("\n====> API: updateColorGrading Start <====");
		String status = "failed";
		try {
			if (dashboardColorGradeList != null && dashboardColorGradeList.size() > 0) {
				status = dashboardConsoleDao.updateColorGrading(dashboardColorGradeList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", status);

		System.out.println("\n====> API: updateColorGrading End <====");
		return jsonObject.toString();
	}

	@RequestMapping(value = "/dbconsole/getConnectionValidationMap", method = RequestMethod.GET)
	public @ResponseBody List<DashboardConnectionValidion> getConnectionValidationMap(HttpServletRequest request,
			@RequestParam long domainId, @RequestParam long projectId, @RequestParam long connectionId) {
		System.out.println("\n====> API: getConnectionValidationMap Start <====");

		List<DashboardConnectionValidion> dashboardConnectionValidionList = null;
		try {
			dashboardConnectionValidionList = dashboardConsoleDao.getConnectionValidtionMap(domainId, projectId,
					connectionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n====> API: getConnectionValidationMap End <====");
		return dashboardConnectionValidionList;
	}

	@RequestMapping(value = "/dbconsole/updateConnectionValidationMap", method = RequestMethod.POST)
	public @ResponseBody String updateConnectionValidationMap(HttpServletRequest request,
			@RequestBody List<DashboardConnectionValidion> dashboardConnectionValidionList) {

		System.out.println("\n====> API: updateConnectionValidationMap Start <====");
		String status = "failed";

		try {
			status = dashboardConsoleDao.updateConnectionValidtionMap(dashboardConnectionValidionList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", status);

		System.out.println("\n====> API: updateConnectionValidationMap End <====");
		return jsonObject.toString();
	}

	@RequestMapping(value = "/dbconsole/getAllCheckComponents", method = RequestMethod.GET)
	public @ResponseBody List<DashboardCheckComponent> getCheckComponentList(HttpServletRequest request) {
		System.out.println("\n====> API: getAllCheckComponents Start <====");

		List<DashboardCheckComponent> dashboardCheckComponentList = null;
		try {
			dashboardCheckComponentList = dashboardConsoleDao.getCheckComponentList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n====> API: getAllCheckComponents End <====");
		return dashboardCheckComponentList;
	}

	@RequestMapping(value = "/dashConfiguration")
	public ModelAndView dashConfiguration(ModelAndView model, HttpSession session) {
		System.out.println("\n========> dashConfiguration <========");
		try {
			Object user = session.getAttribute("user");
			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage");
			}
			String averReportUILink = appDbConnectionProperties.getProperty("aver.report.link");
			String rolename = (String) session.getAttribute("Role");

			System.out.println("\n====>Rolename : " + rolename);

			model.setViewName("dashConfiguration");
			model.addObject("rolename", rolename.toUpperCase());
			model.addObject("averReportUILink", averReportUILink);
			model.addObject("currentSection", "Dash Configuration");
			model.addObject("currentLink", "definition");
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/dbconsole/generateToken", method = RequestMethod.GET)
	public @ResponseBody String generateUserToken(HttpServletRequest request, HttpSession session)
			throws UnsupportedEncodingException {
		System.out.println("\n====> API: generateUserToken Start <====");
		String token = null;
		try {
			// Get the user details from session
			Long idUser = (Long) session.getAttribute("idUser");
			String userName = (String) session.getAttribute("firstName");
			Long idRole = (Long) session.getAttribute("idRole");
			String rolename = (String) session.getAttribute("Role");
			String email = (String) session.getAttribute("email");
			String userLDAPGroups = "";
			
			System.out.println("\n====> email: " + email);
			
			String activeDirectoryUser = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryUser != null && activeDirectoryUser.trim().equalsIgnoreCase("Y")) {
				activeDirectoryUser = "Y";
				
				// Get userLdapGroups
				userLDAPGroups = UserLDAPGroupHolder.getLDAPGroupsForUser(email);
			} else {
				activeDirectoryUser = "N";
			}

			System.out.println("\n====> userLDAPGroups: " + userLDAPGroups);

			// Check if active token available for this user
			token = dashboardConsoleDao.checkForExistingUserToken(idUser, email, activeDirectoryUser);

			boolean validTokenFound = false;

			if (token != null && !token.isEmpty()) {
				// Check if token is still active
				String tokenStatus = getStatusOfUserToken(token);

				// If the token is ACTIVE, Fetch the project list
				if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
					validTokenFound = true;
					System.out.println("\n===> Active token already exists, hence returning the same !!");
				}
			}

			if (!validTokenFound) {
				System.out.println("\n===> Active token not found, hence generating new token !!");

				// Get Login time
				Date loginTime = new Date(System.currentTimeMillis());

				// Expiry time will be login time + 30 mins
				Date expiryTime = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

				// Generate uniqueId
				token = UUID.randomUUID().toString();

				// Prepare User token object
				UserToken userToken = new UserToken(idUser, userName, idRole, rolename, email, loginTime, expiryTime,
						token, "ACTIVE", activeDirectoryUser, userLDAPGroups);

				// Save the User token
				dashboardConsoleDao.insertUserToken(userToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n====> API: generateUserToken End <====");
		System.out.println("token: " + token);

		String encryptedToken = encryptToken("token=" + token);
		encryptedToken = URLEncoder.encode(encryptedToken, "UTF-8");
		System.out.println("encryptedToken: " + encryptedToken);
		return encryptedToken;
	}

	private String encryptToken(String token) {
		String encryptedToken = "";
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
			encryptedToken = encryptor.encrypt(token);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedToken;
	}

	@RequestMapping(value = "/dbconsole/getUserDetailsOfToken", method = RequestMethod.GET)
	public @ResponseBody UserToken getUserDetailsOfToken(@RequestHeader HttpHeaders headers) {

		System.out.println("\n====> API: getUserDetailsOfToken Start <====");
		UserToken userToken = null;
		try {
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				System.out.println("\n====> token: " + token);

				// Check if token is still active
				String tokenStatus = getStatusOfUserToken(token);

				// If the token is ACTIVE, Fetch the User details
				if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
					userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				} else {
					System.out.println("\n====> Token expired !!");
				}
			} else {
				throw new Exception("Token is missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> API: getUserDetailsOfToken End <====");
		return userToken;
	}

	@RequestMapping(value = "/dbconsole/checkTokenStatus", method = RequestMethod.GET)
	public @ResponseBody String checkTokenStatus(@RequestHeader HttpHeaders headers) {

		System.out.println("\n====> API: checkTokenStatus Start <====");
		JSONObject jsonObj = new JSONObject();

		try {
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				System.out.println("\n====> token: " + token);
				String tokenStatus = getStatusOfUserToken(token);
				System.out.println("\n====> tokenStatus: " + tokenStatus);
				jsonObj.put("tokenStatus", tokenStatus);
			} else {
				throw new Exception("Token is missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n====> API: checkTokenStatus End <====");
		return jsonObj.toString();
	}

	@RequestMapping(value = "/dbconsole/getUserProjects", method = RequestMethod.GET)
	public ResponseEntity<Object> getUserProjects(@RequestHeader HttpHeaders headers) {
		System.out.println("\n====> API: getUserProjects Start <====");
		List<DomainProject> projectList = null;
		try {
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (userToken != null) {
					String tokenStatus = getStatusOfUserToken(token);
					if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
						// projectList = iProjectDAO.getAllDomainProjectsForUser(userToken.getEmail());
						projectList = iProjectDAO.getAllDomainProjectsForUser();
					} else {
						throw new Exception("Token expired !!");
					}

				} else {
					throw new Exception("Failed to get Token details!!");
				}
			} else {
				throw new Exception("Token is missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.EXPECTATION_FAILED);
		}
		System.out.println("\n====> API: getUserProjects End <====");
		return new ResponseEntity<>(projectList, HttpStatus.OK);
	}

	/**
	 * This method is to get the count summary details of various connections
	 * 
	 * @param request
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/dbconsole/getDashboardSummary")
	public @ResponseBody String getDashboardSummary(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestParam String startDate, @RequestParam String endDate) {
		System.out.println("\n=====> getDashboardSummary - START <=====");
		String result = "";
		try {
			List<ReportUIDashboardSummary> summaryList = new ArrayList<ReportUIDashboardSummary>();

			// Get the list of connections enabled for the project
			List<DashboardConnection> dashboardConnectionList = dashboardConsoleDao
					.getEnabledConnectionsForDashboard(domainId, projectId);

			if (dashboardConnectionList != null && dashboardConnectionList.size() > 0) {
				for (DashboardConnection dashboardConnection : dashboardConnectionList) {

					if (dashboardConnection != null) {
						ReportUIDashboardSummary reportUIDashboardSummary = dashboardConsoleDao
								.getDashboardSummaryForConnection(domainId, projectId, dashboardConnection, startDate,
										endDate);

						if (reportUIDashboardSummary != null) {
							summaryList.add(reportUIDashboardSummary);
						}
					}
				}
			}

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(summaryList);
			System.out.println("result:" + result);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getDashboardSummary API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getDashboardSummary - END  <=====");
		return result;
	}

	@RequestMapping(value = "/dbconsole/getTableSummary")
	public @ResponseBody String getTableSummary(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestParam String startDate, @RequestParam String endDate) {

		System.out.println("\n=====> getTableSummary - START <=====");
		String result = "";
		try {
			List<ReportUITableSummary> summaryList = new ArrayList<ReportUITableSummary>();

			// Get unique datasource List
			List<String> datasourceList = dashboardConsoleDao.getUniqueDatasourcesFromConnValidationMap(domainId,
					projectId);

			if (datasourceList != null) {
				for (String sourceType : datasourceList) {

					System.out.println("\n====>datasource: " + sourceType);

					ReportUITableSummary sourceSumry = dashboardConsoleDao.getSummaryForSourceTypeDateRange(domainId,
							projectId, sourceType, startDate, endDate);

					if (sourceSumry != null && sourceSumry.getProcessedCount() > 0) {
						summaryList.add(sourceSumry);
					}

				}
			}
			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(summaryList);
			System.out.println("result:" + result);
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTableSummary API !!");
			e.printStackTrace();
		}

		System.out.println("\n=====> getTableSummary - END  <=====");
		return result;
	}

	/*
	 * Daily Performance Trend:
	 * 
	 * For 10 Dates Total Processed, Passed, Failed, %Failed
	 */
	@RequestMapping(value = "/dbconsole/getDailyPerformanceTrend")
	public @ResponseBody String getDailyPerformanceTrend(HttpServletRequest request, long domainId, long projectId,
			long connectionId, String startDate, String endDate) {

		System.out.println("\n=====> getDailyPerformanceTrend - START <=====");
		String result = "";

		try {
			List<ReportUIPerformanceSummary> summryList = dashboardConsoleDao.getDailyPerformanceTrend(domainId,
					projectId, connectionId, startDate, endDate);

			if (summryList != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(summryList);
			}
		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getDailyPerformanceTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getDailyPerformanceTrend - END  <=====");
		return result;
	}

	/*
	 * Daily Pass Trend
	 * 
	 * For each source Type pass percentage for Date Range Eg: 10 Dates
	 */
	@RequestMapping(value = "/dbconsole/getDailyPassTrend")
	public @ResponseBody String getDailyPassTrend(HttpServletRequest request, long domainId, long projectId,
			long connectionId, String startDate, String endDate) {

		System.out.println("\n=====> getDailyPassTrend - START <=====");
		String result = "";

		try {
			List<ReportUIPassTrendSummary> summryList = new ArrayList<ReportUIPassTrendSummary>();

			Map<String, Map<String, Double>> resultMap = dashboardConsoleDao.getDailyPassTrend(domainId, projectId,
					connectionId, startDate, endDate);

			if (resultMap != null && resultMap.size() > 0) {
				System.out.println("resultMap: " + resultMap);

				for (String f_date : resultMap.keySet()) {
					ReportUIPassTrendSummary summry = new ReportUIPassTrendSummary();

					Map<String, Double> sourcePercMap = resultMap.get(f_date);
					summry.setDate(f_date);
					summry.setSourcePercentage(sourcePercMap);
					summryList.add(summry);
				}
			}

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(summryList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getDailyPassTrend API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getDailyPassTrend - END  <=====");
		return result;
	}

	/*
	 * Daily Failed Files
	 * 
	 */
	@RequestMapping(value = "/dbconsole/getDailyFailedFiles")
	public @ResponseBody String getDailyFailedFiles(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestParam long connectionId, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> getDailyFailedFiles - START <=====");
		String result = "";
		try {
			List<ReportUIFailedFilesSummary> summryList = dashboardConsoleDao.getDailyFailedFiles(domainId, projectId,
					connectionId, startDate, endDate);

			if (summryList != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(summryList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getDailyFailedFiles API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getDailyFailedFiles - END  <=====");
		return result;
	}

	@RequestMapping(value = "/dbconsole/getSummaryForDataSource")
	public @ResponseBody String getSummaryForSourceType(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestParam String datasource, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> getSummaryForDataSource - START <=====");

		String result = "";

		try {
			List<ReportUIDatasourceSummary> sourceSumryList = new ArrayList<ReportUIDatasourceSummary>();

			// Get the list of connections enabled for the project
			List<DashboardConnection> dashboardConnectionList = dashboardConsoleDao
					.getEnabledConnectionsForDashboard(domainId, projectId);

			if (dashboardConnectionList != null && dashboardConnectionList.size() > 0) {

				// Get unique Source list for Datasource
				List<String> sourceList = dashboardConsoleDao.getSourceListForDatasource(domainId, projectId,
						datasource);

				if (sourceList != null && sourceList.size() > 0) {

					for (String source : sourceList) {
						System.out.println("Source: " + source);

						// Get the unique FileName for the Source
						List<String> fileNameList = dashboardConsoleDao.getFileNameListForSource(domainId, projectId,
								datasource, source);

						if (fileNameList != null && fileNameList.size() > 0) {

							for (String fileName : fileNameList) {
								ReportUIDatasourceSummary sourceSumry = new ReportUIDatasourceSummary();
								sourceSumry.setDatasource(datasource);
								sourceSumry.setSource(source);
								sourceSumry.setFileName(fileName);

								List<ReportUIConnCheckSummary> connectionCheckList = new ArrayList<ReportUIConnCheckSummary>();
								for (DashboardConnection dashConn : dashboardConnectionList) {

									long connectionId = dashConn.getConnectionId();

									// For each Connection, Datasource, Source and filename combination get the
									// validation list
									List<Long> validationList = dashboardConsoleDao.getValdiationsForSourceFile(
											domainId, projectId, connectionId, datasource, source, fileName);

									if (validationList != null) {
										for (Long idApp : validationList) {

											// Get AppType
											String appType = validationCheckDao.getAppTypeFromListApplication(idApp);
											System.out.println("idApp: " + idApp + "  -  appType: " + appType);

											if (appType != null) {

												ReportUIConnCheckSummary ConnCheckSummary = new ReportUIConnCheckSummary();
												ConnCheckSummary.setConnectionId(connectionId);
												ConnCheckSummary.setConnectionName(dashConn.getConnectionName());
												ConnCheckSummary.setDisplayName(dashConn.getDisplayName());
												ConnCheckSummary.setDisplayOrder(dashConn.getDisplayOrder());

												List<ReportUIComponentCheckSummary> checkList = new ArrayList<ReportUIComponentCheckSummary>();

												// Current only DQ validations are considered
												// TODO: need to do for other types in future
												if (appType.trim().equalsIgnoreCase("Data Forensics")) {

													// Check if essential checks are enabled
													boolean essentialCheckEnabled = dashboardConsoleDao
															.isDQComponentCheckEnabledForApp(idApp, "Essential Check");

													// Check if advanced checks are enabled
													boolean advancedCheckEnabled = dashboardConsoleDao
															.isDQComponentCheckEnabledForApp(idApp, "Advanced Check");

													// Get the max execution Date of the Application
													Date maxDate = dashboardConsoleDao.getMaxDateForValidation(idApp,
															startDate, endDate);

													if (maxDate != null
															&& (essentialCheckEnabled || advancedCheckEnabled)) {

														String appMaxDate = new SimpleDateFormat("yyyy-MM-dd")
																.format(maxDate);

														if (advancedCheckEnabled) {
															// Get the totalDQI of latest Run
															Double totalDQI = dashboardConsoleDao
																	.getTotalDQIOfIdAppForComponentType(idApp,
																			appMaxDate, "Advanced Check");
															if (totalDQI != null) {
																ReportUIComponentCheckSummary checkSumry = new ReportUIComponentCheckSummary();
																checkSumry.setSource(source);
																checkSumry.setCheckName("Advanced Check");
																checkSumry.setDqi(totalDQI);
																checkSumry.setAppId(idApp);
																checkSumry.setExectionDate(appMaxDate);
																checkList.add(checkSumry);
															}
														}

														if (essentialCheckEnabled) {
															// Get the totalDQI of latest Run
															Double totalDQI = dashboardConsoleDao
																	.getTotalDQIOfIdAppForComponentType(idApp,
																			appMaxDate, "Essential Check");
															if (totalDQI != null) {
																ReportUIComponentCheckSummary checkSumry = new ReportUIComponentCheckSummary();
																checkSumry.setSource(source);
																checkSumry.setCheckName("Essential Check");
																checkSumry.setDqi(totalDQI);
																checkSumry.setAppId(idApp);
																checkSumry.setExectionDate(appMaxDate);
																checkList.add(checkSumry);
															}
														}

														if (checkList != null && checkList.size() > 0) {
															ConnCheckSummary.setCheckList(checkList);
															connectionCheckList.add(ConnCheckSummary);
														}
													}
												}
											}
										}
									}
								}
								if (connectionCheckList != null && connectionCheckList.size() > 0) {
									sourceSumry.setConnectionCheckList(connectionCheckList);
									sourceSumryList.add(sourceSumry);
								}
							}
						}
					}
				}
			}

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(sourceSumryList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getSummaryForDataSource API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getSummaryForDataSource - END  <=====");
		return result;
	}

	/**
	 * Essential Check
	 * 
	 * This Method is API for Summary Details for a Datasource, Source , FileName
	 * for a Specific Date
	 * 
	 * 
	 * @param request
	 * @param processedDate
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/dbconsole/getEssentialCheckSummaryDetailsOfFile")
	public @ResponseBody String getEssentialCheckSummaryDetailsOfFile(HttpServletRequest request,
			@RequestParam String processedDate, @RequestParam Long idApp) {

		System.out.println("\n=====> getEssentialCheckSummaryDetailsOfFile - START <=====");
		String result = "";
		try {

			List<EssentialCheckSummaryReport> essCheckSummaryReportList = dashboardConsoleDao
					.getEssentialCheckSummaryDetailsOfFile(processedDate, idApp);

			if (essCheckSummaryReportList != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(essCheckSummaryReportList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getEssentialCheckSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getEssentialCheckSummaryDetailsOfFile - END  <=====");
		return result;
	}

	/**
	 * Essential Check
	 * 
	 * This Method is API for Summary Details of a Rule on click on Rule link
	 * 
	 * @param request
	 * @param processedDate
	 * @param fileName
	 * @return
	 */
	@RequestMapping(value = "/dbconsole/getEssentalCheckRuleSummaryDetailsOfFile")
	public @ResponseBody String getEssentalCheckRuleSummaryDetailsOfFile(HttpServletRequest request,
			@RequestParam String processedDate, @RequestParam Long idApp, @RequestParam long checkComponentId,
			@RequestParam String technicalName, @RequestParam String ruleName, @RequestParam String columnName) {

		System.out.println("\n=====> getEssentalCheckRuleSummaryDetailsOfFile - START <=====");
		String result = "";
		try {

			List<EssentialCheckRuleSummaryReport> essCheckSummaryReportList = dashboardConsoleDao
					.getEssentalCheckRuleSummaryDetailsOfFile(processedDate, idApp, checkComponentId, technicalName,
							ruleName, columnName);

			if (essCheckSummaryReportList != null) {
				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(essCheckSummaryReportList);
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getEssentalCheckRuleSummaryDetailsOfFile API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getEssentalCheckRuleSummaryDetailsOfFile - END  <=====");
		return result;
	}

	/**
	 * This method is API to get the project coverage details
	 * 
	 * @param projectId
	 */
	@RequestMapping(value = "/dbconsole/getProjectCoverage")
	public @ResponseBody String getProjectCoverage(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId) {

		System.out.println("\n=====> getProjectCoverage - START <=====");
		String result = "";
		try {

			// Get project details
			Project project = iProjectDAO.getSelectedProject(projectId);

			if (project != null && project.getIdProject() != 0l) {

				ReportUIProjectCoverage reportUIProjectCoverage = new ReportUIProjectCoverage();
				reportUIProjectCoverage.setDomainId(domainId);
				reportUIProjectCoverage.setProjectId(projectId);
				reportUIProjectCoverage.setProjectName(project.getProjectName());

				// Get connection coverage details
				reportUIProjectCoverage = dashboardConsoleDao.getProjectCoverage(reportUIProjectCoverage, domainId,
						projectId);

				ObjectMapper objMapper = new ObjectMapper();
				result = objMapper.writeValueAsString(reportUIProjectCoverage);
			} else {
				System.out.println("\n=====> Failed to fetch project details ..!!");
			}

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getProjectCoverage API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getProjectCoverage - END  <=====");
		return result;
	}

	/**
	 * This method is API to get the top 10 failed assets
	 * 
	 * @param projectId
	 */
	@RequestMapping(value = "/dbconsole/getTopFailedAssets")
	public @ResponseBody String getTopFailedAssets(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId) {

		System.out.println("\n=====> getTopFailedAssets - START <=====");
		String result = "";
		try {

			// Get connection coverage details
			List<ReportUIFailedAsset> failedAssetsList = dashboardConsoleDao.getTopFailedAssets(domainId, projectId);

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(failedAssetsList);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getTopFailedAssets API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getTopFailedAssets - END  <=====");
		return result;
	}

	/**
	 * This method is API to get the overall DQI index for project
	 * 
	 * @param projectId
	 */
	@RequestMapping(value = "/dbconsole/getOverallDQIIndexForProject")
	public @ResponseBody String getOverallDQIIndexForProject(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId) {

		System.out.println("\n=====> getOverallDQIIndexForProject - START <=====");
		String result = "";
		try {

			ReportUIOverallDQIIndex reportUIOverallDQIIndex = dashboardConsoleDao.getOverallDQIIndexForProject(domainId,
					projectId);

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(reportUIOverallDQIIndex);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getOverallDQIIndexForProject API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getOverallDQIIndexForProject - END  <=====");
		return result;
	}

	/**
	 * This method is API to get the DQI index history for project
	 * 
	 * @param projectId
	 */
	@RequestMapping(value = "/dbconsole/getIndexTrendHistory")
	public @ResponseBody String getIndexTrendHistory(HttpServletRequest request, @RequestParam long domainId,
			@RequestParam long projectId, @RequestParam String indexName, @RequestParam String startDate,
			@RequestParam String endDate) {

		System.out.println("\n=====> getIndexTrendHistory - START <=====");
		String result = "";
		try {
			ReportUIDQIIndexHistory reportUIDQIIndexHistory = dashboardConsoleDao.getIndexTrendHistory(domainId,
					projectId, indexName, startDate, endDate);

			ObjectMapper objMapper = new ObjectMapper();
			result = objMapper.writeValueAsString(reportUIDQIIndexHistory);

		} catch (Exception e) {
			System.out.println("\n=====>Exception occurred in getIndexTrendHistory API !!");
			e.printStackTrace();
		}
		System.out.println("\n=====> getIndexTrendHistory - END  <=====");
		return result;
	}

	
	private String getStatusOfUserToken(String token) {
		String tokenStatus = "EXPIRED";

		try {
			// Fetch token details
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

			if (userToken != null) {

				// Check if token is already EXPIRED, else check if it is still active
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {

					// Check if token is expired
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";

					// Update the token status in database
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			} else {
				System.out.println("\n====> Failed to get Token details!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tokenStatus;
	}

	@RequestMapping(value = "/dashboardConsole")
	public ModelAndView dashboardConsole(HttpServletRequest req, HttpSession session) throws NullPointerException {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Dashboard", "R", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("dashboardConsole");
			String averReportUILink = appDbConnectionProperties.getProperty("aver.report.link");
			modelAndView.addObject("averReportUILink", averReportUILink);
			modelAndView.addObject("currentSection", "DashboardConsole");
//			modelAndView.addObject("currentLink", "dashconsole");
			return modelAndView;
		} else {
			return new ModelAndView("loginPage");
		}
	}
}
