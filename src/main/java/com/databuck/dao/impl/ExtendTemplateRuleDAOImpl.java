package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.TemplateView;
import com.databuck.bean.listDataAccess;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IExtendTemplateRuleDAO;
import com.databuck.service.IProjectService;

@Repository
public class ExtendTemplateRuleDAOImpl implements IExtendTemplateRuleDAO {
	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	private IProjectService IProjectservice;
	
	private static final Logger LOG = Logger.getLogger(ExtendTemplateRuleDAOImpl.class);

	public SqlRowSet checkIfDuplicateRuleNameAndDuplicateDataTemplate(ListColRules lcr) {
		try {
			String query = "SELECT ruleName,idData FROM listColRules WHERE ruleName=? and idData=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, lcr.getRuleName(), lcr.getIdData());
			queryForRowSet.next();
			LOG.debug(queryForRowSet.getString(1) + "" + queryForRowSet.getLong(2));
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}



		public boolean isExtendTemplateRuleAlreadyExists(ListColRules oLcr) {
		String sQuery = "SELECT count(*) as Count FROM listColRules WHERE ruleName=? and idData=?";
		SqlRowSet aRowSet = null;
		boolean lRetValue = false;

		try {
			aRowSet = jdbcTemplate.queryForRowSet(sQuery, oLcr.getRuleName(), oLcr.getIdData());
			aRowSet.first();
			lRetValue = (aRowSet.getLong("Count") > 0) ? true : false;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return lRetValue;
	}

		public long insertintolistColRules(ListColRules lcr) {
			
			// The default dimension for custom rules is 'Validity'
			// If the dimension Id of 'Validity' is not found, we will set default value '0'
			Long defaultDimensionId = 0l;
			if (lcr.getIdDimension() == null || lcr.getIdDimension() <= 0l) {
				try {
					String sql = "select idDimension from dimension where dimensionName='Validity'";
					Integer validity_dims_id = jdbcTemplate.queryForObject(sql, Integer.class);
					if (validity_dims_id != null && validity_dims_id > 0)
						defaultDimensionId = validity_dims_id.longValue();
	
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			} else {
				defaultDimensionId = lcr.getIdDimension();
			}
			
			Long dimension_id = defaultDimensionId;
			
			String query = "insert into listColRules"
					+ "(idData,ruleName,description,createdAt,ruleType,expression,external,"
					+ "externalDatasetName,idRightData,matchingRules,matchType,createdByUser,ruleThreshold,project_id, domain_id,domensionId,anchorColumns) values(?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			// Query compatibility changes for both POSTGRES and MYSQL
			String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idlistcolrules"
					: "idListColrules";
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(query, new String[] { key_name });
					pst.setLong(1, lcr.getIdData());
					pst.setString(2, lcr.getRuleName());
					pst.setString(3,lcr.getDescription());
					pst.setString(4,lcr.getRuleType());
					pst.setString(5,lcr.getExpression());
					pst.setString(6,lcr.getExternal());
					pst.setString(7,lcr.getExternalDatasetName());
					pst.setLong(8,lcr.getIdRightData());
					pst.setString(9,lcr.getMatchingRules());
					pst.setString(10,lcr.getMatchType()); 
					pst.setString(11,lcr.getCreatedByUser());
					pst.setDouble(12,lcr.getRuleThreshold());
					pst.setLong(13,lcr.getProjectId());
					pst.setLong(14,lcr.getDomainId());
					pst.setLong(15,dimension_id);
					pst.setString(16,lcr.getAnchorColumns());
					return pst;
				}
			}, keyHolder);

			Long idListColrules = keyHolder.getKey().longValue();
			return idListColrules;
		}


	public List<ListColRules> getListColRulesForViewRules(long projectId, List<Project> projlst, String fromDate, String toDate) {
		// Sumeet_06_08_2018
		String projIds = IProjectservice.getListofProjectIdsAssignedToCurrentUser(projlst);
		String sql = "";
		// String sql = "select * from listColRules";
		
		sql = sql + "SELECT *, ls.name, p.projectName, CASE WHEN ds.dimensionName IS NULL THEN '' ELSE ds.dimensionName END AS dimensionName ";
		sql = sql + "FROM listColRules la ";
		sql = sql + "LEFT OUTER JOIN listDataSources ls ON ls.idData = la.idData ";
		sql = sql + "LEFT OUTER JOIN dimension ds ON la.domensionId = ds.idDimension ";
		sql = sql + "LEFT OUTER JOIN project p ON ls.project_id = p.idProject ";
		if(toDate != null && !toDate.trim().isEmpty() && fromDate != null && !fromDate.trim().isEmpty()) {			
			sql = sql + "WHERE la.createdAt >= '" + toDate + "' and la.createdAt <= '" + fromDate + "' and ls.project_id in ( " + projectId + " ) and la.activeFlag = 'Y'";
		}else {
			sql = sql + "WHERE ls.project_id in ( " + projectId + " ) and la.activeFlag = 'Y'";
		}
		
		
		LOG.debug("$$$$$ getListColRulesForViewRules ->"+sql);

		List<ListColRules> templateview = jdbcTemplate.query(sql, new RowMapper<ListColRules>() {

			@Override
			public ListColRules mapRow(ResultSet rs, int rowNum) throws SQLException {

				ListColRules listColRules = new ListColRules();
				listColRules.setIdListColrules(rs.getLong("idListColrules"));
				listColRules.setIdData(rs.getLong("idData"));
				listColRules.setTemplateName(rs.getString("name"));
				listColRules.setRuleName(rs.getString("ruleName"));
				listColRules.setDimensionName(rs.getString("dimensionName"));
				listColRules.setDescription(rs.getString("description"));
				listColRules.setCreatedAt(rs.getDate("createdAt"));
				listColRules.setRuleType(rs.getString("ruleType"));
				listColRules.setExpression(rs.getString("expression"));
				listColRules.setMatchingRules(rs.getString("matchingRules"));
				listColRules.setCreatedByUser(rs.getString("createdByUser"));
				listColRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
				listColRules.setProjectId(rs.getLong("project_id"));
				listColRules.setProjectName(rs.getString("projectName"));
				return listColRules;
			}

		});

		return templateview;

	}

