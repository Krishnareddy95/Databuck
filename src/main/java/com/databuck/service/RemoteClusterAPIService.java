package com.databuck.service;

import com.databuck.bean.ListDataSchema;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import com.databuck.util.ConnectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import javax.net.ssl.SSLContext;

import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;


@Service
public class RemoteClusterAPIService {

    @Autowired
    private ConnectionUtil connectionUtil;
    

    @Autowired
    private ITaskDAO iTaskDAO;
    
    private static final Logger LOG = Logger.getLogger(RemoteClusterAPIService.class);

    public ResponseEntity<String> getCDPResponsesForPostAPIs(String URI, String token,
                                                             String inputJson){
        //System.out.println("Inside getCDPResponsesForPostAPIs");
        LOG.debug("\n====>URI:"+URI);
        ResponseEntity<String> out=null;
        try {
            // Invoking the API
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
//            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.add("token", token);

            // set my entity
            HttpEntity<Object> entity = new HttpEntity<Object>(inputJson, headers);

            out = restTemplate.exchange(URI, HttpMethod.POST, entity, String.class);

            String responseJson = out.getBody();

        }catch (ResourceAccessException re){
            LOG.error("\n====>Proxy Server is not Accessible for the URI:"+URI);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public String generateRemoteClusterAPIToken(String clusterCategory){
        String URI=  connectionUtil.getRemoteClusterUrlByClusterCategory(clusterCategory, DatabuckConstants.TOKEN_GENERATE_API);
        return generateToken(URI);
    }

    public String generateRemoteClusterAPIToken(long idDataSchema){
        String URI= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.TOKEN_GENERATE_API);
        return generateToken(URI);
    }

    public String generateToken(String URI){
    //    LOG.debug("Inside generateClusterAPIToken");

        String token=null;
        try {
            token = UUID.randomUUID().toString();//add timestamp in UTC format
            String currentTimeInMillis= ""+System.currentTimeMillis();
            token= currentTimeInMillis+","+token;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

   /* public String generateToken(String URI){

        LOG.debug("Inside generateRemoteClusterAPIToken");
//        String URI="https://140.238.249.1:8085/cdp/generateAPIToken";

        String token=null;
        ResponseEntity<String> out=null;

        try {
            // Invoking the API
            RestTemplate restTemplate = new RestTemplate();

            out = restTemplate.getForEntity(URI, String.class);

            if (out!=null && out.getStatusCode() == HttpStatus.OK) {
                LOG.debug("\n====> TestConnection api:[" + URI + "] triggering is successful !!");

                String responseJson = out.getBody();
                LOG.debug("responseJson="+responseJson);

                JSONObject outObj= new JSONObject(responseJson);
                token = outObj.getString("token");

            } else {
                LOG.debug("\n====> TestConnection api: [" + URI + "] triggering is failed !!");
            }

            LOG.debug("token="+token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    } */

    public boolean testConnectionByRemoteCluster(String URI, String token,
                                                     String inputJson) {
      //  LOG.debug("Inside testRemoteClusterConnection");
        boolean status=false;
        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);

        if (out!=null && out.getStatusCode() == HttpStatus.OK) {

            LOG.debug("\n====> TestConnection api:[" + URI + "] triggering is successful !!");

//            status = true;
            String responseJson = out.getBody();

            JSONObject responseObj= new JSONObject(responseJson);
            String logTrace = responseObj.getString("logTrace");
            if(logTrace.toLowerCase().contains("passed")||logTrace.toLowerCase().contains("success"))
                status=true;

            LOG.info("\n====>Log trace from REST API start...\n");
            LOG.debug(logTrace);
            LOG.info("\n====>Log trace from REST API end...");

        } else {
            LOG.debug("\n====> TestConnection api: [" + URI + "] triggering is failed !!");
        }

        return status;
    }

    public long runTaskByRemoteCluster(String URI, String token,
                                            String inputJson) {
        LOG.info("Inside runTaskByRemoteCluster");
        long pid=-1l;
        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);

        if (out!=null && out.getStatusCode() == HttpStatus.OK) {
            LOG.info("\n====> RunCDPTask api:[" + URI + "] triggering is successful !!");
            String responseJson = out.getBody();
            JSONObject responseObj= new JSONObject(responseJson);
            pid = responseObj.getLong("pid");
        } else {
            LOG.info("\n====> RunCDPTask api: [" + URI + "] triggering is failed !!");
        }
        return pid;
    }

    public String killTaskByRemoteCluster(String URI, String token,
                                     String inputJson){
        LOG.info("Inside killTaskByRemoteCluster");
        String terminalLog="";
        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);
        if (out!=null && out.getStatusCode() == HttpStatus.OK) {
            LOG.info("\n====> CDPKillTask api:[" + URI + "] triggering is successful !!");
            String responseJson = out.getBody();
            JSONObject responseObj= new JSONObject(responseJson);
            terminalLog = responseObj.getString("logTrace");
        } else {
            LOG.info("\n====> CDPKillTask api: [" + URI + "] triggering is failed !!");
        }
        return terminalLog;
    }

