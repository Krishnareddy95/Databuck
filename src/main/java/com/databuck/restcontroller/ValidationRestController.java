package com.databuck.restcontroller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDmCriteria;
import com.databuck.bean.Project;
import com.databuck.bean.UserToken;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.service.IProjectService;
import com.databuck.service.LoginService;
import com.databuck.service.PrimaryKeyMatchingResultService;
import com.databuck.util.JwfSpaInfra;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class ValidationRestController {

	@Autowired
	PrimaryKeyMatchingResultService primaryKeyMatchingResultService;
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private IProjectService IProjectservice;
	
	@Autowired
	IValidationCheckDAO validationcheckdao;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;
	
	@Autowired
	private IProjectService projService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private ITemplateViewDAO templateviewdao;
	
	private static final Logger LOG = Logger.getLogger(ValidationRestController.class);

	@RequestMapping(value = "/dbconsole/validationCheckName", method = RequestMethod.POST)
	public ResponseEntity<Object> validationCheckName(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Object> request ) {
		LOG.info("dbconsole/validationCheckName - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				//LOG.info("dbconsole/validationCheckName - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			
			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {

				String sApplicationListQry = "";
				List<Map<String, Object>> aListAppIdsAndNames = new ArrayList<Map<String, Object>>();
				String sRunnableStatusRowIds = "";
				
				Boolean lIsRuleCatalogUsed = JwfSpaInfra
						.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N")
						.equalsIgnoreCase("Y") ? true : false;

				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
				long domainId = Long.parseLong(request.get("domainId").toString());
				long projectId = Long.parseLong(request.get("projectId").toString());
				// Get ProjectList 
				List<Project> projList = null;
				String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
				
				if (activeDirectoryFlag != null && activeDirectoryFlag.equalsIgnoreCase("Y")) {
					
					String[] ldapGroups = userToken.getUserLDAPGroups().split(",");
					ArrayList<String> userLdapGroups = new ArrayList<String>(Arrays.asList(ldapGroups));
					
					projList = loginService.getProjectListOfUser(userLdapGroups);
				} else {
					projList = projService.getAllProjectsOfAUser(userToken.getEmail());
				}
				
				String sListProjectIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
				sRunnableStatusRowIds = validationcheckdao.getRunnableStatusRowIds();

				if (lIsRuleCatalogUsed) {
					sApplicationListQry = "select name, idapp from listApplications where active ='yes' "
							+ "and project_id in (" + projectId + ") and ((approve_status in ("
							+ sRunnableStatusRowIds + ")) or (appType !='Data Forensics')) and domain_id = ("+ domainId +") order by idapp desc;";
				} else {
					sApplicationListQry = String.format(
							"select name, idApp from listApplications where active ='yes' and project_id in (%1$s) and domain_id in(%2$s) order by idapp desc",
							projectId, domainId);
				}

				List<ListApplications> aListApplications = jdbcTemplate.query(sApplicationListQry,
						new RowMapper<ListApplications>() {
							public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
								ListApplications oListApplication = new ListApplications();

								oListApplication.setName(rs.getString("name"));
								oListApplication.setIdApp(rs.getLong("idApp"));
								return oListApplication;
							}
						});

				for (ListApplications oListApplication : aListApplications) {
					String[] aNameParts = oListApplication.getName().split("_");
					String sIdApp = "";
					String sIdAppToString = "";
					Map<String, Object> idNNameMap = new HashMap<String, Object>();

					/*
					 * If application name contains leading idApp in name itself then ignore
					 * appending again else append it
					 */
					if (aNameParts.length > 1) {
						sIdAppToString = String.format("%1$s", oListApplication.getIdApp());
						sIdApp = aNameParts[0];
						sIdApp = (sIdAppToString.equalsIgnoreCase(sIdApp)) ? ""
								: String.format("%1$s_", oListApplication.getIdApp());
					} else {
						sIdApp = String.format("%1$s_", oListApplication.getIdApp());
					}
					idNNameMap.put("appId", oListApplication.getIdApp());
					idNNameMap.put("validationName", oListApplication.getName());
					aListAppIdsAndNames.add(idNNameMap);
				}

				response.put("result", aListAppIdsAndNames);
				response.put("message", "Validation name fetched successfully.");
				LOG.info("Validation name fetched successfully.");
				response.put("status", "success");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				response.put("status", "failed");
				LOG.error("Token failed...");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("message", "Failed to fetch validation names.");
			response.put("status", "failed");
			LOG.error("Failed to fetch validation names.");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/validationCheckName - END");
		return new ResponseEntity<Object>(response, status);
	}
	
	@RequestMapping(value = "/dbconsole/validationDomain", method = RequestMethod.GET)
	public ResponseEntity<Object> validationDomain(@RequestHeader HttpHeaders headers) {
		LOG.info("dbconsole/validationDomain - START");
		Map<String, Object> response = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				//LOG.info("dbconsole/validationDomain - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
			
			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				
				
				response.put("result", "");
				response.put("message", "Validation name fetched successfully.");
				response.put("status", "success");
				LOG.info("Validation name fetched successfully.");
				status = HttpStatus.OK;
			} else {
				response.put("message", "Token failed...");
				LOG.error("Token failed...");
				response.put("status", "failed");
				status = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			response.put("message", "Failed to fetch validation names.");
			response.put("status", "failed");
			LOG.error("Failed to fetch validation names.");
			//LOG.info("dbconsole/validationDomain - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		LOG.info("dbconsole/validationDomain - END");
		return new ResponseEntity<Object>(response, status);
	}
	
	
	@RequestMapping(value = "/dbconsole/deleteMatchingRule", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteIdListColRulesData(@RequestHeader HttpHeaders headers,
			@RequestBody  Map<String, Long> request) {
		LOG.info("dbconsole/deleteMatchingRule - START");
		
		Map<String, Object> response = new HashMap<>();
		try {
			if (!headers.containsKey("token") || headers.get("token").isEmpty()) {
				response.put("message", "Please provide token.");
				response.put("status", "failed");
				LOG.error("Please provide token.");
				//LOG.info("dbconsole/deleteMatchingRule - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		
			if(!request.containsKey("idApp")) {
				response.put("message", "Required parameters are missing.");
				response.put("status", "failed");
				LOG.error("Required parameters are missing.");
				//LOG.info("dbconsole/deleteMatchingRule - END");
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			if (primaryKeyMatchingResultService.validateToken(headers.get("token").get(0))) {
				int result=0;
				
				long idDMCriteria = request.get("idApp");
				
				// Get details of DM criteria
				ListDmCriteria listDMCriteria = validationcheckdao.getlistDMCriteriaDetailsByID(idDMCriteria);
				
				String leftColumn = "";
				String rightColumn = "";
				Long leftTemplateId = null;
				Integer rightTemplateId = null;
				Long idDM = null;
				Long validationId = null;
				String matchType = "";
				
				// For Measurement Match get the left or right columns
				if(listDMCriteria != null) {
					idDM = listDMCriteria.getIdDm();
					rightColumn = listDMCriteria.getRightSideExp();
					leftColumn = listDMCriteria.getLeftSideExp();
				}
				 LOG.info("idDM: "+idDM);
				 LOG.info("leftColumn: "+leftColumn);
				 LOG.info("rightColumn: "+rightColumn);
				 
				// Get DM rule details
				 Map<String,Object> dmDetails = validationcheckdao.getListDMRulesByIdDM(idDM);
				 if(dmDetails != null && dmDetails.size()>0) {
					 if(dmDetails.get("idApp") != null) {
						 validationId = Long.parseLong(dmDetails.get("idApp").toString());
					 }
					
					 if(dmDetails.get("matchType2") != null) {
						 matchType = dmDetails.get("matchType2").toString();
					 }
				 }
				 LOG.info("validationId: "+validationId);
				 LOG.info("matchType: "+matchType);
				 
				// Get the validation details and template details
				 if(validationId != null) {
					 ListApplications listApplications= validationcheckdao.getdatafromlistapplications(validationId);
					 leftTemplateId = listApplications.getIdData();
					 rightTemplateId = listApplications.getIdRightData();
				 }
				 
				 LOG.info("leftTemplateId: "+leftTemplateId);
				 LOG.info("rightTemplateId: "+rightTemplateId);
				 
				// Deleting DMRules 
				result=validationcheckdao.deleteEntryFromListDMRulesWithIdDm1(request.get("idApp"));
								
				if(result>0) {
					
					// For Measurement Match Disable and Approve Measurement fields in Template Metadata
					if (matchType != null && matchType.trim().equalsIgnoreCase("Measurements Match") && leftTemplateId != null
							&& rightTemplateId != null && rightColumn != null && leftColumn != null) {
						
						// Disable in Left Template
						templateviewdao.updateCheckValueIntoListDatadefinition(leftTemplateId, "measurement", leftColumn.trim(), "N");
						templateviewdao.updateCheckValueIntoStagingListDatadefinition(leftTemplateId, "measurement", leftColumn.trim(), "N");
						
						// Disable in right Template
						templateviewdao.updateCheckValueIntoListDatadefinition(rightTemplateId, "measurement", rightColumn.trim(), "N");
						templateviewdao.updateCheckValueIntoStagingListDatadefinition(rightTemplateId, "measurement", rightColumn.trim(), "N");

					}
					
					response.put("message", "Successfully deleted !!");
					LOG.info("Successfully deleted !!");
					response.put("status", "success");
					//LOG.info("dbconsole/deleteMatchingRule - END");
					return new ResponseEntity<Object>(response, HttpStatus.OK);
				}else {
					response.put("status", "failed");
					response.put("message", "Error occurred while deleting record");
					LOG.error("Error occurred while deleting record");
					//LOG.info("dbconsole/deleteMatchingRule - END");
					return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				response.put("message", "Token expired.");
				LOG.error("Token expired.");
				response.put("status", "failed");
				//LOG.info("dbconsole/deleteMatchingRule - END");
				return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			response.put("status", "failed");
			response.put("message", "There was a problem");
			LOG.error("There was a problem");
			LOG.info("dbconsole/deleteMatchingRule - END");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
}
