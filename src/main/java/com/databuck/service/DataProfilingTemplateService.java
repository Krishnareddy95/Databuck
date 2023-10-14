package com.databuck.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpSession;

import com.databuck.datatemplate.DatabricksDeltaConnection;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.databuck.bean.GloabalRule;
import com.databuck.bean.HiveSource;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.listDataAccess;
import com.databuck.constants.DatabuckConstants;
import com.databuck.controller.DataTemplateController;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.ITemplateViewDAO;
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
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.HiveKerberosConnection;
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
import com.databuck.econstants.TemplateRunTypes;
import com.databuck.security.LogonManager;
import com.databuck.util.DatabuckFileUtility;

@Async
@Service
public class DataProfilingTemplateService {
	@Autowired
	IProjectDAO projectDao;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	IDataAlgorithService dataAlgorithService;

	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	PostgresConnection postgresConnection;

	@Autowired
	TeradataConnection teradataConnection;

	@Autowired
	MYSQLConnection mysqlconnection;

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
	public SchemaDAOI SchemaDAOI;

	@Autowired
	ITemplateViewDAO templateviewdao;

	@Autowired
	HiveKerberosConnection hiveKerberosConnection;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private DataTemplateController dataTemplateController;

	@Autowired
	private ITaskService iTaskService;

	@Autowired
	private ITaskDAO iTaskDAO;
	@Autowired
	IUserDAO userDAO;

	@Autowired
	SnowflakeConnection snowFlakeConnection;

	@Autowired
	BigQueryConnection bigQueryConnection;

	@Autowired
	AzureConnection azureConnection;

	@Autowired
	S3BatchConnection s3BatchConnection;

	@Autowired
	AzureDataLakeConnection azureDataLakeConnection;

	@Autowired
	private BatchFileSystem batchFileSystem;

	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	@Autowired
	private LogonManager logonManager;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	private AzureDataLakeGen2Connection azureDataLakeGen2Connection;

	@Autowired
	private AzureCosmosDb azureCosmosDb;

	@Autowired
	public IProjectService projService;

	@Autowired
	private DatabricksDeltaConnection databricksDeltaConnection;

	private static final Logger LOG = Logger.getLogger(DataProfilingTemplateService.class);

