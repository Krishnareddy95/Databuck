package com.databuck.service;

import com.databuck.bean.ListDataSource;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.impl.ValidationCheckDAOImpl;
import com.databuck.restcontroller.DatabuckRestDAOImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;

@Service
public class CreateValidationService {
	
	@Autowired
	private Properties appDbConnectionProperties;
	
	@Autowired
	private FileMonitorDao fileMonitorDao;
	
	@Autowired
	private ValidationCheckDAOImpl validationCheckDAO;
	
	private static final Logger LOG = Logger.getLogger(CreateValidationService.class);
	
	public Long createValidationCheck(String validationCheckName, Long idData, ListDataSource listDataSource,
			  List<ListDataDefinition> lstDataDefinition, long projectId, long domainId,
			  boolean microsegValidationAllowed) throws Exception {

			LOG.debug("projectId in createValidationCheck >>>>> " + projectId);
			
			long idApp = -99;
			
			Long idUser = listDataSource.getCreatedBy();
			LOG.debug("In createValidationCheck  idUser====> " + idUser);
			
			String createdByUser = listDataSource.getCreatedByUser();
			LOG.debug("=== createdByUserName ====>" + createdByUser);
			
			try {
				idApp = fileMonitorDao.insertintolistapplications(validationCheckName, "A default validation check",
				"Data Forensics", idData, idUser, (Double)0.0, "N", "", "", "","",projectId, createdByUser,domainId);
				
				LOG.debug("\n====> validation idApp is :" + idApp);
				
				// If idApp is invalid, return null
				if (idApp <= 0l) {
					return null;
				}
				
				ListApplications listApplications = new ListApplications();
				listApplications.setIdApp(idApp);
				listApplications.setName(idApp + "_" + validationCheckName);
				listApplications.setColOrderValidation("N");
				listApplications.setFileNameValidation("N");
				listApplications.setEntityColumn("N");
				listApplications.setNumericalStatCheck("N");
				listApplications.setStringStatCheck("N");
				listApplications.setDataDriftCheck("N");
				listApplications.setRecordAnomalyCheck("N");
				listApplications.setNonNullCheck("N");
				listApplications.setDupRowIdentity("N");
				listApplications.setDupRowIdentityThreshold(0.0);
				listApplications.setDupRowAll("N");
				listApplications.setDupRowAllThreshold(0.0);
				listApplications.setDuplicateCheck("N");
				listApplications.setUpdateFrequency("Never");
				listApplications.setRecordCountAnomaly("Y");
				listApplications.setKeyGroupRecordCountAnomaly("N");
				listApplications.setApplyDerivedColumns("N");
				listApplications.setApplyRules("N");
				listApplications.setRecordCountAnomalyThreshold(3.0);
				listApplications.setNumericalStatThreshold(3.0);
				listApplications.setStringStatThreshold(0.0);
				listApplications.setDataDriftThreshold(0.0);
				listApplications.setNonNullThreshold(0.0);
				listApplications.setTimeSeries("None");
				listApplications.setOutOfNormCheck("N");
				listApplications.setGroupEquality("N");
				listApplications.setGroupEqualityThreshold(0.0);
				listApplications.setBuildHistoricFingerPrint("N");
				listApplications.setIncrementalMatching("N");
				listApplications.setTimelinessKeyChk("N");
				listApplications.setDefaultCheck("N");
				listApplications.setlengthCheck("N");
				listApplications.setMaxLengthCheck("N");
				listApplications.setdGroupNullCheck("N");
				listApplications.setdGroupDateRuleCheck("N");
				listApplications.setdGroupDataDriftCheck("N");
				listApplications.setBadData("N");
				listApplications.setPatternCheck("N");
				listApplications.setCorrelationcheck("N");
				listApplications.setDateRuleChk("N");
				listApplications.setProjectId(projectId);
				listApplications.setDomainId(domainId);
				listApplications.setDefaultPatternCheck("N");
				
				if (lstDataDefinition != null) {
					for (Iterator<ListDataDefinition> lstIterator = lstDataDefinition.iterator(); lstIterator.hasNext();) {
						ListDataDefinition dataDefinition = lstIterator.next();
						if (dataDefinition.getNumericalStat().equalsIgnoreCase("Y")) {
							/*
							* Temporarily stopping the automatic enable of Distribution check
							*/
							// listApplications.setNumericalStatCheck("Y");
							listApplications.setNumericalStatThreshold(dataDefinition.getNumericalThreshold());
						}
						if (dataDefinition.getStringStat()!=null && dataDefinition.getStringStat().equalsIgnoreCase("Y")) {
							listApplications.setStringStatCheck("Y");
							listApplications.setStringStatThreshold(dataDefinition.getStringStatThreshold());
						}
						if (dataDefinition.getDataDrift()!=null && dataDefinition.getDataDrift().equalsIgnoreCase("Y")) {
							listApplications.setDataDriftCheck("Y");
							listApplications.setDataDriftThreshold(dataDefinition.getDataDriftThreshold());
						}
						if (dataDefinition.getRecordAnomaly()!=null && dataDefinition.getRecordAnomaly().equalsIgnoreCase("Y")) {
							
						}
						if (dataDefinition.getNonNull().equalsIgnoreCase("Y")) {
							listApplications.setNonNullCheck("Y");
							listApplications.setNonNullThreshold(dataDefinition.getNullCountThreshold());
						}
						if (dataDefinition.getPrimaryKey().equalsIgnoreCase("Y")) {
							listApplications.setDupRowIdentity("Y");
						}
						if (dataDefinition.getDupkey().equalsIgnoreCase("Y")) {
							listApplications.setDuplicateCheck("Y");
							listApplications.setDupRowAll("Y");
						}
						if (microsegValidationAllowed) {
							if (dataDefinition.getDgroup().equalsIgnoreCase("Y")) {
								listApplications.setRecordCountAnomaly("N");
								listApplications.setKeyGroupRecordCountAnomaly("Y");
							}
						}
						if (dataDefinition.getDefaultCheck() != null
							&& dataDefinition.getDefaultCheck().equalsIgnoreCase("Y")) {
							listApplications.setDefaultCheck("Y");
						}
						if (dataDefinition.getPatternCheck() != null
							&& dataDefinition.getPatternCheck().equalsIgnoreCase("Y")) {
							listApplications.setPatternCheck("Y");
						}
						if (dataDefinition.getLengthCheck()!=null && dataDefinition.getLengthCheck().equalsIgnoreCase("Y")) {
							listApplications.setlengthCheck("Y");
						}
						if (dataDefinition.getMaxLengthCheck()!=null && dataDefinition.getMaxLengthCheck().equalsIgnoreCase("Y")) {
							listApplications.setMaxLengthCheck("Y");
						}
						// Added filter For s3 iam role config batch only TODO: to be removed after
						// fixing the bad data check issue
//						if (dataDefinition.getBadData() != null && dataDefinition.getBadData().equalsIgnoreCase("Y")
//							&& listDataSource.getDataLocation() != DataSourceType.S3IAMROLEBATCHCONFIG) {
//							listApplications.setBadData("Y");
//						}
						if (dataDefinition.getDateRule() != null && dataDefinition.getDateRule().equalsIgnoreCase("Y")) {
							// listApplications.setDateRuleChk("Y");
						}
						if (dataDefinition.getDefaultPatternCheck() != null && dataDefinition.getDefaultPatternCheck().equalsIgnoreCase("Y")) {
							listApplications.setDefaultPatternCheck("Y");
						}
					}
				}
				
				validationCheckDAO.insertintolistdfsetruleandtranrule(idApp,
				listApplications.getRecordCountAnomalyThreshold(), listApplications.getDuplicateCheck());
				
				int updateintolistapplication = validationCheckDAO
				.updateintolistapplicationForAjaxRequest(listApplications);
				LOG.debug("\n====> Updating ListApplications Status :[" + updateintolistapplication + "]");
				



				int updateintolistdatadefinitions = validationCheckDAO.updateintolistdatadefinitions(
						listApplications.getIdData(), "N", listApplications.getNonNullThreshold(), "N", "N",
						listApplications.getNumericalStatThreshold(), "N", listApplications.getStringStatThreshold(), "N",
						listApplications.getRecordAnomalyThreshold(), listApplications.getDataDriftThreshold(), 0.0);
				
				
				
				LOG.debug("\n====> Updating ListDataDefinition for thresholds Status :["
				+ updateintolistdatadefinitions + "]");
				
				int insertintolistdftranrule = validationCheckDAO.insertintolistdftranrule(idApp,
				listApplications.getDupRowIdentity(), listApplications.getDupRowIdentityThreshold(),
				listApplications.getDupRowAll(), listApplications.getDupRowAllThreshold());
				LOG.debug("\n====> Inserting ListDFTranRule Status:[" + insertintolistdftranrule + "]");
				
			} catch (Exception e) {
			LOG.error("\n====> Exception encountered while creating validation check.");
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			throw e;
			}
			return idApp;
			
			}
	
