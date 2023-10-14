package com.databuck.restcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.databuck.bean.DomainProject;
import com.databuck.bean.LoginSSORequest;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.bean.UserToken;
import com.databuck.controller.LoginController;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.ILoginDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.databasemigration.MigrationManagement;
import com.databuck.integration.EmailIntegrationService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IProjectService;
import com.databuck.service.IUserService;
import com.databuck.service.LoginService;
import com.databuck.util.DateUtility;
import com.databuck.util.ExportUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.UserLDAPGroupHolder;

@CrossOrigin(origins = "*")
@RestController
public class LoginRestController {

	private static final Logger LOG = Logger.getLogger(LoginController.class);
	@Autowired
	public LoginService loginService;
	@Autowired
	public IUserService iUserService;
	@Autowired
	public IProjectService projService;
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
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private EmailIntegrationService emailIntegrationService;

	@RequestMapping(value = "/dbconsole/getActiveDirFlag", method = RequestMethod.GET)
	public ResponseEntity<Object> getActiveDirFlag() {
		LOG.info("/dbconsole/getActiveDirFlag - START");
		String activeDirectoryFlag = "N";
		Map<String, String> res = new HashMap<>();
		try {
			activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryFlag == null || activeDirectoryFlag.isEmpty()) {
				activeDirectoryFlag = "N";
			}
		} catch (Exception ex) {
			LOG.error("Exception  " + ex.getMessage());
			ex.printStackTrace();
			activeDirectoryFlag = "N";
		}
		res.put("activeDirectoryFlag", activeDirectoryFlag);
		res.put("status", "success");
		LOG.info("/dbconsole/getActiveDirFlag - END");
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/sendExpiryMail", method = RequestMethod.GET)
	public ResponseEntity<Object> sendMail() {
		LOG.info("/dbconsole/sendExpiryMail - START");
		String response = "";
		Map<String, String> res = new HashMap<>();
		try {
			response = emailIntegrationService.sendLicenseExpiryMail();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  " + ex.getMessage());
		}
		res.put("message", response);
		res.put("status", "success");
		LOG.info("/dbconsole/sendExpiryMail - END");
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/getSSODetails", method = RequestMethod.GET)
	public ResponseEntity<Object> getSSODetails() {
		LOG.info("/dbconsole/getSSODetails - START");
		String isSSOenabled = "N";
		String tenentIdToken = "";
		String clientIdToken = "";
		String redirectUrl = "";
		Map<String, String> res = new HashMap<>();
		try {
			isSSOenabled = appDbConnectionProperties.getProperty("isSSOenabled");
			if (isSSOenabled == null || isSSOenabled.isEmpty()) {
				isSSOenabled = "N";
			}
			tenentIdToken = appDbConnectionProperties.getProperty("ssoTenentIdToken");// 394aa0d7-af1a-4788-b38f-523006fd9dbd
			clientIdToken = appDbConnectionProperties.getProperty("ssoClientIdToken");// 930119c1-2e01-4ef8-93bc-886713319192
			redirectUrl = appDbConnectionProperties.getProperty("ssoRedirectUrl");// http%3A%2F%2Flocalhost%3A4200%2F
			if (redirectUrl != null && !redirectUrl.isEmpty()) {
				redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  " + ex.getMessage());
			isSSOenabled = "N";
		}
		res.put("isSSOenabled", isSSOenabled);
		res.put("tenentIdToken", tenentIdToken);
		res.put("clientIdToken", clientIdToken);
		res.put("redirectUrl", redirectUrl);
		res.put("status", "success");
		LOG.info("/dbconsole/getSSODetails - END");
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/dbconsole/login", method = RequestMethod.POST)
	public ResponseEntity<Object> loginPage(@RequestBody Map<String, String> loginReq, HttpServletRequest request,
			HttpSession session, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/login - START");
		DateFormat expdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Successfully logged in.");
		Map<String, Object> failureResponse = new HashMap<>();
		Map<String, Object> licenseDetails= new HashMap<>();
		failureResponse.put("status", "failed");
		try {
			if (!loginReq.containsKey("email") || !loginReq.containsKey("password")) {
				failureResponse.put("message", "Username or password is missing.");
				LOG.error("Username or password is missing.");
				
				return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
			}
			String email = loginReq.get("email");
			String password = loginReq.get("password");

			session.setAttribute("csrfToken", loginService.generateCSRFToken());

			ArrayList<String> alist = new ArrayList<String>();
			String msg = "The username or password you entered is incorrect";
			String licenseMsg = "License expired - please contact info@firsteigen.com";
			// activedirectory flag check
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryFlag == null)
				activeDirectoryFlag = "N";
			boolean lByPassMigration = migrationManage.isByPassMigrationEnabled();
			String sByPassMigrationMsg = "";
			Map<String, Object> oMigrationView = new HashMap<>();

			LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

			/*
			 * Pradeep 7-Jan-2020 - Intercept login process to check is fresh customer with
			 * full import DataBuck schema import or it need upgrade?
			 */
			String sPageContext = null;
			MigrationManagement.DbMigrationContext oDbMigrationContext = migrationManage
					.getDbMigrationContext(request.getServletContext());

			Map<String, Object> sPageContext1 = new HashMap<>();

			session.setAttribute("freshDBSetUpRestartRequired", "N");

			String sContinueMsg = "Kindly click proceed to continue ..";
			String sSupportMsg = "Can not continue to login .. Kindly contact FirstEigen support.";
//			String supportMessageMThree = "Program encounted technical error while submitting request to application server.\n"
//					+ sSupportMsg;
			String supportMessageMTwo = "Inconsistent state of DataBuck database detected or error occured.\n"
					+ sSupportMsg;
//			String supportMessageMOne = "Error occured while upgrade/import data schema.\n" + sSupportMsg;
			String contMsgZero = "The DataBuck product need to import all database schema before you use it.\n"
					+ sContinueMsg;
			String contMsgOne = "The DataBuck database need to be upgraded before you use  features/bug fixes.\n"
					+ sContinueMsg;
//			String contMsgTwo = "The DataBuck database upgraded/schema imported successfully, kindly login to DataBuck.";

			if (oDbMigrationContext == MigrationManagement.DbMigrationContext.FreshDatabase) {
				session.setAttribute("freshDBSetUpRestartRequired", "Y");

				if (lByPassMigration) {
					sByPassMigrationMsg = "The DataBuck product need to import all database schemas before you use it.CRLFCRLFManual DB Migration is enabled, kindly setup databases manually and restart server.";
					// sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
					sPageContext1.put("pageContext", 3);
					sPageContext1.put("message", "");
				} else {
					// sPageContext = "{ `PageContext`: 0, `Msg`: `` }".replace('`', '\"');
					sPageContext1.put("pageContext", 0);
					sPageContext1.put("message", contMsgZero);
				}

			} else if (oDbMigrationContext == MigrationManagement.DbMigrationContext.ExistingDatabaseWithChanges) {
				// sPageContext = "{ `PageContext`: 1, `Msg`: `{0}` }".replace('`', '\"');
				sPageContext1.put("pageContext", 1);
				sPageContext1.put("message", contMsgOne);
				try {
					if (lByPassMigration) {
						sByPassMigrationMsg = migrationManage.getByPassMigrationDisplableMsg(oDbMigrationContext);
						// sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
						sPageContext1.put("pageContext", 3);
						sPageContext1.put("message", "");
					}
				} catch (Exception oException) {
					oException.printStackTrace();
					LOG.error("Exception  "+oException.getMessage());
				}
				LOG.debug(String.format(
						"Bypass migration = '%1$s', Bypass msg = '%2$s'", lByPassMigration, sByPassMigrationMsg));

			} else if ((oDbMigrationContext == MigrationManagement.DbMigrationContext.InconsistentDatabase)
					|| (oDbMigrationContext == MigrationManagement.DbMigrationContext.ErrorOccured)) {
				sPageContext = "{ `PageContext`: -2, `Msg`: `` }".replace('`', '\"');
				sPageContext1.put("pageContext", -2);
				sPageContext1.put("message", supportMessageMTwo);
			}

			if (sPageContext1 != null && !sPageContext1.isEmpty()) {
				// oMigrationView = new ModelAndView("DatabaseMigration");
				oMigrationView.put("result", sPageContext1);
				oMigrationView.put("isDBMigrationNeed", true);
				oMigrationView.put("ByPassMigrationMsg", sByPassMigrationMsg);

				LOG.info(String.format(
						"Database schema needs import or upgrade as '%1$s' detected, redirecting to database migration page",
						oDbMigrationContext));
			
				return new ResponseEntity<Object>(oMigrationView, HttpStatus.OK);

			} else {
				LOG.error("Database schema is up-to-date no schema changes were detected, continuing to BAU login process ...");
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
						
						if (loginService.validateLicense(licenseDetails) < 0) {
							User userDb = new User();
							response.put("isUserValid", "validUser");
							String firstName = loginService.getFirstNameFromUserTable(idUser);
							// session.setAttribute("firstName", firstName);
							userDb.setFirstName(firstName);
							userDb.setIdUser(idUser);
							// session.setAttribute("user", "validUser");
							// System.out.println("in login pro:" + session.getAttribute("user"));
							// session.setAttribute("email", email);
							userDb.setEmail(email);
							// session.setAttribute("idUser", idUser);
							LOG.debug("idUser" + idUser);
							Long idRole = loginService.getRolesFromUserRoleTable(idUser);
							LOG.debug("idRole=" + idRole);
							// session.setAttribute("idRole", idRole);
							String Rolename;
							Rolename = loginService.getRoleFromRoleTable(idRole);
							// session.setAttribute("Role", Rolename);
							userDb.setIdRole(idRole);
							userDb.setRoleName(Rolename);
							SqlRowSet roleModuleTable = loginService
									.getIdTaskandAccessControlFromRoleModuleTable(idRole);
							long idTask = 0l;
							Map<String, String> module = new LinkedHashMap<String, String>();
							LOG.debug("module in login controller" + module);
							while (roleModuleTable.next()) {
								idTask = roleModuleTable.getLong("idTask");
								// System.out.println("idTask="+idTask+"accessControl="+roleModuleTable.getString("accessControl"));
								String taskName = loginService.getTaskNameFromModuleTable(idTask);
								module.put(taskName, roleModuleTable.getString("accessControl"));
							}
							LOG.debug("module in login controller" + module);
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date today = new Date();
							Calendar cal = new GregorianCalendar();
							cal.setTime(today);
							String curr_today = dateFormat.format(cal.getTime());
							cal.add(Calendar.DAY_OF_MONTH, -30);
							String curr_today_30 = dateFormat.format(cal.getTime());
							LOG.debug("fromDate" + curr_today_30);
							LOG.debug("toDate" + curr_today);
							String dateFilter = "";
							dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
							session.setAttribute("dateFilter", dateFilter);
							session.setAttribute("toDate", curr_today_30);
							session.setAttribute("fromDate", curr_today);
							session.setAttribute("RunFilter", 0);
							session.setAttribute("module", module);
							List<Project> projList = projService.getAllProjectsOfAUser(email);
							LOG.debug("projList ->>" + projList);
							response.put("userProjectList", projList);
							dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);
							response.put("domainProjectList", dplist);
							if (dplist.size() > 0) {
								response.put("projectId", Long.valueOf(dplist.get(0).getIdProject()));
								response.put("domainId", dplist.get(0).getDomainId());
								response.put("projectName", dplist.get(0).getProjectName());
								response.put("domainName", dplist.get(0).getDomainName());
							}
							// Added for Adapter implementation as authorized access
							UserToken usertoken = generateTokenAndRefreshtoken(userDb);
							
							response.put("token", usertoken.getToken());
							response.put("refreshToken", usertoken.getRefreshtoken());
							
							response.put("expiryTime", expdateFormat.format(usertoken.getExpiryTime()) );
							response.put("user", userDb);
							response.put("status", "success");
							response.put("licenseDetails", licenseDetails);
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						} else {
							// Check if it is fresh database setup and restart is required

							String freshDBSetUpRestartRequired = (String) session
									.getAttribute("freshDBSetUpRestartRequired");
							if (freshDBSetUpRestartRequired != null
									&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
								msg = "Kindly restart the application and login";
								failureResponse.put("message", msg);
								
								return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
							} else {
								LOG.error(licenseMsg);
								failureResponse.put("message", licenseMsg);
								failureResponse.put("licenseDetails", licenseDetails);
								
								return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
							}
						}
					} else {

						LOG.error(msg);
						// Update status in DatabuckLoginAccessLog
						iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, idUser, "failed");
						failureResponse.put("message", msg);
					
						return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
					}
				} else {
					if (loginService.validateLicense(licenseDetails) < 0) {
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

							LOG.debug("\n=====> Script command: " + databuckHome + "/scripts/ldap_login.sh "
									+ email + " xxxxxxx xxxxxxx\n");
							String[] ldap_login_cmd_args = { sCmdLine, email, Adminpasssword, principal };

							oProgramOutput = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_login_cmd_args,
									new String[] { "result: 0 Success" });
							LOG.debug("oProgramOutput : " + oProgramOutput);

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
											//System.out.println("1");
											break;
										} else {
											previousLine = previousLine.trim() + "" + a.trim();
											String[] groupname = previousLine.split(":");
											domainname = groupname[1];
											//System.out.println("2");
											break;
										}
									}
									if (a.startsWith("dn:")) {
										previousLine = a;
									}

								}
							}

							if (domainname.equalsIgnoreCase("")) {
								LOG.error("invalid cn entered (DN not found): " + email);
								LOG.error(msg);
								failureResponse.put("message", msg);
								return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
							} else // authentication block
							{
								LOG.debug("  (DN  found for entered CN ): " + domainname);
								String[] ldap_auth_cmd_args = { sCmdLine1, email, password, principal, Adminpasssword };
								LOG.debug("\n=====> Script command: " + databuckHome + "/scripts/ldap_auth.sh "
										+ email + " xxxxxxx xxxxxxx xxxxxxx\n");
								oProgramOutput1 = JwfSpaInfra.runProgramAndSearchPatternInOutput(ldap_auth_cmd_args,
										new String[] { "Result: Success (0)" });
								LOG.debug("oProgramOutput1 :   " + oProgramOutput1);
								if (oProgramOutput1.get("Program Result").equalsIgnoreCase("1")) {
									LOG.info("User authenticated succesfully  ");
									LOG.debug("loadComponentAccessControlViewList1 02 " + oProgramOutput1);

									// Update status in DatabuckLoginAccessLog
									iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "passed");
								}

								else {
									LOG.error("Invalid credentials for dn/password : " + domainname);
									LOG.error(msg);

									// Update status in DatabuckLoginAccessLog
									iLoginDAO.updateDatabuckLoginAccessLog(login_log_row_id, null, "failed");
									failureResponse.put("message", msg);
									
									return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
								}
							}

							String ProgramOutput = oProgramOutput.get("Program Std Out");

							alist = loginService.getgroupfrom_Program_Std_Out(ProgramOutput);
							session.setAttribute("UserLDAPGroups", String.join(",", alist)); // Pradeep 10-Oct-2020 for
																								// LDAP
																								// groups to project
																								// mapping

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
									LOG.debug("\nRolename : " + Rolename + "\tidRole : " + idRole);

									/*
									 * Pradeep 26-Mar-2021 changes so session can contain as many roles mapped in
									 * DataBuck to logged in user's LDAP member group
									 */
									String sBelongsToRoles = loginService.getBelongsToRoles(RoleData);
									response.put("BelongsToRoles", sBelongsToRoles);
									LOG.debug(String.format(
											"Session Values for Roles 'idRole / Rolename' = '%1$s / %2$s' ,'BelongsToRoles' = '%3$s'",
											idRole, Rolename, sBelongsToRoles));

									HashMap<Long, String> ProjectData = loginService
											.getProjectDataFromLdapAfterLogin(alist);
									LOG.debug("\nProjectname : " + (String) ProjectData.values().toArray()[0]
											+ "\tProjectid : " + (Long) ProjectData.keySet().toArray()[0]);
									projList = loginService.getProjectListOfUser(alist);

								} catch (Exception e) {
									LOG.error("No role and projects are found mapped to group ");
									failureResponse.put("message", "No role and projects are found mapped to group");
									
									return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
								}
							} else {
								if (defaulAdminLdapGroup.equalsIgnoreCase("")
										|| defaulAdminLdapRole.equalsIgnoreCase("")) {
									LOG.error("default_ldap_admin, default_ldap_role properties are missing");
									failureResponse.put("message",
											"default_ldap_admin, default_ldap_role properties are missing");
								
									return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
								}
								Rolename = defaulAdminLdapRole;
								idRole = loginService.getRoleIdFromRoleTable(Rolename);
								projList = loginService.getProjectListOfUser(alist);
							}

							boolean isUserPresent = loginService.getIsUserPresent(email);
							if (!isUserPresent) {
								boolean newUserRecordInsertedFlag = loginService.insertNewUserRecord(idRole, email,
										password, alist);
								LOG.debug("newUserRecordInsertedFlag: " + newUserRecordInsertedFlag);
							}

							user = userDAO.getUserDataByName(email);
							/* [29-Sep-2020]:Changes for LDAP group role mapping Ends */
							LOG.debug("User object in Login_process-->" + user);
							// session.setAttribute("idUser", user.getIdUser());
							// session.setAttribute("idUser", user.getEmail());
							// System.out.println("idUser -> " + user.getEmail());
							String defaultrole = activeDirectoryConnectionProperties.getProperty("defaultrole");
							LOG.debug("defaultrole ->" + defaultrole);
							// session.setAttribute("firstName", user.getFirstName());
							// session.setMaxInactiveInterval(604800);
							// session.setAttribute("user", "validUser");
							// System.out.println("in login pro:" + session.getAttribute("user"));
							// session.setAttribute("email", user.getEmail());
							// session.setAttribute("createdByUser", user.getEmail());
							// session.setAttribute("idRole", idRole);
							// session.setAttribute("Role", Rolename);
							SqlRowSet roleModuleTable = loginService
									.getIdTaskandAccessControlFromRoleModuleTable(idRole);
							long idTask = 0l;
							Map<String, String> module = new LinkedHashMap<String, String>();
							LOG.debug("module in login controller" + module);
							while (roleModuleTable.next()) {
								idTask = roleModuleTable.getLong("idTask");
								String taskName = loginService.getTaskNameFromModuleTable(idTask);
								module.put(taskName, roleModuleTable.getString("accessControl"));
							}
							LOG.debug("module in login controller" + module);
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date today = new Date();
							Calendar cal = new GregorianCalendar();
							cal.setTime(today);
							String curr_today = dateFormat.format(cal.getTime());
							cal.add(Calendar.DAY_OF_MONTH, -30);
							String curr_today_30 = dateFormat.format(cal.getTime());
							LOG.debug("fromDate" + curr_today_30);
							LOG.debug("toDate" + curr_today);
							String dateFilter = "";
							dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
							session.setAttribute("dateFilter", dateFilter);
							session.setAttribute("toDate", curr_today_30);
							session.setAttribute("fromDate", curr_today);
							session.setAttribute("RunFilter", 0);
							session.setAttribute("module", module);
							// ModelAndView modelandview = new ModelAndView("loginSuccess");
							// List<Project> projList = projService.getAllProjectsOfAUser(user.getEmail());
							// List<Project> projList
							// =loginService.getAllProjectsOfARole(Ldapgroupname,idRole);
							response.put("userProjectList", projList);
							/*
							 * if(projList.size() > 0){ session.setAttribute("projectId",
							 * projList.get(0).getIdProject()); session.setAttribute("projectName",
							 * projList.get(0).getProjectName()); session.setAttribute("isOwner",
							 * projList.get(0).getIsOwner()); }
							 */

							dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);
							response.put("domainProjectList", dplist);
							if (dplist.size() > 0) {
								response.put("projectId", Long.valueOf(dplist.get(0).getIdProject()));
								response.put("domainId", dplist.get(0).getDomainId());
								response.put("projectName", dplist.get(0).getProjectName());
								response.put("domainName", dplist.get(0).getDomainName());
							}
							UserToken usertoken = generateTokenAndRefreshtoken(user);
							
							String helpDocumentUrl = appDbConnectionProperties.getProperty("helpDocumentURL", "");
							response.put("token", usertoken.getToken());
							response.put("refreshToken", usertoken.getRefreshtoken());
							
							response.put("expiryTime", expdateFormat.format(usertoken.getExpiryTime()) );
							response.put("user", user);
							response.put("helpDocUrl", helpDocumentUrl);
							response.put("status", "success");
							response.put("licenseDetails", licenseDetails);
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);

						} catch (Exception e) {
							e.printStackTrace();
							LOG.error("Exception  "+e.getMessage());
						}
					} else {

						// Check if it is fresh database setup and restart is required
						String freshDBSetUpRestartRequired = (String) session
								.getAttribute("freshDBSetUpRestartRequired");

						if (freshDBSetUpRestartRequired != null
								&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
							msg = "Kindly restart the application and login";
							failureResponse.put("message", msg);
							return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
						} else {
							LOG.error(licenseMsg);
							failureResponse.put("message", licenseMsg);
							failureResponse.put("licenseDetails", licenseDetails);
							
							return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
						}
					}
				}
			}

			LOG.info(msg);

			failureResponse.put("message", msg);
			LOG.info("/dbconsole/login - END");
			return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Exception  "+ex.getMessage());
			failureResponse.put("message", "The username or password you entered is incorrect");
			
			return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/dbconsole/renewLicense", method = RequestMethod.POST)
	public ResponseEntity<Object> updateNewLicenseKey(@RequestBody Map<String, String> reqParam) {
		LOG.info("/dbconsole/renewLicense - START");

		LOG.debug("reqParam " + reqParam);
		Map<String, Object> response = new HashMap<>();
		if (!reqParam.containsKey("licenseKey")) {
			response.put("message", "License key is not found in request body.");
			response.put("status", "failed");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		String licenseKey = reqParam.get("licenseKey");
		LOG.info("\n====> New License Key: " + licenseKey);
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
					LOG.error("\n====> Invalid License Key, please enter valid License key !!");
					licenseMsg = "Invalid License Key";
					e.printStackTrace();
				}
				SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
				Date licenseExpiryDate = null;
				try {
					licenseExpiryDate = format.parse(decryptedLicense.split("-")[3]);

					LOG.debug("\n====> License Expiry Date: " + licenseExpiryDate);

					if (Days.daysBetween(new LocalDate(Calendar.getInstance().getTime()),
							new LocalDate(licenseExpiryDate.getTime())).getDays() < 0) {

						licenseMsg = "License is expired, please enter valid License Key";
						response.put("licenseExpired", "true");

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
					LOG.error(licenseMsg + " Exception  " + e.getMessage());
					e.printStackTrace();
				}

			} else {
				licenseMsg = "Invalid License Key";
				LOG.error(licenseMsg);
			}

		} catch (Exception e) {
			licenseMsg = "Error occurred, Please retry or contact info@firsteigen.com";
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		// If License is updated successfully, redirect to login page
		if (licenseUpdateStatus) {
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			response.put("activeDirFlag", activeDirectoryFlag);
			response.put("status", "success");
		} else {
			response.put("message", licenseMsg);
			response.put("status", "failed");
		}
		LOG.info("/dbconsole/renewLicense - END");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	private String generateToken(User user) {
		LOG.info("generateToken - START");
		String token = null;
		try {
			LOG.debug("\n====> email: " + user.getEmail());
			String userLDAPGroups = "";
			String activeDirectoryUser = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryUser != null && activeDirectoryUser.trim().equalsIgnoreCase("Y")) {
				activeDirectoryUser = "Y";

				// Get userLdapGroups
				userLDAPGroups = UserLDAPGroupHolder.getLDAPGroupsForUser(user.getEmail());
			} else {
				activeDirectoryUser = "N";
			}
			LOG.debug("\n====> userLDAPGroups: " + userLDAPGroups);
			// Check if active token available for this user
			token = dashboardConsoleDao.checkForExistingUserToken(user.getIdUser(), user.getEmail(),
					activeDirectoryUser);
			boolean validTokenFound = false;
			if (token != null && !token.isEmpty()) {
				// Check if token is still active
				String tokenStatus = getStatusOfUserToken(token);
				// If the token is ACTIVE, Fetch the project list
				if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
					validTokenFound = true;
					LOG.info("\n===> Active token already exists, hence returning the same !!");
				}
			}
			if (!validTokenFound) {
				LOG.info("\n===> Active token not found, hence generating new token !!");
				Date loginTime = new Date(System.currentTimeMillis());
				Date expiryTime = new Date(System.currentTimeMillis() + (8 * 60 * 60 * 1000));
				token = UUID.randomUUID().toString();
				

				UserToken userToken = new UserToken(user.getIdUser(), user.getFirstName(), user.getIdRole(),
						user.getRoleName(), user.getEmail(), loginTime, expiryTime, token, "ACTIVE",
						activeDirectoryUser, userLDAPGroups);
				dashboardConsoleDao.insertUserToken(userToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		LOG.info("token: " + token);
		LOG.info("generateToken - END");

		return token;
	}

	private UserToken generateTokenAndRefreshtoken(User user) {
		LOG.info("generateTokenAndRefreshtoken - START");
		//String token = null, refreshtoken = null;
		UserToken userToken=null;
		try {
			LOG.debug("\n====> email: " + user.getEmail());
			String userLDAPGroups = "";
			String activeDirectoryUser = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryUser != null && activeDirectoryUser.trim().equalsIgnoreCase("Y")) {
				activeDirectoryUser = "Y";

				// Get userLdapGroups
				userLDAPGroups = UserLDAPGroupHolder.getLDAPGroupsForUser(user.getEmail());
			} else {
				activeDirectoryUser = "N";
			}
			LOG.debug("\n====> userLDAPGroups: " + userLDAPGroups);
			// Check if active token available for this user
			 userToken = dashboardConsoleDao.checkForExistingUserTokenAndRefreshToken(user.getIdUser(), user.getEmail(),
					activeDirectoryUser);
			
			
			
			boolean validTokenFound = false;
			if (userToken != null) {
				// Check if token is still active
				String tokenStatus = getStatusOfUserToken(userToken.getToken());
				// If the token is ACTIVE, Fetch the project list
				if (tokenStatus != null && tokenStatus.trim().equalsIgnoreCase("ACTIVE")) {
					validTokenFound = true;
					LOG.info("\n===> Active token already exists, hence returning the same !!");
				}
			}
			if (!validTokenFound) {
				LOG.info("\n===> Active token not found, hence generating new token !!");
				Date loginTime = new Date(System.currentTimeMillis());
				Date expiryTime = new Date(System.currentTimeMillis() + (8 * 60 * 60 * 1000));
				String token = UUID.randomUUID().toString();
				String refreshtoken = UUID.randomUUID().toString();

				 userToken = new UserToken(user.getIdUser(), user.getFirstName(), user.getIdRole(),
						user.getRoleName(), user.getEmail(), loginTime, expiryTime, token, refreshtoken, "ACTIVE",
						activeDirectoryUser, userLDAPGroups);
				dashboardConsoleDao.insertUserToken(userToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		
		LOG.info("generateTokenAndRefreshtoken - END");

		return userToken;
	}

	private static String decode(String encodedString) {
		return new String(Base64.getUrlDecoder().decode(encodedString));
	}

	@RequestMapping(value = "/dbconsole/loginSSO", method = RequestMethod.POST)
	public ResponseEntity<Object> loginPage(@RequestBody LoginSSORequest loginReq, HttpServletRequest request,
			HttpSession session, HttpServletResponse httpResponse) {
		LOG.info("/dbconsole/loginSSO - START");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Successfully logged in.");
		Map<String, Object> failureResponse = new HashMap<>();
		Map<String, Object> licenseDetails = new HashMap<>();
		failureResponse.put("status", "failed");
		try {
			String[] parts = loginReq.getToken().split("\\.");
			JSONObject payload = new JSONObject(decode(parts[1]));
			String groupName = payload.optString("department", "");
			String role = payload.optString("jobtitle", "");
			String email = payload.optString("preferred_username", "");
			if (groupName.isEmpty()) {
				groupName = payload.optString("http://schemas.microsoft.com/identity/claims/department/department", "");
			}
			if (role.isEmpty()) {
				role = payload.optString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/jobtitle/jobtitle", "");
			}
			if (groupName != null && !groupName.isEmpty() && email != null && !email.isEmpty()) {
				session.setAttribute("csrfToken", loginService.generateCSRFToken());
				ArrayList<String> alist = new ArrayList<String>();
				String msg = "The username or password you entered is incorrect";
				String licenseMsg = "License expired - please contact info@firsteigen.com";
				// activedirectory flag check
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				if (activeDirectoryFlag == null)
					activeDirectoryFlag = "N";
				boolean lByPassMigration = migrationManage.isByPassMigrationEnabled();
				String sByPassMigrationMsg = "";
				Map<String, Object> oMigrationView = new HashMap<>();
				LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
				String sPageContext = null;
				MigrationManagement.DbMigrationContext oDbMigrationContext = migrationManage
						.getDbMigrationContext(request.getServletContext());
				Map<String, Object> sPageContext1 = new HashMap<>();
				session.setAttribute("freshDBSetUpRestartRequired", "N");
				String sContinueMsg = "Kindly click proceed to continue ..";
				String sSupportMsg = "Can not continue to login .. Kindly contact FirstEigen support.";
				String supportMessageMTwo = "Inconsistent state of DataBuck database detected or error occured.\n"
						+ sSupportMsg;
				String contMsgZero = "The DataBuck product need to import all database schema before you use it.\n"
						+ sContinueMsg;
				String contMsgOne = "The DataBuck database need to be upgraded before you use  features/bug fixes.\n"
						+ sContinueMsg;
				if (oDbMigrationContext == MigrationManagement.DbMigrationContext.FreshDatabase) {
					session.setAttribute("freshDBSetUpRestartRequired", "Y");

					if (lByPassMigration) {
						sByPassMigrationMsg = "The DataBuck product need to import all database schemas before you use it.CRLFCRLFManual DB Migration is enabled, kindly setup databases manually and restart server.";
						sPageContext1.put("pageContext", 3);
						sPageContext1.put("message", "");
					} else {
						// sPageContext = "{ `PageContext`: 0, `Msg`: `` }".replace('`', '\"');
						sPageContext1.put("pageContext", 0);
						sPageContext1.put("message", contMsgZero);
					}

				} else if (oDbMigrationContext == MigrationManagement.DbMigrationContext.ExistingDatabaseWithChanges) {
					// sPageContext = "{ `PageContext`: 1, `Msg`: `{0}` }".replace('`', '\"');
					sPageContext1.put("pageContext", 1);
					sPageContext1.put("message", contMsgOne);
					try {
						if (lByPassMigration) {
							sByPassMigrationMsg = migrationManage.getByPassMigrationDisplableMsg(oDbMigrationContext);
							// sPageContext = "{ `PageContext`: 3, `Msg`: `` }".replace('`', '\"');
							sPageContext1.put("pageContext", 3);
							sPageContext1.put("message", "");
						}
					} catch (Exception oException) {
						oException.printStackTrace();
						LOG.error("Exception  " + oException.getMessage());
					}

					LOG.debug(String.format("Bypass migration = '%1$s', Bypass msg = '%2$s'", lByPassMigration,
							sByPassMigrationMsg));

				} else if ((oDbMigrationContext == MigrationManagement.DbMigrationContext.InconsistentDatabase)
						|| (oDbMigrationContext == MigrationManagement.DbMigrationContext.ErrorOccured)) {
					sPageContext = "{ `PageContext`: -2, `Msg`: `` }".replace('`', '\"');
					sPageContext1.put("pageContext", -2);
					sPageContext1.put("message", supportMessageMTwo);
				}

				if (sPageContext != null) {
					// oMigrationView = new ModelAndView("DatabaseMigration");
					oMigrationView.put("result", sPageContext1);
					oMigrationView.put("ByPassMigrationMsg", sByPassMigrationMsg);

					LOG.debug(String.format(
							"Database schema needs import or upgrade as '%1$s' detected, redirecting to database migration page",
							oDbMigrationContext));
					return new ResponseEntity<Object>(oMigrationView, HttpStatus.OK);

				} else {
					LOG.debug(
							"Database schema is up-to-date no schema changes were detected, continuing to BAU login process ...");
				}

				if (email != null && email.trim().length() > 0) {
					if (loginService.validateLicense(licenseDetails) < 0) {
						try {
							// Insert into DatabuckLoginAccessLog
							iLoginDAO.insertDatabuckLoginAccessLog(null, email, "/login_process", session.getId(),
									"login", "");

							User user = new User();

							/* [29-Sep-2020]:Changes for LDAP group role mapping Starts */
							String defaulAdminLdapGroup = activeDirectoryConnectionProperties
									.getProperty("default_ldap_admin");
							String defaulAdminLdapRole = activeDirectoryConnectionProperties
									.getProperty("default_ldap_role");

							// user =loginService.userActiveDirectoryAuthentication(email, password);
							if (role != null) {
								alist.add(role);
							} else if (defaulAdminLdapRole != null) {
								alist.add(defaulAdminLdapRole);
							} else {
								alist.add("APPROVER");
							}
							String[] groupArray = groupName.split(",");
							for (String g : groupArray) {
								alist.add(g);
							}
							List<DomainProject> dplist = new ArrayList<DomainProject>();
							session.setAttribute("UserLDAPGroups", String.join(",", alist));
							UserLDAPGroupHolder.addOrUpdateUserLDAPGroup(email, String.join(",", alist));

							boolean isLdapAdmin = false;
							for (String group : alist) {
								if (defaulAdminLdapGroup != null && defaulAdminLdapGroup.equals(group)
										&& !group.trim().equals("")) {
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
									String sBelongsToRoles = loginService.getBelongsToRoles(RoleData);
									response.put("BelongsToRoles", sBelongsToRoles);
									LOG.info(String.format(
											"Session Values for Roles 'idRole / Rolename' = '%1$s / %2$s' ,'BelongsToRoles' = '%3$s'",
											idRole, Rolename, sBelongsToRoles));

									HashMap<Long, String> ProjectData = loginService
											.getProjectDataFromLdapAfterLogin(alist);
									LOG.debug("\nProjectname : " + (String) ProjectData.values().toArray()[0]
											+ "\tProjectid : " + (Long) ProjectData.keySet().toArray()[0]);
									projList = loginService.getProjectListOfUser(alist);

								} catch (Exception e) {
									failureResponse.put("message", "No role and projects are found mapped to group");
									LOG.error("No role and projects are found mapped to group " + e.getMessage());
									return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
								}
							} else {
								if (defaulAdminLdapGroup.equalsIgnoreCase("")
										|| defaulAdminLdapRole.equalsIgnoreCase("")) {
									LOG.info("default_ldap_admin, default_ldap_role properties are missing");
									failureResponse.put("message",
											"default_ldap_admin, default_ldap_role properties are missing");
									return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
								}
								Rolename = defaulAdminLdapRole;
								idRole = loginService.getRoleIdFromRoleTable(Rolename);
								projList = loginService.getProjectListOfUser(alist);
							}

							boolean isUserPresent = loginService.getIsUserPresent(email);
							if (!isUserPresent) {
								boolean newUserRecordInsertedFlag = loginService.insertNewUserRecord(idRole, email,
										"abc@12345", alist);
								LOG.debug("newUserRecordInsertedFlag: " + newUserRecordInsertedFlag);
							}

							user = userDAO.getUserDataByName(email);
							LOG.debug("User object in Login_process-->" + user);
							String defaultrole = activeDirectoryConnectionProperties.getProperty("defaultrole");
							LOG.debug("defaultrole ->" + defaultrole);
							SqlRowSet roleModuleTable = loginService
									.getIdTaskandAccessControlFromRoleModuleTable(idRole);
							long idTask = 0l;
							Map<String, String> module = new LinkedHashMap<String, String>();
							LOG.debug("module in login controller" + module);
							while (roleModuleTable.next()) {
								idTask = roleModuleTable.getLong("idTask");
								String taskName = loginService.getTaskNameFromModuleTable(idTask);
								module.put(taskName, roleModuleTable.getString("accessControl"));
							}
							LOG.debug("module in login controller" + module);
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date today = new Date();
							Calendar cal = new GregorianCalendar();
							cal.setTime(today);
							String curr_today = dateFormat.format(cal.getTime());
							cal.add(Calendar.DAY_OF_MONTH, -30);
							String curr_today_30 = dateFormat.format(cal.getTime());
							LOG.debug("fromDate" + curr_today_30);
							LOG.debug("toDate" + curr_today);
							String dateFilter = "";
							dateFilter = " Date >= '" + curr_today_30 + "' and Date <= '" + curr_today + "'";
							session.setAttribute("dateFilter", dateFilter);
							session.setAttribute("toDate", curr_today_30);
							session.setAttribute("fromDate", curr_today);
							session.setAttribute("RunFilter", 0);
							session.setAttribute("module", module);
							response.put("userProjectList", projList);
							dplist = projService.getDomainProjectAssociationOfCurrentUser(projList);
							response.put("domainProjectList", dplist);
							if (dplist.size() > 0) {
								response.put("projectId", Long.valueOf(dplist.get(0).getIdProject()));
								response.put("domainId", dplist.get(0).getDomainId());
								response.put("projectName", dplist.get(0).getProjectName());
								response.put("domainName", dplist.get(0).getDomainName());
							}
							String token = generateToken(user);
							String helpDocumentUrl = appDbConnectionProperties.getProperty("helpDocumentURL", "");
							response.put("token", token);
							response.put("user", user);
							response.put("helpDocUrl", helpDocumentUrl);
							response.put("status", "success");
							response.put("licenseDetails", licenseDetails);

							return new ResponseEntity<Object>(response, HttpStatus.OK);

						} catch (Exception e) {
							e.printStackTrace();
							LOG.error("Exception  " + e.getMessage());
						}
					} else {

						// Check if it is fresh database setup and restart is required
						String freshDBSetUpRestartRequired = (String) session
								.getAttribute("freshDBSetUpRestartRequired");

						if (freshDBSetUpRestartRequired != null
								&& freshDBSetUpRestartRequired.toString().trim().equalsIgnoreCase("Y")) {
							msg = "Kindly restart the application and login";
							failureResponse.put("message", msg);

							return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
						} else {
							LOG.info(licenseMsg);
							failureResponse.put("message", licenseMsg);
							failureResponse.put("licenseDetails", licenseDetails);

							return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
						}
					}
				}

				LOG.info(msg);

				failureResponse.put("message", msg);
			} else {
				LOG.error("This active directory user is not assigned to any department/jobtitle/email");
				failureResponse.put("message",
						"This active directory user is not assigned to any department/jobtitle/email");
				failureResponse.put("status", "failed");
			}

			return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			failureResponse.put("message", "The username or password you entered is incorrect");
			LOG.error("Exception  " + ex.getMessage());

			return new ResponseEntity<Object>(failureResponse, HttpStatus.OK);
		}
	}

	private String getStatusOfUserToken(String token) {
		LOG.info("getStatusOfUserToken - START");
		String tokenStatus = "EXPIRED";
		try {
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
			if (userToken != null) {
				if (userToken.getTokenStatus() != null
						&& !userToken.getTokenStatus().trim().equalsIgnoreCase("EXPIRED")) {
					long currTime = System.currentTimeMillis();
					tokenStatus = (currTime > userToken.getExpiryTime().getTime()) ? "EXPIRED" : "ACTIVE";
					dashboardConsoleDao.updateUserTokenStatus(token, tokenStatus);
				}
			} else {
				LOG.error("\n====> Failed to get Token details!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		LOG.info("getStatusOfUserToken - END");
		return tokenStatus;
	}

	@RequestMapping(value = "/dbconsole/migrateDatabase", method = RequestMethod.POST, produces = "application/json")
	public void migrateDatabase(HttpSession oSession, HttpServletResponse oResponse, HttpServletRequest oRequest,
			@RequestBody Map<String, Integer> req) throws Exception {
		LOG.info("/dbconsole/migrateDatabase - START");
		int nCallContext = req.get("nCallContext");
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
				oMigrationStatus.put("status", "success");
				oMigrationStatus.put("message", oMigrationStatus.getString("Msg"));
			}
		} else {
			oMigrationStatus = new JSONObject("{ PageContext: -1, message: failed, status: failed  }");
		}
		oResponse.getWriter().println(oMigrationStatus);
		oResponse.getWriter().flush();
		LOG.info("/dbconsole/migrateDatabase - END");
	}

	@RequestMapping(value = "/dbconsole/databuckLoginActivity", method = RequestMethod.POST)
	public ResponseEntity<Object> databuckLoginActivity(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr, HttpSession session) {
		LOG.info("/dbconsole/databuckLoginActivity - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "Failed to logout";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				LOG.debug("token   " + headers.get("token").get(0));
				token = headers.get("token").get(0);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {
				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					String activeDirectoryFlag = appDbConnectionProperties
							.getProperty("isActiveDirectoryAuthentication");
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						JSONObject inputJson = new JSONObject(inputJsonStr);

						Long idUser = inputJson.getLong("idUser");
						String email = inputJson.getString("email");
						String sReqURL = inputJson.getString("sReqURL");
						String databuck_feature = inputJson.getString("databuck_feature");
						String login_status = inputJson.getString("login_status");
						String session_id = session.getId();
						boolean isActiveDirectoryEnabled = false;
						if (activeDirectoryFlag != null && activeDirectoryFlag.trim().equalsIgnoreCase("Y")) {
							isActiveDirectoryEnabled = true;
							idUser = -1l;
						}
						if (isActiveDirectoryEnabled || (idUser != null && idUser > 0)) {
							if (email != null && !email.trim().isEmpty()) {
								if ((sReqURL != null && !sReqURL.trim().isEmpty())
										|| (sReqURL.equalsIgnoreCase("/login")
												|| sReqURL.equalsIgnoreCase("/logout"))) {
									if (databuck_feature.equalsIgnoreCase("login")
											|| databuck_feature.equalsIgnoreCase("logout")) {
										// Insert into logging activity
										if ((login_status != null && !login_status.trim().isEmpty())
												|| (login_status.equalsIgnoreCase("passed")
														|| login_status.equalsIgnoreCase("failed"))) {
											iLoginDAO.insertDatabuckLoginAccessLog(idUser, email, sReqURL, session_id,
													databuck_feature, login_status);
											message = "databuck " + databuck_feature + " activity successfully done";
											status = "success";
											LOG.info(message);
										} else {
											message = "Invalid " + databuck_feature + " status";
											LOG.error(message);
										}

									} else {
										message = "Invalid databuck feature";
										LOG.error(message);
									}

								} else {
									message = "Invalid sReqURL";
									LOG.error(message);
								}

							} else {
								message = "Invalid email";
								LOG.error(message);
							}

						} else {
							message = "Invalid idUser";
							LOG.error(message);
						}

					} else {
						message = "Invalid request";
						LOG.error(message);
					}

				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error("Exception  " + e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("/dbconsole/databuckLoginActivity - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
	
	@RequestMapping(value = "/dbconsole/extendsToken", method = RequestMethod.POST)
	public ResponseEntity<Object> extendsToken(@RequestBody Map<String, String> loginReq, HttpServletRequest request,
			HttpSession session, HttpServletResponse httpResponse) {
		Map<String, Object> failureResponse = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		DateFormat expdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LOG.info("/dbconsole/extendsToken - START");
		try {
			if (!loginReq.containsKey("refreshToken") || !loginReq.containsKey("expiryTime")) {
				failureResponse.put("message", "Token or Expiry Time is missing.");
				LOG.error("Token or Expiry Time is missing.");
				
				return new ResponseEntity<Object>(failureResponse, HttpStatus.FORBIDDEN);
			}
			LOG.debug("loginReq "+loginReq);
			session.setAttribute("csrfToken", loginService.generateCSRFToken());
			String refreshtoken = loginReq.get("refreshToken");	
			String ExpiryTime = loginReq.get("expiryTime");
			
			UserToken usertoken=dashboardConsoleDao.extendTokenValidity(refreshtoken, ExpiryTime);
			if(usertoken!=null) {
				result.put("refreshToken", usertoken.getRefreshtoken());
				
				result.put("expiryTime", expdateFormat.format(usertoken.getExpiryTime()));
				response.put("result", result);
				response.put("status", "success");
				response.put("message", "Token validity extended successfully");
				LOG.info("Token validity extended successfully");
				LOG.info("/dbconsole/extendsToken - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}else {
				response.put("status", "failed");
				response.put("message", "Token validity failed to extend");
				LOG.error("Token validity failed to extend");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			
		}catch(Exception ex) {
			response.put("status", "failed");
			response.put("message", "Token validity failed to extend");
			LOG.error("Exception "+ex.getMessage());
			return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		
		
		

		
		
	}
	
	@RequestMapping(value = "/dbconsole/getVersion",method = RequestMethod.GET)
	public ResponseEntity<Object> getVersion(){
		Map<String,Object> response = new HashMap<>();
		Map<String,String> result = new HashMap<>();
		
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
		    String fullPath = URLDecoder.decode(path, "UTF-8");
		    
		    FileInputStream fis = new FileInputStream(fullPath+"version.properties");
		    Properties prop = new Properties();
		    prop.load(fis);
		    
		    LOG.debug("version is : "+prop.getProperty("app.version"));
		    result.put("version",prop.getProperty("app.version"));
		    
			File fl = new File(fullPath.replace("WEB-INF/classes/","assets/buildid.txt"));
			Scanner sc = new Scanner(fl);
			String buildNumber = "";
			
			while(sc.hasNextLine()) {
				String text[] =sc.nextLine().split(":");
				if(text[0].contains("Pipeline.2-War-44-server-Build ID")) {
					buildNumber = text[1].trim();
				}
			}
			
		    LOG.debug("version is :"+buildNumber);
		    result.put("buildNumber",buildNumber);
		    
		    response.put("result",result);
		    response.put("status","success");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			response.put("message",e.getMessage());
			response.put("status","success");
		}
		return new ResponseEntity<Object>(response,HttpStatus.OK);
	}

}
