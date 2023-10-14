package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;
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

import com.databuck.bean.Domain;
import com.databuck.bean.GlobalFilters;
import com.databuck.bean.GlobalRuleView;
import com.databuck.bean.ImportExportGlobalRuleDTO;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListColRules;
import com.databuck.bean.RuleToSynonym;
import com.databuck.bean.SynonymLibrary;
import com.databuck.bean.ruleFields;
import com.databuck.bean.RuleMappingDetails;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.GlobalRuleDAO;


@Repository
public class GlobalRuleDAOImpl implements GlobalRuleDAO {
	@Autowired
	private  JdbcTemplate jdbcTemplate;
	
	@Autowired
	private  JdbcTemplate jdbcTemplate1;
	
	@Autowired
	private Properties resultDBConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(GlobalRuleDAOImpl.class);

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
	
	@Override
	public boolean checkIfDuplicateGlobalRuleName(ListColGlobalRules lcr) {
		boolean status = false;
		try {
			String query = "Select count(1) as dup_count from listColGlobalRules where trim(ruleName)=?";
			Long duplicateCount = jdbcTemplate.queryForObject(query, Long.class, lcr.getRuleName().trim());
			if (duplicateCount != null && duplicateCount > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public double getGlobalRulesThresholdByIdListColRules(long idListColRule) {
		double threshold= -1.0;
		try{
			String sql= "select ruleThreshold from listColGlobalRules where idListColrules=?";
			threshold = jdbcTemplate.queryForObject(sql, Double.class, idListColRule);

		}catch(Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return threshold;
	}

	@Override
	public boolean updateListColGlobalRuleThreshold(double threshold, long idListColRule) {
		try{
			String sql="update listColGlobalRules set ruleThreshold=? where idListColrules=?";
			int count = jdbcTemplate.update(sql, threshold, idListColRule);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public int checkIfDuplicateGlobalRule(ListColGlobalRules lcr) {
		try {
			String query = "";
			SqlRowSet queryForRowSet = null;
			String expression="";
			String matchingRules="";

			// Query compatibility changes for both POSTGRES and MYSQL
			if(lcr.getExpression() !=null && !lcr.getExpression().isEmpty()){
				expression =  lcr.getExpression();
				expression = expression.replaceAll("'", "''");
				expression = "'" + expression.replace("\\", "\\\\") + "'";
			}

			if(lcr.getMatchingRules() !=null && !lcr.getMatchingRules().isEmpty()){
				matchingRules= lcr.getMatchingRules();
				matchingRules = matchingRules.replaceAll("'", "''");
				matchingRules = "'" + matchingRules.replace("\\", "\\\\") + "'";
			}

			if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
					|| lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)) {
				query = "select count(1) as dup_count from listColGlobalRules where expression=" + expression
						+ " and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";
				queryForRowSet = jdbcTemplate.queryForRowSet(query, lcr.getDomain_id(),lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());

			} else if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)) {
				query = "select count(1) as dup_count from listColGlobalRules where matchingRules="
						+matchingRules+ " and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";

				queryForRowSet = jdbcTemplate.queryForRowSet(query, lcr.getDomain_id(),lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());

			} else if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {

				query = "select count(1) as dup_count from listColGlobalRules where matchingRules=? and filterId=? and right_template_filter_id=? " +
						" and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";
				queryForRowSet = jdbcTemplate.queryForRowSet(query,matchingRules,lcr.getFilterId(), lcr.getRightTemplateFilterId(), lcr.getDomain_id(), lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());
			
			} else if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE) 
					|| lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
					|| lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
					|| lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

				query = "select count(1) as dup_count from listColGlobalRules where expression=? and filterId=?" +
						" and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";

				queryForRowSet = jdbcTemplate.queryForRowSet(query,expression,lcr.getFilterId(), lcr.getDomain_id(),lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());
				
			} else if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
				query = "select count(1) as dup_count from listColGlobalRules where matchingRules=? and expression=? and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";
				queryForRowSet = jdbcTemplate.queryForRowSet(query, matchingRules, expression, lcr.getDomain_id(),lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());

			}else if (lcr.getRuleType().equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
				query = "select count(1) as dup_count from listColGlobalRules where matchingRules=? and expression=? and domain_id=? and ruleType=? and ruleThreshold=? and dimension_id=? and aggregateResultsEnabled=?";
				queryForRowSet = jdbcTemplate.queryForRowSet(query, matchingRules, expression, lcr.getDomain_id(),lcr.getRuleType(),lcr.getRuleThreshold(),lcr.getDimensionId(), lcr.getAggregateResultsEnabled());
			}

			if (!queryForRowSet.next()) {
				LOG.info("no results");
				return -99;
			} else {
				long request_glbl_rule_Id = lcr.getIdListColrules();
				int dup_count = queryForRowSet.getInt(1);
				
				if(request_glbl_rule_Id > 0l && dup_count == 1) {
					dup_count = 0;
				}
				
				return dup_count;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public int checkIfDuplicateRulefield(ruleFields rc) {
		try {
			String query = "SELECT count(1) as dup_count from ruleFields where usercolumns=? and possiblenames=? group by usercolumns and possiblenames";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, rc.getColumn(), rc.getPossiblenames());
			if (!queryForRowSet.next()) {
				LOG.info("no results");
				return -99;
			} else {
				LOG.debug("dup count is " + queryForRowSet.getInt(1));
				return queryForRowSet.getInt(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;

	}

	public int getMaxSynonymIdFromSynonymLibrary() {
		int maxsynonymId = 0;
		try {
			String query = "SELECT max(synonyms_Id) from SynonymLibrary";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);

			if (!queryForRowSet.next()) {
				//LOG.debug("first entry");
				maxsynonymId = 1;
			} else {
				maxsynonymId = queryForRowSet.getInt(1);
				LOG.debug("max synonym id:" + maxsynonymId);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return maxsynonymId;

	}

	public String getListPossibleName(int domain_id, String fieldname) {
		try {
			String sql = "SELECT possiblenames from SynonymLibrary where domain_Id=? and tableColumn = ?";

			// tableColumn SynonymLibrary possiblenames
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, domain_id, fieldname);
			if (queryForRowSet.next()) {

				return queryForRowSet.getString(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public int getruleId(String rulename, int domain_id) {
		try {
			String query = "SELECT idListColrules as rule_id from listColGlobalRules where ruleName=? and domain_id=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, rulename, domain_id);
			if (!queryForRowSet.next()) {
				LOG.info("no results");
				return -99;
			} else {
				LOG.debug("rule_id is " + queryForRowSet.getInt(1));
				return queryForRowSet.getInt(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public Long insertintolistColRules(ListColGlobalRules lcr) {
		String query = "insert into listColGlobalRules"
				+ "(ruleName,description,createdAt,expression,domain_id,ruleType,externalDatasetName,idRightData,matchingRules,createdByUser,ruleThreshold,project_id, dimension_id,filterId, aggregateResultsEnabled, right_template_filter_id) values(?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idlistcolrules"
				: "idListColrules";
					
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(query, new String[] { key_name });
				pst.setString(1,  lcr.getRuleName());
				pst.setString(2, lcr.getDescription());
				pst.setString(3, lcr.getExpression());
				pst.setInt(4, lcr.getDomain_id());
				pst.setString(5, lcr.getRuleType());
				pst.setString(6, lcr.getExternalDatasetName());
				pst.setLong(7, lcr.getIdRightData());
				pst.setString(8, lcr.getMatchingRules());
				pst.setString(9, lcr.getCreatedByUser());
				pst.setDouble(10, lcr.getRuleThreshold());
				pst.setLong(11, lcr.getProjectId());
				pst.setLong(12, lcr.getDimensionId());
				pst.setInt(13, lcr.getFilterId());
				pst.setString(14, lcr.getAggregateResultsEnabled());
				pst.setInt(15, lcr.getRightTemplateFilterId());
				return pst;
			}
		}, keyHolder);
		
		return keyHolder.getKey().longValue();
	}

	public int insertintoSynonymsLibrary(ruleFields rc) {
		int synonymId = rc.getSynonyms_Id();

		if (synonymId == 0) {
			synonymId = getMaxSynonymIdFromSynonymLibrary() + 1;
		}

		String query = "insert into SynonymLibrary"
				+ "(synonyms_Id,domain_Id,tableColumn,possiblenames) values(?,?,?,?)";

		if (rc.getPossiblenames() == null || rc.getPossiblenames().trim().equals("null")) {
			rc.setPossiblenames("");
		} else if (rc.getPossiblenames().contains("span")) {
			// in case of configuring synonyms by copying text from any web page,
			// span tags occurring -cleaning those span tags
			String s1 = rc.getPossiblenames();
			s1 = s1.substring(54, s1.length() - 1);
			s1 = s1.substring(0, s1.length() - 6);
			rc.setPossiblenames(s1);
			LOG.debug("get value poss name1 " + rc.getPossiblenames());

		}
		LOG.debug("get value poss name2 " + rc.getPossiblenames());
		int update = jdbcTemplate.update(query,
				new Object[] { synonymId, rc.getDomain_id(), rc.getUsercolumns(), rc.getPossiblenames() });
		LOG.debug("insert into ruleusercolumns=" + update);
		return synonymId;
	}

	public int deleteGlobalRulesField(int ruleid) {
		String deleteQuery = "DELETE FROM SynonymLibrary WHERE synonyms_Id=?";
		int update = jdbcTemplate.update(deleteQuery, ruleid);
		LOG.debug("DELETE FROM ruleFields=" + update);
		return update;
	}

	public List<GlobalRuleView> getListColRulesForViewRules() {
		
		String sql = "";
		sql = sql + "SELECT l.*,g.global_filter_condition as filterCondition,rg.global_filter_condition as rightTemplateFilterCondition,d.domainName, dm.dimensionName FROM listColGlobalRules l ";
		sql = sql + "join domain d on l.domain_id=d.domainId ";
		sql = sql + "left join dimension dm on dm.idDimension = l.dimension_id left join global_filters g on l.filterId = g.global_filter_id left join global_filters rg on l.right_template_filter_id = rg.global_filter_id ";
        LOG.debug("Global Rule View: "+sql);
		
		List<GlobalRuleView> templateview = jdbcTemplate.query(sql, new RowMapper<GlobalRuleView>() {
			@Override
			public GlobalRuleView mapRow(ResultSet rs, int rowNum) throws SQLException {
				GlobalRuleView listColRules = new GlobalRuleView();
				listColRules.setIdListColrules(rs.getLong("idListColrules"));
				listColRules.setRuleName(rs.getString("ruleName"));
				listColRules.setDomain(rs.getString("domainName"));
				listColRules.setDescription(rs.getString("description"));
				listColRules.setCreatedAt(rs.getString("createdAt"));
				listColRules.setExpression(rs.getString("expression"));
				listColRules.setProjectId(rs.getLong("project_id"));
				listColRules.setExternalDatasetName("externalDatasetName");
				listColRules.setRuleType(rs.getString("ruleType"));
				listColRules.setMatchingRules(rs.getString("matchingRules"));
				listColRules.setCreatedByUser(rs.getString("createdByUser"));
				listColRules.setDimensionName(rs.getString("dimensionName"));
				listColRules.setFilterCondition(rs.getString("filterCondition"));
				listColRules.setRightTemplateFilterCondition(rs.getString("rightTemplateFilterCondition"));
				listColRules.setFilterId(rs.getInt("filterId"));
				listColRules.setRightTemplateFilterId(rs.getInt("right_template_filter_id"));
				listColRules.setDomainId(rs.getLong("domain_id"));
				listColRules.setDimensionId(rs.getLong("dimension_id"));
				listColRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
				listColRules.setIdRightData(rs.getLong("idRightData"));
				return listColRules;
			}

			/*
			 * public ListColGlobalRules mapRow(ResultSet rs, int rowNum) throws
			 * SQLException { ListColGlobalRules listColRules = new
			 * ListColGlobalRules();
			 * listColRules.setIdListColrules(rs.getLong("idListColrules"));
			 * listColRules.setRuleName(rs.getString("ruleName"));
			 * listColRules.setDomain(rs.getString("domainName"));
			 * listColRules.setDescription(rs.getString("description"));
			 * listColRules.setCreatedAt(rs.getDate("createdAt"));
			 * listColRules.setExpression(rs.getString("expression"));
			 * listColRules.setExternalDatasetName("externalDatasetName");
			 * listColRules.setRuleType(rs.getString("ruleType"));
			 * listColRules.setMatchingRules(rs.getString("matchingRules"));
			 *
			 * return listColRules; }
			 */
		});
		return templateview;
	}

	public List<ruleFields> getListGlobalRulesSynonyms() {
		String sql = "select s.tableColumn,s.possiblenames,d.domainName  from SynonymLibrary s join domain d on s.domain_Id=d.domainId ";
		List<ruleFields> templateview = jdbcTemplate.query(sql, new RowMapper<ruleFields>() {
			@Override
			public ruleFields mapRow(ResultSet rs, int rowNum) throws SQLException {
				ruleFields rulefields = new ruleFields();

				rulefields.setDomain_name(rs.getString("domainName"));
				rulefields.setUsercolumns(rs.getString("tableColumn"));
				rulefields.setPossiblenames(rs.getString("possiblenames"));

				return rulefields;
			}
		});
		return templateview;
	}

	@Override
	public ListColGlobalRules getDataFromListDataSourcesOfIdData(long idData) {

		String sql = " SELECT * FROM listColGlobalRules where idListColrules=" + idData;

		return jdbcTemplate.query(sql, new ResultSetExtractor<ListColGlobalRules>() {

			public ListColGlobalRules extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListColGlobalRules listColRules = new ListColGlobalRules();
					listColRules.setIdListColrules(rs.getLong("idListColrules"));
					listColRules.setRuleName(rs.getString("ruleName"));
					// listColRules.setExpression(rs.getString("expression"));
					listColRules.setDescription(rs.getString("description"));
					listColRules.setCreatedAt(rs.getDate("createdAt"));
					listColRules.setExpression(rs.getString("expression"));
					listColRules.setDomain(rs.getString("domain"));
					listColRules.setDomain_id(rs.getInt("domain_id"));

					return listColRules;
				}
				return null;
			}
		});
	}

	public List<ruleFields> getRuleField(int synonyms_Id) {
		try {
			String sql = " SELECT * FROM SynonymLibrary where synonyms_Id =?";

			List<ruleFields> rules = jdbcTemplate.query(sql, new Object[] { synonyms_Id }, new RowMapper<ruleFields>() {

				@Override
				public ruleFields mapRow(ResultSet rs, int rowNum) throws SQLException {

					ruleFields rf = new ruleFields();
					rf.setSynonyms_Id(rs.getInt("synonyms_Id"));
					rf.setUsercolumns(rs.getString("usercolumns"));
					rf.setPossiblenames(rs.getString("possiblenames"));

					return rf;
				}

			});
			//LOG.debug("1111::::::: " + rules);
			return rules;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public void updateintolistColRules(ListColGlobalRules lcr) {
		try {
			String query = "UPDATE listColGlobalRules SET expression=?, domain_id=?, ruleThreshold=?, matchingRules=?,filterId=?, right_template_filter_id=?, dimension_id = ?, aggregateResultsEnabled=? where idListColrules=?";

			int update = jdbcTemplate.update(query, lcr.getExpression(), lcr.getDomain_id(), lcr.getRuleThreshold(),
					lcr.getMatchingRules(), lcr.getFilterId(), lcr.getRightTemplateFilterId(), lcr.getDimensionId(),
					lcr.getAggregateResultsEnabled(), lcr.getIdListColrules());
			LOG.debug("update=" + update);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void deleteGlobalRulesData(long globalRuleId) {
		// TODO Auto-generated method stub
		String deleteStatement = "DELETE FROM listColGlobalRules WHERE idListColrules=?";
		int update = jdbcTemplate.update(deleteStatement, globalRuleId);
		LOG.debug("DELETE FROM ListColGlobalRules=" + update);
	}
	
	
	@Override
	public long copyGlobalRulesData(String newRuleName, long idListColrules) {
		// TODO Auto-generated method stub
		 String updateListColRules = ("insert into listColGlobalRules (ruleName, "
				 + "description, createdAt, expression, domain_id,matchingRules,ruleType,externalDatasetName,idRightData,"
				 + " project_id,dimension_id , createdByUser ,ruleThreshold, filterId, right_template_filter_id) "
				 + "(select '" + newRuleName + "' as ruleName, description, createdAt, expression,domain_id,matchingRules,ruleType,externalDatasetName,idRightData,"
				 + "project_id , dimension_id ,createdByUser , ruleThreshold, filterId , right_template_filter_id "
				 + "from listColGlobalRules where "
				 + "idListColrules = " + idListColrules + ")");

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idlistcolrules"
				: "idListColrules";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(updateListColRules, new String[] { key_name });
				return pst;
			}
		}, keyHolder);

		LOG.info("listColGlobalRules updated");
		// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
		return keyHolder.getKey().longValue();
	}

	// check domain present or not
	public String getdomainName(String domain) {
		try {
			String query = "SELECT domainName  from domain where domainName=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, domain);
			if (queryForRowSet.next()) {

				return queryForRowSet.getString(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * //for insert domain name in domain table public void
	 * insertInToDamains(ListColGlobalRules lcr) { try { String query =
	 * "insert into domain_name" + "(domainName) values(?)"; int update =
	 * jdbcTemplate.update(query,new Object[] {lcr.getDomain() });
	 * LOG.debug("insert into domain_name=" + update); }catch(Exception
	 * r) { LOG.debug("No record insert in domain_name table"); } }
	 */

	// for domainId 09-07-19
	public int getDomainId(String domain) {
		// String domain1 = domain.substring(1, domain.length() - 1);
		try {
			String query = "SELECT domainId from domain where  domainName=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, domain);
			if (queryForRowSet.next()) {
				LOG.debug("domain id:" + queryForRowSet.getInt(1));
				return queryForRowSet.getInt(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	public List<Domain> getDomainList() {

		String query = "SELECT * from domain";

		List<Domain> domainlist = jdbcTemplate.query(query, new RowMapper<Domain>() {

			@Override
			public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub

				Domain domainlist = new Domain();
				domainlist.setDomainId(rs.getInt(1));
				domainlist.setDomainName(rs.getString(2));

				return domainlist;
			}
		});

		return domainlist;

	}
	
	@Override
	public String getPossibleName(ruleFields rc) {
		// TODO Auto-generated method stub

		try {
			String sql = " SELECT possiblenames FROM SynonymLibrary where domain_Id =? and tableColumn=?";

			//LOG.debug("1111::::::: " + rc.getDomain_id() + " " + rc.getUsercolumns());

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, rc.getDomain_id(), rc.getUsercolumns());
			if (queryForRowSet.next()) {
				LOG.debug("possiblenames is " + queryForRowSet.getString(1));
				return queryForRowSet.getString(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();

		}
		return null;
	}

	@Override
	public int getCountOfTableColumn(ruleFields rc) {
		// TODO Auto-generated method stub

		try {
			String sql = "select count(1) from SynonymLibrary where tableColumn in(?) and domain_Id =?";
			// String sql = " SELECT count FROM SynonymLibrary where domain_Id
			// =? and tableColumn=?";

			//LOG.debug("1111::::::: " + rc.getDomain_id() + " " + rc.getUsercolumns());

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, rc.getUsercolumns(), rc.getDomain_id());
			if (queryForRowSet.next()) {
				LOG.debug("count  is " + queryForRowSet.getInt(1));
				return queryForRowSet.getInt(1);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();

		}

		return 0;
	}

	@Override
	public int updateIntoSynonymsLibrary(ruleFields rc) {
		// TODO Auto-generated method stub
		try {
			if (rc.getPossiblenames().equals("null")) {
				rc.setPossiblenames("");
			}

			String query= "UPDATE SynonymLibrary SET " + "possiblenames='" + rc.getPossiblenames()
					+ "'  where synonyms_Id=" + rc.getSynonyms_Id() + " and domain_Id=" + rc.getDomain_id()
					+ " and tableColumn ='" + rc.getUsercolumns() + "'";
			LOG.debug(query);

			int update = jdbcTemplate.update(query);

			return update;
		} catch (Exception e) {
			LOG.error("problem occured while updating ..");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getsynosymId(ruleFields rc) {
		// TODO Auto-generated method stub
		try {
			String sql = "select synonyms_Id,tableColumn as synonymn_name from SynonymLibrary where tableColumn in('"+rc.getUsercolumns()+"') and domain_Id =?";
			// String sql = " SELECT count FROM SynonymLibrary where domain_Id
			// =? and
			// tableColumn=?";
			LOG.debug("sql="+sql+" ,rc.getUsercolumns()="+rc.getUsercolumns());
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql, rc.getDomain_id());
			if (queryForRowSet.next()) {
				LOG.debug(queryForRowSet.getString("synonymn_name")+" "+queryForRowSet.getInt("synonyms_Id"));
				String tableColumn = queryForRowSet.getString("synonymn_name");
				// Check if the synonym name matches exactly
				if(tableColumn != null && tableColumn.trim().equalsIgnoreCase(rc.getUsercolumns().trim())) {
					LOG.info("synonym name matches");
				}
				return queryForRowSet.getInt("synonyms_Id");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();

		}
		return 0;
	}

	@Override
	public ListColGlobalRules getDataFromListDataSourcesruledomain(long idData, int domain_id) {
		// TODO Auto-generated method stub

		String sql = ""
				+ "select l.idListColrules, l.ruleName, l.expression, l.description, l.createdAt, l.ruleType, l.externalDatasetName, "
				+ "l.matchingRules,l.filterId, l.right_template_filter_id, l.idRightData, l.ruleThreshold, d.domainName, "
				+ "CASE WHEN ds.dimensionName IS NULL THEN 'Please Select Dimension' ELSE ds.dimensionName END AS dimensionName, "
				+ "CASE WHEN ds.idDimension IS NULL THEN '-1' ELSE ds.idDimension END AS idDimension, l.aggregateResultsEnabled "
				+ "from listColGlobalRules l "
				+ "join domain d on l.domain_id=d.domainId "
				+ "left outer join dimension ds on ds.idDimension = l.dimension_id where idListColrules= " + idData
				+ " and domain_id=" + domain_id;
		// String sql = " SELECT * FROM listColGlobalRules where
		// idListColrules=" + idData+ " and domain_id="+domain_id;

		return jdbcTemplate.query(sql, new ResultSetExtractor<ListColGlobalRules>() {

			public ListColGlobalRules extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					ListColGlobalRules listColRules = new ListColGlobalRules();
					listColRules.setIdListColrules(rs.getLong("idListColrules"));
					listColRules.setRuleName(rs.getString("ruleName"));
					listColRules.setExpression(rs.getString("expression"));
					listColRules.setDescription(rs.getString("description"));
					listColRules.setCreatedAt(rs.getDate("createdAt"));
					listColRules.setExpression(rs.getString("expression"));
					listColRules.setDomain(rs.getString("domainName"));
					listColRules.setRuleType(rs.getString("ruleType"));
					listColRules.setExternalDatasetName(rs.getString("externalDatasetName"));
					listColRules.setMatchingRules(rs.getString("matchingRules"));
					listColRules.setIdRightData(rs.getLong("idRightData"));
					listColRules.setRuleThreshold(rs.getDouble("ruleThreshold"));
					listColRules.setDimensionId(rs.getLong("idDimension"));
					listColRules.setDimensionName(rs.getString("dimensionName"));
					listColRules.setFilterId(rs.getInt("filterId"));
					listColRules.setRightTemplateFilterId(rs.getInt("right_template_filter_id"));
					listColRules.setAggregateResultsEnabled(rs.getString("aggregateResultsEnabled"));

					return listColRules;
				}
				return null;
			}
		});

	}

	@Override
	public List<ruleFields> getsynforruledomain(int domain_id, long rule_id) {
		// TODO Auto-generated method stub
		String sql = "select distinct(rts.rule_id),sl.* from ruleTosynonym  rts join SynonymLibrary sl on rts.synonym_id=sl.synonyms_Id where rts.rule_id=? and sl.synonyms_Id= ?";
		// String sql = " SELECT * FROM listColGlobalRules where
		// idListColrules=" + idData+ " and domain_id="+domain_id;

		List<ruleFields> rules = jdbcTemplate.query(sql, new Object[] { rule_id, rule_id },
				new RowMapper<ruleFields>() {

					@Override
					public ruleFields mapRow(ResultSet rs, int rowNum) throws SQLException {

						ruleFields rf = new ruleFields();
						// rf.setSynonyms_Id(rs.getInt("synonyms_Id"));
						rf.setUsercolumns(rs.getString("tableColumn"));
						rf.setPossiblenames(rs.getString("possiblenames"));

						return rf;
					}

				});

		return rules;

	}

	/*
	 * select s.tableColumn,s.possiblenames,s.synonyms_Id from
	 * databuck_app_db_development.synonymlibrary s,
	 * databuck_app_db_development.ruleTosynonym r where
	 * s.synonyms_id=r.synonym_id and r.rule_id=32
	 */

	@Override
	public List<ruleFields> getSynonymIdByRuleId(long idListColrules) {
		/*
		 * String sql = "SELECT synonym_id FROM ruletosynonym where rule_id=?";
		 *
		 * List<Integer> synIds = jdbcTemplate.query(sql, new Object[] {
		 * idListColrules }, new RowMapper<Integer>() {
		 *
		 * @Override public Integer mapRow(ResultSet rs, int rowNum) throws
		 * SQLException { return rs.getInt(1); } });
		 *
		 * LOG.debug("####### Sysnonyms IDs =>"+synIds); //
		 * ruleids.add(rules); return synIds;
		 */

		String sql = "select distinct(s.tableColumn),s.possiblenames,s.synonyms_Id from SynonymLibrary s,ruleTosynonym r where s.synonyms_id=r.synonym_id and r.rule_id= ?";

		List<ruleFields> rules = jdbcTemplate.query(sql, new Object[] { idListColrules }, new RowMapper<ruleFields>() {

			@Override
			public ruleFields mapRow(ResultSet rs, int rowNum) throws SQLException {

				ruleFields rf = new ruleFields();
				rf.setSynonyms_Id(rs.getInt("synonyms_Id"));
				rf.setUsercolumns(rs.getString("tableColumn"));
				rf.setPossiblenames(rs.getString("possiblenames"));

				return rf;
			}

		});

		return rules;

	}

	public List<SynonymLibrary> getSynonymViewList(int nDomainId) {
		String sSqlQry = "";

		sSqlQry = sSqlQry + "select a.synonyms_Id, a.domain_id, b.domainName, a.tableColumn, a.possiblenames\n";
		sSqlQry = sSqlQry + "from SynonymLibrary a, domain b\n";
		sSqlQry = sSqlQry + "where a.domain_id = b.domainId\n";
		sSqlQry = sSqlQry + ( (nDomainId > 0) ? String.format("and   a.domain_id = %1$s\n", nDomainId) : "" );
		sSqlQry = sSqlQry + "order by b.domainName, a.tableColumn;";

		List<SynonymLibrary> aSynonymLibrary = jdbcTemplate.query(sSqlQry, new RowMapper<SynonymLibrary>() {

			@Override
			public SynonymLibrary mapRow(ResultSet oSqlRow, int nRowNum) throws SQLException {

				SynonymLibrary oSynonymLibrary = new SynonymLibrary();
				oSynonymLibrary.setSynonymsId(oSqlRow.getInt("synonyms_Id"));
				oSynonymLibrary.setDomainId(oSqlRow.getInt("domain_id"));
				oSynonymLibrary.setDomainName(oSqlRow.getString("domainName"));
				oSynonymLibrary.setTableColumn(oSqlRow.getString("tableColumn"));
				oSynonymLibrary.setPossibleNames(oSqlRow.getString("possiblenames"));

				return oSynonymLibrary;
			}

		});
		return aSynonymLibrary;
	}

	public int getEnterpriseDomainId() {
		int nDomainId = 0;

		try {

			String sSqlQry = "select domainid as DomainId from domain where is_enterprise_domain = 1;";
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);
			nDomainId = (oSqlRowSet.next()) ? oSqlRowSet.getInt("DomainId") : nDomainId;

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}

		return nDomainId;
	}

	public double getGlobalRuleThreshold(long nIdData, long nIdApp, String sRuleName) {
		String sSqlQry = "";
		Double dRuleThreshold = 0.0;
		SqlRowSet oSqlRowSet = null;

		try {
			if (nIdData < 0) {
				sSqlQry = String.format("select idData from listApplications where idApp = %1$s", nIdApp);
				oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);
				nIdData = (oSqlRowSet.next()) ? oSqlRowSet.getInt("idData") : nIdData;
			}

			sSqlQry = "";
			sSqlQry = sSqlQry + "select a.ruleThreshold \n";
			sSqlQry = sSqlQry + "from listColGlobalRules a, rule_Template_Mapping b \n";
			sSqlQry = sSqlQry + "where a.idListColrules = b.ruleId \n";
			sSqlQry = sSqlQry + "and   b.templateid = %1$s \n";
			sSqlQry = sSqlQry + "and   b.ruleName = '%2$s' \n";
			sSqlQry = sSqlQry + "limit 1;";

			sSqlQry = String.format(sSqlQry, nIdData, sRuleName);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);

			dRuleThreshold = (oSqlRowSet.next()) ? oSqlRowSet.getDouble("ruleThreshold") : dRuleThreshold;
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}

		return dRuleThreshold;
	}

	public String unlinkGlobalRule(int nRuleCatalogRowId, String sRuleType) {
		String sRetMsg = "";
		String sSqlQry = "";
		SqlRowSet oSqlRowSet = null;
		int nMappingRowId = -1;
		int nRowsDeleted = 0;

		sSqlQry = sSqlQry + "select c.id \n";
		sSqlQry = sSqlQry + "from listApplicationsRulesCatalog b, listApplications a, rule_Template_Mapping c \n";
		sSqlQry = sSqlQry + "where b.idApp = a.idApp \n";
		sSqlQry = sSqlQry + "and   a.idData = c.templateid \n";
		sSqlQry = sSqlQry + String.format("and   lower(b.rule_type) = lower('%1$s') \n", sRuleType);
		sSqlQry = sSqlQry + "and   lower(b.rule_name) = lower(c.ruleName) \n";
		sSqlQry = sSqlQry + String.format("and   b.row_id = %1$s", nRuleCatalogRowId);

		try {
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQry);
			nMappingRowId = oSqlRowSet.next() ? oSqlRowSet.getInt("id") : -1;
			
			if (nMappingRowId > -1) {
				sSqlQry = String.format("delete from rule_Template_Mapping where id = %1$s", nMappingRowId);
				nRowsDeleted = jdbcTemplate.update(sSqlQry);
				
				sSqlQry = String.format("delete from listApplicationsRulesCatalog where row_id = %1$s", nRuleCatalogRowId);
				nRowsDeleted = nRowsDeleted + jdbcTemplate.update(sSqlQry);				
			}
			sRetMsg = (nRowsDeleted < 2) ? "Error while unlinking Incorrect or NO rule found linked to data template" : "";
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			sRetMsg  = String.format("Exception while unlinking '%1$s'", oException.getMessage());
		}

		return sRetMsg;
	}

	@Override
	public void insertRuleSynonymMapping(RuleToSynonym ruleToSynonym) {
		try {
			String query = "insert into ruleTosynonym(rule_id,synonym_id) values(?,?)";
			jdbcTemplate.update(query, new Object[] { ruleToSynonym.getGlobalRuleId(), ruleToSynonym.getSynonymId() });
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<ImportExportGlobalRuleDTO> getGlobalRulesOfDomainForExport(int exportDomainId, Long projectId) {
		List<ImportExportGlobalRuleDTO> rulesList = null;
		try {
			String query = "select t1.ruleName,t1.expression,t1.ruleThreshold,t2.synonym_id,t3.tableColumn from listColGlobalRules t1 "
					+ " left outer join ruleTosynonym t2 on t1.idListColrules = t2.rule_id "
					+ " left outer join SynonymLibrary t3 on t2.synonym_id=t3.synonyms_Id"
					+ " where t1.ruleType in ('referential','Referential') and t1.domain_id = " + exportDomainId
					+ " and t1.project_id = " + projectId;
			rulesList = jdbcTemplate.query(query, new RowMapper<ImportExportGlobalRuleDTO>() {

				@Override
				public ImportExportGlobalRuleDTO mapRow(ResultSet rs, int nRowNum) throws SQLException {

					ImportExportGlobalRuleDTO ruleDTO = new ImportExportGlobalRuleDTO();
					ruleDTO.setRuleName(rs.getString("ruleName"));
					String expression = rs.getString("expression");
					ruleDTO.setRuleExpression("\"" + expression + "\"");
					String tableColumn = rs.getString("tableColumn");
					if (tableColumn == null || tableColumn.trim().isEmpty()) {

						if (expression != null && !expression.trim().isEmpty()) {
							// Read expression to get the table column
							String tmp = "";
							for (int i = 0; i < expression.length(); ++i) {
								char s = expression.charAt(i);
								if (Character.isAlphabetic(s) || s == '_') {
									tmp = tmp + s;
								} else {
									break;
								}
							}
							tableColumn = tmp;
						}
					}
					ruleDTO.setColumnName(tableColumn);
					ruleDTO.setRuleThreshold(rs.getDouble("ruleThreshold"));
					return ruleDTO;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return rulesList;
	}

	@Override
	public List<Long> getTemplateIdsForGlobalRuleId(long idListColrules) {
		List<Long> listOfTemplateId = null;
		try{
			String sql="select a.templateid from rule_Template_Mapping a join listColGlobalRules b on a.ruleId=b.idListColrules " +
					"where b.idListColrules="+idListColrules;
			listOfTemplateId = jdbcTemplate.queryForList(sql, Long.class);
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return listOfTemplateId;
	}

	@Override
	public List<GlobalFilters> getListGlobalFilters() {

		String sql = "";
		sql = sql + "SELECT g.*,d.domainName FROM global_filters g ";
		sql = sql + "join domain d on g.domain_id=d.domainId ";

		List<GlobalFilters> globalFiltersList = jdbcTemplate.query(sql, new RowMapper<GlobalFilters>() {
			@Override
			public GlobalFilters mapRow(ResultSet rs, int rowNum) throws SQLException {
				GlobalFilters globalFilters = new GlobalFilters();
				globalFilters.setFilterId(rs.getLong("global_filter_id"));
				globalFilters.setFilterName(rs.getString("global_filter_name"));
				globalFilters.setFilterCondition(rs.getString("global_filter_condition"));
				globalFilters.setDomain(rs.getString("domainName"));
				globalFilters.setDescription(rs.getString("description"));
				globalFilters.setCreatedAt(rs.getDate("createdAt").toString());
				globalFilters.setDomainId(rs.getInt("domain_id"));

				return globalFilters;
			}

		});
		return globalFiltersList;
	}

	@Override
	public GlobalFilters getDataFromGlobalFilters(long filterId, int domain_id) {

		String sql = "";
		sql = sql + "SELECT g.*,d.domainName FROM global_filters g ";
		sql = sql + "join domain d on g.domain_id=d.domainId where g.domain_id="+domain_id+" " +
				"and g.global_filter_id="+filterId;

		return jdbcTemplate.query(sql, new ResultSetExtractor<GlobalFilters>() {

			public GlobalFilters extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					GlobalFilters globalFilters = new GlobalFilters();
					globalFilters.setFilterId(rs.getLong("global_filter_id"));
					globalFilters.setFilterName(rs.getString("global_filter_name"));
					globalFilters.setFilterCondition(rs.getString("global_filter_condition"));
					globalFilters.setDomain(rs.getString("domainName"));
					globalFilters.setDescription(rs.getString("description"));
					globalFilters.setCreatedAt(rs.getDate("createdAt").toString());

					return globalFilters;
				}
				return null;
			}
		});

	}
	
	@Override
	public GlobalFilters getGlobalFilterById(long filterId) {

		String sql = "SELECT g.*,d.domainName FROM global_filters g join domain d on g.domain_id=d.domainId where g.global_filter_id="+filterId;

		return jdbcTemplate.query(sql, new ResultSetExtractor<GlobalFilters>() {

			public GlobalFilters extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					GlobalFilters globalFilters = new GlobalFilters();
					globalFilters.setFilterId(rs.getLong("global_filter_id"));
					globalFilters.setFilterName(rs.getString("global_filter_name"));
					globalFilters.setFilterCondition(rs.getString("global_filter_condition"));
					globalFilters.setDomain(rs.getString("domainName"));
					globalFilters.setDescription(rs.getString("description"));
					globalFilters.setCreatedAt(rs.getDate("createdAt").toString());
					return globalFilters;
				}
				return null;
			}
		});

	}

	@Override
	public int updateIntoGlobalFilters(GlobalFilters globalFilters) {
		int status=0;
		try{
			String query="UPDATE global_filters SET " + "global_filter_condition=?"
					+ " ,  description=? where global_filter_id=? and domain_id=?";

			String description= globalFilters.getDescription();
			String filterCondition= globalFilters.getFilterCondition();

			status = jdbcTemplate.update(query,filterCondition,description,
					globalFilters.getFilterId(),globalFilters.getDomainId());

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}


	@Override
	public Long insertIntoGlobalFilters(GlobalFilters globalFilters) {
		String query = "insert into global_filters"
				+ "(global_filter_name,description,global_filter_condition,createdAt,domain_id) values(?,?,?,now(),?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(query, new String[] { "global_filter_id" });
				pst.setString(1,  globalFilters.getFilterName());
				pst.setString(2, globalFilters.getDescription());
				pst.setString(3, globalFilters.getFilterCondition());
				pst.setInt(4, globalFilters.getDomainId());

				return pst;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	@Override
	public List<GlobalFilters> getAllGlobalFiltersByDomain(int domainId) {
		List<GlobalFilters> globalFilterList= new ArrayList<>();
		try{
			String sql = "";
			sql = sql + "SELECT g.*,d.domainName FROM global_filters g ";
			sql = sql + "join domain d on g.domain_id=d.domainId where g.domain_id="+domainId;

			globalFilterList = jdbcTemplate.query(sql, new RowMapper<GlobalFilters>() {
				@Override
				public GlobalFilters mapRow(ResultSet rs, int rowNum) throws SQLException {
					GlobalFilters globalFilters = new GlobalFilters();
					globalFilters.setFilterId(rs.getLong("global_filter_id"));
					globalFilters.setFilterName(rs.getString("global_filter_name"));
					globalFilters.setFilterCondition(rs.getString("global_filter_condition"));
					globalFilters.setDomain(rs.getString("domainName"));
					globalFilters.setDescription(rs.getString("description"));
					globalFilters.setCreatedAt(rs.getDate("createdAt").toString());
					globalFilters.setDomainId(rs.getInt("domain_id"));

					return globalFilters;
				}

			});

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return globalFilterList;
	}

	@Override
	public JSONObject getGlobalFilterConditionByName(String filterName,int domainId) {
		JSONObject globalFilterObj= new JSONObject();
		try{
			String query="select global_filter_id,global_filter_condition from global_filters where domain_id=? and global_filter_name=? order by global_filter_id desc limit 1";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, domainId, filterName);

			if(queryForRowSet!=null && queryForRowSet.next()) {
				globalFilterObj.put("filterId", queryForRowSet.getInt("global_filter_id"));
				globalFilterObj.put("filterCondition", queryForRowSet.getString("global_filter_condition"));
			}
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return globalFilterObj;
	}

	@Override
	public SqlRowSet getGlobalRulesByFilterId(long filterId) {
		String sql = "";
		sql = sql + "SELECT l.idListColrules,r.templateid as idData FROM listColGlobalRules l join global_filters g ";
		sql = sql + "on g.global_filter_id=l.filterId left outer join rule_Template_Mapping r on r.ruleId = l.idListColrules and g.global_filter_id="+filterId;

		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
		return sqlRowSet;
	}

	@Override
	public SqlRowSet getGlobalRulesByRightTemplateFilterId(long rightTemplateFilterId) {
		String sql = "";
		sql = sql + "SELECT l.idListColrules,r.templateid as idData FROM listColGlobalRules l join global_filters g ";
		sql = sql + "on g.global_filter_id=l.right_template_filter_id left outer join rule_Template_Mapping r on r.ruleId = l.idListColrules and g.global_filter_id="+rightTemplateFilterId;

		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
		return sqlRowSet;
	}

	@Override
	public boolean updateFilterConditionOfRuleTemplateMapping(long templateId, long globalRuleId, String filterCondition) {
		try{
			String sql="update rule_Template_Mapping set filter_condition=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, filterCondition, templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateMatchingRulesOfRuleTemplateMapping(long templateId, long globalRuleId, String matchingRules) {
		try{
			String sql="update rule_Template_Mapping set matchingRules=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, matchingRules, templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateRuleExpressionOfRuleTemplateMapping(long templateId, long globalRuleId,
			String ruleExpression) {
		try{
			String sql="update rule_Template_Mapping set ruleExpression=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, ruleExpression, templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<HashMap<String, String>> getAvailableGlobalRules(String domainIdList) {
		List<HashMap<String, String>> globalRulesList = new ArrayList<HashMap<String, String>>();
		try {
			String sql = "select a.idListColrules as Id, a.ruleName as Name, b.domainName as DomainName, a.ruleType as RuleType, a.idRightData, gf.global_filter_condition as FilterCondition,"
					+ " rgf.global_filter_condition as rightTemplateFilterCondition,a.expression as Expression ,a.matchingRules as MatchingRules, 'false' as Selected"
					+ " from listColGlobalRules a join domain b on a.domain_id = b.domainId"
					+ " left outer join global_filters gf on a.filterId=gf.global_filter_id"
					+ " left outer join global_filters rgf on a.right_template_filter_id=rgf.global_filter_id"
					+ " where  a.ruleType is not null and b.domainId in (" + domainIdList + ")";

			LOG.debug(sql);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			if (queryForRowSet != null) {
				while (queryForRowSet.next()) {
					HashMap<String, String> globalRuleDetailMap = new HashMap<String, String>();
					globalRuleDetailMap.put("Id", queryForRowSet.getString("Id"));
					globalRuleDetailMap.put("DomainName", queryForRowSet.getString("DomainName"));
					globalRuleDetailMap.put("Name", queryForRowSet.getString("Name"));
					globalRuleDetailMap.put("RuleType", queryForRowSet.getString("RuleType"));
					globalRuleDetailMap.put("FilterCondition", queryForRowSet.getString("FilterCondition"));
					globalRuleDetailMap.put("RightTemplateFilterCondition", queryForRowSet.getString("rightTemplateFilterCondition")==null?"":queryForRowSet.getString("rightTemplateFilterCondition"));
					globalRuleDetailMap.put("MatchingRules", queryForRowSet.getString("MatchingRules"));
					globalRuleDetailMap.put("Expression", queryForRowSet.getString("Expression"));
					Long idRightData = queryForRowSet.getLong("idRightData");
					if(idRightData == null || idRightData <= 0l)
						idRightData = 0l;
					globalRuleDetailMap.put("RightTemplateId", idRightData.toString());
					globalRulesList.add(globalRuleDetailMap);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return globalRulesList;
	}

	@Override
	public List<HashMap<String, String>> getAvailableReferenceRulesForTemplate(String domainIdList, long idDataSchema,
			String tableName, long idData) {
		List<HashMap<String, String>> referenceRulesList = new ArrayList<HashMap<String, String>>();
		try {

			String resultDatabaseName = resultDBConnectionProperties.getProperty("db1.schema.name").trim();

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				String tableFilter = "";

				if (idDataSchema > 0l)
					tableFilter = " and  1 = (case when lsc.idDataSchema = " + idDataSchema + " and lda.folderName = '"
							+ tableName + "' then -1 else 1 end) ";

				String sql = "select id as Id, idData as MasterTemplateId, Column_Name as MasterColumnName, Unique_Count as UniqueCount, Min_Length as MinLength, Max_Length as MaxLength from column_profile_master_table  where lower(Data_Type) not in ('date', 'decimal', 'float', 'double') and idData != "
						+ idData;

				SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

				if (queryForRowSet != null) {
					while (queryForRowSet.next()) {

						String masterTemplateId = queryForRowSet.getString("MasterTemplateId");
						HashMap<String, String> globalRuleDetailMap = new HashMap<String, String>();
						globalRuleDetailMap.put("Id", queryForRowSet.getString("Id"));
						globalRuleDetailMap.put("MasterTemplateId", masterTemplateId);
						globalRuleDetailMap.put("MasterColumnName", queryForRowSet.getString("MasterColumnName"));
						globalRuleDetailMap.put("UniqueCount", queryForRowSet.getString("UniqueCount"));
						globalRuleDetailMap.put("MinLength", queryForRowSet.getString("MinLength"));
						globalRuleDetailMap.put("MaxLength", queryForRowSet.getString("MaxLength"));

						String details_sql = "select t2.domain_id as DomainId,dn.domainName as DomainName, "
								+ " lsc.schemaName as MasterConnectionName, t2.name as MasterTemplateName, "
								+ " lda.folderName as MasterTableName from listDataSources t2 "
								+ " join listDataAccess lda on lda.idData=t2.idData"
								+ " join domain dn on t2.domain_id = dn.domainId "
								+ " left outer join listDataSchema lsc on t2.idDataSchema = lsc.idDataSchema  where t2.idData="
								+ masterTemplateId + " and t2.domain_id in (" + domainIdList
								+ ") and lower(t2.active) = 'yes' " + tableFilter;

						SqlRowSet rs = jdbcTemplate.queryForRowSet(details_sql);
						if (rs != null) {
							while (rs.next()) {
								globalRuleDetailMap.put("DomainId", rs.getString("DomainId"));
								globalRuleDetailMap.put("DomainName", rs.getString("DomainName"));
								globalRuleDetailMap.put("MasterConnectionName", rs.getString("MasterConnectionName"));
								globalRuleDetailMap.put("MasterTemplateName", rs.getString("MasterTemplateName"));
								globalRuleDetailMap.put("MasterTableName", rs.getString("MasterTableName"));
								referenceRulesList.add(globalRuleDetailMap);
							}
						}
					}
				}

			} else {

				String tableFilter = "";

				if (idDataSchema > 0l)
					tableFilter = " where  1 = (case when lsc.idDataSchema = " + idDataSchema
							+ " and lda.folderName = '" + tableName + "' then -1 else 1 end) ";

				String sql = "select t1.id as Id, t2.domain_id as DomainId,dn.domainName as DomainName, dn.is_enterprise_domain as IsGlobalDomain, "
						+ " lsc.schemaName as MasterConnectionName, t1.idData as MasterTemplateId, t1.column_name as MasterColumnName, t2.name as MasterTemplateName, "
						+ " lda.folderName as MasterTableName, t1.Unique_Count as UniqueCount,t1.Min_Length as MinLength,t1.Max_Length as MaxLength from "
						+ " (select id, idData, Column_Name, Unique_Count, Min_Length, Max_Length from "
						+ resultDatabaseName + ".column_profile_master_table "
						+ " where lower(Data_Type) not in ('date', 'decimal', 'float', 'double') and idData != "
						+ idData + ") t1 join listDataSources t2 on t1.idData=t2.idData and t2.domain_id in ("
						+ domainIdList + ") and lower(t2.active) = 'yes' join domain dn on t2.domain_id = dn.domainId "
						+ " left outer join listDataSchema lsc on t2.idDataSchema = lsc.idDataSchema "
						+ " join listDataAccess lda on lda.idData=t2.idData " + tableFilter;
				LOG.debug("\n===> sql: " + sql);

				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

				if (queryForRowSet != null) {
					while (queryForRowSet.next()) {
						HashMap<String, String> globalRuleDetailMap = new HashMap<String, String>();
						globalRuleDetailMap.put("Id", queryForRowSet.getString("Id"));
						globalRuleDetailMap.put("DomainId", queryForRowSet.getString("DomainId"));
						globalRuleDetailMap.put("DomainName", queryForRowSet.getString("DomainName"));
						globalRuleDetailMap.put("MasterConnectionName",
								queryForRowSet.getString("MasterConnectionName"));
						globalRuleDetailMap.put("MasterTemplateId", queryForRowSet.getString("MasterTemplateId"));
						globalRuleDetailMap.put("MasterColumnName", queryForRowSet.getString("MasterColumnName"));
						globalRuleDetailMap.put("MasterTemplateName", queryForRowSet.getString("MasterTemplateName"));
						globalRuleDetailMap.put("MasterTableName", queryForRowSet.getString("MasterTableName"));
						globalRuleDetailMap.put("UniqueCount", queryForRowSet.getString("UniqueCount"));
						globalRuleDetailMap.put("MinLength", queryForRowSet.getString("MinLength"));
						globalRuleDetailMap.put("MaxLength", queryForRowSet.getString("MaxLength"));
						referenceRulesList.add(globalRuleDetailMap);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return referenceRulesList;

	}
	
	public List<HashMap<String, String>> getAvailableGlobalThresholds(String domainIdList) {
		List<HashMap<String, String>> globalRulesList = new ArrayList<HashMap<String, String>>();
		try {
			String sql = "select a.idGlobalThreshold as Id, a.globalColumnName, a.nullCountThreshold, a.numericalThreshold, a.lengthCheckThreshold, a.dataDriftThreshold, a.recordAnomalyThreshold "
					+ " from listGlobalThresholds a, domain b where a.domainId = b.domainId and b.domainId in ("
					+ domainIdList + ")";
			LOG.debug("\n===> sql: " + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			if (queryForRowSet != null) {
				while (queryForRowSet.next()) {
					HashMap<String, String> globalRuleDetailMap = new HashMap<String, String>();
					globalRuleDetailMap.put("Id", queryForRowSet.getString("Id"));
					globalRuleDetailMap.put("globalColumnName", queryForRowSet.getString("globalColumnName"));
					globalRuleDetailMap.put("nullCountThreshold",
							((Double) queryForRowSet.getDouble("nullCountThreshold")).toString());
					globalRuleDetailMap.put("numericalThreshold",
							((Double) queryForRowSet.getDouble("numericalThreshold")).toString());
					globalRuleDetailMap.put("lengthCheckThreshold",
							((Double) queryForRowSet.getDouble("lengthCheckThreshold")).toString());
					globalRuleDetailMap.put("dataDriftThreshold",
							((Double) queryForRowSet.getDouble("dataDriftThreshold")).toString());
					globalRuleDetailMap.put("recordAnomalyThreshold",
							((Double) queryForRowSet.getDouble("recordAnomalyThreshold")).toString());
					globalRulesList.add(globalRuleDetailMap);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return globalRulesList;
	}

	@Override
	public void insertIntoListGlobalThresholdsSelected(long idGlobalThreshold, long idData, long idColumn) {
		try {
			String sql = "insert into listGlobalThresholdsSelected(idGlobalThreshold, idData, idColumn) values(?,?,?)";
			jdbcTemplate.update(sql, idGlobalThreshold, idData, idColumn);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public ListColGlobalRules getGlobalRuleById(long globalRuleId) {
		ListColGlobalRules listColGlobalRules = null;
		try {
			String sql = "Select * from listColGlobalRules where idListColrules = " + globalRuleId;
			listColGlobalRules = jdbcTemplate.queryForObject(sql, new RowMapper<ListColGlobalRules>() {
				@Override
				public ListColGlobalRules mapRow(ResultSet rs, int rowNum) throws SQLException {
					ListColGlobalRules globalRule = new ListColGlobalRules();
					globalRule.setIdListColrules(rs.getLong("idListColrules"));
					globalRule.setRuleName(rs.getString("ruleName"));
					globalRule.setDescription(rs.getString("description"));
					globalRule.setExpression(rs.getString("expression"));
					globalRule.setDomain_id(rs.getInt("domain_id"));
					globalRule.setProjectId(rs.getLong("project_id"));
					globalRule.setRuleType(rs.getString("ruleType"));
					globalRule.setExternalDatasetName(rs.getString("externalDatasetName"));
					globalRule.setIdRightData(rs.getLong("idRightData"));
					globalRule.setMatchingRules(rs.getString("matchingRules"));
					globalRule.setRuleThreshold(rs.getDouble("ruleThreshold"));
					globalRule.setDimensionId(rs.getLong("dimension_id"));
					globalRule.setFilterId(rs.getInt("filterId"));
					return globalRule;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return listColGlobalRules;
	}
	
	@Override
	public boolean updateSynonymPossiblenames(long synonyms_Id, String newPossibleName) {
		boolean status = false;
		try {
			String query = "update SynonymLibrary set possiblenames=CONCAT(possiblenames,'," + newPossibleName.trim()
					+ "') where synonyms_Id=" + synonyms_Id;
			int updateCount = jdbcTemplate.update(query);
			if(updateCount > 0)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean updateListColGlobalRuleDimension(long dimensionId, long idListColRule) {
		try{
			String sql="update listColGlobalRules set dimension_id=? where idListColrules=?";
			int count = jdbcTemplate.update(sql, dimensionId, idListColRule);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateRightTemplateFilterConditionOfRuleTemplateMapping(long templateId, long globalRuleId,
			String rightTemplateFilterCondition) {
		try{
			String sql="update rule_Template_Mapping set right_template_filter_condition=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, rightTemplateFilterCondition, templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public JSONObject getValidateGlobalRuleByUniqueId(String uniqueId) {
		JSONObject globalRuleObj = new JSONObject();
		try {
			String sql="select * from run_global_rule_validate_tasks where unique_id='"+uniqueId+"'";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()){
				globalRuleObj.put("id",queryForRowSet.getInt("Id"));
				globalRuleObj.put("uniqueId",queryForRowSet.getString("unique_id"));
				globalRuleObj.put("processId",queryForRowSet.getInt("process_id"));
				globalRuleObj.put("sparkAppId",queryForRowSet.getString("spark_app_id"));
				globalRuleObj.put("deployMode",queryForRowSet.getString("deploy_mode"));
				globalRuleObj.put("startTime",queryForRowSet.getDate("start_time"));
				globalRuleObj.put("endTime",queryForRowSet.getDate("end_time"));
				globalRuleObj.put("triggeredBy",queryForRowSet.getString("triggered_by"));
				globalRuleObj.put("triggeredByHost",queryForRowSet.getString("triggered_by_host"));
				globalRuleObj.put("status",queryForRowSet.getString("status"));
				globalRuleObj.put("executionPercentage",queryForRowSet.getInt("execution_percentage"));
				globalRuleObj.put("isQueryValid",queryForRowSet.getBoolean("is_query_valid"));
				globalRuleObj.put("executionError",queryForRowSet.getString("execution_errors")==null?"":queryForRowSet.getString("execution_errors"));
				globalRuleObj.put("idData",queryForRowSet.getInt("id_data"));
				globalRuleObj.put("idRightData",queryForRowSet.getInt("id_right_data"));
				globalRuleObj.put("ruleId",queryForRowSet.getInt("rule_id"));
				globalRuleObj.put("ruleName",queryForRowSet.getString("rule_name")==null?"":queryForRowSet.getString("rule_name"));
				globalRuleObj.put("ruleType",queryForRowSet.getString("rule_type")==null?"":queryForRowSet.getString("rule_type"));
				globalRuleObj.put("ruleExpression",queryForRowSet.getString("rule_expression")==null?"":queryForRowSet.getString("rule_expression"));
				globalRuleObj.put("matchingRules",queryForRowSet.getString("matching_rules")==null?"":queryForRowSet.getString("matching_rules"));
				globalRuleObj.put("filterCondition",queryForRowSet.getString("filter_condition")==null?"":queryForRowSet.getString("filter_condition"));
				globalRuleObj.put("rightTemplatefilterCondition",queryForRowSet.getString("right_template_filter_condition"));

				String ruleType = queryForRowSet.getString("rule_type");
				List<String> expressions = new ArrayList<>();

				if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
					expressions.add("Rule expression");
				}
				if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {
					expressions.add("Matching expression");
				}
				if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {
					expressions.add("Filter condition");
				}
				if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {
					expressions.add("Refrence Template Filter condition");
				}
				String exceptionMessage = "Incorrect Syntax for "+String.join(",",expressions)+ " , Please use Valid syntax. ";
				if(queryForRowSet.getBoolean("is_query_valid"))
					exceptionMessage="";
				globalRuleObj.put("validateCustomRuleMessage",exceptionMessage);
			}

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return globalRuleObj;
	}

	@Override
	public List<SynonymLibrary> getSynonymListByDomainAndName(int nDomainId, String synonymName) {
		String sSqlQry = "";

		sSqlQry = sSqlQry + "select a.synonyms_Id, a.domain_id, b.domainName, a.tableColumn, a.possiblenames\n";
		sSqlQry = sSqlQry + "from SynonymLibrary a, domain b\n";
		sSqlQry = sSqlQry + "where a.domain_id = b.domainId\n";
		sSqlQry = sSqlQry + "and a.tableColumn='"+synonymName+"'\n";
		sSqlQry = sSqlQry + ( (nDomainId > 0) ? String.format("and   a.domain_id = %1$s\n", nDomainId) : "" );
		sSqlQry = sSqlQry + "order by b.domainName, a.tableColumn;";

		List<SynonymLibrary> aSynonymLibrary = jdbcTemplate.query(sSqlQry, new RowMapper<SynonymLibrary>() {

			@Override
			public SynonymLibrary mapRow(ResultSet oSqlRow, int nRowNum) throws SQLException {

				SynonymLibrary oSynonymLibrary = new SynonymLibrary();
				oSynonymLibrary.setSynonymsId(oSqlRow.getInt("synonyms_Id"));
				oSynonymLibrary.setDomainId(oSqlRow.getInt("domain_id"));
				oSynonymLibrary.setDomainName(oSqlRow.getString("domainName"));
				oSynonymLibrary.setTableColumn(oSqlRow.getString("tableColumn"));
				oSynonymLibrary.setPossibleNames(oSqlRow.getString("possiblenames"));

				return oSynonymLibrary;
			}

		});
		return aSynonymLibrary;
	}

	@Override
	public boolean updateAnchorColumnOfRuleTemplateMapping(long templateId, long globalRuleId, String anchorColumns) {
		try{
			String sql="update rule_Template_Mapping set anchorColumns=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, anchorColumns, templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public HashMap<String, String> getNullFilterColumnsForTemplate(long templateId) {
		HashMap<String, String> result = new HashMap<>();
		try {
			String sql = "select * from rule_Template_Mapping where templateid=" + templateId;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				result.put(queryForRowSet.getString("ruleId"), queryForRowSet.getString("null_filter_columns"));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JSONObject getValidateGlobalFilterByUniqueId(String uniqueId) {
		JSONObject globalRuleObj = new JSONObject();
		try {
			String sql="select * from run_global_filter_validate_tasks where unique_id='"+uniqueId+"'";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()){
				globalRuleObj.put("id",queryForRowSet.getInt("Id"));
				globalRuleObj.put("uniqueId",queryForRowSet.getString("unique_id"));
				globalRuleObj.put("processId",queryForRowSet.getInt("process_id"));
				globalRuleObj.put("sparkAppId",queryForRowSet.getString("spark_app_id"));
				globalRuleObj.put("deployMode",queryForRowSet.getString("deploy_mode"));
				globalRuleObj.put("startTime",queryForRowSet.getDate("start_time"));
				globalRuleObj.put("endTime",queryForRowSet.getDate("end_time"));
				globalRuleObj.put("triggeredBy",queryForRowSet.getString("triggered_by"));
				globalRuleObj.put("status",queryForRowSet.getString("status"));
				globalRuleObj.put("executionPercentage",queryForRowSet.getInt("execution_percentage"));
				globalRuleObj.put("isFilterQueryValid",queryForRowSet.getBoolean("is_filter_query_valid"));
				globalRuleObj.put("executionError",queryForRowSet.getString("execution_errors")==null?"":queryForRowSet.getString("execution_errors"));
				globalRuleObj.put("idData",queryForRowSet.getInt("id_data"));
				globalRuleObj.put("filterCondition",queryForRowSet.getString("filter_condition")==null?"":queryForRowSet.getString("filter_condition"));

				String filterCondition = queryForRowSet.getString("filter_condition")==null?"":queryForRowSet.getString("filter_condition");
				String exceptionMessage = "Please review your Filter Condition:"+filterCondition;
				if(queryForRowSet.getBoolean("is_filter_query_valid"))
					exceptionMessage="";
				globalRuleObj.put("validateGlobalFilterMessage",exceptionMessage);
			}

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return globalRuleObj;
	}

	@Override
	public boolean isDuplicateGlobalFilter(int domainId,String globalFilterName, String filterCondition){
		boolean isDuplicateFilter=false;
		try{
			String sql="select count(*) from global_filters where domain_id=? AND (global_filter_name=? OR global_filter_condition=?)";

			System.out.println("select count(*) from global_filters where domain_id="+domainId+" AND (global_filter_name='"+globalFilterName+"' OR global_filter_condition='"+filterCondition+"')");
			int count = jdbcTemplate.queryForObject(sql,Integer.class,domainId,globalFilterName,filterCondition);
			if(count > 0)
				isDuplicateFilter=true;

		}catch (Exception e){
			e.printStackTrace();
		}
		return isDuplicateFilter;
	}
	@Override
	public List<RuleMappingDetails> getMappedGlobalRuleForTemplate(long templateId) {
		List<RuleMappingDetails> referenceRulesList = new ArrayList<RuleMappingDetails>();
		try {

			String sql = "select * from rule_Template_Mapping where templateid=" + templateId;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				RuleMappingDetails mappingDetails = new RuleMappingDetails();

				mappingDetails.setId(queryForRowSet.getInt("id"));
				mappingDetails.setTemplateid(queryForRowSet.getInt("templateid"));
				mappingDetails.setRuleId(queryForRowSet.getString("ruleId"));
				mappingDetails.setRuleName(queryForRowSet.getString("ruleName"));
				mappingDetails.setRuleExpression(queryForRowSet.getString("ruleExpression"));
				mappingDetails.setRuleType(queryForRowSet.getString("ruleType"));
				mappingDetails.setAnchorColumns(queryForRowSet.getString("anchorColumns"));
				mappingDetails.setActiveFlag(queryForRowSet.getString("activeFlag"));
				mappingDetails.setFilterCondition(queryForRowSet.getString("filter_condition"));
				mappingDetails.setMatchingRules(queryForRowSet.getString("matchingRules"));
				mappingDetails.setRightTemplateFilterCondition(queryForRowSet.getString("right_template_filter_condition"));
				mappingDetails.setNullFilterColumns(queryForRowSet.getString("null_filter_columns"));

				referenceRulesList.add(mappingDetails);
			}


		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return referenceRulesList;

	}

	@Override
	public List<ListColGlobalRules> getGlobalRulesForExport(String globalRuleId) {
		List<ListColGlobalRules> listColGlobalRules = new ArrayList<>();
		try {
			String sql = "Select * from listColGlobalRules where idListColrules in (" + globalRuleId + ")";

			String sQry = "SELECT l.*,g.global_filter_condition as filterCondition,rg.global_filter_condition as rightTemplateFilterCondition,"
					+ " d.domainName, dm.dimensionName FROM listColGlobalRules l "
					+ "join domain d on l.domain_id=d.domainId "
					+ "left join dimension dm on dm.idDimension = l.dimension_id "
					+ "left join global_filters g on l.filterId = g.global_filter_id "
					+ "left join global_filters rg on l.right_template_filter_id = rg.global_filter_id "
					+ "where l.idListColrules in (" + globalRuleId + ")";

			SqlRowSet rs = jdbcTemplate.queryForRowSet(sQry);
			while (rs.next()) {
				ListColGlobalRules globalRule = new ListColGlobalRules();
				globalRule.setIdListColrules(rs.getLong("idListColrules"));
				globalRule.setRuleName(rs.getString("ruleName"));
				globalRule.setDescription(rs.getString("description"));

				globalRule.setExpression(rs.getString("expression"));
				globalRule.setDomain_id(rs.getInt("domain_id"));
				globalRule.setProjectId(rs.getLong("project_id"));
				globalRule.setRuleType(rs.getString("ruleType"));

				globalRule.setExternalDatasetName(rs.getString("externalDatasetName"));
				globalRule.setIdRightData(rs.getLong("idRightData"));
				globalRule.setMatchingRules(rs.getString("matchingRules"));

				globalRule.setRuleThreshold(rs.getDouble("ruleThreshold"));
				globalRule.setDimensionId(rs.getLong("dimension_id"));
				globalRule.setFilterId(rs.getInt("filterId"));
				globalRule.setAggregateResultsEnabled(rs.getString("aggregateResultsEnabled"));
				globalRule.setRightTemplateFilterId(rs.getInt("right_template_filter_id"));

				globalRule.setFilterCondition(rs.getString("filterCondition"));
				globalRule.setRightTemplateFilterCondition(rs.getString("rightTemplateFilterCondition"));
				globalRule.setDomain(rs.getString("domainName"));
				globalRule.setDimensionName(rs.getString("dimensionName"));

				listColGlobalRules.add(globalRule);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return listColGlobalRules;
	}

	@Override
	public List<GlobalFilters> getGlobalFilterForExport(String filterId) {

		List<GlobalFilters> globalFiltersList = new ArrayList<>();
		String sql = "SELECT * FROM global_filters where global_filter_id in (" + filterId + ")";
		SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
		if (rs != null) {
			while (rs.next()) {
				GlobalFilters globalFilters = new GlobalFilters();
				globalFilters.setFilterId(rs.getLong("global_filter_id"));
				globalFilters.setFilterName(rs.getString("global_filter_name"));
				globalFilters.setDescription(rs.getString("description"));
				globalFilters.setFilterCondition(rs.getString("global_filter_condition"));
				globalFilters.setCreatedAt(rs.getDate("createdAt").toString());
				globalFilters.setDomainId(rs.getInt("domain_id"));
				globalFiltersList.add(globalFilters);
			}
		}
		return globalFiltersList;
	}

}