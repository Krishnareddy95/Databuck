package com.databuck.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.Module;
import com.databuck.bean.User;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IUserDAO;
import com.databuck.service.IUserService;

@Service
public class UserServiecImpl implements IUserService {

    @Autowired
    private IUserDAO userdao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger LOG = Logger.getLogger(UserServiecImpl.class);

    public List<User> getData() {

	List<User> user = userdao.getData();

	return user;
    }

    public List<User> getData_WithRole() {

	List<User> user = userdao.getData_WithRole();

	return user;
    }

    public long deleteUser(long idRole) {

	long count = userdao.deleteUser(idRole);
	return count;
    }

    @Override
    public User get(long idUser) {
	User user = userdao.get(idUser);
	return user;
    }

    public boolean updateUserPassword(Long idUser, String newPassword) {
	return userdao.updateUserPassword(idUser, newPassword);
    }

    public boolean validateCurrentPassword(Long idUser, String currentPassword) {
	return userdao.validateCurrentPassword(idUser, currentPassword);
    }

    public SqlRowSet getAccessControlsFromModuleTable() {
	return userdao.getAccessControlsFromModuleTable();
    }

    public SqlRowSet getroleManagementFromRoleTable() {

	String sql = "select idRole,roleName,description from Role";
	return jdbcTemplate.queryForRowSet(sql);
    }