	@SuppressWarnings("unchecked")
	public CompletableFuture<Long> createDataTemplate(HttpSession session, long idDataSchema, String datalocation,
			String tableName, String dataTemplateName, String description, String schema, String headerId,
			String rowsId, String whereCondition, String queryCheckbox, String lstTable, String queryTextbox,
			String incrementalType, String dateFormat, String sliceStart, String sliceEnd, long idUser, String HostURI,
			String folder, String dataFormat, String userlogin, String password, String schemaName, MultipartFile file,
			String tar_brokerUri, String tar_topicName, String profilingEnabled, List<GloabalRule> selected_list,
			Long projectId, String advancedRulesEnabled, String createdByUser, String rollingHeaderPresent,
			String rollingColumn, String historicDateTable, String childFilePattern, String subFolderName) {

		Integer domainId =null;
		LOG.info("\n========> createDataTemplate <========");

		// ListDataSource object creation
		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setName(dataTemplateName);
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(datalocation);
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(idUser);
		listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
		listDataSource.setCreatedByUser(createdByUser);
		// Setting profiling enabled
		if (profilingEnabled == null || !profilingEnabled.equalsIgnoreCase("Y")) {
			profilingEnabled = "N";
		}
		listDataSource.setProfilingEnabled(profilingEnabled);

		// Setting AdvancedRules enabled
		if (advancedRulesEnabled == null || !advancedRulesEnabled.equalsIgnoreCase("Y")) {
			advancedRulesEnabled = "N";
		}
		listDataSource.setAdvancedRulesEnabled(advancedRulesEnabled);

		// Get Schema details
		List<ListDataSchema> listDataSchema = null;

		String connectionName = "";
		String connectionType = "";
		String portName = "";
		String domain = "";
		String bigQueryProjectName = "";
		String privatekeyId = "";
		String privatekey = "";
		String clientId = "";
		String clientEmail = "";
		String datasetName = "";
		String serviceName = "";

		String azureClientId = "";
		String azureClientSecret = "";
		String azureTenantId = "";
		String azureServiceURI = "";
		String azureFilePath = "";
		String kmsAuthDisabled = "";
		String sslEnb = "";
		Integer domainIdFromSchema = null;
		String httpPath= "";

		if (idDataSchema > 0l) {
			listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			connectionName = listDataSchema.get(0).getSchemaName();
			connectionType = listDataSchema.get(0).getSchemaType();
			HostURI = listDataSchema.get(0).getIpAddress();
			folder = listDataSchema.get(0).getDatabaseSchema();
			userlogin = listDataSchema.get(0).getUsername();
			password = listDataSchema.get(0).getPassword();
			portName = listDataSchema.get(0).getPort();
			domain = listDataSchema.get(0).getDomain();
			serviceName = listDataSchema.get(0).getKeytab();

			bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
			privatekeyId = listDataSchema.get(0).getPrivatekeyId();
			privatekey = listDataSchema.get(0).getPrivatekey();
			clientId = listDataSchema.get(0).getClientId();
			clientEmail = listDataSchema.get(0).getClientEmail();
			datasetName = listDataSchema.get(0).getDatasetName();

			azureClientId = listDataSchema.get(0).getAzureClientId();
			azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
			azureTenantId = listDataSchema.get(0).getAzureTenantId();
			azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
			azureFilePath = listDataSchema.get(0).getAzureFilePath();
			kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();

			domainIdFromSchema = listDataSchema.get(0).getDomainId();
			sslEnb = listDataSchema.get(0).getSslEnb();
			if (sslEnb == null || !sslEnb.trim().equalsIgnoreCase("Y"))
				sslEnb = "N";

			//Adding for Azure Delta Lake
			httpPath = listDataSchema.get(0).getHttpPath();
			folder = listDataSchema.get(0).getDatabaseSchema();
		}

		// Get DomainId and Domain Name of the connection, under which it is created.

		if (session != null) {
			domainId = (Integer) session.getAttribute("domainId");
			LOG.debug("\n====> domain id from session=" + domainId);
		} else if (domainId != null && domainId > 0) {
			LOG.debug("\n====> domain id through REST API=" + domainId);
		} else if (domainIdFromSchema > 0) {
			domainId = domainIdFromSchema;
			LOG.debug("\n====> domain id through connection=" + domainId);
		} else {
			domainId = new Long(iTaskDAO.getDomainIdByProjectId(projectId)).intValue();
			LOG.debug("\n====> domain id through project=" + domainId);
		}

		// Get Domain Name
		domain = iTaskDAO.getDomainNameById(domainId.longValue());
		LOG.debug("\n====> Domain Name:" + domain);

		// ListDataAccess object creation
		listDataAccess listdataAccess = new listDataAccess();
		listdataAccess.setHostName(HostURI);
		listdataAccess.setPortName(portName);
		listdataAccess.setUserName(userlogin);
		listdataAccess.setPwd(password);
		listdataAccess.setSchemaName(folder);
		listdataAccess.setDomain(domain);
		listdataAccess.setQueryString(queryTextbox);
		listdataAccess.setIdDataSchema(idDataSchema);
		listdataAccess.setWhereCondition(whereCondition);
		listdataAccess.setQuery(queryCheckbox);
		listdataAccess.setHistoricDateTable(historicDateTable);
		listdataAccess.setIncrementalType(incrementalType);
		listdataAccess.setDateFormat(dateFormat);
		listdataAccess.setSliceStart(sliceStart);
		listdataAccess.setSliceEnd(sliceEnd);
		listdataAccess.setFileHeader(headerId);
		listdataAccess.setRollingHeader(rollingHeaderPresent);
		listdataAccess.setRollingColumn(rollingColumn);
		listdataAccess.setFolderName(tableName);
		listdataAccess.setSslEnb(sslEnb);
		listDataSource.setName(dataTemplateName);

		// Set domainId
		listDataSource.setDomain(domainId);

		if (queryCheckbox.equalsIgnoreCase("Y")) {
			if (datalocation.equals("FileSystem Batch") || datalocation.equals("S3 Batch")
					|| datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
				listdataAccess.setFolderName(lstTable.trim().replace("[", "").replace("]", "").replace("\"", ""));
			} else {
				listdataAccess.setFolderName(queryTextbox);
			}
		} else {
			listdataAccess.setFolderName(tableName);
		}

		// setting garbage rows
		Long rows = Long.valueOf(0L);
		if ((rowsId != null) && (!rowsId.equals("")))
			rows = Long.valueOf(Long.parseLong(rowsId));
		listDataSource.setGarbageRows(rows);

		List<String> primaryKeys = new ArrayList<String>();
		long idData = 0l;

		/*
		 * When KMS Authentication is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(connectionType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(connectionName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					HostURI = conn_user_details.get("hostname");
					portName = (connectionType.equalsIgnoreCase("Oracle"))
							? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.debug("\n====>KMS Authentication is not supported for [" + connectionType + "] !!");

			if (!flag) {
				LOG.error(
						"\n====>Unable to create template, failed to fetch connection details from logon manager !!");
				return CompletableFuture.completedFuture(idData);
			}
		}

		if (datalocation.equals("MSSQLActiveDirectory")) {
			listDataSource.setDataSource("MSSQLActiveDirectory");
			Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, folder, userlogin,
					password, tableName, portName, domain, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Vertica")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = verticaconnection.verticaconnection(HostURI, folder, userlogin, password, tableName,
					portName, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("MYSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mysqlconnection.readTablesFromMYSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName, sslEnb);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];
			List<String> primaryKeys_1 = (List<String>) obj[1];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess,
					primaryKeys_1, null, selected_list, projectId);

		} else if (datalocation.equals("BigQuery")) {
			listDataSource.setDataSource("SQL");
			LinkedHashMap<String, String> metadata = bigQueryConnection.readTablesFromBigQuery(bigQueryProjectName,
					privatekeyId, privatekey, clientId, clientEmail, datasetName, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureSynapseMSSQL")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = azureConnection.readTablesFromAzureSynapse(HostURI, portName,
					folder, userlogin, password, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureDataLakeStorageGen1")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = azureDataLakeConnection.readColumsFromDataLake(azureClientId,
					azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("MSSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mSSQLConnection.readTablesFromMSSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Postgres")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = postgresConnection.readTablesFromPostgres(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName, sslEnb);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Teradata")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = teradataConnection.readTablesFromTeradata(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Cassandra")) {
			listDataSource.setDataSource("SQL");
			Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, folder, userlogin, password, tableName,
					portName);
			Map<String, String> metadata = (Map<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = oracleconnection.readTablesFromOracle(HostURI, folder, userlogin, password,
					tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle RAC")) {
			listDataSource.setDataSource("SQL");
			listdataAccess.setSchemaName(serviceName);
			Map<String, String> metadata = OracleRACConnection.readTablesFromOracleRAC(HostURI, folder, userlogin,
					password, tableName, portName, serviceName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Hive") || datalocation.equals("Hive Kerberos")
				|| datalocation.equals("ClouderaHive") || datalocation.equals("Hive knox")
				|| datalocation.equals("MapR Hive")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			String principle = listDataSchema.get(0).getDomain();
			String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
			String trustPassword = listDataSchema.get(0).getTrustPassword();
			String hivejdbchost = listDataSchema.get(0).getHivejdbchost();
			String hivejdbcport = listDataSchema.get(0).getHivejdbcport();
			String keytab = listDataSchema.get(0).getKeytab();
			String krb5conf = listDataSchema.get(0).getKrb5conf();
			String gatewayPath = listDataSchema.get(0).getGatewayPath();
			String jksPath = listDataSchema.get(0).getJksPath();
			String zookeeperUrl = listDataSchema.get(0).getZookeeperUrl();

			boolean isKerberosEnabled = datalocation.equals("Hive Kerberos") ? true : false;
			boolean isKnoxEnabled = datalocation.equals("Hive knox") ? true : false;

			listdataAccess.setDomain(principle);
			listdataAccess.setSslEnb(sslEnb);
			listdataAccess.setSslTrustStorePath(sslTrustStorePath);
			listdataAccess.setTrustPassword(trustPassword);
			listdataAccess.setHivejdbchost(hivejdbchost);
			listdataAccess.setHivejdbcport(hivejdbcport);
			listdataAccess.setGatewayPath(gatewayPath);
			listdataAccess.setJksPath(jksPath);
			listdataAccess.setZookeeperUrl(zookeeperUrl);

			Map<String, String> metadata = null;

			String clusterPropertyCategory = listDataSchema.get(0).getClusterPropertyCategory();
			LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

			if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

				String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);

				metadata = remoteClusterAPIService.getTableMetaDataByRemoteCluster(HostURI, folder, userlogin,
						password, portName, domain, tableName, queryTextbox, encryptedToken, idDataSchema);
			}else{
				if (datalocation.equals("MapR Hive")) {

					metadata = hiveconnection.getTableMetaDataFromMapRHive(HostURI, folder, userlogin, password,
							portName, domain, tableName, queryTextbox);
				} else {
					metadata = hiveconnection.readTablesFromHive(datalocation, HostURI, folder, userlogin, password,
							tableName, portName, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
							krb5conf, principle, isKnoxEnabled, gatewayPath, jksPath, zookeeperUrl, queryTextbox);
				}
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("Amazon Redshift")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);
			Map<String, String> metadata = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI, folder,
					userlogin, password, tableName, portName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File Management")) {
			listDataSource.setDataLocation("File Management");
			listDataSource.setDataSource(dataFormat);

			listdataAccess.setHostName(HostURI);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			idData = dataTemplateAddNewDAO.insertIntoListDataSources(listDataSource, projectId);
			listdataAccess.setIdData(idData);

			ArrayList<String> al = new ArrayList<String>();
			try {
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

				String line = br.readLine();
				if (line != null) {
					for (String word : line.split(",")) {
						al.add(word);
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			dataTemplateAddNewDAO.insertIntoListDataAccess(listdataAccess);
			dataTemplateAddNewDAO.insertIntoListDataFiles(idData, al);

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			listDataSource.setDataSource(dataFormat);

			// Validate the connection details
			boolean isConnectionValid = azureDataLakeGen2Connection.validateConnection(userlogin, password, HostURI,
					folder);

			if (isConnectionValid)
				// No metadata is generated for files in Azure, backend does the processing
				idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
						null, selected_list, projectId);
			else
				LOG.error(
						"\n====> AzureDataLakeGen2 connection details are incorrect, failed to create template !!");

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("AzureCosmosDB")) {

			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}

		else if (datalocation.equals("File System") || datalocation.equals("HDFS") || datalocation.equals("S3")
				|| datalocation.equals("MapR FS") || datalocation.equals("MapR DB") || datalocation.equals("Kafka")) {
			listDataSource.setTarBrokerUri(tar_brokerUri);
			listDataSource.setTarTopicName(tar_topicName);
			// Setting DataSource
			listDataSource.setDataSource(dataFormat);
			// Setting Datalocation
			if (datalocation.equals("File System"))
				listDataSource.setDataLocation("FILESYSTEM");
			else
				listDataSource.setDataLocation(datalocation);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			if (headerId.equalsIgnoreCase("N") || datalocation.equals("Kafka")
					|| rollingHeaderPresent.equalsIgnoreCase("Y")) {

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

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				} else {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate").split(",");

				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
					dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
					InputStream inputStream = file.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

					if (dataFormat.equalsIgnoreCase("FLAT")) {
						metadata = databuckFileUtility.prepareFlatFileMetadata(br, "CSV");
					} else {
						String line = br.readLine();
						String dataLine = br.readLine();

						boolean isJSON = false;
						String[] dataValues = {};
						String[] headerValues = {};
						Map<String, String> jsonHM = new HashMap<String, String>();

						String splitBy = "\\,";
						if (dataFormat.equalsIgnoreCase("psv")) {
							splitBy = "\\|";
						} else if (dataFormat.equalsIgnoreCase("tsv")) {
							splitBy = "\\t";
						} else if (dataFormat.equalsIgnoreCase("json")) {
							isJSON = true;
							jsonHM = getTableColumns(file);
						} else {
							splitBy = "\\,";
						}

						if (!isJSON) {
							// This needs to be sorted
							if (line != null && dataLine != null) {
								dataValues = dataLine.split(splitBy, -1);
								headerValues = line.split(splitBy);
							}

							// if dataline is not provided, initiallize to string
							if (line != null && dataLine == null) {
								headerValues = line.split(splitBy);
								dataValues = new String[headerValues.length];
								for (int i = 0; i < headerValues.length; i++) {
									dataValues[i] = "";
								}
							}
						} else {
							headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
							dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
						}

						LOG.debug(" dataValues " + dataValues);
						LOG.debug(" headerValues " + headerValues);

						for (int i = 0; i < headerValues.length; i++) {
							String word = headerValues[i];
							String columnName = headerValues[i];
							String dataValue = dataValues[i];
							String columnType = "String";

							int first = word.lastIndexOf("(");
							int last = word.lastIndexOf(")");

							boolean isColumnTypePresent = false;
							if (first != -1 && last != -1) {
								isColumnTypePresent = true;
								if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
									columnType = word.substring(first + 1, last).trim();
									columnName = word.substring(0, first);
								} else {
									columnName = word.substring(0, last);
								}
							} else {
								columnName = word;
							}

							// Determine column type based on first row. This will
							// be done only if
							// column type is not already specified in csv.
							if (!isColumnTypePresent) {
								boolean isColumnTypeIdentified = false;
								if (dataValue.matches(numberRegex)) {
									isColumnTypeIdentified = true;
									columnType = "number";
								} else {
									for (int j = 0; j < dateRegexFormate.size(); j++) {
										String regex = dateRegexFormate.get(j).trim();
										if (dataValue.matches(regex) || (dataValue.length() >= 10
												&& dataValue.substring(0, 10).trim().matches(regex))) {
											columnType = "Date";
											isColumnTypeIdentified = true;
										}
									}
								}

								if (!isColumnTypeIdentified) {
									columnType = "varchar";
								}
							}

							columnName = columnName.trim();
							String modifiedColumn = "";
							String[] charArray = columnName.split("(?!^)");
							for (int j = 0; j < charArray.length; j++) {
								if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

									modifiedColumn = modifiedColumn + charArray[j];
								}
							}

							modifiedColumn = modifiedColumn.replace("-", "_");
							modifiedColumn = modifiedColumn.replace(".", "_");
							modifiedColumn = modifiedColumn.replace(" ", "_");

							LOG.debug("columnName=" + modifiedColumn);
							LOG.debug("columnType=" + columnType);
							metadata.put(modifiedColumn, columnType);
						}

						for (Map.Entry m : metadata.entrySet()) {
							LOG.debug(m.getKey());
							LOG.debug(m.getValue());
						}
					}
				} catch (IOException e1) {
					LOG.error(e1.getMessage());
					e1.printStackTrace();
				}

			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("SnowFlake")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = snowFlakeConnection.readTablesFromSnowflake(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("FileSystem Batch")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				listDataSource.setDataSource("FILESYSTEMBATCH");

				listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());

				listdataAccess.setHostName(listDataSchema.get(0).getFolderPath());

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {

					if (fileDataFormat.equalsIgnoreCase("json")) {
						metadata = null;
					} else
						metadata = batchFileSystem.getTablecolumnsFromfilesystem(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			if (metadata != null)
				metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 Batch")) {

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();
				listDataSource.setDataSource(fileDataFormat);

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				// Metadata read is not performed here for Parquet and ORC files
				if (!(queryCheckbox.equalsIgnoreCase("Y") || fileDataFormat.equalsIgnoreCase("parquet")
						|| fileDataFormat.equalsIgnoreCase("orc"))) {
					metadata = s3BatchConnection.getMetadataOfTableForS3(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				if (datalocation.equals("S3 IAMRole Batch")) {
					listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());
				} else {
					listDataSource.setDataSource(dataFormat);
				}

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName, listDataSchema.get(0),
							childFilePattern, subFolderName, dataFormat);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}else if (datalocation.equals("DatabricksDeltaLake")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = databricksDeltaConnection.readTablesFromDatabricksDeltaLake(HostURI, portName,
					folder, userlogin, password, tableName, httpPath);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);
			LOG.debug("idData="+idData);

		}

		return CompletableFuture.completedFuture(idData);

	}

	public CompletableFuture<Long> createDataTemplateWithDomainId(HttpSession session, long idDataSchema, String datalocation,
			String tableName, String dataTemplateName, String description, String schema, String headerId,
			String rowsId, String whereCondition, String queryCheckbox, String lstTable, String queryTextbox,
			String incrementalType, String dateFormat, String sliceStart, String sliceEnd, long idUser, String HostURI,
			String folder, String dataFormat, String userlogin, String password, String schemaName, MultipartFile file,
			String tar_brokerUri, String tar_topicName, String profilingEnabled, List<GloabalRule> selected_list,
			Long projectId, String advancedRulesEnabled, String createdByUser, String rollingHeaderPresent,
			String rollingColumn, String historicDateTable, String childFilePattern, String subFolderName,Integer domainId) {

		LOG.info("\n========> createDataTemplate <========");
		// ListDataSource object creation
		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setName(dataTemplateName);
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(datalocation);
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(idUser);
		listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
		listDataSource.setCreatedByUser(createdByUser);
		// Setting profiling enabled
		if (profilingEnabled == null || !profilingEnabled.equalsIgnoreCase("Y")) {
			profilingEnabled = "N";
		}
		listDataSource.setProfilingEnabled(profilingEnabled);

		// Setting AdvancedRules enabled
		if (advancedRulesEnabled == null || !advancedRulesEnabled.equalsIgnoreCase("Y")) {
			advancedRulesEnabled = "N";
		}
		listDataSource.setAdvancedRulesEnabled(advancedRulesEnabled);

		// Get Schema details
		List<ListDataSchema> listDataSchema = null;

		String connectionName = "";
		String connectionType = "";
		String portName = "";
		String domain = "";
		String bigQueryProjectName = "";
		String privatekeyId = "";
		String privatekey = "";
		String clientId = "";
		String clientEmail = "";
		String datasetName = "";
		String serviceName = "";

		String azureClientId = "";
		String azureClientSecret = "";
		String azureTenantId = "";
		String azureServiceURI = "";
		String azureFilePath = "";
		String kmsAuthDisabled = "";
		String sslEnb = "";
		Integer domainIdFromSchema = null;
		String httpPath= "";

		if (idDataSchema > 0l) {
			listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			connectionName = listDataSchema.get(0).getSchemaName();
			connectionType = listDataSchema.get(0).getSchemaType();
			HostURI = listDataSchema.get(0).getIpAddress();
			folder = listDataSchema.get(0).getDatabaseSchema();
			userlogin = listDataSchema.get(0).getUsername();
			password = listDataSchema.get(0).getPassword();
			portName = listDataSchema.get(0).getPort();
			domain = listDataSchema.get(0).getDomain();
			serviceName = listDataSchema.get(0).getKeytab();

			bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
			privatekeyId = listDataSchema.get(0).getPrivatekeyId();
			privatekey = listDataSchema.get(0).getPrivatekey();
			clientId = listDataSchema.get(0).getClientId();
			clientEmail = listDataSchema.get(0).getClientEmail();
			datasetName = listDataSchema.get(0).getDatasetName();

			azureClientId = listDataSchema.get(0).getAzureClientId();
			azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
			azureTenantId = listDataSchema.get(0).getAzureTenantId();
			azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
			azureFilePath = listDataSchema.get(0).getAzureFilePath();
			kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();

			domainIdFromSchema = listDataSchema.get(0).getDomainId();
			sslEnb = listDataSchema.get(0).getSslEnb();
			if (sslEnb == null || !sslEnb.trim().equalsIgnoreCase("Y"))
				sslEnb = "N";

			//Adding for Azure Delta Lake
			httpPath = listDataSchema.get(0).getHttpPath();
			folder = listDataSchema.get(0).getDatabaseSchema();
		}

		// Get DomainId and Domain Name of the connection, under which it is created.

		if (session != null) {
			domainId = (Integer) session.getAttribute("domainId");
			LOG.debug("\n====> domain id from session=" + domainId);
		} else if (domainId != null && domainId > 0) {
			LOG.debug("\n====> domain id through REST API=" + domainId);
		} else if (domainIdFromSchema > 0) {
			domainId = domainIdFromSchema;
			LOG.debug("\n====> domain id through connection=" + domainId);
		} else {
			domainId = new Long(iTaskDAO.getDomainIdByProjectId(projectId)).intValue();
			LOG.debug("\n====> domain id through project=" + domainId);
		}

		// Get Domain Name
		domain = iTaskDAO.getDomainNameById(domainId.longValue());
		LOG.debug("\n====> Domain Name:" + domain);

		// ListDataAccess object creation
		listDataAccess listdataAccess = new listDataAccess();
		listdataAccess.setHostName(HostURI);
		listdataAccess.setPortName(portName);
		listdataAccess.setUserName(userlogin);
		listdataAccess.setPwd(password);
		listdataAccess.setSchemaName(folder);
		listdataAccess.setDomain(domain);
		listdataAccess.setQueryString(queryTextbox);
		listdataAccess.setIdDataSchema(idDataSchema);
		listdataAccess.setWhereCondition(whereCondition);
		listdataAccess.setQuery(queryCheckbox);
		listdataAccess.setHistoricDateTable(historicDateTable);
		listdataAccess.setIncrementalType(incrementalType);
		listdataAccess.setDateFormat(dateFormat);
		listdataAccess.setSliceStart(sliceStart);
		listdataAccess.setSliceEnd(sliceEnd);
		listdataAccess.setFileHeader(headerId);
		listdataAccess.setRollingHeader(rollingHeaderPresent);
		listdataAccess.setRollingColumn(rollingColumn);
		listdataAccess.setFolderName(tableName);
		listdataAccess.setSslEnb(sslEnb);
		listDataSource.setName(dataTemplateName);

		// Set domainId
		listDataSource.setDomain(domainId);

		if (queryCheckbox.equalsIgnoreCase("Y")) {
			if (datalocation.equals("FileSystem Batch") || datalocation.equals("S3 Batch")
					|| datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
				listdataAccess.setFolderName(lstTable.trim().replace("[", "").replace("]", "").replace("\"", ""));
			} else {
				listdataAccess.setFolderName(queryTextbox);
			}
		} else {
			listdataAccess.setFolderName(tableName);
		}

		// setting garbage rows
		Long rows = Long.valueOf(0L);
		if ((rowsId != null) && (!rowsId.equals("")))
			rows = Long.valueOf(Long.parseLong(rowsId));
		listDataSource.setGarbageRows(rows);

		List<String> primaryKeys = new ArrayList<String>();
		long idData = 0l;

		/*
		 * When KMS Authentication is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(connectionType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(connectionName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					HostURI = conn_user_details.get("hostname");
					portName = (connectionType.equalsIgnoreCase("Oracle"))
							? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.debug("\n====>KMS Authentication is not supported for [" + connectionType + "] !!");

			if (!flag) {
				LOG.error(
						"\n====>Unable to create template, failed to fetch connection details from logon manager !!");
				return CompletableFuture.completedFuture(idData);
			}
		}

		if (datalocation.equals("MSSQLActiveDirectory")) {
			listDataSource.setDataSource("MSSQLActiveDirectory");
			Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, folder, userlogin,
					password, tableName, portName, domain, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Vertica")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = verticaconnection.verticaconnection(HostURI, folder, userlogin, password, tableName,
					portName, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("MYSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mysqlconnection.readTablesFromMYSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName, sslEnb);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];
			List<String> primaryKeys_1 = (List<String>) obj[1];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess,
					primaryKeys_1, null, selected_list, projectId);

		} else if (datalocation.equals("BigQuery")) {
			listDataSource.setDataSource("SQL");
			LinkedHashMap<String, String> metadata = bigQueryConnection.readTablesFromBigQuery(bigQueryProjectName,
					privatekeyId, privatekey, clientId, clientEmail, datasetName, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureSynapseMSSQL")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = azureConnection.readTablesFromAzureSynapse(HostURI, portName,
					folder, userlogin, password, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureDataLakeStorageGen1")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = azureDataLakeConnection.readColumsFromDataLake(azureClientId,
					azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("MSSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mSSQLConnection.readTablesFromMSSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Postgres")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = postgresConnection.readTablesFromPostgres(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName, sslEnb);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Teradata")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = teradataConnection.readTablesFromTeradata(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Cassandra")) {
			listDataSource.setDataSource("SQL");
			Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, folder, userlogin, password, tableName,
					portName);
			Map<String, String> metadata = (Map<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = oracleconnection.readTablesFromOracle(HostURI, folder, userlogin, password,
					tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle RAC")) {
			listDataSource.setDataSource("SQL");
			listdataAccess.setSchemaName(serviceName);
			Map<String, String> metadata = OracleRACConnection.readTablesFromOracleRAC(HostURI, folder, userlogin,
					password, tableName, portName, serviceName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Hive") || datalocation.equals("Hive Kerberos")
				|| datalocation.equals("ClouderaHive") || datalocation.equals("Hive knox")
				|| datalocation.equals("MapR Hive")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			String principle = listDataSchema.get(0).getDomain();
			String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
			String trustPassword = listDataSchema.get(0).getTrustPassword();
			String hivejdbchost = listDataSchema.get(0).getHivejdbchost();
			String hivejdbcport = listDataSchema.get(0).getHivejdbcport();
			String keytab = listDataSchema.get(0).getKeytab();
			String krb5conf = listDataSchema.get(0).getKrb5conf();
			String gatewayPath = listDataSchema.get(0).getGatewayPath();
			String jksPath = listDataSchema.get(0).getJksPath();
			String zookeeperUrl = listDataSchema.get(0).getZookeeperUrl();

			boolean isKerberosEnabled = datalocation.equals("Hive Kerberos") ? true : false;
			boolean isKnoxEnabled = datalocation.equals("Hive knox") ? true : false;

			listdataAccess.setDomain(principle);
			listdataAccess.setSslEnb(sslEnb);
			listdataAccess.setSslTrustStorePath(sslTrustStorePath);
			listdataAccess.setTrustPassword(trustPassword);
			listdataAccess.setHivejdbchost(hivejdbchost);
			listdataAccess.setHivejdbcport(hivejdbcport);
			listdataAccess.setGatewayPath(gatewayPath);
			listdataAccess.setJksPath(jksPath);
			listdataAccess.setZookeeperUrl(zookeeperUrl);

			Map<String, String> metadata = null;

			String clusterPropertyCategory = listDataSchema.get(0).getClusterPropertyCategory();
			LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

			if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

				String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);

				metadata = remoteClusterAPIService.getTableMetaDataByRemoteCluster(HostURI, folder, userlogin,
						password, portName, domain, tableName, queryTextbox, encryptedToken, idDataSchema);
			}else {
				if (datalocation.equals("MapR Hive")) {
					metadata = hiveconnection.getTableMetaDataFromMapRHive(HostURI, folder, userlogin, password,
							portName, domain, tableName, queryTextbox);
				} else {
					metadata = hiveconnection.readTablesFromHive(datalocation, HostURI, folder, userlogin, password,
							tableName, portName, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
							krb5conf, principle, isKnoxEnabled, gatewayPath, jksPath, zookeeperUrl, queryTextbox);
				}
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("Amazon Redshift")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);
			Map<String, String> metadata = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI, folder,
					userlogin, password, tableName, portName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File Management")) {
			listDataSource.setDataLocation("File Management");
			listDataSource.setDataSource(dataFormat);

			listdataAccess.setHostName(HostURI);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			idData = dataTemplateAddNewDAO.insertIntoListDataSources(listDataSource, projectId);
			listdataAccess.setIdData(idData);

			ArrayList<String> al = new ArrayList<String>();
			try {
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

				String line = br.readLine();
				if (line != null) {
					for (String word : line.split(",")) {
						al.add(word);
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			dataTemplateAddNewDAO.insertIntoListDataAccess(listdataAccess);
			dataTemplateAddNewDAO.insertIntoListDataFiles(idData, al);

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			listDataSource.setDataSource(dataFormat);

			// Validate the connection details
			boolean isConnectionValid = azureDataLakeGen2Connection.validateConnection(userlogin, password, HostURI,
					folder);

			if (isConnectionValid)
				// No metadata is generated for files in Azure, backend does the processing
				idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
						null, selected_list, projectId);
			else
				LOG.error(
						"\n====> AzureDataLakeGen2 connection details are incorrect, failed to create template !!");

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("AzureCosmosDB")) {

			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}

		else if (datalocation.equals("File System") || datalocation.equals("HDFS") || datalocation.equals("S3")
				|| datalocation.equals("MapR FS") || datalocation.equals("MapR DB") || datalocation.equals("Kafka")) {
			listDataSource.setTarBrokerUri(tar_brokerUri);
			listDataSource.setTarTopicName(tar_topicName);
			// Setting DataSource
			listDataSource.setDataSource(dataFormat);
			// Setting Datalocation
			if (datalocation.equals("File System"))
				listDataSource.setDataLocation("FILESYSTEM");
			else
				listDataSource.setDataLocation(datalocation);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			if (headerId.equalsIgnoreCase("N") || datalocation.equals("Kafka")
					|| rollingHeaderPresent.equalsIgnoreCase("Y")) {

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

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				} else {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate").split(",");

				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
					dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
					InputStream inputStream = file.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

					if (dataFormat.equalsIgnoreCase("FLAT")) {
						metadata = databuckFileUtility.prepareFlatFileMetadata(br, "CSV");
					} else {
						String line = br.readLine();
						String dataLine = br.readLine();

						boolean isJSON = false;
						String[] dataValues = {};
						String[] headerValues = {};
						Map<String, String> jsonHM = new HashMap<String, String>();

						String splitBy = "\\,";
						if (dataFormat.equalsIgnoreCase("psv")) {
							splitBy = "\\|";
						} else if (dataFormat.equalsIgnoreCase("tsv")) {
							splitBy = "\\t";
						} else if (dataFormat.equalsIgnoreCase("json")) {
							isJSON = true;
							jsonHM = getTableColumns(file);
						} else {
							splitBy = "\\,";
						}

						if (!isJSON) {
							// This needs to be sorted
							if (line != null && dataLine != null) {
								dataValues = dataLine.split(splitBy, -1);
								headerValues = line.split(splitBy);
							}

							// if dataline is not provided, initiallize to string
							if (line != null && dataLine == null) {
								headerValues = line.split(splitBy);
								dataValues = new String[headerValues.length];
								for (int i = 0; i < headerValues.length; i++) {
									dataValues[i] = "";
								}
							}
						} else {
							headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
							dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
						}

						LOG.debug(" dataValues " + dataValues);
						LOG.debug(" headerValues " + headerValues);

						for (int i = 0; i < headerValues.length; i++) {
							String word = headerValues[i];
							String columnName = headerValues[i];
							String dataValue = dataValues[i];
							String columnType = "String";

							int first = word.lastIndexOf("(");
							int last = word.lastIndexOf(")");

							boolean isColumnTypePresent = false;
							if (first != -1 && last != -1) {
								isColumnTypePresent = true;
								if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
									columnType = word.substring(first + 1, last).trim();
									columnName = word.substring(0, first);
								} else {
									columnName = word.substring(0, last);
								}
							} else {
								columnName = word;
							}

							// Determine column type based on first row. This will
							// be done only if
							// column type is not already specified in csv.
							if (!isColumnTypePresent) {
								boolean isColumnTypeIdentified = false;
								if (dataValue.matches(numberRegex)) {
									isColumnTypeIdentified = true;
									columnType = "number";
								} else {
									for (int j = 0; j < dateRegexFormate.size(); j++) {
										String regex = dateRegexFormate.get(j).trim();
										if (dataValue.matches(regex) || (dataValue.length() >= 10
												&& dataValue.substring(0, 10).trim().matches(regex))) {
											columnType = "Date";
											isColumnTypeIdentified = true;
										}
									}
								}

								if (!isColumnTypeIdentified) {
									columnType = "varchar";
								}
							}

							columnName = columnName.trim();
							String modifiedColumn = "";
							String[] charArray = columnName.split("(?!^)");
							for (int j = 0; j < charArray.length; j++) {
								if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

									modifiedColumn = modifiedColumn + charArray[j];
								}
							}

							modifiedColumn = modifiedColumn.replace("-", "_");
							modifiedColumn = modifiedColumn.replace(".", "_");
							modifiedColumn = modifiedColumn.replace(" ", "_");

							LOG.debug("columnName=" + modifiedColumn);
							LOG.debug("columnType=" + columnType);
							metadata.put(modifiedColumn, columnType);
						}

						for (Map.Entry m : metadata.entrySet()) {
							LOG.debug(m.getKey());
							LOG.debug(m.getValue());
						}
					}
				} catch (IOException e1) {
					LOG.error(e1.getMessage());
					e1.printStackTrace();
				}

			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("SnowFlake")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = snowFlakeConnection.readTablesFromSnowflake(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("FileSystem Batch")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				listDataSource.setDataSource("FILESYSTEMBATCH");

				listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());

				listdataAccess.setHostName(listDataSchema.get(0).getFolderPath());

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {

					if (fileDataFormat.equalsIgnoreCase("json")) {
						metadata = null;
					} else
						metadata = batchFileSystem.getTablecolumnsFromfilesystem(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			if (metadata != null)
				metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 Batch")) {

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();
				listDataSource.setDataSource(fileDataFormat);

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				// Metadata read is not performed here for Parquet and ORC files
				if (!(queryCheckbox.equalsIgnoreCase("Y") || fileDataFormat.equalsIgnoreCase("parquet")
						|| fileDataFormat.equalsIgnoreCase("orc"))) {
					metadata = s3BatchConnection.getMetadataOfTableForS3(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				if (datalocation.equals("S3 IAMRole Batch")) {
					listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());
				} else {
					listDataSource.setDataSource(dataFormat);
				}

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName, listDataSchema.get(0),
							childFilePattern, subFolderName, dataFormat);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}else if (datalocation.equals("DatabricksDeltaLake")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = databricksDeltaConnection.readTablesFromDatabricksDeltaLake(HostURI, portName,
					folder, userlogin, password, tableName, httpPath);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);
			LOG.debug("idData="+idData);

		}

		return CompletableFuture.completedFuture(idData);

	}

	public CompletableFuture<Long> createDerivedDataTemplate(HttpSession session, String datalocation,
			String dataTemplateName, String description, long idUser, Long projectId, String template1Value,
			String template2Value, String template1Name, String template2Name, String aliasNameTemplate1,
			String aliasNameTemplate2, String queryText, String createdByUser, Integer domainId) {

		LOG.info("createDerivedDataTemplate");
		// ListDataSource object creation
		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setName(dataTemplateName);
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(datalocation);
		listDataSource.setDataSource(datalocation);
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(idUser);
		listDataSource.setGarbageRows(Long.valueOf("0"));
		listDataSource.setIdDataSchema(Long.valueOf("-3"));
		listDataSource.setCreatedByUser(createdByUser);
		listDataSource.setDomain(domainId);
		// Setting profiling enabled
		String profilingEnabled = "N";
		listDataSource.setProfilingEnabled(profilingEnabled);
		// Setting AdvancedRules enabled

		String advancedRulesEnabled = "N";

		listDataSource.setAdvancedRulesEnabled(advancedRulesEnabled);
		// derived data source object creation

		ListDerivedDataSource listDerivedDataSource = new ListDerivedDataSource();
		listDerivedDataSource.setName(dataTemplateName);
		listDerivedDataSource.setDescription(description);
		listDerivedDataSource.setTemplate1Name(template1Name);
		listDerivedDataSource.setTemplate2Name(template2Name);
		listDerivedDataSource.setTemplate1IdData(Long.valueOf(template1Value));
		listDerivedDataSource.setTemplate2IdData(Long.valueOf(template2Value));
		listDerivedDataSource.setTemplate1AliasName(aliasNameTemplate1);
		listDerivedDataSource.setTemplate2AliasName(aliasNameTemplate2);
		listDerivedDataSource.setQueryText(queryText.trim());
		listDerivedDataSource.setCreatedByUser(createdByUser);
		listDerivedDataSource.setCreatedAt(new Date());
		listDerivedDataSource.setCreatedBy(idUser);
		long idData = 0l;

		idData = dataTemplateAddNewDAO.insertIntoListDataSources(listDataSource, projectId);
		listDerivedDataSource.setIdData(idData);
		dataTemplateAddNewDAO.insertIntoListDerivedDataSources(listDerivedDataSource, projectId);

		return CompletableFuture.completedFuture(idData);
	}

	/**
	 * get table columns from json Object
	 * 
	 * @param file
	 * @return
	 */
	private Map<String, String> getTableColumns(MultipartFile file) {
		Map<String, String> columns = new HashMap<String, String>();
		// parse json object file
		JSONObject obj;
		try {
			JSONTokener tokener = new JSONTokener(new InputStreamReader(file.getInputStream(), "UTF-8"));
			// parse JSON object
			obj = new JSONObject(tokener);
			obj.keySet().forEach(keyStr -> {
				String keyvalue = obj.get(keyStr).toString();
				LOG.debug("key: " + keyStr + " value: " + keyvalue);
				columns.put(keyStr, keyvalue);
			});
			LOG.debug(" columns " + columns);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return columns;
	}

	public CompletableFuture<String> triggerDataTemplate(long idData, String datalocation, String profilingEnabled,
			String advancedRulesEnabled) {
		String uniqueId = "";
		if (idData != 0l) {
			if (!(datalocation.equals("File Management") || datalocation.equals("Kafka"))) {
				// create an entry in TemplateTasks table
				uniqueId = iTaskDAO.placeTemplateJobInQueue(idData, TemplateRunTypes.newtemplate.toString());
				iTaskService.insertTaskListForTemplate(idData, profilingEnabled, advancedRulesEnabled);

			} else {
				uniqueId = iTaskDAO.placeTemplateJobInQueue(idData, TemplateRunTypes.newtemplate.toString());
				iTaskDAO.updateTemplateCreationJobStatus(idData, uniqueId, "completed");
			}
		} else {
			LOG.error("Invalid idData, Can't proceed further ...!!");
		}
		return CompletableFuture.completedFuture(uniqueId);
	}

	public void createTemplatesAsync(HttpSession session, long idDataSchema, String datalocation, String tableName,
			String dataTemplateName, String description, String schema, String headerId, String rowsId,
			String whereCondition, String queryCheckbox, String lstTable, String queryTextbox, String incrementalType,
			String dateFormat, String sliceStart, String sliceEnd, long idUser, String HostURI, String folder,
			String dataFormat, String userlogin, String password, String schemaName, MultipartFile file,
			String tar_brokerUri, String tar_topicName, String profilingEnabled, String advancedRulesEnabled,
			List<GloabalRule> selected_list, String rollingHeaderPresent, String rollingColumn,
			String historicDateTable, String createdByUser, Long projectId) {

		if (projectId == null) {
			projectId = (Long) session.getAttribute("projectId");
		}
		LOG.info("\n********* createTemplateAsync - START ********");
		LOG.debug("=== Created by in DataProfilingTemplate Service==>" + createdByUser);

		try {

			LOG.debug("datalocation =>" + datalocation);
			LOG.debug("tableName =>" + tableName);
			LOG.debug("dataTemplateName =>" + dataTemplateName);

			if (lstTable != null) {
				lstTable = lstTable.replace("[", "").replace("]", "");
			}

			StringTokenizer tokenizer = new StringTokenizer(lstTable, ",");

			while (tokenizer.hasMoreTokens()) {

				tableName = tokenizer.nextToken();
				tableName = tableName.replace("\"", "");

				LOG.debug("\n====>Template creation for table:" + tableName);

				String templateName = dataTemplateName + "_" + tableName;
				LOG.debug("\n====>Template name for table [" + tableName + "] : " + templateName);

				CompletableFuture<Long> result = createDataTemplate(session, idDataSchema, datalocation, tableName,
						templateName, description, schema, headerId, rowsId, whereCondition, queryCheckbox, lstTable,
						queryTextbox, incrementalType, dateFormat, sliceStart, sliceEnd, idUser, HostURI, folder,
						dataFormat, userlogin, password, schemaName, file, tar_brokerUri, tar_topicName,
						profilingEnabled, selected_list, projectId, advancedRulesEnabled, createdByUser,
						rollingHeaderPresent, rollingColumn, historicDateTable, null, null);

				long idData = result.get();
				triggerDataTemplate(idData, datalocation, profilingEnabled, advancedRulesEnabled);

			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====>Exception occurred while executing createTemplateAsync method !!");
			e.printStackTrace();
		}
	}

	/**
	 * The following method will filter out unsupported data types columns from
	 * metadata
	 * 
	 * @param metadata
	 * @return
	 */
	private LinkedHashMap<String, String> metadataFilter(Map<String, String> metadata) {
		LOG.info("\n====>Filtering out unsupported datatypes columns from metadata ..");

		LinkedHashMap<String, String> modifiedMetadata = null;

		if (metadata != null) {
			modifiedMetadata = new LinkedHashMap<String, String>();

			// list of unsupported data types is retrieved from properties file
			String unsupportedTypes = appDbConnectionProperties.getProperty("dq.unsupported.datatypes");
			LOG.debug("\n====>dq.unsupported.datatypes: " + unsupportedTypes);

			if (unsupportedTypes != null && !unsupportedTypes.trim().isEmpty()) {

				List<String> unsupportedDataTypesList = Arrays.asList(unsupportedTypes.toLowerCase().split(","));

				for (String columnName : metadata.keySet()) {
					String columnDataType = metadata.get(columnName);

					if (unsupportedDataTypesList.contains(columnDataType.toLowerCase())) {
						LOG.debug(
								"\n====>Excluding column: [" + columnName + "] with datatype [" + columnDataType + "]");
						continue;
					}
					modifiedMetadata.put(columnName, columnDataType);
				}
			} else
				modifiedMetadata.putAll(metadata);
		}
		LOG.info("\n====>Filtering Metadata - END\n");

		return modifiedMetadata;
	}

	@SuppressWarnings("unchecked")
	public CompletableFuture<Long> createDataTemplateWithoutSession(String email, long idDataSchema,
			String datalocation, String tableName, String dataTemplateName, String description, String schema,
			String headerId, String rowsId, String whereCondition, String queryCheckbox, String lstTable,
			String queryTextbox, String incrementalType, String dateFormat, String sliceStart, String sliceEnd,
			long idUser, String HostURI, String folder, String dataFormat, String userlogin, String password,
			String schemaName, MultipartFile file, String tar_brokerUri, String tar_topicName, String profilingEnabled,
			List<GloabalRule> selected_list, Long projectId, String advancedRulesEnabled, String createdByUser,
			String rollingHeaderPresent, String rollingColumn, String historicDateTable, String childFilePattern,
			String subFolderName) {

		LOG.info("\n========> createDataTemplate <========");
		Integer domainId = null;
		// ListDataSource object creation
		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setName(dataTemplateName);
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(datalocation);
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(idUser);
		listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
		listDataSource.setCreatedByUser(createdByUser);
		// Setting profiling enabled
		if (profilingEnabled == null || !profilingEnabled.equalsIgnoreCase("Y")) {
			profilingEnabled = "N";
		}
		listDataSource.setProfilingEnabled(profilingEnabled);

		// Setting AdvancedRules enabled
		if (advancedRulesEnabled == null || !advancedRulesEnabled.equalsIgnoreCase("Y")) {
			advancedRulesEnabled = "N";
		}
		listDataSource.setAdvancedRulesEnabled(advancedRulesEnabled);

		// Get Schema details
		List<ListDataSchema> listDataSchema = null;

		String connectionName = "";
		String connectionType = "";
		String portName = "";
		String domain = "";
		String bigQueryProjectName = "";
		String privatekeyId = "";
		String privatekey = "";
		String clientId = "";
		String clientEmail = "";
		String datasetName = "";
		String serviceName = "";

		String azureClientId = "";
		String azureClientSecret = "";
		String azureTenantId = "";
		String azureServiceURI = "";
		String azureFilePath = "";
		String kmsAuthDisabled = "";
		Integer domainIdFromSchema = null;
		String sslEnb = "";

		if (idDataSchema > 0l) {
			listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			connectionName = listDataSchema.get(0).getSchemaName();
			connectionType = listDataSchema.get(0).getSchemaType();
			HostURI = listDataSchema.get(0).getIpAddress();
			folder = listDataSchema.get(0).getDatabaseSchema();
			userlogin = listDataSchema.get(0).getUsername();
			password = listDataSchema.get(0).getPassword();
			portName = listDataSchema.get(0).getPort();
			domain = listDataSchema.get(0).getDomain();
			serviceName = listDataSchema.get(0).getKeytab();

			bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
			privatekeyId = listDataSchema.get(0).getPrivatekeyId();
			privatekey = listDataSchema.get(0).getPrivatekey();
			clientId = listDataSchema.get(0).getClientId();
			clientEmail = listDataSchema.get(0).getClientEmail();
			datasetName = listDataSchema.get(0).getDatasetName();

			azureClientId = listDataSchema.get(0).getAzureClientId();
			azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
			azureTenantId = listDataSchema.get(0).getAzureTenantId();
			azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
			azureFilePath = listDataSchema.get(0).getAzureFilePath();
			kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();

			domainIdFromSchema = listDataSchema.get(0).getDomainId();
			sslEnb = listDataSchema.get(0).getSslEnb();
			if (sslEnb == null || !sslEnb.trim().equalsIgnoreCase("Y"))
				sslEnb = "N";
		}

		// Get DomainId and Domain Name of the connection, under which it is created.
		if (domainIdFromSchema != null && domainIdFromSchema > 0)
			domainId = domainIdFromSchema;
		else if (domainId == null) {
			domainId = projService.getDomainProjectAssociationOfCurrentUserByMailId(email).get(0).getIdProject();
			if (domainId == null)
				domainId = 0;
		}

		// ListDataAccess object creation
		listDataAccess listdataAccess = new listDataAccess();
		listdataAccess.setHostName(HostURI);
		listdataAccess.setPortName(portName);
		listdataAccess.setUserName(userlogin);
		listdataAccess.setPwd(password);
		listdataAccess.setSchemaName(folder);
		listdataAccess.setDomain(domain);
		listdataAccess.setQueryString(queryTextbox);
		listdataAccess.setIdDataSchema(idDataSchema);
		listdataAccess.setWhereCondition(whereCondition);
		listdataAccess.setQuery(queryCheckbox);
		listdataAccess.setHistoricDateTable(historicDateTable);
		listdataAccess.setIncrementalType(incrementalType);
		listdataAccess.setDateFormat(dateFormat);
		listdataAccess.setSliceStart(sliceStart);
		listdataAccess.setSliceEnd(sliceEnd);
		listdataAccess.setFileHeader(headerId);
		listdataAccess.setRollingHeader(rollingHeaderPresent);
		listdataAccess.setRollingColumn(rollingColumn);
		listdataAccess.setFolderName(tableName);
		listdataAccess.setSslEnb(sslEnb);
		listDataSource.setName(dataTemplateName);

		// Get Domain Name
		String domainName = iTaskDAO.getDomainNameById(domainId.longValue());

		// Set domainId
		listDataSource.setDomain(domainId);

		if (queryCheckbox.equalsIgnoreCase("Y")) {
			if (datalocation.equals("FileSystem Batch") || datalocation.equals("S3 Batch")
					|| datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
				listdataAccess.setFolderName(lstTable.trim().replace("[", "").replace("]", "").replace("\"", ""));
			} else {
				listdataAccess.setFolderName(queryTextbox);
			}
		} else {
			listdataAccess.setFolderName(tableName);
		}

		// setting garbage rows
		Long rows = Long.valueOf(0L);
		if ((rowsId != null) && (!rowsId.equals("")))
			rows = Long.valueOf(Long.parseLong(rowsId));
		listDataSource.setGarbageRows(rows);

		List<String> primaryKeys = new ArrayList<String>();
		long idData = 0l;

		/*
		 * When KMS Authentication is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(connectionType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(connectionName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					HostURI = conn_user_details.get("hostname");
					portName = (connectionType.equalsIgnoreCase("Oracle"))
							? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.debug("\n====>KMS Authentication is not supported for [" + connectionType + "] !!");

			if (!flag) {
				LOG.error(
						"\n====>Unable to create template, failed to fetch connection details from logon manager !!");
				return CompletableFuture.completedFuture(idData);
			}
		}

		if (datalocation.equals("MSSQLActiveDirectory")) {
			listDataSource.setDataSource("MSSQLActiveDirectory");
			Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, folder, userlogin,
					password, tableName, portName, domain, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Vertica")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = verticaconnection.verticaconnection(HostURI, folder, userlogin, password, tableName,
					portName, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("MYSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mysqlconnection.readTablesFromMYSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName, sslEnb);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];
			List<String> primaryKeys_1 = (List<String>) obj[1];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess,
					primaryKeys_1, null, selected_list, projectId);

		} else if (datalocation.equals("BigQuery")) {
			listDataSource.setDataSource("SQL");
			LinkedHashMap<String, String> metadata = bigQueryConnection.readTablesFromBigQuery(bigQueryProjectName,
					privatekeyId, privatekey, clientId, clientEmail, datasetName, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureSynapseMSSQL")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = azureConnection.readTablesFromAzureSynapse(HostURI, portName,
					folder, userlogin, password, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureDataLakeStorageGen1")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = azureDataLakeConnection.readColumsFromDataLake(azureClientId,
					azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("MSSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mSSQLConnection.readTablesFromMSSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Postgres")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = postgresConnection.readTablesFromPostgres(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName, sslEnb);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Teradata")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = teradataConnection.readTablesFromTeradata(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Cassandra")) {
			listDataSource.setDataSource("SQL");
			Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, folder, userlogin, password, tableName,
					portName);
			Map<String, String> metadata = (Map<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = oracleconnection.readTablesFromOracle(HostURI, folder, userlogin, password,
					tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle RAC")) {
			listDataSource.setDataSource("SQL");
			listdataAccess.setSchemaName(serviceName);
			Map<String, String> metadata = OracleRACConnection.readTablesFromOracleRAC(HostURI, folder, userlogin,
					password, tableName, portName, serviceName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Hive") || datalocation.equals("Hive Kerberos")
				|| datalocation.equals("ClouderaHive") || datalocation.equals("Hive knox")
				|| datalocation.equals("MapR Hive")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			String principle = listDataSchema.get(0).getDomain();
			String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
			String trustPassword = listDataSchema.get(0).getTrustPassword();
			String hivejdbchost = listDataSchema.get(0).getHivejdbchost();
			String hivejdbcport = listDataSchema.get(0).getHivejdbcport();
			String keytab = listDataSchema.get(0).getKeytab();
			String krb5conf = listDataSchema.get(0).getKrb5conf();
			String gatewayPath = listDataSchema.get(0).getGatewayPath();
			String jksPath = listDataSchema.get(0).getJksPath();
			String zookeeperUrl = listDataSchema.get(0).getZookeeperUrl();

			boolean isKerberosEnabled = datalocation.equals("Hive Kerberos") ? true : false;
			boolean isKnoxEnabled = datalocation.equals("Hive knox") ? true : false;

			listdataAccess.setDomain(principle);
			listdataAccess.setSslEnb(sslEnb);
			listdataAccess.setSslTrustStorePath(sslTrustStorePath);
			listdataAccess.setTrustPassword(trustPassword);
			listdataAccess.setHivejdbchost(hivejdbchost);
			listdataAccess.setHivejdbcport(hivejdbcport);
			listdataAccess.setGatewayPath(gatewayPath);
			listdataAccess.setJksPath(jksPath);
			listdataAccess.setZookeeperUrl(zookeeperUrl);

			Map<String, String> metadata = null;
			if (datalocation.equals("MapR Hive")) {

				String clusterPropertyCategory = listDataSchema.get(0).getClusterPropertyCategory();
				LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

				if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
						&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

					String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

					StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
					encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

					String encryptedToken = encryptor.encrypt(token);

					metadata = remoteClusterAPIService.getTableMetaDataByRemoteCluster(HostURI, folder, userlogin,
							password, portName, domainName, tableName, queryTextbox, encryptedToken, idDataSchema);
				} else
					metadata = hiveconnection.getTableMetaDataFromMapRHive(HostURI, folder, userlogin, password,
							portName, domainName, tableName, queryTextbox);
			} else {
				metadata = hiveconnection.readTablesFromHive(datalocation, HostURI, folder, userlogin, password,
						tableName, portName, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
						krb5conf, principle, isKnoxEnabled, gatewayPath, jksPath, zookeeperUrl, queryTextbox);
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("Amazon Redshift")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);
			Map<String, String> metadata = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI, folder,
					userlogin, password, tableName, portName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File Management")) {
			listDataSource.setDataLocation("File Management");
			listDataSource.setDataSource(dataFormat);

			listdataAccess.setHostName(HostURI);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			idData = dataTemplateAddNewDAO.insertIntoListDataSources(listDataSource, projectId);
			listdataAccess.setIdData(idData);

			ArrayList<String> al = new ArrayList<String>();
			try {
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

				String line = br.readLine();
				if (line != null) {
					for (String word : line.split(",")) {
						al.add(word);
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			dataTemplateAddNewDAO.insertIntoListDataAccess(listdataAccess);
			dataTemplateAddNewDAO.insertIntoListDataFiles(idData, al);

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			listDataSource.setDataSource(dataFormat);

			// Validate the connection details
			boolean isConnectionValid = azureDataLakeGen2Connection.validateConnection(userlogin, password, HostURI,
					folder);

			if (isConnectionValid)
				// No metadata is generated for files in Azure, backend does the processing
				idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
						null, selected_list, projectId);
			else
				LOG.error(
						"\n====> AzureDataLakeGen2 connection details are incorrect, failed to create template !!");

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File System") || datalocation.equals("HDFS") || datalocation.equals("S3")
				|| datalocation.equals("MapR FS") || datalocation.equals("MapR DB") || datalocation.equals("Kafka")) {
			listDataSource.setTarBrokerUri(tar_brokerUri);
			listDataSource.setTarTopicName(tar_topicName);
			// Setting DataSource
			listDataSource.setDataSource(dataFormat);
			// Setting Datalocation
			if (datalocation.equals("File System"))
				listDataSource.setDataLocation("FILESYSTEM");
			else
				listDataSource.setDataLocation(datalocation);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			if (headerId.equalsIgnoreCase("N") || datalocation.equals("Kafka")
					|| rollingHeaderPresent.equalsIgnoreCase("Y")) {

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

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				} else {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate").split(",");

				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
					dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
					InputStream inputStream = file.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

					if (dataFormat.equalsIgnoreCase("FLAT")) {
						metadata = databuckFileUtility.prepareFlatFileMetadata(br, "CSV");
					} else {
						String line = br.readLine();
						String dataLine = br.readLine();

						boolean isJSON = false;
						String[] dataValues = {};
						String[] headerValues = {};
						Map<String, String> jsonHM = new HashMap<String, String>();

						String splitBy = "\\,";
						if (dataFormat.equalsIgnoreCase("psv")) {
							splitBy = "\\|";
						} else if (dataFormat.equalsIgnoreCase("tsv")) {
							splitBy = "\\t";
						} else if (dataFormat.equalsIgnoreCase("json")) {
							isJSON = true;
							jsonHM = getTableColumns(file);
						} else {
							splitBy = "\\,";
						}

						if (!isJSON) {
							// This needs to be sorted
							if (line != null && dataLine != null) {
								dataValues = dataLine.split(splitBy, -1);
								headerValues = line.split(splitBy);
							}

							// if dataline is not provided, initiallize to string
							if (line != null && dataLine == null) {
								headerValues = line.split(splitBy);
								dataValues = new String[headerValues.length];
								for (int i = 0; i < headerValues.length; i++) {
									dataValues[i] = "";
								}
							}
						} else {
							headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
							dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
						}

						LOG.debug(" dataValues " + dataValues);
						LOG.debug(" headerValues " + headerValues);

						for (int i = 0; i < headerValues.length; i++) {
							String word = headerValues[i];
							String columnName = headerValues[i];
							String dataValue = dataValues[i];
							String columnType = "String";

							int first = word.lastIndexOf("(");
							int last = word.lastIndexOf(")");

							boolean isColumnTypePresent = false;
							if (first != -1 && last != -1) {
								isColumnTypePresent = true;
								if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
									columnType = word.substring(first + 1, last).trim();
									columnName = word.substring(0, first);
								} else {
									columnName = word.substring(0, last);
								}
							} else {
								columnName = word;
							}

							// Determine column type based on first row. This will
							// be done only if
							// column type is not already specified in csv.
							if (!isColumnTypePresent) {
								boolean isColumnTypeIdentified = false;
								if (dataValue.matches(numberRegex)) {
									isColumnTypeIdentified = true;
									columnType = "number";
								} else {
									for (int j = 0; j < dateRegexFormate.size(); j++) {
										String regex = dateRegexFormate.get(j).trim();
										if (dataValue.matches(regex) || (dataValue.length() >= 10
												&& dataValue.substring(0, 10).trim().matches(regex))) {
											columnType = "Date";
											isColumnTypeIdentified = true;
										}
									}
								}

								if (!isColumnTypeIdentified) {
									columnType = "varchar";
								}
							}

							columnName = columnName.trim();
							String modifiedColumn = "";
							String[] charArray = columnName.split("(?!^)");
							for (int j = 0; j < charArray.length; j++) {
								if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

									modifiedColumn = modifiedColumn + charArray[j];
								}
							}

							modifiedColumn = modifiedColumn.replace("-", "_");
							modifiedColumn = modifiedColumn.replace(".", "_");
							modifiedColumn = modifiedColumn.replace(" ", "_");

							LOG.debug("columnName=" + modifiedColumn);
							LOG.debug("columnType=" + columnType);
							metadata.put(modifiedColumn, columnType);
						}

						for (Map.Entry m : metadata.entrySet()) {
							LOG.debug(m.getKey());
							LOG.debug(m.getValue());
						}
					}
				} catch (IOException e1) {
					LOG.error(e1.getMessage());
					e1.printStackTrace();
				}

			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("SnowFlake")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = snowFlakeConnection.readTablesFromSnowflake(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("FileSystem Batch")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				listDataSource.setDataSource("FILESYSTEMBATCH");

				listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());

				listdataAccess.setHostName(listDataSchema.get(0).getFolderPath());

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = batchFileSystem.getTablecolumnsFromfilesystem(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 Batch")) {

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();
				listDataSource.setDataSource(fileDataFormat);

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				// Metadata read is not performed here for Parquet and ORC files
				if (!(queryCheckbox.equalsIgnoreCase("Y") || fileDataFormat.equalsIgnoreCase("parquet")
						|| fileDataFormat.equalsIgnoreCase("orc"))) {
					metadata = s3BatchConnection.getMetadataOfTableForS3(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				if (datalocation.equals("S3 IAMRole Batch")) {
					listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());
				} else {
					listDataSource.setDataSource(dataFormat);
				}

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName, listDataSchema.get(0),
							childFilePattern, subFolderName, dataFormat);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}

		return CompletableFuture.completedFuture(idData);

	}

	public CompletableFuture<Long> createDataTemplatewithoutSession(Integer nDomainId, long idDataSchema,
			String datalocation, String tableName, String dataTemplateName, String description, String schema,
			String headerId, String rowsId, String whereCondition, String queryCheckbox, String lstTable,
			String queryTextbox, String incrementalType, String dateFormat, String sliceStart, String sliceEnd,
			long idUser, String HostURI, String folder, String dataFormat, String userlogin, String password,
			String schemaName, MultipartFile file, String tar_brokerUri, String tar_topicName, String profilingEnabled,
			List<GloabalRule> selected_list, Long projectId, String advancedRulesEnabled, String createdByUser,
			String rollingHeaderPresent, String rollingColumn, String historicDateTable, String childFilePattern,
			String subFolderName) {

		LOG.info("\n========> createDataTemplate <========");
		Integer domainId = null;
		// ListDataSource object creation
		ListDataSource listDataSource = new ListDataSource();
		listDataSource.setName(dataTemplateName);
		listDataSource.setDescription(description);
		listDataSource.setDataLocation(datalocation);
		listDataSource.setCreatedAt(new Date());
		listDataSource.setCreatedBy(idUser);
		listDataSource.setIdDataSchema(Long.valueOf(idDataSchema));
		listDataSource.setCreatedByUser(createdByUser);
		// Setting profiling enabled
		if (profilingEnabled == null || !profilingEnabled.equalsIgnoreCase("Y")) {
			profilingEnabled = "N";
		}
		listDataSource.setProfilingEnabled(profilingEnabled);

		// Setting AdvancedRules enabled
		if (advancedRulesEnabled == null || !advancedRulesEnabled.equalsIgnoreCase("Y")) {
			advancedRulesEnabled = "N";
		}
		listDataSource.setAdvancedRulesEnabled(advancedRulesEnabled);

		// Get Schema details
		List<ListDataSchema> listDataSchema = null;

		String connectionName = "";
		String connectionType = "";
		String portName = "";
		String domain = "";
		String bigQueryProjectName = "";
		String privatekeyId = "";
		String privatekey = "";
		String clientId = "";
		String clientEmail = "";
		String datasetName = "";
		String serviceName = "";

		String azureClientId = "";
		String azureClientSecret = "";
		String azureTenantId = "";
		String azureServiceURI = "";
		String azureFilePath = "";
		String kmsAuthDisabled = "";
		String sslEnb = "";
		Integer domainIdFromSchema = null;

		if (idDataSchema > 0l) {
			listDataSchema = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			connectionName = listDataSchema.get(0).getSchemaName();
			connectionType = listDataSchema.get(0).getSchemaType();
			HostURI = listDataSchema.get(0).getIpAddress();
			folder = listDataSchema.get(0).getDatabaseSchema();
			userlogin = listDataSchema.get(0).getUsername();
			password = listDataSchema.get(0).getPassword();
			portName = listDataSchema.get(0).getPort();
			domain = listDataSchema.get(0).getDomain();
			serviceName = listDataSchema.get(0).getKeytab();

			bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
			privatekeyId = listDataSchema.get(0).getPrivatekeyId();
			privatekey = listDataSchema.get(0).getPrivatekey();
			clientId = listDataSchema.get(0).getClientId();
			clientEmail = listDataSchema.get(0).getClientEmail();
			datasetName = listDataSchema.get(0).getDatasetName();

			azureClientId = listDataSchema.get(0).getAzureClientId();
			azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
			azureTenantId = listDataSchema.get(0).getAzureTenantId();
			azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
			azureFilePath = listDataSchema.get(0).getAzureFilePath();
			kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();

			domainIdFromSchema = listDataSchema.get(0).getDomainId();
			sslEnb = listDataSchema.get(0).getSslEnb();
			if (sslEnb == null || !sslEnb.trim().equalsIgnoreCase("Y"))
				sslEnb = "N";
		}

		// Get DomainId and Domain Name of the connection, under which it is created.

		if (nDomainId != null && nDomainId > 0) {
			domainId = nDomainId;
			LOG.debug("\n====> domain id through REST API=" + domainId);
		} else if (domainIdFromSchema != null && domainIdFromSchema > 0) {
			domainId = domainIdFromSchema;
			LOG.debug("\n====> domain id through connection=" + domainId);
		} else {
			domainId = new Long(iTaskDAO.getDomainIdByProjectId(projectId)).intValue();
			LOG.debug("\n====> domain id through project=" + domainId);
		}

		// Get Domain Name
		domain = iTaskDAO.getDomainNameById(domainId.longValue());

		// ListDataAccess object creation
		listDataAccess listdataAccess = new listDataAccess();
		listdataAccess.setHostName(HostURI);
		listdataAccess.setPortName(portName);
		listdataAccess.setUserName(userlogin);
		listdataAccess.setPwd(password);
		listdataAccess.setSchemaName(folder);
		listdataAccess.setDomain(domain);
		listdataAccess.setQueryString(queryTextbox);
		listdataAccess.setIdDataSchema(idDataSchema);
		listdataAccess.setWhereCondition(whereCondition);
		listdataAccess.setQuery(queryCheckbox);
		listdataAccess.setHistoricDateTable(historicDateTable);
		listdataAccess.setIncrementalType(incrementalType);
		listdataAccess.setDateFormat(dateFormat);
		listdataAccess.setSliceStart(sliceStart);
		listdataAccess.setSliceEnd(sliceEnd);
		listdataAccess.setFileHeader(headerId);
		listdataAccess.setRollingHeader(rollingHeaderPresent);
		listdataAccess.setRollingColumn(rollingColumn);
		listdataAccess.setFolderName(tableName);
		listdataAccess.setSslEnb(sslEnb);
		listDataSource.setName(dataTemplateName);

		// Set domainId
		listDataSource.setDomain(domainId);

		if (queryCheckbox.equalsIgnoreCase("Y")) {
			if (datalocation.equals("FileSystem Batch") || datalocation.equals("S3 Batch")
					|| datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
				listdataAccess.setFolderName(lstTable.trim().replace("[", "").replace("]", "").replace("\"", ""));
			} else {
				listdataAccess.setFolderName(queryTextbox);
			}
		} else {
			listdataAccess.setFolderName(tableName);
		}

		// setting garbage rows
		Long rows = Long.valueOf(0L);
		if ((rowsId != null) && (!rowsId.equals("")))
			rows = Long.valueOf(Long.parseLong(rowsId));
		listDataSource.setGarbageRows(rows);

		List<String> primaryKeys = new ArrayList<String>();
		long idData = 0l;

		/*
		 * When KMS Authentication is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(connectionType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(connectionName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					HostURI = conn_user_details.get("hostname");
					portName = (connectionType.equalsIgnoreCase("Oracle"))
							? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.debug("\n====>KMS Authentication is not supported for [" + connectionType + "] !!");

			if (!flag) {
				LOG.error(
						"\n====>Unable to create template, failed to fetch connection details from logon manager !!");
				return CompletableFuture.completedFuture(idData);
			}
		}

		if (datalocation.equals("MSSQLActiveDirectory")) {
			listDataSource.setDataSource("MSSQLActiveDirectory");
			Object[] arr = msSqlActiveDirectoryConnectionObject.readTablesFromMSSQL(HostURI, folder, userlogin,
					password, tableName, portName, domain, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Vertica")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = verticaconnection.verticaconnection(HostURI, folder, userlogin, password, tableName,
					portName, queryTextbox);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("MYSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mysqlconnection.readTablesFromMYSQLNOPrimaryField(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName, sslEnb);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];
			List<String> primaryKeys_1 = (List<String>) obj[1];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess,
					primaryKeys_1, null, selected_list, projectId);

		} else if (datalocation.equals("BigQuery")) {
			listDataSource.setDataSource("SQL");
			LinkedHashMap<String, String> metadata = bigQueryConnection.readTablesFromBigQuery(bigQueryProjectName,
					privatekeyId, privatekey, clientId, clientEmail, datasetName, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureSynapseMSSQL")) {
			listDataSource.setDataSource("SQL");

			LinkedHashMap<String, String> metadata = azureConnection.readTablesFromAzureSynapse(HostURI, portName,
					folder, userlogin, password, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("AzureDataLakeStorageGen1")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = azureDataLakeConnection.readColumsFromDataLake(azureClientId,
					azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, tableName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, null, null,
					selected_list, projectId);

		} else if (datalocation.equals("MSSQL")) {
			listDataSource.setDataSource("SQL");
			Object[] obj = mSSQLConnection.readTablesFromMSSQL(HostURI, folder, userlogin, password, tableName,
					queryTextbox, portName);
			LinkedHashMap<String, String> metadata = (LinkedHashMap<String, String>) obj[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Postgres")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = postgresConnection.readTablesFromPostgres(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName, sslEnb);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Teradata")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = teradataConnection.readTablesFromTeradata(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Cassandra")) {
			listDataSource.setDataSource("SQL");
			Object[] arr = cassandraconnection.readTablesFromCassandra(HostURI, folder, userlogin, password, tableName,
					portName);
			Map<String, String> metadata = (Map<String, String>) arr[0];

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = oracleconnection.readTablesFromOracle(HostURI, folder, userlogin, password,
					tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Oracle RAC")) {
			listDataSource.setDataSource("SQL");
			listdataAccess.setSchemaName(serviceName);
			Map<String, String> metadata = OracleRACConnection.readTablesFromOracleRAC(HostURI, folder, userlogin,
					password, tableName, portName, serviceName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("Hive") || datalocation.equals("Hive Kerberos")
				|| datalocation.equals("ClouderaHive") || datalocation.equals("Hive knox")
				|| datalocation.equals("MapR Hive")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);

			String principle = listDataSchema.get(0).getDomain();
			String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
			String trustPassword = listDataSchema.get(0).getTrustPassword();
			String hivejdbchost = listDataSchema.get(0).getHivejdbchost();
			String hivejdbcport = listDataSchema.get(0).getHivejdbcport();
			String keytab = listDataSchema.get(0).getKeytab();
			String krb5conf = listDataSchema.get(0).getKrb5conf();
			String gatewayPath = listDataSchema.get(0).getGatewayPath();
			String jksPath = listDataSchema.get(0).getJksPath();
			String zookeeperUrl = listDataSchema.get(0).getZookeeperUrl();

			boolean isKerberosEnabled = datalocation.equals("Hive Kerberos") ? true : false;
			boolean isKnoxEnabled = datalocation.equals("Hive knox") ? true : false;

			listdataAccess.setDomain(principle);
			listdataAccess.setSslEnb(sslEnb);
			listdataAccess.setSslTrustStorePath(sslTrustStorePath);
			listdataAccess.setTrustPassword(trustPassword);
			listdataAccess.setHivejdbchost(hivejdbchost);
			listdataAccess.setHivejdbcport(hivejdbcport);
			listdataAccess.setGatewayPath(gatewayPath);
			listdataAccess.setJksPath(jksPath);
			listdataAccess.setZookeeperUrl(zookeeperUrl);

			Map<String, String> metadata = null;
			if (datalocation.equals("MapR Hive")) {

				String clusterPropertyCategory = listDataSchema.get(0).getClusterPropertyCategory();
				LOG.debug("\n====>clusterPropertyCategory:" + clusterPropertyCategory);

				if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
						&& !clusterPropertyCategory.equalsIgnoreCase("cluster")) {

					String token = remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

					StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
					encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

					String encryptedToken = encryptor.encrypt(token);

					metadata = remoteClusterAPIService.getTableMetaDataByRemoteCluster(HostURI, folder, userlogin,
							password, portName, domain, tableName, queryTextbox, encryptedToken, idDataSchema);
				} else
					metadata = hiveconnection.getTableMetaDataFromMapRHive(HostURI, folder, userlogin, password,
							portName, domain, tableName, queryTextbox);
			} else {
				metadata = hiveconnection.readTablesFromHive(datalocation, HostURI, folder, userlogin, password,
						tableName, portName, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab,
						krb5conf, principle, isKnoxEnabled, gatewayPath, jksPath, zookeeperUrl, queryTextbox);
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equalsIgnoreCase("Amazon Redshift")) {
			listDataSource.setDataSource("SQL");
			HiveSource hivesource = new HiveSource();
			hivesource.setName(dataTemplateName);
			hivesource.setDescription(description);
			hivesource.setIdDataSchema(idDataSchema);
			if (queryCheckbox.equalsIgnoreCase("Y")) {
				hivesource.setTableName(queryTextbox);
			} else {
				hivesource.setTableName(tableName);
			}
			dataTemplateAddNewDAO.insertDataIntoHiveSource(hivesource);
			Map<String, String> metadata = amazonRedshiftConnection.readTablesFromAmazonRedshift(HostURI, folder,
					userlogin, password, tableName, portName, queryTextbox);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File Management")) {
			listDataSource.setDataLocation("File Management");
			listDataSource.setDataSource(dataFormat);

			listdataAccess.setHostName(HostURI);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			idData = dataTemplateAddNewDAO.insertIntoListDataSources(listDataSource, projectId);
			listdataAccess.setIdData(idData);

			ArrayList<String> al = new ArrayList<String>();
			try {
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

				String line = br.readLine();
				if (line != null) {
					for (String word : line.split(",")) {
						al.add(word);
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			dataTemplateAddNewDAO.insertIntoListDataAccess(listdataAccess);
			dataTemplateAddNewDAO.insertIntoListDataFiles(idData, al);

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);
			listDataSource.setDataSource(dataFormat);

			// Validate the connection details
			boolean isConnectionValid = azureDataLakeGen2Connection.validateConnection(userlogin, password, HostURI,
					folder);

			if (isConnectionValid)
				// No metadata is generated for files in Azure, backend does the processing
				idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
						null, selected_list, projectId);
			else
				LOG.error(
						"\n====> AzureDataLakeGen2 connection details are incorrect, failed to create template !!");

		} else if (datalocation.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(tableName);
			listDataSource.setDataSource(dataFormat);

			// No metadata is generated for files in Azure, backend does the processing
			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, null, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("File System") || datalocation.equals("HDFS") || datalocation.equals("S3")
				|| datalocation.equals("MapR FS") || datalocation.equals("MapR DB") || datalocation.equals("Kafka")) {
			listDataSource.setTarBrokerUri(tar_brokerUri);
			listDataSource.setTarTopicName(tar_topicName);
			// Setting DataSource
			listDataSource.setDataSource(dataFormat);
			// Setting Datalocation
			if (datalocation.equals("File System"))
				listDataSource.setDataLocation("FILESYSTEM");
			else
				listDataSource.setDataLocation(datalocation);
			listdataAccess.setPortName("");
			listdataAccess.setSchemaName("");
			listdataAccess.setFolderName(folder);
			listdataAccess.setIdDataSchema(0);

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			if (headerId.equalsIgnoreCase("N") || datalocation.equals("Kafka")
					|| rollingHeaderPresent.equalsIgnoreCase("Y")) {

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

				String numberFormat = appDbConnectionProperties.getProperty("number.Format");
				String numberRegex = "";
				if (numberFormat.equalsIgnoreCase("US")) {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexUS").trim();
				} else {
					numberRegex = appDbConnectionProperties.getProperty("match.numberRegexEU").trim();
				}
				String[] dateRegex = appDbConnectionProperties.getProperty("match.dateRegexFormate").split(",");

				ArrayList<String> dateRegexFormate = new ArrayList<String>();
				for (int regIdx = 0; regIdx < dateRegex.length; regIdx++) {
					dateRegexFormate.add(dateRegex[regIdx].trim());
				}

				try {
					InputStream inputStream = file.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

					if (dataFormat.equalsIgnoreCase("FLAT")) {
						metadata = databuckFileUtility.prepareFlatFileMetadata(br, "CSV");
					} else {
						String line = br.readLine();
						String dataLine = br.readLine();

						boolean isJSON = false;
						String[] dataValues = {};
						String[] headerValues = {};
						Map<String, String> jsonHM = new HashMap<String, String>();

						String splitBy = "\\,";
						if (dataFormat.equalsIgnoreCase("psv")) {
							splitBy = "\\|";
						} else if (dataFormat.equalsIgnoreCase("tsv")) {
							splitBy = "\\t";
						} else if (dataFormat.equalsIgnoreCase("json")) {
							isJSON = true;
							jsonHM = getTableColumns(file);
						} else {
							splitBy = "\\,";
						}

						if (!isJSON) {
							// This needs to be sorted
							if (line != null && dataLine != null) {
								dataValues = dataLine.split(splitBy, -1);
								headerValues = line.split(splitBy);
							}

							// if dataline is not provided, initiallize to string
							if (line != null && dataLine == null) {
								headerValues = line.split(splitBy);
								dataValues = new String[headerValues.length];
								for (int i = 0; i < headerValues.length; i++) {
									dataValues[i] = "";
								}
							}
						} else {
							headerValues = jsonHM.keySet().toArray(new String[jsonHM.keySet().size()]);
							dataValues = jsonHM.values().toArray(new String[jsonHM.keySet().size()]);
						}

						LOG.debug(" dataValues " + dataValues);
						LOG.debug(" headerValues " + headerValues);

						for (int i = 0; i < headerValues.length; i++) {
							String word = headerValues[i];
							String columnName = headerValues[i];
							String dataValue = dataValues[i];
							String columnType = "String";

							int first = word.lastIndexOf("(");
							int last = word.lastIndexOf(")");

							boolean isColumnTypePresent = false;
							if (first != -1 && last != -1) {
								isColumnTypePresent = true;
								if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
									columnType = word.substring(first + 1, last).trim();
									columnName = word.substring(0, first);
								} else {
									columnName = word.substring(0, last);
								}
							} else {
								columnName = word;
							}

							// Determine column type based on first row. This will
							// be done only if
							// column type is not already specified in csv.
							if (!isColumnTypePresent) {
								boolean isColumnTypeIdentified = false;
								if (dataValue.matches(numberRegex)) {
									isColumnTypeIdentified = true;
									columnType = "number";
								} else {
									for (int j = 0; j < dateRegexFormate.size(); j++) {
										String regex = dateRegexFormate.get(j).trim();
										if (dataValue.matches(regex) || (dataValue.length() >= 10
												&& dataValue.substring(0, 10).trim().matches(regex))) {
											columnType = "Date";
											isColumnTypeIdentified = true;
										}
									}
								}

								if (!isColumnTypeIdentified) {
									columnType = "varchar";
								}
							}

							columnName = columnName.trim();
							String modifiedColumn = "";
							String[] charArray = columnName.split("(?!^)");
							for (int j = 0; j < charArray.length; j++) {
								if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

									modifiedColumn = modifiedColumn + charArray[j];
								}
							}

							modifiedColumn = modifiedColumn.replace("-", "_");
							modifiedColumn = modifiedColumn.replace(".", "_");
							modifiedColumn = modifiedColumn.replace(" ", "_");

							LOG.debug("columnName=" + modifiedColumn);
							LOG.debug("columnType=" + columnType);
							metadata.put(modifiedColumn, columnType);
						}

						for (Map.Entry m : metadata.entrySet()) {
							LOG.debug(m.getKey());
							LOG.debug(m.getValue());
						}
					}
				} catch (IOException e1) {
					LOG.error(e1.getMessage());
					e1.printStackTrace();
				}

			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("SnowFlake")) {
			listDataSource.setDataSource("SQL");
			Map<String, String> metadata = snowFlakeConnection.readTablesFromSnowflake(HostURI, folder, userlogin,
					password, tableName, queryTextbox, portName);

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("FileSystem Batch")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				listDataSource.setDataSource("FILESYSTEMBATCH");

				listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());

				listdataAccess.setHostName(listDataSchema.get(0).getFolderPath());

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = batchFileSystem.getTablecolumnsFromfilesystem(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 Batch")) {

			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				String fileDataFormat = listDataSchema.get(0).getFileDataFormat();
				listDataSource.setDataSource(fileDataFormat);

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				// Metadata read is not performed here for Parquet and ORC files
				if (!(queryCheckbox.equalsIgnoreCase("Y") || fileDataFormat.equalsIgnoreCase("parquet")
						|| fileDataFormat.equalsIgnoreCase("orc"))) {
					metadata = s3BatchConnection.getMetadataOfTableForS3(listDataSchema.get(0), tableName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		} else if (datalocation.equals("S3 IAMRole Batch") || datalocation.equals("S3 IAMRole Batch Config")) {
			Map<String, String> metadata = new LinkedHashMap<String, String>();

			try {
				if (datalocation.equals("S3 IAMRole Batch")) {
					listDataSource.setDataSource(listDataSchema.get(0).getFileDataFormat());
				} else {
					listDataSource.setDataSource(dataFormat);
				}

				String fullFolderPath = listDataSchema.get(0).getBucketName() + "/"
						+ listDataSchema.get(0).getFolderPath();
				listdataAccess.setHostName(fullFolderPath);

				String headerPresent = listDataSchema.get(0).getHeaderPresent();
				listdataAccess.setFileHeader(headerPresent);

				// header read has be done when Query support is not enabled
				if (!queryCheckbox.equalsIgnoreCase("Y")) {
					metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName, listDataSchema.get(0),
							childFilePattern, subFolderName, dataFormat);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// Filtering non-supported data types columns
			metadata = metadataFilter(metadata);

			idData = dataTemplateAddNewDAO.addintolistdatasource(listDataSource, metadata, listdataAccess, primaryKeys,
					null, selected_list, projectId);

		}

		return CompletableFuture.completedFuture(idData);

	}

}
