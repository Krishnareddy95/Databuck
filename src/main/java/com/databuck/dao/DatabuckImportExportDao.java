package com.databuck.dao;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabuckImportExportDao {
    public Map<String, String> getLinkedIds(Long idApp);
    public Map<String, Set<Long>> getLinkedIds(Long idData, String connectionIds);

    public Map<String, JSONArray> getExcludedRuleAndTemplate(Long idData, String connectionIds);

    public Map<String, Map<String, String>> getSynonymsForDomain(long domainId);

    public List<ListColGlobalRules> getGlobalRuleForDomainAndProject(long domainId, long projectId);

    public Long saveListDataSource(JSONObject listDataSource, long idDataSchema, int userId, String userName, Long projectId, Long domainId);

    public Long saveListDataAccess(JSONObject listDataAccess, long idDataSchema, int userId, long idData);

    public void saveListDataDefination(JSONArray listDataDefinations, long idData, boolean isStagingTransaction);

    public void saveRuleTemplateMapping(JSONArray ruleMappingDetails, long idData, Map<Long, Long> ruleIds);

    public Long saveListApplication(ListApplications listApplications);

    public Long saveSynonym(long domainId, String tableColumn, String possibleNames);

    public void updateSynonym(long synonymId, String tableColumns);

    public Long saveTaskStatus(String uniqueId, long exportedApplicationId, long importedApplicationId, String taskType, String filePath,
                               String status, String statusMessage, String errorCode, String errorMessage, long createdBy, String createdByUser, String hash);

    public void updateTaskStatus(long id, String filePath, String status, String statusMessage, String errorCode, String errorMessage, long importedApplicationId);
    public JSONObject getValidationImportByHashCode(String hashCode);

    public void deleteValidationDetails(String templateIds);
    public void deleteTemplateDetails(String templateIds);
    public void deleteGlobalRuleDetails(String ruleIds);
    public void deleteGlobalFiltersDetails(String filterIds);
    public void deleteSynonyms(String synonymsIds);
}
