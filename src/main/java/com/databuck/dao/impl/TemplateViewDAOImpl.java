package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;							 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DeleteTempView;
import com.databuck.bean.Dimension;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.TemplateView;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.listDataBlend;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.service.IProjectService;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

@Repository
public class TemplateViewDAOImpl implements ITemplateViewDAO {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplate jdbcTemplate1;

    @Autowired
    private IProjectService IProjectservice;

    private static final Logger LOG = Logger.getLogger(TemplateViewDAOImpl.class);

    public void deleteIdListColRulesData(long idListColrules) {
	String deleteStatement = "DELETE FROM listColRules WHERE idListColrules=?";
	int update = jdbcTemplate.update(deleteStatement, idListColrules);
	LOG.debug("DELETE FROM listColRules=" + update);
    }

    public int insertintolistdatablend(String name, Long sourceid, String description, Long idUser,
	    String createdByUser, Long projectId) {
	String query = "insert into listDataBlend"
		+ "(idData,name,description,createdAt,updatedAt,createdBy,updatedBy,createdByUser,project_id) values(?,?,?,now(),now(),?,?,?,?)";
	int update = jdbcTemplate.update(query,
		new Object[] { sourceid, name, description, idUser, idUser, createdByUser, projectId });
	LOG.debug("update=" + update);
	return update;

    }

