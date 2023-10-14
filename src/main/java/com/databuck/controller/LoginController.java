package com.databuck.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.dao.ILoginDAO;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.UserLDAPGroupHolder;
import com.databuck.bean.ChangePassword;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.service.IProjectService;
import com.databuck.service.IUserService;
import com.databuck.service.LoginService;
import com.databuck.service.RBACController;
import com.databuck.util.ExportUtility;
import com.databuck.databasemigration.MigrationManagement;
import com.databuck.econstants.DatabuckPropertyCategory;
import com.databuck.util.DateUtility;

@Controller
public class LoginController {

	static Logger logger = Logger.getLogger(LoginController.class.getName());

	@Autowired
	public LoginService loginService;
	@Autowired
	public IUserService iUserService;
	@Autowired
	public IProjectService projService;
	@Autowired
	private RBACController rbacController;
	@Autowired
	public Properties licenseProperties;
	@Autowired
	public ExportUtility exportUtility;
	@Autowired
	private Properties activeDirectoryConnectionProperties;
	@Autowired
	private Properties appDbConnectionProperties;
	@Autowired
	private MigrationManagement migrationManage;
	@Autowired
	public ITaskDAO taskDao;
	@Autowired
	IUserDAO userDAO;

	@Autowired
	ILoginDAO iLoginDAO;

	@Autowired
	private IUserDAO iUserdao;

	@RequestMapping(value = "/welcomePage", method = RequestMethod.GET)
	public ModelAndView welcomePage(HttpSession session) {
		System.out.println("this is welcomePage");
		ModelAndView modelAndView = new ModelAndView("welcomePage");
		Map<String, String> module = (Map<String, String>) session.getAttribute("module");
		System.out.println("module=" + module);

		// Check if the user has access to only "Dashboard" tab
		// Then redirect him to Report UI page directly
		if (module.size() == 1 && module.containsKey("Dashboard")) {
			modelAndView = new ModelAndView("dashboardConsole");
			String averReportUILink = appDbConnectionProperties.getProperty("aver.report.link");
			modelAndView.addObject("averReportUILink", averReportUILink);
			modelAndView.addObject("currentSection", "DashboardConsole");
		} else {
			if (module.containsKey("Data Template")) {
				modelAndView.addObject("currentSection", "Data Template");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("Data Connection")) {
				modelAndView.addObject("currentSection", "Data Connection");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("Extend Template & Rule")) {
				modelAndView.addObject("currentSection", "Extend Template");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("Validation Check")) {
				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("Tasks")) {
				modelAndView.addObject("currentSection", "Tasks");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("Results")) {
				modelAndView.addObject("currentSection", "Dashboard");
				modelAndView.addObject("currentLink", "View");
			} else if (module.containsKey("User Settings")) {
				modelAndView.addObject("currentSection", "User Settings");
				modelAndView.addObject("currentLink", "View Modules");
			} else if (module.containsKey("Dashboard")) {
				modelAndView.addObject("currentSection", "Dashboard View");
				modelAndView.addObject("currentLink", "DashboardViews");
			}
			/*
			 * else if (module.containsKey("Extend Template & Rule")) {
			 * modelAndView.addObject("currentSection", "Global Rule");
			 * modelAndView.addObject("currentLink", "View"); }
			 */
		}
		return modelAndView;
	}

	@RequestMapping(value = "/loginPage", method = RequestMethod.GET)
	public ModelAndView getLoginPage() {
		logger.info("This is login page");
		System.out.println("this is login page req");
		Date d = new Date();
		System.out.println("Todays Date=" + d);
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		ModelAndView modelandview = new ModelAndView("loginPage");
		modelandview.addObject("ADflag", activeDirectoryFlag);
		return modelandview;

		// return new ModelAndView("loginPage");
	}

	/*
	 * @RequestMapping(value = "/login_process", method = RequestMethod.GET) public
	 * ModelAndView getlogin_process() { logger.info( "This is login succes");
	 * System.out.println("this is login process req"); return new
	 * ModelAndView("loginPage"); }
	 */

