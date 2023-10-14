package com.databuck.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.service.ApplicationSettingsService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ApplicationSettingsUpdate;
import com.databuck.bean.DatabuckProperties;
import com.databuck.config.DatabuckPropertyLoader;
import com.databuck.econstants.DatabuckPropertyCategory;
import com.databuck.dao.ITaskDAO;
import com.databuck.service.ITaskService;
import com.databuck.service.RBACController;

@Controller
public class ApplicationSettingsController {

	@Autowired
	private ITaskService iTaskService;

	@Autowired
	private DatabuckPropertyLoader databuckPropertyLoader;

	@Autowired
	private ApplicationSettingsService applicationSettingsService;

	@Autowired
	private static boolean isPropChangesWaitingRestart = false;

	/*
	 * add property files attributes(model and view)based on the page property file
	 */

	@RequestMapping(value = "/applicationSettingsView", method = RequestMethod.GET)
	public ModelAndView applicationSettingsView(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {

		String propertyCategory = request.getParameter("vData");
		Object user = session.getAttribute("user");

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Application Settings", "R", session);
		if (rbac) {
			model.setViewName("applicationSettingsView");

			model.addObject("currentSection", "Application Settings");
			model.addObject("currentLink", "AppSettingsView");

			List<String> propertyCategoriesNames = Stream.of(DatabuckPropertyCategory.values()).map(Enum::name).collect(Collectors.toList());

			List<DatabuckProperties> propertiesList = new ArrayList<>();
			String currentSubLink = "";

			if(propertyCategory!=null && !propertyCategory.trim().isEmpty() && propertyCategoriesNames.contains(propertyCategory.trim().toLowerCase())){
				propertiesList = databuckPropertyLoader.getPropertiesForCategory(propertyCategory, false);
				currentSubLink = propertyCategory;
			}

			model.addObject("propertiesList", propertiesList);
			model.addObject("currentSubLink", currentSubLink);
			model.addObject("isPropChangesWaitingRestart", isPropChangesWaitingRestart);
			return model;
		} else {
			System.out.println(
					"\n====>User don't have read access on Application Settings page, so redirecting to login page!!");
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/applicationReboot", method = RequestMethod.GET)
	public ModelAndView applicationReboot(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("Application Settings", "R", session);
		if (rbac) {
			model.setViewName("applicationRebootView");
			model.addObject("currentSection", "Application Settings");
			model.addObject("currentLink", "Application Reboot");

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/triggerRebootScript", method = RequestMethod.GET)
	public void triggerRebootScript(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {

		int triggerResult = iTaskService.runRebootScript();
		JSONObject json = new JSONObject();
		if (triggerResult > 0) {

			try {
				json.append("success", "Databuck application restart triggered");
				response.getWriter().println(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			try {
				json.append("fail", "There was a problem");
				response.getWriter().println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/saveUpdatedProperties", method = RequestMethod.POST, consumes = "application/json")
	public void saveUpdatedProperties(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestBody ApplicationSettingsUpdate[] paramJsonValues) {

		JSONObject json = applicationSettingsService.saveUpdatedProperties(paramJsonValues);

		String isRestartRequired = json.getString("isRestartRequired");
		if(isRestartRequired != null && isRestartRequired.trim().equalsIgnoreCase("Y")){
			isPropChangesWaitingRestart=true;
		}

		try {
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void setIsPropChangesWaitingRestart(boolean status){
		isPropChangesWaitingRestart = status;
	}

	public static boolean isPropChangesWaitingRestart(){
		return isPropChangesWaitingRestart;
	}

}
