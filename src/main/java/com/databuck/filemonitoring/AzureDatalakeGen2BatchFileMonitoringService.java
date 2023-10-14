package com.databuck.filemonitoring;


import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMHistory;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.impl.ValidationDAOImpl;
import com.databuck.service.DataProfilingTemplateService;
import com.databuck.service.CreateValidationService;


@Service
public class AzureDatalakeGen2BatchFileMonitoringService {

	@Autowired
	private FileMonitorDao fileMonitorDao;
	
	@Autowired
	DataProfilingTemplateService dataProfilingTemplateService;
	
	@Autowired
	ValidationDAOImpl validationDAO;
	
	@Autowired
	SchemaDAOI schemaDAOI;
	
    @Autowired
    ITaskDAO iTaskDAO;
    
    @Autowired
    private CreateValidationService createValidationService;

	public void azureDatalakeGen2Monitoring(DbkFMHistory loadToFMhistory,HttpSession session) {
		String account_name = loadToFMhistory.getAccount_name();
		String container_name = loadToFMhistory.getContainer_name();
		String schema_name = loadToFMhistory.getSchema_name();
		
		
		System.out.println("\n====> Inside azureDatalakeGen2Monitoring service");
		//get connection id
		try {
		
		Map<String, Object> connectionAndValidation = fileMonitorDao.getAzureConnectionByAccountKeyAndContainer(account_name,container_name,schema_name);
		System.out.println("connection and Validation for "+connectionAndValidation.toString());
		
		Long connection_Id = Long.valueOf((connectionAndValidation.get("idDataSchema")).toString());
		
		Long validation_Id = Long.valueOf((connectionAndValidation.get("idApp")).toString());
		
		loadToFMhistory.setConnection_id(connection_Id);
		loadToFMhistory.setValidation_id(validation_Id);
		
		
		boolean validationIdStatus = false;
		
		String connection_type = loadToFMhistory.getConnection_type();

		String table_or_subfolder_name = loadToFMhistory.getTable_or_subfolder_name();
		String dayOfWeek = loadToFMhistory.getCurrentLoadTime().getDayOfWeek().toString().substring(0,1)+loadToFMhistory.getCurrentLoadTime().getDayOfWeek().toString().substring(1).toLowerCase();
		Integer hour_of_day = loadToFMhistory.getCurrentLoadTime().getHour();
		String load_dateS = FileMonitoringUtilService.getUtcDate(loadToFMhistory.getCurrentLoadTime());
		String fileName = loadToFMhistory.getFile_name();
		
		Integer record_count = loadToFMhistory.getRecord_count();
		
		int loaded_time = loadToFMhistory.getCurrentLoadTime().getMinute();
		int loaded_hour = loadToFMhistory.getCurrentLoadTime().getHour();

		if(loadToFMhistory.getLast_load_time()!=null){

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

			String dateInString = loadToFMhistory.getLast_load_time();
			Date date = formatter.parse(dateInString);
//			String formattedDateString = formatter.format(date);

			OffsetDateTime offsetDateTime= OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			loaded_time = offsetDateTime.getMinute();
			loaded_hour = offsetDateTime.getHour();
		}

		String record_count_check= null;
		String column_metadata_check = null;
		String file_validity_status = null;
				

		//get conditional/expected information for summary table
		if (record_count > 0)
			record_count_check = "passed";
		else
			record_count_check = "failed";
		/* @@@ change below line after testing is done   */
		
		if (record_count_check.equalsIgnoreCase("passed"))
			file_validity_status = "passed";
		else
			file_validity_status = "failed";



		//save data to dbkFMLoadHistoryTable
		fileMonitorDao.saveDbkFMLoadHistory(loadToFMhistory);

		DBKFileMonitoringRules dbk_file_monitor_rules = new DBKFileMonitoringRules();
		dbk_file_monitor_rules.setConnectionId(connection_Id);
		dbk_file_monitor_rules.setValidationId(validation_Id);
		dbk_file_monitor_rules.setSchemaName(schema_name);
		dbk_file_monitor_rules.setTableName(table_or_subfolder_name);
		dbk_file_monitor_rules.setDayOfWeek(dayOfWeek);
		

		String file_indicator = null;
		String file_arrival_status = null;
		Integer expected_time = null;
		Integer expected_hour = null;
		Integer start_hour = null;
		Integer end_hour = null;

		List<DBKFileMonitoringRules> rulesList = fileMonitorDao.getRulesListForFile(dbk_file_monitor_rules);

		if (rulesList != null && rulesList.size() > 0) {

			DBKFileMonitoringRules rulesDetail = rulesList.get(0);

			file_indicator = rulesDetail.getFileIndicator();
			expected_hour = rulesDetail.getHourOfDay();
			expected_time = rulesDetail.getExpectedTime();
			start_hour = rulesDetail.getStartHour();
			end_hour = rulesDetail.getEndHour();

			if (file_indicator.equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_HOURLY)) {

				DBKFileMonitoringRules matchedHourlyRule = null;

				// loaded hour == 6
				// fmrule 1 == 8
				// fmrule2 == 7
				// fmrule2 == 4

				for(DBKFileMonitoringRules fmRule : rulesList) {
					if(fmRule.getFileIndicator().equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_HOURLY)) {
						Integer fm_expected_hour = fmRule.getHourOfDay();
						
						if(expected_hour == loaded_hour) {
							matchedHourlyRule =fmRule;
							break;
						} 
						else {
							if(matchedHourlyRule == null) {
								matchedHourlyRule = fmRule;
							} else {
								// matched hour
								Integer matched_hour = matchedHourlyRule.getHourOfDay();

								// matched hour = 8  expected_hour=7 loaddedhour = 5
								// 8-5= 3   7-5 = 2 pick 7

								if( Math.abs(matched_hour - loaded_hour) > Math.abs(fm_expected_hour - loaded_hour))
									matchedHourlyRule=fmRule;
							}
						}
					}
				}
				
				if(matchedHourlyRule != null) {
					expected_hour = matchedHourlyRule.getHourOfDay();
					expected_time = matchedHourlyRule.getExpectedTime();

					// Get the expected count
					int expectedCount = matchedHourlyRule.getExpectedFileCount();
					
					// Get the arrival count from the arrival table
					int actualArrivalcount = fileMonitorDao.getArrivalCountOfFile(dbk_file_monitor_rules, load_dateS, loaded_hour);
					
					if((actualArrivalcount + 1) > expectedCount) {
						file_indicator = DatabuckConstants.FILE_INDICATOR_HOURLY;
						file_arrival_status = DatabuckConstants.FILE_ARRIVAL_ADDITIONAL;
						
					} else {
						if (expected_time == loaded_time)
							file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_ON_TIME;
						else if (expected_time > loaded_time)
							file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_EARLY;
						else
							file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_DELAYED;
					}

				}
			} else if (file_indicator.equalsIgnoreCase(DatabuckConstants.FILE_INDICATOR_FRQUENCY)) {

				expected_hour = end_hour;

				if (loaded_hour >= start_hour && loaded_hour <= end_hour)
					file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_ON_TIME;
				else if (loaded_hour < start_hour)
					file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_EARLY;
				else if (loaded_hour > end_hour)
					file_arrival_status = DatabuckConstants.FILE_ARRIVAL_STATUS_DELAYED;
			}
		} else {
			file_indicator = DatabuckConstants.FILE_INDICATOR_HOURLY;
			file_arrival_status = DatabuckConstants.FILE_ARRIVAL_NEW_FILE;
		}
		DbkFMFileArrivalDetails dbkFMFileArrivalDetails = new DbkFMFileArrivalDetails();
		dbkFMFileArrivalDetails.setConnection_id(connection_Id);
		dbkFMFileArrivalDetails.setValidation_id(validation_Id);
		dbkFMFileArrivalDetails.setSchema_name(schema_name);
		dbkFMFileArrivalDetails.setTable_or_subfolder_name(table_or_subfolder_name);
		dbkFMFileArrivalDetails.setFile_indicator(file_indicator);
		dbkFMFileArrivalDetails.setDayOfWeek(dayOfWeek);
		dbkFMFileArrivalDetails.setLoad_date(load_dateS);
		dbkFMFileArrivalDetails.setLoaded_hour(loaded_hour);
		dbkFMFileArrivalDetails.setLoaded_time(loaded_time);
		dbkFMFileArrivalDetails.setExpected_hour(expected_hour);
		dbkFMFileArrivalDetails.setExpected_time(expected_time);
		dbkFMFileArrivalDetails.setRecord_count(record_count);
		dbkFMFileArrivalDetails.setRecord_count_check(record_count_check);
		dbkFMFileArrivalDetails.setColumn_metadata_check(column_metadata_check);
		dbkFMFileArrivalDetails.setFile_validity_status(file_validity_status);
		dbkFMFileArrivalDetails.setFile_arrival_status(file_arrival_status);
		dbkFMFileArrivalDetails.setFileName(fileName);