    public SqlRowSet getUsersFromUserTable() {
	// Query compatibility changes for both POSTGRES and MYSQL
//		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
//				: "User";
//		String role_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"role\""
//				: "Role";
	String sql = "";
	if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
	    sql = "select u.idUser,u.firstName,u.lastName,u.email, r.roleName from \"User\" as u join \"role\" as r on u.usertype = r. idrole";
	} else {
	    sql = "select u.idUser,u.firstName,u.lastName,u.email, r.roleName from User as u join Role as r on u.userType = r. idRole";
	}
	return jdbcTemplate.queryForRowSet(sql);
    }

    public Map<Long, String> getRoleNameandIdRoleFromRoleTable() {
	String sql = "select idRole,roleName from Role";
	Map<Long, String> Roles = new LinkedHashMap<Long, String>();
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	while (queryForRowSet.next()) {
	    Roles.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
	    // LOG.debug("idRole="+"roleName=");
	}
	LOG.debug("Roles=" + Roles);
	return Roles;
    }

    public int insertDataIntoUserTable(final Long roleid, final String firstName, final String lastName,
	    final String userName, final String password) {
	// Query compatibility changes for both POSTGRES and MYSQL
	String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
		: "User";
	final String sql = "insert into " + user_table
		+ "(firstName,lastName,password,email,userType,createdAt,updatedAt)" + "VALUES (?,?,?,?,?,now(),now())";
	final String encryptedTextPassword = BCrypt.hashpw(password, BCrypt.gensalt());

	// Query compatibility changes for both POSTGRES and MYSQL
	String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "iduser"
		: "idUser";

	KeyHolder keyHolder = new GeneratedKeyHolder();
	jdbcTemplate.update(new PreparedStatementCreator() {

	    @Override
	    public PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(sql, new String[] { key_name });
		ps.setString(1, firstName);
		ps.setString(2, lastName);
		ps.setString(3, encryptedTextPassword);
		ps.setString(4, userName);
		ps.setLong(5, roleid);
		return ps;
	    }
	}, keyHolder);
	Long key = keyHolder.getKey().longValue();
	LOG.debug("key=" + key);

	String query = "insert into UserRole(	idUser,idRole)" + "VALUES(?,?)";
	int update = jdbcTemplate.update(query, key, roleid);
	LOG.debug("insert into UserRole=" + update);
	// LOG.debug("keyHolder.getKey():"+keyHolder.getKey());
	return keyHolder.getKey().intValue();
    }

    public Map<Long, String> getIdTaskandTaskName() {
	String sql = "select idTask,taskName from Module";
	Map<Long, String> Modules = new LinkedHashMap<Long, String>();
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	while (queryForRowSet.next()) {
	    Modules.put(queryForRowSet.getLong(1), queryForRowSet.getString(2));
	    // LOG.debug("idRole="+"roleName=");
	}
	// LOG.debug("Modules="+Modules);
	return Modules;
    }

    public List<Module> getIdTaskandTaskNameandDisplayName() {
	String sql = "select idTask,taskName, displayName from Module";
	List<Module> Modules = new ArrayList<>();
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	while (queryForRowSet.next()) {
	    Module mod = new Module();
	    mod.setIdModule(queryForRowSet.getLong(1));
	    mod.setModuleName(queryForRowSet.getString(2));
	    mod.setDisplayName(queryForRowSet.getString(3));
	    Modules.add(mod);
	}
	// LOG.debug("Modules="+Modules);
	return Modules;
    }

    public List<Module> getIdTaskandTaskNameandDisplayNameWithFilter() {
	String sql = "select idTask,taskName, displayName from Module "
		+ "where taskName in ('Data Connection','Data Template','Validation Check','Tasks','Results',"
		+ "'User Settings','Global Rule','Application Settings','QuickStart','Alert Inbox','Profile Data View')";
	List<Module> Modules = new ArrayList<>();
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	while (queryForRowSet.next()) {
	    Module mod = new Module();
	    mod.setIdModule(queryForRowSet.getLong(1));
	    mod.setModuleName(queryForRowSet.getString(2));
	    mod.setDisplayName(queryForRowSet.getString(3));
	    Modules.add(mod);
	}
	// LOG.debug("Modules="+Modules);
	return Modules;
    }

    public int updateIntoRoleandRoleModuleTable(List<String> key, List<String> value, Long idRole) {
	int update = 0;
	try {
	    String sql = "DELETE FROM RoleModule WHERE idRole=" + idRole;
	    update = jdbcTemplate.update(sql);
	    LOG.debug("DELETE FROM RoleModule=" + update);

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String query = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
		query = "insert into RoleModule(idRole,idTask,accessControl) VALUES(?,?::int,?)";
	    } else {
		query = "insert into RoleModule(idRole,idTask,accessControl) VALUES(?,?,?)";
	    }

	    for (int i = 0; i < key.size(); i++) {
		if (i > 0)
		    update = jdbcTemplate.update(query, idRole, key.get(i),
			    value.get(i).substring(1, value.get(i).length()));
		else
		    update = jdbcTemplate.update(query, idRole, key.get(i), value.get(i));
	    }
	    LOG.debug("insert into RoleModule=" + update);
	    return update;
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
	return update;
    }

    public int insertIntoRoleandRoleModuleTable(List<String> key, List<String> value, final String roleName,
	    final String description) {
	final String sql = "insert into Role(roleName,description,createdAt,updatedAt)" + "VALUES (?,?,now(),now())";

	// Query compatibility changes for both POSTGRES and MYSQL
	String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idrole"
		: "idRole";

	KeyHolder keyHolder = new GeneratedKeyHolder();
	jdbcTemplate.update(new PreparedStatementCreator() {

	    @Override
	    public PreparedStatement createPreparedStatement(java.sql.Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(sql, new String[] { key_name });
		ps.setString(1, roleName);
		ps.setString(2, description);
		return ps;
	    }
	}, keyHolder);
	Long idRole = keyHolder.getKey().longValue();
	LOG.debug("idRole=" + idRole);
	int update = 0;

	// Query compatibility changes for both POSTGRES and MYSQL
	String query = "";
	if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
	    query = "insert into RoleModule(idRole,idTask,accessControl) VALUES(?,?::int,?)";
	} else {
	    query = "insert into RoleModule(idRole,idTask,accessControl) VALUES(?,?,?)";
	}

	for (int i = 0; i < key.size(); i++) {
	    if (i > 0)
		update = jdbcTemplate.update(query, idRole, key.get(i),
			value.get(i).substring(1, value.get(i).length()));
	    else
		update = jdbcTemplate.update(query, idRole, key.get(i), value.get(i));
	}
	LOG.debug("insert into RoleModule=" + update);
	return Math.toIntExact(idRole);

    }

    public Map<Long, String> getIdTaskandAccessControlFromRoleModuleTable(String idRole) {
	String query = "select * from RoleModule where idRole=" + idRole + " ORDER BY idTask";
	SqlRowSet roleModuleTable = jdbcTemplate.queryForRowSet(query);
	Map<Long, String> Modules = new LinkedHashMap<Long, String>();
	while (roleModuleTable.next()) {
	    Modules.put(roleModuleTable.getLong("idTask"), roleModuleTable.getString("accessControl"));
	}
	LOG.debug("RoleModules=" + Modules);
	return Modules;
    }

    public Map<String, String> getRoleNameandDescriptionFromRole(String idRole) {
	String sql = "select roleName,description from Role where idRole=" + idRole;
	SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	Map<String, String> roleNameandDescription = new LinkedHashMap<String, String>();
	while (queryForRowSet.next()) {
	    roleNameandDescription.put(queryForRowSet.getString(1), queryForRowSet.getString(2));
	}
	return roleNameandDescription;

    }

    public String checkDuplicateEmail(String email) {
	String Name = null;
	// Query compatibility changes for both POSTGRES and MYSQL
	String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
		: "User";
	String q = "SELECT email FROM " + user_table + " WHERE email=? limit 1";
	Object[] inputs = new Object[] { email };
	try {
	    Name = jdbcTemplate.queryForObject(q, inputs, String.class);
	    LOG.debug("Name=" + Name);
	    return Name;
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    return Name;
	}

    }

    public String checkDuplicateRoleName(String roleName) {
	String Name = null;
	String q = "SELECT roleName FROM Role WHERE roleName=?";
	Object[] inputs = new Object[] { roleName };
	try {
	    Name = jdbcTemplate.queryForObject(q, inputs, String.class);
	    LOG.debug("Name=" + Name);
	    return Name;
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    return Name;
	}
    }

    public void updateIntoSecureAPI(String randomString, String secretAccessToken) {
	try {
	    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	    encryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
	    jdbcTemplate.update("delete from secure_API");
	    String sql = "insert into secure_API values(?,?)";
	    jdbcTemplate.update(sql, encryptor.encrypt(randomString), encryptor.encrypt(secretAccessToken));
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Code By : Anant S. Mahale; Date : 24thMarch2020
     * 
     * @param locationName : collect from UI
     * @param projectId    : collect from UI
     * @return : return true if record inserted in table else false
     */
    @Override
    public boolean insertLocationRecord(String locationName, int projectId) {
	try {
	    LOG.debug(" UserServiceImpl : insertLocationRecord  : ");
	    if (locationName != null && projectId > 0) {
		LOG.debug(" UserServiceImpl : insertLocationRecord  : locationName :: " + locationName
			+ " | projectId :: " + projectId);

		String sql = "INSERT INTO locations " + "(locationName, projectId) VALUES (?, ?)";

		int intCheckInsertionStatus = jdbcTemplate.update(sql, new Object[] { locationName, projectId });
		if (intCheckInsertionStatus > 0)
		    return true;
		else
		    return false;
	    }
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : insertLocationRecord  : Exception :: " + e.getMessage());
	}
	return false;
    }

    /**
     * Code By : Anant S. Mahale; Date : 24thMarch2020
     * 
     * @param projectId : project id from ui dropdown
     * @return : return map of location name as value and location id by respective
     *         project id
     */
    @Override
    public Map<Integer, String> getListOfLocationsbyProject(int projectId) {
	LOG.debug(" UserServiceImpl : getListOfLocationsbyProject  ");
	try {
	    if (projectId > 0) {
		LOG.debug(" UserServiceImpl : getListOfLocationsbyProject : projectId :: " + projectId);
		Map<Integer, String> mapProjectIdAndProjectName = new java.util.HashMap<Integer, String>();

		String sql = " SELECT id,locationName FROM locations WHERE projectid =" + projectId;
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

		while (queryForRowSet.next()) {
		    mapProjectIdAndProjectName.put(queryForRowSet.getInt("id"),
			    queryForRowSet.getString("locationName"));
		}

		return mapProjectIdAndProjectName;

	    } else {
		LOG.error(" UserServiceImpl : getListOfLocationsbyProject  : Custome Error :: Project ID is not valid");
		return null;
	    }
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : getListOfLocationsbyProject  : Exception :: " + e.getMessage());
	    return null;
	}
    }

    /**
     * Code By : Anant S. Mahale; Date : 24thMarch2020
     * 
     * @param projectId : project id from ui dropdown
     * @return : return map of validation name as value and validation id by
     *         respective project id
     */
    @Override
    public Map<Integer, String> getListOfValidationsbyProject() {
	LOG.info(" UserServiceImpl : getListOfValidationsbyProject  ");
	try {
	    LOG.info(" UserServiceImpl : getListOfValidationsbyProject : ");
	    Map<Integer, String> mapProjectIdAndProjectName = new java.util.HashMap<Integer, String>();

	    String sql = "SELECT idApp, NAME FROM listApplications";
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

	    while (queryForRowSet.next()) {
		mapProjectIdAndProjectName.put(queryForRowSet.getInt("idApp"), queryForRowSet.getString("name"));
	    }

	    return mapProjectIdAndProjectName;
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : getListOfValidationsbyProject  : Exception :: " + e.getMessage());
	    return null;
	}
    }

    /**
     * Code By : Anant S. Mahale; Date : 24thMarch2020
     * 
     * @param projectId : project id from ui dropdown
     * @return : return boolean response if true then record inserted and false that
     *         mean record not inserted
     */
    @Override
    public boolean insertLocationMapping(int idApp, int locationId) {
	try {
	    LOG.info(" UserServiceImpl : insertLocationMapping  : ");
	    if (idApp > 0 && locationId > 0) {
		LOG.debug(" UserServiceImpl : insertLocationMapping  : idApp :: " + idApp + " | locationId :: "
			+ locationId);

		String sql = "INSERT INTO locationMapping " + "(locationId, idApp) VALUES (?, ?)";

		int intCheckInsertionStatus = jdbcTemplate.update(sql, new Object[] { locationId, idApp });
		if (intCheckInsertionStatus > 0)
		    return true;
		else
		    return false;
	    }
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : insertLocationMapping  : Exception :: " + e.getMessage());
	}
	return false;

    }

    /**
     * Code By : Anant S. Mahale; Date : 24thMarch2020
     * 
     * @param projectId : project id from ui dropdown
     * @return : return boolean response if true then record inserted and false that
     *         mean record not inserted
     */
    @Override
    public boolean insertValidationMapping(int validation, int existingValidation) {
	try {
	    LOG.info(" UserServiceImpl : insertValidationMapping  : ");
	    if (validation > 0 && existingValidation > 0) {
		LOG.debug(" UserServiceImpl : insertValidationMapping  : validation :: " + validation
			+ " | existingValidation :: " + existingValidation);

		String sql = "INSERT INTO validationMapping " + "(idApp, relationIdApp) VALUES (?, ?)";

		int intCheckInsertionStatus = jdbcTemplate.update(sql, new Object[] { validation, existingValidation });
		if (intCheckInsertionStatus > 0)
		    return true;
		else
		    return false;
	    }
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : insertValidationMapping  : Exception :: " + e.getMessage());
	}
	return false;
    }

    /**
     * Code By : Anant S. Mahale; Date : 09thApril2020
     * 
     * @param paramStrTableName   : table name for query
     * @param paramStrWhereClause : string type where clause including all condition
     * @return : return boolean value it is build to check that perticular record
     *         exists or not. If not exists then new record will be insert.
     */
    @Override
    public boolean validateInsertRecord(String paramStrTableName, String paramStrWhereClause) {
	try {
	    LOG.info(" UserServiceImpl : validateInsertRecord  : ");
	    StringBuilder strBuildQuery = new StringBuilder();
	    strBuildQuery.append("SELECT COUNT(*) FROM ");
	    strBuildQuery.append(paramStrTableName);
	    strBuildQuery.append(" WHERE ");
	    strBuildQuery.append(paramStrWhereClause);
	    LOG.debug(" UserServiceImpl : validateInsertRecord  : Query :: " + strBuildQuery.toString());
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(strBuildQuery.toString());
	    int intCountRows = 0;
	    while (queryForRowSet.next()) {
		intCountRows = queryForRowSet.getInt(1);
	    }
	    if (intCountRows > 0)
		return false;
	    else
		return true;
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : validateInsertRecord  : Exception :: " + e.getMessage());
	    return false;
	}

    }

    /**
     * Last Update : 17thApril 2020 Code By : Anant S. Mahale To check validation
     * mapping with privious locations. If it exists then boolean false will return.
     * That will terminate process.
     */
    @Override
    public boolean checkMappingWithPreviousLocations(int paramIntLocationId, int paramIntIDApp) {
	try {
	    LOG.debug(" UserServiceImpl : checkMappingWithPreviousLocations : paramIntLocationId :: "
		    + paramIntLocationId + " & paramIntIDApp :: " + paramIntIDApp);
	    List<Integer> listLocationId = getListOfLocations(paramIntLocationId);
	    int checkMappingExistsOrNot = 0;
	    for (Integer integerLocationId : listLocationId) {
		int intRowCount = 0;
		String strQuery = "SELECT COUNT(*) FROM locationMapping AS lm WHERE lm.locationId = "
			+ integerLocationId + " AND lm.idApp = " + paramIntIDApp;
		LOG.debug(" UserServiceImpl : checkMappingWithPreviousLocations : Query :: " + strQuery);
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(strQuery.toString());
		while (queryForRowSet.next()) {
		    intRowCount = queryForRowSet.getInt(1);
		}
		LOG.debug(" UserServiceImpl : checkMappingWithPreviousLocations : Query :: " + strQuery + " | Count :: "
			+ intRowCount);
		if (intRowCount > 0) {
		    checkMappingExistsOrNot++;
		}
	    }
	    if (checkMappingExistsOrNot > 0)
		return false;
	    else
		return true;
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : checkMappingWithPreviousLocations  : Exception :: " + e.getMessage());
	    return false;
	}

    }

    /**
     * Last Updated : 17thApril2020 Code By : Anant S. Mahale.
     * 
     * @param paramIntLocationId
     * @return List of locations it checks previous locations of given location id.
     */
    private List<Integer> getListOfLocations(int paramIntLocationId) {
	try {
	    LOG.debug(" UserServiceImpl : getListOfLocations : paramIntLocationId :: " + paramIntLocationId);
	    List<Integer> listLocationId = new ArrayList<Integer>();
	    String strQuery = "SELECT ls.id FROM locations AS ls WHERE ls.id  < " + paramIntLocationId;
	    LOG.debug(" UserServiceImpl : getListOfLocations : Query :: " + strQuery);
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(strQuery.toString());
	    while (queryForRowSet.next()) {
		listLocationId.add(queryForRowSet.getInt("id"));
	    }
	    LOG.debug(" UserServiceImpl : getListOfLocations : listLocationId :: " + listLocationId);
	    return listLocationId;
	} catch (Exception e) {
	    LOG.error(" UserServiceImpl : getListOfLocations  : Exception :: " + e.getMessage());
	    return null;
	}

    }

    @Override
    public void deleteRole(String idRole) {
	int update = 0;

	try {
	    String sqlRoleModule = "DELETE FROM RoleModule WHERE idRole=" + idRole;
	    update = jdbcTemplate.update(sqlRoleModule);

	    String sqlUserRole = "DELETE FROM UserRole WHERE idRole=" + idRole;
	    update = jdbcTemplate.update(sqlUserRole);

	    String sqlRole = "DELETE FROM Role WHERE idRole=" + idRole;
	    update = jdbcTemplate.update(sqlRole);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}

    }

    @Override
    public void deleteUsers(String idUser) {
	int update = 0;

	try {

	    String sqlUserRole = "DELETE FROM UserRole WHERE idUser=" + idUser;
	    update = jdbcTemplate.update(sqlUserRole);

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
		    : "User";

	    String sqlRole = "DELETE FROM " + user_table + " WHERE idUser=" + idUser;
	    update = jdbcTemplate.update(sqlRole);
	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}

    }

    @Override
    public int updateUser(Long idUser, String firstName, String lastName, String email, Long roleId) {
	int test = 0;
	try {

	    String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
		    : "User";

	    int update = jdbcTemplate
		    .update("UPDATE  " + user_table + " SET lastName = '" + lastName + "', firstName = '" + firstName
			    + "',  " + " email = '" + email + "', userType = " + roleId + " WHERE idUser= " + idUser);
	    LOG.debug(update);
	    int x = update;
	    int update2 = jdbcTemplate.update("UPDATE UserRole SET idRole = " + roleId + " WHERE idUser= " + idUser);
	    LOG.debug(update2);
	    return x;

	} catch (Exception e) {
	    LOG.error("exception " + e.getMessage());
	    e.printStackTrace();
	}
	return test;

    }

}
