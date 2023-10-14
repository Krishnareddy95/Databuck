package com.databuck.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.constants.DatabuckConstants;
import com.databuck.service.DataConnectionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.AppGroupMapping;
import com.databuck.bean.DATA_QUALITY_INDEX;
import com.databuck.bean.ListAppGroup;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListSchedule;
import com.databuck.bean.Project;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IResultsDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.ITaskService;
import com.databuck.service.RBACController;

@Controller
public class TaskController {

	@Autowired
	private ITaskDAO iTaskDAO;
	@Autowired
	public ITaskService iTaskService;
	@Autowired
	private RBACController rbacController;
	@Autowired
	public IResultsDAO iResultsDAO;
	@Autowired
	IValidationCheckDAO validationcheckdao;
	@Autowired
	private Properties clusterProperties;
	@Autowired
	private SchemaDAOI schemaDao;
	@Autowired
	private IListDataSourceDAO listdatasourcedao;
	@Autowired
	private IUserDAO IUserdAO ;
	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private DataConnectionService dataConnectionService;
	
	@RequestMapping(value = "/viewSchedules", method = RequestMethod.GET)
	public ModelAndView viewSchedules(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {
			Long projectId= (Long)session.getAttribute("projectId");
			List<Project> projlst =  (List<Project>)session.getAttribute("userProjectList");
			List<ListSchedule> ListScheduleData = iTaskDAO.getSchedulers(projectId,projlst);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listScheduleData", ListScheduleData);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "viewSchedules");
			modelAndView.setViewName("viewSchedulers");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteTrigger", method = RequestMethod.GET)
	public ModelAndView deleteTrigger(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "D", session);
		if (rbac) {
			Long id = Long.parseLong(request.getParameter("id"));
			iTaskDAO.deleteFromScheduledTasks(id);
			System.out.println("id=" + id);
			ModelAndView modelAndView = new ModelAndView("Data Template Created Successfully");
			modelAndView.addObject("message", "Trigger Deleted Successfully");
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "viewTriggers");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteSchedule", method = RequestMethod.GET)
	public ModelAndView deleteSchedule(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "D", session);
		if (rbac) {
			Long idSchedule = Long.parseLong(request.getParameter("idSchedule"));
			iTaskDAO.deleteFromListSchedule(idSchedule);
			System.out.println("idSchedule=" + idSchedule);
			ModelAndView modelAndView = new ModelAndView("Data Template Created Successfully");
			modelAndView.addObject("message", "Scheduler Deleted Successfully");
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "viewSchedules");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/viewTriggers", method = RequestMethod.GET)
	public ModelAndView viewTriggers(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {
			Long projectId= (Long)session.getAttribute("projectId");
			List<Project> projlst =  (List<Project>)session.getAttribute("userProjectList");
			
			SqlRowSet validationTriggerData = iTaskDAO.getValidationTriggers(projectId,projlst);
			SqlRowSet schemaTriggerData = iTaskDAO.getSchemaTriggers(projectId,projlst);
			
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("validationTriggerData", validationTriggerData);
			modelAndView.addObject("schemaTriggerData", schemaTriggerData);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "viewTriggers");
			modelAndView.setViewName("viewTriggers");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/triggerTask", method = RequestMethod.GET)
	public ModelAndView triggerTask(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {

			Map<Long, String> listScheduleData = iTaskDAO.getListScheduleData(projectId,projList);

			// Get the List of Applications associated with Project
			List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId,projList);
			String idAppArray = "";
			if(listApplicationsdata!=null) {
				for(ListApplications listApplications :listApplicationsdata) {
					idAppArray = idAppArray + listApplications.getIdApp()+",";
				}
			}
			
			if(idAppArray.endsWith(",")) {
				idAppArray = idAppArray.substring(0,idAppArray.length()-1);
			}
			
			// Get the List of Schemas associated with Project
			
			List<ListDataSchema> listSchemaData = listdatasourcedao.getListDataSchema(projectId,projList,"","");
			String idDataSchemaArray = "";
			if(listSchemaData!=null) {
				for(ListDataSchema listDataSchema :listSchemaData) {
					idDataSchemaArray = idDataSchemaArray + listDataSchema.getIdDataSchema()+",";
				}
			}
			
			if(idDataSchemaArray.endsWith(",")) {
				idDataSchemaArray = idDataSchemaArray.substring(0,idDataSchemaArray.length()-1);
			}
			

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listScheduleData", listScheduleData);
			modelAndView.addObject("listApplicationsdata", listApplicationsdata);
			modelAndView.addObject("listSchemaData", listSchemaData);
			modelAndView.addObject("idDataSchemaArray", idDataSchemaArray);
			modelAndView.addObject("idAppArray", idAppArray);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Trigger Task");
			modelAndView.setViewName("triggerTask");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/scheduledTask", method = RequestMethod.GET)
	public ModelAndView scheduledTask(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {

			List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId,projList);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listApplicationsdata", listApplicationsdata);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Scheduled Task");
			modelAndView.setViewName("scheduledTask");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/triggerTaskSchedule", method = RequestMethod.POST)
	public void triggerTask(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String idApp,  @RequestParam String idDataSchema, @RequestParam Long idScheduler) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("idScheduler=" + idScheduler);
			System.out.println("idDataSchema=" + idDataSchema);
			System.out.println("idApp=" + idApp);
			
			Long projectId = (Long) session.getAttribute("projectId");

			int jobsCount = 0;
			int insertintoscheduledtasks = 0;

			if (idApp != null && !idApp.trim().isEmpty()) {
				String[] idAppList = idApp.trim().split(",");
				
				if (idAppList != null && idAppList.length > 0) {
					jobsCount = idAppList.length;
					
					for (String s_idApp : idAppList) {
						long l_idApp = Long.parseLong(s_idApp);
						int insertCount = iTaskDAO.insertintoscheduledtasks(l_idApp, null, idScheduler, projectId);
						insertintoscheduledtasks += insertCount;
					}
				}
			}
			else if (idDataSchema != null && !idDataSchema.trim().isEmpty()) {
				String[] idDataSchemaList = idDataSchema.trim().split(",");
				
				if (idDataSchemaList != null && idDataSchemaList.length > 0) {
					jobsCount = idDataSchemaList.length;
					
					for (String s_idDataSchema : idDataSchemaList) {
						long l_idDataSchema = Long.parseLong(s_idDataSchema);
						int insertCount = iTaskDAO.insertintoscheduledtasks(null, l_idDataSchema, idScheduler,
								projectId);
						insertintoscheduledtasks += insertCount;
					}
				}
			}

			if (insertintoscheduledtasks == jobsCount) {
				response.getWriter().println("Runtask Scheduled successfully");
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@RequestMapping(value = "/getValditionsForSchedule", method = RequestMethod.GET)
	public @ResponseBody String getValditionsForSchedule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idScheduler) {
		System.out.println("idScheduler=" + idScheduler);
		List<Long> idAppList = iTaskDAO.getValditionsForSchedule(idScheduler);
		String resultArray = "";
		if(idAppList!=null && idAppList.size()>0) {
			for(Long idApp : idAppList) {
				resultArray = resultArray + String.valueOf(idApp)+",";
			}
			
			if(resultArray.endsWith(",")) {
				resultArray = resultArray.substring(0,resultArray.length()-1);
			}
			
		}
		return resultArray;
	}
	

	@RequestMapping(value = "/getSchemasForSchedule", method = RequestMethod.GET)
	public @ResponseBody String getSchemasForSchedule(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idScheduler) {
		System.out.println("idScheduler=" + idScheduler);
		List<Long> schemaIdList = iTaskDAO.getSchemasForSchedule(idScheduler);
		String resultArray = "";
		if(schemaIdList!=null && schemaIdList.size()>0) {
			for(Long idApp : schemaIdList) {
				resultArray = resultArray + String.valueOf(idApp)+",";
			}
			
			if(resultArray.endsWith(",")) {
				resultArray = resultArray.substring(0,resultArray.length()-1);
			}
			
		}
		return resultArray;
	}

	@RequestMapping(value = "/editSchedule", method = RequestMethod.GET)
	public ModelAndView editSchedule(HttpSession session, long idSchedule) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {
			ListSchedule listScheduleData = iTaskDAO.getSchedulerById(idSchedule);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listScheduleData", listScheduleData);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Scheduled Task");
			modelAndView.setViewName("editScheduledTask");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
 	
	@RequestMapping(value = "/editScheduleTask", method = RequestMethod.POST)
	public void editScheduleTask(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam long idSchedule, @RequestParam String name, @RequestParam String description, @RequestParam String frequency,
			@RequestParam String scheduledDay, @RequestParam String day, @RequestParam String ScheduleTimer) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int insertIntolistSchedule = iTaskDAO.updateListSchedule(idSchedule, name, description, frequency, scheduledDay, day,
				ScheduleTimer);

		if (insertIntolistSchedule > 0) {
			try {
				response.getWriter().println("Runtask Schedule edited successfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				response.getWriter().println("There is a problem");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/scheduleTask", method = RequestMethod.POST)
	public void scheduleTask(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name, @RequestParam String description, @RequestParam String frequency,
			@RequestParam String scheduledDay, @RequestParam String day, @RequestParam String ScheduleTimer) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("scheduledDay=" + scheduledDay);
		Long projectId= (Long)session.getAttribute("projectId");
		int insertIntolistSchedule = iTaskDAO.insertIntolistSchedule(name, description, frequency, scheduledDay, day,
				ScheduleTimer,projectId);
		System.out.println("insertIntolistSchedule=" + insertIntolistSchedule);

		if (insertIntolistSchedule > 0) {
			try {
				response.getWriter().println("Runtask Scheduled successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				response.getWriter().println("There is a problem");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * ModelAndView modelAndView = new ModelAndView();
		 * modelAndView.addObject("currentSection","Tasks");
		 * modelAndView.addObject("currentLink","Scheduled Task");
		 * modelAndView.setViewName("scheduledTask");
		 * 
		 * return modelAndView;
		 */
	}

	@RequestMapping(value = "/runValidationByRunType", method = RequestMethod.GET)
	public ModelAndView runValidationByRunType(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam long idApp, @RequestParam String validationName, @RequestParam String validationRunType) {

		System.out.println("\n====> runValidationByRunType - START");
		try {
			String email = (String) session.getAttribute("email");
			Object user = session.getAttribute("user");

			if (user != null && user.equals("validUser")) {
				String executionStatus = "N";
				String executionStatusMsg = "";
				String uniqueId = "";
				try {
					// Get deployMode
					String deployMode = clusterProperties.getProperty("deploymode");

					if (deployMode.trim().equalsIgnoreCase("2")) {
						deployMode = "local";
					} else {
						deployMode = "cluster";
					}

					// Check if job is already in progress
					boolean isJobInProgress = iTaskDAO.isApplicationInProgress(idApp);

					if (isJobInProgress) {
						System.out.println("\n====> Validtion is already queued or in-progress.");
						executionStatusMsg = "Validtion is already queued or in-progress.";
					} else {
						// Place the job in queue
						 uniqueId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, email,
								validationRunType);

						System.out.println("\n====> uniqueId: " + uniqueId);

						if (uniqueId != null && !uniqueId.trim().isEmpty())
							executionStatus = "Y";
						else
							executionStatusMsg = "Failed to run the validation, please retry.";
					}

				} catch (Exception e) {
					executionStatusMsg = "Error occurred, failed to run the validation.";
					e.printStackTrace();
				}
				ModelAndView modelAndView = new ModelAndView("validationJobProgress");
				modelAndView.addObject("idApp", idApp);
				modelAndView.addObject("validationName", validationName);
				modelAndView.addObject("currentSection", "Tasks");
				modelAndView.addObject("currentLink", "Run Task");
				modelAndView.addObject("uniqueId", uniqueId);
				modelAndView.addObject("jobExecStatus", executionStatus);
				modelAndView.addObject("jobExecStatusMsg", executionStatusMsg);

				return modelAndView;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("loginPage.jsp");
	}

	/* ==============code for single validation check selection for execution */
	@RequestMapping(value = "/runTaskResult", method = RequestMethod.POST, produces = "application/json")
	public void runtaskresult(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@RequestParam String idApp1, @RequestParam String task_name, @RequestParam String validationRunType) {

		String arr[] = idApp1.split(",");

		// String array[] = idApp1.split(idApp1, ',');

		try {
			System.out.println("run task controller1");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			
			String email = (String)session.getAttribute("email");
			System.out.println("email:" + email);
			
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Get deployMode
			String deployMode = clusterProperties.getProperty("deploymode");

			if (deployMode.trim().equalsIgnoreCase("2")) {
				deployMode = "local";
			} else {
				deployMode = "cluster";
			}

			System.out.println("\n===>Validation Run Type: "+ validationRunType);
			
			for (int i = 0; i < arr.length; i++) {

				// Long idApp = (long) Integer.parseInt(arr[i]);
				String uniqueId = iTaskDAO.insertRunScheduledTask((long) Integer.parseInt(arr[i]), "queued", deployMode, email, validationRunType);
				
				System.out.println(" ############################idApp :" + (long) Integer.parseInt(arr[i]));
				System.out.println("task_name:" + task_name);

				session.setAttribute("scheduledTask", "yes");
				//iTaskService.runJar(Long.parseLong(arr[i]));
				//Thread.sleep(1000);
				session.removeAttribute("scheduledTask");

				JSONObject json = new JSONObject();
				json.put("success", "success");
				json.put("appName", task_name);
				json.put("uniqueId", uniqueId);
				response.getWriter().println(json);
				System.out.println(json);
				response.getWriter().flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// task.setFail("Task could not be completed!");
			// return task;

		}

	}

	/*
	 * @RequestMapping(value = "/runTaskResult", method = RequestMethod.POST,
	 * produces = "application/json") public void runtaskresult(HttpServletRequest
	 * request, HttpSession session, HttpServletResponse response,
	 * 
	 * @RequestParam String task_name , @RequestParam String idAppString) {
	 * 
	 * Task task=new Task(); task.setSuccess("Task completed successfully!" );
	 * 
	 * try { System.out.println("run task controller1"); Object user =
	 * session.getAttribute("user"); System.out.println("user:" + user); if ((user
	 * == null) || (!user.equals("validUser"))) { try {
	 * response.sendRedirect("loginPage.jsp"); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * //System.out.println("idapp=>>>>>>>>>>>>>>"+idApp);
	 * //System.out.println("idappString=>>>>>>>>>>>>>>"+idAppString);
	 * 
	 * String[] idAppVar=idAppString.split(",");
	 * //System.out.println(Arrays.toString(idSS));
	 * 
	 * System.out.println("=============>>>>>><<<<<<<<<<<<<================");
	 * Long[] idApp=new Long[idAppVar.length]; for(int i=0;i<idAppVar.length;i++) {
	 * idApp[i]=Long.parseLong(idAppVar[i].replace("[","").replace("]","").replace(
	 * "\"",""));
	 * //System.out.println(Long.parseLong(idSS[i].replace("[","").replace("]","").
	 * replace("\"",""))); }
	 * 
	 * System.out.println("idApps of Selected validation =>>>>>>"); for(int
	 * i=0;i<idAppVar.length;i++) {
	 * 
	 * System.out.println(idApp[i]); }
	 * 
	 * for(int i=0;i<idAppVar.length;i++) {
	 * 
	 * boolean updateStatus = iTaskDAO.updateRunScheduledTask(idApp[i]); boolean
	 * insertOldSixTablesStatus = iTaskService.insertOldSixTables(idApp[i]); //
	 * boolean insertOldSixTablesStatus=true;
	 * System.out.println("insertOldSixTablesStatus:" + insertOldSixTablesStatus);
	 * System.out.println("idApp :" + idApp[i]); System.out.println("task_name:" +
	 * task_name.replace("[","").replace("]","").replace("\"",""));
	 * 
	 * //String lstValidations=request.getParameter("selectedValidation");
	 * //System.out.println("+++++++++++++******************* Selected Validation=>"
	 * +lstValidations);
	 * 
	 * session.setAttribute("scheduledTask", "yes"); iTaskService.runJar(idApp[i]);
	 * Thread.sleep(1000); session.removeAttribute("scheduledTask");
	 * 
	 * JSONObject json = new JSONObject(); json.put("success", "success");
	 * json.put("appName", task_name); response.getWriter().println(json);
	 * System.out.println(json); response.getWriter().flush();
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * // response.getWriter().println("{\"success\":\"some message\"}"); //
	 * response.getWriter().flush();
	 * 
	 * if(insertOldSixTablesStatus) { System.out.println(
	 * "succesufully inserted  to old tables"); task.setSuccess(
	 * "Task completed successfully!");
	 * 
	 * return task; } else { task.setSuccess( "Task completed successfully!");
	 * //task.setFail( "Task could not be completed!"); System.err.println(
	 * "problem while inserting in old six tables"); return task; }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } //
	 * task.setFail("Task could not be completed!"); // return task;
	 * 
	 * }
	 */

	@RequestMapping(value = "/listApplicationsView", method = RequestMethod.POST)
	public ModelAndView listApplicationsView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {

			List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId,projList);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listApplicationsdata", listApplicationsdata);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Run Task");
			modelAndView.setViewName("listApplicationsView");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/listApplicationsView", method = RequestMethod.GET)
	public ModelAndView listApplicationsViewGet(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {

			String isRuleCatalogDiscovery = appDbConnectionProperties.getProperty("isRuleCatalogDiscovery");
			isRuleCatalogDiscovery = (isRuleCatalogDiscovery != null
					&& isRuleCatalogDiscovery.trim().equalsIgnoreCase("Y")) ? "Y" : "N";

			Map<String,String> validationRunTypeMap = new HashMap<>();
			if(isRuleCatalogDiscovery.equalsIgnoreCase("Y")) {
				validationRunTypeMap.put(DatabuckConstants.VAL_RUN_TYPE_UNIT_TESTING,DatabuckConstants.VAL_RUN_TYPE_UNIT_TESTING_DISPLAY_NAME);
				boolean createPermission = rbacController.rbac("Tasks", "C", session);
				if(createPermission)
					validationRunTypeMap.put(DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD,DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD_DISPLAY_NAME);
			} else {
				validationRunTypeMap.put(DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD,DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD_DISPLAY_NAME);
			}

			List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId,projList);

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listApplicationsdata", listApplicationsdata);
			modelAndView.addObject("isRuleCatalogDiscovery", isRuleCatalogDiscovery);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Run Task");
			modelAndView.addObject("validationRunTypeMap", validationRunTypeMap);
			modelAndView.setViewName("listApplicationsView");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/statusPoll", method = RequestMethod.POST, produces = "application/json")
	public void startStatuspoll(HttpServletRequest request, HttpSession session, @RequestParam String idApp1,
			HttpServletResponse response, @RequestParam String uniqueId) {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				System.out.println("user:" + user);
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String arr[] = idApp1.split(",");
		String uniqueIds[] = uniqueId.split(",");
		for (int i = 0; i < arr.length; i++) {

			request.setAttribute("currentSection", "Tasks");
			request.setAttribute("currentLink", "Run Task");
			long idApp = (long) Integer.parseInt(arr[i]);
			String validationJobStatus = iTaskService.getTaskStatusFromRunScheduledTasks(idApp, uniqueIds[i]);
			String status = "Task " + validationJobStatus;
			
			SqlRowSet appTypeFromListApplications = iTaskService.getAppTypeFromListApplications(idApp);
			String appType = "", appName = "";
			while (appTypeFromListApplications.next()) {
				appType = appTypeFromListApplications.getString(1);
				appName = appTypeFromListApplications.getString(2);
			}
			// for showing task % status
			int percentage = 5;
			if (appType.equalsIgnoreCase("Data Forensics")) {
				percentage = iTaskService.getStatusOfDfReadFromTaskProgressStatus((long) Integer.parseInt(arr[i]));
				if (percentage >= 30) {
					double count = iTaskService
							.getColumnCountYesInListApplicationsAndListDFTranrule((long) Integer.parseInt(arr[i]));
					double passedStatus = iTaskService.getTaskStatusForPassed((long) Integer.parseInt(arr[i]));
					percentage = percentage + (int) ((passedStatus / count) * 70);
					
					if (percentage > 100) {
						percentage = 100;
					}
				}
			} else if (appType.equalsIgnoreCase("Data Matching") || appType.equalsIgnoreCase("Data Matching")
					|| appType.equalsIgnoreCase("Data Matching Group")
					|| appType.equalsIgnoreCase("Statistical Matching")
					|| appType.equalsIgnoreCase("Rolling DataMatching")) {
				percentage = iTaskService.getStatusForMatching((long) Integer.parseInt(arr[i]));
				// System.out.println("Matching");
			} else if (appType.equalsIgnoreCase("Schema Matching")) {
				percentage = iTaskService.getStatusForSchemaMatching((long) Integer.parseInt(arr[i]));
				if (percentage > 100) {
					percentage = 100;
				}
			}
			// System.out.println("percentage="+percentage);
		
			try {
				if (status.equalsIgnoreCase("Task completed1")) {
					ListApplications listApplicationsData = validationcheckdao
							.getdatafromlistapplications((long) Integer.parseInt(arr[i]));
					Long idData = listApplicationsData.getIdData();
					String recordCountAnomaly = listApplicationsData.getRecordCountAnomaly();
					DATA_QUALITY_INDEX DQI = new DATA_QUALITY_INDEX();
					// Record Anomaly Status on Dashboard
					// String recordAnomalyNewStatus =
					// iResultsDAO.getStatusFromRecordAnomalyTable(idApp);
					// DQI.setRecord_anomaly_status(recordAnomalyNewStatus);
					// RCA Dashboard
					String rcaStatus = iResultsDAO.getDashboardStatusForRCA((long) Integer.parseInt(arr[i]),
							recordCountAnomaly);
					DQI.setRecord_count_status(rcaStatus);
					// nullcountStatus Dashboard
					String nullcountStatus = iResultsDAO
							.getDashboardStatusForNullCount((long) Integer.parseInt(arr[i]));
					DQI.setNull_count_status(nullcountStatus);
					// numericalFieldStatsStatus Dashboard
					String numericalFieldStatus = iResultsDAO
							.getDashboardStatusFornumericalFieldStatsStatus((long) Integer.parseInt(arr[i]));
					DQI.setNumerical_field_status(numericalFieldStatus);
					// stringFieldStatsStatus Dashboard
					String stringFieldStatus = iResultsDAO
							.getDashboardStatusForstringFieldStatsStatus((long) Integer.parseInt(arr[i]));
					DQI.setString_field_status(stringFieldStatus);
					// recordAnomalyStatus Dashboard
					String recordAnomalystatus = iResultsDAO
							.getDashboardStatusForRecordAnomalyStatus((long) Integer.parseInt(arr[i]));
					DQI.setRecord_anomaly_status(recordAnomalystatus);
					// dataDriftStatus Dashboard
					// String dataDriftStatus =
					// iResultsDAO.getDashboardStatusForDataDriftStatus(idApp);
					// modelAndView.addObject("dataDriftStatus", dataDriftStatus);
					// System.out.println("dataDriftStatus="+dataDriftStatus);
					// allFieldsStatus Dashboard
					String allFieldsStatus = iResultsDAO
							.getDashboardStatusForAllFieldsStatus((long) Integer.parseInt(arr[i]));
					DQI.setAll_fields_status(allFieldsStatus);
					// identityfieldsStatus Dashboard
					String identityfieldsStatus = iResultsDAO
							.getDashboardStatusForIdentityfieldsStatus((long) Integer.parseInt(arr[i]));
					DQI.setUser_selected_fields_status(identityfieldsStatus);
					// Score
					boolean calculateScore = iResultsDAO
							.checkRunIntheResultTableForScore((long) Integer.parseInt(arr[i]), recordCountAnomaly);
					// calculateScore
					if (calculateScore) {
						DecimalFormat df = new DecimalFormat("#.00");
						// RecordAnomaly
						String recordAnomalyScore = iResultsDAO.CalculateScoreForRecordAnomaly(idData,
								(long) Integer.parseInt(arr[i]), recordCountAnomaly, rcaStatus, listApplicationsData);
						if (recordAnomalyScore.contains("∞")) {
							recordAnomalyScore = "0";
						}
						DQI.setRecord_count_score(recordAnomalyScore);
						// NullCountScore
						String nullCountScore = iResultsDAO.CalculateScoreForNullCount(idData,
								(long) Integer.parseInt(arr[i]), recordCountAnomaly, nullcountStatus,
								listApplicationsData);
						if (nullCountScore.contains("∞")) {
							nullCountScore = "0";
						}
						DQI.setNull_count_score(nullCountScore);
						// All FieldsScore
						String allFieldsScore = iResultsDAO.CalculateScoreForallFields(idData,
								(long) Integer.parseInt(arr[i]), allFieldsStatus, listApplicationsData);
						if (allFieldsScore.contains("∞")) {
							allFieldsScore = "0";
						}
						DQI.setAll_fields_score(allFieldsScore);
						// identityFieldsScore
						SqlRowSet dashboardDetails = iResultsDAO.CalculateDetailsForDashboard(
								(long) Integer.parseInt(arr[i]), "DQ_Uniqueness -Seleted Fields", listApplicationsData);
						String identityFieldsScore = "";
						while (dashboardDetails.next()) {
							identityFieldsScore = dashboardDetails.getString(4);
						}
						if (identityFieldsScore.contains("∞")) {
							identityFieldsScore = "0";
						}
						DQI.setUser_selected_fields_score(identityFieldsScore);
						// Numerical Field
						String numericalFieldScore = iResultsDAO.CalculateScoreForNumericalField(idData,
								(long) Integer.parseInt(arr[i]), numericalFieldStatus, listApplicationsData);
						if (numericalFieldScore.contains("∞")) {
							numericalFieldScore = "0";
						}
						DQI.setNumerical_field_score(numericalFieldScore);
						// string Field
						String stringFieldScore = iResultsDAO.CalculateScoreForStringField(idData,
								(long) Integer.parseInt(arr[i]), stringFieldStatus, listApplicationsData);
						if (stringFieldScore.contains("∞")) {
							stringFieldScore = "0";
						}
						DQI.setString_field_score(stringFieldScore);
						// Record Fingerprint
						String recordFieldScore = iResultsDAO.CalculateScoreForrecordFieldScore(idData,
								(long) Integer.parseInt(arr[i]), recordAnomalystatus, listApplicationsData);
						if (recordFieldScore.contains("∞")) {
							recordFieldScore = "0";
						}
						DQI.setRecord_anomaly_score(recordFieldScore);
						DQI.setValidation_check_id((long) Integer.parseInt(arr[i]));
						DQI.setValidation_check_name(listApplicationsData.getName());
						iTaskService.insertDataIntoDATA_QUALITY_INDEX(DQI, recordCountAnomaly);

					} else {

					}

				}

				// Check if job is completed, killed or failed and log it
				if (validationJobStatus != null && (validationJobStatus.equalsIgnoreCase("completed")
						|| validationJobStatus.equalsIgnoreCase("failed")
						|| validationJobStatus.equalsIgnoreCase("killed"))) {
					System.out.println("\n====>Validation with Id [" + idApp + "] and uniqueId [" + uniqueId + "] is "
							+ validationJobStatus + "!!");
				}
				
				JSONObject json = new JSONObject();
				json.put("percentage", percentage);
				json.put("success", status);
				// json.put("appName", appName);
				response.getWriter().println(json);
				response.getWriter().flush();
				// response.getWriter().println("{\"percentage\": \""+ percentage +
				// "\",\"success\": \""+ status + "\"}");
				// response.getWriter().println("{}");
				// response.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value = "/viewAppGroups", method = RequestMethod.GET)
	public ModelAndView viewAppGroups(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {
			List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId,projList);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listAppGroupData", listAppGroupData);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "viewAppGroups");
			modelAndView.setViewName("viewAppGroups");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/runAppGroup", method = RequestMethod.GET)
	public ModelAndView runAppGroup(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId = (Long) session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {
			List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId,projList);
			List<String> appGroupList = new ArrayList<String>();
			if (listAppGroupData != null && listAppGroupData.size() > 0) {
				for (ListAppGroup listAppGroup : listAppGroupData) {
					String data = listAppGroup.getIdAppGroup() + "-" + listAppGroup.getName();
					appGroupList.add(data);
				}
			}
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listAppGroupData", appGroupList);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "runAppGroup");
			modelAndView.setViewName("runAppGroup");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/getAppGroupNames", method = RequestMethod.GET)
	public @ResponseBody List<String> getAppGroupNames(HttpServletResponse response, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Long projectId = (Long) session.getAttribute("projectId");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		List<ListAppGroup> listAppGroupData = iTaskDAO.getAppGroupsForProject(projectId,projList);
		List<String> appGroupList = new ArrayList<String>();
		if (listAppGroupData != null && listAppGroupData.size() > 0) {
			for (ListAppGroup listAppGroup : listAppGroupData) {
				String data = listAppGroup.getIdAppGroup() + "-" + listAppGroup.getName();
				appGroupList.add(data);
			}
		}
		return appGroupList;
	}
	
	@RequestMapping(value = "/triggerAppGroup", method = RequestMethod.POST)
	public void triggerAppGroup(HttpServletResponse response, HttpServletRequest request, 
			HttpSession session, @RequestParam Long idAppGroup) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage.jsp");
			}

			JSONObject json = new JSONObject();
			String resultStatus = "failure";
			String resultMessage = "";
			if (idAppGroup != null && idAppGroup != 0l) {
				// Check if AppGroup is already in queued or started or in progress
				boolean status = iTaskDAO.checkIfAppGroupJobInQueue(idAppGroup);
				if (status) {
					resultMessage = "AppGroup is already in Queue";
				} else {
					status = iTaskDAO.checkIfAppGroupJobInProgress(idAppGroup);
					if (status) {
						resultMessage = "AppGroup is already in started / in progress";
					} else {
						String uniqueId = iTaskDAO.addAppGroupJobToQueue(idAppGroup);
						if (uniqueId != null) {
							resultStatus = "success";
							resultMessage = "AppGroup placed in queue successfully";
						} else {
							resultMessage = "There was a problem, failed to place in queue";
						}
					}
				}

			} else {
				resultMessage = "Invalid AppGroup";
			}

			json.append(resultStatus, resultMessage);
			response.getWriter().println(json);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/addAppGroup", method = RequestMethod.GET)
	public ModelAndView addAppGroup(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projlst =  (List<Project>)session.getAttribute("userProjectList");
		List<Project> projList = (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {
			List<ListApplications> listApplicationsdata = iTaskDAO.listApplicationsView(projectId,projList);
			List<ListSchedule> scheduleList = iTaskDAO.getSchedulers(projectId,projlst);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listApplicationsdata", listApplicationsdata);
			modelAndView.addObject("scheduleList", scheduleList);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Add AppGroup");
			modelAndView.setViewName("createAppGroup");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/createAppGroup", method = RequestMethod.POST)
	public void createAppGroup(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam String name, @RequestParam String description, @RequestParam String idAppList, 
			@RequestParam String schedulerEnabled, @RequestParam Long idScheduler) {
		try {
			System.out.println("\n============> createAppGroup <============");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Long projectId = (Long) session.getAttribute("projectId");

			if (schedulerEnabled != null && schedulerEnabled.equalsIgnoreCase("Y")) {
				schedulerEnabled = "Y";
			} else {
				schedulerEnabled = "N";
			}
			System.out.println("idAppList: " + idAppList);
			System.out.println("schedulerEnabled: " + schedulerEnabled);
			System.out.println("idScheduler: " + idScheduler);

			JSONObject json = new JSONObject();

			// Check if AppGroup name is Duplicate
			boolean isDuplicateName = iTaskDAO.isAppGroupNameDuplicated(name);

			if (!isDuplicateName) {
				if (idAppList != null && !idAppList.trim().isEmpty()) {
					Long insertIntolistAppGroup = iTaskDAO.insertIntolistAppGroup(name, description,
							schedulerEnabled, idScheduler, projectId, idAppList);
					System.out.println("insertIntolistAppGroup=" + insertIntolistAppGroup);

					if (insertIntolistAppGroup > 0) {
						json.append("success", "AppGroup successfully saved");
					} else {
						json.append("fail", "There was a problem");
					}
				} else {
					json.append("fail", "No validation selected,AppGroup failed");
				}
			} else
				json.append("fail", "AppGroup name already exists");
			response.getWriter().println(json);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/customizeAppGroup", method = RequestMethod.GET)
	public ModelAndView customizeAppGroup(HttpSession session,@RequestParam Long idAppGroup) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId= (Long)session.getAttribute("projectId");
		List<Project> projlst =  (List<Project>)session.getAttribute("userProjectList");
		boolean rbac = rbacController.rbac("Tasks", "C", session);
		if (rbac) {
			List<AppGroupMapping> appGroupMappings = iTaskDAO.getApplicationMappingForGroup(idAppGroup);
			List<ListSchedule> scheduleList = iTaskDAO.getSchedulers(projectId,projlst);
			String selectedAppIds = "";
			for(AppGroupMapping appGroupMapping : appGroupMappings) {
				selectedAppIds = selectedAppIds +appGroupMapping.getAppId()+",";
			}
			if(selectedAppIds!=null && selectedAppIds.length()>0) {
				selectedAppIds = selectedAppIds.substring(0,selectedAppIds.length()-1);
			}
			
			ListAppGroup listAppGroup = iTaskDAO.getListAppGroupById(idAppGroup);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("listAppGroup", listAppGroup);
			modelAndView.addObject("appGroupMappings", appGroupMappings);
			modelAndView.addObject("selectedAppIds", selectedAppIds);
			modelAndView.addObject("scheduleList", scheduleList);
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "Add AppGroup");
			modelAndView.setViewName("editAppGroup");

			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/deleteAppGroup", method = RequestMethod.POST)
	public void deleteAppGroup(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idAppGroup) {
		JSONObject json = new JSONObject();
		try {
			System.out.println("\n============> deleteAppGroup <============");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			iTaskDAO.deleteAppGroupById(idAppGroup);
			json.put("success", "AppGroup deleted successfully");
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/deleteAppGroupMapping", method = RequestMethod.POST)
	public void deleteAppGroupMapping(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idAppGroupMapping) {
		try {
			System.out.println("\n============> deleteAppGroupMapping <============");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			iTaskDAO.deleteAppGroupMapping(idAppGroupMapping);
			response.getWriter().println("AppGroupMapping deleted successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/editAppGroup", method = RequestMethod.POST)
	public void editAppGroup(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idAppGroup, @RequestParam String name, @RequestParam String description, 
			@RequestParam String idAppList, @RequestParam String schedulerEnabled, @RequestParam Long idScheduler) {
		try {
			System.out.println("\n============> editAppGroup <============");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Long projectId = (Long) session.getAttribute("projectId");
			
			if (schedulerEnabled != null && schedulerEnabled.equalsIgnoreCase("Y")) {
				schedulerEnabled = "Y";
			} else {
				schedulerEnabled = "N";
			}
			System.out.println("idAppList: " + idAppList);
			System.out.println("schedulerEnabled: " + schedulerEnabled);
			System.out.println("idScheduler: " + idScheduler);

			JSONObject json = new JSONObject();
			if (idAppList != null && !idAppList.trim().isEmpty()) {
				boolean updateIntolistAppGroup = iTaskDAO.updateIntolistAppGroup(idAppGroup, name, description, schedulerEnabled,
						idScheduler, projectId, idAppList);
				System.out.println("updateIntolistAppGroup=" + updateIntolistAppGroup);

				if (updateIntolistAppGroup) {
					json.append("success", "AppGroup edited successfully");
				} else {
					json.append("fail", "There was a problem");
				}
			} else {
				json.append("fail", "No validation selected,AppGroup failed");
			}
			response.getWriter().println(json);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// get validation name
	@RequestMapping(value = "/getApprovedValidationsForProject", method = RequestMethod.GET)
	public @ResponseBody List<String> getApprovedValidationsForProject(ModelAndView model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
		
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Long projectId = (Long) session.getAttribute("projectId");
		
		List<String> validationList = iTaskDAO.getApprovedValidationNamesListForProject(projectId);
		List<String> outputList = new ArrayList<>();
		for (String list : validationList) {
			String str = list.toString().replace("[", "").replace("]", "");
			outputList.add(str);
		}
		return outputList;

	}


	// Get list of schemaNames
	@RequestMapping(value = "/getSchemaNames", method = RequestMethod.GET)
	public @ResponseBody List<String> getSchemaNames(ModelAndView model, HttpSession session,
			HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
		List<String> schemaList = null;
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Long projectId = (Long) session.getAttribute("projectId");
			schemaList = schemaDao.getSchemaNames(projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemaList;
	}

	@RequestMapping(value = "/triggerDataSchema", method = RequestMethod.POST)
	public void triggerDataSchema(HttpServletResponse response, HttpServletRequest request, 
			HttpSession session, @RequestParam Long idDataSchema) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage.jsp");
			}

			JSONObject json = dataConnectionService.runSchemaJob(idDataSchema,null);
			response.getWriter().println(json);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/runSchema", method = RequestMethod.GET)
	public ModelAndView runSchema(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "R", session);
		if (rbac) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("currentSection", "Tasks");
			modelAndView.addObject("currentLink", "runSchema");
			modelAndView.setViewName("runSchema");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/accessLog", method = RequestMethod.GET)
	public ModelAndView viewAccessLog(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "R", session);
		if (rbac) {
			
			SqlRowSet loggingActivityData = IUserdAO.getlogging_activity();
			
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("logging_activity", loggingActivityData);
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "Access Log");
			modelAndView.setViewName("viewAccessLog");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/clearAccesslog", method = RequestMethod.GET)
	public void clearAllclearAccesslog(HttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			System.out.println("\n====> clearAccesslog - START <====");
			IUserdAO.clearAccessLog();
			json.put("success", "success");
			System.out.println("\n====> clearAccesslog - END  <====");
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
	//By Mamta (22-Jan-2022)
	@RequestMapping(value = "/duplicateschedulername", method = RequestMethod.POST)
	public void duplicateschedulername(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String schedulername = req.getParameter("val");
	    System.out.println("Schedulername =" + schedulername);

		// Eliminates spaces and special characters -changed by mamta
        /*
		if (schedulername == null || schedulername.trim().isEmpty()) {

			 try {
				    JSONObject json = new JSONObject();
					json.put("fail", "Please Enter Scheduler Name ");
					res.getWriter().println(json.getString("fail"));
					return;
				
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}*/
		Pattern pattern = Pattern.compile("[^A-Za-z0-9_]");				
		if (pattern.matcher(schedulername).find()) {
			try {
				
				JSONObject json = new JSONObject();
				json.put("fail", "Please Enter SchedularName without spaces and special character");
				res.getWriter().println(json.getString("fail"));
				return;
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		long projectId = (Long) session.getAttribute("projectId");
		boolean name = iTaskDAO.duplicateschedulername(schedulername,projectId);
		if(name){
			try {
				JSONObject json = new JSONObject();
				json.put("fail", "The Schedular Name already exists");
				res.getWriter().println(json.getString("fail"));
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}