	@RequestMapping(value = "/login_process", method = RequestMethod.POST)
	public ModelAndView loginPage(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletRequest request, HttpSession session) {

		/*
		 * HttpSession oldSession = request.getSession(false); if (oldSession != null) {
		 * oldSession.invalidate(); } //generate a new session HttpSession session =
		 * request.getSession(true);
		 */

		session.setAttribute("csrfToken", loginService.generateCSRFToken());

		// Active for 10 min
		/* session.setMaxInactiveInterval(10*60); */

		ArrayList<String> alist = new ArrayList<String>();
		String msg = "The email or password you entered is incorrect";
		String licenseMsg = "License expired - please contact info@firsteigen.com";
		// activedirectory flag check
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		if (activeDirectoryFlag == null)
			activeDirectoryFlag = "N";
		if (activeDirectoryFlag != null && activeDirectoryFlag.trim().equalsIgnoreCase("Y")) {
			msg = msg.replace("email", "UID");
		}

		boolean lByPassMigration = migrationManage.isByPassMigrationEnabled();
		String sByPassMigrationMsg = "";
		ModelAndView oMigrationView = new ModelAndView();

		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

		/*
		 * Pradeep 7-Jan-2020 - Intercept login process to check is fresh customer with
		 * full import DataBuck schema import or it need upgrade?
		 */
		String sPageContext = null;
		MigrationManagement.DbMigrationContext oDbMigrationContext = migrationManage
				.getDbMigrationContext(request.getServletContext());

		// Default value is N
		session.setAttribute("freshDBSetUpRestartRequired", "N");

		if (oDbMigrationContext == MigrationManagement.DbMigrationContext.FreshDatabase) {
			session.setAttribute("freshDBSetUpRestartRequired", "Y");

			if (lByPassMigration) {
				sByPassMigrationMsg = "The DataBuck product need to import all database schemas before you use it.CRLFCRLFManual DB Migration is enabled, kindly setup databases manually and restart server.";
				sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
			} else {
				sPageContext = "{ `PageContext`: 0, `Msg`: `` }".replace('`', '\"');
			}

		} else if (oDbMigrationContext == MigrationManagement.DbMigrationContext.ExistingDatabaseWithChanges) {
			sPageContext = "{ `PageContext`: 1, `Msg`: `{0}` }".replace('`', '\"');

			try {
				if (lByPassMigration) {
					sByPassMigrationMsg = migrationManage.getByPassMigrationDisplableMsg(oDbMigrationContext);
					sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
				}
			} catch (Exception oException) {
				oException.printStackTrace();
			}
			DateUtility.DebugLog("LoginProcess Bypass Issue", String
					.format("Bypass migration = '%1$s', Bypass msg = '%2$s'", lByPassMigration, sByPassMigrationMsg));

		} else if ((oDbMigrationContext == MigrationManagement.DbMigrationContext.InconsistentDatabase)
				|| (oDbMigrationContext == MigrationManagement.DbMigrationContext.ErrorOccured)) {
			sPageContext = "{ `PageContext`: -2, `Msg`: `` }".replace('`', '\"');
		}

		if (sPageContext != null) {
			oMigrationView = new ModelAndView("DatabaseMigration");
			oMigrationView.addObject("PageContext", sPageContext);
			oMigrationView.addObject("ByPassMigrationMsg", sByPassMigrationMsg);
			new ModelAndView("DatabaseMigration", "PageContext", sPageContext); // pass json data to page

			DateUtility.DebugLog("LoginProcess 01", String.format(
					"Database schema needs import or upgrade as '%1$s' detected, redirecting to database migration page",
					oDbMigrationContext));
			return oMigrationView;

		} else {
			DateUtility.DebugLog("LoginProcess 02",
					"Database schema is up-to-date no schema changes were detected, continuing to BAU login process ...");
		}

		if (email != null && email.trim().length() > 0 && password != null && password.trim().length() > 0) {
			Long login_log_row_id = 0l;
			if (activeDirectoryFlag == null || activeDirectoryFlag.trim().equals("N")) {

				// Insert into DatabuckLoginAccessLog
				login_log_row_id = iLoginDAO.insertDatabuckLoginAccessLog(null, email, "/login_process",
						session.getId(), "login", "");

				List<DomainProject> dplist = new ArrayList<DomainProject>();
				Long idUser = loginService.userAuthentication(email, password);

				if (idUser != null) {

					// Update status in DatabuckLoginAccessLog
					iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, idUser, "passed");

					if (loginService.checkDaysLeftForLicenseRenewal(session) < 0) {

						String firstName = loginService.getFirstNameFromUserTable(idUser);
						session.setAttribute("firstName", firstName);
						// session = request.getSession();
						// session.setMaxInactiveInterval(604800);
						session.setAttribute("user", "validUser");
						System.out.println("in login pro:" + session.getAttribute("user"));
						session.setAttribute("email", email);
						session.setAttribute("idUser", idUser);
						System.out.println("idUser" + idUser);
						Long idRole = loginService.getRolesFromUserRoleTable(idUser);
						System.out.println("idRole=" + idRole);
						session.setAttribute("idRole", idRole);
						String Rolename;
						Rolename = loginService.getRoleFromRoleTable(idRole);
						session.setAttribute("Role", Rolename);
						SqlRowSet roleModuleTable = loginService.getIdTaskandAccessControlFromRoleModuleTable(idRole);
						long idTask = 0l;
						Map<String, String> module = new LinkedHashMap<String, String>();
						System.out.println("module in login controller" + module);
						while (roleModuleTable.next()) {
							idTask = roleModuleTable.getLong("idTask");
							// System.out.println("idTask="+idTask+"accessControl="+roleModuleTable.getString("accessControl"));
							String taskName = loginService.getTaskNameFromModuleTable(idTask);
							module.put(taskName, roleModuleTable.getString("accessControl"));
						}
						System.out.println("module in login controller" + module);
						// exportUtility.exportSelectedData("VC", 1540L, "export-file.csv");
						/*
						 * for (Map.Entry m : module.entrySet()) {
						 * System.out.println("idTask="+m.getKey()+"accessControl="+m. getValue()); }
						 */
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date today = new Date();
						Calendar cal = new GregorianCalendar();
						cal.setTime(today);
						String curr_today = dateFormat.format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, -30);
						String curr_today_30 = dateFormat.format(cal.getTime());

						System.out.println("fromDate" + curr_today_30);
						System.out.println("toDate" + curr_today);
						String dateFilter = "";
						dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
						session.setAttribute("dateFilter", dateFilter);
						session.setAttribute("toDate", curr_today_30);
						session.setAttribute("fromDate", curr_today);
						session.setAttribute("RunFilter", 0);
						session.setAttribute("module", module);
						ModelAndView modelandview = new ModelAndView("loginSuccess");
						List<Project> projList = projService.getAllProjectsOfAUser(email);
						System.out.println("projList ->>" + projList);
						session.setAttribute("userProjectList", projList);
						/*
						 * if(projList.size() > 0){ session.setAttribute("projectId",
						 * projList.get(0).getIdProject()); session.setAttribute("projectName",
						 * projList.get(0).getProjectName()); session.setAttribute("isOwner",
						 * projList.get(0).getIsOwner()); }
						 */

						dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);
						session.setAttribute("domainProjectList", dplist);
						if (dplist.size() > 0) {
							session.setAttribute("projectId", Long.valueOf(dplist.get(0).getIdProject()));
							session.setAttribute("domainId", dplist.get(0).getDomainId());
							session.setAttribute("projectName", dplist.get(0).getProjectName());
							session.setAttribute("domainName", dplist.get(0).getDomainName());
						}
						// Added for Adapter implementation as authorized access
						List<String> componentList = loginService.getComponentListofRole(idRole);
						if (componentList != null && componentList.size() > 0) {
							session.setAttribute("componentList", componentList);
						}
						modelandview.addObject("currentSection", "Results");
						modelandview.addObject("currentLink", "View");
						return modelandview;
					} else {

						// Check if it is fresh database setup and restart is required
						String freshDBSetUpRestartRequired = (String) session
								.getAttribute("freshDBSetUpRestartRequired");

						if (freshDBSetUpRestartRequired != null
								&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
							msg = "Kindly restart the application and login";
							return new ModelAndView("loginPage", "msg", msg);
						} else {
							System.out.println(licenseMsg);

							// Show renew license page
							return new ModelAndView("renewLicense", "msg", licenseMsg);
						}
					}
				} else {

					System.out.println(msg);

					// Update status in DatabuckLoginAccessLog
					iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, idUser, "failed");

