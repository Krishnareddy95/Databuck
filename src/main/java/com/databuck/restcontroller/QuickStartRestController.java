package com.databuck.restcontroller;

import com.databuck.bean.Project;
import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.ExecutiveSummaryService;
import com.databuck.service.QuickStartService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import org.apache.log4j.Logger;

@CrossOrigin(origins = "*")
@RestController
public class QuickStartRestController {
	@Autowired
	ExecutiveSummaryService executiveSummaryService;

	@Autowired
	QuickStartService quickStartService;
	@Autowired
	DataProfilingTemplateService dataProfilingTemplateService;

	@Autowired
	IDashboardConsoleDao dashboardConsoleDao;

	@Autowired
	IProjectDAO iProjectDAO;

	@Autowired
	private ITaskDAO iTaskDAO;
	
	private static final Logger LOG = Logger.getLogger(QuickStartRestController.class);

	@RequestMapping(value = "/dbconsole/processDQQuickStart", method = RequestMethod.POST)
	public ResponseEntity<Object> processLocalFSQuickStartRestApi(@RequestHeader HttpHeaders headers,
			@RequestParam("dataupload") MultipartFile file, @RequestParam Long projectId,@RequestParam Integer domainId) {
		LOG.info("dbconsole/processDQQuickStart - START");
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
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					Project project = iProjectDAO.getSelectedProject(projectId);
					String filename = file.getOriginalFilename();
					if (project != null) {

						if (filename != null && !filename.trim().isEmpty()) {
							String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart";
							
							// Create Path if not exists
							File hostUri_folder = new File(hostUri);
							if(!hostUri_folder.exists() || !hostUri_folder.isDirectory()) {
								hostUri_folder.mkdir();
							}

							String path = hostUri + "/" + filename;
							String fileDownloadStatus = "failed";
							String fileDownloadMessage = "";
							long templateId = 0l;
							String templateUniqueId = "";
							String dataTemplateName = "QuickStart_" + filename;

							if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

								// Download file to DATABUCK_HOME directory
								try (BufferedOutputStream stream = new BufferedOutputStream(
										new FileOutputStream(new File(path)));) {
									byte[] bytes = file.getBytes();
									stream.write(bytes);
									fileDownloadStatus = "success";
									fileDownloadMessage = "File download to " + path + " is successful !!";

								} catch (Exception e) {
									LOG.error("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
									e.printStackTrace();
								}

								int count = 0;
								if(fileDownloadStatus.equalsIgnoreCase("success")){
									try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)));) {
										while (reader.readLine() != null){
											count ++;
											if(count>2)
												break;
										}
									} catch (Exception e) {
										LOG.error("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
										e.printStackTrace();
									}
								}

								// When file download is success create template
								if (fileDownloadStatus.equalsIgnoreCase("success") && count>=2) {
									String createdByUser = userToken.getUserName();
									Long idUser = userToken.getIdUser();
									//dataProfilingTemplateService.domainId=domainId;
									// Create a template
									CompletableFuture<Long> createTemplate = dataProfilingTemplateService
											.createDataTemplateWithDomainId(null, 0l, "File System", filename, dataTemplateName, "",
													"", "Y", "", "", "N", "", null, "", "", "", "", idUser, hostUri,
													filename, "csv", "", "", "", file, "", "", "Y", null, projectId,
													"Y", createdByUser, "N", null, "", null, null,domainId);
									templateId = createTemplate.get();
									templateUniqueId = dataProfilingTemplateService
											.triggerDataTemplate(templateId, "File System", "Y", "Y").get();
									status = "success";
									message = "Template creation is in progress";
								} else if(count == 1){
									fileDownloadMessage = "Unable to process this file!! It only contains header!!";
									fileDownloadStatus = "failed";
									status = "success";
								} else if (count == 0) {
									fileDownloadMessage = "Unable to process blank file!!";
									fileDownloadStatus = "failed";
									status = "success";
								} else {
									LOG.error("\n====>File doesn't exist in DATABUCK_HOME!!");
									fileDownloadMessage = "File doesn't exist!!";
									fileDownloadStatus = "failed";
								}
							} else {
								LOG.error(
										"\n====>Unable to process this file!! Currently this feature supports only csv files!!");
								fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
							}

