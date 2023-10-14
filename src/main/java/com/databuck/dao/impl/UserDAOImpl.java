package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
//import org.mindrot.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.LoginGroupMapping;
import com.databuck.bean.User;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IUserDAO;

@Repository
public class UserDAOImpl implements IUserDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(UserDAOImpl.class);

	public List<User> getData() {
		/*
		 * 
		 * 
		 * 
		 * lastName; salt; password; company; department; email; createdAt;
		 * updatedAt;
		 * 
		 */
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		String sql = " SELECT idUser,firstName, lastName,salt, password"
				+ " ,company,department,email,createdAt,updatedAt FROM "+user_table;
		
		LOG.debug("sql "+sql);

		List<User> user = jdbcTemplate.query(sql, new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();

				user.setIdUser(rs.getLong("idUser"));
				user.setFirstName(rs.getString("firstName"));
				user.setLastName(rs.getString("lastName"));
				user.setSalt(rs.getString("salt"));
				user.setPassword(rs.getString("password"));
				user.setCompany(rs.getString("company"));
				user.setDepartment(rs.getString("department"));
				user.setEmail(rs.getString("email"));
				user.setCreatedAt(rs.getDate("createdAt"));
				user.setUpdatedAt(rs.getDate("updatedAt"));
				return user;
			}

		});
		return user;

	}
	
	
	
	
	public List<User> getData_WithRole() {
		/*
		 * 
		 * 
		 * 
		 * lastName; salt; password; company; department; email; createdAt;
		 * updatedAt;
		 * 
		 */
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
//		String sql = " SELECT idUser,firstName, lastName,salt, password"
//				+ " ,company,department,email,createdAt,updatedAt FROM "+user_table;
		
		String sql = "SELECT u.idUser, u.firstName, r.roleName,r.idRole, u.lastName,u.salt, u.password"
				+ " u.company, u.department ,u.email,u.createdAt,u.updatedAt FROM " + user_table + " u, Role r"
				+ " WHERE u.idUser > r.idRole";
		
		LOG.debug("sql "+sql);

		List<User> user = jdbcTemplate.query(sql, new RowMapper<User>() {

			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();

				user.setIdUser(rs.getLong("idUser"));
				user.setFirstName(rs.getString("firstName"));
				user.setLastName(rs.getString("lastName"));
				user.setSalt(rs.getString("salt"));
				user.setPassword(rs.getString("password"));
				user.setCompany(rs.getString("company"));
				user.setDepartment(rs.getString("department"));
				user.setEmail(rs.getString("email"));
				user.setCreatedAt(rs.getDate("createdAt"));
				user.setUpdatedAt(rs.getDate("updatedAt"));
				user.setRoleName(rs.getString("roleName"));
				user.setIdRole(rs.getLong("idRole"));
				return user;
			}

		});
		return user;

	}
	
	
	

	/*
	 * Delete the User details
	 * 
	 * 
	 */
	public long deleteUser(long idUser) {
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		String sql = "DELETE FROM " + user_table + " WHERE idUser=?";
		long count = jdbcTemplate.update(sql, idUser);

		return count;
	}
	/*
	 * Toget the Perticuler user Details
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.databuck.dao.IUserDAO#get(long)
	 */

	public User get(long idUser) {

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		String sql = "SELECT u.idUser, u.firstName, r.roleName,r.idRole, u.lastName, "
				+ " u.company, u.department  FROM " + user_table + " u, Role r"
				+ " WHERE u.idUser > r.idRole && u.idUser =" + idUser;
				
		
		LOG.debug("sql "+sql);

		return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {

			public User extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					User user = new User();

					user.setIdUser(rs.getLong("idUser"));
					user.setFirstName(rs.getString("firstName"));
					user.setRoleName(rs.getString("roleName"));
					user.setIdRole(rs.getLong("idRole"));
					user.setLastName(rs.getString("lastName"));
					user.setCompany(rs.getString("company"));
					user.setDepartment(rs.getString("department"));

					return user;
				}

				return null;
			}

		});
	}
	
	public boolean validateCurrentPassword(Long idUser, String currentPassword)
	{
		boolean checkpwStatus=false;
		try{
			// Query compatibility changes for both POSTGRES and MYSQL
			String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
					: "User";
			String currentPwdEncypted = jdbcTemplate.queryForObject("select password from "+user_table+" where idUser=?", String.class,idUser);
			 checkpwStatus = BCrypt.checkpw(currentPassword,currentPwdEncypted);
		}catch(Exception e)
		{
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return checkpwStatus;
	}
	public boolean updateUserPassword(Long idUser, String newPassword)
	{
		try {
			String encryptedPwd = BCrypt.hashpw(newPassword, BCrypt.gensalt());

			// Query compatibility changes for both POSTGRES and MYSQL
			String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
					: "User";
			String sql = "update " + user_table + " set password='" + encryptedPwd + "' where idUser=" + idUser;

			LOG.debug("idUser=" + idUser);
			int update = jdbcTemplate.update(sql);
			LOG.debug("update :" + update);
			return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public SqlRowSet getAccessControlsFromModuleTable(){
		String query="select taskName from Module";
		SqlRowSet accessControlsData = jdbcTemplate.queryForRowSet(query);
		return accessControlsData;
	}

	@Override
	public String getUserNameByUserId(Long idUser) {
	
			try {
				// Query compatibility changes for both POSTGRES and MYSQL
				String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
						: "User";

				String query = "SELECT firstName,COALESCE(lastName,'')  from "+user_table+" where idUser=?";
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, idUser);
				if (queryForRowSet.next() ) {
					
					String userName = queryForRowSet.getString(1) + " " + queryForRowSet.getString(2);
					return userName;
				} 
			} catch (Exception e) {
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
	@Override
	public String getfirstNameByUserId(Long idUser) {
	
			try {
				// Query compatibility changes for both POSTGRES and MYSQL
				String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
						: "User";
				
				String query = "SELECT firstName  from "+user_table+" where idUser=?";
				SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, idUser);
				if (queryForRowSet.next() ) {
					
					String userName = queryForRowSet.getString(1) ;
					return userName;
				} 
			} catch (Exception e) {
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			return null;
		}



	public User getUserDataByName(String userName) {
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		
		String sql = "select a.idUser, a.firstName, a.lastName, a.company, a.department, a.email, a.userType, b.idRole, b.roleName from "+user_table+" a,Role b where a.firstName='" + userName + "' and a.userType = b.idRole";
		LOG.debug("sql "+sql);
		return jdbcTemplate.query(sql, new ResultSetExtractor<User>() {

			public User extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					User user = new User();

					user.setIdUser(rs.getLong("idUser"));
					user.setFirstName(rs.getString("firstName"));
					user.setRoleName(rs.getString("roleName"));
					user.setIdRole(rs.getLong("idRole"));
					user.setLastName(rs.getString("lastName"));
					user.setCompany(rs.getString("company"));
					user.setDepartment(rs.getString("department"));
					user.setEmail(rs.getString("email"));

					return user;
				}

				return null;
			}

		});
	}

	@Override
	public String getActivityFromUrl(String ReqUrl) {

		try {
			String sql_check = "select activity_title from databuck_activity_urls where http_url =?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql_check, ReqUrl);
			if (queryForRowSet.next()) {

				String databuck_feature = queryForRowSet.getString(1);
				LOG.debug("=========databuck_feature=========>" + databuck_feature);
				return databuck_feature;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		LOG.info("=========databuck_feature=========> not found");
		return null;
	}

	@Override
	public SqlRowSet getlogging_activity() {
		try {
			
			String sql="select la.user_name , la.databuck_feature ,la.activity_log_time ,la.access_url from logging_activity la";
			LOG.debug("getlogging_activity :"+sql);
			return jdbcTemplate.queryForRowSet(sql);
		} catch(Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void clearAccessLog() {
		// TODO Auto-generated method stub
		try {
		LOG.info("\n====>Inside clearAccessLog ...");
		String sql = "delete from logging_activity where 1=1";
		jdbcTemplate.update(sql);
	} catch (Exception e) {
		LOG.error("exception "+e.getMessage());
		e.printStackTrace();
	}
	}

	@Override
	public List<LoginGroupMapping> getListOfLoginGroupMapping() {
		List<LoginGroupMapping> loginGroupMappingList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sDataSql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = sDataSql
						+ "select  core_qry.GroupName as groupname, core_qry.IsApproverStr as isapproverstr, \n";
				sDataSql = sDataSql
						+ "	 string_agg(distinct Role.roleName,',') as rolenames,  \n";
				sDataSql = sDataSql
						+ "		 string_agg(distinct project.projectName,',') as projectnames \n";
				sDataSql = sDataSql + "from ( \n";
				sDataSql = sDataSql + "	select  \n";
				sDataSql = sDataSql + "		a.row_id as GroupRowId, a.group_name as GroupName,  \n";
				sDataSql = sDataSql
						+ "		case when COALESCE(a.is_approver,cast(0 as int)) > 0 then 1 else 0 end as IsApproverInt, \n";
				sDataSql = sDataSql
						+ "		case when COALESCE(a.is_approver,cast(0 as int)) > 0 then 'Yes' else 'No' end as IsApproverStr, \n";
				sDataSql = sDataSql + "		  b.role_row_id as RoleRowId, c.project_row_id as ProjectRowId \n";
				sDataSql = sDataSql + "	from login_group a \n";
				sDataSql = sDataSql
						+ "		left outer join login_group_to_role b on a.row_id = b.login_group_row_id \n";
				sDataSql = sDataSql
						+ "		left outer join login_group_to_project c on a.row_id = c.login_group_row_id \n";
				sDataSql = sDataSql + ") core_qry \n";
				sDataSql = sDataSql + "	left outer join Role on core_qry.RoleRowId = Role.idRole \n";
				sDataSql = sDataSql + "	left outer join project on core_qry.ProjectRowId = project.idProject \n";
				sDataSql = sDataSql
						+ "group by  core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt;";

			} else {
				sDataSql = sDataSql
						+ "select core_qry.GroupName as groupname, core_qry.IsApproverStr as isapproverstr,  \n";
				sDataSql = sDataSql
						+ "	 group_concat(distinct Role.roleName) as rolenames,  \n";
				sDataSql = sDataSql
						+ "		 group_concat(distinct project.projectName) as projectnames \n";
				sDataSql = sDataSql + "from ( \n";
				sDataSql = sDataSql + "	select  \n";
				sDataSql = sDataSql + "		a.row_id as GroupRowId, a.group_name as GroupName,  \n";
				sDataSql = sDataSql
						+ "		case when ifnull(a.is_approver,cast(0 as unsigned)) > 0 then 1 else 0 end as IsApproverInt, \n";
				sDataSql = sDataSql
						+ "		case when ifnull(a.is_approver,cast(0 as unsigned)) > 0 then 'Yes' else 'No' end as IsApproverStr, \n";
				sDataSql = sDataSql + "		  b.role_row_id as RoleRowId, c.project_row_id as ProjectRowId \n";
				sDataSql = sDataSql + "	from login_group a \n";
				sDataSql = sDataSql
						+ "		left outer join login_group_to_role b on a.row_id = b.login_group_row_id \n";
				sDataSql = sDataSql
						+ "		left outer join login_group_to_project c on a.row_id = c.login_group_row_id \n";
				sDataSql = sDataSql + ") core_qry \n";
				sDataSql = sDataSql + "	left outer join Role on core_qry.RoleRowId = Role.idRole \n";
				sDataSql = sDataSql + "	left outer join project on core_qry.ProjectRowId = project.idProject \n";
				sDataSql = sDataSql
						+ "group by core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt;";

			}
			
			LOG.debug("sDataSql "+sDataSql);

			loginGroupMappingList = new ArrayList<LoginGroupMapping>();
			SqlRowSet loginGroupMappingData = jdbcTemplate.queryForRowSet(sDataSql);
			while (loginGroupMappingData.next()) {
				String loginGroupName = loginGroupMappingData.getString("groupname");
				String approverGroup = loginGroupMappingData.getString("isapproverstr");
				String assignedRoles = loginGroupMappingData.getString("rolenames");
				String assignedProjects = loginGroupMappingData.getString("projectnames");
				LoginGroupMapping loginGroupMapping = new LoginGroupMapping(loginGroupName, approverGroup, assignedRoles, assignedProjects);
				loginGroupMappingList.add(loginGroupMapping);
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return loginGroupMappingList;
	}
	@Override
	public boolean getDefectCodeAndDiamensionId(String defectCode,int diamensionId) {
		try{
			String sql = "select defect_code,dimension_id from defect_codes where  defect_code=? and dimension_id=?";
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, defectCode, diamensionId);
			while (sqlRowSet.next()) {
				return true;
			}
		}catch (Exception e){
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean checkDuplicateDimension(String dimensionName) {
		Integer count = 0;
		String Sql = "select count(*) as count from dimension where dimensionName = '%1$s'";
		try {
			Sql = String.format(Sql, dimensionName);
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(Sql);
			if (queryForRowSet.next()) {
				count = queryForRowSet.getInt("count");
			}
			
		} catch (Exception oException) {
			oException.printStackTrace();
		}
		if(count>0) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public int addDefectCode(String defectCode, String defectDescription,int dimensionId) {
		try{
			String sql="insert into defect_codes(defect_code, defect_description, dimension_id) values(?,?,?)";
			return jdbcTemplate.update(sql, defectCode, defectDescription, dimensionId);
		}catch (Exception e){
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}
	@Override
	public String getRoleNameByRoleId(Long idRole) {

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "role"
					: "Role";

			String query = "SELECT roleName  from "+user_table+" where idRole=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, idRole);
			if (queryForRowSet.next() ) {
				String roleName = queryForRowSet.getString("roleName");
				return roleName;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String getProjectNameByProjectId(Long projectId) {

		try {
			String query = "SELECT projectName  from project where idProject=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, projectId);
			if (queryForRowSet.next() ) {
				String projectName = queryForRowSet.getString("projectName");
				return projectName;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public String getDimensionNameByDimensionId(Long idDimension) {
		try {
			String query = "SELECT dimensionName  from dimension where idDimension=?";
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query, idDimension);
			if (queryForRowSet.next() ) {
				String dimensionName = queryForRowSet.getString("dimensionName");
				return dimensionName;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
