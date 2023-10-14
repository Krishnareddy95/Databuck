package com.databuck.dao;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.databuck.bean.JiraIntegrationBean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.AppGroupJobDTO;
import com.databuck.bean.AppGroupJobQueue;
import com.databuck.bean.AppGroupMapping;
import com.databuck.bean.DataQualitySQSRequest;
import com.databuck.bean.DatabuckSNSRequest;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainJobDTO;
import com.databuck.bean.DomainJobQueue;
import com.databuck.bean.DomainLiteJobDTO;
import com.databuck.bean.DomainLiteJobQueue;
import com.databuck.bean.ExternalAPIAlertPOJO;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListSchedule;
import com.databuck.bean.LoggingActivity;
import com.databuck.bean.NotificationTopics;
import com.databuck.bean.Project;
import com.databuck.bean.ProjectJobDTO;
import com.databuck.bean.ProjectJobQueue;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobDTO;
import com.databuck.bean.SchemaJobQueue;
import com.databuck.bean.Task;
import com.databuck.bean.ValidationRunDTO;
import com.databuck.bean.LoginTrail;
import com.databuck.exception.AppGroupTriggerFailedException;
import com.databuck.exception.ConnectionTriggerFailedException;
import com.databuck.exception.ProjectTriggerFailedException;
import com.databuck.exception.TemplateTriggerFailedException;
import com.databuck.exception.ValidationTriggerFailedException;
import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.ListTrigger;

public interface ITaskDAO {

	// save a new Task
	public int saveTask(String task);


	// Delete A task
	public long deleteTask(long idTask);

	// get Task
	public List<Task> getData();

	// edit Task


	//update the task
	public void saveOrUpdate(Task task);
	public Task get(long idTask);
	public List<ListApplications> listApplicationsView(Long project_id,List<Project> projlst);
	public String getTaskStatusFromRunScheduledTask(Long idApp, String uniqueId);
	public boolean updateRunScheduledTask(Long idApp, String status, String uniqueId);
	public int insertintoscheduledtasks(Long idApp, Long idDataSchema, Long idScheduler,Long project_id);
	public int getCountscheduledtasks(Long idDataSchema,Long project_id);
	public String getTypeOfApplication(long idApp);
	public int insertIntolistSchedule(String name, String description, String frequency, String scheduledDay,String day, String scheduleTimer,Long project_id);
	public int updateListSchedule(long idSchedule, String name, String description, String frequency, String scheduledDay, String day, String scheduleTimer);
	public ListSchedule getSchedulerById(long idSchedule);
	public List<Long> getValditionsForSchedule(Long idScheduler);
	public Map<Long,String> getListScheduleData(Long project_id,List<Project> projlst);
	public List<ListSchedule> getSchedulers(Long project_id,List<Project> projlst);
	
	public List<ListTrigger> getValidationTriggersList(Long project_id,List<Project> projlst);
	public List<ListTrigger> getValidationTriggersListProjectName(Long project_id,List<Project> projlst);
	public List<ListTrigger> getSchemaTriggersList(Long project_id,List<Project> projlst);
	
	public SqlRowSet getTriggers(Long project_id);
	public void insertIntoSub_Task_Status(Long idApp);

	public void deleteFromScheduledTasks(Long id);
	public void deleteFromListSchedule(Long idSchedule);
	public boolean insertIntoRunningtaskStatus(String status, Long idApp);
	public boolean updateRunningtaskStatus(String status, Long idApp);
	public String getSchemaNameFolderNameListDataAccess(Long idData);
	public boolean recordExistInRunningtaskStatus(Long idApp);
	public String insertRunScheduledTask(Long idApp,String status, String deployMode, String triggeredBy, String validationRunType);
	public void updateRunScheduledTaskPid(Long idApp, long pid, String uniqueId);
	public String addIncrementalFileValidationToQueue(Long idApp, String incrementalFileName);
	// Template creation task progress
	public String getTemplateCreationJobStatusById(Long idData, String uniqueId);
	public String placeTemplateJobInQueue(Long idData, String templateRunType);
	public boolean updateTemplateCreationJobStatus(Long idData, String uniqueId, String status);
	public void updateTemplateCreationJobPid(long idData, String uniqueId, long pid);

