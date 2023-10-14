package com.databuck.bean;

import java.util.List;

public class TemplateDeltaResponse {
	private Long idData;
	private Long idDataSchema;
	private String deltaApprovalStatus;
	private List<ListDataDefinitionDelta> deltaListDataDefinition;
	private List<ListDataDefinition> newColumnsList;
	private List<ListDataDefinition> missingColumnsList;
	private String microsegmentsChanged;
	private String columnsAdded;
	private String columnsDeleted;
	private String matchColumnsConfigChanged;

	public Long getIdData() {
		return idData;
	}

	public void setIdData(Long idData) {
		this.idData = idData;
	}

	public Long getIdDataSchema() {
		return idDataSchema;
	}

	public void setIdDataSchema(Long idDataSchema) {
		this.idDataSchema = idDataSchema;
	}

	public String getDeltaApprovalStatus() {
		return deltaApprovalStatus;
	}

	public void setDeltaApprovalStatus(String deltaApprovalStatus) {
		this.deltaApprovalStatus = deltaApprovalStatus;
	}

	public List<ListDataDefinitionDelta> getDeltaListDataDefinition() {
		return deltaListDataDefinition;
	}

	public void setDeltaListDataDefinition(List<ListDataDefinitionDelta> deltaListDataDefinition) {
		this.deltaListDataDefinition = deltaListDataDefinition;
	}

	public List<ListDataDefinition> getNewColumnsList() {
		return newColumnsList;
	}

	public void setNewColumnsList(List<ListDataDefinition> newColumnsList) {
		this.newColumnsList = newColumnsList;
	}

	public List<ListDataDefinition> getMissingColumnsList() {
		return missingColumnsList;
	}

	public void setMissingColumnsList(List<ListDataDefinition> missingColumnsList) {
		this.missingColumnsList = missingColumnsList;
	}

	public String getMicrosegmentsChanged() {
		return microsegmentsChanged;
	}

	public void setMicrosegmentsChanged(String microsegmentsChanged) {
		this.microsegmentsChanged = microsegmentsChanged;
	}

	public String getColumnsAdded() {
		return columnsAdded;
	}

	public void setColumnsAdded(String columnsAdded) {
		this.columnsAdded = columnsAdded;
	}

	public String getColumnsDeleted() {
		return columnsDeleted;
	}

	public void setColumnsDeleted(String columnsDeleted) {
		this.columnsDeleted = columnsDeleted;
	}

	public String getMatchColumnsConfigChanged() {
		return matchColumnsConfigChanged;
	}

	public void setMatchColumnsConfigChanged(String matchColumnsConfigChanged) {
		this.matchColumnsConfigChanged = matchColumnsConfigChanged;
	}

}
