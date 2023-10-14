package com.databuck.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.DefectCode;
import com.databuck.bean.Dimension;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
//import com.databuck.bean.Group;
import com.databuck.bean.Project;

public interface IProjectDAO {	

	
	
	public List<Project> getPaginationProject(int start, int numRecords, String sSearch);
	public List<Domain> getPaginationDomain(int start, int numRecords, String sSearch);
	public int getTotalRecordCount();
	public int getTotalDisplayRecords(String sSearch);	
	public int getTotalRecordCountdomain();
	public int getTotalDisplayRecordsdomain(String sSearch);	
	public boolean deleteProject(Long idProject);
    public Project getSelectedProject(Long idProject);	
	public List<Project> getAllProjects();
	public Long insertDataIntoProjectTable(String projectName, String projectDescription);
	public Long insertDataIntoDomainTable(String domainName, String domainDescription);
	public int insertProjectToGroupAssociation(Long projectId, String groupId, String isOwner);
	public int insertDomainToProjectAssociation(Long domainName, String projectId, String isOwner);
	public int insertProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner);
	public List<Project> getAllProjectsOfAUser(String idUser);
	public Long updateDataIntoProjectTable(String projectName, String projectDescription, int id);	
	public int updateProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner);
	public int updateProjectToGroupAssociation(Long projectId, String groupId, String isOwner);
	public int delProjectToGroupAssociation(Long projectId);
	public int delProjectToGroupAssociationActive(Long projectId);
	public List<Domain> getAllDomain();
	public List<Dimension> getAllDimension();
	public List<DomainProject> getDomainProjectAssociationOfCurrentUser(List<Project> projlst);
	//Insert project in database
	//	
	
	//public int insertProjectToGroupAssociation(Long projectId, Long groupId, String isOwner);	
	
	//public int delAssociatedOwn(String delValues);
	//public int addAssociatedOwn(Long projId, Long addValues, String isOwner);
	
	/** 9thApril2020
	 * Code By : Anant Mahale
	 * @param projectName
	 * @return : return project id
	 */
	public int getProjectIdByProjectName(String projectName);
	public String getProjectNameByProjectid(Long projectId);
	public Long getProjectIdfromListAppTable(Long idApp);

	public boolean isProjectFromDomain(long projectId, long domainId);
	public boolean isProjectIdValid(long projectId);
	public boolean isDomainIdValid(long domainId);
	public List<Project> getAllProjectsForDomain(int domainId);
	public List<DomainProject> getAllDomainProjectsForUser(String idUser);
	public boolean isDomainProjectValid(int domainId, int projectId);
	public List<DomainProject> getAllDomainProjectsForUser();
	public List<DefectCode> getAllDefectCode();
	public List<Map<String, Object>> getAllProjectsWithAggDomains();
	
	
}
