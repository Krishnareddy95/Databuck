package com.databuck.dao;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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

public interface GlobalRuleDAO {


	List<GlobalRuleView> getListColRulesForViewRules();


	ListColGlobalRules getDataFromListDataSourcesOfIdData(long idData);
	 ListColGlobalRules getDataFromListDataSourcesruledomain(long idData,int domain_id);
	List<ruleFields> getsynforruledomain(int domain_id,long rule_id);

	void updateintolistColRules(ListColGlobalRules lcr);
	Long insertintolistColRules(ListColGlobalRules lcr);
	void insertRuleSynonymMapping(RuleToSynonym ruleToSynonym);
	
	int insertintoSynonymsLibrary(ruleFields rc);

	SqlRowSet  checkIfDuplicateRuleNameAndDuplicateDataTemplate(ListColRules lcr);
	int  checkIfDuplicateGlobalRule(ListColGlobalRules lcr);
	int  checkIfDuplicateRulefield(ruleFields rc);
	int getruleId(String rulename,int domain_id);
	void deleteGlobalRulesData(long idListColrules);


	List<ruleFields> getListGlobalRulesSynonyms();
	 public List<ruleFields> getRuleField(int idListColrules);

	 public int deleteGlobalRulesField(int ruleid );
	 public String getListPossibleName(int domain_id,String fieldname);
	 public int getDomainId(String domain);
	/* public void insertInToDamains(ListColGlobalRules lcr);*/
	 public String getdomainName(String domain);

	 public List<Domain> getDomainList();

	 public String getPossibleName(ruleFields rc);
	 public int getCountOfTableColumn(ruleFields rc);
	public int  updateIntoSynonymsLibrary(ruleFields rc);
	public int getsynosymId(ruleFields rc);

	//Added methode for editGlobal Rule
	public List<ruleFields> getSynonymIdByRuleId(long idListColrules);

	public List<SynonymLibrary> getSynonymViewList(int nDomainId);

	public int getEnterpriseDomainId();

	public double getGlobalRuleThreshold(long nIdData, long nIdApp, String sRuleName);
	public String unlinkGlobalRule(int nRuleCatalogRowId, String sRuleType);
	public List<ImportExportGlobalRuleDTO> getGlobalRulesOfDomainForExport(int exportDomainId, Long projectId);
	public boolean checkIfDuplicateGlobalRuleName(ListColGlobalRules lcr);
	public boolean updateListColGlobalRuleThreshold(double threshold, long idListColRule);
	public List<Long> getTemplateIdsForGlobalRuleId(long idListColrules);
	public double getGlobalRulesThresholdByIdListColRules(long idListColRule);
	List<GlobalFilters> getListGlobalFilters();
	public GlobalFilters getDataFromGlobalFilters(long filterId, int domain_id);
	public GlobalFilters getGlobalFilterById(long filterId);
	public int updateIntoGlobalFilters(GlobalFilters globalFilters);
	public Long insertIntoGlobalFilters(GlobalFilters globalFilters);
	public List<GlobalFilters> getAllGlobalFiltersByDomain(int domainId);
	public JSONObject getGlobalFilterConditionByName(String filterName,int domainId);
	public SqlRowSet getGlobalRulesByFilterId(long filterId);
	public boolean updateFilterConditionOfRuleTemplateMapping(long templateId,long globalRuleId, String filterCondition);
	public boolean updateMatchingRulesOfRuleTemplateMapping(long templateId, long globalRuleId, String matchingRules);
	public boolean updateRuleExpressionOfRuleTemplateMapping(long templateId, long globalRuleId, String ruleExpression);
	public boolean updateAnchorColumnOfRuleTemplateMapping(long templateId, long globalRuleId, String anchorColumns);
	public List<HashMap<String, String>> getAvailableGlobalRules(String domainIdList);
	public List<HashMap<String, String>> getAvailableReferenceRulesForTemplate(String domainIdList, long idDataSchema, String tableName, long idData);	
	public List<HashMap<String, String>> getAvailableGlobalThresholds(String domainIdList);
	public void insertIntoListGlobalThresholdsSelected(long idGlobalThreshold, long idData, long idColumn);
	public ListColGlobalRules getGlobalRuleById(long globalRuleId);
	public boolean updateSynonymPossiblenames(long synonyms_Id, String newPossibleName);
	public boolean updateListColGlobalRuleDimension(long dimensionId, long idListColRule);
	public long copyGlobalRulesData(String newRuleName, long idListColrules);
	public boolean updateRightTemplateFilterConditionOfRuleTemplateMapping(long templateId, long globalRuleId,
			String rightTemplateFilterCondition);
	public SqlRowSet getGlobalRulesByRightTemplateFilterId(long rightTemplateFilterId);

	public JSONObject getValidateGlobalRuleByUniqueId(String uniqueId);

	public List<SynonymLibrary> getSynonymListByDomainAndName(int nDomainId, String synonymName);

	public HashMap<String, String> getNullFilterColumnsForTemplate(long templateId);

	public JSONObject getValidateGlobalFilterByUniqueId(String uniqueId);

	public boolean isDuplicateGlobalFilter(int domainId,String globalFilterName, String filterCondition);

	public List<RuleMappingDetails> getMappedGlobalRuleForTemplate(long templateId);

	public List<ListColGlobalRules> getGlobalRulesForExport(String globalRuleId);

	public List<GlobalFilters> getGlobalFilterForExport(String filterId);
}
