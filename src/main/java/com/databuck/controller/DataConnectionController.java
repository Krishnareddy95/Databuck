package com.databuck.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.integration.AlationIntegrationService;
import com.databuck.service.RemoteClusterAPIService;
import com.databuck.service.DataConnectionService;
import com.databuck.util.ConnectionUtil;
import com.databuck.util.DatabuckUtility;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.Project;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.security.LogonManager;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.google.cloud.bigquery.BigQuery;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.AzureADToken;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.databuck.datatemplate.AzureDataLakeGen2Connection;

@Controller
public class DataConnectionController {
	@Autowired
	private IListDataSourceDAO listdatasourcedao;
	@Autowired
	public SchemaDAOI SchemaDAOI;
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
	private IValidationCheckDAO validationCheckDao;
	@Autowired
	private FileMonitorDao fileMonitorDao;
	
	@Autowired
	AzureDataLakeGen2Connection azureDataLakeGen2Connection;
	
	@Autowired
	private LogonManager logonManager;

	@Autowired
	private DataConnectionService dataConnectionService;

	@Autowired
	private AlationIntegrationService alationIntegrationService;

	@Autowired
	private Properties integrationProperties;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	private ConnectionUtil connectionUtil;

	@RequestMapping(value = "/dataConnectionView")
	public ModelAndView getListDataSchema(ModelAndView model, HttpSession session) throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "R", session);
		if (rbac) {
			// Object projectId = session.getAttribute("projectId");
			// int project_id = Integer.parseInt((String) projectId);
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			// int project_ID = IProjectdAO.getproject_idsession( Integer.parseInt((String)
			// projectId));
			
			String fromDate = (String) session.getAttribute("fromDate");
			String toDate = (String) session.getAttribute("toDate");
			Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");
			
			// When project is not selected in search filter, default selected project will
			// be considered for search filter
			Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
					? searchfilter_projectId
					: projectId;
			Project selectedProject = IProjectdAO.getSelectedProject(selected_projectId);
			
			List<ListDataSchema> listdataschema = listdatasourcedao.getAllActiveAndInActiveConnections(selected_projectId, projList, fromDate, toDate);
			model.addObject("projectList", projList);
			model.addObject("selectedProject", selectedProject);
			model.addObject("listdataschema", listdataschema);
			model.addObject("currentSection", "Data Connection");
			model.addObject("currentLink", "DCView");
			model.setViewName("dataConnectionView");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/editConnection", method = RequestMethod.GET)
	public ModelAndView editSchema(HttpServletRequest req, HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "D", session);
		if (rbac) {

			long idDataSchema = Long.parseLong(req.getParameter("id"));
			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchemaForIdDataSchema(idDataSchema);
			String connectionType= listdataschema.get(0).getSchemaType();
			
			ModelAndView model = new ModelAndView();
			System.out.println("getIdDataSchema=" + listdataschema.get(0).getIdDataSchema());
			model.addObject("listdataschema", listdataschema);
			model.addObject("idDataSchema", listdataschema.get(0).getIdDataSchema());

			model.addObject("schemaName", listdataschema.get(0).getSchemaName());
			model.addObject("schemaType", listdataschema.get(0).getSchemaType());

			model.addObject("ipAddress", listdataschema.get(0).getIpAddress());
			model.addObject("databaseSchema", listdataschema.get(0).getDatabaseSchema());
			model.addObject("username", listdataschema.get(0).getUsername());
			model.addObject("servicename", listdataschema.get(0).getKeytab());
			model.addObject("krb5conf", listdataschema.get(0).getKrb5conf());
			model.addObject("hivejdbchost", listdataschema.get(0).getHivejdbchost());
			model.addObject("hivejdbcport", listdataschema.get(0).getHivejdbcport());
			model.addObject("sslEnb", listdataschema.get(0).getSslEnb());
			model.addObject("sslTrustStorePath", listdataschema.get(0).getSslTrustStorePath());
			model.addObject("trustPassword", listdataschema.get(0).getTrustPassword());
			model.addObject("gatewayPath", listdataschema.get(0).getGatewayPath());
			model.addObject("jksPath", listdataschema.get(0).getJksPath());
			model.addObject("zookeeperUrl", listdataschema.get(0).getZookeeperUrl());
			model.addObject("sslEnabled", listdataschema.get(0).getSslEnb());
			System.out.println("gatewayPath:" + listdataschema.get(0).getGatewayPath());
			System.out.println("jksPath:" + listdataschema.get(0).getJksPath());
			System.out.println("zookeeperUrl:" + listdataschema.get(0).getZookeeperUrl());
			System.out.println("connectionType:" + connectionType);

			// Setting fileSystem batch properties
			if(connectionType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch") )
			{
				model.addObject("azureFolderPath", listdataschema.get(0).getFolderPath());
				model.addObject("azureFileDataFormat", listdataschema.get(0).getFileDataFormat());
				model.addObject("azurePartitionedFolders", listdataschema.get(0).getPartitionedFolders());
				model.addObject("partitionedFolders", "");
				model.addObject("fileDataFormat", "");
				model.addObject("folderPath", "");
			}else{
				model.addObject("azureFolderPath", "");
				model.addObject("azureFileDataFormat", "");
				model.addObject("azurePartitionedFolders", "");
				model.addObject("partitionedFolders", listdataschema.get(0).getPartitionedFolders());
				model.addObject("fileDataFormat", listdataschema.get(0).getFileDataFormat());
				model.addObject("folderPath", listdataschema.get(0).getFolderPath());
			}

			model.addObject("fileNamePattern", listdataschema.get(0).getFileNamePattern());
			model.addObject("headerPresent", listdataschema.get(0).getHeaderPresent());
			model.addObject("headerFilePath", listdataschema.get(0).getHeaderFilePath());
			model.addObject("headerFileNamePattern", listdataschema.get(0).getHeaderFileNamePattern());
			model.addObject("headerFileDataFormat", listdataschema.get(0).getHeaderFileDataFormat());
			model.addObject("accessKey", listdataschema.get(0).getAccessKey());
			model.addObject("secretKey", listdataschema.get(0).getSecretKey());
			model.addObject("bucketName", listdataschema.get(0).getBucketName());
			model.addObject("multiPattern", listdataschema.get(0).getMultiPattern());
			model.addObject("startingUniqueCharCount", listdataschema.get(0).getStartingUniqueCharCount());
			model.addObject("endingUniqueCharCount", listdataschema.get(0).getEndingUniqueCharCount());
			model.addObject("maxFolderDepth", listdataschema.get(0).getMaxFolderDepth());
			model.addObject("bigQueryProjectName", listdataschema.get(0).getBigQueryProjectName());
			model.addObject("privatekeyId", listdataschema.get(0).getPrivatekeyId());
			String privatekey = listdataschema.get(0).getPrivatekey();
			if (privatekey != null) {
				privatekey = privatekey.replaceAll("\n", "\\\\n");
			}
			model.addObject("privatekey", privatekey);
			model.addObject("clientId", listdataschema.get(0).getClientId());
			model.addObject("clientEmail", listdataschema.get(0).getClientEmail());
			model.addObject("datasetName", listdataschema.get(0).getDatasetName());
			model.addObject("azureClientId", listdataschema.get(0).getAzureClientId());
			model.addObject("azureClientSecret", listdataschema.get(0).getAzureClientSecret());
			model.addObject("azureTenantId", listdataschema.get(0).getAzureTenantId());
			model.addObject("azureServiceURI", listdataschema.get(0).getAzureServiceURI());
			model.addObject("azureFilePath", listdataschema.get(0).getAzureFilePath());
			model.addObject("enableFileMonitoring", listdataschema.get(0).getEnableFileMonitoring());
			model.addObject("fileEncrypted", listdataschema.get(0).getFileEncrypted());
			model.addObject("singleFile", listdataschema.get(0).getSingleFile());
			// model.addObject("username", listdataschema.get(0).getUsername());
			if (listdataschema.get(0).getSchemaType().equalsIgnoreCase("oracle")) {
				String[] split = listdataschema.get(0).getPort().split("/");
				model.addObject("port", split[0]);
				model.addObject("servicename", split[1]);
			} else {
				model.addObject("port", listdataschema.get(0).getPort());
			}
			model.addObject("domain", listdataschema.get(0).getDomain());
			// System.out.println(listdataschema);
			model.addObject("currentSection", "Data Connection");
			model.addObject("currentLink", "DCView");

			model.addObject("enableFileMonitoring",  listdataschema.get(0).getEnableFileMonitoring());
			model.addObject("externalFileNamePattern",  listdataschema.get(0).getExtenalFileNamePattern());
			model.addObject("externalFileName",listdataschema.get(0).getExtenalFileName() );
			model.addObject("patternColumn",listdataschema.get(0).getPatternColumn() );
			model.addObject("headerColumn", listdataschema.get(0).getHeaderColumn());
			model.addObject("localDirectoryColumnIndex", listdataschema.get(0).getLocalDirectoryColumnIndex());
			model.addObject("xsltFolderName", listdataschema.get(0).getXsltFolderPath());
			model.addObject("kmsAuthDisabled", listdataschema.get(0).getKmsAuthDisabled());
			model.addObject("pushDownQueryEnabled", listdataschema.get(0).getPushDownQueryEnabled());
			model.addObject("readLatestPartition", listdataschema.get(0).getReadLatestPartition());
			model.addObject("alation_integration_enabled", listdataschema.get(0).getAlation_integration_enabled());
			model.addObject("incrementalDataReadEnabled", listdataschema.get(0).getIncrementalDataReadEnabled());
			model.addObject("multiFolderEnabled", listdataschema.get(0).getMultiFolderEnabled());
			model.setViewName("editSchema");

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteConnection", method = RequestMethod.GET)
	public ModelAndView deleteSchema(HttpServletRequest req, HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "D", session);
		if (rbac) {

			long idDataSchema = Long.parseLong(req.getParameter("id"));
			boolean deleteSchema = listdatasourcedao.deactivateSchema(idDataSchema);
			System.out.println("idDataSchema" + idDataSchema);
			System.out.println("deleteSchema" + deleteSchema);
			
			ModelAndView model = new ModelAndView();
			model.addObject("message", "Schema Successfully Deactivated");
			model.addObject("currentSection", "Data Connection");
			model.addObject("currentLink", "DCView");
			model.setViewName("SchemaSuccessfullyDeleted");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createConnection", method = RequestMethod.GET)
	public ModelAndView getSchemaPage(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		Long projectId = (Long) session.getAttribute("projectId");
		Integer domainId = (Integer) session.getAttribute("domainId");
		SqlRowSet RCAqueryForRowSet = null;
		System.out.println("session projectId : " + projectId);
		System.out.println("session domainId : " + domainId);

		String healthCheck = appDbConnectionProperties.getProperty("connection.healthcheck.enabled");
		if(healthCheck!=null && !healthCheck.trim().isEmpty() && !healthCheck.equalsIgnoreCase("Y"))
			healthCheck="N";

		/*
		 * String id = SchemaDAOI.getConnectionId(schemaName); System.out.println(id)
		 */
		if (projectId == null || domainId == null) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("currentSection", "Data Connection");
			modelAndView.addObject("currentLink", "DCAdd New");
			modelAndView.addObject("healthCheck", healthCheck);
			modelAndView.setViewName("RestartLogin");
			modelAndView.addObject("msg", "No default domain or project selected, "
					+ "it may be due setup is either missing or changed, so connection will be saved incorrectly");
			return modelAndView;
		}
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "C", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView("createschema");
			modelAndView.addObject("currentSection", "Data Connection");
			modelAndView.addObject("currentLink", "DCAdd New");
			modelAndView.addObject("healthCheck", healthCheck);
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	// Adding fields for Hive(Knox)

	@RequestMapping(value = "/saveSchema", method = RequestMethod.POST)
	public void getSchemaDatagetSchemaData(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String schemaName, @RequestParam String schemaType, @RequestParam String uri,
			@RequestParam String database, @RequestParam String username, @RequestParam String password,
			@RequestParam String port, @RequestParam String domain, @RequestParam String serviceName,
			@RequestParam String krb5conf, @RequestParam String autoGenerateId, @RequestParam String hivejdbchost,
			@RequestParam String hivejdbcport, @RequestParam String suffix, @RequestParam String prefix,
			@RequestParam String sslEnb, @RequestParam String sslTrustStorePath, @RequestParam String trustPassword,
			@RequestParam String gatewayPath, @RequestParam String jksPath, @RequestParam String zookeeperUrl,
			@RequestParam String folderPath, @RequestParam String fileNamePattern, @RequestParam String fileDataFormat,
			@RequestParam String headerPresent, @RequestParam String partitionedFolders,
			@RequestParam String headerFilePath, @RequestParam String headerFileNamePattern,
			@RequestParam String headerFileDataFormat, @RequestParam String accessKey, @RequestParam String secretKey,
			@RequestParam String bucketName, @RequestParam String bigQueryProjectName,
			@RequestParam String privatekeyId, @RequestParam String privatekey, @RequestParam String clientId,
			@RequestParam String clientEmail, @RequestParam String datasetName, @RequestParam String azureClientId,
			@RequestParam String azureClientSecret, @RequestParam String azureTenantId,
			@RequestParam String azureServiceURI, @RequestParam String azureFilePath,
			@RequestParam String enableFileMonitoring, @RequestParam String multiPattern,
			@RequestParam int startingUniqueCharCount, @RequestParam int endingUniqueCharCount,
			@RequestParam int maxFolderDepth, @RequestParam String fileEncrypted, @RequestParam String singleFile,
			@RequestParam String externalFileNamePatternId, @RequestParam String externalFileName,
			@RequestParam String patternColumn, @RequestParam String headerColumn, @RequestParam String localDirectoryColumnIndex,String xsltFolderPath,
			@RequestParam String kmsAuthDisabled, @RequestParam String readLatestPartition, @RequestParam String alation_integration_enabled,
		  	@RequestParam String  incrementalDataReadEnabled,@RequestParam String clusterPropertyCategory, @RequestParam String multiFolderEnabled,
		  	@RequestParam String pushDownQueryEnabled,@RequestParam String azureAuthenticationType) {

		System.out.println("\n====> saveSchema - START ");

		JSONObject json = new JSONObject();
		String status="failed";
		String message="";

		if(!(clusterPropertyCategory.trim().equalsIgnoreCase("cluster"))){
			//If no proxy connections are given revert submitted clusterCategory to cluster
			try{
				String proxy_connections= appDbConnectionProperties.getProperty("proxy_connections");
				if(proxy_connections!=null && !proxy_connections.trim().isEmpty()){
					if(!proxy_connections.contains(clusterPropertyCategory)){
						clusterPropertyCategory="cluster";
						System.out.println("\n====>Proxy connections does not contain selected clusterCategory so reverting it to cluster");
					}
				}else{
					clusterPropertyCategory="cluster";
					System.out.println("\n====>No proxy connections are provided so reverting clusterCategory to cluster");
				}
			}catch (Exception e){
				//e.printStackTrace();
				System.out.println("\n====>No proxy connections are provided so reverting clusterCategory to cluster");
				clusterPropertyCategory="cluster";
			}
		}

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {
				if(incrementalDataReadEnabled != null && incrementalDataReadEnabled.trim().equalsIgnoreCase("Y")) {
					incrementalDataReadEnabled = "Y";
				}
				else {
					incrementalDataReadEnabled = "N";
				}
				System.out.println("=======================>incrementalDataReadEnabled="+incrementalDataReadEnabled);
				System.out.println("\n====>multiFolderEnabled="+multiFolderEnabled);
				
				Long projectId = (Long) session.getAttribute("projectId");
				Integer domainId = (Integer) session.getAttribute("domainId");
				long idUser = (Long) session.getAttribute("idUser");

				// Active Directory flag check
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				String createdByUser = "";
				if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
					createdByUser = (String) session.getAttribute("createdByUser");
					System.out.println("======= createdByUser ===>" + createdByUser);

				} else {
					// getting createdBy username from createdBy userId
					System.out.println("======= idUser ===>" + idUser);

					createdByUser = userDAO.getUserNameByUserId(idUser);

					System.out.println("======= createdByUser ===>" + createdByUser);
				}
				
				long idDataSchema = dataConnectionService.saveConnectionInfo(projectId, domainId, idUser, createdByUser, schemaName, schemaType, uri,
						database, username, password, port, domain, serviceName, krb5conf, autoGenerateId, hivejdbchost,
						hivejdbcport, suffix, prefix, sslEnb, sslTrustStorePath, trustPassword, gatewayPath, jksPath,
						zookeeperUrl, folderPath, fileNamePattern, fileDataFormat, headerPresent, partitionedFolders,
						headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,
						bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
						azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath,
						enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
						maxFolderDepth, fileEncrypted, singleFile, externalFileNamePatternId, externalFileName,
						patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath, kmsAuthDisabled,
						readLatestPartition,alation_integration_enabled,incrementalDataReadEnabled,clusterPropertyCategory,multiFolderEnabled,pushDownQueryEnabled,"","",azureAuthenticationType);

				if (idDataSchema > 0l) {
					status = "success";
					message = "Data Connection Created Successfully";
					json.put("idDataSchema", idDataSchema);

					System.out.println("\n====> Data Connection Created Successfully");
				} else {
					message = "Data Connection failed,Please check Configuration";

					System.out.println("\n====> Data Connection failed");
				}
			} else
				response.sendRedirect("loginPage.jsp");

		} catch (IOException e) {
			e.printStackTrace();
			message = "Data Connection failed,Please check Configuration";
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		try {
			json.put("status",status);
			json.put("message",message);
			response.getWriter().println(json);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value = "/publishSchemaSummaryToAlation", method = RequestMethod.POST)
	public void publishSchemaSummaryToAlation(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam long idDataSchema) {
		System.out.println("\n====> publishSchemaSummaryToAlation - START ");
		JSONObject json = new JSONObject();
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				List<ListDataSchema> dataConnectionList = listdatasourcedao
						.getListDataSchemaForIdDataSchema(idDataSchema);

				if (dataConnectionList != null && dataConnectionList.size() > 0) {

					JSONObject alationResultObj = alationIntegrationService.publishSchemaSummaryToAlation(idDataSchema);
							
					if (alationResultObj != null) {

						String alationMessage = alationResultObj.getString("message");

						if (alationResultObj.getString("status").trim().equalsIgnoreCase("success")) {
							status = "success";
							message = "Schema summary is published to Alation successfully";
						} else
							message = alationMessage;
					} else
						message = "Publishing Schema summary to Alation is failed";
				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Publishing Schema summary to Alation is failed";
			e.printStackTrace();
		}

		System.out.println("\n====> publishSchemaSummaryToAlation - END ");
		try {
			json.put("status", status);
			json.put("message", message);
			response.getWriter().println(json);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/runHealthCheck", method = RequestMethod.POST)
	public void runHealthCheck(HttpServletRequest req, HttpServletResponse response, HttpSession session,
	   @RequestParam String schemaName, @RequestParam String schemaType, @RequestParam String uri,
	   @RequestParam String database, @RequestParam String username, @RequestParam String password,
	   @RequestParam String port, @RequestParam String domain, @RequestParam String serviceName,
	   @RequestParam String krb5conf, @RequestParam String autoGenerateId, @RequestParam String hivejdbchost,
	   @RequestParam String hivejdbcport, @RequestParam String suffix, @RequestParam String prefix,
	   @RequestParam String sslEnb, @RequestParam String sslTrustStorePath, @RequestParam String trustPassword,
	   @RequestParam String gatewayPath, @RequestParam String jksPath, @RequestParam String zookeeperUrl,
	   @RequestParam String folderPath, @RequestParam String fileNamePattern, @RequestParam String fileDataFormat,
	   @RequestParam String headerPresent, @RequestParam String partitionedFolders,
	   @RequestParam String headerFilePath, @RequestParam String headerFileNamePattern,
	   @RequestParam String headerFileDataFormat, @RequestParam String accessKey, @RequestParam String secretKey,
	   @RequestParam String bucketName, @RequestParam String bigQueryProjectName,
	   @RequestParam String privatekeyId, @RequestParam String privatekey, @RequestParam String clientId,
	   @RequestParam String clientEmail, @RequestParam String datasetName, @RequestParam String azureClientId,
	   @RequestParam String azureClientSecret, @RequestParam String azureTenantId,
	   @RequestParam String azureServiceURI, @RequestParam String azureFilePath,
	   @RequestParam String enableFileMonitoring, @RequestParam String multiPattern,
	   @RequestParam int startingUniqueCharCount, @RequestParam int endingUniqueCharCount,
	   @RequestParam int maxFolderDepth, @RequestParam String fileEncrypted, @RequestParam String singleFile,
	   @RequestParam String externalFileNamePatternId, @RequestParam String externalFileName,
	   @RequestParam String patternColumn, @RequestParam String headerColumn, @RequestParam String localDirectoryColumnIndex,String xsltFolderPath,
	   @RequestParam String kmsAuthDisabled, @RequestParam String readLatestPartition,@RequestParam String alation_integration_enabled,
	   @RequestParam String incrementalDataReadEnabled,@RequestParam String clusterPropertyCategory,@RequestParam String multiFolderEnabled,
	   @RequestParam String pushDownQueryEnabled,@RequestParam String azureAuthenticationType) {
		
		System.out.println("\n====> runHealthCheck - START ");

		JSONObject json = new JSONObject();
		String status="failed";
		String message="";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {
				
				Long projectId = (Long) session.getAttribute("projectId");
				Integer domainId = (Integer) session.getAttribute("domainId");
				long idUser = (Long) session.getAttribute("idUser");

				// Active Directory flag check
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");

				String createdByUser = "";
				if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
					createdByUser = (String) session.getAttribute("createdByUser");
					System.out.println("======= createdByUser ===>" + createdByUser);

				} else {
					// getting createdBy username from createdBy userId
					System.out.println("======= idUser ===>" + idUser);

					createdByUser = userDAO.getUserNameByUserId(idUser);

					System.out.println("======= createdByUser ===>" + createdByUser);
				}

				long idDataSchema = dataConnectionService.saveConnectionInfo(projectId,domainId,idUser,createdByUser, schemaName, schemaType, uri,
						database, username, password, port, domain, serviceName, krb5conf, autoGenerateId, hivejdbchost,
						hivejdbcport, suffix, prefix, sslEnb, sslTrustStorePath, trustPassword, gatewayPath, jksPath,
						zookeeperUrl, folderPath, fileNamePattern, fileDataFormat, headerPresent, partitionedFolders,
						headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey, bucketName,
						bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
						azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath,
						enableFileMonitoring, multiPattern, startingUniqueCharCount, endingUniqueCharCount,
						maxFolderDepth, fileEncrypted, singleFile, externalFileNamePatternId, externalFileName,
						patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath, kmsAuthDisabled,
						readLatestPartition,alation_integration_enabled, incrementalDataReadEnabled,clusterPropertyCategory,multiFolderEnabled,pushDownQueryEnabled,"","",azureAuthenticationType);

				if (idDataSchema > 0l) {
					message = "Data Connection Created.";
					System.out.println("\n====> Data Connection Created Successfully");

					json = dataConnectionService.runSchemaJob(idDataSchema, "Y");

					String healthCheckStatus = json.getString("status");
					if(healthCheckStatus.equalsIgnoreCase("success")){
						status="success";
					}
					String healthCheckMessage = json.getString("message");

					message = message + healthCheckMessage;

				} else {
					message ="Data Connection failed,Please check Configuration";
					System.out.println("\n====> Data Connection failed");
				}
			} else
				response.sendRedirect("loginPage.jsp");
		} catch (Exception e) {
			e.printStackTrace();
			message ="Data Connection failed,Please check Configuration";
		}
		try {
			json.put("status",status);
			json.put("message",message);
			response.getWriter().println(json);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value = "/statusBarAutoDt", method = RequestMethod.POST, produces = "application/json")
	public void startStatuspoll(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		Long statusForAutoDT = SchemaDAOI.getStatusForAutoDT();
		JSONObject json = new JSONObject();
		json.put("percentage", statusForAutoDT);
		System.out.println(json);
		// json.put("appName", appName);
		try {
			response.getWriter().println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/isAlationIntegrationEnabled", method = RequestMethod.POST)
	public void isAlationIntegrationEnabled(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String schemaType, @RequestParam String schemaName) {
		System.out.println("Inside method /isAlationIntegrationEnabled");
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				JSONObject alation_integration_json = alationIntegrationService
						.isAlationEnabledForDatabaseSchema(schemaType, schemaName);

				String alation_status = alation_integration_json.getString("status");
				String alation_message = alation_integration_json.getString("message");

				if (alation_status != null && alation_status.trim().equalsIgnoreCase("success")) {
					status = "success";
					message= "Alation Integration is enabled for schema["+schemaName+"]";
				} else
					message = alation_message;

			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "Failed to determine Alation Integration !!";
		}
		try {
			json.put("status", status);
			json.put("message", message);
			response.getWriter().println(json);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value = "/updateSchema", method = RequestMethod.POST)
	public void updateSchema(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String schemaName, @RequestParam String schemaType, @RequestParam String uri,
			@RequestParam String database, @RequestParam String username, @RequestParam String password,
			@RequestParam String port, @RequestParam String domain, @RequestParam String hivejdbchost,
			@RequestParam String hivejdbcport, @RequestParam String serviceName, @RequestParam long idDataSchema,
			@RequestParam String krb5conf, @RequestParam String sslTrustStorePath, @RequestParam String trustPassword,
			@RequestParam String gatewayPath, @RequestParam String jksPath, @RequestParam String zookeeperUrl,
			@RequestParam String folderPath, @RequestParam String fileNamePattern, @RequestParam String fileDataFormat,
			@RequestParam String headerPresent, @RequestParam String partitionedFolders,
			@RequestParam String headerFilePath, @RequestParam String headerFileNamePattern,
			@RequestParam String headerFileDataFormat, @RequestParam String accessKey, @RequestParam String secretKey,
			@RequestParam String bucketName, @RequestParam String bigQueryProjectName,
			@RequestParam String privatekeyId, @RequestParam String privatekey, @RequestParam String clientId,
			@RequestParam String clientEmail, @RequestParam String datasetName, @RequestParam String azureClientId,
			@RequestParam String azureClientSecret, @RequestParam String azureTenantId,
			@RequestParam String azureServiceURI, @RequestParam String azureFilePath, String multiPattern,
			int startingUniqueCharCount, int endingUniqueCharCount, @RequestParam int maxFolderDepth,
			@RequestParam String fileEncrypted, @RequestParam String singleFile,
			@RequestParam String externalFileNamePatternId, @RequestParam String externalFileName,
			@RequestParam String patternColumn, @RequestParam String headerColumn,
			@RequestParam String localDirectoryColumnIndex, @RequestParam String xsltFolderPath, 
			@RequestParam String kmsAuthDisabled, @RequestParam String readLatestPartition,
			@RequestParam String enableFileMonitoring,@RequestParam String alation_integration_enabled,@RequestParam String incrementalDataReadEnabled,
			@RequestParam String multiFolderEnabled,@RequestParam String sslEnb, @RequestParam String pushDownQueryEnabled, @RequestParam String azureAuthenticationType) {
		
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("password=" + password);
		String results = "";
		// System.out.println("schemaName="+schemaName);
		System.out.println("schemaType=" + schemaType);
		if (schemaType.equalsIgnoreCase("oracle")) {
			port = port + "/" + serviceName;
		}

		if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
			uri = azureServiceURI;
		}
		// String name = SchemaDAOI.duplicateSchemaName(schemaName);
		// System.out.println("duplicateatabasename=" + name);
		// if (name == null || name == "") {
		System.out.println("@@@@@@@@@@@@@ ====> Inside updateSchema(-)......");
		List<ListDataSchema> listDataSchemaList = dataTemplateAddNewDAO.getListDataSchema(idDataSchema);
		ListDataSchema listDataSchema = listDataSchemaList.get(0);
		String enableFileMonitoring_Old=listDataSchema.getEnableFileMonitoring();
		
		try {
			if(incrementalDataReadEnabled != null && incrementalDataReadEnabled.trim().equalsIgnoreCase("Y")) {
				incrementalDataReadEnabled = "Y";
			}
			else {
				incrementalDataReadEnabled = "N";
			}
			Long update = SchemaDAOI.updateDataIntoListDataSchema(uri, database, username, password, port, schemaName,
					schemaType, domain, idDataSchema, serviceName, krb5conf, hivejdbchost, hivejdbcport, sslTrustStorePath,
					trustPassword, gatewayPath, jksPath, zookeeperUrl, folderPath, fileNamePattern, fileDataFormat,
					headerPresent, headerFilePath, headerFileNamePattern, headerFileDataFormat, accessKey, secretKey,
					bucketName, bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName,
					azureClientId, azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, partitionedFolders,
					multiPattern, startingUniqueCharCount, endingUniqueCharCount, maxFolderDepth, fileEncrypted,
					singleFile, externalFileNamePatternId, externalFileName, patternColumn, headerColumn, localDirectoryColumnIndex, xsltFolderPath,
					kmsAuthDisabled, readLatestPartition, enableFileMonitoring, alation_integration_enabled, incrementalDataReadEnabled, multiFolderEnabled, sslEnb, pushDownQueryEnabled,"","",azureAuthenticationType,"");
			System.out.println("update=" + update+" enableFileMonitoring= "+enableFileMonitoring);
			
			// update FileMonitoring rules associated with the idDataSchema
			if (schemaType.equalsIgnoreCase("S3 IAMRole Batch") || schemaType.equalsIgnoreCase("S3 IAMRole Batch Config") || schemaType.equalsIgnoreCase("S3 Batch")
					|| schemaType.equalsIgnoreCase("FileSystem Batch") || schemaType.equalsIgnoreCase("SnowFlake") || schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
				List<FileMonitorRules> fmRulesList = fileMonitorDao.getFileMonitorRulesForSchema(idDataSchema);

				if (fmRulesList != null && fmRulesList.size() > 0) {
					for (FileMonitorRules fmRule : fmRulesList) {
						fmRule.setFolderPath(folderPath);
						fmRule.setFilePattern(fileNamePattern);
						fmRule.setPartitionedFolders(partitionedFolders);
						if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")
								|| schemaType.equalsIgnoreCase("S3 Batch") || schemaType.equalsIgnoreCase("S3 IAMRole Batch Config")) {
							fmRule.setBucketName(bucketName);
						}
						fileMonitorDao.addMonitorRule(fmRule);
					}
				}
				dataConnectionService.enableOrDisableFileMonitoringForConnection(idDataSchema, enableFileMonitoring, enableFileMonitoring_Old);
			}
			
			if (update > 0l) {
//				status = "success";
//				message = "Data Connection Customized Successfully";
//				json.put("idDataSchema", update);

				System.out.println("Data Connection Customized Successfully");
				response.getWriter().println("Data Connection Customized Successfully");
			} else {
				response.getWriter().println("Data Connection Failed, Please check Configuration");
				System.out.println("\n====> Data Connection Customized failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().println("Data Connection Failed, Please check Configuration");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

//		try {
//			System.out.println("Data Connection Customized Successfully");
//			response.getWriter().println("Data Connection Customized Successfully");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		/*
		 * } else { try { System.out.println("Schema Name already exists");
		 * response.getWriter().println("Schema Name already exists"); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); } }
		 */
	}

	@RequestMapping(value = "/testDataConnection", method = RequestMethod.POST)
	public void testDataConnection(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String schemaName, @RequestParam String schemaType, @RequestParam String uri,
			@RequestParam String database, @RequestParam String username, @RequestParam String password,
			@RequestParam String port, @RequestParam String domain, @RequestParam String serviceName,
			@RequestParam String krb5conf, @RequestParam long idDataSchema, @RequestParam String gatewayPath,
			@RequestParam String jksPath, @RequestParam String folderPath, @RequestParam String fileNamePattern,
			@RequestParam String fileDataFormat, @RequestParam String headerPresent,
			@RequestParam String partitionedFolders, @RequestParam String headerFilePath,
			@RequestParam String headerFileNamePattern, @RequestParam String headerFileDataFormat,
			@RequestParam String accessKey, @RequestParam String secretKey, @RequestParam String bucketName,
			@RequestParam String bigQueryProjectName, @RequestParam String privatekeyId,
			@RequestParam String privatekey, @RequestParam String clientId, @RequestParam String clientEmail,
			@RequestParam String datasetName, @RequestParam String azureClientId,
			@RequestParam String azureClientSecret, @RequestParam String azureTenantId,
			@RequestParam String azureServiceURI, @RequestParam String azureFilePath, @RequestParam String multiPattern,
			@RequestParam int startingUniqueCharCount, @RequestParam int endingUniqueCharCount,
			@RequestParam int maxFolderDepth, @RequestParam String fileEncrypted, @RequestParam String kmsAuthDisabled,
		    @RequestParam String incrementalDataReadEnabled, @RequestParam String sslEnb ) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (password.trim().length() > 1) {

		} else {
			password = SchemaDAOI.getPasswordForIdDataSchema(idDataSchema);
		}
		
		if (secretKey == null || secretKey.trim().isEmpty()) {
			secretKey = SchemaDAOI.getSecretKeyForIdDataSchema(idDataSchema);
		} 
		
		//System.out.println("password=" + password);
		System.out.println("schemaType=" + schemaType);
		Connection con = null;
		boolean mapRConn = false;
		CqlSession cassandraSession = null;
		boolean fileSystemBatchCon = false;
		boolean s3BatchCon = false;
		boolean bigQueryCon = false;
		boolean azureDataLakeCon = false;
		boolean azureDataLakeGen2BatchCon= false;
		boolean flag = true;
		
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
				System.out.println("\n====>KMS Authentication is not supported for [" + schemaType + "] !!");
			}
		}
		
		if(flag) {
			if (schemaType.equalsIgnoreCase("Oracle")) {
	
				port = port + "/" + serviceName;
	
				String url = "jdbc:oracle:thin:@" + uri + ":" + port;
				try {
					Class.forName("oracle.jdbc.driver.OracleDriver");
					con = DriverManager.getConnection(url, username, password);
					// System.out.println(con);
				} catch (Exception e) {
					// System.out.println(con);
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
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
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
	
			} else if (schemaType.equalsIgnoreCase("MSSQL")) {
	
				String url = "jdbc:sqlserver://" + uri + ":" + port+ ";encrypt=true;trustServerCertificate=true;";
				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
					con = DriverManager.getConnection(url, username, password);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
	
			} else if (schemaType.equalsIgnoreCase("Postgres")) {
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
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
	
			} else if (schemaType.equalsIgnoreCase("Teradata")) {
				String[] dbAndSchema = database.split(",");
	
				String url = "jdbc:teradata://" + uri;
				try {
					Class.forName("com.teradata.jdbc.TeraDriver");
					con = DriverManager.getConnection(url, username, password);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			} else if (schemaType.equalsIgnoreCase("MSSQLActiveDirectory")) {
	
				try {
					Class.forName("net.sourceforge.jtds.jdbc.Driver");
					String db_connect_string = "jdbc:jtds:sqlserver://" + uri.trim() + ":" + port.trim() + ";domain="
							+ domain.trim();
					con = DriverManager.getConnection(db_connect_string, username.trim(), password.trim());
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
	
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
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
	
			} else if (schemaType.equalsIgnoreCase("Cassandra")) {
				try {
					CqlSessionBuilder builder = CqlSession.builder();
			        builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port))).withAuthCredentials(username, password);

			        cassandraSession = builder.build();
					cassandraSession.close();
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			} else if (schemaType.equalsIgnoreCase("Hive") || schemaType.equalsIgnoreCase("Cloudera Hive")) {
				try {
					String dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database;
					Class.forName("org.apache.hive.jdbc.HiveDriver");
					con = DriverManager.getConnection(dbURL2, username, password);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
	
				}
			} else if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
				try {
					System.setProperty("java.security.auth.login.config", serviceName);
					// System.setProperty("sun.security.jgss.debug","true");
					System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
					System.setProperty("java.security.krb5.conf", krb5conf);
					Class.forName("org.apache.hive.jdbc.HiveDriver");
	
					// In case of zookeeper url there is no need of principal name
					/*
					String zookeeperURL = hiveConnectionURL(uri, port, database);
					if (zookeeperURL != null)
						url = "jdbc:hive2://" + zookeeperURL;
					else
						url = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain;
					*/
					
					String url = null;
					if(uri.contains("2181") && jksPath !=null && !jksPath.trim().isEmpty()) {
						url = "jdbc:hive2://" + uri + "/" + database
								+";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;principal=" + domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
								+ password;
					} else if(jksPath !=null && !jksPath.trim().isEmpty()) {
						url = "jdbc:hive2://" + uri + ":" + port +"/" + database
								+";principal=" + domain + ";ssl=true;sslTrustStore=" + jksPath + ";trustStorePassword="
								+ password;
					}
					else
						{
						//dbURL2 = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain;
						String zookeeperURL = dataConnectionService.hiveConnectionURL(uri, port, database);
						if (zookeeperURL != null)
							url = "jdbc:hive2://" + zookeeperURL;
						else
							url = "jdbc:hive2://" + uri + ":" + port + "/" + database + ";principal=" + domain;
						}
	
					con = DriverManager.getConnection(url);
				} catch (Exception e) {
					try {
						JSONObject json = new JSONObject();
						json.put("fail", "Data Connection failed,Please check Configuration");
						response.getWriter().println(json);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
	
				}
	
			} else if (schemaType.equalsIgnoreCase("MapR Hive")) {
				try {

					// Get the domain name
					Integer domainId = (Integer) session.getAttribute("domainId");
					String domainName = iTaskDAO.getDomainNameById(domainId.longValue());

					String clusterPropertyCategory = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema).get("cluster_property_category");
					System.out.println("\n====>clusterPropertyCategory:"+clusterPropertyCategory);

					if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
							&& !clusterPropertyCategory.equalsIgnoreCase("cluster")){


						String proxyConnectionList= appDbConnectionProperties.getProperty("proxy_connections");
						boolean isRemoteIp=false;
						if(proxyConnectionList.contains(clusterPropertyCategory.trim())){
							try{
								String proxy_uri= appDbConnectionProperties.getProperty("proxy_"+clusterPropertyCategory.trim());
								if(proxy_uri!=null && !proxy_uri.isEmpty()){
									isRemoteIp=true;
								}else
									System.out.println("Remote connection is not enabled for:"+clusterPropertyCategory);
							}catch (Exception e){
								System.out.println("Remote connection is not enabled for:"+clusterPropertyCategory);
							}

						}

						if(isRemoteIp){
//							String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";
							String publishUrl= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.DATA_CONNECTION_API);
							String token= remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

							StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
							encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

							String encryptedToken = encryptor.encrypt(token);

							JSONObject inputObj= new JSONObject();

							inputObj.put("domainName",domainName);
							inputObj.put("uri",uri);
							inputObj.put("port",port);
							inputObj.put("database",database);
							//inputObj.put("propertySource",""+propertySource);


							String inputJson= inputObj.toString();
							System.out.println("inputJson="+inputJson);

							boolean testConnStatus=remoteClusterAPIService.testConnectionByRemoteCluster(publishUrl,encryptedToken,inputJson);

							if(testConnStatus){
								System.out.println("Success!");
								mapRConn = true;
							}else{
								System.out.println("Failure!");
							}
						}else{
							System.out.println("Connection id["+idDataSchema+"] does not have remote ip support");
						}


					}else{
						String databuckHome = DatabuckUtility.getDatabuckHome();

						ProcessBuilder processBuilder = new ProcessBuilder();
						// Need the get the project name
						processBuilder.command("bash", "-c", "sh " + databuckHome + "/scripts/HiveTable.sh " + domainName
								+ " com.databuck.mapr.hive.ValidateHiveConnection " + uri + ":" + port + " " + database);

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

						System.out.println(output);

						if (exitVal == 0 && successfulConnection) {
							System.out.println("Success!");
							mapRConn = true;
							// System.exit(0);
						} else {
							// abnormal...
							System.out.println("Failure!");
						}
					}


					if(!mapRConn) {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					}

				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}

			} else if (schemaType.equalsIgnoreCase("Amazon Redshift")) {
				try {
					// String dbURL2 = "jdbc:hive2://"+uri+":"+port+"/"+database;
					Class.forName("com.amazon.redshift.jdbc42.Driver");
					Properties props = new Properties();
					props.setProperty("user", username);
					props.setProperty("password", password);
					con = DriverManager.getConnection("jdbc:redshift://" + uri + ":" + port + "/" + database, props);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
	
				}
			} else if (schemaType.equalsIgnoreCase("MySQL")) { // changes for import mysql connection 29jan2019
	
				String url = "jdbc:mysql://" + uri + ":" + port;
				try {
					Class.forName("com.mysql.jdbc.Driver");

					if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y")){
						url = "jdbc:mysql://" + uri + ":" + port +"/"+database +"?verifyServerCertificate=false&useSSL=true";
					}
					con = DriverManager.getConnection(url, username, password);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
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
				System.out.println("In MapR =" + url);
	
				try {
					Class.forName("org.apache.hive.jdbc.HiveDriver");
					con = DriverManager.getConnection(url, username, password);
				} catch (Exception e) {
	
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
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
	
				System.out.println("In Hive Knox =>" + url);
	
				try {
					// load Hive JDBC Driver
					Class.forName("org.apache.hive.jdbc.HiveDriver");
	
					// configure JDBC connection
					con = DriverManager.getConnection(url, username, password);
	
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			} else if (schemaType.equalsIgnoreCase("SnowFlake")) {
				String[] dbAndSchema = database.split(",");
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
				System.out.println(url);
	
				// String
				// url="jdbc:snowflake://vs90372.us-east-1.snowflakecomputing.com/"+"?warehouse=SF_TUTS_WH&db="+
				// "TEST"+"&schema=TEST1";
				try {
					Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
					con = DriverManager.getConnection(url, username, password);
				} catch (Exception e) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
	
			// --------------------
	
			// ----------- Added FileSystem Batch connection
			else if (schemaType.equalsIgnoreCase("FileSystem Batch")) {
				System.out.println("\n====> FileSystem Batch details <====");
	
				System.out.println("\n** FolderPath: " + folderPath);
				System.out.println("\n** FileName Pattern: " + fileNamePattern);
				System.out.println("\n** File Data Format: " + fileDataFormat);
				System.out.println("\n** Header Present: " + headerPresent);
				System.out.println("\n** Header File Path: " + headerFilePath);
				System.out.println("\n** Header FileName Pattern: " + headerFileNamePattern);
				System.out.println("\n** Header File Data Pattern: " + headerFileDataFormat);
	
				try {
					System.out.println("\n====> Checking if the Folder Path exist ...");
	
					File f_folder = new File(folderPath);
	
					System.out.println("\n====> Checking if the Folder Path exist ...");
	
					if (f_folder.exists() && f_folder.isDirectory()) {
	
						System.out.println("\n====> Folder Path exits is true !!");
	
						if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
							System.out.println("\n====> File has header, hence no header path checking !!");
	
							System.out.println("\n====> Connection Established Successfully !!");
							fileSystemBatchCon = true;
	
						} else {
							System.out.println("\n====> Checking if Header Path exist ...");
	
							File h_folder = new File(headerFilePath);
	
							if (h_folder.exists() && h_folder.isDirectory()) {
								System.out.println("\n====> Header Path exits is true !!");
	
								System.out.println("\n====> Data Connection successful !!");
	
								fileSystemBatchCon = true;
	
							} else {
								System.out.println("\n====> Header Path does not exist !!");
	
								System.out.println("\n====> Data Connection failed !!");
							}
	
						}
	
					} else {
						System.out.println("\n====> Folder Path does not exist !!");
	
						System.out.println("\n====> Data Connection failed !!");
					}
	
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				if (!fileSystemBatchCon) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
	
			// ----------- Added S3 Batch connection
			else if (schemaType.equalsIgnoreCase("S3 Batch")) {
	
				System.out.println("\n====> S3 Batch details <====");
	
				System.out.println("\n** bucketName: " + bucketName);
				System.out.println("\n** FolderPath: " + folderPath);
				System.out.println("\n** FileName Pattern: " + fileNamePattern);
				System.out.println("\n** File Data Format: " + fileDataFormat);
				System.out.println("\n** Header Present: " + headerPresent);
				System.out.println("\n** Header File Path: " + headerFilePath);
				System.out.println("\n** Header FileName Pattern: " + headerFileNamePattern);
				System.out.println("\n** Header File Data Pattern: " + headerFileDataFormat);
	
				try {
					System.out.println("\n====> Checking if the Folder Path exist ...");
	
					AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
	
					S3Object folderPathKey = s3Client.getObject(bucketName, folderPath);
	
					if (folderPathKey != null) {
	
						System.out.println("\n====> Folder Path exits is true !!");
	
						if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
							System.out.println("\n====> File has header, hence no header path checking !!");
	
							System.out.println("\n====> Data Connection successful !!");
	
							s3BatchCon = true;
	
						} else {
							System.out.println("\n====> Checking if Header Path exist ...");
	
							S3Object headerFolderPathKey = s3Client.getObject(bucketName, headerFilePath);
	
							if (headerFolderPathKey != null) {
								System.out.println("\n====> Header Path exits is true !!");
	
								System.out.println("\n====> Data Connection successful !!");
	
								s3BatchCon = true;
	
							} else {
								System.out.println("\n====> Header Path does not exist !!");
	
								System.out.println("\n====> Data Connection failed !!");
							}
						}
	
					} else {
						System.out.println("\n====> Folder Path does not exist !!");
	
						System.out.println("\n====> Data Connection failed !!");
					}
	
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				if (!s3BatchCon) {
					try {
						response.getWriter().println("Data Connection failed,Please check Configuration");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
	
			if (schemaType.equalsIgnoreCase("S3 IAMRole Batch")) {
	
				System.out.println("\n====> S3 IAMRole Batch details <====");
	
				System.out.println("\n** bucketName: " + bucketName);
				System.out.println("\n** FolderPath: " + folderPath);
				System.out.println("\n** FileName Pattern: " + fileNamePattern);
				System.out.println("\n** File Data Format: " + fileDataFormat);
				System.out.println("\n** Header Present: " + headerPresent);
				System.out.println("\n** Header File Path: " + headerFilePath);
				System.out.println("\n** Header FileName Pattern: " + headerFileNamePattern);
				System.out.println("\n** Header File Data Pattern: " + headerFileDataFormat);
	
				try {
					System.out.println("\n====> Checking if the Folder Path exist ...");
	
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
	
						System.out.println("\n====> Folder Path exits is true !!");
	
						if (headerPresent != null && headerPresent.equalsIgnoreCase("Y")) {
							System.out.println("\n====> File has header, hence no header path checking !!");
	
							System.out.println("\n====> Data Connection successful !!");
	
							s3BatchCon = true;
						} else {
							System.out.println("\n====> Checking if Header Path exist ...");
	
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
								System.out.println("\n====> Header Path exits is true !!");
	
								System.out.println("\n====> Data Connection successful !!");
	
								s3BatchCon = true;
	
							} else {
								System.out.println("\n====> Header Path does not exist !!");
	
								System.out.println("\n====> Data Connection failed !!");
							}
						}
	
					} else {
						System.out.println("\n====> Folder Path does not exist !!");
	
						System.out.println("\n====> Data Connection failed !!");
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
									bigQueryProjectName, privatekeyId, privatekey, clientId, clientEmail, datasetName);
	
							if (tablesList != null && tablesList.size() > 0) {
								bigQueryCon = true;
							} else {
								System.out.println("\n====> Failed to connect to Dataset or no tables found!!");
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
					System.out.println("accessToken : " + accessToken);
					azureDataLakeCon = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (schemaType.equalsIgnoreCase("AzureDataLakeStorageGen2Batch")) {
				azureDataLakeGen2BatchCon = azureDataLakeGen2Connection.validateConnection(accessKey,secretKey,bucketName,folderPath);
			}
	
			if (con != null || cassandraSession != null || fileSystemBatchCon || s3BatchCon || bigQueryCon
					|| azureDataLakeCon || mapRConn || azureDataLakeGen2BatchCon) {
				try {
					// Closing the connection
					if(con != null) {
						con.close();
					}
					
					if(cassandraSession != null) {
						cassandraSession.close();
					}
					
					System.out.println("Connection Established Successfully");
					response.getWriter().println("Connection Established Successfully");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		} else {
			System.out.println("\n====>Failed to fetch connection details from logon manager !!");
			try {
				response.getWriter().println("Data Connection failed,Please check Configuration");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/saveDataTemplateDataInListDataDefinition", method = RequestMethod.POST)
	public void saveDataTemplateDataInListDataDefinition(HttpServletRequest req, HttpServletResponse response,
			HttpSession session, @RequestParam Long idColumn, @RequestParam String columnName,
			@RequestParam String columnValue) throws JSONException, IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (columnValue.equalsIgnoreCase("y")) {
			columnValue = "Y";
		} else if (columnValue.equalsIgnoreCase("n")) {
			columnValue = "N";
		}
		//for negative Threshold -Mamta 17/5/2022
		ArrayList<String> bl = new ArrayList<String>();
		bl.add("nullCountThreshold");
		bl.add("dataDriftThreshold");
		bl.add("recordAnomalyThreshold");
		bl.add("patternCheckThreshold");
		bl.add("badDataCheckThreshold");
		bl.add("lengthCheckThreshold");
		bl.add("numericalThreshold");
				
		if (bl.contains(columnName)) 
		{
			System.out.println("Threshold Value =>" + columnValue);
			double value = Double.parseDouble(columnValue);
		    if(value<0)
		    {
		       System.out.println(value + "Threshold is negative");
		        JSONObject json = new JSONObject();
		        json.put("fail", "Please enter positive value for Threshold");
              	response.getWriter().println(json);
              	return;
		       
		    }
		    else {
			       System.out.println(value + "Threshold is positive");
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
		

		// changes for lengthValue 4dec2019
		// al.add("lengthValue");

		/*
		 * al.add("Sachin"); al.add("Amit"); al.add("Vijay"); al.add("Kumar");
		 * al.add("Sachin");
		 */
		boolean flag = true;
		if (al.contains(columnName)) {
			if (columnValue.equalsIgnoreCase("Y") || columnValue.equalsIgnoreCase("N")) {

			} else {
				flag = false;
			}
		}
		System.out.println("idColumn=" + idColumn);
		System.out.println("columnName=" + columnName);
		System.out.println("columnValue=" + columnValue);

		String msg = "";
		if (flag) {
			if (columnName.equals("lengthValue")) {
				if ((Pattern.compile("[^0-9,]").matcher(columnValue).find())) {
					flag = false;
					msg = "Please enter length values as comma seperated";
				}
			}
		}
		JSONObject json = new JSONObject();
		if (flag) {
			Long update = SchemaDAOI.updateDataIntoStagingListDataDefinition(idColumn, columnName, columnValue);
			SchemaDAOI.updateKBEIntoStagingListDataDefinition(idColumn, columnName, columnValue);
			System.out.println("update=" + update);

			if (update == -1) {
				json = new JSONObject();

				if (columnName.equals("timelinessKey")) {
					json.put("success", "More than one business key is not allowed ");
				} else if (columnName.equals("startDate")) {
					json.put("success", "More than one start date key is not allowed ");
				} else if (columnName.equals("endDate")) {
					json.put("success", "More than one end date key is not allowed ");
				} else
					json.put("success", "Not Allowed For This Data Type");

				if (columnName.equals("lengthValue")) {
					System.out.println("If ...........lengthValue =>" + columnName);
					json.put("columnValue", "0");
				} else {
					System.out.println("Else...........lengthValue =>" + columnName);
					json.put("columnValue", "N");
				}

				response.getWriter().println(json);
			} else if (update == -3) {
				json = new JSONObject();
				System.out.println("update == -3 ................ =>" + columnName);
				json.put("success", "startDate and End date should be enabled");
				response.getWriter().println(json);
			} else if (update == -2) {
				json = new JSONObject();
				System.out.println("update == -2 ................ =>" + columnName);
				json.put("success", "Not Allowed For This Data Type");
				response.getWriter().println(json);
			} else if (update > 0) {
				json = new JSONObject();
				System.out.println("update > 0 ................ =>" + columnName);
				json.put("success", "Item Updated Successfully");
				json.put("columnValue", columnValue);
				response.getWriter().println(json);
			} else {
				json = new JSONObject();
				json.put("success", "Item failed to Update");
				response.getWriter().println(json);
			}
		} else {
			if (msg.isEmpty())
				msg = "Please write Y for Yes and N for No";
			json.put("fail", msg);
			response.getWriter().println(json);
		}
	}

	@RequestMapping(value = "/duplicateSchemaName", method = RequestMethod.POST)
	public void duplicatedatatemplatename(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long projectId = (Long) session.getAttribute("projectId");
		Integer domainId = (Integer) session.getAttribute("domainId");

		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		  String dataSchemaName = req.getParameter("val");
		  
		  //Eliminates spaces and special characters - by mamta
		  
		  if (dataSchemaName == null || dataSchemaName.trim().isEmpty()) {
		       
			  try {
					res.getWriter().println("Please Enter Connection Name");
					return;
				
				} catch (IOException e) {
					e.printStackTrace();
				} 
		     }
		  
		 /* Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");//"[^A-Za-z0-9_]"
	      Matcher match = pattern.matcher(dataSchemaName);
	      boolean val = match.find();
	      System.out.println("val"+val);
	      if (val == false)
	      {
				try {
					res.getWriter().println("Name must begin with a letter and cannot contain spaces,special characters");
					return;
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}*/
		  Pattern pattern = Pattern.compile("^[a-zA-Z][A-Za-z0-9_]*$");//"[^A-Za-z0-9_]"
	      Matcher match = pattern.matcher(dataSchemaName);
	      boolean val = match.find();
	      System.out.println("val"+val);
	      if (val == false)
	      {
				try {
					res.getWriter().println("Name must begin with a letter and cannot contain spaces,special characters");
					return;
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
		  }else {
			  Pattern pattern2 = Pattern.compile("^[A-Za-z0-9_]*$");
			  Matcher match2 = pattern2.matcher(dataSchemaName);
		      boolean val2 = match2.find();
		      if (val2 == false)
		      {
					try {
						res.getWriter().println("Name must begin with a letter and cannot contain spaces,special characters");
						return;
						
					} catch (IOException e) {
						e.printStackTrace();
					} 
			  }
		      
		  }
		// only for spaces
		/*if(dataSchemaName.matches(".*\\s.*"))
		{
			try {
				res.getWriter().println("Please Enter Schema Name without spaces.");
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}*/
		  
		
		
		// System.out.println("dataTemplateName=" + dataTemplateName);
		String name = SchemaDAOI.duplicateSchemaName(dataSchemaName,projectId,domainId);
		// System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter().println("This Schema Name is in use. Please choose another name.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/addNewBatch")
	public ModelAndView addNewBatch(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "C", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("addNewBatch");
			modelAndView.addObject("currentSection", "Data Connection");
			modelAndView.addObject("currentLink", "DCBatchAdd New");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createSchemaBatch", method = RequestMethod.POST)
	public @ResponseBody ModelAndView createSchemaBatch(@RequestParam("dataupload") MultipartFile file,
			HttpSession session, HttpServletRequest request, HttpServletResponse res) {
		Object user = session.getAttribute("user");
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		System.out.println("user:" + user);
		Integer domainId = (Integer) session.getAttribute("domainId");
		// getting createdByUser
		String createdByUser = userDAO.getUserNameByUserId(idUser);
		System.out.println("=== Created by in dataconnection==>" + createdByUser);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId = (Long) session.getAttribute("projectId");
		ModelAndView modelAndView = new ModelAndView("message");
		modelAndView.addObject("msg", "Batch Schema Created Successfully");
		String schemaBatchName = request.getParameter("schemaName");
		String schemaType = request.getParameter("schemaType");

		/* Big Query */
		String privatekey = request.getParameter("privatekey");
		String privatekeyid = request.getParameter("privatekeyid");
		String projectid = request.getParameter("projectid");
		String datasetName = request.getParameter("datasetName");
		String clientId = request.getParameter("clientId");
		String clientEmail = request.getParameter("clientEmail");
		/* Big Query */

		System.out.println("schemaName=" + schemaBatchName);
		System.out.println("schemaType=" + schemaType);
		Long idBatchSchema = SchemaDAOI.insertDataIntoListBatchSchema(schemaBatchName, schemaType,
				file.getOriginalFilename());

		// activedirectory flag check
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

		if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
			createdByUser = (String) session.getAttribute("createdByUser");
			System.out.println("======= createdByUser ===>" + createdByUser);
		} else {
			// getting createdBy username from createdBy userId
			System.out.println("======= idUser ===>" + idUser);

			createdByUser = userDAO.getUserNameByUserId(idUser);

			System.out.println("======= createdByUser ===>" + createdByUser);
		}

		if (schemaType.equalsIgnoreCase("Oracle RAC")) {
			try {
				String line = "";
				String cvsSplitBy = ",";
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				br.readLine();
				while ((line = br.readLine()) != null) {
					try {
						String[] data = line.split(cvsSplitBy);
						String schemaName = data[0];
						String uri = data[1];
						String database = data[2];
						String port = data[3];
						String username = data[4];
						String password = data[5];
						String serviceName = data[6];
						String autoGenerate = data[7];
						Long idDataSchema = SchemaDAOI.saveDataIntoListDataSchema(uri, database, username, password,
								port, schemaName, schemaType, "", serviceName, "", autoGenerate, "", "", "", "", "", "",
								"", "", "", "", "", "", "", "", "", "", "", projectId, createdByUser, "", "", "",
								projectid, privatekeyid, privatekey, clientId, clientEmail, datasetName, "", "", "", "",
								"", "N", "N", "N", 0, 0, 2, "N", domainId, "N","","","","","","","Y","N","N","N","cluster","N","N","","","");

						System.out.println("idDataSchema=" + idDataSchema);
						if (autoGenerate.equalsIgnoreCase("Y")) {
							SchemaDAOI.processAutoGenerateTemplate_oracleRAC(uri, database, username, password, port,
									schemaName, schemaType, "", serviceName, "", autoGenerate, idUser, idDataSchema,
									projectId, domainId);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
			try {
				String line = "";
				String cvsSplitBy = ",";
				InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				br.readLine();
				StringJoiner idDataSchemas = new StringJoiner(",");
				while ((line = br.readLine()) != null) {
					try {
						String[] data = line.split(cvsSplitBy);
						String schemaName = data[0];
						String principal = data[1];
						String hiveip = data[2];
						String gss_jaas = data[3];
						String krb5conf = data[4];
						String dbName = data[5];
						String port = data[6];
						String username = data[7];
						String password = data[8];
						String autoGenerate = data[9];
						String suffixes = data[10];
						String prefixes = data[11];
						Long createdBy = 1l;

						Long idDataSchema = SchemaDAOI.saveDataIntoListDataSchema(hiveip, dbName, username, password,
								port, schemaName, schemaType, principal, gss_jaas, krb5conf, autoGenerate, suffixes,
								prefixes, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", projectId,
								createdByUser, "", "", "", projectid, privatekeyid, privatekey, clientId, clientEmail,
								datasetName, "", "", "", "", "", "N", "N", "N", 0, 0, 2, "N", domainId, "N","","","","","","","Y","N","N","N","cluster","N","N","","","");

						idDataSchemas.add(idDataSchema.toString());
						/*
						 * if(autoGenerate.equalsIgnoreCase("Y")){ try { System.out.println("java -jar "
						 * + System.getenv("DATABUCK_HOME") + "/hive-kerberos-con-autodt.jar " +
						 * idDataSchema); Process proc = Runtime.getRuntime() .exec("java -jar " +
						 * System.getenv("DATABUCK_HOME") + "/hive-kerberos-con-autodt.jar " +
						 * idDataSchema); proc.waitFor(); // Then retreive the process output
						 * InputStream in = proc.getInputStream(); InputStream err =
						 * proc.getErrorStream();
						 *
						 * byte b[] = new byte[in.available()]; in.read(b, 0, b.length);
						 * System.out.println(new String(b));
						 *
						 * byte c[] = new byte[err.available()]; err.read(c, 0, c.length);
						 * System.out.println(new String(c)); } catch (Exception e) { } }
						 */
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				SchemaDAOI.updateidDataSchemasInListBatchSchema(idBatchSchema, idDataSchemas.toString());
				try {
					System.out.println("java -jar " + System.getenv("DATABUCK_HOME")
							+ "/hive-kerberos-batch-schema.jar " + idBatchSchema);
					Process proc = Runtime.getRuntime().exec("java -jar " + System.getenv("DATABUCK_HOME")
							+ "/hive-kerberos-batch-schema.jar " + idBatchSchema);
					proc.waitFor();
					// Then retreive the process output
					InputStream in = proc.getInputStream();
					InputStream err = proc.getErrorStream();

					byte b[] = new byte[in.available()];
					in.read(b, 0, b.length);
					System.out.println(new String(b));

					byte c[] = new byte[err.available()];
					err.read(c, 0, c.length);
					System.out.println(new String(c));
				} catch (Exception e) {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		modelAndView.addObject("currentSection", "Data Connection");
		modelAndView.addObject("currentLink", "DCBatchAdd New");
		return modelAndView;
	}

	@RequestMapping(value = "/updateGlobalThreshold", method = RequestMethod.POST)
	public void updateGlobalThreshold(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String idColumn, @RequestParam String columnName, @RequestParam String columnValue)
			throws JSONException, IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("idColumn.............................=" + idColumn);

		System.out.println("idColumn=" + idColumn);
		System.out.println("columnName=" + columnName);
		System.out.println("columnValue=" + columnValue);

		Double columnThresholdValue = Double.parseDouble(columnValue);
		Long update = SchemaDAOI.updateIntoListGlobalThreshold(idColumn, columnName, columnThresholdValue);

		System.out.println("update=" + update);

		if (update == -1) {
			JSONObject json = new JSONObject();

			json.put("success", "Not Allowed For This Data Type");

			response.getWriter().println(json);
		}

		else if (update > 0) {
			JSONObject json = new JSONObject();
			System.out.println("update > 0 ................ =>" + columnName);
			json.put("success", "Item Updated Successfully");
			json.put("columnValue", columnValue);
			response.getWriter().println(json);
		} else {

			JSONObject json = new JSONObject();

			json.put("success", "Item failed to Update");
			response.getWriter().println(json);
		}

	}

	@RequestMapping(value = "/insertGlobalThreshold", method = RequestMethod.POST)
	public void insertGlobalThreshold(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam String columnName, @RequestParam int domainId) throws JSONException, IOException {
		Object user = session.getAttribute("user");
		JSONObject json = new JSONObject();

		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		DateUtility.DebugLog("insertGlobalThreshold 01",
				String.format("Form Submit data DomainId = %1$s, columnName = %2$s", domainId, columnName));
		String sResult = SchemaDAOI.inserIntoListGlobalThreshold(domainId, columnName);
		DateUtility.DebugLog("insertGlobalThreshold 02", String.format("Insert result %1$s", sResult));

		if (sResult.length() > 0) {
			json.put("fail",
					String.format(
							"Failed to add new global threshold '%1$s', check for duplicate name under same domain",
							columnName));
		} else {
			json.put("success", String.format("New global threshold '%1$s' added Successfully", columnName));
		}
		response.getWriter().println(json);
	}

	@RequestMapping(value = "/activateFileMonitoring")
	public void activateFileMonitoring(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long idDataSchema) {
		try {
			Object user = session.getAttribute("user");
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage.jsp");
			}

			boolean rbac = RBACController.rbac("Data Connection", "U", session);
			if (rbac) {
				long idUser = (Long) session.getAttribute("idUser");

				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

				String createdByUser = "";
				if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
					createdByUser = (String) session.getAttribute("createdByUser");
					System.out.println("======= createdByUser ===>" + createdByUser);
				} else {
					// getting createdBy username from createdBy userId
					System.out.println("======= idUser ===>" + idUser);

					createdByUser = userDAO.getUserNameByUserId(idUser);

					System.out.println("======= createdByUser ===>" + createdByUser);
				}

				// Update enableFileMonitoring flag to 'Y'
				SchemaDAOI.updateEnableFileMonitoringFlagForSchema(idDataSchema, "Y");

				// Identify FileMonitor rules which have idDataSchema associated and get the
				// corresponding the idApp
				List<Long> fmAppList = SchemaDAOI.getFileMonitoringValidationsForSchema(idDataSchema);

				List<Long> final_fmAppList = new ArrayList<Long>();

				if (fmAppList != null && fmAppList.size() > 0) {
					// Filter invalid appId's
					for (Long appId : fmAppList) {
						if (appId != null && appId > 0l) {
							final_fmAppList.add(appId);
						}
					}
				}

				// Activate the fileMonitoring validations
				if (final_fmAppList != null && final_fmAppList.size() > 0) {
					SchemaDAOI.activateOrDeactivateFMValidations(final_fmAppList, "yes");
				} else {
					// If no FileMonitoring validations for this idDataSchema, create a new one
					dataConnectionService.createFileMonitoringValidation(idDataSchema);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deactivateFileMonitoring")
	public void deactivateFileMonitoring(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long idDataSchema) {
		try {
			Object user = session.getAttribute("user");
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage.jsp");
			}

			boolean rbac = RBACController.rbac("Data Connection", "U", session);
			if (rbac) {
				long idUser = (Long) session.getAttribute("idUser");

				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

				String createdByUser = "";
				if (activeDirectoryFlag.equalsIgnoreCase("Y")) {
					createdByUser = (String) session.getAttribute("createdByUser");
					System.out.println("======= createdByUser ===>" + createdByUser);
				} else {
					// getting createdBy username from createdBy userId
					System.out.println("======= idUser ===>" + idUser);

					createdByUser = userDAO.getUserNameByUserId(idUser);

					System.out.println("======= createdByUser ===>" + createdByUser);
				}

				// Update enableFileMonitoring flag to 'N'
				SchemaDAOI.updateEnableFileMonitoringFlagForSchema(idDataSchema, "N");

				// Identify FileMonitor rules which have idDataSchema associated and get the
				// corresponding the idApp
				List<Long> fmAppList = SchemaDAOI.getFileMonitoringValidationsForSchema(idDataSchema);

				List<Long> final_fmAppList = new ArrayList<Long>();

				if (fmAppList != null && fmAppList.size() > 0) {
					// Filter invalid appId's
					for (Long appId : fmAppList) {
						if (appId != null && appId > 0l) {
							final_fmAppList.add(appId);
						}
					}
				}

				// Deactivate the fileMonitoring validations
				if (final_fmAppList != null && final_fmAppList.size() > 0) {
					SchemaDAOI.activateOrDeactivateFMValidations(final_fmAppList, "no");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}