					return new ModelAndView("loginPage", "msg", msg);
				}
			} else {
				if (loginService.checkDaysLeftForLicenseRenewal(session) < 0) {
					try {
						// Insert into DatabuckLoginAccessLog
						login_log_row_id = iLoginDAO.insertDatabuckLoginAccessLog(null, email, "/login_process",
								session.getId(), "login", "");

						User user = new User();
						// user =loginService.userActiveDirectoryAuthentication(email, password);
						List<DomainProject> dplist = new ArrayList<DomainProject>();
						HashMap<String, String> oProgramOutput = null;
						HashMap<String, String> oProgramOutput1 = null;
						String databuckHome = loginService.getDatabuckHome();
						String sCmdLine = databuckHome + "/scripts/ldap_login.sh";
						String sCmdLine1 = databuckHome + "/scripts/ldap_auth.sh";
						String domainname = "";
						String principal = activeDirectoryConnectionProperties.getProperty("principal");
						String Adminpasssword = activeDirectoryConnectionProperties.getProperty("credentials");

						System.out.println("\n=====> Script command: " + databuckHome + "/scripts/ldap_login.sh "
								+ email + " xxxxxxx xxxxxxx\n");
						String[] ldap_login_cmd_args = { sCmdLine, email, Adminpasssword, principal };

						oProgramOutput = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_login_cmd_args,
								new String[] { "result: 0 Success" });
						System.out.println("oProgramOutput : " + oProgramOutput);

						if (oProgramOutput.get("Program Result").equalsIgnoreCase("1")) {
							String ProgramOutput = oProgramOutput.get("Program Std Out");
							String[] arrOfStr = ProgramOutput.split("\n");
							String previousLine = null;
							for (String a : arrOfStr) { // dn: //getting DN from cn
								/*
								 * if (a.startsWith("dn:")) { String[] groupname = a.split(":"); domainname =
								 * groupname[1]; break; }
								 */
								if (previousLine != null) {
									// compare
									if (a.startsWith("memberOf:")) {

										String[] groupname = previousLine.split(":");
										domainname = groupname[1];
										System.out.println("1");
										break;
									} else {
										previousLine = previousLine.trim() + "" + a.trim();
										String[] groupname = previousLine.split(":");
										domainname = groupname[1];
										System.out.println("2");
										break;
									}
								}
								if (a.startsWith("dn:")) {
									previousLine = a;
								}

							}
						}

						if (domainname.equalsIgnoreCase("")) {
							System.out.println("invalid cn entered (DN not found): " + email);
							System.out.println(msg);
							return new ModelAndView("loginPage", "msg", msg);
						} else // authentication block
						{
							System.out.println("  (DN  found for entered CN ): " + domainname);
							String[] ldap_auth_cmd_args = { sCmdLine1, email, password, principal, Adminpasssword };
							System.out.println("\n=====> Script command: " + databuckHome + "/scripts/ldap_auth.sh "
									+ email + " xxxxxxx xxxxxxx xxxxxxx\n");
							oProgramOutput1 = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_auth_cmd_args,
									new String[] { "Result: Success (0)" });
							System.out.println("oProgramOutput1 :   " + oProgramOutput1);
							if (oProgramOutput1.get("Program Result").equalsIgnoreCase("1")) {
								System.out.println("User authenticated succesfully  ");
								System.out.println("loadComponentAccessControlViewList1 02 " + oProgramOutput1);

								// Update status in DatabuckLoginAccessLog
								iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "passed");
							}

							else {
								System.out.println("Invalid credentials for dn/password : " + domainname);
								System.out.println(msg);

								// Update status in DatabuckLoginAccessLog
								iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "failed");
								return new ModelAndView("loginPage", "msg", msg);
							}
						}

						String ProgramOutput = oProgramOutput.get("Program Std Out");

						alist = loginService.getgroupfrom_Program_Std_Out(ProgramOutput);
						session.setAttribute("UserLDAPGroups", String.join(",", alist)); // Pradeep 10-Oct-2020 for LDAP
																							// groups to project mapping

						// Save user and ldapGroup Mapping in holder
						UserLDAPGroupHolder.addOrUpdateUserLDAPGroup(email, String.join(",", alist));

						/* [29-Sep-2020]:Changes for LDAP group role mapping Starts */
						String defaulAdminLdapGroup = activeDirectoryConnectionProperties
								.getProperty("default_ldap_admin");
						String defaulAdminLdapRole = activeDirectoryConnectionProperties
								.getProperty("default_ldap_role");

						boolean isLdapAdmin = false;
						for (String group : alist) {
							if (defaulAdminLdapGroup.equals(group)) {
								isLdapAdmin = true;
							}
						}

						String Rolename = "";
						Long idRole = 0l;
						HashMap<Long, String> RoleData = new HashMap<Long, String>();
						List<Project> projList = new ArrayList<Project>();

						if (!isLdapAdmin)// compare groupOf user with adminGroup present in property
						{
							try {

								RoleData = loginService.getRoleDataFromLdapAfterLogin(alist);
								Rolename = (String) RoleData.values().toArray()[0];
								idRole = (Long) RoleData.keySet().toArray()[0];
								System.out.println("\nRolename : " + Rolename + "\tidRole : " + idRole);

								/*
								 * Pradeep 26-Mar-2021 changes so session can contain as many roles mapped in
								 * DataBuck to logged in user's LDAP member group
								 */
								String sBelongsToRoles = loginService.getBelongsToRoles(RoleData);
								session.setAttribute("BelongsToRoles", sBelongsToRoles);
								System.out.println(String.format(
										"Session Values for Roles 'idRole / Rolename' = '%1$s / %2$s' ,'BelongsToRoles' = '%3$s'",
										idRole, Rolename, sBelongsToRoles));

								HashMap<Long, String> ProjectData = loginService
										.getProjectDataFromLdapAfterLogin(alist);
								System.out.println("\nProjectname : " + (String) ProjectData.values().toArray()[0]
										+ "\tProjectid : " + (Long) ProjectData.keySet().toArray()[0]);
								projList = loginService.getProjectListOfUser(alist);

							} catch (Exception e) {
								System.out.println("No role and projects are found mapped to group ");
								return new ModelAndView("loginPage", "msg",
										"No role and projects are found mapped to group");
							}
						} else {
							if (defaulAdminLdapGroup.equalsIgnoreCase("") || defaulAdminLdapRole.equalsIgnoreCase("")) {
								System.out.println("default_ldap_admin, default_ldap_role properties are missing");
								return new ModelAndView("loginPage", "msg",
										"default_ldap_admin, default_ldap_role properties are missing");
							}
							Rolename = defaulAdminLdapRole;
							idRole = loginService.getRoleIdFromRoleTable(Rolename);
							projList = loginService.getProjectListOfUser(alist);
						}

						boolean isUserPresent = loginService.getIsUserPresent(email);
						if (!isUserPresent) {
							boolean newUserRecordInsertedFlag = loginService.insertNewUserRecord(idRole, email,
									password, alist);
							System.out.println("newUserRecordInsertedFlag: " + newUserRecordInsertedFlag);
						}

						user = userDAO.getUserDataByName(email);
						/* [29-Sep-2020]:Changes for LDAP group role mapping Ends */
						System.out.println("User object in Login_process-->" + user);
						session.setAttribute("idUser", user.getIdUser());
						// session.setAttribute("idUser", user.getEmail());
						System.out.println("idUser -> " + user.getEmail());
						String defaultrole = activeDirectoryConnectionProperties.getProperty("defaultrole");
						System.out.println("defaultrole ->" + defaultrole);
						session.setAttribute("firstName", user.getFirstName());
						// session.setMaxInactiveInterval(604800);
						session.setAttribute("user", "validUser");
						System.out.println("in login pro:" + session.getAttribute("user"));
						session.setAttribute("email", user.getEmail());
						session.setAttribute("createdByUser", user.getEmail());
						session.setAttribute("idRole", idRole);
						session.setAttribute("Role", Rolename);
						SqlRowSet roleModuleTable = loginService.getIdTaskandAccessControlFromRoleModuleTable(idRole);
						long idTask = 0l;
						Map<String, String> module = new LinkedHashMap<String, String>();
						System.out.println("module in login controller" + module);
						while (roleModuleTable.next()) {
							idTask = roleModuleTable.getLong("idTask");
							String taskName = loginService.getTaskNameFromModuleTable(idTask);
							module.put(taskName, roleModuleTable.getString("accessControl"));
						}
						System.out.println("module in login controller" + module);
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date today = new Date();
						Calendar cal = new GregorianCalendar();
						cal.setTime(today);
						String curr_today = dateFormat.format(cal.getTime());
						cal.add(Calendar.DAY_OF_MONTH, -30);
						String curr_today_30 = dateFormat.format(cal.getTime());
						System.out.println("fromDate" + curr_today_30);
						System.out.println("toDate" + curr_today);
						String dateFilter = "";
						dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
						session.setAttribute("dateFilter", dateFilter);
						session.setAttribute("toDate", curr_today_30);
						session.setAttribute("fromDate", curr_today);
						session.setAttribute("RunFilter", 0);
						session.setAttribute("module", module);
						ModelAndView modelandview = new ModelAndView("loginSuccess");
						// List<Project> projList = projService.getAllProjectsOfAUser(user.getEmail());
						// List<Project> projList
						// =loginService.getAllProjectsOfARole(Ldapgroupname,idRole);
						session.setAttribute("userProjectList", projList);
						/*
						 * if(projList.size() > 0){ session.setAttribute("projectId",
						 * projList.get(0).getIdProject()); session.setAttribute("projectName",
						 * projList.get(0).getProjectName()); session.setAttribute("isOwner",
						 * projList.get(0).getIsOwner()); }
						 */

						dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);
						session.setAttribute("domainProjectList", dplist);
						if (dplist.size() > 0) {
							session.setAttribute("projectId", Long.valueOf(dplist.get(0).getIdProject()));
							session.setAttribute("domainId", dplist.get(0).getDomainId());
							session.setAttribute("projectName", dplist.get(0).getProjectName());
							session.setAttribute("domainName", dplist.get(0).getDomainName());
						}
						modelandview.addObject("currentSection", "Data Connection");
						modelandview.addObject("currentLink", "View");
						return modelandview;

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					// Check if it is fresh database setup and restart is required
					String freshDBSetUpRestartRequired = (String) session.getAttribute("freshDBSetUpRestartRequired");

					if (freshDBSetUpRestartRequired != null
							&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
						msg = "Kindly restart the application and login";
						return new ModelAndView("loginPage", "msg", msg);
					} else {
						System.out.println(licenseMsg);

						// Show renew license page
						return new ModelAndView("renewLicense", "msg", licenseMsg);
					}
				}
			}
		}

		System.out.println(msg);

		return new ModelAndView("loginPage", "msg", msg);
	}

