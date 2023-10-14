package com.databuck.restcontroller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.ITaskDAO;
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
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.databuck.bean.DatabuckTags;
import com.databuck.bean.ListApplications;
import com.databuck.dao.DatabuckTagsDao;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.DatabuckTagsService;
import com.databuck.service.ExecutiveSummaryService;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckTagsRestController {

    @Autowired
    private DatabuckTagsDao databuckTagsDao;

    @Autowired
    private ExecutiveSummaryService executiveSummaryService;

    @Autowired
    private IValidationCheckDAO validationCheckDAO;

    @Autowired
    private DatabuckTagsService databuckTagsService;
    @Autowired
    private IDashboardConsoleDao dashboardConsoleDao;
    @Autowired
    ITaskDAO iTaskDAO;

    private static final Logger LOG = Logger.getLogger(DatabuckTagsRestController.class);   
    
    @RequestMapping(value = "/dbconsole/getAllDatabuckTags", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllDatabuckTags(@RequestHeader HttpHeaders headers) {        
        LOG.info("dbconsole/getAllDatabuckTags - START");

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
                responseStatus= HttpStatus.EXPECTATION_FAILED;
            }
            // Validate token
            if (token != null && !token.isEmpty()) {

                // Check if token is expired or not
                JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
                String tokenStatus = tokenStatusObj.getString("status");

                if (tokenStatus.equals("success")) {
                    // get all databuck tags list
                    JSONArray databuckTagsList = databuckTagsDao.getAllDatabuckTags();

                    // validate databuckTagsList
                    if (databuckTagsList != null && databuckTagsList.length() > 0) {
                        status = "success";
                        message = "Databuck Tag List Fetched successfully";
                        result.put("databuckTagList", databuckTagsList);
                    } else {
                        message = "Failed to read Databuck Tag List";
                    }
                } else {
                    message = "Token expired.";
                    responseStatus = HttpStatus.EXPECTATION_FAILED;
                }
            } else {
                message = "Token is missing in header";
                responseStatus= HttpStatus.EXPECTATION_FAILED;
            }
            LOG.info("Message "+message);
        } catch (Exception e) {
            message = "Request failed";
            LOG.error(message+" \n Exception  "+e.getMessage());
            e.printStackTrace();
        }
        json.put("status", status);
        json.put("message", message);
        json.put("result", result);
        LOG.info("/dbconsole/getAllDatabuckTags - END");
        return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

	@RequestMapping(value = "/dbconsole/addDatabuckTags", method = RequestMethod.POST)
	public ResponseEntity<Object> addDatabuckTags(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		
		LOG.info("dbconsole/addDatabuckTags - START");

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "Failed to add DatabuckTag";
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
				responseStatus = HttpStatus.EXPECTATION_FAILED;
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
						String tagName = inputJson.getString("tagName");
						String description = inputJson.getString("description");
                        //get updated status
						JSONObject updateStatus = databuckTagsService.addDatabuckTags(tagName, description);
                        // changes regarding Audit trail
                        UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
                                DatabuckConstants.DBK_FEATURE_DATABUCKTAGS, formatter.format(new Date()), updateStatus.getLong("tagId"),
                                DatabuckConstants.ACTIVITY_TYPE_CREATED,tagName);
						status = updateStatus.getString("status");
						message = updateStatus.getString("message");
					} else {
						message = "Invalid request";
					}
				} else {
                    message = "Token expired.";
                    responseStatus = HttpStatus.EXPECTATION_FAILED;
                }
			} else {
                message = "Token is missing in header";
                responseStatus = HttpStatus.EXPECTATION_FAILED;
            }
			
			LOG.info("message "+message);
			
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message+ "\n Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/addDatabuckTags - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

    @RequestMapping(value = "/dbconsole/updateDatabuckTagInfo", method = RequestMethod.POST)
    public ResponseEntity<Object> updateDatabuckTagInfo(@RequestHeader HttpHeaders headers,
                                                                             @RequestBody String inputJsonStr) {
       
        LOG.info("/dbconsole/updateDatabuckTagInfo - START");

        JSONObject json = new JSONObject();
        String status = "failed";
        String message = "Failed to update Databuck tag";
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
                responseStatus = HttpStatus.EXPECTATION_FAILED;
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
                        Integer tagId = inputJson.getInt("tagId");
                        String tagName = inputJson.getString("tagName");
                        String description = inputJson.getString("description");

                        int updateStatus = databuckTagsDao.updateDatabuckTagInfo(tagId, tagName, description);
                        // changes regarding Audit trail
                        UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
                                DatabuckConstants.DBK_FEATURE_DATABUCKTAGS, formatter.format(new Date()), Long.valueOf(tagId),
                                DatabuckConstants.ACTIVITY_TYPE_EDITED,tagName);

                        if (updateStatus != 0) {
                            status = "success";
                            message = "Databuck tag updated successfully'";
                        }

                    } else {
                        message = "Invalid request";
                    }
                } else {
                    message = "Token expired.";
                    responseStatus = HttpStatus.EXPECTATION_FAILED;
                }
            } else {
                message = "Token is missing in header";
                responseStatus = HttpStatus.EXPECTATION_FAILED;
            }
            
            LOG.info("message "+message);
            
        } catch (Exception e) {
            message = "Request failed";
            LOG.error("Exception  "+e.getMessage());
            e.printStackTrace();
        }
        json.put("status", status);
        json.put("message", message);
        LOG.info("/dbconsole/updateDatabuckTagInfo - END");
        return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

	@RequestMapping(value = "/dbconsole/databuckTagsCSV", method = RequestMethod.GET)
	public void databuckTagsCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
		
		LOG.info("dbconsole/databuckTagsCSV - START");

		String token = "";

		try {
			// Get token from request header
			try {
				LOG.debug("token   " + headers.containsKey("token") + "   " + headers.get("token"));
				token = headers.get("token").get(0);
				
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Exception  "+e.getMessage());
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					List<DatabuckTags> databuckTagsList = databuckTagsDao.getDatabuckTags();

					httpResponse.setContentType("text/csv");
					String csvFileName = "DatabuckTag" + LocalDateTime.now() + ".csv";
					String headerKey = "Content-Disposition";
					String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
					httpResponse.setHeader(headerKey, headerValue);
					ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(),
							CsvPreference.STANDARD_PREFERENCE);
					String[] fields = { "tagId", "tagName", "description" };
					String[] header = { "Tag Id", "Tag Name", "Tag Description" };
					csvWriter.writeHeader(header);

					for (DatabuckTags databuckTags : databuckTagsList) {
						csvWriter.write(databuckTags, fields);
					}
					csvWriter.close();

				} else
					LOG.error("Token Expired.");
					throw new Exception("Token Expired.");
			} else
				LOG.error("Token is missing in header");
				throw new Exception("Token is missing in header");
		} catch (Exception e) {
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("/dbconsole/databuckTagsCSV - END");
	}
	
	@RequestMapping(value = "/dbconsole/linkDatabuckTagsToRules", method = RequestMethod.POST)
	public ResponseEntity<Object> linkDatabuckTagsToRules(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
	
		LOG.info("dbconsole/linkDatabuckTagsToRules - START");

		JSONObject json = new JSONObject();
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

						Integer globalRuleId = inputJson.getInt("ruleReferenceId");
						JSONArray tagIdList = inputJson.getJSONArray("tagIdList");
						int idApp = inputJson.getInt("idApp");

						JSONObject resJson = databuckTagsService.linkDatabuckTagToRule(globalRuleId, tagIdList, idApp);
						if (resJson != null && resJson.length() > 0) {
							status = resJson.getString("status");
							message = resJson.getString("message");
						} else
							message = "Failed to link global rule to databuck tags";
					} else
						message = "Invalid request";
				} else {
					message = "Token expired.";
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in header";
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			 LOG.info("message "+message);
			
		} catch (Exception e) {
			message = "Request failed";
			LOG.error("Exception  "+e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/linkDatabuckTagsToRules - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

    @RequestMapping(value = "/dbconsole/getLinkedTagsForRule", method = RequestMethod.POST)
    public ResponseEntity<Object> getLinkedTagsForRule(@RequestHeader HttpHeaders headers,
                                                       @RequestBody String inputJsonStr) {
    	LOG.info("dbconsole/getLinkedTagsForRule - START");

        JSONObject json = new JSONObject();
        JSONArray linkedTags = new JSONArray();
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
                        Long idApp = inputJson.getLong("idApp");
                        int ruleId = inputJson.getInt("ruleId");

                        // Validate idApp
                        ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(idApp);
                        if (listApplications != null) {
                            //get linked tags
                            linkedTags = databuckTagsDao.getAllLinkedTags(idApp, ruleId);
                            if (linkedTags != null && linkedTags.length() > 0) {
                                message = "Tags Fetch Successfully";
                                status = "success";
                            } else
                                message = "No Tags Found";
                            status = "success";
                        } else
                            message = "Invalid ValidationId";
                    } else
                        message = "Invalid request";
                } else {
                    message = "Token expired.";
                    responseStatus = HttpStatus.EXPECTATION_FAILED;
                }
            } else {
                message = "Token is missing in header";
                responseStatus = HttpStatus.EXPECTATION_FAILED;
            }
            
            LOG.info("message "+message);
        } catch (Exception e) {
            message = "Request failed";
            LOG.error("Exception  "+e.getMessage());
            e.printStackTrace();
        }
        json.put("result", linkedTags);
        json.put("status", status);
        json.put("message", message);
        LOG.error("dbconsole/getLinkedTagsForRule - END");
        return new ResponseEntity<Object>(json.toString(), responseStatus);
    }

}
