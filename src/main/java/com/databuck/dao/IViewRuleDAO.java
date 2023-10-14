package com.databuck.dao;

import java.util.List;
import java.util.Map;

import com.databuck.bean.GlobalRuleView;
import com.databuck.bean.ListApplicationsandListDataSources;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.RuleCatalog;
import com.databuck.bean.ViewRule;

public interface IViewRuleDAO {

	public List<ViewRule> getViewRuleList();

	public List<GlobalRuleView> getRuleListByTempalte(Long idData);

	public List<ViewRule> getViewRuleById(Long idruleMap);

	public boolean saveDashboardRule(ViewRule viewRuleObj);

	public boolean updateDashboardRule(ViewRule viewRuleObj);

	public boolean deleteView(Long idruleMap);

	public List<ListDataSource> getListDataSources(Long projectId, String idData);

	public List<ListApplicationsandListDataSources> getlistOfValidation(Long projectId, String idData);

	List<RuleCatalog> getRuleListByRuleId(String idlistColRules);

	public List<RuleCatalog> getRulesByIdApp(long nIdApp);

	public List<ListApplicationsandListDataSources> getDataFromListDataSources(Long projId);

	public Map<String, Object> getResultsForBadDataCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForDataDriftCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForDateRuleCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForDefaultCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForLengthCheck(Map<String, Object> oRuleCatalogRow);
	
	public Map<String, Object> getResultsForMaxLengthCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForNullCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForNumericalStatisticsCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForPatternCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForRules(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForRecordAnomalyCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForTimelinessCheck(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForDuplicateCheckPrimaryFields(Map<String, Object> oRuleCatalogRow);

	public Map<String, Object> getResultsForDuplicateCheckSelectedFields(Map<String, Object> oRuleCatalogRow);
	
	public Map<String, Object> getResultsForDefaultPatternCheck(Map<String, Object> oRuleCatalogRow);

	public long getValidationTotalRecordsByDateRun(long idApp, String executionDate, long run);

	public boolean isDateRunValid(long idApp, String executionDate, long run);
	
}