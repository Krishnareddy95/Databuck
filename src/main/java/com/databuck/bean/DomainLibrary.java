package com.databuck.bean;

import java.io.Serializable;

public class DomainLibrary implements Serializable {
	
	private int domainId;
	private String domainName;
	private String isGlobalDomain;
	private String projectIds;
	private String projectNames;
	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getIsGlobalDomain() {
		return isGlobalDomain;
	}
	public void setIsGlobalDomain(String isGlobalDomain) {
		this.isGlobalDomain = isGlobalDomain;
	}
	public String getProjectIds() {
		return projectIds;
	}
	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}
	public String getProjectNames() {
		return projectNames;
	}
	public void setProjectNames(String projectNames) {
		this.projectNames = projectNames;
	}
	
	public DomainLibrary(int domainId, String domainName, String isGlobalDomain, String projectIds,
			String projectNames) {
		super();
		this.domainId = domainId;
		this.domainName = domainName;
		this.isGlobalDomain = isGlobalDomain;
		this.projectIds = projectIds;
		this.projectNames = projectNames;
	}
	
	@Override
	public String toString() {
		return "DomainManagementService [domainId=" + domainId + ", domainName=" + domainName + ", isGlobalDomain="
				+ isGlobalDomain + ", projectIds=" + projectIds + ", projectNames=" + projectNames + "]";
	}
}
