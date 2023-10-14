package com.databuck.service;

import com.databuck.bean.RunningTaskDTO;
import com.databuck.dao.ITaskDAO;
import com.databuck.taskmanager.TaskManagerService;

import com.databuck.util.DatabuckUtility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class RunningJobService {

	@Autowired
	private TaskManagerService taskManagerService;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private ITaskDAO iTaskDAO;
	
	private static final Logger LOG = Logger.getLogger(RunningJobService.class);

	public boolean killTemplateOrValidationJob(long taskId, String taskType, long processId, String deployMode,
			String sparkAppId, String uniqueId) {
		boolean status = false;
		try {
			// Trigger kill Job
			if(clusterProperties.getProperty("deploymentMode").trim().equalsIgnoreCase("azure") && clusterProperties.getProperty("databricksCluster").trim().equalsIgnoreCase("N")){
				System.out.println("\n====>Running KillJob for Synapse");
				status = killAzureJob(processId);
			}else {
				status = taskManagerService.triggerKillJobScript(taskId, taskType, processId, deployMode, sparkAppId,
						uniqueId);
			}

			if (status) {
				// Update the status to killed
				if (taskType.trim().equalsIgnoreCase("validation")) {

					LOG.info("\n====>Updating validation job status to killed !!");
					iTaskDAO.killRunScheduledTask(taskId, uniqueId);

				} else if (taskType.trim().equalsIgnoreCase("template")) {

					LOG.info("\n====>Updating template job status to killed !!");
					iTaskDAO.updateTemplateCreationJobStatus(taskId, uniqueId, "killed");
				}
			}
		} catch (Exception e) {
			LOG.error("\n====> Exception occurred while killing [" + taskType + "] job");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean killSchemaJob(long idDataSchema, String uniqueId, long processId, String sparkAppId,
			String deployMode) {
		LOG.debug("\n====> Identifying and killing running template jobs under schema job with idDataSchema:["
				+ idDataSchema + "] for uniqueId:[" + uniqueId + "]");
		boolean status = false;
		try {

			if (processId > 0l)
				// kill current job
				taskManagerService.triggerKillJobScript(idDataSchema, "connection", processId, deployMode, sparkAppId,
						uniqueId);

			// Update status
			status = iTaskDAO.updateSchemaJobRunStatus(idDataSchema, uniqueId, "killed");

			// Fetching running template jobs
			List<RunningTaskDTO> templateList = iTaskDAO.getRunningTemplatesOfSchemaJob(idDataSchema, uniqueId);
			if (templateList != null && templateList.size() > 0) {
				LOG.debug("\n====>Number of running template jobs found:" + templateList.size());

				for (RunningTaskDTO runningTaskDTO : templateList) {
					long templateId = runningTaskDTO.getApplicationId();
					String template_uniqueId = runningTaskDTO.getUniqueId();

					LOG.debug("\n====>Killing the Template job with Id:[" + templateId
							+ "] and with uniqueId:[" + template_uniqueId + "]");

					status = taskManagerService.triggerKillJobScript(templateId, runningTaskDTO.getTaskType().trim(),
							runningTaskDTO.getProcessId(), runningTaskDTO.getDeployMode(),
							runningTaskDTO.getSparkAppId(), template_uniqueId);

					LOG.debug("\n====>Updating the status to 'killed' for Template job with Id:[" + templateId
							+ "] and with uniqueId:[" + template_uniqueId + "]");
					iTaskDAO.updateTemplateCreationJobStatus(templateId, template_uniqueId, "killed");

				}
			} else
				LOG.debug("\n====>No running template jobs found for schema Id:[" + idDataSchema
						+ "] with uniqueId:[" + uniqueId + "]");

			LOG.debug("\n====>Updating schema job status as 'killed' for idDataSchema:[" + idDataSchema
					+ "] with uniqueId:[" + uniqueId + "]");
			status = iTaskDAO.updateSchemaJobRunStatus(idDataSchema, uniqueId, "killed");

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean killAppGroupJob(long idAppGroup, String uniqueId) {
		LOG.debug("\n====> Identifying and killing running validation jobs under App Group job with idAppGroup:["
						+ idAppGroup + "] for uniqueId:[" + uniqueId + "]");
		boolean status = false;

		try {
			// Fetching running validation jobs
			List<RunningTaskDTO> validationsList = iTaskDAO.getRunningValidationsOfAppGroupJob(idAppGroup, uniqueId);
			if (validationsList != null && validationsList.size() > 0) {
				LOG.debug("\n====>Number of running validation jobs found:" + validationsList.size());

				for (RunningTaskDTO runningTaskDTO : validationsList) {
					long validationId = runningTaskDTO.getApplicationId();
					String validaiton_uniqueId = runningTaskDTO.getUniqueId();

					LOG.debug("\n====>Killing the Validation job with Id:[" + validationId
							+ "] and with uniqueId:[" + validaiton_uniqueId + "]");

					status = taskManagerService.triggerKillJobScript(validationId, runningTaskDTO.getTaskType().trim(),
							runningTaskDTO.getProcessId(), runningTaskDTO.getDeployMode(),
							runningTaskDTO.getSparkAppId(), validaiton_uniqueId);

					LOG.debug("\n====>Updating the status to 'killed' for Validation job with Id:["
							+ validationId + "] and with uniqueId:[" + validaiton_uniqueId + "]");
					iTaskDAO.updateRunScheduledTask(validationId, "killed", validaiton_uniqueId);
				}
			} else
				LOG.debug("\n====>No running validation jobs found for App Group Id:[" + idAppGroup
						+ "] with uniqueId:[" + uniqueId + "]");

			LOG.debug("\n====>Updating App Group job status as 'killed' for idAppGroup:[" + idAppGroup
					+ "] with uniqueId:[" + uniqueId + "]");
			status = iTaskDAO.updateAppGroupJobRunStatus(idAppGroup, uniqueId, "killed");
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean killProjectJob(long projectId, String uniqueId, long processId, String sparkAppId,
			String deployMode) {
		LOG.debug("\n====> Identifying and killing running schema jobs under project job with Project Id:["
				+ projectId + "] for uniqueId:[" + uniqueId + "]");
		boolean status = false;
		try {

			if (processId > 0l)
				// kill current job
				taskManagerService.triggerKillJobScript(projectId, "project", processId, deployMode, sparkAppId,
						uniqueId);

			// Update status
			status = iTaskDAO.updateProjectJobRunStatus(projectId, uniqueId, "killed");

			// Fetching running schema jobs
			List<RunningTaskDTO> schemaList = iTaskDAO.getRunningSchemasOfProjectJob(projectId, uniqueId);
			if (schemaList != null && schemaList.size() > 0) {
				LOG.debug("\n====>Number of running schema jobs found:" + schemaList.size());

				// Killing Schema jobs
				for (RunningTaskDTO runningTaskDTO : schemaList) {

					LOG.debug("\n====>Killing the Schema job with Id:[" + runningTaskDTO.getApplicationId()
							+ "] and with uniqueId:[" + runningTaskDTO.getUniqueId() + "]");

					status = killSchemaJob(runningTaskDTO.getApplicationId(), runningTaskDTO.getUniqueId(),
							runningTaskDTO.getProcessId(), runningTaskDTO.getSparkAppId(),
							runningTaskDTO.getDeployMode());
				}
			} else
				LOG.debug("\n====>No running schema jobs found for Project Id:[" + projectId
						+ "] with uniqueId:[" + uniqueId + "]");

			LOG.debug("\n====>Updating Project job status as 'killed' for Project Id:[" + projectId
					+ "] with uniqueId:[" + uniqueId + "]");
			status = iTaskDAO.updateProjectJobRunStatus(projectId, uniqueId, "killed");
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean killDomainJob(long domainId, String uniqueId, long processId, String sparkAppId, String deployMode) {
		LOG.debug("\n====> Identifying and killing running project jobs under domain job with Domain Id:["
				+ domainId + "] for uniqueId:[" + uniqueId + "]");
		boolean status = false;
		try {
			if (processId > 0l)
				// kill current job
				taskManagerService.triggerKillJobScript(domainId, "domain", processId, deployMode, sparkAppId,
						uniqueId);

			// update status
			status = iTaskDAO.updateDomainJobRunStatus(domainId, uniqueId, "killed");

			// Fetching running project jobs
			List<RunningTaskDTO> projectList = iTaskDAO.getRunningProjectsOfDomainJob(domainId, uniqueId);
			if (projectList != null && projectList.size() > 0) {

				LOG.debug("\n====>Number of running project jobs found:" + projectList.size());

				// Killing Project Jobs
				for (RunningTaskDTO runningTaskDTO : projectList) {
					LOG.debug("\n====>Killing the Project job with Id:[" + runningTaskDTO.getApplicationId()
							+ "] and with uniqueId:[" + runningTaskDTO.getUniqueId() + "]");

					status = killProjectJob(runningTaskDTO.getApplicationId(), runningTaskDTO.getUniqueId(),
							runningTaskDTO.getProcessId(), runningTaskDTO.getSparkAppId(),
							runningTaskDTO.getDeployMode());
				}
			} else
				LOG.debug("\n====>No running project jobs found for Domain Id:[" + domainId
						+ "] with uniqueId:[" + uniqueId + "]");

			LOG.debug("\n====>Updating Domain job status as 'killed' for Domain Id:[" + domainId
					+ "] with uniqueId:[" + uniqueId + "]");
			status = iTaskDAO.updateDomainJobRunStatus(domainId, uniqueId, "killed");
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public boolean killAzureJob(long levyId){
		try{
			String databuckHome = DatabuckUtility.getDatabuckHome();

			databuckHome = databuckHome.replace("\\", "/");
			System.out.println("=========runId :"+ levyId);
			String cmd = databuckHome + "/scripts/stopTask_azure.sh " +  levyId;
			System.out.println("\n====>Kill cmd: " + cmd);

			System.out.println("\n====>Executing cmd ...");
			Process process = Runtime.getRuntime().exec(cmd);

			System.out.println("\n====> Waiting for the script execution to complete ..");
			while (process.isAlive()) {
			}
			if (process.exitValue() == 0) {
				System.out.println("Job Killed Successfully");
				return true;
			}
			else
				System.out.println("Couldnt kill the job");
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
