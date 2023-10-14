package com.databuck.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.service.RBACController;
import com.databuck.service.UserSettingService;
import com.databuck.util.DateUtility;

@Controller
public class ComponentAccessController {

	@Autowired
	private RBACController rbacController;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UserSettingService userSettingService;

	/* Load MVC view i.e. JSP page for component access control management */
	@RequestMapping(value = "/componentAccessControl", method = RequestMethod.GET)
	public ModelAndView getComponentAccessControl(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("componentAccessControl");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "Features Access Control");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	/* Load or reload access control view list */
	@RequestMapping(value = "/loadComponentAccessControlViewList", method = RequestMethod.POST, produces = "application/json")
	public void loadComponentAccessControlViewList(HttpSession oSession, HttpServletResponse oResponse)
			throws IOException {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadComponentAccessControlViewList 01", "Begin controller");
			oJsonResponse = userSettingService.getComponentAccessControlAllData(null);
			DateUtility.DebugLog("loadRuleCatalogRecordList 02", "End controller");

			oJsonResponse.put("Status", true);
			oJsonResponse.put("Msg", "");
		} catch (Exception oException) {
			oException.printStackTrace();

			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	/* Save access control data */
	@RequestMapping(value = "/saveSelectedAccessControl", method = RequestMethod.POST, produces = "application/json")
	public void saveSelectedAccessControl(HttpSession oSession, @RequestParam String SelectedComponents,
			@RequestParam String SelectedRoles, HttpServletResponse oResponse) throws IOException {

		JSONObject oJsonResponse = new JSONObject();
		DateUtility.DebugLog("saveSelectedAccessControl 01",
				String.format("Begin controller with '%1$s', '%2$s'", SelectedComponents, SelectedRoles));

		try {
			String[] aSelectedComponents = SelectedComponents.split(",", 0);
			String[] aSelectedRoles = SelectedRoles.split(",", 0);

			DateUtility.DebugLog("saveSelectedAccessControl 02", String.format("Begin controller with '%1$s', '%2$s'",
					String.join(",", aSelectedComponents), String.join(",", aSelectedRoles)));

			userSettingService.saveAccessControl(aSelectedComponents, aSelectedRoles);

			oJsonResponse.put("Status", true);
			oJsonResponse.put("Msg", "Role assignment successfully saved");

		} catch (Exception oException) {
			oException.printStackTrace();

			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();

		DateUtility.DebugLog("saveSelectedAccessControl 02", "End controller");
	}

}
