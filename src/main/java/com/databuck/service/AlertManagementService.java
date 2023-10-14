package com.databuck.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.AlertEventMaster;
import com.databuck.bean.AlertEventSubscription;
import com.databuck.dao.AlertEventDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.restcontroller.AlertManagementRestController;
import org.apache.log4j.Logger;

@Service
public class AlertManagementService {

	@Autowired
	AlertEventDao alertEventDao;

	@Autowired
	private IProjectDAO projectDao;

	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private Properties integrationProperties;
	
	private static final Logger LOG = Logger.getLogger(AlertManagementService.class);

	public JSONObject addEventSubscription(List<AlertEventSubscription> alertEventSubscriptionList) {
		LOG.info("addEventSubscription - START");

		JSONObject resJson = new JSONObject();
		String message = "";
		String status = "failed";
		try {
			if (alertEventSubscriptionList != null && alertEventSubscriptionList.size() > 0) {

				int updateCount = 0;

				for (AlertEventSubscription alertEventSubscription : alertEventSubscriptionList) {

					LOG.debug("projectId:" + alertEventSubscription.getProjectId());
					LOG.debug("eventId:" + alertEventSubscription.getEventId());
					LOG.debug("communicationModeId:" + alertEventSubscription.getCommModeId());
					LOG.debug("IsGlobalSubscription:" + alertEventSubscription.getIsGlobalSubscription());
					LOG.debug("CommunicationValues:" + alertEventSubscription.getCommunicationValues());

					// validate EventId
					AlertEventMaster alertEventMaster = alertEventDao
							.getAlertEventById(alertEventSubscription.getEventId());

					if (alertEventMaster != null) {
						// Validate projectId
						boolean project = projectDao.isProjectIdValid(alertEventSubscription.getProjectId());
						if (!project && alertEventSubscription.getIsGlobalSubscription().equalsIgnoreCase("N")) {
							message = "Invalid ProjectId=>[" + alertEventSubscription.getProjectId() + "]";
							resJson.put("status", status);
							resJson.put("message", message);
							return resJson;
						}
						// Verify if there is any entry already available for this combination
						boolean is_sub_exists = alertEventDao.isAlertEventSubscriptionExists(alertEventSubscription);

						int updateStatus = 0;
						// If exists update subscription else add subscription
						if (is_sub_exists)
							updateStatus = alertEventDao.updateAlertEventSubscription(alertEventSubscription);
						else
							updateStatus = alertEventDao.addAlertEventSubscription(alertEventSubscription);

						if (updateStatus <= 0)
							message = message + "Failed to add event subscription for event with id["
									+ alertEventSubscription.getEventId() + "] for the project["
									+ alertEventSubscription.getProjectId() + "]\n";
						else
							updateCount++;
					} else {
						message = "Invalid EventId=>[" + alertEventSubscription.getEventId() + "]";
						resJson.put("status", status);
						resJson.put("message", message);
						return resJson;
					}
				}
				if (updateCount == alertEventSubscriptionList.size()) {
					message = "Event subscriptions added successfully";
					status = "success";
				}
			} else
				message = "No subscription details found to add";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		resJson.put("status", status);
		resJson.put("message", message);
		return resJson;

	}

	public JSONArray isCommunicationModeEnable() {
		LOG.info("isCommunicationModeEnable -START");
		JSONArray result = new JSONArray();
		try {
			// get all commModeList
			JSONArray communicationModes = alertEventDao.getAllCommunicationModes();
			// validate commModeList
			if (communicationModes != null && communicationModes.length() > 0) {
				for (int i = 0; i < communicationModes.length(); i++) {
					JSONObject jsonObject = communicationModes.getJSONObject(i);
					List<String> missingConfigurationList = new ArrayList<String>();

					int commModeId = jsonObject.getInt("commModeId");
					String commModeName = jsonObject.getString("commModeName");
					String isEmailNotificationEnabled = "Y";
					String isJiraNotificationEnabled = integrationProperties.getProperty("jira.integration.enabled");
					//String isSlackNotificationEnabled = integrationProperties.getProperty("slack.integration.enabled");
					String isSNSNotificationEnabled = appDbConnectionProperties.getProperty("SNSNotifications");
					String isSqsNotificationEnabled = appDbConnectionProperties.getProperty("sqs.notifications");

					String integrationEnabled = "N";
					String isConfigurationEnabled = "N";
					boolean isReqParamsValid = false;
					JSONObject configEnable = new JSONObject();

					if (commModeName.trim().equalsIgnoreCase("email") && isEmailNotificationEnabled.trim().equalsIgnoreCase("Y")) {
						integrationEnabled = isEmailNotificationEnabled;
						// Smtp host
						String smtpHost = appDbConnectionProperties.getProperty("smtp_host");
						// smtp port
						String smtpPort = appDbConnectionProperties.getProperty("smtp_port");
						//get Auth Mode
						String authMode = appDbConnectionProperties.getProperty("smtp_mode");
						// get property mail sender
						String mailSender = appDbConnectionProperties.getProperty("mailSender");
						if (authMode == null || !authMode.equalsIgnoreCase("noAuth")) {
							// smtp username
							String smtpUsername = appDbConnectionProperties.getProperty("smtp_username");
							String smtpPassword = appDbConnectionProperties.getProperty("smtp_password");
							if (smtpUsername == null || smtpUsername.trim().isEmpty()) {
								missingConfigurationList.add("smtpUsername");
							}
							if (smtpPassword == null || smtpPassword.trim().isEmpty()) {
								missingConfigurationList.add("smtpPassword");
							}
						}
						if (smtpHost == null || smtpHost.trim().isEmpty()) {
							missingConfigurationList.add("HOST");
						}
						if (smtpPort == null || smtpPort.trim().isEmpty()) {
							missingConfigurationList.add("smtpPort");
						}
						if (mailSender == null || mailSender.trim().isEmpty()) {
							missingConfigurationList.add("mailSender");
						}
						String missingConfigurations = String.join(",", missingConfigurationList);
						isReqParamsValid = missingConfigurationList.isEmpty() ? true : false;
						configEnable.put("missingConfigurations", missingConfigurations);
					} else if (commModeName.trim().equalsIgnoreCase("jira") && isJiraNotificationEnabled.trim().equalsIgnoreCase("Y")) {
						integrationEnabled = isJiraNotificationEnabled;
						// Get Jira host name and port
						String hostport = integrationProperties.getProperty("jira.api.hostport");
						// Get Jira username
						String username = integrationProperties.getProperty("jira.api.username");
						// Get API Token
						String apiToken = integrationProperties.getProperty("jira.api.apitoken");
						// Get project key
						String projectKey = integrationProperties.getProperty("jira.api.projectkey");

						if (hostport == null || hostport.trim().isEmpty()) {
							missingConfigurationList.add("hostPort");
						}
						if (projectKey == null || projectKey.trim().isEmpty()) {
							missingConfigurationList.add("projectKey");
						}
						if (username == null || username.trim().isEmpty()) {
							missingConfigurationList.add("username");
						}
						if (apiToken == null || apiToken.trim().isEmpty()) {
							missingConfigurationList.add("APIToken");
						}
						String missingConfigurations = String.join(",", missingConfigurationList);
						isReqParamsValid = missingConfigurationList.isEmpty() ? true : false;
						configEnable.put("missingConfigurations", missingConfigurations);
					} else if (commModeName.trim().equalsIgnoreCase("slack")) {
						String slackToken = integrationProperties.getProperty("slack.token");
						if (slackToken == null || slackToken.trim().isEmpty()) {
							missingConfigurationList.add("slackToken");
							integrationEnabled="N";
						}else {
							integrationEnabled="Y";
						}
						String missingConfigurationsList = String.join(",", missingConfigurationList);
						isReqParamsValid = missingConfigurationList.isEmpty() ? true : false;
						configEnable.put("missingConfigurations", missingConfigurationsList);
					} else if (commModeName.trim().equalsIgnoreCase("sns") && isSNSNotificationEnabled.trim().equalsIgnoreCase("Y")) {
						integrationEnabled = isSNSNotificationEnabled;
						isReqParamsValid = missingConfigurationList.isEmpty() ? true : false;
					} else if (commModeName.trim().equalsIgnoreCase("sqs") && isSqsNotificationEnabled.trim().equalsIgnoreCase("Y")) {
						integrationEnabled = isSqsNotificationEnabled;
						isReqParamsValid = missingConfigurationList.isEmpty() ? true : false;
					}
					if (isReqParamsValid) {
						isConfigurationEnabled = "Y";
					}

					configEnable.put("integrationEnabled", integrationEnabled);
					configEnable.put("isConfigurationEnabled", isConfigurationEnabled);
					configEnable.put("communicationModeName", commModeName);
					configEnable.put("communicationModeID", commModeId);
					result.put(configEnable);
				}
			}
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

}