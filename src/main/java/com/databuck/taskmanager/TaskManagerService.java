package com.databuck.taskmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.databuck.filemonitoring.DatabricksFileMonitoringService;
import com.databuck.service.TaskService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.databuck.bean.AppGroupJobQueue;
import com.databuck.bean.DataQualitySQSRequest;
import com.databuck.bean.DatabuckSNSRequest;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainJobQueue;
import com.databuck.bean.DomainLiteJobQueue;
import com.databuck.bean.JiraIntegrationBean;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ProjectJobQueue;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobQueue;
import com.databuck.constants.DatabuckConstants;
import com.databuck.controller.JSONController;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.econstants.AlertManagement;
import com.databuck.econstants.TaskTypes;
import com.databuck.exception.AppGroupTriggerFailedException;
import com.databuck.exception.ConnectionTriggerFailedException;
import com.databuck.exception.DomainTriggerFailedException;
import com.databuck.exception.ProjectTriggerFailedException;
import com.databuck.exception.TemplateTriggerFailedException;
import com.databuck.exception.ValidationTriggerFailedException;
import com.databuck.integration.IntegrationMasterService;
import com.databuck.integration.JiraIntegrationService;
import com.databuck.service.DomainLiteJobService;
import com.databuck.service.RemoteClusterAPIService;
import com.databuck.util.ConnectionUtil;
import com.databuck.util.DateUtility;

@Service
@EnableScheduling
public class TaskManagerService {

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private JSONController jsonController;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private SchemaDAOI schemaDao;

	@Autowired
	private DomainLiteJobService domainLiteJobService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private ConnectionUtil connectionUtil;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private JiraIntegrationService jiraIntegrationService;

	@Autowired
	private IntegrationMasterService integrationMasterService;

	@Autowired
	private DatabricksFileMonitoringService databricksFileMonitoringService;

	@Autowired
	private TaskService taskService;

	private String lastTimeStamp="";

