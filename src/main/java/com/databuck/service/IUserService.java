package com.databuck.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.databuck.bean.Module;
import com.databuck.bean.User;

public interface IUserService {
	
	//retrive the user Details
	public List<User> getData();
	
	//retrive the user Details with ROle Name
		public List<User> getData_WithRole();
	
	
	
	// Delete Az User
	public long deleteUser(long idUser);

	public User get(long idUser);
	public boolean updateUserPassword(Long idUser, String newPassword);
	public boolean validateCurrentPassword(Long idUser, String curentPassword);

	public SqlRowSet getAccessControlsFromModuleTable();

	public SqlRowSet getroleManagementFromRoleTable();

	public SqlRowSet getUsersFromUserTable();

	public Map<Long,String> getRoleNameandIdRoleFromRoleTable();

	public int insertDataIntoUserTable(Long roleid, String firstName, String lastName, String userName,
			String password);

	public Map<Long, String> getIdTaskandTaskName();

	public int insertIntoRoleandRoleModuleTable(List<String> key, List<String> value, String roleName, String description);

	public Map<Long, String> getIdTaskandAccessControlFromRoleModuleTable(String idRole);

	public Map<String, String> getRoleNameandDescriptionFromRole(String idRole);

	public String checkDuplicateEmail(String email);

	public String checkDuplicateRoleName(String roleName);

	public int updateIntoRoleandRoleModuleTable(List<String> key, List<String> value, Long idRole);

	public void updateIntoSecureAPI(String randomString, String secretAccessToken);
	
	/**Code By : Anant S. Mahale; 
	 * Date : 24thMarch2020
	 * @param locationName : collect from UI 
	 * @param projectId : collect from UI
	 * @return : return true if record inserted in table else false
	 */
	public boolean insertLocationRecord(String locationName, int projectId);
	
	/**Code By : Anant S. Mahale; 
	 * Date : 24thMarch2020
	 * @param locationName : collect from UI 
	 * @param projectId : collect from UI
	 * @return : return location name as value and id as key by project id
	 */
	public Map<Integer, String> getListOfLocationsbyProject(int projectId);
	
	/**Code By : Anant S. Mahale; 
	 * Date : 24thMarch2020
	 * @param locationName : collect from UI 
	 * @param projectId : collect from UI
	 * @return : return map of validation name as value and id as key
	 */
	public Map<Integer, String> getListOfValidationsbyProject();
	
	/**Code By : Anant S. Mahale; 
	 * Date : 24thMarch2020
	 * @param locationName : collect from UI 
	 * @param projectId : collect from UI
	 * @return : return true if record inserted in table else false
	 */
	public boolean insertLocationMapping(int idApp, int locationId);
	
	/**Code By : Anant S. Mahale; 
	 * Date : 24thMarch2020
	 * @param locationName : collect from UI 
	 * @param projectId : collect from UI
	 * @return : return true if record inserted in table else false
	 */
	public boolean insertValidationMapping(int validation, int existingValidation);
	
	/**Code By : Anant S. Mahale; 
	 * Date : 09thApril2020
	 * @param paramStrTableName : table name for query
	 * @param paramStrWhereClause : string type where clause including all condition
	 * @return : return boolean value
	 * it is build to check that perticular record exists or not. If not exists then new record will be insert. 
	 */
	public boolean validateInsertRecord(String paramStrTableName, String paramStrWhereClause);
	
	
	/**09thApril2020
	 * Code By : Anant S. Mahale
	 * @param paramIntLocationId
	 * @param paramIntIDApp
	 * @return
	 * to check that location mapping and validation mapping not go in circular manner
	 */ 
	public boolean checkMappingWithPreviousLocations(int paramIntLocationId, int paramIntIDApp);

	public void deleteRole(String idRole);

	public void deleteUsers(String idUser);

	int updateUser(Long idUser,String firstName, String lastName, String email, Long roleId);
	
	public List<Module> getIdTaskandTaskNameandDisplayName();
	public List<Module> getIdTaskandTaskNameandDisplayNameWithFilter();
}
