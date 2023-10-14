package com.databuck.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
//import com.databuck.bean.Group;
import com.databuck.bean.Project;
import com.databuck.bean.User;

public interface IProjectService {	
	
	public List<Project> getAllProjects();	
	public List<Project> getPaginationProject(int start, int numRecords, String sSearch);	
	public List<Domain> getPaginationDomain(int start, int numRecords, String sSearch);	
	public int getTotalRecordCount();
	public int getTotalDisplayRecords(String sSearch);
	public int getTotalRecordCountdomain();
	public int getTotalDisplayRecordsdomain(String sSearch);
	public boolean deleteProject(Long idProject);
	public Project getSelectedProject(Long idProject);
	public int checkDuplicateProject(String projectName);
	public int checkDuplicateDomain(String domainName);
	public Long insertDataIntoProjectTable(String projectName, String projectDescription);
	public int insertProjectToGroupAssociation(Long projectId, String groupId, String isOwner);
	public int insertProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner);
	public int insertDomainToProjectAssociation(Long domainId, String projectId, String isOwner);
	public List<Project> getAllProjectsOfAUser(String idUser);
	public Long updateDataIntoProjectTable(String projectName, String projectDescription, int id);
	public int updateProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner);
	public int updateProjectToGroupAssociation(Long projectId, String groupId, String isOwner);
	public int  delProjectToGroupAssociationActive(Long sprojectId);
	public int  delProjectToGroupAssociation(Long sprojectId);
	public String getListofProjectIdsAssignedToCurrentUser(List<Project> projList);
	public Long insertDataIntoDomainTable(String domainName, String domainDescription);
	public List<DomainProject> getDomainProjectAssociationOfCurrentUser(List<Project> projlst);
	public List<DomainProject> getDomainProjectAssociationOfCurrentUserByMailId(String emailId);
	/** 9thApril2020
	 * Code By : Anant Mahale
	 * @param projectName
	 * @return : return project id
	 */
	public int getProjectIdByProjectName(String projectName);
	public List<Map<String, Object>> getAllProjectsWithAggDomains();
	
}
