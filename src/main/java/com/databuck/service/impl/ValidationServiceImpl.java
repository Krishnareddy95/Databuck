package com.databuck.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IValidationDAO;
import com.databuck.service.IValidationService;

@Service
public class ValidationServiceImpl implements IValidationService {

	@Autowired
	private IValidationDAO validationdao;
	@Autowired
	private IListDataSourceDAO iListDataSourceDAO;
	
	private static final Logger LOG = Logger.getLogger(ValidationServiceImpl.class);
	public List<ListDataSchema> getData() {

		List<ListDataSchema> listDataSchema= validationdao.getData();
		return listDataSchema;
	}
	public List<ListApplications> validationCheckView()
	{
		List<ListApplications> dataFromListApplicationsOfDQType = validationdao.getDataFromListApplicationsOfDQType();
		return dataFromListApplicationsOfDQType;
	}
	public boolean deleteValidationViewApplication(Long idApp)
	{
		return validationdao.deleteValidationViewApplication( idApp);
	}
	public int[] saveSchemavalues(Long threshold ,Long identity , Long idApp)
	{
		int[] updateSchemavaluesinListDfTranRule = validationdao.updateSchemavaluesinListDfTranRule(threshold, identity,idApp);
		return updateSchemavaluesinListDfTranRule;
	}

	public ListDataSource getListDataSourceDataOfIdData(Long idData)
	{
		return iListDataSourceDAO.getDataFromListDataSourcesOfIdData(idData);
	}
	public List<ListDataDefinition> getListDataDefinitionData(Long idData)
	{
		return validationdao.getListDataDefinitionsByIdData(idData);
	}
	public boolean changeAllNonNullsToYes(Long idData)
	{
		return validationdao.changeAllNonNullsToYes(idData);
	}
	public boolean changeAllMicrosegmentToYes(Long idData)
	{
		return validationdao.changeAllMicrosegmentToYes(idData);
	}
	public boolean changeAlllastReadTimeToYes(Long idData)
	{
		return validationdao.changeAlllastReadTimeToYes(idData);
	}
	
	public boolean changeAllIsMaskedToYes(Long idData)
	{
		return validationdao.changeAllIsMaskedToYes(idData);
	}
	public boolean changeAllPartitionByToYes(Long idData)
	{
		return validationdao.changeAllPartitionByToYes(idData);
	}
	public boolean changeAllDataDriftToYes(Long idData)
	{
		return validationdao.changeAllDataDriftToYes(idData);
	}
	public boolean changeAllStartDateToYes(Long idData)
	{
		return validationdao.changeAllStartDateToYes(idData);
	}
	public boolean changeAllEndDateToYes(Long idData)
	{
		return validationdao.changeAllEndDateToYes(idData);
	}
	public boolean changeAllTimelinessKeyToYes(Long idData)
	{
		return validationdao.changeAllTimelinessKeyToYes(idData);
	}
	public boolean changeAllRecordAnomalyToYes(Long idData)
	{
		return validationdao.changeAllRecordAnomalyToYes(idData);
	}
	public boolean changeAllDefaultCheckToYes(Long idData)
	{
		return validationdao.changeAllDefaultCheckToYes(idData);
	}
	public boolean changeAllDateRuleToYes(Long idData)
	{
		return validationdao.changeAllDateRuleToYes(idData);
	}
	public boolean changeAllPatternCheckToYes(Long idData)
	{
		return validationdao.changeAllPatternCheckToYes(idData);
	}
	public boolean changeAllDefaultPatternCheckToYes(Long idData)
	{
		return validationdao.changeAllDefaultPatternCheckToYes(idData);
	}
	public boolean changeAllBadDataToYes(Long idData)
	{
		return validationdao.changeAllBadDataToYes(idData);
	}
	public boolean changeAllLengthCheckToYes(Long idData)
	{
		return validationdao.changeAllLengthCheckToYes(idData);
	}
	public boolean changeAllMaxLengthCheckToYes(Long idData)
	{
		return validationdao.changeAllMaxLengthCheckToYes(idData);
	}
	public boolean changeAllMatchValuetToYes(Long idData)
	{
		return validationdao.changeAllMatchValuetToYes(idData);
	}
}