package com.databuck.dao.impl;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListDataSchema;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DatabuckImportExportDao;
import com.databuck.dao.IDataTemplateAddNewDAO;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class DatabuckImportExportDaoImpl implements DatabuckImportExportDao {

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    IDataTemplateAddNewDAO iDataTemplateAddNewDAO;

    @Autowired
    ValidationCheckDAOImpl validationCheckDAO;

    private static final Logger LOG = Logger.getLogger(DatabuckImportExportDaoImpl.class);

    @Override
    public Map<String, String> getLinkedIds(Long idApp) {
        Map<String, String> result = new HashMap<>();
        try {
            // Query compatibility changes for both POSTGRES and MYSQL
            String sql = "";
            if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
                sql = "SELECT string_agg(DISTINCT r.ruleId::text, ',') AS ruleId, " +
                        " string_agg(DISTINCT CASE WHEN g.idRightData > 0 THEN g.idRightData::text ELSE '' END, ',') AS templateId, " +
                        " string_agg(DISTINCT CASE WHEN g.filterId > 0 THEN g.filterId::text ELSE '' END, ',') AS filterId, " +
                        " string_agg(DISTINCT CASE WHEN g.right_template_filter_id > 0 THEN g.right_template_filter_id::text ELSE '' END, ',') AS rightTemplateFilterId " +
                        " FROM rule_Template_Mapping r " +
                        " INNER JOIN listColGlobalRules g ON g.idListColrules = r.ruleId " +
                        " WHERE r.templateid = (SELECT idData FROM listApplications l WHERE l.idApp = ?)";
            } else {
                sql = "SELECT GROUP_CONCAT(DISTINCT(r.ruleId)) AS ruleId,  " +
                        " GROUP_CONCAT(DISTINCT(if(g.idRightData>0, g.idRightData, ''))) AS templateId,  " +
                        " GROUP_CONCAT(DISTINCT(if(g.filterId>0, g.filterId, ''))) AS filterId,  " +
                        " GROUP_CONCAT(DISTINCT(if(g.right_template_filter_id>0, g.right_template_filter_id, ''))) AS rightTemplateFilterId " +
                        " FROM rule_Template_Mapping r  " +
                        " INNER JOIN listColGlobalRules g ON g.idListColrules=r.ruleId " +
                        " WHERE r.templateid=(SELECT idData FROM listApplications l WHERE l.idApp=?)";
            }
            SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, idApp);
            while (queryForRowSet.next()) {
                result.put("ruleId", queryForRowSet.getString("ruleId"));
                result.put("templateId", queryForRowSet.getString("templateId"));
                result.put("filterId", queryForRowSet.getString("filterId"));
                result.put("rightTemplateFilterId", queryForRowSet.getString("rightTemplateFilterId"));
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Set<Long>> getLinkedIds(Long idData, String connectionIds) {
        Map<String, Set<Long>> result = new HashMap<>();
        Set<Long> ruleList = new HashSet<>();
        Set<Long> templateList = new HashSet<>();
        Set<Long> filterList = new HashSet<>();
        Set<Long> rightTemplateFilterList = new HashSet<>();
        try {
            String sql = "SELECT r.ruleId, gr.idRightData, gr.filterId, gr.right_template_filter_id FROM rule_Template_Mapping r " +
                    "LEFT JOIN listColGlobalRules gr ON gr.idListColrules=r.ruleId " +
                    "LEFT JOIN listDataSources ls ON ls.idData=gr.idRightData " +
                    "WHERE r.templateid=? AND ((gr.idRightData IS NULL OR gr.idRightData<=0) OR (ls.idDataSchema IN (?))) ";
            SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, idData, connectionIds);
            while (queryForRowSet.next()) {
                if (queryForRowSet.getLong("ruleId") > 0) {
                    ruleList.add(queryForRowSet.getLong("ruleId"));
                }
                if (queryForRowSet.getLong("idRightData") > 0) {
                    templateList.add(queryForRowSet.getLong("idRightData"));
                }
                if (queryForRowSet.getLong("filterId") > 0) {
                    filterList.add(queryForRowSet.getLong("filterId"));
                }
                if (queryForRowSet.getLong("right_template_filter_id") > 0) {
                    rightTemplateFilterList.add(queryForRowSet.getLong("right_template_filter_id"));
                }
            }
            result.put("ruleId", ruleList);
            result.put("templateId", templateList);
            result.put("filterId", filterList);
            result.put("rightTemplateFilterId", rightTemplateFilterList);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, JSONArray> getExcludedRuleAndTemplate(Long idData, String connectionIds) {
        Map<String, JSONArray> result = new HashMap<>();
        JSONArray ruleList = new JSONArray();
        JSONArray templateList = new JSONArray();
        try {
            String sql = "SELECT r.ruleName, r.ruleId, ls.name, ls.idData FROM rule_Template_Mapping r " +
                    "LEFT JOIN listColGlobalRules gr ON gr.idListColrules=r.ruleId " +
                    "LEFT JOIN listDataSources ls ON ls.idData=gr.idRightData " +
                    "WHERE r.templateid=? AND gr.idRightData IS NOT NULL AND gr.idRightData>0 AND ls.idDataSchema NOT IN (?) ";
            SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, idData, connectionIds);
            while (queryForRowSet.next()) {
                JSONObject rule = new JSONObject();
                rule.put("ruleId", queryForRowSet.getLong("ruleId"));
                rule.put("ruleName", queryForRowSet.getString("ruleName"));
                ruleList.put(rule);

                JSONObject template = new JSONObject();
                template.put("templateId", queryForRowSet.getLong("idData"));
                template.put("templateName", queryForRowSet.getString("name"));
                templateList.put(template);
            }
            result.put("rule", ruleList);
            result.put("template", templateList);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Map<String, String>> getSynonymsForDomain(long domainId) {
        Map<String, Map<String, String>> synonymnMap = new HashMap<>();

        try {
            String sQry = "select a.synonyms_Id,trim(a.tableColumn) as SynonymName, trim(a.possiblenames) as TemplateColumns "
                    + " from SynonymLibrary a, domain b "
                    + " where a.domain_Id = b.domainId and b.domainId =" + domainId;

            SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sQry);
            while (oSqlRowSet.next()) {
                Map<String, String> map = new HashMap<>();
                map.put("synonymId", oSqlRowSet.getString("synonyms_Id"));
                map.put("templateColumns", oSqlRowSet.getString("TemplateColumns").toString());
                synonymnMap.put(oSqlRowSet.getString("SynonymName").toString(), map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return synonymnMap;
    }

    @Override
    public List<ListColGlobalRules> getGlobalRuleForDomainAndProject(long domainId, long projectId) {
        List<ListColGlobalRules> listColGlobalRulesList = new ArrayList<>();
        try {
            String sQry = "SELECT l.*,g.global_filter_condition as filterCondition,rg.global_filter_condition as rightTemplateFilterCondition,"
                    + " d.domainName, dm.dimensionName FROM listColGlobalRules l "
                    + "join domain d on l.domain_id=d.domainId "
                    + "left join dimension dm on dm.idDimension = l.dimension_id "
                    + "left join global_filters g on l.filterId = g.global_filter_id "
                    + "left join global_filters rg on l.right_template_filter_id = rg.global_filter_id "
                    + "where l.domain_id=" + domainId + " and l.project_id=" + projectId;

            SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sQry);
            while (oSqlRowSet.next()) {
                ListColGlobalRules listColGlobalRules = new ListColGlobalRules();
                listColGlobalRules.setIdListColrules(oSqlRowSet.getLong("idListColrules"));
                listColGlobalRules.setRuleName(oSqlRowSet.getString("ruleName"));
                listColGlobalRules.setDescription(oSqlRowSet.getString("description"));
                listColGlobalRules.setRuleType(oSqlRowSet.getString("ruleType"));
                listColGlobalRules.setExternalDatasetName(oSqlRowSet.getString("externalDatasetName"));
                listColGlobalRules.setIdRightData(oSqlRowSet.getLong("idRightData"));
                listColGlobalRules.setRuleThreshold(oSqlRowSet.getDouble("ruleThreshold"));
                listColGlobalRules.setCreatedByUser(oSqlRowSet.getString("createdByUser"));
                listColGlobalRules.setProjectId(oSqlRowSet.getLong("project_id"));
                listColGlobalRules.setDomain_id(oSqlRowSet.getInt("domain_id"));
                listColGlobalRules.setDimensionId(oSqlRowSet.getLong("dimension_id"));
                listColGlobalRules.setExpression(oSqlRowSet.getString("expression"));
                listColGlobalRules.setMatchingRules(oSqlRowSet.getString("matchingRules"));
                listColGlobalRules.setFilterId(oSqlRowSet.getInt("filterId"));
                listColGlobalRules.setRightTemplateFilterId(oSqlRowSet.getInt("right_template_filter_id"));
                listColGlobalRules.setAggregateResultsEnabled(oSqlRowSet.getString("aggregateResultsEnabled"));
                listColGlobalRules.setFilterCondition(oSqlRowSet.getString("filterCondition"));
                listColGlobalRules.setRightTemplateFilterCondition(oSqlRowSet.getString("rightTemplateFilterCondition"));
                listColGlobalRules.setDomain(oSqlRowSet.getString("domainName"));
                listColGlobalRules.setDomain(oSqlRowSet.getString("dimensionName"));
                listColGlobalRulesList.add(listColGlobalRules);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listColGlobalRulesList;
    }

    @Override
    public Long saveListDataSource(JSONObject listDataSource, long idDataSchema, int userId, String userName, Long projectId, Long domainId) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            final String sql = "insert into listDataSources (name,description,dataLocation,dataSource,createdAt,updatedAt," +
                    "createdBy,updatedBy,idDataSchema,ignoreRowsCount,active,project_id,profilingEnabled,advancedRulesEnabled," +
                    "createdByUser,domain_id,template_create_success,deltaApprovalStatus,subcribed_email_id)"
                    + " VALUES (?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pst_lds = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst_lds.setString(1, listDataSource.optString("name"));
            pst_lds.setString(2, listDataSource.optString("description"));
            pst_lds.setString(3, listDataSource.optString("dataLocation"));
            pst_lds.setString(4, listDataSource.optString("dataSource"));
            pst_lds.setInt(5, userId);
            pst_lds.setInt(6, userId);
            pst_lds.setLong(7, idDataSchema);
            pst_lds.setLong(8, listDataSource.getLong("ignoreRowsCount"));
            pst_lds.setString(9, listDataSource.optString("active"));
            pst_lds.setLong(10, projectId);
            pst_lds.setString(11, listDataSource.optString("profilingEnabled"));
            pst_lds.setString(12, listDataSource.optString("advancedRulesEnabled"));
            pst_lds.setString(13, userName);
            pst_lds.setLong(14, domainId);
            pst_lds.setString(15, listDataSource.optString("templateCreateSuccess"));
            pst_lds.setString(16, listDataSource.optString("deltaApprovalStatus"));
            pst_lds.setString(17, listDataSource.optString("subcribed_email_id"));

            pst_lds.executeUpdate();
            ResultSet generatedKeys_rs = pst_lds.getGeneratedKeys();
            Long listDatasourceId = null;
            if (generatedKeys_rs != null && generatedKeys_rs.next()) {
                listDatasourceId = generatedKeys_rs.getLong(1);
            }

            conDb.close();

            return listDatasourceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long saveListDataAccess(JSONObject listDataAccess, long idDataSchema, int userId, long idData) {
        try {
            ListDataSchema listDataSchema = iDataTemplateAddNewDAO.getListDataSchema(idDataSchema).get(0);

            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            String sql = "insert into listDataAccess("
                    + "idData,hostName,portName,userName,pwd,schemaName,folderName,query,idDataSchema,whereCondition,domain,incrementalType,"
                    + "dateFormat,sliceStart,sliceEnd,queryString,hivejdbchost,hivejdbcport,sslEnb,sslTrustStorePath,trustPassword,"
                    + "gatewayPath,jksPath,zookeeperUrl,fileHeader,metaData,isRawData,rollingHeader,rollingColumn,historicDateTable) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
            String encryptedText = encryptor.encrypt(listDataSchema.getPassword());
            PreparedStatement pst_lda = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst_lda.setLong(1, idData);
            pst_lda.setString(2, listDataSchema.getIpAddress());
            pst_lda.setString(3, listDataSchema.getPort());
            pst_lda.setString(4, listDataSchema.getUsername());
            pst_lda.setString(5, encryptedText);
            pst_lda.setString(6, listDataAccess.getString("schemaName"));
            pst_lda.setString(7, listDataAccess.optString("folderName"));
            pst_lda.setString(8, listDataAccess.optString("query"));
            pst_lda.setLong(9, listDataSchema.getIdDataSchema());
            pst_lda.setString(10, listDataAccess.optString("whereCondition"));
            pst_lda.setString(11, listDataSchema.getDomain());
            pst_lda.setString(12, listDataAccess.optString("incrementalType"));
            pst_lda.setString(13, listDataAccess.optString("dateFormat"));
            pst_lda.setString(14, listDataAccess.optString("sliceStart"));
            pst_lda.setString(15, listDataAccess.optString("sliceEnd"));
            pst_lda.setString(16, listDataAccess.optString("queryString"));
            pst_lda.setString(17, listDataSchema.getHivejdbchost());
            pst_lda.setString(18, listDataSchema.getHivejdbcport());
            pst_lda.setString(19, listDataSchema.getSslEnb());
            pst_lda.setString(20, listDataSchema.getSslTrustStorePath());
            pst_lda.setString(21, listDataSchema.getTrustPassword());
            pst_lda.setString(22, listDataSchema.getGatewayPath());
            pst_lda.setString(23, listDataSchema.getJksPath());
            pst_lda.setString(24, listDataSchema.getZookeeperUrl());
            pst_lda.setString(25, listDataAccess.optString("fileHeader"));
            pst_lda.setString(26, listDataAccess.optString("metaData"));//metaData
            pst_lda.setString(27, listDataAccess.optString("isRawData"));//isRawData
            pst_lda.setString(28, listDataAccess.optString("rollingHeader"));
            pst_lda.setString(29, listDataAccess.optString("rollingColumn"));
            pst_lda.setString(30, listDataAccess.optString("historicDateTable"));
            pst_lda.executeUpdate();

            ResultSet generatedKeys_rs = pst_lda.getGeneratedKeys();
            Long listDatasourceId = null;
            if (generatedKeys_rs != null && generatedKeys_rs.next()) {
                listDatasourceId = generatedKeys_rs.getLong(1);
            }

            conDb.close();

            return listDatasourceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveListDataDefination(JSONArray listDataDefinations, long idData, boolean isStagingTransaction) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();
            String tableName = "listDataDefinition";
            if (isStagingTransaction) {
                tableName = "staging_listDataDefinition";
            }
            for (int temp = 0; temp < listDataDefinations.length(); temp++) {

                JSONObject listDataDefination = listDataDefinations.getJSONObject(temp);

                String sql = "insert into " + tableName + "("
                        + "idData,columnName,displayName,format,hashValue,numericalStat,stringStat,nullCountThreshold," //8
                        + "numericalThreshold,stringStatThreshold,KBE,dgroup,dupkey,measurement,blend,idCol,incrementalCol," //17
                        + "idDataSchema,nonNull,primaryKey,recordAnomaly,recordAnomalyThreshold,dataDrift,dataDriftThreshold," //24
                        + "outOfNormStat,outOfNormStatThreshold,isMasked,partitionBy,lengthCheck,lengthValue,applyrule," // 31
                        + "startDate,timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,dateRule,badData," // 40
                        + "dateFormat,correlationcolumn,lengthCheckThreshold,badDataCheckThreshold,patternCheckThreshold," // 45
                        + "maxLengthCheck,defaultPatternCheck,defaultPatterns) " // 48
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

//                System.out.println(sql);
                PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setLong(1, idData);
                pst.setString(2, listDataDefination.getString("columnName"));
                pst.setString(3, listDataDefination.getString("displayName"));
                pst.setString(4, listDataDefination.getString("format"));
                pst.setString(5, listDataDefination.getString("hashValue"));
                pst.setString(6, listDataDefination.getString("numericalStat"));
                pst.setString(7, listDataDefination.getString("stringStat"));

                pst.setDouble(8, listDataDefination.getDouble("nullCountThreshold"));
                pst.setDouble(9, listDataDefination.getDouble("numericalThreshold"));
                pst.setDouble(10, listDataDefination.getDouble("stringStatThreshold"));

                pst.setString(11, listDataDefination.getString("KBE"));
                pst.setString(12, listDataDefination.getString("dgroup"));
                pst.setString(13, listDataDefination.getString("dupkey"));
                pst.setString(14, listDataDefination.getString("measurement"));
                pst.setString(15, listDataDefination.getString("blend"));
                pst.setInt(16, listDataDefination.getInt("idCol"));
                pst.setString(17, listDataDefination.getString("incrementalCol"));

                pst.setLong(18, listDataDefination.getLong("idDataSchema"));
                pst.setString(19, listDataDefination.getString("nonNull"));
                pst.setString(20, listDataDefination.getString("primaryKey"));
                pst.setString(21, listDataDefination.getString("recordAnomaly"));
                pst.setLong(22, listDataDefination.getLong("recordAnomalyThreshold"));
                pst.setString(23, listDataDefination.getString("dataDrift"));
                pst.setLong(24, listDataDefination.getLong("dataDriftThreshold"));

                pst.setString(25, listDataDefination.getString("outOfNormStat"));
                pst.setLong(26, listDataDefination.getLong("outOfNormStatThreshold"));
                pst.setString(27, listDataDefination.getString("isMasked"));
                pst.setString(28, listDataDefination.getString("partitionBy"));
                pst.setString(29, listDataDefination.getString("lengthCheck"));
                pst.setString(30, listDataDefination.getString("lengthValue"));
                pst.setString(31, listDataDefination.getString("applyrule"));

                pst.setString(32, listDataDefination.getString("startDate"));
                pst.setString(33, listDataDefination.getString("timelinessKey"));
                pst.setString(34, listDataDefination.getString("endDate"));
                pst.setString(35, listDataDefination.getString("defaultCheck"));
                pst.setString(36, listDataDefination.getString("defaultValues"));
                pst.setString(37, listDataDefination.getString("patternCheck"));
                pst.setString(38, listDataDefination.getString("patterns"));
                pst.setString(39, listDataDefination.getString("dateRule"));
                pst.setString(40, listDataDefination.getString("badData"));

                pst.setString(41, listDataDefination.getString("dateFormat"));
                pst.setString(42, listDataDefination.getString("correlationcolumn"));
                pst.setDouble(43, listDataDefination.getDouble("lengthThreshold"));
                pst.setDouble(44, listDataDefination.getDouble("badDataThreshold"));
                pst.setDouble(45, listDataDefination.getDouble("patternCheckThreshold"));

                pst.setString(46, listDataDefination.getString("maxLengthCheck"));
                pst.setString(47, listDataDefination.getString("defaultPatternCheck"));
                pst.setString(48, listDataDefination.getString("defaultPatterns"));

                pst.executeUpdate();
            }
            conDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRuleTemplateMapping(JSONArray ruleMappingDetails, long idData, Map<Long, Long> ruleIds) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();
            for (int temp = 0; temp < ruleMappingDetails.length(); temp++) {
                JSONObject ruleMapping = ruleMappingDetails.getJSONObject(temp);
                String sql = "insert into rule_Template_Mapping (templateid, ruleId, ruleName, ruleExpression, ruleType, "
                        + "matchingRules,filter_condition,right_template_filter_condition,anchorColumns,null_filter_columns) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?)";

                if (ruleIds == null || !ruleIds.keySet().contains(Long.parseLong(ruleMapping.getString("ruleId")))) {
                    continue;
                }
                Long ruleId = ruleIds.get(Long.parseLong(ruleMapping.getString("ruleId")));
                PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setLong(1, idData);
                pst.setLong(2, ruleId);
                pst.setString(3, ruleMapping.getString("ruleName"));
                pst.setString(4, ruleMapping.getString("ruleExpression"));
                pst.setString(5, ruleMapping.getString("ruleType"));
                pst.setString(6, ruleMapping.getString("matchingRules"));
                pst.setString(7, ruleMapping.getString("filterCondition"));
                pst.setString(8, ruleMapping.getString("rightTemplateFilterCondition"));
                pst.setString(9, ruleMapping.getString("anchorColumns"));
                pst.setString(10, ruleMapping.getString("nullFilterColumns"));
                pst.executeUpdate();
            }
            conDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long saveListApplication(ListApplications listApplications) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            String sql = "INSERT INTO listApplications (name,description,appType,idData,idRightData,createdBy," + // 6
                    "updatedBy,fileNameValidation,entityColumn,colOrderValidation,matchingThreshold,nonNullCheck," + // 12
                    "numericalStatCheck,stringStatCheck,recordAnomalyCheck,incrementalMatching,incrementalTimestamp,dataDriftCheck," + // 18
                    "updateFrequency,frequencyDays,recordCountAnomaly,recordCountAnomalyThreshold,timeSeries," + //23
                    "keyGroupRecordCountAnomaly,outOfNormCheck,applyRules,applyDerivedColumns,csvDir,groupEquality," + // 29
                    "groupEqualityThreshold,buildHistoricFingerPrint,historicStartDate,historicEndDate,historicDateFormat," + // 34
                    "active,lengthCheck,correlationcheck,project_id,timelinessKeyCheck,defaultCheck,defaultValues,patternCheck," +// 42
                    "dateRuleCheck,badData,idLeftData,prefix1,prefix2,dGroupNullCheck,dGroupDateRuleCheck,fuzzylogic," + // 50
                    "fileMonitoringType,createdByUser,validityThreshold,dGroupDataDriftCheck,rollTargetSchemaId,thresholdsApplyOption," + // 56
                    "continuousFileMonitoring,rollType,approve_status,approve_comments,approve_date,approve_by,domain_id," + //63
                    "subcribed_email_id,approver_name,data_domain_id,staging_approve_status,maxLengthCheck,defaultPatternCheck," + // 69
                    "reprofiling,validation_job_size,createdAt,updatedAt) " //73
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    "?,?,?,?,?,?,?,?,?,?,?,now(),now())";

            PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

//            name,description,appType,idData,idRightData,createdBy
            pst.setString(1, listApplications.getName());
            pst.setString(2, listApplications.getDescription());
            pst.setString(3, listApplications.getAppType());
            pst.setLong(4, listApplications.getIdData());
            pst.setLong(5, listApplications.getIdRightData());
            pst.setString(6, listApplications.getCreatedBy());

//            updatedBy,fileNameValidation,entityColumn,colOrderValidation,matchingThreshold,nonNullCheck
            pst.setString(7, listApplications.getUpdatedBy());
            pst.setString(8, listApplications.getFileNameValidation());
            pst.setString(9, listApplications.getEntityColumn());
            pst.setString(10, listApplications.getColOrderValidation());
            pst.setDouble(11, listApplications.getMatchingThreshold());
            pst.setString(12, listApplications.getNonNullCheck());

//            numericalStatCheck,stringStatCheck,recordAnomalyCheck,incrementalMatching,incrementalTimestamp,dataDriftCheck
            pst.setString(13, listApplications.getNumericalStatCheck());
            pst.setString(14, listApplications.getStringStatCheck());
            pst.setString(15, listApplications.getRecordAnomalyCheck());
            pst.setString(16, listApplications.getIncrementalMatching());
            pst.setDate(17, (Date) listApplications.getIncrementalTimestamp());
            pst.setString(18, listApplications.getDataDriftCheck());

//            updateFrequency,frequencyDays,recordCountAnomaly,recordCountAnomalyThreshold,timeSeries
            pst.setString(19, listApplications.getUpdateFrequency());
            pst.setInt(20, listApplications.getFrequencyDays());
            pst.setString(21, listApplications.getRecordCountAnomaly());
            pst.setDouble(22, listApplications.getRecordCountAnomalyThreshold());
            pst.setString(23, listApplications.getTimeSeries());

//            keyGroupRecordCountAnomaly,outOfNormCheck,applyRules,applyDerivedColumns,csvDir,groupEquality
            pst.setString(24, listApplications.getKeyGroupRecordCountAnomaly());
            pst.setString(25, listApplications.getOutOfNormCheck());
            pst.setString(26, listApplications.getApplyRules());
            pst.setString(27, listApplications.getApplyDerivedColumns());
            pst.setString(28, listApplications.getCsvDir());
            pst.setString(29, listApplications.getGroupEquality());

//            groupEqualityThreshold,buildHistoricFingerPrint,historicStartDate,historicEndDate,historicDateFormat
            pst.setDouble(30, listApplications.getGroupEqualityThreshold());
            pst.setString(31, listApplications.getBuildHistoricFingerPrint());
            pst.setString(32, listApplications.getHistoricStartDate());
            pst.setString(33, listApplications.getHistoricEndDate());
            pst.setString(34, listApplications.getHistoricDateFormat());

//            active,lengthCheck,correlationcheck,project_id,timelinessKeyCheck,defaultCheck,defaultValues,patternCheck
            pst.setString(35, listApplications.getActive());
            pst.setString(36, listApplications.getlengthCheck());
            pst.setString(37, listApplications.getCorrelationcheck());
            pst.setLong(38, listApplications.getProjectId());
            pst.setString(39, listApplications.getTimelinessKeyChk());
            pst.setString(40, listApplications.getDefaultCheck());
            pst.setString(41, listApplications.getDefaultValues());
            pst.setString(42, listApplications.getPatternCheck());

//            dateRuleCheck,badData,idLeftData,prefix1,prefix2,dGroupNullCheck,dGroupDateRuleCheck,fuzzylogic
            pst.setString(43, listApplications.getDateRuleChk());
            pst.setString(44, listApplications.getBadData());
            pst.setLong(45, listApplications.getIdLeftData());
            pst.setString(46, listApplications.getPrefix1());
            pst.setString(47, listApplications.getPrefix2());
            pst.setString(48, listApplications.getdGroupNullCheck());
            pst.setString(49, listApplications.getdGroupDateRuleCheck());
            pst.setString(50, null);

//            fileMonitoringType,createdByUser,validityThreshold,dGroupDataDriftCheck,rollTargetSchemaId,thresholdsApplyOption
            pst.setString(51, listApplications.getFileMonitoringType());
            pst.setString(52, listApplications.getCreatedByUser());
            pst.setDouble(53, listApplications.getValidityThreshold());
            pst.setString(54, listApplications.getdGroupDataDriftCheck());
            pst.setLong(55, listApplications.getRollTargetSchemaId());
            pst.setInt(56, listApplications.getThresholdsApplyOption());

//            continuousFileMonitoring,rollType,approve_status,approve_comments,approve_date,approve_by,domain_id
            pst.setString(57, listApplications.getContinuousFileMonitoring());
            pst.setString(58, listApplications.getRollType());
            pst.setInt(59, listApplications.getApproveStatus());
            pst.setString(60, listApplications.getApproveComments());
            pst.setString(61, listApplications.getApproveDate());
            pst.setInt(62, listApplications.getApproveBy());
            pst.setLong(63, listApplications.getDomainId());

//            subcribed_email_id,approver_name,data_domain_id,staging_approve_status,maxLengthCheck,defaultPatternCheck
            pst.setString(64, listApplications.getSubcribedEmailId());
            pst.setString(65, listApplications.getApproverName());
            pst.setInt(66, listApplications.getData_domain());
            pst.setInt(67, listApplications.getStagingApproveStatus());
            pst.setString(68, listApplications.getMaxLengthCheck());
            pst.setString(69, listApplications.getDefaultPatternCheck());

//            reprofiling,validation_job_size
            pst.setString(70, listApplications.getReprofiling());
            pst.setString(71, listApplications.getValidationJobSize());

            pst.executeUpdate();

            ResultSet generatedKeys_rs = pst.getGeneratedKeys();
            Long idApp = null;
            if (generatedKeys_rs != null && generatedKeys_rs.next()) {
                idApp = generatedKeys_rs.getLong(1);
            }

            String name = idApp + "_" + listApplications.getName();
            String updateNameSql = "update listApplications set name='" + name + "' where idApp=" + idApp;
            jdbcTemplate.execute(updateNameSql);

            validationCheckDAO.insertintolistdftranrule(idApp, listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
                    listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());

            conDb.close();
            return idApp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0l;
    }

    @Override
    public Long saveSynonym(long domainId, String tableColumn, String possibleNames) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();
            String sql = "insert into SynonymLibrary(domain_Id,tableColumn,possiblenames) VALUES (?,?,?)";
            PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setLong(1, domainId);
            pst.setString(2, tableColumn);
            pst.setString(3, possibleNames);

            pst.executeUpdate();

            ResultSet generatedKeys_rs = pst.getGeneratedKeys();
            Long id = null;
            if (generatedKeys_rs != null && generatedKeys_rs.next()) {
                id = generatedKeys_rs.getLong(1);
            }

            conDb.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public void updateSynonym(long synonymId, String tableColumns) {
        try {
            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            String sql = "update SynonymLibrary set possiblenames=? where synonyms_Id=?";
            PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, tableColumns);
            pst.setLong(2, synonymId);
            pst.executeUpdate();

            conDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long saveTaskStatus(String uniqueId, long exportedApplicationId, long importedApplicationId, String taskType, String filePath,
                               String status, String statusMessage, String errorCode, String errorMessage, long createdBy, String createdByUser, String hash) {
        try {

            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            String sql = "INSERT INTO import_export_audit_logs (unique_id,exported_application_id,imported_application_id,task_type,"
                    + "file_path,start_time,status,status_message,error_code,error_message,created_by,created_by_user,hash) "
                    + "VALUES (?,?,?,?,?,now(),?,?,?,?,?,?,?)";

            PreparedStatement pst = conDb.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, uniqueId);
            pst.setLong(2, exportedApplicationId);
            pst.setLong(3, importedApplicationId);
            pst.setString(4, taskType);
            pst.setString(5, filePath);
            pst.setString(6, status);
            pst.setString(7, statusMessage);
            pst.setString(8, errorCode);
            pst.setString(9, errorMessage);
            pst.setLong(10, createdBy);
            pst.setString(11, createdByUser);
            pst.setString(12, hash);
            pst.executeUpdate();

            ResultSet generatedKeys_rs = pst.getGeneratedKeys();
            Long id = null;
            if (generatedKeys_rs != null && generatedKeys_rs.next()) {
                id = generatedKeys_rs.getLong(1);
            }

            conDb.close();

            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateTaskStatus(long id, String filePath, String status, String statusMessage, String errorCode, String errorMessage, long importedApplicationId) {
        try {

            Connection conDb = jdbcTemplate.getDataSource().getConnection();

            String sql = "update import_export_audit_logs set end_time=now(), file_path=?, status=?,status_message=?," +
                    "error_code=?,error_message=?,imported_application_id=? where id=?";

            PreparedStatement pst = conDb.prepareStatement(sql);
            pst.setString(1, filePath);
            pst.setString(2, status);
            pst.setString(3, statusMessage);
            pst.setString(4, errorCode);
            pst.setString(5, errorMessage);
            pst.setLong(6, importedApplicationId);
            pst.setLong(7, id);
            pst.executeUpdate();
            conDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject getValidationImportByHashCode(String hashCode) {
        JSONObject details = null;
        try {
            String sql = "select t1.*, t2.name from import_export_audit_logs t1 " +
                    " left join listApplications t2 ON t1.imported_application_id=t2.idApp " +
                    " where status='success' and hash='"+hashCode+"' order by id desc limit 1";

            SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sql);
            if (oSqlRowSet.next()) {
                details = new JSONObject();
                details.put("filePath", oSqlRowSet.getString("file_path"));
                details.put("startTime", oSqlRowSet.getString("start_time"));
                details.put("validationName", oSqlRowSet.getString("name"));
                details.put("validationId", oSqlRowSet.getLong("imported_application_id"));
                details.put("createdByUser", oSqlRowSet.getString("created_by_user"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public void deleteValidationDetails(String templateIds) {
        try {
            String sql = "delete from listDFTranRule where idApp in (select idApp from listApplications where idData in ("+templateIds+")) ";
            LOG.debug("Delete  Validation Data: "+sql);
            jdbcTemplate.update(sql);
            sql = "delete from listApplications where idData in ("+templateIds+")";
            LOG.debug("Delete  Validation Data: "+sql);
            jdbcTemplate.update(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTemplateDetails(String templateIds) {
        try {

            String sql = "delete from rule_Template_Mapping where templateid in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

            sql = "delete from staging_listDataDefinition where idData in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

            sql = "delete from listDataDefinition where idData in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

            sql = "delete from listDerivedDataSources where idData in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

            sql = "delete from listDataAccess where idData in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

            sql = "delete from listDataSources where idData in ("+templateIds+")";
            LOG.debug("Delete Template Data: "+sql);
            jdbcTemplate.update(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGlobalRuleDetails(String ruleIds) {
        try {
            String sql = "delete from listColGlobalRules where idListColrules in ("+ruleIds+")";
            LOG.debug("Delete Global Rule Data: "+sql);
            jdbcTemplate.update(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteGlobalFiltersDetails(String filterIds) {
        try {
            String sql = "delete from global_filters where global_filter_id in ("+filterIds+")";
            LOG.debug("Delete Global Filter Data: "+sql);
            jdbcTemplate.update(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteSynonyms(String synonymsIds) {
        try {
            String sql = "delete from SynonymLibrary where synonyms_Id in ("+synonymsIds+")";
            LOG.debug("Delete Synonyms Data: "+sql);
            jdbcTemplate.update(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
