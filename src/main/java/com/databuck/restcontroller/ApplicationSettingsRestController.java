package com.databuck.restcontroller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.ITaskDAO;
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

import com.databuck.bean.ApplicationSettingsUpdate;
import com.databuck.bean.DatabuckProperties;
import com.databuck.config.DatabuckPropertyLoader;
import com.databuck.controller.ApplicationSettingsController;
import com.databuck.econstants.DatabuckPropertyCategory;
import com.databuck.service.ApplicationSettingsService;
import com.databuck.service.ExecutiveSummaryService;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class ApplicationSettingsRestController {

    @Autowired
    ApplicationSettingsService applicationSettingsService;

    @Autowired
    ExecutiveSummaryService executiveSummaryService;

    @Autowired
    DatabuckPropertyLoader databuckPropertyLoader;

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	ITaskDAO iTaskDAO;

    private static final Logger LOG = Logger.getLogger(ApplicationSettingsRestController.class);

    @RequestMapping(value = "/dbconsole/appset/getPropertiesForPropertyCategory", method = RequestMethod.POST)
    public ResponseEntity<Object> getPropertiesForPropertyCategory(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputJsonStr) {
	LOG.info("dbconsole/appset/getPropertiesForPropertyCategory - START");

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
		LOG.debug("token " + token.toString());
	    } catch (Exception e) {
		LOG.error(e.getMessage());
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

			// Get Property Category
			String propertyCategory = inputJson.getString("propertyCategory");
			LOG.info("\n====> Fetching properties for propertyCategory: " + propertyCategory);

			// Get the list of valid Property Categories
			List<String> propertyCategoriesNames = Stream.of(DatabuckPropertyCategory.values())
				.map(Enum::name).collect(Collectors.toList());

			// Validate Property Category
			if (propertyCategoriesNames.contains(propertyCategory.trim().toLowerCase())) {

			    // Fetch the properties
			    List<DatabuckProperties> propertiesList = databuckPropertyLoader
				    .getPropertiesForCategory(propertyCategory, false);
			    json.put("result", propertiesList);
			    status = "success";
			} else {
			    message = "Invalid PropertyCategory";
			    LOG.error(message);
			}

		    } else {
			message = "Invalid PropertyCategory";
			LOG.error(message);
		    }

		} else {
		    message = "Token expired.";
		    LOG.error(message);
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		}

	    } else {
		message = "Token is missing in headers";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/appset/getPropertiesForPropertyCategory - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/appset/saveUpdatedPropertiesForPropertyCategory", method = RequestMethod.POST)
    public ResponseEntity<Object> saveUpdatedPropertiesForPropertyCategory(@RequestHeader HttpHeaders headers,
	    @RequestBody String inputJsonStr) {
	LOG.info("dbconsole/appset/saveUpdatedPropertiesForPropertyCategory - START");

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
		LOG.debug("token " + token.toString());
	    } catch (Exception e) {
		LOG.error(e.getMessage());
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

			JSONArray modifiedProperties = inputJson.getJSONArray("modifiedProperties");

			boolean isValidPropValue = true;

			if (modifiedProperties != null && modifiedProperties.length() > 0) {
			    ApplicationSettingsUpdate[] modified_props_array = new ApplicationSettingsUpdate[modifiedProperties
				    .length()];

			    for (int i = 0; i < modifiedProperties.length(); i++) {
				JSONObject prop_params_json = (JSONObject) modifiedProperties.get(i);

				String defaultValue = prop_params_json.getString("propertyDefaultvalue");
				String propValue = prop_params_json.getString("propertyValue");
				String propType = prop_params_json.getString("propertyDataType");

				if ((defaultValue.equals("Y") || defaultValue.equals("N"))
					&& !(propValue.equals("Y") || propValue.equals("N"))
					&& propType.equalsIgnoreCase("string")) {
				    isValidPropValue = false;
				    message = "Property value should be Y or N only for the property "
					    + prop_params_json.getString("propertyName");
				    continue;
				}

				ApplicationSettingsUpdate property_details = new ApplicationSettingsUpdate();
				property_details.setPropName(prop_params_json.getString("propertyCategoryName"));
				property_details.setPropKeys(prop_params_json.getString("propertyName"));
				property_details.setPropValues(prop_params_json.getString("propertyValue"));
				property_details.setPropEncrypt(prop_params_json.getBoolean("valueEncrypted"));
				property_details.setPropReqRestart(prop_params_json.getString("propRequiresRestart"));

				modified_props_array[i] = property_details;
					// changes regarding Audit trail
					UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_APPLICATION_SETTINGS, formatter.format(new Date()), prop_params_json.getLong("propertyId"),
							DatabuckConstants.ACTIVITY_TYPE_EDITED,prop_params_json.getString("propertyName"));
			    }
			    if (isValidPropValue) {
				// Save the properties changes
				json = applicationSettingsService.saveUpdatedProperties(modified_props_array);

				// Check if restart required
				String isRestartRequired = json.getString("isRestartRequired");
				if (isRestartRequired != null && isRestartRequired.trim().equalsIgnoreCase("Y")) {
				    ApplicationSettingsController.setIsPropChangesWaitingRestart(true);
				}

				// Read status and message
				status = json.getString("status");
				message = json.getString("message");
			    }
			    LOG.info(message);

			} else {
			    message = "No modified properties found in request";
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
		message = "Token is missing in headers";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	json.put("status", status);
	json.put("message", message);
	LOG.info("dbconsole/appset/saveUpdatedPropertiesForPropertyCategory - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

    @RequestMapping(value = "/dbconsole/appset/isPropChangesWaitingRestart", method = RequestMethod.GET)
    public ResponseEntity<Object> isPropChangesWaitingRestart(@RequestHeader HttpHeaders headers) {
	LOG.info("dbconsole/appset/isPropChangesWaitingRestart - START");

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
		LOG.error(e.getMessage());
		e.printStackTrace();
	    }
	    // Validate token
	    if (token != null && !token.isEmpty()) {

		// Check if token is expired or not
		JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
		String tokenStatus = tokenStatusObj.getString("status");
		if (tokenStatus.equals("success")) {

		    boolean isPropChangesWaitingRestart = ApplicationSettingsController.isPropChangesWaitingRestart();
		    result.put("isPropChangesWaitingRestart", isPropChangesWaitingRestart);
		    status = "success";

		} else {
		    message = "Token expired.";
		    LOG.error(message);
		    responseStatus = HttpStatus.EXPECTATION_FAILED;
		}

	    } else {
		message = "Token is missing in headers";
		LOG.error(message);
	    }
	} catch (Exception e) {
	    message = "Request failed";
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	json.put("status", status);
	json.put("message", message);
	json.put("result", result);
	LOG.info("dbconsole/appset/isPropChangesWaitingRestart - END");
	return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

}