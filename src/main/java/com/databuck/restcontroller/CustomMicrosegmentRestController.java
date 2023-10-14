package com.databuck.restcontroller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.CustomMicrosegment;
import com.databuck.bean.UserToken;
import com.databuck.dao.CustomMicrosegmentDao;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.constants.DatabuckConstants;
import com.databuck.service.CustomMicrosegmentService;
import com.databuck.service.ExecutiveSummaryService;

@CrossOrigin(origins = "*")
@RestController
public class CustomMicrosegmentRestController {
	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	private CustomMicrosegmentService customMicrosegmentService;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	private IProjectDAO projectDao;

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private CustomMicrosegmentDao customMicroegmentDao;

	@Autowired
	private CustomMicrosegmentDao customMicrosegmentDao;

	@Autowired
	ITaskDAO iTaskDAO;

	private static final Logger LOG = Logger.getLogger(CustomMicrosegmentRestController.class);
	@RequestMapping(value = "/dbconsole/getCustomMicroSegmentColumnNamesForCheck", method = RequestMethod.POST)
	public ResponseEntity<Object> getCustomMicroSegmentColumnNamesForCheck(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {		
		LOG.info("/dbconsole/getCustomMicroSegmentColumnNamesForCheck - START");

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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.getLong("idData");
						String checkName = inputJson.getString("checkName");

						// Validate template Id
						String columnNames = validationcheckdao.getNameFromListDataSources(idData);

						if (columnNames != null && !columnNames.isEmpty()) {
							// fetch column names
							List<String> templateColumnNames = customMicrosegmentDao
									.getCustomMicroSegmentsColumnNamesForCheck(idData, checkName);

							result = new JSONArray(templateColumnNames);
							status = "success";

						} else
							message = "Invalid Template Id";

					} else {
						message = "Invalid Request";
						responseStatus = HttpStatus.BAD_REQUEST;
					}

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("message "+message);
		LOG.info("/dbconsole/getCustomMicroSegmentColumnNamesForCheck - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getCustomMicroSegmentsByTemplateId", method = RequestMethod.POST)
	public ResponseEntity<Object> getCustomMicroSegmentsByTemplateId(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("/dbconsole/getCustomMicroSegmentsByTemplateId - START");
		

		JSONObject json = new JSONObject();
		JSONArray results = new JSONArray();
		String status = "failed";
		String message = "";
		String token = "";

		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.getLong("idData");

						String templateName = validationcheckdao.getNameFromListDataSources(idData);

						if (templateName != null && !templateName.isEmpty()) {

							List<CustomMicrosegment> customMicrosegmentList = customMicroegmentDao
									.getCustomMicroSegmentsByIdData(idData);
							if (customMicrosegmentList != null && customMicrosegmentList.size() > 0) {
								message = "CustomMicrosegmentList List Fetched Successfully";
								results = new JSONArray(customMicrosegmentList);
								status = "success";
							} else {
								message = "Template does not have any custom microsegment";
								status = "success";
							}
						} else {
							message = "Invalid TemplateId";
						}
					} else {
						message = "Invalid Request";
					}
				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}

		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			message = "Request failed";
			e.printStackTrace();
		}

		json.put("status", status);
		json.put("message", message);
		json.put("result", results);
		LOG.info("message "+message);
		LOG.info("/dbconsole/getCustomMicroSegmentsByTemplateId - END");

		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/addCustomMicrosegments", method = RequestMethod.POST)
	public ResponseEntity<Object> addCustomMicrosegments(@RequestHeader HttpHeaders headers,
			@RequestBody CustomMicrosegment customMicrosegment) {

		
		LOG.info("/dbconsole/addCustomMicrosegments - START");

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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);

				if (tokenStatus.equals("success")) {

					Long idUser = userToken.getIdUser();

					if (idUser > 0 && idUser != null) {
						long templateId = customMicrosegment.getTemplateId();
						String templateName = validationcheckdao.getNameFromListDataSources(templateId);
						LOG.debug("Getting  parameters for customMicrosegment , " + customMicrosegment);
						result = customMicrosegmentService.addCustomMicrosegments(customMicrosegment);
						message = result.getString("message");
						status = result.getString("status");
						// changes regarding Audit trail
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_CUSTOM_MICROSEGMENTS, formatter.format(new Date()), templateId,
								DatabuckConstants.ACTIVITY_TYPE_CREATED,templateName);
					} else
						message = "Invalid User";

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("message "+message);
		LOG.info("/dbconsole/addCustomMicrosegments - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getListOfTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getListOfTemplate(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {		
		
		LOG.info("/dbconsole/getListOfTemplate - START");

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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long projectId = inputJson.getLong("projectId");

						// Validate ProjectId
						boolean isValidProject = projectDao.isProjectIdValid(projectId);

						if (isValidProject) {
							// fetch template list
							List<String> tamplateList = validationcheckdao.getTemplateListDataSource(projectId);
							List<String> templateNamesList = new ArrayList<>();

							for (String templateInfo : tamplateList) {
								String templateStr = templateInfo.toString().replace("[", "").replace("]", "");
								templateNamesList.add(templateStr);
							}

							status = "success";
							result = new JSONArray(templateNamesList);

						} else
							message = "Invalid ProjectId";

					} else {
						message = "Invalid Request";
						responseStatus = HttpStatus.BAD_REQUEST;
					}

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("message "+message);
		LOG.info("/dbconsole/getListOfTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/changeDataColumnAjax", method = RequestMethod.POST)
	public ResponseEntity<Object> changeDataColumnAjax(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("/dbconsole/changeDataColumnAjax - START");

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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						JSONObject inputJson = new JSONObject(inputJsonStr);
						long idData = inputJson.getLong("idData");

						// Validate template Id
						String templateName = validationcheckdao.getNameFromListDataSources(idData);

						if (templateName != null && !templateName.isEmpty()) {
							// fetch column names
							List<String> listDataDefinitionColumnNames = validationcheckdao
									.getDisplayNamesFromListDataDefinition(idData);
							result = new JSONArray(listDataDefinitionColumnNames);
							status = "success";

						} else
							message = "Invalid Template Id";

					} else {
						message = "Invalid Request";
						responseStatus = HttpStatus.BAD_REQUEST;
					}

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("message "+message);
		LOG.info("/dbconsole/changeDataColumnAjax - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/deleteCustomMicrosegments", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteCustomMicrosegments(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
	
		LOG.info("/dbconsole/deleteCustomMicrosegments - START");

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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {

						JSONObject inputJson = new JSONObject(inputJsonStr);
						long rowId = inputJson.getLong("id");
						boolean isDeleted = customMicroegmentDao.deleteCustomMicrosegments(rowId);

						if (isDeleted) {
							message = "Custom Microsegment Deleted successfully";
							status = "success";
						} else {
							message = "Failed to delete Custom Microsegment";
						}

					} else {
						message = "Invalid request";
						responseStatus = HttpStatus.BAD_REQUEST;
					}

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("message "+message);
		LOG.info("/dbconsole/deleteCustomMicrosegments - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getListOfMicrosegmentTemplate", method = RequestMethod.POST)
	public ResponseEntity<Object> getListOfMicrosegmentTemplate(@RequestHeader HttpHeaders headers, @RequestBody String inputJsonStr) {
		
		LOG.info("/dbconsole/getListOfMicrosegmentTemplate - START");
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
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				LOG.error("Exception  "+e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {
					LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {

						JSONObject inputJson = new JSONObject(inputJsonStr);
						long projectId = inputJson.getLong("projectId");

						// Validate ProjectId
						boolean isValidProject = projectDao.isProjectIdValid(projectId);

						if (isValidProject) {
							// fetch template list
							List<String> tamplateList = validationcheckdao.getMicrosegmentTemplateListDataSource(projectId);
							List<String> templateNamesList = new ArrayList<>();

							for (String templateInfo : tamplateList) {
								String templateStr = templateInfo.toString().replace("[", "").replace("]", "");
								templateNamesList.add(templateStr);
							}

							status = "success";
							result = new JSONArray(templateNamesList);

						} else
							message = "Invalid ProjectId";

					} else {
						message = "Invalid Request";
						responseStatus = HttpStatus.BAD_REQUEST;
					}

				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else
				message = "Token is missing in headers";

		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("message "+message);
		LOG.info("/dbconsole/getListOfMicrosegmentTemplate - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

}
