package com.databuck.restcontroller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.ITaskDAO;
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

import com.databuck.service.DomainManagementService;
import com.databuck.util.DateUtility;
import com.databuck.util.TokenValidator;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class DomainManagementRestController {

	@Autowired
	private DomainManagementService domainService;

	@Autowired
	private TokenValidator tokenValidator;
	
	private static final Logger LOG = Logger.getLogger(DomainManagementRestController.class);

	@Autowired
	private IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	ITaskDAO iTaskDAO;

	@RequestMapping(value = "/dbconsole/loadDomainRecordList", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> loadDomainRecordList(@RequestHeader HttpHeaders headers) {
		LOG.info("/dbconsole/loadDomainRecordList - START");
		Map<String, Object> response = new HashMap<>();
		JSONObject oJsonResponse = new JSONObject();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/loadDomainRecordList - END");
			//return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			LOG.info("/dbconsole/loadDomainRecordList - END");
			//return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		try {
			DateUtility.DebugLog("loadDomainList 01", "Begin controller processing for domain list");
			oJsonResponse = domainService.getDomainPageData("AngularAPI");
			DateUtility.DebugLog("loadDomainList 02", "Got data sending to client");
			response.put("result", oJsonResponse.toMap());
			response.put("status", "Success");
			response.put("message", "Got running job list Successfully");
			LOG.info("Got running job list Successfully");
			LOG.info("/dbconsole/loadDomainRecordList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "Failed");
			response.put("message", "Failed to Get running job list.");
			LOG.error( "Failed to Get running job list.");
			LOG.info("/dbconsole/loadDomainRecordList - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

//	@RequestMapping(value = "/dbconsole/loadDomainRecordCSV", method = RequestMethod.POST, produces = "application/json")
//	public void loadDomainRecordCSV(@RequestHeader HttpHeaders headers, HttpServletResponse httpResponse) {
//		Map<String, Object> response = new HashMap<>();
//		JSONObject oJsonResponse = new JSONObject();
//		response.put("status", "failed");
//		try {
//			String token = null;
//			token = headers.get("token").get(0);
//			if (token == null || token.isEmpty()) {
//				throw new Exception("Token is missing in headers.");
//			}
//			if (!tokenValidator.isValid(token)) {
//				throw new Exception("Token is expired.");
//			}
//			httpResponse.setContentType("text/csv");
//			String csvFileName = "RunningJob" + LocalDateTime.now() + ".csv";
//			String headerKey = "Content-Disposition";
//			String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
//			httpResponse.setHeader(headerKey, headerValue);
//			ICsvBeanWriter csvWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
//			DateUtility.DebugLog("loadDomainList 01", "Begin controller processing for domain list");
//			oJsonResponse = domainService.getDomainPageData("AngularAPI");
//			DateUtility.DebugLog("loadDomainList 02", "Got data sending to client");
//			response.put("result", oJsonResponse.toMap());
//			response.put("status", "Success");
//			response.put("message", "Got running job list Successfully");
//			csvWriter.writeHeader(getHeaders(tabName));
//			String[] fields = getFields(tabName);
//			for (Object job : jobs) {
//				csvWriter.write(job, fields);
//			}
//			csvWriter.close();
//		} catch (
//
//		Exception e) {
//			e.printStackTrace();
//			e.printStackTrace();
//			try {
//				httpResponse.sendError(0, e.getMessage());
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}

	@RequestMapping(value = "/dbconsole/mainDomainHandler", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> mainDomainHandler(HttpSession oSession, @RequestBody String DomainData,
			@RequestHeader HttpHeaders headers) throws IOException {
		LOG.info("/dbconsole/mainDomainHandler - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			LOG.info("/dbconsole/mainDomainHandler - END");
			//return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired");
			LOG.info("/dbconsole/mainDomainHandler - END");
			//return new ResponseEntity<Object>(response, HttpStatus.EXPECTATION_FAILED);
		}
		JSONObject oDomainData = new JSONObject(DomainData);
		JSONObject oJsonResponse = new JSONObject();
		String sContext;
		boolean lNewDomain = false;
		LOG.debug("Getting request parameters" + oDomainData.toString());
		try {
			String domainName = oDomainData.getJSONObject("Data").getString("DomainName");
			LOG.info("***Domain Names =" + domainName);
			if (domainName == null || domainName.isEmpty() || domainName == " ") {
				throw new Exception("Please enter Domain AccessKey");
			}

			String projectIds = oDomainData.getJSONObject("Data").getString("ProjectIds");
			LOG.info("***ProjectIds=" + projectIds);
			if (projectIds == null || projectIds.isEmpty() || projectIds == " ") {
				throw new Exception("Please select Domain Projects");
			}
			DateUtility.DebugLog("Domain handler data 01", String.format("Begin controller Context = %1$s, Data = %2$s",
					oDomainData.getString("Context"), oDomainData.getJSONObject("Data")));
			sContext = oDomainData.getString("Context");
			// changes regarding Audit trail
			UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(headers.get("token").get(0));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			switch (sContext) {
			case "DataSave":
				lNewDomain = (Integer.parseInt(oDomainData.getJSONObject("Data").getString("DomainId")) < 0) ? true
						: false;
				if (lNewDomain) {
					oJsonResponse = domainService.addNewDomain(oDomainData.getJSONObject("Data"));
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_DOMAIN, formatter.format(new Date()), (long) oJsonResponse.getInt("newDomainId"),
							DatabuckConstants.ACTIVITY_TYPE_CREATED,domainName);
				} else {
					oJsonResponse = domainService.updateExistingDomain(oDomainData.getJSONObject("Data"));
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_DOMAIN, formatter.format(new Date()), (long)Integer.parseInt(oDomainData.getJSONObject("Data").getString("DomainId")),
							DatabuckConstants.ACTIVITY_TYPE_EDITED,domainName );
				}
				break;
			case "DataDelete":
				Long domainId = Long.valueOf(oDomainData.getJSONObject("Data").getString("DomainId"));
				oJsonResponse = domainService.deleteSelectedDomain(oDomainData.getJSONObject("Data"));
				iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
						DatabuckConstants.DBK_FEATURE_DOMAIN, formatter.format(new Date()),  domainId,
						DatabuckConstants.ACTIVITY_TYPE_DELETED,domainName );
				break;
			default:
			}

			DateUtility.DebugLog("Domain handler data 02", "End controller");
			response.put("status", oJsonResponse.get("Status"));
			response.put("message", oJsonResponse.get("Msg"));
			LOG.info("/dbconsole/mainDomainHandler - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "Failed");
			response.put("message", "Failed to get running job list.");
			LOG.error("Failed to get running job list.");
			LOG.info("/dbconsole/mainDomainHandler - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

}
