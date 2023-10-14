package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ColumnCombinationProfile_DP;
import com.databuck.bean.ColumnProfileDetails_DP;
import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.ListAdvancedRules;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.NumericalProfile_DP;
import com.databuck.bean.Project;
import com.databuck.bean.RowProfile_DP;
import com.databuck.bean.listDataAccess;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.service.IProjectService;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.databuck.util.ToCamelCase;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ListDataSourceDAOImpl implements IListDataSourceDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private IProjectService IProjectservice;

	private static final Logger LOG = Logger.getLogger(ListDataSourceDAOImpl.class);

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean deactivateSchema(long idDataSchema) {
		boolean status = false;
		try {

			// Deactivate connection
			int update = jdbcTemplate.update("update listDataSchema set Action = 'No' WHERE idDataSchema = ?",
					new Object[] { idDataSchema });

			if (update > 0)
				status = true;

			// Get all associated templates
			String idDataList = "";
			String idDataListSql = "select idData as idData from listDataSources where idDataSchema=" + idDataSchema;
			SqlRowSet rs = jdbcTemplate.queryForRowSet(idDataListSql);
			while (rs.next()) {
				idDataList = idDataList + rs.getString("idData") + ", ";
			}
			idDataList = idDataList.replaceAll(", $", "");

			// Deactivate associated templates
			if (idDataList != "" && !idDataList.trim().isEmpty()) {
				String sql = "update listDataSources set active='no' where idData in (" + idDataList + ")";
				int count = jdbcTemplate.update(sql);

				// Get all associated validations
				String idAppList = "";
				String getAllIdApps = "select idApp as idApp from listApplications where idData in (" + idDataList
						+ ")";
				SqlRowSet rs1 = jdbcTemplate.queryForRowSet(getAllIdApps);
				while (rs1.next()) {
					idAppList = idAppList + rs1.getString("idApp") + ", ";
				}
				idAppList = idAppList.replaceAll(", $", "");

				// Deactivate associated validations
				if (idAppList != null && !idAppList.trim().isEmpty()) {
					String deleteValiationSql = "update listApplications set active='no' where idApp in (" + idAppList
							+ ")";
					int count1 = jdbcTemplate.update(deleteValiationSql);
				}

				// Get all associated custom rules
				String idListColrules = "";
				String idListColrulesSql = "select idListColrules as idListColrules from listColRules where idData in ("
						+ idDataList + ")";
				SqlRowSet rs2 = jdbcTemplate.queryForRowSet(idListColrulesSql);
				while (rs2.next()) {
					idListColrules = idListColrules + rs2.getString("idListColrules") + ", ";
				}
				idListColrules = idListColrules.replaceAll(", $", "");

				// Deactivate associated custom rules
				if (idListColrules != "") {
					String deleteStatement = "update listColRules set activeFlag='N' WHERE idListColrules in ("
							+ idListColrules + ")";
					int update1 = jdbcTemplate.update(deleteStatement);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean deactivateTemplate(long idData) {
		boolean status = false;
		try {

			String sql = "update listDataSources set active='no' where idData in (" + idData + ")";
			int count = jdbcTemplate.update(sql);
			if (count > 0)
				status = true;

			// Get all associated validations
			String idAppList = "";
			String getAllIdApps = "select idApp as idApp from listApplications where idData in (" + idData + ")";
			SqlRowSet rs1 = jdbcTemplate.queryForRowSet(getAllIdApps);
			while (rs1.next()) {
				idAppList = idAppList + rs1.getString("idApp") + ", ";
			}
			idAppList = idAppList.replaceAll(", $", "");

			// Deactivate associated validations
			if (idAppList != null && !idAppList.trim().isEmpty()) {
				String deleteValiationSql = "update listApplications set active='no' where idApp in (" + idAppList
						+ ")";
				int count1 = jdbcTemplate.update(deleteValiationSql);
			}

			// Get all associated custom rules
			String idListColrules = "";
			String idListColrulesSql = "select idListColrules as idListColrules from listColRules where idData in ("
					+ idData + ")";
			SqlRowSet rs2 = jdbcTemplate.queryForRowSet(idListColrulesSql);
			while (rs2.next()) {
				idListColrules = idListColrules + rs2.getString("idListColrules") + ", ";
			}
			idListColrules = idListColrules.replaceAll(", $", "");

			// Deactivate associated custom rules
			if (idListColrules != "") {
				String deleteStatement = "update listColRules set activeFlag='N' WHERE idListColrules in ("
						+ idListColrules + ")";
				int update1 = jdbcTemplate.update(deleteStatement);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean activateSchema(long idDataSchema) {
		boolean status = false;
		try {

			// Activate connection
			int update = jdbcTemplate.update("update listDataSchema set Action = 'Yes' WHERE idDataSchema = ?",
					new Object[] { idDataSchema });

			if (update > 0)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean activateTemplate(long idData) {
		boolean status = false;
		try {
			// Activate associated templates
			String sql = "update listDataSources set active='yes' where idData in (" + idData + ")";
			int update = jdbcTemplate.update(sql);
			if (update > 0) {
				status = true;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;
	}

	public List<ListDataSource> getListDataSourceTable(Long projectId, List<Project> projlist, String fromDate,
			String toDate) {
		// String sql = "SELECT idData,name, description, dataLocation,
		// dataSource,createdAt,createdBy from listDataSources ";
		// String sql="select * from listDataSources where active='yes'";
		// Added as part of derived template changes
		String sql = "";
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlist);
		sql = sql
				+ "SELECT name, foldername, listDataSources.idData,description,dataLocation,dataSource,template_create_success,deltaApprovalStatus,profilingEnabled,advancedRulesEnabled,listDataSources.createdAt,createdBy,listDataSources.updatedAt,createdByUser, project.idProject as projectId, project.projectName ";
		sql = sql + "FROM listDataSources ";
		sql = sql + "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData ";
		sql = sql + "INNER JOIN project ON listDataSources.project_id = project.idProject ";

		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "WHERE listDataSources.createdAt >= '" + toDate + "' and listDataSources.createdAt <= ('"
						+ fromDate
						+ "'::DATE + '1 day'::INTERVAL) and listDataSources.active =  'yes' and listDataSources.project_id in ( "
						+ projectId + " ) ";
			} else {
				sql = sql + "WHERE listDataSources.createdAt >= '" + toDate
						+ "' and listDataSources.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY) and listDataSources.active =  'yes' and listDataSources.project_id in ( "
						+ projectId + " ) ";
			}

		} else {
			sql = sql + "WHERE listDataSources.active =  'yes' and listDataSources.project_id in ( " + projectId
					+ " ) ";
		}
		sql = sql + "union ";
		sql = sql
				+ "SELECT a.name,concat(template1Name,' , ',template2Name) foldername, a.idData,b.description,a.dataLocation,a.dataSource,a.template_create_success,a.deltaApprovalStatus,a.profilingEnabled,a.advancedRulesEnabled,a.createdAt,a.createdBy,a.updatedAt,a.createdByUser, project.idProject as projectId, project.projectName ";
		sql = sql + "FROM listDataSources a ";
		sql = sql + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData ";
		sql = sql + "INNER JOIN project ON a.project_id = project.idProject ";

		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "WHERE a.createdAt >= '" + toDate + "' and a.createdAt <= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL) and a.active = 'yes' and a.dataLocation =  'Derived'  and a.project_id  in ( "
						+ projectId + " );";
			} else {
				sql = sql + "WHERE a.createdAt >= '" + toDate + "' and a.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY) and a.active = 'yes' and a.dataLocation =  'Derived'  and a.project_id  in ( "
						+ projectId + " );";
			}

		} else {
			sql = sql + "WHERE a.active =  'yes'and a.dataLocation =  'Derived'  and a.project_id  in ( " + projectId
					+ " );";
		}
		LOG.debug("getListDataSourceTable sql :" + sql);
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
				alistdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setUpdatedAt(rs.getDate("updatedAt"));
				// alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	public List<ListDataSource> getListDataSourceTableId(long idData) {
		// String sql = "SELECT idData,name, description, dataLocation,
		// dataSource,createdAt,createdBy from listDataSources ";
		// String sql="select * from listDataSources where active='yes'";
		String sql = "SELECT name, foldername, listDataSources.idData,description,dataLocation,dataSource,createdAt,createdBy,template_create_success,deltaApprovalStatus,profilingEnabled,advancedRulesEnabled "
				+ "FROM listDataSources INNER JOIN listDataAccess ON "
				+ "listDataSources.idData = listDataAccess.idData WHERE listDataSources.active =  'yes' AND listDataAccess.idData="
				+ idData;
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
				alistdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
				alistdatasource.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
				// alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	public List<ListDataSource> getListDataSource(long idDataSchema) {

		LOG.debug("idDataSchema:" + idDataSchema);
		String sql = "SELECT idData,name, description, dataLocation, dataSource,createdAt,idDataSchema,profilingEnabled,advancedRulesEnabled,template_create_success,deltaApprovalStatus from listDataSources where idDataSchema ="
				+ idDataSchema + " order by idData desc";
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
				alistdatasource.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
				return alistdatasource;
			}

		});

		return listdatasource;

	}

	public List<ListApplications> getDataFromListApplicationsforValidationCheckAddNewCustomize(
			List<Long> listApplicationsIdList, String dataBaseSchema) {
		List<ListApplications> listOfListApplicationObjs = new ArrayList<ListApplications>();
		for (Long idApp : listApplicationsIdList) {
			LOG.debug("idApp is :" + idApp);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet("select * from listApplications where idApp=?",
					idApp);
			while (queryForRowSet.next()) {
				ListApplications listApplications = new ListApplications();
				listApplications.setIdApp(queryForRowSet.getLong("idApp"));
				listApplications.setName(queryForRowSet.getString("name"));
				listApplications.setDescription(queryForRowSet.getString("description"));
				listApplications.setAppType(queryForRowSet.getString("appType"));
				listApplications.setIdData(queryForRowSet.getLong("idData"));
				listApplications.setIdRightData(queryForRowSet.getInt("idRightData"));
				listApplications.setCreatedBy(queryForRowSet.getString("createdBy"));
				listApplications.setCreatedAt(queryForRowSet.getString("createdAt"));
				listApplications.setUpdatedAt(queryForRowSet.getString("updatedAt"));
				listApplications.setUpdatedBy(queryForRowSet.getString("updatedBy"));
				// String schema = jdbcTemplate.queryForObject("select
				// schemaName from listDataAccess where
				// idData=?",String.class,queryForRowSet.getInt("idData"));
				listApplications.setSchema(dataBaseSchema);
				listOfListApplicationObjs.add(listApplications);
			}
		}
		return listOfListApplicationObjs;
	}

	public Map<Long, String> getDataTemplateForRequiredLocation(String location, Long projectId, Integer domainId) {
		try {
			String sql = "SELECT idDataSchema,schemaName from listDataSchema where lower(schemaType)=lower('" + location
					+ "') and Action = 'Yes'  and project_id = " + projectId + " and domain_id = " + domainId;
			Map<Long, String> mapObject = new LinkedHashMap<Long, String>();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				mapObject.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return mapObject;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<Long, String> getConnectionsByDomainProject(Integer domainId, Long projectId) {
		try {
			String sql = "SELECT idDataSchema,schemaName from listDataSchema where domain_id = " + domainId
					+ " and project_id = " + projectId + " and Action = 'Yes'";
			Map<Long, String> mapObject = new LinkedHashMap<Long, String>();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				mapObject.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return mapObject;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	public Map<Long, String> getRollDataTargetSchemaList(Long projectId) {
		try {
			String sql = "SELECT idDataSchema,schemaName from listDataSchema where Action = 'Yes' and schemaType in ('Postgres') and project_id = "
					+ projectId;
			Map<Long, String> mapObject = new LinkedHashMap<Long, String>();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				mapObject.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
			}
			return mapObject;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	public List<ListDataSchema> getListDataSchema(Long project_id, List<Project> projList, String fromDate,
			String toDate) {
		String sql = "";
		String projIds = "";
		if (projList != null && !projList.isEmpty()) {
			projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		}
		LOG.debug("in getListDataSchema : project_Ids=" + projIds);

		sql = sql + "SELECT listDataSchema.*,project.projectName from listDataSchema ";
		sql = sql + "INNER JOIN project ON listDataSchema.project_id = project.idProject ";
		if (fromDate != null && !fromDate.trim().isEmpty() && toDate != null && !toDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "where listDataSchema.createdAt >= '" + toDate + "' and listDataSchema.createdAt <= ('"
						+ fromDate + "'::DATE + '1 day'::INTERVAL) and project_id in (  " + project_id
						+ " ) and  Action = 'Yes'";
			} else {
				sql = sql + "where listDataSchema.createdAt >= '" + toDate
						+ "' and listDataSchema.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY) and project_id in (  " + project_id + " ) and  Action = 'Yes'";
			}

		} else {

			sql = sql + "where project_id in (  " + project_id + " ) and  Action = 'Yes'";
		}

		LOG.debug("sql: " + sql);

		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setProjectName(rs.getString("projectName"));
				alistdataschema.setAction(rs.getString("Action"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	// This method will return all active and inactive connections
	@Override
	public List<ListDataSchema> getAllActiveAndInActiveConnections(Long project_id, List<Project> projList,
			String fromDate, String toDate) {
		String sql = "";
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projList);
		LOG.debug("in getListDataSchema : project_Ids=" + projIds);

		sql = "SELECT t1.*,t2.projectName,t3.domainName from listDataSchema t1, project t2, domain t3 where "
				+ "t1.project_id = t2.idProject and t1.domain_id = t3.domainId and ";

		if (fromDate != null && !fromDate.trim().isEmpty() && toDate != null && !toDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "t1.createdAt >= '" + toDate + "' and t1.createdAt <= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL) and t1.project_id in (  " + project_id + " )";
			} else {
				sql = sql + "t1.createdAt >= '" + toDate + "' and t1.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY) and t1.project_id in (  " + project_id + " )";
			}

		} else {

			sql = sql + "project_id in (  " + project_id + " )";
		}

		LOG.debug("sql: " + sql);

		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setProjectName(rs.getString("projectName"));
				alistdataschema.setAction(rs.getString("Action"));
				alistdataschema.setDomainName(rs.getString("domainName"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				alistdataschema.setReadLatestPartition(rs.getString("readLatestPartition"));
				alistdataschema.setAlation_integration_enabled(rs.getString("alation_integration_enabled"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	public List<ListDataSchema> getListDataSchemaId(long idDataSchema) {
		String sql = "SELECT * from listDataSchema where idDataSchema=" + idDataSchema;
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setAction(rs.getString("Action"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	public List<ListDataSchema> getListDataSchemaForIdDataSchema(long idDataSchema) {
		String sql = "SELECT * from listDataSchema where idDataSchema=" + idDataSchema;
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setHivejdbchost(rs.getString("hivejdbchost"));
				alistdataschema.setHivejdbcport(rs.getString("hivejdbcport"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				alistdataschema.setGatewayPath(rs.getString("gatewayPath"));
				alistdataschema.setJksPath(rs.getString("jksPath"));
				alistdataschema.setZookeeperUrl(rs.getString("zookeeperUrl"));
				alistdataschema.setFolderPath(rs.getString("folderPath"));
				alistdataschema.setFileNamePattern(rs.getString("fileNamePattern"));
				alistdataschema.setFileDataFormat(rs.getString("fileDataFormat"));
				alistdataschema.setHeaderPresent(rs.getString("headerPresent"));
				alistdataschema.setHeaderFilePath(rs.getString("headerFilePath"));
				alistdataschema.setHeaderFileNamePattern(rs.getString("headerFileNamePattern"));
				alistdataschema.setHeaderFileDataFormat(rs.getString("headerFileDataFormat"));
				alistdataschema.setAccessKey(rs.getString("accessKey"));
				alistdataschema.setSecretKey(rs.getString("secretKey"));
				alistdataschema.setBucketName(rs.getString("bucketName"));
				alistdataschema.setBigQueryProjectName(rs.getString("bigQueryProjectName"));
				alistdataschema.setPrivatekey(rs.getString("privateKey"));
				alistdataschema.setPrivatekeyId(rs.getString("privateKeyId"));
				alistdataschema.setClientEmail(rs.getString("clientEmail"));
				alistdataschema.setClientId(rs.getString("clientId"));
				alistdataschema.setDatasetName(rs.getString("datasetName"));
				alistdataschema.setAzureClientId(rs.getString("azureClientId"));
				alistdataschema.setAzureClientSecret(rs.getString("azureClientSecret"));
				alistdataschema.setAzureTenantId(rs.getString("azureTenantId"));
				alistdataschema.setAzureServiceURI(rs.getString("azureServiceURI"));
				alistdataschema.setAzureFilePath(rs.getString("azureFilePath"));
				alistdataschema.setPartitionedFolders(rs.getString("partitionedFolders"));
				alistdataschema.setMaxFolderDepth(rs.getInt("maxFolderDepth"));
				alistdataschema.setMultiPattern(rs.getString("multiPattern"));
				alistdataschema.setStartingUniqueCharCount(rs.getInt("startingUniqueCharCount"));
				alistdataschema.setEndingUniqueCharCount(rs.getInt("endingUniqueCharCount"));
				alistdataschema.setFileEncrypted(rs.getString("fileEncrypted"));
				alistdataschema.setCreatedBy(rs.getLong("createdBy"));
				alistdataschema.setCreatedByUser(rs.getString("createdByUser"));
				alistdataschema.setProjectId(rs.getLong("project_id"));
				alistdataschema.setSingleFile(rs.getString("singleFile"));
				alistdataschema.setExtenalFileNamePattern(rs.getString("externalfileNamePattern"));
				alistdataschema.setExtenalFileName(rs.getString("externalfileName"));
				alistdataschema.setPatternColumn(rs.getString("patternColumn"));
				alistdataschema.setHeaderColumn(rs.getString("headerColumn"));
				alistdataschema.setLocalDirectoryColumnIndex(rs.getString("localDirectoryColumnIndex"));
				alistdataschema.setXsltFolderPath(rs.getString("xsltFolderPath"));
				alistdataschema.setKmsAuthDisabled(rs.getString("kmsAuthDisabled"));
				alistdataschema.setPushDownQueryEnabled(rs.getString("push_down_query_enabled"));
				alistdataschema.setDomainId(rs.getInt("domain_id"));
				alistdataschema.setReadLatestPartition(rs.getString("readLatestPartition"));
				alistdataschema.setAlation_integration_enabled(rs.getString("alation_integration_enabled"));
				alistdataschema.setEnableFileMonitoring(rs.getString("enableFileMonitoring"));
				alistdataschema.setIncrementalDataReadEnabled(rs.getString("incremental_dataread_enabled"));
				alistdataschema.setClusterPropertyCategory(rs.getString("cluster_property_category"));
				alistdataschema.setMultiFolderEnabled(rs.getString("multiFolderEnabled"));
				return alistdataschema;

			}
		});
		return listdataschema;
	}

	/**
	 *
	 *
	 * Delete the listDataSource first view the dataSource
	 */
	public ListDataSource delete(int idData) {

		String sql = "SELECT idData,name, description, dataLocation, dataSource,createdAt from listDataSources where idData="
				+ idData;
		return jdbcTemplate.query(sql, new ResultSetExtractor<ListDataSource>() {

			public ListDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListDataSource listdatasource = new ListDataSource();
					listdatasource.setIdData(rs.getInt("idData"));
					listdatasource.setName(rs.getString("name"));
					listdatasource.setDescription(rs.getString("description"));
					listdatasource.setDataLocation(rs.getString("dataLocation"));
					listdatasource.setDataSource(rs.getString("dataSource"));
					listdatasource.setCreatedAt(rs.getDate("createdAt"));
					return listdatasource;
				}

				return null;
			}

		});

	}

	// added method for getting schemaName by pravin

	public String getSchemaNameByIdData(long idData) {

		String sql = "select schemaName from listDataSchema where idDataSchema=" + idData;
		LOG.debug("SQL  getSchemaNameByIdData=>" + sql);

		return jdbcTemplate.queryForObject(sql, String.class);
	}

	/**
	 *
	 *
	 * Delete DataSource Completely
	 */

	@Override
	public int deleteDataSource(int idData) {
		ListDataSource listDataSource = getDataFromListDataSourcesOfIdData((long) idData);
		// String sql = "DELETE FROM listDataSources WHERE idData=?";
		String sql = "update listDataSources set active='no' where idData=?";
		int count = jdbcTemplate.update(sql, idData);
		LOG.debug("listDataSource Delete Success" + idData);
		// get all appId related to this
		String idAppList = "";
		String getAllIdApps = "select idApp as idApp from listApplications where idData=" + idData;
		SqlRowSet rs = jdbcTemplate.queryForRowSet(getAllIdApps);
		while (rs.next()) {
			idAppList = idAppList + rs.getString("idApp") + ", ";
		}
		idAppList = idAppList.replaceAll(", $", "");
		if (idAppList != "") {
			// String deleteValiationSql = "update listApplications set active='no' where
			// idApp in (" + idAppList + ")";

			String deleteValiationSql = "DELETE FROM listApplications where idApp in (" + idAppList + ")";
			int count1 = jdbcTemplate.update(deleteValiationSql);

			// get all idListColrules
			String idListColrules = "";
			String idListColrulesSql = "select idListColrules as idListColrules from listColRules where idData in ("
					+ idData + ")";
			SqlRowSet rs2 = jdbcTemplate.queryForRowSet(idListColrulesSql);
			while (rs2.next()) {
				idListColrules = idListColrules + rs2.getString("idListColrules") + ", ";
			}
			idListColrules = idListColrules.replaceAll(", $", "");
			if (idListColrules != "") {
				String deleteStatement = "DELETE FROM listColRules WHERE idListColrules in (" + idListColrules + ")";
				int update1 = jdbcTemplate.update(deleteStatement);
			}
		}
		if (listDataSource.getName().equalsIgnoreCase("ref_non_aggregate_mapping_table")) {
			sql = "delete from listDataSources where idData=?";
			jdbcTemplate.update(sql, idData);
		}

		return count;
	}

	public ListDataSource getDataFromListDataSourcesOfIdData(Long idData) {

		String sql = "SELECT idData, idDataSchema, createdBy, name, description, dataLocation, dataSource,createdAt,ignoreRowsCount, domain_id, project_id, advancedRulesEnabled, profilingEnabled, template_create_success, deltaApprovalStatus, createdByUser,active from listDataSources where idData="
				+ idData;
		return jdbcTemplate.query(sql, new ResultSetExtractor<ListDataSource>() {

			public ListDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListDataSource listdatasource = new ListDataSource();
					listdatasource.setIdData(rs.getInt("idData"));
					listdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
					listdatasource.setName(rs.getString("name"));
					listdatasource.setDescription(rs.getString("description"));
					listdatasource.setDataLocation(rs.getString("dataLocation"));
					listdatasource.setDataSource(rs.getString("dataSource"));
					listdatasource.setCreatedAt(rs.getTimestamp("createdAt"));
					listdatasource.setIgnoreRowsCount(rs.getLong("ignoreRowsCount"));
					listdatasource.setGarbageRows(rs.getLong("ignoreRowsCount"));
					listdatasource.setDomain(rs.getInt("domain_id"));
					listdatasource.setProjectId(rs.getInt("project_id"));
					listdatasource.setAdvancedRulesEnabled("advancedRulesEnabled");
					listdatasource.setProfilingEnabled("profilingEnabled");
					listdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
					listdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
					listdatasource.setCreatedByUser(rs.getString("createdByUser"));
					listdatasource.setActive(rs.getString("active"));
					listdatasource.setCreatedBy(rs.getLong("createdBy"));
					return listdatasource;
				}
				return null;
			}
		});
	}

	// Added as part of derived template changes
	public ListDerivedDataSource getDataFromListDerivedDataSourcesOfIdData(Long idData) {

		String sql = "SELECT idDerivedData,idData,name,description, template1Name, template2Name, template1AliasName, template2AliasName,"
				+ "template1IdData, template2IdData,createdAt,querytext from listDerivedDataSources where idData="
				+ idData;
		return jdbcTemplate.query(sql, new ResultSetExtractor<ListDerivedDataSource>() {

			public ListDerivedDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListDerivedDataSource listderiveddatasource = new ListDerivedDataSource();
					listderiveddatasource.setIdDerivedData(rs.getInt("idDerivedData"));
					listderiveddatasource.setIdData(rs.getInt("idData"));
					listderiveddatasource.setName(rs.getString("name"));
					listderiveddatasource.setDescription(rs.getString("description"));
					listderiveddatasource.setTemplate1Name(rs.getString("template1Name"));
					listderiveddatasource.setTemplate2Name(rs.getString("template2Name"));
					listderiveddatasource.setTemplate1IdData(rs.getInt("template1IdData"));
					listderiveddatasource.setTemplate2IdData(rs.getInt("template2IdData"));
					listderiveddatasource.setTemplate1AliasName(rs.getString("template1AliasName"));
					listderiveddatasource.setTemplate2AliasName(rs.getString("template2AliasName"));
					listderiveddatasource.setQueryText(rs.getString("queryText"));
					listderiveddatasource.setCreatedAt(rs.getDate("createdAt"));

					return listderiveddatasource;
				}
				return null;
			}
		});
	}

	public listDataAccess getListDataAccess(Long iData) {
		RowMapper<listDataAccess> rowMapper = (rs, i) -> {
			listDataAccess lda = new listDataAccess();
			lda.setIdlistDataAccess(rs.getLong("idlistDataAccess"));
			lda.setIdData(rs.getLong("idData"));
			lda.setHostName(rs.getString("hostName"));
			lda.setPortName(rs.getString("portName"));
			lda.setUserName(rs.getString("userName"));
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
			String decryptedText = decryptor.decrypt(rs.getString("pwd"));
			lda.setPwd(decryptedText);
			lda.setSchemaName(rs.getString("schemaName"));
			lda.setFolderName(rs.getString("folderName"));
			lda.setQuery(rs.getString("query"));
			lda.setQueryString(rs.getString("queryString"));
			lda.setIncrementalType(rs.getString("incrementalType"));
			lda.setIdDataSchema(rs.getLong("idDataSchema"));
			lda.setWhereCondition(rs.getString("whereCondition"));
			lda.setDomain(rs.getString("domain"));
			lda.setDateFormat(rs.getString("dateFormat"));
			lda.setSliceStart(rs.getString("sliceStart"));
			lda.setSliceEnd(rs.getString("sliceEnd"));
			lda.setFileHeader(rs.getString("fileHeader"));
			lda.setRollingHeader(rs.getString("rollingHeader"));
			lda.setRollingColumn(rs.getString("rollingColumn"));
			lda.setHistoricDateTable(rs.getString("historicDateTable"));
			// lda.setKeytab(rs.getString("keytab"));
			return lda;
		};
		String sql = "select * from listDataAccess where  idData=?";
		List<listDataAccess> rlist = jdbcTemplate.query(sql, rowMapper, iData);
		if (rlist != null && rlist.size() > 0) {
			return rlist.get(0);
		}
		return null;
	}

	public boolean updateDataTemplate(String sql_lds, String sql_lda) {
		try {
			int[] batchUpdate = jdbcTemplate.batchUpdate(sql_lds, sql_lda);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
			return false;
		}
	}

	// Added as part of derived template changes
	public boolean updateDerivedDataTemplate(String sql_ldds) {
		try {
			LOG.debug(sql_ldds);
			int[] batchUpdate = jdbcTemplate.batchUpdate(sql_ldds);
			if (batchUpdate.length > 0)
				return true;
			else
				return false;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
			return false;
		}
	}

	// added for export func. 14jan2019
	public List<ListDataSchema> getListDataSchemaForExport() {
		String sql = "SELECT * from listDataSchema order by idDataSchema desc";
		List<ListDataSchema> listdataschema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			@Override
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema alistdataschema = new ListDataSchema();
				alistdataschema.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdataschema.setIpAddress(rs.getString("ipAddress"));
				alistdataschema.setDatabaseSchema(rs.getString("databaseSchema"));
				alistdataschema.setUsername(rs.getString("username"));
				alistdataschema.setPassword(rs.getString("password"));
				alistdataschema.setPort(rs.getString("port"));
				alistdataschema.setSchemaName(rs.getString("schemaName"));
				alistdataschema.setSchemaType(rs.getString("schemaType"));
				alistdataschema.setDomain(rs.getString("domain"));
				alistdataschema.setKeytab(rs.getString("gss_jaas"));
				alistdataschema.setKrb5conf(rs.getString("krb5conf"));
				alistdataschema.setSslEnb(rs.getString("sslEnb"));
				alistdataschema.setSslTrustStorePath(rs.getString("sslTrustStorePath"));
				alistdataschema.setTrustPassword(rs.getString("trustPassword"));
				return alistdataschema;
			}
		});
		return listdataschema;
	}

	/*-- Changes for Export 15Jan2019 priyanka --*/
	public List<ListDataSource> getListDataSourceTableForExport() {
		// String sql = "SELECT idData,name, description, dataLocation,
		// dataSource,createdAt,createdBy from listDataSources ";
		// String sql="select * from listDataSources where active='yes'";
		String sql = "SELECT name, foldername, listDataSources.idData,description,dataLocation,dataSource,createdAt,createdBy "
				+ "FROM listDataSources INNER JOIN listDataAccess ON "
				+ "listDataSources.idData = listDataAccess.idData WHERE listDataSources.active =  'yes' order by listDataSources.idData desc";
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				// alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	// added schemaName For profiling UI chng.
	public List<ListDataSource> getListDataSourceTableForProfiling(Long projectId, List<Project> projlst,
			String fromDate, String toDate) {

		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);

		String sql = "(SELECT ld.name, lda.foldername,lds.schemaName, ld.idData,ld.description,ld.dataLocation,ld.dataSource,ld.createdAt,ld.createdBy, ld.project_id as projectId, p.projectName,ld.template_create_success ";
		sql = sql + "FROM listDataSources ld ";
		sql = sql + "INNER JOIN listDataSchema lds ON ld.idDataSchema = lds.idDataSchema ";
		sql = sql + "JOIN listDataAccess lda ON ld.idData = lda.idData ";
		sql = sql + "JOIN project p on ld.project_id = p.idProject ";
		sql = sql + "WHERE ld.active =  'yes' and ld.profilingEnabled =  'Y' and ld.project_id in ( " + projectId
				+ " ) ";

		// Apply Date filter
		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL)";
			} else {
				sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY)";
			}
		}
		sql = sql + ") union ";
		sql = sql
				+ "(select ld.name, lda.foldername,'' as schemaName, ld.idData,ld.description,ld.dataLocation,ld.dataSource,ld.createdAt,ld.createdBy, ld.project_id as projectId, p.projectName,ld.template_create_success ";
		sql = sql + "FROM listDataSources ld ";
		sql = sql + "LEFT JOIN listDataAccess lda ON ld.idData = lda.idData ";
		sql = sql + "JOIN project p on ld.project_id = p.idProject ";
		sql = sql
				+ "WHERE ld.active =  'yes' and ld.profilingEnabled = 'Y' and (ld.idDataSchema = -1 or ld.idDataSchema = -3 or ld.idDataSchema = 0 or ld.idDataSchema is Null) ";
		sql = sql + "and ld.project_id in ( " + projectId + " ) ";

		// Apply Date filter
		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL)";
			} else {
				sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY)";
			}
		}
		sql = sql + ") ";

		LOG.debug("Sql: " + sql);

		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setSchemaName(rs.getString("schemaName"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public List<ListDataSource> getListDataSourceTableForProfilingDate(Long projectId, List<Project> projlst,
			String fromDate, String toDate) {

//		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);

		String sql = "(SELECT ld.name, lda.schemaName as databaseName,case when lda.folderName = 'Query' then ld.name else lda.folderName end as foldername,lds.schemaName, ld.idData,ld.description,ld.dataLocation,ld.dataSource,ld.createdAt,ld.createdBy, ld.project_id as projectId,"
				+ " p.projectName,ld.template_create_success,ld.active ";
		sql = sql + "FROM listDataSources ld ";
		sql = sql + "INNER JOIN listDataSchema lds ON ld.idDataSchema = lds.idDataSchema ";
		sql = sql + "JOIN listDataAccess lda ON ld.idData = lda.idData ";
		sql = sql + "JOIN project p on ld.project_id = p.idProject ";
		sql = sql + "WHERE ld.active =  'yes' and ld.profilingEnabled =  'Y' and ld.project_id in ( " + projectId
				+ " ) ";

		// Apply Date filter
		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL)";
			} else {
				sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY)";
			}
		}
		sql = sql + ") union ";
		sql = sql
				+ "(select ld.name, lda.schemaName as databaseName,case when lda.folderName = 'Query' then ld.name else lda.folderName end as foldername,'' as schemaName, ld.idData,ld.description,ld.dataLocation,ld.dataSource,ld.createdAt,ld.createdBy, ld.project_id as projectId, "
				+ "p.projectName,ld.template_create_success,ld.active ";
		sql = sql + "FROM listDataSources ld ";
		sql = sql + "LEFT JOIN listDataAccess lda ON ld.idData = lda.idData ";
		sql = sql + "JOIN project p on ld.project_id = p.idProject ";
		sql = sql
				+ "WHERE ld.active =  'yes' and ld.profilingEnabled = 'Y' and (ld.idDataSchema = -1 or ld.idDataSchema = -3 or ld.idDataSchema = 0 or ld.idDataSchema is Null) ";
		sql = sql + "and ld.project_id in ( " + projectId + " ) ";

		// Apply Date filter
		if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= ('" + fromDate
						+ "'::DATE + '1 day'::INTERVAL)";
			} else {
				sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= DATE_ADD('" + fromDate
						+ "', INTERVAL 1 DAY)";
			}
		}
		sql = sql + ") ";

		LOG.debug("Sql: " + sql);

		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				// alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreDate(rs.getDate("createdAt").toString());
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setSchemaName(rs.getString("schemaName"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setActive(rs.getString("active"));
				alistdatasource.setDatabaseName(rs.getString("databaseName"));

				Date exc_Date = rs.getDate("createdAt");
				String execDate = null;
				if (exc_Date != null) {
					execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);
					alistdatasource.setCreatedAt(exc_Date);
				}
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public List<ListDataSource> getListDataSourceTableForAdvancedRules(Long projectId, List<Project> projlst) {

		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
		String sql = "";
		sql = sql
				+ "SELECT a.name, b.foldername, a.idData, a.description, a.dataLocation, a.dataSource, a.createdAt, a.createdBy, a.createdByUser, c.idProject as projectId, c.projectName,a.template_create_success ";
		sql = sql + "FROM listDataSources a ";
		sql = sql + "INNER JOIN listDataAccess b ON a.idData = b.idData ";
		sql = sql + "INNER JOIN project c ON a.project_id = c.idProject ";
		sql = sql + "WHERE a.active =  'yes' and a.advancedRulesEnabled =  'Y' and a.project_id in (" + projIds + ");";
		LOG.debug("sql -> " + sql);
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public List<ListDataSource> getListDataSourceTableForAdvancedRules(String projlst) {
		String sql = "";
		sql = sql
				+ "SELECT a.name, b.foldername, a.idData, a.description, a.dataLocation, a.dataSource, a.createdAt, a.createdBy, a.createdByUser, c.idProject as projectId, c.projectName,a.template_create_success ";
		sql = sql + "FROM listDataSources a ";
		sql = sql + "INNER JOIN listDataAccess b ON a.idData = b.idData ";
		sql = sql + "INNER JOIN project c ON a.project_id = c.idProject ";
		sql = sql + "WHERE a.active =  'yes' and a.advancedRulesEnabled =  'y' and a.project_id in (" + projlst + ");";
		LOG.debug("sql -> " + sql);
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public List<ListAdvancedRules> getAdvancedRulesForId(long idData) {

		String sql = "SELECT Date,Run,ruleId,ruleType,columnName,ruleExpr,ruleSql,isRuleActive,isCustomRuleEligible,idListColrules from listAdvancedRules WHERE idData = "
				+ idData + " and Date=(select max(Date) from listAdvancedRules where idData=" + idData
				+ ") and Run=(select max(Run) from listAdvancedRules where idData=" + idData
				+ " and Date=(select max(Date) from listAdvancedRules where idData=" + idData
				+ ")) order by ruleId desc";
		List<ListAdvancedRules> advancedRulesList = jdbcTemplate.query(sql, new RowMapper<ListAdvancedRules>() {

			@Override
			public ListAdvancedRules mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListAdvancedRules listAdvancedRules = new ListAdvancedRules();
				String execDate = "";
				if (rs.getDate("Date") != null) {
					execDate = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("Date"));
				}
				listAdvancedRules.setExecDate(execDate);
				listAdvancedRules.setRun(rs.getLong("Run"));
				listAdvancedRules.setRuleId(rs.getLong("ruleId"));
				listAdvancedRules.setRuleType(rs.getString("ruleType"));
				listAdvancedRules.setColumnName(rs.getString("columnName"));
				listAdvancedRules.setRuleExpr(rs.getString("ruleExpr"));
				listAdvancedRules.setRuleSql(rs.getString("ruleSql"));
				listAdvancedRules.setIsRuleActive(rs.getString("isRuleActive"));
				listAdvancedRules.setIsCustomRuleEligible(rs.getString("isCustomRuleEligible"));
				listAdvancedRules.setIdListColrules(rs.getLong("idListColrules"));
				return listAdvancedRules;
			}

		});

		return advancedRulesList;

	}

	@Override
	public List<ListAdvancedRules> getAdvancedRulesForChecks(Long idData, String checkType,
			JSONObject filterAttribute) {
		String ruleType = "";
		String filterCondition = "";
		Set<String> keySet = filterAttribute.keySet();
		for (String filterColumn : keySet) {
			try {
				String filterValue = "" + filterAttribute.get(filterColumn);
				if (!filterValue.isEmpty())
					filterCondition = filterCondition + " LOWER(" + filterColumn + ") LIKE '%" + filterValue
							+ "%' and ";
			} catch (Exception e) {
				LOG.error(e.getMessage() + " " + e.getCause());
				e.printStackTrace();
			}
		}
		if (checkType.equals("advance")) {
			ruleType = " and ruleType in('Drift','Reasonability', 'ColumnRelationship') ";
		} else if (checkType.equals("essential")) {
			ruleType = " and ruleType not in('Drift','Reasonability', 'ColumnRelationship') ";
		}
		String sql = "SELECT Date,Run,ruleId,ruleType,columnName,ruleExpr,ruleSql,isRuleActive,isCustomRuleEligible,idListColrules from listAdvancedRules WHERE "
				+ filterCondition + " idData = " + idData + ruleType
				+ " and Date=(select max(Date) from listAdvancedRules where " + filterCondition + " idData=" + idData
				+ ruleType + ") and Run=(select max(Run) from listAdvancedRules where " + filterCondition + " idData="
				+ idData + ruleType + " and Date=(select max(Date) from listAdvancedRules where " + filterCondition
				+ " idData=" + idData + ruleType + ")) order by ruleId desc";
		List<ListAdvancedRules> advancedRulesList = jdbcTemplate.query(sql, new RowMapper<ListAdvancedRules>() {

			@Override
			public ListAdvancedRules mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListAdvancedRules listAdvancedRules = new ListAdvancedRules();
				String execDate = "";
				if (rs.getDate("Date") != null) {
					execDate = new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("Date"));
				}
				listAdvancedRules.setExecDate(execDate);
				listAdvancedRules.setRun(rs.getLong("Run"));
				listAdvancedRules.setRuleId(rs.getLong("ruleId"));
				listAdvancedRules.setRuleType(getRuleType(rs.getString("ruleType")));
				listAdvancedRules.setColumnName(rs.getString("columnName"));
				listAdvancedRules.setRuleExpr(rs.getString("ruleExpr"));
				listAdvancedRules.setRuleSql(rs.getString("ruleSql"));
				listAdvancedRules.setIsRuleActive(rs.getString("isRuleActive"));
				listAdvancedRules.setIsCustomRuleEligible(rs.getString("isCustomRuleEligible"));
				listAdvancedRules.setIdListColrules(rs.getLong("idListColrules"));
				return listAdvancedRules;
			}

		});

		return advancedRulesList;
	}

	private String getRuleType(String ruleType) {
		switch (ruleType) {
		case "Uniqueness":
			return "Duplicate";
		case "Conformity":
			return "Conformity";
		case "Consistency":
			return "Consistency";
		case "Completeness":
			return "Null";
		default:
			return ruleType;
		}
	}

	@Override
	public int getAdvancedRulesCount(long idData) {

		int advancedRulesListcount = 0;
		try {
			String sql = "SELECT count(*) as rulecount from listAdvancedRules WHERE idData = " + idData;

			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
			while (rs.next()) {
				advancedRulesListcount = rs.getInt("rulecount");

			}

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}

		return advancedRulesListcount;

	}

	@Override
	public void updateAdvancedRulesActiveStatus(long ruleId, String status, Long idListColrules) {

		try {
			String sql = "UPDATE listAdvancedRules set isRuleActive=? , idListColrules =? WHERE ruleId = ?";
			jdbcTemplate.update(sql, status, idListColrules, ruleId);
		} catch (Exception e) {
			LOG.error("Exception occurred while activating or deactivating advanced Rules !! " + e.getMessage() + " "
					+ e.getCause());
			e.printStackTrace();
		}
	}

	public List<ListDataSource> getListDataSourceTableForRef(String projIds,String domainIds) {
		// String sql = "SELECT idData,name, description, dataLocation,
		// dataSource,createdAt,createdBy from listDataSources ";
		// String sql="select * from listDataSources where active='yes'";
		String sql = "";
		sql = sql
				+ "SELECT a.name, b.foldername, a.idData, a.description, a.dataLocation, a.dataSource, a.createdAt, a.createdBy, a.createdByUser, c.idProject as projectId, c.projectName ";
		sql = sql + "FROM listDataSources a ";
		sql = sql + "INNER JOIN listDataAccess b ON a.idData = b.idData ";
		sql = sql + "INNER JOIN project c ON a.project_id = c.idProject ";
		sql = sql + "WHERE a.active =  'yes' AND a.name LIKE 'ref%' and a.project_id in (" + projIds + ") and a.domain_id in (" + domainIds + ");";
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				// alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public List<RowProfile_DP> readRowProfileForTemplate(Long idData) {
		List<RowProfile_DP> rowProfileList = new ArrayList<RowProfile_DP>();
		try {
			// Get the maxDate and maxRun
			String sql = "select Date,Max(Run) as Run from row_profile_master_table where idData=" + idData
					+ " and Date=(select Max(Date) from row_profile_master_table where idData=" + idData
					+ ") group by Date";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

			Date e_date = null;
			Long run = null;
			String e_Date_str = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					e_date = sqlRowSet.getDate("Date");
					if (e_date != null) {
						e_Date_str = new SimpleDateFormat("yyyy-MM-dd").format(e_date);
					}
					run = sqlRowSet.getLong("Run");
				}
			}

			sql = "select Date,Run,Number_of_Columns_with_NULL, Number_of_Records, Percentage_Missing from row_profile_master_table where idData=?";

			if (e_Date_str != null && !e_Date_str.trim().isEmpty() && run != null && run != 0l) {
				sql = sql + " and Date='" + e_Date_str + "' and Run=" + run;
			}

			rowProfileList = jdbcTemplate1.query(sql, new RowMapper<RowProfile_DP>() {

				@Override
				public RowProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {
					RowProfile_DP rowProfile = new RowProfile_DP();
					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						rowProfile.setExecDate(execDate);
						rowProfile.setRun(rs.getLong("Run"));
					}
					rowProfile.setNumber_of_Columns_with_NULL(rs.getLong("Number_of_Columns_with_NULL"));
					rowProfile.setNumber_of_Records(rs.getLong("Number_of_Records"));
					rowProfile.setPercentageMissing(rs.getDouble("Percentage_Missing"));
					return rowProfile;
				}

			}, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return rowProfileList;
	}

	@Override
	public List<NumericalProfile_DP> readNumericProfileForTemplate(Long idData) {
		List<NumericalProfile_DP> numericProfileList = new ArrayList<NumericalProfile_DP>();
		try {

			// Get the maxDate and maxRun
			String sql = "select Date,Max(Run) as Run from numerical_profile_master_table where idData=" + idData
					+ " and Date=(select Max(Date) from numerical_profile_master_table where idData=" + idData
					+ ") group by Date";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

			Date e_date = null;
			Long run = null;
			String e_Date_str = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					e_date = sqlRowSet.getDate("Date");
					if (e_date != null) {
						e_Date_str = new SimpleDateFormat("yyyy-MM-dd").format(e_date);
					}
					run = sqlRowSet.getLong("Run");
				}
			}

			sql = "select Date,Run, Column_Name_1, Column_Name_2, Correlation from numerical_profile_master_table where idData=?";

			if (e_Date_str != null && !e_Date_str.trim().isEmpty() && run != null && run != 0l) {
				sql = sql + " and Date='" + e_Date_str + "' and Run=" + run;
			}

			numericProfileList = jdbcTemplate1.query(sql, new RowMapper<NumericalProfile_DP>() {

				@Override
				public NumericalProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {
					NumericalProfile_DP numericalProfile = new NumericalProfile_DP();
					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						numericalProfile.setExecDate(execDate);
						numericalProfile.setRun(rs.getLong("Run"));
					}
					numericalProfile.setColumnName(rs.getString("Column_Name_1"));
					numericalProfile.setColumnName1(rs.getString("Column_Name_2"));
					numericalProfile.setCorrelation(rs.getDouble("Correlation"));
					return numericalProfile;
				}

			}, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return numericProfileList;
	}

	@Override
	public List<ColumnProfileDetails_DP> readColumnProfileDetailsForTemplate(Long idData) {
		List<ColumnProfileDetails_DP> columnProfileDetailsList = new ArrayList<ColumnProfileDetails_DP>();
		try {

			// Get the maxDate and maxRun
			String sql = "select Date,Max(Run) as Run from column_profile_detail_master_table where idData=" + idData
					+ " and Date=(select Max(Date) from column_profile_detail_master_table where idData=" + idData
					+ ") group by Date";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

			Date e_date = null;
			Long run = null;
			String e_Date_str = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					e_date = sqlRowSet.getDate("Date");
					if (e_date != null) {
						e_Date_str = new SimpleDateFormat("yyyy-MM-dd").format(e_date);
					}
					run = sqlRowSet.getLong("Run");
				}
			}

			sql = "select Date, Run, Column_Name, Column_Value, Count,Percentage from column_profile_detail_master_table where idData=?";

			if (e_Date_str != null && !e_Date_str.trim().isEmpty() && run != null && run != 0l) {
				sql = sql + " and Date='" + e_Date_str + "' and Run=" + run;
			}

			columnProfileDetailsList = jdbcTemplate1.query(sql, new RowMapper<ColumnProfileDetails_DP>() {

				@Override
				public ColumnProfileDetails_DP mapRow(ResultSet rs, int rowNum) throws SQLException {
					ColumnProfileDetails_DP columnProfileDetails = new ColumnProfileDetails_DP();
					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						columnProfileDetails.setExecDate(execDate);
						columnProfileDetails.setRun(rs.getLong("Run"));
					}
					columnProfileDetails.setColumnName(rs.getString("Column_Name"));
					columnProfileDetails.setColumnValue(rs.getString("Column_Value"));
					columnProfileDetails.setCount(rs.getLong("Count"));
					columnProfileDetails.setPercentage(rs.getDouble("Percentage"));
					return columnProfileDetails;
				}

			}, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnProfileDetailsList;
	}

	@Override
	public List<ColumnProfile_DP> readColumnProfileForTemplate(Long idData) {
		List<ColumnProfile_DP> columnProfileList = new ArrayList<ColumnProfile_DP>();
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			// Get the maxDate and maxRun
			String sql = "select Date,Max(Run) as Run from column_profile_master_table where idData=" + idData
					+ " and Date=(select Max(Date) from column_profile_master_table where idData=" + idData
					+ ") group by Date";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

			Date e_date = null;
			Long run = null;
			String e_Date_str = null;
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					e_date = sqlRowSet.getDate("Date");
					if (e_date != null) {
						e_Date_str = new SimpleDateFormat("yyyy-MM-dd").format(e_date);
					}
					run = sqlRowSet.getLong("Run");
				}
			}
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select Date, Run, table_or_fileName, Column_Name, Data_Type,Total_Record_Count, Missing_Value, Percentage_Missing, Unique_Count, Min_Length, Max_Length, Mean, Std_Dev, Min, Max, \"99_percentaile\", \"75_percentile\", \"25_percentile\", \"1_percentile\", Default_Patterns from column_profile_master_table where idData=? ";
			} else {
				sql = "select Date, Run, table_or_fileName, Column_Name, Data_Type,Total_Record_Count, Missing_Value, Percentage_Missing, Unique_Count, Min_Length, Max_Length, Mean, Std_Dev, Min, Max, 99_percentaile, 75_percentile, 25_percentile, 1_percentile, Default_Patterns from column_profile_master_table where idData=? ";
			}

			if (e_Date_str != null && !e_Date_str.trim().isEmpty() && run != null && run != 0l) {
				sql = sql + " and Date='" + e_Date_str + "' and Run=" + run;
			}
			columnProfileList = jdbcTemplate1.query(sql, new RowMapper<ColumnProfile_DP>() {

				@Override
				public ColumnProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {

					ColumnProfile_DP columnProfile = new ColumnProfile_DP();
					columnProfile.setIdData(idData);
					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						columnProfile.setExecDate(execDate);
						columnProfile.setRun(rs.getLong("Run"));
					}
					columnProfile.setTable_or_fileName(rs.getString("table_or_fileName"));
					columnProfile.setColumnName(rs.getString("Column_Name"));
					columnProfile.setDataType(rs.getString("Data_Type"));
					columnProfile.setTotalRecordCount(rs.getLong("Total_Record_Count"));
					columnProfile.setMissingValue(rs.getLong("Missing_Value"));
					columnProfile.setPercentageMissing(rs.getDouble("Percentage_Missing"));
					columnProfile.setUniqueCount(rs.getLong("Unique_Count"));
					columnProfile.setMinLength(rs.getLong("Min_Length"));
					columnProfile.setMaxLength(rs.getLong("Max_Length"));
					String mean = rs.getString("Mean");
					if (mean != null && !mean.trim().isEmpty()) {
						mean = decimalFormat.format(Double.parseDouble(mean));
					}
					columnProfile.setMean(mean);

					String stddev = rs.getString("Std_Dev");
					if (stddev != null && !stddev.trim().isEmpty()) {
						stddev = decimalFormat.format(Double.parseDouble(stddev));
					}
					columnProfile.setStdDev(stddev);
					columnProfile.setMin(rs.getString("Min").replace("+00", "").trim());
					columnProfile.setMax(rs.getString("Max").replace("+00", "").trim());
					columnProfile.setPercentile_99(rs.getString("99_percentaile"));
					columnProfile.setPercentile_75(rs.getString("75_percentile"));
					columnProfile.setPercentile_25(rs.getString("25_percentile"));
					columnProfile.setPercentile_1(rs.getString("1_percentile"));
					columnProfile.setDefaultPatterns(rs.getString("Default_Patterns"));
					return columnProfile;
				}

			}, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnProfileList;
	}

	@Override
	public List<ColumnProfile_DP> readColumnDataProfile(Long selectedProjectId, String fromDate, String toDate) {
		List<ColumnProfile_DP> columnProfileList = new ArrayList<ColumnProfile_DP>();
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");

			// Get the templateIds list based of Project and date filter
			String sql = "Select ld.idData from listDataSources ld where ld.project_id in ( " + selectedProjectId
					+ " ) ";

			// Apply Date filter
			if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= ('" + fromDate
							+ "'::DATE + '1 day'::INTERVAL)";
				} else {
					sql = sql + " and ld.createdAt >= '" + toDate + "' and ld.createdAt <= DATE_ADD('" + fromDate
							+ "', INTERVAL 1 DAY)";
				}
			}

			List<Long> templateIdList = jdbcTemplate.queryForList(sql, Long.class);

			if (templateIdList != null && !templateIdList.isEmpty()) {
				String templateIds = "";
				for (Long idData : templateIdList) {
					templateIds = templateIds + idData + ",";
				}
				if (templateIds.endsWith(","))
					templateIds = templateIds.substring(0, templateIds.length() - 1);

				sql = "select t3.* from column_profile_master_table t3  join  (select t1.idData, t1.Date, max(t2.Run) as Run from column_profile_master_table t2 join (select max(Date) as Date ,idData from column_profile_master_table group by idData) t1 on t1.idData=t2.idData and t1.Date=t2.Date group by t1.idData, t1.Date) t4 on t3.idData=t4.idData and t3.Date=t4.Date and t3.Run=t4.Run and t3.idData in ("
						+ templateIds + ") order by t3.idData desc";

				columnProfileList = jdbcTemplate1.query(sql, new RowMapper<ColumnProfile_DP>() {

					@Override
					public ColumnProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {

						ColumnProfile_DP columnProfile = new ColumnProfile_DP();

						columnProfile.setIdData(rs.getLong("idData"));
						Date exc_Date = rs.getDate("Date");
						String execDate = null;
						if (exc_Date != null) {
							execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

							columnProfile.setExecDate(execDate);
							columnProfile.setRun(rs.getLong("Run"));
						}
						columnProfile.setTable_or_fileName(rs.getString("table_or_fileName"));
						columnProfile.setColumnName(rs.getString("Column_Name"));
						columnProfile.setDataType(rs.getString("Data_Type"));
						columnProfile.setTotalRecordCount(rs.getLong("Total_Record_Count"));
						columnProfile.setMissingValue(rs.getLong("Missing_Value"));
						columnProfile.setPercentageMissing(rs.getDouble("Percentage_Missing"));
						columnProfile.setUniqueCount(rs.getLong("Unique_Count"));
						columnProfile.setMinLength(rs.getLong("Min_Length"));
						columnProfile.setMaxLength(rs.getLong("Max_Length"));
						String mean = rs.getString("Mean");
						if (mean != null && !mean.trim().isEmpty()) {
							mean = decimalFormat.format(Double.parseDouble(mean));
						}
						columnProfile.setMean(mean);

						String stddev = rs.getString("Std_Dev");
						if (stddev != null && !stddev.trim().isEmpty()) {
							stddev = decimalFormat.format(Double.parseDouble(stddev));
						}
						columnProfile.setStdDev(stddev);
						columnProfile.setMin(rs.getString("Min"));
						columnProfile.setMax(rs.getString("Max"));

						// conversion multiple decimal digit to 2 decimal digit
						String perc99 = rs.getString("99_percentaile");
						double p99 = Double.parseDouble(perc99);
						columnProfile.setPercentile_99(String.format("%.2f", p99));

						String perc75 = rs.getString("75_percentile");
						double p75 = Double.parseDouble(perc75);
						columnProfile.setPercentile_75(String.format("%.2f", p75));

						String perc25 = rs.getString("25_percentile");
						double p25 = Double.parseDouble(perc25);
						columnProfile.setPercentile_25(String.format("%.2f", p25));

						String perc1 = rs.getString("1_percentile");
						double p1 = Double.parseDouble(perc1);
						columnProfile.setPercentile_1(String.format("%.2f", p1));

						columnProfile.setProjectName(getProjectNameOfIdData(rs.getLong("idData")));
						columnProfile.setDefaultPatterns(rs.getString("Default_Patterns"));
						return columnProfile;
					}
				});
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnProfileList;
	}

	@Override
	public List<ColumnProfile_DP> readColumnDataProfileDate(Long selectedProjectId, String fromDate, String toDate) {
		List<ColumnProfile_DP> columnProfileList = new ArrayList<ColumnProfile_DP>();
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");

			// Get the templateIds list based of Project and date filter
			String sql = "Select ld.idData from listDataSources ld where ld.project_id in ( " + selectedProjectId
					+ " ) ";

			// Apply Date filter
			if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {

				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= ('" + fromDate
							+ "'::DATE + '1 day'::INTERVAL)";
				} else {
					sql = sql + " and ld.createdAt <= '" + toDate + "' and ld.createdAt >= DATE_ADD('" + fromDate
							+ "', INTERVAL 1 DAY)";
				}
			}

			List<Long> templateIdList = jdbcTemplate.queryForList(sql, Long.class);

			if (templateIdList != null && !templateIdList.isEmpty()) {
				String templateIds = "";
				for (Long idData : templateIdList) {
					templateIds = templateIds + idData + ",";
				}
				if (templateIds.endsWith(","))
					templateIds = templateIds.substring(0, templateIds.length() - 1);

				sql = "select t3.* from column_profile_master_table t3  join  (select t1.idData, t1.Date, max(t2.Run) as Run from column_profile_master_table t2 join (select max(Date) as Date ,idData from column_profile_master_table group by idData) t1 on t1.idData=t2.idData and t1.Date=t2.Date group by t1.idData, t1.Date) t4 on t3.idData=t4.idData and t3.Date=t4.Date and t3.Run=t4.Run and t3.idData in ("
						+ templateIds + ") order by t3.idData desc";

				columnProfileList = jdbcTemplate1.query(sql, new RowMapper<ColumnProfile_DP>() {

					@Override
					public ColumnProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {

						ColumnProfile_DP columnProfile = new ColumnProfile_DP();

						columnProfile.setIdData(rs.getLong("idData"));
						Date exc_Date = rs.getDate("Date");
						String execDate = null;
						if (exc_Date != null) {
							execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

							columnProfile.setExecDate(execDate);
							columnProfile.setRun(rs.getLong("Run"));
						}
						columnProfile.setTable_or_fileName(rs.getString("table_or_fileName"));
						columnProfile.setColumnName(rs.getString("Column_Name"));
						columnProfile.setDataType(rs.getString("Data_Type"));
						columnProfile.setTotalRecordCount(rs.getLong("Total_Record_Count"));
						columnProfile.setMissingValue(rs.getLong("Missing_Value"));
						columnProfile.setPercentageMissing(rs.getDouble("Percentage_Missing"));
						columnProfile.setUniqueCount(rs.getLong("Unique_Count"));
						columnProfile.setMinLength(rs.getLong("Min_Length"));
						columnProfile.setMaxLength(rs.getLong("Max_Length"));
						String mean = rs.getString("Mean");
						if (mean != null && !mean.trim().isEmpty()) {
							mean = decimalFormat.format(Double.parseDouble(mean));
						}
						columnProfile.setMean(mean);

						String stddev = rs.getString("Std_Dev");
						if (stddev != null && !stddev.trim().isEmpty()) {
							stddev = decimalFormat.format(Double.parseDouble(stddev));
						}
						columnProfile.setStdDev(stddev);
						columnProfile.setMin(rs.getString("Min"));
						columnProfile.setMax(rs.getString("Max"));
						columnProfile.setPercentile_99(rs.getString("99_percentaile"));
						columnProfile.setPercentile_75(rs.getString("75_percentile"));
						columnProfile.setPercentile_25(rs.getString("25_percentile"));
						columnProfile.setPercentile_1(rs.getString("1_percentile"));
						columnProfile.setProjectName(getProjectNameOfIdData(rs.getLong("idData")));
						columnProfile.setDefaultPatterns(rs.getString("Default_Patterns"));
						return columnProfile;
					}
				});
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnProfileList;
	}

	public String getProjectNameOfIdData(Long idData) {
		String projname = "";
		try {
			String sql = "";
			sql = sql + "select a.projectName from project a , listDataSources b ";
			sql = sql + "where b.project_id = a.idProject and b.idData = ? ";
			projname = (String) jdbcTemplate.queryForObject(sql, new Object[] { idData }, String.class);
		} catch (EmptyResultDataAccessException e) {
			// LOG.error(e.getMessage() +" "+e.getCause());e.printStackTrace();
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return projname;
	}

	@Override
	public List<ColumnCombinationProfile_DP> readColumnCombinationProfileForTemplate(Long idData) {
		List<ColumnCombinationProfile_DP> columnCombinationProfileList = new ArrayList<ColumnCombinationProfile_DP>();
		try {

			// Get the maxDate and maxRun
			String sql = "select Date,Max(Run) as Run from column_combination_profile_master_table where idData="
					+ idData + " and Date=(select Max(Date) from column_combination_profile_master_table where idData="
					+ idData + ") group by Date";
			SqlRowSet sqlRowSet = jdbcTemplate1.queryForRowSet(sql);

			Date e_date = null;
			Long run = null;
			String e_Date_str = null;

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					e_date = sqlRowSet.getDate("Date");
					if (e_date != null) {
						e_Date_str = new SimpleDateFormat("yyyy-MM-dd").format(e_date);
					}
					run = sqlRowSet.getLong("Run");
				}
			}

			sql = "select Date,Run, Column_Group_Name, Column_Group_Value, Count,Percentage from column_combination_profile_master_table where idData=?";
			if (e_Date_str != null && !e_Date_str.trim().isEmpty() && run != null && run != 0l) {
				sql = sql + " and Date='" + e_Date_str + "' and Run=" + run;
			}
			columnCombinationProfileList = jdbcTemplate1.query(sql, new RowMapper<ColumnCombinationProfile_DP>() {

				@Override
				public ColumnCombinationProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {
					ColumnCombinationProfile_DP columnCombinationProfile = new ColumnCombinationProfile_DP();
					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						columnCombinationProfile.setExecDate(execDate);
						columnCombinationProfile.setRun(rs.getLong("Run"));
					}
					columnCombinationProfile.setColumn_Group_Name(rs.getString("Column_Group_Name"));
					columnCombinationProfile.setColumn_Group_Value(rs.getString("Column_Group_Value"));
					columnCombinationProfile.setCount(rs.getLong("Count"));
					columnCombinationProfile.setPercentage(rs.getDouble("Percentage"));
					return columnCombinationProfile;
				}

			}, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnCombinationProfileList;
	}

	@Override
	public SqlRowSet getPresentAndLastRunDetailsOfColumnProfile(long idData) {
		SqlRowSet sqlRowSet = null;
		try {
			String sql = "select Date,Run from column_profile_master_table where idData=? and Date is not null and Run is not null group by Date,Run order by Date desc,Run desc limit 2";
			sqlRowSet = jdbcTemplate1.queryForRowSet(sql, idData);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public List<ColumnProfile_DP> readColumnProfileForTemplate(long idData, Date previousExecDate, Long previousRun) {
		List<ColumnProfile_DP> columnProfileList = new ArrayList<ColumnProfile_DP>();
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select Date, Run, Column_Name, Data_Type,Total_Record_Count, Missing_Value, Percentage_Missing, Unique_Count,"
						+ " Min_Length, Max_Length, Mean, Std_Dev, Min, Max, \"99_percentaile\", \"75_percentile\", \"25_percentile\",\"1_percentile\", Default_Patterns "
						+ "from column_profile_master_table where idData=? and Date=?::Date and Run=?";
			} else {
				sql = "select Date, Run, Column_Name, Data_Type,Total_Record_Count, Missing_Value, Percentage_Missing, Unique_Count,"
						+ " Min_Length, Max_Length, Mean, Std_Dev, Min, Max, 99_percentaile, 75_percentile, 25_percentile, 1_percentile, Default_Patterns "
						+ "from column_profile_master_table where idData=? and Date=? and Run=?";
			}
			LOG.debug("SQL FOR PROFILING:" + sql);
			columnProfileList = jdbcTemplate1.query(sql, new RowMapper<ColumnProfile_DP>() {

				@Override
				public ColumnProfile_DP mapRow(ResultSet rs, int rowNum) throws SQLException {

					ColumnProfile_DP columnProfile = new ColumnProfile_DP();

					Date exc_Date = rs.getDate("Date");
					String execDate = null;
					if (exc_Date != null) {
						execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);

						columnProfile.setExecDate(execDate);
						columnProfile.setRun(rs.getLong("Run"));
					}

					columnProfile.setColumnName(rs.getString("Column_Name"));
					columnProfile.setDataType(rs.getString("Data_Type"));
					columnProfile.setTotalRecordCount(rs.getLong("Total_Record_Count"));
					columnProfile.setMissingValue(rs.getLong("Missing_Value"));
					columnProfile.setPercentageMissing(rs.getDouble("Percentage_Missing"));
					columnProfile.setUniqueCount(rs.getLong("Unique_Count"));
					columnProfile.setMinLength(rs.getLong("Min_Length"));
					columnProfile.setMaxLength(rs.getLong("Max_Length"));
					String mean = rs.getString("Mean");
					if (mean != null && !mean.trim().isEmpty()) {
						mean = decimalFormat.format(Double.parseDouble(mean));
					}
					columnProfile.setMean(mean);

					String stddev = rs.getString("Std_Dev");
					if (stddev != null && !stddev.trim().isEmpty()) {
						stddev = decimalFormat.format(Double.parseDouble(stddev));
					}
					columnProfile.setStdDev(stddev);
					columnProfile.setMin(rs.getString("Min"));
					columnProfile.setMax(rs.getString("Max"));
					columnProfile.setPercentile_99(rs.getString("99_percentaile"));
					columnProfile.setPercentile_75(rs.getString("75_percentile"));
					columnProfile.setPercentile_25(rs.getString("25_percentile"));
					columnProfile.setPercentile_1(rs.getString("1_percentile"));
					columnProfile.setDefaultPatterns(rs.getString("Default_Patterns"));
					return columnProfile;
				}

			}, idData, previousExecDate, previousRun);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return columnProfileList;
	}

	@Override
	public boolean isGlobalruleLinkedToTemplate(long idData, long globalRuleId) {
		boolean isRuleLinked = false;
		try {
			String sql = " Select count(*) from rule_Template_Mapping where templateid=?  and ruleId=? ";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idData, globalRuleId);

			if (count != null && count > 0) {
				isRuleLinked = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return isRuleLinked;
	}

	@Override
	public boolean isGlobalThresholdLinkedToTemplate(long idData, long globalThresholdId, long idColumn) {
		boolean isRuleLinked = false;
		try {
			String sql = " Select count(*) from listGlobalThresholdsSelected where idData=? and idGlobalThreshold=? and idColumn=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idData, globalThresholdId, idColumn);
			if (count != null && count > 0) {
				isRuleLinked = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return isRuleLinked;
	}

	@Override
	public boolean updateTemplateDeltaApprovalStatus(Long idData, String approvalStatus) {
		boolean updateStatus = false;
		try {
			String sql = "update listDataSources set deltaApprovalStatus=? where idData=?";
			int count = jdbcTemplate.update(sql, approvalStatus, idData);
			if (count == 1) {
				updateStatus = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return updateStatus;
	}

	@Override
	public boolean deactivateDataSource(long idData) {
		boolean status = false;
		int count = 0;
		try {
			String sql = "update listDataSources set active='no' where idData=?";
			count = jdbcTemplate.update(sql, idData);

			// Get all appId related to this
			String idAppList = "";
			String getAllIdApps = "select idApp as idApp from listApplications where idData=" + idData;
			SqlRowSet rs = jdbcTemplate.queryForRowSet(getAllIdApps);
			while (rs.next()) {
				idAppList = idAppList + rs.getString("idApp") + ", ";
			}
			idAppList = idAppList.replaceAll(", $", "");

			if (idAppList != "") {
				String deleteValiationSql = "update listApplications set active='no' where idApp in (" + idAppList
						+ ")";
				jdbcTemplate.update(deleteValiationSql);
			}

			if (count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public void insertListDataDefintionsFromStagingForIdData(Long newIdData, Long oldIdData) {
		try {
			// Clear the listDataDefintion table
			String deleteSql = "delete from listDataDefinition where idData=" + newIdData;
			jdbcTemplate.update(deleteSql);

			// Insert the listDataDefintion from staging
			String sql = "insert into listDataDefinition (idData, columnName, "
					+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
					+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
					+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
					+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
					+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
					+ "lengthcheck ,maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
					+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
					+ "dateFormat, defaultPatternCheck, defaultPatterns) (select " + newIdData
					+ " as idData, columnName, "
					+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
					+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
					+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
					+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
					+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
					+ "lengthcheck, maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
					+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
					+ "dateFormat, defaultPatternCheck, defaultPatterns from staging_listDataDefinition where idData="
					+ oldIdData + ")";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
	}

	@Override
	public void copyListDataDefintionsOfTemplateToStaging(Long idData) {
		try {
			String sql = "insert into staging_listDataDefinition (idData, columnName, "
					+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
					+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
					+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
					+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
					+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
					+ "lengthcheck ,maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
					+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
					+ "dateFormat, defaultPatternCheck, defaultPatterns) (select idData, columnName, "
					+ "displayName, format, hashValue, numericalStat, stringStat, nullCountThreshold, "
					+ "numericalThreshold, stringStatThreshold, KBE, dgroup, dupkey, measurement, blend, "
					+ "idCol, incrementalCol, idDataSchema, nonNull, primaryKey, recordAnomaly, "
					+ "recordAnomalyThreshold, dataDrift, dataDriftThreshold, outOfNormStat, "
					+ "outOfNormStatThreshold, isMasked, correlationcolumn, partitionBy, "
					+ "lengthcheck, maxLengthCheck, lengthvalue, applyrule, startDate, timelinessKey, endDate, "
					+ "defaultCheck, defaultValues, patternCheck, patterns, dateRule, badData, "
					+ "dateFormat, defaultPatternCheck, defaultPatterns from listDataDefinition where idData=" + idData
					+ ")";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}

	}

	@Override
	public void clearListDataDefinitonStagingForIdData(long idData) {
		try {
			String sql = "delete from staging_listDataDefinition where idData=?";
			jdbcTemplate.update(sql, idData);
		} catch (Exception e) {
			LOG.error("Exception occurred configDao - clearListDataDefinitonStagingForIdData API " + e.getMessage()
					+ " " + e.getCause());
			e.printStackTrace();
		}
	}

	@Override
	public int deleteListDataDefinitionByIdColumn(long idColumn) {
		int count = 0;
		try {
			String sql = "delete from listDataDefinition where idColumn=?";
			count = jdbcTemplate.update(sql, idColumn);
		} catch (Exception e) {
			LOG.error("Exception occurred configDao - deleteListDataDefinitionByIdColumn API" + " " + e.getMessage()
					+ " " + e.getCause());
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int insertListDataDefinitionForIdData(long idData, ListDataDefinition colDataDefinition) {
		int count = 0;
		try {

			String sql = "insert into listDataDefinition(idData,columnName,displayName,format,KBE,dgroup,dupkey,"
					+ "measurement,idCol,blend,idDataSchema,hashValue,defaultCheck,defaultValues,numericalStat,numericalThreshold,"
					+ "stringStat,stringStatThreshold,incrementalCol,nonNull,nullCountThreshold,"
					+ "primarykey,recordanomaly,recordanomalyThreshold,dataDrift,dataDriftThreshold,isMasked,"
					+ "partitionBy,badData,badDataCheckThreshold,lengthCheck,lengthValue,lengthCheckThreshold,"
					+ "startDate,endDate,timelinessKey,outOfNormStat,outOfNormStatThreshold,patterns,patternCheck,patternCheckThreshold,dateFormat,dateRule) "
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			count = jdbcTemplate.update(sql, new Object[] { idData, "", colDataDefinition.getDisplayName(),
					colDataDefinition.getFormat(),
					(colDataDefinition.getKBE() != null && !colDataDefinition.getKBE().equalsIgnoreCase("null"))
							? colDataDefinition.getKBE()
							: null,
					(colDataDefinition.getDgroup() != null && !colDataDefinition.getDgroup().equalsIgnoreCase("null"))
							? colDataDefinition.getDgroup()
							: null,
					(colDataDefinition.getDupkey() != null && !colDataDefinition.getDupkey().equalsIgnoreCase("null"))
							? colDataDefinition.getDupkey()
							: null,
					(colDataDefinition.getMeasurement() != null
							&& !colDataDefinition.getMeasurement().equalsIgnoreCase("null"))
									? colDataDefinition.getMeasurement()
									: null,
					colDataDefinition.getIdCol(), colDataDefinition.getBlend(), Integer.valueOf(0),
					(colDataDefinition.getHashValue() != null
							&& !colDataDefinition.getHashValue().equalsIgnoreCase("null"))
									? colDataDefinition.getHashValue()
									: null,
					(colDataDefinition.getDefaultCheck() != null
							&& !colDataDefinition.getDefaultCheck().equalsIgnoreCase("null"))
									? colDataDefinition.getDefaultCheck()
									: null,
					(colDataDefinition.getDefaultValues() != null
							&& !colDataDefinition.getDefaultValues().equalsIgnoreCase("null"))
									? colDataDefinition.getDefaultValues()
									: null,
					(colDataDefinition.getNumericalStat() != null
							&& !colDataDefinition.getNumericalStat().equalsIgnoreCase("null"))
									? colDataDefinition.getNumericalStat()
									: null,
					colDataDefinition.getNumericalThreshold(),
					(colDataDefinition.getStringStat() != null
							&& !colDataDefinition.getStringStat().equalsIgnoreCase("null"))
									? colDataDefinition.getStringStat()
									: null,
					colDataDefinition.getStringStatThreshold(),
					(colDataDefinition.getIncrementalCol() != null
							&& !colDataDefinition.getIncrementalCol().equalsIgnoreCase("null"))
									? colDataDefinition.getIncrementalCol()
									: null,
					(colDataDefinition.getNonNull() != null && !colDataDefinition.getNonNull().equalsIgnoreCase("null"))
							? colDataDefinition.getNonNull()
							: null,
					colDataDefinition.getNullCountThreshold(),
					(colDataDefinition.getPrimaryKey() != null
							&& !colDataDefinition.getPrimaryKey().equalsIgnoreCase("null"))
									? colDataDefinition.getPrimaryKey()
									: null,
					(colDataDefinition.getRecordAnomaly() != null
							&& !colDataDefinition.getRecordAnomaly().equalsIgnoreCase("null"))
									? colDataDefinition.getRecordAnomaly()
									: null,
					colDataDefinition.getRecordAnomalyThreshold(),
					(colDataDefinition.getDataDrift() != null
							&& !colDataDefinition.getDataDrift().equalsIgnoreCase("null"))
									? colDataDefinition.getDataDrift()
									: null,
					colDataDefinition.getDataDriftThreshold(),
					(colDataDefinition.getIsMasked() != null
							&& !colDataDefinition.getIsMasked().equalsIgnoreCase("null"))
									? colDataDefinition.getIsMasked()
									: null,
					(colDataDefinition.getPartitionBy() != null
							&& !colDataDefinition.getPartitionBy().equalsIgnoreCase("null"))
									? colDataDefinition.getPartitionBy()
									: null,
					(colDataDefinition.getBadData() != null && !colDataDefinition.getBadData().equalsIgnoreCase("null"))
							? colDataDefinition.getBadData()
							: null,
					colDataDefinition.getBadDataThreshold(),
					(colDataDefinition.getLengthCheck() != null
							&& !colDataDefinition.getLengthCheck().equalsIgnoreCase("null"))
									? colDataDefinition.getLengthCheck()
									: null,
					(colDataDefinition.getLengthValue() != null
							&& !colDataDefinition.getLengthValue().equalsIgnoreCase("null"))
									? colDataDefinition.getLengthValue()
									: null,
					colDataDefinition.getLengthThreshold(),
					(colDataDefinition.getStartDate() != null
							&& !colDataDefinition.getStartDate().equalsIgnoreCase("null"))
									? colDataDefinition.getStartDate()
									: null,
					(colDataDefinition.getEndDate() != null && !colDataDefinition.getEndDate().equalsIgnoreCase("null"))
							? colDataDefinition.getEndDate()
							: null,
					(colDataDefinition.getTimelinessKey() != null
							&& !colDataDefinition.getTimelinessKey().equalsIgnoreCase("null"))
									? colDataDefinition.getTimelinessKey()
									: null,
					(colDataDefinition.getOutOfNormStat() != null
							&& !colDataDefinition.getOutOfNormStat().equalsIgnoreCase("null"))
									? colDataDefinition.getOutOfNormStat()
									: null,
					colDataDefinition.getOutOfNormStatThreshold(),
					(colDataDefinition.getPatterns() != null
							&& !colDataDefinition.getPatterns().equalsIgnoreCase("null"))
									? colDataDefinition.getPatterns()
									: null,
					(colDataDefinition.getPatternCheck() != null
							&& !colDataDefinition.getPatternCheck().equalsIgnoreCase("null"))
									? colDataDefinition.getPatternCheck()
									: null,
					colDataDefinition.getPatternCheckThreshold(),
					(colDataDefinition.getDateFormat() != null
							&& !colDataDefinition.getDateFormat().equalsIgnoreCase("null"))
									? colDataDefinition.getDateFormat()
									: null,
					(colDataDefinition.getDateRule() != null
							&& !colDataDefinition.getDateRule().equalsIgnoreCase("null"))
									? colDataDefinition.getDateRule()
									: null });

		} catch (Exception e) {
			LOG.error("Exception occurred configDao - insertListDataDefinitionForIdData API");
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public SqlRowSet getTemplatesColumnChangesForProject(long projectId) {
		SqlRowSet sqlRowSet = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select tc.templateId,ls.name as templateName,(case when ls.idDataSchema>0 then ls.idDataSchema else null end) as connectionId, lds.schemaName as connectionName,ls.dataLocation, lda.folderName as tableOrFile, tc.addedColumns,tc.missingColumns,tc.changeDetectedTime from (select templateId,changeDetectedTime,string_agg(case when (isNewColumn='Y') then columnName else null end,',') as addedColumns,string_agg(case when (isMissingColumn='Y') then columnName else null end,',') as missingColumns from template_column_change_history group by templateId, changeDetectedTime) tc join listDataSources ls on tc.templateId=ls.idData join listDataAccess lda on lda.idData=tc.templateId left outer join listDataSchema lds on lds.idDataSchema=ls.idDataSchema where ls.project_id=?";
			} else {
				sql = "select tc.templateId,ls.name as templateName,(case when ls.idDataSchema>0 then ls.idDataSchema else null end) as connectionId, lds.schemaName as connectionName,ls.dataLocation, lda.folderName as tableOrFile, tc.addedColumns,tc.missingColumns,tc.changeDetectedTime from (select templateId,changeDetectedTime,group_concat(case when (isNewColumn='Y') then columnName else null end) as addedColumns,group_concat(case when (isMissingColumn='Y') then columnName else null end) as missingColumns from template_column_change_history group by templateId, changeDetectedTime) tc join listDataSources ls on tc.templateId=ls.idData join listDataAccess lda on lda.idData=tc.templateId left outer join listDataSchema lds on lds.idDataSchema=ls.idDataSchema where ls.project_id=?";
			}
			sqlRowSet = jdbcTemplate.queryForRowSet(sql, projectId);
		} catch (Exception e) {
			LOG.debug("\n====>Exception occurred in configDao - getTemplatesColumnChanges API");
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return sqlRowSet;
	}

	@Override
	public String getAlreadyLinkedGlobalRulesToDataTemplate(long nDataTemplateRowId) {
		SqlRowSet oSqlRowSet = null;
		String sSqlQry = "";
		String sRetValue = "";

		try {

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQry = sSqlQry + "select string_agg(b.ruleId::text,',') as LinkedGlobalRuleRowIds \n";
				sSqlQry = sSqlQry + "from listColGlobalRules a, rule_Template_Mapping b, listDataSources c \n";
				sSqlQry = sSqlQry + "where b.ruleId = a.idListColrules \n";
				sSqlQry = sSqlQry + "and   b.activeFlag='Y' and b.templateid = c.idData \n";
				sSqlQry = sSqlQry + String.format("and   b.templateid = %1$s \n", nDataTemplateRowId);
				sSqlQry = sSqlQry + "group by b.templateid;";
			} else {
				sSqlQry = sSqlQry + "select group_concat(b.ruleId) as LinkedGlobalRuleRowIds \n";
				sSqlQry = sSqlQry + "from listColGlobalRules a, rule_Template_Mapping b, listDataSources c \n";
				sSqlQry = sSqlQry + "where b.ruleId = a.idListColrules \n";
				sSqlQry = sSqlQry + "and   b.activeFlag='Y' and b.templateid = c.idData \n";
				sSqlQry = sSqlQry + String.format("and   b.templateid = %1$s \n", nDataTemplateRowId);
				sSqlQry = sSqlQry + "group by b.templateid;";
			}

			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);
			sRetValue = (oSqlRowSet.next()) ? oSqlRowSet.getString("LinkedGlobalRuleRowIds") : "";

		} catch (Exception oException) {
			LOG.debug(String.format(
					"\n====>Exception occurred in IListDataSourceDAO.getAlreadyLinkedGlobalRulesToDataTemplate() \n%1$s",
					oException.getMessage()));
			oException.printStackTrace();
		}

		return sRetValue;
	}

	@Override
	public boolean unlinkGlobalRuleFromDataTemplate(long nDataTemplateRowId, long nGlobalRuleId) {
		boolean lRetValue = false;
		int nRowsDeleted = 0;

		String sDeleteQry = "delete from rule_Template_Mapping where templateid = %1$s and ruleId = %2$s;";

		try {
			sDeleteQry = String.format(sDeleteQry, nDataTemplateRowId, nGlobalRuleId);
			nRowsDeleted = jdbcTemplate.update(sDeleteQry);
			lRetValue = (nRowsDeleted < 1) ? false : true;
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		return lRetValue;
	}

	public List<ListDataSource> getListDataSourceTable(Long projectId, List<Project> projlist, String fromDate,
			String toDate, String objName) {

		String sql = "";
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlist);
		sql = sql
				+ "SELECT name, foldername, listDataSources.idData,description,dataLocation,dataSource,template_create_success,deltaApprovalStatus,profilingEnabled,advancedRulesEnabled,listDataSources.createdAt,createdBy,listDataSources.updatedAt,createdByUser, project.idProject as projectId, project.projectName ";
		sql = sql + "FROM listDataSources ";
		sql = sql + "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData ";
		sql = sql + "INNER JOIN project ON listDataSources.project_id = project.idProject ";
		sql = sql + "WHERE listDataSources.active =  'yes' and listDataSources.project_id in ( " + projectId
				+ " ) and name like '%" + objName + "%' or project.projectName LIKE '%" + objName + "%' ";
		sql = sql + "union ";
		sql = sql
				+ "SELECT a.name,concat(template1Name,' , ',template2Name) foldername, a.idData,b.description,a.dataLocation,a.dataSource,a.template_create_success,a.deltaApprovalStatus,a.profilingEnabled,a.advancedRulesEnabled,a.createdAt,a.createdBy,a.updatedAt,a.createdByUser, project.idProject as projectId, project.projectName ";
		sql = sql + "FROM listDataSources a ";
		sql = sql + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData ";
		sql = sql + "INNER JOIN project ON a.project_id = project.idProject ";
		sql = sql + "WHERE a.active =  'yes'and a.dataLocation =  'Derived'  and a.project_id  in ( " + projectId
				+ " ) and a.name like '%" + objName + "%' or project.projectName LIKE '%" + objName + "%' ";
		LOG.debug("getListDataSourceTable sql :" + sql);
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setCreatedBy(rs.getInt("createdBy"));
				alistdatasource.setTableName(rs.getString("foldername"));
				alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
				alistdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
				alistdatasource.setProjectId(rs.getInt("projectId"));
				alistdatasource.setProjectName(rs.getString("projectName"));
				alistdatasource.setUpdatedAt(rs.getDate("updatedAt"));
				// alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				return alistdatasource;
			}

		});

		return listdatasource;
	}

	@Override
	public void deactivateGlobalRuleOfTemplate(long idData, long globalRuleId) {
		try {
			String sql = "update rule_Template_Mapping set activeFlag='N' where templateid=? and ruleId=?";
			jdbcTemplate.update(sql, idData, globalRuleId);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
	}

	/* Avishkar[25-Aug-2021] JsonData method for data template view page */
	public JSONArray getPaginatedDataTemplateJsonData(HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] { "TemplateName", "Tablename", "TemplateId", "Description", "DataLocation",
				"DataSource", "Template_create_success", "DeltaApprovalStatus", "ProfilingEnabled",
				"AdvancedRulesEnabled", "CreatedAt", "CreatedBy", "UpdatedAt", "CreatedByUser", "ProjectId",
				"ProjectName" };

		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		DateUtility.DebugLog("getPaginatedDataTemplateJsonData 01",
				String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			sOption1Sql = String.format("and (CreatedAt between '%1$s' and '%2$s') \n",
					oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

			sOption2Sql = "and 1 = case when TemplateName like 'LIKE-TEXT' then 1 when Tablename like 'LIKE-TEXT' then 1 else 0 end \n"
					.replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "" + "Select * from " + "(  " 
						+ "SELECT name as TemplateName, foldername as Tablename, listDataSources.idData as TemplateId,description as Description,dataLocation as DataLocation,dataSource as DataSource, "
						+ "template_create_success as Template_create_success, deltaApprovalStatus as DeltaApprovalStatus, profilingEnabled as ProfilingEnabled, advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "to_date(listDataSources.createdAt::TEXT, 'YYYY-MM-DD') as CreatedAt,createdBy as CreatedBy, to_date(listDataSources.updatedAt::TEXT, 'YYYY-MM-DD')  as UpdatedAt, createdByUser as CreatedByUser, project.idProject as ProjectId, project.projectName as ProjectName "
						+ "FROM listDataSources "
						+ "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData "
						+ "INNER JOIN project ON listDataSources.project_id = project.idProject "
						+ "WHERE listDataSources.active =  'yes' and listDataSources.project_id in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "union "
						+ "SELECT a.name as TemplateName,CONCAT_WS(' , ',IF(LENGTH(`template1Name`),`template1Name`,NULL),IF(LENGTH(`template2Name`),`template2Name`,NULL)) Tablename, a.idData as TemplateId,b.description as Description,a.dataLocation as DataLocation, "
						+ "a.dataSource as DataSource, a.template_create_success as Template_create_success, a.deltaApprovalStatus as DeltaApprovalStatus, a.profilingEnabled as ProfilingEnabled, "
						+ "a.advancedRulesEnabled as AdvancedRulesEnabled, to_date(a.createdAt::TEXT, 'YYYY-MM-DD') as CreatedAt, a.createdBy as CreatedBy, to_date(a.updatedAt::TEXT, 'YYYY-MM-DD') as UpdatedAt, a.createdByUser as CreatedByUser, "
						+ "project.idProject as projectId, project.projectName as ProjectName "
						+ "FROM listDataSources a " + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData "
						+ "INNER JOIN project ON a.project_id = project.idProject "
						+ "WHERE a.active = 'yes' and a.dataLocation =  'Derived'  and a.project_id  in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "order by TemplateId desc) as core_qry "
						+ String.format("where ProjectId in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ (oPaginationParms.get("filterByDerived") != null
								? (oPaginationParms.get("filterByDerived").equalsIgnoreCase("true")
										? " and dataLocation = 'Derived' "
										: " and dataLocation != 'Derived' ")
								: "");
			} else {
				sDataSql = "" + "Select * from " + "(  "
						+ "SELECT name as TemplateName, foldername as Tablename, listDataSources.idData as TemplateId,description as Description,dataLocation as DataLocation,dataSource as DataSource, "
						+ "template_create_success as Template_create_success, deltaApprovalStatus as DeltaApprovalStatus, profilingEnabled as ProfilingEnabled, advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "cast(date_format(listDataSources.createdAt, '%y-%m-%d') as date) as CreatedAt,createdBy as CreatedBy, cast(date_format(listDataSources.updatedAt, '%y-%m-%d') as date) as UpdatedAt, createdByUser as CreatedByUser, project.idProject as ProjectId, project.projectName as ProjectName "
						+ "FROM listDataSources "
						+ "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData "
						+ "INNER JOIN project ON listDataSources.project_id = project.idProject "
						+ "WHERE listDataSources.active =  'yes' and listDataSources.project_id in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "union "
						+ "SELECT a.name as TemplateName,CONCAT_WS(' , ',IF(LENGTH(`template1Name`),`template1Name`,NULL),IF(LENGTH(`template2Name`),`template2Name`,NULL)) Tablename, a.idData as TemplateId,b.description as Description,a.dataLocation as DataLocation, "
						+ "a.dataSource as DataSource, a.template_create_success as Template_create_success, a.deltaApprovalStatus as DeltaApprovalStatus, a.profilingEnabled as ProfilingEnabled, "
						+ "a.advancedRulesEnabled as AdvancedRulesEnabled, cast(date_format(a.createdAt, '%y-%m-%d') as date) as CreatedAt, a.createdBy as CreatedBy, cast(date_format(a.updatedAt, '%y-%m-%d') as date) as UpdatedAt, a.createdByUser as CreatedByUser, "
						+ "project.idProject as projectId, project.projectName as ProjectName "
						+ "FROM listDataSources a " + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData "
						+ "INNER JOIN project ON a.project_id = project.idProject "
						+ "WHERE a.active = 'yes' and a.dataLocation =  'Derived'  and a.project_id  in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "order by TemplateId desc) as core_qry "
						+ String.format("where ProjectId in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ (oPaginationParms.get("filterByDerived") != null
								? (oPaginationParms.get("filterByDerived").equalsIgnoreCase("true")
										? " and dataLocation = 'Derived' "
										: " and dataLocation != 'Derived' ")
								: "");
			}

			if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("1")) {
				sDataSql = sDataSql + sOption1Sql + "limit 1000;";
			} else if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("2")) {
				sDataSql = sDataSql + sOption2Sql + "limit 1000;";
			} else {
				sDataSql = sDataSql + sOption1Sql + sOption2Sql + "limit 1000;";
			}

			DateUtility.DebugLog("getPaginatedDataTemplateJsonData 02", String
					.format("Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql));

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);
			aRetValue = new JSONArray(sDataViewList);

			DateUtility.DebugLog("getPaginatedDataTemplateJsonData 03",
					String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return aRetValue;
	}

	@Override
	public List<Map<String, Object>> findProfilingResultsByFilter(String sql) {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
			LOG.debug(sql);

			while (queryForRowSet.next()) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					if (object == null)
						resultMap.put(camCaseCol, "");
					else {
						resultMap.put(camCaseCol, object);
					}
				}
				if (resultMap != null)
					results.add(resultMap);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public boolean isGlobalruleLinkedToTemplateDeactivated(long idData, Long globalRuleId) {
		boolean isRuleDeactivated = false;
		try {
			String sql = "select * from rule_Template_Mapping where templateid=? and ruleId=?";
			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, idData, globalRuleId);

			if (rs != null) {
				while (rs.next()) {
					String activeFlag = rs.getString("activeFlag");
					if (activeFlag != null && activeFlag.equalsIgnoreCase("N")) {
						isRuleDeactivated = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}

		return isRuleDeactivated;
	}

	@Override
	public void activateGlobalruleLinkedToTemplate(long idData, ListColGlobalRules listColGlobalRules) {
		try {
			long globalRuleId = listColGlobalRules.getIdListColrules();
			String ruleExpression = listColGlobalRules.getExpression();
			String matchingRules = listColGlobalRules.getMatchingRules();
			String filterCondition = listColGlobalRules.getFilterCondition();
			String rightTemplateFilterCondition = listColGlobalRules.getRightTemplateFilterCondition();

			ruleExpression = ruleExpression.replace("'", "''");
			matchingRules = matchingRules.replace("'", "''");
			filterCondition = filterCondition.replace("'", "''");
			if (rightTemplateFilterCondition != null)
				rightTemplateFilterCondition = rightTemplateFilterCondition.trim().replaceAll("'", "''");

			String sql = "update rule_Template_Mapping set activeFlag='Y', ruleExpression='" + ruleExpression
					+ "', matchingRules='" + matchingRules + "', filter_condition='" + filterCondition
					+ "', right_template_filter_condition='" + rightTemplateFilterCondition
					+ "' where templateid=? and ruleId=?";
			jdbcTemplate.update(sql, idData, globalRuleId);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
	}

	@Override
	public boolean insertToRuleTemplateMapping(long idData, ListColGlobalRules listColGlobalRules,
			String anchorColumns) {
		boolean status = false;
		try {
			long globalRuleId = listColGlobalRules.getIdListColrules();
			String ruleExpression = listColGlobalRules.getExpression();
			String matchingRules = listColGlobalRules.getMatchingRules();
			String filterCondition = listColGlobalRules.getFilterCondition();
			String rightTemplateFilterCondition = listColGlobalRules.getRightTemplateFilterCondition();

			ruleExpression = ruleExpression.replace("'", "''");
			matchingRules = matchingRules.replace("'", "''");
			filterCondition = filterCondition.replace("'", "''");
			rightTemplateFilterCondition = (rightTemplateFilterCondition != null)
					? rightTemplateFilterCondition.trim().replaceAll("'", "''")
					: "";

			String sql = "insert into rule_Template_Mapping(templateid, ruleId, ruleName, ruleExpression, ruleType, anchorColumns,matchingRules,filter_condition,right_template_filter_condition,null_filter_columns) (select ("
					+ idData + ") as templateid, idListColrules, ruleName, '" + ruleExpression
					+ "' as ruleExpression, ruleType, '" + anchorColumns + "','" + matchingRules
					+ "' as matchingRules,'" + filterCondition + "' as filter_condition, '"
					+ rightTemplateFilterCondition + "' as right_template_filter_condition, '" + anchorColumns
					+ "' as null_filter_columns from listColGlobalRules where idListColrules = " + globalRuleId + ")";
			LOG.debug("insert query : " + sql);
			int count = jdbcTemplate.update(sql);
			if (count > 0)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}

		return status;
	}

	@Override
	public boolean updateRuleTemplateMapping(long idData, ListColGlobalRules listColGlobalRules, String anchorColumns) {
		boolean status = false;
		try {
			long globalRuleId = listColGlobalRules.getIdListColrules();
			String ruleExpression = listColGlobalRules.getExpression();
			String matchingRules = listColGlobalRules.getMatchingRules();
			String filterCondition = listColGlobalRules.getFilterCondition();
			String rightTemplateFilterCondition = listColGlobalRules.getRightTemplateFilterCondition();

			ruleExpression = ruleExpression.replace("'", "''");
			matchingRules = matchingRules.replace("'", "''");
			filterCondition = filterCondition.replace("'", "''");
			rightTemplateFilterCondition = (rightTemplateFilterCondition != null)
					? rightTemplateFilterCondition.trim().replaceAll("'", "''")
					: "";

			String sql = "update rule_Template_Mapping set ruleName=?, ruleExpression=?, ruleType=?, anchorColumns=?,matchingRules=?,filter_condition=?,right_template_filter_condition=?,null_filter_columns=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, listColGlobalRules.getRuleName(), ruleExpression,
					listColGlobalRules.getRuleType(), anchorColumns, matchingRules, filterCondition,
					rightTemplateFilterCondition, anchorColumns, idData, globalRuleId);
			LOG.debug("update query : " + sql);
			if (count > 0)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}

		return status;
	}

	@Override
	public List<ListDataSource> getListDataSourceBySource(long idDataSchema, String sourceName) {

		LOG.debug("idDataSchema:" + idDataSchema);
		String sql = "SELECT idData,name, description, dataLocation, dataSource,createdAt,idDataSchema,profilingEnabled,advancedRulesEnabled,template_create_success,deltaApprovalStatus,active from listDataSources where idDataSchema ="
				+ idDataSchema + " and name ='" + sourceName + "' order by idData desc";
		List<ListDataSource> listdatasource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			@Override
			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource alistdatasource = new ListDataSource();
				alistdatasource.setIdData(rs.getInt("idData"));
				alistdatasource.setName(rs.getString("name"));
				alistdatasource.setDescription(rs.getString("description"));
				alistdatasource.setDataLocation(rs.getString("dataLocation"));
				alistdatasource.setDataSource(rs.getString("dataSource"));
				alistdatasource.setCreatedAt(rs.getDate("createdAt"));
				alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
				alistdatasource.setProfilingEnabled(rs.getString("profilingEnabled"));
				alistdatasource.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
				alistdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
				alistdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
				alistdatasource.setActive(rs.getString("active"));
				return alistdatasource;
			}

		});

		return listdatasource;

	}

	@Override
	public List<Map<String, Object>> getDataTemplateForRequiredConnectionType(String connectionType, Long projectId,
			Integer domainId) {
		try {
			String sql = "SELECT idDataSchema,schemaName,databaseSchema from listDataSchema where schemaType='" + connectionType
					+ "' and Action = 'Yes'  and project_id = " + projectId + " and domain_id = " + domainId;
			List<Map<String, Object>> listMapObject = new ArrayList<Map<String, Object>>();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				Map<String, Object> mapObject = new HashMap<String, Object>();
				mapObject.put("databaseName", queryForRowSet.getString(3));
				mapObject.put("connectionName", queryForRowSet.getString(2));
				mapObject.put("id", queryForRowSet.getLong(1));
				listMapObject.add(mapObject);
			}
			return listMapObject;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean enableSSLForConnectionById(long idDataSchema) {
		try {
			String sql = "update listDataSchema set sslEnb='Y' where idDataSchema=" + idDataSchema;
			int update = jdbcTemplate.update(sql);
			if (update > 0)
				return true;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return false;
	}

	public List<HashMap<String, String>> getPaginatedDataTemplateJsonDatawithDomain(
			HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] { "TemplateName", "databaseName", "Tablename", "TemplateId", "Description", "DataLocation",
				"DataSource", "Template_create_success", "DeltaApprovalStatus", "ProfilingEnabled",
				"AdvancedRulesEnabled", "CreatedAt", "CreatedBy", "UpdatedAt", "CreatedByUser", "ProjectId",
				"ProjectName" };

		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		DateUtility.DebugLog("getPaginatedDataTemplateJsonData 01",
				String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			sOption1Sql = String.format("and (CreatedAt between '%1$s' and '%2$s') \n",
					oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

			sOption2Sql = "and 1 = case when TemplateName like 'LIKE-TEXT' then 1 when Tablename like 'LIKE-TEXT' then 1 else 0 end \n"
					.replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "" + "Select * from " + "(  "
						+ "SELECT name as TemplateName, case when listDataAccess.folderName = 'Query' then listDataSources.name else listDataAccess.folderName end as Tablename, COALESCE(NULLIF(listDataAccess.schemaName, ''), listDataSources.dataLocation) as databaseName, listDataSources.idData as TemplateId,description as Description,dataLocation as DataLocation,dataSource as DataSource, "
						+ "template_create_success as Template_create_success, CASE WHEN deltaApprovalStatus='' THEN 'reviewpending' ELSE deltaApprovalStatus END AS DeltaApprovalStatus, profilingEnabled as ProfilingEnabled, advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "to_date(listDataSources.createdAt::TEXT, 'YYYY-MM-DD') as CreatedAt,createdBy as CreatedBy, to_date(listDataSources.updatedAt::TEXT, 'YYYY-MM-DD')  as UpdatedAt, createdByUser as CreatedByUser, project.idProject as ProjectId, project.projectName as ProjectName "
						+ "FROM listDataSources "
						+ "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData "
						+ "INNER JOIN project ON listDataSources.project_id = project.idProject "
						+ "WHERE listDataSources.active =  'yes' and listDataSources.domain_id in ("
						+ oPaginationParms.get("domainId") + ") and listDataSources.project_id in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "union "
						+ "SELECT a.name as TemplateName,concat(template1Name,' , ',template2Name) Tablename,'Derived' as databaseName, a.idData as TemplateId,b.description as Description,a.dataLocation as DataLocation, "
						+ "a.dataSource as DataSource, a.template_create_success as Template_create_success, a.deltaApprovalStatus as DeltaApprovalStatus, a.profilingEnabled as ProfilingEnabled, "
						+ "a.advancedRulesEnabled as AdvancedRulesEnabled, to_date(a.createdAt::TEXT, 'YYYY-MM-DD') as CreatedAt, a.createdBy as CreatedBy, to_date(a.updatedAt::TEXT, 'YYYY-MM-DD') as UpdatedAt, a.createdByUser as CreatedByUser, "
						+ "project.idProject as projectId, project.projectName as ProjectName "
						+ "FROM listDataSources a " + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData "
						+ "INNER JOIN project ON a.project_id = project.idProject "
						+ "WHERE a.active = 'yes' and a.dataLocation =  'Derived' and a.domain_id in ("
						+ oPaginationParms.get("domainId") + ") and a.project_id  in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "order by TemplateId desc) as core_qry "
						+ String.format("where ProjectId in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ (oPaginationParms.get("filterByDerived") != null
								? (oPaginationParms.get("filterByDerived").equalsIgnoreCase("true")
										? " and dataLocation = 'Derived' "
										: " and dataLocation != 'Derived' ")
								: "");
			} else {
				sDataSql = "" + "Select * from " + "(  "
						+ "SELECT name as TemplateName, case when listDataAccess.folderName = 'Query' then listDataSources.name else listDataAccess.folderName end as Tablename, COALESCE(NULLIF(listDataAccess.schemaName, ''), listDataSources.dataLocation) as databaseName,listDataSources.idData as TemplateId,description as Description,dataLocation as DataLocation,dataSource as DataSource, "
						+ "template_create_success as Template_create_success, CASE WHEN deltaApprovalStatus='' THEN 'reviewpending' ELSE deltaApprovalStatus END AS DeltaApprovalStatus, profilingEnabled as ProfilingEnabled, advancedRulesEnabled as AdvancedRulesEnabled, "
						+ "cast(date_format(listDataSources.createdAt, '%y-%m-%d') as date) as CreatedAt,createdBy as CreatedBy, cast(date_format(listDataSources.updatedAt, '%y-%m-%d') as date) as UpdatedAt, createdByUser as CreatedByUser, project.idProject as ProjectId, project.projectName as ProjectName "
						+ "FROM listDataSources "
						+ "INNER JOIN listDataAccess ON listDataSources.idData = listDataAccess.idData "
						+ "INNER JOIN project ON listDataSources.project_id = project.idProject "
						+ "WHERE listDataSources.active =  'yes' and  listDataSources.domain_id in ("
						+ oPaginationParms.get("domainId") + ") and listDataSources.project_id in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "union "
						+ "SELECT a.name as TemplateName,concat(template1Name,' , ',template2Name) as Tablename,'Derived' as databaseName, a.idData as TemplateId,b.description as Description,a.dataLocation as DataLocation, "
						+ "a.dataSource as DataSource, a.template_create_success as Template_create_success, a.deltaApprovalStatus as DeltaApprovalStatus, a.profilingEnabled as ProfilingEnabled, "
						+ "a.advancedRulesEnabled as AdvancedRulesEnabled, cast(date_format(a.createdAt, '%y-%m-%d') as date) as CreatedAt, a.createdBy as CreatedBy, cast(date_format(a.updatedAt, '%y-%m-%d') as date) as UpdatedAt, a.createdByUser as CreatedByUser, "
						+ "project.idProject as projectId, project.projectName as ProjectName "
						+ "FROM listDataSources a " + "INNER JOIN listDerivedDataSources b ON a.idData = b.idData "
						+ "INNER JOIN project ON a.project_id = project.idProject "
						+ "WHERE a.active = 'yes' and a.dataLocation =  'Derived' and a.domain_id in ("
						+ oPaginationParms.get("domainId") + ") and a.project_id  in ( "
						+ oPaginationParms.get("ProjectIds") + " ) " + "order by TemplateId desc) as core_qry "
						+ String.format("where ProjectId in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ (oPaginationParms.get("filterByDerived") != null
								? (oPaginationParms.get("filterByDerived").equalsIgnoreCase("true")
										? " and dataLocation = 'Derived' "
										: " and dataLocation != 'Derived' ")
								: "");

			}

			if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("1")) {
				sDataSql = sDataSql + sOption1Sql + "limit 1000;";
			} else if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("2")) {
				sDataSql = sDataSql + sOption2Sql + "limit 1000;";
			} else {
				sDataSql = sDataSql + sOption1Sql + sOption2Sql + "limit 1000;";
			}

			LOG.debug("sDataSql" + sDataSql);

			DateUtility.DebugLog("getPaginatedDataTemplateJsonData 02", String
					.format("Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql));

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			// sDataViewList = oMapper.writeValueAsString(aDataViewList);
			// aRetValue = new JSONArray(sDataViewList);

			DateUtility.DebugLog("getPaginatedDataTemplateJsonData 03",
					String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return aDataViewList;
	}

	@Override
	public String checkTemplateStatus(int dataTemplateId) {
		String status = "";
		try {
			String sql = "select status from runTemplateTasks where idData=?";
			status = jdbcTemplate.queryForObject(sql, String.class, dataTemplateId);
		} catch (Exception e) {
			LOG.debug("Exception Occurred in getTemplateCreationJobStatusById for IdData:" + dataTemplateId);
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return status;

	}

	@Override
	public long getIdDataByDerivedId(long idDerivedData) {
		long idData = 0l;
		try {
			String sql = "select idData from listDerivedDataSources where idDerivedData = " + idDerivedData;
			idData = jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
		return idData;
	}

	@Override
	public ListDataSource getDataFromListDataSourcesByName(String templateName) {

		String sql = "SELECT idData, idDataSchema, name, description, dataLocation, dataSource,createdAt,ignoreRowsCount, domain_id, project_id, advancedRulesEnabled, profilingEnabled, template_create_success, deltaApprovalStatus, createdByUser,active from listDataSources where name='"
				+ templateName + "' and active='yes'";
		return jdbcTemplate.query(sql, new ResultSetExtractor<ListDataSource>() {

			public ListDataSource extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListDataSource listdatasource = new ListDataSource();
					listdatasource.setIdData(rs.getInt("idData"));
					listdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
					listdatasource.setName(rs.getString("name"));
					listdatasource.setDescription(rs.getString("description"));
					listdatasource.setDataLocation(rs.getString("dataLocation"));
					listdatasource.setDataSource(rs.getString("dataSource"));
					listdatasource.setCreatedAt(rs.getTimestamp("createdAt"));
					listdatasource.setIgnoreRowsCount(rs.getLong("ignoreRowsCount"));
					listdatasource.setGarbageRows(rs.getLong("ignoreRowsCount"));
					listdatasource.setDomain(rs.getInt("domain_id"));
					listdatasource.setProjectId(rs.getInt("project_id"));
					listdatasource.setAdvancedRulesEnabled("advancedRulesEnabled");
					listdatasource.setProfilingEnabled("profilingEnabled");
					listdatasource.setTemplateCreateSuccess(rs.getString("template_create_success"));
					listdatasource.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
					listdatasource.setCreatedByUser(rs.getString("createdByUser"));
					listdatasource.setActive(rs.getString("active"));
					return listdatasource;
				}
				return null;
			}
		});
	}

	@Override
	public List<Map<String, Object>> getListDataSchemaForDropDown(Long project_id) {
		String sql = "";
		String projIds = "";
		List<Map<String, Object>> details = new ArrayList<>();
		try {
			LOG.debug("in getListDataSchema : project_Ids=" + projIds);

			sql = sql + "SELECT idDataSchema, schemaName, schemaType from listDataSchema ";
			sql = sql + " where project_id in (  " + project_id
					+ " ) and  Action = 'Yes' and schemaType in('Hive Kerberos', 'Oracle', 'Postgres', 'Teradata')";
			LOG.debug("sql: " + sql);

			SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);

			while (rs.next()) {
				Map<String, Object> connDetailsMap = new HashMap<>();
				connDetailsMap.put("idDataSchema", rs.getLong("idDataSchema"));
				connDetailsMap.put("schemaName", rs.getString("schemaName"));
				connDetailsMap.put("schemaType", rs.getString("schemaType"));
				details.add(connDetailsMap);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return details;
	}

	@Override
	public long getActiveIdDataByTableName(String tableName) {
		long idData = 0;
		try {
			String sql = "SELECT lda.idData FROM listDataAccess lda "
					+ " INNER JOIN listDataSources lds ON lda.idData=lds.idData " + " WHERE lda.folderName ='"
					+ tableName + "' AND lds.active='yes' " + " ORDER BY idlistDataAccess DESC LIMIT 1";
			idData = jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idData;
	}
}
