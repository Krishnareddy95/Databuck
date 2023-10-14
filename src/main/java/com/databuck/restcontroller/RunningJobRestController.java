package com.databuck.restcontroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.AppGroupJobDTO;
import com.databuck.bean.DomainJobDTO;
import com.databuck.bean.Project;
import com.databuck.bean.ProjectJobDTO;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobDTO;
import com.databuck.csvmodel.NullCheck;
import com.databuck.dao.ITaskDAO;
import com.databuck.service.RunningJobService;
import com.databuck.util.TokenValidator;

@CrossOrigin(origins = "*")
@RestController
public class RunningJobRestController {

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private RunningJobService runningJobService;

	@Autowired
	private TokenValidator tokenValidator;
	
	private static final Logger LOG = Logger.getLogger(RunningJobRestController.class);

	@RequestMapping(value = "/dbconsole/runningJobsView")
	public ResponseEntity<Object> getRunningJobs(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/runningJobsView - START");
		JSONObject response = new JSONObject();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("projectIds")) {
			response.put("message", "Project ids are missing in request.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		String[] projIds = params.get("projectIds").split(",");
		if (!(projIds.length > 0)) {
			response.put("message", "Project ids are missing in request.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		List<Project> projList = new ArrayList<>();
		for (int i = 0; i < projIds.length; i++) {
			Project p = new Project();
			p.setIdProject(Long.parseLong(projIds[i]));
			projList.add(p);
		}
		String tabName = params.get("tabName");
		try {
			if (tabName.equalsIgnoreCase("RunningJobs")) {
				List<RunningTaskDTO> runningAppsList = new ArrayList<RunningTaskDTO>();
				List<RunningTaskDTO> runningTemplateJobs = iTaskDAO.getRunningTemplateJobList(projList);
				if (runningTemplateJobs != null && runningTemplateJobs.size() > 0) {
					runningAppsList.addAll(runningTemplateJobs);
				}
				List<RunningTaskDTO> runningValidations = iTaskDAO.getRunningValidationJobList(projList);
				if (runningValidations != null && runningValidations.size() > 0) {
					runningAppsList.addAll(runningValidations);
				}
				response.put("result", runningAppsList);
			} else if (tabName.equalsIgnoreCase("QueuedJobs")) {
				List<RunningTaskDTO> queuedJobslist = new ArrayList<RunningTaskDTO>();
				List<RunningTaskDTO> queuedTemplateJobs = iTaskDAO.getQueuedTemplateJobList(projList);
				if (queuedTemplateJobs != null && queuedTemplateJobs.size() > 0) {
					queuedJobslist.addAll(queuedTemplateJobs);
				}
				List<RunningTaskDTO> queuedValidations = iTaskDAO.getQueuedValidationJobList(projList);
				if (queuedValidations != null && queuedValidations.size() > 0) {
					queuedJobslist.addAll(queuedValidations);
				}
				response.put("result", queuedJobslist);
			} else if (tabName.equalsIgnoreCase("CompletedJobs")) {
				List<RunningTaskDTO> completedAppsList = iTaskDAO.getCompletedTemplateValidationJobList(projList);
				response.put("result", completedAppsList);
			} else if (tabName.equalsIgnoreCase("SchemaJobs")) {
				List<SchemaJobDTO> schemaJobsList = iTaskDAO.getSchemaJobsList(projList);
				response.put("result", schemaJobsList);
			} else if (tabName.equalsIgnoreCase("AppGroupJobs")) {
				List<AppGroupJobDTO> appGroupJobsList = iTaskDAO.getAppGroupJobsList();
				response.put("result", appGroupJobsList);
			} else if (tabName.equalsIgnoreCase("ProjectJobs")) {
				List<ProjectJobDTO> projectJobsList = iTaskDAO.getProjectJobsList(projList);
				response.put("result", projectJobsList);
			} else if (tabName.equalsIgnoreCase("DomainJobs")) {
				List<DomainJobDTO> domainJobsList = iTaskDAO.getDomainJobsList();
				response.put("result", domainJobsList);
			} else {
				response.put("result", new ArrayList<>());
			}
			response.put("status", "Success");
			response.put("message", "Got running job list Successfully");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("status", "Failed");
			response.put("message", "Failed to Get running job list.");
			LOG.info("dbconsole/runningJobsView - END");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/runningJobsCSV")
	public void getRunningJobsCSV(@RequestHeader HttpHeaders headers, @RequestBody Map<String, String> params,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/runningJobsCSV - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token == null || token.isEmpty()) {
				throw new Exception("Token is missing in headers.");
			}
			if (!tokenValidator.isValid(token)) {
				throw new Exception("Token is expired.");
			}
			if (!params.containsKey("projectIds")) {
				throw new Exception("Project ids are missing in request.");
			}
			String[] projIds = params.get("projectIds").split(",");
			if (!(projIds.length > 0)) {
				throw new Exception("Token is expired.");
			}
			List<Project> projList = new ArrayList<>();
			for (int i = 0; i < projIds.length; i++) {
				Project p = new Project();
				p.setIdProject(Long.parseLong(projIds[i]));
				projList.add(p);
			}
			LOG.debug("Getting request parameters  " + params);
			String tabName = params.get("tabName");
			httpResponse.setContentType("text/csv");
			String csvFileName = "RunningJob" + LocalDateTime.now() + ".csv";
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
			httpResponse.setHeader(headerKey, headerValue);
			ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
			List<?> jobs = new ArrayList<>();
			if (tabName.equalsIgnoreCase("RunningJobs")) {
				LOG.info("In RunningJobs");
				List<RunningTaskDTO> runningAppsList = new ArrayList<RunningTaskDTO>();
				List<RunningTaskDTO> runningTemplateJobs = iTaskDAO.getRunningTemplateJobList(projList);
				if (runningTemplateJobs != null && runningTemplateJobs.size() > 0) {
					runningAppsList.addAll(runningTemplateJobs);
				}
				List<RunningTaskDTO> runningValidations = iTaskDAO.getRunningValidationJobList(projList);
				if (runningValidations != null && runningValidations.size() > 0) {
					runningAppsList.addAll(runningValidations);
				}
				
				jobs = runningAppsList;
			} else if (tabName.equalsIgnoreCase("QueuedJobs")) {
				List<RunningTaskDTO> queuedJobslist = new ArrayList<RunningTaskDTO>();
				List<RunningTaskDTO> queuedTemplateJobs = iTaskDAO.getQueuedTemplateJobList(projList);
				if (queuedTemplateJobs != null && queuedTemplateJobs.size() > 0) {
					queuedJobslist.addAll(queuedTemplateJobs);
				}
				List<RunningTaskDTO> queuedValidations = iTaskDAO.getQueuedValidationJobList(projList);
				if (queuedValidations != null && queuedValidations.size() > 0) {
					queuedJobslist.addAll(queuedValidations);
				}
				jobs = queuedJobslist;
			} else if (tabName.equalsIgnoreCase("CompletedJobs")) {
				jobs = iTaskDAO.getCompletedTemplateValidationJobList(projList);
			} else if (tabName.equalsIgnoreCase("SchemaJobs")) {
				jobs = iTaskDAO.getSchemaJobsList(projList);
			} else if (tabName.equalsIgnoreCase("AppGroupJobs")) {
				jobs = iTaskDAO.getAppGroupJobsList();
			} else if (tabName.equalsIgnoreCase("ProjectJobs")) {
				jobs = iTaskDAO.getProjectJobsList(projList);
			} else if (tabName.equalsIgnoreCase("DomainJobs")) {
				jobs = iTaskDAO.getDomainJobsList();
			} else {
				throw new Exception("Tab name is not matching.");
			}
			csvWriter.writeHeader(getHeaders(tabName));
			String[] fields = getFields(tabName);
			for (Object job : jobs) {
				csvWriter.write(job, fields);
			}
			csvWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		LOG.info("dbconsole/runningJobsCSV - END");
	}

	private String[] getFields(String tabName) {

		switch (tabName) {
		case "SchemaJobs": {
			String[] fields = { "idDataSchema", "schemaName", "uniqueId", "status", "deployMode", "processId",
					"sparkAppId", "projectName", "createdAt", "startTime", "endTime", "fullDuration",
					"triggeredByHost" };
			return fields;
		}
		case "DomainJobs": {
			String[] fields = { "domainId", "domainName", "status", "deployMode", "processId", "sparkAppId",
					"createdAt", "startTime", "endTime", "fullDuration", "triggeredByHost" };
			return fields;
		}
		case "ProjectJobs": {
			String[] fields = { "projectId", "projectName", "status", "deployMode", "processId", "sparkAppId",
					"projectName", "createdAt", "startTime", "endTime", "fullDuration", "triggeredByHost" };
			return fields;
		}
		case "AppGroupJobs": {
			String[] fields = { "idAppGroup", "appGroupName", "uniqueId", "status", "deployMode", "processId",
					"sparkAppId", "createdAt", "startTime", "endTime", "fullDuration", "triggeredByHost" };
			return fields;
		}
		case "CompletedJobs": {
			String[] fields = { "taskType", "applicationId", "applicationName", "uniqueId", "status", "deployMode",
					"processId", "sparkAppId", "triggeredByHost", "projectName", "startTime", "endTime",
					"fullDuration" };
			return fields;
		}
		case "RunningApps": {
			String[] fields = { "taskType", "applicationId", "applicationName", "status", "deployMode", "uniqueId",
					"processId", "sparkAppId", "projectName", "startTime", "fullDuration", "triggeredByHost" };
			return fields;
		}
		case "QueuedJobs": {
			String[] fields = { "taskType", "applicationId", "applicationName", "uniqueId", "status", "projectName",
					"startTime", "fullDuration" };
			return fields;
		}
		case "RunningJobs": {
			String[] fields = { "taskType", "applicationId", "applicationName", "status","deployMode", "uniqueId",
					"startTime", "fullDuration" };
			return fields;
		}
		}
		return null;
	}

	private String[] getHeaders(String tabName) {

		switch (tabName) {
		case "SchemaJobs": {
			String[] header = { "Schema Id", "Schema Name", "Unique Id", "Status", "Deploy Mode", "Process Id",
					"Spark App Id", "Project Name", "Created At", "Start Time", "End Time", "Duration",
					"Triggered By Host" };
			return header;
		}
		case "DomainJobs": {
			String[] header = { "Domain Id", "Domain Name", "Status", "Deploy Mode", "Process Id", "Spark App Id",
					"Created At", "Start Time", "End Time", "Duration", "Triggered By Host" };
			return header;
		}
		case "ProjectJobs": {
			String[] header = { "Project Id", "Project Name", "Status", "Deploy Mode", "Process Id", "Spark App Id",
					"Project Name", "Created At", "Start Time", "End Time", "Duration", "Triggered By Host" };
			return header;
		}
		case "AppGroupJobs": {
			String[] header = { "AppGroup Id", "AppGroup Name", "Unique Id", "Status", "Deploy Mode", "Process Id",
					"Spark App Id", "Created At", "Start Time", "End Time", "Duration", "Triggered By Host" };
			return header;
		}
		case "RunningApps": {
			String[] header = { "Task Type", "Validation Id/ Template Id", "Name", "Status", "Deploy Mode", "Unique Id",
					"Process Id", "Spark App Id", "Project Name", "Start Time", "Duration", "Triggered By Host" };
			return header;
		}
		case "CompletedJobs": {
			String[] header = { "Task Type", "Validation Id/ Template Id", "Name", "Unique Id", "Status", "Deploy Mode",
					"Process Id", "Spark App Id", "Triggered By Host", "Project Name", "Start Time", "End Time",
					"Duration" };
			return header;
		}

		case "QueuedJobs": {
			String[] header = { "Task Type", "Validation Id/ Template Id", "Name", "Status", "Deploy Mode", "Unique ID", "Start Time",
				 "Duration" ,"Overtime Job"};
			return header;
		}
		
		case "RunningJobs": {
			String[] header = { "Task Type", "Validation Id/ Template Id", "Name", "Status", "Deploy Mode", "Unique Id",
					"Start Time", "Duration" };
			return header;
		}
		}
		return null;
	}

	@RequestMapping(value = "/dbconsole/deleteJobFromQueue", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteJobFromQueue(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/deleteJobFromQueue - START");
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("status", "failed");
			String token = null;
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (token == null || token.isEmpty()) {
				response.put("message", "Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!tokenValidator.isValid(token)) {
				response.put("message", "Token is expired.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if (!params.containsKey("taskId") && !params.containsKey("taskType") && !params.containsKey("uniqueId")) {
				response.put("message", "idData is missing in request parameters.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			LOG.debug("Getting request parameters  " + params);
			String taskType = params.get("taskType").toString();
			String uniqueId = params.get("uniqueId").toString();
			long taskId = Long.parseLong(params.get("taskId").toString());
			if (taskType.trim().equalsIgnoreCase("validation")) {
				iTaskDAO.deleteValidationJobFromQueue(taskId, uniqueId);
			} else if (taskType.trim().equalsIgnoreCase("template")) {
				iTaskDAO.deleteTemplateJobFromQueue(taskId, uniqueId);
			}
			response.put("message", "Successfully deleted job from the queue");
			response.put("status", "success");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("failure", "failure");
			response.put("message", "Error occurred!!");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteJobFromQueue - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/clearAllQueuedJobs", method = RequestMethod.POST)
	public ResponseEntity<Object> clearAllQueuedJobs(@RequestHeader HttpHeaders headers) {
		Map<String, Object> response = new HashMap<>();
		LOG.info("dbconsole/clearAllQueuedJobs - START");
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			iTaskDAO.deleteAllQueuedTemplateJobs();
			iTaskDAO.deleteAllQueuedValidationJobs();
			response.put("status", "success");
			response.put("message", "cleared all queued jobs successfully");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("status", "failed");
			response.put("message", "Error occurred!!");
			LOG.info("dbconsole/clearAllQueuedJobs - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/stopRunningJob", method = RequestMethod.POST)
	public ResponseEntity<Object> stopRunningJob(@RequestBody Map<String, Object> params,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/stopRunningJob - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			long taskId = Long.parseLong(String.valueOf(params.get("taskId")));
			String taskType = String.valueOf(params.get("taskType"));
			long processId = Long.parseLong(String.valueOf(params.get("processId")));
			String deployMode = String.valueOf(params.get("deployMode"));
			String sparkAppId = String.valueOf(params.get("sparkAppId"));
			String uniqueId = String.valueOf(params.get("uniqueId"));

			 LOG.info("\n====> Stopping the running Job <====");
			LOG.debug("TaskId: " + taskId);
			LOG.debug("TaskType: " + taskType);
			LOG.debug("SparkAppId: " + sparkAppId);
			LOG.debug("UniqueId:" + uniqueId);
			LOG.debug("DeployMode:" + deployMode);
			LOG.debug("ProcessId:" + processId);

			boolean status = false;

			if (uniqueId != null && !uniqueId.isEmpty()) {

				if (taskType.equalsIgnoreCase("connection"))
					status = runningJobService.killSchemaJob(taskId, uniqueId, processId, sparkAppId, deployMode);

				else if (taskType.equalsIgnoreCase("appgroup"))
					status = runningJobService.killAppGroupJob(taskId, uniqueId);

				else if (taskType.equalsIgnoreCase("project"))
					status = runningJobService.killProjectJob(taskId, uniqueId, processId, sparkAppId, deployMode);

				else if (taskType.equalsIgnoreCase("domain"))
					status = runningJobService.killDomainJob(taskId, uniqueId, processId, sparkAppId, deployMode);

				else if (taskType.equalsIgnoreCase("template") || taskType.equalsIgnoreCase("validation"))
					status = runningJobService.killTemplateOrValidationJob(taskId, taskType, processId, deployMode,
							sparkAppId, uniqueId);

			} else {
				LOG.info("\n====>Unable to kill the job, UniqueId is missing!!");
			}

			if (status) {
				response.put("status", "success");
				response.put("message", "Successfully killed the job.");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("message", "Failed to kill the job");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			response.put("status", "failed");
			response.put("message", "Error occurred!!");
			LOG.info("dbconsole/stopRunningJob - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getSchemaJobAssociatedTemplates", method = RequestMethod.POST)
	public ResponseEntity<Object> getSchemaJobAssociatedTemplates(@RequestBody Map<String, Object> params,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getSchemaJobAssociatedTemplates - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			long idDataSchema = Long.parseLong(String.valueOf(params.get("idDataSchema")));
			String uniqueId = String.valueOf(params.get("uniqueId"));
			response.put("result", iTaskDAO.getSchemaJobTemplatesStatus(uniqueId, idDataSchema));
			response.put("status", "success");
			response.put("message", "Records found successfully");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			response.put("message", "Failed to get records");
			LOG.info("dbconsole/getSchemaJobAssociatedTemplates - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/getAppGroupAssociatedValidations", method = RequestMethod.POST)
	public ResponseEntity<Object> getAppGroupAssociatedValidations(@RequestBody Map<String, Object> params,
			@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getAppGroupAssociatedValidations - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		long idAppGroup = Long.parseLong(String.valueOf(params.get("idAppGroup")));
		String uniqueId = String.valueOf(params.get("uniqueId"));
		try {
			response.put("result", iTaskDAO.getAppGroupJobValidationsMaps(uniqueId, idAppGroup));
			response.put("status", "success");
			response.put("message", "Records found successfully");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			response.put("message", "Failed to get records");
			LOG.info("dbconsole/getAppGroupAssociatedValidations - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}
}
