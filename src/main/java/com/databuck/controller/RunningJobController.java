package com.databuck.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.service.RunningJobService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.AppGroupJobDTO;
import com.databuck.bean.DomainJobDTO;
import com.databuck.bean.Project;
import com.databuck.bean.ProjectJobDTO;
import com.databuck.bean.RunningTaskDTO;
import com.databuck.bean.SchemaJobDTO;
import com.databuck.dao.ITaskDAO;

/**
 * This class is used for killing the long running jobs
 * 
 * @author Sreelakshmi
 *
 */
@RestController
public class RunningJobController {

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private RunningJobService runningJobService;

	@RequestMapping(value = "/runningJobsView")
	public ModelAndView getRunningJobs(ModelAndView model, HttpSession session) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		List<Project> projList = (List<Project>) session.getAttribute("userProjectList");

		List<RunningTaskDTO> runningAppsList = new ArrayList<RunningTaskDTO>();
		List<RunningTaskDTO> queuedJobslist = new ArrayList<RunningTaskDTO>();

		// Get the list of running Template Jobs list
		List<RunningTaskDTO> runningTemplateJobs = iTaskDAO.getRunningTemplateJobList(projList);

		if (runningTemplateJobs != null && runningTemplateJobs.size() > 0) {
			runningAppsList.addAll(runningTemplateJobs);
		}

		// Get the list of running Validation Jobs list
		List<RunningTaskDTO> runningValidations = iTaskDAO.getRunningValidationJobList(projList);

		if (runningValidations != null && runningValidations.size() > 0) {
			runningAppsList.addAll(runningValidations);
		}

		// Get the list of Template Jobs Queued
		List<RunningTaskDTO> queuedTemplateJobs = iTaskDAO.getQueuedTemplateJobList(projList);

		if (queuedTemplateJobs != null && queuedTemplateJobs.size() > 0) {
			queuedJobslist.addAll(queuedTemplateJobs);
		}

		// Get the list of Validations Jobs Queued
		List<RunningTaskDTO> queuedValidations = iTaskDAO.getQueuedValidationJobList(projList);

		if (queuedValidations != null && queuedValidations.size() > 0) {
			queuedJobslist.addAll(queuedValidations);
		}

		// Get the list of Template Jobs Completed/Failed/Killed
		List<RunningTaskDTO> completedAppsList = iTaskDAO.getCompletedTemplateValidationJobList(projList);

		// Get the list of Schema jobs
		List<SchemaJobDTO> schemaJobsList = iTaskDAO.getSchemaJobsList(projList);

		// Get the list of Project jobs
		List<ProjectJobDTO> projectJobsList = iTaskDAO.getProjectJobsList(projList);

		// Get the list of AppGroup Jobs
		List<AppGroupJobDTO> appGroupJobsList = iTaskDAO.getAppGroupJobsList();

		// Get the list of Domain Jobs
		List<DomainJobDTO> domainJobsList = iTaskDAO.getDomainJobsList();

