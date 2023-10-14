package com.databuck.service;

import com.databuck.bean.DBKFileMonitoringRules;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.apache.log4j.Logger;

@Service
public class DBKFileMonitoringService {
	
	private static final Logger LOG = Logger.getLogger(DBKFileMonitoringService.class);

    public List<DBKFileMonitoringRules> submitFileMonitoringCSVForSnowFlake(File monitorFile, String fileName, long idApp){

        // Create List for holding FileMonitorRules objects
        List<DBKFileMonitoringRules> dbkFileMonitoringRulesList = new ArrayList<>();

        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String headers= br.readLine();
            // Delimiters used in the CSV file
            final String COMMA_DELIMITER = ",";
            String line = "";

            while ((line = br.readLine()) != null) {

                String[] fileDetails = line.split(COMMA_DELIMITER);
                int lineSize= fileDetails.length;

                LOG.debug("\n====>Size of columns in a line:"+lineSize);

                if (fileDetails.length > 0) {

                    DBKFileMonitoringRules dbkFileMonitoringRules = new DBKFileMonitoringRules();

                    try{
                        dbkFileMonitoringRules.setValidationId(idApp);

                        if(lineSize > 0)
                            dbkFileMonitoringRules.setSchemaName(fileDetails[0]);
                        else
                            dbkFileMonitoringRules.setSchemaName("");

                        if(lineSize > 1)
                            dbkFileMonitoringRules.setTableName(fileDetails[1]);
                        else
                            dbkFileMonitoringRules.setTableName("");

                        if(lineSize > 2)
                            dbkFileMonitoringRules.setFileIndicator(fileDetails[2]);
                        else
                            dbkFileMonitoringRules.setFileIndicator("");

                        if(lineSize > 3)
                            dbkFileMonitoringRules.setDayOfWeek(fileDetails[3]);
                        else
                            dbkFileMonitoringRules.setDayOfWeek("");

                        if(lineSize > 4){
                            try{
                                dbkFileMonitoringRules.setHourOfDay(Integer.parseInt(fileDetails[4]));
                            }catch (Exception e){
                                LOG.error("Hour of Day not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setHourOfDay(null);
                            }
                        }else
                            dbkFileMonitoringRules.setHourOfDay(null);

                        if(lineSize > 5){
                            try{
                                dbkFileMonitoringRules.setExpectedTime(Integer.parseInt(fileDetails[5]));
                            }catch (Exception e){
                                LOG.error("Expected Time not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setExpectedTime(null);
                            }
                        }else
                            dbkFileMonitoringRules.setExpectedTime(null);

                        if(lineSize > 6) {
                            try{
                                dbkFileMonitoringRules.setExpectedFileCount(Integer.parseInt(fileDetails[6]));
                            }catch (Exception e){
                                LOG.error("Expected File Count not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setExpectedFileCount(null);
                            }
                        }else
                            dbkFileMonitoringRules.setExpectedFileCount(null);

                        if(lineSize > 7){
                            try{
                                dbkFileMonitoringRules.setStartHour(Integer.parseInt(fileDetails[7]));
                            }catch (Exception e){
                                LOG.error("Start Hour not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setStartHour(null);
                            }
                        }else
                            dbkFileMonitoringRules.setStartHour(null);

                        if(lineSize > 8){
                            try{
                                dbkFileMonitoringRules.setEndHour(Integer.parseInt(fileDetails[8]));
                            }catch (Exception e){
                                LOG.error("End Hour not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setEndHour(null);
                            }
                        }else
                            dbkFileMonitoringRules.setEndHour(null);

                        if(lineSize > 9){
                            try{
                                dbkFileMonitoringRules.setFrequency(Integer.parseInt(fileDetails[9]));
                            }catch (Exception e){
                                LOG.error("Frequency not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setFrequency(null);
                            }
                        }else
                            dbkFileMonitoringRules.setFrequency(null);


                        dbkFileMonitoringRulesList.add(dbkFileMonitoringRules);

                    }catch (Exception e){
                    	LOG.error(e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
        }catch (Exception e){
        	LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return dbkFileMonitoringRulesList;
    }

    public List<DBKFileMonitoringRules> submitFileMonitoringCSVForSnowFlake(MultipartFile monitorFile, String fileName, long idApp){

   
        // Create List for holding FileMonitorRules objects
        List<DBKFileMonitoringRules> dbkFileMonitoringRulesList = new ArrayList<>();

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(monitorFile.getInputStream(), "UTF-8"));
            String headers= br.readLine();
            // Delimiters used in the CSV file
            final String COMMA_DELIMITER = ",";
            String line = "";

            while ((line = br.readLine()) != null) {

                String[] fileDetails = line.split(COMMA_DELIMITER);
                int lineSize= fileDetails.length;

                LOG.debug("\n====>Size of columns in a line:"+lineSize);

                if (fileDetails.length > 0) {

                    DBKFileMonitoringRules dbkFileMonitoringRules = new DBKFileMonitoringRules();

                    try{
                    	 dbkFileMonitoringRules.setId(-1);
                        dbkFileMonitoringRules.setValidationId(idApp);

                        if(lineSize > 0)
                            dbkFileMonitoringRules.setSchemaName(fileDetails[0]);
                        else
                            dbkFileMonitoringRules.setSchemaName("");

                        if(lineSize > 1)
                            dbkFileMonitoringRules.setTableName(fileDetails[1]);
                        else
                            dbkFileMonitoringRules.setTableName("");

                        if(lineSize > 2)
                            dbkFileMonitoringRules.setFileIndicator(fileDetails[2]);
                        else
                            dbkFileMonitoringRules.setFileIndicator("");

                        if(lineSize > 3)
                            dbkFileMonitoringRules.setDayOfWeek(fileDetails[3]);
                        else
                            dbkFileMonitoringRules.setDayOfWeek("");

                        if(lineSize > 4){
                            try{
                                dbkFileMonitoringRules.setHourOfDay(Integer.parseInt(fileDetails[4]));
                            }catch (Exception e){
                                LOG.error("Hour of Day not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setHourOfDay(0);
                            }
                        }else
                            dbkFileMonitoringRules.setHourOfDay(0);

                        if(lineSize > 5){
                            try{
                                dbkFileMonitoringRules.setExpectedTime(Integer.parseInt(fileDetails[5]));
                            }catch (Exception e){
                                LOG.error("Expected Time not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setExpectedTime(0);
                            }
                        }else
                            dbkFileMonitoringRules.setExpectedTime(0);

                        if(lineSize > 6) {
                            try{
                                dbkFileMonitoringRules.setExpectedFileCount(Integer.parseInt(fileDetails[6]));
                            }catch (Exception e){
                                LOG.error("Expected File Count not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setExpectedFileCount(0);
                            }
                        }else
                            dbkFileMonitoringRules.setExpectedFileCount(0);

                        if(lineSize > 7){
                            try{
                                dbkFileMonitoringRules.setStartHour(Integer.parseInt(fileDetails[7]));
                            }catch (Exception e){
                                LOG.error("Start Hour not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setStartHour(0);
                            }
                        }else
                            dbkFileMonitoringRules.setStartHour(0);

                        if(lineSize > 8){
                            try{
                                dbkFileMonitoringRules.setEndHour(Integer.parseInt(fileDetails[8]));
                            }catch (Exception e){
                                LOG.error("End Hour not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setEndHour(0);
                            }
                        }else
                            dbkFileMonitoringRules.setEndHour(0);

                        if(lineSize > 9){
                            try{
                                dbkFileMonitoringRules.setFrequency(Integer.parseInt(fileDetails[9]));
                            }catch (Exception e){
                                LOG.error("Frequency not in Integer format. Keeping it empty");
                                LOG.error(e.getMessage());
                                dbkFileMonitoringRules.setFrequency(0);
                            }
                        }else
                            dbkFileMonitoringRules.setFrequency(0);
                        
                        LOG.debug("data --"+dbkFileMonitoringRules.getTableName());


                        dbkFileMonitoringRulesList.add(dbkFileMonitoringRules);

                    }catch (Exception e){
                        e.printStackTrace();
                        LOG.error("exceptions "+e.getMessage());
                    }

                }

            }
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("exceptions "+e.getMessage());
        }
        return dbkFileMonitoringRulesList;
    }

    

}
