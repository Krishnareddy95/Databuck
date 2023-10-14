package com.databuck.dao;

import java.util.List;

import com.databuck.bean.LoggingActivity;
import com.databuck.bean.LoginGroupMapping;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.Role;
import com.databuck.bean.User;

public interface IUserDAO {


	// get User DATA
	public List<User> getData();

	// get User DATA with Role
		public List<User> getData_WithRole();
	
	
	public long deleteUser(long idUser);


	public boolean updateUserPassword(Long idUser, String newPassword);


	public User get(long idUser);
	public boolean validateCurrentPassword(Long idUser, String currentPassword);


	public SqlRowSet getAccessControlsFromModuleTable();

	String getUserNameByUserId(Long idUser);
	String getRoleNameByRoleId(Long idRole);
	String getProjectNameByProjectId(Long projectId);
	String getDimensionNameByDimensionId(Long idDimension);
	public User getUserDataByName(String userName);
	
	public String getActivityFromUrl(String ReqUrl);
	public SqlRowSet getlogging_activity();
	public void clearAccessLog();
	public String getfirstNameByUserId(Long idUser) ;
	public List<LoginGroupMapping> getListOfLoginGroupMapping();
	public boolean getDefectCodeAndDiamensionId(String defectCode,int diamensionId);
	public int addDefectCode(String defectCode, String defectDescription,int dimensionId);
	public boolean checkDuplicateDimension(String dimensionName);
}