		model.setViewName("runningJobsView");
		model.addObject("runningappslist", runningAppsList);
		model.addObject("queuedJobslist", queuedJobslist);
		model.addObject("completedAppsList", completedAppsList);
		model.addObject("schemaJobsList", schemaJobsList);
		model.addObject("appGroupJobsList", appGroupJobsList);
		model.addObject("projectJobsList", projectJobsList);
		model.addObject("domainJobsList", domainJobsList);
		model.addObject("currentSection", "Tasks");
		model.addObject("currentLink", "viewJobStatus");
		return model;
	}

	@RequestMapping(value = "/getAppGroupAssociatedValidations", method = RequestMethod.POST)
	public @ResponseBody List<RunningTaskDTO> getAppGroupAssociatedValidations(HttpServletResponse response,
			@RequestParam long idAppGroup, @RequestParam String uniqueId) {
		List<RunningTaskDTO> validationList = null;
		try {
			System.out.println("\n====> getAppGroupAssociatedValidations - START <====");
			validationList = iTaskDAO.getAppGroupJobValidationsStatus(uniqueId, idAppGroup);
			System.out.println("\n====> getAppGroupAssociatedValidations - END  <====");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validationList;
	}

	@RequestMapping(value = "/getSchemaJobAssociatedTemplates", method = RequestMethod.POST)
	public @ResponseBody List<RunningTaskDTO> getSchemaJobAssociatedTemplates(HttpServletResponse response,
			@RequestParam long idDataSchema, @RequestParam String uniqueId) {
		List<RunningTaskDTO> templateList = null;
		try {
			System.out.println("\n====> getSchemaJobAssociatedTemplates - START <====");
			templateList = iTaskDAO.getSchemaJobTemplatesStatus(uniqueId, idDataSchema);
			System.out.println("\n====> getSchemaJobAssociatedTemplates - END  <====");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateList;
	}

	@RequestMapping(value = "/getDomainJobAssociatedProjects", method = RequestMethod.POST)
	public @ResponseBody List<ProjectJobDTO> getDomainJobAssociatedProjects(HttpServletResponse response,
			@RequestParam long domainId, @RequestParam String uniqueId) {
		List<ProjectJobDTO> connectionList = null;
		try {
			System.out.println("\n====> getDomainJobAssociatedProjects - START <====");
			connectionList = iTaskDAO.getDomainJobAssociatedProjects(uniqueId, domainId);
			System.out.println("\n====> getDomainJobAssociatedProjects - END  <====");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectionList;
	}

	@RequestMapping(value = "/getProjectJobAssociatedConnections", method = RequestMethod.POST)
	public @ResponseBody List<SchemaJobDTO> getProjectJobAssociatedConnections(HttpServletResponse response,
			@RequestParam long projectId, @RequestParam String uniqueId) {
		List<SchemaJobDTO> connectionList = null;
		try {
			System.out.println("\n====> getProjectJobAssociatedConnections - START <====");
			connectionList = iTaskDAO.getProjectJobAssociatedConnections(uniqueId, projectId);
			System.out.println("\n====> getProjectJobAssociatedConnections - END  <====");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectionList;
	}


	@RequestMapping(value = "/deleteJobFromQueue", method = RequestMethod.POST)
	public void deleteJobFromQueue(HttpServletResponse response, @RequestParam long taskId,
			@RequestParam String taskType, @RequestParam String uniqueId) {
		try {
			System.out.println("\n====> DeleteJobFromQueue - START <====");
			System.out.println("TaskId: " + taskId);
			System.out.println("TaskType: " + taskType);

			if (taskType.trim().equalsIgnoreCase("validation")) {
				iTaskDAO.deleteValidationJobFromQueue(taskId, uniqueId);
			} else if (taskType.trim().equalsIgnoreCase("template")) {
				iTaskDAO.deleteTemplateJobFromQueue(taskId, uniqueId);
			}

			JSONObject json = new JSONObject();
			json.put("success", "success");
			response.getWriter().println(json);
			response.getWriter().flush();
			System.out.println("\n====> DeleteJobFromQueue - END  <====");
		} catch (Exception e) {
			e.printStackTrace();

			JSONObject json = new JSONObject();
			json.put("failure", "failure");
			json.put("message", "Error occurred!!");
			try {
				response.getWriter().println(json);
				response.getWriter().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/clearAllQueuedJobs", method = RequestMethod.GET)
	public void clearAllQueuedJobs(HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			System.out.println("\n====> clearAllQueuedJobs - START <====");
			iTaskDAO.deleteAllQueuedTemplateJobs();

			iTaskDAO.deleteAllQueuedValidationJobs();

			json.put("success", "success");
			System.out.println("\n====> clearAllQueuedJobs - END  <====");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("failure", "failure");
			json.put("message", "Error occurred!!");
		}

		try {
			response.getWriter().println(json);
			response.getWriter().flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value = "/stopRunningJob", method = RequestMethod.POST)
	public void stopRunningJob(HttpServletResponse response, @RequestParam long taskId, @RequestParam String taskType,
			@RequestParam long processId, @RequestParam String deployMode, @RequestParam String sparkAppId,
			@RequestParam String uniqueId) {
		try {
			System.out.println("\n====> Stopping the running Job <====");
			System.out.println("TaskId: " + taskId);
			System.out.println("TaskType: " + taskType);
			System.out.println("SparkAppId: " + sparkAppId);
			System.out.println("UniqueId:" + uniqueId);
			System.out.println("DeployMode:" + deployMode);
			System.out.println("ProcessId:" + processId);

			JSONObject json = new JSONObject();
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
				System.out.println("\n====>Unable to kill the job, UniqueId is missing!!");
			}

			if (status)
				json.put("success", "success");
			else
				json.put("failure", "Failed to kill the job");
			response.getWriter().println(json);
			response.getWriter().flush();

		} catch (Exception e) {
			e.printStackTrace();

			JSONObject json = new JSONObject();
			json.put("failure", "failure");
			json.put("message", "Error occurred!!");
			try {
				response.getWriter().println(json);
				response.getWriter().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
