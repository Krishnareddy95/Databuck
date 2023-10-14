package com.databuck.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.dao.*;
import com.databuck.taskmanager.TaskManagerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.ColumnProfile_DP;
import com.databuck.bean.GlobalFilters;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.ruleFields;
import com.databuck.bean.SynonymLibrary;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IExtendTemplateRuleDAO;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GlobalRuleService {

	@Autowired
	private GlobalRuleDAO globalRuleDAO;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private TaskManagerService taskManagerService;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private IValidationCheckDAO validationCheckDAO;

	@Autowired
	private IValidationService validationService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;

	@Autowired
	private ITemplateViewDAO templateViewDao;

	@Autowired
	private IExtendTemplateRuleDAO externdTemplateRuleDao;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	private static final Logger LOG = Logger.getLogger(GlobalRuleService.class);

	public JSONObject validateSynonymExpression(String syn_expression, int domain_id) {

		String status = "success";
		String message = "";

		try {
			// Check if the expression is non empty
			if (syn_expression != null && !syn_expression.trim().isEmpty()) {

				// Get the synonyms used in the expression
				String[] synonyms_in_exp = StringUtils.substringsBetween(syn_expression, "@", "@");

				// Check synonyms exists in the expression
				if (synonyms_in_exp != null && synonyms_in_exp.length > 0) {

					// Check if the synonym is valid
					for (String synonym : synonyms_in_exp) {

						ruleFields rc = new ruleFields();

						rc.setUsercolumns(synonym);
						rc.setDomain_id(domain_id);

						int syn_id = globalRuleDAO.getsynosymId(rc);// synonym table syn id

						if (syn_id <= 0) {
							// message = "[" + synonym + "] is not a valid synonym name";
							LOG.info(message);
							LOG.debug("creating new synonym with synonym name[" + synonym + "]");
							JSONObject newSynonymJson = new JSONObject();
							newSynonymJson.put("SynonymsId", "-1");
							newSynonymJson.put("DomainId", domain_id);
							newSynonymJson.put("SynonymsName", synonym);
							newSynonymJson.put("UserFields", synonym);
							JSONObject responseObj = saveSynonymsFormFromViewList(newSynonymJson);
							if (responseObj != null && responseObj.getBoolean("Result"))
								LOG.debug("New synonym with synonym name[" + synonym + "] is created");
							else {
								status = "failed";
								message = "failed to create new synonym";
								LOG.error("\n====>" + message);
								break;
							}
						}
					}
				} else {
					status = "failed";
					String short_expr = (syn_expression.length() > 20) ? syn_expression.substring(0, 20) + " ..."
							: syn_expression;
					message = "No synonym is found in expression " + short_expr;
					LOG.error("\n====>" + message);
				}
			} else {
				status = "failed";
				message = "Empty expression";
				LOG.error("\n====>" + message);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Unexpected error, failed to validate synonym expression";
			e.printStackTrace();
		}

		JSONObject synonymStatusObj = new JSONObject();
		synonymStatusObj.put("status", status);
		synonymStatusObj.put("message", message);

		return synonymStatusObj;
	}

	public JSONObject updateAssociatedTemplatesByRightFilterCondition(GlobalFilters globalFilter) {
		LOG.info("\n======> updateAssociatedTemplatesByRightFilterCondition - START <======");

		JSONObject updateTemplateMappingObj = new JSONObject();
		JSONArray mappingErrors = new JSONArray();
		String status = "success";
		String message = "Right Filter Condition is successfully updated in Rule Template Mapping";

		Set<Long> templateIdList = new HashSet<Long>();
		try {

			Integer domainId = globalFilter.getDomainId();
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			if (expandedSynonymMap != null && expandedSynonymMap.size() > 0) {

				SqlRowSet sqlRowSet = globalRuleDAO.getGlobalRulesByRightTemplateFilterId(globalFilter.getFilterId());

				if (sqlRowSet != null) {

					while (sqlRowSet.next()) {

						String filterCondition = globalFilter.getFilterCondition();
						long globalRuleId = sqlRowSet.getLong("idListColrules");
						long templateId = sqlRowSet.getLong("idData");
						String mappingError = "";
						boolean modifyStatus = false;
						boolean updateStatus = false;

						LOG.debug("\n====> Updating right filter condition of global rule [" + globalRuleId
								+ "] for template with Id: " + templateId);

						LOG.debug("\n====>Right FilterCondition: " + filterCondition);

						if (globalRuleId <= 0l || templateId <= 0l)
							continue;

						// Add the template Id to list for updating associated validation catalogs
						templateIdList.add(templateId);

						// Get the effective synonyms for the template
						Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(templateId,
								expandedSynonymMap);
						LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

						// Mapping the filter condition with actual columns of template
						LOG.info("\n====> Update Right Filter condition");

						String[] columns = StringUtils.substringsBetween(filterCondition, "@", "@");

						Set<String> conditionColumns = new HashSet<>(Arrays.asList(columns));

						int synonymCount = conditionColumns.size();

						int matchCount = 0;

						for (String sKey : effectiveSynonymsMap.keySet()) {
							String synonym = effectiveSynonymsMap.get(sKey);
							synonym = "@" + synonym + "@";
							if (filterCondition.indexOf(synonym) > -1) {
								filterCondition = filterCondition.replaceAll(synonym, sKey);
								matchCount++;
							}
						}
						if (matchCount == synonymCount)
							modifyStatus = true;

						if (modifyStatus) {
							LOG.debug("\n====> modified right filterCondition: " + filterCondition);

							// Updating the filter condition in database
							LOG.info("\n====> Updating the right filter condition in database");
							updateStatus = globalRuleDAO.updateRightTemplateFilterConditionOfRuleTemplateMapping(
									templateId, globalRuleId, filterCondition);
						} else {
							LOG.error(
									"\n====> Failed to map the right filter condition with actual columns of template !!");
						}

						if (!modifyStatus || !updateStatus) {
							status = "failed";

							message = "Failed to update right filter condition in one or more associated Templates";

							mappingError = "Failed to update right filter condition for TemplateId[" + templateId
									+ "] and global rule[" + globalRuleId + "]";

							LOG.debug("\n====> " + mappingError);

							mappingErrors.put(mappingError);
						}

					}

					// Check if Rule catalog is enabled and update all the associated validations
					boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

					if (isRuleCatalogEnabled) {
						LOG.info("\n====> Updating all the associated validations rule catalog ..");
						for (long idData : templateIdList) {
							CompletableFuture.runAsync(() -> {
								ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
							});
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while updating global filter";
			e.printStackTrace();
		}

		LOG.debug("\n====>" + message);
		updateTemplateMappingObj.put("status", status);
		updateTemplateMappingObj.put("message", message);
		updateTemplateMappingObj.put("mappingErrors", mappingErrors);
		return updateTemplateMappingObj;
	}

	public JSONObject updateGlobalFilterInAssociatedTemplates(GlobalFilters globalFilter) {

		LOG.info("\n======> updateGlobalFilterInAssociatedTemplates - START <======");

		JSONObject updateTemplateMappingObj = new JSONObject();
		JSONArray mappingErrors = new JSONArray();
		String status = "success";
		String message = "Filter Condition is successfully updated in Rule Template Mapping";

		Set<Long> templateIdList = new HashSet<Long>();
		try {
			Integer domainId = globalFilter.getDomainId();
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			if (expandedSynonymMap != null && expandedSynonymMap.size() > 0) {

				SqlRowSet sqlRowSet = globalRuleDAO.getGlobalRulesByFilterId(globalFilter.getFilterId());

				if (sqlRowSet != null) {

					while (sqlRowSet.next()) {

						String filterCondition = globalFilter.getFilterCondition();
						long globalRuleId = sqlRowSet.getLong("idListColrules");
						long templateId = sqlRowSet.getLong("idData");
						String mappingError = "";
						boolean modifyStatus = false;
						boolean updateStatus = false;

						LOG.debug("\n====> Updating filter condition of global rule [" + globalRuleId
								+ "] for template with Id: " + templateId);

						LOG.debug("\n====> FilterCondition: " + filterCondition);

						if (globalRuleId <= 0l || templateId <= 0l)
							continue;

						// Add the template Id to list for updating associated validation catalogs
						templateIdList.add(templateId);

						// Get the effective synonyms for the template
						Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(templateId,
								expandedSynonymMap);
						LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

						// Mapping the filter condition with actual columns of template
						LOG.info("\n====> Update Filter condition");

						String[] columns = StringUtils.substringsBetween(filterCondition, "@", "@");

						Set<String> conditionColumns = new HashSet<>(Arrays.asList(columns));

						int synonymCount = conditionColumns.size();

						int matchCount = 0;

						for (String sKey : effectiveSynonymsMap.keySet()) {
							String synonym = effectiveSynonymsMap.get(sKey);
							synonym = "@" + synonym + "@";
							if (filterCondition.indexOf(synonym) > -1) {
								filterCondition = filterCondition.replaceAll(synonym, sKey);
								matchCount++;
							}
						}
						if (matchCount == synonymCount)
							modifyStatus = true;

						if (modifyStatus) {
							LOG.debug("\n====> modified filterCondition: " + filterCondition);

							// Updating the filter condition in database
							LOG.info("\n====> Updating the filter condition in database");
							updateStatus = globalRuleDAO.updateFilterConditionOfRuleTemplateMapping(templateId,
									globalRuleId, filterCondition);
						} else {
							LOG.error("\n====> Failed to map the filter condition with actual columns of template !!");
						}

						if (!modifyStatus || !updateStatus) {
							status = "failed";

							message = "Failed to update filter condition in one or more associated Templates";

							mappingError = "Failed to update filter condition for TemplateId[" + templateId
									+ "] and global rule[" + globalRuleId + "]";

							LOG.debug("\n====> " + mappingError);

							mappingErrors.put(mappingError);
						}

					}

					// Check if Rule catalog is enabled and update all the associated validations
					boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

					if (isRuleCatalogEnabled) {
						LOG.info("\n====> Updating all the associated validations rule catalog ..");
						for (long idData : templateIdList) {
							CompletableFuture.runAsync(() -> {
								ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
							});
						}
					}
				}
				// Updating Associated Templates for right filter condition
				try {

					JSONObject updateRightTemplateMappingObj = updateAssociatedTemplatesByRightFilterCondition(
							globalFilter);

					JSONArray rightTemplateMappingErrors = updateRightTemplateMappingObj.getJSONArray("mappingErrors");

					String rightTemplateStatus = updateRightTemplateMappingObj.getString("status");
					String rightTemplateMessage = updateRightTemplateMappingObj.getString("message");

					for (int i = 0; i < rightTemplateMappingErrors.length(); i++)
						mappingErrors.put(rightTemplateMappingErrors.get(i));
					if (rightTemplateStatus.trim().equalsIgnoreCase("failed")) {
						status = "failed";
						message = rightTemplateMessage;
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while updating global filter";
			e.printStackTrace();
		}

		LOG.debug("\n====>" + message);
		updateTemplateMappingObj.put("status", status);
		updateTemplateMappingObj.put("message", message);
		updateTemplateMappingObj.put("mappingErrors", mappingErrors);

		return updateTemplateMappingObj;
	}

	public JSONObject updateGlobalRuleMappingInAssociatedTemplates(ListColGlobalRules listColGlobalRules) {

		LOG.info("\n======> updateGlobalRuleMappingInAssociatedTemplates - START <======");

		JSONObject responeJson = new JSONObject();
		JSONArray mappingErrors = new JSONArray();

		String status = "success";
		String message = "Global Rule is successfully updated in Rule Template Mapping";

		try {
			long globalRuleId = listColGlobalRules.getIdListColrules();
			Integer domainId = listColGlobalRules.getDomain_id();
			String ruleType = listColGlobalRules.getRuleType();
			String ruleExpression = listColGlobalRules.getExpression();
			String matchingRules = listColGlobalRules.getMatchingRules();
			Integer filterId = listColGlobalRules.getFilterId();
			Integer rightTemplateFilterId = listColGlobalRules.getRightTemplateFilterId();
			long rightTemplateId = listColGlobalRules.getIdRightData();

			LOG.info("\n====> Global Rule details:");
			LOG.debug("globalRuleId: " + globalRuleId);
			LOG.debug("domainId: " + domainId);
			LOG.debug("ruleType: " + ruleType);
			LOG.debug("ruleExpression: " + ruleExpression);
			LOG.debug("matchingRules: " + matchingRules);
			LOG.debug("rightTemplateId: " + rightTemplateId);
			LOG.debug("rightTemplateFilterId: " + rightTemplateFilterId);

			// Get global filter details
			String filterCondition = "";
			if (filterId != null && filterId > 0) {
				GlobalFilters globalFilter = globalRuleDAO.getGlobalFilterById(filterId);
				filterCondition = globalFilter.getFilterCondition();
			}
			LOG.debug("filterCondition: " + filterCondition);

			// Get Right Template global filter details
			String rightTemplateFilterCondition = "";
			if (rightTemplateFilterId != null && rightTemplateFilterId > 0) {
				GlobalFilters rt_globalFilter = globalRuleDAO.getGlobalFilterById(rightTemplateFilterId);
				rightTemplateFilterCondition = rt_globalFilter.getFilterCondition();
			}

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			if (expandedSynonymMap != null && expandedSynonymMap.size() > 0) {

				// Get list of templateIds linked with the global rule
				List<Long> templateIds = globalRuleDAO.getTemplateIdsForGlobalRuleId(globalRuleId);

				if (templateIds != null && templateIds.size() > 0) {

					for (long templateId : templateIds) {

						Set<String> anchorColumns = new HashSet<>();
						LOG.debug("\n====> Updating rule for template with Id: " + templateId);

						if (globalRuleId <= 0l || templateId <= 0l)
							continue;

						// Get effective synonyms of the Template
						Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(templateId,
								expandedSynonymMap);

						LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

						if (filterCondition != null && !filterCondition.trim().isEmpty()
								&& (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
										|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
										|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
										|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
										|| ruleType
												.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK))) {

							boolean isModified = false;
							boolean updateStatus = false;
							String modifiedFilterCondition = filterCondition;

							// Update Filter condition
							LOG.info("\n====> Update Filter condition");
							for (String sKey : effectiveSynonymsMap.keySet()) {
								String synonym = effectiveSynonymsMap.get(sKey);
								synonym = "@" + synonym + "@";
								if (modifiedFilterCondition.indexOf(synonym) > -1) {
									modifiedFilterCondition = modifiedFilterCondition.replaceAll(synonym, sKey);
									anchorColumns.add(sKey);
									isModified = true;
								}
							}
							String[] unmatched = StringUtils.substringsBetween(modifiedFilterCondition, "@", "@");
							List<String> leftUnmatchedSynonyms = unmatched == null ? new ArrayList<>()
									: Arrays.asList(unmatched);
							if (leftUnmatchedSynonyms.size() > 0) {
								List<String> columnsList = validationCheckDAO
										.getDisplayNamesFromListDataDefinition(templateId);
								Set<String> unmatchedSet = new HashSet<>();
								unmatchedSet.addAll(Arrays
										.asList(StringUtils.substringsBetween(modifiedFilterCondition, "@", "@")));
								for (String unmatchedSynonym : unmatchedSet) {
									List<SynonymLibrary> synonymLibraries = globalRuleDAO
											.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
									if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
										for (SynonymLibrary library : synonymLibraries) {
											List<String> possibleNames = Arrays
													.stream(library.getPossibleNames().split(","))
													.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
											for (String possibleName : possibleNames) {
												if (columnsList.contains(possibleName)) {
													String sSynonym = "@" + unmatchedSynonym + "@";
													if (modifiedFilterCondition.indexOf(sSynonym) > -1) {
														anchorColumns.add(possibleName);
														modifiedFilterCondition = modifiedFilterCondition
																.replaceAll(sSynonym, possibleName);
														isModified = true;
														break;
													}
												}
											}
											String[] extraSynonyms = StringUtils
													.substringsBetween(modifiedFilterCondition, "@", "@");
											if (extraSynonyms == null || extraSynonyms.length == 0) {
												break;
											}
										}
									}

								}
							}

							if (isModified) {
								LOG.debug("\n====> modified filterCondition: " + modifiedFilterCondition);

								// Updating the filter condition in database
								LOG.info("\n====> Updating the filter condition in database");

								updateStatus = globalRuleDAO.updateFilterConditionOfRuleTemplateMapping(templateId,
										globalRuleId, modifiedFilterCondition);
							}

							if (!isModified || !updateStatus) {
								status = "failed";

								message = "Failed to update global rule mapping in one or more associated Templates";

								String fc_mappingError = "Failed to update filter condition for TemplateId["
										+ templateId + "] and global rule[" + globalRuleId + "]";

								LOG.debug("\n====> " + fc_mappingError);

								mappingErrors.put(fc_mappingError);
							}
						}

						// Update Right Template Filter condition
						if (rightTemplateId > 0 && rightTemplateFilterCondition != null
								&& !rightTemplateFilterCondition.trim().isEmpty()
								&& ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)) {

							boolean isModified = false;
							boolean updateStatus = false;
							String modifiedRightTemplateFilterCondition = rightTemplateFilterCondition;

							// Get effective synonyms of the Right Template
							Map<String, String> rt_effectiveSynonymsMap = getEffectiveSynonymsForTemplate(
									rightTemplateId, expandedSynonymMap);

							LOG.debug("\n====> effectiveSynonymsMap of Right Template: " + effectiveSynonymsMap);

							// Update Filter condition
							LOG.info("\n====> Update Right Template Filter condition");
							for (String sKey : rt_effectiveSynonymsMap.keySet()) {
								String synonym = rt_effectiveSynonymsMap.get(sKey);
								synonym = "@" + synonym + "@";
								if (modifiedRightTemplateFilterCondition.indexOf(synonym) > -1) {
									modifiedRightTemplateFilterCondition = modifiedRightTemplateFilterCondition
											.replaceAll(synonym, sKey);
									isModified = true;
								}
							}
							String[] unmatched = StringUtils.substringsBetween(modifiedRightTemplateFilterCondition,
									"@", "@");
							List<String> rightUnmatchedSynonyms = unmatched == null ? new ArrayList<>()
									: Arrays.asList(unmatched);
							if (rightUnmatchedSynonyms.size() > 0) {
								List<String> columnsList = validationCheckDAO
										.getDisplayNamesFromListDataDefinition(rightTemplateId);
								Set<String> unmatchedSet = new HashSet<>();
								unmatchedSet.addAll(Arrays.asList(
										StringUtils.substringsBetween(modifiedRightTemplateFilterCondition, "@", "@")));
								for (String unmatchedSynonym : unmatchedSet) {
									List<SynonymLibrary> synonymLibraries = globalRuleDAO
											.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
									if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
										for (SynonymLibrary library : synonymLibraries) {
											List<String> possibleNames = Arrays
													.stream(library.getPossibleNames().split(","))
													.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
											for (String possibleName : possibleNames) {
												if (columnsList.contains(possibleName)) {
													String sSynonym = "@" + unmatchedSynonym + "@";
													if (modifiedRightTemplateFilterCondition.indexOf(sSynonym) > -1) {
														modifiedRightTemplateFilterCondition = modifiedRightTemplateFilterCondition
																.replaceAll(sSynonym, possibleName);
														isModified = true;
														break;
													}
												}
											}
											String[] extraSynonyms = StringUtils
													.substringsBetween(modifiedRightTemplateFilterCondition, "@", "@");
											if (extraSynonyms == null || extraSynonyms.length == 0) {
												break;
											}
										}
									}

								}
							}

							if (isModified) {
								LOG.debug("\n====> modified Right Template filterCondition: "
										+ modifiedRightTemplateFilterCondition);

								// Updating the filter condition in database
								LOG.info("\n====> Updating the right template filter condition in database");

								updateStatus = globalRuleDAO.updateRightTemplateFilterConditionOfRuleTemplateMapping(
										templateId, globalRuleId, modifiedRightTemplateFilterCondition);
							}

							if (!isModified || !updateStatus) {
								status = "failed";

								message = "Failed to update global rule mapping in one or more associated Templates";

								String fc_mappingError = "Failed to update right template filter condition for TemplateId["
										+ templateId + "] and global rule[" + globalRuleId + "]";

								LOG.debug("\n====> " + fc_mappingError);

								mappingErrors.put(fc_mappingError);
							}
						}

						if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {

							boolean isModified = false;
							boolean updateStatus = false;

							String[] aMatchPair = matchingRules.split("=");
							String left_matchingRule = aMatchPair[0].trim();
							String modifiedMatchingRule = "";

							// Update Matching rule
							LOG.info("\n====> Update Matching rule");
							for (String sKey : effectiveSynonymsMap.keySet()) {
								String synonym = effectiveSynonymsMap.get(sKey);
								synonym = "@" + synonym + "@";
								if (left_matchingRule.indexOf(synonym) > -1) {
									left_matchingRule = left_matchingRule.replaceAll(synonym, "originalDf." + sKey);
									anchorColumns.add(sKey);
									isModified = true;
								}
							}
							String[] unmatched = StringUtils.substringsBetween(left_matchingRule, "@", "@");
							List<String> leftUnmatchedSynonyms = unmatched == null ? new ArrayList<>()
									: Arrays.asList(unmatched);
							if (leftUnmatchedSynonyms.size() > 0) {
								List<String> columnsList = validationCheckDAO
										.getDisplayNamesFromListDataDefinition(templateId);
								Set<String> unmatchedSet = new HashSet<>();
								unmatchedSet.addAll(
										Arrays.asList(StringUtils.substringsBetween(left_matchingRule, "@", "@")));
								for (String unmatchedSynonym : unmatchedSet) {
									List<SynonymLibrary> synonymLibraries = globalRuleDAO
											.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
									if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
										for (SynonymLibrary library : synonymLibraries) {
											List<String> possibleNames = Arrays
													.stream(library.getPossibleNames().split(","))
													.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
											for (String possibleName : possibleNames) {
												if (columnsList.contains(possibleName)) {
													String sSynonym = "@" + unmatchedSynonym + "@";
													if (left_matchingRule.indexOf(sSynonym) > -1) {
														left_matchingRule = left_matchingRule.replaceAll(sSynonym,
																"originalDf." + possibleName);
														anchorColumns.add(possibleName);
														isModified = true;
														break;
													}
												}
											}
											String[] extraSynonyms = StringUtils.substringsBetween(left_matchingRule,
													"@", "@");
											if (extraSynonyms == null || extraSynonyms.length == 0) {
												break;
											}
										}
									}

								}
							}
							if (isModified) {
								if (aMatchPair.length > 1) {
									modifiedMatchingRule = left_matchingRule + "=" + aMatchPair[1].trim();
								}
								LOG.debug("\n====> modified matchingRules: " + modifiedMatchingRule);

								// Updating the MatchingRules in database
								LOG.info("\n====> Updating the MatchingRules in database");

								updateStatus = globalRuleDAO.updateMatchingRulesOfRuleTemplateMapping(templateId,
										globalRuleId, modifiedMatchingRule);
							}

							if (!isModified || !updateStatus) {
								status = "failed";

								message = "Failed to update global rule mapping in one or more associated Templates";

								String mr_mappingError = "Failed to update MatchingRules for TemplateId[" + templateId
										+ "] and global rule[" + globalRuleId + "]";

								LOG.debug("\n====> " + mr_mappingError);

								mappingErrors.put(mr_mappingError);
							}
						}

						if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
								|| ruleExpression.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

							boolean isModified = false;
							boolean updateStatus = false;
							String modifiedRuleExpression = ruleExpression;

							// Update Rule expression
							LOG.info("\n====> Update Rule expression");

							for (String sKey : effectiveSynonymsMap.keySet()) {
								String synonym = effectiveSynonymsMap.get(sKey);
								synonym = "@" + synonym + "@";
								if (modifiedRuleExpression.indexOf(synonym) > -1) {
									if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE))
										modifiedRuleExpression = modifiedRuleExpression.replaceAll(synonym,
												"originalDf." + sKey);
									else
										modifiedRuleExpression = modifiedRuleExpression.replaceAll(synonym, sKey);
									anchorColumns.add(sKey);
									isModified = true;
								}
							}
							String[] unmatched = StringUtils.substringsBetween(modifiedRuleExpression, "@", "@");
							List<String> leftUnmatchedSynonyms = unmatched == null ? new ArrayList<>()
									: Arrays.asList(unmatched);
							if (leftUnmatchedSynonyms.size() > 0) {
								List<String> columnsList = validationCheckDAO
										.getDisplayNamesFromListDataDefinition(templateId);
								Set<String> unmatchedSet = new HashSet<>();
								unmatchedSet.addAll(
										Arrays.asList(StringUtils.substringsBetween(modifiedRuleExpression, "@", "@")));
								for (String unmatchedSynonym : unmatchedSet) {
									List<SynonymLibrary> synonymLibraries = globalRuleDAO
											.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
									if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
										for (SynonymLibrary library : synonymLibraries) {
											List<String> possibleNames = Arrays
													.stream(library.getPossibleNames().split(","))
													.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
											for (String possibleName : possibleNames) {
												if (columnsList.contains(possibleName)) {
													String sSynonym = "@" + unmatchedSynonym + "@";
													if (modifiedRuleExpression.indexOf(sSynonym) > -1) {
														if (ruleType.equalsIgnoreCase(
																DatabuckConstants.CROSSREFERENTIAL_RULE))
															modifiedRuleExpression = modifiedRuleExpression
																	.replaceAll(sSynonym, "originalDf." + possibleName);
														else
															modifiedRuleExpression = modifiedRuleExpression
																	.replaceAll(sSynonym, possibleName);
														anchorColumns.add(possibleName);
														isModified = true;
														break;
													}
												}
											}
											String[] extraSynonyms = StringUtils
													.substringsBetween(modifiedRuleExpression, "@", "@");
											if (extraSynonyms == null || extraSynonyms.length == 0) {
												break;
											}
										}
									}

								}
							}

							if (isModified) {
								LOG.debug("\n====> modified ruleExpression: " + modifiedRuleExpression);

								// Updating the RuleExpression in database
								LOG.info("\n====> Updating the RuleExpression in database");

								updateStatus = globalRuleDAO.updateRuleExpressionOfRuleTemplateMapping(templateId,
										globalRuleId, modifiedRuleExpression);
							}

							if (!isModified || !updateStatus) {
								status = "failed";

								message = "Failed to update global rule mapping in one or more associated Templates";

								String re_mappingError = "Failed to update RuleExpression for TemplateId[" + templateId
										+ "] and global rule[" + globalRuleId + "]";

								LOG.debug("\n====> " + re_mappingError);

								mappingErrors.put(re_mappingError);
							}
						}

						if (anchorColumns != null && !anchorColumns.isEmpty()) {
							String anchorCol = String.join(",", anchorColumns);
							boolean updateStatus = globalRuleDAO.updateAnchorColumnOfRuleTemplateMapping(templateId,
									globalRuleId, anchorCol);
							if (!updateStatus) {
								status = "failed";
								message = "Failed to update global rule anchor columns in one or more associated Templates";
								String re_mappingError = "Failed to update Anchor Columns for TemplateId[" + templateId
										+ "] and global rule[" + globalRuleId + "]";
								LOG.debug("\n====> " + re_mappingError);
								mappingErrors.put(re_mappingError);
							}
							updateStatus = ruleCatalogDao.updateNullFilterColumnsOfRuleTemplateMapping(templateId,
									globalRuleId, anchorCol);
							if (!updateStatus) {
								status = "failed";
								message = "Failed to update null filter columns in one or more associated Templates";
								String re_mappingError = "Failed to update null filter columns for TemplateId["
										+ templateId + "] and global rule[" + globalRuleId + "]";
								LOG.debug("\n====> " + re_mappingError);
								mappingErrors.put(re_mappingError);
							}
						}

					}

					// Check if Rule catalog is enabled and update all the associated validations
					// rule catalogs
					LOG.info("\n====> Updating all the associated validations rule catalog ..");

					ruleCatalogService.updateRuleCatalogsForGlobalRuleThresholdChange(globalRuleId);

				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while updating global rule mapping in template";
			e.printStackTrace();
		}

		responeJson.put("status", status);
		responeJson.put("message", message);
		responeJson.put("mappingErrors", mappingErrors);

		return responeJson;
	}

	public JSONObject linkGlobalRuleToTemplateById(long templateId, long globalRuleId) {

		LOG.info("\n======> linkGlobalRuleToTemplateById - START <======");

		JSONObject responeJson = new JSONObject();

		String status = "success";
		String message = "Global Rule is successfully linked to Template";

		try {
			// Get Global Rule details
			ListColGlobalRules listColGlobalRules = globalRuleDAO.getGlobalRuleById(globalRuleId);

			Integer domainId = listColGlobalRules.getDomain_id();
			String ruleType = listColGlobalRules.getRuleType();
			Integer filterId = listColGlobalRules.getFilterId();
			Integer rightTemplateFilterId = listColGlobalRules.getRightTemplateFilterId();
			Long rightTemplateId = listColGlobalRules.getIdRightData();

			// Get RuleExpression
			String ruleExpression = listColGlobalRules.getExpression();
			ruleExpression = (ruleExpression != null && !ruleExpression.trim().isEmpty())
					? ruleExpression = ruleExpression.trim()
					: "";

			// Get MatchingRules
			String matchingRules = listColGlobalRules.getMatchingRules();
			matchingRules = (matchingRules != null && !matchingRules.trim().isEmpty())
					? matchingRules = matchingRules.trim()
					: "";

			// Get global filter details
			String filterCondition = "";
			if (filterId != null && filterId > 0) {
				GlobalFilters globalFilter = globalRuleDAO.getGlobalFilterById(filterId);
				filterCondition = globalFilter.getFilterCondition();

				filterCondition = (filterCondition != null && !filterCondition.trim().isEmpty())
						? filterCondition.trim()
						: "";
			}

			// Get global filter details
			String rightTemplateFilterCondition = "";
			if (rightTemplateFilterId != null && rightTemplateFilterId > 0) {
				GlobalFilters globalFilter = globalRuleDAO.getGlobalFilterById(rightTemplateFilterId);
				rightTemplateFilterCondition = globalFilter.getFilterCondition();

				rightTemplateFilterCondition = (rightTemplateFilterCondition != null
						&& !rightTemplateFilterCondition.trim().isEmpty()) ? rightTemplateFilterCondition.trim() : "";
			}

			LOG.info("\n====> Global Rule details:");
			LOG.debug("globalRuleId: " + globalRuleId);
			LOG.debug("domainId: " + domainId);
			LOG.debug("ruleType: " + ruleType);
			LOG.debug("ruleExpression: " + ruleExpression);
			LOG.debug("matchingRules: " + matchingRules);
			LOG.debug("filterCondition: " + filterCondition);
			LOG.debug("rightTemplateFilterCondition: " + rightTemplateFilterCondition);
			LOG.debug("rightTemplateId: " + rightTemplateId);

			Set<String> anchorColumnsList = new HashSet<String>();

			// Variables to hold updated values
			String modifiedRuleExpression = ruleExpression;
			String modifiedMatchingRule = matchingRules;
			String modifiedFilterCondition = filterCondition;
			String modifiedRightTemplateFilterCondition = rightTemplateFilterCondition;

			boolean isRuleExpressionValid = false;
			boolean isMatchingRulesValid = false;
			boolean isFilterConditionValid = false;
			boolean isRightTemplateFilterConditionValid = false;
			boolean isRuleApplicable = false;

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			if (expandedSynonymMap != null && expandedSynonymMap.size() > 0) {

				LOG.debug("\n====> linking rule to template: " + templateId);

				// Get effective synonyms of the Template
				Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(templateId, expandedSynonymMap);

				LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

				if (filterCondition != null && !filterCondition.trim().isEmpty()
						&& (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
								|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK))) {

					boolean isModified = false;

					// Update Filter condition
					LOG.info("\n====> Update Filter condition");
					for (String sKey : effectiveSynonymsMap.keySet()) {
						String synonym = effectiveSynonymsMap.get(sKey);
						synonym = "@" + synonym + "@";
						if (modifiedFilterCondition.indexOf(synonym) > -1) {
							modifiedFilterCondition = modifiedFilterCondition.replaceAll(synonym, sKey);

							// Add the column name to anchor columns list
							anchorColumnsList.add(sKey);

							isModified = true;
						}
					}

					String[] unmatchedSynonyms = StringUtils.substringsBetween(modifiedFilterCondition, "@", "@");
					if(unmatchedSynonyms!=null && unmatchedSynonyms.length>0){
						List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(templateId);
						Set<String> unmatchedSet = new HashSet<>();
						unmatchedSet.addAll(Arrays.asList(unmatchedSynonyms));
						for (String unmatchedSynonym : unmatchedSet) {
							List<SynonymLibrary> synonymLibraries = globalRuleDAO.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
							if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
								for (SynonymLibrary library : synonymLibraries) {
									List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
											.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
									for (String possibleName : possibleNames) {
										if (columnsList.contains(possibleName)) {
											String sSynonym = "@" + unmatchedSynonym + "@";
											if (modifiedFilterCondition.indexOf(sSynonym) > -1) {
												isModified = true;
												anchorColumnsList.add(possibleName);
												modifiedFilterCondition = modifiedFilterCondition.replaceAll(sSynonym, possibleName);
												break;
											}
										}
									}
									String[] extraSynonyms = StringUtils.substringsBetween(modifiedFilterCondition, "@", "@");
									if (extraSynonyms == null || extraSynonyms.length == 0) {
										break;
									}
								}
							}

						}
					}

					unmatchedSynonyms = StringUtils.substringsBetween(modifiedFilterCondition, "@", "@");
					if (isModified && (unmatchedSynonyms==null || unmatchedSynonyms.length<=0)) {
						LOG.debug("\n====> modified filterCondition: " + modifiedFilterCondition);
						isFilterConditionValid = true;
					}
				} else
					isFilterConditionValid = true;

				if (rightTemplateFilterCondition != null && !rightTemplateFilterCondition.trim().isEmpty()
						&& ((ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE))
						||(ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)))) {

					boolean isModified = false;

					// Get effective synonyms of the Right Template
					Map<String, String> r_effectiveSynonymsMap = getEffectiveSynonymsForTemplate(rightTemplateId,
							expandedSynonymMap);

					LOG.debug("\n====> effectiveSynonymsMap of Right Template: " + effectiveSynonymsMap);

					if (r_effectiveSynonymsMap != null && r_effectiveSynonymsMap.size() > 0) {
						// Update Right Template Filter condition
						LOG.debug("\n====> Update Right Template Filter condition");
						for (String sKey : r_effectiveSynonymsMap.keySet()) {
							String synonym = r_effectiveSynonymsMap.get(sKey);
							synonym = "@" + synonym + "@";
							if (modifiedRightTemplateFilterCondition.indexOf(synonym) > -1) {
								modifiedRightTemplateFilterCondition = modifiedRightTemplateFilterCondition
										.replaceAll(synonym, sKey);

								// Add the column name to anchor columns list
//								anchorColumnsList.add(sKey);
								isModified = true;
							}
						}
					}
					String[] unmatchedSynonyms = StringUtils.substringsBetween(modifiedRightTemplateFilterCondition, "@", "@");
					if(unmatchedSynonyms!=null && unmatchedSynonyms.length>0){
						List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(rightTemplateId);
						Set<String> unmatchedSet = new HashSet<>();
						unmatchedSet.addAll(Arrays.asList(unmatchedSynonyms));
						for (String unmatchedSynonym : unmatchedSet) {
							List<SynonymLibrary> synonymLibraries = globalRuleDAO.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
							if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
								for (SynonymLibrary library : synonymLibraries) {
									List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
											.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
									for (String possibleName : possibleNames) {
										if (columnsList.contains(possibleName)) {
											String sSynonym = "@" + unmatchedSynonym + "@";
											if (modifiedRightTemplateFilterCondition.indexOf(sSynonym) > -1) {
												isModified = true;
												modifiedRightTemplateFilterCondition = modifiedRightTemplateFilterCondition.replaceAll(sSynonym, possibleName);
												break;
											}
										}
									}
									String[] extraSynonyms = StringUtils.substringsBetween(modifiedRightTemplateFilterCondition, "@", "@");
									if (extraSynonyms == null || extraSynonyms.length == 0) {
										break;
									}
								}
							}

						}
					}
					unmatchedSynonyms = StringUtils.substringsBetween(modifiedRightTemplateFilterCondition, "@", "@");
					if (isModified && (unmatchedSynonyms==null || unmatchedSynonyms.length<=0)) {
						LOG.debug("\n====> modified Right template filterCondition: "+ modifiedRightTemplateFilterCondition);
						isRightTemplateFilterConditionValid = true;
					}
				} else
					isRightTemplateFilterConditionValid = true;

				if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {

					boolean isModified = false;

					String[] aMatchPair = matchingRules.split("=");
					String left_matchingRule = aMatchPair[0].trim();

					// Update Matching rule
					LOG.info("\n====> Update Matching rule");
					for (String sKey : effectiveSynonymsMap.keySet()) {
						String synonym = effectiveSynonymsMap.get(sKey);
						synonym = "@" + synonym + "@";
						if (left_matchingRule.indexOf(synonym) > -1) {
							left_matchingRule = left_matchingRule.replaceAll(synonym, "originalDf." + sKey);

							// Add the column name to anchor columns list
							anchorColumnsList.add(sKey);
							isModified = true;
						}
					}

					String[] unmatchedSynonyms = StringUtils.substringsBetween(left_matchingRule, "@", "@");
					if(unmatchedSynonyms!=null && unmatchedSynonyms.length>0){
						List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(templateId);
						Set<String> unmatchedSet = new HashSet<>();
						unmatchedSet.addAll(Arrays.asList(unmatchedSynonyms));
						for (String unmatchedSynonym : unmatchedSet) {
							List<SynonymLibrary> synonymLibraries = globalRuleDAO.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
							if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
								for (SynonymLibrary library : synonymLibraries) {
									List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
											.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
									for (String possibleName : possibleNames) {
										if (columnsList.contains(possibleName)) {
											String sSynonym = "@" + unmatchedSynonym + "@";
											if (left_matchingRule.indexOf(sSynonym) > -1) {
												anchorColumnsList.add(possibleName);
												isModified = true;
												if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
														|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
													left_matchingRule = left_matchingRule.replaceAll(sSynonym, "originalDf." + possibleName);
												else
													left_matchingRule = left_matchingRule.replaceAll(sSynonym, possibleName);
												break;
											}
										}
									}
									String[] extraSynonyms = StringUtils.substringsBetween(left_matchingRule,"@", "@");
									if (extraSynonyms == null || extraSynonyms.length == 0) {
										break;
									}
								}
							}

						}
					}
					unmatchedSynonyms = StringUtils.substringsBetween(left_matchingRule, "@", "@");
					if (isModified && (unmatchedSynonyms==null || unmatchedSynonyms.length<=0)) {
						modifiedMatchingRule = left_matchingRule + "=" + aMatchPair[1].trim();
						LOG.debug("\n====> modified matchingRules: " + modifiedMatchingRule);

						isMatchingRulesValid = true;
					}

				} else
					isMatchingRulesValid = true;

				if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
						|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

					boolean isModified = false;

					// Update Rule expression
					LOG.info("\n====> Update Rule expression");

					for (String sKey : effectiveSynonymsMap.keySet()) {
						String synonym = effectiveSynonymsMap.get(sKey);
						synonym = "@" + synonym + "@";
						if (modifiedRuleExpression.indexOf(synonym) > -1) {
							if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
									|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
								modifiedRuleExpression = modifiedRuleExpression.replaceAll(synonym,
										"originalDf." + sKey);
							else
								modifiedRuleExpression = modifiedRuleExpression.replaceAll(synonym, sKey);

							// Add the column name to anchor columns list
							anchorColumnsList.add(sKey);

							isModified = true;
						}
					}


					String[] unmatchedSynonyms = StringUtils.substringsBetween(modifiedRuleExpression, "@", "@");
					if(unmatchedSynonyms!=null && unmatchedSynonyms.length>0){
						List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(templateId);
						Set<String> unmatchedSet = new HashSet<>();
						unmatchedSet.addAll(Arrays.asList(unmatchedSynonyms));
						for (String unmatchedSynonym : unmatchedSet) {
							List<SynonymLibrary> synonymLibraries = globalRuleDAO.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
							if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
								for (SynonymLibrary library : synonymLibraries) {
									List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
											.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
									for (String possibleName : possibleNames) {
										if (columnsList.contains(possibleName)) {
											String sSynonym = "@" + unmatchedSynonym + "@";
											if (modifiedRuleExpression.indexOf(sSynonym) > -1) {
												anchorColumnsList.add(possibleName);
												isModified = true;
												if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
														|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
													modifiedRuleExpression = modifiedRuleExpression.replaceAll(sSynonym, "originalDf." + possibleName);
												else
													modifiedRuleExpression = modifiedRuleExpression.replaceAll(sSynonym, possibleName);
												break;
											}
										}
									}
									String[] extraSynonyms = StringUtils.substringsBetween(modifiedRuleExpression,"@", "@");
									if (extraSynonyms == null || extraSynonyms.length == 0) {
										break;
									}
								}
							}

						}
					}
					unmatchedSynonyms = StringUtils.substringsBetween(modifiedRuleExpression, "@", "@");
					if (isModified && (unmatchedSynonyms==null || unmatchedSynonyms.length<=0)) {
						LOG.debug("\n====> modified ruleExpression: " + modifiedRuleExpression);
						isRuleExpressionValid = true;
					}
				} else
					isRuleExpressionValid = true;

				if (isRuleExpressionValid && isMatchingRulesValid && isFilterConditionValid
						&& isRightTemplateFilterConditionValid)
					isRuleApplicable = true;

				String anchorColumns = String.join(",", anchorColumnsList);

				if (isRuleApplicable) {
					boolean updateStatus = false;

					listColGlobalRules.setFilterCondition(modifiedFilterCondition);
					listColGlobalRules.setMatchingRules(modifiedMatchingRule);
					listColGlobalRules.setExpression(modifiedRuleExpression);
					listColGlobalRules.setRightTemplateFilterCondition(modifiedRightTemplateFilterCondition);

					// Check if the rule is mapped or not
					boolean isRuleLinked = listDataSourceDao.isGlobalruleLinkedToTemplate(templateId, globalRuleId);
					LOG.debug("\n====> Is the rule already linked: " + isRuleLinked);

					// Check if it is already linked, Update it or insert into rule_Template_Mapping
					if (isRuleLinked)
						updateStatus = listDataSourceDao.updateRuleTemplateMapping(templateId, listColGlobalRules,
								anchorColumns);
					else
						updateStatus = listDataSourceDao.insertToRuleTemplateMapping(templateId, listColGlobalRules,
								anchorColumns);

					if (!updateStatus) {
						LOG.debug("\n====> Failed to link global rule [" + globalRuleId + "] to Template");
						status = "failed";
						message = "Failed to link global rule [" + globalRuleId + "] to Template";
					}
				} else {
					status = "failed";
					message = "Failed to link global rule [" + globalRuleId + "] to Template";
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Failed to link global rule [" + globalRuleId + "] to Template";
			e.printStackTrace();
		}

		responeJson.put("status", status);
		responeJson.put("message", message);
		return responeJson;
	}

	public JSONObject getEligibleGlobalThresholdsForTemplate(long idData) {

		List<HashMap<String, String>> eligibleThresholdsList = null;

		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get enterprise domainId
			Integer enterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			// Get effective synonyms of the Template
			Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);

			LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

			// Get available global thresholds for domainId and enterprise domainId
			String domainIdList = "" + enterpriseDomainId + "," + domainId;
			List<HashMap<String, String>> allGlobalThresholds = globalRuleDAO
					.getAvailableGlobalThresholds(domainIdList);

			// Get column names of templateId
			List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);

			// Get applicable global thresholds for the template
			eligibleThresholdsList = getApplicableThresholds(allGlobalThresholds, columnsList, effectiveSynonymsMap);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("DataSet", eligibleThresholdsList);

		return responseJson;

	}

	public JSONObject getEligibleReferenceRulesForTemplate(long idData) {

		List<HashMap<String, String>> eligibleReferenceRulesList = null;

		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			long idDataSchema = listDataSource.getIdDataSchema();

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get enterprise domainId
			Integer enterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			// Get effective synonyms of the Template
			Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);

			LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

			// Get table name
			listDataAccess listDataAccess = listDataSourceDao.getListDataAccess(idData);
			String tableName = listDataAccess.getFolderName();

			LOG.debug("\n====> idDataSchema: " + idDataSchema);
			LOG.debug("\n====> tableName: " + tableName);

			// Get available reference rules for domainId and enterprise domainId
			String domainIdList = "" + enterpriseDomainId + "," + domainId;
			List<HashMap<String, String>> allReferenceRules = globalRuleDAO
					.getAvailableReferenceRulesForTemplate(domainIdList, idDataSchema, tableName, idData);

			// Get column names of templateId
			List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);

			// Get applicable reference rules for the template
			eligibleReferenceRulesList = getApplicableReferenceRules(allReferenceRules, columnsList,
					effectiveSynonymsMap, idData);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("DataSet", eligibleReferenceRulesList);

		return responseJson;

	}

	public JSONObject getEligibleGlobalRulesForTemplate(long idData) {

		List<HashMap<String, String>> eligibleRulesList = null;

		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get enterprise domainId
			Integer enterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			// Get effective synonyms of the Template
			Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);

			LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

			// Get available global rules for domainId and enterprise domainId
			String domainIdList = "" + enterpriseDomainId + "," + domainId;
			List<HashMap<String, String>> allGlobalRules = globalRuleDAO.getAvailableGlobalRules(domainIdList);

			// Get column names of templateId
			List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);

			// Get the applicable global rules from all the rules
			eligibleRulesList = getApplicableOrNonApplicableGlobalRules(allGlobalRules, columnsList,
					effectiveSynonymsMap, expandedSynonymMap, true, idData, domainId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("DataSet", eligibleRulesList);

		return responseJson;
	}

	public JSONObject getNonEligibleGlobalRulesForTemplate(long idData) {

		List<HashMap<String, String>> nonEligibleRulesList = null;

		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get enterprise domainId
			Integer enterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			// Get effective synonyms of the Template
			Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);

			LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

			// Get available global rules for domainId and enterprise domainId
			String domainIdList = "" + enterpriseDomainId + "," + domainId;
			List<HashMap<String, String>> allGlobalRules = globalRuleDAO.getAvailableGlobalRules(domainIdList);

			// Get column names of templateId
			List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);

			// Get the list of global rules which are not applicable for the template
			nonEligibleRulesList = getApplicableOrNonApplicableGlobalRules(allGlobalRules, columnsList,
					effectiveSynonymsMap, expandedSynonymMap, false, idData, domainId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("DataSet", nonEligibleRulesList);

		return responseJson;
	}

	public List<HashMap<String, String>> getApplicableOrNonApplicableGlobalRules(
			List<HashMap<String, String>> aGlobalRules, List<String> aTableColumns,
			Map<String, String> oEffectiveSynonyms, Map<String, String> expandedSynonymMap, boolean fetchEligibleRules,
			long idData, int domainId) {

		List<HashMap<String, String>> eligibleRulesList = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, String>> nonEligibleRulesList = new ArrayList<HashMap<String, String>>();

		if (aGlobalRules != null) {
			HashMap<String, String> nullFilterColumnsList = new HashMap<>();
			if (fetchEligibleRules) {
				nullFilterColumnsList = globalRuleDAO.getNullFilterColumnsForTemplate(idData);
			}

			for (HashMap<String, String> glbl_rule : aGlobalRules) {
				try {

					// Get Rule Type
					String ruleType = glbl_rule.get("RuleType");

					// Get RuleExpression
					String ruleExpression = glbl_rule.get("Expression");
					ruleExpression = (ruleExpression != null && !ruleExpression.trim().isEmpty())
							? ruleExpression.trim()
							: "";

					// Get MatchingRules
					String matchingRules = glbl_rule.get("MatchingRules");
					matchingRules = (matchingRules != null && !matchingRules.trim().isEmpty()) ? matchingRules.trim()
							: "";

					// Get FilterCondition
					String filterCondition = glbl_rule.get("FilterCondition");
					filterCondition = (filterCondition != null && !filterCondition.trim().isEmpty())
							? filterCondition.trim()
							: "";

					// Get Right Template FilterCondition
					String rightTemplatefilterCondition = glbl_rule.get("RightTemplateFilterCondition");
					rightTemplatefilterCondition = (rightTemplatefilterCondition != null
							&& !rightTemplatefilterCondition.trim().isEmpty()) ? rightTemplatefilterCondition.trim()
									: "";

					Long rightTemplateId = Long.parseLong(glbl_rule.get("RightTemplateId"));

					// Variables to hold updated values
					String updated_ruleExpression = ruleExpression;
					String updated_matchingRules = matchingRules;
					String updated_filterCondition = filterCondition;
					String updated_rightTemplatefilterCondition = rightTemplatefilterCondition;

					Set<String> anchorColumnsList = new HashSet<String>();
					Set<String> total_usedSynonymsList = new HashSet<String>();
					Set<String> total_matchedSynonymsList = new HashSet<String>();
					// For right template - Conditional Orphan with right global filter
					Set<String> r_total_usedSynonymsList = new HashSet<String>();
					Set<String> r_total_matchedSynonymsList = new HashSet<String>();

					boolean isRuleExpressionValid = false;
					boolean isMatchingRulesValid = false;
					boolean isFilterConditionValid = false;
					boolean isRightTemplateFilterConditionValid = false;
					boolean isRuleApplicable = false;

					/*
					 * Validate Rule Expression
					 */
					if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

						// Get the unique synonyms used in the RuleExpression
						String[] rexp_used_synonymn_cols = StringUtils.substringsBetween(updated_ruleExpression, "@",
								"@");
						Set<String> rexp_conditionColumns = new HashSet<>();
						if (rexp_used_synonymn_cols != null && rexp_used_synonymn_cols.length > 0) {
							rexp_conditionColumns = new HashSet<>(Arrays.asList(rexp_used_synonymn_cols));

							// Adding synonyms to total used synonym list
							total_usedSynonymsList.addAll(rexp_conditionColumns);
						}

						int rexp_usedSynonymCount = rexp_conditionColumns.size();
						int rexp_matchCount = 0;

						for (String sKey : oEffectiveSynonyms.keySet()) {
							String actual_sSynonym = oEffectiveSynonyms.get(sKey);

							// Synonym name will be surrounded by @
							String sSynonym = "@" + actual_sSynonym + "@";

							if (updated_ruleExpression.indexOf(sSynonym) > -1) {
								// Update the expression
								if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
										|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
									updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym,
											"originalDf." + sKey);
								else
									updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym, sKey);

								// Add the column name to anchor columns list
								anchorColumnsList.add(sKey);

								// Add synonym to total matched synonym list
								total_matchedSynonymsList.add(actual_sSynonym);

								++rexp_matchCount;
							}
						}

						Map<String, String> leftUnmatchedSynonyms = new HashMap<>();
						total_usedSynonymsList.stream().filter(s -> !total_matchedSynonymsList.contains(s))
								.collect(Collectors.toList()).forEach(s -> {
									leftUnmatchedSynonyms.put(s, "");
								});

						/*
						 * If unmatched synonym found check for the possible_name for same synonym.
						 */
						if (leftUnmatchedSynonyms.size() > 0) {
							List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
							Set<String> unmatchedSet = new HashSet<>();
							unmatchedSet.addAll(total_usedSynonymsList);
							for (String unmatchedSynonym : unmatchedSet) {
								List<SynonymLibrary> synonymLibraries = globalRuleDAO
										.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
								if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
									for (SynonymLibrary library : synonymLibraries) {
										List<String> possibleNames = Arrays
												.stream(library.getPossibleNames().split(","))
												.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
										for (String possibleName : possibleNames) {
											if (columnsList.contains(possibleName)) {
												String sSynonym = "@" + unmatchedSynonym + "@";
												if (updated_ruleExpression.indexOf(sSynonym) > -1) {
													++rexp_matchCount;
													total_matchedSynonymsList.add(unmatchedSynonym);
													anchorColumnsList.add(possibleName);
													if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
															|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
														updated_ruleExpression = updated_ruleExpression
																.replaceAll(sSynonym, "originalDf." + possibleName);
													else
														updated_ruleExpression = updated_ruleExpression
																.replaceAll(sSynonym, possibleName);
													break;
												}
											}
										}
										String[] extraSynonyms = StringUtils.substringsBetween(updated_ruleExpression,
												"@", "@");
										if (extraSynonyms == null || extraSynonyms.length == 0) {
											break;
										}
									}
								}

							}
						}

						if (rexp_matchCount == rexp_usedSynonymCount) {
							isRuleExpressionValid = true;
						}

					} else if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE))
						isRuleExpressionValid = true;

					/*
					 * Validate Matching expression
					 */
					if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
				            ||ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {

						String sGlobalExpr = "";
						String[] aMatchPair = null;

						try {
							sGlobalExpr = updated_matchingRules;
							aMatchPair = sGlobalExpr.split("=");

							// MatchingRules must contain 2 parts : Left and right parts
							if (aMatchPair.length < 2)
								continue;

							// For Orphan and Cross Referential rules, split the MatchingRules and take the
							// left part of it
							sGlobalExpr = aMatchPair[0].trim();

						} catch (Exception e) {
							LOG.error(e.getMessage());
							e.printStackTrace();
							continue;
						}

						// Get the unique synonyms used in the MatchingRules
						String[] mr_used_synonymn_cols = StringUtils.substringsBetween(sGlobalExpr, "@", "@");
						Set<String> mr_conditionColumns = new HashSet<>();
						if (mr_used_synonymn_cols != null && mr_used_synonymn_cols.length > 0) {
							mr_conditionColumns = new HashSet<>(Arrays.asList(mr_used_synonymn_cols));

							// Adding synonyms to total used synonym list
							total_usedSynonymsList.addAll(mr_conditionColumns);
						}

						int mr_usedSynonymCount = mr_conditionColumns.size();
						int mr_matchCount = 0;

						for (String sKey : oEffectiveSynonyms.keySet()) {
							String actual_sSynonym = oEffectiveSynonyms.get(sKey);

							// Synonym name will be surrounded by @
							String sSynonym = "@" + actual_sSynonym + "@";

							if (sGlobalExpr.indexOf(sSynonym) > -1) {

								// For Orphan rules we need to add prefix "originalDf" to avoid conflict of same
								// names in left and right datasets
								sGlobalExpr = sGlobalExpr.replaceAll(sSynonym, "originalDf." + sKey);

								// Add the column name to anchor columns list
								anchorColumnsList.add(sKey);

								// Add synonym to total matched synonym list
								total_matchedSynonymsList.add(actual_sSynonym);

								++mr_matchCount;
							}
						}

						Map<String, String> leftUnmatchedSynonyms = new HashMap<>();
						total_usedSynonymsList.stream().filter(s -> !total_matchedSynonymsList.contains(s))
								.collect(Collectors.toList()).forEach(s -> {
									leftUnmatchedSynonyms.put(s, "");
								});
						/*
						 * If unmatched synonym found check for the possible_name for same synonym.
						 */
						if (leftUnmatchedSynonyms.size() > 0) {
							List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
							Set<String> unmatchedSet = new HashSet<>();
							unmatchedSet.addAll(total_usedSynonymsList);
							for (String unmatchedSynonym : unmatchedSet) {
								List<SynonymLibrary> synonymLibraries = globalRuleDAO
										.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
								if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
									for (SynonymLibrary library : synonymLibraries) {
										List<String> possibleNames = Arrays
												.stream(library.getPossibleNames().split(","))
												.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
										for (String possibleName : possibleNames) {
											if (columnsList.contains(possibleName)) {
												String sSynonym = "@" + unmatchedSynonym + "@";
												if (sGlobalExpr.indexOf(sSynonym) > -1) {
													++mr_matchCount;
													total_matchedSynonymsList.add(unmatchedSynonym);
													anchorColumnsList.add(possibleName);
													sGlobalExpr = sGlobalExpr.replaceAll(sSynonym,
															"originalDf." + possibleName);
													break;
												}
											}
										}
										String[] extraSynonyms = StringUtils.substringsBetween(sGlobalExpr, "@", "@");
										if (extraSynonyms == null || extraSynonyms.length == 0) {
											break;
										}
									}
								}

							}
						}

						if (mr_matchCount == mr_usedSynonymCount) {
							isMatchingRulesValid = true;
							// Updated MatchingRules value
							updated_matchingRules = sGlobalExpr + "=" + aMatchPair[1].trim();
						}

					} else if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE))
						isMatchingRulesValid = true;

					/*
					 * Validate Filter condition
					 */
					if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)
					        ||ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)) {

						// Get the unique synonyms used in the filter condition
						String[] fc_used_synonymn_cols = StringUtils.substringsBetween(updated_filterCondition, "@",
								"@");
						Set<String> fc_conditionColumns = new HashSet<>();
						if (fc_used_synonymn_cols != null && fc_used_synonymn_cols.length > 0) {
							fc_conditionColumns = new HashSet<>(Arrays.asList(fc_used_synonymn_cols));

							// Adding synonyms to total used synonym list
							total_usedSynonymsList.addAll(fc_conditionColumns);
						}

						int fc_usedSynonymCount = fc_conditionColumns.size();
						int fc_matchCount = 0;

						for (String sKey : oEffectiveSynonyms.keySet()) {
							String actual_sSynonym = oEffectiveSynonyms.get(sKey);

							// Synonym name will be surrounded by braces
							String sSynonym = "@" + actual_sSynonym + "@";

							if (updated_filterCondition.indexOf(sSynonym) > -1) {
								updated_filterCondition = updated_filterCondition.replaceAll(sSynonym, sKey);

								// Add the column name to anchor columns list
								anchorColumnsList.add(sKey);

								// Add synonym to total matched synonym list
								total_matchedSynonymsList.add(actual_sSynonym);

								++fc_matchCount;
							}
						}

						Map<String, String> leftUnmatchedSynonyms = new HashMap<>();
						total_usedSynonymsList.stream().filter(s -> !total_matchedSynonymsList.contains(s))
								.collect(Collectors.toList()).forEach(s -> {
									leftUnmatchedSynonyms.put(s, "");
								});
						/*
						 * If unmatched synonym found check for the possible_name for same synonym.
						 */
						if (leftUnmatchedSynonyms.size() > 0) {
							List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
							Set<String> unmatchedSet = new HashSet<>();
							unmatchedSet.addAll(total_usedSynonymsList);
							for (String unmatchedSynonym : unmatchedSet) {
								List<SynonymLibrary> synonymLibraries = globalRuleDAO
										.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
								if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
									for (SynonymLibrary library : synonymLibraries) {
										List<String> possibleNames = Arrays
												.stream(library.getPossibleNames().split(","))
												.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
										for (String possibleName : possibleNames) {
											if (columnsList.contains(possibleName)) {
												String sSynonym = "@" + unmatchedSynonym + "@";
												if (updated_filterCondition.indexOf(sSynonym) > -1) {
													++fc_matchCount;
													total_matchedSynonymsList.add(unmatchedSynonym);
													anchorColumnsList.add(possibleName);
													updated_filterCondition = updated_filterCondition
															.replaceAll(sSynonym, possibleName);
													break;
												}
											}
										}
										String[] extraSynonyms = StringUtils.substringsBetween(updated_filterCondition,
												"@", "@");
										if (extraSynonyms == null || extraSynonyms.length == 0) {
											break;
										}
									}
								}

							}
						}

						if (fc_matchCount == fc_usedSynonymCount)
							isFilterConditionValid = true;

					} else if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE))
						isFilterConditionValid = true;

					/*
					 * Validate Right Template Filter condition
					 */
					if ((ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
							&& rightTemplatefilterCondition != null && !rightTemplatefilterCondition.trim().isEmpty()) {

						// Get effective synonyms of the Template
						Map<String, String> r_effectiveSynonyms = getEffectiveSynonymsForTemplate(rightTemplateId,
								expandedSynonymMap);

						LOG.debug("\n====> effectiveSynonymsMap of Right Template: " + r_effectiveSynonyms);

						// Get the unique synonyms used in the filter condition
						String[] rfc_used_synonymn_cols = StringUtils
								.substringsBetween(updated_rightTemplatefilterCondition, "@", "@");

						Set<String> rfc_conditionColumns = new HashSet<>();
						if (rfc_used_synonymn_cols != null && rfc_used_synonymn_cols.length > 0) {
							rfc_conditionColumns = new HashSet<>(Arrays.asList(rfc_used_synonymn_cols));

							// Adding synonyms to total used synonym list
							r_total_usedSynonymsList.addAll(rfc_conditionColumns);
						}

						int rfc_usedSynonymCount = rfc_conditionColumns.size();
						int rfc_matchCount = 0;

						for (String sKey : r_effectiveSynonyms.keySet()) {
							String actual_sSynonym = r_effectiveSynonyms.get(sKey);

							// Synonym name will be surrounded by braces
							String sSynonym = "@" + actual_sSynonym + "@";

							if (updated_rightTemplatefilterCondition.indexOf(sSynonym) > -1) {
								updated_rightTemplatefilterCondition = updated_rightTemplatefilterCondition
										.replaceAll(sSynonym, sKey);

								// Add the column name to anchor columns list
//								anchorColumnsList.add(sKey);

								// Add synonym to total matched synonym list
								r_total_matchedSynonymsList.add(actual_sSynonym);

								++rfc_matchCount;
							}
						}

						Map<String, String> rightUnmatchedSynonyms = new HashMap<>();
						r_total_usedSynonymsList.stream().filter(s -> !r_total_matchedSynonymsList.contains(s))
								.collect(Collectors.toList()).forEach(s -> {
									rightUnmatchedSynonyms.put(s, "");
								});
						/*
						 * If unmatched synonym found check for the possible_name for same synonym.
						 */
						if (rightUnmatchedSynonyms.size() > 0) {
							List<String> columnsList = validationCheckDAO
									.getDisplayNamesFromListDataDefinition(rightTemplateId);
							Set<String> unmatchedSet = new HashSet<>();
							unmatchedSet.addAll(r_total_usedSynonymsList);
							for (String unmatchedSynonym : unmatchedSet) {
								List<SynonymLibrary> synonymLibraries = globalRuleDAO
										.getSynonymListByDomainAndName(domainId, unmatchedSynonym);
								if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
									for (SynonymLibrary library : synonymLibraries) {
										List<String> possibleNames = Arrays
												.stream(library.getPossibleNames().split(","))
												.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
										for (String possibleName : possibleNames) {
											if (columnsList.contains(possibleName)) {
												String sSynonym = "@" + unmatchedSynonym + "@";
												if (updated_rightTemplatefilterCondition.indexOf(sSynonym) > -1) {
													++rfc_matchCount;
													r_total_matchedSynonymsList.add(unmatchedSynonym);
//													anchorColumnsList.add(possibleName);
													updated_rightTemplatefilterCondition = updated_rightTemplatefilterCondition
															.replaceAll(sSynonym, possibleName);
													break;
												}
											}
										}
										String[] extraSynonyms = StringUtils
												.substringsBetween(updated_rightTemplatefilterCondition, "@", "@");
										if (extraSynonyms == null || extraSynonyms.length == 0) {
											break;
										}
									}
								}

							}
						}

						if (rfc_matchCount == rfc_usedSynonymCount)
							isRightTemplateFilterConditionValid = true;

					} else
						isRightTemplateFilterConditionValid = true;

					if (isRuleExpressionValid && isMatchingRulesValid && isFilterConditionValid
							&& isRightTemplateFilterConditionValid)
						isRuleApplicable = true;

					String anchorColumns = String.join(",", anchorColumnsList);

					// If rule is applicable add to eligible rule list else to non eligible rule
					// list
					if (isRuleApplicable) {

						String nullFilterColumn = nullFilterColumnsList.get(glbl_rule.get("Id"));

						glbl_rule.put("AnchorColumns", anchorColumns);
						glbl_rule.put("EffectiveExpression", updated_ruleExpression);
						glbl_rule.put("MatchingRules", updated_matchingRules);
						glbl_rule.put("FilterCondition", updated_filterCondition);
						glbl_rule.put("RightTemplateFilterCondition", updated_rightTemplatefilterCondition);
						glbl_rule.put("NullFilterColumn", nullFilterColumn == null ? "" : nullFilterColumn);
						glbl_rule.put("businessAttributeId", "");
						glbl_rule.put("businessAttributes", "");
						eligibleRulesList.add(glbl_rule);

					} else {
						glbl_rule.put("AnchorColumns", anchorColumns);
						glbl_rule.put("EffectiveExpression", ruleExpression);
						glbl_rule.put("MatchingRules", matchingRules);
						glbl_rule.put("FilterCondition", filterCondition);
						glbl_rule.put("RightTemplateFilterCondition", rightTemplatefilterCondition);
						glbl_rule.put("businessAttributeId", "");
						glbl_rule.put("businessAttributes", "");

						/*
						 * Get total Unmatched synonyms
						 */
						Set<String> total_unmatchedSynonymList = total_usedSynonymsList;

						// Remove the matched synonyms for total used synonyms list
						total_unmatchedSynonymList.removeAll(total_matchedSynonymsList);

						glbl_rule.put("UnmatchedSynonyms", String.join(",", total_unmatchedSynonymList));

						/*
						 * Get total Unmatched synonyms of Right Global Filter - Conditional Orphan rule
						 */
						Set<String> r_total_unmatchedSynonymList = r_total_usedSynonymsList;

						// Remove the matched synonyms for total used synonyms list
						r_total_unmatchedSynonymList.removeAll(r_total_matchedSynonymsList);

						glbl_rule.put("RightTemplateUnmatchedSynonyms", String.join(",", r_total_unmatchedSynonymList));

						nonEligibleRulesList.add(glbl_rule);
					}

				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}

		// Return eligible rules if the flag is enabled, else return non eligible rules
		if (fetchEligibleRules)
			return eligibleRulesList;
		else
			return nonEligibleRulesList;
	}

	private List<HashMap<String, String>> getApplicableThresholds(List<HashMap<String, String>> availableThresholds,
			List<String> tableColumns, Map<String, String> oEffectiveSynonyms) {

		List<HashMap<String, String>> applicableThresholds = new ArrayList<HashMap<String, String>>();

		if (availableThresholds != null) {
			for (HashMap<String, String> oThreshold : availableThresholds) {
				try {
					boolean lApplicableThreshold = false;
					String sGlobalColumnName = oThreshold.get("globalColumnName");

					/* Check if this threshold applicable via Synonyms */
					for (String sKey : oEffectiveSynonyms.keySet()) {
						String sSynonym = oEffectiveSynonyms.get(sKey);

						lApplicableThreshold = (sGlobalColumnName.equals(sSynonym)) ? true : false;
						if (lApplicableThreshold) {
							sGlobalColumnName = sKey;
							break;
						}
					}

					/* If threshold not applicable via Synonyms then scan all table columns */
					if (!lApplicableThreshold) {
						for (String sColumn : tableColumns) {

							lApplicableThreshold = (sGlobalColumnName.equals(sColumn)) ? true : false;
							if (lApplicableThreshold) {
								break;
							}
						}
					}
					oThreshold.put("EffectiveGlobalColumnName", sGlobalColumnName);

					if (lApplicableThreshold)
						applicableThresholds.add(oThreshold);
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}

			}
		}
		return applicableThresholds;
	}

	private List<HashMap<String, String>> getApplicableReferenceRules(List<HashMap<String, String>> allReferenceRules,
			List<String> tableColumns, Map<String, String> oEffectiveSynonyms, long idData) {

		List<HashMap<String, String>> applicableReferenceRules = new ArrayList<HashMap<String, String>>();

		if (allReferenceRules != null) {

			// Fetching latest run Column Profile details of the template
			List<ColumnProfile_DP> columnProfileList = listDataSourceDao.readColumnProfileForTemplate(idData);

			// Prepare Column Profile Map with Column name as key
			Map<String, ColumnProfile_DP> columnProfileMap = new HashMap<>();
			for (ColumnProfile_DP columnProfileDp : columnProfileList)
				columnProfileMap.put(columnProfileDp.getColumnName(), columnProfileDp);

			for (HashMap<String, String> referenceRule : allReferenceRules) {

				try {
					String sMasterColumnName = referenceRule.get("MasterColumnName");

					// Get the profiling details from reference rule details
					long refUniqueCount = Long.valueOf("" + referenceRule.get("UniqueCount"));
					long refMinLength = Long.valueOf("" + referenceRule.get("MinLength"));
					long refMaxLength = Long.valueOf("" + referenceRule.get("MaxLength"));

					boolean isRuleApplicable = false;
					boolean isSynonymMatch = false;
					boolean isColumnMatch = false;
					String sTmplColumn = "";

					/* Check if this master column applicable via Synonyms */
					for (String sKey : oEffectiveSynonyms.keySet()) {
						String sSynonym = oEffectiveSynonyms.get(sKey);

						if (sMasterColumnName.equals(sSynonym)) {
							sTmplColumn = sKey;
							isSynonymMatch = true;
							break;
						}
					}

					/* If master column not applicable via Synonyms then scan all table columns */
					if (!isRuleApplicable) {
						for (String sColumn : tableColumns) {

							if (sMasterColumnName.equals(sColumn)) {
								sTmplColumn = sColumn;
								isColumnMatch = true;
								break;
							}
						}
					}

					if (isSynonymMatch || isColumnMatch) {

						// Get the Profile details of the Template column
						ColumnProfile_DP columnProfileDp = columnProfileMap.get(sTmplColumn);

						// Default confidence level
						String sConfidenceLevel = (isColumnMatch ? "0.35" : "0.25");

						// Check if the profile details are available for the column
						if (columnProfileDp != null) {

							// Read the column profile details
							long sourceUniqueCount = Long.valueOf("" + columnProfileDp.getUniqueCount());
							long sourceMinLength = Long.valueOf("" + columnProfileDp.getMinLength());
							long sourceMaxLength = Long.valueOf("" + columnProfileDp.getMaxLength());

							// Check if source unique count is less than reference
							if (sourceUniqueCount < refUniqueCount) {
								// Set rule is applicable
								isRuleApplicable = true;

								// Match min and max lengths
								if (sourceMinLength == refMinLength && sourceMaxLength == refMaxLength) {
									if (isSynonymMatch)
										sConfidenceLevel = "0.8";
									else if (isColumnMatch)
										sConfidenceLevel = "0.9";

								} else {
									if (isSynonymMatch)
										sConfidenceLevel = "0.6";
									else if (isColumnMatch)
										sConfidenceLevel = "0.7";
								}
							}
						}

						if (isRuleApplicable) {
							referenceRule.put("NewColumnName", sTmplColumn);
							referenceRule.put("ConfidenceLevel", sConfidenceLevel);
							applicableReferenceRules.add(referenceRule);
						}
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return applicableReferenceRules;
	}

	public JSONObject linkSelectedGlobalRulesToTemplate(JSONArray selectedRules, JSONArray delinkedRules, long idData) {
		String status = "success";
		String message = "Custom Rules link/delink to template is successful";

		try {
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

			if ((selectedRules != null && selectedRules.length() > 0)
					|| (delinkedRules != null && delinkedRules.length() > 0)) {

				LOG.info("\n====> Link the selected Global Rules  <====");

				if (selectedRules != null && selectedRules.length() > 0) {
					for (int i = 0; i < selectedRules.length(); i++) {
						// Get Selected Rule
						JSONObject oRule = selectedRules.getJSONObject(i);

						// Get global Rule Id
						long globalRuleId = Long.parseLong(oRule.getString("Id"));

						ListColGlobalRules listColGlobalRules = new ListColGlobalRules();
						listColGlobalRules.setIdListColrules(globalRuleId);
						listColGlobalRules.setExpression(oRule.getString("EffectiveExpression"));
						listColGlobalRules.setMatchingRules(oRule.getString("MatchingRules"));
						listColGlobalRules.setFilterCondition(oRule.getString("FilterCondition"));
						listColGlobalRules
								.setRightTemplateFilterCondition(oRule.getString("RightTemplateFilterCondition"));

						String anchorColumns = oRule.getString("AnchorColumns");
						if (anchorColumns == null || anchorColumns.trim().isEmpty())
							anchorColumns = "";

						// Check if the rule is mapped or not
						boolean isRuleLinked = listDataSourceDao.isGlobalruleLinkedToTemplate(idData, globalRuleId);
						LOG.debug("\n====>Global Rule Id: " + globalRuleId + "  isRuleLinked: " + isRuleLinked);

						if (isRuleLinked) {
							// Check if the Rule is in deactivated state
							boolean isRuleDeactivated = listDataSourceDao
									.isGlobalruleLinkedToTemplateDeactivated(idData, globalRuleId);

							// If deactivated then activate the global rule
							if (isRuleDeactivated) {
								LOG.info("\n====>Activating the Global Rule linked to Template ..");

								listDataSourceDao.activateGlobalruleLinkedToTemplate(idData, listColGlobalRules);
							}

						} else {
							LOG.info("\n====>Linking the Global Rule to Template ..");

							// insert into rule_Template_Mapping
							listDataSourceDao.insertToRuleTemplateMapping(idData, listColGlobalRules, anchorColumns);
						}
					}
				}

				LOG.info("\n====> Delink the unchecked Global Rules  <====");
				if (delinkedRules != null && delinkedRules.length() > 0) {
					for (int i = 0; i < delinkedRules.length(); i++) {
						long globalRuleId = delinkedRules.getJSONObject(i).getLong("Id");

						LOG.debug("\n====>Unlinking global rule [" + globalRuleId + "] from data template [" + idData
								+ "]");

						if (isRuleCatalogEnabled) {
							listDataSourceDao.deactivateGlobalRuleOfTemplate(idData, globalRuleId);
						} else {
							listDataSourceDao.unlinkGlobalRuleFromDataTemplate(idData, globalRuleId);
						}
					}
				}

				// When RuleCatalog is enabled, We need to add this new rule in all the
				// associated rule catalog
				if (isRuleCatalogEnabled) {
					LOG.info("\n====> Updating Global Rule changes in all the Associated validations RuleCatalogs");
					CompletableFuture.runAsync(() -> {
						ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
					});
				}

			} else {
				status = "failed";
				message = "No Custom rules found to link or delink";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while link/delink Custom Rules to Template";
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("status", status);
		responseJson.put("message", message);
		return responseJson;
	}

	public JSONObject linkSelectedOtherGlobalRulesToTemplate(JSONArray selectedRules, long idData) {
		JSONArray mappingErrors = new JSONArray();

		String status = "success";
		String message = "GlobalRules are linked to template successfully";

		try {
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

			if (selectedRules != null && selectedRules.length() > 0) {

				LOG.info("\n====> Link the selected Global Rules  <====");

				if (selectedRules != null && selectedRules.length() > 0) {
					for (int i = 0; i < selectedRules.length(); i++) {
						// Get Selected Rule
						JSONObject oRule = selectedRules.getJSONObject(i);

						// Get global Rule Id
						long globalRuleId = Long.parseLong(oRule.getString("Id"));

						// Link the Global Rule to Template
						JSONObject statusJson = linkGlobalRuleToTemplateById(idData, globalRuleId);

						if (statusJson.get("status").toString().equalsIgnoreCase("failed")) {
							status = "failed";
							message = "Failed to link one or more GlobalRules to Template";
							mappingErrors.put(statusJson.get("message"));
						}

					}
				}

				// When RuleCatalog is enabled, We need to add this new rule in all the
				// associated rule catalog
				if (isRuleCatalogEnabled) {
					LOG.info("\n====> Updating Global Rule changes in all the Associated validations RuleCatalogs");
					CompletableFuture.runAsync(() -> {
						ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
					});
				}

			} else {
				status = "failed";
				message = "No Global rules found to link to Template";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while linking GlobalRules to Template";
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("status", status);
		responseJson.put("message", message);
		responseJson.put("mappingErrors", mappingErrors);
		return responseJson;
	}

	public JSONObject linkSelectedReferenceRulesToTemplate(JSONArray refRules, long idData) {

		String status = "success";
		String message = "Reference Rules are linked to template is successfully";

		try {
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

			if ((refRules != null && refRules.length() > 0)) {

				LOG.info("\n====> Link the selected Reference Rules  <====");

				// Read Template details
				ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);
				String createdByUser = listDataSource.getCreatedByUser();
				int domainId = listDataSource.getDomain();
				long projectId = listDataSource.getProjectId();

				for (int i = 0; i < refRules.length(); i++) {
					// Get Selected Rule
					JSONObject ref_rule = refRules.getJSONObject(i);

					String newColumnName = ref_rule.getString("NewColumnName");
					String masterColumnName = ref_rule.getString("MasterColumnName");
					String masterTemplateId = ref_rule.getString("MasterTemplateId");
					long idRightData = Long.parseLong(masterTemplateId);
					String sOrphanExpr = newColumnName + "=" + masterColumnName;
					String ruleName = newColumnName + "_" + masterColumnName + "_auto_discovery";

					// Get Right Template name
					String rightTemplName = validationCheckDAO.getNameFromListDataSources(idRightData);

					ListColRules listColRules = new ListColRules();
					listColRules.setRuleName(ruleName);
					listColRules.setIdData(idData);
					listColRules.setIdRightData(idRightData);
					listColRules.setDescription("");
					listColRules.setRuleType("Orphan");
					listColRules.setMatchingRules(sOrphanExpr);
					listColRules.setExpression("");
					listColRules.setExternal("Y");
					listColRules.setExternalDatasetName(rightTemplName);
					listColRules.setRuleThreshold(1.0);
					listColRules.setCreatedByUser(createdByUser);
					listColRules.setAnchorColumns(newColumnName);
					listColRules.setProjectId(projectId);
					listColRules.setDomainId(domainId);

					long refRuleId = externdTemplateRuleDao.insertintolistColRules(listColRules);

					if (refRuleId <= 0l) {
						status = "failed";
						message = "Failed to link one or more Reference Rules of Template";
					}
				}

				// When RuleCatalog is enabled, We need to add this new rule in all the
				// associated rule catalog
				if (isRuleCatalogEnabled) {
					LOG.info("\n====> Updating all the Associated validations RuleCatalogs");
					CompletableFuture.runAsync(() -> {
						ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
					});
				}

			} else {
				status = "failed";
				message = "No Reference rules found to link to template";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while link Reference Rules to Template";
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("status", status);
		responseJson.put("message", message);
		return responseJson;
	}

	public JSONObject linkGlobalThresholdToDataTemplate(JSONArray selectedGlobalThresholds, long idData) {

		JSONArray mappingErrors = new JSONArray();
		String status = "success";
		String message = "GlobalThresholds are linked to template successfully";

		try {
			if (selectedGlobalThresholds != null && selectedGlobalThresholds.length() > 0) {

				for (int i = 0; i < selectedGlobalThresholds.length(); i++) {

					JSONObject globalThresholdJson = selectedGlobalThresholds.getJSONObject(i);

					// Get GlobalThreshold Id
					long idGlobalThreshold = Long.parseLong(globalThresholdJson.getString("Id"));
					String columnName = globalThresholdJson.getString("ColumnName");

					// Get column Details
					List<ListDataDefinition> listDataDefinitions = templateViewDao.view(idData);

					long idColumn = 0l;
					for (ListDataDefinition ldd : listDataDefinitions) {
						if (ldd.getDisplayName().equalsIgnoreCase(columnName.trim())) {
							idColumn = ldd.getIdColumn();
							break;
						}
					}

					if (idColumn > 0l) {

						// Check if the threshold is mapped or not
						boolean isThresholdLinked = listDataSourceDao.isGlobalThresholdLinkedToTemplate(idData,
								idGlobalThreshold, idColumn);

						if (!isThresholdLinked) {

							LOG.info("\n====> Linking the Global Threshold to Template ..");

							// Insert selected global Threshold
							globalRuleDAO.insertIntoListGlobalThresholdsSelected(idGlobalThreshold, idData, idColumn);
						}

					} else {
						LOG.debug("\n===> Failed to get column Id for column[" + columnName
								+ "], unable to link global threshold to template !!");

						status = "failed";

						message = "Failed to link Global Threshold for one or more columns of Template";

						String mappingError = "Failed to link Global Threshold for column [" + columnName + "]";

						LOG.debug("\n====> " + mappingError);

						mappingErrors.put(mappingError);

					}
				}
			} else {
				LOG.error("\n===> No selected GlobalThresholds are found to link !!");

				status = "failed";
				message = "No selected GlobalThresholds are found to link to template";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while linking GlobalThresholds to Template";
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("status", status);
		responseJson.put("message", message);
		responseJson.put("mappingErrors", mappingErrors);
		return responseJson;
	}

	public JSONObject saveSynonymMappings(long idData, JSONObject synonymMappingsJson) {

		JSONArray mappingErrors = new JSONArray();
		String status = "success";
		String message = "Synonym Mappings are saved successfully";

		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get enterprise domainId
			Integer enterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

			int synonym_count = 0;
			if (synonymMappingsJson != null) {
				Iterator<String> keys = synonymMappingsJson.keys();

				while (keys.hasNext()) {
					++synonym_count;
					String synonym = keys.next().trim();
					String template_column = synonymMappingsJson.getString(synonym).trim();

					LOG.debug("\n====>synonym: " + synonym + " template_column: " + template_column);

					// Get synonym Id by domainId and synonym name
					ruleFields rc = new ruleFields();
					rc.setUsercolumns(synonym);
					rc.setDomain_id(domainId);

					// Fetch the synonym Id from the domain
					int syn_id = globalRuleDAO.getsynosymId(rc);

					// If synonymId is not found check in enterprise domain
					if (syn_id <= 0) {

						// Check if the synonym is present in enterprise domain
						rc.setDomain_id(enterpriseDomainId);

						syn_id = globalRuleDAO.getsynosymId(rc);

					}
					LOG.debug("\n====>Synonym Id: " + syn_id);

					boolean updateStatus = false;
					// If synonym is found - update the synonym, add the template column to possible
					// values
					if (syn_id > 0) {

						// Get the possible names of the synonym
						String possibleValues = globalRuleDAO.getPossibleName(rc);

						// Convert to possibleValues to List
						List<String> possibleValues_list = new ArrayList<String>();
						if (possibleValues != null && !possibleValues.trim().isEmpty()) {
							String[] values = possibleValues.split(",");

							for (String possible_name : values) {
								possibleValues_list.add(possible_name.trim());
							}
						}

						// Check if the template column is already present in possible values list of
						// synonym
						if (possibleValues_list != null && possibleValues_list.contains(template_column)) {
							updateStatus = true;

						} else {
							// Update the synonym, add the template column to possible values
							updateStatus = globalRuleDAO.updateSynonymPossiblenames(syn_id, template_column);
						}

					} else {
						// Create new synoymn
						LOG.info("\n====> Synonymn not found, creating new one ..");
						rc.setDomain_id(domainId);
						rc.setPossiblenames(template_column.trim());
						int insert_count = globalRuleDAO.insertintoSynonymsLibrary(rc);

						if (insert_count > 0)
							updateStatus = true;
					}

					LOG.debug("\n====> updateStatus : " + updateStatus);

					if (!updateStatus) {
						status = "failed";
						message = "Failed to update synonym mappings for one or more synonyms";

						String mappingError = "Failed to update synonym [" + synonym + "] with possible name ["
								+ template_column + "]";
						mappingErrors.put(mappingError);

					}

				}
			}

			if (synonym_count == 0) {
				LOG.error("\n===> No Synonym Mappings found to save!!");

				status = "failed";
				message = "No Synonym Mappings found to save";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			status = "failed";
			message = "Error occurred while saving synonym mappings";
			e.printStackTrace();
		}

		JSONObject responseJson = new JSONObject();
		responseJson.put("status", status);
		responseJson.put("message", message);
		responseJson.put("mappingErrors", mappingErrors);
		return responseJson;
	}

	public Map<String, String> getExpandedSynonymMapForDomain(Integer domainId) {
		Integer nEnterpriseDomainId = globalRuleDAO.getEnterpriseDomainId();

		String sDomainInList = "";
		if (domainId != null && domainId != 0l && nEnterpriseDomainId != null && nEnterpriseDomainId != 0l) {
			sDomainInList = domainId + "," + nEnterpriseDomainId;
		} else if (domainId != null && domainId != 0l) {
			sDomainInList = "" + domainId;
		} else if (nEnterpriseDomainId != null && nEnterpriseDomainId != 0l) {
			sDomainInList = "" + nEnterpriseDomainId;

		}

		Map<String, String> expandedSynonymMap = null;

		if (sDomainInList != null && !sDomainInList.trim().isEmpty()) {
			// Get global column names
			expandedSynonymMap = JwfSpaInfra.getExpandedSynonyms(jdbcTemplate, sDomainInList);
		}

		return expandedSynonymMap;
	}

	public Map<String, String> getEffectiveSynonymsForTemplate(long templateId,
			Map<String, String> expandedSynonymMap) {
		// get column names of templateId
		List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(templateId);

		Map<String, String> effectiveSynonymsMap = new HashMap<String, String>();

		if (columnsList != null && columnsList.size() > 0) {

			for (int nIndex = 0; nIndex < columnsList.size(); nIndex++) {
				String columnName = columnsList.get(nIndex).trim();
				columnsList.set(nIndex, columnName);

				if (expandedSynonymMap.containsKey(columnName.toLowerCase())) {
					String globalName = expandedSynonymMap.get(columnName.toLowerCase());
					effectiveSynonymsMap.put(columnName, globalName);
				}
			}
		}

		return effectiveSynonymsMap;
	}

	public JSONObject getSynonymsPageData(int nDataContext) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		String sDataListSql, sSynonymsViewList, sDomainList;

		String[] aColumnSpec = new String[] { "SynonymsId:checkbox", "SynonymsId", "DomainId", "DomainName",
				"SynonymsName", "UserFields", "SynonymsId:edit", "SynonymsId:delete" };

		JSONObject oJsonRetValue = new JSONObject();
		JSONArray aJsonDataList = new JSONArray();
		JSONArray aJsonDomainList = new JSONArray();
		JSONObject oJsonLists = new JSONObject();

		ObjectMapper oMapper = new ObjectMapper();

		DateUtility.DebugLog("loadSynonymsViewList 01", "Begin controller processing");

		try {
			/*
			 * Get Synonyms list for page load and reloading after user adds or modify
			 * synonyms via UI (nDataContext = 0 or 1) common for both
			 */
			sDataListSql = "";
			sDataListSql = sDataListSql
					+ "select a.synonyms_Id as SynonymsId, a.domain_id as DomainId, a.tableColumn as SynonymsName, a.possiblenames as UserFields, b.domainName as DomainName\n";
			sDataListSql = sDataListSql + "from SynonymLibrary a, domain b\n";
			sDataListSql = sDataListSql + "where a.domain_id = b.domainId\n";
			sDataListSql = sDataListSql + "order by b.domainName, a.tableColumn;";

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataListSql, aColumnSpec, "sampleTable",
					null);

			sSynonymsViewList = oMapper.writeValueAsString(aDataViewList);
			aJsonDataList = new JSONArray(sSynonymsViewList);

			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			/*
			 * Get domain list for page load only (nDataContext = 0) ignore for
			 * (nDataContext = 1)
			 */
			if (nDataContext < 1) {
				sDataListSql = "";
				sDataListSql = sDataListSql + "select * from\n";
				sDataListSql = sDataListSql + "(\n";
				sDataListSql = sDataListSql
						+ "select domainId as row_id, domainName as element_reference, domainName as element_text, 0 is_default, domainId as position\n";
				sDataListSql = sDataListSql + "from domain\n";
				sDataListSql = sDataListSql + ") core_qry;";

				aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataListSql, new String[] {}, "",
						null);
				sDomainList = oMapper.writeValueAsString(aDataViewList);
			} else {
				sDomainList = "[]";
			}

			aJsonDataList = new JSONArray(sDomainList);
			oJsonLists = oJsonLists.put("DOMAIN_LIST", aJsonDataList);
			oJsonRetValue = oJsonRetValue.put("PageOptionLists", oJsonLists);

			DateUtility.DebugLog("loadSynonymsViewList 02", "End controller processing");
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			throw oException;
		}

		// DemoOfGettingAllPageOptionsLists();

		return oJsonRetValue;
	}

	public JSONObject getSynonymsList(int nDataContext) throws Exception {
		JSONObject responseObj = new JSONObject();
		try {
			String sql = "select a.synonyms_Id as synonymsId, a.domain_id as domainId, a.tableColumn, a.possiblenames, "
					+ "b.domainName from SynonymLibrary a, domain b where a.domain_id = b.domainId order by b.domainName, a.tableColumn";

			LOG.debug(sql);
			JSONArray data = new JSONArray();
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {
				JSONObject resultObj = new JSONObject();

				resultObj.put("synonymsId", queryForRowSet.getObject("synonymsId"));
				resultObj.put("domainId", queryForRowSet.getObject("domainId"));
				resultObj.put("tableColumn", queryForRowSet.getString("tableColumn"));
				resultObj.put("possiblenames", queryForRowSet.getString("possiblenames"));
				resultObj.put("domainName", queryForRowSet.getString("domainName"));

				if (resultObj != null)
					data.put(resultObj);
			}
			if (nDataContext < 1) {
				JSONArray domains = new JSONArray();
				sql = "select domainId as rowId, domainName as elementReference, domainName as elementText, 0 as isDefault, domainId as position from domain";

				LOG.debug(sql);
				queryForRowSet = jdbcTemplate.queryForRowSet(sql);

				while (queryForRowSet.next()) {
					JSONObject resultObj = new JSONObject();

					resultObj.put("rowId", queryForRowSet.getObject("rowId"));
					resultObj.put("elementReference", queryForRowSet.getString("elementReference"));
					resultObj.put("elementText", queryForRowSet.getString("elementText"));
					resultObj.put("isDefault", queryForRowSet.getObject("isDefault"));
					resultObj.put("position", queryForRowSet.getObject("position"));

					if (resultObj != null)
						domains.put(resultObj);
				}
				responseObj.put("DOMAIN_LIST", domains);
			} else {
				responseObj.put("DOMAIN_LIST", new ArrayList<>());
			}
			responseObj.put("PageOptionLists", data);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return responseObj;
	}

	public JSONObject saveSynonymsFormFromViewList(JSONObject oSynonymsRecordToSave) {
		boolean lNewSynonyms = (oSynonymsRecordToSave.getString("SynonymsId").equalsIgnoreCase("-1")) ? true : false;
		String sInsertSql = "insert into SynonymLibrary (domain_Id, tableColumn, possiblenames) values (%1$s,'%2$s','%3$s');";
		String sUpdateSql = "update SynonymLibrary set tableColumn = '%1$s', possiblenames = '%2$s' where synonyms_Id = %3$s;";
		String sSqlToUpdate = "";
		String sDuplicateMsg = "Duplicate Synonyms Name within same domain is not allowed.";
		JSONObject oJsonRetValue = new JSONObject();

		try {
			if (lNewSynonyms) {
				sSqlToUpdate = String.format(sInsertSql, oSynonymsRecordToSave.getInt("DomainId"),
						oSynonymsRecordToSave.getString("SynonymsName"), oSynonymsRecordToSave.getString("UserFields"));

				// Query compatibility changes for both POSTGRES and MYSQL
//				String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
//						? "synonymlibrary"
//						: "SynonymLibrary";
				
				String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
						? "synonyms_id"
						: "synonyms_Id";

				KeyHolder keyHolder = new GeneratedKeyHolder();
				String finalSSqlToUpdate = sSqlToUpdate;

				jdbcTemplate.update(new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement pst = con.prepareStatement(finalSSqlToUpdate, new String[] { key_name });
						return pst;
					}
				}, keyHolder);
				int synonymsId;

				if (keyHolder.getKeys().size() > 1) {
					synonymsId = (int) keyHolder.getKeys().get(key_name);
				} else {
					synonymsId = keyHolder.getKey().intValue();
				}

				oJsonRetValue.put("synonymId", synonymsId);
			} else {
				sSqlToUpdate = String.format(sUpdateSql, oSynonymsRecordToSave.getString("SynonymsName"),
						oSynonymsRecordToSave.getString("UserFields"), oSynonymsRecordToSave.getString("SynonymsId"));
				jdbcTemplate.update(sSqlToUpdate);
			}

			oJsonRetValue.put("Result", true);
			oJsonRetValue.put("Msg", "Synonyms Successfully Saved");
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			// String sExceptionMsg = oException.getMessage();
			// String sExceptionMsg = "Data is too long for column (Only 200 characters
			// allowed for Synonym Name)";
			String sExceptionMsg = "Duplicate Synonyms Name within same domain is not allowed)";
			LOG.debug("!!!!!!sExceptionMsg=>" + sExceptionMsg);

			oException.printStackTrace();
			// LOG.debug( oException.printStackTrace());

			oJsonRetValue.put("Result", false);
			oJsonRetValue.put("Msg",
					((sExceptionMsg.toLowerCase().indexOf("duplicate") > -1) ? sDuplicateMsg : sExceptionMsg));
		}
		return oJsonRetValue;
	}

	/*
	 * Method to validate synonyms mapping for global rule.
	 */
	public JSONObject getGlobalRuleDetails(long idData, ListColGlobalRules globalRule) {
		JSONObject response = new JSONObject();
		try {
			// Get Rule Type
			String ruleType = globalRule.getRuleType();

			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(globalRule.getDomain_id());
			Map<String, String> oEffectiveSynonyms = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);
			Map<String, String> leftMatchedSynonyms = new HashMap<>();
			Map<String, String> leftUnmatchedSynonyms = new HashMap<>();
			Map<String, String> rightMatchedSynonyms = new HashMap<>();
			Map<String, String> rightUnmatchedSynonyms = new HashMap<>();

			// Get RuleExpression
			String ruleExpression = globalRule.getExpression();
			ruleExpression = (ruleExpression != null && !ruleExpression.trim().isEmpty()) ? ruleExpression.trim() : "";

			// Get MatchingRules
			String matchingRules = globalRule.getMatchingRules();
			matchingRules = (matchingRules != null && !matchingRules.trim().isEmpty()) ? matchingRules.trim() : "";

			// Get FilterCondition
			String filterCondition = globalRule.getFilterCondition();
			filterCondition = (filterCondition != null && !filterCondition.trim().isEmpty()) ? filterCondition.trim()
					: "";

			// Get Right Template FilterCondition
			String rightTemplatefilterCondition = globalRule.getRightTemplateFilterCondition();
			rightTemplatefilterCondition = (rightTemplatefilterCondition != null
					&& !rightTemplatefilterCondition.trim().isEmpty()) ? rightTemplatefilterCondition.trim() : "";

			Long rightTemplateId = globalRule.getIdRightData();

			// Variables to hold updated values
			String updated_ruleExpression = ruleExpression;
			String updated_matchingRules = matchingRules;
			String updated_filterCondition = filterCondition;
			String updated_rightTemplatefilterCondition = rightTemplatefilterCondition;

			Set<String> total_usedSynonymsList = new HashSet<String>();
			// For right template - Conditional Orphan with right global filter
			Set<String> r_total_usedSynonymsList = new HashSet<String>();

			boolean isRuleExpressionValid = false;
			boolean isMatchingRulesValid = false;
			boolean isFilterConditionValid = false;
			boolean isRightTemplateFilterConditionValid = false;
			boolean isRuleApplicable = false;

			/*
			 * Validate Rule Expression
			 */
			if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

				// Get the unique synonyms used in the RuleExpression
				String[] rexp_used_synonymn_cols = StringUtils.substringsBetween(updated_ruleExpression, "@", "@");
				Set<String> rexp_conditionColumns = new HashSet<>();
				if (rexp_used_synonymn_cols != null && rexp_used_synonymn_cols.length > 0) {
					rexp_conditionColumns = new HashSet<>(Arrays.asList(rexp_used_synonymn_cols));
					// Adding synonyms to total used synonym list
					total_usedSynonymsList.addAll(rexp_conditionColumns);
				}

				int rexp_usedSynonymCount = rexp_conditionColumns.size();
				int rexp_matchCount = 0;

				for (String sKey : oEffectiveSynonyms.keySet()) {
					String actual_sSynonym = oEffectiveSynonyms.get(sKey);
					// Synonym name will be surrounded by @
					String sSynonym = "@" + actual_sSynonym + "@";

					if (updated_ruleExpression.indexOf(sSynonym) > -1) {
						// Update the expression
						if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE))
							updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym, "originalDf." + sKey);
						else
							updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym, sKey);
						// Add synonym to total matched synonym list
						leftMatchedSynonyms.put(actual_sSynonym, sKey);
						++rexp_matchCount;
					}
				}
				total_usedSynonymsList.removeAll(leftMatchedSynonyms.keySet());
				total_usedSynonymsList.forEach(s -> {
					leftUnmatchedSynonyms.put(s, "");
				});

				/*
				 * If unmatched synonym found check for the possible_name for same synonym.
				 */
				if (leftUnmatchedSynonyms.size() > 0) {
					List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
					Set<String> unmatchedSet = new HashSet<>();
					unmatchedSet.addAll(total_usedSynonymsList);
					for (String unmatchedSynonym : unmatchedSet) {
						List<SynonymLibrary> synonymLibraries = globalRuleDAO
								.getSynonymListByDomainAndName(globalRule.getDomain_id(), unmatchedSynonym);
						if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
							for (SynonymLibrary library : synonymLibraries) {
								List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
										.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
								for (String possibleName : possibleNames) {
									if (columnsList.contains(possibleName)) {
										String sSynonym = "@" + unmatchedSynonym + "@";
										if (updated_ruleExpression.indexOf(sSynonym) > -1) {
											leftMatchedSynonyms.put(unmatchedSynonym, possibleName);
											total_usedSynonymsList.remove(unmatchedSynonym);
											leftUnmatchedSynonyms.remove(unmatchedSynonym);
											++rexp_matchCount;
											if (ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
													|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
												updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym,
														"originalDf." + possibleName);
											else
												updated_ruleExpression = updated_ruleExpression.replaceAll(sSynonym,
														possibleName);
											break;
										}
									}
								}
								String[] extraSynonyms = StringUtils.substringsBetween(updated_ruleExpression, "@",
										"@");
								if (extraSynonyms == null || extraSynonyms.length == 0) {
									break;
								}
							}
						}

					}
				}

				if (rexp_matchCount == rexp_usedSynonymCount) {
					isRuleExpressionValid = true;
					globalRule.setExpression(updated_ruleExpression);
				}

			} else if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE))
				isRuleExpressionValid = true;

			/*
			 * Validate Matching expression
			 */
			if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)) {

				String sGlobalExpr = "";
				String[] aMatchPair = null;

				try {
					sGlobalExpr = updated_matchingRules;
					aMatchPair = sGlobalExpr.split("=");

					// MatchingRules must contain 2 parts : Left and right parts
					if (aMatchPair.length < 2)
						return null;

					// For Orphan and Cross Referential rules, split the MatchingRules and take the
					// left part of it
					sGlobalExpr = aMatchPair[0].trim();

				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
					return null;
				}

				// Get the unique synonyms used in the MatchingRules
				String[] mr_used_synonymn_cols = StringUtils.substringsBetween(sGlobalExpr, "@", "@");
				Set<String> mr_conditionColumns = new HashSet<>();
				if (mr_used_synonymn_cols != null && mr_used_synonymn_cols.length > 0) {
					mr_conditionColumns = new HashSet<>(Arrays.asList(mr_used_synonymn_cols));
					// Adding synonyms to total used synonym list
					total_usedSynonymsList.addAll(mr_conditionColumns);
				}

				int mr_usedSynonymCount = mr_conditionColumns.size();
				int mr_matchCount = 0;

				for (String sKey : oEffectiveSynonyms.keySet()) {
					String actual_sSynonym = oEffectiveSynonyms.get(sKey);
					// Synonym name will be surrounded by @
					String sSynonym = "@" + actual_sSynonym + "@";
					if (sGlobalExpr.indexOf(sSynonym) > -1) {
						// For Orphan rules we need to add prefix "originalDf" to avoid conflict of same
						// names in left and right datasets
						sGlobalExpr = sGlobalExpr.replaceAll(sSynonym, "originalDf." + sKey);
						// Add synonym to total matched synonym list
						leftMatchedSynonyms.put(actual_sSynonym, sKey);
						++mr_matchCount;
					}
				}

				total_usedSynonymsList.removeAll(leftMatchedSynonyms.keySet());
				total_usedSynonymsList.forEach(s -> {
					leftUnmatchedSynonyms.put(s, "");
				});

				if (leftUnmatchedSynonyms.size() > 0) {
					List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
					Set<String> unmatchedSet = new HashSet<>();
					unmatchedSet.addAll(total_usedSynonymsList);
					for (String unmatchedSynonym : unmatchedSet) {
						List<SynonymLibrary> synonymLibraries = globalRuleDAO
								.getSynonymListByDomainAndName(globalRule.getDomain_id(), unmatchedSynonym);
						if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
							for (SynonymLibrary library : synonymLibraries) {
								List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
										.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
								for (String possibleName : possibleNames) {
									if (columnsList.contains(possibleName)) {
										String sSynonym = "@" + unmatchedSynonym + "@";
										if (sGlobalExpr.indexOf(sSynonym) > -1) {
											leftMatchedSynonyms.put(unmatchedSynonym, possibleName);
											total_usedSynonymsList.remove(unmatchedSynonym);
											leftUnmatchedSynonyms.remove(unmatchedSynonym);
											++mr_matchCount;
											sGlobalExpr = sGlobalExpr.replaceAll(sSynonym,
													"originalDf." + possibleName);
											break;
										}
									}
								}
								String[] extraSynonyms = StringUtils.substringsBetween(sGlobalExpr, "@", "@");
								if (extraSynonyms == null || extraSynonyms.length == 0) {
									break;
								}
							}
						}

					}
				}

				if (mr_matchCount == mr_usedSynonymCount) {
					isMatchingRulesValid = true;
					// Updated MatchingRules value
					updated_matchingRules = sGlobalExpr + "=" + aMatchPair[1].trim();
					globalRule.setMatchingRules(updated_matchingRules);
				}

			} else if (ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE))
				isMatchingRulesValid = true;

			/*
			 * Validate Filter condition
			 */
			if (ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_SQLINTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_DUPLICATE_CHECK)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_COMPLETENESS_CHECK)) {

				// Get the unique synonyms used in the filter condition
				String[] fc_used_synonymn_cols = StringUtils.substringsBetween(updated_filterCondition, "@", "@");
				Set<String> fc_conditionColumns = new HashSet<>();
				if (fc_used_synonymn_cols != null && fc_used_synonymn_cols.length > 0) {
					fc_conditionColumns = new HashSet<>(Arrays.asList(fc_used_synonymn_cols));

					// Adding synonyms to total used synonym list
					total_usedSynonymsList.addAll(fc_conditionColumns);
				}

				int fc_usedSynonymCount = fc_conditionColumns.size();
				int fc_matchCount = 0;

				for (String sKey : oEffectiveSynonyms.keySet()) {
					String actual_sSynonym = oEffectiveSynonyms.get(sKey);

					// Synonym name will be surrounded by braces
					String sSynonym = "@" + actual_sSynonym + "@";

					if (updated_filterCondition.indexOf(sSynonym) > -1) {
						updated_filterCondition = updated_filterCondition.replaceAll(sSynonym, sKey);
						// Add synonym to total matched synonym list
						leftMatchedSynonyms.put(actual_sSynonym, sKey);
						++fc_matchCount;
					}
				}

				total_usedSynonymsList.removeAll(leftMatchedSynonyms.keySet());
				total_usedSynonymsList.forEach(s -> {
					leftUnmatchedSynonyms.put(s, "");
				});

				if (leftUnmatchedSynonyms.size() > 0) {
					List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
					Set<String> unmatchedSet = new HashSet<>();
					unmatchedSet.addAll(total_usedSynonymsList);
					for (String unmatchedSynonym : unmatchedSet) {
						List<SynonymLibrary> synonymLibraries = globalRuleDAO
								.getSynonymListByDomainAndName(globalRule.getDomain_id(), unmatchedSynonym);
						if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
							for (SynonymLibrary library : synonymLibraries) {
								List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
										.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
								for (String possibleName : possibleNames) {
									if (columnsList.contains(possibleName)) {
										String sSynonym = "@" + unmatchedSynonym + "@";
										if (updated_filterCondition.indexOf(sSynonym) > -1) {
											leftMatchedSynonyms.put(unmatchedSynonym, possibleName);
											total_usedSynonymsList.remove(unmatchedSynonym);
											leftUnmatchedSynonyms.remove(unmatchedSynonym);
											++fc_matchCount;
											updated_filterCondition = updated_filterCondition.replaceAll(sSynonym,
													possibleName);
											break;
										}
									}
								}
								String[] extraSynonyms = StringUtils.substringsBetween(updated_filterCondition, "@",
										"@");
								if (extraSynonyms == null || extraSynonyms.length == 0) {
									break;
								}
							}
						}

					}
				}

				if (fc_matchCount == fc_usedSynonymCount) {
					isFilterConditionValid = true;
					globalRule.setFilterCondition(updated_filterCondition);
				}

			} else if (ruleType.equalsIgnoreCase(DatabuckConstants.ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.REFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.SQL_INTERNAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CROSSREFERENTIAL_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.DIRECT_QUERY_RULE))
				isFilterConditionValid = true;

			/*
			 * Validate Right Template Filter condition
			 */
			if ((ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_ORPHAN_RULE)
					|| ruleType.equalsIgnoreCase(DatabuckConstants.CONDITIONAL_CROSSREFERENTIAL_RULE))
					&& rightTemplatefilterCondition != null && !rightTemplatefilterCondition.trim().isEmpty()) {

				// Get effective synonyms of the Template
				Map<String, String> r_effectiveSynonyms = getEffectiveSynonymsForTemplate(rightTemplateId,
						expandedSynonymMap);
				LOG.debug("\n====> effectiveSynonymsMap of Right Template: " + r_effectiveSynonyms);

				// Get the unique synonyms used in the filter condition
				String[] rfc_used_synonymn_cols = StringUtils.substringsBetween(updated_rightTemplatefilterCondition,
						"@", "@");

				Set<String> rfc_conditionColumns = new HashSet<>();
				if (rfc_used_synonymn_cols != null && rfc_used_synonymn_cols.length > 0) {
					rfc_conditionColumns = new HashSet<>(Arrays.asList(rfc_used_synonymn_cols));
					// Adding synonyms to total used synonym list
					r_total_usedSynonymsList.addAll(rfc_conditionColumns);
				}

				int rfc_usedSynonymCount = rfc_conditionColumns.size();
				int rfc_matchCount = 0;

				for (String sKey : r_effectiveSynonyms.keySet()) {
					String actual_sSynonym = r_effectiveSynonyms.get(sKey);
					// Synonym name will be surrounded by braces
					String sSynonym = "@" + actual_sSynonym + "@";
					if (updated_rightTemplatefilterCondition.indexOf(sSynonym) > -1) {
						updated_rightTemplatefilterCondition = updated_rightTemplatefilterCondition.replaceAll(sSynonym,
								sKey);
						// Add synonym to total matched synonym list
						rightMatchedSynonyms.put(actual_sSynonym, sKey);
						++rfc_matchCount;
					}
				}
				r_total_usedSynonymsList.removeAll(rightMatchedSynonyms.keySet());
				r_total_usedSynonymsList.forEach(s -> {
					rightUnmatchedSynonyms.put(s, "");
				});

				if (rightUnmatchedSynonyms.size() > 0) {
					List<String> columnsList = validationCheckDAO
							.getDisplayNamesFromListDataDefinition(rightTemplateId);
					Set<String> unmatchedSet = new HashSet<>();
					unmatchedSet.addAll(r_total_usedSynonymsList);
					for (String unmatchedSynonym : unmatchedSet) {
						List<SynonymLibrary> synonymLibraries = globalRuleDAO
								.getSynonymListByDomainAndName(globalRule.getDomain_id(), unmatchedSynonym);
						if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
							for (SynonymLibrary library : synonymLibraries) {
								List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
										.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
								for (String possibleName : possibleNames) {
									if (columnsList.contains(possibleName)) {
										String sSynonym = "@" + unmatchedSynonym + "@";
										if (updated_rightTemplatefilterCondition.indexOf(sSynonym) > -1) {
											rightMatchedSynonyms.put(unmatchedSynonym, possibleName);
											r_total_usedSynonymsList.remove(unmatchedSynonym);
											rightUnmatchedSynonyms.remove(unmatchedSynonym);
											++rfc_matchCount;
											updated_rightTemplatefilterCondition = updated_rightTemplatefilterCondition
													.replaceAll(sSynonym, possibleName);
											break;
										}
									}
								}
								String[] extraSynonyms = StringUtils
										.substringsBetween(updated_rightTemplatefilterCondition, "@", "@");
								if (extraSynonyms == null || extraSynonyms.length == 0) {
									break;
								}
							}
						}

					}
				}

				if (rfc_matchCount == rfc_usedSynonymCount) {
					isRightTemplateFilterConditionValid = true;
					globalRule.setRightTemplateFilterCondition(updated_rightTemplatefilterCondition);
				}

			} else
				isRightTemplateFilterConditionValid = true;

			if (isRuleExpressionValid && isMatchingRulesValid && isFilterConditionValid
					&& isRightTemplateFilterConditionValid)
				isRuleApplicable = true;

			List<String> leftColumnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
			List<String> rightColumnsList = validationCheckDAO
					.getDisplayNamesFromListDataDefinition(globalRule.getIdRightData());

			response.put("globalRuleDetails", globalRule);
			response.put("idData", idData);
			response.put("isRuleApplicable", isRuleApplicable);
			response.put("leftColumnDetails", leftColumnsList);
			response.put("rightColumnDetails", rightColumnsList);
			response.put("leftMatchedSynonyms", getSynonymsDetails(leftMatchedSynonyms));
			response.put("leftUnmatchedSynonyms", getSynonymsDetails(leftUnmatchedSynonyms));
			response.put("rightMatchedSynonyms", getSynonymsDetails(rightMatchedSynonyms));
			response.put("rightUnmatchedSynonyms", getSynonymsDetails(rightUnmatchedSynonyms));

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}

		return response;
	}

	public JSONArray getSynonymsDetails(Map<String, String> synonymsList) {
		JSONArray response = new JSONArray();
		try {
			for (Map.Entry<String, String> entry : synonymsList.entrySet()) {
				JSONObject json = new JSONObject();
				json.put("synonymName", entry.getKey());
				json.put("columnName", entry.getValue());
				response.put(json);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * Add possible names for synonyms.
	 */
	public boolean addPossibleNamesToSynonyms(int domainId, JSONArray unmatchedSynonyms) {
		try {
			if (unmatchedSynonyms != null && unmatchedSynonyms.length() > 0) {
				for (int i = 0; i < unmatchedSynonyms.length(); i++) {
					JSONObject jsonObject = unmatchedSynonyms.getJSONObject(i);
					String synonymName = jsonObject.getString("synonymName");
					String columnName = jsonObject.getString("columnName");

					// Get synonym Id by domainId and synonym name
					ruleFields rc = new ruleFields();
					rc.setUsercolumns(synonymName);
					rc.setDomain_id(domainId);

					// Fetch the synonym Id from the domain
					int syn_id = globalRuleDAO.getsynosymId(rc);
					LOG.debug("\n====>Synonym Id: " + syn_id);
					boolean updateStatus = false;

					// If synonym is found - update the synonym, add the template column to possible
					// values
					if (syn_id > 0) {
						// Get the possible names of the synonym
						String possibleValues = globalRuleDAO.getPossibleName(rc);
						// Convert to possibleValues to List
						List<String> possibleValues_list = new ArrayList<String>();
						if (possibleValues != null && !possibleValues.trim().isEmpty()) {
							possibleValues_list = Arrays.asList(possibleValues.split(","));
						}

						// Check if the template column is already present in possible values list of
						// synonym
						if (possibleValues_list != null && possibleValues_list.contains(columnName)) {
							updateStatus = true;
						} else {
							// Update the synonym, add the template column to possible values
							updateStatus = globalRuleDAO.updateSynonymPossiblenames(syn_id, columnName);
						}
					}
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}
		return true;
	}

	public JSONObject updateNullFilterColumn(long idApp, long idData, long ruleId, String nullFilterColumns) {

		String status = "failed";
		String message = "";
		JSONObject jsonObject = new JSONObject();
		try {
			// Get the validation approval status
			LOG.info("\n====> Get the validation approval status ..");
			String validationStatus = ruleCatalogDao.getValidationApprovalStatus(idApp);

			ruleCatalogDao.updateNullFilterColumnsOfRuleTemplateMapping(idData, ruleId, nullFilterColumns);

			int count = ruleCatalogDao.updateNullFilterColumnsOfRuleCatlog(idApp, ruleId, nullFilterColumns,
					validationStatus.trim());
			if (count > 0) {
				status = "success";
				message = "Null filter columns updated successfully.";
			} else {
				message = "Null filter columns not updated.";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		jsonObject.put("status", status);
		jsonObject.put("message", message);
		return jsonObject;
	}

	public boolean validateSynonymExpression(String exp) {
		boolean result = true;
		try {
			if (exp.trim() != null && !exp.trim().isEmpty()) {
				List<String> synonyms_in_exp = Arrays.asList(StringUtils.substringsBetween(exp.trim(), "@", "@"));
				synonyms_in_exp.replaceAll(synm -> synm.trim());
				if (synonyms_in_exp != null && synonyms_in_exp.contains("")) {
					result = false;
				}
				if(result){
					for (String synonymExp : synonyms_in_exp) {
						if (!synonymExp.matches("^[a-zA-Z0-9_]*$")) {
							result = false;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject runValidationByCustomRuleId(String tableName, long ruleId) {
		long idData = 0l;
		String status = "failed";
		String message = "";
		String uniqueValidId = "";
		long existingValId = 0l;
		JSONObject jsonResult = new JSONObject();
		JSONObject result = new JSONObject();

		List<HashMap<String, String>> eligibleRulesList = null;
		try {
			//validate ruleId
			ListColGlobalRules listColGlobalRules = globalRuleDAO.getGlobalRuleById(ruleId);
			if (listColGlobalRules != null){

				//get templateId by table name
				idData = listDataSourceDao.getActiveIdDataByTableName(tableName);

			LOG.debug("Template_Id: "+idData);
			if (idData > 0) {
				//get Exixting Validation By idData
				existingValId = validationCheckDAO.getMaxValidationByIdData(idData);
				ListApplications listApp = validationCheckDAO.getdatafromlistapplications(existingValId);
				if(existingValId > 0){
					Map<String, Object> dtsResult = iResultsDAO.getAvgDtsByIdApp(existingValId);
					if(!dtsResult.isEmpty()){
						result.put("aggregateDQI",dtsResult.get("aggregateDQI"));
						result.put("existingValName",dtsResult.get("validationName"));
						result.put("isExistingValExecuted",true);
					} else {
						result.put("aggregateDQI", 0);
						result.put("existingValName", listApp.getName());
						result.put("isExistingValExecuted", false);
					}
				}else
					result.put("isValidationExist",false);

				//check the ruleId is eligiable for template
				LOG.debug("====> Checking Global Rule eligible for Template");
				eligibleRulesList = checkGlobalRuleEligibleForTemplate(idData, ruleId);
				if (eligibleRulesList != null && !eligibleRulesList.isEmpty()) {
					// Link the Global Rule to Template
					LOG.debug("====> Link the Global Rule to Template");
					JSONObject statusJson = linkGlobalRuleToTemplateById(idData, ruleId);
					if (statusJson.get("status").toString().equalsIgnoreCase("failed")) {
						message = "Failed to link Global Rule to Template";
					} else {
						//create validation
						ListDataSource listDataSourceobj = validationService.getListDataSourceDataOfIdData(idData);
						String sql = "SELECT MAX(idApp) FROM listApplications";
						Long valId = jdbcTemplate.queryForObject(sql, Long.class);
						String validationCheckName = valId + 1 + "_" + listDataSourceobj.getName() + "_Validation";

						long idApp = validationCheckDAO.insertintolistapplications(validationCheckName, listDataSourceobj.getDescription(), "Data Forensics", idData, listDataSourceobj.getCreatedBy(),
								0.0d, "N", null, null, "local",
								"N", Long.valueOf(listDataSourceobj.getProjectId()), listDataSourceobj.getCreatedByUser(), listDataSourceobj.getDomain(), 2, "N",DatabuckConstants.DEFAULT_VALIDATION_JOB_SIZE);

						//update listApplication and enable RecordCountAnomaly
						ListApplications listApplications = validationCheckDAO.getListApplicationForRCA(idApp, listDataSourceobj.getProjectId(), listDataSourceobj.getDomain(), listDataSourceobj.getName());
						LOG.debug("Validation Id: " + idApp);

						int updationStatus = validationCheckDAO.updateintolistapplicationForAjaxRequest(listApplications);

						if (updationStatus > 0) {
							//update Rule catalog
							ruleCatalogService.updateRuleCatalog(idApp);

							//update validation rule catalog status to unit test ready
							boolean approvalStatus = ruleCatalogDao.updateValidationRuleCatalogStatus(idApp, 17, null, listDataSourceobj.getCreatedBy(), listDataSourceobj.getCreatedByUser());
							if (approvalStatus) {
								// Get deployMode
								String deployMode = clusterProperties.getProperty("deploymode");
								deployMode = (deployMode != null && deployMode.trim().equalsIgnoreCase("2")) ? "local" : "cluster";
								// Place job in queue
								uniqueValidId = iTaskDAO.insertRunScheduledTask(idApp, "queued", deployMode, null, null);
								LOG.debug("Validation uniqueId .." + uniqueValidId);

								if (uniqueValidId != null && !uniqueValidId.isEmpty()) {
									status = "success";
									result.put("uniqueValidId", uniqueValidId);
									result.put("idApp", idApp);
									result.put("validationName", validationCheckName);
									result.put("idData", idData);
									result.put("tamplateName", listDataSourceobj.getName());
								} else
									message = "Failed to run validation";
							} else
								message = "Failed to update validation rule catalog status";
						} else
							message = "Failed to update validation";
					}
				} else
					message = "Global Rule is not eligible for table name: " + tableName;
			} else
				message = "No template found for table name: " + tableName;
		}else
				message = "Invalid rule id: " + ruleId;

		} catch (Exception ex) {
			ex.printStackTrace();
			message = ex.getMessage();
		}
		jsonResult.put("result",result);
		jsonResult.put("status", status);
		jsonResult.put("message", message);
		jsonResult.put("uniqueValidId", uniqueValidId);
		return jsonResult;
	}

	public List<HashMap<String,String>> checkGlobalRuleEligibleForTemplate(long idData, long ruleId) {

		List<HashMap<String, String>> eligibleRulesList = null;
		try {
			// Get template details
			ListDataSource listDataSource = validationCheckDAO.getdatafromlistdatasource(idData).get(0);

			// Get domain id of the template
			Integer domainId = listDataSource.getDomain();

			// Get the synonyms for the domain
			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(domainId);

			// Get effective synonyms of the Template
			Map<String, String> effectiveSynonymsMap = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);

			LOG.debug("\n====> effectiveSynonymsMap of Template: " + effectiveSynonymsMap);

			// Get available global rules for domainId and enterprise domainId
			ListColGlobalRules globalRule = globalRuleDAO.getGlobalRuleById(ruleId);
			List<HashMap<String,String>> allGlobalRules = getGlobalRuleDetails(globalRule);

			// Get column names of templateId
			List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);

			// Get the applicable global rules from all the rules
			eligibleRulesList = getApplicableOrNonApplicableGlobalRules(allGlobalRules, columnsList,
					effectiveSynonymsMap, expandedSynonymMap, true, idData, domainId);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return eligibleRulesList;
	}

	public List<HashMap<String, String>> getGlobalRuleDetails(ListColGlobalRules listColGlobalRules) {
		List<HashMap<String, String>> globalRuleDetails = new ArrayList<>();
		try {
			HashMap<String, String> globalRuleDetailMap = new HashMap<String, String>();
			globalRuleDetailMap.put("Id", String.valueOf(listColGlobalRules.getIdListColrules()));
			globalRuleDetailMap.put("DomainName", listColGlobalRules.getDomain());
			globalRuleDetailMap.put("Name", listColGlobalRules.getRuleName());
			globalRuleDetailMap.put("RuleType", listColGlobalRules.getRuleType());
			globalRuleDetailMap.put("FilterCondition", listColGlobalRules.getFilterCondition());
			globalRuleDetailMap.put("RightTemplateFilterCondition", listColGlobalRules.getRightTemplateFilterCondition() == null ? "" : listColGlobalRules.getRightTemplateFilterCondition());
			globalRuleDetailMap.put("MatchingRules", listColGlobalRules.getMatchingRules());
			globalRuleDetailMap.put("Expression", listColGlobalRules.getExpression());
			Long idRightData = listColGlobalRules.getIdRightData();
			if (idRightData == null || idRightData <= 0l)
				idRightData = 0l;
			globalRuleDetailMap.put("RightTemplateId", idRightData.toString());
			globalRuleDetails.add(globalRuleDetailMap);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return globalRuleDetails;
	}

	public JSONObject getGlobalFilterDetails(long idData, GlobalFilters globalFilter) {
		JSONObject response = new JSONObject();
		try {

			Map<String, String> expandedSynonymMap = getExpandedSynonymMapForDomain(globalFilter.getDomainId());
			Map<String, String> oEffectiveSynonyms = getEffectiveSynonymsForTemplate(idData, expandedSynonymMap);
			Map<String, String> matchedSynonyms = new HashMap<>();
			Map<String, String> unmatchedSynonyms = new HashMap<>();

			// Get FilterCondition
			String filterCondition = globalFilter.getFilterCondition();
			filterCondition = (filterCondition != null && !filterCondition.trim().isEmpty()) ? filterCondition.trim() : "";

			// Variables to hold updated values
			String updated_filterCondition = filterCondition;
			Set<String> total_usedSynonymsList = new HashSet<String>();
			boolean isFilterConditionValid = false;

			/*
			 * Validate Filter condition
			 */
			if (globalFilter!=null && !globalFilter.getFilterCondition().isEmpty()) {

				// Get the unique synonyms used in the filter condition
				String[] fc_used_synonymn_cols = StringUtils.substringsBetween(updated_filterCondition, "@", "@");
				Set<String> fc_conditionColumns = new HashSet<>();
				if (fc_used_synonymn_cols != null && fc_used_synonymn_cols.length > 0) {
					fc_conditionColumns = new HashSet<>(Arrays.asList(fc_used_synonymn_cols));
					// Adding synonyms to total used synonym list
					total_usedSynonymsList.addAll(fc_conditionColumns);
				}

				int fc_usedSynonymCount = fc_conditionColumns.size();
				int fc_matchCount = 0;

				for (String sKey : oEffectiveSynonyms.keySet()) {
					String actual_sSynonym = oEffectiveSynonyms.get(sKey);

					// Synonym name will be surrounded by braces
					String sSynonym = "@" + actual_sSynonym + "@";

					if (updated_filterCondition.indexOf(sSynonym) > -1) {
						updated_filterCondition = updated_filterCondition.replaceAll(sSynonym, sKey);
						// Add synonym to total matched synonym list
						matchedSynonyms.put(actual_sSynonym, sKey);
						++fc_matchCount;
					}
				}

				total_usedSynonymsList.removeAll(matchedSynonyms.keySet());
				total_usedSynonymsList.forEach(s -> {
					unmatchedSynonyms.put(s, "");
				});

				if (unmatchedSynonyms.size() > 0) {
					List<String> columnsList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
					Set<String> unmatchedSet = new HashSet<>();
					unmatchedSet.addAll(total_usedSynonymsList);
					for (String unmatchedSynonym : unmatchedSet) {
						List<SynonymLibrary> synonymLibraries = globalRuleDAO.getSynonymListByDomainAndName(globalFilter.getDomainId(), unmatchedSynonym);
						if (synonymLibraries != null && !synonymLibraries.isEmpty()) {
							for (SynonymLibrary library : synonymLibraries) {
								List<String> possibleNames = Arrays.stream(library.getPossibleNames().split(","))
										.filter(s -> columnsList.contains(s)).collect(Collectors.toList());
								for (String possibleName : possibleNames) {
									if (columnsList.contains(possibleName)) {
										String sSynonym = "@" + unmatchedSynonym + "@";
										if (updated_filterCondition.indexOf(sSynonym) > -1) {
											matchedSynonyms.put(unmatchedSynonym, possibleName);
											total_usedSynonymsList.remove(unmatchedSynonym);
											unmatchedSynonyms.remove(unmatchedSynonym);
											++fc_matchCount;
											updated_filterCondition = updated_filterCondition.replaceAll(sSynonym, possibleName);
											break;
										}
									}
								}
								String[] extraSynonyms = StringUtils.substringsBetween(updated_filterCondition, "@", "@");
								if (extraSynonyms == null || extraSynonyms.length == 0) {
									break;
								}
							}
						}

					}
				}

				globalFilter.setFilterCondition(updated_filterCondition);
				if (fc_matchCount == fc_usedSynonymCount) {
					isFilterConditionValid = true;
				}

			}

			List<String> columnList = validationCheckDAO.getDisplayNamesFromListDataDefinition(idData);
			Map<String, String> globalFilterDetails = new HashMap<>();
			globalFilterDetails.put("domain", globalFilter.getDomain());
			globalFilterDetails.put("filterCondition", globalFilter.getFilterCondition());
			globalFilterDetails.put("filterName", globalFilter.getFilterName());
			globalFilterDetails.put("description", globalFilter.getDescription());

			response.put("globalFilterDetails", globalFilterDetails);
			response.put("idData", idData);
			response.put("isFilterConditionValid", isFilterConditionValid);
			response.put("columnDetails", columnList);
			response.put("matchedSynonyms", getSynonymsDetails(matchedSynonyms));
			response.put("unmatchedSynonyms", getSynonymsDetails(unmatchedSynonyms));

		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		}

		return response;
	}

}