	// Kill Running jobs 
	public List<RunningTaskDTO> getRunningTemplateJobList(List<Project> projList);
	public List<RunningTaskDTO> getRunningValidationJobList(List<Project> projList);
	public List<RunningTaskDTO> getQueuedTemplateJobList(List<Project> projList);
	public List<RunningTaskDTO> getQueuedValidationJobList(List<Project> projList);
	public void killRunScheduledTask(long taskId, String uniqueId);
	public void deleteValidationJobFromQueue(long taskId, String uniqueId);
	public void deleteTemplateJobFromQueue(long taskId, String uniqueId);
	public void deleteAllQueuedTemplateJobs();
	public void deleteAllQueuedValidationJobs();
	
	// Task Manager
	public int getRunningTasksCount();
	public List<RunningTaskDTO> getJobsInQueue();
	public String getDataLocationForIdData(long idData);

	public long insertIntolistAppGroup(String name, String description, String schedulerEnabled, Long idScheduler,
			Long projectId,String idAppList);
	public List<ListAppGroup> getAppGroupsForProject(Long projectId,List<Project> projlst);
	public List<AppGroupMapping> getApplicationMappingForGroup(Long idAppGroup);
	public void deleteAppGroupById(Long idAppGroup);
	public void deleteAppGroupMapping(Long idAppGroupMapping);
	public List<String> getApprovedValidationNamesListForProject(Long projectId);
	public ListAppGroup getListAppGroupById(Long idAppGroup);
	public boolean updateIntolistAppGroup(Long idAppGroup, String name, String description, String schedulerEnabled,
			Long idScheduler, Long projectId, String idAppList);
	public List<Long> getValidationsOfAppGroup(Long idAppGroup);
	
	public List<Long> getSchemasForSchedule(Long idScheduler);
	public SqlRowSet getValidationTriggers(Long Project_id,List<Project> projlst);
	public SqlRowSet getSchemaTriggers(Long Project_id,List<Project> projlst);

	public List<SchemaJobQueue> getSchemaJobsInQueue();
	public String addSchemaJobToQueue(long idDataSchema,String healthCheckFlag);
	public boolean updateSchemaJobRunStatus(long idDataSchema, String uniqueId, String status);
	public boolean checkIfSchemaJobInProgress(Long idDataSchema);
	public void deleteOldQueueEntriesAndAssociationsOfSchema(Long idDataSchema);
	public boolean checkIfSchemaJobInQueue(Long idDataSchema);
	public List<SchemaJobQueue> getAssociationsInProgressSchemaJobList();
	public List<Map<String,Object>> getAssociatedTemplatesForSchema(String uniqueId, Long idDataSchema);

	public List<RunningTaskDTO> getCompletedTemplateValidationJobList(List<Project> projList);
	public List<SchemaJobDTO> getSchemaJobsList(List<Project> projList);
	public List<RunningTaskDTO> getJobsUnderProcessing();
	public List<RunningTaskDTO> getSchemaJobTemplatesStatus(String uniqueId, Long idDataSchema);

	public boolean checkIfAppGroupJobInQueue(Long idAppGroup);
	public boolean checkIfAppGroupJobInProgress(Long idAppGroup);
	public String addAppGroupJobToQueue(Long idAppGroup);
	public boolean updateAppGroupJobRunStatus(long idAppGroup, String uniqueId, String status);
	public List<AppGroupJobQueue> getAssociationsInProgressAppGroupList();
	public List<Map<String, Object>> getAssociatedValidationsForAppGroupJob(String appGroupUniqueId, Long idAppGroup);
	public String getValidationJobStatusById(Long idApp, String validation_uniqueId);
	public List<AppGroupJobQueue> getAppGroupJobsInQueue();
	public List<AppGroupJobDTO> getAppGroupJobsList();
	public List<RunningTaskDTO> getAppGroupJobValidationsStatus(String uniqueId, Long idAppGroup);
	public List<Map<String, Object>> getAppGroupJobValidationsMaps(String uniqueId, Long idAppGroup);

	public boolean isApplicationInProgress(long idApp);
	public String getJobStatusByUniqueId(String uniqueId);
	public String getAppGroupJobStatusByUniqueId(String uniqueId);
	public List<AppGroupJobDTO> getAppGroupHistoryById(Long idAppGroup);
	public boolean checkIfValidationJobQueuedOrInProgress(Long idApp);

	public void updateSQSAlertSentStatus(long requestId, String sqsAlertSentStatus);
	public List<DataQualitySQSRequest> getDataQualitySQSRequestList();