	public Long createIncrementalValidationCheck(String validationCheckName, Long idData, ListDataSource listDataSource,
			 List<ListDataDefinition> lstDataDefinition, String projectId, long domainId,
			 boolean microsegValidationAllowed) throws Exception {

		LOG.debug("\n====> Creating  Incremental validation for template id: " + idData);
		
		Long idApp = null;
		try {
			String incr_coln_names_str = appDbConnectionProperties.getProperty("incremental.lastread.column.name");
			LOG.debug("\n====> Property [incremental.lastread.column.name] value:" + incr_coln_names_str);
			
			List<String> inc_col_list = null;
			if (incr_coln_names_str != null) {
			String[] comma_separated_column_names = incr_coln_names_str.trim().toLowerCase().split(",");
			inc_col_list = Stream.of(comma_separated_column_names).collect(Collectors.toList());
			}
			
			// Identifying the incremental column
			LOG.info("\n====> Identifying the incremental column ..");
			
			String selectedIncrCol = null;
			if (inc_col_list != null) {
				for (ListDataDefinition ldd : lstDataDefinition) {
					// Get column name and format
					String colName = ldd.getDisplayName();
					String format = ldd.getFormat();
					
					if (format.toLowerCase().startsWith("date") && inc_col_list.contains(colName.toLowerCase())) {
						selectedIncrCol = colName;
						LOG.debug("\n====> Incremental column found in template: " + selectedIncrCol);
						
						// Set IncrementalColumn to 'Y' in Template listDataDefinition and
						// Staging_listDataDefinition
						fileMonitorDao.updateIncrementalColStatus(idData, selectedIncrCol);
						
						break;
					}
				}
			}
			
			if (selectedIncrCol != null) {
				// Creating validation
				LOG.info("\n====> Creating incremental validation ");
				idApp = createValidationCheck(validationCheckName, idData, listDataSource, lstDataDefinition, Long.parseLong(projectId),
						
				domainId, microsegValidationAllowed);
				// change the validation type to Incremental -- IncrementalMatching='Y'
				fileMonitorDao.updateIncrementalMatchingOfApplications(idApp, "Y");
			
			} else
				LOG.info("\n====> No Incremental column found ,Creating normal Bulk load Validation!!");
			
		} catch (Exception e) {
		LOG.error("\n====> Failed to create Incremental validation !!");
		LOG.error("exception "+e.getMessage());
		e.printStackTrace();
		}
		return idApp;
		}
}
