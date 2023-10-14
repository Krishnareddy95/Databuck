package com.databuck.restcontroller;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;

import com.databuck.service.ExecutiveSummaryService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Response;
import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import java.text.SimpleDateFormat;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DataDomain;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.KeyMeasurementValUpdate;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDmCriteria;
import com.databuck.bean.ValidationCreateReq;
import com.databuck.bean.listModelGovernance;
import com.databuck.bean.listStatisticalMatchingConfig;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.TaskTypes;
import com.databuck.integration.IntegrationMasterService;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.CSVGenerator;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.TokenValidator;
import com.databuck.util.JwfSpaInfra.CustomizeDataTableColumn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckValidationRestController {

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private TokenValidator tokenValidator;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IUserDAO userDAO;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private CSVGenerator csvGenerator;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private SchemaDAOI schemaDao;

	@Autowired
	GlobalRuleDAO globalRuleDAO;

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	private IntegrationMasterService integrationMasterService;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	protected static final String PRIMARY_KEY_MATCH_JOIN_FIELD = "PRIMARY_KEY_MATCH_JOIN_FIELD";
	protected static final String PRIMARY_KEY_MATCH_VALUE_FIELD = "PRIMARY_KEY_MATCH_VALUE_FIELD";

	private static final Logger LOG = Logger.getLogger(DatabuckValidationRestController.class);

	@RequestMapping(value = "/dbconsole/validations", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getPaginatedCustomRulesList(@RequestHeader HttpHeaders headers,
			@RequestBody HashMap<String, String> params) {
		LOG.info("dbconsole/validations - START");
		JSONObject response = new JSONObject();
		response.put("status", "failed");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token == null || token.isEmpty()) {
				response.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");
				return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			if (!tokenValidator.isValid(token)) {
				response.put("message", "Token is expired.");
				LOG.error("Token is expired.");
				return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			LOG.debug("Getting request parameters  " + params);
			JSONObject validationRes = new JSONObject();
			validationRes.put("validations", validationcheckdao.getPaginatedValidationsJsonData(params));
			validationRes.put("isRuleCatalogEnabled", ruleCatalogService.isRuleCatalogEnabled());

			response.put("result", validationRes);
			response.put("status", "success");
			response.put("message", "Successfully fetched validation list.");
			LOG.info("Successfully fetched validation list.");
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to get records.");
			LOG.error(ex.getMessage());
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		LOG.info("dbconsole/validations - END");
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/validationCSV", method = RequestMethod.POST)
	public void validationCSV(@RequestHeader HttpHeaders headers, @RequestBody HashMap<String, String> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/validationCSV - START");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token == null || token.isEmpty()) {
				LOG.error("Token is missing in headers.");
				httpResponse.sendError(0, "Token is missing in headers.");
			}
			if (!tokenValidator.isValid(token)) {
				LOG.error("Token is expired.");
				httpResponse.sendError(0, "Token is expired.");
			}
			LOG.debug("Getting request parameters  " + params);
			JSONArray validations = validationcheckdao.getPaginatedValidationsJsonData(params);
			if (validations == null) {
				validations = new JSONArray();
			}
			httpResponse.setContentType("text/csv");
			String csvFileName = "GlobalRule" + LocalDateTime.now() + ".csv";
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
			httpResponse.setHeader(headerKey, headerValue);
			JSONArray headerValues = new JSONArray();
			JSONArray columns = new JSONArray();
			headerValues.put("Validation Id").put("Validation Check Name").put("Validation Template").put("App Type")
					.put("App Mode").put("Project Name").put("Created On").put("Created By").put("Approval Status")
					.put("Staging Status").put("Approved On").put("Approved By").put("Status").put("Job Size");
			columns.put("ValidationId").put("ValidationCheckName").put("DataTemplateName").put("AppType").put("AppMode")
					.put("ProjectName").put("CreatedOn").put("CreatedBy").put("ApprovalStatus").put("StagingStatus")
					.put("ApprovedOn").put("ApprovedBy").put("Status").put("JobSize");
			httpResponse.getWriter().append(csvGenerator.getCSVString(validations, headerValues, columns));
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage());
			try {
				httpResponse.sendError(0, ex.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LOG.info("dbconsole/validationCSV - END");
	}

	@RequestMapping(value = "/dbconsole/validation-dropdownData")
	public ResponseEntity<Object> listValidation(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/validation-dropdownData - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			LOG.debug("Getting request parameters  " + params);
			List<ListDataSchema> listdataschema = listdatasourcedao
					.getListDataSchema(Long.valueOf((String) params.get("projectId")), null, null, null);
			List<DataDomain> lstDataDomain = validationcheckdao.getAllDataDomainNames();
			List<String> tamplateListFrom = validationcheckdao
					.getTemplateListDataSource(Long.valueOf((String) params.get("projectId")));
			List<String> list1 = new ArrayList<>();
			for (String list : tamplateListFrom) {
				String str = list.toString().replace("[", "").replace("]", "");
				list1.add(str);
			}
			response.put("listDataSchemas", listdataschema);
			response.put("dataDomains", lstDataDomain);
			response.put("tamplateListFrom", list1);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to get data");
			LOG.error(e.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		Map<String, Object> op = new HashMap<>();
		op.put("status", "success");
		op.put("message", "Succussfully got the data.");
		op.put("result", response);
		LOG.info("dbconsole/validation-dropdownData - END");
		return new ResponseEntity<Object>(op, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/connection-dropdownData")
	public ResponseEntity<Object> connectioDetails(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/connection-dropdownData - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("status", "failed");
			response.put("message", "Token is expired.");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			LOG.debug("Getting request parameters  " + params);
			List<Map<String, Object>> listdataschema = listdatasourcedao
					.getListDataSchemaForDropDown(params.get("projectId"));
			response.put("listDataSchemas", listdataschema);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to get data");
			LOG.error(e.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		Map<String, Object> op = new HashMap<>();
		op.put("status", "success");
		op.put("message", "Succussfully got the data.");
		op.put("result", response);
		LOG.info("Succussfully got the data.");
		LOG.info("dbconsole/connection-dropdownData - END");
		return new ResponseEntity<Object>(op, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/duplicateValidationCheckName", method = RequestMethod.POST)
	public ResponseEntity<Object> duplicateValidationCheckName(@RequestHeader HttpHeaders headers,
			@RequestBody HashMap<String, String> params) {
		LOG.info("dbconsole/duplicateValidationCheckName - START");
		String token = null;
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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

		String validationCheckName = params.get("validationName");
		if (validationCheckName == null || validationCheckName.trim().isEmpty()) {
			response.put("isValidationNameValid", false);
			response.put("message", "Please enter Validation Name");
			LOG.error("Please Enter Validation Name");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.debug("Getting request parameters  " + params);
		Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");
		Matcher match = pattern.matcher(validationCheckName);
		boolean val = match.find();
		LOG.debug("val" + val);
		if (val == false) {
			response.put("isValidationNameValid", false);
			response.put("message",
					"Please start naming with alphabets and do not use spaces or special characters except _(underscore)");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_]*$");
			Matcher match2 = pattern2.matcher(validationCheckName);
			boolean val2 = match2.find();
			if (val2 == false) {
				response.put("isValidationNameValid", false);
				response.put("message",
						"Please start naming with alphabets and do not use spaces or special characters except _(underscore)");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		}
		String name = validationcheckdao.duplicateValidationCheckNameNew(validationCheckName);
		if (name != null) {
			response.put("isValidationNameValid", false);
			response.put("message", "This Validation name is in use.");
			response.put("status", "success");
			LOG.error("This Validation Check name is in use.Please choose another name.");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("status", "success");
		response.put("isValidationNameValid", true);
		response.put("message", "You could use this validation name");
		LOG.info("dbconsole/duplicateValidationCheckName - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/createValidationCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> createValidationCheckAjax(HttpServletResponse response, HttpSession session,
			@RequestBody ValidationCreateReq validation, @RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/createValidationCheck - START");
		Integer domainId = validation.getDomainId();
		String validationJobSize = validation.getValidationJobSize();
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("status", "success");
		responseMap.put("message", "Successfully stored the details.");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("status", "failed");
			responseMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			responseMap.put("status", "failed");
			responseMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		// for DC-2979
		if (validation.getCreatedBy().trim().isEmpty()) {
			System.out.println("i am here.............");
			responseMap.put("status", "failed");
			responseMap.put("message", "Created By field is mandatory");
			LOG.error("Created By field is mandatory");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		// changes regarding Audit trail
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long idUser = validation.getIdUser();
		LOG.debug("idUser=" + idUser);
		LOG.info("createValidationCheckAjax");
		String name = validation.getName();
		LOG.debug("name=" + name);
		String description = validation.getDescription();
		LOG.debug("description=" + description);
		long idData = 0l;
		String apptype = validation.getApptype();
		LOG.debug("apptype=" + apptype);
		LOG.info("************ DATA DOMAIN **********");
		int data_Domain_id = validation.getDataDomainId();

		Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");
		Matcher match = pattern.matcher(name);
		boolean val = match.find();
		LOG.debug("val" + val);
		if (val == false) {
			responseMap.put("status", "failed");
			responseMap.put("message", "Name must begin with a letter and cannot contain spaces,special characters");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		} else {
			Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_]*$");
			Matcher match2 = pattern2.matcher(name);
			boolean val2 = match2.find();
			if (val2 == false) {
				responseMap.put("status", "failed");
				responseMap.put("message",
						"Name must begin with a letter and cannot contain spaces,special characters.");
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		String fm_connectionId = validation.getFmConnectionId();
		LOG.debug("fm_connectionId: " + fm_connectionId);
		idData = 0l;
		if (apptype.equals("Schema Matching") || apptype.equals("File Monitoring")) {

			idData = validationcheckdao.getlatestListDataSourceIdData();
			String templatename = validation.getSelectedTables();
			String str = templatename.toString().replace("[", "").replace("]", "").toString().replace("\"", "").trim();
			LOG.debug("templatename :: " + templatename + "  valuess :: " + str + " @@@ " + idData);
		} else {
			String templatename = validation.getSelectedTables();
			String str = templatename.toString().replace("[", "").replace("]", "").toString().replace("\"", "").trim();
			LOG.debug("templatename :: " + templatename + "  valuess :: " + str);
			idData = validationcheckdao.getListDataSourceIdData(str);
			LOG.debug("templatename :: " + templatename + "  valuess :: " + str + " @@@ " + idData);
		}

		if (apptype.equalsIgnoreCase("Data Forensics")) {
			idData = Long.parseLong(validation.getSelectedTables().replace("\"", "").trim());
			LOG.debug("********Data Forensics idData=" + idData);
		}

		try {
			String stringStatCheck = "N";
			if (apptype.equalsIgnoreCase("Matching")) {
				apptype = validation.getMatchapptype();
				LOG.debug("apptype=" + apptype);
				idData = Long.parseLong(validation.getSelectedTables().replace("\"", "").trim());
				LOG.debug("********Matcing idData=" + idData);
				stringStatCheck = "Y";
			}

			if (apptype.equalsIgnoreCase("Rolling DataMatching")) {
				idData = Long.parseLong(validation.getSelectedTables().replace("\"", "").trim());
				LOG.debug("********Matcing idData=" + idData);
			}
			Long idData1 = -1l;
			try {
				LOG.debug("idData=" + idData1);
				idData1 = validation.getSourceId();

			} catch (Exception e) {

			}

			Double matchingThreshold = validation.getThreshold();

			LOG.debug("matchingThreshold=" + matchingThreshold);

			String incrementalMatching = validation.getIncrementalMatching();
			LOG.debug("incrementalMatching=" + incrementalMatching);

			String dateFormat = validation.getDateformatid();
			LOG.debug("dateFormat=" + dateFormat);
			String leftSliceEnd = validation.getLeftsliceend();
			LOG.debug("leftSliceEnd=" + leftSliceEnd);

			String fileMonitoringType = validation.getFileMonitoringType();
			LOG.debug("fileMonitoringType=" + fileMonitoringType);

			String continuousFileMonitoring = validation.getEnableContinuousMonitoring();
			LOG.debug("enableContinuousMonitoring=" + continuousFileMonitoring);
			if (continuousFileMonitoring == null || !continuousFileMonitoring.trim().equalsIgnoreCase("Y")) {
				continuousFileMonitoring = "N";
			}

			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
			String createdByUser = validation.getCreatedBy();
			ListDataSource ld = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			// Integer domain_Id = (Integer) ld.getDomain();
			Long project_Id = Long.valueOf(ld.getProjectId());
			Integer domain_Id = domainId;
			if (apptype.equals("File Monitoring") || apptype.equals("Schema Matching")) {
				// if file monitoring getting credentials from session
				// domain_Id = domainId;
				project_Id = validation.getProjectId();
			}

			long idApp = validationcheckdao.insertintolistapplications(name, description, apptype, idData, idUser,
					matchingThreshold, incrementalMatching, dateFormat, leftSliceEnd, fileMonitoringType,
					continuousFileMonitoring, project_Id, createdByUser, domain_Id, data_Domain_id, stringStatCheck,
					validationJobSize);
			int updateApplicationNameWithIdApp = validationcheckdao.updateApplicationNameWithIdApp(name, idApp);
			LOG.debug("updateApplicationNameWithIdApp=" + updateApplicationNameWithIdApp);

			// Save validation creation status to alert_log
			if (idApp > 0l) {
				// changes regarding Audit trail
				iTaskDAO.addAuditTrailDetail(idUser, userToken.getUserName(), DatabuckConstants.DBK_FEATURE_VALIDATION,
						formatter.format(new Date()), idApp, DatabuckConstants.ACTIVITY_TYPE_CREATED, name);
				integrationMasterService.saveAlertEventLog("", 1l, project_Id, idApp, name, TaskTypes.validation,
						AlertManagement.Validation_Creation_Success, "success", createdByUser, null);
			} else {
				JSONArray mappingErros = new JSONArray();
				mappingErros.put("Exception occured while inserting data into listApplications");
				integrationMasterService.saveAlertEventLog("", 1l, project_Id, 0l, name, TaskTypes.validation,
						AlertManagement.Validation_Creation_Failure, "failed", createdByUser, mappingErros);
			}

			LOG.debug("idApp=" + idApp);
			boolean applyRulesEnabled = false;
			if (idApp > 0) {
				int count = validationcheckdao.getGlobalRulesCountByTemplateId(idData);
				if (count > 0) {
					LOG.debug("\n====>Enabling Apply rules for validation:" + idApp);
					applyRulesEnabled = validationcheckdao.enableApplyRules(idApp);
				}
			}

			String applicationName = validationcheckdao.getNameFromListDataSources(idData);

			LOG.debug("applicationName=" + applicationName);

			if (apptype.equals("File Monitoring") && (fileMonitoringType.equalsIgnoreCase("snowflake")
					|| fileMonitoringType.equalsIgnoreCase("azuredatalakestoragegen2batch")
					|| fileMonitoringType.equalsIgnoreCase("S3 Batch"))) {
				if (fm_connectionId != null) {
					long schemaId = Long.parseLong(fm_connectionId);
					int checkdata = validationcheckdao.checkFMConnectionDetailsForConnection(schemaId);
					LOG.debug("checkdata=" + checkdata);

					if (checkdata > 1)
						validationcheckdao.updateFMConnectionDetails(idApp, schemaId);
					else
						validationcheckdao.insertFMConnectionDetails(idApp, schemaId);
				}
			}
			if (apptype.equals("Data Forensics")) {

				int insertintolistdftranrule = validationcheckdao.insertintolistdftranrule(idApp, "N", 0.0, "N", 0.0);

				Map<String, Object> result = new HashMap<String, Object>();
				String dataLocation = validationcheckdao.getDataLocationInListDataSources(idData);
				LOG.debug("dataLocation=" + dataLocation);
				if (dataLocation.equalsIgnoreCase("HDFS") || dataLocation.equalsIgnoreCase("FILESYSTEM")) {
					LOG.info("finishSchemaCustomization");
					// result.setViewName("finishSchemaCustomization");
					result.put("validationName", name);
					result.put("filesystem", true);
				} else {
					LOG.info("finishSchemaCustomize");
					// result.setViewName("finishSchemaCustomization");
					result.put("validationName", name);
					result.put("filesystem", false);
				}

				if (dataLocation.equalsIgnoreCase("Kafka")) {

					int windowTime = validation.getWindowTime();
					String startTime = validation.getStartTime();
					String endTime = validation.getEndTime();

					LOG.debug("idApp =>" + idApp);
					LOG.debug("windowTime =>" + windowTime);
					LOG.debug("startTime =>" + startTime);
					LOG.debug("endTime =>" + endTime);

					templateviewdao.updatelistApplicationsForKafka(idApp, windowTime, startTime, endTime);

					LOG.info("updated LA for KAFKA........");

				}

				/*
				 * Pradeep 8-Mar-2020 Global threshold changes: drop down list JSON data is send
				 * to load in select control on page
				 */
				Map<String, JSONArray> oThresholdOptions = JwfSpaInfra.getAppListsOptionsMap(jdbcTemplate,
						"GLOBAL_THRESHOLDS_OPTION");
				JSONArray aThresholdOptions = oThresholdOptions.get("GLOBAL_THRESHOLDS_OPTION");

				DateUtility.DebugLog("createValidationCheckAjax 01", aThresholdOptions.toString());

				result.put("ThresholdOptions", aThresholdOptions.toString());
				result.put("idApp", idApp);
				result.put("idData", idData);

				if (applyRulesEnabled)
					result.put("applyRulesEnabled", "Y");
				else
					result.put("applyRulesEnabled", "N");

				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Schema Matching")) {
				Map<String, Object> result = new HashMap<String, Object>();
				Long schemaid1 = validation.getSchemaid1();
				Long schemaid2 = validation.getSchemaid2();
				String prefix1 = validation.getPrefix1();
				String prefix2 = validation.getPrefix2();

				String schematypename = validation.getSchematypename();

				if (schematypename.equalsIgnoreCase("metadata")) {
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, null, null, prefix1, prefix2);
				} else if (schematypename.equalsIgnoreCase("RC")) {
					String threasholdType = validation.getSchemaThresholdtype();
					String rcThreshold = validation.getSchemaRc();

					LOG.debug("threasholdType:" + threasholdType);
					LOG.debug("rcThreshold:" + rcThreshold);
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, threasholdType, rcThreshold, prefix1, prefix2);

				} else if (schematypename.equalsIgnoreCase("both")) {
					String threasholdType = validation.getSchemaThresholdtype();
					String rcThreshold = validation.getSchemaRc();
					templateviewdao.updatelistApplicationsForSchemamatching(idApp, schemaid1, schemaid2, schematypename,
							name, threasholdType, rcThreshold, prefix1, prefix2);

					long idApp1 = validationcheckdao.insertintolistapplications(name, description, apptype, idData,
							idUser, matchingThreshold, incrementalMatching, dateFormat, leftSliceEnd,
							fileMonitoringType, continuousFileMonitoring, project_Id, createdByUser, domain_Id,
							data_Domain_id, "N", validationJobSize);

					templateviewdao.updatelistApplicationsForSchemamatchingForBoth_RC(idApp1, schemaid1, schemaid2,
							schematypename, name, threasholdType, rcThreshold);
				}

				// update listapplications

				LOG.debug("schema ids :" + schemaid1 + "   " + schemaid2);

				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Data Matching")) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				if (incrementalMatching.equalsIgnoreCase("Y")) {
					result.put("incrementalMatching", incrementalMatching);
					result.put("Source2DateFormat", true);
				} else {
					result.put("incrementalMatching", "N");
				}
				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				LOG.debug("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					result.put("matchingRulesTrue", true);
				}
				result.put("matchingRules", matchingRules);
				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("listRefFunctionsname", listRefFunctionsname);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("currentSection", "Validation Check");
				// model.addObject("currentLink", "VCView");
				// result.setViewName("matchKeyCreateView");

				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Rolling DataMatching")) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				LOG.debug("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					result.put("matchingRulesTrue", true);
				}
				result.put("matchingRules", matchingRules);
				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("listRefFunctionsname", listRefFunctionsname);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("currentSection", "Validation Check");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Data Matching Group")) {
				Map<String, Object> result = new HashMap<String, Object>();
				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("File Management")) {
				Map<String, Object> result = new HashMap<String, Object>();
				LOG.info("File Management");
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Statistical Matching")) {
				Map<String, Object> result = new HashMap<String, Object>();
				LOG.info("Statistical Matching");
				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("listRefFunctionsname", listRefFunctionsname);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("currentSection", "Validation Check");
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Primary Key Matching")) {
				Map<String, Object> result = new HashMap<String, Object>();

				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List primaryId = validationcheckdao.getPrimaryKeyListDataDefinition(idData);
				LOG.debug("=======================>primaryId=" + primaryId);
				ObjectMapper mapper = new ObjectMapper();
				String jsonprimaryId = "";
				try {
					jsonprimaryId = mapper.writeValueAsString(primaryId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				result.put("jsonprimaryId", jsonprimaryId);
				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("listRefFunctionsname", listRefFunctionsname);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("currentSection", "Validation Check");
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Model Governance")) {
				Map<String, Object> result = new HashMap<String, Object>();
				LOG.info("Model Governance");
				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao
						.getlistdatasourcesname(validation.getProjectId());
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

				result.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				result.put("listRefFunctionsname", listRefFunctionsname);
				result.put("getlistdatasourcesname", getlistdatasourcesname);
				result.put("currentSection", "Validation Check");
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("Model Governance Dashboard")) {
				Map<String, Object> result = new HashMap<String, Object>();
				LOG.info("Model Governance Dashboard");
				Map<Long, String> decileEqualityAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Equality");
				Map<Long, String> decileConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Consistency");
				Map<Long, String> scoreConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Score Consistency");
				result.put("decileEqualityAppType", decileEqualityAppType);
				result.put("decileConsistencyAppType", decileConsistencyAppType);
				result.put("scoreConsistencyAppType", scoreConsistencyAppType);
				result.put("idApp", idApp);
				result.put("idData", idData);
				result.put("apptype", apptype);
				result.put("applicationName", applicationName);
				result.put("description", description);
				result.put("name", name);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else if (apptype.equals("File Monitoring")) {
				Map<String, Object> result = new HashMap<String, Object>();
				LOG.info("File Monitoring......");
				LOG.debug("in Val Check idApp->" + idApp);
				result.put("idApp", idApp);
				result.put("currentSection", "Validation Check");
				result.put("currentLink", "Add New");
				result.put("fileMonitoringType", fileMonitoringType);
				if (fm_connectionId == null || fm_connectionId.trim().isEmpty())
					fm_connectionId = "-1";
				result.put("connectionId", fm_connectionId);
				responseMap.put("result", result);
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseMap.put("message", "Error occurred. Validation not created");
			responseMap.put("stackTrace", ex.getMessage());
			responseMap.put("status", "failed");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/createValidationCheck - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateValidationCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> updateValidationCheck(@RequestBody ListApplications listApplications,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/updateValidationCheck - START");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		boolean flag = true;
		String token = null;
		responseMap.put("status", "failed");
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			responseMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		/*
		 * Pradeep 8-Mar-2020 Global threshold changes: Debug log only, no code change
		 * to this method at all just View log for new field 'thresholdsApplyOption'
		 * reach from UI to 'listApplications' variable or not. so this object carry new
		 * field via updateintolistapplicationForAjaxRequest() parameter to dao save
		 * code
		 */
		try {
			ObjectMapper oMapper = new ObjectMapper();
			String sListApplication = oMapper.writeValueAsString(listApplications);
			DateUtility.DebugLog("updateValidationCheckAjax 01",
					String.format("thresholdsApplyOption = %1$s \n full submitted data = %2$s",
							listApplications.getThresholdsApplyOption(), sListApplication));
		} catch (Exception oException) {
			oException.printStackTrace();
		}

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
		doFeatureFlagsSanitizaton(listApplications);

		int dataDomainId = validationcheckdao.getCurrentDataDomainForIdApp(listApplications.getIdApp());
		LOG.debug("@@@@@@@@2 getCurrentDataDomainForIdApp =>" + dataDomainId);
		listApplications.setData_domain(dataDomainId);

		if (flag) {
			boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
					listApplications);
			LOG.debug("applyRulesFlag=" + applyRulesFlag);
			if (!applyRulesFlag) {
				flag = false;
				responseMap.put("message", "The rules are configured incorrectly");
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean applyDerivedColumnsFlag = validationcheckdao
					.checkTheConfigurationForapplyDerivedColumns(listdatadefinition, listApplications);
			LOG.debug("applyDerivedColumnsFlag=" + applyDerivedColumnsFlag);
			if (!applyDerivedColumnsFlag) {
				flag = false;
				responseMap.put("message", "The Derived Columns are configured incorrectly");
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			if (listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")) {
				if (listApplications.getHistoricStartDate() == null
						|| listApplications.getHistoricStartDate().trim().isEmpty()
						|| listApplications.getHistoricEndDate() == null
						|| listApplications.getHistoricEndDate().isEmpty()) {
					flag = false;
					responseMap.put("message", "Start Date or End Date is missing");
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}
		if (flag) {
			boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForBuildHistoric(listdatadefinition,
					listApplications);
			LOG.debug("buildHistoricFlag=" + buildHistoricFlag);
			if (!buildHistoricFlag) {
				flag = false;
				responseMap.put("message", "The last read time check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {

			boolean identityFlag = validationcheckdao.checkTheConfigurationForDupRowIdentity(listdatadefinition,
					listApplications);
			LOG.debug("identityFlag=" + identityFlag);
			if (!identityFlag) {
				flag = false;
				responseMap.put("message", "The duplicate identity check (primary key) is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean AllFlag = validationcheckdao.checkTheConfigurationForDupRowAll(listdatadefinition,
					listApplications);
			LOG.debug("AllFlag=" + AllFlag);
			if (!AllFlag) {
				flag = false;
				responseMap.put("message", "The duplicate all check (duplicate key) is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean dataDriftFlag = validationcheckdao.checkTheConfigurationForDataDrift(listdatadefinition,
					listApplications);
			LOG.debug("dataDriftFlag=" + dataDriftFlag);
			if (!dataDriftFlag) {
				flag = false;
				responseMap.put("message", "The data drift check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean numFieldFlag = validationcheckdao.checkTheConfigurationForNumField(listdatadefinition,
					listApplications);
			LOG.debug("numFieldFlag=" + numFieldFlag);
			if (!numFieldFlag) {
				flag = false;
				responseMap.put("message", "The numerical fingerprint check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean stringFieldFlag = validationcheckdao.checkTheConfigurationForstringField(listdatadefinition,
					listApplications);
			LOG.debug("stringFieldFlag=" + stringFieldFlag);
			if (!stringFieldFlag) {
				flag = false;
				responseMap.put("message", "The string fingerprint check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			// System.out.println("recordcountanomaly="+listApplications.getRecordCountAnomaly());
			// System.out.println("getkeyBasedRecordCountAnomaly="+listApplications.getkeyBasedRecordCountAnomaly());
			boolean dGroupFlag = validationcheckdao.checkTheConfigurationFordGroup(listdatadefinition,
					listApplications);
			LOG.debug("dGroupFlag=" + dGroupFlag);
			if (!dGroupFlag) {
				flag = false;
				responseMap.put("message", "The Microsegment check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean recordAnomalyFlag = validationcheckdao.checkTheConfigurationForRecordAnomaly(listdatadefinition,
					listApplications);
			LOG.debug("recordAnomalyFlag=" + recordAnomalyFlag);
			if (!recordAnomalyFlag) {
				flag = false;
				responseMap.put("message", "The record anomaly check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean nonNullFlag = validationcheckdao.checkTheConfigurationForNonNullField(listdatadefinition,
					listApplications);
			LOG.debug("nonNullFlag=" + nonNullFlag);
			if (!nonNullFlag) {
				flag = false;
				responseMap.put("message", "The Null check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		// incremental check in VC source should not have incremental select in
		// DT pre check
		if (flag) {
			if (listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
				Long idData = listApplications.getIdData();
				boolean incrementalCheck = validationcheckdao.checkForIncrementalInListDataAccess(idData);
				if (!incrementalCheck) {
					flag = false;
					responseMap.put("message",
							"Cannot use data slices in source with Historic and Incremental Type of Application");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}
		//
		if (flag) {
			if (listApplications.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
				boolean timelinessKeyFlag = validationcheckdao
						.checkTheConfigurationForTimelinessKeyField(listdatadefinition, listApplications);
				LOG.debug("timelinessKeyFlag=" + timelinessKeyFlag);
				if (!timelinessKeyFlag) {
					flag = false;
					responseMap.put("message", "The TimelinessKey check is configured incorrectly");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}
		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.checkTheConfigurationDefaultCheck(listdatadefinition,
					listApplications);
			LOG.debug("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				responseMap.put("message", "The Default check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}
		if (flag) {
			boolean patternCheckFlag = validationcheckdao.checkTheConfigurationPatternCheck(listdatadefinition,
					listApplications);
			LOG.debug("patternCheckFlag=" + patternCheckFlag);
			if (!patternCheckFlag) {
				flag = false;
				responseMap.put("message", "The Regex Pattern Check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			boolean defaultPatternCheckFlag = validationcheckdao
					.checkTheConfigurationDefaultPatternCheck(listdatadefinition, listApplications);
			LOG.debug("defaultPatternCheckFlag=" + defaultPatternCheckFlag);
			if (!defaultPatternCheckFlag) {
				flag = false;
				responseMap.put("message", "The Default Pattern check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		// Changes to allow user to select either DateRuleCheck or
		// MicrosegmentDateRuleCheck
		// only at one time
		if (flag) {
			if (listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDateRuleCheck() != null
					&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
				flag = false;
				responseMap.put("message",
						"Choose either DateRuleCheck or MicrosegmentDateRuleCheck, both can't be enabled together.");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			boolean dateRuleCheckFlag = validationcheckdao.checkTheConfigurationDateRuleCheck(listdatadefinition,
					listApplications);
			LOG.debug("dateRuleCheckFlag=" + dateRuleCheckFlag);
			if (!dateRuleCheckFlag) {
				flag = false;
				responseMap.put("message", "The Date Rule check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			boolean dgroupNonNullFlag = validationcheckdao.checkConfigurationForDGroupNullCheck(listdatadefinition,
					listApplications);
			LOG.debug("MicrosegmentNonNullFlag=" + dgroupNonNullFlag);
			if (!dgroupNonNullFlag) {
				flag = false;
				responseMap.put("message", "The Microsegment Null check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			boolean dGroupDateRuleCheckFlag = validationcheckdao
					.checkConfigurationDgroupDateRuleCheck(listdatadefinition, listApplications);
			LOG.debug("dGroupDateRuleCheckFlag=" + dGroupDateRuleCheckFlag);
			if (!dGroupDateRuleCheckFlag) {
				flag = false;
				responseMap.put("message", "The Microsegment DateRule check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		// sumeet---create
		if (flag) {
			if (listApplications.getBadData().equalsIgnoreCase("Y")) {
				boolean badDataCheckFlag = validationcheckdao.checkTheBadData(listdatadefinition, listApplications);
				LOG.debug("badDataCheckFlag=" + badDataCheckFlag);
				if (!badDataCheckFlag) {
					flag = false;
					responseMap.put("message", "The Data type check is configured incorrectly");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}

		// ---------- [priyanka 25-12-2018] --

		// chnges for length Check

		if (flag) {
			if (listApplications.getlengthCheck().equalsIgnoreCase("Y")) {

				LOG.info(
						"ValidationCheckController if ( uncom listApplications.getlengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean lengthCheck = validationcheckdao.checkForLengthCheck(listdatadefinition, listApplications);
				LOG.debug("lengthCheck=" + lengthCheck);
				if (!lengthCheck) {
					flag = false;
					responseMap.put("message", "The Length check is configured incorrectly");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}

		// -------------------

		// Max Length Check

		if (flag) {
			if (listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {

				LOG.info(
						"ValidationCheckController if ( uncom listApplications.getMaxLengthCheck().equalsIgnoreCase(\"Y\")) ...........");

				Long idData = listApplications.getIdData();
				boolean maxLengthCheck = validationcheckdao.checkForMaxLengthCheck(listdatadefinition,
						listApplications);
				LOG.debug("maxLengthCheck=" + maxLengthCheck);
				if (!maxLengthCheck) {
					flag = false;
					responseMap.put("message", "The MAX Length check is configured incorrectly");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}
		// End of Max Length Check

		if (flag) {
			boolean defaultCheckFlag = validationcheckdao.defaultCheckFlag(listdatadefinition, listApplications);
			LOG.debug("defaultCheckFlag=" + defaultCheckFlag);
			if (!defaultCheckFlag) {
				flag = false;
				responseMap.put("message", "The Default check is configured incorrectly");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		// Changes to allow user to select either DataDrift or Microsegment Based
		// DataDrift
		// only at one time
		if (flag) {
			if (listApplications.getDataDriftCheck() != null
					&& listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
					&& listApplications.getdGroupDataDriftCheck() != null
					&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
				flag = false;
				responseMap.put("message",
						"Choose either DataDrift or Microsegment Based DataDrift, both can't be enabled together.");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		}

		if (flag) {
			if (listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
				boolean microsegmentFlag = validationcheckdao.checkTheConfigurationForMicrosegment(listdatadefinition,
						listApplications);
				LOG.debug("microsegmentFlag=" + microsegmentFlag);
				if (!microsegmentFlag) {
					flag = false;
					responseMap.put("message", "The Microsegment check is configured incorrectly");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			}
		}

		if (flag) {

			Long idApp = listApplications.getIdApp();
			validationcheckdao.insertintolistdfsetruleandtranrule(idApp,
					listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());

			int updateintolistapplication = validationcheckdao
					.updateintolistapplicationForAjaxRequest(listApplications);
			LOG.debug("updateintolistapplication=" + updateintolistapplication);
			LOG.debug("datadriftthreshold=" + listApplications.getDataDriftThreshold());
			// Sumeet_21_08_2018
			int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
					listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
					listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(), "N",
					listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(), 0.0);
			LOG.debug("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

			int insertintolistdftranrule = 0;
			if (validationCheckDAO.isDuplicateCheckEnabled(idApp)) {
				System.out.println("Duplicate Check is already enabled for idApp[" + idApp + "] so updating it");
				insertintolistdftranrule = validationcheckdao.updateintolistdftranrule(idApp,
						listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
						listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());
			} else {
				System.out.println("Duplicate Check is not enabled for idApp[" + idApp + "] so inserting details");
				insertintolistdftranrule = validationcheckdao.insertintolistdftranrule(idApp,
						listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
						listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());
			}

			// Create rule catalog
			boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
			if (isRuleCatalogDiscovery)
				ruleCatalogService.createRuleCatalog(idApp);

			try {
				JSONObject json = new JSONObject();
				// put some value pairs into the JSON object .
				json.put("success", "Validation Check Created Successfully");
				responseMap.put("status", "success");
				responseMap.put("message", "Validation Check Created Successfully");

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				responseMap.put("message", "Validation Check Creation Failed");
				responseMap.put("stackTrace", e.getMessage());
				LOG.error(e.getMessage());

				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}

		}
		LOG.info("dbconsole/updateValidationCheck - END");
		return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/updateSchemaMatching", method = RequestMethod.POST)
	public ResponseEntity<Object> updateSchemaMatching(@RequestHeader HttpHeaders headers,
			@RequestBody ValidationCreateReq validation) {
		LOG.info("dbconsole/updateSchemaMatching - START");
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");

			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			responseMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");

			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			if (validation.getSchematypename().equalsIgnoreCase("metadata")) {
				templateviewdao.updateSchemamatching(validation.getIdApp(), validation.getDescription(), "", "0.0",
						validation.getPrefix1(), validation.getPrefix2());
			} else {
				String threasholdType = validation.getSchemaThresholdtype();
				String rcThreshold = validation.getSchemaRc();
				LOG.debug("threasholdType:" + threasholdType);
				LOG.debug("rcThreshold:" + rcThreshold);
				templateviewdao.updateSchemamatching(validation.getIdApp(), validation.getDescription(), threasholdType,
						rcThreshold, validation.getPrefix1(), validation.getPrefix2());
			}
			responseMap.put("message", "Successfully updated the schema matching details.");
			responseMap.put("status", "success");
			LOG.info("Successfully updated the schema matching details.");
		} catch (Exception ex) {
			ex.printStackTrace();
			responseMap.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
		}
		LOG.info("dbconsole/updateSchemaMatching - END");
		return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
	}

	private static void doFeatureFlagsSanitizaton(ListApplications oListApplication) {
		BeanInfo oBeanInfo = null;
		PropertyDescriptor aPropertyDescriptor[] = null;
		Method oSetterMethod = null;
		String sPropName, sPropValue;
		String sColumnNames = "nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,lengthCheck,maxLengthCheck,correlationcheck,timelinessKeyCheck,defaultCheck,patternCheck,dGroupDateRuleCheck,badData,dGroupDataDriftCheck,continuousFileMonitoring";

		DateUtility.DebugLog("doFeatureFlagsSanitizaton 01", "Begin");

		try {
			oBeanInfo = Introspector.getBeanInfo(oListApplication.getClass());
			aPropertyDescriptor = oBeanInfo.getPropertyDescriptors();

			for (PropertyDescriptor oPropertyDescriptor : aPropertyDescriptor) {
				sPropName = oPropertyDescriptor.getName();
				if (sColumnNames.indexOf(sPropName) > -1) {
					sPropValue = (String) oPropertyDescriptor.getReadMethod().invoke(oListApplication);
					oSetterMethod = oPropertyDescriptor.getWriteMethod();

					if ((sPropValue == null) || (sPropValue.isEmpty())) {
						oSetterMethod.invoke(oListApplication, "N");
						DateUtility.DebugLog("doFeatureFlagsSanitizaton 02", String.format(
								"Replacement done for Property Name = %1$s, Original Value = %2$s, Replaced to = %3$s",
								sPropName, sPropValue, "N"));
					} else {
						DateUtility.DebugLog("doFeatureFlagsSanitizaton 02",
								String.format("No replacement done for Property Name = %1$s, as Original Value = %2$s",
										sPropName, sPropValue));
					}
				}
			}

		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/dbconsole/saveDataIntoListDMCriteria", method = RequestMethod.POST)
	public ResponseEntity<Object> saveDataIntoListDMCriteria(@RequestHeader HttpHeaders headers,
			@RequestBody KeyMeasurementValUpdate validation) {
		LOG.info("dbconsole/saveDataIntoListDMCriteria - START");
		validation.setExpression(validation.getExpression().replaceAll("\\s", "").trim());
		LOG.debug("expression=" + validation.getExpression());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");

			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			responseMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");

			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		boolean flag = true;
		List<String> leftMatchValueCols = validationcheckdao.getMatchValueColumns(validation.getLeftSourceId());
		List<String> rightMatchValueCols = validationcheckdao.getMatchValueColumns(validation.getRightSourceId());

		List<String> leftTemplatecolumns = validationcheckdao.getDisplayName(validation.getLeftSourceId());
		List<String> rightTemplatecolumns = validationcheckdao.getDisplayName(validation.getRightSourceId());

		List<String> leftMatchingRulesColumn = new ArrayList<String>();
		List<String> rightMatchingRulesColumn = new ArrayList<String>();
		String[] matchingColumnRules = validation.getExpression().split("&&");
		if (!validation.getMatchingRuleAutomatic().equalsIgnoreCase("Y")) {
			for (int i = 0; i < matchingColumnRules.length; i++) {
				String[] displayName = matchingColumnRules[i].split("=");
				leftMatchingRulesColumn.add(displayName[0]);
				rightMatchingRulesColumn.add(displayName[1]);
			}
		}

		if (validation.getMeasurementid().equalsIgnoreCase("Y")) {
			boolean leftTemplateflag = false;
			if (leftMatchValueCols != null && leftMatchValueCols.size() >= 1) {
				leftTemplateflag = true;
			}
			LOG.debug("leftTemplateflag=" + leftTemplateflag);

			if (!leftTemplateflag) {
				flag = false;
				responseMap.put("message",
						"Please select atleast one match value column for your Validation Check in First Data Template.");
				responseMap.put("templateId", validation.getLeftSourceId());
			}

			if (flag) {
				boolean rightTemplateflag = false;
				if (rightMatchValueCols != null && rightMatchValueCols.size() >= 1) {
					rightTemplateflag = true;
				}
				LOG.debug("rightTemplateflag=" + rightTemplateflag);
				if (!rightTemplateflag) {
					flag = false;
					responseMap.put("message",
							"Please select atleast one match value column for your Validation Check in Second Data Template.");
					responseMap.put("templateId", validation.getRightSourceId());
				}
			}

			if (flag) {
				// Check if the size of the match values column is same on left and right
				if (leftMatchValueCols.size() != rightMatchValueCols.size()) {
					flag = false;
					responseMap.put("message", "Please select same number of match value column in both templates.");
					responseMap.put("templateId", validation.getRightSourceId());
				}

			}

			if (flag) {
				if (leftMatchValueCols.size() > 1 && rightMatchValueCols.size() > 1) {
					Collections.sort(leftMatchValueCols);
					Collections.sort(rightMatchValueCols);
					List<String> leftMatchValueCols_1 = leftMatchValueCols.stream().map(String::toUpperCase)
							.collect(Collectors.toList());
					List<String> rightMatchValueCols_1 = rightMatchValueCols.stream().map(String::toUpperCase)
							.collect(Collectors.toList());
					// Check if the match value columns are matching or not
					if (!leftMatchValueCols_1.equals(rightMatchValueCols_1)) {
						List<String> leftMatchValueCols_temp = new ArrayList<String>(leftMatchValueCols_1);
						leftMatchValueCols_temp.removeAll(rightMatchValueCols_1);
						flag = false;
						responseMap.put("message",
								"Please make sure match value columns names match in both templates. "
										+ Arrays.toString(leftMatchValueCols_temp.toArray())
										+ " columns are missing in second template.");
						responseMap.put("templateId", validation.getRightSourceId());
					}
				}
			}
		}
		if (flag) {
			// checking matching Rule columns are present
			if (leftMatchingRulesColumn.size() >= 1 && leftMatchingRulesColumn != null) {
				List<String> leftTemplatecolumns_1 = new ArrayList<>(leftTemplatecolumns);
				List<String> missingColumsList = new ArrayList<String>();
				for (String lcol : leftMatchingRulesColumn) {
					if (!leftTemplatecolumns_1.contains(lcol)) {
						missingColumsList.add(lcol);
					}
				}

				if (missingColumsList != null && missingColumsList.size() > 0) {
					validation.setExpression(null);
					flag = false;
					responseMap.put("message", "Please make sure match Rule columns names match in templates. "
							+ Arrays.toString(missingColumsList.toArray()) + " columns are missing in First template.");
					responseMap.put("match", "Yes");
					responseMap.put("templateId", validation.getLeftSourceId());
				}
			}

		}
		if (flag) {
			// checking matching Rule columns are present
			if (rightMatchingRulesColumn.size() >= 1 && rightMatchingRulesColumn != null) {
				List<String> rightTemplatecolumns_1 = new ArrayList<>(rightTemplatecolumns);
				List<String> missingColumsList = new ArrayList<String>();
				for (String rcol : rightMatchingRulesColumn) {
					if (!rightTemplatecolumns_1.contains(rcol)) {
						missingColumsList.add(rcol);
					}
				}

				if (missingColumsList != null && missingColumsList.size() > 0) {
					validation.setExpression(null);
					flag = false;
					responseMap.put("message",
							"Please make sure match Rule columns names match in templates. "
									+ Arrays.toString(missingColumsList.toArray())
									+ " columns are missing in Second template.");
					responseMap.put("match", "Yes");
					responseMap.put("templateId", validation.getRightSourceId());
				}
			}

		}

		//////
		if (flag) {
			if (validation.getGroupbyid().equalsIgnoreCase("Y")) {
				boolean leftTemplateflag = validationcheckdao.checkWhetherTheSameDgroupAreSelectedInDataTemplate(
						validation.getExpression().trim(), validation.getLeftSourceId());
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				LOG.debug("leftTemplateflag=" + leftTemplateflag);
				if (!leftTemplateflag) {
					flag = false;
					responseMap.put("message",
							"Subsegment(s) should be from the key matching column(s). Please configure again.");
					responseMap.put("templateId", validation.getLeftSourceId());
				}
			}
		}
		LOG.debug("incrementalMatching=" + validation.getIncrementalMatching());
		if (flag) {
			if (validation.getIncrementalMatching().equalsIgnoreCase("Y")) {
				boolean incrementalLeft = validationcheckdao
						.checkWhetherIncrementalForLeftTemplate(validation.getLeftSourceId());
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				LOG.debug("incrementalLeft=" + incrementalLeft);
				if (!incrementalLeft) {
					flag = false;
					responseMap.put("message",
							"Please select atleast one Last Read Time column for First Source Template.");
					responseMap.put("templateId", validation.getLeftSourceId());

				}
			}
		}
		if (flag) {
			if (validation.getIncrementalMatching().equalsIgnoreCase("Y")) {
				boolean incrementalLeft = validationcheckdao
						.checkWhetherIncrementalForLeftTemplate(validation.getRightSourceId());
				// boolean leftTemplateflag =
				// validationcheckdao.checkDgroupColumnForLeftTemplate(leftSourceId);
				LOG.debug("incrementalLeft=" + incrementalLeft);
				if (!incrementalLeft) {
					flag = false;
					responseMap.put("message",
							"Please select atleast one Last Read Time column for Second Source Template.");
					responseMap.put("templateId", validation.getRightSourceId());

				}
			}
		}
		validationcheckdao.deleteEntryFromListDMRules(validation.getIdApp(), "Primary Key Matching");
		if (flag) {
			// primaryKeyMatching
			if (validation.getPrimaryKey().equalsIgnoreCase("Y")) {
				Long primaryKeyidDM = validationcheckdao.getIddmFromListDMRules(validation.getIdApp(),
						"Primary Key Matching");
				if (primaryKeyidDM == 0) {
					primaryKeyidDM = validationcheckdao.insertIntoListDMRules(validation.getIdApp(),
							"Primary Key Matching", "One to One");
				}
				System.out.println("primaryKeyidDM=" + primaryKeyidDM);
				Long primaryKeyMatchingUpdate = validationcheckdao.updateListDMRulesForPrimaryKeyMatching(
						primaryKeyidDM, validation.getIdApp(), validation.getRightSourceId(),
						validation.getLeftSourceId());
				if (primaryKeyMatchingUpdate <= 0) {
					flag = false;
					responseMap.put("leftsource",
							"Please select atleast one Primary Key column for First Source Template.");
					responseMap.put("templateId", validation.getLeftSourceId());

				}
			}
		}
		if (flag) {
			LOG.debug("rightSliceEnd=" + validation.getRightSliceEnd());
			LOG.debug("measurementid=" + validation.getMeasurementid());
			LOG.debug("idApp=" + validation.getIdApp());
			// System.out.println("idDM=" + idDM);
			LOG.debug("rightSourceId=" + validation.getRightSourceId());
			LOG.debug("leftSourceId=" + validation.getLeftSourceId());
			LOG.debug("expression=" + validation.getExpression());
			LOG.debug("matchingRuleAutomatic=" + validation.getMatchingRuleAutomatic());
			validationcheckdao.deleteEntryFromListDMRules(validation.getIdApp(), "Measurements Match");
			Long idDM = validationcheckdao.getIddmFromListDMRules(validation.getIdApp(), "Key Fields Match");
			if (idDM == 0) {
				idDM = validationcheckdao.insertIntoListDMRules(validation.getIdApp(), validation.getMatchType(),
						"One to One");
				LOG.debug("idDM=" + idDM);
			}

			if (validation.getMeasurementid().equalsIgnoreCase("Y")) {
				Long measurementIdDM = validationcheckdao.insertIntoListDMRules(validation.getIdApp(),
						"Measurements Match", "One to One");
				validationcheckdao.insertDataIntoListDMCriteriaForMeasurementMatch(measurementIdDM, leftMatchValueCols,
						rightMatchValueCols);
			}
			validationcheckdao.updateDataIntoListApplications(validation.getIdApp(), validation.getRightSourceId(),
					validation.getAbsoluteThresholdId(), validation.getGroupbyid(), validation.getMeasurementid(),
					validation.getDateFormat(), validation.getRightSliceEnd(), validation.getExpression(),
					validation.getMatchingRuleAutomatic(), validation.getRecordCount(), validation.getPrimaryKey(),
					validation.getUnMatchedAnomalyThreshold());
			if (validation.getMatchingRuleAutomatic().equalsIgnoreCase("Y")) {
				List<String> rightSourceColumnNames = validationcheckdao
						.getDisplayNamesFromListDataDefinition(validation.getRightSourceId());
				List<String> LeftSourceColumnNames = validationcheckdao
						.getDisplayNamesFromListDataDefinition(validation.getLeftSourceId());
				int updateMatchingRuleAutomatic = validationcheckdao.updateMatchingRuleAutomaticIntoListDMCriteria(
						rightSourceColumnNames, LeftSourceColumnNames, idDM, validation.getIdApp());
				LOG.debug("updateMatchingRuleAutomatic=" + updateMatchingRuleAutomatic);

				if (updateMatchingRuleAutomatic > 0) {
					responseMap.put("message", "Match Key created successfully");
					responseMap.put("status", "success");
				} else {
					responseMap.put("message", "No Matching key found");
				}
			} else if (validation.getExpression() != "") {
				int insertDataIntoListDMCriteria = validationcheckdao.insertDataIntoListDMCriteria(
						validation.getExpression().trim(), idDM, validation.getIdApp(), leftMatchValueCols,
						rightMatchValueCols);
				LOG.debug("insertDataIntoListDMCriteria=" + insertDataIntoListDMCriteria);

				if (insertDataIntoListDMCriteria >= 1) {
					responseMap.put("message", "Match Key created successfully");
					responseMap.put("status", "success");
					LOG.info("Match Key created successfully");
				} else if (insertDataIntoListDMCriteria <= 0) {
					responseMap.put("message", "Matching key already exists");
					LOG.error("Matching key already exists");
				} else {
					responseMap.put("message", "No Matching key found");
					LOG.error("No Matching key found");
				}
			} else {
				responseMap.put("message", "No Matching key found");
				LOG.error("No Matching key found");
			}
		}
		LOG.info("dbconsole/saveDataIntoListDMCriteria - END");
		return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/changeDataColumn", method = RequestMethod.POST)
	public ResponseEntity<Object> changeDataColumnAjax(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/changeDataColumn - START");
		Map<String, Object> response = new HashMap<>();
		LOG.debug("idData=" + params.get("idData"));
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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
			response.put("result", validationcheckdao.getDisplayNamesFromListDataDefinition(params.get("idData")));
			response.put("message", "Successfully got the details.");
			response.put("status", "success");
			LOG.info("Successfully got the details.");

			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to get the details.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/changeDataColumn - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/copyValidation")
	public ResponseEntity<Object> copyValidation(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/copyValidation - START");
		Map<String, Object> response = new HashMap<String, Object>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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
		LOG.debug("Getting request parameters  " + params);
		String newValidationName = String.valueOf(params.get("newValidationName"));
		String createdByUser = String.valueOf(params.get("createdBy"));
		long idApp = Long.parseLong(String.valueOf(params.get("idApp")));
		try {
			Long newIdApp = validationcheckdao.copyValidation(idApp, newValidationName, createdByUser);

			if (newIdApp != null && newIdApp > 0l) {
				// changes regarding Audit trail
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_VALIDATION, formatter.format(new Date()), newIdApp,
						DatabuckConstants.ACTIVITY_TYPE_CREATED, newValidationName);

				// Create rule catalog
				boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
				if (isRuleCatalogDiscovery) {
					ruleCatalogDao.copyRuleCatalog(idApp, newIdApp);
					ruleCatalogDao.copyRuleTagMapping(idApp, newIdApp);
				}
				response.put("message", "Validation Check Copied Successfully");
				response.put("status", "Success");
				LOG.info("Validation Check Copied Successfully");

				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else
				response.put("message", "Validation Check Copied failed");
			response.put("status", "failed");
			LOG.error("Validation Check Copied failed");

			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("message", "Failed to fetch Validation Check Copied.");
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/copyValidation - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/changeValidationStatus")
	public ResponseEntity<Object> inactivateValidation(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/changeValidationStatus - START");
		Map<String, Object> response = new HashMap<String, Object>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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
			LOG.debug("Getting request parameters  " + params);
			String activeStatusSql = "select active from listApplications where idApp = " + params.get("idApp");
			SqlRowSet rs1 = jdbcTemplate.queryForRowSet(activeStatusSql);
			String activeStatus = "";
			while (rs1.next()) {
				activeStatus = rs1.getString(1);
			}
			if (activeStatus.equals("yes")) {
				String inacctivateSql = "update listApplications set active = 'no' where idApp = "
						+ params.get("idApp");
				jdbcTemplate.execute(inacctivateSql);
			}
			if (activeStatus.equals("no")) {
				ListDataSource ld = validationcheckdao.getTemplateDetailsForAppId(params.get("idApp"));
				List<ListDataSchema> ls = listdatasourcedao.getListDataSchemaId(ld.getIdDataSchema());
				if (ld != null && ls != null) {
					boolean activate = false;
					Map<String, Object> result = new HashMap<>();
					String message = "Re-activate its respective ";
					if (!ls.isEmpty() && ls.get(0).getAction().equalsIgnoreCase("no")) {
						activate = true;
						message += "connection(" + ls.get(0).getSchemaName() + ") ";
						result.put("connectionId", ls.get(0).getIdDataSchema());
						result.put("connectionName", ls.get(0).getSchemaName());
					}
					if (ld.getActive().equalsIgnoreCase("no")) {
						activate = true;
						message += "and template(" + ld.getName() + ") ";
						result.put("templateId", ld.getIdData());
						result.put("templateName", ld.getName());
					}
					message += "to re-activate this Validation.";
					if (activate) {
						response.put("message", message.trim().replaceAll("and", ""));
						response.put("status", "failed");
						result.put("activate", true);
						response.put("result", result);
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
				String acctivateSql = "update listApplications set active = 'yes' where idApp = " + params.get("idApp");
				jdbcTemplate.execute(acctivateSql);
			}
			response.put("message", "Status changed Successfully");
			response.put("status", "Success");
			LOG.info("Status changed Successfully");

			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/changeValidationStatus - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/customizeValidation", method = RequestMethod.POST)
	public ResponseEntity<Object> customizeValidation(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/customizeValidation - START");
		Map<String, Object> resMap = new HashMap<>();
		resMap.put("status", "failed");
		Long projectId = Long.parseLong(String.valueOf(params.get("projectId")));
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			resMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");

			return new ResponseEntity<Object>(resMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			resMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");

			return new ResponseEntity<Object>(resMap, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			LOG.debug("idApp =====>>>> " + params.get("idApp"));
			Long idApp = Long.parseLong(String.valueOf(params.get("idApp")));
			String name = String.valueOf(params.get("name"));
			LOG.debug("applicationName=" + name);

			String applicationName = String.valueOf(params.get("appName"));
			LOG.debug("applicationName=.........................." + applicationName);
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String appType = validationcheckdao.getAppTypeFromListApplication(idApp);

			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
			boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);

			ListApplications listApplicationsData = null;
			Multimap<String, Double> map = null;
			if (isRuleCatalogEnabled && validationStatingEnabled) {
				listApplicationsData = ruleCatalogDao.getDataFromStagingListapplications(idApp);
				map = ruleCatalogDao.getDataFromStagingListDfTranRule(idApp);
			} else {
				listApplicationsData = validationcheckdao.getdatafromlistapplications(idApp);
				map = validationcheckdao.getDataFromListDfTranRule(idApp);
			}

			// added Data_Domain for executive summary

			List<DataDomain> lstDataDomain = validationcheckdao.getAllDataDomainNames();
			LOG.debug("$$$$$$$$$$$$ Test Data Domain =>" + lstDataDomain);

			int data_domain = listApplicationsData.getData_domain();

			LOG.debug("$$$$$$$$$$$$ Test Data Domain =>" + data_domain);

			LOG.debug("appType=" + appType);
			Map<String, Object> response = new HashMap();
			if (appType.equals("Schema Matching")) {
				response.put("NameValidation", listApplicationsData.getFileNameValidation());
				response.put("name", name);
				response.put("prefix1", listApplicationsData.getPrefix1());
				response.put("prefix2", listApplicationsData.getPrefix2());
				response.put("description", listApplicationsData.getDescription());
				response.put("RecordCountThreshold", listApplicationsData.getRecordCountAnomalyThreshold());
				response.put("entityColumn", listApplicationsData.getEntityColumn());
				response.put("recordCountAnomaly", listApplicationsData.getRecordCountAnomaly());
				response.put("description", listApplicationsData.getDescription());
				response.put("projectId", listApplicationsData.getProjectId());
				response.put("dataDomainId", listApplicationsData.getData_domain());
				List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId, null, null, null);
				long iddataSchemaLeft = listApplicationsData.getIdLeftData();
				long iddataSchemaRight = listApplicationsData.getIdRightData();
				LOG.debug("iddataSchema" + iddataSchemaLeft);
				String idDataSchemaNmeLeft = listdatasourcedao.getSchemaNameByIdData(iddataSchemaLeft);
				String idDataSchemaNmeRight = listdatasourcedao.getSchemaNameByIdData(iddataSchemaRight);
				listdataschema.removeIf(e -> e.getSchemaName().equals(idDataSchemaNmeLeft));
				listdataschema.removeIf(f -> f.getSchemaName().equals(idDataSchemaNmeRight));
				response.put("idDataSchemaNmeLeft", idDataSchemaNmeLeft);
				response.put("idDataSchemaNmeRight", idDataSchemaNmeRight);
				response.put("listdataschema1", listdataschema);
				LOG.debug("idDataSchemaNmeVal =>" + idDataSchemaNmeLeft);
			} else if (appType.equals("Data Matching")) {
				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				response.put("matchingRules", matchingRules);
				LOG.debug("matchingRulesSize=" + matchingRules.size());
				// ModelAndView model=new ModelAndView("matchTypeCreateView");
				if (matchingRules.size() >= 1) {
					response.put("matchingRulesTrue", true);
				}
				LOG.debug("matchKeyCustomizeView");
				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				// List matchingRules =
				// validationcheckdao.getMatchingRules(idApp);
				LOG.debug("matchingRulesSize=" + matchingRules.size());
				// ModelAndView model = new ModelAndView("matchKeyCreateView");
				if (matchingRules.size() >= 1) {
					response.put("matchingRulesTrue", true);
				}
				response.put("matchingRules", matchingRules);
				response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				response.put("listRefFunctionsname", listRefFunctionsname);
				response.put("getlistdatasourcesname", getlistdatasourcesname);
				response.put("currentSection", "Validation Check");
				response.put("currentLink", "VCView");
				response.put("dateFormat", listApplicationsData.getUpdateFrequency());
				response.put("matchByValue", listApplicationsData.getFileNameValidation());
				response.put("matchBySubsegment", listApplicationsData.getColOrderValidation());
				response.put("idRightData", listApplicationsData.getIdRightData());
				response.put("absoluteThreshold", listApplicationsData.getRecordCountAnomalyThreshold());
				response.put("threshold", listApplicationsData.getMatchingThreshold());
				response.put("UnMatchedAnomalyThreshold", listApplicationsData.getGroupEqualityThreshold());
				// String
				// matchkeyformula=validationcheckdao.getMatchingExpressionFromListDMRules(idApp);
				response.put("matchkeyformula", listApplicationsData.getKeyGroupRecordCountAnomaly());
				response.put("setMatchingAutomatic", listApplicationsData.getOutOfNormCheck());
				response.put("recordCount", listApplicationsData.getNumericalStatCheck());
				response.put("primaryKey", listApplicationsData.getStringStatCheck());
				response.put("incrementalMatching", listApplicationsData.getIncrementalMatching());
				// modelAndView.addObject("matchkeyformula",matchkeyformula);
				if (listApplicationsData.getIncrementalMatching().equalsIgnoreCase("Y")) {
					response.put("Source2DateFormat", true);
				}
			} else if (appType.equals("Rolling DataMatching")) {

				List matchingRules = validationcheckdao.getMatchingRules(idApp);
				response.put("matchingRules", matchingRules);
				LOG.debug("matchingRulesSize=" + matchingRules.size());
				if (matchingRules.size() >= 1) {
					response.put("matchingRulesTrue", true);
				}
				LOG.debug("editRollingDataMatching");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				if (matchingRules.size() >= 1) {
					response.put("matchingRulesTrue", true);
				}
				response.put("matchingRules", matchingRules);
				response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				response.put("listRefFunctionsname", listRefFunctionsname);
				response.put("getlistdatasourcesname", getlistdatasourcesname);
				response.put("currentSection", "Validation Check");
				response.put("currentLink", "VCView");
				response.put("idRightData", listApplicationsData.getIdRightData());
				response.put("matchkeyformula", listApplicationsData.getKeyGroupRecordCountAnomaly());
				response.put("recordCount", listApplicationsData.getNumericalStatCheck());
				response.put("primaryKey", listApplicationsData.getStringStatCheck());
				response.put("targetSchemaId", listApplicationsData.getRollTargetSchemaId());
				response.put("rollType", listApplicationsData.getRollType());

			} else if (appType.equals("Data Forensics")) {
				LOG.info("dataApplicationCustomizeView");
				List<ListDataDefinition> ListDataDefinitiondata = validationcheckdao
						.getDataFromListDataDefinition(idData);
				LOG.debug("ListDataDefinitiondata=" + ListDataDefinitiondata);
				LOG.debug("getStringStat=" + ListDataDefinitiondata.get(0).getStringStat());
				LOG.debug("StringThreshold" + ListDataDefinitiondata.get(0).getStringStatThreshold());
				response.put("listDataDefinitionData", ListDataDefinitiondata);
				Set keySet = map.keySet();
				Iterator keyIterator = keySet.iterator();
				int i = 0;
				while (keyIterator.hasNext()) {
					Object key = keyIterator.next();
					Collection values = (Collection) map.get((String) key);
					Iterator valuesIterator = values.iterator();
					while (valuesIterator.hasNext()) {
						LOG.debug("Key: " + key + ", ");
						if (i == 0) {
							LOG.debug("allDupRow=" + key);
							response.put("allDupRow", key);
							response.put("allThreshold", valuesIterator.next());
							i++;
						} else {
							LOG.debug("identityDupRow=" + key);
							response.put("identityDupRow", key);
							response.put("identityThreshold", valuesIterator.next());
						}
					}
				}
				String timeSeries = validationcheckdao.getTimeSeriesForIdApp(idApp);
				if (!timeSeries.equals("None")) {
					String[] split = timeSeries.split(",");
					for (int i1 = 0; i1 < split.length; i1++) {
						LOG.debug(split[i1]);
						if (split[i1].equalsIgnoreCase("month")) {
							LOG.info("month condition");
							response.put("month", split[i1]);
						}
						if (split[i1].equalsIgnoreCase("dayOfWeek")) {
							response.put("dayOfWeek", split[i1]);
						}
						if (split[i1].equalsIgnoreCase("hourOfDay")) {
							response.put("hourOfDay", split[i1]);
							LOG.info("hourOfDay");
						}
						if (split[i1].equalsIgnoreCase("dayOfMonth")) {
							response.put("dayOfMonth", split[i1]);
						}
					}
				} else {
					LOG.debug("None" + timeSeries);
				}

				LOG.debug("listApplicationsData=" + listApplicationsData);
				response.put("listApplicationsData", listApplicationsData);
				// updatefrequency
				String updateFrequency = listApplicationsData.getUpdateFrequency();
				LOG.debug("listApplicationsData.getUpdateFrequency();=" + updateFrequency);
				if (updateFrequency.equalsIgnoreCase("Daily") || updateFrequency.equalsIgnoreCase("Never")) {
					LOG.info("daily or never");
					response.put("updateFrequency", updateFrequency);
				} else {
					int frequencyDays = listApplicationsData.getFrequencyDays();
					LOG.debug("frequencyDays=" + frequencyDays);
					response.put("updateFrequency", updateFrequency);
					response.put("frequencyDays", frequencyDays);
				}

				response.put("stringStatStatus", listApplicationsData.getStringStatCheck());
				response.put("nullCountStatus", listApplicationsData.getNonNullCheck());
				response.put("numericalStatsStatus", listApplicationsData.getNumericalStatCheck());
				response.put("recordAnomalyStatus", listApplicationsData.getRecordAnomalyCheck());
				response.put("dataDriftStatus", listApplicationsData.getDataDriftCheck());
				response.put("outofNormStatus", listApplicationsData.getOutOfNormCheck());
				response.put("recordCountAnomalyTypeStatus", listApplicationsData.getRecordCountAnomaly());
				response.put("recordCountAnomalyThresholdStatus",
						listApplicationsData.getRecordCountAnomalyThreshold());
				response.put("applyRulesStatus", listApplicationsData.getApplyRules());
				response.put("applyDerivedColumnsStatus", listApplicationsData.getApplyDerivedColumns());
				response.put("fileNameValStatus", listApplicationsData.getFileNameValidation());
				response.put("columnOrderValStatus", listApplicationsData.getColOrderValidation());
				response.put("entityColumn", listApplicationsData.getEntityColumn());
				response.put("incrementalMatching", listApplicationsData.getIncrementalMatching());
				response.put("buildHistoricFingerPrint", listApplicationsData.getBuildHistoricFingerPrint());
				response.put("defaultCheckStatus", listApplicationsData.getDefaultCheck());
				response.put("patternCheckStatus", listApplicationsData.getPatternCheck());
				response.put("badDataStatus", listApplicationsData.getBadData());

				// priyanka 25-12-2018

				response.put("lengthCheckStatus", listApplicationsData.getlengthCheck());

				response.put("maxLengthCheckStatus", listApplicationsData.getMaxLengthCheck());

				response.put("dateRuleChkStatus", listApplicationsData.getDateRuleChk());

				// date rule changes 8jan2019 priyanka

				response.put("timelinessKeyStatus", listApplicationsData.getTimelinessKeyChk());

				response.put("dGroupNullCheckStatus", listApplicationsData.getdGroupNullCheck());

				LOG.debug("dGroupDateRuleCheckStatus****:" + listApplicationsData.getdGroupDateRuleCheck());
				response.put("dGroupDateRuleCheckStatus", listApplicationsData.getdGroupDateRuleCheck());
				response.put("defaultPatternCheckStatus", listApplicationsData.getDefaultPatternCheck());

				if (listApplicationsData.getHistoricStartDate() != null
						&& listApplicationsData.getHistoricEndDate() != null) {
					String[] StartDate = listApplicationsData.getHistoricStartDate().split(" ");
					String[] EndDate = listApplicationsData.getHistoricEndDate().split(" ");
					// System.out.println(split[0].length());
					response.put("historicStartDate", StartDate[0]);
					response.put("historicEndDate", EndDate[0]);
				} else {
					response.put("historicStartDate", listApplicationsData.getHistoricStartDate());
					response.put("historicEndDate", listApplicationsData.getHistoricEndDate());
				}

				response.put("historicDateFormat", listApplicationsData.getHistoricDateFormat());
				response.put("csvDir", listApplicationsData.getCsvDir());
				response.put("groupEquality", listApplicationsData.getGroupEquality());
				response.put("groupEqualityThreshold", listApplicationsData.getGroupEqualityThreshold());
				response.put("dGroupDataDriftCheckStatus", listApplicationsData.getdGroupDataDriftCheck());

				String dataLocation = validationcheckdao.getDataLocationFromListDataSources(idData);
				LOG.debug("dataLocation=" + dataLocation);
				if (dataLocation.equalsIgnoreCase("FILESYSTEM")) {
					response.put("dataLocation", dataLocation);
				}
				String duplicateCheck = validationcheckdao.getDuplicateCheckFromListDFSetRule(idApp);
				response.put("duplicateCheck", duplicateCheck);

				/*
				 * Pradeep 8-Mar-2020 Global threshold changes: Debug log + pass object to JSP
				 * page
				 */
				String sThresholdOptions = "[]";
				String sThresholdsApplyOption = "0";

				try {
					Map<String, JSONArray> oThresholdOptions = JwfSpaInfra.getAppListsOptionsMap(jdbcTemplate,
							"GLOBAL_THRESHOLDS_OPTION");

					ObjectMapper oMapper = new ObjectMapper();
					String sListApplication = oMapper.writeValueAsString(listApplicationsData);

					JSONArray aThresholdOptions = oThresholdOptions.get("GLOBAL_THRESHOLDS_OPTION");
					sThresholdOptions = aThresholdOptions.toString();
					sThresholdsApplyOption = Integer.toString(listApplicationsData.getThresholdsApplyOption());

					DateUtility.DebugLog("customizeValidation 01", String.format(
							"thresholdsApplyOption = %1$s \n full retrieved data from DB = %2$s \n List Options = %3$s",
							sThresholdsApplyOption, sListApplication, sThresholdOptions));
				} catch (Exception oException) {
					oException.printStackTrace();
				}
				ObjectMapper mapper = new ObjectMapper();
				response.put("ThresholdOptions",
						mapper.readValue(sThresholdOptions, new TypeReference<List<Map<String, Object>>>() {
						}));
				response.put("ThresholdsApplyOption", sThresholdsApplyOption);

			} else if (appType.equals("Statistical Matching")) {

				LOG.info("statisticalMatchingCustomizeView");

				// Long projectId= (Long)session.getAttribute("projectId");
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);

				response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				response.put("listRefFunctionsname", listRefFunctionsname);
				response.put("getlistdatasourcesname", getlistdatasourcesname);
				response.put("currentSection", "Validation Check");
				response.put("idApp", idApp);
				response.put("idData", idData);
				response.put("applicationName", applicationName);
				response.put("name", name);
				long secondSourceIdData = validationcheckdao.getNamefromlistDataSources(idApp);
				response.put("secondSourceIdData", secondSourceIdData);
				LOG.debug("secondSourceIdData=" + secondSourceIdData);
				listStatisticalMatchingConfig listStatisticalMatchingData = validationcheckdao
						.getDataFromlistStatisticalMatchingConfig(idApp);
				LOG.debug("listStatisticalMatchingData=" + listStatisticalMatchingData.getMeasurementMeanThreshold());
				String expression = listStatisticalMatchingData.getLeftSideExp() + "="
						+ listStatisticalMatchingData.getRightSideExp();
				response.put("expression", expression);
				response.put("leftSideExp", listStatisticalMatchingData.getLeftSideExp());
				response.put("rightSideExp", listStatisticalMatchingData.getRightSideExp());

				response.put("RCType", listStatisticalMatchingData.getRecordCountType());
				response.put("RCThreshold", listStatisticalMatchingData.getRecordCountThreshold());
				response.put("MSum", listStatisticalMatchingData.getMeasurementSum());
				response.put("MSType", listStatisticalMatchingData.getMeasurementSumType());
				response.put("MSThreshold", listStatisticalMatchingData.getMeasurementSumThreshold());
				response.put("RCthreshold", listStatisticalMatchingData.getRecordCountThreshold());
				response.put("MMean", listStatisticalMatchingData.getMeasurementMean());
				response.put("MMType", listStatisticalMatchingData.getMeasurementMeanType());
				response.put("MMThreshold", listStatisticalMatchingData.getMeasurementMeanThreshold());
				response.put("MSD", listStatisticalMatchingData.getMeasurementStdDev());
				response.put("MSDType", listStatisticalMatchingData.getMeasurementStdDevType());
				response.put("MSDThreshold", listStatisticalMatchingData.getMeasurementStdDevThreshold());
				response.put("GroupBy", listStatisticalMatchingData.getGroupBy());
			} else if (appType.equals("Data Matching Group")) {
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				response.put("getlistdatasourcesname", getlistdatasourcesname);
				response.put("idApp", idApp);
				response.put("DataMatchingGroupCustomize", "DataMatchingGroupCustomize");
				response.put("idRightData", listApplicationsData.getIdRightData());
				response.put("matchingThreshold", listApplicationsData.getMatchingThreshold());

			} else if (appType.equals("Model Governance")) {
				listModelGovernance listModelGovernanceObject = validationcheckdao
						.getDataFromListModelGovernance(idApp);
				LOG.debug("listModelGovernanceObject.getModelGovernanceType()="
						+ listModelGovernanceObject.getModelGovernanceType());
				if (listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Decile Equality")
						|| listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Decile Consistency")) {
					response.put("modelGovernanceType", listModelGovernanceObject.getModelGovernanceType());
					response.put("modelIdCol", listModelGovernanceObject.getModelIdCol());
					response.put("decileCol", listModelGovernanceObject.getDecileCol());
					response.put("expectedPercentage", listModelGovernanceObject.getExpectedPercentage());
					response.put("thresholdPercentage", listModelGovernanceObject.getThresholdPercentage());
					response.put("incrementalMatching", listApplicationsData.getIncrementalMatching());
					response.put("buildHistoricFingerPrint", listApplicationsData.getBuildHistoricFingerPrint());
					response.put("updateFrequency", listApplicationsData.getUpdateFrequency());
					response.put("frequencyDays", listApplicationsData.getFrequencyDays());
					response.put("historicDateFormat", listApplicationsData.getHistoricDateFormat());
					if (listApplicationsData.getHistoricStartDate() != null
							&& listApplicationsData.getHistoricEndDate() != null) {
						String[] StartDate = listApplicationsData.getHistoricStartDate().split(" ");
						String[] EndDate = listApplicationsData.getHistoricEndDate().split(" ");
						// System.out.println(split[0].length());
						response.put("historicStartDate", StartDate[0]);
						response.put("historicEndDate", EndDate[0]);
					} else {
						response.put("historicStartDate", listApplicationsData.getHistoricStartDate());
						response.put("historicEndDate", listApplicationsData.getHistoricEndDate());
					}
					String timeSeries = listApplicationsData.getTimeSeries();
					if (!timeSeries.equals("None")) {
						String[] split = timeSeries.split(",");
						for (int i1 = 0; i1 < split.length; i1++) {
							LOG.debug(split[i1]);
							if (split[i1].equalsIgnoreCase("month")) {
								LOG.info("month condition");
								response.put("month", split[i1]);
							}
							if (split[i1].equalsIgnoreCase("dayOfWeek")) {
								response.put("dayOfWeek", split[i1]);
							}
							if (split[i1].equalsIgnoreCase("hourOfDay")) {
								response.put("hourOfDay", split[i1]);
								LOG.info("hourOfDay");
							}
							if (split[i1].equalsIgnoreCase("dayOfMonth")) {
								response.put("dayOfMonth", split[i1]);
							}
						}
					} else {
						response.put("None", timeSeries);
					}
					List listDataDefinitionColumnNames = validationcheckdao
							.getDisplayNamesFromListDataDefinition(idData);
					response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				} else if (listModelGovernanceObject.getModelGovernanceType().equalsIgnoreCase("Score Consistency")) {
					response.put("modelGovernanceType", listModelGovernanceObject.getModelGovernanceType());
					response.put("leftSourceSliceStart", listModelGovernanceObject.getLeftSourceSliceStart());
					response.put("leftSourceSliceEnd", listModelGovernanceObject.getLeftSourceSliceEnd());
					response.put("rightSourceSliceStart", listModelGovernanceObject.getRightSourceSliceStart());
					response.put("rightSourceSliceEnd", listModelGovernanceObject.getRightSourceSliceEnd());
					response.put("matchingExpression", listModelGovernanceObject.getMatchingExpression());
					response.put("measurementExpression", listModelGovernanceObject.getMeasurementExpression());
					response.put("threshold", listModelGovernanceObject.getThresholdPercentage());
					response.put("modelIdCol", listModelGovernanceObject.getModelIdCol());
					response.put("decileCol", listModelGovernanceObject.getDecileCol());
					List listDataDefinitionColumnNames = validationcheckdao
							.getDisplayNamesFromListDataDefinition(idData);
					response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
					response.put("sourcedateformat", listApplicationsData.getHistoricDateFormat());
				}
			} else if (appType.equals("Model Governance Dashboard")) {
				String mgDashboard = listApplicationsData.getCsvDir();
				String[] split = mgDashboard.split("-");
				/*
				 * for (int i = 0; i < split.length; i++) { System.out.println("id="+split[i]);
				 * }
				 */
				response.put("decileEquality", split[0]);
				response.put("decileConsistency", split[1]);
				response.put("scoreConsistency", split[2]);
				response.put("mgCustomizeView", "mgCustomizeView");
				Map<Long, String> decileEqualityAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Equality");
				Map<Long, String> decileConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Decile Consistency");
				Map<Long, String> scoreConsistencyAppType = validationcheckdao
						.getModelGovernanceAppTypeFromListApplications("Score Consistency");
				response.put("decileEqualityAppType", decileEqualityAppType);
				response.put("decileConsistencyAppType", decileConsistencyAppType);
				response.put("scoreConsistencyAppType", scoreConsistencyAppType);
			} else if (appType.equals("File Management")) {
				validationcheckdao.deleteFileManagementFromListFMRules(idApp);
				response.put("idApp", idApp);
				response.put("idData", idData);
				response.put("apptype", appType);
				response.put("applicationName", applicationName);
				// model.addObject("description", description);
				response.put("name", name);

			} else if (appType.equals("File Monitoring")) { // changes for File Monitoring
				// validationcheckdao.deleteFileManagementFromListFMRules(idApp);

				LOG.info("In File Monitoring..................");

				String fileMonitorType = fileMonitorDao.getFileMonitorTypeByIdApp(idApp);

				LOG.debug("fileMonitorType " + fileMonitorType);
				response.put("fileMonitorType", fileMonitorType);

				if (fileMonitorType.equalsIgnoreCase("snowflake")
						|| fileMonitorType.trim().equalsIgnoreCase("azuredatalakestoragegen2batch")
						|| fileMonitorType.equalsIgnoreCase("aws s3")
						|| fileMonitorType.equalsIgnoreCase("databricksdeltalake")) {

					// Check if there is data in staging table
					Long stagingDataCount = validationcheckdao.getDbkFileMonitorRulesCountInStaging(idApp);

					LOG.debug("stagingDataCount " + stagingDataCount);

					// Get connection details from fm_connection_details table

					if (stagingDataCount > 0l) {
						List<DBKFileMonitoringRules> stagingFMRulesList = validationcheckdao
								.getStagingDbkFileMonitorRules(idApp);
						response.put("dbkFileMonitoringRules", stagingFMRulesList);
					} else {
						List<DBKFileMonitoringRules> fileMonitoringRulesList = fileMonitorDao
								.getDBKFileMonitorDetailsByIdApp(idApp);

						response.put("dbkFileMonitoringRules", fileMonitoringRulesList);
					}

				} else {

					// int idApp1=0;
					List<FileMonitorRules> arrListFileMonitorRule = fileMonitorDao
							.getAllFileMonitorRuleDetailsByIdApp(idApp);

					LOG.debug("arrListFileMonitorRule =>" + arrListFileMonitorRule);

					response.put("arrListFileMonitorRule", arrListFileMonitorRule);
				}

				long connectionId = schemaDao.getConnectionIdForFMValidation(idApp);
				response.put("connectionId", connectionId);
				response.put("idApp", idApp);
				response.put("apptype", appType);
				response.put("applicationName", applicationName);
				// model.addObject("description", description);
				response.put("name", name);

				LOG.debug("File Monitoring -------- idApp ->" + idApp);
				LOG.debug("File Monitoring -------- apptype -> " + appType);
				LOG.debug("File Monitoring -------- applicationName->" + applicationName);
				LOG.debug("File Monitoring -name------- name->" + name);

			} else if (appType.equals("Primary Key Matching")) {
				response.put("applicationName", applicationName);
				List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
				List listRefFunctionsname = validationcheckdao.getDataFromlistRefFunctions();
				List listDataDefinitionColumnNames = validationcheckdao.getDisplayNamesFromListDataDefinition(idData);
				List<ListDmCriteria> getMatchingRules = validationcheckdao.getMatchingRules(idApp);

				response.put("jsongetMatchingRules", getMatchingRules);
				long secondSourceIdData = validationcheckdao.getNamefromlistDataSources(idApp);
				response.put("secondSourceIdData", secondSourceIdData);
				String secondSourceName = validationcheckdao.getNameFromListDataSources(secondSourceIdData);
				response.put("secondSourceName", secondSourceName);
				response.put("listDataDefinitionColumnNames", listDataDefinitionColumnNames);
				response.put("listRefFunctionsname", listRefFunctionsname);
				response.put("getlistdatasourcesname", getlistdatasourcesname);
				response.put("incrementalMatching", listApplicationsData.getIncrementalMatching());
				response.put("idApp", idApp);

			}
			// changes regarding Audit trail
			iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
					DatabuckConstants.DBK_FEATURE_VALIDATION, formatter.format(new Date()), idApp,
					DatabuckConstants.ACTIVITY_TYPE_EDITED, name);
			LOG.info("After if condition");
			response.put("idApp", idApp);
			response.put("idData", idData);
			response.put("apptype", appType);
			response.put("name", name);
			response.put("description", listApplicationsData.getDescription());
			response.put("listappName", name);
			response.put("applicationName", applicationName);

			// Data_Domain
			response.put("selectedDataDomain", data_domain);
			response.put("lstDataDomain", lstDataDomain);

			LOG.debug("idApp ::" + idApp);
			LOG.debug("idData ::" + idData);
			LOG.debug("apptype ::" + appType);
			LOG.debug("name ::" + name);
			LOG.debug("listappName ::" + name);
			LOG.debug("applicationName :: " + applicationName);
			response.put("idApp", idApp);
			response.put("currentSection", "Validation Check");
			response.put("currentLink", "VCView");
			resMap.put("result", response);
			resMap.put("status", "success");
			resMap.put("message", "successfully got the data.");
			LOG.info("successfully got the data.");

			return new ResponseEntity<Object>(resMap, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			resMap.put("status", "failed");
			resMap.put("message", "Failed to get details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/customizeValidation - END");
			return new ResponseEntity<Object>(resMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/customizeUpdateValidation", method = RequestMethod.POST)
	public ResponseEntity<Object> customizeUpdateValidationCheck(@RequestHeader HttpHeaders headers,
			@RequestBody ListApplications listApplications) {
		LOG.info("dbconsole/customizeUpdateValidation - START");
		boolean flag = true;
		LOG.debug("###############33 listApplications data domain =" + listApplications.getData_domain());
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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
//		if (listlistApplications.getCreatedByUser().trim().isEmpty()) {
//			response.put("message", "Created By field is mandatory.");
//			LOG.error("Created By field is mandatory.");
//			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
//		}
		try {
			ObjectMapper oMapper = new ObjectMapper();
			String sListApplication = oMapper.writeValueAsString(listApplications);
			DateUtility.DebugLog("customizeUpdateValidationCheckAjax 01",
					String.format("thresholdsApplyOption = %1$s \n full submitted data = %2$s",
							listApplications.getThresholdsApplyOption(), sListApplication));
			List<ListDataDefinition> listdatadefinition = templateviewdao.view(listApplications.getIdData());
			if (flag) {
				boolean buildHistoricFlag = validationcheckdao
						.checkTheConfigurationForPatternCheckTab(listdatadefinition, listApplications);
				LOG.debug("buildHistoricFlag=" + buildHistoricFlag);
				if (!buildHistoricFlag) {
					flag = false;
					response.put("message", "The Regex Pattern check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean defaultPatternFlag = validationcheckdao
						.checkTheConfigurationForDefaultPatternCheckTab(listdatadefinition, listApplications);
				LOG.debug("defaultPatternFlag=" + defaultPatternFlag);
				if (!defaultPatternFlag) {
					flag = false;
					response.put("message", "The Default Pattern check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				if (listApplications.getBadData().equalsIgnoreCase("Y")) {
					boolean badDataCheckFlag = validationcheckdao.checkTheBadData(listdatadefinition, listApplications);
					LOG.debug("badDataCheckFlag=" + badDataCheckFlag);
					if (!badDataCheckFlag) {
						flag = false;
						response.put("message", "The Data type check is configured incorrectly");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}
			if (flag) {
				if (listApplications.getBuildHistoricFingerPrint().equalsIgnoreCase("Y")) {
					if (listApplications.getHistoricStartDate() == null
							|| listApplications.getHistoricStartDate().trim().isEmpty()
							|| listApplications.getHistoricEndDate() == null
							|| listApplications.getHistoricEndDate().isEmpty()) {
						flag = false;
						response.put("message", "Start Date or End Date is missing");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}
			if (flag) {
				boolean buildHistoricFlag = validationcheckdao.checkTheConfigurationForBuildHistoric(listdatadefinition,
						listApplications);
				LOG.debug("buildHistoricFlag=" + buildHistoricFlag);
				if (!buildHistoricFlag) {
					flag = false;
					response.put("message", "The last read time check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			if (flag) {
				boolean identityFlag = validationcheckdao.checkTheConfigurationForDupRowIdentity(listdatadefinition,
						listApplications);
				LOG.debug("identityFlag=" + identityFlag);
				if (!identityFlag) {
					flag = false;
					response.put("message", "The duplicate identity check (primary key) is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean applyRulesFlag = validationcheckdao.checkTheConfigurationForapplyRules(listdatadefinition,
						listApplications);
				LOG.debug("applyRulesFlag=" + applyRulesFlag);
				if (!applyRulesFlag) {
					flag = false;
					response.put("message", "The rules are configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean applyDerivedColumnsFlag = validationcheckdao
						.checkTheConfigurationForapplyDerivedColumns(listdatadefinition, listApplications);
				LOG.debug("applyDerivedColumnsFlag=" + applyDerivedColumnsFlag);
				if (!applyDerivedColumnsFlag) {
					flag = false;
					response.put("message", "The Derived Columns are configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean AllFlag = validationcheckdao.checkTheConfigurationForDupRowAll(listdatadefinition,
						listApplications);
				LOG.debug("AllFlag=" + AllFlag);
				if (!AllFlag) {
					flag = false;
					response.put("message", "Duplicate Check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean dataDriftFlag = validationcheckdao.checkTheConfigurationForDataDrift(listdatadefinition,
						listApplications);
				LOG.debug("dataDriftFlag=" + dataDriftFlag);
				if (!dataDriftFlag) {
					flag = false;
					response.put("message", "The data drift check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean numFieldFlag = validationcheckdao.checkTheConfigurationForNumField(listdatadefinition,
						listApplications);
				LOG.debug("numFieldFlag=" + numFieldFlag);
				if (!numFieldFlag) {
					flag = false;
					response.put("message", "The Distribution check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean stringFieldFlag = validationcheckdao.checkTheConfigurationForstringField(listdatadefinition,
						listApplications);
				LOG.debug("stringFieldFlag=" + stringFieldFlag);
				if (!stringFieldFlag) {
					flag = false;
					response.put("message", "The string stat check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean dGroupFlag = validationcheckdao.checkTheConfigurationFordGroup(listdatadefinition,
						listApplications);
				LOG.debug("dGroupFlag=" + dGroupFlag);
				if (!dGroupFlag) {
					flag = false;
					response.put("message", "The Microsegment check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean recordAnomalyFlag = validationcheckdao.checkTheConfigurationForRecordAnomaly(listdatadefinition,
						listApplications);
				LOG.debug("recordAnomalyFlag=" + recordAnomalyFlag);
				if (!recordAnomalyFlag) {
					flag = false;
					response.put("message", "The record anomaly check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				boolean nonNullFlag = validationcheckdao.checkTheConfigurationForNonNullField(listdatadefinition,
						listApplications);
				LOG.debug("nonNullFlag=" + nonNullFlag);
				if (!nonNullFlag) {
					flag = false;
					response.put("message", "The Null check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}
			if (flag) {
				if (listApplications.getIncrementalMatching().equalsIgnoreCase("Y")) {
					Long idData = listApplications.getIdData();
					boolean incrementalCheck = validationcheckdao.checkForIncrementalInListDataAccess(idData);
					LOG.debug("incrementalCheck=" + incrementalCheck);
					if (!incrementalCheck) {
						flag = false;
						response.put("message",
								"Cannot use data slices in source with Historic and Incremental Type of Application");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}

			// Changes to allow user to select either DateRuleCheck or
			// MicrosegmentDateRuleCheck
			// only at one time
			if (flag) {
				if (listApplications.getDateRuleChk() != null && listApplications.getDateRuleChk().equalsIgnoreCase("Y")
						&& listApplications.getdGroupDateRuleCheck() != null
						&& listApplications.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
					flag = false;
					response.put("message",
							"Choose either DateRuleCheck or MicrosegmentDateRuleCheck, both can't be enabled together.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			// Changes to allow user to select either DataDriftCheck or
			// MicrosegmentBasedDataDrift
			// only at one time
			if (flag) {
				if (listApplications.getDataDriftCheck() != null
						&& listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
						&& listApplications.getdGroupDataDriftCheck() != null
						&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
					flag = false;
					response.put("message",
							"Choose either DataDriftCheck or MicrosegmentBasedDataDrift, both can't be enabled together.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			if (flag) {
				if (listApplications.getDateRuleChk().equalsIgnoreCase("Y")) {
					LOG.info("-----------in validationCheckControllerD getDateRuleChk");
					Long idData = listApplications.getIdData();
					LOG.debug("validationCheckControllerD idData=>" + idData);

					// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
					boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(listdatadefinition,
							listApplications);
					LOG.debug("validationCheckControllerD dateRuleCheck=>" + dateRuleCheck);
					if (!dateRuleCheck) {
						flag = false;
						response.put("message", "The Date Consistency check is configured incorrectly");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}

			if (flag) {
				boolean dGroupDateRuleCheckFlag = validationcheckdao
						.checkConfigurationDgroupDateRuleCheck(listdatadefinition, listApplications);
				LOG.debug("MicrosegmentDateRuleCheckFlag=" + dGroupDateRuleCheckFlag);
				if (!dGroupDateRuleCheckFlag) {
					flag = false;
					response.put("message", "The Microsegment Date Consistency check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			if (flag) {
				boolean dgroupNonNullFlag = validationcheckdao.checkConfigurationForDGroupNullCheck(listdatadefinition,
						listApplications);
				LOG.debug("MicrosegmentNonNullFlag=" + dgroupNonNullFlag);
				if (!dgroupNonNullFlag) {
					flag = false;
					response.put("message", "The Microsegment check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			if (flag) {
				boolean defaultCheckFlag = validationcheckdao.checkTheConfigurationDefaultCheck(listdatadefinition,
						listApplications);
				LOG.debug("defaultCheckFlag=" + defaultCheckFlag);
				if (!defaultCheckFlag) {
					flag = false;
					response.put("message", "The Default check is configured incorrectly");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			//////////////////// --AKSHAY 4/3/2019--////////////////////////////
			if (flag) {
				if (listApplications.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
					boolean time = validationcheckdao.checkTheConfigurationForTimelinessKeyField(listdatadefinition,
							listApplications);
					LOG.debug("time=" + time);
					if (!time) {
						flag = false;
						response.put("message", "The TimelinessKey check is configured incorrectly");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}
			/////////////////////////////////////////////////////////////////
			// changes for lengthCheck 31Dec18
			if (flag) {
				if (listApplications.getlengthCheck().equalsIgnoreCase("Y")) {
					LOG.debug(
							"validationCheckController if (flag) listApplications.getlengthCheck().equalsIgnoreCase(\"Y\") -> ");
					Long idData = listApplications.getIdData();
					// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
					boolean lengthCheck = validationcheckdao.checkForLengthCheck(listdatadefinition, listApplications);

					LOG.debug("lengthCheck=" + lengthCheck);
					if (!lengthCheck) {
						flag = false;
						response.put("message", "The Length check is configured incorrectly");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
				// Max Length Check
				if (flag) {
					if (listApplications.getMaxLengthCheck().equalsIgnoreCase("Y")) {
						LOG.debug(
								"validationCheckController if (flag) listApplications.getMaxLengthCheck().equalsIgnoreCase(\"Y\") -> ");
						Long idData = listApplications.getIdData();
						// boolean dateRuleCheck = validationcheckdao.checkFordateRuleCheck(idData);
						boolean maxLengthCheck = validationcheckdao.checkForMaxLengthCheck(listdatadefinition,
								listApplications);

						LOG.debug("MaxlengthCheck=" + maxLengthCheck);
						if (!maxLengthCheck) {
							flag = false;
							response.put("message", "The Max Length check is configured incorrectly");

							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					}
				}
			}

			// Changes to allow user to select either DataDrift or Microsegment Based
			// DataDrift
			// only at one time
			if (flag) {
				if (listApplications.getDataDriftCheck() != null
						&& listApplications.getDataDriftCheck().equalsIgnoreCase("Y")
						&& listApplications.getdGroupDataDriftCheck() != null
						&& listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
					flag = false;
					response.put("message",
							"Choose either DataDrift or Microsegment Based DataDrift, both can't be enabled together.");

					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}
			}

			if (flag) {
				if (listApplications.getdGroupDataDriftCheck().equalsIgnoreCase("Y")) {
					boolean microsegmentFlag = validationcheckdao
							.checkTheConfigurationForMicrosegment(listdatadefinition, listApplications);
					LOG.debug("microsegmentFlag=" + microsegmentFlag);
					if (!microsegmentFlag) {
						flag = false;
						response.put("message", "The Microsegment Data Drift check is configured incorrectly");

						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				}
			}

			Long idApp = listApplications.getIdApp();
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

			// If RuleCatalog is enabled and staging is activated updating the details in
			// staging_listApplications table
			boolean validationStatingEnabled = ruleCatalogService.isValidationStagingActivated(idApp);
			double dupRowAllThreshold = 0.0;
			double dupRowIdentityThreshold = 0.0;

			if (listApplications.getDupRowAllThreshold() != null)
				dupRowAllThreshold = listApplications.getDupRowAllThreshold();

			if (listApplications.getDupRowIdentityThreshold() != null)
				dupRowIdentityThreshold = listApplications.getDupRowIdentityThreshold();

			if (isRuleCatalogEnabled && validationStatingEnabled) {

				ruleCatalogDao.updateIntoStagingListapplication(listApplications);

				ruleCatalogDao.updateStagingListDfTranRule(idApp, listApplications.getDupRowIdentity(),
						dupRowIdentityThreshold, listApplications.getDupRowAll(), dupRowAllThreshold);

				// Update Foundation checks details to actual listApplications
				// As these are related to only Application Mode, Historic and Incremental
				// details, Frequency
				validationcheckdao.updateFoundationChecksDetailsToListApplications(listApplications);
			} else {
				validationcheckdao.updateintolistdfsetruleandtranrule(idApp,
						listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());

				int updateintolistapplication = validationcheckdao
						.updateintolistapplicationForAjaxRequest(listApplications);
				LOG.debug("updateintolistapplication=" + updateintolistapplication);
				LOG.debug("datadriftthreshold=" + listApplications.getDataDriftThreshold());

				// Sumeet_21_08_2018
				int updateintolistdatadefinitions = validationcheckdao.updateintolistdatadefinitions(
						listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
						listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(),
						"N", listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(),
						0.0);
				LOG.debug("updateintolistdatadefinitions=" + updateintolistdatadefinitions);

				if (!validationcheckdao.isDuplicateCheckEnabled(idApp)) {
					validationcheckdao.insertintolistdftranrule(idApp, "N", 0.0, "N", 0.0);
				}
				int insertintolistdftranrule = validationcheckdao.updateintolistdftranrule(idApp,
						listApplications.getDupRowIdentity(), dupRowIdentityThreshold, listApplications.getDupRowAll(),
						dupRowAllThreshold);
				LOG.debug("insertintolistdftranrule=" + insertintolistdftranrule);
			}

			// If RuleCatalog is enabled, update Rule catalog
			if (isRuleCatalogEnabled) {
				LOG.debug("\n====> Updating the rule changes related to validation customization into Rule Catalog");
				ruleCatalogService.updateRuleCatalog(idApp);
			}
			response.put("status", "success");
			response.put("message", "Validation Check Customized Successfully");
			LOG.info("Validation Check Customized Successfully");

			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("stackTrace", ex.getCause());
			response.put("status", "failed");
			response.put("message", "Failed to customize Validation Check");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/customizeUpdateValidation - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/mainPrimaryMatchingHandler", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> mainPrimaryMatchingHandler(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/mainPrimaryMatchingHandler - START");
		Map<String, Object> resMap = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			resMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");

			return new ResponseEntity<Object>(resMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			resMap.put("message", "Token is expired.");

			return new ResponseEntity<Object>(resMap, HttpStatus.EXPECTATION_FAILED);
		}
		LOG.debug("Getting request parameters  " + params);
		JSONObject oWrapperData = new JSONObject(params);
		JSONObject oPrimaryKeyMatchingData = new JSONObject();
		JSONArray aPrimaryKeyMatchingData = new JSONArray();

		JSONObject oJsonResponse = new JSONObject();

		String sContext;

		long nLeftTmplId = 0l;
		long nRightTmplId = 0l;

		try {
			// oWrapperData = new JSONObject(params.get("sWrapperData"));
			sContext = params.get("context").toString();
			DateUtility.DebugLog("mainPrimaryMatchingHandler 01", "Server call context = " + sContext);
			switch (sContext) {
			case "LoadMatchConfiguration":
				// oPrimaryKeyMatchingData = oWrapperData.getJSONObject("Data");
				nLeftTmplId = Long.parseUnsignedLong(params.get("LeftDataTmplId").toString());
				nRightTmplId = Long.parseUnsignedLong(params.get("RightDataTmplId").toString());
				oJsonResponse = loadPrimaryMatchingDataList(nLeftTmplId, nRightTmplId);
				oJsonResponse = enablePrimaryKeyColumns(oJsonResponse, nLeftTmplId, nRightTmplId);
				break;
			case "SaveMatchConfiguration":
				aPrimaryKeyMatchingData = oWrapperData.getJSONArray("Data");
				if (isRuleOrCriteriaExists(oWrapperData.getString("IdApp")) < 1) {
					oJsonResponse = saveMatchConfiguration(aPrimaryKeyMatchingData, oWrapperData.getString("IdApp"),
							oWrapperData.getString("RightDataTmplId"));
					if (oJsonResponse.getBoolean("Status")) {
						DateUtility.DebugLog("mainPrimaryMatchingHandler", "Redirect to validation view page");
					}
				} else {
					resMap.put("status", "failed");
					resMap.put("message", "Data already exist for this List Application");
				}
				break;

			default:
			}
			resMap.put("result", oJsonResponse.toMap());
			resMap.put("status", "success");
			resMap.put("message", "success");

			return new ResponseEntity<>(resMap, HttpStatus.OK);
		} catch (Exception oException) {
			oException.printStackTrace();
			resMap.put("status", "failed");
			resMap.put("message", oException.getMessage());
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/mainPrimaryMatchingHandler - END");
			return new ResponseEntity<>(resMap, HttpStatus.OK);
		}
	}

	private JSONObject enablePrimaryKeyColumns(JSONObject oJsonResponse, long leftTemplateId, long rightTemplateId) {
		try {
			JSONArray leftDataSetArr = oJsonResponse.getJSONArray("LeftDataSet");

			List<String> leftPrimaryColumns = templateviewdao.getPrimaryCheckEnabledColumnsByIdData(leftTemplateId);
			leftPrimaryColumns.replaceAll(e -> e.toUpperCase());

			List<String> rightPrimaryColumns = templateviewdao.getPrimaryCheckEnabledColumnsByIdData(rightTemplateId);
			rightPrimaryColumns.replaceAll(e -> e.toUpperCase());

			for (int i = 0; i < leftDataSetArr.length(); i++) {

				JSONObject leftColumnMapObj = leftDataSetArr.getJSONObject(i);
				String leftColumnName = leftColumnMapObj.getString("LeftColumnName").trim().toUpperCase();

				if (leftPrimaryColumns.contains(leftColumnName) && rightPrimaryColumns.contains(leftColumnName)) {
					leftColumnMapObj.put("IsLeftColumnPrimaryField", true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oJsonResponse;
	}

	private JSONObject loadPrimaryMatchingDataList(long nLeftTmplId, long nRightTmplId) throws Exception {
		List<HashMap<String, String>> aLeftDataList = null;
		List<HashMap<String, String>> aRightDataList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] {};

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		CustomizeDataTableColumn oRightSelectControl = (oDataRow) -> {
			// String sSelectControlTmpl = "<select
			// id='RightColumnNameAndType-%1$s'></select>";
			return oDataRow.get("LeftPrimaryColumnId");
		};

		CustomizeDataTableColumn oLeftExprTextBox = (oDataRow) -> {
			// String sLeftExprTextBox = "<input type='text' id='LeftExprTextBox-%1$s'>";
			// return String.format(sLeftExprTextBox, oDataRow.get("LeftPrimaryColumnId"));
			return oDataRow.get("LeftPrimaryColumnId");
		};

		CustomizeDataTableColumn oRightExprTextBox = (oDataRow) -> {
			// String sRightExprTextBox = "<input type='text' id='RightExprTextBox-%1$s'>";
			// return String.format(sRightExprTextBox, oDataRow.get("LeftPrimaryColumnId"));
			return oDataRow.get("LeftPrimaryColumnId");
		};

		HashMap<String, CustomizeDataTableColumn> oCustomizeDataTable = new HashMap<String, CustomizeDataTableColumn>() {
			{
				put("RightColumnNameAndType", oRightSelectControl);
				put("LeftColumnExpr", oLeftExprTextBox);
				put("RightColumnExpr", oRightExprTextBox);
			}
		};

		try {

//			aColumnSpec = new String[] {
//				"LeftPrimaryColumnId", "LeftColumnName", "LeftColumnType",  "LeftValueColumnId",
//				"IsLeftColumnPrimaryField", "IsLeftColumnValueField", "IsLeftColumnCustomized", "LeftColumnCustomizedExpr",
//				"LeftPrimaryColumnId:namedcheckbox", "LeftValueColumnId:namedcheckbox", "LeftCustomize:namedcheckbox","LeftColumnExpr:lambda",
//				"LeftPrimaryColumnId:namedcheckbox", "LeftValueColumnId:namedcheckbox", "LeftCustomize:namedcheckbox",
//				"RightColumnIdAndName", "IsRightColumnCustomized", "RightColumnCustomizedExpr",
//				"RightCustomize:namedcheckbox", "RightColumnNameAndType:lambda", "RightColumnExpr:lambda"
//			};

			aColumnSpec = new String[] { "LeftPrimaryColumnId", "LeftColumnName", "LeftColumnType", "LeftValueColumnId",
					"IsLeftColumnPrimaryField", "IsLeftColumnValueField", "IsLeftColumnCustomized",
					"LeftColumnCustomizedExpr", "RightColumnIdAndName", "IsRightColumnCustomized",
					"RightColumnCustomizedExpr", };

			sDataSql = "";
			sDataSql = sDataSql
					+ "select idColumn as LeftPrimaryColumnId, displayName as LeftColumnName, format as LeftColumnType, idColumn as LeftValueColumnId, idColumn as LeftCustomize, \n";
			sDataSql = sDataSql
					+ "'false' as IsLeftColumnPrimaryField, 'false' as IsLeftColumnValueField, 'false' as IsLeftColumnCustomized, '' as LeftColumnCustomizedExpr, \n";
			sDataSql = sDataSql
					+ "idColumn as RightCustomize, '' as RightColumnIdAndName, 'false' as IsRightColumnCustomized, '' as RightColumnCustomizedExpr \n";
			sDataSql = sDataSql + String.format("from listDataDefinition where idData = %1$s;", nLeftTmplId);

			aLeftDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "",
					oCustomizeDataTable);
			sDataViewList = oMapper.writeValueAsString(aLeftDataList);
			sDataViewList = sDataViewList.replaceAll("\"false\"", "false");

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("LeftDataSet", aJsonDataList);

			sDataSql = String.format(
					"select idColumn as RightColumnId, displayName as RightColumnName, format as RightColumnType from listDataDefinition where idData = %1$s",
					nRightTmplId);

			aColumnSpec = new String[] { "RightColumnId", "RightColumnName", "RightColumnType" };
			aRightDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", null);
			sDataViewList = oMapper.writeValueAsString(aRightDataList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("RightDataSet", aJsonDataList);

			DateUtility.DebugLog("loadPrimaryMatchingDataList", String.format("Return JSON = \n%1$s\n", oJsonRetValue));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	private long isRuleOrCriteriaExists(String sIdApp) {
		long nRetValue = 0l;
		String sSelectSql = String.format("select count(*) as Count from listDMRules where idApp = %1$s;", sIdApp);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nRetValue = oSqlRowSet.next() ? oSqlRowSet.getLong("Count") : -1l;

		return nRetValue;
	}

	private JSONObject saveMatchConfigurationChanges(JSONArray aPrimaryKeyMatchingData, String sIdApp,
			String RightDataTmplId) throws Exception {
		long nCriteriaRuleId = 0;
		JSONObject oJsonRetValue = new JSONObject();

		oJsonRetValue.put("status", "failed");
		oJsonRetValue.put("message", "Failed to save Matching Configuration");

		try {
			DateUtility.DebugLog("saveMatchConfiguration",
					String.format("Input data sIdApp = %1$s \n%2$s\n", sIdApp, aPrimaryKeyMatchingData));

			// update validation for right data id
			for (int nIndex = 0; nIndex < aPrimaryKeyMatchingData.length(); nIndex++) {
				JSONObject oPrimaryKeyMatchingData = aPrimaryKeyMatchingData.getJSONObject(nIndex);
				/* One field cannot be primary key and value field at same time */
				if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnPrimaryField") == true) {

					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_JOIN_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				} else if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnValueField") == true) {
					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_VALUE_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);
				}
			}

			oJsonRetValue.put("status", "success");
			oJsonRetValue.put("message", "Match criteria or configuration updated successfully");
		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	private long getRuleId(String sWhichRule, String sIdApp) {
		long nRetValue = 0l;
		String sSelectSql = String.format(
				"select idDM as RuleId from listDMRules where idApp = %1$s and matchType2 = '%2$s';", sIdApp,
				sWhichRule);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nRetValue = oSqlRowSet.next() ? oSqlRowSet.getLong("RuleId") : -1l;

		return nRetValue;
	}

	private void insertMatchCriteria(long nRuleId, JSONObject oPrimaryKeyMatchingData) {
		String sCriteriaInsertSqlTmpl = "";
		String sCriteriaInsertSql = "";
		String sRightColumnIdName = oPrimaryKeyMatchingData.getString("RightColumnIdAndName");

		long nLeftColumnId = Long.parseLong(oPrimaryKeyMatchingData.getString("LeftPrimaryColumnId"));
		long nRightColumnId = Long.parseLong(sRightColumnIdName.split("-")[0]);
		String sLeftColumnName = oPrimaryKeyMatchingData.getString("LeftColumnName");
		String sLeftColumnExpr = oPrimaryKeyMatchingData.getString("LeftColumnCustomizedExpr");
		String sRightColumnName = sRightColumnIdName.split("-")[1];
		String sRightColumnExpr = oPrimaryKeyMatchingData.getString("RightColumnCustomizedExpr");

		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "insert into listDMCriteria \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl
				+ "(idDM, idLeftColumn, leftSideColumn, leftSideExp, idRightColumn, rightSideColumn, rightSideExp) \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "values \n";
		sCriteriaInsertSqlTmpl = sCriteriaInsertSqlTmpl + "(%1$s, %2$s, '%3$s', '%4$s', %5$s, '%6$s', '%7$s') \n";

		sCriteriaInsertSql = String.format(sCriteriaInsertSqlTmpl, nRuleId, nLeftColumnId, sLeftColumnName,
				sLeftColumnExpr, nRightColumnId, sRightColumnName, sRightColumnExpr);
		jdbcTemplate.update(sCriteriaInsertSql);
	}

	private JSONObject saveMatchConfiguration(JSONArray aPrimaryKeyMatchingData, String sIdApp, String RightDataTmplId)
			throws Exception {
		String sRuleInsertSqlTmpl = "insert into listDMRules (idApp, matchType, matchType2) values (%1$s, '%2$s', '%3$s');";
		String sRuleInsertSql = "";
		String sUpdateListApplicationsSql = "update listApplications set idRightData = %1$s where idApp = %2$s";
		long nCriteriaRuleId = 0;
		JSONObject oJsonRetValue = new JSONObject();

		oJsonRetValue.put("Status", false);
		oJsonRetValue.put("Msg", "Default failure status");

		try {
			DateUtility.DebugLog("saveMatchConfiguration",
					String.format("Input data sIdApp = %1$s \n%2$s\n", sIdApp, aPrimaryKeyMatchingData));

			// update validation for right data id
			sUpdateListApplicationsSql = String.format(sUpdateListApplicationsSql, RightDataTmplId, sIdApp);
			jdbcTemplate.update(sUpdateListApplicationsSql);

			sRuleInsertSql = String.format(sRuleInsertSqlTmpl, sIdApp, "One to One", PRIMARY_KEY_MATCH_JOIN_FIELD);
			jdbcTemplate.update(sRuleInsertSql);

			sRuleInsertSql = String.format(sRuleInsertSqlTmpl, sIdApp, "One to One", PRIMARY_KEY_MATCH_VALUE_FIELD);
			jdbcTemplate.update(sRuleInsertSql);

			for (int nIndex = 0; nIndex < aPrimaryKeyMatchingData.length(); nIndex++) {
				JSONObject oPrimaryKeyMatchingData = aPrimaryKeyMatchingData.getJSONObject(nIndex);

				/* One field cannot be primary key and value field at same time */
				if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnPrimaryField") == true) {

					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_JOIN_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				} else if (oPrimaryKeyMatchingData.getBoolean("IsLeftColumnValueField") == true) {
					nCriteriaRuleId = getRuleId(PRIMARY_KEY_MATCH_VALUE_FIELD, sIdApp);
					insertMatchCriteria(nCriteriaRuleId, oPrimaryKeyMatchingData);

				}

			}

			oJsonRetValue.put("Status", true);
			oJsonRetValue.put("Msg", "Match criteria or configuration saved successfully");

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	@RequestMapping(value = "/dbconsole/editPrimaryMatchingHandler", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> editPrimaryMatchingHandler(@RequestHeader HttpHeaders headers,
			@RequestBody String paramsBody) throws Exception {
		LOG.info("dbconsole/editPrimaryMatchingHandler - START");
		JSONObject resMap = new JSONObject();
		String token = null;
		JSONObject params = new JSONObject(paramsBody);
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			resMap.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");

			return new ResponseEntity<String>(resMap.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			resMap.put("message", "Token is expired.");
			LOG.error("Token is expired.");

			return new ResponseEntity<String>(resMap.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		JSONObject oWrapperData = new JSONObject();
		JSONObject oPrimaryKeyMatchingData = new JSONObject();
		JSONArray aPrimaryKeyMatchingData = new JSONArray();

		JSONObject oJsonResponse = new JSONObject();
		String sContext = "";
		String status = "failed";
		String message = "";

		long nLeftTmplId = 0l;
		long nRightTmplId = 0l;
		long idApp = 0l;
		try {
			LOG.debug(params.toString());
			oWrapperData = params.getJSONObject("sWrapperData");
			idApp = params.getLong("IdApp");
			sContext = params.getString("Context");
			LOG.debug(oWrapperData.toString());

			DateUtility.DebugLog("editPrimaryMatchingHandler 01", "Server call context = " + sContext);
			switch (sContext) {

			case "LoadMatchConfiguration":
				oPrimaryKeyMatchingData = oWrapperData.getJSONObject("Data");
				try {
					nLeftTmplId = oPrimaryKeyMatchingData.getLong("LeftDataTmplId");
					nRightTmplId = oPrimaryKeyMatchingData.getLong("RightDataTmplId");
				} catch (Exception e) {
					LOG.debug(e.getLocalizedMessage());
				}
				if (oPrimaryKeyMatchingData != null
						&& (oPrimaryKeyMatchingData.isNull("RightDataTmplId") || nRightTmplId <= 0)) {
					message = "Validation configuration is incomplete [right template id missing]";
				} else {
					nRightTmplId = oPrimaryKeyMatchingData.getLong("RightDataTmplId");

					oJsonResponse = loadPrimaryMatchingDataList(nLeftTmplId, nRightTmplId);
					// Get validation Details
					ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);
					JSONObject validationInfo = new JSONObject();
					validationInfo.put("description", listApplication.getDescription());
					validationInfo.put("validationName", listApplication.getName());
					validationInfo.put("validationType", listApplication.getAppType());
					validationInfo.put("threashold", listApplication.getMatchingThreshold());
					validationInfo.put("dateFormat", listApplication.getHistoricDateFormat());
					oJsonResponse.put("validationInfo", validationInfo);

					// Get matching criteria as per idApp and merge the values to response json
					List<ListDmCriteria> listDmCriteriaList = new ArrayList<>();
					try {
						listDmCriteriaList = validationcheckdao.getMatchingRules(idApp);

						if (listDmCriteriaList != null && listDmCriteriaList.size() > 0) {
							for (ListDmCriteria listDmCriteria : listDmCriteriaList) {
								String leftSideColumn = listDmCriteria.getLeftSideColumn();
								String rightSideColumn = listDmCriteria.getRightSideColumn();
								String leftSideExp = listDmCriteria.getLeftSideExp();
								String rightSideExp = listDmCriteria.getRightSideExp();
								String rightColumn = listDmCriteria.getRightColumn();
								String matchType = listDmCriteria.getMatchType();

								JSONArray leftDataSet = oJsonResponse.getJSONArray("LeftDataSet");
								for (int i = 0; i < leftDataSet.length(); i++) {
									JSONObject columnObj = leftDataSet.getJSONObject(i);
									if (columnObj.getString("LeftColumnName").equalsIgnoreCase(leftSideColumn)) {
										columnObj.put("LeftColumnCustomizedExpr", leftSideExp);
										columnObj.put("RightColumnCustomizedExpr", rightSideExp);
										columnObj.put("RightColumnIdAndName", rightColumn);

										if (leftSideExp != null && !leftSideExp.trim().isEmpty())
											columnObj.put("IsLeftColumnCustomized", true);

										if (rightSideExp != null && !rightSideExp.trim().isEmpty())
											columnObj.put("IsRightColumnCustomized", true);

										if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_JOIN_FIELD")
												|| matchType.equalsIgnoreCase("Key Fields Match"))
											columnObj.put("IsLeftColumnPrimaryField", true);
										else if (matchType.equalsIgnoreCase("PRIMARY_KEY_MATCH_VALUE_FIELD")
												|| matchType.equalsIgnoreCase("Measurements Match"))
											columnObj.put("IsLeftColumnValueField", true);

									}
								}
							}
							resMap.put("result", oJsonResponse);
							message = "Matching configuration loaded successfully";
							status = "success";
							LOG.info("Matching configuration loaded successfully");
						} else {
							message = "Failed to load matching configuration";
							LOG.error(message);
						}
					} catch (Exception e) {
						e.printStackTrace();
						message = "Failed to load matching configuration";
						LOG.error(e.getMessage());
					}
				}

				break;

			case "SaveMatchConfiguration":
				aPrimaryKeyMatchingData = oWrapperData.getJSONArray("Data");
				validationcheckdao.deleteEntryFromListDMRulesWithIdApp(idApp);
				ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
				oJsonResponse = saveMatchConfigurationChanges(aPrimaryKeyMatchingData, "" + idApp,
						oWrapperData.getString("RightDataTmplId"));
				//Audit Trail Changes
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_VALIDATION,
				formatter.format(new Date()), idApp,DatabuckConstants.ACTIVITY_TYPE_EDITED,listApplications.getName());

				resMap = oJsonResponse;

				return new ResponseEntity<String>(resMap.toString(), HttpStatus.OK);

			default:
				message = "Wrong context";
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			LOG.error(oException.getMessage());
		}
		resMap.put("status", status);
		resMap.put("message", message);
		LOG.info("dbconsole/editPrimaryMatchingHandler - END");
		return new ResponseEntity<String>(resMap.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/getConnectionListFM", method = RequestMethod.POST)
	public ResponseEntity<Object> changeLocationForDataTemplateAjax(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/getConnectionListFM - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
		} catch (Exception e) {
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
			Long projectId = Long.parseLong(String.valueOf(params.get("projectId")));
			Integer domainId = Integer.parseInt(String.valueOf(params.get("domainId")));
			String location = String.valueOf(params.get("localtion"));
			Map<Long, String> dataTemplateForRequiredLocation = listdatasourcedao
					.getDataTemplateForRequiredLocation(location, projectId, domainId);
			LOG.debug("dataTemplateForRequiredLocation=" + dataTemplateForRequiredLocation);
			response.put("result", dataTemplateForRequiredLocation);
			response.put("message", "Successfully got the details.");
			response.put("status", "success");
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage());
			response.put("message", "Error occurred while fetching the connection list");
		}
		LOG.info("dbconsole/getConnectionListFM - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/dbconsole/updateValidationJobSize", method = RequestMethod.POST)
	public ResponseEntity<Object> updateValidationJobSize(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/updateValidationJobSize - START");

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
						JSONObject inputJson = new JSONObject(inputJsonStr);
						int idApp = inputJson.getInt("idApp");
						String validationJobSize = inputJson.getString("validationJobSize");
						if (validationCheckDAO.updateValidationJobsize(idApp, validationJobSize)) {
							status = "success";
							message = "Validation Job Size updated successfully";
						} else {
							message = "Failed to update validationJobSize";
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
				message = "Token is missing in header";
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
		LOG.info("dbconsole/updateValidationJobSize - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

}
