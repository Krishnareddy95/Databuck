package com.databuck.restcontroller;

import com.databuck.bean.UserToken;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DatabuckImportExportDao;
import com.databuck.dao.IDashboardConsoleDao;
import com.databuck.service.DatabuckImportExportService;
import com.databuck.util.DateUtility;
import com.databuck.util.FileHashUtility;
import com.databuck.util.TokenValidator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
public class DatabuckImportExportController {

    @Autowired
    private IDashboardConsoleDao dashboardConsoleDao;
    @Autowired
    private TokenValidator tokenValidator;
    @Autowired
    private DatabuckImportExportService databuckImportExportService;

    @Autowired
    private DatabuckImportExportDao databuckImportExportDao;

    @Autowired
    FileHashUtility fileHashUtility;

    private static final Logger LOG = Logger.getLogger(DatabuckImportExportController.class);

    @RequestMapping(value = "/dbconsole/exportValidationDetails", method = RequestMethod.POST)
    public String exportValidationDetails(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestStr) {
        LOG.info("/dbconsole/exportValidationDetails - START");
        JSONObject json = new JSONObject();
        String status = "failed";

        try {
            LOG.debug("token   " + request.getHeader("token"));
            String authorization = request.getHeader("token");
            boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
            if (isUserValid) {
                UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(authorization);
                JSONObject requestJson = new JSONObject(requestStr);
                String idApp = requestJson.optString("idApp", null);
                if (idApp != null && !idApp.isEmpty()) {
                    Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
                    String uniqueId = "ImpExp_" + idApp + "_" + String.valueOf(currentDate.getTime());
                    Long taskStatusId = databuckImportExportDao.saveTaskStatus(uniqueId, Long.parseLong(idApp), 0l, DatabuckConstants.DBK_FEATURE_EXPORT,
                            "", DatabuckConstants.DBK_FEATURE_STATUS_INPROGRESS, "", "", "", userToken.getIdUser(), userToken.getUserName(), "");

                    JSONObject result = databuckImportExportService.getExportDetails(requestJson);
                    if (result != null && result.getString("status").equalsIgnoreCase("success")) {
                        databuckImportExportDao.updateTaskStatus(taskStatusId, result.getString("filePath"), DatabuckConstants.DBK_FEATURE_STATUS_SUCCESS,
                                result.getString("message"), "", "", 0l);
                        json.put("message", result.getString("message"));
                        result.remove("status");
                        result.remove("message");
                        json.put("result", result);
                        status = "success";
                    } else {
                        json.put("message", result.getString("message"));
                        status = "failed";
                        databuckImportExportDao.updateTaskStatus(taskStatusId, "", DatabuckConstants.DBK_FEATURE_STATUS_FAILED,
                                "", "", result.getString("message"), 0l);
                    }
                } else {
                    LOG.error("idApp parameter is missing");
                    json.put("message", "idApp parameter is missing");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return json.toString();
                }
            } else {
                LOG.error("Invalid Authorization");
                json.put("message", "Invalid Authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "Request failed");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        json.put("status", status);

        LOG.info("/dbconsole/exportValidationDetails - END");
        return json.toString();
    }

    @RequestMapping(value = "/dbconsole/importValidationDetails", method = RequestMethod.POST)
    public String importValidationDetails(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestStr) {
        LOG.info("/dbconsole/importValidationDetails - START");
        JSONObject json = new JSONObject();
        String status = "failed";

        try {
            LOG.debug("token   " + request.getHeader("token"));
            String authorization = request.getHeader("token");
            boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
            if (isUserValid) {
                UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(authorization);
                JSONObject requestJson = new JSONObject(requestStr);
                String filePath = requestJson.optString("filePath", null);
                JSONArray connectionDetails = requestJson.optJSONArray("connectionDetails");
                if (filePath != null && !filePath.isEmpty()) {

                    String hash = fileHashUtility.calculateHash(filePath);
                    JSONObject  importedValidationDetails = databuckImportExportDao.getValidationImportByHashCode(hash);
                    if(importedValidationDetails == null){
                        Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
                        String uniqueId = "ImpExp_" + String.valueOf(currentDate.getTime());
                        Long taskStatusId = databuckImportExportDao.saveTaskStatus(uniqueId, 0l, 0l, DatabuckConstants.DBK_FEATURE_IMPORT,
                                filePath, DatabuckConstants.DBK_FEATURE_STATUS_INPROGRESS, "", "", "", userToken.getIdUser(), userToken.getUserName(), hash);

                        JSONObject result = databuckImportExportService.importValidation(filePath, connectionDetails, userToken.getIdUser().intValue(), userToken.getUserName());
                        if (result != null && result.getString("status").equalsIgnoreCase("success")) {
                            databuckImportExportDao.updateTaskStatus(taskStatusId, filePath, DatabuckConstants.DBK_FEATURE_STATUS_SUCCESS,
                                    result.getString("message"), "", "", result.getLong("newValidationId"));
                            json.put("message", result.getString("message"));
                            result.remove("status");
                            result.remove("message");
                            json.put("result", result);
                            status = "success";
                        } else {
                            json.put("message", result.getString("message"));
                            status = "failed";
                            databuckImportExportDao.updateTaskStatus(taskStatusId, filePath, DatabuckConstants.DBK_FEATURE_STATUS_FAILED,
                                    "", "", result.getString("message"), 0l);
                        }
                    } else {
                        json.put("message", "This file is already imported.");
                        json.put("importedValidationDetails", importedValidationDetails);
                        status = "failed";
                    }
                } else {
                    LOG.error("filePath parameter is missing");
                    json.put("message", "filePath parameter is missing");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return json.toString();
                }
            } else {
                LOG.error("Invalid Authorization");
                json.put("message", "Invalid Authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "Request failed");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        json.put("status", status);

        LOG.info("/dbconsole/importValidationDetails - END");
        return json.toString();
    }

    @RequestMapping(value = "/dbconsole/getConnectionDetailsForImportValidation", method = RequestMethod.POST)
    public String getConnectionDetailsForImportValidation(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestStr) {
        LOG.info("/dbconsole/getConnectionDetailsForImportValidation - START");
        JSONObject json = new JSONObject();
        String status = "failed";

        try {
            LOG.debug("token   " + request.getHeader("token"));
            String authorization = request.getHeader("token");
            boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
            if (isUserValid) {
                UserToken userToken = dashboardConsoleDao.getUserDetailsOfToken(authorization);
                JSONObject requestJson = new JSONObject(requestStr);
                String filePath = requestJson.optString("filePath", null);
                if (filePath != null && !filePath.isEmpty()) {
                    String hash = fileHashUtility.calculateHash(filePath);
                    JSONObject  importedValidationDetails = databuckImportExportDao.getValidationImportByHashCode(hash);
                    if(importedValidationDetails == null){
                        JSONObject result = databuckImportExportService.getConnectionDetailsForImportValidation(filePath);
                        if (result != null && result.getString("status").equalsIgnoreCase("success")) {
                            json.put("message", result.getString("message"));
                            result.remove("status");
                            result.remove("message");
                            json.put("result", result);
                            status = "success";
                        } else {
                            json.put("message", result.getString("message"));
                            status = "failed";
                        }
                    } else {
                        json.put("message", "This file is already imported.");
                        json.put("importedValidationDetails", importedValidationDetails);
                        status = "failed";
                    }
                } else {
                    LOG.error("filePath parameter is missing");
                    json.put("message", "filePath parameter is missing");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return json.toString();
                }
            } else {
                LOG.error("Invalid Authorization");
                json.put("message", "Invalid Authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (FileNotFoundException ex) {
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "File not found. Please provide correct filepath.");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "Request failed");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        json.put("status", status);

        LOG.info("/dbconsole/getConnectionDetailsForImportValidation - END");
        return json.toString();
    }

    @RequestMapping(value = "/dbconsole/getConnectionDetailsForImportValidationByFile", method = RequestMethod.POST)
    public String getConnectionDetailsForImportValidationByFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("importfile") MultipartFile multipartFile) {
        LOG.info("/dbconsole/getConnectionDetailsForImportValidationByFile - START");
        JSONObject json = new JSONObject();
        String status = "failed";

        try {
            LOG.debug("token   " + request.getHeader("token"));
            String authorization = request.getHeader("token");
            boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
            if (isUserValid) {

                String filename = multipartFile.getOriginalFilename();
                String fileDownloadStatus = "failed";
                String filePath = "";
                if (filename != null && !filename.trim().isEmpty()) {
                    String hostUri = System.getenv("DATABUCK_HOME") + "/importexport";
                    File hostUri_folder = new File(hostUri);
                    if(!hostUri_folder.exists() || !hostUri_folder.isDirectory()) {
                        hostUri_folder.mkdir();
                    }

                    filePath = hostUri + "/" + filename;
                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));) {
                        byte[] bytes = multipartFile.getBytes();
                        stream.write(bytes);
                        fileDownloadStatus = "success";
                        LOG.debug("File download to " + filePath + " is successful !!");

                    } catch (Exception e) {
                        LOG.error("\n====>Exception occurred while saving file to DATABUCK_HOME !!");
                        e.printStackTrace();
                    }

                } else {
                    LOG.error("File is missing");
                    json.put("message", "File is missing. Please provide file to import");
                    json.put("status", status);
                    response.setStatus(HttpServletResponse.SC_OK);
                    return json.toString();
                }

                if(fileDownloadStatus.equalsIgnoreCase("success")){
                    if (filePath != null && !filePath.isEmpty() && filePath.endsWith(".zip")) {
                        String hash = fileHashUtility.calculateHash(filePath);
                        JSONObject  importedValidationDetails = databuckImportExportDao.getValidationImportByHashCode(hash);
                        if(importedValidationDetails == null){
                            JSONObject result = databuckImportExportService.getConnectionDetailsForImportValidation(filePath);
                            if (result != null && result.getString("status").equalsIgnoreCase("success")) {
                                json.put("message", result.getString("message"));
                                result.put("filePath", filePath);
                                result.remove("status");
                                result.remove("message");
                                json.put("result", result);
                                status = "success";
                            } else {
                                json.put("message", result.getString("message"));
                                status = "failed";
                            }
                        } else {
                            json.put("message", "This file is already imported.");
                            json.put("importedValidationDetails", importedValidationDetails);
                            status = "failed";
                        }
                    } else {
                        LOG.error("Please provide proper compress file to import.");
                        json.put("message", "Please provide proper compress file to import.");
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                } else {
                    LOG.error("Failed to download file");
                    json.put("message", "Failed to download file");
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            } else {
                LOG.error("Invalid Authorization");
                json.put("message", "Invalid Authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "Request failed");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        json.put("status", status);

        LOG.info("/dbconsole/getConnectionDetailsForImportValidationByFile - END");
        return json.toString();
    }

    @RequestMapping(value = "/dbconsole/downloadExportedFile", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestBody String requestStr) throws IOException {
        LOG.info("/dbconsole/download - START");
        JSONObject json = new JSONObject();
        String status = "failed";

        try {
            LOG.debug("token   " + request.getHeader("token"));
            String authorization = request.getHeader("token");
            boolean isUserValid = tokenValidator.isValid(authorization != null ? authorization : "");
            if (isUserValid) {
                JSONObject requestJson = new JSONObject(requestStr);
                String filePath = requestJson.optString("filePath", null);
                File file = new File(filePath);
                InputStream inputStream = new FileInputStream(file);

                byte[] fileContent = new byte[(int) file.length()];
                inputStream.read(fileContent);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", file.getName());
                return ResponseEntity.ok().headers(headers).body(fileContent);
            } else {
                LOG.error("Invalid Authorization");
                json.put("message", "Invalid Authorization");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("Exception  " + ex.getMessage());
            json.put("message", "Request failed");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        return null;
    }


}

