package com.databuck.controller;

import java.io.IOException;
import java.util.Properties;

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

import com.databuck.Migration.Migration;
import com.databuck.dao.IUserDAO;
import com.databuck.databasemigration.MigrationManagement;
import com.databuck.service.DomainManagementService;
import com.databuck.service.IProjectService;
import com.databuck.service.IUserService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.DateUtility;

@Controller
public class DomainManagementController {
	@Autowired
	private RBACController rbacController;

	@Autowired
	private DomainManagementService domainService;

	@Autowired
	public IUserService userservice;

	@Autowired
	private Migration migration;

	@Autowired
	private MigrationManagement migrationManage;

	@Autowired
	private IProjectService iProjectService;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	public LoginService loginService;

	@Autowired
	IUserDAO userDAO;

	protected static final String GLOBAL_DOMAIN_UNIQUE = "Already one global domain exists and multiple global domains not allowed, save data failed";

	@RequestMapping(value = "/domainViewList")
	public ModelAndView domainViewList(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		String isRuleCatalogDiscovery = appDbConnectionProperties.getProperty("isRuleCatalogDiscovery");

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "C", session);
		if (rbac) {

			model.setViewName("domainListViewManagement");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "View Domain");
			model.addObject("IsRuleCatalogDiscovery", isRuleCatalogDiscovery);
			return model;
		} else {
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/loadDomainRecordList", method = RequestMethod.POST, produces = "application/json")
	public void loadDomainRecordList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadDomainList 01", "Begin controller processing for domain list");

			oJsonResponse = domainService.getDomainPageData("oldUI");

			DateUtility.DebugLog("loadDomainList 02", "Got data sending to client");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	@RequestMapping(value = "/mainDomainHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainDomainHandler(HttpSession oSession, @RequestParam String DomainData, HttpServletResponse oResponse)
			throws IOException {
		JSONObject oDomainData = new JSONObject(DomainData);
		JSONObject oJsonResponse = new JSONObject();
		String sContext;
		String sMsg = "";
		boolean lStatus = true;
		boolean lNewDomain = false;

		System.out.println("values of input" + oDomainData.toString());

		try {
			String domainName = oDomainData.getJSONObject("Data").getString("DomainName");
			System.out.println("***Domain Names =" + domainName);
			if (domainName == null || domainName.isEmpty() || domainName == " ") {
				throw new Exception("Please enter Domain AccessKey");
			}

			String projectIds = oDomainData.getJSONObject("Data").getString("ProjectIds");
			System.out.println("***ProjectIds=" + projectIds);
			if (projectIds == null || projectIds.isEmpty() || projectIds == " ") {
				throw new Exception("Please select Domain Projects");
			}

			DateUtility.DebugLog("Domain handler data 01", String.format("Begin controller Context = %1$s, Data = %2$s",
					oDomainData.getString("Context"), oDomainData.getJSONObject("Data")));
			sContext = oDomainData.getString("Context");

			switch (sContext) {

			case "DataSave":
				lNewDomain = (Integer.parseInt(oDomainData.getJSONObject("Data").getString("DomainId")) < 0) ? true
						: false;

				if (lNewDomain) {
					oJsonResponse = domainService.addNewDomain(oDomainData.getJSONObject("Data"));
				} else {
					oJsonResponse = domainService.updateExistingDomain(oDomainData.getJSONObject("Data"));
				}

				break;

			case "DataDelete":
				oJsonResponse = domainService.deleteSelectedDomain(oDomainData.getJSONObject("Data"));
				break;

			default:
			}

			DateUtility.DebugLog("Domain handler data 02", "End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

}
