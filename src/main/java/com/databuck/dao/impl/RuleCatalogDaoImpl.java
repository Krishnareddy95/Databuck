package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.ListApplications;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.RuleCatalogCheckSpecification;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.databuck.bean.ListDfTranRule;

@Repository
public class RuleCatalogDaoImpl implements RuleCatalogDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	public Properties appDbConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(RuleCatalogDaoImpl.class);

	@Override
	public List<Map<String, Object>> getRCApprovalOptionsList() {
		try {
			String sql = " select b.element_reference, b.row_id, b.element_text, b.is_default, b.position from app_option_list a, app_option_list_elements b where b.elements2app_list = a.row_id and b.active > 0 and a.list_reference='DQ_RULE_CATALOG_STATUS' order by position";
			return jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
		    	LOG.error(e.getMessage());LOG.error(e.getMessage());e.printStackTrace();
		}
		return null;
	}

	@Override
	public void autoApproveThresholdChangesInActualRuleCatalog(long idApp, String checkName, String column_or_rule_name,
			double updated_Threshold, String rule_expression, String defaultPattern) {
		String sql = "";
		int count = 0;
		try {
			if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {
				sql = "update listApplicationsRulesCatalog set threshold_value =? where  rule_name=? and rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, column_or_rule_name, checkName, idApp);
			} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {
				sql = "update listApplicationsRulesCatalog set threshold_value =? where rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, checkName, idApp);
			} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)) {
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					sql = "update listApplicationsRulesCatalog set rule_expression =  case when rule_expression is null or trim(rule_expression) = '' then  '"
							+ rule_expression + "'" + " else concat(rule_expression ,'" + rule_expression
							+ "') end where idApp=? and  rule_type=? and column_name=? and"
							+ " COALESCE(rule_expression,'') not like '%" + defaultPattern + "per:%'";
				else
					sql = "update listApplicationsRulesCatalog set rule_expression =  case when rule_expression is null or trim(rule_expression) = '' then  '"
							+ rule_expression + "'" + " else concat(rule_expression ,'" + rule_expression
							+ "') end where idApp=? and  rule_type=? and column_name=? and"
							+ " ifnull(rule_expression,'') not like '%" + defaultPattern + "per:%'";
				count = jdbcTemplate.update(sql, idApp, checkName, column_or_rule_name);
				LOG.debug("sql for pattern check listApplicationsRulesCatalog= " + sql);
			} else {
				sql = "update listApplicationsRulesCatalog set threshold_value =? where column_name=? and rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, column_or_rule_name, checkName, idApp);
				LOG.debug("sql all other listApplicationsRulesCatalog= " + sql);
			}
			LOG.debug("sql listApplicationsRulesCatalog= " + sql);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void autoApproveThresholdChangesInStagingRuleCatalog(long idApp, String checkName,
			String column_or_rule_name, double updated_Threshold, String rule_expression, String defaultPattern) {
		String sql = "";
		int count = 0;
		try {
			if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {
				sql = "update staging_listApplicationsRulesCatalog set threshold_value =? where  rule_name=? and rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, column_or_rule_name, checkName, idApp);
			} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {
				sql = "update staging_listApplicationsRulesCatalog set threshold_value =? where rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, checkName, idApp);

			} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)) {
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					sql = "update staging_listApplicationsRulesCatalog set rule_expression =  case when rule_expression is null or trim(rule_expression) = '' then  '"
							+ rule_expression + "'" + " else concat(rule_expression ,'" + rule_expression
							+ "') end where idApp=? and  rule_type=? and column_name=? and"
							+ " COALESCE(rule_expression,'') not like '%" + defaultPattern + "per:%'";
				else
					sql = "update staging_listApplicationsRulesCatalog set rule_expression =  case when rule_expression is null or trim(rule_expression) = '' then  '"
							+ rule_expression + "'" + " else concat(rule_expression ,'" + rule_expression
							+ "') end where idApp=? and  rule_type=? and column_name=? and"
							+ " ifnull(rule_expression,'') not like '%" + defaultPattern + "per:%'";
				count = jdbcTemplate.update(sql, idApp, checkName, column_or_rule_name);
				LOG.debug("pattern check staging_listApplicationsRulesCatalog= " + sql);
			} else {
				sql = "update staging_listApplicationsRulesCatalog set threshold_value =? where column_name=? and rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, column_or_rule_name, checkName, idApp);
			}
			LOG.debug("sql all other staging_listApplicationsRulesCatalog= " + sql);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public boolean editRuleInRuleCatalog(RuleCatalog ruleCatalog) {
		boolean status = false;
		try {
			String reviewComments = ruleCatalog.getReviewComments();
			reviewComments = (reviewComments != null) ? reviewComments.trim() : "";

			String columnName = ruleCatalog.getColumnName();
			columnName = (columnName != null) ? columnName.trim() : "";

			String ruleDescription = ruleCatalog.getRuleDescription();
			ruleDescription = (ruleDescription != null) ? ruleDescription.trim() : "";

			String ruleExpression = ruleCatalog.getRuleExpression();
			ruleExpression = (ruleExpression != null) ? ruleExpression.trim() : "";

			String matchingRules = ruleCatalog.getMatchingRules();
			matchingRules = (matchingRules != null) ? matchingRules.trim() : "";

			String filterCondition = ruleCatalog.getFilterCondition();
			filterCondition = (filterCondition != null) ? filterCondition.trim() : "";

			String rightTemplateFilterCondition = ruleCatalog.getRightTemplateFilterCondition();
			rightTemplateFilterCondition = (rightTemplateFilterCondition != null) ? rightTemplateFilterCondition.trim()
					: "";

			// Delete rule business attribute linking and insert new linking.
			String deleteSql = "delete from rule_business_attribute_mapping where rule_id=? and idApp=?";
			jdbcTemplate.update(deleteSql, ruleCatalog.getRuleReference(), ruleCatalog.getIdApp());

			if (ruleCatalog.getBusinessAttributeId() != null && !ruleCatalog.getBusinessAttributeId().trim().isEmpty()) {
				LOG.debug("=====> Bussiness Attribute Mapping");
				List<String> businessAttributeIdList = Arrays.asList(ruleCatalog.getBusinessAttributeId().split(","));
				for (String businessAttributeId : businessAttributeIdList) {
					String mappingSql = "insert into rule_business_attribute_mapping (business_attribute_id, rule_id, idApp) values (?,?,?)";
					jdbcTemplate.update(mappingSql, Integer.parseInt(businessAttributeId), ruleCatalog.getRuleReference(), ruleCatalog.getIdApp());
				}
			}

			String sql = "update listApplicationsRulesCatalog set threshold_value =?, defect_code = ?, dimension_id = ?, review_date = now() , review_by = ?, review_comments = ?, rule_description = ?, column_name=?, rule_expression = ?, matching_rules = ?, filter_condition=?, right_template_filter_condition=?, null_filter_columns=?  where row_id=?";
			int count = jdbcTemplate.update(sql, ruleCatalog.getThreshold(), ruleCatalog.getDefectCode(),
					ruleCatalog.getDimensionId(), ruleCatalog.getReviewBy(), reviewComments, ruleDescription,
					columnName, ruleExpression, matchingRules, filterCondition, rightTemplateFilterCondition,
					columnName, ruleCatalog.getRowId());
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean editRuleInRuleCatalogStaging(RuleCatalog ruleCatalog) {
		boolean status = false;
		try {

			String reviewComments = ruleCatalog.getReviewComments();
			reviewComments = (reviewComments != null) ? reviewComments.trim() : "";

			String columnName = ruleCatalog.getColumnName();
			columnName = (columnName != null) ? columnName.trim() : "";

			String ruleDescription = ruleCatalog.getRuleDescription();
			ruleDescription = (ruleDescription != null) ? ruleDescription.trim() : "";

			String filterCondition = ruleCatalog.getFilterCondition();
			filterCondition = (filterCondition != null) ? filterCondition.trim() : "";

			String ruleExpression = ruleCatalog.getRuleExpression();
			ruleExpression = (ruleExpression != null) ? ruleExpression.trim() : "";

			String matchingRules = ruleCatalog.getMatchingRules();
			matchingRules = (matchingRules != null) ? matchingRules.trim() : "";

			String sql = "update staging_listApplicationsRulesCatalog set threshold_value =?, defect_code = ?, dimension_id = ?, review_date = now() , review_by = ?, review_comments = ?, rule_description = ?, column_name=?, rule_expression = ?, matching_rules = ?,filter_condition=?,null_filter_columns=?  where row_id=?";
			int count = jdbcTemplate.update(sql, ruleCatalog.getThreshold(), ruleCatalog.getDefectCode(),
					ruleCatalog.getDimensionId(), ruleCatalog.getReviewBy(), reviewComments, ruleDescription,
					columnName, ruleExpression, matchingRules, filterCondition, columnName, ruleCatalog.getRowId());
			if (count > 0)
				status = true;

			String ruleName= ruleCatalog.getRuleName();
			long idApp= ruleCatalog.getIdApp();

			// Delete rule business attribute linking and insert new linking.
			String deleteSql = "delete from rule_business_attribute_mapping where rule_id=? and idApp=?";
			jdbcTemplate.update(deleteSql, ruleCatalog.getRuleReference(), ruleCatalog.getIdApp());

			if (ruleCatalog.getBusinessAttributeId() != null && !ruleCatalog.getBusinessAttributeId().trim().isEmpty()) {
				LOG.debug("=====> Bussiness Attribute Mapping");
				List<String> businessAttributeIdList = Arrays.asList(ruleCatalog.getBusinessAttributeId().split(","));
				for (String businessAttributeId : businessAttributeIdList) {
					String mappingSql = "insert into rule_business_attribute_mapping (business_attribute_id, rule_id, idApp) values (?,?,?)";
					jdbcTemplate.update(mappingSql, Integer.parseInt(businessAttributeId), ruleCatalog.getRuleReference(), ruleCatalog.getIdApp());
				}
			}

			sql = "update listApplicationsRulesCatalog set defect_code = ?, review_comments = ?, rule_description = ? where idApp=? and rule_name=?";
			count = jdbcTemplate.update(sql, ruleCatalog.getDefectCode(),reviewComments, ruleDescription,idApp,ruleName);
			if (count > 0)
				LOG.debug("Defect code/Review Comment/Rule Description are updated in actual rule catalog");
			else
				LOG.debug("Could not update Defect code/Review Comment/Rule Description in actual rule catalog");
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean updateValidationRuleCatalogStatus(long idApp, int approve_status, String approve_comments,
			Long reviewedByUser, String approverName) {
		boolean status = false;
		try {
			String sql = "update listApplications set approve_status = ?, approve_comments = ? ,approve_date = now() ,approve_by = ?, approver_name= ? where idApp = ?";
			int count = jdbcTemplate.update(sql, approve_status, approve_comments, reviewedByUser, approverName, idApp);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean updateValidationStagingRuleCatalogStatus(long idApp, int approve_status, String approve_comments,
			Long reviewedByUser, String approverName) {
		boolean status = false;
		try {
			String sql = "update listApplications set staging_approve_status = ?, approve_comments = ? ,approve_date = now() ,approve_by = ?, approver_name= ? where idApp = ?";
			jdbcTemplate.update(sql, approve_status, approve_comments, reviewedByUser, approverName, idApp);

			sql = "update staging_listApplications set staging_approve_status = ?, approve_comments = ? ,approve_date = now() ,approve_by = ?, approver_name= ? where idApp = ?";
			int count = jdbcTemplate.update(sql, approve_status, approve_comments, reviewedByUser, approverName, idApp);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public Map<String, Long> getDimensionsList() {
		Map<String, Long> dimensionMap = new HashMap<String, Long>();

		try {
			String sql = "select idDimension as DimensionId, dimensionName as DimensionName from dimension";
			List<Map<String, Object>> dimensionList = jdbcTemplate.queryForList(sql);

			if (dimensionList != null) {
				for (Map<String, Object> dimensionRow : dimensionList) {
					String dimensionName = (String) dimensionRow.get("DimensionName");
					Object dimensionId = dimensionRow.get("DimensionId");
					if (dimensionId != null && dimensionName != null && !dimensionName.trim().isEmpty()) {
						dimensionMap.put(dimensionName.toLowerCase(), Long.parseLong(dimensionId.toString()));
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return dimensionMap;
	}

	@Override
	public void updateModifiedRulesInRuleCatalog(long idApp, RuleCatalog current_rc_check,
			RuleCatalog modified_rc_check) {
		try {
			// Get the RowId and RuleCategory from current check object
			long rowId = current_rc_check.getRowId();
			String ruleCategory = current_rc_check.getRuleCategory();
			String ruleType = current_rc_check.getRuleType();

			// Get the new values for modified check Object
			String ruleDescription = modified_rc_check.getRuleDescription();
			String ruleExpression = modified_rc_check.getRuleExpression();
			double threshold_value = modified_rc_check.getThreshold();
			String matchingRules = modified_rc_check.getMatchingRules();
			String filterCondition = modified_rc_check.getFilterCondition();
			String rightTemplateFilterCondition = modified_rc_check.getRightTemplateFilterCondition();

			if (filterCondition == null)
				filterCondition = "";

			if (rightTemplateFilterCondition == null)
				rightTemplateFilterCondition = "";

			long dimensionId = modified_rc_check.getDimensionId();

			String columnName = modified_rc_check.getColumnName();
			if (columnName == null || columnName.trim().isEmpty())
				columnName = "";

			String sql = "";
			if (ruleCategory.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)) {
				sql = "update listApplicationsRulesCatalog set threshold_value=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, threshold_value, idApp, rowId);

			}

			else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {
				sql = "update listApplicationsRulesCatalog set rule_description=?, threshold_value=?, rule_expression=?, matching_rules=?, column_name=?, dimension_id=?, null_filter_columns=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, ruleDescription, threshold_value, ruleExpression, matchingRules, columnName,
						dimensionId, columnName, idApp, rowId);
			}

			else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)) {
				sql = "update listApplicationsRulesCatalog set rule_expression=?,threshold_value=?, matching_rules=?, filter_condition=?, right_template_filter_condition=?, column_name=?, null_filter_columns=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, ruleExpression, threshold_value, matchingRules, filterCondition,
						rightTemplateFilterCondition, columnName, columnName, idApp, rowId);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void updateModifiedRulesInRuleCatalogStaging(long idApp, RuleCatalog current_rc_check,
			RuleCatalog modified_rc_check) {
		try {
			// Get the RowId and RuleCategory from current check object
			long rowId = current_rc_check.getRowId();
			String ruleCategory = current_rc_check.getRuleCategory();
			String ruleType = current_rc_check.getRuleType();

			// Get the new values for modified check Object
			String ruleDescription = modified_rc_check.getRuleDescription();
			String ruleExpression = modified_rc_check.getRuleExpression();
			String matchingRules = modified_rc_check.getMatchingRules();
			String filterCondition = modified_rc_check.getFilterCondition();
			String rightTemplateFilterCondition = modified_rc_check.getRightTemplateFilterCondition();

			if (rightTemplateFilterCondition == null)
				rightTemplateFilterCondition = "";

			double threshold_value = modified_rc_check.getThreshold();
			long dimensionId = modified_rc_check.getDimensionId();

			String columnName = modified_rc_check.getColumnName();
			if (columnName == null || columnName.trim().isEmpty())
				columnName = "";

			String sql = "";
			if (ruleCategory.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)) {
				sql = "update staging_listApplicationsRulesCatalog set threshold_value=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, threshold_value, idApp, rowId);

			} else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {
				sql = "update staging_listApplicationsRulesCatalog set rule_description=?, threshold_value=?, rule_expression=?, matching_rules=?, column_name=?, dimension_id=?, null_filter_columns=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, ruleDescription, threshold_value, ruleExpression, matchingRules, columnName,
						dimensionId, columnName, idApp, rowId);

			} else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)) {
				sql = "update staging_listApplicationsRulesCatalog set rule_expression=?, threshold_value=?, matching_rules=?,filter_condition=?, right_template_filter_condition=?, column_name=?, null_filter_columns=? where idApp=? and row_id=?";
				jdbcTemplate.update(sql, ruleExpression, threshold_value, matchingRules, filterCondition,
						rightTemplateFilterCondition, columnName, columnName, idApp, rowId);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public List<RuleCatalog> getRulesFromRuleCatalog(long idApp) {
		List<RuleCatalog> ruleCatalogList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select lc.*, dm.dimensionName, gtm.ruleTags, gba.business_attributes, gba.business_attribute_id from listApplicationsRulesCatalog lc  "
						+ " left outer join dimension dm on lc.dimension_id = dm.idDimension "
						+ " left outer join (select rta.rule_id, array_agg(tg.tag_name) as RuleTags, rta.idApp from rule_tag_mapping rta join databuck_tags tg on tg.tag_id = rta.tag_id group by rta.idApp,rule_id) gtm on gtm.rule_id = lc.rule_reference and gtm.idApp=lc.idApp "
						+ " left outer join (select rba.rule_id, string_agg(dc.defect_code::text, ',') as business_attributes, string_agg(rba.business_attribute_id::text, ',') as business_attribute_id, rba.idApp from rule_business_attribute_mapping rba join defect_codes dc on dc.row_id = rba.business_attribute_id group by rba.idApp,rba.rule_id) gba ON gba.rule_id = lc.rule_reference and gba.idApp=lc.idApp"
						+ " where lc.idApp=?";
			} else {
				sql = "select lc.*, dm.dimensionName, gtm.ruleTags, gba.business_attributes, gba.business_attribute_id from listApplicationsRulesCatalog lc  "
						+ " left outer join dimension dm on lc.dimension_id = dm.idDimension "
						+ " left outer join (select rta.rule_id, group_concat(tg.tag_name) as RuleTags, rta.idApp from rule_tag_mapping rta join databuck_tags tg on tg.tag_id = rta.tag_id group by rta.idApp,rule_id) gtm on gtm.rule_id = lc.rule_reference and gtm.idApp=lc.idApp "
						+ " left outer join (select rba.rule_id, group_concat(dc.defect_code) as business_attributes, group_concat(rba.business_attribute_id) as business_attribute_id, rba.idApp from rule_business_attribute_mapping rba join defect_codes dc on dc.row_id = rba.business_attribute_id group by rba.idApp,rba.rule_id) gba ON gba.rule_id = lc.rule_reference and gba.idApp=lc.idApp"
						+ " where lc.idApp=?";
			}

			LOG.debug(sql);
			ruleCatalogList = jdbcTemplate.query(sql, new RowMapper<RuleCatalog>() {

				@Override
				public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
					DecimalFormat numberFormat = new DecimalFormat("#0.00");
					RuleCatalog rc_checkDetails = new RuleCatalog();
					rc_checkDetails.setRowId(rs.getLong("row_id"));
					rc_checkDetails.setIdApp(idApp);
					rc_checkDetails.setRuleReference(rs.getLong("rule_reference"));
					rc_checkDetails.setRuleType(rs.getString("rule_type"));
					rc_checkDetails.setColumnName(rs.getString("column_name"));
					rc_checkDetails.setRuleName(rs.getString("rule_name"));
					rc_checkDetails.setRuleCategory(rs.getString("rule_category"));
					rc_checkDetails.setRuleExpression(rs.getString("rule_expression"));
					rc_checkDetails.setMatchingRules(rs.getString("matching_rules"));
					rc_checkDetails.setCustomOrGlobalRuleId(rs.getLong("custom_or_global_ruleId"));
					rc_checkDetails.setRuleCode(rs.getString("rule_code"));
					rc_checkDetails.setDefectCode(rs.getString("defect_code"));
					rc_checkDetails
							.setThreshold(Double.parseDouble(numberFormat.format(rs.getDouble("threshold_value"))));
					rc_checkDetails.setReviewBy(rs.getString("review_by"));
					rc_checkDetails.setReviewDate(rs.getString("review_date"));
					rc_checkDetails.setReviewComments(rs.getString("review_comments"));
					rc_checkDetails.setDimensionId(rs.getLong("dimension_id"));
					rc_checkDetails.setDimensionName(rs.getString("dimensionName"));
					rc_checkDetails.setAgingCheckEnabled(rs.getString("agingCheckEnabled"));
					rc_checkDetails.setRuleDescription(rs.getString("rule_description"));
					rc_checkDetails.setRuleTags(rs.getString("ruleTags"));
					rc_checkDetails.setCustomOrGlobalRuleType(rs.getString("custom_or_global_rule_type"));
					rc_checkDetails.setFilterCondition(rs.getString("filter_condition"));
					rc_checkDetails.setRightTemplateFilterCondition(
							rs.getString("right_template_filter_condition") == null ? ""
									: rs.getString("right_template_filter_condition"));
					rc_checkDetails.setBusinessAttributeId(rs.getString("business_attribute_id") == null ? ""
							: rs.getString("business_attribute_id"));
					rc_checkDetails.setBusinessAttributes(rs.getString("business_attributes") == null ? "" : rs.getString("business_attributes"));
					rc_checkDetails.setNullFilterColumn(rs.getString("null_filter_columns"));
					return rc_checkDetails;
				}

			}, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return ruleCatalogList;
	}

	@Override
	public List<RuleCatalog> getRulesFromRuleCatalogStaging(long idApp) {
		List<RuleCatalog> ruleCatalogList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select lc.*, dm.dimensionName, gtm.ruleTags, gba.business_attributes, gba.business_attribute_id  from staging_listApplicationsRulesCatalog lc"
						+ " left outer join dimension dm on lc.dimension_id = dm.idDimension  "
						+ " left outer join (select rta.rule_id, array_agg(tg.tag_name) as RuleTags, rta.idApp from rule_tag_mapping rta join databuck_tags tg on tg.tag_id = rta.tag_id group by rta.idApp,rule_id) gtm on gtm.rule_id = lc.rule_reference and gtm.idApp=lc.idApp "
						+ " left outer join (select rba.rule_id ,string_agg(dc.defect_code::text, ',') as business_attributes ,string_agg(rba.business_attribute_id::text, ',') as business_attribute_id, rba.idApp from rule_business_attribute_mapping rba join defect_codes dc on dc.row_id = rba.business_attribute_id group by rba.idApp,rba.rule_id) gba ON gba.rule_id = lc.rule_reference and gba.idApp=lc.idApp "
						+ " where lc.idApp=?";
			} else {
				sql = "select lc.*, dm.dimensionName, gtm.ruleTags, gba.business_attributes, gba.business_attribute_id  from staging_listApplicationsRulesCatalog lc "
						+ " left outer join dimension dm on lc.dimension_id = dm.idDimension  "
						+ " left outer join (select rta.rule_id, group_concat(tg.tag_name) as RuleTags, rta.idApp from rule_tag_mapping rta join databuck_tags tg on tg.tag_id = rta.tag_id group by rta.idApp,rule_id) gtm on gtm.rule_id = lc.rule_reference and gtm.idApp=lc.idApp "
						+ " left outer join (select rba.rule_id,group_concat(dc.defect_code) as business_attributes ,group_concat(rba.business_attribute_id) as business_attribute_id, rba.idApp from rule_business_attribute_mapping rba join defect_codes dc on dc.row_id = rba.business_attribute_id group by rba.idApp,rba.rule_id) gba ON gba.rule_id = lc.rule_reference and gba.idApp=lc.idApp "
						+ " where lc.idApp=?";
			}
			LOG.debug(sql);
			ruleCatalogList = jdbcTemplate.query(sql, new RowMapper<RuleCatalog>() {

				@Override
				public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
					RuleCatalog rc_checkDetails = new RuleCatalog();
					DecimalFormat numberFormat = new DecimalFormat("#0.00");
					rc_checkDetails.setRowId(rs.getLong("row_id"));
					rc_checkDetails.setIdApp(idApp);
					rc_checkDetails.setRuleReference(rs.getLong("rule_reference"));
					rc_checkDetails.setRuleType(rs.getString("rule_type"));
					rc_checkDetails.setColumnName(rs.getString("column_name"));
					rc_checkDetails.setRuleName(rs.getString("rule_name"));
					rc_checkDetails.setRuleCategory(rs.getString("rule_category"));
					rc_checkDetails.setRuleExpression(rs.getString("rule_expression"));
					rc_checkDetails.setMatchingRules(rs.getString("matching_rules"));
					rc_checkDetails.setCustomOrGlobalRuleId(rs.getLong("custom_or_global_ruleId"));
					rc_checkDetails.setRuleCode(rs.getString("rule_code"));
					rc_checkDetails.setDefectCode(rs.getString("defect_code"));
					rc_checkDetails
							.setThreshold(Double.parseDouble(numberFormat.format(rs.getDouble("threshold_value"))));
					rc_checkDetails.setReviewBy(rs.getString("review_by"));
					rc_checkDetails.setReviewDate(rs.getString("review_date"));
					rc_checkDetails.setReviewComments(rs.getString("review_comments"));
					rc_checkDetails.setDimensionId(rs.getLong("dimension_id"));
					rc_checkDetails.setDimensionName(rs.getString("dimensionName"));
					rc_checkDetails.setAgingCheckEnabled(rs.getString("agingCheckEnabled"));
					rc_checkDetails.setRuleDescription(rs.getString("rule_description"));
					rc_checkDetails.setRuleTags(rs.getString("ruleTags"));
					rc_checkDetails.setCustomOrGlobalRuleType(rs.getString("custom_or_global_rule_type"));
					rc_checkDetails.setFilterCondition(rs.getString("filter_condition"));
					rc_checkDetails.setRightTemplateFilterCondition(
							rs.getString("right_template_filter_condition") == null ? ""
									: rs.getString("right_template_filter_condition"));
					rc_checkDetails.setBusinessAttributeId(rs.getString("business_attribute_id") == null ? ""
							: rs.getString("business_attribute_id"));
					rc_checkDetails.setBusinessAttributes(rs.getString("business_attributes") == null ? "" : rs.getString("business_attributes"));
					rc_checkDetails.setNullFilterColumn(rs.getString("null_filter_columns"));
					return rc_checkDetails;
				}

			}, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return ruleCatalogList;
	}

	@Override
	public void insertRuleCatalogRecords(List<RuleCatalog> rulesList, long idApp) {
		try {
			String c_sql = "select case when max(rule_reference) > 0 then max(rule_reference) else 0 end from listApplicationsRulesCatalog where idApp=?";
			Long nIndex = jdbcTemplate.queryForObject(c_sql, Long.class, idApp);

			for (RuleCatalog rc_checkDetails : rulesList) {
				++nIndex;

				LOG.debug("insert into listApplicationsRulesCatalog:ruleName="+rc_checkDetails.getRuleName());

				String columnName = (rc_checkDetails.getColumnName() != null) ? rc_checkDetails.getColumnName() : "";
				String rightTemplateFilterCondition = (rc_checkDetails.getRightTemplateFilterCondition() != null)
						? rc_checkDetails.getRightTemplateFilterCondition().trim()
						: "";

				String sql = "insert into listApplicationsRulesCatalog (idApp, rule_reference, rule_code, defect_code, rule_type, column_name, rule_name, rule_category, rule_expression, matching_rules, custom_or_global_ruleId,threshold_value,dimension_id,rule_description,activeFlag,custom_or_global_rule_type,filter_condition,right_template_filter_condition,null_filter_columns) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				jdbcTemplate.update(sql,
						new Object[] { rc_checkDetails.getIdApp(), nIndex, "", "", rc_checkDetails.getRuleType(),
								columnName, rc_checkDetails.getRuleName(), rc_checkDetails.getRuleCategory(),
								rc_checkDetails.getRuleExpression(), rc_checkDetails.getMatchingRules(),
								rc_checkDetails.getCustomOrGlobalRuleId(), rc_checkDetails.getThreshold(),
								rc_checkDetails.getDimensionId(), rc_checkDetails.getRuleDescription(), 1,
								rc_checkDetails.getCustomOrGlobalRuleType(), rc_checkDetails.getFilterCondition(),
								rightTemplateFilterCondition, columnName });
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void insertRuleCatalogRecordsToStaging(List<RuleCatalog> rulesList, long idApp) {
		try {
			String c_sql = "select case when max(rule_reference) > 0 then max(rule_reference) else 0 end from staging_listApplicationsRulesCatalog where idApp=?";
			Long nIndex = jdbcTemplate.queryForObject(c_sql, Long.class, idApp);

			for (RuleCatalog rc_checkDetails : rulesList) {
				++nIndex;

				String sql = "insert into staging_listApplicationsRulesCatalog (idApp, rule_reference, rule_code, defect_code, rule_type, column_name, rule_name, rule_category, rule_expression, matching_rules, custom_or_global_ruleId,threshold_value,dimension_id,rule_description,activeFlag,custom_or_global_rule_type,filter_condition,right_template_filter_condition,null_filter_columns) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				jdbcTemplate.update(sql, new Object[] { rc_checkDetails.getIdApp(), nIndex, "", "",
						rc_checkDetails.getRuleType(), rc_checkDetails.getColumnName(), rc_checkDetails.getRuleName(),
						rc_checkDetails.getRuleCategory(), rc_checkDetails.getRuleExpression(),
						rc_checkDetails.getMatchingRules(), rc_checkDetails.getCustomOrGlobalRuleId(),
						rc_checkDetails.getThreshold(), rc_checkDetails.getDimensionId(),
						rc_checkDetails.getRuleDescription(), 1, rc_checkDetails.getCustomOrGlobalRuleType(),
						rc_checkDetails.getFilterCondition(), rc_checkDetails.getRightTemplateFilterCondition(),rc_checkDetails.getColumnName()});
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void deletesRecordsFromRuleCatalog(List<RuleCatalog> rulesList, long idApp) {
		try {
			for (RuleCatalog rc_checkDetails : rulesList) {
				long rowId = rc_checkDetails.getRowId();
				String sql = "delete from listApplicationsRulesCatalog where row_id=? and idApp=?";
				LOG.debug("sql:"+sql+",rowid,idApp:"+rowId+","+idApp);
				jdbcTemplate.update(sql, rowId, idApp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void deleteTagMappingsForDeletedRules(List<RuleCatalog> rulesList, long idApp) {
		try {
			for (RuleCatalog rc_checkDetails : rulesList) {
				long rowId = rc_checkDetails.getRuleReference();
				String sql = "delete from rule_tag_mapping where rule_id=? and idApp=?";
				LOG.debug("sql:"+sql+",rowid,idApp:"+rowId+","+idApp);
				jdbcTemplate.update(sql, rowId, idApp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void deletesRecordsFromRuleCatalogStaging(List<RuleCatalog> rulesList, long idApp) {
		try {
			for (RuleCatalog rc_checkDetails : rulesList) {
				long rowId = rc_checkDetails.getRowId();
				String sql = "delete from staging_listApplicationsRulesCatalog where row_id=? and idApp=?";
				jdbcTemplate.update(sql, rowId, idApp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void updateRuleCatalogStatusToCreated(long idApp) {
		try {
			int nCreateStatusRowId = validationcheckdao.getRuleCatalogCreateStatus();
			if (nCreateStatusRowId > -1) {
				String sUpdateSql = "update listApplications set approve_status = ? where idApp = ?";
				jdbcTemplate.update(sUpdateSql, nCreateStatusRowId, idApp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void updateStagingRuleCatalogStatusToCreated(long idApp) {
		try {
			int nCreateStatusRowId = validationcheckdao.getRuleCatalogCreateStatus();
			if (nCreateStatusRowId > -1) {
				String sUpdateSql = "update listApplications set staging_approve_status = ? where idApp = ?";
				jdbcTemplate.update(sUpdateSql, nCreateStatusRowId, idApp);

				sUpdateSql = "update staging_listApplications set staging_approve_status = ? where idApp = ?";
				jdbcTemplate.update(sUpdateSql, nCreateStatusRowId, idApp);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public List<RuleCatalog> getChecksDataForColumnCheck(RuleCatalogCheckSpecification oCheckSpec,
			ListApplications oListApplication) {

		List<RuleCatalog> ruleCatalogList = null;

		try {
			long idApp = oListApplication.getIdApp();
			String ruleType = oCheckSpec.getCheckDisplayName();
			String thresholdColumn = oCheckSpec.getTemplateCheckThresholdColumn();
			String checkEnabledColumn = oCheckSpec.getTemplateCheckEnabledColumn();
			String ruleCategory = DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY;
			long idData = oListApplication.getIdData();
			long dimensionId = oCheckSpec.getDimensionId();
			String checkDescription = oCheckSpec.getCheckDescription();
			double defaultPatternThreshold = Double
					.parseDouble(appDbConnectionProperties.getProperty("default.pattern.threshold"));

			// Query compatibility changes for both POSTGRES and MYSQL
			String ifnull_function = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					? "COALESCE"
					: "ifnull";
			String sql = "select displayName as ColumnName, " + ifnull_function + "(" + thresholdColumn
					+ ",0.0) as Threshold,'' as ruleExpression from listDataDefinition where idData = " + idData
					+ " and   " + checkEnabledColumn + " = 'Y'";

			if (checkEnabledColumn.equalsIgnoreCase("defaultPatternCheck")) {
				sql = "select displayName as ColumnName," + ifnull_function + "(" + defaultPatternThreshold
						+ ",0.0) as Threshold, defaultPatterns as ruleExpression  from listDataDefinition where idData = "
						+ idData + " and   " + checkEnabledColumn + " = 'Y'";
			}

			ruleCatalogList = jdbcTemplate.query(sql, new RowMapper<RuleCatalog>() {

				@Override
				public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
					// Get column name
					String columnName = rs.getString("ColumnName");

					// Prepare rule name
					String ruleName = ruleCategory.replace(" ", "") + "_" + ruleType.replace(" ", "") + "_"
							+ columnName.replace(" ", "");

					// Prepare ruleDescription
					String ruleDescription = checkDescription;

					RuleCatalog rc_checkDetails = new RuleCatalog();
					rc_checkDetails.setRowId(0l);
					rc_checkDetails.setIdApp(idApp);
					rc_checkDetails.setRuleReference(0l);
					rc_checkDetails.setRuleType(ruleType);
					rc_checkDetails.setColumnName(columnName);
					rc_checkDetails.setRuleName(ruleName);
					rc_checkDetails.setRuleCategory(ruleCategory);
					rc_checkDetails.setRuleExpression(rs.getString("ruleExpression"));
					rc_checkDetails.setRuleCode("");
					rc_checkDetails.setDefectCode("");
					rc_checkDetails.setThreshold(rs.getDouble("Threshold"));
					rc_checkDetails.setDimensionId(dimensionId);
					rc_checkDetails.setReviewBy("");
					rc_checkDetails.setReviewDate(null);
					rc_checkDetails.setReviewComments("");
					rc_checkDetails.setRuleDescription(ruleDescription);
					return rc_checkDetails;
				}

			});
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return ruleCatalogList;
	}

	@Override
	public List<RuleCatalog> getChecksDataForRulesCheck(RuleCatalogCheckSpecification oCheckSpec,
			ListApplications oListApplication) {

		List<RuleCatalog> ruleCatalogList = null;

		try {
			long idApp = oListApplication.getIdApp();
			long idData = oListApplication.getIdData();
			long dimensionId = oCheckSpec.getDimensionId();

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "(select '" + DatabuckConstants.RULE_TYPE_GLOBAL_RULE
						+ "' as RuleType,c.ruleName as RuleName, a.anchorColumns, a.ruleId AS customOrGlobalRuleId, a.ruleExpression AS RuleExpression , a.matchingRules AS matchingRules, a.filter_condition as filterCondition, a.right_template_filter_condition as rightTemplateFilterCondition, COALESCE(c.ruleThreshold,0.0) as Threshold, "
						+ "c.dimension_id,c.ruleType as customOrGlobalRuleType, '' as description from rule_Template_Mapping a, listColGlobalRules c where a.activeFlag='Y' and a.ruleId = c.idListColrules and a.templateid = ?) union "
						+ "(select '" + DatabuckConstants.RULE_TYPE_CUSTOM_RULE
						+ "' AS RuleType, ruleName as RuleName, anchorColumns, idListColrules AS customOrGlobalRuleId, expression as RuleExpression, matchingRules, '' as filterCondition, '' as rightTemplateFilterCondition, COALESCE(ruleThreshold,0.0) as Threshold, domensionId as dimension_id,ruleType as customOrGlobalRuleType, description from listColRules where activeFlag='Y' and idData=?)";
			} else {
				sql = "(select '" + DatabuckConstants.RULE_TYPE_GLOBAL_RULE
						+ "' as RuleType,c.ruleName as RuleName, a.anchorColumns, a.ruleId AS customOrGlobalRuleId, a.ruleExpression AS RuleExpression , a.matchingRules AS matchingRules, a.filter_condition as filterCondition, a.right_template_filter_condition as rightTemplateFilterCondition, ifnull(c.ruleThreshold,0.0) as Threshold, "
						+ "c.dimension_id,c.ruleType as customOrGlobalRuleType, '' as description from rule_Template_Mapping a, listColGlobalRules c where a.activeFlag='Y' and a.ruleId = c.idListColrules and a.templateid = ?) union "
						+ "(select '" + DatabuckConstants.RULE_TYPE_CUSTOM_RULE
						+ "' AS RuleType, ruleName as RuleName, anchorColumns, idListColrules AS customOrGlobalRuleId, expression as RuleExpression, matchingRules, '' as filterCondition, '' as rightTemplateFilterCondition, ifnull(ruleThreshold,0.0) as Threshold, domensionId as dimension_id,ruleType as customOrGlobalRuleType, description from listColRules where activeFlag='Y' and idData=?)";
			}
			ruleCatalogList = jdbcTemplate.query(sql, new RowMapper<RuleCatalog>() {

				@Override
				public RuleCatalog mapRow(ResultSet rs, int rowNum) throws SQLException {
					RuleCatalog rc_checkDetails = new RuleCatalog();
					rc_checkDetails.setRowId(0l);
					rc_checkDetails.setIdApp(idApp);
					rc_checkDetails.setRuleReference(0l);
					rc_checkDetails.setRuleType(rs.getString("RuleType"));
					rc_checkDetails.setColumnName(rs.getString("anchorColumns"));
					rc_checkDetails.setRuleName(rs.getString("RuleName"));
					rc_checkDetails.setRuleCategory(DatabuckConstants.RULE_CATEGORY_CUSTOM);
					rc_checkDetails.setRuleExpression(rs.getString("RuleExpression"));
					rc_checkDetails.setMatchingRules(rs.getString("matchingRules"));
					rc_checkDetails.setCustomOrGlobalRuleId(rs.getLong("customOrGlobalRuleId"));
					rc_checkDetails.setRuleCode("");
					rc_checkDetails.setDefectCode("");
					rc_checkDetails.setThreshold(rs.getDouble("Threshold"));
					rc_checkDetails.setReviewBy("");
					rc_checkDetails.setReviewDate(null);
					rc_checkDetails.setReviewComments("");
					rc_checkDetails.setRuleDescription(rs.getString("description"));
					rc_checkDetails.setCustomOrGlobalRuleType(rs.getString("customOrGlobalRuleType"));
					rc_checkDetails.setFilterCondition(rs.getString("filterCondition"));
					String rightTemplateFilterCondition = rs.getString("rightTemplateFilterCondition");
					rightTemplateFilterCondition = (rightTemplateFilterCondition != null)
							? rightTemplateFilterCondition.trim()
							: "";
					rc_checkDetails.setRightTemplateFilterCondition(rightTemplateFilterCondition);

					// Set the dimension Id of the rule , if not found set default dimension Id
					Long rule_dimension_Id = rs.getLong("dimension_id");
					if (rule_dimension_Id != null && rule_dimension_Id > 0l)
						rc_checkDetails.setDimensionId(rule_dimension_Id);
					else
						rc_checkDetails.setDimensionId(dimensionId);

					return rc_checkDetails;
				}

			}, idData, idData);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return ruleCatalogList;
	}

	@Override
	public String getValidationApprovalStatus(long idApp) {
		String approvalStatus = "";
		try {
			String sql = "select case when b.row_id is null then 'Not Started' else b.element_reference end as approvalStatus from listApplications a left outer join  app_option_list_elements as b on a.approve_status = b.row_id where idApp=?";
			approvalStatus = jdbcTemplate.queryForObject(sql, String.class, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return approvalStatus;
	}

	@Override
	public int getCountOfValidationsAssociatedWithCustomRule(long idListColrules) {
		int count = 0;
		try {
			String sql = "select count(*) from listApplicationsRulesCatalog where custom_or_global_ruleId =?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, idListColrules);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return count;
	}

	@Override
	public String isDuplicateCheckAllEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from listDFTranRule where type='all' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public String isDuplicateCheckIdentityEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public String isStagingDuplicateCheckAllEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from staging_listDFTranRule where type='all' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public String isStagingDuplicateCheckIdentityEnabled(long idApp) {
		String status = "N";
		try {
			String sql = "select dupRow from staging_listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRow = jdbcTemplate.queryForObject(sql, String.class);
			if (dupRow != null) {
				status = dupRow;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return status;
	}

	@Override
	public List<Map<String, Object>> getDimensionDefectCodeList() {
		List<Map<String, Object>> defectDimensionList = null;
		try {
			String sSqlQry = "select idDimension as DimensionId, dimensionName as DimensionName from dimension";
			defectDimensionList = jdbcTemplate.query(sSqlQry, new RowMapper<Map<String, Object>>() {
				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					HashMap<String, Object> defectDimensionList1 = new HashMap<String, Object>();
					JSONArray defectCodeJsonList = new JSONArray();
					String dimensionId = rs.getString("DimensionId");
					defectDimensionList1.put("DimensionId", rs.getString("DimensionId"));
					defectDimensionList1.put("DimensionName", rs.getString("DimensionName"));
					String defectCodesQuery = "select row_id,defect_code from defect_codes where dimension_id =" + dimensionId;
					List<String> defectCodesList = new ArrayList<>();
					SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(defectCodesQuery);
					String defectCodes = "";
					if(sqlRowSet != null){
						while(sqlRowSet.next()){
							JSONObject defectCodeJson = new JSONObject();
							defectCodesList.add(sqlRowSet.getString("defect_code"));
							defectCodeJson.put("attributeId",sqlRowSet.getInt("row_id"));
							defectCodeJson.put("attributeName",sqlRowSet.getString("defect_code"));
							defectCodeJsonList.put(defectCodeJson);
						}
					}
					if(defectCodesList != null && !defectCodesList.isEmpty()) {
						defectCodes = String.join(",", defectCodesList);
					}
					defectDimensionList1.put("DefectCodes", defectCodes);
					defectDimensionList1.put("BusinessAttributes", defectCodeJsonList);
					return defectDimensionList1;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return defectDimensionList;

	}

	@Override
	public List<Map<String, Object>> getValidationsListForSchema(long idDataSchema) {
		List<Map<String, Object>> validationList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select ds.dataSource as DataSource, ds.idDataSchema as DataSchemaID, ds.dataLocation as DataLocation, ds.description as Description, ls.idApp as idApp, ls.name as ValidationName, ls.description as ValDescription, ls.appType as AppType, ls.idData as idData, ls.createdAt as CreatedAt, ls.updatedAt as UpdatedAt, ls.updatedBy as UpdatedBy, COALESCE((select element_text from app_option_list_elements where ls.approve_status = row_id),'Not Started') as Approve_status, ls.approve_comments as Approve_comments, ls.approve_date as Approve_date, ls.approver_name as Approve_by from listApplications ls ,listDataSources  ds  where  ls.idData = ds.idData  and ds.idDataSchema = "
						+ idDataSchema + " order by idApp desc";
			} else {
				sql = "select ds.dataSource as DataSource, ds.idDataSchema as DataSchemaID, ds.dataLocation as DataLocation, ds.description as Description, ls.idApp as idApp, ls.name as ValidationName, ls.description as ValDescription, ls.appType as AppType, ls.idData as idData, ls.createdAt as CreatedAt, ls.updatedAt as UpdatedAt, ls.updatedBy as UpdatedBy, ifnull((select element_text from app_option_list_elements where ls.approve_status = row_id),'Not Started') as Approve_status, ls.approve_comments as Approve_comments, ls.approve_date as Approve_date, ls.approver_name as Approve_by from listApplications ls ,listDataSources  ds  where  ls.idData = ds.idData  and ds.idDataSchema = "
						+ idDataSchema + " order by idApp desc";
			}

			LOG.debug("sql: " + sql);
			validationList = jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return validationList;
	}

	@Override
	public void clearRulesFromActualRuleCatalog(long idApp) {
		try {
			String sql = "delete from listApplicationsRulesCatalog where idApp=?";
			jdbcTemplate.update(sql, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void clearRulesFromStagingRuleCatalog(long idApp) {
		try {
			String sql = "delete from staging_listApplicationsRulesCatalog where idApp=?";
			jdbcTemplate.update(sql, idApp);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyRulesFromStagingCatalogToActual(long idApp) {
		try {
			String sql = "insert into listApplicationsRulesCatalog(idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,"
					+ "column_name ,rule_category ,rule_expression ,threshold_value ,review_comments ,review_date ,review_by ,"
					+ "rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,matching_rules ,custom_or_global_ruleId,"
					+ "rule_description,custom_or_global_rule_type,filter_condition,right_template_filter_condition,null_filter_columns) "
					+ "(select idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,column_name ,rule_category ,rule_expression ,"
					+ "threshold_value ,review_comments ,review_date ,review_by ,rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,"
					+ "matching_rules ,custom_or_global_ruleId, rule_description ,custom_or_global_rule_type,filter_condition,"
					+ "right_template_filter_condition,null_filter_columns from staging_listApplicationsRulesCatalog where idApp="
					+ idApp + ")";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyRulesFromActualCatalogToStaging(long idApp) {
		try {
			String sql = "insert into staging_listApplicationsRulesCatalog(idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,"
					+ "column_name ,rule_category ,rule_expression ,threshold_value ,review_comments ,review_date ,review_by ,"
					+ "rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,matching_rules ,custom_or_global_ruleId, rule_description,"
					+ "custom_or_global_rule_type,filter_condition,right_template_filter_condition,null_filter_columns) "
					+ "(select idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,column_name ,rule_category ,rule_expression ,"
					+ "threshold_value ,review_comments ,review_date ,review_by ,rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,"
					+ "matching_rules ,custom_or_global_ruleId, rule_description, custom_or_global_rule_type,filter_condition,"
					+ "right_template_filter_condition,null_filter_columns from listApplicationsRulesCatalog where idApp="
					+ idApp + ")";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyRuleCatalog(long sourceIdApp, long targetIdApp) {
		try {
			String sql = "insert into listApplicationsRulesCatalog(idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,"
					+ "column_name ,rule_category ,rule_expression ,threshold_value ,review_comments ,review_date ,review_by ,"
					+ "rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,matching_rules ,custom_or_global_ruleId,"
					+ "rule_description,custom_or_global_rule_type,filter_condition,right_template_filter_condition,null_filter_columns) "
					+ "(select "+targetIdApp+" as idApp ,rule_reference ,rule_code ,defect_code ,rule_type ,column_name ,rule_category ,rule_expression ,"
					+ "threshold_value ,review_comments ,review_date ,review_by ,rule_name ,activeFlag ,dimension_id ,agingCheckEnabled ,"
					+ "matching_rules ,custom_or_global_ruleId, rule_description ,custom_or_global_rule_type,filter_condition,"
					+ "right_template_filter_condition,null_filter_columns from listApplicationsRulesCatalog where idApp="
					+ sourceIdApp + ")";
			LOG.debug("sql:"+sql);
			jdbcTemplate.update(sql);

			// Update the status to 'CREATED'
			LOG.debug("Update the status to 'CREATED' ..");
			updateRuleCatalogStatusToCreated(targetIdApp);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}


	@Override
	public void copyRuleTagMapping(long sourceIdApp, long targetIdApp){
		//rule_tag_mapping
		try {
			String sql = "insert into rule_tag_mapping(tag_id,idApp,rule_id) " +
					"select tag_id," + targetIdApp + " as idApp,rule_id from rule_tag_mapping where idApp=" + sourceIdApp;
			LOG.debug("sql:" + sql);
			jdbcTemplate.update(sql);
		}catch (Exception e){
			LOG.error(e.getMessage());e.printStackTrace();
		}

	}

	@Override
	public String getApproveStatusNameById(int approve_status) {
		String approveStatusName = null;
		try {
			String sql = "select case when  b.row_id is null then 'Not Started' else b.element_reference end as ApprovalStatusName from app_option_list_elements b, app_option_list a where b.elements2app_list = a.row_id and upper(a.list_reference) = 'DQ_RULE_CATALOG_STATUS' and b.active > 0 and   b.row_id="
					+ approve_status;
			approveStatusName = jdbcTemplate.queryForObject(sql, String.class);

		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return approveStatusName;
	}

	@Override
	public void deleteUnusedDeactivatedCustomRules() {
		try {
			// Get the list of deactivated customrules
			String sql = "Select idListColrules from listColRules where activeFlag='N'";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					long idListColrules = sqlRowSet.getLong("idListColrules");

					int count = getCountOfValidationsAssociatedWithCustomRule(idListColrules);

					if (count == 0) {
						sql = "delete from listColRules where idListColrules = " + idListColrules;
						jdbcTemplate.execute(sql);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyLisApplicationsFromStagingToActual(long idApp) {
		try {
			ListApplications listApplications = getDataFromStagingListapplications(idApp);

			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "update listApplications set updatedAt=now(),updatedBy =?,fileNameValidation =?,entityColumn =?,"
						+ "colOrderValidation =?,matchingThreshold =?,nonNullCheck =?,numericalStatCheck =?,stringStatCheck =?,"
						+ "recordAnomalyCheck =?,incrementalMatching =?,incrementalTimestamp =?,dataDriftCheck =?,updateFrequency =?,"
						+ "frequencyDays =?,recordCountAnomaly =?,recordCountAnomalyThreshold =?,timeSeries =?,"
						+ "keyGroupRecordCountAnomaly =?,outOfNormCheck =?,applyRules =?,applyDerivedColumns =?,csvDir =?,"
						+ "groupEquality =?,groupEqualityThreshold =?,buildHistoricFingerPrint =?,historicStartDate =?,"
						+ "historicEndDate =?,historicDateFormat =?,active =?,lengthCheck =?,maxLengthCheck = ?,correlationcheck =?,timelinessKeyCheck =?,"
						+ "defaultCheck =?,defaultValues =?,patternCheck =?,dateRuleCheck =?,badData =?,prefix1 =?,prefix2 =?,"
						+ "dGroupNullCheck =?,dGroupDateRuleCheck =?,fileMonitoringType =?,validityThreshold =?,"
						+ "dGroupDataDriftCheck =?,rollTargetSchemaId =?,thresholdsApplyOption =?,continuousFileMonitoring =?,defaultPatternCheck=?,"
						+ "rollType =?,approve_status =?,approve_comments =?,approve_date =?::timestamp,approve_by =?,subcribed_email_id =?,"
						+ "approver_name =?,data_domain_id =?,staging_approve_status =?,reprofiling=?,validation_job_size=? where idApp="
						+ listApplications.getIdApp();
			else
				sql = "update listApplications set updatedAt=now(),updatedBy =?,fileNameValidation =?,entityColumn =?,"
						+ "colOrderValidation =?,matchingThreshold =?,nonNullCheck =?,numericalStatCheck =?,stringStatCheck =?,"
						+ "recordAnomalyCheck =?,incrementalMatching =?,incrementalTimestamp =?,dataDriftCheck =?,updateFrequency =?,"
						+ "frequencyDays =?,recordCountAnomaly =?,recordCountAnomalyThreshold =?,timeSeries =?,"
						+ "keyGroupRecordCountAnomaly =?,outOfNormCheck =?,applyRules =?,applyDerivedColumns =?,csvDir =?,"
						+ "groupEquality =?,groupEqualityThreshold =?,buildHistoricFingerPrint =?,historicStartDate =?,"
						+ "historicEndDate =?,historicDateFormat =?,active =?,lengthCheck =?,maxLengthCheck = ?,correlationcheck =?,timelinessKeyCheck =?,"
						+ "defaultCheck =?,defaultValues =?,patternCheck =?,dateRuleCheck =?,badData =?,prefix1 =?,prefix2 =?,"
						+ "dGroupNullCheck =?,dGroupDateRuleCheck =?,fileMonitoringType =?,validityThreshold =?,"
						+ "dGroupDataDriftCheck =?,rollTargetSchemaId =?,thresholdsApplyOption =?,continuousFileMonitoring =?,defaultPatternCheck=?,"
						+ "rollType =?,approve_status =?,approve_comments =?,approve_date =?,approve_by =?,subcribed_email_id =?,"
						+ "approver_name =?,data_domain_id =?,staging_approve_status =?,reprofiling=?,validation_job_size=? where idApp="
						+ listApplications.getIdApp();

			jdbcTemplate.update(sql,
					new Object[] { listApplications.getUpdatedBy(), listApplications.getFileNameValidation(),
							listApplications.getEntityColumn(), listApplications.getColOrderValidation(),
							listApplications.getMatchingThreshold(), listApplications.getNonNullCheck(),
							listApplications.getNumericalStatCheck(), listApplications.getStringStatCheck(),
							listApplications.getRecordAnomalyCheck(), listApplications.getIncrementalMatching(),
							listApplications.getIncrementalTimestamp(), listApplications.getDataDriftCheck(),
							listApplications.getUpdateFrequency(), listApplications.getFrequencyDays(),
							listApplications.getRecordCountAnomaly(), listApplications.getRecordCountAnomalyThreshold(),
							listApplications.getTimeSeries(), listApplications.getKeyGroupRecordCountAnomaly(),
							listApplications.getOutOfNormCheck(), listApplications.getApplyRules(),
							listApplications.getApplyDerivedColumns(), listApplications.getCsvDir(),
							listApplications.getGroupEquality(), listApplications.getGroupEqualityThreshold(),
							listApplications.getBuildHistoricFingerPrint(), listApplications.getHistoricStartDate(),
							listApplications.getHistoricEndDate(), listApplications.getHistoricDateFormat(),
							listApplications.getActive(), listApplications.getlengthCheck(),
							listApplications.getMaxLengthCheck(), listApplications.getCorrelationcheck(),
							listApplications.getTimelinessKeyChk(), listApplications.getDefaultCheck(),
							listApplications.getDefaultValues(), listApplications.getPatternCheck(),
							listApplications.getDateRuleChk(), listApplications.getBadData(),
							listApplications.getPrefix1(), listApplications.getPrefix2(),
							listApplications.getdGroupNullCheck(), listApplications.getdGroupDateRuleCheck(),
							listApplications.getFileMonitoringType(), listApplications.getValidityThreshold(),
							listApplications.getdGroupDataDriftCheck(), listApplications.getRollTargetSchemaId(),
							listApplications.getThresholdsApplyOption(), listApplications.getContinuousFileMonitoring(),
							listApplications.getDefaultPatternCheck(), listApplications.getRollType(),
							listApplications.getApproveStatus(), listApplications.getApproveComments(),
							listApplications.getApproveDate(), listApplications.getApproveBy(),
							listApplications.getSubcribedEmailId(), listApplications.getApproverName(),
							listApplications.getData_domain(), listApplications.getStagingApproveStatus() ,
							listApplications.getReprofiling(),
							listApplications.getValidationJobSize()});

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyLisApplicationsFromActualToStaging(long idApp) {
		try {
			// Delete the entry and update it
			String sql = "delete from staging_listApplications where idApp=" + idApp;
			jdbcTemplate.execute(sql);

			sql = "INSERT into staging_listApplications(idApp ,name ,description ,appType ,idData ,idRightData ,createdBy ,"
					+ "createdAt ,updatedAt ,updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,"
					+ "matchingThreshold ,nonNullCheck ,numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,"
					+ "incrementalMatching ,incrementalTimestamp ,dataDriftCheck ,updateFrequency ,frequencyDays ,"
					+ "recordCountAnomaly ,recordCountAnomalyThreshold ,timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,"
					+ "applyRules ,applyDerivedColumns ,csvDir ,groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,"
					+ "historicStartDate ,historicEndDate ,historicDateFormat ,active ,lengthCheck ,maxLengthCheck,correlationcheck ,"
					+ "project_id ,timelinessKeyCheck ,defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,"
					+ "idLeftData ,prefix1 ,prefix2 ,dGroupNullCheck ,dGroupDateRuleCheck ,fileMonitoringType ,"
					+ "createdByUser ,validityThreshold ,dGroupDataDriftCheck ,rollTargetSchemaId ,thresholdsApplyOption ,"
					+ "continuousFileMonitoring ,rollType ,approve_status ,approve_comments ,approve_date ,approve_by ,"
					+ "domain_id ,subcribed_email_id ,approver_name ,data_domain_id ,staging_approve_status, defaultPatternCheck,reprofiling,"
					+ "validation_job_size) "
					+ "(SELECT idApp ,name ,description ,appType ,idData ,idRightData ,createdBy ,createdAt ,updatedAt ,"
					+ "updatedBy ,fileNameValidation ,entityColumn ,colOrderValidation ,matchingThreshold ,nonNullCheck ,"
					+ "numericalStatCheck ,stringStatCheck ,recordAnomalyCheck ,incrementalMatching ,incrementalTimestamp ,"
					+ "dataDriftCheck ,updateFrequency ,frequencyDays ,recordCountAnomaly ,recordCountAnomalyThreshold ,"
					+ "timeSeries ,keyGroupRecordCountAnomaly ,outOfNormCheck ,applyRules ,applyDerivedColumns ,csvDir ,"
					+ "groupEquality ,groupEqualityThreshold ,buildHistoricFingerPrint ,historicStartDate ,historicEndDate ,"
					+ "historicDateFormat ,active ,lengthCheck ,maxLengthCheck,correlationcheck ,project_id ,timelinessKeyCheck ,"
					+ "defaultCheck ,defaultValues ,patternCheck ,dateRuleCheck ,badData ,idLeftData ,prefix1 ,prefix2 ,"
					+ "dGroupNullCheck ,dGroupDateRuleCheck,fileMonitoringType ,createdByUser ,"
					+ "validityThreshold ,dGroupDataDriftCheck ,rollTargetSchemaId ,thresholdsApplyOption ,"
					+ "continuousFileMonitoring ,rollType ,approve_status ,approve_comments ,approve_date ,approve_by ,"
					+ "domain_id ,subcribed_email_id ,approver_name ,data_domain_id ,staging_approve_status, defaultPatternCheck,reprofiling,validation_job_size "
					+ "from listApplications where idApp=" + idApp + ")";
			jdbcTemplate.execute(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public ListApplications getDataFromStagingListapplications(long idApp) {
		try {
			ListApplications la = new ListApplications();
			String sql = "select * from staging_listApplications where idApp=" + idApp;

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			if (queryForRowSet.next()) {
				la.setIdApp(queryForRowSet.getLong("idApp"));
				la.setFileNameValidation(queryForRowSet.getString("fileNameValidation"));
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
				la.setDefaultCheck(queryForRowSet.getString("defaultCheck"));
				la.setPatternCheck(queryForRowSet.getString("patternCheck"));
				la.setBadData(queryForRowSet.getString("badData"));
				la.setlengthCheck(queryForRowSet.getString("lengthCheck"));
				la.setMaxLengthCheck(queryForRowSet.getString("maxLengthCheck"));
				la.setDateRuleChk(queryForRowSet.getString("dateRuleCheck"));
				la.setdGroupNullCheck(queryForRowSet.getString("dGroupNullCheck"));
				la.setdGroupDateRuleCheck(queryForRowSet.getString("dGroupDateRuleCheck"));
				la.setFileMonitoringType(queryForRowSet.getString("fileMonitoringType"));
				la.setAppType(queryForRowSet.getString("appType"));
				la.setdGroupDataDriftCheck(queryForRowSet.getString("dGroupDataDriftCheck"));
				la.setRollTargetSchemaId(queryForRowSet.getLong("rollTargetSchemaId"));
				la.setThresholdsApplyOption(queryForRowSet.getInt("thresholdsApplyOption"));
				la.setContinuousFileMonitoring(queryForRowSet.getString("continuousFileMonitoring"));
				la.setRollType(queryForRowSet.getString("rollType"));
				la.setdGroupDataDriftCheck(queryForRowSet.getString("dGroupDataDriftCheck"));
				la.setProjectId(queryForRowSet.getLong("project_id"));
				la.setDomainId(queryForRowSet.getLong("domain_id"));
				la.setData_domain(queryForRowSet.getInt("data_domain_id"));
				la.setApproveBy(queryForRowSet.getInt("approve_by"));
				la.setApproveComments(queryForRowSet.getString("approve_comments"));
				la.setApproveStatus(queryForRowSet.getInt("approve_status"));
				la.setApproveDate(queryForRowSet.getString("approve_date"));
				la.setApproverName(queryForRowSet.getString("approver_name"));
				la.setStagingApproveStatus(queryForRowSet.getInt("staging_approve_status"));
				la.setSubcribedEmailId(queryForRowSet.getString("subcribed_email_id"));
				la.setActive(queryForRowSet.getString("active"));
				la.setIncrementalTimestamp(queryForRowSet.getDate("incrementalTimestamp"));
				la.setDefaultPatternCheck(queryForRowSet.getString("defaultPatternCheck"));
				la.setReprofiling(queryForRowSet.getString("reprofiling"));
				la.setValidityThreshold(queryForRowSet.getDouble("validityThreshold"));
				la.setValidationJobSize(queryForRowSet.getString("validation_job_size"));

				String sql2 = "select * from staging_listDFTranRule where idApp=" + idApp;
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
					sql2 = "select * from staging_listdftranrule where idApp=" + idApp;

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
							la.setDupRowAllThreshold(Double.valueOf(map.get("identity").split(",")[1]));
						} else if (map.containsKey("all")) {
							la.setDupRowIdentity(map.get("all").split(",")[0]);
							la.setDupRowAllThreshold(Double.valueOf(map.get("all").split(",")[1]));
						}
					}
				}
				return la;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return null;
	}

	@Override
	public int updateIntoStagingListapplication(ListApplications listApplications) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += "update staging_listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,timelinessKeyCheck=?,"
						+ "defaultCheck=?, recordAnomalyCheck=?,dataDriftCheck=?,updateFrequency=?,frequencyDays=?,"
						+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,timeSeries=?,keyGroupRecordCountAnomaly=?,"
						+ "outOfNormCheck=?,applyRules=?, applyDerivedColumns=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
						+ "buildHistoricFingerPrint=?,historicStartDate=?::date,historicEndDate=?::date,historicDateFormat=?,patternCheck=?,"
						+ "dateRuleCheck=?,badData=?,lengthCheck=?, maxLengthCheck=?,dGroupNullCheck=?, dGroupDateRuleCheck=?, validityThreshold=?, dGroupDataDriftCheck=?, "
						+ "thresholdsApplyOption=?,data_domain_id=?, defaultPatternCheck=?,reprofiling=? where idApp="
						+ listApplications.getIdApp();
			else
				sql = "update staging_listApplications set colOrderValidation=?,fileNameValidation=?,entityColumn=?,nonNullCheck=?,numericalStatCheck=?,stringStatCheck=?,timelinessKeyCheck=?,"
						+ "defaultCheck=?, recordAnomalyCheck=?,dataDriftCheck=?,updateFrequency=?,frequencyDays=?,"
						+ "recordCountAnomaly=?,recordCountAnomalyThreshold=?,timeSeries=?,keyGroupRecordCountAnomaly=?,"
						+ "outOfNormCheck=?,applyRules=?, applyDerivedColumns=?,csvDir=?,groupEquality=?,groupEqualityThreshold=?,incrementalMatching=?,"
						+ "buildHistoricFingerPrint=?,historicStartDate=?,historicEndDate=?,historicDateFormat=?,patternCheck=?,"
						+ "dateRuleCheck=?,badData=?,lengthCheck=?, maxLengthCheck=?,dGroupNullCheck=?, dGroupDateRuleCheck=?, validityThreshold=?, dGroupDataDriftCheck=?, "
						+ "thresholdsApplyOption=?,data_domain_id=?, defaultPatternCheck=?,reprofiling=? where idApp="
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
							listApplications.getData_domain(), listApplications.getDefaultPatternCheck() ,listApplications.getReprofiling() });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateStagingListDfTranRule(Long idApp, String duplicateCountIdentity,
			Double duplicateCountIdentityThreshold, String duplicateCountAll, Double duplicateCountAllThreshold) {
		int update = 0;
		try {

			if (duplicateCountIdentity == null || !duplicateCountIdentity.trim().equalsIgnoreCase("Y"))
				duplicateCountIdentity = "N";
			if (duplicateCountAll == null || !duplicateCountAll.trim().equalsIgnoreCase("Y"))
				duplicateCountAll = "N";

			String query = "update staging_listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
			update = jdbcTemplate.update(query, new Object[] { duplicateCountIdentity, "N",
					duplicateCountIdentityThreshold, "identity", 0, idApp, "identity" });

			String query1 = "update staging_listDFTranRule set duprow=?,seqrow=?,threshold=?,type=?,seqidcol=? where idApp=? and type=?";
			update = jdbcTemplate.update(query1,
					new Object[] { duplicateCountAll, "N", duplicateCountAllThreshold, "all", 0, idApp, "all" });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public void insertIntoStagingListDfTranRule(Long idApp) {
		try {

			String query = "insert into staging_listDFTranRule(idApp,duprow,seqrow,threshold,type,seqidcol) values(?,?,?,?,?,?)";
			jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "identity", 0 });

			jdbcTemplate.update(query, new Object[] { idApp, "N", "N", 0, "all", 0 });
			
			LOG.debug("Added new entry int tran rule table.");
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public Multimap<String, Double> getDataFromStagingListDfTranRule(Long idApp) {
		Multimap<String, Double> map = LinkedListMultimap.create();
		try {

			String sql = "select threshold,dupRow from staging_listDFTranRule where type='all' and idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				map.put(queryForRowSet.getString(2), queryForRowSet.getDouble(1));
			}

			String sql1 = "select threshold,dupRow from staging_listDFTranRule where type='identity' and idApp="
					+ idApp;
			SqlRowSet queryForRowSet1 = jdbcTemplate.queryForRowSet(sql1);
			while (queryForRowSet1.next()) {
				map.put(queryForRowSet1.getString(2), queryForRowSet1.getDouble(1));
			}
			return map;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return null;
	}

	@Override
	public void copyListDfTranRuleFromStagingToActual(long idApp) {
		try {
			String duplicateCountAll = "N";
			Double duplicateCountAllThreshold = 0.0;
			String duplicateCountIdentity = "N";
			Double duplicateCountIdentityThreshold = 0.0;

			String sql = "select threshold,dupRow from staging_listDFTranRule where type='all' and idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				duplicateCountAll = queryForRowSet.getString(2);
				duplicateCountAllThreshold = queryForRowSet.getDouble(1);
			}

			String sql1 = "select threshold,dupRow from staging_listDFTranRule where type='identity' and idApp="
					+ idApp;
			SqlRowSet queryForRowSet1 = jdbcTemplate.queryForRowSet(sql1);
			while (queryForRowSet1.next()) {
				duplicateCountIdentity = queryForRowSet1.getString(2);
				duplicateCountIdentityThreshold = queryForRowSet1.getDouble(1);
			}

			validationcheckdao.updateintolistdftranrule(idApp, duplicateCountIdentity, duplicateCountIdentityThreshold,
					duplicateCountAll, duplicateCountAllThreshold);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void copyListDfTranRuleFromActualToStaging(long idApp) {
		try {
			String sql = "delete from staging_listDFTranRule where idApp=?";
			jdbcTemplate.update(sql, idApp);

			sql = "insert into staging_listDFTranRule(idApp,dupRow,seqRow,seqIDcol,threshold,type) "
					+ "(select idApp,dupRow,seqRow,seqIDcol,threshold,type from listDFTranRule where idApp=" + idApp
					+ ")";
			jdbcTemplate.update(sql);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

	}

	@Override
	public Map<String, String> getDataFromStagingListDFTranRuleForMap(long idApp) {
		Map<String, String> map = new HashMap<>();
		try {
			String sql = "select dupRow from staging_listDFTranRule where type='all' and idApp=" + idApp;
			String dupRowall = jdbcTemplate.queryForObject(sql, String.class);

			String sql1 = "select dupRow from staging_listDFTranRule where type='identity' and idApp=" + idApp;
			String dupRowidentity = jdbcTemplate.queryForObject(sql1, String.class);

			map.put("all", dupRowall);
			map.put("identity", dupRowidentity);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return map;
	}

	@Override
	public int getApprovalStatusCodeByStatusName(String approvalStatus) {
		int approval_status_code = -1;
		try {
			String sql = "select b.row_id as RowId from app_option_list_elements b, app_option_list a where b.elements2app_list = a.row_id  and upper(list_reference) = 'DQ_RULE_CATALOG_STATUS'"
					+ " and  b.active > 0 and b.element_reference = '" + approvalStatus + "'";

			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sql);
			approval_status_code = (oSqlRowSet.next()) ? oSqlRowSet.getInt("RowId") : -1;

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return approval_status_code;
	}

	@Override
	public int getCountOfValidationsAssociatedWithGlobalRule(long templateId, long globalRuleId) {
		int count = 0;
		try {
			String sql = "select count(*) from listApplicationsRulesCatalog t1 join listApplications t2 on t1.idApp=t2.idApp where rule_type='Global Rule' and t2.idData=? and t1.custom_or_global_ruleId=?";
			count = jdbcTemplate.queryForObject(sql, Integer.class, templateId, globalRuleId);
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return count;
	}

	@Override
	public void deleteUnusedDeactivatedGlobalRules() {
		try {
			// Get the list of deactivated global rules
			String sql = "Select templateid,ruleId from rule_Template_Mapping where activeFlag='N'";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					long templateId = sqlRowSet.getLong("templateid");
					long globalRuleId = sqlRowSet.getLong("ruleId");

					int count = getCountOfValidationsAssociatedWithGlobalRule(templateId, globalRuleId);

					if (count == 0) {
						sql = "delete from rule_Template_Mapping where templateid = " + templateId + " and ruleId="
								+ globalRuleId;
						LOG.debug("sql:"+sql);
						jdbcTemplate.execute(sql);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public boolean updateRuleDetailsOfCustomRuleFromRuleCatalog(RuleCatalog ruleCatalog) {
		boolean status = false;
		try {
			String columnName = ruleCatalog.getColumnName();
			if (columnName == null || columnName.trim().isEmpty())
				columnName = "";

			String sql = "update listColRules set description=?, expression=?, matchingRules=?, anchorColumns=?, ruleThreshold=? ,domensionId=? where idListColrules=?";
			int count = jdbcTemplate.update(sql, ruleCatalog.getRuleDescription(), ruleCatalog.getRuleExpression(),
					ruleCatalog.getMatchingRules(), columnName, ruleCatalog.getThreshold(),
					ruleCatalog.getDimensionId(), ruleCatalog.getCustomOrGlobalRuleId());

			if (count == 1)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

		return status;
	}

	@Override
	public boolean updateRuleDetailsOfGlobalRuleFromRuleCatalog(long templateid, RuleCatalog ruleCatalog) {
		boolean status = false;
		try {

			String ruleExpression = ruleCatalog.getRuleExpression();
			String matchingRules = ruleCatalog.getMatchingRules();
			String filterCondition = ruleCatalog.getFilterCondition();
			String rightTemplateFilterCondition = ruleCatalog.getRightTemplateFilterCondition();

			String columnName = ruleCatalog.getColumnName();
			if (columnName == null || columnName.trim().isEmpty())
				columnName = "";

			String sql = "update rule_Template_Mapping set ruleExpression=?,matchingRules=?, anchorColumns=?,filter_condition=?,right_template_filter_condition=?  where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, ruleExpression, matchingRules, columnName, filterCondition,
					rightTemplateFilterCondition, templateid, ruleCatalog.getCustomOrGlobalRuleId());
			if (count == 1)
				status = true;
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

		return status;
	}

	@Override
	public boolean disableColumnCheckFromListDataDefinition(long templateId, String columnName, String check_columnName,
			String columnValue) {
		boolean status = false;

		try {
			String sql = "update listDataDefinition set " + check_columnName + "=? where idData=? and displayName=?";
			int count = jdbcTemplate.update(sql, columnValue, templateId, columnName);

			if (count == 1)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

		return status;
	}

	@Override
	public boolean disableColumnCheckFromStagingListDataDefinition(long templateId, String columnName,
			String check_columnName, String columnValue) {
		boolean status = false;

		try {
			String sql = "update staging_listDataDefinition set " + check_columnName
					+ "=? where idData=? and displayName=?";
			int count = jdbcTemplate.update(sql, columnValue, templateId, columnName);

			if (count == 1)
				status = true;

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

		return status;
	}

	@Override
	public int updateDuplicateCheckThresholdByType(long idApp, String type, double threshold) {
		int update = 0;
		try {
			String query = "update listDFTranRule set threshold=? where idApp=? and type=? and dupRow='Y'";

			update = jdbcTemplate.update(query, new Object[] { threshold, idApp, type });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public int updateStagingDuplicateCheckThresholdByType(long idApp, String type, double threshold) {
		int update = 0;
		try {
			String query = "update staging_listDFTranRule set threshold=? where idApp=? and type=? and dupRow='Y'";

			update = jdbcTemplate.update(query, new Object[] { threshold, idApp, type });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public long getRuleIdForGlobalRules(long templateId, String ruleName) {
		long ruleId = 0;
		try {

			String sql = "select rt.ruleId from rule_Template_Mapping rt join listColGlobalRules lg on rt.ruleId=lg.idListColRules where rt.templateId=? and lg.ruleName=? and activeFlag='Y'";
			ruleId = jdbcTemplate.queryForObject(sql, Long.class, templateId, ruleName);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return ruleId;
	}

	@Override
	public int updateGlobalRuleThreshold(long ruleId, double updated_Threshold) {
		int update = 0;
		try {
			String query = "update listColGlobalRules set ruleThreshold=? where idListColrules=?";

			update = jdbcTemplate.update(query, new Object[] { updated_Threshold, ruleId });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public int updateCustomRuleThreshold(long templateId, String ruleName, double updated_Threshold) {
		int update = 0;
		try {
			String query = "update listColRules set ruleThreshold=? where idData= ? and ruleName=?  and activeFlag='Y'";

			update = jdbcTemplate.update(query, new Object[] { updated_Threshold, templateId, ruleName });

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public List<ListDfTranRule> getDataFromStagingListDfTranRuleForThreshold(Long idApp) {
		RowMapper<ListDfTranRule> rm = (rs, i) -> {
			ListDfTranRule ldf = new ListDfTranRule();
			ldf.setIdDFT(rs.getLong("idDFT"));
			ldf.setIdApp(rs.getLong("idApp"));
			ldf.setDupRow(rs.getString("dupRow"));
			ldf.setSeqRow(rs.getString("seqRow"));
			ldf.setSeqIDcol(rs.getLong("seqIDcol"));
			ldf.setThreshold(rs.getDouble("threshold"));
			ldf.setType(rs.getString("type"));
			return ldf;
		};
		return jdbcTemplate.query("SELECT * FROM staging_listDFTranRule WHERE idApp=?", rm, idApp);
	}

	@Override
	public JSONArray getNonEnabledColumnsForCheck(long idData, String checkColumnName,
			String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format," + templateCheckThresholdColumn
					+ " as threshold_value from listDataDefinition where idData=" + idData + " and " + checkColumnName
					+ " ='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					String columnName = sqlRowSet.getString("displayName").trim();
					String format = sqlRowSet.getString("format");
					double threshold = sqlRowSet.getDouble("threshold_value");

					// For DateRule check - format must be date
					if (checkColumnName.equalsIgnoreCase("dateRule") && !format.equalsIgnoreCase("date"))
						continue;

					// For Distribution check , Record Anomaly - format must be numeric types or
					// decimal
					else if ((checkColumnName.equalsIgnoreCase("numericalStat")
							|| checkColumnName.equalsIgnoreCase("recordAnomaly"))
							&& !(format.equalsIgnoreCase("NUMBER") || format.equalsIgnoreCase("int")
									|| format.equalsIgnoreCase("INTEGER") || format.equalsIgnoreCase("NUMERIC")
									|| format.equalsIgnoreCase("BIGINT") || format.equalsIgnoreCase("SMALLINT")
									|| format.equalsIgnoreCase("FLOAT") || format.equalsIgnoreCase("DOUBLE")
									|| format.equalsIgnoreCase("DECIMAL")))
						continue;

					JSONObject colObj = new JSONObject();
					colObj.put("name", columnName);
					colObj.put("format", format);
					colObj.put("threshold", threshold);
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	@Override
	public JSONArray getNonEnabledColumnsForLengthCheck(long idData, String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format,lengthvalue," + templateCheckThresholdColumn
					+ " as threshold_value from listDataDefinition where idData=" + idData
					+ " and  lengthCheck='N'  and maxLengthCheck='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim());
					colObj.put("format", sqlRowSet.getString("format"));
					colObj.put("lengthvalue", sqlRowSet.getString("lengthValue"));
					colObj.put("threshold", sqlRowSet.getDouble("threshold_value"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	@Override
	public JSONArray getNonEnabledColumnsForPatternCheck(long idData, String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format,patterns," + templateCheckThresholdColumn
					+ " as threshold_value from listDataDefinition where idData=" + idData + " and patterncheck ='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim());
					colObj.put("format", sqlRowSet.getString("format"));
					colObj.put("patterns", sqlRowSet.getString("patterns"));
					colObj.put("threshold", sqlRowSet.getDouble("threshold_value"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}
	@Override
	public JSONArray getNonEnabledColumnsForDefaultPatternCheck(long idData, String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		templateCheckThresholdColumn = templateCheckThresholdColumn.equalsIgnoreCase("defaultPatterns") ? "0.0" : templateCheckThresholdColumn;
		try {
			String query = "select displayName,format,patterns," + templateCheckThresholdColumn
					+ " as threshold_value from listDataDefinition where idData=" + idData + " and defaultPatternCheck ='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim());
					colObj.put("format", sqlRowSet.getString("format"));
					colObj.put("patterns", sqlRowSet.getString("patterns"));
					colObj.put("threshold", sqlRowSet.getDouble("threshold_value"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	public JSONArray getNonEnabledColumnsForDefaultCheck(long idData, String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format,defaultvalues," + templateCheckThresholdColumn
					+ " as threshold_value from listDataDefinition where idData=" + idData + " and defaultcheck ='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim());
					colObj.put("format", sqlRowSet.getString("format"));
					colObj.put("defaultvalues", sqlRowSet.getString("defaultvalues"));
					colObj.put("threshold", sqlRowSet.getDouble("threshold_value"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	public JSONArray getNonEnabledColumnsForDateRuleCheck(long idData, String templateCheckThresholdColumn) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format,dateformat from listDataDefinition where idData=" + idData
					+ " and daterule ='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim()); 
					colObj.put("format", sqlRowSet.getString("format"));
					colObj.put("dateformat", sqlRowSet.getString("dateformat"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	@Override
	public JSONArray getNonEnabledColumnsForPrimaryKeyAndDuplicateKeyCheck(long idData) {
		JSONArray inactiveColumns = new JSONArray();
		try {
			String query = "select displayName,format from listDataDefinition where idData=" + idData
					+ " and  primaryKey='N'  and dupkey='N'";

			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);

			if (sqlRowSet != null) {
				while (sqlRowSet.next()) {
					JSONObject colObj = new JSONObject();
					colObj.put("name", sqlRowSet.getString("displayName").trim());
					colObj.put("format", sqlRowSet.getString("format"));
					inactiveColumns.put(colObj);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return inactiveColumns;
	}

	@Override
	public void enableOrDisableCheckInValidation(long idApp, String checkEntityColumn, String checkValue) {
		try {
			String query = "update listApplications set " + checkEntityColumn + "='" + checkValue + "' where idApp="
					+ idApp;

			jdbcTemplate.update(query);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void enableOrDisableCheckInValidationStaging(long idApp, String checkEntityColumn, String checkValue) {
		try {
			String query = "update staging_listApplications set " + checkEntityColumn + "='" + checkValue
					+ "' where idApp=" + idApp;

			jdbcTemplate.update(query);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void enableOrDisableDuplicateCheckByType(long idApp, String type, String checkEntityColumn,
			String checkValue) {
		try {
			String query = "update listDFTranRule set " + checkEntityColumn + "='" + checkValue
					+ "' where idApp=? and type=?";
			LOG.debug("query: " + query);
			LOG.debug("type: " + type);

			jdbcTemplate.update(query, idApp, type);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void enableOrDisableStagingDuplicateCheckByType(long idApp, String type, String checkEntityColumn,
			String checkValue) {
		try {
			String query = "update staging_listDFTranRule set " + checkEntityColumn + "='" + checkValue
					+ "' where idApp=? and type=?";
			LOG.debug("query: " + query);
			LOG.debug("type: " + type);

			jdbcTemplate.update(query, idApp, type);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void approveRCAThresholdChangesInActualRuleCatalog(long idApp, String checkName, double updated_Threshold) {
		String sql = "";
		int count = 0;
		try {
			if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY)) {
				sql = "update listApplicationsRulesCatalog set threshold_value =? where rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, checkName, idApp);
			}
			LOG.debug("sql listApplicationsRulesCatalog= " + sql);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void approveRCAThresholdChangesInStagingRuleCatalog(long idApp, String checkName, double updated_Threshold) {
		String sql = "";
		int count = 0;
		try {
			if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY)) {
				sql = "update staging_listApplicationsRulesCatalog set threshold_value =? where rule_type=? and idApp=?";
				count = jdbcTemplate.update(sql, updated_Threshold, checkName, idApp);
			}
			LOG.debug("sql all other staging_listApplicationsRulesCatalog= " + sql);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public void approveRCAThresholdChangesInStagingValidation(long idApp, String checkName, String column_Name,
			double updated_Threshold) {
		try {
			String query = "update staging_listApplications set " + column_Name + "=" + updated_Threshold
					+ "where idApp=?";
			LOG.debug("query: " + query);
			jdbcTemplate.update(query, idApp);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}

	}

	@Override
	public void approveRCAThresholdChangesInActualValidation(long idApp, String checkName, String column_Name,
			double updated_Threshold) {
		try {
			String query = "update listApplications set " + column_Name + "=" + updated_Threshold + "where idApp=?";
			LOG.debug("query: " + query);
			jdbcTemplate.update(query, idApp);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
	}

	@Override
	public int getRowIdFromActualValidation(long idApp, int rule_reference) {
		int update = 0;
		try {
			String query = "select row_id from listApplicationsRulesCatalog where idApp=? and rule_reference=?";
			LOG.debug("query: " + query);
			update = jdbcTemplate.queryForObject(query, Integer.class, idApp, rule_reference);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public int getRowIdFromStagingValidation(long idApp, int rule_reference) {
		int update = 0;
		try {
			String query = "select row_id from staging_listApplicationsRulesCatalog where idApp=? and rule_reference=?";
			LOG.debug("query: " + query);
			update = jdbcTemplate.queryForObject(query, Integer.class, idApp, rule_reference);

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return update;
	}

	@Override
	public boolean updateNullFilterColumnsOfRuleTemplateMapping(long templateId, long globalRuleId, String nullFilterColumns) {
		try{
			String sql="update rule_Template_Mapping set null_filter_columns=? where templateid=? and ruleId=?";
			int count = jdbcTemplate.update(sql, nullFilterColumns,templateId, globalRuleId);
			if(count >=1)
				return true;
		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int updateNullFilterColumnsOfRuleCatlog(long idApp, long ruleId, String nullFilterColumns, String validationStatus) {
		int updateCount = 0;
		try {

			if (nullFilterColumns == null || nullFilterColumns.trim().isEmpty())
				nullFilterColumns = "";

			String sql = "";
			if (validationStatus.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {
				sql = "update staging_listApplicationsRulesCatalog set null_filter_columns=? where idApp=? and custom_or_global_ruleId=?";
				updateCount = jdbcTemplate.update(sql, nullFilterColumns, idApp, ruleId);

			} else {
				sql = "update listApplicationsRulesCatalog set null_filter_columns=? where idApp=? and custom_or_global_ruleId=?";
				updateCount = jdbcTemplate.update(sql, nullFilterColumns, idApp, ruleId);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());e.printStackTrace();
		}
		return updateCount;
	}

	@Override
	public Map<String, Double> getRuleCatalogThresholdValues(Long idApp, String check_name) {
		Map<String, Double> threshold_values = new HashMap<>();
		try {
			String query = "select threshold_value,column_name from  listApplicationsRulesCatalog where rule_type='" + check_name + "' and idApp =" + idApp + "";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query);
			while (sqlRowSet.next()) {
				threshold_values.put(sqlRowSet.getString("column_name"), sqlRowSet.getDouble("threshold_value"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return threshold_values;
	}

}
