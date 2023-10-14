package com.databuck.restcontroller;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.bean.UserToken;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UrlPathHelper;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.AccessLog;
import com.databuck.bean.ChildModule;
import com.databuck.bean.DefectCode;
import com.databuck.bean.Dimension;
import com.databuck.bean.DomainLibrary;
import com.databuck.bean.FeaturesAccessControl;
import com.databuck.bean.LoggingActivity;
import com.databuck.bean.LoginGroupMapping;
import com.databuck.bean.Module;
import com.databuck.bean.Project;
import com.databuck.bean.Role;
import com.databuck.bean.RoleModule;
import com.databuck.bean.User;
import com.databuck.bean.LoginTrail;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.security.CheckVulnerability;
import com.databuck.service.ChecksCSVService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.IGroupService;
import com.databuck.service.IProjectService;
import com.databuck.service.IRoleServie;
import com.databuck.service.IUserService;
import com.databuck.service.TaskService;
import com.databuck.service.UserSettingService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@CrossOrigin(origins = "*")
@RestController
public class UserSettingsRestController {

	@Autowired
	public IUserService userservice;

	@Autowired
	public TaskService taskService;

	@Autowired
	private ChecksCSVService csvService;

	@Autowired
	private IProjectDAO projectDao;

	@Autowired
	public IProjectService projectService;

	@Autowired
	private UserSettingService userSettingService;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	public IGroupService groupService;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	public IRoleServie roleService;

	@Autowired
	IUserDAO iUserDAO;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private CheckVulnerability checkVulnerability;

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	ITaskDAO iTaskDAO;

	UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private static final Logger LOG = Logger.getLogger(UserSettingsRestController.class);