		//save data to dbk_fm_summary_details table
		fileMonitorDao.saveDbkFMFileArrivalDetails(dbkFMFileArrivalDetails);

		if (file_validity_status.equalsIgnoreCase("passed")) {
			Long templateId = fileMonitorDao.getTemplateIdOfConnectionByTableName(connection_Id, table_or_subfolder_name);


			System.out.println("\n====> templateId for " + table_or_subfolder_name + " table is =====> " + templateId);
			if (templateId == null || templateId == 0l) {
				//create a new template for this Table
				System.out.println("\n====> creating new template for " + table_or_subfolder_name + " table");

				Map<String, Object> listDataSchemaDeatils = fileMonitorDao.getListDataSchemaDeatilsForidData(connection_Id);
				
				String schemaType = (String)listDataSchemaDeatils.get("schemaType");
				Long createdBy = (Long)listDataSchemaDeatils.get("createdBy");
				Long projectId = Long.parseLong(listDataSchemaDeatils.get("project_id").toString());
				String createdByUser = (String)listDataSchemaDeatils.get("createdByUser");
				
				System.out.println("connection_Id ==> "+connection_Id+" schemaType => "+schemaType +"  createdBy ==> "+createdBy+" projectId => "+projectId +"  createdByUser ==> "+createdByUser+
						" schemaType  ==> "+schemaType+" table_or_subfolder_name ===> "+table_or_subfolder_name);
				
				
				
				CompletableFuture<Long> createDataTemplate = dataProfilingTemplateService.createDataTemplate(session, connection_Id, schemaType, table_or_subfolder_name,
						table_or_subfolder_name+"_template", "", "", "N", "", "", "N", "",
						null, "", "", "", "", createdBy, "", "", "", "",
						"", "", null, "", "", "N", null,projectId, "N",
						createdByUser, "N", null, "", "",
						"");
				Long idData = 0l;
				try {
					idData = createDataTemplate.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				System.out.println("\n====> template Id  for " + table_or_subfolder_name + " table is ====> " + templateId);
				// Place the templates in Queue
				String uniqueId = "";
				try {
					uniqueId = dataProfilingTemplateService.triggerDataTemplate(idData, schemaType, "Y",
							"Y").get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("\n====> Template Id:[" + idData + "] with uniqueId: " + uniqueId
						+ " is in queue for execution !!");
			} else {
			
					String validation_uniqueId = null;

					Long validationId = fileMonitorDao.getIncrementalDQApplicationsForIdData(templateId);

					if (validationId != null && validationId > 0l) {
						System.out.println("\n====> validationId Id for templateId " + templateId + " is ====> " + validationId);
						System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
						String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, loadToFMhistory.getFile_name());
						System.out.println("\n====> validation_uniqueId in queue is " + validation_uniqueId);
					
					} else {
						System.out.println("\n====>  Incremental validation does not exist");

						ListDataSource listDataSource = fileMonitorDao.getDataSourceForRowAdd(templateId);
						String validationCheckName = listDataSource.getName() + "_validation";
						int projectId = listDataSource.getProjectId();
						int domainId = listDataSource.getDomain();

						List<ListDataDefinition> listDataDefinitions = validationDAO.getListDataDefinitionsByIdData(templateId);

						//check for incremental validation is exist or not ,if not create that one ,if not create bulk load validation
						validationId = createValidationService.createIncrementalValidationCheck(validationCheckName, templateId, listDataSource,
								listDataDefinitions, Integer.valueOf(projectId).toString(), (long)(domainId), false);
						
						

						//if  incremental validation create successfully then put that  in queue
						if (validationId != null && validationId > 0l) {
							System.out.println("\n====> validation id ===> " + validationId);
							if (validationIdStatus) {
								System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
								String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, loadToFMhistory.getFile_name());
								System.out.println("\n====> validation_uniqueId in queue is " + validation_uniqueId);
							}
						} else {
							validationId = fileMonitorDao.getDQApplicationsForIdData(templateId);
							if (validationId != null && validationId > 0l) {
								System.out.println("\n====> validation id ===> " + validationId);
									System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
									String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, loadToFMhistory.getFile_name());
									System.out.println("\n====> validation_uniqueId in queue is " + validation_uniqueId);
								
							} else {
								System.out.println("\n====> creating normal validation for template id ==>  " + templateId);
								validationId = createValidationService.createValidationCheck(validationCheckName, templateId, listDataSource,
										listDataDefinitions, projectId, domainId, false);
								if (validationId != null && validationId > 0l) {
									System.out.println("\n====> validation id ===> " + validationId);
										System.out.println("\n====> Adding validation with validation id " + validationId + " to queue");
										String uniqueId = iTaskDAO.addIncrementalFileValidationToQueue(validationId, loadToFMhistory.getFile_name());
										System.out.println("\n====> validation_uniqueId in queue is " + validation_uniqueId);
									
								}
							}

						}

					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n====> SnowflakeFileMonitoringService - END <====");

}

}
