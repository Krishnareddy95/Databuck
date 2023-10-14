package com.databuck.integration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.databuck.service.DashboardSummaryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.AlertEventSubscription;
import com.databuck.bean.DatabuckAlertLog;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.AlertEventDao;
import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.CommunicationModes;
import com.databuck.econstants.TaskTypes;

@Service
public class IntegrationMasterService {

	@Autowired
	private JiraIntegrationService jiraIntegrationService;

	@Autowired
	private EmailIntegrationService emailIntegrationService;

	@Autowired
	private SlackIntegrationService slackIntegrationService;

	@Autowired
	private SNSIntegrationService snsIntegrationService;

	@Autowired
	private SQSIntegrationService sqsIntegrationService;

	@Autowired
	private AlertEventDao alertEventDao;

	@Autowired
	private DashboardSummaryService dashboardSummaryService;

	@Autowired
	private Properties integrationProperties;

	/*
	 * Following method is used to save run info which will be further used in Alert
	 * Notification
	 */
	public void saveAlertEventLog(String taskUniqueId, long jobRunNumber, long projectId, long taskId, String name,
			TaskTypes taskName, AlertManagement eventName, String status, String userName, JSONArray mappingErrors) {

		try {
			String alertEventDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			int eventId = 0;
			String alertEventMessage = "";
			String isEventSubscribed = "N";
			String eventMessageSubject = "";

			JSONObject eventObj = alertEventDao.getEventDetailsByEventName(eventName.toString());
			if (eventObj != null && eventObj.length() > 0) {
				try {
					eventId = eventObj.getInt("event_id");
					alertEventMessage = eventObj.getString("event_message_body");
					eventMessageSubject = eventObj.getString("event_message_subject");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (alertEventDao.isEventSubscribed(eventId, projectId))
				isEventSubscribed = "Y";

			DatabuckAlertLog databuckAlertLog = new DatabuckAlertLog();
			databuckAlertLog.setJobExecutionDate(alertEventDate);
			databuckAlertLog.setEventid(eventId);
			databuckAlertLog.setProjectId(projectId);
			databuckAlertLog.setIsEventPublished("N");
			databuckAlertLog.setIsEventSubscribed(isEventSubscribed);
			databuckAlertLog.setJobRunNumber(jobRunNumber);
			databuckAlertLog.setTaskId(taskId);
			databuckAlertLog.setTaskName(taskName.toString());
			databuckAlertLog.setTaskUniqueId(taskUniqueId == null ? "" : taskUniqueId);

			// Filling focus object id of event subject
			eventMessageSubject = eventMessageSubject.replace(DatabuckConstants.FOCUS_OBJECT_ID_FILLER, "" + taskId);

			databuckAlertLog.setEventMessageSubject(eventMessageSubject);

			// Filling the values of default dynamic objects to prepare Alert Message
			alertEventMessage = alertEventMessage.replace(DatabuckConstants.NAME_FILLER, name);
			alertEventMessage = alertEventMessage.replace(DatabuckConstants.FOCUS_OBJECT_ID_FILLER, "" + taskId);
			alertEventMessage = alertEventMessage.replace(DatabuckConstants.USER_FILLER, "" + userName);
			alertEventMessage = alertEventMessage.replace(DatabuckConstants.STATUS_FILLER, "" + status);

			String hostLoginUrl= integrationProperties.getProperty("databuck.baseurl");

			if(hostLoginUrl!=null && !hostLoginUrl.trim().isEmpty()){
				alertEventMessage = alertEventMessage+"\nPlease login for more details:\n"+hostLoginUrl.trim();
			}

			databuckAlertLog.setAlertMessage(alertEventMessage);

			if (mappingErrors != null && mappingErrors.length() > 0) {
				mappingErrors.put("\n... Please check the logs for complete details");
				databuckAlertLog.setExecutionErrors(mappingErrors.toString());
			} else
				databuckAlertLog.setExecutionErrors("");

			boolean isAlertLogSaved = alertEventDao.saveDatabuckAlertLog(databuckAlertLog);
			if (isAlertLogSaved)
				System.out.println("Databuck Alert Log is saved for the Event:[" + eventName + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushtNotifications() {

		try {

			// Fetch allAlertLogs between range which are subscribed and non published
			List<DatabuckAlertLog> databuckAlertLogList = alertEventDao.getNonPublishedDatabuckAlertLogs();

			if (databuckAlertLogList != null)

				// Iterate over databuckAlert Logs and process each type of communication
				// separately
				for (DatabuckAlertLog databuckAlertLog : databuckAlertLogList) {

					String taskName = databuckAlertLog.getTaskName();
					long taskId = databuckAlertLog.getTaskId();
					String taskUniqueId = databuckAlertLog.getTaskUniqueId();
					long projectId = databuckAlertLog.getProjectId();
					long alertLogId = databuckAlertLog.getAlertId();
					int eventId = databuckAlertLog.getEventid();
					String messageSubject = databuckAlertLog.getAlertMessageSubject();
					String messageBody = databuckAlertLog.getAlertMessage();
					String currentExecutionErrors = databuckAlertLog.getExecutionErrors();
					String executionErrors="";
					String eventName = alertEventDao.getEventNameByEventId(eventId);

					// One event may be subscribed to multiple communication modes
					List<AlertEventSubscription> alertEventSubscriptionList = alertEventDao
							.getAlertSubscriptionByEventId(projectId, eventId);

					// Fetch Global Subscription
					List<AlertEventSubscription> globalSubscriptions = alertEventDao
							.getGlobalAlertSubscriptionByEventId(eventId);

					List<AlertEventSubscription> finalSubscriptionList = new ArrayList<AlertEventSubscription>();

					if(currentExecutionErrors.length()>=300){
						executionErrors=currentExecutionErrors.substring(0,300);
					}else
						executionErrors=currentExecutionErrors;

					if (alertEventSubscriptionList != null)
						finalSubscriptionList.addAll(alertEventSubscriptionList);

					if (globalSubscriptions != null)
						finalSubscriptionList.addAll(globalSubscriptions);

					// Push Notifications
					if (finalSubscriptionList != null && finalSubscriptionList.size() > 0) {

						// Prepare final list
						Map<String, Set<String>> totalSubscription = new HashMap<String, Set<String>>();

						for (AlertEventSubscription eventSubscription : finalSubscriptionList) {

							String communicationMode = eventSubscription.getCommunicaionMode();
							String communicationValues = eventSubscription.getCommunicationValues();

							Set<String> allCommunicationValues = totalSubscription.get(communicationMode);
							if (allCommunicationValues == null || allCommunicationValues.size() == 0) {
								allCommunicationValues = new HashSet<String>();
							}

							allCommunicationValues.add(communicationValues);
							totalSubscription.put(communicationMode, allCommunicationValues);
						}

						System.out.println("\ntotalSubscription: " + totalSubscription);
						System.out.println("alertLogId :: "+alertLogId);
						System.out.println("taskId :: "+taskId);

						if (totalSubscription != null && totalSubscription.size() > 0) {

							// Iterate over one or multiple communication modes and push notification
							for (String communicationMode : totalSubscription.keySet()) {

								// Get the communication mode to call respective functionality
								Set<String> communicationValues = totalSubscription.get(communicationMode);
								System.out.println("\ncommunicationMode:"+communicationMode);
								System.out.println("\ncommunicationValues:"+communicationValues);

								if (communicationValues != null && communicationValues.size() > 0) {

									try {
										// Processing email subscriptions
										if (communicationMode.equalsIgnoreCase(CommunicationModes.email.name())) {
											String recepientEmails = String.join(",", communicationValues);
											String fullMessageBody = messageBody;
                                            if(executionErrors != null && !executionErrors.trim().isEmpty()){
												fullMessageBody = fullMessageBody + "\n\nErrors occurred during execution:\n" + executionErrors.trim();
											}

											emailIntegrationService.sendAlertNotificationByEmail(messageSubject, fullMessageBody, recepientEmails);

										}

										// Processing JIRA subscriptions
										else if (communicationMode.equalsIgnoreCase(CommunicationModes.jira.name())
												&& taskName.equalsIgnoreCase(TaskTypes.validation.name())) {

											for (String jira_project_key : communicationValues) {
												jiraIntegrationService.sendAlertNotificationByJIRA(jira_project_key,
														taskUniqueId);
											}
										}

										// Processing SLACK subscriptions
										else if (communicationMode.equalsIgnoreCase(CommunicationModes.slack.name())) {
											for (String channel_id : communicationValues) {
												slackIntegrationService.sendAlertNotificationBySlack(channel_id,
														messageBody);
											}
										}

										// Processing SNS subscriptions
										else if (communicationMode.equalsIgnoreCase(CommunicationModes.sns.name())) {
											for (String topic : communicationValues) {
												snsIntegrationService.sendAlertNotificationBySNS(topic, messageBody);
											}

										}
										// Processing SQS subscriptions
										else if (communicationMode.equalsIgnoreCase(CommunicationModes.sqs.name())) {
											for (String queueUrl : communicationValues) {
												sqsIntegrationService.sendAlertNotificationBySQS(queueUrl, messageBody);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							alertEventDao.updateDatabuckAlertLogsPublicationDetails(alertLogId, "Y");
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDashbordSummaryDQValidation(Long idApp) {
		try {
			List<Map<String, Object>> dqResultData = dashboardSummaryService.getValidationDQISummaryDetails(idApp);
			String concatMessage = "";
			for (Map<String, Object> stringObjectMap : dqResultData) {
				String test = (String) stringObjectMap.get("test");
				String Key_Metric_1_Label = (String) stringObjectMap.get("Key_Metric_1_Label");
				Double Key_Metric_1 = Double.parseDouble(stringObjectMap.get("Key_Metric_1").toString().replace(",",""));
				String Key_Metric_2_Label = (String) stringObjectMap.get("Key_Metric_2_Label");
				Double Key_Metric_2 = (Double.parseDouble(stringObjectMap.get("Key_Metric_2").toString().replace(",","")));
				Integer run = (Integer) stringObjectMap.get("run");
				Double DQI = Double.parseDouble(stringObjectMap.get("DQI").toString());
				String status = (String) stringObjectMap.get("status");
				if (DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY.contains(test)) {
					concatMessage = concatMessage + "\n" + "CheckName: " + test + ",".concat("DQI: " + DQI + ",").concat("Status: " + status);
				} else {
					concatMessage = concatMessage + "\n" + "CheckName: " + test + ",".concat("DQI: " + DQI + ",").concat("Status: " + status + ",").concat(Key_Metric_1_Label + ": " + Key_Metric_1 + ",").concat(Key_Metric_2_Label + ": " + Key_Metric_2);
				}
			}
			return concatMessage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
