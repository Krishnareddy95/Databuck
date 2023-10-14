package com.databuck.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.databuck.bean.GloabalRule;
import com.databuck.bean.HiveSource;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ListDerivedDataSource;
import com.databuck.bean.listDataAccess;
import com.databuck.restcontroller.ListDataDefinitionBean;

public interface IDataTemplateAddNewDAO {

	public Long addintolistdatasource(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
		List<String> primarykeyCols, List<ListDataDefinition> lstDataDefinition, List<GloabalRule> selected_list,Long projectId);

	public Long addintolistdatasourceupdate(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primarykeyCols, List<ListDataDefinition> lstDataDefinition,Long idData);

	public List<ListDataSchema> getListDataSchema(Long idDataSchema);

	public int insertDataIntoHiveSource(HiveSource hivesource);

	public String duplicatedatatemplatename(String dataTemplateName);
	
	public String duplicateCustomrulename(String customRuleName);

	public Long insertIntoListDataSources(ListDataSource lds,Long project_id);
	
	public Long insertIntoListDerivedDataSources(ListDerivedDataSource lds,Long project_id);

	public int insertIntoListDataAccess(listDataAccess lda);

	public void insertIntoListDataFiles(Long idData, ArrayList<String> al);
	public void deleteListDataDefinition(Long idData) ;

	public Long addintolistdatasourceForOracle(ListDataSource listDataSource, Map<String, String> tableData,
			listDataAccess listdataAccess, List<String> primarykeyCols,Long project_id);

	public JSONObject createTemplateForRestAPI(ListDataSource lds, Map<String, String> hm, listDataAccess lda,
			List<String> primaryKeyCols, List<ListDataDefinition> lstDataDefinition,
			List<ListDataDefinitionBean> metaData, HttpServletRequest req,Long project_id);
	
	public Long addIntoResultDb(long idApp, HttpSession session);
	
	public void populateColumnProfileMasterTable(long nDataTmplId, int nDomainId);
	public String getUserMessageFromExp(String errorMessage);
	public List<ListDataSchema> getuniqueUrlfromAllListDataSchema();
}