	// Fixed time every 10 seconds
	@Scheduled(fixedDelay = 10000)
	public void processQueue() {
		try {
			// Get the property value of Max active jobs count
			String maxActiveJobs = appDbConnectionProperties.getProperty("maxActiveJobCount");

			int maxActiveJobCount = 16;
			if (maxActiveJobs != null && !maxActiveJobs.trim().isEmpty()) {
				maxActiveJobCount = Integer.parseInt(maxActiveJobs);
			}
			// Get the list of Template and validation jobs in queue
			List<RunningTaskDTO> queueJobsList = iTaskDAO.getJobsInQueue();

			// Get the list of Template and validation jobs in stared and in-progress state
			List<RunningTaskDTO> activeJobsList = iTaskDAO.getJobsUnderProcessing();
			Set<Long> templates_list = new HashSet<Long>();
			Set<Long> validation_list = new HashSet<Long>();

			if (activeJobsList != null) {
				for (RunningTaskDTO runningTaskDTO : activeJobsList) {
					String taskType = runningTaskDTO.getTaskType();
					long applicationId = runningTaskDTO.getApplicationId();

					if (applicationId != 0l) {
						if (taskType.equalsIgnoreCase("template")) {

							templates_list.add(applicationId);
						} else if (taskType.equalsIgnoreCase("validation")) {
							validation_list.add(applicationId);
						}
					}
				}
			}

			// Check the count of active template and validation jobs
			int currentActiveJobsCount = iTaskDAO.getRunningTasksCount();

			int totalActiveJobsCount = currentActiveJobsCount;

			if (queueJobsList != null && queueJobsList.size() > 0) {

				Set<Long> new_templates_list = new HashSet<Long>();
				Set<Long> new_validation_list = new HashSet<Long>();

				for (RunningTaskDTO runningTaskDTO : queueJobsList) {
					if (totalActiveJobsCount >= maxActiveJobCount) {
						break;
					} else {

						String taskType = runningTaskDTO.getTaskType();
						long applicationId = runningTaskDTO.getApplicationId();
						String uniqueId = runningTaskDTO.getUniqueId();

						if (taskType.equalsIgnoreCase("template")) {

							// Check if there is an application in progress already and just started
							if (!(templates_list.contains(applicationId)
									|| new_templates_list.contains(applicationId))) {
								System.out.println("\n******* TaskManagerService:  processQueue *******");
								System.out.println("\nMaximum Active Job count:" + maxActiveJobCount);
								System.out.println("\nCurrent Active Job count:" + currentActiveJobsCount);
								System.out.println("\nQueued Job count:" + queueJobsList.size());

								System.out.println("\nStarting Task[" + taskType + "] with id:" + applicationId);
								triggerTemplateJob(applicationId, uniqueId);

								new_templates_list.add(applicationId);
								++totalActiveJobsCount;
							}
						} else if (taskType.equalsIgnoreCase("validation")) {

							// Check if there is an application in progress already and just started
							if (!(validation_list.contains(applicationId)
									|| new_validation_list.contains(applicationId))) {
								System.out.println("\n******* TaskManagerService:  processQueue *******");
								System.out.println("\nMaximum Active Job count:" + maxActiveJobCount);
								System.out.println("\nCurrent Active Job count:" + currentActiveJobsCount);
								System.out.println("\nQueued Job count:" + queueJobsList.size());

								System.out.println("\nStarting Task[" + taskType + "] with id:" + applicationId);
								triggerValidationJob(applicationId, uniqueId);

								new_validation_list.add(applicationId);
								++totalActiveJobsCount;
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fixed time every 15 seconds
	@Scheduled(fixedDelay = 15000)
	public void processSchemaQueue() {
		try {
			// Fetch the jobs whose associations are in progress
			// Check if the associations are processed, update the status to 'completed'
			List<SchemaJobQueue> progressingJobsList = iTaskDAO.getAssociationsInProgressSchemaJobList();
			if (progressingJobsList != null && progressingJobsList.size() > 0) {
				for (SchemaJobQueue schemaJobQueue : progressingJobsList) {
					if (schemaJobQueue != null) {
						Long idDataSchema = schemaJobQueue.getIdDataSchema();
						String schemaJobUniqueId = schemaJobQueue.getUniqueId();

						if (idDataSchema != null && idDataSchema != 0l) {
							// Get the associations
							List<Map<String, Object>> idDataList = iTaskDAO
									.getAssociatedTemplatesForSchema(schemaJobUniqueId, idDataSchema);

							boolean jobCompleted = true;

							// Check the status of the associations
							if (idDataList != null && idDataList.size() > 0) {
								for (Map<String, Object> dataMap : idDataList) {
									Long idData = Long.parseLong(dataMap.get("idData").toString());
									String uniqueId = dataMap.get("template_uniqueId").toString();
									String templStatus = iTaskDAO.getTemplateCreationJobStatusById(idData, uniqueId);

									if (templStatus != null && (templStatus.equalsIgnoreCase("queued")
											|| templStatus.equalsIgnoreCase("started")
											|| templStatus.equalsIgnoreCase("in progress"))) {
										jobCompleted = false;
										break;
									}
								}
							}

							if (jobCompleted) {
								// If all the associated templates are completed, change the status to completed
								iTaskDAO.updateSchemaJobRunStatus(idDataSchema, schemaJobUniqueId, "completed");

								//perform health check if eligible
								performSchemaHealthCheck(schemaJobQueue);

								//Save Schema run details to alert log
								ListDataSchema listDataSchema = schemaDao.readdatafromlistdataschema(idDataSchema).get(0);
								integrationMasterService.saveAlertEventLog(schemaJobUniqueId, 1l, listDataSchema.getProjectId(), idDataSchema,
										listDataSchema.getSchemaName(),TaskTypes.schema, AlertManagement.RunSchema_Success, "completed", listDataSchema.getUsername(), null);
							}
						}

					}
				}
			}

			List<SchemaJobQueue> queueJobsList = iTaskDAO.getSchemaJobsInQueue();
			if (queueJobsList != null && queueJobsList.size() > 0) {
				for (SchemaJobQueue schemaJobQueue : queueJobsList) {

					// Get the schema Id
					Long idDataSchema = schemaJobQueue.getIdDataSchema();

					if (schemaJobQueue != null) {
						if (idDataSchema != null && idDataSchema != 0l) {

							String uniqueId = schemaJobQueue.getUniqueId();

							// Check if the same Id is in progress
							boolean isJobExists = iTaskDAO.checkIfSchemaJobInProgress(idDataSchema);

							System.out.println("\n******* TaskManagerService:  processSchemaQueue *******");

							// If same Id is in progress do not trigger again
							if (isJobExists) {
								System.out.println("\n====> Schema Job with Id: " + idDataSchema
										+ " is already in progress!! So the job cannot be triggered !!");
							} else {
								// Delete old entries and associations of that idDataSchema
								// iTaskDAO.deleteOldQueueEntriesAndAssociationsOfSchema(idDataSchema);

								// Trigger schema job
								System.out.println("\nStarting Schema with id:[" + idDataSchema + "] !!");

								triggerSchemaJob(idDataSchema, uniqueId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Fixed time every 12 seconds
	@Scheduled(fixedDelay = 12000)
	public void processAppGroupQueue() {
		try {
			long validationId=0l;
			// Fetch the jobs whose associations are in progress
			// Check if the associations are processed, update the status to 'completed'
			List<AppGroupJobQueue> progressingJobsList = iTaskDAO.getAssociationsInProgressAppGroupList();

			if (progressingJobsList != null && progressingJobsList.size() > 0) {

				for (AppGroupJobQueue appGroupJobQueue : progressingJobsList) {

					if (appGroupJobQueue != null) {
						Long idAppGroup = appGroupJobQueue.getIdAppGroup();
						String appGroupUniqueId = appGroupJobQueue.getUniqueId();

						if (idAppGroup != null && idAppGroup != 0l) {
							// Get the associations
							List<Map<String, Object>> idAppList = iTaskDAO
									.getAssociatedValidationsForAppGroupJob(appGroupUniqueId, idAppGroup);

							boolean jobCompleted = true;

							// Check the status of the associations
							if (idAppList != null && idAppList.size() > 0) {
								for (Map<String, Object> dataMap : idAppList) {
									Long idApp = Long.parseLong(dataMap.get("idApp").toString());
									String validation_uniqueId = (String) dataMap.get("validation_uniqueId");

									if(validationId <=0 && idApp > 0l)
										validationId = idApp;

									if (validation_uniqueId != null) {
										validation_uniqueId = validation_uniqueId.toString();
										String validationStatus = iTaskDAO.getValidationJobStatusById(idApp,
												validation_uniqueId);

										if (validationStatus != null && (validationStatus.equalsIgnoreCase("queued")
												|| validationStatus.equalsIgnoreCase("started")
												|| validationStatus.equalsIgnoreCase("in progress"))) {
											jobCompleted = false;
											break;
										}
									}
								}
							}

							if (jobCompleted) {
								// If all the associated validations are completed, change the status to completed
								iTaskDAO.updateAppGroupJobRunStatus(idAppGroup, appGroupUniqueId, "completed");

								//Save Appgroup run info to alert log
								ListDataSource listDataSource = validationCheckDAO.getTemplateDetailsForAppId(validationId);

								ListAppGroup listAppGroup = iTaskDAO.getListAppGroupById(idAppGroup);
								integrationMasterService.saveAlertEventLog(appGroupUniqueId, 1l, listDataSource.getProjectId(), idAppGroup,
										listAppGroup.getName(),TaskTypes.appgroup, AlertManagement.RunAppGroup_Success, "completed", listDataSource.getCreatedByUser(),null);
							}
						}

					}
				}
			}

			List<AppGroupJobQueue> queueJobsList = iTaskDAO.getAppGroupJobsInQueue();

			if (queueJobsList != null && queueJobsList.size() > 0) {

				for (AppGroupJobQueue appGroupJobQueue : queueJobsList) {

					// Get the AppGroup Id
					Long idAppGroup = appGroupJobQueue.getIdAppGroup();

					if (appGroupJobQueue != null) {
						if (idAppGroup != null && idAppGroup != 0l) {

							String uniqueId = appGroupJobQueue.getUniqueId();

							// Check if the same Id is in progress
							boolean isJobExists = iTaskDAO.checkIfAppGroupJobInProgress(idAppGroup);

							System.out.println("\n******* TaskManagerService:  processAppGroupQueue *******");

							// If same Id is in progress do not trigger again
							if (isJobExists) {
								System.out.println("\n====> AppGroup Job with Id: " + idAppGroup
										+ " is already in progress!! So the job cannot be triggered !!");
							} else {
								// Trigger appGroup job
								System.out.println("\nStarting AppGroup with id:[" + idAppGroup + "] !!");

								triggerAppGroupJob(idAppGroup, uniqueId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fixed time every 10 seconds
	@Scheduled(fixedDelay = 10000)
	public void processProjectQueue() {
		try {
			// Fetch the jobs whose associations are in progress
			// Check if the associations are processed, update the status to 'completed'
			List<ProjectJobQueue> progressingJobsList = iTaskDAO.getAssociationsInProgressProjectJobList();

			if (progressingJobsList != null && progressingJobsList.size() > 0) {

				for (ProjectJobQueue projectJobQueue : progressingJobsList) {

					if (projectJobQueue != null) {
						Long projectId = projectJobQueue.getProjectId();
						String projectJobUniqueId = projectJobQueue.getUniqueId();

						if (projectId != null && projectId != 0l) {
							// Get the associations
							List<Map<String, Object>> idDataSchemaList = iTaskDAO
									.getAssociatedConnectionsForProject(projectJobUniqueId, projectId);

							boolean jobCompleted = true;

							// Check the status of the associations
							if (idDataSchemaList != null && idDataSchemaList.size() > 0) {

								for (Map<String, Object> dataMap : idDataSchemaList) {
									String connection_uniqueId = dataMap.get("connection_uniqueId").toString();
									String schemaStatus = iTaskDAO.getSchemaJobStatusByUniqueId(connection_uniqueId);

									if (schemaStatus != null && (schemaStatus.equalsIgnoreCase("queued")
											|| schemaStatus.equalsIgnoreCase("started")
											|| schemaStatus.equalsIgnoreCase("in progress")
											|| schemaStatus.equalsIgnoreCase("subtasks in progress"))) {
										jobCompleted = false;
										break;
									}
								}
							}

							if (jobCompleted) {
								// If all the associated connections are completed, change the project status to
								// completed
								iTaskDAO.updateProjectJobRunStatus(projectId, projectJobUniqueId, "completed");
							}
						}

					}
				}
			}

			List<ProjectJobQueue> queueJobsList = iTaskDAO.getProjectJobsInQueue();

			if (queueJobsList != null && queueJobsList.size() > 0) {

				for (ProjectJobQueue projectJobQueue : queueJobsList) {
					// Get the schema Id
					Long projectId = projectJobQueue.getProjectId();

					if (projectJobQueue != null) {
						if (projectId != null && projectId != 0l) {

							String uniqueId = projectJobQueue.getUniqueId();

							// Check if the same Id is in progress
							boolean isJobExists = iTaskDAO.checkIfProjectJobInProgress(projectId);

							System.out.println("\n******* TaskManagerService:  processProjectJobQueue *******");

							// If same Id is in progress do not trigger again
							if (isJobExists) {
								System.out.println("\n====> Project Job with Id: " + projectId
										+ " is already in progress!! So the job cannot be triggered !!");
							} else {
								// Delete old entries and associations of that idDataSchema
								// iTaskDAO.deleteOldQueueEntriesAndAssociationsOfProject(projectId);

								// Trigger Project job
								System.out.println("\nStarting Project with id:[" + projectId + "] !!");

								triggerProjectJob(projectId, uniqueId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Fixed time every 10 seconds
	@Scheduled(fixedDelay = 10000)
	public void processDomainQueue() {
		try {
			// Fetch the jobs whose projects are in progress
			// Check if the projects are processed, update the status to 'completed'
			List<DomainJobQueue> progressingJobsList = iTaskDAO.getInProgressDomainJobList();

			if (progressingJobsList != null && progressingJobsList.size() > 0) {

				for (DomainJobQueue domainJobQueue : progressingJobsList) {

					if (domainJobQueue != null) {
						Long domainId = domainJobQueue.getDomainId();
						String domainJobUniqueId = domainJobQueue.getUniqueId();

						if (domainId != null && domainId != 0l) {
							// Get the projects
							List<Map<String, Object>> projectsList = iTaskDAO
									.getAssociatedProjectsForDomain(domainJobUniqueId, domainId);

							boolean jobCompleted = true;

							// Check the status of the associations
							if (projectsList != null && projectsList.size() > 0) {

								for (Map<String, Object> dataMap : projectsList) {
									String project_uniqueId = dataMap.get("project_uniqueId").toString();
									String projectStatus = iTaskDAO.getProjectJobStatusByUniqueId(project_uniqueId);

									if (projectStatus != null && (projectStatus.equalsIgnoreCase("queued")
											|| projectStatus.equalsIgnoreCase("started")
											|| projectStatus.equalsIgnoreCase("in progress")
											|| projectStatus.equalsIgnoreCase("subtasks in progress"))) {
										jobCompleted = false;
										break;
									}
								}
							}

							if (jobCompleted) {
								// If all the associated projects are completed, change the domain status to
								// completed
								iTaskDAO.updateDomainJobRunStatus(domainId, domainJobUniqueId, "completed");
							}
						}

					}
				}
			}
			List<DomainJobQueue> queueJobsList = iTaskDAO.getDomainJobsInQueue();

			if (queueJobsList != null && queueJobsList.size() > 0) {

				for (DomainJobQueue domainJobQueue : queueJobsList) {
					// Get the domain Id
					Long domainId = domainJobQueue.getDomainId();

					if (domainJobQueue != null) {
						if (domainId != null && domainId != 0l) {

							String uniqueId = domainJobQueue.getUniqueId();

							// Check if the same Id is in progress
							boolean isJobExists = iTaskDAO.checkIfDomainJobInProgress(domainId);

							System.out.println("\n******* TaskManagerService:  processDomainJobQueue *******");

							// If same Id is in progress do not trigger again
							if (isJobExists) {
								System.out.println("\n====> Domain Job with Id: " + domainId
										+ " is already in progress!! So the job cannot be triggered !!");
							} else {
								// Trigger Domain job
								System.out.println("\nStarting Domain with id:[" + domainId + "] !!");

								triggerDomainJob(domainId, uniqueId);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Fixed time every 5 seconds
	@Scheduled(fixedDelay = 5000)
	public void monitorAndPlaceMsgInSQS() {
		try {
			String sqsMessageEnabled = appDbConnectionProperties.getProperty("sqs.notifications");

			if (sqsMessageEnabled != null && sqsMessageEnabled.trim().equalsIgnoreCase("Y")) {

				// Get SQS Queue Url and region
				String sqsQueueUrl = appDbConnectionProperties.getProperty("sqs.notifications.queue.url");

				String region = appDbConnectionProperties.getProperty("sqs.notifications.queue.region");

				if (sqsQueueUrl != null && !sqsQueueUrl.trim().isEmpty() && region != null
						&& !region.trim().isEmpty()) {

					// Get the List of Validations for which SQS message has to be sent

					List<DataQualitySQSRequest> dqSQSList = iTaskDAO.getDataQualitySQSRequestList();

					if (dqSQSList != null) {
						for (DataQualitySQSRequest dqReq : dqSQSList) {

							// Get Validation Id
							long idApp = dqReq.getIdApp();

							// Fetch Validation Details
							ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);

							if (listApplications != null) {
								List<ListDataSource> listDataSources = validationCheckDAO
										.getdatafromlistdatasource(listApplications.getIdData());

								if (listDataSources != null && listDataSources.size() > 0) {
									ListDataSource listDataSource = listDataSources.get(0);

									// Get DataSource Type
									String dataLocation = listDataSource.getDataLocation();
									String locationName = "";

									// Get Connection details
									Long idDataSchema = listDataSource.getIdDataSchema();

									if (idDataSchema != null && idDataSchema != 0l) {
										List<ListDataSchema> listDataSchemas = listDataSourceDao
												.getListDataSchemaForIdDataSchema(idDataSchema);

										if (listDataSchemas != null && listDataSchemas.size() > 0) {

											ListDataSchema listDataSchema = listDataSchemas.get(0);

											if (dataLocation.equalsIgnoreCase("FileSystem Batch")) {
												locationName = listDataSchema.getFolderPath();

											} else if (dataLocation.equalsIgnoreCase("S3 Batch")
													|| dataLocation.equalsIgnoreCase("S3 IAMRole Batch")
													|| dataLocation.equalsIgnoreCase("S3 IAMRole Batch Config")) {
												locationName = listDataSchema.getBucketName() + "/"
														+ listDataSchema.getFolderPath();
											} else {
												locationName = listDataSchema.getSchemaName();
											}
										}
									}

									// Get the request details
									long requestId = dqReq.getId();
									String execDate = new SimpleDateFormat("yyyy-MM-dd")
											.format(dqReq.getExecutionDate());
									int run = Long.valueOf(dqReq.getRun()).intValue();
									String uniqueId = dqReq.getUniqueId();

									// Get DQ response
									JSONObject dqResultJson = jsonController.prepareJSONData(idApp, execDate, run,
											false);

									JSONObject feDataQuality = dqResultJson.getJSONObject("feDataQuality");
									String tableOrFileName = (String) feDataQuality.remove("tableOrFileName");

									// File path modification
									String[] b = tableOrFileName.split("/");
									String filename = b[b.length - 1];
									String[] subFolder = tableOrFileName.split(filename);

									if (b.length > 1) {
										locationName = locationName.replace("//", "/") + subFolder[0];
									}

									// Prepare message body
									JSONObject msgJSONObject = new JSONObject();
									msgJSONObject.put("nextStepProceed", "Y");
									msgJSONObject.put("locationName", locationName);
									msgJSONObject.put("tableOrFileName", filename);
									msgJSONObject.put("errorCode", "");
									msgJSONObject.put("uniqueId", uniqueId);
									msgJSONObject.put("timestamp",
											new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
									msgJSONObject.put("feDataQuality", feDataQuality);

									String messageBody = msgJSONObject.toString();

									// Place message in queue
									SendMessageRequest send_msg_request = new SendMessageRequest()
											.withQueueUrl(sqsQueueUrl).withMessageBody(messageBody);

									AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(region).build();
									sqs.sendMessage(send_msg_request);

									// Update the sqs_alert_sent to 'Y'
									iTaskDAO.updateSQSAlertSentStatus(requestId, "Y");
								} else {
									System.out.println(
											"\n====>SQS Message Prepartion failed. Failed to Template details for validation Id:["
													+ idApp + "] !!");
								}
							} else {
								System.out.println("\n====>SQS Message Prepartion failed. Invalid validation Id:["
										+ idApp + "] !!");
							}
						}
					}
				} else {
					System.out.println("\n====> SQS queue Url/ region is missing !!");
				}
			}

		} catch (Exception e) {
			System.out.println("Exception while placing message in SQS queue: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Fixed time every 20 seconds
	@Scheduled(fixedDelay = 20000)
	public void processAlertNotifications() {
		try {
			synchronized (this){
				integrationMasterService.pushtNotifications();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fixed time every 10 seconds
	@Scheduled(fixedDelay = 10000)
	public void cleanProcessedRequestsFromSNSAndSQSQueues() {
		try {
			// Delete processed messages from SQS queue
			iTaskDAO.deleteProcessedMessagesFromSQSQueue();

			// Delete processed messages from SNS queue
			iTaskDAO.deleteProcessedMessagesFromSNSQueue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fixed time every 15 mins
	@Scheduled(fixedDelay = 900000)
	public void monitorAndUpdateKilledJobs() {
		try {
			// Get the list of Template and validation jobs in 'started' and 'in progress'
			// state of the current host
			List<RunningTaskDTO> activeJobsList = iTaskDAO.getJobsUnderProcessingForCurrentHost();

			// Fetch KillTime
			String killTimeProperty = appDbConnectionProperties.getProperty("killTime");

			long killTimeInMillisecs = 0;
			if (killTimeProperty != null && !killTimeProperty.trim().isEmpty()) {
				try {
					String[] hourAndMins = killTimeProperty.trim().split(":");
					int killHour = Integer.parseInt(hourAndMins[0]);
					int killMins = Integer.parseInt(hourAndMins[1]);
					killTimeInMillisecs = killHour * 60 * 60 * 1000 + killMins * 60 * 1000;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			final long overTimeLimit = (killTimeInMillisecs > 0) ? killTimeInMillisecs : (60 * 60 * 1000);

			if (activeJobsList != null) {
				for (RunningTaskDTO runningTaskDTO : activeJobsList) {
					long applicationId = runningTaskDTO.getApplicationId();
					String taskType = runningTaskDTO.getTaskType();
					String uniqueId = runningTaskDTO.getUniqueId();
					long processId = runningTaskDTO.getProcessId();
					String sparkAppId = runningTaskDTO.getSparkAppId();
					String deployMode = runningTaskDTO.getDeployMode();
					Date startTime = runningTaskDTO.getStartTime();
					String runId="";
					boolean isJobRunning=true;

					if (applicationId != 0l) {

						// If processId is a non-zero value
						if (processId > 0l) {

							// Check if SparkApplicationId is present and it is cluster sparkApplicationId
							if (sparkAppId != null && !sparkAppId.trim().isEmpty()
									&& !sparkAppId.trim().toLowerCase().startsWith("local-")) {
								
								String clusterPropertyCategory = iTaskDAO
										.getclusterPropertyCategoryForApplicationId(applicationId);
								if (clusterPropertyCategory.equalsIgnoreCase("databricks")
										&& !clusterPropertyCategory.isEmpty()) {
									
									String databuckLog = getDatabuckLogs();
									String filePath =databuckLog + "/Other/showio_" +uniqueId+".log";
									System.out.println(" =======filePath: "+ filePath);
									if(Files.exists(Paths.get(filePath))) {
										BufferedReader dataRead  = new BufferedReader(new FileReader(filePath));
							            String st;
							            while ((st = dataRead.readLine()) != null) {
							            	if(st.contains("\"run_id\":")) {
							            		runId = st.split(":")[1].trim();
							            		System.out.println(st);
							            	}		            
							            }
							            
							            dataRead.close();
										
									}if (runId.length() > 0l) {
										 isJobRunning = checkDatabricksProcessAliveByPID(Long.parseLong(runId));
										 System.out.println("\n====>Databricks.runId.alive: "+runId);
										}else {
											 isJobRunning = checkDatabricksProcessAliveByPID((processId));
										}
									
									if (!isJobRunning) {

										System.out.println("\n====> [" + taskType + "] task with Id:[" + applicationId
												+ "] , uniqueId[" + uniqueId + "], run_id[" + processId
												+ "] has not running, Hence triggered kill script !!");
										triggerKillJobScript(applicationId, taskType, processId, deployMode, sparkAppId,
												uniqueId);
									}

								}else {

								// Fetch java and spark job status
								Map<String, String> statusMap = getDatabuckJobStatus(applicationId, taskType,
										deployMode, processId, sparkAppId);

								String processId_status = statusMap.get("PID_STATUS");
								String sparkAppId_status = statusMap.get("SPARKAPP_STATUS");

								/*
								 * When both UI process and spark process both are not running, update the
								 * status of job to failed
								 */
								if (processId_status != null && !processId_status.equalsIgnoreCase("RUNNING")
										&& sparkAppId_status != null
										&& !sparkAppId_status.equalsIgnoreCase("RUNNING")) {

									System.out.println("\n====> [" + taskType + "] task with Id:[" + applicationId
											+ "] , uniqueId[" + uniqueId + "], processId[" + processId
											+ "] and sparkApplicationId[" + sparkAppId
											+ "] is not running, changing the status to killed !!");

									// Updating the status to killed
									if (taskType.equalsIgnoreCase("template"))
										iTaskDAO.updateTemplateCreationJobStatus(applicationId, uniqueId, "killed");

									else if (taskType.equalsIgnoreCase("validation"))
										iTaskDAO.updateRunScheduledTask(applicationId, "killed", uniqueId);

								}
								/*
								 * When only UI process is running and spark process is not running, Trigger
								 * kill scripts
								 */
								else if (processId_status != null && processId_status.equalsIgnoreCase("RUNNING")
										&& sparkAppId_status != null
										&& !sparkAppId_status.equalsIgnoreCase("RUNNING")) {

									System.out.println("\n====> [" + taskType + "] task with Id:[" + applicationId
											+ "] , uniqueId[" + uniqueId + "], processId[" + processId
											+ "] and sparkApplicationId[" + sparkAppId
											+ "] has only local process running and SparkApplication is not running, Hence triggered kill script !!");

									triggerKillJobScript(applicationId, taskType, processId, deployMode, sparkAppId,
											uniqueId);
								}
							  }

							} else {

								// Check local processId status
								 isJobRunning = checkProcessAliveByPID(processId);

								/*
								 * When local process (PID) is running and sparkApplicationId is not available,
								 * if job is overtime then trigger kill script
								 */
								if (isJobRunning && startTime != null) {
									Date currentDate= DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);

									long duration = Math.abs(currentDate.getTime() - startTime.getTime());

									// Check if the job is running overtime
									if (duration > overTimeLimit) {

										System.out.println("\n====> [" + taskType + "] task with Id:[" + applicationId
												+ "] , uniqueId[" + uniqueId + "], processId[" + processId
												+ "] and sparkApplicationId[" + sparkAppId
												+ "] has no sparkApplicatinId associated and job is running overtime, Hence triggered kill script !!");

										triggerKillJobScript(applicationId, taskType, processId, deployMode, sparkAppId,
												uniqueId);
									}
								}
								/*
								 * When local process (PID) is not running and sparkApplicationId is not
								 * available update the status to killed
								 */
								else {
									System.out.println("\n====> [" + taskType + "] task with Id:[" + applicationId
											+ "] , uniqueId[" + uniqueId + "], processId[" + processId
											+ "] and sparkApplicationId[" + sparkAppId
											+ "] is not running, changing the status to killed !!");

									// Updating the status to killed
									if (taskType.equalsIgnoreCase("template"))
										iTaskDAO.updateTemplateCreationJobStatus(applicationId, uniqueId, "killed");

									else if (taskType.equalsIgnoreCase("validation"))
										iTaskDAO.updateRunScheduledTask(applicationId, "killed", uniqueId);
								}

							}

						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkDatabricksProcessAliveByPID(long runId) {
		boolean isJobRunning = true;
		try {
			// Check if the run_Id is active or killed
			String command = "databricks runs get --run-id " + runId;
			Process child = Runtime.getRuntime().exec(command);

			BufferedReader stdOut = new BufferedReader(new InputStreamReader(child.getInputStream()));
			String stdOutStr = stdOut.lines().collect(Collectors.joining(System.lineSeparator()));
			JSONObject jsonObj = new JSONObject(stdOutStr);
			String databricksTemplateJobAlive = jsonObj.getJSONObject("state").getString("life_cycle_state");

			if (databricksTemplateJobAlive.equalsIgnoreCase("TERMINATED")) {
				isJobRunning = false;
			} else {
				isJobRunning = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isJobRunning;
	}

	// Fixed time every 5 minutes
	@Scheduled(fixedDelay = 5*60000)
	public void performDatabrickFM() {

		//read appDB property enableDatabricksFileMonitoring
		// If its set as 'Y' make a call to performDatabricksMonitoring

		String enableDatabricksFileMonitoring= "";
		try {
			enableDatabricksFileMonitoring = appDbConnectionProperties.getProperty("enable_databricks_fm");
		}catch (Exception e){
			e.printStackTrace();
		}

		if(enableDatabricksFileMonitoring!=null && enableDatabricksFileMonitoring.trim().equalsIgnoreCase("Y"))
			lastTimeStamp=databricksFileMonitoringService.performDatabricksMonitoring(lastTimeStamp);
		else
			lastTimeStamp = "";
	}


	// Fixed time every 10 seconds
	@Scheduled(fixedDelay = 10000)
	public void processDomainLiteQueue() {
		String uniqueId = null;

		try {
			// Get the list of domain_lite-job in queue
			List<DomainLiteJobQueue> queueJobsList = iTaskDAO.getDomainLiteJobsInQueue();

			if (queueJobsList != null && queueJobsList.size() > 0) {

				for (DomainLiteJobQueue domainLiteJobQueue : queueJobsList) {

					if (domainLiteJobQueue != null) {

						// Get the domain lite Id
						Long domainId = domainLiteJobQueue.getDomainId();
						uniqueId = domainLiteJobQueue.getUniqueId();

						if (domainId != null && domainId > 0l) {

							// Check if the same Id is in progress
							boolean isJobExists = iTaskDAO.checkIfDomainLiteJobInProgress(domainId);

							System.out.println("\n******* TaskManagerService:  processDomainLiteJobQueue *******");

							// If same Id is in progress do not trigger again
							if (isJobExists) {
								System.out.println("\n====> Domain Lite Job with Id: " + domainId
										+ " is already in progress!! So the job cannot be triggered !!");
							} else {

								System.out.println("\n====> Starting Domain Lite with id:[" + domainId + "] !!");

								// Updating the status to started
								boolean status = iTaskDAO.startDomainLiteJobByUniqueId(domainId, uniqueId);

								if (status) {
									// Trigger Domain Lite job
									triggerDomainLiteJob(domainId, uniqueId);
								}
							}
						}
					}
				}
			}
		} catch (ProjectTriggerFailedException e) {
			System.out
					.println("\n====>Failed to start Domain Lite Job with uniqueId [" + uniqueId + "] - Reason: " + e);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean checkProcessAliveByPID(long processId) {
		boolean isJobRunning = false;
        if(clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("azure") && clusterProperties.getProperty("databricksCluster").trim().equalsIgnoreCase("N")){
            System.out.println("\n====>Checking Synapse Job Status");
            isJobRunning = taskService.checkIfAzureJobRunning(processId);
        }else {
            try {
                // Check if the process Id is active or killed
                String command = "ps -p " + processId + " -o pid";
                Process child = Runtime.getRuntime().exec(command);

                BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        if (line.trim().equalsIgnoreCase("PID"))
                            continue;
                        else
                            break;
                    } else {
                        if (line.trim().startsWith("" + processId)) {
                            isJobRunning = true;
                        }
                        break;
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		return isJobRunning;
	}

	public Map<String, String> getDatabuckJobStatus(long taskId, String taskType, String deployMode, long processId,
			String sparkAppId) {
		Map<String, String> statusMap = new HashMap<String, String>();
		try {

			// Getting the script location
			String databuckHome = getDatabuckHome();

			// Fetch Domain Id
			System.out.println("\n====>Fetching Domain details ...");

			long domainId = 0l;
			if (taskType.trim().equalsIgnoreCase("validation")) {
				ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(taskId);
				domainId = listApplications.getDomainId();

			} else if (taskType.trim().equalsIgnoreCase("template")) {
				ListDataSource listDataSource = listDataSourceDao.getDataFromListDataSourcesOfIdData(taskId);
				domainId = listDataSource.getDomain();
			}
			System.out.println("\n====>Domain Id: " + domainId);

			// Fetch Domain name
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			String requestType = "status";
			String scriptPath = databuckHome + "/scripts/stopTask.sh";
			String[] argList = { scriptPath, deployMode, String.valueOf(processId), sparkAppId, requestType,
					domainName };

			String cmd = "sh " + scriptPath + " " + deployMode + " " + processId + " " + sparkAppId + " " + requestType
					+ " " + domainName;
			if(clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("azure") && clusterProperties.getProperty("databricksCluster").trim().equalsIgnoreCase("N")){
				scriptPath = databuckHome + "/scripts/stopTask_azure.sh";
				cmd = "sh " + scriptPath + " "+ processId;
			}
			System.out.println("\n====>cmd: " + cmd);

			System.out.println("\n====>Executing cmd ...");
			ProcessBuilder processBuilder = new ProcessBuilder().command(argList);
			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = null;
			String pid_status = null;
			String sparkApp_status = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				if (line.startsWith("--output--")) {
					line = line.replace("--output--", "");
					String statusList[] = line.split(",");
					pid_status = statusList[0].split(":")[1];
					sparkApp_status = statusList[0].split(":")[1];
					break;
				}
			}

			statusMap.put("PID_STATUS", pid_status);
			statusMap.put("SPARKAPP_STATUS", sparkApp_status);

		} catch (Exception e) {
			System.out.println("\n====>Exception occurred while fetching the status of job !!");
			e.printStackTrace();
		}
		return statusMap;
	}

	public boolean triggerKillJobScript(long taskId, String taskType, long processId, String deployMode,
			String sparkAppId, String uniqueId) {

		boolean status = false;
		try {
			System.out.println("\n====> Trigger Kill Job : START <====");
			System.out.println("TaskId: " + taskId);
			System.out.println("TaskType: " + taskType);
			System.out.println("ProcessId: " + processId);
			System.out.println("DeployMode: " + deployMode);
			System.out.println("SparkAppId: " + sparkAppId);
			String runId = "";
			// Getting the script location
			String databuckHome = getDatabuckHome();

			// Fetch Domain Id
			System.out.println("\n====>Fetching Domain details ...");

			String clusterPropertyCategory="";
			long idDataSchema=0l;

			long domainId = 0l;
			if (taskType.trim().equalsIgnoreCase("validation")) {

				ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(taskId);
				domainId = listApplications.getDomainId();

				clusterPropertyCategory = getClusterPropertyCategoryByIdApp(taskId);
				long idData= listApplications.getIdData();

				idDataSchema= iTaskDAO.getIdDataSchemaByIdData(idData);

			} else if (taskType.trim().equalsIgnoreCase("template")) {

				ListDataSource listDataSource = listDataSourceDao.getDataFromListDataSourcesOfIdData(taskId);
				domainId = listDataSource.getDomain();

				clusterPropertyCategory = getClusterPropertyCategoryByidData(taskId);
				idDataSchema= iTaskDAO.getIdDataSchemaByIdData(taskId);

			} else if (taskType.trim().equalsIgnoreCase("connection")) {
				ListDataSchema listDataSchema = schemaDao.getSchemaDetailsForConnectionUtil(taskId);
				domainId = listDataSchema.getDomainId();

			} else if (taskType.trim().equalsIgnoreCase("project")) {
				domainId = iTaskDAO.getDomainIdByProjectId(taskId);

			} else if (taskType.trim().equalsIgnoreCase("domain")) {
				domainId = taskId;
			}
			System.out.println("\n====>Domain Id: " + domainId);

			// Fetch Domain name
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			if (sparkAppId == null || sparkAppId.trim().isEmpty()) {
				System.out.println("\n====>sparkAppId is missing, setting to value 0 !!");
				sparkAppId = "0";
			}
			String requestType = "kill";

			String terminalLog="";

			if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
					&& !(clusterPropertyCategory.equalsIgnoreCase("cluster"))
					&& !(clusterPropertyCategory.equalsIgnoreCase("local"))
					&& !(clusterPropertyCategory.equalsIgnoreCase("databricks"))){

				terminalLog= initiateRemoteClusterKillTaskAPI(deployMode,""+processId,sparkAppId,requestType,domainName,idDataSchema);
				System.out.println(terminalLog);

			}/*else if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
					&& clusterPropertyCategory.equalsIgnoreCase("databricks")) {
				status = initiateDatabricksKillTaskAPI(processId);
				System.out.println("DatabricksStatus log...." + status);
			}*/else{
				
				if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
						&& clusterPropertyCategory.equalsIgnoreCase("databricks")) {
					String databuckLog = getDatabuckLogs();
					String filePath =databuckLog + "/Other/showio_" +uniqueId+".log";
					System.out.println(" =======filePath: "+ filePath);
					if(Files.exists(Paths.get(filePath))) {
						BufferedReader dataRead  = new BufferedReader(new FileReader(filePath));
			            String st;
			            while ((st = dataRead.readLine()) != null) {
			            	if(st.contains("\"run_id\":")) {
			            		runId = st.split(":")[1].trim();
			            		System.out.println(st);
			            	}		            
			            }
			            
			            dataRead.close();
						
					}
				
				}
				// Stopping the job
				databuckHome = databuckHome.replace("\\", "/");
				System.out.println("=========runId :"+ runId);
				String cmd = databuckHome + "/scripts/stopTask.sh " + deployMode + " " + processId + " " + sparkAppId + " "
						+ requestType + " " + domainName + " " + runId;
				System.out.println("\n====>Kill cmd: " + cmd);

				System.out.println("\n====>Executing cmd ...");
				Process process = Runtime.getRuntime().exec(cmd);

				System.out.println("\n====> Waiting for the script execution to complete ..");
				while (process.isAlive()) {
				}
				if (clusterPropertyCategory != null && !clusterPropertyCategory.trim().isEmpty()
						&& clusterPropertyCategory.equalsIgnoreCase("databricks")) {
					return true;
				}
				// Check if process exited normally or not
				if (process.exitValue() != 0) {
					System.out.println("\n====>Exception occurred while executing stopTask script for [" + taskType
							+ "] task with Id:[" + taskId + "] , uniqueId[" + uniqueId + "], processId[" + processId
							+ "] and sparkApplicationId[" + sparkAppId + "] !!");

					// Read error stream
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String line;

					System.out.println("\n====> Printing error log ..");
					while ((line = bufferedReader.readLine()) != null) {
						System.out.println(line);
					}

					if (bufferedReader != null)
						bufferedReader.close();

				} else {
					System.out.println("\n====>stopTask script execution is successful for [" + taskType
							+ "] task with Id:[" + taskId + "] , uniqueId[" + uniqueId + "], processId[" + processId
							+ "] and sparkApplicationId[" + sparkAppId + "] !!");

					// Checking if the job got killed successfully
					System.out.println("\n====>Checking if the job got killed ..");
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					System.out.println("\n====>reader  .."+ reader.readLine());
					String line = null;
					String killjob_status = null;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
						if (line.startsWith("--output--")) {
							line = line.replace("--output--", "");
							killjob_status = line.split(":")[1];
							killjob_status= killjob_status.split(",")[0];
							break;
						}
					}

					if (reader != null) {
						reader.close();
					}

					if (killjob_status != null && killjob_status.equalsIgnoreCase("SUCCESS")) {
						status = true;

						System.out.println("\n====>[" + taskType + "] task with Id:[" + taskId + "] , uniqueId[" + uniqueId
								+ "], processId[" + processId + "] and sparkApplicationId[" + sparkAppId
								+ "] got killed successfully !!");
					}

				}
			}


		} catch (Exception e) {
			System.out.println("\n====>Exception occurred while killing [" + taskType + "] task with Id:[" + taskId
					+ "] , uniqueId[" + uniqueId + "], processId[" + processId + "] and sparkApplicationId["
					+ sparkAppId + "] !!");

			e.printStackTrace();
		}

		return status;
	}

	private void triggerTemplateJob(long idData, String uniqueId) {
		try {
			long idDataSchema= iTaskDAO.getIdDataSchemaByIdData(idData);
			
			Map<String, String> clusterDetails = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema);
			String clusterPropertyCategory = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema).get("cluster_property_category");
			Properties clusterPropertiesNew = iTaskDAO.getPropertiesFromDB(clusterPropertyCategory);

			System.out.println("template job trigger start");
			// Change the status to started
			iTaskDAO.startTemplateByUniqueId(idData, uniqueId);

			String databuckHome = getDatabuckHome();

			// Get DataLocation
			String datalocation = validationCheckDAO.getDataLocationInListDataSources(idData);
			System.out.println("Datalocation:" + datalocation);

			// Get Domain Name
			ListDataSource listDataSource = listDataSourceDao.getDataFromListDataSourcesOfIdData(idData);
			long domainId = listDataSource.getDomain();
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			String scriptPath = "";

			if (datalocation != null && (datalocation.equalsIgnoreCase("FileSystem")
					|| datalocation.equalsIgnoreCase("FileSystem Batch"))
					|| clusterPropertyCategory.equalsIgnoreCase("local")) {
				scriptPath = databuckHome + "/scripts/runDataProfile1.sh";
			}else {
				scriptPath = databuckHome + "/scripts/runDataProfile.sh";
			}

			// Preparing command and arguments
			String[] list = null;
			String cmd = "";

			/*
			 * Mapr ticket enabled is enabled projectName must be sent in the argument list
			 */
			String maprTicketEnabled = clusterPropertiesNew.getProperty("mapr.ticket.enabled");

			long pid = -1;
			System.out.println("\n====>clusterPropertyCategory="+clusterPropertyCategory);

			if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
					&& !clusterPropertyCategory.equalsIgnoreCase("cluster")
					&& !(clusterPropertyCategory.equalsIgnoreCase("local"))
					&& !(clusterPropertyCategory.equalsIgnoreCase("databricks"))){

				pid= initiateRemoteClusterRunTaskAPI("template",maprTicketEnabled,domainName,idData,idDataSchema);

			} else{
				if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
					// Arguments list
					String[] argList = { scriptPath, domainName, String.valueOf(idData) };
					list = argList;

					cmd = scriptPath + " " + domainName + " " + idData;

				}else if(clusterPropertyCategory.equalsIgnoreCase("databricks")) {
					//For Azure Databricks
					Properties clusterPropertiesDatabricks = iTaskDAO.getPropertiesFromDB("databricks");
					Properties clusterPropertiesS3 = iTaskDAO.getPropertiesFromDB("appdb");
					 if(clusterPropertiesDatabricks.getProperty("deploymentMode") != null && clusterPropertiesDatabricks.getProperty("deploymentMode").equalsIgnoreCase("databricks")) {
		             String clusterPolicyId = clusterDetails.get("cluster_policy_id");
		    					
		             String[] argList = { scriptPath, idData+"", uniqueId, clusterPolicyId,clusterPropertiesDatabricks.getProperty("adls.azure.accessKey"), clusterPropertiesDatabricks.getProperty("adls.azure.storageName"), 
		            		 clusterPropertiesDatabricks.getProperty("adls.azure.containerName"), clusterPropertiesDatabricks.getProperty("adls.azure.propertiesfilePath")};
		    	     list = argList;				
		    		 cmd = scriptPath + " " + idData +" " + uniqueId +" "+ clusterPolicyId +" "+ clusterPropertiesDatabricks.getProperty("adls.azure.accessKey")+ " " + clusterPropertiesDatabricks.getProperty("adls.azure.storageName")+" " 
		    							+ clusterPropertiesDatabricks.getProperty("adls.azure.containerName") + " "+ clusterPropertiesDatabricks.getProperty("adls.azure.propertiesfilePath");
		    					
		    				System.out.println("\n========>Script execution command: " + cmd);
                    	
                    }else {
                    //For AWS Databricks
					String clusterPolicyId = clusterDetails.get("cluster_policy_id");
					
					String[] argList = { scriptPath, idData+"", uniqueId, clusterPolicyId,clusterPropertiesS3.getProperty("s3.aws.accessKey"), clusterPropertiesS3.getProperty("s3.aws.secretKey"), 
							clusterPropertiesS3.getProperty("s3.bucketname"), clusterPropertiesS3.getProperty("s3.aws.regionName")};
					list = argList;				
					cmd = scriptPath + " " + idData +" " + uniqueId +" "+ clusterPolicyId +" "+ clusterPropertiesS3.getProperty("s3.aws.accessKey")+ " " + clusterPropertiesS3.getProperty("s3.aws.secretKey")+" " 
							+ clusterPropertiesS3.getProperty("s3.bucketname") + " "+ clusterPropertiesS3.getProperty("s3.aws.regionName");
					System.out.println("\n========>Script execution command: " + cmd);
                    }
				
				} else {
					String[] argList = { scriptPath, String.valueOf(idData) };
					list = argList;

					cmd = scriptPath + " " + idData;
				}

				if(clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("azure") && clusterProperties.getProperty("databricksCluster").trim().equalsIgnoreCase("N")){
					//This function is to triggered jobs on synapse
					pid = taskService.runAzureJob(scriptPath,idData);
				}else{
					System.out.println("\n====>Script execution command: " + cmd);

					// Execute
					ProcessBuilder builder = new ProcessBuilder().command(list);

					// execute script
					Process process = builder.start();

					System.out.println("\n====>Template creation : [" + idData + "] is in progress !! Please wait ...");

					try {
						if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
							Field f = process.getClass().getDeclaredField("pid");
							f.setAccessible(true);
							pid = f.getLong(process);
							f.setAccessible(false);
						}
					} catch (Exception e) {
						System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
						pid = -1;
					}
				}
			}

			System.out.println("\n====>Process Id: " + pid);

			// Insert process Id into runTemplateTasks table
			iTaskDAO.updateTemplateCreationJobPid(idData, uniqueId, pid);

		} catch (TemplateTriggerFailedException e) {
			System.out.println("\n====>Failed to start template with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println(
					"***** Exception occurred while executing DataAnalysisAndProfilingScript for Template Id : "
							+ idData + " !!!");
			e1.printStackTrace();
			iTaskDAO.updateTemplateCreationJobStatus(idData, uniqueId, "failed");
		}
		System.out.println("template job trigger end");
	}

	private void triggerValidationJob(long idApp, String uniqueId) {
		try {

			long idData = iResultsDAO.getIdDataFromListApplictions(idApp);
			long idDataSchema= iTaskDAO.getIdDataSchemaByIdData(idData);
			Map<String, String> clusterDetails = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema);
			String clusterPropertyCategory = clusterDetails.get("cluster_property_category");
			Properties clusterPropertiesNew = iTaskDAO.getPropertiesFromDB(clusterPropertyCategory);

			System.out.println("validation job triger start");
			// Start the validation
			iTaskDAO.startValidationByUniqueId(idApp, uniqueId);

			iTaskDAO.updateRunningtaskStatus("started", idApp);

			// Read ApplicatonType
			System.out.println("\n===>Reading the application Type of idApp:[" + idApp + "]");

			String appType = validationCheckDAO.getAppTypeFromListApplication(idApp);

			System.out.println("\n===>Application Type: " + appType);

			// Read DataLocation
			System.out.println("\n===>Reading the DataLocation Type of idApp:[" + idApp + "]");

			String dataLocation = validationCheckDAO.getDataLocationForIdApp(idApp);

			System.out.println("\n===>DataLocation Type: " + dataLocation);

			// Check if continuousFileMonitoring enabled
			ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
			String continuousFileMonitoring = "N";
			if (listApplications != null && listApplications.getContinuousFileMonitoring() != null) {
				continuousFileMonitoring = listApplications.getContinuousFileMonitoring().trim();
			}
			System.out.println("\n===>continuousFileMonitoring: " + continuousFileMonitoring);

			// Get Domain Name
			Long domainId = listApplications.getDomainId();
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			Process process = null;

			String databuckHome = getDatabuckHome();

			String scriptLocation = "";
			if (appType != null && appType.equalsIgnoreCase("File Monitoring")
					&& continuousFileMonitoring.equalsIgnoreCase("Y")) {
				System.out.println(
						"\n====> This Validation app is monitored continuously, can't allow manualy triggering !!");
				// Deleting the job from queue
				iTaskDAO.killRunScheduledTask(idApp, uniqueId);
			} else {
				if ((dataLocation != null && (dataLocation.equalsIgnoreCase("FILESYSTEM")
						|| dataLocation.equalsIgnoreCase("FileSystem Batch")))
						|| (appType != null && appType.equalsIgnoreCase("File Monitoring"))
						|| clusterPropertyCategory.equalsIgnoreCase("local")) {

					scriptLocation = databuckHome + "/scripts/runValidation1.sh";

				}else {
					scriptLocation = databuckHome + "/scripts/runValidation.sh";
				}

				System.out.println("\n**** script location: " + scriptLocation);
				System.out.println("\n**** clusterPropertyCategory: " + clusterPropertyCategory);

				// Preparing command and arguments
				List<String> commandList = new ArrayList<String>();
				String cmd = "";

				/*
				 * Mapr ticket enabled is enabled projectName must be sent in the argument list
				 */
				String maprTicketEnabled = clusterPropertiesNew.getProperty("mapr.ticket.enabled");

				long pid = -1;

				if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
						&& !clusterPropertyCategory.equalsIgnoreCase("cluster")
						&& !(clusterPropertyCategory.equalsIgnoreCase("local"))
						&& !(clusterPropertyCategory.equalsIgnoreCase("databricks"))){

					pid= initiateRemoteClusterRunTaskAPI("validation",maprTicketEnabled,domainName,idApp,idDataSchema);

				}else{
					if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
						// Arguments list
						commandList.add(scriptLocation);
						commandList.add(domainName);
						commandList.add("" + idApp);

						cmd = scriptLocation + " " + domainName + " " + idApp;

					}else if (clusterPropertyCategory.equalsIgnoreCase("databricks")) {	
						
						Properties clusterPropertiesS3 = iTaskDAO.getPropertiesFromDB("appdb");
						Properties clusterPropertiesDatabricks = iTaskDAO.getPropertiesFromDB("databricks");
						//For Azure Databricks
						if(clusterPropertiesDatabricks.getProperty("deploymentMode") != null && clusterPropertiesDatabricks.getProperty("deploymentMode").equalsIgnoreCase("databricks")) {
							String clusterPolicyId = clusterDetails.get("cluster_policy_id");
							commandList.add(scriptLocation);
							commandList.add(idApp+"");	
							commandList.add(uniqueId+"");	
							commandList.add(clusterPolicyId);	
							commandList.add(clusterPropertiesDatabricks.getProperty("adls.azure.accessKey"));
							commandList.add(clusterPropertiesDatabricks.getProperty("adls.azure.storageName"));
							commandList.add(clusterPropertiesDatabricks.getProperty("adls.azure.containerName"));
							commandList.add(clusterPropertiesDatabricks.getProperty("adls.azure.propertiesfilePath"));
							
							cmd = scriptLocation + " " + idApp +" " + uniqueId +" "+ clusterPolicyId +" "+ clusterPropertiesDatabricks.getProperty("adls.azure.accessKey")+ " " + clusterPropertiesDatabricks.getProperty("adls.azure.storageName")+" " 
	    							+ clusterPropertiesDatabricks.getProperty("adls.azure.containerName") + " "+ clusterPropertiesDatabricks.getProperty("adls.azure.propertiesfilePath");
							
          
						System.out.println("\n========>Script execution command: " + cmd);
						}else {  
					    //For AWS Databricks
						String clusterPolicyId = clusterDetails.get("cluster_policy_id");
						commandList.add(scriptLocation);
						commandList.add(idApp+"");	
						commandList.add(uniqueId+"");	
						commandList.add(clusterPolicyId);	
						commandList.add(clusterPropertiesS3.getProperty("s3.aws.accessKey"));
						commandList.add(clusterPropertiesS3.getProperty("s3.aws.secretKey"));
						commandList.add(clusterPropertiesS3.getProperty("s3.bucketname"));
						commandList.add(clusterPropertiesS3.getProperty("s3.aws.regionName"));
						
						cmd = scriptLocation + " " + idApp + "" + uniqueId + " "+ clusterPolicyId +" "+ clusterPropertiesS3.getProperty("s3.aws.accessKey")+ " " + clusterPropertiesS3.getProperty("s3.aws.secretKey")+" " 
								+ clusterPropertiesS3.getProperty("s3.bucketname") + " "+ clusterPropertiesS3.getProperty("s3.aws.regionName"); 
						
						System.out.println("\n========>Script execution command: " + cmd);
						}
										
						
					} else {
						commandList.add(scriptLocation);
						commandList.add("" + idApp);

						cmd = scriptLocation + "  " + idApp;
					}

					System.out.println("\n**** Command : " + cmd);

					if(clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("azure") && clusterProperties.getProperty("databricksCluster").trim().equalsIgnoreCase("N")){
						//This function is to triggered jobs on synapse
						pid = taskService.runAzureJob(scriptLocation,idApp);
					}else{
						ProcessBuilder processBuilder = new ProcessBuilder();
						processBuilder.command(commandList);
						process = processBuilder.start();

						if(process!=null) {
							try {
								if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
									Field f = process.getClass().getDeclaredField("pid");
									f.setAccessible(true);
									pid = f.getLong(process);
									f.setAccessible(false);
								}
							} catch (Exception e) {
								System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
								pid = -1;
							}
						}else
							pid=-1;
					}

				}


				System.out.println("\n====>Process Id: " + pid);

				// insert processId into runScheduledTasks
				System.out.println("\n====>Insert processId into runScheduledTasks");
				iTaskDAO.updateRunScheduledTaskPid(idApp, pid, uniqueId);

			}

		} catch (ValidationTriggerFailedException e) {
			System.out.println("\n====>Failed to start validation with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while executing Validation [" + idApp + "] with uniqueId : "
					+ uniqueId + " !!!");
			e1.printStackTrace();

			iTaskDAO.updateRunScheduledTask(idApp, "failed", uniqueId);
		}
		System.out.println("validation job trigger end");
	}

	private void triggerSchemaJob(long idDataSchema, String uniqueId) {
		try {
			System.out.println("schema job trigger start");
			// Change the status to started
			iTaskDAO.startSchemaJobByUniqueId(idDataSchema, uniqueId);

			// Get the project Id of Schema
			ListDataSchema listDataSchema = schemaDao.getSchemaDetailsForConnectionUtil(idDataSchema);

			long domainId = listDataSchema.getDomainId();
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			// Get Script Path
			String databuckHome = getDatabuckHome();
			String scriptPath = databuckHome + "/scripts/runSchema.sh";

			// Preparing command and arguments
			String[] list = null;
			String cmd = "";

			/*
			 * Mapr ticket enabled is enabled projectName must be sent in the argument list
			 */
			String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

			if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
				// Arguments list
				String[] argList = { scriptPath, domainName, String.valueOf(idDataSchema) };
				list = argList;

				cmd = scriptPath + " " + domainName + " " + idDataSchema;

			} else {
				String[] argList = { scriptPath, String.valueOf(idDataSchema) };
				list = argList;

				cmd = scriptPath + " " + idDataSchema;
			}

			System.out.println("\n====>Script execution command: " + cmd);

			// Execute
			ProcessBuilder builder = new ProcessBuilder().command(list);

			// execute script
			Process process = builder.start();

			System.out.println("\n====>Schema: [" + idDataSchema + "] execution is in progress !! Please wait ...");

			long pid = -1;

			try {
				if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getLong(process);
					f.setAccessible(false);
				}
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
				pid = -1;
			}

			System.out.println("\n====>Process Id: " + pid);

			// Insert process Id into schema_jobs_queue table
			iTaskDAO.updateSchemaJobPid(idDataSchema, uniqueId, pid);

		} catch (ConnectionTriggerFailedException e) {
			System.out.println("\n====>Failed to start SchemaJob with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while triggering Schema: " + idDataSchema + " job !!!");
			e1.printStackTrace();
			iTaskDAO.updateSchemaJobRunStatus(idDataSchema, uniqueId, "failed");
		}
	}

	private void triggerAppGroupJob(long idAppGroup, String uniqueId) {
		try {
			// Change the status to started
			iTaskDAO.startAppGroupJobByUniqueId(idAppGroup, uniqueId);

			// Get the appgroup associated domain Id
			ListAppGroup listAppGroup = iTaskDAO.getListAppGroupById(idAppGroup);
			long domainId = iTaskDAO.getDomainIdByProjectId(listAppGroup.getProjectId());
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			// Get Script Path
			String databuckHome = getDatabuckHome();
			String scriptPath = databuckHome + "/scripts/triggerAppGroup.sh";

			// Preparing command and arguments
			String[] list = null;
			String cmd = "";

			/*
			 * Mapr ticket enabled is enabled domainName must be sent in the argument list
			 */
			String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

			if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
				// Arguments list
				String[] argList = { scriptPath, domainName, String.valueOf(idAppGroup) };
				list = argList;

				cmd = scriptPath + " " + domainName + " " + idAppGroup;

			} else {
				String[] argList = { scriptPath, String.valueOf(idAppGroup) };
				list = argList;

				cmd = scriptPath + " " + idAppGroup;
			}

			System.out.println("\n====>Script execution command: " + cmd);

			// Execute
			ProcessBuilder builder = new ProcessBuilder().command(list);

			// execute script
			Process process = builder.start();

			System.out.println("\n====>AppGroup: [" + idAppGroup + "] execution is in progress !! Please wait ...");

			long pid = -1;

			try {
				if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getLong(process);
					f.setAccessible(false);
				}
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
				pid = -1;
			}

			System.out.println("\n====>Process Id: " + pid);

			// Insert process Id into appgroup_jobs_queue table
			iTaskDAO.updateAppGroupJobPid(idAppGroup, uniqueId, pid);

		} catch (AppGroupTriggerFailedException e) {
			System.out.println("\n====>Failed to start AppGroup with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while triggering AppGroup: " + idAppGroup + " job !!!");
			e1.printStackTrace();
			iTaskDAO.updateAppGroupJobRunStatus(idAppGroup, uniqueId, "failed");
		}
	}

	private void triggerProjectJob(long projectId, String uniqueId) {
		try {
			// Change the status to started
			iTaskDAO.startProjectJobByUniqueId(projectId, uniqueId);

			// Get the project Name of Domain
			long domainId = iTaskDAO.getDomainIdByProjectId(projectId);
			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			// Get Script Path
			String databuckHome = getDatabuckHome();
			String scriptPath = databuckHome + "/scripts/runProject.sh";

			// Preparing command and arguments
			String[] list = null;
			String cmd = "";

			/*
			 * Mapr ticket enabled is enabled domainName must be sent in the argument list
			 */
			String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

			if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
				// Arguments list
				String[] argList = { scriptPath, domainName, String.valueOf(projectId) };
				list = argList;

				cmd = scriptPath + " " + domainName + " " + projectId;

			} else {
				String[] argList = { scriptPath, String.valueOf(projectId) };
				list = argList;

				cmd = scriptPath + " " + projectId;
			}

			System.out.println("\n====>Script execution command: " + cmd);

			// Execute
			ProcessBuilder builder = new ProcessBuilder().command(list);

			// execute script
			Process process = builder.start();

			System.out.println("\n====>Project: [" + projectId + "] execution is in progress !! Please wait ...");

			long pid = -1;

			try {
				if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getLong(process);
					f.setAccessible(false);
				}
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
				pid = -1;
			}

			System.out.println("\n====>Process Id: " + pid);

			// Insert process Id into project_jobs_queue table
			iTaskDAO.updateProjectJobPid(projectId, uniqueId, pid);

		} catch (ProjectTriggerFailedException e) {
			System.out.println("\n====>Failed to start Project Job with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while triggering Project: " + projectId + " job !!!");
			e1.printStackTrace();
			iTaskDAO.updateProjectJobRunStatus(projectId, uniqueId, "failed");
		}
	}

	private void triggerDomainJob(long domainId, String uniqueId) {
		try {
			// Change the status to started
			iTaskDAO.startDomainJobByUniqueId(domainId, uniqueId);

			String domainName = iTaskDAO.getDomainNameById(domainId);
			System.out.println("\n====>Domain Name: " + domainName);

			// Get Script Path
			String databuckHome = getDatabuckHome();
			String scriptPath = databuckHome + "/scripts/runDomain.sh";

			// Preparing command and arguments
			String[] list = null;
			String cmd = "";

			/*
			 * Mapr ticket enabled is enabled domainName must be sent in the argument list
			 */
			String maprTicketEnabled = clusterProperties.getProperty("mapr.ticket.enabled");

			if (maprTicketEnabled != null && maprTicketEnabled.trim().equalsIgnoreCase("Y")) {
				// Arguments list
				String[] argList = { scriptPath, domainName, String.valueOf(domainId) };
				list = argList;

				cmd = scriptPath + " " + domainName + " " + domainId;

			} else {
				String[] argList = { scriptPath, String.valueOf(domainId) };
				list = argList;

				cmd = scriptPath + " " + domainId;
			}

			System.out.println("\n====>Script execution command: " + cmd);

			// Execute
			ProcessBuilder builder = new ProcessBuilder().command(list);

			// execute script
			Process process = builder.start();

			System.out.println("\n====>Domain: [" + domainId + "] execution is in progress !! Please wait ...");

			long pid = -1;

			try {
				if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					pid = f.getLong(process);
					f.setAccessible(false);
				}
			} catch (Exception e) {
				System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
				pid = -1;
			}

			System.out.println("\n====>Process Id: " + pid);

			// Insert process Id into domain_jobs_queue table
			iTaskDAO.updateDomainJobPid(domainId, uniqueId, pid);

		} catch (DomainTriggerFailedException e) {
			System.out.println("\n====>Failed to start Domain Job with uniqueId [" + uniqueId + "] - Reason: " + e);

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while triggering Domain: " + domainId + " job !!!");
			e1.printStackTrace();
			iTaskDAO.updateDomainJobRunStatus(domainId, uniqueId, "failed");
		}
	}

	private void triggerDomainLiteJob(long domainId, String uniqueId) {
		try {
			// Change the status to in progress
			iTaskDAO.updateDomainLiteJobRunStatus(domainId, uniqueId, "in progress");

			// Fetch domain details
			Domain domain = iTaskDAO.getDomainDetailsById(domainId);

			boolean status = false;
			if (domain != null) {

				String domainName = domain.getDomainName();
				System.out.println("\n====>Domain Name: " + domainName);

				// Get the execution result of domain lite job
				String domainLiteJobResultJson = domainLiteJobService.prepareDomainLiteJson(domain);

				// Insert json to domain_lite table
				status = iTaskDAO.updateDomainLite(domainId, uniqueId, domainLiteJobResultJson);
			}
			// Change the status to completed or failed
			if (status)
				iTaskDAO.updateDomainLiteJobRunStatus(domainId, uniqueId, "completed");
			else
				iTaskDAO.updateDomainLiteJobRunStatus(domainId, uniqueId, "failed");

		} catch (Exception e1) {
			System.out.println("***** Exception occurred while triggering DomainLiteJob: " + domainId + " job !!!");
			e1.printStackTrace();
			iTaskDAO.updateDomainLiteJobRunStatus(domainId, uniqueId, "failed");
		}
	}

	private String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		System.out.println("\n====>DATABUCK_HOME:" + databuckHome);
		return databuckHome;
	}
	
	private String getDatabuckLogs() {
		String databuckHome = "/opt/databuck/logs";

		if (System.getenv("DATABUCK_LOG") != null && !System.getenv("DATABUCK_LOG").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_LOG");

		} else if (System.getProperty("DATABUCK_LOG") != null
				&& !System.getProperty("DATABUCK_LOG").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_LOG");

		}
		System.out.println("\n====>DATABUCK_LOG:" + databuckHome);
		return databuckHome;
	}

	private void performSchemaHealthCheck(SchemaJobQueue schemaJobQueue){

		// Get the health check status of connection to initiate health check job
		String healthCheckFlag= schemaJobQueue.getHealthCheck();

		if(healthCheckFlag!=null && healthCheckFlag.trim().equalsIgnoreCase("Y")){
			// find the validations for every successful template
			// find latest validations which in created state and change status to unit test ready for every non microsegment and non incremental validation of successful templates

			try{

				Long idDataSchema = schemaJobQueue.getIdDataSchema();
				String schemaJobUniqueId = schemaJobQueue.getUniqueId();

				List<Long> validationList = iTaskDAO.getValidationsForTemplateSuccess(idDataSchema,schemaJobUniqueId);

				if(validationList!=null && validationList.size()>0){

					for(long idApp:validationList){

						if(idApp > 0l){

							int approvalCode = ruleCatalogDao.getApprovalStatusCodeByStatusName(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1);
							ruleCatalogDao.updateValidationRuleCatalogStatus(idApp, approvalCode, "Approving validation for health check run",0l,"");

							String deployMode = clusterProperties.getProperty("deploymode");
							deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local"
									: "cluster";
							String val_uniqueId= iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null, null);
							System.out.println("\n===>Validation job with uniqueId["+val_uniqueId+"] is placed into queue");
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}

	private String initiateRemoteClusterKillTaskAPI(String deployMode,String processId,String sparkAppId,String requestType,String domainName,long idDataSchema){

//		String publishUrl="https://140.238.249.1:8085/cdp/killRemoteClusterTask";
		String publishUrl= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.KILL_JOB_API);
		String token= remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

		String encryptedToken = encryptor.encrypt(token);

		String clusterCategory = iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema).get("cluster_property_category");

		JSONObject inputObj= new JSONObject();

		inputObj.put("domainName",domainName);
		inputObj.put("deployMode",""+deployMode);
		inputObj.put("processId",""+processId);
		inputObj.put("sparkAppId",""+sparkAppId);
		inputObj.put("requestType",requestType);
		inputObj.put("clusterCategory",clusterCategory);

		String inputJson= inputObj.toString();
		System.out.println("\n====>inputJson:"+inputJson);

		String terminalLog= remoteClusterAPIService.killTaskByRemoteCluster(publishUrl,encryptedToken,inputJson);
		return terminalLog;
	}

	private long initiateRemoteClusterRunTaskAPI(String taskName,String maprTicketEnabled,String domainName,long applicationId,long idDataSchema){

//		String publishUrl="https://140.238.249.1:8085/cdp/runRemoteClusterTask";
		String URI= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.RUN_TASK_API);
		String token= remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

		String encryptedToken = encryptor.encrypt(token);
		String propertySource= iTaskDAO.getClusterCategoryNameBySchemaId(idDataSchema).get("cluster_property_category");

		JSONObject inputObj= new JSONObject();

		inputObj.put("domainName",domainName);
		inputObj.put("mapr.ticket.enabled",maprTicketEnabled);
		inputObj.put("taskName",taskName);
		inputObj.put("applicationId",""+applicationId); //applicationId
		inputObj.put("propertySource",""+propertySource);

		String inputJson= inputObj.toString();
		System.out.println("inputJson="+inputJson);

		long pid= remoteClusterAPIService.runTaskByRemoteCluster(URI,encryptedToken,inputJson);
		return pid;
	}

	private String getClusterPropertyCategoryByidData(long idData){

		long idListDataSchema = iTaskDAO.getIdDataSchemaByIdData(idData);

		String clusterPropertyCategory="";

		if (idListDataSchema > 0l) {

			clusterPropertyCategory = iTaskDAO.getClusterCategoryNameBySchemaId(idListDataSchema).get("cluster_property_category");
		}
		return clusterPropertyCategory;
	}

	private String getClusterPropertyCategoryByIdApp(long idApp){

		ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);

		String clusterPropertyCategory="";

		long idData= listApplications.getIdData();

		if (idData > 0l) {

			clusterPropertyCategory = getClusterPropertyCategoryByidData(idData);
		}
		return clusterPropertyCategory;
	}

}

