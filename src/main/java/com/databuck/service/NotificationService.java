package com.databuck.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import com.databuck.constants.DatabuckConstants;
import com.databuck.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.databuck.bean.NotificationTopics;
import com.databuck.config.DatabuckEnv;
import com.databuck.controller.JSONController;
import com.databuck.dao.ITaskDAO;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.security.LogonManager;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.SendEmailNotificationUtil;
import com.databuck.util.JwfSpaInfra.CustomizeDataTableColumn;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ajax.JSON;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class NotificationService {

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private SendEmailNotificationUtil sendEmailNotificationUtil;

	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	JSONController Jsoncontroller;
	
	@Autowired
	private ITaskDAO iTaskDao;
	
	@Autowired
	private LogonManager logonManager;
	
	@Autowired
	private DatabuckUtility databuckUtility;
	
	private static final Logger LOG = Logger.getLogger(NotificationService.class);

	private enum DataBuckObjectType {
		Connection(0),	DataTemplate(1), ValidationApplication(2), AppGroup(3), RunSchema(4);

	    private int value;
	    private static Map map = new HashMap<>();

	    private DataBuckObjectType(int nValue) {
	        this.value = nValue;
	    }

	    static {
	        for (DataBuckObjectType oDataBuckObjectType : DataBuckObjectType.values()) {
	            map.put(oDataBuckObjectType.value, oDataBuckObjectType);
	        }
	    }

	    public static DataBuckObjectType valueOf(int nValue) {
	        return (DataBuckObjectType) map.get(nValue);
	    }

	    public int getValue() {
	        return value;
	    }
	}

	public JSONObject SendNotification(String sTopic, HashMap<String, String> oTokens, String sUserId) {
		HashMap<String, String> oDbNotifyMetaData = null;
		HashMap<String, String> oActualNotifyMetaData = null;
		JSONObject json = new JSONObject();
		String message = "";
		String status="failed";

		LOG.debug(String.format("SendNotification 01 %1$s, %2$s, %3$s", sTopic, oTokens, sUserId));
		
		try {
			oDbNotifyMetaData = getDbNotifyMetaData(sTopic);
			
			if(oDbNotifyMetaData != null) {
				if ((sUserId != null) && (!sUserId.isEmpty())) {
					oTokens.put("User", sUserId);
				}
				oActualNotifyMetaData = getActulaNotifyMetaData(oDbNotifyMetaData, oTokens);
	
				if ( oActualNotifyMetaData.get("IsEmail").equalsIgnoreCase("true") ) {
					sendNotificationByEmail(oActualNotifyMetaData);
				}
	
				if ( oActualNotifyMetaData.get("IsSms").equalsIgnoreCase("true") ) {
					sendNotificationBySms(oActualNotifyMetaData);
				}
				
				// Send SNS notification
				sendEmailNotificationUtil.sendSNSNotification(oActualNotifyMetaData.get("MsgBody"));
	
				fireNotificationToProjectMembers(oActualNotifyMetaData, oTokens);
				
				sendSubscribedEmail(oActualNotifyMetaData, oTokens);
				
				sendNotificationToExternalAPI(oTokens, sTopic);
				message = "Successfully send notification";
				status="success";
				LOG.info(String.format("SendNotification 02 Mail sending method successfully done" ));
			}
			else {
				message="Invalid TopicTitle";
				LOG.debug(String.format("SendNotification 03 Data is not present in notification tables for topic %1$s.", sTopic ));
			}
		} catch (Exception oException) {
			message="Unexpected Exception occurred while sending Notification";
			LOG.error("Exception occurred while sending Notification:" + oException.getMessage());
			oException.printStackTrace();
		}
		json.put("message", message);
		json.put("status",status);

		return json;
	}

	private JSONObject getEventInfo(HashMap<String, String> oActualNotifyMetaData, HashMap<String, String> oTokens) {
		LOG.info("getEventInfo stub");
		JSONObject retJsonData = null;
		DataBuckObjectType oFocusObjectType = DataBuckObjectType.valueOf(Integer.valueOf(oActualNotifyMetaData.get("FocusType")));

		switch (oFocusObjectType) {
			case Connection:
				retJsonData = getConnectionEventInfo(oTokens.get("FocusObjectId"));
				break;
			case DataTemplate:
				retJsonData = null;
				break;
			case ValidationApplication:
				retJsonData = null;
				break;
			}
		
		
		return retJsonData;
	}

	private JSONObject getConnectionEventInfo(String FocusObjectId) {
		String sDataQry = String.format("select a.idDataSchema,a.schemaName,a.schemaType,a.ipAddress,a.databaseSchema,a.port,a.createdAt,a.updatedAt,a.project_id,a.domain_id, c.projectName,b.domainName from listDataSchema a, domain b, project c where a.domain_id = b.domainId and   a.project_id = c.idProject and   a.idDataSchema = %1$s" , FocusObjectId);
		JSONObject tempJson = new JSONObject();
		JSONObject retJsonData = new JSONObject();
		try {
			SqlRowSet sSqlRowSet= jdbcTemplate.queryForRowSet(sDataQry);
			if(sSqlRowSet != null) {
				while(sSqlRowSet.next()) {
				tempJson.put("idDataSchema", sSqlRowSet.getLong("idDataSchema"));
				tempJson.put("schemaName", sSqlRowSet.getString("schemaName"));
				tempJson.put("schemaType", sSqlRowSet.getString("schemaType"));
				tempJson.put("ipAddress", sSqlRowSet.getString("ipAddress"));
				tempJson.put("databaseSchema", sSqlRowSet.getString("databaseSchema"));
				tempJson.put("port", sSqlRowSet.getLong("port"));
				tempJson.put("createdAt", sSqlRowSet.getDate("createdAt"));
				tempJson.put("updatedAt", sSqlRowSet.getDate("updatedAt"));
				tempJson.put("project_id", sSqlRowSet.getLong("project_id"));
				tempJson.put("domain_id", sSqlRowSet.getLong("domain_id"));				
				tempJson.put("projectName", sSqlRowSet.getString("projectName"));
				tempJson.put("domainName", sSqlRowSet.getString("domainName"));
				}
				
				retJsonData.put("DatabuckObjectType", "CONNECTION");
				retJsonData.put("DataBuckEventId", "CREATE_CONNECTION_STATUS");
				retJsonData.put("DataBuckEventData", tempJson);
				
				LOG.debug(String.format("Json Data to return: %1$s", retJsonData));
			}
			else {
				LOG.debug(String.format("SqlRowSet is null for query: %1$s", sDataQry));
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}
		return retJsonData;
	}

	private HashMap<String, String> getDbNotifyMetaData(String sTopic) {
		List<HashMap<String, String>> oMetadataList = null;
		HashMap<String, String> oDbNotifyMetaData = null;
		String[] aColumnList =  new String[] {
				"RowId", "TopicTitle", "TopicVersion", "FocusType", "IsSelected", "IsEmail", "BaseMediaIds", "IsSms", "MsgSubject", "MsgBody", "IsPublishExternally", "PublishUrlFirst", "PublishUrlSecond"
		};
		
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String query = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			query = query + "select a.row_id as RowId, a.topic_title as TopicTitle, a.focus_type as FocusType, b.topic_version as TopicVersion, ";
			query = query + "b.is_selected as IsSelected, b.is_email as IsEmail, ";
			query = query + "b.base_media_ids as BaseMediaIds, b.is_sms as IsSms, b.message_subject as MsgSubject, b.message_body as MsgBody, ";
			query = query + "a.is_publish_externally as IsPublishExternally, COALESCE(a.publish_url_1,'') as PublishUrlFirst, COALESCE(a.publish_url_2,'') as PublishUrlSecond  \n";
			query = query + "from notification_topics a, notification_topic_versions b ";
			query = query + "where a.topic_title = '" + sTopic + "' and b.is_selected = 1 and a.row_id = b.topic_row_id";
		} else {
			query = query + "select a.row_id as RowId, a.topic_title as TopicTitle, a.focus_type as FocusType, b.topic_version as TopicVersion, ";
			query = query + "b.is_selected as IsSelected, b.is_email as IsEmail, ";
			query = query + "b.base_media_ids as BaseMediaIds, b.is_sms as IsSms, b.message_subject as MsgSubject, b.message_body as MsgBody, ";
			query = query + "a.is_publish_externally as IsPublishExternally, ifnull(a.publish_url_1,'') as PublishUrlFirst, ifnull(a.publish_url_2,'') as PublishUrlSecond  \n";
			query = query + "from notification_topics a, notification_topic_versions b ";
			query = query + "where a.topic_title = '" + sTopic + "' and b.is_selected = 1 and a.row_id = b.topic_row_id";
			
		}
		oMetadataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, query, aColumnList, "SampleTable", null);

		oDbNotifyMetaData = (oMetadataList.isEmpty())? null : oMetadataList.get(0);

		return oDbNotifyMetaData;
	}

	private HashMap<String, String> getActulaNotifyMetaData(HashMap<String, String> oDbNotifyMetaData, HashMap<String, String> oTokens) {
		HashMap<String, String> oRetValue = (HashMap<String, String>) oDbNotifyMetaData.clone();

		List<String> aPredefinedTokens = Arrays.asList(new String[] { "User" });

		LOG.debug(String.format("getActulaNotifyMetaData 01 input meta data is %1$s", oRetValue));

		String sMsgSubject = oRetValue.get("MsgSubject");
		String sMsgBody = oRetValue.get("MsgBody");

		for(String sKey : oTokens.keySet()) {
			if(oTokens.get(sKey) == null) {
				oTokens.put(sKey, "");
			}
			String sSanitizedKey = (aPredefinedTokens.indexOf(sKey) > -1) ? String.format("{{%1$s}}", sKey) : String.format("{%1$s}", sKey);

			if(sMsgSubject.contains(sSanitizedKey) || sMsgBody.contains(sSanitizedKey)) {
				sMsgSubject = sMsgSubject.replace(sSanitizedKey, oTokens.get(sKey));
				sMsgBody = sMsgBody.replace(sSanitizedKey, oTokens.get(sKey));
			}
		}
		oRetValue.put("MsgSubject", sMsgSubject);
		oRetValue.put("MsgBody", sMsgBody);

		LOG.debug(String.format("getActulaNotifyMetaData 02 Token replaced meta data is %1$s", oRetValue));

		return oRetValue;
	}

	private void sendNotificationByEmail(HashMap<String, String> oActualNotifyMetaData) {
		LOG.info("Notificatin sent by Email");
		sendEmailNotificationUtil.sendEmailBySmtpWithEmail(oActualNotifyMetaData.get("MsgSubject"), oActualNotifyMetaData.get("MsgBody"), oActualNotifyMetaData.get("BaseMediaIds"));
	}

	private void sendNotificationBySms(HashMap<String, String> oActualNotifyMetaData) {
		LOG.info("Notificatin sent by sms");
	}

	private void fireNotificationToProjectMembers(HashMap<String, String> oActualNotifyMetaData, HashMap<String, String> oTokens) {
		DataBuckObjectType oFocusObjectType = DataBuckObjectType.valueOf(Integer.valueOf(oActualNotifyMetaData.get("FocusType")));

		String sProjectQry = "";
		String sEmailIdList = "";

		try {
		switch (oFocusObjectType) {
			case Connection:
				sProjectQry = "select a.project_id as ProjectId from listDataSchema a where a.idDataSchema = " + oTokens.get("FocusObjectId");
				break;
			case DataTemplate:
				sProjectQry = "select a.project_id as ProjectId from listDataSources a where a.idData = " + oTokens.get("FocusObjectId");
				break;
			case ValidationApplication:
				sProjectQry = "select a.project_id as ProjectId from listApplications a where a.idApp = " + oTokens.get("FocusObjectId");
				break;
			}
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String sSql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sSql = "select subscriptions.topic_row_id, string_agg(subscriptions.project_row_id::text,',') as ProjectIds, string_agg(projects.projectName,',') as ProjectNames, string_agg(projects.notification_email,',') as ProjectEmailIds "
				 + "from notification_project_subscriptions subscriptions, project projects  "
				 + "where subscriptions.project_row_id = projects.idProject  and subscriptions.topic_row_id = %1$s "
				 + "group by subscriptions.topic_row_id;";
		} else {
			sSql = "select subscriptions.topic_row_id, group_concat(subscriptions.project_row_id) as ProjectIds, group_concat(projects.projectName) as ProjectNames, group_concat(projects.notification_email) as ProjectEmailIds "
					 + "from notification_project_subscriptions subscriptions, project projects  "
					 + "where subscriptions.project_row_id = projects.idProject  and subscriptions.topic_row_id = %1$s "
					 + "group by subscriptions.topic_row_id;";
		}
		String sProjectIdfromSubsProjects = "";
		
			if(sProjectQry.trim().length() != 0) {
				SqlRowSet queryForRowSet2 = jdbcTemplate.queryForRowSet(sProjectQry);
				if(queryForRowSet2 != null) {
					while(queryForRowSet2.next()) {
						sProjectIdfromSubsProjects = queryForRowSet2.getString("ProjectId");
					}
				}
			}
			sProjectIdfromSubsProjects = (sProjectIdfromSubsProjects.length() != 0)? (sProjectIdfromSubsProjects + ", ") : "";
			sSql = String.format(sSql, oActualNotifyMetaData.get("RowId"));
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sSql);
			if(queryForRowSet != null) {
				while(queryForRowSet.next()) {
					sProjectIdfromSubsProjects = sProjectIdfromSubsProjects + queryForRowSet.getString("ProjectIds");
				}
			}
			sProjectIdfromSubsProjects = (!sProjectIdfromSubsProjects.isEmpty())? sProjectIdfromSubsProjects.replaceAll(", $", "") : "" ;
			
			sEmailIdList = (!sProjectIdfromSubsProjects.isEmpty()) ? getEmailsFromProjectId(sProjectIdfromSubsProjects, Integer.valueOf(oActualNotifyMetaData.get("FocusType"))) : "";
			if(sEmailIdList.length() != 0) {
			sendEmailNotificationUtil.sendEmailBySmtpWithEmail(oActualNotifyMetaData.get("MsgSubject"), oActualNotifyMetaData.get("MsgBody"), sEmailIdList);
			}
			else {
				LOG.error(String.format("No emails found for projects."));
			}
			
		} catch (Exception oException) {
			LOG.error("Exception occurred while firing Notification to project members:" + oException.getMessage());
			oException.printStackTrace();
		}


	}
	
	
	 
	private String getEmailsFromProjectId(String sProjectIdfromSubsProjects, int focusType) {
		String sEmailIdList = "";
		String sSqlQry = "";
		boolean lIsActiveDirectoryAuthentication = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "isActiveDirectoryAuthentication", "N").equalsIgnoreCase("Y");
		/*if(lIsActiveDirectoryAuthentication) {
			sEmailIdList = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "mailRecepients", "");
		}
		else {*/
			sSqlQry = sSqlQry + "select distinct notification_email as Email from notification_project_subscriptions ";
			sSqlQry = sSqlQry + "where project_row_id in ("+ sProjectIdfromSubsProjects + ") and focus_type = " + focusType;
			LOG.debug("\ngetEmailsFromProjectId 01 sSqlQry: " + sSqlQry );
			
			try {
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sSqlQry);
				while(queryForRowSet.next()) {
					if(queryForRowSet.getString("Email")!= null && !(queryForRowSet.getString("Email").isEmpty()) ) {
						sEmailIdList = sEmailIdList + queryForRowSet.getString("Email") + ", ";
					}
				}
				if(!sEmailIdList.isEmpty()) {
					sEmailIdList = sEmailIdList.replaceAll(", $", "");
				}
			} catch (Exception oException) {
				LOG.error("Exception occurred while getting emails of subscribed projects:" + oException.getMessage());
				oException.printStackTrace();
			}
		//}
		LOG.debug("\nsEmailIdList:\t" + sEmailIdList);
		return sEmailIdList;
	}
	
	private void sendSubscribedEmail(HashMap<String, String> oActualNotifyMetaData, HashMap<String, String> oTokens) {
		LOG.info("sendSubscribedEmail 01 Start");
		DataBuckObjectType oFocusObjectType = DataBuckObjectType
				.valueOf(Integer.valueOf(oActualNotifyMetaData.get("FocusType")));

		String sEmailQry = "";
		String sEmailIdList = "";

		// Query compatibility changes for both POSTGRES and MYSQL
		String fi = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		try {
		switch (oFocusObjectType) {
		case Connection:
			// For future extension
			break;
		case DataTemplate:
			// Query compatibility changes for both POSTGRES and MYSQL
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sEmailQry = "select COALESCE(a.subcribed_email_id,'') as SubcribedEmailId from listDataSources a where a.idData = " + oTokens.get("FocusObjectId");
			else
				sEmailQry = "select ifnull(a.subcribed_email_id,'') as SubcribedEmailId from listDataSources a where a.idData = " + oTokens.get("FocusObjectId");
			break;
		case ValidationApplication:
			// Query compatibility changes for both POSTGRES and MYSQL
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sEmailQry = "select COALESCE(a.subcribed_email_id,'') as SubcribedEmailId from listApplications a where a.idApp = " + oTokens.get("FocusObjectId");
			else
				sEmailQry = "select ifnull(a.subcribed_email_id,'') as SubcribedEmailId from listApplications a where a.idApp = " + oTokens.get("FocusObjectId");
			break;
		}
		LOG.debug("sendSubscribedEmail 02 sEmailQry: " + sEmailQry);
		if (!sEmailQry.isEmpty()) {
			try {
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sEmailQry);
				while (queryForRowSet.next()) {
					sEmailIdList = queryForRowSet.getString("SubcribedEmailId");
				}

				if (sEmailIdList.length()!= 0) {
					LOG.debug("sendSubscribedEmail 03 sEmailIdList: " + sEmailIdList);
					sendEmailNotificationUtil.sendEmailBySmtpWithEmail(oActualNotifyMetaData.get("MsgSubject"),	oActualNotifyMetaData.get("MsgBody"), sEmailIdList);
				}
				else {
					LOG.error("sendSubscribedEmail 04 sEmailIdList is empty");
				}
			} catch (Exception oException) {
				LOG.error("Exception occurred while getting SubcribedEmailId:" + oException.getMessage());
				oException.printStackTrace();
			}

		}
		}  catch (Exception oException) {
			LOG.error("Exception occurred while getting SubcribedEmailId:" + oException.getMessage());
			oException.printStackTrace();
		}
		LOG.info("sendSubscribedEmail 05 End");
	}
	
	private void sendNotificationToExternalAPI(HashMap<String, String> oTokens, String sTopic) {

		// Fetch the alert message details of API by topic Id
		LOG.info("\n====> Publishing to external API's - START ");

		String status = oTokens.get("Status");
		LOG.debug("\n====> Job Status: " + status);

		long taskId = Long.parseLong(oTokens.get("FocusObjectId"));

		String mainTopic = sTopic;
		String taskType = "";
		String uniqueId = oTokens.get("uniqueId");
		uniqueId = uniqueId != null ? uniqueId.trim() : "";

		// Template
		if (sTopic != null && sTopic.equalsIgnoreCase("CREATE_TEMPLATE_STATUS")) {
			mainTopic = "CREATE_TEMPLATE_STATUS";
			if (status != null && status.equalsIgnoreCase("PASSED"))
				sTopic = "CREATE_TEMPLATE_SUCCESS";
			else if (status != null && status.equalsIgnoreCase("FAILED"))
				sTopic = "CREATE_TEMPLATE_FAILED";

			taskType = "template";
		}

		// Connection
		else if (sTopic != null && sTopic.equalsIgnoreCase("CREATE_CONNECTION_STATUS")) {
			if (status != null && status.equalsIgnoreCase("PASSED"))
				sTopic = "CREATE_CONNECTION_SUCCESS";
			else if (status != null && status.equalsIgnoreCase("FAILED"))
				sTopic = "CREATE_CONNECTION_FAILED";
			taskType = "connection";
		}

		// AppGroup
		else if (sTopic != null && (sTopic.equalsIgnoreCase("RUN_APPGROUP_COMPLETE")
				|| sTopic.equalsIgnoreCase("RUN_APPGROUP_FAILURE"))) {
			taskType = "appgroup";
		}

		// RunSchema
		else if (sTopic != null
				&& (sTopic.equalsIgnoreCase("RUN_SCHEMA_COMPLETE") || sTopic.equalsIgnoreCase("RUN_SCHEMA_FAILURE"))) {
			taskType = "runschema";
		}

		LOG.debug("\n====> Topic Name: " + sTopic);
		LOG.debug("\n====> Main Topic Name: " + mainTopic);

		NotificationTopics notificationTopics = iTaskDao.getNotificationInfoByTopic(mainTopic);
		if (notificationTopics != null) {

			String PublishUrlFirst = notificationTopics.getPublish_url_1();
			String PublishUrlSecond = notificationTopics.getPublish_url_2();

			LOG.debug("\n====> First Publish Url: " + PublishUrlFirst);
			LOG.debug("\n====> Second Publish Url: " + PublishUrlSecond);

			if (PublishUrlFirst != null && !PublishUrlFirst.trim().isEmpty()) {
				LOG.info("\n\n***** Publishing to STEEL *****");

				Map<String, String> notificationInfoMap = prepareNotificationJsonForExternalSteelAPI(sTopic, oTokens);

				String notificationJsonStr = notificationInfoMap.get("alert_json");
				LOG.debug("\n====> Alert message to post to STEEL: " + notificationJsonStr);

				if (notificationJsonStr != null && !notificationJsonStr.isEmpty()) {

					// Preparing pojo with alert information to save in queue
					ExternalAPIAlertPOJO externalAPIAlertPOJO = new ExternalAPIAlertPOJO();
					externalAPIAlertPOJO.setTaskType(taskType);
					externalAPIAlertPOJO.setTaskId(taskId);
					externalAPIAlertPOJO.setUniqueId(uniqueId);
					externalAPIAlertPOJO.setExecDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					externalAPIAlertPOJO.setRun(Long.parseLong(notificationInfoMap.get("currentRun")));
					externalAPIAlertPOJO.setTestRun(notificationInfoMap.get("unitTestingFlag"));
					externalAPIAlertPOJO.setAlertMsg(notificationInfoMap.get("alert_msg"));
					externalAPIAlertPOJO.setAlertMsgCode(notificationInfoMap.get("alert_msg_code"));
					externalAPIAlertPOJO.setAlertLabel(notificationInfoMap.get("alert_label"));
					externalAPIAlertPOJO.setAlertJson(notificationInfoMap.get("alert_json").replaceAll("\"", "\\\""));
					externalAPIAlertPOJO.setAlertTimeStamp(notificationInfoMap.get("alert_timestamp"));

					String logonManagerEnabled = appDbConnectionProperties.getProperty("logon_manager.enabled");

					boolean flag = true;
					String externalAPIType = "steel";

					String service_id = notificationTopics.getService_id();
					String password = notificationTopics.getPassword();

					// REST type post call to send JSON data to publish_url1
					LOG.info("\n====> Publishing message to first url ..");
					LOG.debug("\n====> PublishUrlFirst: " + PublishUrlFirst);

					// Saving the alert details in queue
					LOG.info("\n====> Saving the alert details in queue ..");
					externalAPIAlertPOJO.setExternalAPIType(externalAPIType);
					iTaskDao.addExternalAPIMsgToQueue(externalAPIAlertPOJO);

					// Check if logon manager is enabled and
					if (logonManagerEnabled != null && logonManagerEnabled.equalsIgnoreCase("Y")) {

						// Fetch username and password from LogonManager
						Map<String, String> api_user_details = logonManager.getCredentialsFromLogonCmd(service_id);

						// Validate the logon manager response for key
						boolean responseStatus = logonManager.validateLogonManagerResponseForAPI(api_user_details);

						if (responseStatus) {
							service_id = api_user_details.get("username");
							password = api_user_details.get("password");
						} else {
							flag = false;
							LOG.error(
									"\n====> Failed to get user details of API, hence cannot publish to url !!");
						}
					}

					boolean isMsgDelivered = false;
					if (flag) {
						// Post the message
						LOG.info("\n====> Post the message ..");
						isMsgDelivered = publishNotificationToExternalAPI(PublishUrlFirst, service_id, password,
								notificationJsonStr);
					}

					// Update the message delivered status in the queue
					LOG.info("\n====> Update the message delivered status in the queue ..");
					String alert_deliver_status = (isMsgDelivered) ? "passed" : "failed";
					iTaskDao.updateAlertMsgDeliverStatusForMessage(externalAPIType, taskId, taskType, uniqueId,
							alert_deliver_status);

				}
			}

			if (PublishUrlSecond != null && !PublishUrlSecond.trim().isEmpty()) {
				LOG.info("\n\n***** Publishing to NETCOOL *****");

				Map<String, String> notificationInfoMap = prepareNotificationJsonForExternalNetcoolAPI(sTopic, oTokens);

				String notificationJsonStr = notificationInfoMap.get("alert_json");
				LOG.debug("\n====> Alert message to post to Netcool: " + notificationJsonStr);

				if (notificationJsonStr != null && !notificationJsonStr.isEmpty()) {

					// Preparing pojo with alert information to save in queue
					ExternalAPIAlertPOJO externalAPIAlertPOJO = new ExternalAPIAlertPOJO();
					externalAPIAlertPOJO.setTaskType(taskType);
					externalAPIAlertPOJO.setTaskId(taskId);
					externalAPIAlertPOJO.setUniqueId(uniqueId);
					externalAPIAlertPOJO.setExecDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					externalAPIAlertPOJO.setRun(Long.parseLong(notificationInfoMap.get("currentRun")));
					externalAPIAlertPOJO.setTestRun(notificationInfoMap.get("unitTestingFlag"));
					externalAPIAlertPOJO.setAlertMsg(notificationInfoMap.get("alert_msg"));
					externalAPIAlertPOJO.setAlertMsgCode(notificationInfoMap.get("alert_msg_code"));
					externalAPIAlertPOJO.setAlertLabel(notificationInfoMap.get("alert_label"));
					externalAPIAlertPOJO.setAlertJson(notificationInfoMap.get("alert_json").replaceAll("\"", "\\\""));
					externalAPIAlertPOJO.setAlertTimeStamp(notificationInfoMap.get("alert_timestamp"));

					String logonManagerEnabled = appDbConnectionProperties.getProperty("logon_manager.enabled");

					boolean flag = true;
					String externalAPIType = "netcool";
					String url2_service_id = notificationTopics.getUrl2_service_id();
					String url2_password = notificationTopics.getUrl2_password();

					// REST type post call to send JSON data to publish_url2
					LOG.info("\n====> Publishing message to Second url ..");
					LOG.debug("\n====> PublishUrlSecond: " + PublishUrlSecond);

					// Saving the alert details in queue
					LOG.info("\n====> Saving the alert details in queue ..");
					externalAPIAlertPOJO.setExternalAPIType(externalAPIType);
					iTaskDao.addExternalAPIMsgToQueue(externalAPIAlertPOJO);

					// Check if logon manager is enabled and
					if (logonManagerEnabled != null && logonManagerEnabled.equalsIgnoreCase("Y")) {

						// Fetch username and password from LogonManager
						Map<String, String> api_user_details = logonManager.getCredentialsFromLogonCmd(url2_service_id);

						// Validate the logon manager response for key
						boolean responseStatus = logonManager.validateLogonManagerResponseForAPI(api_user_details);

						if (responseStatus) {
							url2_service_id = api_user_details.get("username");
							url2_password = api_user_details.get("password");
						} else {
							flag = false;
							LOG.error(
									"\n====> Failed to get user details of API, hence cannot publish to url !!");
						}
					}

					boolean isMsgDelivered = false;
					if (flag) {
						// Post the message
						LOG.error("\n====> Post the message ..");
						isMsgDelivered = publishNotificationToExternalAPI(PublishUrlSecond, url2_service_id,
								url2_password, notificationJsonStr);
					}

					// Update the message delivered status in the queue
					LOG.info("\n====> Update the message delivered status in the queue ..");
					String alert_deliver_status = (isMsgDelivered) ? "passed" : "failed";
					iTaskDao.updateAlertMsgDeliverStatusForMessage(externalAPIType, taskId, taskType, uniqueId,
							alert_deliver_status);
				}
			}
		}
		LOG.info("\n====> Publishing to external API's - END   ");
	}
	private boolean publishNotificationToExternalAPI(String publishUrl, String username, String password,
			String notificationResponse) {
		boolean status = false;
		try {
			// Invoking the API
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String notEncoded = username + ":" + password;
			String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
			headers.add("Authorization", encodedAuth);

			// set my entity
			HttpEntity<Object> entity = new HttpEntity<Object>(notificationResponse, headers);

			ResponseEntity<String> out = restTemplate.exchange(publishUrl, HttpMethod.POST, entity, String.class);

			if (out.getStatusCode() == HttpStatus.OK) {
				LOG.debug("\n====> Notification api:[" + publishUrl + "] triggering is successful !!");
				status = true;
			} else {
				LOG.debug("\n====> Notification api: [" + publishUrl + "] triggering is failed !!");
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return status;
	}
	
	private Map<String, String> prepareNotificationJsonForExternalSteelAPI(String sTopic,
			HashMap<String, String> oTokens) {
		String notificationResponse = null;
		Map<String, String> notificationInfoMap = new HashMap<>();

		try {
			String focusObjectId = oTokens.get("FocusObjectId");
			String unitTestingFlag = "N";
			String currentRun = "1";

			notificationInfoMap.put("unitTestingFlag", unitTestingFlag);
			notificationInfoMap.put("currentRun", currentRun);

			// Fetch the alert message details by topic name
			LOG.debug("\n====> Fetching the alert message and code details for topic: " + sTopic);
			JSONObject notification_alert_api = iTaskDao.getAlertNotificationDetailsByTopic(sTopic);

			if (notification_alert_api != null && notification_alert_api.length() > 0) {
				String alert_msg = (String) notification_alert_api.get("alert_msg");
				String alert_msg_code = "" + notification_alert_api.get("alert_msg_code");
				String alert_label = "" + notification_alert_api.get("alert_label");
				Date currentDate= DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
				String alter_timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);

				notificationInfoMap.put("alert_msg", alert_msg);
				notificationInfoMap.put("alert_msg_code", alert_msg_code);
				notificationInfoMap.put("alert_label", alert_label);
				notificationInfoMap.put("alert_timestamp", alter_timestamp);

				JSONObject alertObj = new JSONObject();
				alertObj.put("Node", "localhost");
				alertObj.put("Summary", "Lorem ipsum dolor sit amet");
				alertObj.put("AddInfo", "consectetur adipiscing elit");
				alertObj.put("Agent", "Some Agent Type");
				alertObj.put("AlertGroup", alert_msg);
				alertObj.put("AlertKey", "An Instance within message classification");
				alertObj.put("Severity", alert_msg_code);
				alertObj.put("AssetType", 12);

				JSONObject alertinfoObj = new JSONObject();
				alertinfoObj.put("idapp", focusObjectId);
				alertinfoObj.put("ApplicationName", alert_label);
				alertinfoObj.put("EventID", alert_msg);

				JSONObject msgInfoObj = new JSONObject();
				msgInfoObj.put("descr", alert_msg);
				msgInfoObj.put("format", "DEFAULT");
				msgInfoObj.put("subformat", "DEFAULT");
				msgInfoObj.put("version", "1");
				msgInfoObj.put("date time stamp", alter_timestamp);
				msgInfoObj.put("unitTestingFlag", unitTestingFlag);
				msgInfoObj.put("RunNo", currentRun);

				JSONObject notificationObj = new JSONObject();
				notificationObj.put("alert", alertObj);
				notificationObj.put("alertInfo", alertinfoObj);
				notificationObj.put("msgInfo", msgInfoObj);
				notificationResponse = notificationObj.toString();
			} else {
				LOG.error("\n====> Alert msg and code details are not found for the topic [" + sTopic + "] !!");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		notificationInfoMap.put("alert_json", notificationResponse);

		return notificationInfoMap;
	}
	
	private Map<String, String> prepareNotificationJsonForExternalNetcoolAPI(String sTopic,
			HashMap<String, String> oTokens) {
		String notificationResponse = null;
		Map<String, String> notificationInfoMap = new HashMap<>();

		try {
			String focusObjectId = oTokens.get("FocusObjectId");
			String unitTestingFlag = "N";
			String currentRun = "1";

			notificationInfoMap.put("unitTestingFlag", unitTestingFlag);
			notificationInfoMap.put("currentRun", currentRun);

			// Fetch the alert message details by topic name
			LOG.debug("\n====> Fetching the alert message and code details for topic: " + sTopic);
			JSONObject notification_alert_api = iTaskDao.getAlertNotificationDetailsByTopic(sTopic);

			if (notification_alert_api != null && notification_alert_api.length() > 0) {
				String alert_msg = (String) notification_alert_api.get("alert_msg");
				String alert_msg_code = "" + notification_alert_api.get("alert_msg_code");
				String alert_label = "" + notification_alert_api.get("alert_label");

				Date currentDate= DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
				String alter_timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);

				notificationInfoMap.put("alert_msg", alert_msg);
				notificationInfoMap.put("alert_msg_code", alert_msg_code);
				notificationInfoMap.put("alert_label", alert_label);
				notificationInfoMap.put("alert_timestamp", alter_timestamp);

				// Get instance name
				String hostName = databuckUtility.getCurrentHostName();

				JSONObject alertObj = new JSONObject();
				alertObj.put("Node", "localhost");
				alertObj.put("ReportingInstance", hostName);
				alertObj.put("ReportingServer", hostName);
				alertObj.put("Summary", "Lorem ipsum dolor sit amet");
				alertObj.put("AddInfo", "consectetur adipiscing elit");
				alertObj.put("Agent", "Some Agent Type");
				alertObj.put("AlertGroup", alert_msg);
				alertObj.put("AlertKey", "An Instance within message classification");
				alertObj.put("AssetType", 12);
				alertObj.put("Severity", alert_msg_code);
				alertObj.put("ExpireTime", 3600);

				JSONObject alertinfoObj = new JSONObject();
				alertinfoObj.put("AppID", focusObjectId);
				alertinfoObj.put("ApplicationName", alert_label);
				alertinfoObj.put("EventID", alert_msg);

				JSONObject msgInfoObj = new JSONObject();
				msgInfoObj.put("descr", alert_msg);
				msgInfoObj.put("format", "DEFAULT");
				msgInfoObj.put("subformat", "DEFAULT");
				msgInfoObj.put("version", "1");

				JSONObject notificationObj = new JSONObject();
				notificationObj.put("alert", alertObj);
				notificationObj.put("alertinfo", alertinfoObj);
				notificationObj.put("msginfo", msgInfoObj);
				notificationResponse = notificationObj.toString();
			} else {
				System.out
						.println("\n====> Alert msg and code details are not found for the topic [" + sTopic + "] !!");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		notificationInfoMap.put("alert_json", notificationResponse);

		return notificationInfoMap;
	}
	
	public JSONObject loadNotificationDataList() throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		List<HashMap<String, String>> aProjectDataList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] {
				"TopicRowId", "TopicTitle", "TopicVersion", "VersionRowId", "SubscribedProjectRowIds", "SubscribedProjectNames",
				"IsSelected", "FocusType", "VersionRowId:edit", "VersionRowId:copy", "VersionRowId:delete", "BaseMediaIds", "MessageSubject", "MessageBody", "IsPublishExternally", "PublishUrlFirst", "PublishUrlSecond", "VersionRowId:checkbox", "VerifyIcon:lambda",
				"Authorization", "Username", "Password", "url2_Authorization", "url2_Username", "url2_Password"
		};

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();
		JSONObject oProjectData = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		CustomizeDataTableColumn oVerifyIcon = (oDataRow) -> {
			String sVerifyAnkerTmpl = "<a class='datatable-anker' id='verify-%1$s'><img style='width: 25px; height: 30px; background-color: red;' src='./assets/img/verifyImage.jpg'></a>";
			return String.format(sVerifyAnkerTmpl, oDataRow.get("VersionRowId"));
		 };

		HashMap<String, CustomizeDataTableColumn> oCustomizeDataTable = new HashMap<String, CustomizeDataTableColumn>() {{
		    	put("VerifyIcon", oVerifyIcon);
		  }};

		try {
			handleFreshDataSetup();

			// Query compatibility changes for both POSTGRES and MYSQL
			sDataSql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = sDataSql + "SELECT "; 
				sDataSql = sDataSql + "CASE WHEN sub_qry.topic_row_id IS NULL THEN '' ELSE sub_qry.ProjectIds END AS SubscribedProjectRowIds, ";
				sDataSql = sDataSql + "CASE WHEN sub_qry.topic_row_id IS NULL THEN '' ELSE sub_qry.ProjectNames END AS SubscribedProjectNames, ";
				sDataSql = sDataSql + "CASE WHEN sub_qry.ProjectEmailId IS NULL THEN '' ELSE sub_qry.ProjectEmailId END AS SubscribedProjectEmailId, ";
				sDataSql = sDataSql + "core_qry.* ";
				sDataSql = sDataSql + "FROM ";
				sDataSql = sDataSql + "(SELECT "; 
				sDataSql = sDataSql + "notification_topics.row_id AS TopicRowId, notification_topics.topic_title AS TopicTitle, notification_topic_versions.row_id AS VersionRowId, ";
				sDataSql = sDataSql + "notification_topic_versions.topic_version AS TopicVersion, ";
				sDataSql = sDataSql + "CASE WHEN notification_topics.focus_type = 0 THEN 'Connection' WHEN notification_topics.focus_type = 1 THEN 'Template' ";
				sDataSql = sDataSql + "WHEN notification_topics.focus_type = 2 THEN 'Validation' WHEN notification_topics.focus_type = 3 THEN 'AppGroup' ";
				sDataSql = sDataSql + "WHEN notification_topics.focus_type = 4 THEN 'Run Schema' ELSE 'Undefined Type' END AS FocusType, ";
				sDataSql = sDataSql + "CASE WHEN notification_topic_versions.is_selected = 1 THEN 'true' ELSE 'false' END AS IsSelected, ";
				sDataSql = sDataSql + "notification_topic_versions.base_media_ids AS BaseMediaIds, notification_topic_versions.message_subject AS MessageSubject, ";
				sDataSql = sDataSql + "notification_topic_versions.message_body AS MessageBody, notification_topics.is_publish_externally AS IsPublishExternally, ";
				sDataSql = sDataSql + "COALESCE(notification_topics.publish_url_1, '') AS PublishUrlFirst, COALESCE(notification_topics.publish_url_2, '') AS PublishUrlSecond, ";
				sDataSql = sDataSql + "notification_topics.focus_type, notification_topics.authorization as Authorization, COALESCE(notification_topics.service_id, '') as Username, COALESCE(notification_topics.password, '') as Password, ";
				sDataSql = sDataSql + "notification_topics.url2_authorization as url2_Authorization, COALESCE(notification_topics.url2_service_id, '') as url2_Username, COALESCE(notification_topics.url2_password, '') as url2_Password ";
				sDataSql = sDataSql + "FROM notification_topics, notification_topic_versions ";
				sDataSql = sDataSql + "WHERE notification_topic_versions.topic_row_id = notification_topics.row_id AND notification_topics.managed_by = 0 ";
				sDataSql = sDataSql + "AND notification_topics.active > 0) AS core_qry ";
				sDataSql = sDataSql + "LEFT OUTER JOIN ";
				sDataSql = sDataSql + "(SELECT subscriptions.topic_row_id, subscriptions.focus_type, string_agg(subscriptions.project_row_id::text,',') AS ProjectIds, ";
				sDataSql = sDataSql + "string_agg(projects.projectName,',') AS ProjectNames, string_agg(subscriptions.notification_email,',') AS ProjectEmailId ";
				sDataSql = sDataSql + "FROM notification_project_subscriptions subscriptions, project projects ";
				sDataSql = sDataSql + "WHERE subscriptions.project_row_id = projects.idProject GROUP BY subscriptions.topic_row_id , subscriptions.focus_type) ";
				sDataSql = sDataSql + "AS sub_qry ON core_qry.TopicRowId = sub_qry.topic_row_id AND core_qry.focus_type = sub_qry.focus_type; ";
			} else {
				sDataSql = sDataSql + "SELECT "; 
				sDataSql = sDataSql + "CASE WHEN sub_qry.topic_row_id IS NULL THEN '' ELSE sub_qry.ProjectIds END AS SubscribedProjectRowIds, ";
				sDataSql = sDataSql + "CASE WHEN sub_qry.topic_row_id IS NULL THEN '' ELSE sub_qry.ProjectNames END AS SubscribedProjectNames, ";
				sDataSql = sDataSql + "CASE WHEN sub_qry.ProjectEmailId IS NULL THEN '' ELSE sub_qry.ProjectEmailId END AS SubscribedProjectEmailId, ";
				sDataSql = sDataSql + "core_qry.* ";
				sDataSql = sDataSql + "FROM ";
				sDataSql = sDataSql + "(SELECT "; 
				sDataSql = sDataSql + "notification_topics.row_id AS TopicRowId, notification_topics.topic_title AS TopicTitle, notification_topic_versions.row_id AS VersionRowId, ";
				sDataSql = sDataSql + "notification_topic_versions.topic_version AS TopicVersion, ";
				sDataSql = sDataSql + "CASE WHEN notification_topics.focus_type = 0 THEN 'Connection' WHEN notification_topics.focus_type = 1 THEN 'Template' ";
				sDataSql = sDataSql + "WHEN notification_topics.focus_type = 2 THEN 'Validation' WHEN notification_topics.focus_type = 3 THEN 'AppGroup' ";
				sDataSql = sDataSql + "WHEN notification_topics.focus_type = 4 THEN 'Run Schema' ELSE 'Undefined Type' END AS FocusType, ";
				sDataSql = sDataSql + "CASE WHEN notification_topic_versions.is_selected = 1 THEN 'true' ELSE 'false' END AS IsSelected, ";
				sDataSql = sDataSql + "notification_topic_versions.base_media_ids AS BaseMediaIds, notification_topic_versions.message_subject AS MessageSubject, ";
				sDataSql = sDataSql + "notification_topic_versions.message_body AS MessageBody, notification_topics.is_publish_externally AS IsPublishExternally, ";
				sDataSql = sDataSql + "IFNULL(notification_topics.publish_url_1, '') AS PublishUrlFirst, IFNULL(notification_topics.publish_url_2, '') AS PublishUrlSecond, ";
				sDataSql = sDataSql + "notification_topics.focus_type, notification_topics.authorization as Authorization, IFNULL(notification_topics.service_id, '') as Username, IFNULL(notification_topics.password, '') as Password, ";
				sDataSql = sDataSql + "notification_topics.url2_authorization as url2_Authorization, IFNULL(notification_topics.url2_service_id, '') as url2_Username, IFNULL(notification_topics.url2_password, '') as url2_Password ";
				sDataSql = sDataSql + "FROM notification_topics, notification_topic_versions ";
				sDataSql = sDataSql + "WHERE notification_topic_versions.topic_row_id = notification_topics.row_id AND notification_topics.managed_by = 0 ";
				sDataSql = sDataSql + "AND notification_topics.active > 0) AS core_qry ";
				sDataSql = sDataSql + "LEFT OUTER JOIN ";
				sDataSql = sDataSql + "(SELECT subscriptions.topic_row_id, subscriptions.focus_type, GROUP_CONCAT(subscriptions.project_row_id) AS ProjectIds, ";
				sDataSql = sDataSql + "GROUP_CONCAT(projects.projectName) AS ProjectNames, GROUP_CONCAT(subscriptions.notification_email) AS ProjectEmailId ";
				sDataSql = sDataSql + "FROM notification_project_subscriptions subscriptions, project projects ";
				sDataSql = sDataSql + "WHERE subscriptions.project_row_id = projects.idProject GROUP BY subscriptions.topic_row_id , subscriptions.focus_type) ";
				sDataSql = sDataSql + "AS sub_qry ON core_qry.TopicRowId = sub_qry.topic_row_id AND core_qry.focus_type = sub_qry.focus_type; ";	
			}
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", oCustomizeDataTable);

			//Decrypt passwords & Put back into Hashmap
			for (HashMap<String, String> oDataViewListRecord : aDataViewList) {
				if(!oDataViewListRecord.get("Password").isEmpty()) {
				oDataViewListRecord.put("Password", getEncryptedOrDecryptedPassword(oDataViewListRecord.get("Password"), "Decrypt"));	
				}
				
				if(!oDataViewListRecord.get("url2_Password").isEmpty()) {
					oDataViewListRecord.put("url2_Password", getEncryptedOrDecryptedPassword(oDataViewListRecord.get("url2_Password"), "Decrypt"));	
				}
			}
			
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "select p.idProject as projectrowid, p.projectName as projectname, COALESCE(string_agg(distinct nps.notification_email,','), '') as projectemail from project p left outer join notification_project_subscriptions nps on p.idProject = nps.project_row_id GROUP BY p.idProject;";
			} else {
				sDataSql = "select p.idProject as projectrowid, p.projectName as projectname, ifnull(GROUP_CONCAT(distinct nps.notification_email), '') as projectemail from project p left outer join notification_project_subscriptions nps on p.idProject = nps.project_row_id GROUP BY p.idProject;";
			}
			aProjectDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);

			for (HashMap<String, String> oProjectRecord : aProjectDataList) {
				JSONObject oProject = new JSONObject();

				oProject.put("ProjectName", oProjectRecord.get("projectname"));
				oProject.put("ProjectEmail", oProjectRecord.get("projectemail"));

				oProjectData.put(oProjectRecord.get("projectrowid"), oProject);
			}
			//sDataViewList = oMapper.writeValueAsString(oProjectEmailIds);
			oJsonRetValue = oJsonRetValue.put("ProjectData", oProjectData);
			
			sDataSql = "";
			sDataSql = sDataSql + "SELECT a.row_id as RowId, a.topic_title as TopicTitle, a.focus_type as FocusType, a.managed_by as ManagedBy, b.tag_id as TagId ";
			sDataSql = sDataSql + "from notification_topics a,  notification_tags_master b, notification_applicable_tags c ";
			sDataSql = sDataSql + "where c.topic_row_id = a.row_id ";
			sDataSql = sDataSql + "and   c.tag_row_id = b.row_id ";
			sDataSql = sDataSql + "order by a.row_id, b.tag_id";
			
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("ApplicableTagData", aJsonDataList);

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	public void handleFreshDataSetup() {
		SqlRowSet oSqlRowSet = null;
		String sSelectSql = "", sUpdateSql = "";
		int nFreshCount = -1;
		int nIntRowsAffected = -1;

		String sMailRecepients = JwfSpaInfra.getPropertyValue(appDbConnectionProperties, "mailRecepients", "xx");
		boolean lValidMailRecepients = sMailRecepients.equalsIgnoreCase("xx") ? false : true;

		sSelectSql = sSelectSql + "select count(*) as Count \n";
		sSelectSql = sSelectSql + "from notification_topics a, notification_topic_versions b \n";
		sSelectSql = sSelectSql + "where a.row_id = b.topic_row_id \n";
		sSelectSql = sSelectSql + "and   (b.topic_version > 0 or length(trim(b.base_media_ids)) > 0)";

		oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nFreshCount = (oSqlRowSet.next()) ? oSqlRowSet.getInt("Count") : -1;

		if ( (nFreshCount == 0) && (lValidMailRecepients) ){
			sUpdateSql = String.format("update notification_topic_versions set base_media_ids = '%1$s'", sMailRecepients);
			nIntRowsAffected = nIntRowsAffected + jdbcTemplate.update(sUpdateSql);
			DateUtility.DebugLog("handleFreshDataSetup 01",	String.format("mailRecepients = '%1$s' email id updated into 'notification_topic_versions' no of rows affected = %2$s",sMailRecepients, nIntRowsAffected));
		} else {
			DateUtility.DebugLog("handleFreshDataSetup 02",	"Normal page load i.e. customer gone beyond fresh data setup");
		}
	}

	public boolean saveSelectedVersionAsActive(JSONObject oNotificationData) {
		String sVersionRowId = oNotificationData.getString("VersionRowId");
		String sTopicRowId = oNotificationData.getString("TopicRowId");

		SqlRowSet oSqlRowSet = null;
		String sSelectSql = "", sUpdateSql = "";
		int nIntRowsAffected = -1;
		boolean lRetValue = true;

		sSelectSql = sSelectSql + "select count(*) as OtherActiveCount, \n";
		sSelectSql = sSelectSql + String.format("  case when (select count(*) from notification_topic_versions where row_id = %1$s and is_selected < 1) > 0 then 1 else 0 end as CurrentNotActive \n",sVersionRowId);
		sSelectSql = sSelectSql + "from notification_topic_versions \n";
		sSelectSql = sSelectSql + String.format("where topic_row_id = %1$s \n", sTopicRowId);
		sSelectSql = sSelectSql + "and   is_selected = 1 \n";
		sSelectSql = sSelectSql + String.format("and row_id <> %1$s \n", sVersionRowId);

		oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);

		if (oSqlRowSet.next()) {
			if ( (oSqlRowSet.getInt("OtherActiveCount") == 1) && (oSqlRowSet.getInt("CurrentNotActive") == 1) ) {
				sUpdateSql = String.format("update notification_topic_versions set is_selected = 0 where topic_row_id = %1$s and is_selected = 1",sTopicRowId);
				nIntRowsAffected = jdbcTemplate.update(sUpdateSql);

				sUpdateSql = String.format("update notification_topic_versions set is_selected = 1 where row_id = %1$s;",sVersionRowId);
				nIntRowsAffected = nIntRowsAffected + jdbcTemplate.update(sUpdateSql);
			}
		}
		lRetValue = (nIntRowsAffected == 2) ? true : false;

		return lRetValue;
	}

	public int makeCopyOfSelectedVersion(JSONObject oNotificationData) {
		String sVersionRowId = oNotificationData.getString("VersionRowId");
		String sTopicRowId = oNotificationData.getString("TopicRowId");

		int nVersionRowId = -1;
		String sInsertSql = "", sSelectSql = "";
		SqlRowSet oSqlRowSet = null;

		sInsertSql = sInsertSql + "insert into notification_topic_versions \n";
		sInsertSql = sInsertSql + "  (topic_row_id, topic_version, is_selected, is_email, is_sms, base_media_ids, message_subject, message_body) \n";
		sInsertSql = sInsertSql + "  select topic_row_id, \n";
		sInsertSql = sInsertSql + String.format("(select max(topic_version)+1 from notification_topic_versions where topic_row_id = %1$s), \n", sTopicRowId);
		sInsertSql = sInsertSql + "   0, is_email, is_sms, base_media_ids, message_subject, message_body \n";
		sInsertSql = sInsertSql + "from notification_topic_versions \n";
		sInsertSql = sInsertSql + String.format("where row_id = %1$s;", sVersionRowId);

		jdbcTemplate.batchUpdate(sInsertSql);

		sSelectSql = String.format("(select row_id as MaxVersionRowId from notification_topic_versions where topic_row_id = %1$s order by topic_version desc limit 1);", sTopicRowId);
		oSqlRowSet = jdbcTemplate.queryForRowSet(sSelectSql);
		nVersionRowId = oSqlRowSet.next() ? oSqlRowSet.getInt("MaxVersionRowId") : -1;

		return nVersionRowId;
	}

	public void deleteSelectedVersion(JSONObject oNotificationData) {
		String sVersionRowId = oNotificationData.getString("VersionRowId");
		String sDeleteSql = String.format("delete from notification_topic_versions where row_id = %1$s and is_selected < 1 and topic_version <> 0", sVersionRowId);

		int nIntRowsAffected = jdbcTemplate.update(sDeleteSql);

		DateUtility.DebugLog("deleteSelectedVersion", String.format("Notification version with sVersionRowId = '%1$s' deleted.  No of rows affected = %2$s", sVersionRowId, nIntRowsAffected));

		return;
	}

	public JSONObject verfiySelectedVersion(JSONObject oNotificationData) {
		HashMap<String,String> oTokens = new HashMap<>();
		oTokens.put("FocusObjectId", "125");
		oTokens.put("ConnName", "Retail_MSSQL");
		oTokens.put("TmplName", "Retail_Accounts");
		oTokens.put("TableName", "Accounts");
		oTokens.put("DatabuckLink", "host:port/databuck/publishProfileResults");
		oTokens.put("idApp", "678");
		oTokens.put("idData", "127");
		oTokens.put("ConnId", "12");
		oTokens.put("status", "pass");
		oTokens.put("ErrorMsg", "java.lang.NullPointerException");
		oTokens.put("User", "User");
		return SendNotification(oNotificationData.getString("TopicTitle"), oTokens, null);
	}

	public JSONObject saveFullNotificationData(JSONObject oNotificationData) {
		String sVersionRowId = oNotificationData.getString("VersionRowId");
		String sTopicRowId = oNotificationData.getString("TopicRowId");
		Integer topicRowId = Integer.parseInt(sTopicRowId);
		String sUpdateSql = "", sDeleteSql = "", sInsertSql = "";
		String[] aProjectRowIds = (oNotificationData.getString("SubscribedProjectRowIds").trim().length() > 0) ? oNotificationData.getString("SubscribedProjectRowIds").split(",") : new String[]{};
		String sMsg = "";
		JSONObject saveJson = new JSONObject();
		if(isInvalidCharactersInString(oNotificationData.getString("MessageBody")) || isInvalidCharactersInString(oNotificationData.getString("MessageSubject"))) {
			sMsg = "Special character single quoate and back slash are not allowed.";
		}
		else {
			String sMsgBody = oNotificationData.getString("MessageBody").replaceAll("'", "");
			String sMsgSubject = oNotificationData.getString("MessageSubject").replaceAll("'", "");
			int oFocusType = 9 ; 
			String sPassword = getEncryptedOrDecryptedPassword(oNotificationData.getString("Password"), "Encrypt");
			String sUrl2Password = getEncryptedOrDecryptedPassword(oNotificationData.getString("url2_Password"), "Encrypt");
					
			if(oNotificationData.getString("FocusType").equalsIgnoreCase("Connection")) {
				oFocusType = 0;
			}else if(oNotificationData.getString("FocusType").equalsIgnoreCase("Template")){
				oFocusType = 1;
			}else if(oNotificationData.getString("FocusType").equalsIgnoreCase("Validation")) {
				oFocusType = 2;
			}else if(oNotificationData.getString("FocusType").equalsIgnoreCase("AppGroup")){
				oFocusType = 3;
			}else if(oNotificationData.getString("FocusType").equalsIgnoreCase("Run Schema")) {
				oFocusType = 4;
			}

			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sUpdateSql = "update notification_topics set publish_url_1= ?, \"authorization\" = ?, service_id = ?, password = ?, publish_url_2 = ?, url2_authorization=?, url2_service_id=?, url2_password=? where row_id = ?";
			else
				sUpdateSql = "update notification_topics set publish_url_1= ?, authorization = ?, service_id = ?, password = ?, publish_url_2 = ?, url2_authorization=?, url2_service_id=?, url2_password=? where row_id = ?";

			LOG.debug("saveFullNotificationData:"+sUpdateSql);
			LOG.debug("Username:"+oNotificationData.getString("Username"));
			
			jdbcTemplate.update(sUpdateSql, oNotificationData.getString("PublishUrlFirst"),
					oNotificationData.getString("Authorization"), oNotificationData.getString("Username"), sPassword,
					oNotificationData.getString("PublishUrlSecond"), oNotificationData.getString("url2_Authorization"),
					oNotificationData.getString("url2_Username"), sUrl2Password, topicRowId);
	
			sUpdateSql = "";
			sUpdateSql = sUpdateSql + "update notification_topic_versions \n";
			sUpdateSql = sUpdateSql + String.format(
						"set base_media_ids = '%1$s', message_subject = '%2$s', message_body = '%3$s' \n",
						oNotificationData.getString("BaseMediaIds"), sMsgSubject, sMsgBody
					);
			sUpdateSql = sUpdateSql + String.format("where row_id = %1$s;", sVersionRowId);
			jdbcTemplate.update(sUpdateSql);
	
			sDeleteSql = String.format("delete from notification_project_subscriptions where topic_row_id = %1$s;", sTopicRowId);
			jdbcTemplate.batchUpdate(sDeleteSql);
	
			for (String sProjectRowId : aProjectRowIds) {
				sInsertSql = String.format("insert into notification_project_subscriptions (topic_row_id, project_row_id, focus_type) values (%1$s, %2$s, %3$s);", sTopicRowId, sProjectRowId, oFocusType);
				jdbcTemplate.update(sInsertSql);
			}
	
			for (String sKey : oNotificationData.keySet()) {
				if (sKey.startsWith("project-email-")) {
					String sProjectRowId = sKey.split("-")[2];
	
					sUpdateSql = String.format("update notification_project_subscriptions set notification_email = '%1$s' where project_row_id = %2$s;", oNotificationData.getString(sKey), sProjectRowId);
					jdbcTemplate.update(sUpdateSql);
					DateUtility.DebugLog("verfiySelectedVersion project email changes", String.format("Project email to be changed %1$s, %2$s, %3$s", sKey, sProjectRowId, oNotificationData.getString(sKey)) );
				}
			}
			
			sMsg = "Successfully saved notification data changes";
			saveJson.put("message",sMsg);
		}
		return saveJson;
	}
	
	public JSONObject saveFullExternalApiAlertData(JSONObject oExternalApiAlertData) {
		String sMsg = "", sUpdateAPIsql = "";
		JSONObject json = new JSONObject();
		/* fix for ' character in defect save*/
		if (oExternalApiAlertData.getString("AlertMsg") != null) { oExternalApiAlertData.put("AlertMsg", oExternalApiAlertData.getString("AlertMsg").trim().replace("'", "\\'"));	}
		
		sUpdateAPIsql = "UPDATE notification_alert_api set alert_msg = '%1$s', alert_msg_code = %2$s where parent_topic_row_id = %3$s and topic_row_id = %4$s; ";
		sUpdateAPIsql = String.format(sUpdateAPIsql, oExternalApiAlertData.getString("AlertMsg"), oExternalApiAlertData.getString("AlertMsgCode"), oExternalApiAlertData.getString("ParentTopicRowId"), oExternalApiAlertData.getString("SubTopicRowId") );
		
		DateUtility.DebugLog("ExternalApiAlertDataCopy 02 sUpdateAPIsql:",	sUpdateAPIsql);
		
		jdbcTemplate.update(sUpdateAPIsql);
		sMsg = "Successfully saved External Api Alert data changes";
		json.put("message", sMsg);
		json.put("status","success");
		return json;
	}
	
	public static String getEncryptedOrDecryptedPassword(String sPassword, String context) {
		String lRetValue = "";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		if (context.equalsIgnoreCase("Encrypt")) {
			lRetValue = encryptor.encrypt(sPassword);
		} else if (context.equalsIgnoreCase("Decrypt")) {
			lRetValue = encryptor.decrypt(sPassword);
		}
		return lRetValue;
	}
	
	public static boolean isInvalidCharactersInString(String sValue) {
		boolean lRetValue = false;
		
		if (sValue.indexOf('\\') > -1) {
			lRetValue = true;
		} else if (sValue.indexOf("'") > -1) {
			lRetValue = true;
		}
		return lRetValue;
	}
	
	public JSONObject loadExternalApiAlertDataList() throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList;
		
		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();
		ObjectMapper oMapper = new ObjectMapper();
		
		String[] aColumnSpec = new String[] {
				"SubTopicRowId", "SubTopicTitle", "AlertMsg", "AlertMsgCode", "AlertLabel", "ParentTopicRowId", "ParentTopicTitle",
				"FocusType", "SubTopicRowId:edit"
		};
		
		try {
			sDataSql = "";
			sDataSql = 	"SELECT core_qry.*, sub_qry.ParentTopicTitle FROM ( " +
						"SELECT notification_alert_api.topic_row_id AS SubTopicRowId, notification_alert_api.alert_msg AS AlertMsg, notification_alert_api.alert_msg_code AS AlertMsgCode, " +
						"notification_alert_api.alert_label AS AlertLabel, notification_alert_api.parent_topic_row_id AS ParentTopicRowId, notification_topics.topic_title AS SubTopicTitle, " +
						"CASE WHEN notification_topics.focus_type = 0 THEN 'Connection' WHEN notification_topics.focus_type = 1 THEN 'Template' " +
						"WHEN notification_topics.focus_type = 2 THEN 'Validation' WHEN notification_topics.focus_type = 3 THEN 'AppGroup' " +
						"WHEN notification_topics.focus_type = 4 THEN 'Run Schema' ELSE 'Undefined Type' END AS FocusType " +
						"FROM notification_alert_api, notification_topics WHERE notification_alert_api.topic_row_id = notification_topics.row_id " + 
						") AS core_qry " +
						"LEFT OUTER JOIN ( " +
						"SELECT row_id AS ParentTopicRowId, topic_title AS ParentTopicTitle FROM notification_topics " +
						") AS sub_qry ON core_qry.ParentTopicRowId = sub_qry.ParentTopicRowId " ;

			DateUtility.DebugLog("loadExternalApiAlertDataList 01 sDataSql:", sDataSql);
			
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		return oJsonRetValue;
	}
}
