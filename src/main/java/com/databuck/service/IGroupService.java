
package com.databuck.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.User;
import com.databuck.bean.ListApplications;
import com.databuck.bean.Project;
import com.databuck.bean.User;


public interface IGroupService {
	
	
	public List<User> getAllGroups();	
	public List<User> getAllGroupsfromActiveDirectory();
	public List<User> getAllassignGroups(Long projectId);	
	public List<User> getAllassignGroupsfromActiveDirectory(Long projectId);
	
	
	

}
