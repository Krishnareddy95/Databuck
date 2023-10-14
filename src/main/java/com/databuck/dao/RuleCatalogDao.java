package com.databuck.dao;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.databuck.bean.ListApplications;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.RuleCatalogCheckSpecification;
import com.google.common.collect.Multimap;
import com.databuck.bean.ListDfTranRule;

public interface RuleCatalogDao {

	public List<Map<String, Object>> getRCApprovalOptionsList();

	public boolean editRuleInRuleCatalog(RuleCatalog ruleCatalog);

	public boolean editRuleInRuleCatalogStaging(RuleCatalog ruleCatalog);

	public boolean updateValidationRuleCatalogStatus(long idApp, int approve_status, String approve_comments,
			Long reviewedByUser, String approverName);

	public boolean updateValidationStagingRuleCatalogStatus(long idApp, int approve_status, String approve_comments,
			Long reviewedByUser, String approverName);

	public Map<String, Long> getDimensionsList();
	
	public JSONArray getNonEnabledColumnsForCheck(long idData,String checkColumnName, String thresholdColumnName);
	
	public JSONArray getNonEnabledColumnsForLengthCheck(long idData, String templateCheckThresholdColumn);

	public JSONArray getNonEnabledColumnsForPatternCheck(long idData,String templateCheckThresholdColumn);
	public JSONArray getNonEnabledColumnsForDefaultCheck(long idData,String templateCheckThresholdColumn);
	public JSONArray getNonEnabledColumnsForDateRuleCheck(long idData,String templateCheckThresholdColumn);

	public JSONArray getNonEnabledColumnsForPrimaryKeyAndDuplicateKeyCheck(long idData);

	public void autoApproveThresholdChangesInActualRuleCatalog(long idApp,String checkName,String column_or_rule_name,double updated_Threshold, String rule_expression, String defaultPattern);

	public void autoApproveThresholdChangesInStagingRuleCatalog(long idApp,String checkName,String column_or_rule_name,double updated_Threshold, String rule_expression, String defaultPattern);

	public void updateModifiedRulesInRuleCatalog(long idApp, RuleCatalog current_rc_check,
			RuleCatalog modified_rc_check);

	public void updateModifiedRulesInRuleCatalogStaging(long idApp, RuleCatalog current_rc_check,
			RuleCatalog modified_rc_check);

	public List<RuleCatalog> getRulesFromRuleCatalog(long idApp);

	public List<RuleCatalog> getRulesFromRuleCatalogStaging(long idApp);

	public void insertRuleCatalogRecords(List<RuleCatalog> rulesList, long idApp);

	public void insertRuleCatalogRecordsToStaging(List<RuleCatalog> rulesList, long idApp);

	public void deletesRecordsFromRuleCatalog(List<RuleCatalog> rulesList, long idApp);

	public void deletesRecordsFromRuleCatalogStaging(List<RuleCatalog> rulesList, long idApp);

	public void updateRuleCatalogStatusToCreated(long idApp);

	public void updateStagingRuleCatalogStatusToCreated(long idApp);

	public List<RuleCatalog> getChecksDataForColumnCheck(RuleCatalogCheckSpecification oCheckSpec,
			ListApplications oListApplication);

	public List<RuleCatalog> getChecksDataForRulesCheck(RuleCatalogCheckSpecification oCheckSpec,
			ListApplications oListApplication);

	public String getValidationApprovalStatus(long idApp);

	public int getCountOfValidationsAssociatedWithCustomRule(long idListColrules);

	public String isDuplicateCheckAllEnabled(long idApp);

	public String isDuplicateCheckIdentityEnabled(long idApp);

	public String isStagingDuplicateCheckAllEnabled(long idApp);

	public String isStagingDuplicateCheckIdentityEnabled(long idApp);

	public List<Map<String, Object>> getDimensionDefectCodeList();

	public List<Map<String, Object>> getValidationsListForSchema(long idDataSchema);

	public void clearRulesFromActualRuleCatalog(long idApp);

	public void clearRulesFromStagingRuleCatalog(long idApp);

	public void copyRulesFromStagingCatalogToActual(long idApp);

