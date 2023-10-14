package com.databuck.filemonitoring;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMHistory;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.impl.ValidationDAOImpl;
import com.databuck.service.CreateValidationService;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.util.DatabuckUtility;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class FMService {
        @Autowired
        private FileMonitorDao fileMonitorDao;

        @Autowired
        DataProfilingTemplateService dataProfilingTemplateService;

        @Autowired
        ValidationDAOImpl validationDAO;

        @Autowired
        SchemaDAOI schemaDAOI;

        @Autowired
        ITaskDAO iTaskDAO;

        @Autowired
        private CreateValidationService createValidationService;

        @Autowired
        private Properties appDbConnectionProperties;

        @Autowired
        private Properties clusterProperties;

        public long executeFileMonitoring(DbkFMHistory loadToFMhistory) {
            String account_name = loadToFMhistory.getAccount_name();
            String container_name = loadToFMhistory.getContainer_name();
            long file_arrival_id= 0l;

            System.out.println("\n====> Inside FMService: Processing File Arrival Info...");

            try {

                boolean validationIdStatus = false;

                Long connection_Id = loadToFMhistory.getConnection_id();
                Long validation_Id = loadToFMhistory.getValidation_id();
                String connection_type = loadToFMhistory.getConnection_type();
                String schema_name = loadToFMhistory.getSchema_name();
                String table_or_subfolder_name = loadToFMhistory.getTable_or_subfolder_name();
                String dayOfWeek = loadToFMhistory.getCurrentLoadTime().getDayOfWeek().toString().substring(0,1)+loadToFMhistory.getCurrentLoadTime().getDayOfWeek().toString().substring(1).toLowerCase();
                Integer hour_of_day = loadToFMhistory.getCurrentLoadTime().getHour();
                String load_dateS = FileMonitoringUtilService.getUtcDate(loadToFMhistory.getCurrentLoadTime());
                String fileFullPath = loadToFMhistory.getFolderPath();
                Integer record_count = loadToFMhistory.getRecord_count();

                if(connection_Id<=0 || validation_Id<=0 || schema_name==null || schema_name.trim().isEmpty()
                    || table_or_subfolder_name==null || table_or_subfolder_name.trim().isEmpty()
                    || dayOfWeek==null || dayOfWeek.trim().isEmpty())
                    return 0l;

                int loaded_time = loadToFMhistory.getCurrentLoadTime().getMinute();
                int loaded_hour = loadToFMhistory.getCurrentLoadTime().getHour();

                if(loadToFMhistory.getLast_load_time()!=null){

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                    String dateInString = loadToFMhistory.getLast_load_time();
                    Date date = formatter.parse(dateInString);

                    OffsetDateTime offsetDateTime= OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                    loaded_time = offsetDateTime.getMinute();
                    loaded_hour = offsetDateTime.getHour();
                }

                String record_count_check= null;
                String column_metadata_check = null;
                String file_validity_status = null;


                //get conditional/expected information for summary table
                if (record_count > 0)
                    record_count_check = "passed";
                else
                    record_count_check = "failed";
                /* @@@ change below line after testing is done   */

                if (record_count_check.equalsIgnoreCase("passed"))
                    file_validity_status = "passed";
                else
                    file_validity_status = "failed";

                //save data to dbkFMLoadHistoryTable
                fileMonitorDao.saveDbkFMLoadHistory(loadToFMhistory);

                DBKFileMonitoringRules dbk_file_monitor_rules = new DBKFileMonitoringRules();
                dbk_file_monitor_rules.setConnectionId(connection_Id);
                dbk_file_monitor_rules.setValidationId(validation_Id);
                dbk_file_monitor_rules.setSchemaName(schema_name);
                dbk_file_monitor_rules.setTableName(table_or_subfolder_name);
                dbk_file_monitor_rules.setDayOfWeek(dayOfWeek);

                System.out.println("\n====> Details from API trigger:");
                System.out.println("====> connection_Id:" + connection_Id);
                System.out.println("====> validation_Id:" + validation_Id);
                System.out.println("====> schema_name:" + schema_name);
                System.out.println("====> table_or_subfolder_name:" + table_or_subfolder_name);
                System.out.println("====> dayOfWeek:" + dayOfWeek);
                System.out.println("====> loaded_hour="+loaded_hour);
                System.out.println("====> loaded_time="+loaded_time);

                String file_indicator = null;
                String file_arrival_status = null;
                Integer expected_time = null;
                Integer expected_hour = null;
                Integer start_hour = null;
                Integer end_hour = null;

                List<DBKFileMonitoringRules> rulesList = fileMonitorDao.getRulesListForFile(dbk_file_monitor_rules);

                if (rulesList != null && rulesList.size() > 0) {

                    DBKFileMonitoringRules rulesDetail = rulesList.get(0);

                    file_indicator = rulesDetail.getFileIndicator();
                    expected_hour = rulesDetail.getHourOfDay();
                    expected_time = rulesDetail.getExpectedTime();
                    start_hour = rulesDetail.getStartHour();
                    end_hour = rulesDetail.getEndHour();

                    System.out.println("\n====> Rule found [First rule]:");
                    System.out.println("====> file_indicator:" + file_indicator);
                    System.out.println("====> expected_hour:" + expected_hour);
                    System.out.println("====> expected_time:" + expected_time);
                    System.out.println("====> start_hour:" + start_hour);
                    System.out.println("====> end_hour:" + end_hour);

                    if (file_indicator.equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_HOURLY)) {

                        DBKFileMonitoringRules matchedHourlyRule = null;

                        for(DBKFileMonitoringRules fmRule : rulesList) {
                            if(fmRule.getFileIndicator().equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_HOURLY)) {
                                Integer fm_expected_hour = fmRule.getHourOfDay();

                                if(expected_hour == loaded_hour) {
                                    matchedHourlyRule =fmRule;
                                    break;
                                }
                                else {
                                    if(matchedHourlyRule == null) {
                                        matchedHourlyRule = fmRule;
                                    } else {
                                        // matched hour
                                        Integer matched_hour = matchedHourlyRule.getHourOfDay();

                                        // matched hour = 8  expected_hour=7 loaddedhour = 5
                                        // 8-5= 3   7-5 = 2 pick 7

                                        if( Math.abs(matched_hour - loaded_hour) > Math.abs(fm_expected_hour - loaded_hour))
                                            matchedHourlyRule=fmRule;
                                    }
                                }
                            }
                        }

                        if(matchedHourlyRule != null) {
                            expected_hour = matchedHourlyRule.getHourOfDay();
                            expected_time = matchedHourlyRule.getExpectedTime();

                            // Get the expected count
                            int expectedCount = matchedHourlyRule.getExpectedFileCount();

                            // Get the arrival count from the arrival table
                            int actualArrivalcount = fileMonitorDao.getArrivalCountOfFile(dbk_file_monitor_rules, load_dateS, loaded_hour);

                            if((actualArrivalcount + 1) > expectedCount) {
                                file_indicator = DatabuckConstants.FILE_INDICATOR_HOURLY;
                                file_arrival_status = DatabuckConstants.FILE_ARRIVAL_ADDITIONAL;

                            } else {
                                if (expected_time == loaded_time)
                                    file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_ON_TIME;
                                else if (expected_time > loaded_time)
                                    file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_EARLY;
                                else
                                    file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_DELAYED;
                            }

                        }
                    } else if (file_indicator.equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_FRQUENCY)) {

                        expected_hour = end_hour;

                        if (loaded_hour >= start_hour && loaded_hour <= end_hour)
                            file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_ON_TIME;
                        else if (loaded_hour < start_hour)
                            file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_EARLY;
                        else if (loaded_hour > end_hour)
                            file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_DELAYED;
                    }
                } else {
                    file_indicator = DatabuckConstants.FILE_INDICATOR_HOURLY;
                    file_arrival_status = DatabuckConstants.FILE_ARRIVAL_NEW_FILE;
                }
                DbkFMFileArrivalDetails dbkFMFileArrivalDetails = new DbkFMFileArrivalDetails();
                dbkFMFileArrivalDetails.setConnection_id(connection_Id);
                dbkFMFileArrivalDetails.setValidation_id(validation_Id);
                dbkFMFileArrivalDetails.setSchema_name(schema_name);
                dbkFMFileArrivalDetails.setTable_or_subfolder_name(table_or_subfolder_name);
                dbkFMFileArrivalDetails.setFile_indicator(file_indicator);
                dbkFMFileArrivalDetails.setDayOfWeek(dayOfWeek);
                dbkFMFileArrivalDetails.setLoad_date(load_dateS);
                dbkFMFileArrivalDetails.setLoaded_hour(loaded_hour);
                dbkFMFileArrivalDetails.setLoaded_time(loaded_time);
                dbkFMFileArrivalDetails.setExpected_hour(expected_hour);
                dbkFMFileArrivalDetails.setExpected_time(expected_time);
                dbkFMFileArrivalDetails.setRecord_count(record_count);
                dbkFMFileArrivalDetails.setRecord_count_check(record_count_check);
                dbkFMFileArrivalDetails.setColumn_metadata_check(column_metadata_check);
                dbkFMFileArrivalDetails.setFile_validity_status(file_validity_status);
                dbkFMFileArrivalDetails.setFile_arrival_status(file_arrival_status);
                dbkFMFileArrivalDetails.setFileName(fileFullPath);


                //save data to dbk_fm_summary_details table
                file_arrival_id= fileMonitorDao.saveDbkFMFileArrivalDetails(dbkFMFileArrivalDetails);

                if (file_validity_status.equalsIgnoreCase("passed")) {

                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("\n====> FMService - END <====");
            return file_arrival_id;
        }

    public JSONObject processDatabricksFMTemplate(DbkFMHistory loadToFMhistory){

        String status= "";
        String message="";

        Long connection_Id = loadToFMhistory.getConnection_id();
        String folderPath = loadToFMhistory.getFolderPath();
        String table_or_subfolder_name = loadToFMhistory.getTable_or_subfolder_name();

        try{
            Long templateId = fileMonitorDao.getTemplateIdOfConnectionByTableName(connection_Id, folderPath);

            System.out.println("\n====> templateId for " + folderPath + " table is =====> " + templateId);
            if (templateId == null || templateId == 0l) {
                //create a new template for this Table
                System.out.println("\n====> creating new template for " + folderPath + " table");

                Map<String, Object> listDataSchemaDeatils = fileMonitorDao.getListDataSchemaDeatilsForidData(connection_Id);

                String schemaType = (String)listDataSchemaDeatils.get("schemaType");
                Long createdBy=0l;

                // Query compatibility changes for both POSTGRES and MYSQL
                if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
                    createdBy = Long.valueOf((Integer)listDataSchemaDeatils.get("createdBy"));
                else
                    createdBy  = (Long)listDataSchemaDeatils.get("createdBy");


                Long projectId = Long.parseLong(listDataSchemaDeatils.get("project_id").toString());
                String createdByUser = (String)listDataSchemaDeatils.get("createdByUser");

                System.out.println("connection_Id ==> "+connection_Id+" schemaType => "+schemaType +"  createdBy ==> "+createdBy+" projectId => "+projectId +"  createdByUser ==> "+createdByUser+
                        " schemaType  ==> "+schemaType+" folderPath ===> "+folderPath);


                CompletableFuture<Long> createDataTemplate = dataProfilingTemplateService.createDataTemplate(null, connection_Id, schemaType, folderPath,
                        table_or_subfolder_name+"_template", "", "", "N", "", "", "N", "",
                        null, "", "", "", "", createdBy, "", "", "", "",
                        "", "", null, "", "", "Y", null,projectId, "Y",
                        createdByUser, "N", null, "", "",
                        "");
                Long idData = 0l;
                try {
                    idData = createDataTemplate.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("\n====> template Id  for " + table_or_subfolder_name + " table is ====> " + templateId);
                // Place the templates in Queue
                String uniqueId = "";
                try {
                    uniqueId = dataProfilingTemplateService.triggerDataTemplate(idData, schemaType, "Y",
                            "Y").get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
                        + " is in queue for execution !!");
            } else {

                runValidation(loadToFMhistory,templateId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject statusObj= new JSONObject();
        statusObj.put("status",status);
        statusObj.put("message",message);
        return statusObj;
    }

        public JSONObject processAzureFMTemplate(DbkFMHistory loadToFMhistory){

            String status= "";
            String message="";

            Long connection_Id = loadToFMhistory.getConnection_id();
            String folderPath = loadToFMhistory.getFolderPath();
            String table_or_subfolder_name = loadToFMhistory.getTable_or_subfolder_name();

            try{
                Long templateId = fileMonitorDao.getTemplateIdOfConnectionByTableName(connection_Id, folderPath);

                System.out.println("\n====> templateId for " + folderPath + " table is =====> " + templateId);
                if (templateId == null || templateId == 0l) {
                    //create a new template for this Table
                    System.out.println("\n====> creating new template for " + folderPath + " table");

                    Map<String, Object> listDataSchemaDeatils = fileMonitorDao.getListDataSchemaDeatilsForidData(connection_Id);

                    String schemaType = (String)listDataSchemaDeatils.get("schemaType");
                    Long createdBy=0l;

                    // Query compatibility changes for both POSTGRES and MYSQL
                    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
                         createdBy = Long.valueOf((Integer)listDataSchemaDeatils.get("createdBy"));
                    else
                         createdBy  = (Long)listDataSchemaDeatils.get("createdBy");


                    Long projectId = Long.parseLong(listDataSchemaDeatils.get("project_id").toString());
                    String createdByUser = (String)listDataSchemaDeatils.get("createdByUser");

                    System.out.println("connection_Id ==> "+connection_Id+" schemaType => "+schemaType +"  createdBy ==> "+createdBy+" projectId => "+projectId +"  createdByUser ==> "+createdByUser+
                            " schemaType  ==> "+schemaType+" folderPath ===> "+folderPath);


                    CompletableFuture<Long> createDataTemplate = dataProfilingTemplateService.createDataTemplate(null, connection_Id, schemaType, folderPath,
                            table_or_subfolder_name+"_template", "", "", "N", "", "", "N", "",
                            null, "", "", "", "", createdBy, "", "", "", "",
                            "", "", null, "", "", "Y", null,projectId, "Y",
                            createdByUser, "N", null, "", "",
                            "");
                    Long idData = 0l;
                    try {
                        idData = createDataTemplate.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\n====> template Id  for " + table_or_subfolder_name + " table is ====> " + templateId);
                    // Place the templates in Queue
                    String uniqueId = "";
                    try {
                        uniqueId = dataProfilingTemplateService.triggerDataTemplate(idData, schemaType, "Y",
                                "Y").get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
                            + " is in queue for execution !!");
                } else {
                    runValidation(loadToFMhistory,templateId);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            JSONObject statusObj= new JSONObject();
            statusObj.put("status",status);
            statusObj.put("message",message);
            return statusObj;
        }

        public JSONObject processS3FMTemplate(DbkFMHistory loadToFMhistory){

            String status= "";
            String message="";

            Long connection_Id = loadToFMhistory.getConnection_id();
            String fullPath = loadToFMhistory.getFolderPath();
            String table_or_subfolder_name = loadToFMhistory.getTable_or_subfolder_name();
            table_or_subfolder_name = table_or_subfolder_name.replaceAll("//","/");
            String templateName = table_or_subfolder_name.replaceAll("/", "_")+"_template";

            try{
                Long templateId = fileMonitorDao.getTemplateIdOfConnectionByTableName(connection_Id, table_or_subfolder_name);

                System.out.println("\n====> templateId for " + table_or_subfolder_name + " table is =====> " + templateId);
                if (templateId == null || templateId == 0l) {
                    //create a new template for this Table
                    System.out.println("\n====> creating new template for " + table_or_subfolder_name + " table");

                    Map<String, Object> listDataSchemaDeatils = fileMonitorDao.getListDataSchemaDeatilsForidData(connection_Id);

                    String schemaType = (String)listDataSchemaDeatils.get("schemaType");
                    Long createdBy = Long.parseLong(listDataSchemaDeatils.get("createdBy").toString());
                    Long projectId = Long.parseLong(listDataSchemaDeatils.get("project_id").toString());
                    String createdByUser = (String)listDataSchemaDeatils.get("createdByUser");

                    System.out.println("connection_Id ==> "+connection_Id+" schemaType => "+schemaType +"  createdBy ==> "+createdBy+" projectId => "+projectId +"  createdByUser ==> "+createdByUser+
                            " schemaType  ==> "+schemaType+" filePath ===> "+fullPath);


                    CompletableFuture<Long> createDataTemplate = dataProfilingTemplateService.createDataTemplate(null, connection_Id, schemaType, table_or_subfolder_name,
                    		templateName, "", "", "N", "", "", "N", "",
                            null, "", "", "", "", createdBy, "", "", "", "",
                            "", "", null, "", "", "Y", null,projectId, "Y",
                            createdByUser, "N", null, "", "",
                            "");
                    Long idData = 0l;
                    try {
                        idData = createDataTemplate.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\n====> template Id  for " + table_or_subfolder_name + " table is ====> " + templateId);
                    // Place the templates in Queue
                    String uniqueId = "";
                    try {
                        uniqueId = dataProfilingTemplateService.triggerDataTemplate(idData, schemaType, "Y",
                                "Y").get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
                            + " is in queue for execution !!");
                } else {

                    String validation_uniqueId = null;
                    String incrementalFileName= fullPath;
                    System.out.println("incrementalFileName:"+incrementalFileName);
                    ListDataSource listDataSource = fileMonitorDao.getDataSourceForRowAdd(templateId);
                    int domainId = listDataSource.getDomain();
                    String domainName = iTaskDAO.getDomainNameById(Long.valueOf(""+domainId));

                    Long validationId = fileMonitorDao.getIncrementalDQApplicationsForIdData(templateId);

                    if (validationId != null && validationId > 0l) {
                        //Call script to detect schema changes

                        System.out.println("\n====> validationId Id for templateId " + templateId + " is ====> " + validationId);
                        System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                        String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                        System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                        processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                    } else {
                        System.out.println("\n====>  Incremental validation does not exist");

                        String validationCheckName = listDataSource.getName() + "_validation";
                        int projectId = listDataSource.getProjectId();

                        List<ListDataDefinition> listDataDefinitions = validationDAO.getListDataDefinitionsByIdData(templateId);

                        //check for incremental validation is exist or not ,if not create that one ,if not create bulk load validation
                        validationId = createValidationService.createIncrementalValidationCheck(validationCheckName, templateId, listDataSource,
                                listDataDefinitions, Integer.valueOf(projectId).toString(), (long)(domainId), false);



                        //if  incremental validation create successfully then put that  in queue
                        if (validationId != null && validationId > 0l) {
                            //Call script to detect schema changes

                            System.out.println("\n====> validation id ===> " + validationId);
                            System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                            String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                            System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                            processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);
                        } else {
                            validationId = fileMonitorDao.getDQApplicationsForIdData(templateId);
                            if (validationId != null && validationId > 0l) {
                                //Call script to detect schema changes

                                System.out.println("\n====> validation id ===> " + validationId);
                                System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                                String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                                System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                                processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                            } else {
                                System.out.println("\n====> creating normal validation for template id ==>  " + templateId);
                                validationId = createValidationService.createValidationCheck(validationCheckName, templateId, listDataSource,
                                        listDataDefinitions, projectId, domainId, false);
                                if (validationId != null && validationId > 0l) {
                                    //Call script to detect schema changes

                                    System.out.println("\n====> validation id ===> " + validationId);
                                    System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                                    String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                                    System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                                    processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                                }
                            }

                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            JSONObject statusObj= new JSONObject();
            statusObj.put("status",status);
            statusObj.put("message",message);
            return statusObj;
        }


        private boolean processShellScript(long idApp, long file_arrival_id, String uniqueId){

            String databuckHome = DatabuckUtility.getDatabuckHome();
            String scriptLocation = databuckHome + "/scripts/runSchemaDetection.sh";
            // Preparing command and arguments
            List<String> commandList = new ArrayList<String>();
            String cmd = "";
            Process process = null;

            long pid = -1;

            try{
                commandList.add(scriptLocation);
                commandList.add("" + idApp);
                commandList.add("" + file_arrival_id);
                commandList.add("" + uniqueId);

                cmd = scriptLocation + "  " + idApp + " "+ file_arrival_id + " "+uniqueId;

                System.out.println("\n**** Command : " + cmd);

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(commandList);
                process = processBuilder.start();

                try {
                    if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
                        Field f = process.getClass().getDeclaredField("pid");
                        f.setAccessible(true);
                        pid = f.getLong(process);
                        f.setAccessible(false);
                    }
                } catch (Exception e) {
                    System.out.println("\n====>Exception occurred failed to get the process Id of current job !!");
                    pid = -1;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("\n====>Process Id: " + pid);
            if(pid > -1)
                return true;
            return false;
        }

        public String runValidation(DbkFMHistory loadToFMhistory,long templateId){

            String statusStr="";
            try {
                ListDataSource listDataSource = fileMonitorDao.getDataSourceForRowAdd(templateId);
                int domainId = listDataSource.getDomain();
                String domainName = iTaskDAO.getDomainNameById(Long.valueOf("" + domainId));
                System.out.println("Domain Name:" + domainName);

                String incrementalFileName = loadToFMhistory.getFile_name();
                System.out.println("incrementalFileName:" + incrementalFileName);

                Long validationId = fileMonitorDao.getIncrementalDQApplicationsForIdData(templateId);

                if (validationId != null && validationId > 0l) {
                    System.out.println("\n====> validationId Id for templateId " + templateId + " is ====> " + validationId);
                    //Call script to detect schema changes

                    System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                    String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                    System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                    processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                } else {
                    System.out.println("\n====>  Incremental validation does not exist");

                    String validationCheckName = listDataSource.getName() + "_validation";
                    int projectId = listDataSource.getProjectId();

                    validationId = null;
                    List<ListDataDefinition> listDataDefinitions = validationDAO.getListDataDefinitionsByIdData(templateId);

                    String lastReadColName = appDbConnectionProperties.getProperty("incremental.lastread.column.name");
                    String incrementalValCreationEn = appDbConnectionProperties.getProperty("auto.incremental.validation.creation.enabled");

                    if (lastReadColName != null && !lastReadColName.trim().isEmpty() &&
                            incrementalValCreationEn != null && incrementalValCreationEn.trim().equalsIgnoreCase("Y")) {

                        //check for incremental validation is exist or not ,if not create that one ,if not create bulk load validation
                        validationId = createValidationService.createIncrementalValidationCheck(validationCheckName, templateId, listDataSource,
                                listDataDefinitions, Integer.valueOf(projectId).toString(), (long) (domainId), false);

                        if (validationId != null && validationId > 0l) {
                            //Call script to detect schema changes

                            System.out.println("\n====> Incremental validation id ===> " + validationId);
                            System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                            String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                            System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                            processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);
                        }
                    }

                    //if  incremental validation create successfully then put that  in queue
                    if (validationId == null) {
                        System.out.println("\n====> Incremental validation id is null");
                        validationId = fileMonitorDao.getDQApplicationsForIdData(templateId);
                        if (validationId != null && validationId > 0l) {
                            System.out.println("\n====> DQ validation id ===> " + validationId);
                            //Call script to detect schema changes

                            System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                            String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                            System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                            processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                        } else {
                            System.out.println("\n====> creating normal validation for template id ==>  " + templateId);
                            validationId = createValidationService.createValidationCheck(validationCheckName, templateId, listDataSource,
                                    listDataDefinitions, projectId, domainId, false);
                            if (validationId != null && validationId > 0l) {
                                System.out.println("\n====> validation id ===> " + validationId);
                                //Call script to detect schema changes

                                System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
                                String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, incrementalFileName);
                                System.out.println("\n====> validation_uniqueId in queue is " + uniqueId);
                                processShellScript(validationId, loadToFMhistory.getFile_arrival_id(), uniqueId);

                            }
                        }

                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return statusStr;
        }

}
