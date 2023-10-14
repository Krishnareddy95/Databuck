package com.databuck.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.bean.TemplateDeltaResponse;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.DataTemplateDeltaCheckService;
import com.databuck.service.IValidationService;
import com.databuck.util.TokenValidator;

@CrossOrigin(origins = "*")
@RestController
public class SelectKyesController {

	@Autowired
	public SchemaDAOI SchemaDAOI;

	@Autowired
	public ITemplateViewDAO templateviewdao;

	@Autowired
	public IListDataSourceDAO listdatasourcedao;

	@Autowired
	public IValidationService validationService;

	@Autowired
	private DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	@Autowired
	private TokenValidator tokenValidator;
	
	private static final Logger LOG = Logger.getLogger(SelectKyesController.class);

	@PostMapping(value = "/dbconsole/primaryKeyyes")
	public ResponseEntity<Object> primaryKeyyes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/primaryKeyyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName primaryKeyyes ==========================" + templateName);
			int primaryKeyyes = templateviewdao.primaryKeyyes(idData);
			LOG.debug("primaryKeyyes" + primaryKeyyes);
			LOG.debug("templateName primaryKeyyes ==========================" + templateName);
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			Map<String, Object> result = new HashMap<>();
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("status", "success");
			response.put("result", result);
			response.put("message", "Successfully update primary key values.");
			LOG.info("Successfully update primary key values.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/primaryKeyyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/dupkeyyes")
	public ResponseEntity<Object> dupkeyyes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/dupkeyyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			int updateDupKeyYes = templateviewdao.updateDupKeyYes(idData);
			LOG.debug("updateDupKeyYes=" + updateDupKeyYes);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName dupkeyyes ==========================" + templateName);
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			Map<String, Object> result = new HashMap<>();
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("status", "success");
			response.put("result", result);
			response.put("message", "Successfully update duplicate key values.");
			LOG.info("Successfully update duplicate key values.");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/dupkeyyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@PostMapping(value = "/dbconsole/microSegmentyes")
	public ResponseEntity<Object> changeAllMicrosegmentToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/microSegmentyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName microsegment ==========================" + templateName);

