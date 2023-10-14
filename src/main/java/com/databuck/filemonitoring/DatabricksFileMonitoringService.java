package com.databuck.filemonitoring;

import com.databuck.bean.DbkFMHistory;
import com.databuck.bean.ListDataSchema;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.SchemaDAOI;
import com.databuck.datatemplate.DatabricksDeltaConnection;
import com.databuck.util.DateUtility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class DatabricksFileMonitoringService {

    @Autowired
    private SchemaDAOI schemaDAO;

    @Autowired
    private FMService fmService;

    @Autowired
    private FileMonitorDao fileMonitorDao;

    @Autowired
    private DatabricksDeltaConnection databricksDeltaConnection;

    private static final Logger LOG = Logger.getLogger(DatabricksFileMonitoringService.class);

    // Method to perform databricks file monitoring
    public String performDatabricksMonitoring(String lastTimeStamp){
        LOG.info("\n====>Inside performDatabricksMonitoring with last timestamp:"+lastTimeStamp);

        Timestamp ts1 = null;
        if(!lastTimeStamp.trim().isEmpty())
            ts1 = Timestamp.valueOf(lastTimeStamp);

        String currentTimeStamp ="";

        if(!lastTimeStamp.trim().isEmpty()) {
            try {
                List<ListDataSchema> databricksMonitoringSchemas = schemaDAO.readDatabricksFileMonitoringListDataSchema();

                if (databricksMonitoringSchemas.size() == 0)
                    LOG.info("\n====>File Monitoring Connections does not exits");

                //Iterate on every file monitoring connection and fetch table updates
                for (ListDataSchema listDataSchema : databricksMonitoringSchemas) {
                    JdbcTemplate jdbcTemplate = databricksDeltaConnection.getJdbcTemplateBySchema(listDataSchema);
                    Long connection_Id = listDataSchema.getIdDataSchema();

                    // Fetch tables list
                    if (jdbcTemplate != null) {

                        String databaseName = listDataSchema.getDatabaseSchema();

                        List<String> tablesList= databricksDeltaConnection.getDatabricksTablesList(jdbcTemplate, databaseName);
                        LOG.info("\n====>table list for connection['"+listDataSchema.getIdDataSchema()+"]");
                        LOG.info(tablesList);

                        currentTimeStamp = DateUtility.getCurrentUTCTimeByFormat("yyyy-MM-dd HH:mm:ss.S");

                        if (tablesList != null && tablesList.size() > 0) {
                            //Iterate on each table to perform monitoring

                            for (String tableName : tablesList) {

                                DbkFMHistory dbkFMHistory= databricksDeltaConnection.getDatabricksTableDetailsByName(jdbcTemplate,databaseName ,tableName, lastTimeStamp);

                                if(dbkFMHistory!=null){

                                    Long validation_Id = fileMonitorDao.getFMValidationByIdDataSchema(connection_Id);

                                    LOG.info("\n====>Performing file Monitoring for:"+dbkFMHistory.getTable_or_subfolder_name());

                                    if(validation_Id!=null && validation_Id > 0l){

                                        dbkFMHistory.setConnection_id(connection_Id);
                                        dbkFMHistory.setValidation_id(validation_Id);

                                        long file_arrival_id= fmService.executeFileMonitoring(dbkFMHistory);
                                        dbkFMHistory.setFile_arrival_id(file_arrival_id);
                                        fmService.processDatabricksFMTemplate(dbkFMHistory);
                                    }
                                }

                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(currentTimeStamp.trim().isEmpty()) {
//            System.out.println("Current timestamp is empty");
            currentTimeStamp = DateUtility.getCurrentUTCTimeByFormat("yyyy-MM-dd HH:mm:ss.S");
        }
        return currentTimeStamp;
    }

}
