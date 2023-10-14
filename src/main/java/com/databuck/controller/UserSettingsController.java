package com.databuck.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.Migration.Migration;
import com.databuck.dao.IUserDAO;
import com.databuck.databasemigration.MigrationManagement;
import com.databuck.security.CheckVulnerability;
import com.databuck.service.IProjectService;
import com.databuck.service.IUserService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.service.UserSettingService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class UserSettingsController {
	@Autowired
	private RBACController rbacController;

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

	@Autowired
	private JwfSpaInfra jwfSpaInfra;

	@Autowired
	private CheckVulnerability checkVulnerability;

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	IUserDAO iUserDAO;

	@RequestMapping(value = "/accessControls")
	public ModelAndView accessControls(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "R", session);
		if (rbac) {

			SqlRowSet accessControlsData = userservice.getAccessControlsFromModuleTable();
			System.out.println("accessControlsData=" + accessControlsData);
			model.setViewName("showAccessControlsData");
			model.addObject("accessControlsData", accessControlsData);
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "View Modules");
			return model;
		} else {
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/roleManagement")
	public ModelAndView roleManagement(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		// rbacController.RBAC();

		SqlRowSet roleManagementData = userservice.getroleManagementFromRoleTable();
		System.out.println("accessControlsData=" + roleManagementData);
		model.setViewName("showroleManagementData");
		model.addObject("roleManagementData", roleManagementData);
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "View Roles");
		return model;
	}

	@RequestMapping(value = "/viewUsers")
	public ModelAndView viewUsers(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		SqlRowSet usersTableData = userservice.getUsersFromUserTable();
		System.out.println("usersTableData=" + usersTableData);
		model.setViewName("showUsersData");
		model.addObject("usersTableData", usersTableData);
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "View Users");
		return model;
	}

	@RequestMapping(value = "/addNewUser")
	public ModelAndView addNewUser(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
		model.addObject("Roles", Roles);

		model.setViewName("addNewUser");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "Add New User");
		return model;
	}

	@RequestMapping(value = "/addNewUserIntoDatabase", method = RequestMethod.POST)
	public void addNewUserIntoDatabase(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam Long roleid, @RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String userName, @RequestParam String password) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		System.out.println("roleid=" + roleid);
		System.out.println("firstName=" + firstName);
		System.out.println("lastName=" + lastName);
		System.out.println("userName=" + userName);
		// System.out.println("password=" + password);
		int update = userservice.insertDataIntoUserTable(roleid, firstName, lastName, userName, password);
		JSONObject json = new JSONObject();
		if (update > 0) {
			try {
				json.append("success", "updated successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				json.append("fail", "There was a problem");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/addNewRole")
	public ModelAndView addNewRole(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Map<Long, String> Modules = userservice.getIdTaskandTaskName();
		model.addObject("Modules", Modules);

		model.setViewName("addNewRole");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "Add New Role");
		return model;
	}

	@RequestMapping(value = "/saveNewRoleIntoDatabase")
	public void saveNewRoleIntoDatabase(ModelAndView model, HttpServletResponse response, HttpSession session,
			@RequestParam String roleName, @RequestParam String description, @RequestParam String taskDataAll,
			@RequestParam String accessControlAll) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		System.out.println("roleName=" + roleName + "description=" + description);
		System.out.println("taskDataAll=" + taskDataAll + "accessControlAll=" + accessControlAll);
		String[] taskData = taskDataAll.split("-");
		String[] accessControl = accessControlAll.split(",");
		List<String> key = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		for (int i = 0; i < accessControl.length || i < taskData.length; i++) {
			if (accessControl[i].length() > 0) {
				System.out.println("accessControl=" + accessControl[i]);
				value.add(accessControl[i]);
			}
		}
		for (int i = 0; i < taskData.length; i++) {
			key.add(taskData[i]);
			System.out.println("taskData=" + taskData[i]);
		}
		int update = userservice.insertIntoRoleandRoleModuleTable(key, value, roleName, description);
		JSONObject json = new JSONObject();
		if (update > 0) {
			try {
				json.append("success", "NewRole Created successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				json.append("fail", "There was a problem");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/saveEditedRoleIntoDatabase")
	public void saveEditedRoleIntoDatabase(ModelAndView model, HttpServletResponse response, HttpSession session,
			HttpServletRequest request, @RequestParam String roleName, @RequestParam String description,
			@RequestParam Long idRole, @RequestParam String taskDataAll, @RequestParam String accessControlAll) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		System.out.println("roleName=" + roleName + "description=" + description);
		System.out.println("taskDataAll=" + taskDataAll + "accessControlAll=" + accessControlAll);
		String[] taskData = taskDataAll.split("-");
		String[] accessControl = accessControlAll.split(",");
		List<String> key = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		try {
			for (int i = 0; i < accessControl.length || i < taskData.length; i++) {
				if (accessControl[i].length() > 0) {
					// System.out.println("accessControl="+accessControl[i]);
					value.add(accessControl[i]);
				}
			}
			for (int i = 0; i < taskData.length; i++) {
				key.add(taskData[i]);
				// System.out.println("taskData="+taskData[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String idRole = request.getParameter("idRole");
		System.out.println("idRole=" + idRole);
		int update = userservice.updateIntoRoleandRoleModuleTable(key, value, idRole);
		JSONObject json = new JSONObject();
		if (update > 0) {
			try {
				json.append("success", "New Role Created Successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				json.append("fail", "There was a problem");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/editRoleModule")
	public ModelAndView editRoleModule(ModelAndView model, HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "U", session);
		if (rbac) {
			String idRole = request.getParameter("idRole");
			System.out.println("idRole=" + idRole);
			Map<String, String> roleNameandDescription = userservice.getRoleNameandDescriptionFromRole(idRole);
			for (Map.Entry<String, String> Names : roleNameandDescription.entrySet()) {
				model.addObject("Name", Names.getKey());
				model.addObject("Description", Names.getValue());
			}
			Map<Long, String> RoleModule = userservice.getIdTaskandAccessControlFromRoleModuleTable(idRole);
			model.addObject("RoleModule", RoleModule);
			Map<Long, String> Module = userservice.getIdTaskandTaskName();
			model.addObject("Module", Module);
			System.out.println("Module=" + Module);
			model.setViewName("editRoleModule");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "View Roles");
			model.addObject("idRole", idRole);
			return model;
		} else {
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/deleteRoleModule", method = RequestMethod.GET)
	public ModelAndView deleteRoleModule(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "D", session);
		if (rbac) {
			String idRole = request.getParameter("idRole");
			/* iTaskDAO.deleteFromListSchedule(idSchedule); */
			userservice.deleteRole(idRole);
			ModelAndView modelAndView = new ModelAndView("Data Template Created Successfully");
			modelAndView.addObject("message", "Role Deleted Successfully");
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "View Roles");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/deleteUserModule", method = RequestMethod.GET)
	public ModelAndView deleteUserModule(HttpSession session, HttpServletRequest request) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Tasks", "D", session);
		if (rbac) {
			String idUser = request.getParameter("idUser");
			userservice.deleteUsers(idUser);
			ModelAndView modelAndView = new ModelAndView("Data Template Created Successfully");
			modelAndView.addObject("message", "User Deleted Successfully");
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "View Users");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	// To check whether current user have access to download csv or not.
	@RequestMapping(value = "/isDownloadCsvAllowed", method = RequestMethod.POST)
	public void isDownloadCsvAllowed(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, @RequestParam Long idRole) {
		String sAccessControl = "";
		JSONObject json = new JSONObject();
		String msg = "";
		String status = "failed";

		try {
			// get the property enabled value for downloading data reports
			String downloadFailedDataReportsAllowed = appDbConnectionProperties
					.getProperty("download.failed.data.reports.allowed");
			if (downloadFailedDataReportsAllowed == null || downloadFailedDataReportsAllowed.trim().isEmpty()) {
				downloadFailedDataReportsAllowed = "Y";
				System.out.println(
						"\n====>Property download.failed.data.reports.allowed value is null or missing hence setting it to default value 'Y'");
			}

			if (downloadFailedDataReportsAllowed.trim().equalsIgnoreCase("Y")) {
				// getting accessControl values for current role & Results module.
				String query = "select a.accessControl from RoleModule a, Module b where idRole = " + idRole
						+ " and a.idTask = b.idTask and b. taskName = 'Results'";

				SqlRowSet roleModuleTable = jdbcTemplate.queryForRowSet(query);
				while (roleModuleTable.next()) {
					sAccessControl = roleModuleTable.getString("accessControl");
				}

				// checking for whether accessContol have D access & sending response.
				if (sAccessControl.contains("D")) {
					status = "success";
					msg = "success";
				} else {
					msg = "User role is not authorised to download failed data report";
				}
			} else {
				msg = "Failed data report download is not enabled";
				System.out.println("\n====>Property download.failed.data.reports.allowed value ["
						+ downloadFailedDataReportsAllowed + "] hence, failed data report download is not allowed");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Error occurred while downloading failed data report file";
		}
		try {
			json.put(status, msg);
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/duplicateEmail", method = RequestMethod.POST)
	public void duplicateEmail(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String email = req.getParameter("val");
		System.out.println("email=" + email);
		String name = userservice.checkDuplicateEmail(email);
		System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter().println(
						"The email address you entered is already in use. Please choose another email address.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/duplicateRoleName", method = RequestMethod.POST)
	public void duplicateRoleName(HttpServletRequest req, HttpServletResponse res, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				res.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String roleName = req.getParameter("val");
		System.out.println("roleName=" + roleName);
		String name = userservice.checkDuplicateRoleName(roleName);
		System.out.println("name=" + name);
		if (name != null) {
			try {
				res.getWriter()
						.println("The Role name you entered is already in use. Please choose another Role name.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/migrateDatabase", method = RequestMethod.POST, produces = "application/json")
	public void migrateDatabase(HttpSession oSession, HttpServletResponse oResponse, HttpServletRequest oRequest,
			@RequestParam int nCallContext) throws Exception {
		JSONObject oMigrationStatus = new JSONObject();
		MigrationManagement.DbMigrationContext oDbMigrationContext = null;
		boolean lValidCallContext = (nCallContext == 1) ? true : false;

		DateUtility.DebugLog("migrateDatabase() 01",
				String.format("Call context parameter/continue %1$s/%2$s", nCallContext, lValidCallContext));

		if (lValidCallContext) {
			oDbMigrationContext = migrationManage.getDbMigrationContext(oRequest.getServletContext());
			if ((oDbMigrationContext == MigrationManagement.DbMigrationContext.FreshDatabase)
					|| (oDbMigrationContext == MigrationManagement.DbMigrationContext.ExistingDatabaseWithChanges)) {
				oMigrationStatus = migrationManage.doDatabaseMigration(oDbMigrationContext);
				DateUtility.DebugLog("migrateDatabase() 02",
						String.format("Migration status Json return value %1$s", oMigrationStatus));
			}
		} else {
			oMigrationStatus = new JSONObject("{ `PageContext`: `-1`, `Msg`: `` }".replace('`', '\"'));
		}

		oResponse.getWriter().println(oMigrationStatus);
		oResponse.getWriter().flush();

		/*
		 * boolean rbac = rbacController.rbac("User Settings", "U", session); if (rbac)
		 * { int migrationStatus = migration.getMigration(); if (migrationStatus > 0) {
		 * model.setViewName("demo"); model.addObject("msg",
		 * "Database Migrated Successfully"); } else { model.setViewName("demo");
		 * model.addObject("msg", "Database Migrate is upto Date"); }
		 * model.addObject("currentSection", "User Settings");
		 * model.addObject("currentLink", "Migrate Database"); return model; } else
		 * return new ModelAndView("loginPage");
		 */
	}

	@RequestMapping(value = "/generateSecureAPI")
	public ModelAndView generateSecureAPI(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "U", session);
		if (rbac) {
			model.setViewName("generateSecureAPI");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "generateSecureAPI");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/licenseInformation")
	public ModelAndView licenseInformation(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "U", session);
		if (rbac) {
			model.setViewName("licenseInformation");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "licenseInformation");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	public static String randomString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 20) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		return salt.toString();
	}

	@RequestMapping(value = "/generateToken")
	public @ResponseBody String generateToken(HttpSession session) {
		String accessToken = randomString();
		String secretAccessToken = randomString();
		userservice.updateIntoSecureAPI(accessToken, secretAccessToken);
		JSONObject json = new JSONObject();
		json.put("accessToken", accessToken);
		json.put("secretAccessToken", secretAccessToken);
		return json.toString();
	}

	/**
	 * Code By : Anant S. Mahale Date : 23-03-2020 Code Update : Anant S. Mahale
	 * Date : 24-03-2020
	 * 
	 * @param session
	 * @return : addNewLocation.jsp page
	 */
	@RequestMapping(value = "/addNewLocation", method = RequestMethod.GET)
	public ModelAndView addNewLocation(ModelAndView model, HttpSession session) {
		try {
			System.out.println("UserSettingsController : addNewLocation ");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage");
			}
			long idUser = (Long) session.getAttribute("idUser");

			System.out.println("UserSettingsController : addNewLocation : idUser :: " + idUser);
			Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
			model.addObject("Roles", Roles);
			model.setViewName("addNewLocation");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "Add New Location");
			return model;
		} catch (Exception e) {
			System.err.println("UserSettingsController : addNewLocation : Exception :: " + e);
			return new ModelAndView("loginPage");
		}

	}

	/**
	 * Code By : Anant S. Mahale Date : 23-03-2020
	 * 
	 * @param session, request , response and modal
	 * @return : addNewLocation.jsp page
	 */
	@RequestMapping(value = "/mapLocationAndValidation", method = RequestMethod.GET)
	public ModelAndView mapLocationAndValidation(ModelAndView model, HttpServletRequest req, HttpServletResponse res,
			HttpSession session) {
		try {
			System.out.println("UserSettingsController : mapLocationAndValidation ");
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				return new ModelAndView("loginPage");
			}
			Long p_Id = (Long) session.getAttribute("projectId");
			String projectId = String.valueOf(p_Id);
			// int intProjectId = Integer.parseInt(projectId);
			Map<Long, String> Roles = userservice.getRoleNameandIdRoleFromRoleTable();
			model.addObject("Roles", Roles);
			model.setViewName("mapLocationAndValidation");
			model.setViewName("mapLocationAndValidation");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "Add New Location");
			if (projectId != null && !projectId.isEmpty()) {
				// int intProjectId = iProjectService.getProjectIdByProjectName(projectId);
				int intProjectId = Integer.parseInt(projectId);
				Map<Integer, String> mapLocationAndLocationId = new HashMap<Integer, String>();
				mapLocationAndLocationId = userservice.getListOfLocationsbyProject(intProjectId);
				Map<Integer, String> mapValidationAndValiatoinId = new HashMap<Integer, String>();
				mapValidationAndValiatoinId = userservice.getListOfValidationsbyProject();
				model.addObject("locationList", mapLocationAndLocationId);
				model.addObject("ValidationList", mapValidationAndValiatoinId);
				return model;
			} else {
				System.err.println("UserSettingsController : mapLocationAndValidation : project id null or empty");
				return model;
			}

		} catch (Exception e) {
			System.err.println("UserSettingsController : mapLocationAndValidation : Exception :: " + e);
			return null;
		}
	}

	/**
	 * Code By : Anant S. Mahale Date : 23-03-2020
	 * 
	 * @param session, request , response and modal
	 * @return : addNewLocation.jsp page
	 *
	 *         Insert Location Record
	 */
	@RequestMapping(value = "/insertNewLocation", method = RequestMethod.POST)
	public void insertNewLocation(ModelAndView model, HttpServletRequest req, HttpServletResponse res,
			HttpSession session) {
		try {
			Object user = session.getAttribute("user");
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					res.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			int intProjectId = 0;
			boolean booleanInsertionStatus = false;
			String strLocationName = req.getParameter("locationname");
			Long projectId = (Long) session.getAttribute("projectId");
			String strProjectId = String.valueOf(projectId);

			if ((strLocationName != null && !strLocationName.isEmpty())
					&& (strProjectId != null && !strProjectId.isEmpty())) {
				System.out.println("UserSettingsController : insertNewLocation : record inserted :: strLocationName :: "
						+ strLocationName);
				System.out.println("UserSettingsController : insertNewLocation : record inserted :: intProjectId :: "
						+ strProjectId);
				// intProjectId = iProjectService.getProjectIdByProjectName(strProjectId);
				intProjectId = Integer.valueOf(strProjectId);
				System.out.println("UserSettingsController : insertNewLocation ");
				StringBuilder strBuildWhereClause = new StringBuilder();
				strBuildWhereClause.append(" locationName = ");
				strBuildWhereClause.append("'" + strLocationName + "'");
				strBuildWhereClause.append(" and projectId = ");
				strBuildWhereClause.append("" + intProjectId + "");

				boolean booleanCheckRecordInsertOrNot = userservice.validateInsertRecord("locations",
						strBuildWhereClause.toString());
				if (booleanCheckRecordInsertOrNot)
					booleanInsertionStatus = userservice.insertLocationRecord(strLocationName, intProjectId);
				else
					System.err.println("UserSettingsController : insertNewLocation : "
							+ "Record already exists. With Location Name :: " + strLocationName + " & Project ID :: "
							+ intProjectId + " You can not entere similar Record");
				System.out.println(
						" UserSettingsController : insertNewLocation : record inserted :: " + booleanInsertionStatus);
			} else {
				System.out.println(
						"UserSettingsController : insertNewLocation : Location Name or ProjecId is null or empty");
			}

			JSONObject json = new JSONObject();
			if (booleanInsertionStatus == true) {
				try {
					// req.setAttribute("message", "Location : "+strLocationName+" successfully
					// saved");
					json.append("success", "Location :" + strLocationName + " successfully saved");
					res.getWriter().println(json);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					json.append("fail", "There was a problem");
					res.getWriter().println(json);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.println("UserSettingsController : insertNewLocation : Exception :: " + e);
		}

	}

	/**
	 * Code By : Anant S. Mahale Date : 23-03-2020
	 * 
	 * @param session, request , response and modal
	 * @return : mapLocationAndValidation.jsp page Insert Location Mapping \
	 */
	@RequestMapping(value = "/insertLocationMapping", method = RequestMethod.POST)
	public void insertLocationMapping(ModelAndView model, HttpServletRequest req, HttpServletResponse res,
			HttpSession session) {
		try {
			System.out.println(" UserSettingsController : insertLocationMapping ");
			Object user = session.getAttribute("user");
			int intexistingValidation = 0;
			int intValidation = 0;
			System.out.println("user:" + user);
			if ((user == null) || (!user.equals("validUser"))) {
				try {
					res.sendRedirect("loginPage.jsp");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String strexistingValidation = req.getParameter("existingValidation"); // to map validation with validation
			boolean booleanCheckReverseValidationToValidationMapping = false;
			boolean booleanCheckReverseLocationToValidationMapping = false;
			String strvalidation = req.getParameter("validation"); // to map validation with location
			int intvalidation = Integer.parseInt(strvalidation);

			String strlocation = req.getParameter("location");

			int intlocation = Integer.parseInt(strlocation); // location name

			if (strexistingValidation != null && !strexistingValidation.isEmpty()) {
				System.out.println(" UserSettingsController : insertLocationMapping : strexistingValidation :: "
						+ strexistingValidation);
				intexistingValidation = Integer.parseInt(strexistingValidation);
				booleanCheckReverseValidationToValidationMapping = userservice
						.checkMappingWithPreviousLocations(intlocation, intexistingValidation);
				if (!booleanCheckReverseValidationToValidationMapping) {
					System.out.println(
							" UserSettingsController : insertLocationMapping : mapped validation with validation is mapped with previous locations, so it could not map");
				}
			} else {
				booleanCheckReverseValidationToValidationMapping = true;
			}

			boolean booleanInsertionStatusWithValidation = false;
			boolean booleanInsertionValidationRecord = false;

			if (strvalidation != null && !strvalidation.isEmpty()) {
				System.out
						.println(" UserSettingsController : insertLocationMapping : strvalidation :: " + strvalidation);
				intValidation = Integer.parseInt(strvalidation);
				booleanCheckReverseLocationToValidationMapping = userservice
						.checkMappingWithPreviousLocations(intlocation, intValidation);
				if (!booleanCheckReverseLocationToValidationMapping) {
					System.out.println(
							" UserSettingsController : insertLocationMapping : mapped location with validation is mapped with previous locations, so it could not map");
				}
			}

			if ((strlocation != null && !strlocation.isEmpty()) && (strvalidation != null && !strvalidation.isEmpty())
					&& booleanCheckReverseValidationToValidationMapping == true
					&& booleanCheckReverseLocationToValidationMapping == true) {

				StringBuilder strBuildLocationMappingWhereClause = new StringBuilder(); // where clause to check
																						// location to validation
																						// mapping exists or not
				strBuildLocationMappingWhereClause.append(" locationId = ");
				strBuildLocationMappingWhereClause.append(intlocation);
				strBuildLocationMappingWhereClause.append(" and idApp = ");
				strBuildLocationMappingWhereClause.append(intvalidation);

				System.out.println(" UserSettingsController : insertLocationMapping : existingValidation :: "
						+ strexistingValidation + " | validation :: " + strvalidation + " | location :: "
						+ strlocation);

				// first param : table name, sec param : where clause. to check record exists in
				// given table or not
				boolean booleanCheckLocationMappingRecordInsertOrNot = userservice
						.validateInsertRecord("locationMapping", strBuildLocationMappingWhereClause.toString());

				if (booleanCheckLocationMappingRecordInsertOrNot) {
					booleanInsertionValidationRecord = true; // flag to insert validation to validation mapping
					booleanInsertionStatusWithValidation = userservice.insertLocationMapping(intvalidation,
							intlocation);
				} else // if location to validation is already exists.
					System.err.println(
							"UserSettingsController : insertLocationMapping : Location Mapping Record already exists as location id :: "
									+ intvalidation + " & idApp :: " + intvalidation
									+ " | also validation mapping is not going to insert ");

				// check idapp is available and check flag from location to validation mapping
				// insertion
				// to insert validation to validation mapping
				if (intexistingValidation > 0 && booleanInsertionValidationRecord) {
					StringBuilder strBuildValidationMappingWhereClause = new StringBuilder();
					strBuildValidationMappingWhereClause.append(" idApp = ");
					strBuildValidationMappingWhereClause.append(intvalidation);
					strBuildValidationMappingWhereClause.append(" and relationIdApp = ");
					strBuildValidationMappingWhereClause.append(intexistingValidation);

					// first param : table name, sec param : where clause. to check record exists in
					// given table or not
					boolean booleanCheckValidationMappingRecordInsertOrNot = userservice
							.validateInsertRecord("validationMapping", strBuildValidationMappingWhereClause.toString());

					if (booleanCheckValidationMappingRecordInsertOrNot)
						userservice.insertValidationMapping(intvalidation, intexistingValidation);
					else
						System.err.println(
								"UserSettingsController : insertValidationMapping : Validation Mapping Record already exists as idApp - id :: "
										+ intvalidation + " & validation id :: " + intvalidation);
				}
			} else {
				System.err.println(
						"UserSettingsController : insertLocationMapping : location or validation is null or empty | Or there is posibility of previous mapping ");
			}

			System.out.println(" UserSettingsController : insertLocationMapping : record inserted :: "
					+ booleanInsertionStatusWithValidation);
			JSONObject json = new JSONObject();
			if (booleanInsertionStatusWithValidation == true) {
				try {
					json.append("success", "Inserted successfully");
					res.getWriter().println(json);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					json.append("fail", "There was a problem");
					res.getWriter().println(json);
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.println(" UserSettingsController : insertLocationMapping  : Exception :: " + e);
		}

	}

	@RequestMapping(value = "/dimensionViewList")
	public ModelAndView dimensionViewList(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		String isRuleCatalogDiscovery = appDbConnectionProperties.getProperty("isRuleCatalogDiscovery");

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "C", session);
		if (rbac) {

			model.setViewName("dimensionListViewManagement");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "View Dimension");
			model.addObject("IsRuleCatalogDiscovery", isRuleCatalogDiscovery);
			return model;
		} else {
			return new ModelAndView("loginPage");
		}
	}

	@RequestMapping(value = "/saveDimensionRecord", method = RequestMethod.POST, produces = "application/json")
	public void saveDimensionRecord(@RequestParam String DimensionRecordToSave, HttpSession oSession,
			HttpServletResponse oResponse) {
		JSONObject oDimensionRecordToSave = new JSONObject();
		oDimensionRecordToSave = new JSONObject(DimensionRecordToSave);

		JSONObject oJsonResponse = new JSONObject();

		boolean lNewDomain = (oDimensionRecordToSave.getString("DimensionId").equalsIgnoreCase("-1")) ? true : false;
		String sInsertSql = "insert into dimension (dimensionName) values ('%1$s');";
		String sUpdateSql = "update dimension set dimensionName = '%1$s' where idDimension = %2$s;";
		String sSqlToUpdate = "";
		String sDuplicateMsg = "Duplicate dimension is not allowed.";

		try {

			if (lNewDomain) {
				sSqlToUpdate = String.format(sInsertSql, oDimensionRecordToSave.getString("DimensionName"));
			} else {
				sSqlToUpdate = String.format(sUpdateSql, oDimensionRecordToSave.getString("DimensionName"),
						oDimensionRecordToSave.getString("DimensionId"));
			}
			jdbcTemplate.update(sSqlToUpdate);

			oJsonResponse.put("Result", true);
			oJsonResponse.put("Msg", "Dimension Record Successfully Saved");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();

			DateUtility.DebugLog("SaveDimensionRecordList 03",
					String.format("Pushed response to UI as \n%1$s\n", oJsonResponse.toString()));
		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			oException.printStackTrace();
			oJsonResponse.put("Result", false);
			oJsonResponse.put("Msg",
					((sExceptionMsg.toLowerCase().indexOf("duplicate") > -1) ? sDuplicateMsg : sExceptionMsg));
		}
	}

	@RequestMapping(value = "/loadDimensionRecordList", method = RequestMethod.POST, produces = "application/json")
	public void loadDimensionRecordList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadDimensionList 01", "Begin controller processing for dimension list");

			oJsonResponse = getDimensionPageData();

			DateUtility.DebugLog("loadDimensionList 02", "Got data sending to client");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}

	}

	private JSONObject getDimensionPageData() throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] { "DimensionId", "DimensionName", "DimensionId:edit",
				"DimensionId:delete" };

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();
		ObjectMapper oMapper = new ObjectMapper();

		try {
			sDataSql = "";
			sDataSql = sDataSql + "select idDimension as DimensionId, dimensionName as DimensionName from dimension";

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			String sIsEDimensionpresentSql = "select idDimension, dimensionName from dimension;";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sIsEDimensionpresentSql);
			if (queryForRowSet.next()) {
				oJsonRetValue.put("sEDimensionPresenntId", "check-" + queryForRowSet.getInt("idDimension"));
			}

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	@RequestMapping(value = "/deleteDimensionRecord", method = RequestMethod.POST, produces = "application/json")
	public void deleteDimensionRecord(@RequestParam String DimensionIdToDelete, HttpSession oSession,
			HttpServletResponse oResponse) {

		JSONObject oJsonResponse = new JSONObject();
		try {
			DateUtility.DebugLog("deleteDimensionRecord 01",
					"Begin controller processing for delete dimension Dimension Id = " + DimensionIdToDelete);
			oJsonResponse = deletDimensionData(DimensionIdToDelete);
			DateUtility.DebugLog("deleteDimensionRecord 02", "Deleted dimension sending response");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}

	}

	private JSONObject deletDimensionData(String DimensionIdToDelete) throws Exception {
		JSONObject oJsonRetValue = new JSONObject();

		String deleteSql = "delete from dimension where idDimension = %1$s";

		try {
			deleteSql = String.format(deleteSql, DimensionIdToDelete);
			jdbcTemplate.update(deleteSql);
			oJsonRetValue.put("Result", true);
			oJsonRetValue.put("Msg", "Dimension Record Successfully Deleted");
			return oJsonRetValue;

		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			oException.printStackTrace();
			oJsonRetValue.put("Result", false);
			oJsonRetValue.put("Msg", sExceptionMsg);
			return oJsonRetValue;
		}

	}

	/*
	 * Load MVC view i.e. JSP page for login group to role and projects membership
	 * management
	 */
	@RequestMapping(value = "/loginGroupMapping", method = RequestMethod.GET)
	public ModelAndView getComponentAccessControl(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("loginGroupMapping");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "loginGroupMapping");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/loadLoginGroupMappingList", method = RequestMethod.POST, produces = "application/json")
	public void loadLoginGroupMappingList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			DateUtility.DebugLog("loadLoginGroupMappingList 01", "Begin controller");
			oJsonResponse = userSettingService.getLoginGroupMappingList(null);
			DateUtility.DebugLog("loadLoginGroupMappingList 02", "End controller");
			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	/*
	 * Save login group data along with roles and projects to which it will be
	 * mapped
	 */
	@RequestMapping(value = "/saveLoginGroupData", method = RequestMethod.POST, produces = "application/json")
	public void saveLoginGroupData(HttpSession oSession, @RequestParam String sDataToSave,
			HttpServletResponse oResponse) throws IOException {

		JSONObject oJsonResponse = new JSONObject();
		JSONObject oDataToSave = null;
		boolean lStatus = true;
		String sMsg = "";

		DateUtility.DebugLog("saveLoginGroupDatal 01", String.format("Begin controller with '%1$s''", sDataToSave));

		try {
			oDataToSave = new JSONObject(sDataToSave);

			if (userSettingService.isLoginLdapGroupExists(oDataToSave.getString("GroupName"))) {
				sMsg = userSettingService.saveLoginGroupAndRoleProjectMapping(oDataToSave, (Long) oSession.getAttribute("idUser"), (String) oSession.getAttribute("firstName"));
				lStatus = true;

			} else {
				lStatus = false;
				sMsg = "Entered login group name does not exists in LDAP server";
			}

			oJsonResponse.put("Status", lStatus);
			oJsonResponse.put("Msg", sMsg);

		} catch (Exception oException) {
			String sErrorMsg = (oException.getMessage().toLowerCase().indexOf("duplicate") > -1)
					? "Duplicate group name not allowed"
					: oException.getMessage();

			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", sErrorMsg);
			oException.printStackTrace();
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();

		DateUtility.DebugLog("saveLoginGroupDatal 02", "End controller");
	}

	/*
	 * Two methods below (Not_Used appended) to be removed as groupRoleMapAddnew.jsp
	 * replaced with loginGroupMapping.jsp
	 */

	@RequestMapping(value = "/groupRoleMapAddnew_Not_Used", method = RequestMethod.GET)
	public ModelAndView getGroupRoleMapAddnew(HttpServletRequest request, ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long projectId = (Long) session.getAttribute("projectId");
		boolean rbac = rbacController.rbac("User Settings", "C", session);
		if (rbac) {

			model.setViewName("groupRoleMapAddnew");

			try {

				ArrayList<String> LdapGroups = loginService.getAllRoleNamefromActiveDirectory();
				/*
				 * ArrayList<String> LdapGroups = new ArrayList<String>(); LdapGroups.add("BA");
				 * LdapGroups.add("Approver"); LdapGroups.add("RWI");
				 */

				Set<String> ldapGroupSet = new LinkedHashSet<>();
				ldapGroupSet.addAll(LdapGroups);
				LdapGroups.clear();
				LdapGroups.addAll(ldapGroupSet);

				model.addObject("LdapGroups", LdapGroups);
			} catch (Exception e) {
				e.printStackTrace();
			}

			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "LdapGroupRoleMap");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/SaveGroupRoleRecord_Not_Used", method = RequestMethod.POST, produces = "application/json")
	public void SaveGroupRoleRecord(@RequestParam String GroupRoleRecordToSave, HttpSession oSession,
			HttpServletResponse oResponse) {
		JSONObject oGroupRoleRecordToSave = new JSONObject();
		oGroupRoleRecordToSave = new JSONObject(GroupRoleRecordToSave);

		JSONObject oJsonResponse = new JSONObject();

		String sInsertSql = "insert into databuck_security_matrix (ldap_group_name, idRole) values ('%1$s', %2$s);";
		String sUpdateSql = "update databuck_security_matrix set idRole = %1$s where ldap_group_name = '%2$s';";
		String sSqlToUpdate = "";

		String sIsGroupPresentSql = "select count(*) as Count from databuck_security_matrix where ldap_group_name = '"
				+ oGroupRoleRecordToSave.getString("ADGroupName") + "'";

		try {

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sIsGroupPresentSql);

			if (queryForRowSet.next()) {
				if (queryForRowSet.getInt("Count") == 0) {
					sSqlToUpdate = String.format(sInsertSql, oGroupRoleRecordToSave.getString("ADGroupName"),
							oGroupRoleRecordToSave.getInt("Role"));
				} else {
					sSqlToUpdate = String.format(sUpdateSql, oGroupRoleRecordToSave.getInt("Role"),
							oGroupRoleRecordToSave.getString("ADGroupName"));
				}

			}
			jdbcTemplate.update(sSqlToUpdate);
			oJsonResponse.put("Result", true);
			oJsonResponse.put("Msg", "ADGroup-Role Mapped Successfully");

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();

		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			oException.printStackTrace();
			oJsonResponse.put("Result", false);
			oJsonResponse.put("Msg", sExceptionMsg);
		}
	}

	@RequestMapping(value = "/defectCodesView")
	public ModelAndView defectCodesView(HttpServletRequest oRequest, HttpSession oSession) {
		Object oUser = oSession.getAttribute("user");
		boolean lModuleAccess = rbacController.rbac("User Settings", "R", oSession);
		ModelAndView oModelAndView = null;

		if (lModuleAccess) {
			oModelAndView = new ModelAndView("defectCodesViewManagement");

			oModelAndView.addObject("currentSection", "User Settings");
			oModelAndView.addObject("currentLink", "defectCodesView");
		} else {
			oModelAndView = new ModelAndView("loginPage");
		}
		return oModelAndView;
	}

	@RequestMapping(value = "/loadDefectCodeViewList", method = RequestMethod.POST, produces = "application/json")
	public void loadDefectCodeViewList(HttpSession oSession, HttpServletResponse oResponse) {
		JSONObject oJsonResponse = new JSONObject();

		try {
			oJsonResponse = loadDefectCodeDataList(oSession);

			oResponse.getWriter().println(oJsonResponse);
			oResponse.getWriter().flush();
		} catch (Exception oException) {
			oException.printStackTrace();
		}
	}

	private JSONObject loadDefectCodeDataList(HttpSession oSession) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] { "RowId", "DefectCode", "DefectDescription", "DimensionId",
				"DimensionName", "RowId:edit", "RowId:delete" };

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		try {
			sDataSql = "";
			sDataSql = sDataSql
					+ "select dc.row_id as RowId, dc.defect_code as DefectCode, dc.dimension_id as DimensionId, \n";
			sDataSql = sDataSql + "dc.defect_description as DefectDescription, dm.dimensionName as DimensionName \n";
			sDataSql = sDataSql + "from defect_codes dc, dimension dm \n";
			sDataSql = sDataSql + "where dc.dimension_id = dm.idDimension \n";

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "", null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DefectCodeViewList", aJsonDataList);

			/* Put all Dimension onto return JSON */
			sDataSql = "";
			sDataSql = sDataSql + "select * from\n";
			sDataSql = sDataSql + "(\n";
			sDataSql = sDataSql
					+ "select idDimension as row_id, dimensionName as element_reference, dimensionName as element_text, 0 is_default, idDimension as position \n";
			sDataSql = sDataSql + "from dimension \n";
			sDataSql = sDataSql + ") core_qry;";

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);

			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DimensionList", aJsonDataList);

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return oJsonRetValue;
	}

	@RequestMapping(value = "/mainDefectCodeHandler", method = RequestMethod.POST, produces = "application/json")
	public void mainDefectCodeHandler(HttpSession oSession, @RequestParam String DefectCodeData,
			HttpServletResponse oResponse) throws IOException {
		JSONObject oWrapperData = new JSONObject(DefectCodeData);
		JSONObject oDefectCodeData = null;
		JSONObject oJsonResponse = new JSONObject();
		String sContext;
		String sMsg = "Sample server call";
		boolean lStatus = true;

		boolean lNewReport = false;

		try {
			sContext = oWrapperData.getString("Context");
			oDefectCodeData = oWrapperData.getJSONObject("Data");
			DateUtility.DebugLog("Defect Code Data 01",
					String.format("Begin controller Context = %1$s, Data = %2$s", sContext, oDefectCodeData));
			/* fix for ' character in defect save */
			if (oDefectCodeData.getString("DefectDescription") != null) {
				oDefectCodeData.put("DefectDescription",
						oDefectCodeData.getString("DefectDescription").trim().replace("'", "\\'"));
			}

			switch (sContext) {

			case "saveDefectCodeRecord":

				DateUtility.DebugLog("Defect Code Data 02", oDefectCodeData.getString("RowId"));
				lNewReport = (Integer.parseInt(oDefectCodeData.getString("RowId")) < 0) ? true : false;
				DateUtility.DebugLog("Defect Code Data 03", String.format("%1$s", lNewReport));

				// check Vulnerable Data even it is checked in Java Script so database is
				// absolutely safe
				sMsg = validateVulnerableData(oDefectCodeData);

				if (sMsg.length() > 0) {
					lStatus = false;
				} else if (lNewReport) {
					sMsg = createDefectCodeRecord(oDefectCodeData);
				} else {
					sMsg = updateExistingDefectCodeRecord(oDefectCodeData);
				}
				break;

			case "deleteDefectCodeRecord":
				sMsg = deleteDefectCodeRecord(oDefectCodeData);
				break;

			default:
			}

			oJsonResponse.put("Status", lStatus);
			oJsonResponse.put("Msg", sMsg);

			DateUtility.DebugLog("Defect Code Data 02", "End controller");

		} catch (Exception oException) {
			oException.printStackTrace();
			oJsonResponse.put("Status", false);
			oJsonResponse.put("Msg", oException.getMessage());
		}
		oResponse.getWriter().println(oJsonResponse);
		oResponse.getWriter().flush();
	}

	private String validateVulnerableData(JSONObject oDefectCodeData) {
		String sRetMsg = "";

		if (checkVulnerability.getVulnerabilityTagCounts(
				oDefectCodeData.getString("DefectDescription") + ' ' + oDefectCodeData.getString("DefectCode")) > 0) {
			sRetMsg = "Submitted Defect data record contains Vulnerable Contents, cannot save it to database.";
		}
		return sRetMsg;
	}

	private String createDefectCodeRecord(JSONObject oDefectCodeData) {
		String sRetMsg = "", sInsertSql = "";

		try {
			boolean isCombinationExist = iUserDAO.getDefectCodeAndDiamensionId(oDefectCodeData.getString("DefectCode"), Integer.parseInt(oDefectCodeData.getString("DimensionId")));
			if (isCombinationExist) {
				sRetMsg = "Defect Code and Dimension Name combination already exists";
			} else {
				sInsertSql = String.format(
						"insert into defect_codes(defect_code, defect_description, dimension_id) values ('%1$s', '%2$s', %3$s);",
						oDefectCodeData.getString("DefectCode"), oDefectCodeData.getString("DefectDescription"),
						oDefectCodeData.getString("DimensionId"));
				jdbcTemplate.update(sInsertSql);
				sRetMsg = "Successfully saved new Defect Code Record";
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			sRetMsg = "Error occurred while inserting defect code record";
		}
		return sRetMsg;
	}

	private String updateExistingDefectCodeRecord(JSONObject oDefectCodeData) {
		String sRetMsg = "", sUpdateSql = "";
		try {
			boolean isCombinationExist = iUserDAO.getDefectCodeAndDiamensionId(oDefectCodeData.getString("DefectCode"), Integer.parseInt(oDefectCodeData.getString("DimensionId")));
			if (isCombinationExist) {
				sRetMsg = "DefectCode and DimensionId combination is already exists";
			} else {
				sUpdateSql = String.format(
						"update defect_codes set defect_description = '%1$s', dimension_id = %2$s where row_id = %3$s",
						oDefectCodeData.getString("DefectDescription"), oDefectCodeData.getString("DimensionId"),
						oDefectCodeData.getString("RowId"));
				jdbcTemplate.update(sUpdateSql);
				sRetMsg = "Successfully updated Defect Code Record";
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			sRetMsg = "Error occurred while updating defect code record";
		}
		return sRetMsg;
	}

	private String deleteDefectCodeRecord(JSONObject oDefectCodeData) {
		String sRetMsg = "", sDeleteSql = "";
		try {
			sDeleteSql = String.format("delete from defect_codes where row_id = %1$s",
					oDefectCodeData.getString("RowId"));
			jdbcTemplate.update(sDeleteSql);
			sRetMsg = "Successfully deleted Defect Code Data";
		} catch (Exception oException) {
			oException.printStackTrace();
			sRetMsg = "Error occurred while deleting defect code record";
		}
		return sRetMsg;
	}

}
