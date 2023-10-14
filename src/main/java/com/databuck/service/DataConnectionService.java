package com.databuck.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.TaskTypes;
import com.databuck.integration.IntegrationMasterService;
import com.databuck.util.DebugUtil;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.DashboardTableCount;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.ListDataSchema;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IDashboardConsoleDao; 
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.AzureConnection;
import com.databuck.datatemplate.AzureCosmosDb;
import com.databuck.datatemplate.AzureDataLakeConnection;	 
import com.databuck.datatemplate.AzureDataLakeGen2Connection;
import com.databuck.datatemplate.BatchFileSystem;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.DatabricksDeltaConnection;
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
import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.TaskTypes;
import com.databuck.integration.IntegrationMasterService;			 
import com.databuck.security.LogonManager;
import com.databuck.util.ConnectionUtil;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.DebugUtil;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.google.cloud.bigquery.BigQuery;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.AzureADToken;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

@Service
public class DataConnectionService {

	@Autowired
	private LogonManager logonManager;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	public SchemaDAOI schemaDAOI;

	@Autowired
	private BigQueryConnection bigQueryConnection;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDao;

	@Autowired
	private FileMonitorDao fileMonitorDao;

	@Autowired
	IUserDAO userDAO;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	private ConnectionUtil connectionUtil;

	@Autowired
	private AzureDataLakeGen2Connection azureDataLakeGen2Connection;

	@Autowired
	private AzureCosmosDb azureCosmosConnection;

	@Autowired
	private IntegrationMasterService integrationMasterService;
	
	@Autowired
	SnowflakeConnection snowFlakeConnection;

	@Autowired
	BatchFileSystem batchFileSystem;

	@Autowired
	S3BatchConnection s3BatchConnection;

	@Autowired
	AzureDataLakeConnection azureDataLakeConnection;

	@Autowired
	AzureCosmosDb azureCosmosDb;
	
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
	AzureConnection azureConnection;

	@Autowired
	VerticaConnection verticaconnection;
	
	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;
	
	@Autowired
	private DatabricksDeltaConnection databricksDeltaConnection;
	
	@Autowired
	private IDashboardConsoleDao iDashboardConsoleDao;
	
	private static final Logger LOG = Logger.getLogger(DataConnectionService.class);

	public long saveConnectionInfo(Long projectId, Integer domainId, Long idUser, String createdByUser,
			String schemaName, String schemaType, String uri, String database, String username, String password,
			String port, String domain, String serviceName, String krb5conf, String autoGenerateId, String hivejdbchost,
			String hivejdbcport, String suffix, String prefix, String sslEnb, String sslTrustStorePath,
			String trustPassword, String gatewayPath, String jksPath, String zookeeperUrl, String folderPath,
			String fileNamePattern, String fileDataFormat, String headerPresent, String partitionedFolders,
			String headerFilePath, String headerFileNamePattern, String headerFileDataFormat, String accessKey,
			String secretKey, String bucketName, String bigQueryProjectName, String privatekeyId, String privatekey,
			String clientId, String clientEmail, String datasetName, String azureClientId, String azureClientSecret,
			String azureTenantId, String azureServiceURI, String azureFilePath, String enableFileMonitoring,
			String multiPattern, int startingUniqueCharCount, int endingUniqueCharCount, int maxFolderDepth,
			String fileEncrypted, String singleFile, String externalFileNamePatternId, String externalFileName,
			String patternColumn, String headerColumn, String localDirectoryColumnIndex, String xsltFolderPath,
			String kmsAuthDisabled, String readLatestPartition, String alation_integration_enabled,
			String incrementalDataReadEnabled, String clusterPropertyCategory, String multiFolderEnabled,
			String pushDownQueryEnabled,String httpPath,String clusterPolicyId,String azureAuthenticationType)throws Exception {
		long idDataSchema = 0l;
		LOG.info("Inside saveConnectionInfo");
		String connectionFailureMessage = null;
		Exception connectionFailureException = null;
		JSONArray mappingErrors = new JSONArray();
		
		String credentialsFail = "Entered wrong username/password.";
		String uriFail = "Entered wrong uri/port.";
		String dbFail = "Entered wrong database/schema.";

		try {
			// if allation is null, making it 'N'
			if (alation_integration_enabled == null || alation_integration_enabled.trim().isEmpty())
				alation_integration_enabled = "N";

			// if not Mapr Hive readLatestPartition should be 'N'
			if (readLatestPartition == null || readLatestPartition.trim().isEmpty()
					|| !readLatestPartition.trim().equalsIgnoreCase("Y") || !schemaType.equalsIgnoreCase("MapR Hive")) {
				readLatestPartition = "N";
			}

			if (schemaType.equalsIgnoreCase("oracle")) {
				port = port + "/" + serviceName;
			}

			Connection con = null;
			CqlSession cassandraSession = null;
			String name = null;
			boolean flag = true;
			boolean connectionStatus = false;

			/*
			 * When logon manager is enabled user details coming from UI are KMS keys these
			 * will be saved in databuck, but the details used used to establish connection
			 * comes from logon manager
			 */
			if(schemaName.trim().isEmpty()) {
				throw new Exception("Please enter Connection Name.");
			}else{
				Pattern symbol = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");
				Matcher matcher = symbol.matcher(schemaName.trim().substring(0));
				if(!matcher.find()) { 
					throw new Exception("Please start connection name with alphabets and do not use spaces or special characters except _(underscore)");
				}
				
			}
			String actual_uri = uri;
			String actual_port = port;
			String actual_username = username;
			String actual_password = password;

			if (name == null || name == "") {

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
							port = (schemaType.equalsIgnoreCase("Oracle"))
									? conn_user_details.get("port") + "/" + serviceName
									: conn_user_details.get("port");
							username = conn_user_details.get("username");
							password = conn_user_details.get("password");
						} else
							flag = false;

					} else {
						LOG.debug("\n====>KMS Authentication is not supported for [" + schemaType + "] !!");
						flag = false;
					}

					if (!flag) {
						LOG.error("\n====>Failed to fetch connection details from logon manager !!");
						connectionFailureMessage = "Failed to fetch connection details from logon manager";
					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("oracle")) {
						String url = "jdbc:oracle:thin:@" + uri + ":" + port;
						try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							LOG.debug("con=" + con);
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("MSSQL")) {

						int dotPosition = database.indexOf(".");
						String database1 = database;
						String[] databasAndSchema = database1.split("\\.");
						if(databasAndSchema.length==2 && databasAndSchema[0]!=null && !databasAndSchema[0].trim().isEmpty()
								&& databasAndSchema[1]!=null && !databasAndSchema[1].trim().isEmpty()) {
							database1 = database.substring(0, dotPosition);
							String schema = database.substring(dotPosition+1, database.length());
							String url = "jdbc:sqlserver://" + uri + ":" + port + ";databaseName=" + database1 + ";encrypt=true;trustServerCertificate=true;";
							try {
								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								con = DriverManager.getConnection(url, username, password);
								ResultSet rsSchema = con.getMetaData().getSchemas();
								boolean isSchemaNameValid = false;
								while (rsSchema.next()) {
									String schemaNameStr = rsSchema.getString("TABLE_SCHEM");
									if(schemaNameStr.equalsIgnoreCase(schema)) {
										isSchemaNameValid = true;
										break;
									}
								}

								if(!isSchemaNameValid)
									throw new Exception(dbFail);
								connectionStatus = true;
							} catch (Exception e) {
								LOG.error(e.getMessage());
								flag = false;
								connectionFailureException = e;
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

					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureSynapseMSSQL")) {

						String url = "jdbc:sqlserver://" + uri + ":" + port + ";DatabaseName=" + database;

						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
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
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;

						}
					}
				}
				
				if (flag) {
					if (schemaType.equalsIgnoreCase("MSSQLAD")) {
						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							String db_connect_string = "";
							
							String databaseSchema = database.split("\\.")[0];
							String schema = database.split("\\.")[1];
							
							if(azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryPassword")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+databaseSchema+";user="+username+";password="+password+";"
									+ "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"
									+ "loginTimeout=30;authentication=ActiveDirectoryPassword";
							}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryIntegrated")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+databaseSchema+";encrypt=true;trustServerCertificate=false;"
										+ "hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
										+ "authentication=ActiveDirectoryIntegrated";
							}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryDefault")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+databaseSchema+";user="+username+";password="+password+";"//CloudSA8d3b0977@databuck
										+ "encrypt=true;trustServerCertificate=false;"
										+ "hostNameInCertificate=*.database.windows.net;"
										+ "loginTimeout=30;";
							}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryManagedIdentity")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";authentication=ActiveDirectoryManagedIdentity;database="+database;
							}else if (azureAuthenticationType.equalsIgnoreCase("ActiveDirectoryServicePrinicipal")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+databaseSchema+";user="+username+";password="+password+";Authentication=ActiveDirectoryServicePrincipal";
							}else if (azureAuthenticationType.equalsIgnoreCase("NotSpecified")) {
								db_connect_string = "jdbc:sqlserver://"+uri+":"+port+";database="+databaseSchema+";user="+username+";password="+password+";"
										+ "integratedSecurity=true;trustServerCertificate=true;authenticationScheme=NTLM;authentication=NotSpecified";
							}
							LOG.info("Connection String created >>>>>>>>>>>>>"+ db_connect_string);
							con = DriverManager.getConnection(db_connect_string);
							LOG.info("Azure Active Directory"+azureAuthenticationType+"connection established successfully");
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;

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
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Postgres")) {
						String[] dbAndSchema = database.split(",");
						String schema = "public";
						if (dbAndSchema.length > 1 && dbAndSchema[1].length() > 0) {
							schema = dbAndSchema[1];
						}

