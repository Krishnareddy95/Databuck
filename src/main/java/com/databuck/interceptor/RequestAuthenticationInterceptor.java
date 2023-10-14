package com.databuck.interceptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IUserDAO;
import com.databuck.exception.ExceptionHandler;
import com.databuck.security.CheckVulnerability;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestAuthenticationInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = Logger.getLogger("RequestAuthenticationInterceptor");

	@Autowired
	HttpServletRequest httpRequest;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private IUserDAO IUserdao;

	@Autowired
	private CheckVulnerability checkVulnerability;

	UrlPathHelper urlPathHelper = new UrlPathHelper();

	private final String ADMIN_ROLE_NAME = "Admin";

	private final int BYPASS_SECURITY_RESTAPI_ANGULAR = 1;
	private final int BYPASS_SECURITY_DATABUCK_FORM_SUBMIT = 2; // Not used now as form submit security token bypass is
																// via techical type not by url address hard coding
	private final int BYPASS_SECURITY_DATABUCK_MISC = 3; // Can be used for any techical type of DataBuck url added now
															// to bypass project wise restriction
	private final int BYPASS_SECURITY_RESTAPI_ANGULAR_SESSION = 4;

	private final int SESSION_EXPIRED = 1;
	private final int UNASSIGNED_FEATURE_ACCESS = 2;
	private final int UNAUTHORIZED_ACCESS = 3;
	private final int SECURITY_TOKEN_INVALID = 4;
	private final int DATA_PARAMS_VULNERABLE = 5;
	private final int DOMAIN_PROJECT_VIOLATION = 6;
	private final int UNRECONIZED_FAILURE_CONDITION = 7;

	private final HashMap<Integer, String> oInterceptionErrors = new HashMap<Integer, String>() {
		{
			put(Integer.valueOf(SESSION_EXPIRED), "Your session timed out, kindly re-login in DataBuck");
			put(Integer.valueOf(UNASSIGNED_FEATURE_ACCESS),
					"Your Role or User Group do not have access to DataBuck Feature you trying to access");
			put(Integer.valueOf(UNAUTHORIZED_ACCESS), "Unauthorized access, directing to re-login in DataBuck");
			put(Integer.valueOf(SECURITY_TOKEN_INVALID), "Attempt to access DataBuck with invalid security token");
			put(Integer.valueOf(DATA_PARAMS_VULNERABLE), "Vulnerable input data, directing to re-login in DataBuck");
			put(Integer.valueOf(DOMAIN_PROJECT_VIOLATION),
					"Unauthorized DataBuck object access (you are not member of the project), directing to re-login in DataBuck");
			put(Integer.valueOf(UNRECONIZED_FAILURE_CONDITION),
					"Unrecognized DataBuck access failure, directing to re-login in DataBuck");
		}
	};

	private HttpSession getActiveSession(HttpServletRequest request) {
		return request.getSession(false);
	}

	private boolean isSessionApiUrlToBeByPassed(String sRequestUrI, int nByPassContext) {
		boolean lRetValue = false;
		List<String> urlbyPassList = new ArrayList<String>();
		int nLastIndex = 0;
		String sApiPostFix = "";

		String[] aApiOrAngularUrls = new String[] { "dbconsole/getDecryptedToken", "restapi","swagger-resources","v2","html","login" };

		// Added on 23 Nov 21 by Sreelakshmi
		urlbyPassList.add("quickStartTaskTracking");
		urlbyPassList.add("quickStartTaskStatus");

		// Added on 9 Dec 21 by Pradeep - trigger is not classical DataBuck object, do
		// not have project id based security/membership
		urlbyPassList.add("deleteTrigger");
		
		//Added on 29-4-2022 by Mamta
				urlbyPassList.add("createConnection");
				urlbyPassList.add("addNewBatch");
				urlbyPassList.add("duplicateSchemaName");
				
				urlbyPassList.add("dataTemplateAddNew");
				urlbyPassList.add("duplicatedatatemplatename");
				
				urlbyPassList.add("dataApplicationCreateView");
				urlbyPassList.add("duplicateValidationCheckName");
				
				urlbyPassList.add("duplicateRoleName");
				urlbyPassList.add("addNewRole");

				urlbyPassList.add("getConnectionDetailsForImportValidationByFile");


		try {
			nLastIndex = sRequestUrI.lastIndexOf("/");
			sApiPostFix = sRequestUrI.substring(nLastIndex + 1);

//Commented By :Vinav 29-09-2022
// Removed the bypass code to check vulnerabilities in angular
			if (nByPassContext == BYPASS_SECURITY_RESTAPI_ANGULAR_SESSION) {
				for (String sUrlPart : aApiOrAngularUrls) {
					if (sRequestUrI.toLowerCase().contains(sUrlPart)) {
						lRetValue = true;
						break;
					}
				}
			} else 
				
			if (nByPassContext == BYPASS_SECURITY_DATABUCK_MISC) {
				lRetValue = urlbyPassList.contains(sApiPostFix);
			} else {
				lRetValue = false;
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			DateUtility.DebugLog("isApiUrlToBeByPassed 99",
					String.format(
							"Exception in Checking Angular or Rest API URL Exception '%1$s' / return value '%2$s'",
							sRequestUrI, lRetValue));
			lRetValue = false;
		}
		// DateUtility.DebugLog("isApiUrlToBeByPassed 01", String.format("Checking for
		// Angular or Rest API bypass of URL = '%1$s' / bypassed? = '%2$s'",
		// sRequestUrI, lRetValue));
		return lRetValue;
	}

	private boolean isApiUrlToBeByPassed(String sRequestUrI, int nByPassContext) {
		boolean lRetValue = false;
		List<String> urlbyPassList = new ArrayList<String>();
		int nLastIndex = 0;
		String sApiPostFix = "";

		// Added 26 Nov by Pradeep (containing text in URL rather then end point)
		String[] aApiOrAngularUrls = new String[] { "dbconsole", "restapi","swagger-resources","v2", "html","login" };

		/*
		 * Most of DataBuck urls NOT to be hard coded as technical type (get, post, form
		 * submit post, view JSP page load) wise logic is standardized
		 * urlbyPassList.add("createDerivedDataTemplate");
		 * urlbyPassList.add("createValidationCheckAjax");
		 * 
		 * urlbyPassList.add("createSchemaBatch");
		 * urlbyPassList.add("createDataTemplate");
		 * 
		 * urlbyPassList.add("duplicateEmail");
		 * urlbyPassList.add("duplicatedatatemplatename");
		 * urlbyPassList.add("duplicateValidationCheckName");
		 * urlbyPassList.add("duplicateSchemaName");
		 * urlbyPassList.add("duplicateatabasename");
		 * urlbyPassList.add("duplicateRoleName");
		 * 
		 * urlbyPassList.add("processDQQuickStart");
		 * urlbyPassList.add("processDMQuickStart");
		 * urlbyPassList.add("quickStartTaskForm"); urlbyPassList.add("renewLicense");
		 * urlbyPassList.add("quickStartTaskStatus");
		 * urlbyPassList.add("downloadCsvS3");
		 * urlbyPassList.add("DownloadCustomDateRangeReportAsCsv");
		 * urlbyPassList.add("downloadDQSummary");
		 * urlbyPassList.add("updateDerivedDataTemplate");
		 * 
		 * urlbyPassList.add("exportCSVFileData");
		 * urlbyPassList.add("submitFileMonitoringCSV");
		 * urlbyPassList.add("exportGlobalRulesToCSV");
		 * urlbyPassList.add("importGlobalRulesFromCSV");
		 * urlbyPassList.add("submitImportUiForm");
		 * 
		 * urlbyPassList.add("statusBarAutoDt");
		 */

		// Added on 23 Nov 21 by Sreelakshmi
		urlbyPassList.add("quickStartTaskTracking");
		urlbyPassList.add("quickStartTaskStatus");

		// Added on 9 Dec 21 by Pradeep - trigger is not classical DataBuck object, do
		// not have project id based security/membership
		urlbyPassList.add("deleteTrigger");
		
		//Added on 29-4-2022 by Mamta
		urlbyPassList.add("createConnection");
		urlbyPassList.add("addNewBatch");
		urlbyPassList.add("duplicateSchemaName");
		
		urlbyPassList.add("dataTemplateAddNew");
		urlbyPassList.add("duplicatedatatemplatename");
		
		urlbyPassList.add("dataApplicationCreateView");
		urlbyPassList.add("duplicateValidationCheckName");
		
		urlbyPassList.add("duplicateRoleName");
		urlbyPassList.add("addNewRole");
		urlbyPassList.add("getConnectionDetailsForImportValidationByFile");

		//Added on 25-5-2022 by Bharat
		urlbyPassList.add("mainPrimaryMatchingHandler");
		urlbyPassList.add("renewLicense");

		if(sRequestUrI.contains("/renewLicense"))
			nByPassContext=BYPASS_SECURITY_DATABUCK_MISC;

		try {
			nLastIndex = sRequestUrI.lastIndexOf("/");
			sApiPostFix = sRequestUrI.substring(nLastIndex + 1);

			if (nByPassContext == BYPASS_SECURITY_RESTAPI_ANGULAR) {
				for (String sUrlPart : aApiOrAngularUrls) {
					if (sRequestUrI.toLowerCase().contains(sUrlPart)) {
						lRetValue = true;
						break;
					}
				}
			} else if (nByPassContext == BYPASS_SECURITY_DATABUCK_MISC) {
				lRetValue = urlbyPassList.contains(sApiPostFix);
			} else {
				lRetValue = false;
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			DateUtility.DebugLog("isApiUrlToBeByPassed 99",
					String.format(
							"Exception in Checking Angular or Rest API URL Exception '%1$s' / return value '%2$s'",
							sRequestUrI, lRetValue));
			lRetValue = false;
		}
		// DateUtility.DebugLog("isApiUrlToBeByPassed 01", String.format("Checking for
		// Angular or Rest API bypass of URL = '%1$s' / bypassed? = '%2$s'",
		// sRequestUrI, lRetValue));
		return lRetValue;
	}

	@SuppressWarnings("unused")
	private boolean isSessionActive(HttpServletRequest oRequest) {
		boolean lRetValue = true;
		HttpSession oSession = getActiveSession(oRequest);
		Object oUser = null;

		if (oSession != null) {
			oUser = oSession.getAttribute("user");
		}

		lRetValue = ((oUser == null) || (!oUser.equals("validUser"))) ? false : true;
		return lRetValue;
	}

	private boolean isValidSecurityToken(HttpServletRequest oRequest) {
		boolean lRetValue = true;
		HttpSession oSession = null;
		String sSessionToken = "", sHeaderToken = "";
		boolean lIsFormSubmit = (oRequest.getParameter("IsFormSubmit") == null) ? false : true;

		try {
			oSession = getActiveSession(oRequest);
			sSessionToken = (String) oSession.getAttribute("csrfToken");
			sHeaderToken = oRequest.getHeader("token");

			// DateUtility.DebugLog("isValidSecurityToken 01", String.format("URL '%1$s' /
			// '%2$s' / '%3$s'", sSessionToken, sHeaderToken, lIsFormSubmit));

			if (!lIsFormSubmit) {
				lRetValue = (sHeaderToken == null || sSessionToken == null || sHeaderToken.trim().isEmpty()
						|| !sSessionToken.equals(sHeaderToken)) ? false : true;
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			lRetValue = false;
		}

		return lRetValue;
	}

	private boolean isGetRequestVulnerable(HttpServletRequest oRequest, String[] aReqQryParms) {
		boolean lRetValue = false;
		int nVulnerableParms = 0;

		try {
			for (String sGetParameter : aReqQryParms) {
				String[] aGetParts = sGetParameter.split(",");

				if (aGetParts.length > 1) {
					if (checkVulnerability.getVulnerabilityTagCounts(aGetParts[1]) > 0) {
						++nVulnerableParms;
					}
				}
				lRetValue = (nVulnerableParms > 0) ? true : false;
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			lRetValue = false;
		}

		return lRetValue;	
	}

	private boolean isPostRequestVulnerable(HttpServletRequest oRequest) {
		Enumeration<String> parameterNames = oRequest.getParameterNames();
		boolean lRetValue = false;
		try {
			boolean isJson = true;
			while (parameterNames.hasMoreElements()) {
				isJson = false;
				String parameterName = parameterNames.nextElement();
				String paramValue = "" + oRequest.getParameter(parameterName);
				if (paramValue == null || paramValue.trim().isEmpty())
					continue;
				if (checkVulnerability.getVulnerabilityTagCounts(paramValue) > 0) {
					lRetValue = true;
				}
			}
		
			if(isJson) {
				String line = "";
				StringBuffer stringHtml = new StringBuffer();
				BufferedReader reader = null;
				InputStream inputStream = oRequest.getInputStream();
		        byte[] body = StreamUtils.copyToByteArray(inputStream);
		        InputStreamReader rs = new InputStreamReader(new ByteArrayInputStream(body));
		        reader = new BufferedReader(rs);
			    while ((line = reader.readLine()) != null) {
			    	stringHtml.append(line);
			    }
			    String match = stringHtml.toString();
			    boolean containsHTMLTag = match.matches(".*\\<[^>]+>.*");
			    if(containsHTMLTag) {
			    	lRetValue = true;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lRetValue;
	}

	/**
	 * Validate for requested component access
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private boolean validateComponentAccess(HttpServletRequest oRequest) {
		boolean lRetValue = true;
		int nCount = 0;
		HttpSession oSession = getActiveSession(oRequest);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();

		String sIdRole = oSession.getAttribute("idRole").toString();
		Long idUser = (Long) oSession.getAttribute("idUser");
		String userName = IUserdao.getfirstNameByUserId(idUser);
		String activity_Log_Time = formatter.format(date);
		String sessionId = oSession.getId();
		String sReqURL = urlPathHelper.getLookupPathForRequest(oRequest);

		String sUrlValidateSql = String
				.format("select count(*) as Count from component where lower(http_url) = lower('%1$s');", sReqURL);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sUrlValidateSql);

		nCount = (oSqlRowSet.next()) ? oSqlRowSet.getInt("Count") : 0;

		/*
		 * If url being attempted is not present in database? Then url do not need
		 * security allow to open it
		 */
		if (nCount < 1) {
			lRetValue = true;

			/* else if url is present then does access to given to logged in role? */
		} else {
			sUrlValidateSql = "";
			sUrlValidateSql = sUrlValidateSql
					+ "select http_url as ComponentUrl from component a, component_access b \n";
			sUrlValidateSql = sUrlValidateSql
					+ "where a.http_url = '%1$s' and a.row_id = b.component_row_id and b.role_row_id = %2$s;";

			sUrlValidateSql = String.format(sUrlValidateSql, sReqURL, sIdRole);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sUrlValidateSql);

			lRetValue = (oSqlRowSet.next()) ? true : false;
		}

		if (!lRetValue) {
			DateUtility.DebugLog("validateComponentAccess 01",
					String.format("Access denied for URL = '%1$s', Result = %2$s",
							urlPathHelper.getLookupPathForRequest(oRequest), lRetValue));
		}

		// log user activity if user has access to requested URL
		if (lRetValue) {
			// DateUtility.DebugLog("validateComponentAccess 02",String.format("User Id,
			// User Name, Access URL, Log Time, Session Id %1$s, %2$s, %3$s, %4$s", idUser,
			// userName, sReqURL, sessionId));

			try {
				String databuck_feature = IUserdao.getActivityFromUrl(sReqURL);

				String sql = "insert into logging_activity(user_id, user_name, access_url,activity_log_time,session_id,databuck_feature) "
						+ " VALUES (?,?,?,?,?,?)";
				int update = jdbcTemplate.update(new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement pst = con.prepareStatement(sql);
						pst.setLong(1, idUser);
						pst.setString(2, userName);
						pst.setString(3, sReqURL);
						pst.setString(4, activity_Log_Time);
						pst.setString(5, sessionId);
						pst.setString(6, databuck_feature != null ? databuck_feature : "");
						return pst;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lRetValue;
	}

	@SuppressWarnings({ "unchecked" })
	private boolean validateParamBasedAccess(HttpServletRequest oRequest, String[] aReqQryParms) {
		boolean lRetValue = true;
		String sAccessValidatorSql = "";
		Optional<String> oProjectId;

		HttpSession oSession = getActiveSession(oRequest);
		boolean lHttpGetMethodType = (oRequest.getMethod().equalsIgnoreCase("GET")) ? true : false;

		// DateUtility.DebugLog("validateParamBasedAccess 01", String.format("%1$s,%2$s,
		// %3$s",oRequest.getMethod(), urlPathHelper.getLookupPathForRequest(oRequest),
		// aReqQryParms.length));

		if (aReqQryParms.length > 0) {
			// DateUtility.DebugLog("validateParamBasedAccess 02",
			// String.format("%1$s,%2$s",urlPathHelper.getLookupPathForRequest(oRequest),aReqQryParms[0]));
			sAccessValidatorSql = getAccessValidatorSql(aReqQryParms, oRequest, oSession);

			if (sAccessValidatorSql.equalsIgnoreCase(ADMIN_ROLE_NAME)) {
				lRetValue = isLoggedUserHasAdminRole(oSession) ? true : false;

			} else if (sAccessValidatorSql.isEmpty()) {
				lRetValue = true;

			} else {
				oProjectId = jdbcTemplate.queryForList(sAccessValidatorSql, String.class).stream().findFirst();
				lRetValue = oProjectId.isPresent();
			}
		}

		if (!lRetValue) {
			DateUtility.DebugLog("validateParamBasedAccess 99",
					String.format("Query based access denied URL = '%1$s', SQL used = '%2$s'",
							urlPathHelper.getLookupPathForRequest(oRequest), sAccessValidatorSql));
		}
		return lRetValue;
	}

	/*
	 * Below are get parameters used in HTTP URLs to open data filtered specific
	 * pages: listApplications = idApp,idApp1,appId, listDataSchema =
	 * id,idDataSchema,idDataSchema1, listDataSources = idData,idData1,
	 * listDataBlend = idDataBlend, listColGlobalRules/listColRules = idListColrules
	 */
	private String getAccessValidatorSql(String[] aReqQryParms, HttpServletRequest oRequest, HttpSession oSession) {
		String sReqURL = urlPathHelper.getLookupPathForRequest(oRequest);
		String sRetSql = "";
		String sProjectMappingSql = getProjectMappingSqlQry(oSession);

		String[] aBaseSqlSet = new String[] {
				"select DataObject.project_id from listApplications DataObject, ({sProjectMappingSql}) Projects where DataObject.project_id = Projects.ProjectId and idApp = %1$s"
						.replace("{sProjectMappingSql}", sProjectMappingSql),
				"select DataObject.project_id from listDataSchema DataObject, ({sProjectMappingSql}) Projects where DataObject.project_id = Projects.ProjectId and DataObject.idDataSchema = %1$s"
						.replace("{sProjectMappingSql}", sProjectMappingSql),
				"select DataObject.project_id from listDataSources DataObject, ({sProjectMappingSql}) Projects where DataObject.project_id = Projects.ProjectId and DataObject.idData = %1$s;"
						.replace("{sProjectMappingSql}", sProjectMappingSql),
				"select DataObject.project_id from listDataBlend DataObject, listDataSources DataObject1, ({sProjectMappingSql}) Projects where DataObject.idData = DataObject1.idData and DataObject.idDataBlend = %1$s"
						.replace("{sProjectMappingSql}", sProjectMappingSql),
				"select DataObject.project_id from listColGlobalRules DataObject, ({sProjectMappingSql}) Projects where DataObject.project_id = Projects.ProjectId and DataObject.idListColrules = %1$s"
						.replace("{sProjectMappingSql}", sProjectMappingSql),
				"select DataObject.project_id from listColRules DataObject,  ({sProjectMappingSql}) Projects where DataObject.project_id = Projects.ProjectId and DataObject.idListColrules = %1$s"
						.replace("{sProjectMappingSql}", sProjectMappingSql) };

		HashMap<String, String> oApplicableSqlSet = new HashMap<String, String>() {
			{
				put("idApp", aBaseSqlSet[0]);
				put("idApp1", aBaseSqlSet[0]);
				put("appId", aBaseSqlSet[0]);

				put("id", aBaseSqlSet[1]);
				put("idDataSchema", aBaseSqlSet[1]);
				put("idDataSchema1", aBaseSqlSet[1]);

				put("idData", aBaseSqlSet[2]);
				put("idData1", aBaseSqlSet[2]);

				put("idDataBlend", aBaseSqlSet[3]);
			}
		};
		String aFirstParm[] = aReqQryParms[0].split(","); // only first parameter used rest ignored

		if (sReqURL.toLowerCase().indexOf("project") > -1) {
			sRetSql = "Admin";

		} else if (oApplicableSqlSet.containsKey(aFirstParm[0])) {
			sRetSql = String.format(oApplicableSqlSet.get(aFirstParm[0]), aFirstParm[1]);

		} else if (aFirstParm[0].equalsIgnoreCase("idListColrules")) {

			if (sReqURL.toLowerCase().indexOf("extend") > -1) {
				sRetSql = String.format(aBaseSqlSet[5], aFirstParm[1]);

			} else if (sReqURL.toLowerCase().indexOf("global") > -1) {
				sRetSql = String.format(aBaseSqlSet[4], aFirstParm[1]);

			} else {

				sRetSql = "";
			}
		} else {
			sRetSql = "";
		}

		// DateUtility.DebugLog("getAccessValidatorSql", String.format("Query parameter
		// = '%1$s' Generated SQL = '%2$s'", aReqQryParms[0], sRetSql));

		return sRetSql;
	}

	@Override
	public boolean preHandle(HttpServletRequest oRequest, HttpServletResponse oResponse, Object oHandler)
			throws Exception {
		boolean lRetValue = true;
		boolean lIsURLSecurityRequired = false;
		String sEndUserMsg = "", sSysLogMsg = "";
		int nEndUserCode = 0;
		oResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
		boolean lIsJspLoadingGetRequest = false;
		boolean lHttpGetMethodType = false;
		boolean lHttpPostMethodType = false;
		boolean lIsRelogin = false;

		String[] aReqQryParms = new String[] {};

		/*
		 * Quick exit to boost performance and rule out unwanted request hits like Java
		 * script, images and api calls etc
		 */
		if (oHandler instanceof ResourceHttpRequestHandler) {
			return true;

		} else if (isSessionApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_RESTAPI_ANGULAR_SESSION)) {
			return true;

		} else if (oHandler instanceof HandlerMethod) {
			Object bean = ((HandlerMethod) oHandler).getBean();
			if (bean instanceof ExceptionHandler) {
				// DateUtility.DebugLog("Request Interception 99", "Exception occured in
				// security interceptor: " + urlPathHelper.getLookupPathForRequest(oRequest));
				return true;
			}
		}

		/* Relevant URL requests start here */
		lIsURLSecurityRequired = JwfSpaInfra.isURLSecurityRequired(jdbcTemplate);
		lIsJspLoadingGetRequest = (oRequest.getHeader("IsJspLoadingGetRequest") == null) ? true : false;
		lIsRelogin = urlPathHelper.getLookupPathForRequest(oRequest).toLowerCase().contains("relogin".toLowerCase())
				? true
				: false;

		// DateUtility.DebugLog("Request Interception 01", String.format("'%1$s',
		// '%2$s', '%3$s', '%4$s'", urlPathHelper.getLookupPathForRequest(oRequest),
		// lIsURLSecurityRequired, oRequest.getParameter("IsFormSubmit"), lIsRelogin));

		if (lIsURLSecurityRequired) {
			try {
				lHttpGetMethodType = (oRequest.getMethod().equalsIgnoreCase("Get")) ? true : false;
				lHttpPostMethodType = (oRequest.getMethod().equalsIgnoreCase("Post")) ? true : false;
				aReqQryParms = (lHttpGetMethodType) ? getGetRequestParamaters(oRequest) : new String[] {};

				if (lIsRelogin) {
					oResponse.setHeader("IsReloginResponse", "true");

				} else if (!isSessionActive(oRequest)
						&& !(isApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_RESTAPI_ANGULAR))) {
					nEndUserCode = SESSION_EXPIRED;
					sSysLogMsg = "";

				} else if (isApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_DATABUCK_MISC)) {
					lRetValue = true;

				} else if (!validateParamBasedAccess(oRequest, aReqQryParms)
						&& !(isApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_RESTAPI_ANGULAR))) {
					nEndUserCode = DOMAIN_PROJECT_VIOLATION;
					sSysLogMsg = oInterceptionErrors.get(nEndUserCode);

				} else if ((lHttpGetMethodType) && isGetRequestVulnerable(oRequest, aReqQryParms)) {
					nEndUserCode = DATA_PARAMS_VULNERABLE;
					sSysLogMsg = oInterceptionErrors.get(nEndUserCode);

				} else if ((lHttpPostMethodType) && isPostRequestVulnerable(oRequest)) {
					nEndUserCode = DATA_PARAMS_VULNERABLE;
					sSysLogMsg = oInterceptionErrors.get(nEndUserCode);
					if(oRequest.getRequestURI().contains("mainPrimaryMatchingHandler") || oRequest.getRequestURI().contains("editPrimaryMatchingHandler") || oRequest.getRequestURI().contains("insertGlobalThreshold"))
					  {
			            nEndUserCode = 0;
			          }

				} else if ((aReqQryParms.length > 0) && (!validateComponentAccess(oRequest))
						&& !(isApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_RESTAPI_ANGULAR))) {
					nEndUserCode = UNASSIGNED_FEATURE_ACCESS;
					sSysLogMsg = oInterceptionErrors.get(nEndUserCode);

					/*
					 * After all is well on HTML injection, base access ok checks if any kind of
					 * miscellaneous DataBuck application url to be skipped for say security token,
					 * project & domain security wise checks then add them in isApiUrlToBeByPassed()
					 * function array.
					 */
				} else if (!(isApiUrlToBeByPassed(oRequest.getRequestURI(), BYPASS_SECURITY_RESTAPI_ANGULAR))) {
					if ((lHttpPostMethodType) && (!isValidSecurityToken(oRequest))) {
						nEndUserCode = UNAUTHORIZED_ACCESS;
						sSysLogMsg = oInterceptionErrors.get(SECURITY_TOKEN_INVALID);
					}
				}

				if (nEndUserCode > 0) {
					lRetValue = false;
					sEndUserMsg = oInterceptionErrors.get(nEndUserCode);
					DateUtility.DebugLog("Request Interception 02", String.format(
							"Rejected access to URL '%1$s', Redirect to Intermediate screen with Message '%2$s'",
							urlPathHelper.getLookupPathForRequest(oRequest), sEndUserMsg));

					oResponse.setHeader("InterceptorErrorCode", Integer.toString(nEndUserCode));
					oResponse.setHeader("InterceptorErrorMsg", oInterceptionErrors.get(nEndUserCode));
					oResponse.setHeader("IsJspLoadingGetRequest", (lIsJspLoadingGetRequest ? "true" : "false"));

					if (!sSysLogMsg.isEmpty()) {
						DateUtility.DebugLog("Request Interception 03", sSysLogMsg);
					}

					if (lIsJspLoadingGetRequest) {
						oResponse.sendRedirect("relogin?reason=" + Integer.toString(nEndUserCode));
					} else {
						oResponse.setHeader("ReloginUrl", "relogin?reason=" + Integer.toString(nEndUserCode));
						oResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					}
					DateUtility.DebugLog("Request Interception 04",
							String.format("Completed pre handler (error flow) for %1$s'",
									urlPathHelper.getLookupPathForRequest(oRequest)));
				} else {
					// DateUtility.DebugLog("Request Interception 04", String.format("Completed pre
					// handler (success flow) for %1$s'",
					// urlPathHelper.getLookupPathForRequest(oRequest)));
				}
			} catch (Exception oException) {
				oException.printStackTrace();
				DateUtility.DebugLog("Request Interception 99", String.format(
						"Exception occured in security interceptor pre handle: \n'%1$s'", oException.getMessage()));
				lRetValue = false;
			}
		} else {
			lRetValue = true;
		}
		return lRetValue;
	}

	@Override
	public void postHandle(HttpServletRequest oRequest, HttpServletResponse oResponse, Object oHandler,
			ModelAndView oModelAndView) throws Exception {
		String sReloginReason = "", sInterceptorErrorMsg = "";
		int nInterceptorErrorCode = 0;

		if (oResponse.getHeader("IsReloginResponse") != null) {
			sReloginReason = (oRequest.getParameter("reason") != null) ? oRequest.getParameter("reason")
					: Integer.toString(UNAUTHORIZED_ACCESS);
			try {
				nInterceptorErrorCode = Integer.parseInt(sReloginReason);
			} catch (Exception oExcpetion) {
				nInterceptorErrorCode = UNAUTHORIZED_ACCESS;
			}
			sInterceptorErrorMsg = oInterceptionErrors.get(nInterceptorErrorCode);
			oModelAndView.addObject("InterceptorErrorMsg", sInterceptorErrorMsg);

			// DateUtility.DebugLog("Request Interception 05", String.format("From post
			// handler for URL = '%1$s', User Msg = '%2$s'",
			// urlPathHelper.getLookupPathForRequest(oRequest), sInterceptorErrorMsg));
		}

	}

	@Override
	public void afterCompletion(HttpServletRequest oRequest, HttpServletResponse oResponse, Object oHandler,
			Exception oException) throws Exception {
		// DateUtility.DebugLog("afterCompletion", String.format("afterCompletion method
		// of RequestAuthenticationInterceptor: URL = '%1$s', Response Status = '%2$s'",
		// urlPathHelper.getLookupPathForRequest(oRequest), oResponse.getStatus()));
	}

	/*
	 * Get all request query parameters from current request. Exception is
	 * swallowed, if exception occurs it sends empty set
	 */
	private String[] getGetRequestParamaters(HttpServletRequest oRequest) {
		Enumeration oEnumeration = null;
		List<String> aReqParms = new ArrayList<String>();
		String[] aRetArray = new String[] {};

		try {
			oEnumeration = oRequest.getParameterNames();
			while (oEnumeration.hasMoreElements()) {
				String sParameterName = oEnumeration.nextElement().toString();
				String sParameterValue = oRequest.getParameter(sParameterName);

				aReqParms.add(String.format("%1$s,%2$s", sParameterName, sParameterValue));
			}
			aRetArray = (aReqParms.size() > 0) ? aReqParms.toArray(aRetArray) : aRetArray;
		} catch (Exception oException) {
			oException.printStackTrace();
			aRetArray = new String[] {};
		}
		return aRetArray;
	}

	private String getProjectMappingSqlQry(HttpSession oSession) {
		String sRetValue = "";
		boolean lIsActiveDirectoryAuthentication = JwfSpaInfra
				.getPropertyValue(appDbConnectionProperties, "isActiveDirectoryAuthentication", "xx")
				.equalsIgnoreCase("Y");
		String sUserLDAPGroups, sLdapGroupInClause = "";

		if (lIsActiveDirectoryAuthentication) {
			if (oSession.getAttribute("UserLDAPGroups") != null) {
				sUserLDAPGroups = oSession.getAttribute("UserLDAPGroups").toString();
			} else {
				sUserLDAPGroups = "";
			}
			sLdapGroupInClause = getSqlInClause(sUserLDAPGroups);
			sRetValue = "";

			sRetValue = sRetValue + "select distinct c.idProject as ProjectId ";
			sRetValue = sRetValue
					+ "from login_group a, Role b, project c, login_group_to_role d, login_group_to_project e ";
			sRetValue = sRetValue + "where a.row_id = d.login_group_row_id ";
			sRetValue = sRetValue + "and b.idRole = d.role_row_id ";
			sRetValue = sRetValue + "and a.row_id = e.login_group_row_id ";
			sRetValue = sRetValue + "and c.idProject = e.project_row_id ";
			sRetValue = sRetValue + "and a.group_name in (%1$s) ";

			sRetValue = String.format(sRetValue, sLdapGroupInClause);
		} else {
			// Query compatibility changes for both POSTGRES and MYSQL
			String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
					: "User";
			sRetValue = String.format(
					"select distinct b.idProject as ProjectId from "+user_table+" a, projecttouser b where a.email = b.idUser and  b.idUser = '%1$s'",
					oSession.getAttribute("email").toString());
		}
		return sRetValue;
	}

	private String getSqlInClause(String sStringWithCommaDelimitor) {
		String sRetValue = "";
		String sSanitizedValue = sStringWithCommaDelimitor.trim().replaceAll(" ", "");

		if (sSanitizedValue.isEmpty()) {
			sRetValue = "";
		} else {
			String[] aParts = sSanitizedValue.split(",");

			for (int nIndex = 0; nIndex < aParts.length; ++nIndex) {
				aParts[nIndex] = "'" + aParts[nIndex] + "'";
			}
			sRetValue = String.join(",", aParts);
		}

		return sRetValue;
	}

	private boolean isLoggedUserHasAdminRole(HttpSession oSession) {
		boolean lRetValue = true;
		String sRoleName = "";

		String sGetRoleSql = String.format("select roleName as RoleName from Role where idRole = %1$s;",
				oSession.getAttribute("idRole").toString());
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sGetRoleSql);

		sRoleName = (oSqlRowSet.next()) ? oSqlRowSet.getString("RoleName") : "xx";
		lRetValue = sRoleName.equalsIgnoreCase(ADMIN_ROLE_NAME);

		return lRetValue;
	}
}
