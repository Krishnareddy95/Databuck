package com.databuck.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.databuck.bean.DeleteTempView;
import com.databuck.bean.Dimension;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.TemplateView;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.listDataBlend;

public interface ITemplateViewDAO {

	List<TemplateView> getTemplateView(List<Project> projList);

	List<ListDataDefinition> view(Long long1);

	DeleteTempView delete(int idDataBlend);

	int DeleteTempViewFully(Long idDataBlend);

	public Object[] getDerievedColumns(Long idData);

	int savedataforderievedcolumns(String name, String colExpression, Long idDataBlend, Long idData, String blendcolumn,
			String columnCategory, String columnValue, String columnValueType);

	int savedataforfilter(String name, String filteringExp, Long idDataBlend, Long idData, String blendcolumn);

	List<ListDataSource> getlistdatasourcesname(Long projectId);

	int insertintolistdatablend(String name, Long sourceid, String description, Long idUser, String createdByUser, Long projectId);

	// delete the listListDataBeanColDef record
	int DeleteListDataBeanColDef(int idDataBlend);

	public int nonNullyes(Long idData);

	public int hashValueyes(Long idData);

	public int numericalStatyes(Long idData);

	public int stringStatyes(Long idData);

	public int primaryKeyyes(Long idData);

	int deletederivedcolumns(Long idDataBlend);

	public int deletefilter(Long idDataBlend);

	List<listDataAccess> getDataFromListDataAccess(Long idData);

	String getReferenceFilesFromListDataFiles(Long idData);

	void matchingDerivedColumn(listDataBlend ldb);

	void matchingFilter(listDataBlend ldb);

	void qualityApplication(listDataBlend ldb);

	void deleteIdListColRulesData(long idListColrules);
	


	void insertIntoListDataBlendRowAdd(listDataBlend ldb);

	void deleteDataFromListDataBlend(long idDataBlend);

	int updateDupKeyYes(Long idData);

	List<listDataBlend> getDataFromListDataBlend(String name);

	public void updatelistApplicationsForSchemamatching(Long idApp, Long idData, Long idRightData, String matchtype,
			String name,String threasholdtype, String threshold, String prefix1, String prefix2);

	public void updatelistApplicationsForSchemamatchingForBoth_RC(Long idApp, Long idData, Long idRightData,
			String matchtype, String name,String threasholdtype, String threshold);
	
	//for Kafka changes
	public String getDataLocationByidData(Long idData);
	
	public void updatelistApplicationsForKafka(Long idApp,int windowTime,String startTime,String endTime);

	//For SecondSourceTemplateNames From Matching
	public List<String> getSecondSourceTemplateNames(Long projectId);

	public List<ListApplications> getValidationCheckOfTemplateById(Long idData, String templateName);
	
	public Long copyTemplate(long idData, String newTemplateName, String createdByUser);

	List<Dimension> getlistdimensionname();
	
	public List<ListDataDefinition> getListDataDefinitionsInStaging(long idData);
	public int updateCheckValueIntoListDatadefinition(long idData, String checkName, String columnName, String columnValue);
	public int updateCheckValueIntoStagingListDatadefinition(long idData, String checkName, String columnName, String columnValue);	
	
	public int updatePatternIntoListDatadefinition(long idData, String checkName, String columnName, String columnValue, String defaultPattern);
	public int updatePatternIntoStagingListDatadefinition(long idData, String checkName, String columnName, String columnValue, String defaultPattern);
	
	List<Map<String, Object>> getListSecondDatasources(Long projectId);

	void updateSchemamatching(Long idApp, String description, String threasholdType, String rcThreshold, String prefix1,
			String prefix2);

	public List<String> getPrimaryCheckEnabledColumnsByIdData(Long idData);
	public void copyDerivedTemplate(long idData, long newIdData, String createdByUser,  String newTemplateName);
	public List<listDataAccess> getMonitoredTableNamesfromListDataAccess(listDataAccess lda);
	
	public List<listDataAccess> getMonitoredTableNamesfromidDataList(List<Long> idDataList);
	 public List<ListApplications> getidAppListFromListApplication(Long idDataList);
	 public List<listDataAccess> getUniqueListDataAccess();
	public List<listDataAccess> getDataFromListDataAccessToExport(Long idData);
}
