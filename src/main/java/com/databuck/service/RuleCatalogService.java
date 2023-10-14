package com.databuck.service;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.*;
import com.databuck.econstants.DeltaType;
import com.databuck.econstants.RuleActionTypes;
import com.databuck.econstants.RuleCatalogCheckTypes;
import com.databuck.econstants.TemplateDeltaStatusTypes;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class RuleCatalogService {

	@Autowired
	private IViewRuleDAO viewRuleDao;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private JsonDaoI jsonDaoI;

	@Autowired
	private DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	@Autowired
	private IValidationCheckDAO validationcheckdao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	GlobalRuleDAO globalRuleDao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private IExtendTemplateRuleDAO extendTemplateRuleDAO;

	@Autowired
	private ITaskDAO iTaskDAO;

	@Autowired
	private IListDataSourceDAO iListDataSourceDAO;

	@Autowired
	private IProjectDAO iProjectDAO;

	@Autowired
	private IResultsDAO iResultsDAO;

	@Autowired
	private ITemplateViewDAO templateViewDAO;

	@Autowired
	private IListDataSourceDAO listDataSourceDao;
	
	private static final Logger LOG = Logger.getLogger(RuleCatalogService.class);

	public interface GetChecksData {
		List<RuleCatalog> getChecksDataForGivenCheck(RuleCatalogCheckSpecification oCheckSpec,
				ListApplications oListApplication);
	}

	public interface GetResultstData {
		Map<String, Object> getResultstDataForGivenCheck(Map<String, Object> oRuleCatalogRow);
	}

	/*
	 * This method is used to check if the Rule catalog is enabled
	 */
	public boolean isRuleCatalogEnabled() {
		// Check if RuleCatalog is enabled
		String isRuleCatalogDiscovery = appDbConnectionProperties.getProperty("isRuleCatalogDiscovery");
		boolean rcEnabled = (isRuleCatalogDiscovery != null && isRuleCatalogDiscovery.trim().equalsIgnoreCase("Y"))
				? true
				: false;
		return rcEnabled;
	}

	public boolean isValidationStagingActivated(long idApp) {
		boolean rcStatingEnabled = false;
		try {
			// Get the validation approval status
			String approvalStatus = ruleCatalogDao.getValidationApprovalStatus(idApp);

			rcStatingEnabled = (approvalStatus != null
					&& approvalStatus.trim().equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) ? true
							: false;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return rcStatingEnabled;
	}

	/*
	 * This method is to update Rule catalog status
	 */
	public boolean updateRuleCatalogStatus(long idApp, int approve_status, String approve_comments, Long reviewedByUser,
			String approverName) {

		LOG.debug("\n*****====> updateRuleCatalogStatus for validation [" + idApp + "]- START <====*****");

		boolean status = false;

		try {
			// Get the validation actual catalog approval status
			String val_cur_approve_status = ruleCatalogDao.getValidationApprovalStatus(idApp);
			LOG.debug("\n====> Validation [" + idApp + "] current approve status: " + val_cur_approve_status);

			// Get the new approval status name
			String val_new_approve_status = ruleCatalogDao.getApproveStatusNameById(approve_status);
			LOG.debug("\n====> Validation [" + idApp + "] new approve status: " + val_new_approve_status);

			if (val_new_approve_status != null) {
				/*
				 * If current Validation approval status is Approved, approval will be applied
				 * on staging
				 */
				if (val_cur_approve_status != null && val_cur_approve_status.trim()
						.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {
					/*
					 * If the new status is "APPROVED FOR PRODUCTION", it means validation staging
					 * is approved.
					 *
					 * Copy validation details from staging_listApplications to listApplications
					 * Clear actual catalog and copy all the rules from staging and update staging
					 * status to CREATED
					 */

					// Update staging catalog status
					LOG.debug("\n====> Validation [" + idApp
							+ "] is already 'Approved For Production', updating the status of Staging Rule Catalog ..");

					status = ruleCatalogDao.updateValidationStagingRuleCatalogStatus(idApp, approve_status,
							approve_comments, reviewedByUser, approverName);

					if (status) {
						if (val_new_approve_status.trim()
								.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {

							LOG.debug("\n====> Validation [" + idApp
									+ "] Staging changes are 'Approved For Production', moving the rules from staging to actual rule catalog");

							// Copy validation details from staging_listApplications to
							// listApplications
							LOG.debug("\n====> Copy validation [" + idApp
									+ "] details from staging_listApplications to listApplications table");
							ruleCatalogDao.copyLisApplicationsFromStagingToActual(idApp);
							ruleCatalogDao.copyListDfTranRuleFromStagingToActual(idApp);

							// Clear actual catalog
							LOG.debug("\n====> Clear the actual Rule catalog of the validation [" + idApp + "]");
							ruleCatalogDao.clearRulesFromActualRuleCatalog(idApp);

							// Copy all the rules from staging to actual
							LOG.debug("\n====> Copy all the rules of validation [" + idApp
									+ "] from Staging to Actual Rule catalog");
							ruleCatalogDao.copyRulesFromStagingCatalogToActual(idApp);

							// update staging status to CREATED
							LOG.debug("\n====> Reset the Staging approve status to 'CREATED' for validation ["
									+ idApp + "]");
							ruleCatalogDao.updateStagingRuleCatalogStatusToCreated(idApp);

							// Delete all the deactivated custom rules which are no longer used
							LOG.debug(
									"\n====> Delete all the deactivated custom rules associated with validation ["
											+ idApp + "]");
							ruleCatalogDao.deleteUnusedDeactivatedCustomRules();

							// Delete all the deactivated global rules which are no longer used
							LOG.debug(
									"\n====> Delete all the deactivated global rules associated with validation ["
											+ idApp + "]");
							ruleCatalogDao.deleteUnusedDeactivatedGlobalRules();
						}
					}

				} else {
					/*
					 * If the new status is "APPROVED FOR PRODUCTION", it means validation is
					 * approved.
					 *
					 * Copy validation details from listApplications to staging_listApplications
					 * Clear staging and copy all the Rules from actual to staging rule catalog and
					 * update staging status to CREATED
					 */

					// Update actual catalog status
					LOG.debug(
							"\n====> Validation [" + idApp + "] , updating the status of Actual Rule Catalog ..");

					status = ruleCatalogDao.updateValidationRuleCatalogStatus(idApp, approve_status, approve_comments,
							reviewedByUser, approverName);

					if (status) {
						if (val_new_approve_status.trim()
								.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {

							LOG.debug("\n====> Validation [" + idApp
									+ "] is 'Approved For Production', freeze the actual rule catalog and create staging rule catalog");

							// Copy validation details from listApplications to
							// staging_listApplications
							LOG.debug("\n====> Copy validation [" + idApp
									+ "] details from listApplications to staging_listApplications table");
							ruleCatalogDao.copyLisApplicationsFromActualToStaging(idApp);
							ruleCatalogDao.copyListDfTranRuleFromActualToStaging(idApp);

							// Clear Staging
							LOG.debug(
									"\n====> Clear the Staging Rule catalog of the validation [" + idApp + "]");
							ruleCatalogDao.clearRulesFromStagingRuleCatalog(idApp);

							// Copy all the rules from actual to staging
							LOG.debug("\n====> Copy all the rules of validation [" + idApp
									+ "] from Actual to Staging Rule catalog");
							ruleCatalogDao.copyRulesFromActualCatalogToStaging(idApp);

							// update staging status to CREATED
							LOG.debug("\n====> update the Staging approve status to 'CREATED' for validation ["
									+ idApp + "]");
							ruleCatalogDao.updateStagingRuleCatalogStatusToCreated(idApp);
						}
					}

				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n*****====> updateRuleCatalogStatus for validation [" + idApp + "]- END <====*****");

		return status;
	}

	/*
	 * This method is to fetch all the data from actual or staging Rule catalog
	 * depending on validation approval status to display in UI
	 */
	public JSONObject getRuleCatalogRecordList(long idApp) {

		LOG.debug("\n*****====> getRuleCatalogRecordList for validation [" + idApp + "] - START <====*****");

		JSONObject oJsonRetValue = new JSONObject();
		try {

			List<RuleCatalog> outputRulesList = new ArrayList<RuleCatalog>();

			// Read actual rule catalog
			LOG.info("\n====> Read rules from actual rule catalog ..");
			List<RuleCatalog> catalogRulesList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

			// Read stating rule catalog
			LOG.info("\n====> Read rules from staging rule catalog ..");
			List<RuleCatalog> stagingCatalogRulesList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);

			// Get the validation approval status
			LOG.info("\n====> Get the validation approval status ..");
			String approvalStatus = ruleCatalogDao.getValidationApprovalStatus(idApp);

			List<RuleCatalog> deletedRulesList = new ArrayList<RuleCatalog>();
			List<RuleCatalog> newRulesList = new ArrayList<RuleCatalog>();
			List<RuleCatalog> unchangedRulesList = new ArrayList<RuleCatalog>();
			List<RuleCatalog> modifiedRulesList = new ArrayList<>();

			/*
			 * If Validation approval status is Approved, Find the differences between
			 * staging and actual
			 */
			if (approvalStatus != null
					&& approvalStatus.trim().equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {

				// Find the differences and mark them using some new column (as unchanged,
				// modified, new and deleted)
				LOG.info(
						"\n====> Validation is 'Approved For Production', finding the differences between actual and staging ..");

				if (stagingCatalogRulesList != null && !stagingCatalogRulesList.isEmpty()) {

					// Identify the new rules
					LOG.info("\n====> Identifying new rules ....");

					if (catalogRulesList != null && !catalogRulesList.isEmpty()) {

						for (RuleCatalog stg_rc_check : stagingCatalogRulesList) {
							boolean matchFound = false;

							for (RuleCatalog approved_rc_check : catalogRulesList) {
								boolean isRuleModified = false;

								if (stg_rc_check.getRuleType().equalsIgnoreCase(approved_rc_check.getRuleType())
										&& stg_rc_check.getRuleCategory()
												.equalsIgnoreCase(approved_rc_check.getRuleCategory())
										&& ((stg_rc_check.getRuleCategory()
												.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
												&& stg_rc_check.getColumnName()
														.equalsIgnoreCase(approved_rc_check.getColumnName()))
												|| (stg_rc_check.getRuleCategory()
														.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
														&& stg_rc_check.getCustomOrGlobalRuleId() == approved_rc_check
																.getCustomOrGlobalRuleId()))) {
									matchFound = true;

									// Check there are any changes in rule details
									if (stg_rc_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& !stg_rc_check.getRuleType()
													.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)
											&& stg_rc_check.getThreshold() != approved_rc_check.getThreshold()) {
										isRuleModified = true;

									} else if (stg_rc_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& stg_rc_check.getRuleType()
													.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)
											&& ((stg_rc_check.getRuleExpression() == null
													&& approved_rc_check.getRuleExpression() != null)
													|| (stg_rc_check.getRuleExpression() != null && !stg_rc_check.getRuleExpression()
															.equals(approved_rc_check.getRuleExpression())))) {
										isRuleModified = true;

									} else if (stg_rc_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
											&& isCustomRuleModified(approved_rc_check, stg_rc_check)) {
										isRuleModified = true;
									}

									if (isRuleModified) {
										// Changed rule will be added to modifiedRulesList
										modifiedRulesList.add(stg_rc_check);

										// Actual rule will be added to deletedRulesList
										deletedRulesList.add(approved_rc_check);
									} else
										unchangedRulesList.add(stg_rc_check);

									break;
								}

							}

							// If this rule is not found in rule catalog, it is a new rule it should be
							// added to rule catalog
							if (!matchFound) {
								newRulesList.add(stg_rc_check);
							}
						}

						// Identify the deleted rules
						LOG.info("\n====> Identifying deleted rules ....");

						for (RuleCatalog approved_rc_check : catalogRulesList) {
							boolean matchFound = false;

							for (RuleCatalog stg_rc_check : stagingCatalogRulesList) {

								if (stg_rc_check.getRuleType().equalsIgnoreCase(approved_rc_check.getRuleType())
										&& stg_rc_check.getRuleCategory()
												.equalsIgnoreCase(approved_rc_check.getRuleCategory())
										&& ((stg_rc_check.getRuleCategory()
												.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
												&& stg_rc_check.getColumnName()
														.equalsIgnoreCase(approved_rc_check.getColumnName()))
												|| (stg_rc_check.getRuleCategory()
														.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
														&& stg_rc_check.getCustomOrGlobalRuleId() == approved_rc_check
																.getCustomOrGlobalRuleId()))) {
									matchFound = true;
									break;
								}

							}

							// If a rule is not found in staging, it is a deleted rule
							if (!matchFound) {
								deletedRulesList.add(approved_rc_check);
							}
						}

					} else {
						// If there no checks present in rule catalog, all the rules in staging are new
						// rules
						LOG.error(
								"\n====> No rules found in actual, all the rules in the staging rule catalog are considered new rules ..");
						newRulesList = stagingCatalogRulesList;
					}

				} else {
					// If there no checks found in staging, all the rules in the actual rule
					// catalog are the deleted rules
					LOG.error(
							"\n====> No rules found in staging, all the rules in the actual rule catalog are considered deleted rules .. ");
					deletedRulesList = catalogRulesList;
				}

			} else {
				LOG.error(
						"\n====> Validation is not 'Approved For Production', hence reading all the rules from actual rule catalog ..");
				unchangedRulesList = catalogRulesList;
			}

			// Set the DeltaType NOCHANGE to Unchanged rules
			LOG.info("\n====> Set DeltaType as 'NOCHANGE' for Unchanged rules");
			if (unchangedRulesList != null && !unchangedRulesList.isEmpty()) {
				for (RuleCatalog rc_check : unchangedRulesList) {
					rc_check.setDeltaType(DeltaType.NOCHANGE);
					outputRulesList.add(rc_check);
				}
			}

			// Set the DeltaType New to new rules
			LOG.info("\n====> Set DeltaType as 'NEW' for new rules");
			if (newRulesList != null && !newRulesList.isEmpty()) {
				for (RuleCatalog rc_check : newRulesList) {
					rc_check.setDeltaType(DeltaType.NEW);
					outputRulesList.add(rc_check);
				}
			}

			// Set the DeltaType CHANGED to modify rules
			LOG.info("\n====> Set DeltaType as 'CHANGED' for new rules");
			if (modifiedRulesList != null && !modifiedRulesList.isEmpty()) {
				for (RuleCatalog rc_check : modifiedRulesList) {
					rc_check.setDeltaType(DeltaType.CHANGED);
					outputRulesList.add(rc_check);
				}
			}

			// Set the DeltaType Missing to deleted rules
			LOG.info("\n====> Set DeltaType as 'MISSING' for deleted rules");
			if (deletedRulesList != null && !deletedRulesList.isEmpty()) {
				for (RuleCatalog rc_check : deletedRulesList) {
					rc_check.setDeltaType(DeltaType.MISSING);
					outputRulesList.add(rc_check);
				}
			}

			ObjectMapper oMapper = new ObjectMapper();
			JSONArray aJsonArray = new JSONArray(oMapper.writeValueAsString(outputRulesList));
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonArray);

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}

		LOG.debug("\n*****====> getRuleCatalogRecordList for validation [" + idApp + "] - END <====*****");

		return oJsonRetValue;

	}

	/*
	 * This method is used to create rule catalog for a validation
	 */
	public void createRuleCatalog(long nIdApp) {

		LOG.debug("\n*****====> createRuleCatalog for validation [" + nIdApp + "]- START <====*****");

		try {
			// Get the validation details from actual table i.e., listApplications
			LOG.info("\n====> Get the validation details from actual table i.e., listApplications table ..");
			ListApplications listApplication = validationcheckdao.getdatafromlistapplications(nIdApp);

			// Get all the rules associated with the validation
			List<RuleCatalog> aRetDataRows = getAllRulesAssociatedWithValidation(nIdApp, listApplication, false);

			// Insert into Rules into RuleCatalog
			LOG.info("\n====> Insert Rules into RuleCatalog ..");
			ruleCatalogDao.insertRuleCatalogRecords(aRetDataRows, nIdApp);

			// Update the status to 'CREATED'
			LOG.info("\n====> Update the status to 'CREATED' ..");
			ruleCatalogDao.updateRuleCatalogStatusToCreated(nIdApp);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n*****====> createRuleCatalog for validation [" + nIdApp + "]- END <====*****");
	}

	/*
	 * This method is to update the actual or stating rule catalog depending on
	 * validation approval status
	 */
	public void updateRuleCatalog(long idApp) {

		LOG.debug("\n*****====> updateRuleCatalog for validation [" + idApp + "]- START <====*****");

		// Get the validation approval status
		String approvalStatus = ruleCatalogDao.getValidationApprovalStatus(idApp);
		LOG.debug("\n====> Validation Approval Status: " + approvalStatus);

		/*
		 * If the validation is not Approved and Rule catalog is not loaded, create new
		 * catalog
		 */
		if ((approvalStatus == null
				|| !approvalStatus.trim().equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2))
				&& !validationcheckdao.isRuleCatalogExists(idApp)) {
			LOG.error("\n====> validation is not Approved and Rule catalog is not loaded, creating catalog ..");
			createRuleCatalog(idApp);
		}
		/*
		 * If Validation approval status is Approved, all the changes will be added in
		 * staging
		 */
		else if (approvalStatus != null
				&& approvalStatus.trim().equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {

			LOG.info("\n====> Validation is Approved, updating changes in Staging rule catalog ..");
			updateRuleCatalogStaging(idApp);
		}
		/*
		 * If Validation approval status is NOT Approved, all the changes will be
		 * updated in the actual catalog and staging will be empty
		 */
		else {
			LOG.error("\n====> Validation is not Approved, updating changes in actual rule catalog ..");
			updateActualRuleCatalog(idApp);
		}

		LOG.debug("\n*****====> updateRuleCatalog for validation [" + idApp + "]- END <====*****");
	}

	/*
	 * This method is to update the actual rule catalog of the validation.
	 */
	private void updateActualRuleCatalog(long idApp) {
		LOG.debug("\n*****====> updateActualRuleCatalog for validation [" + idApp + "]- START <====*****");

		try {
			// Get all the rules present in rule catalog
			LOG.info("\n====> Get all the rules present in rule catalog ..");
			List<RuleCatalog> catalogRulesList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

			// Get the validation details from actual table i.e., listApplications
			LOG.info("\n====> Get the validation details from actual table i.e., listApplications table ..");
			ListApplications listApplication = validationcheckdao.getdatafromlistapplications(idApp);

			// Get all the checks associated
			LOG.info("\n====> Get all the rules associated with validation based on checks enabled ..");
			List<RuleCatalog> allRulesList = getAllRulesAssociatedWithValidation(idApp, listApplication, false);

			// Find the differences
			List<RuleCatalog> deletedRulesList = new ArrayList<RuleCatalog>();
			List<RuleCatalog> newRulesList = new ArrayList<RuleCatalog>();

			if (allRulesList != null && !allRulesList.isEmpty()) {

				// Identify the new rules
				LOG.info("\n====> Identifying the new rules ..");
				if (catalogRulesList != null && !catalogRulesList.isEmpty()) {

					for (RuleCatalog val_check : allRulesList) {
						boolean matchFound = false;
						for (RuleCatalog rc_check : catalogRulesList) {
							boolean isRuleModified = false;

							if (val_check.getRuleType().equalsIgnoreCase(rc_check.getRuleType())
									&& val_check.getRuleCategory().equalsIgnoreCase(rc_check.getRuleCategory())
									&& ((val_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& val_check.getColumnName().equalsIgnoreCase(rc_check.getColumnName()))
											|| (val_check.getRuleCategory()
													.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
													&& val_check.getCustomOrGlobalRuleId() == rc_check
															.getCustomOrGlobalRuleId()))) {
								matchFound = true;

								// Check there are any changes in rule details
								if (val_check.getRuleCategory()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
										&& val_check.getThreshold() != rc_check.getThreshold()) {
									isRuleModified = true;

								} else if (val_check.getRuleType()
										.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)
										&& val_check.getThreshold() != rc_check.getThreshold()) {
									isRuleModified = true;
									LOG.debug("rule name=" + val_check.getRuleName());
								} else if (val_check.getRuleCategory()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
										&& isCustomRuleModified(rc_check, val_check)) {
									isRuleModified = true;
								}

								// Update modified rules in the rule catalog
								if (isRuleModified)
									ruleCatalogDao.updateModifiedRulesInRuleCatalog(idApp, rc_check, val_check);

								break;
							}

						}

						// If this rule is not found in rule catalog, it is a new rule it should be
						// added to rule catalog
						if (!matchFound) {
							newRulesList.add(val_check);
						}
					}

					// Identify the deleted rules
					LOG.info("\n====> Identifying the deleted rules ..");
					for (RuleCatalog rc_check : catalogRulesList) {
						boolean matchFound = false;

						for (RuleCatalog val_check : allRulesList) {

							if (val_check.getRuleType().equalsIgnoreCase(rc_check.getRuleType())
									&& val_check.getRuleCategory().equalsIgnoreCase(rc_check.getRuleCategory())
									&& ((val_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& val_check.getColumnName().equalsIgnoreCase(rc_check.getColumnName()))
											|| (val_check.getRuleCategory()
													.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
													&& val_check.getCustomOrGlobalRuleId() == rc_check
															.getCustomOrGlobalRuleId()))) {
								matchFound = true;
								break;
							}

						}

						// If this rule is not found all the validation rules list, it is a deleted rule
						// it should be removed from rule catalog
						if (!matchFound) {
							deletedRulesList.add(rc_check);
						}
					}

				} else {
					// If there no checks present in rule catalog, all the rules associated with the
					// validation must be inserted into rule catalog
					LOG.error(
							"\n====> No checks present in rule catalog, all the rules associated with the validation will be inserted into rule catalog ..");
					newRulesList = allRulesList;
				}

			} else {
				// If there no checks associated with the validation, all the rules in the rule
				// catalog must be deleted
				LOG.error(
						"\n====> No checks associated with the validation, all the rules in the rule catalog will be deleted ..");
				deletedRulesList = catalogRulesList;
			}

			// All the changes (new, delete, modified) goes to actual rule catalog table.

			if (deletedRulesList != null && !deletedRulesList.isEmpty()) {
				// Delete rules from the rule catalog
				LOG.info("\n====> Removing deleted rules from rule catalog ..");
				ruleCatalogDao.deletesRecordsFromRuleCatalog(deletedRulesList, idApp);
				
				// Delete the tag mapping of the deleted rules
				LOG.info("\n====> Removing tag mapping of deleted rules ..");
				ruleCatalogDao.deleteTagMappingsForDeletedRules(deletedRulesList, idApp);
				
			}

			if (newRulesList != null && !newRulesList.isEmpty()) {
				// Add new rules to the rule catalog
				LOG.info("\n====> Adding new rules to rule catalog ..");
				ruleCatalogDao.insertRuleCatalogRecords(newRulesList, idApp);
			}

		} catch (

		Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n*****====> updateActualRuleCatalog for validation [" + idApp + "]- END <====*****");
	}

	/*
	 * This method is to update the staging rule catalog of the validation.
	 */
	private void updateRuleCatalogStaging(long idApp) {

		LOG.debug("\n*****====> updateRuleCatalogStaging for validation [" + idApp + "]- START <====*****");

		try {
			// Get the rules present in catalog staging
			LOG.info("\n====> Get the rules present in catalog staging ..");
			List<RuleCatalog> statingRulesList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);

			// Get the validation details from staging table i.e., staging_listApplications
			LOG.info(
					"\n====> Get the validation details from staging table i.e., staginglistApplications table ..");
			ListApplications listApplication = ruleCatalogDao.getDataFromStagingListapplications(idApp);

			// If Validation data is missing in staging_ListApplications is missing, Copy
			// actual to Staging and read the data again
			if (listApplication == null) {
				ruleCatalogDao.copyLisApplicationsFromActualToStaging(idApp);
				listApplication = ruleCatalogDao.getDataFromStagingListapplications(idApp);
			}

			// Get all the checks associated
			LOG.info("\n====> Get all the rules associated with validation based on checks enabled ..");
			List<RuleCatalog> allRulesList = getAllRulesAssociatedWithValidation(idApp, listApplication, true);

			// Find the differences
			List<RuleCatalog> deletedRulesList = new ArrayList<RuleCatalog>();
			List<RuleCatalog> newRulesList = new ArrayList<RuleCatalog>();

			if (allRulesList != null && !allRulesList.isEmpty()) {

				if (statingRulesList != null && !statingRulesList.isEmpty()) {

					// Identify the New rules
					LOG.info("\n====> Identifying the new rules ..");

					for (RuleCatalog val_check : allRulesList) {
						boolean matchFound = false;

						for (RuleCatalog st_rc_check : statingRulesList) {
							boolean isRuleModified = false;

							if (val_check.getRuleType().equalsIgnoreCase(st_rc_check.getRuleType())
									&& val_check.getRuleCategory().equalsIgnoreCase(st_rc_check.getRuleCategory())
									&& ((val_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& val_check.getColumnName().equalsIgnoreCase(st_rc_check.getColumnName()))
											|| (val_check.getRuleCategory()
													.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
													&& val_check.getCustomOrGlobalRuleId() == st_rc_check
															.getCustomOrGlobalRuleId()))) {
								matchFound = true;

								// Check there are any changes in rule details
								if (val_check.getRuleCategory()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
										&& val_check.getThreshold() != st_rc_check.getThreshold()) {
									isRuleModified = true;

								} else if (val_check.getRuleType()
										.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)
										&& val_check.getThreshold() != st_rc_check.getThreshold()) {
									isRuleModified = true;

								} else if (val_check.getRuleCategory()
										.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
										&& isCustomRuleModified(st_rc_check, val_check)) {
									isRuleModified = true;
								}

								// Update modified rules in the rule catalog
								if (isRuleModified)
									ruleCatalogDao.updateModifiedRulesInRuleCatalogStaging(idApp, st_rc_check,
											val_check);

								break;
							}

						}

						// If this rule is not found in rule catalog staging, it is a new rule it should
						// be added to rule catalog staging
						if (!matchFound) {
							newRulesList.add(val_check);
						}
					}

					// Identify the deleted rules
					LOG.info("\n====> Identifying the deleted rules ..");

					for (RuleCatalog st_rc_check : statingRulesList) {
						boolean matchFound = false;

						for (RuleCatalog val_check : allRulesList) {

							if (val_check.getRuleType().equalsIgnoreCase(st_rc_check.getRuleType())
									&& val_check.getRuleCategory().equalsIgnoreCase(st_rc_check.getRuleCategory())
									&& ((val_check.getRuleCategory()
											.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)
											&& val_check.getColumnName().equalsIgnoreCase(st_rc_check.getColumnName()))
											|| (val_check.getRuleCategory()
													.equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_CUSTOM)
													&& val_check.getCustomOrGlobalRuleId() == st_rc_check
															.getCustomOrGlobalRuleId()))) {
								matchFound = true;
								break;
							}

						}

						// If this rule is not found all the validation rules list, it is a deleted rule
						// it should be removed from rule catalog staging
						if (!matchFound) {
							deletedRulesList.add(st_rc_check);
						}
					}

				} else {
					// If there are no rules present in rule catalog staging, all the rules
					// associated with the validation must be inserted into rule catalog staging
					LOG.debug(
							"\n====> No rules present in rule catalog staging, adding all the rules associated with validation to rule catalog staging ..");
					newRulesList = allRulesList;
				}

			} else {
				// If there no checks associated with the validation, all the rules in the rule
				// catalog staging must be deleted
				LOG.error(
						"\n====> No checks associated with the validation, deleting all the rules from rule catalog staging ..");
				deletedRulesList = statingRulesList;
			}

			// All the changes (new, delete, modified) goes to staging table.

			if (deletedRulesList != null && !deletedRulesList.isEmpty()) {
				// Delete rules from the rule catalog staging
				LOG.info("\n====> Removing the deleted rules from rule catalog staging ..");
				ruleCatalogDao.deletesRecordsFromRuleCatalogStaging(deletedRulesList, idApp);
				
				// Delete the tag mapping of the deleted rules
				LOG.info("\n====> Removing tag mapping of deleted rules ..");
				ruleCatalogDao.deleteTagMappingsForDeletedRules(deletedRulesList, idApp);
			}

			if (newRulesList != null && !newRulesList.isEmpty()) {
				// Add new rules to the rule catalog staging
				LOG.info("\n====> Adding the new rules to rule catalog staging ..");
				ruleCatalogDao.insertRuleCatalogRecordsToStaging(newRulesList, idApp);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n*****====> updateRuleCatalogStaging for validation [" + idApp + "]- END <====*****");
	}

	/*
	 * This method is to update the template changes in all the associated
	 * validations
	 */
	public void updateAssociatedRuleCatalogsOfTemplate(long idData) {
		LOG.info("\n*****====> updateAssociatedRuleCatalogsOfTemplate - START <====*****");

		try {
			// Get the list of active validations associated with template
			LOG.info("\n====> Get the list of active validations associated with template ..");
			List<ListApplicationsandListDataSources> validationList = validationcheckdao
					.getListApplicationsByIdData(idData);

			if (validationList != null) {
				for (ListApplicationsandListDataSources la : validationList) {

					// Updating Rule catalog of Validation
					long idApp = la.getIdApp();
					updateRuleCatalog(idApp);
				}
			}

			ruleCatalogDao.deleteUnusedDeactivatedGlobalRules();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("\n*****====> updateAssociatedRuleCatalogsOfTemplate - END <====*****");
	}

	/*
	 * This method is to update the custom rule changes i.e.,add, delete or modify
	 * in all the associated validations
	 */
	public void updateAssociatedRuleCatalogsForCustomRuleChange(long idListColrules, RuleActionTypes action) {
		LOG.info("\n*****====> updateAssociatedRuleCatalogsForCustomRuleChange - START <====*****");

		try {
			// Get the details of the custom rule
			LOG.debug("\n====> Get the details of the custom rule [" + idListColrules + "]..");
			ListColRules listColRules = extendTemplateRuleDAO.getListColRulesById(idListColrules);

			if (action != null && action == RuleActionTypes.DELETE) {
				// Deactivate the rule
				LOG.debug("\n====> Deactivating the custom rule [" + idListColrules + "] ..");
				extendTemplateRuleDAO.deactivateCustomRuleById(idListColrules);
			}

			if (listColRules != null) {
				// Get the template Id of the rule
				long idData = listColRules.getIdData();
				LOG.debug("\n====> Template Id of the Rule: " + idData);

				// Get the list of active validations associated with template
				LOG.debug(
						"\n====> Get the list of active validations associated with template [" + idData + "]");
				List<ListApplicationsandListDataSources> validationList = validationcheckdao
						.getListApplicationsByIdData(idData);

				if (validationList != null) {
					for (ListApplicationsandListDataSources la : validationList) {

						// Updating Rule catalog of Validation
						long idApp = la.getIdApp();
						updateRuleCatalog(idApp);
					}
				}

			}

			if (action != null && action == RuleActionTypes.DELETE) {
				// Check if the rule is still present in any approved rule catalog
				int count = ruleCatalogDao.getCountOfValidationsAssociatedWithCustomRule(idListColrules);

				LOG.debug("\n====> Number of approved rule catalogs using custom rule [" + idListColrules
						+ "]: " + count);

				// Delete the rule if it is not present
				if (count == 0) {
					LOG.info("\n====> Deleting the custom rule ..");
					templateViewDAO.deleteIdListColRulesData(idListColrules);
				} else {
					LOG.error("\n====> Custom rule is still being used cannot delete it ..");
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("\n*****====> updateAssociatedRuleCatalogsForCustomRuleChange - END <====*****");
	}

	public void updateRuleCatalogsForGlobalRuleThresholdChange(long idListColrules) {
		if (isRuleCatalogEnabled()) {
			LOG.info("\n====> Updating the template changes in the Associated validations RuleCatalogs");
			CompletableFuture.runAsync(() -> {
				List<Long> templateIdList = globalRuleDao.getTemplateIdsForGlobalRuleId(idListColrules);

				if (templateIdList != null) {
					for (long idData : templateIdList)
						updateAssociatedRuleCatalogsForGlobalRuleChange(idListColrules, idData,
								RuleActionTypes.MODIFIED);
				}

			});
		}
	}

	/*
	 * This method is to update the global rule changes i.e.,add, delete or modify
	 * in all the associated validations
	 */
	public void updateAssociatedRuleCatalogsForGlobalRuleChange(long globalRuleId, long templateId,
			RuleActionTypes action) {
		LOG.info("\n*****====> updateAssociatedRuleCatalogsForGlobalRuleChange - START <====*****");

		try {

			if (action != null && action == RuleActionTypes.DELETE) {
				// Deactivate the rule
				LOG.debug("\n====> Deactivating the Global rule [" + globalRuleId + "] for the template ["
						+ templateId + "]..");
				listDataSourceDao.deactivateGlobalRuleOfTemplate(templateId, globalRuleId);
			}

			LOG.debug("\n====> Template Id of the Rule: " + templateId);

			// Get the list of active validations associated with template
			LOG.debug(
					"\n====> Get the list of active validations associated with template [" + templateId + "]");
			List<ListApplicationsandListDataSources> validationList = validationcheckdao
					.getListApplicationsByIdData(templateId);

			if (validationList != null) {
				for (ListApplicationsandListDataSources la : validationList) {

					// Updating Rule catalog of Validation
					long idApp = la.getIdApp();
					updateRuleCatalog(idApp);
				}
			}

			if (action != null && action == RuleActionTypes.DELETE) {
				// Check if the rule is still present in any approved rule catalog
				int count = ruleCatalogDao.getCountOfValidationsAssociatedWithGlobalRule(templateId, globalRuleId);

				LOG.debug("\n====> Number of approved rule catalogs using Global rule [" + globalRuleId
						+ "] of Template [" + templateId + "]: " + count);

				// Delete the rule if it is not present
				if (count == 0) {
					LOG.info("\n====> Deleting the Template Global rule mapping..");
					listDataSourceDao.unlinkGlobalRuleFromDataTemplate(templateId, globalRuleId);
				} else {
					LOG.error("\n====> Global rule is still being used cannot delete it ..");
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("\n*****====> updateAssociatedRuleCatalogsForGlobalRuleChange - END <====*****");
	}

	/*
	 * This method is to update the changes in actual rules tables which were edited
	 * successfully from Rule catalog
	 */
	public boolean updateAssociatedRuleCatalogsForCatalogRuleEdit(RuleCatalog ruleCatalog) {
		LOG.info("\n*****====> updateAssociatedRuleCatalogsForCatalogRuleEdit - START <====*****");

		boolean updateStatus = false;
		try {

			long idApp = ruleCatalog.getIdApp();

			// Fetch the details of validation
			ListApplications listApplications = validationcheckdao.getdatafromlistapplications(idApp);

			if (listApplications != null) {

				// Get the templateId
				long templateId = listApplications.getIdData();

				/*
				 * If the validation is Approved, the changes will be saved to Staging rule
				 * catalog
				 */
				if (isValidationStagingActivated(idApp)) {

					LOG.info("\n====> Validation is Approved, saving the changes to Staging Rule Catalog ..");
					ruleCatalogDao.editRuleInRuleCatalogStaging(ruleCatalog);
				}
				/*
				 * If the validation is not Approved, the changes will be saved to actual rule
				 * catalog
				 */
				else {
					LOG.error(
							"\n====> Validation is not Approved, saving the changes to actual Rule Catalog ..");
					ruleCatalogDao.editRuleInRuleCatalog(ruleCatalog);
				}

				// Get the Template details
				long idData = listApplications.getIdData();
				ListDataSource listDataSource = iListDataSourceDAO.getDataFromListDataSourcesOfIdData(idData);

				// Get Template Create status
				String templateCreateStatus = listDataSource.getTemplateCreateSuccess();
				LOG.debug("\n====> Template Create Success: " + templateCreateStatus);

				// Get the approval status
				String deltaApprovalStatus = listDataSource.getDeltaApprovalStatus();
				LOG.debug("\n====> DeltaApprovalStatus: " + deltaApprovalStatus);

				// If the template is approved and staging does not exist then Create the
				// staging for template

				if (templateCreateStatus != null && templateCreateStatus.trim().equalsIgnoreCase("Y")
						&& (deltaApprovalStatus == null || deltaApprovalStatus.trim().isEmpty()
						|| deltaApprovalStatus.equalsIgnoreCase(TemplateDeltaStatusTypes.approved.toString())
						|| deltaApprovalStatus
						.equalsIgnoreCase(TemplateDeltaStatusTypes.rejected.toString()))) {

					// Clear the staging
					iListDataSourceDAO.clearListDataDefinitonStagingForIdData(idData);

					// Copy the listDataDefinition to staging
					iListDataSourceDAO.copyListDataDefintionsOfTemplateToStaging(idData);
				}

				/*
				 * For Auto-discovered Rules
				 */

				if (ruleCatalog.getRuleCategory().equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)) {
					boolean status = false;
					String columnName = ruleCatalog.getColumnName();
					String ruleType = ruleCatalog.getRuleType();
					double columnThreshold = ruleCatalog.getThreshold();
					String type = null;
					if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {
						if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS))
							type = "identity";
						else
							type = "all";

						boolean ruleCatlogStatus = isRuleCatalogEnabled();
						boolean validationStagingActivationStatus = isValidationStagingActivated(idApp);
						int thresholdUpdateStatus = 0;
						if (ruleCatlogStatus && validationStagingActivationStatus)
							thresholdUpdateStatus = ruleCatalogDao.updateStagingDuplicateCheckThresholdByType(idApp,
									type, columnThreshold);
						else
							thresholdUpdateStatus = ruleCatalogDao.updateDuplicateCheckThresholdByType(idApp, type,
									columnThreshold);
						if (thresholdUpdateStatus > 0)
							status = true;
						else
							LOG.error("\n====>Failed to update status");
					} else {
						RuleCatalogCheckSpecification ruleCatalogCheckSpecification = getCheckSpecificationByRuleType(
								ruleType);
						String checkName = ruleCatalogCheckSpecification.getTemplateCheckThresholdColumn();

						int thresholdUpdateStatus = 0;
						String updateValue = "" + columnThreshold;

						if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DATERULECHECK)) {
							status = true;
						}
						// Mamta 10-May-2022
						else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTCHECK)) {
							status = true;
						}
						// Mamta 30-May-2022
						else if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_TIMELINESSCHECK)) {
							status = true;
						} else {
							if (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)) {
								updateValue = ruleCatalog.getRuleExpression();
							}
							thresholdUpdateStatus = templateViewDAO.updateCheckValueIntoListDatadefinition(templateId,
									checkName, columnName, updateValue);
						}
						if (thresholdUpdateStatus > 0) {

							thresholdUpdateStatus = templateViewDAO.updateCheckValueIntoStagingListDatadefinition(
									templateId, checkName, columnName, updateValue);

							if (thresholdUpdateStatus > 0)
								status = true;
							else
								LOG.error("\n====>Failed to update status into staging_listDataDefinition");
						} else
							LOG.error("\n====>Failed to update status into listDataDefinition");

						if(ruleType.trim().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY) || ruleType.trim().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY )) {
							JSONObject rcaThresholstatus = dataTemplateDeltaCheckService.updateRecordCountAnomalyThreshold(idApp, ruleType, columnThreshold);
							if (rcaThresholstatus != null && rcaThresholstatus.getString("status").trim().equalsIgnoreCase("success")) {
								status = true;
							} else
								LOG.error("\n====>Failed to update RCAthreshold into rule catalog");
						}


					}
					if (status) {
						// Update rule catalog of current validation
						updateRuleCatalog(idApp);

						// Update Associated Rule catalogs
						CompletableFuture.runAsync(() -> {
							updateAssociatedRuleCatalogsOfTemplate(templateId);
						});
						updateStatus = true;
					}

				}

				/*
				 * For Custom rule
				 */
				else if (ruleCatalog.getRuleType().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {

					// Update Anchor Column, Rule expression and Matching rules changes in custom
					// rule table listColRules
					boolean status = ruleCatalogDao.updateRuleDetailsOfCustomRuleFromRuleCatalog(ruleCatalog);

					if (status) {
						updateStatus = true;

						// Update rule catalog of current validation
						updateRuleCatalog(idApp);

						// Update Associated Rule catalogs
						CompletableFuture.runAsync(() -> {
							updateAssociatedRuleCatalogsForCustomRuleChange(ruleCatalog.getCustomOrGlobalRuleId(),
									RuleActionTypes.MODIFIED);
						});
					}

				}

				/*
				 * For Global rule
				 */
				else if (ruleCatalog.getRuleType().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)) {

					// Update Anchor Column, Rule expression and Matching rules changes in Global
					// rule table rule_Template_mapping
					boolean status = ruleCatalogDao.updateRuleDetailsOfGlobalRuleFromRuleCatalog(templateId,
							ruleCatalog);

					if (status) {
						updateStatus = true;
						boolean isThresholdChange = false;

						long idListColRule = ruleCatalog.getCustomOrGlobalRuleId();

						double threshold = globalRuleDao.getGlobalRulesThresholdByIdListColRules(idListColRule);

						if (ruleCatalog.getThreshold() != threshold) {
							isThresholdChange = true;
							globalRuleDao.updateListColGlobalRuleThreshold(ruleCatalog.getThreshold(), idListColRule);
						}

						// Update dimensionId
						long dimensionId = ruleCatalog.getDimensionId();
						if (dimensionId > 0l) {
							globalRuleDao.updateListColGlobalRuleDimension(dimensionId, idListColRule);
						}

						// Update rule catalog of current validation
						updateRuleCatalog(idApp);

						if (isThresholdChange) {
							updateRuleCatalogsForGlobalRuleThresholdChange(idListColRule);
						} else
							CompletableFuture.runAsync(() -> {
								updateAssociatedRuleCatalogsForGlobalRuleChange(ruleCatalog.getCustomOrGlobalRuleId(),
										templateId, RuleActionTypes.MODIFIED);
							});

					}
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("\n*****====> updateAssociatedRuleCatalogsForCatalogRuleEdit - END <====*****");

		return updateStatus;
	}

	public boolean deleteRuleFromRuleCatalog(long idApp, RuleCatalog ruleCatalog) {
		LOG.info("\n*****====> deleteRuleFromRuleCatalog - START <====*****");

		boolean deleteStatus = false;

		try {

			// Fetch the details of validation
			ListApplications listApplications = validationcheckdao.getdatafromlistapplications(ruleCatalog.getIdApp());

			if (listApplications != null) {

				// Get the templateId
				long templateId = listApplications.getIdData();
				String columnName = ruleCatalog.getColumnName();
				String ruleType = ruleCatalog.getRuleType();
				long customOrGlobalRuleId = ruleCatalog.getCustomOrGlobalRuleId();

				/*
				 * For Auto discovered template rules
				 */
				if (ruleCatalog.getRuleCategory().equalsIgnoreCase(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY)) {

					List<RuleCatalogCheckSpecification> checks_specs_List = getRuleCatalogCheckSpecifications();

					for (RuleCatalogCheckSpecification rc_check : checks_specs_List) {
						if (rc_check.getCheckDisplayName().toLowerCase().equalsIgnoreCase(ruleType.toLowerCase())) {

							String check_columnName = rc_check.getTemplateCheckEnabledColumn();

							boolean stg_ldd_status = ruleCatalogDao.disableColumnCheckFromStagingListDataDefinition(
									templateId, columnName, check_columnName, "N");

							if (stg_ldd_status) {
								boolean ac_ldd_status = ruleCatalogDao.disableColumnCheckFromListDataDefinition(
										templateId, columnName, check_columnName, "N");

								if (ac_ldd_status) {
									deleteStatus = true;

									// Update rule catalog of current validation
									updateRuleCatalog(idApp);

									// Update Associated Rule catalogs
									CompletableFuture.runAsync(() -> {
										updateAssociatedRuleCatalogsOfTemplate(templateId);
									});
								}
							}

							break;
						}
					}

				}

				/*
				 * For Custom rule
				 */
				else if (ruleCatalog.getRuleType().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE)) {

					deleteStatus = true;

					// Deactivate the rule
					LOG.debug("\n====> Deactivating the custom rule [" + customOrGlobalRuleId + "] ..");
					extendTemplateRuleDAO.deactivateCustomRuleById(customOrGlobalRuleId);

					// Update rule catalog of current validation
					updateRuleCatalog(idApp);

					// Update Associated Rule catalogs
					CompletableFuture.runAsync(() -> {
						updateAssociatedRuleCatalogsForCustomRuleChange(ruleCatalog.getCustomOrGlobalRuleId(),
								RuleActionTypes.DELETE);
					});
				}

				/*
				 * For Global rule
				 */
				else if (ruleCatalog.getRuleType().equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)) {

					deleteStatus = true;

					// Deactivate the Global rule
					LOG.debug("\n====> Deactivating the Global rule [" + customOrGlobalRuleId + "] ..");
					listDataSourceDao.deactivateGlobalRuleOfTemplate(templateId, customOrGlobalRuleId);

					// Update rule catalog of current validation
					updateRuleCatalog(idApp);

					// Update Associated Rule catalogs
					CompletableFuture.runAsync(() -> {
						updateAssociatedRuleCatalogsForGlobalRuleChange(ruleCatalog.getCustomOrGlobalRuleId(),
								templateId, RuleActionTypes.DELETE);
					});

				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.info("\n*****====> deleteRuleFromRuleCatalog - END <====*****");

		return deleteStatus;
	}

	/*
	 * This method is to get the all the Rules associated with the Validation based
	 * on the checks enabled during the validation creation
	 */
	private List<RuleCatalog> getAllRulesAssociatedWithValidation(long nIdApp, ListApplications oListApplication,
			boolean isValidationStagingEnabled) {

		LOG.debug("\n*****====> Get all rules associated With validation [" + nIdApp + "] - START <====*****");

		List<RuleCatalog> aRetDataRows = new ArrayList<RuleCatalog>();
		try {
			List<RuleCatalogCheckSpecification> aCheckSpecifications = getRuleCatalogCheckSpecifications();
			Map<String, GetChecksData> aGetChecksDataMethods = getCheckDataMethods();

			long idApp = oListApplication.getIdApp();
			boolean lApplyDerivedColumns = false;
			boolean lApplyRules = false;
			boolean rcaCheckEnabled = false;
			boolean microsegRCACheckEnabled = false;
			Field oField = null;

			lApplyRules = (oListApplication.getApplyRules() == null
					|| oListApplication.getApplyDerivedColumns().isEmpty()
					|| oListApplication.getApplyRules().equalsIgnoreCase("N")) ? false : true;

			lApplyDerivedColumns = (oListApplication.getApplyDerivedColumns() == null
					|| oListApplication.getApplyDerivedColumns().isEmpty()
					|| oListApplication.getApplyDerivedColumns().equalsIgnoreCase("N")) ? false : true;

			lApplyRules = (lApplyRules || lApplyDerivedColumns) ? true : false; // so this is used as effective apply

			rcaCheckEnabled = (oListApplication.getRecordCountAnomaly() != null
					&& oListApplication.getRecordCountAnomaly().trim().equalsIgnoreCase("Y")) ? true : false;

			microsegRCACheckEnabled = (oListApplication.getKeyGroupRecordCountAnomaly() != null
					&& oListApplication.getKeyGroupRecordCountAnomaly().trim().equalsIgnoreCase("Y")) ? true : false;

			// When both Null check and Microsegment Null check both are enabled since both
			// of them refer to same columns only once the checks have to be picked
			boolean isNullCheckRulesFetched = false;
			boolean isDriftCheckRulesFetched = false;

			for (RuleCatalogCheckSpecification oCheckSpec : aCheckSpecifications) {
				List<RuleCatalog> aCheckDataRows = new ArrayList<RuleCatalog>();

				boolean lInvokeCheckMethod = false;

				String sCheckType = oCheckSpec.getCheckType();
				String sCheckName = oCheckSpec.getCheckName();
				String sCheckEntityName = oCheckSpec.getEntityName();
				String sFieldValue = "";

				/*
				 * When both Null check and Microsegment Null check are enabled, only one for
				 * them the rules must be loaded as they both of them refer to same columns
				 */
				if ((sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.MicrosegmentNullCheck.name())
						|| sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.NullCheck.name()))
						&& isNullCheckRulesFetched) {
					continue;
				}

				/*
				 * When both Drfit check and Microsegment Drift check are enabled, only one for
				 * them the rules must be loaded as they both of them refer to same columns
				 */
				if ((sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.MicrosegmentDataDriftCheck.name())
						|| sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.DataDriftCheck.name()))
						&& isDriftCheckRulesFetched) {
					continue;
				}

				// For Column level checks
				if (sCheckType.equalsIgnoreCase(DatabuckConstants.RC_CHECK_TYPE_COLUMN)) {

					// All the checks whose data is part of listApplications entity
					if (sCheckEntityName.equalsIgnoreCase(DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS)) {

						// For RCA
						if (sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.RecordCountAnomaly.name())) {
							sFieldValue = (rcaCheckEnabled || microsegRCACheckEnabled) ? "Y" : "N";
						}
						// For Microsegment RCA
						else if (sCheckName
								.equalsIgnoreCase(RuleCatalogCheckTypes.MicrosegementRecordCountAnomaly.name())) {
							sFieldValue = (microsegRCACheckEnabled) ? "Y" : "N";
						} else {
							oField = oListApplication.getClass().getDeclaredField(oCheckSpec.getCheckColumn());
							oField.setAccessible(true);

							sFieldValue = (String) oField.get(oListApplication);
							sFieldValue = ((sFieldValue == null) || sFieldValue.isEmpty()) ? "N"
									: sFieldValue.trim().toUpperCase();
						}
					}

					// This is for Duplicate check whose data is part of another entity
					else if (sCheckEntityName.equalsIgnoreCase(DatabuckConstants.RC_ENTITY_LISTDFTRANRULE)) {

						// Duplicate Check - Primary Columns
						if (sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name())) {
							if (isValidationStagingEnabled)
								sFieldValue = ruleCatalogDao.isStagingDuplicateCheckIdentityEnabled(idApp);
							else
								sFieldValue = ruleCatalogDao.isDuplicateCheckIdentityEnabled(idApp);
						}
						// Duplicate Check - Selected Columns
						else if (sCheckName
								.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckSelectedFields.name())) {
							if (isValidationStagingEnabled)
								sFieldValue = ruleCatalogDao.isStagingDuplicateCheckAllEnabled(idApp);
							else
								sFieldValue = ruleCatalogDao.isDuplicateCheckAllEnabled(idApp);
						}
					}

				}

				// For custom and global rules
				else if (sCheckType.equalsIgnoreCase(DatabuckConstants.RC_CHECK_TYPE_RULES)) {
					sFieldValue = (lApplyRules) ? "Y" : "N";
				}

				// if the check is enabled, fetch the check details
				lInvokeCheckMethod = sFieldValue.equalsIgnoreCase("Y") ? true : false;

				// Set the flag to true when either Null check and Microsegment Null check is
				// processed
				if ((sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.MicrosegmentNullCheck.name())
						|| sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.NullCheck.name())) && lInvokeCheckMethod) {
					isNullCheckRulesFetched = true;
				}

				// Set the flag to true when either Drift check and Microsegment drift check is
				// processed
				if ((sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.MicrosegmentDataDriftCheck.name())
						|| sCheckName.equalsIgnoreCase(RuleCatalogCheckTypes.DataDriftCheck.name())) && lInvokeCheckMethod) {
					isDriftCheckRulesFetched = true;
				}

				if (lInvokeCheckMethod && aGetChecksDataMethods.containsKey(sCheckName)) {
					aCheckDataRows = aGetChecksDataMethods.get(sCheckName).getChecksDataForGivenCheck(oCheckSpec,
							oListApplication);
					LOG.debug(
							"\n====>Rule Type:[" + sCheckName + "] and Rule count: [" + aCheckDataRows.size() + "]");
					if (aCheckDataRows.size() > 0) {
						if (sCheckEntityName.equalsIgnoreCase(DatabuckConstants.RC_ENTITY_LISTDFTRANRULE)) {
							List<ListDfTranRule> listDfTranRule = null;
							if (isValidationStagingEnabled)
								listDfTranRule = ruleCatalogDao.getDataFromStagingListDfTranRuleForThreshold(idApp);
							else
								listDfTranRule = jsonDaoI.getDataFromListDfTranRule(idApp);
							for (ListDfTranRule dups : listDfTranRule) {
								for (RuleCatalog rules : aCheckDataRows) {
									if (sCheckName
											.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name())
											&& dups.getType().equalsIgnoreCase("identity")
											&& dups.getDupRow().equalsIgnoreCase("Y")) {
										rules.setThreshold(dups.getThreshold());

									} else if (sCheckName
											.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckSelectedFields.name())
											&& dups.getType().equalsIgnoreCase("all")
											&& dups.getDupRow().equalsIgnoreCase("Y")) {
										rules.setThreshold(dups.getThreshold());
									}
								}
							}
						}
						aRetDataRows.addAll(aCheckDataRows);
					}
				}
			}

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
		}

		LOG.debug("\n*****====> Get all rules associated With validation [" + nIdApp + "] - END <====*****");

		return aRetDataRows;
	}

	/*
	 * This method is to get the Rule specifications of each type check
	 */
	public List<RuleCatalogCheckSpecification> getRuleCatalogCheckSpecifications() {
		List<RuleCatalogCheckSpecification> checks_specs_List = new ArrayList<RuleCatalogCheckSpecification>();

		// Get the list of Dimensions name and Id
		Map<String, Long> defaultDimensions = ruleCatalogDao.getDimensionsList();

		/*
		 * Rule Specfications:
		 * 
		 * 1.checkType -- Indicates whether Column level check or Rules. Value is
		 * "Column" or "Rules".
		 * 
		 * 2.checkName -- Indicates the name of the check Eg: BadDataCheck, NullCheck
		 * etc
		 * 
		 * 3.checkDisplayName -- Indicates the display name of the check and the same is
		 * saved in rule catalog in RuleType column
		 * 
		 * 4.entityName -- Indicates the entity i.e., table name to which the check
		 * enabled column belongs to. Eg: listApplications table "badData" column value
		 * 'Y' indicates bad data check is enabled.
		 * 
		 * 5.checkColumn -- Indicates the column name in the table or entity whose value
		 * indicates if the check is enabled.
		 * 
		 * 6.templateCheckEnabledColumn -- For column level checks listDataDefinition
		 * table will be referred to see which checks on each column. Eg: All the fields
		 * which have "nonNull" column value 'Y' in listDataDefinition table are
		 * considered enabled for Null check.
		 *
		 * 7.templateCheckThresholdColumn -- For column level checks each check has a
		 * threshold column in listDataDefinition table.
		 * 
		 * 8.Default DimensionId for the check
		 */

		// BadDataCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.BadDataCheck.name(), DatabuckConstants.RULE_TYPE_BADDATACHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "badData", "badData", "badDataCheckThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Invalid data type < Threshold"));

		// Duplicate check - Identity specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name(),
				DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS, DatabuckConstants.RC_ENTITY_LISTDFTRANRULE,
				"dupRow", "primaryKey", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_UNIQUENESS.toLowerCase(), 0l),
				"Composite key dups < Threshold"));

		// Duplicate check - All specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DuplicateCheckSelectedFields.name(),
				DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS, DatabuckConstants.RC_ENTITY_LISTDFTRANRULE,
				"dupRow", "dupkey", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_UNIQUENESS.toLowerCase(), 0l),
				"Custom column dups < Threshold"));

		// NullCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.NullCheck.name(), DatabuckConstants.RULE_TYPE_NULLCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "nonNullCheck", "nonNull", "nullCountThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_COMPLETENESS.toLowerCase(), 0l),
				"Null or blank < Threshold"));

		// MicrosegmentNullCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.MicrosegmentNullCheck.name(), DatabuckConstants.RULE_TYPE_NULLCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "dGroupNullCheck", "nonNull", "nullCountThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_COMPLETENESS.toLowerCase(), 0l),
				"Null or blank < Threshold"));

		// PatternCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.PatternCheck.name(), DatabuckConstants.RULE_TYPE_PATTERNCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "patternCheck", "patternCheck", "patternCheckThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_ACCURACY.toLowerCase(), 0l),
				"Invalid RegEx patterns < Threshold"));

		// Default PatternCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DefaultPatternCheck.name(), DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "defaultPatternCheck", "defaultPatternCheck",
				"defaultPatterns",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_ACCURACY.toLowerCase(), 0l),
				"Invalid patterns < Threshold"));

		// DefaultCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DefaultCheck.name(), DatabuckConstants.RULE_TYPE_DEFAULTCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "defaultCheck", "defaultCheck", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Invalid default values < Threshold"));

		// LengthCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.LengthCheck.name(), DatabuckConstants.RULE_TYPE_LENGTHCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "lengthCheck", "lengthCheck", "lengthCheckThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_COMPLETENESS.toLowerCase(), 0l),
				"Elements w/Invalid length < Threshold"));

		// Max LengthCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.MaxLengthCheck.name(), DatabuckConstants.RULE_TYPE_MAXLENGTHCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "maxLengthCheck", "maxLengthCheck",
				"lengthCheckThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_COMPLETENESS.toLowerCase(), 0l),
				"Elements w/Invalid Max length < Threshold"));

		// DateRuleCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DateRuleCheck.name(), DatabuckConstants.RULE_TYPE_DATERULECHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "dateRuleChk", "dateRule", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Out of range date values < Threshold"));

		// MicrosegmentDateRuleCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.MicrosegmentDateRuleCheck.name(), DatabuckConstants.RULE_TYPE_DATERULECHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "dGroupDateRuleCheck", "dateRule", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Out of range date values < Threshold"));

		// DataDriftCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.DataDriftCheck.name(), DatabuckConstants.RULE_TYPE_DATADRIFTCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "dataDriftCheck", "dataDrift", "dataDriftThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Values outside predermined list"));

		// MicrosegmentDataDriftCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.MicrosegmentDataDriftCheck.name(), DatabuckConstants.RULE_TYPE_DATADRIFTCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "dGroupDataDriftCheck", "dataDrift", "dataDriftThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Values outside predermined list"));

		// NumericalStatisticsCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.NumericalStatisticsCheck.name(),
				DatabuckConstants.RULE_TYPE_NUMERICALSTATISTICSCHECK, DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS,
				"numericalStatCheck", "numericalStat", "numericalThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_CONSISTENCY.toLowerCase(), 0l),
				"Entire microsegment behavior deviates from history < Threshold"));

		// RecordAnomalyCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.ValueAnomalyCheck.name(), DatabuckConstants.RULE_TYPE_VALUEANOMALYCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "recordAnomalyCheck", "recordAnomaly",
				"recordAnomalyThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_CONSISTENCY.toLowerCase(), 0l),
				"Column values inconsistent with historical behavior < Threshold"));

		// TimelinessCheck specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.TimelinessCheck.name(), DatabuckConstants.RULE_TYPE_TIMELINESSCHECK,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "timelinessKeyChk", "timelinessKey", "",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Date gaps and overlaps not allowed"));

		// Rules(Custom and Global) specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_RULES,
				RuleCatalogCheckTypes.Rules.name(), DatabuckConstants.RULE_TYPE_RULES,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "NA", "NA", "NA",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l), ""));

		// Record Count Anomaly Specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.RecordCountAnomaly.name(), DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "recordCountAnomaly", "NA", "recordCountAnomalyThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Entire Dataset record count deviates from history < Threshold"));

		// Microsegment Record Count Anomaly Specifications
		checks_specs_List.add(new RuleCatalogCheckSpecification(DatabuckConstants.RC_CHECK_TYPE_COLUMN,
				RuleCatalogCheckTypes.MicrosegementRecordCountAnomaly.name(),
				DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY,
				DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS, "keyGroupRecordCountAnomaly", "NA",
				"recordCountAnomalyThreshold",
				defaultDimensions.getOrDefault(DatabuckConstants.DIMENSION_VALIDITY.toLowerCase(), 0l),
				"Entire microsegment record count deviates from history < Threshold"));

		return checks_specs_List;
	}

	/*
	 * This method is used to get Rule Expression For Column Level Checks
	 */
	private Map<String, String> getRuleExpressionForColumnLevelChecks() {
		Map<String, String> colChecks_ruleExpMap = new HashMap<>();
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.BadDataCheck.name(), "Validate Format for <column>");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name(), "<column> must be unique");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DuplicateCheckSelectedFields.name(), "<column> must be unique");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.NullCheck.name(), "<column> can not be null or blank");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.MicrosegmentNullCheck.name(),
				"<column> can not be null or blank");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.PatternCheck.name(), "<column> must have the valid pattern");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DefaultPatternCheck.name(),
				"<column> must have the valid pattern");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DefaultCheck.name(),
				"<column> must have the valid default values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.LengthCheck.name(), "<column> must have the fixed length");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.MaxLengthCheck.name(),
				"<column> must have the fixed max length");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DateRuleCheck.name(),
				"<column> must have the reasonable date values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.MicrosegmentDateRuleCheck.name(),
				"<column> must have the reasonable date values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.DataDriftCheck.name(),
				"<column> must have the specific list of values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.MicrosegmentDataDriftCheck.name(),
				"<column> must have the specific list of values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.NumericalStatisticsCheck.name(),
				"<column> must be consistent with historical values");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.ValueAnomalyCheck.name(), "<column> should not have outlier");
		colChecks_ruleExpMap.put(RuleCatalogCheckTypes.TimelinessCheck.name(),
				"<column> must be consistent with the time parameter");
		return colChecks_ruleExpMap;
	}

	/*
	 * This method is used to get the GetChecksData object for each check in a map
	 * to read the specifications details and rules details of the check
	 */
	private Map<String, GetChecksData> getCheckDataMethods() {

		Map<String, GetChecksData> aGetChecksDataMethods = new HashMap<String, GetChecksData>();

		List<RuleCatalogCheckTypes> checksList = Arrays.asList(RuleCatalogCheckTypes.values());

		for (RuleCatalogCheckTypes ruleCatalogCheckTypes : checksList) {

			if (ruleCatalogCheckTypes == RuleCatalogCheckTypes.Rules) {
				aGetChecksDataMethods.put(ruleCatalogCheckTypes.name(), (oCheckSpec, oListApplication) -> {
					return ruleCatalogDao.getChecksDataForRulesCheck(oCheckSpec, oListApplication);
				});
			} else if (ruleCatalogCheckTypes == RuleCatalogCheckTypes.RecordCountAnomaly) {

				aGetChecksDataMethods.put(ruleCatalogCheckTypes.name(), (oCheckSpec, oListApplication) -> {

					// Prepare rule name
					String ruleName = DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY.replace(" ", "") + "_"
							+ DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY.replace(" ", "");

					RuleCatalog rc_checkDetails = new RuleCatalog();
					rc_checkDetails.setRowId(0l);
					rc_checkDetails.setIdApp(oListApplication.getIdApp());
					rc_checkDetails.setRuleReference(0l);
					rc_checkDetails.setRuleType(DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY);
					rc_checkDetails.setColumnName("");
					rc_checkDetails.setRuleName(ruleName);
					rc_checkDetails.setRuleCategory(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY);
					rc_checkDetails.setRuleExpression("");
					rc_checkDetails.setRuleCode("");
					rc_checkDetails.setDefectCode("");
					rc_checkDetails.setThreshold(oListApplication.getRecordCountAnomalyThreshold());
					rc_checkDetails.setDimensionId(oCheckSpec.getDimensionId());
					rc_checkDetails.setReviewBy("");
					rc_checkDetails.setReviewDate(null);
					rc_checkDetails.setReviewComments("");
					rc_checkDetails.setRuleDescription(oCheckSpec.getCheckDescription());
					List<RuleCatalog> ruleCatalogList = new ArrayList<>();
					ruleCatalogList.add(rc_checkDetails);
					return ruleCatalogList;
				});

			} else if (ruleCatalogCheckTypes == RuleCatalogCheckTypes.MicrosegementRecordCountAnomaly) {

				aGetChecksDataMethods.put(ruleCatalogCheckTypes.name(), (oCheckSpec, oListApplication) -> {

					// Prepare rule name for Microsegment Record Count Anomaly
					String dGroupRuleName = DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY.replace(" ", "") + "_"
							+ DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY.replace(" ", "");

					RuleCatalog dGroup_rc_checkDetails = new RuleCatalog();
					dGroup_rc_checkDetails.setRowId(0l);
					dGroup_rc_checkDetails.setIdApp(oListApplication.getIdApp());
					dGroup_rc_checkDetails.setRuleReference(0l);
					dGroup_rc_checkDetails.setRuleType(DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY);
					dGroup_rc_checkDetails.setColumnName("");
					dGroup_rc_checkDetails.setRuleName(dGroupRuleName);
					dGroup_rc_checkDetails.setRuleCategory(DatabuckConstants.RULE_CATEGORY_AUTO_DISCOVERY);
					dGroup_rc_checkDetails.setRuleExpression("");
					dGroup_rc_checkDetails.setRuleCode("");
					dGroup_rc_checkDetails.setDefectCode("");
					dGroup_rc_checkDetails.setThreshold(oListApplication.getRecordCountAnomalyThreshold());
					dGroup_rc_checkDetails.setDimensionId(oCheckSpec.getDimensionId());
					dGroup_rc_checkDetails.setReviewBy("");
					dGroup_rc_checkDetails.setReviewDate(null);
					dGroup_rc_checkDetails.setReviewComments("");
					dGroup_rc_checkDetails.setRuleDescription(oCheckSpec.getCheckDescription());

					List<RuleCatalog> ruleCatalogList = new ArrayList<>();
					ruleCatalogList.add(dGroup_rc_checkDetails);
					return ruleCatalogList;
				});
			} else {
				aGetChecksDataMethods.put(ruleCatalogCheckTypes.name(), (oCheckSpec, oListApplication) -> {
					return ruleCatalogDao.getChecksDataForColumnCheck(oCheckSpec, oListApplication);
				});

			}
		}

		return aGetChecksDataMethods;
	}

	/*
	 * Get results handler to populate results for each row of rule catalog for
	 * given type of check
	 */
	public HashMap<String, GetResultstData> getResultsDataMethods() {
		HashMap<String, GetResultstData> aGetResultsDataMethods = new HashMap<String, GetResultstData>() {

			private static final long serialVersionUID = 1L;
			{
				put(RuleCatalogCheckTypes.BadDataCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForBadDataCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDuplicateCheckPrimaryFields(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DuplicateCheckSelectedFields.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDuplicateCheckSelectedFields(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DataDriftCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDataDriftCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DateRuleCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDateRuleCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DefaultCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDefaultCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.DefaultPatternCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForDefaultPatternCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.LengthCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForLengthCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.MaxLengthCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForMaxLengthCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.NullCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForNullCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.NumericalStatisticsCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForNumericalStatisticsCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.PatternCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForPatternCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.ValueAnomalyCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForRecordAnomalyCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.TimelinessCheck.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForTimelinessCheck(oRuleCatalogRow);
				});
				put(RuleCatalogCheckTypes.Rules.name(), (oRuleCatalogRow) -> {
					return viewRuleDao.getResultsForRules(oRuleCatalogRow);
				});
			}
		};

		return aGetResultsDataMethods;
	}

	/*
	 * This method is used to check the Results of Rule Catalog rules of a
	 * validation for a specific Date and Run
	 */
	public JSONArray getRuleCatalogResultData(ListApplications listApplications, long idApp, String executionDate,
			long run, long totalRecords, boolean isTestRun) {
		JSONArray apiResult = new JSONArray();

		// Fetch project and domain details
		long idData = listApplications.getIdData();
		long projectId = listApplications.getProjectId();
		long domainId = iResultsDAO.getDomainIdByIdData(idData);

		String projectName = iProjectDAO.getProjectNameByProjectid(projectId);
		String domainName = iTaskDAO.getDomainNameById(domainId);
		String validationName = listApplications.getName();

		// Get Validation approval status
		int val_approve_status_code = listApplications.getApproveStatus();
		String validationApprovalStatus = validationcheckdao.getApproveStatusById(val_approve_status_code);

		int val_staging_approve_status_code = listApplications.getStagingApproveStatus();
		String validationStagingStatus = (val_staging_approve_status_code > 0)
				? validationcheckdao.getApproveStatusById(val_staging_approve_status_code)
				: "";

		// Get validation status
		String validationStatus = "Inactive";
		if (listApplications.getActive() != null && listApplications.getActive().trim().equalsIgnoreCase("yes"))
			validationStatus = "Active";

		// Fetch table details
		listDataAccess lda = iListDataSourceDAO.getListDataAccess(idData);
		String folderName = lda.getFolderName();

		// Fetch connection details
		long idDataSchema = lda.getIdDataSchema();
		String connectionName = "";
		String databaseSchemaName = "";
		if (idDataSchema > 0l) {
			List<ListDataSchema> schemaList = iListDataSourceDAO.getListDataSchemaId(idDataSchema);

			if (schemaList != null && schemaList.size() > 0) {
				ListDataSchema listDataSchema = schemaList.get(0);
				// Get the database schema name of the connection
				connectionName = listDataSchema.getSchemaName();
				databaseSchemaName = listDataSchema.getDatabaseSchema();
			}
		}
		HashMap<String, GetResultstData> getResultsDataMethods = getResultsDataMethods();

		// Check if the validation staging is enabled
		boolean validationStatingEnabled = isValidationStagingActivated(idApp);

		List<RuleCatalog> catalogRuleList = null;
		if (isTestRun && validationStatingEnabled)
			catalogRuleList = ruleCatalogDao.getRulesFromRuleCatalogStaging(idApp);
		else
			catalogRuleList = ruleCatalogDao.getRulesFromRuleCatalog(idApp);

		if (catalogRuleList != null && !catalogRuleList.isEmpty()) {

			// Duplicate check - Primary columns , the results of all these columns should
			// be fetched only once, since we cannot get them separately
			boolean isDupcheckPrmyColsResultsFetched = false;
			List<String> dupcheckPrmyColsList = new ArrayList<String>();
			Map<String, Object> dupcheckPrmyColsResultsData = null;

			// Get the RuleExpressions for Column level checks
			Map<String, String> colChecks_ruleExpMap = getRuleExpressionForColumnLevelChecks();
			

			for (RuleCatalog ruleCatalog : catalogRuleList) {

				try {
					Map<String, Object> ruleBaseData = new HashMap<String, Object>();

					String ruleName = ruleCatalog.getRuleName();
					String ruleCategory = ruleCatalog.getRuleCategory();
					String ruleType = ruleCatalog.getRuleType();
					String columnName = ruleCatalog.getColumnName();

					// Get rule description
					String ruleDescription = ruleCatalog.getRuleDescription();

					// Check the check type
					String sCheckType = "";
					if (ruleType != null && (ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)
							|| ruleType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE))) {
						sCheckType = DatabuckConstants.RULE_TYPE_RULES;
					} else {
						sCheckType = ruleType.replaceAll(" ", "");
					}

					System.out.println("sCheckType"+sCheckType);
					// Get rule Expression
					String ruleExp = "";
					if (sCheckType.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_RULES)) {
						ruleExp = ruleCatalog.getRuleExpression();
					} else {
						ruleExp = colChecks_ruleExpMap.get(sCheckType);
						ruleExp = ruleExp.replace("<column>", columnName);
					}

					// Don't fetch results for Duplicate check Primary fields if already done, there
					// will be only one result for all the columns of this check
					if (sCheckType.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name())) {

						// Add the column name to list
						dupcheckPrmyColsList.add(columnName);

						if (isDupcheckPrmyColsResultsFetched)
							continue;
					}

					ruleBaseData.put("idApp", idApp);
					ruleBaseData.put("resultRunDate", executionDate + " 00:00:00");
					ruleBaseData.put("resultRunNo", run);
					ruleBaseData.put("isTestRun", isTestRun);
					ruleBaseData.put("application", projectName);
					ruleBaseData.put("connectionName", connectionName);
					ruleBaseData.put("databaseSchemaName", databaseSchemaName);
					ruleBaseData.put("validationName", validationName);
					ruleBaseData.put("validationStatus", validationStatus);
					ruleBaseData.put("validationApprovalStatus", validationApprovalStatus);
					ruleBaseData.put("validationStagingStatus", validationStagingStatus);
					ruleBaseData.put("ruleStagingStatus", "NOCHANGE");
					ruleBaseData.put("domain", domainName);
					ruleBaseData.put("datasetName", folderName);
					ruleBaseData.put("ruleNo", ruleCatalog.getRowId());
					ruleBaseData.put("ruleReference", ruleCatalog.getRuleReference());
					ruleBaseData.put("ruleName", ruleName);
					ruleBaseData.put("ruleDescription", ruleDescription);
					ruleBaseData.put("ruleType", ruleType);
					ruleBaseData.put("columnName", columnName);
					ruleBaseData.put("ruleCategory", ruleCategory);
					ruleBaseData.put("ruleExpression", ruleExp);
					ruleBaseData.put("ruleCode", ruleCatalog.getRuleCode());
					ruleBaseData.put("defectCode", ruleCatalog.getDefectCode());
					ruleBaseData.put("threshold", ruleCatalog.getThreshold());
					ruleBaseData.put("comments", ruleCatalog.getReviewComments());
					ruleBaseData.put("reviewedDate", ruleCatalog.getReviewDate());
					ruleBaseData.put("reviewedBy", ruleCatalog.getReviewBy());
					ruleBaseData.put("dqDimension", ruleCatalog.getDimensionName());
					ruleBaseData.put("totalRecords", totalRecords);

					Map<String, Object> oRuleCatalogResultsData = null;

					if (getResultsDataMethods.containsKey(sCheckType)) {

						oRuleCatalogResultsData = getResultsDataMethods.get(sCheckType)
								.getResultstDataForGivenCheck(ruleBaseData);

						// DuplicateCheckPrimaryFields is processed once - update the flag
						if (sCheckType.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name()))
							isDupcheckPrmyColsResultsFetched = true;

						// Add all the result data of the check to output
						for (String sResultKey : oRuleCatalogResultsData.keySet()) {
							ruleBaseData.put(sResultKey, oRuleCatalogResultsData.get(sResultKey));
						}

					}

					double failedRecords = 0.0;
					if (ruleBaseData.get("failedRecords") != null) {
						failedRecords = Double.parseDouble(ruleBaseData.get("failedRecords").toString());
					}
					double full_Records = 0.0;
					if (ruleBaseData.get("totalRecords") != null) {
						full_Records = Double.parseDouble(ruleBaseData.get("totalRecords").toString());
					}

					double resultDQI_cal = 0.0;
					if (full_Records > 0) {
						resultDQI_cal = (full_Records - failedRecords) * 100 / full_Records;
					}

					String resultStatus = (String) ruleBaseData.get("resultStatus");
					if (resultStatus == null || resultStatus.trim().isEmpty()) {
						resultStatus = (failedRecords > 0.0) ? "failed" : "passed";
					}

					DecimalFormat df = new DecimalFormat("#0.00");

					ruleBaseData.put("resultStatus", resultStatus);
					ruleBaseData.put("failedRecords", failedRecords);
					ruleBaseData.put("resultDQI", df.format(resultDQI_cal));

					// Store the result of DuplicateCheckPrimaryFields into variable
					if (sCheckType.equalsIgnoreCase(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name()))
						dupcheckPrmyColsResultsData = ruleBaseData;
					// Set the result to final variable
					else
						apiResult.put(new JSONObject(ruleBaseData));

				} catch (Exception e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}

			}

			// Check if DuplicateCheckPrimaryFields results are available and add them to
			// final result
			if (isDupcheckPrmyColsResultsFetched && dupcheckPrmyColsResultsData != null) {
				// Set columns
				String columnNames = String.join(",", dupcheckPrmyColsList);
				dupcheckPrmyColsResultsData.put("columnName", columnNames);

				// Set rule Expression
				String ruleexp = colChecks_ruleExpMap.get(RuleCatalogCheckTypes.DuplicateCheckPrimaryFields.name());
				ruleexp = ruleexp.replace("<column>", columnNames);
				dupcheckPrmyColsResultsData.put("ruleExpression", ruleexp);

				// Add to final response
				apiResult.put(new JSONObject(dupcheckPrmyColsResultsData));
			}

		}
		return apiResult;
	}

	/*
	 * This method is used to check if the logged in user is Approved
	 */
	public boolean isLoggedInUserApprover(String userLDAPGroups, Long nIdRole) {
		boolean lRetValue = false;
		boolean lActiveDirectoryFlag = JwfSpaInfra
				.getPropertyValue(appDbConnectionProperties, "isActiveDirectoryAuthentication", "N")
				.equalsIgnoreCase("Y");

		String sBelongsToLdapGroups = "";
		List<String> aBelongsToLdapGroups = new ArrayList<String>();
		String sDefaultAdminRoleName = "ADMIN";

		String sSqlQyery = "";
		SqlRowSet oSqlRowSet = null;

		if (userLDAPGroups != null) {
			sBelongsToLdapGroups = userLDAPGroups.toUpperCase();
			aBelongsToLdapGroups = Arrays.asList(sBelongsToLdapGroups.split(","));
			sBelongsToLdapGroups = "'" + sBelongsToLdapGroups.replaceAll(",", "','") + "'";
		}

		if (nIdRole == null) {
			nIdRole = 0l;
		}

		LOG.debug("\n====> UserLDAPGroups: " + sBelongsToLdapGroups);
		DateUtility.DebugLog("isLoggedInUserApprover 01",
				String.format("Session and Input values used for logic **%1$s**, **%2$s**, **%3$s**, **%4$s**",
						lActiveDirectoryFlag, sDefaultAdminRoleName, nIdRole, sBelongsToLdapGroups));

		if (lActiveDirectoryFlag) {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQyery = sSqlQyery + "select count(*) as BelongsToApproverGroup \n";
				sSqlQyery = sSqlQyery + "from login_group \n";
				sSqlQyery = sSqlQyery + String.format("where upper(group_name) in (%1$s) \n", sBelongsToLdapGroups);
				sSqlQyery = sSqlQyery + "and   COALESCE(is_approver,cast(0 as int)) > 0;";
			} else {
				sSqlQyery = sSqlQyery + "select count(*) as BelongsToApproverGroup \n";
				sSqlQyery = sSqlQyery + "from login_group \n";
				sSqlQyery = sSqlQyery + String.format("where upper(group_name) in (%1$s) \n", sBelongsToLdapGroups);
				sSqlQyery = sSqlQyery + "and   ifnull(is_approver,cast(0 as unsigned)) > 0;";
			}
			LOG.debug("\n====> Query: " + sSqlQyery);
			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQyery);

			lRetValue = (oSqlRowSet.next()) ? ((oSqlRowSet.getInt("BelongsToApproverGroup") > 0) ? true : false)
					: false;
		} else {
			sSqlQyery = String.format(
					"select count(*) as Count from Role where idRole = %1$s and upper(roleName) = '%2$s'", nIdRole,
					sDefaultAdminRoleName);

			LOG.debug("\n====> Query: " + sSqlQyery);

			oSqlRowSet = jdbcTemplate.queryForRowSet(sSqlQyery);
			if (oSqlRowSet.next()) {
				lRetValue = (oSqlRowSet.getInt("Count") > 0) ? true : false;
			}

		}

		DateUtility.DebugLog("isLoggedInUserApprover 02",
				String.format("Logical processed values **%1$s**, **%2$s**, **%3$s**, **%4$s**, **%5$s**",
						lActiveDirectoryFlag, sDefaultAdminRoleName, sBelongsToLdapGroups, nIdRole, lRetValue));

		return lRetValue;
	}

	/*
	 * This method is used to trigger Jbpm work flow
	 */
	public boolean triggerJbpmWorkFlow(long idApp, int statusCode) {
		LOG.info("\n=========> triggerJbpmWorkFlow - Start <=========");
		boolean result = false;
		try {
			/*
			 * If the approve_status is 'UNIT_TEST_READY' then Invoke External Rest API
			 */
			LOG.debug("\n====> idApp: " + idApp);

			String approveStatusCode = validationcheckdao.getApproveStatusById(statusCode);
			LOG.debug("\n====> approveStatusCode: " + approveStatusCode);

			if (approveStatusCode != null
					&& approveStatusCode.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1)) {

				String jbpm_workflow_url = appDbConnectionProperties.getProperty("jbpm.workflow.url");
				LOG.debug("\n====> jbpm_workflow_url: " + jbpm_workflow_url);

				String jbpm_workflow_user = appDbConnectionProperties.getProperty("jbpm.workflow.user");
				String jbpm_workflow_password = appDbConnectionProperties.getProperty("jbpm.workflow.password");

				if (jbpm_workflow_url != null && !jbpm_workflow_url.trim().isEmpty()) {
					try {
						jbpm_workflow_url = jbpm_workflow_url + idApp;
						LOG.debug("\n====> jbpm_workflow_url with request params: " + jbpm_workflow_url);

						// Invoking the API
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);
						String notEncoded = jbpm_workflow_user + ":" + jbpm_workflow_password;
						String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
						headers.add("Authorization", encodedAuth);

						// set my entity
						HttpEntity<Object> entity = new HttpEntity<Object>(headers);

						ResponseEntity<String> out = restTemplate.exchange(jbpm_workflow_url, HttpMethod.GET, entity,
								String.class);

						LOG.debug(out.getBody());
						LOG.debug(out.getStatusCode());

						if (out.getStatusCode() == HttpStatus.OK) {
							LOG.debug(
									"\n====> jbpm_workflow for idApp:[" + idApp + "] triggering is successful !!");
							result = true;
						} else {
							LOG.debug(
									"\n====> jbpm_workflow for idApp: [" + idApp + "] triggering is failed !!");
						}

					} catch (Exception e) {
						LOG.error(e.getMessage());
						LOG.debug("\n====> Exception occurred while invoking jbpm workflow API for idApp: ["
								+ idApp + "] !!");
						e.printStackTrace();
					}
				} else {
					LOG.error("\n====> jbpm_workflow_url is empty cannot trigger rest API !!");
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug("\n====> Exception occurred while in  triggerJbpmWorkFlow for idApp:[" + idApp + "]!!");
			e.printStackTrace();
		}
		LOG.info("\n=========> triggerJbpmWorkFlow - End <=========");
		return result;
	}

	/*
	 * This method is used to fetch and Update the Defect code for all the rules
	 */
	public boolean fetchAndUpdateDefectCodeForRule(long idApp, int statusCode) {
		LOG.info("\n=========> fetchAndUpdateDefectCodeForRule - Start <=========");

		boolean result = false;
		try {
			/*
			 * If the approve_status is 'UNIT_TEST_READY' then Invoke External Rest API to
			 * fetch the defect codes and update them in rule catalog
			 */
			LOG.debug("\n====> idApp: " + idApp);

			String approveStatusCode = validationcheckdao.getApproveStatusById(statusCode);
			LOG.debug("\n====> approveStatusCode: " + approveStatusCode);

			if (approveStatusCode != null
					&& approveStatusCode.equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_1)) {

				String ruleDefectCodeRestUrl = appDbConnectionProperties.getProperty("ruledefectcode.rest.url");
				LOG.debug("\n====> ruleDefectCodeRestUrl: " + ruleDefectCodeRestUrl);

				if (ruleDefectCodeRestUrl != null && !ruleDefectCodeRestUrl.trim().isEmpty()) {
					try {
						ruleDefectCodeRestUrl = ruleDefectCodeRestUrl + idApp;
						LOG.debug("\n====> ruleDefectCodeRestUrl with request params: " + ruleDefectCodeRestUrl);

						// Invoking the API
						RestTemplate restTemplate = new RestTemplate();
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						// set my entity
						HttpEntity<Object> entity = new HttpEntity<Object>(headers);

						ResponseEntity<String> out = restTemplate.exchange(ruleDefectCodeRestUrl, HttpMethod.GET,
								entity, String.class);

						LOG.debug("\nResponse status: " + out.getStatusCode());

						if (out.getStatusCode() == HttpStatus.OK) {
							String responseBody = out.getBody();
							LOG.debug("\nResponse body: " + responseBody);

							JSONArray jsonArray = new JSONArray(responseBody);
							if (jsonArray != null) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = jsonArray.getJSONObject(i);

									// Get RuleNo and DQRId
									String ruleNo = object.getString("ruleNo");
									long ruleId = Long.parseLong(ruleNo);
									String defectCode = object.getString("dqrId");

									// Update the defect code to rule
									boolean isDefectCodeLinkedToRule = validationcheckdao.linkDefectCodeToRule(idApp,
											ruleId, defectCode);

									LOG.debug("\n====> Rule No: " + ruleNo + " , defectCode: " + defectCode
											+ ", isDefectCodeLinkedToRule: " + isDefectCodeLinkedToRule);

								}
							}

							LOG.debug("\n====> fetchAndUpdateDefectCodeForRule for idApp:[" + idApp
									+ "] triggering is successful !!");
							result = true;
						} else {
							LOG.debug("\n====> fetchAndUpdateDefectCodeForRule for idApp: [" + idApp
									+ "] triggering is failed !!");
						}

					} catch (Exception e) {
						LOG.error(e.getMessage());
						LOG.debug(
								"\n====> Exception occurred while invoking rest api to get defectcodes for idApp: ["
										+ idApp + "] !!");
						e.printStackTrace();
					}
				} else {
					LOG.error("\n====> ruledefectcode.rest.url is empty cannot trigger rest API !!");
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug("\n====> Exception occurred fetchAndUpdateDefectCodeForRule for idApp:[" + idApp + "]!!");
			e.printStackTrace();
		}
		LOG.info("\n=========> fetchAndUpdateDefectCodeForRule - End <=========\n");
		return result;
	}

	/*
	 * This method is to compare two rules of RuleCatalog
	 */
	private boolean isCustomRuleModified(RuleCatalog actual_rc_check, RuleCatalog modified_rc_check) {

		// Rule expresssion
		String modified_ruleExpression = modified_rc_check.getRuleExpression();
		String act_ruleExpression = actual_rc_check.getRuleExpression();

		// Matching Rules
		String modified_matchingRules = modified_rc_check.getMatchingRules();
		String act_matchingRules = actual_rc_check.getMatchingRules();

		// Threshold
		double modified_threshold = modified_rc_check.getThreshold();
		double act_threshold = actual_rc_check.getThreshold();

		// columnName
		String modified_columnName = modified_rc_check.getColumnName();
		String act_columnName = actual_rc_check.getColumnName();

		// Rule Description
		String modified_ruleDescription = modified_rc_check.getRuleDescription();
		String act_ruleDescription = actual_rc_check.getRuleDescription();

		// DimensionId
		long modified_dimensionId = modified_rc_check.getDimensionId();
		long act_dimensionId = actual_rc_check.getDimensionId();

		// Defect code
		String modified_defectCode = modified_rc_check.getDefectCode();
		String act_defectCode = actual_rc_check.getDefectCode();

		// Filter Condition
		String modified_filterCondition = modified_rc_check.getFilterCondition();
		String act_filterCondition = actual_rc_check.getFilterCondition();

		// Filter Condition
		String modified_rightTemplatefilterCondition = modified_rc_check.getRightTemplateFilterCondition();
		String act_rightTemplatefilterCondition = actual_rc_check.getRightTemplateFilterCondition();

		boolean custom_rule_modified = false;

		// Compare ruleExpression
		if (((modified_ruleExpression == null || modified_ruleExpression.trim().isEmpty())
				&& (act_ruleExpression != null && !act_ruleExpression.trim().isEmpty()))
				|| ((act_ruleExpression == null || act_ruleExpression.trim().isEmpty())
						&& (modified_ruleExpression != null && !modified_ruleExpression.trim().isEmpty()))
				|| (modified_ruleExpression != null && act_ruleExpression != null
						&& !modified_ruleExpression.trim().equalsIgnoreCase(act_ruleExpression.trim()))) {
			custom_rule_modified = true;
		}
		// Compare matching rules
		else if (((modified_matchingRules == null || modified_matchingRules.trim().isEmpty())
				&& (act_matchingRules != null && !act_matchingRules.trim().isEmpty()))
				|| ((act_matchingRules == null || act_matchingRules.trim().isEmpty())
						&& (modified_matchingRules != null && !modified_matchingRules.trim().isEmpty()))
				|| (modified_matchingRules != null && act_matchingRules != null
						&& !modified_matchingRules.trim().equalsIgnoreCase(act_matchingRules.trim()))) {
			custom_rule_modified = true;
		}
		// Compare Threshold
		else if (modified_threshold != act_threshold) {
			custom_rule_modified = true;
		}
		// Compare DimensionId
		else if (modified_dimensionId != act_dimensionId) {
			custom_rule_modified = true;
		}
		// Compare Defect code
		else if (((modified_defectCode == null || modified_defectCode.trim().isEmpty())
				&& (act_defectCode != null && !act_defectCode.trim().isEmpty()))
				|| ((act_defectCode == null || act_defectCode.trim().isEmpty())
						&& (modified_defectCode != null && !modified_defectCode.trim().isEmpty()))
				|| (modified_defectCode != null && act_defectCode != null
						&& !modified_defectCode.trim().equalsIgnoreCase(act_defectCode.trim()))) {
			custom_rule_modified = true;
		}
		// Compare ColumnName
		else if (((modified_columnName == null || modified_columnName.trim().isEmpty())
				&& (act_columnName != null && !act_columnName.trim().isEmpty()))
				|| ((act_columnName == null || act_columnName.trim().isEmpty())
						&& (modified_columnName != null && !modified_columnName.trim().isEmpty()))
				|| (modified_columnName != null && act_columnName != null
						&& !modified_columnName.trim().equalsIgnoreCase(act_columnName.trim()))) {
			custom_rule_modified = true;
		}
		// Compare Rule Description
		else if (((modified_ruleDescription == null || modified_ruleDescription.trim().isEmpty())
				&& (act_ruleDescription != null && !act_ruleDescription.trim().isEmpty()))
				|| ((act_ruleDescription == null || act_ruleDescription.trim().isEmpty())
						&& (modified_ruleDescription != null && !modified_ruleDescription.trim().isEmpty()))
				|| (modified_ruleDescription != null && act_ruleDescription != null
						&& !modified_ruleDescription.trim().equalsIgnoreCase(act_ruleDescription.trim()))) {
			custom_rule_modified = true;
		}
		// Compare Filter Condition
		else if (((modified_filterCondition == null || modified_filterCondition.trim().isEmpty())
				&& (act_filterCondition != null && !act_filterCondition.trim().isEmpty()))
				|| ((act_filterCondition == null || act_filterCondition.trim().isEmpty())
						&& (modified_filterCondition != null && !modified_filterCondition.trim().isEmpty()))
				|| (modified_filterCondition != null && act_filterCondition != null
						&& !modified_filterCondition.trim().equalsIgnoreCase(act_filterCondition.trim()))) {
			custom_rule_modified = true;
		}
		// Compare Right Template Filter Condition
		else if (((modified_rightTemplatefilterCondition == null
				|| modified_rightTemplatefilterCondition.trim().isEmpty())
				&& (act_rightTemplatefilterCondition != null && !act_rightTemplatefilterCondition.trim().isEmpty()))
				|| ((act_rightTemplatefilterCondition == null || act_rightTemplatefilterCondition.trim().isEmpty())
						&& (modified_rightTemplatefilterCondition != null
								&& !modified_rightTemplatefilterCondition.trim().isEmpty()))
				|| (modified_rightTemplatefilterCondition != null && act_rightTemplatefilterCondition != null
						&& !modified_rightTemplatefilterCondition.trim()
								.equalsIgnoreCase(act_rightTemplatefilterCondition.trim()))) {
			custom_rule_modified = true;
		}

		return custom_rule_modified;
	}

	public List<String> getRuleTypeList() {
		List<String> ruleTypeList = new ArrayList<>();
		try {
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_GLOBAL_RULE);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_CUSTOM_RULE);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_NULLCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_LENGTHCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_MAXLENGTHCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_BADDATACHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DEFAULTCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_PATTERNCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DATERULECHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DATADRIFTCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_VALUEANOMALYCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_TIMELINESSCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_NUMERICALSTATISTICSCHECK);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS);
			ruleTypeList.add(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return ruleTypeList;
	}

	public RuleCatalogCheckSpecification getCheckSpecificationByRuleType(String ruleType) {

		RuleCatalogCheckSpecification ruleCatalogCheckSpecification = null;
		List<RuleCatalogCheckSpecification> checks_specs_List = getRuleCatalogCheckSpecifications();

		for (RuleCatalogCheckSpecification rcSpecification : checks_specs_List) {

			String checkDisplayName = rcSpecification.getCheckDisplayName();
			if (checkDisplayName.equalsIgnoreCase(ruleType)) {
				ruleCatalogCheckSpecification = rcSpecification;
				LOG.debug(rcSpecification);
				break;
			}
		}
		return ruleCatalogCheckSpecification;
	}

	/*
	 * This method is to get threshold_value for specific columns and check
	 */
	public double getRuleCatalogThresholdForChecks(long idApp,String ColName,String Checks) {
		String query = "select threshold_value from  listApplicationsRulesCatalog where rule_type='"+Checks+"' and idApp ="+idApp+" and column_name='"+ColName+"'";
		double threshold_value = jdbcTemplate.queryForObject(query, Double.class);
		return threshold_value;
	}
	
}
