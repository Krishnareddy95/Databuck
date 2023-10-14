package com.databuck.restcontroller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DataTemplateCreateRequest;
import com.databuck.bean.Demo;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
import com.databuck.bean.GloabalRule;
import com.databuck.bean.HiveSource;
import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.bean.UserToken;
import com.databuck.bean.listDataAccess;
import com.databuck.constants.DatabuckConstants;
import com.databuck.controller.DataTemplateController;
import com.databuck.controller.JSONController;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ILoginDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.databasemigration.MigrationManagement;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.MsSqlActiveDirectoryConnection;
import com.databuck.datatemplate.OracleConnection;
import com.databuck.datatemplate.OracleRACConnection;
import com.databuck.datatemplate.PostgresConnection;
import com.databuck.datatemplate.TeradataConnection;
import com.databuck.datatemplate.VerticaConnection;
import com.databuck.dto.CreateDataConnectionResponse;
import com.databuck.dto.CreateTemplateResponse;
import com.databuck.dto.CreateValidationCheckResponse;
import com.databuck.dto.LoginRequest;
import com.databuck.dto.LoginResponse;
import com.databuck.security.LogonManager;
import com.databuck.service.AuthorizationService;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.IDataAlgorithService;
import com.databuck.service.IProjectService;
import com.databuck.service.ITaskService;
import com.databuck.service.IUserService;
import com.databuck.service.LoginService;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.DateUtility;
import com.databuck.util.ExportUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.TokenValidator;
import com.databuck.util.UserLDAPGroupHolder;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckRestController {

	@Autowired
	public LoginService loginService;
	@Autowired
	public IUserService iUserService;
	@Autowired
	public IProjectService projService;
	@Autowired
	public Properties licenseProperties;
	@Autowired
	public ExportUtility exportUtility;
	@Autowired
	private Properties activeDirectoryConnectionProperties;
	@Autowired
	private Properties appDbConnectionProperties;
	@Autowired
	private MigrationManagement migrationManage;
	@Autowired
	public ITaskDAO taskDao;
	@Autowired
	ILoginDAO iLoginDAO;
	@Autowired
	JSONController Jsoncontroller;
	
	@Autowired
	public SchemaDAOI SchemaDAOI;

	@Autowired
	public IProjectDAO iProjectDAO;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	OracleRACConnection OracleRACConnection;

	@Autowired
	TeradataConnection teradataConnection;
	
	@Autowired
	PostgresConnection postgresConnection;
	
	@Autowired
	CassandraConnection cassandraconnection;

	@Autowired
	HiveConnection hiveconnection;

	@Autowired
	DataTemplateController dataTemplateController;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;

	@Autowired
	IDataAlgorithService dataAlgorithService;
	
	@Autowired
	MSSQLConnection mSSQLConnection;
	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;
	@Autowired
	private IListDataSourceDAO listdatasourcedao;
	@Autowired
	IValidationCheckDAO validationcheckdao;	
	@Autowired
	private ITaskDAO iTaskDAO;
	@Autowired
	public ITaskService iTaskService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	private Properties clusterProperties;

	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private LogonManager logonManager;
	
	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;
	
	@Autowired
	private TokenValidator tokenValidator;
	
	@Autowired
	private ChecksCSVService csvService;
	
	@Autowired
	SchemaDAOI schemaDao;
	
	@Autowired
	DataProfilingTemplateService dataProilingTemplateService;
	
	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;
	
	private static final Logger LOG = Logger.getLogger(DatabuckRestController.class);
	
	

	@RequestMapping(value = "restapi/testjson", method = RequestMethod.POST, consumes = "application/json")
	public String getTableDataAsDynamic(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Demo person) {
		LOG.info("/restapi/testjson - START");
		try {
			LOG.debug("Authorization   " + request.getHeader("Authorization"));
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);

			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				
				LOG.info("person=" + person);
				// set HTTP code to "201 Created"
				return "RequestSuccess".toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("/restapi/testjson - END");
		return null;
	}

	@ApiOperation(value = "Create data connection", notes = "This API is used to create Data Connection in Databuck.", tags="Connection")
	@ResponseBody
	@RequestMapping(value = "restapi/createDataConnection", method = RequestMethod.POST,produces = "application/json")
	public CreateDataConnectionResponse createDataConnection(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestBody ListDataSchema listDataSchema) {			
		LOG.info("restapi/createDataConnection - START");
		CreateDataConnectionResponse json = new CreateDataConnectionResponse();
		long idUser = 1l;
		Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
		LOG.debug("token   " + request.getHeader("token") );
		String authorization = request.getHeader("token");
		LOG.debug("listDataSchema   " + listDataSchema );
		LOG.debug("session   " + session );
		boolean isUserValid = tokenValidator.isValid(authorization!=null?authorization:"");

			if (isUserValid) {
				
				String schemaName = listDataSchema.getSchemaName();
				String schemaType = listDataSchema.getSchemaType();
				String uri = listDataSchema.getIpAddress();
				String database = listDataSchema.getDatabaseSchema();
				String username = listDataSchema.getUsername();
				String password = listDataSchema.getPassword();
				String port = listDataSchema.getPort();
				String domain = listDataSchema.getDomain();
				String serviceName = listDataSchema.getServiceName();
				String krb5conf = listDataSchema.getKrb5conf();
				String autoGenerateId = "N";
				String suffix = "", prefix = "";
				String kmsAuthDisabled= listDataSchema.getKmsAuthDisabled();
				String readLatestPartition= listDataSchema.getReadLatestPartition();
				String alation_integration_enabled = listDataSchema.getAlation_integration_enabled();
				String httpPath= listDataSchema.getHttpPath();

				if (alation_integration_enabled == null || alation_integration_enabled.trim().isEmpty())
					alation_integration_enabled = "N";
				if (readLatestPartition==null || readLatestPartition.trim().isEmpty()
						|| !readLatestPartition.trim().equalsIgnoreCase("Y")
						|| !schemaType.equalsIgnoreCase("MapR Hive")){
					readLatestPartition="N";
				}

				boolean kmsStatus=false;
				CqlSession cassandraSession = null;

				// getting createdBy username from createdBy userId
				
				LOG.debug("idUser   " + idUser );
				
				String createdByName = userDAO.getUserNameByUserId(idUser);
				
				
				LOG.debug("createdByName   " + createdByName );
				
				// checkParameters
                if (schemaName == null || schemaName.isEmpty()) {
					json.setFail( "schemaName parameter is missing");
					LOG.error("schemaName parameter is missing" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				if (schemaType == null || schemaType.isEmpty()) {
					json.setFail( "schemaType parameter is missing");
					LOG.error("schemaType parameter is missing" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}

				if (database == null || database.isEmpty()) {
					json.setFail( "database parameter is missing");
					LOG.error("database parameter is missing" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}   

				/*
				 * When KMS Authentication is enabled, db user details comes from logon manager
				 */
				if (kmsAuthDisabled != null && kmsAuthDisabled.trim().equalsIgnoreCase("N")) {

					String kmsMsg="";

					// Check if the schema type is KMS enabled
					if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(schemaType)) {

						// Get the credentials from logon manager
						Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(schemaName);

						if(conn_user_details!=null && conn_user_details.size()>0){
							// Validate the logon manager response for key
							boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

							if (responseStatus) {
								uri = conn_user_details.get("hostname");
								port = (schemaType.equalsIgnoreCase("Oracle"))
										? conn_user_details.get("port") + "/" + serviceName
										: conn_user_details.get("port");
								username = conn_user_details.get("username");
								password = conn_user_details.get("password");

								kmsStatus=true;

							}else
								kmsMsg="Lognon Manager Credentials could not be found";
						}else
							kmsMsg="Invalid KMS Key";

					}else
						kmsMsg ="KMS Authentication is not supported for [" + schemaType + "] !!";

					if(!kmsStatus){
						json.setFail( kmsMsg);
						LOG.error(kmsMsg );
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}

				}else{
					if(!schemaType.equalsIgnoreCase("MapR Hive")) {
						
						if ((username == null || username.isEmpty())) {
							json.setFail( "username parameter is missing");
							LOG.error("username parameter is missing" );
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json;
						}
						if ((password == null || password.isEmpty())){
							json.setFail( "password parameter is missing");
							LOG.error("password parameter is missing" );
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json;
						}
						
					}

					if (uri == null || uri.isEmpty()) {
						json.setFail( "uri parameter is missing");
						LOG.error("uri parameter is missing" );
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}

					if (port == null || port.isEmpty()) {
						json.setFail( "port parameter is missing");
						LOG.error("port parameter is missing" );
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}
				}

				
				Long project_id = listDataSchema.getProjectId();
				if(project_id==null || project_id==0l){
					json.setFail( "Project Id is missing");
					LOG.error( "Project Id is missing" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				Integer domain_id= listDataSchema.getDomainId();
				if(domain_id==null || domain_id==0){
					json.setFail( "Domain Id is missing");
					LOG.error("Domain Id is missing" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				boolean isDomainIdValid= iProjectDAO.isDomainIdValid(domain_id);
				if(!isDomainIdValid){
					json.setFail( "Domain Id is invalid");
					LOG.error("Domain Id is invalid" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				boolean isProjectIdValid= iProjectDAO.isProjectIdValid(project_id);
				if(!isProjectIdValid){
					json.setFail( "Project Id is invalid");
					LOG.error("Project Id is invalid" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				boolean isProjectFromDomain = iProjectDAO.isProjectFromDomain(project_id,domain_id.longValue());
				if(!isProjectFromDomain){
					json.setFail( "Project with Id:["+project_id+"] does not belong to domain Id: ["+domain_id+"]");
					LOG.error("Project with Id:["+project_id+"] does not belong to domain Id: ["+domain_id+"]" );
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				if (schemaType.equalsIgnoreCase("oracle")) {
					port = port + "/" + serviceName;
				}
				Connection con = null;
				String name = SchemaDAOI.duplicateSchemaName(schemaName,project_id,domain_id);
				 LOG.info("duplicateatabasename=" + name);
				
				boolean flag = true;

				if (name == null) {

					if (schemaType.equalsIgnoreCase("oracle")) {

						if (serviceName == null) {
							json.setFail( "serviceName parameter is missing");
							LOG.error("serviceName parameter is missing" );
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json;
						}
						String url = "jdbc:oracle:thin:@" + uri + ":" + port;
						try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
							con = DriverManager.getConnection(url, username, password);

						} catch (Exception e) {
							flag = false;
							
							LOG.info("con=" + con );
							try {
								json.setFail( "Data Connection failed,Please check Configuration");
								LOG.error("Data Connection failed,Please check Configuration" );
								response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
								return json;
							} catch (Exception e1) {
								e1.printStackTrace();
								LOG.error("Exception  "+e1.getMessage());
							}
							e.printStackTrace();
							LOG.error("Exception  "+e.getMessage());
							
						}

					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("Oracle RAC")) {

							if (serviceName == null) {
								json.setFail( "serviceName parameter is missing");
								LOG.error("serviceName parameter is missing" );
								return json;
							}
							String url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)" + "(HOST = "
									+ uri + ")(PORT = " + port + "))(CONNECT_DATA =(SERVER = DEDICATED)"
									+ "(SERVICE_NAME = " + serviceName + ")))";
							try {
								Class.forName("oracle.jdbc.driver.OracleDriver");
								con = DriverManager.getConnection(url, username, password);

							} catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration" );
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									e1.printStackTrace();
									LOG.error("Exception  "+e1.getMessage());
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());
							}

						}
					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("MSSQL")) {

							String url = "jdbc:sqlserver://" + uri + ":" + port+ ";encrypt=true;trustServerCertificate=true;";
							try {
								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								con = DriverManager.getConnection(url, username, password);
							} catch (Exception e) {
								flag = false;
								LOG.error("Exception  "+e.getMessage());
								e.printStackTrace();
							}

						}
					}
					if (flag) { 
						if (schemaType.equalsIgnoreCase("SnowFlake")) {
							System.out.println("database ====>" + database);
							System.out.println("uri ==>" + uri);
				
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
							try {
								Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
								con = DriverManager.getConnection(url, username, password);
							} catch (Exception e) {
								flag = false;
								LOG.error("Exception  "+e.getMessage());
								e.printStackTrace();
							}

						}
					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("Postgres")) {

							String[] dbAndSchema = database.split(",");
							
							String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0];
							if(dbAndSchema.length > 1 && dbAndSchema[1].length() > 0 ){
								url = url+"?currentSchema="+dbAndSchema[1]+"&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
							}else{			
								url = url + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
							}
							try {
								Class.forName("org.postgresql.Driver");
								con = DriverManager.getConnection(url, username, password);
							} catch (Exception e) {
								flag = false;
								LOG.error("Exception  "+e.getMessage());
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
							}

						}
					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("MSSQLActiveDirectory")) {

							try {
								Class.forName("net.sourceforge.jtds.jdbc.Driver");
								String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim()
										+ ";domain=" + domain.trim();
								con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
							} catch (Exception e) {
								LOG.error("Exception  "+e.getMessage());
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();

							}
						}
					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("Vertica")) {

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
								LOG.error("Exception  "+e.getMessage());
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();	
								LOG.error("Exception  "+e.getMessage());
							}
						}
					}

					if (flag) {
						if (schemaType.equalsIgnoreCase("Hive")) {

							try {
								String dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database;
								Class.forName("org.apache.hive.jdbc.HiveDriver");
								con = DriverManager.getConnection(dbURL2, username, password);
							} catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());

							}
						}
					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("Amazon Redshift")) {

							try {
								Class.forName("com.amazon.redshift.jdbc42.Driver");
								Properties props = new Properties();
								props.setProperty("user", username);
								props.setProperty("password", password);
								con = DriverManager
										.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database, props);
							} catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());

							}
						}
					}

					if (flag) {
						if (schemaType.equalsIgnoreCase("Cassandra")) {

							try {
								CqlSessionBuilder builder = CqlSession.builder();
						        builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port))).withAuthCredentials(username, password);

						        cassandraSession = builder.build();
								cassandraSession.close();
							} catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());
							}
						}
					}
					
					//getting createdByUser 
					String createdByUser = userDAO.getUserNameByUserId(idUser);
				
					LOG.info("Created by in DatabuckRestController==>"+createdByUser);
					if (flag) {
						if (schemaType.equalsIgnoreCase("Hive Kerberos")) {

							try {
								Long projectId= listDataSchema.getProjectId();
								Integer domainId = listDataSchema.getDomainId();
								Long idDataSchema = SchemaDAOI.saveDataIntoListDataSchema(uri, database, username,
										password, port, schemaName, schemaType, domain, serviceName, krb5conf,
										autoGenerateId, suffix, prefix,"","","","","","","","","","","","","","","",projectId,createdByUser,"","","",
										"","","","","","","","","","","","N","N","N",0,0,2,"N",domainId,"N","","","","","","","Y",readLatestPartition,alation_integration_enabled,"N","cluster","N","N",httpPath,"","");
								LOG.debug("java -jar " + System.getenv("DATABUCK_HOME")
								+ "/hive-kerberos-con-autodt.jar " + idDataSchema);
								Process proc = Runtime.getRuntime().exec("java -jar " + System.getenv("DATABUCK_HOME")
								+ "/hive-kerberos-con-autodt.jar " + idDataSchema);

								proc.waitFor(); // Then retreive the process
												// output
								InputStream in = proc.getInputStream();
								InputStream err = proc.getErrorStream();

								byte b[] = new byte[in.available()];
								in.read(b, 0, b.length);
								
								LOG.debug("new String(b)" +new String(b));

								byte c[] = new byte[err.available()];
								err.read(c, 0, c.length);
								
								LOG.debug("new String(c)" +new String(c));

								String status = SchemaDAOI.getStatusForHiveKerberos(idDataSchema);
								if (status.equalsIgnoreCase("success")) {
									json.setIdDataSchema( idDataSchema);
									LOG.info("Data Connection Created Successfully");
									json.setSuccess( "Data Connection Created Successfully");
									return json;
								} else {
									SchemaDAOI.deleteEntryFromListDataSchemaAndHiveSource(idDataSchema);
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								}
							} catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());
							}
						}
					}

					if (flag) {
						if (schemaType.equalsIgnoreCase("MapR Hive")) {

							try {
								Long projectId= listDataSchema.getProjectId();
								Integer domainId = listDataSchema.getDomainId();
								String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
								ProcessBuilder processBuilder = new ProcessBuilder();

								String databuckHome = DatabuckUtility.getDatabuckHome();

								String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
										+ " com.databuck.mapr.hive.ValidateHiveConnection " + uri + ":" + port + " " + database;
								
								LOG.info(" shellCommand - " + shellCommand);
								processBuilder.command("bash", "-c", shellCommand);

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

								
								LOG.info("output "+output);

								if (exitVal == 0 && successfulConnection) {
									LOG.info("Success!");
									Long idDataSchema = SchemaDAOI.saveDataIntoListDataSchema(uri, database, username,
											password, port, schemaName, schemaType, domain, serviceName, krb5conf,
											autoGenerateId, suffix, prefix,"","","","","","","","","","","","","","","",projectId,createdByUser,"","","",
											"","","","","","","","","","","","N","N","N",0,0,2,"N",domainId,"N","","","","","","","Y",readLatestPartition,alation_integration_enabled,"N","cluster","N","N",httpPath,"","");
									
									LOG.info("idDataSchema=" + idDataSchema);
                                    json.setIdDataSchema( idDataSchema);
									json.setSuccess( "Data Connection Created Successfully");
									LOG.info("Data Connection Created Successfully" );

									// System.exit(0);
								} else {
									// abnormal...
									
									LOG.error("Connection Failure!");
									flag = false;
									try {
										json.setFail( "Data Connection failed,Please check Configuration");
										response.getWriter().println(json);
									} catch (Exception e1) {
										LOG.error("Exception  "+e1.getMessage());
										e1.printStackTrace();
									}
								}
							}catch (Exception e) {
								flag = false;
								try {
									json.setFail( "Data Connection failed,Please check Configuration");
									LOG.error("Data Connection failed,Please check Configuration");
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									return json;
								} catch (Exception e1) {
									LOG.error("Exception  "+e1.getMessage());
									e1.printStackTrace();
								}
								e.printStackTrace();
								LOG.error("Exception  "+e.getMessage());
							}
							return json;
						}
					}

					if (con != null || cassandraSession != null ) {
						try {
							// Closing the connection
							if(con != null) {
								con.close();
							}

							if(cassandraSession != null) {
								cassandraSession.close();
							}

							if (flag) {

								// if kms is enabled, need to insert following fields as empty
								if(kmsStatus){
									uri="";
									port="";
									username="";
									password="";
									kmsAuthDisabled="N";
								}else
									kmsAuthDisabled="Y";

								Long projectId= listDataSchema.getProjectId();
								Integer domainId = listDataSchema.getDomainId();
								Long idDataSchema = SchemaDAOI.saveDataIntoListDataSchema(uri, database, username, password,
										port, schemaName, schemaType, domain, serviceName, krb5conf, autoGenerateId, suffix,
										prefix,"","","","","","","","","","","","","","","",projectId,createdByUser,"","","",
										"","","","","","","","","","","","N","N","N",0,0,2,"N",domainId,"N","","","","","","",kmsAuthDisabled,"N",alation_integration_enabled,"N","cluster","N","N",httpPath,"","");

								
								LOG.error("idDataSchema=" + idDataSchema);
								try {
									json.setIdDataSchema( idDataSchema);
									json.setSuccess( "Data Connection Created Successfully");
									LOG.info("Data Connection Created Successfully" );
									return json;
								} catch (Exception e) {
									LOG.error("Exception  "+e.getMessage());
									e.printStackTrace();
								}
							}

							LOG.info("Connection Established Successfully" );
							response.getWriter().println("Connection Established Successfully");
						} catch (Exception e) {
							LOG.error("Exception  "+e.getMessage());
							e.printStackTrace();
						}
					}else{
						try {
							LOG.error("Data Connection failed,Please check Configuration");
							json.setFail( "Data Connection failed,Please check Configuration");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json;
						} catch (Exception e1) {
							LOG.error("Exception  "+e1.getMessage());
							e1.printStackTrace();
						}
					}

				} else {
					json.setFail( "Schema Name already exists");
					LOG.error("Schema Name already exists");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
			} else {
				json.setFail( "Invalid Authorization");
				LOG.error("Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return json;
			}
		json.setFail( "Data Connection failed,Please check Configuration");
		LOG.error("Data Connection failed,Please check Configuration");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		LOG.info("restapi/createDataConnection - END");
		return json;
		
		
	}
	
	

	@ApiOperation(value = "Create data template", notes = "This API is used to create Data Template in Databuck ", tags="Template")	@ResponseBody
	@RequestMapping(value = "restapi/createDataTemplate", method = RequestMethod.POST,produces = "application/json")
	public CreateTemplateResponse createDataTemplateAA(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestBody DataTemplateCreateRequest dataTemplateCreateRequest) {
		LOG.info("restapi/createDataConnection - START");
		CreateTemplateResponse json = new CreateTemplateResponse();

		try{
			LOG.debug("token   " + request.getHeader("token") );
			String authorization = request.getHeader("token");
			
			LOG.debug("Getting  parameters for dataTemplateBean , " + dataTemplateCreateRequest);
			boolean isUserValid = tokenValidator.isValid(authorization!=null?authorization:"");
			if(isUserValid){

				if (dataTemplateCreateRequest.getIdUser() == null) {
					throw new Exception("IdUser is empty in the request");
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
					//response.put("status", "failed");
						json.setFail( "Failed to create data template. Template name is already in use.");						
						json.setSuccess(null);
						json.setIdData(null);
						json.setIsUpdate(null);
						
					LOG.error("This Data Template name is in use. Please choose another name : "+dataTemplateCreateRequest.getDataTemplateName());
					return json;
					//return new ResponseEntity<Object>(response, HttpStatus.OK);
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
				if(schema!=null || !schema.trim().equals("")) {
					try {
						long idDataSchema=Long.parseLong(schema);
						List<ListDataSchema> listDataSchema = SchemaDAOI.readdatafromlistdataschema(idDataSchema);
						ListDataSchema ListDataSchemaobj = (ListDataSchema) listDataSchema.get(0);
						if(ListDataSchemaobj.getAction().equalsIgnoreCase("No")) {
							json.setFail( "Data Connection is not active.");						
							json.setSuccess(null);
							json.setIdData(null);
							json.setIsUpdate(null);	
							return json;
						}
						
					} catch (Exception e) {
						json.setFail( "Invalid idDataSchema ... "+e.getMessage());						
						json.setSuccess(null);
						json.setIdData(null);
						json.setIsUpdate(null);						
						e.printStackTrace();
						return json;
					}
				}
				

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
							sliceEnd, idUser, HostURI, folder, dataFormat, userlogin, password, schemaName, null,
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
							password, schemaName, null, tar_brokerUri, tar_topicName, profilingEnabled,
							selected_list, projectId, advancedRulesEnabled, createdByUser, rollingHeaderPresent,
							rollingColumn, historicDateTable, null, null,domainId);

					Long idData = 0l;
					try {
						idData = result.get();
						
						
						json.setSuccess("Validation Template Created Successfully");
						json.setIdData(idData);
						json.setIsUpdate("N");
						json.setFail(null);
						
						
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

						//response.put("templateId", String.valueOf(idData));
						//response.put("uniqueId", uniqueId);
						
						//changes regarding Audit trail
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(idUser,userDAO.getUserNameByUserId(Long.valueOf(idUser)),DatabuckConstants.DBK_FEATURE_TEMPLATE,formatter.format(new Date()),idData,DatabuckConstants.ACTIVITY_TYPE_CREATED,DataTemplateName);

					} else {
					
						json.setFail("Validation Template not Created Successfully");
						json.setSuccess(null);
						json.setIdData(null);
						json.setIsUpdate(null);
						
						LOG.error(message);
					}
				}

				
				
			
				
				
			} else {
				LOG.error("Invalid Authorization");
				json.setFail( "Invalid Authorization");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		    }
        }catch (Exception e){
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.setFail( "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		
		LOG.info("restapi/createDataConnection - END");
		return json;

	}
	
	
	
	@RequestMapping(value = "restapi/createAndRunValidation", method = RequestMethod.POST)
	public String createAndRunValidation(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestBody DataTemplateBean dataTemplateBean) {
		LOG.info("restapi/createAndRunValidation - START");
		JSONObject json = new JSONObject();
		
		try {
			LOG.info("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			if(isUserValid){
				String dataTemplateName = dataTemplateBean.getDataTemplateName();
				String dataLocation = dataTemplateBean.getDataLocation();
				String description = dataTemplateBean.getDescription();
				Long idDataSchema = dataTemplateBean.getIdDataSchema();
				String tableName = dataTemplateBean.getTableName();
				String autoDT = dataTemplateBean.getAuto();
				//filesys
				String dataFormat = dataTemplateBean.getDataFormat();
				String hostURI = dataTemplateBean.getHostURI();
				String folder = dataTemplateBean.getFolder();
				String userLogin = dataTemplateBean.getUserLogin();
				String password = dataTemplateBean.getPassword();
				Long projectId= dataTemplateBean.getProjectId();
				Integer domainId = dataTemplateBean.getDomainId();
				
				LOG.info("Columns=" + dataTemplateBean.getColumns());
				
				
				List<ListDataDefinitionBean> metaData = dataTemplateBean.getColumns();
				if (dataTemplateName == null) {
					LOG.error("dataTemplateName parameter is missing");
					json.put("fail", "dataTemplateName parameter is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json.toString();
				}
				if (dataLocation == null) {
					LOG.error("dataLocation parameter is missing");
					json.put("fail", "dataLocation parameter is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json.toString();
				}
				if(dataLocation.equalsIgnoreCase("S3") && !(dataFormat.equalsIgnoreCase("parquet"))){
					hostURI = dataTemplateBean.getBucketName();
					folder = dataTemplateBean.getKey();
					userLogin = dataTemplateBean.getAccessKey();
					password = dataTemplateBean.getSecretKey();
				}
				if (dataLocation.equals("File System") || dataLocation.equals("HDFS") || dataLocation.equals("S3")
						|| dataLocation.equals("MapR FS") || dataLocation.equals("MapR DB")) {
					if(dataLocation.equals("S3")){
						if (hostURI == null) {
							LOG.error("bucketName parameter is missing");
							json.put("fail", "bucketName parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (folder == null) {
							LOG.error("key parameter is missing");
							json.put("fail", "key parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (userLogin == null) {
							LOG.error("accessKey parameter is missing");
							json.put("fail", "accessKey parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (password == null) {
							LOG.error("secretKey parameter is missing");
							json.put("fail", "secretKey parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
					}else{
						if (dataFormat == null) {
							LOG.error("dataFormat parameter is missing");
							json.put("fail", "dataFormat parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (hostURI == null) {
							LOG.error("hostURI parameter is missing");
							json.put("fail", "hostURI parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (folder == null) {
							LOG.error("folder parameter is missing");
							json.put("fail", "folder parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (userLogin == null) {
							LOG.error("userLogin parameter is missing");
							json.put("fail", "userLogin parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
						if (password == null) {
							LOG.error("password parameter is missing");
							json.put("fail", "password parameter is missing");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return json.toString();
						}
					}
				} else {
					if (idDataSchema == 0) {
						LOG.error("idDataSchema parameter is missing");
						json.put("fail", "idDataSchema parameter is missing");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json.toString();
					}

					if (tableName == null) {
						LOG.error("tableName parameter is missing");
						json.put("fail", "tableName parameter is missing");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json.toString();
					}
				}
				try {
					ModelAndView model = new ModelAndView();
					if (!autoDT.equalsIgnoreCase("N")) {
						//boolean isTheFileProcessed = validationcheckdao.checkIfTheFileIsProcessed(hostURI+folder);
						boolean isTheFileProcessed = false;
						if(!isTheFileProcessed){
							Long idData = 0L;
							String isRawData = "N";
                            CreateTemplateResponse message = createTemplate(dataTemplateName, dataLocation, description, idDataSchema, tableName,
									req, response, session, metaData,dataFormat,hostURI,folder,userLogin,password,isRawData, projectId, domainId);
							Map<String, List<ListDataDefinition>> mapDataDefinition = (Map) session.getAttribute("dataDefinition");
							List<ListDataDefinition> lstDataDefinition = null;
							if(dataFormat.equals("parquet")){
								lstDataDefinition = (List) mapDataDefinition.get(folder);
							}else{
								lstDataDefinition = (List) mapDataDefinition.get(tableName);
							}

							JSONObject jsonTemplateInfo = new JSONObject(message);
							idData = jsonTemplateInfo.getLong("idData");
							String isUpdate = jsonTemplateInfo.getString("isUpdate");

							String validationCheckName = dataTemplateName + "_" + folder + "_Validation";
							Long idApp = 0L;
							JSONObject jsonResponse = null;

							if(isUpdate.equalsIgnoreCase("N")){
								idApp = dataTemplateController.createValidationCheck(session,validationCheckName, idData, 1L, lstDataDefinition,projectId);
								
								// Create rule catalog
								boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
								if (isRuleCatalogDiscovery)
									ruleCatalogService.createRuleCatalog(idApp);
								
							}else{
								idApp = validationcheckdao.getListApplicationsFromIdData(idData);
								
								// update rule catalog
								boolean isRuleCatalogDiscovery = ruleCatalogService.isRuleCatalogEnabled();
								if (isRuleCatalogDiscovery)
									ruleCatalogService.updateRuleCatalog(idApp);
								
							}
							
							// Get deployMode
							String deployMode = clusterProperties.getProperty("deploymode");

							if(deployMode.trim().equalsIgnoreCase("2")) {
								deployMode = "local";
							} else {
								deployMode = "cluster";
							}

							String uniqueId = iTaskDAO.insertRunScheduledTask(idApp, "started", deployMode, null, null);

							session.setAttribute("scheduledTask", "yes");
							session.removeAttribute("scheduledTask");

							jsonResponse = new JSONObject();
							jsonResponse.put("success", "Data Template and Validation Check Created/Updated Successfully. Results generated");
							jsonResponse.put("idData", idData);
							jsonResponse.put("idApp", idApp);
							jsonResponse.put("uniqueId", uniqueId);
							return jsonResponse.toString();
						}else{
							JSONObject jsonResponse = new JSONObject();
							jsonResponse.put("success", "This file"+hostURI+folder+" is already processed.");
							return jsonResponse.toString();
						}
					}
				} catch (Exception e) {
					LOG.error("Exception  "+e.getMessage());
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("failure", "Error while creating/running validation check. Please check the logs.");
					e.printStackTrace();
					return jsonResponse.toString();
				}
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		
		LOG.info("restapi/createAndRunValidation - END");
		return json.toString();
	}	
	


	private CreateTemplateResponse createTemplate(String dataTemplateName, String dataLocation, String description, Long idDataSchema,
			String tableName, HttpServletRequest req, HttpServletResponse response, HttpSession session,
			List<ListDataDefinitionBean> metaData, String dataFormat, String hostURI2, String folder, String userLogin2, 
			String password2, String isRawData, Long projectId, Integer domainId) {
		LOG.info("createTemplate - START");
		Map<String, List<ListDataDefinition>> mapDataDefinition = (Map) session.getAttribute("dataDefinition");
		CreateTemplateResponse json = new CreateTemplateResponse();		
		String HostURI = "";
		String databaseSchema = "";
		String userlogin = "";
		String password = "";
		String portName = "";
		String domain = "";
		String sslEnb="N";

		List<ListDataSchema> listDataSchema = null;
		
		if(idDataSchema != 0){
			listDataSchema = DataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			
			HostURI = listDataSchema.get(0).getIpAddress();
			databaseSchema = listDataSchema.get(0).getDatabaseSchema();
			userlogin = listDataSchema.get(0).getUsername();
			password = listDataSchema.get(0).getPassword();
			portName = listDataSchema.get(0).getPort();
			domain = listDataSchema.get(0).getDomain();
			sslEnb = listDataSchema.get(0).getSslEnb();
			if (sslEnb == null || !sslEnb.trim().equalsIgnoreCase("Y"))
				sslEnb = "N";
		}

		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(dataLocation);
		listDataSource.setDataSource("SQL");
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(1l);
		listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
		listDataSource.setGarbageRows(0l);
		listDataSource.setName(dataTemplateName + "_" + tableName);
		listDataSource.setTableName(tableName);
		listDataSource.setDomain(domainId);
		listDataAccess listdataAccess = new listDataAccess();
		listdataAccess.setHostName(HostURI);
		listdataAccess.setPortName(portName);
		listdataAccess.setUserName(userlogin);
		listdataAccess.setPwd(password);
		listdataAccess.setSchemaName(databaseSchema);
		listdataAccess.setQueryString("");
		listdataAccess.setHistoricDateTable("");
		listdataAccess.setFolderName(tableName);
		listdataAccess.setIdDataSchema(idDataSchema);
		listdataAccess.setWhereCondition("");
		listdataAccess.setDomain(domain);
		listdataAccess.setQuery("");
		listdataAccess.setIncrementalType("");
		listdataAccess.setDateFormat("");
		listdataAccess.setSliceStart("");
		listdataAccess.setSliceEnd("");
		listdataAccess.setFileHeader("");
		listdataAccess.setFolderName(tableName);
		String queryTextbox = ""; 
		if(isRawData != null){
			listdataAccess.setIsRawData(isRawData);
		}
		listDataSource.setName(dataTemplateName + "_" + tableName);
		if (dataLocation.equals("MSSQLActiveDirectory")) {
			Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, databaseSchema, userlogin,
					password, tableName, portName, domain,queryTextbox);
			LinkedHashMap readTablesFromMSSQLActiveDirectory = (LinkedHashMap) arr[0];
			List<String> primarykeyCols = (ArrayList<String>) arr[1];
            if (readTablesFromMSSQLActiveDirectory.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}
			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource,
					readTablesFromMSSQLActiveDirectory, listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);
			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;
		} else if (dataLocation.equals("Vertica")) {

			Object[] obj = verticaconnection.verticaconnection(HostURI, databaseSchema, userlogin, password, tableName,
					portName,queryTextbox);
			LinkedHashMap readTablesFromVertica = (LinkedHashMap) obj[0];
			List<String> primarykeyCols = (List<String>) obj[1];
			if (readTablesFromVertica.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}
			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromVertica,
					listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);
			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;

		} else if (dataLocation.equals("MSSQL")) {

			Object[] arr = mSSQLConnection.readTablesFromMSSQL(HostURI, databaseSchema, userlogin, password, tableName,
					queryTextbox, portName);
			LinkedHashMap readTablesFromMYSQL = (LinkedHashMap) arr[0];
			List<String> primarykeyCols = (List<String>) arr[1];
			if (readTablesFromMYSQL.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}

			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromMYSQL,
					listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);
			json.setIdData( idData);
			json.setIsUpdate( "N");
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;

		}  else if (dataLocation.equals("Teradata")) {
			tableName = tableName.replace("\"", "");
			listdataAccess.setFolderName(tableName);
			listDataSource.setName(dataTemplateName + "_" + tableName);

			Map readTablesFromTeradata = teradataConnection.readTablesFromTeradata(HostURI, databaseSchema, userlogin, password,
					tableName, "", portName);
			List<String> primaryKeyCols = new ArrayList();
			if (readTablesFromTeradata.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				if(mapDataDefinition!=null) {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  "+e.getMessage());
			}
			long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromTeradata,
					listdataAccess, primaryKeyCols, lstDataDefinition, null,projectId);
			json.setIdData( idData);
			json.setIsUpdate( "N");
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;
			
		 }  else if (dataLocation.equals("Postgres")) {

			Map readTablesFromPostgres = postgresConnection.readTablesFromPostgres(HostURI, databaseSchema, userlogin, password, tableName, "", portName, sslEnb);
			List<String> primaryKeyCols = new ArrayList();
			if (readTablesFromPostgres.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				if(mapDataDefinition!=null) {
					lstDataDefinition = (List) mapDataDefinition.get(tableName);
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}

			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromPostgres,
					listdataAccess, primaryKeyCols, lstDataDefinition, null,projectId);
			json.setIdData( idData);
			json.setIsUpdate( "N");
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;

		} else if (dataLocation.equals("Cassandra")) {

			Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, databaseSchema, userlogin, password,
					tableName, portName);
			Map<String, String> readTablesFromCassandra = (Map<String, String>) arr[0];
			List<String> primarykeyCols = (List<String>) arr[1];
			if (readTablesFromCassandra.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}

			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromCassandra,
					listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);

			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;

		} else if (dataLocation.equals("Oracle")) {

			Map readTablesFromOracle = oracleconnection.readTablesFromOracle(HostURI, databaseSchema, userlogin,
					password, tableName, null, portName);
			if (readTablesFromOracle.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<String> primaryKeyCols = oracleconnection.readPrimaryKeyColumnsFromOracle(HostURI, databaseSchema,
					userlogin, password, tableName, portName);

			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}
			
			JSONObject jsonObj = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, readTablesFromOracle,
					listdataAccess, primaryKeyCols, lstDataDefinition,metaData,req,projectId);			

			json.setIdData( jsonObj.getLong("idData"));
			json.setIsUpdate( "N");
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;
		}

		else if (dataLocation.equals("Oracle RAC")) {
			String serviceName = ((ListDataSchema) listDataSchema.get(0)).getKeytab();
			Map readTablesFromOracleRAC = OracleRACConnection.readTablesFromOracleRAC(HostURI, databaseSchema,
					userlogin, password, tableName, portName, serviceName,"");
			if (readTablesFromOracleRAC.isEmpty()) {
				json.setFail( "Data Template failed,Please check Configuration");
				LOG.error("Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<String> primaryKeyCols = OracleRACConnection.readPrimaryKeyColumnsFromOracleRAC(HostURI,
					databaseSchema, userlogin, password, tableName, portName, serviceName);

			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}

			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromOracleRAC,
					listdataAccess, primaryKeyCols, lstDataDefinition, null,projectId);

			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;
		}

		else if (dataLocation.equals("Hive")) {
			LOG.info(" Hive ");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			hivesource.setTableName(tableName);
			int insertDataIntoHiveSource = DataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			Map readTablesFromHive = hiveconnection.readTablesFromHive(dataLocation, HostURI, databaseSchema, userlogin, password,
					tableName, portName, "N", "", "",false,"","","",false,"","","","");
			if (readTablesFromHive.isEmpty()) {
				LOG.error("Data Template failed,Please check Configuration");
				json.setFail( "Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<String> primarykeyCols = new ArrayList<String>();
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}
			LOG.debug("tableName=" + tableName);
			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromHive,
					listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);

			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			return json;
		} else if (dataLocation.equalsIgnoreCase("Amazon Redshift")) {
			LOG.info("Amazon Redshift ");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			hivesource.setTableName(tableName);
			int insertDataIntoHiveSource = DataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			Map readTablesFromAmazon = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI, databaseSchema,
					userlogin, password, tableName, portName, "");

			if (readTablesFromAmazon.isEmpty()) {
				LOG.error("Data Template failed,Please check Configuration");
				json.setFail( "Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			List<String> primarykeyCols = new ArrayList();
			List<ListDataDefinition> lstDataDefinition = null;
			try {
				lstDataDefinition = (List) mapDataDefinition.get(tableName);
			} catch (Exception e) {
				// TODO: handle exception
				LOG.error("Exception  "+e.getMessage());
			}
			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromAmazon,
					listdataAccess, primarykeyCols, lstDataDefinition, null,projectId);

			json.setIdData( idData);
			LOG.info("Data Template Created Successfully");
			json.setSuccess( "Data Template Created Successfully");
			return json;
		}
		String principle;
		String keytab;
		String krb5conf;
		if (dataLocation.equalsIgnoreCase("Hive Kerberos")) {
			LOG.info("Hive Kerberos");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			hivesource.setTableName(tableName);
			Map readTablesFromHive = new HashMap();
			List<String> primarykeyCols = new ArrayList<String>();

			Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, readTablesFromHive,
					listdataAccess, primarykeyCols, null, null,projectId);
			
			LOG.info("idData=" + idData);
			try {
				
				LOG.debug("java -jar " + System.getenv("DATABUCK_HOME") + "/hive-kerberos-dt.jar " + idData);
				Process proc = Runtime.getRuntime()
						.exec("java -jar " + System.getenv("DATABUCK_HOME") + "/hive-kerberos-dt.jar " + idData);
				proc.waitFor();

				InputStream in = proc.getInputStream();
				InputStream err = proc.getErrorStream();

				byte[] b = new byte[in.available()];
				in.read(b, 0, b.length);
				
				LOG.debug("new String(b)" +new String(b));

				byte[] c = new byte[err.available()];
				err.read(c, 0, c.length);
				
				LOG.debug("new String(c)" +new String(c));

			}
			catch (Exception e) {
				
				LOG.error("Data Template failed,Please check Configuration");
				LOG.error("Exception  "+e.getMessage());
				json.setFail( "Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			}
			json.setIdData( idData);
			json.setSuccess( "Data Template Created Successfully");
			LOG.info("Data Template Created Successfully");
			
			return json;
		} else if (dataLocation.equals("File System") || dataLocation.equals("HDFS") || dataLocation.equals("S3")
				|| dataLocation.equals("MapR FS") || dataLocation.equals("MapR DB")) {
			List<ListDataDefinition> listDataDefinition = null;
			listDataSource.setName(dataTemplateName);
			listDataSource.setDescription(description);
			if (dataLocation.equals("File System"))
				listDataSource.setDataLocation("FILESYSTEM");
			else
				listDataSource.setDataLocation(dataLocation);
			listDataSource.setDataSource(dataFormat);
			listDataSource.setCreatedAt(new Date());
			listDataSource.setCreatedBy(1l);
			listDataSource.setIdDataSchema(0l);
			listDataSource.setGarbageRows(0l);

			listdataAccess.setHostName(hostURI2);
			listdataAccess.setPortName("");
			listdataAccess.setUserName(userLogin2);
			listdataAccess.setPwd(password2);
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			listdataAccess.setFileHeader("");
			
			List<String> datatypes = new ArrayList<String>();
			datatypes.add("int");
			datatypes.add("char");
			datatypes.add("long");
			datatypes.add("float");
			datatypes.add("double");
			datatypes.add("varchar");
			datatypes.add("text");
			datatypes.add("string");
			datatypes.add("date");
			datatypes.add("number");
			Map<String, String> hm = new LinkedHashMap<String, String>();
			List<String> primarykeyCols = new ArrayList<String>();	
			if(metaData == null){
				metaData = new ArrayList<ListDataDefinitionBean>();
			}
			if(!dataFormat.equals("parquet")){
				for (ListDataDefinitionBean ldd : metaData) {					
					hm.put(ldd.getColumnName(), ldd.getColumnType());
				}
			}else{	
				listDataDefinition = (List) mapDataDefinition.get(folder);
				for (ListDataDefinition ldd : listDataDefinition) {					
					hm.put(ldd.getColumnName(), ldd.getFormat());
					ListDataDefinitionBean lddBean = new ListDataDefinitionBean();
					lddBean.setColumnName(ldd.getColumnName());
					lddBean.setColumnType(ldd.getFormat());
					metaData.add(lddBean);					
				}
			}
			
			if (hm.isEmpty()) {
				LOG.error("Data Template failed,Please check Configuration");
				json.setFail( "Data Template failed,Please check Configuration");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return json;
			} else {
				Long idData = null;				
				JSONObject jsonObj = DataTemplateAddNewDAO.createTemplateForRestAPI(listDataSource, hm,
						listdataAccess, primarykeyCols, listDataDefinition,metaData, req,projectId);
				
				/*Long idData = DataTemplateAddNewDAO.addintolistdatasource(listDataSource, hm, listdataAccess,
						primarykeyCols, null);*/
				json.setIdData( jsonObj.getLong("idData"));
				json.setIsUpdate( jsonObj.getString("isUpdate"));
				json.setSuccess("Data Template Created Successfully");
				LOG.info("Data Template Created Successfully");
				return json;
			}
		}
		LOG.error("Data Template failed,Please check Configuration");
		json.setFail( "Data Template failed,Please check Configuration");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		LOG.info("createTemplate - END");
		return json;
	}


	@ResponseBody
	@RequestMapping(value = "restapi/viewAllDataConnections", method = RequestMethod.GET,produces = "application/json")
																						
	public String viewAllDataConnections(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {
		LOG.info("restapi/viewAllDataConnections - START");
		JSONObject json = new JSONObject();
		Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
		String authorization = request.getHeader("Authorization");
		
		LOG.debug("authorization=" + authorization);
		if (authorization != null && authorization.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
			// credentials = username:password
			final String[] values = credentials.split(":", 2);
			for (int i = 0; i < values.length; i++) {
				LOG.debug("values=" + values[i]);
			}
		if (values[0].equals(secureAPIMap.get("accessTokenId"))
				&& values[1].equals(secureAPIMap.get("secretAccessToken"))) {
			Long projectId= (Long)session.getAttribute("projectId");
			List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId,projList,"","");
		JSONArray jsonArray = new JSONArray();
		JSONObject mainObj = new JSONObject();
		for (ListDataSchema lds : listdataschema) {
			JSONObject obj = new JSONObject();
			obj.put("idDataSchema", lds.getIdDataSchema());
			obj.put("schemaName", lds.getSchemaName());
			obj.put("schemaType", lds.getSchemaType());
			obj.put("ipAddress", lds.getIpAddress());
			obj.put("databaseSchema", lds.getDatabaseSchema());
			obj.put("port", lds.getPort());
			if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle")
					|| lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle RAC")) {
				obj.put("serviceName", lds.getKeytab());
			}
			if (lds.getSchemaType().toString().trim().equalsIgnoreCase("MSSQLActiveDirectory")) {
				obj.put("domain", lds.getDomain());
			}
			if (lds.getSchemaType().toString().trim().equalsIgnoreCase("Hive Kerberos")) {
				obj.put("principal", lds.getDomain());
				obj.put("gss_jaas", lds.getKeytab());
				obj.put("krb5conf", lds.getKrb5conf());
			}
			jsonArray.put(obj);
		}
		LOG.debug("All data Connections"+ jsonArray);
		mainObj.put("All data Connections", jsonArray);
		return mainObj.toString();
		} else {
			LOG.error("Invalid Authorization  ");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return json.toString();
		}
		}
		LOG.error("Invalid Authorization  ");
		json.put("fail", "Invalid Authorization");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		LOG.info("restapi/viewAllDataConnections - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/viewIdDataConnections", method = RequestMethod.GET)	
	public String viewIdDataConnections(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session,long idDataSchema) {
		LOG.info("restapi/viewIdDataConnections - START");
		JSONObject json = new JSONObject();

		try {
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchemaId(idDataSchema);
				JSONObject obj = new JSONObject();
				for(ListDataSchema lds : listdataschema ){
					if(idDataSchema == lds.getIdDataSchema()){
						obj.put("idDataSchema",lds.getIdDataSchema());
						obj.put("schemaName",lds.getSchemaName());
						obj.put("schemaType",lds.getSchemaType());
						obj.put("ipAddress",lds.getIpAddress());
						obj.put("databaseSchema",lds.getDatabaseSchema());
						obj.put("port",lds.getPort());
						if(lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle") || lds.getSchemaType().toString().trim().equalsIgnoreCase("Oracle RAC")){
							obj.put("serviceName",lds.getKeytab());
						}
						if(lds.getSchemaType().toString().trim().equalsIgnoreCase("MSSQLActiveDirectory")){
							obj.put("domain",lds.getDomain());
						}
						if(lds.getSchemaType().toString().trim().equalsIgnoreCase("Hive Kerberos")){
							obj.put("principal",lds.getDomain());
							obj.put("gss_jaas",lds.getKeytab());
							obj.put("krb5conf",lds.getKrb5conf());
						}
					}
				}
				return obj.toString();
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
		e.printStackTrace();
		LOG.error("Exception  "+e.getMessage());
		json.put("fail", "Request failed");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/viewIdDataConnections - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/viewAllDataTemplate", method = RequestMethod.GET)
	public String viewAllDataTemplate(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {
		LOG.info("restapi/viewAllDataTemplate - START");
		JSONObject json = new JSONObject();

		try{
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);
			boolean isUserValid = authorizationService.authenticateUser(authorization);
			if (isUserValid) {
				Long projectId= (Long)session.getAttribute("projectId");
				List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTable(projectId,projList,"","");
				JSONArray jsonArray = new JSONArray();
				JSONObject mainObj = new JSONObject();
				for (ListDataSource lds : listdatasource) {
					JSONObject obj = new JSONObject();
					obj.put("idData", lds.getIdData());
					obj.put("name", lds.getName());
					obj.put("dataLocation", lds.getDataLocation());
					obj.put("tableName", lds.getTableName());
					obj.put("createdAt", lds.getCreatedAt());
					jsonArray.put(obj);
				}

				mainObj.put("All data Template", jsonArray);
				LOG.info("All data Template "+jsonArray);
				return mainObj.toString();
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/viewAllDataTemplate - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/viewIdDataTemplate", method = RequestMethod.GET)
	public String viewIdDataTemplate(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,

			HttpSession session,long idData) {
		LOG.info("restapi/viewIdDataTemplate - START");
		JSONObject json = new JSONObject();
		try{
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			String authorization = request.getHeader("Authorization");
			LOG.debug("authorization=" + authorization);
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableId(idData);
				JSONObject obj = new JSONObject();
				for(ListDataSource lds : listdatasource ){
					if(idData == lds.getIdData()){
						obj.put("idData",lds.getIdData());
						obj.put("name",lds.getName());
						obj.put("dataLocation",lds.getDataLocation());
						obj.put("tableName",lds.getTableName());
						obj.put("createdAt",lds.getCreatedAt());
					}
				}
				return obj.toString();
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			e.printStackTrace();
			json.put("fail", "Request failed");
			LOG.error("Exception  "+e.getMessage());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/viewIdDataTemplate - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/viewAllValidationChecks", method = RequestMethod.GET)
	public String viewAllValidationChecks(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) {
		LOG.info("restapi/viewIdValidationChecks - START");
		JSONObject json = new JSONObject();
		try{
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			
			String authorization = request.getHeader("Authorization");
			
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				Long projectId= (Long)session.getAttribute("projectId");
				String fromDate = (String) session.getAttribute("fromDate");
				String toDate = (String) session.getAttribute("toDate");
				List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
				List<ListApplicationsandListDataSources> listappslistds = validationcheckdao.getdatafromlistappsandlistdatasources(projectId,projList,toDate,fromDate);
				JSONArray jsonArray = new JSONArray();
				JSONObject mainObj = new JSONObject();
				for (ListApplicationsandListDataSources lad : listappslistds) {
					JSONObject obj = new JSONObject();
					// System.out.println("idApp:"+lad.getIdApp());
					obj.put("idApp", lad.getIdApp());
					obj.put("validationCheckName", lad.getLaName());
					obj.put("DataTemplateName", lad.getLsName());
					obj.put("appType", lad.getAppType());
					obj.put("createdAt", lad.getCreatedAt());
					jsonArray.put(obj);
				}
				LOG.debug("All Application Checks"+ jsonArray);
				mainObj.put("All Application Checks", jsonArray);
				return mainObj.toString();
			}
			LOG.error("Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			LOG.info("exception "+e.getMessage());
			e.printStackTrace();
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/viewIdValidationChecks - END");
		return json.toString();
	}

	@RequestMapping(value = "restapi/viewIdValidationChecks", method = RequestMethod.GET)
	public String viewIdValidationChecks(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session,long idApp ) {
		LOG.info("restapi/viewIdValidationChecks - START");
		JSONObject json = new JSONObject();

		try{
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			LOG.debug("Authorization "+request.getHeader("Authorization"));
			String authorization = request.getHeader("Authorization");
			
			boolean isUserValid = authorizationService.authenticateUser(authorization);

			if (isUserValid) {
				List<ListApplicationsandListDataSources> listappslistds = validationcheckdao.getdatafromlistappsandlistdatasourcesId(idApp);
				JSONObject obj = new JSONObject();
				for (ListApplicationsandListDataSources lad : listappslistds) {
					if (idApp == lad.getIdApp()) {
						obj.put("idApp", lad.getIdApp());
						obj.put("validationCheckName", lad.getLaName());
						obj.put("DataTemplateName", lad.getLsName());
						obj.put("appType", lad.getAppType());
						obj.put("createdAt", lad.getCreatedAt());
					}
				}
				return obj.toString();
			}
			LOG.error( "Invalid Authorization");
			json.put("fail", "Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
			json.put("fail", "Request failed");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		LOG.info("restapi/viewIdValidationChecks - END");
		return json.toString();
		
	}

	@ApiOperation(value = "Creates validation check", notes = "This API is used to create Validation for a particular template in "
			+ "Databuck", tags="Validation")
	@ResponseBody
	@RequestMapping(value = "restapi/createValidationCheck", method = RequestMethod.POST,produces = "application/json")
	public CreateValidationCheckResponse createValidationCheck(HttpServletRequest req, HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestBody ValidationCheckBean validationCheckBean) {
		LOG.info("restapi/createValidationCheck - START");
		CreateValidationCheckResponse json = new CreateValidationCheckResponse();
		try{
			Map<String, String> secureAPIMap = Jsoncontroller.getSecureAPI();
			LOG.debug("token   " + request.getHeader("token") );
			String authorization = request.getHeader("token");
			boolean isUserValid = tokenValidator.isValid(authorization!=null?authorization:"");
			if (isUserValid) {
				String validationCheckName=validationCheckBean.getValidationCheckName();
				String validationCheckType=validationCheckBean.getValidationCheckType();
				String description=validationCheckBean.getDescription();
				String idData=validationCheckBean.getIdData();
				String fileNameValidation=validationCheckBean.getFileNameValidation();
				String colOrderValidation=validationCheckBean.getColOrderValidation();
				String nonNullCheck=validationCheckBean.getNonNullCheck();
				String numericalStatCheck=validationCheckBean.getNumericalStatCheck();
				String stringStatCheck=validationCheckBean.getStringStatCheck();
				String recordAnomalyCheck=validationCheckBean.getRecordAnomalyCheck();
				String incrementalColCheck="N";
				String dataDriftCheck=validationCheckBean.getDataDriftCheck();
				String updateFrequency=validationCheckBean.getUpdateFrequency();
				String timeSeries=validationCheckBean.getTimeSeries();
				String recordCountAnomalyCheck=validationCheckBean.getRecordCountAnomalyCheck();
				String recordCountAnomalyThreshold=validationCheckBean.getRecordCountAnomalyThreshold();
				String keyGroupRecordCountAnomalyCheck=validationCheckBean.getKeyGroupRecordCountAnomalyCheck();
				String keyGroupRecordCountAnomalyThreshold=validationCheckBean.getKeyGroupRecordCountAnomalyThreshold();
				String outOfNormCheck=validationCheckBean.getOutOfNormCheck();
				String applyRules=validationCheckBean.getApplyRules();
				String applyDerivedColumns=validationCheckBean.getApplyDerivedColumns();
				String groupEqualityCheck=validationCheckBean.getGroupEqualityCheck();
				String groupEqualityThreshold=validationCheckBean.getGroupEqualityThreshold();
				String buildHistoricFingerPrint="N";
				String historicStartDate=validationCheckBean.getHistoricStartDate();
				String historicEndDate=validationCheckBean.getHistoricEndDate();
				String historicDateFormat=validationCheckBean.getHistoricDateFormat();
				

				if (validationCheckName == null) {
					LOG.error("validationCheckName parameter is missing  ");
					json.setFail( "validationCheckName parameter is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}
				if (validationCheckType == null) {
					LOG.error("validationCheckType parameter is missing  ");
					json.setFail( "validationCheckType parameter is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}else if (validationCheckType.trim().equalsIgnoreCase("Bulk Load")){
					incrementalColCheck="N";
					buildHistoricFingerPrint="N";
				}else if (validationCheckType.trim().equalsIgnoreCase("Historic")){
					incrementalColCheck="Y";
					buildHistoricFingerPrint="Y";
					if(historicStartDate == null){
						LOG.error("historic Start Date  is missing  ");
						json.setFail( "historic Start Date is missing");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}else{
						SimpleDateFormat DFt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						historicStartDate = DFt.format(historicStartDate);

					}
					if(historicEndDate == null){
						LOG.error("historic End Date is missing  ");
						json.setFail( "historic End Date is missing");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}else{
						SimpleDateFormat DFt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						historicEndDate = DFt.format(historicEndDate);
					}
					if(historicDateFormat == null){
						LOG.error("historic Date Format is missing  ");
						json.setFail( "historic Date Format is missing");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						return json;
					}
				}else if (validationCheckType.trim().equalsIgnoreCase("Incremental")){
					incrementalColCheck="Y";
					buildHistoricFingerPrint="N";
				}

				if (idData == null) {
					json.setFail( "idData parameter is missing");
					LOG.error("idData parameter is missing  ");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}

				if(description == null){
					description = "";
				}

				if(fileNameValidation == null){
					fileNameValidation = "N";
				}

				if(colOrderValidation == null){
					colOrderValidation = "N";
				}

				if(nonNullCheck == null){
					nonNullCheck = "N";
				}

				if(numericalStatCheck == null){
					numericalStatCheck = "N";
				}

				if(stringStatCheck == null){
					stringStatCheck = "N";
				}

				if(recordAnomalyCheck == null){
					recordAnomalyCheck = "N";
				}

				if(dataDriftCheck == null){
					dataDriftCheck = "N";
				}

				if(updateFrequency == null){
					updateFrequency = "Never";
					timeSeries = "None";
				}else if(timeSeries == null){
					LOG.error("timeSeries parameter is missing  ");
					json.setFail( "timeSeries parameter is missing");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return json;
				}

				if(recordCountAnomalyCheck == null){
					recordCountAnomalyCheck = "N";
					recordCountAnomalyThreshold = "0.0";
				}else if(recordCountAnomalyThreshold == null){
					recordCountAnomalyThreshold = "0.0";
				}

				if(keyGroupRecordCountAnomalyCheck == null){
					keyGroupRecordCountAnomalyCheck = "N";
					keyGroupRecordCountAnomalyThreshold = "0.0";
				}else if(keyGroupRecordCountAnomalyThreshold == null){
					keyGroupRecordCountAnomalyThreshold = "0.0";
				}

				if(outOfNormCheck == null){
					outOfNormCheck = "N";
				}

				if(applyRules == null){
					applyRules = "N";
				}

				if(applyDerivedColumns == null){
					applyDerivedColumns = "N";
				}

				if(groupEqualityCheck == null){
					groupEqualityCheck = "N";
					groupEqualityThreshold="0.0";
				}else if(groupEqualityThreshold == null){
					groupEqualityThreshold = "0.0";
				}

				String maxIdAppQuery = "select max(idApp) as idApp from listApplications";
				SqlRowSet maxIdAppRowSet =  jdbcTemplate.queryForRowSet(maxIdAppQuery);
				int maxIdApp = 0;
				if (maxIdAppRowSet.next()) {
					maxIdApp = Integer.parseInt(maxIdAppRowSet.getString("idApp"));
				}
				int DomainID=0,ProjectID = 0;
				try {
					String projectDomainQuery = "select project_id,domain_id from listDataSources where idData=?";
					SqlRowSet projectDomainSet =  jdbcTemplate.queryForRowSet(projectDomainQuery,idData);
					
					if (projectDomainSet.next()) {
						ProjectID = Integer.parseInt(projectDomainSet.getString("project_id"));
						DomainID = Integer.parseInt(projectDomainSet.getString("domain_id"));
					}
				} catch (Exception e) {
					LOG.error("No data present related to idData");
					json.setFail( "No data present related to idData");
				}

				String newValidationName = (maxIdApp + 1) + "_" + validationCheckBean.getValidationCheckName();

				String listApplicationsQuery = ("insert into listApplications (name, description, "
						+ "appType, idData,createdBy,createdAt,"
						+ "fileNameValidation, entityColumn, colOrderValidation, matchingThreshold, "
						+ "nonNullCheck, numericalStatCheck, stringStatCheck, recordAnomalyCheck, "
						+ "incrementalMatching, incrementalTimestamp, dataDriftCheck, updateFrequency, "
						+ "frequencyDays, recordCountAnomaly, recordCountAnomalyThreshold, timeSeries, "
						+ "keyGroupRecordCountAnomaly, outOfNormCheck, applyRules, applyDerivedColumns, "
						+ "csvDir, groupEquality, groupEqualityThreshold, buildHistoricFingerPrint, "
						+ "historicStartDate, historicEndDate, historicDateFormat, active, correlationcheck, "
						+ "project_id, timelinessKeyCheck, defaultCheck, defaultValues, "
						+ "patternCheck, dateRuleCheck, badData, lengthCheck, maxLengthCheck,reprofiling,domain_id) Values(?,?,?,?,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?,?" +
						")");
				long createdBy = 1l;
				String createdAt = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				jdbcTemplate.update(listApplicationsQuery,
						new Object[] {newValidationName, description, validationCheckType, idData,createdBy,createdAt,fileNameValidation, "", colOrderValidation, "1.0", nonNullCheck, numericalStatCheck, stringStatCheck, recordAnomalyCheck, "", null, dataDriftCheck, updateFrequency, 0, recordCountAnomalyCheck, recordCountAnomalyThreshold, timeSeries, keyGroupRecordCountAnomalyCheck, outOfNormCheck, applyRules, applyDerivedColumns, "", groupEqualityCheck, groupEqualityThreshold, buildHistoricFingerPrint, historicStartDate, historicEndDate, historicDateFormat, "yes", "N", ProjectID, "N", "N", "", "N", "N", "N", "N","N","N",DomainID });
				
				LOG.info("listApplication updated  ");


				json.setSuccess( "Validation Check created Successfully");
				LOG.info("Validation Check created Successfully  ");
				json.setValidationName( newValidationName);

				return json;
			}
			LOG.error("Invalid Authorization");
			json.setFail("Invalid Authorization");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
			json.setFail( "Request failed Error occurred");
														
		}
		LOG.info("restapi/createValidationCheck - END");
		return json;
	}
	
	@ApiOperation(value = "Get autherization token by loggin in", notes = "This API gives authorization token if user is valid, enter this token in swagger token field", tags="Login")
	@ResponseBody
	@RequestMapping(value = "/restapi/login", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<LoginResponse> loginPage(@RequestBody LoginRequest loginReq, HttpServletRequest request,
			HttpSession session, HttpServletResponse httpResponse) {
		LOG.info("/restapi/login - START");
		LoginResponse response = new LoginResponse();
		response.setMessage( "Successfully logged in.");
		LoginResponse failureResponse = new LoginResponse();
		failureResponse.setStatus( "failed");
		try {
			if (loginReq.getEmail()==null || loginReq.getPassword()==null) {
				failureResponse.setMessage( "Username or password is missing.");
				return new ResponseEntity<LoginResponse>((LoginResponse) failureResponse, HttpStatus.FORBIDDEN);
			}
			String email = loginReq.getEmail();
			String password = loginReq.getPassword();

			session.setAttribute("csrfToken", loginService.generateCSRFToken());

			ArrayList<String> alist = new ArrayList<String>();
			String msg = "The username or password you entered is incorrect";
			String licenseMsg = "License expired - please contact info@firsteigen.com";
			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryFlag == null)
				activeDirectoryFlag = "N";
			boolean lByPassMigration = migrationManage.isByPassMigrationEnabled();
			String sByPassMigrationMsg = "";
			LoginResponse oMigrationView = new LoginResponse();

			
			LOG.info("activeDirectoryFlag-->" + activeDirectoryFlag);

			/*
			 * Pradeep 7-Jan-2020 - Intercept login process to check is fresh customer with
			 * full import DataBuck schema import or it need upgrade?
			 */
			String sPageContext = null;
			MigrationManagement.DbMigrationContext oDbMigrationContext = migrationManage
					.getDbMigrationContext(request.getServletContext());

			Map<String, Object> sPageContext1 = new HashMap<>();

			session.setAttribute("freshDBSetUpRestartRequired", "N");

			String sContinueMsg = "Kindly click proceed to continue ..";
			String sSupportMsg = "Can not continue to login .. Kindly contact FirstEigen support.";
//			String supportMessageMThree = "Program encounted technical error while submitting request to application server.\n"
//					+ sSupportMsg;
			String supportMessageMTwo = "Inconsistent state of DataBuck database detected or error occured.\n"
					+ sSupportMsg;
//			String supportMessageMOne = "Error occured while upgrade/import data schema.\n" + sSupportMsg;
			String contMsgZero = "The DataBuck product need to import all database schema before you use it.\n"
					+ sContinueMsg;
			String contMsgOne = "The DataBuck database need to be upgraded before you use  features/bug fixes.\n"
					+ sContinueMsg;
//			String contMsgTwo = "The DataBuck database upgraded/schema imported successfully, kindly login to DataBuck.";

			if (oDbMigrationContext == MigrationManagement.DbMigrationContext.FreshDatabase) {
				session.setAttribute("freshDBSetUpRestartRequired", "Y");

				if (lByPassMigration) {
					sByPassMigrationMsg = "The DataBuck product need to import all database schemas before you use it.CRLFCRLFManual DB Migration is enabled, kindly setup databases manually and restart server.";
					// sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
					sPageContext1.put("pageContext", 3);
					sPageContext1.put("message", "");
				} else {
					// sPageContext = "{ `PageContext`: 0, `Msg`: `` }".replace('`', '\"');
					sPageContext1.put("pageContext", 0);
					sPageContext1.put("message", contMsgZero);
				}

			} else if (oDbMigrationContext == MigrationManagement.DbMigrationContext.ExistingDatabaseWithChanges) {
				// sPageContext = "{ `PageContext`: 1, `Msg`: `{0}` }".replace('`', '\"');
				sPageContext1.put("pageContext", 1);
				sPageContext1.put("message", contMsgOne);
				try {
					if (lByPassMigration) {
						sByPassMigrationMsg = migrationManage.getByPassMigrationDisplableMsg(oDbMigrationContext);
						// sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
						sPageContext1.put("pageContext", 3);
						sPageContext1.put("message", "");
					}
				} catch (Exception oException) {
					oException.printStackTrace();
					LOG.error("Exception  "+oException.getMessage());
				}
				LOG.debug( String.format(
						"Bypass migration = '%1$s', Bypass msg = '%2$s'", lByPassMigration, sByPassMigrationMsg));

			} else if ((oDbMigrationContext == MigrationManagement.DbMigrationContext.InconsistentDatabase)
					|| (oDbMigrationContext == MigrationManagement.DbMigrationContext.ErrorOccured)) {
				sPageContext = "{ `PageContext`: -2, `Msg`: `` }".replace('`', '\"');
				sPageContext1.put("pageContext", -2);
				sPageContext1.put("message", supportMessageMTwo);
			}

			if (sPageContext != null) {
				// oMigrationView = new ModelAndView("DatabaseMigration");
				oMigrationView.setResult( sPageContext1);
				
				LOG.debug(String.format(
						"Database schema needs import or upgrade as '%1$s' detected, redirecting to database migration page",
						oDbMigrationContext));
				return new ResponseEntity<LoginResponse>(oMigrationView, HttpStatus.FORBIDDEN);

			} else {
				LOG.info(
						"Database schema is up-to-date no schema changes were detected, continuing to BAU login process ...");
			}

			if (email != null && email.trim().length() > 0 && password != null && password.trim().length() > 0) {
				Long login_log_row_id = 0l;
				if (activeDirectoryFlag == null || activeDirectoryFlag.trim().equals("N")) {
					// Insert into DatabuckLoginAccessLog
					login_log_row_id = iLoginDAO.insertDatabuckLoginAccessLog(null, email, "/login_process",
							session.getId(), "login", "");
					List<DomainProject> dplist = new ArrayList<DomainProject>();
					Long idUser = loginService.userAuthentication(email, password);
					if (idUser != null) {
						// Update status in DatabuckLoginAccessLog
						iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, idUser, "passed");
						if (loginService.checkDaysLeftForLicenseRenewal(session) < 0) {
							User userDb = new User();
							String firstName = loginService.getFirstNameFromUserTable(idUser);
							// session.setAttribute("firstName", firstName);
							userDb.setFirstName(firstName);
							userDb.setIdUser(idUser);
							// session.setAttribute("user", "validUser");
							// System.out.println("in login pro:" + session.getAttribute("user"));
							// session.setAttribute("email", email);
							userDb.setEmail(email);
							// session.setAttribute("idUser", idUser);
							LOG.debug("idUser" + idUser);
							Long idRole = loginService.getRolesFromUserRoleTable(idUser);
							LOG.debug("idRole=" + idRole);
							// session.setAttribute("idRole", idRole);
							String Rolename;
							Rolename = loginService.getRoleFromRoleTable(idRole);
							// session.setAttribute("Role", Rolename);
							userDb.setIdRole(idRole);
							userDb.setRoleName(Rolename);
							SqlRowSet roleModuleTable = loginService
									.getIdTaskandAccessControlFromRoleModuleTable(idRole);
							long idTask = 0l;
							Map<String, String> module = new LinkedHashMap<String, String>();
							LOG.debug("module in login controller" + module);
							while (roleModuleTable.next()) {
								idTask = roleModuleTable.getLong("idTask");
								// System.out.println("idTask="+idTask+"accessControl="+roleModuleTable.getString("accessControl"));
								String taskName = loginService.getTaskNameFromModuleTable(idTask);
								module.put(taskName, roleModuleTable.getString("accessControl"));
							}
							
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date today = new Date();
							Calendar cal = new GregorianCalendar();
							cal.setTime(today);
							String curr_today = dateFormat.format(cal.getTime());
							cal.add(Calendar.DAY_OF_MONTH, -30);
							String curr_today_30 = dateFormat.format(cal.getTime());
							LOG.debug("fromDate" + curr_today_30);
							LOG.debug("toDate" + curr_today);
							String dateFilter = "";
							dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
							session.setAttribute("dateFilter", dateFilter);
							session.setAttribute("toDate", curr_today_30);
							session.setAttribute("fromDate", curr_today);
							session.setAttribute("RunFilter", 0);
							session.setAttribute("module", module);
							String token = generateToken(userDb);
							response.setToken(token);
							response.setStatus("success");
							return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
						} else {
							// Check if it is fresh database setup and restart is required

							String freshDBSetUpRestartRequired = (String) session
									.getAttribute("freshDBSetUpRestartRequired");
							if (freshDBSetUpRestartRequired != null
									&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
								msg = "Kindly restart the application and login";
								failureResponse.setMessage(  msg);
								return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
							} else {
								LOG.info(licenseMsg);
								failureResponse.setMessage(  licenseMsg);
								return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
							}
						}
					} else {

						LOG.info(msg);
						// Update status in DatabuckLoginAccessLog
						iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, idUser, "failed");
						failureResponse.setMessage(  msg);
						return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
					}
				} else {
					if (loginService.checkDaysLeftForLicenseRenewal(session) < 0) {
						try {
							// Insert into DatabuckLoginAccessLog
							login_log_row_id = iLoginDAO.insertDatabuckLoginAccessLog(null, email, "/login_process",
									session.getId(), "login", "");

							User user = new User();
							// user =loginService.userActiveDirectoryAuthentication(email, password);
							List<DomainProject> dplist = new ArrayList<DomainProject>();
							HashMap<String, String> oProgramOutput = null;
							HashMap<String, String> oProgramOutput1 = null;
							String databuckHome = loginService.getDatabuckHome();
							String sCmdLine = databuckHome + "/scripts/ldap_login.sh";
							String sCmdLine1 = databuckHome + "/scripts/ldap_auth.sh";
							String domainname = "";
							String principal = activeDirectoryConnectionProperties.getProperty("principal");
							String Adminpasssword = activeDirectoryConnectionProperties.getProperty("credentials");

							LOG.debug(" Script command: " + databuckHome + "/scripts/ldap_login.sh "
									+ email + " xxxxxxx xxxxxxx\n");
							String[] ldap_login_cmd_args = { sCmdLine, email, Adminpasssword, principal };

							oProgramOutput = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_login_cmd_args,
									new String[] { "result: 0 Success" });
							LOG.debug("oProgramOutput : " + oProgramOutput);

							if (oProgramOutput.get("Program Result").equalsIgnoreCase("1")) {
								String ProgramOutput = oProgramOutput.get("Program Std Out");
								String[] arrOfStr = ProgramOutput.split("\n");
								String previousLine = null;
								for (String a : arrOfStr) { // dn: //getting DN from cn
									/*
									 * if (a.startsWith("dn:")) { String[] groupname = a.split(":"); domainname =
									 * groupname[1]; break; }
									 */
									if (previousLine != null) {
										// compare
										if (a.startsWith("memberOf:")) {

											String[] groupname = previousLine.split(":");
											domainname = groupname[1];
											LOG.info("1");
											break;
										} else {
											previousLine = previousLine.trim() + "" + a.trim();
											String[] groupname = previousLine.split(":");
											domainname = groupname[1];
											LOG.info("2");
											break;
										}
									}
									if (a.startsWith("dn:")) {
										previousLine = a;
									}

								}
							}

							if (domainname.equalsIgnoreCase("")) {
								LOG.debug("invalid cn entered (DN not found): " + email);
								LOG.info(msg);
								failureResponse.setMessage(  msg);
								return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
							} else // authentication block
							{
								LOG.debug("  (DN  found for entered CN ): " + domainname);
								String[] ldap_auth_cmd_args = { sCmdLine1, email, password, principal, Adminpasssword };
								LOG.debug(" Script command: " + databuckHome + "/scripts/ldap_auth.sh "
										+ email + " xxxxxxx xxxxxxx xxxxxxx\n");
								oProgramOutput1 = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_auth_cmd_args,
										new String[] { "Result: Success (0)" });
								LOG.debug("oProgramOutput1 :   " + oProgramOutput1);
								if (oProgramOutput1.get("Program Result").equalsIgnoreCase("1")) {
									LOG.info("User authenticated succesfully  ");
									LOG.debug("loadComponentAccessControlViewList1 02 " + oProgramOutput1);

									// Update status in DatabuckLoginAccessLog
									iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "passed");
								}

								else {
									LOG.debug("Invalid credentials for dn/password : " + domainname);
									LOG.info(msg);

									// Update status in DatabuckLoginAccessLog
									iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "failed");
									failureResponse.setMessage(  msg);
									return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
								}
							}

							String ProgramOutput = oProgramOutput.get("Program Std Out");

							alist = loginService.getgroupfrom_Program_Std_Out(ProgramOutput);
							session.setAttribute("UserLDAPGroups", String.join(",", alist)); // Pradeep 10-Oct-2020 for
																								// LDAP
																								// groups to project
																								// mapping

							// Save user and ldapGroup Mapping in holder
							UserLDAPGroupHolder.addOrUpdateUserLDAPGroup(email, String.join(",", alist));

							/* [29-Sep-2020]:Changes for LDAP group role mapping Starts */
							String defaulAdminLdapGroup = activeDirectoryConnectionProperties
									.getProperty("default_ldap_admin");
							String defaulAdminLdapRole = activeDirectoryConnectionProperties
									.getProperty("default_ldap_role");

							boolean isLdapAdmin = false;
							for (String group : alist) {
								if (defaulAdminLdapGroup.equals(group)) {
									isLdapAdmin = true;
								}
							}

							String Rolename = "";
							Long idRole = 0l;
							HashMap<Long, String> RoleData = new HashMap<Long, String>();
							List<Project> projList = new ArrayList<Project>();

							if (!isLdapAdmin)// compare groupOf user with adminGroup present in property
							{
								try {

									RoleData = loginService.getRoleDataFromLdapAfterLogin(alist);
									Rolename = (String) RoleData.values().toArray()[0];
									idRole = (Long) RoleData.keySet().toArray()[0];
									LOG.debug("\nRolename : " + Rolename + "\tidRole : " + idRole);

									/*
									 * Pradeep 26-Mar-2021 changes so session can contain as many roles mapped in
									 * DataBuck to logged in user's LDAP member group
									 */
									String sBelongsToRoles = loginService.getBelongsToRoles(RoleData);
									LOG.debug(String.format(
											"Session Values for Roles 'idRole / Rolename' = '%1$s / %2$s' ,'BelongsToRoles' = '%3$s'",
											idRole, Rolename, sBelongsToRoles));

									HashMap<Long, String> ProjectData = loginService
											.getProjectDataFromLdapAfterLogin(alist);
									LOG.debug("\nProjectname : " + (String) ProjectData.values().toArray()[0]
											+ "\tProjectid : " + (Long) ProjectData.keySet().toArray()[0]);
									projList = loginService.getProjectListOfUser(alist);

								} catch (Exception e) {
									LOG.error("No role and projects are found mapped to group ");
									LOG.error("Exception  "+e.getMessage());
									failureResponse.setMessage(  "No role and projects are found mapped to group");
									return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
								}
							} else {
								if (defaulAdminLdapGroup.equalsIgnoreCase("")
										|| defaulAdminLdapRole.equalsIgnoreCase("")) {
									LOG.error("default_ldap_admin, default_ldap_role properties are missing");
									failureResponse.setMessage( 
											"default_ldap_admin, default_ldap_role properties are missing");
									return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
								}
								Rolename = defaulAdminLdapRole;
								idRole = loginService.getRoleIdFromRoleTable(Rolename);
								projList = loginService.getProjectListOfUser(alist);
							}

							boolean isUserPresent = loginService.getIsUserPresent(email);
							if (!isUserPresent) {
								boolean newUserRecordInsertedFlag = loginService.insertNewUserRecord(idRole, email,
										password, alist);
								LOG.debug("newUserRecordInsertedFlag: " + newUserRecordInsertedFlag);
							}

							user = userDAO.getUserDataByName(email);
							/* [29-Sep-2020]:Changes for LDAP group role mapping Ends */
							LOG.debug("User object in Login_process-->" + user);
							// session.setAttribute("idUser", user.getIdUser());
							// session.setAttribute("idUser", user.getEmail());
							// System.out.println("idUser -> " + user.getEmail());
							String defaultrole = activeDirectoryConnectionProperties.getProperty("defaultrole");
							LOG.debug("defaultrole ->" + defaultrole);
							// session.setAttribute("firstName", user.getFirstName());
							// session.setMaxInactiveInterval(604800);
							// session.setAttribute("user", "validUser");
							// System.out.println("in login pro:" + session.getAttribute("user"));
							// session.setAttribute("email", user.getEmail());
							// session.setAttribute("createdByUser", user.getEmail());
							// session.setAttribute("idRole", idRole);
							// session.setAttribute("Role", Rolename);
							SqlRowSet roleModuleTable = loginService
									.getIdTaskandAccessControlFromRoleModuleTable(idRole);
							long idTask = 0l;
							Map<String, String> module = new LinkedHashMap<String, String>();
							LOG.debug("module in login controller" + module);
							while (roleModuleTable.next()) {
								idTask = roleModuleTable.getLong("idTask");
								String taskName = loginService.getTaskNameFromModuleTable(idTask);
								module.put(taskName, roleModuleTable.getString("accessControl"));
							}
							LOG.debug("module in login controller" + module);
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date today = new Date();
							Calendar cal = new GregorianCalendar();
							cal.setTime(today);
							String curr_today = dateFormat.format(cal.getTime());
							cal.add(Calendar.DAY_OF_MONTH, -30);
							String curr_today_30 = dateFormat.format(cal.getTime());
							LOG.debug("fromDate" + curr_today_30);
							LOG.debug("toDate" + curr_today);
							String dateFilter = "";
							dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
							session.setAttribute("dateFilter", dateFilter);
							session.setAttribute("toDate", curr_today_30);
							session.setAttribute("fromDate", curr_today);
							session.setAttribute("RunFilter", 0);
							session.setAttribute("module", module);
							String token = generateToken(user);
							response.setToken(token);
							response.setStatus( "success");
							return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);

						} catch (Exception e) {
							e.printStackTrace();
							LOG.error("Exception  "+e.getMessage());
						}
					} else {

						// Check if it is fresh database setup and restart is required
						String freshDBSetUpRestartRequired = (String) session
								.getAttribute("freshDBSetUpRestartRequired");

						if (freshDBSetUpRestartRequired != null
								&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
							msg = "Kindly restart the application and login";
							failureResponse.setMessage(  msg);
							return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
						} else {
							
							LOG.info("licenseMsg "+licenseMsg);
							failureResponse.setMessage(  licenseMsg);
							return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
						}
					}
				}
			}

			
			LOG.info("msg "+msg);
			
			failureResponse.setMessage( msg);
			LOG.info("/restapi/login - END");
			return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
			failureResponse.setMessage( "The username or password you entered is incorrect");
			LOG.info("/restapi/login - END");
			return new ResponseEntity<LoginResponse>(failureResponse, HttpStatus.FORBIDDEN);
		}
	}
	


	private String generateToken(User user) {
		LOG.info("generateToken - START");
		String token = null;
		try {
			LOG.info(" email: " + user.getEmail());
			String userLDAPGroups = "";
			String activeDirectoryUser = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryUser != null && activeDirectoryUser.trim().equalsIgnoreCase("Y")) {
				activeDirectoryUser = "Y";

				// Get userLdapGroups
				userLDAPGroups = UserLDAPGroupHolder.getLDAPGroupsForUser(user.getEmail());
			} else {
				activeDirectoryUser = "N";
			}
			LOG.info(" userLDAPGroups: " + userLDAPGroups);
			// Check if active token available for this user
			token = dashboardConsoleDao.checkForExistingUserToken(user.getIdUser(), user.getEmail(),
					activeDirectoryUser);
			boolean validTokenFound = false;
			if (token != null && !token.isEmpty()) {
				// Check if token is still active
				String tokenStatus = getStatusOfUserToken(token);
				// If the token is ACTIVE, Fetch the project list
				if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
					validTokenFound = true;
					LOG.info(" Active token already exists, hence returning the same !!");
				}
			}
			if (!validTokenFound) {
				LOG.info(" Active token not found, hence generating new token !!121313");
				Date loginTime = new Date(System.currentTimeMillis());
				Date expiryTime = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
				token = UUID.randomUUID().toString();
				UserToken userToken = new UserToken(user.getIdUser(), user.getFirstName(), user.getIdRole(),
						user.getRoleName(), user.getEmail(), loginTime, expiryTime, token, "ACTIVE",
						activeDirectoryUser, userLDAPGroups);
				dashboardConsoleDao.insertUserToken(userToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  "+e.getMessage());
		}
		
		LOG.info("token: " + token);
		LOG.info("generateToken - END");
		return token;
	}
	
	private String getStatusOfUserToken(String token) {
		LOG.info("getStatusOfUserToken - START");
		String tokenStatus = "EXPIRED";
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
			if (userToken != null) {
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			} else {
				
				LOG.error("Failed to get Token details!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("getStatusOfUserToken - END");
		return tokenStatus;
	}

	
}