    public List<listDataAccess> getDataFromListDataAccess(Long idData) {
	String query = "select hostName,userName,folderName from listDataAccess where idData=" + idData;
	List<listDataAccess> listdataaccess = jdbcTemplate.query(query, new RowMapper<listDataAccess>() {
	    public listDataAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
		listDataAccess lda = new listDataAccess();
		lda.setHostName(rs.getString("hostName"));
		lda.setUserName(rs.getString("userName"));
		lda.setFolderName(rs.getString("folderName"));
		return lda;
	    }
	});
	return listdataaccess;
    }
    
	public List<listDataAccess> getUniqueListDataAccess() {
    	List<listDataAccess> listdataaccess = new ArrayList<listDataAccess>();
	    	String query =  String.format("select hostName,userName,folderName,portName,schemaName from listDataAccess where Query = 'N' group by hostName,userName,folderName,portName,schemaName");
			listdataaccess = jdbcTemplate.query(query, new RowMapper<listDataAccess>() {
	    	    public listDataAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
	    		listDataAccess lda = new listDataAccess();
	    		lda.setHostName(rs.getString("hostName"));
	    		lda.setUserName(rs.getString("userName"));
	    		lda.setFolderName(rs.getString("folderName"));
	    		lda.setPortName(rs.getString("portName"));
	    		lda.setSchemaName(rs.getString("schemaName"));
	    		return lda;
	    	    }
	    	});
    	return listdataaccess;
    }
    
    
	public List<listDataAccess> getMonitoredTableNamesfromListDataAccess(listDataAccess ulda) {
    	List<listDataAccess> masterListdataaccess = new ArrayList<listDataAccess>();
    	//List<listDataAccess> uniqueListDataAccess = getUniqueListDataAccess();
    	
    			try {
			    	String query =  "select hostName,userName,folderName,idData,idDataSchema from listDataAccess where";
			    	if(ulda.getHostName()!=null && !ulda.getHostName().equals("")) {
			    			query = query+ " hostName = '"+ulda.getHostName()+"'";
			    	}
			    	if(ulda.getUserName()!=null && !ulda.getUserName().equals("")) {
			    			query = query+ " AND userName = '"+ulda.getUserName()+"'";
			    	}
			    	if(ulda.getFolderName()!=null && !ulda.getFolderName().equals("")) {
			    			query = query+ " AND folderName = '"+ulda.getFolderName()+"'";
			    	}
			    	if(ulda.getPortName()!=null && !ulda.getPortName().equals("")) {
			    			query = query+ " AND portName = '"+ulda.getPortName()+"'";
			    	}
			    	if(ulda.getSchemaName()!=null && !ulda.getSchemaName().equals("")) {
			    			query = query+ " AND schemaName = '"+ulda.getSchemaName()+"'";
			    	}
			    			query = query+ "AND Query = 'N'";
			    	List<listDataAccess> listdataaccess = jdbcTemplate.query(query, new RowMapper<listDataAccess>() {
			    	    public listDataAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
			    		listDataAccess lda = new listDataAccess();
			    		lda.setHostName(rs.getString("hostName"));
			    		lda.setUserName(rs.getString("userName"));
			    		lda.setFolderName(rs.getString("folderName"));
			    		lda.setIdData(rs.getLong("idData"));
			    		lda.setIdDataSchema(rs.getLong("idDataSchema"));
			    		return lda;
			    	    }
			    	});
			    	masterListdataaccess.addAll(listdataaccess);
    			}catch(Exception ce) {
    				ce.printStackTrace();
    			}
    	
    	return masterListdataaccess;
    }
    
    @SuppressWarnings("deprecation")
    public List<listDataAccess> getMonitoredTableNamesfromidDataList(List<Long> idDataList) {
    	List<listDataAccess> listdataaccess = new ArrayList<listDataAccess>();
    	if(idDataList.size()>0) {
	    	String inSql = String.join(",", Collections.nCopies(idDataList.size(), "?"));
	    	String query =  String.format("select hostName,userName,folderName,idData,idDataSchema from listDataAccess where idData in (%s) AND Query = 'N'",inSql);
			listdataaccess = jdbcTemplate.query(query,idDataList.toArray(), new RowMapper<listDataAccess>() {
	    	    public listDataAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
	    		listDataAccess lda = new listDataAccess();
	    		lda.setHostName(rs.getString("hostName"));
	    		lda.setUserName(rs.getString("userName"));
	    		lda.setFolderName(rs.getString("folderName"));
	    		lda.setIdData(rs.getLong("idData"));
	    		lda.setIdDataSchema(rs.getLong("idDataSchema"));
	    		return lda;
	    	    }
	    	});
    	}
    	return listdataaccess;
    }
    
    public List<ListApplications> getidAppListFromListApplication(Long idData) {
    	List<ListApplications> listdataApplication = new ArrayList<ListApplications>();
    	if(idData!=null && idData>0) {
	    	String query = "select idApp,name,idData from listApplications where idData ="+idData+" AND active = 'yes'";
	    	listdataApplication = jdbcTemplate.query(query, new RowMapper<ListApplications>() {
	    	    public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
					ListApplications application = new ListApplications();
					application.setIdApp(rs.getInt("idApp"));
					application.setName(rs.getString("name"));
					application.setIdData(rs.getLong("idData"));
					return application;
	    	    }
	    	});
    	}
    	return listdataApplication;
    }	
									
    public String getReferenceFilesFromListDataFiles(Long idData) {
	LOG.info("getReferenceFilesFromListDataFiles");
	String sql = "select fileName from listDataFiles where idData=" + idData;
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	List<String> data = new ArrayList<String>();
	StringJoiner sj = new StringJoiner(",");
	while (queryForRowSet.next()) {
	    sj.add(queryForRowSet.getString(1));
	    data.add(queryForRowSet.getString(1));
	}
	return sj.toString();
    }

    // changes done for KAFKA

    public List<ListDataSource> getlistdatasourcesname(Long projectId) {
	String sql = "SELECT name,idData FROM listDataSources where active='yes'and project_id=" + projectId
		+ " order by idData desc";

	List<ListDataSource> listDataSource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

	    @Override
	    public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {

		ListDataSource listDataSource = new ListDataSource();

		listDataSource.setName(rs.getString("name"));
		listDataSource.setIdData(rs.getInt("idData"));

		return listDataSource;
	    }
	});
	return listDataSource;
    }

    // similar to the one above except for the return type. Created new one for
    // Angular to reduce return object size and to avoid unnecessary conversion to
    // reduce the size
    public List<Map<String, Object>> getListSecondDatasources(Long projectId) {
	String sql = "SELECT name,idData FROM listDataSources where active='yes'and project_id=" + projectId
		+ " order by idData desc";

	List<Map<String, Object>> listMap = new ArrayList<>();
	SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

	while (sqlRowSet.next()) {
	    Map<String, Object> dataMap = new HashMap<>();
	    dataMap.put("name", sqlRowSet.getString("name"));
	    dataMap.put("idData", sqlRowSet.getString("idData"));
	    listMap.add(dataMap);
	}

	return listMap;
    }

    public int savedataforfilter(String name, String filteringExp, Long idDataBlend, Long idData, String blendcolumn) {
	String query = "insert into listDataBlendFilterDefinitions" + "(name,idDataBlend,filteringExp) values(?,?,?)";
	int update = jdbcTemplate.update(query, new Object[] { name, idDataBlend, filteringExp });
	LOG.debug("update=" + update);

	String q = "insert into staging_listDataDefinition("
		+ "idData,columnName,displayName,format,blend,KBE,dgroup,dupkey,measurement,"
		+ "idDataSchema,hashValue,numericalStat"
		+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
		+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
		+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int update2 = jdbcTemplate.update(q,
		new Object[] { idData, name, filteringExp, "Double", "Y", "N", "N", "N", "N", Integer.valueOf(0), "N",
			"N", "N", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), "N", "N", "N", "N",
			Double.valueOf(3.0D), "N", Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N", "N", null,
			"N", null, "N", "N", "N", 0 });
	LOG.debug("update2=" + update2);
	return update + update2;
    }

    public void matchingDerivedColumn(listDataBlend ldb) {
	String listDataBlendQuery = "update listDataBlend set derivedColType=? , columnValue=? , columnValueType=? , expression=? ,columnName=? where idDataBlend=? ";
	int update3 = jdbcTemplate.update(listDataBlendQuery, ldb.getDerivedColType(), ldb.getColumnValue(),
		ldb.getColumnValueType(), ldb.getExpression(), ldb.getColumnName(), ldb.getIdDataBlend());
	LOG.debug("update listDataBlend" + update3);

	String query = "insert into listDataBlendColDefinitions" + "(name,idDataBlend,colExpression) values(?,?,?)";
	int update = jdbcTemplate.update(query,
		new Object[] { ldb.getColumnName(), ldb.getIdDataBlend(), ldb.getExpression() });
	LOG.debug("insert into listDataBlendColDefinitions=" + update);

	String q = "insert into staging_listDataDefinition("
		+ "idData,columnName,displayName,format,blend,idCol,KBE,dgroup,dupkey,measurement,"
		+ "idDataSchema,hashValue,numericalStat"
		+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
		+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
		+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int update2 = jdbcTemplate.update(q,
		new Object[] { ldb.getIdData(), ldb.getColumnName(), ldb.getColumnName(), ldb.getColumnValueType(), "Y",
			ldb.getIdDataBlend(), "N", "N", "N", "N", Integer.valueOf(0), "N", "N", "N", Integer.valueOf(3),
			Integer.valueOf(3), Integer.valueOf(3), "N", "N", "N", "N", Double.valueOf(3.0D), "N",
			Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N", "N", null, "N", null, "N", "N", "N",
			0 });
	LOG.debug("insert into staging_listDataDefinition=" + update2);
    }

    public void insertIntoListDataBlendRowAdd(listDataBlend ldb) {
	String sql = "insert into listDataBlendRowAdd (idDataBlend,rowAddExpression) values(?,?)";
	int update = jdbcTemplate.update(sql, ldb.getIdDataBlend(), ldb.getRowAddExpression());
	LOG.debug("insert into listDataBlendRowAdd" + update);
    }

    public void matchingFilter(listDataBlend ldb) {
	String listDataBlendQuery = "update listDataBlend set derivedColType=? , columnValue=? , columnValueType=? , expression=? ,columnName=? where idDataBlend=? ";
	int update3 = jdbcTemplate.update(listDataBlendQuery, ldb.getDerivedColType(), ldb.getColumnValue(),
		ldb.getColumnValueType(), ldb.getExpression(), ldb.getFilterName(), ldb.getIdDataBlend());
	LOG.debug("update listDataBlend" + update3);

	String query = "insert into listDataBlendFilterDefinitions" + "(name,idDataBlend,filteringExp) values(?,?,?)";
	int update = jdbcTemplate.update(query,
		new Object[] { ldb.getFilterName(), ldb.getIdDataBlend(), ldb.getExpression() });
	LOG.debug("update=" + update);

	String q = "insert into staging_listDataDefinition("
		+ "idData,columnName,displayName,format,blend,idCol,KBE,dgroup,dupkey,measurement,"
		+ "idDataSchema,hashValue,numericalStat"
		+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
		+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
		+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int update2 = jdbcTemplate.update(q,
		new Object[] { ldb.getIdData(), ldb.getFilterName(), ldb.getColumnName(), ldb.getColumnValueType(), "Y",
			ldb.getIdDataBlend(), "N", "N", "N", "N", Integer.valueOf(0), "N", "N", "N", Integer.valueOf(3),
			Integer.valueOf(3), Integer.valueOf(3), "N", "N", "N", "N", Double.valueOf(3.0D), "N",
			Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N", "N", null, "N", null, "N", "N", "N",
			0 });
	LOG.debug("insert into staging_listDataDefinition=" + update2);
    }

    public void qualityApplication(listDataBlend ldb) {
	/*
	 * String listDataBlendQuery =
	 * "update listDataBlend set derivedColType=? , columnValue=? , columnValueType=? , expression=? ,columnName=? where idDataBlend=? "
	 * ; int update3 = jdbcTemplate.update(listDataBlendQuery,
	 * ldb.getDerivedColType(), ldb.getColumnValue(), ldb.getColumnValueType(),
	 * ldb.getExpression(), ldb.getColumnName(), ldb.getIdDataBlend());
	 * LOG.debug("update listDataBlend" + update3);
	 */
	String sql = "select expression from listDataBlend where idDataBlend=" + ldb.getIdDataBlend();
	String expression = jdbcTemplate.queryForObject(sql, String.class);
	LOG.debug("expression=" + expression);

	if (expression == null) {
	    String listDataBlendQuery = "update listDataBlend set derivedColType=? , columnValue=? , columnValueType=? , expression=? ,"
		    + "columnName=?,idColumn=? where idDataBlend=? ";
	    int update3 = jdbcTemplate.update(listDataBlendQuery, ldb.getDerivedColType(), ldb.getColumnValue(),
		    ldb.getColumnValueType(), ldb.getExpression(), ldb.getColumnName(), ldb.getIdColumn(),
		    ldb.getIdDataBlend());
	    LOG.debug("update listDataBlend" + update3);
	} else {
	    String query = "insert into listDataBlend"
		    + "(idData,name,description,createdAt,updatedAt,createdBy,updatedBy,derivedColType, columnValue , columnValueType, expression ,columnName,idColumn) "
		    + "values(?,?,?,now(),now(),?,?,?,?,?,?,?,?)";
	    int update = jdbcTemplate.update(query,
		    new Object[] { ldb.getIdData(), ldb.getName(), ldb.getDescription(), ldb.getIdUser(),
			    ldb.getIdUser(), ldb.getDerivedColType(), ldb.getColumnValue(), ldb.getColumnValueType(),
			    ldb.getExpression(), ldb.getColumnName(), ldb.getIdColumn() });
	    LOG.debug("insert into listDataBlend=" + update);
	}
	String q1 = "select displayName from listDataDefinition where idData=" + ldb.getIdData();
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(q1);
	boolean sameColumnName = true;
	while (queryForRowSet.next()) {

	    String displayName = queryForRowSet.getString("displayName");
	    LOG.debug("displayName from ldd=" + displayName);
	    if (displayName.equalsIgnoreCase(ldb.getColumnName())) {
		LOG.info("same column name already exists");
		sameColumnName = false;
	    }
	}
	LOG.debug("sameColumnName=" + sameColumnName);
	if (sameColumnName) {

	    String q = "insert into staging_listDataDefinition("
		    + "idData,columnName,displayName,format,blend,idCol,KBE,dgroup,dupkey,measurement,"
		    + "idDataSchema,hashValue,numericalStat"
		    + ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
		    + "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
		    + "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,lengthValue) "
		    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    int update2 = jdbcTemplate.update(q,
		    new Object[] { ldb.getIdData(), ldb.getColumnName(), ldb.getColumnName(), ldb.getColumnValueType(),
			    "Y", ldb.getIdDataBlend(), "N", "N", "N", "N", Integer.valueOf(0), "N", "N", "N",
			    Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), "N", "N", "N", "N",
			    Double.valueOf(3.0D), "N", Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N", "N", null,
			    "N", null, "N", "N", "N", 0 });
	    LOG.debug("insert into listDataDefinition=" + update2);
	}
    }

    public int savedataforderievedcolumns(String name, String colExpression, Long idDataBlend, Long idData,
	    String blendcolumn, String columnCategory, String columnValue, String columnValueType) {

	String listDataBlendQuery = "update listDataBlend set derivedColType=? , columnValue=? , columnValueType=?  where idDataBlend=? ";
	int update3 = jdbcTemplate.update(listDataBlendQuery, columnCategory, columnValue, columnValueType,
		idDataBlend);
	LOG.debug("update listDataBlend" + update3);

	String query = "insert into listDataBlendColDefinitions" + "(name,idDataBlend,colExpression) values(?,?,?)";
	int update = jdbcTemplate.update(query, new Object[] { name, idDataBlend, colExpression });
	LOG.debug("insert into listDataBlendColDefinitions=" + update);

	String q = "insert into staging_listDataDefinition("
		+ "idData,columnName,displayName,format,blend,KBE,dgroup,dupkey,measurement,"
		+ "idDataSchema,hashValue,numericalStat"
		+ ",stringStat,nullCountThreshold,numericalThreshold,stringStatThreshold,incrementalCol,nonNull,primarykey,"
		+ "recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,partitionBy,startDate,"
		+ "timelinessKey,endDate,defaultCheck,defaultValues,patternCheck,patterns,badData,dateFormat,dateRule,lengthCheck,maxLengthCheck,lengthValue) "
		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int update2 = jdbcTemplate.update(q,
		new Object[] { idData, name, colExpression, "Double", "Y", "N", "N", "N", "N", Integer.valueOf(0), "N",
			"N", "N", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(3), "N", "N", "N", "N",
			Double.valueOf(3.0D), "N", Integer.valueOf(3), "N", "N", "N", "N", "N", "N", "N", "N", null,
			"N", null, "N", "N", "N", 0 });
	LOG.debug("insert into listDataDefinition=" + update2);
	return update;
    }

    public List<TemplateView> getTemplateView(List<Project> projList) {

	String sql = "";
	String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);

	sql = sql
		+ "select le.name, le.description,le.createdAt ,ls.name, le.idDataBlend, le.idData,le.createdByUser,ls.project_id as projectId,p.projectName ";
	sql = sql + "from listDataBlend le,listDataSources ls ,project p ";
	sql = sql + "where le.idData = ls.idData and ls.project_id = p.idProject and ls.project_id in ( " + projIds
		+ " )";

	List<TemplateView> templateview = jdbcTemplate.query(sql, new RowMapper<TemplateView>() {

	    @Override
	    public TemplateView mapRow(ResultSet rs, int rowNum) throws SQLException {

		TemplateView templateview = new TemplateView();

		templateview.setName(rs.getString(1));
		templateview.setLbdescription(rs.getString(2));
		templateview.setCreatedAt(rs.getDate(3));
		templateview.setLsdescription(rs.getString(4));
		templateview.setIdDataBlend(rs.getInt(5));
		templateview.setIdData(rs.getInt(6));
		templateview.setCreatedByUser(rs.getString(7));
		templateview.setProjectId(rs.getInt("projectId"));
		templateview.setProjectName(rs.getString("projectName"));

		return templateview;
	    }

	});

	return templateview;
    }

    public List<listDataBlend> getDataFromListDataBlend(String name) {
	try {
	    String sql = "select name,expression,columnName,columnValue from listDataBlend where name='" + name + "'";
	    List<listDataBlend> listdatablend = jdbcTemplate.query(sql, new RowMapper<listDataBlend>() {

		@Override
		public listDataBlend mapRow(ResultSet rs, int rowNum) throws SQLException {

		    listDataBlend ldb = new listDataBlend();
		    ldb.setName(rs.getString("name"));
		    ldb.setExpression(rs.getString("expression"));
		    ldb.setColumnName(rs.getString("columnName"));
		    ldb.setColumnValue(rs.getString("columnValue"));
		    return ldb;
		}
	    });
	    return listdatablend;
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
	return null;
    }

    public Object[] getDerievedColumns(Long idData) {

	String sqlQuery = "select idDataBlend from listDataBlend where idData=?";
	List<Integer> res = jdbcTemplate.queryForList(sqlQuery, Integer.class, idData);
	if (res.size() == 0) {
	    return null;
	}
	// return res.get(0);
	LOG.debug("res.get(0)=" + res);
	Map<String, String> mapobject = new HashMap<String, String>();
	// Map<String, String> mapobjectFilters = new HashMap<>();
	Multimap<String, String> mapobjectFilters = LinkedHashMultimap.create();
	Iterator iterator = res.iterator();
	while (iterator.hasNext()) {
	    int idDataBlend = (Integer) iterator.next();
	    LOG.debug("idDataBlend=" + idDataBlend);
	    String queryForDerievedColumns = "SELECT name,filteringExp FROM listDataBlendFilterDefinitions where idDataBlend="
		    + idDataBlend;
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(queryForDerievedColumns);
	    while (queryForRowSet.next()) {
		String name = queryForRowSet.getString("name");
		String filteringExp = queryForRowSet.getString("filteringExp");
		LOG.debug("name=" + name + "filteringExp=" + filteringExp);
		mapobject.put(name, filteringExp);
	    }
	    String queryForFilters = "SELECT name,colExpression FROM listDataBlendColDefinitions where idDataBlend="
		    + idDataBlend;
	    SqlRowSet queryForRowSetFilters = jdbcTemplate.queryForRowSet(queryForFilters);
	    while (queryForRowSetFilters.next()) {
		String name = queryForRowSetFilters.getString("name");
		String colExpression = queryForRowSetFilters.getString("colExpression");
		LOG.debug("name=" + name + "colExpression=" + colExpression);
		mapobjectFilters.put(name, colExpression);
	    }
	}
	String q = "select name,expression from listDataBlend where idData=" + idData;
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(q);
	while (queryForRowSet.next()) {
	    String name = queryForRowSet.getString("name");
	    String expression = queryForRowSet.getString("expression");
	    LOG.debug("name=" + name + "colExpression=" + expression);
	    mapobjectFilters.put(name, expression);
	}
	return new Object[] { true, mapobject, mapobjectFilters };
    }

    public List<ListDataDefinition> view(Long idData) {

	String sql = "SELECT * FROM listDataDefinition WHERE idData =" + idData;

	List<ListDataDefinition> listdatadefinition = jdbcTemplate.query(sql, new RowMapper<ListDataDefinition>() {

	    @Override
	    public ListDataDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {

		ListDataDefinition listdatadefinition = new ListDataDefinition();

		listdatadefinition.setIdData(rs.getInt("idData"));
		listdatadefinition.setIdColumn(rs.getLong("idColumn"));

		listdatadefinition.setColumnName(rs.getString("columnName"));
		listdatadefinition.setDisplayName(rs.getString("displayName"));

		listdatadefinition.setPrimaryKey(rs.getString("primaryKey"));
		listdatadefinition.setNonNull(rs.getString("nonNull"));
		// LOG.debug(rs.getString("nonNull"));

		listdatadefinition.setFormat(rs.getString("format"));

		listdatadefinition.setHashValue(rs.getString("hashValue"));
		listdatadefinition.setNumericalStat(rs.getString("numericalStat"));
		listdatadefinition.setStringStat(rs.getString("stringStat"));

		listdatadefinition.setNumericalThreshold(rs.getDouble("numericalThreshold"));
		listdatadefinition.setStringStatThreshold(rs.getDouble("stringStatThreshold"));
		listdatadefinition.setNullCountThreshold(rs.getDouble("nullCountThreshold"));

		listdatadefinition.setKBE(rs.getString("KBE"));
		/*
		 * listdatadefinition.setIdDataSchema(rs.getLong("idDataSchema") );
		 * LOG.debug("idDataSchema="+rs.getLong("idDataSchema") );
		 */
		listdatadefinition.setDgroup(rs.getString("dgroup"));
		listdatadefinition.setDupkey(rs.getString("dupkey"));
		listdatadefinition.setMeasurement(rs.getString("measurement"));
		listdatadefinition.setIncrementalCol(rs.getString("incrementalCol"));
		listdatadefinition.setRecordAnomaly(rs.getString("recordAnomaly"));
		listdatadefinition.setStartDate(rs.getString("startDate"));
		listdatadefinition.setEndDate(rs.getString("endDate"));
		listdatadefinition.setTimelinessKey(rs.getString("timelinessKey"));
		listdatadefinition.setDefaultCheck(rs.getString("defaultCheck"));
		listdatadefinition.setDefaultValues(rs.getString("defaultValues"));
		listdatadefinition.setRecordAnomalyThreshold(rs.getDouble("recordAnomalyThreshold"));
		listdatadefinition.setBlend(rs.getString("blend"));
		listdatadefinition.setIdCol(rs.getInt("idCol"));
		listdatadefinition.setDataDrift(rs.getString("dataDrift"));
		listdatadefinition.setDataDriftThreshold(rs.getDouble("dataDriftThreshold"));
		listdatadefinition.setOutOfNormStat(rs.getString("outOfNormStat"));
		listdatadefinition.setOutOfNormStatThreshold(rs.getDouble("outOfNormStatThreshold"));
		listdatadefinition.setIsMasked(rs.getString("isMasked"));
		listdatadefinition.setPartitionBy(rs.getString("partitionBy"));
		listdatadefinition.setPatterns(rs.getString("patterns")==null?"":rs.getString("patterns"));
		listdatadefinition.setPatternCheck(rs.getString("patternCheck"));
		listdatadefinition.setDateRule(rs.getString("dateRule"));
		listdatadefinition.setbadData(rs.getString("badData"));
		listdatadefinition.setDateFormat(rs.getString("dateFormat")==null?"":rs.getString("dateFormat"));

		// <!-- 24_DEC_2018 (12.43pm) Priyanka -->
		listdatadefinition.setLengthCheck(rs.getString("lengthCheck"));
		listdatadefinition.setLengthValue(rs.getString("lengthValue"));
		/* =================== */

		// Max Length Check -->
		listdatadefinition.setMaxLengthCheck(rs.getString("maxLengthCheck"));

		listdatadefinition.setLengthThreshold(rs.getDouble("lengthCheckThreshold"));
		listdatadefinition.setBadDataThreshold(rs.getDouble("badDataCheckThreshold"));

		listdatadefinition.setPatternCheckThreshold(rs.getDouble("patternCheckThreshold"));

		listdatadefinition.setDefaultPatternCheck(rs.getString("defaultPatternCheck"));
		listdatadefinition.setDefaultPatterns(rs.getString("defaultPatterns")==null?"":rs.getString("defaultPatterns"));
		listdatadefinition.setApplyRule(rs.getString("applyrule"));
		listdatadefinition.setCorrelationcolumn(rs.getString("correlationcolumn")==null?"":rs.getString("correlationcolumn"));

		return listdatadefinition;
	    }

	});
	return listdatadefinition;

    }

    public int nonNullyes(Long idData) {
	String sql = "select nonNull from listDataDefinition where idData=" + idData + " Limit 1";
	String nonNull = jdbcTemplate.queryForObject(sql, String.class);
	String query = "";
	LOG.debug("nonNull=" + nonNull);
	if (nonNull.equalsIgnoreCase("n")) {
	    query = "update listDataDefinition set nonNull='Y',KBE='Y' where idData='" + idData + "' ";
	} else {
	    query = "update listDataDefinition set nonNull='N' where idData='" + idData + "' ";
	}
	return jdbcTemplate.update(query);

    }

    public int hashValueyes(Long idData) {
	String sql = "select hashValue from staging_listDataDefinition where idData=" + idData + " Limit 1";
	String hashValue = jdbcTemplate.queryForObject(sql, String.class);
	String query = "";
	if (hashValue.equalsIgnoreCase("n")) {
	    query = "update staging_listDataDefinition set hashValue='Y',KBE='Y' where idData='" + idData + "' ";
	} else {
	    query = "update staging_listDataDefinition set hashValue='N' where idData='" + idData + "' ";
	}
	return jdbcTemplate.update(query);

    }

    public int numericalStatyes(Long idData) {

	String sql = "select numericalStat from staging_listDataDefinition where idData=" + idData + "  and lower(format) like '%int%'  Limit 1";
	String numericalStat = jdbcTemplate.queryForObject(sql, String.class);

	String query = "";
	if (numericalStat.equalsIgnoreCase("n")) {
	    // query = "update listDataDefinition set numericalStat='Y',KBE='Y' where
	    // idData='" + idData + "' ";

	    query = "update staging_listDataDefinition set numericalStat='Y',KBE='Y' where idData=" + idData
		    + " and (upper(format) = 'INT' or upper(format) = 'NUMBER' or upper(format) = 'INTEGER' or upper(format) = 'NUMERIC' or upper(format) = 'FLOAT' or upper(format) = 'DOUBLE' or upper(format) = 'BIGINT' or upper(format) = 'SMALLINT' or upper(format) = 'DECIMAL')";
	} else {
		//DC- 2769 Mamta
	    query = "update staging_listDataDefinition set numericalStat='N' where idData=" + idData + "";
	}
	return jdbcTemplate.update(query);

    }

    public int stringStatyes(Long idData) {
	String sql = "select stringStat from staging_listDataDefinition where idData=" + idData + " Limit 1";
	String stringStat = jdbcTemplate.queryForObject(sql, String.class);
	String query = "";
	if (stringStat.equalsIgnoreCase("n")) {
	    query = "update staging_listDataDefinition set stringStat='Y',KBE='Y' where idData='" + idData + "' ";
	} else {
	    query = "update staging_listDataDefinition set stringStat='N' where idData='" + idData + "' ";
	}
	return jdbcTemplate.update(query);

    }

	public int updateDupKeyYes(Long idData) {
		String dupSql = "select displayName from staging_listDataDefinition where primaryKey='Y' and format not in ('Decimal','Date') and idData=" + idData;
		List<String> displayNameList = jdbcTemplate.queryForList(dupSql, String.class);
		String columnNames = StringUtils.join(displayNameList, "', '").trim();
		String dispNameCondition = "";
		long dupKeyCount = 0l;
		if (displayNameList!=null && displayNameList.size() > 0)
			dispNameCondition = " and displayName not in('" + columnNames + "')";
		String sql = "select count(*) from staging_listDataDefinition where idData=" + idData +
				"" + dispNameCondition +" and format not in ('Decimal','Date')  and dupkey='N'";
		dupKeyCount = jdbcTemplate.queryForObject(sql, Long.class);
		String query = "";
		if (dupKeyCount > 0) {
			query = "update staging_listDataDefinition set dupkey='Y',KBE='Y' where idData='" + idData
					+ "' " + dispNameCondition + " and format not in ('Decimal','Date') ";
		} else {
			query = "update staging_listDataDefinition set dupKey='N' where idData='" + idData + "'";
		}
		return jdbcTemplate.update(query);
	}

	public int primaryKeyyes(Long idData) {
		String dupSql = "select displayName from staging_listDataDefinition where dupkey='Y' and format not in ('Decimal','Date') and idData=" + idData;
		List<String> displayNameList = jdbcTemplate.queryForList(dupSql, String.class);
		String columnNames = StringUtils.join(displayNameList, "', '").trim();
		String dispNameCondition = "";
		long primaryKeyCount = 0l;
		if (displayNameList!=null && displayNameList.size() > 0)
			dispNameCondition = " and displayName not in('" + columnNames + "') ";

		String sql = "select count(*) from staging_listDataDefinition where idData=" + idData +
				"" + dispNameCondition +" and format not in ('Decimal','Date') and primaryKey='N'";
		primaryKeyCount = jdbcTemplate.queryForObject(sql, Long.class);
		String query = "";
		if (primaryKeyCount > 0) {
			query = "update staging_listDataDefinition set primaryKey='Y',KBE='Y' where idData='" + idData + "' " +
					"" + dispNameCondition + " and format not in ('Decimal','Date') ";
		} else {
			query = "update staging_listDataDefinition set primaryKey='N' where idData='" + idData + "' ";
		}
		return jdbcTemplate.update(query);

	}

    public void deleteDataFromListDataBlend(long idDataBlend) {
	String sql = "delete from listDataBlend where idDataBlend=" + idDataBlend;
	int update = jdbcTemplate.update(sql);
	LOG.debug("deleted from listDataBlend " + update);
    }

    public DeleteTempView delete(int idDataBlend) {

	String sql = "SELECT le.name, le.description, ls.description,le.idDataBlend ,"
		+ " lsdc.name,	lsdc.colExpression , ldbfd.name,"
		+ " ldbfd.filteringExp,le.idData  from listDataSources ls, "
		+ "listDataBlend le	,listDataBlendColDefinitions lsdc ,"
		+ "	listDataBlendFilterDefinitions ldbfd " + "WHERE le.idData = ls.idData && le.idDataBlend ="
		+ idDataBlend;

	/*
	 * String sql="SELECT le.name, le.description, ls.description," +
	 * "le.idDataBlend from listDataSources ls,listDataBlend le " +
	 * "WHERE le.idData = ls.idData && le.idDataBlend ="+idDataBlend;
	 */

	return jdbcTemplate.query(sql, new ResultSetExtractor<DeleteTempView>() {

	    public DeleteTempView extractData(ResultSet rs) throws SQLException, DataAccessException {
		if (rs.next()) {

		    DeleteTempView deletetempview = new DeleteTempView();
		    deletetempview.setLename(rs.getString(1));
		    deletetempview.setLedescription(rs.getString(2));
		    deletetempview.setLsdescription(rs.getString(3));
		    deletetempview.setIdDataBlend(rs.getInt(4));

		    deletetempview.setLsdcname(rs.getString(5));
		    deletetempview.setLsdccolExpression(rs.getString(6));

		    deletetempview.setLdbfdname(rs.getString(7));

		    deletetempview.setLdbfdfilteringExp(rs.getString(8));
		    deletetempview.setIdData(rs.getInt(9));
		    return deletetempview;
		}

		return null;
	    }

	});
    }

    /*
     * 
     * 
     * Delete the Extend TEmplate data
     * 
     * 
     * 
     */
    public int deletefilter(Long idDataBlend) {

	String deleteStatement = "DELETE FROM listDataBlendFilterDefinitions WHERE idDataBlend=?";
	return jdbcTemplate.update(deleteStatement, idDataBlend);
    }

    public int deletederivedcolumns(Long idDataBlend) {

	String deleteStatement = "DELETE FROM listDataBlendColDefinitions WHERE idDataBlend=?";
	return jdbcTemplate.update(deleteStatement, idDataBlend);
    }

    public int DeleteTempViewFully(Long idDataBlend) {

	String sql = "delete from listDataBlend WHERE idDataBlend=?";
	int count = jdbcTemplate.update(sql, idDataBlend);

	return count;
    }

    public int DeleteListDataBeanColDef(int idDataBlend) {
	String sql = "delete from listDataBlendColDefinitions WHERE idDataBlend=?";
	int count = jdbcTemplate.update(sql, idDataBlend);
	return count;
    }

    public void updatelistApplicationsForSchemamatching(Long idApp, Long idData, Long idRightData, String matchtype,
	    String name, String threasholdtype, String threshold, String prefix1, String prefix2) {
	String entityColumn = "";
	if (matchtype.equalsIgnoreCase("metadata")) {
	    name = idApp + "_" + name + "_MetaData";
	    entityColumn = "MetaData";
	    threasholdtype = "";
	    threshold = "0";
	} else if (matchtype.equalsIgnoreCase("rc")) {
	    name = idApp + "_" + name + "_Record Count";
	    entityColumn = "Record Count";
	} else if (matchtype.equalsIgnoreCase("both")) {
	    name = idApp + "_" + name + "_MetaData";
	    entityColumn = "MetaData";
	    // threasholdtype = "";
	    // threshold = "0";
	}

	LOG.debug("threasholdtype:" + threasholdtype + " :threshold:" + threshold);

	// Adding variable for Postgresql
	try {
	    Double thresholdp = new Double(threshold);
	    String sql = "update listApplications set  name=? ,idLeftData=?, idRightData = ?, entityColumn=? ,recordCountAnomalyThreshold=?,recordCountAnomaly=?, prefix1=?, prefix2=?  Where idApp="
		    + idApp;

	    jdbcTemplate.update(sql, name, idData, idRightData, entityColumn, thresholdp, threasholdtype, prefix1,
		    prefix2);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}

    }

    @Override
    public void updateSchemamatching(Long idApp, String description, String threasholdtype, String threshold,
	    String prefix1, String prefix2) {
	LOG.debug("threasholdtype:" + threasholdtype + " :threshold:" + threshold);
	try {
	    Double thresholdp = new Double(threshold);
	    String sql = "update listApplications set description=?, recordCountAnomalyThreshold=?, recordCountAnomaly=?, prefix1=?, prefix2=?  Where idApp="
		    + idApp;
	    jdbcTemplate.update(sql, description, thresholdp, threasholdtype, prefix1, prefix2);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}

    }

    @Override
    public List<String> getPrimaryCheckEnabledColumnsByIdData(Long idData) {
	List<String> primaryKeyColumns = null;

	try {
	    String sql = "select displayName from listDataDefinition where primaryKey='Y' and idData=?";
	    primaryKeyColumns = jdbcTemplate.queryForList(sql, String.class, idData);

	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
	return primaryKeyColumns;
    }

    public void updatelistApplicationsForSchemamatchingForBoth_RC(Long idApp, Long idData, Long idRightData,
	    String matchtype, String name, String threasholdtype, String threshold) {
	String entityColumn = "";
	if (matchtype.equalsIgnoreCase("metadata")) {
	    name = idApp + "_" + name + "_MetaData";
	    entityColumn = "MetaData";
	    threasholdtype = "";
	    threshold = "0";
	} else if (matchtype.equalsIgnoreCase("rc")) {
	    name = idApp + "_" + name + "_Record Count";
	    entityColumn = "Record Count";
	} else if (matchtype.equalsIgnoreCase("both")) {
	    name = idApp + "_" + name + "_Record Count";
	    entityColumn = "Record Count";
	}
	LOG.debug(" both threasholdtype :" + threasholdtype + " :threshold:" + threshold);
	// Adding variable for Postgresql
	try {
	    Double thresholdp = new Double(threshold);
	    String sql = "update listApplications set  name=? ,idLeftData=?, idRightData = ?, entityColumn=? ,recordCountAnomalyThreshold=?,recordCountAnomaly=?  Where idApp=?";

	    jdbcTemplate.update(sql, name, idData, idRightData, entityColumn, thresholdp, threasholdtype, idApp);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
    }

    /*
     * public String getReferenceFilesFromListDataFiles(Long idData) {
     * LOG.debug("getReferenceFilesFromListDataFiles"); String sql =
     * "select fileName from listDataFiles where idData=" + idData; SqlRowSet
     * queryForRowSet = jdbcTemplate.queryForRowSet(sql); List<String> data = new
     * ArrayList<String>(); StringJoiner sj = new StringJoiner(","); while
     * (queryForRowSet.next()) { sj.add(queryForRowSet.getString(1));
     * data.add(queryForRowSet.getString(1)); } return sj.toString(); }
     * 
     * public List<ListDataSource> getlistdatasourcesname() { String sql =
     * "SELECT name,idData FROM listDataSources where active='yes'";
     */

    // changes for KAFKA

    @Override
    public String getDataLocationByidData(Long idData) {

	LOG.info("In getDataLocationByidData ");
	String sql = "SELECT dataLocation FROM listDataSources where idData=" + idData;

	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	List<String> data = new ArrayList<String>();
	StringJoiner sj = new StringJoiner(",");
	while (queryForRowSet.next()) {
	    sj.add(queryForRowSet.getString(1));
	    data.add(queryForRowSet.getString(1));
	}

	LOG.debug("getDataLocationByidData ->" + sj.toString());

	return sj.toString();

    }

    public void updatelistApplicationsForKafka(Long idApp, int windowTime, String startTime, String endTime) {

	LOG.info("In updatelistApplicationsForKafka");
	String sql = "update listApplications set  windowTime=? ,startTime=?, endTime = ? Where idApp=?";
	jdbcTemplate.update(sql, windowTime, startTime, endTime, idApp);
    }

    // added for Second Source Template

    public List<String> getSecondSourceTemplateNames(Long projectId) {
	String sql = "SELECT name,idData FROM listDataSources where active='yes' and project_id=" + projectId
		+ " order by idData desc";

	List<String> list;

	List lstTemplateName = jdbcTemplate.query(sql, new RowMapper<String>() {

	    @Override
	    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		List<String> templateNameData = new ArrayList();
		templateNameData.add(rs.getInt("idData") + "-" + rs.getString("name"));

		return templateNameData.toString();
	    }

	});

	return lstTemplateName;
    }

    @Override
    public List<ListApplications> getValidationCheckOfTemplateById(Long idData, String validationName) {
	try {
	    LOG.info("\n====>Execute getValidationCheckOfTemplateById ....");

	    String sql = "SELECT * FROM listApplications where idData=" + idData + " and name LIKE '%" + validationName
		    + "' order by idApp desc";
	    LOG.debug("\n===>Sql: " + sql);

	    List<ListApplications> listApplications = jdbcTemplate.query(sql, new RowMapper<ListApplications>() {
		@Override
		public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
		    ListApplications alistdatasource = new ListApplications();
		    alistdatasource.setIdApp(rs.getInt("idApp"));
		    alistdatasource.setName(rs.getString("name"));
		    return alistdatasource;
		}
	    });
	    return listApplications;
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    @Override
    public Long copyTemplate(long idData, String newTemplateName, String createdByUser) {
	Long newTemplateId = null;
	try {

	    String datasourceSql = ("insert into listDataSources(name ,description ,dataLocation ,dataSource ,createdBy ,idDataBlend ,createdAt ,"
		    + "updatedAt ,updatedBy ,schemaName ,idDataSchema ,ignoreRowsCount ,active ,project_id ,profilingEnabled ,advancedRulesEnabled ,"
		    + "template_create_success,deltaApprovalStatus,domain_id,createdByUser) (select '" + newTemplateName
		    + "' ,description ,dataLocation ,dataSource ,createdBy ,idDataBlend ,now() as createdAt,"
		    + "now() as updatedAt, updatedBy ,schemaName ,idDataSchema ,ignoreRowsCount ,active ,project_id ,profilingEnabled ,advancedRulesEnabled ,"
		    + "template_create_success,deltaApprovalStatus,domain_id,'" + createdByUser
		    + "' as createdByUser from listDataSources where idData=" + idData + ")");

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddata"
		    : "idData";

	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    jdbcTemplate.update(new PreparedStatementCreator() {
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    PreparedStatement pst = con.prepareStatement(datasourceSql, new String[] { key_name });
		    return pst;
		}
	    }, keyHolder);

	    newTemplateId = keyHolder.getKey().longValue();

	    if (newTemplateId != null && newTemplateId != 0l) {
		LOG.debug("\n=====> Inserted into listDataSource: Success -- New templateId:[" + newTemplateId + "]");

		String updateListDataAccess = "insert into listDataAccess (idData ,hostName ,portName ,userName ,pwd ,"
			+ "schemaName ,folderName ,queryString ,query ,incrementalType ,idDataSchema ,whereCondition ,"
			+ "domain ,fileHeader ,dateFormat ,sliceStart ,sliceEnd ,metaData ,isRawData ,sslEnb ,"
			+ "sslTrustStorePath ,trustPassword ,hivejdbcport ,hivejdbchost ,gatewayPath ,jksPath ,"
			+ "zookeeperUrl ,rollingHeader ,rollingColumn) (select  " + newTemplateId
			+ " as idData , hostName ,portName ,userName ,pwd ," + "schemaName ,"
			+ "folderName ,queryString ,query ,incrementalType ,idDataSchema ,whereCondition ,"
			+ "domain ,fileHeader ,dateFormat ,sliceStart ,sliceEnd ,metaData ,isRawData ,sslEnb ,"
			+ "sslTrustStorePath ,trustPassword ,hivejdbcport ,hivejdbchost ,gatewayPath ,jksPath ,"
			+ "zookeeperUrl ,rollingHeader ,rollingColumn from listDataAccess where idData=" + idData + ")";
		jdbcTemplate.execute(updateListDataAccess);

		LOG.info("\n=====> Inserted into listDataAccess");

		String updateListDataDefinationQuery = "insert into listDataDefinition (idData, columnName, "
			+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
			+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
			+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
			+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
			+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
			+ "lengthcheck , maxLengthCheck , lengthvalue, applyrule, startDate, timelinessKey, endDate, "
			+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
			+ "dateFormat, defaultPatternCheck, defaultPatterns) (select " + newTemplateId
			+ " as idData, columnName, "
			+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
			+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
			+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
			+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
			+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
			+ "lengthcheck, maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
			+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
			+ "dateFormat, defaultPatternCheck, defaultPatterns from listDataDefinition where idData="
			+ idData + ")";
		jdbcTemplate.execute(updateListDataDefinationQuery);

		LOG.info("\n=====> Inserted into listDataDefinition");

		// Copy advanced rules
		String updateAdvancedRulesData = "insert into listAdvancedRules(idData,Date,Run,ruleType,columnName,ruleExpr,ruleSql,isCustomRuleEligible)"
			+ " (Select " + newTemplateId
			+ " as IdData,Date,Run,ruleType,columnName,ruleExpr,ruleSql,isCustomRuleEligible from listAdvancedRules where idData="
			+ idData + ")  ";

		jdbcTemplate.execute(updateAdvancedRulesData);

		LOG.info("\n=====> Inserted into listAdvancedRules");

		// Copy custom rules
		String updateCustomRulesData = "insert into listColRules(idData ,idCol ,ruleName ,description ,createdAt ,ruleType ,expression ,external ,"
			+ "externalDatasetName ,idRightData ,matchingRules ,matchType ,sourcetemplateone ,sourcetemplatesecond ,ruleThreshold ,"
			+ "createdByUser, project_id, domain_id, domensionId, activeFlag, anchorColumns)(select "
			+ newTemplateId + " as idData, idCol ,concat('Tmpl_" + newTemplateId
			+ "_',ruleName) as ruleName ,description ,now() as createdAt, ruleType ,expression ,external ,"
			+ " externalDatasetName ,idRightData ,matchingRules ,matchType ,sourcetemplateone ,sourcetemplatesecond ,ruleThreshold ,'"
			+ createdByUser
			+ "' as createdByUser, project_id, domain_id, domensionId, activeFlag, anchorColumns from listColRules where idData="
			+ idData + ")";

		jdbcTemplate.execute(updateCustomRulesData);

		LOG.info("\n=====> Inserted into listColRules");

		// Copy Global Rules
		String updateGlobalRulesData = "insert into  rule_Template_Mapping(templateid,ruleId,ruleName,ruleExpression,ruleType,anchorColumns,"
			+ "activeFlag,filter_condition,matchingRules)(select " + newTemplateId
			+ " as templateid, ruleId,ruleName,ruleExpression,ruleType, "
			+ "anchorColumns, activeFlag,filter_condition,matchingRules from rule_Template_Mapping where templateid ="
			+ idData + ")";
		jdbcTemplate.execute(updateGlobalRulesData);

		LOG.info("\n=====> Inserted into rule_Template_Mapping");

		// Copy profiling results

		// Column Profile
		// Query compatibility changes for both POSTGRES and MYSQL
		String updateColProfileData = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
		    updateColProfileData = "insert into column_profile_master_table(Date ,Run ,idDataSchema ,folderPath ,"
			    + "table_or_fileName ,Column_Name ,Data_Type ,Total_Record_Count ,Missing_Value ,Percentage_Missing ,"
			    + "Unique_Count ,Min_Length ,Max_Length ,Mean ,Std_Dev ,Min ,Max ,\"99_percentaile\" ,\"75_percentile\" ,"
			    + "\"25_percentile\" ,\"1_percentile\", Default_Patterns ,idData) (Select Date ," + 1 + " as Run ,idDataSchema ,folderPath ,table_or_fileName ,"
			    + "Column_Name ,Data_Type ,Total_Record_Count ,Missing_Value ,Percentage_Missing ,Unique_Count ,"
			    + "Min_Length ,Max_Length ,Mean ,Std_Dev ,Min ,Max ,\"99_percentaile\" ,\"75_percentile\" ,\"25_percentile\" ,"
			    + "\"1_percentile\" ,Default_Patterns ," + newTemplateId
			    + " as idData from column_profile_master_table where idData=" + idData + " And Date = (SELECT Max(DATE) FROM column_profile_master_table WHERE idData=" + idData + ") "
				+ " And Run = (SELECT Run FROM column_profile_master_table WHERE idData=" + idData + ""
				+ " ORDER BY DATE DESC,Run DESC LIMIT 1))";
		} else {
			updateColProfileData = "insert into column_profile_master_table(Date ,Run ,idDataSchema ,folderPath ,"
					+ "table_or_fileName ,Column_Name ,Data_Type ,Total_Record_Count ,Missing_Value ,Percentage_Missing ,"
					+ "Unique_Count ,Min_Length ,Max_Length ,Mean ,Std_Dev ,Min ,Max ,99_percentaile ,75_percentile ,"
					+ "25_percentile ,1_percentile, Default_Patterns ,idData) (Select Date ," + 1 + " as Run ,idDataSchema ,folderPath ,table_or_fileName ,"
					+ "Column_Name ,Data_Type ,Total_Record_Count ,Missing_Value ,Percentage_Missing ,Unique_Count ,"
					+ "Min_Length ,Max_Length ,Mean ,Std_Dev ,Min ,Max ,99_percentaile ,75_percentile ,25_percentile ,"
					+ "1_percentile ,Default_Patterns ," + newTemplateId
					+ " as idData from column_profile_master_table where idData=" + idData
					+ "  And Date = (SELECT Max(DATE) FROM column_profile_master_table WHERE idData=" + idData + ") "
					+ " And Run = (SELECT Run FROM column_profile_master_table WHERE idData=" + idData + ""
					+ " ORDER BY DATE DESC,Run DESC LIMIT 1))";
		}
		jdbcTemplate1.execute(updateColProfileData);

		LOG.info("\n=====> Inserted into column_profile_master_table");

		// Column Detail Profile
			String updateColDetailProfileData = "insert into column_profile_detail_master_table(Date ,Run ,idData ,idDataSchema ,"
					+ "folderPath ,table_or_fileName ,Column_Name ,Column_Value ,Count ,Percentage)"
					+ " (Select Date ," + 1 + " as Run ," + newTemplateId
					+ " as idData ,idDataSchema ,folderPath ,table_or_fileName ,"
					+ "Column_Name ,Column_Value ,Count ,Percentage from column_profile_detail_master_table where idData="
					+ idData + " And Date = (SELECT Max(DATE) FROM column_profile_detail_master_table WHERE idData= "+ idData + ") "
					+ " And Run = (SELECT Run FROM column_profile_detail_master_table WHERE idData=" + idData + " ORDER BY DATE DESC,Run DESC LIMIT 1))";
			jdbcTemplate1.execute(updateColDetailProfileData);

		LOG.info("\n=====> Inserted into column_profile_detail_master_table");

		// Column combination Profile
			String updateColCombProfileData = "insert into column_combination_profile_master_table(Date ,Run ,idData ,"
					+ "idDataSchema ,folderPath ,table_or_fileName ,Column_Group_Name ,Column_Group_Value ,Count ,Percentage) "
					+ "(Select Date ," + 1 + " as Run ," + newTemplateId
					+ " as idData ,idDataSchema ,folderPath ,table_or_fileName ,Column_Group_Name ,Column_Group_Value ,"
					+ "Count ,Percentage from column_combination_profile_master_table where idData=" + idData + " "
					+ "And Date = (SELECT Max(DATE) FROM column_combination_profile_master_table WHERE idData=" + idData + ") "
					+ "And Run = (SELECT Run FROM column_combination_profile_master_table WHERE idData=" + idData + " ORDER BY DATE DESC,Run DESC LIMIT 1))";
			jdbcTemplate1.execute(updateColCombProfileData);

		LOG.info("\n=====> Inserted into column_combination_profile_master_table");

		// Numerical Profile
			String updateNumericalProfileData = "insert into numerical_profile_master_table(Date ,Run ,idData ,idDataSchema ,"
					+ "folderPath ,table_or_fileName ,Column_Name_1 ,Column_Name_2 ,Correlation  ) "
					+ "(Select Date ," + 1 + " as Run , " + newTemplateId
					+ " as idData ,idDataSchema ,folderPath ,table_or_fileName ,Column_Name_1 ,Column_Name_2 ,Correlation  "
					+ "from numerical_profile_master_table where idData=" + idData
					+ " And Date = (SELECT Max(DATE) FROM numerical_profile_master_table WHERE idData=" + idData + ") "
					+ " And Run = (SELECT Run FROM numerical_profile_master_table WHERE idData=" + idData + " "
					+ "ORDER BY DATE DESC,Run DESC LIMIT 1))";
			jdbcTemplate1.execute(updateNumericalProfileData);

		LOG.info("\n=====> Inserted into numerical_profile_master_table");

		// Row Profile
			String updateRowProfileData = "insert into row_profile_master_table(Date ,Run , idData ,idDataSchema ,folderPath ,"
					+ "table_or_fileName ,Number_of_Columns_with_NULL ,Number_of_Records ,Percentage_Missing) "
					+ "(Select Date ," + 1 + " as Run , " + newTemplateId + " as idData ,idDataSchema ,folderPath ,"
					+ "table_or_fileName ,Number_of_Columns_with_NULL ,Number_of_Records ,Percentage_Missing "
					+ "from row_profile_master_table where idData=" + idData + " And Date = (SELECT Max(DATE) FROM row_profile_master_table WHERE idData=" + idData + ") "
					+ "And Run = (SELECT Run FROM row_profile_master_table WHERE idData=" + idData + " "
					+ "ORDER BY DATE DESC,Run DESC LIMIT 1))";
			jdbcTemplate1.execute(updateRowProfileData);

		LOG.info("\n=====> Inserted into row_profile_master_table");

	    } else {
		LOG.info("\n=====> Insert into listDataSource: Failed ");
	    }

	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
	return newTemplateId;
    }

    @Override
    public List<Dimension> getlistdimensionname() {
	String sql = "SELECT idDimension,dimensionName FROM dimension";

	List<Dimension> listDimension = jdbcTemplate.query(sql, new RowMapper<Dimension>() {

	    @Override
	    public Dimension mapRow(ResultSet rs, int rowNum) throws SQLException {

		Dimension listDimension = new Dimension();

		listDimension.setIdDimension(rs.getInt("idDimension"));
		listDimension.setDimensionName(rs.getString("dimensionName"));

		return listDimension;
	    }
	});
	return listDimension;
    }

    @Override
    public List<ListDataDefinition> getListDataDefinitionsInStaging(long idData) {

	String sql = "SELECT * FROM staging_listDataDefinition WHERE idData =" + idData;

	List<ListDataDefinition> listdatadefinition = jdbcTemplate.query(sql, new RowMapper<ListDataDefinition>() {

	    @Override
	    public ListDataDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {

		ListDataDefinition listdatadefinition = new ListDataDefinition();

		listdatadefinition.setIdData(rs.getInt("idData"));
		listdatadefinition.setIdColumn(rs.getLong("idColumn"));
		listdatadefinition.setColumnName(rs.getString("columnName"));
		listdatadefinition.setDisplayName(rs.getString("displayName"));
		listdatadefinition.setPrimaryKey(rs.getString("primaryKey"));
		listdatadefinition.setNonNull(rs.getString("nonNull"));
		listdatadefinition.setFormat(rs.getString("format"));
		listdatadefinition.setHashValue(rs.getString("hashValue"));
		listdatadefinition.setNumericalStat(rs.getString("numericalStat"));
		listdatadefinition.setStringStat(rs.getString("stringStat"));
		listdatadefinition.setNumericalThreshold(rs.getDouble("numericalThreshold"));
		listdatadefinition.setStringStatThreshold(rs.getDouble("stringStatThreshold"));
		listdatadefinition.setNullCountThreshold(rs.getDouble("nullCountThreshold"));
		listdatadefinition.setKBE(rs.getString("KBE"));
		listdatadefinition.setDgroup(rs.getString("dgroup"));
		listdatadefinition.setDupkey(rs.getString("dupkey"));
		listdatadefinition.setMeasurement(rs.getString("measurement"));
		listdatadefinition.setIncrementalCol(rs.getString("incrementalCol"));
		listdatadefinition.setRecordAnomaly(rs.getString("recordAnomaly"));
		listdatadefinition.setStartDate(rs.getString("startDate"));
		listdatadefinition.setEndDate(rs.getString("endDate"));
		listdatadefinition.setTimelinessKey(rs.getString("timelinessKey"));
		listdatadefinition.setDefaultCheck(rs.getString("defaultCheck"));
		listdatadefinition.setDefaultValues(rs.getString("defaultValues"));
		listdatadefinition.setRecordAnomalyThreshold(rs.getDouble("recordAnomalyThreshold"));
		listdatadefinition.setBlend(rs.getString("blend"));
		listdatadefinition.setIdCol(rs.getInt("idCol"));
		listdatadefinition.setDataDrift(rs.getString("dataDrift"));
		listdatadefinition.setDataDriftThreshold(rs.getDouble("dataDriftThreshold"));
		listdatadefinition.setOutOfNormStat(rs.getString("outOfNormStat"));
		listdatadefinition.setOutOfNormStatThreshold(rs.getDouble("outOfNormStatThreshold"));
		listdatadefinition.setIsMasked(rs.getString("isMasked"));
		listdatadefinition.setPartitionBy(rs.getString("partitionBy"));
		listdatadefinition.setPatterns(rs.getString("patterns")==null?"":rs.getString("patterns"));
		listdatadefinition.setPatternCheck(rs.getString("patternCheck"));
		listdatadefinition.setDateRule(rs.getString("dateRule"));
		listdatadefinition.setbadData(rs.getString("badData"));
		listdatadefinition.setDateFormat(rs.getString("dateFormat")==null?"":rs.getString("dateFormat"));
		listdatadefinition.setLengthCheck(rs.getString("lengthCheck"));
		listdatadefinition.setMaxLengthCheck(rs.getString("maxLengthCheck")); // Max Length Check
		listdatadefinition.setLengthValue(rs.getString("lengthValue"));
		listdatadefinition.setLengthThreshold(rs.getDouble("lengthCheckThreshold"));
		listdatadefinition.setBadDataThreshold(rs.getDouble("badDataCheckThreshold"));
		listdatadefinition.setPatternCheckThreshold(rs.getDouble("patternCheckThreshold"));
		listdatadefinition.setDefaultPatternCheck(rs.getString("defaultPatternCheck"));
		listdatadefinition.setDefaultPatterns(rs.getString("defaultPatterns")==null?"":rs.getString("defaultPatterns"));
		listdatadefinition.setApplyRule(rs.getString("applyrule"));
		listdatadefinition.setCorrelationcolumn(rs.getString("correlationcolumn")==null?"":rs.getString("correlationcolumn"));
		return listdatadefinition;
	    }

	});
	return listdatadefinition;
    }

    @Override
    public int updateCheckValueIntoListDatadefinition(long idData, String checkName, String columnName,
	    String columnValue) {
	int success = 0;
	try {
	    // Query compatibility changes for both POSTGRES and MYSQL
	    List<String> threshold_columns_list = new ArrayList<String>();
	    threshold_columns_list.add("nullcountthreshold");
	    threshold_columns_list.add("numericalthreshold");
	    threshold_columns_list.add("stringstatthreshold");
	    threshold_columns_list.add("datadriftthreshold");
	    threshold_columns_list.add("recordanomalythreshold");
	    threshold_columns_list.add("outofnormstatthreshold");
	    threshold_columns_list.add("lengthcheckthreshold");
	    threshold_columns_list.add("baddatacheckthreshold");
	    threshold_columns_list.add("patterncheckthreshold");

		String getFormat = "select format from listDataDefinition where  idData = " + idData + " and trim(displayName)='"+columnName+"'";
		String format = jdbcTemplate.queryForObject(getFormat, String.class);
		if (checkName.equalsIgnoreCase("lengthCheck") || checkName.equalsIgnoreCase("maxLengthCheck")) {
			if (!(format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
					|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
					|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
					|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
					|| format.equalsIgnoreCase("NUMERIC"))) {
				return success;
			}
		}

		String sql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
		    && (threshold_columns_list.contains(checkName.toLowerCase()))) {
			double threshold_value = 0.0;
			if (columnValue != null) {
				threshold_value = Double.parseDouble(columnValue);
			}
			if (checkName.equalsIgnoreCase("lengthCheck")) {
				sql = "update listDataDefinition set " + checkName + "=" + threshold_value
						+ " ,lengthValue= '0' where idData=? and trim(displayName)=?";
				success = jdbcTemplate.update(sql, idData, columnName);
			}else if (checkName.equalsIgnoreCase("lengthCheckThreshold")
				|| checkName.equalsIgnoreCase("patterncheckthreshold")
				|| checkName.equalsIgnoreCase("baddatacheckthreshold")
				|| checkName.equalsIgnoreCase("outofnormstatthreshold")
				|| checkName.equalsIgnoreCase("datadriftthreshold")
				|| checkName.equalsIgnoreCase("stringstatthreshold")
				|| checkName.equalsIgnoreCase("numericalthreshold")
				|| checkName.equalsIgnoreCase("nullcountthreshold")) {
        			sql = "update listDataDefinition set " + checkName + "=? where idData=? and trim(displayName)=?";
        			success = jdbcTemplate.update(sql, threshold_value, idData, columnName);
			}  
			else if (checkName.equalsIgnoreCase("nullCountThreshold")) {
    			sql = "update listDataDefinition set " + checkName + "=? where idData=? and trim(displayName)=?";
    			success = jdbcTemplate.update(sql, threshold_value, idData, columnName);
		}  else if (checkName.equalsIgnoreCase("recordCountAnomalyThreshold"))
				success = 1;
			else {
				sql = "update listDataDefinition set " + checkName + "=? where idData=? and trim(displayName)=?";
				success = jdbcTemplate.update(sql, threshold_value, idData, columnName);
			}

		} else {
			if (checkName.equalsIgnoreCase("lengthCheck")) {
				sql = "update listDataDefinition set " + checkName
						+ "=?,lengthValue= '0' where idData=? and trim(displayName)=?";
				success = jdbcTemplate.update(sql, columnValue, idData, columnName);
			} else if (checkName.equalsIgnoreCase("recordCountAnomalyThreshold"))
				success = 1;
			  else {
				sql = "update listDataDefinition set " + checkName + "=? where idData=? and trim(displayName)=?";
				success = jdbcTemplate.update(sql, columnValue, idData, columnName);
			}
		}

	} catch (Exception e) {
	    LOG.error("\n====>Exception occurred while updating check value into listDataDefinition" + e.getMessage());
	    e.printStackTrace();
	}
	return success;
    }

    @Override
    public int updateCheckValueIntoStagingListDatadefinition(long idData, String checkName, String columnName,
	    String columnValue) {
	int success = 0;
	try {

		String getFormat = "select format from staging_listDataDefinition where  idData = " + idData + " and trim(displayName)='"+columnName+"'";
		String format = jdbcTemplate.queryForObject(getFormat, String.class);

		if (checkName.equalsIgnoreCase("lengthCheck") || checkName.equalsIgnoreCase("maxLengthCheck")) {
			if (!(format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("VARCHAR2")
					|| format.equalsIgnoreCase("string") || format.equalsIgnoreCase("int")
					|| format.equalsIgnoreCase("VARCHAR") || format.equalsIgnoreCase("NVARCHAR")
					|| format.equalsIgnoreCase("SMALLINT") || format.equalsIgnoreCase("INTEGER")
					|| format.equalsIgnoreCase("NUMERIC"))) {
				return success;
			}
		}

	    // Query compatibility changes for both POSTGRES and MYSQL
	    // For threshold columns
	    List<String> threshold_columns_list = new ArrayList<String>();
	    threshold_columns_list.add("nullcountthreshold");
	    threshold_columns_list.add("numericalthreshold");
	    threshold_columns_list.add("stringstatthreshold");
	    threshold_columns_list.add("datadriftthreshold");
	    threshold_columns_list.add("recordanomalythreshold");
	    threshold_columns_list.add("outofnormstatthreshold");
	    threshold_columns_list.add("lengthcheckthreshold");
	    threshold_columns_list.add("baddatacheckthreshold");
	    threshold_columns_list.add("patterncheckthreshold");

	    String sql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)
		    && (threshold_columns_list.contains(checkName.toLowerCase()))) {
		double threshold_value = 0.0;
		if (columnValue != null) {
		    threshold_value = Double.parseDouble(columnValue);
		}
		if (checkName.equalsIgnoreCase("lengthCheck")|| checkName.equalsIgnoreCase("nullCheck") ) {
		    sql = "update staging_listDataDefinition set " + checkName + "=" + threshold_value
			    + ", lengthValue='0' where idData=? and displayName=?";
		    success = jdbcTemplate.update(sql, idData, columnName);
		} else if (checkName.equalsIgnoreCase("recordCountAnomalyThreshold"))
			success = 1;
		    else {
				sql = "update staging_listDataDefinition set " + checkName + "=? where idData=? and displayName=?";
				success = jdbcTemplate.update(sql, threshold_value, idData, columnName);
			}
		} else {
			if (checkName.equalsIgnoreCase("lengthCheck") || checkName.equalsIgnoreCase("nullCheck")) {
				sql = "update staging_listDataDefinition set " + checkName
						+ "=?, lengthValue='0' where idData=? and displayName=?";
				success = jdbcTemplate.update(sql, columnValue, idData, columnName);
			} else if (checkName.equalsIgnoreCase("recordCountAnomalyThreshold"))
				success = 1;
				else {
					sql = "update staging_listDataDefinition set " + checkName + "=? where idData=? and displayName=?";
					success = jdbcTemplate.update(sql, columnValue, idData, columnName);
				}
		}

	} catch (Exception e) {
	    LOG.error("\n====>Exception occurred while updating check value into staging_listDataDefinition"
		    + e.getMessage());

	    e.printStackTrace();
	}
	return success;
    }

    @Override
    public int updatePatternIntoListDatadefinition(long idData, String checkName, String columnName, String columnValue,
	    String defaultPattern) {
	int success = 0;
	try {
	    String columnValueConcat = "," + columnValue;

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String sql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
		sql = "update listDataDefinition set " + checkName + "=  case when " + checkName + " is null or trim( "
			+ checkName + ") = '' then  '" + columnValue + "'" + "     else concat(" + checkName + ",'"
			+ columnValueConcat + "') end where idData=? and displayName=? and COALESCE(" + checkName
			+ ",'')  not like '%" + defaultPattern + "per:%'";
	    else
		sql = "update listDataDefinition set " + checkName + "=  case when " + checkName + " is null or trim( "
			+ checkName + ") = '' then  '" + columnValue + "'" + "     else concat(" + checkName + ",'"
			+ columnValueConcat + "') end where idData=? and displayName=? and ifnull(" + checkName
			+ ",'')  not like '%" + defaultPattern + "per:%'";

	    success = jdbcTemplate.update(sql, idData, columnName);
	    LOG.debug("\n====>sql" + sql);

	} catch (Exception e) {
	    LOG.error("\n====>Exception occurred while updating check value into listDataDefinition" + e.getMessage());
	    e.printStackTrace();
	}
	if (success == 0) {
	    String countSql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
		countSql = "select count(*) as count from listDataDefinition where idData=" + idData
			+ " and displayName='" + columnName + "' and COALESCE(" + checkName + ",'') like '%"
			+ defaultPattern + "per:%'";
	    } else {
		countSql = "select count(*) as count from listDataDefinition where idData=" + idData
			+ " and displayName='" + columnName + "' and ifnull(" + checkName + ",'') like '%"
			+ defaultPattern + "per:%'";
	    }
	    SqlRowSet rowSet = jdbcTemplate.queryForRowSet(countSql);
	    while (rowSet.next()) {
		return rowSet.getInt("count");
	    }
	}
	return success;
    }

    @Override
    public int updatePatternIntoStagingListDatadefinition(long idData, String checkName, String columnName,
	    String columnValue, String defaultPattern) {
	int success = 0;
	try {
	    String columnValueConcat = "," + columnValue;

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String sql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
		sql = "update staging_listDataDefinition set " + checkName + "=  case when " + checkName
			+ " is null or trim( " + checkName + ") = '' then  '" + columnValue + "'" + "     else concat("
			+ checkName + ",'" + columnValueConcat + "') end where idData=? and displayName=? and COALESCE("
			+ checkName + ",'') not like '%" + defaultPattern + "per:%'";
	    else
		sql = "update staging_listDataDefinition set " + checkName + "=  case when " + checkName
			+ " is null or trim( " + checkName + ") = '' then  '" + columnValue + "'" + "     else concat("
			+ checkName + ",'" + columnValueConcat + "') end where idData=? and displayName=? and ifnull("
			+ checkName + ",'') not like '%" + defaultPattern + "per:%'";

	    success = jdbcTemplate.update(sql, idData, columnName);
	} catch (Exception e) {
	    LOG.error("\n====>Exception occurred while updating check value into staging_listDataDefinition"
		    + e.getMessage());
	    e.printStackTrace();
	}
	if (success == 0) {
	    String countSql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
		countSql = "select count(*) as count from staging_listDataDefinition where idData=" + idData
			+ " and displayName='" + columnName + "' and COALESCE(" + checkName + ",'') like '%"
			+ defaultPattern + "per:%'";
	    } else {
		countSql = "select count(*) as count from staging_listDataDefinition where idData=" + idData
			+ " and displayName='" + columnName + "' and ifnull(" + checkName + ",'') like '%"
			+ defaultPattern + "per:%'";
	    }

	    LOG.debug("countSql " + countSql);
	    SqlRowSet rowSet = jdbcTemplate.queryForRowSet(countSql);
	    while (rowSet.next()) {
		return rowSet.getInt("count");
	    }
	}
	return success;
    }

    @Override
    public void copyDerivedTemplate(long idData, long newIdData, String createdByUser, String newTemplateName) {
	try {
	    String datasourceSql = ("insert into listDerivedDataSources(idData, name, description ,template1Name, template1IdData, template1AliasName, template2Name, template2IdData, template2AliasName, queryText, createdBy, createdAt, updatedAt, updatedBy, project_id, createdByUser)"
		    + " (select " + newIdData + " as idData, '" + newTemplateName
		    + "' as name, description ,template1Name, template1IdData, template1AliasName, template2Name, template2IdData, template2AliasName, queryText, createdBy, now() as createdAt, now() as updatedAt, updatedBy, project_id, '"
		    + createdByUser + "' as createdByUser from listDerivedDataSources where idData=" + idData + ")");

	    LOG.debug("datasourceSql " + datasourceSql);
	    jdbcTemplate.update(datasourceSql);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
    }

	@Override
	public List<listDataAccess> getDataFromListDataAccessToExport(Long idData) {
		String query = "select * from listDataAccess where idData=" + idData;
		List<listDataAccess> listdataaccess = jdbcTemplate.query(query, new RowMapper<listDataAccess>() {
			public listDataAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
				listDataAccess lda = new listDataAccess();
				lda.setIdlistDataAccess(rs.getLong("idlistDataAccess"));
				lda.setIdData(rs.getLong("idData"));
				lda.setHostName(rs.getString("hostName"));
				lda.setPortName(rs.getString("portName"));
				lda.setUserName(rs.getString("userName"));
				lda.setPwd(rs.getString("pwd"));
				lda.setSchemaName(rs.getString("schemaName"));
				lda.setFolderName(rs.getString("folderName"));
				lda.setQueryString(rs.getString("queryString"));
				lda.setQuery(rs.getString("query"));
				lda.setIdDataSchema(rs.getLong("idDataSchema"));
				lda.setWhereCondition(rs.getString("whereCondition"));
				lda.setDomain(rs.getString("domain"));

				lda.setIncrementalType(rs.getString("incrementalType"));
				lda.setDateFormat(rs.getString("dateFormat"));
				lda.setSliceStart(rs.getString("sliceStart"));
				lda.setSliceEnd(rs.getString("sliceEnd"));
				lda.setFileHeader(rs.getString("fileHeader"));
				lda.setIsRawData(rs.getString("isRawData"));
				lda.setHivejdbcport(rs.getString("hivejdbchost"));

				lda.setHivejdbcport(rs.getString("hivejdbcport"));
				lda.setSslEnb(rs.getString("sslEnb"));
				lda.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				lda.setTrustPassword(rs.getString("trustPassword"));
				lda.setMetaData(rs.getString("metaData"));
				lda.setGatewayPath(rs.getString("gatewayPath"));

				lda.setJksPath(rs.getString("jksPath"));
				lda.setZookeeperUrl(rs.getString("zookeeperUrl"));
				lda.setRollingHeader(rs.getString("rollingHeader"));
				lda.setRollingColumn(rs.getString("rollingColumn"));
				lda.setHistoricDateTable(rs.getString("historicDateTable"));

				return lda;
			}
		});
		return listdataaccess;
	}
}