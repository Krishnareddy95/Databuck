package com.databuck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.Domain;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.bean.UserLogin;

public interface LoginService {

	Long userAuthentication(String email, String password);

	Long getRolesFromUserRoleTable(Long idUser);
	
	Long getRoleIdFromRoleTable(String roleName);

	SqlRowSet getIdTaskandAccessControlFromRoleModuleTable(Long idRole);

	String getTaskNameFromModuleTable(long idTask);

	String getFirstNameFromUserTable(Long idUser);

	User userActiveDirectoryAuthentication(String email, String password)throws Exception;
	
	//Long getRoleIdActiveDirectory(String Rolename)throws Exception;
	
	String getRoleNamefromActiveDirectory(String email, String password)throws Exception;
	ArrayList<String> getAllRoleNamefromActiveDirectory()throws Exception;
	Long getRoleMapToLdapGroupFromGroupRolemapTable(String ldapGroup);
	String getRoleFromRoleTable(Long idRole);
	List<Project> getAllProjectsOfARole(String ldapGroup , Long dRole);
	List<Domain> getAllDomains();
	//List<User> getListOfUsersActiveDirectory()throws Exception;
	List<String> getComponentListofRole(Long idRole);
	ArrayList<String> getgroupfrom_Program_Std_Out(String ProgramOutput);
	
	/*[29-Sep-2020]:Changes for LDAP group role mapping Starts*/
	public HashMap<Long,String> getRoleDataFromLdapAfterLogin(ArrayList<String> groupNameList);
	public HashMap<Long,String> getProjectDataFromLdapAfterLogin(ArrayList<String> groupNameList);
	public boolean getIsUserPresent(String cn);
	public boolean insertNewUserRecord(Long idRole, String cn, String password, ArrayList<String> aGroupNameList);
	public List<Project> getProjectListOfUser(ArrayList<String> groupNameList);
	/*[29-Sep-2020]:Changes for LDAP group role mapping Ends*/

	List<Project> getAllDistinctProjectListForUser(HttpSession oSession);

	public boolean validateIdRole(long idRole);

	String generateCSRFToken();

	String getDatabuckHome();

	int checkDaysLeftForLicenseRenewal(HttpSession session);

	String getBelongsToRoles(HashMap<Long, String> oMappedRoles);

	boolean updateLicenseKeyPropertyInDB(String licenseKey);

	public int validateLicense(Map<String, Object> licenseDetails);
}
