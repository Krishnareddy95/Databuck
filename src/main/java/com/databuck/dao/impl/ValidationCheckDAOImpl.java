package com.databuck.dao.impl;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.datatemplate.*;
import com.databuck.service.IProjectService;
import com.databuck.util.DateUtility;
import com.databuck.util.DebugUtil;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Repository
public class ValidationCheckDAOImpl implements IValidationCheckDAO {

	private static final Logger LOG = Logger.getLogger(ValidationCheckDAOImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;

	@Autowired
	ITaskDAO oTaskDao;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	OracleRACConnection OracleRACConnection;

	@Autowired
	CassandraConnection cassandraconnection;

	@Autowired
	HiveConnection hiveconnection;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;

	@Autowired
	MSSQLConnection mSSQLConnection;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IProjectService IProjectservice;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	public List<String> getPrimaryKeyListDataDefinition(long idData) {
		try {
			List<String> listDataSources = new ArrayList<String>();
			String Sql = "SELECT  idColumn,displayName  from listDataDefinition  where Primarykey='y' AND idData="
					+ idData;
			List<String> listDataSource = jdbcTemplate.query(Sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {

					listDataSources.add(rs.getString("idColumn"));
					return rs.getString("displayName");
				}
			});
			return listDataSources;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public boolean checkWhetherTheSameDgroupAreSelectedInDataTemplate(String expression, Long leftSourceId) {
		try {
			String sql = "SELECT displayName FROM listDataDefinition WHERE dgroup='Y' AND idData=" + leftSourceId;
			Set<String> dgroup = new TreeSet<String>();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				dgroup.add(queryForRowSet.getString("displayName"));
			}
			Set<String> left = new TreeSet<String>();
			String[] split = expression.split("&&");
			if (split.length >= 1) {
				for (String str : split) {
					left.add(str.split("=")[0]);
				}
			}
			LOG.debug("dgroup=" + dgroup + "left=" + left);
			return dgroup.isEmpty() ? false : left.containsAll(dgroup);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return false;
	}

	public boolean checkIfTheFileIsProcessed(String filePath) {
		String sql = "SELECT  count(*) FROM  inProcessFiles WHERE  fileCompletePath='" + filePath + "'";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		if (count >= 1) {
			return true;
		}
		return false;
	}

	public void insertIntoInProcessFiles(String filePath) {

		String sql = "INSERT INTO inProcessFiles " + "(fileCompletePath, status) VALUES (?,?)";
		jdbcTemplate.update(sql, filePath, "Processed");
	}

	public boolean checkWhetherIncrementalForLeftTemplate(Long leftSourceId) {
		try {
			String sql = "SELECT displayName FROM listDataDefinition WHERE incrementalCol='Y' AND idData="
					+ leftSourceId;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return false;
	}

	public boolean checkDgroupColumnForLeftTemplate(Long leftSourceId) {
		String leftSql = "SELECT  count(*) FROM  listDataDefinition WHERE  dgroup =  'Y' AND idData =" + leftSourceId;
		Integer count = jdbcTemplate.queryForObject(leftSql, Integer.class);
		if (count == 1) {
			return true;
		}
		return false;
	}

	public boolean checkDgroupColumnForRightTemplate(Long rightSourceId) {
		String leftSql = "SELECT  count(*) FROM  listDataDefinition WHERE  dgroup =  'Y' AND idData =" + rightSourceId;
		Integer count = jdbcTemplate.queryForObject(leftSql, Integer.class);
		if (count == 1) {
			return true;
		}
		return false;
	}

	public boolean checkMeasurementColumnForLeftTemplate(Long leftSourceId) {
		String leftSql = "SELECT  count(*) FROM  listDataDefinition WHERE  measurement =  'Y' AND idData ="
				+ leftSourceId;
		Integer count = jdbcTemplate.queryForObject(leftSql, Integer.class);
		if (count == 1) {
			return true;
		}
		return false;
	}

	public void deleteEntryFromListDMRules(Long idApp, String type) {
		String sql = "delete from listDMRules where idApp=? and matchType2=?";
		int update = jdbcTemplate.update(sql, idApp, type);
		LOG.debug("deleteEntryFromListDMRules=" + update);
	}

	public int deleteEntryFromListDMRulesWithIdDm1(long idlistDMCriteria) {
		String sql = "delete from listDMCriteria where idlistDMCriteria=" + idlistDMCriteria;
		int update = jdbcTemplate.update(sql);
		LOG.debug("deleteEntryFromlistDMCriteriaWithIdDm=" + update);
		return update;
	}

	public void deleteEntryFromListDMRulesWithIdDm(long idlistDMCriteria) {
		String sql = "delete from listDMCriteria where idlistDMCriteria=" + idlistDMCriteria;
		int update = jdbcTemplate.update(sql);
		LOG.debug("deleteEntryFromlistDMCriteriaWithIdDm=" + update);
	}

	public void deleteFileManagementFromListFMRules(Long idApp) {
		String sql = "delete from listFMRules where idApp=" + idApp;
		int update = jdbcTemplate.update(sql);
		LOG.debug("deleteFileManagementFromListFMRules=" + update);
	}

	public boolean checkMeasurementColumnForRightTemplate(Long rightSourceId) {
		String leftSql = "SELECT  count(*) FROM  listDataDefinition WHERE  measurement =  'Y' AND idData ="
				+ rightSourceId;
		Integer count = jdbcTemplate.queryForObject(leftSql, Integer.class);
		if (count == 1) {
			return true;
		}
		return false;
	}

	public int insertIntoListFmRules(Long idApp, String dupCheck) {
		try {
			String sql = "INSERT INTO listFMRules " + "(idApp,dupCheck) VALUES (?,?)";
			return jdbcTemplate.update(sql, idApp, dupCheck);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public void updateDataIntoListApplications(Long idApp, Long rightSourceId, double absoluteThresholdId,
			String groupbyid, String measurementid, String dateFormat, String rightSliceEnd, String expression,
			String matchingRuleAutomatic, String recordCount, String primaryKey, double unMatchedAnomalyThreshold) {
		try {
			String sql = "update listApplications set idRightData=?,recordCountAnomalyThreshold=?,fileNameValidation=?,colOrderValidation=?"
					+ ",updateFrequency=?,applyDerivedColumns=?,keyGroupRecordCountAnomaly=?,outOfNormCheck=?,numericalStatCheck=?,"
					+ "stringStatCheck=?,groupEqualityThreshold=? where idApp=" + idApp;
			int update = jdbcTemplate.update(sql,
					new Object[] { rightSourceId, absoluteThresholdId, measurementid, groupbyid, dateFormat,
							rightSliceEnd, expression, matchingRuleAutomatic, recordCount, primaryKey,
							unMatchedAnomalyThreshold });
			LOG.debug("updateDataIntoListApplications=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateRollTargetSchemaIdForIdApp(Long idApp, Long targetSchemaId) {
		try {
			String sql = "update listApplications set rollTargetSchemaId=? where idApp=?";
			int update = jdbcTemplate.update(sql, new Object[] { targetSchemaId, idApp });
			LOG.debug("updateDataIntoListApplications=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void updateRollTypeForIdApp(Long idApp, String rollType) {
		try {
			String sql = "update listApplications set rollType=? where idApp=?";
			int update = jdbcTemplate.update(sql, new Object[] { rollType, idApp });
			LOG.debug("updateDataIntoListApplications=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateDataIntoListApplication(long idApp, long rightSourceId) {
		try {
			String sql = "update listApplications set idRightData=? where idApp=" + idApp;
			int update = jdbcTemplate.update(sql, new Object[] { rightSourceId });
			LOG.debug("updateDataIntoListApplications=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Long getIddmFromListDMRules(Long idApp, String matchType2) {
		try {
			String sql = "Select idDm FROM listDMRules where idApp=" + idApp + " and matchType2='" + matchType2 + "'";
			return jdbcTemplate.queryForObject(sql, Long.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return 0l;
	}

	public int checkFMConnectionDetailsForConnection(long schemaId) {
		int check = 0;
		try {
			String sql = "SELECT count(*) from fm_connection_details where idDataSchema=?";
			check = jdbcTemplate.queryForObject(sql, Integer.class, schemaId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return check;
	}

	public int updateFMConnectionDetails(long idApp, long schemaId) {
		int update = 0;
		try {
			String sql = "update fm_connection_details set idApp=? where idDataSchema=?";
			update = jdbcTemplate.update(sql, idApp, schemaId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return update;
	}

	public List<ListDmCriteria> getMatchingRules(Long idApp) {
		List<ListDmCriteria> matchingRules = new ArrayList<ListDmCriteria>();
		try {
			String sql = "Select idDm,matchType2 FROM listDMRules where idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			Map<Long, String> map = new HashMap<Long, String>();
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong("idDm"), queryForRowSet.getString("matchType2"));
			}

			for (Entry<Long, String> e : map.entrySet()) {

				SqlRowSet rs = jdbcTemplate.queryForRowSet(
						"select idlistDMCriteria,leftSideExp,rightSideExp,rightSideColumn,idRightColumn,idLeftColumn,leftSideColumn from listDMCriteria where idDM=?",
						e.getKey());
				while (rs.next()) {
					ListDmCriteria lmc = new ListDmCriteria();
					lmc.setIdDm(e.getKey());
					lmc.setIdlistDMCriteria(rs.getLong("idlistDMCriteria"));
					lmc.setLeftSideExp(rs.getString("leftSideExp"));
					lmc.setRightSideExp(rs.getString("rightSideExp"));
					lmc.setRightSideColumn(rs.getString("rightSideColumn"));
					lmc.setIdRightColumn(rs.getLong("idRightColumn"));
					lmc.setIdLeftColumn(rs.getLong("idLeftColumn"));
					lmc.setLeftSideColumn(rs.getString("leftSideColumn"));
					lmc.setRightColumn(rs.getLong("idRightColumn") + "-" + rs.getString("rightSideColumn"));
					lmc.setMatchType(e.getValue());
					matchingRules.add(lmc);
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return matchingRules;
	}

	public int updateintolistStatisticalMatchingConfig(listStatisticalMatchingConfig listStatisticalMatchingConfig) {
		try {
			// String[] split =
			// listStatisticalMatchingConfig.getExpression().split("=");
			LOG.debug("RecordType=" + listStatisticalMatchingConfig.getRecordCountType());
			String sql = "update listStatisticalMatchingConfig set leftSideExp=?,rightSideExp=?,recordCountType=?,recordCountThreshold=?,"
					+ "measurementSum=?,measurementSumType=?,measurementSumThreshold=?,measurementMean=?,measurementMeanType=?,measurementMeanThreshold=?,"
					+ "measurementStdDev=?,measurementStdDevType=?,measurementStdDevThreshold=?,groupBy=? where idApp=?";
			int update = jdbcTemplate.update(sql, listStatisticalMatchingConfig.getLeftSideExp(),
					listStatisticalMatchingConfig.getRightSideExp(), listStatisticalMatchingConfig.getRecordCountType(),
					listStatisticalMatchingConfig.getRecordCountThreshold(),
					listStatisticalMatchingConfig.getMeasurementSum(),
					listStatisticalMatchingConfig.getMeasurementSumType(),
					listStatisticalMatchingConfig.getMeasurementSumThreshold(),
					listStatisticalMatchingConfig.getMeasurementMean(),
					listStatisticalMatchingConfig.getMeasurementMeanType(),
					listStatisticalMatchingConfig.getMeasurementMeanThreshold(),
					listStatisticalMatchingConfig.getMeasurementStdDev(),
					listStatisticalMatchingConfig.getMeasurementStdDevType(),
					listStatisticalMatchingConfig.getMeasurementStdDevThreshold(),
					listStatisticalMatchingConfig.getGroupBy(), listStatisticalMatchingConfig.getIdApp());
			LOG.debug("update=" + update);
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int insertintolistStatisticalMatchingConfig(listStatisticalMatchingConfig listStatisticalMatchingConfig) {
		try {
			// String[] split =
			// listStatisticalMatchingConfig.getExpression().split("=");
			LOG.debug("RecordType=" + listStatisticalMatchingConfig.getRecordCountType());
			String sql = "insert into listStatisticalMatchingConfig  (idApp,leftSideExp,rightSideExp,recordCountType,recordCountThreshold,"
					+ "measurementSum,measurementSumType,measurementSumThreshold,measurementMean,measurementMeanType,measurementMeanThreshold,"
					+ "measurementStdDev,measurementStdDevType,measurementStdDevThreshold,groupBy) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int update = jdbcTemplate.update(sql, listStatisticalMatchingConfig.getIdApp(),
					listStatisticalMatchingConfig.getLeftSideExp(), listStatisticalMatchingConfig.getRightSideExp(),
					listStatisticalMatchingConfig.getRecordCountType(),
					listStatisticalMatchingConfig.getRecordCountThreshold(),
					listStatisticalMatchingConfig.getMeasurementSum(),
					listStatisticalMatchingConfig.getMeasurementSumType(),
					listStatisticalMatchingConfig.getMeasurementSumThreshold(),
					listStatisticalMatchingConfig.getMeasurementMean(),
					listStatisticalMatchingConfig.getMeasurementMeanType(),
					listStatisticalMatchingConfig.getMeasurementMeanThreshold(),
					listStatisticalMatchingConfig.getMeasurementStdDev(),
					listStatisticalMatchingConfig.getMeasurementStdDevType(),
					listStatisticalMatchingConfig.getMeasurementStdDevThreshold(),
					listStatisticalMatchingConfig.getGroupBy());
			LOG.debug("update=" + update);
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateMatchingRuleAutomaticIntoListDMCriteria(List<String> rightSourceColumnNames,
			List<String> leftSourceColumnNames, Long idDM, Long idApp) {
		try {
			// copy right data rightSourceColumnNameslower
			List<String> rightSourceColumnNamesCopy = new ArrayList<String>();
			for (String ir : rightSourceColumnNames) {
				rightSourceColumnNamesCopy.add(ir.toLowerCase());
			}
			// copy left data leftSourceColumnNameslower
			List<String> leftSourceColumnNamesCopy = new ArrayList<String>();
			for (String il : leftSourceColumnNames) {
				leftSourceColumnNamesCopy.add(il.toLowerCase());
			}
			List<String> matchedRules = new ArrayList<String>(rightSourceColumnNamesCopy);
			matchedRules.retainAll(leftSourceColumnNamesCopy);
			int i = 0;
			String rightSideExp = "", leftSideExp = "";
			for (String matchedRule : matchedRules) {
				// for loop right and find original right
				for (String ir : rightSourceColumnNames) {
					if (ir.equalsIgnoreCase(matchedRule)) {
						rightSideExp = ir;
						break;
					}
				}
				// for loop left and find original left
				for (String il : leftSourceColumnNames) {
					if (il.equalsIgnoreCase(matchedRule)) {
						leftSideExp = il;
						break;
					}
				}
				LOG.debug("matchedRule=" + matchedRule);
				boolean flag = checkForDuplicateEntryRuleInListDMCriteria(idDM, leftSideExp + "=" + rightSideExp);
				if (flag) {
					String sql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
					i = i + jdbcTemplate.update(sql, idDM, leftSideExp, rightSideExp);
				}
				// LOG.debug("update=" + update);
			}
			return i;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	boolean checkForDuplicateEntryRuleInListDMCriteria(Long idDM, String expression) {
		try {
			String sql = "select * from listDMCriteria where idDm=" + idDM;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			List<String> expressions = new ArrayList<String>();
			while (queryForRowSet.next()) {
				expressions
						.add(queryForRowSet.getString("leftSideExp") + "=" + queryForRowSet.getString("rightSideExp"));
			}
			if (!expressions.contains(expression)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public Long updateListDMRulesForPrimaryKeyMatching(Long idDM, Long idApp, Long rightSourceId, Long leftSourceId) {

		Long i = 0l;
		try {
			String rightSql = "SELECT displayName FROM listDataDefinition WHERE idData=" + rightSourceId
					+ " and primaryKey='Y'";
			SqlRowSet rightQueryForRowSet = jdbcTemplate.queryForRowSet(rightSql);
			List<String> rightSourceColumnNames = new ArrayList<String>();
			while (rightQueryForRowSet.next()) {
				rightSourceColumnNames.add(rightQueryForRowSet.getString("displayName"));
			}
			LOG.debug("rightSourceColumnNames=" + rightSourceColumnNames);
			String leftSql = "SELECT displayName FROM listDataDefinition WHERE idData=" + leftSourceId
					+ " and primaryKey='Y'";
			SqlRowSet leftQueryForRowSet = jdbcTemplate.queryForRowSet(leftSql);
			List<String> leftSourceColumnNames = new ArrayList<String>();
			while (leftQueryForRowSet.next()) {
				leftSourceColumnNames.add(leftQueryForRowSet.getString("displayName"));
			}
			LOG.debug("leftSourceColumnNames=" + leftSourceColumnNames);

			// Fetch All Column Names From List Data Definition
			String rightFullSql = "SELECT displayName FROM listDataDefinition WHERE idData=" + rightSourceId;
			SqlRowSet rightFullQueryForRowSet = jdbcTemplate.queryForRowSet(rightFullSql);
			List<String> rightSourceFullColumnNames = new ArrayList<String>();
			while (rightFullQueryForRowSet.next()) {
				rightSourceFullColumnNames.add(rightFullQueryForRowSet.getString("displayName"));
			}
			LOG.debug("rightSourceFullColumnNames=" + rightSourceFullColumnNames);
			String leftFullSql = "SELECT displayName FROM listDataDefinition WHERE idData=" + leftSourceId;
			SqlRowSet leftFullQueryForRowSet = jdbcTemplate.queryForRowSet(leftFullSql);
			List<String> leftFullSourceColumnNames = new ArrayList<String>();
			while (leftFullQueryForRowSet.next()) {
				leftFullSourceColumnNames.add(leftFullQueryForRowSet.getString("displayName"));
			}
			LOG.debug("leftFullSourceColumnNames=" + leftFullSourceColumnNames);
			// copy right data rightSourceColumnNameslower
			List<String> rightSourceColumnNamesCopy = new ArrayList<String>();
			for (String ir : rightSourceColumnNames) {
				rightSourceColumnNamesCopy.add(ir.toLowerCase());
			}
			// copy left data leftSourceColumnNameslower
			List<String> leftSourceColumnNamesCopy = new ArrayList<String>();
			for (String il : leftSourceColumnNames) {
				leftSourceColumnNamesCopy.add(il.toLowerCase());
			}
			// copy right data FULLrightSourceColumnNameslower
			List<String> rightSourceFullColumnNamesCopy = new ArrayList<String>();
			for (String ir : rightSourceFullColumnNames) {
				rightSourceFullColumnNamesCopy.add(ir.toLowerCase());
			}
			// copy left data FULLleftSourceColumnNameslower
			List<String> leftSourceFullColumnNamesCopy = new ArrayList<String>();
			for (String il : leftFullSourceColumnNames) {
				leftSourceFullColumnNamesCopy.add(il.toLowerCase());
			}
			List<String> rightMatchedRules = new ArrayList<String>(rightSourceColumnNamesCopy);
			rightMatchedRules.retainAll(leftSourceFullColumnNamesCopy);
			List<String> leftMatchedRules = new ArrayList<String>(leftSourceColumnNamesCopy);
			leftMatchedRules.retainAll(rightSourceFullColumnNamesCopy);
			String rightSideExp = "", leftSideExp = "";
			LOG.debug("rightMatchedRules=" + rightMatchedRules);
			LOG.debug("leftMatchedRules=" + leftMatchedRules);

			for (String matchedRule : rightMatchedRules) {
				// for loop right and find original right
				for (String ir : rightSourceColumnNames) {
					if (ir.equalsIgnoreCase(matchedRule)) {
						rightSideExp = ir;
						break;
					}
				}
				// for loop left and find original left
				for (String il : leftFullSourceColumnNames) {
					if (il.equalsIgnoreCase(matchedRule)) {
						leftSideExp = il;
						break;
					}
				}
				LOG.debug(leftSideExp + "=" + rightSideExp);
				boolean flag = checkForDuplicateEntryRuleInListDMCriteria(idDM, leftSideExp + "=" + rightSideExp);
				if (flag) {
					try {
						String sql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
						i = i + jdbcTemplate.update(sql, idDM, leftSideExp, rightSideExp);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						e.printStackTrace();
					}
				}
			}
			for (String matchedRule : leftMatchedRules) {
				// for loop right and find original right
				for (String ir : rightSourceFullColumnNames) {
					if (ir.equalsIgnoreCase(matchedRule)) {
						rightSideExp = ir;
						break;
					}
				}
				// for loop left and find original left
				for (String il : leftSourceColumnNames) {
					if (il.equalsIgnoreCase(matchedRule)) {
						leftSideExp = il;
						break;
					}
				}
				LOG.debug(leftSideExp + "=" + rightSideExp);
				boolean flag = checkForDuplicateEntryRuleInListDMCriteria(idDM, leftSideExp + "=" + rightSideExp);
				if (flag) {
					try {
						String sql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
						i = i + jdbcTemplate.update(sql, idDM, leftSideExp, rightSideExp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return i;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public int insertDataIntoListDMCriteria(String expression, Long idDM, Long idApp) {
		try {
			/*
			 * List<ListDmCriteria> matchingRules = getMatchingRules(idApp); for
			 * (ListDmCriteria listDmCriteria : matchingRules) { String exp =
			 * listDmCriteria.getLeftSideExp() + "=" + listDmCriteria.getRightSideExp();
			 *
			 * if (Arrays.asList(expression.split("&&")).contains(exp)) { return -1; } }
			 */
			String s = expression;
			String[] split = s.split("&&");
			LOG.debug(split.length);
			int i = 0;
			if (split.length > 0) {
				for (String inner : split) {
					String[] split2 = inner.split("=");
					if (split2.length > 0) {
						/*
						 * String sql =
						 * "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (" +
						 * idDM+ ",";
						 */
						//// Query compatibility changes for POSTGRES
						String sql1 = "";
						if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
							sql1 = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?::integer,?,?)";
						} else {

							sql1 = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
						}

						List<String> lfExp = new ArrayList<String>(2);
						lfExp.add(idDM.toString());
						for (String finalword : split2) {
							/* sql = sql + "'" + finalword.trim() + "'" + ","; */
							lfExp.add(finalword.trim());
						}

						/*
						 * sql = sql.substring(0, sql.length() - 1) + ")"; LOG.debug(sql);
						 */
						LOG.debug(lfExp.get(1) + "=" + lfExp.get(2));
						boolean flag = checkForDuplicateEntryRuleInListDMCriteria(idDM,
								lfExp.get(1) + "=" + lfExp.get(2));
						if (flag) {

							int update = jdbcTemplate.update(sql1, lfExp.toArray());
							i = i + update;
						}
					}
				}
			}
			return i;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int insertDataIntoListDMCriteria(String expression, Long idDM, Long idApp, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols) {
		try {
			/*
			 * List<ListDmCriteria> matchingRules = getMatchingRules(idApp); for
			 * (ListDmCriteria listDmCriteria : matchingRules) { String exp =
			 * listDmCriteria.getLeftSideExp() + "=" + listDmCriteria.getRightSideExp();
			 *
			 * if (Arrays.asList(expression.split("&&")).contains(exp)) { return -1; } }
			 */
			String s = expression;
			String[] split = s.split("&&");
			LOG.debug(split.length);
			int i = 0;
			if (split.length > 0) {
				for (String inner : split) {
					String[] split2 = inner.split("=");
					if (split2.length > 0 && !leftMatchValueCols.contains(split2[0])
							&& !rightMatchValueCols.contains(split2[0])) {
						/*
						 * String sql =
						 * "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (" +
						 * idDM+ ",";
						 */
						//// Query compatibility changes for POSTGRES
						String sql1 = "";
						if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
							sql1 = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?::integer,?,?)";
						} else {

							sql1 = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
						}

						List<String> lfExp = new ArrayList<String>(2);
						lfExp.add(idDM.toString());
						for (String finalword : split2) {
							/* sql = sql + "'" + finalword.trim() + "'" + ","; */
							lfExp.add(finalword.trim());
						}

						/*
						 * sql = sql.substring(0, sql.length() - 1) + ")"; LOG.debug(sql);
						 */
						LOG.debug(lfExp.get(1) + "=" + lfExp.get(2));
						boolean flag = checkForDuplicateEntryRuleInListDMCriteria(idDM,
								lfExp.get(1) + "=" + lfExp.get(2));
						if (flag) {

							int update = jdbcTemplate.update(sql1, lfExp.toArray());
							i = i + update;
						}
					}
				}
			}
			return i;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public void insertDataIntoListDMCriteriaForMeasurementMatch(Long measurementIdDM, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols) {
		try {
			String sql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
			int count = 0;
			for (String leftColumn : leftMatchValueCols) {
				String rightColumn = rightMatchValueCols.get(count);
				jdbcTemplate.update(sql, measurementIdDM, leftColumn, rightColumn);

				++count;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public List<String> getDisplayNamesFromListDataDefinition(Long idData) {
		try {
			String sql = "select displayName from listDataDefinition where idData=" + idData + " order by displayName";
			List<String> listDataSource = jdbcTemplate.query(sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("displayName").trim();
				}
			});
			return listDataSource;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public List<String> getDisplayNamesForMeasurementKeys(Long idData) {
		try {
			String sql = "select displayName from listDataDefinition where idData=" + idData
					+ " and measurement not in('Y') order by displayName";
			List<String> listDataSource = jdbcTemplate.query(sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("displayName");
				}
			});
			return listDataSource;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public List getDataFromlistRefFunctions() {
		try {

			String sql = "select name from listRefFunctions";

			List<ListDataSource> listDataSource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

				@Override
				public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {

					ListDataSource listDataSource = new ListDataSource();

					listDataSource.setName(rs.getString("name"));

					return listDataSource;
				}
			});
			return listDataSource;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void deleteEntryFromListDMRulesWithIdApp(long idApp) {
		try {
			String sql = "delete from listDMCriteria where idDM IN (select idDM from listDMRules where idApp = " + idApp
					+ " )";
			int update = jdbcTemplate.update(sql);
			LOG.debug("deleteEntryFromlistDMCriteriaWithIdDm=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Long insertIntoListDMRules(Long idApp, String matchtype, String matchCategory) {
		try {
			String sql = "INSERT INTO listDMRules " + "(idApp,matchType,matchType2) VALUES (?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddm"
					: "idDM";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setLong(1, idApp);
					pst.setString(2, matchCategory);
					pst.setString(3, matchtype);
					return pst;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public String getNameFromListDataSources(Long idData) {
		try {
			String sql = "select name from listDataSources where idData=" + idData;
			String name = jdbcTemplate.queryForObject(sql, String.class);
			LOG.debug("name=" + name);
			return name;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public int updateintolistapplications(Long idApp, String numericalStats, String recordAnomaly, String nullCount,
			String stringStat) {
		try {
			String sql = "update listApplications set numericalStatCheck=?,recordAnomalyCheck=?,nonNullCheck=?,stringStatCheck=? where idApp="
					+ idApp;
			int update = jdbcTemplate.update(sql,
					new Object[] { numericalStats, recordAnomaly, nullCount, stringStat });
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int saveDataIntolistApplicationForDataMatchingGroup(Long idApp, Long secondSourceId,
			double absoluteThresholdId) {
		try {
			String sql = "update listApplications set idRightData=?,matchingThreshold=? where idApp=" + idApp;
			int update = jdbcTemplate.update(sql, new Object[] { secondSourceId, absoluteThresholdId });
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public String getTimeSeriesForIdApp(Long idApp) {
		String sql = "select timeSeries from listApplications where idApp=" + idApp;
		String timeSeries = jdbcTemplate.queryForObject(sql, String.class);
		LOG.debug("timeSeries=" + timeSeries);
		return timeSeries;
	}

	public long getNamefromlistDataSources(Long idApp) {
		try {
			String sql = "select idData from listDataSources where idData =(select idRightData from listApplications where idApp="
					+ idApp + ")";
			long idData = jdbcTemplate.queryForObject(sql, Long.class);
			return idData;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return 0l;
		}
		// return 0l;

	}

	public listStatisticalMatchingConfig getDataFromlistStatisticalMatchingConfig(Long idApp) {
		try {
			listStatisticalMatchingConfig listStatics = new listStatisticalMatchingConfig();
			String sql = "select * from listStatisticalMatchingConfig where idApp=" + idApp + " LIMIT 1";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			queryForRowSet.next();
			listStatics.setRecordCountType(queryForRowSet.getString("recordCountType"));
			listStatics.setRecordCountThreshold(queryForRowSet.getDouble("recordCountThreshold"));
			listStatics.setMeasurementSum(queryForRowSet.getString("measurementSum"));
			listStatics.setMeasurementSumType(queryForRowSet.getString("measurementSumType"));
			listStatics.setMeasurementSumThreshold(queryForRowSet.getDouble("measurementSumThreshold"));
			listStatics.setMeasurementMean(queryForRowSet.getString("measurementMean"));
			listStatics.setMeasurementMeanType(queryForRowSet.getString("measurementMeanType"));
			listStatics.setMeasurementMeanThreshold(queryForRowSet.getDouble("measurementMeanThreshold"));
			listStatics.setMeasurementStdDev(queryForRowSet.getString("measurementStdDev"));
			listStatics.setMeasurementStdDevType(queryForRowSet.getString("measurementStdDevType"));
			listStatics.setMeasurementStdDevThreshold(queryForRowSet.getDouble("measurementStdDevThreshold"));
			listStatics.setLeftSideExp(queryForRowSet.getString("leftSideExp"));
			listStatics.setGroupBy(queryForRowSet.getString("groupBy"));
			listStatics.setRightSideExp(queryForRowSet.getString("rightSideExp"));
			return listStatics;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Long getListApplicationsFromIdData(Long idData) {
		Long idApp = 0L;
		try {
			String sql = "select max(idApp) as idApp from listApplications where active='yes' and appType='Data Forensics' and idData="
					+ idData;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				idApp = queryForRowSet.getLong("idApp");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return idApp;
	}

	public Long getMaxValidationByIdData(Long idData) {
		Long idApp = 0L;
		try {
			String sql = "select max(idApp) as idApp from listApplications where idData=" + idData
					+ " and active='yes' and appType='Data Forensics' and keyGroupRecordCountAnomaly ='N'";

			LOG.debug("\n====sql: " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				idApp = queryForRowSet.getLong("idApp");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return idApp;
	}

	@Override
	public Long getMaxMicrosegmentValidationByIdData(Long idData) {
		Long idApp = 0L;
		try {
			String sql = "select max(idApp) as idApp from listApplications where idData=" + idData
					+ " and active='yes' and appType='Data Forensics' and keyGroupRecordCountAnomaly ='Y'";

			LOG.debug("\n====sql: " + sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				idApp = queryForRowSet.getLong("idApp");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return idApp;
	}

	public ListApplications getdatafromlistapplications(Long idApp) {
		try {
			ListApplications la = new ListApplications();
			String sql = "select * from listApplications" + " where idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			queryForRowSet.next();
			la.setIdApp(queryForRowSet.getLong("idApp"));
			la.setFileNameValidation(queryForRowSet.getString("fileNameValidation"));
			// la.setGarbageRows(queryForRowSet.getInt("garbageRows"));
			la.setEntityColumn(queryForRowSet.getString("entityColumn"));
			la.setColOrderValidation(queryForRowSet.getString("colOrderValidation"));
			la.setNumericalStatCheck(queryForRowSet.getString("numericalStatCheck"));
			la.setStringStatCheck(queryForRowSet.getString("stringStatCheck"));
			la.setTimelinessKeyChk(queryForRowSet.getString("timelinessKeyCheck"));
			la.setRecordAnomalyCheck(queryForRowSet.getString("recordAnomalyCheck"));
			la.setNonNullCheck(queryForRowSet.getString("nonNullCheck"));
			la.setDataDriftCheck(queryForRowSet.getString("dataDriftCheck"));
			la.setRecordCountAnomaly(queryForRowSet.getString("recordCountAnomaly"));
			la.setRecordCountAnomalyThreshold(queryForRowSet.getDouble("recordCountAnomalyThreshold"));
			la.setOutOfNormCheck(queryForRowSet.getString("outOfNormCheck"));
			la.setApplyRules(queryForRowSet.getString("applyRules"));
			la.setApplyDerivedColumns(queryForRowSet.getString("applyDerivedColumns"));
			la.setKeyGroupRecordCountAnomaly(queryForRowSet.getString("keyGroupRecordCountAnomaly"));
			la.setUpdateFrequency(queryForRowSet.getString("updateFrequency"));
			la.setFrequencyDays(queryForRowSet.getInt("frequencyDays"));

			la.setIncrementalMatching(queryForRowSet.getString("incrementalMatching"));
			la.setBuildHistoricFingerPrint(queryForRowSet.getString("buildHistoricFingerPrint"));
			la.setHistoricStartDate(queryForRowSet.getString("historicStartDate"));
			la.setHistoricEndDate(queryForRowSet.getString("historicEndDate"));
			la.setHistoricDateFormat(queryForRowSet.getString("historicDateFormat"));
			la.setCsvDir(queryForRowSet.getString("csvDir"));
			la.setGroupEquality(queryForRowSet.getString("groupEquality"));
			la.setGroupEqualityThreshold(queryForRowSet.getDouble("groupEqualityThreshold"));
			la.setIdData(queryForRowSet.getLong("idData"));
			la.setIdLeftData(queryForRowSet.getLong("idLeftData"));
			la.setPrefix1(queryForRowSet.getString("prefix1"));
			la.setPrefix2(queryForRowSet.getString("prefix2"));
			la.setTimeSeries(queryForRowSet.getString("timeSeries"));
			la.setIdRightData(queryForRowSet.getInt("idRightData"));
			la.setMatchingThreshold(queryForRowSet.getDouble("matchingThreshold"));
			la.setName(queryForRowSet.getString("name"));
			// la.setIncrementalTimestamp(queryForRowSet.getTimestamp("incrementalTimestamp"));
			la.setDefaultCheck(queryForRowSet.getString("defaultCheck"));
			la.setPatternCheck(queryForRowSet.getString("patternCheck"));
			// sumeet
			la.setBadData(queryForRowSet.getString("badData"));

			la.setlengthCheck(queryForRowSet.getString("lengthCheck"));

			la.setMaxLengthCheck(queryForRowSet.getString("maxLengthCheck"));

			// date rule changes 8jan2019

			la.setDateRuleChk(queryForRowSet.getString("dateRuleCheck"));
			la.setdGroupNullCheck(queryForRowSet.getString("dGroupNullCheck"));
			la.setdGroupDateRuleCheck(queryForRowSet.getString("dGroupDateRuleCheck"));
			la.setFileMonitoringType(queryForRowSet.getString("fileMonitoringType"));
			la.setAppType(queryForRowSet.getString("appType"));
			// la.setIdApp(idApp);
			la.setdGroupDataDriftCheck(queryForRowSet.getString("dGroupDataDriftCheck"));

			la.setRollTargetSchemaId(queryForRowSet.getLong("rollTargetSchemaId"));
			la.setThresholdsApplyOption(queryForRowSet.getInt("thresholdsApplyOption"));
			la.setContinuousFileMonitoring(queryForRowSet.getString("continuousFileMonitoring"));
			la.setRollType(queryForRowSet.getString("rollType"));
			la.setdGroupDataDriftCheck(queryForRowSet.getString("dGroupDataDriftCheck"));
			la.setProjectId(queryForRowSet.getLong("project_id"));
			la.setDomainId(queryForRowSet.getLong("domain_id"));
			la.setData_domain(queryForRowSet.getInt("data_domain_id"));
			la.setActive(queryForRowSet.getString("active"));

			la.setApproveStatus(queryForRowSet.getInt("approve_status"));
			la.setStagingApproveStatus(queryForRowSet.getInt("staging_approve_status"));
			la.setDefaultPatternCheck(queryForRowSet.getString("defaultPatternCheck"));
			la.setDescription(queryForRowSet.getString("description"));
			la.setValidityThreshold(queryForRowSet.getDouble("validityThreshold"));
			la.setReprofiling(queryForRowSet.getString("reprofiling"));
			la.setValidationJobSize(queryForRowSet.getString("validation_job_size"));
			String sql2 = "select * from listDFTranRule where idApp=" + idApp;
			SqlRowSet queryForRowSet2 = jdbcTemplate.queryForRowSet(sql2);
			List<Map<String, String>> duplicateCheckMaps = new ArrayList<>();
			while (queryForRowSet2.next()) {
				Map<String, String> map = new HashMap<>();
				map.put(queryForRowSet2.getString("type"),
						queryForRowSet2.getString("dupRow") + "," + queryForRowSet2.getDouble("threshold"));
				duplicateCheckMaps.add(map);
			}
			if (!duplicateCheckMaps.isEmpty()) {
				for (Map<String, String> map : duplicateCheckMaps) {
					if (map.containsKey("identity")) {
						la.setDupRowIdentity(map.get("identity").split(",")[0]);
						la.setDupRowIdentityThreshold(Double.valueOf(map.get("identity").split(",")[1]));
					} else if (map.containsKey("all")) {
						la.setDupRowAll(map.get("all").split(",")[0]);
						la.setDupRowAllThreshold(Double.valueOf(map.get("all").split(",")[1]));
					}
				}
			}
			return la;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List getDataFromListDataDefinition(Long idData) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "SELECT DISTINCT idData, nonNull, nullCountThreshold, incrementalCol, numericalStat, numericalThreshold, stringStat,startDate,endDate,timelinessKey,"
						+ " stringStatThreshold,recordAnomalyThreshold,dataDrift,dataDriftThreshold, defaultCheck, defaultValues "
						+ "FROM listDataDefinition WHERE idData ='" + idData + "' OFFSET 0 LIMIT 1";
			} else {
				sql = "SELECT DISTINCT idData, nonNull, nullCountThreshold, incrementalCol, numericalStat, numericalThreshold, stringStat,startDate,endDate,timelinessKey,"
						+ " stringStatThreshold,recordAnomalyThreshold,dataDrift,dataDriftThreshold, defaultCheck, defaultValues "
						+ "FROM listDataDefinition WHERE idData ='" + idData + "' LIMIT 0 , 1";
			}
			List<ListDataDefinition> listdatadefinition = jdbcTemplate.query(sql, new RowMapper<ListDataDefinition>() {

				@Override
				public ListDataDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
					ListDataDefinition alistdatadefinition = new ListDataDefinition();
					alistdatadefinition.setIdData(rs.getInt("idData"));
					alistdatadefinition.setNonNull(rs.getString("nonNull"));
					alistdatadefinition.setNullCountThreshold(rs.getDouble("nullCountThreshold"));
					alistdatadefinition.setIncrementalCol(rs.getString("incrementalCol"));
					alistdatadefinition.setNumericalStat(rs.getString("numericalStat"));
					alistdatadefinition.setNumericalThreshold(rs.getDouble("numericalThreshold"));
					alistdatadefinition.setStringStat(rs.getString("stringStat"));
					alistdatadefinition.setStringStatThreshold(rs.getDouble("stringStatThreshold"));
					alistdatadefinition.setRecordAnomalyThreshold(rs.getDouble("recordAnomalyThreshold"));
					alistdatadefinition.setDataDrift(rs.getString("dataDrift"));
					alistdatadefinition.setDataDriftThreshold(rs.getDouble("dataDriftThreshold"));

					alistdatadefinition.setStartDate(rs.getString("startDate"));
					alistdatadefinition.setEndDate(rs.getString("endDate"));
					alistdatadefinition.setTimelinessKey(rs.getString("timelinessKey"));
					alistdatadefinition.setDefaultCheck(rs.getString("defaultCheck"));
					alistdatadefinition.setDefaultValues(rs.getString("defaultValues"));
					return alistdatadefinition;
				}
			});
			return listdatadefinition;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public void updateintolistdfsetruleandtranrule(Long idApp, Double recordCountAnomalyThreshold,
			String duplicateCheck) {
		try {
			String sql = "update listDFSetRule set duplicateFile=? where idApp=" + idApp;
			int update = jdbcTemplate.update(sql, duplicateCheck);
			LOG.debug("update=" + update);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
	}

	public int insertintolistdfsetruleandtranrule(Long idApp, Double dFSetComparisonnull, String duplicateCheck) {
		try {
			if (dFSetComparisonnull == 0.0)
				dFSetComparisonnull = 3.0;
			Long idDFSet = insertintolistDFSetRule(idApp, duplicateCheck);
			LOG.debug("insertintolistDFSetRule idDFSet=" + idDFSet);
			String sql = "insert into listDFSetComparisonRule(idDFSet,comparisonType,comparisonMethod,comparisonDuration,threshold)"
					+ " VALUES (?,?,?,?,?)";
			int update = jdbcTemplate.update(sql,
					new Object[] { idDFSet, "History", "Std Deviation", 0, dFSetComparisonnull });
			LOG.debug("listDFSetComparisonRule=" + update);
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistDFSetComparisonRule(Long idApp, String duplicateCheck) {
		try {
			int up = updateintolistDFSetRule(idApp, duplicateCheck);

			return up;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public String getDuplicateCheckFromListDFSetRule(Long idApp) {
		try {
			String sql = "Select duplicateFile from listDFSetRule where idApp=" + idApp + " LIMIT 1";
			String duplicateFile = jdbcTemplate.queryForObject(sql, String.class);
			return duplicateFile;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return null;
	}

	public Long insertintolistDFSetRule(Long idApp, String duplicateCheck) {
		try {
			LOG.info("inserting into listDFSetRule");
			String sql = "insert into listDFSetRule(idApp,count,sum,correlation,statisticalParam,duplicateFile)"
					+ " VALUES (?,?,?,?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iddfset"
					: "idDFSet";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setLong(1, idApp);
					pst.setString(2, "Y");
					pst.setString(3, "Y");
					pst.setString(4, "Y");
					pst.setString(5, "Y");
					pst.setString(6, duplicateCheck);
					return pst;
				}
			}, keyHolder);
			// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int updateintolistDFSetRule(Long idApp, String duplicateCheck) {
		try {
			LOG.info("updating into listDFSetRule");
			String sql = "update listDFSetRule set duplicateFile=? where idApp=" + idApp;
			int update = jdbcTemplate.update(sql, new Object[] { duplicateCheck });
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updatedataintolistdftranrule(Long idApp, Long thresholdAll, Long thresholdIdentity) {
		try {
			String sql = "update listDFTranRule set threshold=? where type='all' and idApp=" + idApp;
			String sql1 = "update listDFTranRule set threshold=? where type='identity' and idApp=" + idApp;
			int update = jdbcTemplate.update(sql, new Object[] { thresholdAll });
			int update2 = jdbcTemplate.update(sql1, new Object[] { thresholdIdentity });
			LOG.debug("update+update2" + update + update2);
			return update + update2;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public Map getdatafromlistdftranrule(Long idApp) {
		try {
			String sql = "select threshold from listDFTranRule where type='all' and idApp=" + idApp;
			Integer thresholdall = jdbcTemplate.queryForObject(sql, Integer.class);
			LOG.debug("thresholdall=" + thresholdall);
			String sql1 = "select threshold from listDFTranRule where type='identity' and idApp=" + idApp;
			Integer thresholdidentity = jdbcTemplate.queryForObject(sql1, Integer.class);
			LOG.debug("thresholdidentity=" + thresholdidentity);
			Map map = new HashMap<>();
			map.put(thresholdall, thresholdidentity);
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Multimap<String, Double> getDataFromListDfTranRule(Long idApp) {
		/*
		 * try { SqlRowSet queryForRowSet = jdbcTemplate1 .queryForRowSet(
		 * "select appID,AppName from result_master_table where  AppType='DF' " );
		 *
		 * Map<Long, String> map = new HashMap<Long, String>(); while
		 * (queryForRowSet.next()) { map.put(queryForRowSet.getLong(1),
		 * queryForRowSet.getString(2)); } return map;
		 */
		Multimap<String, Double> map = LinkedListMultimap.create();
		try {

			String sql = "select threshold,dupRow from listDFTranRule where type='all' and idApp=" + idApp;
			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				LOG.debug(queryForRowSet.getString(2) + "/" + queryForRowSet.getDouble(1));
				map.put(queryForRowSet.getString(2), queryForRowSet.getDouble(1));
			}
			String sql1 = "select threshold,dupRow from listDFTranRule where type='identity' and idApp=" + idApp;
			SqlRowSet queryForRowSet1 = jdbcTemplate.queryForRowSet(sql1);
			while (queryForRowSet1.next()) {
				LOG.debug(queryForRowSet1.getString(2) + "/" + queryForRowSet1.getDouble(1));
				map.put(queryForRowSet1.getString(2), queryForRowSet1.getDouble(1));
			}
			LOG.debug("map=" + map);
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int updateintolistdftranrule(Long idApp, String duplicateCount, Double duplicateCountTextNull,
			String duplicateCountAll, Double duplicateCountAllTextNull) {
		LOG.info("\n====>Inside updateintolistdftranrule");
		try {
			int update = 0;
			if (duplicateCount == null)
				duplicateCount = "";
			if (duplicateCountAll == null)
				duplicateCountAll = "";
			// LOG.debug(idApp+""+duplicateCount+""+duplicateCountTextNull+""+duplicateCountAll+""+duplicateCountAllTextNull);
			if (duplicateCount.equals("Y") && duplicateCountAll.equals("Y")) {
				/*
				 * UPDATE Customers SET City = 'Hamburg' WHERE CustomerID = 1;
				 */

				String query = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query,
						new Object[] { "Y", "N", duplicateCountTextNull, "identity", 0, idApp, "identity" });
				LOG.debug("update listDFTranRule=" + update);

				String query1 = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query1,
						new Object[] { "Y", "N", duplicateCountAllTextNull, "all", 0, idApp, "all" });
				LOG.debug("update=" + update);

			} else if (duplicateCount.equals("Y")) {

				String query = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query,
						new Object[] { "Y", "N", duplicateCountTextNull, "identity", 0, idApp, "identity" });
				LOG.debug("update listDFTranRule=" + update);
				String query1 = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query1,
						new Object[] { "N", "N", duplicateCountAllTextNull, "all", 0, idApp, "all" });
				LOG.debug("update=" + update);

			} else if (duplicateCountAll.equals("Y")) {

				String query = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query,
						new Object[] { "N", "N", duplicateCountTextNull, "identity", 0, idApp, "identity" });
				LOG.debug("update listDFTranRule=" + update);
				String query1 = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query1,
						new Object[] { "Y", "N", duplicateCountAllTextNull, "all", 0, idApp, "all" });
				LOG.debug("update=" + update);
			} else {
				String query = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query,
						new Object[] { "N", "N", duplicateCountTextNull, "identity", 0, idApp, "identity" });
				LOG.debug("update listDFTranRule=" + update);
				String query1 = "update listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
				update = jdbcTemplate.update(query1,
						new Object[] { "N", "N", duplicateCountAllTextNull, "all", 0, idApp, "all" });
				LOG.debug("update=" + update);

			}
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int insertintolistdftranrule(Long idApp, String duplicateCount, Double duplicateCountTextNull,
			String duplicateCountAll, Double duplicateCountAllTextNull) {
		LOG.info("\n====>Inside insertintolistdftranrule");
		try {
			int update = 0;
			if (duplicateCount == null)
				duplicateCount = "";
			if (duplicateCountAll == null)
				duplicateCountAll = "";
			// LOG.debug(idApp+""+duplicateCount+""+duplicateCountTextNull+""+duplicateCountAll+""+duplicateCountAllTextNull);
			if (duplicateCount.equals("Y") && duplicateCountAll.equals("Y")) {

				String query = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query,
						new Object[] { idApp, "Y", "N", duplicateCountTextNull, "identity", 0 });
				LOG.debug("update=" + update);

				String query1 = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query1,
						new Object[] { idApp, "Y", "N", duplicateCountAllTextNull, "all", 0 });
				LOG.debug("update=" + update);

			} else if (duplicateCount.equals("Y")) {

				String query = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query,
						new Object[] { idApp, "Y", "N", duplicateCountTextNull, "identity", 0 });
				LOG.debug("update=" + update);

				String query1 = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query1, new Object[] { idApp, "N", "N", 0, "all", 0 });
				LOG.debug("update=" + update);

			} else if (duplicateCountAll.equals("Y")) {

				String query1 = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query1,
						new Object[] { idApp, "Y", "N", duplicateCountAllTextNull, "all", 0 });
				LOG.debug("update=" + update);

				String query = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "identity", 0 });
				LOG.debug("update=" + update);
			} else {
				String query1 = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query1, new Object[] { idApp, "N", "N", 0, "all", 0 });
				LOG.debug("update=" + update);

				String query = "insert into listDFTranRule"
						+ "(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
				update = jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "identity", 0 });
				LOG.debug("update=" + update);

			}
			return update;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	// 24_DEC_2018 (12.43pm) Priyanka

	public int updateintolistapplicationforCustomize(String outofNorm, String columnOrderVal, String fileNameVal,
			double d, String nameofEntityColumn, Long idApp, String nullCount, String recordAnomaly,
			String numericalStats, String stringStat, String dataDriftCheck, String updateFrequency,
			int frequencyDaysnull, String recordCount, Double dFSetComparisonnull, String timeSeries,
			String recordCountAnomalyType, String applyRules, String applyDerivedColumns, String badData,
			String lengthCheck, String maxLengthCheck, String dateRuleCheck, ListApplications listApplication) {
		try {
			String keyGroupRecordCountAnomaly = "N";
			if (listApplication.getColOrderValidation() == null)
				listApplication.setColOrderValidation("");
			if (listApplication.getFileNameValidation() == null)
				listApplication.setFileNameValidation("");
			if (listApplication.getNonNullCheck() == null)
				listApplication.setNonNullCheck("N");
			if (listApplication.getNumericalStatCheck() == null)
				listApplication.setNumericalStatCheck("N");
			if (listApplication.getStringStatCheck() == null)
				listApplication.setStringStatCheck("N");
			if (listApplication.getRecordAnomalyCheck() == null)
				listApplication.setRecordAnomalyCheck("N");
			if (listApplication.getEntityColumn() == null)
				listApplication.setEntityColumn("");
			if (listApplication.getDataDriftCheck() == null)
				listApplication.setDataDriftCheck("N");
			if (listApplication.getUpdateFrequency() == null)
				listApplication.setUpdateFrequency("Never");
			if (listApplication.getRecordCountAnomaly() == null)
				listApplication.setRecordCountAnomaly("N");
			if (listApplication.getTimeSeries().equals(""))
				listApplication.setTimeSeries("None");
			if (listApplication.getApplyRules() == null)
				listApplication.setApplyRules("N");
			if (listApplication.getApplyDerivedColumns() == null)
				listApplication.setApplyDerivedColumns("N");
			if (listApplication.getBadData() == null)
				listApplication.setBadData("N");

			LOG.info("--------------updateintolistapplicationforCustomize------------------- ");

			if (listApplication.getlengthCheck() == null)
				listApplication.setlengthCheck("N");

			if (listApplication.getMaxLengthCheck() == null)
				listApplication.setMaxLengthCheck("N");

			// date rule changes 8jan2019
			if (listApplication.getDateRuleChk() == null)
				listApplication.setDateRuleChk("N");

			/*
			 * if(recordCountAnomalyType.equalsIgnoreCase("RecordCountAnomaly")) {
			 * listApplication.setRecordCountAnomaly("Y"); }else
			 * if(recordCountAnomalyType.equalsIgnoreCase( "KeyBasedRecordCountAnomaly")){
			 * keyGroupRecordCountAnomaly="Y"; }
			 */
			if (listApplication.getDataDriftCheck() != null) {
				if (listApplication.getDataDriftCheck().equalsIgnoreCase("Y"))
					listApplication.setStringStatCheck("Y");
			}
			if (listApplication.getOutOfNormCheck() != null) {
				if (listApplication.getOutOfNormCheck().equalsIgnoreCase("Y")) {
					listApplication.setNumericalStatCheck("Y");
				}
			}
			/*
			 * if(listApplication.getHistoricStartDate()!=""){ }else{
			 * listApplication.setHistoricStartDate("2017-01-01 00:00:00"); }
			 * if(listApplication.getHistoricEndDate()!=""){ }else{
			 * listApplication.setHistoricEndDate("2017-01-01 00:00:00"); }
			 */
			if (listApplication.getHistoricStartDate() == "") {
				// LOG.debug("NULL");
				listApplication.setHistoricStartDate(null);
			} else {
				// listApplication.setHistoricStartDate("2017-01-01 00:00:00");
			}
			if (listApplication.getHistoricEndDate() == "") {
				listApplication.setHistoricEndDate(null);
			} else {
				// listApplication.setHistoricEndDate("2017-01-01 00:00:00");
			}

			if (listApplication.getGroupEquality() == null) {
				listApplication.setGroupEquality("N");
			}
			LOG.debug("listApplication.getHistoricStartDate()=" + listApplication.getHistoricStartDate());
			if (listApplication.getIncrementalMatching() == null)
				listApplication.setIncrementalMatching("N");
			LOG.debug("numericalStats=" + numericalStats);
			String sql = "update listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,"
					+ "numericalStatCheck=?,stringStatCheck=?," + "recordAnomalyCheck=?,dataDriftCheck=?,"
					+ "updateFrequency=?,frequencyDays=?,recordCountAnomalyThreshold=?,"
					+ "timeSeries=?,outOfNormCheck=?,applyRules=?, " + "applyDerivedColumns=?,incrementalMatching=?,"
					+ "buildHistoricFingerPrint=?,historicStartDate=?,"
					+ "historicEndDate=?,historicDateFormat=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,badData=?,lengthCheck=?,maxLengthCheck=?,dateRuleCheck=?, defaultPatternCheck=? where idApp="
					+ idApp;

			return jdbcTemplate.update(sql,
					new Object[] { listApplication.getColOrderValidation(), listApplication.getFileNameValidation(),
							listApplication.getEntityColumn(), listApplication.getNonNullCheck(),
							listApplication.getNumericalStatCheck(), listApplication.getStringStatCheck(),
							listApplication.getRecordAnomalyCheck(), listApplication.getDataDriftCheck(),
							listApplication.getUpdateFrequency(), listApplication.getFrequencyDays(),
							listApplication.getRecordCountAnomalyThreshold(), listApplication.getTimeSeries(),
							listApplication.getOutOfNormCheck(), listApplication.getApplyRules(),
							listApplication.getApplyDerivedColumns(), listApplication.getIncrementalMatching(),
							listApplication.getBuildHistoricFingerPrint(), listApplication.getHistoricStartDate(),
							listApplication.getHistoricEndDate(), listApplication.getHistoricDateFormat(),
							listApplication.getCsvDir(), listApplication.getGroupEquality(),
							listApplication.getGroupEqualityThreshold(), listApplication.getBadData(),
							listApplication.getlengthCheck(), listApplication.getMaxLengthCheck(),
							listApplication.getDateRuleChk(), listApplication.getDefaultPatternCheck() });
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * 24_DEC_2018 (12.43pm) Priyanka Pradeep 8-Mar-2020 Global threshold changes:
	 * 'thresholdsApplyOption' new field added / saved in this method.
	 */

	public int updateintolistapplicationForAjaxRequest(ListApplications listApplications) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "update listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,timelinessKeyCheck=?,"
						+ "defaultCheck=?, recordAnomalyCheck=?,dataDriftCheck=?,updateFrequency=?,frequencyDays=?,"
						+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,timeSeries=?,keyGroupRecordCountAnomaly=?,"
						+ "outOfNormCheck=?,applyRules=?, applyDerivedColumns=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
						+ "buildHistoricFingerPrint=?,historicStartDate=?::Date,historicEndDate=?::Date,historicDateFormat=?,patternCheck=?,"
						+ "dateRuleCheck=?,badData=?,lengthCheck=?, maxLengthCheck=?,dGroupNullCheck=?, dGroupDateRuleCheck=?, validityThreshold=?, dGroupDataDriftCheck=?, "
						+ "thresholdsApplyOption=?,data_domain_id=?, defaultPatternCheck= ?,reprofiling=? where idApp="
						+ listApplications.getIdApp();
			else
				sql = "update listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,timelinessKeyCheck=?,"
						+ "defaultCheck=?, recordAnomalyCheck=?,dataDriftCheck=?,updateFrequency=?,frequencyDays=?,"
						+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,timeSeries=?,keyGroupRecordCountAnomaly=?,"
						+ "outOfNormCheck=?,applyRules=?, applyDerivedColumns=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
						+ "buildHistoricFingerPrint=?,historicStartDate=?,historicEndDate=?,historicDateFormat=?,patternCheck=?,"
						+ "dateRuleCheck=?,badData=?,lengthCheck=?, maxLengthCheck=?,dGroupNullCheck=?, dGroupDateRuleCheck=?, validityThreshold=?, dGroupDataDriftCheck=?, "
						+ "thresholdsApplyOption=?,data_domain_id=?, defaultPatternCheck= ? ,reprofiling=? where idApp="
						+ listApplications.getIdApp();
			return jdbcTemplate.update(sql,
					new Object[] { listApplications.getColOrderValidation(), listApplications.getFileNameValidation(),
							listApplications.getEntityColumn(), listApplications.getNonNullCheck(),
							listApplications.getNumericalStatCheck(), listApplications.getStringStatCheck(),
							listApplications.getTimelinessKeyChk(), listApplications.getDefaultCheck(),
							listApplications.getRecordAnomalyCheck(), listApplications.getDataDriftCheck(),
							listApplications.getUpdateFrequency(), listApplications.getFrequencyDays(),
							listApplications.getRecordCountAnomaly(), listApplications.getRecordCountAnomalyThreshold(),
							listApplications.getTimeSeries(), listApplications.getkeyBasedRecordCountAnomaly(), "N",
							listApplications.getApplyRules(), listApplications.getApplyDerivedColumns(),
							listApplications.getCsvDir(), listApplications.getGroupEquality(),
							listApplications.getGroupEqualityThreshold(), listApplications.getIncrementalMatching(),
							listApplications.getBuildHistoricFingerPrint(), listApplications.getHistoricStartDate(),
							listApplications.getHistoricEndDate(), listApplications.getHistoricDateFormat(),
							listApplications.getPatternCheck(), listApplications.getDateRuleChk(),
							listApplications.getBadData(), listApplications.getlengthCheck(),
							listApplications.getMaxLengthCheck(), listApplications.getdGroupNullCheck(),
							listApplications.getdGroupDateRuleCheck(), listApplications.getValidityThreshold(),
							listApplications.getdGroupDataDriftCheck(), listApplications.getThresholdsApplyOption(),
							listApplications.getData_domain(), listApplications.getDefaultPatternCheck(),
							listApplications.getReprofiling() });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistapplication(String outofNorm, String columnOrderVal, String fileNameVal, Double rows,
			String nameofEntityColumn, Long idApp, String nullCount, String recordAnomaly, String numericalStats,
			String stringStat, String dataDriftCheck, String updateFrequency, Double frequencyDaysnull,
			String recordCount, Double dFSetComparisonnull, String timeSeries, String recordCountAnomalyType,
			String applyRules, String applyDerivedColumns, String csvDirectory, String groupEquality,
			Double groupEqualityText, String dGroupNullCheck, String dGroupDateRuleCheck,
			ListApplications listApplication) {
		try {
			if (groupEquality == null) {
				groupEquality = "N";
			}
			String keyGroupRecordCountAnomaly = "N";
			if (columnOrderVal == null)
				columnOrderVal = "";
			if (fileNameVal == null)
				fileNameVal = "";
			if (nullCount == null)
				nullCount = "N";
			if (numericalStats == null)
				numericalStats = "N";
			if (stringStat == null)
				stringStat = "N";
			if (recordAnomaly == null)
				recordAnomaly = "N";
			if (nameofEntityColumn == null)
				nameofEntityColumn = "";
			if (dataDriftCheck == null)
				dataDriftCheck = "N";
			if (updateFrequency == null)
				updateFrequency = "Never";
			if (recordCount == null)
				recordCount = "N";
			if (timeSeries.equals(""))
				timeSeries = "None";
			if (applyRules == null)
				applyRules = "N";
			if (applyDerivedColumns == null)
				applyDerivedColumns = "N";
			if (recordCountAnomalyType.equalsIgnoreCase("RecordCountAnomaly")) {
				recordCount = "Y";
			} else if (recordCountAnomalyType.equalsIgnoreCase("KeyBasedRecordCountAnomaly")) {
				keyGroupRecordCountAnomaly = "Y";
			}
			if (dGroupNullCheck == null) {
				dGroupNullCheck = "N";
			}
			if (dGroupDateRuleCheck == null) {
				dGroupDateRuleCheck = "N";
			}
			if (dataDriftCheck != null) {
				if (dataDriftCheck.equalsIgnoreCase("Y"))
					stringStat = "Y";
			}
			if (outofNorm != null) {
				if (outofNorm.equalsIgnoreCase("Y")) {
					numericalStats = "Y";
				}
			}
			if (listApplication.getHistoricStartDate() != "") {
				// listApplication.setHistoricStartDate(listApplication.getHistoricStartDate()+"
				// "+"00:00:00");
			} else {
				listApplication.setHistoricStartDate("0001-01-01 00:00:00");
			}
			if (listApplication.getHistoricEndDate() != "") {
				// listApplication.setHistoricEndDate(listApplication.getHistoricEndDate()+"
				// "+"00:00:00");
			} else {
				listApplication.setHistoricEndDate("0001-01-01 00:00:00");
			}
			LOG.debug("listApplication.getHistoricStartDate()=" + listApplication.getHistoricStartDate());
			if (listApplication.getIncrementalMatching() == null)
				listApplication.setIncrementalMatching("N");
			LOG.debug("groupEqualityText=" + groupEqualityText);
			LOG.debug("numericalStats=" + numericalStats);
			String sql = "update listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,"
					+ "recordAnomalyCheck=?,dataDriftCheck=?,updateFrequency=?,frequencyDays=?,"
					+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,timeSeries=?,keyGroupRecordCountAnomaly=?,"
					+ "outOfNormCheck=?,applyRules=?, applyDerivedColumns=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
					+ "buildHistoricFingerPrint=?,historicStartDate=?,historicEndDate=?,historicDateFormat=?,dGroupNullCheck=?,dGroupDateRuleCheck=? where idApp="
					+ idApp;
			return jdbcTemplate.update(sql,
					new Object[] { columnOrderVal, fileNameVal, nameofEntityColumn, nullCount, numericalStats,
							stringStat, recordAnomaly, dataDriftCheck, updateFrequency, frequencyDaysnull, recordCount,
							dFSetComparisonnull, timeSeries, keyGroupRecordCountAnomaly, outofNorm, applyRules,
							applyDerivedColumns, csvDirectory, groupEquality, groupEqualityText,
							listApplication.getIncrementalMatching(), listApplication.getBuildHistoricFingerPrint(),
							listApplication.getHistoricStartDate(), listApplication.getHistoricEndDate(),
							listApplication.getHistoricDateFormat(), dGroupNullCheck, dGroupDateRuleCheck });
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistdatadefinitions(Long idData, String recordCount, Double nullCountTextNull,
			String incrementalCheck, String numericalStats, Double numericalStatsTextNull, String stringStat,
			Double stringStatTextNull, String recordAnomaly, Double recordAnomalyThresholdnull,
			Double dataDriftCheckTextnull, Double outofNormThresholdnull) {
		try {
			if (recordCount == null)
				recordCount = "N";
			if (incrementalCheck == null)
				incrementalCheck = "N";
			if (numericalStats == null)
				numericalStats = "N";
			if (stringStat == null)
				stringStat = "N";
			if (recordAnomalyThresholdnull != null && recordAnomalyThresholdnull == 0l)
				recordAnomalyThresholdnull = 3.0;
			if (recordAnomaly == null) {
				recordAnomaly = "N";
			}
			/*
			 * LOG.debug("recordAnomalyThresholdnull=" + recordAnomalyThresholdnull); String
			 * sql =
			 * "update listDataDefinition set recordAnomalyThreshold=?,nullCountThreshold=?,"
			 * +
			 * "numericalThreshold=?,stringStatThreshold=?,dataDriftThreshold=?,outOfNormStatThreshold=? where idData="
			 * + idData; return jdbcTemplate.update(sql, new Object[] { 1.5,
			 * nullCountTextNull, numericalStatsTextNull, stringStatTextNull,
			 * dataDriftCheckTextnull, outofNormThresholdnull });
			 */
			LOG.debug("recordAnomalyThresholdnull=" + recordAnomalyThresholdnull);

			// TODO: This functionality will have an enhancement - don't enable this
			/*
			 * String sql = "update listDataDefinition set recordAnomalyThreshold=? ," +
			 * "numericalThreshold=?,stringStatThreshold=?,dataDriftThreshold=?,outOfNormStatThreshold=? where idData="
			 * + idData; return jdbcTemplate.update(sql, new Object[] {
			 * recordAnomalyThresholdnull, numericalStatsTextNull, stringStatTextNull,
			 * dataDriftCheckTextnull, outofNormThresholdnull });
			 */

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistdatadefinitionsAdCheckTab(Long idData, String numericalStats,
			Double numericalStatsTextNull, String recordAnomaly, Double recordAnomalyThresholdnull) {
		try {

			if (numericalStats == null)
				numericalStats = "N";
			if (recordAnomalyThresholdnull != null && recordAnomalyThresholdnull == 0l)
				recordAnomalyThresholdnull = 3.0;
			if (recordAnomaly == null) {
				recordAnomaly = "N";
			}
			LOG.debug("recordAnomalyThresholdnull at Advanced tab=" + recordAnomalyThresholdnull);
			String sql = "update listDataDefinition set recordAnomalyThreshold=? ,"
					+ "numericalThreshold=? where idData=" + idData;
			return jdbcTemplate.update(sql, new Object[] { recordAnomalyThresholdnull, numericalStatsTextNull });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int deletefromlistapplications(Long idApp, String mode) {
		try {
			// if (mode.equalsIgnoreCase("updateActive")) {
			String sql = "update listApplications set active='no' where idApp=" + idApp;
			return jdbcTemplate.update(sql);
			/*
			 * } else if (mode.equalsIgnoreCase("delete")) { String sql =
			 * "DELETE FROM  listApplications  where idApp=" + idApp; return
			 * jdbcTemplate.update(sql); }
			 */
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public List<ListDataSource> getdatafromlistdatasource(Long idData) {
		try {
			String sql = "SELECT idData,name, description, dataLocation, dataSource,createdAt,createdBy,domain_id,idDataSchema,createdByUser from listDataSources where idData="
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
					alistdatasource.setCreatedByUser(rs.getString("createdByUser"));
					alistdatasource.setDomain(rs.getInt("domain_id"));
					alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
					return alistdatasource;
				}
			});

			return listdatasource;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public long insertIntoListSchedule(long idApp, String time) {
		try {
			String sql = "insert into listSchedule(time,name,description,frequency,scheduleDay,exceptionMatching) "
					+ " VALUES (?,?,?,?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idschedule"
					: "idSchedule";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setString(1, time);
					pst.setString(2, "data_matching_" + idApp);
					pst.setString(3, "data_matching_" + idApp);
					pst.setString(4, "weekly");
					pst.setString(5, "all");
					pst.setString(6, "Y");
					return pst;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public int insertintoscheduledtasks(Long idApp, Long idScheduler) {
		String sql = "insert into scheduledTasks(idApp,idSchedule,status) " + " VALUES (?,?,?)";
		return jdbcTemplate.update(sql, idApp, idScheduler, "Scheduled");
	}

	public int insertFMConnectionDetails(long idApp, long idDataSchema) {
		int count = 0;
		try {
			String sql = "insert into fm_connection_details(idApp,idDataSchema)" + " VALUES (?,?)";
			count = jdbcTemplate.update(sql, idApp, idDataSchema);
			LOG.debug("insertDataIntofm_connection_details" + count);
			return count;
		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}

	}

	public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
			Double matchingThreshold, String incrementalMatching, String dateFormat, String leftSliceEnd,
			String fileMonitoringType, String continuousFileMonitoring, Long project_id, String createdByUser,
			Integer domainId) {

		// LOG.debug("idDataSchema:"+idDataSchema);
		try {
			String sql = "INSERT INTO listApplications "
					+ "(name,description,apptype,idData,createdBy,createdAt,updatedAt,updatedBy,fileNameValidation,entityColumn,colOrderValidation,"
					+ "matchingThreshold,nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,incrementalMatching,historicDateFormat,fileMonitoringType,continuousFileMonitoring, project_id,"
					+ "createdByUser,validityThreshold,domain_id) VALUES (?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idapp"
					: "idApp";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setString(1, name);
					pst.setString(2, description);
					pst.setString(3, apptype);
					pst.setLong(4, idData);
					pst.setLong(5, idUser);
					pst.setLong(6, idUser);
					pst.setString(7, " ");
					pst.setString(8, " ");
					pst.setString(9, " ");
					pst.setDouble(10, matchingThreshold);
					pst.setString(11, "N");
					pst.setString(12, "N");
					pst.setString(13, "N");
					pst.setString(14, "N");
					pst.setString(15, incrementalMatching);
					pst.setString(16, dateFormat);
					pst.setString(17, fileMonitoringType);
					pst.setString(18, continuousFileMonitoring);
					pst.setLong(19, project_id);
					pst.setString(20, createdByUser);
					pst.setDouble(21, 1.0);
					pst.setInt(22, domainId);
					return pst;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	// created overloaded method for Adding Data Domain.
	public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
			Double matchingThreshold, String incrementalMatching, String dateFormat, String leftsliceend,
			String fileMonitoringType, String continuousFileMonitoring, Long project_id, String createdByUser,
			Integer domainId, Integer data_Domain_id, String stringStatCheck,String validationJobSize) {

		// LOG.debug("idDataSchema:"+idDataSchema);
		try {
			String sql = "INSERT INTO listApplications "
					+ "(name,description,apptype,idData,createdBy,createdAt,updatedAt,updatedBy,fileNameValidation,entityColumn,colOrderValidation,"
					+ "matchingThreshold,nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,incrementalMatching,historicDateFormat,fileMonitoringType,continuousFileMonitoring, project_id,"
					+ "createdByUser,validityThreshold,domain_id,data_domain_id,validation_job_size) VALUES (?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idapp"
					: "idApp";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
					pst.setString(1, name);
					pst.setString(2, description);
					pst.setString(3, apptype);
					pst.setLong(4, idData);
					pst.setLong(5, idUser);
					pst.setLong(6, idUser);
					pst.setString(7, " ");
					pst.setString(8, " ");
					pst.setString(9, " ");
					pst.setDouble(10, matchingThreshold);
					pst.setString(11, "N");
					pst.setString(12, "N");
					pst.setString(13, stringStatCheck);
					pst.setString(14, "N");
					pst.setString(15, incrementalMatching);
					pst.setString(16, dateFormat);
					pst.setString(17, fileMonitoringType);
					pst.setString(18, continuousFileMonitoring);
					pst.setLong(19, project_id);
					pst.setString(20, createdByUser);
					pst.setDouble(21, 1.0);

					pst.setInt(22, domainId);
					pst.setInt(23, data_Domain_id);
					pst.setString(24, validationJobSize);
					return pst;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0l;
	}

	public int updateApplicationNameWithIdApp(String name, long idApp) {
		String newAppName = idApp + "_" + name;
		String query = "update listApplications set name=? where idApp=?";
		return jdbcTemplate.update(query, newAppName, idApp);
	}

	/* Avishkar[25-Aug-2021] JsonData method for validation view page */
	public JSONArray getPaginatedValidationsJsonData(HashMap<String, String> oPaginationParms) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		
		
		
		String menuFilter = "";
		if (oPaginationParms.get("menuFilter") != null) {
			List<String> strings=Arrays.asList(oPaginationParms.get("menuFilter").split(","));
			
			menuFilter = "  and t.appType in ("+(strings.isEmpty() ? "" : "\"" + String.join("\", \"", strings) + "\"" )+")" ;
		}
		String sDataSql, sDataViewList, sOption1Sql, sOption2Sql;
		String[] aColumnSpec = new String[] { "ValidationId", "ValidationCheckName", "DataTemplateName",
				"DataTemplateId", "AppType", "AppMode", "CreatedOn", "ProjectName", "CreatedBy", "ApprovalStatus",
				"StagingStatus", "ApprovedOn", "ApprovedBy", "Status", "ProfilingEnabled", "AdvancedRulesEnabled",
				"RightTemplateId", "IncrementalMatching", "DataDomainId", "MatchingThreshold", "ValidityThreshold","JobSize" };

		ObjectMapper oMapper = new ObjectMapper();
		JSONArray aRetValue = new JSONArray();

		String domainIdCondition = "";
		if (oPaginationParms.containsKey("domainId")) {
			domainIdCondition = " and t.domain_id in (" + oPaginationParms.get("domainId") + ")";
		}

		DateUtility.DebugLog("getPaginatedValidationsJsonData 01",
				String.format("oPaginationParms = %1$s", oPaginationParms));

		try {
			sOption1Sql = String.format("and (t.CreatedOn between '%1$s' and '%2$s') \n",
					oPaginationParms.get("FromDate"), oPaginationParms.get("ToDate"));

			sOption2Sql = "and 1 = case when t.name like 'LIKE-TEXT' then 1 when ls.name like 'LIKE-TEXT' then 1 else 0 end \n"
					.replaceAll("LIKE-TEXT", "%" + oPaginationParms.get("SearchText") + "%");
			sOption2Sql = (oPaginationParms.get("SearchText").isEmpty()) ? "" : sOption2Sql;

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = "" + "select "
						+ "		t.idApp as ValidationId, t.name as ValidationCheckName, t.appType as AppType, t.idRightData as RightTemplateId,"
						+ " t.incrementalMatching as IncrementalMatching , t.data_domain_id as DataDomainId, t.matchingThreshold as MatchingThreshold,"
						+ "t.validityThreshold as ValidityThreshold,"
						+ " case when(t.incrementalMatching  = 'Y') then (case when (t.buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode, "
						+ " t.createdByUser as CreatedBy, t.CreatedOn, ls.name as  DataTemplateName, ls.idData as DataTemplateId,ls.advancedRulesEnabled as AdvancedRulesEnabled, ls.profilingEnabled as ProfilingEnabled, t.approve_status_name as ApprovalStatus,t.approve_date as ApprovedOn, t.approver_name as ApprovedBy,t.active as Status, case when ap.element_text is null  then 'Not Started' else ap.element_text end as StagingStatus, t.data_domain_id as DataDomainId,t.project_id as ProjectId, p.projectName as ProjectName,t.description as Description,t.validation_job_size As JobSize  "
						+ "from ( "
						+ "		select listApplications.*, to_date(listApplications.createdAt::text, 'YYYY-MM-DD') as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join listDataSources ls on ls.idData = t.idData join project p on t.project_id = p.idProject "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ domainIdCondition+ menuFilter;
			} else {
				sDataSql = "" + "select "
						+ "		t.idApp as ValidationId, t.name as ValidationCheckName, t.appType as AppType, t.idRightData as RightTemplateId,"
						+ " t.incrementalMatching as IncrementalMatching , t.data_domain_id as DataDomainId, t.matchingThreshold as MatchingThreshold,"
						+ "t.validityThreshold as ValidityThreshold, "
						+ " case when(t.incrementalMatching  = 'Y') then (case when (t.buildHistoricFingerPrint = 'Y') then 'Historic' else 'Incremental' end) else 'Bulk Load' end AS AppMode, "
						+ " t.createdByUser as CreatedBy, t.CreatedOn, ls.name as  DataTemplateName, ls.idData as DataTemplateId,ls.advancedRulesEnabled as AdvancedRulesEnabled, ls.profilingEnabled as ProfilingEnabled, t.approve_status_name as ApprovalStatus,t.approve_date as ApprovedOn, t.approver_name as ApprovedBy,t.active as Status, case when ap.element_text is null  then 'Not Started' else ap.element_text end as StagingStatus, t.data_domain_id as DataDomainId,t.project_id as ProjectId, p.projectName as ProjectName,t.description as Description,t.validation_job_size As JobSize "
						+ "from ( "
						+ "		select listApplications.*, cast(date_format(listApplications.createdAt, '%y-%m-%d') as date) as CreatedOn, "
						+ "		case when  app_option_list_elements.element_text is null  then 'Not Started' else app_option_list_elements.element_text end as approve_status_name "
						+ "		from listApplications "
						+ "		left join app_option_list_elements on listApplications.approve_status = app_option_list_elements.row_id "
						+ "		) t "
						+ "join listDataSources ls on ls.idData = t.idData join project p on t.project_id = p.idProject "
						+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
						+ String.format("where t.project_id in ( %1$s ) ", oPaginationParms.get("ProjectIds"))
						+ domainIdCondition+menuFilter;
			}

			if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("1")) {
				sDataSql = sDataSql + sOption1Sql + " order by t.idApp desc limit 1000;";
			} else if (oPaginationParms.get("SearchByOption").equalsIgnoreCase("2")) {
				sDataSql = sDataSql + sOption2Sql + " order by t.idApp desc limit 1000;";
			} else {
				sDataSql = sDataSql + sOption1Sql + sOption2Sql + " order by t.idApp desc limit 1000;";
			}

			DateUtility.DebugLog("getPaginatedValidationsJsonData 02", String
					.format("Search option and SQL '%1$s' / '%2$s'", oPaginationParms.get("SearchByOption"), sDataSql));

			LOG.debug("sDataSql" + sDataSql);

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);
			aRetValue = new JSONArray(sDataViewList);

			DateUtility.DebugLog("getPaginatedValidationsJsonData 03",
					String.format("No of records sending to clinet '%1$s'", aDataViewList.size()));

		} catch (Exception oException) {
			oException.printStackTrace();
			throw oException;
		}
		return aRetValue;
	}

	public List<ListApplicationsandListDataSources> getdatafromlistappsandlistdatasources(Long projId,
			List<Project> projlst, String toDate, String fromDate) {

		try {

			String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);

			String sql = "select t.idApp,t.name,t.appType,t.createdByUser,t.createdAt,ls.name as templateName, ls.idData,ls.advancedRulesEnabled ,ls.profilingEnabled,"
					+ "t.approve_status_name,t.approve_date, t.approver_name,t.active,case when ap.element_text is null  then 'Not Started' else ap.element_text end as staging_approve_status, "
					+ "t.data_domain_id,t.project_id as projectId, p.projectName from "
					+ "(select la.*, case when  el.element_text is null  then 'Not Started' else el.element_text end as approve_status_name "
					+ "from  listApplications la left join app_option_list_elements el  on la.approve_status = el.row_id) t "
					+ "join listDataSources ls on ls.idData = t.idData "
					+ "join project p on t.project_id = p.idProject "
					+ "left join app_option_list_elements ap  on t.staging_approve_status = ap.row_id "
					+ "where t.project_id in ( " + projId + " ) and " + "t.createdAt >= '" + toDate + "' ";
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = sql + "and t.createdAt <= ('" + fromDate + "'::DATE + '1 day'::INTERVAL) ";
			} else {
				sql = sql + "and t.createdAt <= DATE_ADD('" + fromDate + "', INTERVAL 1 DAY) ";
			}

			LOG.debug("sql -> " + sql);

			List<ListApplicationsandListDataSources> lsAndLds = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {
						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();
							listappslistds.setLaName(rs.getString("name"));

							if (rs.getString("appType").equals("Schema Matching"))
								listappslistds.setLsName("");
							else
								listappslistds.setLsName(rs.getString("templateName"));
							listappslistds.setCreatedAt(rs.getDate("createdAt"));
							listappslistds.setIdData(rs.getLong("idData"));
							listappslistds.setIdApp(rs.getLong("idApp"));
							listappslistds.setAppType(rs.getString("appType"));
							listappslistds.setApprovalStatus(rs.getString("t.approve_status_name"));
							listappslistds.setStagingApprovalStatus(rs.getString("staging_approve_status"));
							listappslistds.setCreatedByUser(rs.getString("createdByUser"));
							listappslistds.setActive(rs.getString("active"));
							listappslistds.setProjectId(rs.getLong("projectId"));
							listappslistds.setProjectName(rs.getString("projectName"));
							listappslistds.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
							listappslistds.setProfilingEnabled(rs.getString("profilingEnabled"));
							listappslistds.setApproverName(rs.getString("approver_name"));
							String approvedDateStr = "";
							Date approvedDate = rs.getTimestamp("approve_date");
							if (approvedDate != null) {
								approvedDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(approvedDate);
							}
							listappslistds.setApprovalDate(approvedDateStr);
							listappslistds.setData_domain(rs.getInt("data_domain_id"));
							return listappslistds;
						}
					});
			return lsAndLds;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List getdatafromlistappsandlistdatasourcesId(long idApp) {
		try {
			String sql = "select la.name, la.createdAt ,ls.name,ls.idData,la.idApp,la.appType from listApplications la,listDataSources ls where ls.idData = la.idData and la.active='yes' and la.idApp="
					+ idApp;
			List<ListApplicationsandListDataSources> lsAndLds = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {
						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();
							listappslistds.setLaName(rs.getString("name"));
							listappslistds.setLsName(rs.getString(3));
							listappslistds.setCreatedAt(rs.getDate("createdAt"));
							listappslistds.setIdData(rs.getLong("idData"));
							listappslistds.setIdApp(rs.getLong("idApp"));
							listappslistds.setAppType(rs.getString("appType"));
							return listappslistds;
						}
					});

			/*
			 * RowMapper<ListApplicationsandListDataSources> rm = (rs, i) -> {
			 * ListApplicationsandListDataSources listappslistds = new
			 * ListApplicationsandListDataSources();
			 * listappslistds.setLaName(rs.getString("name"));
			 * listappslistds.setLsName(rs.getString(3));
			 * listappslistds.setCreatedAt(rs.getDate("createdAt"));
			 * listappslistds.setIdData(rs.getLong("idDataSchema"));
			 * listappslistds.setIdApp(rs.getLong("idApp"));
			 * listappslistds.setAppType(rs.getString("appType")); return listappslistds; };
			 *
			 * String sql1 =
			 * "select la.name, la.createdAt ,lds.schemaName , lds.idDataSchema,la.idApp,la.appType from listApplications la,listDataSchema lds where la.idData = lds.idDataSchema and la.active='yes'"
			 * ; List<ListApplicationsandListDataSources> laAndLds =
			 * jdbcTemplate.query(sql1, rm); for (ListApplicationsandListDataSources lalds :
			 * laAndLds) { lsAndLds.add(lalds); }
			 */
			return lsAndLds;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String getAppTypeFromListApplication(Long idApp) {
		try {
			return jdbcTemplate.queryForObject("select appType from listApplications where idApp=?", String.class,
					idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String getDataLocationInListDataSources(Long idData) {
		try {
			return jdbcTemplate.queryForObject("select datalocation from listDataSources where idData=?", String.class,
					idData);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	//// String duplicateCount, String duplicateCountAll
	public List<String> validationCheckprerequisite(Long idApp, Long idData, String duplicateCountAll, String nullCount,
			String duplicateCount, String recordAnomaly, String stringStat, String numericalStats) {

		List<String> warningMsgs = new ArrayList<String>();

		// dupkey prerequisite check
		if ((duplicateCountAll != null) && (duplicateCountAll.equalsIgnoreCase("Y"))) {
			String duplicateCountAllstatus = jdbcTemplate.queryForObject(
					"select dupRow from listDFTranRule where idApp=? and  type='All'", String.class, idApp);
			if (duplicateCountAllstatus.equalsIgnoreCase("N")) {
				warningMsgs.add("select dupRow check box in validation check page");
			}
		}

		// non null check prerequisite check
		if ((nullCount != null) && (nullCount.equalsIgnoreCase("Y"))) {
			SqlRowSet nonNullRowSet = jdbcTemplate
					.queryForRowSet("select nonNull from listDataDefinition where idData = ?", idData);
			boolean nonNullStatus = false;
			while (nonNullRowSet.next()) {
				if (nonNullRowSet.getString("nonNull").equalsIgnoreCase("Y")) {
					nonNullStatus = true;
				}
			}
			if (!nonNullStatus) {
				warningMsgs.add("set atleast one nonNull check box");
			}
		}

		// primary key prerequisite check
		if ((duplicateCount != null) && (duplicateCount.equalsIgnoreCase("Y"))) {
			String duplicateCountStatus = jdbcTemplate.queryForObject(
					"select dupRow from listDFTranRule where idApp=? and  type='Identity'", String.class, idApp);
			if (duplicateCountStatus.equalsIgnoreCase("N")) {
				warningMsgs.add("set atleast one primaryKey  check box");
			}
		}

		// record anomoly prerequisite check
		if ((recordAnomaly != null) && (recordAnomaly.equalsIgnoreCase("Y"))) {
			SqlRowSet recordAnomalyRowSet = jdbcTemplate
					.queryForRowSet("select recordAnomaly from listDataDefinition where idData = ? ", idData);
			boolean recordAnomalyKeyStatus = false;
			while (recordAnomalyRowSet.next()) {
				if (recordAnomalyRowSet.getString("recordAnomaly").equalsIgnoreCase("Y")) {
					recordAnomalyKeyStatus = true;
				}
			}
			if (!recordAnomalyKeyStatus) {
				warningMsgs.add("set atleast one record anomoly  check box");
			}
		}

		// //sumeet
		// //badData prerequisite check
		// if ((badData != null) && (badData.equalsIgnoreCase("Y"))) {
		// SqlRowSet badDataRowSet = jdbcTemplate
		// .queryForRowSet("select badData from listDataDefinition where idData = ? ",
		// idData);
		// boolean badDataStatus = false;
		// while (badDataRowSet.next()) {
		// if (badDataRowSet.getString("badData").equalsIgnoreCase("Y")) {
		// badDataStatus = true;
		// }
		// }
		// if (!badDataStatus) {
		// warningMsgs.add("set atleast one badData check box");
		// }
		// }

		// string stat prerequisite check

		if ((stringStat != null) && (stringStat.equalsIgnoreCase("Y"))) {
			SqlRowSet stringStatRowSet = jdbcTemplate
					.queryForRowSet("select stringStat from listDataDefinition where idData = ? ", idData);
			boolean stringStatstatus = false;
			while (stringStatRowSet.next()) {
				if (stringStatRowSet.getString("stringStat").equalsIgnoreCase("Y")) {
					stringStatstatus = false;
				}
			}
			if (!stringStatstatus) {
				warningMsgs.add("set atleast one string stat check box");
			}
		}

		// num stat prerequisite check

		if ((numericalStats != null) && (numericalStats.equalsIgnoreCase("Y"))) {
			SqlRowSet numericalStatsRowSet = jdbcTemplate
					.queryForRowSet("select numericalStat from listDataDefinition where idData = ? ", idData);
			boolean numericalStatsStatus = false;
			while (numericalStatsRowSet.next()) {
				if (numericalStatsRowSet.getString("numericalStat").equalsIgnoreCase("Y")) {
					numericalStatsStatus = true;
				}
			}
			if (!numericalStatsStatus) {
				warningMsgs.add("set atleast one numerical stat check box");
			}
		}

		return warningMsgs;
	}

	public String duplicateValidationCheckName(String validationCheckName) {
		String Name = null;		
		String q = "SELECT name FROM listApplications WHERE name = ?";
		Object[] inputs = new Object[] { validationCheckName };
		try {
			Name = jdbcTemplate.queryForObject(q, inputs, String.class);
			LOG.debug("Name=" + Name);
			return Name;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return Name;
		}
	}

	public String duplicateValidationCheckNameNew(String validationCheckName) {

		String Name = null;
		String queryclause = DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES) ? "~" : "REGEXP";
		String sql = "SELECT name FROM listApplications WHERE name "+queryclause+" ?";
		Object[] inputs = new Object[] { "^[0-9]{1,99}_" + validationCheckName + "$" };
		try {
			SqlRowSet oSqlResults = jdbcTemplate.queryForRowSet(sql, inputs);
			if (oSqlResults.next()) {
				 Name = validationCheckName;
				 return Name;
			} else {
				return Name;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return Name;
		}
	}

	public String getDataLocationFromListDataSources(Long idData) {
		String q = "SELECT dataLocation FROM listDataSources WHERE idData=" + idData;
		String dataLocation = jdbcTemplate.queryForObject(q, String.class);
		return dataLocation;

	}

	public int insertintolistModelGovernance(Long idApp, String modelGovernanceType, String modelIdCol,
			String decileCol, double expectedPercentage, double thresholdPercentage) {
		String sql = "insert into listModelGovernance(idApp,modelGovernanceType,modelIdCol,decileCol,expectedPercentage,thresholdPercentage) "
				+ " VALUES (?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, idApp, modelGovernanceType, modelIdCol, decileCol, expectedPercentage,
				thresholdPercentage);
	}

	public int updateintolistModelGovernance(Long idApp, String modelGovernanceType, String modelIdCol,
			String decileCol, double expectedPercentage, double thresholdPercentage) {
		String sql = "update listModelGovernance set modelGovernanceType=?,modelIdCol=?,"
				+ "decileCol=?,expectedPercentage=?,thresholdPercentage=? where idApp=?";
		int update = jdbcTemplate.update(sql, modelGovernanceType, modelIdCol, decileCol, expectedPercentage,
				thresholdPercentage, idApp);
		return update;
	}

	public listModelGovernance getDataFromListModelGovernance(Long idApp) {
		String sql = "select * from listModelGovernance where idApp= " + idApp;
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		listModelGovernance lmg = new listModelGovernance();
		if (queryForRowSet.next()) {

			lmg.setDecileCol(queryForRowSet.getString("decileCol"));
			lmg.setExpectedPercentage(queryForRowSet.getDouble("expectedPercentage"));
			lmg.setIdModel(queryForRowSet.getLong("idModel"));
			lmg.setModelGovernanceType(queryForRowSet.getString("modelGovernanceType"));
			lmg.setModelIdCol(queryForRowSet.getString("modelIdCol"));
			lmg.setThresholdPercentage(queryForRowSet.getDouble("thresholdPercentage"));
			lmg.setLeftSourceSliceStart(queryForRowSet.getString("leftSourceSliceStart"));
			lmg.setLeftSourceSliceEnd(queryForRowSet.getString("leftSourceSliceEnd"));
			lmg.setRightSourceSliceStart(queryForRowSet.getString("rightSourceSliceStart"));
			lmg.setRightSourceSliceEnd(queryForRowSet.getString("rightSourceSliceEnd"));
			lmg.setMeasurementExpression(queryForRowSet.getString("measurementExpression"));
			lmg.setMatchingExpression(queryForRowSet.getString("matchingExpression"));
		}
		return lmg;
	}

	public void updateIntoListApplicationForModelGovernance(ListApplications la) {
		String sql = "update listApplications set buildHistoricFingerPrint=?,historicStartDate=?,"
				+ "historicEndDate=?,historicDateFormat=?,timeSeries=?,updateFrequency=?,frequencyDays=?,incrementalMatching=? where idApp="
				+ la.getIdApp();
		jdbcTemplate.update(sql,
				new Object[] { la.getBuildHistoricFingerPrint(), la.getHistoricStartDate(), la.getHistoricEndDate(),
						la.getHistoricDateFormat(), la.getTimeSeries(), la.getUpdateFrequency(), la.getFrequencyDays(),
						la.getIncrementalMatching() });
	}

	public int insertintolistModelGovernanceForScoreConsistency(Long idApp, String modelGovernanceType,
			String leftSourceSliceStart, String leftSourceSliceEnd, String rightSourceSliceStart,
			String rightSourceSliceEnd, String matchingExpression, String measurementExpression, double scThreshold,
			String sourceDateFormat, String modelIdCol, String decileCol) {
		String la = "update listApplications set historicDateFormat=? where idApp=?";
		jdbcTemplate.update(la, sourceDateFormat, idApp);
		String sql = "insert into listModelGovernance(idApp,modelGovernanceType,leftSourceSliceStart,leftSourceSliceEnd,rightSourceSliceStart,rightSourceSliceEnd,matchingExpression,"
				+ "measurementExpression,modelIdCol,decileCol,thresholdPercentage" + ") "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, idApp, modelGovernanceType, leftSourceSliceStart, leftSourceSliceEnd,
				rightSourceSliceStart, rightSourceSliceEnd, matchingExpression, measurementExpression, modelIdCol,
				decileCol, scThreshold);
	}

	public int updateintolistModelGovernanceForScoreConsistency(Long idApp, String modelGovernanceType,
			String leftSourceSliceStart, String leftSourceSliceEnd, String rightSourceSliceStart,
			String rightSourceSliceEnd, String matchingExpression, String measurementExpression, double scThreshold,
			String sourceDateFormat, String modelIdCol, String decileCol) {
		String la = "update listApplications set historicDateFormat=? where idApp=?";
		jdbcTemplate.update(la, sourceDateFormat, idApp);
		String sql = "update listModelGovernance set modelGovernanceType=?,leftSourceSliceStart=?,"
				+ "leftSourceSliceEnd=?,rightSourceSliceStart=?,rightSourceSliceEnd=?,matchingExpression=?,measurementExpression=?"
				+ ",modelIdCol=?,decileCol=?,thresholdPercentage=? where idApp=?";
		return jdbcTemplate.update(sql, modelGovernanceType, leftSourceSliceStart, leftSourceSliceEnd,
				rightSourceSliceStart, rightSourceSliceEnd, matchingExpression, measurementExpression, modelIdCol,
				decileCol, scThreshold, idApp);
	}

	public Map<Long, String> getModelGovernanceAppTypeFromListApplications(String appType) {
		Map<Long, String> map = new LinkedHashMap<Long, String>();
		try {
			String sql = "SELECT la.idApp,la.name FROM listApplications la,listModelGovernance lm WHERE lm.modelGovernanceType='"
					+ appType + "' and la.idApp=lm.idApp";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getLong("idApp"), queryForRowSet.getString("name"));
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	public int updateModelGovernanceDashboardIntoListApplications(String modelGovernanceDashboard, Long idApp) {
		String sql = "update listApplications set csvDir=? where idApp=?";
		return jdbcTemplate.update(sql, modelGovernanceDashboard, idApp);
	}

	public boolean checkTheConfigurationForBuildHistoric(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		// boolean flag=false;
		if (la.getIncrementalMatching().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getIncrementalCol().equalsIgnoreCase("Y")) {
					return true;
				}
			}

		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForPatternCheckTab(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		// boolean flag=false;
		if (la.getPatternCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getPatternCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}

		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForDefaultPatternCheckTab(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		// boolean flag=false;
		if (la.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}

		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForDupRowIdentity(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getDupRowIdentity().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getPrimaryKey().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForDupRowAll(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getDupRowAll().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDupkey().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForDataDrift(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getDataDriftCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDataDrift().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForNumField(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getNumericalStatCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getNumericalStat().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForstringField(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getStringStatCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getStringStat().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForRecordAnomaly(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getRecordAnomalyCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getRecordAnomaly().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForapplyRules(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {

		try {
			if (la.getApplyRules().equalsIgnoreCase("Y")) {
				/*
				 * String sql = "select ruleName from listColRules where idData=" +
				 * la.getIdData(); SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
				 * LOG.debug("Custom rule configured   " +queryForRowSet.get); String
				 * sql_global_rule=
				 * "select ruleName from rule_Template_Mapping where templateid=" +
				 * la.getIdData(); SqlRowSet queryForRowSet_global_rule =
				 * jdbcTemplate.queryForRowSet(sql_global_rule);
				 * LOG.debug("global rule configured "+queryForRowSet_global_rule); if
				 * (queryForRowSet.next() || queryForRowSet_global_rule.next() ) { return true;
				 * } } else { return true; }
				 */

				int custom_rule_configured_count;
				int global_rule_configured_count;
				String sql = "select count(1) from listColRules where activeFlag='Y' and idData=" + la.getIdData();

				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
				if (!queryForRowSet.next()) {
					LOG.info("no results");
					custom_rule_configured_count = -1;
				} else {

					custom_rule_configured_count = queryForRowSet.getInt(1);
				}
				LOG.debug("Custom rules configured ::" + custom_rule_configured_count);
				String sql_global_rule = "select count(1) from rule_Template_Mapping where templateid="
						+ la.getIdData();
				SqlRowSet queryForRowSet_global_rule = jdbcTemplate.queryForRowSet(sql_global_rule);
				if (!queryForRowSet_global_rule.next()) {
					LOG.info("no results");
					global_rule_configured_count = -1;
				} else {

					global_rule_configured_count = queryForRowSet_global_rule.getInt(1);
				}
				LOG.debug("global rule configured ::" + global_rule_configured_count);
				if (custom_rule_configured_count > 0 || global_rule_configured_count > 0) {
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkTheConfigurationForapplyDerivedColumns(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		try {
			if (la.getApplyDerivedColumns().equalsIgnoreCase("Y")) {
				String sql = "select name from listDataBlend where idData=" + la.getIdData();
				LOG.debug(sql);
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
				if (queryForRowSet.next()) {
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return false;
	}

	public boolean checkTheConfigurationFordGroup(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getkeyBasedRecordCountAnomaly().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDgroup().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForMicrosegment(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getRecordCountAnomaly().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDgroup().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForNonNullField(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getNonNullCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getNonNull().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForTimelinessKeyField(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		boolean flagStartDate = false;
		boolean flagEndDate = false;
		boolean flagTimelinessDate = false;
		if (la.getTimelinessKeyChk().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getStartDate().equalsIgnoreCase("Y")) {
					flagStartDate = true;
				}
				if (ldd.getEndDate().equalsIgnoreCase("Y")) {
					flagEndDate = true;
				}
				if (ldd.getTimelinessKey().equalsIgnoreCase("Y")) {
					flagTimelinessDate = true;
				}
				if (flagStartDate == true & flagEndDate == true & flagTimelinessDate == true) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationForDefaultCheckField(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getDefaultCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDefaultCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationDefaultCheck(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getDefaultCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDefaultCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationPatternCheck(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getPatternCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getPatternCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationDefaultPatternCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkTheConfigurationDateRuleCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getDateRuleChk().equalsIgnoreCase("Y")) {
			LOG.info("---------------validationCheckDAOImpl checkTheConfigurationDateRuleCheck =>");
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDateRule().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkConfigurationDgroupDateRuleCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getdGroupDateRuleCheck().equalsIgnoreCase("Y")) {
			boolean dateRulesPresent = false;
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDateRule().equalsIgnoreCase("Y")) {
					dateRulesPresent = true;
					break;
				}
			}
			if (dateRulesPresent) {
				return checkDataTemplateForDGroup(listdatadefinition);
			}

		} else {
			return true;
		}
		return false;
	}

	public boolean checkConfigurationForDGroupNullCheck(List<ListDataDefinition> listdatadefinition,
			ListApplications la) {
		if (la.getdGroupNullCheck().equalsIgnoreCase("Y")) {
			boolean colsSelected = false;
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getNonNull().equalsIgnoreCase("Y")) {
					colsSelected = true;
					break;
				}
			}
			if (colsSelected) {
				return checkDataTemplateForDGroup(listdatadefinition);
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkDataTemplateForDGroup(List<ListDataDefinition> listdatadefinition) {
		for (ListDataDefinition ldd : listdatadefinition) {
			if (ldd.getDgroup().equalsIgnoreCase("Y")) {
				return true;
			}
		}
		return false;
	}

	public boolean checkForIncrementalInListDataAccess(Long idData) {
		try {
			String sql = "select incrementalType from listDataAccess where idData=" + idData;
			String incrementalType = jdbcTemplate.queryForObject(sql, String.class);
			if (incrementalType.equalsIgnoreCase("Y")) {
				return false;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			// TODO: handle exception
		}
		return true;
	}

	// sumeet
	public boolean checkTheBadData(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		LOG.debug("ValidationCheckDAOImpl ->  checkForgetBadData ........" + la.getBadData());
		if (la.getBadData().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getBadData().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkForLengthCheck(List<ListDataDefinition> listdatadefinition, ListApplications la) {

		LOG.debug("ValidationCheckDAOImpl ->  checkForLengthCheck ........" + la.getlengthCheck());
		if (la.getlengthCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getLengthCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	// -----------------------------

	// Max Length Check
	public boolean checkForMaxLengthCheck(List<ListDataDefinition> listdatadefinition, ListApplications la) {

		LOG.debug("ValidationCheckDAOImpl ->  checkForMaxLengthCheck ........" + la.getMaxLengthCheck());
		if (la.getMaxLengthCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getMaxLengthCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	// End Of Max Length Check

	public boolean defaultCheckFlag(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		if (la.getDefaultCheck().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDefaultCheck().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean checkFordateRuleCheck(List<ListDataDefinition> listdatadefinition, ListApplications la) {
		LOG.debug("ValidationCheckDAOImpl ->  checkFordateRuleCheck ........" + la.getBadData());
		if (la.getDateRuleChk().equalsIgnoreCase("Y")) {
			for (ListDataDefinition ldd : listdatadefinition) {
				if (ldd.getDateRule().equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	// public boolean checkFordateRuleCheck(Long idData) {
	// try {
	// String sql = "select dateRuleCheck from listDataAccess where idData=" +
	// idData;
	//
	// LOG.debug("----------ValidationCheckController
	// checkFordateRuleCheck"+idData);
	//
	// String incrementalType = jdbcTemplate.queryForObject(sql, String.class);
	// if (incrementalType.equalsIgnoreCase("Y")) {
	// return false;
	// }
	// } catch (Exception e) { LOG.error(e.getMessage());
	// // TODO: handle exception
	// }
	// return true;
	// }

	public String getMatchingExpressionFromListDMRules(Long idApp) {
		try {

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String getSchemaTypeFromListDataSchema(String idDataSchema) {
		String sql = "SELECT  schemaType FROM listDataSchema WHERE idDataSchema=" + idDataSchema;
		return jdbcTemplate.queryForObject(sql, String.class);
	}

	public Integer getCountFromResult_masterById(Long idApp) {
		String sql = "SELECT COUNT(*) FROM result_master_table WHERE appID=?";
		return jdbcTemplate1.queryForObject(sql, Integer.class, idApp);
	}

	@Override
	public String getAutomaticDateFormat(ListApplications listApplications) {
		try {
			// Long idData = listApplications.getIdData();
			String sql = "select idDataSchema,folderName from listDataAccess where idData="
					+ listApplications.getIdData();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			Long idDataSchema = 0l;
			String tableName = "";
			while (queryForRowSet.next()) {
				idDataSchema = queryForRowSet.getLong(1);
				tableName = queryForRowSet.getString(2);
			}
			List<ListDataSchema> listDataSchema = DataTemplateAddNewDAO.getListDataSchema(idDataSchema);
			String hostURI = listDataSchema.get(0).getIpAddress();
			String database = listDataSchema.get(0).getDatabaseSchema();
			String userlogin = listDataSchema.get(0).getUsername();
			String password = listDataSchema.get(0).getPassword();
			String port = listDataSchema.get(0).getPort();
			String domain = listDataSchema.get(0).getDomain();
			String serviceName = listDataSchema.get(0).getKeytab();
			String schemaType = listDataSchema.get(0).getSchemaType();
			String sql1 = "select displayName from listDataDefinition where idData=" + listApplications.getIdData()
					+ " and incrementalCol='Y' limit 1";
			String dateColumnName = jdbcTemplate.queryForObject(sql1, String.class);
			if (schemaType.equalsIgnoreCase("Oracle")) {
				String sampleDate = oracleconnection.getOneDateRecordForDateFormat(hostURI, database, userlogin,
						password, tableName, port, dateColumnName);
				return oracleparse(sampleDate);
			}
			if (schemaType.equalsIgnoreCase("Oracle RAC")) {
				String sampleDate = OracleRACConnection.getOneDateRecordForDateFormat(hostURI, database, userlogin,
						password, tableName, port, dateColumnName, serviceName);
				return oracleparse(sampleDate);
			}
			if (schemaType.equalsIgnoreCase("MSSQL")) {
				String sampleDate = mSSQLConnection.getOneDateRecordForDateFormat(hostURI, database, userlogin,
						password, tableName, port, dateColumnName);
				return mssqlparse(sampleDate);
			}
			if (schemaType.equalsIgnoreCase("MSSQLActiveDirectory")) {
				String sampleDate = msSqlActiveDirectoryConnectionObject.getOneDateRecordForDateFormat(hostURI,
						database, userlogin, password, tableName, port, dateColumnName, domain);
				return mssqlparse(sampleDate);
			}
			if (schemaType.equalsIgnoreCase("Vertica")) {
				String sampleDate = verticaconnection.getOneDateRecordForDateFormat(hostURI, database, userlogin,
						password, tableName, port, dateColumnName, domain);
				return mssqlparse(sampleDate);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String oracleparse(String date) {
		HashMap<String, String> oracleDateFormat = new HashMap<String, String>();
		oracleDateFormat.put("dd-MMM-yy hh:mm:ss.SSSSSSSSS a", "dd-MMM-yy hh:mm:ss.SSSSSSSSS a");
		oracleDateFormat.put("dd-MMM-yy HH:mm:ss.SSSSSSSSS", "dd-MMM-yy HH:mm:ss.SSSSSSSSS");
		oracleDateFormat.put("dd-MMM-yy hh:mm:ss a", "dd-MMM-yy hh:mm:ss a");
		oracleDateFormat.put("dd-MMM-yy HH:mm:ss", "dd-MMM-yy HH:mm:ss");
		oracleDateFormat.put("dd-MMM-yy", "dd-MMM-yy");
		oracleDateFormat.put("YYYY-MM-DD hh:mm:ss.SSSSSSSSS a", "dd-MMM-yy hh:mm:ss.SSSSSSSSS a");
		oracleDateFormat.put("YYYY-MM-DD HH:mm:ss.SSSSSSSSS", "dd-MMM-yy HH:mm:ss.SSSSSSSSS");
		oracleDateFormat.put("YYYY-MM-DD hh:mm:ss a", "dd-MMM-yy hh:mm:ss a");
		oracleDateFormat.put("YYYY-MM-DD HH:mm:ss", "dd-MMM-yy HH:mm:ss");
		oracleDateFormat.put("YYYY-MM-DD", "dd-MMM-yy");
		oracleDateFormat.put("yyyyMMdd", "dd-MMM-yy");
		if (date != null) {
			for (Entry<String, String> m : oracleDateFormat.entrySet()) {
				SimpleDateFormat sdf = new SimpleDateFormat(m.getKey().toString());
				try {
					sdf.parse(date);
					LOG.debug(
							"ORACLE:Printing the value of " + m.getKey().toString() + " is " + m.getValue().toString());
					return m.getValue().toString();
				} catch (ParseException e) {

				}

			}
		}
		return null;
	}

	public String mssqlparse(String date) {
		HashMap<String, String> sampleDateFormat = new LinkedHashMap<String, String>();
		sampleDateFormat.put("MMM DD YYYY HH:mma", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("DD MMM YYYY HH:mm:ss:sss", "yyyy-MM-dd HH:mm:ss.sss");
		sampleDateFormat.put("DD MMM YYYY hh:mm:ss:sssa", "yyyy-MM-dd hh:mm:ss.sss a");
		sampleDateFormat.put("DD/MM/YYYY hh:mm:ss:sssa", "yyyy-MM-dd hh:mm:ss.sss a");
		sampleDateFormat.put("MMM DD, YYYY HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("MMM DD YYYY hh:mm:ss:sssa", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("YYYY-MM-DD HH:mm:ss.SSS a", "yyyy-MM-dd HH:mm:ss.SSS a");
		sampleDateFormat.put("YYYY-MM-DD HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS");
		sampleDateFormat.put("YYYY-MM-DD hh:mm:ss.SSSSSS a", "yyyy-MM-dd hh:mm:ss.SSSSSS a");
		sampleDateFormat.put("YYYY-MM-DD HH:mm:ss.SSSSSS", "yyyy-MM-dd HH:mm:ss.SSSSSS");
		sampleDateFormat.put("YYYY-MM-DD HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("YYYY-MM-DD hh:mm:ss a", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("YYYY-MM-DD HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("MM/DD/YYYY hh:mm:ss a", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("MM/DD/YYYY HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("MM/DD/YY hh:mm:ss a", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("MM/DD/YY HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("DD/MM/YY hh:mm:ss a", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("DD/MM/YYYY HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("DD-MM-YYYY hh:mm:ss a", "yyyy-MM-dd hh:mm:ss a");
		sampleDateFormat.put("DD-MM-YYYY HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		sampleDateFormat.put("YYYY-MM-DD", "yyyy-MM-dd");
		sampleDateFormat.put("MM/DD/YY", "yyyy-MM-dd");
		sampleDateFormat.put("MM/DD/YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("YY.MM.DD", "yyyy-MM-dd");
		sampleDateFormat.put("YYYY.MM.DD", "yyyy-MM-dd");
		sampleDateFormat.put("DD/MM/YY", "yyyy-MM-dd");
		sampleDateFormat.put("DD/MM/YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("DD.MM.YY", "yyyy-MM-dd");
		sampleDateFormat.put("DD.MM.YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("DD-MM-YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("DD MMM YY", "yyyy-MM-dd");
		sampleDateFormat.put("DD MMM YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("MMM DD, YY", "yyyy-MM-dd");
		sampleDateFormat.put("MM-DD-YY", "yyyy-MM-dd");
		sampleDateFormat.put("MM-DD-YYYY", "yyyy-MM-dd");
		sampleDateFormat.put("YY/MM/DD", "yyyy-MM-dd");
		sampleDateFormat.put("YYYY/MM/DD", "yyyy-MM-dd");
		sampleDateFormat.put("YYMMDD", "yyyy-MM-dd");
		sampleDateFormat.put("YYYYMMDD", "yyyy-MM-dd");
		sampleDateFormat.put("DD-MM-YY", "yyyy-MM-dd");
		if (date != null) {
			for (Entry<String, String> m : sampleDateFormat.entrySet()) {
				SimpleDateFormat sdf = new SimpleDateFormat(m.getKey().toString());
				try {
					sdf.parse(date);
					LOG.debug(
							"MSSQL:Printing the value of " + m.getKey().toString() + " is " + m.getValue().toString());
					return m.getValue().toString();
				} catch (ParseException e) {

				}

			}
		}
		return null;
	}

	/*--changes for import export 22jan2019 priyanka*/
	public List getdatafromlistappsandlistdatasourcesForExport() {
		try {
			String sql = "select la.name, la.createdAt ,ls.name,ls.idData,la.idApp,la.appType from listApplications la,listDataSources ls where ls.idData = la.idData and la.active='yes' order by la.idApp desc";
			List<ListApplicationsandListDataSources> lsAndLds = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {
						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();
							listappslistds.setLaName(rs.getString("name"));
							listappslistds.setLsName(rs.getString(3));
							listappslistds.setCreatedAt(rs.getDate("createdAt"));
							listappslistds.setIdData(rs.getLong("idData"));
							listappslistds.setIdApp(rs.getLong("idApp"));
							listappslistds.setAppType(rs.getString("appType"));
							return listappslistds;
						}
					});
			return lsAndLds;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDataLocationForIdApp(Long idApp) {
		String dataLocation = null;
		try {
			String sql = "SELECT lds.dataLocation from listDataSources lds join listApplications la  on lds.idData = la.idData where la.active='yes' and la.idApp="
					+ idApp;
			dataLocation = jdbcTemplate.queryForObject(sql, String.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dataLocation;
	}

	@Override
	public List<String> getTemplateListDataSource(Long projectId) {
		String sql = "SELECT name,idData FROM listDataSources where active='yes' and template_create_success='Y' and project_id="
				+ projectId + "  order by idData desc";

		List<String> list;
		List listDataSource = jdbcTemplate.query(sql, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {

				// ListDataSource listDataSource = new ListDataSource();
				List<String> tamplateNameData = new ArrayList();
				// listDataSource.setName(rs.getString("name"));
				tamplateNameData.add(rs.getInt("idData") + "-" + rs.getString("name"));
				// tamplateNameData.add(rs.getInt("idData")).;

				return tamplateNameData.toString();
			}
		});
		return listDataSource;
	}

	@Override
	public long getListDataSourceIdData(String templatename) {

		String sql = "select idData from listDataSources where name='" + templatename + "'";

		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

		while (queryForRowSet.next()) {

			return queryForRowSet.getLong(1);

		}
		return 0l;
	}

	@Override
	public long getlatestListDataSourceIdData() {

		String sql = "SELECT idData FROM listDataSources order by idData desc limit 1 ";
		// SELECT idData FROM databuck_app_db_development.listdatasources order by
		// idData desc limit 1
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

		while (queryForRowSet.next()) {

			return queryForRowSet.getLong(1);

		}
		return 0l;
	}

	/*---- End of changes for import export 22jan2019 priyanka-*/

	@Override
	public List<String> getValidationNames(Long projectId) {
		// String sql = "SELECT name,idApp FROM listApplications WHERE active='yes' and
		// project_id = "+projectId+" ORDER BY idApp DESC";
		boolean isRuleCatalogDiscovery = JwfSpaInfra
				.getPropertyValue(appDbConnectionProperties, "isRuleCatalogDiscovery", "N").equalsIgnoreCase("Y") ? true
						: false;
		boolean isTestApprovalRequired = JwfSpaInfra
				.getPropertyValue(appDbConnectionProperties, "isTestApprovalRequired", "N").equalsIgnoreCase("Y") ? true
						: false;

		String sql = "";
		if (isRuleCatalogDiscovery) {
			if (isTestApprovalRequired) {

				sql = "SELECT name,idApp " + "FROM listApplications " + "WHERE active='yes'\n" + " and project_id="
						+ projectId + " and  ( (appType != 'Data Forensics') "
						+ " or ( appType = 'Data Forensics' and approve_status in (select a.row_id from app_option_list_elements a,\n"
						+ " app_option_list b \n" + " where a.elements2app_list=b.row_id  \n"
						+ " and a.element_reference in ('APPROVED FOR TEST' , 'READY FOR EXPORT','APPROVED FOR EXPORT') \n"
						+ " and b.list_reference='DQ_RULE_CATALOG_STATUS'))) \n" + " ORDER BY idApp DESC";
			}
			if (!isTestApprovalRequired) {
				sql = "SELECT name,idApp " + "FROM listApplications " + "WHERE active='yes'\n" + " and project_id="
						+ projectId + " and  ( (appType != 'Data Forensics') "
						+ " or ( appType = 'Data Forensics' and approve_status in (select a.row_id from app_option_list_elements a,\n"
						+ " app_option_list b \n" + " where a.elements2app_list=b.row_id  \n"
						+ " and a.element_reference in ('READY FOR TEST','APPROVED FOR TEST' , 'READY FOR EXPORT','APPROVED FOR EXPORT') \n"
						+ " and b.list_reference='DQ_RULE_CATALOG_STATUS'))) \n" + " ORDER BY idApp DESC";
			}
		} else
			sql = "SELECT name,idApp FROM listApplications WHERE active='yes' and project_id = " + projectId
					+ " ORDER BY idApp DESC";

		List<String> list;
		List validationNames = jdbcTemplate.query(sql, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {

				// ListDataSource listDataSource = new ListDataSource();
				List<String> validationNameData = new ArrayList();
				// listDataSource.setName(rs.getString("name"));
				validationNameData.add(rs.getInt("idApp") + "-" + rs.getString("name"));
				// tamplateNameData.add(rs.getInt("idData")).;

				return validationNameData.toString();
			}
		});
		return validationNames;
	}

	@Override
	public ListDataSource getTemplateDetailsForAppId(long idApp) {
		try {
			LOG.info("\n====>Execute getValidationCheckOfTemplateById ....");

			String sql = "SELECT * FROM listDataSources where idData=(select idData from listApplications where idApp="
					+ idApp + ")";
			LOG.debug("\n===>Sql: " + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				ListDataSource listDataSource = new ListDataSource();
				listDataSource.setIdData(queryForRowSet.getInt("idData"));
				listDataSource.setName(queryForRowSet.getString("name"));
				listDataSource.setProjectId(new Long(queryForRowSet.getLong("project_id")).intValue());
				listDataSource.setCreatedByUser(queryForRowSet.getString("createdByUser"));
				listDataSource.setIdDataSchema(queryForRowSet.getLong("idDataSchema"));
				listDataSource.setDomain(queryForRowSet.getInt("domain_id"));
				listDataSource.setActive(queryForRowSet.getString("active"));
				return listDataSource;
			}
//			List<ListDataSource> listDataSource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {
//				@Override
//				public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
//					ListDataSource listDataSource = new ListDataSource();
//					listDataSource.setIdData(rs.getInt("idData"));
//					listDataSource.setName(rs.getString("name"));
//					return listDataSource;
//				}
//			});
//			if (listDataSource == null || listDataSource.isEmpty()) {
//				return null;
//			}
//			return listDataSource.get(0);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int updateintolistapplicationForFoundationChecksAjaxRequest(ListApplications listApplications) {
		try {
			String sql = "update listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,updateFrequency=?,frequencyDays=?,"
					+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,keyGroupRecordCountAnomaly=?,"
					+ "outOfNormCheck=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
					+ "buildHistoricFingerPrint=?,historicStartDate=?,historicEndDate=?,historicDateFormat=?,timeSeries=?,"
					+ " validityThreshold=? where idApp=" + listApplications.getIdApp();
			return jdbcTemplate.update(sql,
					new Object[] { listApplications.getColOrderValidation(), listApplications.getFileNameValidation(),
							listApplications.getEntityColumn(), listApplications.getUpdateFrequency(),
							listApplications.getFrequencyDays(), listApplications.getRecordCountAnomaly(),
							listApplications.getRecordCountAnomalyThreshold(),
							listApplications.getkeyBasedRecordCountAnomaly(), "N", listApplications.getGroupEquality(),
							listApplications.getGroupEqualityThreshold(), listApplications.getIncrementalMatching(),
							listApplications.getBuildHistoricFingerPrint(), listApplications.getHistoricStartDate(),
							listApplications.getHistoricEndDate(), listApplications.getHistoricDateFormat(),
							listApplications.getTimeSeries(), listApplications.getValidityThreshold() });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistapplicationForEssentialChecksAjaxRequest(ListApplications listApplications) {
		try {
			String sql = "update listApplications set nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,"
					+ "defaultCheck=?,dataDriftCheck=?,outOfNormCheck=?,csvDir=?,patternCheck=?,"
					+ "dateRuleCheck=?,badData=?,lengthCheck=?,maxLengthCheck=?, dGroupNullCheck=?, dGroupDateRuleCheck=?, dGroupDataDriftCheck=?  where idApp="
					+ listApplications.getIdApp();
			return jdbcTemplate.update(sql,
					new Object[] { listApplications.getNonNullCheck(), listApplications.getNumericalStatCheck(),
							listApplications.getStringStatCheck(), listApplications.getDefaultCheck(),
							listApplications.getDataDriftCheck(), "N", listApplications.getCsvDir(),
							listApplications.getPatternCheck(), listApplications.getDateRuleChk(),
							listApplications.getBadData(), listApplications.getlengthCheck(),
							listApplications.getMaxLengthCheck(), listApplications.getdGroupNullCheck(),
							listApplications.getdGroupDateRuleCheck(), listApplications.getdGroupDataDriftCheck() });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int updateintolistapplicationForAdvancedChecksAjaxRequest(ListApplications listApplications) {
		try {
			String sql = "update listApplications set numericalStatCheck=?, recordAnomalyCheck=?, timelinessKeyCheck=?, applyRules=?, applyDerivedColumns=? where idApp="
					+ listApplications.getIdApp();
			return jdbcTemplate.update(sql,
					new Object[] { listApplications.getNumericalStatCheck(), listApplications.getRecordAnomalyCheck(),
							listApplications.getTimelinessKeyChk(), listApplications.getApplyRules(),
							listApplications.getApplyDerivedColumns() });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<String> getMatchValueColumns(Long idData) {
		List<String> result = new ArrayList<String>();
		try {
			String leftSql = "SELECT  displayName FROM  listDataDefinition WHERE  measurement =  'Y' AND idData ="
					+ idData;
			result = jdbcTemplate.queryForList(leftSql, String.class);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while getting the measurement column size !!");
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<String> getPrimaryColumns(Long idData) {
		List<String> result = new ArrayList<String>();
		try {
			String leftSql = "SELECT  displayName FROM  listDataDefinition WHERE  primaryKey =  'Y' AND idData ="
					+ idData;
			result = jdbcTemplate.queryForList(leftSql, String.class);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while getting the primary column size !!");
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void insertDataIntoListDMCriteriaForRollMatch(Long measurementIdDM, List<String> leftMatchValueCols,
			List<String> rightMatchValueCols) {
		try {
			String sql = "insert into listDMCriteria  (idDM,leftSideExp,rightSideExp) values (?,?,?)";
			int count = 0;
			for (String leftColumn : leftMatchValueCols) {
				String rightColumn = rightMatchValueCols.get(count);
				jdbcTemplate.update(sql, measurementIdDM, leftColumn, rightColumn);

				++count;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public List<String> getDisplayNamesFromListDataDefinitionForReftables(Long idData) {
		try {
			String sql = "select displayName from listDataDefinition where  idData=" + idData;

			List<String> listDataSource = jdbcTemplate.query(sql, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("displayName");
				}
			});
			return listDataSource;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getDisplayNamesFromListDataDefinitionFordGroup(long idData) {
		try {
			List<String> listDataDefinitionColumnNames = new ArrayList<String>();

			LOG.info("\n====>ValidationCheckDAOImpl : getDisplayNamesFromListDataDefinitionFordGroup ....");

			String sql = "SELECT displayName FROM listDataDefinition WHERE dgroup='Y' AND idData =" + idData;
			LOG.debug("\n===>Sql: " + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				listDataDefinitionColumnNames.add(queryForRowSet.getString("displayName"));
			}
			LOG.debug(
					"ValidationCheckDAOImpl : getDisplayNamesFromListDataDefinitionFordGroup : listDataDefinitionColumnNames :: "
							+ listDataDefinitionColumnNames.toString());
			return listDataDefinitionColumnNames;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getIdDataTFromListApplication(Long idApp) {
		String sql = "select idData from listApplications where idApp=" + idApp;
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		while (queryForRowSet.next()) {
			return queryForRowSet.getLong(1);
		}
		return 0l;
	}

	@Override
	public Map<String, String> getRuleCatalogApprovalStatus(Long nIdApp) {
		String sSqlQry = "";
		SqlRowSet oSqlResults = null;
		Map<String, String> oStatusMap = new HashMap<String, String>();

		sSqlQry = sSqlQry + "select\n";
		sSqlQry = sSqlQry + "  case when  b.row_id is null then -1 else b.row_id end as approvalStatusCode,\n";
		sSqlQry = sSqlQry
				+ "  case when  b.row_id is null then 'Not Started' else b.element_text end as approvalStatus, a.approve_comments as approvalComments\n";
		sSqlQry = sSqlQry + "from listApplications a\n";
		sSqlQry = sSqlQry + "  left outer join  app_option_list_elements as b on a.approve_status = b.row_id\n";
		sSqlQry = sSqlQry + String.format("where a.idApp = %1$s;", nIdApp);

		oSqlResults = jdbcTemplate.queryForRowSet(sSqlQry);

		if (oSqlResults.next()) {
			oStatusMap.put("approvalStatus", oSqlResults.getString("approvalStatus"));
			oStatusMap.put("approvalStatusCode", oSqlResults.getString("approvalStatusCode"));
			oStatusMap.put("approvalComments", oSqlResults.getString("approvalComments"));
		}
		return oStatusMap;
	}

	@Override
	public Map<String, String> getStagingRuleCatalogApprovalStatus(Long nIdApp) {
		Map<String, String> oStatusMap = new HashMap<String, String>();

		String sql = "select case when  b.row_id is null then -1 else b.row_id end as approvalStatusCode,"
				+ " case when  b.row_id is null then 'Not Started' else b.element_text end as approvalStatus, a.approve_comments as approvalComments "
				+ " from listApplications a left outer join  app_option_list_elements as b on a.staging_approve_status = b.row_id where a.idApp = ?";

		SqlRowSet oSqlResults = jdbcTemplate.queryForRowSet(sql, nIdApp);

		if (oSqlResults != null && oSqlResults.next()) {
			oStatusMap.put("approvalStatus", oSqlResults.getString("approvalStatus"));
			oStatusMap.put("approvalStatusCode", oSqlResults.getString("approvalStatusCode"));
			oStatusMap.put("approvalComments", oSqlResults.getString("approvalComments"));
		}
		return oStatusMap;
	}

	@Override
	public boolean isRuleCatalogExists(long nIdApp) {
		String sSqlQry = String.format("select count(*) as Count from listApplicationsRulesCatalog where idApp = %1$s",
				nIdApp);

		SqlRowSet oSqlResults = jdbcTemplate.queryForRowSet(sSqlQry);
		boolean lRetValue = (oSqlResults.next() && (oSqlResults.getInt("Count") > 0)) ? true : false;

		return lRetValue;
	}

	@Override
	public List<String> getDisplayName(Long idData) {
		List<String> result = new ArrayList<String>();
		try {
			String leftSql = "SELECT  displayName FROM  listDataDefinition WHERE idData =" + idData;
			result = jdbcTemplate.queryForList(leftSql, String.class);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while getting the Display !!");
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public Long copyValidation(Long idApp, String newValidationName, String createdByUser) {
		Long new_idApp = null;
		try {
			String updateListApplications = ("insert into listApplications (name ,description ,appType ,idData ,"
					+ "idRightData ,createdBy ,createdAt ,updatedAt ,updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,"
					+ "matchingThreshold ,nonNullCheck ,numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,incrementalMatching ,"
					+ "incrementalTimestamp ,dataDriftCheck ,updateFrequency ,frequencyDays ,recordCountAnomaly ,"
					+ "recordCountAnomalyThreshold ,timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,applyRules ,"
					+ "applyDerivedColumns ,csvDir ,groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,historicStartDate ,"
					+ "historicEndDate ,historicDateFormat ,active ,lengthCheck , maxLengthCheck, correlationcheck ,project_id ,timelinessKeyCheck ,"
					+ "defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,idLeftData ,prefix1 ,prefix2 ,"
					+ "dGroupNullCheck ,dGroupDateRuleCheck ,fileMonitoringType ,createdByUser ,validityThreshold ,dGroupDataDriftCheck ,"
					+ "rollTargetSchemaId ,thresholdsApplyOption ,continuousFileMonitoring ,rollType, domain_id, subcribed_email_id, data_domain_id, defaultPatternCheck,validation_job_size) "
					+ "(select '" + newValidationName + "' as name, description, appType, idData, "
					+ "idRightData,createdBy ,now() as createdAt ,now() as updatedAt ,updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,"
					+ "matchingThreshold ,nonNullCheck ,numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,incrementalMatching ,"
					+ "incrementalTimestamp ,dataDriftCheck ,updateFrequency ,frequencyDays ,recordCountAnomaly ,"
					+ "recordCountAnomalyThreshold ,timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,applyRules ,"
					+ "applyDerivedColumns ,csvDir ,groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,historicStartDate ,"
					+ "historicEndDate ,historicDateFormat ,active ,lengthCheck , maxLengthCheck, correlationcheck ,project_id ,timelinessKeyCheck ,"
					+ "defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,idLeftData ,prefix1 ,prefix2 ,"
					+ "dGroupNullCheck ,dGroupDateRuleCheck ,fileMonitoringType ,'" + createdByUser
					+ "' as createdByUser ,validityThreshold ,dGroupDataDriftCheck ,"
					+ "rollTargetSchemaId ,thresholdsApplyOption ,continuousFileMonitoring ,rollType, domain_id, subcribed_email_id, data_domain_id, defaultPatternCheck,validation_job_size"
					+ " from listApplications where " + "idApp = " + idApp + ")");

			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idapp"
					: "idApp";

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(updateListApplications, new String[] { key_name });
					return pst;
				}
			}, keyHolder);

			new_idApp = keyHolder.getKey().longValue();

			newValidationName = (new_idApp) + "_" + newValidationName;

			String updateNameSql = "update listApplications set name='" + newValidationName + "' where idApp="
					+ new_idApp;
			jdbcTemplate.execute(updateNameSql);

			LOG.info("\n=====> listApplication updated");

			// Copy duplicate check details
			String sql = "delete from listDFTranRule where idApp=?";
			jdbcTemplate.update(sql, new_idApp);

			sql = "insert into listDFTranRule(idApp,dupRow,seqRow,seqIDcol,threshold,type) " + "(select " + new_idApp
					+ " AS idApp,dupRow,seqRow,seqIDcol,threshold,type from listDFTranRule where idApp=" + idApp + ")";
			jdbcTemplate.update(sql);

			LOG.debug("\n=====> listDFTranRule updated");

			// Copy Matching validation details into listDMRules and listDMCriteria
			ListApplications listApplications = getdatafromlistapplications(idApp);
			if (listApplications.getAppType().trim().contains("Matching")) {
				sql = "insert into listDMRules(idApp,matchType,matchType2) (select " + new_idApp
						+ " as idApp,matchType,matchType2 from listDMRules where idApp =" + idApp + ")";
				jdbcTemplate.update(sql);

				sql = "insert into listDMCriteria(idDM,leftSideExp,rightSideExp,idLeftColumn,leftSideColumn,idRightColumn,rightSideColumn) (SELECT lrn.idDM,lc.leftSideExp, lc.rightSideExp, lc.idLeftColumn, lc.leftSideColumn, lc.idRightColumn, lc.rightSideColumn FROM listDMRules lr INNER JOIN listDMRules lrn ON lrn.idApp="
						+ new_idApp
						+ " AND lrn.matchType=lr.matchType AND lrn.matchType2=lr.matchType2 LEFT JOIN listDMCriteria lc ON lc.idDM=lr.idDM WHERE lr.idApp="
						+ idApp + ")";
				jdbcTemplate.update(sql);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return new_idApp;
	}

	@Override
	public List<String> getNonDerivedTemplateList(Long projectId) {
		List<String> list = null;
		try {
			// String sql = "SELECT name,idData FROM listDataSources where
			// dataLocation!='Derived' and active='yes' and project_id="
			// + projectId + " ORDER BY idData DESC";
			// changes to get derived as well as non derived template List - 14-Dec-2022

			// DC 2143-Mamta
			// String sql = "SELECT name,idData FROM listDataSources where active='yes' and
			// project_id=" + projectId
			// + " ORDER BY idData DESC";
            
			//DC 2835 Mamta
			//String sql = "SELECT name,idData FROM listDataSources where template_create_success='Y' and project_id="
					//+ projectId + " ORDER BY idData DESC";
			String sql = "SELECT name,idData FROM listDataSources where active='yes' and template_create_success='Y' and project_id="
					+ projectId + " ORDER BY idData DESC";


			list = jdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					List<String> tamplateNameData = new ArrayList<String>();
					tamplateNameData.add(rs.getInt("idData") + "-" + rs.getString("name"));
					return tamplateNameData.toString();
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ListApplicationsandListDataSources> getListApplicationsByIdData(long idData) {
		try {
			String sql = "select la.name, la.createdAt ,ls.name,ls.idData,la.idApp,la.appType,la.createdByUser, la.active from listApplications la,listDataSources ls where ls.idData = la.idData and ls.idData="
					+ idData;

			List<ListApplicationsandListDataSources> lsAndLds = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {
						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();
							listappslistds.setLaName(rs.getString("name"));

							if (rs.getString("appType").equals("Schema Matching"))
								listappslistds.setLsName("");
							else
								listappslistds.setLsName(rs.getString(3));

							listappslistds.setCreatedAt(rs.getDate("createdAt"));
							listappslistds.setIdData(rs.getLong("idData"));
							listappslistds.setIdApp(rs.getLong("idApp"));
							listappslistds.setAppType(rs.getString("appType"));
							listappslistds.setCreatedByUser(rs.getString("createdByUser"));
							listappslistds.setActive(rs.getString("active"));
							return listappslistds;
						}
					});
			return lsAndLds;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getDateRunForUniqueId(String uniqueId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String sql = "select execution_date,run from app_uniqueId_master_table where uniqueId=? order by id desc limit 1";
			List<Map<String, Object>> outputMap = jdbcTemplate1.queryForList(sql, uniqueId);
			if (outputMap != null && outputMap.size() == 1) {
				resultMap = outputMap.get(0);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Date getMaxDateForTable(String tableName, long idApp) {
		Date maxDate = null;
		try {
			String sql = "select max(Date) from " + tableName + " where idApp=?";
			maxDate = jdbcTemplate1.queryForObject(sql, Date.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return maxDate;
	}

	@Override
	public int getMaxRunForDate(String strMaxDate, String tableName, long idApp) {
		int maxRun = 0;
		try {
			String sql = "select max(Run) from " + tableName + " where idApp=" + idApp + " and Date='" + strMaxDate
					+ "'";
			Integer mRun = jdbcTemplate1.queryForObject(sql, Integer.class);
			if (mRun != null)
				maxRun = mRun;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return maxRun;
	}

	@Override
	public String getValidationId(String projectid, String fromDate, String toDate) {
		List<Idapp> IdApp = new ArrayList<>();
		try {

			String sql = "select idApp from listApplications where project_id in (" + projectid + ")  order by idApp";
			List<Long> idAppList = jdbcTemplate.queryForList(sql, Long.class);

			if (idAppList != null && !idAppList.isEmpty()) {
				String idApps = "";
				for (Long idApp : idAppList)
					idApps = idApps + idApp + ",";

				if (idApps.endsWith(","))
					idApps = idApps.substring(0, idApps.length() - 1);

				sql = "select idApp from data_quality_dashboard where idApp in (" + idApps + ")";

				// Apply Date filter
				if (toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {
					sql = sql + " and date >= '" + toDate + "' and date <= '" + fromDate + "'";
				}

				sql = sql + " order by idApp ";

				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
				while (queryForRowSet.next()) {
					Idapp idapp = new Idapp();
					idapp.setIdApp(queryForRowSet.getLong("idApp"));
					IdApp.add(idapp);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		String text = IdApp.toString().replace("[", "").replace("]", "");
		// LOG.debug("text1-->"+text.replace(",","|"));
		return text.replace(",", "|");
	}

	@Override
	public String getApproveStatusById(int statusCode) {
		String approveStatus = null;
		try {
			String sql = "select element_reference from app_option_list_elements where row_id=" + statusCode;
			approveStatus = jdbcTemplate.queryForObject(sql, String.class);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while ApproveStatus by Id !!");
			e.printStackTrace();
		}
		return approveStatus;
	}

	@Override
	public String getTestRunByUniqueId(String uniqueId, long idApp) {
		String testRun = "N";
		try {
			String sql = "select test_run from app_uniqueId_master_table where uniqueId=? and idApp=?";
			String isTestRun = jdbcTemplate1.queryForObject(sql, String.class, uniqueId, idApp);
			if (isTestRun != null && !isTestRun.trim().isEmpty()) {
				testRun = isTestRun.trim();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return testRun;
	}

	@Override
	public boolean updateTemplateIdOfValidation(Long newIdApp, Long newIdData) {
		boolean status = false;
		try {
			String sql = "update listApplications set idData=? where idApp=?";
			int count = jdbcTemplate.update(sql, newIdData, newIdApp);
			if (count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean linkDefectCodeToRule(long idApp, long ruleId, String defectCode) {
		boolean status = false;
		try {
			String sql = "update listApplicationsRulesCatalog set defect_code=? where idApp=? and rule_reference=?";
			int count = jdbcTemplate.update(sql, defectCode, idApp, ruleId);
			if (count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public int getRuleCatalogCreateStatus() {
		String sCreateStatusQry = "";
		int nCreateStatusRowId = -1;
		SqlRowSet oSqlRowSet = null;

		sCreateStatusQry = sCreateStatusQry + "select b.row_id as RowId \n";
		sCreateStatusQry = sCreateStatusQry + "from app_option_list_elements b, app_option_list a \n";
		sCreateStatusQry = sCreateStatusQry + "where b.elements2app_list = a.row_id \n";
		sCreateStatusQry = sCreateStatusQry + "and   upper(list_reference) = 'DQ_RULE_CATALOG_STATUS' \n";
		sCreateStatusQry = sCreateStatusQry
				+ String.format("and   b.element_reference = '%1$s' \n", DatabuckConstants.RULE_CATALOG_CREATE_STATUS);
		sCreateStatusQry = sCreateStatusQry + "and   b.active > 0;";

		oSqlRowSet = jdbcTemplate.queryForRowSet(sCreateStatusQry);
		nCreateStatusRowId = (oSqlRowSet.next()) ? oSqlRowSet.getInt("RowId") : -1;

		DateUtility.DebugLog("getRuleCatalogCreateStatus",
				String.format("nCreateStatusRowId '%1$s'", nCreateStatusRowId));

		return nCreateStatusRowId;
	}

	public boolean isValidationInRunnableStatus(long nIdApp) {
		String sRunnableStatusQry = "";
		boolean lRetValue = false;
		SqlRowSet oSqlRowSet = null;

		sRunnableStatusQry = sRunnableStatusQry + "select count(*) as Count \n";
		sRunnableStatusQry = sRunnableStatusQry + "from listApplications a, app_option_list_elements b \n";
		sRunnableStatusQry = sRunnableStatusQry + "where a.approve_status = b.row_id \n";
		sRunnableStatusQry = sRunnableStatusQry + String.format("and   a.idApp = %1$s \n", nIdApp);
		sRunnableStatusQry = sRunnableStatusQry + String.format("and   b.element_reference in ('%1$s', '%2$s') \n",
				DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1, DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2);
		sRunnableStatusQry = sRunnableStatusQry + "and   b.active > 0;";

		oSqlRowSet = jdbcTemplate.queryForRowSet(sRunnableStatusQry);
		lRetValue = (oSqlRowSet.next()) ? (oSqlRowSet.getInt("Count") > 0 ? true : false) : false;

		DateUtility.DebugLog("isValidationRunnableFromUI",
				String.format("validation app '%1$s' is runnable '%2$s'", nIdApp, lRetValue));

		return lRetValue;
	}

	public String getRunnableStatusRowIds() {
		String sRunnableRowIdQry = "";
		String sRetValue = "";
		SqlRowSet oSqlRowSet = null;

		// Query compatibility changes for both POSTGRES and MYSQL
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sRunnableRowIdQry = sRunnableRowIdQry + "select string_agg(b.row_id::text,',') as RunnableRowIds \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "from app_option_list_elements b, app_option_list a \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "where b.elements2app_list = a.row_id \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "and   upper(list_reference) = 'DQ_RULE_CATALOG_STATUS' \n";
			sRunnableRowIdQry = sRunnableRowIdQry + String.format("and   b.element_reference in ('%1$s', '%2$s') \n",
					DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1, DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2);
			sRunnableRowIdQry = sRunnableRowIdQry + "and   b.active > 0 \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "group by a.row_id;";
		} else {
			sRunnableRowIdQry = sRunnableRowIdQry + "select group_concat(b.row_id) as RunnableRowIds \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "from app_option_list_elements b, app_option_list a \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "where b.elements2app_list = a.row_id \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "and   upper(list_reference) = 'DQ_RULE_CATALOG_STATUS' \n";
			sRunnableRowIdQry = sRunnableRowIdQry + String.format("and   b.element_reference in ('%1$s', '%2$s') \n",
					DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1, DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2);
			sRunnableRowIdQry = sRunnableRowIdQry + "and   b.active > 0 \n";
			sRunnableRowIdQry = sRunnableRowIdQry + "group by a.row_id;";
		}
		oSqlRowSet = jdbcTemplate.queryForRowSet(sRunnableRowIdQry);
		sRetValue = (oSqlRowSet.next()) ? oSqlRowSet.getString("RunnableRowIds") : "";

		DateUtility.DebugLog("getRunnableStatusRowIds", String.format("runnable status row ids '%1$s'", sRetValue));

		return sRetValue;
	}

	@Override
	public List<Long> getValidationIdListForSchema(long idDataSchema) {
		List<Long> validationIdList = null;
		try {
			String sql = "select t1.idApp  from listApplications t1, listDataSources t2 where t1.idData = t2.idData and t1.idApp IS NOT NULL and t2.idDataSchema=?";
			validationIdList = jdbcTemplate.queryForList(sql, Long.class, idDataSchema);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return validationIdList;
	}

	@Override
	public HashMap<Integer, String> getAllValidationDataDomains(boolean lOnlyActive) {
		HashMap<Integer, String> oRetValue = new HashMap<Integer, String>();
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;
		int nActiveFlag = -1;

		try {
			nActiveFlag = (lOnlyActive) ? 0 : -1;
			sSqlQry = String.format(
					"select row_id as DataDomainId, name as DataDomainName from data_domain where active > %1$s;",
					nActiveFlag);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);

			while (oSqlRowSet.next()) {
				oRetValue.put(oSqlRowSet.getInt("DataDomainId"), oSqlRowSet.getString("DataDomainName"));
			}
		} catch (Exception oException) {
			oRetValue = new HashMap<Integer, String>();
			oException.printStackTrace();
		}
		return oRetValue;
	}

	@Override
	public List<DataDomain> getAllDataDomainNames() {

		List<DataDomain> lstDomainName = new ArrayList<DataDomain>();
		try {
			String nameSql = "select row_id,name from data_domain ";

			// lstDomainName = jdbcTemplate.queryForList(nameSql, String.class);

			lstDomainName = jdbcTemplate.query(nameSql, new RowMapper<DataDomain>() {
				@Override
				public DataDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
					DataDomain domainObj = new DataDomain();

					domainObj.setRow_id(rs.getInt("row_id"));
					domainObj.setName(rs.getString("name"));

					LOG.debug("Data Domain =>" + domainObj.getName());
					// DomainNameData.add(rs.getInt("row_id") + "-" + rs.getString("name"));
					return domainObj;
				}
			});

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("\n====> Exception occurred while getting the Display Domain Name!!");
			e.printStackTrace();
		}
		return lstDomainName;

	}

	@Override
	public int getCurrentDataDomainForIdApp(long idApp) {
		int currDataDomainId = 0;
		try {
			String sql = "select data_domain_id from listApplications where idApp=?";
			currDataDomainId = jdbcTemplate.queryForObject(sql, Integer.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return currDataDomainId;

	}

	@Override
	public boolean checkTestRunByExcecutionDateAndRun(long idApp, String executionDate, long run) {
		boolean isTestRun = false;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select test_run from app_uniqueId_master_table where idApp=? and execution_date=?::date and run=? order by id desc limit 1";
			} else {
				sql = "select test_run from app_uniqueId_master_table where idApp=? and execution_date=? and run=? order by id desc limit 1";
			}

			String test_run = jdbcTemplate1.queryForObject(sql, String.class, idApp, executionDate, run);
			if (test_run != null && test_run.trim().equalsIgnoreCase("Y")) {
				isTestRun = true;
			}
		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return isTestRun;
	}

	@Override
	public boolean updateForgotRunStatusOfValidationLatestRun(long idApp, String maxExecDate, long maxExecRun,
			String checkValue, String tableName) {

		boolean updateRunExecutionStatus = false;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sqlCount = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sqlCount = "select count(*) from " + tableName + " where idApp=? and Date=?::Date and Run=?";
			else
				sqlCount = "select count(*) from " + tableName + " where idApp=? and Date=? and Run=?";

			Long runcount = jdbcTemplate1.queryForObject(sqlCount, Long.class, idApp, maxExecDate, maxExecRun);

			// Query compatibility changes for both POSTGRES and MYSQL
			String sqlUpdate = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sqlUpdate = "update " + tableName
						+ " set forgot_run_enabled=? where idApp=? and Date=?::Date and Run=?";
			else
				sqlUpdate = "update " + tableName + " set forgot_run_enabled=? where idApp=? and Date=? and Run=?";

			long updateRowCount = (long) jdbcTemplate1.update(sqlUpdate, checkValue, idApp, maxExecDate, maxExecRun);
			if (runcount == updateRowCount)
				updateRunExecutionStatus = true;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug("can not update table" + tableName);
			e.printStackTrace();
		}
		return updateRunExecutionStatus;
	}

	@Override
	public void updateFoundationChecksDetailsToListApplications(ListApplications listApplications) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "update listApplications set buildHistoricFingerPrint=?,historicStartDate=?::date,historicEndDate=?::date,historicDateFormat=?,"
						+ "incrementalMatching=?, updateFrequency=?,frequencyDays=?,timeSeries=?,keyGroupRecordCountAnomaly=?,recordCountAnomalyThreshold=?,groupEquality=?,"
						+ "groupEqualityThreshold=?,validityThreshold=?,thresholdsApplyOption=?,data_domain_id=?,reprofiling=? where idApp="
						+ listApplications.getIdApp();
			else
				sql = "update listApplications set buildHistoricFingerPrint=?,historicStartDate=?,historicEndDate=?,historicDateFormat=?,"
						+ "incrementalMatching=?, updateFrequency=?,frequencyDays=?,timeSeries=?,keyGroupRecordCountAnomaly=?,recordCountAnomalyThreshold=?,groupEquality=?,"
						+ "groupEqualityThreshold=?,validityThreshold=?,thresholdsApplyOption=?,data_domain_id=? ,reprofiling=? where idApp="
						+ listApplications.getIdApp();
			jdbcTemplate.update(sql,
					new Object[] { listApplications.getBuildHistoricFingerPrint(),
							listApplications.getHistoricStartDate(), listApplications.getHistoricEndDate(),
							listApplications.getHistoricDateFormat(), listApplications.getIncrementalMatching(),
							listApplications.getUpdateFrequency(), listApplications.getFrequencyDays(),
							listApplications.getTimeSeries(), listApplications.getkeyBasedRecordCountAnomaly(),
							listApplications.getRecordCountAnomalyThreshold(), listApplications.getGroupEquality(),
							listApplications.getGroupEqualityThreshold(), listApplications.getValidityThreshold(),
							listApplications.getThresholdsApplyOption(), listApplications.getData_domain(),
							listApplications.getReprofiling() });

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteStagingDbkFileMonitorRules(Long idApp) {
		try {
			String sql = "delete from staging_dbk_file_monitor_rules where validation_id=?";
			jdbcTemplate.update(sql, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteDBkFileMonitoringRules(long idApp) {
		try {
			String sql = "delete from dbk_file_monitor_rules where validation_id=?";
			jdbcTemplate.update(sql, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void copyDbkFileMonitorRulesFromStaging(long idApp) {
		try {
			String sql = "insert into dbk_file_monitor_rules(connection_id,validation_id,schema_name,table_name,file_indicator,dayOfWeek,hourOfDay,expected_time,expected_file_count,start_hour,end_hour,frequency) "
					+ "(select connection_id,validation_id,schema_name,table_name,file_indicator,dayOfWeek,hourOfDay,expected_time,expected_file_count,start_hour,end_hour,frequency from staging_dbk_file_monitor_rules where validation_id=? and rule_delta_type not in ('MISSING'))";
			jdbcTemplate.update(sql, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Long getDbkFileMonitorRulesCountInStaging(Long idApp) {
		long count = 0l;
		try {
			// Don't consider rule which are not changed
			String sql = "select count(*) from staging_dbk_file_monitor_rules where validation_id=? and rule_delta_type not in ('NOCHANGE')";
			count = jdbcTemplate.queryForObject(sql, Long.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public List<DBKFileMonitoringRules> getStagingDbkFileMonitorRules(Long idApp) {
		List<DBKFileMonitoringRules> fileMonitoringRulesList = null;

		try {
			String sql = "select * from staging_dbk_file_monitor_rules where validation_id=" + idApp;

			fileMonitoringRulesList = jdbcTemplate.query(sql, new RowMapper<DBKFileMonitoringRules>() {
				@Override
				public DBKFileMonitoringRules mapRow(ResultSet rs, int rowNum) throws SQLException {
					DBKFileMonitoringRules fileMonitoringRules = new DBKFileMonitoringRules();
					fileMonitoringRules.setConnectionId(rs.getLong("connection_id"));
					fileMonitoringRules.setValidationId(rs.getLong("validation_id"));
					fileMonitoringRules.setSchemaName(rs.getString("schema_name"));
					fileMonitoringRules.setTableName(rs.getString("table_name"));
					fileMonitoringRules.setFileIndicator(rs.getString("file_indicator"));
					fileMonitoringRules.setDayOfWeek(rs.getString("dayOfWeek"));
					fileMonitoringRules.setHourOfDay(rs.getInt("hourOfDay"));
					fileMonitoringRules.setExpectedTime(rs.getInt("expected_time"));
					fileMonitoringRules.setExpectedFileCount(rs.getInt("expected_file_count"));
					fileMonitoringRules.setStartHour(rs.getInt("start_hour"));
					fileMonitoringRules.setEndHour(rs.getInt("end_hour"));
					fileMonitoringRules.setFrequency(rs.getInt("frequency"));
					fileMonitoringRules.setRuleDeltaType(rs.getString("rule_delta_type"));
					return fileMonitoringRules;
				}
			});

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		if (fileMonitoringRulesList == null)
			fileMonitoringRulesList = new ArrayList<>();

		return fileMonitoringRulesList;
	}

	@Override
	public Integer getGlobalRulesCountByTemplateId(long idData) {
		Integer count = 0;
		try {
			String sql = " select count(*) from rule_Template_Mapping r join listColGlobalRules g on "
					+ "r.ruleId = g.idListColrules where r.templateid=?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, idData);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public boolean enableApplyRules(long idApp) {
		int count = 0;
		try {
			String sql = "update listApplications set applyRules='Y' where idApp=?";
			LOG.debug(sql + " idApp=" + idApp);
			count = jdbcTemplate.update(sql, idApp);
			if (count > 0)
				return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public JSONArray getAllDataAsKeyValueByTableName(String tableName, JSONArray tableColumnNames) {
		JSONArray tableData = new JSONArray();
		try {
			String sql = "select * from " + tableName;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			while (queryForRowSet.next()) {

				JSONObject colObj = new JSONObject();
				for (int i = 0; i < tableColumnNames.length(); i++) {
					String col = (String) tableColumnNames.get(i);
					colObj.put(col, queryForRowSet.getObject(col));

				}
				tableData.put(colObj);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return tableData;
	}

	@Override
	public List<String> getMicrosegmentTemplateListDataSource(Long projectId) {
		List<String> listDataSource = new ArrayList<>();

		try {
			String sql = "SELECT name, idData FROM listDataSources WHERE active='yes' AND project_id=" + projectId
					+ " AND idData in (SELECT DISTINCT idData FROM listDataDefinition WHERE dgroup='Y') "
					+ " ORDER BY idData DESC";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				listDataSource.add(queryForRowSet.getInt("idData") + "-" + queryForRowSet.getString("name"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return listDataSource;
	}

	@Override
	public void insertIntolistdfsetruleandtranrule(long idApp) {
		try {

			String query = "insert into listDFTranRule(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
			jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "identity", 0 });

			jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "all", 0 });

			LOG.info("Added new entry int tran rule table.");

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public ListDmCriteria getlistDMCriteriaDetailsByID(long idDMCriteria) {
		ListDmCriteria listDmCriteria = null;
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
				return ldmc;
			};
			String sql = "SELECT * FROM listDMCriteria where idlistDMCriteria=?";
			List<ListDmCriteria> dataList = jdbcTemplate.query(sql, rm, idDMCriteria);
			if (dataList != null && dataList.size() > 0) {
				listDmCriteria = dataList.get(0);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return listDmCriteria;
	}

	@Override
	public Map<String, Object> getListDMRulesByIdDM(Long idDM) {
		Map<String, Object> dmRulesMap = null;
		try {
			String sql = "SELECT idDM,idApp,matchType,matchType2 FROM listDMRules where idDM=?";
			dmRulesMap = jdbcTemplate.queryForMap(sql, idDM);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dmRulesMap;
	}

	public boolean isDuplicateCheckEnabled(long idApp) {
		boolean status = false;
		try {
			String sql = "select count(*) from listDFTranRule where idApp=" + idApp;
			int count = jdbcTemplate.queryForObject(sql, Integer.class);
			if (count > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}
   	@Override
	public ListApplications getListApplicationForRCA(Long idApp, long projectId, long domainId , String validationCheckName){

		ListApplications listApplications = new ListApplications();
		try{

			listApplications.setIdApp(idApp);
			listApplications.setName(idApp + "_" + validationCheckName);
			listApplications.setColOrderValidation("N");
			listApplications.setFileNameValidation("N");
			listApplications.setEntityColumn("N");
			listApplications.setNumericalStatCheck("N");
			listApplications.setStringStatCheck("N");
			listApplications.setDataDriftCheck("N");
			listApplications.setRecordAnomalyCheck("N");
			listApplications.setNonNullCheck("N");
			listApplications.setDupRowIdentity("N");
			listApplications.setDupRowIdentityThreshold(0.0);
			listApplications.setDupRowAll("N");
			listApplications.setDupRowAllThreshold(0.0);
			listApplications.setDuplicateCheck("N");
			listApplications.setUpdateFrequency("Never");
			listApplications.setRecordCountAnomaly("Y");
			listApplications.setkeyBasedRecordCountAnomaly("N");
			listApplications.setApplyDerivedColumns("N");
			listApplications.setApplyRules("Y");
			listApplications.setRecordCountAnomalyThreshold(3.0);
			listApplications.setNumericalStatThreshold(3.0);
			listApplications.setStringStatThreshold(0.0);
			listApplications.setDataDriftThreshold(0.0);
			listApplications.setNonNullThreshold(0.0);
			listApplications.setTimeSeries("None");
			listApplications.setOutOfNormCheck("N");
			listApplications.setGroupEquality("N");
			listApplications.setGroupEqualityThreshold(0.0);
			listApplications.setBuildHistoricFingerPrint("N");
			listApplications.setIncrementalMatching("N");
			listApplications.setTimelinessKeyChk("N");
			listApplications.setDefaultCheck("N");
			listApplications.setlengthCheck("N");
			listApplications.setMaxLengthCheck("N");
			listApplications.setdGroupNullCheck("N");
			listApplications.setdGroupDateRuleCheck("N");
			listApplications.setdGroupDataDriftCheck("N");
			listApplications.setBadData("N");
			listApplications.setPatternCheck("N");
			listApplications.setCorrelationcheck("N");
			listApplications.setDateRuleChk("N");
			listApplications.setProjectId(projectId);
			listApplications.setDomainId(domainId);
			listApplications.setDefaultPatternCheck("N");
			listApplications.setData_domain(2);

		}catch (Exception e){
			e.printStackTrace();
		}
		return listApplications;
	}
	public boolean updateValidationJobsize(int idApp,String validationJobSize){
		boolean status = false;
		String sql="";
		try {
			// Get the validation actual catalog approval status
			String val_cur_approve_status = ruleCatalogDao.getValidationApprovalStatus(idApp);
			if(val_cur_approve_status.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)){
				sql = "update staging_listApplications set validation_job_size = ? where idApp = ?";
			} else  {
				sql = "update listApplications set validation_job_size = ? where idApp = ?";
			}
			int count = jdbcTemplate.update(sql,validationJobSize,idApp);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}
}