//	private String generateCSRFToken() {
//		// TODO Auto-generated method stub
//		return UUID.randomUUID().toString();
//	}

	@RequestMapping(value = "/renewLicense", method = RequestMethod.POST)
	public ModelAndView updateNewLicenseKey(HttpSession session, HttpServletResponse response,
			@RequestParam("licenseKey") String licenseKey) {

		System.out.println("\n====> License Renewal - START <====");
		System.out.println("\n====> New License Key: " + licenseKey);

		boolean licenseUpdateStatus = false;
		String licenseMsg = "";
		try {

			if (licenseKey != null && !licenseKey.trim().isEmpty()) {
				StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
				decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
				String decryptedLicense = "";
				try {
					decryptedLicense = decryptor.decrypt(licenseKey);
				} catch (Exception e) {
					System.out.println("\n====> Invalid License Key, please enter valid License key !!");
					licenseMsg = "Invalid License Key";
					e.printStackTrace();
				}
				SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
				Date licenseExpiryDate = null;
				try {
					licenseExpiryDate = format.parse(decryptedLicense.split("-")[3]);

					System.out.println("\n====> License Expiry Date: " + licenseExpiryDate);

					if (Days.daysBetween(new LocalDate(Calendar.getInstance().getTime()),
							new LocalDate(licenseExpiryDate.getTime())).getDays() < 0) {

						licenseMsg = "License is expired, please enter valid License Key";
						session.setAttribute("licenseExpired", "true");

					} else {
						// Update the license key in database
						boolean updateStatus = loginService.updateLicenseKeyPropertyInDB(licenseKey);

						if (updateStatus) {
							licenseUpdateStatus = true;
						} else {
							licenseMsg = "Error occurred, Please retry or contact info@firsteigen.com";
						}
					}

				} catch (ParseException e) {
					licenseMsg = "Invalid License Key";
					e.printStackTrace();
				}

			} else {
				licenseMsg = "Invalid License Key";
			}

		} catch (Exception e) {
			licenseMsg = "Error occurred, Please retry or contact info@firsteigen.com";
			e.printStackTrace();
		}

		ModelAndView modelandview = null;

		// If License is updated successfully, redirect to login page
		if (licenseUpdateStatus) {
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			modelandview = new ModelAndView("loginPage");
			modelandview.addObject("ADflag", activeDirectoryFlag);
		} else {
			modelandview = new ModelAndView("renewLicense", "msg", licenseMsg);
		}

		return modelandview;
	}

