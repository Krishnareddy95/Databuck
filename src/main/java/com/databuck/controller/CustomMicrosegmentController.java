package com.databuck.controller;

import com.databuck.bean.CustomMicrosegment;
import com.databuck.dao.CustomMicrosegmentDao;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.CustomMicrosegmentService;
import com.databuck.service.RBACController;
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
import java.util.List;

@Controller
public class CustomMicrosegmentController {

	@Autowired
	IValidationCheckDAO validationcheckdao;

	@Autowired
	private CustomMicrosegmentService customMicrosegmentService;

	@Autowired
	private CustomMicrosegmentDao customMicrosegmentDao;

	@RequestMapping(value = "/customMicrosegments", method = RequestMethod.GET)
	public ModelAndView alertNotificationView(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Extend Template & Rule", "R", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<String> tamplateListFrom = validationcheckdao.getTemplateListDataSource(projectId);
			List<String> templateNamesList = new ArrayList<>();
			for (String templateInfo : tamplateListFrom) {
				String templateStr = templateInfo.toString().replace("[", "").replace("]", "");
				templateNamesList.add(templateStr);
			}

			try {
				model.setViewName("customMicrosegments");
				model.addObject("templateNames", templateNamesList);
				model.addObject("currentSection", "Extend Template & Rule");
				model.addObject("currentLink", "Custom Microsegments");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/getCustomMicroSegmentsByTemplateId", method = RequestMethod.GET)
	public void getCustomMicroSegmentsByTemplateId(HttpServletRequest req, HttpServletResponse response,
			HttpSession session, @RequestParam long idData) {
		System.out.println("\n====> getCustomMicroSegmentsByTemplateId - START ");

		List<CustomMicrosegment> customMicrosegmentList = null;
		try {
			Object user = session.getAttribute("user");

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			customMicrosegmentList = customMicrosegmentDao.getCustomMicroSegmentsByIdData(idData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PrintWriter out = response.getWriter();
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("aaData", new JSONArray(customMicrosegmentList));
			jsonResult.put("iTotalRecords", customMicrosegmentList.size());
			jsonResult.put("iTotalDisplayRecords", customMicrosegmentList.size());
			out.print(jsonResult);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

	}

	@RequestMapping(value = "/getCustomMicroSegmentColumnNamesForCheck", method = RequestMethod.POST)
	public void getCustomMicroSegmentColumnNamesForCheck(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam long idData, @RequestParam String checkName) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<String> columnNamesForCheck = customMicrosegmentDao.getCustomMicroSegmentsColumnNamesForCheck(idData,
				checkName);

		ObjectMapper mapper = new ObjectMapper();
		try {

			String jsonInString = mapper.writeValueAsString(columnNamesForCheck);
			System.out.println("jsonInString=" + jsonInString);

			JSONObject displayName = new JSONObject();
			displayName.put("success", jsonInString);
			System.out.println("displayName=" + displayName);

			response.getWriter().println(displayName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "/addCustomMicrosegments", method = RequestMethod.POST)
	public void addCustomMicrosegments(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestBody CustomMicrosegment customMicrosegment) {

		String message = "";
		String status = "failed";
		JSONObject resultObj = new JSONObject();
		try {

			// Validate user
			Object user = session.getAttribute("user");
			long idUser = (Long) session.getAttribute("idUser");
			System.out.println("idUser=" + idUser);

			if ((user == null) || (!user.equals("validUser"))) {
				try {
					response.sendRedirect("loginPage");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Add custom Microsegments
			resultObj = customMicrosegmentService.addCustomMicrosegments(customMicrosegment);

		} catch (Exception e) {
			e.printStackTrace();
			message = "Exception occurred while adding custom microsegments";
			resultObj.put("status", status);
			resultObj.put("message", message);
		}

		try {
			response.getWriter().println(resultObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteCustomMicrosegments", method = RequestMethod.POST)
	public void deleteCustomMicrosegments(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long id) {

		String message = "";
		String status = "failed";
		try {

			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			long idUser = (Long) session.getAttribute("idUser");
			System.out.println("idUser=" + idUser);
			if ((user == null) || (!user.equals("validUser"))) {
				response.sendRedirect("loginPage");
			}

			boolean isDeleted = customMicrosegmentDao.deleteCustomMicrosegments(id);
			if (isDeleted) {
				message = "Custom Microsegment Deleted successfully";
				status = "success";
			} else {
				message = "Failed to delete Custom Microsegment";
			}

		} catch (Exception e) {
			e.printStackTrace();
			message = "Exception occurred while deleting Custom Microsegment";
		}

		try {
			JSONObject json = new JSONObject();
			json.put("status", status);
			json.put("message", message);
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