    public List<String> getListOfTableNamesByRemoteCluster(ListDataSchema listDataSchema, String token){
        LOG.info("Inside getListOfTableNamesByRemoteCluster");
        List<String> tablesList= new ArrayList<>();

        String hostURI= listDataSchema.getIpAddress();
        String database = listDataSchema.getDatabaseSchema();
        String port= listDataSchema.getPort();
        String domainName = listDataSchema.getDomainName();

        long idDataSchema = listDataSchema.getIdDataSchema();

//        String URI="https://140.238.249.1:8085/cdp/getTablesList";
        String URI= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.GET_TABLESLIST_API);

        JSONObject inputObj= new JSONObject();

        inputObj.put("domainName",domainName);
        inputObj.put("uri",hostURI);
        inputObj.put("port",port);
        inputObj.put("database",database);

        String inputJson= inputObj.toString();
        LOG.debug("inputJson="+inputJson);

        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);

        if (out!=null && out.getStatusCode() == HttpStatus.OK) {

            LOG.info("\n====> GetTablesList api:[" + URI + "] triggering is successful !!");
            String responseJson = out.getBody();

            JSONObject responseObj= new JSONObject(responseJson);

            LOG.debug("responseObj:"+responseObj);
            JSONArray tablesListArr = responseObj.getJSONArray("tablesList");
            LOG.debug("tablesListArr:"+tablesListArr);

            if(tablesListArr!=null && tablesListArr.length() > 0){

                for(int i=0;i<tablesListArr.length();i++){
                    LOG.debug(tablesListArr.getString(i));
                    tablesList.add(""+tablesListArr.getString(i));
                }
            }
        } else {
            LOG.error("\n====> GetTablesList api: [" + URI + "] triggering is failed !!");
        }
        return tablesList;
    }


    public Map<String, String> getTableMetaDataByRemoteCluster(String hostURI, String database, String userlogin,
                                                               String password, String port, String domainName, String tableName, String queryString,String token,long idDataSchema){
        Map<String, String> tableMetadataMap = null;

        LOG.info("Inside getListOfTableNamesFromCDPMapRHive");

//        String URI="https://140.238.249.1:8085/cdp/getTableMetadata";
        String URI= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.GET_METADATA_API);

        JSONObject inputObj= new JSONObject();

        inputObj.put("domainName",domainName);
        inputObj.put("uri",hostURI);
        inputObj.put("port",port);
        inputObj.put("database",database);
        inputObj.put("tableName",tableName);
        inputObj.put("query",queryString);

        String inputJson= inputObj.toString();
        LOG.debug("inputJson="+inputJson);

        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);

        if (out!=null && out.getStatusCode() == HttpStatus.OK) {

            LOG.info("\n====> GetTablesList api:[" + URI + "] triggering is successful !!");
            String responseJson = out.getBody();

            JSONObject responseObj= new JSONObject(responseJson);

            LOG.debug("responseObj:"+responseObj);
            JSONObject tableMetadataObj= responseObj.getJSONObject("tableMetadata");
            LOG.debug("tableMetadata:"+tableMetadataObj);

            if(tableMetadataObj!=null && tableMetadataObj.length() > 0){
                try{
                    tableMetadataMap = new ObjectMapper().readValue(tableMetadataObj.toString(), Map.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            LOG.error("\n====> GetTablesList api: [" + URI + "] triggering is failed !!");
        }

        return tableMetadataMap;
    }

    public List<String> getProfileData(String hostURI, String database, String userlogin, String password, String port, String domainName, String queryString,String token,long idDataSchema){
        List<String> tableMetadataMap = null;

        LOG.info("Inside getProfileData");

//        String URI="https://140.238.249.1:8686/databuck/proxy/getProfileData";
        String URI= connectionUtil.getRemoteClusterUrlByIdDataSchema(idDataSchema,DatabuckConstants.GET_PROFILEDATA_API);

        JSONObject inputObj= new JSONObject();

        inputObj.put("domainName",domainName);
        inputObj.put("uri",hostURI);
        inputObj.put("port",port);
        inputObj.put("database",database);
        inputObj.put("query",queryString);

        String inputJson= inputObj.toString();
        LOG.debug("inputJson="+inputJson);

        ResponseEntity<String> out = getCDPResponsesForPostAPIs(URI,token,inputJson);

        if (out!=null && out.getStatusCode() == HttpStatus.OK) {

            LOG.info("\n====> getProfileData api:[" + URI + "] triggering is successful !!");
            String responseJson = out.getBody();

            JSONObject responseObj= new JSONObject(responseJson);

            LOG.debug("responseObj:"+responseObj);
            JSONArray profileData= responseObj.getJSONArray("profileData");
            LOG.debug("profileData:"+profileData);

            if(profileData!=null && profileData.length() > 0){
                try{
                    tableMetadataMap = new ObjectMapper().readValue(profileData.toString(), List.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            LOG.error("\n====> getProfileData api: [" + URI + "] triggering is failed !!");
        }

        return tableMetadataMap;
    }

}
