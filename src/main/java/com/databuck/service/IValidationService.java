package com.databuck.service;

import java.util.List;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.ValidationView;

public interface IValidationService {

	List<ListDataSchema> getData();
	public List<ListApplications> validationCheckView();
	public boolean deleteValidationViewApplication(Long idApp);
	public int[] saveSchemavalues(Long threshold ,Long identity , Long idApp);
	public ListDataSource getListDataSourceDataOfIdData(Long idData);
	public List<ListDataDefinition> getListDataDefinitionData(Long idData);
	public boolean changeAllNonNullsToYes(Long idData);
	public boolean changeAllMicrosegmentToYes(Long idData);
	public boolean changeAlllastReadTimeToYes(Long idData);
	public boolean changeAllIsMaskedToYes(Long idData);
	public boolean changeAllPartitionByToYes(Long idData);
	public boolean changeAllDataDriftToYes(Long idData);
	public boolean changeAllStartDateToYes(Long idData);
	public boolean changeAllEndDateToYes(Long idData);
	public boolean changeAllTimelinessKeyToYes(Long idData);
	public boolean changeAllRecordAnomalyToYes(Long idData);
	public boolean changeAllDefaultCheckToYes(Long idData);
	public boolean changeAllDateRuleToYes(Long idDate);
	public boolean changeAllPatternCheckToYes(Long idData);
	public boolean changeAllDefaultPatternCheckToYes(Long idData);
	public boolean changeAllBadDataToYes(Long idData);
	public boolean changeAllLengthCheckToYes(Long idData);
	public boolean changeAllMaxLengthCheckToYes(Long idData);
	public boolean changeAllMatchValuetToYes(Long idData);

}