	public void copyRulesFromActualCatalogToStaging(long idApp);

	public String getApproveStatusNameById(int approve_status);

	public void deleteUnusedDeactivatedCustomRules();

	public void copyLisApplicationsFromStagingToActual(long idApp);

	public void copyLisApplicationsFromActualToStaging(long idApp);

	public ListApplications getDataFromStagingListapplications(long idApp);

	public int updateStagingListDfTranRule(Long idApp, String duplicateCountIdentity,
			Double duplicateCountIdentityThreshold, String duplicateCountAll, Double duplicateCountAllThreshold);

	public int updateIntoStagingListapplication(ListApplications listApplications);

	public void copyListDfTranRuleFromStagingToActual(long idApp);

	public void copyListDfTranRuleFromActualToStaging(long idApp);

	public Multimap<String, Double> getDataFromStagingListDfTranRule(Long idApp);

	public Map<String, String> getDataFromStagingListDFTranRuleForMap(long idApp);

	public int getApprovalStatusCodeByStatusName(String approvalStatus);

	public int getCountOfValidationsAssociatedWithGlobalRule(long templateId, long globalRuleId);

	public void deleteUnusedDeactivatedGlobalRules();

	public boolean updateRuleDetailsOfCustomRuleFromRuleCatalog(RuleCatalog ruleCatalog);

	public boolean updateRuleDetailsOfGlobalRuleFromRuleCatalog(long templateid, RuleCatalog ruleCatalog);

	public boolean disableColumnCheckFromListDataDefinition(long templateId, String columnName, String check_columnName,
			String columnValue);

	public boolean disableColumnCheckFromStagingListDataDefinition(long templateId, String columnName,
			String check_columnName, String columnValue);

	public int updateStagingDuplicateCheckThresholdByType(long idApp,String type,double threshold);

	public int updateDuplicateCheckThresholdByType(long idApp,String type,double threshold);

	public long getRuleIdForGlobalRules(long templateId,String ruleName);

	public int updateGlobalRuleThreshold(long ruleId,double updated_Threshold);

	public int  updateCustomRuleThreshold(long  templateId , String ruleName,double updated_Threshold);

	public List<ListDfTranRule> getDataFromStagingListDfTranRuleForThreshold(Long idApp);

	public void enableOrDisableCheckInValidation(long idApp, String checkEntityColumn, String checkValue);
	
	public void enableOrDisableCheckInValidationStaging(long idApp, String checkEntityColumn, String checkValue);

	public void enableOrDisableDuplicateCheckByType(long idApp, String type, String checkEntityColumn, String checkValue);
	
	public void enableOrDisableStagingDuplicateCheckByType(long idApp, String type, String checkEntityColumn, String checkValue);
	
	public void approveRCAThresholdChangesInActualRuleCatalog(long idApp,String checkName,double updated_Threshold);
	
	public void approveRCAThresholdChangesInStagingRuleCatalog(long idApp,String checkName,double updated_Threshold);
	
	public void approveRCAThresholdChangesInStagingValidation(long idApp,String checkName,String column_Name,double updated_Threshold);
	
	public void approveRCAThresholdChangesInActualValidation(long idApp,String checkName,String column_Name,double updated_Threshold);

	public int getRowIdFromActualValidation(long idApp, int ruleType);

	public int getRowIdFromStagingValidation(long idApp, int rule_reference);

	public void deleteTagMappingsForDeletedRules(List<RuleCatalog> deletedRulesList, long idApp);

	void insertIntoStagingListDfTranRule(Long idApp);

	public void copyRuleCatalog(long sourceIdApp, long targetIdApp);

	public void copyRuleTagMapping(long sourceIdApp, long targetIdApp);

	public JSONArray getNonEnabledColumnsForDefaultPatternCheck(long idData, String templateCheckThresholdColumn);

	public boolean updateNullFilterColumnsOfRuleTemplateMapping(long templateId, long globalRuleId, String nullFilterColumns);
	public int updateNullFilterColumnsOfRuleCatlog(long idApp, long ruleId, String nullFilterColumns, String validationStatus);
	public Map<String, Double> getRuleCatalogThresholdValues(Long idApp, String check_name);
}