						String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0] + "?currentSchema=" + schema;

						if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
																		  
			  
							url = url + "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
						}

						try {
							Class.forName("org.postgresql.Driver");
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Teradata")) {

						String url = "jdbc:teradata://" + uri;
						try {
							Class.forName("com.teradata.jdbc.TeraDriver");
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Cassandra")) {

						try {
							CqlSessionBuilder builder = CqlSession.builder();
							builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port)))
									.withAuthCredentials(username, password);

							cassandraSession = builder.build();
							cassandraSession.close();
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("Hive") || schemaType.equalsIgnoreCase("Hive Kerberos")
							|| schemaType.equalsIgnoreCase("ClouderaHive")) {

						if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
								&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

							try {
								String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
								LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

								String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");

								boolean rempteIpEnbled = false;
								if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
									try {
										String proxy_uri = appDbConnectionProperties
												.getProperty("proxy_" + clusterPropertyCategory.trim());
										if (proxy_uri != null && !proxy_uri.isEmpty()) {
											rempteIpEnbled = true;
										} else
											LOG.debug(
													"Remote connection is not enabled for:" + clusterPropertyCategory);
									} catch (Exception e) {
										LOG.error(e.getMessage());
										LOG.debug(
												"Remote connection is not enabled for:" + clusterPropertyCategory);
									}

								}

								// String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
								if (rempteIpEnbled) {
									String publishUrl = connectionUtil.getRemoteClusterUrlByClusterCategory(
											clusterPropertyCategory, DatabuckConstants.DATA_CONNECTION_API);
									String token = remoteClusterAPIService
											.generateRemoteClusterAPIToken(clusterPropertyCategory);

									StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
									encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

									String encryptedToken = encryptor.encrypt(token);

									JSONObject inputObj = new JSONObject();

									inputObj.put("domainName", domainName);
									inputObj.put("uri", uri);
									inputObj.put("port", port);
									inputObj.put("database", database);
									inputObj.put("clusterCategory", clusterPropertyCategory);

									String inputJson = inputObj.toString();
									LOG.debug("inputJson=" + inputJson);

									boolean testConnStatus = remoteClusterAPIService
											.testConnectionByRemoteCluster(publishUrl, encryptedToken, inputJson);

									if (testConnStatus) {
										LOG.info("Success!");
										connectionStatus = true;
									} else {
										LOG.error("Failure!");
										connectionFailureMessage = "Connection Failure";
										flag = false;
									}
								} else
									LOG.debug("Given uri:" + uri + " is not configured for remote connection");
							} catch (Exception e) {
								LOG.error(e.getMessage());
								e.printStackTrace();
							}

						} else {
							try {
								String dbURL2 = "";
								Class.forName("org.apache.hive.jdbc.HiveDriver");
								// Hive Kerberos
								if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
									System.setProperty("java.security.auth.login.config", serviceName);
									System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
									System.setProperty("java.security.krb5.conf", krb5conf);
									Class.forName("org.apache.hive.jdbc.HiveDriver");

									if (uri.contains("2181") && jksPath != null && !jksPath.trim().isEmpty()) {
										dbURL2 = "jdbc:hive2://" + uri + "/" + database
												+ ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal="
												+ domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
												+ password;
									} else if (jksPath != null && !jksPath.trim().isEmpty()) {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
												+ domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
												+ password;
									} else {
										// dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
										// + domain;
										String zookeeperURL = hiveConnectionURL(uri, port, database);
										if (zookeeperURL != null)
											dbURL2 = "jdbc:hive2://" + zookeeperURL;
										else
											dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
													+ domain;
									}

									LOG.debug("**dbURL2:" + dbURL2);
									con = DriverManager.getConnection(dbURL2);
								} else {
									// ClouderaHive SSL enabled
									if (schemaType.equalsIgnoreCase("ClouderaHive") && sslEnb != null
											&& sslEnb.equalsIgnoreCase("Y")) {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database
												+ ";ssl=true;sslTrustStore=" + sslTrustStorePath
												+ ";trustStorePassword=" + trustPassword;
									}
									// Normal Hive connection
									else {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database;
									}
									con = DriverManager.getConnection(dbURL2, username, password);
								}
								connectionStatus = true;
							} catch (Exception e) {
								LOG.error(e.getMessage());
								flag = false;
								e.printStackTrace();
								connectionFailureException = e;
							}
						}
					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MapR Hive")) {
						try {
							String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
							ProcessBuilder processBuilder = new ProcessBuilder();
							if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
									&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

								LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

								String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");

								boolean rempteIpEnbled = false;
								if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
									try {
										String proxy_uri = appDbConnectionProperties
												.getProperty("proxy_" + clusterPropertyCategory.trim());
										if (proxy_uri != null && !proxy_uri.isEmpty()) {
											rempteIpEnbled = true;
										} else
											LOG.debug(
													"Remote connection is not enabled for:" + clusterPropertyCategory);
									} catch (Exception e) {
										LOG.error(e.getMessage());
										LOG.debug(
												"Remote connection is not enabled for:" + clusterPropertyCategory);
									}

								}

								// String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
								if (rempteIpEnbled) {
									String publishUrl = connectionUtil.getRemoteClusterUrlByClusterCategory(
											clusterPropertyCategory, DatabuckConstants.DATA_CONNECTION_API);
									String token = remoteClusterAPIService
											.generateRemoteClusterAPIToken(clusterPropertyCategory);

									StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
									encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

									String encryptedToken = encryptor.encrypt(token);

									JSONObject inputObj = new JSONObject();

									inputObj.put("domainName", domainName);
									inputObj.put("uri", uri);
									inputObj.put("port", port);
									inputObj.put("database", database);
									inputObj.put("clusterCategory", clusterPropertyCategory);

									String inputJson = inputObj.toString();
									LOG.debug("inputJson=" + inputJson);

									boolean testConnStatus = remoteClusterAPIService
											.testConnectionByRemoteCluster(publishUrl, encryptedToken, inputJson);

									if (testConnStatus) {
										LOG.info("Success!");
										connectionStatus = true;
									} else {
										LOG.error("Failure!");
										connectionFailureMessage = "Connection Failure";
										flag = false;
									}
								} else
									LOG.debug("Given uri:" + uri + " is not configured for remote connection");

							} else {

								String databuckHome = DatabuckUtility.getDatabuckHome();

								LOG.debug("DATABUCK_HOME:" + databuckHome);

								String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
										+ " com.databuck.mapr.hive.ValidateHiveConnection " + uri + ":" + port + " "
										+ database;
								LOG.debug(" shellCommand - " + shellCommand);
								processBuilder.command("bash", "-c", shellCommand);

								Process process = processBuilder.start();

								StringBuilder output = new StringBuilder();

								BufferedReader reader = new BufferedReader(
										new InputStreamReader(process.getInputStream()));

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
									LOG.info("Success!");
									connectionStatus = true;
									// System.exit(0);
								} else {
									// abnormal...
									LOG.error("Connection Failure!");
									flag = false;
									connectionFailureMessage = "Connection Failure";
								}
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
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
							con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database,
									props);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Oracle RAC")) {
						try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
							con = DriverManager.getConnection(uri, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MySQL")) {

						String url = "jdbc:mysql://" + uri + ":" + port+ "/" + database;
						try {
							Class.forName("com.mysql.jdbc.Driver");

							if (sslEnb != null && sslEnb.trim().equalsIgnoreCase("Y")) {
								url = "jdbc:mysql://" + uri + ":" + port + "/" + database
										+ "?verifyServerCertificate=false&useSSL=true";
							}
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
							if(e.getMessage().contains("Access denied for user")) {
								throw new Exception(credentialsFail);
							}else if(e.getMessage().contains("Communications link failure")) {
								throw new Exception(uriFail);
							}else {
								throw new Exception(dbFail);
							}
						}

					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MapR DB")) {

						String url = "jdbc:hive2://" + uri + ":" + port + "/" + database;
						LOG.debug("In MapR =" + url);

						try {
							Class.forName("org.apache.hive.jdbc.HiveDriver");
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}

				}
				// ----------- Added Hive (Knox) connection 4June2019
				if (flag) {
					if (schemaType.equalsIgnoreCase("Hive knox")) {

						/*
						 * connection URL has to be following:
						 *
						 * jdbc:hive2://<gateway-host>:<gateway-port>/?hive.server2.servermode=https;
						 * hive.server2.http.path=<gateway-path>/<cluster-name>/hive
						 *
						 */
						System.setProperty("javax.net.ssl.trustStore", jksPath);

						String url = hiveKnoxConnectionURL(uri, port, database, gatewayPath);

						LOG.debug("In Hive Knox =>" + url);

						try {
							// load Hive JDBC Driver
							Class.forName("org.apache.hive.jdbc.HiveDriver");

							// configure JDBC connection
							con = DriverManager.getConnection(url, username, password);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("SnowFlake")) {
							LOG.debug("database ====>" + database);
							LOG.debug("uri ==>" + uri);
							String[] dbAndSchema = database.split(",");
							if (dbAndSchema.length != 3) {
								flag = false;
								throw new Exception("Data Connection failed,Please provide valid database name");
							} else {
								String securemode = appDbConnectionProperties.getProperty("snowflake.securemode");
								if (securemode != null && securemode.trim().equalsIgnoreCase("Y")) {
									securemode = "Y";
								} else {
									securemode = "N";
								}

								String url = "";
								if (securemode.equalsIgnoreCase("Y")) {
									// url = "jdbc:snowflake://" + uri + "?insecureMode=true";
									url = "jdbc:snowflake://" + uri + "/?db=" + dbAndSchema[1] + "&warehouse="
											+ dbAndSchema[0] + "&schema=" + dbAndSchema[2] + "&insecureMode=true";
								} else {
									// url = "jdbc:snowflake://" + uri;
									url = "jdbc:snowflake://" + uri + "/?db=" + dbAndSchema[1] + "&warehouse="
											+ dbAndSchema[0] + "&schema=" + dbAndSchema[2];
								}
								LOG.debug(url);

								try {
									Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
									con = DriverManager.getConnection(url, username, password);
									connectionStatus = true;
								} catch (Exception e) {
									LOG.error(e.getMessage());
									flag = false;
									e.printStackTrace();
									connectionFailureException = e;
								}
							}
						}
					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("SnowFlake")) {
						LOG.debug("database ====>" + database);
						LOG.debug("uri ==>" + uri);

						String[] dbAndSchema = database.split(",");
						if (dbAndSchema.length != 3) {
							flag = false;
						} else {
							String securemode = appDbConnectionProperties.getProperty("snowflake.securemode");
							if (securemode != null && securemode.trim().equalsIgnoreCase("Y")) {
								securemode = "Y";
							} else {
								securemode = "N";
							}

							String url = "";

							if (securemode.equalsIgnoreCase("Y")) {
								// url = "jdbc:snowflake://" + uri + "?insecureMode=true";
								url = "jdbc:snowflake://" + uri + "/?db=" + dbAndSchema[1] + "&warehouse="
										+ dbAndSchema[0] + "&schema=" + dbAndSchema[2] + "&insecureMode=true";
							} else {
								// url = "jdbc:snowflake://" + uri;
								url = "jdbc:snowflake://" + uri + "/?db=" + dbAndSchema[1] + "&warehouse="
										+ dbAndSchema[0] + "&schema=" + dbAndSchema[2];
							}

							LOG.debug(url);

							try {
								Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
								con = DriverManager.getConnection(url, username, password);
								connectionStatus = true;
							} catch (Exception e) {
								LOG.error(e.getMessage());
								flag = false;
								e.printStackTrace();
								connectionFailureException = e;
							}
						}
					}
				}

				// ----------- Added FileSystem Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("FileSystem Batch")) {

						LOG.debug("\n====> FileSystem Batch details <====");

						LOG.debug("\n** FolderPath: " + folderPath);
						LOG.debug("\n** FileName Pattern: " + fileNamePattern);
						LOG.debug("\n** File Data Format: " + fileDataFormat);
						LOG.debug("\n** Header Present: " + headerPresent);
						LOG.debug("\n** Header File Path: " + headerFilePath);
						LOG.debug("\n** Header FileName Pattern: " + headerFileNamePattern);
						LOG.debug("\n** Header File Data Pattern: " + headerFileDataFormat);

						try {
							LOG.debug("\n====> Checking if the Folder Path exist ...");

							File f_folder = new File(folderPath);

							if (f_folder.exists() && f_folder.isDirectory()) {

								LOG.info("\n====> Folder Path exits is true !!");

								if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
									LOG.info("\n====> File has header, hence no header path checking !!");

									LOG.info("\n====> Data Connection successful !!");
									connectionStatus = true;
								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									File h_folder = new File(headerFilePath);

									if (h_folder.exists() && h_folder.isDirectory()) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");
										connectionStatus = true;
									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}

								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}

				// ----------- Added S3 Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("S3 Batch")) {

						LOG.info("\n====> S3 Batch details <====");

						// LOG.debug("\n** accessKey: " + accessKey);
						// LOG.debug("\n** secretKey: " + secretKey);
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
									connectionStatus = true;
								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									S3Object headerFolderPathKey = s3Client.getObject(bucketName, headerFilePath);

									if (headerFolderPathKey != null) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");
										connectionStatus = true;
									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}
								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				// ----------- Added S3 IAMRole Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")
							|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")) {

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

							LOG.debug("\n====> awsCliCommand: " + awsCliCommand);
							Process child = Runtime.getRuntime().exec(awsCliCommand);

							BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
							boolean folderPathExist = false;
							String line = null;

							// Get Files list
							while ((line = br.readLine()) != null) {
								LOG.debug("line: " + line);
								folderPathExist = true;
								break;
							}

							br.close();

							if (folderPathExist) {

								LOG.info("\n====> Folder Path exits is true !!");

								if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
									LOG.info("\n====> File has header, hence no header path checking !!");

									LOG.info("\n====> Data Connection successful !!");
									connectionStatus = true;
								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									headerFilePath = headerFilePath.replace("//", "/");

									if (!headerFilePath.trim().isEmpty() && !headerFilePath.endsWith("/")) {
										headerFilePath = headerFilePath + "/";
									}

									String headerFileFullPath = (bucketName + "/" + headerFilePath).replace("//", "/");
									headerFileFullPath = "s3://" + headerFileFullPath;

									String h_awsCliCommand = "aws s3 ls " + headerFileFullPath;

									LOG.debug("\n====> h_awsCliCommand: " + h_awsCliCommand);
									Process hchild = Runtime.getRuntime().exec(h_awsCliCommand);

									BufferedReader hbr = new BufferedReader(
											new InputStreamReader(hchild.getInputStream()));
									boolean headerFolderPathExist = false;
									String h_line = null;

									// Get Files list
									while ((h_line = hbr.readLine()) != null) {
										LOG.debug("line: " + h_line);
										headerFolderPathExist = true;
										break;
									}
									hbr.close();

									if (headerFolderPathExist) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");
										connectionStatus = true;
									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}
								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				// ----------- Added Big Query connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("BigQuery")) {
						LOG.info("\n=====> Checking BigQuery Connection ...");
						try {
							BigQuery bigQuery = bigQueryConnection.getBigQueryConnection(bigQueryProjectName,
									privatekeyId, privatekey, clientId, clientEmail, datasetName);

							if (bigQuery != null) {
								// Check if tables present in dataset
								List<String> tablesList = bigQueryConnection.getListOfTableNamesFromBigQuery(
										bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail,
										datasetName);
								if (tablesList != null && tablesList.size() > 0) {
									connectionStatus = true;
									flag = true;
								} else {
									LOG.error("\n====> Failed to connect to Dataset or no tables found!!");
									flag = false;
									connectionFailureMessage = "Failed to connect to Dataset or no tables found";
								}
							} else {
								flag = false;
								connectionFailureMessage = "BigQuery Connection Failed";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}
				// ----------- Added Azure Data Lake Storage Gen1 connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
						LOG.info("\n=====> Checking AzureDataLakeStorageGen1 Connection ...");
						uri = azureServiceURI;
						if (null == azureFilePath || azureFilePath == "") {
							azureFilePath = "/";
						}

						AccessTokenProvider provider = new ClientCredsTokenProvider(
								"https://login.microsoftonline.com/" + azureTenantId + "/oauth2/token", azureClientId,
								azureClientSecret);
						AzureADToken azureADToken = null;
						try {
							azureADToken = provider.getToken();
							String accessToken = azureADToken.accessToken;
							LOG.debug("accessToken : " + accessToken);
							connectionStatus = true;
						} catch (IOException e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}
				// ----------- Added Azure Data Lake Storage Gen2 Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
						connectionStatus = azureDataLakeGen2Connection.validateConnection(accessKey, secretKey, bucketName,
								folderPath);
					}
				}

				// ---------------Cosmos DB connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureCosmosDB")) {

						connectionStatus = azureCosmosConnection.validateConnectionCosmos(uri, port, secretKey, database);
					}
				}

				//--------------Added Databricks Delta Lake Connection

				if (flag) {
					if (schemaType.equalsIgnoreCase("DatabricksDeltaLake")) {
						String url = "jdbc:databricks://" + uri + ":" + port + ";" + "HttpPath=" + httpPath;

						LOG.debug("url........." + url);
						Properties properties = new Properties();

						properties.put("username", username);
						properties.put("PWD", password);
						try {
							Class.forName("com.databricks.client.jdbc.Driver");
							con = DriverManager.getConnection(url, properties);
							connectionStatus = true;
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				// Closing the connection
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
					connectionFailureException = e;
				}

				if (connectionFailureException != null)
					mappingErrors.put((DebugUtil.getInitialExceptionDetails(connectionFailureException)));
				else if (connectionFailureMessage != null)
					mappingErrors.put(connectionFailureMessage);

				if (connectionStatus) {
					if (database == null)
						database = "";
					int axs = 0;
					idDataSchema = schemaDAOI.saveDataIntoListDataSchema(actual_uri, database, actual_username,
							actual_password, actual_port, schemaName, schemaType, domain, serviceName, krb5conf,
							autoGenerateId, suffix, prefix, hivejdbchost, hivejdbcport, sslEnb, sslTrustStorePath,
							trustPassword, gatewayPath, jksPath, zookeeperUrl, folderPath, fileNamePattern,
							fileDataFormat, headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat,
							projectId, createdByUser, accessKey, secretKey, bucketName, bigQueryProjectName,
							privatekeyId, privatekey, clientId, clientEmail, datasetName, azureClientId,
							azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, partitionedFolders,
							enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
							maxFolderDepth, fileEncrypted, domainId, singleFile, externalFileNamePatternId,
							externalFileName, patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath,
							kmsAuthDisabled, readLatestPartition, alation_integration_enabled,
							incrementalDataReadEnabled, clusterPropertyCategory, multiFolderEnabled,
							pushDownQueryEnabled,httpPath,clusterPolicyId,azureAuthenticationType);

					LOG.debug("idDataSchema=" + idDataSchema);

					if (autoGenerateId.equalsIgnoreCase("Y")) {
						if (schemaType.equalsIgnoreCase("Oracle RAC")) {
							schemaDAOI.insertDataIntoHiveSourceForAutoGenerateStatus(idDataSchema);
							schemaDAOI.processAutoGenerateTemplate_oracleRAC(actual_uri, database, actual_username,
									actual_password, actual_port, schemaName, schemaType, domain, serviceName, krb5conf,
									autoGenerateId, idUser, idDataSchema, projectId, domainId);
						}
					}

					if (enableFileMonitoring != null && enableFileMonitoring.equalsIgnoreCase("Y")) {
						createFileMonitoringValidation(idDataSchema);
					}

					// Saving AlertNotificationLog to database
					integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
							TaskTypes.connection, AlertManagement.Connection_Creation_Success, "success", createdByUser,
							mappingErrors);

				} else {
					LOG.error("Connection failed");
					if(!connectionStatus) {
						throw new Exception("Please enter valid connection details.");
					}
					integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
							TaskTypes.connection, AlertManagement.Connection_Creation_Failure, "failed", createdByUser,
							mappingErrors);
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			mappingErrors.put((DebugUtil.getInitialExceptionDetails(e)));
			integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
					TaskTypes.connection, AlertManagement.Connection_Creation_Failure, "failed", createdByUser,
					mappingErrors);
			throw new Exception(e.getMessage());
		}
		return idDataSchema;
	}

	public String hiveConnectionURL(String hosts, String port, String domain) {
		String zookeeperURL = null;

		// Check if the server has commad seperated values to identify if it is a
		// zookeeper connection
		if (hosts.contains(",") || port.equalsIgnoreCase("2181")) {
			String parts[] = hosts.split("\\,");
			System.out.print(parts);
			for (String part : parts) {
				LOG.info(" part");
				LOG.debug(part);
				if (zookeeperURL != null)
					zookeeperURL = zookeeperURL + "," + part + ":2181";
				else
					zookeeperURL = part + ":2181";
			}
		}

		if (zookeeperURL != null)
			zookeeperURL = zookeeperURL + "/" + domain
					+ ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";

		LOG.info(" zookeeperURL");
		LOG.debug(zookeeperURL);
		return zookeeperURL;
	}

	public String hiveKnoxConnectionURL(String hosts, String port, String database, String gatewayPath) {
		String knoxUrl = "jdbc:hive2://" + hosts + ":" + port + "/" + database
				+ ";ssl=true;transportMode=http;httpPath=" + gatewayPath;
		return knoxUrl;
	}

	public void createFileMonitoringValidation(long idDataSchema) {
		try {
			List<ListDataSchema> listDataSchemaList = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);

			Date dt = new Date();

			if (listDataSchemaList != null && listDataSchemaList.size() > 0) {
				ListDataSchema listDataSchema = listDataSchemaList.get(0);

				String schemaType = listDataSchema.getSchemaType();

				if (schemaType.equalsIgnoreCase("S3 IAMRole Batch") || schemaType.equalsIgnoreCase("S3 Batch")
						|| schemaType.equalsIgnoreCase("FileSystem Batch")
						|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")
						|| schemaType.equalsIgnoreCase("SnowFlake")
						|| schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")
						|| schemaType.equalsIgnoreCase("DatabricksDeltaLake")) {

					String fileMonitoringType = "";
					if (schemaType.equalsIgnoreCase("S3 IAMRole Batch"))
						fileMonitoringType = "s3iamrole";
					else if (schemaType.equalsIgnoreCase("S3 IAMRole Batch Config"))
						fileMonitoringType = "s3iamroleconfig";
					else if (schemaType.equalsIgnoreCase("S3 Batch"))
						fileMonitoringType = "s3";
					else if (schemaType.equalsIgnoreCase("FileSystem Batch"))
						fileMonitoringType = "local";
					else if (schemaType.equalsIgnoreCase("SnowFlake"))
						fileMonitoringType = "snowflake";
					else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch"))
						fileMonitoringType = "azuredatalakestoragegen2batch";
					else if(schemaType.trim().equalsIgnoreCase("DatabricksDeltaLake"))
						fileMonitoringType = "databricksdeltalake";

					// Fetch latest idData from listDataSources
					long idData = validationCheckDao.getlatestListDataSourceIdData();

					// Create FileMonitoring validation
					String fmValidationName = "File_Monitoring_" + listDataSchema.getSchemaName();
					long fmIdApp = validationCheckDao.insertintolistapplications(fmValidationName, "",
							"File Monitoring", idData, listDataSchema.getCreatedBy(), 0.0, "N", null, null,
							fileMonitoringType, "Y", listDataSchema.getProjectId(), listDataSchema.getCreatedByUser(),
							listDataSchema.getDomainId());
					validationCheckDao.updateApplicationNameWithIdApp(fmValidationName, fmIdApp);
					// Following if block will be removed, when S3 IAMRole Batch , S3 Batch,S3
					// IAMRole Batch Config is removed.
					// Currently following if block is used to create file monitoring validation for
					// 'aws s3' and making entry in the fm_Connection_Details.
					if (schemaType.equalsIgnoreCase("S3 IAMRole Batch") || schemaType.equalsIgnoreCase("S3 Batch")
							|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")) {
						fileMonitoringType = "aws s3";
						fmValidationName = "Lambda_" + fmValidationName;
						long s3FMIdApp = validationCheckDao.insertintolistapplications(fmValidationName, "",
								"File Monitoring", idData, listDataSchema.getCreatedBy(), 0.0, "N", null, null,
								fileMonitoringType, "Y", listDataSchema.getProjectId(),
								listDataSchema.getCreatedByUser(), listDataSchema.getDomainId());
						validationCheckDao.updateApplicationNameWithIdApp(fmValidationName, s3FMIdApp);
						validationCheckDao.insertFMConnectionDetails(s3FMIdApp, idDataSchema);
					} else {
						validationCheckDao.insertFMConnectionDetails(fmIdApp, idDataSchema);
					}
					LOG.debug("\n====> FileMonitoring validation Id:[" + fmIdApp + "] and name: ["
							+ fmValidationName + "]");
					String folderpath = (schemaType.equalsIgnoreCase("SnowFlake")) ? listDataSchema.getDatabaseSchema()
							: listDataSchema.getFolderPath();
					if (fmIdApp != 0l) {
						// Add the fileMonitoring rule
						FileMonitorRules fileMonitorRules = new FileMonitorRules();
						fileMonitorRules.setBucketName(listDataSchema.getBucketName());
						fileMonitorRules.setFolderPath(folderpath);
						fileMonitorRules.setFilePattern(listDataSchema.getFileNamePattern());
						fileMonitorRules.setPartitionedFolders(listDataSchema.getPartitionedFolders());
						fileMonitorRules.setMaxFolderDepth(listDataSchema.getMaxFolderDepth());
						fileMonitorRules.setFileCount(1);
						fileMonitorRules.setFileSizeThreshold(5);
						fileMonitorRules.setFrequency("Daily");
						fileMonitorRules.setIdDataSchema(idDataSchema);
						fileMonitorRules.setLastProcessedDate(dt);
						fileMonitorRules.setDayOfCheck(1);
						fileMonitorRules.setTimeOfCheck("23:00");
						fileMonitorRules.setIdApp(Long.valueOf(fmIdApp).intValue());
						fileMonitorDao.addMonitorRule(fileMonitorRules);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while creating fileMonitoring validation !!");
			e.printStackTrace();
		}
	}

	public JSONObject runSchemaJob(Long idDataSchema, String healthCheck) {

		JSONObject json = new JSONObject();
		String resultStatus = "failed";
		String resultMessage = "";

		try {
			if (idDataSchema != null && idDataSchema > 0l) {

				// Check if schema is already in queued or started or in progress
				boolean status = iTaskDAO.checkIfSchemaJobInQueue(idDataSchema);
				if (status) {
					resultMessage = "Schema is already in Queue";
				} else {
					status = iTaskDAO.checkIfSchemaJobInProgress(idDataSchema);
					if (status) {
						resultMessage = "Schema is already in started / in progress";
					} else {

						resultMessage = "Schema placed in queue successfully";

						if (healthCheck != null && !healthCheck.trim().isEmpty()
								&& healthCheck.trim().equalsIgnoreCase("Y")) {

							LOG.debug(
									"\n====> Health Check Job is initiated for schema with id[" + idDataSchema + "]");
							// resultMessage = "Health Check Job is initiated";
							// mamta 9/9/2022
							resultMessage = "Trust Check Job is initiated";

						} else
							healthCheck = "N";

						String schemaJobUniqueId = iTaskDAO.addSchemaJobToQueue(idDataSchema, healthCheck);
						resultStatus = "success";
						json.put("schemaJobUniqueId", schemaJobUniqueId);
					}
				}

			} else
				resultMessage = "Invalid Schema";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			resultMessage = "Schema job could not be run";
		}

		json.put("status", resultStatus);
		json.put("message", resultMessage);
		return json;
	}

	public void enableOrDisableFileMonitoringForConnection(long idDataSchema, String enableFileMonitoring,
			String enableFileMonitoring_Old) {
		LOG.debug("enableFileMonitoringOld=" + enableFileMonitoring_Old);
		List<Long> idApplist = schemaDAOI.getValidationsForFMConnection(idDataSchema);
		if (enableFileMonitoring_Old.equalsIgnoreCase("Y") && enableFileMonitoring.equalsIgnoreCase("N")) {
			String activeFlagStatus = "no";
			schemaDAOI.activateOrDeactivateFMValidations(idApplist, activeFlagStatus);
		} else if (enableFileMonitoring_Old.equalsIgnoreCase("N") && enableFileMonitoring.equalsIgnoreCase("Y")) {
			if (idApplist.size() > 0) {
				String activeFlagStatus = "yes";
				schemaDAOI.activateOrDeactivateFMValidations(idApplist, activeFlagStatus);
			} else
				createFileMonitoringValidation(idDataSchema);
		}
	}

	public List<ListDataSchema> getConnections(Map<String, String> params) throws Exception {
		if (!params.containsKey("projectIds") || !params.containsKey("fromDate") || !params.containsKey("toDate")) {
			throw new Exception("Project id or fromDate or toDate is missing in the request body");
		}
		return schemaDAOI.getListDataConnections(params.get("projectIds"), params.get("fromDate"),
				params.get("toDate"));
	}
	
	public List<ListDataSchema> getConnectionsbyDomain(Map<String, String> params) throws Exception {
		if (!params.containsKey("projectIds") || !params.containsKey("fromDate") || !params.containsKey("toDate") || !params.containsKey("domainId")) {
			throw new Exception("Project id or Domain id or fromDate or toDate is missing in the request body");
		}
		return schemaDAOI.getListDataConnectionsbyDomain(params.get("projectIds"), params.get("fromDate"),
				params.get("toDate"), params.get("domainId"));
	}

	// new method created by vishwajit

	public Map<String, Object> saveConnectionInfoNew(Long projectId, Integer domainId, Long idUser,
			String createdByUser, String schemaName, String schemaType, String uri, String database, String username,
			String password, String port, String domain, String serviceName, String krb5conf, String autoGenerateId,
			String hivejdbchost, String hivejdbcport, String suffix, String prefix, String sslEnb,
			String sslTrustStorePath, String trustPassword, String gatewayPath, String jksPath, String zookeeperUrl,
			String folderPath, String fileNamePattern, String fileDataFormat, String headerPresent,
			String partitionedFolders, String headerFilePath, String headerFileNamePattern, String headerFileDataFormat,
			String accessKey, String secretKey, String bucketName, String bigQueryProjectName, String privatekeyId,
			String privatekey, String clientId, String clientEmail, String datasetName, String azureClientId,
			String azureClientSecret, String azureTenantId, String azureServiceURI, String azureFilePath,
			String enableFileMonitoring, String multiPattern, int startingUniqueCharCount, int endingUniqueCharCount,
			int maxFolderDepth, String fileEncrypted, String singleFile, String externalFileNamePatternId,
			String externalFileName, String patternColumn, String headerColumn, String localDirectoryColumnIndex,
			String xsltFolderPath, String kmsAuthDisabled, String readLatestPartition,
			String alation_integration_enabled, String incrementalDataReadEnabled, String clusterPropertyCategory,
			String multiFolderEnabled, String pushDownQueryEnabled,String azureAuthenticationType) {

		long idDataSchema = 0l;
		LOG.info("Inside saveConnectionInfo");
		String connectionFailureMessage = null;
		Exception connectionFailureException = null;
		JSONArray mappingErrors = new JSONArray();

		String failureMessage = null;
		String databaseFailure = "Unknown database";
		String credentialsFailure = "Access denied for user";
		String hostAndPortFailure = "Communications link failure";
		String response = null;

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// if allation is null, making it 'N'
			if (alation_integration_enabled == null || alation_integration_enabled.trim().isEmpty())
				alation_integration_enabled = "N";

			// if not Mapr Hive readLatestPartition should be 'N'
			if (readLatestPartition == null || readLatestPartition.trim().isEmpty()
					|| !readLatestPartition.trim().equalsIgnoreCase("Y") || !schemaType.equalsIgnoreCase("MapR Hive")) {
				readLatestPartition = "N";
			}

			if (schemaType.equalsIgnoreCase("oracle")) {
				port = port + "/" + serviceName;
			}

			Connection con = null;
			CqlSession cassandraSession = null;
			String name = null;
			boolean flag = true;

			/*
			 * When logon manager is enabled user details coming from UI are KMS keys these
			 * will be saved in databuck, but the details used used to establish connection
			 * comes from logon manager
			 */
			String actual_uri = uri;
			String actual_port = port;
			String actual_username = username;
			String actual_password = password;

			if (name == null || name == "") {

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
							port = (schemaType.equalsIgnoreCase("Oracle"))
									? conn_user_details.get("port") + "/" + serviceName
									: conn_user_details.get("port");
							username = conn_user_details.get("username");
							password = conn_user_details.get("password");
						} else
							flag = false;

					} else {
						LOG.debug("\n====>KMS Authentication is not supported for [" + schemaType + "] !!");
						flag = false;
					}

					if (!flag) {
						LOG.error("\n====>Failed to fetch connection details from logon manager !!");
						connectionFailureMessage = "Failed to fetch connection details from logon manager";
					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("oracle")) {
						String url = "jdbc:oracle:thin:@" + uri + ":" + port;
						try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
							con = DriverManager.getConnection(url, username, password);

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							LOG.debug("con=" + con);
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("MSSQL")) {
						int dotPosition = database.indexOf(".");
						String database1 = database;
						if (dotPosition != -1) {
							database1 = database.substring(0, dotPosition);
						}
						String url = "jdbc:sqlserver://" + uri + ":" + port +"/" + database1+ ";encrypt=true;trustServerCertificate=true;";
						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;

							failureMessage = e.getMessage();
						}

					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureSynapseMSSQL")) {

						String url = "jdbc:sqlserver://" + uri + ":" + port + ";DatabaseName=" + database;

						try {
							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
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
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;

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
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Postgres")) {
						String[] dbAndSchema = database.split(",");

						String url = "jdbc:postgresql://" + uri + ":" + port + "/" + dbAndSchema[0];
						if (dbAndSchema.length > 1 && dbAndSchema[1].length() > 0) {
							url = url + "?currentSchema=" + dbAndSchema[1]
									+ "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
						} else {
							url = url + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
						}
						try {
							Class.forName("org.postgresql.Driver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Teradata")) {

						String url = "jdbc:teradata://" + uri;
						try {
							Class.forName("com.teradata.jdbc.TeraDriver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Cassandra")) {

						try {
							CqlSessionBuilder builder = CqlSession.builder();
							builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port)))
									.withAuthCredentials(username, password);

							cassandraSession = builder.build();
							cassandraSession.close();
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("Hive") || schemaType.equalsIgnoreCase("Hive Kerberos")
							|| schemaType.equalsIgnoreCase("ClouderaHive")) {

						if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
								&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

							try {
								String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
								LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

								String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");

								boolean rempteIpEnbled = false;
								if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
									try {
										String proxy_uri = appDbConnectionProperties
												.getProperty("proxy_" + clusterPropertyCategory.trim());
										if (proxy_uri != null && !proxy_uri.isEmpty()) {
											rempteIpEnbled = true;
										} else
											LOG.debug(
													"Remote connection is not enabled for:" + clusterPropertyCategory);
									} catch (Exception e) {
										LOG.error(e.getMessage());
										LOG.debug(
												"Remote connection is not enabled for:" + clusterPropertyCategory);
									}

								}

								// String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
								if (rempteIpEnbled) {
									String publishUrl = connectionUtil.getRemoteClusterUrlByClusterCategory(
											clusterPropertyCategory, DatabuckConstants.DATA_CONNECTION_API);
									String token = remoteClusterAPIService
											.generateRemoteClusterAPIToken(clusterPropertyCategory);

									StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
									encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

									String encryptedToken = encryptor.encrypt(token);

									JSONObject inputObj = new JSONObject();

									inputObj.put("domainName", domainName);
									inputObj.put("uri", uri);
									inputObj.put("port", port);
									inputObj.put("database", database);
									inputObj.put("clusterCategory", clusterPropertyCategory);

									String inputJson = inputObj.toString();
									LOG.debug("inputJson=" + inputJson);

									boolean testConnStatus = remoteClusterAPIService
											.testConnectionByRemoteCluster(publishUrl, encryptedToken, inputJson);

									if (testConnStatus) {
										LOG.info("Success!");
									} else {
										LOG.error("Failure!");
										connectionFailureMessage = "Connection Failure";
										flag = false;
									}
								} else
									LOG.debug("Given uri:" + uri + " is not configured for remote connection");
							} catch (Exception e) {
								LOG.error(e.getMessage());
								e.printStackTrace();
							}

						} else {
							try {
								String dbURL2 = "";
								Class.forName("org.apache.hive.jdbc.HiveDriver");
								// Hive Kerberos
								if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
									System.setProperty("java.security.auth.login.config", serviceName);
									System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
									System.setProperty("java.security.krb5.conf", krb5conf);
									Class.forName("org.apache.hive.jdbc.HiveDriver");

									if (uri.contains("2181") && jksPath != null && !jksPath.trim().isEmpty()) {
										dbURL2 = "jdbc:hive2://" + uri + "/" + database
												+ ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal="
												+ domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
												+ password;
									} else if (jksPath != null && !jksPath.trim().isEmpty()) {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
												+ domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
												+ password;
									} else {
										// dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
										// + domain;
										String zookeeperURL = hiveConnectionURL(uri, port, database);
										if (zookeeperURL != null)
											dbURL2 = "jdbc:hive2://" + zookeeperURL;
										else
											dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal="
													+ domain;
									}

									LOG.debug("**dbURL2:" + dbURL2);
									con = DriverManager.getConnection(dbURL2);
								} else {
									// ClouderaHive SSL enabled
									if (schemaType.equalsIgnoreCase("ClouderaHive") && sslEnb != null
											&& sslEnb.equalsIgnoreCase("Y")) {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database
												+ ";ssl=true;sslTrustStore=" + sslTrustStorePath
												+ ";trustStorePassword=" + trustPassword;
									}
									// Normal Hive connection
									else {
										dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database;
									}
									con = DriverManager.getConnection(dbURL2, username, password);
								}

							} catch (Exception e) {
								LOG.error(e.getMessage());
								flag = false;
								e.printStackTrace();
								connectionFailureException = e;
							}
						}
					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MapR Hive")) {
						try {
							String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
							ProcessBuilder processBuilder = new ProcessBuilder();
							if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
									&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

								LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

								String proxyConnectionList = appDbConnectionProperties.getProperty("proxy_connections");

								boolean rempteIpEnbled = false;
								if (proxyConnectionList.contains(clusterPropertyCategory.trim())) {
									try {
										String proxy_uri = appDbConnectionProperties
												.getProperty("proxy_" + clusterPropertyCategory.trim());
										if (proxy_uri != null && !proxy_uri.isEmpty()) {
											rempteIpEnbled = true;
										} else
											LOG.debug(
													"Remote connection is not enabled for:" + clusterPropertyCategory);
									} catch (Exception e) {
										LOG.error(e.getMessage());
										LOG.debug(
												"Remote connection is not enabled for:" + clusterPropertyCategory);
									}

								}

								// String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
								if (rempteIpEnbled) {
									String publishUrl = connectionUtil.getRemoteClusterUrlByClusterCategory(
											clusterPropertyCategory, DatabuckConstants.DATA_CONNECTION_API);
									String token = remoteClusterAPIService
											.generateRemoteClusterAPIToken(clusterPropertyCategory);

									StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
									encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

									String encryptedToken = encryptor.encrypt(token);

									JSONObject inputObj = new JSONObject();

									inputObj.put("domainName", domainName);
									inputObj.put("uri", uri);
									inputObj.put("port", port);
									inputObj.put("database", database);
									inputObj.put("clusterCategory", clusterPropertyCategory);

									String inputJson = inputObj.toString();
									LOG.debug("inputJson=" + inputJson);

									boolean testConnStatus = remoteClusterAPIService
											.testConnectionByRemoteCluster(publishUrl, encryptedToken, inputJson);

									if (testConnStatus) {
										LOG.info("Success!");
									} else {
										LOG.error("Failure!");
										connectionFailureMessage = "Connection Failure";
										flag = false;
									}
								} else
									LOG.debug("Given uri:" + uri + " is not configured for remote connection");

							} else {

								String databuckHome = DatabuckUtility.getDatabuckHome();

								LOG.debug("DATABUCK_HOME:" + databuckHome);

								String shellCommand = "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
										+ " com.databuck.mapr.hive.ValidateHiveConnection " + uri + ":" + port + " "
										+ database;
								LOG.debug(" shellCommand - " + shellCommand);
								processBuilder.command("bash", "-c", shellCommand);

								Process process = processBuilder.start();

								StringBuilder output = new StringBuilder();

								BufferedReader reader = new BufferedReader(
										new InputStreamReader(process.getInputStream()));

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
									LOG.info("Success!");

									// System.exit(0);
								} else {
									// abnormal...
									LOG.error("Connection Failure!");
									flag = false;
									connectionFailureMessage = "Connection Failure";
								}
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
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
							con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database,
									props);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}
				if (flag) {
					if (schemaType.equalsIgnoreCase("Oracle RAC")) {
						try {
							Class.forName("oracle.jdbc.driver.OracleDriver");
							con = DriverManager.getConnection(uri, username, password);

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MySQL")) {

						// String url = "jdbc:mysql://" + uri + ":" + port;
						String url = "jdbc:mysql://" + uri + ":" + port + "/" + database;
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
							connectionFailureException = e;

							failureMessage = e.getMessage();

						}

					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("MapR DB")) {

						String url = "jdbc:hive2://" + uri + ":" + port + "/" + database;
						LOG.debug("In MapR =" + url);

						try {
							Class.forName("org.apache.hive.jdbc.HiveDriver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}

				}
				// ----------- Added Hive (Knox) connection 4June2019
				if (flag) {
					if (schemaType.equalsIgnoreCase("Hive knox")) {

						/*
						 * connection URL has to be following:
						 *
						 * jdbc:hive2://<gateway-host>:<gateway-port>/?hive.server2.servermode=https;
						 * hive.server2.http.path=<gateway-path>/<cluster-name>/hive
						 *
						 */
						System.setProperty("javax.net.ssl.trustStore", jksPath);

						String url = hiveKnoxConnectionURL(uri, port, database, gatewayPath);

						LOG.debug("In Hive Knox =>" + url);

						try {
							// load Hive JDBC Driver
							Class.forName("org.apache.hive.jdbc.HiveDriver");

							// configure JDBC connection
							con = DriverManager.getConnection(url, username, password);

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
					if (flag) {
						if (schemaType.equalsIgnoreCase("SnowFlake")) {
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
								url = "jdbc:snowflake://" + uri + "?insecureMode=true&db=" + database;
							} else {
								url = "jdbc:snowflake://" + uri + "?db=" + database;
							}
							LOG.debug(url);

							try {
								Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
								con = DriverManager.getConnection(url, username, password);
							} catch (Exception e) {
								LOG.error(e.getMessage());
								flag = false;
								e.printStackTrace();
								connectionFailureException = e;

								failureMessage = e.getMessage();
							}
						}
					}

				}

				if (flag) {
					if (schemaType.equalsIgnoreCase("SnowFlake")) {
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
							url = "jdbc:snowflake://" + uri + "?insecureMode=true&db=" + database;
						} else {
							url = "jdbc:snowflake://" + uri + "?db=" + database;
						}

						LOG.debug(url);

						try {
							Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
							con = DriverManager.getConnection(url, username, password);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;

							failureMessage = e.getMessage();
						}
					}
				}

				// ----------- Added FileSystem Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("FileSystem Batch")) {

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

							if (f_folder.exists() && f_folder.isDirectory()) {

								LOG.info("\n====> Folder Path exits is true !!");

								if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
									LOG.info("\n====> File has header, hence no header path checking !!");

									LOG.info("\n====> Data Connection successful !!");

								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									File h_folder = new File(headerFilePath);

									if (h_folder.exists() && h_folder.isDirectory()) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");

									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}

								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}
					}
				}

				// ----------- Added S3 Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("S3 Batch")) {

						LOG.info("\n====> S3 Batch details <====");

						// LOG.debug("\n** accessKey: " + accessKey);
						// LOG.debug("\n** secretKey: " + secretKey);
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

								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									S3Object headerFolderPathKey = s3Client.getObject(bucketName, headerFilePath);

									if (headerFolderPathKey != null) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");

									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}
								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				// ----------- Added S3 IAMRole Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")
							|| schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")) {

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

							LOG.debug("\n====> awsCliCommand: " + awsCliCommand);
							Process child = Runtime.getRuntime().exec(awsCliCommand);

							BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
							boolean folderPathExist = false;
							String line = null;

							// Get Files list
							while ((line = br.readLine()) != null) {
								LOG.debug("line: " + line);
								folderPathExist = true;
								break;
							}

							br.close();

							if (folderPathExist) {

								LOG.info("\n====> Folder Path exits is true !!");

								if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
									LOG.info("\n====> File has header, hence no header path checking !!");

									LOG.info("\n====> Data Connection successful !!");

								} else {
									LOG.info("\n====> Checking if Header Path exist ...");

									headerFilePath = headerFilePath.replace("//", "/");

									if (!headerFilePath.trim().isEmpty() && !headerFilePath.endsWith("/")) {
										headerFilePath = headerFilePath + "/";
									}

									String headerFileFullPath = (bucketName + "/" + headerFilePath).replace("//", "/");
									headerFileFullPath = "s3://" + headerFileFullPath;

									String h_awsCliCommand = "aws s3 ls " + headerFileFullPath;

									LOG.debug("\n====> h_awsCliCommand: " + h_awsCliCommand);
									Process hchild = Runtime.getRuntime().exec(h_awsCliCommand);

									BufferedReader hbr = new BufferedReader(
											new InputStreamReader(hchild.getInputStream()));
									boolean headerFolderPathExist = false;
									String h_line = null;

									// Get Files list
									while ((h_line = hbr.readLine()) != null) {
										LOG.debug("line: " + h_line);
										headerFolderPathExist = true;
										break;
									}
									hbr.close();

									if (headerFolderPathExist) {
										LOG.info("\n====> Header Path exits is true !!");

										LOG.info("\n====> Data Connection successful !!");

									} else {
										LOG.error("\n====> Header Path does not exist !!");

										LOG.error("\n====> Data Connection failed !!");
										flag = false;
										connectionFailureMessage = "Connection Failure. Header Path does not exist";
									}
								}

							} else {
								LOG.error("\n====> Folder Path does not exist !!");

								LOG.error("\n====> Data Connection failed !!");
								flag = false;
								connectionFailureMessage = "Connection Failure. Folder Path does not exist";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}

				// ----------- Added Big Query connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("BigQuery")) {
						LOG.info("\n=====> Checking BigQuery Connection ...");
						try {
							BigQuery bigQuery = bigQueryConnection.getBigQueryConnection(bigQueryProjectName,
									privatekeyId, privatekey, clientId, clientEmail, datasetName);

							if (bigQuery != null) {
								// Check if tables present in dataset
								List<String> tablesList = bigQueryConnection.getListOfTableNamesFromBigQuery(
										bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail,
										datasetName);
								if (tablesList != null && tablesList.size() > 0) {
									flag = true;
								} else {
									LOG.error("\n====> Failed to connect to Dataset or no tables found!!");
									flag = false;
									connectionFailureMessage = "Failed to connect to Dataset or no tables found";
								}
							} else {
								flag = false;
								connectionFailureMessage = "BigQuery Connection Failed";
							}

						} catch (Exception e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}
				// ----------- Added Azure Data Lake Storage Gen1 connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
						LOG.info("\n=====> Checking AzureDataLakeStorageGen1 Connection ...");
						uri = azureServiceURI;
						if (null == azureFilePath || azureFilePath == "") {
							azureFilePath = "/";
						}

						AccessTokenProvider provider = new ClientCredsTokenProvider(
								"https://login.microsoftonline.com/" + azureTenantId + "/oauth2/token", azureClientId,
								azureClientSecret);
						AzureADToken azureADToken = null;
						try {
							azureADToken = provider.getToken();
							String accessToken = azureADToken.accessToken;
							LOG.debug("accessToken : " + accessToken);
						} catch (IOException e) {
							LOG.error(e.getMessage());
							flag = false;
							e.printStackTrace();
							connectionFailureException = e;
						}

					}
				}
				// ----------- Added Azure Data Lake Storage Gen2 Batch connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
						flag = azureDataLakeGen2Connection.validateConnection(accessKey, secretKey, bucketName,
								folderPath);
					}
				}

				// ---------------Cosmos DB connection
				if (flag) {
					if (schemaType.equalsIgnoreCase("AzureCosmosDB")) {

						flag = azureCosmosConnection.validateConnectionCosmos(uri, port, secretKey, database);
					}
				}
				// Closing the connection
				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
					connectionFailureException = e;
				}

				if (connectionFailureException != null)
					mappingErrors.put((DebugUtil.getInitialExceptionDetails(connectionFailureException)));
				else if (connectionFailureMessage != null)
					mappingErrors.put(connectionFailureMessage);

				if (flag) {
					if (database == null)
						database = "";
					int axs = 0;
					idDataSchema = schemaDAOI.saveDataIntoListDataSchema(actual_uri, database, actual_username,
							actual_password, actual_port, schemaName, schemaType, domain, serviceName, krb5conf,
							autoGenerateId, suffix, prefix, hivejdbchost, hivejdbcport, sslEnb, sslTrustStorePath,
							trustPassword, gatewayPath, jksPath, zookeeperUrl, folderPath, fileNamePattern,
							fileDataFormat, headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat,
							projectId, createdByUser, accessKey, secretKey, bucketName, bigQueryProjectName,
							privatekeyId, privatekey, clientId, clientEmail, datasetName, azureClientId,
							azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, partitionedFolders,
							enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
							maxFolderDepth, fileEncrypted, domainId, singleFile, externalFileNamePatternId,
							externalFileName, patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath,
							kmsAuthDisabled, readLatestPartition, alation_integration_enabled,
							incrementalDataReadEnabled, clusterPropertyCategory, multiFolderEnabled,
							pushDownQueryEnabled,"","",azureAuthenticationType);

					LOG.debug("idDataSchema=" + idDataSchema);

					if (autoGenerateId.equalsIgnoreCase("Y")) {
						if (schemaType.equalsIgnoreCase("Oracle RAC")) {
							schemaDAOI.insertDataIntoHiveSourceForAutoGenerateStatus(idDataSchema);
							schemaDAOI.processAutoGenerateTemplate_oracleRAC(actual_uri, database, actual_username,
									actual_password, actual_port, schemaName, schemaType, domain, serviceName, krb5conf,
									autoGenerateId, idUser, idDataSchema, projectId, domainId);
						}
					}

					if (enableFileMonitoring != null && enableFileMonitoring.equalsIgnoreCase("Y")) {
						createFileMonitoringValidation(idDataSchema);
					}

					// Saving AlertNotificationLog to database
					integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
							TaskTypes.connection, AlertManagement.Connection_Creation_Success, "success", createdByUser,
							mappingErrors);

				} else {
					LOG.error("Connection failed");
					integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
							TaskTypes.connection, AlertManagement.Connection_Creation_Failure, "failed", createdByUser,
							mappingErrors);
				}

			}

			if (flag == false) {

				if (failureMessage.contains(hostAndPortFailure)) {
					response = "Entered wrong hostname(URI) or port";
				}
				if (failureMessage.contains(databaseFailure)) {
					response = "Entered wrong database";
				}
				if (failureMessage.contains(credentialsFailure)) {
					response = "Entered wrong username or password";
				}

			}

			result.put("message", response);
			result.put("idDataSchema", idDataSchema);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			mappingErrors.put((DebugUtil.getInitialExceptionDetails(e)));
			integrationMasterService.saveAlertEventLog("", 1l, projectId, idDataSchema, schemaName,
					TaskTypes.connection, AlertManagement.Connection_Creation_Failure, "failed", createdByUser,
					mappingErrors);

			result.put("message", "Data Connection failed");
			result.put("idDataSchema", idDataSchema);

		}

		return result;
	}
	

	public TableListforSchema getTableListForSchema(String selSchemaId) {
		LOG.info("getTableListForSchema - START");
		LOG.debug("\n====> Fetching table list for Connection Id: [" + selSchemaId + "]");
		LOG.debug("Schema id:" + selSchemaId);
		List<ListDataSchema> listDataSchemaArrList = null;
		List<String> masterListofAllSchemaTables = new ArrayList<String>();
		List<Long> listofSchemaIds = new ArrayList<Long>();
			listDataSchemaArrList = dataTemplateAddNewDAO.getListDataSchema(Long.parseLong(selSchemaId));
		for (ListDataSchema listDataSchema:listDataSchemaArrList) {
			try { 
				List<String> tableListFrom = new ArrayList<String>();
				
				String hostURI = listDataSchema.getIpAddress();
				String database = listDataSchema.getDatabaseSchema();
				String userlogin = listDataSchema.getUsername();
				String password = listDataSchema.getPassword();
				String port = listDataSchema.getPort();
				String domain = listDataSchema.getDomain();
				String serviceName = listDataSchema.getKeytab();
				String sslEnb = listDataSchema.getSslEnb();
				String sslTrustStorePath = listDataSchema.getSslTrustStorePath();
				String trustPassword = listDataSchema.getTrustPassword();
				String principle = listDataSchema.getDomain();
				String keytab = listDataSchema.getKeytab();
				String krb5conf = listDataSchema.getKrb5conf();
				String gatewayPath = listDataSchema.getGatewayPath();
				String jksPath = listDataSchema.getJksPath();
				String folderPath = listDataSchema.getFolderPath();
				String fileNamePattern = listDataSchema.getFileNamePattern();
				String accessKey = listDataSchema.getAccessKey();
				String secretKey = listDataSchema.getSecretKey();
				String bucketName = listDataSchema.getBucketName();
				String partitionedFolders = listDataSchema.getPartitionedFolders();
				int maxFolderDepth = listDataSchema.getMaxFolderDepth();
				String multiFolderEnabled = listDataSchema.getMultiFolderEnabled();
		
				String bigQueryProjectName = listDataSchema.getBigQueryProjectName();
				String privatekeyId = listDataSchema.getPrivatekeyId();
				String privatekey = listDataSchema.getPrivatekey();
				String clientId = listDataSchema.getClientId();
				String clientEmail = listDataSchema.getClientEmail();
				String datasetName = listDataSchema.getDatasetName();
		
				String azureClientId = listDataSchema.getAzureClientId();
				String azureClientSecret = listDataSchema.getAzureClientSecret();
				String azureTenantId = listDataSchema.getAzureTenantId();
				String azureServiceURI = listDataSchema.getAzureServiceURI();
				String azureFilePath = listDataSchema.getAzureFilePath();
		
				String schemaType = listDataSchema.getSchemaType();
				String schemaName = listDataSchema.getSchemaName();
				String kmsAuthDisabled = listDataSchema.getKmsAuthDisabled();
				String httpPath = listDataSchema.getHttpPath();
				String azureAuthenticationType = listDataSchema.getAzureAuthenticationType();
				listofSchemaIds.add(listDataSchema.getIdDataSchema());
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
						//if(selSchemaId!=null) {
							return new TableListforSchema(tableListFrom, listofSchemaIds);
						//}
					}
				}
		
				if (schemaType.equals("Oracle")) {
					tableListFrom = oracleconnection.getListOfTableNamesFromOracle(hostURI, userlogin, password, port,
							database);
				}
				if (schemaType.equals("Postgres")) {
					tableListFrom = postgresConnection.getListOfTableNamesFromPostgres(hostURI, userlogin, password, port,
							database, sslEnb);
				} else if (schemaType.equals("MYSQL")) {
					tableListFrom = mysqlconnection.getListOfTableNamesFromMYSql(hostURI, userlogin, password, port, database,
							sslEnb);
				} else if (schemaType.equals("MSSQL")) {
					tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSql(hostURI, userlogin, password, port, database); 
				} else if (schemaType.equals("MSSQLAD")) {
					tableListFrom = mSSQLConnection.getListOfTableNamesFromMsSqlAzure(hostURI, userlogin, password, port, database, azureAuthenticationType);
				}else if (schemaType.equals("Teradata")) {
					tableListFrom = teradataConnection.getListOfTableNamesFromTeradata(hostURI, userlogin, password, port,
							database);
				} else if (schemaType.equals("Vertica")) {
					tableListFrom = verticaconnection.getListOfTableNamesFromVertica(hostURI, userlogin, password, port,
							database);
				} else if (schemaType.equalsIgnoreCase("MSSQLActiveDirectory")) {
					tableListFrom = msSqlActiveDirectoryConnectionObject.getListOfTableNamesFromMsSqlActiveDirectory(hostURI,
							userlogin, password, port, domain, database);
				} else if (schemaType.equalsIgnoreCase("Amazon Redshift")) {
					tableListFrom = amazonRedshiftConnection.getListOfTableNamesFromAmazonRedshift(hostURI, userlogin, password,
							port, domain, database);
				} else if (schemaType.equalsIgnoreCase("Cassandra")) {
					tableListFrom = cassandraconnection.getListOfTableNamesFromCassandra(hostURI, userlogin, password, port,
							database, domain);
				} else if (schemaType.equalsIgnoreCase("Oracle RAC")) {
					tableListFrom = OracleRACConnection.getListOfTableNamesFromOracleRAC(hostURI, userlogin, password, port,
							database, domain, serviceName);
				} else if (schemaType.equalsIgnoreCase("Hive") || schemaType.equalsIgnoreCase("Hive Kerberos")
						|| schemaType.equalsIgnoreCase("ClouderaHive") || schemaType.equalsIgnoreCase("MapR Hive")) {
					boolean isKerberosEnabled = schemaType.equals("Hive Kerberos") ? true : false;
					// Add special condition for MapR hive
		
					// Get DomainId and Domain Name of the connection, under which it is created.
					Integer domainId = listDataSchema.getDomainId();
					String domainName = iTaskDAO.getDomainNameById(domainId.longValue());
		
					ListDataSchema ldd = listDataSchema;
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
						if (schemaType.equalsIgnoreCase("MapR Hive")) {
		
							tableListFrom = hiveconnection.getListOfTableNamesFromMapRHive(hostURI, database, userlogin,
									password, port, domainName);
						} else {
							tableListFrom = hiveconnection.getListOfTableNamesFromHive(schemaType, hostURI, database,
									userlogin, password, port, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
									krb5conf, principle, jksPath);
						}
					}
				} else if (schemaType.equalsIgnoreCase("Hive knox")) {
					tableListFrom = hiveconnection.getListOfTableNamesFromHiveKnox(hostURI, database, userlogin, password, port,
							jksPath, gatewayPath);
				} else if (schemaType.equals("SnowFlake")) {
					tableListFrom = snowFlakeConnection.getListOfTableNamesFromSnowflake(hostURI, userlogin, password, port,
							database);
				} else if (schemaType.equalsIgnoreCase("FileSystem Batch")) {
					tableListFrom = batchFileSystem.getListOfTableNamesFromFolder(folderPath, fileNamePattern);
				} else if (schemaType.equalsIgnoreCase("S3 Batch")) {
					tableListFrom = s3BatchConnection.getListOfTableNamesFromFolder(accessKey, secretKey, bucketName,
							folderPath, fileNamePattern, multiFolderEnabled);
				} else if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")) {
					tableListFrom = s3BatchConnection.getListOfTableNamesInFolderForS3IAMRole(bucketName, folderPath,
							fileNamePattern, partitionedFolders, maxFolderDepth);
				} else if (schemaType.equalsIgnoreCase("BigQuery")) {
					tableListFrom = bigQueryConnection.getListOfTableNamesFromBigQuery(bigQueryProjectName, privatekeyId,
							privatekey, clientId, clientEmail, datasetName);
				} else if (schemaType.equalsIgnoreCase("AzureSynapseMSSQL")) {
					tableListFrom = azureConnection.getListOfTableNamesFromAzure(hostURI, port, database, userlogin, password);
				} else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
					tableListFrom = azureDataLakeConnection.getListOfFilesFromDataLake(azureClientId, azureClientSecret,
							azureTenantId, azureServiceURI, azureFilePath);
				} else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
					tableListFrom = azureDataLakeGen2Connection.getFolderListFromDataLake(accessKey, secretKey, bucketName,
							folderPath);
				}
		
				else if (schemaType.equalsIgnoreCase("AzureCosmosDB")) {
		
					tableListFrom = azureCosmosDb.getFolderListFromCosmosDataLake(database, hostURI, port, secretKey);
				}else if (schemaType.equals("DatabricksDeltaLake")) {
					tableListFrom = databricksDeltaConnection.getListOfTableNamesFromDatabricksDeltaLake(hostURI, userlogin, password, database, port, httpPath);
				}
				masterListofAllSchemaTables.addAll(tableListFrom);
				LOG.info("getTableListForSchema - END");
			}catch(Exception ce) {
				ce.printStackTrace();
				LOG.info(ce.getCause());
			}
		}
		return new TableListforSchema(masterListofAllSchemaTables, listofSchemaIds);
	}



}
