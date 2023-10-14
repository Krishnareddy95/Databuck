package com.databuck.service;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.GlobalFilters;
import com.databuck.bean.SynonymLibrary;
import com.databuck.bean.RuleMappingDetails;
import com.databuck.bean.RuleCatalog;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.DatabuckImportExportDao;
import com.databuck.util.DatabuckUtility;
import com.databuck.util.DateUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
public class DatabuckImportExportService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final Logger LOG = Logger.getLogger(DatabuckImportExportService.class);

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    IValidationCheckDAO iValidationCheckDAO;

    @Autowired
    IListDataSourceDAO listDataSourceDAO;

    @Autowired
    ITemplateViewDAO templateViewDAO;

    @Autowired
    IDataTemplateAddNewDAO iDataTemplateAddNewDAO;

    @Autowired
    private RuleCatalogDao ruleCatalogDao;

    @Autowired
    private GlobalRuleDAO globalRuleDAO;

    @Autowired
    DatabuckImportExportDao databuckImportExportDao;

    @Autowired
    RuleCatalogService ruleCatalogService;

    static Long domainId = 0L;
    static Long projectId = 0L;

    public JSONObject getExportDetails(JSONObject requestJson) {
        JSONArray templateResultArray = new JSONArray();
        JSONObject validationResult = new JSONObject();
        JSONObject templateResult = new JSONObject();
        JSONObject connectionResult = new JSONObject();
        JSONObject globalRuleResult = new JSONObject();
        JSONObject response = new JSONObject();

        String responseStatus = "failed";
        String responseMessage = "";

        try {

            Set<Long> templateIdSet = new HashSet<>();

            String idApp = requestJson.optString("idApp", null);
            if (idApp != null && !idApp.isEmpty()) {

                /*
                 * Read Validation Data
                 */
                JSONObject validation = new JSONObject();
                LOG.info("\n====> Read validation ..");
                ListApplications listApplications = iValidationCheckDAO.getdatafromlistapplications(Long.valueOf(idApp));
                if (listApplications != null) {

                    Gson gson = new GsonBuilder().serializeNulls().create();
                    String listApplicationsStr = gson.toJson(listApplications);
                    JSONObject listApplication = new JSONObject(listApplicationsStr);
                    validation.put("listApplication", listApplication);

                    // Read rule catalog
                    Map<String, List<RuleCatalog>> ruleCatalog = getValidationRuleCatlogData(Long.parseLong(idApp));
                    validation.put("ruleCatalog", ruleCatalog);

                    validationResult.put("validation", validation);

                    /*
                     * Read Template Data
                     */
                    long idData = listApplications.getIdData();
                    templateIdSet.add(idData);
                    boolean isDerivedTemplate = false;
                    Set<Long> connectionIdList = new HashSet<>();

                    JSONObject template = getTemplateData(idData);
                    Map<String, List<ListDataDefinition>> dataDefination = getTemplateDataDefination(idData);
                    template.put("dataDefination", dataDefination);
                    List<listDataAccess> listDataAccess = templateViewDAO.getDataFromListDataAccessToExport(idData);
                    template.put("listDataAccess", listDataAccess);
                    List<RuleMappingDetails> ruleMappingDetailsList = getGlobalRuleLinking(idData);
                    template.put("ruleMappingDetailsList", ruleMappingDetailsList);

                    templateResultArray.put(template);


                    long template1IdData = 0;
                    long template2IdData = 0;
                    if (template.has("listDerivedDataSource")) {
                        isDerivedTemplate = true;
                        JSONObject listDerivedDataSource = template.getJSONObject("listDerivedDataSource");
                        template1IdData = listDerivedDataSource.optLong("template1IdData");
                        template2IdData = listDerivedDataSource.optLong("template2IdData");
                        if (template1IdData > 0) {
                            JSONObject linkedToDerivedTemplate = getTemplateDataLinkedToDerivedTemplate(template1IdData);
                            long template1IdDataSchema = linkedToDerivedTemplate.getJSONObject("listDataSource").getLong("idDataSchema");
                            if (linkedToDerivedTemplate.has("listDerivedDataSource")) {
                                throw new Exception("Derived template is linked so cannot export validation.");
                            }
                            templateResultArray.put(linkedToDerivedTemplate);
                            templateIdSet.add(template1IdData);
                            connectionIdList.add(template1IdDataSchema);
                        }
                        if (template2IdData > 0) {
                            JSONObject linkedToDerivedTemplate = getTemplateDataLinkedToDerivedTemplate(template2IdData);
                            long template2IdDataSchema = linkedToDerivedTemplate.getJSONObject("listDataSource").getLong("idDataSchema");
                            if (linkedToDerivedTemplate.has("listDerivedDataSource")) {
                                throw new Exception("Derived template is linked so cannot export validation.");
                            }
                            templateResultArray.put(linkedToDerivedTemplate);
                            templateIdSet.add(template2IdData);
                            connectionIdList.add(template2IdDataSchema);
                        }
                    } else {
                        connectionIdList.add(listDataAccess.get(0).getIdDataSchema());
                    }

                    Map<String, Set<Long>> linkedIds = databuckImportExportDao.getLinkedIds(idData, StringUtils.join(connectionIdList, ","));
                    Map<String, JSONArray> excludedRuleAndTemplate = databuckImportExportDao.getExcludedRuleAndTemplate(idData, StringUtils.join(connectionIdList, ","));

                    // Get rightTemplateMapping from global rule
                    Set<Long> rightTemplateIds = linkedIds.get("templateId");

                    /*
                     * Read Linked Template Data
                     */
                    if (!rightTemplateIds.isEmpty()) {
                        for (Long rightTemplateId : rightTemplateIds) {
                            if (rightTemplateId == null || templateIdSet.contains(rightTemplateId)) {
                                continue;
                            }

                            JSONObject rightTemplate = getTemplateData(rightTemplateId);
                            List<listDataAccess> rightListDataAccess = templateViewDAO.getDataFromListDataAccessToExport(rightTemplateId);

                            long rightConnectionId = rightListDataAccess.get(0).getIdDataSchema();
                            if (connectionIdList.contains(rightConnectionId)) {
                                templateIdSet.add(rightTemplateId);

                                Map<String, List<ListDataDefinition>> rightDataDefination = getTemplateDataDefination(rightTemplateId);
                                rightTemplate.put("dataDefination", rightDataDefination);

                                rightTemplate.put("listDataAccess", rightListDataAccess);
                                templateResultArray.put(rightTemplate);

//                                List<RuleMappingDetails> rightRuleMappingDetailsList = getGlobalRuleLinking(Long.parseLong(rightTemplateId));
//                                rightTemplate.put("ruleMappingDetailsList", rightRuleMappingDetailsList);

                                connectionIdList.add(rightListDataAccess.get(0).getIdDataSchema());
                            }

                        }

                    }

                    templateResult.put("template", templateResultArray);

                    /*
                     * Read Connection Data
                     */
                    List<ListDataSchema> listDataSchemas = new ArrayList<>();
                    for (Long idDataSchema : connectionIdList) {
                        listDataSchemas.addAll(getConnectionData(idDataSchema));
                    }
                    for(ListDataSchema listDataSchema: listDataSchemas){
                        String encryptedPassword = listDataSchema.getPassword();
                        if(encryptedPassword != null &&! encryptedPassword.trim().isEmpty()) {
                            try {
                                StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
                                decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
                                encryptedPassword = decryptor.encrypt(encryptedPassword);
                            } catch(Exception e) {
                                LOG.error(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        listDataSchema.setPassword(encryptedPassword);
                    }
                    connectionResult.put("connection", listDataSchemas);


                    String ruleIds = "";
                    if (linkedIds != null && !linkedIds.isEmpty() && !linkedIds.get("ruleId").isEmpty()) {
                        ruleIds = StringUtils.join(linkedIds.get("ruleId"), ",");
                    }

                    /*
                     * Read Global Rule Data
                     */
                    List<ListColGlobalRules> listColGlobalRules = new ArrayList<>();
                    if (ruleIds != null && !ruleIds.isEmpty()) {
                        String skippedRuleNames = "";
                        listColGlobalRules = getGlobalRule(ruleIds);
                        // Added to take the global rules for same connection templates, others are skiped.
                        listColGlobalRules = listColGlobalRules.stream()
                                .filter(globalRules -> templateIdSet.contains(globalRules.getIdRightData()) || globalRules.getIdRightData() <= 0)
                                .collect(Collectors.toList());

                        if (!listColGlobalRules.isEmpty()) {

                            JSONArray jsonArray = new JSONArray(gson.toJson(listColGlobalRules));
                            globalRuleResult.put("globalRule", jsonArray);
                            Map<Integer, Set<String>> synonymsMap = new HashMap<>();

                            for (ListColGlobalRules globalRules : listColGlobalRules) {

                                // Read Synonyms from rule expression and matching rule
                                String synonymsStr = globalRules.getExpression() + globalRules.getMatchingRules();
                                Set<String> synonymsList = new HashSet<>();
                                synonymsList.addAll(Arrays.asList(StringUtils.substringsBetween(synonymsStr, "@", "@")));
                                if (!synonymsList.isEmpty()) {
                                    if (synonymsMap.containsKey(globalRules.getDomain_id())) {
                                        synonymsList.addAll(synonymsMap.get(globalRules.getDomain_id()));
                                        synonymsMap.put(globalRules.getDomain_id(), synonymsList);
                                    } else {
                                        synonymsMap.put(globalRules.getDomain_id(), synonymsList);
                                    }
                                }

                            }

                            String globalFilterIds = "";
                            if (linkedIds != null && !linkedIds.isEmpty()) {
                                Set<Long> filterIds = linkedIds.get("filterId");
                                filterIds.addAll(linkedIds.get("rightTemplateFilterId"));
                                globalFilterIds = StringUtils.join(filterIds, ",");
                            }
                            List<GlobalFilters> globalFiltersList = new ArrayList<>();
                            if (!globalFilterIds.isEmpty()) {
                                globalFiltersList = getGlobalFilter(globalFilterIds);
                            }
                            if (!globalFiltersList.isEmpty()) {
                                for (GlobalFilters globalFilter : globalFiltersList) {
                                    Set<String> synonymsList = new HashSet<>();
                                    synonymsList.addAll(Arrays.asList(StringUtils.substringsBetween(globalFilter.getFilterCondition(), "@", "@")));
                                    if (synonymsMap.containsKey(globalFilter.getDomainId())) {
                                        synonymsList.addAll(synonymsMap.get(globalFilter.getDomainId()));
                                        synonymsMap.put(globalFilter.getDomainId(), synonymsList);
                                    } else {
                                        synonymsMap.put(globalFilter.getDomainId(), synonymsList);
                                    }
                                }
                                globalRuleResult.put("globalFilter", globalFiltersList);
                            }

                            if (!synonymsMap.isEmpty()) {
                                List<SynonymLibrary> synonymsList = new ArrayList<>();
                                for (Integer domain : synonymsMap.keySet()) {
                                    Set<String> synDetails = synonymsMap.get(domain);
                                    List<SynonymLibrary> synonymLibraryList = globalRuleDAO.getSynonymViewList(domain);
                                    synonymsList.addAll(synonymLibraryList.stream().filter(synonymLibrary -> synDetails.contains(synonymLibrary.getTableColumn())).collect(Collectors.toList()));
                                }
                                globalRuleResult.put("synonyms", synonymsList);
                            }
                        }
                    }

                    if (excludedRuleAndTemplate.containsKey("rule")) {
                        response.put("skippedRules", excludedRuleAndTemplate.get("rule"));
                    }

                    LOG.info("\n\n====> Save data to json files ..");
                    Date currentDate = DateUtility.getCurrentDateTimeByTimeZone(DatabuckConstants.DATABUCK_JOB_TIMEZONE_UTC);
                    String uniqueId = idApp + "_" + String.valueOf(currentDate.getTime());
                    LOG.info("\n====> Unique Id :: " + uniqueId);
                    LOG.info("\n====> Saving Validation Result");
                    saveDataToJsonFile("validationResult.json", validationResult, uniqueId);
                    LOG.info("\n====> Saving Template Result");
                    saveDataToJsonFile("templateResult.json", templateResult, uniqueId);
                    LOG.info("\n====> Saving Connection Result");
                    saveDataToJsonFile("connectionResult.json", connectionResult, uniqueId);
                    LOG.info("\n====> Saving Global Rule Result");
                    saveDataToJsonFile("globalRuleResult.json", globalRuleResult, uniqueId);

                    /*
                     * Compress results directory
                     */
                    LOG.info("\n\n====> Adding files to zip ::");
                    String path = compressData(uniqueId);
                    response.put("filePath", path + ".zip");
                    responseStatus = "success";
                    responseMessage = "Validation exported successfully.";

                } else {
                    LOG.info("Validation not found");
                    responseStatus = "failed";
                    responseMessage = "Validation not found.";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseStatus = "failed";
            responseMessage = ex.getMessage();
        }
        response.put("message", responseMessage);
        response.put("status", responseStatus);
        return response;
    }

    public Map<String, List<RuleCatalog>> getValidationRuleCatlogData(long idApp) {
        Map<String, List<RuleCatalog>> ruleCatalog = new HashMap<>();
        try {
            // Read actual rule catalog
            LOG.info("\n====> Read rules from actual rule catalog ..");
            List<RuleCatalog> catalogRulesList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);
            ruleCatalog.put("catalogRulesList", catalogRulesList);

//            // Read stating rule catalog
//            LOG.info("\n====> Read rules from staging rule catalog ..");
//            List<RuleCatalog> stagingCatalogRulesList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
//            ruleCatalog.put("stagingCatalogRulesList", stagingCatalogRulesList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ruleCatalog;
    }

    public JSONObject getTemplateData(long idData) {
        JSONObject jsonObject = new JSONObject();
        try {
            // Read Template Data
            LOG.info("\n====> Read Template Data ..");
            ListDataSource listDataSource = listDataSourceDAO.getDataFromListDataSourcesOfIdData(idData);
            Gson gson = new GsonBuilder().serializeNulls().create();
            String listDataSourceStr = gson.toJson(listDataSource);
            JSONObject listDataSourceJson = new JSONObject(listDataSourceStr);
            jsonObject.put("listDataSource", listDataSourceJson);
            if (listDataSource.getDataLocation() != null && listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
                ListDerivedDataSource listDerivedDataSource = listDataSourceDAO.getDataFromListDerivedDataSourcesOfIdData(idData);
                String derivedListDataSourceStr = gson.toJson(listDerivedDataSource);
                jsonObject.put("listDerivedDataSource", new JSONObject(derivedListDataSourceStr));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getTemplateDataLinkedToDerivedTemplate(long idData) {
        JSONObject jsonObject = new JSONObject();
        try {
            // Read Template Data
            LOG.info("\n====> Read Template Data ..");
            ListDataSource listDataSource = listDataSourceDAO.getDataFromListDataSourcesOfIdData(idData);
            Gson gson = new GsonBuilder().serializeNulls().create();
            String listDataSourceStr = gson.toJson(listDataSource);
            JSONObject listDataSourceJson = new JSONObject(listDataSourceStr);
            jsonObject.put("listDataSource", listDataSourceJson);
            if (listDataSource.getDataLocation() != null && listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
                ListDerivedDataSource listDerivedDataSource = listDataSourceDAO.getDataFromListDerivedDataSourcesOfIdData(idData);
                String derivedListDataSourceStr = gson.toJson(listDerivedDataSource);
                jsonObject.put("listDerivedDataSource", new JSONObject(derivedListDataSourceStr));
            }
            Map<String, List<ListDataDefinition>> dataDefination = getTemplateDataDefination(idData);
            jsonObject.put("dataDefination", dataDefination);
            List<listDataAccess> listDataAccess = templateViewDAO.getDataFromListDataAccessToExport(idData);
            jsonObject.put("listDataAccess", listDataAccess);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    public Map<String, List<ListDataDefinition>> getTemplateDataDefination(long idData) {
        Map<String, List<ListDataDefinition>> dataDefination = new HashMap<>();
        try {

            // Read Template DataDefination
            LOG.info("\n====> Read Template Defination ..");
            List<ListDataDefinition> listDataDefinitions = templateViewDAO.view(idData);
            dataDefination.put("listDataDefinitions", listDataDefinitions);

            // Read Staging Template Data Defination
//            LOG.info("\n====> Read Staging Template Defination ..");
//            List<ListDataDefinition> stagingListDataDefinitions = templateViewDAO.getListDataDefinitionsInStaging(idData);
//            dataDefination.put("stagingListDataDefinitions", stagingListDataDefinitions);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dataDefination;
    }

    public List<ListDataSchema> getConnectionData(long idDataSchema) {
        List<ListDataSchema> connectionList = new ArrayList<>();
        try {
            // Read Connection Details
            LOG.info("\n====> Read connection details..");
            connectionList = iDataTemplateAddNewDAO.getListDataSchema(idDataSchema);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return connectionList;
    }

    public List<ListColGlobalRules> getGlobalRule(String ruleIds) {
        List<ListColGlobalRules> listColGlobalRules = new ArrayList<>();
        try {
            LOG.info("\n====> Get Global Rule ..");
            listColGlobalRules = globalRuleDAO.getGlobalRulesForExport(ruleIds);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listColGlobalRules;
    }

    public List<RuleMappingDetails> getGlobalRuleLinking(long idData) {
        List<RuleMappingDetails> ruleMappingDetailsList = new ArrayList<>();
        try {
            ruleMappingDetailsList = globalRuleDAO.getMappedGlobalRuleForTemplate(idData);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return ruleMappingDetailsList;
    }

    public List<GlobalFilters> getGlobalFilter(String globalFilterIds) {
        List<GlobalFilters> globalFiltersList = new ArrayList<>();
        try {
            // Read Global Filters
            LOG.info("\n====> Get Global Filters ..");
            globalFiltersList = globalRuleDAO.getGlobalFilterForExport(globalFilterIds);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return globalFiltersList;
    }

    public static void saveDataToJsonFile(String fileName, JSONObject jsonObject, String uniqueId) {

        String databuckHome = DatabuckUtility.getDatabuckHome();
        String directoryPath = databuckHome + "/importexport/" + uniqueId; // Specify directory path
        Path filePath = Paths.get(directoryPath, fileName); // Create path for file

        try {
            if (!Files.exists(filePath.getParent())) { // Check if directory exists
                Files.createDirectories(filePath.getParent()); // Create directory if it doesn't exist
            }
            Files.write(filePath, jsonObject.toString().getBytes()); // Write data to file
            LOG.debug("Data saved to " + fileName + " successfully.");
            LOG.debug("File saved successfully: " + filePath);
        } catch (IOException e) {
            LOG.debug("Error while saving file: " + e.getMessage());
        }
    }

    public static String compressData(String uniqueId) throws IOException {
        String databuckHome = DatabuckUtility.getDatabuckHome();
        String directoryPath = databuckHome + "/importexport/" + uniqueId;
        // Name of the output zip file
        String zipFilePath = directoryPath + ".zip";
        LOG.debug("zipFilePath :: " + zipFilePath);
        // Create an output stream to write to the zip file
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        // Recursively add all files in the directory to the zip output stream
        File directory = new File(directoryPath);
        addFilesToZip(directory, directory.getName(), zos);

        // Close the zip output stream
        zos.close();
        fos.close();

        return directoryPath;
    }

    private static void addFilesToZip(File file, String fileName, ZipOutputStream zos) throws IOException {

        // If the file is a directory, recursively add all files in the directory to the zip output stream
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                LOG.debug("Adding file to zip :: " + f.getName());
                addFilesToZip(f, fileName + "/" + f.getName(), zos);
            }
        }
        // If the file is a regular file, add it to the zip output stream
        else {
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(fileName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    public JSONObject importValidation(String zipFilePath, JSONArray connectionDetails, int userId, String userName) {
        JSONObject response = new JSONObject();
        Map<Long, Long> templateIds = new HashMap<>();
        Map<String, Map<Long, Long>> ruleMappingIds = new HashMap<>();
        Long idApp = 0L;
        LOG.debug("=====>>>  Inside importValidation :: ");
        try {
            LOG.debug("\n\n====>> File Path :: " + zipFilePath);

            JSONObject validationResult = null;
            JSONObject templateResult = null;
            JSONObject connectionResult = null;
            JSONObject globalRuleResult = null;

            LOG.debug("\n\n====>> Reading Data");
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                List<ZipEntry> zipEntryList = zipFile.stream()
                        .filter(zipEntry -> !zipEntry.isDirectory() && zipEntry.getName().endsWith(".json"))
                        .collect(Collectors.toList());
                if (!zipEntryList.isEmpty()) {
                    for (ZipEntry entry : zipEntryList) {

                        // Read the contents of the JSON file
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8));
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        reader.close();

                        // Parse the JSON content
//                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        if (entry.getName().endsWith("validationResult.json")) {
                            LOG.debug("Reading data for Validation");
                            validationResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("templateResult.json")) {
                            LOG.debug("Reading data for Template");
                            templateResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("connectionResult.json")) {
                            LOG.debug("Reading data for Connection");
                            connectionResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("globalRuleResult.json")) {
                            globalRuleResult = new JSONObject(jsonContent.toString());
                        }
                    }

                }

            } catch (JSONException ex){
                throw new Exception("Invalid data to import. Please provide correct file to import validation.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(e.getMessage());
            }

            if(connectionResult==null || validationResult==null || templateResult==null || globalRuleResult==null){
                throw new Exception("Incomplete data to import. Please provide correct file to import validation.");
            }

            List<ListDataSchema> listDataSchemas = new ArrayList<>();
            Map<Long, Long> schemaMap = new HashMap<>();
            for (int i = 0; i < connectionDetails.length(); i++) {
                JSONObject jsonObject = connectionDetails.getJSONObject(i);
                long schemaId = Long.parseLong(jsonObject.get("newConnectionId").toString());
                long idDataSchema = Long.parseLong(jsonObject.get("oldConnectionId").toString());
                String schemaType = jsonObject.getString("oldConnectionType");
                List<ListDataSchema> dataSchemas = iDataTemplateAddNewDAO.getListDataSchema(schemaId);
                if (dataSchemas != null && !dataSchemas.isEmpty()) {
                    if(dataSchemas.get(0).getSchemaType().equalsIgnoreCase(schemaType)){
                        listDataSchemas.addAll(dataSchemas);
                        schemaMap.put(idDataSchema, schemaId);
                    } else {
                        throw new Exception("Connection details for connection type "+schemaType+" is incorrect.");
                    }
                } else {
                    throw new Exception("Connection details not found.");
                }
            }

            if (listDataSchemas != null && !listDataSchemas.isEmpty()) {
                ListDataSchema listDataSchema = listDataSchemas.get(0);
                projectId = listDataSchema.getProjectId();
                domainId = listDataSchema.getDomainId().longValue();
            }
            if (validationResult != null) {

                if (templateResult != null) {
                    LOG.debug("\n\n====>> Importing Template Data");
                    LOG.debug("Reading Json for Template Data");
                    if (templateResult.has("template")) {
                        LOG.debug("******************** Importing Template Data Started ********************");
                        templateIds = saveTemplateData(templateResult, listDataSchemas, schemaMap, userId, userName);
                        LOG.debug("******************** Importing Template Data Completed ********************");
                    }

                } else {
                    LOG.debug("Template is not available to Import");
                }

                if (globalRuleResult != null) {
                    LOG.debug("******************** Importing Global Rule Data Started ********************");
                    ruleMappingIds = saveGlobalRuleData(globalRuleResult, templateIds, userId, userName);
                    LOG.debug("******************** Importing Global Rule Data Completed ********************");

                } else {
                    LOG.debug("Global Rule is not available to Import");
                }

                if (templateResult != null) {
                    LOG.debug("\n\n====>> Importing Template Global Rule Mapping Data");
                    LOG.debug("Reading Json for Template Data");
                    if (templateResult.has("template")) {
                        LOG.debug("******************** Importing Template Global Rule Mapping Data Started ********************");
                        saveTemplateGlobalRuleMappingData(templateResult, templateIds, ruleMappingIds);
                        LOG.debug("******************** Importing Template Global Rule Mapping Data Completed ********************");
                    }

                } else {
                    LOG.debug("Template Global Rule Mapping is not available to Import");
                }

                if (validationResult != null) {
                    LOG.debug("\n\n====>> Importing Validation Data");
                    LOG.debug("Reading Json for Validation Data");
                    if (validationResult.has("validation")) {
                        LOG.debug("******************** Importing Validation Data Started ********************");
                        idApp = saveValidationData(validationResult.getJSONObject("validation"), templateIds, userId, userName);
                        ListApplications listApplications = iValidationCheckDAO.getdatafromlistapplications(idApp);
                        ListDataSource listDataSource = listDataSourceDAO.getDataFromListDataSourcesOfIdData(listApplications.getIdData());
                        response.put("newValidationName", listApplications.getName());
                        response.put("newValidationId", listApplications.getIdApp());
                        response.put("newTemplateId", listDataSource.getIdData());
                        response.put("newTemplateName", listDataSource.getName());

                        LOG.debug("******************** Importing Validation Data Completed ********************");
                    }

                } else {
                    LOG.debug("Validation is not available to Import");
                }

            } else {
                LOG.debug("Validation is not available to Import");
                throw new Exception("Validation is not available to Import");
            }
            response.put("status", "success");
            response.put("message", "Validation imported successfully");

        } catch (Exception e) {
//            e.printStackTrace();
            LOG.error(e.getMessage());

            // Revert Logic
            deleteImportedIncompleteData(templateIds, ruleMappingIds, idApp);

            response.put("status", "failed");
            response.put("message", e.getMessage());
        }
        return response;
    }

    private Map<Long, Long> saveTemplateData(JSONObject templateData, List<ListDataSchema> connectionDetails, Map<Long, Long> schemaMap, int userId, String userName) throws Exception {
        Map<Long, Long> templateIdsMap = new HashMap<>();
        Map<Long, String> templateNamesMap = new HashMap<>();
        JSONArray templateArray = templateData.getJSONArray("template");

        // Save Normal Template
        for (int i = 0; i < templateArray.length(); i++) {
            JSONObject template = templateArray.getJSONObject(i);
            JSONObject reqListDataSource = template.getJSONObject("listDataSource");
            if (reqListDataSource.optString("dataSource").equalsIgnoreCase("Derived")) {
                continue;
            }

            Long idDataSchemaKey = reqListDataSource.optLong("idDataSchema");
            if (!schemaMap.containsKey(idDataSchemaKey)) {
                throw new Exception("Connection details not found to import template");
            }
            Long idDataSchema = schemaMap.get(idDataSchemaKey);

            LOG.debug("\n=====> Saving listDataSource");
            String templateName = "ImpExp_" + reqListDataSource.getString("name");
            reqListDataSource.put("name", templateName);
            Long idData = databuckImportExportDao.saveListDataSource(reqListDataSource, idDataSchema, userId, userName, projectId, domainId);
            LOG.debug("idData ::" + idData);
            templateIdsMap.put(reqListDataSource.getLong("idData"), idData);
            templateNamesMap.put(reqListDataSource.getLong("idData"), templateName);

            if (idData != null) {

                JSONArray listDataAccess = template.getJSONArray("listDataAccess");
                for (int temp = 0; temp < listDataAccess.length(); temp++) {
                    LOG.debug("\n=====> Saving listDataAccess");
                    Long listDataAccessId = databuckImportExportDao.saveListDataAccess(listDataAccess.getJSONObject(temp), idDataSchema, userId, idData);
                    LOG.debug("listDataAccessId ::" + listDataAccessId);
                }
                JSONObject dataDefination = template.getJSONObject("dataDefination");
                JSONArray listDataDefinitions = dataDefination.optJSONArray("listDataDefinitions");
                if (listDataDefinitions != null && listDataDefinitions.length() > 0) {
                    LOG.debug("\n=====> Saving listDataDefinitions");
                    LOG.debug("length :: " + listDataDefinitions.length());
                    databuckImportExportDao.saveListDataDefination(listDataDefinitions, idData, false);
                    LOG.debug("\n=====> Saving StaginglistDataDefinitions");
                    LOG.debug("length :: " + listDataDefinitions.length());
                    databuckImportExportDao.saveListDataDefination(listDataDefinitions, idData, true);
                }
            } else {
                throw new Exception("Failed to save template.");
            }
        }

        // Save Derived Template
        for (int i = 0; i < templateArray.length(); i++) {
            JSONObject template = templateArray.getJSONObject(i);
            JSONObject reqListDataSource = template.getJSONObject("listDataSource");
            if (!reqListDataSource.optString("dataSource").equalsIgnoreCase("Derived")) {
                continue;
            }

            Long idDataSchema = -3L;
            JSONObject listDerivedDataSource = template.getJSONObject("listDerivedDataSource");

            LOG.debug("\n=====> Saving listDataSource");
            reqListDataSource.put("name", "ImpExp_" + reqListDataSource.getString("name"));
            Long idData = databuckImportExportDao.saveListDataSource(reqListDataSource, idDataSchema, userId, userName, projectId, domainId);
            LOG.debug("idData ::" + idData);
            templateIdsMap.put(reqListDataSource.getLong("idData"), idData);

            if (idData != null) {

                if(listDerivedDataSource !=null){
                    listDerivedDataSource.remove("createdAt");
                    Gson gson = new Gson();
                    ListDerivedDataSource derivedDataSource = gson.fromJson(listDerivedDataSource.toString(), ListDerivedDataSource.class);
                    derivedDataSource.setIdData(idData);
                    derivedDataSource.setCreatedBy(userId);
                    derivedDataSource.setCreatedByUser(userName);
                    derivedDataSource.setName("ImpExp_"+derivedDataSource.getName());

                    if(derivedDataSource.getTemplate1IdData()>0){
                        derivedDataSource.setTemplate1Name(templateIdsMap.get(derivedDataSource.getTemplate1IdData()) + "-" + templateNamesMap.get(derivedDataSource.getTemplate1IdData()));
                        derivedDataSource.setTemplate1IdData(templateIdsMap.get(derivedDataSource.getTemplate1IdData()));
                    }
                    if(derivedDataSource.getTemplate2IdData()>0){
                        derivedDataSource.setTemplate2Name(templateIdsMap.get(derivedDataSource.getTemplate2IdData()) + "-" + templateNamesMap.get(derivedDataSource.getTemplate2IdData()));
                        derivedDataSource.setTemplate2IdData(templateIdsMap.get(derivedDataSource.getTemplate2IdData()));
                    }
                    Long idDerivedData = iDataTemplateAddNewDAO.insertIntoListDerivedDataSources(derivedDataSource, projectId);
                }

                JSONObject dataDefination = template.getJSONObject("dataDefination");
                JSONArray listDataDefinitions = dataDefination.optJSONArray("listDataDefinitions");
                if (listDataDefinitions != null && listDataDefinitions.length() > 0) {
                    LOG.debug("Data Defination length :: " + listDataDefinitions.length());
                    LOG.debug("\n=====> Saving listDataDefinitions");
                    databuckImportExportDao.saveListDataDefination(listDataDefinitions, idData, false);
                    LOG.debug("\n=====> Saving StaginglistDataDefinitions");
                    databuckImportExportDao.saveListDataDefination(listDataDefinitions, idData, true);
                }
            } else {
                throw new Exception("Failed to save template.");
            }
        }

        return templateIdsMap;
    }

    private Map<Long, Long> saveTemplateGlobalRuleMappingData(JSONObject templateData, Map<Long, Long> idDataMap, Map<String, Map<Long, Long>> ruleMappingIds) {
        Map<Long, Long> result = new HashMap<>();
        try {
            JSONArray templateArray = templateData.getJSONArray("template");
            for (int i = 0; i < templateArray.length(); i++) {
                JSONObject template = templateArray.getJSONObject(i);
                JSONObject reqListDataSource = template.getJSONObject("listDataSource");
                JSONArray ruleMappingDetailsList = template.optJSONArray("ruleMappingDetailsList");

                Long idData = reqListDataSource.getLong("idData");
                idData = idDataMap.get(idData);
                LOG.debug("New idData ::" + idData);

                if (idData != null) {
                    if (ruleMappingDetailsList!=null && ruleMappingDetailsList.length() > 0) {
                        LOG.debug("=====> Saving rule mapping for " + idData);
                        databuckImportExportDao.saveRuleTemplateMapping(ruleMappingDetailsList, idData, ruleMappingIds.get("globalRule"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, Map<Long, Long>> saveGlobalRuleData(JSONObject globalRuleData, Map<Long, Long> idDataMap, int userId, String userName) {
        Map<String, Map<Long, Long>> result = new HashMap<>();
        try {
            LOG.debug("\n\n====>> Importing Global Rule Data");
            if (globalRuleData.has("synonyms")) {
                Map<Long, Long> newSynonyms = new HashMap<>();
                LOG.debug("\n\n=====> Saving Synonyms");
                JSONArray synonymsList = globalRuleData.getJSONArray("synonyms");
                Map<String, Map<String, String>> domainSynonyms = databuckImportExportDao.getSynonymsForDomain(domainId);
                for (int i = 0; i < synonymsList.length(); i++) {
                    JSONObject synonym = synonymsList.getJSONObject(i);
                    String tableColumn = synonym.getString("tableColumn");
                    String possibleNames = synonym.getString("possibleNames");
                    LOG.debug("\nSynonym Name :: " + tableColumn);

                    if (domainSynonyms.containsKey(tableColumn)) {
                        Map<String, String> syn = domainSynonyms.get(tableColumn);
                        String oldPossibleNames = syn.get("templateColumns");
                        Long synonymId = Long.parseLong(syn.get("synonymId"));
                        LOG.debug("Synonym found with id :: " + synonymId);
                        String fullSyns = String.join(",", oldPossibleNames, possibleNames);
                        Set<String> possibeNamesSet = new LinkedHashSet<>(Arrays.asList(fullSyns.split(",")));
                        databuckImportExportDao.updateSynonym(synonymId, String.join(",", possibeNamesSet));
                    } else {
                        LOG.debug("Adding Synonym");
                        Long synId = databuckImportExportDao.saveSynonym(domainId, tableColumn, possibleNames);
                        newSynonyms.put(synId, synId);
                    }

                }
                result.put("newSynonyms", newSynonyms);

            } else {
                LOG.debug("Synonyms is not available to Import");
            }

            if (globalRuleData.has("globalFilter")) {
                LOG.debug("\n\n=====> Saving Global Filters");
                JSONArray globalFilterList = globalRuleData.getJSONArray("globalFilter");
//                LOG.debug("list :: " + globalFilterList);

                Map<Long, Long> globalFilterIds = new HashMap<>();
                Map<Long, Long> newGlobalFilterIds = new HashMap<>();
                List<GlobalFilters> domainGlobalFilter = globalRuleDAO.getAllGlobalFiltersByDomain(Math.toIntExact(domainId));

                for (int i = 0; i < globalFilterList.length(); i++) {
                    JSONObject globalFilter = globalFilterList.getJSONObject(i);
                    Long filterId = globalFilter.getLong("filterId");
                    String filterName = globalFilter.getString("filterName");
                    String description = globalFilter.getString("description");
                    String filterCondition = globalFilter.getString("filterCondition");

                    List<GlobalFilters> matchedGlobalFilter = domainGlobalFilter.stream()
                            .filter(globalFilters -> globalFilters.getFilterCondition().equals(filterCondition)).collect(Collectors.toList());

                    if (!matchedGlobalFilter.isEmpty()) {
                        GlobalFilters globalFilters = matchedGlobalFilter.get(0);
                        globalFilterIds.put(filterId, globalFilters.getFilterId());
                    } else {
                        LOG.debug("Adding new global filter");
                        GlobalFilters globalFilters = new GlobalFilters();
                        globalFilters.setFilterName("ImpExp_" + filterName);
                        globalFilters.setDescription(description);
                        globalFilters.setFilterCondition(filterCondition);
                        globalFilters.setDomainId(Math.toIntExact(domainId));
                        long globalFilterId = globalRuleDAO.insertIntoGlobalFilters(globalFilters);
                        globalFilterIds.put(filterId, globalFilterId);
                        newGlobalFilterIds.put(globalFilterId, globalFilterId);
                    }
                }
                result.put("globalFilter", globalFilterIds);
                result.put("newGlobalFilter", newGlobalFilterIds);
            } else {
                LOG.debug("Global Filters is not available to Import");
            }

            if (globalRuleData.has("globalRule")) {
                LOG.debug("\n\n=====> Saving Global Rules");
                JSONArray globalRuleList = globalRuleData.getJSONArray("globalRule");
                Map<Long, Long> globalRuleIds = new HashMap<>();
                Map<Long, Long> newGlobalRuleIds = new HashMap<>();

                List<ListColGlobalRules> listColGlobalRulesList = databuckImportExportDao.getGlobalRuleForDomainAndProject(domainId, projectId);
                LOG.debug("\n\nGlobalRule List length :: " + globalRuleList.length());

                for (int i = 0; i < globalRuleList.length(); i++) {

                    JSONObject globalRule = globalRuleList.getJSONObject(i);

                    long ruleId = globalRule.getLong("idListColrules");
                    String ruleName = globalRule.getString("ruleName");
                    String filterCondition = globalRule.optString("filterCondition", "");
                    String rightFilterCondition = globalRule.optString("rightTemplateFilterCondition", "");
                    String matchingRules = globalRule.optString("matchingRules", "");
                    String expression = globalRule.optString("expression", "");
                    String ruleType = globalRule.getString("ruleType");

                    List<ListColGlobalRules> filteredRuleList = listColGlobalRulesList.stream()
                            .filter(globalRules -> globalRules.getRuleType().equals(ruleType)).collect(Collectors.toList());

                    List<ListColGlobalRules> matchedRules = new ArrayList<>();
                    if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
                            || ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)) {
                        // Rule Expression
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getExpression().equals(expression)).collect(Collectors.toList());

                    } else if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
                        // Matching Expression
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getMatchingRules().equals(matchingRules)).collect(Collectors.toList());

                    } else if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
                        // Rule Expression, Matching Expression
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getExpression().equals(expression) &&
                                        listColGlobalRules.getMatchingRules().equals(matchingRules)).collect(Collectors.toList());

                    } else if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
                        // Rule Expression, Matching Expression, Filter, Right Filter
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getExpression().equals(expression) &&
                                        listColGlobalRules.getMatchingRules().equals(matchingRules) &&
                                        listColGlobalRules.getFilterCondition().equals(filterCondition) &&
                                        listColGlobalRules.getRightTemplateFilterCondition().equals(rightFilterCondition)).collect(Collectors.toList());
                    } else if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {
                        // Matching Expression, Filter, Right Filter
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getMatchingRules().equals(matchingRules) &&
                                        listColGlobalRules.getFilterCondition().equals(filterCondition) &&
                                        listColGlobalRules.getRightTemplateFilterCondition().equals(rightFilterCondition)).collect(Collectors.toList());
                    } else if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
                            || ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
                            || ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
                            || ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
                        // Rule Expression, Filter
                        matchedRules = filteredRuleList.stream().filter(listColGlobalRules ->
                                listColGlobalRules.getExpression().equals(expression) &&
                                        listColGlobalRules.getFilterCondition().equals(filterCondition)).collect(Collectors.toList());
                    }

                    if (!matchedRules.isEmpty()) {
                        LOG.debug("GlobalRule Found with same expressions for " + ruleName + " !!");
                        LOG.debug("Matched Rules : " + matchedRules.size());
                        if (matchedRules.size() > 0) {
                            ListColGlobalRules listColGlobalRules = matchedRules.get(0);
                            LOG.debug("GlobalRule Found !!");
                            globalRuleIds.put(ruleId, listColGlobalRules.getIdListColrules());
                        }
                    } else {
                        LOG.debug("Global rule not found with same conditions. Adding new Rule");
                        Map<Long, Long> globalFilterIds = result.get("globalFilter");
                        long filterId = 0;
                        long rightFilterId = 0;

                        if (globalFilterIds != null) {
                            filterId = globalFilterIds.get(globalRule.getLong("filterId")) == null ? 0 : globalFilterIds.get(globalRule.getLong("filterId"));
                            rightFilterId = globalFilterIds.get(globalRule.getLong("rightTemplateFilterId")) == null ? 0 : globalFilterIds.get(globalRule.getLong("rightTemplateFilterId"));
                        }
                        long rightDataId = idDataMap.get(globalRule.getLong("idRightData")) == null ? 0 : idDataMap.get(globalRule.getLong("idRightData"));
                        ListColGlobalRules listColGlobalRules = new ListColGlobalRules();

                        listColGlobalRules.setRuleName("ImpExp_" + globalRule.getString("ruleName"));
                        listColGlobalRules.setDescription(globalRule.getString("description"));
                        listColGlobalRules.setRuleType(globalRule.getString("ruleType"));
                        listColGlobalRules.setExternalDatasetName(globalRule.optString("externalDatasetName"));
                        listColGlobalRules.setIdRightData(rightDataId);
                        listColGlobalRules.setRuleThreshold(globalRule.getDouble("ruleThreshold"));
                        listColGlobalRules.setCreatedByUser(userName);
                        listColGlobalRules.setProjectId(projectId);
                        listColGlobalRules.setDomain_id(Math.toIntExact(domainId));
                        listColGlobalRules.setDimensionId(globalRule.getLong("dimensionId")); // Need to change logic for dimensionId
                        listColGlobalRules.setExpression(globalRule.optString("expression"));
                        listColGlobalRules.setMatchingRules(globalRule.optString("matchingRules"));
                        listColGlobalRules.setFilterId((int) filterId);
                        listColGlobalRules.setRightTemplateFilterId((int) rightFilterId);
                        listColGlobalRules.setAggregateResultsEnabled(globalRule.optString("aggregateResultsEnabled", null));
                        long id = this.globalRuleDAO.insertintolistColRules(listColGlobalRules);
                        globalRuleIds.put(ruleId, id);
                        newGlobalRuleIds.put(id, id);
                    }
                }
                result.put("globalRule", globalRuleIds);
                result.put("newGlobalRule", newGlobalRuleIds);


            } else {
                LOG.debug("Global Rules is not available to Import");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private Long saveValidationData(JSONObject validationData, Map<Long, Long> templateIds, int userId, String userName) throws Exception {
        try {
            JSONObject validationJson = validationData.getJSONObject("listApplication");
            if (validationJson != null) {
                LOG.debug("\n=====> Saving listApplication");
                Gson gson = new Gson();
                ListApplications listApplications = gson.fromJson(validationJson.toString(), ListApplications.class);

                listApplications.setName("ImpExp_" + listApplications.getName().substring(listApplications.getName().indexOf("_") + 1));
                listApplications.setIdApp(0);
                listApplications.setIdData(templateIds.get(listApplications.getIdData()));
                listApplications.setDomainId(domainId);
                listApplications.setProjectId(projectId);

                listApplications.setCreatedBy(String.valueOf(userId));
                listApplications.setUpdatedBy(String.valueOf(userId));
                listApplications.setCreatedByUser(userName);
                listApplications.setApproveBy(userId);
                listApplications.setApproverName(userName);

                Long idApp = databuckImportExportDao.saveListApplication(listApplications);

                LOG.debug("\n=====> Saving listApplication ruleCatalog");
                ruleCatalogService.createRuleCatalog(idApp);
                return idApp;
            }
        } catch (Exception e) {
            throw new Exception("Error occured while saving validation :"+e.getMessage());
        }
        return 0l;
    }

    public JSONObject getConnectionDetailsForImportValidation(String zipFilePath) {
        JSONObject response = new JSONObject();
        String status = "failed";
        String message = "";
        LOG.debug("=====>>>  Inside getConnectionDetailsForImportValidation :: ");
        try {
            LOG.debug("\n\n====>> File Path :: " + zipFilePath);

            JSONObject connectionResult = null;
            JSONObject validationResult = null;
            JSONObject templateResult = null;
            JSONObject globalRuleResult = null;

            if(!zipFilePath.endsWith(".zip")){
                throw new Exception("Invalid file to import. Please provide correct file to import validation.");
            }

            LOG.debug("\n\n====>> Reading Data");
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                List<ZipEntry> zipEntryList = zipFile.stream()
                        .filter(zipEntry -> !zipEntry.isDirectory() && zipEntry.getName().endsWith(".json"))
                        .collect(Collectors.toList());
                if (!zipEntryList.isEmpty()) {
                    for (ZipEntry entry : zipEntryList) {

                        // Read the contents of the JSON file
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8));
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        reader.close();

                        // Parse the JSON content
                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        if (entry.getName().endsWith("connectionResult.json")) {
                            LOG.debug("Reading data for Connection");
                            connectionResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("validationResult.json")) {
                            LOG.debug("Reading data for Validation");
                            validationResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("templateResult.json")) {
                            LOG.debug("Reading data for Template");
                            templateResult = new JSONObject(jsonContent.toString());
                        } else if (entry.getName().endsWith("globalRuleResult.json")) {
                            globalRuleResult = new JSONObject(jsonContent.toString());
                        }
                    }
                }

            } catch (JSONException ex){
                throw new Exception("Invalid data to import. Please provide correct file to import validation.");
            }  catch (Exception e) {
                throw new Exception(e.getMessage());
            }

            if(connectionResult==null || validationResult==null || templateResult==null || globalRuleResult==null){
                throw new Exception("Incomplete data to import. Please provide correct file to import validation.");
            }

            JSONArray result = new JSONArray();
            if (connectionResult != null) {
                if (connectionResult.has("connection") && connectionResult.getJSONArray("connection").length() > 0) {
                    JSONArray connectionsList = connectionResult.getJSONArray("connection");
                    for (int i = 0; i < connectionsList.length(); i++) {
                        JSONObject conn = connectionsList.getJSONObject(i);
                        JSONObject connDetails = new JSONObject();
                        connDetails.put("oldConnectionId", conn.getLong("idDataSchema"));
                        connDetails.put("oldConnectionType", conn.getString("schemaType"));
                        connDetails.put("oldConnectionName", conn.getString("schemaName"));
                        result.put(connDetails);
                    }
                    response.put("connectionDetails", result);
                } else {
                    throw new Exception("Connection Data is not available in file.");
                }
            }
            status = "success";
            message = "Connection details fetched successfully";

        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        response.put("status", status);
        response.put("message", message);
        return response;
    }

    private void deleteImportedIncompleteData(Map<Long, Long> templateIds, Map<String, Map<Long, Long>> ruleMappingIds, Long idApp) {
        try {
            LOG.debug("=====> Deleting incomplete Data :: ");

            if (!ruleMappingIds.isEmpty()) {
//                Commented code to delete newly added synonyms.
//                if (ruleMappingIds.containsKey("newSynonyms") && !ruleMappingIds.get("newSynonyms").isEmpty()) {
//                    LOG.debug("Deleting Synonyms Data :: "+ruleMappingIds.get("newSynonyms").values());
//                    String synonymIdsToDelete = StringUtils.join(ruleMappingIds.get("newSynonyms").values(), ",");
//                    databuckImportExportDao.deleteSynonyms(synonymIdsToDelete);
//                }
                if (ruleMappingIds.containsKey("newGlobalFilter") && !ruleMappingIds.get("newGlobalFilter").isEmpty()) {
                    LOG.debug("Deleting Global Filter Data :"+ruleMappingIds.get("newGlobalFilter").values());
                    String filterIdsToDelete = StringUtils.join(ruleMappingIds.get("newGlobalFilter").values(), ",");
                    databuckImportExportDao.deleteGlobalFiltersDetails(filterIdsToDelete);
                }
                if (ruleMappingIds.containsKey("newGlobalRule") && !ruleMappingIds.get("newGlobalRule").isEmpty()) {
                    LOG.debug("Deleting Global Rule Data :"+ruleMappingIds.get("newGlobalRule").values());
                    String ruleIdsToDelete = StringUtils.join(ruleMappingIds.get("newGlobalRule").values(), ",");
                    databuckImportExportDao.deleteGlobalRuleDetails(ruleIdsToDelete);
                }
            }

            if (!templateIds.isEmpty()) {
                String templateIdsToDelete = StringUtils.join(templateIds.values(), ",");
                LOG.debug("Deleting Validation Data");
                databuckImportExportDao.deleteValidationDetails(templateIdsToDelete);
                LOG.debug("Deleting Template Data");
                databuckImportExportDao.deleteTemplateDetails(templateIdsToDelete);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
