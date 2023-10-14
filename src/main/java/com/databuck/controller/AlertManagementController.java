package com.databuck.controller;

import com.databuck.bean.AlertEventMaster;
import com.databuck.bean.AlertEventSubscription;
import com.databuck.bean.Project;
import com.databuck.dao.AlertEventDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.service.AlertManagementService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Controller
public class AlertManagementController {

	@Autowired
	private AlertManagementService alertManagementService;

	@Autowired
	private AlertEventDao alertEventDao;

	@Autowired
	private IProjectDAO projectDAO;

	@Autowired
	public LoginService loginService;

	@Autowired
	public IProjectDAO iProjectDAO;

	@RequestMapping(value = "/alertNotificationView", method = RequestMethod.GET)
	public ModelAndView alertNotificationView(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		System.out.println("\n====> alertNotificationView - START ");
		Object user = session.getAttribute("user");

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "R", session);
		if (rbac) {
			try {
				model.setViewName("alertNotificationView");
				model.addObject("currentSection", "User Settings");
				model.addObject("currentLink", "AlertNotifications");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getAllAlertEvents", method = RequestMethod.GET)
	public void getAllAlertEvents(HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> getAllAlertEvents - START ");
		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";
		int totalRecordCount = 0;

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				JSONArray alertEventList = alertEventDao.getAllAlertEvents();

				if (alertEventList != null && alertEventList.length() > 0) {
					totalRecordCount = alertEventList.length();
					status = "success";
					message = "Alert Event List Fetched successfully";
					jsonResult.put("aaData", alertEventList);
				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to read Alert Event List";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			jsonResult.put("iTotalRecords", totalRecordCount);
			jsonResult.put("iTotalDisplayRecords", totalRecordCount);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/editAlertEventNotification", method = RequestMethod.GET)
	public ModelAndView editAlertEventNotification(@RequestParam int eventId, HttpServletRequest request,
			ModelAndView model, HttpSession session) throws IOException {
		System.out.println("\n====> editAlertEventNotification - START ");
		Object user = session.getAttribute("user");

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "R", session);
		if (rbac) {

			AlertEventMaster alertEventMaster = alertEventDao.getAlertEventById(eventId);

			if (alertEventMaster != null) {
				try {
					JSONObject json = null;
					JSONArray alertEventList = alertEventDao.getAllAlertEvents();
					List<AlertEventMaster> eventNameList = new ArrayList<AlertEventMaster>();
					AlertEventMaster eventMaster = null;
					for (int i = 0; i < alertEventList.length(); i++) {
						eventMaster = new AlertEventMaster();
						json = (JSONObject) alertEventList.get(i);
						eventMaster.setEventId(json.getInt("eventId"));
						eventMaster.setEventName(json.getString("eventName"));
						eventNameList.add(eventMaster);
					}
					model.setViewName("eventViewManagement");
					model.addObject("currentSection", "User Settings");
					model.addObject("currentLink", "AlertNotifications");
					model.addObject("alertEventMaster", alertEventMaster);
					model.addObject("eventNameList", eventNameList);

				} catch (Exception e) {
					e.printStackTrace();
				}

				return model;
			} else
				return null;

		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/addEventSubscription", method = RequestMethod.POST, consumes = "application/json")
	public void addEventSubscription(@RequestBody List<AlertEventSubscription> alertEventSubscriptionList,
			HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> addEventSubscription - START ");

		JSONObject jsonResult = new JSONObject();

		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {
				JSONObject resJson = alertManagementService.addEventSubscription(alertEventSubscriptionList);
				status = resJson.getString("status");
				message = resJson.getString("message");

			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to add Event subscriptions";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/updateEventSubscription", method = RequestMethod.POST)
	public void updateEventSubscription(@RequestParam int eventId, @RequestParam String name,
			@RequestParam String msgSubject, @RequestParam String msgBody, HttpServletRequest req,
			HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> updateEventSubscription - START ");

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				int result = alertEventDao.updateAlertEventMessage(eventId, msgBody);

				if (result > 0) {
					status = "success";
					message = "Event Subscription updated successfully";
				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to update Event Subscription";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/alertEventSubscription", method = RequestMethod.GET)
	public ModelAndView alertEventSubscription(@RequestParam int selectedEvent, HttpServletRequest request,
			ModelAndView model, HttpSession session) throws IOException {
		System.out.println("\n====> alertEventSubscription - START ");

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "R", session);
		if (rbac) {
			try {
				List<AlertEventMaster> alertEventList = alertEventDao.getListOfAlertEvents();
				AlertEventMaster alertEventMaster = alertEventDao.getAlertEventById(selectedEvent);

				String selectedEventName = alertEventMaster.getEventName();
				List<Project> projectList = projectDAO.getAllProjects();

				model.setViewName("alertEventSubscription");
				model.addObject("eventId", selectedEvent);
				model.addObject("eventName", selectedEventName);
				model.addObject("alertEventList", alertEventList);
				model.addObject("projectList", projectList);
				model.addObject("currentSection", "User Settings");
				model.addObject("currentLink", "AlertNotifications");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/updateEventView", method = RequestMethod.POST)
	public void updateEventView(@RequestParam int eventId, @RequestParam String name, @RequestParam String msgBody,
			@RequestParam String msgSubject, HttpServletRequest req, HttpServletResponse response,
			HttpSession session) {
		System.out.println("\n====> updateEventView - START ");

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				int result = alertEventDao.updateAlertEventMessage(eventId, msgBody);

				if (result > 0) {
					status = "success";
					message = "Event Message Updated successfully";
				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to update Event Message";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/getAlertEventById", method = RequestMethod.GET)
	public void getAlertEventById(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam int eventId) {
		System.out.println("\n====> getAlertEventById - START ");

		JSONObject jsonResult = new JSONObject();
		PrintWriter out = null;

		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String message = "";
		String status = "failed";

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				AlertEventMaster alertEventMaster = alertEventDao.getAlertEventById(eventId);

				if (alertEventMaster != null) {
					JSONObject alertEventObj = new JSONObject();
					alertEventObj.put("eventId", eventId);
					alertEventObj.put("eventName", alertEventMaster.getEventName());
					alertEventObj.put("eventMessegeSubject", alertEventMaster.getEventMessageSubject());
					alertEventObj.put("alertEventMessege", alertEventMaster.getEventMessageBody());

					status = "success";
					message = "Alert Event List Fetched successfully";
					jsonResult.put("result", alertEventObj);

				}
			} else
				message = "Invalid User";

		} catch (Exception e) {
			message = "Failed to read Alert Event List";
			e.printStackTrace();
		}
		System.out.println("message:" + message);
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("status", status);
			jsonResult.put("message", message);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/getAllCommunicationModes", method = RequestMethod.GET)
	public void getAllCommunicationModes(HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> getAllCommunicationModes - START ");

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		JSONArray commModeList = new JSONArray();

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				commModeList = alertEventDao.getAllCommunicationModes();

				if (commModeList != null && commModeList.length() > 0) {
					System.out.println("Success");
				}
			} else
				System.out.println("Failed");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");

			out.print(commModeList);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/getAllSubscribedEvents", method = RequestMethod.GET)
	public void getAllSubscribedEvents(HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		System.out.println("\n====> getAllSubscribedEvents - START ");

		JSONObject jsonResult = new JSONObject();
		JSONArray subscribedEventList = null;
		int totalRecordCount = 0;
		int displayRecordCount = 0;

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				subscribedEventList = alertEventDao.getSubscribedEvents();

				if (subscribedEventList != null && subscribedEventList.length() > 0) {
					totalRecordCount = subscribedEventList.length();
					displayRecordCount = subscribedEventList.length();
				}
			} else
				response.sendRedirect("loginPage.jsp");

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("iTotalRecords", totalRecordCount);
			jsonResult.put("iTotalDisplayRecords", displayRecordCount);
			jsonResult.put("aaData", subscribedEventList);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/getDatabuckAlertLog", method = RequestMethod.POST, produces = "application/json")
	public void getDatabuckAlertLog(HttpSession oSession, HttpServletRequest oRequest, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();
		HashMap<String, String> oPaginationParms = new HashMap<String, String>();
		ObjectMapper oObjectMapper = new ObjectMapper();

		Long nProjectId = 0l;
		List<Project> aProjectList = null;

		try {
			Object user = oSession.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				nProjectId = (Long) oSession.getAttribute("projectId");
				aProjectList = loginService.getAllDistinctProjectListForUser(oSession);
				JSONArray alertEventNames = alertEventDao.getAllAlertEvents();
				List<String> taskNames = alertEventDao.geTaskNameList();
				oJsonResponse.put("alertEventNames", alertEventNames);
				oJsonResponse.put("taskNames", taskNames);
				oJsonResponse.put("SelectedProjectId", nProjectId);
				oJsonResponse.put("AllProjectList", new JSONArray(oObjectMapper.writeValueAsString(aProjectList)));
                
				for (String sParmName : new String[] { "EventIds", "TaskName", "FromDate", "ToDate", "ProjectIds", "SearchText" }) {
					oPaginationParms.put(sParmName, oRequest.getParameter(sParmName));
              
				}
				oJsonResponse.put("ViewAlertEventDataList", alertEventDao.getDatabuckAlertLogs(oPaginationParms));
				oResponse.getWriter().println(oJsonResponse);
				oResponse.getWriter().flush();
			} else
				oResponse.sendRedirect("loginPage.jsp");

		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/getAllSubscribedEventByEventId", method = RequestMethod.GET)
	public void getAllSubscribedEventByEventId(HttpServletRequest req, HttpServletResponse response,
			HttpSession session, @RequestParam Long eventId) {
		System.out.println("\n====> getAllSubscribedEventByEventId - START ");

		JSONObject jsonResult = new JSONObject();
		JSONArray subscribedEventList = null;
		int totalRecordCount = 0;
		int displayRecordCount = 0;

		try {
			Object user = session.getAttribute("user");

			if ((user != null) && (user.equals("validUser"))) {

				subscribedEventList = alertEventDao.getAllSubscribedEventByEventId(eventId);
				if (subscribedEventList != null && subscribedEventList.length() > 0) {
					totalRecordCount = subscribedEventList.length();
					displayRecordCount = subscribedEventList.length();
				}
			} else {
				response.sendRedirect("loginPage.jsp");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			jsonResult.put("iTotalRecords", totalRecordCount);
			jsonResult.put("iTotalDisplayRecords", displayRecordCount);
			jsonResult.put("aaData", subscribedEventList);
			out.print(jsonResult);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/alertManagementView", method = RequestMethod.GET)
	public ModelAndView alertManagementView(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		System.out.println("\n====> alertNotificationView - START ");
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "R", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			Long searchfilter_projectId = (Long) session.getAttribute("searchfilter_projectId");
			// When project is not selected in search filter, default selected project will
			// be considered for search filter
			Long selected_projectId = (searchfilter_projectId != null && searchfilter_projectId > 0l)
					? searchfilter_projectId
					: projectId;
			Project selectedProject = projectDAO.getSelectedProject(selected_projectId);
			try {
				model.setViewName("alertManagementView");
				model.addObject("currentSection", "User Settings");
				model.addObject("currentLink", "AlertNotifications");
				model.addObject("selectedProject", selectedProject);
				model.addObject("SelectedProjectId", (Long) session.getAttribute("projectId"));

			} catch (Exception e) {
				e.printStackTrace();
			}

			return model;
		} else
			return new ModelAndView("loginPage");
	}
	
	@RequestMapping(value = "/updateAlertSubscriptionCommunicationValues",  method = RequestMethod.POST)
	public void updateAlertSubscriptionCommunicationValues(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam long alertSubscriptionId, @RequestParam String communicationValues) {

		String status = "failed";
		String message = "Failed to update communication values";

		try {
			System.out.println("\n====> updateAlertSubscriptionCommunicationValues - START ");

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			boolean updateStatus = alertEventDao.updateAlertSubscriptionCommunicationValues(alertSubscriptionId,
					communicationValues);
			if (updateStatus) {
				status = "success";
				message = "Communication Values updated successfully";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("status", status);
		json.put("message", message);
		try {
			response.getWriter().print(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