							result.put("fileDownloadStatus", fileDownloadStatus);
							result.put("fileDownloadMessage", fileDownloadMessage);
							result.put("fileLocation", path);
							result.put("fileName", filename);
							result.put("templateId", templateId);
							result.put("templateName", dataTemplateName);
							result.put("uniqueId", templateUniqueId);
							json.put("result", result);
						} else {
							message = "File could not be found.";
							LOG.error(message);
							responseStatus = HttpStatus.EXPECTATION_FAILED;
						}
					} else {
						message = "Invalid ProjectId";
						LOG.error(message);
						responseStatus = HttpStatus.EXPECTATION_FAILED;
					}
					// changes regarding Audit trail
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
							DatabuckConstants.DBK_FEATURE_QUICK_START, formatter.format(new Date()), 0l,
							DatabuckConstants.ACTIVITY_TYPE_CREATED,filename);
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
			LOG.error("Message "+message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/processDQQuickStart - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/getQuickStartValidationDetailsByIdData", method = RequestMethod.POST)
	public ResponseEntity<Object> getQuickStartValidationDetailsByIdData(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/getQuickStartValidationDetailsByIdData - START");

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
						long idData = inputJson.getLong("idData");
						String enableMicrosegment = inputJson.getString("enableMicrosegment");
						
						// get user email
						UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
						JSONObject obj = quickStartService.getQuickStartValidationDetailsByIdData(idData,
								userToken.getEmail(), enableMicrosegment);
						if (obj != null && obj.length() > 0) {

							message = obj.getString("message");
							status = obj.getString("status");
							result = obj.getJSONObject("result");
						} else {
							message = "Failed result";
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
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			
		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/getQuickStartValidationDetailsByIdData - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/processDQQuickStartByPath", method = RequestMethod.POST)
	public ResponseEntity<Object> processLocalFSQuickStartByPathRestApi(@RequestHeader HttpHeaders headers,
			@RequestBody String inputJsonStr) {
		LOG.info("dbconsole/processDQQuickStartByPath - START");
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
				LOG.debug(token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String filePath = inputJson.getString("filePath");
						Long projectId = inputJson.getLong("projectId");
						Integer domainId = inputJson.getInt("domainId");
						File file = new File(filePath);
						Project project = iProjectDAO.getSelectedProject(projectId);

						if (project != null) {
							String filename = file.getName();

							if (filename != null && !filename.trim().isEmpty()) {
								String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart";

								String fileDownloadStatus = "failed";
								String fileDownloadMessage = "";
								long templateId = 0l;
								String templateUniqueId = "";
								String dataTemplateName = "QuickStart_" + filename;

								if (filename.split("\\.")[1].equalsIgnoreCase("csv")) {

									DiskFileItem fileItem = new DiskFileItem("file", "text/csv", false, file.getName(),
											(int) file.length(), file.getParentFile());
									fileItem.getOutputStream();
									fileDownloadStatus = "success";
									MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

									int count = 0;
									if(fileDownloadStatus.equalsIgnoreCase("success")){
										try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));) {
											while (reader.readLine()!=null){
												count++;
												if(count>2)
													break;
											}
										} catch (Exception e) {
											LOG.error("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
											e.printStackTrace();
											count = -1;
										}
									}

									if (fileDownloadStatus.equalsIgnoreCase("success") && count>=2) {
										String createdByUser = userToken.getUserName();
										Long idUser = userToken.getIdUser();
//										dataProfilingTemplateService.domainId=domainId;

										// Create a template
										CompletableFuture<Long> createTemplate = dataProfilingTemplateService
												.createDataTemplateWithDomainId(null, 0l, "File System", filename, dataTemplateName,
														"", "", "Y", "", "", "N", "", null, "", "", "", "", idUser,
														hostUri, filename, "csv", "", "", "", multipartFile, "", "",
														"Y", null, projectId, "Y", createdByUser, "N", null, "", null,
														null, domainId);
										templateId = createTemplate.get();
										templateUniqueId = dataProfilingTemplateService
												.triggerDataTemplate(templateId, "File System", "Y", "Y").get();
										fileDownloadMessage = "File download to " + filePath + " is successful !!";
										status = "success";
										message = "Template creation is in progress";
									} else if(count == 1){
										fileDownloadMessage = "Unable to process this file!! It only contains header!!";
										fileDownloadStatus = "failed";
										status = "success";
									}  else if(count == 0){
										fileDownloadMessage = "Unable to process blank file!!";
										fileDownloadStatus = "failed";
										status = "success";
									}else {
										LOG.error("\n====>File doesn't exist in DATABUCK_HOME!!");
										fileDownloadMessage = "File doesn't exist!!";
										fileDownloadStatus = "failed";
									}
								} else {
									LOG.error(
											"\n====>Unable to process this file!! Currently this feature supports only csv files!!");
									fileDownloadMessage = "Unable to process this file!! Currently this feature supports only csv files!!";
								}

								result.put("fileDownloadStatus", fileDownloadStatus);
								result.put("fileDownloadMessage", fileDownloadMessage);
								result.put("fileLocation", filePath);
								result.put("fileName", filename);
								result.put("templateId", templateId);
								result.put("templateName", dataTemplateName);
								result.put("uniqueId", templateUniqueId);
								json.put("result", result);
							} else {
								
								message = "File could not be found.";
							    LOG.error(message);
							}
						} else {
							message = "Invalid ProjectId";
							LOG.error(message);
						}
						// changes regarding Audit trail
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						iTaskDAO.addAuditTrailDetail(userToken.getIdUser(), userToken.getUserName(),
								DatabuckConstants.DBK_FEATURE_QUICK_START, formatter.format(new Date()), 0l,
								DatabuckConstants.ACTIVITY_TYPE_CREATED,filePath);
					} else
					{
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
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			

		} catch (Exception e) {
			message = "Request failed";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		json.put("result", result);
		LOG.info("dbconsole/processDQQuickStartByPath - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	@RequestMapping(value = "/dbconsole/deleteFileFromPath", method = RequestMethod.POST)
	public ResponseEntity<Object> deleteFileFromPath(@RequestHeader HttpHeaders headers,
													 @RequestBody String inputJsonStr) {

		LOG.info("dbconsole/deleteFileFromPath - START");
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
				LOG.debug(token.toString());
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");
				UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(token);
				if (tokenStatus.equals("success")) {
					if (inputJsonStr != null && !inputJsonStr.trim().isEmpty()) {
						LOG.debug("Getting  parameters for inputJsonStr , " + inputJsonStr);
						JSONObject inputJson = new JSONObject(inputJsonStr);
						String filePath = inputJson.getString("filePath");
						if (filePath != null && !filePath.trim().isEmpty()) {
							String hostUri = System.getenv("DATABUCK_HOME") + "/quickStart";
							if (filePath.contains(hostUri)) {
								File file = new File(filePath);
								if (file.delete()) {
									message = file.getName() + " is deleted!";
									status = "success";
								} else
								{
									message = "Failed to delete the file.";
									LOG.error(message);
								}
							} else {
								message = "Invalid file path.";
								LOG.error(message);
							}

						} else
						{
							message = "File could not be found.";
							LOG.error(message);
						}
					}
				} else {
					message = "Token expired.";
					LOG.error(message);
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}
			} else {
				message = "Token is missing in headers";
				LOG.error(message);
				responseStatus = HttpStatus.EXPECTATION_FAILED;
			}
			
		} catch (Exception e) {
			message = "Failed to delete the file.";
			LOG.error(message);
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		LOG.info("dbconsole/deleteFileFromPath - END");
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

}