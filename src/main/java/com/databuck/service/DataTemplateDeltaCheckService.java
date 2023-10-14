package com.databuck.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.databuck.bean.*;
import com.databuck.config.DatabuckEnv;
import com.databuck.dao.*;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.SnowflakeConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.constants.DatabuckConstants;
import com.databuck.econstants.DeltaType;
import com.databuck.econstants.TemplateDeltaStatusTypes;
@Service
public class DataTemplateDeltaCheckService {

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private MSSQLConnection mSSQLConnection;

	@Autowired
	IDataTemplateAddNewDAO dataTemplateAddNewDAO;
	@Autowired
	private ITemplateViewDAO templateviewdao;
	@Autowired
	private SnowflakeConnection snowFlakeConnection;
	@Autowired
	private ITaskDAO iTaskDAO;
	@Autowired
	private IValidationCheckDAO validationCheckDao;

	@Autowired
	private SchemaDAOI schemaDAOI;

	@Autowired
	private RuleCatalogService ruleCatalogService;

	@Autowired
	private RuleCatalogDao ruleCatalogDao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	HiveConnection hiveConnection;

	@Autowired
	public IResultsDAO iResultsDAO;
	
	private static final Logger LOG = Logger.getLogger(DataTemplateDeltaCheckService.class);