	public List<DatabuckSNSRequest> getSNSMessageList();
	public void updateSNSAlertSentStatus(long requestId, String snsAlertSentStatus);

	public void deleteProcessedMessagesFromSQSQueue();
	public void deleteProcessedMessagesFromSNSQueue();

	public List<ValidationRunDTO> getValidationRunHistory(Long idApp);
	public String getSchemaJobStatusByUniqueId(String id);
	public List<SchemaJobDTO> getSchemaJobHistoryById(Long idDataSchema);

	public boolean isTemplateJobQueuedOrInProgress(Long templateId);
	public boolean startTemplateByUniqueId(Long idData, String uniqueId) throws TemplateTriggerFailedException;
	public boolean startValidationByUniqueId(Long idApp, String uniqueId) throws ValidationTriggerFailedException;
	public boolean startSchemaJobByUniqueId(long idDataSchema, String uniqueId) throws ConnectionTriggerFailedException;
	public boolean startAppGroupJobByUniqueId(long idAppGroup, String uniqueId) throws AppGroupTriggerFailedException;

	public boolean checkIfProjectJobInProgress(Long projectId);
	public String addProjectJobToQueue(long projectId);
	public String getProjectJobStatusByUniqueId(String uniqueId);
	public List<ProjectJobDTO> getProjectJobHistoryById(long projectId);
	public List<SchemaJobDTO> getProjectJobAssociatedConnections(String uniqueId, long projectId);
    public boolean isProjectUniqueIdValid(String uniqueId);
    public boolean checkIfProjectJobInQueue(Long idDataSchema);

	public List<ProjectJobQueue> getAssociationsInProgressProjectJobList();
	public List<Map<String, Object>> getAssociatedConnectionsForProject(String uniqueId, Long projectId);
	public boolean updateProjectJobRunStatus(Long projectId, String uniqueId, String status);
	public List<ProjectJobQueue> getProjectJobsInQueue();
	public void deleteOldQueueEntriesAndAssociationsOfProject(Long projectId);
	public boolean startProjectJobByUniqueId(long projectId, String uniqueId) throws ProjectTriggerFailedException;

	public void updateAppGroupJobPid(long idAppGroup, String uniqueId, long pid);
	public void updateSchemaJobPid(long idDataSchema, String uniqueId, long pid);
	public void updateProjectJobPid(long projectId, String uniqueId, long pid);
	public String getDomainNameById(Long domainId);
	public Map<String,Object> getRuleCheckDetails(long idApp, long dqrId);

	public ListAppGroup getListAppGroupByName(String appGroupName);
	
	public JSONObject getAlertNotificationDetailsByTopic(String sTopic);
	public NotificationTopics getNotificationInfoByTopic(String topic);
	
	public List<ProjectJobDTO> getProjectJobsList(List<Project> projList);

	public boolean updatePropertyValue(String propertyCategoryName, String propertyName, String propertyValue);

	public List<RunningTaskDTO> getJobsUnderProcessingForCurrentHost();

	public void addExternalAPIMsgToQueue(ExternalAPIAlertPOJO externalAPIAlertPOJO);

	public void updateAlertMsgDeliverStatusForMessage(String externalAPIType, long taskId, String taskType,
			String uniqueId, String alert_deliver_status);
	public long getDomainIdByProjectId(long projectId);
	
	public Domain getDomainDetailsById(Long domainId);
	public boolean checkIfDomainJobInProgress(Long domainId);
	public boolean checkIfDomainJobInQueue(Long domainId);
	public String addDomainJobToQueue(long domainId);
	public boolean isDomainUniqueIdValid(String uniqueId);
	public String getDomainJobStatusByUniqueId(String uniqueId);
	public String getJobStatusByIdapp(Long idApp);
	public List<ProjectJobDTO> getDomainJobAssociatedProjects(String uniqueId, long domainId);
	public List<RunningTaskDTO> getProjectJobSchemaStatus(String uniqueId, Long projectId);
	public List<DomainJobDTO> getDomainJobHistoryById(long domainId);

