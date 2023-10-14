package com.databuck.restcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.databuck.config.DatabuckEnv;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import java.text.SimpleDateFormat;
import com.databuck.datatemplate.AzureDataLakeGen2Connection;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.integration.AlationIntegrationService;
import com.databuck.security.LogonManager;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.DataConnectionService;
import com.databuck.service.RBACController;
import com.databuck.service.RemoteClusterAPIService;
import com.databuck.util.ConnectionUtil;
import com.databuck.util.DatabuckUtility;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.google.cloud.bigquery.BigQuery;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.AzureADToken;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

@CrossOrigin(origins = "*")
@RestController
public class DataConnectionRestController {

	@Autowired
	private ChecksCSVService csvService;
	@Autowired
	private DataConnectionService dataConnectionService;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;
	@Autowired
	public SchemaDAOI schemaDAOI;
	@Autowired
	private RBACController rbacController;
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;
	@Autowired
	IProjectDAO IProjectdAO;

	@Autowired
	private ITaskDAO iTaskDAO;
	@Autowired
	private Properties appDbConnectionProperties;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	BigQueryConnection bigQueryConnection;
	@Autowired
	private FileMonitorDao fileMonitorDao;
	@Autowired
	AzureDataLakeGen2Connection azureDataLakeGen2Connection;

	@Autowired
	private LogonManager logonManager;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private AlationIntegrationService alationIntegrationService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	private ConnectionUtil connectionUtil;

	private static final Logger LOG = Logger.getLogger(DataConnectionRestController.class);

	@PostMapping("/dbconsole/dataConnections")
	public ResponseEntity<Object> getDataConnections(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/dataConnections - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String isHealthCheckEnabled = "N";
					if (appDbConnectionProperties.containsKey("connection.healthcheck.enabled")) {
						isHealthCheckEnabled = appDbConnectionProperties.getProperty("connection.healthcheck.enabled");
					}
					LOG.debug("Getting request parameters  " + params);
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("isHealthCheckEnabled", isHealthCheckEnabled);
					// DC - 2348
					List<ListDataSchema> connections = dataConnectionService.getConnectionsbyDomain(params);
					if (connections != null) {
						result.put("connections", connections);
						response.put("result", result);
						response.put("status", "success");
						response.put("message", "Successfully fetched records.");
						LOG.info("Successfully fetched records.");
						LOG.info("dbconsole/dataConnections - END");
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						result.put("connections", new ArrayList<>());
						response.put("result", result);
						response.put("status", "success");
						response.put("message", "Successfully fetched records.");
						LOG.info("Successfully fetched records.");
						LOG.info("dbconsole/dataConnections - END");
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/dataConnections - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/dataConnections - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/dataConnections - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/dataConnectionCSV")
	public void getDataConnectionCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/dataConnectionCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + params);
					List<ListDataSchema> connections = dataConnectionService.getConnections(params);
					if (connections != null) {
						response.put("result", connections);
						response.put("status", "success");
						response.put("message", "Successfully fetched records.");
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "IdDataSchema", "SchemaName", "SchemaType", "IpAddress", "DatabaseSchema",
								"Username", "Port", "DomainName", "ProjectName", "CreatedByUser" };
						String[] header = { "Connection Id", "Connection Name", "Connection Type", "Host", "Schema",
								"User Name", "Port", "Domain Name", "Project Name", "Created By" };
						csvWriter.writeHeader(header);
						for (ListDataSchema connection : connections) {
							csvWriter.write(connection, fields);
						}
						csvWriter.close();
					} else {
						LOG.error("Records not found.");
						throw new Exception("Records not found.");
					}
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

	@RequestMapping(value = "/dbconsole/isAlationIntegrationEnabled", method = RequestMethod.POST)
	public ResponseEntity<Object> isAlationIntegrationEnabled(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/isAlationIntegrationEnabled - START");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + params);
					String schemaType = params.get("schemaType");
					String schemaName = params.get("schemaName");
					JSONObject alation_integration_json = alationIntegrationService
							.isAlationEnabledForDatabaseSchema(schemaType, schemaName);

					String alation_status = alation_integration_json.getString("status");
					String alation_message = alation_integration_json.getString("message");

					if (alation_status != null && alation_status.trim().equalsIgnoreCase("success")) {
						status = "success";
						message = "Alation Integration is enabled for schema[" + schemaName + "]";
						LOG.info(message);
					} else
						message = alation_message;
				} else {
					message = "Token expired";
					json.put("status", status);
					json.put("message", message);
					LOG.error("Token is expired.");
					LOG.info("dbconsole/isAlationIntegrationEnabled - END");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				message = "Token is missing in headers.";
				json.put("status", status);
				json.put("message", message);
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/isAlationIntegrationEnabled - END");
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "Failed to determine Alation Integration !!";
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/isAlationIntegrationEnabled - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/publishSchemaSummaryToAlation", method = RequestMethod.POST)
	public ResponseEntity<Object> publishSchemaSummaryToAlation(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/publishSchemaSummaryToAlation - START");
		JSONObject json = new JSONObject();
		String message = "";
		String status = "failed";
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + params);
					long idDataSchema = params.get("idDataSchema");
					List<ListDataSchema> dataConnectionList = listdatasourcedao
							.getListDataSchemaForIdDataSchema(idDataSchema);
					if (dataConnectionList != null && dataConnectionList.size() > 0) {
						JSONObject alationResultObj = alationIntegrationService
								.publishSchemaSummaryToAlation(idDataSchema);
						if (alationResultObj != null) {
							String alationMessage = alationResultObj.getString("message");
							if (alationResultObj.getString("status").trim().equalsIgnoreCase("success")) {
								status = "success";
								message = "Schema summary is published to Alation successfully";
								LOG.info(message);
							} else
								message = alationMessage;
						} else {
							message = "Publishing Schema summary to Alation is failed";
							LOG.error(message);
						}
					}
				} else {
					message = "Token expired";
					json.put("status", status);
					json.put("message", message);
					LOG.error("Token is expired.");
					LOG.info("dbconsole/publishSchemaSummaryToAlation - END");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				message = "Token is missing in headers.";
				json.put("status", status);
				json.put("message", message);
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/publishSchemaSummaryToAlation - END");
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			message = "Publishing Schema summary to Alation is failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/publishSchemaSummaryToAlation - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/createConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> getSchemaData(@RequestHeader HttpHeaders headers, @RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/createConnection - START");
		System.out.println("=========REST createConnection Start==========");
		JSONObject params = new JSONObject(inputsJsonStr);

