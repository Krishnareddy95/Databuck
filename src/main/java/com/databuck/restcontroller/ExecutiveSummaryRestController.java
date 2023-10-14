package com.databuck.restcontroller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.dao.IProjectDAO;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.util.DateUtility;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class ExecutiveSummaryRestController {

	@Autowired
	private ExecutiveSummaryService executiveSummaryService;

	@Autowired
	public IProjectDAO projectDAO;
	
	private static final Logger LOG = Logger.getLogger(ExecutiveSummaryRestController.class);

	/*
	 * The method 'getDqResultsForExecutiveSummary' accept Selected Domain
	 * Projects,Selected Date Range, Selected Download Format,Png Random Token and
	 * returns DqResultData to plot graphs on UI.
	 */
	@RequestMapping(value = "/dbconsole/getDqResultsForExecutiveSummary", method = RequestMethod.POST)
	public ResponseEntity<String> getDqResultsForExecutiveSummary(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getDqResultsForExecutiveSummary - START");

		DateUtility.DebugLog("getDqResultsForExecutiveSummary 01",
		String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));
		LOG.debug("Getting request parameters  " + inputsJsonStr);

		JSONObject inputJson = new JSONObject(inputsJsonStr);
		
		String msg = "Results data successfully build";

		String status = "failed";
		JSONObject dqResultData = new JSONObject();
		JSONObject apiResponse = new JSONObject();

		int domainId = 0;
		int projectId = 0;
		int selectedDataRange = 0;
		String token = "";
		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null || !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {

					try {
						domainId = inputJson.getInt("domainId");
						projectId = inputJson.getInt("projectId");
						String fromDate = inputJson.getString("fromDate");
						String toDate = inputJson.getString("toDate");
						// selectedDataRange = inputJson.getInt("selectedDateRange");

						// check if domain-project combination is valid or not
						if (projectDAO.isDomainProjectValid(domainId, projectId)) {

							// dqResultData =
							// executiveSummaryService.getDqResultData(inputJson,domainId,projectId,selectedDataRange);
							dqResultData = executiveSummaryService.getDqResultData(inputJson, domainId, projectId,
									fromDate, toDate);
							if (dqResultData != null && dqResultData.length() > 0) {
								responseStatus = HttpStatus.OK;
								status = "success";
							} else {
								msg = "Failed to build Result";
								LOG.error(msg);
							}

						} else {
							msg = "Invalid Domain-Project";
							LOG.error(msg);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg = "Wrong Input Data";
						LOG.error(msg);
					}

				} else {
					msg = tokenStatusObj.getString("msg");
					LOG.error(msg);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				msg = "Token is missing in headers";
				LOG.error(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Request failed";
			LOG.error(e.getMessage());
		}

		apiResponse.put("result", dqResultData);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getDqResultsForExecutiveSummary - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/downloadExecutiveSummaryCsv", method = RequestMethod.POST)
	public ResponseEntity<String> downloadExecutiveSummaryCsv(@RequestHeader HttpHeaders headers,
			@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/downloadExecutiveSummaryCsv - START");
		DateUtility.DebugLog("downloadExecutiveSummaryCsv 01",
				String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));
		LOG.debug("Getting request parameters  " + inputsJsonStr);
		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String msg = "";
		String data = "";
		String status = "failed";
		JSONObject resultJson = new JSONObject();

		String executiveSummaryFileName = "";
		InputStreamResource inputStreamResource = null;

		int domainId = 0;
		int projectId = 0;
		int selectedDataRange = 0;
		String token = "";

		HttpStatus responseStatus = HttpStatus.NOT_FOUND;

		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {

				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {

					try {
						domainId = inputJson.getInt("domainId");
						projectId = inputJson.getInt("projectId");
						// selectedDataRange = inputJson.getInt("selectedDateRange");
						String fromDate = inputJson.getString("fromDate");
						String toDate = inputJson.getString("toDate");

						// validate domain-project combination
						if (projectDAO.isDomainProjectValid(domainId, projectId)) {

							executiveSummaryFileName = executiveSummaryService.prepareExecutiveSummaryCSVFile(domainId,
									projectId, fromDate, toDate);

							if (executiveSummaryFileName != null && !executiveSummaryFileName.isEmpty()) {
								File generatedFile = new File(executiveSummaryFileName);
								List<String> lines = Files.readAllLines(Paths.get(generatedFile.getAbsolutePath()));

								for (String line : lines) {
									data = data + line.replaceAll("\"", "") + "\n";
								}
								data = data.substring(0, data.lastIndexOf("\n"));
								status = "success";
								msg = "ExecutiveSummary CSV is generated and returned back to UI";
								LOG.info(msg);
								responseStatus = HttpStatus.OK;
							} else {
								msg = "Could not generate CSV";
							LOG.error(msg);
							}
						} else {
							msg = "Invalid Domain-Project";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg = "Wrong Input Data";
						LOG.error(e.getMessage());
					}

				} else {
					msg = tokenStatusObj.getString("msg");
					LOG.error(msg);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				msg = "Token is missing in headers";
				LOG.error(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Request failed";
			LOG.error(e.getMessage());
		}

		resultJson.put("status", status);
		resultJson.put("message", msg);
		resultJson.put("result", data);
		LOG.info("dbconsole/downloadExecutiveSummaryCsv - END");
		return new ResponseEntity<>(resultJson.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getValidationResultDeviationStatus", method = RequestMethod.POST)
	public ResponseEntity<String> getValidationResultDeviationStatus(@RequestHeader HttpHeaders headers,
	  	@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getValidationResultDeviationStatus - START");
	
		String msg = "";
		String status = "failed";
		JSONObject resultJson = new JSONObject();
		JSONArray dqStatusMessages = new JSONArray();
		String token = "";

		HttpStatus responseStatus = HttpStatus.OK;
		try {
			LOG.debug("Getting request parameters  " + inputsJsonStr);
			JSONObject inputJson = new JSONObject(inputsJsonStr);
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {

				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {

					try {
						long idApp = inputJson.getLong("idApp");
						JSONObject statusObj= executiveSummaryService.getValidationDeviationStatusByCheck(idApp);

						if(statusObj!=null){

							status= statusObj.getString("status");
							msg= statusObj.getString("message");

							dqStatusMessages = statusObj.getJSONArray("result");

						}else {
							msg="Failed to calculate deviation";
							LOG.error(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg = "Wrong Input Data";
						LOG.error(msg);
					}
				}else {
					msg="Token expired";
					LOG.error(msg);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			}else {
				msg="Token is null or empty";
				LOG.error(msg);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		}catch (Exception e) {
			e.printStackTrace();
			msg = "Failed to process request";
			LOG.error(e.getMessage());
		}

		resultJson.put("status", status);
		resultJson.put("message", msg);
		resultJson.put("result", dqStatusMessages);
		LOG.info("dbconsole/getValidationResultDeviationStatus - END");
		return new ResponseEntity<>(resultJson.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getEnterpriseDTSChartData", method = RequestMethod.POST)
	public ResponseEntity<String> getEnterpriseDTSChartData(@RequestHeader HttpHeaders headers,
															@RequestBody String inputsJsonStr) {
		LOG.info("dbconsole/getEnterpriseDTSChartData - START");
		DateUtility.DebugLog("getEnterpriseData",
				String.format("Got input parameters from UI as '%1$s'", inputsJsonStr));
		LOG.debug("Getting request parameters  " + inputsJsonStr);

		JSONObject inputJson = new JSONObject(inputsJsonStr);
		String msg = "Success";
		String status = "failed";
		JSONObject dqResultData = new JSONObject();
		JSONObject apiResponse = new JSONObject();
		String token = "";
		HttpStatus responseStatus = HttpStatus.OK;
		try {
			try {
				token = headers.get("token").get(0);
				LOG.debug("token "+token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}

			// check if token is empty or not
			if (token != null && !token.isEmpty()) {
				// validate received token
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equalsIgnoreCase("success")) {

					if (inputsJsonStr != null && !inputsJsonStr.isEmpty()) {
						String fromDate = inputJson.getString("fromDate");
						String toDate = inputJson.getString("toDate");
						dqResultData = executiveSummaryService.getDqResultForEnterpriseData(
								fromDate, toDate);
						if (dqResultData != null && dqResultData.length() > 0) {
							responseStatus = HttpStatus.OK;
							status = "success";
						} else {
							msg = "Failed";
							LOG.error(msg);
						}
					} else {
						msg = "Invalid request";
						LOG.error(msg);
					}
				} else {
					msg = "Token expired";
					LOG.error(msg);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				msg = "Token is missing in header";
				LOG.error(msg);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Request failed";
			LOG.error(e.getMessage());
		}
		apiResponse.put("result", dqResultData);
		apiResponse.put("status", status);
		apiResponse.put("message", msg);
		LOG.info("dbconsole/getEnterpriseDTSChartData - END");
		return new ResponseEntity<>(apiResponse.toString(), responseStatus);
	}

}
