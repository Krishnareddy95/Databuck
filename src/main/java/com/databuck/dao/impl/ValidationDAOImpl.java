package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.databuck.util.DatabuckUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDmCriteria;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IValidationDAO;
import com.databuck.service.RuleCatalogService;
import com.databuck.util.ToCamelCase;

import net.snowflake.client.jdbc.internal.google.common.base.Optional;

@Repository
public class ValidationDAOImpl implements IValidationDAO {
	
	private static final Logger LOG = Logger.getLogger(ValidationDAOImpl.class);

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	public JdbcTemplate jdbcTemplate1;

	@Autowired
	private DatabuckUtility databuckUtility;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	public SqlRowSet getdatafromresultmaster() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='DF' ORDER BY appID DESC";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	@Override
	public List<ListDataSchema> getData() {
		String sql = "SELECT databaseSchema,idDataSchema from listDataSchema";
		List<ListDataSchema> listDataSchema = jdbcTemplate.query(sql, new RowMapper<ListDataSchema>() {
			public ListDataSchema mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSchema listDataSchema = new ListDataSchema();

				listDataSchema.setDatabaseSchema(rs.getString("databaseSchema"));
				listDataSchema.setIdDataSchema(rs.getLong("idDataSchema"));
				LOG.debug("rs.getString(databaseSchema)" + rs.getString("databaseSchema"));
				LOG.debug("rs.getLong(idDataSchema):" + rs.getLong("idDataSchema"));
				return listDataSchema;

			}

		});
		return listDataSchema;
	}

	public List<ListApplications> getDataFromListApplicationsOfDQType() {
		RowMapper<ListApplications> rowMapper = (rs, i) -> {
			ListApplications listApplications = new ListApplications();
			listApplications.setIdApp(rs.getInt("idApp"));
			listApplications.setName(rs.getString("name"));
			listApplications.setDescription(rs.getString("description"));
			listApplications.setAppType(rs.getString("appType"));
			listApplications.setIdData(rs.getLong("idData"));
			String schemaName = jdbcTemplate.queryForObject("select schemaName from listDataAccess where idData=?",
					String.class, rs.getLong("idData"));
			listApplications.setSchema(schemaName);
			listApplications.setIdRightData(rs.getInt("idRightData"));
			listApplications.setCreatedBy(rs.getString("createdBy"));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
			Date createAtDate;
			String formattedCreatedAt = "";
			try {
				createAtDate = sdf.parse(rs.getString("createdAt"));
				formattedCreatedAt = output.format(createAtDate);
			} catch (ParseException e) {
				LOG.error(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listApplications.setCreatedAt(formattedCreatedAt);
			listApplications.setUpdatedAt(rs.getString("updatedAt"));
			listApplications.setUpdatedBy(rs.getString("updatedBy"));
			return listApplications;
		};
		List<ListApplications> listApplicationDataOfDQType = jdbcTemplate
				.query("select * from listApplications where appType='DATA_QUALITY'", rowMapper);
		LOG.debug("listApplicationDataOfDQType:" + listApplicationDataOfDQType);
		// LOG.debug("listApplicationDataOfDQType.get(0).getIdApp():"+listApplicationDataOfDQType.get(0).getIdApp());
		return listApplicationDataOfDQType;
	}

	public boolean deleteValidationViewApplication(Long idApp) {
		try {
			Long idData = jdbcTemplate.queryForObject("select idData from listApplications Where idApp=?", Long.class,
					idApp);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet("select idDFSet from listDFSetRule where idApp=?",
					idApp);
			Long idDFSet = null;
			if (queryForRowSet.next()) {
				idDFSet = queryForRowSet.getLong(1);
			}
			String dataSourceDeleteQuery = "delete from listDataSources where idData=" + idData;
			String listDfTranRuleDeleteQuery = "delete from listDFTranRule where idApp=" + idApp;
			String listDataDefinitionDeleteQuery = "delete from listDataDefinition where idData=" + idData;
			String listDFComparionDeleteQuery = "delete from listDFSetComparisonRule where idDFSet=" + idDFSet;
			String listApplicationDeleteQuery = "delete from listApplications Where idApp=" + idApp;
			jdbcTemplate.batchUpdate(
					new String[] { listDFComparionDeleteQuery, listDataDefinitionDeleteQuery, listDfTranRuleDeleteQuery,
							listDataDefinitionDeleteQuery, dataSourceDeleteQuery, listApplicationDeleteQuery });
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public int[] updateSchemavaluesinListDfTranRule(Long threshold, Long identity, Long idApp) {
		String sql1 = "update listDFTranRule set threshold=" + threshold + "   where   type= 'all'  and idApp=" + idApp;
		String sql2 = "update listDFTranRule set threshold=" + identity + "   where  type= 'identity'  and idApp="
				+ idApp;

		LOG.debug("sql1:" + sql1);
		LOG.debug("sql2:" + sql2);
		int[] batchUpdate = jdbcTemplate.batchUpdate(new String[] { sql1, sql2 });
		return batchUpdate;
	}

	public List<ListDataDefinition> getListDataDefinitionsByIdData(Long idData) {

		RowMapper<ListDataDefinition> rowMapper = (rs, i) -> {
			ListDataDefinition listDataDefinition = new ListDataDefinition();
			listDataDefinition.setIdColumn(rs.getLong(1));
			listDataDefinition.setIdData(rs.getInt(2));
			listDataDefinition.setDisplayName(rs.getString("displayName"));
			listDataDefinition.setFormat(rs.getString("format"));
			listDataDefinition.setHashValue(rs.getString("hashValue"));
			listDataDefinition.setNumericalStat(rs.getString("numericalStat"));
			listDataDefinition.setStringStat(rs.getString("stringStat"));
			listDataDefinition.setNullCountThreshold(rs.getDouble("nullCountThreshold"));
			listDataDefinition.setNumericalThreshold(rs.getDouble("numericalThreshold"));
			listDataDefinition.setKBE(rs.getString("KBE"));
			listDataDefinition.setDgroup(rs.getString("dgroup"));
			listDataDefinition.setDupkey(rs.getString("dupKey"));
			listDataDefinition.setMeasurement(rs.getString("measurement"));
			listDataDefinition.setBlend(rs.getString("blend"));
			listDataDefinition.setNonNull(rs.getString("nonNull"));
			listDataDefinition.setPrimaryKey(rs.getString("primaryKey"));
			listDataDefinition.setStringStatThreshold(rs.getDouble("stringStatThreshold"));
			listDataDefinition.setDataDrift(rs.getString("dataDrift"));
			return listDataDefinition;
		};
		List<ListDataDefinition> ListOfDataDefinition = jdbcTemplate
				.query("select * from listDataDefinition where idData=?", rowMapper, idData);
		return ListOfDataDefinition;
	}

	public boolean changeAllNonNullsToYes(Long idData) {
		try {
			String sql = "select nonNull from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			// LOG.debug("nonNull="+nonNull);
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set nonNull='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set nonNull='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllMicrosegmentToYes(Long idData) {
		try {
			String sql = "select dgroup from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set dgroup='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set dgroup='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAlllastReadTimeToYes(Long idData) {
		try {
			String sql = "select incrementalCol from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set incrementalCol='Y',KBE='Y' where idData='" + idData
						+ "' ";
			} else {
				query = "update staging_listDataDefinition set incrementalCol='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllIsMaskedToYes(Long idData) {
		try {
			String sql = "select count(*) from staging_listDataDefinition where idData=" + idData + " and isMasked='Y'";
			Integer maskedColCount = jdbcTemplate.queryForObject(sql, Integer.class);
			String query = "";
			if (maskedColCount != null && maskedColCount == 0) {
				query = "update staging_listDataDefinition set isMasked='Y',KBE='Y' where numericalStat='N' and dgroup='N' and recordAnomaly='N' and dataDrift='N' and idData='"
						+ idData + "' ";
			} else if (maskedColCount != null && maskedColCount > 0) {
				query = "update staging_listDataDefinition set isMasked='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllPartitionByToYes(Long idData) {
		try {
			String sql = "select partitionBy from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set partitionBy='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set partitionBy='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllDataDriftToYes(Long idData) {
		try {
			String sql = "select dataDrift from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set dataDrift='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set dataDrift='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllStartDateToYes(Long idData) {
		try {
			String sql = "select startDate from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set startDate='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set startDate='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllEndDateToYes(Long idData) {
		try {
			String sql = "select endDate from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set endDate='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set endDate='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllTimelinessKeyToYes(Long idData) {
		try {
			String sql = "select timelinessKey from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set timelinessKey='Y',KBE='Y' where idData='" + idData
						+ "' ";
			} else {
				query = "update staging_listDataDefinition set timelinessKey='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllRecordAnomalyToYes(Long idData) {
		try {
			String nonNull = "";
			String sql = "select recordAnomaly from staging_listDataDefinition where idData=" + idData + " and lower(format) like '%int%'  Limit 1";
			//String sql = "select recordAnomaly from staging_listDataDefinition where idData=" + idData + " Limit 1";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
			while(sqlRowSet.next()) {
				nonNull = sqlRowSet.getString("recordAnomaly");
			}
			// nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set recordAnomaly='Y',KBE='Y' where idData=" + idData
						//+ "' and lower(format) like '%int%'";
						 + " and (upper(format) = 'INT' or upper(format) = 'NUMBER' or upper(format) = 'INTEGER' or upper(format) = 'NUMERIC' or upper(format) = 'FLOAT' or upper(format) = 'DOUBLE' or upper(format) = 'BIGINT' or upper(format) = 'SMALLINT'  or upper(format) = 'DECIMAL')";
			} else {
				query = "update staging_listDataDefinition set recordAnomaly='N' where idData=" + idData;
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllDefaultCheckToYes(Long idData) {
		try {
			String sql = "select defaultCheck from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set defaultCheck='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set defaultCheck='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllDateRuleToYes(Long idData) {
		try {
			String sql = "select dateRule from staging_listDataDefinition where idData=" + idData
					+ " and lower(format) like '%date%' Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql,String.class);
			String query = "";
			if (!nonNull.isEmpty() && nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set dateRule='Y',KBE='Y' where idData='" + idData
						+ "' and lower(format) like '%date%'";
			} else {
				query = "update staging_listDataDefinition set dateRule='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllPatternCheckToYes(Long idData) {
		try {
			String sql = "select patternCheck from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set patternCheck='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set patternCheck='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllDefaultPatternCheckToYes(Long idData) {
		try {
			String sql = "select defaultPatternCheck from staging_listDataDefinition where idData=" + idData
					+ " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set defaultPatternCheck='Y',KBE='Y' where idData='" + idData
						+ "' ";
			} else {
				query = "update staging_listDataDefinition set defaultPatternCheck='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllBadDataToYes(Long idData) {
		try {
			String sql = "select badData from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set badData='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set badData='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllLengthCheckToYes(Long idData) {
		try {
			long nonNullCount = 0l;
			String dispNameCondition = "";
			String lengthSql = "select displayName from staging_listDataDefinition where maxLengthCheck='Y' and idData=" + idData;
			List<String> displayNameList = jdbcTemplate.queryForList(lengthSql, String.class);
			String columnNames = StringUtils.join(displayNameList, "', '").trim();
			if (displayNameList!=null && displayNameList.size() > 0)
				dispNameCondition = " and displayName not in('" + columnNames + "')";

			String sql = "select count(*) from staging_listDataDefinition where lengthCheck = 'N'" + dispNameCondition + " and idData=" + idData;
			nonNullCount = jdbcTemplate.queryForObject(sql, Long.class);
			String query = "";
			if (nonNullCount > 0) {
				query = "update staging_listDataDefinition set lengthCheck='Y',KBE='Y' where idData='" + idData + "'" + dispNameCondition;
			} else {
				query = "update staging_listDataDefinition set lengthCheck='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllMaxLengthCheckToYes(Long idData) {
		try {
			long nonNullCount = 0l;
			String dispNameCondition = "";
			String lengthSql = "select displayName from staging_listDataDefinition where lengthCheck='Y' and idData=" + idData;
			List<String> displayNameList = jdbcTemplate.queryForList(lengthSql, String.class);
			String columnNames = StringUtils.join(displayNameList, "', '").trim();
			if (displayNameList!=null && displayNameList.size() > 0)
				dispNameCondition = " and displayName not in('" + columnNames + "')";
			String sql = "select count(*) from staging_listDataDefinition where maxLengthCheck = 'N'" + dispNameCondition + " and idData=" + idData;
			nonNullCount = jdbcTemplate.queryForObject(sql, Long.class);
			String query = "";
			if (nonNullCount > 0) {
				query = "update staging_listDataDefinition set maxLengthCheck='Y',KBE='Y' where idData='" + idData
						+ "' "+ dispNameCondition;
			} else {
				query = "update staging_listDataDefinition set maxLengthCheck='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean changeAllMatchValuetToYes(Long idData) {
		try {
			String sql = "select measurement from staging_listDataDefinition where idData=" + idData + " Limit 1";
			String nonNull = jdbcTemplate.queryForObject(sql, String.class);
			String query = "";
			if (nonNull.equalsIgnoreCase("n")) {
				query = "update staging_listDataDefinition set measurement='Y',KBE='Y' where idData='" + idData + "' ";
			} else {
				query = "update staging_listDataDefinition set measurement='N' where idData='" + idData + "' ";
			}
			jdbcTemplate.update(query);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public JSONArray getValidationResultsByCheck(long idApp, String fromDate, String toDate, String tableName,
			String dGroupCondition) {
		String dqiCondition = "";
		if (tableName.equalsIgnoreCase("DATA_QUALITY_Transactionset_sum_dgroup")) {
			String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
			Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
			dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
					+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 " + "ELSE (100 - ( (ABS(dGroupDeviation - "
					+ threshold + ") *100) / ( 6 - " + threshold + " ) )) END) END) AS dgDqi";

		}
		String sql = "select *  " + dqiCondition + "  from " + tableName + " where idApp = " + idApp
				+ " and Date between '" + fromDate + "' and '" + toDate + "' " + dGroupCondition
				+ " order by Date DESC limit 1000";

		if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Summary")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
			return getValidationResultsByCheckDetails(sql, tableName);
		}
		return getValidationResultsByCheckDetails(sql);
	}

	private Map<String, String> getRegexPatternsByIdData(long idData) {
		Map<String, String> regexPatternColumnMap = new HashMap<>();
		try {
			String sql = "select displayname,patterns from listDataDefinition where idData=" + idData;
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				String colName = sqlRowSet.getString("displayname");
				String patterns = sqlRowSet.getString("patterns");
				regexPatternColumnMap.put(colName, patterns);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return regexPatternColumnMap;
	}

	private JSONArray getValidationResultsForRegexPatternCheck(String sql, long idApp) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("<<==sqlQuery==>>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();

			Long idData = jdbcTemplate.queryForObject("select idData from listApplications where idApp=" + idApp,
					Long.class);

			Map<String, String> regexPatternColumnMap = getRegexPatternsByIdData(idData);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);

					if (column_name.equalsIgnoreCase("colName") || column_name.equalsIgnoreCase("Col_Name")) {

						if (regexPatternColumnMap != null && !regexPatternColumnMap.isEmpty()
								&& regexPatternColumnMap.containsKey(object.toString())) {
							String pattern = regexPatternColumnMap.get(object.toString());
							resultObj.put("pattern_list", pattern);
						}
					}

					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null) {
						resultObj.put(camCaseCol, camCaseCol.equals("dGroupVal") ? "null" : "");
					} else {
						resultObj.put(camCaseCol, object);
					}
				}

				if (resultObj != null) {
					if (resultObj.has("dGroupRcStatus") && resultObj.has("action")) {
						String dGroupRcStatus = resultObj.getString("dGroupRcStatus");
						String action = resultObj.getString("action");
						if (dGroupRcStatus.trim().equalsIgnoreCase("review")
								&& action.trim().equalsIgnoreCase("accepted")) {
							resultObj.put("dGroupRcStatus", "reviewed");
							resultArr.put(resultObj);
						}
					}
					resultArr.put(resultObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultArr;
	}

	private JSONArray getValidationResultsByCheckDetails(String sql, String tableName) {
		JSONArray resultArr = new JSONArray();
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
			LOG.debug("\n====>sql:" + sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null) {
						resultObj.put(camCaseCol, camCaseCol.equals("dGroupVal") ? "null" : "");
					} else {
						if (camCaseCol.equalsIgnoreCase("type")) {
							if (String.valueOf(object).equalsIgnoreCase("all")) {
								object = "Individual";
							} else if (String.valueOf(object).equalsIgnoreCase("identity")) {
								object = "Composite";
							}
						}
						resultObj.put(camCaseCol, object);
					}
				}
				if (resultObj != null)
					resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getValidationResultsByCheckWithColName(long idApp, String fromDate, String toDate,
			String tableName, String dGroupCondition, String colName, String column, String checkName,
			JSONObject columnValues) {
		String sql = getQuery(checkName, tableName, idApp, fromDate, toDate);

		if ("".equals(sql) && colName.isEmpty() && colName.equals("")) {
			sql = "Select E.* from (select B." + column + ",B.Date,max(B.Run) as Run from (select " + column
					+ ",max(Date) as Date from  " + tableName + " where idApp=" + idApp + " group by " + column
					+ ") S INNER JOIN  " + tableName + " B ON S." + column + "= B." + column
					+ " AND S.Date = B.Date AND idApp=" + idApp + " group by B.Date,B." + column + ") D INNER JOIN "
					+ tableName + " E ON E." + column + "=D." + column
					+ " and E.Date = D.Date and E.Run = D.Run and idApp = " + idApp + " and E.Date between '" + fromDate
					+ "' and '" + toDate + "' ";
			;
		} else if (!colName.equals("")) {
			int id = 0;
			if (!columnValues.has("id")) {
				id = getMaxId(column, tableName, idApp, colName, checkName, columnValues, fromDate, toDate);
			} else {
				id = columnValues.getInt("id");
			}
			sql = "";
			sql = getQueryForNextResult(tableName, idApp, id, fromDate, toDate, dGroupCondition, checkName,
					columnValues);
			if ("".equals(sql)) {
				String statusCondition = "";
				if ("DATA_QUALITY_Unmatched_Default_Pattern_Data".equalsIgnoreCase(tableName)) {
					statusCondition = " and New_Pattern = 'N'";
					if (columnValues.has("status") && columnValues.getString("status").equalsIgnoreCase("NEW")) {
						statusCondition = " and New_Pattern = 'Y'";
					}

				}
				sql = "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and " + column + "='" + colName + "' and Date between '" + fromDate
						+ "' and '" + toDate + "' " + statusCondition;
			}
		}
		if (tableName.equalsIgnoreCase("DATA_QUALITY_Unmatched_Pattern_Data"))
			return getValidationResultsForRegexPatternCheck(sql, idApp);

		if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_SUMMARY")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY"))
			return getValidationResultsDataDrift(sql, idApp);

		if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Summary")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
			return getValidationResultsByCheckDetails(sql, tableName);
		}

		return getValidationResultsByCheckDetails(sql);
	}

	@Override
	public JSONArray getValidationResultsByCheckWithColNameNew(long idApp, String fromDate, String toDate,
			String tableName, String dGroupCondition, String colName, String column, String checkName,
			JSONObject columnValues, int offset, int records) {
		String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "COALESCE"
				: "ifnull";
		String sql = "";
		switch (checkName) {
		case "micro_null_check": {
			sql = "select *  from  DATA_QUALITY_Column_Summary where Id in(select Max(Id) from  DATA_QUALITY_Column_Summary where idApp="
					+ idApp + " and Date between '" + fromDate + "' and '" + toDate
					+ "' and Null_Threshold is not null and Status in ('passed','failed','PASSED','FAILED','Passed','Failed')  group by ColName, "
					+ ifnull_function + " (dGroupVal, '')) order by Id DESC limit " + records + " OFFSET "
					+ 1 * offset * records;
			break;
		}
		case "distribution_check": {
			sql = "select *,  CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
					+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
					+ " ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi   from  DATA_QUALITY_Column_Summary "
					+ "where Id in(select Max(Id) from  DATA_QUALITY_Column_Summary where idApp=" + idApp
					+ " and Null_Threshold is null " + " and Date between '" + fromDate + "' and '" + toDate
					+ "'  group by ColName," + ifnull_function + " (dGroupVal, '')) order by Id DESC limit " + records
					+ " OFFSET " + 1 * offset * records;
			break;
		}
		case "count_reasonability": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_A1")) {
				sql = "select * from  DATA_QUALITY_Transactionset_sum_A1 "
						+ "where Id in ( select max(Id) from DATA_QUALITY_Transactionset_sum_A1 where idApp =" + +idApp
						+ " and Date between '" + fromDate + "' and '" + toDate + "'  group by " + ifnull_function
						+ " (dGroupVal, '')) order by Id DESC limit " + records + " OFFSET " + 1 * offset * records;
			} else if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				sql = "select * " + dqiCondition + " from  DATA_QUALITY_Transactionset_sum_dgroup "
						+ "where Id in ( select max(Id) from DATA_QUALITY_Transactionset_sum_dgroup where idApp ="
						+ +idApp + " and Date between '" + fromDate + "' and '" + toDate
						+ "' and validity ='False' group by " + ifnull_function
						+ " (dGroupVal, '')) order by Id DESC limit " + records + " OFFSET " + 1 * offset * records;
			}
		}
		}

		LOG.debug("Result Query : " + sql);
		if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Summary")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
			return getValidationResultsByCheckDetails(sql, tableName);
		}
		return getValidationResultsByCheckDetails(sql);
	}

	@Override
	public int getResultCount(long idApp, String fromDate, String toDate, String tableName, String dGroupCondition,
			String checkName) {
		String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "COALESCE"
				: "ifnull";
		String sql = "";
		switch (checkName) {
		case "distribution_check": {
			sql = "select count(*) as recordCount  from  DATA_QUALITY_Column_Summary where Id in(select Max(Id) from  DATA_QUALITY_Column_Summary where idApp="
					+ idApp + " and Date between '" + fromDate + "' and '" + toDate
					+ "' and NumMeanThreshold IS NOT NULL  group by ColName," + ifnull_function + " (dGroupVal, ''))";
			break;
		}
		case "micro_null_check": {
			sql = "select count(*) as recordCount  from  DATA_QUALITY_Column_Summary where Id in(select Max(Id) from  DATA_QUALITY_Column_Summary where idApp="
					+ idApp + " and Date between '" + fromDate + "' and '" + toDate
					+ "' and Status in ('passed','failed','PASSED','FAILED','Passed','Failed')  group by ColName, "
					+ ifnull_function + " (dGroupVal, ''))";
			break;
		}
		case "count_reasonability": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_A1")) {
				sql = "select count(*) as recordCount from  DATA_QUALITY_Transactionset_sum_A1 "
						+ "where Id in ( select max(Id) from DATA_QUALITY_Transactionset_sum_A1 where idApp =" + +idApp
						+ " and Date between '" + fromDate + "' and '" + toDate + "'  group by " + ifnull_function
						+ " (dGroupVal, ''))";
			} else if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				sql = "select  count(*) as recordCount from  DATA_QUALITY_Transactionset_sum_A1 "
						+ "where Id in ( select max(Id) from DATA_QUALITY_Transactionset_sum_dgroup where idApp ="
						+ +idApp + " and Date between '" + fromDate + "' and '" + toDate
						+ "' and validity ='False' group by " + ifnull_function + " (dGroupVal, ''))";
			}
			break;
		}
		}
		LOG.debug("Result Query : " + sql);
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		while (queryForRowSet.next()) {
			return queryForRowSet.getInt("recordCount");
		}
		return 0;
	}

	@Override
	public JSONArray getValidationResultsByCheckInitialRecords(long idApp, String fromDate, String toDate,
			String tableName) {
		String sql = "select * from " + tableName + " where idApp = " + idApp + " and Date between '" + fromDate
				+ "' and '" + toDate + "' limit 50";
		LOG.debug("Result Query : " + sql);
		if (tableName.equalsIgnoreCase("DATA_QUALITY_Transaction_Summary")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
			return getValidationResultsByCheckDetails(sql, tableName);
		}
		return getValidationResultsByCheckDetails(sql);
	}

	private String getQueryForNextResult(String tableName, long idApp, int id, String fromDate, String toDate,
			String dGroupCondition, String checkName, JSONObject columnValues) {
		String microSegmentVal = "";
		String microSegmentValCon = "";

		// Query compatibility changes for both POSTGRES and MYSQL
		String binary_key = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "" : "BINARY";
		if (columnValues.has("dGroupVal")) {
			microSegmentVal = String.valueOf(columnValues.get("dGroupVal"));
			if (microSegmentVal != null && !microSegmentVal.equals("null")) {
				microSegmentValCon = " and dGroupVal like " + binary_key + " '" + microSegmentVal + "'";
			} else {
				microSegmentValCon = " and dGroupVal IS NULL";
			}
		}
		switch (checkName) {
		case "micro_null_check": {
			return "select * from " + tableName
					+ " where Status in ('passed','failed','PASSED','FAILED','Passed','Failed') and idApp = " + idApp
					+ " and id not in (" + id + ") " + dGroupCondition + " and ColName like " + binary_key + " '"
					+ columnValues.get("colName") + "' " + microSegmentValCon + " and Date between '" + fromDate
					+ "' and '" + toDate + "' ";
		}
		case "default_check": {
			return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
					+ dGroupCondition + " and ColName like " + binary_key + " '" + columnValues.get("colName")
					+ "' and Default_Value like " + binary_key + " '" + columnValues.get("defaultValue")
					+ "' and Date between '" + fromDate + "' and '" + toDate + "' ";
		}
		case "data_drift_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY"))
				return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and colName like " + binary_key + " '" + columnValues.get("colName")
						+ "' " + microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
		}
		case "distribution_check": {
			if (tableName.equals("DATA_QUALITY_Column_Summary")) {
				return "select *, CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "	ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi "
						+ "from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and ColName like " + binary_key + " '" + columnValues.get("colName")
						+ "' " + microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
			}
		}
		case "custom_distribution_check": {
			if (tableName.equals("DATA_QUALITY_Custom_Column_Summary")) {
				return "select *, CASE WHEN (RUN > 2 or (NumSDStatus IS NOT NULL)) THEN CASE WHEN ((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus ,("
						+ " CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "	ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi "
						+ "from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and ColName like " + binary_key + " '" + columnValues.get("colName")
						+ "' " + microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
			}
		}
		case "duplicate_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
				return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and duplicateCheckFields like " + binary_key + " '"
						+ columnValues.get("duplicateCheckFields") + "' " + microSegmentValCon + " and Date between '"
						+ fromDate + "' and '" + toDate + "' ";
			}
		}
		case "global_rules": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules"))
				return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and ruleName like " + binary_key + " '" + columnValues.get("colName")
						+ "' " + microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
		}
		case "record_anomaly_check": {
			if (tableName.equals("DATA_QUALITY_Record_Anomaly")) {
				return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + " and colName like " + binary_key + " '" + columnValues.get("colName")
						+ "' " + microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
			}
		}
		case "count_reasonability": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_A1")) {
				return "select * from " + tableName + " where idApp = " + idApp + " and id not in (" + id + ") "
						+ dGroupCondition + "  " + microSegmentValCon + " and Date between '" + fromDate + "' and '"
						+ toDate + "' ";
			} else if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				return "select * " + dqiCondition + " from " + tableName + " where idApp = " + idApp
						+ " and id not in (" + id + ") " + dGroupCondition + " " + microSegmentValCon
						+ " and Date between '" + fromDate + "' and '" + toDate + "' and validity ='False'";

			}
		}
		case "micro_rca_check": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				return "select *" + dqiCondition + " from " + tableName + " where idApp = " + idApp + " and id not in ("
						+ id + ") " + dGroupCondition + " " + microSegmentValCon + " and Date between '" + fromDate
						+ "' and '" + toDate + "'  and validity ='True'";
			}
		}
		default:
			return "";
		}
	}

	private String getQuery(String checkName, String tableName, long idApp, String fromDate, String toDate) {

		// Query compatibility changes for both POSTGRES and MYSQL
		String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "COALESCE"
				: "ifnull";
		String maxDate = getMaxDateForIdApp(tableName, fromDate, toDate, idApp);

		switch (checkName) {
		case "micro_null_check": {
			return "Select E.* from (select B.dGroupVal, B.ColName,B.Date,max(B.Run) as Run "
					+ "from (select dGroupVal, ColName,max(Date) as Date  from  " + tableName + " where idApp=" + idApp
					+ " and Date between '" + fromDate + "' and '" + toDate + "' "
					+ " group by ColName, dGroupVal)  S INNER JOIN  " + tableName + " B ON S.ColName= B.ColName and "
					+ ifnull_function + "(S.dGroupVal,'') = " + ifnull_function + "(B.dGroupVal,'') "
					+ " AND S.Date = B.Date AND idApp=" + idApp
					+ " group by B.ColName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
					+ " E ON E.ColName=D.ColName and " + ifnull_function + "(E.dGroupVal,'') = " + ifnull_function
					+ "(D.dGroupVal,'')  and E.Date = D.Date "
					+ "and E.Run = D.Run and E.Status in ('passed','failed','PASSED','FAILED','Passed','Failed') and idApp = "
					+ idApp;
		}
		case "default_check": {
			return "Select E.* from (select B.Default_Value, B.ColName,B.Date,max(B.Run) as Run "
					+ "from (select Default_Value, ColName,max(Date) as Date  from  " + tableName + " where idApp="
					+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
					+ " group by ColName, Default_Value)  S INNER JOIN  " + tableName
					+ "  B ON S.ColName= B.ColName and S.Default_Value = B.Default_Value "
					+ " AND S.Date = B.Date AND idApp=" + idApp
					+ " group by B.ColName, B.Default_Value, B.Date) D  INNER JOIN " + tableName
					+ " E ON E.ColName=D.ColName and E.Default_Value = D.Default_Value  and E.Date = D.Date "
					+ "and E.Run = D.Run and idApp = " + idApp;
		}
		case "data_drift_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY"))
//				return "Select E.* from (select B.dGroupVal, B.colName,B.Date,max(B.Run) as Run "
//						+ "from (select dGroupVal, colName,max(Date) as Date  from  " + tableName + " where idApp="
//						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
//						+ " group by colName, dGroupVal)  S INNER JOIN  " + tableName
//						+ "  B ON S.colName= B.colName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
//						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
//						+ " group by B.colName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
//						+ " E ON E.colName=D.colName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
//						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp;
//				
				return "select * from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where idApp=" + idApp
						+ " and run=(select max(run) as maxrun from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where idApp="
						+ idApp + " and Date = '" + maxDate + "') and Date = '" + maxDate
						+ "' order by Id desc limit 50000";
		}
		case "global_rules": {
			long fetchLimit = databuckUtility.getGlobalRulesFetchLimit();
			if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules"))
				return "select * from DATA_QUALITY_GlobalRules where idApp=" + idApp
						+ " and date=(select max(date) from DATA_QUALITY_GlobalRules " + "where idApp=" + idApp
						+ ") and run=(select max(run) as maxrun from DATA_QUALITY_GlobalRules where idApp=" + idApp
						+ " group by date " + "order by date desc limit 1) and Date between '" + fromDate + "' and '"
						+ toDate + "' order by status asc, Id desc limit " + fetchLimit;
		}
		case "distribution_check": {
			if (tableName.equals("DATA_QUALITY_Column_Summary")) {
				return "Select E.*, CASE WHEN (E.RUN > 2 or (E.NumSDStatus IS NOT NULL)) THEN CASE WHEN((ABS(E.SumOfNumStat-E.NumSumAvg)/E.NumSumStdDev) <= E.NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numSumStatus,("
						+ "	 CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "	 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from (select B.ColName, B.dGroupVal,B.Date,max(B.Run) as Run "
						+ "from (select ColName ,dGroupVal ,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by ColName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.ColName= B.ColName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.ColName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.ColName=D.ColName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'') and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.NumMeanThreshold IS NOT NULL";
			}
		}
		case "custom_distribution_check": {
			if (tableName.equals("DATA_QUALITY_Custom_Column_Summary")) {
				return "Select E.*, CASE WHEN (E.RUN > 2 or (E.NumSDStatus IS NOT NULL)) THEN CASE WHEN((ABS(E.SumOfNumStat-E.NumSumAvg)/E.NumSumStdDev) <= E.NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numSumStatus,("
						+ "	 CASE WHEN (NumMeanDeviation <= NumMeanThreshold)THEN 100 ELSE (CASE WHEN (NumMeanDeviation >=6)THEN 0 "
						+ "	 ELSE (100 - ( (ABS(NumMeanDeviation - NumMeanThreshold) *100) / ( 6 - NumMeanThreshold ) )) END) END) AS NumDqi from (select B.ColName, B.dGroupVal,B.Date,max(B.Run) as Run "
						+ "from (select ColName ,dGroupVal ,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by ColName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.ColName= B.ColName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.ColName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.ColName=D.ColName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'') and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.NumMeanThreshold IS NOT NULL";
			}
		}
		case "record_anomaly_check": {
			if (tableName.equals("DATA_QUALITY_Record_Anomaly")) {
				return "Select E.* from (select B.dGroupVal, B.colName,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, colName,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by colName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.colName= B.colName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.colName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.colName=D.colName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp;
			}
		}
		case "duplicate_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
				return "Select E.* from (select B.dGroupVal, B.duplicateCheckFields,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, duplicateCheckFields,max(Date) as Date  from  " + tableName
						+ " where idApp=" + idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by duplicateCheckFields, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.duplicateCheckFields= B.duplicateCheckFields and " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.duplicateCheckFields, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.duplicateCheckFields=D.duplicateCheckFields and " + ifnull_function
						+ "(E.dGroupVal,'')=" + ifnull_function + "(D.dGroupVal,'')  and E.Date = D.Date "
						+ "and E.Run = D.Run and idApp = " + idApp;
			}
		}
		case "count_reasonability": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_A1")) {
				return "Select E.* from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by  dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp + " group by  B.dGroupVal, B.Date) D  INNER JOIN "
						+ tableName + " E ON  " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp;
			} else if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				return "Select E.* " + dqiCondition + " from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ " and validity ='False' and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by  dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ " and B.validity ='False' group by  B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.validity ='False'";

			}
		}
		case "micro_rca_check": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				return "Select E.* " + dqiCondition + " from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ "  and validity ='True' and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by  dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ "  and B.validity ='True' group by  B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ "  and E.validity ='True'";
			}
		}
		default:
			return "";

		}
	}

	private String getMaxDateForIdApp(String tableName, String fromDate, String toDate, long idApp) {
		String sql = "select max(Date) as date from " + tableName + " where idApp=" + idApp + " and Date between '"
				+ fromDate + "' and '" + toDate + "'";
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		while (queryForRowSet.next()) {
			return queryForRowSet.getString("date");
		}
		return "";
	}

	private int getMaxId(String column, String tableName, long idApp, String colName, String checkName,
			JSONObject columnValues, String fromDate, String toDate) {
		String sql = getQueryForDiffCols(checkName, tableName, idApp, columnValues, fromDate, toDate);
		if ("".equals(sql)) {
			sql = "Select E.id from (select B." + column + ",B.Date,max(B.Run) as Run from (select " + column
					+ ",max(Date) as Date from  " + tableName + " where idApp=" + idApp + " and Date between '"
					+ fromDate + "' and '" + toDate + "' " + " group by " + column + ") S INNER JOIN  " + tableName
					+ " B ON S." + column + "= B." + column + " AND S.Date = B.Date AND idApp=" + idApp
					+ " group by B.Date,B." + column + ") D INNER JOIN " + tableName + " E ON E." + column + "=D."
					+ column + " and E.Date = D.Date and E.Run = D.Run and idApp = " + idApp + " and E." + column + "='"
					+ colName + "'";
		}
		LOG.debug("Fetching results for query: " + sql);
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		while (queryForRowSet.next()) {
			int id = queryForRowSet.getInt("id");
			return id;
		}
		return 0;
	}

	private String getQueryForDiffCols(String checkName, String tableName, long idApp, JSONObject columnValues,
			String fromDate, String toDate) {
		String microSegmentVal = "";
		String microSegmentValCon = "";

		// Query compatibility changes for both POSTGRES and MYSQL
		String binary_key = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "" : "BINARY";

		if (columnValues.has("dGroupVal")) {
			microSegmentVal = String.valueOf(columnValues.get("dGroupVal"));
			if (microSegmentVal != null && !microSegmentVal.equals("null")) {
				microSegmentValCon = " and E.dGroupVal like " + binary_key + "  '" + microSegmentVal + "'";
			} else {
				microSegmentValCon = " and E.dGroupVal IS NULL";
			}
		}
		// Query compatibility changes for both POSTGRES and MYSQL
		String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "COALESCE"
				: "ifnull";

		switch (checkName) {
		case "micro_null_check": {
			return "Select E.id from (select B.dGroupVal, B.ColName,B.Date,max(B.Run) as Run "
					+ "from (select dGroupVal, ColName,max(Date) as Date  from  " + tableName + " where idApp=" + idApp
					+ " and ColName ='" + columnValues.get("colName") + "' "
					+ microSegmentValCon.replaceAll("E.dGroupVal", "dGroupVal") + " and Date between '" + fromDate
					+ "' and '" + toDate + "' " + " group by ColName, dGroupVal)  S INNER JOIN  " + tableName
					+ " B ON S.ColName= B.ColName and " + ifnull_function + "(S.dGroupVal,'') = " + ifnull_function
					+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp + " and B.ColName ='"
					+ columnValues.get("colName") + "' " + microSegmentValCon.replaceAll("E.dGroupVal", "B.dGroupVal")
					+ " group by B.dGroupVal, B.ColName,B.Date) D  INNER JOIN " + tableName
					+ " E ON E.ColName=D.ColName and " + ifnull_function + "(E.dGroupVal,'') = " + ifnull_function
					+ "(D.dGroupVal,'')  and E.Date = D.Date "
					+ "and E.Run = D.Run and E.Status in ('passed','failed','PASSED','FAILED','Passed','Failed') and idApp = "
					+ idApp + " and E.ColName ='" + columnValues.get("colName") + "' " + microSegmentValCon;
		}
		case "global_rules": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_GlobalRules"))
				return "Select E.id from (select B.dGroupVal, B.ruleName,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, ruleName,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and ruleName ='" + columnValues.get("colName") + "' "
						+ microSegmentValCon.replaceAll("E.dGroupVal", "dGroupVal") + " and Date between '" + fromDate
						+ "' and '" + toDate + "' " + " group by ruleName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.ruleName= B.ruleName and " + ifnull_function + "(S.dGroupVal,'') = "
						+ ifnull_function + "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " and B.ruleName ='" + columnValues.get("colName") + "' "
						+ microSegmentValCon.replaceAll("E.dGroupVal", "B.dGroupVal")
						+ " group by B.dGroupVal, B.ruleName,B.Date) D  INNER JOIN " + tableName
						+ " E ON E.ruleName=D.ruleName and " + ifnull_function + "(E.dGroupVal,'') = " + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp;
		}
		case "default_check": {
			return "Select E.id from (select B.Default_Value, B.ColName,B.Date,max(B.Run) as Run "
					+ "from (select Default_Value, ColName,max(Date) as Date  from  " + tableName + " where idApp="
					+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' " + " and ColName='"
					+ columnValues.get("colName") + "' and Default_Value='" + columnValues.get("defaultValue")
					+ "' group by Default_Value, ColName)  S INNER JOIN  " + tableName
					+ "  B ON S.ColName= B.ColName and S.Default_Value = B.Default_Value "
					+ " AND S.Date = B.Date AND idApp=" + idApp + " and B.ColName='" + columnValues.get("colName")
					+ "' and B.Default_Value='" + columnValues.get("defaultValue")
					+ "' group by B.Default_Value, B.ColName,B.Date) D  INNER JOIN " + tableName
					+ " E ON E.ColName=D.ColName and E.Default_Value = D.Default_Value  and E.Date = D.Date "
					+ " and E.Run = D.Run and idApp = " + idApp;
		}
		case "data_drift_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY"))
				return "Select E.id from (select B.dGroupVal, B.colName,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, colName,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by colName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.colName= B.colName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.colName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.colName=D.colName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.ColName ='" + columnValues.get("colName") + "' " + microSegmentValCon;
		}
		case "duplicate_check": {
			if (tableName.equalsIgnoreCase("DATA_QUALITY_Duplicate_Check_Summary")) {
				return "Select E.id from (select B.dGroupVal, B.duplicateCheckFields,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, duplicateCheckFields,max(Date) as Date  from  " + tableName
						+ " where idApp=" + idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by duplicateCheckFields, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.duplicateCheckFields= B.duplicateCheckFields and " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.duplicateCheckFields, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.duplicateCheckFields=D.duplicateCheckFields and " + ifnull_function
						+ "(E.dGroupVal,'')=" + ifnull_function + "(D.dGroupVal,'')  and E.Date = D.Date "
						+ "and E.Run = D.Run and idApp = " + idApp + " and E.duplicateCheckFields ='"
						+ columnValues.get("duplicateCheckFields") + "' " + microSegmentValCon;
			}
		}

		case "distribution_check": {
			if (tableName.equals("DATA_QUALITY_Column_Summary")) {
//				return "select id from " + tableName + " where idApp = " + idApp + " and Run in (select max(Run) from "
//						+ tableName + " where " + " idApp = " + idApp + " and ColName ='" + columnValues.get("colName")
//						+ "' " + microSegmentValCon + " ) "+ " and Date in (select max(Date) from "
//						+ tableName + " where " + " idApp = " + idApp + " and ColName ='" + columnValues.get("colName")
//						+ "' " + microSegmentValCon + " ) " + " and ColName ='" + columnValues.get("colName") + "' "
//						+ microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
				return "Select E.id, CASE WHEN (E.RUN > 2 or (E.NumSDStatus IS NOT NULL)) THEN CASE WHEN((ABS(E.SumOfNumStat-E.NumSumAvg)/E.NumSumStdDev) <= E.NumSumThreshold) THEN 'passed' ELSE 'failed' END ELSE '' END AS numSumStatus from (select B.ColName, B.dGroupVal,B.Date,max(B.Run) as Run "
						+ "from (select ColName ,dGroupVal ,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by ColName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.ColName= B.ColName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.ColName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.ColName=D.ColName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'') and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.ColName ='" + columnValues.get("colName") + "' " + microSegmentValCon
						+ " and E.NumMeanThreshold IS NOT NULL";
			}
		}
		case "record_anomaly_check": {
			if (tableName.equals("DATA_QUALITY_Record_Anomaly")) {
//				return "select id from " + tableName + " where idApp = " + idApp + " and Run in (select max(Run) from "
//						+ tableName + " where " + " idApp = " + idApp + " and ColName ='" + columnValues.get("colName")
//						+ "' " + microSegmentValCon + " ) " + " and ColName ='" + columnValues.get("colName") + "' "
//						+ microSegmentValCon + " and Date between '" + fromDate + "' and '" + toDate + "' ";
				return "Select E.* from (select B.dGroupVal, B.colName,B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, colName,max(Date) as Date  from  " + tableName + " where idApp="
						+ idApp + " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by colName, dGroupVal)  S INNER JOIN  " + tableName
						+ "  B ON S.colName= B.colName and " + ifnull_function + "(S.dGroupVal,'')=" + ifnull_function
						+ "(B.dGroupVal,'') " + " AND S.Date = B.Date AND idApp=" + idApp
						+ " group by B.colName, B.dGroupVal, B.Date) D  INNER JOIN " + tableName
						+ " E ON E.colName=D.colName and " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.ColName ='" + columnValues.get("colName") + "' " + microSegmentValCon;
			}
		}
		case "count_reasonability": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_A1")) {
				return "Select E.id from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ " and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by  dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp + " group by B.dGroupVal, B.Date) D  INNER JOIN "
						+ tableName + " E ON " + ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp + " "
						+ microSegmentValCon;
			} else if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				String thresholdQuery = "select recordCountAnomalyThreshold from listApplications where idApp=" + idApp;
				Double threshold = jdbcTemplate.queryForObject(thresholdQuery, Double.class);
				String dqiCondition = ", (CASE WHEN (dGroupDeviation <= " + threshold
						+ ")THEN 100 ELSE (CASE WHEN (dGroupDeviation >=6)THEN 0 "
						+ "ELSE (100 - ( (ABS(dGroupDeviation - " + threshold + ") *100) / ( 6 - " + threshold
						+ " ) )) END) END) AS dgDqi";
				return "Select E.id " + dqiCondition + " from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ " and validity ='False' and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by  dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ " and B.validity ='False' group by  B.dGroupVal,B.Date) D  INNER JOIN " + tableName + " E ON "
						+ ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp
						+ " and E.validity ='False'" + microSegmentValCon;
			}
		}
		case "micro_rca_check": {
			if (tableName.equals("DATA_QUALITY_Transactionset_sum_dgroup")) {
				return "Select E.id from (select B.dGroupVal, B.Date,max(B.Run) as Run "
						+ "from (select dGroupVal, max(Date) as Date  from  " + tableName + " where idApp=" + idApp
						+ "  and validity ='True' and Date between '" + fromDate + "' and '" + toDate + "' "
						+ " group by dGroupVal)  S INNER JOIN  " + tableName + "  B ON " + ifnull_function
						+ "(S.dGroupVal,'')=" + ifnull_function + "(B.dGroupVal,'') "
						+ " AND S.Date = B.Date AND idApp=" + idApp
						+ " and  B.validity ='True' group by B.dGroupVal, B.Date) D  INNER JOIN " + tableName + " E ON "
						+ ifnull_function + "(E.dGroupVal,'')=" + ifnull_function
						+ "(D.dGroupVal,'')  and E.Date = D.Date " + "and E.Run = D.Run and idApp = " + idApp + " "
						+ microSegmentValCon + "  and E.validity ='True'";
			}
		}
		default:
			return "";
		}
	}

	@Override
	public List<ListDmCriteria> getDataFromListDMCriteria(Long idApp) {
		try {
			RowMapper<ListDmCriteria> rm = (rs, i) -> {
				ListDmCriteria ldmc = new ListDmCriteria();
				ldmc.setIdlistDMCriteria(rs.getLong("idlistDMCriteria"));
				ldmc.setIdDm(rs.getLong("idDM"));
				ldmc.setLeftSideExp(rs.getString("leftSideExp"));
				ldmc.setRightSideExp(rs.getString("rightSideExp"));
				ldmc.setIdLeftColumn(rs.getLong("idLeftColumn"));
				ldmc.setLeftSideColumn(rs.getString("leftSideColumn"));
				ldmc.setIdRightColumn(rs.getLong("idRightColumn"));
				ldmc.setRightSideColumn(rs.getString("rightSideColumn"));
				String matchType2 = rs.getString("matchType2");
				ldmc.setMatchType(matchType2);
				return ldmc;
			};
			String sql = "SELECT * FROM listDMRules JOIN listDMCriteria ON listDMRules.idDM=listDMCriteria.idDM where listDMRules.idApp=?";
			return jdbcTemplate.query(sql, rm, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public JSONArray getFilteredValidationResultsByCheck(long idApp, String fromDate, String toDate, String tableName,
			JSONObject filterAttribute, String dGroupCondition) {
		String filterCondition = "";
		Set<String> keySet = filterAttribute.keySet();
		for (String filterColumn : keySet) {
			try {
				String filterValue = "" + filterAttribute.get(filterColumn);
				if (!filterValue.isEmpty())
					filterCondition = filterCondition + " LOWER(" + filterColumn + ") LIKE '%" + filterValue + "%' and";
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		String numSumStatus = "";
		if (tableName.equalsIgnoreCase("DATA_QUALITY_Column_Summary")
				|| tableName.equalsIgnoreCase("DATA_QUALITY_Custom_Column_Summary")) {
			numSumStatus = " , CASE WHEN (RUN > 2 or (NumSDStatus IS NOT null)) THEN CASE WHEN((ABS(SumOfNumStat-NumSumAvg)/NumSumStdDev) <= NumSumThreshold) THEN 'passed' ELSE 'failed' END  ELSE '' END AS numSumStatus";
		}
		String sql = "select * " + numSumStatus + " from " + tableName + " where idApp = " + idApp + " and "
				+ filterCondition + " Date between '" + fromDate + "' and '" + toDate + "' " + dGroupCondition
				+ " ORDER BY Id DESC limit 100000";

		LOG.debug("\n====>sql:" + sql);

		if (tableName.equalsIgnoreCase("DATA_QUALITY_Unmatched_Pattern_Data"))
			return getValidationResultsForRegexPatternCheck(sql, idApp);

		return getValidationResultsByCheckDetailsForCSV(sql, tableName);
	}

	private JSONArray getValidationResultsByCheckDetails(String sql) {
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("<<==sqlQuery==>>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
			LOG.debug(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null) {
						resultObj.put(camCaseCol, camCaseCol.equals("dGroupVal") ? "null" : "");
					} else {

						if (object instanceof Double) {
							double dbldata = (double) object;
							resultObj.put(camCaseCol, new DecimalFormat("0.00").format(dbldata));

						} else {
							resultObj.put(camCaseCol, object);
						}

					}
				}
				if (resultObj != null) {
					if (resultObj.has("dGroupRcStatus") && resultObj.has("action")) {
						String dGroupRcStatus = resultObj.getString("dGroupRcStatus");
						String action = resultObj.getString("action");
						if (dGroupRcStatus.trim().equalsIgnoreCase("review")
								&& action.trim().equalsIgnoreCase("accepted")) {
							resultObj.put("dGroupRcStatus", "reviewed");
						}
					}
					resultArr.put(resultObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultArr;
	}

	private JSONArray getValidationResultsDataDrift(String sql, long idApp) {
		LOG.info("In getValidationResultsDataDrift Method");
		JSONArray resultArr = new JSONArray();
		try {
			LOG.debug("<<==sqlQuery==>>" + sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
			LOG.debug(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null) {
						resultObj.put(camCaseCol, camCaseCol.equals("dGroupVal") ? "null" : "");
					} else {

						if (object instanceof Double) {
							double dbldata = (double) object;
							resultObj.put(camCaseCol, new DecimalFormat("0.00").format(dbldata));

						} else {
							resultObj.put(camCaseCol, object);
						}

					}

					

				}
				if (resultObj != null) {
					if (resultObj.has("dGroupRcStatus") && resultObj.has("action")) {
						String dGroupRcStatus = resultObj.getString("dGroupRcStatus");
						String action = resultObj.getString("action");
						if (dGroupRcStatus.trim().equalsIgnoreCase("review")
								&& action.trim().equalsIgnoreCase("accepted")) {
							resultObj.put("dGroupRcStatus", "reviewed");
						}
					}
										
					if (resultObj.has("colName") ) {
					double threshold_value = ruleCatalogService.getRuleCatalogThresholdForChecks(idApp,
							resultObj.getString("colName"), "Data Drift Check");
					resultObj.put("threshold", threshold_value);
					}
					
					
					
					
					resultArr.put(resultObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultArr;
	}

	private String getMappedColForPostgres(String origionalColName) {

		if (origionalColName.isEmpty())
			return origionalColName;
		else {
			origionalColName = origionalColName.replaceAll("_", "");

			if (origionalColName.toLowerCase().equalsIgnoreCase("idApp".toLowerCase()))
				return "idApp";
			if (origionalColName.toLowerCase().equalsIgnoreCase("date".toLowerCase()))
				return "date";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dGroupCol".toLowerCase()))
				return "dGroupCol";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dGroupVal".toLowerCase()))
				return "dGroupVal";
			if (origionalColName.toLowerCase().equalsIgnoreCase("forgotRunEnabled".toLowerCase()))
				return "forgotRunEnabled";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dupcount".toLowerCase()))
				return "dupcount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("duplicateCheckValues".toLowerCase()))
				return "duplicateCheckValues";
			if (origionalColName.toLowerCase().equalsIgnoreCase("run".toLowerCase()))
				return "run";
			if (origionalColName.toLowerCase().equalsIgnoreCase("id".toLowerCase()))
				return "id";
			if (origionalColName.toLowerCase().equalsIgnoreCase("duplicateCheckFields".toLowerCase()))
				return "duplicateCheckFields";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalBadRecord".toLowerCase()))
				return "totalBadRecord";
			if (origionalColName.toLowerCase().equalsIgnoreCase("colName".toLowerCase()))
				return "colName";
			if (origionalColName.toLowerCase().equalsIgnoreCase("colVal".toLowerCase()))
				return "colVal";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalRecord".toLowerCase()))
				return "totalRecord";
			if (origionalColName.toLowerCase().equalsIgnoreCase("badDataPercentage".toLowerCase()))
				return "badDataPercentage";
			if (origionalColName.toLowerCase().equalsIgnoreCase("badDataThreshold".toLowerCase()))
				return "badDataThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("status".toLowerCase()))
				return "status";
			if (origionalColName.toLowerCase().equalsIgnoreCase("defaultValue".toLowerCase()))
				return "defaultValue";
			if (origionalColName.toLowerCase().equalsIgnoreCase("defaultPercentage".toLowerCase()))
				return "defaultPercentage";
			if (origionalColName.toLowerCase().equalsIgnoreCase("defaultCount".toLowerCase()))
				return "defaultCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalNumberOfRecords".toLowerCase()))
				return "totalNumberOfRecords";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalFailedRecords".toLowerCase()))
				return "totalFailedRecords";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dateField".toLowerCase()))
				return "dateField";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalRecords".toLowerCase()))
				return "totalRecords";
			if (origionalColName.toLowerCase().equalsIgnoreCase("failedRecordsPercentage".toLowerCase()))
				return "failedRecordsPercentage";
			if (origionalColName.toLowerCase().equalsIgnoreCase("patternsList".toLowerCase()))
				return "patternsList";
			if (origionalColName.toLowerCase().equalsIgnoreCase("patternThreshold".toLowerCase()))
				return "patternThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalMatchedRecords".toLowerCase()))
				return "totalMatchedRecords";
			if (origionalColName.toLowerCase().equalsIgnoreCase("newPattern".toLowerCase()))
				return "newPattern";
			if (origionalColName.toLowerCase().equalsIgnoreCase("csvFilePath".toLowerCase()))
				return "csvFilePath";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rADqi".toLowerCase()))
				return "rADqi";
			if (origionalColName.toLowerCase().equalsIgnoreCase("threshold".toLowerCase()))
				return "threshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("mean".toLowerCase()))
				return "mean";
			if (origionalColName.toLowerCase().equalsIgnoreCase("raDeviation".toLowerCase()))
				return "raDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("uniqueValues".toLowerCase()))
				return "uniqueValues";
			if (origionalColName.toLowerCase().equalsIgnoreCase("userName".toLowerCase()))
				return "userName";
			if (origionalColName.toLowerCase().equalsIgnoreCase("time".toLowerCase()))
				return "time";
			if (origionalColName.toLowerCase().equalsIgnoreCase("operation".toLowerCase()))
				return "operation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("recordCount".toLowerCase()))
				return "recordCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("missingValueCount".toLowerCase()))
				return "missingValueCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("newValueCount".toLowerCase()))
				return "newValueCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("uniqueValuesCount".toLowerCase()))
				return "uniqueValuesCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("TimelinessKey".toLowerCase()))
				return "timelinessKey";
			if (origionalColName.toLowerCase().equalsIgnoreCase("ruleName".toLowerCase()))
				return "ruleName";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalFailed".toLowerCase()))
				return "totalFailed";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalFailedCount".toLowerCase()))
				return "totalFailedCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("totalCount".toLowerCase()))
				return "totalCount";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dateFieldValues".toLowerCase()))
				return "dateFieldValues";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dateFieldCols".toLowerCase()))
				return "dateFieldCols";
			if (origionalColName.toLowerCase().equalsIgnoreCase("failureReason".toLowerCase()))
				return "failureReason";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dayOfMonth".toLowerCase()))
				return "dayOfMonth";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dayOfWeek".toLowerCase()))
				return "dayOfWeek";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dayOfYear".toLowerCase()))
				return "dayOfYear";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numDqi".toLowerCase()))
				return "numDqi";
			if (origionalColName.toLowerCase().equalsIgnoreCase("sumOfNumStat".toLowerCase()))
				return "sumOfNumStat";
			if (origionalColName.toLowerCase().equalsIgnoreCase("strCardinalityDeviation".toLowerCase()))
				return "strCardinalityDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("stringCardinalityAvg".toLowerCase()))
				return "stringCardinalityAvg";
			if (origionalColName.toLowerCase().equalsIgnoreCase("stringCardinalityStdDev".toLowerCase()))
				return "stringCardinalityStdDev";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numMeanAvg".toLowerCase()))
				return "numMeanAvg";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numMeanDeviation".toLowerCase()))
				return "numMeanDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numMeanStatus".toLowerCase()))
				return "numMeanStatus";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numMeanStdDev".toLowerCase()))
				return "numMeanStdDev";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numMeanThreshold".toLowerCase()))
				return "numMeanThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSDAvg".toLowerCase()))
				return "numSDAvg";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSDDeviation".toLowerCase()))
				return "numSDDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSDStatus".toLowerCase()))
				return "numSDStatus";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSDStdDev".toLowerCase()))
				return "numSDStdDev";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSDThreshold".toLowerCase()))
				return "numSDThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSumAvg".toLowerCase()))
				return "numSumAvg";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSumStatus".toLowerCase()))
				return "numSumStatus";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSumStdDev".toLowerCase()))
				return "numSumStdDev";
			if (origionalColName.toLowerCase().equalsIgnoreCase("numSumThreshold".toLowerCase()))
				return "numSumThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("outOfNormStatStatus".toLowerCase()))
				return "outOfNormStatStatus";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rulePercentage".toLowerCase()))
				return "rulePercentage";
			if (origionalColName.toLowerCase().equalsIgnoreCase("ruleThreshold".toLowerCase()))
				return "ruleThreshold";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcStdDev".toLowerCase()))
				return "rcStdDev";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcStdDevStatus".toLowerCase()))
				return "rcStdDevStatus";
			if (origionalColName.toLowerCase().equalsIgnoreCase("duplicateDataSet".toLowerCase()))
				return "duplicateDataSet";
			if (origionalColName.toLowerCase().equalsIgnoreCase("hourOfDay".toLowerCase()))
				return "hourOfDay";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dgDqi".toLowerCase()))
				return "dgDqi";
			if (origionalColName.toLowerCase().equalsIgnoreCase("dGroupDeviation".toLowerCase()))
				return "dGroupDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcDeviation".toLowerCase()))
				return "rcDeviation";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcMean".toLowerCase()))
				return "rcMean";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcMeanMovingAvg".toLowerCase()))
				return "rcMeanMovingAvg";
			if (origionalColName.toLowerCase().equalsIgnoreCase("rcMeanMovingAvgStatus".toLowerCase()))
				return "rcMeanMovingAvgStatus";
			else
				return origionalColName;

		}
	}

	private JSONArray getValidationResultsByCheckDetailsForCSV(String sql, String tableName) {
		JSONArray resultArr = new JSONArray();
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			SqlRowSetMetaData metaData = queryForRowSet.getMetaData();
			LOG.debug(sql);
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();
				int count = metaData.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String column_name = metaData.getColumnName(i);
					Object object = queryForRowSet.getObject(column_name);
					String camCaseCol = ToCamelCase.toCamelCase(column_name);
					camCaseCol = getMappedColForPostgres(camCaseCol);
					if (object == null)
						if (camCaseCol.equals("status"))
							resultObj.put(camCaseCol, "NA");
						else
							resultObj.put(camCaseCol, "-");
					else {
						if (object instanceof Double) {
//							BigDecimal value = new BigDecimal(Double.valueOf(String.valueOf(object)));
//							object = value.setScale(2, RoundingMode.HALF_UP);
							object = decimalFormat.format(Double.valueOf(String.valueOf(object)));
						}
						if ("DATA_QUALITY_Duplicate_Check_Summary".equalsIgnoreCase(tableName)) {
							if (camCaseCol.equalsIgnoreCase("type")) {
								if (String.valueOf(object).equalsIgnoreCase("all")) {
									object = "Individual";
								} else if (String.valueOf(object).equalsIgnoreCase("identity")) {
									object = "Composite";
								}
							}
						}
						resultObj.put(camCaseCol, object);
					}
				}
				if (resultObj != null)
					resultArr.put(resultObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultArr;
	}

	@Override
	public JSONArray getValidationResultsForDataDrift(long idApp, String fromDate, String toDate, String colName,
			JSONObject columnValues) {
		String sql = "";

		sql = "select * from DATA_QUALITY_DATA_DRIFT_SUMMARY where idApp = " + idApp + " and colName ='"
				+ columnValues.get("colName") + "' and Date between '" + fromDate + "' and '" + toDate
				+ "' order by Run DESC";
		return getValidationResultsByCheckDetails(sql);
	}

	@Override
	public int getFirstRunCount(long idApp) {
		String sql = "";
		sql = "select count(*) as dataCount from (select Run,Date from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY  where idApp="
				+ idApp + " group by Date,Run) a";
		return jdbcTemplate1.queryForObject(sql, Integer.class);
	}

}