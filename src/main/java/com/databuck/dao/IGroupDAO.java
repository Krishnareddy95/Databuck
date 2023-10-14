package com.databuck.dao;

import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.User;
import com.databuck.bean.Project;
import com.databuck.bean.Role;
import com.databuck.bean.User;
public interface IGroupDAO {
	
	//public List<User> getData();

	//public Group getSelectedGroup(Long idGroup);
	public List<User> getAllGroups();
	public List<User> getAllGroupsfromActiveDirectory();
	public List<User> getAllassignGroups(Long projectId);
	public List<User> getAllassignGroupsfromActiveDirectory(Long projectId);
	public List<User> getAllassignuserfromassociationtable(Long projectId);
	public List<User> getAllassignuserfromnormalassociationtable(Long projectId);
	/*public List<Group> getPaginationGroups(int start, int numRecords, String sSearch);
	public int getTotalRecordCount();
	public int getTotalDisplayRecords(String sSearch);
	public Long insertDataIntoGroup(String groupName, String description);
	public int insertUserToGroupAssociation(Long groupId, Long userId);
	public int deleteGroup(Long idGroup);
	public List<Group> getAssociatedGroups(Long idProject, String isOwner);
	public List<User> getAssociatedUser(Long idProject);
	public Long updateDataIntoGroupTable(String groupName, String description, int id);
	public int delAssociatedOwn(String delValues);
	public int addAssociatedOwn(Long projId, Long grpOwnAddId);*/
}