//	private boolean updateLicenseKeyPropertyInDB(String licenseKey) {
//		boolean updateStatus = false;
//		try {
//			// Update the property in database
//			String propertyCategoryName = DatabuckPropertyCategory.license.toString();
//			String propertyName = "LicenseKey";
//			updateStatus = taskDao.updatePropertyValue(propertyCategoryName, propertyName, licenseKey);
//
//			System.out.println("\n====> Updating property[" + propertyName + "] of PropertyCategory:["
//					+ propertyCategoryName + "] - Status[" + updateStatus + "]");
//
//			if (updateStatus) {
//				licenseProperties.setProperty(propertyName, licenseKey);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return updateStatus;
//	}

	@RequestMapping(value = "/updateDomainProjectIdInSession", method = RequestMethod.GET)
	public void updateDomainProjectIdInSession(HttpSession session, HttpServletResponse response,
			@RequestParam("projectId") Integer projectId, @RequestParam("domainId") Integer domainId) {
		System.out.println("\n====> updateDomainProjectIdInSession - START <====");

		String status = "failed";

		try {
			List<DomainProject> domainprojectList = (List<DomainProject>) session.getAttribute("domainProjectList");
			Integer lngProjId = projectId;
			Integer lngDomId = domainId;
			DomainProject domainproject = null;

			if (domainprojectList != null && domainprojectList.size() > 0) {
				for (Iterator<DomainProject> projIterator = domainprojectList.iterator(); projIterator.hasNext();) {
					DomainProject DomainProject = projIterator.next();
					if (DomainProject.getDomainId() == lngDomId && DomainProject.getIdProject() == lngProjId) {
						domainproject = DomainProject;
						break;
					}
				}
			} else {
				System.out.println("\n====> Domain-Project List is empty !");
			}

			if (domainproject != null) {
				status = "success";
				session.setAttribute("projectId", Long.valueOf(domainproject.getIdProject()));
				session.setAttribute("domainId", domainproject.getDomainId());
				session.setAttribute("projectName", domainproject.getProjectName());
				session.setAttribute("domainName", domainproject.getDomainName());

				System.out.println("\n====> Domain-Project combination updated successfully !!");
				System.out.println("current projectId -->" + session.getAttribute("projectId"));
				System.out.println("current projectName  -->" + session.getAttribute("projectName"));
				System.out.println("current domainId -->" + session.getAttribute("domainId"));
				System.out.println("current domainName  -->" + session.getAttribute("domainName"));
			} else {
				System.out.println("\n====> Failed to update Domain-Project combination !!");
			}

		} catch (Exception e) {
			System.out.println("\n====> Failed to update Domain-Project combination !!");
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("status", status);
		try {
			response.getWriter().println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/updateProjectIdInSession", method = RequestMethod.GET)
	public void updateProjectIdInSession(HttpSession session, HttpServletResponse response,
			@RequestParam("projectId") String projectId) {

		List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
		// List<Domain> domainList =
		// (List<Domain>)session.getAttribute("userdomainList");
		Long lngProjId = Long.parseLong(projectId);
		Project selectedProject = null;
		for (Iterator<Project> projIterator = projList.iterator(); projIterator.hasNext();) {
			Project project = projIterator.next();
			if (project.getIdProject() == lngProjId) {
				selectedProject = project;
				break;
			}
		}
		if (selectedProject != null) {
			session.setAttribute("projectId", selectedProject.getIdProject());
			session.setAttribute("projectName", selectedProject.getProjectName());
			session.setAttribute("isOwner", selectedProject.getIsOwner());
		}

		System.out.println("current projectId-->" + session.getAttribute("projectId"));
		System.out.println("current isOwner-->" + session.getAttribute("isOwner"));

		/*
		 * Long lngDomainId = Long.parseLong(projectId); Domain selectedDomain = null;
		 * for(Iterator<Domain> projIterator=domainList.iterator();
		 * projIterator.hasNext();){ Domain domain = projIterator.next();
		 * if(domain.getDomainId() == lngDomainId){ selectedDomain = domain; break; } }
		 * if(selectedDomain != null){ session.setAttribute("domainId",
		 * selectedDomain.getDomainId()); session.setAttribute("domainName",
		 * selectedDomain.getDomainName()); }
		 * 
		 * System.out.println("current domainId-->"+session.getAttribute("domainId"));
		 * System.out.println("current domainName-->"+session.getAttribute("domainName")
		 * );
		 */

		try {
			System.out.println("in updateProjectIdInSession try() : ");
			if (RBACController.rbac("Results", "R", session)) {
				response.sendRedirect("dashboard_View");
			} else if (RBACController.rbac("Data Connection", "R", session)) {
				response.sendRedirect("dataConnectionView");
			} else if (RBACController.rbac("Data Template", "R", session)) {
				response.sendRedirect("datatemplateview");
			} else if (RBACController.rbac("Extend Template & Rule", "R", session)) {
				response.sendRedirect("extendTemplateView");
			} else if (RBACController.rbac("Global Rule", "R", session)) {
				response.sendRedirect("viewGlobalRules");
			} else if (RBACController.rbac("Validation Check", "R", session)) {
				response.sendRedirect("validationCheck_View");
			} else if (RBACController.rbac("Tasks", "R", session)) {
				response.sendRedirect("viewSchedules");
			} else if (RBACController.rbac("User Settings", "R", session)) {
				response.sendRedirect("accessControls");
			} else
				response.sendRedirect("welcomePage");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	 private int checkDaysLeftForLicenseRenewal(HttpSession session) {
//		  String licenseKey = licenseProperties.getProperty("LicenseKey");
//		  if(licenseKey!=null && !licenseKey.trim().isEmpty()) {
//	    	StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
//	    	decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
//	    	String decryptedLicense = decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[3];
//	    	session.setAttribute("VersionNumber", decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[2]);
//	    	SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
//	    	Date licenseExpiryDate = null;
//	    	try {
//				licenseExpiryDate = format.parse(decryptedLicense);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//	    	if(Math.abs(Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()), new LocalDate(Calendar.getInstance().getTime())).getDays()) <= 30){
//	    	      session.setAttribute("licenseExpired", "true");
//	    	}else{
//	    		session.setAttribute("licenseExpired", "false");
//	    	}
//	    	session.setAttribute("licenseExpiryDate", licenseExpiryDate);
//	    	return Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()), new LocalDate(Calendar.getInstance().getTime())).getDays();
//		  } else {
//			  System.out.println("LicenseKey not found !!");
//			  return 0;
//		  }
//	    }

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		// Insert into logging activity
		try {
			Long idUser = (Long) session.getAttribute("idUser");
			String email = (String) session.getAttribute("email");
			if (activeDirectoryFlag != null && activeDirectoryFlag.trim().equalsIgnoreCase("Y")) {
				idUser = -1l;
			}
			iLoginDAO.insertDatabuckLoginAccessLog(idUser, email, "/logout", session.getId(), "logout", "passed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelAndView modelandview = new ModelAndView("loginPage");
		modelandview.addObject("ADflag", activeDirectoryFlag);
		if ((user == null) || (!user.equals("validUser"))) {
			return modelandview;
		}
		System.out.println(" =============== " + session.getAttribute("name"));
		session.invalidate();
		return modelandview;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public ModelAndView changePassword(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("changePassword");
		modelAndView.addObject("currentSection", "User Settings");
		modelAndView.addObject("currentLink", "changePassword");

		return modelAndView;
	}

	@RequestMapping(value = "/sendnewpassword", method = RequestMethod.POST)
	public void sendNewPassword(@RequestBody ChangePassword changepassword, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		// System.out.println("new password" + changepassword.getNewpassword());
		// System.out.println("old password" + changepassword.getOldpassword());

		String newpassword = changepassword.getNewpassword();
		// String confirmpassword = request.getParameter("passwordcheck");
		String oldpassword = changepassword.getOldpassword();
		// System.out.println("newpassword" + newpassword);
		// System.out.println("oldpasswordid" + oldpassword);
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ModelAndView modelAndView = new ModelAndView("abcd");

		Long idUser = (Long) session.getAttribute("idUser");
		boolean validatepasword = iUserService.validateCurrentPassword(idUser, oldpassword);
		if (validatepasword) {
			boolean updatePwdStatus = iUserService.updateUserPassword(idUser, newpassword);
			System.out.println("updatePwdStatus" + updatePwdStatus);
			if (updatePwdStatus) {
				try {
					response.getWriter().println("Password Updated Succesufully");
					System.out.println("Password Updated Succesufully");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				modelAndView.addObject("updatePwdStatus", "PassWord Updated Succesufully");
			} else {
				try {
					response.getWriter().println("problem while Updating  password");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				modelAndView.addObject("updatePwdStatus", "problem while Updating  password");
			}
		} else {
			try {
				response.getWriter().println("old password is invalid");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("old password is in valid:" + oldpassword);
			modelAndView.addObject("updatePwdStatus", "old password is invalid");
		}
	}

//	private String getDatabuckHome() {
//		String databuckHome = "/opt/databuck";
//
//		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {
//
//			databuckHome = System.getenv("DATABUCK_HOME");
//
//		} else if (System.getProperty("DATABUCK_HOME") != null
//				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {
//
//			databuckHome = System.getProperty("DATABUCK_HOME");
//
//		}
//		System.out.println("DATABUCK_HOME:" + databuckHome);
//		return databuckHome;
//	}
//
//	private String getBelongsToRoles(HashMap<Long, String> oMappedRoles) {
//		String sRetValue = "";
//		List<String> aRoleList = new ArrayList<String>();
//
//		for(Map.Entry<Long, String> oRole : oMappedRoles.entrySet()) {
//			aRoleList.add(oRole.getValue().toString());
//		}
//		sRetValue = String.join(",", aRoleList);
//
//		return sRetValue;
//	}

	@RequestMapping(value = "/relogin", method = RequestMethod.GET)
	public ModelAndView relogin(HttpSession oSession) {
		// oSession.invalidate();
		return new ModelAndView("relogin");
	}
}

/*
 * @ExceptionHandler(Exception.class) public ModelAndView
 * handleNullPointerException(HttpServletRequest req, Exception exception) {
 *
 * System.out.println("Request   : "+req.getRequestURI()+ "  rised "
 * +exception);
 *
 * ModelAndView model= new ModelAndView();
 * model.addObject("exception",exception);
 * model.addObject("url",req.getRequestURI());
 *
 * model.setViewName("errorPage"); return model; }
 */
