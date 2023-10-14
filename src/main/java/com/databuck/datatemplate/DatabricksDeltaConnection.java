package com.databuck.datatemplate;

import com.databuck.bean.DbkFMHistory;
import com.databuck.bean.ListDataSchema;
import com.databuck.filemonitoring.DatabricksFileMonitoringService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

@Component
public class DatabricksDeltaConnection {

    private static final Logger LOG = Logger.getLogger(DatabricksDeltaConnection.class);

    public List<String> getListOfTableNamesFromDatabricksDeltaLake(String uri, String username, String password,
                                                          String database, String port, String httpPath) {
        List<String> tableNameData = new ArrayList<String>();
        try {

            Class.forName("com.databricks.client.jdbc.Driver");
            String url = "jdbc:databricks://" + uri + ":" + port + ";" + "HttpPath=" + httpPath;
            Properties properties = new java.util.Properties();

            properties.put("username", username);
            properties.put("PWD", password);

            Connection Connection = DriverManager.getConnection(url, properties);

            LOG.info("\n====>Connected sucessfully...");

            Statement statement = Connection.createStatement();
            String sql = "SHOW TABLES FROM " + database;

            LOG.info("\n====>sql:" + sql);
            ResultSet resultset = statement.executeQuery(sql);

            while (resultset.next()) {
                tableNameData.add(resultset.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableNameData;
    }


    public LinkedHashMap<String, String> readTablesFromDatabricksDeltaLake(String uri, String port, String database,
                                                                    String username, String password, String tableName,String httpPath) {

        LinkedHashMap<String, String> tableData = new LinkedHashMap<String, String>();
        try {

            Class.forName("com.databricks.client.jdbc.Driver");
            String url = "jdbc:databricks://" + uri + ":" + port + ";" + "HttpPath=" + httpPath;

            Properties properties = new java.util.Properties();

            properties.put("username", username);
            properties.put("PWD", password);

            Connection connection = DriverManager.getConnection(url, properties);

            LOG.info("\n====>Connected sucessfully ...");

            String metadataQuery = "select * from " + database + "."+ tableName + "";
            LOG.info("\n====>metadataQuery:"+metadataQuery);

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(metadataQuery);

            ResultSetMetaData rsmd = rs.getMetaData();
            int NumOfCol = rsmd.getColumnCount();

            for (int i = 1; i <= NumOfCol; i++) {
                LOG.info("\n====>Column Name : Data Type =>" + rsmd.getColumnName(i) + ":" + rsmd.getColumnTypeName(i));
                tableData.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableData;
    }

    // This method will return databricks tables list when database name is given
    public List<String> getDatabricksTablesList(JdbcTemplate jdbcTemplate, String databaseName){
        List<String> tablesList= new ArrayList<>();
        try{
            String sql = "SHOW TABLES FROM " + databaseName;
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
            while (sqlRowSet.next()){
                String tableName= sqlRowSet.getString("tableName");
                tablesList.add(tableName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tablesList;
    }


    // This method will return jdbc template object for single data connection
    public JdbcTemplate getJdbcTemplateBySchema(ListDataSchema listDataSchema){

        JdbcTemplate jdbcTemplate= null;

        try{
            String userName = listDataSchema.getUsername();
            String raw_token = listDataSchema.getPassword(); // decrypt this
            StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
            decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
            String token = decryptor.decrypt(raw_token);

            String dataBase = listDataSchema.getDatabaseSchema();
            String hostName = listDataSchema.getIpAddress();
            String port = ""+ listDataSchema.getPort();
            String httpPath = listDataSchema.getHttpPath();

            String url = "jdbc:databricks://" + hostName + ":" + port + ";" + "HttpPath=" + httpPath;

            Properties p = new java.util.Properties();

            p.put("username", userName);
            p.put("PWD", token);

            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.databricks.client.jdbc.Driver");
            dataSource.setUrl(url);

            dataSource.setUsername(userName);
            dataSource.setPassword(token);

            jdbcTemplate= new JdbcTemplate(dataSource);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jdbcTemplate;
    }


    // This method will give you details of table if its eligible for monitoring
    public DbkFMHistory getDatabricksTableDetailsByName(JdbcTemplate jdbcTemplate,String databaseName ,String tableName, String lastTimeStamp){
        DbkFMHistory dbkFMHistory= null;
        Timestamp ts1 = null;
        if(!lastTimeStamp.trim().isEmpty())
            ts1 = Timestamp.valueOf(lastTimeStamp);
        try{
            String sql = "DESCRIBE HISTORY " + databaseName + "." + tableName + " limit 1";
            LOG.info("\n====>sql:" + sql);

            SqlRowSet sqlRowSet = null;
            try {
                sqlRowSet = jdbcTemplate.queryForRowSet(sql);
            }catch (Exception e){
                System.out.println("Error: History description is not available for ["+tableName+"]");
            }

            if(sqlRowSet!=null){
                boolean eligible=false;
                String timestamp = "";
                int recordCount=0;

                while (sqlRowSet.next()) {
                    timestamp = sqlRowSet.getString("timestamp");

                    try{
                        String operationMetrics= ""+sqlRowSet.getObject("operationMetrics");

                        JSONObject operationObj= new JSONObject(operationMetrics);
                        recordCount= Integer.parseInt(operationObj.getString("numOutputRows"));
                    }catch (Exception e){
                        LOG.error("Error :"+e.getLocalizedMessage());
                    }

                    Timestamp ts2 = Timestamp.valueOf(timestamp);

                    if(ts2.compareTo(ts1) > 0) {
                        LOG.info("\n====>Table :" + tableName + " is eligible for monitoring");
                        eligible= true;
                        break;
                    }
                }
                if(eligible) {

                    dbkFMHistory= new DbkFMHistory();
                    dbkFMHistory.setConnection_type("DatabricksDeltaLake");
                    dbkFMHistory.setTable_or_subfolder_name(tableName);
                    dbkFMHistory.setLast_load_time(timestamp);
                    dbkFMHistory.setLast_altered(timestamp);
                    dbkFMHistory.setFile_name(tableName);
                    dbkFMHistory.setRecord_count(0);
                    dbkFMHistory.setCurrentLoadTime(OffsetDateTime.now(ZoneOffset.UTC));
                    dbkFMHistory.setFolderPath(tableName);
                    dbkFMHistory.setRecord_count(recordCount);
                    dbkFMHistory.setSchema_name(databaseName);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return dbkFMHistory;
    }

}