	@GetMapping("/dbconsole/viewUsers")
	public ResponseEntity<Object> viewUsers(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/viewUsers - START"); 
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					SqlRowSet usersTableData = userservice.getUsersFromUserTable();
					response.put("result", retrieveRecord(usersTableData));
					response.put("status", "success");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/viewUsers - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/addNewUserIntoDatabase")
	public ResponseEntity<Object> addNewUserIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/addNewUserIntoDatabase - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					Long roleid = inputJson.getLong("roleId");
					String firstName = inputJson.getString("firstName");
					String lastName = inputJson.getString("lastName");
					String userName = inputJson.getString("userName");
					String password = inputJson.getString("password");
					int userId = userservice.insertDataIntoUserTable(roleid, firstName, lastName, userName, password);
					if (userId > 0) {
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_USER, formatter.format(new Date()), (long) userId,
								DatabuckConstants.ACTIVITY_TYPE_CREATED,firstName+" "+lastName );
						response.put("status", "success");
						response.put("message", "User added successfully.");
					} else {
						response.put("status", "failed");
						response.put("message", "Failed to add new user.");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/addNewUserIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/deleteUserModule")
	public ResponseEntity<Object> deleteUserModule(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/deleteUserModule - START");
		Map<String, Object> response = new HashMap<>();

		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (!params.containsKey("idUser")) {
						response.put("status", "failed");
						response.put("message", "idUser is missing in request body.");
						LOG.error("idUser is missing in request body.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						String userName = iUserDAO.getUserNameByUserId(Long.valueOf(params.get("idUser")));
						userservice.deleteUsers(params.get("idUser"));
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_USER, formatter.format(new Date()), Long.valueOf(params.get("idUser")),
								DatabuckConstants.ACTIVITY_TYPE_DELETED,userName);
						response.put("status", "success");
						response.put("message", "User deleted successfully.");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				
				LOG.error("Token is missing in headers.");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteUserModule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/roleManagement")
	public ResponseEntity<Object> roleManagement(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/roleManagement - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					SqlRowSet roleManagementData = userservice.getroleManagementFromRoleTable();
					response.put("result", retrieveRecord(roleManagementData));
					response.put("status", "success");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/roleManagement - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/saveNewRoleIntoDatabase")
	public ResponseEntity<Object> saveNewRoleIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/saveNewRoleIntoDatabase - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					String roleName = inputJson.getString("roleName");
					String description = inputJson.getString("description");
					String taskDataAll = inputJson.getString("taskDataAll");
					String accessControlAll = inputJson.getString("accessControlAll");
					String[] taskData = taskDataAll.split("-");
					String[] accessControl = accessControlAll.split(",");
					List<String> key = new ArrayList<String>();
					List<String> value = new ArrayList<String>();
					if (taskData.length > 0) {
						if (accessControl.length > 0) {
							for (int i = 0; i < accessControl.length; i++) {
								if (accessControl[i].length() > 0) {
									value.add(accessControl[i]);
								}
							}
							for (int i = 0; i < taskData.length; i++) {
								key.add(taskData[i]);
							}
							if (key.size() == value.size()) {
								int idRole = userservice.insertIntoRoleandRoleModuleTable(key, value, roleName,
										description);
								if (idRole > 0) {
									// changes regarding Audit trail
									UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
											DatabuckConstants.DBK_FEATURE_ROLE, formatter.format(new Date()), (long) idRole,
											DatabuckConstants.ACTIVITY_TYPE_CREATED,roleName );
									response.put("status", "success");
									response.put("message", "Role added successfully.");
								} else {
									response.put("status", "failed");
									response.put("message", "Failed to add new role.");
								}
								
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							} else {
								response.put("status", "failed");
								response.put("message", "Please select the access control operations properly.");
								LOG.error("Please select the access control operations properly.");
								
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}
						} else {
							response.put("status", "failed");
							response.put("message", "Please select the access control operations.");
							LOG.error("Please select the access control operations.");
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					} else {
						response.put("status", "failed");
						response.put("message", "Please select the access control.");
						LOG.error("Please select the access control.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/saveNewRoleIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/saveEditedRoleIntoDatabase")
	public ResponseEntity<Object> saveEditedRoleIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/saveEditedRoleIntoDatabase - START");
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + inputsJsonStr);
					Long idRole = inputJson.getLong("idRole");
					String taskDataAll = inputJson.getString("taskDataAll");
					String accessControlAll = inputJson.getString("accessControlAll");
					String[] taskData = taskDataAll.split("-");
					String[] accessControl = accessControlAll.split(",");
					List<String> key = new ArrayList<String>();
					List<String> value = new ArrayList<String>();
					if (taskData.length > 0) {
						if (accessControl.length > 0) {
							for (int i = 0; i < accessControl.length || i < taskData.length; i++) {
								if (i < accessControl.length && accessControl[i].length() > 0) {
									value.add(accessControl[i]);
								}
							}
							for (int i = 0; i < taskData.length; i++) {
								key.add(taskData[i]);
							}
							if (key.size() == value.size()) {
								String roleName = iUserDAO.getRoleNameByRoleId(Long.valueOf(idRole));
								int update = userservice.updateIntoRoleandRoleModuleTable(key, value, idRole);
								if (update > 0) {
									// changes regarding Audit trail
									UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
									SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
											DatabuckConstants.DBK_FEATURE_ROLE, formatter.format(new Date()), (long) idRole,
											DatabuckConstants.ACTIVITY_TYPE_EDITED,roleName );
									response.put("status", "success");
									response.put("message", "Role updated successfully.");
								} else {
									response.put("status", "failed");
									response.put("message", "Failed to add new role.");
								}
								
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							} else {
								response.put("status", "failed");
								response.put("message", "Please select the access control operations properly.");
								LOG.error("Please select the access control operations properly.");
								
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}

						} else {
							response.put("status", "failed");
							response.put("message", "Please select the access control operations.");
							LOG.error("Please select the access control operations.");
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						}
					} else {
						response.put("status", "failed");
						response.put("message", "Please select the access control.");
						LOG.error("Please select the access control.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/saveEditedRoleIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/deleteRoleModule")
	public ResponseEntity<Object> deleteRoleModule(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/deleteRoleModule - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (!params.containsKey("idRole")) {
						response.put("status", "failed");
						response.put("message", "idRole is missing in request body.");
						LOG.error("idRole is missing in request body.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						String roleName = iUserDAO.getRoleNameByRoleId(Long.valueOf(params.get("idRole")));
						userservice.deleteRole(params.get("idRole"));
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_ROLE, formatter.format(new Date()), Long.valueOf(params.get("idRole")),
								DatabuckConstants.ACTIVITY_TYPE_DELETED,roleName);
						response.put("status", "success");
						response.put("message", "Role deleted successfully.");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteRoleModule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/duplicateEmail")
	public ResponseEntity<Object> duplicateEmail(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/duplicateEmail - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (!params.containsKey("val")) {
						response.put("status", "failed");
						response.put("message", "val is missing in request body.");
						LOG.error("val is missing in request body.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						String name = userservice.checkDuplicateEmail(params.get("val").trim());
						if (name != null && !name.equalsIgnoreCase("")) {
							response.put("status", "failed");
							response.put("message",
									"The email address you entered is already in use. Please choose another email address.");
						} else {
							response.put("status", "success");
							response.put("message", "The email address you entered is unique.");
						}
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/duplicateEmail - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping("/dbconsole/duplicateRoleName")
	public ResponseEntity<Object> duplicateRoleName(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/duplicateRoleName - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (!params.containsKey("val")) {
						response.put("status", "failed");
						response.put("message", "val is missing in request body.");
						LOG.error("val is missing in request body.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						String name = userservice.checkDuplicateRoleName(params.get("val"));
						if (name != null) {
							response.put("status", "failed");
							response.put("message",
									"The Role name you entered is already in use. Please choose another Role name.");
						} else {
							response.put("status", "success");
							response.put("message", "The role name you entered is unique.");
						}
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/duplicateRoleName - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/userCSV")
	public ResponseEntity<Object> userCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/userCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					SqlRowSet usersTableData = userservice.getUsersFromUserTable();
					List<User> users = retrieveUsers(usersTableData);
					if (users != null) {
						// response.put("result", retrieveRecord(usersTableData));
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "firstName", "lastName", "email", "roleName" };
						String[] header = { "First Name", "Last Name", "Email", "Role Name" };
						csvWriter.writeHeader(header);
						// ArrayList<Map<String,Object>> userMap = retrieveRecord(usersTableData);
						for (User user : users) {
							csvWriter.write(user, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/userCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	public List<User> retrieveUsers(SqlRowSet rset) {
		List<User> users = new ArrayList<>();
		while (rset.next()) {
			User user = new User();
			user.setRoleName(rset.getString("roleName"));
			user.setFirstName(rset.getString("firstName"));
			user.setLastName(rset.getString("lastName"));
			user.setEmail(rset.getString("email"));
			users.add(user);
		}
		return users;
	}

	@GetMapping("/dbconsole/roleCSV")
	public ResponseEntity<Object> roleCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/roleCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<Role> roleManagementData = roleService.getData();
					if (roleManagementData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "roleName", "description" };
						String[] header = { "Role Name", "Description" };
						csvWriter.writeHeader(header);
						for (Role role : roleManagementData) {
							csvWriter.write(role, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/roleCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/editRoleModule")
	public ResponseEntity<Object> editRoleModule(@RequestHeader HttpHeaders headers, HttpSession session,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/editRoleModule - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (!params.containsKey("idRole")) {
						response.put("status", "failed");
						response.put("message", "idRole is missing in request body.");
						LOG.error("idRole is missing in request body.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						String idRole = params.get("idRole");
						LOG.debug("idRole=" + idRole);
						Map<String, String> roleNameandDescription = userservice
								.getRoleNameandDescriptionFromRole(idRole);
						for (Map.Entry<String, String> Names : roleNameandDescription.entrySet()) {
							response.put("Name", Names.getKey());
							response.put("Description", Names.getValue());
						}
						ArrayList<RoleModule> roleModuleList = new ArrayList<RoleModule>();
						ArrayList<Module> modList = new ArrayList<Module>();
						ArrayList<Module> moduleList = new ArrayList<Module>();
						userservice.getIdTaskandAccessControlFromRoleModuleTable(idRole).entrySet().stream()
								.forEach(x -> {
									roleModuleList.add(new RoleModule(x.getKey(), x.getValue()));
								});
						userservice.getIdTaskandTaskNameandDisplayNameWithFilter().stream().forEach(x -> {
							modList.add(new Module(x.getIdModule(), x.getModuleName(), x.getDisplayName()));
						});

						for (Module m : modList) {
							Module mod = new Module();
							mod.setIdModule(m.getIdModule());
							mod.setModuleName(m.getModuleName());
							mod.setDisplayName(m.getDisplayName());
							mod.setChecked(false);
							roleModuleList.stream().forEach(rm -> {
								if (m.getIdModule().equals(rm.getId())) {
									if (!mod.getChecked()) {
										mod.setChecked(true);
									}
									ArrayList<ChildModule> childModuleList = new ArrayList<ChildModule>();
									String[] strSplit = rm.getName().split("-");
									ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
									strList.stream().forEach(s -> {
										if (!s.trim().equals("")) {
											ChildModule chMod = new ChildModule();
											chMod.setDisplayName(s);
											chMod.setValue("-" + s);
											chMod.setChecked(true);
											childModuleList.add(chMod);
										}
									});
									mod.setChildModule(childModuleList);
								}
							});
							moduleList.add(mod);
						}
						response.put("Module", moduleList);
						response.put("currentSection", "User Settings");
						response.put("currentLink", "View Roles");
						response.put("idRole", idRole);
						response.put("status", "success");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/editRoleModule - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/dbconsole/addNewRole")
	public ResponseEntity<Object> addNewRole(@RequestHeader HttpHeaders headers, HttpSession session,
			HttpServletRequest request) {
		LOG.info("dbconsole/addNewRole - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					ArrayList<RoleModule> roleModuleList = new ArrayList<RoleModule>();
					userservice.getIdTaskandTaskNameandDisplayNameWithFilter().stream().forEach(x -> {
						roleModuleList.add(new RoleModule(x.getIdModule(), x.getModuleName(), x.getDisplayName()));
					});
					response.put("Modules", roleModuleList);
					response.put("currentSection", "User Settings");
					response.put("currentLink", "Add New Role");
					response.put("status", "success");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);

				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/addNewRole - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	public ArrayList<Map<String, Object>> retrieveRecord(SqlRowSet rset) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int colCount = rset.getMetaData().getColumnCount();
		while (rset.next()) {
			Map<String, Object> columnValueMap = new HashMap<String, Object>();
			for (int i = 1; i <= colCount; i++) {
				String columnName = rset.getMetaData().getColumnLabel(i);

				if (columnName.toLowerCase().equalsIgnoreCase("roleName")) {
					columnName = "roleName";
				}
				if (columnName.toLowerCase().equalsIgnoreCase("idRole")) {
					columnName = "idRole";
				}
				if (columnName.toLowerCase().equalsIgnoreCase("idUser")) {
					columnName = "idUser";
				}
				if (columnName.toLowerCase().equalsIgnoreCase("firstName")) {
					columnName = "firstName";
				}
				if (columnName.toLowerCase().equalsIgnoreCase("lastName")) {
					columnName = "lastName";
				}
				columnValueMap.put(columnName, rset.getObject(i));
			}

			list.add(columnValueMap);
		}
		return list;
	}

	@GetMapping(value = "/dbconsole/projectResultView")
	public ResponseEntity<Object> projectResultView(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/projectResultView - START");
		Map<String, Object> response = new HashMap<>();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			List<Map<String, Object>> paginationProject = projectService.getAllProjectsWithAggDomains();
			response.put("result", paginationProject);
			response.put("status", "success");
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "Failed");
			response.put("message", "Failed to get project result view");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/projectResultView - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/cmpAccessControlList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadComponentAccessControlViewList(@RequestHeader HttpHeaders headers)
			throws IOException {
		LOG.info("dbconsole/cmpAccessControlList - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					DateUtility.DebugLog("loadComponentAccessControlViewList 01", "Begin controller");
					JSONObject oJsonResponse = userSettingService.getComponentAccessControlAllData("AngluarUI");
					DateUtility.DebugLog("loadRuleCatalogRecordList 02", "End controller");
					response.put("result", oJsonResponse.toMap());
					response.put("status", "success");
					response.put("message", "Successfully got the records.");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("status", "failed");
			response.put("message", oException.getMessage());
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/cmpAccessControlList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/saveSelectedAccessControl", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveSelectedAccessControl(HttpSession oSession,
			@RequestBody Map<String, String> params, @RequestHeader HttpHeaders headers) throws IOException {
		LOG.info("dbconsole/saveSelectedAccessControl - START");
		Map<String, Object> response = new HashMap<>();
		DateUtility.DebugLog("saveSelectedAccessControl 01", String.format("Begin controller with '%1$s', '%2$s'",
				params.get("selectedComponents"), params.get("selectedRoles")));
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + params);
					String[] aSelectedComponents = params.get("selectedComponents").split(",");
					String[] aSelectedRoles = params.get("selectedRoles").split(",");
					DateUtility.DebugLog("saveSelectedAccessControl 02",
							String.format("Begin controller with '%1$s', '%2$s'", String.join(",", aSelectedComponents),
									String.join(",", aSelectedRoles)));
					userSettingService.saveAccessControl(aSelectedComponents, aSelectedRoles);
					response.put("status", "success");
					response.put("message", "Role assignment successfully saved");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("status", "failed");
			response.put("message", oException.getMessage());
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/saveSelectedAccessControl - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/loadLoginGroupMappingList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadLoginGroupMappingList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/loadLoginGroupMappingList - START");
		JSONObject oJsonResponse = new JSONObject();
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					DateUtility.DebugLog("loadLoginGroupMappingList 01", "Begin controller");
					oJsonResponse = userSettingService.getLoginGroupMappingList("angularAPI");
					DateUtility.DebugLog("loadLoginGroupMappingList 02", "End controller");

					response.put("result", oJsonResponse.toMap());
					response.put("status", "success");
					response.put("message", "Successfully got the records.");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("status", "failed");
			response.put("message", oException.getMessage());
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/loadLoginGroupMappingList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/saveLoginGroupData", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveLoginGroupData(@RequestHeader HttpHeaders headers,
			@RequestBody String sDataToSave) throws IOException {
		LOG.info("dbconsole/saveLoginGroupData - START");
		JSONObject oDataToSave = null;
		Map<String, Object> response = new HashMap<>();
		DateUtility.DebugLog("saveLoginGroupDatal 01", String.format("Begin controller with '%1$s''", sDataToSave));
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + sDataToSave);
					oDataToSave = new JSONObject(sDataToSave);
					if (userSettingService.isLoginLdapGroupExists(oDataToSave.getString("GroupName"))) {
						response.put("message", userSettingService.saveLoginGroupAndRoleProjectMapping(oDataToSave,userToken.getIdUser(),userToken.getUserName()));
						response.put("status", "success");
					} else {
						response.put("message", "Entered login group name does not exists in LDAP server");
						response.put("status", "failed");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception oException) {
			String sErrorMsg = (oException.getMessage().toLowerCase().indexOf("duplicate") > -1)
					? "Duplicate group name not allowed"
					: oException.getMessage();
			response.put("status", false);
			response.put("message", sErrorMsg);
			oException.printStackTrace();
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/saveLoginGroupData - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/addNewProjectIntoDatabase")
	public ResponseEntity<Object> addNewProjectIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/addNewProjectIntoDatabase - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					if (params.containsKey("projectName") && params.containsKey("projectDescription")) {
						LOG.debug("Getting request parameters  " + params);
						String selectedOwnerGroups = params.get("selectedOwnerGroups").toString();
						String projectName = params.get("projectName").toString();
						String projectDescription = params.get("projectDescription").toString();
						selectedOwnerGroups = selectedOwnerGroups.replace("[", "");
						selectedOwnerGroups = selectedOwnerGroups.replace("]", "");
						int duplicateProject = projectService.checkDuplicateProject(projectName);
						if (duplicateProject != 0) {
							response.put("message", "Project already Exist!!");
							response.put("status", "failed");
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						} else {
							long projectId = projectService.insertDataIntoProjectTable(projectName, projectDescription);
							if (projectId <= 0) {
								response.put("status", "failed");
								
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}
							StringTokenizer tokenizer = new StringTokenizer(selectedOwnerGroups, ",");

							String userId = "";
							while (tokenizer.hasMoreTokens()) {
								userId = tokenizer.nextToken();
								userId = userId.replace("\"", "");
								if (userId != null && !userId.equals("") && !userId.equals("null")) {
									String activeDirectoryFlag = appDbConnectionProperties
											.getProperty("isActiveDirectoryAuthentication");
									LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
									if (activeDirectoryFlag.equals("Y")) {
										int update = projectService.insertProjectToGroupAssociationActive(projectId,
												userId, "Y");
										if (update <= 0) {
											response.put("status", "failed");
											response.put("message", "There was a problem");
											return new ResponseEntity<Object>(response, HttpStatus.OK);
										}
									} else {
										int update = projectService.insertProjectToGroupAssociation(projectId, userId,
												"Y");
										if (update <= 0) {
											response.put("status", "failed");
											response.put("message", "There was a problem");
											return new ResponseEntity<Object>(response, HttpStatus.OK);
										}
									}
								}
							}
							// changes regarding Audit trail
							UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
									DatabuckConstants.DBK_FEATURE_PROJECT, formatter.format(new Date()),  projectId,
									DatabuckConstants.ACTIVITY_TYPE_CREATED,projectName );
						}
						response.put("status", "success");
						response.put("message", "Inserted Successfully");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("message", "Please provide excepted parameter.");
						response.put("status", "failed");
						LOG.error("Please provide excepted parameter.");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			if(e.getMessage().contains("Data too long for column")){
				response.put("message", "Data too long for column name");
				response.put("status", "failed");
			}
			else{
				response.put("message", e.getMessage());
				response.put("status", "failed");
			}
			
			
			
			
			LOG.error(e.getMessage());
			LOG.info("dbconsole/addNewProjectIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@PostMapping(value = "/dbconsole/editProject")
	public ResponseEntity<Object> editProject(@RequestHeader HttpHeaders header,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/editProject - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+header.get("token").get(0).toString());
			List<User> lstUser = null;
			List<User> lstassignUser = null;
			LOG.debug("Getting request parameters  " + params);
			long projectId = Long.parseLong(params.get("projectId").toString());
			Project selectedProject = projectService.getSelectedProject(projectId);
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
			if (activeDirectoryFlag.equals("Y")) {
				lstUser = groupService.getAllGroupsfromActiveDirectory();
				lstassignUser = groupService.getAllassignGroupsfromActiveDirectory(projectId);

			} else {
				lstUser = groupService.getAllGroups();
				lstassignUser = groupService.getAllassignGroups(projectId);

			}
			Map<String, Object> result = new HashMap<>();
			result.put("groupList", lstUser);
			result.put("assignedgroupList", lstassignUser);
			result.put("selectedProject", selectedProject);
			response.put("result", result);
			response.put("status", "success");
			response.put("message", "success");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "failed");
			response.put("message", "Active Directory is Enable,Unable to get User Details");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/editProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/dbconsole/addNewProject")
	public ResponseEntity<Object> addNewProject(@RequestHeader HttpHeaders header) {
		LOG.info("dbconsole/addNewProject - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+header.get("token").get(0).toString());
			List<User> lstUser = null;
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);
			if (activeDirectoryFlag.equals("Y")) {
				lstUser = groupService.getAllGroupsfromActiveDirectory();

			} else {
				lstUser = groupService.getAllGroups();

			}
			Map<String, Object> result = new HashMap<>();
			result.put("groupList", lstUser);
			response.put("result", result);
			response.put("status", "success");
			response.put("message", "success");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "failed");
			response.put("message", "There was a problem");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/addNewProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/updateProjectIntoDatabase")
	public ResponseEntity<Object> updateProjectIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/updateProjectIntoDatabase - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + params);
					if (params.containsKey("projectName") && params.containsKey("projectDescription")
							&& params.containsKey("selectedOwnerGroups") && params.containsKey("id")
							&& params.containsKey("oldProject")) {
						String projectName = params.get("projectName").toString();
						String projectDescription = params.get("projectDescription").toString();
						String selectedOwnerGroups = params.get("selectedOwnerGroups").toString();
						int id = (int) params.get("id");
						String oldProject = params.get("oldProject").toString();

						String activeDirectoryFlag = appDbConnectionProperties
								.getProperty("isActiveDirectoryAuthentication");
						LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

						selectedOwnerGroups = selectedOwnerGroups.replace("[", "");
						selectedOwnerGroups = selectedOwnerGroups.replace("]", "");

						boolean isSuccess = true;
						int isDuplicate = 0;

						int duplicateProject;
						if (!oldProject.equals(projectName))

						{
							duplicateProject = projectService.checkDuplicateProject(projectName);
						} else {

							duplicateProject = 0;
						}
						if (duplicateProject != 0) {
							LOG.info("Project alredy Exist!!");
							isDuplicate = 1;
							isSuccess = false;
							if (isDuplicate == 1) {
								response.put("status", "failed");
								response.put("message", "Duplicate project name.");
								return new ResponseEntity<Object>(response, HttpStatus.OK);
							}
						} else {
							long projectId = projectService.updateDataIntoProjectTable(projectName, projectDescription,
									id);
							// changes regarding Audit trail
							UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
									DatabuckConstants.DBK_FEATURE_PROJECT, formatter.format(new Date()), (long) id,
									DatabuckConstants.ACTIVITY_TYPE_EDITED,projectName );
							if (projectId <= 0) {
								isSuccess = false;
							}
							Long projectid = new Long(id);
							if (activeDirectoryFlag.equals("Y")) {

								int success = projectService.delProjectToGroupAssociationActive(projectid);
								if (success > 0) {
									LOG.info("Old project Association are Removed");
								}

							} else {
								int success = projectService.delProjectToGroupAssociation(projectid);
								if (success > 0) {
									LOG.info("Old project Association are Removed");
								}
							}

							StringTokenizer tokenizer = new StringTokenizer(selectedOwnerGroups, ",");

							String userId = "";
							while (tokenizer.hasMoreTokens()) {
								userId = tokenizer.nextToken();
								userId = userId.replace("\"", "");
								// Long ursId = -1L;
								if (userId != null && !userId.equals("") && !userId.equals("null")) {
									// ursId = Long.parseLong(userId);

									LOG.debug("projectid-->" + projectid);
									LOG.debug("ursId-->" + userId);

									if (activeDirectoryFlag.equals("Y")) {

										int update = projectService.updateProjectToGroupAssociationActive(projectid,
												userId, "Y");
										if (update <= 0) {
											isSuccess = false;

										}
									} else {
										int update = projectService.updateProjectToGroupAssociation(projectid, userId,
												"Y");
										if (update <= 0) {
											isSuccess = false;
										}
									}
								}
							}
						}

						if (isSuccess) {
							response.put("status", "success");
							response.put("message", "Updated successfully !!");
							
							return new ResponseEntity<Object>(response, HttpStatus.OK);
						} else {
							response.put("status", "Fail");
							response.put("message", "This has failed");
							LOG.error("This has failed");
							
							return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						response.put("message", "Please provide excepted parameter.");
						response.put("status", "failed");
						LOG.error("Please provide excepted parameter.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();			
			if(e.getMessage().contains("Data too long for column"))
			{
				response.put("message", "Data too long for column name");
				response.put("status", "failed");
			}
			else
			{
				response.put("message", e.getMessage());
				response.put("status", "failed");
			}
			
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateProjectIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/deleteProject")
	public ResponseEntity<Object> deleteProject(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) throws IOException {
		LOG.info("dbconsole/deleteProject - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			long projectId = Long.parseLong(params.get("projectId").toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + params);
					String projectName = iUserDAO.getProjectNameByProjectId(projectId);
					boolean deleteProject = projectService.deleteProject(projectId);
					if (deleteProject == true) {
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_PROJECT, formatter.format(new Date()),  projectId,
								DatabuckConstants.ACTIVITY_TYPE_DELETED,projectName );
						response.put("status", "success");
						response.put("message", "Project deleted successfully");
					} else if (deleteProject == false) {
						response.put("status", "fail");
						response.put("message", "Associated Project can't Deleted");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/deleteProject - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/loadDimensionRecordList", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> loadDimensionRecordList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/loadDimensionRecordList - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			String sIsEDimensionpresentSql = "select idDimension, dimensionName from dimension;";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sIsEDimensionpresentSql);
			ArrayList <Dimension> dimentionList = new ArrayList<Dimension>();
			while (queryForRowSet.next()) {
				Dimension dimension= new Dimension();
				dimension.setDimensionName(queryForRowSet.getString("dimensionName"));
				dimension.setIdDimension(queryForRowSet.getInt("idDimension"));
				dimentionList.add(dimension);
			}
			response.put("message", "Dimention list fetched successfully!!");
			response.put("status", "success");
			response.put("result", dimentionList);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("message", "Error occurred while fetching the dimension list");
			response.put("status", "failed");
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/loadDimensionRecordList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/dbconsole/deleteDimensionRecord", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> deleteDimensionRecord(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) {
		LOG.info("dbconsole/deleteDimensionRecord - START");
		String status="",message="";
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			String IdDimension = params.get("DimensionIdToDelete");
			LOG.debug("*********************" + IdDimension);
			if(checkDimensionMapped(IdDimension)) {				
				response.put("message", "The dimension is already used in Rule Catalog ,Custom Rule.");
				response.put("status", "failed");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}else {
				String dimensionName = iUserDAO.getDimensionNameByDimensionId(Long.valueOf(IdDimension));
				deletDimensionData(IdDimension);
				// changes regarding Audit trail
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_DIMENSION, formatter.format(new Date()), Long.valueOf(IdDimension),
						DatabuckConstants.ACTIVITY_TYPE_DELETED,dimensionName );

				response.put("message", "Dimension Deleted Successfully !! ");
				response.put("status", "Success");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			
			

			
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("message", "Error occured while deleting the dimension");
			response.put("status", "failed");
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/deleteDimensionRecord - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	private void deletDimensionData(String DimensionIdToDelete) throws Exception {

		String deleteSql = "delete from dimension where idDimension = %1$s";

		try {
			deleteSql = String.format(deleteSql, DimensionIdToDelete);
			jdbcTemplate.update(deleteSql);
		} catch (Exception oException) {
			oException.printStackTrace();
		}

	}
	
	private boolean checkDimensionMapped(String DimensionIdToCheck) {
		int grulecount=0,rcatalogcount=0,scatalogcount=0,defect_count=0;
		
		String Sql = "select count(*) as count from listColGlobalRules where dimension_id = %1$s";
		try {
			Sql = String.format(Sql, DimensionIdToCheck);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(Sql);
			if (queryForRowSet.next()) {
				grulecount = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		

		
		String rcatalogSql = "select count(*) as count from listApplicationsRulesCatalog where dimension_id = %1$s";
		try {
			rcatalogSql = String.format(rcatalogSql, DimensionIdToCheck);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(rcatalogSql);
			if (queryForRowSet.next()) {
				rcatalogcount = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		
		String scatalogSql = "select count(*) as count from staging_listApplicationsRulesCatalog where dimension_id = %1$s";
		try {
			scatalogSql = String.format(scatalogSql, DimensionIdToCheck);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(scatalogSql);
			if (queryForRowSet.next()) {
				scatalogcount = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		
		String defectSql = "select count(*) as count from defect_codes where dimension_id = %1$s";
		try {
			defectSql = String.format(defectSql, DimensionIdToCheck);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(defectSql);
			if (queryForRowSet.next()) {
				defect_count = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		return (grulecount> 0 || rcatalogcount > 0 || scatalogcount> 0 || defect_count > 0);
	}

	@RequestMapping(value = "/dbconsole/saveDimensionRecord", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveDimensionRecord(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/saveDimensionRecord - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			String DimensionId = params.get("DimensionId").toString();
			String DimensionName = params.get("DimensionName").toString();
			boolean lNewDomain = (DimensionId.equalsIgnoreCase("-1")) ? true : false;
			String sInsertSql = "insert into dimension (dimensionName) values ('%1$s');";
			String sUpdateSql = "update dimension set dimensionName = '%1$s' where idDimension = %2$s;";
			String sSqlToUpdate = "";
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			boolean isDuplicate = iUserDAO.checkDuplicateDimension(DimensionName);
			if(!isDuplicate) {
				if (lNewDomain) {
					sSqlToUpdate = String.format(sInsertSql, DimensionName);
					// Query compatibility changes for both POSTGRES and MYSQL
					String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddimension"
							: "idDimension";
	
					KeyHolder keyHolder = new GeneratedKeyHolder();
					String finalSSqlToUpdate = sSqlToUpdate;
					jdbcTemplate.update(new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement pst = con.prepareStatement(finalSSqlToUpdate, new String[] { key_name });
							return pst;
						}
					}, keyHolder);
	
					Long idDimension = keyHolder.getKey().longValue();
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_DIMENSION, formatter.format(new Date()),  idDimension,
							DatabuckConstants.ACTIVITY_TYPE_CREATED,DimensionName );
				} else {
					sSqlToUpdate = String.format(sUpdateSql, DimensionName, DimensionId);
					jdbcTemplate.update(sSqlToUpdate);
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_DIMENSION, formatter.format(new Date()), Long.valueOf(DimensionId),
							DatabuckConstants.ACTIVITY_TYPE_EDITED,DimensionName );
				}
				response.put("message", "Dimension Record Successfully Saved");
				response.put("status", "Success");
			}else {
				response.put("message", "Dimension Already Exists");
				response.put("status", "Failed");
			}
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			response.put("message", sExceptionMsg);
			response.put("status", "Failed");
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/saveDimensionRecord - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		}
	}

	@GetMapping("/dbconsole/dimensionCSV")
	public ResponseEntity<Object> dimensionCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/dimensionCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<Dimension> dimensionTableData = projectDao.getAllDimension();
					if (dimensionTableData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "idDimension", "dimensionName" };
						String[] header = { "Dimension ID", "Dimension Name" };
						csvWriter.writeHeader(header);
						for (Dimension dimension : dimensionTableData) {
							csvWriter.write(dimension, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/dimensionCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/projectCSV")
	public ResponseEntity<Object> projectCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/projectCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<Project> projectTableData = projectService.getAllProjects();
					if (projectTableData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "idProject", "projectName", "projectDescription" ,"domainId" };
						String[] header = { "Project Id", "Project Name", "Description" ,"Domain Id" };
						csvWriter.writeHeader(header);
						for (Project project : projectTableData) {
							csvWriter.write(project, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/projectCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/accessLogCSV")
	public ResponseEntity<Object> accessLogCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/accessLogCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<AccessLog> accessLogTableData = taskService.getAllAccessLog();
					if (accessLogTableData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "userName", "activity", "activityLogTime", "applicationUrl" };
						String[] header = { "User", "Activity", "Date Time", "Application URL" };
						csvWriter.writeHeader(header);
						for (AccessLog accessLog : accessLogTableData) {
							csvWriter.write(accessLog, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/accessLogCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/loginGroupMappingCSV")
	public ResponseEntity<Object> loginGroupMappingCSV(@RequestHeader HttpHeaders headers,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/loginGroupMappingCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<LoginGroupMapping> loginGroupMapping = iUserDAO.getListOfLoginGroupMapping();
					if (loginGroupMapping != null) {
						// response.put("result", retrieveRecord(usersTableData));
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "loginGroupName", "approverGroup", "assignedRoles", "assignedProjects" };
						String[] header = { "Login Group Name", "In Approver Group", "Assigned Roles",
								"Assigned Projects" };
						csvWriter.writeHeader(header);
						// ArrayList<Map<String,Object>> userMap = retrieveRecord(usersTableData);
						for (LoginGroupMapping loginGroupMappingdata : loginGroupMapping) {
							csvWriter.write(loginGroupMappingdata, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/loginGroupMappingCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/featuresAccessControlCSV")
	public ResponseEntity<Object> featuresAccessControlCSV(@RequestHeader HttpHeaders headers,
			HttpServletResponse httpResponse) {
		LOG.info("dbconsole/featuresAccessControlCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<FeaturesAccessControl> featuresAccessControlTableData = userSettingService
							.getComponentAccessControlData();
					if (featuresAccessControlTableData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "moduleName", "feature", "roles" };
						String[] header = { "Module Name", "Feature", "Roles" };
						csvWriter.writeHeader(header);
						for (FeaturesAccessControl accessControl : featuresAccessControlTableData) {
							csvWriter.write(accessControl, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/featuresAccessControlCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/dbconsole/domainCSV")
	public ResponseEntity<Object> domainCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/domainCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<DomainLibrary> domainTableData = userSettingService.getDomainManagementServiceData();
					if (domainTableData != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "domainId", "domainName", "isGlobalDomain", "projectIds", "projectNames" };
						String[] header = { "Domain Id", "Domain AccessKey", "Is Global Domain", "Project Ids",
								"Project Names" };
						csvWriter.writeHeader(header);
						for (DomainLibrary domain : domainTableData) {
							csvWriter.write(domain, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/domainCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/dbconsole/loadDefectCodeViewList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadDefectCodeViewList(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/loadDefectCodeViewList - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			JSONObject oJsonResponse = loadDefectCodeDataList();
			response.put("status", "success");
			response.put("message", "success");
			response.put("result", oJsonResponse.toMap());
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("message", oException.getMessage());
			response.put("status", "failed");
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/loadDefectCodeViewList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	private JSONObject loadDefectCodeDataList() throws Exception {
		List<HashMap<String, String>> aDataViewList = null;

		String sDataSql, sDataViewList;
		String[] aColumnSpec = new String[] { "RowId", "DefectCode", "DefectDescription", "DimensionId",
				"DimensionName" };

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

	@RequestMapping(value = "/dbconsole/mainDefectCodeHandler", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> mainDefectCodeHandler(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> params) throws IOException {
		LOG.info("dbconsole/mainDefectCodeHandler - START");
		Map<String, Object> response = new HashMap<>();
		try {
			LOG.debug("token "+headers.get("token").get(0).toString());
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			LOG.debug("Getting request parameters  " + params);
			JSONObject oDefectCodeData = new JSONObject();
			String sContext;
			String sMsg = "Sample server call";
			HttpStatus responseCode = HttpStatus.OK;
			boolean lStatus = true;
			String status = "";

			boolean lNewReport = false;

			sContext = params.get("context");
			// oDefectCodeData = oWrapperData.getJSONObject("Data");
			oDefectCodeData.put("RowId", params.get("rowId"));
			oDefectCodeData.put("DefectCode", params.get("defectCode"));
			oDefectCodeData.put("DefectDescription", params.get("defectDescription"));
			oDefectCodeData.put("DimensionId", params.get("dimensionId"));

			DateUtility.DebugLog("Defect Code Data 01",
					String.format("Begin controller Context = %1$s, Data = %2$s", sContext, oDefectCodeData));
			/* fix for ' character in defect save */
			if (oDefectCodeData.getString("DefectDescription") != null) {
				oDefectCodeData.put("DefectDescription",
						oDefectCodeData.getString("DefectDescription").trim().replace("'", "\\'"));
			}
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			switch (sContext) {

			case "saveDefectCodeRecord":

				DateUtility.DebugLog("Defect Code Data 02", oDefectCodeData.getString("RowId"));
				lNewReport = (Integer.parseInt(oDefectCodeData.getString("RowId")) < 0) ? true : false;
				DateUtility.DebugLog("Defect Code Data 03", String.format("%1$s", lNewReport));

				// check Vulnerable Data even it is checked in Java Script so database is
				// absolutely safe
				sMsg = validateVulnerableData(oDefectCodeData);
				if (checkVulnerability.getVulnerabilityTagCounts(oDefectCodeData.getString("DefectDescription") + ' '
						+ oDefectCodeData.getString("DefectCode")) > 0) {
					sMsg = "Submitted Defect data record contains Vulnerable Contents, cannot save it to database.";
					responseCode = HttpStatus.OK;
					status = "failed";
				}

				if (sMsg.length() > 0) {
					lStatus = false;
				} else if (lNewReport) {
					String sRetMsg = "", sInsertSql = "";
					try {
						boolean isCombinationExist = iUserDAO.getDefectCodeAndDiamensionId(
								oDefectCodeData.getString("DefectCode"),
								Integer.parseInt(oDefectCodeData.getString("DimensionId")));
						if (isCombinationExist) {
							sRetMsg = "Defect Code and Dimension Name combination already exists";
							status = "failed";
						} else {
							sInsertSql = String.format(
									"insert into defect_codes(defect_code, defect_description, dimension_id) values ('%1$s', '%2$s', %3$s);",
									oDefectCodeData.getString("DefectCode"),
									oDefectCodeData.getString("DefectDescription"),
									oDefectCodeData.getString("DimensionId"));
							// Query compatibility changes for both POSTGRES and MYSQL
							String key_name = "row_id";

							KeyHolder keyHolder = new GeneratedKeyHolder();
							String finalSSqlToUpdate = sInsertSql;
							jdbcTemplate.update(new PreparedStatementCreator() {
								public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement pst = con.prepareStatement(finalSSqlToUpdate, new String[] { key_name });
									return pst;
								}
							}, keyHolder);

							Long defectCodeId = keyHolder.getKey().longValue();
							iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
									DatabuckConstants.DBK_FEATURE_DEFECTCODES, formatter.format(new Date()), defectCodeId,
									DatabuckConstants.ACTIVITY_TYPE_CREATED,oDefectCodeData.getString("DefectCode"));
							sRetMsg = "Successfully saved new Defect Code Record";
							status = "success";
							responseCode = HttpStatus.OK;
						}
					} catch (Exception oException) {
						oException.printStackTrace();
						sRetMsg = "Error occurred while inserting defect code record";
						responseCode = HttpStatus.OK;
						status = "failed";
					}
					sMsg = sRetMsg;
				} else {
					// sMsg = updateExistingDefectCodeRecord(oDefectCodeData);
					String sRetMsg = "", sUpdateSql = "";
					try {
						/*
						 * boolean isCombinationExist =
						 * iUserDAO.getDefectCodeAndDiamensionId(oDefectCodeData.getString("DefectCode")
						 * , Integer.parseInt(oDefectCodeData.getString("DimensionId"))); if
						 * (isCombinationExist) { sRetMsg =
						 * "Defect Code and Dimension Name combination already exists"; status =
						 * "failed"; } else {
						 */
						sUpdateSql = String.format(
								"update defect_codes set defect_description = '%1$s', dimension_id = %2$s where row_id = %3$s",
								oDefectCodeData.getString("DefectDescription"),
								oDefectCodeData.getString("DimensionId"), oDefectCodeData.getString("RowId"));
						jdbcTemplate.update(sUpdateSql);
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_DEFECTCODES, formatter.format(new Date()), Long.valueOf(oDefectCodeData.getString("RowId")),
								DatabuckConstants.ACTIVITY_TYPE_EDITED,oDefectCodeData.getString("DefectCode"));
						sRetMsg = "Successfully updated Defect Code Record";
						status = "success";
						responseCode = HttpStatus.OK;
						// }
					} catch (Exception oException) {
						oException.printStackTrace();
						sRetMsg = "Error occurred while updating defect code record";
						status = "failed";
						responseCode = HttpStatus.OK;
					}
					sMsg = sRetMsg;
				}
				break;

			case "deleteDefectCodeRecord":
				// sMsg = deleteDefectCodeRecord(oDefectCodeData);
				String sRetMsg = "", sDeleteSql = "";
				try {
					if(checkDefectCodeMapped(oDefectCodeData.getString("DefectCode").trim())) {
						sRetMsg = "The Defect code is already used in Rule Catalog.";						
						status = "failed";
						responseCode = HttpStatus.OK;
					}else {
						String defectCode = oDefectCodeData.getString("DefectCode");
						sDeleteSql = String.format("delete from defect_codes where row_id = %1$s",
								oDefectCodeData.getString("RowId"));
						jdbcTemplate.update(sDeleteSql);
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_DEFECTCODES, formatter.format(new Date()), Long.valueOf(oDefectCodeData.getString("RowId")),
								DatabuckConstants.ACTIVITY_TYPE_DELETED,defectCode);
						sRetMsg = "Successfully deleted Defect Code Data";
						status = "success";
						responseCode = HttpStatus.OK;
					}
					
				} catch (Exception oException) {
					oException.printStackTrace();
					sRetMsg = "Error occurred while deleting defect code record";
					status = "failed";
					responseCode = HttpStatus.OK;
				}
				sMsg = sRetMsg;
				break;

			default:
			}

			response.put("status", status);
			response.put("message", sMsg);

			DateUtility.DebugLog("Defect Code Data 02", "End controller");
			
			return new ResponseEntity<>(response, responseCode);
		} catch (Exception oException) {
			oException.printStackTrace();
			response.put("status", "failed");
			response.put("message", oException.getMessage());
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/mainDefectCodeHandler - END");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	
	private boolean checkDefectCodeMapped(String Defect_Code) {
		int rcatalogcount=0,scatalogcount=0;
		
		
		

		
		String rcatalogSql = "select count(*) as count from listApplicationsRulesCatalog where defect_code = '%1$s'";
		try {
			rcatalogSql = String.format(rcatalogSql, Defect_Code);
			System.out.println("rcatalogSql "+rcatalogSql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(rcatalogSql);
			if (queryForRowSet.next()) {
				rcatalogcount = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		
		String scatalogSql = "select count(*) as count from staging_listApplicationsRulesCatalog where defect_code = '%1$s'";
		try {
			scatalogSql = String.format(scatalogSql, Defect_Code);
			LOG.debug("rcatalogSql "+scatalogSql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(scatalogSql);
			if (queryForRowSet.next()) {
				scatalogcount = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		
		
		return ( rcatalogcount > 0 || scatalogcount> 0 );
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
			sInsertSql = String.format(
					"insert into defect_codes(defect_code, defect_description, dimension_id) values ('%1$s', '%2$s', %3$s);",
					oDefectCodeData.getString("DefectCode"), oDefectCodeData.getString("DefectDescription"),
					oDefectCodeData.getString("DimensionId"));
			jdbcTemplate.update(sInsertSql);
			sRetMsg = "Successfully saved new Defect Code Record";
		} catch (Exception oException) {
			oException.printStackTrace();
			sRetMsg = "Error occurred while inserting defect code record";
		}

		return sRetMsg;
	}

	private String updateExistingDefectCodeRecord(JSONObject oDefectCodeData) {
		String sRetMsg = "", sUpdateSql = "";
		try {
			sUpdateSql = String.format(
					"update defect_codes set defect_description = '%1$s', dimension_id = %2$s where row_id = %3$s",
					oDefectCodeData.getString("DefectDescription"), oDefectCodeData.getString("DimensionId"),
					oDefectCodeData.getString("RowId"));
			jdbcTemplate.update(sUpdateSql);
			sRetMsg = "Successfully updated Defect Code Record";
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

	@GetMapping("/dbconsole/defectCodeCSV")
	public ResponseEntity<Object> defectCodeCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		LOG.info("dbconsole/defectCodeCSV - START");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token))) {
					List<DefectCode> defectCodeList = projectDao.getAllDefectCode();
					LOG.debug(defectCodeList);
					if (defectCodeList != null) {
						httpResponse.setContentType("text/csv");
						String csvFileName = "ProcessData" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "defectCode", "dimensionId", "defectDescription", "dimensionName" };
						String[] header = { "Defect Code", "Dimension Id", "Defect Description", "Dimension Name" };
						csvWriter.writeHeader(header);
						for (DefectCode defectCode : defectCodeList) {
							csvWriter.write(defectCode, fields);
						}
						csvWriter.close();
						response.put("status", "success");
						response.put("message", "File sent");
						
						return new ResponseEntity<Object>(response, HttpStatus.OK);
					} else {
						response.put("status", "failed");
						response.put("message", "Records not found.");
						LOG.error("Records not found.");
						
						return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
					}
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			try {
				httpResponse.sendError(0, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.info("dbconsole/defectCodeCSV - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	private HttpSession getActiveSession(HttpServletRequest request) {
		return request.getSession(false);
	}

	@RequestMapping(value = "/dbconsole/saveLoggingActivity", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> saveLoggingActivity(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> param) {
		LOG.info("dbconsole/saveLoggingActivity - START");
		Map<String, Object> response = new HashMap<>();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			if ("success".equalsIgnoreCase(csvService.validateUserToken(headers.get("token").get(0)))) {
				boolean lRetValue = true;
				int nCount = 0;
				// HttpSession oSession = getActiveSession(oRequest);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();

				// String sIdRole = param.get("idRole").toString();
				String activity = param.get("activity").toString();
				Long idUser = Long.parseLong(param.get("idUser").toString());
				String userName = iUserDAO.getfirstNameByUserId(idUser);
				String activity_Log_Time = formatter.format(date);
				// String sessionId = oSession.getId();
				String sReqURL = param.get("url").toString();

				String sInsertSql = String.format(
						"insert into logging_activity(user_id, user_name, access_url, activity_log_time,"
								+ "session_id, databuck_feature) VALUES (%1$s, '%2$s','%3$s', '%4$s','%5$s', '%6$s' );",
						idUser, userName, sReqURL, activity_Log_Time, " ", activity);

				jdbcTemplate.update(sInsertSql);
				LOG.debug(sInsertSql);
				response.put("message", "Logging Activity Successfully Saved");
				response.put("status", "success");
				
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			} else {
				response.put("status", "failed");
				response.put("message", "Token is expired.");
				LOG.error("Token is expired.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception oException) {
			String sExceptionMsg = oException.getMessage();
			oException.printStackTrace();
			response.put("message", sExceptionMsg);
			response.put("status", "Failed");
			LOG.error(oException.getMessage());
			LOG.info("dbconsole/saveLoggingActivity - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);

		}
	}

	@PostMapping("/dbconsole/updateUserIntoDatabase")
	public ResponseEntity<Object> updateUserIntoDatabase(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> param) {
		LOG.info("dbconsole/updateUserIntoDatabase - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					LOG.debug("Getting request parameters  " + param);
					Long roleId = Long.parseLong(param.get("roleId").toString());
					Long userId = Long.parseLong(param.get("userId").toString());
					String firstName = param.get("firstName").toString();
					String lastName = param.get("lastName").toString();
					String email = param.get("email").toString();
					int update = userservice.updateUser(userId, firstName, lastName, email, roleId);
					if (update > 0) {
						// changes regarding Audit trail
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_USER, formatter.format(new Date()),  userId,
								DatabuckConstants.ACTIVITY_TYPE_EDITED,firstName+" "+lastName );
						response.put("status", "success");
						response.put("message", "User Updated successfully.");
					} else {
						response.put("status", "failed");
						response.put("message", "Failed to Update user.");
					}
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/updateUserIntoDatabase - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
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

	@GetMapping("/dbconsole/generateTokens")
	public ResponseEntity<Object> generateToken(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/generateTokens - START");
		Map<String, Object> response = new HashMap<>();
		try {
			List<String> token = headers.get("token");
			LOG.debug("token "+token.toString());
			if (token != null && !token.isEmpty()) {
				if ("success".equalsIgnoreCase(csvService.validateUserToken(token.get(0)))) {
					String accessToken = randomString();
					String secretAccessToken = randomString();
					userservice.updateIntoSecureAPI(accessToken, secretAccessToken);
					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_GENERATE_API_TOKEN, formatter.format(new Date()), 0l,
							DatabuckConstants.ACTIVITY_TYPE_CREATED,accessToken);
					JSONObject json = new JSONObject();
					json.put("accessToken", accessToken);
					json.put("secretAccessToken", secretAccessToken);
					response.put("status", "success");
					response.put("result", json.toMap());
					response.put("message", "Token generated successfully.");
					
					return new ResponseEntity<Object>(response, HttpStatus.OK);

				} else {
					response.put("status", "failed");
					response.put("message", "Token is expired.");
					LOG.error("Token is expired.");
					
					return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Token is missing in the headers.");
				LOG.error("Token is missing in headers.");
				
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
			response.put("status", "failed");
			LOG.error(e.getMessage());
			LOG.info("dbconsole/generateTokens - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	// Api to get Audit Details for fromDate and toDate
	@RequestMapping(value = "/dbconsole/getLatestAuditTrailDetails", method = RequestMethod.POST)
	public ResponseEntity<Object> getLatestAuditTrailDetails(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getLatestAuditTrailDetails - START");
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String toDate = inputJson.getString("toDate");
						String fromDate = inputJson.getString("fromDate");

						List<LoggingActivity> latestAuditTrailDetails = iTaskDAO.getLatestAuditTrailDetails(toDate,
								fromDate);
						if (latestAuditTrailDetails != null && latestAuditTrailDetails.size() > 0) {
							status = "success";
							message = "LatestAuditTrailDetails Fetched successfully";
							result.put("latestAuditTrailDetails", latestAuditTrailDetails);
						} else {
							message = "Failed to fetch latestAuditTrailDetails";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getLatestAuditTrailDetails - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/clearLogs", method = RequestMethod.POST)
	public ResponseEntity<Object> clearLogs(@RequestHeader HttpHeaders headers, @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/clearLogs - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String fromDate = inputJson.getString("fromDate");
						String toDate = inputJson.getString("toDate");

						int clearLogs = iTaskDAO.clearLogs(fromDate, toDate);
						if (clearLogs > 0) {
							status = "success";
							message = "Logs cleared  successfully";
						} else {
							message = "Failed to clear logs";
							LOG.error(message);
						}
					} else {
						message = "Invalid Request";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/clearLogs - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/downloadCsvforAuditLog", method = RequestMethod.POST)
	public void downloadCsvforAuditLog(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/downloadCsvforAuditLog - START");

		String token = "";

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String toDate = inputJson.getString("toDate");
						String fromDate = inputJson.getString("fromDate");
						List<LoggingActivity> latestAuditTrailDetailsBeanData = iTaskDAO
								.getLatestAuditTrailDetails(toDate, fromDate);
						httpResponse.setContentType("text/csv");
						String csvFileName = "AccessLogCsv" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "activityName","entityId", "entityName", "databuckFeature", "activityLogTime", "userName"};
						String[] header = { "Activity Name","Entity Id", "Entity Name", "Databuck Feature", "Activity Log Time", "User Name"};
						ObjectMapper objectMapper = new ObjectMapper();
						csvWriter.writeHeader(header);
						for (Object fileData : latestAuditTrailDetailsBeanData) {
							JSONObject jsonObject = new JSONObject(fileData);
							LoggingActivity loggingActivity = objectMapper.readValue(jsonObject.toString(),
									LoggingActivity.class);
							csvWriter.write(loggingActivity, fields);
						}
						csvWriter.close();
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in header");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/dbconsole/createDefectCodesByFile", method = RequestMethod.POST)
	public ResponseEntity<Object> createDefectCodesByFile(@RequestHeader HttpHeaders headers,
			@RequestParam("dataupload") MultipartFile multipartFile) {
		LOG.info("dbconsole/createDefectCodesByFile - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (multipartFile.getOriginalFilename().endsWith(".csv")) {
						InputStream inputStream = multipartFile.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
						CSVReader csvReader = new CSVReaderBuilder(bufferedReader).build();
						List<String[]> linesData = csvReader.readAll();
						if (linesData != null && linesData.size() >= 2) {
							JSONObject defectCodesByFile = userSettingService.createDefectCodesByFile(linesData);
							if (defectCodesByFile != null) {
								result = defectCodesByFile.getJSONObject("result");
								message = defectCodesByFile.getString("message");
								status = defectCodesByFile.getString("status");
							}
						} else if (linesData.size() == 1) {
							if (String.join("", linesData.get(0)).trim().equals("")) {
								message = "Unable to process blank file!!";
							} else {
								message = "Unable to process file, it only contains headers!!";
							}
							LOG.error(message);
						} else {
							message = "Unable to process blank file!!";
							LOG.error(message);
						}
					} else {
						message = "Unable to process this file!! Currently this feature supports only csv files!!";
						LOG.error(message);
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
				// changes regarding Audit trail
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_DEFECTCODES, formatter.format(new Date()), 0l,
						DatabuckConstants.ACTIVITY_TYPE_CREATED,"Bulk Upload");
			} else {
				message = "Token is missing in header";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("message", message);
		json.put("status", status);
		LOG.info("dbconsole/createDefectCodesByFile - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/createDefectCodesByFileByPath", method = RequestMethod.POST)
	public ResponseEntity<Object> createDefectCodesByFileByPath(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/createDefectCodesByFileByPath - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String filePath = inputJson.getString("filePath");
						try{
							if (filePath != null && !filePath.trim().isEmpty()) {
								File file = new File(filePath);
								if (file.getName().endsWith(".csv")) {
									InputStream inputStream = new FileInputStream(file);
									BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
									CSVReader csvReader = new CSVReaderBuilder(bufferedReader).build();
									List<String[]> linesData = csvReader.readAll();
									if (linesData != null && linesData.size() >= 2) {
										JSONObject defectCodesByFile = userSettingService
												.createDefectCodesByFile(linesData);
										if (defectCodesByFile != null) {
											result = defectCodesByFile.getJSONObject("result");
											message = defectCodesByFile.getString("message");
											status = defectCodesByFile.getString("status");
										}
									} else if (linesData.size() == 1) {
										if (String.join("", linesData.get(0)).trim().equals("")) {
											message = "Unable to process blank file!!";
										} else {
											message = "Unable to process file, does not contains data!!";
										}
										LOG.error(message);
									} else {
										message = "Unable to process blank file!!";
										LOG.error(message);
									}
								} else {
									message = "Unable to process this file!! Currently this feature supports only csv files!!";
									LOG.error(message);
								}
							}
							// changes regarding Audit trail
							UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
									DatabuckConstants.DBK_FEATURE_DEFECTCODES, formatter.format(new Date()), 0l,
									DatabuckConstants.ACTIVITY_TYPE_CREATED,"Bulk Upload");
						}catch (Exception e){
							e.printStackTrace();
							message = "Invalid filePath";
							LOG.error(message);
						}
					} else {
						message = "Request Failed";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("message", message);
		json.put("status", status);
		LOG.info("dbconsole/createDefectCodesByFileByPath - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getLogsList", method = RequestMethod.POST)
	public ResponseEntity<Object> displayLogsInUI(@RequestHeader HttpHeaders headers,
												  @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/displayLogsInUI - START");
		JSONObject json = new JSONObject();
		JSONArray result = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String logFolderPath = inputJson.getString("logFolderPath");
						File logFolderfile = new File(logFolderPath);

						// Check if the logs folder exists and is a directory
						if (!logFolderfile.exists() || !logFolderfile.isDirectory()) {
							message = "Folder does not exist or is not a directory:" + logFolderPath;
						} else {
							File[] files = logFolderfile.listFiles();
							for (File file : files) {
								JSONObject fileObject = new JSONObject();

								fileObject.put("name", file.getName());
								fileObject.put("isDirectory", file.isDirectory());
								result.put(fileObject);
							}
							message="Log file list fetch successfully";
							status="success";
						}

					} else {
						message = "Invalid Request";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/displayLogsInUI - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/readLogFileDataByPath", method = RequestMethod.POST)
	public ResponseEntity<Object> readLogFileDataByPath(@RequestHeader HttpHeaders headers,
														@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/readLogFileDataByPath - START");

		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String lastLines = "";
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String logFilePath = inputJson.getString("logFilePath");
						int n = 100;
						try {
							try{
								if(inputJson.has("noOfLines") && inputJson.get("noOfLines") != null){
									n=inputJson.getInt("noOfLines");
								}
							}catch (Exception exception){
								n = 100;
							}

							if (logFilePath != null && !logFilePath.trim().isEmpty()) {

								List<String> lines = new ArrayList<>(n);
								// read last 100 lines
								try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
									String line;
									while ((line = reader.readLine()) != null) {
										lines.add(line);
										if (lines.size() > n) {
											lines.remove(0);
										}
									}
								}
								StringBuilder sb = new StringBuilder();
								sb = sb.append(String.join("\n", lines));
								lastLines = sb.toString();
								message = "File log fetch successfully";
								status = "success";
							} else {
								message = "Invalid filePath";
							}
						} catch (Exception e) {
							e.printStackTrace();
							message = "Invalid filePath";
							LOG.error(message);
						}
					} else {
						message = "Request Failed";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("result", lastLines);
		json.put("message", message);
		json.put("status", status);
		LOG.info("dbconsole/readLogFileDataByPath - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getEnvironmentVariables", method = RequestMethod.GET)
	public ResponseEntity<Object> getEnvironmentVariables(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/getEnvironmentVariables - START");
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					String databuckHome = "";
					if (System.getenv("DATABUCK_HOME") != null) {
						databuckHome = System.getenv("DATABUCK_HOME");
					} else if (System.getProperty("DATABUCK_HOME") != null) {
						databuckHome = System.getProperty("DATABUCK_HOME");
					}
					String tomcatHome = "";
					if (System.getenv("TOMCAT_HOME") != null) {
						tomcatHome = System.getenv("TOMCAT_HOME");
					} else if (System.getProperty("TOMCAT_HOME") != null) {
						tomcatHome = System.getProperty("TOMCAT_HOME");
					}
					if ((databuckHome != null && !databuckHome.trim().isEmpty()) || (tomcatHome != null && !tomcatHome.trim().isEmpty())) {
						result.put("databuckHome", databuckHome);
						result.put("tomcatHome", tomcatHome);
						status = "success";
						message = "Environment Variable fetch successfully";
					} else {
						message = "Invalid Environment Variable";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getEnvironmentVariables - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
	@RequestMapping(value = "/dbconsole/getLatestLoginActivityDetails", method = RequestMethod.POST)
	public ResponseEntity<Object> getLatestLoginActivityDetails(@RequestHeader HttpHeaders headers,
															 @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getLatestLoginActivityDetails - START");
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String toDate = inputJson.getString("toDate");
						String fromDate = inputJson.getString("fromDate");

						List<LoginTrail> latestLoginActivityDetails = iTaskDAO.getLatestLoginActivityDetails(toDate,
								fromDate);
						if (latestLoginActivityDetails != null && latestLoginActivityDetails.size() > 0) {
							status = "success";
							message = "getLatestLoginActivityDetails Fetched successfully";
							result.put("getLatestLoginActivityDetails", latestLoginActivityDetails);
						} else {
							message = "Failed to fetch latestAuditTrailDetails";
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
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/getLatestLoginActivityDetails - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}
	@RequestMapping(value = "/dbconsole/downloadCsvforLoginActivity", method = RequestMethod.POST)
	public void downloadCsvforLoginActivity(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse,
									   @RequestBody String inputJsonStr) {
		LOG.info("dbconsole/downloadCsvforLoginActivity - START");

		String token = "";

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting request parameters  " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String toDate = inputJson.getString("toDate");
						String fromDate = inputJson.getString("fromDate");
						List<LoginTrail> latestAuditTrailDetails = iTaskDAO
								.getLatestLoginActivityDetails(toDate, fromDate);
						httpResponse.setContentType("text/csv");
						String csvFileName = "AccessLogCsv" + LocalDateTime.now() + ".csv";
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
						httpResponse.setHeader(headerKey, headerValue);
						ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
								CsvPreference.STANDARD_PREFERENCE);
						String[] fields = { "userName", "databuckFeature", "activityLogTime","sessionId" };
						String[] header = { "User Name", "Activity Name", "Activity Log Time","Session Id" };
						ObjectMapper objectMapper = new ObjectMapper();
						csvWriter.writeHeader(header);
						for (Object fileData : latestAuditTrailDetails) {
							JSONObject jsonObject = new JSONObject(fileData);
							LoginTrail loginTrail = objectMapper.readValue(jsonObject.toString(),
									LoginTrail.class);
							csvWriter.write(loginTrail, fields);
						}
						csvWriter.close();
						LOG.info("dbconsole/downloadCsvforLoginActivity - END");
					}
				} else {
					LOG.error("Token is expired.");
					throw new Exception("Token Expired.");
				}
			} else {
				LOG.error("Token is missing in headers.");
				throw new Exception("Token is missing in header");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
