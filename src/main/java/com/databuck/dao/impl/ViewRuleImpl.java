package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.databuck.bean.GlobalRuleView;
import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.ViewRule;
import com.databuck.dao.IViewRuleDAO;

@Repository
public class ViewRuleImpl implements IViewRuleDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	
	private static final Logger LOG = Logger.getLogger(ViewRuleImpl.class);

	@Override
	public List<ViewRule> getViewRuleList() {
		String sql = "select * from ruleMapping";
		List<ViewRule> listViewRule = jdbcTemplate.query(sql, new RowMapper<ViewRule>() {
			public ViewRule mapRow(ResultSet rs, int rowNum) throws SQLException {
				ViewRule listViewRule = new ViewRule();

				listViewRule.setIdruleMap(rs.getLong("idruleMap"));
				listViewRule.setViewName(rs.getString("viewName"));
				listViewRule.setDescription(rs.getString("description"));
				listViewRule.setIdListColrules(rs.getString("idListColrules"));
				listViewRule.setIdData(rs.getString("idData"));
				listViewRule.setIdApp(rs.getString("idApp"));
				return listViewRule;
			}
		});
		return listViewRule;
	}

	@Override
	public List<GlobalRuleView> getRuleListByTempalte(Long idData) {
		String sql = "SELECT lc.idlistColRules, lc.ruleName, lc.description, lc.ruleType, lc.expression, ld.name from listColRules lc join listDataSources ld on ld.idData=lc.idData where lc.activeFlag='Y' and lc.idData="
				+ idData;
		List<GlobalRuleView> listColRules = jdbcTemplate.query(sql, new RowMapper<GlobalRuleView>() {
			public GlobalRuleView mapRow(ResultSet rs, int rowNum) throws SQLException {
				GlobalRuleView listColRules = new GlobalRuleView();
				listColRules.setIdListColrules(rs.getLong("idListColrules"));
				listColRules.setRuleName(rs.getString("ruleName"));
				listColRules.setDescription(rs.getString("description"));
				listColRules.setRuleType(rs.getString("ruleType"));
				listColRules.setExpression(rs.getString("expression"));
				listColRules.setName(rs.getString("name"));
				return listColRules;
			}
		});
		return listColRules;
	}

	@Override
	public List<RuleCatalog> getRuleListByRuleId(String idlistColRules) {
		// String sql =
		// "SELECT lc.idlistColRules, lc.ruleName, lc.description, lc.ruleType,
		// lc.expression, ld.name from listColRules lc join listDataSources ld on
		// ld.idData=lc.idData where lc.idlistColRules = " +
		// idlistColRules;
		String sql = "SELECT larc.activeFlag ,larc.row_id,larc.idApp,larc.rule_reference,\r\n"
				+ "larc.rule_type,case when length(larc.column_name) > 0 then larc.column_name else larc.rule_name end as column_name,larc.rule_category,larc.rule_expression,larc.threshold_value,\r\n"
				+ "lds.name\r\n" + "from listApplicationsRulesCatalog larc join listApplications la \r\n"
				+ "on larc.idApp = la.idApp \r\n" + "join listDataSources lds \r\n" + "on la.idData = lds.idData \r\n"
				+ "where larc.row_id in(" + idlistColRules + ")";
//	    return jdbcTemplate.queryForRowSet(sql);
		List<RuleCatalog> listColRules = jdbcTemplate.query(sql, new RowMapper<RuleCatalog>() {
			public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
				RuleCatalog listColRules = new RuleCatalog();
				listColRules.setRowId(rs.getLong("row_id"));
				listColRules.setIdApp(rs.getLong("idApp"));
				listColRules.setActiveFlag(rs.getBoolean("activeFlag"));
				listColRules.setRuleReference(rs.getLong("rule_reference"));
				listColRules.setRuleType(rs.getString("rule_type"));
				listColRules.setColumnName(rs.getString("column_name"));
				listColRules.setRuleCategory(rs.getString("rule_category"));
				listColRules.setRuleExpression(rs.getString("rule_expression"));
				listColRules.setThreshold(rs.getLong("threshold_value"));
				listColRules.setLaName(rs.getString("name"));
				return listColRules;
			}
		});
		return listColRules;

	}

	@Override
	public List<ViewRule> getViewRuleById(Long idruleMap) {
		String sql = "select * from ruleMapping where idruleMap=" + idruleMap;
		List<ViewRule> listViewRule = jdbcTemplate.query(sql, new RowMapper<ViewRule>() {
			public ViewRule mapRow(ResultSet rs, int rowNum) throws SQLException {
				ViewRule listViewRule = new ViewRule();

				listViewRule.setIdruleMap(rs.getLong("idruleMap"));
				listViewRule.setViewName(rs.getString("viewName"));
				listViewRule.setDescription(rs.getString("description"));
				listViewRule.setIdListColrules(rs.getString("idListColrules"));
				listViewRule.setIdData(rs.getString("idData"));
				listViewRule.setIdApp(rs.getString("idApp"));
				return listViewRule;
			}
		});
		return listViewRule;
	}

	@Override
	public boolean saveDashboardRule(ViewRule viewRuleObj) {
		String sql = "insert into ruleMapping(viewName,description,idListColrules,idData,idApp) values(?,?,?,?,?)";
		int value = jdbcTemplate.update(sql, viewRuleObj.getViewName(), viewRuleObj.getDescription(),
				viewRuleObj.getIdListColrules(), viewRuleObj.getIdData(), viewRuleObj.getIdApp());
		if (value == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateDashboardRule(ViewRule viewRuleObj) {
		String sql = "update ruleMapping set viewName='" + viewRuleObj.getViewName() + "',description='"
				+ viewRuleObj.getDescription() + "', idListColrules='" + viewRuleObj.getIdListColrules() + "',idData='"
				+ viewRuleObj.getIdData() + "',idApp='" + viewRuleObj.getIdApp() + "' where idruleMap="
				+ viewRuleObj.getIdruleMap();
		LOG.debug(sql);
		int update = jdbcTemplate.update(sql);
		LOG.debug("update: " + update);

		if (update == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean deleteView(Long idruleMap) {
		int update = 0;
		try {
			String sqlRoleModule = "DELETE FROM ruleMapping WHERE idruleMap=" + idruleMap;
			update = jdbcTemplate.update(sqlRoleModule);
			if (update == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<ListDataSource> getListDataSources(Long projectId, String idData) {
		String sql = "select lds.idData,lds.name,lds.dataLocation,lds.createdAt,lds.createdByUser,p.projectName from listDataSources lds JOIN project p ON lds.project_id = p.idProject where lds.idData in( "
				+ idData + " ) and lds.project_id=" + projectId;
		LOG.debug("sql: " + sql);
		List<ListDataSource> listDataSource = jdbcTemplate.query(sql, new RowMapper<ListDataSource>() {

			public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
				ListDataSource listDataSource = new ListDataSource();
				listDataSource.setIdData(rs.getInt("idData"));
				listDataSource.setName(rs.getString("name"));
				listDataSource.setDataLocation(rs.getString("dataLocation"));
				listDataSource.setCreatedAt(rs.getDate("createdAt"));
				listDataSource.setCreatedByUser(rs.getString("createdByUser"));
				listDataSource.setProjectName(rs.getString("projectName"));
				return listDataSource;
			}
		});
		return listDataSource;
	}

	@Override
	public List<ListApplicationsandListDataSources> getlistOfValidation(Long projectId, String idData) {
		try {
			String sql = "SELECT idApp,name from listApplications where idData=" + idData + " and project_id="
					+ projectId;
			List<ListApplicationsandListDataSources> listdatasource = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {

						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources alistdatasource = new ListApplicationsandListDataSources();
							alistdatasource.setIdApp(rs.getLong("idApp"));
							alistdatasource.setLaName(rs.getString("name"));
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

	@Override
	public List<RuleCatalog> getRulesByIdApp(long nIdApp) {
		String sSqlQry = "";
		sSqlQry = sSqlQry + "select a.row_id, a.idApp, a.rule_reference, a.rule_type,\n";
		sSqlQry = sSqlQry
				+ "case when length(a.column_name) > 0 then a.column_name else a.rule_name end as column_name,\n";
		sSqlQry = sSqlQry + "a.rule_category, a.rule_expression, a.threshold_value, \n";
		sSqlQry = sSqlQry + "a.review_by, a.activeFlag, a.agingCheckEnabled,lds.name \n";
		sSqlQry = sSqlQry + "from listApplicationsRulesCatalog a join listApplications la \r\n"
				+ "on a.idApp = la.idApp \r\n" + "join listDataSources lds \r\n" + "on la.idData = lds.idData \n";
		sSqlQry = sSqlQry + "where a.idApp = " + nIdApp + "; \n";
		List<RuleCatalog> listColRules = jdbcTemplate.query(sSqlQry, new RowMapper<RuleCatalog>() {
			public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
				RuleCatalog listColRules = new RuleCatalog();
				listColRules.setRowId(rs.getLong("row_id"));
				listColRules.setIdApp(rs.getLong("idApp"));
				listColRules.setActiveFlag(rs.getBoolean("activeFlag"));
				listColRules.setRuleReference(rs.getLong("rule_reference"));
				listColRules.setRuleType(rs.getString("rule_type"));
				listColRules.setColumnName(rs.getString("column_name"));
				listColRules.setRuleCategory(rs.getString("rule_category"));
				listColRules.setRuleExpression(rs.getString("rule_expression"));
				listColRules.setThreshold(rs.getLong("threshold_value"));
				listColRules.setLaName(rs.getString("name"));
				return listColRules;
			}
		});
		return listColRules;
	}

	@Override
	public List<ListApplicationsandListDataSources> getDataFromListDataSources(Long projId) {

		try {

			String sql = "select DISTINCT ls.name,ls.idData from listDataSources ls join listApplications la on ls.idData = la.idData join project p on ls.project_id = p.idProject left join app_option_list_elements el  on la.approve_status = el.row_id where la.project_id in ("
					+ projId + " )";

			LOG.debug("sql -> " + sql);
			List<ListApplicationsandListDataSources> lsAndLds = jdbcTemplate.query(sql,
					new RowMapper<ListApplicationsandListDataSources>() {
						public ListApplicationsandListDataSources mapRow(ResultSet rs, int rowNum) throws SQLException {
							ListApplicationsandListDataSources listappslistds = new ListApplicationsandListDataSources();

							listappslistds.setLsName(rs.getString("name"));
							listappslistds.setIdData(rs.getLong("idData"));

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
	public Map<String, Object> getResultsForBadDataCheck(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select  TotalBadRecord as failedRecords, badDataThreshold as ResultThreshold, Status as resultStatus, 0.0 as resultDQI"
					+ " from DATA_QUALITY_badData where  idApp=? and Date=? and Run=? and (ColName=? || colName like '"
					+ columnName + "__%') limit 1";

			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@Override
	public Map<String, Object> getResultsForDataDriftCheck(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select (missingValueCount + newValueCount) as failedRecords, case when (missingValueCount + newValueCount)  > 0 then  'failed' else 'passed' end  as resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_DATA_DRIFT_COUNT_SUMMARY where idApp=? and Date=? and Run=? and ColName = ? limit 1";

			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);
		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForDateRuleCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select TotalNumberOfRecords as totalRecords ,  TotalFailedRecords as failedRecords,"
					+ "case when TotalFailedRecords > 0 then  'failed' else 'passed' end  as resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_DateRule_Summary where idApp=? and Date=? and Run=? and DateField = ? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);
		
		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForDefaultCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");
			long totalRecords = (long) oRuleCatalogRow.get("totalRecords");

			String sql = "select ABS((" + totalRecords
					+ "-sum(Default_Count))) AS failedRecords, case when sum(Default_Count) = 0 then  'failed' else 'passed' end  as resultStatus, 0.0 as resultDQI  from DATA_QUALITY_default_value where idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForLengthCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select RecordCount as totalRecords , TotalFailedRecords as failedRecords, Length_Threshold as resultThreshold, Status as resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_Length_Check where idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	//Max Length Check
	@Override
	public Map<String, Object> getResultsForMaxLengthCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select RecordCount as totalRecords , TotalFailedRecords as failedRecords, Length_Threshold as resultThreshold, Status as resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_Max_Length_Check where idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}



	@Override
	public Map<String, Object> getResultsForNullCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select Record_Count as totalRecords , Null_Value as failedRecords, Null_Threshold as resultThreshold, Status as resultStatus, 0.0 as resultDQI "
					+ "from DATA_QUALITY_NullCheck_Summary where idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForNumericalStatisticsCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select count(*) as failedRecords, case when count(*)>0 then 'failed' else 'passed' end as resultStatus, max(NumSDThreshold) as resultThreshold "
					+ " from DATA_QUALITY_Column_Summary where (NumSDStatus='failed' or NumMeanStatus='failed') and idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForPatternCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select  Total_Failed_Records as failedRecords, Pattern_Threshold as resultThreshold, Status as resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_Unmatched_Pattern_Data where idApp=? and Date=? and Run=? and Col_Name=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}
	
	public Map<String, Object> getResultsForDefaultPatternCheck(Map<String, Object> oRuleCatalogRow) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select sum(Total_Failed_Records) as failedRecords, max(Pattern_Threshold) as ResultThreshold, "
					+ "case when sum(Total_Failed_Records) > 0 then  'failed' else 'passed' end  as resultStatus, 0.0 as resultDQI  "
					+ "from DATA_QUALITY_Unmatched_Default_Pattern_Data where idApp=? and Date=? and Run=? and Col_Name=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForRecordAnomalyCheck(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String columnName = (String) oRuleCatalogRow.get("columnName");

			String sql = "select count(*) AS failedRecords, case when count(*) > 0 then 'failed' else 'passed' end AS resultStatus "
					+ " from DATA_QUALITY_Record_Anomaly where idApp=? and Date=? and Run=? and ColName=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, columnName);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForTimelinessCheck(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");

			String sql = "select sum(TotalFailedCount) as failedRecords, '0.0' as resultThreshold, case when count(TotalFailedCount) > 0 then 'failed' else 'passed' end AS resultStatus, 0.0 as resultDQI "
					+ " from DATA_QUALITY_timeliness_check where idApp=? and Date=? and Run=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForDuplicateCheckPrimaryFields(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");

			String sql = "select Duplicate as failedRecords, Threshold as resultThreshold, Status AS resultStatus, Percentage as resultDQI "
					+ " from DATA_QUALITY_Transaction_Summary where Type='identity' and idApp=? and Date=? and Run=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForDuplicateCheckSelectedFields(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");

			String sql = "select Duplicate as failedRecords, Threshold as resultThreshold, Status AS resultStatus, Percentage as resultDQI "
					+ " from DATA_QUALITY_Duplicate_Check_Summary where Type='all' and idApp=? and Date=? and Run=? limit 1";
			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run);

		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> getResultsForRules(Map<String, Object> oRuleCatalogRow) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			long idApp = (long) oRuleCatalogRow.get("idApp");
			String execDate = (String) oRuleCatalogRow.get("resultRunDate");
			long run = (long) oRuleCatalogRow.get("resultRunNo");
			String ruleName = (String) oRuleCatalogRow.get("ruleName");
			String sRuleType = ((String) oRuleCatalogRow.get("ruleType")).toLowerCase();

			String sql = "";
			// For global rules
			if (sRuleType.indexOf("global") >= 0) {
				sql = "select totalFailed as failedRecords, ruleThreshold  as resultThreshold, status as resultStatus, null as resultDQI "
						+ " from DATA_QUALITY_GlobalRules where idApp=? and Date=? and Run=? and lower(ruleName) = lower(?) limit 1";

			}
			// For custom rules
			else {
				sql = "select totalFailed as failedRecords, ruleThreshold as resultThreshold, status as resultStatus, null as resultDQI "
						+ " from DATA_QUALITY_Rules where idApp=? and Date=? and Run=? and lower(ruleName) = lower(?) limit 1";
			}

			resultMap = jdbcTemplate1.queryForMap(sql, idApp, execDate, run, ruleName);
		} catch (EmptyResultDataAccessException e) {
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public long getValidationTotalRecordsByDateRun(long idApp, String executionDate, long run) {
		long totalRecords = 0l;
		try {
			String sql = "SELECT  RecordCount as TotalRecords from DATA_QUALITY_Transactionset_sum_A1  where idApp=? and Date=? and Run=? limit 1";
			Long count = jdbcTemplate1.queryForObject(sql, Long.class, idApp, executionDate, run);
			if (count != null)
				totalRecords = count;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return totalRecords;
	}

	@Override
	public boolean isDateRunValid(long idApp, String executionDate, long run) {
		boolean status = false;
		try {
			String sql = "SELECT count(*) from DATA_QUALITY_Transactionset_sum_A1  where idApp=? and Date=? and Run=?";
			long count = jdbcTemplate1.queryForObject(sql, Long.class, idApp, executionDate, run);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}
}
