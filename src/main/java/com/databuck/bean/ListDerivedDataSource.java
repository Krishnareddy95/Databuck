package com.databuck.bean;

import java.util.Date;

/*Bean created for listDerivedDataSource - which holds the derived template data 
 */
public class ListDerivedDataSource {

	// data member
	private long idDerivedData;
	private long idData;
	private String name;
	private String description;
	private String template1Name;
	private long template1IdData;
	private String template1AliasName;
	private String template2Name;
	private long template2IdData;
	private String template2AliasName;
	private String queryText;
	private long createdBy;
	private Date createdAt;
	private Date updatedAt;
	private long updatedBy;
	private String createdByUser;

	public long getIdDerivedData() {
		return idDerivedData;
	}

	public void setIdDerivedData(int idDerivedData) {
		this.idDerivedData = idDerivedData;
	}

	public long getIdData() {
		return idData;
	}

	public void setIdData(long idData) {
		this.idData = idData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTemplate1Name() {
		return template1Name;
	}

	public void setTemplate1Name(String template1Name) {
		this.template1Name = template1Name;
	}

	public String getTemplate2Name() {
		return template2Name;
	}

	public void setTemplate2Name(String template2Name) {
		this.template2Name = template2Name;
	}

	public String getTemplate1AliasName() {
		return template1AliasName;
	}

	public void setTemplate1AliasName(String template1AliasName) {
		this.template1AliasName = template1AliasName;
	}

	public String getTemplate2AliasName() {
		return template2AliasName;
	}

	public void setTemplate2AliasName(String template2AliasName) {
		this.template2AliasName = template2AliasName;
	}

	public long getTemplate1IdData() {
		return template1IdData;
	}

	public void setTemplate1IdData(long template1IdData) {
		this.template1IdData = template1IdData;
	}

	public long getTemplate2IdData() {
		return template2IdData;
	}

	public void setTemplate2IdData(long template2IdData) {
		this.template2IdData = template2IdData;
	}

	public String getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(String createdByUser) {
		this.createdByUser = createdByUser;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public ListDerivedDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ListDerivedDataSource(long idDerivedData, long idData, String name, String description, String template1Name,
			long template1IdData, String template1AliasName, String template2Name, long template2IdData,
			String template2AliasName, String queryText, long createdBy, Date createdAt, Date updatedAt, long updatedBy,
			String createdByUser) {
		super();
		this.idDerivedData = idDerivedData;
		this.idData = idData;
		this.name = name;
		this.description = description;
		this.template1Name = template1Name;
		this.template1IdData = template1IdData;
		this.template1AliasName = template1AliasName;
		this.template2Name = template2Name;
		this.template2IdData = template2IdData;
		this.template2AliasName = template2AliasName;
		this.queryText = queryText;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
		this.createdByUser = createdByUser;

	}

	@Override
	public String toString() {
		return "ListDerivedDataSource [idData=" + idData + ", idDerivedData=" + idDerivedData + ", name=" + name
				+ ", description=" + description + ", template1Name=" + template1Name + ", template1IdData="
				+ template1IdData + ", createdBy=" + createdBy + ", template1AliasName=" + template1AliasName
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", updatedBy=" + updatedBy
				+ ", template2Name=" + template2Name + ", template2IdData=" + template2IdData + ", template2AliasName="
				+ template2AliasName + ", queryText=" + queryText + ", createdByUser=" + createdByUser + "]";
	}

}