	@Override
	public ListColRules getListColRulesById(long idListColrules) {
		String sql = ""
				+ "SELECT la.idListColrules, la.idData, la.ruleName, la.externalDatasetName, la.expression, la.description, la.createdAt, "
				+ "la.ruleType, la.matchingRules, la.external, la.idRightData, la.ruleThreshold,la.anchorColumns, ls.name, "
				+ "CASE WHEN ds.dimensionName IS NULL THEN 'Please Select Dimension' ELSE ds.dimensionName END AS dimensionName, "
				+ "CASE WHEN ds.idDimension IS NULL THEN '-1' ELSE ds.idDimension END AS idDimension  "
				+ "FROM listDataSources ls, listColRules la "
				+ "LEFT OUTER JOIN dimension ds ON la.domensionId = ds.idDimension "
				+ "where la.activeFlag='Y' and la.idData = ls.idData  AND la.idListColrules= " + idListColrules;
		
		return jdbcTemplate.query(sql, new ResultSetExtractor<ListColRules>() {

			public ListColRules extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListColRules listColRules = new ListColRules();
					listColRules.setIdListColrules(rs.getLong("idListColrules"));
					listColRules.setIdData(rs.getLong("idData"));
					listColRules.setTemplateName(rs.getString("name"));
					listColRules.setRuleName(rs.getString("ruleName"));
					listColRules.setDimensionName(rs.getString("dimensionName"));
					listColRules.setExternalDatasetName(rs.getString("externalDatasetName"));
					listColRules.setExpression(rs.getString("expression"));
					listColRules.setDescription(rs.getString("description"));
					listColRules.setCreatedAt(rs.getDate("createdAt"));
					listColRules.setRuleType(rs.getString("ruleType"));
					listColRules.setExpression(rs.getString("expression"));
					listColRules.setMatchingRules(rs.getString("matchingRules"));
					listColRules.setExternal(rs.getString("external"));
					listColRules.setIdRightData(rs.getLong("idRightData"));
					listColRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
					listColRules.setIdDimension(rs.getLong("idDimension"));
					listColRules.setAnchorColumns(rs.getString("anchorColumns"));
					return listColRules;
				}
				return null;

			}

		});

	}

	// Sumeet_08_08_2018   Pradeep 21-Apr-2020 fix long standing bug of ' character
	public void updateintolistColRules(ListColRules lcr) {
		String sUpdateSql = "update listColRules set description='%1$s', expression = '%2$s', matchingRules = '%3$s', ruleThreshold = '%4$s', domensionId = %5$s, anchorColumns= '%6$s' where idListColrules=%7$s;";
		sUpdateSql = String.format(sUpdateSql, lcr.getDescription(), lcr.getExpression(), lcr.getMatchingRules(), lcr.getRuleThreshold(), lcr.getIdDimension(), lcr.getAnchorColumns(), lcr.getIdListColrules());
		
		LOG.debug("updateintolistColRules = " + sUpdateSql);
		
		int nUpdatedRows = jdbcTemplate.update(sUpdateSql);

		/*
		int update = jdbcTemplate.update("UPDATE listColRules SET " + "expression='" + lcr.getExpression(),
				+ "', matchingRules='" + lcr.getMatchingRules() + "' where idListColrules=" + lcr.getIdListColrules());
		*/

		LOG.debug("update = " + nUpdatedRows);

	}

	@Override
	public long getCustomRuleByName(String ruleName) {
		String query = "select idListColrules from listColRules where ruleName=? limit 1";
		long idListColrules = 0l;
		try {
			idListColrules = jdbcTemplate.queryForObject(query, Long.class, ruleName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			idListColrules = 0l;
		}
		return idListColrules;
	}


	@Override
	public SqlRowSet getOperatorsDataFromSymbol() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT id,symbol FROM symbol where Type='Operator' ORDER BY iD DESC ";
		try {
			queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	@Override
	public SqlRowSet getFunctionssDataFromSymbol() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT  id,symbol FROM symbol where Type='Function' ORDER BY iD DESC ";
		try {
			queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}
	
	@Override
	public void deactivateCustomRuleById(long idListColrules) {
		try {
			String sql = "update listColRules set activeFlag='N' where idListColrules=?";
			jdbcTemplate.update(sql, idListColrules);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
