package com.databuck.restcontroller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.dao.*;
import com.databuck.datatemplate.DatabricksDeltaConnection;
import com.databuck.service.*;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ApproveMicroSegmentsDTO;
import com.databuck.bean.DataTemplateCreateRequest;
import com.databuck.bean.DomainProject;
import com.databuck.bean.GloabalRule;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataDefinitionDelta;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.TemplateDeltaResponse;
import com.databuck.bean.UserToken;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.ValidateQuery;
import com.databuck.bean.DerivedTemplateValidateQuery;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IImportExportUtilityDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.AzureConnection;
import com.databuck.datatemplate.AzureCosmosDb;
import com.databuck.datatemplate.AzureDataLakeConnection;
import com.databuck.datatemplate.AzureDataLakeGen2Connection;
import com.databuck.datatemplate.BatchFileSystem;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.MYSQLConnection;
import com.databuck.datatemplate.MsSqlActiveDirectoryConnection;
import com.databuck.datatemplate.OracleConnection;
import com.databuck.datatemplate.OracleRACConnection;
import com.databuck.datatemplate.PostgresConnection;
import com.databuck.datatemplate.S3BatchConnection;
import com.databuck.datatemplate.SnowflakeConnection;
import com.databuck.datatemplate.TeradataConnection;
import com.databuck.datatemplate.VerticaConnection;
import com.databuck.dto.TableListforSchema;						   
import com.databuck.econstants.DeltaType;
import com.databuck.econstants.TemplateRunTypes;
import com.databuck.security.LogonManager;
import com.databuck.util.DateUtility;
import com.databuck.util.TokenValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "*")
@RestController
public class DataTemplateRestController {

	@Autowired
	PrimaryKeyMatchingResultService primaryKeyMatchingResultService;

	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;

	@Autowired
	private LogonManager logonManager;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	MYSQLConnection mysqlconnection;

	@Autowired
	PostgresConnection postgresConnection;

	@Autowired
	TeradataConnection teradataConnection;

	@Autowired
	OracleRACConnection OracleRACConnection;

	@Autowired
	CassandraConnection cassandraconnection;

	@Autowired
	HiveConnection hiveconnection;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;

	@Autowired
	MSSQLConnection mSSQLConnection;

	@Autowired
	BigQueryConnection bigQueryConnection;

	@Autowired
	AzureConnection azureConnection;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	ITaskDAO iTaskDAO;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	SnowflakeConnection snowFlakeConnection;

	@Autowired
	BatchFileSystem batchFileSystem;

	@Autowired
	S3BatchConnection s3BatchConnection;
	
	@Autowired
	private DashboardSummaryService dashboardSummaryService;

	@Autowired
	AzureDataLakeConnection azureDataLakeConnection;

	@Autowired
	AzureDataLakeGen2Connection azureDataLakeGen2Connection;

	@Autowired
	AzureCosmosDb azureCosmosDb;

	@Autowired
	public LoginService loginService;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	public IProjectService projService;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private ChecksCSVService csvService;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	SchemaDAOI schemaDao;

	@Autowired
	DataProfilingTemplateService dataProilingTemplateService;

	@Autowired
	public ITaskService iTaskService;

	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	private TokenValidator tokenValidator;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IImportExportUtilityDAO importExportDao;

	@Autowired
	DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	@Autowired
	public SchemaDAOI schemaDAOI;

	@Autowired
	GlobalRuleDAO globalruledao;
	
	@Autowired
	private RBACController rbacController;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private IListDataSourceDAO iListDataSourceDAO;

	@Autowired
	IValidationCheckDAO iValidationCheckDAO;

	@Autowired
	private DatabricksDeltaConnection databricksDeltaConnection;
	
	@Autowired
	DataConnectionService dataConnectionService;

	private static final Logger LOG = Logger.getLogger(DataTemplateRestController.class);