		String schemaName = params.getString("schemaName");
		String schemaType = params.getString("schemaType");
		String uri = params.getString("uri");
		String database = params.getString("database");
		String username = params.getString("username");
		String password = params.getString("password");
		String port = params.getString("port");
		String domain = params.getString("domain"); // princ
		String serviceName = params.getString("serviceName"); // gss_jaas
		String krb5conf = params.getString("krb5conf");
		String autoGenerateId = params.getString("autoGenerateId");
		String hivejdbchost = params.getString("hivejdbchost");
		String hivejdbcport = params.getString("hivejdbcport");
		String suffix = params.getString("suffix");
		String prefix = params.getString("prefix");
		String sslEnb = params.getString("sslEnb");
		String sslTrustStorePath = params.getString("sslTrustStorePath");
		String trustPassword = params.getString("trustPassword");
		String gatewayPath = params.getString("gatewayPath");
		String jksPath = params.getString("jksPath");
		String zookeeperUrl = params.getString("zookeeperUrl");
		String folderPath = params.getString("folderPath");
		String fileNamePattern = params.getString("fileNamePattern");
		String fileDataFormat = params.getString("fileDataFormat");
		String headerPresent = params.getString("headerPresent");
		String partitionedFolders = params.getString("partitionedFolders");
		String headerFilePath = params.getString("headerFilePath");
		String headerFileNamePattern = params.getString("headerFileNamePattern");
		String headerFileDataFormat = params.getString("headerFileDataFormat");
		String accessKey = params.getString("accessKey");
		String secretKey = params.getString("secretKey");
		String bucketName = params.getString("bucketName");
		String bigQueryProjectName = params.getString("bigQueryProjectName");
		String privatekeyId = params.getString("privatekeyId");
		String privatekey = params.getString("privatekey");
		String clientId = params.getString("clientId");
		String clientEmail = params.getString("clientEmail");
		String datasetName = params.getString("datasetName");
		String azureClientId = params.getString("azureClientId");
		String azureClientSecret = params.getString("azureClientSecret");
		String azureTenantId = params.getString("azureTenantId");
		String azureServiceURI = params.getString("azureServiceURI");
		String azureFilePath = params.getString("azureFilePath");
		String enableFileMonitoring = params.getString("enableFileMonitoring");
		String multiPattern = params.getString("multiPattern");
		int startingUniqueCharCount = params.getInt("startingUniqueCharCount");
		int endingUniqueCharCount = params.getInt("endingUniqueCharCount");
		int maxFolderDepth = params.getInt("maxFolderDepth");
		String fileEncrypted = params.getString("fileEncrypted");
		String singleFile = params.getString("singleFile");
		String externalFileNamePatternId = params.getString("externalFileNamePatternId");
		String externalFileName = params.getString("externalFileName");
		String patternColumn = params.getString("patternColumn");
		String headerColumn = params.getString("headerColumn");
		String localDirectoryColumnIndex = params.getString("localDirectoryColumnIndex");
		String xsltFolderPath = params.getString("xsltFolderPath");
		String kmsAuthDisabled = params.getString("kmsAuthDisabled");
		String readLatestPartition = params.getString("readLatestPartition");
		String alationIntegrationEnabled = params.getString("alationIntegrationEnabled");
		String incrementalDataReadEnabled = params.getString("incrementalDataReadEnabled");
		String pushDownQueryEnabled = params.getString("pushDownQueryEnabled");
		// String masterKey = params.getString("masterKey");
		String createdBy = params.getString("createdBy");
		long idUser = params.getLong("idUser");
		Integer domainId = params.getInt("domainId");
		long projectId = params.getLong("projectId");
		String clusterPropertyCategory = params.getString("clusterType");
		String httpPath = params.getString("httpPath");
		String azureAuthenticationType = params.getString("azureAuthenticationType");
		String clusterPolicyId = "";
		if (params.has("clusterPolicyId")) {
			clusterPolicyId = params.getString("clusterPolicyId");
		}
		if (clusterPropertyCategory == null || clusterPropertyCategory.trim().isEmpty())
			clusterPropertyCategory = "cluster";
		System.out.println("\n====> saveSchema - START ");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (incrementalDataReadEnabled != null && incrementalDataReadEnabled.trim().equalsIgnoreCase("Y")) {
						incrementalDataReadEnabled = "Y";
					} else {
						incrementalDataReadEnabled = "N";
					}
					if (pushDownQueryEnabled != null && pushDownQueryEnabled.trim().equalsIgnoreCase("Y")) {
						pushDownQueryEnabled = "Y";
					} else {
						pushDownQueryEnabled = "N";
					}
					String name = schemaDAOI.duplicateSchemaName(schemaName, projectId, domainId);
					if (name != null && !name.isEmpty()) {
						throw new Exception("The Connection Name is in use. Please choose another name.");
					}
					LOG.debug("=======================>incrementalDataReadEnabled=" + incrementalDataReadEnabled);
					long idDataSchema = dataConnectionService.saveConnectionInfo(projectId, domainId, idUser, createdBy,
							schemaName, schemaType, uri, database, username, password, port, domain, serviceName,
							krb5conf, autoGenerateId, hivejdbchost, hivejdbcport, suffix, prefix, sslEnb,
							sslTrustStorePath, trustPassword, gatewayPath, jksPath, zookeeperUrl, folderPath,
							fileNamePattern, fileDataFormat, headerPresent, partitionedFolders, headerFilePath,
							headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,
							bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
							azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath,
							enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
							maxFolderDepth, fileEncrypted, singleFile, externalFileNamePatternId, externalFileName,
							patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath, kmsAuthDisabled,
							readLatestPartition, alationIntegrationEnabled, incrementalDataReadEnabled,
							clusterPropertyCategory, "N", pushDownQueryEnabled, httpPath, clusterPolicyId,
							azureAuthenticationType);
					if (idDataSchema > 0l) {
						// changes regarding Audit trail
						iTaskDAO.addAuditTrailDetail(idUser, userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_CONNECTION, formatter.format(new Date()), idDataSchema,
								DatabuckConstants.ACTIVITY_TYPE_CREATED, schemaName);
						status = "success";
						message = "Data Connection Created Successfully";
						json.put("idDataSchema", idDataSchema);
						LOG.info("\n====> Data Connection Created Successfully");
					} else {
						message = "Entered wrong Credentials";
						LOG.error("\n====> Data Connection failed");
					}
				} else {
					json.put("status", "failed");
					json.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/createConnection - END");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				json.put("status", "failed");
				json.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/createConnection - END");
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			LOG.error(e.getMessage());
		}
		try {
			json.put("status", status);
			json.put("message", message);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		LOG.info("dbconsole/createConnection - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/healthCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> runHealthCheck(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/healthCheck - START");
		JSONObject params = new JSONObject(inputsJsonStr);
		String schemaName = params.getString("schemaName");
		String schemaType = params.getString("schemaType");
		String uri = params.getString("uri");
		String database = params.getString("database");
		String username = params.getString("username");
		String password = params.getString("password");
		String port = "" + params.get("port");
		String domain = params.getString("domain");
		String serviceName = params.getString("serviceName");
		String krb5conf = params.getString("krb5conf");
		String autoGenerateId = params.getString("autoGenerateId");
		String hivejdbchost = params.getString("hivejdbchost");
		String hivejdbcport = params.getString("hivejdbcport");
		String suffix = params.getString("suffix");
		String prefix = params.getString("prefix");
		String sslEnb = params.getString("sslEnb");
		String sslTrustStorePath = params.getString("sslTrustStorePath");
		String trustPassword = params.getString("trustPassword");
		String gatewayPath = params.getString("gatewayPath");
		String jksPath = params.getString("jksPath");
		String zookeeperUrl = params.getString("zookeeperUrl");
		String folderPath = params.getString("folderPath");
		String fileNamePattern = params.getString("fileNamePattern");
		String fileDataFormat = params.getString("fileDataFormat");
		String headerPresent = params.getString("headerPresent");
		String partitionedFolders = params.getString("partitionedFolders");
		String headerFilePath = params.getString("headerFilePath");
		String headerFileNamePattern = params.getString("headerFileNamePattern");
		String headerFileDataFormat = params.getString("headerFileDataFormat");
		String accessKey = params.getString("accessKey");
		String secretKey = params.getString("secretKey");
		String bucketName = params.getString("bucketName");
		String bigQueryProjectName = params.getString("bigQueryProjectName");
		String privatekeyId = params.getString("privatekeyId");
		String privatekey = params.getString("privatekey");
		String clientId = params.getString("clientId");
		String clientEmail = params.getString("clientEmail");
		String datasetName = params.getString("datasetName");
		String azureClientId = params.getString("azureClientId");
		String azureClientSecret = params.getString("azureClientSecret");
		String azureTenantId = params.getString("azureTenantId");
		String azureServiceURI = params.getString("azureServiceURI");
		String azureFilePath = params.getString("azureFilePath");
		String enableFileMonitoring = params.getString("enableFileMonitoring");
		String multiPattern = params.getString("multiPattern");
		int startingUniqueCharCount = params.getInt("startingUniqueCharCount");
		int endingUniqueCharCount = params.getInt("endingUniqueCharCount");
		int maxFolderDepth = params.getInt("maxFolderDepth");
		String fileEncrypted = params.getString("fileEncrypted");
		String singleFile = params.getString("singleFile");
		String externalFileNamePatternId = params.getString("externalFileNamePatternId");
		String externalFileName = params.getString("externalFileName");
		String patternColumn = params.getString("patternColumn");
		String headerColumn = params.getString("headerColumn");
		String localDirectoryColumnIndex = params.getString("localDirectoryColumnIndex");
		String xsltFolderPath = params.getString("xsltFolderPath");
		String kmsAuthDisabled = params.getString("kmsAuthDisabled");
		String readLatestPartition = params.getString("readLatestPartition");
		String alationIntegrationEnabled = params.getString("alationIntegrationEnabled");
		String incrementalDataReadEnabled = params.getString("incrementalDataReadEnabled");
		String pushDownQueryEnabled = params.getString("pushDownQueryEnabled");
		String createdBy = params.getString("createdBy");
		long idUser = params.getLong("idUser");
		Integer domainId = params.getInt("domainId");
		long projectId = params.getLong("projectId");
		String clusterPropertyCategory = params.getString("clusterType");
		String httpPath = params.getString("httpPath");
		String azureAuthenticationType = params.getString("azureAuthenticationType");
		String clusterPolicyId = "";
		if (params.has("clusterPolicyId")) {
			clusterPolicyId = params.getString("clusterPolicyId");
		}
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (incrementalDataReadEnabled != null && incrementalDataReadEnabled.trim().equalsIgnoreCase("Y")) {
						incrementalDataReadEnabled = "Y";
					} else {
						incrementalDataReadEnabled = "N";
					}
					if (pushDownQueryEnabled != null && pushDownQueryEnabled.trim().equalsIgnoreCase("Y")) {
						pushDownQueryEnabled = "Y";
					} else {
						pushDownQueryEnabled = "N";
					}
					LOG.debug("=======================>incrementalDataReadEnabled=" + incrementalDataReadEnabled);
					long idDataSchema = dataConnectionService.saveConnectionInfo(projectId, domainId, idUser, createdBy,
							schemaName, schemaType, uri, database, username, password, port, domain, serviceName,
							krb5conf, autoGenerateId, hivejdbchost, hivejdbcport, suffix, prefix, sslEnb,
							sslTrustStorePath, trustPassword, gatewayPath, jksPath, zookeeperUrl, folderPath,
							fileNamePattern, fileDataFormat, headerPresent, partitionedFolders, headerFilePath,
							headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,
							bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
							azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath,
							enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
							maxFolderDepth, fileEncrypted, singleFile, externalFileNamePatternId, externalFileName,
							patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath, kmsAuthDisabled,
							readLatestPartition, alationIntegrationEnabled, incrementalDataReadEnabled,
							clusterPropertyCategory, "N", pushDownQueryEnabled, httpPath, clusterPolicyId,
							azureAuthenticationType);
					if (idDataSchema > 0l) {
						message = "Data Connection Created.";
						LOG.info("\n====> Data Connection Created Successfully");
						json = dataConnectionService.runSchemaJob(idDataSchema, "Y");
						String healthCheckStatus = json.getString("status");
						if (healthCheckStatus.equalsIgnoreCase("success")) {
							status = "success";
						}
						String healthCheckMessage = json.getString("message");
						message = message + healthCheckMessage;
						status = "success";
						json.put("idDataSchema", idDataSchema);
						LOG.info("\n====> Data Connection Created Successfully");
					} else {
						message = "Data Connection failed,Please check Configuration";
						LOG.error("\n====> Data Connection failed");
					}
				} else {
					json.put("status", "failed");
					json.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/healthCheck - END");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				json.put("status", "failed");
				json.put("message", "Token is missing in headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/healthCheck - END");
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			LOG.error(e.getMessage());
		}
		try {
			json.put("status", status);
			json.put("message", message);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		LOG.info("dbconsole/healthCheck - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/updateConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> updateSchema(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/updateConnection - START");
		String schemaName = (String) params.get("schemaName");
		String schemaType = (String) params.get("schemaType");
		String uri = (String) params.get("uri");
		String database = (String) params.get("database");
		String username = (String) params.get("username");
		String password = (String) params.get("password");
		String port = String.valueOf(params.get("port"));
		String domain = (String) params.get("domain");
		String serviceName = (String) params.get("serviceName");
		String krb5conf = (String) params.get("krb5conf");
		String hivejdbchost = (String) params.get("hivejdbchost");
		String hivejdbcport = (String) params.get("hivejdbcport");
		String sslTrustStorePath = (String) params.get("sslTrustStorePath");
		String trustPassword = (String) params.get("trustPassword");
		String gatewayPath = (String) params.get("gatewayPath");
		String jksPath = (String) params.get("jksPath");
		String zookeeperUrl = (String) params.get("zookeeperUrl");
		String folderPath = (String) params.get("folderPath");
		String fileNamePattern = (String) params.get("fileNamePattern");
		String fileDataFormat = (String) params.get("fileDataFormat");
		String headerPresent = (String) params.get("headerPresent");
		String partitionedFolders = (String) params.get("partitionedFolders");
		String headerFilePath = (String) params.get("headerFilePath");
		String headerFileNamePattern = (String) params.get("headerFileNamePattern");
		String headerFileDataFormat = (String) params.get("headerFileDataFormat");
		String accessKey = (String) params.get("accessKey");
		String secretKey = (String) params.get("secretKey");
		String bucketName = (String) params.get("bucketName");
		String bigQueryProjectName = (String) params.get("bigQueryProjectName");
		String privatekeyId = (String) params.get("privatekeyId");
		String privatekey = (String) params.get("privatekey");
		String clientId = (String) params.get("clientId");
		String clientEmail = (String) params.get("clientEmail");
		String datasetName = (String) params.get("datasetName");
		String azureClientId = (String) params.get("azureClientId");
		String azureClientSecret = (String) params.get("azureClientSecret");
		String azureTenantId = (String) params.get("azureTenantId");
		String azureServiceURI = (String) params.get("azureServiceURI");
		String azureFilePath = (String) params.get("azureFilePath");
		String enableFileMonitoring = (String) params.get("enableFileMonitoring");
		String multiPattern = (String) params.get("multiPattern");
		int startingUniqueCharCount = (int) params.get("startingUniqueCharCount");
		int endingUniqueCharCount = (int) params.get("endingUniqueCharCount");
		int maxFolderDepth = Integer.parseInt(params.get("maxFolderDepth").toString());
		String fileEncrypted = (String) params.get("fileEncrypted");
		String singleFile = (String) params.get("singleFile");
		String externalFileNamePatternId = (String) params.get("externalFileNamePatternId");
		String externalFileName = (String) params.get("externalFileName");
		String patternColumn = (String) params.get("patternColumn");
		String headerColumn = (String) params.get("headerColumn");
		String localDirectoryColumnIndex = (String) params.get("localDirectoryColumnIndex");
		String xsltFolderPath = (String) params.get("xsltFolderPath");
		String kmsAuthDisabled = (String) params.get("kmsAuthDisabled");
		String readLatestPartition = (String) params.get("readLatestPartition");
		String alationIntegrationEnabled = (String) params.get("alationIntegrationEnabled");
		String incrementalDataReadEnabled = (String) params.get("incrementalDataReadEnabled");
		long idDataSchema = Long.valueOf(String.valueOf(params.get("idDataSchema")));
		String clusterPropertyCategory = (String) params.get("clusterType");
		String sslEnabled = (String) params.get("sslEnb");
		String pushDownQueryEnabled = (String) params.get("pushDownQueryEnabled");
		String azureAuthenticationType = (String) params.get("azureAuthenticationType");
		String clusterPolicyId = (String) params.get("clusterPolicyId");

		// Added for Delta Lake
		String httpPath = (String) params.get("httpPath");

		String status = "failed";
		String message = "";
		Map<String, String> response = new HashMap<>();
		LOG.debug("schemaType=" + schemaType);
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					if (!schemaType.equalsIgnoreCase("mysql")) {
						sslEnabled = "N";
					}
					if (schemaType.equalsIgnoreCase("oracle")) {
						port = port + "/" + serviceName;
					}
					if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
						uri = azureServiceURI;
					}
					List<ListDataSchema> listDataSchemaList = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
					ListDataSchema listDataSchema = listDataSchemaList.get(0);
					String enableFileMonitoring_Old = listDataSchema.getEnableFileMonitoring();

					if (incrementalDataReadEnabled != null && incrementalDataReadEnabled.trim().equalsIgnoreCase("Y")) {
						incrementalDataReadEnabled = "Y";
					} else {
						incrementalDataReadEnabled = "N";
					}
					Long update = schemaDAOI.updateDataIntoListDataSchema(uri, database, username, password, port,
							schemaName, schemaType, domain, idDataSchema, serviceName, krb5conf, hivejdbchost,
							hivejdbcport, sslTrustStorePath, trustPassword, gatewayPath, jksPath, zookeeperUrl,
							folderPath, fileNamePattern, fileDataFormat, headerPresent, headerFilePath,
							headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,
							bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
							azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath,
							partitionedFolders, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
							maxFolderDepth, fileEncrypted, singleFile, externalFileNamePatternId, externalFileName,
							patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath, kmsAuthDisabled,
							readLatestPartition, enableFileMonitoring, alationIntegrationEnabled,
							incrementalDataReadEnabled, "N", sslEnabled, pushDownQueryEnabled, httpPath,
							clusterPropertyCategory, azureAuthenticationType, clusterPolicyId);
					System.out.println("update=" + update + " enableFileMonitoring= " + enableFileMonitoring);

					// update FileMonitoring rules associated with the idDataSchema
					if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")
							|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")
							|| schemaType.equalsIgnoreCase("S3 Batch")
							|| schemaType.equalsIgnoreCase("FileSystem Batch")
							|| schemaType.equalsIgnoreCase("SnowFlake")
							|| schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")
							|| schemaType.equalsIgnoreCase("DatabricksDeltaLake")) {
						List<FileMonitorRules> fmRulesList = fileMonitorDao.getFileMonitorRulesForSchema(idDataSchema);

						if (fmRulesList != null && fmRulesList.size() > 0) {
							for (FileMonitorRules fmRule : fmRulesList) {
								fmRule.setFolderPath(folderPath);
								fmRule.setFilePattern(fileNamePattern);
								fmRule.setPartitionedFolders(partitionedFolders);
								if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")
										|| schemaType.equalsIgnoreCase("S3 Batch")
										|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")) {
									fmRule.setBucketName(bucketName);
								}
								fileMonitorDao.addMonitorRule(fmRule);
							}
						}
						dataConnectionService.enableOrDisableFileMonitoringForConnection(idDataSchema,
								enableFileMonitoring, enableFileMonitoring_Old);
					}

					if (update > 0l) {
						// changes regarding Audit trail
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_CONNECTION, formatter.format(new Date()), idDataSchema,
								DatabuckConstants.ACTIVITY_TYPE_EDITED, schemaName);
						message = "Data Connection Customized Successfully";
						status = "success";
						LOG.info(message);
					} else {
						message = "Entered wrong Credentials";
						status = "failed";
						LOG.error("\n====> Data Connection Customized failed");
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/updateConnection - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/updateConnection - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		response.put("status", status);
		response.put("message", message);
		LOG.info("dbconsole/updateConnection - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/deleteConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteSchema(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/deleteConnection - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					long idDataSchema = params.get("id");
					String schemaName = "";
					List<ListDataSchema> listDataSchemaById = listdatasourcedao.getListDataSchemaId(idDataSchema);
					for (ListDataSchema listDataSchema : listDataSchemaById) {
						schemaName = listDataSchema.getSchemaName();
					}
					boolean deleteSchema = listdatasourcedao.deactivateSchema(idDataSchema);
					LOG.debug("idDataSchema" + idDataSchema);
					LOG.debug("deleteSchema" + deleteSchema);
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_CONNECTION, formatter.format(new Date()), idDataSchema,
							DatabuckConstants.ACTIVITY_TYPE_DELETED, schemaName);
					response.put("message", "Data Connection Deactivated Successfully");
					response.put("status", "success");
					LOG.info("Data Connection Deactivated Successfully");
					LOG.info("dbconsole/deleteConnection - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/deleteConnection - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/deleteConnection - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to deactivate Schema.");
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteConnection - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/activateConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> activateConnection(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/activateConnection - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());

			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					long idDataSchema = params.get("id");

					listdatasourcedao.activateSchema(idDataSchema);

					response.put("message", "Data Connection activated Successfully");
					response.put("status", "success");
					LOG.info("Data Connection activated Successfully");
					LOG.info("dbconsole/activateConnection - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/activateConnection - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/activateConnection - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to activate Schema.");
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/activateConnection - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/activateTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> activateTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/activateTemplate - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());

			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					long idData = params.get("id");
					ListDataSource dataFromListDataSourcesOfIdData = listdatasourcedao
							.getDataFromListDataSourcesOfIdData(idData);
					List<ListDataSchema> ls = listdatasourcedao
							.getListDataSchemaId(dataFromListDataSourcesOfIdData.getIdDataSchema());
					Map<String, Object> result = new HashMap<>();
					if (!ls.isEmpty() && ls.get(0).getAction().equalsIgnoreCase("no")) {
						result.put("connectionId", ls.get(0).getIdDataSchema());
						result.put("connectionName", ls.get(0).getSchemaName());
						result.put("activate", true);
						response.put("result", result);
						response.put("status", "failed");
						response.put("message", "Re-activate respective connection(" + ls.get(0).getSchemaName()
								+ ") to activate this template.");
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
					boolean status = listdatasourcedao.activateTemplate(idData);
					if (status) {
						response.put("message", "Data Template activated Successfully");
						response.put("status", "success");
					}else {
						response.put("message", "Failed to activate data ");
						response.put("status", "success");
					}
					LOG.info("Data Template activated Successfully");
					LOG.info("dbconsole/activateTemplate - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/activateTemplate - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/activateTemplate - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to activate Template.");
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/activateTemplate - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/duplicateSchemaName", method = RequestMethod.POST)
	public ResponseEntity<Object> duplicatedatatemplatename(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/duplicateSchemaName - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		Map<String, Object> response = new HashMap<>();
		long projectId = inputJson.getLong("projectId");
		Integer domainId = inputJson.getInt("domainId");
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					LOG.debug("Getting request parameters  " + inputJson);
					String dataSchemaName = inputJson.getString("connectionName");
					if (dataSchemaName == null || dataSchemaName.trim().isEmpty()) {
						throw new Exception("Please enter Connection Name");
//						response.put("messge", "Please Enter Connection Name");
//						response.put("status", "failed");
//						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
					Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");// "[^A-Za-z0-9_]"
					Matcher match = pattern.matcher(dataSchemaName);
					boolean val = match.find();
					LOG.debug("val" + val);
					if (val == false) {
						throw new Exception(
								"Please start naming with alphabets and do not use spaces or special characters except _(underscore)");
//						response.put("messge", "Name must begin with a letter and cannot contain spaces,special characters");
//						response.put("status", "failed");
//						return new ResponseEntity<Object>(response, HttpStatus.OK);

					} else {
						Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_]*$");
						Matcher match2 = pattern2.matcher(dataSchemaName);
						boolean val2 = match2.find();
						if (val2 == false) {
							throw new Exception(
									"Please start naming with alphabets and do not use spaces or special characters except _(underscore)");
//							response.put("messge", "Name must begin with a letter and cannot contain spaces,special characters");
//							response.put("status", "failed");
//							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					}
					String name = schemaDAOI.duplicateSchemaName(dataSchemaName, projectId, domainId);
					if (name != null && !name.isEmpty()) {
						throw new Exception("The Connection Name is in use. Please choose another name.");
//						response.put("messge", "This Schema Name is in use. Please choose another name.");
//						response.put("status", "failed");
//						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/duplicateSchemaName - END");
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/duplicateSchemaName - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("isDataConnectionNameValid", false);
			response.put("message", e.getMessage());
			response.put("status", "success");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/duplicateSchemaName - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("isDataConnectionNameValid", true);
		response.put("messge", "The Connection Name is not in use.");
		response.put("status", "success");
		LOG.error("The Connection Name is not in use.");
		LOG.info("dbconsole/duplicateSchemaName - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/copyConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> copyConnection(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/copyConnection - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		// Map<String, Object> response = new HashMap<>();
		String newConnectionName = inputJson.getString("newConnectionName");
		int idDataSchema = inputJson.getInt("idDataSchema");
		String createdByUser = inputJson.getString("createdBy");
		LOG.debug("newConnectionName is " + newConnectionName);
		LOG.debug("idDataSchema is " + idDataSchema);
		JSONObject json = new JSONObject();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					String activeDirectoryFlag = appDbConnectionProperties
							.getProperty("isActiveDirectoryAuthentication");
					LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
					List<ListDataSchema> listDataSchema_list = listdatasourcedao
							.getListDataSchemaForIdDataSchema(idDataSchema);
					if (listDataSchema_list != null && listDataSchema_list.size() > 0) {
						ListDataSchema listDataSchema = listDataSchema_list.get(0);
						long projectId = listDataSchema.getProjectId();
						int domainId = listDataSchema.getDomainId();
						String duplicateName = schemaDAOI.duplicateSchemaName(newConnectionName, projectId, domainId);
						if (duplicateName != null && !duplicateName.trim().isEmpty()) {
							LOG.debug("\n====> A connection with name [] already exists in Domain:[" + domainId
									+ "] and Project:[" + projectId + "] combination !!");
							json.put("message", "Connection copy failed, name already exits");
							json.put("status", "failed");
						} else {
							String updateListDatasSchema = ("insert into listDataSchema (schemaName, schemaType, "
									+ "ipAddress, databaseSchema, username, password, port, project_id, domain, "
									+ "gss_jaas, krb5conf, autoGenerate, suffixes, "
									+ "prefixes, createdAt, updatedAt, createdBy, "
									+ "updatedBy, hivejdbchost, hivejdbcport, sslEnb, "
									+ "sslTrustStorePath, trustPassword, Action, createdByUser, folderPath, fileNamePattern, fileDataFormat, "
									+ "headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,"
									+ "bigQueryProjectName, privateKey, privateKeyId, clientId, clientEmail, datasetName,"
									+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath,partitionedFolders,"
									+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount, maxFolderDepth, fileEncrypted, domain_id, "
									+ "readLatestPartition,alation_integration_enabled,incremental_dataread_enabled,cluster_property_category,multiFolderEnabled,push_down_query_enabled,http_path"
									+ ") (select '" + newConnectionName + "' as schemaName, schemaType, "
									+ "ipAddress, databaseSchema, username, password, port, project_id, domain, "
									+ "gss_jaas, krb5conf, autoGenerate, suffixes, "
									+ "prefixes, createdAt, updatedAt, createdBy, "
									+ "updatedBy, hivejdbchost, hivejdbcport, sslEnb, "
									+ "sslTrustStorePath, trustPassword, 'Yes', '" + createdByUser
									+ "' as createdByUser, folderPath, fileNamePattern, fileDataFormat, "
									+ "headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName, "
									+ "bigQueryProjectName, privateKey, privateKeyId, clientId, clientEmail, datasetName,"
									+ "azureClientId,azureClientSecret,azureTenantId,azureServiceURI,azureFilePath,partitionedFolders,"
									+ "multiPattern,startingUniqueCharCount,endingUniqueCharCount, maxFolderDepth, fileEncrypted, domain_id, "
									+ "readLatestPartition,alation_integration_enabled,incremental_dataread_enabled,cluster_property_category,multiFolderEnabled,push_down_query_enabled,http_path"
									+ " from listDataSchema where " + "idDataSchema = " + idDataSchema + ")");

							// Query compatibility changes for both POSTGRES and MYSQL
							String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
									? "iddataschema"
									: "idDataSchema";

							KeyHolder keyHolder = new GeneratedKeyHolder();
							jdbcTemplate.update(new PreparedStatementCreator() {
								public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement pst = con.prepareStatement(updateListDatasSchema,
											new String[] { key_name });
									return pst;
								}
							}, keyHolder);

							// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
							int resultIdDataSchema = (int) keyHolder.getKey().longValue();

							if (resultIdDataSchema > 0) {
								// changes regarding Audit trail
								UserToken userToken = dashboardConsoleDao
										.getUserDetailsOfToken(headers.get("token").get(0));
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
										DatabuckConstants.DBK_FEATURE_CONNECTION, formatter.format(new Date()),
										(long) resultIdDataSchema, DatabuckConstants.ACTIVITY_TYPE_CREATED,
										newConnectionName);
							}
							LOG.info("listdataschema updated");

							json.put("message", "Data Connection Copied Successfully");
							json.put("status", "success");
						}
					} else {
						LOG.debug("\n====> Failed to get details of connection with Id[" + idDataSchema
								+ "], hence copy failed !!");
						json.put("message", "Data Connection copy failed");
						json.put("status", "failed");
					}
				} else {
					json.put("status", "failed");
					json.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					LOG.info("dbconsole/copyConnection - END");
					return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				json.put("status", "failed");
				json.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/copyConnection - END");
				return new ResponseEntity<Object>(json.toString(), HttpStatus.EXPECTATION_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		LOG.info("dbconsole/copyConnection - END");
		return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/testDataConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> testDataConnection(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/testDataConnection - START");
		Map<String, Object> response = new HashMap<String, Object>();
		JSONObject param = new JSONObject(inputsJsonStr);
		String schemaName = param.getString("schemaName");
		String schemaType = param.getString("schemaType");
		String uri = param.getString("uri");
		String database = param.getString("database");
		String username = param.getString("username");
		String password = param.getString("password");
		String port = ""+ param.get("port");
		String domain = param.getString("domain");
		String serviceName = param.getString("serviceName");
		String krb5conf = param.getString("serviceName");
		long idDataSchema = param.getLong("idDataSchema");
		String gatewayPath = param.getString("gatewayPath");
		String jksPath = param.getString("jksPath");
		String folderPath = param.getString("folderPath");
		String fileNamePattern = param.getString("fileNamePattern");
		String fileDataFormat = param.getString("fileDataFormat");
		String headerPresent = param.getString("headerPresent");
		String partitionedFolders = param.getString("partitionedFolders");
		String headerFilePath = param.getString("headerFilePath");
		String headerFileNamePattern = param.getString("headerFileNamePattern");
		String headerFileDataFormat = param.getString("headerFileDataFormat");
		String accessKey = param.getString("accessKey");
		String secretKey = param.getString("secretKey");
		String bucketName = param.getString("bucketName");
		String bigQueryProjectName = param.getString("bigQueryProjectName");
		String privatekeyId = param.getString("privatekeyId");
		String privatekey = param.getString("privatekey");
		String clientId = param.getString("clientId");
		String clientEmail = param.getString("clientEmail");
		String datasetName = param.getString("datasetName");
		String azureClientId = param.getString("azureClientId");
		String azureClientSecret = param.getString("azureClientSecret");
		String azureTenantId = param.getString("azureTenantId");
		String azureServiceURI = param.getString("azureServiceURI");
		String azureFilePath = param.getString("azureFilePath");
		String multiPattern = param.getString("multiPattern");
		int startingUniqueCharCount = param.getInt("startingUniqueCharCount");
		int endingUniqueCharCount = param.getInt("endingUniqueCharCount");
		int maxFolderDepth = param.getInt("maxFolderDepth");
		String fileEncrypted = param.getString("fileEncrypted");
		String kmsAuthDisabled = param.getString("kmsAuthDisabled");
		String incrementalDataReadEnabled = param.getString("incrementalDataReadEnabled");
		int domainId = param.getInt("domainId");
		String sslEnb = param.getString("sslEnb");
		String azureAuthenticationType = param.getString("azureAuthenticationType");
		// added for Delta Lake
		String httpPath = param.getString("httpPath");

		String token = headers.get("token").get(0);
		LOG.debug("token " + token.toString());
		if (token != null && !token.isEmpty()) {
			if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
				response.put("status", "failed");
				response.put("message", "Token is expired.");
				LOG.error("Token is expired.");
				LOG.info("dbconsole/testDataConnection - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} else {
			response.put("status", "failed");
			response.put("message", "Token is missing in headers.");
			LOG.info("dbconsole/testDataConnection - END");
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}

		if (password.trim().length() > 1) {
		} else {
			password = schemaDAOI.getPasswordForIdDataSchema(idDataSchema);
		}
		if (secretKey == null || secretKey.trim().isEmpty()) {
			secretKey = schemaDAOI.getSecretKeyForIdDataSchema(idDataSchema);
		}
		LOG.debug("schemaType=" + schemaType);
		Connection con = null;
		boolean mapRConn = false;
		CqlSession cassandraSession = null;
		boolean fileSystemBatchCon = false;
		boolean s3BatchCon = false;
		boolean bigQueryCon = false;
		boolean azureDataLakeCon = false;
		boolean azureDataLakeGen2BatchCon = false;
		boolean flag = true;
		
		String credentialsFail = "Entered wrong username/password.";
		String uriFail = "Entered wrong uri/port.";
		String dbFail = "Entered wrong database/schema.";

		try {

			/*
			 * When KMS Authentication is enabled, db user details comes from logon manager
			 */
			if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

				// Check if the schema type is KMS enabled
				if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(schemaType)) {

					// Get the credentials from logon manager
					Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(schemaName);

					// Validate the logon manager response for key
					boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

					if (responseStatus) {
						uri = conn_user_details.get("hostname");
						port = conn_user_details.get("port");
						username = conn_user_details.get("username");
						password = conn_user_details.get("password");
					} else
						flag = false;
				} else {
					flag = false;
					LOG.debug("\n====>KMS Authentication is not supported for [" + schemaType + "] !!");
				}
			}

			if (flag) {
				if (schemaType.equalsIgnoreCase("Oracle")) {

					port = port + "/" + serviceName;

					String url = "jdbc:oracle:thin:@" + uri + ":" + port;
					try {
						Class.forName("oracle.jdbc.driver.OracleDriver");
						con = DriverManager.getConnection(url, username, password);
						// System.out.println(con);
					} catch (Exception e) {
						// System.out.println(con);
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				} else if (schemaType.equalsIgnoreCase("Oracle RAC")) {
					/*
					 * String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" +
					 * "(HOST = " + uri + ")(PORT = " + port +
					 * "))(CONNECT_DATA =(SERVER = DEDICATED)" + "(SERVICE_NAME = " + serviceName +
					 * ")))";
					 */
					try {
						Class.forName("oracle.jdbc.driver.OracleDriver");
						con = DriverManager.getConnection(uri, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				} else if (schemaType.equalsIgnoreCase("MSSQL")) {
					int dotPosition = database.indexOf(".");
					String database1 = database;
					String[] databasAndSchema = database1.split("\\.");
					if (databasAndSchema.length == 2 && databasAndSchema[0] != null
							&& !databasAndSchema[0].trim().isEmpty() && databasAndSchema[1] != null
							&& !databasAndSchema[1].trim().isEmpty()) {
						database1 = database.substring(0, dotPosition);
						String schema = database.substring(dotPosition + 1, database.length());
						String url = "jdbc:sqlserver://" + uri + ":" + port + ";databaseName=" + database1
								+ ";encrypt=true;trustServerCertificate=true;";
						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(url, username, password);
							ResultSet rsSchema = con.getMetaData().getSchemas();
							boolean isSchemaNameValid = false;
							while (rsSchema.next()) {
								String schemaNameStr = rsSchema.getString("TABLE_SCHEM");
								if (schemaNameStr.equalsIgnoreCase(schema)) {
									isSchemaNameValid = true;
									break;
								}
							}
							if (!isSchemaNameValid)
								throw new Exception(dbFail);
							
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							if(e.getMessage().contains("Login failed for user")) {
								throw new Exception(credentialsFail);
							}else if(e.getMessage().contains("The TCP/IP connection to the host")) {
								throw new Exception(uriFail);
							}else {
								throw new Exception(dbFail);
							}
								
						}
					} else
						throw new Exception(dbFail);

				} else if (schemaType.equalsIgnoreCase("MSSQLAD")) {
					int dotPosition = database.indexOf(".");
					String database1 = database;
					String[] databasAndSchema = database1.split("\\.");
					if (databasAndSchema.length == 2 && databasAndSchema[0] != null
							&& !databasAndSchema[0].trim().isEmpty() && databasAndSchema[1] != null
							&& !databasAndSchema[1].trim().isEmpty()) {
						database1 = database.substring(0, dotPosition);
						String schema = database.substring(dotPosition + 1, database.length());
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						String dbConnectString = "";

						String databaseSchema = database.split("\\.")[0];
						schema = database.split("\\.")[1];

						if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryPassword")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port + ";database=" + databaseSchema
									+ ";user=" + username + ";password=" + password + ";"
									+ "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"
									+ "loginTimeout=30;authentication=ActiveDirectoryPassword";
						} else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryIntegrated")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port + ";database=" + databaseSchema
									+ ";encrypt=true;trustServerCertificate=false;"
									+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
									+ "authentication=ActiveDirectoryIntegrated";
						} else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryDefault")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port + ";database=" + databaseSchema
									+ ";user=" + username + ";password=" + password + ";"// CloudSA8d3b0977@databuck
									+ "encrypt=true;trustServerCertificate=false;"
									+ "hostNameInCertificate=*.database.windows.net;" + "loginTimeout=30;";
						} else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryManagedIdentity")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port
									+ ";authentication=ActiveDirectoryManagedIdentity;database=" + database;
						} else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryServicePrinicipal")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port + ";database=" + databaseSchema
									+ ";user=" + username + ";password=" + password
									+ ";Authentication=ActiveDirectoryServicePrincipal";
						} else if (azureAuthenticationType.equalsIgnoreCase("NotSpecified")) {
							dbConnectString = "jdbc:sqlserver://" + uri + ":" + port + ";database=" + databaseSchema
									+ ";user=" + username + ";password=" + password + ";"
									+ "integratedSecurity=true;trustServerCertificate=true;authenticationScheme=NTLM;authentication=NotSpecified";
						}
						LOG.info("Connection String created >>>>>>>>>>>>>" + dbConnectString);

						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(dbConnectString);
							ResultSet rsSchema = con.getMetaData().getSchemas();
							boolean isSchemaNameValid = false;
							while (rsSchema.next()) {
								String schemaNameStr = rsSchema.getString("TABLE_SCHEM");
								if (schemaNameStr.equalsIgnoreCase(schema)) {
									isSchemaNameValid = true;
									break;
								}
							}
							if (!isSchemaNameValid)
								throw new Exception("Data Connection failed. Please provide valid schema name.");

						} catch (Exception e) {
							if (e.getMessage().startsWith("Cannot open database")) {
								throw new Exception("Data Connection failed. Please provide valid schema name.");
							}
							throw new Exception(e.getMessage());
						}

					} else
						throw new Exception("Data Connection failed,Please  provide valid schema name");

				} else if (schemaType.equalsIgnoreCase("Postgres")) {
					String[] dbAndSchema = database.split(",");

					String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0];
					if (dbAndSchema.length > 1 && dbAndSchema[1].length() > 0) {
						url = url + "?currentSchema=" + dbAndSchema[1]
								+ "&";
					} else {
						url = url + "?";
					}
					if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
						url = url + "ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
					}

					try {
						Class.forName("org.postgresql.Driver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				} else if (schemaType.equalsIgnoreCase("Teradata")) {
					String[] dbAndSchema = database.split(",");

					String url = "jdbc:teradata://" + uri;
					try {
						Class.forName("com.teradata.jdbc.TeraDriver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				} else if (schemaType.equalsIgnoreCase("MSSQLActiveDirectory")) {

					try {
						Class.forName("net.sourceforge.jtds.jdbc.Driver");
						String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim()
								+ ";domain=" + domain.trim();
						con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");

					}

				} else if (schemaType.equalsIgnoreCase("Vertica")) {

					int dotPosition = database.indexOf(".");
					String database1 = database;
					if (dotPosition != -1) {
						database1 = database.substring(0, dotPosition);
					}
					String url = "jdbc:vertica://" + uri + ":" + port + "/" + database1;
					try {
						Class.forName("com.vertica.jdbc.Driver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				} else if (schemaType.equalsIgnoreCase("Cassandra")) {
					try {
						CqlSessionBuilder builder = CqlSession.builder();
						builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port)))
								.withAuthCredentials(username, password);

						cassandraSession = builder.build();
						cassandraSession.close();
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				} else if (schemaType.equalsIgnoreCase("Hive") || schemaType.equalsIgnoreCase("Cloudera Hive")) {

					// Get the domain name
					// Integer domainId = (Integer) session.getAttribute("domainId");
					String domainName = iTaskDAO.getDomainNameById((long) domainId);
					String clusterPropertyCategory = "cluster";
					Map<String, String> clusterProperties = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema);
					if (clusterProperties != null && clusterProperties.containsKey("cluster_property_category")) {
						clusterPropertyCategory = clusterProperties.get("cluster_property_category");
					}
					LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

					if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
							&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

						String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");
						boolean isRemoteIp = false;
						if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
							try {
								String proxy_uri = appDbConnectionProperties
										.getProperty("proxy_" + clusterPropertyCategory.trim());
								if (proxy_uri != null && !proxy_uri.isEmpty()) {
									isRemoteIp = true;
								} else
									LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
							} catch (Exception e) {
								LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
							}

						}

						if (isRemoteIp) {
//								String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
							String publishUrl = connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,
									DatabuckConstants.DATA_CONNECTION_API);
							String token1 = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

							StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
							encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

							String encryptedToken = encryptor.encrypt(token1);

							JSONObject inputObj = new JSONObject();

							inputObj.put("domainName", domainName);
							inputObj.put("uri", uri);
							inputObj.put("port", port);
							inputObj.put("database", database);
							// inputObj.put("propertySource",""+propertySource);

							String inputJson = inputObj.toString();
							LOG.debug("inputJson=" + inputJson);

							boolean testConnStatus = remoteClusterAPIService.testConnectionByRemoteCluster(publishUrl,
									encryptedToken, inputJson);

							if (testConnStatus) {
								LOG.info("Success!");
								mapRConn = true;
							} else {
								LOG.error("Failure!");
							}
						} else {
							LOG.debug("Connection id[" + idDataSchema + "] does not have remote ip support");
						}

					} else {
						try {
							String dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database;
							Class.forName("org.apache.hive.jdbc.HiveDriver");
							con = DriverManager.getConnection(dbURL2, username, password);
						} catch (Exception e) {
							throw new Exception("Data Connection failed,Please check Configuration");

						}
					}
				} else if (schemaType.equalsIgnoreCase("Hive Kerberos")) {

					// Get the domain name
					// Integer domainId = (Integer) session.getAttribute("domainId");
					String domainName = iTaskDAO.getDomainNameById((long) domainId);

					String clusterPropertyCategory = "cluster";
					Map<String, String> clusterProperties = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema);
					if (clusterProperties != null && clusterProperties.containsKey("cluster_property_category")) {
						clusterPropertyCategory = clusterProperties.get("cluster_property_category");
					}
					LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

					if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
							&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

						String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");
						boolean isRemoteIp = false;
						if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
							try {
								String proxy_uri = appDbConnectionProperties
										.getProperty("proxy_" + clusterPropertyCategory.trim());
								if (proxy_uri != null && !proxy_uri.isEmpty()) {
									isRemoteIp = true;
								} else
									LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
							} catch (Exception e) {
								LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
							}

						}

						if (isRemoteIp) {
//								String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
							String publishUrl = connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,
									DatabuckConstants.DATA_CONNECTION_API);
							String token1 = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

							StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
							encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

							String encryptedToken = encryptor.encrypt(token1);

							JSONObject inputObj = new JSONObject();

							inputObj.put("domainName", domainName);
							inputObj.put("uri", uri);
							inputObj.put("port", port);
							inputObj.put("database", database);
							// inputObj.put("propertySource",""+propertySource);

							String inputJson = inputObj.toString();
							LOG.debug("inputJson=" + inputJson);

							boolean testConnStatus = remoteClusterAPIService.testConnectionByRemoteCluster(publishUrl,
									encryptedToken, inputJson);

							if (testConnStatus) {
								LOG.info("Success!");
								mapRConn = true;
							} else {
								LOG.error("Failure!");
							}
						} else {
							LOG.debug("Connection id[" + idDataSchema + "] does not have remote ip support");
						}

					} else {
						try {
							System.setProperty("java.security.auth.login.config", serviceName);
							// System.setProperty("sun.security.jgss.debug","true");
							System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
							System.setProperty("java.security.krb5.conf", krb5conf);
							Class.forName("org.apache.hive.jdbc.HiveDriver");

							// In case of zookeeper url there is no need of principal name
							/*
							 * String zookeeperURL = hiveConnectionURL(uri, port, database); if
							 * (zookeeperURL != null) url = "jdbc:hive2://" + zookeeperURL; else url =
							 * "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain;
							 */

							String url = null;
							if (uri.contains("2181") && jksPath != null && !jksPath.trim().isEmpty()) {
								LOG.debug(" uri: " + uri);
								url = "jdbc:hive2://" + uri + "/" + database
										+ ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal="
										+ domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
										+ password;
							} else if (jksPath != null && !jksPath.trim().isEmpty()) {
								LOG.debug(" uri: " + uri);
								url = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain
										+ ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword=" + password;
							} else {
								// dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
								// + domain;
								String zookeeperURL = dataConnectionService.hiveConnectionURL(uri, port, database);
								if (zookeeperURL != null)
									url = "jdbc:hive2://" + zookeeperURL;
								else
									url = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain;
							}
							LOG.debug("URL: " + url);
							con = DriverManager.getConnection(url);
						} catch (Exception e) {
							throw new Exception("Data Connection failed,Please check Configuration");

						}
					}

				} else if (schemaType.equalsIgnoreCase("MapR Hive")) {
					try {

						// Get the domain name
						// Integer domainId = (Integer) session.getAttribute("domainId");
						String domainName = iTaskDAO.getDomainNameById((long) domainId);

						String clusterPropertyCategory = "cluster";
						Map<String, String> clusterProperties = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema);
						if (clusterProperties != null && clusterProperties.containsKey("cluster_property_category")) {
							clusterPropertyCategory = clusterProperties.get("cluster_property_category");
						}
						LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

						if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
								&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

							String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");
							boolean isRemoteIp = false;
							if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
								try {
									String proxy_uri = appDbConnectionProperties
											.getProperty("proxy_" + clusterPropertyCategory.trim());
									if (proxy_uri != null && !proxy_uri.isEmpty()) {
										isRemoteIp = true;
									} else
										LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
								} catch (Exception e) {
									LOG.debug("Remote connection is not enabled for:" + clusterPropertyCategory);
								}

							}

							if (isRemoteIp) {
//								String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
								String publishUrl = connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,
										DatabuckConstants.DATA_CONNECTION_API);
								String token1 = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

								StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
								encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

								String encryptedToken = encryptor.encrypt(token1);

								JSONObject inputObj = new JSONObject();

								inputObj.put("domainName", domainName);
								inputObj.put("uri", uri);
								inputObj.put("port", port);
								inputObj.put("database", database);
								// inputObj.put("propertySource",""+propertySource);

								String inputJson = inputObj.toString();
								LOG.debug("inputJson=" + inputJson);

								boolean testConnStatus = remoteClusterAPIService
										.testConnectionByRemoteCluster(publishUrl, encryptedToken, inputJson);

								if (testConnStatus) {
									LOG.info("Success!");
									mapRConn = true;
								} else {
									LOG.error("Failure!");
								}
							} else {
								LOG.debug("Connection id[" + idDataSchema + "] does not have remote ip support");
							}

						} else {
							String databuckHome = DatabuckUtility.getDatabuckHome();

							ProcessBuilder processBuilder = new ProcessBuilder();
							// Need the get the project name
							processBuilder.command("bash", "-c",
									"sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
											+ " com.databuck.mapr.hive.ValidateHiveConnection " + uri + ":" + port + " "
											+ database);

							Process process = processBuilder.start();

							StringBuilder output = new StringBuilder();

							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

							String line;
							boolean successfulConnection = false;

							while ((line = reader.readLine()) != null) {
								output.append(line + "\n");
								if (line.contains("**Connection Status:Passed**")) {
									successfulConnection = true;
									break;
								}
							}

							int exitVal = process.waitFor();

							LOG.debug(output);

							if (exitVal == 0 && successfulConnection) {
								System.out.println("Success!");
								mapRConn = true;
								// System.exit(0);
							} else {
								// abnormal...
								LOG.debug("Failure!");
							}
						}

						if (!mapRConn) {
							throw new Exception("Data Connection failed,Please check Configuration");
						}

					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				} else if (schemaType.equalsIgnoreCase("Amazon Redshift")) {
					try {
						// String dbURL2 = "jdbc:hive2://"+uri+":"+port+"/"+database;
						Class.forName("com.amazon.redshift.jdbc42.Driver");
						Properties props = new Properties();
						props.setProperty("user", username);
						props.setProperty("password", password);
						con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database,
								props);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");

					}
				} else if (schemaType.equalsIgnoreCase("MySQL")) { // changes for import mysql connection 29jan2019

					String url = "jdbc:mysql://" + uri + ":" + port;
					try {
						Class.forName("com.mysql.jdbc.Driver");

						if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
							url = "jdbc:mysql://" + uri + ":" + port + "/" + database
									+ "?verifyServerCertificate=false&useSSL=true";
						}
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						flag = false;
						e.printStackTrace();
						if(e.getMessage().contains("Access denied for user")) {
							throw new Exception(credentialsFail);
						}else if(e.getMessage().contains("Communications link failure")) {
							throw new Exception(uriFail);
						}else {
							throw new Exception(dbFail);
						}
					}

				}

				// -------------------

				// changes for MapR 28May2019

				/*
				 * Hostname : ec2-3-81-127-18.compute-1.amazonaws.com
				 *
				 * Port : 8243
				 *
				 * Username : mapr
				 *
				 * Password : Welcome1
				 */

				// +";ssl=true"

				else if (schemaType.equalsIgnoreCase("MapR DB")) {

					String url = "jdbc:hive2://" + uri + ":" + port + "/" + database;
					// String url = "jdbc:hive2://" + uri + ":" + port +
					// "/default;principal=hive/ec2-3-81-127-18.compute-1.amazonaws.com";
					LOG.debug("In MapR =" + url);

					try {
						Class.forName("org.apache.hive.jdbc.HiveDriver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}

				}

				// ----------- Added Hive (Knox) connection 4June2019
				else if (schemaType.equalsIgnoreCase("Hive knox")) {
					/*
					 * connection URL has to be following:
					 * 
					 * jdbc:hive2://<gateway-host>:<gateway-port>/?hive.server2.servermode=https;
					 * hive.server2.http.path=<gateway-path>/<cluster-name>/hive
					 * 
					 */
					System.setProperty("javax.net.ssl.trustStore", jksPath);

					String url = dataConnectionService.hiveKnoxConnectionURL(uri, port, database, gatewayPath);

					LOG.debug("In Hive Knox =>" + url);

					try {
						// load Hive JDBC Driver
						Class.forName("org.apache.hive.jdbc.HiveDriver");

						// configure JDBC connection
						con = DriverManager.getConnection(url, username, password);

					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				} else if (schemaType.equalsIgnoreCase("SnowFlake")) {
					String[] dbAndSchema = database.split(",");
					LOG.debug("database ====>" + database);
					LOG.debug("uri ==>" + uri);

					String securemode = appDbConnectionProperties.getProperty("snowflake.securemode");
					if (securemode != null && securemode.trim().equalsIgnoreCase("Y")) {
						securemode = "Y";
					} else {
						securemode = "N";
					}

					String url = "";
					if (securemode.equalsIgnoreCase("Y")) {
						url = "jdbc:snowflake://" + uri + "?insecureMode=true";
					} else {
						url = "jdbc:snowflake://" + uri;
					}
					LOG.debug(url);

					// String
					// url="jdbc:snowflake://vs90372.us-east-1.snowflakecomputing.com/"+"?warehouse=SF_TUTS_WH&db="+
					// "TEST"+"&schema=TEST1";
					try {
						Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				}

				// --------------------

				// ----------- Added FileSystem Batch connection
				else if (schemaType.equalsIgnoreCase("FileSystem Batch")) {
					LOG.info("\n====> FileSystem Batch details <====");

					LOG.debug("\n** FolderPath: " + folderPath);
					LOG.debug("\n** FileName Pattern: " + fileNamePattern);
					LOG.debug("\n** File Data Format: " + fileDataFormat);
					LOG.debug("\n** Header Present: " + headerPresent);
					LOG.debug("\n** Header File Path: " + headerFilePath);
					LOG.debug("\n** Header FileName Pattern: " + headerFileNamePattern);
					LOG.debug("\n** Header File Data Pattern: " + headerFileDataFormat);

					try {
						LOG.info("\n====> Checking if the Folder Path exist ...");

						File f_folder = new File(folderPath);

						LOG.info("\n====> Checking if the Folder Path exist ...");

						if (f_folder.exists() && f_folder.isDirectory()) {

							LOG.info("\n====> Folder Path exits is true !!");

							if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
								LOG.info("\n====> File has header, hence no header path checking !!");

								LOG.info("\n====> Data Connection Established Successfully !!");
								fileSystemBatchCon = true;

							} else {
								LOG.info("\n====> Checking if Header Path exist ...");

								File h_folder = new File(headerFilePath);

								if (h_folder.exists() && h_folder.isDirectory()) {
									LOG.info("\n====> Header Path exits is true !!");

									LOG.info("\n====> Data Connection successful !!");

									fileSystemBatchCon = true;

								} else {
									LOG.error("\n====> Header Path does not exist !!");

									LOG.error("\n====> Data Connection failed !!");
								}

							}

						} else {
							LOG.error("\n====> Folder Path does not exist !!");

							LOG.error("\n====> Data Connection failed !!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!fileSystemBatchCon) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				}

				// ----------- Added S3 Batch connection
				else if (schemaType.equalsIgnoreCase("S3 Batch")) {

					LOG.info("\n====> S3 Batch details <====");

					LOG.debug("\n** bucketName: " + bucketName);
					LOG.debug("\n** FolderPath: " + folderPath);
					LOG.debug("\n** FileName Pattern: " + fileNamePattern);
					LOG.debug("\n** File Data Format: " + fileDataFormat);
					LOG.debug("\n** Header Present: " + headerPresent);
					LOG.debug("\n** Header File Path: " + headerFilePath);
					LOG.debug("\n** Header FileName Pattern: " + headerFileNamePattern);
					LOG.debug("\n** Header File Data Pattern: " + headerFileDataFormat);

					try {
						LOG.info("\n====> Checking if the Folder Path exist ...");

						AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

						S3Object folderPathKey = s3Client.getObject(bucketName, folderPath);

						if (folderPathKey != null) {

							LOG.info("\n====> Folder Path exits is true !!");

							if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
								LOG.info("\n====> File has header, hence no header path checking !!");

								LOG.info("\n====> Data Connection successful !!");

								s3BatchCon = true;

							} else {
								LOG.info("\n====> Checking if Header Path exist ...");

								S3Object headerFolderPathKey = s3Client.getObject(bucketName, headerFilePath);

								if (headerFolderPathKey != null) {
									LOG.info("\n====> Header Path exits is true !!");

									LOG.info("\n====> Data Connection successful !!");

									s3BatchCon = true;

								} else {
									LOG.error("\n====> Header Path does not exist !!");

									LOG.error("\n====> Data Connection failed !!");
								}
							}

						} else {
							LOG.error("\n====> Folder Path does not exist !!");

							LOG.error("\n====> Data Connection failed !!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!s3BatchCon) {
						throw new Exception("Data Connection failed,Please check Configuration");
					}
				}

				if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")) {

					LOG.info("\n====> S3 IAMRole Batch details <====");

					LOG.debug("\n** bucketName: " + bucketName);
					LOG.debug("\n** FolderPath: " + folderPath);
					LOG.debug("\n** FileName Pattern: " + fileNamePattern);
					LOG.debug("\n** File Data Format: " + fileDataFormat);
					LOG.debug("\n** Header Present: " + headerPresent);
					LOG.debug("\n** Header File Path: " + headerFilePath);
					LOG.debug("\n** Header FileName Pattern: " + headerFileNamePattern);
					LOG.debug("\n** Header File Data Pattern: " + headerFileDataFormat);

					try {
						LOG.info("\n====> Checking if the Folder Path exist ...");

						folderPath = folderPath.replace("//", "/");

						if (!folderPath.trim().isEmpty() && !folderPath.endsWith("/")) {
							folderPath = folderPath + "/";
						}

						String fullPath = (bucketName + "/" + folderPath).replace("//", "/");
						fullPath = "s3://" + fullPath;

						String awsCliCommand = "aws s3 ls " + fullPath;
						Process child = Runtime.getRuntime().exec(awsCliCommand);

						BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
						boolean folderPathExist = false;

						// Get Files list
						while (br.readLine() != null) {
							folderPathExist = true;
							break;
						}

						br.close();

						if (folderPathExist) {

							LOG.info("\n====> Folder Path exits is true !!");

							if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
								LOG.info("\n====> File has header, hence no header path checking !!");

								LOG.info("\n====> Data Connection successful !!");

								s3BatchCon = true;
							} else {
								LOG.info("\n====> Checking if Header Path exist ...");

								headerFilePath = headerFilePath.replace("//", "/");

								if (!headerFilePath.trim().isEmpty() && !headerFilePath.endsWith("/")) {
									headerFilePath = headerFilePath + "/";
								}

								String headerFileFullPath = (bucketName + "/" + headerFilePath).replace("//", "/");
								headerFileFullPath = "s3://" + headerFileFullPath;

								String h_awsCliCommand = "aws s3 ls " + headerFileFullPath;
								Process hchild = Runtime.getRuntime().exec(h_awsCliCommand);

								BufferedReader hbr = new BufferedReader(new InputStreamReader(hchild.getInputStream()));
								boolean headerFolderPathExist = false;

								// Get Files list
								while (hbr.readLine() != null) {
									headerFolderPathExist = true;
									break;
								}
								hbr.close();

								if (headerFolderPathExist) {
									LOG.info("\n====> Header Path exits is true !!");

									LOG.info("\n====> Data Connection successful !!");

									s3BatchCon = true;

								} else {
									LOG.error("\n====> Header Path does not exist !!");

									LOG.error("\n====> Data Connection failed !!");
								}
							}

						} else {
							LOG.error("\n====> Folder Path does not exist !!");

							LOG.error("\n====> Data Connection failed !!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else if (schemaType.equalsIgnoreCase("BigQuery")) {
					try {
						BigQuery bigQuery = bigQueryConnection.getBigQueryConnection(bigQueryProjectName, privatekeyId,
								privatekey, clientId, clientEmail, datasetName);
						if (bigQuery != null) {
							if (bigQuery != null) {
								// Check if tables present in dataset
								List<String> tablesList = bigQueryConnection.getListOfTableNamesFromBigQuery(
										bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail,
										datasetName);

								if (tablesList != null && tablesList.size() > 0) {
									bigQueryCon = true;
								} else {
									LOG.error("\n====> Failed to connect to Dataset or no tables found!!");
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (schemaType.equalsIgnoreCase("AzureSynapseMSSQL")) {

					String url = "jdbc:sqlserver://" + uri + ":" + port + ";DatabaseName=" + database;

					try {
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						con = DriverManager.getConnection(url, username, password);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
					try {
						if (null == azureFilePath || azureFilePath == "") {
							azureFilePath = "/";
						}

						AccessTokenProvider provider = new ClientCredsTokenProvider(
								"https://login.microsoftonline.com/" + azureTenantId + "/oauth2/token", azureClientId,
								azureClientSecret);
						AzureADToken azureADToken = null;

						azureADToken = provider.getToken();
						String accessToken = azureADToken.accessToken;
						LOG.debug("accessToken : " + accessToken);
						azureDataLakeCon = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
					azureDataLakeGen2BatchCon = azureDataLakeGen2Connection.validateConnection(accessKey, secretKey,
							bucketName, folderPath);
				} else if (schemaType.equalsIgnoreCase("DatabricksDeltaLake")) {
					System.out.println("========Testing DatabricksDeltaLake connection=========");
					String url = "jdbc:databricks://" + uri + ":" + port + ";" + "HttpPath=" + httpPath;

					System.out.println("url........." + url);
					Properties properties = new Properties();

					properties.put("username", username);
					properties.put("PWD", password);
					try {
						Class.forName("com.databricks.client.jdbc.Driver");
						con = DriverManager.getConnection(url, properties);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (con != null || cassandraSession != null || fileSystemBatchCon || s3BatchCon || bigQueryCon
						|| azureDataLakeCon || mapRConn || azureDataLakeGen2BatchCon) {
					try {
						// Closing the connection
						if (con != null) {
							con.close();
						}

						if (cassandraSession != null) {
							cassandraSession.close();
						}
						response.put("status", "success");
						response.put("message", "Data Connection Established Successfully");
						LOG.info("Data Connection Established Successfully");
						LOG.info("dbconsole/testDataConnection - END");
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else {
					throw new Exception("Data Connection failed,Please check Configuration");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("status", "failed");
			response.put("message", ex.getMessage());
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/testDataConnection - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("status", "failed");
		response.put("message", "Entered wrong Credentials");
		LOG.error("Entered wrong Credentials");
		LOG.info("dbconsole/testDataConnection - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/dbconsole/activateFileMonitoring")
	public ResponseEntity<Object> activateFileMonitoring(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/activateFileMonitoring - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token == null || token.isEmpty()) {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/activateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
				response.put("status", "failed");
				response.put("message", "Token is expired.");
				LOG.error("Token is expired.");
				LOG.info("dbconsole/activateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.containsKey("idDataSchema")) {
				response.put("status", "failed");
				response.put("message", "IdDataSchema is missing in request body.");
				LOG.error("IdDataSchema is missing in request body.");
				LOG.info("dbconsole/activateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			LOG.debug("Getting request parameters  " + params);
			schemaDAOI.updateEnableFileMonitoringFlagForSchema(params.get("idDataSchema"), "Y");
			List<Long> fmAppList = schemaDAOI.getFileMonitoringValidationsForSchema(params.get("idDataSchema"));
			List<Long> finalFMAppList = new ArrayList<Long>();
			if (fmAppList != null && fmAppList.size() > 0) {
				// Filter invalid appId's
				for (Long appId : fmAppList) {
					if (appId != null && appId > 0l) {
						finalFMAppList.add(appId);
					}
				}
			}
			// Activate the fileMonitoring validations
			if (finalFMAppList != null && finalFMAppList.size() > 0) {
				schemaDAOI.activateOrDeactivateFMValidations(finalFMAppList, "yes");
			} else {
				// If no FileMonitoring validations for this idDataSchema, create a new one
				dataConnectionService.createFileMonitoringValidation(params.get("idDataSchema"));
			}
			response.put("status", "success");
			response.put("message", "File monitoring is activated.");
			LOG.info("File monitoring is activated.");
			LOG.info("dbconsole/activateFileMonitoring - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to activate file monitoring.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/activateFileMonitoring - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/deactivateFileMonitoring")
	public ResponseEntity<Object> deactivateFileMonitoring(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Long> params) {
		LOG.info("dbconsole/deactivateFileMonitoring - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token " + token.toString());
			if (token == null || token.isEmpty()) {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				LOG.info("dbconsole/deactivateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!"success".equalsIgnoreCase(csvService.validateUserToken(token))) {
				response.put("status", "failed");
				response.put("message", "Token is expired.");
				LOG.error("Token is expired.");
				LOG.info("dbconsole/deactivateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.containsKey("idDataSchema")) {
				response.put("status", "failed");
				response.put("message", "IdDataSchema is missing in request body.");
				LOG.error("IdDataSchema is missing in request body.");
				LOG.info("dbconsole/deactivateFileMonitoring - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			LOG.debug("Getting request parameters  " + params);
			schemaDAOI.updateEnableFileMonitoringFlagForSchema(params.get("idDataSchema"), "N");
			// Identify FileMonitor rules which have idDataSchema associated and get the
			// corresponding the idApp
			List<Long> fmAppList = schemaDAOI.getFileMonitoringValidationsForSchema(params.get("idDataSchema"));
			List<Long> finalFMAppList = new ArrayList<Long>();
			if (fmAppList != null && fmAppList.size() > 0) {
				// Filter invalid appId's
				for (Long appId : fmAppList) {
					if (appId != null && appId > 0l) {
						finalFMAppList.add(appId);
					}
				}
			}
			// Deactivate the fileMonitoring validations
			if (finalFMAppList != null && finalFMAppList.size() > 0) {
				schemaDAOI.activateOrDeactivateFMValidations(finalFMAppList, "no");
			}
			response.put("status", "success");
			response.put("message", "File monitoring is deactivated.");
			LOG.info("File monitoring is deactivated.");
			LOG.info("dbconsole/deactivateFileMonitoring - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Failed to deactivate file monitoring.");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deactivateFileMonitoring - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/checkDuplicateSchemaConnection", method = RequestMethod.POST)
	public ResponseEntity<Object> isDuplicateConnection(@RequestHeader HttpHeaders headers,
											   @RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/checkDuplicateSchemaConnection - START");

		String schemaType = ""+ params.get("schemaType");
		String uri = ""+ params.get("uri");
		String database = ""+ params.get("database");
		String port = ""+ params.get("port");
		String serviceName = ""+ params.get("serviceName");
		String folderPath = ""+ params.get("folderPath");
		String bucketName = ""+ params.get("bucketName");
		String azureServiceURI = ""+ params.get("azureServiceURI");
		String azureFilePath = ""+ params.get("azureFilePath");
		Integer domainId = Integer.parseInt(""+params.get("domainId"));
		long projectId = Long.parseLong(""+params.get("projectId"));

		String status = "failed";
		String message = "";
		JSONObject response = new JSONObject();

		HttpStatus httpStatus = HttpStatus.OK;

		LOG.debug("schemaType=" + schemaType);
		try {
			String token ="";
			try {
				token = headers.get("token").get(0).toString();

				if (token != null && !token.isEmpty()) {

					LOG.debug("token " + token);
					if (csvService.validateUserToken(token).equalsIgnoreCase("success")) {

						if (schemaType.equalsIgnoreCase("oracle")) {
							port = port + "/" + serviceName;
						}

						if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
							uri = azureServiceURI;
						}

						ListDataSchema listDataSchema = new ListDataSchema();
						listDataSchema.setProjectId(projectId);
						listDataSchema.setDomainId(domainId);
						listDataSchema.setSchemaType(schemaType);
						listDataSchema.setIpAddress(uri);
						listDataSchema.setDatabaseSchema(database);
						listDataSchema.setPort(port);
						listDataSchema.setFolderPath(folderPath);
						listDataSchema.setBucketName(bucketName);

						JSONArray connectionDetails= schemaDAOI.checkDuplicateSchemaConnection(listDataSchema);

						if(connectionDetails!=null && connectionDetails.length() > 0){
								message ="Connection already exists";
								status="success";
								response.put("result", connectionDetails);
						}else{
							message="Connection does not exists";
						}
					}else {
						message = "Token is expired";
						httpStatus = HttpStatus.EXPECTATION_FAILED;
					}
				}else{
					httpStatus= HttpStatus.EXPECTATION_FAILED;
					message ="Invalid token or empty token";
				}
			}catch (Exception e){
				System.out.println(e.getLocalizedMessage());
				httpStatus= HttpStatus.EXPECTATION_FAILED;
				message ="Token is missing in the headers";
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		response.put("status", status);
		response.put("message", message);
		LOG.info("dbconsole/checkDuplicateSchemaConnection - END");
		return new ResponseEntity<Object>(response.toString(), httpStatus);
	}
}
