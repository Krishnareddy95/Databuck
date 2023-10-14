package com.databuck.dao;

import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.listDataAccess;

public interface IExtendTemplateRuleDAO {


	List<ListColRules> getListColRulesForViewRules(long projectId, List<Project> projlst, String fromDate, String toDate);
	
	//Sumeet_07_08_2018
	ListColRules getListColRulesById(long idListColrules);
	void updateintolistColRules(ListColRules lcr);
	long insertintolistColRules(ListColRules lcr);

	SqlRowSet  checkIfDuplicateRuleNameAndDuplicateDataTemplate(ListColRules lcr);
	boolean isExtendTemplateRuleAlreadyExists(ListColRules lcr);

	long getCustomRuleByName(String ruleName);
	
	SqlRowSet getOperatorsDataFromSymbol();
	SqlRowSet getFunctionssDataFromSymbol();

	public void deactivateCustomRuleById(long idListColrules);
}