	public List<DomainJobQueue> getInProgressDomainJobList();
	public List<Map<String, Object>> getAssociatedProjectsForDomain(String uniqueId, Long domainId);
	public boolean updateDomainJobRunStatus(Long domainId, String uniqueId, String status);
	public List<DomainJobQueue> getDomainJobsInQueue();
	public boolean startDomainJobByUniqueId(long domainId, String uniqueId);
	public void updateDomainJobPid(long domainId, String uniqueId, long pid);
	public List<DomainJobDTO> getDomainJobsList();
	public List<RunningTaskDTO> getRunningTemplatesOfSchemaJob(Long idDataSchema, String uniqueId);
	public List<RunningTaskDTO> getRunningValidationsOfAppGroupJob(Long idAppGroup, String uniqueId);
	public List<RunningTaskDTO> getRunningSchemasOfProjectJob(Long projectId, String uniqueId);
	public List<RunningTaskDTO> getRunningProjectsOfDomainJob(Long domainId, String uniqueId);

	public boolean isAppGroupNameDuplicated(String name);
	
	public boolean duplicateschedulername(String schedulerName,long projectId);
	
	public boolean checkIfDomainLiteJobInProgress(Long domainId);
	public String addDomainLiteJobToQueue(long domainId);
	public boolean checkIfDomainLiteJobInQueue(Long domainId);
	public boolean isDomainLiteUniqueIdValid(String uniqueId);
	public String getDomainLiteJobStatusByUniqueId(String uniqueId);
	public String getDomainLiteJobResultByUniqueId(String uniqueId);
	public boolean updateDomainLiteJobRunStatus(Long domainId, String uniqueId, String status);
	public boolean updateDomainLite(Long domainId, String uniqueId, String result_json);
	public List<DomainLiteJobQueue> getDomainLiteJobsInQueue();
	public boolean startDomainLiteJobByUniqueId(long domainId, String uniqueId);
	public List<DomainLiteJobDTO> getDomainLiteJobHistoryById(long domainId);
	public String getTemplateRunJobStatusById(String uniqueId);
	public boolean isTemplateUniqueIdValid(String uniqueId);
	public RunningTaskDTO getValidationRunDetailsByUniqueId(String uniqueId);
	public RunningTaskDTO getSchemaRunDetailsByUniqueId(String uniqueId);
	public List<Long> getValidationsForTemplateSuccess(long idDataSchema,String schemaJobUniqueId);
	public List<JiraIntegrationBean> getNonSubmittedJiraTicketDetails();
	public boolean deleteAllPublishedJiraTickets();
	public boolean updateJiraTicketSubmitStatus(long id, String submitStatus);
	public boolean updateJiraTicketProcessStatus(long id, String submitStatus);
	public String getJobTemplateUniqueIdApi(String uniqueId);
	public String getJobStatusByUniqueIdApi(String uniqueId);

	public long getIdDataSchemaByIdData(long idData);
	public Properties getPropertiesFromDB(String propertyCategory);
	public  Map<String, String> getClusterCategoryNameBySchemaId(Long idDataSchema);
	public String getMaxUniqueIdByIdApp(Long idApp);
	public JiraIntegrationBean getJIRATicketDetailsByTaskUniqueId(String taskUniqueId);


	public Map<Long, String> getListScheduleDatawithDomain(long projectId, List<Project> projList, long domainId);


	public int insertIntolistSchedule(String name, String description, String frequency, String scheduledDay,
			String day, String scheduleTimer, long projectId, long domainId);


	public List<ListSchedule> getSchedulers(long projectId, List<Project> projList, long domainId);

	public void addAuditTrailDetail(Long userId, String userName,String moduleName, String date, Long entityId, String activityName,String entityName);
	
	public List<ListSchedule> getSchedulersForProjectId(long projectId, long domainId);

	public boolean enableSchedulingCheckAppGroup(long idSchedule);
	public boolean enableSchedulingCheckTrigger(long idSchedule);
	public List<LoggingActivity> getLatestAuditTrailDetails(String toDate, String fromDate);
	public int  clearLogs(String fromDate,String toDate);

	public List<ListTrigger> getSchemaTriggersListWithDomain(Long project_id, Long domainId);

	public String insertRunGlobalRuleValidateTask(String deployMode, String triggeredBy,long processId, JSONObject inputJson);
	public void updateRunGlobalRuleUpdateTask(String uniqueId,long processId);


	public String getclusterPropertyCategoryForApplicationId(long applicationId);

	public String insertRunGlobalFilterValidateTask(String deployMode, String triggeredBy, long processId, JSONObject inputJson);

	public void updateRunGlobalFilterUpdateTask(String uniqueId, long processId);
	
	public List<LoginTrail> getLatestLoginActivityDetails(String toDate, String fromDate);
	
}