	public TemplateDeltaResponse getTemplateDeltaChanges(long idData) {

		TemplateDeltaResponse templateDeltaResponse = new TemplateDeltaResponse();

		try {
			List<ListDataDefinitionDelta> deltaListDataDefinition = new ArrayList<ListDataDefinitionDelta>();
			List<ListDataDefinition> newColumnsList = new ArrayList<ListDataDefinition>();
			List<ListDataDefinition> missingColumnsList = new ArrayList<ListDataDefinition>();
			boolean isMatchColumnsConfigChanged = false;

			// Set the Template Id
			templateDeltaResponse.setIdData(idData);

			// Get the Template details
			ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);

			if (listDataSource != null) {

				// Get Template Create status
				String templateCreateStatus = listDataSource.getTemplateCreateSuccess();
				LOG.debug("\n====> Template Create Success: " + templateCreateStatus);

				// Get the approval status
				String deltaApprovalStatus = listDataSource.getDeltaApprovalStatus();
				LOG.debug("\n====> DeltaApprovalStatus: " + deltaApprovalStatus);

				// Get the listDataDefinition
				List<ListDataDefinition> curListdatadefinition = templateviewdao.view(idData);

				// Get the listDataDefinition from staging
				List<ListDataDefinition> stgListDataDefinition = templateviewdao
						.getListDataDefinitionsInStaging(idData);

				/*
				 * If staging is blank, Check templateCreateStatus is blank or 'N' else if
				 * templateCreateStatus is 'Y' and deltaApprovalStatus is blank or empty or
				 * approved or rejected then insert latest listDataDefintion to staging table
				 */
				if ((templateCreateStatus == null || templateCreateStatus.trim().equalsIgnoreCase("N")
						|| (templateCreateStatus != null && templateCreateStatus.trim().equalsIgnoreCase("Y")
								&& (deltaApprovalStatus == null || deltaApprovalStatus.trim().isEmpty()
										|| deltaApprovalStatus
												.equalsIgnoreCase(TemplateDeltaStatusTypes.approved.toString())
										|| deltaApprovalStatus
												.equalsIgnoreCase(TemplateDeltaStatusTypes.rejected.toString()))))
						&& (stgListDataDefinition == null || stgListDataDefinition.size() == 0)) {

					// Clear the staging
					listdatasourcedao.clearListDataDefinitonStagingForIdData(idData);

					// Copy the listDataDefinition to staging
					listdatasourcedao.copyListDataDefintionsOfTemplateToStaging(idData);

					stgListDataDefinition = templateviewdao.getListDataDefinitionsInStaging(idData);
				}

				if (curListdatadefinition != null && curListdatadefinition.size() > 0 && stgListDataDefinition != null
						&& stgListDataDefinition.size() > 0) {

					for (ListDataDefinition s_ldd : stgListDataDefinition) {
						ListDataDefinitionDelta delta_ldd = new ListDataDefinitionDelta();
						boolean columnFound = false;

						// Find Common columns
						for (ListDataDefinition c_ldd : curListdatadefinition) {
							if (s_ldd.getDisplayName().equals(c_ldd.getDisplayName())) {
								delta_ldd.setStgListDataDefinition(s_ldd);
								delta_ldd.setCurListDataDefinition(c_ldd);
								delta_ldd.setIdData(idData);
								columnFound = true;

								// Check if Column config Data(i.e., checks & thresholds) are changed
								boolean isDataChanged = checkIfColumnConfigDataChanged(s_ldd, c_ldd);
								if (isDataChanged) {
									isMatchColumnsConfigChanged = true;
									delta_ldd.setDeltaType(DeltaType.CHANGED);
								} else {
									delta_ldd.setDeltaType(DeltaType.NOCHANGE);
								}
								break;
							}
						}

						// Find new columns
						if (!columnFound) {
							delta_ldd.setDeltaType(DeltaType.NEW);
							delta_ldd.setStgListDataDefinition(s_ldd);
							delta_ldd.setCurListDataDefinition(null);
							delta_ldd.setIdData(idData);
							// Adding to new columns list
							newColumnsList.add(s_ldd);
						}

						deltaListDataDefinition.add(delta_ldd);
					}

					// Find missing columns
					for (ListDataDefinition c_ldd : curListdatadefinition) {
						ListDataDefinitionDelta delta_ldd = new ListDataDefinitionDelta();
						boolean columnFound = false;

						// Find Column columns
						for (ListDataDefinition s_ldd : stgListDataDefinition) {
							if (s_ldd.getDisplayName().equals(c_ldd.getDisplayName())) {
								columnFound = true;
								break;
							}
						}

						// Find missing columns
						if (!columnFound) {
							delta_ldd.setDeltaType(DeltaType.MISSING);
							long missingColId = c_ldd.getIdColumn();
							c_ldd.setIdColumn(0l);
							delta_ldd.setStgListDataDefinition(c_ldd);
							delta_ldd.setCurListDataDefinition(null);
							deltaListDataDefinition.add(delta_ldd);
							delta_ldd.setIdData(idData);
							delta_ldd.setMissingColId(missingColId);
							// Adding to missing columns list
							missingColumnsList.add(c_ldd);
						}

					}
				}

				// Enable checks
				String columnsAdded = (newColumnsList != null && newColumnsList.size() > 0) ? "Y" : "N";
				String columnsDeleted = (missingColumnsList != null && missingColumnsList.size() > 0) ? "Y" : "N";
				String matchColumnsConfigChanged = isMatchColumnsConfigChanged ? "Y" : "N";

				boolean isMicrosegmentsChanged = checkIfMicrosegmentsChanged(deltaListDataDefinition);
				String microsegmentsChanged = isMicrosegmentsChanged ? "Y" : "N";

				LOG.info("\n====> Delta Changes Report <====");
				LOG.debug("isMicrosegmentsChanged: " + microsegmentsChanged);
				LOG.debug("isColConfigChanged: " + matchColumnsConfigChanged);
				LOG.debug("isColumnsAdded: " + columnsAdded);
				LOG.debug("isColumnsDeleted: " + columnsDeleted);

				if (templateCreateStatus != null && templateCreateStatus.trim().equalsIgnoreCase("Y")) {

					if ((deltaApprovalStatus == null || deltaApprovalStatus.trim().isEmpty()
							|| deltaApprovalStatus.equalsIgnoreCase(TemplateDeltaStatusTypes.approved.toString()))
							&& (microsegmentsChanged.equalsIgnoreCase("Y")
									|| matchColumnsConfigChanged.equalsIgnoreCase("Y")
									|| columnsAdded.equalsIgnoreCase("Y") || columnsDeleted.equalsIgnoreCase("Y"))) {

						// Update Status to reviewpending
						deltaApprovalStatus = TemplateDeltaStatusTypes.reviewpending.toString();
						listdatasourcedao.updateTemplateDeltaApprovalStatus(idData, deltaApprovalStatus);

					} else if ((deltaApprovalStatus == null || (deltaApprovalStatus != null
							&& !deltaApprovalStatus.equalsIgnoreCase(TemplateDeltaStatusTypes.approved.toString())
							&& !deltaApprovalStatus.equalsIgnoreCase(TemplateDeltaStatusTypes.rejected.toString())))
							&& microsegmentsChanged.equalsIgnoreCase("N")
							&& matchColumnsConfigChanged.equalsIgnoreCase("N") && columnsAdded.equalsIgnoreCase("N")
							&& columnsDeleted.equalsIgnoreCase("N")) {

						// Update Status to approved
						deltaApprovalStatus = TemplateDeltaStatusTypes.approved.toString();
						listdatasourcedao.updateTemplateDeltaApprovalStatus(idData, deltaApprovalStatus);
					}
				}

				// Set all the data to final response object
				templateDeltaResponse.setIdData(idData);
				templateDeltaResponse.setIdDataSchema(listDataSource.getIdDataSchema());
				templateDeltaResponse.setDeltaApprovalStatus(deltaApprovalStatus);
				templateDeltaResponse.setMicrosegmentsChanged(microsegmentsChanged);
				templateDeltaResponse.setColumnsAdded(columnsAdded);
				templateDeltaResponse.setColumnsDeleted(columnsDeleted);
				templateDeltaResponse.setMatchColumnsConfigChanged(matchColumnsConfigChanged);
				templateDeltaResponse.setNewColumnsList(newColumnsList);
				templateDeltaResponse.setMissingColumnsList(missingColumnsList);
				templateDeltaResponse.setDeltaListDataDefinition(deltaListDataDefinition);

			} else {
				LOG.error("\n====> getTemplateDeltaChanges : Failed to get template data/Invalid Id");
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return templateDeltaResponse;
	}

	private boolean checkIfMicrosegmentsChanged(List<ListDataDefinitionDelta> listdatadefinitionDelta_list) {
		boolean isMicrosegmentsChanged = false;
		try {
			if (listdatadefinitionDelta_list != null && listdatadefinitionDelta_list.size() > 0) {
				List<String> currentMicrosegments = new ArrayList<String>();
				List<String> stagingMicrosegments = new ArrayList<String>();

				for (ListDataDefinitionDelta lddDelta : listdatadefinitionDelta_list) {
					if (lddDelta.getCurListDataDefinition() != null
							&& lddDelta.getCurListDataDefinition().getDgroup() != null
							&& lddDelta.getCurListDataDefinition().getDgroup().equalsIgnoreCase("Y")) {
						currentMicrosegments.add(lddDelta.getCurListDataDefinition().getDisplayName());
					}

					if (lddDelta.getStgListDataDefinition() != null
							&& lddDelta.getStgListDataDefinition().getDgroup() != null
							&& lddDelta.getStgListDataDefinition().getDgroup().equalsIgnoreCase("Y")) {
						stagingMicrosegments.add(lddDelta.getStgListDataDefinition().getDisplayName());
					}
				}

				if (currentMicrosegments.size() != stagingMicrosegments.size()) {
					isMicrosegmentsChanged = true;
				} else {
					for (String microseg : currentMicrosegments) {
						if (!stagingMicrosegments.contains(microseg)) {
							isMicrosegmentsChanged = true;
							break;
						}
					}
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return isMicrosegmentsChanged;
	}

	private boolean checkIfColumnConfigDataChanged(ListDataDefinition staging_ldd, ListDataDefinition current_ldd) {
		boolean isMatchColumnsConfigChanged = false;
		try {
			if (staging_ldd != null && current_ldd != null) {

				if (((staging_ldd.getFormat() == null && current_ldd.getFormat() != null)
						|| (staging_ldd.getFormat() != null
								&& !staging_ldd.getFormat().equals(current_ldd.getFormat())))
						|| ((staging_ldd.getPrimaryKey() == null && current_ldd.getPrimaryKey() != null)
								|| (staging_ldd.getPrimaryKey() != null
										&& !staging_ldd.getPrimaryKey().equals(current_ldd.getPrimaryKey())))
						|| ((staging_ldd.getDgroup() == null && current_ldd.getDgroup() != null)
								|| (staging_ldd.getDgroup() != null
										&& !staging_ldd.getDgroup().equals(current_ldd.getDgroup())))
						|| ((staging_ldd.getIncrementalCol() == null && current_ldd.getIncrementalCol() != null)
								|| (staging_ldd.getIncrementalCol() != null
										&& !staging_ldd.getIncrementalCol().equals(current_ldd.getIncrementalCol())))
						|| ((staging_ldd.getIsMasked() == null && current_ldd.getIsMasked() != null)
								|| (staging_ldd.getIsMasked() != null
										&& !staging_ldd.getIsMasked().equals(current_ldd.getIsMasked())))
						|| ((staging_ldd.getPartitionBy() == null && current_ldd.getPartitionBy() != null)
								|| (staging_ldd.getPartitionBy() != null
										&& !staging_ldd.getPartitionBy().equals(current_ldd.getPartitionBy())))
						|| ((staging_ldd.getNonNull() == null && current_ldd.getNonNull() != null)
								|| (staging_ldd.getNonNull() != null
										&& !staging_ldd.getNonNull().equals(current_ldd.getNonNull())))
						|| ((staging_ldd.getDupkey() == null && current_ldd.getDupkey() != null)
								|| (staging_ldd.getDupkey() != null
										&& !staging_ldd.getDupkey().equals(current_ldd.getDupkey())))
						|| ((staging_ldd.getNumericalStat() == null && current_ldd.getNumericalStat() != null)
								|| (staging_ldd.getNumericalStat() != null
										&& !staging_ldd.getNumericalStat().equals(current_ldd.getNumericalStat())))
						|| ((staging_ldd.getStringStat() == null && current_ldd.getStringStat() != null)
								|| (staging_ldd.getStringStat() != null
										&& !staging_ldd.getStringStat().equals(current_ldd.getStringStat())))
						|| ((staging_ldd.getDataDrift() == null && current_ldd.getDataDrift() != null)
								|| (staging_ldd.getDataDrift() != null
										&& !staging_ldd.getDataDrift().equals(current_ldd.getDataDrift())))
						|| ((staging_ldd.getStartDate() == null && current_ldd.getStartDate() != null)
								|| (staging_ldd.getStartDate() != null
										&& !staging_ldd.getStartDate().equals(current_ldd.getStartDate())))
						|| ((staging_ldd.getEndDate() == null && current_ldd.getEndDate() != null)
								|| (staging_ldd.getEndDate() != null
										&& !staging_ldd.getEndDate().equals(current_ldd.getEndDate())))
						|| ((staging_ldd.getTimelinessKey() == null && current_ldd.getTimelinessKey() != null)
								|| (staging_ldd.getTimelinessKey() != null
										&& !staging_ldd.getTimelinessKey().equals(current_ldd.getTimelinessKey())))
						|| ((staging_ldd.getRecordAnomaly() == null && current_ldd.getRecordAnomaly() != null)
								|| (staging_ldd.getRecordAnomaly() != null
										&& !staging_ldd.getRecordAnomaly().equals(current_ldd.getRecordAnomaly())))
						|| ((staging_ldd.getDefaultCheck() == null && current_ldd.getDefaultCheck() != null)
								|| (staging_ldd.getDefaultCheck() != null
										&& !staging_ldd.getDefaultCheck().equals(current_ldd.getDefaultCheck())))
						|| ((staging_ldd.getDefaultValues() == null && current_ldd.getDefaultValues() != null)
								|| (staging_ldd.getDefaultValues() != null
										&& !staging_ldd.getDefaultValues().equals(current_ldd.getDefaultValues())))
						|| ((staging_ldd.getDateRule() == null && current_ldd.getDateRule() != null)
								|| (staging_ldd.getDateRule() != null
										&& !staging_ldd.getDateRule().equals(current_ldd.getDateRule())))
						|| ((staging_ldd.getDateFormat() == null && current_ldd.getDateFormat() != null)
								|| (staging_ldd.getDateFormat() != null
										&& !staging_ldd.getDateFormat().trim().equals(current_ldd.getDateFormat())))
						|| ((staging_ldd.getPatternCheck() == null && current_ldd.getPatternCheck() != null)
								|| (staging_ldd.getPatternCheck() != null
										&& !staging_ldd.getPatternCheck().equals(current_ldd.getPatternCheck())))
						|| ((staging_ldd.getPatterns() == null && current_ldd.getPatterns() != null)
								|| (staging_ldd.getPatterns() != null
										&& !staging_ldd.getPatterns().trim().equals(current_ldd.getPatterns())))
						|| ((staging_ldd.getDefaultPatternCheck() == null && current_ldd.getDefaultPatternCheck() != null)
								|| (staging_ldd.getDefaultPatternCheck() != null
										&& !staging_ldd.getDefaultPatternCheck().equals(current_ldd.getDefaultPatternCheck())))
						|| ((staging_ldd.getBadData() == null && current_ldd.getBadData() != null)
								|| (staging_ldd.getBadData() != null
										&& !staging_ldd.getBadData().equals(current_ldd.getBadData())))
						|| ((staging_ldd.getLengthCheck() == null && current_ldd.getLengthCheck() != null)
								|| (staging_ldd.getLengthCheck() != null
										&& !staging_ldd.getLengthCheck().equals(current_ldd.getLengthCheck())))
						|| ((staging_ldd.getMaxLengthCheck() == null && current_ldd.getMaxLengthCheck() != null)
								|| (staging_ldd.getMaxLengthCheck() != null
										&& !staging_ldd.getMaxLengthCheck().equals(current_ldd.getMaxLengthCheck())))
						|| ((staging_ldd.getLengthValue() == null && current_ldd.getLengthValue() != null)
								|| (staging_ldd.getLengthValue() != null
										&& !staging_ldd.getLengthValue().equals(current_ldd.getLengthValue())))
						|| ((staging_ldd.getNullCountThreshold() == null && current_ldd.getNullCountThreshold() != null)
								|| (staging_ldd.getNullCountThreshold() != null && !staging_ldd.getNullCountThreshold()
										.equals(current_ldd.getNullCountThreshold())))
						|| ((staging_ldd.getNumericalThreshold() == null && current_ldd.getNumericalThreshold() != null)
								|| (staging_ldd.getNumericalThreshold() != null && !staging_ldd.getNumericalThreshold()
										.equals(current_ldd.getNumericalThreshold())))
						|| ((staging_ldd.getStringStatThreshold() == null
								&& current_ldd.getStringStatThreshold() != null)
								|| (staging_ldd.getStringStatThreshold() != null && !staging_ldd
										.getStringStatThreshold().equals(current_ldd.getStringStatThreshold())))
						|| ((staging_ldd.getDataDriftThreshold() == null && current_ldd.getDataDriftThreshold() != null)
								|| (staging_ldd.getDataDriftThreshold() != null && !staging_ldd.getDataDriftThreshold()
										.equals(current_ldd.getDataDriftThreshold())))
						|| ((staging_ldd.getRecordAnomalyThreshold() == null
								&& current_ldd.getRecordAnomalyThreshold() != null)
								|| (staging_ldd.getRecordAnomalyThreshold() != null && !staging_ldd
										.getRecordAnomalyThreshold().equals(current_ldd.getRecordAnomalyThreshold())))
						|| ((staging_ldd.getLengthThreshold() == null && current_ldd.getLengthThreshold() != null)
								|| (staging_ldd.getLengthThreshold() != null
										&& !staging_ldd.getLengthThreshold().equals(current_ldd.getLengthThreshold())))
						|| ((staging_ldd.getBadDataThreshold() == null && current_ldd.getBadDataThreshold() != null)
								|| (staging_ldd.getBadDataThreshold() != null && !staging_ldd.getBadDataThreshold()
										.equals(current_ldd.getBadDataThreshold())))
						|| ((staging_ldd.getPatternCheckThreshold() == null
								&& current_ldd.getPatternCheckThreshold() != null)
								|| (staging_ldd.getPatternCheckThreshold() != null && !staging_ldd
										.getPatternCheckThreshold().equals(current_ldd.getPatternCheckThreshold())))
						|| ((staging_ldd.getMeasurement() == null && current_ldd.getMeasurement() != null)
								|| (staging_ldd.getMeasurement() != null
										&& !staging_ldd.getMeasurement().equals(current_ldd.getMeasurement())))) {
					isMatchColumnsConfigChanged = true;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			isMatchColumnsConfigChanged = true;
		}
		return isMatchColumnsConfigChanged;
	}

	public boolean rejectMicrosegments(List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		boolean status = false;
		try {
			if (listDataDefinitionDelta != null && listDataDefinitionDelta.size() > 0) {
				long misMatchesCount = 0;
				long updatedCount = 0;
				for (ListDataDefinitionDelta lddDelta : listDataDefinitionDelta) {
					ListDataDefinition s_ldd = lddDelta.getStgListDataDefinition();
					ListDataDefinition c_ldd = lddDelta.getCurListDataDefinition();

					if (s_ldd != null && c_ldd != null) {

						long stg_idColumn = s_ldd.getIdColumn();
						String check_columnName = "dgroup";
						String curDGroupCheck = c_ldd.getDgroup();
						String stgDGroupCheck = s_ldd.getDgroup();

						boolean microSegmentMisMatch = false;
						String finalColumnValue = "";

						if (curDGroupCheck == null && stgDGroupCheck != null) {
							microSegmentMisMatch = true;

							// Disable the Dgroup check
							finalColumnValue = "N";

						} else if (curDGroupCheck.equalsIgnoreCase("N")
								&& (stgDGroupCheck == null || stgDGroupCheck.equalsIgnoreCase("Y"))) {
							microSegmentMisMatch = true;

							// Disable the Dgroup check
							finalColumnValue = "N";

						} else if (curDGroupCheck.equalsIgnoreCase("Y")
								&& (stgDGroupCheck == null || stgDGroupCheck.equalsIgnoreCase("N"))) {
							microSegmentMisMatch = true;

							// Enable the Dgroup check
							finalColumnValue = "Y";
						}

						if (microSegmentMisMatch) {
							++misMatchesCount;
							LOG.info("mismatch found");
							LOG.debug("stg_idColumn:" + stg_idColumn);
							long count = schemaDAOI.updateDataIntoStagingListDataDefinition(stg_idColumn,
									check_columnName, finalColumnValue);
							updatedCount += count;
						}

					}
				}

				if (misMatchesCount == updatedCount) {
					status = true;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public ApproveMicroSegmentsDTO approveMicrosegments(long idData, String createdByUser) {
		ApproveMicroSegmentsDTO approveMicroSegmentsDTO = new ApproveMicroSegmentsDTO();
		String newTemplateName = "";
		String newValidationName = "";
		Long newIdData = null;
		Long newIdApp = null;
		boolean templateCreationStatus = false;
		boolean validationCreationStatus = false;
		try {
			LOG.info("\n====> Fetching the oldTemplate data ...");
			ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);

			if (listDataSource != null) {
				newTemplateName = listDataSource.getName();
				newValidationName = listDataSource.getName() + "_Validation";

				// Create a copy of template
				LOG.info("\n====> Create a copy of template ...");
				newIdData = templateviewdao.copyTemplate(idData, newTemplateName, createdByUser);

				// Check if it is derived template
				if(listDataSource.getDataLocation().equalsIgnoreCase("Derived")) {
					// Copy the listDerivedDataSource
					templateviewdao.copyDerivedTemplate(idData, newIdData, createdByUser, newTemplateName);
				}
				// Copy the listDataDefinitions from staging to actual for new template
				listdatasourcedao.insertListDataDefintionsFromStagingForIdData(newIdData, idData);

				boolean isMicrosegmentEnabled = false;
				List<ListDataDefinition> listDataDefinitionList = templateviewdao.view(newIdData);
				for (ListDataDefinition ldd : listDataDefinitionList) {
					if (ldd.getDgroup().equalsIgnoreCase("Y")) {
						isMicrosegmentEnabled = true;
						break;
					}
				}

				// Clear the staging old template
				listdatasourcedao.clearListDataDefinitonStagingForIdData(idData);

				if (newIdData != null && newIdData != 0l) {
					LOG.debug("New TemplateId: " + newIdData);
					LOG.debug("New TemplateName: " + newTemplateName);

					templateCreationStatus = true;
					approveMicroSegmentsDTO.setTemplateCreationStatus(templateCreationStatus);
					approveMicroSegmentsDTO.setTemplateId(newIdData);
					approveMicroSegmentsDTO.setTemplateName(newTemplateName);

					// Update status to Approved for new template
					LOG.info("\n====> Update status to Approved for new template");
					listdatasourcedao.updateTemplateDeltaApprovalStatus(newIdData,
							TemplateDeltaStatusTypes.approved.toString());

					// Update status to Rejected for old template
					LOG.info("\n====> Update status to Rejected for old template");
					listdatasourcedao.updateTemplateDeltaApprovalStatus(idData,
							TemplateDeltaStatusTypes.rejected.toString());

					// Get latest validation associated with the template
					Long idApp = 0l;
					if(isMicrosegmentEnabled){
						idApp = validationCheckDao.getListApplicationsFromIdData(idData);
					} else {
						idApp = validationCheckDao.getMaxValidationByIdData(idData);
					}

					if (idApp != null && idApp != 0l) {
						LOG.debug("\n====> Latest validation associated with the template: " + idApp);

						// Create a copy of latest validation associated with the template
						newIdApp = validationCheckDao.copyValidation(idApp, newValidationName, createdByUser);

						if (newIdApp != null && newIdApp != 0l) {
							// Update the idData to newIdData
							boolean templateUpdateStatus = validationCheckDao.updateTemplateIdOfValidation(newIdApp,
									newIdData);
							if (templateUpdateStatus) {
								newValidationName = newIdApp + "_" + newValidationName;
								LOG.debug("New ValidationId: " + newIdApp);
								LOG.debug("New ValidationName: " + newValidationName);

								validationCreationStatus = true;
								approveMicroSegmentsDTO.setValidationId(newIdApp);
								approveMicroSegmentsDTO.setValidationName(newValidationName);
								approveMicroSegmentsDTO.setValidationCreationStatus(validationCreationStatus);

								// If RuleCatalog is enabled, create and update the RuleCatalog for the
								// validation
								boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

								if (isRuleCatalogEnabled) {
									ruleCatalogDao.copyRuleCatalog(idApp, newIdApp);
									ruleCatalogDao.copyRuleTagMapping(idApp, newIdApp);
								}
							}

						} else {
							LOG.error("\n====> Failed to create a copy of validation ...");
						}
					} else {
						LOG.error("\n====> Failed to get validation associated with the template ...");
					}

					// deactivate the template and deactivate the validations associated with that
					// template
					LOG.info("\n====> Deactivation of template and its associated validations ...");
					boolean deactivateStatus = listdatasourcedao.deactivateDataSource(idData);
					approveMicroSegmentsDTO.setTemplateDeactivateStatus(deactivateStatus);
				}
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return approveMicroSegmentsDTO;
	}

	public boolean approveColumnAnalysisChanges(List<ListDataDefinitionDelta> listDataDefinitionDelta) {
		boolean status = false;
		try {
			if (listDataDefinitionDelta != null && listDataDefinitionDelta.size() > 0) {
				long misMatchesCount = 0;
				long updatedCount = 0;

				for (ListDataDefinitionDelta lddDelta : listDataDefinitionDelta) {
					ListDataDefinition current_ldd = lddDelta.getCurListDataDefinition();
					ListDataDefinition staging_ldd = lddDelta.getStgListDataDefinition();

					if (current_ldd != null && staging_ldd != null
							&& lddDelta.getDeltaType().equals(DeltaType.CHANGED)) {

						long cur_idColumn = current_ldd.getIdColumn();
						String check_columnName = "";
						String finalColumnValue = "";

						if ((staging_ldd.getFormat() == null && current_ldd.getFormat() != null)
								|| (staging_ldd.getFormat() != null
										&& !staging_ldd.getFormat().equals(current_ldd.getFormat()))) {
							check_columnName = "format";
							finalColumnValue = (staging_ldd.getFormat() != null
									&& !staging_ldd.getFormat().equalsIgnoreCase("null")) ? staging_ldd.getFormat()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDgroup() == null && current_ldd.getDgroup() != null)
								|| (staging_ldd.getDgroup() != null
										&& !staging_ldd.getDgroup().equals(current_ldd.getDgroup()))) {
							check_columnName = "dgroup";
							finalColumnValue = (staging_ldd.getDgroup() != null
									&& !staging_ldd.getDgroup().equalsIgnoreCase("null")) ? staging_ldd.getDgroup()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getPrimaryKey() == null && current_ldd.getPrimaryKey() != null)
								|| (staging_ldd.getPrimaryKey() != null
										&& !staging_ldd.getPrimaryKey().equals(current_ldd.getPrimaryKey()))) {
							check_columnName = "primaryKey";
							finalColumnValue = (staging_ldd.getPrimaryKey() != null
									&& !staging_ldd.getPrimaryKey().equalsIgnoreCase("null"))
											? staging_ldd.getPrimaryKey()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getIncrementalCol() == null && current_ldd.getIncrementalCol() != null)
								|| (staging_ldd.getIncrementalCol() != null
										&& !staging_ldd.getIncrementalCol().equals(current_ldd.getIncrementalCol()))) {
							check_columnName = "incrementalCol";
							finalColumnValue = (staging_ldd.getIncrementalCol() != null
									&& !staging_ldd.getIncrementalCol().equalsIgnoreCase("null"))
											? staging_ldd.getIncrementalCol()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getIsMasked() == null && current_ldd.getIsMasked() != null)
								|| (staging_ldd.getIsMasked() != null
										&& !staging_ldd.getIsMasked().equals(current_ldd.getIsMasked()))) {
							check_columnName = "isMasked";
							finalColumnValue = (staging_ldd.getIsMasked() != null
									&& !staging_ldd.getIsMasked().equalsIgnoreCase("null")) ? staging_ldd.getIsMasked()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getPartitionBy() == null && current_ldd.getPartitionBy() != null)
								|| (staging_ldd.getPartitionBy() != null
										&& !staging_ldd.getPartitionBy().equals(current_ldd.getPartitionBy()))) {
							check_columnName = "partitionBy";
							finalColumnValue = (staging_ldd.getPartitionBy() != null
									&& !staging_ldd.getPartitionBy().equalsIgnoreCase("null"))
											? staging_ldd.getPartitionBy()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getNonNull() == null && current_ldd.getNonNull() != null)
								|| (staging_ldd.getNonNull() != null
										&& !staging_ldd.getNonNull().equals(current_ldd.getNonNull()))) {
							check_columnName = "nonNull";
							finalColumnValue = (staging_ldd.getNonNull() != null
									&& !staging_ldd.getNonNull().equalsIgnoreCase("null")) ? staging_ldd.getNonNull()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDupkey() == null && current_ldd.getDupkey() != null)
								|| (staging_ldd.getDupkey() != null
										&& !staging_ldd.getDupkey().equals(current_ldd.getDupkey()))) {
							check_columnName = "dupkey";
							finalColumnValue = (staging_ldd.getDupkey() != null
									&& !staging_ldd.getDupkey().equalsIgnoreCase("null")) ? staging_ldd.getDupkey()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getNumericalStat() == null && current_ldd.getNumericalStat() != null)
								|| (staging_ldd.getNumericalStat() != null
										&& !staging_ldd.getNumericalStat().equals(current_ldd.getNumericalStat()))) {
							check_columnName = "numericalStat";
							finalColumnValue = (staging_ldd.getNumericalStat() != null
									&& !staging_ldd.getNumericalStat().equalsIgnoreCase("null"))
											? staging_ldd.getNumericalStat()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getStringStat() == null && current_ldd.getStringStat() != null)
								|| (staging_ldd.getStringStat() != null
										&& !staging_ldd.getStringStat().equals(current_ldd.getStringStat()))) {
							check_columnName = "stringStat";
							finalColumnValue = (staging_ldd.getStringStat() != null
									&& !staging_ldd.getStringStat().equalsIgnoreCase("null"))
											? staging_ldd.getStringStat()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDataDrift() == null && current_ldd.getDataDrift() != null)
								|| (staging_ldd.getDataDrift() != null
										&& !staging_ldd.getDataDrift().equals(current_ldd.getDataDrift()))) {
							check_columnName = "dataDrift";
							finalColumnValue = (staging_ldd.getDataDrift() != null
									&& !staging_ldd.getDataDrift().equalsIgnoreCase("null"))
											? staging_ldd.getDataDrift()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getStartDate() == null && current_ldd.getStartDate() != null)
								|| (staging_ldd.getStartDate() != null
										&& !staging_ldd.getStartDate().equals(current_ldd.getStartDate()))) {
							check_columnName = "startDate";
							finalColumnValue = (staging_ldd.getStartDate() != null
									&& !staging_ldd.getStartDate().equalsIgnoreCase("null"))
											? staging_ldd.getStartDate()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getEndDate() == null && current_ldd.getEndDate() != null)
								|| (staging_ldd.getEndDate() != null
										&& !staging_ldd.getEndDate().equals(current_ldd.getEndDate()))) {
							check_columnName = "endDate";
							finalColumnValue = (staging_ldd.getEndDate() != null
									&& !staging_ldd.getEndDate().equalsIgnoreCase("null")) ? staging_ldd.getEndDate()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getTimelinessKey() == null && current_ldd.getTimelinessKey() != null)
								|| (staging_ldd.getTimelinessKey() != null
										&& !staging_ldd.getTimelinessKey().equals(current_ldd.getTimelinessKey()))) {
							check_columnName = "timelinessKey";
							finalColumnValue = (staging_ldd.getTimelinessKey() != null
									&& !staging_ldd.getTimelinessKey().equalsIgnoreCase("null"))
											? staging_ldd.getTimelinessKey()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getRecordAnomaly() == null && current_ldd.getRecordAnomaly() != null)
								|| (staging_ldd.getRecordAnomaly() != null
										&& !staging_ldd.getRecordAnomaly().equals(current_ldd.getRecordAnomaly()))) {
							check_columnName = "recordAnomaly";
							finalColumnValue = (staging_ldd.getRecordAnomaly() != null
									&& !staging_ldd.getRecordAnomaly().equalsIgnoreCase("null"))
											? staging_ldd.getRecordAnomaly()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDefaultCheck() == null && current_ldd.getDefaultCheck() != null)
								|| (staging_ldd.getDefaultCheck() != null
										&& !staging_ldd.getDefaultCheck().equals(current_ldd.getDefaultCheck()))) {
							check_columnName = "defaultCheck";
							finalColumnValue = (staging_ldd.getDefaultCheck() != null
									&& !staging_ldd.getDefaultCheck().equalsIgnoreCase("null"))
											? staging_ldd.getDefaultCheck()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDefaultValues() == null && current_ldd.getDefaultValues() != null)
								|| (staging_ldd.getDefaultValues() != null
										&& !staging_ldd.getDefaultValues().equals(current_ldd.getDefaultValues()))) {
							check_columnName = "defaultValues";
							finalColumnValue = (staging_ldd.getDefaultValues() != null
									&& !staging_ldd.getDefaultValues().equalsIgnoreCase("null"))
											? staging_ldd.getDefaultValues()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDateRule() == null && current_ldd.getDateRule() != null)
								|| (staging_ldd.getDateRule() != null
										&& !staging_ldd.getDateRule().equals(current_ldd.getDateRule()))) {
							check_columnName = "dateRule";
							finalColumnValue = (staging_ldd.getDateRule() != null
									&& !staging_ldd.getDateRule().equalsIgnoreCase("null")) ? staging_ldd.getDateRule()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDateFormat() == null && current_ldd.getDateFormat() != null)
								|| (staging_ldd.getDateFormat() != null
										&& !staging_ldd.getDateFormat().equals(current_ldd.getDateFormat()))) {
							check_columnName = "dateFormat";
							finalColumnValue = (staging_ldd.getDateFormat() != null
									&& !staging_ldd.getDateFormat().equalsIgnoreCase("null"))
											? staging_ldd.getDateFormat()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getPatternCheck() == null && current_ldd.getPatternCheck() != null)
								|| (staging_ldd.getPatternCheck() != null
										&& !staging_ldd.getPatternCheck().equals(current_ldd.getPatternCheck()))) {
							check_columnName = "patternCheck";
							finalColumnValue = (staging_ldd.getPatternCheck() != null
									&& !staging_ldd.getPatternCheck().equalsIgnoreCase("null"))
											? staging_ldd.getPatternCheck()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getPatterns() == null && current_ldd.getPatterns() != null)
								|| (staging_ldd.getPatterns() != null
										&& !staging_ldd.getPatterns().equals(current_ldd.getPatterns()))) {
							check_columnName = "patterns";
							finalColumnValue = (staging_ldd.getPatterns() != null
									&& !staging_ldd.getPatterns().equalsIgnoreCase("null")) ? staging_ldd.getPatterns()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}
						
						if ((staging_ldd.getDefaultPatternCheck() == null && current_ldd.getDefaultPatternCheck() != null)
								|| (staging_ldd.getDefaultPatternCheck() != null
										&& !staging_ldd.getDefaultPatternCheck().equals(current_ldd.getDefaultPatternCheck()))) {
							check_columnName = "defaultPatternCheck";
							finalColumnValue = (staging_ldd.getDefaultPatternCheck() != null
									&& !staging_ldd.getDefaultPatternCheck().equalsIgnoreCase("null"))
											? staging_ldd.getDefaultPatternCheck()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getBadData() == null && current_ldd.getBadData() != null)
								|| (staging_ldd.getBadData() != null
										&& !staging_ldd.getBadData().equals(current_ldd.getBadData()))) {
							check_columnName = "badData";
							finalColumnValue = (staging_ldd.getBadData() != null
									&& !staging_ldd.getBadData().equalsIgnoreCase("null")) ? staging_ldd.getBadData()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getLengthCheck() == null && current_ldd.getLengthCheck() != null)
								|| (staging_ldd.getLengthCheck() != null
										&& !staging_ldd.getLengthCheck().equals(current_ldd.getLengthCheck()))) {
							check_columnName = "lengthCheck";
							finalColumnValue = (staging_ldd.getLengthCheck() != null
									&& !staging_ldd.getLengthCheck().equalsIgnoreCase("null"))
											? staging_ldd.getLengthCheck()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}
						
						if ((staging_ldd.getMaxLengthCheck() == null && current_ldd.getMaxLengthCheck() != null)
								|| (staging_ldd.getMaxLengthCheck() != null
										&& !staging_ldd.getMaxLengthCheck().equals(current_ldd.getMaxLengthCheck()))) {
							check_columnName = "maxLengthCheck";
							finalColumnValue = (staging_ldd.getMaxLengthCheck() != null
									&& !staging_ldd.getMaxLengthCheck().equalsIgnoreCase("null"))
											? staging_ldd.getMaxLengthCheck()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getLengthValue() == null && current_ldd.getLengthValue() != null)
								|| (staging_ldd.getLengthValue() != null
										&& !staging_ldd.getLengthValue().equals(current_ldd.getLengthValue()))) {
							check_columnName = "lengthValue";
							finalColumnValue = (staging_ldd.getLengthValue() != null
									&& !staging_ldd.getLengthValue().equalsIgnoreCase("null"))
											? staging_ldd.getLengthValue()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getNullCountThreshold() == null && current_ldd.getNullCountThreshold() != null)
								|| (staging_ldd.getNullCountThreshold() != null && staging_ldd
										.getNullCountThreshold() != current_ldd.getNullCountThreshold())) {
							check_columnName = "nullCountThreshold";
							finalColumnValue = staging_ldd.getNullCountThreshold() != null
									? staging_ldd.getNullCountThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getNumericalThreshold() == null && current_ldd.getNumericalThreshold() != null)
								|| (staging_ldd.getNumericalThreshold() != null && staging_ldd
										.getNumericalThreshold() != current_ldd.getNumericalThreshold())) {
							check_columnName = "numericalThreshold";
							finalColumnValue = staging_ldd.getNumericalThreshold() != null
									? staging_ldd.getNumericalThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getStringStatThreshold() == null
								&& current_ldd.getStringStatThreshold() != null)
								|| (staging_ldd.getStringStatThreshold() != null && staging_ldd
										.getStringStatThreshold() != current_ldd.getStringStatThreshold())) {
							check_columnName = "stringStatThreshold";
							finalColumnValue = staging_ldd.getStringStatThreshold() != null
									? staging_ldd.getStringStatThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getDataDriftThreshold() == null && current_ldd.getDataDriftThreshold() != null)
								|| (staging_ldd.getDataDriftThreshold() != null && staging_ldd
										.getDataDriftThreshold() != current_ldd.getDataDriftThreshold())) {
							check_columnName = "dataDriftThreshold";
							finalColumnValue = staging_ldd.getDataDriftThreshold() != null
									? staging_ldd.getDataDriftThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getRecordAnomalyThreshold() == null
								&& current_ldd.getRecordAnomalyThreshold() != null)
								|| (staging_ldd.getRecordAnomalyThreshold() != null && staging_ldd
										.getRecordAnomalyThreshold() != current_ldd.getRecordAnomalyThreshold())) {
							check_columnName = "recordAnomalyThreshold";
							finalColumnValue = staging_ldd.getRecordAnomalyThreshold() != null
									? staging_ldd.getRecordAnomalyThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getLengthThreshold() == null && current_ldd.getLengthThreshold() != null)
								|| (staging_ldd.getLengthThreshold() != null
										&& staging_ldd.getLengthThreshold() != current_ldd.getLengthThreshold())) {
							check_columnName = "lengthCheckThreshold";
							finalColumnValue = staging_ldd.getLengthThreshold() != null
									? staging_ldd.getLengthThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getBadDataThreshold() == null && current_ldd.getBadDataThreshold() != null)
								|| (staging_ldd.getBadDataThreshold() != null
										&& staging_ldd.getBadDataThreshold() != current_ldd.getBadDataThreshold())) {
							check_columnName = "badDataCheckThreshold";
							finalColumnValue = staging_ldd.getBadDataThreshold() != null
									? staging_ldd.getBadDataThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

						if ((staging_ldd.getPatternCheckThreshold() == null
								&& current_ldd.getPatternCheckThreshold() != null)
								|| (staging_ldd.getPatternCheckThreshold() != null && staging_ldd
										.getPatternCheckThreshold() != current_ldd.getPatternCheckThreshold())) {
							check_columnName = "patternCheckThreshold";
							finalColumnValue = staging_ldd.getPatternCheckThreshold() != null
									? staging_ldd.getPatternCheckThreshold().toString()
									: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}
						if ((staging_ldd.getMeasurement() == null && current_ldd.getMeasurement() != null)
								|| (staging_ldd.getMeasurement() != null
										&& !staging_ldd.getMeasurement().equals(current_ldd.getMeasurement()))) {
							check_columnName = "measurement";
							finalColumnValue = (staging_ldd.getMeasurement() != null
									&& !staging_ldd.getMeasurement().equalsIgnoreCase("null"))
											? staging_ldd.getMeasurement()
											: null;
							long count = schemaDAOI.updateDataIntoListDataDefinition(cur_idColumn, check_columnName,
									finalColumnValue);
							updatedCount += count;
							++misMatchesCount;
						}

					} else if (lddDelta.getDeltaType().equals(DeltaType.MISSING)) {
						++misMatchesCount;

						if (lddDelta.getMissingColId() != 0l) {
							// Delete the column from listDataDefinition
							long count = listdatasourcedao
									.deleteListDataDefinitionByIdColumn(lddDelta.getMissingColId());
							updatedCount += count;
						}

					} else if (lddDelta.getDeltaType().equals(DeltaType.NEW)) {
						++misMatchesCount;

						if (staging_ldd != null) {
							staging_ldd.setIdColumn(0l);

							// Add the column to listDataDefinition
							long count = listdatasourcedao.insertListDataDefinitionForIdData(lddDelta.getIdData(),
									staging_ldd);
							updatedCount += count;
						}
					}
				}

				if (misMatchesCount == updatedCount) {
					status = true;
				}

				// If RuleCatalog is enabled, update the change in the RuleCatalog table
				boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();

				if (isRuleCatalogEnabled) {
					LOG.info(
							"\n====> Updating the template changes in the Associated validations RuleCatalogs");
					CompletableFuture.runAsync(() -> {
						long idData = listDataDefinitionDelta.get(0).getIdData();
						ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
					});
				}

			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	public JSONObject updateColumnCheckThreshold(long idApp, String checkName, String column_or_rule_name,double failed_Threshold, String defaultPattern) {
		String status = "failed";
		String message = "";
		String type;
		int thresholdUpdateStatus;
		JSONObject json = new JSONObject();
		boolean ruleCatlogStatus = false;
		boolean  validationStagingActivationStatus =false;
		String updated_Threshold_formatted ="";
		try {
			// Fetching template Details
			ruleCatlogStatus = ruleCatalogService.isRuleCatalogEnabled();
			validationStagingActivationStatus = ruleCatalogService.isValidationStagingActivated(idApp);
			ListDataSource listDataSource = validationCheckDao.getTemplateDetailsForAppId(idApp);
			DecimalFormat numberFormat = new DecimalFormat("#0.00");

			if (listDataSource != null) {

				long templateId = listDataSource.getIdData();

				long globalRuleId = 0l;

				double updated_Threshold = failed_Threshold;

				if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)){
					updated_Threshold_formatted=",val:"+defaultPattern+"per: ("+Math.round(failed_Threshold)+"%)";
				}else {			

				    updated_Threshold_formatted = numberFormat.format(updated_Threshold);
					updated_Threshold= Double.valueOf(updated_Threshold_formatted);
				}

				if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS) || checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {

					if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)) {
						type = "identity";
					} else {
						type = "all";
					}

					// update in actual table
					thresholdUpdateStatus = ruleCatalogDao.updateDuplicateCheckThresholdByType(idApp, type, updated_Threshold);
					
					if (thresholdUpdateStatus > 0 && ruleCatlogStatus && validationStagingActivationStatus) {
						// Update in staging table 
						thresholdUpdateStatus = ruleCatalogDao.updateStagingDuplicateCheckThresholdByType(idApp, type, updated_Threshold);
					}

					if (thresholdUpdateStatus > 0) {
						status = "success";
						message = "Threshold Successfully Updated by " + updated_Threshold_formatted;
					} else {
						LOG.error("\n====>Failed to update status for DuplicateCheck-"+type);
						message = "Failed to update threshold of DuplicateCheck-"+type;
					}

				} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE)) {

					globalRuleId = ruleCatalogDao.getRuleIdForGlobalRules(templateId,column_or_rule_name);
					thresholdUpdateStatus = ruleCatalogDao.updateGlobalRuleThreshold(globalRuleId, updated_Threshold);

					LOG.debug("Updated global threshold by => " + updated_Threshold);

					if (thresholdUpdateStatus > 0) {
						status = "success";
						message = "Threshold Successfully Updated by " + updated_Threshold_formatted;
					} else {
						LOG.error("\n====>Failed to update status into listColGlobalRules");
						message = "Failed to update threshold of Custom Rule";
					}
				} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_CUSTOM_RULE )) {
					thresholdUpdateStatus = ruleCatalogDao.updateCustomRuleThreshold(templateId,column_or_rule_name, updated_Threshold);
					LOG.debug("Updated custom threshold by => " + updated_Threshold);

					if (thresholdUpdateStatus > 0) {
						status = "success";
						message = "Threshold Successfully Updated by " + updated_Threshold_formatted;
					} else {
						LOG.error("\n====>Failed to update status into listColRules");
						message = "Failed to update threshold of Custom Rule";
					}
				}else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK )) {
					
					LOG.info("\n====Update default Pattern check Patterns");
					
					RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
					if (ruleSpec != null) {
						// Get threshold column name
						String thresholdColumnName = ruleSpec.getTemplateCheckThresholdColumn();
						thresholdUpdateStatus = templateviewdao.updatePatternIntoListDatadefinition(templateId, thresholdColumnName, column_or_rule_name, updated_Threshold_formatted, defaultPattern);

						if (thresholdUpdateStatus > 0) {

							thresholdUpdateStatus = templateviewdao.updatePatternIntoStagingListDatadefinition(templateId, thresholdColumnName, column_or_rule_name, updated_Threshold_formatted, defaultPattern);

							if (thresholdUpdateStatus > 0) {
								status = "success";
								message = "Pattern Successfully Updated by " + updated_Threshold_formatted.replace("val:", "").replace("per:", "");
							} else {
								LOG.error("\n====>Failed to update status into staging_listDataDefinition");
								message = "Failed to update pattern in template staging";
							}
						} else {
							LOG.error("\n====>Failed to update status into listDataDefinition");
							message = "Failed to update pattern in template";
						}
					} else {
						LOG.debug("\n====>Failed to  retreived pattern column name for check " + checkName);
					}

				}  else {
					RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
					if (ruleSpec != null) {
						// Get threshold column name
						String thresholdColumnName = ruleSpec.getTemplateCheckThresholdColumn();
						thresholdUpdateStatus = templateviewdao.updateCheckValueIntoListDatadefinition(templateId, thresholdColumnName, column_or_rule_name, updated_Threshold_formatted);

						if (thresholdUpdateStatus > 0) {

							thresholdUpdateStatus = templateviewdao.updateCheckValueIntoStagingListDatadefinition(templateId, thresholdColumnName, column_or_rule_name, updated_Threshold_formatted);

							if (thresholdUpdateStatus > 0) {
								status = "success";
								message = "Threshold Successfully Updated by " + updated_Threshold_formatted;
							} else {
								LOG.error("\n====>Failed to update status into staging_listDataDefinition");
								message = "Failed to update threshold in template staging";
							}
						} else {
							LOG.error("\n====>Failed to update status into listDataDefinition");
							message = "Failed to update threshold in template";
						}
					} else {
						LOG.debug("\n====>Failed to  retreived threshold column name for check " + checkName);
					}
				}
				if (status.equalsIgnoreCase("success")) {
					// Auto approve the changes in Rule catalog
					// Threshold  change will be applied directly in the staging and actual rule catalog tables
					// TODO: In future this becomes a manual approval, then this step have to removed again
					// ---- START ----
					ruleCatalogDao.autoApproveThresholdChangesInActualRuleCatalog(idApp,checkName,column_or_rule_name,updated_Threshold, updated_Threshold_formatted, defaultPattern);

					ruleCatalogDao.autoApproveThresholdChangesInStagingRuleCatalog(idApp,checkName,column_or_rule_name,updated_Threshold, updated_Threshold_formatted, defaultPattern);
					// ---- END ----
					ruleCatalogService.updateRuleCatalog(idApp);

					// Update Associated Rule catalogs
					if(checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_GLOBAL_RULE))
						ruleCatalogService.updateRuleCatalogsForGlobalRuleThresholdChange(globalRuleId);
					else
						CompletableFuture.runAsync(() -> {
							ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(templateId);
						});
				}

			}else {
				LOG.error("\n====>Failed to fetch template details");
				message = "Unable to update threshold, failed to fetch template details";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to update Threshold";
			e.printStackTrace();
		}
		json.put("status",status);
		json.put("message",message);

		return json;
	}

	public JSONObject deactivateColumnCheck(long idApp, String checkName, String columnNames) {

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		List<String> deactivate_failedColumns = new ArrayList<String>();
		List<String> deactivate_passedColumns = new ArrayList<String>();

		try {

			// Validate idApp
			ListApplications listApplications = validationCheckDao.getdatafromlistapplications(idApp);

			if (listApplications != null) {
				long idData = listApplications.getIdData();

				// Validate checkName
				RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
				if (ruleSpec != null) {

					/*
					 * Validate columnNames
					 */

					// Get template column names list
					List<String> templateColumnsList = validationCheckDao.getDisplayNamesFromListDataDefinition(idData);

					// Check if the template has columns
					if (templateColumnsList != null && !templateColumnsList.isEmpty()) {

						// Verify if the columnName field has data
						if (columnNames != null && columnNames.trim().split(",").length > 0) {

							// Variable to store invalid column names
							List<String> invalidColumnNamesList = new ArrayList<String>();

							// Split and get the list of selected columns
							String[] selectedColumns = columnNames.trim().split(",");

							// Filter out invalid columns list
							for (String selectedColumn : selectedColumns) {
								if (templateColumnsList != null && !templateColumnsList.contains(selectedColumn.trim()))
									invalidColumnNamesList.add(selectedColumn.trim());
							}

							// Proceed if no invalid columns found
							if (invalidColumnNamesList.isEmpty()) {

								// For each selected column, deactivate the check in actual and staging
								// listDataDefinition
								for (String selectedColumn : selectedColumns) {
									selectedColumn = selectedColumn.trim();

									String columnValue = "N";
									String templateCheckEnabledColumn = ruleSpec.getTemplateCheckEnabledColumn();

									// Deactivating check in the actual listDataDefinition
									int updateStatus = templateviewdao.updateCheckValueIntoListDatadefinition(idData,
											templateCheckEnabledColumn, selectedColumn, columnValue);
									if (updateStatus > 0) {

										// Get the listDataDefinition from staging
										List<ListDataDefinition> stgListDataDefinition = templateviewdao
												.getListDataDefinitionsInStaging(idData);

										if (stgListDataDefinition != null && stgListDataDefinition.size() > 0) {

											// Deactivating check in the staging listDataDefinition
											updateStatus = templateviewdao
													.updateCheckValueIntoStagingListDatadefinition(idData,
															templateCheckEnabledColumn, selectedColumn, columnValue);

											if (updateStatus > 0) {
												deactivate_passedColumns.add(selectedColumn);
											} else
												deactivate_failedColumns.add(selectedColumn);
										}

									} else
										deactivate_failedColumns.add(selectedColumn);

								}

								// Update the rule catalog of specific validation
								ruleCatalogService.updateRuleCatalog(idApp);

								// Update the rule catalogs of all the associated validations of template
								CompletableFuture.runAsync(() -> {
									ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
								});
								checkName = (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_BADDATACHECK)) ? "Data Type Check":checkName;
								if (deactivate_failedColumns.isEmpty()) {
									status = "success";									
									message = "Deactivation of " + checkName + " for column " + columnNames + " was successful";
								} else {
									
									message = "Deactivation of " + checkName + " is failed for columns " +  String.join(",", deactivate_failedColumns) +   " was successful";
									
									
									json.put("deactivatedColumns", deactivate_passedColumns);
									json.put("failedColumns", deactivate_failedColumns);
								}

							} else {								
								message = String.join(",", invalidColumnNamesList)+" "+(invalidColumnNamesList.size()>1?" are ":" is " )+" invalid column(s) in Column Name field.";
							}
						} else
							message = "Invalid columnNames";
					} else
						message = "No columns found for this template";

				}else 
					message = "Invalid checkName";
			 }else
				message = "Invalid ValidationId";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to update ColumnNameCheck";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		return json;
	}
	
	public JSONObject getNonEnabledColumnsForCheck(long idApp, String checkName) {
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";

		try {
			// Validate idApp
			ListApplications listApplications = validationCheckDao.getdatafromlistapplications(idApp);

			if (listApplications != null) {

				// Validate checkName
				RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
				if (ruleSpec != null) {

					// Get templateId
					long idData = listApplications.getIdData();
					String templateCheckEnabledColumn = ruleSpec.getTemplateCheckEnabledColumn();
					String templateCheckThresholdColumn = ruleSpec.getTemplateCheckThresholdColumn();

					JSONArray nonEnabledColumns = new JSONArray();
					if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_LENGTHCHECK)
							|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MAXLENGTHCHECK)) {
						// Fetch the list of column and its datatypes not enabled for Length and
						// MaxLength check
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForLengthCheck(idData,
								templateCheckThresholdColumn);
					} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_PATTERNCHECK)) {
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForPatternCheck(idData, templateCheckThresholdColumn);
					} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTPATTERNCHECK)) {
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForDefaultPatternCheck(idData, templateCheckThresholdColumn);
					} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTCHECK)) {
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForDefaultCheck(idData, templateCheckThresholdColumn);
					} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DATERULECHECK)) {
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForDateRuleCheck(idData, templateCheckThresholdColumn);
					} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)
							|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {
						// Fetch the list of column and its datatypes not enabled for primary key check and duplicate key
						//check
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForPrimaryKeyAndDuplicateKeyCheck(idData);

					} else {
						// Fetch the list of column and its datatypes for which the check is not enabled
						nonEnabledColumns = ruleCatalogDao.getNonEnabledColumnsForCheck(idData,
								templateCheckEnabledColumn, templateCheckThresholdColumn);
					}
					status = "success";
					json.put("result", nonEnabledColumns);
				} else
					message = "Invalid checkName";
			} else
				message = "Invalid ValidationId";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		return json;
	}
	
	public JSONObject activateColumnCheck(long idApp, String checkName, JSONArray columnNames) {

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		List<String> activate_failedColumns = new ArrayList<String>();
		List<String> activate_passedColumns = new ArrayList<String>();
		Map<String, JSONObject> columns_map = new HashMap<String, JSONObject>();

		try {
			// Validate idApp
			ListApplications listApplications = validationCheckDao.getdatafromlistapplications(idApp);

			if (listApplications != null) {
				long idData = listApplications.getIdData();

				// Validate checkName
				RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
				LOG.debug(ruleSpec);
				if (ruleSpec != null) {
					/*
					 * Validate columnNames
					 */
					// Get template column names list
					List<String> templateColumnsList = validationCheckDao.getDisplayNamesFromListDataDefinition(idData);
					LOG.debug("templateColumnsList "+templateColumnsList);
					// Check if the template has columns
					if (templateColumnsList != null && !templateColumnsList.isEmpty()) {

						// Verify if the columnName field has data
						if (columnNames != null && columnNames.length() > 0) {

							// Variable to store invalid column names
							List<String> invalidColumnNamesList = new ArrayList<String>();

							for (Object json_obj : columnNames) {
								JSONObject col_obj = (JSONObject) json_obj;
								columns_map.put(col_obj.getString("name"), col_obj);
							}
							LOG.debug("columns_map  "+columns_map);

							// Get the list of columns
							Set<String> selectedColumns = columns_map.keySet();
							
							LOG.debug("selectedColumns  "+selectedColumns);

							// Filter out invalid columns list
							for (String selectedColumn : selectedColumns) {
								if (templateColumnsList != null && !templateColumnsList.contains(selectedColumn.trim()))
									invalidColumnNamesList.add(selectedColumn.trim());
							}
							
							LOG.debug("templateColumnsList "+templateColumnsList);

							// Proceed if no invalid columns found
							if (invalidColumnNamesList.isEmpty()) {

								// For each selected column, activate the check in actual and staging
								// listDataDefinition
								for (String selectedColumn : selectedColumns) {
									selectedColumn = selectedColumn.trim();

									String columnValue = "Y";
									String templateCheckEnabledColumn = ruleSpec.getTemplateCheckEnabledColumn();
									String templateCheckThresholdColumn = ruleSpec.getTemplateCheckThresholdColumn();

									// Activating check in the actual listDataDefinition
									int updateStatus = templateviewdao.updateCheckValueIntoListDatadefinition(idData,
											templateCheckEnabledColumn, selectedColumn, columnValue);

									if (updateStatus > 0) {

										// Get the listDataDefinition from staging
										List<ListDataDefinition> stgListDataDefinition = templateviewdao
												.getListDataDefinitionsInStaging(idData);

										JSONObject column_details_obj = columns_map.get(selectedColumn);
										String threshold_value = String.valueOf(column_details_obj.getDouble("threshold"));
										// default pattern check doesn't have any threshold column
										templateCheckThresholdColumn = templateCheckThresholdColumn.trim().equalsIgnoreCase("defaultPatterns") ? "" : templateCheckThresholdColumn;
										if (templateCheckThresholdColumn != null
												&& !templateCheckThresholdColumn.trim().isEmpty()
												&& !templateCheckThresholdColumn.trim().equalsIgnoreCase("0.0"))
											templateviewdao.updateCheckValueIntoListDatadefinition(idData,
													templateCheckThresholdColumn, selectedColumn, threshold_value);

										if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_LENGTHCHECK) || checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MAXLENGTHCHECK)) {
											String lengthValue = String.valueOf(column_details_obj.getLong("lengthValue"));
											templateviewdao.updateCheckValueIntoListDatadefinition(idData,
													"lengthValue", selectedColumn, lengthValue);
										} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_PATTERNCHECK)) {
											String pattern_value = column_details_obj.getString("patterns");
											templateviewdao.updateCheckValueIntoListDatadefinition(idData, "patterns", selectedColumn, pattern_value);
										} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTCHECK)) {
											String defaultValue = column_details_obj.getString("defaultvalues");
											templateviewdao.updateCheckValueIntoListDatadefinition(idData, "defaultvalues", selectedColumn, defaultValue);
										} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DATERULECHECK)) {
											String dateFormat = column_details_obj.getString("dateformat");
											templateviewdao.updateCheckValueIntoListDatadefinition(idData, "dateformat", selectedColumn, dateFormat);
										}

										if (stgListDataDefinition != null && stgListDataDefinition.size() > 0) {

											// Activating check in the staging listDataDefinition
											updateStatus = templateviewdao
													.updateCheckValueIntoStagingListDatadefinition(idData,
															templateCheckEnabledColumn, selectedColumn, columnValue);

											if (templateCheckThresholdColumn != null
													&& !templateCheckThresholdColumn.trim().isEmpty()
													&& !templateCheckThresholdColumn.trim().equalsIgnoreCase("0.0"))
												templateviewdao.updateCheckValueIntoStagingListDatadefinition(idData,
														templateCheckThresholdColumn, selectedColumn, threshold_value);

											if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_LENGTHCHECK) || checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MAXLENGTHCHECK)) {
												String length_value = String.valueOf(column_details_obj.getLong("lengthValue"));
												templateviewdao.updateCheckValueIntoStagingListDatadefinition(idData,
														"lengthValue", selectedColumn, length_value);
											} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_PATTERNCHECK)) {
												String pattern_value = column_details_obj.getString("patterns");
												templateviewdao.updateCheckValueIntoStagingListDatadefinition(idData, "patterns", selectedColumn, pattern_value);
											} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DEFAULTCHECK)) {
												String defaultValue = column_details_obj.getString("defaultvalues");
												templateviewdao.updateCheckValueIntoStagingListDatadefinition(idData, "defaultvalues", selectedColumn, defaultValue);
											} else if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_DATERULECHECK)) {
												String dateFormat = column_details_obj.getString("dateformat");
												templateviewdao.updateCheckValueIntoStagingListDatadefinition(idData, "dateformat", selectedColumn, dateFormat);
											}

											if (updateStatus > 0) {
												activate_passedColumns.add(selectedColumn);
											} else
												activate_failedColumns.add(selectedColumn);
										} else
											activate_passedColumns.add(selectedColumn);

									} else
										activate_failedColumns.add(selectedColumn);

								}

								// If columns are activated and check is not enabled in validation, enable it
								if (activate_passedColumns.size() > 0) {

									String checkEntityName = ruleSpec.getEntityName();
									String checkEntityColumn = ruleSpec.getCheckColumn();
									String fieldValue = "";
									Field oField = null;

									boolean isValidationStagingEnabled = ruleCatalogService
											.isValidationStagingActivated(idApp);

									// Get the validation details from staging table i.e., staging_listApplications
									if (isValidationStagingEnabled) {
										LOG.info(
												"\n====> Get the validation details from staging table i.e., staginglistApplications table ..");
										listApplications = ruleCatalogDao.getDataFromStagingListapplications(idApp);
									}

									// All the checks whose data is part of listApplications entity
									if (checkEntityName
											.equalsIgnoreCase(DatabuckConstants.RC_ENTITY_LISTAPPLICATIONS)) {

										oField = listApplications.getClass().getDeclaredField(checkEntityColumn);
										oField.setAccessible(true);

										fieldValue = (String) oField.get(listApplications);

										if (fieldValue == null || !fieldValue.trim().equalsIgnoreCase("Y")) {
											// Enable the check in staging or actual table
											if (isValidationStagingEnabled)
												ruleCatalogDao.enableOrDisableCheckInValidationStaging(idApp,
														checkEntityColumn, "Y");
											else
												ruleCatalogDao.enableOrDisableCheckInValidation(idApp,
														checkEntityColumn, "Y");
										}

									}

									// This is for Duplicate check whose data is part of another entity
									else if (checkEntityName
											.equalsIgnoreCase(DatabuckConstants.RC_ENTITY_LISTDFTRANRULE)) {

										String type = "";
										// Duplicate Check - Primary Columns
										if (checkName.equalsIgnoreCase(
												DatabuckConstants.RULE_TYPE_DUPLICATECHECKPRIMARYFIELDS)) {
											type = "identity";
											if (isValidationStagingEnabled)
												fieldValue = ruleCatalogDao
														.isStagingDuplicateCheckIdentityEnabled(idApp);
											else
												fieldValue = ruleCatalogDao.isDuplicateCheckIdentityEnabled(idApp);

										}
										// Duplicate Check - Selected Columns
										else if (checkName.equalsIgnoreCase(
												DatabuckConstants.RULE_TYPE_DUPLICATECHECKSELECTEDFIELDS)) {
											type = "all";
											if (isValidationStagingEnabled)
												fieldValue = ruleCatalogDao.isStagingDuplicateCheckAllEnabled(idApp);
											else
												fieldValue = ruleCatalogDao.isDuplicateCheckAllEnabled(idApp);

										}

										if (fieldValue == null || !fieldValue.trim().equalsIgnoreCase("Y")) {
											// Enable the check in staging or actual table
											if (isValidationStagingEnabled)
												ruleCatalogDao.enableOrDisableStagingDuplicateCheckByType(idApp, type,
														checkEntityColumn, "Y");
											else
												ruleCatalogDao.enableOrDisableDuplicateCheckByType(idApp, type,
														checkEntityColumn, "Y");
										}
									}
								}

								// Update the rule catalog of specific validation
								ruleCatalogService.updateRuleCatalog(idApp);

								// Update the rule catalogs of all the associated validations of template
								CompletableFuture.runAsync(() -> {
									ruleCatalogService.updateAssociatedRuleCatalogsOfTemplate(idData);
								});

								checkName = (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_BADDATACHECK)) ? "Data Type Check":checkName;
								if (activate_failedColumns.isEmpty()) {
									status = "success";
									message = "Activation of " + checkName + " for columns "
											+ String.join(",", activate_passedColumns) + " was Successful";
								} else {
									message = "Activation of " + checkName + " is failed for columns "
											+ String.join(",", activate_failedColumns) + " ";
									json.put("activatedColumns", activate_passedColumns);
									json.put("failedColumns", activate_failedColumns);
								}

							} else {
//								message = "Invalid columns present in columnNames field : ["
//										+ String.join(",", invalidColumnNamesList) + "]";
								message = String.join(",", invalidColumnNamesList)+" "+(invalidColumnNamesList.size()>1?" are ":" is " )+" invalid column(s) in Column Name field.";
							}
						} else
							message = "Invalid columnNames";
					} else
						message = "No columns found for this template";

				} else
					message = "Invalid checkName";

			} else
				message = "Invalid ValidationId";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to update ColumnNameCheck";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);
		return json;
	}
	
	public JSONObject updateRecordCountAnomalyThreshold(long idApp, String checkName, double failed_Threshold) {
		String status = "failed";
		String message = "";
		JSONObject json = new JSONObject();
		try {
			RuleCatalogCheckSpecification ruleSpec = ruleCatalogService.getCheckSpecificationByRuleType(checkName);
			String column_Name = ruleSpec.getTemplateCheckThresholdColumn();
			boolean isRuleCatalogEnabled = ruleCatalogService.isRuleCatalogEnabled();
			boolean isValidationStagingActivated = ruleCatalogService.isValidationStagingActivated(idApp);

			DecimalFormat numberFormat = new DecimalFormat("#0.00");
			double updated_Threshold = failed_Threshold;
			String updated_Threshold_formatted = numberFormat.format(updated_Threshold);
			updated_Threshold = Double.valueOf(updated_Threshold_formatted);

			if (checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_RECORDCOUNTANOMALY)
					|| checkName.equalsIgnoreCase(DatabuckConstants.RULE_TYPE_MICROSEGMENTRECORDCOUNTANOMALY)) {

				ruleCatalogDao.approveRCAThresholdChangesInActualValidation(idApp, checkName, column_Name,
						updated_Threshold);

				if (isRuleCatalogEnabled && isValidationStagingActivated) {
					ruleCatalogDao.approveRCAThresholdChangesInStagingValidation(idApp, checkName, column_Name,
							updated_Threshold);
				}

				// Auto approve the changes in Rule catalog
				// Threshold change will be applied directly in the staging and actual rule

				// ---- START ----
				ruleCatalogDao.approveRCAThresholdChangesInActualRuleCatalog(idApp, checkName, updated_Threshold);

				ruleCatalogDao.approveRCAThresholdChangesInStagingRuleCatalog(idApp, checkName, updated_Threshold);
				// ---- END ----
				ruleCatalogService.updateRuleCatalog(idApp);

				status = "success";
				message = "Threshold successfully updated by "+updated_Threshold+"%";

			} else {
				LOG.error("\n====>Invalid Check Name");
				message = "Invalid Check Name...!";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to update Threshold";
			e.printStackTrace();
		}
		json.put("status", status);
		json.put("message", message);

		return json;
	}

	public JSONObject getProfileDataForTemplate(long idData) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		String status = "failed";
		long idDataSchema = 0l;
		String message = "";
		try {
			ListDataSource listDataSource = listdatasourcedao.getDataFromListDataSourcesOfIdData(idData);
			if (listDataSource != null) {
				listDataAccess listDataAccess = listdatasourcedao.getListDataAccess(idData);
				idDataSchema = iTaskDAO.getIdDataSchemaByIdData(idData);
				String dataLocation = listDataSource.getDataLocation();

				if (idDataSchema > 0l) {
					int limit = 100;
					try {
						String limitVal= appDbConnectionProperties.getProperty("profile_data_view_max_count");
						if(limitVal!=null && !limitVal.trim().isEmpty())
							limit = Integer.parseInt(limitVal);
					}catch (Exception e){
						LOG.error(e.getMessage());
						e.printStackTrace();
					}

					JSONObject sourceDataResult = new JSONObject();
					LOG.debug("====> Datalocation :: "+dataLocation);
					if (dataLocation.trim().equalsIgnoreCase("SnowFlake")) {
						sourceDataResult = snowFlakeConnection.getProfileData(listDataSource,listDataAccess,idDataSchema, limit);
						message = "success";
						status = "success";
					} else if (dataLocation.trim().equalsIgnoreCase("MSSQL")) {
						sourceDataResult = mSSQLConnection.getProfileData(listDataSource,listDataAccess,idDataSchema,limit);
						message = "success";
						status = "success";
					} else if (dataLocation.trim().equalsIgnoreCase("Mapr Hive")) {
						sourceDataResult = hiveConnection.getProfileData(listDataSource,listDataAccess,idDataSchema,limit);
						message = "success";
						status = "success";
					} else if (dataLocation.equalsIgnoreCase("Hive Kerberos")) {
						sourceDataResult = hiveConnection.getProfileDataForHive(listDataSource,listDataAccess,idDataSchema,limit);
						message = "success";
						status = "success";
					} else {
						message = "Profile data view is not supported for "+dataLocation;
					}
					result.put("sourceData", sourceDataResult);
				}
			} else
				message = "Invalid templateId";
		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to get profile data details";
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		return json;
	}

	public JSONObject validateQueryForTemplate(ValidateQuery validateQuery) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String valQuery = "";
		try {
			List<ListDataSchema> listDataSchemas = dataTemplateAddNewDAO.getListDataSchema(validateQuery.getIdDataSchema());
			if(listDataSchemas!=null && !listDataSchemas.isEmpty()){
				ListDataSchema listDataSchema = listDataSchemas.get(0);
				String schemaType = listDataSchema.getSchemaType().trim();
				String isQueryEnabled = validateQuery.getIsQueryEnabled();

				if(isQueryEnabled.equalsIgnoreCase("Y") && (validateQuery.getQueryString()==null || validateQuery.getQueryString().isEmpty())){
					message = "Please enter Query.";
				} else if(!isQueryEnabled.equalsIgnoreCase("Y") && (validateQuery.getWhereCondition()==null || validateQuery.getWhereCondition().isEmpty())){
					message = "Please enter Where Condition.";
				} else {
					LOG.debug(validateQuery);
					LOG.debug("====> SchemaType :: " + schemaType);
					valQuery = validateQuery.getQueryString().trim().toLowerCase();
					if ((valQuery != null && !valQuery.isEmpty() && !valQuery.startsWith("insert") && !valQuery.startsWith("alter")
							&& !valQuery.startsWith("drop") && !valQuery.startsWith("delete")) || !isQueryEnabled.equalsIgnoreCase("Y")) {
						if (schemaType.equalsIgnoreCase("MSSQL")) {
							result = mSSQLConnection.validateQuery(validateQuery, listDataSchema);
							message = "success";
							status = "success";
						} else if (schemaType.equalsIgnoreCase("Mapr Hive")) {
							validateQuery.setQueryString(validateQuery.getQueryString().trim().replaceAll("'","\""));
							result = hiveConnection.validateQuery(validateQuery, listDataSchema);
							message = "success";
							status = "success";
						} else if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
							result = hiveConnection.validateQueryForHive(validateQuery, listDataSchema);
							message = "success";
							status = "success";
						} else if (schemaType.equalsIgnoreCase("SnowFlake")) {
							result = snowFlakeConnection.validateQuery(validateQuery, listDataSchema);
							message = "success";
							status = "success";
						} else {
							message = "Validate Query is not supported for " + schemaType;
						}
					} else
						message = "Invalid Query, Please Enter Valid Query";
				}

			} else {
				message = "Invalid Connection Id";
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to validate query.";
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		return json;
	}

	public JSONObject validateQueryForDerivedTemplate(DerivedTemplateValidateQuery validateQuery) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		try {

			ListDataSource listDataSource1 = listdatasourcedao.getDataFromListDataSourcesOfIdData(validateQuery.getTemplate1IdData());
			if(listDataSource1 != null && listDataSource1.getDataLocation().equalsIgnoreCase("Hive Kerberos") ){

				LOG.info("====> Template Name :: "+listDataSource1.getName());
				LOG.info("====> Template DataLocation :: "+listDataSource1.getDataLocation());
				LOG.info("====> idDataSchema:: "+listDataSource1.getIdDataSchema());

				validateQuery.setIdDataSchema1(listDataSource1.getIdDataSchema());
				List<ListDataSchema> listDataSchemas = dataTemplateAddNewDAO.getListDataSchema(validateQuery.getIdDataSchema1());
				if(listDataSchemas!=null && !listDataSchemas.isEmpty()){
					ListDataSchema listDataSchema = listDataSchemas.get(0);
					String schemaType = listDataSchema.getSchemaType().trim();

					if(validateQuery.getQueryString()==null || validateQuery.getQueryString().isEmpty()){
						message = "Please enter Query.";
					} else {
						LOG.debug(validateQuery);
						LOG.debug("====> SchemaType :: "+schemaType);
						if (schemaType.equalsIgnoreCase("Hive Kerberos")) {
							result = hiveConnection.validateQueryForHiveDerivedTemplate(validateQuery, listDataSchema);
							message = "success";
							status = "success";
						} else {
							message = "Validate Query is not supported for this template" + schemaType;
						}
					}
				} else {
					message = "Invalid Connection Id";
				}
			} else {
				message = "Validate Query is not supported for this template";
			}



		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to validate query.";
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);
		return json;
	}

	public JSONObject bulkUploadToRefTable(long idApp, String tableName, List<String[]> allData) {
		String status = "failed";
		String message = "";
		JSONObject json = new JSONObject();
		JSONObject result = new JSONObject();
		int failedCount=0, totalCount=allData.size()-1, duplicateCount=0;
		try {
			String[] header = allData.get(0);
			allData.remove(0);
			String columnName = String.join("$$", header);
			columnName = DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES) ? columnName.toLowerCase() : columnName;
			LOG.debug("Column Headers :: "+columnName.replace("$$", ", "));

			Map<String, String> metadata = iResultsDAO.getRefTableColumns(tableName);
			LOG.debug("Metadata :: "+metadata);
//			Check length of table headers and file headers
			if(metadata.size()-1 == header.length){
				int columnMatchCount =0;
//				Calculate table headers and file headers names
				for(String col : header){
					 col = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? col.toLowerCase() : col;
					if(metadata.containsKey(col))
						columnMatchCount++;
				}
//				Calculate table headers and file headers names
				if(columnMatchCount == header.length){
//					Iterating data to insert in reference table
					for (String[] data : allData) {
						if(!String.join("", data).equals("")){
							String columnValue = String.join("$$", data);
//							Check if duplicate record is available
							boolean isExists = iResultsDAO.checkRecordInRefTable(columnName, columnValue, tableName, metadata);
							if(isExists){
								duplicateCount++;
								continue;
							}
//							Insert record in reference table
							String newRecord = iResultsDAO.insertNewValueToRefTable(columnName, columnValue, tableName);
							if(newRecord == null){
								failedCount++;
								LOG.debug("Failed Record :: "+columnValue);
							}
						}
					}
					status = "success";
					message = "Records imported Successfully";
					result.put("totalCount",totalCount);
					result.put("failedCount",failedCount);
					result.put("duplicateCount",duplicateCount);
				} else {
					message = "Import file columns does not match with table";
				}
			} else {
				message = "Import file columns does not match with table";
			}


		} catch (Exception e) {
			LOG.error(e.getMessage());
			message = "Error Occurred, failed to add record";
			e.printStackTrace();
		}
		json.put("result", result);
		json.put("status", status);
		json.put("message", message);

		return json;
	}
	
}
