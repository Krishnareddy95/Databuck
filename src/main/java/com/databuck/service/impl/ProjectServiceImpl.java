package com.databuck.service.impl;

import com.databuck.service.IProjectService;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
//import com.databuck.bean.Group;
import com.databuck.bean.Project;
import com.databuck.dao.IProjectDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Service
public class ProjectServiceImpl implements IProjectService {

	@Autowired
	private IProjectDAO projectDao;

	public List<Project> getAllProjects() {
		return projectDao.getAllProjects();
	}

	public List<Map<String, Object>> getAllProjectsWithAggDomains() {
		return projectDao.getAllProjectsWithAggDomains();
	}

	public List<Project> getPaginationProject(int start, int numRecords, String sSearch) {
		return projectDao.getPaginationProject(start, numRecords, sSearch);
	}

	public List<Domain> getPaginationDomain(int start, int numRecords, String sSearch) {
		return projectDao.getPaginationDomain(start, numRecords, sSearch);
	}

	public int getTotalRecordCount() {
		return projectDao.getTotalRecordCount();

	}

	public int getTotalRecordCountdomain() {
		return projectDao.getTotalRecordCountdomain();

	}

	public Long updateDataIntoProjectTable(String projectName, String projectDescription, int id) {
		Long update = projectDao.updateDataIntoProjectTable(projectName, projectDescription, id);
		return update;
	}

	public int getTotalDisplayRecords(String sSearch) {
		return projectDao.getTotalDisplayRecords(sSearch);

	}

	public int getTotalDisplayRecordsdomain(String sSearch) {
		return projectDao.getTotalDisplayRecordsdomain(sSearch);

	}

	public boolean deleteProject(Long idProject) {
		return projectDao.deleteProject(idProject);

	}

	public Project getSelectedProject(Long idProject) {
		return projectDao.getSelectedProject(idProject);
	}

	public Long insertDataIntoProjectTable(String projectName, String projectDescription) {
		Long update = projectDao.insertDataIntoProjectTable(projectName, projectDescription);
		return update;
	}

	public Long insertDataIntoDomainTable(String DomainName, String DomainDescription) {
		Long update = projectDao.insertDataIntoDomainTable(DomainName, DomainDescription);
		return update;
	}

	public int insertProjectToGroupAssociation(Long projectId, String groupId, String isOwner) {
		int update = projectDao.insertProjectToGroupAssociation(projectId, groupId, isOwner);
		return update;
	}

	public int insertProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner) {
		int update = projectDao.insertProjectToGroupAssociationActive(projectId, groupId, isOwner);
		return update;
	}

	public int insertDomainToProjectAssociation(Long domainId, String projId, String isOwner) {
		int update = projectDao.insertDomainToProjectAssociation(domainId, projId, isOwner);
		return update;
	}

	public List<Project> getAllProjectsOfAUser(String idUser) {
		return projectDao.getAllProjectsOfAUser(idUser);
	}

	public int checkDuplicateProject(String projectName) {
		List<Project> lstProject = projectDao.getAllProjects();

		int duplicateGroup = 0;

		for (Project proj : lstProject) {

			String val = proj.getProjectName();

			if (val.equalsIgnoreCase(projectName)) {
				duplicateGroup++;
			}
		}
		return duplicateGroup;
	}

	public int checkDuplicateDomain(String domainName) {
		List<Domain> lstProject = projectDao.getAllDomain();

		int duplicateGroup = 0;

		for (Domain proj : lstProject) {

			String val = proj.getDomainName();

			if (val.equalsIgnoreCase(domainName)) {
				duplicateGroup++;
			}
		}
		return duplicateGroup;
	}

	@Override
	public int updateProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner) {
		int update = projectDao.updateProjectToGroupAssociationActive(projectId, groupId, isOwner);
		return update;
	}

	@Override
	public int delProjectToGroupAssociationActive(Long projectId) {
		int update = projectDao.delProjectToGroupAssociationActive(projectId);
		return update;
	}

	@Override
	public int delProjectToGroupAssociation(Long projectId) {
		int update = projectDao.delProjectToGroupAssociation(projectId);
		return update;
	}

	@Override
	public int updateProjectToGroupAssociation(Long projectId, String groupId, String isOwner) {
		int update = projectDao.updateProjectToGroupAssociation(projectId, groupId, isOwner);
		return update;
	}

	/**
	 * 9thApril2020 Code By : Anant Mahale
	 * 
	 * @param projectName
	 * @return : return project id
	 */
	@Override
	public int getProjectIdByProjectName(String projectName) {
		return projectDao.getProjectIdByProjectName(projectName);
	}

	@Override
	public String getListofProjectIdsAssignedToCurrentUser(List<Project> projList) {
		// TODO Auto-generated method stub
		String ProjIds_inclause = "";
		List<Long> ProjIds = new ArrayList<Long>();
		if(projList!=null){
			for (Iterator<Project> projIterator = projList.iterator(); projIterator.hasNext();) {
				Project project = projIterator.next();
				ProjIds.add(project.getIdProject());
			}
			ProjIds_inclause = ProjIds.toString().replace("[", "").replace("]", "");
		}
		// if(ProjIds_inclause.isEmpty())
		if (ProjIds.size() == 0) {
			ProjIds_inclause = "-1";// for empty project list ids
		}
		return ProjIds_inclause;
	}

	@Override
	public List<DomainProject> getDomainProjectAssociationOfCurrentUser(List<Project> projlst) {
		return projectDao.getDomainProjectAssociationOfCurrentUser(projlst);

	}

	@Override
	public List<DomainProject> getDomainProjectAssociationOfCurrentUserByMailId(String emailId) {
		return getDomainProjectAssociationOfCurrentUser(getAllProjectsOfAUser(emailId));
	}
}