			boolean status = validationService.changeAllMicrosegmentToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/microSegmentyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/lastReadTimeyes")
	public ResponseEntity<Object> changeAlllastReadTimeToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/lastReadTimeyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName last read time ==========================" + templateName);
			boolean status = validationService.changeAlllastReadTimeToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/lastReadTimeyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}

	}

	@PostMapping(value = "/dbconsole/doNotDisplayyes")
	public ResponseEntity<Object> changeAllIsMaskedToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/doNotDisplayyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName do not display ==========================" + templateName);

			boolean status = validationService.changeAllIsMaskedToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/doNotDisplayyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/partitionByyes")
	public ResponseEntity<Object> changeAllPartitionByToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/partitionByyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName partition by ==========================" + templateName);

			boolean status = validationService.changeAllPartitionByToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/partitionByyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/dataDriftyes")
	public ResponseEntity<Object> changeAllDataDriftToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/dataDriftyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName DataDrift ==========================" + templateName);

			boolean status = validationService.changeAllDataDriftToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/dataDriftyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/startDateyes")
	public ResponseEntity<Object> changeAllStartDateToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/startDateyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName start date ==========================" + templateName);

			boolean status = validationService.changeAllStartDateToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/startDateyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/endDateyes")
	public ResponseEntity<Object> changeAllEndDateToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/endDateyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName end date ==========================" + templateName);

			boolean status = validationService.changeAllEndDateToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/endDateyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/timelinessKeyyes")
	public ResponseEntity<Object> changeAllTimelinessKeyToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/timelinessKeyyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName timeliness key ==========================" + templateName);

			boolean status = validationService.changeAllTimelinessKeyToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/timelinessKeyyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/recordAnomalyyes")
	public ResponseEntity<Object> changeAllRecordAnomalyToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/recordAnomalyyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName record anomaly ==========================" + templateName);

			boolean status = validationService.changeAllRecordAnomalyToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/recordAnomalyyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/defaultCheckyes")
	public ResponseEntity<Object> changeAllDefaultCheckToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/defaultCheckyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName default check ==========================" + templateName);

			boolean status = validationService.changeAllDefaultCheckToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/defaultCheckyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/dateRuleyes")
	public ResponseEntity<Object> changeAllDateRuleToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/dateRuleyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName date rule check ==========================" + templateName);

			boolean status = validationService.changeAllDateRuleToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/dateRuleyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/patternCheckyes")
	public ResponseEntity<Object> changeAllPatternCheckToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/patternCheckyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName pattern check ==========================" + templateName);

			boolean status = validationService.changeAllPatternCheckToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/patternCheckyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/defaultPatternCheckyes")
	public ResponseEntity<Object> changeAllDefaultPatternCheckToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/defaultPatternCheckyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName default pattern check ==========================" + templateName);

			boolean status = validationService.changeAllDefaultPatternCheckToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);
			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/defaultPatternCheckyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/badDatayes")
	public ResponseEntity<Object> changeAllBadDataToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/badDatayes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName bad data check ==========================" + templateName);

			boolean status = validationService.changeAllBadDataToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/badDatayes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/lengthCheckyes")
	public ResponseEntity<Object> changeAllLengthCheckToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/lengthCheckyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName length check ==========================" + templateName);

			boolean status = validationService.changeAllLengthCheckToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating microsegment");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/lengthCheckyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/maxLengthCheckyes")
	public ResponseEntity<Object> changeAllMaxLengthCheckToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/maxLengthCheckyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName Max length check ==========================" + templateName);

			boolean status = validationService.changeAllMaxLengthCheckToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating max length check");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/maxLengthCheckyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/measurementyes")
	public ResponseEntity<Object> changeAllMatchValuetToYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/measurementyes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Map<String, Object> result = new HashMap<>();
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			LOG.debug("templateName Match value ==========================" + templateName);

			boolean status = validationService.changeAllMatchValuetToYes(idData);
			if (status) {
				response.put("status", "success");
				response.put("message", "Updated sucessfully");
			} else {
				response.put("message", "Problem while updating All Match values");
			}
			// Delta changes of listDatadefinition
			TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

			result.put("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
			result.put("templateDeltaResponse", templateDeltaResponse);
			result.put("idData", idData);
			result.put("name", templateName);
			response.put("result", result);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/measurementyes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/distributionCheckyYes")
	public ResponseEntity<Object> numericalStatyes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, String> request) {
		LOG.info("dbconsole/distributionCheckyYes - START");
		Map<String, Object> response = new HashMap<String, Object>();
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			
			LOG.error("Token is expired.");
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!request.containsKey("idData") || !request.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			Long idData = Long.parseLong(request.get("idData"));
			LOG.debug("idData=" + idData);
			String templateName = request.get("name");
			int nullcountyes = templateviewdao.numericalStatyes(idData);
			LOG.debug("numericalStatyes" + nullcountyes);
			response.put("idData", idData);
			response.put("name", templateName);
			response.put("status", "success");
			response.put("message", "Updated sucessfully");
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", ex.getMessage());
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/distributionCheckyYes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	@PostMapping(value = "/dbconsole/columnUpdateYes")
	public ResponseEntity<Object> columnUpdateYes(@RequestHeader HttpHeaders headers,
			@RequestBody Map<String, Object> params) {
		LOG.info("dbconsole/columnUpdateYes - START");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "failed");
		String token = null;
		try {
			token = headers.get("token").get(0);
			LOG.debug("token "+token.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (token == null || token.isEmpty()) {
			response.put("message", "Token is missing in headers.");
			LOG.error("Token is missing in headers.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!tokenValidator.isValid(token)) {
			response.put("message", "Token is expired.");
			LOG.error("Token is expired.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		if (!params.containsKey("idData") || !params.containsKey("name")) {
			response.put("message", "idData or name is missing in parameters.");
			LOG.error("idData or name is missing in parameters.");
			
			return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
		}
		try {
			long idData = Long.parseLong(String.valueOf(params.get("idData")));
			LOG.debug("idData=" + idData);
			String templateName = String.valueOf(params.get("name"));
			String checkName = String.valueOf(params.get("checkName"));
			LOG.debug("templateName Match value ==========================" + templateName);
			boolean status = getUpdateStatus(idData, checkName);
			if (status) {
				response.put("status", "success");
				response.put("message", checkName + "column updated sucessfully");
			} else {
				response.put("message", "Problem while updating " + checkName);
			}
			response.put("idData", idData);
			response.put("name", templateName);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			response.put("message", "Failed to update details.");
			LOG.error(ex.getMessage());
			LOG.info("dbconsole/columnUpdateYes - END");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}

	private boolean getUpdateStatus(long idData, String checkName) {
		switch (checkName) {
		case "MaxLengthCheck":
			return validationService.changeAllMaxLengthCheckToYes(idData);
		case "MatchValue":
			return validationService.changeAllMatchValuetToYes(idData);
		case "Microsegment":
			return validationService.changeAllMicrosegmentToYes(idData);
		case "LastReadTime":
			return validationService.changeAlllastReadTimeToYes(idData);
		case "IsMasked":
			return validationService.changeAllIsMaskedToYes(idData);
		case "PartitionBy":
			return validationService.changeAllPartitionByToYes(idData);
		case "DataDrift":
			return validationService.changeAllDataDriftToYes(idData);
		case "StartDate":
			return validationService.changeAllStartDateToYes(idData);
		case "EndDate":
			return validationService.changeAllEndDateToYes(idData);
		case "TimelinessKey":
			return validationService.changeAllTimelinessKeyToYes(idData);
		case "RecordAnomaly":
			return validationService.changeAllRecordAnomalyToYes(idData);
		case "PatternCheck":
			return validationService.changeAllPatternCheckToYes(idData);
		case "DateRule":
			return validationService.changeAllDateRuleToYes(idData);
		case "DefaultCheck":
			return validationService.changeAllDefaultCheckToYes(idData);
		case "LengthCheck":
			return validationService.changeAllLengthCheckToYes(idData);
		case "AllMatchValuet":
			return validationService.changeAllMatchValuetToYes(idData);
		case "BadData":
			return validationService.changeAllBadDataToYes(idData);
		case "NullCheck":
			return validationService.changeAllNonNullsToYes(idData);
		case "DefaultPatternCheck":
			return validationService.changeAllDefaultPatternCheckToYes(idData);
		}
		return false;
	}

}