	@RequestMapping(value = "/dbconsole/getPaginatedDataTemplateList", method = RequestMethod.POST)
	public ResponseEntity<Object> getPaginatedDataTemplateList(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/getPaginatedDataTemplateList - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject oJsonResponse = new JSONObject();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("SearchByOption") || !requestBody.containsKey("FromDate")
					|| !requestBody.containsKey("ToDate") || !requestBody.containsKey("ProjectIds")
					|| !requestBody.containsKey("SearchText") || !requestBody.containsKey("domainId")) {
				throw new Exception("Required parameters not found.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("token "+headers.get("token").get(0).toString());
				// Map<String, Object> responseMap = new HashMap<String, Object>();
				HashMap<String, String> oPaginationParms = new HashMap<String, String>();
				LOG.debug("Getting request parameters  " + requestBody);
				for (String sParmName : new String[] { "SearchByOption", "FromDate", "ToDate", "ProjectIds",
						"SearchText","domainId" }) {
					oPaginationParms.put(sParmName, requestBody.get(sParmName));
				}
				
				if(requestBody.containsKey("filterByDerived")) {
					oPaginationParms.put("filterByDerived", requestBody.get("filterByDerived"));
				}

				oJsonResponse.put("ViewPageDataList",
						listdatasourcedao.getPaginatedDataTemplateJsonDatawithDomain(oPaginationParms));

				//
				// responseMap.put("ViewPageDataList",
				// listdatasourcedao.getPaginatedDataTemplateJsonData(oPaginationParms));

				response.put("result", oJsonResponse.toMap());
				response.put("message", "Template list fetched successfully.");
				response.put("status", "success");
				LOG.info("Template list fetched successfully.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/getPaginatedDataTemplateList - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/checkTemplateNameForDuplicate", method = RequestMethod.POST)
	public ResponseEntity<Object> checkTemplateNameForDuplicate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/checkTemplateNameForDuplicate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("templateName") || requestBody.get("templateName").trim().isEmpty()) {
				throw new Exception("Please enter Validation Template Name.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("token "+headers.get("token").get(0).toString());
				LOG.debug("Getting request parameters  " + requestBody);
				String dataTemplateName = requestBody.get("templateName");
				boolean isDataTemplateNameValid = false;
				Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");// "[^A-Za-z0-9_]"
				Matcher match = pattern.matcher(dataTemplateName);
				isDataTemplateNameValid = match.find();
				if (!isDataTemplateNameValid) {
					throw new Exception("Please start naming with alphabets and do not use spaces or special characters except _(underscore)");
				}

				String name = DataTemplateAddNewDAO.duplicatedatatemplatename(dataTemplateName);
				// System.out.println("name=" + name);
				if (name != null) {
					throw new Exception("This Validation Template Name is in use. Please choose another name.");
				}
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("isDataTemplateNameValid", isDataTemplateNameValid);
				response.put("result", responseMap);
				response.put("message", "Validation successfully runned on data template name.");
				response.put("status", "success");
				LOG.info("Validation successfully runned on data template name.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("isDataTemplateNameValid", false);
			response.put("result", responseMap);
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/checkTemplateNameForDuplicate - END");
		return new ResponseEntity<Object>(response, status);
	}
	
	@RequestMapping(value = "/dbconsole/checkCustomRuleNameForDuplicate", method = RequestMethod.POST)
	public ResponseEntity<Object> checkCustomRuleNameForDuplicate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/checkCustomRuleNameForDuplicate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("customRuleName") || requestBody.get("customRuleName").trim().isEmpty()) {
				throw new Exception("Please provide Custom Rule name.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("token "+headers.get("token").get(0).toString());
				LOG.debug("Getting request parameters  " + requestBody);
				String customRuleName = requestBody.get("customRuleName");
				boolean isCustomRuleNameValid = false;
				
				String name = DataTemplateAddNewDAO.duplicateCustomrulename(customRuleName);
				// System.out.println("name=" + name);
				if (name != null) {
					throw new Exception("This Custom Rule  name is in use. Please choose another name.");
				}
				else {
					isCustomRuleNameValid = true;
				}
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("isCustomRuleNameValid", isCustomRuleNameValid);
				response.put("result", responseMap);
				response.put("message", "Validation successfully runned on Custom Rule name.");
				response.put("status", "success");
				LOG.info("Validation successfully runned on Custom Rule name.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("isCustomRuleNameValid", false);
			response.put("result", responseMap);
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/checkCustomRuleNameForDuplicate - END");
		return new ResponseEntity<Object>(response, status);
	}


	@RequestMapping(value = "/dbconsole/getTableNamesByDataConnNameNSchemaID", method = RequestMethod.POST)
	public ResponseEntity<Object> getTableNamesByDataConnNameNSchemaID(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/getTableNamesByDataConnNameNSchemaID - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("schemaId") || !requestBody.containsKey("dataConnectionName")) {
				throw new Exception("Please provide required parameters.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("Getting request parameters  " + requestBody);
				TableListforSchema tableListFrom = dataConnectionService.getTableListForSchema(requestBody.get("schemaId"));
				if (tableListFrom == null) {
					throw new Exception("Failed to retrieve table list.");
				}
				List<Object> tableListWithStatus = getTableListWithProcessedStatus(tableListFrom,
						requestBody.get("schemaId"));
				if (tableListWithStatus.isEmpty()) {
					throw new Exception("Failed to retrieve processed table list.");
				}
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("tableList", tableListWithStatus);
				response.put("result", responseMap);
				response.put("message", "Table list fetched successfully.");
				response.put("status", "success");
				status = HttpStatus.OK;
				LOG.info("Table list fetched successfully.");
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/getTableNamesByDataConnNameNSchemaID - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/getProjectlist", method = RequestMethod.GET)
	public ResponseEntity<Object> getProjectlist(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getProjectlist - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				List<Project> aProjectList = null;

				// aProjectList = loginService.getAllDistinctProjectListForUser(oSession);

				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("AllProjectList", aProjectList);
				// responseMap.put("SecurityFlags", getSecurityFlags(CURRENT_MODULE_NAME,
				// oSession));
				response.put("result", responseMap);
				response.put("message", "Project list fetched successfully.");
				response.put("status", "success");
				status = HttpStatus.OK;
				LOG.info("Project list fetched successfully.");
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/getProjectlist - END");
		return new ResponseEntity<Object>(response, status);
	}
	
	private List<Object> getTableListWithProcessedStatus(TableListforSchema tableListFrom, String selSchemaId) {
		LOG.info("getProccessedTableList - START");
		LOG.debug("\n====> Fetching table list for Connection Id: [" + selSchemaId + "]");
		LOG.debug("Schema id:" + selSchemaId);
		List<Object> tableProcessedList = dashboardSummaryService.getProcessedTableList(tableListFrom, selSchemaId);
		LOG.info("getProccessedTableList - End");
		return tableProcessedList;
	}

	public List<String> getTableListForSchema(String selSchemaId, String selLocationName) {
		LOG.info("getTableListForSchema - START");
		LOG.debug("\n====> Fetching table list for Connection Id: [" + selSchemaId + "]");
		LOG.debug("Schema id:" + selSchemaId);
		LOG.debug("Location name:" + selLocationName);

		List<String> tableListFrom = new ArrayList<String>();
		List<ListDataSchema> listDataSchema = DataTemplateAddNewDAO.getListDataSchema(Long.parseLong(selSchemaId));
		String hostURI = listDataSchema.get(0).getIpAddress();
		String database = listDataSchema.get(0).getDatabaseSchema();
		String userlogin = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String port = listDataSchema.get(0).getPort();
		String domain = listDataSchema.get(0).getDomain();
		String serviceName = listDataSchema.get(0).getKeytab();
		String sslEnb = listDataSchema.get(0).getSslEnb();
		String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
		String trustPassword = listDataSchema.get(0).getTrustPassword();
		String principle = listDataSchema.get(0).getDomain();
		String keytab = listDataSchema.get(0).getKeytab();
		String krb5conf = listDataSchema.get(0).getKrb5conf();
		String gatewayPath = listDataSchema.get(0).getGatewayPath();
		String jksPath = listDataSchema.get(0).getJksPath();
		String folderPath = listDataSchema.get(0).getFolderPath();
		String fileNamePattern = listDataSchema.get(0).getFileNamePattern();
		String accessKey = listDataSchema.get(0).getAccessKey();
		String secretKey = listDataSchema.get(0).getSecretKey();
		String bucketName = listDataSchema.get(0).getBucketName();
		String partitionedFolders = listDataSchema.get(0).getPartitionedFolders();
		int maxFolderDepth = listDataSchema.get(0).getMaxFolderDepth();
		String multiFolderEnabled = listDataSchema.get(0).getMultiFolderEnabled();

		String bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
		String privatekeyId = listDataSchema.get(0).getPrivatekeyId();
		String privatekey = listDataSchema.get(0).getPrivatekey();
		String clientId = listDataSchema.get(0).getClientId();
		String clientEmail = listDataSchema.get(0).getClientEmail();
		String datasetName = listDataSchema.get(0).getDatasetName();

		String azureClientId = listDataSchema.get(0).getAzureClientId();
		String azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
		String azureTenantId = listDataSchema.get(0).getAzureTenantId();
		String azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
		String azureFilePath = listDataSchema.get(0).getAzureFilePath();

		String schemaType = listDataSchema.get(0).getSchemaType();
		String schemaName = listDataSchema.get(0).getSchemaName();
		String kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();
		String httpPath = listDataSchema.get(0).getHttpPath();
		String azureAuthenticationType = listDataSchema.get(0).getAzureAuthenticationType();

		/*
		 * When KMS Auth is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(schemaType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(schemaName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					hostURI = conn_user_details.get("hostname");
					port = (schemaType.equalsIgnoreCase("Oracle")) ? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.error("\n====> KMS Authentication is not supported for [" + schemaType + "] !!");

			if (!flag) {
				LOG.error(
						"\n====> Unable to fetch table list, failed to get connection details from logon manager !!");
				LOG.info("getTableListForSchema - END");
				return tableListFrom;
			}
		}

		if (selLocationName.equals("Oracle")) {
			tableListFrom = oracleconnection.getListOfTableNamesFromOracle(hostURI, userlogin, password, port,
					database);
			// System.out.println("Table name:"+tableListFromOracle.get(0));
		}
		if (selLocationName.equals("Postgres")) {
			tableListFrom = postgresConnection.getListOfTableNamesFromPostgres(hostURI, userlogin, password, port,
					database, sslEnb);
			// System.out.println("Table name:"+tableListFromOracle.get(0));
		} else if (selLocationName.equals("MYSQL")) {
			tableListFrom = mysqlconnection.getListOfTableNamesFromMYSql(hostURI, userlogin, password, port, database,
					sslEnb);
		} else if (selLocationName.equals("MSSQL")) {
			tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSql(hostURI, userlogin, password, port, database); 
		} else if (selLocationName.equals("MSSQLAD")) {
			tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSqlAzure(hostURI, userlogin, password, port, database, azureAuthenticationType);
		}else if (selLocationName.equals("Teradata")) {
			tableListFrom = teradataConnection.getListOfTableNamesFromTeradata(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equals("Vertica")) {
			tableListFrom = verticaconnection.getListOfTableNamesFromVertica(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equalsIgnoreCase("MSSQLActiveDirectory")) {
			tableListFrom = msSqlActiveDirectoryConnectionObject.getListOfTableNamesFromMsSqlActiveDirectory(hostURI,
					userlogin, password, port, domain, database);
		} else if (selLocationName.equalsIgnoreCase("Amazon Redshift")) {
			tableListFrom = amazonRedshiftConnection.getListOfTableNamesFromAmazonRedshift(hostURI, userlogin, password,
					port, domain, database);
		} else if (selLocationName.equalsIgnoreCase("Cassandra")) {
			tableListFrom = cassandraconnection.getListOfTableNamesFromCassandra(hostURI, userlogin, password, port,
					database, domain);
		} else if (selLocationName.equalsIgnoreCase("Oracle RAC")) {
			tableListFrom = OracleRACConnection.getListOfTableNamesFromOracleRAC(hostURI, userlogin, password, port,
					database, domain, serviceName);
		} else if (selLocationName.equalsIgnoreCase("Hive") || selLocationName.equalsIgnoreCase("Hive Kerberos")
				|| selLocationName.equalsIgnoreCase("ClouderaHive") || selLocationName.equalsIgnoreCase("MapR Hive")) {
			boolean isKerberosEnabled = selLocationName.equals("Hive Kerberos") ? true : false;
			// Add special condition for MapR hive

			// Get DomainId and Domain Name of the connection, under which it is created.
			Integer domainId = listDataSchema.get(0).getDomainId();
			String domainName = iTaskDAO.getDomainNameById(domainId.longValue());

			ListDataSchema ldd = listDataSchema.get(0);
			String clusterPropertyCategory = ldd.getClusterPropertyCategory();
			LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);
			long idDataSchema = ldd.getIdDataSchema();

			if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {
				String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);
				ldd.setDomainName(domainName);
				tableListFrom = remoteClusterAPIService.getListOfTableNamesByRemoteCluster(ldd, encryptedToken);
			}else{
				if (selLocationName.equalsIgnoreCase("MapR Hive")) {

					tableListFrom = hiveconnection.getListOfTableNamesFromMapRHive(hostURI, database, userlogin,
							password, port, domainName);
				} else {
					tableListFrom = hiveconnection.getListOfTableNamesFromHive(selLocationName, hostURI, database,
							userlogin, password, port, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
							krb5conf, principle, jksPath);
				}
			}

		} else if (selLocationName.equalsIgnoreCase("Hive knox")) {
			tableListFrom = hiveconnection.getListOfTableNamesFromHiveKnox(hostURI, database, userlogin, password, port,
					jksPath, gatewayPath);

		} else if (selLocationName.equals("SnowFlake")) {
			tableListFrom = snowFlakeConnection.getListOfTableNamesFromSnowflake(hostURI, userlogin, password, port,
					database);
		} else if (selLocationName.equalsIgnoreCase("FileSystem Batch")) {
			tableListFrom = batchFileSystem.getListOfTableNamesFromFolder(folderPath, fileNamePattern);
		} else if (selLocationName.equalsIgnoreCase("S3 Batch")) {
			tableListFrom = s3BatchConnection.getListOfTableNamesFromFolder(accessKey, secretKey, bucketName,
					folderPath, fileNamePattern, multiFolderEnabled);
		} else if (selLocationName.equalsIgnoreCase("S3 IAMRole Batch")) {
			tableListFrom = s3BatchConnection.getListOfTableNamesInFolderForS3IAMRole(bucketName, folderPath,
					fileNamePattern, partitionedFolders, maxFolderDepth);
		} else if (selLocationName.equalsIgnoreCase("BigQuery")) {
			tableListFrom = bigQueryConnection.getListOfTableNamesFromBigQuery(bigQueryProjectName, privatekeyId,
					privatekey, clientId, clientEmail, datasetName);
		} else if (selLocationName.equalsIgnoreCase("AzureSynapseMSSQL")) {
			tableListFrom = azureConnection.getListOfTableNamesFromAzure(hostURI, port, database, userlogin, password);
		} else if (selLocationName.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
			tableListFrom = azureDataLakeConnection.getListOfFilesFromDataLake(azureClientId, azureClientSecret,
					azureTenantId, azureServiceURI, azureFilePath);
		} else if (selLocationName.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			tableListFrom = azureDataLakeGen2Connection.getFolderListFromDataLake(accessKey, secretKey, bucketName,
					folderPath);
		}

		else if (selLocationName.equalsIgnoreCase("AzureCosmosDB")) {

			tableListFrom = azureCosmosDb.getFolderListFromCosmosDataLake(database, hostURI, port, secretKey);

		}else if (selLocationName.equals("DatabricksDeltaLake")) {
			tableListFrom = databricksDeltaConnection.getListOfTableNamesFromDatabricksDeltaLake(hostURI, userlogin, password, database, port, httpPath);
		}
		LOG.info("getTableListForSchema - END");
		return tableListFrom;
	}

	@RequestMapping(value = "/dbconsole/getDataConnectionName", method = RequestMethod.POST)
	public ResponseEntity<Object> getDataConnectionName(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/getDataConnectionName - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

			// Check whether all required parameters are available.
			if (!requestBody.containsKey("connectionType")) {
				throw new Exception("Please provide connection type.");
			}

			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("Getting request parameters  " + requestBody);
				Long projectId = Long.parseLong(String.valueOf(requestBody.get("projectId")));
				Integer domainId = Integer.parseInt(String.valueOf(requestBody.get("domainId")));
				List<Map<String, Object>> dataTemplateForRequiredLocation = listdatasourcedao
						.getDataTemplateForRequiredConnectionType(requestBody.get("connectionType"), projectId,
								domainId);
				Map<String, Object> responseMap = new HashMap<String, Object>();
				responseMap.put("connectionList", dataTemplateForRequiredLocation);
				response.put("result", responseMap);
				response.put("message", "Data connection names retrieved successfully.");
				response.put("status", "success");
				LOG.info("Data connection names retrieved successfully.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/getDataConnectionName - END");
		return new ResponseEntity<Object>(response, status);
	}

	private List<DomainProject> getDomainProjectList(String token) {

		// Fetch token details
		UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

		// Get ProjectList
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

		List<Project> projList = null;
		if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {

			String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
			ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));

			projList = loginService.getProjectListOfUser(userLdapGroups);
		} else {
			projList = projService.getAllProjectsOfAUser(userToken.getEmail());
		}
		return projService.getDomainProjectAssociationOfCurrentUser(projList);
	}

	// Angular team should fix the issue to invoke multipart
	// @PostMapping(value = "/dbconsole/createDataTemplate", produces =
	// MediaType.APPLICATION_JSON_VALUE, consumes = {
	// MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	// public ResponseEntity<Object> createDataTemplate(@RequestHeader HttpHeaders
	// headers,
	// @RequestPart(value = "dataTemplate", required = true)
	// DataTemplateCreateRequest dataTemplateCreateRequest,
	// @Nullable @RequestPart(value = "uploadFile", required = false) MultipartFile
	// file) {

	@PostMapping(value = "/dbconsole/createDataTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> dataTemplateCreate(@RequestHeader HttpHeaders headers,
			@RequestBody(required = true) DataTemplateCreateRequest dataTemplateCreateRequest,
			@Nullable @RequestParam(value = "uploadFile", required = false) MultipartFile file) {
		LOG.info("dbconsole/createDataTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			String token = headers.get("token").get(0);
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {

					if (dataTemplateCreateRequest.getIdUser() == null) {
						throw new Exception("IdUser is empty in the request");
					}
					if(dataTemplateCreateRequest.getDataTemplateName().trim().isEmpty()) {
						throw new Exception("Please enter Validation Template Name.");
					}

					long idUser = (Long) dataTemplateCreateRequest.getIdUser();
					LOG.debug("idUser=" + idUser);

					String createdByUser = "";
					if (dataTemplateCreateRequest.getCreatedByUser() != null) {
						createdByUser = dataTemplateCreateRequest.getCreatedByUser();
					}

					String activeDirectoryFlag = appDbConnectionProperties
							.getProperty("isActiveDirectoryAuthentication");
					DateUtility.DebugLog("createDataTemplate 01",
							String.format("activeDirectoryFlag = %1$s", activeDirectoryFlag));
					createdByUser = (activeDirectoryFlag.equalsIgnoreCase("Y")) ? createdByUser
							: userDAO.getUserNameByUserId(idUser);
					DateUtility.DebugLog("createDataTemplate 02", String.format("createdByUser = %1$s", createdByUser));

					String datalocation = "";
					// changes regarding kafka
					if (dataTemplateCreateRequest.getDataLocation() != null)
						datalocation = dataTemplateCreateRequest.getDataLocation();
					String HostURI = null;
					String folder = null;
					
					String dbName = DataTemplateAddNewDAO.duplicatedatatemplatename(dataTemplateCreateRequest.getDataTemplateName());
						if (dbName != null) {
						response.put("status", "failed");
						response.put("message", "Failed to create data template. Template name is already in use.");
						LOG.error("This Data Template name is in use. Please choose another name : "+dataTemplateCreateRequest.getDataTemplateName());
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}

					if (datalocation.equals("File Management") || datalocation.equals("File System")
							|| datalocation.equals("HDFS") || datalocation.equals("S3")
							|| datalocation.equals("MapR FS") || datalocation.equals("MapR DB")
							|| datalocation.equals("AzureDataLakeStorageGen2")) {

						HostURI = dataTemplateCreateRequest.getHostName();
						folder = dataTemplateCreateRequest.getSchemaName();

					} else if (datalocation.equals("Kafka")) {
						HostURI = dataTemplateCreateRequest.getSrcBrokerUri();
						folder = dataTemplateCreateRequest.getSrcTopicName();
					}

					String dataFormat = dataTemplateCreateRequest.getDataFormat() != null
							? dataTemplateCreateRequest.getDataFormat()
							: "";
					String userlogin = dataTemplateCreateRequest.getUserName() != null
							? dataTemplateCreateRequest.getUserName()
							: "";
					String password = dataTemplateCreateRequest.getPwd() != null ? dataTemplateCreateRequest.getPwd()
							: "";
					String schemaName = dataTemplateCreateRequest.getSchemaName() != null
							? dataTemplateCreateRequest.getSchemaName()
							: "";
					String tar_brokerUri = dataTemplateCreateRequest.getTarBrokerUri() != null
							? dataTemplateCreateRequest.getTarBrokerUri()
							: "";
					String tar_topicName = dataTemplateCreateRequest.getTarTopicName() != null
							? dataTemplateCreateRequest.getTarTopicName()
							: "";
					LOG.debug("tar_brokerUri ->" + tar_brokerUri);
					LOG.debug("tar_topicName ->" + tar_topicName);

					LOG.info("hello create form");
					String DataTemplateName = dataTemplateCreateRequest.getDataTemplateName() != null
							? dataTemplateCreateRequest.getDataTemplateName()
							: "";
					LOG.debug("DataTemplateName=" + DataTemplateName);
					String description = dataTemplateCreateRequest.getDescription() != null
							? dataTemplateCreateRequest.getDescription()
							: "";
					LOG.debug("description=" + description);

					String schema = dataTemplateCreateRequest.getIdDataSchema() != null
							? String.valueOf(dataTemplateCreateRequest.getIdDataSchema())
							: "";

					Long idDataSchema = dataTemplateCreateRequest.getIdDataSchema() != null
							? dataTemplateCreateRequest.getIdDataSchema()
							: 0L;
					LOG.debug("idDataSchema=" + idDataSchema);

					String headerId = dataTemplateCreateRequest.getHeaderId() != null
							? dataTemplateCreateRequest.getHeaderId()
							: "N";
					LOG.debug("headerId=" + headerId);
					String rowsId = dataTemplateCreateRequest.getRowsId() != null
							? dataTemplateCreateRequest.getRowsId()
							: "";
					LOG.debug("rowsId=" + rowsId);

					// tablename for query

					String tableName = dataTemplateCreateRequest.getTableName() != null
							? dataTemplateCreateRequest.getTableName()
							: "";
					LOG.debug("tableName=" + tableName);
					
					Integer domainId = dataTemplateCreateRequest.getDomainId()!=null
							?dataTemplateCreateRequest.getDomainId():0;

					if (dataTemplateCreateRequest.getTableName() != null && tableName.equals("")
							&& dataTemplateCreateRequest.getTableNameList() != null) {

						// tableName = request.getParameter("");
						LOG.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& tableName"
								+ dataTemplateCreateRequest.getTableNameList());

						tableName = dataTemplateCreateRequest.getTableNameList();
					}

					LOG.debug(".................After IF tableName=" + tableName);
					LOG.debug("datalocation=" + datalocation);

					String whereCondition = dataTemplateCreateRequest.getWhereId() != null
							? dataTemplateCreateRequest.getWhereId()
							: "";
					LOG.debug("whereCondition=" + whereCondition);

					String queryCheckbox = dataTemplateCreateRequest.getQueryCheckboxId() != null
							? dataTemplateCreateRequest.getQueryCheckboxId()
							: "N";
					LOG.debug("queryCheckbox=" + queryCheckbox);

					String historicDateTable = dataTemplateCreateRequest.getHistoricDateTable() != null
							? dataTemplateCreateRequest.getHistoricDateTable()
							: "";
					LOG.debug("historicDateTable=" + historicDateTable);

					if (queryCheckbox != null && queryCheckbox.equalsIgnoreCase("Y")) {
						historicDateTable = (historicDateTable != null) ? historicDateTable.trim() : "";
					} else {
						historicDateTable = "";
					}

					String lstTable = dataTemplateCreateRequest.getSelectedTables() != null
							? dataTemplateCreateRequest.getSelectedTables()
							: "";
					LOG.debug("Selected Tables=" + lstTable);
					String queryTextbox = dataTemplateCreateRequest.getQueryTextboxId() != null
							? dataTemplateCreateRequest.getQueryTextboxId()
							: "";
					LOG.debug("queryTextbox=" + queryTextbox);

					LOG.debug("@@@@@@@@@@@@@@@@@ lstTable =>" + lstTable);

					// Property to check if query have to be applied on File
					String fileQueryCheckBoxId = dataTemplateCreateRequest.getFileQueryCheckboxid() != null
							? dataTemplateCreateRequest.getFileQueryCheckboxid()
							: "N";
					LOG.debug("fileQueryCheckBoxId=" + fileQueryCheckBoxId);

					// Property to get the query which have to be applied on File
					String fileQueryTextbox = dataTemplateCreateRequest.getFileQueryTextboxid() != null
							? dataTemplateCreateRequest.getFileQueryTextboxid()
							: "";
					LOG.debug("filequerytextboxid=" + fileQueryTextbox);

					/*
					 * When datalocation is "File System and query is enabled on it
					 */
					if (datalocation.equals("File System") && fileQueryCheckBoxId.equalsIgnoreCase("Y")
							&& fileQueryTextbox != null && !fileQueryTextbox.trim().isEmpty()) {
						queryCheckbox = "Y";
						queryTextbox = fileQueryTextbox;
						LOG.debug("queryCheckbox=" + queryCheckbox);
						LOG.debug("queryTextbox=" + queryTextbox);
					}

					String incrementalType = dataTemplateCreateRequest.getIncrementalSourceId() != null
							? dataTemplateCreateRequest.getIncrementalSourceId()
							: "";
					LOG.debug("incrementalType=" + incrementalType);
					String dateFormat = dataTemplateCreateRequest.getDateFormatId() != null
							? dataTemplateCreateRequest.getDateFormatId()
							: "";
					LOG.debug("dateFormat=" + dateFormat);
					String sliceStart = dataTemplateCreateRequest.getSliceStartId() != null
							? dataTemplateCreateRequest.getSliceStartId()
							: "";
					LOG.debug("sliceStart=" + sliceStart);
					String sliceEnd = dataTemplateCreateRequest.getSliceEndId() != null
							? dataTemplateCreateRequest.getSliceEndId()
							: "";
					LOG.debug("sliceEnd=" + sliceEnd);
					String profilingEnabled = dataTemplateCreateRequest.getProfilingEnabled() != null
							? dataTemplateCreateRequest.getProfilingEnabled()
							: "";
					LOG.debug("profilingEnabled=" + profilingEnabled);

					/*
					 * Pradeep 3/3/2020 Global threshold CR List<GloabalRule> selected_list no
					 * longer used so set as null to nullify effect
					 */
					// List<GloabalRule> selected_list = getSelectedRuleDetails(session);
					List<GloabalRule> selected_list = null;
					String sDomainOptionSelected = dataTemplateCreateRequest.getDomainfunction() != null
							? dataTemplateCreateRequest.getDomainfunction()
							: "";

					/*
					 * Pradeep 18-Jan-2021 Domain and Project to be picked up from connection,
					 * domain dropdown removed from UI
					 */
					HashMap<String, Long> oDomainProject = schemaDao.getDomainProjectFromSchema(idDataSchema);
					Long projectId = oDomainProject.get("ProjectId");
					int nDomainId = oDomainProject.get("DomainId").intValue();

					// When idDataSchema is null then take the current project domain
					if (idDataSchema == null || idDataSchema <= 0l) {
						projectId = dataTemplateCreateRequest.getProjectId();
						nDomainId = dataTemplateCreateRequest.getDomainId();
					}

					String advancedRulesEnabled = dataTemplateCreateRequest.getAdvancedRulesEnabled() != null
							? dataTemplateCreateRequest.getAdvancedRulesEnabled()
							: "";
					LOG.debug("advancedRulesEnabled=" + advancedRulesEnabled);

					// Rolling Header Info
					String rollingHeaderPresent = dataTemplateCreateRequest.getRollingHeaderPresent() != null
							? dataTemplateCreateRequest.getRollingHeaderPresent()
							: "N";
					LOG.debug("rollingHeaderPresent=" + rollingHeaderPresent);

					String rollingColumn = dataTemplateCreateRequest.getRollingColumn() != null
							? dataTemplateCreateRequest.getRollingColumn()
							: "";
					LOG.debug("rollingColumn=" + rollingColumn);

					if (rollingHeaderPresent == null || !rollingHeaderPresent.equalsIgnoreCase("Y")) {
						rollingHeaderPresent = "N";
					}

					String message = "Validation Template creation is in progress, status will be notified to registered email !!";
					String status = "InProgress";

					//Changes for DC 1782 -Mamta
					if (profilingEnabled.equals("Y") &&  advancedRulesEnabled.equals("N"))
	                {
             		message =  "Validation Template creation with Profiling is in progress, status will be notified to registered email !!";
					}
					if (profilingEnabled.equals("Y") &&  advancedRulesEnabled.equals("Y"))
	                {
             		message =  "Validation Template creation with Profiling and Rules is in progress, status will be notified to registered email !!";
					}

					if (datalocation.equals("File Management") || datalocation.equals("Kafka")
							|| (queryCheckbox.equalsIgnoreCase("N") && lstTable != null
									&& lstTable.split(",").length > 1)) {
						// modelAndView.setViewName("Data Template Created Successfully");
						message = "Data Template Created Successfully";
						status = "Completed";
					}

					// Create multiple template
					if (!(datalocation.equals("File Management") || datalocation.equals("File System")
							|| datalocation.equals("HDFS") || datalocation.equals("S3") || datalocation.equals("Kafka")
							|| datalocation.equals("MapR FS") || datalocation.equals("AzureDataLakeStorageGen2")
							|| datalocation.equalsIgnoreCase("DatabricksDeltaLake"))
							&& queryCheckbox.equalsIgnoreCase("N") && lstTable != null && !lstTable.trim().isEmpty()
							&& lstTable.split(",").length > 1) {

						dataProilingTemplateService.createTemplatesAsync(null, idDataSchema.longValue(), datalocation,
								tableName, DataTemplateName, description, schema, headerId, rowsId, whereCondition,
								queryCheckbox, lstTable, queryTextbox, incrementalType, dateFormat, sliceStart,
								sliceEnd, idUser, HostURI, folder, dataFormat, userlogin, password, schemaName, file,
								tar_brokerUri, tar_topicName, profilingEnabled, advancedRulesEnabled, selected_list,
								rollingHeaderPresent, rollingColumn, historicDateTable, createdByUser, projectId);

					}

					// Create single template
					else {

						if (!(datalocation.equals("File Management") || datalocation.equals("File System")
								|| datalocation.equals("HDFS") || datalocation.equals("S3")
								|| datalocation.equals("Kafka") || datalocation.equals("MapR FS")
								|| datalocation.equals("AzureDataLakeStorageGen2")
								|| datalocation.equalsIgnoreCase("DatabricksDeltaLake")) && lstTable != null
								&& !lstTable.trim().isEmpty() && lstTable.split(",").length == 1) {

							String tablesList = lstTable.trim().replace("[", "").replace("]", "");
							tableName = tablesList.split(",")[0];
							tableName = tableName.replace("\"", "");
						}
						
						CompletableFuture<Long> result = dataProilingTemplateService.createDataTemplateWithDomainId(null,
								idDataSchema, datalocation, tableName, DataTemplateName, description, schema, headerId,
								rowsId, whereCondition, queryCheckbox, lstTable, queryTextbox, incrementalType,
								dateFormat, sliceStart, sliceEnd, idUser, HostURI, folder, dataFormat, userlogin,
								password, schemaName, file, tar_brokerUri, tar_topicName, profilingEnabled,
								selected_list, projectId, advancedRulesEnabled, createdByUser, rollingHeaderPresent,
								rollingColumn, historicDateTable, null, null,domainId);

						Long idData = 0l;
						try {
							idData = result.get();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (idData != null && idData != 0l) {
							dataTemplateAddNewDAO.populateColumnProfileMasterTable(idData, nDomainId);

							DateUtility.DebugLog("Rules Auto Discovery 03", "End processing/linking done");

							String uniqueId = "";
							try {
								uniqueId = dataProilingTemplateService.triggerDataTemplate(idData, datalocation,
										profilingEnabled, advancedRulesEnabled).get();
							} catch (Exception e) {
								e.printStackTrace();
								LOG.error(e.getMessage());
							}
							LOG.debug("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
									+ " is in queue for execution !!");

							response.put("templateId", String.valueOf(idData));
							response.put("uniqueId", uniqueId);

							//changes regarding Audit trail
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							iTaskDAO.addAuditTrailDetail(idUser,userDAO.getUserNameByUserId(Long.valueOf(idUser)),DatabuckConstants.DBK_FEATURE_TEMPLATE,formatter.format(new Date()),idData,DatabuckConstants.ACTIVITY_TYPE_CREATED,DataTemplateName);

						} else {
							message = "Validation Template not Created Successfully";
							status = "Completed";
							LOG.error(message);
						}
					}

					response.put("templateName", DataTemplateName);
					response.put("message", message);
					response.put("status", status);
					
					return ResponseEntity.ok(response);
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
			LOG.info("dbconsole/createDataTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "dbconsole/templateStatusPoll", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> startStatuspoll(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/templateStatusPoll - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + params);
					if (params.containsKey("idData") && params.containsKey("uniqueId")) {
						Long idData = Long.valueOf(params.get("idData"));
						String uniqueId = params.get("uniqueId");
						String templateStatus = iTaskService.getTemplateCreationJobStatusById(idData, uniqueId);
						if (templateStatus == null || templateStatus.isEmpty() || templateStatus.length() == 0) {
							templateStatus = "killed";
						}
						String status = "Task " + templateStatus;
						LOG.debug("\n====>idData = " + idData + " =======uniqueId = " + uniqueId);

						// for showing task % status
						int percentage = 5;
						percentage = iTaskService.getStatusOfDfReadTaskForTemplate(idData);
						if (percentage >= 30) {
							double count = iTaskService.getCountOfTasksEnabledForTemplate(idData);
							double passedStatus = iTaskService.getCountOfTasksPassedForTemplate(idData);
							percentage = percentage + (int) ((passedStatus / count) * 70);
							if (percentage > 100) {
								percentage = 100;
							}
						}

						// Check if job is completed, killed or failed and log it
						if (templateStatus != null && (templateStatus.equalsIgnoreCase("completed")
								|| templateStatus.equalsIgnoreCase("failed")
								|| templateStatus.equalsIgnoreCase("killed"))) {
							LOG.debug("\n====>Template with Id [" + idData + "] and uniqueId [" + uniqueId
									+ "] is " + templateStatus + "!!");
						}
						response.put("percentage", percentage);
						// response.put("success", status);
						response.put("status", status);
						
						return ResponseEntity.ok(response);
					} else {
						response.put("status", "failed");
						response.put("message", "Parameters idData or UniqueId not found");
						LOG.error("Parameters idData or UniqueId not found");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
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
			LOG.info("dbconsole/templateStatusPoll - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		}
	}

	@RequestMapping(value = "/dbconsole/deleteDataTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteDataTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Integer> params) {
		LOG.info("dbconsole/deleteDataTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
		LOG.debug("Getting request parameters  " + params);
		int dataTemplateId = params.get("dataTemplateId");
		String status = listdatasourcedao.checkTemplateStatus(dataTemplateId);
		if(!status.equals("") && status != null) {
			LOG.debug("Template status--->" +status);
			if(status.equalsIgnoreCase("queued") || status.equalsIgnoreCase("started") || status.equalsIgnoreCase("in progress")) {
			response.put("status", "failed");
			response.put("message", "Unable to delete Validation template as it is in process.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			if (taskService.checkUserPermission(userToken, "D", "Data Template")) {

				ListDataSource dataFromListDataSourcesOfIdData = listdatasourcedao.getDataFromListDataSourcesOfIdData((long) dataTemplateId);

				//changes regarding Audit trail
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				int deleteDataSource = listdatasourcedao.deleteDataSource(dataTemplateId);

				if(dataFromListDataSourcesOfIdData.getDataLocation().equalsIgnoreCase("Derived")){
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_DERIVED_TEMPLATE,formatter.format(new Date()),Long.valueOf(dataTemplateId),DatabuckConstants.ACTIVITY_TYPE_DELETED,dataFromListDataSourcesOfIdData.getName());
				}else
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_TEMPLATE,formatter.format(new Date()),Long.valueOf(dataTemplateId),DatabuckConstants.ACTIVITY_TYPE_DELETED,dataFromListDataSourcesOfIdData.getName());

				response.put("status", "success");
				response.put("message", "Successfully deleted data template.");
				response.put("result", deleteDataSource);
				LOG.info("Successfully deleted data template.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Permission denied.");
				LOG.info("Permission denied.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to delete data template.");
			LOG.error(e.getMessage());
			response.put("stackTrace", e.getMessage());
			LOG.info("dbconsole/deleteDataTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/dbconsole/deactivateDataTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> deactivateDataTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Integer> params) {
		LOG.info("dbconsole/deleteDataTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
		LOG.debug("Getting request parameters  " + params);
		int dataTemplateId = params.get("dataTemplateId");
		String status = listdatasourcedao.checkTemplateStatus(dataTemplateId);
		if(!status.equals("") && status != null) {
			LOG.debug("Template status--->" +status);
			if(status.equalsIgnoreCase("queued") || status.equalsIgnoreCase("started") || status.equalsIgnoreCase("in progress")) {
			response.put("status", "failed");
			response.put("message", "Unable to delete Validation template as it is in process.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		}
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			if (taskService.checkUserPermission(userToken, "D", "Data Template")) {

				ListDataSource dataFromListDataSourcesOfIdData = listdatasourcedao.getDataFromListDataSourcesOfIdData((long) dataTemplateId);

				//changes regarding Audit trail
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				boolean deleteDataSource = listdatasourcedao.deactivateDataSource(dataTemplateId);

				if(dataFromListDataSourcesOfIdData.getDataLocation().equalsIgnoreCase("Derived")){
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_DERIVED_TEMPLATE,formatter.format(new Date()),Long.valueOf(dataTemplateId),DatabuckConstants.ACTIVITY_TYPE_DELETED,dataFromListDataSourcesOfIdData.getName());
				}else
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_TEMPLATE,formatter.format(new Date()),Long.valueOf(dataTemplateId),DatabuckConstants.ACTIVITY_TYPE_DELETED,dataFromListDataSourcesOfIdData.getName());

				response.put("status", "success");
				response.put("message", "Data template is deactivate successfully.");
				response.put("result", deleteDataSource);
				LOG.info("Successfully deleted data template.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Permission denied.");
				LOG.info("Permission denied.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to delete data template.");
			LOG.error(e.getMessage());
			response.put("stackTrace", e.getMessage());
			LOG.info("dbconsole/deleteDataTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/copyTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> copyTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/copyTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
		LOG.debug("Getting request parameters  " + params);
		String newTemplateName = String.valueOf(params.get("newTemplateName"));
		String dbName = DataTemplateAddNewDAO.duplicatedatatemplatename(newTemplateName);
		if (dbName != null) {
			response.put("status", "failed");
			response.put("message", "Failed to copy data template. Template name is already in use.");
			LOG.error("This Data Template name is in use. Please choose another name : "+newTemplateName);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		// Mamta 29/Aug/2022
		String createdByUser = String.valueOf(params.get("createdBy"));
		long dataTemplateId = Long.parseLong(String.valueOf(params.get("dataTemplateId")));
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			Long newTemplateId = templateviewdao.copyTemplate(dataTemplateId, newTemplateName, createdByUser);
			//check it its derived template
			ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(newTemplateId);
			if(listDataSource != null && listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
				// Copy the listDerivedDataSource
				templateviewdao.copyDerivedTemplate(dataTemplateId, newTemplateId, createdByUser, newTemplateName);
			}
			// userDAO.getUserNameByUserId(userToken.getIdUser()));
			if (newTemplateId != null && newTemplateId != 0l) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (listDataSource != null && listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_DERIVED_TEMPLATE, formatter.format(new Date()), newTemplateId,
							DatabuckConstants.ACTIVITY_TYPE_CREATED, newTemplateName);
				} else {
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_TEMPLATE, formatter.format(new Date()), newTemplateId,
							DatabuckConstants.ACTIVITY_TYPE_CREATED, newTemplateName);
				}
				response.put("status", "success");
				response.put("message", "Successfully copied data template.");
				response.put("result", newTemplateId);
				LOG.info("Successfully copied data template.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Failed to copy data template.");
				LOG.error("Failed to copy data template.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to copy data template.");
			response.put("stackTrace", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/copyTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/rerunDataTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> rerunDataTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/rerunDataTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
		LOG.debug("Getting request parameters  " + params);
		Long dataTemplateId = params.get("dataTemplateId");
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			if (taskService.checkUserPermission(userToken, "C", "Data Template")) {
				ListDataSource listDataSource = importExportDao.getDataFromListDataSources(dataTemplateId);
				if (listDataSource != null) {
					// Check Current status of template
					if (!iTaskDAO.isTemplateJobQueuedOrInProgress(dataTemplateId)) {
						String uniqueId = "";
						// Place template to queue
						if (listDataSource.getTemplateCreateSuccess() != null
								&& listDataSource.getTemplateCreateSuccess().equalsIgnoreCase("N")) {
							uniqueId = iTaskDAO.placeTemplateJobInQueue(dataTemplateId,
									TemplateRunTypes.newtemplate.toString());
						} else {
							uniqueId = iTaskDAO.placeTemplateJobInQueue(dataTemplateId,
									TemplateRunTypes.templatererun.toString());
						}
						iTaskService.insertTaskListForTemplate(dataTemplateId, listDataSource.getProfilingEnabled(),
								listDataSource.getAdvancedRulesEnabled());
						response.put("message",
								"Template rerun is in progress, status will be notified to registered email !!");
						HashMap<String, String> responseMap = new HashMap<>();
						responseMap.put("uniqueId", uniqueId);
						response.put("result", responseMap);
					} else {
						//response.put("message", "Template is queued or already in progress !!");
						response.put("message",
								"Template rerun is in progress, status will be notified to registered email !!");
					}
					response.put("status", "success");
					LOG.info("Template rerun is in progress, status will be notified to registered email !!");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);

				} else {
					response.put("status", "failed");
					response.put("message", "Failed to get Template details !!");
					LOG.error("Failed to get Template details !!");
					
					return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Permission denied.");
				LOG.error("Permission denied.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to rerun data template.");
			response.put("stackTrace", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/rerunDataTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/rerunTemplateProfiling", method = RequestMethod.POST)
	public ResponseEntity<Object> rerunTemplateProfiling(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/rerunTemplateProfiling - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
		LOG.debug("Getting request parameters  " + params);
		Long dataTemplateId = params.get("dataTemplateId");
		ListDataSource listDataSource = importExportDao.getDataFromListDataSources(dataTemplateId);
		if (listDataSource != null) {
			if (!iTaskDAO.isTemplateJobQueuedOrInProgress(dataTemplateId)) {
				String uniqueId = iTaskDAO.placeTemplateJobInQueue(dataTemplateId,
						TemplateRunTypes.profilingrerun.toString());
				iTaskService.insertTaskListForTemplate(dataTemplateId, listDataSource.getProfilingEnabled(),
						listDataSource.getAdvancedRulesEnabled());
				response.put("message", "Profiling rerun is in progress");
				HashMap<String, String> responseMap = new HashMap<>();
				responseMap.put("uniqueId", uniqueId);
				response.put("result", responseMap);
			} else {
				response.put("message", "Template is queued or already in progress !!");
			}
			response.put("status", "success");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} else {
			response.put("message", "Failed to reRun Profiling !!");
			response.put("status", "Failed");
			response.put("result", dataTemplateId);
			LOG.info("dbconsole/rerunTemplateProfiling - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/dataTemplateView", method = RequestMethod.POST)
	public ResponseEntity<Object> dataTemplateView(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/dataTemplateView - START");
		String token = null;
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "failed");
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			responseMap.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("dataLocation") || !params.containsKey("name")) {
			responseMap.put("message", "idData, datalocation or name is missing in request parameters.");
			LOG.error("idData, datalocation or name is missing in request parameters.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		try {
			LOG.debug("idData=" + params.get("idData"));
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("view listDataView  id " + idData);
			String dataLocation = String.valueOf(params.get("dataLocation"));
			LOG.debug("dataLocation=" + dataLocation);
			if (dataLocation.equals("File Management")) {
				String name = String.valueOf(params.get("name"));
				LOG.debug("name=" + name);
				String description = String.valueOf(params.get("description"));
				LOG.debug("description=" + description);
				List<listDataAccess> dataFromListDataAccess = templateviewdao.getDataFromListDataAccess(idData);
				String referenceFiles = templateviewdao.getReferenceFilesFromListDataFiles(idData);
				Map<String, Object> model = new HashMap<>();
				model.put("name", name);
				model.put("description", description);
				model.put("listDataAccessData", dataFromListDataAccess);
				model.put("referenceFiles", referenceFiles);
				responseMap.put("status", "success");
				responseMap.put("result", model);
				responseMap.put("message", "Successfully fetched data.");
				LOG.info("Successfully fetched data.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			} else {
				List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
				System.out.println(listdatadefinition.size());
				Map<String, Object> model = new HashMap<>();
				try {
					model.put("idDataSchema", listdatadefinition.get(0).getIdDataSchema());
				} catch (Exception e) {
					// e.printStackTrace();
					model.put("message", "success");
					model.put("currentSection", "Data Template");
					model.put("currentLink", "DTView");
					model.put("msg", "Validation Template not Created Successfully");
					LOG.error("Validation Template not Created Successfully");
					
					return new ResponseEntity<Object>(model, HttpStatus.OK);
				}
				// Delta changes of listDatadefinition
				TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService
						.getTemplateDeltaChanges(idData);

				model.put("idData", idData);
				model.put("name", params.get("name"));
				model.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
				model.put("templateDeltaResponse", templateDeltaResponse);
				responseMap.put("status", "success");
				responseMap.put("result", model);
				responseMap.put("message", "Successfully fetched data.");
				LOG.error("Successfully fetched data.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			responseMap.put("status", "failed");
			responseMap.put("message", "Failed to get data.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/dataTemplateView - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/editCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> saveDataTemplateDataInListDataDefinition(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/editCheck - START");
		String token = null;
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "failed");
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			responseMap.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idColumn") || !params.containsKey("columnName")
				|| !params.containsKey("columnValue")) {
			responseMap.put("message", "idColumn, columnName or columnValue is missing in request parameters.");
			LOG.info("idColumn, columnName or columnValue is missing in request parameters.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		try {

			Long idColumn = Long.parseLong(String.valueOf(params.get("idColumn")));
			String columnName = String.valueOf(params.get("columnName"));
			String columnValue = String.valueOf(params.get("columnValue"));
			ArrayList<String> bl = new ArrayList<String>();
			bl.add("nullCountThreshold");
			bl.add("dataDriftThreshold");
			bl.add("recordAnomalyThreshold");
			bl.add("patternCheckThreshold");
			bl.add("badDataCheckThreshold");
			bl.add("lengthCheckThreshold");
			bl.add("numericalThreshold");
			if (bl.contains(columnName)) {
				LOG.debug("Threshold Value =>" + columnValue);
				double value = Double.parseDouble(columnValue);
				if (value < 0) {
					LOG.debug(value + "Threshold is negative");
					responseMap.put("message", "Please enter positive value for Threshold");
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else {
					LOG.debug(value + "Threshold is positive");
				}
			}
			ArrayList<String> al = new ArrayList<String>();
			al.add("nonNull");
			al.add("primaryKey");
			al.add("hashValue");
			al.add("numericalStat");
			al.add("stringStat");
			al.add("KBE");
			al.add("dgroup");
			al.add("dupkey");
			al.add("measurement");
			al.add("incrementalCol");
			al.add("recordAnomaly");
			al.add("defaultCheck");
			al.add("patternCheck");
			al.add("dataDrift");
			al.add("outOfNormStat");
			al.add("isMasked");
			al.add("partitionBy");
			al.add("badData");
			al.add("lengthCheck");
			al.add("maxLengthCheck");
			al.add("dGroupNullCheck");
			al.add("dGroupDateRuleCheck");
			al.add("defaultPatternCheck");
			al.add("timelinessKey");
			al.add("startDate");
			al.add("endDate");
			al.add("dateRule");
		//	al.add("dateFormat");
			boolean flag = true;
			if (al.contains(columnName)) {
				if (!columnValue.equalsIgnoreCase("Y") && !columnValue.equalsIgnoreCase("N")) {
					flag = false;
				}
				columnValue = columnValue.toUpperCase();
			}
			LOG.debug("idColumn=" + idColumn);
			LOG.debug("columnName=" + columnName);
			LOG.debug("columnValue=" + columnValue);
			String msg = "";
			if (flag) {
				if (columnName.equals("lengthValue") && schemaDAOI.isLengthCheckEnabled(idColumn)) {
					if ((Pattern.compile("[^0-9,]").matcher(columnValue).find())) {
						flag = false;
						msg = "Please enter correct length values";
					}
				} else if (columnName.equals("lengthValue") && schemaDAOI.isMaxLengthCheckEnabled(idColumn)) {
					if(!columnValue.matches("^\\d+$")) {
						flag = false;
						msg = "Please enter a single integer value for max length check";
					}
				}
			}
			if (flag) {
				if (columnName.equals("dateFormat")) {
					LocalDate now = LocalDate.now();
					try {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(columnValue);
						formatter.format(now);
					} catch (Exception e) {
						e.printStackTrace();
						flag = false;
						msg = " Please enter correct date format. ";
					}
				}
			}
			
			if (flag) {
				Long update = schemaDAOI.updateDataIntoStagingListDataDefinition(idColumn, columnName, columnValue);
				schemaDAOI.updateKBEIntoStagingListDataDefinition(idColumn, columnName, columnValue);
				LOG.debug("update=" + update);
				if (update == -1) {
					if(columnName.equals("timelinessKey")){
						responseMap.put("message", "More than one business key is not allowed ");
					}else if(columnName.equals("startDate")){
						responseMap.put("message", "More than one start date key is not allowed ");
					}else if(columnName.equals("endDate")){
						responseMap.put("message", "More than one end date key is not allowed ");
					}else
						responseMap.put("message", "Not Allowed For This Data Type");
					if (columnName.equals("lengthValue")) {
						LOG.debug("If ...........lengthValue =>" + columnName);
						responseMap.put("columnValue", "0");
					} else {
						LOG.debug("Else...........lengthValue =>" + columnName);
						responseMap.put("columnValue", "N");
					}
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update > 0) {
					LOG.debug("update > 0 ................ =>" + columnName);
					responseMap.put("message", "Item Updated Successfully");
					responseMap.put("status", "success");
					responseMap.put("columnValue", columnValue);
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update == -2) {
					LOG.debug("update == -2 ................ =>" + columnName);
					responseMap.put("message", "Not Allowed For This Data Type");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update == -3) {
					LOG.debug("update == -3 ................ =>" + columnName);
					responseMap.put("message", "Start date and End Date should be enabled");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -4) {
					LOG.debug("update == -4 ................ =>" + columnName);
				responseMap.put("message", "Length check and max length check cannot be enabled at the same time.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -5) {
					LOG.debug("update == -5 ................ =>" + columnName);
					responseMap.put("message", "Length check and max length check cannot be enabled at the same time.");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -6) {
						LOG.debug("update == -6 ................ =>" + columnName);
						responseMap.put("message", "Length check or MaxLengthCheck is not enabled.");

						return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else  if (update == -7) {
					LOG.debug("update == -7 ................ =>" + columnName);
					responseMap.put("message", "PatternCheck is not enabled.");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else {
					responseMap.put("message", "Item failed to Update");
					LOG.error("Item failed to Update");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			} else {
				if (msg.isEmpty())
					msg = "Please write Y for Yes and N for No";
				responseMap.put("message", msg);
				LOG.info("Please write Y for Yes and N for No");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			responseMap.put("status", "failed");
			responseMap.put("message", "Please write Y for Yes and N for No");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/editCheck - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/dbconsole/checkDateFormat", method = RequestMethod.POST)
	public ResponseEntity<Object> saveDataTemplateDateFormat(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/checkDateFormat - START");
		String token = null;
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", "failed");
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			responseMap.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			responseMap.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idColumn") || !params.containsKey("columnName")
				|| !params.containsKey("columnValue")) {
			responseMap.put("message", "idColumn, columnName or columnValue is missing in request parameters.");
			LOG.info("idColumn, columnName or columnValue is missing in request parameters.");
			
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
		try {

			Long idColumn = Long.parseLong(String.valueOf(params.get("idColumn")));
			String columnName = String.valueOf(params.get("columnName"));
			String columnValue = String.valueOf(params.get("columnValue"));
			String dateRule = String.valueOf(params.get("dateRule"));
			boolean flag = true;

			LOG.debug("idColumn=" + idColumn);
			LOG.debug("columnName=" + columnName);
			LOG.debug("columnValue=" + columnValue);
			LOG.debug("dateRule="+dateRule);
			String msg = "";
			
			if(!dateRule.equalsIgnoreCase("Y"))
				flag=false;
			
			if (flag) {
					LocalDate now = LocalDate.now();
					try {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern(columnValue);
						formatter.format(now);
					} catch (Exception e) {
						e.printStackTrace();
						flag = false;
						msg = " Please enter correct date format. ";
					}
			}
			
			if (flag) {
				Long update = schemaDAOI.updateDataIntoStagingListDataDefinition(idColumn, columnName, columnValue);
				schemaDAOI.updateKBEIntoStagingListDataDefinition(idColumn, columnName, columnValue);
				LOG.debug("update=" + update);
				if (update == -1) {
					if(columnName.equals("timelinessKey")){
						responseMap.put("message", "More than one business key is not allowed ");
					}else if(columnName.equals("startDate")){
						responseMap.put("message", "More than one start date key is not allowed ");
					}else if(columnName.equals("endDate")){
						responseMap.put("message", "More than one end date key is not allowed ");
					}else
						responseMap.put("message", "Not Allowed For This Data Type");
					if (columnName.equals("lengthValue")) {
						LOG.debug("If ...........lengthValue =>" + columnName);
						responseMap.put("columnValue", "0");
					} else {
						LOG.debug("Else...........lengthValue =>" + columnName);
						responseMap.put("columnValue", "N");
					}
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update > 0) {
					LOG.debug("update > 0 ................ =>" + columnName);
					responseMap.put("message", "Item Updated Successfully");
					responseMap.put("status", "success");
					responseMap.put("columnValue", columnValue);
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update == -2) {
					LOG.debug("update == -2 ................ =>" + columnName);
					responseMap.put("message", "Not Allowed For This Data Type");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				} else if (update == -3) {
					LOG.debug("update == -3 ................ =>" + columnName);
					responseMap.put("message", "Start date and End Date should be enabled");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -4) {
					LOG.debug("update == -4 ................ =>" + columnName);
				responseMap.put("message", "Length check and max length check cannot be enabled at the same time.");
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -5) {
					LOG.debug("update == -5 ................ =>" + columnName);
					responseMap.put("message", "Length check and max length check cannot be enabled at the same time.");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else if (update == -6) {
						LOG.debug("update == -6 ................ =>" + columnName);
						responseMap.put("message", "Length check or MaxLengthCheck is not enabled.");

						return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else  if (update == -7) {
					LOG.debug("update == -7 ................ =>" + columnName);
					responseMap.put("message", "PatternCheck is not enabled.");

					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}else {
					responseMap.put("message", "Item failed to Update");
					LOG.error("Item failed to Update");
					
					return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
				}
			} else {
				if (msg.isEmpty())
					msg = "Please enable DateConsistency.";
				responseMap.put("message", msg);
				LOG.info(msg);
				
				return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			responseMap.put("status", "failed");
			responseMap.put("message", "Please write Y for Yes and N for No");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/checkDateFormat - END");
			return new ResponseEntity<Object>(responseMap, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/approveColumnAnalysisChanges", method = RequestMethod.POST)
	public ResponseEntity<Object> approveColumnAnalysisChanges(@RequestHeader HttpHeaders headers,
			@RequestBody List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		LOG.info("dbconsole/approveColumnAnalysisChanges - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			response.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			LOG.debug("Getting request parameters  " + listDataDefinitionDelta);
			for (ListDataDefinitionDelta lddDelta : listDataDefinitionDelta) {
				if (lddDelta.getDeltaType().equals(DeltaType.MISSING)) {
					LOG.debug("missingcolId: " + lddDelta.getMissingColId());
				}
			}
			boolean status = dataTemplateDeltaCheckService.approveColumnAnalysisChanges(listDataDefinitionDelta);
			if (status) {
				response.put("status", "success");
				response.put("message", "Successfully approved column analysis.");
				LOG.info("Successfully approved column analysis.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("message", "Failed to update details.");
				LOG.error("Failed to update details.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/approveColumnAnalysisChanges - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/rejectColumnAnalysisChanges")
	public ResponseEntity<Object> rejectColumnAnalysisChanges(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/rejectColumnAnalysisChanges - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			response.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData")) {
			response.put("message", "idData is missing in request parameters.");
			LOG.error("idData is missing in request parameters.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		try {
			LOG.debug("Getting request parameters  " + params);
			listdatasourcedao.clearListDataDefinitonStagingForIdData(params.get("idData"));
			response.put("status", "success");
			response.put("message", "Successfully rejected column analysis.");
			LOG.info("Successfully rejected column analysis.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "success");
			response.put("message", "Failed to update details.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/rejectColumnAnalysisChanges - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateTemplateDeltaApprovalStatus", method = RequestMethod.POST)
	public ResponseEntity<Object> updateTemplateDeltaApprovalStatus(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/updateTemplateDeltaApprovalStatus - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			response.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData")) {
			response.put("message", "idData is missing in request parameters.");
			LOG.error("idData is missing in request parameters.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		try {
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			String approvalStatus = String.valueOf(params.get("approvalStatus"));
			LOG.debug("idData: " + idData);
			LOG.debug("approvalStatus: " + approvalStatus);
			boolean updateStatus = listdatasourcedao.updateTemplateDeltaApprovalStatus(idData, approvalStatus);
			LOG.debug("====> updateStatus: " + updateStatus);
			if (updateStatus) {
				response.put("status", "success");
				response.put("message", "Status updated successfully.");
				LOG.info("Status updated successfully.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("message", "Failed to update status.");
				LOG.error("Failed to update status.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update status.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/updateTemplateDeltaApprovalStatus - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/approveMicroSegments", method = RequestMethod.POST)
	public ResponseEntity<Object> approveMicroSegments(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/approveMicroSegments - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			response.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData")) {
			response.put("message", "idData is missing in request parameters.");
			LOG.error("idData is missing in request parameters.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

		try {
			LOG.debug("Getting request parameters  " + params);
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
			String createdByUser = userToken.getUserName();
			ApproveMicroSegmentsDTO approveMicroSegmentsDTO = dataTemplateDeltaCheckService.approveMicrosegments(idData,
					createdByUser);
			response.put("result", approveMicroSegmentsDTO);
			response.put("status", "success");
			response.put("message", "Microsegment updated successfully.");
			LOG.info("Microsegment updated successfully.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to update details.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/approveMicroSegments - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/rejectMicroSegments", method = RequestMethod.POST)
	public ResponseEntity<Object> rejectMicroSegments(@RequestHeader HttpHeaders headers,
			@RequestBody List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		LOG.info("dbconsole/rejectMicroSegments - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
			response.put("message", "Token is expired");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			LOG.debug("Getting request parameters  " + listDataDefinitionDelta);
			boolean status = dataTemplateDeltaCheckService.rejectMicrosegments(listDataDefinitionDelta);
			if (status) {
				response.put("status", "success");
				response.put("message", "Successfully updated details.");
				LOG.info("Successfully updated details.");
			} else {
				response.put("message", "Failed to update details");
				LOG.error("Failed to update details");
			}
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to update details");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/rejectMicroSegments - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> updateTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/updateTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
			LOG.debug("Getting request parameters  " + requestBody);
			//changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

			String dataLocation = requestBody.get("dataLocation");
			String idData = requestBody.get("idData");
			String templateName = requestBody.get("dataset");
//			List<Integer> templateIds = listdatasourcedao.checkTemplateStatus(Integer.parseInt(idData));
//			if(templateIds.isEmpty()) {
//				response.put("status", "failed");
//				response.put("message", "Unable to update Validation template as it is in process.");
//				return new ResponseEntity<Object>(response, HttpStatus.OK);
//			}

			String idDataSchema = requestBody.get("idDataSchema");

			StringBuffer sb_lds = new StringBuffer("UPDATE listDataSources SET name= '" + requestBody.get("dataset")
					+ "',description='" + requestBody.get("description") + "',dataLocation='" + dataLocation
					+ "',updatedAt=now(), ");

			StringBuffer sb_lda = new StringBuffer("UPDATE listDataAccess SET  ");

			if (dataLocation.equalsIgnoreCase("Oracle RAC") || dataLocation.equalsIgnoreCase("Oracle")
					|| dataLocation.equalsIgnoreCase("ClouderaHive") || dataLocation.equalsIgnoreCase("Hive")
					|| dataLocation.equalsIgnoreCase("Hive Kerberos") || dataLocation.equalsIgnoreCase("MSSQL")
					|| dataLocation.equalsIgnoreCase("MSSQLActiveDirectory") || dataLocation.equalsIgnoreCase("Vertica")
					|| dataLocation.equalsIgnoreCase("Teradata") || dataLocation.equalsIgnoreCase("Postgres")
					|| dataLocation.equalsIgnoreCase("Amazon Redshift")
					|| dataLocation.equalsIgnoreCase("FileSystem Batch") || dataLocation.equalsIgnoreCase("SnowFlake")
					|| dataLocation.equalsIgnoreCase("S3 Batch") || dataLocation.equalsIgnoreCase("BigQuery")
					|| dataLocation.equalsIgnoreCase("AzureSynapseMSSQL")
					|| dataLocation.equalsIgnoreCase("S3 IAMRole Batch")
					|| dataLocation.equalsIgnoreCase("AzureDataLakeStorageGen1")
					|| dataLocation.equalsIgnoreCase("MYSQL") || dataLocation.equalsIgnoreCase("MapR Hive")
					|| dataLocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")
					|| dataLocation.equalsIgnoreCase("DatabricksDeltaLake")) {

				// handle query

				String querycheckboxid = requestBody.get("querycheckbox");

				if (querycheckboxid == null) {
					querycheckboxid = "N";
				}

				String historicDateTable = requestBody.get("historicDateTable");

				if (querycheckboxid != null && querycheckboxid.equalsIgnoreCase("Y")) {
					historicDateTable = (historicDateTable != null) ? historicDateTable.trim() : "";
				} else {
					historicDateTable = "";
				}

				if (querycheckboxid != null && querycheckboxid.equalsIgnoreCase("Y")) {
					String querytextboxid = requestBody.get("querytextboxid");
					sb_lda.append("query='Y',");
					sb_lda.append("queryString=\"" + querytextboxid + "\",");
					sb_lda.append("wherecondition='',");
					sb_lda.append("folderName=\'" + requestBody.get("tableNameid") + "\',");
					sb_lda.append("historicDateTable='" + historicDateTable + "',");
				} else {
					sb_lda.append("query='N',");
					// handle where condtition
					String whereId = requestBody.get("whereId");
					sb_lda.append("folderName=\'" + requestBody.get("tableNameid") + "\',");
					if (whereId != null && whereId.length() != 0) {
						sb_lda.append("wherecondition=\"" + whereId + "\",");
					} else {
						sb_lda.append("wherecondition='',");
					}
				}
				String querytextboxid = requestBody.get("querytextboxid");
				// handle incremental matching source
				String incrementalSourceId = requestBody.get("incrementalsourceid");
				if (incrementalSourceId != null && incrementalSourceId.equalsIgnoreCase("column_name")) {
					sb_lda.append(" incrementalType='" + requestBody.get("incrementalsourceid") + "',");
					sb_lda.append(" dateFormat='" + requestBody.get("dateformatid") + "',");
					sb_lda.append(" sliceStart='" + requestBody.get("slicestartid") + "',");
					sb_lda.append(" sliceEnd='" + requestBody.get("sliceendid") + "',");
				} else {
					sb_lda.append(" incrementalType='N' ,");
					sb_lda.append(" dateFormat='',");
					sb_lda.append(" sliceStart='',");
					sb_lda.append(" sliceEnd='',");
				}

			} else if (dataLocation.equalsIgnoreCase("FILESYSTEM") || dataLocation.equalsIgnoreCase("HDFS")
					|| dataLocation.equalsIgnoreCase("MapR FS") || dataLocation.equalsIgnoreCase("MapR DB")
					|| dataLocation.equalsIgnoreCase("S3")) {

				String fileQueryCheckBoxId = requestBody.get("filequerycheckbox");
				if (fileQueryCheckBoxId == null) {
					fileQueryCheckBoxId = "N";
				}

				if (dataLocation.equalsIgnoreCase("FILESYSTEM") && fileQueryCheckBoxId != null
						&& fileQueryCheckBoxId.equalsIgnoreCase("Y")) {
					String fileQueryTextbox = requestBody.get("filequerytextboxid");
					sb_lda.append("query='Y',");
					sb_lda.append("queryString=\"" + fileQueryTextbox + "\",");
					sb_lda.append("wherecondition='',");
					sb_lda.append("folderName=\"" + requestBody.get("tableNameid") + "\",");
				}
				sb_lds.append(" ignoreRowsCount=" + requestBody.get("rowsId"));
				sb_lda.append(" hostName='" + requestBody.get("hostName") + "', ");
				sb_lda.append(" folderName='" + requestBody.get("schemaName") + "', ");
				sb_lda.append(" userName='" + requestBody.get("userName") + "' ,");
				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String encryptedText = encryptor.encrypt(requestBody.get("pwd"));
				sb_lda.append(" pwd='" + encryptedText + "',");

			} else if (dataLocation.equalsIgnoreCase("File Management")) {
				sb_lda.append(" hostName='" + requestBody.get("hostName") + "', ");
				sb_lda.append(" folderName='" + requestBody.get("schemaName") + "', ");
				sb_lda.append(" userName='" + requestBody.get("userName") + "' ,");

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
				String encryptedText = encryptor.encrypt(requestBody.get("pwd"));
				sb_lda.append(" pwd='" + encryptedText + "' ,");

			}

			String sb_lds_str = sb_lds.toString().trim();
			sb_lds_str = sb_lds_str.substring(0, sb_lds_str.lastIndexOf(","));

			String sb_lda_str = sb_lda.toString().trim();
			sb_lda_str = sb_lda_str.substring(0, sb_lda_str.lastIndexOf(","));

			sb_lda_str = sb_lda_str.concat(" WHERE idData=" + idData + "");
			sb_lds_str = sb_lds_str.concat(" WHERE idData=" + idData + "");

			LOG.debug("sb_lds_str: " + sb_lds_str);
			LOG.debug("sb_lda_str: " + sb_lda_str);
			boolean updateStatus = listdatasourcedao.updateDataTemplate(sb_lds_str, sb_lda_str);

			if (updateStatus) {
				//Audit Trail Changes
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(),userToken.getUserName(),DatabuckConstants.DBK_FEATURE_TEMPLATE, formatter.format(new Date()), Long.parseLong(idData),DatabuckConstants.ACTIVITY_TYPE_EDITED,templateName);
				response.put("status", "success");
				response.put("message", "Data Template Customized Successfully.");
				LOG.info("Data Template Customized Successfully.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Unable to edit Data Template, Please check your configuration and try again.");
				LOG.error("Unable to edit Data Template, Please check your configuration and try again.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to copy data template.");
			response.put("stackTrace", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getDataTemplateByID", method = RequestMethod.POST)
	public ResponseEntity<Object> getDataTemplateByID(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/getDataTemplateByID - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			if (taskService.checkUserPermission(userToken, "C", "Data Template")) {
				Map<String, Object> responseMap = new HashMap<String, Object>();
				ListDataSource listDataSource = listdatasourcedao
						.getDataFromListDataSourcesOfIdData(params.get("dataTemplateId"));
				responseMap.put("dataSource", listDataSource);
				listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(params.get("dataTemplateId"));
				responseMap.put("dataAccess", listDataAccess);
				if (!(listDataSource.getDataLocation().equalsIgnoreCase("FILESYSTEM")
						|| listDataSource.getDataLocation().equalsIgnoreCase("HDFS")
						|| listDataSource.getDataLocation().equalsIgnoreCase("File Management")
						|| listDataSource.getDataLocation().equalsIgnoreCase("MapR FS")
						|| listDataSource.getDataLocation().equalsIgnoreCase("MapR DB")
						|| listDataSource.getDataLocation().equalsIgnoreCase("S3")
						|| listDataSource.getDataLocation().equalsIgnoreCase("AzureDataLakeStorageGen2"))) {
					responseMap.put("dataSchema", listdatasourcedao
							.getListDataSchemaForIdDataSchema(listDataAccess.getIdDataSchema()).get(0));
				}
				responseMap.put("domain", globalruledao.getDomainList());

				response.put("status", "success");
				response.put("message", "Successfully fetched data template.");
				response.put("result", responseMap);
				LOG.info("Successfully fetched data template.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Permission denied.");
				LOG.error("Permission denied.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to fetched data template.");
			response.put("stackTrace", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/getDataTemplateByID - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateDerivedDataTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> updateDerivedDataTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> requestBody) {
		LOG.info("dbconsole/updateDerivedDataTemplate - START");
		Map<String, Object> response = new HashMap<>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
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
			String idDerivedData = requestBody.get("idDerivedData");
			LOG.debug("idDerivedData:" + idDerivedData);
			
//			List<Integer> templateIds = listdatasourcedao.checkTemplateStatus(Integer.parseInt(idDerivedData));
//			if(templateIds.isEmpty()) {
//				response.put("status", "failed");
//				response.put("message", "Unable to update template as it is in process.");
//				return new ResponseEntity<Object>(response, HttpStatus.OK);
//			}
			String queryText = requestBody.get("querytext");
			String derivedDescription = requestBody.get("description");

			String lddsUpdate = "UPDATE listDerivedDataSources SET description='" + derivedDescription + "',queryText='"
					+ queryText.replace("'","''") + "',updatedAt=now()";

			lddsUpdate = lddsUpdate.concat(" WHERE idDerivedData=" + idDerivedData);

			long idData = listdatasourcedao.getIdDataByDerivedId(Long.valueOf(idDerivedData));
			String ldsUpdate = "UPDATE listDataSources SET description='" + derivedDescription + "',updatedAt=now() WHERE idData = " + idData;

			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			Long idUser = userToken.getIdUser();

			boolean updateStatus = listdatasourcedao.updateDerivedDataTemplate(lddsUpdate);
			boolean ldsUpdateStatus = listdatasourcedao.updateDerivedDataTemplate(ldsUpdate);
			ListDataSource dataFromListDataSourcesOfIdData = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			//changes regarding Audit trail
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			iTaskDAO.addAuditTrailDetail(idUser, userDAO.getUserNameByUserId(Long.valueOf(idUser)), DatabuckConstants.DBK_FEATURE_DERIVED_TEMPLATE, formatter.format(new Date()), Long.valueOf(idData), DatabuckConstants.ACTIVITY_TYPE_EDITED, dataFromListDataSourcesOfIdData.getName());

			if (updateStatus && ldsUpdateStatus) {
				response.put("message", "Data Template Customized Successfully");
				response.put("status", "success");
				LOG.info("Data Template Customized Successfully");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("message", "Unable to edit Data Template, Please check your configuration and try again.");
				response.put("status", "success");
				LOG.error("Unable to edit Data Template, Please check your configuration and try again.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to update data template.");
			response.put("stackTrace", e.getMessage());
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateDerivedDataTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/dbconsole/editDerivedDataTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> editDerivedDataTemplate(@RequestHeader HttpHeaders headers,@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/editDerivedDataTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			LOG.debug("token "+headers.get("token").get(0).toString());
			// Check whether all required parameters are available.
			if (!params.containsKey("idData")) {
				throw new Exception("Required parameters not found.");
			}
			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				LOG.debug("Getting request parameters  " + params);
				Long idData = params.get("idData");
				ListDerivedDataSource listDerivedDataSource = listdatasourcedao.getDataFromListDerivedDataSourcesOfIdData(idData);
				response.put("listDerivedDataSource", listDerivedDataSource);
				response.put("currentSection", "Data Template");
				response.put("currentLink", "DerivedDTAdd New");
				response.put("message", "Derived template fetched successfully.");
				response.put("status", "success");
				LOG.info("Derived template fetched successfully.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/editDerivedDataTemplate - END");
		return new ResponseEntity<Object>(response, status);
	}

	@RequestMapping(value = "/dbconsole/getProfileDataForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getProfileDataForTemplate(@RequestHeader HttpHeaders headers, @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getProfileDataForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		long idDataSchema = 0l;

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
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.getLong("idData");
						JSONObject sourceResults = dataTemplateDeltaCheckService.getProfileDataForTemplate(idData);
						message = sourceResults.getString("message");
						status = sourceResults.getString("status");
						result = sourceResults.getJSONObject("result");
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
				message = "Token is missing in header";
				LOG.error("Token is missing in header");
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getProfileDataForTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/validateQueryForTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> validateQueryForTemplate(@RequestHeader HttpHeaders headers, @RequestBody ValidateQuery validateQuery) {
		LOG.info("dbconsole/validateQueryForTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		long idDataSchema = 0l;

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
					if (validateQuery != null) {
						if(validateQuery.getIsQueryEnabled() == null || validateQuery.getIdDataSchema()<=0){
							LOG.debug("Getting request parameters  " + validateQuery);
							message = "Invalid request, please provide proper details.";
						} else {
							JSONObject sourceResults = dataTemplateDeltaCheckService.validateQueryForTemplate(validateQuery);
							message = sourceResults.getString("message");
							status = sourceResults.getString("status");
							result = sourceResults.getJSONObject("result");
						}
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
				message = "Token is missing in header";
				LOG.error("Token is missing in header");
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/validateQueryForTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/validateQueryForDerivedTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> validateQueryForDerivedTemplate(@RequestHeader HttpHeaders headers, @RequestBody DerivedTemplateValidateQuery validateQuery) {
		LOG.info("dbconsole/validateQueryForDerivedTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		long idDataSchema = 0l;

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
					if (validateQuery != null) {
						if(validateQuery.getQueryString() == null || validateQuery.getQueryString().isEmpty() || validateQuery.getTemplate1IdData()<=0){
							LOG.debug("Getting request parameters  " + validateQuery);
							message = "Invalid request, please provide proper details.";
						} else if(validateQuery.getIsJoinCondition() != null && !validateQuery.getIsJoinCondition().isEmpty() && validateQuery.getIsJoinCondition().equalsIgnoreCase("Y")){
							LOG.debug("Validate Query is not supported for join condition");
							message = "Validate Query is not supported for join condition";
						} else {
							JSONObject sourceResults = dataTemplateDeltaCheckService.validateQueryForDerivedTemplate(validateQuery);
							message = sourceResults.getString("message");
							status = sourceResults.getString("status");
							result = sourceResults.getJSONObject("result");
						}
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
				message = "Token is missing in header";
				LOG.error("Token is missing in header");
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/validateQueryForDerivedTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	public static void main(String[] args) {
		if (!(Pattern.compile("yyyy-mm-dd").matcher("yy").find()) && !(Pattern.compile("dd-mm-yyyy").matcher("yy").find())
				&& !(Pattern.compile("yyyy-MM-dd").matcher("yy").find()))  {
			LOG.error(" Please enter correct date format like yyyy-mm-dd or dd-mm-yyyy. ");
		}else {
			LOG.info("success");
		}
	}
}
